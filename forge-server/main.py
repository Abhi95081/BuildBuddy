# main.py
import os
import shutil
import subprocess
import uuid
import time
from pathlib import Path
from typing import Dict, Optional

from fastapi import FastAPI, BackgroundTasks, HTTPException
from pydantic import BaseModel
from fastapi.responses import FileResponse
from fastapi.staticfiles import StaticFiles
from git import Repo

# CONFIG
BASE_DIR = Path.cwd()
WORK_ROOT = BASE_DIR / "work"
REPOS_ROOT = WORK_ROOT / "repos"
ARTIFACTS_ROOT = WORK_ROOT / "artifacts"
BUILDER_IMAGE = os.environ.get("BUILDER_IMAGE", "android-builder:latest")
BUILD_TIMEOUT = int(os.environ.get("BUILD_TIMEOUT", "900"))  # seconds

# make sure dirs exist
REPOS_ROOT.mkdir(parents=True, exist_ok=True)
ARTIFACTS_ROOT.mkdir(parents=True, exist_ok=True)

app = FastAPI(title="ForgeServer - APK Builder (dev)")

# serve artifacts under /downloads/
app.mount("/downloads", StaticFiles(directory=str(ARTIFACTS_ROOT)), name="downloads")

# In-memory build registry (dev). Replace with DB for production.
class BuildEntry(BaseModel):
    id: str
    repo: str
    branch: Optional[str] = "main"
    status: str = "queued"      # queued, cloning, building, success, failed
    message: Optional[str] = None
    started_at: Optional[float] = None
    finished_at: Optional[float] = None
    artifact_name: Optional[str] = None

BUILDS: Dict[str, BuildEntry] = {}

class BuildRequest(BaseModel):
    repoUrl: str
    branch: Optional[str] = "main"
    token: Optional[str] = None

@app.post("/build")
async def create_build(req: BuildRequest, background_tasks: BackgroundTasks):
    # Basic validation
    if not req.repoUrl.startswith("http"):
        raise HTTPException(status_code=400, detail="repoUrl must be a valid URL")

    build_id = str(uuid.uuid4())
    entry = BuildEntry(id=build_id, repo=req.repoUrl, branch=req.branch)
    BUILDS[build_id] = entry

    background_tasks.add_task(run_build_task, build_id, req.dict())
    return {"buildId": build_id, "status": "queued", "downloadUrl": None}

@app.get("/status/{build_id}")
async def status(build_id: str):
    entry = BUILDS.get(build_id)
    if not entry:
        raise HTTPException(status_code=404, detail="Build not found")
    # Provide a downloadUrl if success
    download_url = None
    if entry.status == "success" and entry.artifact_name:
        download_url = f"/downloads/{entry.artifact_name}"
    return {**entry.dict(), "downloadUrl": download_url}

def run_build_task(build_id: str, req: dict):
    entry = BUILDS[build_id]
    entry.started_at = time.time()
    entry.status = "cloning"
    try:
        workspace = REPOS_ROOT / f"{build_id}"
        if workspace.exists():
            shutil.rmtree(workspace)
        workspace.mkdir(parents=True, exist_ok=True)

        repo_url = req["repoUrl"]
        branch = req.get("branch") or "main"
        token = req.get("token")

        # inject token for private repos (simple approach)
        if token and repo_url.startswith("https://") and "github.com" in repo_url:
            repo_url_with_token = repo_url.replace("https://", f"https://{token}@")
        else:
            repo_url_with_token = repo_url

        # shallow clone
        Repo.clone_from(repo_url_with_token, workspace, branch=branch, depth=1)
        entry.status = "building"

        # prepare artifact path name
        artifact_name = f"{build_id}.apk"
        artifact_host_path = ARTIFACTS_ROOT / artifact_name
        if artifact_host_path.exists():
            artifact_host_path.unlink()

        # Build inside docker
        # We mount the workspace as /workspace and artifacts dir as /out inside container
        docker_cmd = [
            "docker", "run", "--rm",
            "-v", f"{str(workspace)}:/workspace",
            "-v", f"{str(ARTIFACTS_ROOT)}:/out",
            BUILDER_IMAGE,
            "sh", "-c",
            "cd /workspace && chmod +x ./gradlew || true && ./gradlew assembleDebug --no-daemon || exit 1; "
            "apk=$(find app/build/outputs/apk -name \"*debug*.apk\" | head -n1) && cp \"$apk\" /out/" + artifact_name
        ]

        proc = subprocess.Popen(docker_cmd, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True)
        try:
            stdout, _ = proc.communicate(timeout=BUILD_TIMEOUT)
        except subprocess.TimeoutExpired:
            proc.kill()
            entry.status = "failed"
            entry.message = "Build timed out"
            entry.finished_at = time.time()
            return

        if proc.returncode != 0:
            entry.status = "failed"
            entry.message = f"Build failed (rc={proc.returncode}). Output:\n{stdout}"
            entry.finished_at = time.time()
            return

        # verify artifact
        if not artifact_host_path.exists():
            entry.status = "failed"
            entry.message = "Build finished but artifact not found"
            entry.finished_at = time.time()
            return

        entry.status = "success"
        entry.message = "Build succeeded"
        entry.artifact_name = artifact_name
        entry.finished_at = time.time()

    except Exception as e:
        entry.status = "failed"
        entry.message = str(e)
        entry.finished_at = time.time()
    finally:
        # optional: remove workspace to save disk (comment if you need debug)
        try:
            if workspace.exists():
                shutil.rmtree(workspace)
        except Exception:
            pass

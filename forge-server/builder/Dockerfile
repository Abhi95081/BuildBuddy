# builder/Dockerfile
FROM openjdk:17-jdk-slim

ENV ANDROID_SDK_ROOT=/sdk
ENV PATH=$PATH:/sdk/cmdline-tools/latest/bin:/sdk/platform-tools

RUN apt-get update && apt-get install -y --no-install-recommends \
    wget unzip curl git ca-certificates zip unzip procps && \
    rm -rf /var/lib/apt/lists/*

# Create SDK directory
RUN mkdir -p ${ANDROID_SDK_ROOT}/cmdline-tools
WORKDIR /sdk

# Download command line tools (version may change; OK to update link)
RUN wget -q https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip -O /tmp/cmdline.zip \
 && unzip /tmp/cmdline.zip -d ${ANDROID_SDK_ROOT}/cmdline-tools \
 && rm /tmp/cmdline.zip \
 && mv ${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools ${ANDROID_SDK_ROOT}/cmdline-tools/latest

ENV PATH="${PATH}:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin"

# Accept licenses and install essential SDK packages
RUN yes | sdkmanager --sdk_root=${ANDROID_SDK_ROOT} --licenses || true
RUN sdkmanager --sdk_root=${ANDROID_SDK_ROOT} "platform-tools" "platforms;android-33" "build-tools;33.0.2" || true

# working dir
WORKDIR /workspace

# default: just run whatever command you pass to image
ENTRYPOINT ["sh", "-c"]

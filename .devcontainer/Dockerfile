FROM debian:trixie AS base

LABEL org.opencontainers.image.source="https://github.com/privacyguides/verified-apps-android"

# Setup environment
ARG DEBIAN_FRONTEND=noninteractive
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8
ENV USER_UID=1000
ENV USER_GID=1000
ENV USERNAME=vscode

# Things I cache with every Dockerfile
RUN --mount=type=cache,id=apt-cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,id=apt-lib,target=/var/lib/apt,sharing=locked \
    --mount=type=cache,id=debconf,target=/var/cache/debconf,sharing=locked \
    set -exu && \
    apt-get update -qq && \
    apt-get -y install -y -qq --no-install-recommends \
        ca-certificates \
        curl \
        gettext \
        git \
        gnupg \
        lsof \
        nano \
        rsync \
        sudo \
        tar \
        unzip \
        wget \
        zip \
    && rm -rf /var/lib/apt/lists/*

# More base packages for repo
RUN --mount=type=cache,id=apt-cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,id=apt-lib,target=/var/lib/apt,sharing=locked \
    --mount=type=cache,id=debconf,target=/var/cache/debconf,sharing=locked \
    set -exu && \
    apt-get update -qq && \
    apt-get -y install -y -qq --no-install-recommends \
        wget \
        net-tools \
        inetutils-ping \
        dnsutils \
        git \
        supervisor \
    && rm -rf /var/lib/apt/lists/*

# Create vscode user
RUN groupadd --gid "${USER_GID}" "${USERNAME}" \
    && useradd --uid "${USER_UID}" --gid "${USER_GID}" -m "${USERNAME}" \
    && echo "${USERNAME} ALL=(root) NOPASSWD:ALL" > "/etc/sudoers.d/${USERNAME}" \
    && chmod 0440 "/etc/sudoers.d/${USERNAME}"

# Install some Android tools
RUN --mount=type=cache,id=apt-cache,target=/var/cache/apt,sharing=locked \
    --mount=type=cache,id=apt-lib,target=/var/lib/apt,sharing=locked \
    --mount=type=cache,id=debconf,target=/var/cache/debconf,sharing=locked \
    set -exu && \
    apt-get update -qq && \
    apt-get -y install -y -qq --no-install-recommends \
        fdroidserver \
        apksigner \
    && rm -rf /var/lib/apt/lists/*

ARG AGE_VERSION=1.3.1
ARG SOPS_VERSION=3.13.1
ARG TARGETARCH

# Installing age
RUN set -eux; \
    arch="${TARGETARCH:-$(dpkg --print-architecture)}"; \
    case "${arch}" in \
        amd64) age_arch="amd64" ;; \
        arm64) age_arch="arm64" ;; \
        *) echo "Unsupported architecture for age: ${arch}" >&2; exit 1 ;; \
    esac; \
    curl --proto "=https" --tlsv1.2 -fsSL \
        "https://github.com/FiloSottile/age/releases/download/v${AGE_VERSION}/age-v${AGE_VERSION}-linux-${age_arch}.tar.gz" \
        -o /tmp/age.tar.gz; \
    tar -xzf /tmp/age.tar.gz -C /tmp; \
    install -m 0755 /tmp/age/age /usr/local/bin/age; \
    install -m 0755 /tmp/age/age-keygen /usr/local/bin/age-keygen; \
    rm -rf /tmp/age /tmp/age.tar.gz; \
    age --version

# Installing sops
RUN set -eux; \
    arch="${TARGETARCH:-$(dpkg --print-architecture)}"; \
    case "${arch}" in \
        amd64) sops_arch="amd64" ;; \
        arm64) sops_arch="arm64" ;; \
        *) echo "Unsupported architecture for sops: ${arch}" >&2; exit 1 ;; \
    esac; \
    curl --proto "=https" --tlsv1.2 -fsSL \
        "https://github.com/getsops/sops/releases/download/v${SOPS_VERSION}/sops_${SOPS_VERSION}_${sops_arch}.deb" \
        -o /tmp/sops.deb; \
    apt-get update; \
    apt-get install -y --no-install-recommends /tmp/sops.deb; \
    rm /tmp/sops.deb; \
    rm -rf /var/lib/apt/lists/*

# Preserve command history across container restarts
RUN SNIPPET="export PROMPT_COMMAND='history -a' && export HISTFILE=/commandhistory/.bash_history" \
    && echo "$SNIPPET" >> "/home/${USERNAME}/.bashrc"

USER $USERNAME
ENTRYPOINT [ "/bin/bash" ]

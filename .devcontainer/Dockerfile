FROM debian:trixie AS base

LABEL org.opencontainers.image.source="https://github.com/privacyguides/verified-apps-android

# Setup environment
ENV LANG=C.UTF-8
ENV LC_ALL=C.UTF-8

# Install eatmydata for better performance when using apt (disable fsync)
RUN apt-get update \
    && apt-get -yq --no-upgrade install \
        ca-certificates \
        eatmydata

# Run some updates on base image
RUN eatmydata apt-get update \
    && eatmydata apt-get -yq upgrade

# Install some base packages
RUN eatmydata apt-get update \
    && eatmydata apt-get -yq --no-upgrade install \
        curl \
        wget \
        nano \
        net-tools \
        inetutils-ping \
        dnsutils \
        lsof \
        unzip \
        zip \
        git \
        gettext \
        supervisor \
        rsync

# Install some Android tools
RUN eatmydata apt-get update \
    && eatmydata apt-get -yq --no-upgrade install \
        fdroidserver \
        apksigner

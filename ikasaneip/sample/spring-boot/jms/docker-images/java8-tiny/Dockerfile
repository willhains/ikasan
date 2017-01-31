FROM centos:7

MAINTAINER Pixel <DL-team@digitalinside>

# Prerequisites.

RUN yum -y update
RUN yum -y install wget tar unzip epel-release
RUN yum -y install jq


RUN yum update -y && \
    curl --insecure --junk-session-cookies --location --remote-name --silent --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u73-b02/jdk-8u73-linux-x64.tar.gz  && \
    mkdir -p /usr/java/default && \
    tar xfz jdk-8u73-linux-x64.tar.gz -C /usr/java/default && \
    alternatives --install /usr/bin/java java /usr/java/default/jdk1.8.0_73/bin/java 1 && \
    alternatives --install /usr/bin/jar  jar  /usr/java/default/jdk1.8.0_73/bin/jar  1 && \
    rm -rf jdk-8u73-linux-x64.tar.gz \
               /usr/java/default/jdk1.8.0_73/lib/missioncontrol \
               /usr/java/default/jdk1.8.0_73/lib/visualvm \
               /usr/java/default/jdk1.8.0_73/lib/*javafx* \
               /usr/java/default/jdk1.8.0_73/jre/lib/plugin.jar \
               /usr/java/default/jdk1.8.0_73/jre/lib/ext/jfxrt.jar \
               /usr/java/default/jdk1.8.0_73/jre/bin/javaws \
               /usr/java/default/jdk1.8.0_73/jre/lib/javaws.jar \
               /usr/java/default/jdk1.8.0_73/jre/lib/desktop \
               /usr/java/default/jdk1.8.0_73/jre/plugin \
               /usr/java/default/jdk1.8.0_73/jre/lib/deploy* \
               /usr/java/default/jdk1.8.0_73/jre/lib/*javafx* \
               /usr/java/default/jdk1.8.0_73/jre/lib/*jfx* \
               /usr/java/default/jdk1.8.0_73/jre/lib/amd64/libdecora_sse.so \
               /usr/java/default/jdk1.8.0_73/jre/lib/amd64/libprism_*.so \
               /usr/java/default/jdk1.8.0_73/jre/lib/amd64/libfxplugins.so \
               /usr/java/default/jdk1.8.0_73/jre/lib/amd64/libglass.so \
               /usr/java/default/jdk1.8.0_73/jre/lib/amd64/libgstreamer-lite.so \
               /usr/java/default/jdk1.8.0_73/jre/lib/amd64/libjavafx*.so \
               /usr/java/default/jdk1.8.0_73/jre/lib/amd64/libjfx*.so && \
    yum clean all

ENV JAVA_HOME=/usr/java/default \
    LANG=en_US.UTF-8 \
    LC_ALL=en_US.UTF-8

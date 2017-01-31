#!/usr/bin/env bash
 
set -e
JAVA_OPTS="${JAVA_JVM_ARGS} ${appenv}"
JAVA_AGENTS="-javaagent:aspectj.jar"
APP_CONF=""
LOGBACK_CONF=""

function go {
    export JAVA_OPTS LOGBACK_CONF APP_CONF JAVA_AGENTS
    java ${JAVA_AGENTS} ${LOGBACK_CONF} -jar $JAVA_OPTS ${project.exec.artifactId}.jar ${APP_CONF}
}


echo "Starting our brand new spring boot service"
go


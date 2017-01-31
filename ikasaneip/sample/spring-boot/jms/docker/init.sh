#!/usr/bin/env bash
 
set -e
JAVA_OPTS="${JAVA_JVM_ARGS} ${appenv}"


function go {
    export JAVA_OPTS
    java ${JAVA_AGENTS} ${LOGBACK_CONF} -jar $JAVA_OPTS ${project.exec.artifactId}.jar ${APP_CONF}
}


echo "Starting our brand new spring boot service"
go


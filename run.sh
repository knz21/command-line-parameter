#!/bin/bash

JAR_FILE=$(find build/libs -type f -name "command-line-parameter-*.jar" | sort -V | tail -n 1)

if [[ -z "$JAR_FILE" ]]; then
    echo "Error: JAR file not found."
    exit 1
fi

java -jar "$JAR_FILE" "$@"

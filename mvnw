#!/bin/sh
# ----------------------------------------------------------------------------
# Maven Wrapper Script for Unix / macOS / Linux
# ----------------------------------------------------------------------------

which mvn >/dev/null 2>&1
if [ $? -eq 0 ]; then
    exec mvn "$@"
else
    echo "[ERROR] Maven 'mvn' command not found in PATH."
    echo "[INFO] Please ensure Java 17+ and Maven are installed."
    exit 1
fi

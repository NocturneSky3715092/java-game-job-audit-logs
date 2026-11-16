#!/usr/bin/env sh
set -eu
mkdir -p build/classes
javac -d build/classes $(find src/main/java -name '*.java')
java -cp build/classes example.GameJobMain

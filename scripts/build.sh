#!/usr/bin/env bash

script_path=$(cd "$(dirname "$BASH_SOURCE")"; cd -P "$(dirname "$(readlink "$BASH_SOURCE" || echo .)")"; pwd)

cd $script_path/../

sbt clean assembly && docker build -t crime .
#!/usr/bin/env bash

script_path=$(cd "$(dirname "$BASH_SOURCE")"; cd -P "$(dirname "$(readlink "$BASH_SOURCE" || echo .)")"; pwd)

docker run --rm --name my-spark-app --net crimes-proj_crime-net \
    --link spark-master:spark-master \
    --env SPARK_APPLICATION_ARGS="-i /dataset/ -o crimes --es es.nodes=es01" \
    -v $script_path/../dataset:/dataset/ \
    -d crime:latest
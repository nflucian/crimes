#!/usr/bin/env bash

script_path=$(cd "$(dirname "$BASH_SOURCE")"; cd -P "$(dirname "$(readlink "$BASH_SOURCE" || echo .)")"; pwd)

cd $script_path/../

mkdir ./dataset
curl https://policeuk-data.s3.amazonaws.com/download/6a28bb6c4f487d65b6d59b7d663a3c0b50fb8549.zip -o ./dataset/dataset.zip
cd ./dataset/ && unzip dataset.zip && rm dataset.zip
#!/usr/bin/env bash

docker exec -it spark-master /spark/bin/spark-shell \
--packages "org.elasticsearch:elasticsearch-spark-30_2.12:7.15.0" \
--conf spark.es.nodes="es03" \
--conf spark.es.resource="crimes"
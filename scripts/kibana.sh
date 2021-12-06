#!/usr/bin/env bash

curl -X POST http://localhost:5601/api/saved_objects/index-pattern -d '{"attributes":{"fieldAttrs":"{}","title":"crimes*","fields":"[]","typeMeta":"{}","runtimeFieldMap":"{}"}}'
#!/bin/bash
#docker run -it -v ~/Documents/Repositories/Tese/Central_Aggregator/src/pt/uc/student/aclima/central_aggregator:/Central_Aggregator -p 8088:8088 -p 8042:8042 -h sandbox sequenceiq/spark:1.6.0 bash

docker run -it -v ~/Documents/Repositories/Tese/Central_Aggregator:/code/Central_Aggregator -p 4567:4567 registry.giantswarm.io/giantswarm/sparkexample bash
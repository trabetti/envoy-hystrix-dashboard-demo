#!/bin/bash

if [ $# -eq 1 ]
  then
    FRONT_ENVOY_DOCKER_NAME=$1
  else
    FRONT_ENVOY_DOCKER_NAME=envoyfrontproxyrandomservice_front-envoy_1
fi

IP=`docker inspect $FRONT_ENVOY_DOCKER_NAME | grep IPAddress | tail -1 | sed 's/\"//g' | sed 's/,//' | awk '{print $2}'`
echo "Paste the following address into hystrix dashboard. Then click \"Add Stream\", followed by \"Monitor Streams\""
echo "http://$IP:8001/hystrix_event_stream"

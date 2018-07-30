#!/bin/bash

DOCKER_NAME=$1
IP=`docker inspect $DOCKER_NAME | grep IPAddress | tail -1 | sed 's/\"//g' | sed 's/,//' | awk '{print $2}'`
echo $DOCKER_NAME IP: $IP

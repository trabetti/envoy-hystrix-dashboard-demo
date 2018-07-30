#!/bin/bash


SERVICE=$1

if [ $# -eq 2 ]
  then
    DOCKER_NAME=$2
  else
    DOCKER_NAME=envoyfrontproxyrandomservice_front-envoy_1
fi

IP=`docker inspect $DOCKER_NAME | grep IPAddress | tail -1 | sed 's/\"//g' | sed 's/,//' | awk '{print $2}'`

if [ $# -eq 0 ]
  then
    	echo "Usage: run_infinite_curl.sh SERVICE_NAME [front-envoy's docker container name or id (default:envoyfrontproxyrandomservice_front-envoy_1]"
else
   echo "command: curl -v $IP:80/service/$SERVICE"
   echo "Press [CTRL+C] to stop.."
   while :
      do
	curl $IP:80/service/$SERVICE
	#sleep 1
      done

fi

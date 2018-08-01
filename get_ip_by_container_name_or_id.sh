#!/bin/bash

if [ $# -eq 1 ]
   then	
	DOCKER_NAME=$1
	IP=`docker inspect $DOCKER_NAME | grep IPAddress | tail -1 | sed 's/\"//g' | sed 's/,//' | awk '{print $2}'`
	echo $DOCKER_NAME IP: $IP
  else
        for i in $( docker ps -a | grep Up | awk '{print $NF}' ); do
            	DOCKER_NAME=$i
		IP=`docker inspect $DOCKER_NAME | grep IPAddress | tail -1 | sed 's/\"//g' | sed 's/,//' | awk '{print $2}'`
		echo $DOCKER_NAME IP: $IP
        done	
	
fi

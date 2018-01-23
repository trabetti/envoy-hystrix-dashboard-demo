#!/bin/bash

IP=$1
SERVICE=$2

if [ $# -eq 0 ]
  then
    	echo "Usage: run_infinite_curl.sh IP_ADDRESS SERVICE_NAME"
else
   echo "command: curl -v $IP:80/service/$SERVICE"
   echo "Press [CTRL+C] to stop.."
   while :
      do
	#curl -v 172.21.0.3:80/service/2
	#curl -v $IP:80/service/$SERVICE
	curl $IP:80/service/$SERVICE
	#sleep 1
      done

fi

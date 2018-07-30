#!/bin/bash

HYSTRIX_DOCKER_NAME=envoyfrontproxyrandomservice_hystrix_dashboard_1
HYSTRIX_IP=`docker inspect $HYSTRIX_DOCKER_NAME | grep IPAddress | tail -1 | sed 's/\"//g' | sed 's/,//' | awk '{print $2}'`

FRONT_ENVOY_DOCKER_NAME=envoyfrontproxyrandomservice_front-envoy_1
FRONT_ENVOY_IP=`docker inspect $HYSTRIX_DOCKER_NAME | grep IPAddress | tail -1 | sed 's/\"//g' | sed 's/,//' | awk '{print $2}'`

echo "Openning hystrix dashboard.."
echo "firefox $HYSTRIX_IP:8080"

firefox $HYSTRIX_IP:8080


#echo "firefox http://$IP:8080/monitor/monitor.html?streams=%5B%7B%22name%22%3A%22%22%2C%22stream%22%3A%22http%3A%2F%2F$FRONT_ENVOY_IP%3A8001%2Fhystrix_event_stream%22%2C%22auth%22%3A%22%22%2C%22delay%22%3A%22%22%7D%5D"

#firefox http://$IP:8080/monitor/monitor.html?streams=%5B%7B%22name%22%3A%22%22%2C%22stream%22%3A%22http%3A%2F%2F$FRONT_ENVOY_IP%3A8001%2Fhystrix_event_stream%22%2C%22auth%22%3A%22%22%2C%22delay%22%3A%22%22%7D%5D


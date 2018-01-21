#!/bin/bash
java -jar JavaWebServer.jar 8080 ${SERVICE_NAME} &
envoy -c /etc/service-envoy.json --service-cluster service${SERVICE_NAME}

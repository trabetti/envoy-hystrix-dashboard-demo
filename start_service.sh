#!/bin/bash
java -jar JavaWebServer.jar 8080 ${ERROR_PERCENTAGE} ${TIMEOUT_PERCENTAGE} ${SERVICE_NAME} &
envoy -c /etc/service-envoy.json --service-cluster service${SERVICE_NAME}

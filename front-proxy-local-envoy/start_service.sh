#!/bin/bash
java -jar JavaWebServer.jar 8080 ${ERROR_PERCENTAGE} ${TIMEOUT_PERCENTAGE} ${SERVICE_NAME} &
/opt/envoy -c /etc/service-envoy.yaml --service-cluster service${SERVICE_NAME}

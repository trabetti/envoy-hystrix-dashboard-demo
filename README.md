This sandbox can be used for testing error traffic. We used it for testing visualization of traffic with errors through envoy, 
using hystrix dashboard.
It is a system with one front proxy envoy, connected to three services, each are a "random server" which returns one of:
* 200 OK
* 503 SERVICE UNAVAILABLE
* 10s delay (should trigger a timeout since it is set to 2s in the config file)

service 1 is configured with 0% errors
service 2 is configured with 20% errors
service 3 is configured with 50% errors

This sandbox is based on:
[envoy front proxy](https://www.envoyproxy.io/docs/envoy/latest/install/sandboxes/front_proxy)

Read the description in the link to better understand the system.

## Clone the repo 
git clone https://github.ibm.com/TALIS/envoy-front-proxy-random-service.git

## Run docker compose:
`docker-compose up --build -d`

### get the IP:
1. `docker ps -a`

you should see:

```
CONTAINER ID        IMAGE                              COMMAND                  CREATED             STATUS              PORTS                                          NAMES
525be3db51a0        envoyfrontproxyrandomservice_service1      "/bin/sh -c /usr/l..."   24 minutes ago      Up 24 minutes       80/tcp                                         envoyfrontproxyrandomservice_service1_1
50796d896aa5        envoyfrontproxyrandomservice_service2      "/bin/sh -c /usr/l..."   24 minutes ago      Up 24 minutes       80/tcp                                         envoyfrontproxyrandomservice_service2_1
609467be0141        envoyfrontproxyrandomservice_front-envoy   "/bin/sh -c '/opt/..."   24 minutes ago      Up 24 minutes       0.0.0.0:8001->8001/tcp, 0.0.0.0:8000->80/tcp   envoyfrontproxyrandomservice_front-envoy_1
32fb50949ff0        envoyfrontproxyrandomservice_service3      "/bin/sh -c /usr/l..."   24 minutes ago      Up 24 minutes       80/tcp                                         envoyfrontproxyrandomservice_service3_1
```
2. `docker inspect envoyfrontproxyrandomservice_front-envoy_1 | grep IPAddress`

result is something like:

```
            "SecondaryIPAddresses": null,
            "IPAddress": "",
                    "IPAddress": "172.21.0.4",
```

:exclamation: Note that on each run, docker-compose may replace the IP Addresses of the 
different containers, so it is important to inspect the IP Address every time


### call the services:
`curl -v <envoyfrontproxyrandomservice_front-envoy's IP ADDRESS>:80/service/1`

`curl -v <envoyfrontproxyrandomservice_front-envoy's IP ADDRESS>:80/service/2`

`curl -v <envoyfrontproxyrandomservice_front-envoy's IP ADDRESS>:80/service/3`

e.g. `curl -v 172.21.0.4:80/service/1`

you should see something like:

```
$ curl -v 172.21.0.4:80/service/1
*   Trying 172.21.0.4...
* Connected to 172.21.0.4 (172.21.0.4) port 80 (#0)
> GET /service/1 HTTP/1.1
> Host: 172.21.0.4
> User-Agent: curl/7.47.0
> Accept: */*
> 
< HTTP/1.1 503 Service Unavailable
< content-type: text/html
< server-name: myserver
< content-length: 32
< x-envoy-upstream-service-time: 0
< date: Sun, 21 Jan 2018 11:06:48 GMT
< server: envoy
< 
Welcome to Random Web Server #1
* Connection #0 to host 172.21.0.4 left intact
$ curl -v 172.21.0.4:80/service/2
*   Trying 172.21.0.4...
* Connected to 172.21.0.4 (172.21.0.4) port 80 (#0)
> GET /service/2 HTTP/1.1
> Host: 172.21.0.4
> User-Agent: curl/7.47.0
> Accept: */*
> 
< HTTP/1.1 200 OK
< content-type: text/html
< server-name: myserver
< content-length: 32
< x-envoy-upstream-service-time: 1
< date: Sun, 21 Jan 2018 11:07:01 GMT
< server: envoy
< 
Welcome to Random Web Server #2
* Connection #0 to host 172.21.0.4 left intact
```

## run_infinite_curl.sh
A small script that executes infinte curl commands until stopped 
Usage: run_infinite_curl.sh IP_ADDRESS SERVICE_NAME
SERVICE_NUMBER should match the last part of the prefix in front-envoy.json (e.g. 1,2,3,slow)


### stop and remove all running dockers:
`docker stop $(docker ps -a -q)`

`docker rm $(docker ps -a -q)`



## Java Random server
The Java server returns one of the following, based on input argument errorPercentage:
* 200 OK (100%-errorPercentage)
* 503 SERVICE UNAVAILABLE (errorPercentage/2)
* 10s delay (errorPercentage/2)

Usage: `JavaWebServer portNumber errorPercantage ServiceNumber`

errorPercantage and ServiceNumber are optional, default to '0'

Compilation of the java code is done as part of the docker build. If making any changes in the java file, it can be tested locally:

### compile java:
`javac JavaWebServer.java`

### build jar:
`jar cvfe JavaWebServer.jar JavaWebServer *.class`

### run jar:
`java -jar JavaWebServer.jar 1234`

### test it on port 1234
`curl -v localhost:1234`

:exclamation: Note that the service docker uses jdk-7

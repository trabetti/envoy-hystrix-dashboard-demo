This sandbox can be used for testing error traffic. We used it for testing visualization of traffic with errors through envoy, 
using hystrix dashboard.
It is a system with one front proxy envoy, connected to two identical services, each are a "random server" which returns one of:
* 200 OK
* 503 SERVICE UNAVAILABLE
* 504 GATEWAY TIMEOUT

This sandbox is based on:
[envoy front proxy](https://www.envoyproxy.io/docs/envoy/latest/install/sandboxes/front_proxy)

Read the description in the link to better understand the system.


## Run docker compose:
`docker-compose up --build -d`

### get the IP:
1. `docker ps -a`

you should see:

```
CONTAINER ID        IMAGE                              COMMAND                  CREATED             STATUS              PORTS                                          NAMES
1d4eb1d59b29        frontproxylocalenvoy_front-envoy   "/bin/sh -c '/opt/..."   14 minutes ago      Up 14 minutes       0.0.0.0:8001->8001/tcp, 0.0.0.0:8000->80/tcp   frontproxylocalenvoy_front-envoy_1
bf13d9a70814        frontproxylocalenvoy_service1      "/bin/sh -c /usr/l..."   14 minutes ago      Up 14 minutes       80/tcp                                         frontproxylocalenvoy_service1_1
89352a1e457c        frontproxylocalenvoy_service2      "/bin/sh -c /usr/l..."   14 minutes ago      Up 14 minutes       80/tcp                                         frontproxylocalenvoy_service2_1
```
2. `docker inspect frontproxylocalenvoy_front-envoy_1 | grep IPAddress`

result is something like:

```
            "SecondaryIPAddresses": null,
            "IPAddress": "",
                    "IPAddress": "172.21.0.4",
```
### call the services:
`curl -v <frontproxy_front-envoy's IP ADDRESS>:80/service/1`

`curl -v <frontproxy_front-envoy's IP ADDRESS>:80/service/2`

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

### stop and remove all running dockers:
`docker stop $(docker ps -a -q)`

`docker rm $(docker ps -a -q)`

## Modifying java file
If making any changes in the java file, it can be tested locally:

### compile java:
`javac JavaWebServer.java`

### build jar:
`jar cvfe JavaWebServer.jar JavaWebServer *.class`

### run jar:
`java -jar JavaWebServer.jar 1234`

### test it on port 1234
`curl -v localhost:1234`

:exclamation: Note that the service docker uses jdk-7

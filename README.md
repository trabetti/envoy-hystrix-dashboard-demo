This is a demo for using Hystrix dashboard to monitor traffic in a microservices system.

# Demo environment setup
It is a system with one front proxy envoy, connected to four services, each is a "random response server", which returns one of:
* 200 OK
* 503 SERVICE UNAVAILABLE
* 10s delay (should trigger a timeout since timeout is set to 2s in the [config file](https://github.com/trabetti/envoy-hystrix-dashboard-demo/blob/fc2070cf2d193f8c12b3cc769d5d5027e70e0e24/service-envoy.yaml#L29))


This sandbox is based on:
[envoy front proxy](https://www.envoyproxy.io/docs/envoy/latest/start/sandboxes/front_proxy)
with service_1 and service_2 replaced by the random response servers.

### Services configuration
The service configuration can be viewed and changed through the environment variables in [Docker-compose.yml](https://github.com/trabetti/envoy-hystrix-dashboard-demo/blob/master/docker-compose.yml):

Service 1:

      - ERROR_PERCENTAGE=0
      - TIMEOUT_PERCENTAGE=0

Service 2:
 
      - ERROR_PERCENTAGE=15
      - TIMEOUT_PERCENTAGE=5

Service 3:

      - ERROR_PERCENTAGE=35
      - TIMEOUT_PERCENTAGE=15

Service 4:

      - ERROR_PERCENTAGE=0
      - TIMEOUT_PERCENTAGE=50

![Demo settings](demo/demo_settings.png?raw=true "Demo settings")

### Connecting fron-envoy to Hystrix Dashboard
Envoy sends through the Hystrix event stream the statistics of its upstream clusters. Therfore, we will configure the front-envoy and connect the Hystrix dashboard to its admin port.

### Enable Hystrix event stream in Envoy
Enable Hystrix sink in the config file:
```
stats_sinks:
- name: envoy.stat_sinks.hystrix
  config:
    num_of_buckets: 10
```
See [full configuration file](https://github.com/trabetti/envoy-hystrix-dashboard-demo/blob/master/front-envoy.yaml)

## Clone the repo 
`git clone https://github.com/trabetti/envoy-hystrix-dashboard-demo.git`

# Requirements
* [docker-compose](https://docs.docker.com/compose/install/)

# Setup the demo environment
Instructions for setting up the demo system on Ubuntu.

### Run docker compose:
`docker-compose up --build -d`

### Check that all the services are running:
`docker ps -a`

you should see something like:

```
CONTAINER ID        IMAGE                                            COMMAND                  CREATED             STATUS              PORTS                                                     NAMES
ed7b43e1aee1        envoyfrontproxyrandomservice_front-envoy         "/usr/bin/dumb-init …"   3 hours ago         Up 3 hours          0.0.0.0:8001->8001/tcp, 10000/tcp, 0.0.0.0:8000->80/tcp   envoyfrontproxyrandomservice_front-envoy_1
e44d3e267b3b        envoyfrontproxyrandomservice_service1            "/bin/sh -c /usr/loc…"   3 hours ago         Up 3 hours          80/tcp, 10000/tcp                                         envoyfrontproxyrandomservice_service1_1
c026e905c4f9        envoyfrontproxyrandomservice_service3            "/bin/sh -c /usr/loc…"   3 hours ago         Up 3 hours          80/tcp, 10000/tcp                                         envoyfrontproxyrandomservice_service3_1
c3cf561cfbf9        envoyfrontproxyrandomservice_service2            "/bin/sh -c /usr/loc…"   3 hours ago         Up 3 hours          80/tcp, 10000/tcp                                         envoyfrontproxyrandomservice_service2_1
3b7b1e8d07c9        envoyfrontproxyrandomservice_service_slow        "/bin/sh -c /usr/loc…"   3 hours ago         Up 3 hours          80/tcp, 10000/tcp                                         envoyfrontproxyrandomservice_service_slow_1
9ff428a2df14        envoyfrontproxyrandomservice_hystrix_dashboard   "java -cp jetty-runn…"   3 hours ago         Up 3 hours          8080/tcp                                                  envoyfrontproxyrandomservice_hystrix_dashboard_1
```

### Open firefox browser with hystrix dashboard
`source open_hystrix_firefox.sh`

Note: _open_hystrix_firefox.sh_ assumes that the hystrix dashboard docker container name is _envoyfrontproxyrandomservice_hystrix_dashboard_1_.

If your container have a different name, you can invoke it with an argument:

`source open_hystrix_firefox.sh [<hystrix dashboard docker container name or id>]`

### Obtain front envoy docker container IP to use by hystrix dashboard
`source get_stream_for_hystrix_dashboard.sh`

you should see something like:
```
Paste the following address into hystrix dashboard. Then click "Add Stream", followed by "Monitor Streams"
http://172.18.0.7:8001/hystrix_event_stream
```

Note: _get_stream_for_hystrix_dashboard.sh_ assumes that the front envoy docker container name is _envoyfrontproxyrandomservice_front-envoy_1_.

If your container have a different name, you can invoke it with an argument:

`source get_stream_for_hystrix_dashboard.sh [<front envoy docker container name or id>]`

### Start monitoring streams
Paste the URL that was printed on screen in the previous step into hystrix dashboard. Then click "Add Stream", followed by "Monitor Streams".

![Screenshot](demo/hystrix-dashboard-start-page.png?raw=true "Setting up Hystrix dashboard to connect to Envoy URL")

# Generating traffic
You may notice that we don't see anything going on in the dashboard.. That is because we haven't generated traffic yet.

### Get envoyfrontproxyrandomservice_front-envoy's IP ADDRESS
`source get_ip_by_container_name_or_id.sh envoyfrontproxyrandomservice_front-envoy_1`

### Use curl to call the services:
`curl -v <envoyfrontproxyrandomservice_front-envoy's IP ADDRESS>:80/service/1`

`curl -v <envoyfrontproxyrandomservice_front-envoy's IP ADDRESS>:80/service/2`

`curl -v <envoyfrontproxyrandomservice_front-envoy's IP ADDRESS>:80/service/3`

`curl -v <envoyfrontproxyrandomservice_front-envoy's IP ADDRESS>:80/service/slow`

e.g. `curl -v 172.18.0.7:80/service/1`

you should see something like:

```
$ curl -v 172.18.0.7:80/service/1
*   Trying 172.18.0.7...
* Connected to 172.18.0.7 (172.18.0.7) port 80 (#0)
> GET /service/1 HTTP/1.1
> Host: 172.18.0.7
> User-Agent: curl/7.47.0
> Accept: */*
> 
< HTTP/1.1 200 OK
< content-type: text/html
< server-name: myserver
< content-length: 0
< x-envoy-upstream-service-time: 2
< date: Tue, 31 Jul 2018 06:58:44 GMT
< server: envoy
< 
* Connection #0 to host 172.18.0.7 left intact
```

When getting an error response:
```
$ curl -v 172.18.0.7:80/service/3
*   Trying 172.18.0.7...
* Connected to 172.18.0.7 (172.18.0.7) port 80 (#0)
> GET /service/3 HTTP/1.1
> Host: 172.18.0.7
> User-Agent: curl/7.47.0
> Accept: */*
> 
< HTTP/1.1 503 Service Unavailable
< content-type: text/html
< server-name: myserver
< content-length: 0
< x-envoy-upstream-service-time: 4
< date: Tue, 31 Jul 2018 07:00:04 GMT
< server: envoy
< 
* Connection #0 to host 172.18.0.7 left intact
```

### Generate automated traffic
Use the included script to run infinite curl loop per service

`run_infinite_curl.sh SERVICE_NUMBER`

SERVICE_NUMBER should match the last part of the prefix in front-envoy.yaml (e.g. 1,2,3,slow).

This script executes infinte curl commands until stopped. 



### Check Hystrix dashboard
Now we should be able to see some traffic. Recall the services [setup](#services-configuration).
We can see in service 1 now a lot of traffic, and no errors, this is since it only return 200 OK status code.
In the other services we can see errors, and they serve less traffic, since they are occasionaly stopped by delayed responses.

You can confirm that the results shown in the dashboard are similar to the configuration
![screenshot](demo/hystrix-dashboard-activity-screenshot.png?raw=true "Screenshot of activity monitored in hystrix dashboard")

## Useful shortuct

### stop and remove all running dockers:
`docker stop $(docker ps -a -q)`

`docker rm $(docker ps -a -q)`


## Java Random server
The Java server returns one of the following, based on input argument errorPercentage:
* 200 OK (100%-(errorPercentage+timeoutPercnetage))
* 503 SERVICE UNAVAILABLE (errorPercentage)
* 10s delay (timeoutPercnetage)

Usage: `JavaWebServer portNumber errorPercantage timeoutPercantage ServiceNumber`

errorPercantage, timeoutPercantage and ServiceNumber are optional, default to '0'

**There is no need to compile the jave code - compilation of the java code is done as part of the docker build.** 

However, if making any changes in the java file, it can be compiled and tested with the following commands:

#### compile java:
`javac JavaWebServer.java`

#### build jar:
`jar cvfe JavaWebServer.jar JavaWebServer *.class`

#### run jar on port 1234:
`java -jar JavaWebServer.jar 1234`

#### test it on port 1234
`curl -v localhost:1234`

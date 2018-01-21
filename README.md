This sandbox can be used for testing error traffic. We used it for testing visualization of traffic with errors through envoy, 
using hystrix dashboard.
It is a system with one front proxy envoy, connected to two identical services, each are a "random server" which returns one of:
* 200 OK
* 503 SERVICE UNAVAILABLE
* 504 GATEWAY TIMEOUT

This sandbox is based on:
to the [envoy docs](https://www.envoyproxy.io/docs/envoy/latest/install/sandboxes/front_proxy)

Read the description in the link to better understand the system.


##Run docker compose:

`docker-compose up --build -d`

###get the IP:

`docker ps -a`

###find the CONTAINER ID for frontproxy_front-envoy and then:

`docker inspect 567de3a9cefe | grep IPAddress`

###call the services:

`curl -v 172.18.0.2:80/service/1`

`curl -v 172.18.0.2:80/service/2`


###stop and remove all running dockers:

`docker stop $(docker ps -a -q)`

`docker rm $(docker ps -a -q)`

##Modifying java file
If making any changes in the java file, oyu can test it locally:

###compile java:
`javac JavaWebServer.java`

###build jar:
`jar cvfe JavaWebServer.jar JavaWebServer *.class`

###run jar:
`java -jar JavaWebServer.jar 1234`

Note that the service docker installs jdk-7

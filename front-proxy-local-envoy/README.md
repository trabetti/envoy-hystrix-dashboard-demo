This directory is for running the envoy-front-proxy-random-service with local envoy

Basically, follow all instructions in [envoy-front-proxy-random-service](https://github.ibm.com/TALIS/envoy-front-proxy-random-service/blob/master/README.md)

Changes:

### cd to front-proxy-local-envoy
`cd front-proxy-local-envoy`

### Modify docker-compose.yml to mount your local envoy in lines 10,26,42, according to example
`/home/talis/envoy_fork/build-debug/envoy/source/exe/envoy-debug:/etc/envoy`

### Run docker-compose
`docker-compose -f ../docker-compose.yml -f docker-compose.local-envoy.yml  up --build -d`

### Get the docker IP Address - step 2 - replace command line with
`docker inspect frontproxylocalenvoy_front-envoy_1 | grep IPAddress`

:exclamation: Note that on each run, docker-compose may switch between the IP Addresses of the 
different containers, so it is important to inspect the IP Address every time



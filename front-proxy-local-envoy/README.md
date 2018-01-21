This directory is for running the envoy-front-proxy-random-service with local envoy

Basically, follow all instructions in [envoy-front-proxy-random-service]

Changes:

### cd to front-proxy-local-envoy
`cd front-proxy-local-envoy`

### change docker-compose.yml to mount your local envoy in lines 10,26,42, according to example
`/home/talis/envoy_fork/build-debug/envoy/source/exe/envoy-debug:/etc/envoy`

:exclamation: Note that on each run, docker-compose may switch between the IP Addresses of the 
different containers, so it is important to inspect the IP Address every time


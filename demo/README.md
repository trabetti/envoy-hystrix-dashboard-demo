# Envoy-Hystrix-Demo

Demonstrates how to connect to envoy from hystrix dashboard and get an event stream.

Hystrix dashboard runs on the local machine.

Envoy runs on 127.22.0.4, admin port is 8001

Services:

**service 1**: returns no errors or timeouts

**service 2**: returns error to 15% of request and timeout to 5% of the requests

**service 3**: returns error to 35% of request and timeout to 15% of the requests

**service slow**: returns no error, timeout to 50% of the requests

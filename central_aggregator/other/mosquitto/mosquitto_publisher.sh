# test on mosquitto server machine, on a different terminal instance
mosquitto_pub -d -t test/topic -m "Hello from Terminal window 2!"

mosquitto_pub -h 192.168.99.100 -t test/topic -m 'test message' -p 32803

#using ssl certificate
#mosquitto_pub -h <my-domain> -t test/topic -m 'test message' -p 8883 --capath /etc/ssl/certs/
# node.py
# Simple Spark client to show node activity from ttn MQTT brooker
# Author: R.Schimmel
# www.schimmel-bisolutions.nl
# use spark 1.6.1 , the first version which contains MQTT
# You have to download de library from search.maven.org 
# Start with: 
# spark-submit --jars /usr/local/spark/lib/spark-streaming*.jar /yourapplicationdir/node.py

# Import MQTT libraries
from pyspark.streaming.mqtt import MQTTUtils
from pyspark.streaming import StreamingContext
from pyspark import SparkContext


# init
sc= SparkContext(appName="CENTRAL_AGGREGATOR_MQTT")
ssc= StreamingContext(sc,5)
#ssc.checkpoint("/home/rudolf/node") # no clue what this is about

# TTN 
SERVER_PROTOCOL = "tcp"#"ssl"
SERVER_URI = "test.mosquitto.org"
SERVER_PORT = "1883"#"8883"
brokerUrl = SERVER_PROTOCOL + "://" + SERVER_URI + ":" + SERVER_PORT
#
topic="/COLLECTED_DATA"

# Open MQTT streaming
mqttStream = MQTTUtils.createStream(ssc,brokerUrl,topic)

# print output to screen , save to hdfs or another action is also possible
mqttStream.pprint()

# start streaming until termination by user
ssc.start()
ssc.awaitTermination()
# project__1


..Eclipse SmartHome Build
We are using the framework of Eclipse SmartHome to build our own solution on top, this means thta what we build is primarily an artifact repò of OSGi bundles that can be used within smart homes. We are also planon to use a tool called Disigner that can be used for editing config files with full IDE support

1. Prerequisites
The build infrastructure is based on Maven in order to make it as easy as possible. 

2. Settings with Maven
To build Eclipse SmartHome from the sources, Maven takes care of everything:
    set MAVEN_OPTS to "-Xms512m -Xmx1024m"
    change into the smarthome directory ("cd smarthome“)
    run "mvn clean install" to compile and package all sources

3.Basic idea

When a client requests /data/feed, responds with Event at the front of the queue, or holds the request until an event happens

When a client requests /data/sensors, responds with a list of all the sensors as defined in options.prop

When a client requests /data/sensors/[sensorName], responds with the current value of that sensor if it exists, or 404 if it doesn't

This is one approach to automate a home.
Currently this is using the 433Utils for receiving/sending data and the Adafruit DHT library to record temperature.
The system will supports reading:
    temperature/humidity (DHT, HTU21D and DSB)
    pressure (BMP180)
    state of TV (Panasonic)
    communication and control of Z-Wave Window Blinds using FHEM
.
The system is split in a webapp and a client (for collecting the sensor data).

Activities can be scheduled by an integrated scheduler.

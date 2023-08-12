# README

## Application Description
The developed application is an RPC-based system designed for a smart residential property. The system consists of three distinct services aimed at addressing specifics aspects of home automation: the Thermostat System, Security System and Weather Forecast System.

### Service Definitions
The three services of the system are described below.

## 1 Thermostat System (Unary)
Description: This service allows the user to check the current temperature and humidity of their residence from the thermostat reading.

Request: no parameters are needed to call the getThermostat method.

Response: temperature and humidity are replied.

## 2 Security System (Client Streaming and Server Streaming)
Description: This service focuses on enhancing the safety and protection of the building. The user can send a stream of commands to lock/unlock doors and arm the alarm system of the house. The server streams the current status of the system.

Request: no parameters are needed to call the getSecurityStatus method; boolean values are used to lock/unlock doors and arm the alarm system.

Response: doors and alarm (system) status are replied.

## 3 Weather Forecast System (Server Streaming)
Description: This service focuses on providing accurate weather information and alerts to the users, allowing them to plan their activities accordingly.

Request: no parameters are needed to call the getWeather method.

Response: temperature, humidity and rain chance are replied.
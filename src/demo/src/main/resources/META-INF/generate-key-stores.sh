#!/bin/sh
rm -f demo.p12
rm -f demo.crt
rm -f ../../../../../demo-client/src/main/resources/META-INF/demo-client.p12
rm -f ../../../../../demo-client/src/main/resources/META-INF/demo-client.crt

# Create the keystores for the demo and demo-client applications
keytool -genkeypair -keystore demo.p12 -storetype pkcs12 -storepass Password1 -alias demo -keyalg RSA -keysize 2048 -keypass Password1 -validity 7300 -dname "CN=demo" -ext san=dns:demo.local,dns:localhost,ip:127.0.0.1
keytool -genkeypair -keystore ../../../../../demo-client/src/main/resources/META-INF/demo-client.p12 -storetype pkcs12 -storepass Password1 -alias demo-client -keyalg RSA -keysize 2048 -keypass Password1 -validity 7300 -dname "CN=demo-client"

# Extract the certificates from the demo and demo-client keystores
keytool -list -rfc -keystore demo.p12 -storetype pkcs12 -storepass Password1 | awk '/-----BEGIN CERTIFICATE-----/,/-----END CERTIFICATE-----/' > demo.crt
keytool -list -rfc -keystore ../../../../../demo-client/src/main/resources/META-INF/demo-client.p12 -storetype pkcs12 -storepass Password1 | awk '/-----BEGIN CERTIFICATE-----/,/-----END CERTIFICATE-----/' > ../../../../../demo-client/src/main/resources/META-INF/demo-client.crt

# Import the certificates into the demo and demo-client keystores
keytool -importcert -trustcacerts -noprompt -keystore demo.p12 -storetype pkcs12 -storepass Password1 -alias demo-client -file ../../../../../demo-client/src/main/resources/META-INF/demo-client.crt
keytool -importcert -trustcacerts -noprompt -keystore ../../../../../demo-client/src/main/resources/META-INF/demo-client.p12 -storetype pkcs12 -storepass Password1 -alias demo -file demo.crt

# Dump the demo and demo-client keystores
keytool -list -keystore demo.p12 -storetype pkcs12 -storepass Password1
keytool -list -keystore ../../../../../demo-client/src/main/resources/META-INF/demo-client.p12 -storetype pkcs12 -storepass Password1

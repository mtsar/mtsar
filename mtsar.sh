#!/bin/sh

if [ -z "$POSTGRES_PORT_5432_TCP_ADDR" ]; then
    POSTGRES_PORT_5432_TCP_ADDR=192.168.1.2
fi

if [ -z "$POSTGRES_DATABASE" ]; then
    POSTGRES_DATABASE=mtsar
fi

if [ -z "$POSTGRES_USER" ]; then
    POSTGRES_USER=mtsar
fi

if [ -z "$POSTGRES_PASSWORD" ]; then
    POSTGRES_PASSWORD=mtsar
fi

if [ ! -z "$1" ]; then
    export COMMAND=$@
else
    export COMMAND=server
fi

cat >mtsar.docker.yml <<YAML
# Beware! This file has been automatically generated by $0 at `date`.
database:
  driverClass: org.postgresql.Driver
  url: jdbc:postgresql://$POSTGRES_PORT_5432_TCP_ADDR/$POSTGRES_DATABASE
  user: $POSTGRES_USER
  password: $POSTGRES_PASSWORD
  properties:
    charSet: UTF-8
  maxWaitForConnection: 1s
  validationQuery: "/* Mechanical Tsar Health Check */ SELECT 1"
  minSize: 8
  maxSize: 32
  checkConnectionWhileIdle: false
  evictionInterval: 10s
  minIdleTime: 1 minute
logging:
  level: INFO
  appenders:
    - type: file
      currentLogFilename: /var/log/mtsar/mtsar.log
      threshold: ALL
      archive: true
      archivedLogFilenamePattern: /var/log/mtsar/mtsar-%d.log
      archivedFileCount: 5
      timeZone: UTC
YAML

exec java -jar mtsar.jar $COMMAND mtsar.docker.yml 2>>/var/log/mtsar/stderr.log >>/var/log/mtsar/stdout.log

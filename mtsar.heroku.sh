#!/bin/sh

# This script is intended to be runned in the Heroku environment.
# Otherwise, you do not really have to care about it.

if [ -z "$DATABASE_URL" ]; then
    echo 'Are you sure you are running on Heroku?'
    exit 1
fi

POSTGRES_URL=`echo $DATABASE_URL | sed -re 's|^postgres://.*@(.+?)$|\\1|'`
POSTGRES_USER=`echo $DATABASE_URL | sed -re 's|postgres://(.+?):.+@.*|\\1|'`
POSTGRES_PASSWORD=`echo $DATABASE_URL | sed -re 's|postgres://.+:(.+?)@.*|\\1|'`

cat >mtsar.heroku.yml <<YAML
# Beware! This file has been automatically generated by $0 at `date`.
server:
  type: simple
    applicationContextPath: /
    adminContextPath: /admin
  connector:
    type: http
    port: $PORT
database:
  driverClass: org.postgresql.Driver
  url: jdbc:postgresql://$POSTGRES_URL
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
    - type: console
      threshold: ALL
      timeZone: UTC
      target: stdout
YAML

ln -sf target/mtsar-*.jar mtsar.jar

if [ -z "$1" ]; then
    java $JAVA_OPTS -jar mtsar.jar db migrate mtsar.heroku.yml
    exec java $JAVA_OPTS -jar mtsar.jar server mtsar.heroku.yml
else
    exec java $JAVA_OPTS -jar mtsar.jar $@ mtsar.heroku.yml
fi

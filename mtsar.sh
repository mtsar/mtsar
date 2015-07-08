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

exec java \
    -Ddw.database.url="jdbc:postgresql://$POSTGRES_PORT_5432_TCP_ADDR/$POSTGRES_DATABASE" \
    -Ddw.database.user="$POSTGRES_USER" \
    -Ddw.database.password="$POSTGRES_PASSWORD" \
    -jar mtsar.jar $COMMAND mtsar.yml 2>>/var/log/mtsar/stderr.log >>/var/log/mtsar/stdout.log

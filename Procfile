web: java $JAVA_OPTS -jar target/mtsar-*.jar db migrate heroku.yml && java $JAVA_OPTS -Ddw.server.connector.port=$PORT -jar target/mtsar-*.jar server heroku.yml

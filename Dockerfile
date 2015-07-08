FROM maven:3-jdk-8

EXPOSE 8080 8081

RUN mkdir -p /mtsar /var/log/mtsar
WORKDIR /mtsar

ADD pom.xml /mtsar/pom.xml
RUN mvn -T 4 verify clean -B -fn -Dmaven.test.skip=true

ADD LICENSE README.md src /mtsar/
RUN mvn -T 4 -B -Dmaven.test.skip=true verify && ln -sf target/mtsar-*.jar mtsar.jar

CMD ["/mtsar/mtsar.sh"]

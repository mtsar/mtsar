FROM maven:3-jdk-8

EXPOSE 8080 8081

RUN mkdir -p /mtsar /var/log/mtsar
WORKDIR /mtsar

COPY LICENSE pom.xml /mtsar/
RUN mvn -T 4 verify clean -B -fn -Dmaven.test.skip=true

COPY src /mtsar/src
RUN mvn -T 4 -B -Dmaven.test.skip=true verify && mv -fv target/mtsar-*.jar mtsar.jar && rm -rf target

COPY README.md mtsar.sh /mtsar/

CMD ["/mtsar/mtsar.sh"]

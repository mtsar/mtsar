FROM maven:3-jdk-8

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app

ADD pom.xml /usr/src/app/pom.xml
RUN mvn verify clean -B -fn -Dmaven.test.skip=true

ADD . /usr/src/app
RUN mvn -B -Dmaven.test.skip=true verify; ln -sf target/mtsar-*.jar mtsar.jar

EXPOSE 8080 8081

CMD ["java", "-jar", "mtsar.jar", "server", "development.yml"]

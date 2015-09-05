FROM maven:3-jdk-8

MAINTAINER Dmitry Ustalov

EXPOSE 8080 8081

ENV MAVEN_OPTS \
-Dmaven.test.skip=true \
-Dmaven.javadoc.skip=true \
-Dmaven.repo.local=/mtsar/.m2

WORKDIR /mtsar

COPY README.md LICENSE pom.xml /mtsar/

RUN mvn -T 4 -B -fn verify clean

COPY src /mtsar/src

RUN \
mvn -T 4 -B package && \
mv -fv target/mtsar-*.jar mtsar.jar && \
rm -rf target dependency-reduced-pom.xml && \
mkdir -p log && \
touch mtsar.docker.yml && \
chown -R nobody log mtsar.docker.yml

COPY mtsar.docker.sh /mtsar/mtsar.sh

USER nobody

CMD ["/mtsar/mtsar.sh"]

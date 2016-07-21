FROM maven:3-jdk-8

MAINTAINER Dmitry Ustalov <dmitry.ustalov@gmail.com>

EXPOSE 8080 8081

ENV MAVEN_OPTS \
-Dmaven.test.skip=true \
-Dmaven.javadoc.skip=true \
-Dmaven.repo.local=/mtsar/.m2

WORKDIR /mtsar

COPY README.md LICENSE pom.xml src mtsar.docker.sh /mtsar/

# Since the src directory is not copied itself, we need to
# do a couple of nasty things to make the build possible.

RUN \
mkdir -p /mtsar/src && \
mv -fv main /mtsar/src && \
mvn -T 4 -B package && \
mv -fv target/mtsar-*.jar mtsar.jar && \
mvn -B clean && \
rm -rf dependency-reduced-pom.xml /mtsar/.m2 && \
mkdir -p log && \
touch mtsar.docker.yml && \
chown -R nobody log mtsar.docker.yml && \
mv /mtsar/mtsar.docker.sh /mtsar/mtsar.sh

USER nobody

CMD ["/mtsar/mtsar.sh"]

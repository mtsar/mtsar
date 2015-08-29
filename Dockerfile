FROM maven:3-jdk-8

EXPOSE 8080 8081

WORKDIR /mtsar

COPY LICENSE pom.xml /mtsar/

RUN mvn -T 4 -B -fn -Dmaven.test.skip=true -Dmaven.javadoc.skip=true verify clean

COPY src /mtsar/src

RUN \
mvn -T 4 -B -Dmaven.test.skip=true -Dmaven.javadoc.skip=true package && \
mv -fv target/mtsar-*.jar mtsar.jar && \
rm -rf target && \
mkdir -p /mtsar/log && \
chown -R nobody /mtsar

COPY mtsar.docker.sh /mtsar/mtsar.sh

USER nobody

CMD ["/mtsar/mtsar.sh"]

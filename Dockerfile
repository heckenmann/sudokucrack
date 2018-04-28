FROM maven:3.5.3-jdk-8-alpine

EXPOSE 8080

COPY src/ /tmp/src
COPY pom.xml /tmp/pom.xml
RUN mkdir -p /opt/sc
WORKDIR /tmp
RUN mvn clean install \
  && mv /tmp/target/sudokucrack-swarm.jar /opt/sc/sudokucrack-swarm.jar \
  && rm -r /tmp/* /root/.m2

CMD java -jar /opt/sc/sudokucrack-swarm.jar -Djava.net.preferIPv4Stack=true

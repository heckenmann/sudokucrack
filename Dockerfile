FROM maven:3.5.3-jdk-8-alpine

EXPOSE 8080

COPY src/ /tmp/src
COPY pom.xml /tmp/pom.xml

WORKDIR /tmp
RUN mvn clean install
RUN mkdir -p /opt/sc
RUN mv /tmp/target/sudokucrack-swarm.jar /opt/sc/sudokucrack-swarm.jar

CMD java -jar /opt/sc/sudokucrack-swarm.jar -Djava.net.preferIPv4Stack=true
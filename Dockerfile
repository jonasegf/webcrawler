FROM maven:3.9.6-eclipse-temurin-21-jammy
ADD . /usr/src/webcrawler
WORKDIR /usr/src/webcrawler
EXPOSE 4567
ENTRYPOINT ["mvn", "clean", "verify", "exec:java"]
FROM openjdk:10
LABEL maintainer="gucluakkaya@gmail.com"
LABEL description="container for akka-kamon-maven sample"

ARG version
COPY akka-kamon-maven-${version}.jar /home/akka-kamon-maven.jar
CMD ["java","-jar","/home/akka-kamon-maven.jar"]
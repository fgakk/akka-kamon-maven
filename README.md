# akka-kamon-maven
Example Project using kamon with aspectj post compile weaving build with maven

## How to Run

- Build project

mvn clean install

- In the project directory run following command to build the docker image

 docker build -t akka_kamon:latest --build-arg version={artifact version} -f docker/Dockerfile target/

- Go to docker directory and run

docker-compose up

Now you can three running container with rest server, prometheus and grafana. 

By default you can access grafana under 
http://localhost:3000

user: admin
password: foobar

## Grafana Dashboard

Look for Kamon Akka dashboard it should automatically using prometheus as datasource and kamon should already be
collecting metrics from rest server

If you want to see meaningful metrics try to run a load test again rest server. There is an example prepared for this:

https://github.com/fgakk/akka-kamon-maven-gatling 

## Rest API

Rest server is just a restful representation of a key value store 

### Default Path 
http://localhost:8080/notes 

#### Show all notes

GET http://localhost:8080/notes

#### Get one note

GET http://localhost:8080/notes/{id}

#### Add one note

POST http://localhost:8080/notes 

Form Data 
 value={value to be set}
 
content-type: application/x-www-form-urlencoded 

#### Delete one note

DELETE http://localhost:8080/notes/{id}

# reactive-mongodb-demo

Demo app using Spring 6 Webflux API with reactive MongoDB.

Technologies used:
- Spring-Boot 3.1.1
- MongoDB
- spring-boot-data-mongodb-reactive
- spring-boot-webflux / reactor
- Testcontainers


You will need to have a running Docker deamon when starting the application. 

Use the class TestSpringBoot31TestcontainerSupportApplication to start the application with an embeded MongoDB if you don't have one installed locally. 


List of all movies:
```
http://localhost:8080/movies
```

Reactive stream of movie events:
```
https://localhost:8080/movies/[id]/events
```
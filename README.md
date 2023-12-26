## Requirements
For building and running the application you need:
- [JDK 17.0.9]
- [Gradle 8.5]
## Tools used
- [PostgreSQL DB] is one of the robust databases available in the industry which is free and open source
- [Project Lombok] helps to avoid writing the getters and setters etc. that to save time.
- [JSON Web Token] is for the purpose of authentication
- [Bucket4J] is the library to implement rate limiting and request throttling on APIs
## Running the application locally
There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.scalable.apis.demo.ScalableApIsApplication` class from your IDE.

Alternatively you can use the [Spring Boot Gradle plugin](https://docs.spring.io/spring-boot/docs/1.4.3.RELEASE/reference/html/build-tool-plugins-gradle-plugin.html) like so:

```shell
./gradlew bootRun
```
# Sample "quotes" Spring Boot web application

### About the code

This sample Spring (Boot) web application started out with
[Spring Initializr](https://start.spring.io/).

It was then filled in, partly by following Spring (Boot) documentation,
and partly by using own ideas. One such idea was the use of an
in-memory (non-database) repository implementation for unit tests of
the web layer (as opposed to mocking).

Overall the purpose of this small project is to learn,
while not shying away from using own ideas. I also try to
create "complete" classes instead of relying on Spring to
auto-wire (final) fields without there being a corresponding constructor.
I also do not use Lombok annotations for the same reason.
I'd rather see complete Java classes and records than relying on
code generation. Constructors should construct completely
initialised objects, fields should be final for the most part,
and the "state space" of classes should be as small as possible
(also see Effective Java). The use of Spring (Boot) should
not change that. Small concessions are needed, though.
Classes are often not final (for repositories, transactional services),
or else cglib code generation by Spring is not possible.

The project uses a familiar layering (web, transactional services, repositories),
and separates interfaces from implementations for services and
repositories.

### Relevant documentation

First I would like to refer to great explanations of
what Spring Framework and Spring Boot are fundamentally about:
* [Spring Framework by Marco Behler](https://www.marcobehler.com/guides/spring-framework)
* [Spring Boot by Marco Behler](https://www.marcobehler.com/guides/spring-boot-autoconfiguration)

I think these 2 guides really get me on the right track
for really learning and understanding Spring Framework
and Spring Boot. With this foundation, a practical guide
about custom Spring Boot Auto-Configuration can be found
[here](https://www.baeldung.com/spring-boot-custom-auto-configuration).

Note that the idea of Dependency Injection (Spring-based or not)
goes well together with the
[SOLID principles](https://www.freecodecamp.org/news/solid-principles-for-better-software-design/),
in particular (but not only) the Dependency Inversion Principle.

Documentation I consulted also includes the links in
[HELP.md](https://github.com/dvreeze/quotes/blob/master/HELP.md),
as well as [getting started with Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started.html#getting-started)
and [using Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html).

Relevant API documentation:
[Spring Framework API doc](https://docs.spring.io/spring-framework/docs/current/javadoc-api/) and
[Spring Boot API doc](https://docs.spring.io/spring-boot/docs/current/api/).
Thymeleaf API documentation can be found
[here](https://www.thymeleaf.org/apidocs/thymeleaf/3.1.2.RELEASE/).

Relevant reference documentation:
[Spring Framework reference doc](https://docs.spring.io/spring-framework/reference/) and
[Spring Boot reference doc](https://docs.spring.io/spring-boot/docs/current/reference/html/).
Also consult the [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)
and [Best Practices for Spring Boot Application Testing](https://spring.academy/guides/spring-spring-boot-testing).

For the combination of Thymeleaf and Spring, consult
[Thymeleaf and Spring](https://www.thymeleaf.org/doc/tutorials/3.1/thymeleafspring.html).
As a prerequisite, [Using Thymeleaf](https://www.thymeleaf.org/doc/tutorials/3.1/usingthymeleaf.html)
should be read first (at least the basics).

Authoritative documentation on HTTP methods can be found
in [HTTP request methods](https://developer.mozilla.org/en-US/docs/Web/HTTP/Methods).

Interesting but opinionated articles about best practices include
[Stop Using Autowired](https://www.linkedin.com/pulse/you-should-stop-using-spring-autowired-felix-coutinho)
and [Don't use Spring Profile annotation](https://reflectoring.io/dont-use-spring-profile-annotation/).
Arguably config values should be put outside the application's
artifact, however, as described
[here](https://dzone.com/articles/please-stop-using-springs-profiles-per-environment).

The latter is inspired by [12factor config](https://12factor.net/config).
In general, it is good to be aware of the practices mentioned in
[The Twelve-Factor App](https://12factor.net/).

### Running the code

To run the web application, a MySQL database is needed.
I did not install MySQL, but used a MySQL Docker container instead.

To see how, see for example [set up and configure MySQL in Docker](https://www.datacamp.com/tutorial/set-up-and-configure-mysql-in-docker).
I followed the following steps:
```shell
docker pull mysql:8.2
docker volume create quotes-db-data

docker run \
  --name quotes-mysql \
  -e MYSQL_ROOT_PASSWORD=some_password \
  -p 3307:3306 \
  -v quotes-db-data:/var/lib/mysql \
  -d mysql:8.2
```
By all means, use an "environment file" with the environment
variable for the MySQL password instead, using `docker run` option
`--env-file` to point to it (although in a toy application it does
not matter that much).

To create the database tables, we would need to `docker exec` into the MySQL
container:
```shell
docker exec -it quotes-mysql /bin/sh
```

Inside that container session, execute the following command(s):
```shell
mysql -u root -p
# After being logged in to MySQL, create/use a new database "quotes"
create database quotes;
use quotes;
# Now create the tables "quote" and "quote_subject" (copy commands from create_tables.sql)
```

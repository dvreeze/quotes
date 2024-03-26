# Sample "quotes" Spring web application

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

Documentation I consulted includes the links in
[HELP.md](https://github.com/dvreeze/quotes/blob/master/HELP.md),
as well as [getting started with Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/getting-started.html#getting-started)
and [using Spring Boot](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html).

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

To create the database tables, we would need to `run exec` into the MySQL
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

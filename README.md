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

The project uses a familiar layering (web, transactional services, repositories),
and separates interfaces from implementations for services and
repositories.

### Running the code

To run the web application, a MySQL database is needed.
I did not install MySQL, but used a MySQL Docker container instead.

To see how, see for example [set up and configure MySQL in Docker](https://www.datacamp.com/tutorial/set-up-and-configure-mysql-in-docker).
I followed the following steps:
```agsl
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
```agsl
docker exec -it quotes-mysql /bin/sh
```

Inside that container session, execute the following command(s):
```agsl
mysql -u root -p
# After being logged in to MySQL, create/use a new database "quotes"
create database quotes;
use quotes;
# Now create the tables "quote" and "quote_subject" (copy commands from create_tables.sql)
```
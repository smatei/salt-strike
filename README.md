[Project Github Pages](https://smatei.github.io/salt-strike/)

# salt-strike

Manange a salt-stack installation (master + minions) using a spring microservice and salt-stack api.

## How to use it

- edit

src/main/resources/salt-api.properties

and input your own salt.api.url, salt.api.user, salt.api.password

- alternatively you can run a docker setup with a salt master, minion and api [Docker](docker/README.md) and use the default api setup of that docker image (salt.api.user: saltuser, salt.api.password: saltpassword, salt.api.url: http://localhost:8000)

- run

mvn spring-boot:run

- open http://localhost:8080/ in any browser

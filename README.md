[Project Github Pages](https://github.com/smatei/salt-strike)

# salt-strike

Manange a salt-stack installation (master + minions) using a spring microservice and salt-stack api.

## How to use it

- edit

src/main/resources/salt-api.properties

and input your own salt.api.url, salt.api.user, salt.api.password

- run

mvn spring-boot:run

- open http://localhost:8080/ in any browser

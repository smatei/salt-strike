# salt-strike

Manange a salt-stack installation (master + minions) using a spring microservice and salt-stack api.

## How to use it

1. edit

src/main/resources/salt-api.properties

and input your own salt.api.url, salt.api.user, salt.api.password

2. run

mvn spring-boot:run

3. open http://localhost:8080/ in any browser
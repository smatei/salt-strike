# docker saltstack setup

Create a docker image with:

- Ubuntu
- salt-master
- salt-minion
- salt-api

## How to build it

- build

docker build -t salt-ubuntu .

- run

docker run --rm -p 8000:8000 -it salt-ubuntu

- test

curl -sk http://localhost:8000/run -H 'Accept: application/json' -d username='saltuser' -d password='saltpassword' -d eauth='pam' -d client='local' -d tgt='*' -d fun='test.ping'

or

curl -sk http://localhost:8000/login -H 'Accept: application/json' -d username='saltuser' -d password='saltpassword' -d eauth='pam' -d client='local' -d tgt='*'

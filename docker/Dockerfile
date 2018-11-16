FROM ubuntu:18.04
RUN  apt-get update \
##################################################################
# prepare for installing the latest version of saltstack on ubuntu
  && apt-get install -y wget \
  && apt-get install -y gnupg2 \
  && rm -rf /var/lib/apt/lists/* \  
  && wget -O - https://repo.saltstack.com/apt/ubuntu/18.04/amd64/latest/SALTSTACK-GPG-KEY.pub | apt-key add - \
  && echo 'deb http://repo.saltstack.com/apt/ubuntu/18.04/amd64/latest bionic main' > /etc/apt/sources.list.d/saltstack.list \
  && apt-get update \  
###############################################
# actual installation of master, minion and api
  && DEBIAN_FRONTEND=noninteractive apt-get install -yq salt-master \
  && apt-get install -y salt-minion \
  && apt-get install -y salt-api \
##############################################
# install cherrypy python library for salt-api
  && apt-get install -y python3-pip \
  && pip3 install cherrypy \
###################################
# add the saltuser and saltpassword  
  && useradd -d /home/saltuser saltuser \
  && echo "saltuser:saltpassword"|chpasswd \
#########################################################
# setup authentication and api port on master
# setup id and master on minion
  && sed -i 's/#external_auth:/external_auth:/g' /etc/salt/master \
  && sed -i 's/#  pam:/  pam:/g' /etc/salt/master \
  && sed -i 's/#    fred:/    saltuser:/g' /etc/salt/master \
  && sed -i 's/#      - test.*/      - .*/g' /etc/salt/master \
  && sed -i 's/#master: salt/master: localhost/g' /etc/salt/minion \
  && sed -i 's/#id:/id: minion/g' /etc/salt/minion \
  && printf "rest_cherrypy:\n  port: 8000\n  disable_ssl: true" >> /etc/salt/master \  
##########################
# cleanup install packages  
  && rm -rf /var/lib/apt/lists/*
##########################
# at startup start the services: master, minion, api
# accept the minion key and open bash
ENTRYPOINT service salt-master start \
  && service salt-minion start \
  && service salt-api start \
  && sleep 10 \
  && /usr/bin/salt-key -A -y \
  && bash  



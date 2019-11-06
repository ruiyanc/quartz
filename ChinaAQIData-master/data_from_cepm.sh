#!/bin/bash

cd ChinaAQIData-master
rm -rf xml/*.xml
rm -rf /root/testFileinDir/*
wget http://106.37.208.233:20035/emcpublish/ClientBin/Env-CnemcPublish-RiaServices-EnvCnemcPublishDomainService.svc/binary/GetAQIDataPublishLives -O GetAQIDataPublishLives
python3 python-wcfbin/wcf2xml.py GetAQIDataPublishLives > data.xml
cp data.xml xml/$(date +%Y%m%d%H%M)_data.xml

python xml2json.py




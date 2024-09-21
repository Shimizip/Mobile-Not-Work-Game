#!/bin/bash

###
#Adapt Code to work in local environment
###

#Change Database URL to docker internal IP
sed -i.bak 's#jdbc:postgresql://IP:PORT/syp-db#jdbc:postgresql://172.21.0.2:5432/syp-db#g' ../server-application/mnwgdbmodel/src/main/resources/hibernate.cfg.xml && rm ../server-application/mnwgdbmodel/src/main/resources/hibernate.cfg.xml.bak
sed -i.bak 's#jdbc:postgresql://IP:PORT/syp-db#jdbc:postgresql://172.21.0.2:5432/syp-db#g' ../server-application/mnwgdbmodel/src/main/resources/META-INF/persistence.xml && rm ../server-application/mnwgdbmodel/src/main/resources/META-INF/persistence.xml.bak

#Change Website Map Rest API url to public ip 
sed -i.bak 's#apiUrl = `http://localhost:8080/karte/get-kartenbereich-daten/spieler-ansicht#apiUrl = `http://IP:PORT/karte/get-kartenbereich-daten/spieler-ansicht#g' ../website/src/components/map/MapWrapper.tsx && rm ../website/src/components/map/MapWrapper.tsx.bak

#Change Website Login Rest API url to public ip
sed -i.bak 's#BACKEND_BASE_URL = "http://localhost:8080"#BACKEND_BASE_URL = "http://IP:PORT"#g' ../website/src/constants.ts && rm ../website/src/constants.ts.bak

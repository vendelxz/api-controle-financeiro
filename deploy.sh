#!/bin/bash

docker stop $(docker ps -q --filter ancestor=finance-api) 2>/dev/null
docker build -t finance-api .
docker run -d -p 8080:8080 \
  -e TOKEN_SECRET= \
  -e SPRING_DATASOURCE_URL= \
  -e SPRING_DATASOURCE_USERNAME= \
  -e SPRING_DATASOURCE_PASSWORD= \
  -e SPRING_MAIL_USERNAME= \
  -e SPRING_MAIL_PASSWORD= \
  -e FRONTEND_URL= \
  finance-api
#! /bin/bash
mvn clean package
docker-compose up --build

read -p "Enter to stop..."

docker-compose down
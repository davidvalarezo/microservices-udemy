# Microservices Workspace

Listado de todos los comandos docker que hemos ejecutado en esta sección
======================== config-server

.\mvnw clean package
 
docker build -t config-server:v1 .
docker network create springcloud
docker run -p 8888:8888 --name config-server --network springcloud config-server:v1


======================== eureka-server

.\mvnw clean package
 
docker build -t servicio-eureka-server:v1 .
docker run -p 8761:8761 --name eureka-server --network springcloud eureka-server:v1
======================== mysql

docker pull mysql:8.0.40
docker run -p 3307:3306 --name mysql8 --network springcloud -e MYSQL_ROOT_PASSWORD=sasa1234 -e MYSQL_DATABASE=db_springboot_cloud -d mysql:8.0.40
docker logs -f mysql8


======================== msvc-products

.\mvnw clean package -DskipTests
 
docker build -t msvc-products:v1 .
docker run -P --network springcloud msvc-products:v1


======================== gateway-server

.\mvnw clean package -DskipTests
 
docker build -t gateway-server .
docker run -p 8090:8090 --network springcloud --name gateway-server -e IP_ADDR=http://192.168.0.21:9100 -d gateway-server


======================== msvc-users

.\mvnw clean package -DskipTests
 
docker build -t msvc-users:v1 .
docker run -P --network springcloud msvc-users:v1


======================== msvc-oauth

.\mvnw clean package -DskipTests
 
docker build -t msvc-oauth:v1 .
docker run -p 9100:9100 --network springcloud msvc-oauth:v1


======================== msvc-items

.\mvnw clean package -DskipTests
 
docker build -t msvc-items:v1 .
docker run -p 8002:8002 -p 8005:8005 -p 8007:8007 --network springcloud msvc-items:v1


======================== zipkin

docker build -t zipkin-server:v1 .
docker run -p 9411:9411 --name zipkin-server --network springcloud -e STORAGE_TYPE=mysql -e MYSQL_USER=zipkin -e MYSQL_PASS=zipkin -e MYSQL_HOST=mysql8 zipkin-server:v1
docker logs -f zipkin-server


======================== Otros comandos

detener y eliminar todos los contenedores:

docker stop $(docker ps -q)
docker rm $(docker ps -a -q)


eliminar todas las imagenes:

docker rmi $(docker images -a -q)
# build the java 8 centos based image
cd jms/docker-images/java8-tiny/
docker -v build -t ikasan/java8-tiny:latest .

# build the wildfly based image
cd ../wildfly
docker -v build -t ikasan/wildfly .

# build the spring boot image
cd ../..
mvn clean install

# deploy all using docker compose
cd ../deployment/docker-compose
docker-compose up
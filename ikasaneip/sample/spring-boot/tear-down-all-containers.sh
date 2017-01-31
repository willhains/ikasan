# docker stop all containers
docker stop $(docker ps -a -q)

# remove all containers
docker rm $(docker ps -a -q)
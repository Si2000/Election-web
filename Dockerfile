
services:
  backend:
    image: simonxie922/election-backend:latest
    container_name: election-backend
    ports:
      - "8082:8081"
    restart: always
    environment:
      SPRING_PROFILES_ACTIVE: prod


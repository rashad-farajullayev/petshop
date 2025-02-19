# Getting Started

### How to setup database

In the root of the project you can notice docker-compose.yml file.
This file will setup MariaDB for local testing purposes.
Just enter the root directory and run 
```
docker-compose up -d
```
This will start mariadb database server with `petshop` database.
```
Username: thesniffers
Password: thesniffers_123456
```
Then you can build and run application with maven:
```
./mvnw clean package
java -jar application/target/application-0.0.1-SNAPSHOT.jar
```

### Security

Currently there is only basic authentication. 
You will have to enter credentials on the authentication page:

```
    Username: user
    Password: password_123456
```

### Welcome
Welcome page currently prints out some welcome message
and is creates random Customer record and inserts into the database.
Currently yet working on other entity definitions
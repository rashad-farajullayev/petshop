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

#### WARNING
If you have setup previous version of this database in your local
you should delete old MariaDB data before starting this one again. Because schema has changed.
```
    rm -rf ~/.data/thesniffers/mariadb
```


Then you can build and run application with maven:
```
./mvnw clean package
java -jar application/target/application-0.0.1-SNAPSHOT.jar
```

### Security

Currently there is only basic authentication. 
You will have to enter one of these tokens to header as Authentication Bearer.
Based on the included token, current user's role will be defined

```
    Admin token: admin-secret-token-123456
    Tenant 1 token: tenant1-secret-token-abcdef
    Tenant 2 token: tenant2-secret-token-ghijkl
```

### Welcome
Welcome page currently prints out some welcome message
and is creates random Customer record and inserts into the database.
Currently yet working on other entity definitions

### Postman for testing
This project already contains test endpoints collection for postman. File is in the root of the repository. It is called
```
TheSniffers - PetShop.postman_collection.json
```
import this json into your Postman and you can test endpoints.
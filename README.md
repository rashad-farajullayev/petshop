# 🛍️ Pet Shop Service Application

A microservice-based API for managing shopping baskets and items with tenant-based role segregation.

## 🚀 Getting Started
### 📌 Setting Up the Database

This project includes a docker-compose.yml file for setting up a local MariaDB database instance.

#### ▶️ Start the Database

Run the following command from the root directory:

```bash
docker-compose up ts_mariadb -d
```

The docker-compose.yml file contains definitions for both the database and the Pet Shop SpringBoot application. 
If you start this file without specifying service name, it will start both of them and make the application ready for debugging.

```bash
docker-compose up -d
```

This command will launch a MariaDB server with a predefined petshop database and credentials:

| Parameter | Value |
|----------|-------- |
| Host | `ts_mariadb` |
| Database | petshop | 
| Username | thesniffers | 
| Password | thesniffers_123456 |

### ⚠️ Resetting the Database

If you've previously set up an older version of the database, remove old data before launching:

```sh
docker stop ts_mariadb && \
docker rm ts_mariadb && \
rm -rf ~/.data/thesniffers/mariadb && \
docker-compose up -d
```

**Note:** _If you haven't set up the database before, you can skip the deletion step._

## 🔧 Building & Running the Application

### 🏗️ Build the Application

Use Maven to compile the project:

```bash
./mvnw clean package
```

### ▶️ Run the Application

```bash
java -jar application/target/application-0.0.1-SNAPSHOT.jar
```

# 🔒 Security & Authentication

The API uses token-based authentication. Include one of the following tokens in the request headers as a Bearer Token:

| Role | Authentication Token |
|------|----------------------|
|Admin | `admin-secret-token-123456`|
|Tenant 1|`tenant1-secret-token-abcdef`|
|Tenant 2|`tenant2-secret-token-ghijkl`|


# 🏠 Welcome Endpoint

The Welcome Page currently returns a welcome message and inserts a random customer into the database.

```bash
curl -X GET http://localhost:8080/ -w "\nHTTP Status: %{http_code}\n"
```


# 📡 API Testing with Postman

A Postman Collection is provided for easy testing. You can find it in the root directory:

```bash
TheSniffers - PetShop.postman_collection.json
```

📌 Import this file into Postman to quickly test the API endpoints.


# 🐳 Building & Running the Application with Docker

🏗️ Build the Docker Image

`cd` into the project root where the `Dockerfile` resides and run the following command

```bash
docker build --platform linux/amd64 -t pet-shop .
```

# ▶️ Run the Docker Container (Development Mode)

This command runs the application with debugging enabled:

```bash
docker run --name pet-shop --platform linux/amd64 \
  -p 8080:8080 -p 5005:5005 \
  --network petshop_the_sniffers_net \
  -e SPRING_DATASOURCE_HOST=ts_mariadb \
  -e SPRING_DATASOURCE_PORT=3306 \
  -e SPRING_DATASOURCE_DATABASE=petshop \
  -e SPRING_DATASOURCE_USERNAME=thesniffers \
  -e SPRING_DATASOURCE_PASSWORD=thesniffers_123456 \
  -e ENABLE_DEBUG=true \
  pet-shop

```

#### 💡 Debugging Port: 5005

To debug, create a Remote JVM Debugging configuration in IntelliJ IDEA and attach it to the container using:

```bash
-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
```

# 🚀 Running in Production Mode
For production, disable debugging and remove the `5005` port:

```bash
docker run --name pet-shop --platform linux/amd64 \
  -p 8080:8080 \
  --network petshop_the_sniffers_net \
  -e SPRING_DATASOURCE_HOST=ts_mariadb \
  -e SPRING_DATASOURCE_PORT=3306 \
  -e SPRING_DATASOURCE_DATABASE=petshop \
  -e SPRING_DATASOURCE_USERNAME=thesniffers \
  -e SPRING_DATASOURCE_PASSWORD=thesniffers_123456 \
  pet-shop
```

# 📌 Configuration Parameters

| Environment Variable | Description | Default Value |
|----------------------|-------------|---------------|
|`SPRING_DATASOURCE_HOST`|Database Host|`localhost`|
|`SPRING_DATASOURCE_PORT`|Database Port|`3306`
|`SPRING_DATASOURCE_DATABASE`|Database Name|`petshop`|
|`SPRING_DATASOURCE_USERNAME`|Database User|`thesniffers`|
|`SPRING_DATASOURCE_PASSWORD`|Database Password|`thesniffers_123456`|
|`ENABLE_DEBUG`|Enable Debugging (`true`/`false`)|`false`|

# 🎯 Additional Notes
* If using a non-dockerized MariaDB instance, update SPRING_DATASOURCE_HOST to localhost or the relevant IP.

* For security reasons, do not expose debugging ports in production environments.
    
* Consider using secrets management for handling credentials in production.
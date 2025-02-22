FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .

RUN ./mvnw dependency:go-offline
RUN ./mvnw package

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/application/target/*.jar app.jar
EXPOSE 8080

# Store debug configuration in an environment variable
ENV DEBUG_OPTS="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005"
ENV ENABLE_DEBUG="false"

# Start the application, enabling debug mode only if ENABLE_DEBUG=true
CMD ["sh", "-c", "java $( [ \"$ENABLE_DEBUG\" = \"true\" ] && echo \"$DEBUG_OPTS\" ) -jar app.jar"]

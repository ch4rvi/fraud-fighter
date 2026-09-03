FROM eclipse-temurin:17-jre

COPY target/demo-0.0.1-SNAPSHOT.jar fraud-fighter/

ENTRYPOINT ["java", "-jar", "fraud-fighter/demo-0.0.1-SNAPSHOT.jar"]
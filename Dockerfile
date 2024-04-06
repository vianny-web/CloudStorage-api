FROM openjdk:21
ADD /target/cloudstorageapi-0.0.1-SNAPSHOT.jar cloudStorage.jar
ENTRYPOINT ["java", "-jar", "cloudStorage.jar"]
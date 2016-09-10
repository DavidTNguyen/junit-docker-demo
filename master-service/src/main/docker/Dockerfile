FROM frolvlad/alpine-oraclejdk8:slim
ADD master-service-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]

FROM openjdk:23
WORKDIR /walthplexbackend
# Add service account json when starting docker
COPY build/libs/WealthPlex-0.0.1-SNAPSHOT.jar WealthPlex.jar
COPY service-account.json service-account.json
EXPOSE 8080


# Add environment variables when starting docker
ENTRYPOINT ["java", "-jar", "WealthPlex.jar"]
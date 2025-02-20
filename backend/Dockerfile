
#Rebuild our backend :D
FROM gradle:latest AS BUILD
WORKDIR /walthplexbackend
COPY . .
RUN gradle build

#
FROM openjdk:23
WORKDIR /walthplexbackend
# Remember to have environment variables setup, as well as service account (temp)

#copy the newly built jar :D
COPY --from=BUILD /walthplexbackend/build/libs/WealthPlex-0.0.1-SNAPSHOT.jar WealthPlex.jar
COPY service-account.json service-account.json
EXPOSE 8080


# Add environment variables when starting docker
ENTRYPOINT ["java", "-jar", "WealthPlex.jar"]
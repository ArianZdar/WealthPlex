
#Rebuild our backend :D
FROM gradle:latest AS buildstage
WORKDIR /walthplexbackend
COPY . .
RUN gradle clean build

#
FROM openjdk:23
WORKDIR /walthplexbackend
# Remember to have environment variables setup, as well as service account (temp)

#copy the newly built jar :D
COPY --from=buildstage /walthplexbackend/build/libs/WealthPlex-0.0.1-SNAPSHOT.jar WealthPlex.jar
COPY service-account.json service-account.json
COPY .env .env
EXPOSE 8080


# Add environment variables when starting docker
ENTRYPOINT ["java", "-jar", "WealthPlex.jar"]
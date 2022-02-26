FROM openjdk:17-ea-jdk-buster as builder
ENV APP_HOME=/app/
WORKDIR $APP_HOME
COPY build.gradle settings.gradle  gradlew $APP_HOME
COPY gradle $APP_HOME/gradle
RUN ./gradlew build || return 0
RUN apt-get update 
RUN apt-get install -y xvfb libgl1-mesa-dev libgtk-3-0
RUN whoami
COPY . .
RUN Xvfb :99 & export DISPLAY=:99
RUN ./gradlew bootJar
RUN ls build/libs/
RUN mv build/libs/*plain.jar /app/spring-boot-application-plain.jar
RUN mv build/libs/*.jar /app/spring-boot-application.jar
EXPOSE 8080
ENTRYPOINT ["/bin/sh", "-c", "/usr/bin/xvfb-run -a $@", ""] 
CMD ["java","-jar","/app/spring-boot-application.jar"]

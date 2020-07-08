FROM openjdk:11.0.2-jdk-stretch

ADD label-printer.jar /apps/app.jar
RUN sh -c 'touch /apps/app.jar'
ENV JAVA_OPTS="-Dspring.profiles.active=${springProfile}"

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /apps/app.jar" ]
HEALTHCHECK CMD curl -v --silent http://localhost:8000/runtime/actuator/health 2>&1 | grep UP

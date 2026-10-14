FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B -DskipTests package

FROM tomcat:10.1-jre21-temurin
ENV JAVA_OPTS="-XX:MaxRAMPercentage=70.0"
COPY --from=build /app/target/Web-Nhac.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["sh", "-c", "sed -i 's/port=\"8080\"/port=\"'\"${PORT:-8080}\"'\"/' \"$CATALINA_HOME/conf/server.xml\" && catalina.sh run"]

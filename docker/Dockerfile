# ---- Runtime image only  ----
FROM eclipse-temurin:25-jre-alpine

RUN addgroup -S app && adduser -S app -G app
WORKDIR /app

# Copy pre-built JAR
COPY ../API/target/*.jar app.jar

RUN chown -R app:app /app
USER app

# JVM tuning for containers
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080

# Health-friendly startup
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]

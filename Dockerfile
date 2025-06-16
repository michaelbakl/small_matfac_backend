# --- Build stage ---
FROM maven:3.9.6 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# --- Runtime stage ---
FROM eclipse-temurin:23-jdk
WORKDIR /app

# Копируем jar-файл из сборочного контейнера
COPY --from=build /app/target/*.jar app.jar

# Необязательно, но хорошо: добавим переменную среды для порта
ENV PORT=8080

EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]

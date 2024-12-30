FROM tomcat:9.0-jdk17-openjdk-slim


# 작업 디렉토리 설정
WORKDIR /app

# Gradle 캐시 디렉토리 복사
COPY gradle /app/gradle
COPY gradlew /app/gradlew
COPY build.gradle /app/build.gradle
COPY settings.gradle /app/settings.gradle

# Gradle Wrapper 실행 권한 추가
RUN chmod +x ./gradlew

# Gradle 의존성만 먼저 다운로드
RUN ./gradlew dependencies --no-daemon

# 프로젝트 소스 복사
COPY . .

# Gradle 빌드 (테스트 제외)
RUN ./gradlew clean build -x test

# 프로젝트 소스 복사
COPY build/libs/silverplus-0.0.1-SNAPSHOT.jar app.jar


# 애플리케이션 실행 명령
ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# 포트 노출
EXPOSE 8080

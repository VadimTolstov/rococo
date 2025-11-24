FROM eclipse-temurin:21-jdk

WORKDIR /rococo
ENV TZ=Europe/Moscow
ENV JAVA_OPTS="-Xmx3g -Xms1g -XX:MaxMetaspaceSize=1g -XX:+UseG1GC"
ENV GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx3g -Xms1g -XX:MaxMetaspaceSize=1g"
ENV GRADLE_BUILD_OPTS="--no-daemon --max-workers=2"
COPY ./gradle ./gradle
COPY ./rococo-e-2-e-tests ./rococo-e-2-e-tests
COPY ./gradlew ./
COPY ./build.gradle ./
COPY ./settings.gradle ./
COPY ./gradle.properties ./

CMD ./gradlew test -Dtest.env=docker -Drepository=jpa -Duser.timezone=Europe/Moscow

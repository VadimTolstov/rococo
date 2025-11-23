#!/bin/bash
source ./docker.properties
export COMPOSE_PROFILES=test
export PROFILE=docker
export PREFIX="${IMAGE_PREFIX}"
export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export ARCH=$(uname -m)

# Значения по умолчанию
BROWSER="chrome"
SKIP_BUILD=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    chrome|firefox)
      BROWSER="$1"
      ;;
    --skip-build)
      SKIP_BUILD=true
      ;;
  esac
  shift
done

echo "### Selected browser: $BROWSER ###"
export SELENOID_BROWSER=$BROWSER

echo "### Skip build: $SKIP_BUILD ###"

echo '### Java version ###'
java --version

if [ "$SKIP_BUILD" = false ]; then
  # Останавливает и удаляет все контейнеры
  echo "### Stopping and removing old containers ###"
  docker compose down
  docker_containers=$(docker ps -a -q)

  if [ -n "$docker_containers" ]; then
    docker stop $docker_containers
    docker rm $docker_containers
  fi

  # Удаляет старые образы Rococo
  docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'rococo')
  if [ -n "$docker_images" ]; then
    echo "### Removing images: $docker_images ###"
    docker rmi $docker_images
  fi

  echo "### Running build ###"
  bash ./gradlew clean
  bash ./gradlew jibDockerBuild -x :rococo-e-2-e-tests:test

  echo "### Starting all containers ###"
  docker compose up -d
else
  # Только пересоздаёт тестовый контейнер с новым браузером
  echo "### Skipping build and image cleanup ###"

  echo "### Recreating test container with new browser ###"
  docker compose rm -f rococo-e-2-e
  docker compose up -d rococo-e-2-e
fi

# Проверяем переменную окружения BROWSER
if [ "$BROWSER" = "chrome" ]; then
    echo "Загрузка образов Chrome..."
    docker pull selenoid/vnc_chrome:127.0
    docker pull selenoid/vnc_chrome:125.0
elif [ "$BROWSER" = "firefox" ]; then
    echo "Загрузка образов Firefox..."
    docker pull selenoid/vnc_firefox:125.0
else
    echo "Неизвестный браузер: $BROWSER"
    echo "Поддерживаемые значения: chrome, firefox"
    exit 1
fi

echo "Загрузка образов завершена!"
docker ps -a
#!/usr/bin/env bash
# Downloads the Gradle wrapper jar so ./gradlew works.
# Run once after cloning: bash setup-wrapper.sh
set -euo pipefail

WRAPPER_JAR="gradle/wrapper/gradle-wrapper.jar"
GRADLE_VERSION="8.7"
WRAPPER_URL="https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-wrapper.jar.sha256"
JAR_URL="https://raw.githubusercontent.com/gradle/gradle/refs/heads/master/gradle/wrapper/gradle-wrapper.jar"

if [ -f "$WRAPPER_JAR" ]; then
  echo "✅ gradle-wrapper.jar already exists."
  exit 0
fi

mkdir -p gradle/wrapper

echo "📥 Downloading gradle-wrapper.jar for Gradle $GRADLE_VERSION..."
curl -fsSL "https://github.com/gradle/gradle/raw/v${GRADLE_VERSION}.0/gradle/wrapper/gradle-wrapper.jar" \
  -o "$WRAPPER_JAR" 2>/dev/null || \
curl -fsSL "https://raw.githubusercontent.com/gradle/gradle/v${GRADLE_VERSION}.0/gradle/wrapper/gradle-wrapper.jar" \
  -o "$WRAPPER_JAR"

echo "✅ gradle-wrapper.jar downloaded."
echo "🚀 You can now run: ./gradlew tasks"

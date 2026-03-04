#!/usr/bin/env bash
# Lokaler Build-Test auf Linux (erstellt ein App-Image zum Testen)
set -e

JAVAFX_VERSION="21.0.5"
SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

# JDK-Pfad ableiten falls JAVA_HOME nicht gesetzt
if [ -z "$JAVA_HOME" ]; then
    JAVA_HOME="$(dirname $(dirname $(readlink -f $(which java))))"
fi
echo "==> Verwende JDK: $JAVA_HOME"

echo "==> 1. App-JAR bauen..."
./gradlew jar

echo "==> 2. JavaFX Linux jmods herunterladen (falls noch nicht vorhanden)..."
if [ ! -d "jfx/javafx-jmods-${JAVAFX_VERSION}" ]; then
    mkdir -p jfx
    curl -L "https://download2.gluonhq.com/openjfx/${JAVAFX_VERSION}/openjfx-${JAVAFX_VERSION}_linux-x64_bin-jmods.zip" \
         -o jfx/javafx-jmods.zip
    unzip -q jfx/javafx-jmods.zip -d jfx/
    rm jfx/javafx-jmods.zip
fi

echo "==> 3. Runtime-Image mit jlink erstellen..."
rm -rf runtime-image
jlink \
  --module-path "jfx/javafx-jmods-${JAVAFX_VERSION}:${JAVA_HOME}/jmods" \
  --add-modules javafx.controls,javafx.fxml,javafx.media,javafx.web,java.base,java.desktop,java.logging,java.xml,java.naming,java.sql,java.management,jdk.unsupported \
  --output runtime-image \
  --strip-debug \
  --no-header-files \
  --no-man-pages

echo "==> 4. App-Image mit jpackage erstellen..."
rm -rf build/jpackage
jpackage \
  --type app-image \
  --name AquaBuddy \
  --app-version 1.0.0 \
  --input build/libs \
  --main-jar WasserTrinkApp.jar \
  --main-class com.waterreminder.Main \
  --runtime-image runtime-image \
  --java-options "--add-modules javafx.controls,javafx.fxml,javafx.media,javafx.web" \
  --java-options "--enable-native-access=javafx.graphics,javafx.web" \
  --dest build/jpackage

echo ""
echo "==> Fertig! Starte die App zum Testen:"
echo "    ./build/jpackage/AquaBuddy/bin/AquaBuddy"
echo ""
./build/jpackage/AquaBuddy/bin/AquaBuddy

#!/bin/bash
# ─────────────────────────────────────────────────────────────────────────────
# Genera el instalador .dmg para macOS
# Requisitos: JDK 21+, Maven (NetBeans)
# ─────────────────────────────────────────────────────────────────────────────
set -e

MVN="/Applications/Apache NetBeans.app/Contents/Resources/netbeans/java/maven/bin/mvn"
JAVA_HOME_DIR="$HOME/.sdkman/candidates/java/current"
JPACKAGE="$JAVA_HOME_DIR/bin/jpackage"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
TARGET="$PROJECT_DIR/target"
DIST="$PROJECT_DIR/dist"

echo "==> Compilando y creando fat JAR..."
"$MVN" -f "$PROJECT_DIR/pom.xml" clean package -q

echo "==> Creando instalador macOS (.dmg)..."
mkdir -p "$DIST"

"$JPACKAGE" \
  --input "$TARGET" \
  --main-jar SistemaConsultas-fat.jar \
  --main-class triagesystem.MainApp \
  --name "Sistema de Consultas" \
  --app-version "1.0" \
  --vendor "UMG" \
  --description "Sistema de Consultas - Universidad Mariano Gálvez de Guatemala" \
  --type dmg \
  --dest "$DIST" \
  --java-options "-Dfile.encoding=UTF-8" \
  --java-options "--add-opens=java.base/java.lang=ALL-UNNAMED" \
  --java-options "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED"

echo ""
echo "✅ Instalador creado en: $DIST"
ls -lh "$DIST"/*.dmg

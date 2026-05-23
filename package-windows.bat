@echo off
REM ─────────────────────────────────────────────────────────────────────────────
REM Genera el instalador .msi para Windows
REM Requisitos: JDK 21+, Maven, WiX Toolset 3.x (https://wixtoolset.org/)
REM             Ejecutar en una máquina Windows
REM ─────────────────────────────────────────────────────────────────────────────

SET PROJECT_DIR=%~dp0
SET DIST=%PROJECT_DIR%dist

echo ==> Compilando y creando fat JAR...
mvn -f "%PROJECT_DIR%pom.xml" clean package -q
IF ERRORLEVEL 1 (
    echo ERROR: fallo Maven
    exit /b 1
)

echo ==> Creando instalador Windows (.msi)...
IF NOT EXIST "%DIST%" mkdir "%DIST%"

jpackage ^
  --input "%PROJECT_DIR%target" ^
  --main-jar SistemaConsultas-fat.jar ^
  --main-class triagesystem.MainApp ^
  --name "Sistema de Consultas" ^
  --app-version "1.0" ^
  --vendor "UMG" ^
  --description "Sistema de Consultas - Universidad Mariano Galvez de Guatemala" ^
  --type msi ^
  --dest "%DIST%" ^
  --win-menu ^
  --win-shortcut ^
  --java-options "-Dfile.encoding=UTF-8" ^
  --java-options "--add-opens=java.base/java.lang=ALL-UNNAMED" ^
  --java-options "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED"

IF ERRORLEVEL 1 (
    echo ERROR: fallo jpackage. Verifica que WiX Toolset este instalado.
    exit /b 1
)

echo.
echo Instalador creado en: %DIST%
dir "%DIST%\*.msi"

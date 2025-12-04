@echo off
set "JAVA_HOME=%~dp0backend\java-runtime\jdk-17.0.17+10"
set "PATH=%JAVA_HOME%\bin;%PATH%"

cd backend
call .\gradlew.bat bootRun
pause

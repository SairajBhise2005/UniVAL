@echo off
REM Set Maven home and add Maven to PATH for this session
set "MAVEN_HOME=C:\apache-maven-3.9.6"
set "PATH=%MAVEN_HOME%\bin;%PATH%"

REM Display Maven version to confirm setup
mvn -version

REM Example Maven commands (uncomment to use)
REM mvn clean compile
REM mvn javafx:run

pause

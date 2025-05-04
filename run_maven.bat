@echo off
REM ====== CONFIGURE JAVA_HOME BELOW ======
set "JAVA_HOME=C:\Program Files\Java\jdk-23.0.2"
set "MAVEN_HOME=C:\apache-maven-3.9.6"
set "PATH=%JAVA_HOME%\bin;%MAVEN_HOME%\bin;%PATH%"

REM ====== DEBUG: PRINT JAVA_HOME ======
echo JAVA_HOME is set to: %JAVA_HOME%
if not exist "%JAVA_HOME%" (
    echo ERROR: JAVA_HOME directory does not exist!
    pause
    exit /b 1
)

REM ====== DISPLAY VERSIONS ======
call mvn -version
call java -version

REM ====== COMPILE ======
call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo Maven compile failed!
    pause
    exit /b %ERRORLEVEL%
)

REM ====== RUN ======
call mvn javafx:run
if %ERRORLEVEL% NEQ 0 (
    echo Application failed to start!
    pause
    exit /b %ERRORLEVEL%
)

pause

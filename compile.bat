@echo off
echo Compiling JavaFX application...

set JAVA_HOME=C:\Program Files\Java\jdk-17
set JAVAFX_HOME=javafx-sdk\javafx-sdk-17.0.2\lib
set PATH=%JAVA_HOME%\bin;%PATH%

javac --module-path "%JAVAFX_HOME%" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "lib/*;target/classes" -d target/classes src/main/java/com/unival/facultyscheduling/*.java src/main/java/com/unival/facultyscheduling/view/*.java src/main/java/com/unival/facultyscheduling/service/*.java src/main/java/com/unival/facultyscheduling/util/*.java

if %ERRORLEVEL% EQU 0 (
    echo Compilation successful!
) else (
    echo Compilation failed!
    pause
)
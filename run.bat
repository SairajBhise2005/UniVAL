@echo off
echo Running JavaFX application...

set JAVA_HOME=C:\Program Files\Java\jdk-17
set JAVAFX_HOME=javafx-sdk\javafx-sdk-17.0.2\lib
set PATH=%JAVA_HOME%\bin;%PATH%

java --module-path "%JAVAFX_HOME%" --add-modules javafx.controls,javafx.fxml,javafx.graphics,javafx.base -cp "lib/*;target/classes" com.unival.facultyscheduling.Main

pause 
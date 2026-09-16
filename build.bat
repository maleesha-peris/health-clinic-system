@echo off
echo Building Community Health Clinic System...
if not exist bin mkdir bin
javac -encoding UTF-8 -cp "lib/*;bin" -d bin src/com/healthclinic/model/*.java src/com/healthclinic/util/*.java src/com/healthclinic/data/*.java src/com/healthclinic/controller/*.java src/com/healthclinic/view/*.java src/com/healthclinic/*.java src/com/healthclinic/test/*.java
if %ERRORLEVEL% equ 0 (
    echo Build successful!
) else (
    echo Build failed with error code %ERRORLEVEL%
)

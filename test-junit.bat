@echo off
echo Running JUnit 5 Platform Test Execution...
java -jar lib\junit-platform-console-standalone-1.10.2.jar execute --class-path bin --select-package com.healthclinic.test
pause

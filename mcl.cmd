@echo off
chcp 65001
setlocal
set JAVA_BINARY=java
%JAVA_BINARY% -jar mcl.jar %*

set EL=%ERRORLEVEL%
if %EL% NEQ 0 (
    echo Process exited with %EL%
    pause
)

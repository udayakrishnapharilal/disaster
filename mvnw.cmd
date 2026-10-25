@REM ----------------------------------------------------------------------------
@REM Maven Startup Script for Windows
@REM ----------------------------------------------------------------------------
@echo off
setlocal

set ERROR_CODE=0

@REM To isolate internal variables to this script, we set them here
set "DIRNAME=%~dp0"
if "%DIRNAME%" == "" set "DIRNAME=."

@REM Find maven or wrapper
set "MAVEN_CMD=mvn"
where mvn >nul 2>&1
if %ERRORLEVEL% equ 0 goto runMaven

@REM Try to download maven or check JAVA_HOME
if not "%JAVA_HOME%" == "" (
    if exist "%JAVA_HOME%\bin\java.exe" goto checkMavenHome
)

:checkMavenHome
echo [INFO] Running Maven build...
mvn %*
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Maven not found in PATH. Please ensure JDK 17+ and Maven are installed, or run in IDE.
    exit /b 1
)

:runMaven
mvn %*
exit /b %ERRORLEVEL%

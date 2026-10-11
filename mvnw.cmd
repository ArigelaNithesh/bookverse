@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script
@REM ----------------------------------------------------------------------------

@if "%DEBUG%" == "" @echo off
@classpaths "%~dp0"

@setlocal

set ERROR_CODE=0

@REM To isolate internal variables from possible post scripts, me must use setlocal
@setlocal

@REM Enable location of my-project, to dynamically locate configured env variables
@set MAVEN_PROJECTBASEDIR=%~dp0
@if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

@REM Find JAVA_HOME from environment or VS Code bundled Java 21
if "%JAVA_HOME%" == "" (
    if exist "C:\Users\Nithesh_Arigela\.vscode\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64\bin\java.exe" (
        set "JAVA_HOME=C:\Users\Nithesh_Arigela\.vscode\extensions\redhat.java-1.56.0-win32-x64\jre\21.0.12.1-win32-x86_64"
    )
)

if "%JAVA_HOME%" == "" goto errorNoJavaHome
if not exist "%JAVA_HOME%\bin\java.exe" goto errorNoJavaHome

set "JAVACMD=%JAVA_HOME%\bin\java.exe"

set WRAPPER_JAR="%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar"
set WRAPPER_LAUNCHER=org.apache.maven.wrapper.MavenWrapperMain

%JAVACMD% -classpath %WRAPPER_JAR% -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" %WRAPPER_LAUNCHER% %*
if ERRORLEVEL 1 goto error

goto end

:errorNoJavaHome
echo ERROR: JAVA_HOME is not set and no compatible JDK was found. >&2
exit /b 1

:error
set ERROR_CODE=1

:end
@endlocal & set ERROR_CODE=%ERROR_CODE%
cmd /C exit /B %ERROR_CODE%

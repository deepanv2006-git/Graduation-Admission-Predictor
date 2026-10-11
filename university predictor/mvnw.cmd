@echo off
rem Maven wrapper script for Windows
if "%JAVA_HOME%"=="" (
    if exist "D:\gta\PyCharm 2026.1.2\jbr\bin\java.exe" (
        set "JAVA_HOME=D:\gta\PyCharm 2026.1.2\jbr"
    )
)
if exist "C:\Users\Deepan V\maven\apache-maven-3.9.6\bin\mvn.cmd" (
    call "C:\Users\Deepan V\maven\apache-maven-3.9.6\bin\mvn.cmd" %*
) else (
    call mvn %*
)

@echo off
if not "%JAVA_HOME%" == "" goto javaHomeAlreadySet
	echo Please set JAVA_HOME environment variable. Will try to find java.exe on normal PATH
	for %%P in (%PATH%) do if exist %%P\java.exe set JAVA_HOME=%%P..\
:javaHomeAlreadySet

REM additional classpath entries. place your libs for plugins (also third party libs) here. multiple entries seperated by ';' (normal java syntax)
set CLASSPATH=lib/*;bin/*;

echo [yadarts] Java path  : %JAVA_HOME%
echo [yadarts] Classpath  : %CLASSPATH%

:startApp
"%JAVA_HOME%/bin/java" -Dfile.encoding=UTF-8 -classpath %CLASSPATH% spare.n52.yadarts.YadartsDesktopMain
set restartcode=%errorlevel%

if %restartcode%==100 goto startApp

pause

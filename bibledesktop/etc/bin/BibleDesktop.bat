
REM STEP 1 - Initial setup
REM @echo off
if "%OS%"=="Windows_NT" @setlocal

REM Win98 does not define ProgramFiles, XP, 2000, do...
if "%ProgramFiles%"=="" set ProgramFiles="C:\Program~1"

REM STEP 2 - Check we know where we are installed
set DEFAULT_JSWORD=%~dp0
if "%JSWORD%"=="" set JSWORD=%DEFAULT_JSWORD%
set DEFAULT_JSWORD=
if exist "%JSWORD%" goto DoneFindJSword
REM have a blind guess ...
if not exist "%ProgramFiles%\CrossW~1\BibleD~1" goto FailedFindJSword
set JSWORD=%ProgramFiles%\CrossW~1\BibleD~1
:DoneFindJSword
echo "Using JSWORD=%JSWORD%"

cd %JSWORD%

REM STEP 3 - Setup the classpath
set LOCALCLASSPATH=%CLASSPATH%
for %%i in ("%JSWORD%\*.jar") do call "%JSWORD%\lcp.bat" %%i

REM STEP 4 - Run JSword
REM -Xmx256M
REM "-Djava.endorsed.dirs=%JSWORD%\lib"
REM -classpath "%JSWORD%\resource"
"%JAVA_HOME%\bin\java.exe" -classpath "%LOCALCLASSPATH%" -Dsword.home="%SWORD_HOME%" org.crosswire.bibledesktop.desktop.Desktop
goto End

:FailedFindJSword
echo "Can't find install directory. Please use C:\Program Files\CrossWire\BibleDesktop or set the JSWORD variable"

:End
set LOCALCLASSPATH=
set _JAVACMD=
if "%OS%"=="Windows_NT" @endlocal

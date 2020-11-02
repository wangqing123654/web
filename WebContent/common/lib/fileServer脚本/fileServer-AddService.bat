@echo off

@echo .
@echo . FileServer-AddService.bat - �� Windows Service �м��� FileServer ����
@echo .

setlocal

if "%JAVA_HOME%" == "" set JAVA_HOME=C:\Program Files (x86)\Java\jdk1.6.0_30\jre
set JVMDIR=%JAVA_HOME%\jre\bin\server
set JSBINDIR=%CD%
set JSEXE="%JSBINDIR%\JavaService.exe"
set SSBINDIR=%JSBINDIR%

@echo . JAVA_HOME=%JAVA_HOME%
@echo . JVMDIR=%JVMDIR%
@echo . JSBINDIR=%JSBINDIR%
@echo . JSEXE=%JSEXE%
@echo . SSBINDIR=%SSBINDIR%

@echo . Using following version of JavaService executable:
@echo .
%JSEXE% -version
@echo .

@echo Installing FileServer service... 
@echo .
 %JSEXE% -install FileServer "%JVMDIR%\jvm.dll" -Djava.class.path="%JAVA_HOME%/lib/tools.jar;C:\Tomcat 6.0\webapps\web\common\lib\T40-api.jar" -Xms100M -Xmx100M -start com.dongyang.server.FileServer -params start memory -stop com.dongyang.server.FileServer -method serviceStop -out "%JSBINDIR%\FileServerout.log" -err "%JSBINDIR%\FileServererr.log" -current "%JSBINDIR%" -manual -description "�����ļ�������"
@echo .

@pause

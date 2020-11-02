@echo off

@echo .
@echo . RootServer-AddService.bat - 在 Windows Service 中加载 RootServer 服务
@echo .

setlocal

if "%JAVA_HOME%" == "" set JAVA_HOME=C:\Program Files (x86)\Java\jdk1.6.0_30
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

@echo Installing RootServer service... 
@echo .
 %JSEXE% -install RootServer "%JVMDIR%\jvm.dll" -Djava.class.path="%JAVA_HOME%/lib/tools.jar;C:\Tomcat 6.0\webapps\web\common\lib\T40-api.jar;C:\Tomcat 6.0\webapps\web\common\lib\classes12.jar;C:\Tomcat 6.0\webapps\web\common\lib\servlet-api.jar;%HISDIR%" -Xms100M -Xmx100M -start com.dongyang.server.RootServer -params start memory -stop com.dongyang.server.RootServer -method serviceStop -out "%JSBINDIR%\RootServerout.log" -err "%JSBINDIR%\RootServererr.log" -current "%JSBINDIR%" -manual -description "蓝创通讯服务器"
@echo .

@pause

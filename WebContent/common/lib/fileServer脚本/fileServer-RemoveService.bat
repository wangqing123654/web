@echo off

@echo .
@echo . FileServer-AddService.bat - 在 Windows Service 中卸载 FileServer 服务
@echo .

setlocal
if "%JAVA_HOME%" == "" set JAVA_HOME=C:\Program Files (x86)\Java\jdk1.6.0_30\jre
set JSBINDIR=%CD%
set JSEXE="%JSBINDIR%\JavaService.exe"

@echo . JSBINDIR=%JSBINDIR%
@echo . JSEXE=%JSEXE%

@echo . Using following version of JavaService executable:
@echo .
%JSEXE% -version
@echo .

@echo Un-installing FileServer service...
@echo .
%JSEXE% -uninstall FileServer
@echo .

@pause
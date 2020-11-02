@echo off

@echo .
@echo . RootServer-AddService.bat - �� Windows Service ��ж�� RootServer ����
@echo .

setlocal
if "%JAVA_HOME%" == "" set JAVA_HOME=C:\Program Files (x86)\Java\jdk1.6.0_30
set JSBINDIR=%CD%
set JSEXE="%JSBINDIR%\JavaService.exe"

@echo . JSBINDIR=%JSBINDIR%
@echo . JSEXE=%JSEXE%

@echo . Using following version of JavaService executable:
@echo .
%JSEXE% -version
@echo .

@echo Un-installing RootServer service...
@echo .
%JSEXE% -uninstall RootServer
@echo .

@pause
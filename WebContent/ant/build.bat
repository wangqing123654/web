@echo off
set JAVA_HOME=E:\Programs\Java\jdk1.5.0_10
set PATH=%JAVA_HOME%\bin;%PATH%
set ANT_HOME=E:\apache-ant-1.7.0

set ANT_CLASSPATH=%JAVA_HOME%/lib/tools.jar
set ANT_CLASSPATH=%ANT_HOME%/lib/ant.jar;%ANT_HOME%/lib/ant-launcher.jar;%ANT_CLASSPATH%
set ANT_CLASSPATH=%ANT_CLASSPATH%;

%JAVA_HOME%\bin\java -classpath "%ANT_CLASSPATH%" -Dant.home=%ANT_HOME% -Dj2ee.home=%J2EE_HOME% org.apache.tools.ant.Main %1 %2 %3 %4




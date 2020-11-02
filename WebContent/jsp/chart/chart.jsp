<%@ page language="java" import="java.util.*" pageEncoding="UTF-8" isELIgnored="false"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
String url = request.getContextPath() + "/DisplayChart?filename=";
%>

<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>${title}</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="-10">
  </head>
  
  <body>
  	<div align="center">
		<img src="<%=url%>${fileName}" width="800" height="550">
	</div>
  </body>
</html>

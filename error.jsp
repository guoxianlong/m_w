<%@ include file="taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<html>
<title>错误</title>
<script>
<%	// 判断request，输出信息
String promptMsg = (String)session.getAttribute("promptMsg");
if(promptMsg != null){
session.removeAttribute("promptMsg");
%>
alert("<%=promptMsg%>");
<%}%> 
history.back();
</script>
<body>
</body>
</html>
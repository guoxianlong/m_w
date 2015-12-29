<%@ page contentType="text/html;charset=utf-8" %>
<%	// 判断request，输出信息
String promptMsg = (String)session.getAttribute("promptMsg");
if(promptMsg != null){
session.removeAttribute("promptMsg");
%>
<script>
alert("<%=promptMsg%>");
window.location.reload();
</script>
<%return;}%>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/pub.js"></script>
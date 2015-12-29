<%@ page contentType="text/html;charset=utf-8" %>
<%
String result = (String) request.getAttribute("result");
Integer opid = (Integer)request.getAttribute("opid");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
history.back(-1);
</script>
<%
	return;
}
%>
<script>
alert("添加成功！");
document.location = "<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=opid%>";
</script>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.bybs.*, adultadmin.bean.buy.*" %>
<%
ByBsAction action = new ByBsAction();
action.addByBsProduct(request,response);
String opid = request.getParameter("opid");
String result = (String) request.getAttribute("result");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
document.location = "<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=opid%>";
</script>
<%
	return;
}
else{
%>
<script>
alert("修改成功！");
document.location = "<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=opid%>";
</script>
<%
	return;
}
%>
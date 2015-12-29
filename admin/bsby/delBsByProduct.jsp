<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.bybs.*, adultadmin.bean.buy.*" %>
<%
ByBsAction action = new ByBsAction();
action.delByBsProduct(request,response);
String opid = request.getParameter("opid");
String result = (String) request.getAttribute("result");
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
else{
%>
<script>
alert("删除成功！");
document.location = "<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=opid%>";
</script>
<%
	return;
}
%>
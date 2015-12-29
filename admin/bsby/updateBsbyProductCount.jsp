<%@ page contentType="text/html;charset=utf-8" %><%@page import="adultadmin.action.bybs.ByBsAction"%>
<%
ByBsAction action = new ByBsAction();
action.updateBsbyProductCount(request,response);

String result = (String) request.getAttribute("result");
String opid = (String)request.getAttribute("opid");
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
alert("修改成功！");
document.location = "<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=opid%>";
</script>
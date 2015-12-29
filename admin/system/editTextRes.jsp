<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.system.*" %>
<%
TextResAction action = new TextResAction();
action.editTextRes(request, response);

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
	String back = (String) request.getAttribute("back");
%>
<script>
alert("修改成功！");
document.location = "<%=back%>";
</script>
<%
	return;
}
%>
<%@ page contentType="text/html;charset=utf-8"%><%@ page import="adultadmin.action.bybs.*"%>
<%
	ByBsAction action = new ByBsAction();
	//action.editRemark(request, response);
	action.updateCurrentType(request, response);
	String result = (String) request.getAttribute("result");
	String opid = (String) request.getParameter("opid");
	String look = (String) request.getAttribute("look");
	if ("failure".equals(result)) {
		String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
document.location.href = "<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=opid%>";
</script>
<%
	return;
	}
%>
<script>
alert("操作成功");
</script>
<% if(look!=null)
{
%>
<script type="text/javascript">
document.location.href = "<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&lookup=1&opid=<%=opid %>";
</script>
<%
}else
{
%>
<script type="text/javascript">
document.location.href = "<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=opid%>";
</script>
<%
}
 %>


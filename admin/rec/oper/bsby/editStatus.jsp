<%@ page language="java" pageEncoding="UTF-8"%>
<%
String opid = (String) request.getParameter("opid");
String look = (String) request.getAttribute("look");
String tip = (String) request.getAttribute("tip");
%>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script type="text/javascript">
alert("<%=tip%>");
</script>
<% if(look!=null)
{
%>
<script type="text/javascript">
window.location.href = "<%=request.getContextPath()%>/ByBsController/getByOpid.mmx?lookup=1&opid=<%=opid%>";
</script>
<%
}else
{
%>
<script type="text/javascript">
window.location.href = "<%=request.getContextPath()%>/ByBsController/getByOpid.mmx?opid=<%=opid%>";
</script>
<%
}
 %>
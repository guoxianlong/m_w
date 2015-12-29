<%@ page language="java" pageEncoding="UTF-8"%>
<%
String opid = request.getParameter("opid");
String tip = (String) request.getAttribute("tip");
%>
<jsp:include page="../../inc/easyui.jsp"></jsp:include>
<jsp:include page="../../inc/easyui-portal.jsp"></jsp:include>
<script>
alert("<%=tip%>");
window.location.href = "<%=request.getContextPath()%>/ByBsController/getByOpid.mmx?opid=<%=opid%>";
<%-- $.messager.alert("提示", "<%=tip%>", "info" , function() {window.location.href = "<%=request.getContextPath()%>/ByBsController/getByOpid.mmx?opid=<%=opid%>";}); --%>
</script>
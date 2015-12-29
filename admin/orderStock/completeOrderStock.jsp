<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*,adultadmin.util.StringUtil,adultadmin.bean.*,adultadmin.action.vo.*" %>
<%

voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();

OrderStockAction orderStockAction = new OrderStockAction();
orderStockAction.completeOrderStock(request, response);
String back = request.getParameter("back");
String action = StringUtil.convertNull(request.getParameter("action"));
String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
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
if(back!=null && back.trim().length()>0){
%>
<script>
//alert("修改成功！");
<%if(request.getAttribute("swWarning")!=null){%>
alert("<%=request.getAttribute("swWarning").toString()%>");
<%}%>
<%if(request.getAttribute("snWarning")!=null){%>
alert("<%=request.getAttribute("snWarning").toString()%>");
<%}%>
<%if(request.getAttribute("tip")!=null){%>
	alert("<%=request.getAttribute("tip").toString()%>");
<%}%>
<%if(action.equals("completeOrderStock") && group.isFlag(319)){%>
document.location = "<%=request.getContextPath() %>/admin/scanOrderStock.do?orderCode=<%=orderCode %>&orderstock=orderStock&scanFlag=1&printType=1";
<%}else if(action.equals("completeOrderStock2")){%>
document.location = "<%=request.getContextPath() %>/admin/orderStock/printPackage.jsp?orderCode=<%=orderCode %>";
<%}else{%>
document.location = "<%=back%>?id=<%=request.getParameter("operId")%>";
<%}%>
</script>
<%	
	return ;
}
%>
<script>
//alert("修改成功！");
document.location = "orderStock.jsp?id=<%=request.getParameter("operId")%>";
</script>
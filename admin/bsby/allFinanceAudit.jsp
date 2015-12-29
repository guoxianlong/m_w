<%@ page contentType="text/html;charset=utf-8"%>
<html>
<head>
<title>批量完成报损报溢单</title>
</head>
<body>
<h3>批量财务审核报损报溢单</h3>
<form action="<%=request.getContextPath() %>/admin/bybs.do?method=allFinanceAudit" method="post">
	<textarea name="bsbyOrder" rows="10" cols="20"></textarea>
	<input type="submit" value="提交报损报溢单"/>
</form>
<%if(request.getAttribute("total")!=null){ %>
	<font color="red">已经处理<%=request.getAttribute("total") %>条数据</font><br/>
<%} %>
<%if(request.getAttribute("tip")!=null){ %>
	<font color="red"><%=request.getAttribute("tip") %></font>
<%} %>
</body>
</html>
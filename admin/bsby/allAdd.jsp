<%@ page contentType="text/html;charset=utf-8"%>

<%@page import="java.util.List"%><html>
<head>
<title>批量添加报损报溢单</title>
</head>
<body>
<h3>批量添加报损报溢单</h3>
<form action="<%=request.getContextPath() %>/admin/bybs.do?method=allAdd" method="post">
	<textarea name="bsbyOrder" rows="10" cols="40"></textarea>
	<input type="submit" value="生成报损报溢单"/>
</form>
<%if(request.getAttribute("total")!=null){ %>
	<font color="red">已经处理<%=request.getAttribute("total") %>条数据</font><br/>
<%} %>
<%if(request.getAttribute("tip")!=null){ %>
	<font color="red"><%=request.getAttribute("tip") %></font>
<%} %>
<br/>
<%if(request.getAttribute("orderCodeList")!=null){%>
	<%List orderCodeList=(List)request.getAttribute("orderCodeList"); %>
		<%for(int i=0;i<orderCodeList.size();i++){ %>
			<font color="red"><%=orderCodeList.get(i).toString()%></font><br/>
		<%} %>
<%} %>
</body>
</html>
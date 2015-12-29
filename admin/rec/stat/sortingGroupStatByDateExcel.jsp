<%@ page language="java" contentType="text/html;charset=UTF-8" %>
<%
String[][] statArray=(String[][])request.getAttribute("statArray");
int year=Integer.parseInt(request.getAttribute("year").toString());
int month=Integer.parseInt(request.getAttribute("month").toString());
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>每日分播量统计</title>
<%
response.setContentType("application/vnd.ms-excel;charset=UTF-8");
String fileName = year+"-"+month+"月每日分播量统计";
response.setHeader("Content-disposition","attachment; filename=\"" + new String(fileName.getBytes("GB2312"), "ISO8859-1") + ".xls\"");
%>
</head>
<body>
<table border="1">
<%for(int i=0;i<statArray.length;i++){ %>
<tr align="center">
	<%for(int j=0;j<statArray[0].length;j++){ %>
		<%if(i==0){ %>
			<%
				if( j == 0 ) {
			%>
				<td><%=statArray[i][j]==null?"":statArray[i][j] %></td>
			<%	
				}
			%>
			<%
				if( (j-1)%3 == 0 ) {
			%>
				<td colspan="3"><%=statArray[i][j]==null?"":statArray[i][j] %></td>
			<%
				}
			%>
		<%}else{ %>
			<td><%=statArray[i][j]==null?"0":statArray[i][j] %></td>
		<%} %>
	<%} %>
</tr>
<%} %>
</table>
</body>
</html>
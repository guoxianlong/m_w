<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<html>
<head>
<title>城市列表</title>
<%List cityList=(List)request.getAttribute("cityList"); %>
<%List areaCountList=(List)request.getAttribute("areaCountList"); %>
<%if(request.getAttribute("deleted")!=null){ %>
<script type="text/javascript">alert("删除成功！");</script>
<%} %>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="../../js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
</head>
<body>
	<p>城市列表</p>
	<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" style="MARGIN-LEFT: 10px">
		<tr bgcolor="#4688D6">
			<td><font color="#FFFFFF">序号</font></td>
			<td><font color="#FFFFFF">城市代号</font></td>
			<td><font color="#FFFFFF">城市名称</font></td>
			<td><font color="#FFFFFF">下属地区个数</font></td>
			<td><font color="#FFFFFF">操作</font></td>
		</tr>
		<%for(int i=0;i<cityList.size();i++){ 
			CargoInfoCityBean bean=(CargoInfoCityBean)cityList.get(i);
		%>
		<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
			<td><%=i+1 %></td>
			<td><%=bean.getCode() %></td>
			<td><%=bean.getName() %></td>
			<td><a href="./cargoInfo.do?method=cargoInfoAreaList&cityId=<%=bean.getId() %>"><%=areaCountList.get(i) %></a></td>
			<%if((Integer)areaCountList.get(i)==Integer.valueOf(0)){ %>
				<td><input type="button" onclick="javascript:if(confirm('如果确认删除，请单击确定，反之，请单击取消!')){window.location='../admin/cargoInfo.do?cityId=<%=bean.getId() %>&method=deleteCity'}" value="删除"/></td>
			<%}else{ %>
				<td><input type="button" disabled='disabled' value="删除"></td>
			<%} %>
		</tr>
		<%} %>
	</table>
	<form action='../admin/cargoInfo.do?method=addCity' method="post">
	<fieldset style="width:360px;"><legend>添加城市</legend>
	城市代号<font color="red">*</font>：<input type="text" name="code" size=5/>(城市名称前两个字的拼音的首字母大写)<br/>
	城市名称<font color="red">*</font>：<input type="text" name="name" size=15/>(允许输入至多10个汉字)<br/>
	<input type="submit" value="提交"/>
	</fieldset>
	</form>
</body>
</html>
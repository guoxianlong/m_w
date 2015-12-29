<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<html>
<head>
<title>地区列表</title>
<%List wholeCityList=(List)request.getAttribute("wholeCityList"); %>
<%List areaList=(List)request.getAttribute("areaList"); %>
<%List storageCountList=(List)request.getAttribute("storageCountList"); %>
<%List cityNameList=(List)request.getAttribute("cityNameList"); %>
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
	地区列表
	<form action="../admin/cargoInfo.do?method=cargoInfoAreaList" method="post" style="MARGIN-LEFT: 10px">
	所属城市:
	<select name="cityId">
		<option value="">全部</option>
		<%for(int i=0;i<wholeCityList.size();i++){ 
			CargoInfoCityBean cityBean=(CargoInfoCityBean)wholeCityList.get(i);%>
			<option value="<%=cityBean.getId() %>" <%if(request.getParameter("cityId")!=null&&!request.getParameter("cityId").equals("")&&Integer.parseInt(request.getParameter("cityId"))==cityBean.getId()){ %>selected='true'<%} %>>
				<%=cityBean.getCode() %>--<%=cityBean.getName() %>
			</option>
		<%} %>
	</select>
	<input type="submit" value="查询"/>
	</form>
	<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" style="MARGIN-LEFT: 10px">
		<tr bgcolor="#4688D6">
			<td><font color="#FFFFFF">序号</font></td>
			<td><font color="#FFFFFF">地区代号</font></td>
			<td><font color="#FFFFFF">地区名称</font></td>
			<td><font color="#FFFFFF">所属城市</font></td>
			<td><font color="#FFFFFF">下属仓库个数</font></td>
			<td><font color="#FFFFFF">操作</font></td>
		</tr>
		<%for(int i=0;i<areaList.size();i++){ 
			CargoInfoAreaBean bean=(CargoInfoAreaBean)areaList.get(i);%>
		<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
			<td><%=i+1 %></td>
			<td><%=bean.getCode() %></td>
			<td><%=bean.getName() %></td>
			<td><%=cityNameList.get(i) %></td>
			<td><a href="./cargoInfo.do?method=cargoInfoStorageList&areaId=<%=bean.getId() %>&cityId=<%=bean.getCityId() %>" ><%=storageCountList.get(i) %></a></td>
			<%if((Integer)storageCountList.get(i)==Integer.valueOf(0)){ %>
				<td><input type="button" onclick="javascript:if(confirm('如果确认删除，请单击确定，反之，请单击取消!')){window.location='../admin/cargoInfo.do?areaId=<%=bean.getId() %>&method=deleteArea'}" value="删除"/></td>
			<%}else{ %>
				<td><input type="button" disabled='disabled' value="删除"></td>
			<%} %>
		</tr>
		<%} %>
	</table>
	<form action='../admin/cargoInfo.do?method=addArea' method="post">
	<fieldset style="width:420px;"><legend>添加地区：</legend>
	所属城市<font color="red">*</font>：<select name="cityId">
				<option value="">请选择</option>
				<%for(int i=0;i<wholeCityList.size();i++){ 
					CargoInfoCityBean cityBean=(CargoInfoCityBean)wholeCityList.get(i);%>
					<option value="<%=cityBean.getId() %>" <%if(request.getParameter("cityId")!=null&&!request.getParameter("cityId").equals("")&&Integer.parseInt(request.getParameter("cityId"))==cityBean.getId()){ %>selected=selected<%} %>>
					<%=cityBean.getCode() %>--<%=cityBean.getName() %>
					</option>
				<%} %>
			</select>
	地区id：<input type="text" name="areaId"><br/>
	地区代号<font color="red">*</font>：<input type="text" name="areaCode" size=14>(地区名称第一个字的拼音的首字母大写)<br/>
	地区名称<font color="red">*</font>：<input type="text" name="areaName" size=14>(允许输入至多10个汉字)<br/>
	<input type="submit" value="提交"/>
	</fieldset>
	</form>
</body>
</html>
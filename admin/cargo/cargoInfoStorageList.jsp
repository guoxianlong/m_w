<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<html>
<head>
<title>仓库列表</title>
<%List wholeCityList=(List)request.getAttribute("wholeCityList"); %>
<%List areaList=(List)request.getAttribute("areaList"); %>
<%List storageList=(List)request.getAttribute("storageList"); %>
<%List cityList=(List)request.getAttribute("cityList"); %>
<%List stockAreaCountList=(List)request.getAttribute("stockAreaCountList"); %>
<%if(request.getAttribute("deleted")!=null){ %>
<script type="text/javascript">alert("删除成功！");</script>
<%} %>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function selectcity(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&cityId="+document.getElementById("cityId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("area").innerHTML = msg;
			selectOption(document.getElementById('areaId'), '<%= request.getParameter("areaId") %>');
		}
	});
}
function selectcity2(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&cityId="+document.getElementById("cityId2").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("area2").innerHTML = msg;
			selectOption(document.getElementById('areaId'), '<%= request.getParameter("areaId") %>');
		}
	});
}
</script>
</head>
<body>
	<p>仓库列表</p>
	<form action="../admin/cargoInfo.do?method=cargoInfoStorageList" method="post" style="MARGIN-LEFT: 20px">
	所属城市:
	<select id="cityId" name="cityId" onchange="selectcity();">
		<option value="">请选择</option>
		<%for(int i=0;i<wholeCityList.size();i++){ 
			CargoInfoCityBean cityBean=(CargoInfoCityBean)wholeCityList.get(i);%>
			<option value="<%=cityBean.getId() %>" <%if(request.getParameter("cityId")!=null&&(!request.getParameter("cityId").equals(""))&&Integer.parseInt(request.getParameter("cityId"))==cityBean.getId()){ %>selected=selected<%} %>>
				<%=cityBean.getCode() %>--<%=cityBean.getName() %>
			</option>
		<%} %>
	</select>

	所属地区:
	<span id="area">
	<select name="areaId">
		<option value=""></option><option value="">请选择</option>
	</select>
	</span>
	<input type="submit" value="查询"/>
	</form>
	<script type="text/javascript">selectcity();</script>
	<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" style="MARGIN-LEFT: 20px">
		<tr bgcolor="#4688D6">
			<td><font color="#FFFFFF">序号</font></td>
			<td><font color="#FFFFFF">仓库代号</font></td>
			<td><font color="#FFFFFF">仓库名称</font></td>
			<td><font color="#FFFFFF">所属地区</font></td>
			<td><font color="#FFFFFF">所属城市</font></td>
			<td><font color="#FFFFFF">仓库内区域个数</font></td>
			<td><font color="#FFFFFF">操作</font></td>
		</tr>
		<%for(int i=0;i<storageList.size();i++){ 
			CargoInfoStorageBean bean=(CargoInfoStorageBean)storageList.get(i);%>
		<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
			<td><%=i+1 %></td>
			<td><%=bean.getCode() %></td>
			<td><%=bean.getName() %></td>
			<td><%=((CargoInfoAreaBean)areaList.get(i)).getCode() %>--<%=((CargoInfoAreaBean)areaList.get(i)).getName() %></td>
			<td><%=((CargoInfoCityBean)cityList.get(i)).getCode() %>--<%=((CargoInfoCityBean)cityList.get(i)).getName() %></td>
			<td><a href="./cargoInfo.do?method=cargoInfoStockAreaList&storageId=<%=bean.getId() %>&cityId=<%=bean.getCityId() %>&areaId=<%=bean.getAreaId() %>"><%=stockAreaCountList.get(i) %></a></td>
			<%if((Integer)stockAreaCountList.get(i)==Integer.valueOf(0)){ %>
				<td><input type="button" onclick="javascript:if(confirm('如果确认删除，请单击确定，反之，请单击取消!')){window.location='../admin/cargoInfo.do?storageId=<%=bean.getId() %>&method=deleteStorage'}" value="删除"/></td>
			<%}else{ %>
				<td><input type="button" disabled='disabled' value="删除"></td>
			<%} %>
		</tr>
		<%} %>
	</table>
	<form action='../admin/cargoInfo.do?method=addStorage' method="post">
	<fieldset style="width:370px;"><legend>添加仓库</legend>
	所属城市<font color="red">*</font>：<select id="cityId2" name="cityId" onchange="selectcity2();">
				<option value="">请选择</option>
				<%for(int i=0;i<wholeCityList.size();i++){ 
					CargoInfoCityBean cityBean=(CargoInfoCityBean)wholeCityList.get(i);%>
					<option value="<%=cityBean.getId() %>" <%if(request.getParameter("cityId")!=null&&(!request.getParameter("cityId").equals(""))&&Integer.parseInt(request.getParameter("cityId"))==cityBean.getId()){ %>selected=selected<%} %>>
						<%=cityBean.getCode() %>--<%=cityBean.getName() %>
					</option>
				<%} %>
			</select>
	所属地区<font color="red">*</font>：
		<span id="area2">
		<select>
			<option value="">请选择</option>
		</select>
		</span><br/>
	仓库代号<font color="red">*</font>：<input type="text" name="storageCode" size=14>(2位数字组成)<br/>
	仓库名称<font color="red">*</font>：<input type="text" name="storageName" size=14>(允许输入至多10个汉字)<br/>
	<input type="submit" value="提交"/>
	</fieldset>
	</form>
	<script type="text/javascript">selectcity2();</script>
</body>
</html>
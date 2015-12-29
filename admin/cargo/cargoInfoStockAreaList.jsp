<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<html>
<head>
<title>仓库列表</title>
<%
String cityId="";
String areaId="";
String storageId="";
if(request.getParameter("cityId")!=null){
	cityId="&cityId="+request.getParameter("cityId");
}
if(request.getParameter("areaId")!=null){
	areaId="&areaId="+request.getParameter("areaId");
}
if(request.getParameter("storageId")!=null){
	storageId="&storageId="+request.getParameter("storageId");
}
%>
<%List wholeCityList=(List)request.getAttribute("wholeCityList"); %>
<%List wholeStorageList=(List)request.getAttribute("wholeStorageList"); %>
<%List storageCodeList=(List)request.getAttribute("storageCodeList"); %>
<%List passageCountList=(List)request.getAttribute("passageCountList"); %>
<%List stockAreaList=(List)request.getAttribute("stockAreaList"); %>
<%PagingBean paging = (PagingBean) request.getAttribute("paging"); %>
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
			selectOption(document.getElementById('areaId'), '<%= request.getAttribute("areaId") %>');
			$.ajax({
				type: "GET",
				url: "cargoInfo.do?method=selection&areaId="+document.getElementById("areaId").value,
				cache: false,
				dataType: "html",
				data: {type: "1"},
				success: function(msg, reqStatus){
					document.getElementById("storage").innerHTML = msg;
					selectOption(document.getElementById('storageId'), '<%= request.getParameter("storageId") %>');
					selectOption(document.getElementById('storageId'), '<%= request.getAttribute("storageId") %>');
					
				}
			});
		}
	});
}
function selectarea(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&areaId="+document.getElementById("areaId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("storage").innerHTML = msg;
			selectOption(document.getElementById('storageId'), '<%= request.getParameter("storageId") %>');
		}
	});
}
</script>
</head>
<body>
	<p>仓库区域列表</p>
	<form action="../admin/cargoInfo.do?method=cargoInfoStockAreaList" method="post" style="MARGIN-LEFT: 20px">
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
	<select id="areaId" name="areaId">
		<option value="">
	</select>
	</span>
	所属仓库:
	<span id="storage">
	<select name="storageId">
		<option value=""></option>
	</select>
	</span>
	区域代号:
	<input type="text" name="stockAreaCode" <%if(request.getParameter("stockAreaCode")!=null){ %>value='<%=request.getParameter("stockAreaCode") %>'<%} %> size=2/>
	<input type="submit" value="查询"/>
	<%if(request.getAttribute("cityId")!=null){ %>
		<script type="text/javascript">
			selectOption(document.getElementById("cityId"),<%=request.getAttribute("cityId")%>);
		</script>
	<%} %>
	<script type="text/javascript">
		selectcity();
	</script>
	</form>
	<table cellpadding="3" border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5" style="MARGIN-LEFT: 20px">
		<tr bgcolor="#4688D6">
			<td><font color="#FFFFFF">序号</font></td>
			<td><font color="#FFFFFF">区域代号</font></td>
			<td><font color="#FFFFFF">所属仓库代号</font></td>
			<td><font color="#FFFFFF">区域名称</font></td>
			<td><font color="#FFFFFF">库存类型</font></td>
			<td><font color="#FFFFFF">巷道数</font></td>
			<td><font color="#FFFFFF">操作</font></td>
		</tr>
		<%for(int i=0;i<stockAreaList.size();i++){ 
			CargoInfoStockAreaBean bean=(CargoInfoStockAreaBean)stockAreaList.get(i);%>
		<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
			<td><%=paging.getCurrentPageIndex()*paging.getCountPerPage()+i+1 %></td>
			<td><%=bean.getCode() %></td>
			<td><%=storageCodeList.get(i) %></td>
			<td><%=bean.getName() %></td>
			<td><%=bean.getStockTypeName() %></td>
			<td><a href="./cargoInfo.do?method=cargoInfoShelfList&stockAreaId=<%=bean.getId() %>&cityId=<%=bean.getCityId() %>&areaId=<%=bean.getAreaId() %>&storageId=<%=bean.getStorageId() %>"><%=passageCountList.get(i) %></a></td>
			<%if((Integer)passageCountList.get(i)==Integer.valueOf(0)){ %>
				<td>
					<input type="submit" onclick="javascript:if(confirm('如果确认删除，请单击确定，反之，请单击取消!')){window.location='../admin/cargoInfo.do?stockAreaId=<%=bean.getId() %>&method=deleteStockArea&pageIndex=<%=paging.getCurrentPageIndex() %><%=cityId %><%=areaId %><%=storageId %>'}" value="删除"/>
				</td>
			<%}else{ %>
				<td>
					<input type="button" disabled='disabled' value="删除">
				</td>
			<%} %>
		</tr>
		<%} %>
	</table>
	<%if(paging!=null){ %>
		<p align="left"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
	<form action='../admin/cargoInfo.do?method=addStockArea' method="post">
	<fieldset style="width:390px;"><legend>添加仓库区域</legend>
	添加仓库区域:<br/>
	所属仓库<font color="red">*</font>：<select id="storageId" name="storageId">
					<option value="">请选择</option>
					<%for(int i=0;i<wholeStorageList.size();i++){ 
					CargoInfoStorageBean storageBean=(CargoInfoStorageBean)wholeStorageList.get(i);%>
					<option value="<%=storageBean.getId() %>" <%if(request.getParameter("storageId")!=null&&(!request.getParameter("storageId").equals(""))&&Integer.parseInt(request.getParameter("storageId"))==storageBean.getId()){ %>selected=selected<%} %>>
						<%=storageBean.getWholeCode() %>
					</option>
					<%} %>
			</select>
	库存类型<font color="red">*</font>：
		<select name="stockType">
		<% 
		Set<Integer> keyset = ProductStockBean.stockTypeMap.keySet();
		Iterator<Integer> itr = keyset.iterator();
		while( itr.hasNext()) {
			Integer key = itr.next();
		%>
			<option value="<%= key%>"><%= ProductStockBean.stockTypeMap.get(key)%></option>
			<% 
			}
			%>
		</select>
		<br/>
	区域代号<font color="red">*</font>：<input type="text" name="newStockAreaCode" size=14>（1位大写字母）<br/>
	区域名称<font color="red">*</font>：<input type="text" name="stockAreaName" size=14>(允许输入至多10个汉字)<br/>
	巷道个数<font color="red">*</font>：<input type="text" name="passageCount" size=14/>(允许输入两位纯数字)<br/>
	<input type="submit" value="提交"/>
	</fieldset>
	</form>
</body>
</html>
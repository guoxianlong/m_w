<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.*" %>
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
<%List shelfList=(List)request.getAttribute("shelfList"); %>
<%List wholeStorageList=(List)request.getAttribute("wholeStorageList"); %>
<%List wholeCityList=(List)request.getAttribute("wholeCityList"); %>
<%List stockAreaCodeList=(List)request.getAttribute("stockAreaCodeList"); %>
<%List cargoCountList=(List)request.getAttribute("cargoCountList"); %>
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
					$.ajax({
						type: "GET",
						url: "cargoInfo.do?method=selection&storageId="+document.getElementById("storageId").value,
						cache: false,
						dataType: "html",
						data: {type: "1"},
						success: function(msg, reqStatus){
							document.getElementById("stockArea").innerHTML = msg;
							selectOption(document.getElementById('stockAreaId'), '<%= request.getParameter("stockAreaId") %>');
							selectOption(document.getElementById('stockAreaId'), '<%= request.getAttribute("stockAreaId") %>');
							$.ajax({
								type: "GET",
								url: "cargoInfo.do?method=selection&stockAreaId="+document.getElementById("stockAreaId").value,
								cache: false,
								dataType: "html",
								data: {type: "1"},
								success: function(msg, reqStatus){
									document.getElementById("passage").innerHTML = msg;
									selectOption(document.getElementById('passageId'), '<%= request.getParameter("passageId") %>');
									selectOption(document.getElementById('passageId'), '<%= request.getAttribute("passageId") %>');
								}
							});
						}
					});
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
			$.ajax({
				type: "GET",
				url: "cargoInfo.do?method=selection&storageId="+document.getElementById("storageId").value,
				cache: false,
				dataType: "html",
				data: {type: "1"},
				success: function(msg, reqStatus){
					document.getElementById("stockArea").innerHTML = msg;
					selectOption(document.getElementById('stockAreaId'), '<%= request.getParameter("stockAreaId") %>');
				}
			});
		}
	});
}
function selectstorage(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&storageId="+document.getElementById("storageId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("stockArea").innerHTML = msg;
		}
	});
}
function selectstorage2(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&storageId="+document.getElementById("storageId2").value+"&add=1",
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("stockArea2").innerHTML = msg;
			selectOption(document.getElementById('stockArea2').firstChild, '<%= request.getParameter("stockAreaId") %>');
			selectOption(document.getElementById('stockArea2').firstChild, '<%= request.getAttribute("stockAreaId") %>');
		}
	});
}
function selectstockarea(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&stockAreaId="+document.getElementById("stockAreaId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("passage").innerHTML = msg;
		}
	});
}
function selectstockarea2(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&stockAreaId="+document.getElementById("stockAreaId2").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("passage2").innerHTML = msg;
		}
	});
}
</script>
</head>
<body>
	<p>货架列表</p>
	<form action="../admin/cargoInfo.do?method=cargoInfoShelfList" method="post">
	<fieldset style="width:800px;"><legend>查询栏</legend>
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
		<option value=""></option>
	</select>
	</span>
	所属仓库:
	<span id="storage">
	<select name="storageId">
		<option value=""></option>
	</select>
	</span>
	所属区域:
	<span id="stockArea">
	<select name="stockAreaId">
		<option value=""></option>
	</select>
	</span>
	所属巷道：
	<span id="passage">
	<select name="passageId">
		<option value=""></option>
	</select>
	</span>
	<br/>
	区域代号:
	<input type="text" name="stockAreaCode" size=8 <%if(request.getParameter("stockAreaCode")!=null&&(!request.getParameter("stockAreaCode").equals(""))){ %>value='<%=request.getParameter("stockAreaCode") %>'<%} %>/>（如：GZF07-A）
	货架代号:
	<input type="text" name="shelfCode" size=10 <%if(request.getParameter("shelfCode")!=null&&(!request.getParameter("shelfCode").equals(""))){ %>value='<%=request.getParameter("shelfCode") %>'<%} %>/>（如：GZF07-A0101）
	<input type="submit" value="查询"/>
	</fieldset>
	</form>
	<%if(request.getAttribute("cityId")!=null){ %>
		<script type="text/javascript">
			selectOption(document.getElementById("cityId"),<%=request.getAttribute("cityId")%>);
		</script>
	<%} %>
	<script type="text/javascript">selectcity();</script>
	<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" style="MARGIN-LEFT: 20px">
		
		<tr bgcolor="#4688D6">
			<td><font color="#FFFFFF">序号</font></td>
			<td><font color="#FFFFFF">货架号</font></td>
			<td><font color="#FFFFFF">所属区域</font></td>
			<td><font color="#FFFFFF">巷道号</font></td>
			<td><font color="#FFFFFF">库存类型</font></td>
			<td><font color="#FFFFFF">货架层数</font></td>
			<td><font color="#FFFFFF">货位数/层</font></td>
			<td><font color="#FFFFFF">操作</font></td>
		</tr>
		<%if(request.getAttribute("shelfList")!=null){ %>
		<%for(int i=0;i<shelfList.size();i++){ 
			CargoInfoShelfBean bean=(CargoInfoShelfBean)shelfList.get(i);%>
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td><%=paging.getCurrentPageIndex()*paging.getCountPerPage()+i+1 %></td>
				<td><%=bean.getCode() %></td>
				<td><%=stockAreaCodeList.get(i) %></td>
				<td><%=bean.getWholeCode().length()==9?"":bean.getWholeCode().substring(7,9) %></td>
				<%
				int stockType=bean.getStockType();%>
				<td><%=bean.getStockTypeName() %></td>
				<td><%=bean.getFloorCount() %>层</td>
				<td><%for(int j=1;j<=bean.getFloorCount();j++) {%>  <a href='./cargoInfo.do?method=selectCargoList&shelfId=<%=bean.getId() %>&floorNum=<%=j %>&areaId=<%=bean.getAreaId()%>'><%=((List)cargoCountList.get(i)).get(j-1)%></a>/<%=j%> <%} %></td>
				<td>
					<%boolean b=true; %>
					<%for(int j=1;j<=bean.getFloorCount();j++){ if(!((List)cargoCountList.get(i)).get(j-1).toString().equals("0")){b=false;}} %>
					<%if(b){ %>
						<input type="button" onclick="javascript:if(confirm('如果确认删除，请单击确定，反之，请单击取消!')){window.location='../admin/cargoInfo.do?shelfId=<%=bean.getId() %>&method=deleteShelf&stockAreaId=<%=bean.getStockAreaId() %><%=cityId %><%=areaId %><%=storageId %>'}" value="删除"/>
					<%}else{ %>
						<input type="button" disabled='disabled' value="删除">
					<%} %>
					<a href="./cargoInfo.do?method=addCargoList&shelfId=<%=bean.getId() %>">批量添加货位</a>
				</td>
			</tr>
		<%} %>
		<%} %>
	</table>
	<%if(paging!=null){ %>
		<p align="left" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
	<form action='../admin/cargoInfo.do?method=addShelf' method="post">
	<fieldset style="width:660px;"><legend>添加新货架</legend>
	所属仓库<font color="red">*</font>：
		<select id="storageId2" name="storageId" onchange="selectstorage2();">
			<option value="">请选择</option>
			<%for(int i=0;i<wholeStorageList.size();i++){ 
			CargoInfoStorageBean storageBean=(CargoInfoStorageBean)wholeStorageList.get(i);%>
			<option value="<%=storageBean.getId() %>" <%if(request.getParameter("storageId")!=null&&(!request.getParameter("storageId").equals(""))&&Integer.parseInt(request.getParameter("storageId"))==storageBean.getId()){ %>selected=selected<%} %>>
				<%=storageBean.getWholeCode() %>
			</option>
		<%} %>
		</select>
	所属区域<font color="red">*</font>：
		<span id="stockArea2">
			<select name="stockAreaId">
				<option value="" >请选择</option>
			</select>
		</span>
	巷道：
		<span id="passage2">
			<select name="passageId">
				<option value="" >请选择</option>
			</select>
		</span>	
	<br/>
	货&nbsp;&nbsp;架&nbsp;号&nbsp;：&nbsp;&nbsp;&nbsp;<input type="text" name="newShelfCode" size=10/>
	货架层数<font color="red">*</font>：共<input name="floorCount" type="text" size=3/>层<br/>
	本次添加货架个数<font color="red">*</font>：共<input type="text" name="shelfCount" size=3/>个货架
	<br/>
	<input type="submit" value="提交"/>
	</fieldset>
	</form>
	<script type="text/javascript">selectstorage2();</script>
</body>
</html>
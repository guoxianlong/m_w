<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.cargo.CargoInfoPassageBean"%>
<%@page import="adultadmin.bean.cargo.CargoInfoCityBean"%>
<%@page import="adultadmin.bean.cargo.CargoInfoStorageBean"%>
<%@page import="adultadmin.bean.PagingBean"%>
<%@page import="adultadmin.util.PageUtil"%><html>
<head>
<title>巷道列表</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<%
List passageList=(List)request.getAttribute("list");
List wholeCityList=(List)request.getAttribute("wholeCityList");
List wholeStorageList=(List)request.getAttribute("wholeStorageList");
PagingBean paging = (PagingBean) request.getAttribute("paging");
%>
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
		url: "cargoInfo.do?method=selection&storageId="+document.getElementById("storageId2").value,
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
</script>
</head>
<body>
	<p>巷道列表</p>
	<form action="cargoInfo.do?method=cargoInfoPassageList" method="post">
	<fieldset style="width:660px;"><legend>查询栏</legend>
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
	</span><br/>
	区域代号:
	<input type="text" name="stockAreaCode" size=8 <%if(request.getParameter("stockAreaCode")!=null&&(!request.getParameter("stockAreaCode").equals(""))){ %>value='<%=request.getParameter("stockAreaCode") %>'<%} %>/>（如：GZF07-A）
	巷道号:
	<input type="text" name="passageCode" size=10 <%if(request.getParameter("passageCode")!=null&&(!request.getParameter("passageCode").equals(""))){ %>value='<%=request.getParameter("passageCode") %>'<%} %>/>（如：GZF07-A01）
	<input type="submit" value="查询"/>
	</fieldset>
	</form>
	<%if(request.getAttribute("cityId")!=null){ %>
		<script type="text/javascript">
			selectOption(document.getElementById("cityId"),<%=request.getAttribute("cityId")%>);
		</script>
	<%} %>
	<script type="text/javascript">selectcity();</script>
<%if(passageList!=null){%>
<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" style="MARGIN-LEFT: 20px">
		
		<tr bgcolor="#4688D6">
			<td><font color="#FFFFFF">序号</font></td>
			<td><font color="#FFFFFF">巷道号</font></td>
			<td><font color="#FFFFFF">所属区域</font></td>
			<td><font color="#FFFFFF">库存类型</font></td>
			<td><font color="#FFFFFF">货架数</font></td>
			<td><font color="#FFFFFF">操作</font></td>
		</tr>
		<%for(int i=0;i<passageList.size();i++){ 
			CargoInfoPassageBean bean=(CargoInfoPassageBean)passageList.get(i);%>
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<td><%=i+1 %></td>
				<td><%=bean.getCode() %></td>
				<td><%=bean.getWholeCode().substring(0,7) %></td>
				<%
				int stockType=bean.getStockType();%>
				<td><%=bean.getStockTypeName() %></td>
				<td><a <%if(bean.getAreaId()==3){ %> href="cargoInfo.do?method=zcCargoList&passageId=<%=bean.getId() %>&areaId=<%=bean.getAreaId()%>"<%}else{ %>href="cargoInfo.do?method=selectCargoList&passageId=<%=bean.getId() %>"<%} %>><%=bean.getShelfCount() %></a></td>
				<td><input type="button" onclick="javascript:if(confirm('如果确认删除，请单击确定，反之，请单击取消!')){window.location='cargoInfo.do?method=deletePassage&passageId=<%=bean.getId() %>'}" value="删除"/></td>
			</tr>
		<%} %>
	</table>
<%if(paging!=null){ %>
		<p align="left" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
<%} %>
<form action='cargoInfo.do?method=addPassage' method="post">
	<fieldset style="width:660px;"><legend>添加新巷道</legend>
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
		</span><br/>
	巷&nbsp;&nbsp;道&nbsp;号&nbsp;&nbsp;：&nbsp;<input type="text" name="newPassageCode" size=10/>
	货架个数&nbsp;&nbsp;：<input type="text" name="shelfCount" size=3/>
	<br/>
	<input type="submit" value="提交"/>
	</fieldset>
</form>
	<script type="text/javascript">selectstorage2();</script>
</body>
</html>
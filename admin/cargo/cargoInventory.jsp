<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.util.StringUtil" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<html>
<head>
<title>盘点导出</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<%
List wholeCityList=null;
if(request.getAttribute("wholeCityList")!=null){
	wholeCityList=(List)request.getAttribute("wholeCityList");
}
List productLineList=null;
if(request.getAttribute("productLineList")!=null){
	productLineList=(List)request.getAttribute("productLineList");
}
List list=null;
if(request.getAttribute("list")!=null){
	list=(List)request.getAttribute("list");
}
List productLineNameList=null;
if(request.getAttribute("productLineNameList")!=null){
	productLineNameList=(List)request.getAttribute("productLineNameList");
}
List productCodeList=null;
if(request.getAttribute("productCodeList")!=null){
	productCodeList=(List)request.getAttribute("productCodeList");
}
List productNameList=null;
if(request.getAttribute("productNameList")!=null){
	productNameList=(List)request.getAttribute("productNameList");
}
String productLineId=StringUtil.convertNull(request.getParameter("productLineId"));
String storeType=StringUtil.convertNull(request.getParameter("storeType"));
String stockType=StringUtil.convertNull(request.getParameter("stockType"));
String type=StringUtil.convertNull(request.getParameter("type"));
String shelfCodeStart=StringUtil.convertNull(request.getParameter("shelfCodeStart"));
String shelfCodeEnd=StringUtil.convertNull(request.getParameter("shelfCodeEnd"));
String floorNum=StringUtil.convertNull(request.getParameter("floorNum"));
String startDate = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("startDate")));
String endDate = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("endDate")));
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

function check(){
	var cityId=document.getElementById("cityId")[document.getElementById("cityId").selectedIndex].value;
	if(cityId==""){
		alert("请选择城市！");
		return false;
	}
	var areaId=document.getElementById("areaId")[document.getElementById("areaId").selectedIndex].value;
	if(areaId==""){
		alert("请选择地区！");
		return false;
	}
	var storageId=document.getElementById("storageId")[document.getElementById("storageId").selectedIndex].value;
	if(storageId==""){
		alert("请选择仓库！");
		return false;
	}
	var stockType=document.getElementById("stockType")[document.getElementById("stockType").selectedIndex].value;
	if(stockType==""){
		alert("请选择库存类型！");
		return false;
	}
	var shelfCodeStart=document.getElementById("shelfCodeStart").value.replace(/(^\s*)|(\s*$)/g,"");
	var shelfCodeEnd=document.getElementById("shelfCodeEnd").value.replace(/(^\s*)|(\s*$)/g,"");
	if((shelfCodeStart==""&&shelfCodeEnd!="")||(shelfCodeStart!=""&&shelfCodeEnd=="")){
		alert("货架代号必须成对输入"+"\r"+"请填写完整！");
		return false;
	}
	if(shelfCodeStart!=""&&shelfCodeEnd!=""){
		var reg=/^[0-9]{2}$/;
		if(reg.exec(shelfCodeStart)==null||reg.exec(shelfCodeEnd)==null){
			alert("货架代号必须是纯数字且长度是2"+"\r"+"请重新填写！");
			return false;
		}
	}
	if(shelfCodeStart>shelfCodeEnd){
		alert("货架代号前一个输入值不得大于后一个输入值"+"\r"+"请重新填写！");
		return false;
	}
	
	var floorNum=document.getElementById("floorNum").value.replace(/(^\s*)|(\s*$)/g,"");
	if(floorNum!=""){
		if(/^[1-9]{1}$/.exec(floorNum)==null){
			alert("货架层数必须是纯数字"+"\r"+"请重新填写！");
			return false;
		}
	}
	var startDate=document.getElementById("startDate").value.replace(/(^\s*)|(\s*$)/g,"");
	var endDate=document.getElementById("endDate").value.replace(/(^\s*)|(\s*$)/g,"");
	if((startDate==""&&endDate!="")||(startDate!=""&&endDate=="")){
		alert("动碰日期必须成对输入"+"\r"+"请填写完整！");
		return false;
	}
	if(startDate!=""&&endDate!=""){
		var reg=/^[0-9]{4}-[0-9]{2}-[0-9]{2}$/;
		if(reg.exec(startDate)==null||reg.exec(endDate)==null){
			alert("动碰日期格式错误，请重新填写！"+"\r"+"正确格式，如：2011-01-01");
			return false;
		}
	}
	if(startDate>endDate){
		alert("动碰日期初始日期不得大于结束日期！"+"\r"+"请重新填写！");
		return false;
	}
	return true;
}

function print(){
	document.getElementById("action").value = "print";
	if(check()){
		if(confirm("如果确认导出，请单击‘确定’，反之，请单击‘取消’！")){
			document.cargoInventory.submit();
		}
	}	
}

function selectcargo(){
	document.getElementById("action").value = "select";
	return check();
}
</script>
</head>
<body>
<div>盘点导出</div><br/>
<form action="cargoInfo.do?method=cargoInventory" id="cargoInventory" name="cargoInventory" method="post">
<font color="red">*</font>所属城市：
	<select id="cityId" name="cityId" onchange="selectcity();">
		<option value="">请选择</option>
		<%for(int i=0;i<wholeCityList.size();i++){ 
			CargoInfoCityBean cityBean=(CargoInfoCityBean)wholeCityList.get(i);%>
			<option value="<%=cityBean.getId() %>" <%if(request.getParameter("cityId")!=null&&(!request.getParameter("cityId").equals(""))&&Integer.parseInt(request.getParameter("cityId"))==cityBean.getId()){ %>selected=selected<%} %>>
				<%=cityBean.getCode() %>--<%=cityBean.getName() %>
			</option>
		<%} %>
	</select>&nbsp;&nbsp;
	<font color="red">*</font>所属地区：
	<span id="area">
	<select name="areaId">
		<option value=""></option>
	</select>
	</span>&nbsp;&nbsp;
	<font color="red">*</font>所属仓库：
	<span id="storage">
	<select name="storageId">
		<option value=""></option>
	</select>
	</span>&nbsp;&nbsp;
	所属区域：
	<span id="stockArea">
	<select name="stockAreaId">
		<option value=""></option>
	</select>
	</span><br/>
	<font color="red">*</font>库存类型：
	<select id="stockType" name="stockType">
		<option value="">请选择</option>
		<option value=0 <%if(stockType.equals("0")){ %>selected=selected<%} %>>合格库</option>
		<option value=1 <%if(stockType.equals("1")){ %>selected=selected<%} %>>待验库</option>
		<option value=4 <%if(stockType.equals("4")){ %>selected=selected<%} %>>退货库</option>
		<option value=3 <%if(stockType.equals("3")){ %>selected=selected<%} %>>返厂库</option>
		<option value=2 <%if(stockType.equals("2")){ %>selected=selected<%} %>>维修库</option>
		<option value=5 <%if(stockType.equals("5")){ %>selected=selected<%} %>>残次品库</option>
		<option value=6 <%if(stockType.equals("6")){ %>selected=selected<%} %>>样品库</option>
		<option value=9 <%if(stockType.equals("9")){ %>selected=selected<%} %>>售后库</option>
	</select>&nbsp;&nbsp;
	货位产品线：<select name="productLineId">
					<option value="">请选择</option>
					<%for(int i=0;i<productLineList.size();i++){ %>
						<%voProductLine productLine=(voProductLine)productLineList.get(i); %>
					<option value="<%=productLine.getId() %>" <%if((!productLineId.equals(""))&&Integer.parseInt(productLineId)==productLine.getId()){ %>selected=selected<%} %>><%=productLine.getName() %></option>
					<%} %>
		       </select>&nbsp;&nbsp;
	存放类型：<select name="storeType">
					<option value="">请选择</option>
					<option value="0" <%if(storeType!=null&&storeType.equals("0")){ %>selected=selected<%} %>>散件区</option>
					<option value="1" <%if(storeType!=null&&storeType.equals("1")){ %>selected=selected<%} %>>整件区</option>
					<option value="2" <%if(storeType!=null&&storeType.equals("2")){ %>selected=selected<%} %>>缓存区</option>
					<option value="4" <%if(storeType!=null&&storeType.equals("4")){ %>selected=selected<%} %>>混合区</option>
			</select>&nbsp;&nbsp;
	货位类型：<select name="type">
					<option value="">请选择</option>
					<option value=0 <%if(type!=null&&type.equals("0")){ %>selected=selected<%} %>>普通</option>
					<option value=1 <%if(type!=null&&type.equals("1")){ %>selected=selected<%} %>>热销</option>
					<option value=2 <%if(type!=null&&type.equals("2")){ %>selected=selected<%} %>>滞销</option>
		         </select><br/>
	货架代号：<input type="text" size=10 id="shelfCodeStart" name="shelfCodeStart" <%if(shelfCodeStart!=null){ %>value='<%=shelfCodeStart%>'<%} %>/>至
			<input type="text" size=10 id="shelfCodeEnd" name="shelfCodeEnd" <%if(shelfCodeEnd!=null){ %>value='<%=shelfCodeEnd%>'<%} %>/>（如：01至30）&nbsp;&nbsp;
	货架层数：第<input type="text" size=5 id="floorNum" name="floorNum" maxlength="1" <%if(floorNum!=null){ %>value='<%=floorNum%>'<%} %> />层<br/>
	动碰日期：<input id="startDate" name="startDate" value="<%= startDate %>" size="10" onclick="SelectDate(this,'yyyy-MM-dd');" />至
			 <input id="endDate" name="endDate" value="<%= endDate %>" size="10" onclick="SelectDate(this,'yyyy-MM-dd');" />（注：如果仅查看动碰商品，请输入该时间段）<br/>
	<input type="hidden" id="action" name="action" value="select"/>
	<input type="hidden" name="isQuery" value="1"/>
	<input type="button" value="导出excel文件" onclick="return print();"/>&nbsp;&nbsp;
	<input type="submit" value="查询" onclick="return selectcargo();"/>
</form>
<script type="text/javascript">selectcity();</script>
<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5">
	<tr bgcolor="#4688D6">
		<td><font color="#FFFFFF">序号</font></td>
		<td><font color="#FFFFFF">货位产品线</font></td>
		<td><font color="#FFFFFF">产品编号</font></td>
		<td><font color="#FFFFFF">产品原名称</font></td>
		<td><font color="#FFFFFF">货位库存量</font></td>
		<td><font color="#FFFFFF">货位冻结量</font></td>
		<td><font color="#FFFFFF">货位号</font></td>
	</tr>
	<%for(int i=0;i<list.size();i++){ %>
		<%CargoProductStockBean cpsBean=(CargoProductStockBean)list.get(i); %>
	<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
		<td><%=paging.getCurrentPageIndex()*paging.getCountPerPage()+i+1 %></td>
		<td><%=productLineNameList.get(i) %></td>
		<td><a href="../admin/fproduct.do?id=<%=cpsBean.getProductId() %>" target="_blank"><%=productCodeList.get(i).toString() %></a></td>
		<td><a href="../admin/fproduct.do?id=<%=cpsBean.getProductId() %>" target="_blank"><%=productNameList.get(i).toString() %></a></td>
		<td><%=cpsBean.getStockCount()+cpsBean.getStockLockCount() %></td>
		<td><%=cpsBean.getStockLockCount() %></td>
		<td><a href="cargoInfo.do?method=updateCargoPage&cargoProductStockId=<%=cpsBean.getId() %>&cargoId=<%=cpsBean.getCargoInfo().getId()%>" target="_blank"><%=cpsBean.getCargoInfo().getWholeCode() %></a></td>
	</tr>
	<%} %>
</table>
<%if(request.getParameter("isQuery")!=null&&list!=null&&list.size()==0){ %>
<div>注：没有找到符合条件的记录</div>
<%} %>
<%if(paging!=null){ %>
		<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
</body>
</html>
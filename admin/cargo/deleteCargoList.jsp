<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>批量删除货位</title>
<%
List list=null;
List productLineList=null;
List productLineNameList=null;
List wholeStorageList=null;
if(request.getAttribute("wholeStorageList")!=null){
	wholeStorageList=(List)request.getAttribute("wholeStorageList");
}
if(request.getAttribute("productLineNameList")!=null){
	productLineNameList=(List)request.getAttribute("productLineNameList");
}
if(request.getAttribute("productLineList")!=null){
	productLineList=(List)request.getAttribute("productLineList");
}
if(request.getAttribute("list")!=null){
	list=(List)request.getAttribute("list");
}
PagingBean paging = (PagingBean) request.getAttribute("paging");
String wholeCode=request.getParameter("wholeCode");
String storageId=request.getParameter("storageId");
String stockAreaId=request.getParameter("stockAreaId");
String shelfCode=request.getParameter("shelfCode");
String floorNum=request.getParameter("floorNum");
String storeType=request.getParameter("storeType");
String stockType=request.getParameter("stockType");
String productLineId=request.getParameter("productLineId");
String type=request.getParameter("type");

%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function selectstorage(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&storageId="+document.getElementById("storageId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("stockArea").innerHTML = msg;
			<%if(request.getParameter("stockAreaId")!=null){%>
				selectOption(document.getElementById('stockAreaId'), '<%= request.getParameter("stockAreaId") %>');
				$.ajax({
					type: "GET",
					url: "cargoInfo.do?method=selection&stockAreaId="+document.getElementById("stockAreaId").value,
					cache: false,
					dataType: "html",
					data: {type: "1"},
					success: function(msg, reqStatus){
						document.getElementById("passage").innerHTML = msg;
						<%if(request.getParameter("passageId")!=null){%>
							selectOption(document.getElementById('passageId'), '<%= request.getParameter("passageId") %>');
						<%}%>
					}
				});
			<%}%>
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
function checkSubmit(){
	var cargoId=document.getElementsByName("cargoId");
	for(var i=0;i<cargoId.length;i++){
		if(cargoId[i].checked ==true){
			if(confirm("如果确认删除，请单击‘确定’，反之，请单击‘取消’！")==false){
				return false;
			}else{
				return true;
			}
		}
	}
	alert("请选择至少一个货位!");
	return false;
}

function check() {     
    var checkAll =document.getElementsByName("checkAll");
    var cargoId = document.getElementsByName("cargoId");
    for(var i=0;i<cargoId.length;i++){
    	cargoId[i].checked =checkAll[0].checked ;
    }
}
</script>
<script type="text/javascript">
<%if(request.getAttribute("tip")!=null){%>
alert('<%=request.getAttribute("tip").toString()%>');
<%}%>
</script>
</head>
<body>
<form action="../admin/cargoInfo.do?method=deleteCargoList" method="post">
批量删除货位<br/>
<fieldset style="width:780px;"><legend>查询栏</legend>
货&nbsp;&nbsp;位&nbsp;号：<input type="text" size=20 maxlength=12 name="wholeCode" <%if(wholeCode!=null){ %>value="<%=wholeCode%>"<%}%> />左精确右模糊<br/>
仓&nbsp;&nbsp;库&nbsp;号：<select id="storageId" name="storageId" onchange="selectstorage()">
<option value="">请选择</option>
<%for(int i=0;i<wholeStorageList.size();i++){ %>
<%CargoInfoStorageBean storageBean=(CargoInfoStorageBean)wholeStorageList.get(i); %>
<option value="<%=storageBean.getId() %>" <%if(storageId!=null&&(!storageId.equals(""))&&Integer.parseInt(storageId)==storageBean.getId()){ %>selected=selected<%} %>><%=storageBean.getWholeCode() %></option>
<%} %>
</select>&nbsp;&nbsp;&nbsp;&nbsp;
仓库区域：<span id="stockArea">
<select name="stockAreaId">
	<option value="">请选择</option>
</select>
</span>&nbsp;&nbsp;&nbsp;&nbsp;
巷道：<span id="passage">
<select name="passageId">
	<option value="">请选择</option>
</select>
</span>
货架代号：<input type="text" size=10 name="shelfCode" <%if(shelfCode!=null){ %>value='<%=shelfCode%>'<%} %>/>（如：01）&nbsp;&nbsp;&nbsp;&nbsp;
第几层：<input type="text" size=5 name="floorNum" <%if(floorNum!=null){ %>value='<%=floorNum%>'<%} %> /><br/>
库存类型：<select name="stockType">
			<option value="0" <%if(stockType!=null&&stockType.equals("0")){ %>selected=selected<%} %>>合格库</option>
			<option value="1" <%if(stockType!=null&&stockType.equals("1")){ %>selected=selected<%} %>>待验库</option>
			<option value="4" <%if(stockType!=null&&stockType.equals("4")){ %>selected=selected<%} %>>退货库</option>
			<option value="3" <%if(stockType!=null&&stockType.equals("3")){ %>selected=selected<%} %>>返厂库</option>
			<option value="2" <%if(stockType!=null&&stockType.equals("2")){ %>selected=selected<%} %>>维修库</option>
			<option value="5" <%if(stockType!=null&&stockType.equals("5")){ %>selected=selected<%} %>>残次品库</option>
			<option value="6" <%if(stockType!=null&&stockType.equals("6")){ %>selected=selected<%} %>>样品库</option>
			<option value="9" <%if(stockType!=null&&stockType.equals("9")){ %>selected=selected<%} %>>售后库</option>
		</select>&nbsp;&nbsp;&nbsp;&nbsp;
存放类型：<select name="storeType">
<option value="">请选择</option>
<option value="0" <%if(storeType!=null&&storeType.equals("0")){ %>selected=selected<%} %>>散件区</option>
<option value="1" <%if(storeType!=null&&storeType.equals("1")){ %>selected=selected<%} %>>整件区</option>
<option value="2" <%if(storeType!=null&&storeType.equals("2")){ %>selected=selected<%} %>>缓存区</option>
<option value="4" <%if(storeType!=null&&storeType.equals("4")){ %>selected=selected<%} %>>混合区</option>
</select>&nbsp;&nbsp;&nbsp;&nbsp;
货位产品线：<select name="productLineId">
<option value="">请选择</option>
<%for(int i=0;i<productLineList.size();i++){ %>
<%voProductLine productLine=(voProductLine)productLineList.get(i); %>
<option value="<%=productLine.getId() %>" <%if(productLineId!=null&&(!productLineId.equals(""))&&Integer.parseInt(productLineId)==productLine.getId()){ %>selected=selected<%} %>><%=productLine.getName() %></option>
<%} %>
</select>&nbsp;&nbsp;&nbsp;&nbsp;
货位类型：<select name="type">
<option value="">请选择</option>
<option value=0 <%if(type!=null&&type.equals("0")){ %>selected=selected<%} %>>普通</option>
<option value=1 <%if(type!=null&&type.equals("1")){ %>selected=selected<%} %>>热销</option>
<option value=2 <%if(type!=null&&type.equals("2")){ %>selected=selected<%} %>>滞销</option>
</select>&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" value="查询"/>
</fieldset>
</form>
<script type="text/javascript">selectstorage();</script>
<form action="../admin/cargoInfo.do?method=deleteCargo" method="post">
<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" style="MARGIN-LEFT: 10px">
		<tr bgcolor="#4688D6" >
			<td><input type="checkbox" name="checkAll" onclick="check();"/><font color="#FFFFFF">序号</font></td>
			<td><font color="#FFFFFF">货位号</font></td>
			<td><font color="#FFFFFF">货位产品线</font></td>
			<td><font color="#FFFFFF">货位警戒线</font></td>
			<td><font color="#FFFFFF">货位最大容量</font></td>
			<td><font color="#FFFFFF">存放类型</font></td>
			<td><font color="#FFFFFF">库存类型</font></td>
			<td><font color="#FFFFFF">货位类型</font></td>
			<td><font color="#FFFFFF">备注</font></td>
		</tr>
		<%for(int i=0;i<list.size();i++){ %>
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<%CargoInfoBean bean=(CargoInfoBean)list.get(i); %>
				<td><input type="checkbox" name="cargoId" value="<%=bean.getId() %>"/><%=paging.getCurrentPageIndex()*paging.getCountPerPage()+i+1 %></td>
				<td><a href="../admin/cargoInfo.do?method=updateCargoPage&cargoId=<%=bean.getId()%>" target="_blank"><%=bean.getWholeCode() %></a></td>
				<td><%=productLineNameList.get(i)%></td>
				<td><%=bean.getWarnStockCount() %></td>
				<td><%=bean.getMaxStockCount() %></td>
				<td><%=bean.getStoreTypeName() %></td>
				<td><%=bean.getStockTypeName() %></td>
				<td><%=bean.getTypeName() %></td>
				<td><%=bean.getRemark().length()>3?bean.getRemark().substring(0,3)+"...":bean.getRemark() %></td>
			</tr>
		<%} %>
</table>
<input type="hidden" name="pageIndex" value="<%=paging.getCurrentPageIndex() %>"/>
<%if(wholeCode!=null){ %><input type="hidden" name="wholeCode" value="<%=wholeCode%>" /><%} %>
<%if(storageId!=null){ %><input type="hidden" name="storageId" value="<%=storageId %>" /><%} %>
<%if(stockAreaId!=null){ %><input type="hidden" name="stockAreaId" value="<%=stockAreaId %>" /><%} %>
<%if(shelfCode!=null){ %><input type="hidden" name="shelfCode" value="<%=shelfCode %>" /><%} %>
<%if(floorNum!=null){ %><input type="hidden" name="floorNum" value="<%=floorNum %>" /><%} %>
<%if(storeType!=null){ %><input type="hidden" name="storeType" value="<%=storeType %>" /><%} %>
<%if(stockType!=null){ %><input type="hidden" name="stockType" value="<%=stockType %>" /><%} %>
<%if(productLineId!=null){ %><input type="hidden" name="productLineId" value="<%=productLineId %>" /><%} %>
<%if(type!=null){ %><input type="hidden" name="type" value="<%=type %>" /><%} %>
<input type="submit" onclick="return checkSubmit()" value="批量删除货位" style="MARGIN-LEFT: 10px"/>
</form>
<%if(paging!=null){ %>
		<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
<%} %>
</body>
</html>
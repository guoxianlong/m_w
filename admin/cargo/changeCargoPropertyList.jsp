<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.util.StringUtil" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<html>
<head>
<title>批量修改货位属性-货位列表页</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<%
List storageList=null;
if(request.getAttribute("storageList")!=null){
	storageList=(List)request.getAttribute("storageList");
}
List cargoList=null;
if(request.getAttribute("cargoList")!=null){
	cargoList=(List)request.getAttribute("cargoList");
}
List relatedOrderCountList=null;
if(request.getAttribute("relatedOrderCountList")!=null){
	relatedOrderCountList=(List)request.getAttribute("relatedOrderCountList");
}
PagingBean paging = (PagingBean) request.getAttribute("paging");
String storageId=StringUtil.convertNull(request.getParameter("storageId"));
String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));
String shelfCode=StringUtil.convertNull(request.getParameter("shelfCode"));
%>
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
function checkAll(){
	var selectAll=document.getElementById("selectAll");
	var cargoId=document.getElementsByName("cargoId");
	for(var i=0;i<cargoId.length;i++){
		cargoId[i].checked=selectAll.checked;
	}
	var pages=document.getElementsByTagName("a");
	for(var i=0;i<pages.length;i++){
		if(selectAll.checked==true){
			pages[i].href=pages[i].href+"&checkAll=1";
		}else{
			pages[i].href=pages[i].href.substring(0,pages[i].href.length-11);
		}
	}
}
function check(){
	var shelfCode=trim(document.getElementById("shelfCode").value);
	var reg=/^[A-Z]{3}[0-9]{2}-[A-Z]{1}[0-9]{4}$/;
	if(shelfCode!=""&&shelfCode!="例：GZF07-A0101"&&reg.exec(shelfCode)==null){
		alert("货架代号错误，请重新输入！");
		return false;
	}else if(shelfCode!=""&&shelfCode=="例：GZF07-A0101"){
		document.getElementById("shelfCode").value="";
	}
	return true;
}
function blurShelfCode(){
	var shelfCode=document.getElementById("shelfCode");
	if(shelfCode.value==""){
		shelfCode.style.color="#B8B8B5";
		shelfCode.value="例：GZF07-A0101";
	}
}
function focusShelfCode(){
	var shelfCode=document.getElementById("shelfCode");
	if(shelfCode.value=="例：GZF07-A0101"){
		shelfCode.value="";
	}
	shelfCode.style.color="black";
}
function checkChange(){
	var cargoId=document.getElementsByName("cargoId");
	for(var i=0;i<cargoId.length;i++){
		if(cargoId[i].checked==true){
			return true;
		}
	}
	alert("请先选择货位！");
	return false;
}
function clickCargo(para){
	if(para.checked==false){
		if(document.getElementById("selectAll").checked==true){
			var pages=document.getElementsByTagName("a");
			for(var i=0;i<pages.length;i++){
				pages[i].href=pages[i].href.substring(0,pages[i].href.length-11);
			}
		}
		document.getElementById("selectAll").checked=false;
	}
}
</script>
</head>
<body>
<form action="cargoInfo.do" method="get">
	<input type="hidden" name="method" value="changeCargoPropertyList"/>
	<fieldset style="width:80%;">
		<legend>查询栏</legend>
		所属仓库：
		<select id="storageId" name="storageId" onchange="selectstorage()">
			<option value="">请选择</option>
			<%for(int i=0;i<storageList.size();i++){ %>
				<%CargoInfoStorageBean storageBean=(CargoInfoStorageBean)storageList.get(i); %>
				<option value="<%=storageBean.getId() %>" <%if(storageId!=null&&(!storageId.equals(""))&&Integer.parseInt(storageId)==storageBean.getId()){ %>selected=selected<%} %>><%=storageBean.getWholeCode() %></option>
			<%} %>
		</select>&nbsp;&nbsp;&nbsp;&nbsp;
		所属区域：
		<span id="stockArea">
			<select name="stockAreaId">
				<option value="">请选择</option>
			</select>
		</span>&nbsp;&nbsp;&nbsp;&nbsp;
		巷道：<span id="passage">
		<select name="passageId">
			<option value="">请选择</option>
		</select>
		</span>
		货架代号：<input type="text" id="shelfCode" size=15 name="shelfCode" <%if(shelfCode!=null){ %>value='<%=shelfCode%>'<%} %> onfocus="focusShelfCode();" onblur="blurShelfCode();"/>（可选）
		<input type="submit" value="查询" onclick="return check();"/>
	</fieldset>
</form>
<%if(cargoList!=null&&cargoList.size()>0){%>
<form action="cargoInfo.do?method=showChangeCargoProperty" name="form1" method="post" target="_blank">
<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5">
	<tr bgcolor="#4688D6"  align="center">
		<td><input id="selectAll" type="checkbox" name="selectAll" onclick="checkAll();"/><font color="#FFFFFF">全选</font></td>
		<td><font color="#FFFFFF">序号</font></td>
		<td><font color="#FFFFFF">货位号</font></td>
		<td><font color="#FFFFFF">货位状态</font></td>
		<%--<td><font color="#FFFFFF">关联单据</font></td> --%>
		<td><font color="#FFFFFF">存放类型</font></td>
		<td><font color="#FFFFFF">货位类型</font></td>
		<td><font color="#FFFFFF">产品线</font></td>
		<td><font color="#FFFFFF">货位最大容量</font></td>
		<td><font color="#FFFFFF">警戒线</font></td>
	</tr>
	<%for(int i=0;i<cargoList.size();i++){ %>
		<%CargoInfoBean cargo=(CargoInfoBean)cargoList.get(i); %>
	<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>  align="center">
		<td><input type="checkbox" name="cargoId" value="<%=cargo.getId() %>" onclick="clickCargo(this);"/></td>
		<td><%=paging.getCurrentPageIndex()*paging.getCountPerPage()+i+1 %></td>
		<td><%=cargo.getWholeCode() %></td>
		<td><%=cargo.getStatusName() %></td>
		<%--<td><%=relatedOrderCountList.get(i) %></td> --%>
		<td><%=cargo.getStoreTypeName() %></td>
		<td><%=cargo.getTypeName() %></td>
		<td><%=cargo.getProductLine()==null?"其它":cargo.getProductLine().getName() %></td>
		<td><%=cargo.getMaxStockCount() %></td>
		<td><%=cargo.getWarnStockCount() %></td>
	</tr>
	<%} %>
</table>
<input type="submit" value="对勾选的货位进行属性修改" onclick="return checkChange();"/>
<%if(paging!=null){ %>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%>
<%} %>
<input type="hidden" name="storageId" value="<%=storageId %>"/>
<input type="hidden" name="stockAreaId" value="<%=stockAreaId %>"/>
<input type="hidden" name="shelfCode" value="<%=shelfCode %>"/>
</form>
<%} %>
<script type="text/javascript">
selectstorage();
blurShelfCode();
<%if(request.getParameter("checkAll")!=null&&request.getParameter("checkAll").equals("1")){%>
	document.getElementById("selectAll").checked=true;
	checkAll();
<%}%>
</script>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<html>
<head>
<title>批量修改货位属性</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<%
String storageId=request.getParameter("storageId");
String stockAreaId=request.getParameter("stockAreaId");
String shelfCode=request.getParameter("shelfCode");
String selectAll=request.getParameter("selectAll");
String[] cargoId=request.getParameterValues("cargoId");
List productLineList=(List)request.getAttribute("productLineList");
String changed="";
if(request.getAttribute("changed")!=null){
	changed=request.getAttribute("changed").toString();
}
%>
<script type="text/javascript">
<%if(changed.equals("1")){%>
	alert("修改成功！");
	window.opener.location.reload();
	window.close();
<%}else if(changed.equals("2")){%>
	alert("选取结果中包含使用中的货位，不能进行属性修改");
	window.close();
<%}%>
function checkLength(){
	var length=document.getElementById("length");
	if(length.value==""){
		length.style.color="#B8B8B5";
		length.value="cm";
	}
}
function checkLength2(){
	var length=document.getElementById("length");
	if(length.value=="cm"){
		length.value="";
	}
	length.style.color="black";
}
function checkWidth(){
	var width=document.getElementById("width");
	if(width.value==""){
		width.style.color="#B8B8B5";
		width.value="cm";
	}
}
function checkWidth2(){
	var width=document.getElementById("width");
	if(width.value=="cm"){
		width.value="";
	}
	width.style.color="black";
}
function checkHigh(){
	var high=document.getElementById("high");
	if(high.value==""){
		high.style.color="#B8B8B5";
		high.value="cm";
	}
}
function checkHigh2(){
	var high=document.getElementById("high");
	if(high.value=="cm"){
		high.value="";
	}
	high.style.color="black";
}
function check(){
	var stockType=document.getElementById("stockType").value;
	if(stockType==""){
		alert("设置的货位属性不正确，请重新输入");
		document.forms[0].reset();
		autoText();
		return false;
	}
	var storeType=document.getElementById("storeType").value;
	if(storeType==""){
		alert("设置的货位属性不正确，请重新输入");
		document.forms[0].reset();
		autoText();
		return false;
	}
	var type=document.getElementById("type").value;
	if(type==""){
		alert("设置的货位属性不正确，请重新输入");
		document.forms[0].reset();
		autoText();
		return false;
	}
	var productLineId=document.getElementById("productLineId").value;
	if(productLineId==""){
		alert("设置的货位属性不正确，请重新输入");
		document.forms[0].reset();
		autoText();
		return false;
	}
	var length=trim(document.getElementById("length").value);
	if(length==""||length=="cm"){
		alert("设置的货位属性不正确，请重新输入");
		document.forms[0].reset();
		autoText();
		return false;
	}
	if(reg.exec(length)==null){
		alert("设置的货位属性不正确，请重新输入");
		document.forms[0].reset();
		autoText();
		return false;
	}
	var width=trim(document.getElementById("width").value);
	if(width==""||width=="cm"){
		alert("设置的货位属性不正确，请重新输入");
		document.forms[0].reset();
		autoText();
		return false;
	}
	if(reg.exec(width)==null){
		alert("设置的货位属性不正确，请重新输入");
		document.forms[0].reset();
		autoText();
		return false;
	}
	var high=trim(document.getElementById("high").value);
	if(high==""||high=="cm"){
		alert("设置的货位属性不正确，请重新输入");
		document.forms[0].reset();
		autoText();
		return false;
	}
	if(reg.exec(high)==null){
		alert("设置的货位属性不正确，请重新输入");
		document.forms[0].reset();
		autoText();
		return false;
	}
	var change=confirm("确定要对选取的货位进行修改吗？");
	if(change){
		return true;
	}else{
		window.close();
		return false;
	}
}
function autoText(){
	checkLength();
	checkWidth();
	checkHigh();
}
</script>
</head>
<body>
<form action="cargoInfo.do" method="get">
	<input type="hidden" name="method" value="changeCargoProperty"/>
	<fieldset style="width:70%;">
		<legend>批量修改货位属性</legend>
		<table align="left">
			<tr>
				<td>
					库存类型：
					<select id="stockType" name="stockType">
						<option value="">请选择</option>
						<%for(int i=0;i<CargoInfoBean.stockTypeMap.size();i++){ %>
							<option value="<%=i %>"><%=CargoInfoBean.stockTypeMap.get(Integer.valueOf(i)) %></option>
						<%} %>
					</select>
				</td>
				<td>
					存放类型：
					<select name="storeType">
						<option value="">请选择</option>
							<option value="0" >散件区</option>
							<option value="1" >整件区</option>
							<option value="2" >缓存区</option>
							<option value="4" >混合区</option>
					</select>
				</td>
				<td>
					货位类型：
					<select name="type">
						<option value="">请选择</option>
						<%for(int i=0;i<CargoInfoBean.typeMap.size();i++){ %>
							<option value="<%=i %>"><%=CargoInfoBean.typeMap.get(Integer.valueOf(i)) %></option>
						<%} %>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					产品线：
					<select id="productLineId" name="productLineId">
						<option value="">请选择</option>
						<%for(int i=0;i<productLineList.size();i++){ %>
							<%voProductLine pl=(voProductLine)productLineList.get(i); %>
							<option value="<%=pl.getId() %>"><%=pl.getName() %></option>
						<%} %>
					</select>
				</td>
			</tr>
			<tr>
				<td>
					货位基本属性：
					长<input type="text" size="8" id="length" name="length" onblur="checkLength();" onfocus="checkLength2();"/>&nbsp;&nbsp;&nbsp;
				</td>
				<td colspan="2">
					宽<input type="text" size="10" id=width name="width" onblur="checkWidth();" onfocus="checkWidth2();"/>&nbsp;&nbsp;&nbsp;
					高<input type="text" size="10" id="high" name="high" onblur="checkHigh();" onfocus="checkHigh2();"/>&nbsp;&nbsp;&nbsp;
					<input type="hidden" name="storageId" value="<%=storageId %>"/>
					<input type="hidden" name="stockAreaId" value="<%=stockAreaId %>"/>
					<input type="hidden" name="shelfCode" value="<%=shelfCode %>"/>
					<input type="hidden" name="selectAll" value="<%=selectAll %>"/>
					<input type="submit" value="修改属性" onclick="return check();"/>
					<%for(int i=0;i<cargoId.length;i++){ %>
						<input type="hidden" name="cargoId" value="<%=cargoId[i] %>"/>
					<%} %>&nbsp;
				</td>
			</tr>
		</table>
	</fieldset>
</form>
<script type="text/javascript">
autoText();
</script>
</body>
</html>
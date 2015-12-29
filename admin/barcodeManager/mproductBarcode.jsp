<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 <%@ include file="/taglibs.jsp"%>
<%@ page import="adultadmin.action.vo.ProductBarcodeVO" %>
<%@ page import="adultadmin.action.vo.voProduct" %>
<%@ page import="adultadmin.util.StringUtil" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>修改产品条形码</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<%ProductBarcodeVO barcodeVo = (ProductBarcodeVO)request.getAttribute("barcodeVO");
	voProduct vop = (voProduct)request.getAttribute("product");
	String productCode = StringUtil.convertNull(request.getParameter("productCode"));
	String procBarcode = StringUtil.convertNull(request.getParameter("procBarcode"));
	String productName = java.net.URLDecoder.decode(StringUtil.convertNull(request.getParameter("eProductName")),"UTF-8");
	String orinameProc = java.net.URLDecoder.decode(StringUtil.convertNull(request.getParameter("eOrinameProc")),"UTF-8");
%>
<script type="text/javascript"><!--
var result ="";
var result ="";
var result ="";
function checksubmit(){
	var reg=new RegExp("^\\d{5,15}$");
	var oldbarcode = document.getElementById("oldbarcode");
	var barcode = document.getElementById("barcode");
	var bs = document.getElementsByName("barcodeSource");
	if(oldbarcode.value=="" && barcode.value==""){
		alert("请输入新条形码再提交！");
		barcode.focus();
		return false;
	}else if(oldbarcode.value==barcode.value){
		alert("新条形码与原条形码相同，请重新输入！");
		barcode.focus();
		return false;
	}
	/*if(bs[1].checked && oldbarcode.value.substring(0,8)==barcode.value){
		alert("新条形码与原条形码相同，请重新生成！");
		return false;;
	}*/
	if(result!=barcode.value && bs[1].checked){
		alert("对不起，条码不是系统生成，不能选择该来源！");
		bs[1].checked=false;
		return false;
	}
	if(barcode.value.length!=0){
		if(barcode.value.indexOf("'")!=-1){
			alert("条形码不允许输入单引号(')，请重新输入！");
			barcode.focus();
			return false;
		}
		
		if(!bs[0].checked && !bs[1].checked){
			alert("请选择条形码来源！");
			bs[0].focus();
			return false;
		}
	}
	if(oldbarcode.value!="" && barcode.value==""){
		return confirm("新条形码未填写，将会清空原条形码。是否确认修改？");
	}
	if(confirm("是否确认修改条形码？")){
		document.getElementById('submitBt').disabled='disabled';
		return true;
	}else
		return false;
}

/**
 * 打开系统生成条码窗口
 */
function openBarcode(){
	var para='';
	var name="<%=vop.getParent1().getName() %>";
	var name2="<%=vop.getParentId2()!=0?vop.getParent2().getName():"" %>";
	para="?catalogId=<%=vop.getParentId1()%>&catalogId2=<%=vop.getParentId2()%>";
	//para=window.encodeURI(para);
	var paramArr= new Array();
	paramArr[0]=name;
	paramArr[1]=name2;
	var resultT = window.showModalDialog("barcodeManager/createBarcodeInfo.jsp"+para,paramArr,"location=no");
	if(resultT){
		result=resultT;
		var barcodeObj = document.getElementById("barcode");
		barcodeObj.value=resultT;
		barcodeObj.readOnly=true;
		document.getElementById("barcodeSource2").checked="checked";
		document.getElementById("barcodeStrs").innerText="新条形码前8位：";
	}
}
//条码状态改变时修改文本框事件
function barcodeSourceChange(obj,flag){
	var barcodeObj = document.getElementById("barcode");
	var barcodeStrObj = document.getElementById("barcodeStrs");
	if(flag==0){
		barcodeObj.readOnly=!obj.checked;
		barcodeStrObj.innerText="新条形码：";
	}else{
		barcodeObj.readOnly=obj.checked;
		barcodeStrObj.innerText="新条形码前8位：";
	}
}
function backButton(){
	document.getElementById("back").submit();
}
</script>
</head>
<body>
<form action="productBarcode.do"  return checksubmit(); method="post">
<input type="hidden" name="id" value="<%=barcodeVo!=null?barcodeVo.getId():0 %>">
<input type="hidden" name="pid" value="<%=request.getParameter("pid") %>">
<input type="hidden" name="sysValue" value="<%=barcodeVo!=null?barcodeVo.getSysValue():"" %>">
<input type="hidden" name="productCode" value="<%=productCode%>">
<input type="hidden" name="procBarcode" value="<%=procBarcode%>">
<input type="hidden" name="productName" value="<%=productName%>">
<input type="hidden" name="orinameProc" value="<%=orinameProc%>">
<table cellpadding="0" cellspacing="5">
	<tr><td colspan="2" align="center"">修改产品条形码</td></tr>
	<tr>
		<td>产品编号：</td>
		<td><bean:write name="product" property="code"/></td>
	</tr>
	<tr>
		<td>产品原名：</td>
		<td><bean:write name="product" property="oriname"/></td>
	</tr>
	<tr>
		<td>小店名称：</td>
		<td><bean:write name="product" property="name"/></td>
	</tr>
	<tr>
		<td>原条形码：</td>
		<td><%=barcodeVo!=null?barcodeVo.getBarcode():""%>
		<input type="hidden" value="<%=barcodeVo!=null?barcodeVo.getBarcode():""%>" id="oldbarcode" name="oldBarcode"/>	
		</td>
	</tr>
	<tr>
		<td>原条形码来源：</td>
		<td><%=barcodeVo!=null?barcodeVo.getBarSource():"" %></td>
	</tr>
	<tr>
		<td id="barcodeStrs">新条形码：</td>
		<td><input type="text" name="barcode" id="barcode" maxlength="50" value=""/>
		<input type="button" value="系统生成"  title="点击系统生成条形码" onclick="openBarcode();"/></td>
	</tr>
	<tr><td height="30" align="center" >新条码形来源：</td>
	<td height="30"  style="vertical-align: middle;">
		<input id="barcodeSource1" type="radio" name="barcodeSource" value="1" onclick="barcodeSourceChange(this,0)" checked="checked"/>产品自带
	<input id="barcodeSource2" type="radio" name="barcodeSource" value="2" onclick="barcodeSourceChange(this,1)"/>系统生成</td>
	</tr>    
	<tr>
		<td colspan="2"><input type="submit" value="确  认" id="submitBt"/>
		<input type="button" value="返  回" onclick="backButton()"/></td>
	</tr>
</table>
</form>
<form id = "back"  action="fproductBarcode.do" method="post">
				<input type="hidden" name="productCode" id="productCodeId" value='<%=productCode%>'/>
				&nbsp;<input type="hidden" name="procBarcode" id="procBarcodeId" value='<%=procBarcode%>'/><br/>
				<input type="hidden" name="productName" id="productNameId" value='<%=productName%>'/>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="hidden" name="orinameProc" id="orinameProcId" value='<%=orinameProc%>'/>
		</form>
</body>
</html>
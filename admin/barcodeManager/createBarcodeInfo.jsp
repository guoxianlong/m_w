<%@page import="java.net.URLDecoder"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file="../taglibs.jsp"%>
<%@ page import="java.util.List"%>
<%@page import="adultadmin.action.barcode.ProductBarcodeAction" %>    
<%@ page import="adultadmin.bean.barcode.CatalogCodeBean"%>
<%@ page import="adultadmin.bean.barcode.StandardsInfoBean"%>
<%
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>系统生成条码</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<%
ProductBarcodeAction productAction = new ProductBarcodeAction();
productAction.getProductStandardsInfo(request,response);
request.setCharacterEncoding("UTF-8");
CatalogCodeBean codeBean = (CatalogCodeBean)request.getAttribute("codeBean");
CatalogCodeBean codeBean2 = (CatalogCodeBean)request.getAttribute("codeBean2");

List standardsInfoList = (List)request.getAttribute("standardsInfoList");
List standardsWeightList = (List)request.getAttribute("standardsWeightList");
List standardsColorList = (List)request.getAttribute("standardsColorList");
String result = (String) request.getAttribute("result");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%><script>alert("<%=tip%>");window.close();</script><%return;}%>
<script type="text/javascript">
function trim(str){   
	return str.toString().replace(/^\s+|\s+$/g,"");   
}
//修改编号提交验证,生成条码
function updateCodeSubmit(){
	var catalogCode = document.getElementById("catalogCodeT");
	var standardsName = document.getElementById("standardsNameId");
	var oldCode = document.getElementById("oldCode").value;
	var reg=new RegExp("^\\d{2}$");
	if(trim(catalogCode.value).length==0){
		alert("请输入分类编号！");
		catalogCode.focus();
		return false;
	}else if(!reg.test(catalogCode.value)){
		alert("必须输入2位的分类编号！");
		catalogCode.focus();
		return false;
	}else if(oldCode==trim(catalogCode.value)){
		alert("本次填写的产品规格和原来相同，请重新填写再提交！");
		catalogCode.focus();
		return false;
	}else if(standardsName.value==0){
		alert("请选择产品规格！");
		standardsName.focus();
		return false;
	}
	return true;
	
}
/**
 * 规格变动更新编号值
 */
function standardsChanage(values){
	document.getElementById("standarSpanId").innerText=values;
	getResult();
}
/**
 * 重量变动更新编号值
 */
function standardsWeightChanage(values){
	document.getElementById("standarWeightSpanId").innerText=values;
	getResult();
}
/**
 * 颜色变动更新编号值
 */
function standardsColorChanage(values){
	document.getElementById("standarColorSpanId").innerText=values;
	getResult();
}

function getResult(){
	var result="";
	result=document.getElementById("parentCodeId").innerText;
	result+=document.getElementById("parentCodeId2").innerText;
	result+=document.getElementById("standarSpanId").innerText;
	result+=document.getElementById("standarWeightSpanId").innerText;
	result+=document.getElementById("standarColorSpanId").innerText;
	document.getElementById("resutlBarcodeId").innerText="以上规则组合成前8位："+result;
	document.getElementById("resultBracode").value=result;
	return result;
}

//初始值
window.onload=function (){
	standardsChanage(new String(document.getElementById("standardsInfoId").value));
	standardsWeightChanage(document.getElementById("standardsWeightId").value);
	standardsColorChanage(document.getElementById("standardsColorId").value);
	if(window.dialogArguments!=null){
		var paraArr = window.dialogArguments;
		document.getElementById("oneCatalogId").innerText=paraArr[0];
		if(paraArr[1]!=null && paraArr[1]!=""){
			document.getElementById("twoCatalogId").innerText=paraArr[1];
		}else
			document.getElementById("twoCatalogId").innerText="空缺";
		
	} 
	getResult();
};

//单击确认处理
function okClickHandler(){
	var tmp = document.getElementById("resultBracode").value;
	var standards = document.getElementById("standardsInfoId");
	var standardsWeight = document.getElementById("standardsWeightId");
	var standardsColor =  document.getElementById("standardsColorId");
	if(standards.value==""){
		alert("产品规格不能为空，请选择。");
		standards.focus();
		return ;
	}else if(standardsWeight.value==""){
		alert("产品重量不能为空，请选择。");
		standardsWeight.focus();
		return ;
	}else if(standardsWeight.value==""){
		alert("产品颜色不能为空，请选择。");
		standardsColor.focus();
		return ;
	}
	if(tmp.length!=8){
		alert("条码不是8位，请确认是否数据是否正确。");
		return ;
	}
	window.returnValue=tmp;
	window.close();
}

</script>
</head>
<body>
<div align="center">
<p><b>系统生成条码</b></p>
<table width="80%">
<tr>
	<td>条码规则</td>
	<td>属性</td>
	<td>对应数值</td>
</tr>
<tr>
<td>一层分类：</td>
<td id="oneCatalogId"></td>
<td id="parentCodeId"><%=codeBean.getCatalogCode() %></td>
</tr>
<tr>
<td>二层分类：</td>
<td id="twoCatalogId"></td>
<td id="parentCodeId2"><%=codeBean2!=null?codeBean2.getCatalogCode():"00" %></td>
</tr>
<tr>
<td>产品规格：</td>
<td><select name="standardsInfo" id="standardsInfoId" onchange="standardsChanage(new String(this.value));">
<option value="00">默认值</option>
	<%if(standardsInfoList!=null && standardsInfoList.size()>0){
		for(int i=0;i<standardsInfoList.size();i+=1){
			StandardsInfoBean standardsInfoBean = (StandardsInfoBean)standardsInfoList.get(i);
			%><option value="<%=standardsInfoBean.getInfoCode() %>"><%=standardsInfoBean.getDescribes()%></option><%	
		}}%>
</select></td>
<td><span id="standarSpanId"></span></td>
</tr>
<tr>
<td>产品重量：</td>
<td><select name="standardsWeight" id="standardsWeightId" onchange="standardsWeightChanage(new String(this.value));">
	<%if(standardsWeightList!=null && standardsWeightList.size()>0){
		for(int i=0;i<standardsWeightList.size();i+=1){
			StandardsInfoBean standardsInfoBean = (StandardsInfoBean)standardsWeightList.get(i);
			%><option value="<%=standardsInfoBean.getInfoCode() %>"><%=standardsInfoBean.getDescribes()%></option><%	
		}}else{%><option value="">&nbsp;&nbsp;</option><%}%>
</select></td>
<td><span id="standarWeightSpanId"></span></td>
</tr>
<tr>
<td>产品颜色：</td>
<td><select name="standardsColor" id="standardsColorId" onchange="standardsColorChanage(new String(this.value));">
	<%if(standardsColorList!=null&& standardsColorList.size()>0){
		for(int i=0;i<standardsColorList.size();i+=1){
			StandardsInfoBean standardsInfoBean = (StandardsInfoBean)standardsColorList.get(i);
			%><option value="<%=standardsInfoBean.getInfoCode() %>"><%=standardsInfoBean.getDescribes()%></option><%	
		}}else{%><option value="0">&nbsp;&nbsp;</option><%}%>
</select></td>
<td><span id="standarColorSpanId"></span></td>
</tr>
<tr>
</tr>
</table>
<div align="left" style="padding-left: 50px;">
<span id="resutlBarcodeId"></span><br/>
<input type="button" id="okButton" onclick="okClickHandler();" value="确 认"/>
<input type="hidden"  id="resultBracode"/><br/>
注：系统生成的条形码由12位数组成，前8位在本页组合生成，而后4位则添加产品时由后台自动加上。
</div>
</div>
</body>
</html>
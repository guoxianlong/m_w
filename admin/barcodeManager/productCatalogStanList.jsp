<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file="../taglibs.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="adultadmin.bean.barcode.CatalogCodeBean"%>
<%@page import="adultadmin.action.barcode.BarcodeCreateManagerAction" %>    
<%@ page import="adultadmin.bean.barcode.ProductStandardsBean"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>产品分类编号</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
function updateBefore(sid,code,cname,standar,cid){
	if(code=='null')
		code="";
	if(cname=='null')
		cname="";
	if(standar=='null')
		standar="请选择";
	var htmlstr='<%	BarcodeCreateManagerAction barcodeAction = new BarcodeCreateManagerAction();barcodeAction.getProductStandards(request,response);List codeStandList = (List)request.getAttribute("standardsList");String result = (String) request.getAttribute("result");if("failure".equals(result)){String tip = (String) request.getAttribute("tip");%>alert("<%=tip%>");<%return;}%>';
	htmlstr+='<fieldset id="ucodeStanFiled" style="width: 80%; text-align: left;">';
	htmlstr+='<legend>添加/修改编号及产品规格</legend><form action="barcodeCreateManager.do"  onsubmit="return updateCodeSubmit();"  method="post">';
	htmlstr+='产品分类：<span id="spanName">'+cname+'</span><br/>';
	htmlstr+='编号：<input type="text" name="catalogCode" maxlength="2" id="catalogCodeT" size="5" value="'+code+'"/>必须是2位整数<br/>';
	htmlstr+='<input type="hidden" value="'+code+'" id="oldCode"/><input type="hidden" value="'+standar+'" id="oldStandar"/>产品规格：<select name="standardsName" id="standardsNameId"><option value="0">请选择</option>';
	htmlstr+='<%if(codeStandList!=null){for(int i=0;i<codeStandList.size();i+=1){ProductStandardsBean productStanBean = (ProductStandardsBean)codeStandList.get(i);%><option value="<%=productStanBean.getId() %>"><%=productStanBean.getStandardsName() %></option><%}}%>';
	htmlstr+='</select><input type="hidden" name="catalogCodeId" value="'+sid+'" id="catalogCodeId"/><input type="hidden" name="catalogIdsub" value="'+cid+'" />';
	htmlstr+='<input type="hidden" name="action" value="ucatalogStandards"/><input type="hidden" name="catalogId" value="<%=request.getParameter("catalogId")%>" /><br/>';
	htmlstr+='<input type="hidden" name="codeFlag" value="<%=request.getParameter("catalogId")%>" /><input type="submit" value="确认">	</form></fieldset>';
	document.getElementById("upcodeStandards").innerHTML=htmlstr;
	var select=document.getElementById("standardsNameId");
	var temlength = select.length;
	 for(var i = 0; i <temlength; i++){
	   if(select.options[i].text == standar){
	        select.options[i].selected = true;
	    }
	 } 
}

function trim(str){   
	return str.toString().replace(/^\s+|\s+$/g,"");   
}
//修改编号提交验证
function updateCodeSubmit(){
	var catalogCode = document.getElementById("catalogCodeT");
	var standardsName = document.getElementById("standardsNameId");
	var catalogCodeArr = document.getElementsByName("catalogCodeH");
	var oldCode = document.getElementById("oldCode").value;
	var oldStandar = document.getElementById("oldStandar").value;
	var reg=new RegExp("^\\d{2}$");
	if(trim(catalogCode.value).length==0){
		alert("请输入分类编号！");
		catalogCode.focus();
		return false;
	}else if(!reg.test(catalogCode.value)){
		alert("必须输入2位的分类编号！");
		catalogCode.focus();
		return false;
	}else if(standardsName.value==0){
		alert("请选择产品规格！");
		standardsName.focus();
		return false;
	}else{
		var length=catalogCodeArr.length;
		for(var i=0;i<length;i+=1){
			if(catalogCode.value==catalogCodeArr[i].value){
				alert("与其他产品分类的编号重复，请重新填写！");
				catalogCode.focus();
				return false;
			}
		}
	}
	if(oldCode==trim(catalogCode.value) && oldStandar==standardsName.options[standardsName.selectedIndex].text){
		alert("本次填写的产品规格和编号与原来相同，请重新填写再提交！");
		catalogCode.focus();
		return false;
	}
	if(oldCode !=trim(catalogCode.value)){
		var length=catalogCodeArr.length;
		for(var i=0;i<length;i+=1){
			if(catalogCode.value==catalogCodeArr[i].value){
				alert("与其他产品分类的编号重复，请重新填写！");
				catalogCode.focus();
				return false;
			}
		}
	}
	return true;
	
}
</script>
</head>
<body>
<p align="center"><b>产品分类编号规格列表</b></p>
<logic:present name="codeBean" scope="request">
<div style="width: 800px; padding-left: 100px;">
<%CatalogCodeBean voBean = (CatalogCodeBean)request.getAttribute("codeBean"); %>
所属一级产品分类：<%=voBean.getVocatalog().getName() %>&nbsp;&nbsp;编号：<%=voBean.getCatalogCode() %>
</div>
</logic:present>
<table width="80%" border="1" bordercolor="#D8D8D5" style="border-collapse:collapse;" align="center">
	<tr style="background-color:#4688D6; color:white;" >
		<td align="center">序号</td>
		<td align="center">产品分类id</td>
		<td align="center">编号</td>
		<td align="center">产品分类名称</td>
		<td align="center">产品规格</td>
	</tr>
<logic:present name="list" scope="request">
<%
List catalogList = (List)request.getAttribute("list");
for(int i=0;i<catalogList.size();i+=1){
	CatalogCodeBean codeBean = (CatalogCodeBean)catalogList.get(i);	
	%><tr><td><%=i+1 %></td>
		<td><%=codeBean.getVocatalog().getId()%></td>
		<td><a href="javascript:void(0);" onclick="updateBefore(<%=codeBean.getId() %>,'<%=codeBean.getCatalogCode()%>','<%=codeBean.getVocatalog().getName()%>','<%=codeBean.getStandardsName()%>',<%=codeBean.getVocatalog().getId()%>);">
		<%=(codeBean.getCatalogCode()==null ||codeBean.getCatalogCode().length()==0)?"添加":codeBean.getCatalogCode()%></a><input type="hidden" name="catalogCodeH" value="<%=codeBean.getCatalogCode()%>"/></td>
		<td><%=codeBean.getVocatalog().getName()%></td>
		<td><a href="javascript:void(0);" onclick="updateBefore(<%=codeBean.getId() %>,'<%=codeBean.getCatalogCode()%>','<%=codeBean.getVocatalog().getName()%>','<%=codeBean.getStandardsName()%>',<%=codeBean.getVocatalog().getId()%>);">
		<%=(codeBean.getStandardsName()==null ||codeBean.getStandardsName().length()==0)?"添加":codeBean.getStandardsName()%></a></td>
	</tr><%
}
%>
</logic:present>	
</table>
<div align="center" id="upcodeStandards">
</div>
</body>
</html>
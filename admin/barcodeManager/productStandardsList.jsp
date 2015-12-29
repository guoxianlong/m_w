<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file="../taglibs.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="adultadmin.bean.barcode.ProductStandardsBean"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>产品规格对照表</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript">

var oldName="";
function trim(str){   
	return str.toString().replace(/^\s+|\s+$/g,"");   
}
/**
*添加修改产品规格
**/
function updateCode(name,sid){
	//alert(sid+"   "+name);
	if(name==null || name.length==0)
		return ;
	document.getElementById("standardsId").value=sid;
	document.getElementById("standardsNameT").value=name;
	oldName=name;
}
//修改编号提交验证
function updateStanSubmit(){
	var standards = document.getElementById("standardsNameT");
	var standardsArr = document.getElementsByName("standardsStr");
	if(trim(standards.value)==0){
		alert("请填写产品规格名称！");
		standards.focus();
		return false;
	}else if(oldName==trim(standards.value)){
		alert("本次填写的产品规格和原来相同，请重新填写再提交！");
		standards.focus();
		return false;
	}else{
		var length = standardsArr.length;
		for(var i=0;i<length;i+=1){
			if(standards.value==standardsArr[i].value){
				alert("该产品规格已存在，请重新填写再提交！");
				standards.focus();
				return false;
			}
		}
	}
	return true;
	
}

</script>
</head>

<body>
<p align="center"><b>产品规格对照表</b></p>
<table width="80%" border="1" align="center" bordercolor="#D8D8D5" style="border-collapse:collapse;">
	<tr style="background-color:#4688D6; color:white;">
		<td width="10%" align="center">序号</td>
		<td align="center">规格名称</td>
		<td width="15%" align="center">操作</td>
	</tr>
<logic:present name="list" scope="request">
<%
List catalogList = (List)request.getAttribute("list");
for(int i=0;i<catalogList.size();i+=1){
	ProductStandardsBean productStandBean = (ProductStandardsBean)catalogList.get(i);	
	%><tr><td><%=i+1 %><input type="hidden" value="<%=productStandBean.getStandardsName() %>" name="standardsStr"/></td>
		<td><a href="productStandards.do?action=standardInfos&infoType=0&standardsId=<%=productStandBean.getId() %>" target="_blank"><%=productStandBean.getStandardsName()%></a></td>
		<td><a href="javascript:void(0);" onclick="updateCode('<%=productStandBean.getStandardsName()%>','<%=productStandBean.getId() %>');">修改</a>
			<a href="productStandards.do?action=deleteStandards&standardsId=<%=productStandBean.getId() %>" onclick="return window.confirm('是否确认要删除当前规格？')">删除</a>
		</td>
	</tr><%
}
%>
</logic:present>	
</table>
<br/>
<div align="center">
<fieldset id="ustandardsFiled" style="width: 80%;">
<legend>添加/修改产品规格</legend>
<form action="productStandards.do" method="post" onsubmit="return updateStanSubmit();" style="text-align: left;">
规格名称：<input type="text" name="standardsName" id="standardsNameT" size="10" maxlength="10"/><br/>
<input type="hidden" name="standardsId" value="0" id="standardsId"/>
<input type="hidden" name="action" value="updateStandard"/>
<input type="submit" value="确认">
</form>
</fieldset>
</div>
</body>
</html>
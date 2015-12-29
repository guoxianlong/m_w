<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file="../taglibs.jsp"%>      
<%@ page import="java.util.*"%>
<%@ page import="adultadmin.bean.barcode.CatalogCodeBean"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>产品分类编号</title>

<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">

<script type="text/javascript">
function trim(str){   
	return str.toString().replace(/^\s+|\s+$/g,"");   
}
var oldCode="";
	/**
	*添加修改编号
	**/
	function updateCode(code,name,ccid,cid){
		//alert(code+"   "+name);
		document.getElementById("ucodeFiled").style.display="block";
		var spanNames = document.getElementById("spanName");
		var catalogCode = document.getElementById("catalogCodeT");
		document.getElementById("catalogCodeId").value=ccid;
		document.getElementById("catalogId").value=cid;
		catalogCode.value="";
		if(name!='null')
			spanNames.innerText=name;
		if(code!='null')
			catalogCode.value=code;
		oldCode=code;
		catalogCode.focus();
	}
	//修改编号提交验证
	function updateCodeSubmit(){
		var catalogCode = document.getElementById("catalogCodeT");
		var catalogCodeArr = document.getElementsByName("catalogCodeH");
		var reg=new RegExp("^\\d{2}$");
		if(trim(catalogCode.value.length)==0){
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
		}else {
			var length=catalogCodeArr.length;
			for(var i=0;i<length;i+=1){
				if(catalogCode.value==catalogCodeArr[i].value){
					alert("与其他产品分类的编号重复，请重新填写！");
					catalogCode.focus();
					return false;
				}
			}
		}
		
		return window.confirm("是否确认提交？");
		
	}
	
	// 没有编号不请求
	function isCode(code){
		if(code=='null' || code.length<0){
			alert("请先添加该产品分类的编号。");
			return false;
		}
		return true;
	}
</script>
</head>
<body>
<p align="center"><b>产品分类编号</b></p>
<table id="mytab" width="80%" border="1"  bordercolor="#D8D8D5" style="border-collapse:collapse;" align="center">
	<thead align="center"><tr  style="background-color:#4688D6; color:white;">
		<td>序号</td>
		<td>产品分类id</td>
		<td>编号</td> 
		<td>产品分类名称</td>
	</tr></thead>
<logic:present name="list" scope="request">
<%
List catalogList = (List)request.getAttribute("list");
for(int i=0;i<catalogList.size();i+=1){
	CatalogCodeBean codeBean = (CatalogCodeBean)catalogList.get(i);	
	%><tr><td align="center"><%=i+1 %><input type="hidden" value="<%=codeBean.getCatalogCode()%>" name="catalogCodeH"/></td>
		<td align="center"><%=codeBean.getVocatalog().getId()%></td>
		<td align="center"><a href="javascript:void(0);" onclick="updateCode('<%=codeBean.getCatalogCode()%>','<%=codeBean.getVocatalog().getName()%>','<%=codeBean.getId()%>','<%=codeBean.getVocatalog().getId()%>');"><%=(codeBean.getCatalogCode()==null ||codeBean.getCatalogCode().length()==0)?"添加":codeBean.getCatalogCode()%></a></td>
		<td align="center"><a href="barcodeCreateManager.do?action=catalogsStandards&catalogId=<%=codeBean.getVocatalog().getId() %>" target="_blank" onclick="return isCode('<%=codeBean.getCatalogCode()%>');"><%=codeBean.getVocatalog().getName()%></a></td>
	</tr><%
}
%>
</logic:present>	 
</table>
<br/>
<fieldset id="ucodeFiled" style="display: none;">
<legend>添加/修改编号</legend>
<form action="barcodeCreateManager.do"  onsubmit="return updateCodeSubmit();">
产品分类：<span id="spanName"></span><br/>
编号：<input type="text" name="catalogCode" id="catalogCodeT" size="5" maxlength="2"/>必须是2位整数<br/>
<input type="hidden" name="catalogCodeId" value="0" id="catalogCodeId"/>
<input type="hidden" name="catalogId" value="0" id="catalogId"/>
<input type="hidden" name="action" value="updateCatalog" />
<input type="hidden" name="codeFlag" value="0" />
<input type="submit" value="确认">
</form>
</fieldset>
</body>
</html>
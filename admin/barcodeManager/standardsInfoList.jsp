<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file="../taglibs.jsp"%>
<%@ page import="java.util.*"%>
<%@ page import="adultadmin.bean.barcode.StandardsInfoBean"%>
<%@ page import="adultadmin.util.StringUtil"%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>产品规格子项列表</title>

<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<%int infoType = StringUtil.StringToId(request.getAttribute("infoType")+""); %>
<script type="text/javascript">
function trim(str){   
	return str.toString().replace(/^\s+|\s+$/g,"");   
}
var oldCode="";
var oldDescribe="";
	/**
	*添加修改编号
	**/
	function updateCode(code,describe,cid){
		//alert(cid+"   "+code+"   "+describe);
		document.getElementById("standardsInfoId").value=cid;
		//document.getElementById("standardsId").value=sid;
		var infoCode = document.getElementById("infoCodeT");
		if(describe!=null)
			document.getElementById("describeId").value=describe;
		if(code!=null)infoCode.value=code;
		oldCode=code;
		oldDescribe=describe;
	}
	//修改编号提交验证
	function updateCodeSubmit(){
		var infoCode = document.getElementById("infoCodeT");
		var describe = document.getElementById("describeId");
		var infoCodeHArr = document.getElementsByName("infoCodeH");
		var describesHArr = document.getElementsByName("describesH");
		var reg=new RegExp("^\\d{<%=infoType==0?2:1%>}");
		if(trim(infoCode.value).length==0){
			alert("请填写编号！");
			infoCode.focus();
			return false;
		}else if(!reg.test(infoCode.value)){
			alert("编号必须是数字且只能为<%=infoType==0?2:1%>位，请重新填写！");
			infoCode.focus();
			return false;
		}else if(oldCode==trim(infoCode.value) && oldDescribe==trim(describe.value)){
			alert("本次填写的内容和原来相同，请重新填写再提交！");
			infoCode.focus();
			return false;
		}else if(trim(describe.value).length==0){
			alert("请填写文字描述！");
			describe.focus();
			return false;
		}else if(describe.value.length>255){
			alert("文字描述不能超过255个字");
			describe.focus();
			return false;
		}else if(document.getElementById("standardsInfoId").value==0){
			var length=infoCodeHArr.length;
			for(var i=0;i<length;i+=1){
				if(infoCode.value==infoCodeHArr[i].value){
					alert("与其他产品分类的编号重复，请重新填写！");
					infoCode.focus();
					return false;
				}else if(describe.value==describesHArr[i].value){
					alert("当前文字描述已填写过，请重新填写再提交！");
					describe.focus();
					return false;
				}
			}
		}else{
			var length=infoCodeHArr.length;
			for(var i=0;i<length;i+=1){
				if(infoCode.value==infoCodeHArr[i].value && describe.value==describesHArr[i].value){
					alert("与其他信息重复，请重新填写！");
					infoCode.focus();
					return false;
				}
			}
		}
		if(trim(infoCode.value)=="00"){
			alert("00为系统默认编号，请重新填写！");
			infoCode.focus();
			return false;
		}
	return window.confirm("是否确认提交？");
	}
</script>
</head>
<body>
<p align="center"><b><%=infoType==0?"产品规格子项列表":infoType==1?"产品重量对照表":"产品颜色对照表" %></b></p>
<logic:present name="procStandardsBean" scope="request">
<div style="width: 800px; padding-left: 100px;">产品规格名称：<bean:write name="procStandardsBean" property="standardsName"/></div>
</logic:present>	
<table width="80%" border="1" align="center" bordercolor="#D8D8D5" style="border-collapse:collapse;">
	<tr style="background-color:#4688D6; color:white;" align="center">
		<td>编号</td>
		<td>文字描述</td>
		<td>操作</td>
	</tr>
<logic:present name="standardsList" scope="request">
<%
List catalogList = (List)request.getAttribute("standardsList");
for(int i=0;i<catalogList.size();i+=1){
	StandardsInfoBean standInfoBean = (StandardsInfoBean)catalogList.get(i);	
	%><tr><td><%=standInfoBean.getInfoCode()%><input name="infoCodeH" type="hidden" value="<%=standInfoBean.getInfoCode()%>"/></td>
		<td><%=standInfoBean.getDescribes()%><input name="describesH" type="hidden" value="<%=standInfoBean.getDescribes()%>"/></td>
		<td><a href="javascript:void(0);" onclick="updateCode('<%=standInfoBean.getInfoCode()%>','<%=standInfoBean.getDescribes()%>','<%=standInfoBean.getId()%>');">修改</a>
			<a href="productStandards.do?action=deleteStandardsInfo&standardsInfoId=<%=standInfoBean.getId() %>&standardsId=<%=standInfoBean.getStandardsId() %>&infoType=<%=infoType %>" onclick="return window.confirm('是否确认要删除当前<%=infoType==0?"子项":"信息"%>？')">删除</a>
		</td>
	</tr><%
}
%>
</logic:present>	
</table>
<br/>
<div align="center">
<fieldset style="width: 80%;">
<legend>添加/修改<%=infoType==0?"子项":""%></legend>
<form action="productStandards.do?action=uStandardsInfo"  onsubmit="return updateCodeSubmit();" method="post" style="text-align:  left;">
编号：<input type="text" name="infoCode" id="infoCodeT" size="5" maxlength="<%=infoType==0?"2":"1"%>"/><br/>
文字描述：<textarea rows="3" cols="20" name="describe" id="describeId"></textarea><br/>
<input type="hidden" name="standardsInfoId" value="0" id="standardsInfoId"/>
<input type="hidden" name="standardsId" value="<%=request.getParameter("standardsId") %>" id="standardsId"/>
<input type="hidden" name="infoType" value="<%=infoType%>" />
<input type="submit" value="确认">
</form>
</fieldset>
</div>
<%
String result = (String) request.getAttribute("result");
if(null!=result){
	String tip = (String) request.getAttribute("tip");
%><script>alert("<%=tip%>");</script><%}%>
</body>
</html>
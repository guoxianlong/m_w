<%@page import="adultadmin.util.StringUtil,mmb.delivery.domain.*"%>
<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<%@ include file="../rec/inc/easyui.jsp" %>
<%
	Object resultObj = request.getAttribute("result");
	Object msgObj = request.getAttribute("msg");
	String result = null;
	String msg = null;
	if(resultObj != null){
		result = (String) resultObj;
	}
	if(msgObj != null){
		msg = (String) msgObj;
	}
	if("".equals(msg))
		msg = null;
	if("".equals(result))
		result = null;
%>
<!DOCTYPE html>
<html>
	<head>
   	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    <title>批量导入配送时效</title>
<script type="text/javascript">
function checkUpload(){
	if($("#popId").val() == -1){
		alert("请选择POP商家");
		return false;
	}
	if ($("#areaId").combobox("getValue") == "") {
		alert("请选择地区");
		return false;
	}
	var value = $("#f").val();
	if(value.length <= 0){
		alert("请您选中文件后,点击上传按钮");
		return false;
	}else{
		if(value.indexOf(".") == -1){
			alert("请您上传excel文件,后缀名为.xlsx或者.xls");
			return false;
		}else{
			var valStack = new Array();
			valStack = value.split(".");
			var lastValue = valStack[valStack.length - 1];
			if (lastValue == "xlsx" || lastValue == "xls") {
				return true;
			}
			alert("请您上传excel文件,后缀名为.xlsx或者.xls");
			return false;					
		}
		return true;
	}
	return false;
}

function reloadData(dom){
	var popId = $(dom).val();
	$('#areaId').combobox({
		url : sysPath+'/Combobox/getStockoutAvailableArea.mmx?popId='+popId,
		valueField : 'id',
		textField : 'text',
		editable : false,
		panelHeight : 'auto'
	});
}
</script>
  </head>
  
  <body>
  	<h3 align="center">批量导入配送时效</h3>
  	<table border="1" width=800px>
  		<tr><td>
			<form action="${path}/EffectDeliverController/upload.mmx" method="post" enctype="multipart/form-data" onsubmit="return checkUpload();"> 
		  		<table class="gridtable">
		  			<tr>
		  				<td><select id="popId" name="popId" onchange="reloadData(this)">
							<option value="-1">POP商家</option>
							<option value="0">买卖宝</option>
							<option value="2">京东</option>
		  				</select></td>
		  				<td><input class="easyui-combobox" id="areaId" name="areaId" style="width:100px"/></td>
		  				<td><input type="file" name="attendance" id="f" />&nbsp;&nbsp;&nbsp;&nbsp;(请选择上传Excel)</td>
		  				<mmb:permit value="3063">
		  				<td colspan="2"><input type="submit" value="确认提交" /></td>
		  				</mmb:permit>
		  			</tr>
		  		</table>
		  	</form>
  		</td>
  		</tr>
  		  			
  		<tr><td>  		
  			操作信息
  			<div style="float: right;  position: relative; ">
  				<a href="${path}/EffectDeliverController/download.mmx?popId=<%=PopBussiness.POP_MMB%>">下载买卖宝模板</a>&nbsp
  				<a href="${path}/EffectDeliverController/download.mmx?popId=<%=PopBussiness.POP_JD%>">下载京东模板</a>
			</div>
  		</td>
  		</tr>
  		<tr><td>
  			<% if(result != null){ %>
  				<div style="color: blue;"> <%=result %> </div>
  			<%} %>
  			<% if(msg != null){ %>
  				<div style="color: red;"> <%=msg %> </div>
  			<%} %>
  			<% if(msg == null && result == null){ %>
  				&nbsp;
  			<%} %>
  		</td>
  		</tr>
  	</table>

  	<div>
  	</div>
  </body>
</html>

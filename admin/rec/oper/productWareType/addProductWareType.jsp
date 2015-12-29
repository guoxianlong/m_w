<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*" %>
<%

%>
<html>
  <head>
    
    <title>商品物流分类</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript">
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		function checkNumber(obj) {
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	$.messager.alert("提示","请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	   				$.messager.alert("提示","请填入整数！！");
	   			}
   			}
   		}	
   		
   		function checkName(obj) {
			var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
			var rs = ""; 
			var s = obj.value;
			for (var i = 0; i < s.length; i++) { 
				if( pattern.exec(s.substr(i,1))) {
					$.messager.alert("提示","请勿输入文本外的字符");
					document.getElementById("name").value="";
					document.getElementById("name").focus();
					break;
				}
			} 
   		}
   		
   		function check() {
   			var name = $("#name").val();
   			var effect = $("#effect").val();
   			
   			if( name == null || name == "") {
   				$.messager.alert("提示","请填写名称");
   				document.getElementById("name").value="";
   				document.getElementById("name").focus();
   				return;
   			} 
   			name = name.trim();
   			if( name == "" ) {
   				$.messager.alert("提示","名称不可以全为空格");
   				document.getElementById("name").value="";
   				document.getElementById("name").focus();
   				return;
   			}
   			if( name.length > 20 ) {
   				$.messager.alert("提示","输入的字符请勿大于20个");
				return;
			}
   			document.addForm.submit();
   		}
   		jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
	</script>

  </head>
  <body onload="javascript:window.opener.location.reload();">
  <div style="margin-left:15px;margin-top:15px;">

  	<h2>添加新的商品物流分类</h2>
   		<div class="easyui-panel" style="width:320px;height:160px;" data-options="title:'添加新的商品物流分类'">
   			<form name="addForm" action="<%= request.getContextPath()%>/admin/addProductWareType.mmx" method="post" onsubmit="return check();">
   				<table align="center" cellspacing="20">
	   				<tr>
	   					<td align="left">商品质检分类：</td>
	   					<td align="left"><input type="text" name="name" id="name" onblur="checkName(this);"/></td>
	   				</tr>
	   				<tr>
	   					<td align="left">
	   					</td>
					   	<td align="left">
					   	<a href="javascript:check();" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" >提交</a>
	   					</td>
	   				</tr>
   				</table>
   			</form>
   		</div>
   		<br>
   	</div>
</body>
</html>

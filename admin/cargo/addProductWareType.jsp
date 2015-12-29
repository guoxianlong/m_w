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
		   		 	alert("请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   		}	
   		
   		function toAddPage() {
   			window.location="";
   		}
   		
   		function checkName(obj) {
			var pattern = new RegExp("[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]");
			var rs = ""; 
			var s = obj.value;
			for (var i = 0; i < s.length; i++) { 
				if( pattern.exec(s.substr(i,1))) {
					alert("请勿输入文本外的字符");
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
   				alert("请填写名称");
   				document.getElementById("name").value="";
   				document.getElementById("name").focus();
   				return false;
   			} 
   			name = name.trim();
   			if( name == "" ) {
   				alert("名称不可以全为空格");
   				document.getElementById("name").value="";
   				document.getElementById("name").focus();
   				return false;
   			}
   			if( name.length > 20 ) {
				alert("输入的字符请勿大于20个");
				return false;
			}
   		}
   		
	</script>

  </head>
  <body onload="javascript:window.opener.location.reload();">
  <div style="margin-left:15px;margin-top:15px;">

  	<h2>添加新的商品物流分类</h2>
   		<div style="width:300px;height:130px;border-style:solid;border-width:1px;border-color:#000000;">
   			<form action="<%= request.getContextPath()%>/admin/productWarePropertyAction.do?method=addProductWareType" method="post" onsubmit="return check();">
   				<table align="center" cellspacing="20">
	   				<tr>
	   					<td align="left">商品质检分类：</td>
	   					<td align="left"><input type="text" name="name" id="name" onblur="checkName(this);"/></td>
	   				</tr>
	   				<tr>
	   					<td align="left"></td>
	   					<td align="left"><input type="submit" value="    提交    " /></td>
	   				</tr>
   				</table>
   			</form>
   		</div>
   		<br>
   	</div>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>打印客户信息</title>
<link href="<%=request.getContextPath() %>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
	/**
	 * 去掉字符串的空格
	 * @return
	 */
	function trim(str){   
		return str.toString().replace(/^\s+|\s+$/g,"");   
	}
	function checkSubmit(){
		var orderCode = document.getElementById("orderCode");
		if(trim(orderCode.value).length==0){
			alert("请先输入编号！");
			orderCode.value="";
			orderCode.focus();
			return false;
		}
		return true;
	}
	
	function onLoadMe(){
		var orderc = document.getElementById("orderCode");
		orderc.value="";
		orderc.focus();
	}
</script>
</head>
<body onload="onLoadMe()">
<p align="center"><b>打印客户信息</b></p>
<form action="../scanOrderStock.do" onsubmit="return checkSubmit();" style="font-size: 12px;">
	<input type="hidden" name="orderstock" value="orderStock"/>
	<input type="hidden" name="scanFlag" value="1"/>
	订单编号：<input type="text" maxlength="30" name="orderCode" id="orderCode"> <input type="submit" value="扫描确认">
	<p>操作步骤：</p>
	<p>1）	发货单复核完毕，进行包装时，请扫描发货单上的订单编号，系统会自动打印客户信息单。</p>
	<p>2）	包装完成后，把客户信息帖至包裹外面。</p>
</form>
</body>
</html>
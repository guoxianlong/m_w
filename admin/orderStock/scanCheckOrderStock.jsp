<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>扫描发货单</title>
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
<p align="center"><b>扫描发货单</b></p>
<form action="../scanOrderStock.do" onsubmit="return checkSubmit();" style="font-size: 12px;">
	<input type="hidden" name="orderstock" value="orderStock"/>
	发货清单编号：<input type="text" maxlength="30" name="orderCode" id="orderCode"> <input type="submit" value="扫描确认">
	<p>说明：</p>
	<p>1）	用扫描枪扫描发货清单编号，页面自动跳转至复核出库页。</p>
	<p>2）	可以手动输入发货清单编号，然后单击‘扫描确认’按钮，页面跳转至复核出库页。</p>
</form>
</body>
</html>
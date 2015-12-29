<%@page import="java.util.List"%>
<%@page import="adultadmin.action.stock.AddProductStock"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%
	AddProductStock addProStock = new AddProductStock();
	addProStock.AddProductStocks(request, response);
	List CodeList = (List) request.getAttribute("CodeList");
	String area = (String) request.getAttribute("area");
	String type = (String) request.getAttribute("type");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>买卖宝后台</title>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="0">
<script language="JavaScript" src="js/JS_functions.js"></script>
<script language="JavaScript" src="js/pub.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet"
	type="text/css">
<style type="text/css">
.titles {
	color: #FFFFFF;
	text-align: center;
}

.prostockinfo {
	text-align: left;
	padding-left: 10px;
}
</style>
</head>
<body>
<script type="text/javascript">
/**
 * 判断字符串长度是否超界限
 * str:要验证的字符串
 * digit:字符串的最大界限值
 * 一个汉字占2个字符
 */
function checkstr(str,digit)
{
	var n = 0;
	for (var i = 0; i < str.length; i++) {
		var leg = str.charCodeAt(i);
		if (leg > 255) {
			n+=2;
		} else {
			n+=1;
		}
	}
	if (n > digit) {
	   return true;
	} else {
	   return false;
	}
}

function checksubmit()
{
	with(addProductStockForm){
		if(area.value.length==0){
			alert("库区域不能为空！");
			area.focus();
			return false;
		}
		if(type.value.length==0){
			alert("库类别不能为空！");
			type.focus();
			return false;
		}
		if(code.value.length==0){
			if(confirm("产品编号为空，全部商品都需要添加库存记录？")) {
				return true;
			}
			code.focus();
			return false;
		}
	}
	return true;
}
function onkeyDown()
{
	if (event.keyCode == 13)
	{
		if(!checksubmit())
		{
			return false;
		}
	}
	return true;
}
</script>
<%@include file="../../header.jsp"%>
<form name="addProductStockForm" method="post" action="?method=Add">
<table width="500" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8"
	align="left">
	<tr bgcolor='#F8F8F8'>
		<td align="left" colspan="2">
		<div style="width: 90px;" align="center">添加库存记录</div>
		</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align="center" width="80">产品编号：</td>
		<td><input type="text" name="code" onkeydown="onkeyDown"
			size="25" />&nbsp;多更产品编号已逗号隔开，为空表示所有产品</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align="center">库区域值：</td>
		<td><input type="text" name="area" onkeydown="onkeyDown" size="5" /></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align="center">库类别值：</td>
		<td><input type="text" name="type" onkeydown="onkeyDown" size="5" /></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td class="prostockinfo" colspan="2"><input type="submit"
			value="确认" onclick="return checksubmit();" /></td>
	</tr>
	<%
		if (CodeList != null && CodeList.size() > 0) {
	%>
	<tr bgcolor='#F8F8F8'>
		<td class="prostockinfo" colspan="2">本次成功添加库存记录：</td>
	</tr>
	<tr>
		<td align="left" colspan="2">
		<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
			<tr bgcolor="#4688D6">
				<td width="40%" class="titles">产品编号</td>
				<td width="30%" class="titles">库区域</td>
				<td width="30%" class="titles">库类别</td>
			</tr>
			<%
				for (int i = 0; i < CodeList.size(); i++) {
			%>
			<tr bgcolor="#F8F8F8">
				<td class="prostockinfo"><%=CodeList.get(i)%></td>
				<td class="prostockinfo"><%=area%></td>
				<td class="prostockinfo"><%=type%></td>
			</tr>
			<%
				}
			%>
		</table>
		</td>
	</tr>
	<%
		} else if (CodeList != null && CodeList.size() == 0) {
	%>
	<tr bgcolor='#F8F8F8'>
		<td class="prostockinfo" colspan="2"><font color="red">数据已存在，请勿重复添加！</font></td>
	</tr>
	<%
		}
	%>
</table>
</form>
<br>
</td>
</tr>
</table>
</body>
</html>
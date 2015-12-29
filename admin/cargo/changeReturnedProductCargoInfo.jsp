<%@ page language="java" import="java.util.*,adultadmin.bean.stock.*,adultadmin.util.*,mmb.stock.stat.*,adultadmin.bean.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.action.vo.voUser,adultadmin.bean.UserGroupBean"%>
<%
	PagingBean paging = null;
	List list = (ArrayList) request.getAttribute("returnedProductCargoList");
	voUser user = (voUser) request.getSession().getAttribute("userView");
	String productCode = request.getParameter("productCode");
	String originCargo = request.getParameter("originCargo");
	String cargoCode = request.getParameter("cargoCode");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>修正目的货位</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<link rel="stylesheet" type="text/css" href="data:text/css,">
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script type="text/javascript">
		 
		 function checkNumber2(obj) {
   			var pattern = /^[0-9]{1,16}$/;
   			var number = obj.value;
   				if( number != "" ) {
	   		if(pattern.exec(number)) {
	    
	   		} else {
	   			obj.value="";
	   			obj.focus();
	    		alert("请填入整数！！");
	   		}
   			}
  		}
		 function check() {
			var pattern = /^[0-9]{1,16}$/;
			var obj = document.getElementById("productCode");
   			var number = document.getElementById("productCode").value;
   				if( number != "" ) {
	   		if(pattern.exec(number)) {
	    		return true;
	   		} else {
	   			obj.value="";
	   			obj.focus();
	    		alert("请填入整数！！");
	    		return false;
	   		}
   			}
		 }
	</script>
</head>
<body>


<center><h2>修正目的货位</h2></center>
<form action="returnStorageAction.do?method=changeReturnedProductCargoInfo" method="post" onsubmit="return check();">
<center><fieldset style="width:78%;height:50px;">
<legend align="left" style="align:left;">查询栏</legend>
<div style="margin-left:10px;position:relative;">原目的货位：<input type="text" name="originCargo" id="originCargo" size="15" value='<%= originCargo == null ? "" : originCargo %>'> 精确 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
产品编号:<input type="text" name="productCode" id="productCode" size="15" value='<%= productCode == null ? "" : productCode %>' > 精确&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<div>
<div style="margin-left:10px;margin-top:4px;position:relative;">库区号：<input type="text" name="cargoCode" id="cargoCode" size="15" value='<%= cargoCode == null ? "" : cargoCode %>'>(如：A01)&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;
<input type="submit" value=" 查  询 " >&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;</div>
</fieldset></center>
</form>
<form action="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=changeReturnedProductCargo" method="post" >
<table  width="82%" align="center" border="0" cellspacing="1" cellpadding="0" bgcolor="#000000">
	<tr bgcolor="#00ccff">
		<td align="center">序号</td>
		<td align="center">产品编号</td>
		<td align="center">原名称</td>
		<td align="center">原目的货位</td>
		<td align="center">推荐目的货位</td>
		<td align="center">退货库总存量</td>
		<td align="center">操作</td>
	</tr>
	<% 
	if( list != null && list.size() > 0 ) {
		for( int i = 0 ; i < list.size(); i ++ ) 
		{
			ReturnedProductCargoBean rpcb = (ReturnedProductCargoBean)list.get(i);
	%>
	<tr bgcolor="#ffffff">
		<td align="center"><%= i + 1 %></td>
		<td align="center"><%= rpcb.getProduct().getCode() %></td>
		<td align="center"><%= rpcb.getProduct().getOriname() %></td>
		<td align="center"><%= rpcb.getCargoInfo().getWholeCode() %></td>
		<td align="center"><%= rpcb.getTargetCargoCode() %></td>
		<td align="center"><%= rpcb.getCount() == -1 ? "没有库存量" : rpcb.getCount() %></td>
		<td align="center"><a href="returnStorageAction.do?method=changeReturnedProductCargo&cargoId=<%= rpcb.getTargetCargoId() %>&rpcbId=<%= rpcb.getId()%>&count=<%= rpcb.getCount() %>">修正目的货位<br/>打印新货位号</a></td>
	</tr>
	<%
		}
	} else {
	%>
		<tr bgcolor="#cccccc"><td colspan="7" align="center">没有需要修正货位的退货库商品，或该条件下无记录</td></tr>
	<%
		}
	%>
</table>
</br>
</form>
</body>
</html>
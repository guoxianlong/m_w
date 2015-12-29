<%@ page language="java" import="java.util.*,adultadmin.bean.stock.*,adultadmin.util.*,mmb.stock.stat.*,adultadmin.bean.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.action.vo.voUser,adultadmin.bean.UserGroupBean"%>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*,mmb.stock.stat.*" %>
<%
	PagingBean paging = (PagingBean) request.getAttribute("paging");
	List list = (ArrayList) request.getAttribute("returnedNoCargoList");
	voUser user = (voUser) request.getSession().getAttribute("userView");
	boolean batchUpdate = false;
	boolean createToCargo = false;
	if( user != null ) {
		UserGroupBean group = user.getGroup();
		batchUpdate = group.isFlag(615);
		createToCargo = group.isFlag(616);
	}
	
	String productCode = request.getParameter("productCode");
	String productName = request.getParameter("productName");
	int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request,wareArea);
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>退货库商品列表</title>
    
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
  		
  		function change1() {
  			document.getElementById("form1").action="returnStorageAction.do?method=getReturnedProductNoCargoMatched";
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
		 
		 function change2() {
		 	document.getElementById("form1").action="returnStorageAction.do?method=exportReturnedProductNoCargoMatched";
		 }
	</script>
</head>

<body>
<center><h2>无散件区货位号商品列表</h2></center>
<form action="" method="post" id="form1" onsubmit="return check();">
<center><fieldset style="width:78%;height:64px;">
<legend align="left" style="align:left;">查询栏</legend>
<div style="margin-left:10px;position:relative;">产品编号:<input type="text" name="productCode" id="productCode" size="15" value='<%= productCode == null ? "" : productCode %>' > 精确
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
产品名称：<input type="text" name="productName" id="productName" size="15" value='<%= productName == null ? "" : productName %>'> 左精确右模糊 
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;
库地区：<%= wareAreaSelectLable%>
</div>
<br>
<div style="margin-left:300px;position:relative;">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" value=" 查  询 " onclick="change1();" onsubmit="check();">
<input type="submit" value="导出为excel表" onclick="change2();" onsubmit="check();">
</div>
</fieldset></center>
</form>
<div style="align:center;margin-left:100px;width:82%;">列表说明：所有已入退货库的商品，销售状态非"已下架"，但在散件区找不到货位号的商品列表</div>
<form action="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=appraisalReturnedProductBatch" method="post" >
<table  width="82%" align="center" border="0" cellspacing="1" cellpadding="0" bgcolor="#000000">
	<tr bgcolor="#00ccff">
		<td align="center">序号</td>
		<td align="center">产品编号</td>
		<td align="center">产品名称</td>
		<td align="center">库存</td>
		<td align="center">冻结量</td>
		<td align="center">地区</td>
	</tr>
	<% 
	if( list != null && list.size() > 0 ) {
		for( int i = 0 ; i < list.size(); i ++ ) 
		{
			ProductStockBean rpb = (ProductStockBean)list.get(i);
	%>
	<tr bgcolor="#ffffff">
		<td align="center"><%= paging.getCurrentPageIndex()*paging.getCountPerPage() + i + 1 %></td>
		<td align="center"><%= rpb.getProduct().getCode() %></td>
		<td align="center"><%= rpb.getProduct().getName() %></td>
		<td align="center"><%= rpb.getStock()%></td>
		<td align="center"><%= rpb.getLockCount() %></td>
		<td align="center"><%= rpb.getAreaName(rpb.getArea()) %></td>
	</tr>
	<%
		}
	} else {
	%>
		<tr bgcolor="#cccccc"><td colspan="6" align="center">暂时还没有退货库商品</td></tr>
	<%
		}
	%>
</table>
</br>

</form>
<%if(paging!=null){ %>
		<p align="center" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
</body>
</html>

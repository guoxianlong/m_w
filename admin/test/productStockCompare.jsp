<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.util.*"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="adultadmin.test.InsertAction"%>
<%@page import="java.sql.*,adultadmin.service.*,adultadmin.service.infc.*,adultadmin.action.vo.*"%>
<%@ page contentType="text/html;charset=utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>商品库存与货位库存比较</title>
<META HTTP-EQUIV="Pragma" CONTENT="no-cache">
<META HTTP-EQUIV="Cache-Control" CONTENT="no-cache">
<META HTTP-EQUIV="Expires" CONTENT="0">
<script language="JavaScript" src="js/JS_functions.js"></script>
<script language="JavaScript" >

	function submit(){
		<%
			InsertAction insertAction=new InsertAction();
		List<ProductStockBean> list = insertAction.productStockCompare(request,response);
		%>
	}
</script>
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
<div align="center">
<form method="post">
	库类型：<select name="stockType">
				<option value="-1">全部</option>
				<% Map<Integer, String> stockTypeMap = ProductStockBean.getStockTypeMap();
				for(Map.Entry<Integer, String> entry : stockTypeMap.entrySet()){ %>
				<option value="<%=entry.getKey()%>"><%=entry.getValue() %></option>
	<%} %>	
			</select>
	库地区：<select name="areaId">
			<option value="-1">全部</option>
					<% Map<Integer, String> areaMap = ProductStockBean.getAreaMap();
					for(Map.Entry<Integer, String> entry : areaMap.entrySet()){ %>
				<option value="<%=entry.getKey()%>"><%=entry.getValue() %></option>
	<%} %>			
			</select>
	<input type="button" onclick="submit()" value="查询"/> 
</form>
<%if(list != null && list.size()>0){ %>
<font size="6" color="red">共检索<%=list.get(0).getId() %>条记录 ,查询数据库<%=list.get(0).getId()+2 %>次,用时<%=list.get(0).getStatus()/1000%>秒,有<%=list.size() %>条符合要求的记录</font>
<table  width="70%" align="center">
	<tr>
		<td width="">  商品编号</td><td  width="">商品库存</td><td  width="">商品货位库存</td><td  width="">差值</td><td  width="">库类型</td><td width="">库地区</td>
	</tr>
	<%for(ProductStockBean bean : list){ %>
	<tr>
		<td><%=bean.getProductId() %></td><td><%=bean.getStock() %></td><td>
		<%=bean.getLockCount() %></td><td><%=bean.getAllStock() %></td>
		<td><%=ProductStockBean.getStockTypeName(bean.getType()) %></td><td><%=ProductStockBean.getAreaName(bean.getArea()) %></td>
	</tr>
<%} %>
</table>
<%} %>
</div>
</body>
</html>
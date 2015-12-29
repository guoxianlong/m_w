<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,adultadmin.util.db.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.util.*, java.net.URLEncoder" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%
	voUser user = (voUser)session.getAttribute("userView");

	boolean isSystem = (user.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (user.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (user.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (user.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (user.getPermission() == 7);	//销售部
	boolean isShangpin = (user.getPermission() == 6);	//商品部
	boolean isTuiguang = (user.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (user.getPermission() == 4);	//运营中心
	boolean isKefu = (user.getPermission() == 3);	//客服部

    Connection conn = DbUtil.getConnection(DbOperation.DB_SLAVE2);
	Statement st = conn.createStatement();

String date = StringUtil.dealParam(request.getParameter("date"));
String parentName = StringUtil.dealParam(request.getParameter("parentName"));
if(parentName != null){
	parentName = Encoder.decrypt(parentName);
}
String curDate = DateUtil.formatDate(new Date());
int parentId = StringUtil.toInt(request.getParameter("parentId"));
try{
	String sql = null;
	//当天的
	if(curDate.equals(date)){
		if(parentId > 0){
			sql = "select sum(ps_bj.stock), sum(ps_gd.stock), sum(ps_bj.stock * p.price3)p1, sum(ps_gd.stock * p.price3) p2, (select name from catalog where id = parent_id2), parent_id2 from product p join (select ps.product_id, sum(ps.stock + ps.lock_count) stock from product_stock ps where ps.area=0 group by ps.product_id ) ps_bj on p.id=ps_bj.product_id  join (select ps.product_id, sum(ps.stock + ps.lock_count) stock from product_stock ps where ps.area in (1,2) group by ps.product_id) ps_gd on p.id=ps_gd.product_id where p.parent_id1 = " + parentId + " group by p.parent_id2";
		}
		else {
			sql = "select sum(ps_bj.stock), sum(ps_gd.stock), sum(ps_bj.stock * p.price3) p1, sum(ps_gd.stock * p.price3) p2, (select name from catalog where id = parent_id1), parent_id1 from product p join (select ps.product_id, sum(ps.stock + ps.lock_count) stock from product_stock ps where ps.area=0 group by ps.product_id ) ps_bj on p.id=ps_bj.product_id  join (select ps.product_id, sum(ps.stock + ps.lock_count) stock from product_stock ps where ps.area in (1,2) group by ps.product_id) ps_gd on p.id=ps_gd.product_id  group by p.parent_id1";
		}
	}
	//之前的
	else {
		if(parentId <= 0){
			sql = "select sum(product_stock_history.stock), sum(product_stock_history.stock_gd), sum(product_stock_history.stock * product.price3) p1, sum(product_stock_history.stock_gd * product.price3) p2, (select name from catalog where id = parent_id1), parent_id1 from product_stock_history join product on product_stock_history.product_id = product.id where log_date = '" + date + "' group by product.parent_id1";
		}
		else {
			sql = "select sum(product_stock_history.stock), sum(product_stock_history.stock_gd), sum(product_stock_history.stock * product.price3) p1, sum(product_stock_history.stock_gd * product.price3) p2, (select name from catalog where id = parent_id2), parent_id2 from product_stock_history join product on product_stock_history.product_id = product.id where log_date = '" + date + "' and parent_id1 = " + parentId + " group by product.parent_id2";
		}
	}	
%>
<html>
<title>买卖宝后台</title>

<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<SCRIPT src="../../js/sorttable.js" type="text/javascript"></SCRIPT>
<body>
<%@include file="../../header.jsp"%>
<p align="center"><%=date%>日的库存数据 <%if(parentName != null){%><%=parentName%><%}%></p>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center class="sortable">
<thead class=sorthead>
	<tr bgcolor='#F8F8F8'>
		<th align=center>分类名称</th>
		<th align=center>库存总数</th>
		<th align=center class="sorttable_default">库存总额</th>
		<th align=center>北京总数</th>
		<th align=center>北京总额</th>
		<th align=center>资金比例</th>
		<th align=center>广东总数</th>
		<th align=center>广东总额</th>
		<th align=center>资金比例</th>
	</tr>
</thead>
<%
    try {
		ResultSet rs = st.executeQuery(sql);
		int stockBj = 0, stockGd = 0, stock = 0;
		float priceBj = 0, priceGd = 0, price = 0;
		int totalStockBj = 0, totalStockGd = 0, totalStock = 0;
		float totalPriceBj = 0, totalPriceGd = 0, totalPrice = 0;
		String catalogName;
		int catalogId = 0;
		rs = st.executeQuery(sql);
		while(rs.next()){
			stockBj = rs.getInt(1);
			stockGd = rs.getInt(2);
			stock = stockBj + stockGd;
			priceBj = rs.getFloat(3);
			priceGd = rs.getFloat(4);
			price = priceBj + priceGd;
			catalogName = rs.getString(5);
			catalogId = rs.getInt(6);

			totalStockBj += stockBj;
			totalStockGd += stockGd;
			totalStock += stock;
			totalPriceBj += priceBj;
			totalPriceGd += priceGd;
			totalPrice += price;
%>
	<tr bgcolor='#F8F8F8'>
	    <td align=center><%if(parentId <= 0){%><a href="productStockDate.jsp?date=<%=date%>&parentId=<%=catalogId%>&parentName=<%=Encoder.encrypt(catalogName)%>"><%} else {%><a href="productStockDetailDate.jsp?date=<%=date%>&parentId=<%=catalogId%>&parentName=<%=Encoder.encrypt(catalogName)%>"><%}%><%=catalogName%><%if(parentId <= 0){%></a><%}%></td>
		<td align=center><%=stock%></td>
		<td align=center><%=StringUtil.formatFloat(price)%></td>
		<td align=center><%=stockBj%></td>		
		<td align=center><%=StringUtil.formatFloat(priceBj)%></td>
		<td align=center><font color="red"><%=(price == 0? "0" : StringUtil.formatFloat(priceBj * 100 / price))%>%</font></td>
		<td align=center><%=stockGd%></td>
		<td align=center><%=StringUtil.formatFloat(priceGd)%></td>
		<td align=center><font color="blue"><%=(price == 0? "0" : StringUtil.formatFloat(priceGd * 100 / price))%>%</font></td>
	</tr>
<%
		}
%>
	<tr bgcolor='#F8F8F8'>
	    <td align=center>总计</td>
		<td align=center><%=totalStock%></td>
		<td align=center><%=StringUtil.formatFloat(totalPrice)%></td>
		<td align=center><%=totalStockBj%></td>		
		<td align=center><%=StringUtil.formatFloat(totalPriceBj)%></td>
		<td align=center><font color="red"><%=(totalPrice == 0? "0" : StringUtil.formatFloat(totalPriceBj * 100 / totalPrice))%>%</font></td>
		<td align=center><%=totalStockGd%></td>
		<td align=center><%=StringUtil.formatFloat(totalPriceGd)%></td>
		<td align=center><font color="blue"><%=(totalPrice == 0? "0" : StringUtil.formatFloat(totalPriceGd * 100 / totalPrice))%>%</font></td>
	</tr>
<%
		rs.close();
	} catch (Exception e) {e.printStackTrace();}
	%>
	
</table>

<%
	st.close();
} catch(Exception e){e.printStackTrace();}
conn.close();
%>

</body>
</html>
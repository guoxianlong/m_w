<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,adultadmin.util.db.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.util.*" %>
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


//	判断是否只显示前100，还是显示所有
	String displayall = request.getParameter("displayall");

	boolean more = (request.getParameter("more")!=null);

    Connection conn = DbUtil.getConnection(DbOperation.DB_SLAVE2);
	Statement st = conn.createStatement();

String curDate = DateUtil.formatDate(new Date());
try{


%>
<html>
<title>买卖宝后台</title>

<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../../header.jsp"%>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center>统计日期</td>
		<td align=center>库存总数</td>
		<td align=center>库存总额</td>
		<td align=center>北京总数</td>		
		<td align=center>北京总额</td>
		<td align=center>资金比例</td>
		<td align=center>广东总数</td>
		<td align=center>广东总额</td>
		<td align=center>资金比例</td>
	</tr>
<%
    try {
	    //当天的
    	int stockBj = 0, stockGd = 0, stock = 0;
		float priceBj = 0, priceGd = 0, price = 0;
		String logDate;

		logDate = curDate;
		String sql = "select sum(ps.stock), sum(ps.stock * p.price3) from product p join product_stock ps on p.id=ps.product_id where p.parent_id1 not in (111,113,119,1385,1425,699,544,545,458,459,340,401,130,123,316,317) and ps.area=0 ";
		ResultSet rs = null;
    	rs = st.executeQuery(sql);
		rs.next();
		stockBj = rs.getInt(1);
		priceBj = rs.getFloat(2);

		sql = "select sum(ps.stock), sum(ps.stock * p.price3) from product p join product_stock ps on p.id=ps.product_id where p.parent_id1 not in (111,113,119,1385,1425,699,544,545,458,459,340,401,130,123,316,317) and ps.area in (1,2,3) ";
    	rs = st.executeQuery(sql);
		rs.next();
		stockGd = rs.getInt(1);
		priceGd = rs.getFloat(2);

		stock = stockBj + stockGd;
		price = priceBj + priceGd;
%>
	<tr bgcolor='#F8F8F8'>
	    <td align=center><a href="productStockDate.jsp?date=<%=logDate%>"><%=logDate%></a></td>
		<td align=center><%=stock%></td>
		<td align=center><%=StringUtil.formatFloat(price)%></td>
		<td align=center><%=stockBj%></td>		
		<td align=center><%=StringUtil.formatFloat(priceBj)%></td>
		<td align=center><font color="red"><%=StringUtil.formatFloat(priceBj * 100 / price)%>%</font></td>
		<td align=center><%=stockGd%></td>
		<td align=center><%=StringUtil.formatFloat(priceGd)%></td>
		<td align=center><font color="blue"><%=StringUtil.formatFloat(priceGd * 100 / price)%>%</font></td>
	</tr>
<%
		rs.close();

		//之前的
		sql = "select sum(psh.stock), sum(psh.stock_gd), sum(psh.stock * (select price3 from product where id = product_id)), sum(psh.stock_gd * (select price3 from product where id = product_id)), log_date from product_stock_history psh join product p on psh.product_id=p.id where p.parent_id1 not in (111,113,119,1385,1425,699,544,545,458,459,340,401,130,123,316,317) group by log_date order by log_date desc";

		if(displayall == null){
			if(more)
				sql += " limit 100";
			else
				sql += " limit 15";
		}

		rs = st.executeQuery(sql);
		while(rs.next()){
			stockBj = rs.getInt(1);
			stockGd = rs.getInt(2);
			stock = stockBj + stockGd;
			priceBj = rs.getFloat(3);
			priceGd = rs.getFloat(4);
			price = priceBj + priceGd;
			logDate = rs.getString(5);
%>
	<tr bgcolor='#F8F8F8'>
	    <td align=center><a href="productStockDate.jsp?date=<%=logDate%>"><%=logDate%></a></td>
		<td align=center><%=stock%></td>
		<td align=center><%=StringUtil.formatFloat(price)%></td>
		<td align=center><%=stockBj%></td>		
		<td align=center><%=StringUtil.formatFloat(priceBj)%></td>
		<td align=center><font color="red"><%=StringUtil.formatFloat(priceBj * 100 / price)%>%</font></td>
		<td align=center><%=stockGd%></td>
		<td align=center><%=StringUtil.formatFloat(priceGd)%></td>
		<td align=center><font color="blue"><%=StringUtil.formatFloat(priceGd * 100 / price)%>%</font></td>
	</tr>
<%
		}
		rs.close();
	} catch (Exception e) {e.printStackTrace();}
	%>
<%if(!more){%><tr><td>
<br/><a href="productStockStat.jsp?more=1">查看更多统计信息</a><br/></td></tr>
<%}%>
	
</table>

<%
	st.close();
} catch(Exception e){e.printStackTrace();}
conn.close();
%>

</body>
</html>
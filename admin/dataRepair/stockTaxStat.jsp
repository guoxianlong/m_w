<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="cache.CatalogCache"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*" %>
<%@page import="java.sql.*,java.util.*,java.text.SimpleDateFormat"%>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
response.setContentType("application/vnd.ms-excel;charset=utf-8");
String fileName = "库存税额_"+DateUtil.getNowDateStr();
response.setHeader("Content-disposition","attachment; filename=\"" + new String(fileName.getBytes("GBK"), "iso8859-1") + ".xls\"");
%>
<html>
<head>
<title></title>
</head>
<body>
<table border="1">
	<tr>
		<td>日期</td>
		<td>产品编号</td>
		<td>产品原名称</td>
		<td>税款金额</td>
		<td>库存结存金额</td>
	</tr>
	<%
Connection conn = adultadmin.util.db.DbUtil.getConnection("adult_slave");
Statement st = conn.createStatement();
Statement st2 = conn.createStatement();
Statement st3 = conn.createStatement();
ResultSet rs = null;
ResultSet rs2 = null;
ResultSet rs3 = null;
try{
	String sql="select id,code,oriname,price5 from product where id order by id asc";
	rs = st.executeQuery(sql);
	while(rs.next()){
		int productId = rs.getInt("id");
		String productCode = rs.getString("code");
		String oriname = rs.getString("oriname");
		float price5 = rs.getFloat("price5");
		float tax = 0;
		float stockSumPrice = 0;
		
		rs2 = st2.executeQuery("select code,price,sum(batch_count) s from stock_batch where product_id = "+productId+" and code like 'R%' group by code");
		int count = 0;
		while(rs2.next()){
			String batchCode = rs2.getString("code");
			float price = rs2.getFloat("price");
			int s = rs2.getInt("s");
			float taxPoint = 0;
			
			rs3 = st3.executeQuery("select bo.tax_point from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id join buy_stockin_product bsip on bsi.id = bsip.buy_stockin_id "+
					               "where bsi.code = '"+batchCode+"' and bsip.product_id = "+productId);
			if(rs3.next()){
				taxPoint = rs3.getFloat("bo.tax_point");
			}
			rs3.close();
			
			if(taxPoint > 0){
				tax = Arith.add(tax,Arith.mul(Arith.div(price,Arith.add(1,taxPoint)),taxPoint));
			}
			count++;
		}
		rs2.close();
		if(count == 0){
			continue;
		}
		
		rs2 = st2.executeQuery("select sum(stock+lock_count) s from product_stock where product_id = "+productId);
		if(rs2.next()){
			stockSumPrice = Arith.mul(price5,rs2.getInt("s"));
		}
		rs2.close();
%>
	<tr>
	<td>2012-09-11</td>
	<td><%=productCode %></td>
	<td><%=oriname %></td>
	<td><%=tax %></td>
	<td><%=stockSumPrice %></td>
	</tr>
<%
	}
	rs.close();
}catch(Exception e){
	e.printStackTrace();
}finally{
	if(rs!=null){
		rs.close();
	}
	if(rs2!=null){
		rs2.close();
	}
	if(rs3!=null){
		rs3.close();
	}
	if(st!=null){
		st.close();
	}
	if(st2!=null){
		st2.close();
	}
	if(st3!=null){
		st3.close();
	}
	if(conn!=null){
		conn.close();
	}
}
%>
</table>
</body>
</html>
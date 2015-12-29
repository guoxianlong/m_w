<%@page import="adultadmin.util.*"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page import="adultadmin.util.db.DbOperation,adultadmin.service.*,adultadmin.service.impl.*,adultadmin.service.infc.*,adultadmin.bean.stock.*,java.util.*"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%
DbOperation dbOp = new DbOperation();
String errorCode = "";
boolean check = true;
IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
try{
dbOp.init("adult_slave");
Connection conn = dbOp.getConn();
Statement stat1 = conn.createStatement();
Statement stat2 = conn.createStatement();
Statement stat3 = conn.createStatement();
ResultSet rs = stat1.executeQuery("select product_id, sum(stock+lock_count) s, type, area from product_stock where type = 4 and area = 3 group by product_id,type,area order by product_id asc");
ResultSet rs2 = null;
ResultSet rs3 = null;
while(rs.next()){
	int productId = rs.getInt("product_id");
	int type = rs.getInt("type");
	int area = rs.getInt("area");
	int count = rs.getInt("s");
	int count2 = 0;
	rs2 = stat2.executeQuery("select sum(batch_count) s from stock_batch where stock_type = "+type+" and stock_area = "+area+" and product_id = "+productId);
	if(rs2.next()){
		count2 = rs2.getInt(1);
	}
	if(count != count2){
%>
<%=productId %>,<%=area %>,<%=type %>异常，库存量<%=count %>,批次量<%=count2 %><br/>
<%
}
%>

<%
}

//检查售后库
rs = stat1.executeQuery("select product_id, sum(stock+lock_count) s, type, area from product_stock where type = 9 and area = 1 group by product_id,type,area order by product_id asc");
while(rs.next()){
	int productId = rs.getInt("product_id");
	int type = rs.getInt("type");
	int area = rs.getInt("area");
	int count = rs.getInt("s");
	int count2 = 0;
	rs2 = stat2.executeQuery("select sum(batch_count) s from stock_batch where stock_type = "+type+" and stock_area = "+area+" and product_id = "+productId);
	if(rs2.next()){
		count2 = rs2.getInt(1);
	}
	if(count != count2){
%>
<%=productId %>,<%=area %>,<%=type %>异常，库存量<%=count %>,批次量<%=count2 %><br/>
<%
}
%>

<%
}

//检查合格库
rs = stat1.executeQuery("select product_id, sum(stock+lock_count) s, type, area from product_stock where type = 0 and area = 3 group by product_id,type,area order by product_id asc");
while(rs.next()){
	int productId = rs.getInt("product_id");
	int type = rs.getInt("type");
	int area = rs.getInt("area");
	int count = rs.getInt("s");
	int count2 = 0;
	rs2 = stat2.executeQuery("select sum(batch_count) s from stock_batch where stock_type = "+type+" and stock_area = "+area+" and product_id = "+productId);
	if(rs2.next()){
		count2 = rs2.getInt(1);
	}
	if(count != count2){
%>
<%=productId %>,<%=area %>,<%=type %>异常，库存量<%=count %>,批次量<%=count2 %><br/>
<%
}
%>

<%
}

//检查待验库
rs = stat1.executeQuery("select product_id, sum(stock+lock_count) s, type, area from product_stock where type = 1 and area = 3 group by product_id,type,area order by product_id asc");
while(rs.next()){
	int productId = rs.getInt("product_id");
	int type = rs.getInt("type");
	int area = rs.getInt("area");
	int count = rs.getInt("s");
	int count2 = 0;
	rs2 = stat2.executeQuery("select sum(batch_count) s from stock_batch where stock_type = "+type+" and stock_area = "+area+" and product_id = "+productId);
	if(rs2.next()){
		count2 = rs2.getInt(1);
	}
	if(count != count2){
%>
<%=productId %>,<%=area %>,<%=type %>异常，库存量<%=count %>,批次量<%=count2 %><br/>
<%
}
%>

<%
}

stat3.close();
stat2.close();
stat1.close();
}catch(Exception e){
	e.printStackTrace();
}finally{
	dbOp.release();
	stockService.releaseAll();
}
%>
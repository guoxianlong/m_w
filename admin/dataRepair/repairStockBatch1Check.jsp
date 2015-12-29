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
ResultSet rs0 = stat3.executeQuery("select id from product order by id asc");
ResultSet rs = null;
ResultSet rs2 = null;
ResultSet rs3 = null;
while(rs0.next()){
	int pid = rs0.getInt("id");
	rs = stat1.executeQuery("select product_id,sum(stock+lock_count) s from product_stock where type not in (7,8) and product_id = "+pid);
while(rs.next()){
	int productId = rs.getInt("product_id");
	int count = rs.getInt("s");
	int count2 = 0;
	rs2 = stat2.executeQuery("select sum(batch_count) s from stock_batch where product_id = "+pid+" group by product_id order by id asc");
	if(rs2.next()){
		count2 = rs2.getInt(1);
	}
	if(count != count2){
%>
<%=productId %>,异常,<%=count %>,<%=count2 %><br/>
<%
}
%>

<%
}
rs.close();
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
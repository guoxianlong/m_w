<%@ include file="../taglibs.jsp"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%@page import="adultadmin.util.DateUtil"%>
<%@page import="java.util.Calendar"%>
<%@page import="adultadmin.util.db.DbUtil"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection,java.io.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="adultadmin.service.IAdminService"%>
<%@page import="adultadmin.service.ServiceFactory"%>
<%@page import="adultadmin.service.infc.ICargoService"%>
<%@page import="adultadmin.service.infc.IBaseService"%>
<%@page import="adultadmin.service.infc.IProductStockService"%>
<%
	if(request.getParameter("code")!=null&&(!request.getParameter("code").equals(""))
		&&request.getParameter("count")!=null&&(!request.getParameter("count").equals(""))){
	String code=request.getParameter("code");//商品编号
	int count=Integer.parseInt(request.getParameter("count"));//订单数量
	
	IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
	ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
	IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
	
	try{
		voProduct product=new voProduct();
		product=adminService.getProduct(code);
		for(int i=0;i<count;i++){
	adminService.getDbOperation().startTransaction();
	String inUserOrder="insert into user_order ";//插入user_order表记录
	adminService.getDbOperation().executeUpdate(inUserOrder);
	String inUserOrderProduct="";//插入user_order_product表记录
	adminService.getDbOperation().executeUpdate(inUserOrderProduct);
	String inOrderStock="";//插入order_stock表记录
	adminService.getDbOperation().executeUpdate(inOrderStock);
	String inOrderStockProduct="";//插入order_stock_product表记录
	adminService.getDbOperation().executeUpdate(inOrderStockProduct);
	
	adminService.getDbOperation().commitTransaction();
		}
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		adminService.close();
	}
}
%>

<%@page import="adultadmin.action.vo.voProduct"%><form action="" method="post">
	产品编号：<input type="text" name="code" /><br/>
	数量：<input type="text" name="count"/><br/>
	<input type="submit" value="提交"/>
</form>
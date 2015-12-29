<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.sql.*,adultadmin.service.*,adultadmin.service.infc.*,adultadmin.action.vo.*"%>
<%@ page contentType="text/html;charset=utf-8"%>
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
货位库存异常产品：<br/>
<%
	IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
	IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
	ResultSet rs = null;
	try{
		
		List list = adminService.getProducts("");
		Iterator iter = list.listIterator();
		while(iter.hasNext()){
	voProduct product = (voProduct)iter.next();
	List psList = service.getProductStockList("product_id = "+product.getId(),-1,-1,"id asc");
	product.setPsList(psList);
	
	int sumStock = 0;
	rs = service.getDbOp().executeQuery("select sum(stock) s from cargo_temp where product_code = '"+(product.getCode().startsWith("0")?product.getCode().substring(1,product.getCode().length()):product.getCode())+"'");
	if(rs.next()){
		sumStock = rs.getInt("s");
	}
	
	if((product.getStock(1,0)+product.getLockCount(1,0))!=sumStock){
%>
<%=product.getCode() %>:总库存量：<%=product.getStock(1,0) %>&nbsp;&nbsp;&nbsp;总冻结库存量：<%=product.getLockCount(1,0) %>&nbsp;&nbsp;&nbsp;货位导入总库存量：<%=sumStock %><br/>
<%				
			}
		}
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		adminService.close();
	}
%>
校验完成！
</body>
</html>
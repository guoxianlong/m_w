<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.sql.*,adultadmin.service.*,adultadmin.service.infc.*,adultadmin.action.vo.*,adultadmin.bean.cargo.*,adultadmin.bean.stock.*"%>
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
<%
	IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
	IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
	ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());
	ResultSet rs = null;
	try{
		
		List list = adminService.getProducts("");
		Iterator iter = list.listIterator();
		while(iter.hasNext()){
	voProduct product = (voProduct)iter.next();
	List psList = service.getProductStockList("product_id = "+product.getId(),-1,-1,"id asc");
	product.setPsList(psList);
	
	Iterator iter2 = psList.listIterator();
	while(iter2.hasNext()){
		ProductStockBean ps = (ProductStockBean)iter2.next();
		if(ps.getType() == 0){
	continue;
		}
		if(ps.getStock()>0){
	CargoInfoBean ci = cargoService.getCargoInfo("area_id = "+ps.getArea()+" and stock_type = "+ps.getType()+" and store_type = 2");
	CargoProductStockBean cps = new CargoProductStockBean();
	if(ci == null){
%>
货位信息缺失：<%=product.getCode() %>&nbsp;&nbsp;&nbsp;地区：<%=ps.getArea() %>&nbsp;&nbsp;&nbsp;库类别：<%=ps.getType() %>
<%
					continue;
					}
					cps.setCargoId(ci.getId());
					cps.setProductId(product.getId());
					cps.setStockCount(ps.getStock());
					cargoService.addCargoProductStock(cps);
					
					cargoService.updateCargoInfo("status = 0","id = "+ci.getId());
				}
%>
<%				
			}
		}
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		adminService.close();
	}
%>
更新完成！
</body>
</html>
<%@page import="java.util.Iterator"%>
<%@page import="java.util.List"%>
<%@page import="java.sql.*,adultadmin.service.*,adultadmin.service.infc.*,adultadmin.action.vo.*,adultadmin.bean.cargo.*,adultadmin.util.*"%>
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
	IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
	ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, null);
	ResultSet rs = null;
	try{
		rs = service.getDbOp().executeQuery("select * from cargo_temp");
		while(rs.next()){
	System.out.println("'"+rs.getString("product_code")+"'");
	if(StringUtil.convertNull(rs.getString("product_code")).equals("")){
		continue;
	}
	voProduct product = adminService.getProduct(rs.getString("product_code").startsWith("0")?rs.getString("product_code").substring(1):rs.getString("product_code"));
	if(product == null){
		continue;
	}
	if(cargoService.getCargoAndProductStock("ci.whole_code = '"+rs.getString("cargo_code")+"' and cps.product_id = "+product.getId())!=null){
%>
异常数据：<%=product.getCode() %>，异常货位：<%=rs.getString("cargo_code") %><br/>
<%				
			}else{
				CargoInfoBean ci = cargoService.getCargoInfo("whole_code = '"+rs.getString("cargo_code")+"'");
				CargoProductStockBean cps = new CargoProductStockBean();
				cps.setCargoId(ci.getId());
				cps.setProductId(product.getId());
				cps.setStockCount(rs.getInt("stock"));
				cargoService.addCargoProductStock(cps);
				
				cargoService.updateCargoInfo("status = 0","id = "+ci.getId());
			}
		}
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		adminService.close();
		service.releaseAll();
		cargoService.releaseAll();
	}
%>
更新完成！
</body>
</html>
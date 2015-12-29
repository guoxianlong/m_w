<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%@page import="com.sun.crypto.provider.HmacMD5"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Date"%>
<%@page import="adultadmin.util.StringUtil"%>
<%@page import="adultadmin.bean.cargo.CargoProductStockBean"%>
<%@page import="java.util.ArrayList"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="java.util.List"%>
<%@page import="adultadmin.service.infc.ICargoService"%>
<%@page import="adultadmin.service.infc.IProductStockService"%>
<%@page import="adultadmin.util.db.DbOperation"%>
<%@page import="adultadmin.service.infc.IBaseService"%>
<%@page import="adultadmin.service.ServiceFactory"%>
<%@page import="adultadmin.service.IAdminService"%>
<%@page import="adultadmin.service.infc.IStockService"%>
<%@page import="adultadmin.bean.UserGroupBean"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.util.db.*"%>
<%@page import="java.sql.*"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%
	long beginTime = new Date().getTime();
voUser adminUser = (voUser)session.getAttribute("userView");
UserGroupBean group = adminUser.getGroup();
DbOperation dbOperation = new DbOperation(true);
dbOperation.init("adult_slave2");
IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOperation);
IProductStockService stockService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOperation);
IAdminService adminService = ServiceFactory.createAdminService(dbOperation);
ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOperation);

int count = stockService.getProductStockCount(null); //总库库存记录条数
List checkProductList = new ArrayList(); //存放查询对比后的所有记录 
HashMap productStockMap = new HashMap(); // 存放每件货物的总库库存总量
try{
	// 将查询出来的记录分批放入productStockList，每批10万条
	for(int i = 0; i < (count/100000)+1 ; i++){
		List productStockList = stockService.getProductStockList(null,i * 100000,100000,"product_id desc"); 
		for(int j = 0; j < productStockList.size(); j++){
	ProductStockBean psb = (ProductStockBean)(productStockList.get(j));
	int productId = psb.getProductId(); // 产品编号，临时
	int stock = psb.getStock(); // 总库库存 ，临时
	int lockCount = psb.getLockCount(); //总库冻结量，临时
	int allStock = stock + lockCount; 
	if(productStockMap.containsKey(productId + "")){ // 判断map里是否存在此编号 
		int temp = Integer.parseInt((String)productStockMap.get(productId + "")) + allStock;
		productStockMap.put(productId + "",temp + "");
	}else{
		productStockMap.put(productId + "",allStock + "");
	}
		}
	}
	
	// 遍历productStockMap将比对记录组成数组checkProductArr，放入checkProductList
	Iterator it = productStockMap.entrySet().iterator();
	while(it.hasNext()){
		Map.Entry entry = (Map.Entry)it.next();
		int productId = Integer.parseInt((String)entry.getKey()); // 产品编号
		int allStock = Integer.parseInt((String)entry.getValue()); // 总库库存
		String productName = "<font color=\"red\">产品名称未登记</font>"; // 产品原名称
		if(adminService.getProduct(productId) != null){
	 productName = adminService.getProduct(productId).getName(); 
		}
		int stockCount = 0 ; // 货位库存
		int stockLockCount = 0; // 货位库存冻结量
		List cargoProductStockList = cargoService.getCargoProductStockList("product_id=" + productId,0,-1,"id asc");
		for(int k = 0; k < cargoProductStockList.size(); k++){
	CargoProductStockBean cpsb = (CargoProductStockBean)(cargoProductStockList.get(k));
	stockCount += cpsb.getStockCount();
	stockLockCount += cpsb.getStockLockCount();
		} 
		int allCargoStock = stockCount + stockLockCount; //货位总库存
		int difStock = allStock - allCargoStock; // 总库存-货位库存的差值
		
		String[] checkProductArr = new String[5];
		checkProductArr[0] = productId + "";
		checkProductArr[1] = productName;
		checkProductArr[2] = allStock + "";
		checkProductArr[3] = allCargoStock + "";
		checkProductArr[4] = difStock + "";
		if(difStock != 0){ //  误差为0的不存，测试时使用2200
	checkProductList.add(checkProductArr);
		}
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
	dbOperation.release();
}
long endTime = new Date().getTime();
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>库存校验</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
</head>
<body>
&nbsp;&nbsp;&nbsp;库存校验 - 共耗时<%=endTime-beginTime %>毫秒 
<table cellpadding="3" border=1 style="border-collapse: collapse;"
	bordercolor="#D8D8D5" align="center" width="95%">
	<tr bgcolor="#4688D6">
		<td align="center"><font color="#FFFFFF">序号</font></td>
		<td align="center"><font color="#FFFFFF">产品编号</font></td>
		<td align="center"><font color="#FFFFFF">产品原名称</font></td>
		<td align="center"><font color="#FFFFFF">总库存(含冻结量)</font></td>
		<td align="center"><font color="#FFFFFF">货位库存(含冻结量)</font></td>
		<td align="center"><font color="#FFFFFF">误差(总库存-货位库存)</font></td>
	</tr>
	<%
	int j = 1;
	for(int i = 0; i < checkProductList.size(); i++){ 
		String[] str = (String[])checkProductList.get(i);
	%>
		<tr <%if(j%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
			<td align="center"><%=j++%></td>
			<td align="center"><%=str[0]%></td>
			<td align="center"><%=str[1]%></td>
			<td align="center"><%=str[2]%></td>
			<td align="center"><%=str[3]%></td>
			<td align="center"><%=str[4]%></td>
		</tr>
	<%} %>
</table>
</body>
</html>
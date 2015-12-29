<%@page contentType="text/html;charset=utf-8" %>
<%@page import="adultadmin.service.infc.*"%>
<%@page import="adultadmin.service.ServiceFactory"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="adultadmin.util.db.DbOperation"%>
<%
	IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
	DbOperation dbOp = new DbOperation();
	dbOp.init();
	dbOp.prepareStatement("select id from product_stock where product_id=? and area=? and `type`=?");
	PreparedStatement prst = dbOp.getPStmt();
	ResultSet rs = dbOp.executeQuery("select product_id from product_stock where id>0 group by product_id");
	while(rs.next()){
		int prodId = rs.getInt(1);
		//String query = "select id from product_stock where product_id="+prodId+" and area = "+ProductStockBean.AREA_GF +" and `type` ="+ ProductStockBean.STOCKTYPE_QUALITYTESTING ;
		//System.out.println(query);
		//ResultSet rs2 = psService.getDbOp().executeQuery(query);
		prst.setInt(1, prodId);
		prst.setInt(2, ProductStockBean.AREA_GF);
		prst.setInt(3, ProductStockBean.STOCKTYPE_QUALITYTESTING);
		ResultSet rs2 = prst.executeQuery();
		if(!rs2.next()){
			ProductStockBean ps = new ProductStockBean();
			ps.setStock(0);
	        ps.setLockCount(0);
	        ps.setProductId(prodId);
	        ps.setArea(ProductStockBean.AREA_GF);
	        ps.setType(ProductStockBean.STOCKTYPE_QUALITYTESTING);
	        ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			psService.addProductStock(ps);
			System.out.println("Product_Id:"+prodId+" area:广分 type:质检库 add OK");
		}else{
			System.out.println("Product_Id:"+prodId+" area:广分 type:质检库 is exist");
		}
		rs2.close();
		
		//query = "select id from product_stock where product_id="+prodId+" and area = "+ProductStockBean.AREA_GF +" and `type` ="+ ProductStockBean.STOCKTYPE_NIFFER;
		//rs2 = psService.getDbOp().executeQuery(query);
		prst.setInt(3, ProductStockBean.STOCKTYPE_NIFFER);
		rs2 = prst.executeQuery();
		if(!rs2.next()){
			ProductStockBean ps = new ProductStockBean();
			ps.setStock(0);
	        ps.setLockCount(0);
	        ps.setProductId(prodId);
	        ps.setArea(ProductStockBean.AREA_GF);
	        ps.setType(ProductStockBean.STOCKTYPE_NIFFER);
	        ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			psService.addProductStock(ps);
			System.out.println("Product_Id:"+prodId+" area:广分 type:换货库 add OK");
		}else{
			System.out.println("Product_Id:"+prodId+" area:广分 type:换货库 is exist");
		}
		rs2.close();
		
		//query = "select id from product_stock where product_id="+prodId+" and area = "+ProductStockBean.AREA_GS +" and `type` ="+ ProductStockBean.STOCKTYPE_NIFFER;
		//rs2 = psService.getDbOp().executeQuery(query);
		prst.setInt(2,ProductStockBean.AREA_GS);
		rs2 = prst.executeQuery();
		if(!rs2.next()){
			ProductStockBean ps = new ProductStockBean();
			ps.setStock(0);
	        ps.setLockCount(0);
	        ps.setProductId(prodId);
	        ps.setArea(ProductStockBean.AREA_GS);
	        ps.setType(ProductStockBean.STOCKTYPE_NIFFER);
	        ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			psService.addProductStock(ps);
			System.out.println("Product_Id:"+prodId+" area:广速 type:换货库 add OK");
		}else{
			System.out.println("Product_Id:"+prodId+" area:广速 type:换货库 is exist");
		}
		rs2.close();
		
		
		//query ="select id from product_stock where product_id="+prodId+" and area = "+ProductStockBean.AREA_GS +" and `type` ="+ ProductStockBean.STOCKTYPE_QUALITYTESTING;
		//rs2 = psService.getDbOp().executeQuery(query);
		prst.setInt(3,ProductStockBean.STOCKTYPE_QUALITYTESTING);
		rs2 = prst.executeQuery();
		if(!rs2.next()){
			ProductStockBean ps = new ProductStockBean();
			ps.setStock(0);
	        ps.setLockCount(0);
	        ps.setProductId(prodId);
	        ps.setArea(ProductStockBean.AREA_GS);
	        ps.setType(ProductStockBean.STOCKTYPE_QUALITYTESTING);
	        ps.setStatus(ProductStockBean.STOCKSTATUS_NORMAL);
			psService.addProductStock(ps);
			System.out.println("Product_Id:"+prodId+" area:广速 type:质检库 add OK");
		}else{
			System.out.println("Product_Id:"+prodId+" area:广速 type:质检库 is exist");
		}
		rs2.close();
	}
	System.out.println("all clean");
%>
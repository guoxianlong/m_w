<%@page import="adultadmin.util.Arith"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page import="adultadmin.util.db.DbOperation"%>
<%@page import="java.util.*,java.io.*"%>
<%@page import="adultadmin.util.StringUtil"%>
<%@page import="adultadmin.bean.buy.*,adultadmin.service.*,adultadmin.service.infc.*"%><%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<html>
<title>买卖宝后台</title>
<script language="JavaScript" src="js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<logic:notEmpty name="tip">
<script language="JavaScript">
alert('<bean:write name="tip" />');
</script>
</logic:notEmpty>
<body>
<%@include file="../../header.jsp"%>
<%
	DbOperation dbOp = new DbOperation();
	DbOperation dbOpSlave = new DbOperation();
	dbOp.init("adult");
	dbOpSlave.init("adult_slave");
	Connection conn = dbOp.getConn();
	Connection connSlave = dbOpSlave.getConn();
	Statement stat1 = connSlave.createStatement();
	Statement stat2 = connSlave.createStatement();
	Statement stat3 = connSlave.createStatement();
	Statement stat0 = conn.createStatement();
	ResultSet rs = null;
	ResultSet rs2 = null;
	ResultSet rs3 = null;
	try{
		rs = stat1.executeQuery("select * from returned_package where storage_status <> 1 and id > 2226 and id <= 11759 order by id asc");
		while(rs.next()){
			String orderCode = rs.getString("order_code");
			String dateTime = rs.getString("storage_time");
			
			//检查退货入库进销存记录是否完整
			boolean check = false;
			rs2 = stat2.executeQuery("select id from stock_card where code = '"+orderCode+"' and card_type = 5");
			if(!rs2.next()){//无进销存记录
				check = true;
			}
			rs2.close();
			
			if(check){
				
				//获取订单发货商品列表
				rs2 = stat2.executeQuery("select osp.* from order_stock os join order_stock_product osp on os.id = osp.order_stock_id where os.order_code = '"+orderCode+"' and os.status = 7");
				while(rs2.next()){
					int productId = rs2.getInt("osp.product_id");
					int count = rs2.getInt("osp.stockout_count");
					
					int stockType = 4;
					int stockArea = 3;
					int cardType = 5;
					int stockAllType = 0;
					int stockAllArea = 0;
					int currentStock = 0;
					int allStock = 0;
					float stockPrice = 0;
					
					
					
					//获取退货前最后一条记录
					//取得库存单价、总库存、区域库存
					rs3 = stat3.executeQuery("select * from stock_card where product_id = "+productId+" and create_datetime <= '"+dateTime+"' order by id desc limit 1");
					if(rs3.next()){
						stockPrice = rs3.getFloat("stock_price");
						allStock = rs3.getInt("all_stock") + count;
						stockAllArea = rs3.getInt("stock_all_area") + count;
					}
					rs3.close();
					
					//获取退货前最后一条退货库记录
					//取得当前库存、本类库库存
					rs3 = stat3.executeQuery("select * from stock_card where product_id = "+productId+" and create_datetime <= '"+dateTime+"' and stock_type = 4 and stock_area = 3 order by id desc limit 1");
					if(rs3.next()){
						currentStock = rs3.getInt("current_stock") + count;
						stockAllType = rs3.getInt("stock_all_type") + count;
					}
					rs3.close();
					
					//插入进销存
					stat0.executeUpdate("insert into stock_card(stock_type, stock_area, stock_id, code, card_type, create_datetime, stock_in_count, stock_in_price_sum, stock_out_count, stock_out_price_sum, stock_all_type, stock_all_area, current_stock, all_stock, stock_price, all_stock_price_sum, product_id) "+
							"values("+stockType+","+stockArea+",0,'"+orderCode+"',5,'"+dateTime+"',"+count+","+Arith.mul(count,stockPrice)+",0,0,"+stockAllType+","+stockAllArea+","+currentStock+","+allStock+","+stockPrice+","+Arith.mul(allStock,stockPrice)+","+productId+")");
					
				}
				rs2.close();
			}
		}
		rs.close();
		stat3.close();
		stat2.close();
		stat1.close();
		stat0.close();
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		dbOp.release();
		dbOpSlave.release();
	}
 %>
处理完毕
</body>
</html> 
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
ResultSet rs = stat1.executeQuery("select id from product where id in (12293,21206,25582,26018,32233,34379,39450,39911,41315,45332,47744,48739,55084,60135,61718,62516,62517,63139,63494,64294,65902,65906,65909,65910,65911,65913,65915,65916,65917,65919,65922,66108,67384,67401,67411,67412,67998,69181,69182,69185,69186,69187,69190,69197,69198,69203,69219,69222,69223,69224,69643,71171,71755,74151,74152,74154,74157,74378,79124,80774,81179,81181,81182,81184,81189,82059,82060,83102,83403,84797,85562,85563,85564,85565,85570,85571,85572,85573,85575,85576,85577,85578,85579,85580,85581,86422,86423,86424,86425,87008,87082,87083,87084,87085,87086,87087,87088,87089,87090,87091,87956,88115,88327,88328,88329,89237,89463,89465,89467,89473,89474,89475,89476,89477,89761,89762,89763,89764,89765,89781,89951,89990,89992,89993) order by id asc");
ResultSet rs2 = null;
ResultSet rs3 = null;
while(rs.next()){
	int productId = rs.getInt(1);
	int stockCount = 0;
	int psId = 0;
	
	//判断是否有库存，没有库存跳过
	rs2 = stat2.executeQuery("select sum(stock+lock_count) s from product_stock where type not in (7,8) and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
	}
	rs2.close();
	
%>
<%=productId %>,<%=stockCount %><br/>
<%
	
	if(stockCount == 0){
		continue;
	}

	String markTime = DateUtil.getNow();
	int mark = 1;
	int remainCount = 0;
	
	//合格库，增城
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 0 and area = 3 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",3,0,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",3,0,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
				markTime = time;
			}
		}else{
			break;
		}
	}
	if(stockCount != 0){
		remainCount = Math.abs(stockCount);
	}
	
	
	//合格库，芳村
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 0 and area = 1 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	mark = 1;
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		if(remainCount > 0){
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime <= '"+markTime+"'");
		}else{
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		}
		mark++;
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();
				
				if(remainCount > 0){
					batchCount = remainCount;
				}
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",1,0,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",1,0,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
				remainCount = 0;
				markTime = time;
			}
		}else{
			break;
		}
	}
	if(stockCount != 0){
		remainCount = Math.abs(stockCount);
	}
	
	//待验库，增城
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 1 and area = 3 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	mark = 1;
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		if(remainCount > 0){
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime <= '"+markTime+"'");
		}else{
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		}
		mark++;
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();

				if(remainCount > 0){
					batchCount = remainCount;
				}
				
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",3,1,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",3,1,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
				remainCount = 0;
				markTime = time;
			}
		}else{
			break;
		}
	}
	if(stockCount != 0){
		remainCount = Math.abs(stockCount);
	}
	
	//待验库，芳村
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 1 and area = 1 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	mark = 1;
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		if(remainCount > 0){
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime <= '"+markTime+"'");
		}else{
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		}
		mark++;
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();

				if(remainCount > 0){
					batchCount = remainCount;
				}
				
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",1,1,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",1,1,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
				remainCount = 0;
				markTime = time;
			}
		}else{
			break;
		}
	}
	if(stockCount != 0){
		remainCount = Math.abs(stockCount);
	}
	
	//退货库，增城
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 4 and area = 3 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	mark = 1;
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		if(remainCount > 0){
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime <= '"+markTime+"'");
		}else{
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		}
		mark++;
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();

				if(remainCount > 0){
					batchCount = remainCount;
				}
				
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",3,4,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",3,4,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
				remainCount = 0;
				markTime = time;
			}
		}else{
			break;
		}
	}
	if(stockCount != 0){
		remainCount = Math.abs(stockCount);
	}
	
	//退货库，芳村
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 4 and area = 1 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	mark = 1;
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		if(remainCount > 0){
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime <= '"+markTime+"'");
		}else{
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		}
		mark++;
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();

				if(remainCount > 0){
					batchCount = remainCount;
				}
				
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",1,4,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",1,4,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
				remainCount = 0;
				markTime = time;
			}
		}else{
			break;
		}
	}
	if(stockCount != 0){
		remainCount = Math.abs(stockCount);
	}
	
	//样品库，增城
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 6 and area = 3 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	mark = 1;
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		if(remainCount > 0){
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime <= '"+markTime+"'");
		}else{
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		}
		mark++;
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();

				if(remainCount > 0){
					batchCount = remainCount;
				}
				
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",3,6,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",3,6,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
				remainCount = 0;
				markTime = time;
			}
		}else{
			break;
		}
	}
	if(stockCount != 0){
		remainCount = Math.abs(stockCount);
	}
	
	//样品库，芳村
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 6 and area = 1 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	mark = 1;
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		if(remainCount > 0){
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime <= '"+markTime+"'");
		}else{
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		}
		mark++;
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();

				if(remainCount > 0){
					batchCount = remainCount;
				}
				
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",1,6,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",1,6,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
				remainCount = 0;
				markTime = time;
			}
		}else{
			break;
		}
	}
	if(stockCount != 0){
		remainCount = Math.abs(stockCount);
	}
	
	//样品库，北京
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 6 and area = 0 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	mark = 1;
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		if(remainCount > 0){
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime <= '"+markTime+"'");
		}else{
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		}
		mark++;
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();

				if(remainCount > 0){
					batchCount = remainCount;
				}
				
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",0,6,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",0,6,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
				remainCount = 0;
				markTime = time;
			}
		}else{
			break;
		}
	}
	if(stockCount != 0){
		remainCount = Math.abs(stockCount);
	}
	
	//售后库，芳村
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 9 and area = 1 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	mark = 1;
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		if(remainCount > 0){
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime <= '"+markTime+"'");
		}else{
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		}
		mark++;
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();

				if(remainCount > 0){
					batchCount = remainCount;
				}
				
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",1,9,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",1,9,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
				remainCount = 0;
				markTime = time;
			}
		}else{
			break;
		}
	}
	if(stockCount != 0){
		remainCount = Math.abs(stockCount);
	}
	
	//维修库，增城
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 2 and area = 3 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	mark = 1;
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		if(remainCount > 0){
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime <= '"+markTime+"'");
		}else{
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		}
		mark++;
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();

				if(remainCount > 0){
					batchCount = remainCount;
				}
				
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",3,2,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",3,2,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
				remainCount = 0;
				markTime = time;
			}
		}else{
			break;
		}
	}
	if(stockCount != 0){
		remainCount = Math.abs(stockCount);
	}
	
	//维修库，北京
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 2 and area = 0 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	mark = 1;
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		if(remainCount > 0){
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime <= '"+markTime+"'");
		}else{
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		}
		mark++;
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();

				if(remainCount > 0){
					batchCount = remainCount;
				}
				
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",0,2,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",0,2,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
				remainCount = 0;
				markTime = time;
			}
		}else{
			break;
		}
	}
	if(stockCount != 0){
		remainCount = Math.abs(stockCount);
	}
	
	//返厂库，北京
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 3 and area = 0 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	mark = 1;
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		if(remainCount > 0){
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime <= '"+markTime+"'");
		}else{
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		}
		mark++;
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();

				if(remainCount > 0){
					batchCount = remainCount;
				}
				
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",0,3,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",0,3,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
				remainCount = 0;
				markTime = time;
			}
		}else{
			break;
		}
	}
	if(stockCount != 0){
		remainCount = Math.abs(stockCount);
	}
	
	//残次品库，增城
	stockCount = 0;
	rs2 = stat2.executeQuery("select sum(stock+lock_count), id from product_stock where type = 5 and area = 3 and product_id = "+productId);
	if(rs2.next()){
		stockCount = rs2.getInt(1);
		psId = rs2.getInt(2);
	}
	rs2.close();
	
	mark = 1;
	//查找入库单记录，重设批次数据
	while(stockCount>0){
		int scId = 0;
		if(remainCount > 0){
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime <= '"+markTime+"'");
		}else{
			rs2 = stat2.executeQuery("select max(id) from stock_card where card_type in (1,8,0) and product_id = "+productId+" and create_datetime < '"+markTime+"'");
		}
		mark++;
		if(rs2.next()){
			scId = rs2.getInt(1);
		}
		rs2.close();
		
		if(scId > 0){
			rs2 = stat2.executeQuery("select code, stock_in_count, create_datetime,stock_in_price_sum from stock_card where id = "+scId);
			if(rs2.next()){
				int batchCount = rs2.getInt(2);
				String code = rs2.getString(1);
				String time = rs2.getString(3);
				float sumprice = rs2.getFloat(4);
				int ticket = 0;
				float batchPrice = Arith.div(sumprice,batchCount);
				markTime = time;
				
				//查询采购退货量
				rs3 = stat3.executeQuery("select sum(batch_count) from stock_batch_log where batch_code = '"+code+"' and product_id = "+productId+" and remark = '采购退货出库'");
				if(rs3.next()){
					batchCount = batchCount - rs3.getInt(1);
				}
				rs3.close();

				if(remainCount > 0){
					batchCount = remainCount;
				}
				
				
				//批次价格、有票无票
				if(code.startsWith("R")){
					rs3 = stat3.executeQuery("select bo.ticket from buy_order bo join buy_stock bs on bo.id = bs.buy_order_id join buy_stockin bsi on bs.id = bsi.buy_stock_id where bsi.code = '"+code+"'");
					if(rs3.next()){
						ticket = rs3.getInt(1); 
					}
				}
				
				if(batchCount >= stockCount){
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+stockCount+",3,5,"+psId+",'"+time+"',"+ticket+")");
				}else{
					stockService.getDbOp().executeUpdate(
							"insert into stock_batch_temp2(code,product_id,price,batch_count,stock_area,stock_type,product_stock_id,create_datetime,ticket) values('"+code+"',"+productId+","+batchPrice+","+batchCount+",3,5,"+psId+",'"+time+"',"+ticket+")");
				}
				
				
				stockCount = stockCount - batchCount;
			}else{
				break;
			}
		}else{
			break;
		}
	}
	
	//处理有票无票均价、库存结存金额
	float price = 0;
	float sumprice = 0;
	rs2 = stat2.executeQuery("select sum(batch_count) s, sum(batch_count*price) s2 from stock_batch_temp2 where ticket = 0 and product_id = "+productId); //有票
	if(rs2.next()){
		int counts = rs2.getInt(1);
		sumprice = rs2.getFloat(2);
		price = Arith.div(sumprice,counts);
	}
	rs2.close();
	if(price > 0){
		stockService.getDbOp().executeUpdate("update finance_product set price_hasticket = "+price+", price_sum_hasticket = "+sumprice+" where product_id="+productId);
	}
	
	price = 0;
	sumprice = 0;
	rs2 = stat2.executeQuery("select sum(batch_count) s, sum(batch_count*price) s2 from stock_batch_temp2 where ticket = 1 and product_id = "+productId); //无票
	if(rs2.next()){
		int counts = rs2.getInt(1);
		sumprice = rs2.getFloat(2);
		price = Arith.div(sumprice,counts);
	}
	rs2.close();
	if(price > 0){
		stockService.getDbOp().executeUpdate("update finance_product set price_noticket = "+price+", price_sum_noticket = "+sumprice+" where product_id="+productId);
	}
	
%>
已处理<%=productId %><br/>
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
System.out.println("repair stock_batch success  "+DateUtil.getNow());
%>
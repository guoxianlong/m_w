<%@page import="adultadmin.util.db.DbOperation"%>
<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,java.sql.*,java.util.*" %>
<html>
<title>买卖宝后台</title>
<head>
<meta http-equiv=refresh content="5">
</head>
<body>
<%
	// _1 实际非重单
	// _2 实际成交单
	// _3 搭销品单
	// _4 搭销品金额
	// _5 成交额
	// _6 积压订单量
	// _7 预测量
	
	DbOperation dbOp = new DbOperation();
	dbOp.init(DbOperation.DB_SLAVE2);

	Calendar cal = Calendar.getInstance();
	String startTime = DateUtil.formatDate(cal.getTime());
	int hour = cal.get(Calendar.HOUR_OF_DAY);
	try{
		
		//非重单，成交订单
		HashMap<String, Integer> yunyingOrderMap = new HashMap<String, Integer>();
		HashMap<String, Float> yunyingOrderMap2 = new HashMap<String, Float>();
		HashMap<String, Integer> wareOrderMap = new HashMap<String, Integer>();
		ResultSet rs1 = dbOp.executeQuery("select order_type, status, count(id), flat from user_order where create_datetime >= '"+startTime+"' group by order_type, status, flat ");
		while(rs1.next()){
			int orderType = rs1.getInt(1);
			int status = rs1.getInt(2);
			int count = rs1.getInt(3);
			int flat = rs1.getInt(4);

			if(orderType == 1 || orderType == 2 || orderType == 5 || orderType == 10){   //3C				
				if (flat ==  2) { // 大Q官网
					if(yunyingOrderMap.containsKey("daq_1")){
						if(status != 10){
							yunyingOrderMap.put("daq_1", yunyingOrderMap.get("daq_1")+count);
						}
					}else{
						if(status != 10){
							yunyingOrderMap.put("daq_1", count);
						}
					}
				
					if(yunyingOrderMap.containsKey("daq_2")){
						if(status == 3 || status == 6 || status == 11 || status == 14){
							yunyingOrderMap.put("daq_2", yunyingOrderMap.get("daq_2")+count);
						}
					}else{
						if(status == 3 || status == 6 || status == 11 || status == 14){
							yunyingOrderMap.put("daq_2", count);
						}
					}
				} else {
					if(yunyingOrderMap.containsKey("3C_1")){
						if(status != 10){
							yunyingOrderMap.put("3C_1", yunyingOrderMap.get("3C_1")+count);
						}
					}else{
						if(status != 10){
							yunyingOrderMap.put("3C_1", count);
						}
					}
				
					if(yunyingOrderMap.containsKey("3C_2")){
						if(status == 3 || status == 6 || status == 11 || status == 14){
							yunyingOrderMap.put("3C_2", yunyingOrderMap.get("3C_2")+count);
						}
					}else{
						if(status == 3 || status == 6 || status == 11 || status == 14){
							yunyingOrderMap.put("3C_2", count);
						}
					}
				}
			}
			
			if(orderType == 4){   //服装
				if(yunyingOrderMap.containsKey("fuzhuang_1")){
					if(status != 10){
						yunyingOrderMap.put("fuzhuang_1", yunyingOrderMap.get("fuzhuang_1")+count);
					}
				}else{
					if(status != 10){
						yunyingOrderMap.put("fuzhuang_1", count);
					}
				}
			
				if(yunyingOrderMap.containsKey("fuzhuang_2")){
					if(status == 3 || status == 6 || status == 11 || status == 14){
						yunyingOrderMap.put("fuzhuang_2", yunyingOrderMap.get("fuzhuang_2")+count);
					}
				}else{
					if(status == 3 || status == 6 || status == 11 || status == 14){
						yunyingOrderMap.put("fuzhuang_2", count);
					}
				}
			}
			
			if(orderType == 6 || orderType == 12 || orderType == 14 || orderType == 18){   //鞋子
				if(yunyingOrderMap.containsKey("xie_1")){
					if(status != 10){
						yunyingOrderMap.put("xie_1", yunyingOrderMap.get("xie_1")+count);
					}
				}else{
					if(status != 10){
						yunyingOrderMap.put("xie_1", count);
					}
				}
			
				if(yunyingOrderMap.containsKey("xie_2")){
					if(status == 3 || status == 6 || status == 11 || status == 14){
						yunyingOrderMap.put("xie_2", yunyingOrderMap.get("xie_2")+count);
					}
				}else{
					if(status == 3 || status == 6 || status == 11 || status == 14){
						yunyingOrderMap.put("xie_2", count);
					}
				}
			}
			
			if(orderType == 3 || orderType == 19 || orderType == 20 ){   //内衣
				if(yunyingOrderMap.containsKey("neiyi_1")){
					if(status != 10){
						yunyingOrderMap.put("neiyi_1", yunyingOrderMap.get("neiyi_1")+count);
					}
				}else{
					if(status != 10){
						yunyingOrderMap.put("neiyi_1", count);
					}
				}
			
				if(yunyingOrderMap.containsKey("neiyi_2")){
					if(status == 3 || status == 6 || status == 11 || status == 14){
						yunyingOrderMap.put("neiyi_2", yunyingOrderMap.get("neiyi_2")+count);
					}
				}else{
					if(status == 3 || status == 6 || status == 11 || status == 14){
						yunyingOrderMap.put("neiyi_2", count);
					}
				}
			}
			
			if(orderType == 7 || orderType == 8 || orderType == 9 || orderType == 11 || orderType == 16){   //护肤
				if(yunyingOrderMap.containsKey("hufu_1")){
					if(status != 10){
						yunyingOrderMap.put("hufu_1", yunyingOrderMap.get("hufu_1")+count);
					}
				}else{
					if(status != 10){
						yunyingOrderMap.put("hufu_1", count);
					}
				}
			
				if(yunyingOrderMap.containsKey("hufu_2")){
					if(status == 3 || status == 6 || status == 11 || status == 14){
						yunyingOrderMap.put("hufu_2", yunyingOrderMap.get("hufu_2")+count);
					}
				}else{
					if(status == 3 || status == 6 || status == 11 || status == 14){
						yunyingOrderMap.put("hufu_2", count);
					}
				}
			}
			
			if(orderType == 17){   //成人
				if(yunyingOrderMap.containsKey("chengren_1")){
					if(status != 10){
						yunyingOrderMap.put("chengren_1", yunyingOrderMap.get("chengren_1")+count);
					}
				}else{
					if(status != 10){
						yunyingOrderMap.put("chengren_1", count);
					}
				}
			
				if(yunyingOrderMap.containsKey("chengren_2")){
					if(status == 3 || status == 6 || status == 11 || status == 14){
						yunyingOrderMap.put("chengren_2", yunyingOrderMap.get("chengren_2")+count);
					}
				}else{
					if(status == 3 || status == 6 || status == 11 || status == 14){
						yunyingOrderMap.put("chengren_2", count);
					}
				}
			}
			
			if(yunyingOrderMap.containsKey("total_1")){
				if(status != 10){
					yunyingOrderMap.put("total_1", yunyingOrderMap.get("total_1")+count);
				}
			}else{
				if(status != 10){
					yunyingOrderMap.put("total_1", count);
				}
			}
			
			if(yunyingOrderMap.containsKey("total_2")){
				if(status == 3 || status == 6 || status == 11 || status == 14){
					yunyingOrderMap.put("total_2", yunyingOrderMap.get("total_2")+count);
				}
			}else{
				if(status == 3 || status == 6 || status == 11 || status == 14){
					yunyingOrderMap.put("total_2", count);
				}
			}
		}
		rs1.close();
		
		//待发货订单
		rs1 = dbOp.executeQuery("select uo.order_type, count(uo.id),os.stock_area from order_stock os join user_order uo on os.order_id = uo.id where os.status in (1,5) group by uo.order_type, os.stock_area");
		while(rs1.next()){
			int orderType = rs1.getInt(1);
			int count = rs1.getInt(2);
			int stockArea = rs1.getInt(3);
			
			//ware
			if(stockArea == 0){
				if(wareOrderMap.containsKey("bj_1")){
					wareOrderMap.put("bj_1", wareOrderMap.get("bj_1")+count);
				}else{
					wareOrderMap.put("bj_1", count);
				}
			}
			
			if(stockArea == 3){
				if(wareOrderMap.containsKey("zc_1")){
					wareOrderMap.put("zc_1", wareOrderMap.get("zc_1")+count);
				}else{
					wareOrderMap.put("zc_1", count);
				}
			}
			
			if(stockArea == 4){
				if(wareOrderMap.containsKey("wx_1")){
					wareOrderMap.put("wx_1", wareOrderMap.get("wx_1")+count);
				}else{
					wareOrderMap.put("wx_1", count);
				}
			}
			
			if(stockArea == 8){
				if(wareOrderMap.containsKey("xa_1")){
					wareOrderMap.put("xa_1", wareOrderMap.get("xa_1")+count);
				}else{
					wareOrderMap.put("xa_1", count);
				}
			}
			
			if(stockArea == 9){
				if(wareOrderMap.containsKey("cd_1")){
					wareOrderMap.put("cd_1", wareOrderMap.get("cd_1")+count);
				}else{
					wareOrderMap.put("cd_1", count);
				}
			}
			
			if(wareOrderMap.containsKey("total_1")){
				wareOrderMap.put("total_1", wareOrderMap.get("total_1")+count);
			}else{
				wareOrderMap.put("total_1", count);
			}
		}
		rs1.close();

		
		//已发货订单
		rs1 = dbOp.executeQuery("select uo.order_type, count(uo.id), os.stock_area from mailing_balance mb join user_order uo  on mb.order_id = uo.id join order_stock os on mb.order_id = os.order_id where stockout_datetime >= '"+startTime+"' and os.status <> 3 group by uo.order_type, os.stock_area");
		while(rs1.next()){
			int orderType = rs1.getInt(1);
			int count = rs1.getInt(2);
			int stockArea = rs1.getInt(3);
			
			//ware
			if(stockArea == 0){
				if(wareOrderMap.containsKey("bj_2")){
					wareOrderMap.put("bj_2", wareOrderMap.get("bj_2")+count);
				}else{
					wareOrderMap.put("bj_2", count);
				}
			}
			
			if(stockArea == 3){
				if(wareOrderMap.containsKey("zc_2")){
					wareOrderMap.put("zc_2", wareOrderMap.get("zc_2")+count);
				}else{
					wareOrderMap.put("zc_2", count);
				}
			}
			
			if(stockArea == 4){
				if(wareOrderMap.containsKey("wx_2")){
					wareOrderMap.put("wx_2", wareOrderMap.get("wx_2")+count);
				}else{
					wareOrderMap.put("wx_2", count);
				}
			}
			
			if(stockArea == 8){
				if(wareOrderMap.containsKey("xa_2")){
					wareOrderMap.put("xa_2", wareOrderMap.get("xa_2")+count);
				}else{
					wareOrderMap.put("xa_2", count);
				}
			}
			
			if(stockArea == 9){
				if(wareOrderMap.containsKey("cd_2")){
					wareOrderMap.put("cd_2", wareOrderMap.get("cd_2")+count);
				}else{
					wareOrderMap.put("cd_2", count);
				}
			}
			
			if(wareOrderMap.containsKey("total_2")){
				wareOrderMap.put("total_2", wareOrderMap.get("total_2")+count);
			}else{
				wareOrderMap.put("total_2", count);
			}
		}
		rs1.close();

		//已交接订单
		rs1 = dbOp.executeQuery("select uo.order_type, count(uo.id), os.stock_area from mailing_batch_package mbp join user_order uo on mbp.order_code = uo.code join order_stock os on mbp.order_code = os.order_code where mbp.create_datetime >= '"+startTime+"' and os.status <> 3 group by uo.order_type, os.stock_area");
		while(rs1.next()){
			int orderType = rs1.getInt(1);
			int count = rs1.getInt(2);
			int stockArea = rs1.getInt(3);
			
			//ware
			if(stockArea == 0){
				if(wareOrderMap.containsKey("bj_3")){
					wareOrderMap.put("bj_3", wareOrderMap.get("bj_3")+count);
				}else{
					wareOrderMap.put("bj_3", count);
				}
			}
			
			if(stockArea == 3){
				if(wareOrderMap.containsKey("zc_3")){
					wareOrderMap.put("zc_3", wareOrderMap.get("zc_3")+count);
				}else{
					wareOrderMap.put("zc_3", count);
				}
			}
			
			if(stockArea == 4){
				if(wareOrderMap.containsKey("wx_3")){
					wareOrderMap.put("wx_3", wareOrderMap.get("wx_3")+count);
				}else{
					wareOrderMap.put("wx_3", count);
				}
			}
			
			if(stockArea == 8){
				if(wareOrderMap.containsKey("xa_3")){
					wareOrderMap.put("xa_3", wareOrderMap.get("xa_3")+count);
				}else{
					wareOrderMap.put("xa_3", count);
				}
			}
			
			if(stockArea == 9){
				if(wareOrderMap.containsKey("cd_3")){
					wareOrderMap.put("cd_3", wareOrderMap.get("cd_3")+count);
				}else{
					wareOrderMap.put("cd_3", count);
				}
			}
			
			if(wareOrderMap.containsKey("total_3")){
				wareOrderMap.put("total_3", wareOrderMap.get("total_3")+count);
			}else{
				wareOrderMap.put("total_3", count);
			}
		}
		rs1.close();
		
		String baseField = "order_count_seg";
		// 统计预测量
		rs1 = dbOp.executeQuery(" SELECT *, CAST(item AS signed) AS startTime FROM order_prediction WHERE pre_date = '"+startTime+"' ");
		while(rs1.next()){
			int start = rs1.getInt("startTime");
			if (start > hour) {
				continue;
			}
			
			int total = 0;
			// 3C
			int count = rs1.getInt(baseField + 3) + rs1.getInt(baseField + 4);
			total += count;
			if(yunyingOrderMap.containsKey("3C_7")){
				yunyingOrderMap.put("3C_7", yunyingOrderMap.get("3C_7")+count);
			}else{
				yunyingOrderMap.put("3C_7", count);
			}
			
			// 服装 
			count = rs1.getInt(baseField + 5) + rs1.getInt(baseField + 6);
			total += count;
			if(yunyingOrderMap.containsKey("fuzhuang_7")){
				yunyingOrderMap.put("fuzhuang_7", yunyingOrderMap.get("fuzhuang_7")+count);
			}else{
				yunyingOrderMap.put("fuzhuang_7", count);
			}
			
			// 鞋类
			count = rs1.getInt(baseField + 9) + rs1.getInt(baseField + 10);
			total += count;
			if(yunyingOrderMap.containsKey("xie_7")){
				yunyingOrderMap.put("xie_7", yunyingOrderMap.get("xie_7")+count);
			}else{
				yunyingOrderMap.put("xie_7", count);
			}
			
			// 内衣
			count = rs1.getInt(baseField + 7) + rs1.getInt(baseField + 8);
			total += count;
			if(yunyingOrderMap.containsKey("neiyi_7")){
				yunyingOrderMap.put("neiyi_7", yunyingOrderMap.get("neiyi_7")+count);
			}else{
				yunyingOrderMap.put("neiyi_7", count);
			}
		 
			// 护肤
			count = rs1.getInt(baseField + 11) + rs1.getInt(baseField + 12);
			total += count;
			if(yunyingOrderMap.containsKey("hufu_7")){
				yunyingOrderMap.put("hufu_7", yunyingOrderMap.get("hufu_7")+count);
			}else{
				yunyingOrderMap.put("hufu_7", count);
			}
			  
			// 成人
			count = rs1.getInt(baseField + 13);
			total += count;
			if(yunyingOrderMap.containsKey("chengren_7")){
				yunyingOrderMap.put("chengren_7", yunyingOrderMap.get("chengren_7")+count);
			}else{
				yunyingOrderMap.put("chengren_7", count);
			}
			 
			// 合计			 
			if(yunyingOrderMap.containsKey("total_7")){
				yunyingOrderMap.put("total_7", yunyingOrderMap.get("total_7")+total);
			}else{
				yunyingOrderMap.put("total_7", total);
			}
		}
		rs1.close();
		
		// 搭销品单量 _3
		rs1 = dbOp.executeQuery(" SELECT uo.flat, uo.order_type, count(DISTINCT uo.id) AS `count` FROM user_order AS uo, user_order_promotion_product AS uopp WHERE uo.id = uopp.order_id AND uopp.flag = 10 AND uo.`status` IN ( 3,6,11,14 ) AND uo.create_datetime >= '"+startTime+"' group by uo.order_type, uo.flat ");
		while(rs1.next()){
			int orderType = rs1.getInt("order_type");
			int count = rs1.getInt("count");
			int flat = rs1.getInt("flat");
			
			if(orderType == 1 || orderType == 2|| orderType == 5 || orderType == 10){   //3C		
				if (flat == 2) { // 大Q官网
					if(yunyingOrderMap.containsKey("daq_3")){
						yunyingOrderMap.put("daq_3", yunyingOrderMap.get("daq_3")+count);
					}else{
						yunyingOrderMap.put("daq_3", count);
					}			
				} else {
					if(yunyingOrderMap.containsKey("3C_3")){
						yunyingOrderMap.put("3C_3", yunyingOrderMap.get("3C_3")+count);
					}else{
						yunyingOrderMap.put("3C_3", count);
					}
				}
			}
			
			if(orderType == 4){   //服装
				if(yunyingOrderMap.containsKey("fuzhuang_3")){
					yunyingOrderMap.put("fuzhuang_3", yunyingOrderMap.get("fuzhuang_3")+count);
				}else{
					yunyingOrderMap.put("fuzhuang_3", count);
				}			 
			}
			
			if(orderType == 6 || orderType == 12 || orderType == 14 || orderType == 18){   //鞋子
				if(yunyingOrderMap.containsKey("xie_3")){
					yunyingOrderMap.put("xie_3", yunyingOrderMap.get("xie_3")+count);
				}else{
					yunyingOrderMap.put("xie_3", count);
				}
			}
			
			if(orderType == 3 || orderType == 19 || orderType == 20){   //内衣
				if(yunyingOrderMap.containsKey("neiyi_3")){
					yunyingOrderMap.put("neiyi_3", yunyingOrderMap.get("neiyi_3")+count);
				}else{
					yunyingOrderMap.put("neiyi_3", count);
				}				 
			}
			
			if(orderType == 7 || orderType == 8 || orderType == 9 || orderType == 11 || orderType == 16){   //护肤
				if(yunyingOrderMap.containsKey("hufu_3")){
					yunyingOrderMap.put("hufu_3", yunyingOrderMap.get("hufu_3")+count);
				}else{
					yunyingOrderMap.put("hufu_3", count);
				}			 
			}
			
			if(orderType == 17){   //成人
				if(yunyingOrderMap.containsKey("chengren_3")){
					yunyingOrderMap.put("chengren_3", yunyingOrderMap.get("chengren_3")+count);
				}else{
					yunyingOrderMap.put("chengren_3", count);
				}				 
			}
			
			// 合计
			if(yunyingOrderMap.containsKey("total_3")){
				yunyingOrderMap.put("total_3", yunyingOrderMap.get("total_3")+count);
			}else{
				yunyingOrderMap.put("total_3", count);
			}
		}
		rs1.close();
		
		// _4 搭销品金额
		StringBuilder sb = new StringBuilder();
		sb.append(" select uo.flat, uo.order_type orderType, sum(uopp.discount_price*uopp.count) pprice ");
		sb.append(" from user_order uo join user_order_promotion_product uopp on uo.id = uopp.order_id and uopp.flag = 10 ");
		sb.append(" where uo.create_datetime >= '"+startTime+"' and uo.status in ( 3,6,11,14 ) ");
		sb.append(" group by uo.order_type, uo.flat ");
		
		rs1 = dbOp.executeQuery(sb.toString());
		while(rs1.next()){
			int orderType = rs1.getInt("orderType");
			int flat = rs1.getInt("flat");						
			float pprice = rs1.getFloat("pprice");
			Float price = Float.valueOf(pprice);
			
			if(orderType == 1 || orderType == 2|| orderType == 5 || orderType == 10){   //3C
				if (flat == 2) { // 大Q官网
					if(yunyingOrderMap2.containsKey("daq_4")){
						yunyingOrderMap2.put("daq_4", yunyingOrderMap2.get("daq_4")+price);
					}else{
						yunyingOrderMap2.put("daq_4", price);
					}			
				} else {
					if(yunyingOrderMap2.containsKey("3C_4")){
						yunyingOrderMap2.put("3C_4", yunyingOrderMap2.get("3C_4")+price);
					}else{
						yunyingOrderMap2.put("3C_4", price);
					}
				}						
			}
			
			if(orderType == 4){   //服装
				if(yunyingOrderMap2.containsKey("fuzhuang_4")){
					yunyingOrderMap2.put("fuzhuang_4", yunyingOrderMap2.get("fuzhuang_4")+price);
				}else{
					yunyingOrderMap2.put("fuzhuang_4", price);
				}			 
			}
			
			if(orderType == 6 || orderType == 12 || orderType == 14 || orderType == 18){   //鞋子
				if(yunyingOrderMap2.containsKey("xie_4")){
					yunyingOrderMap2.put("xie_4", yunyingOrderMap2.get("xie_4")+price);
				}else{
					yunyingOrderMap2.put("xie_4", price);
				}
			}
			
			if(orderType == 3 || orderType == 19 || orderType == 20){   //内衣
				if(yunyingOrderMap2.containsKey("neiyi_4")){
					yunyingOrderMap2.put("neiyi_4", yunyingOrderMap2.get("neiyi_4")+price);
				}else{
					yunyingOrderMap2.put("neiyi_4", price);
				}				 
			}
			
			if(orderType == 7 || orderType == 8 || orderType == 9 || orderType == 11 || orderType == 16){   //护肤
				if(yunyingOrderMap2.containsKey("hufu_4")){
					yunyingOrderMap2.put("hufu_4", yunyingOrderMap2.get("hufu_4")+price);
				}else{
					yunyingOrderMap2.put("hufu_4", price);
				}			 
			}
			
			if(orderType == 17){   //成人
				if(yunyingOrderMap2.containsKey("chengren_4")){
					yunyingOrderMap2.put("chengren_4", yunyingOrderMap2.get("chengren_4")+price);
				}else{
					yunyingOrderMap2.put("chengren_4", price);
				}				 
			}
			
			// 合计
			if(yunyingOrderMap2.containsKey("total_4")){
				yunyingOrderMap2.put("total_4", yunyingOrderMap2.get("total_4")+price);
			}else{
				yunyingOrderMap2.put("total_4", price);
			}
		}
		rs1.close();
		
		// _5 成交额		
		sb.setLength(0);
		sb.append(" select uo.flat, uo.order_type orderType, sum(uo.dprice) price  ");
		sb.append(" from user_order AS uo ");
		sb.append(" where uo.create_datetime >= '"+startTime+"' and uo.status in ( 3,6,11,14 ) ");
		sb.append(" group by uo.order_type, uo.flat ");
	
		rs1 = dbOp.executeQuery(sb.toString());
		while(rs1.next()){
			int orderType = rs1.getInt("orderType");
			int flat = rs1.getInt("flat");						
			float oprice = rs1.getFloat("price");
			Float price = Float.valueOf(oprice);

			if(orderType == 1 || orderType == 2 || orderType == 5 || orderType == 10){   //3C				
				if (flat == 2) { // 大Q官网
					if(yunyingOrderMap2.containsKey("daq_5")){
						yunyingOrderMap2.put("daq_5", yunyingOrderMap2.get("daq_5")+price);
					}else{
						yunyingOrderMap2.put("daq_5", price);
					}			
				} else {
					if(yunyingOrderMap2.containsKey("3C_5")){
						yunyingOrderMap2.put("3C_5", yunyingOrderMap2.get("3C_5")+price);
					}else{
						yunyingOrderMap2.put("3C_5", price);
					}
				}	
			}
			
			if(orderType == 4){   //服装
				if(yunyingOrderMap2.containsKey("fuzhuang_5")){
					yunyingOrderMap2.put("fuzhuang_5", yunyingOrderMap2.get("fuzhuang_5")+price);
				}else{
					yunyingOrderMap2.put("fuzhuang_5", price);
				}			 
			}
			
			if(orderType == 6 || orderType == 12 || orderType == 14 || orderType == 18){   //鞋子
				if(yunyingOrderMap2.containsKey("xie_5")){
					yunyingOrderMap2.put("xie_5", yunyingOrderMap2.get("xie_5")+price);
				}else{
					yunyingOrderMap2.put("xie_5", price);
				}
			}
			
			if(orderType == 3 || orderType == 19 || orderType == 20){   //内衣
				if(yunyingOrderMap2.containsKey("neiyi_5")){
					yunyingOrderMap2.put("neiyi_5", yunyingOrderMap2.get("neiyi_5")+price);
				}else{
					yunyingOrderMap2.put("neiyi_5", price);
				}				 
			}
			
			if(orderType == 7 || orderType == 8 || orderType == 9 || orderType == 11 || orderType == 16){   //护肤
				if(yunyingOrderMap2.containsKey("hufu_5")){
					yunyingOrderMap2.put("hufu_5", yunyingOrderMap2.get("hufu_5")+price);
				}else{
					yunyingOrderMap2.put("hufu_5", price);
				}			 
			}
			
			if(orderType == 17){   //成人
				if(yunyingOrderMap2.containsKey("chengren_5")){
					yunyingOrderMap2.put("chengren_5", yunyingOrderMap2.get("chengren_5")+price);
				}else{
					yunyingOrderMap2.put("chengren_5", price);
				}				 
			}
			
			// 合计
			if(yunyingOrderMap2.containsKey("total_5")){
				yunyingOrderMap2.put("total_5", yunyingOrderMap2.get("total_5")+price);
			}else{
				yunyingOrderMap2.put("total_5", price);
			}
		}
		rs1.close();
		
		
		// _6 积压订单量
		rs1 = dbOp.executeQuery(" SELECT flat, order_type, COUNT(id) AS `count` FROM user_order WHERE `status` = 0 AND create_datetime >= '"+startTime+"'  GROUP BY order_type, flat ");
		while(rs1.next()){
			int orderType = rs1.getInt("order_type");
			int count = rs1.getInt("count");
			int flat = rs1.getInt("flat");
						
			if(orderType == 1 || orderType == 2|| orderType == 5 || orderType == 10){   //3C				
				if (flat == 2) { // 大Q官网
					if(yunyingOrderMap.containsKey("daq_6")){
						yunyingOrderMap.put("daq_6", yunyingOrderMap.get("daq_6")+count);
					}else{
						yunyingOrderMap.put("daq_6", count);
					}			
				} else {
					if(yunyingOrderMap.containsKey("3C_6")){
						yunyingOrderMap.put("3C_6", yunyingOrderMap.get("3C_6")+count);
					}else{
						yunyingOrderMap.put("3C_6", count);
					}
				}	
			}
			
			if(orderType == 4){   //服装
				if(yunyingOrderMap.containsKey("fuzhuang_6")){
					yunyingOrderMap.put("fuzhuang_6", yunyingOrderMap.get("fuzhuang_6")+count);
				}else{
					yunyingOrderMap.put("fuzhuang_6", count);
				}			 
			}
			
			if(orderType == 6 || orderType == 12 || orderType == 14 || orderType == 18){   //鞋子
				if(yunyingOrderMap.containsKey("xie_6")){
					yunyingOrderMap.put("xie_6", yunyingOrderMap.get("xie_6")+count);
				}else{
					yunyingOrderMap.put("xie_6", count);
				}
			}
			
			if(orderType == 3 || orderType == 19 || orderType == 20){   //内衣
				if(yunyingOrderMap.containsKey("neiyi_6")){
					yunyingOrderMap.put("neiyi_6", yunyingOrderMap.get("neiyi_6")+count);
				}else{
					yunyingOrderMap.put("neiyi_6", count);
				}				 
			}
			
			if(orderType == 7 || orderType == 8 || orderType == 9 || orderType == 11 || orderType == 16){   //护肤
				if(yunyingOrderMap.containsKey("hufu_6")){
					yunyingOrderMap.put("hufu_6", yunyingOrderMap.get("hufu_6")+count);
				}else{
					yunyingOrderMap.put("hufu_6", count);
				}			 
			}
			
			if(orderType == 17){   //成人
				if(yunyingOrderMap.containsKey("chengren_6")){
					yunyingOrderMap.put("chengren_6", yunyingOrderMap.get("chengren_6")+count);
				}else{
					yunyingOrderMap.put("chengren_6", count);
				}				 
			}
			
			// 合计
			if(yunyingOrderMap.containsKey("total_6")){
				yunyingOrderMap.put("total_6", yunyingOrderMap.get("total_6")+count);
			}else{
				yunyingOrderMap.put("total_6", count);
			}
		}
		rs1.close();
		
		
				//仓储订单监控
				Calendar calendar = Calendar.getInstance();
				calendar.set(Calendar.MINUTE, 0);
				calendar.set(Calendar.SECOND, 0);
				String endTime = DateUtil.formatDate(calendar.getTime(), DateUtil.normalTimeFormat);
				
				calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)-1);
				startTime = DateUtil.formatDate(calendar.getTime(), DateUtil.normalTimeFormat);
				
				
				//上一小时已发货订单
				rs1 = dbOp.executeQuery("select uo.order_type, count(uo.id), os.stock_area from mailing_balance mb join user_order uo  on mb.order_id = uo.id join order_stock os on mb.order_id = os.order_id where stockout_datetime >= '"+startTime+"' and stockout_datetime <= '"+endTime+"' and os.status <> 3 group by uo.order_type, os.stock_area");
				while(rs1.next()){
					int orderType = rs1.getInt(1);
					int count = rs1.getInt(2);
					int stockArea = rs1.getInt(3);
					
					//ware
					if(stockArea == 0){
						if(wareOrderMap.containsKey("bj_4")){
							wareOrderMap.put("bj_4", wareOrderMap.get("bj_4")+count);
						}else{
							wareOrderMap.put("bj_4", count);
						}
					}
					
					if(stockArea == 3){
						if(wareOrderMap.containsKey("zc_4")){
							wareOrderMap.put("zc_4", wareOrderMap.get("zc_4")+count);
						}else{
							wareOrderMap.put("zc_4", count);
						}
					}
					
					if(stockArea == 4){
						if(wareOrderMap.containsKey("wx_4")){
							wareOrderMap.put("wx_4", wareOrderMap.get("wx_4")+count);
						}else{
							wareOrderMap.put("wx_4", count);
						}
					}
					
					if(stockArea == 8){
						if(wareOrderMap.containsKey("xa_4")){
							wareOrderMap.put("xa_4", wareOrderMap.get("xa_4")+count);
						}else{
							wareOrderMap.put("xa_4", count);
						}
					}
					
					if(stockArea == 9){
						if(wareOrderMap.containsKey("cd_4")){
							wareOrderMap.put("cd_4", wareOrderMap.get("cd_4")+count);
						}else{
							wareOrderMap.put("cd_4", count);
						}
					}
					
					if(wareOrderMap.containsKey("total_4")){
						wareOrderMap.put("total_4", wareOrderMap.get("total_4")+count);
					}else{
						wareOrderMap.put("total_4", count);
					}
				}
				rs1.close();
				
				//本小时已发货订单
				rs1 = dbOp.executeQuery("select uo.order_type, count(uo.id), os.stock_area from mailing_balance mb join user_order uo  on mb.order_id = uo.id join order_stock os on mb.order_id = os.order_id where stockout_datetime >= '"+endTime+"' and os.status <> 3 group by uo.order_type, os.stock_area");
				while(rs1.next()){
					int orderType = rs1.getInt(1);
					int count = rs1.getInt(2);
					int stockArea = rs1.getInt(3);
					
					//ware
					if(stockArea == 0){
						if(wareOrderMap.containsKey("bj_5")){
							wareOrderMap.put("bj_5", wareOrderMap.get("bj_5")+count);
						}else{
							wareOrderMap.put("bj_5", count);
						}
					}
					
					if(stockArea == 3){
						if(wareOrderMap.containsKey("zc_5")){
							wareOrderMap.put("zc_5", wareOrderMap.get("zc_5")+count);
						}else{
							wareOrderMap.put("zc_5", count);
						}
					}
					
					if(stockArea == 4){
						if(wareOrderMap.containsKey("wx_5")){
							wareOrderMap.put("wx_5", wareOrderMap.get("wx_5")+count);
						}else{
							wareOrderMap.put("wx_5", count);
						}
					}
					
					if(stockArea == 8){
						if(wareOrderMap.containsKey("xa_5")){
							wareOrderMap.put("xa_5", wareOrderMap.get("xa_5")+count);
						}else{
							wareOrderMap.put("xa_5", count);
						}
					}
					
					if(stockArea == 9){
						if(wareOrderMap.containsKey("cd_5")){
							wareOrderMap.put("cd_5", wareOrderMap.get("cd_5")+count);
						}else{
							wareOrderMap.put("cd_5", count);
						}
					}
					
					if(wareOrderMap.containsKey("total_5")){
						wareOrderMap.put("total_5", wareOrderMap.get("total_5")+count);
					}else{
						wareOrderMap.put("total_5", count);
					}
				}
				rs1.close();
%>
<table style="font-size:40px" border=1  cellpadding="0" cellspacing="0">
	<tr bgcolor="#4688D6">
		<td colspan="8" align="center"><font color="#FFFFFF"><B>运营系统_订单监控报表</B></font></td>
	</tr>
	<tr bgcolor="#4688D6" style="font-size:35px">
		<td><font color="#FFFFFF">事业部</font></td>
		<td><font color="#FFFFFF">预测量</font></td>
		<td><font color="#FFFFFF">实际非重单</font></td>
		<td><font color="#FFFFFF">实际成交单</font></td>
		<td><font color="#FFFFFF">成交率</font></td>
		<td><font color="#FFFFFF">按订单搭销率</font></td>
		<td><font color="#FFFFFF">按金额搭销率</font></td>
		<td><font color="#FFFFFF">积压订单量</font></td>
	</tr>
	<tr>
		<td>3C</td>
		<td align="right"><%=yunyingOrderMap.get("3C_7")!=null?yunyingOrderMap.get("3C_7"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("3C_1")!=null?yunyingOrderMap.get("3C_1"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("3C_2")!=null?yunyingOrderMap.get("3C_2"):0 %></td>
		<td align="right">
			<%if(yunyingOrderMap.get("3C_1") != null && yunyingOrderMap.get("3C_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("3C_2"))/Float.valueOf(yunyingOrderMap.get("3C_1")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap.get("3C_3") != null && yunyingOrderMap.get("3C_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("3C_3"))/Float.valueOf(yunyingOrderMap.get("3C_2")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap2.get("3C_4") != null && yunyingOrderMap2.get("3C_5") != null){ %>
			<%=NumberUtil.priceOrder((yunyingOrderMap2.get("3C_4")/yunyingOrderMap2.get("3C_5"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right"><%=yunyingOrderMap.get("3C_6")!=null?yunyingOrderMap.get("3C_6"):0 %></td>
	</tr>
	<tr>
		<td>服装</td>
		<td align="right"><%=yunyingOrderMap.get("fuzhuang_7")!=null?yunyingOrderMap.get("fuzhuang_7"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("fuzhuang_1")!=null?yunyingOrderMap.get("fuzhuang_1"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("fuzhuang_2")!=null?yunyingOrderMap.get("fuzhuang_2"):0 %></td>
		<td align="right">
			<%if(yunyingOrderMap.get("fuzhuang_1") != null && yunyingOrderMap.get("fuzhuang_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("fuzhuang_2"))/Float.valueOf(yunyingOrderMap.get("fuzhuang_1")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap.get("fuzhuang_3") != null && yunyingOrderMap.get("fuzhuang_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("fuzhuang_3"))/Float.valueOf(yunyingOrderMap.get("fuzhuang_2")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap2.get("fuzhuang_4") != null && yunyingOrderMap2.get("fuzhuang_5") != null){ %>
			<%=NumberUtil.priceOrder((yunyingOrderMap2.get("fuzhuang_4")/yunyingOrderMap2.get("fuzhuang_5"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>			 
		</td>
		<td align="right"><%=yunyingOrderMap.get("fuzhuang_6")!=null?yunyingOrderMap.get("fuzhuang_6"):0 %></td>
	</tr>
	<tr>
		<td>鞋类</td>
		<td align="right"><%=yunyingOrderMap.get("xie_7")!=null?yunyingOrderMap.get("xie_7"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("xie_1")!=null?yunyingOrderMap.get("xie_1"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("xie_2")!=null?yunyingOrderMap.get("xie_2"):0 %></td>
		<td align="right">
			<%if(yunyingOrderMap.get("xie_1") != null && yunyingOrderMap.get("xie_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("xie_2"))/Float.valueOf(yunyingOrderMap.get("xie_1")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap.get("xie_3") != null && yunyingOrderMap.get("xie_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("xie_3"))/Float.valueOf(yunyingOrderMap.get("xie_2")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap2.get("xie_4") != null && yunyingOrderMap2.get("xie_5") != null){ %>
			<%=NumberUtil.priceOrder((yunyingOrderMap2.get("xie_4")/yunyingOrderMap2.get("xie_5"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right"><%=yunyingOrderMap.get("xie_6")!=null?yunyingOrderMap.get("xie_6"):0 %></td>
	</tr>
	<tr>
		<td>内衣</td>
		<td align="right"><%=yunyingOrderMap.get("neiyi_7")!=null?yunyingOrderMap.get("neiyi_7"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("neiyi_1")!=null?yunyingOrderMap.get("neiyi_1"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("neiyi_2")!=null?yunyingOrderMap.get("neiyi_2"):0 %></td>
		<td align="right">
			<%if(yunyingOrderMap.get("neiyi_1") != null && yunyingOrderMap.get("neiyi_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("neiyi_2"))/Float.valueOf(yunyingOrderMap.get("neiyi_1")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap.get("neiyi_3") != null && yunyingOrderMap.get("neiyi_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("neiyi_3"))/Float.valueOf(yunyingOrderMap.get("neiyi_2")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap2.get("neiyi_4") != null && yunyingOrderMap2.get("neiyi_5") != null){ %>
			<%=NumberUtil.priceOrder((yunyingOrderMap2.get("neiyi_4")/yunyingOrderMap2.get("neiyi_5"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right"><%=yunyingOrderMap.get("neiyi_6")!=null?yunyingOrderMap.get("neiyi_6"):0 %></td>
	</tr>
	<tr>
		<td>护肤</td>
		<td align="right"><%=yunyingOrderMap.get("hufu_7")!=null?yunyingOrderMap.get("hufu_7"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("hufu_1")!=null?yunyingOrderMap.get("hufu_1"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("hufu_2")!=null?yunyingOrderMap.get("hufu_2"):0 %></td>
		<td align="right">
			<%if(yunyingOrderMap.get("hufu_1") != null && yunyingOrderMap.get("hufu_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("hufu_2"))/Float.valueOf(yunyingOrderMap.get("hufu_1")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap.get("hufu_3") != null && yunyingOrderMap.get("hufu_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("hufu_3"))/Float.valueOf(yunyingOrderMap.get("hufu_2")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap2.get("hufu_4") != null && yunyingOrderMap2.get("hufu_5") != null){ %>
			<%=NumberUtil.priceOrder((yunyingOrderMap2.get("hufu_4")/yunyingOrderMap2.get("hufu_5"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right"><%=yunyingOrderMap.get("hufu_6")!=null?yunyingOrderMap.get("hufu_6"):0 %></td>
	</tr>
	<tr>
		<td>成人</td>
		<td align="right"><%=yunyingOrderMap.get("chengren_7")!=null?yunyingOrderMap.get("chengren_7"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("chengren_1")!=null?yunyingOrderMap.get("chengren_1"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("chengren_2")!=null?yunyingOrderMap.get("chengren_2"):0 %></td>
		<td align="right">
			<%if(yunyingOrderMap.get("chengren_1") != null && yunyingOrderMap.get("chengren_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("chengren_2"))/Float.valueOf(yunyingOrderMap.get("chengren_1")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap.get("chengren_3") != null && yunyingOrderMap.get("chengren_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("chengren_3"))/Float.valueOf(yunyingOrderMap.get("chengren_2")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap2.get("chengren_4") != null && yunyingOrderMap2.get("chengren_5") != null){ %>
			<%=NumberUtil.priceOrder((yunyingOrderMap2.get("chengren_4")/yunyingOrderMap2.get("chengren_5"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right"><%=yunyingOrderMap.get("chengren_6")!=null?yunyingOrderMap.get("chengren_6"):0 %></td>
	</tr>
	<tr>
		<td>大Q官网</td>
		<td align="right">——</td>
		<td align="right"><%=yunyingOrderMap.get("daq_1")!=null?yunyingOrderMap.get("daq_1"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("daq_2")!=null?yunyingOrderMap.get("daq_2"):0 %></td>
		<td align="right">
			<%if(yunyingOrderMap.get("daq_1") != null && yunyingOrderMap.get("daq_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("daq_2"))/Float.valueOf(yunyingOrderMap.get("daq_1")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap.get("daq_3") != null && yunyingOrderMap.get("daq_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("daq_3"))/Float.valueOf(yunyingOrderMap.get("daq_2")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">	
			<%if(yunyingOrderMap2.get("daq_4") != null && yunyingOrderMap2.get("daq_5") != null){ %>
			<%=NumberUtil.priceOrder((yunyingOrderMap2.get("daq_4")/yunyingOrderMap2.get("daq_5"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>			 
		</td>
		<td align="right"><%=yunyingOrderMap.get("daq_6")!=null?yunyingOrderMap.get("daq_6"):0 %></td>
	</tr>
	<tr bgcolor="#CCCCCC">
		<td>合计</td>
		<td align="right"><%=yunyingOrderMap.get("total_7")!=null?yunyingOrderMap.get("total_7"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("total_1")!=null?yunyingOrderMap.get("total_1"):0 %></td>
		<td align="right"><%=yunyingOrderMap.get("total_2")!=null?yunyingOrderMap.get("total_2"):0 %></td>
		<td align="right">
			<%if(yunyingOrderMap.get("total_1") != null && yunyingOrderMap.get("total_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("total_2"))/Float.valueOf(yunyingOrderMap.get("total_1")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap.get("total_3") != null && yunyingOrderMap.get("total_2") != null){ %>
			<%=NumberUtil.priceOrder((Float.valueOf(yunyingOrderMap.get("total_3"))/Float.valueOf(yunyingOrderMap.get("total_2")))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right">
			<%if(yunyingOrderMap2.get("total_4") != null && yunyingOrderMap2.get("total_5") != null){ %>
			<%=NumberUtil.priceOrder((yunyingOrderMap2.get("total_4")/yunyingOrderMap2.get("total_5"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<td align="right"><%=yunyingOrderMap.get("total_6")!=null?yunyingOrderMap.get("total_6"):0 %></td>
	</tr>
</table>
<br/><br/>
<table style="font-size:40px" border=1  cellpadding="0" cellspacing="0">
	<tr bgcolor="#4688D6">
		<td colspan="7" align="center"><font color="#FFFFFF"><B>仓储中心_订单监控报表</B></font></td>
	</tr>
	<tr bgcolor="#4688D6">
		<td><font color="#FFFFFF">仓储</font></td>
		<td><font color="#FFFFFF">待发货订单</font></td>
		<td><font color="#FFFFFF">已发货订单</font></td>
		<td><font color="#FFFFFF">已交接订单</font></td>
		<td><font color="#FFFFFF">上期完成量</font></td>
		<td><font color="#FFFFFF">本期完成量</font></td>
	</tr>
	<tr>
		<td>华南仓</td>
		<td><%=wareOrderMap.get("zc_1")!=null?wareOrderMap.get("zc_1"):0 %></td>
		<td><%=wareOrderMap.get("zc_2")!=null?wareOrderMap.get("zc_2"):0 %></td>
		<td><%=wareOrderMap.get("zc_3")!=null?wareOrderMap.get("zc_3"):0 %></td>
		<td><%=wareOrderMap.get("zc_4")!=null?wareOrderMap.get("zc_4"):0 %></td>
		<td><%=wareOrderMap.get("zc_5")!=null?wareOrderMap.get("zc_5"):0 %></td>
	</tr>
	<tr>
		<td>华东仓</td>
		<td><%=wareOrderMap.get("wx_1")!=null?wareOrderMap.get("wx_1"):0 %></td>
		<td><%=wareOrderMap.get("wx_2")!=null?wareOrderMap.get("wx_2"):0 %></td>
		<td><%=wareOrderMap.get("wx_3")!=null?wareOrderMap.get("wx_3"):0 %></td>
		<td><%=wareOrderMap.get("wx_4")!=null?wareOrderMap.get("wx_4"):0 %></td>
		<td><%=wareOrderMap.get("wx_5")!=null?wareOrderMap.get("wx_5"):0 %></td>
	</tr>
	<tr>
		<td>成都仓</td>
		<td><%=wareOrderMap.get("cd_1")!=null?wareOrderMap.get("cd_1"):0 %></td>
		<td><%=wareOrderMap.get("cd_2")!=null?wareOrderMap.get("cd_2"):0 %></td>
		<td><%=wareOrderMap.get("cd_3")!=null?wareOrderMap.get("cd_3"):0 %></td>
		<td><%=wareOrderMap.get("cd_4")!=null?wareOrderMap.get("cd_4"):0 %></td>
		<td><%=wareOrderMap.get("cd_5")!=null?wareOrderMap.get("cd_5"):0 %></td>
	</tr>
	<tr>
		<td>北京仓</td>
		<td><%=wareOrderMap.get("bj_1")!=null?wareOrderMap.get("bj_1"):0 %></td>
		<td><%=wareOrderMap.get("bj_2")!=null?wareOrderMap.get("bj_2"):0 %></td>
		<td><%=wareOrderMap.get("bj_3")!=null?wareOrderMap.get("bj_3"):0 %></td>
		<td><%=wareOrderMap.get("bj_4")!=null?wareOrderMap.get("bj_4"):0 %></td>
		<td><%=wareOrderMap.get("bj_5")!=null?wareOrderMap.get("bj_5"):0 %></td>
	</tr>
	<tr>
		<td>西安仓</td>
		<td><%=wareOrderMap.get("xa_1")!=null?wareOrderMap.get("xa_1"):0 %></td>
		<td><%=wareOrderMap.get("xa_2")!=null?wareOrderMap.get("xa_2"):0 %></td>
		<td><%=wareOrderMap.get("xa_3")!=null?wareOrderMap.get("xa_3"):0 %></td>
		<td><%=wareOrderMap.get("xa_4")!=null?wareOrderMap.get("xa_4"):0 %></td>
		<td><%=wareOrderMap.get("xa_5")!=null?wareOrderMap.get("xa_5"):0 %></td>
	</tr>
	<tr bgcolor="#CCCCCC">
		<td>总计</td>
		<td><%=wareOrderMap.get("total_1")!=null?wareOrderMap.get("total_1"):0 %></td>
		<td><%=wareOrderMap.get("total_2")!=null?wareOrderMap.get("total_2"):0 %></td>
		<td><%=wareOrderMap.get("total_3")!=null?wareOrderMap.get("total_3"):0 %></td>
		<td><%=wareOrderMap.get("total_4")!=null?wareOrderMap.get("total_4"):0 %></td>
		<td><%=wareOrderMap.get("total_5")!=null?wareOrderMap.get("total_5"):0 %></td>
	</tr>
</table>
<%
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		dbOp.release();
	}
%>
</body>
</html>
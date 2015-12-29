<%@page import="adultadmin.util.db.DbOperation"%>
<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,java.sql.*,java.util.*" %>
<html>
<title>买卖宝后台</title>
<head>
<meta http-equiv=refresh content="60">
</head>
<body>
<%
	DbOperation dbOp = new DbOperation();
	dbOp.init(DbOperation.DB_SLAVE);
	String startTime = DateUtil.getNowDateStr();
	try{
		//省列表
		List<String> provinces = new ArrayList<String>();
		ResultSet rs1 = dbOp.executeQuery("select * from provinces");
		while(rs1.next()){
			provinces.add(rs1.getString("name"));
		}
		rs1.close();
		
		HashMap<Integer, HashMap<String, Integer>> yunyingOrderMap = new HashMap<Integer, HashMap<String, Integer>>();
		HashMap<String, Integer> totalMap = new HashMap<String, Integer>();
		yunyingOrderMap.put(99, totalMap);
		
		//待发货订单
		rs1 = dbOp.executeQuery("select pr.name, count(os.id),os.stock_area from order_stock os join user_order_extend_info uoei on os.order_id = uoei.id join provinces pr on uoei.add_id1 = pr.id where os.status in (1,5) group by pr.id, os.stock_area");
				while(rs1.next()){
					String province = rs1.getString(1);
					int count = rs1.getInt(2);
					int stockArea = rs1.getInt(3);
					
					if(yunyingOrderMap.containsKey(stockArea)){
						HashMap<String, Integer> map = yunyingOrderMap.get(stockArea);
						if(map.containsKey(province+"_1")){
							map.put(province+"_1", map.get(province+"_1")+count);
						}else{
							map.put(province+"_1", count);
						}
						
						if(map.containsKey("0_1")){
							map.put("0_1", map.get("0_1")+count);
						}else{
							map.put("0_1", count);
						}
					}else{
						HashMap<String, Integer> map = new HashMap<String, Integer>();
						map.put(province+"_1", count);
						map.put("0_1", count);
						yunyingOrderMap.put(stockArea, map);
					}
					
					
					HashMap<String, Integer> map = yunyingOrderMap.get(99);
					if(map.containsKey(province+"_1")){
						map.put(province+"_1", map.get(province+"_1")+count);
					}else{
						map.put(province+"_1", count);
					}
					if(map.containsKey("0_1")){
						map.put("0_1", map.get("0_1")+count);
					}else{
						map.put("0_1", count);
					}
				}
				rs1.close();
				
		
				//作业中订单
				rs1 = dbOp.executeQuery("select pr.name, count(os.id),os.stock_area from order_stock os join user_order_extend_info uoei on os.order_id = uoei.id join provinces pr on uoei.add_id1 = pr.id where os.status in (5) group by pr.id, os.stock_area");
						while(rs1.next()){
							String province = rs1.getString(1);
							int count = rs1.getInt(2);
							int stockArea = rs1.getInt(3);
							
							if(yunyingOrderMap.containsKey(stockArea)){
								HashMap<String, Integer> map = yunyingOrderMap.get(stockArea);
								if(map.containsKey(province+"_2")){
									map.put(province+"_2", map.get(province+"_2")+count);
								}else{
									map.put(province+"_2", count);
								}
								
								if(map.containsKey("0_2")){
									map.put("0_2", map.get("0_2")+count);
								}else{
									map.put("0_2", count);
								}
							}else{
								HashMap<String, Integer> map = new HashMap<String, Integer>();
								map.put(province+"_2", count);
								map.put("0_2", count);
								yunyingOrderMap.put(stockArea, map);
							}
							
							
							HashMap<String, Integer> map = yunyingOrderMap.get(99);
							if(map.containsKey(province+"_2")){
								map.put(province+"_2", map.get(province+"_2")+count);
							}else{
								map.put(province+"_2", count);
							}
							if(map.containsKey("0_2")){
								map.put("0_2", map.get("0_2")+count);
							}else{
								map.put("0_2", count);
							}
						}
						rs1.close();
				
						
						//已出库订单
						rs1 = dbOp.executeQuery("select pr.name, count(os.id),os.stock_area from mailing_balance mb join order_stock os on mb.order_id = os.order_id join user_order_extend_info uoei on os.order_id = uoei.id join provinces pr on uoei.add_id1 = pr.id where os.status <> 3 and mb.stockout_datetime >= '"+startTime+"' group by pr.id, os.stock_area");
								while(rs1.next()){
									String province = rs1.getString(1);
									int count = rs1.getInt(2);
									int stockArea = rs1.getInt(3);
									
									if(yunyingOrderMap.containsKey(stockArea)){
										HashMap<String, Integer> map = yunyingOrderMap.get(stockArea);
										if(map.containsKey(province+"_3")){
											map.put(province+"_3", map.get(province+"_3")+count);
										}else{
											map.put(province+"_3", count);
										}
										
										if(map.containsKey("0_3")){
											map.put("0_3", map.get("0_3")+count);
										}else{
											map.put("0_3", count);
										}
									}else{
										HashMap<String, Integer> map = new HashMap<String, Integer>();
										map.put(province+"_3", count);
										map.put("0_3", count);
										yunyingOrderMap.put(stockArea, map);
									}
									
									
									HashMap<String, Integer> map = yunyingOrderMap.get(99);
									if(map.containsKey(province+"_3")){
										map.put(province+"_3", map.get(province+"_3")+count);
									}else{
										map.put(province+"_3", count);
									}
									if(map.containsKey("0_3")){
										map.put("0_3", map.get("0_3")+count);
									}else{
										map.put("0_3", count);
									}
								}
								rs1.close();
								
										//已交接订单
										rs1 = dbOp.executeQuery("select pr.name, count(os.id), os.stock_area from mailing_batch_package mbp join order_stock os on mbp.order_code = os.order_code join user_order_extend_info uoei on os.order_id = uoei.id join provinces pr on uoei.add_id1 = pr.id where mbp.create_datetime >= '"+startTime+"' and os.status <> 3 group by pr.id, os.stock_area");
												while(rs1.next()){
													String province = rs1.getString(1);
													int count = rs1.getInt(2);
													int stockArea = rs1.getInt(3);
													
													if(yunyingOrderMap.containsKey(stockArea)){
														HashMap<String, Integer> map = yunyingOrderMap.get(stockArea);
														if(map.containsKey(province+"_4")){
															map.put(province+"_4", map.get(province+"_4")+count);
														}else{
															map.put(province+"_4", count);
														}
														
														if(map.containsKey("0_4")){
															map.put("0_4", map.get("0_4")+count);
														}else{
															map.put("0_4", count);
														}
													}else{
														HashMap<String, Integer> map = new HashMap<String, Integer>();
														map.put(province+"_4", count);
														map.put("0_4", count);
														yunyingOrderMap.put(stockArea, map);
													}
													
													
													HashMap<String, Integer> map = yunyingOrderMap.get(99);
													if(map.containsKey(province+"_4")){
														map.put(province+"_4", map.get(province+"_4")+count);
													}else{
														map.put(province+"_4", count);
													}
													if(map.containsKey("0_4")){
														map.put("0_4", map.get("0_4")+count);
													}else{
														map.put("0_4", count);
													}
												}
												rs1.close();

%>
<table style="font-size:20px" border=1  cellpadding="0" cellspacing="0">
	<tr bgcolor="#4688D6">
		<td colspan="<%=provinces.size()+3 %>" align="center"><font color="#FFFFFF"><B>各仓各区域订单监控表</B></font></td>
	</tr>
	<tr bgcolor="#4688D6">
		<td colspan="2" width="80"></td>
		<td width="50"><font color="#FFFFFF">合计</font></td>
		<%
			for(String name:provinces){
		%>
		<td width="50"><font color="#FFFFFF"><%=name.replace("省", "").replace("自治区", "").replace("市", "").replace("壮族", "").replace("回族", "").replace("维吾尔", "") %></font></td>
		<%	} %>
	</tr>
	<tr>
		<td rowspan="5" width="30">总计</td>
		<td>销售已出库</td>
		<%
		HashMap<String, Integer> map = yunyingOrderMap.get(99);
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_1")!=null?map.get("0_1"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_1")!=null?map.get(name+"_1"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储作业中</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_2")!=null?map.get("0_2"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_2")!=null?map.get(name+"_2"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储已出库</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_3")!=null?map.get("0_3"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_3")!=null?map.get(name+"_3"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储已交接</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_4")!=null?map.get("0_4"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_4")!=null?map.get(name+"_4"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>发货占比</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%if(map.get("0_3") != null){ %>
			<%=NumberUtil.priceOrder(Float.valueOf(map.get("0_3"))/Float.valueOf(map.get("0_3"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%if(map.get("0_3") != null && map.get(name+"_3") != null){ %>
			<%=NumberUtil.priceOrder(Float.valueOf(map.get(name+"_3"))/Float.valueOf(map.get("0_3"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td rowspan="5" width="30">华南仓</td>
		<td>销售已出库</td>
		<%
		map = yunyingOrderMap.get(3);
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_1")!=null?map.get("0_1"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_1")!=null?map.get(name+"_1"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储作业中</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_2")!=null?map.get("0_2"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_2")!=null?map.get(name+"_2"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储已出库</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_3")!=null?map.get("0_3"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_3")!=null?map.get(name+"_3"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储已交接</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_4")!=null?map.get("0_4"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_4")!=null?map.get(name+"_4"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>发货占比</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%if(map.get("0_3") != null){ %>
			<%=NumberUtil.priceOrder(Float.valueOf(map.get("0_3"))/Float.valueOf(map.get("0_3"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%if(map.get("0_3") != null && map.get(name+"_3") != null){ %>
			<%=NumberUtil.priceOrder(Float.valueOf(map.get(name+"_3"))/Float.valueOf(map.get("0_3"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td rowspan="5" width="30">华东仓</td>
		<td>销售已出库</td>
		<%
		map = yunyingOrderMap.get(4);
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_1")!=null?map.get("0_1"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_1")!=null?map.get(name+"_1"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储作业中</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_2")!=null?map.get("0_2"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_2")!=null?map.get(name+"_2"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储已出库</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_3")!=null?map.get("0_3"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_3")!=null?map.get(name+"_3"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储已交接</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_4")!=null?map.get("0_4"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_4")!=null?map.get(name+"_4"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>发货占比</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%if(map.get("0_3") != null){ %>
			<%=NumberUtil.priceOrder(Float.valueOf(map.get("0_3"))/Float.valueOf(map.get("0_3"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%if(map.get("0_3") != null && map.get(name+"_3") != null){ %>
			<%=NumberUtil.priceOrder(Float.valueOf(map.get(name+"_3"))/Float.valueOf(map.get("0_3"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td rowspan="5" width="30">西南仓</td>
		<td>销售已出库</td>
		<%
		map = yunyingOrderMap.get(9);
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_1")!=null?map.get("0_1"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_1")!=null?map.get(name+"_1"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储作业中</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_2")!=null?map.get("0_2"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_2")!=null?map.get(name+"_2"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储已出库</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_3")!=null?map.get("0_3"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_3")!=null?map.get(name+"_3"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储已交接</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_4")!=null?map.get("0_4"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_4")!=null?map.get(name+"_4"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>发货占比</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%if(map.get("0_3") != null){ %>
			<%=NumberUtil.priceOrder(Float.valueOf(map.get("0_3"))/Float.valueOf(map.get("0_3"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%if(map.get("0_3") != null && map.get(name+"_3") != null){ %>
			<%=NumberUtil.priceOrder(Float.valueOf(map.get(name+"_3"))/Float.valueOf(map.get("0_3"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td rowspan="5" width="30">北京仓</td>
		<td>销售已出库</td>
		<%
		map = yunyingOrderMap.get(0);
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_1")!=null?map.get("0_1"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_1")!=null?map.get(name+"_1"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储作业中</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_2")!=null?map.get("0_2"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_2")!=null?map.get(name+"_2"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储已出库</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_3")!=null?map.get("0_3"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_3")!=null?map.get(name+"_3"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储已交接</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_4")!=null?map.get("0_4"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_4")!=null?map.get(name+"_4"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>发货占比</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%if(map.get("0_3") != null){ %>
			<%=NumberUtil.priceOrder(Float.valueOf(map.get("0_3"))/Float.valueOf(map.get("0_3"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%if(map.get("0_3") != null && map.get(name+"_3") != null){ %>
			<%=NumberUtil.priceOrder(Float.valueOf(map.get(name+"_3"))/Float.valueOf(map.get("0_3"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td rowspan="5" width="30">西安仓</td>
		<td>销售已出库</td>
		<%
		map = yunyingOrderMap.get(8);
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_1")!=null?map.get("0_1"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_1")!=null?map.get(name+"_1"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储作业中</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_2")!=null?map.get("0_2"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_2")!=null?map.get(name+"_2"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储已出库</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_3")!=null?map.get("0_3"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_3")!=null?map.get(name+"_3"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>仓储已交接</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%=map.get("0_4")!=null?map.get("0_4"):0 %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%=map.get(name+"_4")!=null?map.get(name+"_4"):0 %>
		</td>
		<%	
			}
		}
		%>
	</tr>
	<tr>
		<td>发货占比</td>
		<%
		if(map != null){
		%>
		<td align="right">
			<%if(map.get("0_3") != null){ %>
			<%=NumberUtil.priceOrder(Float.valueOf(map.get("0_3"))/Float.valueOf(map.get("0_3"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<%
			for(String name:provinces){
		%>
		<td align="right">
			<%if(map.get("0_3") != null && map.get(name+"_3") != null){ %>
			<%=NumberUtil.priceOrder(Float.valueOf(map.get(name+"_3"))/Float.valueOf(map.get("0_3"))*100) %>%
			<%}else{ %>
			0.00%
			<%} %>
		</td>
		<%	
			}
		}
		%>
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

<%@page import="java.util.Map.Entry"%><%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,adultadmin.util.db.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.*,ormap.ProductLineMap" %>
<%@ page import="adultadmin.bean.*, adultadmin.bean.order.*, adultadmin.bean.stock.*,adultadmin.bean.buy.*"%>
<%@ page import="adultadmin.service.*, adultadmin.service.infc.*" %>
<%
	response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);

	voUser user = (voUser)session.getAttribute("userView");

    Connection conn = null;
	Statement st = null;
	IStockService service = null;
    IProductPackageService ppService = null;
    IProductStockService psService = null;
    IAdminService adminService = null;
    
	String[] productIds = request.getParameterValues("productId");
	if(productIds == null || productIds.length <= 0){
%>
<script>
alert('未选择任何产品');
history.back(-1);
</script>
<%		
		return;
	}
	List productIdList = Arrays.asList(productIds);


	response.setContentType("application/vnd.ms-excel");
	String now = DateUtil.getNow().substring(0,10);
	String fileName = "QHB_"+now;
	response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xls\"");


//数据库大查询锁，等待3秒
	if (!DbLock.slaveServerQueryLocked(100)) {
		response.sendRedirect(request.getContextPath()+"/tip.jsp?db=adult_slave");
		return;
	}
	
try{
    DbLock.slaveServerOperator = user.getUsername() + "_即时发货状态统计_" + DateUtil.getNow();

    conn = DbUtil.getConnection(DbOperation.DB_SLAVE2);
    st = conn.createStatement();
	service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
    ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
    psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
    adminService = ServiceFactory.createAdminServiceLBJ();

%>
<osCache:cache scope="application" time="900">
<%!
int checkStock(List orderProductList) {
    if (orderProductList == null) {
        return 3;
    }

    Iterator itr = orderProductList.iterator();
    boolean bj = true;
    boolean gd = true;
    boolean gs = true;
    voOrderProduct op = null;
    while (itr.hasNext()) {
        op = (voOrderProduct) itr.next();
        if (op.getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
            bj = false;
        }
        if (op.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
            gd = false;
        }
        if (op.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
            gs = false;
        }
    }
    if (bj && gd && gs) {
        return 0;
    }
    if (bj && !gd && !gs) {
        return 1;
    }
    if (gd && !bj && !gs) {
        return 2;
    }
    if (gs && !bj && !gd) {
    	return 4;
    }
    if (bj && gd && !gs){
    	return 5;
    }
    if (bj && gs && !gd){
    	return 6;
    }
    if (gd && gs && !bj){
    	return 7;
    }
    return 3;
}
%>
<%
    try {

		String date = DateUtil.getBackFromDate(DateUtil.getNowDateStr(), 40);
		int id = StatUtil.getDateTimeFirstOrderId(date);
		if(id <= 0){
			id = StatUtil.getDayOrderId(date);
		}

    	String sql;
    	ResultSet rs;

		HashMap orderTypeMap = new HashMap();
		rs = st.executeQuery("select id,name from user_order_type");
		while(rs.next()){
			String type = String.valueOf(rs.getInt(1));
			String name = rs.getString(2);
			orderTypeMap.put(type,name);
		}

		// 计算全库 能否发挥
		int noStockCount = 0;
		int hasStockCount = 0;
		int hasStockGSCount = 0;
		sql = "select * from user_order uo join user_order_status uos on uo.status=uos.id left outer join order_stock os on uo.code=os.order_code where uo.id > "+id+" and uo.status in (3,6,9,12,14) and (os.status is null or os.status=0) order by uo.id asc";
		rs = st.executeQuery(sql);
		List orderList = new ArrayList();
		while(rs.next()){
			voOrder order = new voOrder();
			order.setId(rs.getInt("uo.id"));
			order.setCode(rs.getString("uo.code"));
			order.setOrderType(rs.getInt("uo.order_type"));
			orderList.add(order);
		}
		
		//订单类型
		sql = "select id,name from user_order_type order by id asc";
		rs = st.executeQuery(sql);
		List orderTypeList = new ArrayList();
		while(rs.next()){
			OrderTypeBean type = new OrderTypeBean();
			type.setId(rs.getInt("id"));
			int x = rs.getString("name").indexOf("订单");
			if( x != -1 ) {
				type.setName(rs.getString("name").substring(0,rs.getString("name").indexOf("订单")));
			} else {
				type.setName(rs.getString("name"));
			}
			orderTypeList.add(type);
		}
		OrderTypeBean type = new OrderTypeBean();
		type.setId(9);
		type.setName("保健品及其他");
		orderTypeList.add(type);

		Iterator orderIter = orderList.listIterator();
		List hasStockCountList = new ArrayList();
		List hasStockCountGSList = new ArrayList();
		List noStockCountList = new ArrayList();
		HashMap orderProductMap = new HashMap();
		HashMap orderProductGSMap = new HashMap();
		HashMap orderProductGFMap = new HashMap();
		HashMap hasStockMap = new HashMap();
		HashMap hasStockGSMap = new HashMap();
		HashMap noStockMap = new HashMap();
		HashMap orderProductStockMap = new HashMap();
		HashMap orderCodeMap = new HashMap();
		HashMap productMap = new HashMap();
		while(orderIter.hasNext()){
			voOrder order = (voOrder) orderIter.next();

			List orderProductList = adminService.getOrderProducts(order.getId());
			List orderPresentList = adminService.getOrderPresents(order.getId());
			orderProductList.addAll(orderPresentList);
			List detailList = new ArrayList();
			Iterator detailIter = orderProductList.listIterator();
			while(detailIter.hasNext()){
				voOrderProduct vop = (voOrderProduct)detailIter.next();
				voProduct product = adminService.getProduct(vop.getProductId());
        		
				if(product.getIsPackage() == 1){ // 如果这个产品是套装
					List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
					Iterator ppIter = ppList.listIterator();
					while(ppIter.hasNext()){
						ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
						voOrderProduct tempVOP = new voOrderProduct();
						tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
						voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
						tempVOP.setProductId(ppBean.getProductId());
						tempVOP.setCode(tempProduct.getCode());
						tempVOP.setName(tempProduct.getName());
						tempVOP.setPrice(tempProduct.getPrice());
						tempVOP.setOriname(tempProduct.getOriname());
						tempVOP.setPsList(psService.getProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
						detailList.add(tempVOP);
						
						if(productMap.get(tempProduct.getCode())==null){
        					productMap.put(tempProduct.getCode(),tempProduct);
        				}
					}
				} else {
					vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
					detailList.add(vop);
					
					if(productMap.get(product.getCode())==null){
        				productMap.put(product.getCode(),product);
        			}
				}
			}

            //int ss = checkStock(orderProductList);
            //库存检验
            
			int result = 1;
			if(detailList != null){
				Iterator itr = detailList.iterator();
    			voOrderProduct op = null;
    			while (itr.hasNext()) {
        			op = (voOrderProduct) itr.next();
        			
        			//总出货量
        			if(orderProductStockMap.get(String.valueOf(op.getProductId()))==null){
        				orderProductStockMap.put(String.valueOf(op.getProductId()),String.valueOf(op.getCount()));
        			}else{
        				int count = Integer.parseInt(String.valueOf(orderProductStockMap.get(String.valueOf(op.getProductId()))));
        				orderProductStockMap.put(String.valueOf(op.getProductId()),String.valueOf(count+op.getCount()));
        			}
        			
        			if(orderProductMap.get(String.valueOf(op.getProductId()))==null){
        				orderProductMap.put(String.valueOf(op.getProductId()),String.valueOf(op.getStockAllType(ProductStockBean.STOCKTYPE_QUALIFIED)));
        			}
        			if(orderProductGSMap.get(String.valueOf(op.getProductId()))==null){
        				orderProductGSMap.put(String.valueOf(op.getProductId()),String.valueOf(op.getStock(ProductStockBean.AREA_GS,ProductStockBean.STOCKTYPE_QUALIFIED)));
        			}
        			if(orderProductGFMap.get(String.valueOf(op.getProductId()))==null){
        				orderProductGFMap.put(String.valueOf(op.getProductId()),String.valueOf(op.getStock(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_QUALIFIED)));
        			}
        			
        			//总库存
        			int stockCount = Integer.parseInt((String)orderProductMap.get(String.valueOf(op.getProductId())));
        			stockCount = stockCount - op.getCount();
        			orderProductMap.put(String.valueOf(op.getProductId()),String.valueOf(stockCount));
        			
        			//广速库存
        			int stockGSCount = Integer.parseInt((String)orderProductGSMap.get(String.valueOf(op.getProductId())));
        			int _stockGSCount = stockGSCount - op.getCount();
        			orderProductGSMap.put(String.valueOf(op.getProductId()),String.valueOf(_stockGSCount));
        			
        			//芳村库存
        			int stockGFCount = Integer.parseInt((String)orderProductGFMap.get(String.valueOf(op.getProductId())));
        			int _stockGFCount = Integer.parseInt((String)orderProductGFMap.get(String.valueOf(op.getProductId())));
        			if(stockGSCount > 0 && _stockGSCount < 0){
        				stockGFCount = stockGFCount + _stockGSCount;
        			}else if(stockGSCount < 0){
        				stockGFCount = stockGFCount - op.getCount();
        			}
        			_stockGFCount = _stockGFCount - op.getCount();
        			orderProductGFMap.put(String.valueOf(op.getProductId()),String.valueOf(stockGFCount));
        			
        			if (_stockGSCount < 0 && _stockGFCount < 0) {
        				if(stockCount < 0){
        					result = -1;
        					
        					//订单编号
        					if(orderCodeMap.get(String.valueOf(op.getProductId()))==null){
        						orderCodeMap.put(String.valueOf(op.getProductId()),order.getCode());
        					}else{
        						String code = String.valueOf(orderCodeMap.get(String.valueOf(op.getProductId())));
        						orderCodeMap.put(String.valueOf(op.getProductId()),code+"<br/>"+order.getCode());
        					}
        				}else{
        					result = 0;
        				}
        			}
    			}
			}
            if(result == 1){
            	hasStockGSCount++;
            	hasStockCountGSList.add(order);
            	
            	if(hasStockGSMap.get(String.valueOf(order.getOrderType()))==null){
            		hasStockGSMap.put(String.valueOf(order.getOrderType()),String.valueOf(1));
            	}else{
            		int count = Integer.parseInt((String)hasStockGSMap.get(String.valueOf(order.getOrderType())));
            		count++;
            		hasStockGSMap.put(String.valueOf(order.getOrderType()),String.valueOf(count));
            	}
            } else if(result == 0){
            	hasStockCount++;
            	hasStockCountList.add(order);
            	
            	if(hasStockMap.get(String.valueOf(order.getOrderType()))==null){
            		hasStockMap.put(String.valueOf(order.getOrderType()),String.valueOf(1));
            	}else{
            		int count = Integer.parseInt((String)hasStockMap.get(String.valueOf(order.getOrderType())));
            		count++;
            		hasStockMap.put(String.valueOf(order.getOrderType()),String.valueOf(count));
            	}
            } else {
            	noStockCount++;
            	noStockCountList.add(order);
            	
            	if(noStockMap.get(String.valueOf(order.getOrderType()))==null){
            		noStockMap.put(String.valueOf(order.getOrderType()),String.valueOf(1));
            	}else{
            		int count = Integer.parseInt((String)noStockMap.get(String.valueOf(order.getOrderType())));
            		count++;
            		noStockMap.put(String.valueOf(order.getOrderType()),String.valueOf(count));
            	}
            }
		}

		rs.close();
	
	%>
<table width="120%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="center" border="1">
	<tr bgcolor="#4688D6">
		<td width="5%" align="center"><font color="#FFFFFF">序号</font></td>
		<td width="8%" align="center"><font color="#FFFFFF">产品线</font></td>
        <td width="15%" align="center"><font color="#FFFFFF">产品名称</font></td>
        <td width="15%" align="center"><font color="#FFFFFF">原名称</font></td>
        <td width="8%" align="center"><font color="#FFFFFF">产品编号</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">广速出库总量</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">广速缺货量</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">广速合格库可用量</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">广速合格库冻结量</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">芳村合格库可用量</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">芳村合格库冻结量</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">芳村待验库可用量</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">芳村待验库冻结量</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">增城合格库可用量</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">增城合格库冻结量</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">增城待验库可用量</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">增城待验库冻结量</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">预计到货时间</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">采购负责人</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">在途量</font></td>
        <td width="20%" align="left"><font color="#FFFFFF">订单</font></td>
	</tr>
	<%
	List productList = new ArrayList(productMap.entrySet());
	for(int i=0;i<productList.size();i++){
		Map.Entry entry = (Map.Entry) productList.get(i); 
		voProduct product = (voProduct)entry.getValue();
		
		//产品类别
		String productType = null;
		productType = (String)ProductLineMap.getProductLineMap().get(Integer.valueOf(product.getParentId1()));
		if(productType == null){
			productType = (String)ProductLineMap.getProductLineMap().get(Integer.valueOf(product.getParentId2()));
		}
		if(productType == null){
			productType = "无";
		}
		
		product.setProductLineName(productType);
	}
	//根据产品线排序
	Collections.sort(productList, new Comparator() {
   		public int compare(Object o1, Object o2) {
    		Map.Entry obj1 = (Map.Entry) o1;
    		voProduct p1 = (voProduct)obj1.getValue();
   			Map.Entry obj2 = (Map.Entry) o2;
   			voProduct p2 = (voProduct)obj2.getValue();
   		 return p1.getProductLineName().compareTo(p2.getProductLineName());
   		}
  	});
 
  	List productListTemp = new ArrayList();
  	for(int i=0;i<productList.size();i++){
		Map.Entry entry = (Map.Entry) productList.get(i); 
		voProduct product = (voProduct)entry.getValue();
		
		if(productIdList.contains(String.valueOf(product.getId()))){
			productListTemp.add(entry);
		}
	}
	productList = productListTemp;
	
  	
	Iterator productIter = productList.iterator();
	int index = 0;
	while(productIter.hasNext()){
		
		Map.Entry entry = (Map.Entry) productIter.next(); 
		voProduct product = (voProduct)entry.getValue();
		product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, null));
		
		if(Integer.parseInt(String.valueOf(orderProductMap.get(String.valueOf(product.getId())))) >= 0 ){
			continue;
		}
		
		//在途量
		int buyCountGD = 0;
		String condition = "product_id="+product.getId()+" and buy_order_id in (select id from buy_order where " +
						"status = "+BuyOrderBean.STATUS3+" or status ="+BuyOrderBean.STATUS5+")";
		ArrayList bopList = service.getBuyOrderProductList(condition, -1, -1, null);
		Iterator bopIterator = bopList.listIterator();
		while(bopIterator.hasNext()){
			BuyOrderProductBean bop = (BuyOrderProductBean)bopIterator.next();
			buyCountGD += (bop.getOrderCountGD()-bop.getStockinCountGD())>0?(bop.getOrderCountGD()-bop.getStockinCountGD()):0;
		}
		//如果在途量大于0，需要查找  该采购产品的         预计到货时间 及 采购负责人
		String expectArrivalDatetime = "";
		String createUserName = "";
		if(buyCountGD>0){
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<bopList.size();i++){
				BuyOrderProductBean bop = (BuyOrderProductBean)(bopList.get(i));
				sb.append(bop.getBuyOrderId()+",");
			}
			String buyOrderIds = null;
			if(sb.length()>0){
				buyOrderIds = sb.substring(0, sb.length()-1);
			}
			if(buyOrderIds!=null&&buyOrderIds.trim().length()>0){
				BuyStockBean buyStock =  service.getBuyStock(" buy_order_id in ("+buyOrderIds +") and expect_arrival_datetime >= '" + DateUtil.getNow()+"' and status != "+BuyStockBean.STATUS8+" and status != "+BuyStockBean.STATUS6+ " order by expect_arrival_datetime asc limit 0,1 ");
				if(buyStock==null){
					buyStock =  service.getBuyStock(" buy_order_id in ("+buyOrderIds +") and status != "+BuyStockBean.STATUS8+" and status != "+BuyStockBean.STATUS6+ " order by expect_arrival_datetime desc limit 0,1 ");
				}
				if(buyStock != null){
					expectArrivalDatetime = buyStock.getExpectArrivalDatetime();
					if(expectArrivalDatetime!=null&&expectArrivalDatetime.trim().length()>0){
						expectArrivalDatetime = expectArrivalDatetime.substring(0,10);
					}else{
						expectArrivalDatetime = "";
					}
					//操作人
					voUser creatUser = adminService.getAdminUser(buyStock.getCreateUserId());
					if(creatUser!=null){
						createUserName = creatUser.getUsername();
					}
				}
			}
		}			
		index++;
 %>
	<tr>
		<td width="5%" align="center"><%=index %></td>
		<td width="8%" align="center"><%=product.getProductLineName() %></td>
        <td width="20%" align="center"><%=product.getName() %></td>
        <td width="20%" align="center"><%=product.getOriname() %></td>
        <td width="8%" align="center"><%=product.getCode() %></td>
        <td width="7%" align="center"><%=orderProductStockMap.get(String.valueOf(product.getId())) %></td>
        <td width="7%" align="center">
        	<%
        		int c = Integer.parseInt(String.valueOf(orderProductMap.get(String.valueOf(product.getId()))));
        		if(c >= 0){
        			c = 0;
        		}else{
        			c = Math.abs(c);
        		}
        	 %>
        	<%=c %>
        </td>
        <td width="7%" align="center"><%=product.getStock(ProductStockBean.AREA_GS,ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
        <td width="7%" align="center"><%=product.getLockCount(ProductStockBean.AREA_GS,ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
        <td width="7%" align="center"><%=product.getStock(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
        <td width="7%" align="center"><%=product.getLockCount(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
        <td width="7%" align="center"><%=product.getStock(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_CHECK) %></td>
        <td width="7%" align="center"><%=product.getLockCount(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_CHECK) %></td>
        <td width="7%" align="center"><%=product.getStock(ProductStockBean.AREA_ZC,ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
        <td width="7%" align="center"><%=product.getLockCount(ProductStockBean.AREA_ZC,ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
        <td width="7%" align="center"><%=product.getStock(ProductStockBean.AREA_ZC,ProductStockBean.STOCKTYPE_CHECK) %></td>
        <td width="7%" align="center"><%=product.getLockCount(ProductStockBean.AREA_ZC,ProductStockBean.STOCKTYPE_CHECK) %></td>
        <td width="7%" align="center"><%=expectArrivalDatetime %></td>
        <td width="7%" align="center"><%=createUserName %></td>
        <td width="7%" align="center"><%=buyCountGD %></td>
        <td width="20%" align="left"><%=orderCodeMap.get(String.valueOf(product.getId()))==null?"":((String)orderCodeMap.get(String.valueOf(product.getId()))).replace("<br/>","、") %></td>
	</tr>
 <%}%>
  </table>
<%
 } catch (Exception e) {
    e.printStackTrace();
}
  %>

</osCache:cache>
<%
	st.close();
} catch(Exception e){
	e.printStackTrace();
} finally {
	// 释放 大查询锁
	DbLock.slaveServerQueryLock.unlock();

	if(conn != null)
		conn.close();
	if(service != null)
		service.releaseAll();
	if(adminService != null)
		adminService.close();
}
%>
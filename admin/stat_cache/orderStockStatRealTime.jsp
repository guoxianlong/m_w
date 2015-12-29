<%@page import="adultadmin.util.db.DbOperation"%>
<%@page import="java.util.Map.Entry"%><%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,adultadmin.util.db.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.*,ormap.ProductLineMap" %>
<%@ page import="adultadmin.bean.*, adultadmin.bean.order.*, adultadmin.bean.stock.*,adultadmin.bean.buy.*"%>
<%@ page import="adultadmin.service.*, adultadmin.service.infc.*,com.primit.cache.ProductLinePermissionCache" %>
<%
	response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);

	voUser user = (voUser)session.getAttribute("userView");

	//数据库大查询锁，等待3秒
	if (!DbLock.slaveServerQueryLocked(100)) {
		response.sendRedirect(request.getContextPath()+"/tip.jsp?db=adult_slave");
		return;
	}

    Connection conn = null;
	Statement st = null;
	IStockService service = null;
    IProductPackageService ppService = null;
    IProductStockService psService = null;
    IAdminService adminService = null;
    try{
    
    String productLine = StringUtil.convertNull(request.getParameter("productLine"));
	productLine = Encoder.decrypt(productLine);//解码为中文
	if(productLine==null){//解码失败,表示已经为中文,则返回默认
		productLine = StringUtil.dealParam(request.getParameter("productLine"));//名称
	}
	if (productLine==null) productLine="";
	boolean markProductList = StringUtil.toBoolean(request.getParameter("markProductList"));

    DbLock.slaveServerOperator = user.getUsername() + "_即时发货状态统计_" + DateUtil.getNow();

    conn = DbUtil.getConnection(DbOperation.DB_SLAVE2);
    st = conn.createStatement();
	service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
    ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
    psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
    adminService = ServiceFactory.createAdminServiceLBJ();

    UserGroupBean group = user.getGroup();

	boolean isSystem = (user.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (user.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (user.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (user.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (user.getPermission() == 7);	//销售部
	boolean isShangpin = (user.getPermission() == 6);	//商品部
	boolean isTuiguang = (user.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (user.getPermission() == 4);	//运营中心
	boolean isKefu = (user.getPermission() == 3);	//客服部

	boolean padmin = group.isFlag(74);
%>
<html>
<title>买卖宝后台</title>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<style>
.popup1 {
	font-size: 12px;
	cursor: default;
	text-decoration: none;
	color: #0000FF;
	width: 100px;
	border: 1px solid #ffffff;
}

.popup1 img{
	border:0;
	filter:	Alpha(Opacity=70);
}

.popup1:hover {
	/*	border-top: 1px solid buttonhighlight;
	border-left: 1px solid buttonhighlight;
	border-bottom: 1px solid buttonshadow;
	border-right: 1px solid buttonshadow;*/
	border: 1px solid #0A246A;
	cursor: default;
	background-color: #FFEEC2;
	text-decoration: none;
	color: #000000;
	width: 100px;
}

.popup1_hover {
	/*	border-top: 1px solid buttonhighlight;
	border-left: 1px solid buttonhighlight;
	border-bottom: 1px solid buttonshadow;
	border-right: 1px solid buttonshadow;*/
	border: 1px solid #0A246A;
	cursor: default;
	background-color: #FFEEC2;
	text-decoration: none;
	color: #000000;
	width: 100px;
}

.popup1_hover img{
	border:0;
	filter:	Alpha(Opacity=100);
}

.msviLocalToolbar{
border:solid 1px #999;
background:#F1F1F1;

padding:2px 0px 1px 0px;

}
</style>
<body>
<%if(padmin){%><script type="text/javascript" src="../ajax/js/jquery.js"></script><%}%>
<script type="text/javascript">
var markOrderList = false;
function orderList(){
	if(!markOrderList){
		document.getElementById('orderList').style.display = 'block';
		markOrderList = true;
	}else if(markOrderList){
		document.getElementById('orderList').style.display = 'none';
		markOrderList = false;
	}
	
}
var markProductList = false;
function productList(){
	if(!markProductList){
		document.getElementById('productList').style.display = 'block';
		markProductList = true;
	}else if(markProductList){
		document.getElementById('productList').style.display = 'none';
		markProductList = false;
	}
	
}
function selectProductLine(){
	window.location = "orderStockStatRealTime.jsp?markProductList=true&productLine="+document.getElementById("productLine").value;
}
function reserveCheck(name){
  var names=document.getElementsByName(name);
  var len=names.length;
 if(len>0){
 	var i=0;
    for(i=0;i<len;i++){
     names[i].checked=true;
    }
 } 
}
<%if(padmin){	// ajax 快捷商品下架
%>
function hidePopup() {
	document.getElementById('popup').style.display = "none";
}
var fromA,fromAid;
function pstatus(from,uid,name){
//	if(!from||!confirm('确认要下架商品['+name+']?'))
//		return;
	fromA=from;
	fromAid=uid;
	
	var e = window.event;
	var popup = document.getElementById('popup');
	document.getElementById('popupTitle').innerText=name
	
    popup.style.left = e.clientX + document.body.scrollLeft;
    popup.style.top = e.clientY + document.body.scrollTop;
    popup.style.display = "";
	
	return false;
}
function pstatus2(st){
	fromA.style.color='orange'
	$.get('../ajax/stat/productStatus.jsp?id='+fromAid+'&status='+st, function(data) {
  		fromA.style.display='none'
	});
}
<%}%>
</script>
<a href="#" onclick="orderList()">可发货订单类别统计</a>&nbsp;&nbsp;
<%if(group.isFlag(258)){ %>
<a href="#" onclick="productList()">缺货产品列表</a>
<%} %>
<br/>
<table class="msviLocalToolbar" id="popup" width="100"
	style="display: none; position:absolute; z-index:100;
	background-color:white;">

<tr><tr><td id="popupTitle">商品名</td></tr>
<tr>
<td onclick="hidePopup();pstatus2(120);" class="popup1" onMouseOver="this.className='popup1_hover';"
            onMouseOut="this.className='popup1';">
隐藏该商品</td>
</tr>
<tr>
<td onclick="hidePopup();pstatus2(100);" class="popup1" onMouseOver="this.className='popup1_hover';"
            onMouseOut="this.className='popup1';">
下架该商品</td>
</tr>
<tr>
<td onclick="hidePopup();" class="popup1" onMouseOver="this.className='popup1_hover';"
            onMouseOut="this.className='popup1';">
取消</td>
</tr>
</table>
<osCache:cache scope="application" time="3600">
<%@include file="../../header.jsp"%>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center>成交但没有发货的总订单</td>
		<td align=center>没有“申请出库”的订单</td>
		<td align=center>可直接发货的订单</td>
		<td align=center>可调拨发货的订单</td>
		<td align=center>全库缺货的订单</td>
		<td align=center>能发货的待出库订单</td>
		<td align=center>复核中的订单</td>
	</tr>
<%!
int checkStock(List orderProductList) {
    if (orderProductList == null) {
        return 3;
    }

    Iterator itr = orderProductList.iterator();
    boolean zc = true;
    boolean gd = true;
    boolean gs = true;
    voOrderProduct op = null;
    while (itr.hasNext()) {
        op = (voOrderProduct) itr.next();
        if (op.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
            zc = false;
        }
        if (op.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
            gd = false;
        }
        if (op.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
            gs = false;
        }
    }
    if (zc && gd && gs) {
        return 0;
    }
    if (zc && !gd && !gs) {
        return 1;
    }
    if (gd && !zc && !gs) {
        return 2;
    }
    if (gs && !zc && !gd) {
    	return 4;
    }
    if (zc && gd && !gs){
    	return 5;
    }
    if (zc && gs && !gd){
    	return 6;
    }
    if (gd && gs && !zc){
    	return 7;
    }
    return 3;
}
%>
<%--
List hasStockCountList = new ArrayList();
List noStockCountList = new ArrayList();
HashMap orderProductMap = new HashMap();
boolean checkAllStock(List orderProductList) {
    if (orderProductList == null) {
        return true;
    }

    Iterator itr = orderProductList.iterator();
    boolean result = true;
    voOrderProduct op = null;
    while (itr.hasNext()) {
        op = (voOrderProduct) itr.next();
        if(orderProductMap.get(""+op.getProductId())==null){
        	orderProductMap.put(""+op.getProductId(),""+op.getStockAllType(ProductStockBean.STOCKTYPE_QUALIFIED));
        }
        int stockCount = Integer.parseInt((String)orderProductMap.get(""+op.getProductId()));
        stockCount = stockCount - op.getCount();
        orderProductMap.put(""+op.getProductId(),""+stockCount);
        if (stockCount < 0	) {
            result = false;
        }
    }
    return result;
}--%>
<%
		String date = DateUtil.getBackFromDate(DateUtil.getNowDateStr(), 40);
		int id = StatUtil.getDateTimeFirstOrderId(date);
		if(id <= 0){
			id = StatUtil.getDayOrderId(date);
		}

    	String sql;
    	ResultSet rs;
    	System.out.println("start ... "+DateUtil.getNow());
		HashMap orderTypeMap = new HashMap();
		rs = st.executeQuery("select id,name from user_order_type");
		while(rs.next()){
			String type = String.valueOf(rs.getInt(1));
			String name = rs.getString(2);
			orderTypeMap.put(type,name);
		}

    	// 成交但没有发货的总订单：成交订单中没有出库的订单（订单状态为：3,6,9,12,14、发货状态为：为处理、处理中、复核）
    	sql = "select count(*) from user_order uo left outer join order_stock os on uo.id=os.order_id where uo.id > "+id+" and uo.status in (3,6,9,12,14) and (os.status is null or os.status in (0,1,5))";
		int noStockoutCount = 0;
		rs = st.executeQuery(sql);
		if(rs.next()){
			noStockoutCount = rs.getInt(1);
		}

		// 没有“申请出库”的订单：还没有操作“申请出货”的订单（订单状态：3、没有发货记录）
    	sql = "select count(*) from user_order uo left outer join order_stock os on uo.id=os.order_id where uo.id > "+id+" and uo.status in (3,6,9,12,14) and (os.status is null)";
		int noOrderStockCount = 0;
		rs = st.executeQuery(sql);
		if(rs.next()){
			noOrderStockCount = rs.getInt(1);
		}

		// 能发货的待出库订单：库存满足但没有发货的订单（订单发货状态：待发货、排除复核状态的）
		sql = "select count(*) from user_order uo join order_stock os on uo.code=os.order_code where uo.id > "+id+" and uo.status in (3,6,9,12,14) and os.status in (1)";
		int stockReadyCount = 0;
		rs = st.executeQuery(sql);
		if(rs.next()){
			stockReadyCount = rs.getInt(1);
		}

		// 复核中的订单：导完订单，取完货正在复核中的订单（订单发货状态：复核）
		sql = "select count(*) from user_order uo join order_stock os on uo.code=os.order_code where uo.id > "+id+" and uo.status in (3,6,9,12,14) and os.status in (5)";
		int stockRecheckCount = 0;
		rs = st.executeQuery(sql);
		if(rs.next()){
			stockRecheckCount = rs.getInt(1);
		}

		// 计算全库 能否发货
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
			type.setName(rs.getString("name").substring(0,rs.getString("name").indexOf("订单")==-1?rs.getString("name").length():rs.getString("name").indexOf("订单")));
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
		HashMap orderProductZCMap = new HashMap();
		HashMap hasStockMap = new HashMap();
		HashMap hasStockGSMap = new HashMap();
		HashMap noStockMap = new HashMap();
		HashMap orderProductStockMap = new HashMap();
		HashMap orderCodeMap = new HashMap();
		HashMap orderIdMap = new HashMap();
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
        			if(orderProductZCMap.get(String.valueOf(op.getProductId()))==null){
        				orderProductZCMap.put(String.valueOf(op.getProductId()),String.valueOf(op.getStock(ProductStockBean.AREA_ZC,ProductStockBean.STOCKTYPE_QUALIFIED)));
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
        			
        			//增城库存
        			int stockZCCount = Integer.parseInt((String)orderProductZCMap.get(String.valueOf(op.getProductId())));
        			int _stockZCCount = stockZCCount - op.getCount();
        			orderProductZCMap.put(String.valueOf(op.getProductId()),String.valueOf(_stockZCCount));
        			
        			if (_stockGSCount < 0 && _stockGFCount < 0 && _stockZCCount < 0) {
        				if(stockCount < 0){
        					result = -1;
        					
        					//订单编号  orderIdMap
        					if(orderCodeMap.get(String.valueOf(op.getProductId()))==null){
        						orderCodeMap.put(String.valueOf(op.getProductId()),order.getCode());
        					}else{
        						String code = String.valueOf(orderCodeMap.get(String.valueOf(op.getProductId())));
        						orderCodeMap.put(String.valueOf(op.getProductId()),code+"<br/>"+order.getCode());
        					}
        					
        					if(orderIdMap.get(String.valueOf(op.getProductId()))==null){
        						orderIdMap.put(String.valueOf(op.getProductId()),String.valueOf(order.getId()));
        					}else{
        						String idStr = String.valueOf(orderIdMap.get(String.valueOf(op.getProductId())));
        						orderIdMap.put(String.valueOf(op.getProductId()),idStr+","+order.getId());
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

%>
	<tr bgcolor='#F8F8F8'>
	    <td align=center><%= noStockoutCount %></td>
		<td align=center><%= noOrderStockCount %></td>
		<td align=center><%= hasStockGSCount %></td>
		<td align=center><%= hasStockCount %></td>
		<td align=center><%= noStockCount %></td>
		<td align=center><%= stockReadyCount %></td>
		<td align=center><%= stockRecheckCount %></td>
	</tr>
<%
		rs.close();
	
	%>
	
</table>
<pre>
概念说明：
1、成交但没有发货的总订单：成交订单中没有出库的订单
2、没有“申请出库”的订单：还没有操作“申请出货”的订单
3、可直接发货订单：广速库存满足订单中商品的需求量，可以直接发货的订单数<br/>（订单状态：待发货,已发货、订单未申请出货或订单出货状态为未处理、广速库存数量大于等于订单中货品数量）；<br/>订单中商品的需求量根据订单的生成时间累积计算
4、调拨可发货的订单：增城库存不满足订单中的商品需求量，但是通过库房各地域之间的调拨能发货的订单数<br/>（订单状态：待发货,已发货、订单未申请出货或订单出货状态为未处理、增城库存数量小于订单中货品数量、全库存数量大于等于订单中货品数量）；<br/>订单中商品的需求量根据订单的生成时间累积计算
5、全库缺货的订单：库存不足，需要通过采购入库才能发货的订单；订单中商品的需求量根据订单的生成时间累积计算
6、能发货的待出库订单：已冻结库存但没有出库的订单(不包含复核中的)
7、复核中的订单：导完订单，取完货正在复核中的订单
</pre>
<div id="orderList" style="display:none;">
<%-- <table width="50%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="center">
	<tr bgcolor="#4688D6">
        <td width="20%" align="center"><font color="#FFFFFF">订单商品类型</font></td>
        <td width="20%" align="center"><font color="#FFFFFF">可直接发货</font></td>
        <td width="20%" align="center"><font color="#FFFFFF">可调拨发货</font></td>
        <td width="20%" align="center"><font color="#FFFFFF">全库缺货</font></td>
        <td width="20%" align="center"><font color="#FFFFFF">总计</font></td>
	</tr>
	<%
	Iterator hasStockGSIter = hasStockCountGSList.listIterator();
	while(hasStockGSIter.hasNext()){
		voOrder order = (voOrder)hasStockGSIter.next();
 %>
	<tr>
		<td width="20%" align="center"><%=order.getCode() %></font></td>
        <td width="20%" align="center"><%=orderTypeMap.get(String.valueOf(order.getOrderType()))==null?"其他订单":orderTypeMap.get(String.valueOf(order.getOrderType())) %></td>
        <td width="20%" align="center">可直接发货</td>
	</tr>
<%}
	Iterator hasStockIter = hasStockCountList.listIterator();
	while(hasStockIter.hasNext()){
		voOrder order = (voOrder)hasStockIter.next();
 %>
	<tr>
		<td width="20%" align="center"><%=order.getCode() %></font></td>
        <td width="20%" align="center"><%=orderTypeMap.get(String.valueOf(order.getOrderType()))==null?"其他订单":orderTypeMap.get(String.valueOf(order.getOrderType())) %></td>
        <td width="20%" align="center">可调拨发货</td>
	</tr>
 <%}
	Iterator noStockIter = noStockCountList.listIterator();
	while(noStockIter.hasNext()){
		voOrder order = (voOrder)noStockIter.next();
 %>
	<tr>
		<td width="20%" align="center"><%=order.getCode() %></font></td>
        <td width="20%" align="center"><%=orderTypeMap.get(String.valueOf(order.getOrderType()))==null?"其他订单":orderTypeMap.get(String.valueOf(order.getOrderType())) %></td>
        <td width="20%" align="center">不可发货</td>
	</tr>
 <%}%>
 </table>--%>
 <table width="50%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="center">
	<tr bgcolor="#4688D6">
        <td width="20%" align="center"><font color="#FFFFFF">订单商品类型</font></td>
        <td width="20%" align="center"><font color="#FFFFFF">可直接发货</font></td>
        <td width="20%" align="center"><font color="#FFFFFF">可调拨发货</font></td>
        <td width="20%" align="center"><font color="#FFFFFF">全库缺货</font></td>
        <td width="20%" align="center"><font color="#FFFFFF">总计</font></td>
	</tr>
	<%
	int hasStockGSTotal = 0;
	int hasStockTotal = 0;
	int noStockTotal = 0;
	int allTotal = 0;
	Iterator orderTypeIter = orderTypeList.listIterator();
	while(orderTypeIter.hasNext()){
		OrderTypeBean orderType = (OrderTypeBean)orderTypeIter.next();
		int hasStockGS = 0;
		int hasStock = 0;
		int noStock = 0;
		
		if(hasStockGSMap.get(String.valueOf(orderType.getId()))!=null){
			hasStockGS = Integer.parseInt((String)hasStockGSMap.get(String.valueOf(orderType.getId())));
		}
		if(hasStockMap.get(String.valueOf(orderType.getId()))!=null){
			hasStock = Integer.parseInt((String)hasStockMap.get(String.valueOf(orderType.getId())));
		}
		if(noStockMap.get(String.valueOf(orderType.getId()))!=null){
			noStock = Integer.parseInt((String)noStockMap.get(String.valueOf(orderType.getId())));
		}
		
		hasStockGSTotal = hasStockGSTotal+hasStockGS;
		hasStockTotal = hasStockTotal+hasStock;
		noStockTotal = noStockTotal+noStock;
		allTotal = allTotal+(hasStockGS+hasStock+noStock);
		
	%>
	<tr>
		<td width="20%" align="center"><%=orderType.getName() %></td>
        <td width="20%" align="center"><%=hasStockGS %></td>
        <td width="20%" align="center"><%=hasStock %></td>
        <td width="20%" align="center"><%=noStock %></td>
        <td width="20%" align="center"><%=hasStockGS+hasStock+noStock %></td>
	</tr>
	<%} %>
	<tr>
		<td width="20%" align="center">合计</td>
        <td width="20%" align="center"><%=hasStockGSTotal %></td>
        <td width="20%" align="center"><%=hasStockTotal %></td>
        <td width="20%" align="center"><%=noStockTotal %></td>
        <td width="20%" align="center"><%=allTotal %></td>
        
	</tr>
</table>
</div>
<br/><br/>
<div id="productList" style="display:none;">
<%
	List lineList=ProductLinePermissionCache.getAllProductLineList();
%>
<form action="printOrderStockStatRealTime.jsp" method="post">
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="center">
	<tr>
		<td colspan="12" align="left">
		<select name="productLine" id="productLine" onChange="selectProductLine();">
			<option value="">全部</option>
		<%for(Iterator i=lineList.iterator();i.hasNext();){
			voProductLine line = (voProductLine)i.next();
		%>	
			<option value="<%=Encoder.encrypt(line.getName())%>"><%=line.getName()%></option>
		<%}%>	
		</select>
		<script>selectOption(document.getElementById('productLine'), '<%=Encoder.encrypt(productLine)%>');</script>
		</td>
	</tr>
	<tr>
		<td colspan="12" align="left">
		<input type="submit" value="导出勾选项"/>
		</td>
	</tr>
	<tr bgcolor="#4688D6">
		<td width="3%" align="center"><input type="button" value="全选" onclick="reserveCheck('productId')"/></td>
		<td width="5%" align="center"><font color="#FFFFFF">序号</font></td>
		<td width="8%" align="center"><font color="#FFFFFF">产品线</font></td>
        <td width="15%" align="center"><font color="#FFFFFF">产品名称</font></td>
        <td width="15%" align="center"><font color="#FFFFFF">缺货处理意见</font></td>
        <td width="15%" align="center"><font color="#FFFFFF">原名称</font></td>
        <td width="8%" align="center"><font color="#FFFFFF">产品编号</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">广速出库总量</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">广速缺货量</font></td> 
       <!--<td width="7%" align="center"><font color="#FFFFFF">广速合格库可用量(冻结量)</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">芳村合格库可用量(冻结量)</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">芳村待验库可用量(冻结量)</font></td> --> 
        <td width="7%" align="center"><font color="#FFFFFF">增城合格库可用量(冻结量)</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">增城待验库可用量(冻结量)</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">预计到货时间</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">采购负责人</font></td>
        <td width="7%" align="center"><font color="#FFFFFF">在途量</font></td>
        <td width="20%" align="center"><font color="#FFFFFF">订单</font></td>
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
		
		//产品线
		
		
		product.setProductLineName(productType);
	}
//	//根据产品线排序
//	Collections.sort(productList, new Comparator() {
//   		public int compare(Object o1, Object o2) {
//    		Map.Entry obj1 = (Map.Entry) o1;
//    		voProduct p1 = (voProduct)obj1.getValue();
//   			Map.Entry obj2 = (Map.Entry) o2;
//   			voProduct p2 = (voProduct)obj2.getValue();
//   		 return p1.getProductLineName().compareTo(p2.getProductLineName());
//   		}
//  	});

	//根据缺货量排序
	final HashMap tempMap=orderProductMap;
	Collections.sort(productList, new Comparator() {
   		public int compare(Object o1, Object o2) {
   			
    		Map.Entry obj1 = (Map.Entry) o1;
    		voProduct p1 = (voProduct)obj1.getValue();
   			Map.Entry obj2 = (Map.Entry) o2;
   			voProduct p2 = (voProduct)obj2.getValue();
   			int c1=Integer.parseInt(String.valueOf(tempMap.get(String.valueOf(p1.getId()))));
   			int c2=Integer.parseInt(String.valueOf(tempMap.get(String.valueOf(p2.getId()))));
   			if(c1>c2){
   				return 1;
   			}else{
   				return -1;
   			}
   		}
  	});
  	//产品线筛选
  	if(!productLine.equals("")){
  		List productListTemp = new ArrayList();
	  	for(int i=0;i<productList.size();i++){
			Map.Entry entry = (Map.Entry) productList.get(i); 
			voProduct product = (voProduct)entry.getValue();
			
			if(product.getProductLineName().equals(productLine)){
				productListTemp.add(entry);
			}
		}
		productList = productListTemp;
	}
  	
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
			buyCountGD += (bop.getOrderCountGD()-bop.getStockinCountGD()-bop.getStockinCountBJ())>0?(bop.getOrderCountGD()-bop.getStockinCountGD()-bop.getStockinCountBJ()):0;
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
		
		//缺货处理意见
		String lackRemark = "";
		String remarks = product.getRemark();
		if(remarks!=null){
			String[] remarksArr =  remarks.split("\r\n");
			lackRemark = remarksArr[0];
		}
		if(lackRemark.indexOf(".  ")>0){
			lackRemark = lackRemark.substring(lackRemark.indexOf(".  ")+3,lackRemark.length());
		}
		
 %>
	<tr>
		<td width="3%" align="center"><input type="checkbox" name="productId" value="<%=product.getId()%>"/></td>
		<td width="5%" align="center"><%=index %></td>
		<td width="8%" align="center"><%=product.getProductLineName() %></td>
        <td width="20%" align="center"><a href="../fproduct.do?id=<%=product.getId()%>"><%=product.getName() %></a> <%if(padmin&&product.getStatus()<100){%> <a href="#" onclick="return pstatus(this,<%=product.getId()%>,'<%=product.getName().replace("'","‘")%>')" style="color:red">下架</a><%}%></td>
        <td width="20%" align="center"><%=lackRemark %></td>
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
        <!--<td width="7%" align="center"><%=product.getStock(ProductStockBean.AREA_GS,ProductStockBean.STOCKTYPE_QUALIFIED) %>(<%=product.getLockCount(ProductStockBean.AREA_GS,ProductStockBean.STOCKTYPE_QUALIFIED) %>)</td>
        <td width="7%" align="center"><%=product.getStock(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_QUALIFIED) %>(<%=product.getLockCount(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_QUALIFIED) %>)</td>
        <td width="7%" align="center"><%=product.getStock(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_CHECK) %>(<%=product.getLockCount(ProductStockBean.AREA_GF,ProductStockBean.STOCKTYPE_CHECK) %>)</td> --> 
        <td width="7%" align="center"><%=product.getStock(ProductStockBean.AREA_ZC,ProductStockBean.STOCKTYPE_QUALIFIED) %>(<%=product.getLockCount(ProductStockBean.AREA_ZC,ProductStockBean.STOCKTYPE_QUALIFIED) %>)</td>
        <td width="7%" align="center"><%=product.getStock(ProductStockBean.AREA_ZC,ProductStockBean.STOCKTYPE_CHECK) %>(<%=product.getLockCount(ProductStockBean.AREA_ZC,ProductStockBean.STOCKTYPE_CHECK) %>)</td>
        <td width="7%" align="center"><%=expectArrivalDatetime %></td>
        <td width="7%" align="center"><%=createUserName %></td>
        <td width="7%" align="center"><%=buyCountGD %></td>
        <td width="20%" align="center"><%=HttpUtil.getOrderDetailsHref(orderCodeMap.get(String.valueOf(product.getId())),orderIdMap.get(String.valueOf(product.getId()))) %></td>
	</tr>
 <%}%>
  </table>
  </form>
</div>
<%if(markProductList){ %>
<script type="text/javascript">productList()</script>
<%} %>
</osCache:cache>
</body>
</html>
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
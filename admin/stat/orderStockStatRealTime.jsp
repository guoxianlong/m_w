<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.util.db.DbUtil" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*, adultadmin.bean.order.*, adultadmin.bean.stock.*" %>
<%@ page import="adultadmin.service.*, adultadmin.service.infc.*" %>
<%
	voUser user = (voUser)session.getAttribute("userView");

	//数据库大查询锁，等待3秒
	if (!DbLock.bigQueryLocked(100)) {
		response.sendRedirect(request.getContextPath()+"/tip.jsp?db=adult");
		return;
	}

    Connection conn = null;
	Statement st = null;
	IStockService service = null;
    IProductPackageService ppService = null;
    IProductStockService psService = null;
    IAdminService adminService = null;

try{
    DbLock.operator = user.getUsername() + "_即时发货状态统计_" + DateUtil.getNow();

    conn = DbUtil.getConnection("adult_slave");
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
%>
<html>
<title>买卖宝后台</title>

<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../../header.jsp"%>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center>成交但没有发货的总订单</td>
		<td align=center>没有“申请出库”的订单</td>
		<td align=center>全库之和可发货的订单</td>
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

boolean checkAllStock(List orderProductList) {
    if (orderProductList == null) {
        return true;
    }

    Iterator itr = orderProductList.iterator();
    boolean result = true;
    voOrderProduct op = null;
    while (itr.hasNext()) {
        op = (voOrderProduct) itr.next();
        if (op.getStockAllType(ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
            result = false;
        }
    }
    return result;
}
%>
<%
    try {

    	String sql;
    	ResultSet rs;

    	// 成交但没有发货的总订单：成交订单中没有出库的订单（订单状态为：3,6,9,12,14、发货状态为：为处理、处理中、复核）
    	sql = "select count(*) from user_order uo left outer join order_stock os on uo.id=os.order_id where uo.status in (3,6,9,12,14) and (os.status is null or os.status in (0,1,5)) and uo.create_datetime > '2009-01-01'";
		int noStockoutCount = 0;
		rs = st.executeQuery(sql);
		if(rs.next()){
			noStockoutCount = rs.getInt(1);
		}

		// 没有“申请出库”的订单：还没有操作“申请出货”的订单（订单状态：3、没有发货记录）
    	sql = "select count(*) from user_order uo left outer join order_stock os on uo.id=os.order_id where uo.status in (3,6,9,12,14) and (os.status is null) and uo.create_datetime > '2009-01-01'";
		int noOrderStockCount = 0;
		rs = st.executeQuery(sql);
		if(rs.next()){
			noOrderStockCount = rs.getInt(1);
		}

		// 能发货的待出库订单：库存满足但没有发货的订单（订单发货状态：待发货、排除复核状态的）
		sql = "select count(*) from user_order uo join order_stock os on uo.code=os.order_code where uo.status in (3,6,9,12,14) and os.status in (1) and uo.create_datetime > '2009-01-01'";
		int stockReadyCount = 0;
		rs = st.executeQuery(sql);
		if(rs.next()){
			stockReadyCount = rs.getInt(1);
		}

		// 复核中的订单：导完订单，取完货正在复核中的订单（订单发货状态：复核）
		sql = "select count(*) from user_order uo join order_stock os on uo.code=os.order_code where uo.status in (3,6,9,12,14) and os.status in (5) and uo.create_datetime > '2009-01-01'";
		int stockRecheckCount = 0;
		rs = st.executeQuery(sql);
		if(rs.next()){
			stockRecheckCount = rs.getInt(1);
		}

		// 计算全库 能否发挥
		int noStockCount = 0;
		int hasStockCount = 0;
		sql = "select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=uo.id) products from user_order uo join user_order_status uos on uo.status=uos.id left outer join order_stock os on uo.code=os.order_code where uo.status in (3,6,9,12,14) and (os.status is null or os.status=0) and uo.create_datetime > '2009-01-01'";
		rs = st.executeQuery(sql);
		List orderList = new ArrayList();
		while(rs.next()){
			voOrder order = new voOrder();
			order.setId(rs.getInt("uo.id"));
			order.setCode(rs.getString("uo.code"));
			orderList.add(order);
		}

		Iterator orderIter = orderList.listIterator();
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
					}
				} else {
					vop.setPsList(psService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
					detailList.add(vop);
				}
			}

            //int ss = checkStock(orderProductList);
            if(checkAllStock(detailList)){
            	hasStockCount++;
            } else {
            	noStockCount++;
            }
		}

%>
	<tr bgcolor='#F8F8F8'>
	    <td align=center><a href="../statOrderList.do?menu=orderStockStatRealTime_noStockout"><%= noStockoutCount %></a></td>
		<td align=center><a href="../statOrderList.do?menu=orderStockStatRealTime_noAddStockout"><%= noOrderStockCount %></a></td>
		<td align=center><a href="../statOrderList.do?menu=orderStockStatRealTime_hasStock&stockStatus=true"><%= hasStockCount %></a></td>
		<td align=center><a href="../statOrderList.do?menu=orderStockStatRealTime_noStock&stockStatus=false"><%= noStockCount %></a></td>
		<td align=center><a href="../statOrderList.do?menu=orderStockStatRealTime_stockReady"><%= stockReadyCount %></a></td>
		<td align=center><a href="../statOrderList.do?menu=orderStockStatRealTime_stockRecheck"><%= stockRecheckCount %></a></td>
	</tr>
<%
		rs.close();
	} catch (Exception e) {e.printStackTrace();}
	%>
	
</table>
<pre>
概念说明：
1、成交但没有发货的总订单：成交订单中没有出库的订单
2、没有“申请出库”的订单：还没有操作“申请出货”的订单
3、全库之和可发货的订单：单地域库存满足或通过库房各地域之间的调拨能发货的订单数
4、全库缺货的订单：库存不足，需要通过采购入库才能发货的订单
5、能发货的待出库订单：已冻结库存但没有出库的订单(不包含复核中的)
6、复核中的订单：导完订单，取完货正在复核中的订单
</pre>
<%
	st.close();
} catch(Exception e){
	e.printStackTrace();
} finally {
	// 释放 大查询锁
	DbLock.bigQueryLock.unlock();

	if(conn != null)
		conn.close();
	if(service != null)
		service.releaseAll();
	if(adminService != null)
		adminService.close();
}
%>
</body>
</html>
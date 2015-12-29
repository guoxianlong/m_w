<%@page import="adultadmin.util.db.DbOperation"%>
<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>  
<%@ page import="adultadmin.util.*,adultadmin.util.db.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.util.*,adultadmin.bean.stat.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%
	voUser user = (voUser) session.getAttribute("userView");

	boolean isSystem = (user.getSecurityLevel() == 10); //系统管理员
	boolean isGaojiAdmin = (user.getSecurityLevel() == 9); //高级管理员
	boolean isAdmin = (user.getSecurityLevel() == 5); //普通管理员

	boolean isPingtaiyunwei = (user.getPermission() == 8); //平台运维部
	boolean isXiaoshou = (user.getPermission() == 7); //销售部
	boolean isShangpin = (user.getPermission() == 6); //商品部
	boolean isTuiguang = (user.getPermission() == 5); //推广部
	boolean isYunyingzhongxin = (user.getPermission() == 4); //运营中心
	boolean isKefu = (user.getPermission() == 3); //客服部

	Connection conn = DbUtil.getConnection(DbOperation.DB_SLAVE2);
	Statement st = conn.createStatement();
	Statement st2 = conn.createStatement();
	Statement st3 = conn.createStatement();
	ResultSet rs = null;
	ResultSet rs2 = null;
	ResultSet rs3 = null;
	HashMap map = new HashMap();//用户存储查询记录
	InOutStatBean isb = null;//每条记录的javaBean
	String startDate = StringUtil.dealParam(request.getParameter("startDate"));
	if (startDate == null) {
		startDate = "";
	}
	String endDate = StringUtil.dealParam(request.getParameter("endDate"));
	if (endDate == null) {
		endDate = "";
	}
	int stockType = StringUtil.toInt(request.getParameter("stockType"));
	if (stockType < 0) {
		stockType = 0;
	}
	int proxy = StringUtil.toInt(request.getParameter("proxy"));
	int parentId = StringUtil.toInt(request.getParameter("parentId"));
	//System.out.println(startDate+"  "+endDate+"  "+stockType+"  "+proxy+"  "+parentId);
	try {
%>
<html>
<title>买卖宝后台</title>

<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<body>
<%@include file="../../header.jsp"%>
<form method=post action="stockInOutStat.jsp"><table>
<tr><td>
进出货：</td><td><select name="stockType"><option value="0" <%if (stockType == 0) {%>selected<%}%>>出货</option><option value="1" <%if (stockType == 1) {%>selected<%}%>>进货</option></select>
</td></tr>
<tr><td>
代理商：</td><td><select name="proxy">
    <option value="0">所有</option>
<%
	rs = st.executeQuery("select id, name from supplier_standard_info where status=1 order by id");
		while (rs.next()) {
%>
    <option value="<%=rs.getInt(1)%>" <%if (rs.getInt(1) == proxy) {%>selected<%}%>><%=rs.getString(2)%></option>
<%
	}
		rs.close();
%>
</select>
</td></tr>
<tr><td>时间段：</td><td><input type="text" name="startDate" size="20" value="<%=startDate%>" onclick="SelectDate(this,'yyyy-MM-dd');">到<input type="text" name="endDate" size="20" value="<%=endDate%>" onclick="SelectDate(this,'yyyy-MM-dd');">
</td></tr>
</table>
<input type=submit value="查询进出货记录"><br><br>
</form>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center>分类名称</td>
		<td align=center><%=(stockType == 0 ? "出" : "进")%>货总数</td>
		<td align=center>进货总额</td>
		<td align=center>北京总数</td>		
		<td align=center>北京总额</td>
		<td align=center>广东总数</td>
		<td align=center>广东总额</td>
	</tr>
<%
	String sql = null;
		String sql2 = null;
		String sql3 = null;
		String productCondition = null;
		if ("".equals(startDate) || "".equals(endDate)) {
			//do nothing
		} else {
			try {
				productCondition = "product.id > 0";
				if (proxy > 0) {
					//                    and product.proxy_id
					productCondition += " and supplier_standard_info.id = " + proxy;
				}
				//出货        查询字段: 父类id  父类名称 北京总数 北京总额 广东总数 广东总额
				if (stockType == 0) {
					if (parentId < 0) {
						sql  = "select parent_id1,(select name from catalog where id = parent_id1),sum(sbj), sum(sgd), sum(sbj * product.price3), sum(sgd * product.price3) from " + "(SELECT product_id, sum(stock_bj) sbj, sum(stock_gd) sgd from stock_history join stock_operation on stock_history.oper_id = stock_operation.id where stock_operation.type = 0 and stock_operation.status = 2 and last_oper_time >= '" + startDate + " 00:00:00' and last_oper_time <= '" + endDate + " 23:59:59' group by product_id) t1 " + "join " + "product " + "on t1.product_id = product.id " + " left outer join product_supplier c on product.id=c.product_id left join supplier_standard_info on supplier_standard_info.id=c.supplier_id " + " where " + productCondition + " " + "group by parent_id1";
						sql2 = "select parent_id1,(select name from catalog where id = parent_id1),sum(so.stockout_count),sum(so.stockout_count * product.price3) " + " from order_stock_product so  join product  on so.product_id = product.id " + " left outer join product_supplier c on product.id=c.product_id left join supplier_standard_info  on supplier_standard_info.id=c.supplier_id"+ " where " + productCondition + " and so.deal_datetime >= '" + startDate + " 00:00:00' and so.deal_datetime <= '" + endDate + " 23:59:59'" + " and so.stock_area = 0 and stock_type = 0 group by parent_id1 ";//0已完成出库类型
						sql3 = "select parent_id1,(select name from catalog where id = parent_id1),sum(so.stockout_count),sum(so.stockout_count * product.price3) " + " from order_stock_product so  join product  on so.product_id = product.id " + " left outer join product_supplier c on product.id=c.product_id left join supplier_standard_info  on supplier_standard_info.id=c.supplier_id"+ " where " + productCondition + " and so.deal_datetime >= '" + startDate + " 00:00:00' and so.deal_datetime <= '" + endDate + " 23:59:59'" + " and so.stock_area in (1,2) and stock_type = 0 group by parent_id1 ";//0已完成出库类型
					} else {
						productCondition += " and product.parent_id1 = " + parentId;
						sql  = "select parent_id2,(select name from catalog where id = parent_id2),sum(sbj), sum(sgd), sum(sbj * product.price3), sum(sgd * product.price3) from " + "(SELECT product_id, sum(stock_bj) sbj, sum(stock_gd) sgd from stock_history join stock_operation on stock_history.oper_id = stock_operation.id where stock_operation.type = 0 and stock_operation.status = 2 and last_oper_time >= '" + startDate + " 00:00:00' and last_oper_time <= '" + endDate + " 23:59:59' group by product_id) t1 " + "join " + "product " + "on t1.product_id = product.id " + " left outer join product_supplier c on product.id=c.product_id left join supplier_standard_info on supplier_standard_info.id=c.supplier_id " + " where " + productCondition + " " + "group by parent_id2";
						sql2 = "select parent_id1,(select name from catalog where id = parent_id1),sum(so.stockout_count),sum(so.stockout_count * product.price3) " + " from order_stock_product so  join product  on so.product_id = product.id " +" left outer join product_supplier c on product.id=c.product_id left join supplier_standard_info  on supplier_standard_info.id=c.supplier_id"+ " where " + productCondition + " and so.deal_datetime >= '" + startDate + " 00:00:00' and so.deal_datetime <= '" + endDate + " 23:59:59'" + " and so.stock_area = 0 and stock_type = 0 group by parent_id1 ";//0北京,0已完成出库类型
						sql3 = "select parent_id1,(select name from catalog where id = parent_id1),sum(so.stockout_count),sum(so.stockout_count * product.price3) " + " from order_stock_product so  join product  on so.product_id = product.id " +" left outer join product_supplier c on product.id=c.product_id left join supplier_standard_info  on supplier_standard_info.id=c.supplier_id"+ " where " + productCondition + " and so.deal_datetime >= '" + startDate + " 00:00:00' and so.deal_datetime <= '" + endDate + " 23:59:59'" + " and so.stock_area in (1,2) and stock_type = 0 group by parent_id1 ";//(1,2)广东,0已完成出库类型
					}
				}
				//进货       查询字段: 父类id  父类名称 北京总数 北京总额 广东总数 广东总额
				else {
					if (parentId < 0) {
						sql  = "select parent_id1,(select name from catalog where id = parent_id1),sum(sbj), sum(sgd), sum(sbj * product.price3), sum(sgd * product.price3) from " + "(SELECT product_id, sum(stock_bj) sbj, sum(stock_gd) sgd from stock_history join stock_operation on stock_history.oper_id = stock_operation.id where stock_operation.type = 1 and stock_operation.status = 1 and last_oper_time >= '" + startDate + " 00:00:00' and last_oper_time <= '" + endDate + " 23:59:59' group by product_id) t1 " + "join " + "product " + "on t1.product_id = product.id " + " left outer join product_supplier c on product.id=c.product_id left join supplier_standard_info on supplier_standard_info.id=c.supplier_id " + " where " + productCondition + " " + "group by parent_id1";
						sql2 = "select parent_id1,(select name from catalog where id = parent_id1),sum(bsp.stockin_count),sum(bsp.stockin_count* bsp.price3)" + " from buy_stockin_product bsp,buy_stockin bs,product left outer join product_supplier c on product.id=c.product_id left join supplier_standard_info  on supplier_standard_info.id=c.supplier_id " + " where " + productCondition + " and bs.status=4 and bsp.buy_stockin_id = bs.id " //4入库完成,采购入库id=入库处理id
								+ " and bsp.product_id = product.id" + " and bsp.confirm_datetime >= '" + startDate + " 00:00:00' and bsp.confirm_datetime <= '" + endDate + " 23:59:59'" + " and bs.stock_area =0 and stock_type = 1 group by parent_id1 ";//0北京,1待检库
						sql3 = "select parent_id1,(select name from catalog where id = parent_id1),sum(bsp.stockin_count),sum(bsp.stockin_count* bsp.price3)" + " from buy_stockin_product bsp,buy_stockin bs,product left outer join product_supplier c on product.id=c.product_id left join supplier_standard_info  on supplier_standard_info.id=c.supplier_id " + " where " + productCondition + " and bs.status=4 and bsp.buy_stockin_id = bs.id " //4入库完成,采购入库id=入库处理id
								+ " and bsp.product_id = product.id"//入库处理产品id=产品id
								+ " and bsp.confirm_datetime >= '" + startDate + " 00:00:00' and bsp.confirm_datetime <= '" + endDate + " 23:59:59'" + " and bs.stock_area in (1,2) and stock_type = 1 group by parent_id1 ";//(1,2)广东,1待检库 
					} else {
						productCondition += " and product.parent_id1 = " + parentId;
						sql  = "select parent_id2,(select name from catalog where id = parent_id2),sum(sbj), sum(sgd), sum(sbj * product.price3), sum(sgd * product.price3) from " + "(SELECT product_id, sum(stock_bj) sbj, sum(stock_gd) sgd from stock_history join stock_operation on stock_history.oper_id = stock_operation.id where stock_operation.type = 1 and stock_operation.status = 1 and last_oper_time >= '" + startDate + " 00:00:00' and last_oper_time <= '" + endDate + " 23:59:59' group by product_id) t1 " + "join " + "product " + "on t1.product_id = product.id " + " left outer join product_supplier c on product.id=c.product_id left join supplier_standard_info on supplier_standard_info.id=c.supplier_id " + " where " + productCondition + " " + "group by parent_id2";
						sql2 = "select parent_id1,(select name from catalog where id = parent_id1),sum(bsp.stockin_count),sum(bsp.stockin_count* bsp.price3)" + " from buy_stockin_product bsp,buy_stockin bs,product left outer join product_supplier c on product.id=c.product_id left join supplier_standard_info  on supplier_standard_info.id=c.supplier_id " + " where " + productCondition + " and bs.status=4 and bsp.buy_stockin_id = bs.id " //4入库完成,采购入库id=入库处理id
								+ " and bsp.product_id = product.id" + " and bsp.confirm_datetime >= '" + startDate + " 00:00:00' and bsp.confirm_datetime <= '" + endDate + " 23:59:59'" + " and bs.stock_area =0 and stock_type = 1 group by parent_id1 ";//0北京,1待检库
						sql3 = "select parent_id1,(select name from catalog where id = parent_id1),sum(bsp.stockin_count),sum(bsp.stockin_count* bsp.price3)" + " from buy_stockin_product bsp,buy_stockin bs,product left outer join product_supplier c on product.id=c.product_id left join supplier_standard_info  on supplier_standard_info.id=c.supplier_id " + " where " + productCondition + " and bs.status=4 and bsp.buy_stockin_id = bs.id " //4入库完成,采购入库id=入库处理id
								+ " and bsp.product_id = product.id"//入库处理产品id=产品id
								+ " and bsp.confirm_datetime >= '" + startDate + " 00:00:00' and bsp.confirm_datetime <= '" + endDate + " 23:59:59'" + " and bs.stock_area in (1,2) and stock_type = 1 group by parent_id1 ";//(1,2)广东,1待检库
					}
				}
				int catalogId = 0, catalogId2 = 0, catalogId3 = 0;
				String catalogName = null;
				int stock = 0, stockBj = 0, stockGd = 0;
				float price = 0, priceBj = 0, priceGd = 0;
				int totalStock = 0, totalStockBj = 0, totalStockGd = 0;
				float totalPrice = 0, totalPriceBj = 0, totalPriceGd = 0;
				rs = st.executeQuery(sql);
				//新表 北京
				if (sql2 != null) {
					rs2 = st2.executeQuery(sql2);
					while (rs2.next()) {
						isb = new InOutStatBean();
						catalogId2 = rs2.getInt(1);
						isb.setCatalogName(rs2.getString(2));
						isb.setStockBj(rs2.getInt(3));
						isb.setPriceBj(rs2.getFloat(4));
						map.put("" + catalogId2, isb);
					}
				}
				//新表 广东            操作:逐条统计,保证记录的唯一性,精确性
				if (sql3 != null) {
					rs3 = st3.executeQuery(sql3);
					while (rs3.next()) {
						catalogId3 = rs3.getInt(1);
						if (map.get("" + catalogId3) == null) {//该条记录没有被创建,新建
							isb = new InOutStatBean();
							isb.setCatalogName(rs3.getString(2));
						}
						if (map.get("" + catalogId3) != null) {//该条记录已经创建,取出并修改
							isb = (InOutStatBean) map.get("" + catalogId3);
						}
						isb.setStockGd(rs3.getInt(3));
						isb.setPriceGd(rs3.getFloat(4));
						map.put("" + catalogId3, isb);
					}
				}
				//旧表 北广     操作:在保留旧表的基础上汇集	新表中的数据,列出统计									
				while (rs.next()) {
					catalogId = rs.getInt(1);
					if (map.get("" + catalogId) != null) {//新表中已有数据,取出并与旧表数据相加
						isb = (InOutStatBean) map.get("" + catalogId);

						isb.setCatalogName(((InOutStatBean) map.get("" + catalogId)).getCatalogName());
						isb.setStockBj(((InOutStatBean) map.get("" + catalogId)).getStockBj() + rs.getInt(3));
						isb.setPriceBj(((InOutStatBean) map.get("" + catalogId)).getPriceBj() + rs.getFloat(5));

						isb.setStockGd(((InOutStatBean) map.get("" + catalogId)).getStockGd() + rs.getInt(4));
						isb.setPriceGd(((InOutStatBean) map.get("" + catalogId)).getPriceGd() + rs.getFloat(6));
					}
					if (map.get("" + catalogId) == null) {//新表中没有数据,直接创建统计旧表数据
						isb = new InOutStatBean();
						isb.setCatalogName(rs.getString(2));
						isb.setStockBj(rs.getInt(3));
						isb.setPriceBj(rs.getFloat(5));
						isb.setStockGd(rs.getInt(4));
						isb.setPriceGd(rs.getFloat(6));
						map.put("" + catalogId, isb);
					}
				}
				//对统计数据进行迭代
				Set keys = map.keySet();
				if (keys != null) {
					Iterator iterator = keys.iterator();
					while (iterator.hasNext()) {
						Object key = iterator.next();
						InOutStatBean value = (InOutStatBean) map.get(key);
						catalogName = value.getCatalogName();
						stock = value.getStock();
						stockBj = value.getStockBj();
						price = value.getPrice();
						priceBj = value.getPriceBj();
						stockGd = value.getStockGd();
						priceGd = value.getPriceGd();
						//相关合计的统计
						stock = stockBj + stockGd;
						price = priceBj + priceGd;
						totalStock += stock;
						totalStockBj += stockBj;
						totalStockGd += stockGd;
						totalPrice += price;
						totalPriceBj += priceBj;
						totalPriceGd += priceGd;
%>
    <tr bgcolor='#F8F8F8'>
		<td align=center><%
			if (parentId <= 0) {
		%><a
				href="stockInOutStat.jsp?startDate=<%=startDate%>&endDate=<%=endDate%>&stockType=<%=stockType%>&proxy=<%=proxy%>&parentId=<%=key%>&parentName=<%=Encoder.encrypt(catalogName)%>">
					<%
						}
					%><%=catalogName%>
			</td>
		<td align=center><%=stock%></td>
		<td align=center><%=StringUtil.formatFloat(price)%></td>
		<td align=center><%=stockBj%></td>		
		<td align=center><%=StringUtil.formatFloat(priceBj)%></td>
		<td align=center><%=stockGd%></td>
		<td align=center><%=StringUtil.formatFloat(priceGd)%></td>
	</tr>
<%
	}
				}
%>
    <tr bgcolor='#F8F8F8'>
		<td align=center>合计</td>
		<td align=center><%=totalStock%></td>
		<td align=center><%=StringUtil.formatFloat(totalPrice)%></td>
		<td align=center><%=totalStockBj%></td>		
		<td align=center><%=StringUtil.formatFloat(totalPriceBj)%></td>
		<td align=center><%=totalStockGd%></td>
		<td align=center><%=StringUtil.formatFloat(totalPriceGd)%></td>
	</tr>
<%
	//关闭数据库连接
				rs.close();
				if (rs2 != null) {
					rs2.close();
				}
				if (rs3 != null) {
					rs3.close();
					;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
%>
	
</table>

<%
	st.close();
		st2.close();
		st3.close();
	} catch (Exception e) {
		e.printStackTrace();
	}
	conn.close();
%>

</body>
</html>
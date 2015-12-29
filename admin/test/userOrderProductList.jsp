<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file="../../taglibs.jsp"%>
<%@page import="java.sql.*,java.util.*"%>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.service.*" %>
<%@ page import="adultadmin.bean.*" %>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
	voUser user = (voUser)session.getAttribute("userView");

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
	
	String productCode = StringUtil.convertNull(request.getParameter("productCode"));
	String orderCode = StringUtil.convertNull(request.getParameter("orderCode"));
	String table = StringUtil.convertNull(request.getParameter("table"));
	String status = StringUtil.convertNull(request.getParameter("orderStatus"));

	Connection conn = adultadmin.util.db.DbUtil.getConnection("adult_slave");
	Statement st = conn.createStatement();
	ResultSet rs = null;
    try {
%>
<html>
<title>买卖宝后台</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<body>
<%@include file="../../header.jsp"%>
<fieldset>
   <legend>订单产品拆分数据</legend>
   <form name="searchForm" action="userOrderProductList.jsp" method="post">
   产品编号：<input type="text" name="productCode" value="<%=productCode %>" size=14/>&nbsp;&nbsp;
   订单编号：<input type="text" name="orderCode" value="<%=orderCode %>" size=14/>&nbsp;&nbsp;
   订单状态：<select name="orderStatus" id="orderStatus">
   			<option value="-1"></option>
   			<option value="0">未处理</option>
   			<option value="1">电话失败</option>
   			<option value="2">电话成功</option>
   			<option value="3">已到款/待发货</option>
   			<option value="6">已发货</option>
   			<option value="7">已取消</option>
   			<option value="8">废弃</option>
   			<option value="9">待查款</option>
   			<option value="10">重复</option>
   			<option value="11">已退回</option>
   			<option value="12">已结算</option>
   			<option value="13">待退回</option>
   			<option value="14">已妥投</option>
   </select>&nbsp;&nbsp;
   <script>selectOption(document.getElementById('orderStatus'), '<%=status%>');</script>
   数据：<select name="table" id="table">
   			<option value="user_order_product">订单商品(含套装)</option>
   			<option value="user_order_product_split">订单商品(单品，不含套装)</option>
   			<option value="user_order_present">订单赠品(含套装)</option>
   			<option value="user_order_present_split">订单赠品(单品，不含套装)</option>
   </select>
   <script>selectOption(document.getElementById('table'), '<%=table%>');</script>
  <br/>
  <input type="submit" value="查询"/>
   </form>
</fieldset>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
<%
			if(!table.equals("")){
			String params = "?productCode="+productCode+"&orderCode="+orderCode;
			int orderId = 0;
			int productId = 0;
			int orderStatus = StringUtil.toInt(request.getParameter("orderStatus"));
			
			if(!orderCode.equals("")){
				rs = st.executeQuery("select id from user_order where code = '"+orderCode+"'");
				if(rs.next()){
					orderId = rs.getInt("id");
				}
			}
			if(!productCode.equals("")){
				rs = st.executeQuery("select id from product where code = '"+productCode+"'");
				if(rs.next()){
					productId = rs.getInt("id");
				}
			}
			
			StringBuilder buff = new StringBuilder();
			buff.append("select p.id,uo.code,p.code,p.name,h.count,h.price3,uos.name");
			buff.append(" from user_order uo join ");
			buff.append(table);
			buff.append(" h on uo.id = h.order_id left join product p on h.product_id = p.id join user_order_status uos on uo.status = uos.id");
			buff.append(" where ");
			if(orderId>0){
				buff.append("h.order_id = "+orderId);
			}
			if(productId>0){
				if(!buff.toString().endsWith(" where ")){
					buff.append(" and ");
				}
				buff.append("h.product_id = "+productId);
			}
			if(orderStatus >= 0){
				if(!buff.toString().endsWith(" where ")){
					buff.append(" and ");
				}
				buff.append("uo.status = "+orderStatus);
			}
			if(buff.toString().endsWith(" where ")){
				buff.delete(buff.length()-7,buff.length());
			}
			buff.append(" order by h.product_id");
    		
%>
	<tr bgcolor='#F8F8F8'>
		<td align=center width="5%">序号</td>
		<td align=center>订单编号</td>
		<td align=center>订单状态</td>
		<td align=center>产品编号</td>
		<td align=center>小店名称</td>
		<td align=center>数量</td>
		<td align=center>商品出库库存价</td>
	</tr>

<%
    		rs = st.executeQuery(buff.toString());
    		int i=0;
    		while(rs.next()){
    		i++;
    		int productId2 = rs.getInt("p.id");
    		String orderCode1 = StringUtil.convertNull(rs.getString("uo.code"));
    		String orderStatus1 = StringUtil.convertNull(rs.getString("uos.name"));
    		String productCode1 = StringUtil.convertNull(rs.getString("p.code"));
    		String productName = StringUtil.convertNull(rs.getString("p.name"));
    		int count = rs.getInt("h.count");
    		float price = rs.getFloat("h.price3");
    		
%>			
			<tr bgcolor='#F8F8F8'>
				<td align=center><%=i %></td>
				<td align=center><%=orderCode1 %></td>
				<td align=center><%=orderStatus1 %></td>
				<td align=center><a href="../fproduct.do?id=<%=productId2 %>" target="_blank"><%=productCode1 %></a></td>
				<td align=center><%=productName %></td>
				<td align=center><%=count %></td>
				<td align=center><%=price %></td>
			</tr>
<%			}
		}
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		if(rs!=null){
			rs.close();
		}
		if(st!=null){
			st.close();
		}
		if(conn!=null){
			conn.close();
		}
	}

%>
</table>
<br/>
<br/>
</body>


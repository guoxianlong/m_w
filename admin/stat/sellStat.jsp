<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,adultadmin.util.db.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
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

String startDate = StringUtil.convertNull(request.getParameter("startDate"));
if(startDate.length()==0){
	startDate = DateUtil.getNow().substring(0, 10);
}
String endDate = StringUtil.convertNull(request.getParameter("endDate"));
if(endDate.length() == 0){
	endDate = DateUtil.getNow().substring(0, 10);
}
Connection conn = DbUtil.getConnection(DbOperation.DB_SLAVE2);
Statement st = conn.createStatement();
int totalCount=0;
int totalCancelCount=0;
float totalPrice=0;
float totalCancelPrice=0;
int orderCountBj0=0;
int orderCountBj1=0;
int orderCountBj2=0;
int cancelCountBj0=0;
int cancelCountBj1=0;
int cancelCountBj2=0;
float orderPriceBj0=0;
float orderPriceBj1=0;
float orderPriceBj2=0;
float cancelPriceBj0=0;
float cancelPriceBj1=0;
float cancelPriceBj2=0;
int orderCountGd0=0;
int orderCountGd1=0;
int cancelCountGd0=0;
int cancelCountGd1=0;
float orderPriceGd0=0;
float orderPriceGd1=0;
float cancelPriceGd0=0;
float cancelPriceGd1=0;
try{
%>
<html>
<title>买卖宝后台</title>

<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<body>
<%@include file="../../header.jsp"%>
<form name="" action="" method="">
	下单时间：<input type=text name="startDate" size="12" value="<%= startDate %>" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');" />到<input type=text name="endDate" size="12" value="<%= endDate %>" readonly="readonly" onclick="SelectDate(this,'yyyy-MM-dd');" /><br/>
	<input type="submit" value="查询" />
</form>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor="#F8F8F8">
		<td align="center">购买方式</td>
		<td align="center">地区</td>
		<td align="center">快递</td>
		<td align="center">订单数</td>
		<td align="center">订单额度</td>
		<td align="center">退单</td>
		<td align="center">退单额度</td>
	</tr>
<%
    try {
		String sql = "select *,(select so.area from stock_operation so where so.order_code=uo.code order by so.id asc limit 1) stock_area from user_order uo where uo.create_datetime > '" + startDate + "' and uo.create_datetime <= '" + endDate + " 23:59:59' order by uo.buy_mode, uo.areano";
		ResultSet rs = st.executeQuery(sql);
		while(rs.next()){
			int id = rs.getInt("uo.id");
			int buymode = rs.getInt("uo.buy_mode");
			int areano = rs.getInt("stock_area");
			int status = rs.getInt("uo.status");
			if(status == 3 || status == 6 || status == 9 || status == 11){
				totalCount++;
				totalPrice += rs.getFloat("uo.price");
			}
			if(status == 11){
				totalCancelCount++;
				totalCancelPrice += rs.getFloat("uo.price");
			}
			if(areano == 0){ // 北京
				if(buymode == 0){// 货到付款
					if(status == 3 || status == 6 || status == 9){// 成功订单
						orderCountBj0++;
						orderPriceBj0 += rs.getFloat("uo.price");
					} else if(status == 11){// 退掉的订单
						cancelCountBj0++;
						cancelPriceBj0 += rs.getFloat("uo.price");
					}
				} else if(buymode == 1){//邮购
					if(status == 3 || status == 6 || status == 9){// 成功订单
						orderCountBj1++;
						orderPriceBj1 += rs.getFloat("uo.price");
					} else if(status == 11){// 退掉的订单
						cancelCountBj1++;
						cancelPriceBj1 += rs.getFloat("uo.price");
					}
				} else if(buymode == 2){//上门自取
					if(status == 3 || status == 6 || status == 9){// 成功订单
						orderCountBj2++;
						orderPriceBj2 += rs.getFloat("uo.price");
					} else if(status == 11){// 退掉的订单
						cancelCountBj2++;
						cancelPriceBj2 += rs.getFloat("uo.price");
					}
				}
			} else if(areano == 1) { //广东
				if(buymode == 0){// 货到付款
					if(status == 3 || status == 6 || status == 9){// 成功订单
						orderCountGd0++;
						orderPriceGd0 += rs.getFloat("uo.price");
					} else if(status == 11){// 退掉的订单
						cancelCountGd0++;
						cancelPriceGd0 += rs.getFloat("uo.price");
					}
				} else if(buymode == 1){//邮购
					if(status == 3 || status == 6 || status == 9){// 成功订单
						orderCountGd1++;
						orderPriceGd1 += rs.getFloat("price");
					} else if(status == 11){// 退掉的订单
						cancelCountGd1++;
						cancelPriceGd1 += rs.getFloat("price");
					}
				}
			}
		}
%>

<%
		rs.close();
	} catch (Exception e) {e.printStackTrace();}
%>
	<tr bgcolor="#F8F8F8">
		<td align="center"><a href="../searchorder.do?startDate=<%= startDate %>&endDate=<%= endDate %>&buymode=0&areano=0&status=3,6,9,11">货到付款</a></td>
		<td align="center">北京</td>
		<td align="center">&nbsp;</td>
		<td align="center"><%= orderCountBj0 + cancelCountBj0 %></td>
		<td align="center"><%= (int)(orderPriceBj0 + cancelPriceBj0) %></td>
		<td align="center"><a href="../searchorder.do?startDate=<%= startDate %>&endDate=<%= endDate %>&buymode=0&areano=0&orderStatus=11"><%= cancelCountBj0 %></a></td>
		<td align="center"><%= (int)(cancelPriceBj0) %></td>
	</tr>
	<tr bgcolor="#F8F8F8">
		<td align="center"><a href="../searchorder.do?startDate=<%= startDate %>&endDate=<%= endDate %>&buymode=0&areano=1&status=3,6,9,11">货到付款</a></td>
		<td align="center">广东</td>
		<td align="center">&nbsp;</td>
		<td align="center"><%= orderCountGd0 + cancelCountGd0 %></td>
		<td align="center"><%= (int)(orderPriceGd0 + cancelPriceGd0) %></td>
		<td align="center"><a href="../searchorder.do?startDate=<%= startDate %>&endDate=<%= endDate %>&buymode=0&areano=1&orderStatus=11"><%= cancelCountGd0 %></a></td>
		<td align="center"><%= (int)(cancelPriceGd0) %></td>
	</tr>
	<tr bgcolor="#F8F8F8">
		<td align="center"><a href="../searchorder.do?startDate=<%= startDate %>&endDate=<%= endDate %>&buymode=1&areano=0&status=3,6,9,11">邮购</a></td>
		<td align="center">北京</td>
		<td align="center">&nbsp;</td>
		<td align="center"><%= orderCountBj1 + cancelCountBj1 %></td>
		<td align="center"><%= (int)(orderPriceBj1 + cancelPriceBj1) %></td>
		<td align="center"><a href="../searchorder.do?startDate=<%= startDate %>&endDate=<%= endDate %>&buymode=1&areano=0&orderStatus=11"><%= cancelCountBj1 %></a></td>
		<td align="center"><%= (int)(cancelPriceBj1) %></td>
	</tr>
	<tr bgcolor="#F8F8F8">
		<td align="center"><a href="../searchorder.do?startDate=<%= startDate %>&endDate=<%= endDate %>&buymode=1&areano=1&status=3,6,9,11">邮购</a></td>
		<td align="center">广东</td>
		<td align="center">&nbsp;</td>
		<td align="center"><%= orderCountGd1 + cancelCountGd1 %></td>
		<td align="center"><%= (int)(orderPriceGd1 + cancelPriceGd1) %></td>
		<td align="center"><a href="../searchorder.do?startDate=<%= startDate %>&endDate=<%= endDate %>&buymode=1&areano=1&orderStatus=11"><%= cancelCountGd1 %></a></td>
		<td align="center"><%= (int)(cancelPriceGd1) %></td>
	</tr>
	<tr bgcolor="#F8F8F8">
		<td align="center"><a href="../searchorder.do?startDate=<%= startDate %>&endDate=<%= endDate %>&buymode=2&areano=0&status=3,6,9,11">上门自取</a></td>
		<td align="center">北京</td>
		<td align="center">&nbsp;</td>
		<td align="center"><%= orderCountBj2 + cancelCountBj2 %></td>
		<td align="center"><%= (int)(orderPriceBj2 + cancelPriceBj2) %></td>
		<td align="center"><a href="../searchorder.do?startDate=<%= startDate %>&endDate=<%= endDate %>&buymode=2&areano=0&orderStatus=11"><%= cancelCountBj2 %></a></td>
		<td align="center"><%= (int)(cancelPriceBj2) %></td>
	</tr>
	<tr bgcolor="#F8F8F8">
		<td align="center">&nbsp;</td>
		<td align="center">总计</td>
		<td align="center">&nbsp;</td>
		<td align="center"><%= totalCount %></td>
		<td align="center"><%= (int)(totalPrice) %></td>
		<td align="center"><%= totalCancelCount %></td>
		<td align="center"><%= (int)(totalCancelPrice) %></td>
	</tr>
	<tr bgcolor="#F8F8F8">
		<td align="center" colspan="2">去掉退单总数：<%= totalCount - totalCancelCount %></td>
		<td align="center">&nbsp;</td>
		<td align="center" colspan="2">后台实际订单数：<%= totalCount %></td>
		<td align="center">&nbsp;</td>
		<td align="center">&nbsp;</td>
	</tr>
</table>
<%
	st.close();
} catch(Exception e){e.printStackTrace();}
conn.close();
%>
</body>
</html>
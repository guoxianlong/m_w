<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,adultadmin.util.db.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*"%>
<%
	voUser user = (voUser)session.getAttribute("userView");

	boolean isSystem = (user.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (user.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (user.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (user.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (user.getPermission() == 7);	//销售部
	boolean isShangpin = (user.getPermission() == 6);	//商品部
	boolean isTuiguang = (user.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (user.getPermission() == 4);	//运营中心
	boolean isKefu = (user.getPermission() == 3);	//客服部

String curDate = request.getParameter("date");
String phone = StringUtil.convertNull(request.getParameter("phone"));
int currentpage = StringUtil.StringToId(request.getParameter("currentPage"));
if(curDate == null)
	curDate = DateUtil.formatDate(new Date());

Connection conn = DbUtil.getConnection(DbOperation.DB_SLAVE);
	Statement st = conn.createStatement();
int pagerow = 60;
PagingBean paging = new PagingBean(currentpage, 200, pagerow);
paging.setPrefixUrl("./stockStatBuymode.jsp");
try{


%>
<html>
<title>买卖宝后台</title>

<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../../header.jsp"%>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "currentPage", 10)%>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="center">
	<tr bgcolor='#F8F8F8'>
		<td align="center" width="8%">日期</td>
		<td align="center" width="5%">地区</td>
		<td align="center" width="10%">购买方式</td>
		<td align="center" width="8%">成交额</td>
		<td align="center" width="8%">单笔成交额</td>
		<td align="center" width="8%">发单数</td>
	</tr>
<%
    try {
    	//String sql = "select t1.da,t1.c,t2.c,t2.p,t3.c,t3.p from (select left(create_datetime,10) da,count(*) c from user_order group by da) t1 left outer join (select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status=3 or status=6 or status=9 group by da) t2 on t1.da=t2.da left outer join (select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status<>8 and status<>10 group by da) t3 on t1.da=t3.da order by t1.da desc limit 100";
		String sql = "select t1.da,t11.c,t12.c,t13.c,t21.c,t21.p,t22.c,t22.p,t23.c,t23.p,t31.c,t32.c,t33.c,t41.c,t41.p,t42.c,t42.p,t43.c,t43.p,t51.c,t52.c,t53.c,t61.c,t61.p,t62.c,t62.p,t63.c,t63.p from " +
			"(select distinct left(create_datetime,10) da from user_order group by da) t1 " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=1 and so.status=2 and so.stock_area=0 group by da) t11 " +
			"on t1.da=t11.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=0 and so.status=2 and so.stock_area=0 group by da) t12 " +
			"on t1.da=t12.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=2 and so.status=2 and so.stock_area=0 group by da) t13 " +
			"on t1.da=t13.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c,sum(uo.price*uo.discount) p from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=1 and so.status=2 and so.stock_area=0 group by da) t21 " +
			"on t1.da=t21.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c,sum(uo.price*uo.discount) p from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=0 and so.status=2 and so.stock_area=0 group by da) t22 " +
			"on t1.da=t22.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c,sum(uo.price*uo.discount) p from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=2 and so.status=2 and so.stock_area=0 group by da) t23 " +
			"on t1.da=t23.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=1 and so.status=2 and so.stock_area=1 group by da) t31 " +
			"on t1.da=t31.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=0 and so.status=2 and so.stock_area=1 group by da) t32 " +
			"on t1.da=t32.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=2 and so.status=2 and so.stock_area=1 group by da) t33 " +
			"on t1.da=t33.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c,sum(uo.price*uo.discount) p from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=1 and so.status=2 and so.stock_area=1 group by da) t41 " +
			"on t1.da=t41.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c,sum(uo.price*uo.discount) p from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=0 and so.status=2 and so.stock_area=1 group by da) t42 " +
			"on t1.da=t42.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c,sum(uo.price*uo.discount) p from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=2 and so.status=2 and so.stock_area=1 group by da) t43 " +
			"on t1.da=t43.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=1 and so.status=2 and so.stock_area=2 group by da) t51 " +
			"on t1.da=t51.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=0 and so.status=2 and so.stock_area=2 group by da) t52 " +
			"on t1.da=t52.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=2 and so.status=2 and so.stock_area=2 group by da) t53 " +
			"on t1.da=t53.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c,sum(uo.price*uo.discount) p from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=1 and so.status=2 and so.stock_area=2 group by da) t61 " +
			"on t1.da=t61.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c,sum(uo.price*uo.discount) p from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=0 and so.status=2 and so.stock_area=2 group by da) t62 " +
			"on t1.da=t62.da " +
			"left outer join " +
			"(select left(so.last_oper_time,10) da,count(uo.id) c,sum(uo.price*uo.discount) p from user_order uo join order_stock so on uo.code=so.order_code where uo.buy_mode=2 and so.status=2 and so.stock_area=2 group by da) t63 " +
			"on t1.da=t63.da " +
			"order by t1.da desc limit " + (currentpage * pagerow) + "," + pagerow;
		ResultSet rs = st.executeQuery(sql);
		while(rs.next()){%>
			<tr bgcolor='#F8F8F8'>
				<td align="center" rowspan="9"><%if(!isTuiguang){%><a href="../searchorder.do?createDatetime=<%=rs.getString(1)%>" ><%}%><%=rs.getString(1)%><%if(!isTuiguang){%></a><%}%></td>
				<td align="center" rowspan="3">北库</td>
				<td align="center">邮&nbsp;&nbsp;_&nbsp;&nbsp;购：</td>
				<td align="center"><%=NumberUtil.price(rs.getFloat("t21.p"))%></td>
				<td align="center"><%=NumberUtil.div(rs.getFloat("t21.p"), rs.getInt("t11.c"))%></td>
				<td align="center"><%=rs.getInt("t11.c")%></td>
			</tr>
			<tr bgcolor='#F8F8F8'>
				<td align="center">货到付款：</td>
				<td align="center"><%=NumberUtil.price(rs.getFloat("t22.p"))%></td>
				<td align="center"><%=NumberUtil.div(rs.getFloat("t22.p"), rs.getInt("t12.c"))%></td>
				<td align="center"><%=rs.getInt("t12.c")%></td>
			</tr>
			<tr bgcolor='#F8F8F8'>
				<td align="center">上门自取：</td>
				<td align="center"><%=NumberUtil.price(rs.getFloat("t23.p"))%></td>
				<td align="center"><%=NumberUtil.div(rs.getFloat("t23.p"), rs.getInt("t13.c"))%></td>
				<td align="center"><%=rs.getInt("t13.c")%></td>
			</tr>
			<tr bgcolor='#F8F8F8'>
				<td align="center" rowspan="3">芳村</td>
				<td align="center">邮&nbsp;&nbsp;_&nbsp;&nbsp;购：</td>
				<td align="center"><%=NumberUtil.price(rs.getFloat("t41.p"))%></td>
				<td align="center"><%=NumberUtil.div(rs.getFloat("t41.p"), rs.getInt("t31.c"))%></td>
				<td align="center"><%=rs.getInt("t31.c")%></td>
			</tr>
			<tr bgcolor='#F8F8F8'>
				<td align="center">货到付款：</td>
				<td align="center"><%=NumberUtil.price(rs.getFloat("t42.p"))%></td>
				<td align="center"><%=NumberUtil.div(rs.getFloat("t42.p"), rs.getInt("t32.c"))%></td>
				<td align="center"><%=rs.getInt("t32.c")%></td>
			</tr>
			<tr bgcolor='#F8F8F8'>
				<td align="center">上门自取：</td>
				<td align="center"><%=NumberUtil.price(rs.getFloat("t43.p"))%></td>
				<td align="center"><%=NumberUtil.div(rs.getFloat("t43.p"), rs.getInt("t33.c"))%></td>
				<td align="center"><%=rs.getInt("t33.c")%></td>
			</tr>
			<tr bgcolor='#F8F8F8'>
				<td align="center" rowspan="3">广速</td>
				<td align="center">邮&nbsp;&nbsp;_&nbsp;&nbsp;购：</td>
				<td align="center"><%=NumberUtil.price(rs.getFloat("t61.p"))%></td>
				<td align="center"><%=NumberUtil.div(rs.getFloat("t61.p"), rs.getInt("t51.c"))%></td>
				<td align="center"><%=rs.getInt("t51.c")%></td>
			</tr>
			<tr bgcolor='#F8F8F8'>
				<td align="center">货到付款：</td>
				<td align="center"><%=NumberUtil.price(rs.getFloat("t62.p"))%></td>
				<td align="center"><%=NumberUtil.div(rs.getFloat("t62.p"), rs.getInt("t52.c"))%></td>
				<td align="center"><%=rs.getInt("t52.c")%></td>
			</tr>
			<tr bgcolor='#F8F8F8'>
				<td align="center">上门自取：</td>
				<td align="center"><%=NumberUtil.price(rs.getFloat("t63.p"))%></td>
				<td align="center"><%=NumberUtil.div(rs.getFloat("t63.p"), rs.getInt("t53.c"))%></td>
				<td align="center"><%=rs.getInt("t53.c")%></td>
			</tr>
		<%}
		rs.close();
	} catch (Exception e) {e.printStackTrace();}
	%>
	
</table>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "currentPage", 10)%>
<%
	st.close();
} catch(Exception e){e.printStackTrace();}
conn.close();

%>

</body>
</html>
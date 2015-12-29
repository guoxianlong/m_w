<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,adultadmin.util.db.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*"%>
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

String curDate = request.getParameter("date");
String phone = StringUtil.convertNull(request.getParameter("phone"));
int currentpage = StringUtil.StringToId(request.getParameter("currentPage"));
if(curDate == null)
	curDate = DateUtil.formatDate(new Date());

Connection conn = DbUtil.getConnection(DbOperation.DB_SLAVE);
	Statement st = conn.createStatement();
int pagerow = 60;
PagingBean paging = new PagingBean(currentpage, 200, pagerow);
paging.setPrefixUrl("./orderBuymode.jsp");
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
		<td align="center" width="10%">购买方式</td>
		<td align="center" width="8%">订单总数</td>
		<td align="center" width="8%">去掉重复</td>
		<td align="center" width="8%">有效订单数</td>
<%if(group.isFlag(28)) /*if(!(isTuiguang && isAdmin))*/{	/*对推广部普通管理员不开放*/%>
		<td align="center" width="8%">成交订单数</td>
		<td align="center"  width="8%">成交率</td>
		<td align="center"  width="8%">有效成交率</td>
<%}%>
<%if(group.isFlag(29)) /*if(!isTuiguang)*/{ /*对推广部所有人不开放*/%>
        <td align="center" width="10%">最低完成额</td>
		<td align="center" width="10%">总价格</td>
<%}%>
	</tr>
<%
    try {
    	//String sql = "select t1.da,t1.c,t2.c,t2.p,t3.c,t3.p from (select left(create_datetime,10) da,count(*) c from user_order group by da) t1 left outer join (select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status=3 or status=6 or status=9 group by da) t2 on t1.da=t2.da left outer join (select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status<>8 and status<>10 group by da) t3 on t1.da=t3.da order by t1.da desc limit 100";
		String sql = "select t1.da,t11.c,t12.c,t13.c,t21.c,t21.p,t22.c,t22.p,t23.c,t23.p,t31.c,t31.p,t32.c,t32.p,t33.c,t33.p,t41.c,t42.c,t43.c from " +
			"(select distinct left(create_datetime,10) da from user_order group by da) t1 " +
			"left outer join " +    
			"(select left(create_datetime,10) da,count(*) c from user_order where buy_mode=1 group by da) t11 " +
			"on t1.da=t11.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c from user_order where buy_mode=0 group by da) t12 " +
			"on t1.da=t12.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c from user_order where buy_mode=2 group by da) t13 " +
			"on t1.da=t13.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where buy_mode=1 and (status=3 or status=6 or status=9) group by da) t21 " +
			"on t1.da=t21.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where buy_mode=0 and (status=3 or status=6 or status=9) group by da) t22 " +
			"on t1.da=t22.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where buy_mode=2 and (status=3 or status=6 or status=9) group by da) t23 " +
			"on t1.da=t23.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where buy_mode=1 and status<>8 and status<>10 group by da) t31 " +
			"on t1.da=t31.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where buy_mode=0 and status<>8 and status<>10 group by da) t32 " +
			"on t1.da=t32.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where buy_mode=2 and status<>8 and status<>10 group by da) t33 " +
			"on t1.da=t33.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where buy_mode=1 and status<>10 group by da) t41 " +
			"on t1.da=t41.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where buy_mode=0 and status<>10 group by da) t42 " +
			"on t1.da=t42.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where buy_mode=2 and status<>10 group by da) t43 " +
			"on t1.da=t43.da " +
			"order by t1.da desc limit " + (currentpage * pagerow) + "," + pagerow;
		ResultSet rs = st.executeQuery(sql);
		while(rs.next()){%>
			<tr bgcolor='#F8F8F8'>
				<td align="center" rowspan="3"><%if(!isTuiguang){%><a href="../searchorder.do?createDatetime=<%=rs.getString(1)%>" ><%}%><%=rs.getString(1)%><%if(!isTuiguang){%></a><%}%></td>
				<td align="center">邮&nbsp;&nbsp;_&nbsp;&nbsp;购：</td>
				<td align="center"><%=rs.getInt("t11.c")%></td>
				<td align="center"><%=rs.getInt("t41.c")%></td>
				<td align="center"><%=rs.getInt("t31.c")%></td>
<%if(group.isFlag(28)) /*if(!(isTuiguang && isAdmin))*/{	/*对推广部普通管理员不开放*/%>
				<td align="center"><%=rs.getInt("t21.c")%></td>
				<td align="center"><%=NumberUtil.div(rs.getInt("t21.c") * 100, rs.getInt("t11.c"))%>%</td>
				<td align="center"><%=NumberUtil.div(rs.getInt("t21.c") * 100, rs.getInt("t31.c"))%>%</td>
<%}%>
<%if(group.isFlag(29)) /*if(!isTuiguang)*/{ /*对推广部所有人不开放*/%>
                <td align="center" rowspan="3"><font color="red"><%=((rs.getInt("t11.c")+rs.getInt("t12.c")+rs.getInt("t13.c")) * 72)%>元</font></td>
				<td align="right"><%=NumberUtil.price(rs.getFloat("t21.p"))%>元</td>
<%}%>
			</tr>
			<tr bgcolor='#F8F8F8'>
<%--
				<td align="center" rowspan="3"><%if(!isTuiguang){%><a href="../searchorder.do?createDatetime=<%=rs.getString(1)%>" ><%}%><%=rs.getString(1)%><%if(!isTuiguang){%></a><%}%></td>
--%>
				<td align="center">货到付款：</td>
				<td align="center"><%=rs.getInt("t12.c")%></td>
				<td align="center"><%=rs.getInt("t42.c")%></td>
				<td align="center"><%=rs.getInt("t32.c")%></td>
<%if(group.isFlag(28)) /*if(!(isTuiguang && isAdmin))*/{	/*对推广部普通管理员不开放*/%>
				<td align="center"><%=rs.getInt("t22.c")%></td>
				<td align="center"><%=NumberUtil.div(rs.getInt("t22.c") * 100, rs.getInt("t12.c"))%>%</td>
				<td align="center"><%=NumberUtil.div(rs.getInt("t22.c") * 100, rs.getInt("t32.c"))%>%</td>
<%}%>
<%if(group.isFlag(29)) /*if(!isTuiguang)*/{ /*对推广部所有人不开放*/%>
<%--
                <td align="center" rowspan="3"><font color="red"><%=((rs.getInt("t11.c")+rs.getInt("t12.c")+rs.getInt("t13.c")) * 72)%>元</font></td>
--%>
				<td align="right"><%=NumberUtil.price(rs.getFloat("t22.p"))%>元</td>
<%}%>
			</tr>
			<tr bgcolor='#F8F8F8'>
<%--
				<td align="center" rowspan="3"><%if(!isTuiguang){%><a href="../searchorder.do?createDatetime=<%=rs.getString(1)%>" ><%}%><%=rs.getString(1)%><%if(!isTuiguang){%></a><%}%></td>
--%>
				<td align="center">上门自取：</td>
				<td align="center"><%=rs.getInt("t13.c")%></td>
				<td align="center"><%=rs.getInt("t43.c")%></td>
				<td align="center"><%=rs.getInt("t33.c")%></td>
<%if(group.isFlag(28)) /*if(!(isTuiguang && isAdmin))*/{	/*对推广部普通管理员不开放*/%>
				<td align="center"><%=rs.getInt("t23.c")%></td>
				<td align="center"><%=NumberUtil.div(rs.getInt("t23.c") * 100, rs.getInt("t13.c"))%>%</td>
				<td align="center"><%=NumberUtil.div(rs.getInt("t23.c") * 100, rs.getInt("t33.c"))%>%</td>
<%}%>
<%if(group.isFlag(29)) /*if(!isTuiguang)*/{ /*对推广部所有人不开放*/%>
<%--
                <td align="center" rowspan="3"><font color="red"><%=((rs.getInt("t11.c")+rs.getInt("t12.c")+rs.getInt("t13.c")) * 72)%>元</font></td>
--%>
				<td align="right"><%=NumberUtil.price(rs.getFloat("t23.p"))%>元</td>
<%}%>
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
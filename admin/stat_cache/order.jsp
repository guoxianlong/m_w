<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %><%!
static java.text.DecimalFormat dfprice = new java.text.DecimalFormat("0.00");
static String[] orderTypeNames = {"全部"};
%><%@ page import="adultadmin.util.*,adultadmin.util.db.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
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
    int orderType = StringUtil.toInt(request.getParameter("type"));	// 0表示所有订单
    if(orderType<0)	orderType=0;
    Connection conn = null;
	try{
    DbLock.slaveServerOperator = user.getUsername() + "_订单统计_" + DateUtil.getNow();

	UserGroupBean group = user.getGroup();
	boolean isTuiguang = (user.getPermission() == 5);	//推广部

String curDate = request.getParameter("date");
String phone = StringUtil.convertNull(request.getParameter("phone"));
if(curDate == null)
	curDate = DateUtil.formatDate(new Date());

//判断是否只显示前100，还是显示所有
String displayall = request.getParameter("displayall");

//2008-04-01 以后，最低完成额 系数改为 78，之前的使用72
int xishu = 78;
Calendar line = Calendar.getInstance();
line.setTime(DateUtil.parseDate("2008-04-01"));

boolean more = (request.getParameter("more")!=null);

	conn = DbUtil.getConnection(DbOperation.DB_SLAVE2);
	Statement st = conn.createStatement();	
%>
<html>
<title>买卖宝后台</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../../header.jsp"%>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center>日期</td>
		<td align=center>订单总数</td>
		<td align=center>去掉重复(均价)</td>
<%if(group.isFlag(28)){%>
		<td align=center>有效订单(均价)</td>
<%}%>
		<td align=center>成交订单(均价)</td>
		<td align=center>已发货(均价)</td>
<%if(group.isFlag(28)){%>
		<td align=center>已妥投(均价)</td>
<%}%>
		<td align=center>退单(均价)</td>
		<td align=center  width="70">成交率</td>
<%if(group.isFlag(28)){%>
		<td align=center  width="70">有效成交率</td>
<%}%>
<%--if(!isTuiguang){ /*对推广部所有人不开放*/%>
        <td align=center>最低完成额</td>
<%}--%>
<%if(group.isFlag(28)){%>
		<td align=center>总价格</td>
<%}%>
	</tr>
<%
    	String orderCondition = " buy_mode in (0,1,2) ";
    	String urladd="";
    	if(orderType>0){
    		orderCondition=" order_type="+orderType+" and "+orderCondition;
    		urladd="orderContent="+orderType+"&";
    	}
    	ResultSet rs = st.executeQuery("select id from user_order order by id desc limit 1");
		rs.next();
		if(more){
			orderCondition = "id>"+(rs.getInt(1)-300000) + " and " + orderCondition;
		} else {
    		orderCondition = "id>"+(rs.getInt(1)-100000) + " and " + orderCondition;
		}
		rs.close();
    	
    	//String sql = "select t1.da,t1.c,t2.c,t2.p,t3.c,t3.p from (select left(create_datetime,10) da,count(*) c from user_order group by da) t1 left outer join (select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status=3 or status=6 or status=9 group by da) t2 on t1.da=t2.da left outer join (select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status<>8 and status<>10 group by da) t3 on t1.da=t3.da order by t1.da desc limit 100";
		String sql = "select t1.da,t1.c,t2.c,t2.p,t3.c,t3.p,t4.c,t4.p,t3.p,t2.p,t5.c,t5.p,t6.c,t6.p,t7.c,t7.p from " +
		    "(select left(create_datetime,10) da,count(*) c from user_order where "+orderCondition+" group by da) t1 " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status in (3,6,9,12,14) and " + orderCondition + " group by da) t2 " +
			"on t1.da=t2.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status<>8 and status<>10 and " + orderCondition + " group by da) t3 " +
			"on t1.da=t3.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status<>10 and " + orderCondition + " group by da) t4 " +
			"on t1.da=t4.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status in (6,11,12,13,14) and " + orderCondition + " group by da) t5 " +
			"on t1.da=t5.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status in (11,13) and " + orderCondition + " group by da) t6 " +
			"on t1.da=t6.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status = 14 and " + orderCondition + " group by da) t7 " +
			"on t1.da=t7.da " +
			"order by t1.da desc";
		if(displayall == null){
			if(more)
				sql += " limit 100";
			else
				sql += " limit 15";
		}
		rs = st.executeQuery(sql);
		while(rs.next()){
			int yijiesuan = rs.getInt("t7.c");
			int tuidan = rs.getInt("t6.c");
			int yifahuo = rs.getInt("t5.c");
			int chengjiao = rs.getInt("t2.c");
%>
			<tr bgcolor='#F8F8F8'>
				<td align=center><%if(!isTuiguang){%><a href="../searchorder.do?<%=urladd%>createDatetime=<%=rs.getString(1)%>" ><%}%><span <%= ((chengjiao == yijiesuan) && ((yijiesuan+tuidan)==yifahuo))?"style=\"color: red;\"":"" %>><%=rs.getString(1)%></span><%if(!isTuiguang){%></a><%}%></td>
				<td align=center><%if(!isTuiguang){%><a href="../searchorder.do?<%=urladd%>createDatetime=<%=rs.getString(1)%>" ><%}%><%=rs.getInt(2)%><%if(!isTuiguang){%></a><%}%></td>
				<td align=center><%if(!isTuiguang){%><a href="../searchorder.do?<%=urladd%>createDatetime=<%=rs.getString(1)%>&status=0,1,2,3,4,5,6,7,8,9,11,12,13,14" ><%}%><%=rs.getInt(7)%><%if(!isTuiguang){%></a><%}%><%if(group.isFlag(28)){%>(<font color="blue"><%=(int)(rs.getInt(7) == 0? 0 : rs.getFloat(8) / rs.getInt(7))%></font>)<%}%></td>
<%if(group.isFlag(28)){%>
				<td align=center><%if(!isTuiguang){%><a href="../searchorder.do?<%=urladd%>createDatetime=<%=rs.getString(1)%>&status=0,1,2,3,4,5,6,7,9,11,12,13,14" ><%}%><%=rs.getInt(5)%><%if(!isTuiguang){%></a><%}%>(<font color="blue"><%=(int)(rs.getInt(5) == 0? 0 : rs.getFloat(9) / rs.getInt(5))%></font>)</td>
<%}%>
				<td align=center><%if(!isTuiguang){%><a href="../searchorder.do?<%=urladd%>createDatetime=<%=rs.getString(1)%>&status=3,6,9,12,14" ><%}%><%=rs.getInt(3)%><%if(!isTuiguang){%></a><%}%><%if(group.isFlag(28)){%>(<font color="blue"><%=(int)(rs.getInt(3) == 0? 0 : rs.getFloat(10) / rs.getInt(3))%></font>)<%}%></td>
				<td align=center><%if(!isTuiguang){%><a href="../searchorder.do?<%=urladd%>createDatetime=<%=rs.getString(1)%>&status=6,11,12,13,14" ><%}%><%=rs.getInt("t5.c")%><%if(!isTuiguang){%></a><%}%><%if(group.isFlag(28)){%>(<font color="blue"><%=(int)(rs.getInt("t5.c") == 0? 0 : rs.getFloat("t5.p") / rs.getInt("t5.c"))%></font>)<%}%></td>
<%if(group.isFlag(28)){%>
				<td align=center><%if(!isTuiguang){%><a href="../searchorder.do?<%=urladd%>createDatetime=<%=rs.getString(1)%>&status=14" ><%}%><%=rs.getInt("t7.c")%><%if(!isTuiguang){%></a><%}%><%if(group.isFlag(28)){%>(<font color="blue"><%=(int)(rs.getInt("t7.c") == 0? 0 : rs.getFloat("t7.p") / rs.getInt("t7.c"))%></font>)<%}%></td>
<%}%>
				<td align=center><%if(!isTuiguang){%><a href="../searchorder.do?<%=urladd%>createDatetime=<%=rs.getString(1)%>&status=11,13" ><%}%><%=rs.getInt("t6.c")%><%if(!isTuiguang){%></a><%}%><%if(group.isFlag(28)){%>(<font color="blue"><%=(int)(rs.getInt("t6.c") == 0? 0 : rs.getFloat("t6.p") / rs.getInt("t6.c"))%></font>)<%}%></td>
				<td align=center><%=NumberUtil.div(rs.getInt("t2.c") * 100, rs.getInt("t4.c"))%>%</td>
<%if(group.isFlag(28)){%>
				<td align=center><%=NumberUtil.div(rs.getInt(3) * 100, rs.getInt(5))%>%</td>
<%}%>
<%
	String date = rs.getString(1);
	if(date != null){
		Calendar c = Calendar.getInstance();
		c.setTime(DateUtil.parseDate(date));
		if(c.before(line)){
			xishu = 72;
		} else {
			xishu = 78;
		}
	}
%>
<%--if(!isTuiguang){ /*对推广部所有人不开放*/%>
                <td align=center><font color="red"><%=(rs.getInt(2) * xishu)%>元</font></td>
<%}--%>
<%if(group.isFlag(28)) /*if(!(isTuiguang && isAdmin))*/{ /*对推广部非高级管理员不开放*/%>
				<td align=right><%if(rs.getFloat(4) > (rs.getInt(2) * xishu)){%><font color="green"><%}%><%=dfprice.format(rs.getFloat(4))%>元</font></td>
<%}%>
			</tr>
		<%}
		rs.close();
		st.close();
	%>
<%if(!more){%><tr><td colspan=3>
<br/><a href="order.jsp?more=1&type=<%=orderType%>">查看更多统计信息</a><br/>(警告：该查询会消耗大量服务器资源)<br/></td></tr>
<%}%>
</table>
</body>
</html><%
	} catch(Exception e){e.printStackTrace();} finally {
DbLock.slaveServerQueryLock.unlock();
conn.close();
	}
%>
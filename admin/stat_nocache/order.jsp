<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*" %>
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

String curDate = request.getParameter("date");
String phone = StringUtil.convertNull(request.getParameter("phone"));
if(curDate == null)
	curDate = DateUtil.formatDate(new Date());

//2008-04-01 以后，最低完成额 系数改为 78，之前的使用72
int xishu = 78;
Calendar line = Calendar.getInstance();
line.setTime(DateUtil.parseDate("2008-04-01"));

Connection conn = DbUtil.getConnection("java:/comp/env/jdbc/adult");
	Statement st = conn.createStatement();	

try{


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
		<td align=center>有效订单(均价)</td>
<%if(group.isFlag(28)) /*if(!(isTuiguang && isAdmin))*/{	/*对推广部普通管理员不开放*/%>
		<td align=center>成交订单(均价)</td>
		<td align=center  width="100">成交率</td>
		<td align=center  width="100">有效成交率</td>
<%}%>
<%if(group.isFlag(29)) /*if(!isTuiguang)*/{ /*对推广部所有人不开放*/%>
        <td align=center>最低完成额</td>
<%}%>
<%if(group.isFlag(28)) /*if(!(isTuiguang && isAdmin))*/{ /*对推广部非高级管理员不开放*/%>
		<td align=center>总价格</td>
<%}%>
	</tr>
<%
    try {
    	//String sql = "select t1.da,t1.c,t2.c,t2.p,t3.c,t3.p from (select left(create_datetime,10) da,count(*) c from user_order group by da) t1 left outer join (select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status=3 or status=6 or status=9 group by da) t2 on t1.da=t2.da left outer join (select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status<>8 and status<>10 group by da) t3 on t1.da=t3.da order by t1.da desc limit 100";
		String sql = "select t1.da,t1.c,t2.c,t2.p,t3.c,t3.p,t4.c,t4.p,t3.p,t2.p from " +
		    "(select left(create_datetime,10) da,count(*) c from user_order group by da) t1 " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status in (3,6,12) group by da) t2 " +
			"on t1.da=t2.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status<>8 and status<>10 group by da) t3 " +
			"on t1.da=t3.da " +
			"left outer join " +
			"(select left(create_datetime,10) da,count(*) c,sum(dprice) p from user_order where status<>10 group by da) t4 " +
			"on t1.da=t4.da " +
			"order by t1.da desc limit 100";
		ResultSet rs = st.executeQuery(sql);
		while(rs.next()){%>
			<tr bgcolor='#F8F8F8'>
				<td align=center><%if(!(isTuiguang && isAdmin)){%><a href="../searchorder.do?createDatetime=<%=rs.getString(1)%>" ><%}%><%=rs.getString(1)%><%if(!isTuiguang){%></a><%}%></td>
				<td align=center><%=rs.getInt(2)%></td>
				<td align=center><%=rs.getInt(7)%>(<font color="blue"><%=(int)(rs.getInt(7) == 0? 0 : rs.getFloat(8) / rs.getInt(7))%></font>)</td>
				<td align=center><%=rs.getInt(5)%>(<font color="blue"><%=(int)(rs.getInt(5) == 0? 0 : rs.getFloat(9) / rs.getInt(5))%></font>)</td>
<%group.isFlag(28) if(!(isTuiguang && isAdmin)){	/*对推广部普通管理员不开放*/%>
				<td align=center><%=rs.getInt(3)%>(<font color="blue"><%=(int)(rs.getInt(3) == 0? 0 : rs.getFloat(10) / rs.getInt(3))%></font>)</td>
				<td align=center><%=NumberUtil.div(rs.getInt(3) * 100, rs.getInt(2))%>%</td>
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
<%if(group.isFlag(29)) /*if(!isTuiguang)*/{ /*对推广部所有人不开放*/%>
                <td align=center><font color="red"><%=(rs.getInt(2) * xishu)%>元</font></td>
<%}%>
<%if(group.isFlag(28)) /*if(!(isTuiguang && isAdmin))*/{ /*对推广部非高级管理员不开放*/%>
				<td align=right><%if(rs.getFloat(4) > (rs.getInt(2) * xishu)){%><font color="green"><%}%><%=NumberUtil.price(rs.getFloat(4))%>元</font></td>
<%}%>
			</tr>
		<%}
		rs.close();
	} catch (Exception e) {e.printStackTrace();}
	%>
	
</table>

<%
	st.close();
} catch(Exception e){e.printStackTrace();}
conn.close();

%>

</body>
</html>
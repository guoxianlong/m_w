<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,adultadmin.util.db.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.bean.*" %>
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
	
	if (!DbLock.slaveServerQueryLocked(100)) {
		response.sendRedirect(request.getContextPath()+"/tip.jsp?db=adult_slave");
		return;
    }
	try{
	DbLock.slaveServerOperator = user.getUsername() + "_发货统计_" + DateUtil.getNow();
    Connection conn = DbUtil.getConnection(DbOperation.DB_SLAVE);
	Statement st = conn.createStatement();
	Connection conn1 = DbUtil.getConnection(DbOperation.DB_SLAVE);
	Statement st1 = conn1.createStatement();
try{

int p = StringUtil.StringToId(request.getParameter("p"));
%>
<html>
<title>买卖宝后台</title>

<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../../header.jsp"%>
<%if(p>0){%><a href="stockStat.jsp?p=<%=p-1%>">上一页</a><%}else{%>上一页<%}%>
&nbsp;<a href="stockStat.jsp?p=<%=p+1%>">下一页</a>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center>发货日期</td>
		<td align=center>总发单</td>
<%if(group.isFlag(28)){%>
		<td align=center>总金额</td>
<%}%>
		<td align=center>退单</td>		
<%if(group.isFlag(28)){%>
		<td align=center>金额</td>
<%}%>
		<td align=center>成都发单</td>
<%if(group.isFlag(28)){%>
		<td align=center>金额</td>
<%}%>
		<td align=center>退单</td>
<%if(group.isFlag(28)){%>
		<td align=center>金额</td>
<%}%>
		<td align=center>无锡发单</td>
<%if(group.isFlag(28)){%>
		<td align=center>金额</td>
<%}%>
		<td align=center>退单</td>
<%if(group.isFlag(28)){%>
		<td align=center>金额</td>
<%}%>
	</tr>
<%
    	st.executeUpdate("set group_concat_max_len=1048576;");
    	String sql;
		String date = null;
		int orderCount = 0, cdCount = 0, cdTuiCount = 0, jsCount = 0, jsTuiCount = 0;
		float price = 0, cdPrice = 0, cdTuiPrice = 0, jsPrice = 0, jsTuiPrice = 0;
		String [] ss = null;
		ResultSet rs1 = null;
		for(int i = p*30;i<p*30+30;i++){
			
			//北京出货记录
			sql = "select date(date_add(curdate(),interval -"+i+" day))"+
				",count(if(stock_area=9,1,null)),sum(if(stock_area=9,b.dprice,0))"+
				",count(if(stock_area=9 and b.status=11,1,null)),sum(if(stock_area=9 and b.status=11,b.dprice,0))"+
				",count(if(stock_area=4,1,null)),sum(if(stock_area=4,b.dprice,0))"+
				",count(if(stock_area=4 and b.status=11,1,null)),sum(if(stock_area=4 and b.status=11,b.dprice,0))"+
				" from order_stock a,user_order b where a.last_oper_time between date_add(curdate(),interval -"+i+" day)" +
					"and date_add(date_add(curdate(),interval -"+(i-1)+" day),interval-1 second) and a.status in (2,4,6,7,8) and a.order_id=b.id";
			rs1 = st1.executeQuery(sql);
			rs1.next();
			int j=0;
			date = rs1.getString(++j);//发货日期
			cdCount = rs1.getInt(++j);//成都发单
			cdPrice = rs1.getFloat(++j);//成都金额
			cdTuiCount = rs1.getInt(++j);//成都退单
			cdTuiPrice = rs1.getFloat(++j);//成都退单金额
			jsCount = rs1.getInt(++j);//无锡发单
			jsPrice = rs1.getFloat(++j);//无锡金额
			jsTuiCount = rs1.getInt(++j);//无锡退单
			jsTuiPrice = rs1.getFloat(++j);//无锡退单金额
			rs1.close();
%>
	<tr bgcolor='#F8F8F8'>
	    <td align=center><a href="../searchorder.do?stockDate=<%=date%>"><%=date%></a></td>
		<td align=center><a href="../searchorder.do?stockDate=<%=date%>"><%=cdCount+jsCount%></a></td>
<%if(group.isFlag(28)){%>
		<td align=right><%=(int)(cdPrice + jsPrice)%></td>
<%}%>
		<td align=center><a href="../searchorder.do?stockDate=<%=date%>&orderStatus=11"><%=(cdTuiCount + jsTuiCount)%></a></td>		
<%if(group.isFlag(28)){%>
		<td align=right><%=(int)(cdTuiPrice + jsTuiPrice)%></td>
<%}%>
		<td align=center><a href="../searchorder.do?stockDate=<%=date%>&stockArea=9"><%=cdCount%></a></td>
<%if(group.isFlag(28)){%>
		<td align=right><%=(int)(cdPrice)%></td>
<%}%>
		<td align=center><a href="../searchorder.do?stockDate=<%=date%>&stockArea=9&orderStatus=11"><%=cdTuiCount%></a></td>
<%if(group.isFlag(28)){%>
		<td align=right><%=(int)(cdTuiPrice)%></td>
<%}%>
		<td align=center><a href="../searchorder.do?stockDate=<%=date%>&stockArea=4"><%=jsCount%></a></td>
<%if(group.isFlag(28)){%>
		<td align=right><%=(int)(jsPrice)%></td>
<%}%>
		<td align=center><a href="../searchorder.do?stockDate=<%=date%>&stockArea=4&orderStatus=11"><%=jsTuiCount%></a></td>
<%if(group.isFlag(28)){%>
		<td align=right><%=(int)(jsTuiPrice)%></td>
<%}%>
	</tr>
<%
	    }
	%>
	
</table>

<%
	st.close();
    st1.close();
} catch(Exception e){
	e.printStackTrace();
} finally{
	if(conn != null){
		conn.close();
	}
	if(conn1 != null){
		conn1.close();
	}
}
%>

</body>
</html>
<%
	} catch(Exception e){
		e.printStackTrace();
	} finally {
		DbLock.slaveServerQueryLock.unlock();
	}
%>
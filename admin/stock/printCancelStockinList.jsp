<%@ page contentType="text/html;charset=utf-8" %>
<%@page import="adultadmin.bean.stock.StockHistoryBean"%>
<%@ include file="../../taglibs.jsp"%>
<%@ page import="java.sql.*,java.util.*"%>
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
	
	int deliver = StringUtil.StringToId(request.getParameter("deliver"));
	String startTime = StringUtil.convertNull(request.getParameter("startTime"));
	String endTime = StringUtil.convertNull(request.getParameter("endTime"));
	String startHour= StringUtil.convertNull(request.getParameter("startHour"));
	String startMin= StringUtil.convertNull(request.getParameter("startMin"));
	String endHour= StringUtil.convertNull(request.getParameter("endHour"));
	String endMin= StringUtil.convertNull(request.getParameter("endMin"));
	String cancelUserName= StringUtil.convertNull(request.getParameter("cancelUserName"));
	
	response.reset();
	response.setContentType("application/vnd.ms-excel;charset=utf-8");
	String now = DateUtil.getNow().substring(0,10);
	String fileName = "THD_"+now;
	response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xls\"");

	Connection conn = adultadmin.util.db.DbUtil.getConnection("adult_slave");
	Statement st = conn.createStatement();
	PreparedStatement pst = null;
	ResultSet rs = null;
	ResultSet rs2 = null;
    try {
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">  
<meta http-equiv="pragma" content="no-cache">  
<meta http-equiv="cache-control" content="no-cache">
</head>
<body>
<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center border=1>
<%
			
			if(!startTime.equals("")&&!endTime.equals("")){
				if("".equals(startHour)){
					startHour="00";
				}
				if("".equals(startMin)){
					startMin="00";
				}
				if("".equals(endHour)){
					endHour="23";
				}
				if("".equals(endMin)){
					endMin="59";
				}
				startTime=startTime+" "+startHour+":"+startMin+":00";
				endTime = endTime + " "+endHour+":"+endMin+":59";
			
			String sql = "select so.create_datetime, uo.code, uo.name, uo.remark,uo.dprice, mb.stockout_datetime,so.id from stock_operation so join user_order uo on so.order_code = uo.code "+
					     "join mailing_balance mb on uo.id = mb.order_id where so.create_datetime between '"+startTime+"' and '"+endTime+"'";
			if(deliver>0){
				sql = sql + " and uo.deliver = "+deliver;
			}
			if(!"".equals(cancelUserName)){
				sql=sql+" and so.user_name='"+cancelUserName+"'";
			}
			sql = sql +" order by so.create_datetime desc";
%>
	<tr>
		<td align=center>退/换货日期</td>
		<td align=center>时间</td>
		<td align=center>订单号</td>
		<td align=center>客户姓名</td>
		<td align=center>包裹单号</td>
		<td align=center>订单金额</td>
		<td align=center>发货日期</td>
		<td align=center>产品一级分类</td>
		<td align=center>产品编号</td>
		<td align=center>商品名称</td>
		<td align=center>商品数量</td>
	</tr>
<%
    		rs = st.executeQuery(sql);
    		
    		while(rs.next()){
    		int operId = rs.getInt("so.id");
    		String cancleTime = rs.getString("so.create_datetime");
    		String orderCode = rs.getString("uo.code");
    		String name = rs.getString("uo.name");
    		String remark = rs.getString("uo.remark");
    		String packageNum = "";
    		float price = rs.getFloat("uo.dprice");
    		String stockoutTime = rs.getString("mb.stockout_datetime");
    		String[] s = remark.split("&&&");
    		if (s.length > 3){
            	packageNum = s[3];
            }
    		
    		pst = conn.prepareStatement("select p.oriname,sh.stock_bj,p.parent_id1,p.code,cata.name from stock_history sh join product p on sh.product_id = p.id join catalog cata on p.parent_id1=cata.id where oper_id = ? and sh.stock_type = "+StockHistoryBean.IN);
    		pst.setInt(1,operId);
    		rs2 = pst.executeQuery();
    		int i = 0;
    		while(rs2.next()){
    			i++;
    			String productName = rs2.getString("p.oriname");
    			int stockCount = rs2.getInt("sh.stock_bj");
    			String parentId1Name=rs2.getString("cata.name");
    			String code=rs2.getString("p.code");
%>			
			<tr>
			<%if(i==1){%>
				<td align=center><%=cancleTime.substring(0,10) %></td>
				<td align=center><%=cancleTime.substring(11,19) %></td>
				<td align=center><%=orderCode %></td>
				<td align=center><%=name %></td>
				<td align=center><%=packageNum %></td>
				<td align=center><%=df.format(price) %></td>
				<td align=center><%=stockoutTime.substring(0,10) %></td>
			<%}else{ %>
				<td align=center></td>
				<td align=center></td>
				<td align=center></td>
				<td align=center></td>
				<td align=center></td>
				<td align=center></td>
				<td align=center></td>
			<%} %>
				<td align=center><%=parentId1Name %></td>
				<td align=center><%=code %></td>
				<td align=center><%=productName %></td>
				<td align=center><%=stockCount %></td>
			</tr>
<%			
			}
			pst.close();
			}
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
</body>
</html>

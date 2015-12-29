<%@ page contentType="text/html;charset=utf-8" %>
<%@ include file="../../taglibs.jsp"%>
<%@ page import="java.sql.*,java.util.*"%>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.service.*" %>
<%@ page import="adultadmin.bean.*" %>
<%
	voUser user = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	
	response.setContentType("application/vnd.ms-excel;charset=gb2312");
	String now = DateUtil.getNow().substring(0,10);
	String fileName = now+" weifenjian";
	response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xls\"");
	
	Connection conn = adultadmin.util.db.DbUtil.getConnection("adult_slave");
	Statement st = conn.createStatement();
	ResultSet rs = null;
    try {
    	String sql = "select os.name,os.order_code,os.create_datetime,p.code,p.oriname,osp.stockout_count,sum(ps.stock) s1,sum(ps.lock_count) s2 from "+
		 "order_stock os left join order_stock_product osp on os.id = osp.order_stock_id left join product p on osp.product_id = p.id "+
		 "left join product_stock ps on p.id = ps.product_id "+
		 "where os.status = 5 and ps.type = 0 group by os.order_id,osp.product_id order by osp.stockout_count desc";
    	
    	//获取订单发货总量
    	HashMap countMap = new HashMap();
		rs = st.executeQuery(sql);
		while(rs.next()){
			String productCode = rs.getString("p.code");
			int count = rs.getInt("osp.stockout_count");
			if(countMap.get(productCode) == null){
				countMap.put(productCode,String.valueOf(count));
			}else{
				count = count + StringUtil.StringToId((String)countMap.get(productCode));
				countMap.put(productCode,String.valueOf(count));
			}
		}
		rs.close();

		//获取订单发货信息
		HashMap orderStrMap = new HashMap();
		rs = st.executeQuery(sql);
		while(rs.next()){
			String productCode = rs.getString("p.code");
			int count = rs.getInt("osp.stockout_count");
			String orderCode = rs.getString("os.order_code");
			if(orderStrMap.get(productCode) == null){
				orderStrMap.put(productCode,orderCode+"("+count+")");
			}else{
				String s = (String)orderStrMap.get(productCode);
				s = s + "\n";
				orderStrMap.put(productCode,s+orderCode+"("+count+")");
			}
		}
		rs.close();
		
    	rs = st.executeQuery(sql);
%>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">  
<meta http-equiv="pragma" content="no-cache">  
<meta http-equiv="cache-control" content="no-cache">  
<meta http-equiv="expires" content="0"> 
<table width="100%" cellpadding="3" cellspacing="1" border="1">
	<tr>
    	<td width="120" align="center"><strong>产品编号</strong></td>
    	<td width="300" align="center"><strong>产品原名称</strong></td>
    	<td width="80" align="center"><strong>订单发货总量</strong></td>
    	<td width="80" align="center"><strong>合格库可发货量</strong></td>
    	<td width="80" align="center"><strong>合格库锁定量</strong></td>
		<td width="150" align="center"><strong>订单号(发货量)</strong></td>
	</tr>
	<%
		List list = new ArrayList();
		while(rs.next()){
			if(list.contains(rs.getString("p.code"))){
				continue;
			}else{
				list.add(rs.getString("p.code"));
			}
	%>
	<tr>
    	<td align="left"><%=rs.getString("p.code") %></td>
    	<td align="left"><%=rs.getString("p.oriname") %></td>
    	<td align="center"><%=countMap.get(rs.getString("p.code")) %></td>
    	<td align="center"><%=rs.getInt("s1") %></td>
    	<td align="center"><%=rs.getInt("s2") %></td>
    	<td align="center"><%=orderStrMap.get(rs.getString("p.code")) %></td>
	</tr>
	<%
		}
	%>
</table>
<%
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
<%@page import="adultadmin.util.DateUtil"%>
<%@page import="adultadmin.util.Arith"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.sql.Statement"%>
<%@page import="java.sql.Connection"%>
<%@page import="adultadmin.util.db.DbOperation"%>
<%@page import="java.util.*,java.io.*"%>
<%@page import="adultadmin.util.StringUtil"%>
<%@page import="adultadmin.bean.buy.*,adultadmin.service.*,adultadmin.service.infc.*"%><%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<html>
<title>买卖宝后台</title>
<script language="JavaScript" src="js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<logic:notEmpty name="tip">
<script language="JavaScript">
alert('<bean:write name="tip" />');
</script>
</logic:notEmpty>
<body>
<%@include file="../../header.jsp"%>
<%
String result = StringUtil.convertNull((String)request.getAttribute("result"));

String method = StringUtil.convertNull(request.getParameter("method"));
int count = StringUtil.StringToId(request.getParameter("count"));
int id = StringUtil.StringToId(request.getParameter("id"));

	DbOperation dbOp = new DbOperation();
	dbOp.init("adult");
	Connection conn = dbOp.getConn();
	Statement stat0 = conn.createStatement();
	Statement stat1 = conn.createStatement();
	ResultSet rs = null;
	try{
		if(method.equals("ps")){
			boolean update = false;
			
			int productId = 0;
			int oriStock = 0;
			int oriLockCount = 0;
			int newStock = 0;
			int newLockCount = 0;
			
			rs = stat1.executeQuery("select * from product_stock where id = "+id);
			if(rs.next()){
				productId = rs.getInt("product_id");
				oriStock = rs.getInt("stock");
				oriLockCount = rs.getInt("lock_count");
			
				boolean check = true;
				if(count > 0 && oriStock < count){
					result = "可用量不足";
					check = false;
				}
				if(count < 0 && oriLockCount < Math.abs(count)){
					result = "可用量不足";
					check = false;
				}
				
				if(check){
					stat0.executeUpdate("update product_stock set stock = stock - "+count+", lock_count = lock_count + "+count+" where id = "+id);
					update = true;
				}
			}else{
				result = "记录不存在，错误id";
			}
			rs.close();
			
			if(update){
				rs = stat1.executeQuery("select * from product_stock where id = "+id);
				if(rs.next()){
					newStock = rs.getInt("stock");
					newLockCount = rs.getInt("lock_count");
				}
				
				result = "商品id："+productId+";操作前： stock="+oriStock+", lock_count="+oriLockCount+";操作后：stock="+newStock+",lock_count="+newLockCount;
				stat0.executeUpdate("insert into cargo_product_stock_rp(repair_id,content,log_datetime,type) values("+id+",'"+result+"','"+DateUtil.getNow()+"',1)");
			}
		}
		
		if(method.equals("ps_stock")){
			boolean update = false;
			
			int productId = 0;
			int oriStock = 0;
			int oriLockCount = 0;
			int newStock = 0;
			int newLockCount = 0;
			
			rs = stat1.executeQuery("select * from product_stock where id = "+id);
			if(rs.next()){
				productId = rs.getInt("product_id");
				oriStock = rs.getInt("stock");
			
				boolean check = true;
				if(count < 0 && oriStock < Math.abs(count)){
					result = "可用量不足";
					check = false;
				}
				
				if(check){
					stat0.executeUpdate("update product_stock set stock = stock + "+count+" where id = "+id);
					update = true;
				}
			}else{
				result = "记录不存在，错误id";
			}
			rs.close();
			
			if(update){
				rs = stat1.executeQuery("select * from product_stock where id = "+id);
				if(rs.next()){
					newStock = rs.getInt("stock");
				}
				
				result = "商品id："+productId+";操作前： stock="+oriStock+";操作后：stock="+newStock;
				stat0.executeUpdate("insert into cargo_product_stock_rp(repair_id,content,log_datetime,type) values("+id+",'"+result+"','"+DateUtil.getNow()+"',1)");
			}
		}
		
		if(method.equals("cps")){
			boolean update = false;
			
			int productId = 0;
			int oriStock = 0;
			int oriLockCount = 0;
			int newStock = 0;
			int newLockCount = 0;
			
			rs = stat1.executeQuery("select * from cargo_product_stock where id = "+id);
			if(rs.next()){
				productId = rs.getInt("product_id");
				oriStock = rs.getInt("stock_count");
				oriLockCount = rs.getInt("stock_lock_count");
			
				boolean check = true;
				if(count > 0 && oriStock < count){
					result = "可用量不足";
					check = false;
				}
				if(count < 0 && oriLockCount < Math.abs(count)){
					result = "可用量不足";
					check = false;
				}
				
				if(check){
					stat0.executeUpdate("update cargo_product_stock set stock_count = stock_count - "+count+", stock_lock_count = stock_lock_count + "+count+" where id = "+id);
					update = true;
				}
			}else{
				result = "记录不存在，错误id";
			}
			rs.close();
			
			if(update){
				rs = stat1.executeQuery("select * from cargo_product_stock where id = "+id);
				if(rs.next()){
					newStock = rs.getInt("stock_count");
					newLockCount = rs.getInt("stock_lock_count");
				}
				
				result = "商品id："+productId+";操作前： stock_count="+oriStock+", stock_lock_count="+oriLockCount+";操作后：stock_count="+newStock+",stock_lock_count="+newLockCount;
				stat0.executeUpdate("insert into cargo_product_stock_rp(repair_id,content,log_datetime,type) values("+id+",'"+result+"','"+DateUtil.getNow()+"',2)");
			}
		}
		
		if(method.equals("cps_stock")){
			boolean update = false;
			
			int productId = 0;
			int oriStock = 0;
			int oriLockCount = 0;
			int newStock = 0;
			int newLockCount = 0;
			
			rs = stat1.executeQuery("select * from cargo_product_stock where id = "+id);
			if(rs.next()){
				productId = rs.getInt("product_id");
				oriStock = rs.getInt("stock_count");
			
				boolean check = true;
				if(count < 0 && oriStock < Math.abs(count)){
					result = "可用量不足";
					check = false;
				}
				
				if(check){
					stat0.executeUpdate("update cargo_product_stock set stock_count = stock_count + "+count+" where id = "+id);
					update = true;
				}
			}else{
				result = "记录不存在，错误id";
			}
			rs.close();
			
			if(update){
				rs = stat1.executeQuery("select * from cargo_product_stock where id = "+id);
				if(rs.next()){
					newStock = rs.getInt("stock_count");
				}
				
				result = "商品id："+productId+";操作前： stock_count="+oriStock+";操作后：stock_count="+newStock;
				stat0.executeUpdate("insert into cargo_product_stock_rp(repair_id,content,log_datetime,type) values("+id+",'"+result+"','"+DateUtil.getNow()+"',2)");
			}
		}
		
		if(method.equals("ci")){
			boolean update = false;
			
			String cargoCode = "";
			int oriSpaceLock = 0;
			int newSpaceLock = 0;
			
			rs = stat1.executeQuery("select * from cargo_info where id = "+id);
			if(rs.next()){
				cargoCode = rs.getString("whole_code");
				oriSpaceLock = rs.getInt("space_lock_count");
			
				boolean check = true;
				if(count < 0 && oriSpaceLock < Math.abs(count)){
					result = "可用量不足";
					check = false;
				}
				
				if(check){
					stat0.executeUpdate("update cargo_info set space_lock_count = space_lock_count + "+count+" where id = "+id);
					update = true;
				}
			}else{
				result = "记录不存在，错误id";
			}
			rs.close();
			
			if(update){
				rs = stat1.executeQuery("select * from cargo_info where id = "+id);
				if(rs.next()){
					newSpaceLock = rs.getInt("space_lock_count");
				}
				
				result = "货位号："+cargoCode+";操作前： space_lock_count="+oriSpaceLock+";操作后：space_lock_count="+newSpaceLock;
				stat0.executeUpdate("insert into cargo_product_stock_rp(repair_id,content,log_datetime,type) values("+id+",'"+result+"','"+DateUtil.getNow()+"',3)");
			}
		}
		
		if(method.equals("co")){
			boolean update = false;
			
			String code = "";
			int oriEffectStatus = 0;
			int newEffectStatus = 0;
			
			rs = stat1.executeQuery("select * from cargo_operation where id = "+id);
			if(rs.next()){
				code = rs.getString("code");
				oriEffectStatus = rs.getInt("effect_status");
			
				boolean check = true;
				if(check){
					stat0.executeUpdate("update cargo_operation set effect_status = "+count+" where id = "+id);
					update = true;
				}
			}else{
				result = "记录不存在，错误id";
			}
			rs.close();
			
			if(update){
				rs = stat1.executeQuery("select * from cargo_operation where id = "+id);
				if(rs.next()){
					newEffectStatus = rs.getInt("effect_status");
				}
				
				result = "单据号："+code+";操作前： effect_status="+oriEffectStatus+";操作后：effect_status="+newEffectStatus;
				stat0.executeUpdate("insert into cargo_product_stock_rp(repair_id,content,log_datetime,type) values("+id+",'"+result+"','"+DateUtil.getNow()+"',4)");
			}
		}
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		dbOp.release();
	}
 %>
 
<form method=post action="repairPS.jsp" name="importForm">
<table width="40%" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF" align=center>
<tr><td>
 <fieldset>
   <legend>product_stock</legend>
<table width="90%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=left>
		lock_count：<input type="text" name="count"/>&nbsp;&nbsp;psID:<input type="text" name="id"><input type=submit value=" 提 交 ">
		</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align="center"><%if(method.equals("ps")){%><%=result %><%} %></td>
	</tr>
</table>
</fieldset>
</td></tr>
</table>
<input type="hidden" name="method" value="ps"/>
</form>
<br/>
<br/>
<form method=post action="repairPS.jsp" name="importForm">
<table width="40%" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF" align=center>
<tr><td>
 <fieldset>
   <legend>product_stock Warning!!!</legend>
<table width="90%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=left>
		stock：<input type="text" name="count"/>&nbsp;&nbsp;psID:<input type="text" name="id"><input type=submit value=" 提 交 ">
		</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align="center"><%if(method.equals("ps_stock")){%><%=result %><%} %></td>
	</tr>
</table>
</fieldset>
</td></tr>
</table>
<input type="hidden" name="method" value="ps_stock"/>
</form>
<br/>
<br/>
<form method=post action="repairPS.jsp" name="importForm">
<table width="40%" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF" align=center>
<tr><td>
 <fieldset>
   <legend>cargo_product_stock</legend>
<table width="90%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=left>
		stock_lock_count：<input type="text" name="count"/>&nbsp;&nbsp;cpsID:<input type="text" name="id"><input type=submit value=" 提 交 ">
		</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align="center"><%if(method.equals("cps")){%><%=result %><%} %></td>
	</tr>
</table>
</fieldset>
</td></tr>
</table>
<input type="hidden" name="method" value="cps"/>
</form>
<br/>
<br/>
<form method=post action="repairPS.jsp" name="importForm">
<table width="40%" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF" align=center>
<tr><td>
 <fieldset>
   <legend>cargo_product_stock Warning!!!</legend>
<table width="90%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=left>
		stock_count：<input type="text" name="count"/>&nbsp;&nbsp;cpsID:<input type="text" name="id"><input type=submit value=" 提 交 ">
		</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align="center"><%if(method.equals("cps_stock")){%><%=result %><%} %></td>
	</tr>
</table>
</fieldset>
</td></tr>
</table>
<input type="hidden" name="method" value="cps_stock"/>
</form>
<br/>
<br/>
<form method=post action="repairPS.jsp" name="importForm">
<table width="40%" cellpadding="3" cellspacing="1" bgcolor="#FFFFFF" align=center>
<tr><td>
 <fieldset>
   <legend>cargo_operation</legend>
<table width="90%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=left>
		effect_status：<input type="text" name="count"/>&nbsp;&nbsp;operID:<input type="text" name="id"><input type=submit value=" 提 交 ">
		</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align="center"><%if(method.equals("co")){%><%=result %><%} %></td>
	</tr>
</table>
</fieldset>
</td></tr>
</table>
<input type="hidden" name="method" value="co"/>
</form>
<br/>
<br/>
</body>
</html> 
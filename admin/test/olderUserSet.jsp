<%@ page import="adultadmin.util.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="adultadmin.util.db.*"%>
<%@ page contentType="text/html;charset=utf-8"%>
<%
int startOrderId = StringUtil.parstInt(request.getParameter("startOrderId"));
int endOrderId = StringUtil.parstInt(request.getParameter("endOrderId"));
int yearStartId = StringUtil.StringToId(request.getParameter("yearStartId"));

	if(startOrderId!=0 && endOrderId!=0){
	DbOperation dbOp = new DbOperation();
	dbOp.init("adult_slave");	 
	DbOperation dbInsert = new DbOperation();
	dbInsert.init(DbOperation.DB);
	long now= System.currentTimeMillis();
	try{
		Statement st = dbInsert.getConn().createStatement();
		int i=1;
		dbOp.prepareStatement("select count(id) from user_order where create_datetime between ? and ? and phone = ? and status in (3,6,9,12,14) and id<> ?");
		ResultSet rs =null;
		ResultSet rs1=null;
		String startDate=null;
		int startId=yearStartId;
			rs=  dbOp.executeQuery("select id ,create_datetime ,phone from user_order where id > "+startOrderId+" and id < "+endOrderId+" and is_olduser=0");
			//boolean flag =true;
			while(rs.next()){
				int id = rs.getInt(1);
				String createDate = rs.getString(2);
				String phone = rs.getString(3);
				if(startDate==null || !startDate.equals(createDate.substring(0,10))){
					startDate = DateUtil.getBackFromDate(createDate, 365);
					if(startId==0)
						startId= StatUtil.getDayFirstOrderId(startDate);	
				}
				if(startId==0){
					out.print("执行失败");
					return;
				}
				dbOp.getPStmt().setString(1,startDate);
				dbOp.getPStmt().setString(2,createDate);
				dbOp.getPStmt().setString(3,phone);
				dbOp.getPStmt().setInt(4,id);
				rs1 = dbOp.getPStmt().executeQuery();
				if(rs1.next()){
					int count = rs1.getInt(1);
					if(count > 0){
						st.executeUpdate(new StringBuilder(" update user_order set is_olduser=1 where id = ").append(id).toString());
						i++;
					}
				}
				rs1.close();
			}
			rs.close();
		st.close();
		out.print("执行成功");
	}catch(Exception e){
		e.printStackTrace();
		out.print("异常信息"+e.getMessage());
	}finally{
		dbOp.release();
		dbInsert.release();
	}
	out.print((System.currentTimeMillis()-now)/60000+"分!");
}
%>
<html>
	<form action="" >
		起始id<input type="text" name="startOrderId" value='<%=startOrderId==0?"":startOrderId %>'/><br/>
		结束id<input type="text" name="endOrderId" value='<%=endOrderId==0?"":endOrderId %>'/><br/>
		一年前id<input type="text" name="yearStartId" value='<%=yearStartId==0?"":yearStartId %>'/>(判断老用户时使用)<br/>
		<input type="submit" value="提交">
	</form>
</html> 
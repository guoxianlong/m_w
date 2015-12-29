<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.db.*,java.sql.*" %>
<%

	Log log = LogFactory.getLog("debug.Log");

	DbOperation dbOp = new DbOperation();
	dbOp.init(DbOperation.DB_SLAVE2);
	Connection conn = dbOp.getConn();
	Statement st = conn.createStatement();
	Statement st2 = conn.createStatement();
	ResultSet rs = null;
	ResultSet rs2 = null;
	try{
		
		//查出所有有库存商品
		rs = st.executeQuery("select * from product_stock where (stock+lock_count) > 0 and type not in (7,8) order by product_id");
		while(rs.next()){//核对所有库库存
			int stockType = rs.getInt("type");
			int stockArea = rs.getInt("area");
			int stockCount = rs.getInt("stock+lock_count");
			int productId = rs.getInt("product_id");
			
		}
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		dbOp.release();
	}
%>
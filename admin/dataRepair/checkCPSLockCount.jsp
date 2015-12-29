<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.db.*,java.sql.*" %>
<%!
public static int[] productIds = {98198};
public static int[] areaIds = {3};
public static int[] typeIds = {4};
%>
<%

DbOperation dbOp = new DbOperation();
dbOp.init(DbOperation.DB_SLAVE2);
try{
	Statement stat = dbOp.getConn().createStatement();
	Statement stat2 = dbOp.getConn().createStatement();
	ResultSet rs = null;
	ResultSet rs2 = null;

	for(int productId:productIds){
		for(int area:areaIds){
			for(int type:typeIds){
				rs = stat.executeQuery("select * from cargo_product_stock cps join cargo_info ci on cps.cargo_id = ci.id where cps.product_id = "+productId+" and ci.area_id = "+area+" and ci.stock_type = "+type);
				while(rs.next()){
					int lockCount = rs.getInt("cps.stock_lock_count");
					String cargoWholeCode = rs.getString("ci.whole_code");
					int cargoId = rs.getInt("ci.id");
					int rightLockCount = 0;

					//报损单
					rs2 = stat2.executeQuery("select sum(b2.bsby_count) s from bsby_operationnote b1 join bsby_product b2 on b1.id = b2.operation_id join bsby_product_cargo b3 on b2.id = b3.bsby_product_id where b1.type = 0 and b1.current_type in (1,6) and b2.product_id = "+productId+" and b1.warehouse_type = "+type+" and b1.warehouse_area = "+area+" and b3.cargo_id = "+cargoId);
					if(rs2.next()){
						rightLockCount = rightLockCount + rs2.getInt(1);
					}
					rs2.close();
					
					//仓内作业单
					rs2 = stat2.executeQuery("select sum(coc.stock_count) s from cargo_operation co join cargo_operation_cargo coc on co.id = coc.oper_id where (co.stock_out_area <> co.stock_in_area or co.stock_out_type <> co.stock_in_type) and co.status in (2,3,4,5,6,11,12,13,14,15,20,21,22,23,24,28,29,30,31,32,33) and co.effect_status in (0,1) and coc.type = 0 and coc.product_id = "+productId+" and coc.out_cargo_whole_code = '"+cargoWholeCode+"'");
					if(rs2.next()){
						rightLockCount = rightLockCount + rs2.getInt(1);
					}
					rs2.close();
					
					//库存调拨单
					rs2 = stat2.executeQuery("select sum(sepc.stock_count) s from stock_exchange se join stock_exchange_product sep on se.id = sep.stock_exchange_id join stock_exchange_product_cargo sepc on sep.id = sepc.stock_exchange_product_id where se.status in (3,5,6) and sepc.type = 0 and se.stock_out_area = "+area+" and se.stock_out_type = "+type+" and sep.product_id = "+productId+" and sepc.cargo_info_id = "+cargoId);
					if(rs2.next()){
						rightLockCount = rightLockCount + rs2.getInt(1);
					}
					rs2.close();
					
					if(lockCount != rightLockCount){
%>
						商品：<%=productId %>&nbsp;&nbsp;&nbsp;货位号：<%=cargoWholeCode %>&nbsp;&nbsp;&nbsp;实际锁定量：<%=lockCount %>&nbsp;&nbsp;&nbsp;应锁定量：<%=rightLockCount %>&nbsp;&nbsp;&nbsp;差：<%=lockCount-rightLockCount %><br/>
<%
					}
				}
			}
		}
	}
}catch(Exception e){
	e.printStackTrace();
}finally{
	dbOp.release();
}
%>
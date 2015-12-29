package adultadmin.test;

import java.sql.ResultSet;
import java.sql.SQLException;

import adultadmin.action.vo.voUser;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class IMEIUtil {
	/**
	 * 验证商品是否是imei码商品
	 * @param imei
	 * @return
	 */
	public static boolean isImeiProduct(int productId){
		boolean exist=false;
		DbOperation db=new DbOperation();
		db.init("adult_slave");
		String sql="select id from imei_product where product_id="+productId;
		ResultSet rs=db.executeQuery(sql);
		try {
			if(rs.next()){
				exist=true;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			db.release();
		}
		return exist;
	}
	
	/**
	 * 验证IMEI码是否存在
	 * @param imei
	 * @return
	 */
	public static boolean imeiExist(String imei){
		boolean exist=false;
		DbOperation db=new DbOperation();
		db.init("adult_slave");
		String sql="select id from imei where code='"+imei+"'";
		ResultSet rs=db.executeQuery(sql);
		try {
			if(rs.next()){
				exist=true;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			db.release();
		}
		return exist;
	}
	
	/**
	 * 根据IMEI码获得相关联的订单id，如果没有相关的订单则返回-1
	 * @param imei
	 * @return
	 */
	public static int getImeiOrderId(String imei){
		DbOperation db=new DbOperation();
		db.init("adult_slave");
		String sql="select order_id from imei_user_order where imei_code='"+imei+"' order by id desc limit 1";
		ResultSet rs=db.executeQuery(sql);
		int orderId=-1;
		try {
			if(rs.next()){
				orderId=rs.getInt(1);
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			db.release();
		}
		
		return orderId;
	}
	
	
	/**
	 * 添加IMEI日志
	 * @param afterSaleOrderCode 售后单号
	 * @param imei IMEI码
	 * @param content 日志内容
	 * @param user 登陆用户
	 * @return
	 */
	public boolean addImeiLog(String afterSaleOrderCode,String imei,String content,voUser user){
		DbOperation db=new DbOperation();
		db.init("adult");
		String sql="insert into imei_log (oper_code,oper_type,imei,content,user_id,user_name,create_datetime) " +
				"values ('"+afterSaleOrderCode+"',5,'"+imei+"','"+content+"',"+user.getId()+",'"+user.getUsername()+"','"+DateUtil.getNow()+"');";
		try{
			if(!db.executeUpdate(sql)){
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			db.release();
		}
		
		return true;
	}
	
	/**
	 * 修改IMEI码状态为可出库
	 * @param imei
	 * @return
	 */
	public boolean updateImeiStatusInStock(String imei){
		DbOperation db=new DbOperation();
		db.init("adult");
		String sql="update imei set status=2 where code='"+imei+"'";
		try{
			if(!db.executeUpdate(sql)){
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			db.release();
		}
		return true;
	}
	
	/**
	 * 根据订单id得到相关的IMEI码
	 * @param orderId
	 * @return
	 */
	public String getOrderImei(int orderId){
		String imei="";
		DbOperation db=new DbOperation();
		db.init("adult_slave");
		String sql="select imei_code from imei_user_order where order_id="+orderId;
		try{
			ResultSet rs=db.executeQuery(sql);
			while(rs.next()){
				imei+=rs.getString(1);
				imei+=",";
			}
			rs.close();
			if(imei.endsWith(",")){
				imei=imei.substring(0,imei.length()-1);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			db.release();
		}
		return imei;
	}
}

package adultadmin.test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voProduct;
import adultadmin.util.CodeUtil;
import adultadmin.util.db.DbOperation;


public class InsertService{
	
	public voOrder insertOrder(String pcode){
		DbOperation dbo=new DbOperation();
		dbo.startTransaction();//事务开始
		voOrder order=new voOrder();
		String tProduct="";
		int productId=0;
		if(!"".equals(pcode)){
			tProduct="select * from product where code='"+pcode+"'";
		}else{
			productId=(int)(Math.random()*1000+7000);//产品id
			tProduct="select * from product where id="+productId;
		}
		
		ResultSet rs3=dbo.executeQuery(tProduct);//得到productId的产品
		String productName="";
		int productPrice=0;
		String productCode="";//产品code
		try {
			while(rs3.next()){
				productId=rs3.getInt("id");
				productName=rs3.getString("name");
				productPrice=Integer.parseInt(rs3.getString("price"));
				productCode=rs3.getString("code");
			}
			rs3.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		voProduct p=new voProduct();
		p.setName(productName);
		//p.setCount(1);
		p.setPrice(productPrice);
		p.setCode(productCode);
		ArrayList productList=new ArrayList();//一个订单中的产品表
		productList.add(p);
		
		int userId=(int)(Math.random()*100+1);
		String tUser="select * from user where id="+userId;
		ResultSet rs1=dbo.executeQuery(tUser);//得到userId的用户
		String userName="";
		String userAddress="";
		String userPhone="";
		String code=new CodeUtil().getOrderCode();//订单的初始code
		try {
			while(rs1.next()){
				userName=rs1.getString("username");
				userAddress=rs1.getString("address").equals("")?"beijinghaidian":rs1.getString("address");
				userPhone=rs1.getString("phone").equals("")?"12345678901":rs1.getString("phone");
			}
			rs1.close();
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String tUserOrder="insert into user_order (user_id,name,phone,address,code,price)" +
				"values("+userId+",'"+userName+"','"+userPhone+"','"+userAddress+"','"+code+"',"+productPrice+")";
		dbo.executeUpdate(tUserOrder);//插入user_order表
		
		
		int codeId = 0;//订单id的后5位
        ResultSet rs4 = dbo.executeQuery("select last_insert_id()");
        try {
	        if(rs4.next())
	        	codeId = rs4.getInt(1);//刚才插入的条目id值，即本身条目id
	        rs4.close();
        } catch(Exception e) {e.printStackTrace();}
        String orderCode = null;//订单code的后5位
        if(codeId > 99999){
        	String strId = String.valueOf(codeId);
        	orderCode = strId.substring(strId.length()- 5, strId.length());//取codeId的后5位当做在code后面追加的id
        } else {
        	orderCode = String.valueOf(codeId);
        }
        StringBuilder updateBuf = new StringBuilder();//得到最终订单code
    	updateBuf.append("update user_order set code=concat(code, '").append(orderCode).append("') where id=").append(codeId);
    	dbo.executeUpdate(updateBuf.toString());
    	
    	updateBuf.delete(0, updateBuf.length());
       	updateBuf.append("insert into order_dist_pool(id, order_code, create_datetime, phone) (select id, code, create_datetime, phone from user_order where id=").append(codeId).append(")");
       	dbo.executeUpdate(updateBuf.toString());//插入order_dist_pool表
       	
		String tUserOrder2="select * from user_order where name='"+userName+"'";
		ResultSet rs2=dbo.executeQuery(tUserOrder2);//从user_order表得到order_id
		int orderId=0;
		Timestamp createDate=null;
		try {
			while(rs2.next()){//得到最后一个插入的该id值
				orderId=rs2.getInt(1);
				createDate=rs2.getTimestamp("create_datetime");
			}
			rs2.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		order.setId(orderId);
		order.setCode(code+orderCode);
		order.setName(userName);
		order.setPhone(userPhone);
		order.setAddress(userAddress);
		order.setCreateDatetime(createDate);
		order.setPrice(productPrice);
		
		String tUserOrderProduct="insert into user_order_product (order_id,product_id,name,price3)" +
		"values("+orderId+","+productId+",'"+productName+"',+"+productPrice+")";
		dbo.executeUpdate(tUserOrderProduct);//插入user_order_product表
		dbo.commitTransaction();//事务结束
		dbo.release();
		return order;
	}
	
	public boolean find(String code){//按照procuctId查商品是否存在
		String sql="select code from product where code="+code;
		DbOperation dbo=new DbOperation();
		ResultSet rs=dbo.executeQuery(sql);
		try {
			if(rs.next()){
				return true;
			}
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			dbo.release();
		}
		return false;
	}
	
	public voProduct findProduct(int orderId){
		String sql="select product_id from user_order_product where order_id="+orderId;
		DbOperation dbo=new DbOperation();
		ResultSet rs=dbo.executeQuery(sql);
		int productId=0;
		try {
			while(rs.next()){
				productId=rs.getInt("product_id");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String sql2="select * from product where id="+productId;
		ResultSet rs2=dbo.executeQuery(sql2);
		voProduct p=new voProduct();
		try {
			while(rs2.next()){
				p.setCode(rs2.getString("code"));
				p.setName(rs2.getString("name"));
				p.setPrice(rs2.getFloat("price"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return p;
	}
	
}

package adultadmin.util;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import adultadmin.action.vo.voOrderProduct;
import adultadmin.util.db.DbOperation;

/**
 * 获得数据兼容 user_Order_product 数据移到user_product_promotion_product
 * 前提：如果前者的数据大于后者
 * 只适用于订单第二次整改
 * @author szl
 *
 */
public class ForderUtil {
	
	public static int PROMOTION_TYPE_YOUHUI=2;//优惠价
	public static int PROMOTION_TYPE_MANJIAN=4;//满减
	public static List getOrderPorducts(DbOperation dbOp,List userProducts,List userPromotionProducts, int orderId){
		List list = null;
		boolean newDbOp = false;
		if(dbOp == null){
			newDbOp = true;
			dbOp = new DbOperation();
			if(!dbOp.init()){
				return list;
			}
		}
		if(userProducts != null && userPromotionProducts != null){
			if(userProducts.size()>userPromotionProducts.size()){
				list = userProducts;
				for(int i = 0; i < list.size(); i ++){
					voOrderProduct vo = (voOrderProduct)list.get(i);
					for(int j = 0; j < userPromotionProducts.size(); j ++){
						voOrderProduct vo1 = (voOrderProduct)userPromotionProducts.get(j);
						if(vo.getProductId()==vo1.getProductId()){//如果user_order_promotion_product中存在
							userProducts.remove(i);
						}
					}
				}
				PreparedStatement psUpdateOrderPromotionProduct = null;
				try{
					psUpdateOrderPromotionProduct = dbOp.getConn().prepareStatement("insert  user_order_promotion_product set product_price=?, discount_price=?, promotion_id=?,count=?, flag=? , order_id="+orderId +", product_id=?");
					if(userProducts != null && userProducts.size()>0){
						for(int i = 0; i < userProducts.size(); i ++){
							voOrderProduct vo = (voOrderProduct)list.get(i);
							psUpdateOrderPromotionProduct.setFloat(1, vo.getProductPrice());
							psUpdateOrderPromotionProduct.setFloat(2, vo.getDiscountPrice());
							if(vo.getProductDiscountId()>0){
								psUpdateOrderPromotionProduct.setInt(3, vo.getProductDiscountId());
								psUpdateOrderPromotionProduct.setInt(5, PROMOTION_TYPE_YOUHUI);
							}else if(vo.getProductPreferenceId()>0){
								psUpdateOrderPromotionProduct.setInt(3, vo.getProductPreferenceId());
								psUpdateOrderPromotionProduct.setInt(5, PROMOTION_TYPE_MANJIAN);
							}else{
								psUpdateOrderPromotionProduct.setInt(3, 0);
								psUpdateOrderPromotionProduct.setInt(5, 0);
							}
							psUpdateOrderPromotionProduct.setInt(4, vo.getCount());
							psUpdateOrderPromotionProduct.setInt(6, vo.getProductId());
							psUpdateOrderPromotionProduct.addBatch();
						}
						psUpdateOrderPromotionProduct.executeBatch();
						list = null;
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally {
					
					if(psUpdateOrderPromotionProduct != null){
						try {
							psUpdateOrderPromotionProduct.close();
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
					if(newDbOp && dbOp != null){
						dbOp.release();
					}
				}
			}else{
				list = userPromotionProducts;
			}
		}
		return list;
	}
}

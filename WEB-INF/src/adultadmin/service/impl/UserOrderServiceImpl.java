/*
 * Created on 2007-1-24
 *
 */
package adultadmin.service.impl;

import java.sql.ResultSet;
import java.util.ArrayList;

import adultadmin.bean.UserOrderProductHistoryBean;
import adultadmin.bean.UserOrderProductSplitHistoryBean;
import adultadmin.bean.order.UserOrderCommonPropertiesBean;
import adultadmin.bean.order.UserOrderPackageTypeBean;
import adultadmin.service.infc.IUserOrderService;
import adultadmin.util.db.DbOperation;

/**
 * 
 */
public class UserOrderServiceImpl extends BaseServiceImpl implements
		IUserOrderService {
	public UserOrderServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public UserOrderServiceImpl() {
		this.useConnType = CONN_IN_SERVICE;
	}

	
	public UserOrderPackageTypeBean getUserOrderPackageType(String condition){
		return (UserOrderPackageTypeBean)getXXX(condition, "user_order_package_type", "adultadmin.bean.order.UserOrderPackageTypeBean");
	}
	
	public boolean addUserOrderCommonProperties(
			UserOrderCommonPropertiesBean bean) {
		return addXXX(bean, "user_order_common_properties");
	}

	public UserOrderCommonPropertiesBean getUserOrderCommonProperties(String condition) {
		
		String sql ="select u.*,t.content content1,t1.content content2 from user_order_common_properties u left join text_dict t on u.order_status= t.id left join text_dict t1 on u.stockout_status=t1.id where ";
		sql+=condition;
		
		DbOperation dbop = this.getDbOp();
		try{
			ResultSet res = dbop.executeQuery(sql);
			if(res.next()){
				UserOrderCommonPropertiesBean bean = new UserOrderCommonPropertiesBean();
				bean.setOrderId(res.getInt("order_id"));
				bean.setOrderStatus(res.getInt("order_status"));
				bean.setStockoutStatus(res.getInt("stockout_status"));
				bean.setStockoutRemark(res.getString("stockout_remark"));
				bean.setUserCansel(res.getInt("user_cansel"));
				bean.setOrderContent(res.getString("content1"));
				bean.setStockoutContent(res.getString("content2"));
				return bean;
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			release(dbop);
		}
		return null;
	}

	public boolean updateUserOrderCommonProperties(String set, String condition) {
		return updateXXX(set, condition, "user_order_common_properties");
	}
	/*
	 * 销售的售后中需要用到 用来计算退款商品实际销售价格
	 */
	public ArrayList getUserOrderProductSplitHistoryList(String condition, int index, int count,
			String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy, "user_order_product_split_history",
				"mmb.aftersale.UserOrderProductSplitHistoryBean");
		return (ArrayList) queryList;
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getUserOrderProductHistoryCount(String condition) {
		int count = 0;
		count = getXXXCount(condition, "user_order_product_history", "id");
		return count;
	}
	
	//end

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getUserOrderProductHistoryList(String condition, int index, int count,
			String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy, "user_order_product_history",
				"adultadmin.bean.UserOrderProductHistoryBean");
		return (ArrayList) queryList;
	}
	
//	private UserOrderProductHistoryBean logUserOrderProduct(List list,voOrder order, int type,
//			IAdminService adminService, IProductPackageService ppService,
//			float productDprice, float djqPrice, voOrderProduct vop,
//			voProduct product) {
//		UserOrderProductHistoryBean uoph = new UserOrderProductHistoryBean();
//		uoph.setOrderId(order.getId());
//		uoph.setProductId(product.getId());
//		uoph.setCount(vop.getCount());
//		if(product.getIsPackage() == 1){	    			// 套装商品，临时价格
//			uoph.setTypeMask(UserOrderProductHistoryBean.TYPE_PACKAGE);
//			List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
//			Iterator ppIter = ppList.listIterator();
//			float totalPackageProductPrice = 0;
//			while(ppIter.hasNext()){
//				ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
//				voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
//				totalPackageProductPrice += tempProduct.getPrice() * ppBean.getProductCount();
//			}
//			uoph.setPriceTemp(totalPackageProductPrice);
//		} else {
//			uoph.setTypeMask(UserOrderProductHistoryBean.TYPE_NORMAL);
//			uoph.setPriceTemp(product.getPrice());
//		}
//		// 设置类型
//		if(((type & (1 << 1)) != 0) && ((type & (1 << 2)) != 0 )){
//		   		uoph.setTypeMask(UserOrderProductHistoryBean.TYPE_FREQUENT);
//		} else if(type == UserOrderProductHistoryBean.TYPE_GROUPBUY){
//				uoph.setTypeMask(UserOrderProductHistoryBean.TYPE_GROUPBUY);
//		}  else if(type == UserOrderProductHistoryBean.TYPE_FREQUENT){
//				uoph.setTypeMask(UserOrderProductHistoryBean.TYPE_FREQUENT);
//		} 
//		// 设置mmb价格和成交价格
//			uoph.setPrice(product.getPrice());
//		// 如果商品总成交价为0 则单品成交价为0	
//			if(productDprice>0){
//				float baseDisCount = vop.getDiscountPrice()*vop.getCount()/productDprice;
//				float dprice =( vop.getDiscountPrice() -  baseDisCount *djqPrice/vop.getCount())*order.getDiscount()+(baseDisCount*order.getPostage()/vop.getCount());
//				if(dprice-0.01<0){dprice=0;}
//				uoph.setDprice( dprice );
//			}else{
//				uoph.setDprice(0);
//			}
//		uoph.setPrice5(product.getPrice5());
//		
//		UserOrderProductHistoryBean pr = null;
//		if(list!=null&&list.size()>0){
//			pr = NormalOrderPriceCalculator.getUserOrderProductHistoryBean(list, product.getId());
//		}
//		if(pr!=null){
//			if(uoph.getCount()!=pr.getCount()||uoph.getDprice()!=pr.getDprice()){
//				this.updateUserOrderProductHistory(" count="+uoph.getCount()+" , dprice="+uoph.getDprice(), " id="+pr.getId());
//			}
//		}else{
//			this.addUserOrderProductHistory(uoph);
//		}
//		return uoph;
//	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateUserOrderProductHistory(String set, String condition) {
		return updateXXX(set, condition, "user_order_product_history");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addUserOrderProductHistory(UserOrderProductHistoryBean uoph) {
		return addXXX(uoph, "user_order_product_history");
	}
	
//	private void logUserOrderProductSplit(List list,voOrder order, int type,
//			IAdminService adminService, IProductPackageService ppService,
//			float productDprice, float djqPrice, voOrderProduct vop,
//			voProduct product, UserOrderProductHistoryBean uoph) {
//		// 记录订单中的商品信息（拆分套装，到split表）
//		if(product.getIsPackage() == 1){
//			List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
//			Iterator ppIter = ppList.listIterator();
//			
//			float totalPackagePrice = 0;
//			while(ppIter.hasNext()){
//				ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
//				voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
//				totalPackagePrice += tempProduct.getPrice()*ppBean.getProductCount();		
//			}
//			ppIter = ppList.listIterator();
//			while(ppIter.hasNext()){
//				ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
//				voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
//				UserOrderProductSplitHistoryBean uopsh = new UserOrderProductSplitHistoryBean();
//				uopsh.setCount(vop.getCount() * ppBean.getProductCount());
//				uopsh.setOrderId(order.getId());
//				uopsh.setProductId(tempProduct.getId());
//				uopsh.setTypeMask(UserOrderProductSplitHistoryBean.TYPE_PACKAGE);
//				uopsh.setProductParentId1(product.getId());
//
//				if(((type & (1 << 1)) != 0) && ((type & (1 << 2)) != 0 )){
//		            uopsh.setTypeMask(UserOrderProductHistoryBean.TYPE_FREQUENT);
//				} else if(type == UserOrderProductHistoryBean.TYPE_GROUPBUY){
//					uopsh.setTypeMask(UserOrderProductHistoryBean.TYPE_GROUPBUY);
//				} else if(type == UserOrderProductHistoryBean.TYPE_FREQUENT) {
//				
//		    		uopsh.setTypeMask(UserOrderProductHistoryBean.TYPE_FREQUENT);
//				} 
//				    uopsh.setPrice(tempProduct.getPrice());
//				    if(uoph.getDprice()>0&&totalPackagePrice>0){
//				    	float dprice = ((float)(tempProduct.getPrice()*ppBean.getProductCount())/totalPackagePrice)*uoph.getDprice()/ppBean.getProductCount();
//				    	if(dprice-0.01<0){dprice=0;}
//				    	uopsh.setDprice(dprice);
//		    		}else{
//		    			uopsh.setDprice(0);
//		    		}
//				  
//				uopsh.setPrice5(tempProduct.getPrice5());
//				UserOrderProductSplitHistoryBean pr2 = null;
//				if(list!=null&&list.size()>0){
//					pr2 = NormalOrderPriceCalculator.getUserOrderProductSplitHistoryBean(list, product.getId());
//				}
//				if(pr2!=null){
//					if(uopsh.getCount()!=pr2.getCount()||uopsh.getDprice()!=pr2.getDprice()){
//						this.updateUserOrderProductSplitHistory(" count="+uopsh.getCount()+",dprice="+uopsh.getDprice(), " id="+pr2.getId());
//					}
//				}else{
//					this.addUserOrderProductSplitHistory(uopsh);
//				}
//			}
//		} else {
//			UserOrderProductSplitHistoryBean uopsh = new UserOrderProductSplitHistoryBean();
//			uopsh.setCount(vop.getCount());
//			uopsh.setOrderId(order.getId());
//			uopsh.setProductId(product.getId());
//			uopsh.setTypeMask(UserOrderProductSplitHistoryBean.TYPE_NORMAL);
//
//			if(((type & (1 << 1)) != 0) && ((type & (1 << 2)) != 0 )){
//					uopsh.setTypeMask(UserOrderProductHistoryBean.TYPE_FREQUENT);
//			} else if(type == UserOrderProductHistoryBean.TYPE_GROUPBUY){
//					uopsh.setTypeMask(UserOrderProductHistoryBean.TYPE_GROUPBUY);
//			} else if(type == UserOrderProductHistoryBean.TYPE_FREQUENT ) {
//				uopsh.setTypeMask(UserOrderProductHistoryBean.TYPE_FREQUENT);
//			} 
//			// 设置mmb价格和成交价格
//			uopsh.setPrice(product.getPrice());
//		    // 如果商品总成交价为0 则单品成交价为0	
//			if(uoph.getDprice()>0&&productDprice>0){
//				float dprice =uoph.getDprice();
//		    	if(dprice-0.01<0){dprice=0;}
//				uopsh.setDprice( dprice );
//			}else{
//				uopsh.setDprice(0);
//			}
//			uopsh.setPrice5(product.getPrice5());
//			UserOrderProductSplitHistoryBean pr2 = null;
//			if(list!=null&&list.size()>0){
//				pr2 = NormalOrderPriceCalculator.getUserOrderProductSplitHistoryBean(list, product.getId());
//			}
//			if(pr2!=null){
//				if(uopsh.getCount()!=pr2.getCount()||uopsh.getDprice()!=pr2.getDprice()){
//					this.updateUserOrderProductSplitHistory(" count="+uopsh.getCount()+",dprice="+uopsh.getDprice(), " id="+pr2.getId());
//				}
//			}else{
//				this.addUserOrderProductSplitHistory(uopsh);
//			}
//		}
//	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteUserOrderProductHistory(String condition) {
		return deleteXXX(condition, "user_order_product_history");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteUserOrderProductSplitHistory(String condition) {
		return deleteXXX(condition, "user_order_product_split_history");
	}
	
//	public boolean logUserOrderPresent(voOrder order){
//		boolean result = true;
//		IAdminService adminService = null;
//		try{
//			adminService = ServiceFactory.createAdminService(this.getDbOp());
//		    IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
//		    	
//			// 清理旧的订单商品信息
//			String condition = "order_id=" + order.getId();
//			ArrayList presentHistoryList = this.getUserOrderPresentHistoryList(condition, -1, -1, null);
//			ArrayList presentSplitHistoryList = this.getUserOrderPresentSplitHistoryList(condition, -1, -1, null);
//			// 记录订单中的赠品信息
//			List presentList = adminService.getOrderPresents(order.getId());
//			Iterator presentIter = presentList.listIterator();
//			while(presentIter.hasNext()){
//				voOrderProduct vop = (voOrderProduct)presentIter.next();
//				voProduct product = adminService.getProduct(vop.getProductId());
//				if(product.getPrice() < 0){
//					continue;
//				}
//		    	UserOrderProductHistoryBean uoph = new UserOrderProductHistoryBean();
//		    	uoph.setOrderId(order.getId());
//		    	uoph.setProductId(product.getId());
//	    		uoph.setCount(vop.getCount());
//	    		if(product.getIsPackage() == 1){
//	    			uoph.setType(UserOrderProductHistoryBean.TYPE_PACKAGE);
//	    			List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
//					Iterator ppIter = ppList.listIterator();
//					float totalPackageProductPrice = 0;
//					while(ppIter.hasNext()){
//						ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
//						voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
//						totalPackageProductPrice += tempProduct.getPrice() * ppBean.getProductCount();
//					}
//					uoph.setPriceTemp(totalPackageProductPrice);
//	    		} else {
//	    			uoph.setTypeMask(UserOrderProductHistoryBean.TYPE_NORMAL);
//	    		}
//	    		uoph.setPrice(product.getPrice());
//	    		//uoph.setDprice(product.getPrice() * (order.getDprice() / order.getPrice()));
//	    		uoph.setDprice(0);
//	    		uoph.setPrice5(product.getPrice5());
//	    		uoph.setPriceTemp(product.getPrice());
//	    		UserOrderProductHistoryBean pr = null;
//				if(presentHistoryList!=null&&presentHistoryList.size()>0){
//					pr = NormalOrderPriceCalculator.getUserOrderProductHistoryBean(presentHistoryList, product.getId());
//				}
//				if(pr!=null){
//					if(uoph.getCount()!=pr.getCount()){
//						this.updateUserOrderPresentHistory(" count="+uoph.getCount(), " id="+pr.getId());
//					}
//				}else{
//					this.addUserOrderPresentHistory(uoph);
//				}
//	    		if(product.getIsPackage() == 1){
//		    		List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
//					Iterator ppIter = ppList.listIterator();
//					while(ppIter.hasNext()){
//						ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
//						voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
//						UserOrderProductSplitHistoryBean uopsh = new UserOrderProductSplitHistoryBean();
//						uopsh.setCount(vop.getCount() * ppBean.getProductCount());
//						uopsh.setOrderId(order.getId());
//						uopsh.setProductId(tempProduct.getId());
//						uopsh.setTypeMask(UserOrderProductSplitHistoryBean.TYPE_PACKAGE);
//						uopsh.setProductParentId1(product.getId());
//						uopsh.setPrice(tempProduct.getPrice());
//						//uopsh.setDprice(product.getPrice() * (tempProduct.getPrice() / uoph.getPriceTemp()) * (order.getDprice() / order.getPrice()));
//						uopsh.setDprice(0);
//						uopsh.setPrice5(tempProduct.getPrice5());
//						UserOrderProductSplitHistoryBean pr2 = null;
//						if(presentSplitHistoryList!=null&&presentSplitHistoryList.size()>0){
//							pr2 = NormalOrderPriceCalculator.getUserOrderProductSplitHistoryBean(presentSplitHistoryList, product.getId());
//						}
//						if(pr2!=null){
//							if(uopsh.getCount()!=pr2.getCount()){
//								this.updateUserOrderPresentSplitHistory(" count="+uopsh.getCount(), " id="+pr2.getId());
//							}
//						}else{
//							this.addUserOrderPresentSplitHistory(uopsh);
//						}
//					}
//		    	} else {
//					UserOrderProductSplitHistoryBean uopsh = new UserOrderProductSplitHistoryBean();
//					uopsh.setCount(vop.getCount());
//					uopsh.setOrderId(order.getId());
//					uopsh.setProductId(product.getId());
//					uopsh.setTypeMask(UserOrderProductSplitHistoryBean.TYPE_NORMAL);
//					uopsh.setPrice(product.getPrice());
//					//uopsh.setDprice(product.getPrice() * (order.getDprice() / order.getPrice()));
//					uopsh.setDprice(0);
//					uopsh.setPrice5(product.getPrice5());
//					UserOrderProductSplitHistoryBean pr2 = null;
//					if(presentSplitHistoryList!=null&&presentSplitHistoryList.size()>0){
//						pr2 = NormalOrderPriceCalculator.getUserOrderProductSplitHistoryBean(presentSplitHistoryList, product.getId());
//					}
//					if(pr2!=null){
//						if(uopsh.getCount()!=pr2.getCount()){
//							this.updateUserOrderPresentSplitHistory(" count="+uopsh.getCount(), " id="+pr.getId());
//						}
//					}else{
//						this.addUserOrderPresentSplitHistory(uopsh);
//					}
//				}
//			}
//			
//			if(presentHistoryList!=null&&presentHistoryList.size()>0){
//				for(int i=0;i<presentHistoryList.size();i++){
//					UserOrderProductHistoryBean ppb = (UserOrderProductHistoryBean)presentHistoryList.get( i );
//					// 删除多余数据。
//					this.deleteUserOrderPresentHistory(" id="+ppb.getId());
//				}
//			}
//			if(presentSplitHistoryList!=null&&presentSplitHistoryList.size()>0){
//				for(int i=0;i<presentSplitHistoryList.size();i++){
//					UserOrderProductSplitHistoryBean ppb = (UserOrderProductSplitHistoryBean)presentSplitHistoryList.get( i );
//					// 删除多余数据。
//					this.deleteUserOrderPresentSplitHistory(" id="+ppb.getId());
//				}
//			}
//		} finally {
//		}
//		return result;
//	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateUserOrderProductSplitHistory(String set, String condition) {
		return updateXXX(set, condition, "user_order_product_split_history");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addUserOrderProductSplitHistory(UserOrderProductSplitHistoryBean uoph) {
		return addXXX(uoph, "user_order_product_split_history");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getUserOrderPresentHistoryList(String condition, int index, int count,
			String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy, "user_order_present_history",
				"adultadmin.bean.UserOrderProductHistoryBean");
		return (ArrayList) queryList;
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getUserOrderPresentSplitHistoryList(String condition, int index, int count,
			String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy, "user_order_present_split_history",
				"adultadmin.bean.UserOrderProductSplitHistoryBean");
		return (ArrayList) queryList;
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateUserOrderPresentHistory(String set, String condition) {
		return updateXXX(set, condition, "user_order_present_history");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateUserOrderPresentSplitHistory(String set, String condition) {
		return updateXXX(set, condition, "user_order_present_split_history");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addUserOrderPresentSplitHistory(UserOrderProductSplitHistoryBean uoph) {
		return addXXX(uoph, "user_order_present_split_history");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteUserOrderPresentHistory(String condition) {
		return deleteXXX(condition, "user_order_present_history");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteUserOrderPresentSplitHistory(String condition) {
		return deleteXXX(condition, "user_order_present_split_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addUserOrderPresentHistory(UserOrderProductHistoryBean uoph) {
		return addXXX(uoph, "user_order_present_history");
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public UserOrderProductHistoryBean getUserOrderProductHistory(String condition) {
		Object queryObject = null;
		queryObject = (UserOrderProductHistoryBean) getXXX(condition, "user_order_product_history",
				"adultadmin.bean.UserOrderProductHistoryBean");

		return (UserOrderProductHistoryBean) queryObject;
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public UserOrderProductSplitHistoryBean getUserOrderProductSplitHistory(String condition) {
		Object queryObject = null;
		queryObject = (UserOrderProductSplitHistoryBean) getXXX(condition, "user_order_product_split_history",
				"adultadmin.bean.UserOrderProductSplitHistoryBean");

		return (UserOrderProductSplitHistoryBean) queryObject;
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public UserOrderProductHistoryBean getUserOrderPresentHistory(String condition) {
		Object queryObject = null;
		queryObject = (UserOrderProductHistoryBean) getXXX(condition, "user_order_present_history",
				"adultadmin.bean.UserOrderProductHistoryBean");

		return (UserOrderProductHistoryBean) queryObject;
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public UserOrderProductSplitHistoryBean getUserOrderPresentSplitHistory(String condition) {
		Object queryObject = null;
		queryObject = (UserOrderProductSplitHistoryBean) getXXX(condition, "user_order_present_split_history",
				"adultadmin.bean.UserOrderProductSplitHistoryBean");

		return (UserOrderProductSplitHistoryBean) queryObject;
	}
}

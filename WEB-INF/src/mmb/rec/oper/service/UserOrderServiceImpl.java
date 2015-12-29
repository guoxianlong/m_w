/*
 * Created on 2007-1-24
 *
 */
package mmb.rec.oper.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserOrderProductHistoryBean;
import adultadmin.bean.UserOrderProductSplitHistoryBean;
import adultadmin.bean.order.UserOrderCommonPropertiesBean;
import adultadmin.bean.order.UserOrderPackageTypeBean;
import adultadmin.service.IAdminService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.util.db.DbOperation;

/**
 * 
 */
public class UserOrderServiceImpl extends BaseServiceImpl  {
	public UserOrderServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addUserOrderProductHistory(UserOrderProductHistoryBean uoph) {
		return addXXX(uoph, "user_order_product_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteUserOrderProductHistory(String condition) {
		return deleteXXX(condition, "user_order_product_history");
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
	public int getUserOrderProductHistoryCount(String condition) {
		int count = 0;
		count = getXXXCount(condition, "user_order_product_history", "id");
		return count;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getUserOrderProductHistoryList(String condition, int index, int count,
			String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy, "user_order_product_history",
				"adultadmin.bean.UserOrderProductHistoryBean");
		return (ArrayList) queryList;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateUserOrderProductHistory(String set, String condition) {
		return updateXXX(set, condition, "user_order_product_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public Object[] getNPUserOrderProductHistory(String listCondition, int currentId,
			String orderBy, int[] npType) {
		Object queryList = getNPXXX(listCondition, currentId, orderBy, npType,
				"user_order_product_history", "adultadmin.bean.UserOrderProductHistoryBean");
		return (Object[]) queryList;
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
	public boolean deleteUserOrderProductSplitHistory(String condition) {
		return deleteXXX(condition, "user_order_product_split_history");
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
	public int getUserOrderProductSplitHistoryCount(String condition) {
		int count = 0;
		count = getXXXCount(condition, "user_order_product_split_history", "id");
		return count;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getUserOrderProductSplitHistoryList(String condition, int index, int count,
			String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy, "user_order_product_split_history",
				"adultadmin.bean.UserOrderProductSplitHistoryBean");
		return (ArrayList) queryList;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateUserOrderProductSplitHistory(String set, String condition) {
		return updateXXX(set, condition, "user_order_product_split_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public Object[] getNPUserOrderProductSplitHistory(String listCondition, int currentId,
			String orderBy, int[] npType) {
		Object queryList = getNPXXX(listCondition, currentId, orderBy, npType,
				"user_order_product_split_history", "adultadmin.bean.UserOrderProductSplitHistoryBean");
		return (Object[]) queryList;
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
	public boolean deleteUserOrderPresentHistory(String condition) {
		return deleteXXX(condition, "user_order_present_history");
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
	public int getUserOrderPresentHistoryCount(String condition) {
		int count = 0;
		count = getXXXCount(condition, "user_order_present_history", "id");
		return count;
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
	public boolean updateUserOrderPresentHistory(String set, String condition) {
		return updateXXX(set, condition, "user_order_present_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public Object[] getNPUserOrderPresentHistory(String listCondition, int currentId,
			String orderBy, int[] npType) {
		Object queryList = getNPXXX(listCondition, currentId, orderBy, npType,
				"user_order_present_history", "adultadmin.bean.UserOrderProductHistoryBean");
		return (Object[]) queryList;
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
	public boolean deleteUserOrderPresentSplitHistory(String condition) {
		return deleteXXX(condition, "user_order_present_split_history");
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

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getUserOrderPresentSplitHistoryCount(String condition) {
		int count = 0;
		count = getXXXCount(condition, "user_order_present_split_history", "id");
		return count;
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
	public boolean updateUserOrderPresentSplitHistory(String set, String condition) {
		return updateXXX(set, condition, "user_order_present_split_history");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public Object[] getNPUserOrderPresentSplitHistory(String listCondition, int currentId,
			String orderBy, int[] npType) {
		Object queryList = getNPXXX(listCondition, currentId, orderBy, npType,
				"user_order_present_split_history", "adultadmin.bean.UserOrderProductSplitHistoryBean");
		return (Object[]) queryList;
	}



	public boolean logUserOrderPresent(voOrder order){
		boolean result = true;
		IAdminService adminService = null;
		try{
			adminService = ServiceFactory.createAdminService(this.getDbOp());
		    IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		    	
			// 清理旧的订单商品信息
			String condition = "order_id=" + order.getId();
			ArrayList presentHistoryList = this.getUserOrderPresentHistoryList(condition, -1, -1, null);
			ArrayList presentSplitHistoryList = this.getUserOrderPresentSplitHistoryList(condition, -1, -1, null);
			// 记录订单中的赠品信息
			List presentList = adminService.getOrderPresents(order.getId());
			Iterator presentIter = presentList.listIterator();
			while(presentIter.hasNext()){
				voOrderProduct vop = (voOrderProduct)presentIter.next();
				voProduct product = adminService.getProduct(vop.getProductId());
				if(product.getPrice() < 0){
					continue;
				}
		    	UserOrderProductHistoryBean uoph = new UserOrderProductHistoryBean();
		    	uoph.setOrderId(order.getId());
		    	uoph.setProductId(product.getId());
	    		uoph.setCount(vop.getCount());
	    		if(product.getIsPackage() == 1){
	    			uoph.setType(UserOrderProductHistoryBean.TYPE_PACKAGE);
	    			List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
					Iterator ppIter = ppList.listIterator();
					float totalPackageProductPrice = 0;
					while(ppIter.hasNext()){
						ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
						voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
						totalPackageProductPrice += tempProduct.getPrice() * ppBean.getProductCount();
					}
					uoph.setPriceTemp(totalPackageProductPrice);
	    		} else {
	    			uoph.setTypeMask(UserOrderProductHistoryBean.TYPE_NORMAL);
	    		}
	    		uoph.setPrice(product.getPrice());
	    		//uoph.setDprice(product.getPrice() * (order.getDprice() / order.getPrice()));
	    		uoph.setDprice(0);
	    		uoph.setPrice5(product.getPrice5());
	    		uoph.setPriceTemp(product.getPrice());
	    		UserOrderProductHistoryBean pr = null;
				if(presentHistoryList!=null&&presentHistoryList.size()>0){
					pr = getUserOrderProductHistoryBean(presentHistoryList, product.getId());
				}
				if(pr!=null){
					if(uoph.getCount()!=pr.getCount()){
						this.updateUserOrderPresentHistory(" count="+uoph.getCount(), " id="+pr.getId());
					}
				}else{
					this.addUserOrderPresentHistory(uoph);
				}
	    		if(product.getIsPackage() == 1){
		    		List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
					Iterator ppIter = ppList.listIterator();
					while(ppIter.hasNext()){
						ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
						voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
						UserOrderProductSplitHistoryBean uopsh = new UserOrderProductSplitHistoryBean();
						uopsh.setCount(vop.getCount() * ppBean.getProductCount());
						uopsh.setOrderId(order.getId());
						uopsh.setProductId(tempProduct.getId());
						uopsh.setTypeMask(UserOrderProductSplitHistoryBean.TYPE_PACKAGE);
						uopsh.setProductParentId1(product.getId());
						uopsh.setPrice(tempProduct.getPrice());
						//uopsh.setDprice(product.getPrice() * (tempProduct.getPrice() / uoph.getPriceTemp()) * (order.getDprice() / order.getPrice()));
						uopsh.setDprice(0);
						uopsh.setPrice5(tempProduct.getPrice5());
						UserOrderProductSplitHistoryBean pr2 = null;
						if(presentSplitHistoryList!=null&&presentSplitHistoryList.size()>0){
							pr2 = getUserOrderProductSplitHistoryBean(presentSplitHistoryList, product.getId());
						}
						if(pr2!=null){
							if(uopsh.getCount()!=pr2.getCount()){
								this.updateUserOrderPresentSplitHistory(" count="+uopsh.getCount(), " id="+pr2.getId());
							}
						}else{
							this.addUserOrderPresentSplitHistory(uopsh);
						}
					}
		    	} else {
					UserOrderProductSplitHistoryBean uopsh = new UserOrderProductSplitHistoryBean();
					uopsh.setCount(vop.getCount());
					uopsh.setOrderId(order.getId());
					uopsh.setProductId(product.getId());
					uopsh.setTypeMask(UserOrderProductSplitHistoryBean.TYPE_NORMAL);
					uopsh.setPrice(product.getPrice());
					//uopsh.setDprice(product.getPrice() * (order.getDprice() / order.getPrice()));
					uopsh.setDprice(0);
					uopsh.setPrice5(product.getPrice5());
					UserOrderProductSplitHistoryBean pr2 = null;
					if(presentSplitHistoryList!=null&&presentSplitHistoryList.size()>0){
						pr2 = getUserOrderProductSplitHistoryBean(presentSplitHistoryList, product.getId());
					}
					if(pr2!=null){
						if(uopsh.getCount()!=pr2.getCount()){
							this.updateUserOrderPresentSplitHistory(" count="+uopsh.getCount(), " id="+pr.getId());
						}
					}else{
						this.addUserOrderPresentSplitHistory(uopsh);
					}
				}
			}
			
			if(presentHistoryList!=null&&presentHistoryList.size()>0){
				for(int i=0;i<presentHistoryList.size();i++){
					UserOrderProductHistoryBean ppb = (UserOrderProductHistoryBean)presentHistoryList.get( i );
					// 删除多余数据。
					this.deleteUserOrderPresentHistory(" id="+ppb.getId());
				}
			}
			if(presentSplitHistoryList!=null&&presentSplitHistoryList.size()>0){
				for(int i=0;i<presentSplitHistoryList.size();i++){
					UserOrderProductSplitHistoryBean ppb = (UserOrderProductSplitHistoryBean)presentSplitHistoryList.get( i );
					// 删除多余数据。
					this.deleteUserOrderPresentSplitHistory(" id="+ppb.getId());
				}
			}
		} finally {
		}
		return result;
	}
	
	public UserOrderPackageTypeBean getUserOrderPackageType(String condition){
		return (UserOrderPackageTypeBean)getXXX(condition, "user_order_package_type", "adultadmin.bean.order.UserOrderPackageTypeBean");
	}
	
	//2012-08-21 yl 修改商品成交价begin
	public boolean logUserOrderProduct(voOrder order, int type){
		IAdminService adminService = ServiceFactory.createAdminService(this.getDbOp());
        IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
     	boolean result = true;
		try{// 清理旧的订单商品信息
			String condition = "order_id=" + order.getId();
			ArrayList productHistoryList = this.getUserOrderProductHistoryList(condition, -1, -1, null);
			ArrayList productSplitHistoryList = this.getUserOrderProductSplitHistoryList(condition, -1, -1, null);
//			this.deleteUserOrderProductHistory(deleteCondition);
//			this.deleteUserOrderProductSplitHistory(deleteCondition);
    		List productList = adminService.getOrderProducts(order.getId());
			Iterator productIter = productList.listIterator();
			float productDprice = 0;
			float djqPrice = 0;
			int productCount = 0;
			// 先计算一个代金券的价格
			while(productIter.hasNext()){
				voOrderProduct vop = (voOrderProduct)productIter.next();
				if(vop.getDiscountPrice() < 0){
					djqPrice += Math.abs(vop.getDiscountPrice()*vop.getCount());
				}else{
					if(vop.getDiscountPrice()!=0&&order.getDprice()==0&&order.getDiscount()!=0&&order.getCode().startsWith("T")){
						productDprice += 0 ;
					}else{
						productDprice += vop.getDiscountPrice()*vop.getCount();
					}
					productCount+=vop.getCount();
				}
			}
			//订单优惠的价格
			if(productDprice!=0){
				Map map = orderDiscountHistory(order.getId());
				djqPrice += Float.parseFloat(map.isEmpty()?"0":map.get("subtract_price").toString());
			}
			// 记录订单中的商品信息	// 商品信息可能是套装
			productIter = productList.listIterator();
			while(productIter.hasNext()){
				voOrderProduct vop = (voOrderProduct)productIter.next();
				voProduct product = adminService.getProduct(vop.getProductId());
				if(product.getPrice() < 0){
					continue;
				}
		    	// 把记录记录到history表中。
				UserOrderProductHistoryBean uoph = logUserOrderProduct(productHistoryList,order, type, adminService, ppService,
						productDprice, djqPrice, vop, product,productCount);
				
				
                // 把记录分拆到splithistory表中（套装拆分） 
	    		logUserOrderProductSplit(productSplitHistoryList, order, type, adminService, ppService,
						productDprice, djqPrice, vop, product, uoph,productCount);
			}
			
			if(productHistoryList!=null&&productHistoryList.size()>0){
				for(int i=0;i<productHistoryList.size();i++){
					UserOrderProductHistoryBean ppb = (UserOrderProductHistoryBean)productHistoryList.get( i );
					// 删除多余数据。
					this.deleteUserOrderProductHistory(" id="+ppb.getId());
				}
			}
			if(productSplitHistoryList!=null&&productSplitHistoryList.size()>0){
				for(int i=0;i<productSplitHistoryList.size();i++){
					UserOrderProductSplitHistoryBean ppb = (UserOrderProductSplitHistoryBean)productSplitHistoryList.get( i );
					// 删除多余数据。
					this.deleteUserOrderProductSplitHistory(" id="+ppb.getId());
				}
			}
		     // 赠品处理
			logUserOrderPresent(order);
		} finally {
		}
		return result;
	}
	//end

	private UserOrderProductHistoryBean logUserOrderProduct(List list,voOrder order, int type,
			IAdminService adminService, IProductPackageService ppService,
			float productDprice, float djqPrice, voOrderProduct vop,
			voProduct product,int productCount) {
		UserOrderProductHistoryBean uoph = new UserOrderProductHistoryBean();
		uoph.setOrderId(order.getId());
		uoph.setProductId(product.getId());
		uoph.setCount(vop.getCount());
		if(product.getIsPackage() == 1){	    			// 套装商品，临时价格
			uoph.setTypeMask(UserOrderProductHistoryBean.TYPE_PACKAGE);
			List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
			Iterator ppIter = ppList.listIterator();
			float totalPackageProductPrice = 0;
			while(ppIter.hasNext()){
				ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
				voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
				totalPackageProductPrice += tempProduct.getPrice() * ppBean.getProductCount();
			}
			uoph.setPriceTemp(totalPackageProductPrice);
		} else {
			uoph.setTypeMask(UserOrderProductHistoryBean.TYPE_NORMAL);
			uoph.setPriceTemp(product.getPrice());
		}
		// 设置类型
		if(((type & (1 << 1)) != 0) && ((type & (1 << 2)) != 0 )){
		   		uoph.setTypeMask(UserOrderProductHistoryBean.TYPE_FREQUENT);
		} else if(type == UserOrderProductHistoryBean.TYPE_GROUPBUY){
				uoph.setTypeMask(UserOrderProductHistoryBean.TYPE_GROUPBUY);
		}  else if(type == UserOrderProductHistoryBean.TYPE_FREQUENT){
				uoph.setTypeMask(UserOrderProductHistoryBean.TYPE_FREQUENT);
		} 
		// 设置mmb价格和成交价格
			uoph.setPrice(product.getPrice());
		// 判断订单中各商品是否最后成交价为0元。
			boolean dpriceInPost = false;
			if(order.getDprice()-order.getPostage()<=0.1f && order.getPostage() > 0){
				//订单价格只有运费
				dpriceInPost = true;
			}
		    if(dpriceInPost){//如果订单金额只有运费，则将运费平均摊到商品上即可。
		    	float postCount = order.getPostage()/productCount;
		    	uoph.setDprice(postCount);
		    }else if(productDprice>0){
				float baseDisCount = vop.getDiscountPrice()*vop.getCount()/productDprice;//单品根据实际成交价及实际成交总价（不含折扣运费）占比
				float djqDisCount =  baseDisCount *djqPrice*order.getDiscount()/vop.getCount();//代金券及订单减折活动占比值=占比×总减值×订单折扣/商品个数
				float postCount = baseDisCount*order.getPostage()/vop.getCount();//运费均摊=占比×运费/商品个数
				float productDisPrice = vop.getDiscountPrice()*vop.getCount()*order.getDiscount()/vop.getCount();//单品的价格=应该是 总的实际成交价*折扣/数量
				float dprice = productDisPrice - djqDisCount +postCount;
				if(dprice-0.01<0){dprice=0;}
				uoph.setDprice( dprice );
			}else{// 如果商品总成交价为0 
				uoph.setDprice(0);
			}
		uoph.setPrice5(product.getPrice5());
		
		UserOrderProductHistoryBean pr = null;
		if(list!=null&&list.size()>0){
			pr = getUserOrderProductHistoryBean(list, product.getId());
		}
		if(pr!=null){
			if(uoph.getCount()!=pr.getCount()||uoph.getDprice()!=pr.getDprice()){
				this.updateUserOrderProductHistory(" count="+uoph.getCount()+" , dprice="+uoph.getDprice(), " id="+pr.getId());
			}
		}else{
			this.addUserOrderProductHistory(uoph);
		}
		return uoph;
	}

	private void logUserOrderProductSplit(List list,voOrder order, int type,
			IAdminService adminService, IProductPackageService ppService,
			float productDprice, float djqPrice, voOrderProduct vop,
			voProduct product, UserOrderProductHistoryBean uoph,int productCount) {
		// 判断订单中各商品是否最后成交价为0元。
		boolean dpriceInPost = false;
		if(order.getDprice()-order.getPostage()<=0.1f && order.getPostage() > 0){
						//订单价格只有运费
		         dpriceInPost = true;
		}
		// 记录订单中的商品信息（拆分套装，到split表）
		if(product.getIsPackage() == 1){
			List ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
			Iterator ppIter = ppList.listIterator();
			
			float totalPackagePrice = 0;
			int packageProductCount = 0;
			while(ppIter.hasNext()){
				ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
				voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
				totalPackagePrice += tempProduct.getPrice()*ppBean.getProductCount();	
				packageProductCount += vop.getCount() *ppBean.getProductCount();
			}
			ppIter = ppList.listIterator();
			while(ppIter.hasNext()){
				ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
				voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
				UserOrderProductSplitHistoryBean uopsh = new UserOrderProductSplitHistoryBean();
				uopsh.setCount(vop.getCount() * ppBean.getProductCount());
				uopsh.setOrderId(order.getId());
				uopsh.setProductId(tempProduct.getId());
				uopsh.setTypeMask(UserOrderProductSplitHistoryBean.TYPE_PACKAGE);
				uopsh.setProductParentId1(product.getId());

				if(((type & (1 << 1)) != 0) && ((type & (1 << 2)) != 0 )){
		            uopsh.setTypeMask(UserOrderProductHistoryBean.TYPE_FREQUENT);
				} else if(type == UserOrderProductHistoryBean.TYPE_GROUPBUY){
					uopsh.setTypeMask(UserOrderProductHistoryBean.TYPE_GROUPBUY);
				} else if(type == UserOrderProductHistoryBean.TYPE_FREQUENT) {
				
		    		uopsh.setTypeMask(UserOrderProductHistoryBean.TYPE_FREQUENT);
				} 
				    uopsh.setPrice(tempProduct.getPrice());
				    if(dpriceInPost){//如果订单商品只有运费，则需要将打包商品计算好的均摊价格再按比例摊到商品上
				    	float postCount = uoph.getDprice()/packageProductCount;
				    	uopsh.setDprice(postCount);				    	
				    }else if(uoph.getDprice()>0&&totalPackagePrice>0){
				    	float dprice = ((float)(tempProduct.getPrice()*ppBean.getProductCount())/totalPackagePrice)*uoph.getDprice()/ppBean.getProductCount();
				    	if(dprice-0.01<0){dprice=0;}
				    	uopsh.setDprice(dprice);
		    		}else if(uoph.getDprice()>0&&totalPackagePrice<=0.001f){
		    			uopsh.setDprice(uoph.getDprice()/packageProductCount);
		    		}else{
		    			uopsh.setDprice(0);
		    		}
				  
				uopsh.setPrice5(tempProduct.getPrice5());
				UserOrderProductSplitHistoryBean pr2 = null;
				if(list!=null&&list.size()>0){
					pr2 = getUserOrderProductSplitHistoryBean(list, product.getId());
				}
				if(pr2!=null){
					if(uopsh.getCount()!=pr2.getCount()||uopsh.getDprice()!=pr2.getDprice()){
						this.updateUserOrderProductSplitHistory(" count="+uopsh.getCount()+",dprice="+uopsh.getDprice(), " id="+pr2.getId());
					}
				}else{
					this.addUserOrderProductSplitHistory(uopsh);
				}
			}
		} else {
			UserOrderProductSplitHistoryBean uopsh = new UserOrderProductSplitHistoryBean();
			uopsh.setCount(vop.getCount());
			uopsh.setOrderId(order.getId());
			uopsh.setProductId(product.getId());
			uopsh.setTypeMask(UserOrderProductSplitHistoryBean.TYPE_NORMAL);

			if(((type & (1 << 1)) != 0) && ((type & (1 << 2)) != 0 )){
					uopsh.setTypeMask(UserOrderProductHistoryBean.TYPE_FREQUENT);
			} else if(type == UserOrderProductHistoryBean.TYPE_GROUPBUY){
					uopsh.setTypeMask(UserOrderProductHistoryBean.TYPE_GROUPBUY);
			} else if(type == UserOrderProductHistoryBean.TYPE_FREQUENT ) {
				uopsh.setTypeMask(UserOrderProductHistoryBean.TYPE_FREQUENT);
			} 
			// 设置mmb价格和成交价格
			uopsh.setPrice(product.getPrice());
			//如果订单金额只有运费，则将运费平均摊到商品上即可
		 	if(uoph.getDprice()>0&&(dpriceInPost||productDprice>0)){
				float dprice =uoph.getDprice();
		    	if(dprice-0.01<0){dprice=0;}
				uopsh.setDprice( dprice );
			}else{// 如果商品总成交价为0 则单品成交价为0
				uopsh.setDprice(0);
			}
			uopsh.setPrice5(product.getPrice5());
			UserOrderProductSplitHistoryBean pr2 = null;
			if(list!=null&&list.size()>0){
				pr2 = getUserOrderProductSplitHistoryBean(list, product.getId());
			}
			if(pr2!=null){
				if(uopsh.getCount()!=pr2.getCount()||uopsh.getDprice()!=pr2.getDprice()){
					this.updateUserOrderProductSplitHistory(" product_parent_id1 = 0, count="+uopsh.getCount()+",dprice="+uopsh.getDprice(), " id="+pr2.getId());
				}
			}else{
				this.addUserOrderProductSplitHistory(uopsh);
			}
		}
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
	/**
	 * 
	 * 说明:设置订单预约处理时间（预测外呼 中 发货失败 延迟发货 缺货已补货 可以设置该时间）
	 * @param dbOpSlave
	 * @param pretreatTime 预约处理时间
	 * @param vo 订单
	 * @return void
	 */
	public void setOrderPretreatTime(DbOperation dbOpSlave,String pretreatTime,voOrder vo){
		ResultSet rs =  dbOpSlave.executeQuery(" select order_id,pretreat_time from user_order_pretreat_time where order_id = "+vo.getId());
		String originalPretreatTime = null;
		try {
			while(rs!=null&&rs.next()){
				originalPretreatTime = rs.getString("pretreat_time");
			}
			rs.close();
		} catch (SQLException e) {
		}
		if(!pretreatTime.equals("")){
			if(originalPretreatTime != null){
				if(!pretreatTime.equals(originalPretreatTime.substring(0, 16))){//预约处理时间改变了
					//更新
					getDbOp().executeUpdate("update user_order_pretreat_time set pretreat_time = '"+pretreatTime+"' where order_id = "+vo.getId());
				}
			}else{//新增
				getDbOp().executeUpdate("insert into  user_order_pretreat_time values("+vo.getId()+",'"+pretreatTime+"')");
			}
			vo.setPretreatTime(pretreatTime);
		}else{
			if(originalPretreatTime != null && originalPretreatTime.trim().length()>0){
				vo.setPretreatTime(originalPretreatTime);
			}
		}
	}
	public void deleteOrderPretreatTime(DbOperation dbOp,int orderId){
		dbOp.executeUpdate(" delete from user_order_pretreat_time where order_id = " + orderId);
	}
	
	// 2012-12-14 yl 用于财务成交价表结构更改添加方法。 
	public static UserOrderProductHistoryBean getUserOrderProductHistoryBean(List buyProductList,int productId){
		if(buyProductList==null||buyProductList.size()<=0){
			return null;
		}
		for(int i=0;i<buyProductList.size();i++){
			UserOrderProductHistoryBean ppb = (UserOrderProductHistoryBean)buyProductList.get( i );
			// 判断赠品关系表中，是否有需要的数据。
			if(ppb.getProductId()==productId){
				// 已经修改的数据，去掉，无需删除。 剩余的将被删除。
				buyProductList.remove(ppb); 
				return ppb;
			}
		}
		return null;
	}
	public static UserOrderProductSplitHistoryBean getUserOrderProductSplitHistoryBean(List buyProductList,int productId){
		if(buyProductList==null||buyProductList.size()<=0){
			return null;
		}
		for(int i=0;i<buyProductList.size();i++){
			UserOrderProductSplitHistoryBean ppb = (UserOrderProductSplitHistoryBean)buyProductList.get( i );
			// 判断赠品关系表中，是否有需要的数据。
			if(ppb.getProductId()==productId){
				// 已经修改的数据，去掉，无需删除。 剩余的将被删除。
				buyProductList.remove(ppb); 
				return ppb;
			}
		}
		return null;
	}
	
	 public static Map orderDiscountHistory(int orderId){
		 Map  map = new HashMap();
		 DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		 db.prepareStatement("select id,order_id,price,dprice,subtract_price from user_order_discount_history where order_id=?");
		 PreparedStatement ps = db.getPStmt();
		 try {
			ps.setInt(1, orderId);
			ResultSet rs = ps.executeQuery();
			List list = resultToMap(rs, ps);
			if(null!=list&&!list.isEmpty()){
				map = (Map) list.get(0);
			}
		 } catch (SQLException e) {
			e.printStackTrace();
		 }finally{
			 db.release();
		 }
		 return map;
	 }
	 
	 public static List<Map<String,Object>> resultToMap(ResultSet rs,PreparedStatement ps) throws SQLException{
			ResultSetMetaData rsmd = ps.getMetaData();
			// 取得结果集列数
			int columnCount = rsmd.getColumnCount();
			List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
			while(rs.next()){
				Map<String,Object> map = new HashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					map.put(rsmd.getColumnLabel(i),rs.getObject(rsmd.getColumnLabel(i)));
				}
				datas.add(map);
			}
			return datas;
	}
}

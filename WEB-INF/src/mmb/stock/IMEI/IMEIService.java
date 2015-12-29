package mmb.stock.IMEI;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.ware.WareService;
import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.buy.BuyAdminHistoryBean;
import adultadmin.bean.buy.BuyStockProductBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.buy.BuyStockinProductBean;

import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class IMEIService  extends BaseServiceImpl {
	
	public IMEIService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

//	public IMEIService() {
//		this.useConnType = CONN_IN_SERVICE;
//	}
	/**
	 * @return 批量验证imei码
	 * @author syuf
	 */
	public String batchCheckIMEI(String code) {
		try {
			IMEIBean imei= this.getIMEI("code='" + StringUtil.toSql(code) + "'");
			//如果是imei码 并且状态为可出库则返回商品ID 否则提示
			if(imei != null){
				if(imei.getStatus() == IMEIBean.IMEISTATUS2){
					return "success_" + imei.getProductId() + "_" + imei.getId();
				}else{
					return "imei码[" + code + "]不允许出库!";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "程序异常";
		}
		return "imei码[" + code + "]不正确,请仔细核查!"; 
	}
	/**
	 * @return 根据商品ID验证商品是否带有emei码
	 * @author syuf
	 */
	public boolean selectImeiByProductId(int productId) {
		try {
			IMEIBean imei= this.getIMEI("product_id=" + productId);
			if(imei != null){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false; 
	}
	/**
	 * @return 验证传入的code是否是imei码, 如果不是,那么就认为这个code是商品条码或者编码 ,在判断这个商品有没有imei码
	 * @author syuf
	 */
	public String checkIMEI(String code) {
		WareService service = new WareService(this.dbOp);
		IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, this.dbOp);
		try {
			IMEIBean imei= this.getIMEI("code='" + StringUtil.toSql(code) + "'");
			//如果是imei码 并且状态为可出库则返回商品ID 否则提示
			if(imei != null){
				if(imei.getStatus() == IMEIBean.IMEISTATUS2){
					return "success_" + imei.getProductId() + "_" + imei.getId();
				}else{
					return "IMEI码不允许出库!";
				}
			}
			voProduct product = service.getProduct(StringUtil.toSql(code));
			if(product != null){
				imei= this.getIMEI("product_id=" + product.getId());
				if(imei != null){
					return "此商品属于有IMEI码商品,请扫IMEI码复核!";
				} else {
					return "product_" + product.getId() + ""; //若没有imei码则直接返回商品ID
				}
			}
			ProductBarcodeVO barcode = bcmService.getProductBarcode("barcode='" + StringUtil.toSql(code) + "' and barcode_status=0");
			if(barcode != null){
				imei= this.getIMEI("product_id=" + barcode.getProductId());
				if(imei != null){
					return "此商品属于有IMEI码商品,请扫IMEI码复核!";
				} else {
					return "product_" + barcode.getProductId() + ""; //若没有imei码则直接返回商品ID
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "程序异常";
		}
		return "请输入正确的商品条码、编号或IMEI码!"; 
	}
	public boolean addIMEIUserOrder(IMEIUserOrderBean bean){
		return addXXX(bean, "imei_user_order");
	}
	public boolean delIMEIUserOrder(String condition){
		return deleteXXX(condition, "imei_user_order");
	}
	public boolean updateIMEIUserOrder(String set,String condition){
		return updateXXX(set, condition, "imei_user_order");
	}
	public IMEIUserOrderBean getIMEIUserOrder(String condition){
		return (IMEIUserOrderBean) getXXX(condition, "imei_user_order", "mmb.stock.IMEI.IMEIUserOrderBean");
	}
	@SuppressWarnings("unchecked")
	public List<IMEIUserOrderBean> getIMEIUserOrderList(String condition,int index,int count ,String orderBy){
		return getXXXList(condition, index, count, orderBy, "imei_user_order", "mmb.stock.IMEI.IMEIUserOrderBean");
	}
	/**
	 * 添加IMEI
	 * @return
	 */
	public boolean addIMEI(IMEIBean bean) {
		return addXXX(bean, "imei");
	}

	public List getIMEIList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "imei", "mmb.stock.IMEI.IMEIBean");
	}
	
	public int getIMEICount(String condition) {
		return getXXXCount(condition, "imei", "id");
	}

	public IMEIBean getIMEI(String condition) {
		return (IMEIBean) getXXX(condition, "imei",
		"mmb.stock.IMEI.IMEIBean");
	}

	public boolean updateIMEI(String set, String condition) {
		return updateXXX(set, condition, "imei");
	}

	public boolean deleteIMEI(String condition) {
		return deleteXXX(condition, "imei");
	}
	
	/**
	 * 添加IMEILog
	 * @return
	 */
	public boolean addIMEILog(IMEILogBean bean) {
		return addXXX(bean, "imei_log");
	}

	public List getIMEILogList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "imei_log", "mmb.stock.IMEI.IMEILogBean");
	}
	
	public int getIMEILogCount(String condition) {
		return getXXXCount(condition, "imei_log", "id");
	}

	public IMEILogBean getIMEILog(String condition) {
		return (IMEILogBean) getXXX(condition, "imei_log",
		"mmb.stock.IMEI.IMEILogBean");
	}

	public boolean updateIMEILog(String set, String condition) {
		return updateXXX(set, condition, "imei_log");
	}

	public boolean deleteIMEILog(String condition) {
		return deleteXXX(condition, "imei_log");
	}
	
	/**
	 * 添加IMEIBuyStockin
	 * @return
	 */
	public boolean addIMEIBuyStockin(IMEIBuyStockinBean bean) {
		return addXXX(bean, "imei_buy_stockin");
	}
	
	public List getIMEIBuyStockinList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "imei_buy_stockin", "mmb.stock.IMEI.IMEIBuyStockinBean");
	}
	
	public int getIMEIBuyStockinCount(String condition) {
		return getXXXCount(condition, "imei_buy_stockin", "id");
	}
	
	public IMEIBuyStockinBean getIMEIBuyStockin(String condition) {
		return (IMEIBuyStockinBean) getXXX(condition, "imei_buy_stockin",
		"mmb.stock.IMEI.IMEIBuyStockinBean");
	}
	
	public boolean updateIMEIBuyStockin(String set, String condition) {
		return updateXXX(set, condition, "imei_buy_stockin");
	}
	
	public boolean deleteIMEIBuyStockin(String condition) {
		return deleteXXX(condition, "imei_buy_stockin");
	}
	/**
	 * hepeng
	 * 获取imei订单互查的总条数
	 * 
	 */
	public int getImeiOrCodeCount(String joinType, String condition){
		DbOperation dbop = this.getDbOp();
		String countCell="iu.imei_code";
		if("left".equals(joinType)){
			countCell="iu.imei_code";
		}else{
			countCell="os.code";
		}
		String query = "select count( "+countCell+") from imei_user_order iu "+joinType+" join user_order  os on( os.id=iu.order_id ) where 1=1  ";  
		int count = 0;
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs.next()) {
				count = rs.getInt(1);
			}
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.release(dbop);
		}
		return count;
	}
	/**
	 * hepeng
	 * 获取imei订单互查的list
	 * 
	 */
	public List<OrderStockBean> getImeiOrCodeList(String joinType,String condition, int index, int count, String orderBy){
	DbOperation dbop = this.getDbOp();
		
		String query = "select iu.imei_code,os.code from imei_user_order iu "+joinType+" join user_order  os on( os.id=iu.order_id ) where 1=1  "; 
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
//		try {
//			System.out.println(dbop.getConn().getMetaData().getURL());
//		} catch (SQLException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		if (orderBy != null) {
			query += " order by " + orderBy;
		}
		query = DbOperation.getPagingQuery(query, index, count);
		List<OrderStockBean> list = new ArrayList<OrderStockBean>();
		OrderStockBean order = null; //临时存放数据
		try {
			
			ResultSet rs = dbop.executeQuery(query); 
			while (rs.next()) {
				order = new OrderStockBean();
				order.setCode(rs.getString("imei_code"));//存放imei码
				order.setOrderCode(rs.getString("code"));//存放订单
                list.add(order);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.release(dbop);
		}
		return list;
	}
	
	/**
	 *	根据采购入库单号 确定是不是 买卖宝手机 采购入库单
	 */
	public String isBuyStockinMMBMobile (BuyStockinBean bsBean) {
		String result = "NO";
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		WareService  wareService = new WareService(this.dbOp);
		if( bsBean != null ) {
			List<BuyStockinProductBean> list = service.getBuyStockinProductList("buy_stockin_id=" + bsBean.getId(), -1, -1, null);
			if(list == null || list.size() == 0  ) {
				result = "NO";
			} else if( list.size() > 1) {
				result = "NO";
			} else {
				BuyStockinProductBean bspBean = list.get(0);
				voProduct product = wareService.getProduct(bspBean.getProductId());
				result = this.isProductMMBMobile(product);
			}
		}
		return result;
	}
	
	/**
	 * 根据商品voProduct 判断商品是不是买卖宝手机
	 * @return
	 */
	public String isProductMMBMobile(voProduct product) {
		String result = "NO";
		if( product == null ) {
			result = "未找到商品信息！";
		} else {
			//根据商品信息确定是不是 
			//boolean isMMB = this.isProductMMBMobile(product.getParentId1(), product.getParentId2());
			boolean isMMB = this.isProductMMBMobile(product.getId());
			if( isMMB ) {
				result = "YES";
			}
		}
		return result;
	}
	
	/**
	 * 新的方式 productId  来查询对应的表 来判断商品是否需要imei码
	 * @param productId
	 * @return
	 */
	public boolean isProductMMBMobile(int productId) {
		String sql = "select id from imei_product where product_id="+productId;
		boolean result = false;
		ResultSet rs = null;
		DbOperation dbOp = this.getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		rs = dbOp.executeQuery(sql);
		try {
			if (rs.next()) {
				result = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	/**
	 *  根据parentId 确定是否是买卖宝手机
	 * @param parentId1
	 * @param parentId2
	 * @return Haoyabin
	 */
	/*public boolean isProductMMBMobile ( int parentId1, int parentId2) {
		boolean result = false;
		if( parentId1 == 111 && parentId2 == 105 ) {
			result = true;
		}
		return result;
	}*/
	
	public boolean addIMEIStockExchange(IMEIStockExchangeBean bean) {
		return addXXX(bean, "imei_stock_exchange");
	}

	public List getIMEIStockExchangeList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "imei_stock_exchange", "mmb.stock.IMEI.IMEIStockExchangeBean");
	}
	
	public int getIMEIStockExchangeCount(String condition) {
		return getXXXCount(condition, "imei_stock_exchange", "id");
	}

	public IMEIStockExchangeBean getIMEIStockExchange(String condition) {
		return (IMEIStockExchangeBean) getXXX(condition, "imei_stock_exchange",
		"mmb.stock.IMEI.IMEIStockExchangeBean");
	}

	public boolean updateIMEIStockExchange(String set, String condition) {
		return updateXXX(set, condition, "imei_stock_exchange");
	}

	public boolean deleteIMEIStockExchange(String condition) {
		return deleteXXX(condition, "imei_stock_exchange");
	}
	
	/**
	 * 采购入库单添加IMEI号关联方法
	 * @param iMEI
	 * @param productId
	 * @param buyStockinId
	 * @return
	 */
	public String addBuyStockinIMEIInfo(String iMEI, int productId,
			int buyStockinId, voUser user) {
		String result = "SUCCESS";
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		WareService wareService = new WareService(this.dbOp);
		voProduct product = wareService.getProduct(productId);
		if( product == null ) {
			return "商品不存在！";
		}
		IMEIBean temp = this.getIMEI("code='" + iMEI + "'");
		if( temp != null ) {
			return "这个IMEI号已经存在数据库中，不能再添加！";
		}
		//添加之前要看 库里是否已经有了对应的code
		//首先添加IMEI号条目
		//添加IMEI号采购入库单关联条目
		//添加
		IMEIBean iMEIBean = new IMEIBean();
		iMEIBean.setCode(iMEI);
		iMEIBean.setCreateDatetime(DateUtil.getNow());
		iMEIBean.setProductId(productId);
		iMEIBean.setStatus(IMEIBean.IMEISTATUS1);
		if(!this.addIMEI(iMEIBean) ) {
			return "添加IMEI号失败！";
		}
		IMEIBuyStockinBean ibsBean = new IMEIBuyStockinBean();
		ibsBean.setBuyStockinId(buyStockinId);
		ibsBean.setIMEI(iMEI);
		ibsBean.setProductId(productId);
		if( !this.addIMEIBuyStockin(ibsBean) ) {
			return "添加IMEI号，入库单关联失败！";
		}
		BuyStockinBean bean = service.getBuyStockin("id = " + buyStockinId);
		if (bean == null) {
			return "没有找到采购入库单！";
		}
		if( bean.getStatus() == BuyStockinBean.STATUS4 || bean.getStatus() == BuyStockinBean.STATUS6 || bean.getStatus() == BuyStockinBean.STATUS7 || bean.getStatus() == BuyStockinBean.STATUS8 ) {
			return "入库单不是可录入IMEI码的状态！";
		}
		IMEILogBean ilBean = new IMEILogBean();
		ilBean.setOperCode(bean.getCode());
		ilBean.setCreateDatetime(DateUtil.getNow());
		ilBean.setIMEI(ibsBean.getIMEI());
		ilBean.setOperType(IMEILogBean.OPERTYPE1);
		ilBean.setUserId(user.getId());
		ilBean.setUserName(user.getUsername());
		ilBean.setContent("采购入库单,添加IMEI码：" + iMEIBean.getCode()+",地区："+ProductStockBean.areaMap.get(bean.getStockArea()));
		if( !this.addIMEILog(ilBean) ) {
			return "添加操作日志失败！";
		}
		BuyStockinProductBean bspBean = service.getBuyStockinProduct("buy_stockin_id="+buyStockinId +" and product_id=" + productId);
		if (bspBean == null) {
			return "没有找到采购入库单商品！";
		}
		BuyStockProductBean bsp = service.getBuyStockProduct("buy_stock_id = " + bean.getBuyStockId() + " and product_id = " + product.getId());
		if (bsp == null) {
			return "没有找到预计到货单商品！";
		}
		if(!psService.checkBuyStockinProductCount(service.getDbOp(), buyStockinId, bspBean.getStockInCount() + 1, bsp.getBuyCount(), product.getId(), bean.getBuyStockId())) {
			return "采购入库单商品过多！";
		}
		if( !service.updateBuyStockinProduct("stockin_count = stockin_count + 1", "buy_stockin_id="+buyStockinId +" and product_id=" + productId)) {
			return "更新入库数量失败！";
		}

		BuyAdminHistoryBean log = new BuyAdminHistoryBean();
		log.setAdminId(user.getId());
		log.setAdminName(user.getUsername());
		log.setLogId(buyStockinId);
		log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
		log.setOperDatetime(DateUtil.getNow());
		log.setRemark("修改了采购入库单商品["+product.getCode()+"]的入库量("+bspBean.getStockInCount()+"-"+1+") 添加IMEI码：" + iMEI);
		log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
		if( !service.addBuyAdminHistory(log) ) {
			return "添加操作日志失败！";
		}
		return result;
	}
	
	/**
	 * 删除IMEI号和关联关系的方法，
	 * @param codes
	 * @param productId
	 * @param buyStockinId
	 * @param user
	 * @return
	 */
	public String deleteCodes(String[] codes, int productId, int buyStockinId,voUser user) {
		String result = "SUCCESS";
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		WareService wareService = new WareService(this.dbOp);
		voProduct product = wareService.getProduct(productId);
		if( product == null ) {
			return "商品不存在！";
		}
		if( codes == null || codes.length == 0 ) {
			return "没有要删除的对象";
		}
		for( int i = 0 ; i < codes.length ; i ++ ) {
			String code = codes[i];
			if( !this.deleteIMEI("code='" + code + "'" ) ) {
				return "删除失败！";
			}
			
			if( ! this.deleteIMEIBuyStockin("IMEI='" + code + "'") ) {
				return "删除失败！";
			}
			
			BuyStockinBean bean = service.getBuyStockin("id = " + buyStockinId);
			IMEILogBean ilBean = new IMEILogBean();
			ilBean.setOperCode(bean.getCode());
			ilBean.setCreateDatetime(DateUtil.getNow());
			ilBean.setIMEI(code);
			ilBean.setOperType(IMEILogBean.OPERTYPE1);
			ilBean.setUserId(user.getId());
			ilBean.setUserName(user.getUsername());
			ilBean.setContent("采购入库单,删除IMEI码：" + code+",地区："+ProductStockBean.areaMap.get(bean.getStockArea()));
			if( !this.addIMEILog(ilBean) ) {
				return "添加操作日志失败！";
			}
			
			BuyStockinProductBean bspBean = service.getBuyStockinProduct("buy_stockin_id="+buyStockinId +" and product_id=" + productId);
			if( !service.updateBuyStockinProduct("stockin_count = stockin_count - 1", "buy_stockin_id="+buyStockinId +" and product_id=" + productId)) {
				return "更新入库数量失败！";
			}
			BuyAdminHistoryBean log = new BuyAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(buyStockinId);
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("修改了采购入库单商品["+product.getCode()+"]的入库量("+bspBean.getStockInCount()+"-"+1+") 删除IMEI码：" + code);
			log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
			if( !service.addBuyAdminHistory(log) ) {
				return "添加操作日志失败！";
			}
		}
		return result;
	}

	/**
	 * 在审核不通过的时候解除 关联关系，还有删除IMEI码
	 * @param buyStockinId
	 * @return
	 */
	public String disposeRelationWithBuyStockin(int buyStockinId,voUser user) {
		String result = "SUCCESS";
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		//找出关联的单据
		List<IMEIBuyStockinBean> list = this.getIMEIBuyStockinList("buy_stockin_id="+buyStockinId, -1, -1, null);
		if( list == null || list.size() == 0 ) {
			return result;
		} else {
			int x = list.size();
			for(int i = 0 ; i < x; i++ ) {
				IMEIBuyStockinBean ibsBean = list.get(i);
				if( !this.deleteIMEI("code='" + ibsBean.getIMEI() + "'") ) {
					return "删除IMEI码失败！";
				}
				
				BuyStockinBean bean = service.getBuyStockin("id = " + buyStockinId);
				IMEILogBean ilBean = new IMEILogBean();
				ilBean.setOperCode(bean.getCode());
				ilBean.setCreateDatetime(DateUtil.getNow());
				ilBean.setIMEI(ibsBean.getIMEI());
				ilBean.setOperType(IMEILogBean.OPERTYPE1);
				ilBean.setUserId(user.getId());
				ilBean.setUserName(user.getUsername());
				ilBean.setContent("采购入库单,将IMEI码：" + ibsBean.getIMEI() + "取消与入库单关联"+",地区："+ProductStockBean.areaMap.get(bean.getStockArea()));
				if( !this.addIMEILog(ilBean) ) {
					return "添加操作日志失败！";
				}
				
			}
			if( !this.deleteIMEIBuyStockin("buy_stockin_id="+ buyStockinId) ) {
				return "删除IMEI码关联关系失败！";
			}
			//对应的IMEI码删除了  同时也要对数量清0
			List<BuyStockinProductBean> list2 = service.getBuyStockinProductList("buy_stockin_id="+buyStockinId , -1, -1, null);
			if( list2 == null || list2.size() == 0 ) {
				return "入库单数据异常！";
			} else {
				for(int i = 0 ; i < list2.size(); i ++ ) {
					BuyStockinProductBean bspBean = list2.get(i);
					if( !service.updateBuyStockinProduct("stockin_count=0", "id="+bspBean.getId()) ) {
						return "删除数量时，数据库操作失败！";
					}
				}
			}
		}
		return result;
	}

	/**
	 * 审核通过是入库时 把IMEI码改为可出库
	 * @param buyStockinId
	 * @param bspBean
	 * @param buyStockinCode
	 * @param user
	 * @return
	 */
	public String stockinIMEI(int buyStockinId, BuyStockinProductBean bspBean,String buyStockinCode, voUser user) {
		String result = "SUCCESS";
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
			BuyStockinBean bean = service.getBuyStockin("id = " + buyStockinId);
			List<IMEIBuyStockinBean> list = this.getIMEIBuyStockinList("buy_stockin_id="+buyStockinId +" and product_id="+bspBean.getProductId(), -1, -1, null);
			String area="";
			if(bean!=null){
				area = ",地区："+ProductStockBean.areaMap.get(bean.getStockArea());
			}
			if( list == null || list.size() == 0 ) {
				return "查找采购入库单和IMEI码关联关系时无结果！";
			} 
			if( bspBean.getStockInCount() != list.size() ) {
				return "IMEI码的数量与入库量不符，请检查！";
			} else {
				int x = list.size();
				for(int i = 0 ; i < x; i ++ ) {
					IMEIBuyStockinBean ibsBean = list.get(i);
					IMEIBean iMEIBean = this.getIMEI("code='"+ibsBean.getIMEI() + "' and status=" + IMEIBean.IMEISTATUS1);
					if( iMEIBean == null ) {
						return "IMEI码 关联关系有错误,未找到IMEI码！";
					}
					if( !this.updateIMEI("status="+IMEIBean.IMEISTATUS2,  "id=" + iMEIBean.getId() ) ) {
						return "更新IMEI码状态失败！";
					}
					IMEILogBean ilBean = new IMEILogBean();
					ilBean.setOperCode(buyStockinCode);
					ilBean.setCreateDatetime(DateUtil.getNow());
					ilBean.setIMEI(ibsBean.getIMEI());
					ilBean.setOperType(IMEILogBean.OPERTYPE1);
					ilBean.setUserId(user.getId());
					ilBean.setUserName(user.getUsername());
					ilBean.setContent("采购入库单,审核通过,将IMEI码：" + iMEIBean.getCode() + "状态由[入库中]改为[可出库]"+area);
					if( !this.addIMEILog(ilBean) ) {
						return "添加操作日志失败！";
					}
				}
			}
		return result;
	}

	public Map<String,String> getIMEIAndOrder(String condition) {
		DbOperation dbop = this.getDbOp();
		String query = "SELECT iuo.imei_code,iuo.id,iuo.order_id,vo.`code`" +
				"FROM user_order AS vo " +
				"INNER JOIN imei_user_order AS iuo ON iuo.order_id = vo.id WHERE "; 
		if (null != condition && !condition.equals("")) {
			query += condition;
		}
		Map<String,String> map = new HashMap<String, String>();
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs.next()) {
				map.put("oldImeiCode", rs.getString(1));
				map.put("imeiUserOrderId", rs.getInt(2) + "");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.release(dbop);
		}
		return map;
		
	}
	
	/**
	 * 判断imei是不是大q手机
	 * @param imei
	 * @return
	 */
	public boolean isDaqByIEMI(String imei){
		boolean result = false;
		DbOperation dbop = this.getDbOp();
		String query = "select count(i.id) from  imei_product ip join imei i on ip.product_id=i.product_id where 1=1 ";
		if(!"".equals(StringUtil.checkNull(imei))){
			query += " and i.code='" + imei + "'";
		}
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs!=null && rs.next()) {
				int count = rs.getInt(1);
				if(count == 1){
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.release(dbop);
		}
		return result;
	}
	
	/**
	 * 判断imei是不是大q手机
	 * @param imei
	 * @return
	 */
	public boolean isDaqByProductId(int productId){
		boolean result = false;
		DbOperation dbop = this.getDbOp();
		String query = "select count(id) from  imei_product where product_id = " + productId;
		try {
			ResultSet rs = dbop.executeQuery(query);
			if (rs!=null && rs.next()) {
				int count = rs.getInt(1);
				if(count == 1){
					result = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			this.release(dbop);
		}
		return result;
	}
}
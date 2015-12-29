package mmb.stock.fitting.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.BaseService;
import com.mmb.components.service.BuyStockinBaseData;
import com.mmb.components.service.FinanceBaseDataService;

import mmb.finance.service.InitVerificationService;
import mmb.finance.service.impl.InitVerificationServiceImpl;
import mmb.finance.stat.FinanceBuyPayBean;
import mmb.finance.stat.FinanceBuyProductBean;
import mmb.finance.stat.FinanceProductBean;
import mmb.finance.stat.FinanceReportFormsService;
import mmb.finance.stat.FinanceSellProductBean;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.stock.IMEI.IMEIService;
import mmb.stock.fitting.model.FittingBuyStockInBean;
import mmb.stock.fitting.model.FittingBuyStockInProduct;
import mmb.ware.WareService;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductSupplier;
import adultadmin.action.vo.voUser;
import adultadmin.bean.buy.BuyAdminHistoryBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.supplier.SupplierStandardInfoBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.impl.SupplierServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.ISupplierService;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.MyRuntimeException;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class FittingBuyStockinService  extends BaseServiceImpl  {

	public FittingBuyStockinService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public FittingBuyStockinService() {
		this.useConnType = CONN_IN_SERVICE;
	}

	public String constructAllSupplierJson() {
		String result = "[";
		ISupplierService supplierService = new SupplierServiceImpl(IBaseService.CONN_IN_SERVICE,this.getDbOp());
		List<SupplierStandardInfoBean> list = supplierService.getSupplierStandardInfoList("id<> 0 ", -1, -1, " name asc");
		if( list != null && list.size() != 0 ) {
			for( SupplierStandardInfoBean ssib : list ) {
				result +="{ 'id':'"+ssib.getId()+"',"+"'text':'"+ssib.getName() +"'},";
			}
		}
		result +="{'id':'0','text':''}]";
		return result;
	}
	
	public String constructAllBrandJson() {
		String result = "[";
			ResultSet rs = null;
			DbOperation dbOp = this.getDbOp();
			String sql = "select id, name from brand";
			rs = dbOp.executeQuery(sql);
			try {
				while (rs.next()) {
					result +="{ 'id':'"+rs.getInt("id")+"',"+"'text':'"+rs.getString("name") +"'},";
				}
				result +="{'id':'0','text':''}]";
			} catch (SQLException e) {
				e.printStackTrace();
			}  finally {
				release(dbOp);
			}
		return result;
	}

	public void checkFittingProductInfoToAdd(String productCode, int count,
			float price) {
		WareService wareService = new WareService(this.getDbOp());
		ISupplierService supplierService = new SupplierServiceImpl(IBaseService.CONN_IN_SERVICE,this.getDbOp());
		if( productCode.equals("") ) {
			throw new MyRuntimeException("商品编号不可以为空！");
		}
		if( count <= 0 ) {
			throw new MyRuntimeException("数量填写不正确！");
		}
		if( Float.compare(0f, price) >= 0 ) {
			throw new MyRuntimeException("价格填写不正确！");
		}
		/*if( supplierId <= 0 ) {
			throw new MyRuntimeException("供应商填写不正确！");
		}
		if( brandId <= 0 ) {
			throw new MyRuntimeException("品牌填写不正确！");
		}*/
		voProduct product = wareService.getProduct(productCode);
		if( product == null ) {
			throw new MyRuntimeException("根据商品编号没有找到商品信息！");
		}
		
		if( product.getParentId1() != 1536) {
			throw new MyRuntimeException("商品不是配件类型！");
		}
		
		/*SupplierStandardInfoBean ssiBean = supplierService.getSupplierStandardInfo("id="+supplierId);
		if( ssiBean == null ) {
			throw new MyRuntimeException("没有找到对应供应商的信息！");
		}
		
		ResultSet rs = null;
		DbOperation dbOp = this.getDbOp();
		String sql = "select count(id) from brand where id="+brandId;
		rs = dbOp.executeQuery(sql);
		try {
			if( !rs.next() ) {
				throw new MyRuntimeException("没有找到对应的品牌信息！");
			}
		} catch (SQLException e) {
		}  finally {
			release(dbOp);
		}*/
		
	}

	public List<BuyStockinProductBean> getInfoForAll(
			HttpServletRequest request, String[] productCodes) {
		List<BuyStockinProductBean> list = new ArrayList<BuyStockinProductBean>();
		WareService wareService = new WareService(this.getDbOp());
		for( int i = 0 ; i < productCodes.length; i++ ) {
			String productCode = productCodes[i];
			BuyStockinProductBean bspBean = new BuyStockinProductBean();
			bspBean.setProductCode(productCode);
			int count = StringUtil.parstInt(request.getParameter("count_"+productCode));
			float price = StringUtil.toFloat(request.getParameter("price_"+productCode));
			String proxyName = StringUtil.convertNull(request.getParameter("proxyName_"+productCode));
			int fittingType = StringUtil.toInt(request.getParameter("fittingType_" + productCode));
			//int supplierId = StringUtil.toInt(request.getParameter("supplierId_"+productCode));
			//int brandId = StringUtil.parstInt(request.getParameter("brandId_"+productCode));
			voProduct product = wareService.getProduct(productCode);
			if( product == null ) {
				throw new MyRuntimeException("根据商品编号没有找到商品信息！");
			}
			bspBean.setOriname(product.getOriname());
			bspBean.setPrice3(price);
			bspBean.setStockInCount(count);
			bspBean.setProxyName(proxyName);
			bspBean.setFittingType(fittingType);
			/*SupplierStandardInfoBean ssiBean = supplierService.getSupplierStandardInfo("id="+supplierId);
			if( ssiBean == null ) {
				throw new MyRuntimeException("没有找到对应供应商的信息！");
			}*/
			bspBean.setConfirmDatetime("<a href=\"javascript:deleteProduct('"+productCode+"');\">删除</a>");
			//bspBean.setProxyName(ssiBean.getName());
			/*ResultSet rs = null;
			DbOperation dbOp = this.getDbOp();
			String sql = "select name from brand where id="+brandId;
			rs = dbOp.executeQuery(sql);
			try {
				if( rs.next() ) {
					bspBean.setProductLineName(rs.getString("name"));
				}
			} catch (SQLException e) {
			}  finally {
				release(dbOp);
			}*/
			list.add(bspBean);
		}
		return list;
	}

	public void addFittingBuyStockin(
			HttpServletRequest request, String[] productCodes, int area, int supplierId,int stockInType, voUser user) {
		WareService wareService = new WareService(this.getDbOp());
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,this.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		wareService.getDbOp().startTransaction();
		String code = service.generateBuyStockinCodeBref();
		BuyStockinBean stockin = new BuyStockinBean();
		stockin.setBuyStockId(0);
		stockin.setBuyOrderId(0);
		stockin.setCreateDatetime(DateUtil.getNow());
		stockin.setConfirmDatetime(DateUtil.getNow());
		stockin.setStatus(BuyStockinBean.STATUS0);
		stockin.setCode(code);
		stockin.setStockArea(area);
		stockin.setStockType(ProductStockBean.STOCKTYPE_AFTER_SALE_FIITING);
		stockin.setRemark("");
		stockin.setCreateUserId(user.getId());
		stockin.setSupplierId(supplierId);//供应商id
		if (!service.addBuyStockin(stockin)) {
			wareService.getDbOp().rollbackTransaction();
			throw new MyRuntimeException("添加失败！");
		}
		int stockinId = service.getDbOp().getLastInsertId();
		if( !service.fixBuyStockinCode(stockin.getCode(), service.getDbOp(), stockin)) {
			wareService.getDbOp().rollbackTransaction();
			throw new MyRuntimeException("添加失败！");
		}
		code = stockin.getCode();
		
		//log记录
		BuyAdminHistoryBean log = new BuyAdminHistoryBean();
		log.setAdminId(user.getId());
		log.setAdminName(user.getUsername());
		log.setLogId(stockinId);
		log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
		log.setOperDatetime(DateUtil.getNow());
		log.setRemark("配件入库单生成："+code);
		log.setType(BuyAdminHistoryBean.TYPE_ADD);
		if( !service.addBuyAdminHistory(log) ) {
			request.setAttribute("tip", "添加失败！");
			request.setAttribute("result", "failure");
			service.getDbOp().rollbackTransaction();
			return;
		}
		
		int fittingTypeCache = 0;
		for( int i = 0 ; i < productCodes.length; i++ ) {
			String productCode = productCodes[i];
			BuyStockinProductBean bsip = new BuyStockinProductBean();
			int count = StringUtil.parstInt(request.getParameter("count_"+productCode));
			float price = StringUtil.toFloat(request.getParameter("price_"+productCode));
			int fittingType = StringUtil.StringToId(request.getParameter("fittingType_"+productCode));
			if(fittingTypeCache==0){
				fittingTypeCache = fittingType;
			}
			//int supplierId = StringUtil.toInt(request.getParameter("supplierId_"+productCode));
			//int brandId = StringUtil.parstInt(request.getParameter("brandId_"+productCode));
			voProduct product = wareService.getProduct(productCode);
			if( product == null ) {
				wareService.getDbOp().rollbackTransaction();
				throw new MyRuntimeException("根据商品编号没有找到商品信息！");
			}
			if( product.getParentId1() != 1536) {
				wareService.getDbOp().rollbackTransaction();
				throw new MyRuntimeException("商品不是配件类型！");
			}
			ProductStockBean ps = psService.getProductStock("product_id=" + product.getId() + " and type=" + stockin.getStockType() + " and area=" + stockin.getStockArea());
			if( ps == null ) {
				wareService.getDbOp().rollbackTransaction();
				throw new MyRuntimeException("目前该商品在对应库区售后配件库没有库存信息！");
			}
			bsip.setCreateDatetime(DateUtil.getNow());
			bsip.setConfirmDatetime(DateUtil.getNow());
			bsip.setBuyStockinId(stockinId);
			bsip.setStockInId(ps.getId());
			bsip.setProductCode(product.getCode());
			bsip.setProductId(product.getId());
			bsip.setRemark("");
			bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_UNDEAL);
			bsip.setPrice3(price);
			bsip.setProductProxyId(supplierId);
			bsip.setOriname(product.getOriname());
			bsip.setStockInCount(count);
			if (!service.addBuyStockinProduct(bsip)) {
				wareService.getDbOp().rollbackTransaction();
				throw new MyRuntimeException("添加失败！");
			}
			bsip.setId(wareService.getDbOp().getLastInsertId());
			//log记录
			log = new BuyAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(stockin.getId());
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("添加采购入库单商品["+product.getCode()+"]");
			log.setType(BuyAdminHistoryBean.TYPE_ADD);
			if( !service.addBuyAdminHistory(log) ) {
				wareService.getDbOp().rollbackTransaction();
				throw new MyRuntimeException("添加失败！");
			}
			//新增采购入库配件商品相关信息	
			FittingBuyStockInProduct fittingProduct = new FittingBuyStockInProduct();
			fittingProduct.setFittingType(fittingType);
			fittingProduct.setBuyStockinProductId(bsip.getId());
			if(!addFittingBuyStockInProduct(fittingProduct)){
				wareService.getDbOp().rollbackTransaction();
				throw new MyRuntimeException("添加失败！");
			}
		}
		
		//添加配件采购入库单相关信息
		FittingBuyStockInBean fbsib = new FittingBuyStockInBean();
		fbsib.setBuyStockinId(stockinId);
		fbsib.setType(stockInType);
		fbsib.setFittingType(fittingTypeCache);
		
		if(!addFittingBuyStockIn(fbsib)){
			wareService.getDbOp().rollbackTransaction();
			throw new MyRuntimeException("添加失败！");
		}
		wareService.getDbOp().commitTransaction();
	}

	public void editFittingBuyStockin(HttpServletRequest request,
			String[] productCodes, voUser user, int id) {
		WareService wareService = new WareService(this.getDbOp());
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,this.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		wareService.getDbOp().startTransaction();
		BuyStockinBean stockin = service.getBuyStockin("id="+id);
		if( !service.updateBuyStockin("status="+ BuyStockinBean.STATUS0, "id="+stockin.getId()) ) {
			wareService.getDbOp().rollbackTransaction();
			throw new RuntimeException("编辑失败！");
		}
		
		List<BuyStockinProductBean> bspList = service.getBuyStockinProductList("buy_stockin_id="+id, -1, -1, "id asc");
		for( BuyStockinProductBean bspBean : bspList ) {
			if( !service.deleteBuyStockinProduct("id="+bspBean.getId()) ) {
				wareService.getDbOp().rollbackTransaction();
				throw new RuntimeException("编辑失败！");
			}
			BuyAdminHistoryBean log = new BuyAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(stockin.getId());
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("删除采购入库商品[" + bspBean.getProductCode() + "]");
			log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
			if (!service.addBuyAdminHistory(log)) {
				wareService.getDbOp().rollbackTransaction();
				throw new MyRuntimeException("添加采购入库单更新日志失败！");
			}
		}
		for( int i = 0 ; i < productCodes.length; i++ ) {
			String productCode = productCodes[i];
			BuyStockinProductBean bsip = new BuyStockinProductBean();
			int count = StringUtil.parstInt(request.getParameter("count_"+productCode));
			float price = StringUtil.toFloat(request.getParameter("price_"+productCode));
			//int supplierId = StringUtil.toInt(request.getParameter("supplierId_"+productCode));
			//int brandId = StringUtil.parstInt(request.getParameter("brandId_"+productCode));
			voProduct product = wareService.getProduct(productCode);
			if( product == null ) {
				wareService.getDbOp().rollbackTransaction();
				throw new MyRuntimeException("根据商品编号没有找到商品信息！");
			}
			if( product.getParentId1() != 1536) {
				wareService.getDbOp().rollbackTransaction();
				throw new MyRuntimeException("商品不是配件类型！");
			}
			ProductStockBean ps = psService.getProductStock("product_id=" + product.getId() + " and type=" + stockin.getStockType() + " and area=" + stockin.getStockArea());
			if( ps == null ) {
				wareService.getDbOp().rollbackTransaction();
				throw new MyRuntimeException("目前该商品带对应库区售后配件库没有库存信息！");
			}
			bsip.setCreateDatetime(DateUtil.getNow());
			bsip.setConfirmDatetime(DateUtil.getNow());
			bsip.setBuyStockinId(id);
			bsip.setStockInId(ps.getId());
			bsip.setProductCode(product.getCode());
			bsip.setProductId(product.getId());
			bsip.setRemark("");
			bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_UNDEAL);
			bsip.setPrice3(price);
			bsip.setProductProxyId(stockin.getSupplierId());
			bsip.setOriname(product.getOriname());
			bsip.setStockInCount(count);
			if (!service.addBuyStockinProduct(bsip)) {
				wareService.getDbOp().rollbackTransaction();
				throw new MyRuntimeException("添加失败！");
			}
			
			//log记录
			BuyAdminHistoryBean log = new BuyAdminHistoryBean();
			log = new BuyAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(stockin.getId());
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("添加采购入库单商品["+product.getCode()+"]");
			log.setType(BuyAdminHistoryBean.TYPE_ADD);
			if( !service.addBuyAdminHistory(log) ) {
				wareService.getDbOp().rollbackTransaction();
				throw new MyRuntimeException("添加失败！");
			}
		}
		wareService.getDbOp().commitTransaction();
	}
	
	public void auditFittingBuyStockin (int buyStockinId, int mark, String remark, voUser user) {
		WareService wareService = new WareService(this.getDbOp());
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		ISupplierService supplierService = ServiceFactory.createSupplierService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		//处理财务数据类
		BaseService buyStockinService = new BuyStockinBaseData(wareService.getDbOp().getConn());
		
			BuyStockinBean bean = service.getBuyStockin("id = " + buyStockinId);
			if (bean == null) {
				throw new MyRuntimeException("没有这个采购入库单");
			}
			if ( bean.getStatus() != BuyStockinBean.STATUS3 ) {
				throw new MyRuntimeException("状态不是待审核，不可以审核！");
			}

			if (mark == 0) { // 审核未通过
				service.getDbOp().startTransaction();
				if(!service.updateBuyStockin("status = " + BuyStockinBean.STATUS5+", remark='"+remark+"'", "id = " + buyStockinId))
				{
				  service.getDbOp().rollbackTransaction();
				  throw new MyRuntimeException("数据库操作失败");
				}
				//log记录
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(buyStockinId);
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("审核未通过");
				log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
				if(!service.addBuyAdminHistory(log))
				{
				  service.getDbOp().rollbackTransaction();
				  throw new MyRuntimeException("数据库操作失败");
				}
				service.getDbOp().commitTransaction();
			} else {  //审核通过  
				int productId = -1;
				String condition = "buy_stockin_id = " + bean.getId() + " and status = " + BuyStockinProductBean.BUYSTOCKIN_UNDEAL;
				if (productId > 0) {
					condition += " and product_id = " + productId;
				}
				ArrayList shList = service.getBuyStockinProductList(condition, 0, -1, "id");
				Iterator itr = shList.iterator();
				if(shList.size() == 0){
					throw new MyRuntimeException("没有需要执行的数据");
				}
				SupplierStandardInfoBean supplierStandardInfo = supplierService.getSupplierStandardInfo("id = "+bean.getSupplierId());
				FittingBuyStockInBean fittingBuyStockInBean = getFittingBuyStockIn("buy_stockin_id=" + bean.getId());
//				double taxPoint = 0;
				//开始事务
				service.getDbOp().startTransaction();

				//审核采购入库单
				if(!service.updateBuyStockin("status = " + BuyStockinBean.STATUS4 + ", confirm_datetime = now(), auditing_user_id=" + user.getId() +", remark='"+remark+"'", "id = " + buyStockinId))
				{
				  service.getDbOp().rollbackTransaction();
				  throw new MyRuntimeException("数据库操作失败");
				}
				
				BuyAdminHistoryBean log = new BuyAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(bean.getId());
				log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("审核通过");
				log.setType(BuyAdminHistoryBean.TYPE_MODIFY);


				if(!service.addBuyAdminHistory(log))
				{
				  service.getDbOp().rollbackTransaction();
				  throw new MyRuntimeException("数据库操作失败");
				}
				BuyStockinProductBean sh = null;
				voProduct product = null;
				String set = null;
				ProductStockBean ps = null;
				int count = 0;
				while (itr.hasNext()) {
					count++;
					sh = (BuyStockinProductBean) itr.next();
//					sh.setPrice3(Double.valueOf(String.valueOf(Arith.mul(sh.getPrice3(),Arith.add(1,taxPoint)))).floatValue());
					
					if(sh.getStockInCount() <= 0){
						service.getDbOp().rollbackTransaction();
						throw new MyRuntimeException("采购入库量不能为0，操作失败！");
					}
					product = wareService.getProduct(sh.getProductId());
					if(product.getIsPackage()==1){
						service.getDbOp().rollbackTransaction();
						throw new MyRuntimeException("入库单中包含有套装产品，不能入库！");
					}
					product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
					ps = psService.getProductStock("id=" + sh.getStockInId());
					float price5 = 0;
					int totalCount = product.getStockAll() + product.getLockCountAll();
					price5 = ((float)Math.round((product.getPrice5() * (totalCount) + (sh.getPrice3() * sh.getStockInCount())) / (totalCount + sh.getStockInCount()) * 1000))/1000;

					set = "status = " + BuyStockinProductBean.BUYSTOCKIN_DEALED
					+ ", remark = '操作前库存" + ps.getStock()
					+ ",操作后库存" + (ps.getStock() + sh.getStockInCount())
					+ "', confirm_datetime = now()";
					if(!service.updateBuyStockinProduct(set, "id = " + sh.getId()))
					{
					  service.getDbOp().rollbackTransaction();
					  throw new MyRuntimeException("数据库操作失败");
					}
					if(!psService.updateProductStockCount(sh.getStockInId(), sh.getStockInCount())){
						service.getDbOp().rollbackTransaction();
						throw new MyRuntimeException("库存操作失败，可能是库存不足，请与管理员联系！");
					}
					
//					if(!service.getDbOp().executeUpdate("update product set price5=" + price5 + " where id = " + product.getId()))
//					{
//					  service.getDbOp().rollbackTransaction();
//					  throw new MyRuntimeException("没有这个采购入库单");
//					}
					try{
						Float averagePrice = buyStockinService.calAveragePrice(productId, sh.getStockInCount(), sh.getPrice3());
						buyStockinService.updateProductPrice(productId, averagePrice);
						buyStockinService.updateFinanceProductPrice(productId, 
								buyStockinService.calNoTaxAveragePrice(productId, sh.getStockInCount(), 
										Arith.div(sh.getPrice3(),1+Float.valueOf(supplierStandardInfo.getTaxPoint()+""))),
										averagePrice);
					}catch(Exception e){
						e.printStackTrace();
					}
					
					//更新货位库存2011-04-19
					CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+ps.getArea());
					CargoProductStockBean cps = null;
					StringBuilder conSb = new StringBuilder();
					conSb.append("ci.stock_type = ").append(ps.getType()).append(" and ci.area_id = ").append(inCargoArea.getId()).append(" and ci.store_type = ")
							.append(CargoInfoBean.STORE_TYPE2).append(" and cps.product_id = ").append(sh.getProductId());
					//根据不同的配件类别，放入不同类型的货位缓存区
					int cargoType = CargoInfoBean.TYPE3;
					if((byte)fittingBuyStockInBean.getFittingType()==FittingBuyStockInBean.FITTING_TYPE2){
						conSb.append(" and ci.type=").append(CargoInfoBean.TYPE4);
						cargoType = CargoInfoBean.TYPE4;
					}else if((byte)fittingBuyStockInBean.getFittingType()==FittingBuyStockInBean.FITTING_TYPE3){
						conSb.append(" and ci.type=").append(CargoInfoBean.TYPE5);
						cargoType = CargoInfoBean.TYPE5;
					}else{
						conSb.append(" and ci.type=").append(CargoInfoBean.TYPE3);
					}
					List cocList = cargoService.getCargoAndProductStockList(conSb.toString(), -1, -1, "ci.id desc");
					if(cocList == null || cocList.size() == 0){//产品首次入库，无暂存区绑定货位库存信息
						CargoInfoBean cargo = cargoService.getCargoInfo("stock_type = "+ps.getType()+" and type="+ cargoType +" and area_id = "+inCargoArea.getId()+" and store_type = "+CargoInfoBean.STORE_TYPE2);
						if(cargo == null){
							service.getDbOp().rollbackTransaction();
							throw new MyRuntimeException("目的售后配件库缓存区货位未设置，请先添加后再完成入库！");
						}
						cps = new CargoProductStockBean();
						cps.setCargoId(cargo.getId());
						cps.setProductId(sh.getProductId());
						cps.setStockCount(sh.getStockInCount());
						if(!cargoService.addCargoProductStock(cps)){
							service.getDbOp().rollbackTransaction();
							throw new MyRuntimeException("添加货位库存记录失败！");
						}
						cps.setId(cargoService.getDbOp().getLastInsertId());
						
						if(!cargoService.updateCargoInfo("status = 0", "id = "+cargo.getId()))
						{
						  service.getDbOp().rollbackTransaction();
						  throw new MyRuntimeException("数据库操作失败");
						}
					}else{
						cps = (CargoProductStockBean)cocList.get(0);
						if(!cargoService.updateCargoProductStockCount(cps.getId(), sh.getStockInCount()))
						{
						  service.getDbOp().rollbackTransaction();
						  throw new MyRuntimeException("数据库操作失败");
						}
					}

					//更新订单已入库量和已入库总金额
					StockBatchBean batch = null;
					batch = service.getStockBatch("code='" + bean.getCode() + "' and product_id = " + sh.getProductId() + " and stock_type = " + ProductStockBean.STOCKTYPE_CHECK + " and stock_area = " + bean.getStockArea());
					if( batch == null ) {
						batch = new StockBatchBean();
						batch.setCode(bean.getCode());
						batch.setProductId(sh.getProductId());
						batch.setPrice(sh.getPrice3());
						batch.setBatchCount(sh.getStockInCount());
						batch.setProductStockId(ps.getId());
						batch.setStockArea(bean.getStockArea());
						batch.setStockType(ProductStockBean.STOCKTYPE_AFTER_SALE_FIITING);
						batch.setCreateDateTime(DateUtil.getNow());
						batch.setNotaxPrice(Arith.div(sh.getPrice3(), 1+Float.valueOf(supplierStandardInfo.getTaxPoint())));
						batch.setTicket(0);
						batch.setSupplierId(supplierStandardInfo.getId());
						batch.setTax(supplierStandardInfo.getTaxPoint());
						if(!service.addStockBatch(batch))
						{
						  service.getDbOp().rollbackTransaction();
						  throw new MyRuntimeException("数据库操作失败");
						}
					} else {
						if( !service.updateStockBatch("batch_count = " + (batch.getBatchCount() + sh.getStockInCount()), "code='" + bean.getCode() + "' and product_id = " + sh.getProductId() + " and stock_type = " + ProductStockBean.STOCKTYPE_AFTER_SALE_FIITING + " and stock_area = " + bean.getStockArea())) {
							service.getDbOp().rollbackTransaction();
							throw new MyRuntimeException("修改批次数量 时据库操作失败！");
						}
					}
					
					//添加批次操作记录
					StockBatchLogBean batchLog = new StockBatchLogBean();
					batchLog.setCode(batch.getCode());
					batchLog.setStockType(batch.getStockType());
					batchLog.setStockArea(batch.getStockArea());
					batchLog.setBatchCode(batch.getCode());
					batchLog.setBatchCount(batch.getBatchCount());
					batchLog.setBatchPrice(batch.getPrice());
					batchLog.setProductId(batch.getProductId());
					if(fittingBuyStockInBean.getType()==FittingBuyStockInBean.TYPE2){
						batchLog.setRemark("维修返还入库");
					}else{
						batchLog.setRemark("采购入库");
					}
					
					batchLog.setCreateDatetime(DateUtil.getNow());
					batchLog.setUserId(user.getId());
					batchLog.setSupplierId(supplierStandardInfo.getId());
					batchLog.setTax(supplierStandardInfo.getTaxPoint());
					if(!service.addStockBatchLog(batchLog))
					{
					  service.getDbOp().rollbackTransaction();
					  throw new MyRuntimeException("数据库操作失败");
					}
					//判断并插入供货商关联信息 
					voProductSupplier productSupplier = supplierService.getProductSupplierInfo("product_id = "+sh.getProductId()+" and supplier_id = "+sh.getProductProxyId());
					if(productSupplier == null){
						productSupplier = new voProductSupplier();
						productSupplier.setProduct_id(sh.getProductId());
						productSupplier.setSupplier_id(sh.getProductProxyId());
						if(supplierStandardInfo!=null){
							productSupplier.setSupplier_name(supplierStandardInfo.getName());
						}
						if(!supplierService.addProductSupplierInfo(productSupplier))
						{
						  service.getDbOp().rollbackTransaction();
						  throw new MyRuntimeException("数据库操作失败");
						}
					}
					
					// 审核通过，就加 进销存卡片
					product.setPsList(psService.getProductStockList("product_id=" + sh.getProductId(), -1, -1, null));
					cps = (CargoProductStockBean)cargoService.getCargoAndProductStockList("cps.id = "+cps.getId(), 0, 1, "cps.id asc").get(0);

					//计算入库金额
					double totalPrice = batchLog.getBatchCount()*batchLog.getBatchPrice();
					//添加进销存卡片
					int stockCardType = StockCardBean.CARDTYPE_AFTERSALE_FITTING_BUYSTOCKIN;
					if(fittingBuyStockInBean.getType()==FittingBuyStockInBean.TYPE2){
						stockCardType = StockCardBean.CARDTYPE_AFTERSALE_FITTING_REPAIR_RETURN;
					}
					//添加货位进销存卡片
					int cargoStockCardType = CargoStockCardBean.CARDTYPE_AFTERSALE_FITTING_BUY_STOCKIN;
					if(fittingBuyStockInBean.getType()==FittingBuyStockInBean.TYPE2){
						cargoStockCardType = CargoStockCardBean.CARDTYPE_AFTERSALE_FITTING_REPAIR_RETURN_STOCKIN;
					}
					
					StockCardBean sc = this.addStockCard(stockCardType, 
							bean.getCode(), 
							bean.getStockType(),
							bean.getStockArea(),
							sh.getProductId(), 
							sh.getStockInId(), 
							sh.getStockInCount(), 
							totalPrice,
							price5,
							product);
					
					this.addCargoStockCard(
							cargoStockCardType,
							bean.getCode(), 
							bean.getStockType(),
							bean.getStockArea(), 
							sh.getProductId(), 
							cps.getId(), 
							sh.getStockInCount(), 
							totalPrice, 
							cps.getStockCount()+cps.getStockLockCount(), 
							cps.getCargoInfo().getStoreType(), 
							cps.getCargoInfo().getWholeCode(), 
							price5, 
							product);
				}

				if (count == 0) {
					service.getDbOp().rollbackTransaction();
					throw new MyRuntimeException("该操作没有任何库存变动，不能执行！");
				}

				//提交事务
				service.getDbOp().commitTransaction();
			}
	
	}
	
	public StockCardBean addStockCard(int cardType, String code, int stockType, int stockArea, int productId, int stockId, int stockInCount, 
			double totalPrice,float stockPrice, voProduct product) {
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		StockCardBean sc = new StockCardBean();
		sc.setCardType(cardType);
		sc.setCode(code);
		sc.setCreateDatetime(DateUtil.getNow());
		sc.setStockType(stockType);
		sc.setStockArea(stockArea);
		sc.setProductId(productId);
		sc.setStockId(stockId);
		sc.setStockInCount(stockInCount);
		sc.setStockInPriceSum(totalPrice);
//		sc.setStockInPriceSum((new BigDecimal(sh.getStockInCount())).multiply(new BigDecimal(StringUtil.formatDouble2(sh.getPrice3()))).doubleValue());
		sc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
		sc.setStockAllArea(product.getStock(sc.getStockArea()) + product.getLockCount(sc.getStockArea()));
		sc.setStockAllType(product.getStockAllType(sc.getStockType()) + product.getLockCountAllType(sc.getStockType()));
		sc.setAllStock(product.getStockAll() + product.getLockCountAll());
		sc.setStockPrice(stockPrice);
		sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
		if(!psService.addStockCard(sc))
		{
		  this.getDbOp().rollbackTransaction();
		  throw new MyRuntimeException("数据库操作失败");
		}
		return sc;
	}
	
	public void addCargoStockCard(int cardType, String code, int stockType, int stockArea, int productId, int stockId, int stockInCount,  
			double totalPrice, int currentCargoStock, int cargoStoreType, String cargoWholeCode, float stockPrice, voProduct product ) {
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		//货位入库卡片
		CargoStockCardBean csc = new CargoStockCardBean();
		csc.setCardType(cardType);
		csc.setCode(code);
		csc.setCreateDatetime(DateUtil.getNow());
		csc.setStockType(stockType);
		csc.setStockArea(stockArea);
		csc.setProductId(productId);
		csc.setStockId(stockId);
		csc.setStockInCount(stockInCount);
		csc.setStockInPriceSum(totalPrice);
		csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
		csc.setAllStock(product.getStockAll() + product.getLockCountAll());
		csc.setCurrentCargoStock(currentCargoStock);
		csc.setCargoStoreType(cargoStoreType);
		csc.setCargoWholeCode(cargoWholeCode);
		csc.setStockPrice(stockPrice);
		csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
		if(!cargoService.addCargoStockCard(csc))
		{
		  this.getDbOp().rollbackTransaction();
		  throw new MyRuntimeException("数据库操作失败");
		}
	}
	
	public boolean addFittingBuyStockInProduct(FittingBuyStockInProduct bean) {
		return addXXX(bean,"fitting_buy_stockin_product");
	}
	
	public boolean addFittingBuyStockIn(FittingBuyStockInBean bean) {
		return addXXX(bean,"fitting_buy_stockin");
	}
	
	public FittingBuyStockInBean getFittingBuyStockIn(String condition) {
		return (FittingBuyStockInBean) getXXX(condition, "fitting_buy_stockin", "mmb.stock.fitting.model.FittingBuyStockInBean");
	}
}

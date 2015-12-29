package mmb.rec.oper.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockExchangeProductBean;
import adultadmin.bean.stock.StockExchangeProductCargoBean;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.impl.CargoServiceImpl;
import adultadmin.service.impl.ProductStockServiceImpl;
import adultadmin.service.impl.StockServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

import mmb.rec.oper.bean.ConsignmentProductTempBean;
import mmb.rec.oper.bean.ProductSellPropertyBean;
import mmb.stock.IMEI.IMEIService;
import mmb.stock.stat.ClaimsVerificationBean;
import mmb.ware.WareService;

public class ConsignmentService extends BaseServiceImpl {

	
	public ConsignmentService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public ConsignmentService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	//商品销售属性
	public boolean addProductSellProperty(ProductSellPropertyBean bean) {
		return addXXX(bean, "product_sell_property");
	}

	public List getProductSellPropertyList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "product_sell_property", "mmb.rec.oper.bean.ProductSellPropertyBean");
	}
	
	public int getProductSellPropertyCount(String condition) {
		return getXXXCount(condition, "product_sell_property", "id");
	}

	public ProductSellPropertyBean getProductSellProperty(String condition) {
		return (ProductSellPropertyBean) getXXX(condition, "product_sell_property",
		"mmb.rec.oper.bean.ProductSellPropertyBean");
	}

	public boolean updateProductSellProperty(String set, String condition) {
		return updateXXX(set, condition, "product_sell_property");
	}

	public boolean deleteProductSellProperty(String condition) {
		return deleteXXX(condition, "product_sell_property");
	}
	
	/**
	 * 根据商品id判断一个商品是否是代销商品
	 * @param productId
	 * @return
	 */
	public boolean isProductConsignment(int productId){
		//查product_sell_property里的数据 如果type为1 则为代销，否则不是，如果无数据的情况应属于异常情况。
		ProductSellPropertyBean pspBean = this.getProductSellProperty("product_id=" + productId);
		if( pspBean == null ) {
			return false;
		} else {
			if( pspBean.getType() == 1 ) {
				return true;
			} else {
				return false;
			}
		}
		//select * from cargo_info ci, cargo_info_stock_area cisa where ci.stock_area_id = cisa.id and cisa.code='F' and area_id=  //注意这里 area_id 和 ware_area的关系。
	}

	/**
	 * 这个方法针对快销商品调往合格库时，单独对调拨的目的货位的分配进行划分，只可以调往指定区
	 * @param stockInType
	 * @param areaId
	 * @param storeType
	 * @return
	 */
	public CargoInfoBean getStockExchangeConsignmenttInCargo(int stockInType,
			int areaId, int storeType) {
		CargoServiceImpl cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE,this.getDbOp());
		CargoInfoBean result = null;
		StringBuilder condition =  new StringBuilder();
		condition.append(" ci.stock_type=").append(stockInType);
		condition.append(" and ci.area_id = ").append(areaId);
		condition.append(" and ci.stock_type = ").append(storeType);
		result = cargoService.getCargoInfoWithStockAreaCodeRestrict(condition.toString(), Constants.CONSIGNMENT_STOCK_AREA);
		return result;
	}
	
	
	
	/**
	 * 查询退货库中有库存的快销商品的 存量信息
	 * @param string
	 * @param i
	 * @param countPerPage
	 * @param object
	 * @return
	 */
	public List getReturnConsignmentCargoProductStockList(String sql, int index,
			int count, String orderBy) {
		List result = new ArrayList();
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		if( orderBy != null && !orderBy.equals("")) {
			sql += " order by " + orderBy;
		}
		sql = DbOperation.getPagingQuery(sql, index, count);
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				ConsignmentProductTempBean cptBean = new ConsignmentProductTempBean();
				cptBean.setStockCount(rs.getInt("cps.stock_count"));
				cptBean.setStockLockCount(rs.getInt("cps.stock_lock_count"));
				cptBean.setCargoInfoId(rs.getInt("ci.id"));
				cptBean.setProductCode(rs.getString("p.code"));
				cptBean.setProductName(rs.getString("p.name"));
				cptBean.setCargoProductStockId(rs.getInt("cps.id"));
				cptBean.setProductId(rs.getInt("p.id"));
				cptBean.setAreaId(rs.getInt("cia.old_id"));
				cptBean.setWholeCode(rs.getString("ci.whole_code"));
				result.add(cptBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	/**
	 * 用来根据提供的sql查询数量
	 * @param sqlCount
	 * @return
	 */
	public int getReturnConsignmentProductStockCount(String sqlCount) {
		int result = 0;
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		
		rs = dbOp.executeQuery(sqlCount);
		try {
			if (rs.next()) {
				result = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	/**
	 * 判断一个货位的stock_area 是不是 给定参数的区域的
	 * @param id
	 * @param string
	 * @return
	 */
	public boolean isCargoStockAreaCodeSub(int cargoInfoId, String stockAreaCode) {
		boolean result = false;
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return false;
		}
		StringBuilder sql = new StringBuilder("select ci.id from cargo_info ci, cargo_info_stock_area cisa where ci.stock_area_id = cisa.id");
		sql.append(" and ci.id =").append(cargoInfoId);
		sql.append(" and cisa.code='").append(stockAreaCode).append("'");
		rs = dbOp.executeQuery(sql.toString());
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
	 * 基本的添加调拨单的方法只添加调拨单主体，返回Object 为String是发生了异常，返回StockExchangeBean是正常生成了，  返回的bean 带有code 和 id
	 * @param user  用户
	 * @param outWareArea  源地区
	 * @param inWareArea  目的地区
	 * @param outStockType 源库存类型
	 * @param inStockType 目的库存类型
	 * @return 
	 * 
	 */
	public Object createStockExchange(voUser user, int outWareArea, int inWareArea, int outStockType, int inStockType) {
		IStockService service = new StockServiceImpl(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		IProductStockService psService = new ProductStockServiceImpl(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		StockExchangeBean bean = new StockExchangeBean();
        bean.setCreateDatetime(DateUtil.getNow());
        String day = DateUtil.formatDate(new Date(), "yyyy-MM-dd");
        String name = day + "库存调拨";
        bean.setName(name);
        bean.setRemark("");
        bean.setStatus(StockExchangeBean.STATUS0);
        bean.setConfirmDatetime(DateUtil.getNow());
        Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd");
		String brefCode = "DB" + sdf.format(cal.getTime());
        bean.setCode(brefCode);
        bean.setCreateUserId(user.getId());
        bean.setCreateUserName(user.getUsername());
        bean.setStockOutOperName("");
        bean.setAuditingUserName("");
        bean.setStockInOperName("");
        bean.setAuditingUserName2("");
        bean.setStockOutArea(outWareArea);
        bean.setStockInArea(inWareArea);
        bean.setStockOutType(outStockType);
        bean.setStockInType(inStockType);
        bean.setPriorStatus(StockExchangeBean.PRIOR_STATUS0);
        if (!psService.addStockExchange(bean)) {
            return "添加调拨单失败！";
        }
        int id = service.getDbOp().getLastInsertId();
        
		bean.setId(id);
		
		//此处修改调拨单Code
		String newCode = null;
		if(id > 9999){
			String strId = String.valueOf(id);
			newCode = strId.substring(strId.length()- 4, strId.length());
		} else {
			DecimalFormat df2 = new DecimalFormat("0000");
			newCode = df2.format(id);
		}
		String totalCode = brefCode + newCode;
		StringBuilder updateBuf = new StringBuilder();
		updateBuf.append("update stock_exchange set code='" + totalCode + "' where id=").append(id);
		if( !service.getDbOp().executeUpdate(updateBuf.toString())) {
			return "添加调拨单失败！";
		}
        bean.setCode(totalCode);
        //log记录
        StockAdminHistoryBean log2 = new StockAdminHistoryBean();
        log2.setAdminId(user.getId());
        log2.setAdminName(user.getUsername());
        log2.setLogId(id);
        log2.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
        log2.setOperDatetime(DateUtil.getNow());
        log2.setRemark("新建商品调配操作：" + bean.getName());
        log2.setType(StockAdminHistoryBean.CREATE);
        if ( !service.addStockAdminHistory(log2)) {
        	return "添加调拨单失败！";
        }
		return bean;
	}

	/**
	 * 特殊的添加调拨单商品的方法。需要传入源货位库存的id列表，并且会把所有的货位可用库存数量调拨走。
	 * @param user
	 * @param bean
	 * @param cpsIds
	 * @return
	 */
	public String addProductToStockExchange(voUser user, StockExchangeBean bean, List<String> cpsIds) {
		WareService wareService = new WareService(this.getDbOp());
		IStockService service = new StockServiceImpl(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		ICargoService cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		IProductStockService psService = new ProductStockServiceImpl(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		IMEIService imeiService = new IMEIService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		int stockInArea = bean.getStockInArea();
        int stockInType = bean.getStockInType();
        int stockOutArea = bean.getStockOutArea();
        int stockOutType = bean.getStockOutType();
        if(stockInArea == stockOutArea && stockInType == stockOutType){
        	return "不能在同一个库中调配商品！";
        }
        //新货位管理判断
        CargoInfoAreaBean inCargoArea2 = cargoService.getCargoInfoArea("old_id = "+bean.getStockInArea());
        CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+bean.getStockOutArea());
        for (String cpsId : cpsIds ) {
            
            CargoProductStockBean cpsOut = (CargoProductStockBean)cargoService.getCargoProductStock("id=" + cpsId);
            if( cpsOut.getStockCount() <= 0 ) {
            	//如果数量不正确，就不参与调拨
            	continue;
            }
            CargoInfoBean ciIn = cargoService.getCargoInfo("stock_type = "+bean.getStockInType()+" and area_id = "+inCargoArea2.getId()+" and store_type = "+CargoInfoBean.STORE_TYPE2); 
            if(ciIn == null){
            	return "目的库无缓存区货位信息，请先添加货位后，再进行调拨操作！";
            }
            voProduct product = wareService.getProduct(cpsOut.getProductId());
            if( imeiService.isProductMMBMobile(product.getId()) ) {
            	return "商品["+product.getCode()+"]需要IMEI码才可以调拨，不能使用此功能！";
            }
            
            //----------给不合格记录添加调拨单和入库单的信息-------------
    		//采购入库单号
    		//将所有add和update集中起来包括一些查询的最后id的地方
    		ProductStockBean psIn = psService.getProductStock("product_id=" + cpsOut.getProductId() + " and type=" + stockInType + " and area=" + stockInArea);
            ProductStockBean psOut = psService.getProductStock("product_id=" + cpsOut.getProductId() + " and type=" + stockOutType + " and area=" + stockOutArea);
            if(psOut == null){
            		return "商品["+product.getCode()+"]源库没有库存记录！";
            }
            if(psIn == null){
            		return "商品["+product.getCode()+"]目的库没有库存记录！";
            }
            
            //添加调配记录
            StockExchangeProductBean sep = null;
            sep = new StockExchangeProductBean();
            sep.setCreateDatetime(DateUtil.getNow());
            sep.setConfirmDatetime(null);
            sep.setStockExchangeId(bean.getId());
            sep.setProductId(cpsOut.getProductId());
            sep.setRemark("快销商品从退货库调往返厂库");
            sep.setStatus(StockExchangeProductBean.STOCKOUT_UNDEAL);
            sep.setStockOutCount(cpsOut.getStockCount());
            sep.setStockInCount(cpsOut.getStockCount());
            sep.setStockOutId(psOut.getId());
            sep.setStockInId(psIn.getId());
            sep.setReason(1);
            sep.setReasonText("快销商品从退货库调往返厂库");
    		
    		//添加调拨单商品
    		if (!psService.addStockExchangeProduct(sep)) {
                return "调拨单商品添加失败！";
            }
    		int stockExchangeId = psService.getDbOp().getLastInsertId();
    		sep.setId(stockExchangeId);
    		//添加调拨产品货位信息
            int sepId = psService.getDbOp().getLastInsertId();
            List cpsInList = cargoService.getCargoAndProductStockList("ci.stock_type = "+bean.getStockInType()+" and ci.area_id = "+inCargoArea2.getId()+" and cps.product_id = "+cpsOut.getProductId()+" and ci.store_type = "+CargoInfoBean.STORE_TYPE2, 0, 1, "ci.id asc");
            CargoProductStockBean cpsIn = null;
            if(cpsInList == null || cpsInList.size() == 0){
            	cpsIn = new CargoProductStockBean();
            	cpsIn.setCargoId(ciIn.getId());
            	cpsIn.setProductId(cpsOut.getProductId());
            	if( !cargoService.addCargoProductStock(cpsIn)) {
            		return "添加调拨单信息时，数据库操作失败！";
            	}
            	cpsIn.setId(cargoService.getDbOp().getLastInsertId());
            	
    			if( !cargoService.updateCargoInfo("status = 0", "id = "+ciIn.getId())) {
    				return "添加调拨单信息时，数据库操作失败！";
    			}
            }else{
            	cpsIn = (CargoProductStockBean)cpsInList.get(0);
            }
            StockExchangeProductCargoBean sepcOut = new StockExchangeProductCargoBean();
            sepcOut.setStockExchangeProductId(sepId);
            sepcOut.setStockExchangeId(bean.getId());
            sepcOut.setStockCount(cpsOut.getStockCount());
            sepcOut.setCargoProductStockId(cpsOut.getId());
            sepcOut.setCargoInfoId(cpsOut.getCargoId());
            sepcOut.setType(0);
            if( !psService.addStockExchangeProductCargo(sepcOut)) {
            	return "添加调拨单信息时，数据库操作失败！";
            }
            StockExchangeProductCargoBean sepcIn = new StockExchangeProductCargoBean();
            sepcIn.setStockExchangeProductId(sepId);
            sepcIn.setStockExchangeId(bean.getId());
            sepcIn.setStockCount(cpsOut.getStockCount());
            sepcIn.setCargoProductStockId(cpsIn.getId());
            sepcIn.setCargoInfoId(cpsIn.getCargoId());
            sepcIn.setType(1);
            if( !psService.addStockExchangeProductCargo(sepcIn)) {
            	return "添加调拨单信息时，数据库操作失败！";
            }
           
    		//log添加调拨商品的记录
            StockAdminHistoryBean log3 = new StockAdminHistoryBean();
            log3.setAdminId(user.getId());
            log3.setAdminName(user.getUsername());
            log3.setLogId(bean.getId());
            log3.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
            log3.setOperDatetime(DateUtil.getNow());
            log3.setRemark("修改商品调配操作：" + bean.getName() + ",添加了商品[" + product.getCode() + "],源库[" + ProductStockBean.getAreaName(psOut.getArea()) + ProductStockBean.getStockTypeName(psOut.getType()) + "],目的库[" + ProductStockBean.getAreaName(psIn.getArea()) + ProductStockBean.getStockTypeName(psIn.getType()) + "],调拨数量:" + sep.getStockOutCount());
            log3.setType(StockAdminHistoryBean.CHANGE);
            if( !service.addStockAdminHistory(log3)) {
            	return "添加日志信息时，数据库操作失败！";
            }
            if(!psService.updateStockExchange("status=" + StockExchangeBean.STATUS1, "id=" + bean.getId())) {
            	return "添加调拨单商品失败！";
            }
        }
        
        return "SUCCESS";
	}

	/**
	 * 确认调拨单 对所有调拨单中的商品执行锁定， 如果有商品没通过，整个调拨单 也无法提交
	 * @param user
	 * @param stockExchangeId
	 * @return
	 */
	public String confirmStockExchange (voUser user, int stockExchangeId) {
		WareService wareService = new WareService(this.getDbOp());
		IStockService stockService = new StockServiceImpl(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		ICargoService cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		IProductStockService service = new ProductStockServiceImpl(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		StockExchangeBean bean = service.getStockExchange("id = " + stockExchangeId);
    	StringBuilder buf = new StringBuilder();
        String condition = null;
        
        buf.append("stock_exchange_id = ");
        buf.append(bean.getId());
        condition = buf.toString();
        ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id");
        Iterator itr = sepList.iterator();
        StockExchangeProductBean sep = null;
        voProduct product = null;
        String set = null;
        while (itr.hasNext()) {
            sep = (StockExchangeProductBean) itr.next();
            product = wareService.getProduct(sep.getProductId());
			
			ProductStockBean psOut = null;
			ProductStockBean psIn = null;
			// 如果是 没处理的 记录，在确认出库的时候，就要把 库存数量锁定
			if ( sep.getStatus() == StockExchangeProductBean.STOCKOUT_UNDEAL) {
				product = wareService.getProduct(sep.getProductId());
				psIn = service.getProductStock("id=" + sep.getStockInId());
				psOut = service.getProductStock("id=" + sep.getStockOutId());
				
				if (sep.getStockOutCount() > psOut.getStock()) {
					return "调拨量大于库存量！";
				}
				set = "remark = '操作前库存"
						+ psOut.getStock() + ",操作后库存"
						+ (psOut.getStock() - sep.getStockOutCount())
						+ "', confirm_datetime = now()";
				if(!service.updateStockExchangeProduct(set, "id = " + sep.getId()))
				{
				  return "数据库操作失败!";
				}
				if(!service.updateProductStockCount(sep.getStockOutId(), -sep.getStockOutCount())){
					return "修改库存锁定量失败！";
				}
				if(!service.updateProductLockCount(sep.getStockOutId(), sep.getStockOutCount())){
					return "修改库存锁定量失败！";
				}
				
				//如果是从合格库调拨出去，则需要自动检测库存，如果合格库的库存为0，则隐藏商品
				if(psOut.getType()==ProductStockBean.STOCKTYPE_QUALIFIED && psIn.getType() != ProductStockBean.STOCKTYPE_QUALIFIED 
						&& (product.getParentId1() == 123 || product.getParentId1() == 143
						|| product.getParentId1() == 316 || product.getParentId1() == 317
						|| product.getParentId1() == 119 || product.getParentId1() == 340
						|| product.getParentId1() == 1385 || product.getParentId1() == 1425 
						|| product.getParentId1() == 544 || product.getParentId1() == 545
						|| product.getParentId1() == 458 || product.getParentId1() == 459 
						|| product.getParentId1() == 401 || product.getParentId1() == 136 
						|| product.getParentId2() == 203 || product.getParentId2() == 204 
						|| product.getParentId2() == 205 || product.getParentId2() == 699
						|| product.getParentId1() == 145 || product.getParentId1() == 151
						|| product.getParentId1() == 197 || product.getParentId1() == 505
						|| product.getParentId1() == 163 || product.getParentId1() == 690
						|| product.getParentId1() == 908|| product.getParentId1() == 752
						|| product.getParentId1() == 803
						|| product.getParentId1() == 183 || product.getParentId1() == 184
						|| product.getParentId1() == 1093 || product.getParentId1() == 1094
						|| product.getParentId1() == 94 || product.getParentId1() == 6 || product.getParentId1() == 1222)){
					service.checkProductStatus(sep.getProductId());
				}

				// log记录
				StockAdminHistoryBean log = new StockAdminHistoryBean();
				log.setAdminId(user.getId());
				log.setAdminName(user.getUsername());
				log.setLogId(bean.getId());
				log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("商品调配操作：" + bean.getName() + "：将商品[" + product.getCode() + "]出库");
				log.setType(StockAdminHistoryBean.CHANGE);
				if(!stockService.addStockAdminHistory(log))
				{
				  return "数据库操作失败!";
				}
				//锁定货位库存
				//出库
				List sepcOutList = service.getStockExchangeProductCargoList("stock_exchange_id = "+bean.getId()+" and stock_exchange_product_id = "+sep.getId()+" and type = 0", -1, -1, "id asc");
				for(int i=0;i<sepcOutList.size();i++){
					StockExchangeProductCargoBean sepcOut = (StockExchangeProductCargoBean)sepcOutList.get(i);
					if(!cargoService.updateCargoProductStockCount(sepcOut.getCargoProductStockId(), -sepcOut.getStockCount())){
						return "锁定货位库存失败！";
                    }
					if(!cargoService.updateCargoProductStockLockCount(sepcOut.getCargoProductStockId(), sepcOut.getStockCount())){
                    	return "锁定货位库存失败！";
                    }
				}
			}
			
            set = "status = " + StockExchangeProductBean.STOCKOUT_DEALED + ", confirm_datetime = now()";
            if(!service.updateStockExchangeProduct(set, "id = " + sep.getId()))
            {
              return "数据库操作失败!";
            }
			// log记录
			StockAdminHistoryBean log = new StockAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(bean.getId());
			log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("商品调配操作：" + bean.getName() + ": 确认商品[" + product.getCode() + "]出库");
			
			log.setType(StockAdminHistoryBean.CHANGE);
			if(!stockService.addStockAdminHistory(log))
			{
			  return "数据库操作失败!";
			}
			//修改调拨单状态为出库处理中
			if(!service.updateStockExchange("status="+StockExchangeBean.STATUS1, "id="+bean.getId())){
				return "数据库操作失败!";
			}
        }
        return "SUCCESS";
	}
	
	/**
	 * 修改调拨单单位出库确认完成状态
	 * @param user
	 * @param stockExchangeId
	 * @return
	 */
	public String completeStockOut(voUser user, int stockExchangeId) {
		IStockService stockService = new StockServiceImpl(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		IProductStockService service = new ProductStockServiceImpl(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		StockExchangeBean bean = service.getStockExchange("id = " + stockExchangeId);
		StringBuilder buf = new StringBuilder();
        String condition = null;
		buf.append("stock_exchange_id = ");
        buf.append(bean.getId());
        buf.append(" and status = ");
        buf.append(StockExchangeProductBean.STOCKOUT_UNDEAL);
        condition = buf.toString();
    	int count = service.getStockExchangeProductCount(condition);
    	if(count > 0){
            return "还有没确认的商品，不能完成出库操作！";
    	}
    	String set = "status = " + StockExchangeBean.STATUS2 + ", confirm_datetime = now(), stock_out_oper=" + user.getId()+", stock_out_oper_name='"+user.getUsername()+"'";
    	if(!service.updateStockExchange(set, "id = " + bean.getId()))
    	{
    	  return "数据库操作失败!";
    	}
		// log记录
		StockAdminHistoryBean log = new StockAdminHistoryBean();
		log.setAdminId(user.getId());
		log.setAdminName(user.getUsername());
		log.setLogId(bean.getId());
		log.setLogType(StockAdminHistoryBean.STOCK_EXCHANGE);
		log.setOperDatetime(DateUtil.getNow());
		log.setRemark("商品调配操作：" + bean.getName() + ": 确认完成出库操作");
		log.setType(StockAdminHistoryBean.CHANGE);
		if(!stockService.addStockAdminHistory(log))
		{
		  return "数据库操作失败!";
		}
		return "SUCCESS";
	}

}

package mmb.stock.stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.cargo.CartonningInfoBean;
import mmb.stock.cargo.CartonningInfoService;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voProductLineCatalog;
import adultadmin.action.vo.voUser;
import adultadmin.bean.buy.BuyAdminHistoryBean;
import adultadmin.bean.buy.BuyOrderBean;
import adultadmin.bean.buy.BuyOrderProductBean;
import adultadmin.bean.buy.BuyStockBean;
import adultadmin.bean.buy.BuyStockProductBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.supplier.SupplierStandardInfoBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class CheckStockinMissionService extends BaseServiceImpl{

	private final Log logger = LogFactory.getLog(CheckStockinMissionService.class);
	
	
	public CheckStockinMissionService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public CheckStockinMissionService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	public CheckStockinMissionService(int useConnType) {
		this.useConnType = useConnType;
		this.dbOp = new DbOperation();
		this.dbOp.init();
	}
	
	//入库上架
	public boolean addBuyStockinUpshelf(BuyStockinUpshelfBean bean) {
		return addXXX(bean, "buy_stockin_upshelf");
	}

	public List getBuyStockinUpshelfList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "buy_stockin_upshelf", "mmb.stock.stat.BuyStockinUpshelfBean");
	}

	public int getBuyStockinUpshelfCount(String condition) {
		return getXXXCount(condition, "buy_stockin_upshelf", "id");
	}

	public BuyStockinUpshelfBean getBuyStockinUpshelf(String condition) {
		return (BuyStockinUpshelfBean) getXXX(condition, "buy_stockin_upshelf",
		"mmb.stock.stat.BuyStockinUpshelfBean");
	}

	public boolean updateBuyStockinUpshelf(String set, String condition) {
		return updateXXX(set, condition, "buy_stockin_upshelf");
	}

	public boolean deleteBuyStockinUpshelf(String condition) {
		return deleteXXX(condition, "buy_stockin_upshelf");
	}

	
	/**
	 * 添加质检入库任务单
	 * @param planBillNum
	 * @param user
	 * @param productCode
	 */
	private String addCheckStockinMission(BuyStockBean buyStock, voUser user,
			voProduct product) {
		
		try{
			
			StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
			IStockService stockService = ServiceFactory.createStockService(
													IBaseService.CONN_IN_SERVICE, this.getDbOp());
			
			String nowDate = DateUtil.getNowDateStr();
			String beginDate = nowDate + " 00:00:00";
			String endDate = nowDate + " 23:59:59";
			//查询今天生成的同优先级的产能外质检入库任务，如果存在，则不需要再重新修改产能
			boolean needExecutEffect=true;//是否需要重新计算产能
			CheckStockinMissionBean lastMission=statService.getCheckStockinMission("create_date_time between '"+beginDate
					+"' and '"+endDate+"' and code like 'ZJ%' and prior_status="+CheckStockinMissionBean.PRIOR_NORMAL
					+" and ware_area="+buyStock.getArea()+" and product_load='1'");
			if(lastMission!=null){
				needExecutEffect=false;
			}
			
			String code = getNewMissionCode();
			CheckStockinMissionBean bean = new CheckStockinMissionBean();
			bean.setCreateDatetime(DateUtil.getNow());
			bean.setCode(code);
			bean.setStatus(CheckStockinMissionBean.STATUS0);
			bean.setPriorStatus(CheckStockinMissionBean.PRIOR_NORMAL);
			bean.setCreateOperId(user.getId());
			bean.setCreateOperName(user.getUsername());
			bean.setBuyStockinCode(buyStock.getCode());
			bean.setBuyStockinId(buyStock.getId());
			bean.setWareArea(buyStock.getArea());
			if(needExecutEffect==false){
				bean.setProductLoad("1");
			}
			if(!statService.addCheckStockinMission(bean)){
				throw new RuntimeException("添加质检任务失败！");
			}
			int id = this.getDbOp().getLastInsertId();
			
			//计算产能
			if(needExecutEffect==true){
				executeEffect(this.getDbOp(),buyStock.getArea());
			}
			
			//计算产能
			//获取质检效率值
			int checkEffect = getCheckEffect(product.getId());
//			checkEffect = 30;
			if(checkEffect == -1){
				return "checkEffect";//没有质检效率
			}
			String identifyInfo = getIdentifyInfo(product.getId());
			if( identifyInfo == null || identifyInfo.length() == 0 ) {
				return "checkEffect";
			}
			
			
//			//获取日在编人数
//			int staffNum = getStaffNum();
//			staffNum = 10;
//			if(staffNum<0){
//				return "该sku没有当日在编人数，生成任务失败！";
//			}
			
			
			//添加任务产品信息
			CheckStockinMissionBatchBean batchBean = null;
			BuyStockProductBean bProductBean = stockService.getBuyStockProduct(
					"buy_stock_id="+buyStock.getId()+" and product_id="+product.getId());
			SupplierStandardInfoBean spplierBean = null;
			batchBean = new CheckStockinMissionBatchBean();
			batchBean.setBuyCount(bProductBean.getBuyCount());
			batchBean.setBuyStockinId(buyStock.getId());
			batchBean.setMissionId(id);
			batchBean.setProductId(bProductBean.getProductId());
			batchBean.setStatus(CheckStockinMissionBean.STATUS0);
			batchBean.setStockinCount(bProductBean.getBuyCount());
			batchBean.setStockinDatetime(DateUtil.getNow());
			spplierBean = stockService.getSupplierStandardInfoBean(
						"id=" + bProductBean.getProductProxyId());
			if(spplierBean != null){
				batchBean.setSupplierId(spplierBean.getId());
				batchBean.setSupplierName(spplierBean.getName());
			}else{
				batchBean.setSupplierName("");
			}
			if(!statService.addCheckStockinMissionBatch(batchBean)){
				throw new RuntimeException("添加质检任务产品信息失败！");
			}
	
//			String productLoad = "";
//			if(staffNum*8>=batchBean.getBuyCount()/checkEffect){
//				productLoad = CheckStockinMissionBean.INLOAD;
//			}else{
//				productLoad = CheckStockinMissionBean.OUTLOAD;
//			}
			
			if(!statService.updateCheckStockinMission("check_effect="+checkEffect,"id="+id)){
				throw new RuntimeException("更新任务质检效率失败，请联系管理员！");
			}
			
			//log记录
			BuyAdminHistoryBean log = new BuyAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(id);
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("新建质检入库操作：" + bean.getCode());
			log.setType(BuyAdminHistoryBean.TYPE_ADD);
			if(!stockService.addBuyAdminHistory(log)){
				throw new RuntimeException("添加质检操作记录失败！");
			}
			return null;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method addCheckStockinMission exception",e);
			}
			return "添加任务单失败！";
		}
		
	}
	
	/**
	 * 添加质检入库任务单
	 * @param planBillNum
	 * @param user
	 * @param productCode
	 */
	private Object addCheckStockinMission2(BuyStockBean buyStock, voUser user,
			voProduct product) {
		
		try{
			
			StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
			IStockService stockService = ServiceFactory.createStockService(
					IBaseService.CONN_IN_SERVICE, this.getDbOp());
			
			String nowDate = DateUtil.getNowDateStr();
			String beginDate = nowDate + " 00:00:00";
			String endDate = nowDate + " 23:59:59";
			//查询今天生成的同优先级的产能外质检入库任务，如果存在，则不需要再重新修改产能
			boolean needExecutEffect=true;//是否需要重新计算产能
			CheckStockinMissionBean lastMission=statService.getCheckStockinMission("create_date_time between '"+beginDate
					+"' and '"+endDate+"' and code like 'ZJ%' and prior_status="+CheckStockinMissionBean.PRIOR_NORMAL
					+" and ware_area="+buyStock.getArea()+" and product_load='1'");
			if(lastMission!=null){
				needExecutEffect=false;
			}
			
			String code = getNewMissionCode();
			CheckStockinMissionBean bean = new CheckStockinMissionBean();
			bean.setCreateDatetime(DateUtil.getNow());
			bean.setCode(code);
			bean.setStatus(CheckStockinMissionBean.STATUS0);
			bean.setPriorStatus(CheckStockinMissionBean.PRIOR_NORMAL);
			bean.setCreateOperId(user.getId());
			bean.setCreateOperName(user.getUsername());
			bean.setBuyStockinCode(buyStock.getCode());
			bean.setBuyStockinId(buyStock.getId());
			bean.setWareArea(buyStock.getArea());
			if(needExecutEffect==false){
				bean.setProductLoad("1");
			}
			if(!statService.addCheckStockinMission(bean)){
				throw new RuntimeException("添加质检任务失败！");
			}
			int id = this.getDbOp().getLastInsertId();
			bean.setId(id);
			//计算产能
			if(needExecutEffect==true){
				executeEffect(this.getDbOp(),buyStock.getArea());
			}
			
			//计算产能
			//获取质检效率值
			int checkEffect = getCheckEffect(product.getId());
//			checkEffect = 30;
			if(checkEffect == -1){
				return "checkEffect";//没有质检效率
			}
			String identifyInfo = getIdentifyInfo(product.getId());
			if( identifyInfo == null || identifyInfo.length() == 0 ) {
				return "checkEffect";
			}
			
			
//			//获取日在编人数
//			int staffNum = getStaffNum();
//			staffNum = 10;
//			if(staffNum<0){
//				return "该sku没有当日在编人数，生成任务失败！";
//			}
			
			
			//添加任务产品信息
			CheckStockinMissionBatchBean batchBean = null;
			BuyStockProductBean bProductBean = stockService.getBuyStockProduct(
					"buy_stock_id="+buyStock.getId()+" and product_id="+product.getId());
			SupplierStandardInfoBean spplierBean = null;
			batchBean = new CheckStockinMissionBatchBean();
			batchBean.setBuyCount(bProductBean.getBuyCount());
			batchBean.setBuyStockinId(buyStock.getId());
			batchBean.setMissionId(id);
			batchBean.setProductId(bProductBean.getProductId());
			batchBean.setStatus(CheckStockinMissionBean.STATUS0);
			batchBean.setStockinCount(bProductBean.getBuyCount());
			batchBean.setStockinDatetime(DateUtil.getNow());
			spplierBean = stockService.getSupplierStandardInfoBean(
					"id=" + bProductBean.getProductProxyId());
			if(spplierBean != null){
				batchBean.setSupplierId(spplierBean.getId());
				batchBean.setSupplierName(spplierBean.getName());
			}else{
				batchBean.setSupplierName("");
			}
			if(!statService.addCheckStockinMissionBatch(batchBean)){
				throw new RuntimeException("添加质检任务产品信息失败！");
			}
			int batchId = this.getDbOp().getLastInsertId();
			batchBean.setId(batchId);
			batchBean.setCheckStockinMission(bean);
//			String productLoad = "";
//			if(staffNum*8>=batchBean.getBuyCount()/checkEffect){
//				productLoad = CheckStockinMissionBean.INLOAD;
//			}else{
//				productLoad = CheckStockinMissionBean.OUTLOAD;
//			}
			
			if(!statService.updateCheckStockinMission("check_effect="+checkEffect,"id="+id)){
				throw new RuntimeException("更新任务质检效率失败，请联系管理员！");
			}
			
			//log记录
			BuyAdminHistoryBean log = new BuyAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(id);
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("新建质检入库操作：" + bean.getCode());
			log.setType(BuyAdminHistoryBean.TYPE_ADD);
			if(!stockService.addBuyAdminHistory(log)){
				throw new RuntimeException("添加质检操作记录失败！");
			}
			return batchBean;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method addCheckStockinMission exception",e);
			}
			return "添加任务单失败！";
		}
		
	}

	
	private String getIdentifyInfo(int productId) {
		String querySql = "select identity_info from product_ware_property pwp where pwp.product_id="+productId;
		ResultSet rs = null;
		String identityInfo = null;
		try{
			rs = this.getDbOp().executeQuery(querySql);
			while(rs.next()){
				identityInfo = rs.getString("identity_info");
			}
		}catch(Exception e){
			throw new RuntimeException();
		}
		return identityInfo;
	}

	public String getNewMissionCode() {
		
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		String code = "ZJ"+DateUtil.getNowDateStr().substring(2,10).replace("-", "");   
		int maxid = this.getNumber("id", "check_stockin_mission", "max", "id > 0 && code like 'ZJ%'");
		CheckStockinMissionBean stockin;
		stockin = statService.getCheckStockinMission("code like '" + code + "%'");
		if(stockin == null){
			//当日第一份入库单，编号最后三位 001
			code += "00001";
		}else {
			//获取当日入库单编号最大值
			stockin = statService.getCheckStockinMission("id =" + maxid); 
			String _code = stockin.getCode();
			int number = Integer.parseInt(_code.substring(_code.length()-5));
			number++;
			code += String.format("%05d",new Object[]{new Integer(number)});
		}
		return code;
	}

	public CheckStockinMissionBean getCheckStockinMissionBean(int missionId) {
		
		
		WareService wareService = new WareService(this.getDbOp());
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		CheckStockinMissionBean missionBean = statService.getCheckStockinMission("id=" + missionId);
		missionBean.setPriorStatusName((String)CheckStockinMissionBean.priorityMap.get(missionBean.getPriorStatus()+""));
		if(missionBean.getStatus()==CheckStockinMissionBean.STATUS0){
			missionBean.setStatusName("未处理");
		}else if(missionBean.getStatus()==CheckStockinMissionBean.STATUS1){
			missionBean.setStatusName("已确认到货");
		}else if(missionBean.getStatus()==CheckStockinMissionBean.STATUS2){
			missionBean.setStatusName("质检入库中");
		}else if(missionBean.getStatus()==CheckStockinMissionBean.STATUS3){
			missionBean.setStatusName("已完成");
		}else{
			missionBean.setStatusName("已删除");
		}
		List checkStockinMissionBatch = statService.getCheckStockinMissionBatchList(
														"mission_id=" + missionId, 0, -1, "stockin_date_time asc");
//		List batchBeanGroup = null;
		CheckStockinMissionBatchBean batchBean = null;
//		Map cacheBeanMap = new HashMap();
//		Map batchBeanMap = new HashMap();
//		missionBean.setBatchMap(batchBeanMap);
//		missionBean.setBatchSize(checkStockinMissionBatch.size());
		if(missionBean.getCompleteDatetime()!=null && !missionBean.getCompleteDatetime().equals("")){
			missionBean.setCompleteDatetime(missionBean.getCompleteDatetime().substring(0,19));
		}
		if(missionBean.getCreateDatetime() != null && !missionBean.getCreateDatetime().equals("")){
			missionBean.setCreateDatetime(missionBean.getCreateDatetime().substring(0,19));
		}
		for(int i=0; i<checkStockinMissionBatch.size(); i++){
			batchBean = (CheckStockinMissionBatchBean) checkStockinMissionBatch.get(i);
			if(batchBean.getCompleteDatetime()==null){
				batchBean.setCompleteDatetime("");
			}else{
				batchBean.setCompleteDatetime(batchBean.getCompleteDatetime().substring(0,19));
			}
			if(batchBean.getStatus()==CheckStockinMissionBatchBean.STATUS0){
				batchBean.setStatusName("未处理");
			}else if(batchBean.getStatus()==CheckStockinMissionBatchBean.STATUS1){
				batchBean.setStatusName("已确认到货");
			}else if(batchBean.getStatus()==CheckStockinMissionBatchBean.STATUS2){
				batchBean.setStatusName("质检入库中");
			}else if(batchBean.getStatus()==CheckStockinMissionBatchBean.STATUS3){
				batchBean.setStatusName("已完成");
			}else{
				batchBean.setStatusName("已删除");
			}
			if(batchBean.getStockinCount()>batchBean.getBuyCount()){
				batchBean.setDifferenceValue(batchBean.getStockinCount()-batchBean.getBuyCount());
			}else{
				batchBean.setDifferenceValue(batchBean.getBuyCount()-batchBean.getStockinCount());
			}
			voProduct product=wareService.getProduct(batchBean.getProductId());//产品
			if(product != null){
//				if(cacheBeanMap.isEmpty()){
//					if(batchBean.getStatus()==CheckStockinMissionBatchBean.STATUS3){
//						batchBean.setShowButton(false);
//					}else{
//						cacheBeanMap.put(product.getCode(), product.getCode());
//						batchBean.setShowButton(true);
//					}
//				}else{
//					if(cacheBeanMap.get(product.getCode()) != null){
//						batchBean.setShowButton(false);
//					}else{
//						if(batchBean.getStatus()==CheckStockinMissionBatchBean.STATUS3){
//							batchBean.setShowButton(false);
//						}else{
//							cacheBeanMap.put(product.getCode(), product.getCode());
//							batchBean.setShowButton(true);
//						}
//					}
//				}
				//设置产品的产品线名称
				if(wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1())!=null){
					String productLineName=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1()).getName();
					product.setProductLineName(productLineName);
				}else if(wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId2())!=null){
					String productLineName=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId2()).getName();
					product.setProductLineName(productLineName);
				}else if(wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId3())!=null){
					String productLineName=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId3()).getName();
					product.setProductLineName(productLineName);
				}else{
					product.setProductLineName("");
				}
				batchBean.setProduct(product);
				missionBean.setCsmBean(batchBean);
//				if(batchBeanMap.isEmpty()){
//					batchBeanGroup = new ArrayList();
//					batchBeanGroup.add(batchBean);
//					batchBeanMap.put(product.getCode(), batchBeanGroup);
//				}else{
//					if(batchBeanMap.get(product.getCode()) != null){
//						((List)batchBeanMap.get(product.getCode())).add(batchBean);
//					}else{
//						batchBeanGroup = new ArrayList();
//						batchBeanGroup.add(batchBean);
//						batchBeanMap.put(product.getCode(), batchBeanGroup);
//					}
//				}
			}
		}
//		cacheBeanMap.clear();
		return missionBean;
	}

	
	
	public void updateRealStockinCount(int realStockinCount, int batchId) {
		
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		CheckStockinMissionBatchBean batchBean = statService.getCheckStockinMissionBatch("id=" + batchId);
//		int buyCount = batchBean.getBuyCount();//预计到货数量
//		boolean flag = false;
//		if(realStockinCount != buyCount){
//			//生成差异记录
//			CheckStockinMissionBatchBean diffBatchBean = new CheckStockinMissionBatchBean();
//			diffBatchBean.setBuyCount(buyCount-realStockinCount);
//			diffBatchBean.setBuyStockinId(batchBean.getBuyStockinId());
//			diffBatchBean.setMissionId(batchBean.getMissionId());
//			diffBatchBean.setProductId(batchBean.getProductId());
//			diffBatchBean.setStatus(CheckStockinMissionBatchBean.STATUS0);
//			diffBatchBean.setStockinCount(buyCount-realStockinCount);
//			diffBatchBean.setSupplierId(batchBean.getSupplierId());
//			diffBatchBean.setSupplierName(batchBean.getSupplierName());
//			diffBatchBean.setStockinDatetime(DateUtil.getNow());
//			statService.addCheckStockinMissionBatch(diffBatchBean);
//			flag = true;
//		}
		//更新批次任务状态为处理中
		statService.updateCheckStockinMissionBatch(
				"stockin_count="+realStockinCount+",status="+CheckStockinMissionBatchBean.STATUS1, "id="+batchId);
		
		//更新任务为处理中
		statService.updateCheckStockinMission(
				"status=" + CheckStockinMissionBean.STATUS1, "id=" + batchBean.getMissionId());
		
		//是否产生差异记录
//		if(!flag){
//			//查询是否还存在未处理批次任务
//			int count = statService.getCheckStockinMissionBatchCount(
//					"mission_id="+batchBean.getMissionId()+" and status="+CheckStockinMissionBatchBean.STATUS0);
//			//更新预计到货单的状态为采购已完成
//			if(count == 0){
//				stockService.updateBuyStock("status=" + BuyStockBean.STATUS6, "id="+batchBean.getBuyStockinId());
//			}
//		}
		
	}


	
	
	public String getMissionIdsByProductLine(String sParentId1,
			String sParentId2, String supplyId, String beginCompleteTime, String endCompleteTime, String productCode) {
		String result = "";
		ResultSet rs = null;
		DbOperation dbOp = this.dbOp;
		
		StringBuilder parentIdCondition = new StringBuilder();
		
		if(sParentId1.length() > 0 ) {
			parentIdCondition.append(" and p.parent_id1 in (" + sParentId1 +")");
		} 
		if(sParentId2.length() > 0 ) {
			parentIdCondition.append(" and p.parent_id2 in (" + sParentId2 + ")");
		}
		
		if((productCode != null && !productCode.equals(""))){
			parentIdCondition.append(" and p.code='" + productCode + "'");
		}
		
		if(beginCompleteTime != null && !beginCompleteTime.equals("")){
			parentIdCondition.append(" and mbatch.complete_date_time between '" + beginCompleteTime + 
					"' and '" + endCompleteTime +"'");
		}
		
		if(supplyId != null && !supplyId.equals("")
				&& !supplyId.equals("0")){
			parentIdCondition.append(" and mbatch.supplier_id='" + supplyId + "'");
		}
		
		if(parentIdCondition.length() == 0) {
			return result;
		} 
		String sql ="select distinct(mbatch.mission_id) from check_stockin_mission_batch mbatch, " +
					"product p where mbatch.product_id = p.id " + parentIdCondition;
		
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				result += rs.getInt("mission_id") + ",";
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	
	
	public List getMissionIdsByCondition(String condition, int index,
			int countPerPage, String orderBy) throws Exception {
		
		ResultSet rs = null;
		DbOperation dbop = this.dbOp;
		List missionId = new ArrayList();
		try{
			//构建查询语句
	        String query = "select id from check_stockin_mission";
	        if (condition != null) {
	            query += " where " + condition;
	        }
	        if (orderBy != null) {
	            query += " order by " + orderBy;
	        }
	        query = DbOperation.getPagingQuery(query, index, countPerPage);
	        rs = dbop.executeQuery(query);
	        while(rs.next()){
	        	missionId.add(Integer.valueOf(rs.getInt("id")));
	        }
	        return missionId;
		}catch(Exception e){
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			if(rs != null){
				rs.close();
			}
		}
	}

	
	/**
	 * 确认完成任务信息
	 * @param user 
	 * @param parseInt
	 * @param parseInt2
	 */
	public void confirmComCheckStockin(int missionId, CheckStockinMissionBatchBean batchBean, voUser user) {
		
		
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		
		CheckStockinMissionBean csmb = statService.getCheckStockinMission("id="+missionId);
		String createTime = csmb.getCreateDatetime();
		String completeTime = DateUtil.getNow();
		Calendar startDay = Calendar.getInstance();
		Calendar endDay = Calendar.getInstance();
		startDay.setTime(DateUtil.parseDate(createTime,"yyyy-MM-dd HH:mm:ss"));
		endDay.setTime(DateUtil.parseDate(completeTime,"yyyy-MM-dd HH:mm:ss"));
		Double a = Double.valueOf(Arith.div(endDay.getTimeInMillis()- startDay.getTimeInMillis(), 1000*60*60, 2));
		DecimalFormat df = new DecimalFormat("0.00");
		if(!statService.updateCheckStockinMission(
				"real_consumtime="+df.format(a)
				+",status="+CheckStockinMissionBean.STATUS3
				+",complete_date_time='"+completeTime+"'", "id="+csmb.getId())){
			throw new RuntimeException("更新质检任务状态失败！");
		}
		
		List<CheckStockinMissionDetailBean> csmdBeanList = 
			statService.getCheckStockinMissionDetailList("mission_id="+csmb.getId(), -1, -1, null);
		StringBuilder stockinIdBuilder = new StringBuilder();
		for(CheckStockinMissionDetailBean csmd : csmdBeanList){
			if(stockinIdBuilder.length()>0){
				stockinIdBuilder.append(",");
			}
			stockinIdBuilder.append(csmd.getBuyStockinId());
		}
		StringBuilder condition = new StringBuilder();
		condition.append("status!="+BuyStockinBean.STATUS8);
		condition.append(" and id in (");
		condition.append(stockinIdBuilder.toString());
		condition.append(")");
		List<BuyStockinBean> buyStockinList = stockService.getBuyStockinList(condition.toString(), -1, -1, null);
		BuyStockinProductBean bsp = null;
		int allCount = 0;
		for(BuyStockinBean bs : buyStockinList){
			bsp = stockService.getBuyStockinProduct("buy_stockin_id="+bs.getId());
			if(bsp!=null){
				allCount += bsp.getStockInCount();
			}
		}
		
		if(!statService.updateCheckStockinMissionBatch(
				"check_count="+allCount+", qualified_count="+(allCount-batchBean.getCheckCount()), "id="+batchBean.getId())){
			throw new RuntimeException("更新质检数量和合格量失败！");
		}
		
		if(!statService.updateCheckStockinMissionBatch(
				"stockin_count=" + allCount 
				+ ",status=" + CheckStockinMissionBatchBean.STATUS3
				+ ",complete_date_time='" + completeTime +"'", "id=" + batchBean.getId())){
			throw new RuntimeException("更新实际到货数量失败");
		}
	}

	/**
	 * 入库明细
	 * @param strMissionId
	 * @return
	 */
	public Map queryPackingDetailInfo(String strMissionId) {
		
		if(strMissionId == null){
			throw new RuntimeException("missionId cant be null");
		}
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		CartonningInfoService cartService = ServiceFactory.createCartonningInfoService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		StringBuilder condition = new StringBuilder();
		condition.append("mission_id=");
		condition.append(Integer.parseInt(strMissionId));
		List packingDetailList = statService.getCheckStockinMissionDetailList(
				condition.toString(), 0, -1, null);
		Map resultMap = new HashMap();
		if(packingDetailList == null || packingDetailList.isEmpty()){
			return resultMap;
		}
		CheckStockinMissionDetailBean cmcBean = null;
		BuyStockinBean stockinBean = null;
		List cartonningList = null;
		String cartonningName = null;
		for(int i=0; i<packingDetailList.size(); i++){
			List tempList = new ArrayList();
			cmcBean = (CheckStockinMissionDetailBean) packingDetailList.get(i);
			if(cmcBean.getBuyStockinCreateDateTime()!=null && !cmcBean.getBuyStockinCreateDateTime().equals("")){
				cmcBean.setBuyStockinCreateDateTime(cmcBean.getBuyStockinCreateDateTime().substring(0,19));
			}
			stockinBean = stockService.getBuyStockin("id="+cmcBean.getBuyStockinId() + " and status<>" + BuyStockinBean.STATUS8);
			if(stockinBean==null){
				continue;
			}
			cartonningList = cartService.getCartonningList("buy_stockin_id="+stockinBean.getId(), -1, -1, null);
			cartonningName = getCartonningName(cartonningList);
			cmcBean.setCartonningName(cartonningName);
			cmcBean.setBuyStockinStatus(stockinBean.getStatusName());
			if(resultMap.isEmpty()){
				tempList.add(cmcBean);
				resultMap.put(cmcBean.getProductCode(), tempList);
			}else{
				if(resultMap.get(cmcBean.getProductCode()) != null){
					((List)resultMap.get(cmcBean.getProductCode())).add(cmcBean);
				}else{
					tempList.add(cmcBean);
					resultMap.put(cmcBean.getProductCode(), tempList);
				}
			}
		}
		return resultMap;
	}

	
	
	    /**  
	     * 此方法描述的是：  构造装箱单号，以逗号分割
	     * @author: liubo  
	     * @version: 2013-1-24 下午04:19:01  
	     */  
	    
	private String getCartonningName(List cartonningList) {
		
		if(cartonningList == null || cartonningList.isEmpty()){
			return "";
		}
		CartonningInfoBean coBean = null;
		StringBuilder strBuilder = new StringBuilder();
		for(int i=0; i<cartonningList.size(); i++){
			coBean = (CartonningInfoBean) cartonningList.get(i);
			if(strBuilder.length()>0){
				strBuilder.append(",");
			}
			strBuilder.append(coBean.getCode());
		}
		return strBuilder.toString();
	}

	public String addCheckStockinMission(HttpServletRequest request,String planBillNum, voUser user,
			String productCode, String flag) {
		
		WareService wareService = new WareService(this.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, this.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		
		try {
			this.getDbOp().getConn().setAutoCommit(true);
			//判断日单数量是否超过5位
			String nowDate = DateUtil.getNowDateStr();
			String beginDate = nowDate + " 00:00:00";
			String endDate = nowDate + " 23:59:59";
			List csmbList = statService.getCheckStockinMissionList(
					"create_date_time between '"+beginDate+"' and '"+endDate+"' and code like 'ZJ%'", 0, -1, "create_date_time desc");
			if(csmbList != null && !csmbList.isEmpty()){
				CheckStockinMissionBean csmb = (CheckStockinMissionBean) csmbList.get(0);
				int number = Integer.parseInt(csmb.getCode().substring(csmb.getCode().length()-5));
				if(number>=99999){
					return "生成任务失败！今日已生成任务超过99999！";
				}
			}
			
			
			
			//判断商品是否存在
			voProduct product = this.getProductByCode(productCode);
			if(product == null){
				return "该商品不存在！";
			}
			
			//判断预计单是否存在
			BuyStockBean buyStock = stockService.getBuyStock("code='" + planBillNum +"'");
			if(buyStock == null){
				return "该预计到货单不存在！";
			}
			
			if( !CargoDeptAreaService.hasCargoDeptArea(request, buyStock.getArea(), ProductStockBean.STOCKTYPE_CHECK)) {
				return "没有操作预计单到货地区待验库的权限！";
			}
			
			//判断预计到货单是否已经确认
			if(buyStock.getStatus()!=BuyStockBean.STATUS2
					&& buyStock.getStatus()!=BuyStockBean.STATUS3
					&& buyStock.getStatus()!=BuyStockBean.STATUS5){
				return "该预计单状态为"+buyStock.getStatusName()+"，生成任务失败！";
			}
			
			//判断采购订单是否已经完成
			BuyOrderBean buyOrderBean = stockService.getBuyOrder("id="+buyStock.getBuyOrderId());
			if(buyOrderBean.getStatus()!=BuyOrderBean.STATUS3 
					&& buyOrderBean.getStatus()!=BuyOrderBean.STATUS5
					&& buyOrderBean.getStatus()!=BuyOrderBean.STATUS7){
				return "该预计单对应的采购订单状态为"+buyOrderBean.getStatusName()+"，生成任务失败！";
			}
			
			//判断商品是否属于预计单
			int count = stockService.getBuyStockProductCount("product_id="+product.getId()+" and buy_stock_id="+buyStock.getId());
			if(count<=0){
				return "产品（编号）不属于该预计单！";
			}
			
			
			//判断是否存在该sku该预计单对应的未完成任务
			//低效率 	zhaolin
			List cssbList = statService.getCheckStockinMissionBatchList("buy_stockin_id="+buyStock.getId()+" and product_id="+product.getId() + " and status!=4", 0, -1, null);
			if(cssbList != null && !cssbList.isEmpty()){
				CheckStockinMissionBatchBean ccsb = null;
				for(int i=0; i<cssbList.size(); i++){
					ccsb = (CheckStockinMissionBatchBean) cssbList.get(i);
					if(ccsb.getStatus() != CheckStockinMissionBatchBean.STATUS3){
						return "此预计单的该sku存在未完成的任务，生成任务失败！";
					}
				}
				if(flag == null || !flag.equals("1")){
					return "checkStockinTip";
				}
			}
			//判断sku标准装箱量
			String productId = statService.getProductIdbyProductCode(productCode);
			if(productId==null||"".equals(productId))
				 productId = statService.getProductIdByProductBarcode(productCode);
			if(productId!=null&&!"".equals(productId)) {
				int binning = statService.valBinning(productId);
				if(binning==-1){
					return "该sku无商品物流属性!";
				}
				if(binning==0){
					return "请输入该sku的标准装箱量!";
				}
			} 
			
			
			
			this.getDbOp().startTransaction();
			String result = addCheckStockinMission(buyStock,user,product);
			//生成质检入库任务单
			if(result != null){
				if(this.getDbOp() != null){
					this.getDbOp().rollbackTransaction();
				}
				return result;
			}
			
			
			this.getDbOp().commitTransaction();
			return null;
		}catch(Exception e){
			boolean auto = false;
			try{
				auto = this.getDbOp().getConn().getAutoCommit();
			}catch(Exception e1){
				if(logger.isErrorEnabled()){
					logger.error("method addCheckStockinMission exception",e);
				}
				throw new RuntimeException("回滚失败，请联系管理员！");
			}
			if(this.getDbOp() != null && !auto){
				this.getDbOp().rollbackTransaction();
			}
			throw new RuntimeException(e);
		}finally {
			this.releaseAll();
		}
		
	}
	
	public Object addCheckStockinMission2(HttpServletRequest request,String planBillNum, voUser user,
			String productCode) {
		WareService wareService = new WareService(this.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, this.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		Object result = null;
		try {
			this.getDbOp().getConn().setAutoCommit(true);
			//判断日单数量是否超过5位
			String nowDate = DateUtil.getNowDateStr();
			String beginDate = nowDate + " 00:00:00";
			String endDate = nowDate + " 23:59:59";
			List csmbList = statService.getCheckStockinMissionList(
					"create_date_time between '"+beginDate+"' and '"+endDate+"' and code like 'ZJ%'", 0, -1, "create_date_time desc");
			if(csmbList != null && !csmbList.isEmpty()){
				CheckStockinMissionBean csmb = (CheckStockinMissionBean) csmbList.get(0);
				int number = Integer.parseInt(csmb.getCode().substring(csmb.getCode().length()-5));
				if(number>=99999){
					return "生成任务失败！今日已生成任务超过99999！";
				}
			}
			
			
			
			//判断商品是否存在
			voProduct product = this.getProductByCode(productCode);
			if(product == null){
				return "该商品不存在！";
			}
			
			//判断预计单是否存在
			BuyStockBean buyStock = stockService.getBuyStock("code='" + planBillNum +"'");
			if(buyStock == null){
				return "该预计到货单不存在！";
			}
			
			if( !CargoDeptAreaService.hasCargoDeptArea(request, buyStock.getArea(), ProductStockBean.STOCKTYPE_CHECK)) {
				return "没有操作预计单到货地区待验库的权限！";
			}
			
			//判断预计到货单是否已经确认
			if(buyStock.getStatus()!=BuyStockBean.STATUS2
					&& buyStock.getStatus()!=BuyStockBean.STATUS3
					&& buyStock.getStatus()!=BuyStockBean.STATUS5){
				return "该预计单状态为"+buyStock.getStatusName()+"，生成任务失败！";
			}
			
			//判断采购订单是否已经完成
			BuyOrderBean buyOrderBean = stockService.getBuyOrder("id="+buyStock.getBuyOrderId());
			if(buyOrderBean.getStatus()!=BuyOrderBean.STATUS3 
					&& buyOrderBean.getStatus()!=BuyOrderBean.STATUS5
					&& buyOrderBean.getStatus()!=BuyOrderBean.STATUS7){
				return "该预计单对应的采购订单状态为"+buyOrderBean.getStatusName()+"，生成任务失败！";
			}
			
			//判断商品是否属于预计单
			int count = stockService.getBuyStockProductCount("product_id="+product.getId()+" and buy_stock_id="+buyStock.getId());
			if(count<=0){
				return "产品（编号）不属于该预计单！";
			}
			
			
			//判断是否存在该sku该预计单对应的未完成任务
			//低效率 	zhaolin
			List cssbList = statService.getCheckStockinMissionBatchList("buy_stockin_id="+buyStock.getId()+" and product_id="+product.getId() + " and status!=4", 0, -1, null);
			if(cssbList != null && !cssbList.isEmpty()){
				CheckStockinMissionBatchBean ccsb = null;
				for(int i=0; i<cssbList.size(); i++){
					ccsb = (CheckStockinMissionBatchBean) cssbList.get(i);
					if(ccsb.getStatus() != CheckStockinMissionBatchBean.STATUS3){
						return "此预计单的该sku存在未完成的任务，生成任务失败！";
					}
				}
			}
			//判断sku标准装箱量
			String productId = statService.getProductIdbyProductCode(productCode);
			if(productId==null||"".equals(productId))
				 productId = statService.getProductIdByProductBarcode(productCode);
			if(productId!=null&&!"".equals(productId)) {
				int binning = statService.valBinning(productId);
				if(binning==-1){
					return "该sku无商品物流属性!";
				}
				if(binning==0){
					return "请输入该sku的标准装箱量!";
				}
			} 
			
			
			
			this.getDbOp().startTransaction();
			result = addCheckStockinMission2(buyStock,user,product);
			//生成质检入库任务单
			if(result instanceof  String ){
				if(this.getDbOp() != null){
					this.getDbOp().rollbackTransaction();
				}
				return result;
			}
			this.getDbOp().commitTransaction();
		}catch(Exception e){
			boolean auto = false;
			try{
				auto = this.getDbOp().getConn().getAutoCommit();
			}catch(Exception e1){
				if(logger.isErrorEnabled()){
					logger.error("method addCheckStockinMission exception",e);
				}
				throw new RuntimeException("回滚失败，请联系管理员！");
			}
			if(this.getDbOp() != null && !auto){
				this.getDbOp().rollbackTransaction();
			}
		}
		return result;
	}

	public void executeEffect(DbOperation dbOp,int areaId) {
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, dbOp);
		String today = DateUtil.getNowDateStr();
		String beforeday = DateUtil.getBackFromDate(today, 30);
		dbOp.commitTransaction();
		DbOperation dbOpSlave = new DbOperation();
		dbOpSlave.init("adult_slave");
		StatService statServiceSlave = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, dbOpSlave);
		List checkStockinMissionList = statServiceSlave.getCheckStockinMissionList("status!="+CheckStockinMissionBean.STATUS4 + 
				" and left(create_date_time,10) between '" + beforeday + "' and '" + today +"' and ware_area=" + areaId, 0, -1, "prior_status asc, create_date_time asc");
		dbOpSlave.release();
//		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		CheckStockinMissionBean csmBean = null;
		CheckStockinMissionBatchBean csmbBean = null;
		String dateTime = DateUtil.getNowDateStr();
		String beginTime = dateTime+" 00:00:00";
		String endTime = dateTime+" 23:59:59";
		//过滤已完成但不是今天的任务
		for(Iterator it = checkStockinMissionList.iterator(); it.hasNext();){
			csmBean = (CheckStockinMissionBean) it.next();
			if(csmBean.getStatus()==CheckStockinMissionBean.STATUS3 
					&& (csmBean.getCreateDatetime().compareTo(beginTime)>0 
					|| csmBean.getCreateDatetime().compareTo(endTime)<0)
					&&(csmBean.getCompleteDatetime().compareTo(beginTime)<0
							||csmBean.getCompleteDatetime().compareTo(endTime)>0)){
					it.remove();	
			}
		}
		double allTime = 0d;
		Double a = null;
		for(int i=0; i<checkStockinMissionList.size(); i++){
			csmBean = (CheckStockinMissionBean) checkStockinMissionList.get(i);
			int staffNum = this.getStaffNum(areaId);//获取日在编人数
			List checkStockinMissionBatchList = statService.getCheckStockinMissionBatchList("mission_id="+csmBean.getId(), 0, -1, null);
			for(int j=0;j<checkStockinMissionBatchList.size();j++){
				csmbBean = (CheckStockinMissionBatchBean) checkStockinMissionBatchList.get(j);
				a = Double.valueOf(Arith.div(csmbBean.getBuyCount(), csmBean.getCheckEffect(), 2));
				allTime=Arith.add(allTime, a.doubleValue());
				if(staffNum*8>=allTime){
					if(csmBean.getProductLoad()==null || csmBean.getProductLoad().equals("")){
						if(!statService.updateCheckStockinMission("product_load="+CheckStockinMissionBean.INLOAD, "id="+csmBean.getId())){
							throw new RuntimeException("任务"+csmBean.getCode()+"，计算产能失败！");
						}
					}else if(!csmBean.getProductLoad().equals(CheckStockinMissionBean.INLOAD)){
						if(!statService.updateCheckStockinMission("product_load="+CheckStockinMissionBean.INLOAD, "id="+csmBean.getId())){
							throw new RuntimeException("任务"+csmBean.getCode()+"，计算产能失败！");
						}
					}
				}else{
					if(csmBean.getProductLoad()==null || csmBean.getProductLoad().equals("")){
						if(!statService.updateCheckStockinMission("product_load="+CheckStockinMissionBean.OUTLOAD, "id="+csmBean.getId())){
							throw new RuntimeException("任务"+csmBean.getCode()+"，计算产能失败！");
						}
					}else if(!csmBean.getProductLoad().equals(CheckStockinMissionBean.OUTLOAD)){
						if(!statService.updateCheckStockinMission("product_load="+CheckStockinMissionBean.OUTLOAD, "id="+csmBean.getId())){
							throw new RuntimeException("任务"+csmBean.getCode()+"，计算产能失败！");
						}
					}
				}
			}
		}
	}

	public int getStaffNum(int areaId) {
		
		String querySql = "select day_count from check_staff_count ce where ce.date='"+DateUtil.getNowDateStr()+"' and area_id=" + areaId;
		ResultSet rs = null;
		int staffNum = -1;
		try{
			rs = this.getDbOp().executeQuery(querySql);
			while(rs.next()){
				staffNum = rs.getInt("day_count");
			}
		}catch(Exception e){
			throw new RuntimeException();
		}
		return staffNum;
	}

	//获取sku对应的质检效能
	public int getCheckEffect(int productId) {
		String querySql = "select effect from check_effect ce,product_ware_property pwp where pwp.check_effect_id=ce.id and pwp.product_id="+productId;
		ResultSet rs = null;
		int effect = -1;
		try{
			rs = this.getDbOp().executeQuery(querySql);
			while(rs.next()){
				effect = rs.getInt("effect");
			}
		}catch(Exception e){
			throw new RuntimeException();
		}
		return effect;
	}

	
	
	public String modifyMissionPriority(Map missionMap) {
		
		if(missionMap == null || missionMap.isEmpty()){
			return "没有需要修改的任务！";
		}
		
		Integer missionId = null;
		String priority = null;
		String sql = null;
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, dbOp);
		try{
			this.getDbOp().startTransaction();
			for(Iterator it = missionMap.keySet().iterator(); it.hasNext();){
				missionId = (Integer) it.next();
				priority = (String) missionMap.get(missionId);
				sql = "update check_stockin_mission set prior_status="+StringUtil.toSql(priority)+" where id="+missionId;
				this.getDbOp().executeUpdate(sql);
				CheckStockinMissionBean csmBean = statService.getCheckStockinMission("id=" + missionId);
				BuyStockBean buyStock = stockService.getBuyStock("code='" + csmBean.getBuyStockinCode() +"'");
				this.executeEffect(this.getDbOp(),buyStock.getArea());
			}
			
			
			this.getDbOp().commitTransaction();
			return null;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method modifyMissionPriority exception", e);
			}
			return "编辑优先级失败！";
		}finally{
			this.releaseAll();
		}
	}

	
	/**
	 * 导出质检任务
	 * @param checkStockinMissionList
	 * @return
	 */
	public HSSFWorkbook exportMission(List checkStockinMissionList) {
		
		HSSFWorkbook workbook = new HSSFWorkbook();
		// 在Excel 工作簿中建一工作表
		HSSFSheet sheet = workbook.createSheet("质检入库任务表");

		sheet.setColumnWidth(0, 15 * 256);
		sheet.setColumnWidth(1, 15 * 256);
		sheet.setColumnWidth(2, 15 * 256);
		sheet.setColumnWidth(3, 15 * 256);
		sheet.setColumnWidth(4, 15 * 256);
		sheet.setColumnWidth(5, 15 * 256);
		sheet.setColumnWidth(6, 15 * 256);
		sheet.setColumnWidth(7, 15 * 256);
		sheet.setColumnWidth(8, 15 * 256);
		sheet.setColumnWidth(9, 15 * 256);
		sheet.setColumnWidth(10, 15 * 256);
		sheet.setColumnWidth(11, 15 * 256);
		sheet.setColumnWidth(12, 15 * 256);
		sheet.setColumnWidth(13, 15 * 256);
		sheet.setColumnWidth(14, 15 * 256);
		sheet.setColumnWidth(15, 15 * 256);
		sheet.setColumnWidth(16, 15 * 256);
		
		Map columnMap = new HashMap();
		columnMap.put(Integer.valueOf(0), "序号");
		columnMap.put(Integer.valueOf(1), "任务单号");
		columnMap.put(Integer.valueOf(2), "预计单号");
		columnMap.put(Integer.valueOf(3), "产品编号");
		columnMap.put(Integer.valueOf(4), "供应商名称");
		columnMap.put(Integer.valueOf(5), "商品线");
		columnMap.put(Integer.valueOf(6), "质检效率（件/小时）");
		columnMap.put(Integer.valueOf(7), "合格量");
		columnMap.put(Integer.valueOf(8), "生成时间");
		columnMap.put(Integer.valueOf(9), "生产人");
		columnMap.put(Integer.valueOf(10), "状态");
		columnMap.put(Integer.valueOf(11), "库地区");
		columnMap.put(Integer.valueOf(12), "优先程度");
		columnMap.put(Integer.valueOf(13), "产能负荷");
		columnMap.put(Integer.valueOf(14), "完成时间");
		columnMap.put(Integer.valueOf(15), "实际耗时（小时）");
		columnMap.put(Integer.valueOf(16), "暂存号");
		// 设置单元格格式(文本)
		HSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));

		// 在索引0的位置创建行（第一行）
		HSSFRow row = sheet.createRow((short) 0);
		HSSFCell cell = null;
		for(int i=0; i<17; i++){
			cell = row.createCell(i);
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			cell.setCellValue((String)columnMap.get(Integer.valueOf(i)));
		}
		
		CheckStockinMissionBean csmbean = null;
		int sequence = 1;
		for(int i=0; i<checkStockinMissionList.size(); i++){
			row = sheet.createRow((short) i + 1);
			csmbean = (CheckStockinMissionBean) checkStockinMissionList.get(i);
			// 1 序号
			HSSFCell cellc1 = row.createCell(0);
			cellc1.setCellStyle(cellStyle);
			cellc1.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc1.setCellValue(sequence++ +"");
			// 2 任务单号
			HSSFCell cellc2 = row.createCell(1);
			cellc2.setCellStyle(cellStyle);
			cellc2.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc2.setCellValue(csmbean.getCode());
			// 3 预计单号
			HSSFCell cellc3 = row.createCell(2);
			cellc3.setCellStyle(cellStyle);
			cellc3.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc3.setCellValue(csmbean.getBuyStockinCode());
			// 4 产品编号
			HSSFCell cellc4 = row.createCell(3);
			cellc4.setCellStyle(cellStyle);
			cellc4.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc4.setCellValue(csmbean.getCsmBean().getProduct().getCode());
			// 5供应商名称
			HSSFCell cellc5 = row.createCell(4);
			cellc5.setCellStyle(cellStyle);
			cellc5.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc5.setCellValue(csmbean.getCsmBean().getSupplierName());
			
			// 6产品线
			HSSFCell cellc6 = row.createCell(5);
			cellc6.setCellStyle(cellStyle);
			cellc6.setCellType(HSSFCell.CELL_TYPE_STRING);
			cellc6.setCellValue(csmbean.getCsmBean().getProduct().getProductLineName());
			
			// 7 质检效率
			HSSFCell cellc7 = row.createCell(6);
			cellc7.setCellStyle(cellStyle);
			cellc7.setCellType(HSSFCell.CELL_TYPE_STRING);
			cellc7.setCellValue(csmbean.getCheckEffect());
			
			// 8合格量
			HSSFCell cellc8 = row.createCell(7);
			cellc8.setCellStyle(cellStyle);
			cellc8.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc8.setCellValue(csmbean.getCsmBean().getQualifiedCount());
			
			// 9生成时间
			HSSFCell cellc9 = row.createCell(8);
			cellc9.setCellStyle(cellStyle);
			cellc9.setCellType(HSSFCell.CELL_TYPE_STRING);
			String createTime = csmbean.getCreateDatetime();
			if(createTime != null && !createTime.equals("")){
				cellc9.setCellValue(createTime.substring(0,19));
			}
			
			// 10生成人
			HSSFCell cellc10 = row.createCell(9);
			cellc10.setCellStyle(cellStyle);
			cellc10.setCellType(HSSFCell.CELL_TYPE_STRING);
			cellc10.setCellValue(csmbean.getCreateOperName());
			
			// 11状态
			HSSFCell cellc11 = row.createCell(10);
			cellc11.setCellStyle(cellStyle);
			cellc11.setCellType(HSSFCell.CELL_TYPE_STRING);
			cellc11.setCellValue(csmbean.getStatusName());
			
			// 12库地区
			HSSFCell cellc12 = row.createCell(11);
			cellc12.setCellStyle(cellStyle);
			cellc12.setCellType(HSSFCell.CELL_TYPE_STRING);
			cellc12.setCellValue(csmbean.getWareAreaName());
			
			// 13优先程度
			HSSFCell cellc13 = row.createCell(12);
			cellc13.setCellStyle(cellStyle);
			cellc13.setCellType(HSSFCell.CELL_TYPE_STRING);
			cellc13.setCellValue(csmbean.getPriorStatusName());
			
			// 14产能负荷
			HSSFCell cellc14 = row.createCell(13);
			cellc14.setCellStyle(cellStyle);
			cellc14.setCellType(HSSFCell.CELL_TYPE_STRING);
			cellc14.setCellValue((String)CheckStockinMissionBean.productLoadMap.get(csmbean.getProductLoad()));
			
			// 15完成时间
			HSSFCell cellc15 = row.createCell(14);
			cellc15.setCellStyle(cellStyle);
			cellc15.setCellType(HSSFCell.CELL_TYPE_STRING);
			String completeTime = csmbean.getCompleteDatetime();
			if(completeTime != null && !completeTime.equals("")){
				cellc15.setCellValue(completeTime.substring(0,19));
			}
			
			// 16实际耗时
			HSSFCell cellc16= row.createCell(15);
			cellc16.setCellStyle(cellStyle);
			cellc16.setCellType(HSSFCell.CELL_TYPE_STRING);
			cellc16.setCellValue(csmbean.getRealConsumTime());
			
			// 16暂存号
			HSSFCell cellc17= row.createCell(16);
			cellc17.setCellStyle(cellStyle);
			cellc17.setCellType(HSSFCell.CELL_TYPE_STRING);
			cellc17.setCellValue(csmbean.getCsmBean().getTempNum());
		}
		return workbook;
	}

	
	/**
	 * 删除质检
	 * @param parstInt
	 * @return
	 */
	public void deleteMission(int missionId) {
		
		try{
			String upMission = "update check_stockin_mission set status="+CheckStockinMissionBean.STATUS4+" where id="+missionId;
			String upMissionDetail = "update check_stockin_mission_batch set status="+CheckStockinMissionBatchBean.STATUS4+" where mission_id="+missionId;
			this.getDbOp().startTransaction();
			if(!this.getDbOp().executeUpdate(upMission)){
				this.getDbOp().rollbackTransaction();
				throw new RuntimeException("this.getDbOp().executeUpdate(upMission)");
			}
			if(!this.getDbOp().executeUpdate(upMissionDetail)){
				this.getDbOp().rollbackTransaction();
				throw new RuntimeException("this.getDbOp().executeUpdate(upMissionDetail)");
			}
			this.getDbOp().commitTransaction();
		}catch(Exception e){
			this.getDbOp().rollbackTransaction();
			throw new RuntimeException(e);
		}
		
	}

	
	
	/**
	 * 质检合格入库
	 * @param request 
	 * @param user
	 * @param missionId
	 * @param batchId
	 * @param buyStockCode
	 * @param checkNum
	 * @param productCode
	 * @return
	 */
	public Object appraisalQualityInput(HttpServletRequest request, voUser user, String missionId,
			String batchId, String buyStockCode, int checkNum,
			String productCode) {
		WareService wareService = new WareService();
		StatService statService = new StatService(BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		CheckStockinMissionBean csmb = null;
		CheckStockinMissionBatchBean csmbb = null;
		try{
			
			voProduct product = this.getProductByCode(productCode);
			if(product == null){
				return "扫描的商品不存在！";
			}
			
			if(product.getIsPackage()==1){
				return"入库单中包含有套装产品，不能入库！";
			}
			
			csmb = statService.getCheckStockinMission("id = " + missionId );
			
			if(csmb == null || csmb.getStatus()==CheckStockinMissionBean.STATUS4){
				return "质检任务不存在，或者已经删除！";
			}
			
			if(csmb.getStatus()!=CheckStockinMissionBean.STATUS2 
					&& csmb.getStatus()!=CheckStockinMissionBean.STATUS1){
				return "不能进行质检入库，该任务状态为："+CheckStockinMissionBean.getStatusName(csmb.getStatus())+"！";
			}
			
			BuyStockBean stock = service.getBuyStock("code='" + buyStockCode +"'");
			if(stock == null){    
				return "没有这个预计到货表";
			}
			if( !CargoDeptAreaService.hasCargoDeptArea(request,stock.getArea(), ProductStockBean.STOCKTYPE_CHECK)) {
				return "没有操作预计单到货地区待验库的权限！";
			}
			//判断预计到货单是否已经确认
			if(stock.getStatus()!=BuyStockBean.STATUS2
					&& stock.getStatus()!=BuyStockBean.STATUS3
					&& stock.getStatus()!=BuyStockBean.STATUS5){
				return "该预计单状态为"+stock.getStatusName()+"，无法质检！";
			}
			
			//判断采购订单是否已经完成
			BuyOrderBean buyOrderBean = service.getBuyOrder("id="+stock.getBuyOrderId());
			if(buyOrderBean.getStatus()!=BuyOrderBean.STATUS3 
					&& buyOrderBean.getStatus()!=BuyOrderBean.STATUS5
					&& buyOrderBean.getStatus()!=BuyOrderBean.STATUS7){
				return "该预计单对应的采购订单状态为"+buyOrderBean.getStatusName()+"，无法质检！";
			}
			
			stock.setBuyOrder(buyOrderBean);
			wareService.getDbOp().startTransaction();
			
			if (!service.updateBuyStock("transform_count = transform_count + 1" , "id=" + stock.getId())) {
				wareService.getDbOp().rollbackTransaction();
				return "更新转换采购入库单次数失败！";
			}
			
			String queryCheckStockinMissionBatch = "mission_id = " + csmb.getId() + " and product_id = " 
						+ product.getId() + " and status in ("+ CheckStockinMissionBatchBean.STATUS1 +", "
						+ CheckStockinMissionBatchBean.STATUS2 +")";
			int unfinishCount = statService.getCheckStockinMissionBatchCount(queryCheckStockinMissionBatch);
			if(unfinishCount <= 0 ) {
				return "该任务单下没有这个产品！";
			}
			
			List tempBatchList = statService.getCheckStockinMissionBatchList(queryCheckStockinMissionBatch, -1, -1, "stockin_date_time asc");
			
			csmbb = (CheckStockinMissionBatchBean)tempBatchList.get(0);
			
			if(!statService.updateCheckStockinMission(
					"status="+CheckStockinMissionBean.STATUS2, "id="+csmbb.getMissionId())){
				wareService.getDbOp().rollbackTransaction();
				return "更新质检任务状态失败！";
			}
			
			List checkBatchBeanList = statService.getCheckStockinMissionBatchList("mission_id="+csmbb.getMissionId(), 0, -1, null);
			CheckStockinMissionBatchBean temBatchBean = null;
			for(int i=0; i<checkBatchBeanList.size(); i++){
				temBatchBean = (CheckStockinMissionBatchBean) checkBatchBeanList.get(i);
				if(!statService.updateCheckStockinMissionBatch(
						"status="+CheckStockinMissionBatchBean.STATUS2 , "id="+temBatchBean.getId())){
					wareService.getDbOp().rollbackTransaction();
					return "更新质检任务详细信息状态失败！";
				}
			}

			
			//添加采购入库单，状态为入库处理中
			BuyStockinBean	stockin = addBuyStockin(service, stock, user, wareService);
			if(stockin == null){
				return "添加采购入库单失败！";
			}
			stockin.setCreateUserName(user.getUsername());
			stockin.setBuyStock(stock);
			
			
			
			//添加采购入库单log记录
			BuyAdminHistoryBean log = new BuyAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(stockin.getId());
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("质检生成采购入库单，来源预计到货单号：" + stock.getCode());
			log.setType(BuyAdminHistoryBean.TYPE_ADD);
			if(!service.addBuyAdminHistory(log)){
				wareService.getDbOp().rollbackTransaction();
				return "生成采购入库单失败，添加操作记录失败！";
			}
			
			log = new BuyAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(stockin.getId());
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("确认采购入库单");
			log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
			if(!service.addBuyAdminHistory(log)){
				wareService.getDbOp().rollbackTransaction();
				return "添加确认采购入库单日志失败";
			}
				
			
			//添加对应的商品
			ProductStockBean psb = psService.getProductStock(
					"product_id=" + product.getId() + " and type=" + stockin.getStockType() 
					+ " and area=" + stockin.getStockArea());
			if(psb == null){
				wareService.getDbOp().rollbackTransaction();
				return "待验库没有商品:"+product.getCode()+"！";
			}
			BuyStockinProductBean bsip = addBuyStocinProduct(
					service, product, stockin, psb, wareService, checkNum);
			if(bsip == null){
				return "添加采购入库单商品失败！";
			}

			//采购入库明细
			CheckStockinMissionDetailBean csmdBean = new CheckStockinMissionDetailBean();
			csmdBean.setBuyStockinCode(stockin.getCode());
			csmdBean.setBuyStockinCount(bsip.getStockInCount());//该字段作废
			csmdBean.setBuyStockinCreateDateTime(DateUtil.getNow());
			csmdBean.setBuyStockinId(stockin.getId());
			csmdBean.setMissionId(Integer.parseInt(missionId));
			csmdBean.setProductCode(product.getCode());
			csmdBean.setProductId(product.getId());
			if(!statService.addCheckStockinMissionDetail(csmdBean)){
				wareService.getDbOp().rollbackTransaction();
				return "添加采购入库单明细失败！";
			}
			if (product != null) {
//					productList.add(product);
				product.setPsList(
						psService.getProductStockList("product_id = "+product.getId(), -1, -1, null));
				bsip.setProduct(product);
				
				//产品线名称
				voProductLine pl = null;
				voProductLineCatalog plc = null;
				List list2 = wareService.getProductLineListCatalog("catalog_id = "+product.getParentId1());
				if(list2.size()>0){
					plc = (voProductLineCatalog)list2.get(0);
					pl = (voProductLine)wareService.getProductLineList("product_line.id = "+plc.getProduct_line_id()).get(0);
					bsip.setProductLineName(pl.getName());
				}
				if(StringUtil.convertNull(bsip.getProductLineName()).equals("")){
					list2 = wareService.getProductLineListCatalog("catalog_id = "+product.getParentId2());
					if(list2.size()>0){
						plc = (voProductLineCatalog)list2.get(0);
						pl = (voProductLine)wareService.getProductLineList("product_line.id = "+plc.getProduct_line_id()).get(0);
						bsip.setProductLineName(pl.getName());
					}
				}
				if(StringUtil.convertNull(bsip.getProductLineName()).equals("")){
					bsip.setProductLineName("无");
				}
			}

			stockin.getBuyStockinProductList().add(bsip);
			
			//添加采购入库单商品log记录
			log = new BuyAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			log.setLogId(stockin.getId());
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("添加采购入库单商品["+product.getCode()+"]");
			log.setType(BuyAdminHistoryBean.TYPE_ADD);
			if(!service.addBuyAdminHistory(log)){
				wareService.getDbOp().rollbackTransaction();
				return "添加操作日志失败！";
			}
			wareService.getDbOp().commitTransaction();
		    return stockin;
		}catch(Exception e){
			e.printStackTrace();
			wareService.getDbOp().rollbackTransaction();
			return "系统异常，请联系管理员";
		}finally{
			if(wareService != null){
				wareService.releaseAll();
			}
		}
	}
	

	private BuyStockinProductBean addBuyStocinProduct(IStockService service,
			voProduct product, BuyStockinBean stockin, ProductStockBean psb, WareService wareService, int checkNum) {
		BuyStockProductBean bsp = service.getBuyStockProduct("product_id="+product.getId()+" and buy_stock_id="+stockin.getBuyStockId());
		BuyStockinProductBean bsip = new BuyStockinProductBean();
		
		int bsipId = service.getNumber("id", "buy_stockin_product", "max", "id > 0") + 1;
		bsip.setCreateDatetime(DateUtil.getNow());
		bsip.setConfirmDatetime(DateUtil.getNow());
		bsip.setId(bsipId);
		bsip.setBuyStockinId(stockin.getId());
		bsip.setStockInId(psb.getId());
		bsip.setProductCode(product.getCode());
		bsip.setProductId(product.getId());
		bsip.setRemark("");
		bsip.setStatus(BuyStockinProductBean.BUYSTOCKIN_UNDEAL);
		bsip.setPrice3(bsp.getPurchasePrice());
		bsip.setProductProxyId(bsp.getProductProxyId());
		bsip.setOriname(product.getOriname());
		bsip.setStockInCount(checkNum);
		bsip.setRemark("操作前库存" + psb.getStock()
				+ ",操作后库存" + (psb.getStock() + bsip.getStockInCount()));
		if(!service.addBuyStockinProduct(bsip)){
			wareService.getDbOp().rollbackTransaction();
			return null;
		}
		return bsip;
	}

	private BuyStockinBean addBuyStockin(IStockService service, BuyStockBean stock, voUser user, WareService wareService) {
		
		String code = service.generateBuyStockinCodeBref();
		BuyStockinBean	stockin = new BuyStockinBean();
		stockin.setBuyStockId(stock.getId());
		stockin.setCreateDatetime(DateUtil.getNow());
		stockin.setConfirmDatetime(DateUtil.getNow());
		stockin.setStatus(BuyStockinBean.STATUS3);
		stockin.setCode(code);
		//stockin.setId(service.getNumber("id", "buy_stockin", "max", "id > 0")+1);
		if(stock.getArea() == 0){
			stockin.setStockArea(ProductStockBean.AREA_BJ);
		}else if(stock.getArea()==1){
			stockin.setStockArea(ProductStockBean.AREA_GF);
		}else if(stock.getArea()==3){
			stockin.setStockArea(ProductStockBean.AREA_ZC);
		} else if( stock.getArea() == 4 ) {
			stockin.setStockArea(ProductStockBean.AREA_WX);
		}
		stockin.setAuditingUserId(user.getId());
		stockin.setStockType(ProductStockBean.STOCKTYPE_CHECK);
		stockin.setRemark("");
		stockin.setCreateUserId(user.getId());
		stockin.setSupplierId(stock.getSupplierId());//供应商id
		stockin.setBuyOrderId(stock.getBuyOrderId());
		if(!service.addBuyStockin(stockin)){
			wareService.getDbOp().rollbackTransaction();
			return null;
		}
		if( !service.fixBuyStockinCode(code, service.getDbOp(), stockin)) {
			wareService.getDbOp().rollbackTransaction();
			return null;
		}
		return stockin;
	}
	

	
	/**
	 * 
	     * 此方法描述的是：  根据预计单id判断是否可以自动完成订单
	     * @author: liubo  
	     * @version: 2013-1-23 下午04:10:10
	 */
	public String judgeComOrderByStockinId(int buyStockinId) {
		
		try{
			this.getDbOp().getConn().setAutoCommit(true);
			IStockService service = ServiceFactory.createStockService(
							IBaseService.CONN_IN_SERVICE, this.getDbOp());
			BuyStockinBean bean = service.getBuyStockin("id = " + buyStockinId);//采购入库单
			if (bean == null) {
				return "没有这个采购入库单";
			}
			if (bean.getStatus() == BuyStockinBean.STATUS4 || bean.getStatus() == BuyStockinBean.STATUS6) {
				return "该操作已经完成，不能再更改！";
			}
			BuyStockBean stock = service.getBuyStock("id="+bean.getBuyStockId());//预计到货单
			
			String condition = "buy_stockin_id = " + bean.getId() + " and status = " + BuyStockinProductBean.BUYSTOCKIN_UNDEAL;
			List shList = service.getBuyStockinProductList(condition, 0, -1, "id");//采购入库单下的商品列表
			if(shList==null || shList.isEmpty()){
				return "没有需要执行的数据";
			}
			BuyStockinProductBean bsip = null;
			List bopList = null;
			BuyOrderProductBean bopBean = null;
			boolean check = true;
			bopList = service.getBuyOrderProductList("buy_order_id="+stock.getBuyOrderId(), -1, -1, null);//预计到货单相关的采购订单下商品列表
			for(Iterator it = bopList.listIterator(); it.hasNext();){//循环采购订单商品列表
				bopBean = (BuyOrderProductBean)it.next();//采购订单商品
				int stockinCount = 0;
				for(Iterator itr = shList.iterator(); itr.hasNext();){//循环采购入库单商品列表，找出订单中商品和入库单商品id相同的记录
					bsip = (BuyStockinProductBean) itr.next();//采购入库单商品
					if(bopBean.getProductId()==bsip.getProductId()){
						stockinCount = bopBean.getStockinCountBJ()+bopBean.getStockinCountGD()+bsip.getStockInCount();
						break;
					}else{
						stockinCount = bopBean.getStockinCountBJ()+bopBean.getStockinCountGD();
					}
				}
				if(stockinCount<bopBean.getOrderCountBJ()+bopBean.getOrderCountGD()){
					check = false;
					break;
				}
				
			}
			if(check){
				return "0";
			}
			return "1";
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			this.releaseAll();
		}
	}
		/**  
	     * 此方法描述的是： 判断订单，预计单是否可以自动关完成 
	     * @author: liubo  
	     * @version: 2013-1-23 下午01:34:37  
	     */  
	    
	public String judgeAutoCompleteOrder(String buyStockCode,
			String appraisalNumber, String productCode) {
		
		try{
			WareService wareService = new WareService(this.getDbOp());
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
			voProduct product = this.getProductByCode(productCode);
			if(product == null){
				return "商品不存在！";
			}
			BuyStockBean stock = service.getBuyStock("code='" + buyStockCode +"'");
			if(stock == null){    
				return "没有这个预计到货表";
			}
			
			//判断预计到货单是否已经确认
			if(stock.getStatus()!=BuyStockBean.STATUS2
					&& stock.getStatus()!=BuyStockBean.STATUS3
					&& stock.getStatus()!=BuyStockBean.STATUS5){
				return "该预计单状态为"+stock.getStatusName()+"，无法质检！";
			}
			
			//判断采购订单是否已经完成
			BuyOrderBean buyOrderBean = service.getBuyOrder("id="+stock.getBuyOrderId());
			if(buyOrderBean.getStatus()!=BuyOrderBean.STATUS3 
					&& buyOrderBean.getStatus()!=BuyOrderBean.STATUS5
					&& buyOrderBean.getStatus()!=BuyOrderBean.STATUS7){
				return "该预计单对应的采购订单状态为"+buyOrderBean.getStatusName()+"，无法质检！";
			}
			
			ArrayList bopList = service.getBuyOrderProductList("buy_order_id="+stock.getBuyOrderId(), -1, -1, null);
			Iterator bopIterator = bopList.listIterator();
			boolean check = true;
			int checkCount = 0;
			while(bopIterator.hasNext()){
				BuyOrderProductBean bopBean = (BuyOrderProductBean)bopIterator.next();
				if(bopBean.getProductId()==product.getId()){
					checkCount = bopBean.getStockinCountBJ()+bopBean.getStockinCountGD()+Integer.parseInt(appraisalNumber);
				}else{
					checkCount = bopBean.getStockinCountBJ()+bopBean.getStockinCountGD();
				}
				if(checkCount<(bopBean.getOrderCountBJ()+bopBean.getOrderCountGD())){
					check = false;
					break;
				}
			}
			if(check){
				return "0";
			}
			return "1";
		}finally{
			this.releaseAll();
		}
	}
	
	public voProduct getProductByCode(String productCode) {
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		WareService wareService = new WareService(this.getDbOp());
		ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+productCode+"'");
		voProduct product = null;
		if( bBean == null || bBean.getBarcode() == null ) {
			product = wareService.getProduct(productCode);
		} else {
			product = wareService.getProduct(bBean.getProductId());
		}
		return product;
	}

	/**
	 * 获得入库单装箱单上架单 关联的关系
	 * @param sql
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List<BuyStockinUpshelfBean> getBuyStockinUpshelfList2(String sql,
			int index, int count, String orderBy) {
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
				BuyStockinUpshelfBean bsuBean = new BuyStockinUpshelfBean();
				bsuBean.setId(rs.getInt("id"));
				bsuBean.setBuyStockinId(rs.getInt("buy_stockin_id"));
				bsuBean.setBuyStockinDatetime(rs.getString("buy_stockin_datetime"));
				bsuBean.setCargoOperationId(rs.getInt("cargo_operation_id"));
				bsuBean.setCartonningInfoId(rs.getInt("cartonning_info_id"));
				bsuBean.setCartonningInfoName(rs.getString("cartonning_info_name"));
				bsuBean.setProductId(rs.getInt("product_id"));
				bsuBean.setProductCode(rs.getString("product_code"));
				bsuBean.setWareArea(rs.getInt("ware_area"));
				result.add(bsuBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}
	
	public int getBuyStockinUpshelfCount2(String sqlCount) {
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

	public int findUserByUsername(String username) {
		voUser user = (voUser) this.getXXX(" username = '"+username+"'", "admin_user", "adultadmin.action.vo.voUser");
		if(user!=null){
			return user.getId();
		}
		return -1;
	}

	/**
	 * 搜索一个List 是未初录的质检任务的列表
	 * @param buyStockCode
	 * @param productCode
	 * @return
	 */
	public List<CheckStockinMissionBatchBean> getNotBeginCheckMisssions(
			String buyStockCode, int productId) {
		List<CheckStockinMissionBatchBean> result = new ArrayList<CheckStockinMissionBatchBean>();
		String sql = "select csm.*, csmb.* from check_stockin_mission csm, check_stockin_mission_batch csmb where csmb.mission_id = csm.id and csm.status in (0,1,2) and csmb.first_check_status = 0 and csm.buy_stockin_code='"+buyStockCode+"' and csmb.product_id=" + productId;
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				CheckStockinMissionBean bean = new CheckStockinMissionBean();
				bean.setId(rs.getInt("csm.id"));
				bean.setCreateDatetime(rs.getString("csm.create_date_time"));
				bean.setCode(rs.getString("csm.code"));
				bean.setStatus(rs.getInt("csm.status"));
				bean.setPriorStatus(rs.getInt("csm.prior_status"));
				bean.setCreateOperId(rs.getInt("csm.create_oper_id"));
				bean.setCreateOperName(rs.getString("csm.create_oper_name"));
				bean.setBuyStockinCode(rs.getString("csm.buy_stockin_code"));
				CheckStockinMissionBatchBean batchBean = new CheckStockinMissionBatchBean();
				batchBean.setId(rs.getInt("csmb.id"));
				batchBean.setFirstCheckCount(rs.getInt("csmb.first_check_count"));
				batchBean.setFirstCheckStatus(rs.getInt("csmb.first_check_status"));
				batchBean.setSecondCheckCount(rs.getInt("csmb.second_check_count"));
				batchBean.setSecondCheckStatus(rs.getInt("csmb.second_check_status"));
				batchBean.setMissionId(rs.getInt("csmb.mission_id"));
				batchBean.setCheckStockinMission(bean);
				result.add(batchBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		
		return result;
	}
	
	/**
	 * 寻找已经初录但是还未复录的 未完成的质检入库任务
	 * 修改成为不限制
	 * @param code
	 * @param id
	 * @return
	 */
	public List<CheckStockinMissionBatchBean> getAlreadyBeginCheckMisssions(
			String buyStockCode, int productId) {
		List<CheckStockinMissionBatchBean> result = new ArrayList<CheckStockinMissionBatchBean>();
		String sql = "select csm.*, csmb.* from check_stockin_mission csm, check_stockin_mission_batch csmb where csmb.mission_id = csm.id and csm.status in (0,1,2) and csm.buy_stockin_code='"+buyStockCode+"' and csmb.product_id=" + productId;
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				CheckStockinMissionBean bean = new CheckStockinMissionBean();
				bean.setId(rs.getInt("csm.id"));
				bean.setCreateDatetime(rs.getString("csm.create_date_time"));
				bean.setCode(rs.getString("csm.code"));
				bean.setStatus(rs.getInt("csm.status"));
				bean.setPriorStatus(rs.getInt("csm.prior_status"));
				bean.setCreateOperId(rs.getInt("csm.create_oper_id"));
				bean.setCreateOperName(rs.getString("csm.create_oper_name"));
				bean.setBuyStockinCode(rs.getString("csm.buy_stockin_code"));
				CheckStockinMissionBatchBean batchBean = new CheckStockinMissionBatchBean();
				batchBean.setId(rs.getInt("csmb.id"));
				batchBean.setFirstCheckCount(rs.getInt("csmb.first_check_count"));
				batchBean.setFirstCheckStatus(rs.getInt("csmb.first_check_status"));
				batchBean.setSecondCheckCount(rs.getInt("csmb.second_check_count"));
				batchBean.setSecondCheckStatus(rs.getInt("csmb.second_check_status"));
				batchBean.setMissionId(rs.getInt("csmb.mission_id"));
				batchBean.setCheckStockinMission(bean);
				result.add(batchBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		
		return result;
	}
}





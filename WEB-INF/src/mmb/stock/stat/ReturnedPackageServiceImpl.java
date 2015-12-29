package mmb.stock.stat;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import mmb.finance.stat.FinanceProductBean;
import mmb.finance.stat.FinanceReportFormsService;
import mmb.finance.stat.FinanceSellBean;
import mmb.finance.stat.FinanceSellProductBean;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.util.LogUtil;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;
import com.mmb.components.service.FinanceSaleBaseDataService;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInfoModelBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationLogBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.cargo.CargoStaffPerformanceBean;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.impl.CargoServiceImpl;
import adultadmin.service.impl.ProductStockServiceImpl;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.Arith;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class ReturnedPackageServiceImpl extends BaseServiceImpl implements
		ReturnedPackageService {

	private final Log logger = LogFactory.getLog("stock.Log");
	
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	
	//查询退货汇总单下商品信息
	private final static String QUERYRETSHELFCOUNT = "select coc.product_id, coc.stock_count from cargo_operation_cargo coc, cargo_operation co where co.id=coc.oper_id and coc.type=0 and co.source in(";

	
	public ReturnedPackageServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public ReturnedPackageServiceImpl() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	
	public boolean checkCollectBill(HttpServletRequest request, String billCode) throws Exception {
		ICargoService cargoService = ServiceFactory.createCargoService(
							BaseServiceImpl.CONN_IN_SERVICE, this.getDbOp());
		CargoDeptAreaService daService = new CargoDeptAreaService(BaseServiceImpl.CONN_IN_SERVICE, this.getDbOp());
		
		List<CargoOperationBean> coBeanList = cargoService.getCargoOperationList(
												"source='"+StringUtil.toSql(billCode)+"'", -1, -1, null);
		if(coBeanList != null && !coBeanList.isEmpty()){
			StringBuilder strBuilder = new StringBuilder();
			for(CargoOperationBean coBean : coBeanList){
				if(strBuilder.length()>0){
					strBuilder.append(",");
				}
				strBuilder.append(coBean.getId());
			}
			List<CargoOperationCargoBean> cocBeanList = cargoService.getCargoOperationCargoList(
					"oper_id in("+strBuilder.toString()+")"+" and type=0", -1, -1, null);
			CargoInfoBean ci = null;
			CargoInfoAreaBean ciaBean = null;
			if(cocBeanList != null && !cocBeanList.isEmpty()){
				for(CargoOperationCargoBean cocBean : cocBeanList){
					ci = cargoService.getCargoInfo("whole_code='"+cocBean.getInCargoWholeCode()+"'");
					ciaBean = cargoService.getCargoInfoArea("id="+ci.getAreaId());
					if(ci != null){
						if(!daService.hasCargoDeptArea(request, ciaBean.getOldId(), ci.getStockType())){
							return false;
						}
					}
					ci = cargoService.getCargoInfo("whole_code='"+cocBean.getOutCargoWholeCode()+"'");
					ciaBean = cargoService.getCargoInfoArea("id="+ci.getAreaId());
					if(ci != null){
						if(!daService.hasCargoDeptArea(request, ciaBean.getOldId(), ci.getStockType())){
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	public boolean pdaCheckCollectBill(HttpServletRequest request, String billCode,int area) throws Exception {
		ICargoService cargoService = ServiceFactory.createCargoService(
							BaseServiceImpl.CONN_IN_SERVICE, this.getDbOp());
		CargoDeptAreaService daService = new CargoDeptAreaService(BaseServiceImpl.CONN_IN_SERVICE, this.getDbOp());
		
		List<CargoOperationBean> coBeanList = cargoService.getCargoOperationList(
												"source='"+StringUtil.toSql(billCode)+"'", -1, -1, null);
		if(coBeanList != null && !coBeanList.isEmpty()){
			StringBuilder strBuilder = new StringBuilder();
			for(CargoOperationBean coBean : coBeanList){
				if(strBuilder.length()>0){
					strBuilder.append(",");
				}
				strBuilder.append(coBean.getId());
			}
			List<CargoOperationCargoBean> cocBeanList = cargoService.getCargoOperationCargoList(
					"oper_id in("+strBuilder.toString()+")"+" and type=0", -1, -1, null);
			CargoInfoBean ci = null;
			if(cocBeanList != null && !cocBeanList.isEmpty()){
				for(CargoOperationCargoBean cocBean : cocBeanList){
					ci = cargoService.getCargoInfo("whole_code='"+cocBean.getInCargoWholeCode()+"'");
					if(ci != null){
						if(area != ci.getAreaId()){
							return false;
						}
					}
					ci = cargoService.getCargoInfo("whole_code='"+cocBean.getOutCargoWholeCode()+"'");
					if(ci != null){
						if(area != ci.getAreaId()){
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public String storagePackage(String type, String orderCode,
			String productCode, String packageCode, String exceptionPCode,
			String payFlag,voUser user, int wareArea) throws Exception {

		DbOperation dbop = null;
		DbOperation dbOpSlave = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSet rs0 = null;
		ResultSet rers=null;
		PreparedStatement reps=null;
		try {
			dbop = new DbOperation(DbOperation.DB);
			dbOpSlave = new DbOperation();
			dbOpSlave.init("adult_slave");
			synchronized(Constants.LOCK){

				
			if(productCode == null || productCode.equals("")){
				return Constants.UN_INPUTPRODUCT;
			}
			// 分割产品条码
			String[] productCodes = productCode.split("\r\n");

			// 创建服务对象
			WareService wareService = new WareService(dbop);
			IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, dbop);
			IStockService istockService = ServiceFactory.createStockService(
					IBaseService.CONN_IN_SERVICE, dbop);
			IStockService istockServiceSlave = ServiceFactory.createStockService(
					IBaseService.CONN_IN_SERVICE, dbOpSlave);
			IBarcodeCreateManagerService bService = 
				ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, dbop);
			IProductStockService productservice = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());

//			IProductStockService psService = ServiceFactory
//					.createProductStockService(IBaseService.CONN_IN_SERVICE,
//							dbop);
			StatService statService = new StatService(CONN_IN_SERVICE, dbop);

			//财务报表
		    FinanceReportFormsService frfService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, dbop);
		      
			String returnStr = "";

			// 开启事务
			dbop.startTransaction();

			// 判断订单编号是否存在,首先按照order_code查询，然后按照code查询
			OrderStockBean orderStockBean = istockService.getOrderStock("code='"+orderCode+"'" + " and status!="+OrderStockBean.STATUS4);
			voOrder vorder = null;
			if(orderStockBean != null){
				vorder = wareService.getOrder("code='"+orderStockBean.getOrderCode()+"'");
			}else{
				vorder = wareService.getOrder("code='"+orderCode+"'");
				if(vorder == null){
					return Constants.RET_ORDER_NOTEXIST;
				}
				orderStockBean = istockService.getOrderStock("order_id="+vorder.getId()+" and status!="+OrderStockBean.STATUS4);
			}
			
			if (vorder == null || orderStockBean == null) {
				return Constants.RET_ORDER_NOTEXIST;
			}
			
			if( vorder.getStatus() == 11 ) {
				return "该订单已退回不可以再操作！";
			}
			
			ReturnedPackageBean rpBean = statService.getReturnedPackage("order_code='" + vorder.getCode() + "'");
			if( rpBean == null ) {
				return "在待退货包裹列表中没有找到对应该订单的记录！";
			} else {
				if( rpBean.getArea() != wareArea) {
					return "所选地区和待退货地区不同！";
				}
				if( rpBean.getStatus() == ReturnedPackageBean.STATUS_HAS_RETURN) {
					return "这个退货包裹已经入库";
				}
			}

			// 统计订单中商品数量
//			OrderStockBean csb = istockService.getOrderStock("order_id="+vorder.getId()+" and status!=3");
			List voProductList = istockService.getOrderStockProductList("order_stock_id="+orderStockBean.getId(), 0, -1, null);
			Map productMap = new HashMap();
			OrderStockProductBean orderProduct = null;
			voProduct temProduct = null;
			for (int i = 0; i < voProductList.size(); i++) {
				orderProduct = (OrderStockProductBean) voProductList.get(i);
				temProduct = wareService.getProduct(orderProduct.getProductId());
				orderProduct.setProduct(temProduct);
				productMap.put(Integer.valueOf(orderProduct.getProductId()), Integer.valueOf(orderProduct.getStockoutCount()));
			}

			// 统计录入的商品数量，包含订单中所有商品
			voProduct product = null;
			ProductBarcodeVO bBean = null;
			Map enterProductMap = new HashMap();
			for (int i = 0; i < productCodes.length; i++) {
				bBean = bService.getProductBarcode("barcode="+"'"+productCodes[i].trim()+"'");
				if(bBean == null){
					product = wareService.getProduct(productCodes[i].trim());
				}else{
					product = wareService.getProduct(bBean.getProductId());
				}
				if (product == null) {
					return Constants.RET_PRO_NOTEXIST;
				}
				product.setPsList(
						productservice.getProductStockList("product_id="+ product.getId(), -1, -1, null));
				if (productMap.get(Integer.valueOf(product.getId())) == null) {
					return Constants.RET_PRO_ORDER_BELONG;
				}
				if (enterProductMap.get(Integer.valueOf(product.getId())) != null) {
					int temp = ((Integer) enterProductMap.get(Integer.valueOf(product.getId())))
							.intValue() + 1;
					enterProductMap.put(Integer.valueOf(product.getId()), Integer.valueOf(temp));
				} else {
					enterProductMap.put(Integer.valueOf(product.getId()), Integer.valueOf(1));
				}
			}

			// 正常入库
			if (type.equals(Constants.NORMAL_STORAGE)) {

				// 判断包裹单号是否存在
//				AuditPackageBean auditPackageBean = istockService
//						.getAuditPackage("package_code='" + packageCode + "'");
//				if (auditPackageBean == null) {
//					return Constants.RET_PAC_NOTEXIST;
//				}

				ReturnedPackageBean retPackBean = statService
						.getReturnedPackage("order_code='" + vorder.getCode() + "'");
				//判断订单是否已经正常入退货包裹列表，如果有那么不允许重新录入
				if (retPackBean != null && 
						(retPackBean.getStorageStatus() == 0 || retPackBean.getStorageStatus()==2)) {
					return Constants.RET_UNENTER;
				//判断订单是否已经正常入退货包裹列表，如果订单对应包裹缺失商品录入，那么允许再次录入缺失商品列表
				}else if(retPackBean != null && retPackBean.getStorageStatus()==1){
					
					String remark = retPackBean.getRemark();
					//将异常备注信息转换为商品编号和数量map
					Map expProductMap = constructExpMap(remark);
					String pCode = null;
					String count = null;
					int intCount = 0;
					Integer tpId = null;
					//确认扫描的缺失商品是否是异常商品，并判断是否多扫描了异常商品，并重新组装异常信息
					for(Iterator it = enterProductMap.keySet().iterator(); it.hasNext();){
						tpId = ((Integer)it.next());
						product = wareService.getProduct(tpId.intValue());
						pCode = product.getCode();
						if(expProductMap.get(pCode) != null 
								&& !expProductMap.get(pCode).equals("")){
							count = (String) expProductMap.get(pCode);
							intCount = Integer.valueOf(count.split(",")[0]).intValue();
							int moreCount = ((Integer)enterProductMap.get(tpId)).intValue()-intCount;
							if(moreCount>0){
								return "扫描的商品： "+pCode+"比缺失商品多："+ moreCount+"个！";
							}else if(moreCount < 0){
								expProductMap.put(pCode, Integer.valueOf(-moreCount) + "," + count.split(",")[1]);
							}else{
								expProductMap.remove(pCode);
							}
						}else{
							return "商品： "+pCode+"不是缺失商品！";
						}
					}
					StringBuilder strBuilder = new StringBuilder();
					
					for(Iterator it = expProductMap.keySet().iterator(); it.hasNext();){
						if (strBuilder.length() > 0) {
							strBuilder.append(";");
						}
						pCode = (String) it.next();
						strBuilder.append(pCode + ":" + expProductMap.get(pCode));
					}
					
					// 更新包裹异常备注信息
					if(!statService.updateReturnedPackage("remark='"
							+ strBuilder.toString() + "'", "order_code='"
							+ vorder.getCode() + "'")){
						throw new RuntimeException("更新包裹异常备注信息失败");
					}
					
					//如果异常信息不存在，那么更新包裹记录状态为正常入库
					if(strBuilder.length()==0){
						if(!statService.updateReturnedPackage(
								"storage_status="+Constants.NORMAL_STORAGE, "order_code='"+ vorder.getCode() + "'")){
							throw new RuntimeException("更新包裹状态失败");
						}
					}
					
					//更新退货库库存
					IProductStockService productStockService = new ProductStockServiceImpl(CONN_IN_SERVICE, dbop);
					ICargoService cargoService = new CargoServiceImpl(CONN_IN_SERVICE, dbop);
					Integer productId = null;
					OrderStockProductBean voproduct = null;
					CargoInfoAreaBean cargoArea = cargoService.getCargoInfoArea("old_id = "+wareArea);
					CargoInfoBean cargoInfo = cargoService.getCargoInfo(
							"type=0 and status=0 and area_id=" + cargoArea.getId() + " and stock_type=" + CargoInfoBean.STOCKTYPE_RETURN);
//					ReturnedProductBean productBean = null;
					for(Iterator it = enterProductMap.keySet().iterator(); it.hasNext();){
						productId = (Integer) it.next();
						voproduct =(OrderStockProductBean) istockService.getOrderStockProductList(
								"order_stock_id="+orderStockBean.getId()+" and product_id="+ productId, 0, -1, null).get(0);
						temProduct = wareService.getProduct(voproduct.getProductId());
						voproduct.setProduct(temProduct);
						voproduct.setStockoutCount((((Integer)enterProductMap.get(productId)).intValue()));
						updateStockCount(cargoService, productStockService, voproduct, cargoInfo,wareArea);
						//添加退货商品信息
//						productBean = new ReturnedProductBean();
//						productBean.setProductCode(voproduct.getProductCode());
//						productBean.setProductName(voproduct.getProduct().getOriname());
//						productBean.setProductId(voproduct.getProductId());
//						productBean.setCount(voproduct.getStockoutCount());
//						if(!statService.dealReturnedProduct(productBean)){
//							throw new RuntimeException("添加包裹信息失败");
//						}
						CargoInfoAreaBean tcib = cargoService.getCargoInfoArea("id="+cargoInfo.getAreaId());
						if(tcib == null){
							continue;
						}
						ProductStockBean ps = productservice.getProductStock("product_id=" + productId + " and area=" + wareArea + " and type=" + ProductStockBean.STOCKTYPE_RETURN);
						// 入库卡片
						StockCardBean sc = new StockCardBean();
						sc.setCardType(StockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
						sc.setCode(vorder.getCode());
						sc.setCreateDatetime(DateUtil.getNow());
						sc.setStockType(cargoInfo.getStockType());
						sc.setStockArea(wareArea);
						sc.setProductId(productId.intValue());
						sc.setStockId(ps.getId());
						sc.setStockInCount(voproduct.getStockoutCount());
						// sc.setStockInPriceSum(stockinPrice);
						sc.setStockInPriceSum((new BigDecimal(voproduct.getStockoutCount())).multiply(
										new BigDecimal(StringUtil.formatDouble2(product
												.getPrice5()))).doubleValue());
						sc.setCurrentStock(product.getStock(tcib.getOldId(),
								cargoInfo.getStockType()) + product.getLockCount(tcib.getOldId(),cargoInfo.getStockType()));
						sc.setStockAllArea(product.getStock(
								tcib.getOldId()) + product.getLockCount(tcib.getOldId()));
						sc.setStockAllType(product.getStockAllType(
								cargoInfo.getStockType()) + product.getLockCountAllType(cargoInfo.getStockType()));
						sc.setAllStock(product.getStockAll()
								+ product.getLockCountAll());
						sc.setStockPrice(product.getPrice5());
						sc.setAllStockPriceSum((new BigDecimal(
								sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());

						if (!productservice.addStockCard(sc)) {
							throw new RuntimeException("出库添加进销存失败！");
						}
						
						CargoProductStockBean cargoProductStock = cargoService.getCargoAndProductStock("cargo_id="+cargoInfo.getId() + " and product_id="+ productId.intValue());
						//货位入库卡片
						CargoStockCardBean csc = new CargoStockCardBean();
						csc.setCardType(CargoStockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
						csc.setCode(vorder.getCode());
						csc.setCreateDatetime(DateUtil.getNow());
						csc.setStockType(cargoInfo.getStockType());
						csc.setStockArea(wareArea);
						csc.setProductId(productId.intValue());
						csc.setStockId(cargoProductStock.getId());
						csc.setStockInCount(voproduct.getStockoutCount());
						csc.setStockInPriceSum((new BigDecimal(voproduct.getStockoutCount()))
								.multiply(
										new BigDecimal(StringUtil.formatDouble2(product
												.getPrice5()))).doubleValue());
						csc.setCurrentStock(product.getStock(sc.getStockArea(),
								sc.getStockType())
								+ product.getLockCount(sc.getStockArea(),
										sc.getStockType()));
						csc.setAllStock(product.getStockAll()
								+ product.getLockCountAll());
						csc.setCurrentCargoStock(cargoProductStock.getStockCount()
								+ cargoProductStock.getStockLockCount());
						csc.setCargoStoreType(cargoInfo.getStoreType());
						csc.setCargoWholeCode(cargoInfo.getWholeCode());
						csc.setStockPrice(product.getPrice5());
						csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
								.multiply(
										new BigDecimal(StringUtil.formatDouble2(sc
												.getStockPrice()))).doubleValue());
						if (!cargoService.addCargoStockCard(csc)) {
							throw new RuntimeException("出库进销存添加失败！");
						}
						
						
//						  	voOrder order = service.getOrder("code='" + orderCode + "'");
				            float price5 = 0;
				            voOrderProduct vorderProduct = wareService.getOrderProductSplit(vorder.getId(), product.getCode());
				            if(vorderProduct == null){
				            	vorderProduct = wareService.getOrderPresentSplit(vorder.getId(), product.getCode());
				            }
							
							int totalCount = product.getStockAll() + product.getLockCountAll();
							price5 = ((float)Math.round((product.getPrice5() * totalCount + (product.getPrice3() * voproduct.getStockoutCount())) / (totalCount + voproduct.getStockoutCount()) * 1000))/1000;
							if(!wareService.getDbOp().executeUpdate("update product set price5=" + price5 + " where id = " + product.getId())){
								throw new RuntimeException("更新产品"+product.getCode()+"库存价格失败");
							}
//					        ProductStockBean ps = productservice.getProductStock("product_id=" + voproduct.getProductId() + " and area=3 and type=" + ProductStockBean.STOCKTYPE_RETURN);
//							 List batchLogList = istockService.getStockBatchLogList("code='"+vorder.getCode()+"' and product_id="+voproduct.getProductId()+" and remark = '订单出货'", -1, -1, "id desc");
//				             if(batchLogList==null||batchLogList.size()==0){
//
//				             	String code = "X"+DateUtil.getNow().substring(0,10).replace("-", "");
//				             	StockBatchBean newBatch;
//				             	newBatch = istockService.getStockBatch("code like '" + code + "%' and product_id="+voproduct.getProductId());
//				             	int ticket = 0;
//				             	int _count = FinanceProductBean.queryCountIfTicket(dbop, voproduct.getProductId(), ticket);
//				             	if(newBatch == null){
//				             		//当日第一份批次记录，编号最后三位 001
//				             		code += "001";
//				             	}else {
//				             		//获取当日计划编号最大值.
//				             		newBatch = istockService.getStockBatch("code like '" + code + "%' and product_id="+voproduct.getProductId()+" order by id desc limit 1"); 
//				             		String _code = newBatch.getCode();
//				             		int number = Integer.parseInt(_code.substring(_code.length()-3));
//				             		number++;
//				             		code += String.format("%03d",new Object[]{new Integer(number)});
//				             	}
//				             	newBatch = new StockBatchBean();
//				             	newBatch.setCode(code);
//				             	newBatch.setProductId(voproduct.getProductId());
//				             	newBatch.setProductStockId(ps.getId());
//				             	newBatch.setStockArea(wareArea);
//				             	newBatch.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//				             	newBatch.setProductStockId(ps.getId());
//				             	newBatch.setCreateDateTime(istockService.getStockBatchCreateDatetime(code,voproduct.getProductId()));
//				             	newBatch.setPrice(product.getPrice5());
//				             	newBatch.setBatchCount(csc.getStockInCount());
//				             	newBatch.setTicket(ticket);
//
//				             	if(!istockService.addStockBatch(newBatch)){
//				             		throw new RuntimeException("添加库存批次失败！");
//				             	}
//
//				             	//添加批次操作记录
//				             	StockBatchLogBean batchLog = new StockBatchLogBean();
//				             	batchLog.setCode(vorder.getCode());
//				             	batchLog.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//				             	batchLog.setStockArea(wareArea);
//				             	batchLog.setBatchCode(newBatch.getCode());
//				             	batchLog.setBatchCount(voproduct.getStockoutCount());
//				             	batchLog.setBatchPrice(newBatch.getPrice());
//				             	batchLog.setProductId(newBatch.getProductId());
//				             	batchLog.setRemark("退货入库");
//				             	batchLog.setCreateDatetime(DateUtil.getNow());
//				             	batchLog.setUserId(user.getId());
//				             	if(!istockService.addStockBatchLog(batchLog)){
//				             		throw new RuntimeException("添加批次操作记录失败！");
//				             	}
//				             	
//				             	//stockinPrice = batchLog.getBatchCount()*batchLog.getBatchPrice();
//				             	
//				             	//财务产品信息表---liuruilan-----2012-11-01-----
//								FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + voproduct.getProductId());
//								if(fProduct == null){
//									throw new RuntimeException("查询财务数据异常！");
//								}
//								float priceSum = Arith.mul(price5, totalCount);
//								int stockinCount = voproduct.getStockoutCount();
//								
//								//计算公式：(结存总额 + (发货时批次价 * 本批退货数量)) / (库存总数量 + 本批退货数量)
//								float priceHasticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumHasticket(), Arith.mul(batchLog.getBatchPrice(), stockinCount)), Arith.add(_count, stockinCount)), 2);
//								float priceSumHasticket = Arith.mul(priceHasticket,  stockinCount + _count);
//								String set = "price =" + price5 + ", price_sum =" + priceSum + ", price_hasticket =" + priceHasticket + ", price_sum_hasticket =" + priceSumHasticket;
//								frfService.updateFinanceProductBean(set, "product_id = " + product.getId());
//								
//								//财务进销存卡片
//								int currentStock = FinanceStockCardBean.getCurrentStockCount(dbop, batchLog.getStockArea(), batchLog.getStockType(), ticket, voproduct.getProductId());
//								int stockAllType=FinanceStockCardBean.getCurrentStockCount(dbop, -1, batchLog.getStockType(), ticket, voproduct.getProductId());
//								int stockAllArea=FinanceStockCardBean.getCurrentStockCount(dbop, batchLog.getStockArea(), -1,ticket, voproduct.getProductId());
//								FinanceStockCardBean fsc = new FinanceStockCardBean();
//								fsc.setCardType(StockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
//								fsc.setCode(vorder.getCode());
//								fsc.setCreateDatetime(DateUtil.getNow());
//								fsc.setStockType(batchLog.getStockType());
//								fsc.setStockArea(batchLog.getStockArea());
//								fsc.setProductId(voproduct.getProductId());
//								fsc.setStockId(ps.getId());
//								fsc.setStockInCount(-batchLog.getBatchCount());	
//								fsc.setCurrentStock(currentStock);	//只记录分库总库存
//								fsc.setStockAllArea(stockAllArea);
//								fsc.setStockAllType(stockAllType);
//								fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//								fsc.setStockPrice(price5);
//								
//								fsc.setType(fsc.getCardType());
//								fsc.setIsTicket(ticket);
//								fsc.setStockBatchCode(batchLog.getBatchCode());
//								fsc.setBalanceModeStockCount(stockinCount + _count);
//								fsc.setStockInPriceSum(-Double.parseDouble(String.valueOf(Arith.mul(batchLog.getBatchPrice(), stockinCount))));
//								fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
//								double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
//								fsc.setAllStockPriceSum(tmpPrice);
//								frfService.addFinanceStockCardBean(fsc);
//								
//								//将订单商品写入销售商品信息表--商品
//								
//								
//								FinanceSellBean fsbean= frfService.getFinanceSellBean(" data_type=0 and order_id="+vorder.getId());
//		    					FinanceSellBean fsbean1=null;
//		    					List fspList=null;//销售订单里面所有产品
//		    					
//		    					if(fsbean!=null&&fsbean.getId()!=0){
//		    						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+vorder.getId());
//		    						
//		    						FinanceSellProductBean fsbean2=frfService.getFinanceSellProductBean(" (data_type=2 or data_type=3) and order_id="+vorder.getId());
//		    						//如果已经有了销售未妥投订单不做任务操作，
//		    						if((fsbean1!=null&&fsbean1.getId()!=0)||(fsbean2!=null&&fsbean2.getId()!=0)){
//		    							
//		    							//否则向finance_sell,finance_sell_product表添加
//		    						}else{
//		    							fsbean.setId(0);
//		    							fsbean.setCreateDatetime(DateUtil.getNow());
//		    							fsbean.setDataType(1);
//		    							fsbean.setCount(-1);
//		    							fsbean.setPrice(Arith.round(Arith.mul(fsbean.getPrice(),-1),2));
//		    							fsbean.setCarriage(-vorder.getPostage());
//		    							frfService.addFinanceSellBean(fsbean);//向finance_sell表添加未妥投退货记录
//		    							int financeSellId=frfService.getDbOp().getLastInsertId();//获得新添加的记录ID
//		    							fspList=frfService.getFinanceSellProductBeanList(" (data_type=1 or data_type=0) and order_id="+vorder.getId(), -1, -1, null);
//		    							if(fspList!=null&&fspList.size()>0){
//		    								FinanceSellProductBean fspbean=null;
//		    								for(int i=0;i<fspList.size();i++){
//		    									fspbean=(FinanceSellProductBean)fspList.get(i);
//		    									fspbean.setId(0);
//		    									fspbean.setBuyCount(-fspbean.getBuyCount());
//		    									fspbean.setFinanceSellId(financeSellId);
//		    									if(fspbean.getDataType()==0){
//		    										fspbean.setDataType(2);
//		    									}
//		    									if(fspbean.getDataType()==1){
//		    										fspbean.setDataType(3);
//		    									}
//		    									fspbean.setCreateDatetime(DateUtil.getNow());
//		    									frfService.addFinanceSellProductBean(fspbean);//向finance_sell_product表添加未妥投退货记录
//		    									
//		    								}
//		    							}
//		    						}
//		    					}else{
//		    						//如果finance_sell表中没有找到销售订单，要查看一下finance_sell表里面有没有未妥投退单，如果有则什么也没做
//		    						//如果没有则向否则向finance_sell,finance_sell_product表添加记录
//		    						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+vorder.getId());
//		    						FinanceSellProductBean fsbean2=frfService.getFinanceSellProductBean(" (data_type=2 or data_type=3) and order_id="+vorder.getId());
//		    						if((fsbean1!=null&&fsbean1.getId()!=0)||(fsbean2!=null&&fsbean2.getId()!=0)){
//		    							//否则向finance_sell,finance_sell_product表添加
//		    						}else{
//								
//		    							//将订单数据写入发货信息表
//										FinanceSellBean fsBean = new FinanceSellBean();
//										int deliverType = 0;
//										if(voOrder.deliverToBalanceTypeMap.get("" + vorder.getDeliver()) != null){
//											deliverType = Integer.parseInt(voOrder.deliverToBalanceTypeMap.get("" + vorder.getDeliver()).toString());
//										}
//										fsBean.setOrderId(vorder.getId());
//										fsBean.setCode(vorder.getCode());
//										fsBean.setPrice(-(vorder.getDprice()-vorder.getPostage()));	//退货单金额为负
//										fsBean.setCarriage(-vorder.getPostage());	
//										fsBean.setCharge(0);
//										fsBean.setBuyMode(vorder.getBuyMode());
//										fsBean.setPayMode(vorder.getBuyMode()); //备用字段，取值赞同buyMode
//										fsBean.setDeliverType(deliverType);
//										fsBean.setCreateDatetime(DateUtil.getNow());
//										fsBean.setPackageNum(vorder.getPackageNum());
//										fsBean.setDataType(1);	//1-销售退回
//										fsBean.setCount(-1);
//										frfService.addFinanceSellBean(fsBean);
//										int financeSellId=frfService.getDbOp().getLastInsertId();//货得刚添加记录的ID
//										
//										int supplierId = FinanceSellProductBean.querySupplier(dbop, batchLog.getBatchCode());
//										String sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
//													+ "FROM product p JOIN user_order_product_split_history h ON p.id = h.product_id " 
//													+ "WHERE h.order_id = ? ";
//										dbop.prepareStatement(sql);
//										pstmt = dbop.getPStmt();
//										pstmt.setInt(1, vorder.getId());
//										rs = pstmt.executeQuery();
//										while(rs.next()){
//											int pId = rs.getInt("id");
//											sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
//													"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
//											dbop.prepareStatement(sql);
//											pstmt = dbop.getPStmt();
//											pstmt.setInt(1,pId);
//											rs0 = pstmt.executeQuery();
//											
//											FinanceSellProductBean fspBean = new FinanceSellProductBean();
//											fspBean.setOrderId(vorder.getId());
//											fspBean.setProductId(rs.getInt("id"));
//											fspBean.setBuyCount(-rs.getInt("count"));
//											fspBean.setPrice(rs.getFloat("price"));
//											fspBean.setDprice(rs.getFloat("dprice"));
//											fspBean.setPrice5(rs.getFloat("price5"));
//											fspBean.setFinanceSellId(financeSellId);
//											if(rs0.next()){
//												fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
//											}
//											fspBean.setParentId1(rs.getInt("parent_id1"));
//											fspBean.setParentId2(rs.getInt("parent_id2"));
//											fspBean.setParentId3(rs.getInt("parent_id3"));
//											fspBean.setCreateDatetime(DateUtil.getNow());
//											fspBean.setDataType(2);	//2-商品未妥投退回
//											fspBean.setBalanceMode(ticket);
//											fspBean.setSupplierId(supplierId);
//											
//											frfService.addFinanceSellProductBean(fspBean);
//											
//										}
//								
//										//赠品
//										sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
//											+ "FROM product p JOIN user_order_present_split_history h ON p.id = h.product_id " 
//											+ "WHERE h.order_id = ? ";
//										dbop.prepareStatement(sql);
//										pstmt = dbop.getPStmt();
//										pstmt.setInt(1, vorder.getId());
//										rs = pstmt.executeQuery();
//										while(rs.next()){
//											int pId = rs.getInt("id");
//											sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
//													"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
//											dbop.prepareStatement(sql);
//											pstmt = dbop.getPStmt();
//											pstmt.setInt(1,pId);
//											rs0 = pstmt.executeQuery();
//											
//											FinanceSellProductBean fspBean = new FinanceSellProductBean();
//											fspBean.setOrderId(vorder.getId());
//											fspBean.setProductId(rs.getInt("id"));
//											fspBean.setBuyCount(-rs.getInt("count"));
//											fspBean.setPrice(rs.getFloat("price"));
//											fspBean.setDprice(rs.getFloat("dprice"));
//											fspBean.setFinanceSellId(financeSellId);
//											fspBean.setPrice5(rs.getFloat("price5"));
//											if(rs0.next()){
//												fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
//											}
//											fspBean.setParentId1(rs.getInt("parent_id1"));
//											fspBean.setParentId2(rs.getInt("parent_id2"));
//											fspBean.setParentId3(rs.getInt("parent_id3"));
//											fspBean.setCreateDatetime(DateUtil.getNow());
//											fspBean.setDataType(3);	//3-赠品未妥投退回
//											fspBean.setBalanceMode(ticket);
//											fspBean.setSupplierId(supplierId);
//											
//											frfService.addFinanceSellProductBean(fspBean);
//											
//										}
//										
//		    						}
//		    						
//		    					}
//		    					
//								//-------------liuruilan-------------
//				             }else{
//				             	Iterator batchIter = batchLogList.listIterator();
//				         		
//				             	while(batchIter.hasNext()&&voproduct.getStockoutCount()>0){
//				             		StockBatchLogBean batchLog = (StockBatchLogBean)batchIter.next(); 
//				             		StockBatchBean batch = istockService.getStockBatch("code = '"+batchLog.getBatchCode()+"' and product_id="+batchLog.getProductId()+" and stock_type="+ProductStockBean.STOCKTYPE_RETURN+" and stock_area=" + wareArea);
//				             		int ticket = FinanceSellProductBean.queryTicket(dbop, batchLog.getBatchCode());	//是否含票 
//				            		if(ticket == -1){
//				            			throw new RuntimeException("财务数据查询异常！");
//									}
//				            		int _count = FinanceProductBean.queryCountIfTicket(dbop, batchLog.getProductId(), ticket);
//				             		if(batch!=null){
//				             			if(voproduct.getStockoutCount()<=batchLog.getBatchCount()){
//				             				istockService.updateStockBatch("batch_count = batch_count+"+voproduct.getStockoutCount(), "id="+batch.getId());
////				             				batchCount = stockinCount;
////				             				stockinCount = 0;
//				             			}else{
//				             				istockService.updateStockBatch("batch_count = batch_count+"+batchLog.getBatchCount(), "id="+batch.getId());
////				             				stockinCount -= batchLog.getBatchCount();
////				             				batchCount = batchLog.getBatchCount();
//				             			}
//
//				             		}else{
//
//				             			StockBatchBean newBatch = new StockBatchBean();
//				             			newBatch.setCode(batchLog.getBatchCode());
//				             			newBatch.setProductId(voproduct.getProductId());
//				             			newBatch.setProductStockId(ps.getId());
//				             			newBatch.setStockArea(wareArea);
//				             			newBatch.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//				             			newBatch.setProductStockId(ps.getId());
//				             			newBatch.setCreateDateTime(istockService.getStockBatchCreateDatetime(batchLog.getBatchCode(),voproduct.getProductId()));
//				             			newBatch.setPrice(batchLog.getBatchPrice());
//				             			newBatch.setTicket(ticket);
//
//				             			if(voproduct.getStockoutCount()<=batchLog.getBatchCount()){
//				             				newBatch.setBatchCount(voproduct.getStockoutCount());
////				             				batchCount = stockinCount;
////				             				stockinCount = 0;
//				             			}else{
//				             				newBatch.setBatchCount(batchLog.getBatchCount());
////				             				stockinCount -= batchLog.getBatchCount();
////				             				batchCount = batchLog.getBatchCount();
//				             			}
//
//				             			if(!istockService.addStockBatch(newBatch)){
//				             				throw new RuntimeException("添加批次失败！");
//				             			}
//				             		}
//				             		
////				             		stockinPrice = stockinPrice + batchLog.getBatchPrice()*batchCount;
//
//				             		//添加批次操作记录
//				             		StockBatchLogBean newBatchLog = new StockBatchLogBean();
//				             		newBatchLog.setCode(vorder.getCode());
//				             		newBatchLog.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//				             		newBatchLog.setStockArea(wareArea);
//				             		newBatchLog.setBatchCode(batchLog.getBatchCode());
//				             		newBatchLog.setBatchCount(voproduct.getStockoutCount());
//				             		newBatchLog.setBatchPrice(batchLog.getBatchPrice());
//				             		newBatchLog.setProductId(batchLog.getProductId());
//				             		newBatchLog.setRemark("退货入库");
//				             		newBatchLog.setCreateDatetime(DateUtil.getNow());
//				             		newBatchLog.setUserId(user.getId());
//				             		if(!istockService.addStockBatchLog(newBatchLog)){
//				             			throw new RuntimeException("添加批次操作记录失败！");
//				             		}
//				             		
//				             		//财务产品信息表---liuruilan-----2012-11-01-----
//									FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + batchLog.getProductId());
//									if(fProduct == null){
//										throw new RuntimeException("财务数据查询异常！");
//									}
//									
//									float priceSum = Arith.mul(price5, totalCount);
//									float priceHasticket = 0;
//									float priceNoticket = 0;
//									float priceSumHasticket = 0;
//									float priceSumNoticket = 0;
//									int batchCount = voproduct.getStockoutCount();;
//									String set = "price =" + price5 + ", price_sum =" + priceSum;
//									float sqlPrice5=0;
//									if(ticket == 0){	//0-有票
//										//计算公式：(结存总额 + (发货时批次价 * 本批退货数量)) / (库存总数量 + 本批退货数量)
//										
//										String sqlPrice="select price5 from finance_sell_product where product_id="+batchLog.getProductId()+" and data_type=0 and balance_mode="+ticket+" and order_id="+vorder.getId();
//										reps=frfService.getDbOp().getConn().prepareStatement(sqlPrice);
//										rers=reps.executeQuery();
//										boolean flag=false;//为false,finance_sell_product 没有记录，为true,finance_sell_product有记录
//										while(rers.next()){
//											flag=true;
//											sqlPrice5=rers.getFloat(1);
//										}
//										if(!flag){
//											//如果finance_sell_productm 没有记录要在user_order_product_split_history里面找
//											sqlPrice="select price5 from user_order_product_split_history psplit where product_id="+batchLog.getProductId()+" and order_id="+vorder.getId();
//											reps=frfService.getDbOp().getConn().prepareStatement(sqlPrice);
//											rers=reps.executeQuery();
//											while(rers.next()){
//												flag=true;
//												sqlPrice5=rers.getFloat("price5");
//											}
//											if(!flag){//如果user_order_product_split_history 没有记录要在user_order_present_split_history里面找
//												sqlPrice="select price5 from user_order_present_split_history psplit where product_id="+batchLog.getProductId()+" and order_id="+vorder.getId();
//												reps=frfService.getDbOp().getConn().prepareStatement(sqlPrice);
//												rers=reps.executeQuery();
//												while(rers.next()){
//													flag=true;
//													sqlPrice5=rers.getFloat("price5");
//												}
//											}
//										}
//										if(reps!=null){
//											reps.close();
//										}
//										//-------------------------------获得出库价end --------------------------------------------
//										priceHasticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumHasticket(), Arith.mul(sqlPrice5, batchCount)), Arith.add(_count, batchCount)), 2);
//										priceSumHasticket = Arith.mul(priceHasticket,  batchCount + _count);
//										set += ", price_hasticket =" + priceHasticket + ", price_sum_hasticket =" + priceSumHasticket;
//									}
//									if(ticket == 1){	//1-无票
//										
//										String sqlPrice="select price5 from finance_sell_product where product_id="+batchLog.getProductId()+" and data_type=0 and balance_mode="+ticket+" and order_id="+vorder.getId();
//										reps=frfService.getDbOp().getConn().prepareStatement(sqlPrice);
//										rers=reps.executeQuery();
//										boolean flag=false;//为false,finance_sell_product 没有记录，为true,finance_sell_product有记录
//										while(rers.next()){
//											flag=true;
//											sqlPrice5=rers.getFloat(1);
//										}
//										if(reps!=null){
//											reps.close();
//										}
//										priceNoticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumNoticket(), Arith.mul(sqlPrice5, batchCount)), Arith.add(_count, batchCount)), 2);
//										priceSumNoticket = Arith.mul(priceNoticket,  batchCount + _count);
//										set += ", price_noticket =" + priceNoticket + ", price_sum_noticket =" + priceSumNoticket;
//									}
//									frfService.updateFinanceProductBean(set, "product_id = " + product.getId());
//									
//									//财务进销存卡片
//									int currentStock = FinanceStockCardBean.getCurrentStockCount(dbop, newBatchLog.getStockArea(), newBatchLog.getStockType(), ticket, batchLog.getProductId());
//									int stockAllType=FinanceStockCardBean.getCurrentStockCount(dbop, -1, newBatchLog.getStockType(), ticket,batchLog.getProductId());
//									int stockAllArea=FinanceStockCardBean.getCurrentStockCount(dbop, newBatchLog.getStockArea(), -1,ticket, batchLog.getProductId());
//									FinanceStockCardBean fsc = new FinanceStockCardBean();
//									fsc.setCardType(StockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
//									fsc.setCode(vorder.getCode());
//									fsc.setCreateDatetime(DateUtil.getNow());
//									fsc.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//									fsc.setStockArea(newBatchLog.getStockArea());
//									fsc.setProductId(batchLog.getProductId());
//									fsc.setStockId(ps.getId());
//									fsc.setStockInCount(-batchLog.getBatchCount());	
//									fsc.setCurrentStock(currentStock);	//只记录分库总库存
//									fsc.setStockAllArea(stockAllArea);
//									fsc.setStockAllType(stockAllType);
//									fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//									fsc.setStockPrice(price5);
//									
//									fsc.setType(fsc.getCardType());
//									fsc.setIsTicket(ticket);
//									fsc.setStockBatchCode(batchLog.getBatchCode());
//									fsc.setBalanceModeStockCount(batchCount + _count);
//									fsc.setStockInPriceSum(-Double.parseDouble(String.valueOf(Arith.mul(sqlPrice5, batchCount))));
//									if(ticket == 0){
//										fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
//									}
//									if(ticket == 1){
//										fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceNoticket)));
//									}
//									double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumNoticket(), fProduct.getPriceSumHasticket()))), fsc.getStockInPriceSum());
//									fsc.setAllStockPriceSum(tmpPrice);
//									frfService.addFinanceStockCardBean(fsc);
//									
//									//将订单商品写入销售商品信息表--商品
//									
//									
//			    					FinanceSellBean fsbean= frfService.getFinanceSellBean(" data_type=0 and order_id="+vorder.getId());
//			    					FinanceSellBean fsbean1=null;
//			    					List fspList=null;//销售订单里面所有产品
//			    					
//			    					if(fsbean!=null&&fsbean.getId()!=0){
//			    						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+vorder.getId());
//			    						FinanceSellProductBean fsbean2=frfService.getFinanceSellProductBean(" (data_type=2 or data_type=3) and order_id="+vorder.getId());
//			    						//如果已经有了销售未妥投订单不做任务操作，
//			    						if((fsbean1!=null&&fsbean1.getId()!=0)||(fsbean2!=null&&fsbean2.getId()!=0)){
//			    							
//			    							//否则向finance_sell,finance_sell_product表添加
//			    						}else{
//			    							fsbean.setId(0);
//			    							fsbean.setCreateDatetime(DateUtil.getNow());
//			    							fsbean.setDataType(1);
//			    							fsbean.setPrice(Arith.round(Arith.mul(fsbean.getPrice(),-1),2));
//			    							fsbean.setCount(-1);
//			    							fsbean.setCarriage(-vorder.getPostage());
//			    							
//			    							frfService.addFinanceSellBean(fsbean);//向finance_sell表添加未妥投退货记录
//			    							int  financeSellId=frfService.getDbOp().getLastInsertId();//获得刚添加的新记录
//			    							fspList=frfService.getFinanceSellProductBeanList(" (data_type=1 or data_type=0) and order_id="+vorder.getId(), -1, -1, null);
//			    							if(fspList!=null&&fspList.size()>0){
//			    								FinanceSellProductBean fspbean=null;
//			    								for(int i=0;i<fspList.size();i++){
//			    									fspbean=(FinanceSellProductBean)fspList.get(i);
//			    									fspbean.setId(0);
//			    									fspbean.setBuyCount(-fspbean.getBuyCount());
//			    									fspbean.setFinanceSellId(financeSellId);
//			    									if(fspbean.getDataType()==0){
//			    										fspbean.setDataType(2);
//			    									}
//			    									if(fspbean.getDataType()==1){
//			    										fspbean.setDataType(3);
//			    									}
//			    									fspbean.setCreateDatetime(DateUtil.getNow());
//			    									frfService.addFinanceSellProductBean(fspbean);//向finance_sell_product表添加未妥投退货记录
//			    									
//			    								}
//			    							}
//			    						}
//			    					}else{
//			    						//如果finance_sell表中没有找到销售订单，要查看一下finance_sell表里面有没有未妥投退单，如果有则什么也没做
//			    						//如果没有则向否则向finance_sell,finance_sell_product表添加记录
//			    						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+vorder.getId());
//			    						FinanceSellProductBean fsbean2=frfService.getFinanceSellProductBean(" (data_type=2 or data_type=3) and order_id="+vorder.getId());
//			    						if((fsbean1!=null&&fsbean1.getId()!=0)||(fsbean2!=null&&fsbean2.getId()!=0)){
//			    							
//			    							//否则向finance_sell,finance_sell_product表添加
//			    						}else{
//			    							//将订单数据写入发货信息表
//											FinanceSellBean fsBean = new FinanceSellBean();
//											int deliverType = 0;
//											if(voOrder.deliverToBalanceTypeMap.get("" + vorder.getDeliver()) != null){
//												deliverType = Integer.parseInt(voOrder.deliverToBalanceTypeMap.get("" + vorder.getDeliver()).toString());
//											}
//											fsBean.setOrderId(vorder.getId());
//											fsBean.setCode(vorder.getCode());
//											fsBean.setPrice(-(vorder.getDprice()-vorder.getPostage()));	//退货单金额为负，
//											fsBean.setCarriage(-vorder.getPostage());	
//											
//											fsBean.setCharge(0);
//											fsBean.setBuyMode(vorder.getBuyMode());
//											fsBean.setPayMode(vorder.getBuyMode()); //备用字段，取值赞同buyMode
//											fsBean.setDeliverType(deliverType);
//											fsBean.setCreateDatetime(DateUtil.getNow());
//											fsBean.setPackageNum(vorder.getPackageNum());
//											fsBean.setDataType(1);	//1-销售退回
//											fsBean.setCount(-1);
//											frfService.addFinanceSellBean(fsBean);
//											int financeSellId=frfService.getDbOp().getLastInsertId();
//									
//									
//											int supplierId = FinanceSellProductBean.querySupplier(dbop, batchLog.getBatchCode());
//											String sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
//														+ "FROM product p JOIN user_order_product_split_history h ON p.id = h.product_id " 
//														+ "WHERE h.order_id = ? ";
//											dbop.prepareStatement(sql);
//											pstmt = dbop.getPStmt();
//											pstmt.setInt(1, vorder.getId());
//											rs = pstmt.executeQuery();
//											while(rs.next()){
//												int pId = rs.getInt("id");
//												sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
//														"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
//												dbop.prepareStatement(sql);
//												pstmt = dbop.getPStmt();
//												pstmt.setInt(1,pId);
//												rs0 = pstmt.executeQuery();
//												
//												FinanceSellProductBean fspBean = new FinanceSellProductBean();
//												fspBean.setOrderId(vorder.getId());
//												fspBean.setProductId(rs.getInt("id"));
//												fspBean.setFinanceSellId(financeSellId);
//												fspBean.setBuyCount(-rs.getInt("count"));
//												fspBean.setPrice(rs.getFloat("price"));
//												fspBean.setDprice(rs.getFloat("dprice"));
//												fspBean.setPrice5(rs.getFloat("price5"));
//												if(rs0.next()){
//													fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
//												}
//												fspBean.setParentId1(rs.getInt("parent_id1"));
//												fspBean.setParentId2(rs.getInt("parent_id2"));
//												fspBean.setParentId3(rs.getInt("parent_id3"));
//												fspBean.setCreateDatetime(DateUtil.getNow());
//												fspBean.setDataType(2);	//2-商品未妥投退回
//												fspBean.setBalanceMode(ticket);
//												fspBean.setSupplierId(supplierId);
//												
//												frfService.addFinanceSellProductBean(fspBean);
//												
//											}
//											
//											//赠品
//											sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
//												+ "FROM product p JOIN user_order_present_split_history h ON p.id = h.product_id " 
//												+ "WHERE h.order_id = ? ";
//											dbop.prepareStatement(sql);
//											pstmt = dbop.getPStmt();
//											pstmt.setInt(1, vorder.getId());
//											rs = pstmt.executeQuery();
//											while(rs.next()){
//												int pId = rs.getInt("id");
//												sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
//														"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
//												dbop.prepareStatement(sql);
//												pstmt = dbop.getPStmt();
//												pstmt.setInt(1,pId);
//												rs0 = pstmt.executeQuery();
//												
//												FinanceSellProductBean fspBean = new FinanceSellProductBean();
//												fspBean.setOrderId(vorder.getId());
//												fspBean.setProductId(rs.getInt("id"));
//												fspBean.setFinanceSellId(financeSellId);
//												fspBean.setBuyCount(-rs.getInt("count"));
//												fspBean.setPrice(rs.getFloat("price"));
//												fspBean.setDprice(rs.getFloat("dprice"));
//												fspBean.setPrice5(rs.getFloat("price5"));
//												if(rs0.next()){
//													fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
//												}
//												fspBean.setParentId1(rs.getInt("parent_id1"));
//												fspBean.setParentId2(rs.getInt("parent_id2"));
//												fspBean.setParentId3(rs.getInt("parent_id3"));
//												fspBean.setCreateDatetime(DateUtil.getNow());
//												fspBean.setDataType(3);	//3-赠品未妥投退回
//												fspBean.setBalanceMode(ticket);
//												fspBean.setSupplierId(supplierId);
//												
//												frfService.addFinanceSellProductBean(fspBean);
//												
//											}
//										
//			    						}
//			    						}
//									//-------------liuruilan-------------
//				             		
//				             	}
//				             }
					}
					//将订单数据写入发货信息表
//					FinanceSellBean fsBean = new FinanceSellBean();
//					int deliverType = 0;
//					if(voOrder.deliverToBalanceTypeMap.get("" + vorder.getDeliver()) != null){
//						deliverType = Integer.parseInt(voOrder.deliverToBalanceTypeMap.get("" + vorder.getDeliver()).toString());
//					}
//					fsBean.setOrderId(vorder.getId());
//					fsBean.setCode(vorder.getCode());
//					fsBean.setPrice(price);	//退货单金额为负，计算时已经是负，此处不再加负号
//					if(frfService.getFinanceSellBean("data_type = 1 AND code ='" + vorder.getCode() + "'") ==  null){
//						fsBean.setCarriage(-vorder.getPostage());	//多次退单只记录一次运费，符号和订单金额一致
//					}
//					fsBean.setCharge(0);
//					fsBean.setBuyMode(vorder.getBuyMode());
//					fsBean.setPayMode(vorder.getBuyMode()); //备用字段，取值赞同buyMode
//					fsBean.setDeliverType(deliverType);
//					fsBean.setCreateDatetime(DateUtil.getNow());
//					fsBean.setPackageNum(vorder.getPackageNum());
//					fsBean.setDataType(1);	//1-销售退回
//					fsBean.setCount(1);
//					frfService.addFinanceSellBean(fsBean);
					//---------------liuruilan-------------------
					
					returnStr = Constants.RET_SUC;
					
					// --------------------add start--------------------
    				//根据业务类型采集财务基础数据，已经计算过库存均价
    				try{
    					FinanceSaleBaseDataService financeBaseDataService = 
        						FinanceBaseDataServiceFactory.constructFinanceSaleBaseDataService(
        						FinanceStockCardBean.CARDTYPE_CANCELORDERSTOCKIN,	dbop.getConn());
        				financeBaseDataService.acquireFinanceAfterSaleBaseData(vorder.getCode(), vorder.getCode(), user.getId(), 
        						DateUtil.getNow(), ProductStockBean.STOCKTYPE_RETURN, wareArea, 
        						FinanceStockCardBean.CARDTYPE_CANCELORDERSTOCKIN, null);
    				}catch(Exception e){
    					e.printStackTrace();
    				}
					
				// --------------------add end----------------------
				//正常入库并且不是缺失商品入库
				}else{
					Set enterProductId = enterProductMap.keySet();
					int encount = -1;
					int ocount = -1;
					int productId = 0;
					//封装正常入库商品，并判断订单和商品是否匹配
					for (Iterator it = enterProductId.iterator(); it.hasNext();) {
						productId = ((Integer) it.next()).intValue();
						encount = ((Integer) enterProductMap.get(Integer.valueOf(productId)))
								.intValue();
						ocount = ((Integer) productMap.get(Integer.valueOf(productId))).intValue();
						if (encount < ocount) {
							return Constants.RET_PRO_ORDER_UNMATCH;
						}
					}
					// 判断商品是否满足订单数量
//					if ((auditPackageBean.getOrderCode().equals(orderCode) && enterProductMap
//							.size() < productMap.size())
//							|| (!auditPackageBean.getOrderCode().equals(orderCode) && enterProductMap
//									.size() < productMap.size())) {
//						return Constants.RET_PRO_ORDER_UNMATCH;
//	
//						// 判断订单和包裹是否匹配B12072359093
//					} else if (!auditPackageBean.getOrderCode().equals(orderCode)) {
//						return Constants.RET_PAC_ORDER_UNMATCH;
//					}
					
					List auditPackageBean = istockServiceSlave.getAuditPackageList("package_code='" + packageCode + "'", 0, -1, null);
					if (auditPackageBean == null) {
						return Constants.RET_PAC_ORDER_UNMATCH;
					}
					AuditPackageBean apb = null;
					boolean flag = false;
					for(int i=0; i<auditPackageBean.size(); i++){
						apb = (AuditPackageBean) auditPackageBean.get(i);
						if (apb.getOrderCode().equals(vorder.getCode())) {
							flag = true;
						}
					}
					
					//订单和包裹是否匹配
					if(!flag && enterProductMap.size() == productMap.size()){
						return Constants.RET_PAC_ORDER_UNMATCH;
					//订单和商品是否匹配
					}else if (enterProductMap.size() < productMap.size()){
						return Constants.RET_PRO_ORDER_UNMATCH;
					}
	
					// 插入包裹退货记录
					updatePackage(rpBean, packageCode, user, vorder, dbop,
							voProductList, ReturnedPackageBean.NORMALENTER, wareService,wareArea, ReturnedPackageBean.STATUS_HAS_RETURN);
	
					returnStr = Constants.RET_SUC;
				}

				// 异常入库,包裹订单不匹配
			} else if (type.equals(Constants.EXP_STO_PACORDER)) {


				// 插入包裹退货记录
				updatePackage(rpBean, packageCode, user, vorder, dbop,
						voProductList, ReturnedPackageBean.UNMATCHENTER, wareService, wareArea, ReturnedPackageBean.STATUS_HAS_RETURN);

				returnStr = "success";

				// 异常入库，商品订单不匹配
			} else if (type.equals(Constants.EXP_STO_PROORDER)) {


				List excProductList = new ArrayList();

				// 构造备注异常信息
				String[] lostProduct = exceptionPCode.split("\r\n");
				StringBuilder strBuilder = new StringBuilder();
				voProduct expProduct = null;
				Map expMap = new HashMap();
				for (int i = 0; i < lostProduct.length; i++) {
					ProductBarcodeVO pbBean = bService.getProductBarcode("barcode="+"'"+lostProduct[i].trim()+"'");
					if(pbBean == null){
						expProduct = wareService.getProduct(lostProduct[i].trim());
					}else{
						expProduct = wareService.getProduct(pbBean.getProductId());
					}
					if (strBuilder.length() > 0) {
						strBuilder.append(";");
					}
					
					if (expProduct == null) {
						return Constants.RET_PRO_NOTEXIST;
					}
					
					if (productMap.get(Integer.valueOf(expProduct.getId())) == null) {
						return Constants.RET_PRO_ORDER_BELONG;
					}
					
					if(expMap.get(Integer.valueOf(expProduct.getId())) != null){
						int temp = ((Integer) expMap.get(Integer.valueOf(expProduct.getId())))
							.intValue() + 1;
						expMap.put(Integer.valueOf(expProduct.getId()), Integer.valueOf(temp));
					}else{
						expMap.put(Integer.valueOf(expProduct.getId()), Integer.valueOf(1));
					}
				}
				
				//构造异常信息
				Integer pId = null;
				for(Iterator it = expMap.keySet().iterator();it.hasNext();){
					pId = (Integer) it.next();
					expProduct = wareService.getProduct(pId.intValue());
					if (payFlag.equals("1")) {
						if(strBuilder.indexOf(expProduct.getCode())==-1){
							strBuilder.append(expProduct.getCode() + ":" + expMap.get(pId) + ",已索赔");
						}
					} else {
						if(strBuilder.indexOf(expProduct.getCode())==-1){
							strBuilder.append(
									expProduct.getCode() + ":" + expMap.get(pId) + ",未索赔");
						}
					}
				}
				
				Set enterProductId = enterProductMap.keySet();
				int encount = -1;
				int ocount = -1;
				int productId = 0;
				voProduct p = null;
				//对比是否多录入了缺失商品
				for (Iterator it = enterProductId.iterator(); it.hasNext();) {
					productId = ((Integer) it.next()).intValue();
					encount = ((Integer) enterProductMap.get(Integer.valueOf(productId)))
							.intValue();
					if(expMap.get(Integer.valueOf(productId)) != null){
						encount += ((Integer)(expMap.get(Integer.valueOf(expProduct.getId())))).intValue();
					}
					ocount = ((Integer) productMap.get(Integer.valueOf(productId))).intValue();
					if (encount < ocount) {
						return Constants.RET_UNALL_LOSTPRO_ENTER;
					}else if(encount > ocount){
						p = wareService.getProduct(productId);
						return "您多录入了"+(encount-ocount)+"个缺失商品："+p.getCode()+"!";
					}
				}
				
				ReturnedPackageBean retPackBean = statService.getReturnedPackage("package_code='" + packageCode
						+ "' and order_code='" + vorder.getCode() + "'");
				
				if (retPackBean != null) {
					statService.updateReturnedPackage("remark='"
							+ strBuilder.toString() + "'", "order_code='"
							+ vorder.getCode() + "'");
				} else {

					// 统计缺失商品信息
//					Map enterLostProductMap = new HashMap();
					voProduct tempProduct = null;
					OrderStockProductBean tempvoProduct = null;
					int pIdValue = -1;
					for(Iterator it = enterProductMap.keySet().iterator(); it.hasNext();){
						pIdValue = Integer.parseInt((it.next())+"");
						tempProduct = wareService.getProduct(pIdValue);
						tempvoProduct = (OrderStockProductBean) istockService.getOrderStockProductList("order_stock_id="+orderStockBean.getId()+" and product_id="+tempProduct.getId(), 0, -1, null).get(0);
						tempvoProduct.setProduct(tempProduct);
						tempvoProduct.setStockoutCount(
								((Integer.parseInt(enterProductMap.get(Integer.valueOf(tempProduct.getId()))+""))));
						excProductList.add(tempvoProduct);
					}
					

					// 插入包裹信息
					updatePackage(rpBean, packageCode, user, vorder, dbop,
							excProductList, ReturnedPackageBean.LOSTPRODUCTENTER,wareService, wareArea, ReturnedPackageBean.STATUS_HAS_RETURN);

					// 更新包裹异常备注信息
					statService.updateReturnedPackage("remark='"
							+ strBuilder.toString() + "'", "order_code='"
							+ vorder.getCode() + "'");

				}
				returnStr = Constants.RET_SUC_LOSTPRO_ENTER;
			}

			
			//添加订单状态变更日志
			OrderAdminLogBean log = new OrderAdminLogBean();
    		log.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
    		log.setUserId(user.getId());
    		log.setUsername(user.getUsername());
    		log.setOrderId(vorder.getId());
    		log.setOrderCode(vorder.getCode());
    		log.setCreateDatetime(DateUtil.getNow());
    		StringBuilder logContent = new StringBuilder();
    		logContent.append("[订单状态:");
			logContent.append(vorder.getStatus());
			logContent.append("->");
			logContent.append(11);
			logContent.append("]");
    		log.setContent(logContent.toString());
    		if(!logService.addOrderAdminLog(log)){
    			dbop.rollbackTransaction();
    			return "更新订单日志变更失败！";
    		}
    		
			// 修改订单状态为已退回
    		if(!wareService.setOrderStatus(vorder.getId(), 11)){
    			dbop.rollbackTransaction();
    			return "更新订单状态失败！";
    		}
			
			// 修改订单库存状态为实物已退回
			if(!istockService.updateOrderStock("status=" + OrderStockBean.STATUS8,
					"order_id=" + vorder.getId() + " and status!=3")){
				dbop.rollbackTransaction();
				return "更新orderstock失败！";
			}

			
			dbop.commitTransaction();
			return returnStr;
			}
		} catch (Exception e) {
			if(!dbop.getConn().getAutoCommit()){
				dbop.rollbackTransaction();
			}
			e.printStackTrace();
			if(logger.isErrorEnabled()){
				logger.error("添加退货包裹失败",e);
			}
			throw new RuntimeException(e);
		} finally {
			if (dbop != null) {
				dbop.release();
			}
			if (dbOpSlave != null) {
				dbOpSlave.release();
			}
		}
	}

	
	private Map constructExpMap(String remark) {
		Map expMap = new HashMap();
		if(remark != null){
			String[] productInfo = remark.split(";");
			for(int i=0; i<productInfo.length; i++){
				expMap.put(productInfo[i].split(":")[0], productInfo[i].split(":")[1]);
			}
		}
		return expMap;
	}


	private void updatePackage(ReturnedPackageBean rpBean, String packageCode, voUser user,
			voOrder vorder, DbOperation dbop, List productList, int enterType, WareService wareService, int wareArea, int status)throws Exception {
		StatService statService = new StatService(CONN_IN_SERVICE, dbop);
		ICargoService service = new CargoServiceImpl(CONN_IN_SERVICE, dbop);
		FinanceReportFormsService frfService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, dbop);
		IProductStockService productStockService = new ProductStockServiceImpl(CONN_IN_SERVICE, dbop);
		
		//更新退货包裹
		String condition = "package_code='"+packageCode+"', operator_id=" + user.getId() + ", operator_name='" + user.getUsername() + "', storage_status=" + enterType + ", storage_time='" + DateUtil.getNow() + "',status=" + status;
		if(!statService.updateReturnedPackage(condition,"id="+rpBean.getId())){
			throw new RuntimeException("updatePackage---更新退货包裹失败！");
		}
		OrderStockProductBean product = null;
//		ReturnedProductBean productBean = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSet rs0 = null;
		try{
			
		CargoInfoAreaBean cargoArea = service.getCargoInfoArea("old_id = "+wareArea);
		CargoInfoBean cargoInfo = service.getCargoInfo(
				"type=0 and status=0 and area_id=" + cargoArea.getId() + " and stock_type=" + CargoInfoBean.STOCKTYPE_RETURN);
//		synchronized (Constants.LOCK) {
		voProduct vProduct = null;
		IProductStockService productservice = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,dbop);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbop);
		float price = 0;
		for (int i = 0; i < productList.size(); i++) {
			product = (OrderStockProductBean) productList.get(i);
			vProduct = wareService.getProduct(product.getProductId());
			vProduct.setPsList(
					productservice.getProductStockList("product_id="+ product.getProductId(), -1, -1, null));
			
			//更新库存信息
			updateStockCount(service, productStockService, product, cargoInfo, wareArea);
			
			CargoInfoAreaBean tcib = service.getCargoInfoArea("id="+cargoInfo.getAreaId());
			if(tcib == null){
				continue;
			}
			
			//添加库存批次记录
			// 需要计算一下 库存价格
//            float price5 = 0;
//            voOrderProduct orderProduct = adminService.getOrderProductSplit(vorder.getId(), vProduct.getCode());
//            if(orderProduct == null){
//            	orderProduct = adminService.getOrderPresentSplit(vorder.getId(), vProduct.getCode());
//            }
			
//			vProduct.setPsList(productservice.getProductStockList("product_id=" +vProduct.getId(), -1, -1, null));
//			int totalCount = vProduct.getStockAll() + vProduct.getLockCountAll();
//			price5 = ((float)Math.round(
//					(vProduct.getPrice5() * totalCount + (vProduct.getPrice3() * product.getStockoutCount())) 
//					/ (totalCount + product.getStockoutCount()) 
//					* 1000))/1000;
//			if(!service.getDbOp().executeUpdate("update product set price5=" + price5 + " where id = " + product.getProductId())){
//				throw new RuntimeException("更新库存价格失败！");
//			}
			
			
			ProductStockBean ps = productservice.getProductStock("product_id=" + product.getProductId() + " and area=" + wareArea + " and type=" + ProductStockBean.STOCKTYPE_RETURN);
//			 List batchLogList = stockService.getStockBatchLogList("code='"+vorder.getCode()+"' and product_id="+product.getProductId()+" and remark = '订单出货'", -1, -1, "id desc");
//             if(batchLogList==null||batchLogList.size()==0){
//
//             	String code = "X"+DateUtil.getNow().substring(0,10).replace("-", "");
//             	StockBatchBean newBatch;
//             	newBatch = stockService.getStockBatch("code like '" + code + "%' and product_id="+product.getProductId());
//             	int ticket = 0;
//             	int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), product.getProductId(), ticket);
//             	if(newBatch == null){
//             		//当日第一份批次记录，编号最后三位 001
//             		code += "001";
//             	}else {
//             		//获取当日计划编号最大值
//             		newBatch = stockService.getStockBatch("code like '" + code + "%' and product_id="+product.getProductId()+" order by id desc limit 1"); 
//             		String _code = newBatch.getCode();
//             		int number = Integer.parseInt(_code.substring(_code.length()-3));
//             		number++;
//             		code += String.format("%03d",new Object[]{new Integer(number)});
//             	}
//             	
//             	//添加批次记录
//             	newBatch = new StockBatchBean();
//             	newBatch.setCode(code);
//             	newBatch.setProductId(product.getProductId());
//             	newBatch.setProductStockId(ps.getId());
//             	newBatch.setStockArea(wareArea);
//             	newBatch.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//             	newBatch.setProductStockId(ps.getId());
//             	newBatch.setCreateDateTime(DateUtil.getNow());
//             	newBatch.setPrice(vProduct.getPrice5());
//             	newBatch.setBatchCount(product.getStockoutCount());
//             	newBatch.setTicket(ticket);
//
//             	if(!stockService.addStockBatch(newBatch)){
//             		throw new RuntimeException("添加批次记录失败！");
//             	}
//
//             	//添加批次操作记录
//             	StockBatchLogBean batchLog = new StockBatchLogBean();
//             	batchLog.setCode(rpBean.getOrderCode());
//             	batchLog.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//             	batchLog.setStockArea(wareArea);
//             	batchLog.setBatchCode(newBatch.getCode());
//             	batchLog.setBatchCount(product.getStockoutCount());
//             	batchLog.setBatchPrice(newBatch.getPrice());
//             	batchLog.setProductId(newBatch.getProductId());
//             	batchLog.setRemark("退货入库");
//             	batchLog.setCreateDatetime(DateUtil.getNow());
//             	batchLog.setUserId(user.getId());
//             	if(!stockService.addStockBatchLog(batchLog)){
//             		throw new RuntimeException("添加批次操作记录失败！");
//             	}
//             	
//             	//stockinPrice = batchLog.getBatchCount()*batchLog.getBatchPrice();
//             	
//             	
//            	//财务产品信息表---liuruilan-----2012-11-01-----
//				FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + product.getProductId());
//				if(fProduct == null){
//					throw new RuntimeException("财务数据查询异常！");
//				}
//				totalCount = vProduct.getStockAll() + vProduct.getLockCountAll();
//				float priceSum = Arith.mul(price5, totalCount);
//				int stockinCount = product.getStockoutCount();
//				
//				//计算公式：(结存总额 + (发货时批次价 * 本批退货数量)) / (库存总数量 + 本批退货数量)
//				float priceHasticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumHasticket(), Arith.mul(batchLog.getBatchPrice(), stockinCount)), Arith.add(_count, stockinCount)), 2);
//				float priceSumHasticket = Arith.mul(priceHasticket,  stockinCount + _count);
//				String set = "price =" + price5 + ", price_sum =" + priceSum + ", price_hasticket =" + priceHasticket + ", price_sum_hasticket =" + priceSumHasticket;
//				frfService.updateFinanceProductBean(set, "product_id = " + product.getId());
//				
//				//财务进销存卡片
//				int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), batchLog.getStockArea(), batchLog.getStockType(), ticket, product.getProductId());
//				int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, batchLog.getStockType(), ticket, product.getProductId());
//				int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), batchLog.getStockArea(), -1,ticket,  product.getProductId());
//				FinanceStockCardBean fsc = new FinanceStockCardBean();
//				fsc.setCardType(StockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
//				fsc.setCode(rpBean.getOrderCode());
//				fsc.setCreateDatetime(DateUtil.getNow());
//				fsc.setStockType(batchLog.getStockType());
//				fsc.setStockArea(batchLog.getStockArea());
//				fsc.setProductId(product.getProductId());
//				fsc.setStockId(ps.getId());
//				fsc.setStockInCount(-batchLog.getBatchCount());	
//				fsc.setCurrentStock(currentStock);	//只记录分库总库存
//				fsc.setStockAllArea(stockAllArea);
//				fsc.setStockAllType(stockAllType);
//				fsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
//				fsc.setStockPrice(price5);
//				
//				fsc.setType(fsc.getCardType());
//				fsc.setIsTicket(ticket);
//				fsc.setStockBatchCode(batchLog.getBatchCode());
//				fsc.setBalanceModeStockCount(stockinCount + _count);
//				fsc.setStockInPriceSum(-Double.parseDouble(String.valueOf(Arith.mul(batchLog.getBatchPrice(), stockinCount))));
//				fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
//				double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
//				fsc.setAllStockPriceSum(tmpPrice);
//				frfService.addFinanceStockCardBean(fsc);
//				
//				FinanceSellBean fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+vorder.getId());
//				FinanceSellProductBean fsbean2=frfService.getFinanceSellProductBean(" (data_type=2 or data_type=3) and order_id="+vorder.getId());
//				if((fsbean1!=null&&fsbean1.getId()!=0)||(fsbean2!=null&&fsbean2.getId()!=0)){
//					
//					//否则向finance_sell,finance_sell_product表添加
//				}else{
//				//向finance_sell里添加记录
//					FinanceSellBean fsBean = new FinanceSellBean();
//					int deliverType = 0;
//					if(voOrder.deliverToBalanceTypeMap.get("" + vorder.getDeliver()) != null){
//						deliverType = Integer.parseInt(voOrder.deliverToBalanceTypeMap.get("" + vorder.getDeliver()).toString());
//					}
//					fsBean.setOrderId(vorder.getId());
//					fsBean.setCode(vorder.getCode());
//					fsBean.setPrice(-(vorder.getDprice()-vorder.getPostage()));	//退货单金额为负，
//				
//						fsBean.setCarriage(-vorder.getPostage());	
//					
//					fsBean.setCharge(0);
//					fsBean.setBuyMode(vorder.getBuyMode());
//					fsBean.setPayMode(vorder.getBuyMode()); //备用字段，取值赞同buyMode
//					fsBean.setDeliverType(deliverType);
//					fsBean.setCreateDatetime(DateUtil.getNow());
//					fsBean.setPackageNum(vorder.getPackageNum());
//					fsBean.setDataType(1);	//1-销售退回
//					fsBean.setCount(-1);
//					frfService.addFinanceSellBean(fsBean);	
//					int financeSellId=frfService.getDbOp().getLastInsertId();
//				//向finance_sell添加记录end
//				
//				//将订单商品写入销售商品信息表--商品
//				int supplierId = FinanceSellProductBean.querySupplier(dbop, batchLog.getBatchCode());
//				String sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
//							+ "FROM product p JOIN user_order_product_split_history h ON p.id = h.product_id " 
//							+ "WHERE h.order_id = ? ";
//				dbop.prepareStatement(sql);
//				pstmt = dbop.getPStmt();
//				pstmt.setInt(1, vorder.getId());
//				rs = pstmt.executeQuery();
//				while(rs.next()){
//					int pId = rs.getInt("id");
//					sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
//							"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
//					dbop.prepareStatement(sql);
//					pstmt = dbop.getPStmt();
//					pstmt.setInt(1,pId);
//					rs0 = pstmt.executeQuery();
//					
//					FinanceSellProductBean fspBean = new FinanceSellProductBean();
//					fspBean.setOrderId(vorder.getId());
//					fspBean.setProductId(rs.getInt("id"));
//					fspBean.setBuyCount(-rs.getInt("count"));
//					fspBean.setPrice(rs.getFloat("price"));
//					fspBean.setFinanceSellId(financeSellId);
//					fspBean.setDprice(rs.getFloat("dprice"));
//					fspBean.setPrice5(rs.getFloat("price5"));
//					if(rs0.next()){
//						fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
//					}
//					fspBean.setParentId1(rs.getInt("parent_id1"));
//					fspBean.setParentId2(rs.getInt("parent_id2"));
//					fspBean.setParentId3(rs.getInt("parent_id3"));
//					fspBean.setCreateDatetime(DateUtil.getNow());
//					fspBean.setDataType(2);	//2-商品未妥投退回
//					fspBean.setBalanceMode(ticket);
//					fspBean.setSupplierId(supplierId);
//					
//					frfService.addFinanceSellProductBean(fspBean);
//					
//					price = Arith.add(price, Arith.mul(fspBean.getDprice(), fspBean.getBuyCount()));
//				}
//				
//				//赠品
//				sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
//					+ "FROM product p JOIN user_order_present_split_history h ON p.id = h.product_id " 
//					+ "WHERE h.order_id = ? ";
//				dbop.prepareStatement(sql);
//				pstmt = dbop.getPStmt();
//				pstmt.setInt(1, vorder.getId());
//				rs = pstmt.executeQuery();
//				while(rs.next()){
//					int pId = rs.getInt("id");
//					sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
//							"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
//					dbop.prepareStatement(sql);
//					pstmt = dbop.getPStmt();
//					pstmt.setInt(1,pId);
//					rs0 = pstmt.executeQuery();
//					
//					FinanceSellProductBean fspBean = new FinanceSellProductBean();
//					fspBean.setOrderId(vorder.getId());
//					fspBean.setProductId(rs.getInt("id"));
//					fspBean.setBuyCount(-rs.getInt("count"));
//					fspBean.setFinanceSellId(financeSellId);
//					fspBean.setPrice(rs.getFloat("price"));
//					fspBean.setDprice(rs.getFloat("dprice"));
//					fspBean.setPrice5(rs.getFloat("price5"));
//					if(rs0.next()){
//						fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
//					}
//					fspBean.setParentId1(rs.getInt("parent_id1"));
//					fspBean.setParentId2(rs.getInt("parent_id2"));
//					fspBean.setParentId3(rs.getInt("parent_id3"));
//					fspBean.setCreateDatetime(DateUtil.getNow());
//					fspBean.setDataType(3);	//3-赠品未妥投退回
//					fspBean.setBalanceMode(ticket);
//					fspBean.setSupplierId(supplierId);
//					
//					frfService.addFinanceSellProductBean(fspBean);
//					
//					price = Arith.add(price, Arith.mul(fspBean.getDprice(), fspBean.getBuyCount()));
//				}
//				}
//				//-------------liuruilan-------------
//             }else{
//             	Iterator batchIter = batchLogList.listIterator();
//         		
//             	while(batchIter.hasNext()&&product.getStockoutCount()>0){
//             		StockBatchLogBean batchLog = (StockBatchLogBean)batchIter.next(); 
//             		StockBatchBean batch = stockService.getStockBatch("code = '"+batchLog.getBatchCode()+"' and product_id="+batchLog.getProductId()+" and stock_type="+ProductStockBean.STOCKTYPE_RETURN+" and stock_area="+wareArea);
//             		int ticket = FinanceSellProductBean.queryTicket(dbop, batchLog.getBatchCode());	//是否含票 
//            		if(ticket == -1){
//            			throw new RuntimeException("财务数据查询异常！");
//					}
//            		int _count = FinanceProductBean.queryCountIfTicket(dbop, product.getProductId(), ticket);
//             		if(batch!=null){
//             			if(product.getStockoutCount()<=batchLog.getBatchCount()){
//             				stockService.updateStockBatch("batch_count = batch_count+"+product.getStockoutCount(), "id="+batch.getId());
////             				batchCount = stockinCount;
////             				stockinCount = 0;
//             			}else{
//             				stockService.updateStockBatch("batch_count = batch_count+"+batchLog.getBatchCount(), "id="+batch.getId());
////             				stockinCount -= batchLog.getBatchCount();
////             				batchCount = batchLog.getBatchCount();
//             			}
//
//             		}else{
//
//             			StockBatchBean newBatch = new StockBatchBean();
//             			newBatch.setCode(batchLog.getBatchCode());
//             			newBatch.setProductId(product.getProductId());
//             			newBatch.setProductStockId(ps.getId());
//             			newBatch.setStockArea(wareArea);
//             			newBatch.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//             			newBatch.setProductStockId(ps.getId());
//             			newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batchLog.getBatchCode(),product.getProductId()));
//             			newBatch.setPrice(batchLog.getBatchPrice());
//             			newBatch.setTicket(ticket);
//
//             			if(product.getStockoutCount()<=batchLog.getBatchCount()){
//             				newBatch.setBatchCount(product.getStockoutCount());
////             				batchCount = stockinCount;
////             				stockinCount = 0;
//             			}else{
//             				newBatch.setBatchCount(batchLog.getBatchCount());
////             				stockinCount -= batchLog.getBatchCount();
////             				batchCount = batchLog.getBatchCount();
//             			}
//
//             			if(!stockService.addStockBatch(newBatch)){
//             				throw new RuntimeException("添加批次记录失败");
//             			}
//             		}
//             		
////             		stockinPrice = stockinPrice + batchLog.getBatchPrice()*batchCount;
//
//             		//添加批次操作记录
//             		StockBatchLogBean newBatchLog = new StockBatchLogBean();
//             		newBatchLog.setCode(rpBean.getOrderCode());
//             		newBatchLog.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//             		newBatchLog.setStockArea(wareArea);
//             		newBatchLog.setBatchCode(batchLog.getBatchCode());
//             		newBatchLog.setBatchCount(product.getStockoutCount());
//             		newBatchLog.setBatchPrice(batchLog.getBatchPrice());
//             		newBatchLog.setProductId(batchLog.getProductId());
//             		newBatchLog.setRemark("退货入库");
//             		newBatchLog.setCreateDatetime(DateUtil.getNow());
//             		newBatchLog.setUserId(user.getId());
//             		if(!stockService.addStockBatchLog(newBatchLog)){
//             			throw new RuntimeException("添加批次操作记录失败！");
//             		}
//             		
//             		//财务产品信息表---liuruilan-----2012-11-01-----
//					FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + product.getProductId());
//					if(fProduct == null){
//						throw new RuntimeException("财务数据查询异常！");
//					}
//					float priceSum = Arith.mul(price5, totalCount);
//					float priceHasticket = 0;
//					float priceNoticket = 0;
//					float priceSumHasticket = 0;
//					float priceSumNoticket = 0;
//					int batchCount = product.getStockoutCount();;
//					String set = "price =" + price5 + ", price_sum =" + priceSum;
//					if(ticket == 0){	//0-有票
//						//计算公式：(结存总额 + (发货时批次价 * 本批退货数量)) / (库存总数量 + 本批退货数量)
//						priceHasticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumHasticket(), Arith.mul(batchLog.getBatchPrice(), batchCount)), Arith.add(_count, batchCount)), 2);
//						priceSumHasticket = Arith.mul(priceHasticket,  batchCount + _count);
//						set += ", price_hasticket =" + priceHasticket + ", price_sum_hasticket =" + priceSumHasticket;
//					}
//					if(ticket == 1){	//1-无票
//						priceNoticket = Arith.round(Arith.div(Arith.add(fProduct.getPriceSumNoticket(), Arith.mul(batchLog.getBatchPrice(), batchCount)), Arith.add(_count, batchCount)), 2);
//						priceSumNoticket = Arith.mul(priceNoticket,  batchCount + _count);
//						set += ", price_noticket =" + priceNoticket + ", price_sum_noticket =" + priceSumNoticket;
//					}
//					frfService.updateFinanceProductBean(set, "product_id = " + product.getId());
//					
//					//财务进销存卡片
//					int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), newBatchLog.getStockArea(), newBatchLog.getStockType(), ticket, product.getProductId());
//					int stockAllType=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, newBatchLog.getStockType(), ticket, product.getProductId());
//					int stockAllArea=FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), newBatchLog.getStockArea(), -1,ticket, product.getProductId());
//					FinanceStockCardBean fsc = new FinanceStockCardBean();
//					fsc.setCardType(StockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
//					fsc.setCode(vorder.getCode());
//					fsc.setCreateDatetime(DateUtil.getNow());
//					fsc.setStockType(ProductStockBean.STOCKTYPE_RETURN);
//					fsc.setStockArea(newBatchLog.getStockArea());
//					fsc.setProductId(product.getProductId());
//					fsc.setStockId(ps.getId());
//					fsc.setStockInCount(-batchLog.getBatchCount());	
//					fsc.setCurrentStock(currentStock);	//只记录分库总库存
//					fsc.setStockAllArea(stockAllArea);
//					fsc.setStockAllType(stockAllType);
//					fsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
//					fsc.setStockPrice(price5);
//					
//					fsc.setType(fsc.getCardType());
//					fsc.setIsTicket(ticket);
//					fsc.setStockBatchCode(batchLog.getBatchCode());
//					fsc.setBalanceModeStockCount(batchCount + _count);
//					fsc.setStockInPriceSum(-Double.parseDouble(String.valueOf(Arith.mul(batchLog.getBatchPrice(), batchCount))));
//					if(ticket == 0){
//						fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
//					}
//					if(ticket == 1){
//						fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceNoticket)));
//					}
//					double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
//					fsc.setAllStockPriceSum(tmpPrice);
//					frfService.addFinanceStockCardBean(fsc);
//					
//					
//					
//					
//					FinanceSellBean fsbean= frfService.getFinanceSellBean(" data_type=0 and order_id="+vorder.getId());
//					FinanceSellBean fsbean1=null;
//					List fspList=null;//销售订单里面所有产品
//					
//					if(fsbean!=null&&fsbean.getId()!=0){
//						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+vorder.getId());
//						FinanceSellProductBean fsbean2=frfService.getFinanceSellProductBean(" (data_type=2 or data_type=3) and order_id="+vorder.getId());
//						//如果已经有了销售未妥投订单不做任务操作，
//						if((fsbean1!=null&&fsbean1.getId()!=0)||(fsbean2!=null&&fsbean2.getId()!=0)){
//							
//							//否则向finance_sell,finance_sell_product表添加
//						}else{
//							fsbean.setId(0);
//							fsbean.setCreateDatetime(DateUtil.getNow());
//							fsbean.setDataType(1);
//							fsbean.setPrice(Arith.round(Arith.mul(fsbean.getPrice(),-1),2));
//							fsbean.setCarriage(-vorder.getPostage());
//							fsbean.setCount(-1);
//							frfService.addFinanceSellBean(fsbean);//向finance_sell表添加未妥投退货记录
//							int financeSellId=frfService.getDbOp().getLastInsertId();
//							fspList=frfService.getFinanceSellProductBeanList(" (data_type=1 or data_type=0) and order_id="+vorder.getId(), -1, -1, null);
//							if(fspList!=null&&fspList.size()>0){
//								FinanceSellProductBean fspbean=null;
//								for(int j=0;j<fspList.size();j++){
//									fspbean=(FinanceSellProductBean)fspList.get(j);
//									fspbean.setId(0);
//									fspbean.setFinanceSellId(financeSellId);
//									fspbean.setBuyCount(-fspbean.getBuyCount());
//									if(fspbean.getDataType()==0){
//										fspbean.setDataType(2);
//									}
//									if(fspbean.getDataType()==1){
//										fspbean.setDataType(3);
//									}
//									fspbean.setCreateDatetime(DateUtil.getNow());
//									frfService.addFinanceSellProductBean(fspbean);//向finance_sell_product表添加未妥投退货记录
//									
//								}
//							}
//						}
//					}else{
//						//如果finance_sell表中没有找到销售订单，要查看一下finance_sell表里面有没有未妥投退单，如果有则什么也没做
//						//如果没有则向否则向finance_sell,finance_sell_product表添加记录
//						fsbean1=frfService.getFinanceSellBean(" data_type=1 and order_id="+vorder.getId());
//						FinanceSellProductBean fsbean2=frfService.getFinanceSellProductBean(" (data_type=2 or data_type=3) and order_id="+vorder.getId());
//						if((fsbean1!=null&&fsbean1.getId()!=0)||(fsbean2!=null&&fsbean2.getId()!=0)){
//							
//							//否则向finance_sell,finance_sell_product表添加
//						}else{
//					
//							//向finance_sell里添加记录
//							
//							FinanceSellBean fsBean = new FinanceSellBean();
//							int deliverType = 0;
//							if(voOrder.deliverToBalanceTypeMap.get("" + vorder.getDeliver()) != null){
//								deliverType = Integer.parseInt(voOrder.deliverToBalanceTypeMap.get("" + vorder.getDeliver()).toString());
//							}
//							fsBean.setOrderId(vorder.getId());
//							fsBean.setCode(vorder.getCode());
//							fsBean.setPrice(-(vorder.getDprice()-vorder.getPostage()));	//退货单金额为负，
//						
//								fsBean.setCarriage(-vorder.getPostage());	
//							
//							fsBean.setCharge(0);
//							fsBean.setBuyMode(vorder.getBuyMode());
//							fsBean.setPayMode(vorder.getBuyMode()); //备用字段，取值赞同buyMode
//							fsBean.setDeliverType(deliverType);
//							fsBean.setCreateDatetime(DateUtil.getNow());
//							fsBean.setPackageNum(vorder.getPackageNum());
//							fsBean.setDataType(1);	//1-销售退回
//							fsBean.setCount(-1);
//							
//					        frfService.addFinanceSellBean(fsBean);
//							
//							int financeSellId=frfService.getDbOp().getLastInsertId();
//							
//							//向finance_sell添加记录end
//							
//					//将订单商品写入销售商品信息表--商品
//							int supplierId = FinanceSellProductBean.querySupplier(dbop, batchLog.getBatchCode());
//							String sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
//										+ "FROM product p JOIN user_order_product_split_history h ON p.id = h.product_id " 
//										+ "WHERE h.order_id = ? ";
//							dbop.prepareStatement(sql);
//							pstmt = dbop.getPStmt();
//							pstmt.setInt(1, vorder.getId());
//							rs = pstmt.executeQuery();
//							while(rs.next()){
//								int pId = rs.getInt("id");
//								sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
//										"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
//								dbop.prepareStatement(sql);
//								pstmt = dbop.getPStmt();
//								pstmt.setInt(1,pId);
//								rs0 = pstmt.executeQuery();
//								
//								FinanceSellProductBean fspBean = new FinanceSellProductBean();
//								fspBean.setOrderId(vorder.getId());
//								fspBean.setProductId(rs.getInt("id"));
//								fspBean.setBuyCount(-rs.getInt("count"));
//								fspBean.setPrice(rs.getFloat("price"));
//								fspBean.setFinanceSellId(financeSellId);
//								fspBean.setDprice(rs.getFloat("dprice"));
//								fspBean.setPrice5(rs.getFloat("price5"));
//								if(rs0.next()){
//									fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
//								}
//								fspBean.setParentId1(rs.getInt("parent_id1"));
//								fspBean.setParentId2(rs.getInt("parent_id2"));
//								fspBean.setParentId3(rs.getInt("parent_id3"));
//								fspBean.setCreateDatetime(DateUtil.getNow());
//								fspBean.setDataType(2);	//2-商品未妥投退回
//								fspBean.setBalanceMode(ticket);
//								fspBean.setSupplierId(supplierId);
//								
//								frfService.addFinanceSellProductBean(fspBean);
//								
//								price = Arith.add(price, Arith.mul(fspBean.getDprice(), fspBean.getBuyCount()));
//							}
//							
//							//赠品
//							sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
//								+ "FROM product p JOIN user_order_present_split_history h ON p.id = h.product_id " 
//								+ "WHERE h.order_id = ? ";
//							dbop.prepareStatement(sql);
//							pstmt = dbop.getPStmt();
//							pstmt.setInt(1, vorder.getId());
//							rs = pstmt.executeQuery();
//							while(rs.next()){
//								int pId = rs.getInt("id");
//								sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
//										"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
//								dbop.prepareStatement(sql);
//								pstmt = dbop.getPStmt();
//								pstmt.setInt(1,pId);
//								rs0 = pstmt.executeQuery();
//								
//								FinanceSellProductBean fspBean = new FinanceSellProductBean();
//								fspBean.setOrderId(vorder.getId());
//								fspBean.setProductId(rs.getInt("id"));
//								fspBean.setBuyCount(-rs.getInt("count"));
//								fspBean.setPrice(rs.getFloat("price"));
//								fspBean.setFinanceSellId(financeSellId);
//								fspBean.setDprice(rs.getFloat("dprice"));
//								fspBean.setPrice5(rs.getFloat("price5"));
//								if(rs0.next()){
//									fspBean.setProductLine(rs0.getInt("product_line_id"));	//财务用产品线
//								}
//								fspBean.setParentId1(rs.getInt("parent_id1"));
//								fspBean.setParentId2(rs.getInt("parent_id2"));
//								fspBean.setParentId3(rs.getInt("parent_id3"));
//								fspBean.setCreateDatetime(DateUtil.getNow());
//								fspBean.setDataType(3);	//3-赠品未妥投退回
//								fspBean.setBalanceMode(ticket);
//								fspBean.setSupplierId(supplierId);
//								
//								frfService.addFinanceSellProductBean(fspBean);
//								
//								price = Arith.add(price, Arith.mul(fspBean.getDprice(), fspBean.getBuyCount()));
//							}
//						}
//						
//					}
//					//-------------liuruilan-------------
//             	}
//             	
//             }
			
			// --------------------add start--------------------
			//根据业务类型采集财务基础数据,已经计算过库存均价
			try{
				FinanceSaleBaseDataService financeBaseDataService = FinanceBaseDataServiceFactory.constructFinanceSaleBaseDataService(FinanceStockCardBean.CARDTYPE_CANCELORDERSTOCKIN, service.getDbOp().getConn());
				financeBaseDataService.acquireFinanceAfterSaleBaseData(vorder.getCode(), vorder.getCode(), user.getId(), 
						DateUtil.getNow(), ProductStockBean.STOCKTYPE_RETURN, wareArea, 
						FinanceStockCardBean.CARDTYPE_CANCELORDERSTOCKIN, null);
			}catch(Exception e){
				e.printStackTrace();
			}
			
			// --------------------add end----------------------
			
			// 入库卡片
			StockCardBean sc = new StockCardBean();
			sc.setCardType(StockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
			sc.setCode(rpBean.getOrderCode());
			sc.setCreateDatetime(DateUtil.getNow());
			sc.setStockType(cargoInfo.getStockType());
			sc.setStockArea(wareArea);
			sc.setProductId(product.getProductId());
//			ProductStockBean psBean = statService
			sc.setStockId(ps.getId());
			sc.setStockInCount(product.getStockoutCount());
			// sc.setStockInPriceSum(stockinPrice);
			sc.setStockInPriceSum((new BigDecimal(product.getStockoutCount())).multiply(
							new BigDecimal(StringUtil.formatDouble2(vProduct
									.getPrice5()))).doubleValue());
			sc.setCurrentStock(vProduct.getStock(cargoArea.getOldId(),
					cargoInfo.getStockType()) + vProduct.getLockCount(cargoArea.getOldId(),cargoInfo.getStockType()));
			sc.setStockAllArea(vProduct.getStock(
					cargoArea.getOldId()) + vProduct.getLockCount(cargoArea.getOldId()));
			sc.setStockAllType(vProduct.getStockAllType(
					cargoInfo.getStockType()) + vProduct.getLockCountAllType(cargoInfo.getStockType()));
			sc.setAllStock(vProduct.getStockAll()
					+ vProduct.getLockCountAll());
			sc.setStockPrice(vProduct.getPrice5());
			sc.setAllStockPriceSum((new BigDecimal(
					sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());

			if (!productservice.addStockCard(sc)) {
				throw new RuntimeException("出库添加进销存失败！");
			}
			
			CargoProductStockBean cargoProductStock = service.getCargoAndProductStock("cargo_id="+cargoInfo.getId() + " and product_id="+ product.getProductId());
			// 货位入库卡片
			CargoStockCardBean csc = new CargoStockCardBean();
			csc.setCardType(CargoStockCardBean.CARDTYPE_CANCELORDERSTOCKIN);
			csc.setCode(rpBean.getOrderCode());
			csc.setCreateDatetime(DateUtil.getNow());
			csc.setStockType(cargoInfo.getStockType());
			csc.setStockArea(cargoArea.getId());
			csc.setProductId(product.getProductId());
			csc.setStockId(cargoProductStock.getId());
			csc.setStockInCount(product.getStockoutCount());
			csc.setStockInPriceSum((new BigDecimal(product.getStockoutCount()))
					.multiply(
							new BigDecimal(StringUtil.formatDouble2(vProduct
									.getPrice5()))).doubleValue());
			csc.setCurrentStock(vProduct.getStock(sc.getStockArea(),
					sc.getStockType())
					+ vProduct.getLockCount(sc.getStockArea(),
							sc.getStockType()));
			csc.setAllStock(vProduct.getStockAll()
					+ vProduct.getLockCountAll());
			csc.setCurrentCargoStock(cargoProductStock.getStockCount()
					+ cargoProductStock.getStockLockCount());
			csc.setCargoStoreType(cargoInfo.getStoreType());
			csc.setCargoWholeCode(cargoInfo.getWholeCode());
			csc.setStockPrice(vProduct.getPrice5());
			csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
					.multiply(
							new BigDecimal(StringUtil.formatDouble2(sc
									.getStockPrice()))).doubleValue());
			if (!service.addCargoStockCard(csc)) {
				throw new RuntimeException("出库进销存添加失败！");
			}
		}
		
		
		
		//将订单数据写入发货信息表---liuruilan---2012-10-29-----
//		FinanceSellBean fsBean = new FinanceSellBean();
//		int deliverType = 0;
//		if(voOrder.deliverToBalanceTypeMap.get("" + vorder.getDeliver()) != null){
//			deliverType = Integer.parseInt(voOrder.deliverToBalanceTypeMap.get("" + vorder.getDeliver()).toString());
//		}
//		fsBean.setOrderId(vorder.getId());
//		fsBean.setCode(vorder.getCode());
//		fsBean.setPrice(-vorder.getDprice());	//退货单金额为负，
//	
//			fsBean.setCarriage(-vorder.getPostage());	
//		
//		fsBean.setCharge(0);
//		fsBean.setBuyMode(vorder.getBuyMode());
//		fsBean.setPayMode(vorder.getBuyMode()); //备用字段，取值赞同buyMode
//		fsBean.setDeliverType(deliverType);
//		fsBean.setCreateDatetime(DateUtil.getNow());
//		fsBean.setPackageNum(vorder.getPackageNum());
//		fsBean.setDataType(1);	//1-销售退回
//		fsBean.setCount(-1);
//		frfService.addFinanceSellBean(fsBean);
		//-----------------liuruilan---------------------
		
		}finally{
			if(rs != null){
				rs.close();
			}
			if(pstmt != null){
				pstmt.close();
			}
		}
//		}
	}


	private void updateStockCount(ICargoService service,
			IProductStockService productStockService, OrderStockProductBean product,
			CargoInfoBean cargoInfo,int wareArea) {
		CargoProductStockBean cargoProductBean;
		ProductStockBean productStockBean;
		//更新货位库存
		cargoProductBean = service.getCargoProductStock(
				"cargo_id=" + cargoInfo.getId() + " and product_id=" + product.getProductId());
		if(cargoProductBean == null){
			cargoProductBean = new CargoProductStockBean();
			cargoProductBean.setCargoId(cargoInfo.getId());
			cargoProductBean.setProductId(product.getProductId());
			cargoProductBean.setStockCount(product.getStockoutCount());
			if(logger.isInfoEnabled()){
				logger.info(
						"开始添加货位库存，调用者为："+LogUtil.getInvokerName("mmb.stock.stat.ReturnedPackageServiceImpl")
						+"入库数量为："+cargoProductBean.getStockCount());
			}
			if(!service.addCargoProductStock(cargoProductBean)){
				throw new RuntimeException("添加货位库存失败");
			}
			if(logger.isInfoEnabled()){
				logger.info(
						"成功添加货位库存，调用者为："+LogUtil.getInvokerName("mmb.stock.stat.ReturnedPackageServiceImpl")
						+"入库数量为："+cargoProductBean.getStockCount());
			}
		}else{
			if(!service.updateCargoProductStockCount(
					cargoProductBean.getId(), product.getStockoutCount())){
				throw new RuntimeException("更新货位库存失败");
			}
		}
		//更新退货库库存
		productStockBean = productStockService.getProductStock(
				"type=4 and status=0 and area="+wareArea+" and product_id=" + product.getProductId());
		if(productStockBean == null){
			throw new RuntimeException("unexistProductStock");
		}else{
			if(!productStockService.updateProductStockCount(productStockBean.getId(), product.getStockoutCount())){
				throw new RuntimeException("unexistProductStock");
			}
		}
	}

	public List queryPackage(String storageTime, String[] storageStatus,
			int deliver, int startIndex, int pageCount, String orderCode, String packageCode, int wareArea, String availAreaIds) throws Exception {

//		if (storageTime == null || storageTime.equals("")) {
//			storageTime = format.format(new Date());
//		}

		DbOperation dbop = null;
		try {
			dbop = new DbOperation(DbOperation.DB);
			StatService statService = new StatService(CONN_IN_SERVICE, dbop);
			String condition = getQueryPackageSql(storageTime, storageStatus,
					deliver, orderCode, packageCode,wareArea, availAreaIds);
			List packageList = statService.getReturnedPackageList(condition,
					startIndex * pageCount, pageCount, "storage_time desc");
			if (packageList == null) {
				return new ArrayList();
			}
			ReturnedPackageBean rp = null;
			for(int i=0; i<packageList.size(); i++){
				rp = (ReturnedPackageBean) packageList.get(i);
				String temstorageTime = rp.getStorageTime();
				if(temstorageTime != null && !temstorageTime.equals("")){
					rp.setStorageTime(temstorageTime.substring(0,19));
				}
			}
			return packageList;
		} finally {
			if (dbop != null) {
				dbop.release();
			}
		}
	}


	public String getQueryPackageSql(String storageTime,
			String[] storageStatus, int deliver, String orderCode, String packageCode, int wareArea, String availAreaIds) {
		StringBuilder condition = new StringBuilder();
//		String beginTime = storageTime + " 00:00:00";
//		String endTime = storageTime + " 23:59:59";
//		String condition = null;
		if(storageStatus != null && storageStatus.length>0){
			if(storageStatus.length>1){
				condition.append("storage_status in(1,2,0)");
				if(orderCode!=null && !orderCode.equals("")){
					condition.append(" and order_code='");
					condition.append(orderCode);
					condition.append("'");
				}
				if(packageCode != null && !packageCode.equals("")){
					condition.append(" and package_code='");
					condition.append(packageCode);
					condition.append("'");
				}
				if(storageTime != null && !storageTime.equals("")){
					String beginTime = storageTime + " 00:00:00";
					String endTime = storageTime + " 23:59:59";
					condition.append(" and storage_time between '");
					condition.append(beginTime);
					condition.append("' and '");
					condition.append(endTime);
					condition.append("'");
				}
				if(deliver != -1){
					condition.append(" and deliver=");
					condition.append(deliver);
				}
				if( wareArea == -1 ) {
					condition.append(" and area in (" + availAreaIds + ")");
				} else {
					condition.append(" and area = " + wareArea);
				}
			}else{
				if(Integer.parseInt(storageStatus[0]) == 1){
					condition.append("storage_status in(1,2)");
					if(orderCode!=null && !orderCode.equals("")){
						condition.append(" and order_code='");
						condition.append(orderCode);
						condition.append("'");
					}
					if(packageCode != null && !packageCode.equals("")){
						condition.append(" and package_code='");
						condition.append(packageCode);
						condition.append("'");
					}
					if(storageTime != null && !storageTime.equals("")){
						String beginTime = storageTime + " 00:00:00";
						String endTime = storageTime + " 23:59:59";
						condition.append(" and storage_time between '");
						condition.append(beginTime);
						condition.append("' and '");
						condition.append(endTime);
						condition.append("'");
					}
					if(deliver != -1){
						condition.append(" and deliver=");
						condition.append(deliver);
					}
					if( wareArea == -1 ) {
						condition.append(" and area in (" + availAreaIds + ")");
					} else {
						condition.append(" and area = " + wareArea);
					}
				}else{
					condition.append("storage_status=0");
					if(orderCode!=null && !orderCode.equals("")){
						condition.append(" and order_code='");
						condition.append(orderCode);
						condition.append("'");
					}
					if(packageCode != null && !packageCode.equals("")){
						condition.append(" and package_code='");
						condition.append(packageCode);
						condition.append("'");
					}
					if(storageTime != null && !storageTime.equals("")){
						String beginTime = storageTime + " 00:00:00";
						String endTime = storageTime + " 23:59:59";
						condition.append(" and storage_time between '");
						condition.append(beginTime);
						condition.append("' and '");
						condition.append(endTime);
						condition.append("'");
					}
					if(deliver != -1){
						condition.append(" and deliver=");
						condition.append(deliver);
					}
					if( wareArea == -1 ) {
						condition.append(" and area in (" + availAreaIds + ")");
					} else {
						condition.append(" and area = " + wareArea);
					}
				}
			}
		}else{
			condition.append("storage_status in(1,2,0)");
			if(orderCode!=null && !orderCode.equals("")){
				condition.append(" and order_code='");
				condition.append(orderCode);
				condition.append("'");
			}
			if(packageCode != null && !packageCode.equals("")){
				condition.append(" and package_code='");
				condition.append(packageCode);
				condition.append("'");
			}
			if(storageTime != null && !storageTime.equals("")){
				String beginTime = storageTime + " 00:00:00";
				String endTime = storageTime + " 23:59:59";
				condition.append(" and storage_time between '");
				condition.append(beginTime);
				condition.append("' and '");
				condition.append(endTime);
				condition.append("'");
			}
			if(deliver != -1){
				condition.append(" and deliver=");
				condition.append(deliver);
			}
			if( wareArea == -1 ) {
				condition.append(" and area in (" + availAreaIds + ")");
			} else {
				condition.append(" and area = " + wareArea);
			}
		}
		return condition.toString();
	}
	
	public int getReturnedPackageCountDirectly(String sqlCount) {
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
	
	public List<ReturnedPackageBean> getReturnedPackageListDirectly(String sql, int index, int count, String orderBy) {
		List<ReturnedPackageBean> result = new ArrayList<ReturnedPackageBean>();
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return result;
		}
		
		if( orderBy != null && !orderBy.equals("")) {
			sql += " order by " + orderBy;
		}
		sql = DbOperation.getPagingQuery(sql, index, count);
		//System.out.println("query-sql-"+sql);
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				ReturnedPackageBean rp = new ReturnedPackageBean();
				rp.setId(rs.getInt("id"));
				rp.setOrderCode(rs.getString("order_code"));
				rp.setOrderId(rs.getInt("order_id"));
				rp.setPackageCode(rs.getString("package_code"));
				rp.setDeliver(rs.getInt("deliver"));
				rp.setOperatorId(rs.getInt("operator_id"));
				rp.setOperatorName(rs.getString("operator_name"));
				rp.setStorageTime(rs.getString("storage_time"));
				rp.setStorageStatus(rs.getInt("storage_status"));
				rp.setRemark(rs.getString("remark"));
				rp.setReasonId(rs.getInt("reason_id"));
				rp.setClaimsVerificationId(rs.getInt("claims_verification_id"));
				rp.setArea(rs.getInt("area"));
				rp.setStatus(rs.getInt("status"));
				rp.setImportTime(rs.getString("import_time"));
				rp.setImportUserName(rs.getString("import_user_name"));
				rp.setImportUserId(rs.getInt("import_user_id"));
				rp.setCheckDatetime(rs.getString("check_datetime")); //hp 6-4 增加复核时间
				result.add(rp);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	public XSSFWorkbook exportPackage( List<ReturnedPackageBean> list) throws Exception {

//		if (storageTime == null || storageTime.equals("")) {
//			storageTime = format.format(new Date());
//		}
		XSSFWorkbook workbook = new XSSFWorkbook();
		// 在Excel 工作簿中建一工作表
		XSSFSheet sheet = workbook.createSheet("退货包裹列表");

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
		// 设置单元格格式(文本)
		XSSFCellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("@"));

		// 在索引0的位置创建行（第一行）
		XSSFRow row = sheet.createRow(0);

		XSSFCell cell1 = row.createCell(0);// 第一列
		XSSFCell cell2 = row.createCell(1);
		XSSFCell cell3 = row.createCell(2);
		XSSFCell cell4 = row.createCell(3);
		XSSFCell cell5 = row.createCell(4);
		XSSFCell cell6 = row.createCell(5);
		XSSFCell cell7 = row.createCell(6);
		XSSFCell cell8 = row.createCell(7);
		XSSFCell cell9 = row.createCell(8);
		XSSFCell cell10 = row.createCell(9);
		XSSFCell cell11 = row.createCell(10);
		XSSFCell cell12 = row.createCell(11);
		XSSFCell cell13 = row.createCell(12);
		// 定义单元格为字符串类型
		cell1.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell2.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell3.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell4.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell5.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell6.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell7.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell8.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell9.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell10.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell11.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell12.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell13.setCellType(HSSFCell.CELL_TYPE_STRING);

		/*
		 * cell1.setEncoding(HSSFCell.ENCODING_UTF_16);
		 * cell2.setEncoding(HSSFCell.ENCODING_UTF_16);
		 * cell3.setEncoding(HSSFCell.ENCODING_UTF_16);
		 */
		// 在单元格中输入数据
		cell1.setCellValue("序号");
		cell2.setCellValue("订单编号");
		cell3.setCellValue("包裹单号");
		cell4.setCellValue("快递公司");
		cell5.setCellValue("入库地区");
		cell6.setCellValue("操作人");
		cell7.setCellValue("订单状态");
		cell8.setCellValue("导入时间");
		cell9.setCellValue("入库时间");
		cell10.setCellValue("理赔单");
		cell11.setCellValue("理赔状态");
		cell12.setCellValue("退回原因");
		cell13.setCellValue("复核时间");
		Map deliverMap = voOrder.deliverMapAll;
		int x = list.size();
		for( int i = 0 ; i < x;  i++ ) {
			row = sheet.createRow((int) i + 1);
			ReturnedPackageBean rpBean = list.get(i);
			// 1 序号
			XSSFCell cellc1 = row.createCell(0);
			cellc1.setCellStyle(cellStyle);
			cellc1.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc1.setCellValue(i + 1 +"");
			// 2 订单编号
			XSSFCell cellc2 = row.createCell(1);
			cellc2.setCellStyle(cellStyle);
			cellc2.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc2.setCellValue(rpBean.getOrderCode());
			// 3 包裹单号
			XSSFCell cellc3 = row.createCell(2);
			cellc3.setCellStyle(cellStyle);
			cellc3.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc3.setCellValue(rpBean.getPackageCode());
			// 4 快递公司
			XSSFCell cellc4 = row.createCell(3);
			cellc4.setCellStyle(cellStyle);
			cellc4.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc4.setCellValue(StringUtil.convertNull((String)deliverMap.get(String.valueOf(rpBean.getDeliver()))));
			// 5入库地区
			XSSFCell cellc5 = row.createCell(4);
			cellc5.setCellStyle(cellStyle);
			cellc5.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc5.setCellValue(StringUtil.convertNull((String)ProductStockBean.areaMap.get(rpBean.getArea())));
			// 6 入库人
			XSSFCell cellc6 = row.createCell(5);
			cellc6.setCellStyle(cellStyle);
			cellc6.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
		    cellc6.setCellValue(StringUtil.convertNull(rpBean.getOperatorName()));
		    
		    // 7 订单状态
		    XSSFCell cellc7 = row.createCell(6);
		    cellc7.setCellStyle(cellStyle);
		    cellc7.setCellType(HSSFCell.CELL_TYPE_STRING);
		    // cell.setEncoding();
		    cellc7.setCellValue(StringUtil.convertNull(rpBean.getOrderStatusName()));
		    // 8 导入时间
		    XSSFCell cellc8 = row.createCell(7);
		    cellc8.setCellStyle(cellStyle);
		    cellc8.setCellType(HSSFCell.CELL_TYPE_STRING);
		    // cell.setEncoding();
		    cellc8.setCellValue(StringUtil.convertNull(rpBean.getImportTime()).equals("") ? "" : StringUtil.convertNull(rpBean.getImportTime()).substring(0,19));
			// 9 入库时间
			XSSFCell cellc9 = row.createCell(8);
			cellc9.setCellStyle(cellStyle);
			cellc9.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc9.setCellValue(StringUtil.convertNull(rpBean.getStorageTime()).equals("") ? "" : StringUtil.convertNull(rpBean.getStorageTime()).substring(0,19));
			//10 理赔单
			XSSFCell cellc10 = row.createCell(9);
			cellc10.setCellStyle(cellStyle);
			cellc10.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc10.setCellValue( rpBean.getClaimsVerificationBean() == null ? "" : rpBean.getClaimsVerificationBean().getCode());
			
			// 11理赔状态
			XSSFCell cellc11 = row.createCell(10);
			cellc11.setCellStyle(cellStyle);
			cellc11.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc11.setCellValue(rpBean.getClaimsVerificationBean() == null ? "" : rpBean.getClaimsVerificationBean().getStatusName());
			// 12退回原因
			XSSFCell cellc12 = row.createCell(11);
			cellc12.setCellStyle(cellStyle);
			cellc12.setCellType(HSSFCell.CELL_TYPE_STRING);
			// cell.setEncoding();
			cellc12.setCellValue(rpBean.getReturnsReasonBean() == null ? "" : rpBean.getReturnsReasonBean().getReason());
			// 12   复核时间
						XSSFCell cellc13 = row.createCell(12);
						cellc13.setCellStyle(cellStyle);
						cellc13.setCellType(HSSFCell.CELL_TYPE_STRING);
						// cell.setEncoding();
						cellc13.setCellValue(StringUtil.convertNull(rpBean.getCheckDatetime()).equals("") ? "" : StringUtil.convertNull(rpBean.getCheckDatetime()).substring(0,19));
		}
		return workbook;
	}

	public boolean lockCargoProductSpaceStock(
			List preCargoInfo, List curCargoInfo, List nextCargoInfo, ICargoService service) {
		
		CargoInfoModelBean cModelBean = null;
		CargoInfoBean cargoInfo = null;
		if(preCargoInfo != null && !preCargoInfo.isEmpty()){
			for(int i=0; i<preCargoInfo.size(); i++){
				cModelBean = (CargoInfoModelBean) preCargoInfo.get(i);
				cargoInfo = service.getCargoInfo("id=" + cModelBean.getId());
				if(cargoInfo == null){
					System.out.println("目的货位不存在!");
					return false;
				}
				if(cargoInfo.getStatus()==CargoInfoBean.STATUS0){
					if(cModelBean.getCount() > 0){
						if(!service.updateCargoSpaceLockCount(cModelBean.getId(), cModelBean.getCount())){
							return false;
						}
					}
				}else if(cargoInfo.getStatus()==CargoInfoBean.STATUS1 || cargoInfo.getStatus()==CargoInfoBean.STATUS2){
					//更新目的货位为使用中
					service.updateCargoInfo("status="+CargoInfoBean.STATUS0, "id="+cargoInfo.getId());
					//关联商品和货位
					CargoProductStockBean cps = new CargoProductStockBean();
					cps.setCargoId(cargoInfo.getId());
					cps.setProductId(cModelBean.getProductId());
					cps.setStockCount(0);
					cps.setStockLockCount(0);
					service.addCargoProductStock(cps);
				}else{
					System.out.println("目的货位已删除");
					return false;
				}
			}
		}
		
		if(nextCargoInfo != null && !nextCargoInfo.isEmpty()){
			for(int i=0; i<nextCargoInfo.size(); i++){
				cModelBean = (CargoInfoModelBean) nextCargoInfo.get(i);
				cargoInfo = service.getCargoInfo("id=" + cModelBean.getId());
				if(cargoInfo == null){
					System.out.println("目的货位不存在!");
					return false;
				}
				if(cargoInfo.getStatus()==CargoInfoBean.STATUS0){
					if(cModelBean.getCount() > 0){
						if(!service.updateCargoSpaceLockCount(cModelBean.getId(), cModelBean.getCount())){
							return false;
						}
					}
				}else if(cargoInfo.getStatus()==CargoInfoBean.STATUS1 || cargoInfo.getStatus()==CargoInfoBean.STATUS2){
					//更新目的货位为使用中
					service.updateCargoInfo("status="+CargoInfoBean.STATUS0, "id="+cargoInfo.getId());
					//关联商品和货位
					CargoProductStockBean cps = new CargoProductStockBean();
					cps.setCargoId(cargoInfo.getId());
					cps.setProductId(cModelBean.getProductId());
					cps.setStockCount(0);
					cps.setStockLockCount(0);
					service.addCargoProductStock(cps);
					if(!service.updateCargoSpaceLockCount(cModelBean.getId(), cModelBean.getCount())){
						return false;
					}
				}else{
					System.out.println("目的货位已删除");
					return false;
				}
			}
		}
		
		if(curCargoInfo != null && !curCargoInfo.isEmpty()){
			for(int i=0; i<curCargoInfo.size(); i++){
				cModelBean = (CargoInfoModelBean) curCargoInfo.get(i);
				cargoInfo = service.getCargoInfo("id=" + cModelBean.getId());
				if(cargoInfo == null){
					System.out.println("目的货位不存在!");
					return false;
				}
				if(cargoInfo.getStatus()==CargoInfoBean.STATUS0){
					if(cModelBean.getCount() > 0){
						if(!service.updateCargoSpaceLockCount(cModelBean.getId(), cModelBean.getCount())){
							return false;
						}
					}
				}else if(cargoInfo.getStatus()==CargoInfoBean.STATUS1 || cargoInfo.getStatus()==CargoInfoBean.STATUS2){
					//更新目的货位为使用中
					service.updateCargoInfo("status="+CargoInfoBean.STATUS0, "id="+cargoInfo.getId());
					//关联商品和货位
					CargoProductStockBean cps = new CargoProductStockBean();
					cps.setCargoId(cargoInfo.getId());
					cps.setProductId(cModelBean.getProductId());
					cps.setStockCount(0);
					cps.setStockLockCount(0);
					service.addCargoProductStock(cps);
					if(!service.updateCargoSpaceLockCount(cModelBean.getId(), cModelBean.getCount())){
						return false;
					}
				}else{
					System.out.println("目的货位已删除");
					return false;
				}
			}
		}
		return true;
	}

	public void constructSourceInfo(DbOperation dbop, List sourceCargoList, int newWareArea, int productId)
			throws Exception {
		
		ResultSet rs;
		//不需要散件区和使用中条件
		String querySql = "select cps.product_id,ci.whole_code," +
				"ci.id, cps.id as cid, cps.stock_count, cps.stock_lock_count from cargo_product_stock cps, cargo_info ci " +
				"where cps.cargo_id=ci.id " +
				"and ci.area_id=" + newWareArea + " " +
				"and cps.product_id=" + productId + " " +
				"and ci.stock_type="+CargoInfoBean.STOCKTYPE_RETURN;
		
		rs = dbop.executeQuery(querySql);
		CargoInfoModelBean cargoInfo = null;
		while(rs.next()){
			cargoInfo = new CargoInfoModelBean();
			cargoInfo.setProductId(rs.getInt("product_id"));
			cargoInfo.setWholeCode(rs.getString("whole_code"));
			cargoInfo.setSourceStockCount(rs.getInt("stock_count"));
			cargoInfo.setSourceLockCount(rs.getInt("stock_lock_count"));
			cargoInfo.setId(rs.getInt("id"));
			cargoInfo.setCargoStockId(rs.getInt("cid"));
			sourceCargoList.add(cargoInfo);
		}
		if(rs != null){
			rs.close();
		}
	}
	
	public HashMap getReturnedSaleOutProductIds(String availAreaIds, int wareArea){
		HashMap saleOutMap = new HashMap();
		
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return saleOutMap;
		}
		ResultSet rs = null;
		String sql = "select p.id from product_stock ps, product p where ps.stock > 0 and (ps.product_id not in ( select distinct (cps.product_id) from cargo_info ci, cargo_product_stock cps where ci.id = cps.cargo_id and ci.area_id = 3 and " + 
		"ci.stock_type = 0 and ci.store_type in (0,4) ) or (ps.product_id = p.id and p.status = 100)) and ps.area = 3 and ps.type = 4 and p.id = ps.product_id";
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				saleOutMap.put( new Integer(rs.getInt("id")),"");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		
		return saleOutMap;
		
	}


	public void updateRetProductCount(DbOperation dbop, List preCargoInfo,
			List curCargoInfo, List nextCargoInfo) throws Exception {
		String updateSql = "update returned_product set count=count-? where product_id=? and type=1 and count>=?";
		if(preCargoInfo != null && !preCargoInfo.isEmpty()){
			for(int i=0; i<preCargoInfo.size(); i++){
				executUpdateRet(dbop, preCargoInfo, updateSql, i);
			}
		}
		if(nextCargoInfo != null && !nextCargoInfo.isEmpty()){
			for(int i=0; i<nextCargoInfo.size(); i++){
				executUpdateRet(dbop, nextCargoInfo, updateSql, i);
			}
		}
		if(curCargoInfo != null && !curCargoInfo.isEmpty()){
			for(int i=0; i<curCargoInfo.size(); i++){
				executUpdateRet(dbop, curCargoInfo, updateSql, i);
			}
		}
		
	}

	private void executUpdateRet(DbOperation dbop, List preCargoInfo,
			String updateSql, int i) throws SQLException, Exception {
		PreparedStatement ps;
		CargoInfoModelBean cargoBean;
		dbop.prepareStatement(updateSql);
		ps = dbop.getPStmt();
		cargoBean = (CargoInfoModelBean) preCargoInfo.get(i);
		ps.setInt(1, cargoBean.getCount());
		ps.setInt(2, cargoBean.getProductId());
		ps.setInt(3, cargoBean.getCount());
		if(ps.executeUpdate()<=0){
			throw new RuntimeException("更新退货商品表失败");
		}
		updateRpcInfo(cargoBean, dbop);
		if(ps != null){
			ps.close();
		}
	}


	private void updateRpcInfo(CargoInfoModelBean cargoBean, DbOperation dbop) throws Exception {
		
		ReturnedProductCargoBean rpc;
		PreparedStatement ps = null;
		List rpcList = new ArrayList();
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("product_id=");
		strBuilder.append(cargoBean.getProductId());
		strBuilder.append(" and cargo_id=");
		strBuilder.append(cargoBean.getId());
		String updateRpcSql = "update returned_product_cargo set count=count-? where id=?";
		try{
			StatService service = new StatService(IBaseService.CONN_IN_SERVICE, dbop);
			rpcList = service.getReturnedProductCargoList(strBuilder.toString(),0,-1,null);
			if(rpcList != null && !rpcList.isEmpty()){
				for(int j=0; j<rpcList.size(); j++){
					rpc = (ReturnedProductCargoBean)rpcList.get(j);
					if(rpc.getCount()==cargoBean.getCount()){
						if(!service.deleteReturnedProductCargo("id=" + rpc.getId())){
							throw new RuntimeException("删除returned_product_cargo失败");
						}
					}else if(rpc.getCount()>=cargoBean.getCount()){
						dbop.prepareStatement(updateRpcSql);
						ps = dbop.getPStmt();
						ps.setInt(1, cargoBean.getCount());
						ps.setInt(2, rpc.getId());
						if(ps.executeUpdate()<=0){
							throw new RuntimeException("更新returned_product_cargo失败");
						}
					}
				}
			}
		}finally{
			if(ps != null){
				ps.close();
			}
		}
	}

	public boolean lockProductStock(DbOperation dbop, List preCargoInfo,
			List curCargoInfo, List nextCargoInfo) throws Exception {
		PreparedStatement ps;
		CargoInfoModelBean cargoBean;
		String updateRetCarSql = "update product_stock set " +
				"lock_count=lock_count+?, stock=stock-? " +
				"where status=0 and product_id=? and stock>=?" +
				" and area="+ProductStockBean.AREA_ZC + " and type="+ProductStockBean.STOCKTYPE_RETURN;
		if(preCargoInfo != null && !preCargoInfo.isEmpty()){
			for(int i=0; i<preCargoInfo.size(); i++){
				dbop.prepareStatement(updateRetCarSql);
				ps = dbop.getPStmt();
				cargoBean = (CargoInfoModelBean) preCargoInfo.get(i);
				if(cargoBean.getCount() > 0){
					ps.setInt(1, cargoBean.getCount());
					ps.setInt(2, cargoBean.getCount());
					ps.setInt(3, cargoBean.getProductId());
					ps.setInt(4, cargoBean.getCount());
					if(ps.executeUpdate()<=0){
						if(ps != null){
							ps.close();
						}
						return false;
					}
				}
			}
		}
		if(nextCargoInfo != null && !nextCargoInfo.isEmpty()){
			for(int i=0; i<nextCargoInfo.size(); i++){
				dbop.prepareStatement(updateRetCarSql);
				ps = dbop.getPStmt();
				cargoBean = (CargoInfoModelBean) nextCargoInfo.get(i);
				if(cargoBean.getCount() > 0){
					ps.setInt(1, cargoBean.getCount());
					ps.setInt(2, cargoBean.getCount());
					ps.setInt(3, cargoBean.getProductId());
					ps.setInt(4, cargoBean.getCount());
					if(ps.executeUpdate()<=0){
						if(ps != null){
							ps.close();
						}
						return false;
					}
				}
			}
		}
		if(curCargoInfo != null && !curCargoInfo.isEmpty()){
			for(int i=0; i<curCargoInfo.size(); i++){
				dbop.prepareStatement(updateRetCarSql);
				ps = dbop.getPStmt();
				cargoBean = (CargoInfoModelBean) curCargoInfo.get(i);
				if(cargoBean.getCount() > 0){
					ps.setInt(1, cargoBean.getCount());
					ps.setInt(2, cargoBean.getCount());
					ps.setInt(3, cargoBean.getProductId());
					ps.setInt(4, cargoBean.getCount());
					if(ps.executeUpdate()<=0){
						if(ps != null){
							ps.close();
						}
						return false;
					}
				}
			}
		}
		return true;
	}

	public boolean lockCargoProductStock(List preCargoInfo,
			List curCargoInfo, List nextCargoInfo, ICargoService service) throws Exception {
		CargoInfoModelBean cargoBean;
		if(preCargoInfo != null && !preCargoInfo.isEmpty()){
			for(int i=0; i<preCargoInfo.size(); i++){
				cargoBean = (CargoInfoModelBean) preCargoInfo.get(i);
				if(cargoBean.getCount() > 0){
					if(!service.updateCargoProductStockCount(cargoBean.getCargoStockId(),-cargoBean.getCount())){
						return false;
					}
					
					if(!service.updateCargoProductStockLockCount(cargoBean.getCargoStockId(), cargoBean.getCount())){
						return false;
					}
				}
			}
		}
		if(nextCargoInfo != null && !nextCargoInfo.isEmpty()){
			for(int i=0; i<nextCargoInfo.size(); i++){
				cargoBean = (CargoInfoModelBean) nextCargoInfo.get(i);
				if(cargoBean.getCount()>0){
					if(!service.updateCargoProductStockCount(cargoBean.getCargoStockId(),-cargoBean.getCount())){
						return false;
					}
					
					if(!service.updateCargoProductStockLockCount(cargoBean.getCargoStockId(), cargoBean.getCount())){
						return false;
					}
				}
			}
		}
		if(curCargoInfo != null && !curCargoInfo.isEmpty()){
			for(int i=0; i<curCargoInfo.size(); i++){
				cargoBean = (CargoInfoModelBean) curCargoInfo.get(i);
				if(cargoBean.getCount() > 0){
					if(!service.updateCargoProductStockCount(cargoBean.getCargoStockId(),-cargoBean.getCount())){
						return false;
					}
					
					if(!service.updateCargoProductStockLockCount(cargoBean.getCargoStockId(), cargoBean.getCount())){
						return false;
					}
				}
			}
		}
		return true;
	}

	
	private String addCargoOperationCargo(CargoInfoModelBean cargoBean,
			ICargoService cargoService, voUser user, CargoOperationBean carOpBean) throws Exception {
		
		CargoOperationCargoBean cocBean = new CargoOperationCargoBean();
		if(cargoBean.getCount() > 0){
			cocBean.setProductId(cargoBean.getProductId());
			cocBean.setOperId(carOpBean.getId());
			cocBean.setInCargoProductStockId(cargoBean.getInCargoStockId());//目标货位库存id
			cocBean.setInCargoWholeCode(cargoBean.getWholeCode());
			cocBean.setOutCargoProductStockId(cargoBean.getCargoStockId());//源货位库存id
			cocBean.setOutCargoWholeCode(cargoBean.getSourceWholeCode());
			cocBean.setStockCount(cargoBean.getCount());
			cocBean.setType(CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE);
			cocBean.setUseStatus(CargoOperationCargoBean.COC_WITH_INCARGOINFO_STATUS);
			if(!cargoService.addCargoOperationCargo(cocBean)){
				return "添加上架单详细信息失败";
			}
			
			//兼容之前，只记录源货位
			cocBean = new CargoOperationCargoBean();
//			cargoBean = (CargoInfoModelBean) cargoInfoList.get(i);
			cocBean.setProductId(cargoBean.getProductId());
			cocBean.setOperId(carOpBean.getId());
			cocBean.setOutCargoProductStockId(cargoBean.getCargoStockId());//源货位库存id
			cocBean.setOutCargoWholeCode(cargoBean.getSourceWholeCode());
			cocBean.setStockCount(cargoBean.getCount());
			cocBean.setType(CargoOperationCargoBean.COC_UNWITH_INCARGOINFO_TYPE);
			cocBean.setUseStatus(CargoOperationCargoBean.COC_UNWITH_INCARGOINFO_STATUS);
			if(!cargoService.addCargoOperationCargo(cocBean)){
				return "添加上架单详细信息失败";
			}
			
			CargoOperationLogBean logBean=new CargoOperationLogBean();
			logBean.setOperId(cargoBean.getProductId());
			logBean.setOperDatetime(DateUtil.getNow());
			logBean.setOperAdminId(user.getId());
			logBean.setOperAdminName(user.getUsername());
			StringBuilder logRemark = new StringBuilder("审核退货上架单和退货上架汇总单成功，商品：");
			logRemark.append(cargoBean.getProductCode());
			logRemark.append("，");
			logRemark.append("源货位（");
			logRemark.append(cargoBean.getSourceWholeCode());
			logRemark.append("）");
			logRemark.append("，目的货位（");
			logRemark.append(cargoBean.getWholeCode());
			logRemark.append("），");
			logRemark.append("上架量（");
			logRemark.append(cargoBean.getCount());
			logRemark.append("）");
			logBean.setRemark(logRemark.toString());
			if(!cargoService.addCargoOperationLog(logBean)){
				return "添加操作记录失败";
			}
		}else{
			return "商品： "+cargoBean.getProductCode()+" 上架量为0，不允许上架！";
		}
		return null;
	}

	
	
	
	private boolean dealRetProductCargo(int productId) {
		
		IProductStockService ispService = new ProductStockServiceImpl(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		ProductStockBean psBean = ispService.getProductStock(
				"area="+ProductStockBean.AREA_ZC+" and type="+ProductStockBean.STOCKTYPE_RETURN+" and product_id="+productId);
		if(psBean == null || psBean.getStock()==0){
			if(!statService.deleteReturnedProductCargo("product_id="+productId)){
				return false;
			}
		}
		return true;
	}

//	public CargoInfoModelBean getCargInfoModelById(int cargoOperationId) {
//		
//		DbOperation dbop = null;
//		try{
//			dbop = new DbOperation(DbOperation.DB);
//			ICargoService cargoService = ServiceFactory.createCargoService(
//										IBaseService.CONN_IN_SERVICE, dbop); 
//			StatService statService = new StatService(IBaseService.CONN_IN_SERVICE,dbop);
//			System.out.println(cargoOperationId);
//			CargoOperationBean cargoBean = cargoService.getCargoOperation("id=" + cargoOperationId);
//			List cargoOperationCargoList = cargoService.getCargoOperationCargoList(
//					"oper_id=" + cargoBean.getId() +
//							" and in_cargo_whole_code is not null", 0, -1, null);
//			CargoOperationCargoBean cargoOpBean = null;
//			String wholeCode = null;
//			List passageCodeList = new LinkedList();
//			Map cargoInfoMap = new HashMap();
//			ReturnedProductBean retProduct = null;
//			for(int i=0; i<cargoOperationCargoList.size();i++){
//				cargoOpBean = (CargoOperationCargoBean) cargoOperationCargoList.get(i);
//				wholeCode = cargoOpBean.getInCargoWholeCode();
//				int index = wholeCode.indexOf("-");
//				String passageCode = wholeCode.substring(index+1, index+4);
//				insertPassageCode(passageCodeList, passageCode);
//				if(cargoInfoMap.isEmpty()){
//					List cargoList = new ArrayList();
//					retProduct = statService.getReturnedProduct(
//							"product_id=" + cargoOpBean.getProductId() +" and type=1");
//					retProduct.setInCargoWholeCode(cargoOpBean.getInCargoWholeCode());
//					retProduct.setCount(cargoOpBean.getStockCount());
//					retProduct.setCompleteCount(cargoOpBean.getCompleteCount());
//					retProduct.setCargoOperationCargoId(cargoOpBean.getId());
//					cargoList.add(retProduct);
//					cargoInfoMap.put(passageCode, cargoList);
//				}else if(cargoInfoMap.get(passageCode) != null){
//					retProduct =statService.getReturnedProduct(
//							"product_id=" + cargoOpBean.getProductId() +" and type=1");
//					retProduct.setInCargoWholeCode(cargoOpBean.getInCargoWholeCode());
//					retProduct.setCount(cargoOpBean.getStockCount());
//					retProduct.setCompleteCount(cargoOpBean.getCompleteCount());
//					retProduct.setCargoOperationCargoId(cargoOpBean.getId());
//					((List)cargoInfoMap.get(passageCode)).add(retProduct);
//				}else{
//					List cargoList = new ArrayList();
//					retProduct =statService.getReturnedProduct(
//							"product_id=" + cargoOpBean.getProductId() +" and type=1");
//					retProduct.setInCargoWholeCode(cargoOpBean.getInCargoWholeCode());
//					retProduct.setCount(cargoOpBean.getStockCount());
//					retProduct.setCompleteCount(cargoOpBean.getCompleteCount());
//					retProduct.setCargoOperationCargoId(cargoOpBean.getId());
//					cargoList.add(retProduct);
//					cargoInfoMap.put(passageCode, cargoList);
//				}
//			}
//			CargoInfoModelBean cargoModelBean = new CargoInfoModelBean();
//			cargoModelBean.setAuditor(cargoBean.getAuditingUserName());
//			if(cargoBean.getAuditingDatetime() != null){
//				cargoModelBean.setAuditorDate(cargoBean.getAuditingDatetime().substring(0,19));
//			}
//			if(cargoBean.getCreateDatetime() != null){
//				cargoModelBean.setCargoOpDate(cargoBean.getCreateDatetime().substring(0, 19));
//			}
//			cargoModelBean.setCargoOpmaker(cargoBean.getCreateUserName());
//			cargoModelBean.setOperationCode(cargoBean.getCode());
//			CargoOperationProcessBean coprocess = (CargoOperationProcessBean)cargoService.getCargoOperationProcess(
//					"id=" + cargoBean.getStatus());
//			cargoModelBean.setStatus(cargoBean.getStatus());
//			if(coprocess != null){
//				cargoModelBean.setOperationStatus(coprocess.getStatusName());
//			}else{
//				cargoModelBean.setOperationStatus("");
//			}
//			cargoModelBean.setPassageProduct(cargoInfoMap);
//			cargoModelBean.setPassageCode(passageCodeList);
//			cargoModelBean.setPstrCode(getpStrCode(passageCodeList));
//			if(!cargoOperationCargoList.isEmpty()){
//				cargoModelBean.setSourceWholeCode(
//						((CargoOperationCargoBean)cargoOperationCargoList.get(0)).getOutCargoWholeCode());
//			}
//			cargoModelBean.setId(cargoBean.getId());
//			return cargoModelBean;
//		}catch(Exception e){
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}finally{
//			if(dbop != null){
//				dbop.release();
//			}
//		}
//	}

	
	
	private String getpStrCode(List passageCodeList) {
		
		StringBuilder strBuilder = new StringBuilder();
		for(Iterator it = passageCodeList.iterator(); it.hasNext();){
			if(strBuilder.length()>0){
				strBuilder.append(";");
			}
			strBuilder.append(it.next());
		}
		return strBuilder.toString();
	}

	
	
	public String confirmRetShelf(String retShelfCode, voUser user) {
		
		
		DbOperation dbop = null;
		try{
			dbop = new DbOperation(DbOperation.DB);
			dbop.startTransaction();//需要连接检查
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbop);
			StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, dbop);
			ReturnedUpShelfBean rufBean = statService.getReturnedUpShelf("code='"+StringUtil.toSql(retShelfCode)+"'");
			if(rufBean == null){
				return "4";//汇总单不存在
			}
			
			if(rufBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS37){
				return "6";
			}
			
			if(rufBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS39){
				return "2";//作业单已审核
			}
			
			if(rufBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS43
					|| rufBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS45
					|| rufBean.getStatus()==ReturnedUpShelfBean.OPERATION_STATUS46){
				return "5";
			}
			
			List cargoOperationList = service.getCargoOperationList(
					"source='"+StringUtil.toSql(retShelfCode)
					+"' and status="+CargoOperationProcessBean.OPERATION_STATUS2
					+" and effect_status!="+CargoOperationBean.EFFECT_STATUS4, 0, -1, null);
			//rufBean.getPassageWholeCode(); // 很可能要根据这个来了
			//---------RightBlock 怎么得到对应的上架单的地区
			CargoOperationBean coBean = null;
			for(int i=0; i<cargoOperationList.size(); i++){
				coBean = (CargoOperationBean) cargoOperationList.get(i);
				
				if(!service.updateCargoOperation(
						"status="+CargoOperationProcessBean.OPERATION_STATUS3
						+",auditing_datetime='"+dateFormat.format(new Date()) + "'"
						+",auditing_user_name='"+user.getUsername()+"'"
						+",auditing_user_id="+user.getId()
						+",last_operate_datetime='"+DateUtil.getNow()+"'", 
						"id=" + coBean.getId())){
					dbop.rollbackTransaction();
					return "1";
				}
				
				CargoOperationLogBean logBean=new CargoOperationLogBean();
				logBean.setOperId(coBean.getId());
				logBean.setOperDatetime(DateUtil.getNow());
				logBean.setOperAdminId(user.getId());
				logBean.setOperAdminName(user.getUsername());
				logBean.setRemark("审核确认完成");
				if(!service.addCargoOperationLog(logBean)){
					throw new RuntimeException("添加操作记录失败");
				}
			}
			if(!statService.updateReturnedUpShelf(
					"status="+ReturnedUpShelfBean.OPERATION_STATUS39
					+",auditing_datetime='"+dateFormat.format(new Date()) + "'"
					+",auditing_user_name='"+user.getUsername()+"'"
					+",auditing_user_id="+user.getId(), 
					"id="+rufBean.getId())){
				dbop.rollbackTransaction();
				return "1";
			}
			dbop.commitTransaction();
			return "0";
		}catch(Exception e){
			if(dbop!=null){
				dbop.rollbackTransaction();
			}
			e.printStackTrace();
			return "1";
		}finally{
			if(dbop != null){
				dbop.release();
			}
		}
	}

	
	
//	public boolean updateProductStock(int productId, int stockCount, DbOperation dbop) throws Exception {
//		
//		
//		PreparedStatement ps = null;
//		String updateRetCarSql = "update product_stock set " +
//				"lock_count=lock_count-? where product_id=? " +
//				"and lock_count>=? and area="+ProductStockBean.AREA_ZC + " and type="+ProductStockBean.STOCKTYPE_RETURN;
//		try{
//			dbop.prepareStatement(updateRetCarSql);
//			ps = dbop.getPStmt();
//			ps.setInt(1, stockCount);
//			ps.setInt(2, productId);
//			ps.setInt(3, stockCount);
//			if(ps.executeUpdate()<=0){
//				return false;
//			}
//			return true;
//		}finally{
//			if(ps != null){
//				ps.close();
//			}
//		}
//	}

	
	
	public String getRetProductCode(List unScanningList, DbOperation dbOperation)throws Exception {
		
		StringBuilder unScanningCode = new StringBuilder();
		StringBuilder unScanningId = new StringBuilder();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			for(int i=0; i<unScanningList.size(); i++){
				if(unScanningId.length()>0){
					unScanningId.append(",");
				}
				unScanningId.append(unScanningList.get(i));
			}
			String query = "select product_code from returned_product where product_id in("
							+ unScanningId.toString() +") and type=1";
			dbOperation.prepareStatement(query);
			ps = dbOperation.getPStmt();
			rs = ps.executeQuery();
			while(rs.next()){
				if(unScanningCode.length()>0){
					unScanningCode.append(";");
				}
				unScanningCode.append(rs.getString("product_code"));
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}finally{
			if(ps != null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
		return unScanningCode.toString();
	}


	//作业失败
	public String rollbackRetShelf(String upShelfCode, voUser user, WareService wareService) {
		
		try{
			
			this.getDbOp().startTransaction();
			ICargoService cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE, this.getDbOp());
			StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
			
			
			ReturnedUpShelfBean rufBean = statService.getReturnedUpShelf("code='"+StringUtil.toSql(upShelfCode)+"'");
			if(rufBean == null){
				return "10";
			}
			
			//是否存在作业成功任务
			int successCount = cargoService.getCargoOperationCount("source='"+StringUtil.toSql(upShelfCode)+"' and status="+CargoOperationProcessBean.OPERATION_STATUS7);
			
			
			//已审核任务
			List cargoOperationList = cargoService.getCargoOperationList(
					"source='"+StringUtil.toSql(upShelfCode)
					+"' and status="+CargoOperationProcessBean.OPERATION_STATUS3
					+" and effect_status!="+CargoOperationBean.EFFECT_STATUS4, 0, -1, null);
//			if(cargoOperationList == null || cargoOperationList.isEmpty()){
//				return "1";//作业单不存在
//			}
			
			if(cargoOperationList != null){
				CargoOperationBean coBean = null;
				for(int j=0; j<cargoOperationList.size(); j++){
					//判断作业单是否存在
					coBean = (CargoOperationBean) cargoOperationList.get(j);
					
					//获取作业单下所有cargooperationcargo信息
					List cargoOpList = cargoService.getCargoOperationCargoList("oper_id=" + coBean.getId()+" and type=0", 0, -1, null);
					if(cargoOpList == null || cargoOpList.isEmpty()){
						return "2";//作业单下没有详细信息
					}
					
					CargoOperationCargoBean cocBean = null;
					for(int i=0; i<cargoOpList.size(); i++){
						
						//更新退货商品表合格商品数
						cocBean = (CargoOperationCargoBean) cargoOpList.get(i);
						
						//回滚退货商品表和returned_product_cargo表商品合格量
						if(!rollbackRetProductCount(cocBean, cargoService)){
							this.getDbOp().rollbackTransaction();
							return "3";//回滚退货商品表失败
						}
						
						
						//回滚退货库库存
						if(!rollbackProductStock(cocBean)){
							this.getDbOp().rollbackTransaction();
							return "4";//回滚退货库库存失败
						};
						
						//回滚退货(源货位)货位库存
						if(!rollbackCargoProductStock(cocBean, cargoService)){
							this.getDbOp().rollbackTransaction();
							return "5";
						}
						
						
						//回滚目标货位空间锁定量
//						if(!rollbackCargoProductSpaceStock(cocBean, cargoService)){
//							this.getDbOp().rollbackTransaction();
//							return "6";
//						}
						CargoOperationLogBean logBean=new CargoOperationLogBean();
						logBean.setOperId(coBean.getId());
						logBean.setOperDatetime(DateUtil.getNow());
						logBean.setOperAdminId(user.getId());
						logBean.setOperAdminName(user.getUsername());
						StringBuilder logRemark = new StringBuilder("回滚：商品");
						logRemark.append(wareService.getProductCode(cocBean.getProductId()+""));
						logRemark.append("，目的货位（");
						logRemark.append(cocBean.getOutCargoWholeCode());
						logRemark.append("），");
						logRemark.append("回滚量（");
						logRemark.append(cocBean.getStockCount()-cocBean.getCompleteCount());
						logRemark.append("）");
						logBean.setRemark(logRemark.toString());
						if(!cargoService.addCargoOperationLog(logBean)){
							this.getDbOp().rollbackTransaction();
							return "8";//添加操作记录失败
						}
						
					}
					//更新作业单状态
					if(!cargoService.updateCargoOperation(
							"status=" + CargoOperationProcessBean.OPERATION_STATUS9
							+",complete_datetime='"+DateUtil.getNow()+"'"
							+",complete_user_name='"+user.getUsername()+"'"
							+",complete_user_id="+user.getId()
							+",last_operate_datetime='"+DateUtil.getNow()+"'", "id="+coBean.getId())){
						this.getDbOp().rollbackTransaction();
						return "9";//更新作业单状态失败
					}
				}
			}
			if(successCount==0){
				if(!statService.updateReturnedUpShelf(
						"status="+ReturnedUpShelfBean.OPERATION_STATUS45
						+",complete_datetime='"+DateUtil.getNow()+"'"
						+",complete_user_name='"+user.getUsername()+"'"
						+",complete_user_id="+user.getId(), "id="+rufBean.getId())){
					this.getDbOp().rollbackTransaction();
					return "11";
				}
			}else{
				if(!statService.updateReturnedUpShelf(
						"status="+ReturnedUpShelfBean.OPERATION_STATUS46
						+",complete_datetime='"+DateUtil.getNow()+"'"
						+",complete_user_name='"+user.getUsername()+"'"
						+",complete_user_id="+user.getId(), "id="+rufBean.getId())){
					this.getDbOp().rollbackTransaction();
					return "11";
				}
			}
			this.getDbOp().commitTransaction();
			return "0";
		}catch(Exception e){
			this.getDbOp().rollbackTransaction();
			throw new RuntimeException(e);
		}
	}

	
	private boolean rollbackCargoProductSpaceStock(
			CargoOperationCargoBean cocBean, ICargoService cargoService) {

		CargoInfoBean coBean = cargoService.getCargoInfo("whole_code='"+cocBean.getInCargoWholeCode()+"'");
		if(!cargoService.updateCargoSpaceLockCount(
				coBean.getId(), -(cocBean.getStockCount()-cocBean.getCompleteCount()))){
			return false;
		}
		return true;
	}
	
	
	private boolean rollbackCargoProductStock(CargoOperationCargoBean cocBean,
			ICargoService cargoService) {
		
		if(!cargoService.updateCargoProductStockCount(
				cocBean.getOutCargoProductStockId(), cocBean.getStockCount()-cocBean.getCompleteCount())){
			return false;
		}
		
		if(!cargoService.updateCargoProductStockLockCount(
				cocBean.getOutCargoProductStockId(), -(cocBean.getStockCount()-cocBean.getCompleteCount()))){
			return false;
		}
		return true;
	}

	private boolean rollbackProductStock(CargoOperationCargoBean cocBean) throws SQLException {
		
		PreparedStatement ps = null;
		try{
			String updateSql = "update product_stock set " +
					"lock_count=lock_count-?, stock=stock+? " +
					"where status=0 and product_id=? and lock_count>=?" +
					" and area="+ProductStockBean.AREA_ZC + " and type="+ProductStockBean.STOCKTYPE_RETURN;
			this.getDbOp().prepareStatement(updateSql);
			ps = this.getDbOp().getPStmt();
			ps.setInt(1, cocBean.getStockCount()-cocBean.getCompleteCount());
			ps.setInt(2, cocBean.getStockCount()-cocBean.getCompleteCount());
			ps.setInt(3, cocBean.getProductId());
			ps.setInt(4, cocBean.getStockCount());
			if(ps.executeUpdate()<=0){
				return false;
			}
			return true;
		}finally{
			if(ps != null){
				ps.close();
			}
		}
	}

	public boolean rollbackRetProductCount(CargoOperationCargoBean cocBean, ICargoService cargoService) {
		
		
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
		
		CargoInfoBean cargoBean = cargoService.getCargoInfo("whole_code='"+cocBean.getInCargoWholeCode()+"'");
		ReturnedProductCargoBean rpcBean = statService.getReturnedProductCargo("product_id="+cocBean.getProductId()+" and cargo_id="+cargoBean.getId());
		if(rpcBean==null){
			rpcBean = new ReturnedProductCargoBean();
			rpcBean.setCargoId(cargoBean.getId());
			rpcBean.setCount(cocBean.getStockCount()-cocBean.getCompleteCount());
			rpcBean.setProductId(cocBean.getProductId());
			statService.addReturnedProductCargo(rpcBean);
		}else{
			statService.updateReturnedProductCargo("count=count+" + (cocBean.getStockCount()-cocBean.getCompleteCount()),
					"product_id="+cocBean.getProductId()+" and cargo_id="+cargoBean.getId());
		}
		
		return true;
		
	}

	
	
	public int getRetShelfCount(String[] cargoOpStatus, String upShelfCode,
			String cargoCode, String productCode, String createUser) throws Exception {
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement rufCodePs = null;
		ResultSet rufCodeRs = null;
		int count = 0;
		try{
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append(
					"select count(ruf.id) from returned_up_shelf ruf where 1=1");
			
			StringBuilder rufCodeQuery = new StringBuilder();
			if(productCode !=null && !productCode.equals("")){
				voProduct productBean = null;
				if(productCode != null && !productCode.equals("")){
					productBean = wareService.getProduct(productCode);
				}
				if(productBean != null){
					rufCodeQuery.append("select source from cargo_operation co, cargo_operation_cargo coc where coc.oper_id=co.id");
					rufCodeQuery.append(" and coc.type=0");
					if(cargoCode != null && !cargoCode.equals("")){
						rufCodeQuery.append(" and coc.in_cargo_whole_code='"+StringUtil.toSql(cargoCode)+"'");
					}
					rufCodeQuery.append(" and coc.product_id="+productBean.getId());
				}else{
					return 0;
				}
			}else if(cargoCode != null && !cargoCode.equals("")){
				rufCodeQuery.append("select source from cargo_operation co, cargo_operation_cargo coc where coc.oper_id=co.id");
				rufCodeQuery.append(" and coc.type=0");
				if(cargoCode != null && !cargoCode.equals("")){
					rufCodeQuery.append(" and coc.in_cargo_whole_code='"+StringUtil.toSql(cargoCode)+"'");
				}
			}
			StringBuilder rufCodeBuilder = new StringBuilder();
			if(rufCodeQuery.length()>0){
				wareService.getDbOp().prepareStatement(rufCodeQuery.toString());
				rufCodePs = wareService.getDbOp().getPStmt();
				rufCodeRs = rufCodePs.executeQuery();
				while(rufCodeRs.next()){
					if(rufCodeBuilder.length()>0){
						rufCodeBuilder.append(",");
					}
					rufCodeBuilder.append("'"+rufCodeRs.getString(1)+"'");
				}
			}
			
			
			if(upShelfCode != null && !upShelfCode.equals("")){
				strBuilder.append(" and ruf.code='"+StringUtil.toSql(upShelfCode.trim())+"'");
			}
			if(cargoOpStatus != null && !cargoOpStatus.equals("")){
				strBuilder.append(" and ruf.status in(");
				for(int i=0; i<cargoOpStatus.length; i++){
					strBuilder.append(StringUtil.toSql(cargoOpStatus[i].trim()));
					if(i<cargoOpStatus.length-1){
						strBuilder.append(",");
					}
				}
				strBuilder.append(")");
			}
			
			if(rufCodeBuilder.length()>0){
				strBuilder.append(" and ruf.code in("+rufCodeBuilder.toString()+")");
			}else if((cargoCode != null && !cargoCode.equals("")) || (productCode != null && !productCode.equals(""))){
				return 0;
			}
			
			if(createUser != null && !createUser.equals("")){
				strBuilder.append(" and create_user_name='"+createUser+"'");
			}
			
			wareService.getDbOp().prepareStatement(strBuilder.toString());
			ps = wareService.getDbOp().getPStmt();
			rs = ps.executeQuery();
			while(rs.next()){
				count = rs.getInt(1);
			}
			return count;
		}finally{
			if(rs != null){
				rs.close();
			}
			if(ps != null){
				ps.close();
			}
			if(rufCodePs != null){
				rufCodePs.close();
			}
			if(rufCodeRs != null){
				rufCodeRs.close();
			}
			wareService.releaseAll();
		}
	}

	public List getRetShelfList(String[] cargoOpStatus, String upShelfCode,
			String cargoCode, String productCode, int index, int countPerPage, String createUserName, String orderBy) throws Exception {
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		PreparedStatement ps = null;
		ResultSet rs = null;
		PreparedStatement rufCodePs = null;
		ResultSet rufCodeRs = null;
		List resultList = new ArrayList();
		try{
			StringBuilder strBuilder = new StringBuilder();
			strBuilder.append(
					"select ruf.auditing_datetime,ruf.passage_whole_code,ruf.auditing_user_id,ruf.auditing_user_name,ruf.code");
			strBuilder.append(",");
			strBuilder.append("ruf.complete_datetime,ruf.complete_user_id,ruf.complete_user_name");
			strBuilder.append(",");
			strBuilder.append("ruf.confirm_datetime,ruf.confirm_user_name,ruf.create_datetime,ruf.create_user_id");
			strBuilder.append(",");
			strBuilder.append("ruf.create_user_name,ruf.status,ruf.remark,ruf.id");
			strBuilder.append(" from returned_up_shelf ruf where 1=1");
			
			StringBuilder rufCodeQuery = new StringBuilder();
			if(productCode !=null && !productCode.equals("")){
				voProduct productBean = null;
				if(productCode != null && !productCode.equals("")){
					productBean = wareService.getProduct(productCode);
				}
				if(productBean != null){
					rufCodeQuery.append("select source from cargo_operation co, cargo_operation_cargo coc where coc.oper_id=co.id");
					rufCodeQuery.append(" and coc.type=0");
					if(cargoCode != null && !cargoCode.equals("")){
						rufCodeQuery.append(" and coc.in_cargo_whole_code='"+StringUtil.toSql(cargoCode)+"'");
					}
					rufCodeQuery.append(" and coc.product_id="+productBean.getId());
				}else{
					return new ArrayList();
				}
			}else if(cargoCode != null && !cargoCode.equals("")){
				rufCodeQuery.append("select source from cargo_operation co, cargo_operation_cargo coc where coc.oper_id=co.id");
				rufCodeQuery.append(" and coc.type=0");
				if(cargoCode != null && !cargoCode.equals("")){
					rufCodeQuery.append(" and coc.in_cargo_whole_code='"+StringUtil.toSql(cargoCode)+"'");
				}
			}
			StringBuilder rufCodeBuilder = new StringBuilder();
			if(rufCodeQuery.length()>0){
				wareService.getDbOp().prepareStatement(rufCodeQuery.toString());
				rufCodePs = wareService.getDbOp().getPStmt();
				rufCodeRs = rufCodePs.executeQuery();
				while(rufCodeRs.next()){
					if(rufCodeBuilder.length()>0){
						rufCodeBuilder.append(",");
					}
					rufCodeBuilder.append("'"+rufCodeRs.getString(1)+"'");
				}
			}
			if(upShelfCode != null && !upShelfCode.equals("")){
				strBuilder.append(" and ruf.code='"+StringUtil.toSql(upShelfCode.trim())+"'");
			}
			if(cargoOpStatus != null && cargoOpStatus.length>0){
				strBuilder.append(" and ruf.status in(");
				for(int i=0; i<cargoOpStatus.length; i++){
					strBuilder.append(StringUtil.toSql(cargoOpStatus[i].trim()));
					if(i<cargoOpStatus.length-1){
						strBuilder.append(",");
					}
				}
				strBuilder.append(")");
			}
			if(rufCodeBuilder.length()>0){
				strBuilder.append(" and ruf.code in("+rufCodeBuilder.toString()+")");
			}else if((cargoCode != null && !cargoCode.equals("")) || (productCode != null && !productCode.equals(""))){
				return new ArrayList();
			}
			
			if(createUserName != null && !createUserName.equals("")){
				strBuilder.append(" and create_user_name='"+createUserName+"'");
			}
			if(orderBy != null && !orderBy.equals("")){
				strBuilder.append(" order by " + orderBy);
			}
			
			if (index < 0) {
				index = 0;
			}

			if (countPerPage == -1) {
				countPerPage = 99999999;
				strBuilder.append(" limit " + index + ", " + countPerPage);
			} else {
				strBuilder.append(" limit " + index + ", " + countPerPage);
			}
			
			wareService.getDbOp().prepareStatement(strBuilder.toString());
			ps = wareService.getDbOp().getPStmt();
			rs = ps.executeQuery();
			ReturnedUpShelfBean rufBean = null;
			while(rs.next()){
				rufBean = new ReturnedUpShelfBean();
				rufBean.setAuditingDatetime(rs.getString("auditing_datetime"));
				rufBean.setAuditingUserId(rs.getInt("auditing_user_id"));
				rufBean.setAuditingUserName(rs.getString("auditing_user_name"));
				rufBean.setCode(rs.getString("code"));
				rufBean.setCompleteDatetime(rs.getString("complete_datetime"));
				rufBean.setCompleteUserId(rs.getInt("complete_user_id"));
				rufBean.setCompleteUsername(rs.getString("complete_user_name"));
				rufBean.setConfirmDatetime(rs.getString("confirm_datetime"));
				rufBean.setConfirmUserName(rs.getString("confirm_user_name"));
				rufBean.setCreateDatetime(rs.getString("create_datetime"));
				rufBean.setCreateUserId(rs.getInt("create_user_id"));
				rufBean.setCreateUserName(rs.getString("create_user_name"));
				rufBean.setStatus(rs.getInt("status"));
				rufBean.setRemark(rs.getString("remark"));
				rufBean.setId(rs.getInt("id"));
				rufBean.setProductCount(getAllProductCount(rufBean.getCode(), wareService.getDbOp()));
				rufBean.setPassageWholeCode(rs.getString("passage_whole_code"));
				resultList.add(rufBean);
			}
			return resultList;
		}finally{
			if(rs != null){
				rs.close();
			}
			if(ps != null){
				ps.close();
			}
			if(rufCodePs != null){
				rufCodePs.close();
			}
			if(rufCodeRs != null){
				rufCodeRs.close();
			}
			wareService.releaseAll();
		}
	}

	
	
	
	public int getAllProductCount(String code, DbOperation dbop) throws Exception{
		
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("select stock_count from cargo_operation_cargo coc, cargo_operation co where co.id=coc.oper_id and coc.type=0 and co.source='");
		strBuilder.append(code);
		strBuilder.append("'");
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			dbop.prepareStatement(strBuilder.toString());
			ps = dbop.getPStmt();
			rs = ps.executeQuery();
			int count = 0;
			while(rs.next()){
				count += rs.getInt("stock_count");
			}
			return count;
		}catch(Exception e){
			throw e;
		}finally{
			if(ps != null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
	}

	public List statisticQualifiedRetProdcut(String orderType,
			String passageCode, String storageCode) throws Exception {
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			Map passageMap = new HashMap();
			Map useFlagMap = new HashMap();//缓存某个巷道是否可以选择
			Map productMap = null;
			StringBuilder sql = new StringBuilder();
			sql.append("select coc.stock_count, coc.product_id, coc.in_cargo_whole_code, co.source from ");
			sql.append("cargo_operation co, cargo_operation_cargo coc ");
			sql.append("where co.id=coc.oper_id and co.code like ");
			sql.append("'HWTS%' ");
			sql.append("and co.status="+CargoOperationProcessBean.OPERATION_STATUS2);
			this.getDbOp().prepareStatement(sql.toString());
			ps = this.getDbOp().getPStmt();
			rs = ps.executeQuery();
			Integer pId = null;
			Integer count = null;
			String wholeCode = null;
			String source = null;
			while(rs.next()){
				//目的货位号-后三位合起来为巷道号全码
				wholeCode = rs.getString("in_cargo_whole_code")
					.substring(0,rs.getString("in_cargo_whole_code").indexOf("-")+4);
				if(passageCode != null && !passageCode.equals("") && storageCode != null && !storageCode.equals("")){
					if(wholeCode.equals(StringUtil.toSql(storageCode) + "-" + StringUtil.toSql(passageCode.trim()))){
						pId = Integer.valueOf(rs.getInt("product_id"));
						count = Integer.valueOf(rs.getInt("count"));
						source = rs.getString("source");
						if(source != null){
							useFlagMap.put(wholeCode, Boolean.valueOf(false));
						}else{
							useFlagMap.put(wholeCode, Boolean.valueOf(true));
						}
						if(passageMap.isEmpty()){
							productMap = new HashMap();
							productMap.put(pId, count);
							passageMap.put(wholeCode, productMap);
						}else{
							if(passageMap.get(wholeCode)!=null){
								productMap = (Map) passageMap.get(wholeCode);
								if(productMap.get(pId)!=null){
									productMap.put(
											pId, Integer.valueOf(Integer.parseInt(productMap.get(pId)+"")+count.intValue()));
								}else{
									productMap.put(pId, count);
								}
							}else{
								productMap = new HashMap();
								productMap.put(pId, count);
								passageMap.put(wholeCode, productMap);
							}
						}
					}
				}
			}
			
			QualifiedRPStatisticInfoBean qrsBean = null;
			List returnList = new ArrayList();
			String pWholeCode = null;
			Map productInfoMap = null;
//			int unDealCount = 0;
			for(Iterator it = passageMap.keySet().iterator(); it.hasNext();){
				pWholeCode = (String) it.next();
//				unDealCount = statService.getReturnedUpShelfCount("status="+ReturnedUpShelfBean.OPERATION_STATUS38+" and passage_whole_code='"+pWholeCode+"'");
				qrsBean = new QualifiedRPStatisticInfoBean();
				qrsBean.setPassageWholeCode(pWholeCode);
				qrsBean.setPassageCode(
						pWholeCode.substring(pWholeCode.indexOf("-")+1, pWholeCode.length()));
				productInfoMap = (Map) passageMap.get(pWholeCode);
				qrsBean.setProductSKU(productInfoMap.size());
				qrsBean.setProductCount(computeProductCount(productInfoMap));
				if(((Boolean)useFlagMap.get(pWholeCode)).booleanValue()){
					qrsBean.setSelectFlag(true);
				}else{
					qrsBean.setSelectFlag(false);
				}
				returnList.add(qrsBean);
			}
			if(orderType != null){
				return orderByStatisticInfo(orderType,returnList);
			}else{
				return returnList;
			}
		}finally{
			if(ps != null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
		
	}

	
	
	private List orderByStatisticInfo(String orderType, List returnList) {
		
		int type = Integer.parseInt(orderType);
		List orderByList = returnList;
		switch(type){
		case 1:
			orderByList = orderBySkuDESC(returnList);
			break;
		case 2:
			orderByList = orderBySkuASC(returnList);
			break;
		case 3:
			orderByList = orderByProductDESC(returnList);
			break;
		case 4:
			orderByList = orderByProductASC(returnList);
			break;
		default:
			break;
		}
		
		return orderByList;
	}

	
	
	private List orderByProductASC(List returnList) {
		
		List resultList = new ArrayList();
		QualifiedRPStatisticInfoBean qrsBean = null;
		QualifiedRPStatisticInfoBean temqrsBean = null;
		for(int i=0; i<returnList.size(); i++){
			qrsBean = (QualifiedRPStatisticInfoBean) returnList.get(i);
			if(resultList.isEmpty()){
				resultList.add(qrsBean);
			}else{
				boolean flag = false;
				for(int j=0; j<resultList.size(); j++){
					temqrsBean = (QualifiedRPStatisticInfoBean) resultList.get(j);
					if(temqrsBean.getProductCount()>qrsBean.getProductCount()){
						resultList.add(j, qrsBean);
						flag = true;
						break;
					}
				}
				if(!flag){
					resultList.add(qrsBean);
				}
			}
		}
		return resultList;
	}

	private List orderByProductDESC(List returnList) {
		List resultList = new ArrayList();
		QualifiedRPStatisticInfoBean qrsBean = null;
		QualifiedRPStatisticInfoBean temqrsBean = null;
		for(int i=0; i<returnList.size(); i++){
			qrsBean = (QualifiedRPStatisticInfoBean) returnList.get(i);
			if(resultList.isEmpty()){
				resultList.add(qrsBean);
			}else{
				boolean flag = false;
				for(int j=0; j<resultList.size(); j++){
					temqrsBean = (QualifiedRPStatisticInfoBean) resultList.get(j);
					if(temqrsBean.getProductCount()<qrsBean.getProductCount()){
						resultList.add(j, qrsBean);
						flag = true;
						break;
					}
				}
				if(!flag){
					resultList.add(qrsBean);
				}
			}
		}
		return resultList;
	}

	private List orderBySkuASC(List returnList) {
		List resultList = new ArrayList();
		QualifiedRPStatisticInfoBean qrsBean = null;
		QualifiedRPStatisticInfoBean temqrsBean = null;
		for(int i=0; i<returnList.size(); i++){
			qrsBean = (QualifiedRPStatisticInfoBean) returnList.get(i);
			if(resultList.isEmpty()){
				resultList.add(qrsBean);
			}else{
				boolean flag = false;
				for(int j=0; j<resultList.size(); j++){
					temqrsBean = (QualifiedRPStatisticInfoBean) resultList.get(j);
					if(temqrsBean.getProductSKU()>qrsBean.getProductSKU()){
						resultList.add(j, qrsBean);
						flag = true;
						break;
					}
				}
				if(!flag){
					resultList.add(qrsBean);
				}
			}
		}
		return resultList;
	}

	private List orderBySkuDESC(List returnList) {
		List resultList = new ArrayList();
		QualifiedRPStatisticInfoBean qrsBean = null;
		QualifiedRPStatisticInfoBean temqrsBean = null;
		for(int i=0; i<returnList.size(); i++){
			qrsBean = (QualifiedRPStatisticInfoBean) returnList.get(i);
			if(resultList.isEmpty()){
				resultList.add(qrsBean);
			}else{
				boolean flag = false;
				for(int j=0; j<resultList.size(); j++){
					temqrsBean = (QualifiedRPStatisticInfoBean) resultList.get(j);
					if(temqrsBean.getProductSKU()<qrsBean.getProductSKU()){
						resultList.add(j, qrsBean);
						flag = true;
						break;
					}
				}
				if(!flag){
					resultList.add(qrsBean);
				}
			}
		}
		return resultList;
	}
	
	

	private int computeProductCount(Map productInfoMap) {
		
		int count = 0;
		if(productInfoMap != null && !productInfoMap.isEmpty()){
			for(Iterator it = productInfoMap.values().iterator(); it.hasNext();){
				count += Integer.parseInt((it.next()+""));
			}
		}
		return count;
	}

	public int getStatisticRPCount(String passageCode, String storageCode) throws Exception {
		
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			StringBuilder sql = new StringBuilder();
			sql.append("select count(rpc.id) from returned_product_cargo rpc,cargo_info ci,cargo_info_passage cip");
			sql.append(" where rpc.count>0 and ci.id=rpc.cargo_id");
			sql.append(" and ci.passage_id=cip.id");
			if(passageCode != null && !passageCode.equals("")
					&& storageCode != null && !storageCode.equals("")){
				sql.append(" and cip.whole_code=?");
			}
			this.getDbOp().prepareStatement(sql.toString());
			ps = this.getDbOp().getPStmt();
			if(passageCode != null && !passageCode.equals("")
					&& storageCode != null && !storageCode.equals("")){
				ps.setString(1, StringUtil.toSql(storageCode) + "-" + StringUtil.toSql(passageCode.trim()));
			}
			rs = ps.executeQuery();
			int count = -1;
			while(rs.next()){
				count = rs.getInt(1);
			}
			return count;
		}finally{
			if(ps != null){
				ps.close();
			}
			if(rs != null){
				rs.close();
			}
		}
	}


	public String saveReturnedUpShelf(voUser user, StatService statService, String passageWholeCode)
			throws Exception {
		this.getDbOp().getConn().setAutoCommit(true);
		//生成汇总单
		String code = "HWTS"+DateUtil.getNow().substring(2,10).replace("-", "");   
		//生成编号
		ReturnedUpShelfBean retShelfBean = null;
		retShelfBean = statService.getReturnedUpShelf("code like '"+code+"%H' order by id desc limit 1");
		if(retShelfBean == null){
			code = code + "0001";
		}else{//获取当日计划编号最大值
			String _code = retShelfBean.getCode();
			int number = Integer.parseInt(_code.substring(_code.length()-5, _code.length()-1));
			number++;
			code += String.format("%04d",new Object[]{new Integer(number)});
		}
		code +="H";
		retShelfBean = new ReturnedUpShelfBean();
		retShelfBean.setCode(code);
		retShelfBean.setCreateDatetime(dateFormat.format(new Date()));
		retShelfBean.setAuditingDatetime(DateUtil.getNow());
		retShelfBean.setConfirmDatetime(DateUtil.getNow());
		retShelfBean.setConfirmUserName(user.getUsername());
		retShelfBean.setAuditingUserId(user.getId());
		retShelfBean.setAuditingUserName(user.getUsername());
		retShelfBean.setCreateUserId(user.getId());
		retShelfBean.setCreateUserName(user.getUsername());
		retShelfBean.setStatus(ReturnedUpShelfBean.OPERATION_STATUS39);
		retShelfBean.setPassageWholeCode(passageWholeCode);
		this.getDbOp().startTransaction();
		boolean flag = statService.addReturnedUpShelf(retShelfBean);
		if(!flag){
			throw new Exception("添加汇总单失败");
		}
		this.getDbOp().commitTransaction();
		return code;
	}
	
	
	

	private String addCargoOperation(voUser user,
			ICargoService cargoService, String storageCode, String sourceCode) {
//		String code = "HWS"+DateUtil.getNow().substring(2,10).replace("-", "");   
		String code = "HWTS"+DateUtil.getNow().substring(2,10).replace("-", "");   
		//生成编号
		CargoOperationBean cargoOper = cargoService.getCargoOperation("code like '"+code+"%' order by id desc limit 1");
		if(cargoOper == null){
			code = code + "00001";
		}else{//获取当日计划编号最大值
			String _code = cargoOper.getCode();
			int number = Integer.parseInt(_code.substring(_code.length()-5));
			number++;
			code += String.format("%05d",new Object[]{new Integer(number)});
		}
		CargoOperationBean coBean = new CargoOperationBean();
		coBean.setCode(code);
		coBean.setCreateDatetime(dateFormat.format(new Date()));
		coBean.setCreateUserId(user.getId());
		coBean.setSource(sourceCode);
		CargoOperationProcessBean process = 
				cargoService.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS3);
    	if(process!=null){
    		coBean.setStatusName(process.getStatusName());
    	}else{
    		coBean.setStatusName("");
    	}
		coBean.setStorageCode(storageCode);
		coBean.setLastOperateDatetime(coBean.getCreateDatetime());
		coBean.setStockInType(CargoInfoBean.STORE_TYPE0);
		coBean.setStockOutType(CargoInfoBean.STORE_TYPE2);
		coBean.setConfirmDatetime(DateUtil.getNow());
		coBean.setConfirmUserName(user.getUsername());
		coBean.setAuditingDatetime(DateUtil.getNow());
		coBean.setAuditingUserId(user.getId());
		coBean.setAuditingUserName(user.getUsername());
		coBean.setCreateUserName(user.getUsername());
		coBean.setType(CargoOperationBean.TYPE0);
		coBean.setEffectStatus(CargoOperationBean.EFFECT_STATUS0);
		coBean.setStatus(CargoOperationProcessBean.OPERATION_STATUS3);
		if(cargoService.addCargoOperation(coBean)){
			return code;
		}else{
			return null;
		}
	}

	
	/**
	 * 根据汇总单号获取退货汇总单详细信息
	 */
	public CargoInfoModelBean getCargInfoModelByUpShelfCode(String upShelfCode) {
		DbOperation db = new DbOperation();
		db.init(DbOperation.DB);
		try{
			ICargoService cargoService = ServiceFactory.createCargoService(
										IBaseService.CONN_IN_SERVICE, db); 
			WareService wareService = new WareService(db);
			voProduct product = null;
			StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, db);
			IProductStockService ipsService = new ProductStockServiceImpl(IBaseService.CONN_IN_SERVICE, db);
			ProductStockBean psBean = null;
			//获取汇总单
			ReturnedUpShelfBean retShelfBean = statService.getReturnedUpShelf("code='"+StringUtil.toSql(upShelfCode)+"'");
			
			//获取汇总单对应的上架单
			List cargoOperationList = cargoService.getCargoOperationList("source='"+StringUtil.toSql(upShelfCode)+"'", 0, -1, null);
			
			List passageCodeList = new LinkedList();
			Map cargoInfoMap = new HashMap();
			CargoOperationBean cargoBean = null;
			List cargoOperationCargoList = null;
			int areaId = -1;
			for(int i=0; i<cargoOperationList.size(); i++){
//			CargoOperationBean cargoBean = cargoService.getCargoOperation("id=" + cargoOperationId);
				cargoBean = (CargoOperationBean) cargoOperationList.get(i);
				areaId = cargoBean.getStockInArea();
				cargoService.updateCargoOperation("print_count=print_count+"+1, "id="+cargoBean.getId());
				cargoOperationCargoList = cargoService.getCargoOperationCargoList(
						"oper_id=" + cargoBean.getId() + " and type=0", 0, -1, "in_cargo_whole_code");
				CargoOperationCargoBean cargoOpBean = null;
				String wholeCode = null;
				ReturnedProductBean retProduct = null;
				for(int j=0; j<cargoOperationCargoList.size();j++){
					cargoOpBean = (CargoOperationCargoBean) cargoOperationCargoList.get(j);
					wholeCode = cargoOpBean.getInCargoWholeCode();
					int index = wholeCode.indexOf("-");
					String passageCode = wholeCode.substring(index+1, index+4);
					//按照巷道排序
					insertPassageCode(passageCodeList, passageCode);
					product = wareService.getProduct(cargoOpBean.getProductId());
					psBean = ipsService.getProductStock("area="+cargoBean.getStockOutArea()+" and type="+ProductStockBean.STOCKTYPE_RETURN+" and product_id="+cargoOpBean.getProductId());
					if(cargoInfoMap.isEmpty()){
						retProduct = new ReturnedProductBean();
						List cargoList = new ArrayList();
						retProduct.setInCargoWholeCode(cargoOpBean.getInCargoWholeCode());
						retProduct.setProductCode(product.getCode());
						retProduct.setAvailableStockCount(psBean.getStock());
						retProduct.setProductName(product.getOriname());
						retProduct.setCount(cargoOpBean.getStockCount());
						retProduct.setCompleteCount(cargoOpBean.getCompleteCount());
						retProduct.setCargoOperationCargoId(cargoOpBean.getId());
						retProduct.setCargoOprationCode(cargoBean.getCode());
						cargoList.add(retProduct);
						cargoInfoMap.put(passageCode, cargoList);
					}else if(cargoInfoMap.get(passageCode) != null){
						retProduct = new ReturnedProductBean();
						retProduct.setInCargoWholeCode(cargoOpBean.getInCargoWholeCode());
						retProduct.setProductCode(product.getCode());
						retProduct.setAvailableStockCount(psBean.getStock());
						retProduct.setProductName(product.getOriname());
						retProduct.setCount(cargoOpBean.getStockCount());
						retProduct.setCompleteCount(cargoOpBean.getCompleteCount());
						retProduct.setCargoOperationCargoId(cargoOpBean.getId());
						retProduct.setCargoOprationCode(cargoBean.getCode());
						((List)cargoInfoMap.get(passageCode)).add(retProduct);
					}else{
						retProduct = new ReturnedProductBean();
						List cargoList = new ArrayList();
						retProduct.setInCargoWholeCode(cargoOpBean.getInCargoWholeCode());
						retProduct.setProductCode(product.getCode());
						retProduct.setAvailableStockCount(psBean.getStock());
						retProduct.setProductName(product.getOriname());
						retProduct.setCount(cargoOpBean.getStockCount());
						retProduct.setCompleteCount(cargoOpBean.getCompleteCount());
						retProduct.setCargoOperationCargoId(cargoOpBean.getId());
						retProduct.setCargoOprationCode(cargoBean.getCode());
						cargoList.add(retProduct);
						cargoInfoMap.put(passageCode, cargoList);
					}
				}
				
			}
			CargoInfoModelBean cargoModelBean = new CargoInfoModelBean();
			cargoModelBean.setAreaId(areaId);
			cargoModelBean.setAuditor(retShelfBean.getAuditingUserName());
			if(retShelfBean.getAuditingDatetime() != null){
				cargoModelBean.setAuditorDate(retShelfBean.getAuditingDatetime().substring(0,19));
			}
			if(retShelfBean.getCreateDatetime() != null){
				cargoModelBean.setCargoOpDate(retShelfBean.getCreateDatetime().substring(0, 19));
			}
			cargoModelBean.setCargoOpmaker(retShelfBean.getCreateUserName());
			cargoModelBean.setOperationCode(retShelfBean.getCode());
			cargoModelBean.setStatus(retShelfBean.getStatus());
			cargoModelBean.setOperationStatus(
					ReturnedUpShelfBean.statusMap.get(Integer.valueOf(retShelfBean.getStatus()))+"");
			if(cargoInfoMap != null && !cargoInfoMap.isEmpty()){
				Iterator it = cargoInfoMap.values().iterator();
				if(it != null){
					List valueList = (List) it.next();
					Comparator comp = new Comparator(){
				          public int compare(Object o1,Object o2) {
				              ReturnedProductBean p1=(ReturnedProductBean)o1;
				              ReturnedProductBean p2=(ReturnedProductBean)o2;  
				              return p1.getInCargoWholeCode().compareTo(p2.getInCargoWholeCode());
				        };
					};
				    Collections.sort(valueList,comp);
					if(valueList != null && !valueList.isEmpty()){
						ReturnedProductBean retProduct = (ReturnedProductBean) valueList.get(0);
						String inCargoWholeCode = retProduct.getInCargoWholeCode();
						//区分是整件区还是散件区
						CargoInfoBean coBean = cargoService.getCargoInfo("whole_code='"+inCargoWholeCode+"'");
						if(coBean != null){
							if(coBean.getStoreType()==CargoInfoBean.STORE_TYPE0){
								cargoModelBean.setTargetCargoType(1);
							}else{
								cargoModelBean.setTargetCargoType(0);
							}
						}
					}
				}
			}
			cargoModelBean.setPassageProduct(cargoInfoMap);
			cargoModelBean.setPassageCode(passageCodeList);
			cargoModelBean.setPstrCode(getpStrCode(passageCodeList));
			if(cargoOperationCargoList !=null && !cargoOperationCargoList.isEmpty()){
				cargoModelBean.setSourceWholeCode(
						((CargoOperationCargoBean)cargoOperationCargoList.get(0)).getOutCargoWholeCode());
			}
			cargoModelBean.setRetUpShelfCode(retShelfBean.getCode());
			return cargoModelBean;
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method:ReturnedPackageServiceImpl.getCargInfoModelByUpShelfCode exception", e);
			}
			throw new RuntimeException(e);
		}finally{
			if(db != null){
				db.release();
			}
		}
	}

	public String updateStockInfo(CargoInfoModelBean cargoInfoModel,
			ICargoService service, WareService wareService,
			IProductStockService productservice, List cargoOpList, voUser user) throws Exception{
		
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		FinanceReportFormsService frfService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List retProductList = cargoInfoModel.getProductBeanList();
		voProduct productBean = null;
		CargoOperationCargoBean cocBean = null;
		CargoInfoBean sourceCargoInfo = null;//源货位信息
		CargoInfoBean targetCargoInfo = null;//目的货位信息
		CargoProductStockBean cargoProductStock = null;//货位库存
		voProduct product = null;
		CargoOperationBean coBean = null;
		List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>();
		for(int i=0; i<retProductList.size(); i++){
			productBean = (voProduct) retProductList.get(i);
			cocBean = getCargoOperationCargo(cargoOpList, productBean.getId());
//			cocBean = service.getCargoOperationCargo(
//					"oper_id=" + cargoInfoModel.getId() + " and product_id=" + productBean.getProductId());
			if(cocBean.getStockCount()!=cocBean.getCompleteCount()){
				if(!service.updateCargoOperationCargo(
						"complete_count=complete_count+"+productBean.getCount(), 
						"oper_id=" + cocBean.getOperId() + 
						" and product_id=" + productBean.getId() + 
						" and complete_count<stock_count")){
					return "23";//更新作业单上架量
				}
				
				product = wareService.getProduct(productBean.getId());
				product.setPsList(productservice.getProductStockList("product_id="+ productBean.getId(), -1, -1, null));
				ProductStockBean psOut = productservice.getProductStock("product_id="+productBean.getId()+" and area="+sourceCargoInfo.getAreaId()+" and type="+sourceCargoInfo.getStockType());
				ProductStockBean psIn = productservice.getProductStock("product_id="+productBean.getId()+" and area="+targetCargoInfo.getAreaId()+" and type="+targetCargoInfo.getStockType());
				
				//减去源货位库存锁定量
				if(!service.updateCargoProductStockLockCount(
						cocBean.getOutCargoProductStockId(), -productBean.getCount())){
					return "12";//操作失败，货位库存不足！
				}
				
				if(!productservice.updateProductLockCount(psOut.getId(), -productBean.getCount())){
					return "20";////库存操作失败，可能是库存不足，请与管理员联系！
				}
				
//				//减去目的货位空间锁定量
//				CargoProductStockBean cpsBean = service.getCargoProductStock("id=" + cocBean.getInCargoProductStockId());
//				if(!service.updateCargoSpaceLockCount(
//						cpsBean.getCargoId(), -productBean.getCount())){
//					return "17";
//				}
				
				//增加目的货位库存量
				if(!service.updateCargoProductStockCount(
						cocBean.getInCargoProductStockId(), productBean.getCount())){
					return "18";
				}
				
				//增加合格库库存量
				if(!productservice.updateProductStockCount(psIn.getId(), productBean.getCount())){
					return "19";
				}
				
				coBean = service.getCargoOperation("id="+cocBean.getOperId());
				
				targetCargoInfo = service.getCargoInfo("whole_code='"+cocBean.getInCargoWholeCode()+"'");
				sourceCargoInfo = service.getCargoInfo("whole_code='"+cocBean.getOutCargoWholeCode()+"'");
				
				
				BaseProductInfo baseProductInfo = new BaseProductInfo();
				baseProductInfo.setId(product.getId());
				//出库
				baseProductInfo.setProductStockOutId(psOut.getId());
				//入库
				baseProductInfo.setProductStockId(psIn.getId());
				baseProductInfo.setOutCount(productBean.getCount());
				baseList.add(baseProductInfo);
				//批次修改开始
				//更新批次记录、添加调拨出、入库批次记录
//				List sbList = stockService.getStockBatchList("product_id="+product.getId()+" and stock_type="+sourceCargoInfo.getStockType()+" and stock_area="+sourceCargoInfo.getAreaId(), -1, -1, "id asc");
//				double stockinPrice = 0;
//				double stockoutPrice = 0;
//				StringBuilder errMsg = new StringBuilder();
//				if(sbList!=null&&sbList.size()!=0){
//					int stockExchangeCount = productBean.getCount();
//					int index = 0;
//					int stockBatchCount = 0;
//						
//					do {
//						//出库
//						StockBatchBean batch = (StockBatchBean)sbList.get(index);
//						int ticket = FinanceSellProductBean.queryTicket(wareService.getDbOp(), batch.getCode());	//是否含票 
//						if(ticket == -1){
//							errMsg.append("上架单："+coBean.getCode());
//							errMsg.append(",商品："+productBean.getCode());
//							errMsg.append(",财务数据查询异常，数据库操作失败！");
//							errMsg.append("<br/>");
//		        			return errMsg.toString();
//						}
//						int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), batch.getProductId(), ticket);
//						
//						if(stockExchangeCount>=batch.getBatchCount()){
//							if(!stockService.deleteStockBatch("id="+batch.getId())){
//								errMsg.append("上架单："+coBean.getCode());
//								errMsg.append(",商品："+productBean.getCode());
//								errMsg.append(",数据库操作失败！");
//								errMsg.append("<br/>");
//			        			return errMsg.toString();
//							}
//							stockBatchCount = batch.getBatchCount();
//						}else{
//							if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
//								errMsg.append("上架单："+coBean.getCode());
//								errMsg.append(",商品："+productBean.getCode());
//								errMsg.append(",数据库操作失败！");
//								errMsg.append("<br/>");
//			        			return errMsg.toString();
//							}
//							stockBatchCount = stockExchangeCount;
//						}
//							
//						//添加批次操作记录
//						StockBatchLogBean batchLog = new StockBatchLogBean();
//						batchLog.setCode(coBean.getCode());
//						batchLog.setStockType(batch.getStockType());
//						batchLog.setStockArea(batch.getStockArea());
//						batchLog.setBatchCode(batch.getCode());
//						batchLog.setBatchCount(stockBatchCount);
//						batchLog.setBatchPrice(batch.getPrice());
//						batchLog.setProductId(batch.getProductId());
//						batchLog.setRemark("调拨出库");
//						batchLog.setCreateDatetime(DateUtil.getNow());
//						batchLog.setUserId(user.getId());
//						if(!stockService.addStockBatchLog(batchLog)){
//							errMsg.append("上架单："+coBean.getCode());
//							errMsg.append(",商品："+productBean.getCode());
//							errMsg.append(",添加批次日志失败！");
//							errMsg.append("<br/>");
//		        			return errMsg.toString();
//						}
//						
//						//财务进销存卡片---liuruilan----2012-11-07---
//						FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + batch.getProductId());
//						if(fProduct == null){
//							errMsg.append("上架单："+coBean.getCode());
//							errMsg.append(",商品："+productBean.getCode());
//							errMsg.append(",财务数据查询异常，数据库操作失败！");
//							errMsg.append("<br/>");
//		        			return errMsg.toString();
//						}
//						int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), sourceCargoInfo.getType(), sourceCargoInfo.getAreaId(), ticket, batch.getProductId());
//						int stockAllArea = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), sourceCargoInfo.getAreaId(), -1, ticket, batch.getProductId());
//						int stockAllType = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, sourceCargoInfo.getType(), ticket, batch.getProductId());
//						FinanceStockCardBean fsc = new FinanceStockCardBean();
//						fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//						fsc.setCode(coBean.getCode());
//						fsc.setCreateDatetime(DateUtil.getNow());
//						fsc.setStockType(sourceCargoInfo.getType());
//						fsc.setStockArea(sourceCargoInfo.getAreaId());
//						fsc.setProductId(batch.getProductId());
//						fsc.setStockId(psIn.getId());
//						fsc.setStockInCount(stockBatchCount);
//						fsc.setStockAllArea(stockAllArea);
//						fsc.setStockAllType(stockAllType);
//						fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//						fsc.setStockPrice(product.getPrice5());
//						
//						fsc.setCurrentStock(currentStock);
//						fsc.setType(fsc.getCardType());
//						fsc.setIsTicket(ticket);
//						fsc.setStockBatchCode(batch.getCode());
//						fsc.setBalanceModeStockCount(_count - stockBatchCount);
//						if(ticket == 0){
//							fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockBatchCount))));
//							fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
//						}
//						if(ticket == 1){
//							fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockBatchCount))));
//							fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
//						}
//						double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(),fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
//						fsc.setAllStockPriceSum(tmpPrice);
//						frfService.addFinanceStockCardBean(fsc);
//						//---------------liuruilan-----------
//						
//							stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//							
//							//入库
//							StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+targetCargoInfo.getStockType()+" and stock_area="+targetCargoInfo.getAreaId());
//							if(batchBean!=null){
//								if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
//									errMsg.append("上架单："+coBean.getCode());
//									errMsg.append(",商品："+productBean.getCode());
//									errMsg.append(",数据库操作失败！");
//									errMsg.append("<br/>");
//				        			return errMsg.toString();
//								}
//							}else{
//								int _ticket = FinanceSellProductBean.queryTicket(stockService.getDbOp(), batch.getCode());
//								StockBatchBean newBatch = new StockBatchBean();
//								newBatch.setCode(batch.getCode());
//								newBatch.setProductId(batch.getProductId());
//								newBatch.setPrice(batch.getPrice());
//								newBatch.setBatchCount(stockBatchCount);
//								newBatch.setProductStockId(psIn.getId());
//								newBatch.setStockArea(targetCargoInfo.getAreaId());//???
//								newBatch.setStockType(psIn.getType());//???
//								newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batchLog.getBatchCode(),batch.getProductId()));
//								newBatch.setTicket(_ticket);
//								if(!stockService.addStockBatch(newBatch)){
//									errMsg.append("上架单："+coBean.getCode());
//									errMsg.append(",商品："+productBean.getCode());
//									errMsg.append(",添加失败！");
//									errMsg.append("<br/>");
//				        			return errMsg.toString();
//								}
//							}
//							
//							//添加批次操作记录
//							batchLog = new StockBatchLogBean();
//							batchLog.setCode(coBean.getCode());
//							batchLog.setStockType(psIn.getType());//???
//							batchLog.setStockArea(sourceCargoInfo.getAreaId());//???
//							batchLog.setBatchCode(batch.getCode());
//							batchLog.setBatchCount(stockBatchCount);
//							batchLog.setBatchPrice(batch.getPrice());
//							batchLog.setProductId(batch.getProductId());
//							batchLog.setRemark("调拨入库");
//							batchLog.setCreateDatetime(DateUtil.getNow());
//							batchLog.setUserId(user.getId());
//							if(!stockService.addStockBatchLog(batchLog)){
//								errMsg.append("上架单："+coBean.getCode());
//								errMsg.append(",商品："+productBean.getCode());
//								errMsg.append(",添加失败！");
//								errMsg.append("<br/>");
//			        			return errMsg.toString();
//							}
//							
//							//财务进销存卡片---liuruilan-----2012-11-07-----
//							int stockinCount = stockBatchCount;
//							currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), targetCargoInfo.getType(), targetCargoInfo.getAreaId(), ticket, batch.getProductId());
//							stockAllArea = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), targetCargoInfo.getAreaId(), -1, ticket, batch.getProductId());
//							stockAllType = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, targetCargoInfo.getType(), ticket, batch.getProductId());
//	    					fsc = new FinanceStockCardBean();
//	    					fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//	    					fsc.setCode(coBean.getCode());
//	    					fsc.setCreateDatetime(DateUtil.getNow());
//	    					fsc.setStockType(batchLog.getStockType());
//	    					fsc.setStockArea(batchLog.getStockArea());
//	    					fsc.setProductId(batch.getProductId());
//	    					fsc.setStockId(psIn.getId());
//	    					fsc.setStockInCount(stockinCount);	
//	    					fsc.setCurrentStock(currentStock);	//只记录分库总库存
//	    					fsc.setStockAllArea(stockAllArea);
//	    					fsc.setStockAllType(stockAllType);
//	    					fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//	    					fsc.setStockPrice(product.getPrice5());
//	    					
//	    					fsc.setType(fsc.getCardType());
//	    					fsc.setIsTicket(ticket);
//	    					fsc.setStockBatchCode(batchLog.getBatchCode());
//	    					fsc.setBalanceModeStockCount(_count - stockBatchCount + stockinCount);
//	    					if(ticket == 0){
//								fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockinCount))));
//								fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
//							}
//							if(ticket == 1){
//								fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockinCount))));
//								fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
//							}
//	    					tmpPrice = Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket())));
//	    					fsc.setAllStockPriceSum(tmpPrice);
//	    					frfService.addFinanceStockCardBean(fsc);
//	    					//-----------liuruilan-------------
//							
//							stockExchangeCount -= batch.getBatchCount();
//							index++;
//							
//							stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//						} while (stockExchangeCount>0&&index<sbList.size());
//					}
					//批次修改结束
				
				// 出库卡片
				StockCardBean sc = new StockCardBean();
				sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
				sc.setCode(coBean.getCode());
				sc.setCreateDatetime(DateUtil.getNow());
				sc.setStockType(sourceCargoInfo.getStockType());
				CargoInfoAreaBean cia = service.getCargoInfoArea("id="+sourceCargoInfo.getAreaId());
				if(cia == null){
					continue;
				}
				int stockArea = cia.getOldId();
				sc.setStockArea(stockArea);
				sc.setProductId(productBean.getId());
				sc.setStockId(sourceCargoInfo.getStorageId());
				sc.setStockOutCount(productBean.getCount());
				// sc.setStockOutPriceSum(stockoutPrice);
				sc.setStockOutPriceSum((new BigDecimal(productBean.getCount())).multiply(
								new BigDecimal(StringUtil.formatDouble2(product
										.getPrice5()))).doubleValue());
				sc.setCurrentStock(product.getStock(cia.getOldId(),
						sourceCargoInfo.getStockType()) + product.getLockCount(cia.getOldId(),sourceCargoInfo.getStockType()));
				sc.setStockAllArea(product.getStock(
						cia.getOldId()) + product.getLockCount(cia.getOldId()));
				sc.setStockAllType(product.getStockAllType(
						sourceCargoInfo.getStockType()) + product.getLockCountAllType(sourceCargoInfo.getStockType()));
				sc.setAllStock(product.getStockAll()
						+ product.getLockCountAll());
				sc.setStockPrice(product.getPrice5());
				sc.setAllStockPriceSum((new BigDecimal(
						sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
				if (!productservice.addStockCard(sc)) {
					throw new RuntimeException("出库添加进销存库存卡片失败！");
				}
	
				cargoProductStock = service.getCargoAndProductStock("cargo_id="+sourceCargoInfo.getId() + " and product_id="+ productBean.getId());
				// 货位出库卡片
				CargoStockCardBean csc = new CargoStockCardBean();
				csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
				csc.setCode(coBean.getCode());
				csc.setCreateDatetime(DateUtil.getNow());
				csc.setStockType(sourceCargoInfo.getStockType());
				csc.setStockArea(stockArea);
				csc.setProductId(productBean.getId());
				csc.setStockId(sourceCargoInfo.getStorageId());
				csc.setStockOutCount(productBean.getCount());
				csc.setStockOutPriceSum((new BigDecimal(productBean.getCount()))
						.multiply(
								new BigDecimal(StringUtil.formatDouble2(product
										.getPrice5()))).doubleValue());
				csc.setCurrentStock(product.getStock(sc.getStockArea(),
						sc.getStockType())
						+ product.getLockCount(sc.getStockArea(),
								sc.getStockType()));
				csc.setAllStock(product.getStockAll()
						+ product.getLockCountAll());
				csc.setCurrentCargoStock(cargoProductStock.getStockCount()
						+ cargoProductStock.getStockLockCount());
				csc.setCargoStoreType(sourceCargoInfo.getStoreType());
				csc.setCargoWholeCode(sourceCargoInfo.getWholeCode());
				csc.setStockPrice(product.getPrice5());
				csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
						.multiply(
								new BigDecimal(StringUtil.formatDouble2(sc
										.getStockPrice()))).doubleValue());
				if (!service.addCargoStockCard(csc)) {
					throw new RuntimeException("出库进销存货位卡片添加失败！");
				}
	
				CargoInfoAreaBean tcib = service.getCargoInfoArea("id="+targetCargoInfo.getAreaId());
				if(tcib == null){
					continue;
				}
				int tarStockArea = tcib.getOldId();
				// 入库卡片
				sc = new StockCardBean();
				sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
				sc.setCode(coBean.getCode());
				sc.setCreateDatetime(DateUtil.getNow());
				sc.setStockType(targetCargoInfo.getStockType());
				sc.setStockArea(tarStockArea);
				sc.setProductId(productBean.getId());
				sc.setStockId(targetCargoInfo.getStorageId());
				sc.setStockInCount(productBean.getCount());
				// sc.setStockInPriceSum(stockinPrice);
				sc.setStockInPriceSum((new BigDecimal(productBean.getCount())).multiply(
								new BigDecimal(StringUtil.formatDouble2(product
										.getPrice5()))).doubleValue());
				sc.setCurrentStock(product.getStock(tcib.getOldId(),
						targetCargoInfo.getStockType()) + product.getLockCount(tcib.getOldId(),targetCargoInfo.getStockType()));
				sc.setStockAllArea(product.getStock(
						tcib.getOldId()) + product.getLockCount(tcib.getOldId()));
				sc.setStockAllType(product.getStockAllType(
						targetCargoInfo.getStockType()) + product.getLockCountAllType(targetCargoInfo.getStockType()));
				sc.setAllStock(product.getStockAll()
						+ product.getLockCountAll());
				sc.setStockPrice(product.getPrice5());
				sc.setAllStockPriceSum((new BigDecimal(
						sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
	
				if (!productservice.addStockCard(sc)) {
					throw new RuntimeException("出库添加进销存失败！");
				}
				
				cargoProductStock = service.getCargoAndProductStock("cargo_id="+targetCargoInfo.getId() + " and product_id="+ productBean.getId());
				// 货位入库卡片
				csc = new CargoStockCardBean();
				csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEIN);
				csc.setCode(coBean.getCode());
				csc.setCreateDatetime(DateUtil.getNow());
				csc.setStockType(targetCargoInfo.getStockType());
				csc.setStockArea(tarStockArea);
				csc.setProductId(productBean.getId());
				csc.setStockId(targetCargoInfo.getStorageId());
				csc.setStockInCount(productBean.getCount());
				csc.setStockInPriceSum((new BigDecimal(productBean.getCount()))
						.multiply(
								new BigDecimal(StringUtil.formatDouble2(product
										.getPrice5()))).doubleValue());
				csc.setCurrentStock(product.getStock(sc.getStockArea(),
						sc.getStockType())
						+ product.getLockCount(sc.getStockArea(),
								sc.getStockType()));
				csc.setAllStock(product.getStockAll()
						+ product.getLockCountAll());
				csc.setCurrentCargoStock(cargoProductStock.getStockCount()
						+ cargoProductStock.getStockLockCount());
				csc.setCargoStoreType(targetCargoInfo.getStoreType());
				csc.setCargoWholeCode(targetCargoInfo.getWholeCode());
				csc.setStockPrice(product.getPrice5());
				csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
						.multiply(
								new BigDecimal(StringUtil.formatDouble2(sc
										.getStockPrice()))).doubleValue());
				if (!service.addCargoStockCard(csc)) {
					throw new RuntimeException("出库进销存添加失败！");
				}
				if(baseList != null && baseList.size() > 0){
					FinanceBaseDataService baseService = 
						FinanceBaseDataServiceFactory.constructFinanceBaseDataService(
						FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN, service.getDbOp().getConn());
					baseService.acquireFinanceBaseData(baseList, coBean.getCode(), user.getId(), 0, 0);
				}
				
			}
			
		}
		return null;
	}
	public String updateStockInfoByProductList(List<voProduct> retProductList,
			ICargoService service, WareService wareService,
			IProductStockService productservice, List cargoOpList, voUser user) throws Exception{
		
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		FinanceReportFormsService frfService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		voProduct productBean = null;
		CargoOperationCargoBean cocBean = null;
		CargoInfoBean sourceCargoInfo = null;//源货位信息
		CargoInfoBean targetCargoInfo = null;//目的货位信息
		CargoProductStockBean cargoProductStock = null;//货位库存
		voProduct product = null;
		CargoOperationBean coBean = null;
		List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>();
		for(int i=0; i<retProductList.size(); i++){
			productBean = (voProduct) retProductList.get(i);
			cocBean = getCargoOperationCargo(cargoOpList, productBean.getId());
//			cocBean = service.getCargoOperationCargo(
//					"oper_id=" + cargoInfoModel.getId() + " and product_id=" + productBean.getProductId());
			if(cocBean.getStockCount()!=cocBean.getCompleteCount()){
				if(!service.updateCargoOperationCargo(
						"complete_count=complete_count+"+productBean.getCount(), 
						"oper_id=" + cocBean.getOperId() + 
						" and product_id=" + productBean.getId() + 
						" and complete_count<stock_count")){
					return "更新作业单库存量失败！";//更新作业单上架量
				}
				
				product = wareService.getProduct(productBean.getId());
				product.setPsList(productservice.getProductStockList("product_id="+ productBean.getId(), -1, -1, null));
				
				//zhaolin 2013-09-30
				sourceCargoInfo = service.getCargoInfo("whole_code = '"+cocBean.getOutCargoWholeCode()+"'");
				targetCargoInfo = service.getCargoInfo("whole_code = '"+cocBean.getInCargoWholeCode()+"'");
				ProductStockBean psOut = productservice.getProductStock("product_id="+productBean.getId()+" and area="+sourceCargoInfo.getAreaId()+" and type="+sourceCargoInfo.getStockType());
				ProductStockBean psIn = productservice.getProductStock("product_id="+productBean.getId()+" and area="+targetCargoInfo.getAreaId()+" and type="+targetCargoInfo.getStockType());
				
//				//增加合格库库存量
				if(!productservice.updateProductStockCount(psIn.getId(), productBean.getCount())){
					return "库存操作失败，增加合格库库存失败！";
				}
				
				if(!productservice.updateProductLockCount(psOut.getId(), -productBean.getCount())){
					return "库存操作失败，可能是退货库库存不足，请与管理员联系！";////库存操作失败，可能是库存不足，请与管理员联系！
				}
				
//				//减去目的货位空间锁定量
//				CargoProductStockBean cpsBean = service.getCargoProductStock("id=" + cocBean.getInCargoProductStockId());
//				if(!service.updateCargoSpaceLockCount(
//						cpsBean.getCargoId(), -productBean.getCount())){
//					return "17";
//				}
				
				//增加目的货位库存量
				if(!service.updateCargoProductStockCount(
						cocBean.getInCargoProductStockId(), productBean.getCount())){
					return "库存操作失败，增加目的货位库存失败！";
				}
				
				//减去源货位库存锁定量
				if(!service.updateCargoProductStockLockCount(
						cocBean.getOutCargoProductStockId(), -productBean.getCount())){
					return "操作失败，源货位库存不足！";//操作失败，货位库存不足！
				}
				
				coBean = service.getCargoOperation("id="+cocBean.getOperId());
				
				targetCargoInfo = service.getCargoInfo("whole_code='"+cocBean.getInCargoWholeCode()+"'");
				sourceCargoInfo = service.getCargoInfo("whole_code='"+cocBean.getOutCargoWholeCode()+"'");
				BaseProductInfo baseProductInfo = new BaseProductInfo();
				baseProductInfo.setId(product.getId());
				//出库
				baseProductInfo.setProductStockOutId(psOut.getId());
				//入库
				baseProductInfo.setProductStockId(psIn.getId());
				baseProductInfo.setOutCount(productBean.getCount());
				baseList.add(baseProductInfo);
				//批次修改开始
				//更新批次记录、添加调拨出、入库批次记录
//				List sbList = stockService.getStockBatchList("product_id="+product.getId()+" and stock_type="+sourceCargoInfo.getStockType()+" and stock_area="+sourceCargoInfo.getAreaId(), -1, -1, "id asc");
//				double stockinPrice = 0;
//				double stockoutPrice = 0;
//				StringBuilder errMsg = new StringBuilder();
//				if(sbList!=null&&sbList.size()!=0){
//					int stockExchangeCount = productBean.getCount();
//					int index = 0;
//					int stockBatchCount = 0;
//						
//					do {
//						//出库
//						StockBatchBean batch = (StockBatchBean)sbList.get(index);
//						int ticket = FinanceSellProductBean.queryTicket(wareService.getDbOp(), batch.getCode());	//是否含票 
//						if(ticket == -1){
//							errMsg.append("上架单："+coBean.getCode());
//							errMsg.append(",商品："+productBean.getCode());
//							errMsg.append(",财务数据查询异常，数据库操作失败！");
//							errMsg.append("<br/>");
//		        			return errMsg.toString();
//						}
//						int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), batch.getProductId(), ticket);
//						
//						if(stockExchangeCount>=batch.getBatchCount()){
//							if(!stockService.deleteStockBatch("id="+batch.getId())){
//								errMsg.append("上架单："+coBean.getCode());
//								errMsg.append(",商品："+productBean.getCode());
//								errMsg.append(",数据库操作失败！");
//								errMsg.append("<br/>");
//			        			return errMsg.toString();
//							}
//							stockBatchCount = batch.getBatchCount();
//						}else{
//							if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
//								errMsg.append("上架单："+coBean.getCode());
//								errMsg.append(",商品："+productBean.getCode());
//								errMsg.append(",数据库操作失败！");
//								errMsg.append("<br/>");
//			        			return errMsg.toString();
//							}
//							stockBatchCount = stockExchangeCount;
//						}
//							
//						//添加批次操作记录
//						StockBatchLogBean batchLog = new StockBatchLogBean();
//						batchLog.setCode(coBean.getCode());
//						batchLog.setStockType(batch.getStockType());
//						batchLog.setStockArea(batch.getStockArea());
//						batchLog.setBatchCode(batch.getCode());
//						batchLog.setBatchCount(stockBatchCount);
//						batchLog.setBatchPrice(batch.getPrice());
//						batchLog.setProductId(batch.getProductId());
//						batchLog.setRemark("调拨出库");
//						batchLog.setCreateDatetime(DateUtil.getNow());
//						batchLog.setUserId(user.getId());
//						if(!stockService.addStockBatchLog(batchLog)){
//							errMsg.append("上架单："+coBean.getCode());
//							errMsg.append(",商品："+productBean.getCode());
//							errMsg.append(",添加批次日志失败！");
//							errMsg.append("<br/>");
//		        			return errMsg.toString();
//						}
//						
//						//财务进销存卡片---liuruilan----2012-11-07---
//						FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + batch.getProductId());
//						if(fProduct == null){
//							errMsg.append("上架单："+coBean.getCode());
//							errMsg.append(",商品："+productBean.getCode());
//							errMsg.append(",财务数据查询异常，数据库操作失败！");
//							errMsg.append("<br/>");
//		        			return errMsg.toString();
//						}
//						int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), sourceCargoInfo.getType(), sourceCargoInfo.getAreaId(), ticket, batch.getProductId());
//						int stockAllArea = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), sourceCargoInfo.getAreaId(), -1, ticket, batch.getProductId());
//						int stockAllType = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, sourceCargoInfo.getType(), ticket, batch.getProductId());
//						FinanceStockCardBean fsc = new FinanceStockCardBean();
//						fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//						fsc.setCode(coBean.getCode());
//						fsc.setCreateDatetime(DateUtil.getNow());
//						fsc.setStockType(sourceCargoInfo.getType());
//						fsc.setStockArea(sourceCargoInfo.getAreaId());
//						fsc.setProductId(batch.getProductId());
//						fsc.setStockId(psIn.getId());
//						fsc.setStockInCount(stockBatchCount);
//						fsc.setStockAllArea(stockAllArea);
//						fsc.setStockAllType(stockAllType);
//						fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//						fsc.setStockPrice(product.getPrice5());
//						
//						fsc.setCurrentStock(currentStock);
//						fsc.setType(fsc.getCardType());
//						fsc.setIsTicket(ticket);
//						fsc.setStockBatchCode(batch.getCode());
//						fsc.setBalanceModeStockCount(_count - stockBatchCount);
//						if(ticket == 0){
//							fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockBatchCount))));
//							fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
//						}
//						if(ticket == 1){
//							fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockBatchCount))));
//							fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
//						}
//						double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(),fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
//						fsc.setAllStockPriceSum(tmpPrice);
//						if(!frfService.addFinanceStockCardBean(fsc)){
//							errMsg.append("财务进销存卡片添加失败！");
//							errMsg.append("<br/>");
//		        			return errMsg.toString();
//						}
//						//---------------liuruilan-----------
//						
//							stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//							
//							//入库
//							StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+targetCargoInfo.getStockType()+" and stock_area="+targetCargoInfo.getAreaId());
//							if(batchBean!=null){
//								if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
//									errMsg.append("上架单："+coBean.getCode());
//									errMsg.append(",商品："+productBean.getCode());
//									errMsg.append(",数据库操作失败！");
//									errMsg.append("<br/>");
//				        			return errMsg.toString();
//								}
//							}else{
//								int _ticket = FinanceSellProductBean.queryTicket(stockService.getDbOp(), batch.getCode());
//								StockBatchBean newBatch = new StockBatchBean();
//								newBatch.setCode(batch.getCode());
//								newBatch.setProductId(batch.getProductId());
//								newBatch.setPrice(batch.getPrice());
//								newBatch.setBatchCount(stockBatchCount);
//								newBatch.setProductStockId(psIn.getId());
//								newBatch.setStockArea(targetCargoInfo.getAreaId());//???
//								newBatch.setStockType(psIn.getType());//???
//								newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batchLog.getBatchCode(),batch.getProductId()));
//								newBatch.setTicket(_ticket);
//								if(!stockService.addStockBatch(newBatch)){
//									errMsg.append("上架单："+coBean.getCode());
//									errMsg.append(",商品："+productBean.getCode());
//									errMsg.append(",添加失败！");
//									errMsg.append("<br/>");
//				        			return errMsg.toString();
//								}
//							}
//							
//							//添加批次操作记录
//							batchLog = new StockBatchLogBean();
//							batchLog.setCode(coBean.getCode());
//							batchLog.setStockType(psIn.getType());//???
//							batchLog.setStockArea(sourceCargoInfo.getAreaId());//???
//							batchLog.setBatchCode(batch.getCode());
//							batchLog.setBatchCount(stockBatchCount);
//							batchLog.setBatchPrice(batch.getPrice());
//							batchLog.setProductId(batch.getProductId());
//							batchLog.setRemark("调拨入库");
//							batchLog.setCreateDatetime(DateUtil.getNow());
//							batchLog.setUserId(user.getId());
//							if(!stockService.addStockBatchLog(batchLog)){
//								errMsg.append("上架单："+coBean.getCode());
//								errMsg.append(",商品："+productBean.getCode());
//								errMsg.append(",添加失败！");
//								errMsg.append("<br/>");
//			        			return errMsg.toString();
//							}
//							
//							//财务进销存卡片---liuruilan-----2012-11-07-----
//							int stockinCount = stockBatchCount;
//							currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), targetCargoInfo.getType(), targetCargoInfo.getAreaId(), ticket, batch.getProductId());
//							stockAllArea = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), targetCargoInfo.getAreaId(), -1, ticket, batch.getProductId());
//							stockAllType = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, targetCargoInfo.getType(), ticket, batch.getProductId());
//	    					fsc = new FinanceStockCardBean();
//	    					fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//	    					fsc.setCode(coBean.getCode());
//	    					fsc.setCreateDatetime(DateUtil.getNow());
//	    					fsc.setStockType(batchLog.getStockType());
//	    					fsc.setStockArea(batchLog.getStockArea());
//	    					fsc.setProductId(batch.getProductId());
//	    					fsc.setStockId(psIn.getId());
//	    					fsc.setStockInCount(stockinCount);	
//	    					fsc.setCurrentStock(currentStock);	//只记录分库总库存
//	    					fsc.setStockAllArea(stockAllArea);
//	    					fsc.setStockAllType(stockAllType);
//	    					fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//	    					fsc.setStockPrice(product.getPrice5());
//	    					
//	    					fsc.setType(fsc.getCardType());
//	    					fsc.setIsTicket(ticket);
//	    					fsc.setStockBatchCode(batchLog.getBatchCode());
//	    					fsc.setBalanceModeStockCount(_count - stockBatchCount + stockinCount);
//	    					if(ticket == 0){
//								fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockinCount))));
//								fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
//							}
//							if(ticket == 1){
//								fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockinCount))));
//								fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
//							}
//	    					tmpPrice = Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket())));
//	    					fsc.setAllStockPriceSum(tmpPrice);
//	    					if(!frfService.addFinanceStockCardBean(fsc)){
//	    						errMsg.append("财务进销存卡片添加失败！");
//								errMsg.append("<br/>");
//			        			return errMsg.toString();
//	    					}
//	    					//-----------liuruilan-------------
//							
//							stockExchangeCount -= batch.getBatchCount();
//							index++;
//							
//							stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//						} while (stockExchangeCount>0&&index<sbList.size());
//					}
					//批次修改结束
				
				// 出库卡片
				StockCardBean sc = new StockCardBean();
				sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
				sc.setCode(coBean.getCode());
				sc.setCreateDatetime(DateUtil.getNow());
				sc.setStockType(sourceCargoInfo.getStockType());
				CargoInfoAreaBean cia = service.getCargoInfoArea("id="+sourceCargoInfo.getAreaId());
				if(cia == null){
					continue;
				}
				int stockArea = cia.getOldId();
				sc.setStockArea(stockArea);
				sc.setProductId(productBean.getId());
				sc.setStockId(sourceCargoInfo.getStorageId());
				sc.setStockOutCount(productBean.getCount());
				// sc.setStockOutPriceSum(stockoutPrice);
				sc.setStockOutPriceSum((new BigDecimal(productBean.getCount())).multiply(
								new BigDecimal(StringUtil.formatDouble2(product
										.getPrice5()))).doubleValue());
				sc.setCurrentStock(product.getStock(cia.getOldId(),
						sourceCargoInfo.getStockType()) + product.getLockCount(cia.getOldId(),sourceCargoInfo.getStockType()));
				sc.setStockAllArea(product.getStock(
						cia.getOldId()) + product.getLockCount(cia.getOldId()));
				sc.setStockAllType(product.getStockAllType(
						sourceCargoInfo.getStockType()) + product.getLockCountAllType(sourceCargoInfo.getStockType()));
				sc.setAllStock(product.getStockAll()
						+ product.getLockCountAll());
				sc.setStockPrice(product.getPrice5());
				sc.setAllStockPriceSum((new BigDecimal(
						sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
				if (!productservice.addStockCard(sc)) {
					throw new RuntimeException("出库添加进销存库存卡片失败！");
				}
	
				cargoProductStock = service.getCargoAndProductStock("cargo_id="+sourceCargoInfo.getId() + " and product_id="+ productBean.getId());
				// 货位出库卡片
				CargoStockCardBean csc = new CargoStockCardBean();
				csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
				csc.setCode(coBean.getCode());
				csc.setCreateDatetime(DateUtil.getNow());
				csc.setStockType(sourceCargoInfo.getStockType());
				csc.setStockArea(stockArea);
				csc.setProductId(productBean.getId());
				csc.setStockId(sourceCargoInfo.getStorageId());
				csc.setStockOutCount(productBean.getCount());
				csc.setStockOutPriceSum((new BigDecimal(productBean.getCount()))
						.multiply(
								new BigDecimal(StringUtil.formatDouble2(product
										.getPrice5()))).doubleValue());
				csc.setCurrentStock(product.getStock(sc.getStockArea(),
						sc.getStockType())
						+ product.getLockCount(sc.getStockArea(),
								sc.getStockType()));
				csc.setAllStock(product.getStockAll()
						+ product.getLockCountAll());
				csc.setCurrentCargoStock(cargoProductStock.getStockCount()
						+ cargoProductStock.getStockLockCount());
				csc.setCargoStoreType(sourceCargoInfo.getStoreType());
				csc.setCargoWholeCode(sourceCargoInfo.getWholeCode());
				csc.setStockPrice(product.getPrice5());
				csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
						.multiply(
								new BigDecimal(StringUtil.formatDouble2(sc
										.getStockPrice()))).doubleValue());
				if (!service.addCargoStockCard(csc)) {
					throw new RuntimeException("出库进销存货位卡片添加失败！");
				}
	
				CargoInfoAreaBean tcib = service.getCargoInfoArea("id="+targetCargoInfo.getAreaId());
				if(tcib == null){
					continue;
				}
				int tarStockArea = tcib.getOldId();
				// 入库卡片
				sc = new StockCardBean();
				sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
				sc.setCode(coBean.getCode());
				sc.setCreateDatetime(DateUtil.getNow());
				sc.setStockType(targetCargoInfo.getStockType());
				sc.setStockArea(tarStockArea);
				sc.setProductId(productBean.getId());
				sc.setStockId(targetCargoInfo.getStorageId());
				sc.setStockInCount(productBean.getCount());
				// sc.setStockInPriceSum(stockinPrice);
				sc.setStockInPriceSum((new BigDecimal(productBean.getCount())).multiply(
								new BigDecimal(StringUtil.formatDouble2(product
										.getPrice5()))).doubleValue());
				sc.setCurrentStock(product.getStock(tcib.getOldId(),
						targetCargoInfo.getStockType()) + product.getLockCount(tcib.getOldId(),targetCargoInfo.getStockType()));
				sc.setStockAllArea(product.getStock(
						tcib.getOldId()) + product.getLockCount(tcib.getOldId()));
				sc.setStockAllType(product.getStockAllType(
						targetCargoInfo.getStockType()) + product.getLockCountAllType(targetCargoInfo.getStockType()));
				sc.setAllStock(product.getStockAll()
						+ product.getLockCountAll());
				sc.setStockPrice(product.getPrice5());
				sc.setAllStockPriceSum((new BigDecimal(
						sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
	
				if (!productservice.addStockCard(sc)) {
					throw new RuntimeException("出库添加进销存失败！");
				}
				
				cargoProductStock = service.getCargoAndProductStock("cargo_id="+targetCargoInfo.getId() + " and product_id="+ productBean.getId());
				// 货位入库卡片
				csc = new CargoStockCardBean();
				csc.setCardType(CargoStockCardBean.CARDTYPE_STOCKEXCHANGEIN);
				csc.setCode(coBean.getCode());
				csc.setCreateDatetime(DateUtil.getNow());
				csc.setStockType(targetCargoInfo.getStockType());
				csc.setStockArea(tarStockArea);
				csc.setProductId(productBean.getId());
				csc.setStockId(targetCargoInfo.getStorageId());
				csc.setStockInCount(productBean.getCount());
				csc.setStockInPriceSum((new BigDecimal(productBean.getCount()))
						.multiply(
								new BigDecimal(StringUtil.formatDouble2(product
										.getPrice5()))).doubleValue());
				csc.setCurrentStock(product.getStock(sc.getStockArea(),
						sc.getStockType())
						+ product.getLockCount(sc.getStockArea(),
								sc.getStockType()));
				csc.setAllStock(product.getStockAll()
						+ product.getLockCountAll());
				csc.setCurrentCargoStock(cargoProductStock.getStockCount()
						+ cargoProductStock.getStockLockCount());
				csc.setCargoStoreType(targetCargoInfo.getStoreType());
				csc.setCargoWholeCode(targetCargoInfo.getWholeCode());
				csc.setStockPrice(product.getPrice5());
				csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock()))
						.multiply(
								new BigDecimal(StringUtil.formatDouble2(sc
										.getStockPrice()))).doubleValue());
				if (!service.addCargoStockCard(csc)) {
					throw new RuntimeException("出库进销存添加失败！");
				}

				if(baseList != null && baseList.size() > 0){
					FinanceBaseDataService baseService = 
						FinanceBaseDataServiceFactory.constructFinanceBaseDataService(
						FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN, service.getDbOp().getConn());
					baseService.acquireFinanceBaseData(baseList, coBean.getCode(), user.getId(), 0, 0);
				}
				
				
			}
			
		}
		return null;
	}

	
	private CargoOperationCargoBean getCargoOperationCargo(List cargoOpList,
			int productId) {

		CargoOperationCargoBean cocBean = null;
		for(int i=0; i<cargoOpList.size(); i++){
			cocBean = (CargoOperationCargoBean) cargoOpList.get(i);
			if(cocBean.getProductId()==productId){
				break;
			}
		}
		return cocBean;
	}

	
	public String generateUpShelf(Map retUpShelfMap, voUser user, String date) throws Exception{
		
		String sourceCode = null;
		StringBuilder strBuilder = new StringBuilder();
		String retUpShelfCode = null;
		int productCount=0;
		CargoOperationBean coBean = null;
		String passageCode = null;
		CargoOperationCargoBean cocBean = null;
		int currentCount = 0;
		try{
			
			if(retUpShelfMap == null || retUpShelfMap.isEmpty()){
				return "没有可以汇总的上架单！";
			}
			
			StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
			//生成退货上架汇总单
			sourceCode = this.saveReturnedUpShelf(user, statService, "");
			for(Iterator it = retUpShelfMap.keySet().iterator(); it.hasNext();){
				//生成上架单
				retUpShelfCode = (String) it.next();
				coBean = cargoService.getCargoOperation("code='"+retUpShelfCode+"'");
				if(coBean == null){
					strBuilder.append(retUpShelfCode);
					strBuilder.append(":该退货上架单不存在，或者不是退货上架单！");
					continue;
				}
				
				if(coBean.getEffectStatus()==CargoOperationBean.EFFECT_STATUS4){
					strBuilder.append(retUpShelfCode);
					strBuilder.append(":该退货上架单已作业失败！");
					continue;
				}
				
				if(coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS2){
					
					if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS1){
						strBuilder.append(retUpShelfCode);
						strBuilder.append(":该退货上架单还没有确认提交！");
						continue;
					}
					
					if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS3
							|| coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS4
							|| coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS5
							|| coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS6){
						strBuilder.append(retUpShelfCode);
						strBuilder.append(":该退货上架单已汇总！");
						continue;
					}
					
					if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS7
							|| coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS8
							|| coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS9){
						strBuilder.append(retUpShelfCode);
						strBuilder.append(":该退货上架单已完成！");
						continue;
					}
					
				}
				
				//更新上架单状态为已审核，设置源为汇总单编号
				this.getDbOp().startTransaction();
				if(!cargoService.updateCargoOperation(
						"status="+CargoOperationProcessBean.OPERATION_STATUS3
						+",source='"+sourceCode+"'"
						+",auditing_user_id="+user.getId()
						+",auditing_user_name='"+user.getUsername()+"'"
						+",auditing_datetime='"+DateUtil.getNow()+"'"
						+",last_operate_datetime='"+DateUtil.getNow()+"'", "id="+coBean.getId())){
					this.getDbOp().rollbackTransaction();
					strBuilder.append(retUpShelfCode);
					strBuilder.append(":该退货上架单更新失败！");
					continue;
				}
				this.getDbOp().commitTransaction();
				
				//查询上架单库存信息
				cocBean = cargoService.getCargoOperationCargo("oper_id="+coBean.getId()+" and type=0");
				productCount = cocBean.getStockCount();
				
				//获取数量最多的巷道号
				if(currentCount<productCount){
					int index = cocBean.getInCargoWholeCode().indexOf("-");
					passageCode = cocBean.getInCargoWholeCode().substring(index+1, index+4);
					currentCount = productCount;
				}
			}
			
			//开启事务
			this.getDbOp().startTransaction();
			if(!statService.updateReturnedUpShelf("passage_whole_code='"+passageCode+"'", "code='"+sourceCode+"'")){
				strBuilder.append("更新汇总单巷道失败!");
				this.getDbOp().rollbackTransaction();
			}else{
				this.getDbOp().commitTransaction();
				//结束事务
			}
			
			//物流员工绩效考核操作
			this.getDbOp().startTransaction();
			String addPerformanceResult = this.addCargoStaffPerformanceForUpShelf(user, retUpShelfMap,date, cargoService);
			if(addPerformanceResult.equals("SUCCESS")) {
				this.getDbOp().commitTransaction();
			} else {
				this.getDbOp().rollbackTransaction();
				strBuilder.append(addPerformanceResult);
			}
			
			
			if(strBuilder.length()>0){
				strBuilder.append("--");
				strBuilder.append(sourceCode);
			}else{
				strBuilder.append(sourceCode);
			}
		}catch(Exception e){
			if(logger.isErrorEnabled()){
				logger.error("method: ReturnedPackageServiceImpl.generateUpShelf exception", e);
			}
			strBuilder.append("上架单："+retUpShelfCode);
			strBuilder.append("发生了系统异常！");
			strBuilder.append("\r\n");
			if(!this.getDbOp().getConn().getAutoCommit()){
				this.getDbOp().rollbackTransaction();
			}
		}
		return strBuilder.toString();
	}

	
	public String addCargoStaffPerformanceForUpShelf(voUser user, Map retUpShelfMap, String date, ICargoService cargoService) {
		String result = "SUCCESS";
		CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
		if(csBean == null){
			result = "此账号不是物流员工 ,操作将不会计入物流员工绩效！";
		} else {
			CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and type=6 and staff_id=" + csBean.getId() );
			int operCount = 1;
			int productCount2 = 0;
			for(Iterator  it = retUpShelfMap.keySet().iterator();it.hasNext();){
				String retUpShelfCode2 = (String)it.next();
				CargoOperationBean coBean2 = cargoService.getCargoOperation("code='"+StringUtil.toSql(retUpShelfCode2)+"'"); 
				CargoOperationCargoBean cocBean3 = cargoService.getCargoOperationCargo(" oper_id=" + coBean2.getId());
				productCount2 = productCount2 + cocBean3.getStockCount();
			}
			if(cspBean != null){
				productCount2= productCount2 + cspBean.getProductCount();
				operCount = operCount + cspBean.getOperCount();
				boolean flag = cargoService.updateCargoStaffPerformance(" oper_count=" + operCount + ", product_count=" + productCount2, " id=" + cspBean.getId());
				if(!flag){
					result = "物流员工绩效考核更新操作失败,操作将不会计入物流员工绩效！";
				}
			}else{
				CargoStaffPerformanceBean newBean = new CargoStaffPerformanceBean();
				newBean.setDate(date);
				newBean.setProductCount(productCount2);
				newBean.setOperCount(operCount);
				newBean.setStaffId(csBean.getId());
				newBean.setType(6);  //6代表退货上架汇总作业
				boolean flag = cargoService.addCargoStaffPerformance(newBean);
				if(!flag){
					result = "物流员工绩效考核更新操作失败,操作将不会计入物流员工绩效！";
				}
			}
		}
		return result;
	}

	private void setSourceInfo(CargoInfoModelBean modelBean, int productId)throws Exception {
		
		ResultSet rs = null;
		try{
			//不需要散件区和使用中条件
			String querySql = "select ci.whole_code," +
					"ci.id, cps.id as cid from returned_product_cargo rpc, cargo_product_stock cps, cargo_info ci " +
					"where rpc.product_id=cps.product_id and cps.cargo_id=ci.id " +
					"and ci.area_id=3 and rpc.product_id="+productId+
					" and ci.stock_type="+CargoInfoBean.STOCKTYPE_RETURN;
			rs = this.getDbOp().executeQuery(querySql);
			while(rs.next()){
				modelBean.setSourceWholeCode(rs.getString("whole_code"));
				modelBean.setSourceCargoId(rs.getInt("id"));
				modelBean.setCargoStockId(rs.getInt("cid"));
			}
		}finally{
			if(rs != null){
				rs.close();
			}
		}
		
	}

	private CargoInfoModelBean construtTargetInfo(DbOperation dbop, String productCode, String productCount) throws Exception {
		
		WareService wareService = new WareService(dbop);
		IBarcodeCreateManagerService bService = 
			ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, dbop);
		ResultSet rs = null;
		PreparedStatement ps = null;
		voProduct product = null;
		CargoInfoModelBean cargoInfo = null;
		try{
			StringBuilder sql = new StringBuilder();
			ProductBarcodeVO bBean = bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(productCode).trim()+"'");
			if(bBean == null){
				product = wareService.getProduct(StringUtil.toSql(productCode).trim());
			}else{
				product = wareService.getProduct(bBean.getProductId());
			}
			sql.append("select ci.whole_code, ci.id, cps.id cid from returned_product_cargo rpc, cargo_info ci, cargo_product_stock cps");
			sql.append(" where ci.id=rpc.cargo_id and cps.cargo_id=ci.id and rpc.product_id="+product.getId()+" and cps.product_id="+product.getId());
			dbop.prepareStatement(sql.toString());
			ps = dbop.getPStmt();
			rs = ps.executeQuery();
			while(rs.next()){
				cargoInfo = new CargoInfoModelBean();
				cargoInfo.setProductId(product.getId());
				cargoInfo.setProductCode(product.getCode());
				cargoInfo.setWholeCode(rs.getString("whole_code"));
				cargoInfo.setId(rs.getInt("id"));
				cargoInfo.setInCargoStockId(rs.getInt("cid"));
				cargoInfo.setCount(Integer.parseInt(productCount));
			}
			return cargoInfo;
		}finally{
			if(rs != null){
				rs.close();
			}
			if(ps != null){
				ps.close();
			}
		}
	}

	private void insertPassageCode(List passageCodeList, String passCode) {
		
		if(passageCodeList.isEmpty()){
			passageCodeList.add(passCode);
		}else{
			int i = 0; 
			String pCode = null;
			boolean insert = false;
			for(Iterator it = passageCodeList.iterator(); it.hasNext();){
				pCode = (String) it.next();
				//假如passCode<pCode，那么在pCode前插入
				if(passCode.compareTo(pCode)<0 && !passageCodeList.contains(passCode)){
					passageCodeList.add(i, passCode);
					insert = true;
					break;
				}
				i++;
			}
			if(!insert && !passageCodeList.contains(passCode)){
				passageCodeList.add(passCode);
			}
		}		
	}

	public String completeRetShelf(ReturnedUpShelfBean rufBean, voUser user, WareService wareService) throws Exception {
		
		try{
			ICargoService cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE, this.getDbOp());
			StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, this.getDbOp());
			
			//已审核任务
			List cargoOperationList = cargoService.getCargoOperationList(
					"source='"+StringUtil.toSql(rufBean.getCode())
					+"' and effect_status!="+CargoOperationBean.EFFECT_STATUS4
					+" and effect_status!="+CargoOperationBean.EFFECT_STATUS3, 0, -1, null);
			
			if(cargoOperationList != null){
				CargoOperationBean coBean = null;
				for(int j=0; j<cargoOperationList.size(); j++){
					
					//判断作业单是否存在
					coBean = (CargoOperationBean) cargoOperationList.get(j);
					
					//获取作业单下所有cargooperationcargo信息
					List cargoOpList = cargoService.getCargoOperationCargoList("oper_id=" + coBean.getId()+" and type=0", 0, -1, null);
					if(cargoOpList == null || cargoOpList.isEmpty()){
						return "上架单："+coBean.getCode()+"，没有详细信息";//作业单下没有详细信息
					}
					
					CargoOperationCargoBean cocBean = null;
					for(int i=0; i<cargoOpList.size(); i++){
						//如果上架单状态为已审核，回滚库存，同时修改上架单的时效状态为作业失败
						if(coBean.getStatus() == CargoOperationProcessBean.OPERATION_STATUS3){
							cocBean = (CargoOperationCargoBean) cargoOpList.get(i);
							String productCode = wareService.getProductCode(cocBean.getProductId()+"");
							//回滚退货库库存
							if(!rollbackProductStock(cocBean)){
								return "商品："+productCode+"退货库锁定量不足!";//回滚退货库库存失败
							};
							
							//回滚退货(源货位)货位库存
							if(!rollbackCargoProductStock(cocBean, cargoService)){
								return "商品："+productCode+"源货位锁定量不足!";
							}
							
							
							//回滚目标货位空间锁定量
//							if(!rollbackCargoProductSpaceStock(cocBean, cargoService)){
//								return "商品："+productCode+"目的货位空间锁定量不足!";
//							}
							
							CargoOperationLogBean logBean=new CargoOperationLogBean();
							logBean.setOperId(coBean.getId());
							logBean.setOperDatetime(DateUtil.getNow());
							logBean.setOperAdminId(user.getId());
							logBean.setOperAdminName(user.getUsername());
							StringBuilder logRemark = new StringBuilder("回滚：商品");
							logRemark.append(productCode);
							logRemark.append("，目的货位（");
							logRemark.append(cocBean.getOutCargoWholeCode());
							logRemark.append("），");
							logRemark.append("回滚量（");
							logRemark.append(cocBean.getStockCount()-cocBean.getCompleteCount());
							logRemark.append("）");
							logBean.setRemark(logRemark.toString());
							if(!cargoService.addCargoOperationLog(logBean)){
								return "商品："+productCode+"添加操作记录失败!";//添加操作记录失败
							}
							//更新作业单时效状态为作业失败
							if(!cargoService.updateCargoOperation(
									"effect_status=" + CargoOperationBean.EFFECT_STATUS4
									+",last_operate_datetime='"+DateUtil.getNow()+"'", "id="+coBean.getId())){
								return "上架单："+coBean.getCode()+"，更新时效状态失败！";//更新作业单状态失败
							}
						//如果状态为作用完成，那么修改时效状态为作业成功
						}else if(coBean.getStatus() == CargoOperationProcessBean.OPERATION_STATUS7){
							//更新作业单时效状态为作业成功
							if(!cargoService.updateCargoOperation(
									"effect_status=" + CargoOperationBean.EFFECT_STATUS3
									+",last_operate_datetime='"+DateUtil.getNow()+"'", "id="+coBean.getId())){
								return "上架单："+coBean.getCode()+"，更新时效状态失败！";//更新作业单状态失败
							}
						}
				}
			}
			if(!statService.updateReturnedUpShelf(
					"status="+ReturnedUpShelfBean.OPERATION_STATUS46
					+",complete_datetime='"+DateUtil.getNow()+"'"
					+",complete_user_name='"+user.getUsername()+"'"
					+",complete_user_id="+user.getId(), "id="+rufBean.getId())){
				return "更新退货上架汇总单："+rufBean.getCode()+"状态失败！";
			}
		}
	}catch(Exception e){
		throw new RuntimeException(e);
	}
	return null;
}

	public String getPassageCodeByMount(List allCargoOperationCargoList) {
		Map map = new HashMap();
		if( allCargoOperationCargoList == null ) {
			return "";
		} else {
			for(int i = 0; i < allCargoOperationCargoList.size(); i++ ) {
				CargoOperationCargoBean cocb = (CargoOperationCargoBean) allCargoOperationCargoList.get(i);
				String temp = cocb.getInCargoWholeCode();
				if(temp != null ) {
					temp = temp.substring(6,9);
					if( !map.containsKey(temp) ) {
						map.put(temp, Integer.valueOf(cocb.getStockCount()));
					} else {
						int total = ((Integer)map.get(temp)).intValue();
						total += cocb.getStockCount();
						map.put(temp, Integer.valueOf(total));
					}
				}
			}
			int max = 0;
			Iterator itr = map.values().iterator();
			for( ; itr.hasNext(); ) {
				int v = ((Integer)itr.next()).intValue();
				if( v > max ) {
					max = v;
				}
			}
			Iterator itr2 = map.keySet().iterator();
			for( ; itr2.hasNext(); ) {
				String temp2 = (String)itr2.next();
				int v2 = ((Integer)map.get(temp2)).intValue();
				if( v2 == max ) {
					return temp2;
				}
			}
		}
		return "";
	}
	
	public String queryRetShelfStaticInfo(String createDate) throws Exception {
		
		if(createDate == null || createDate.equals("")){
			createDate = DateUtil.getNowDateStr();
		}
		String beginTime = createDate + " 00:00:00";
		String endTime = createDate + " 23:59:59";
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			
			//查询出当天所有汇总单
			StringBuilder condition = new StringBuilder("");
			condition.append("create_datetime between '");
			condition.append(beginTime);
			condition.append("' and '");
			condition.append(endTime);
			condition.append("'");
			List retUpShelfList = statService.getReturnedUpShelfList(condition.toString(), 0, -1, null);
			if(retUpShelfList == null || retUpShelfList.isEmpty()){
				return createDate + "没有生成汇总单！";
			}
			
			StringBuilder strBuilder = new StringBuilder();
			
			//统计信息
			Map skuInfoMap = new HashMap();
			ReturnedUpShelfBean rufBean = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			int allCount = 0;
			for(int i=0; i<retUpShelfList.size(); i++){
				rufBean = (ReturnedUpShelfBean) retUpShelfList.get(i);
				if(strBuilder.length()>0){
					strBuilder.append(",");
				}
				strBuilder.append("'"+rufBean.getCode()+"'");
			}
			strBuilder.append(")");
			
			wareService.getDbOp().prepareStatement(QUERYRETSHELFCOUNT+strBuilder.toString());
			ps = wareService.getDbOp().getPStmt();
//			ps.setString(1, rufBean.getCode());
			rs = ps.executeQuery();
			while(rs.next()){
				allCount += rs.getInt("stock_count");
				if(skuInfoMap.isEmpty()){
					skuInfoMap.put(Integer.valueOf(rs.getInt("product_id")), Integer.valueOf(rs.getInt("stock_count")));
				}else if(skuInfoMap.containsKey(Integer.valueOf(rs.getInt("product_id")))){
					int temCount = Integer.parseInt(skuInfoMap.get(Integer.valueOf(rs.getInt("product_id")))+"");
					skuInfoMap.put(Integer.valueOf(rs.getInt("product_id")),Integer.valueOf(temCount+rs.getInt("stock_count")));
				}else{
					skuInfoMap.put(Integer.valueOf(rs.getInt("product_id")), Integer.valueOf(rs.getInt("stock_count")));
				}
			}
			
			StringBuilder result = new StringBuilder(createDate);
			result.append("共生成");
			result.append(retUpShelfList.size());
			result.append("张汇总单，单内商品总计：");
			result.append("<strong>");
			result.append(skuInfoMap.keySet().size());
			result.append("个sku，");
			result.append(allCount);
			result.append("件商品");
			result.append("</strong>");
			return result.toString();
		}catch(Exception e){
			throw e;
		}finally{
			wareService.releaseAll();
		}
	}

	/**
	 * 质检时生成上架单，生成后状态为已提交， 库存，源货位库存，目的货位空间冻结量已锁定，各项已经锁定了
	 * @param user 用户类
	 * @param product 产品类
	 * @param mount 数量
	 * @param wareService
	 * @param service
	 * @param statService
	 * @param cargoService
	 * @return 返回是一个 String 如果为success 则表示生成过程顺利完成， 如果不是则是错误信息。
	 */
	public CargoOperationCargoBean createUpShelfBill(voUser user,voProduct product, int mount,CargoInfoBean targetCargoInfo,WareService wareService,IProductStockService service,StatService statService,ICargoService cargoService, int wareArea) throws Exception {
		//分配目的货位
		
		CargoInfoAreaBean cargoArea = cargoService.getCargoInfoArea("old_id = "+wareArea);
		ProductStockBean psb = service.getProductStock("stock > 0 and area = " + wareArea + " and type = " + ProductStockBean.STOCKTYPE_RETURN + " and product_id = " + product.getId());
		CargoProductStockBean cpsb = cargoService.getCargoAndProductStock("ci.stock_type = " + CargoInfoBean.STOCKTYPE_RETURN + " and ci.store_type = " + CargoInfoBean.STORE_TYPE2 + " and ci.area_id = " + cargoArea.getId() + " and cps.product_id = " + product.getId() + " and cps.stock_count > 0");
		if( psb == null || psb.getStock() < mount ) {
			throw new Exception("商品" + product.getCode() + "没有足够的库存可用");
		}
		
		if( cpsb == null || cpsb.getStockCount() < mount ) {
			throw new Exception("商品" + product.getCode() + "没有足够的货位库存可用");
		}
		int sEnd = targetCargoInfo.getWholeCode().indexOf("-");
		String storageCode = targetCargoInfo.getWholeCode().substring(0, sEnd);
		//添加上架单
		String operCode = "HWTS"+DateUtil.getNow().substring(2,10).replace("-", "");   
		//生成编号
		CargoOperationBean cargoOper = cargoService.getCargoOperation("code like '"+operCode+"%' order by id desc limit 1");
		if(cargoOper == null){
			operCode = operCode + "00001"; 
		}else{//获取当日计划编号最大值
			String _code = cargoOper.getCode();
			int number = Integer.parseInt(_code.substring(_code.length()-5));
			number++;
			operCode += String.format("%05d",new Object[]{new Integer(number)});
		}
		CargoOperationBean cob = new CargoOperationBean();
		cob.setStatus(CargoOperationProcessBean.OPERATION_STATUS2);
		cob.setCreateDatetime(DateUtil.getNow());
		cob.setCreateUserId(user.getId());
		cob.setCreateUserName(user.getUsername());
		cob.setCode(operCode);
		cob.setStorageCode(storageCode);
		cob.setAuditingDatetime(DateUtil.getNow());
		cob.setAuditingUserId(user.getId());
		cob.setAuditingUserName(user.getUsername());
		cob.setConfirmDatetime(DateUtil.getNow());
		cob.setConfirmUserName(user.getUsername());
		cob.setStockInType(targetCargoInfo.getStoreType());
		cob.setStockOutType(CargoInfoBean.STORE_TYPE2);
		cob.setType(CargoOperationBean.TYPE0);
		cob.setStockInArea(targetCargoInfo.getAreaId());
		cob.setStockOutArea(targetCargoInfo.getAreaId());
		cob.setPrintCount(0);
		cob.setLastOperateDatetime(DateUtil.getNow());
		cob.setEffectStatus(CargoOperationBean.EFFECT_STATUS0);
		if(!cargoService.addCargoOperation(cob)) {
			throw new Exception("商品"+ product.getCode()+"数据库操作失败没有添加上架单,");
		}
		int id = service.getDbOp().getLastInsertId();
		
		CargoProductStockBean cpsbIn = cargoService.getCargoProductStock("product_id = " + product.getId() + " and cargo_id = " + targetCargoInfo.getId());
		List outList = new ArrayList();
		try {
			this.constructSourceInfo(this.getDbOp(),outList,cargoArea.getId(),product.getId());
		} catch (Exception e) {
			throw new Exception("找源货位是发生了错误！！");
		}
		CargoProductStockBean cpsbOut = null;
		for( int s = 0; s < outList.size(); s++ ) {
			CargoInfoModelBean tempcps = (CargoInfoModelBean) outList.get(s);
			if( tempcps.getProductId() == product.getId() ) {
				cpsbOut = new CargoProductStockBean();
				cpsbOut.setCargoId(tempcps.getId());
				cpsbOut.setId(tempcps.getCargoStockId());
			}
		}
		if( cpsbOut == null ) {
			throw new Exception("商品"+ product.getCode()+"没有找到源货位库存的信息");
		}
		CargoInfoBean cargoInfoOut = cargoService.getCargoInfo("id = " + cpsbOut.getCargoId());
		CargoInfoBean cargoInfoIn = cargoService.getCargoInfo("id = " + targetCargoInfo.getId());
		if(cargoInfoIn == null ) {
			throw new Exception("商品"+ product.getCode()+"没有找到目的货位库存的信息");
		}
		CargoOperationCargoBean cocb = new CargoOperationCargoBean();
		cocb.setProductId(product.getId());
		cocb.setOperId(id);
		cocb.setInCargoProductStockId(cpsbIn.getId());
		cocb.setInCargoWholeCode(cargoInfoIn.getWholeCode());
		cocb.setOutCargoProductStockId(cpsbOut.getId());
		cocb.setOutCargoWholeCode(cargoInfoOut.getWholeCode());
		cocb.setStockCount(mount);
		cocb.setType(CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE);
		cocb.setUseStatus(CargoOperationCargoBean.COC_WITH_INCARGOINFO_STATUS);
		cocb.setCompleteCount(0);
		//为寻找库区号做准备。
		if( !cargoService.addCargoOperationCargo(cocb)) {
			throw new Exception("商品"+ product.getCode()+"添加入单失败");
		}
		
		CargoOperationCargoBean cocb2 = new CargoOperationCargoBean();
		cocb2.setProductId(product.getId());
		cocb2.setOperId(id);
		cocb2.setOutCargoProductStockId(cpsbOut.getId());
		cocb2.setOutCargoWholeCode(cargoInfoOut.getWholeCode());
		cocb2.setStockCount(mount);
		cocb2.setType(CargoOperationCargoBean.COC_UNWITH_INCARGOINFO_TYPE);
		cocb2.setUseStatus(CargoOperationCargoBean.COC_UNWITH_INCARGOINFO_STATUS);
		cocb2.setCompleteCount(0);
		if( !cargoService.addCargoOperationCargo(cocb2)) {
			throw new Exception("商品"+ product.getCode()+"添加入单失败");
		}
		
		//锁定目的货位空间冻结量
//		if(!cargoService.updateCargoSpaceLockCount(cargoInfoIn.getId(), mount)){
//			throw new Exception("有商品" + product.getCode() + "操作失败，货位库存不足");
//		}
		CargoInfoAreaBean cargoInfoArea = cargoService.getCargoInfoArea("id="+cargoInfoOut.getAreaId());
		ProductStockBean outProductStock = service.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_RETURN+" and product_id="+product.getId());
		if(outProductStock==null){
			throw new Exception("有商品" + product.getCode() + "合格库库存数据错误");
		}
		//减少商品库存
		if (!service.updateProductStockCount(outProductStock.getId(),-mount)) {
			throw new Exception("有商品" + product.getCode() + "库存操作失败，可能是库存不足，请与管理员联系");
		}
		//增加商品的冻结量
		if (!service.updateProductLockCount(outProductStock.getId(),mount)) {
			throw new Exception("有商品" + product.getCode() + "库存操作失败，可能是库存不足，请与管理员联系");
		}
		//锁定库存， 源货位库存
		//减少源货位的可使用量
		if(!cargoService.updateCargoProductStockCount(cpsbOut.getId(), -mount)){
			throw new Exception("商品" + product.getCode() + "操作失败，货位库存不足");
		}
		//增加源货位的冻结量
		if(!cargoService.updateCargoProductStockLockCount(cpsbOut.getId(), mount)){
			throw new Exception("有商品" + product.getCode() + "操作失败，货位库存不足");
		}
		CargoOperationLogBean logBean=new CargoOperationLogBean();
		logBean.setOperId(id);
		logBean.setOperDatetime(DateUtil.getNow());
		logBean.setOperAdminId(user.getId());
		logBean.setOperAdminName(user.getUsername());
		StringBuilder logRemark = new StringBuilder("保存编辑：商品");
		logRemark.append(wareService.getProductCode(product.getId()+""));
		logRemark.append("，");
		logRemark.append("源货位（");
		logRemark.append(cargoInfoOut.getWholeCode());
		logRemark.append("）");
		logRemark.append("，目的货位（");
		logRemark.append(cargoInfoIn.getWholeCode());
		logRemark.append("），");
		logRemark.append("上架量（");
		logRemark.append(mount);
		logRemark.append("）");
		logBean.setRemark(logRemark.toString());
		if(!cargoService.addCargoOperationLog(logBean)){
			throw new Exception("商品"+ product.getCode()+"添加入单日志失败");
		}
		
		CargoOperationLogBean logBean2=new CargoOperationLogBean();
		logBean2.setOperId(id);
		logBean2.setOperDatetime(DateUtil.getNow());
		logBean2.setOperAdminId(user.getId());
		logBean2.setOperAdminName(user.getUsername());
		StringBuilder logRemark2 = new StringBuilder("退货上架单确认提交，审核通过");
		logRemark.append(wareService.getProductCode(product.getId()+""));
		logRemark2.append("，");
		logRemark2.append("源货位（");
		logRemark2.append(cargoInfoOut.getWholeCode());
		logRemark2.append("）");
		logRemark2.append("，目的货位（");
		logRemark2.append(cargoInfoIn.getWholeCode());
		logRemark2.append("），");
		logRemark2.append("上架量（");
		logRemark2.append(mount);
		logRemark2.append("）");
		logBean2.setRemark(logRemark2.toString());
		if(!cargoService.addCargoOperationLog(logBean2)){
			throw new Exception ("商品"+ product.getCode()+"添加确认，审核日志失败");
		}
		cocb.setCargoOperation(cob);
		return cocb;
	}
	
	//开通货位 并添加货位商品关联
	public boolean dealTargetCargoAndProduct(CargoInfoBean cargoInfo, voProduct product, ICargoService cargoService, IProductStockService service) {
		boolean result = true;
		
		// 如果货位状态为未开通修改货位状态 为开通
		if( cargoInfo.getStatus() == CargoInfoBean.STATUS1 ) {
			if( !cargoService.updateCargoInfo("status=" + CargoInfoBean.STATUS0, "id="+cargoInfo.getId()) ) {
				service.getDbOp().rollbackTransaction();
				result = false;
			}
		}
		//如果货位没有找到对应的 货位库存量信息 添加一条
		CargoProductStockBean cpsb = cargoService.getCargoProductStock("cargo_id = " + cargoInfo.getId() + " and product_id = " + product.getId());
		if( cpsb == null ) {
			CargoProductStockBean cps = new CargoProductStockBean();
			cps.setCargoId(cargoInfo.getId());
			cps.setProductId(product.getId());
			cps.setStockCount(0);
			cps.setStockLockCount(0);
			if(!cargoService.addCargoProductStock(cps)){
				service.getDbOp().rollbackTransaction();
				result = false;
			}
		}
		return result;
	}
	
}


















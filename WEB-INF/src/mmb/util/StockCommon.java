package mmb.util;

import java.math.BigDecimal;

import mmb.finance.stat.FinanceProductBean;
import mmb.finance.stat.FinanceReportFormsService;
import mmb.finance.stat.FinanceSellProductBean;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.ware.WareService;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.ProductStockServiceImpl;
import adultadmin.service.impl.StockServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 库存方法抽象专用
 * @author syuf
 *
 */
public class StockCommon {

	/**
	 * 添加库存批次及添加批次操作日志
	 * @param dbOp 数据源
	 * @param user 用户
	 * @param productId 商品ID
	 * @param count 商品数量
	 * @param stockArea 库地区
	 * @param stockType 库类型
	 * @param code 单据号
	 * @param remark 备注
	 * @return 成功返回null
	 * @author syuf
	 */
	public String addStockBatch(DbOperation dbOp,voUser user, int productId,int count,int stockArea,int stockType,String code,String remark){
		WareService wareService = new WareService(dbOp);
		StockServiceImpl stockService = new StockServiceImpl(IBaseService.CONN_IN_SERVICE, dbOp);
		ProductStockServiceImpl psService = new ProductStockServiceImpl(IBaseService.CONN_IN_SERVICE,dbOp);
		voProduct product = wareService.getProduct(productId);
		ProductStockBean productStock = psService.getProductStock("product_id=" + productId + " and area=" + stockArea + " and type=" +stockType);
		if(productStock == null){
			return "商品库存不存在!";
		}
		// 添加批次记录
		StockBatchBean batch = null;
		String batchCode = "Q" + DateUtil.getNow().substring(0, 10).replace("-", "");
		batch = stockService.getStockBatch("code like '" + code + "%'");
		int ticket = 0;
		if (batch == null) {
			// 当日第一份批次记录，编号最后三位 001
			batchCode += "001";
		} else {
			// 获取当日计划编号最大值
			int maxid = stockService.getNumber("id", "stock_batch", "max", "id > 0 and code like '"
					+ batchCode + "%'");
			batch = stockService.getStockBatch("id =" + maxid);
			String _code = batch.getCode();
			int number = Integer.parseInt(_code.substring(_code.length() - 3));
			number++;
			batchCode += String.format("%03d", new Object[] { new Integer(number) });
		}
		batch = new StockBatchBean();
		batch.setCode(batchCode);
		batch.setProductId(productId);
		batch.setPrice(product.getPrice5());// 报溢的产品的价格是现有价格
		batch.setBatchCount(count);
		batch.setProductStockId(productStock.getId());
		batch.setStockArea(stockArea);
		batch.setStockType(stockType);
		batch.setCreateDateTime(DateUtil.getNow());
		batch.setTicket(ticket);
		if(!stockService.addStockBatch(batch)){
			return "添加批次记录失败!";
		}
		// 添加批次操作记录
		StockBatchLogBean batchLog = new StockBatchLogBean();
		batchLog.setCode(code);
		batchLog.setStockType(batch.getStockType());
		batchLog.setStockArea(batch.getStockArea());
		batchLog.setBatchCode(batch.getCode());
		batchLog.setBatchCount(batch.getBatchCount());
		batchLog.setBatchPrice(batch.getPrice());
		batchLog.setProductId(batch.getProductId());
		batchLog.setRemark(remark);
		batchLog.setCreateDatetime(DateUtil.getNow());
		batchLog.setUserId(user.getId());
		if(!stockService.addStockBatchLog(batchLog)){
			return "添加批次日志失败!";
		}
		return null;
	}
	/**
	 * 更新库存批次及添加批次操作日志
	 * @param dbOp 数据源
	 * @param user 用户
	 * @param batch 要更新的批次
	 * @param count 更新的数量 减库存填写正数 加库存填写负数
	 * @param code 单据号
	 * @param remark 备注
	 * @return 实际批次的修改量
	 * @author syuf
	 */
	public int updateStockBatch(DbOperation dbOp,voUser user,StockBatchBean batch,int count,String code,String remark){
		StockServiceImpl stockService = new StockServiceImpl(IBaseService.CONN_IN_SERVICE, dbOp);
		int batchCount = 0;
		if (count >= batch.getBatchCount()) {
			//如果报损的数量大于当前批次的数量 就删除这个批次. 那这次要报损的数量就是这个批次的数量
			if(!stockService.deleteStockBatch("id=" + batch.getId())){
				return -1;
			}
			batchCount = batch.getBatchCount();
		} else {
			//如果报损数量小于当前批次数量, 那就改变这个批次中的存货厕数量,那这次出货的数量就是剩下的数量
			if(!stockService.updateStockBatch("batch_count = batch_count-" + count,"id=" + batch.getId())){
				return -2;
			}
			batchCount = count;
		}
		// 添加批次操作记录
		StockBatchLogBean batchLog = new StockBatchLogBean();
		batchLog.setCode(code);
		batchLog.setStockType(batch.getStockType());
		batchLog.setStockArea(batch.getStockArea());
		batchLog.setBatchCode(batch.getCode());
		batchLog.setBatchCount(batch.getBatchCount());
		batchLog.setBatchPrice(batch.getPrice());
		batchLog.setProductId(batch.getProductId());
		batchLog.setRemark(remark);
		batchLog.setCreateDatetime(DateUtil.getNow());
		batchLog.setUserId(user.getId());
		if(!stockService.addStockBatchLog(batchLog)){
			return -3;
		}
		return Math.abs(batchCount);
	}
	/**
	 * 添加财务的###入库###进销存卡片
	 * @param dbOp 数据源
	 * @param batch 库存批次对象
	 * @param stockId 库存Id 
	 * @param batchCount 操作数据量
	 * @param cardType 卡片类型
	 * @param code 单据号
	 * @return 成功返回null 
	 * @author syuf
	 */
	public String addFinanceStockCardIn(DbOperation dbOp,StockBatchBean batch,int stockId,int batchCount,int cardType,String code){
		WareService wareService = new WareService(dbOp);
		FinanceReportFormsService frfService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, dbOp);
		voProduct product = wareService.getProduct(batch.getProductId());
		if(product == null){
			return "ID:" + batch.getProductId() + " 商品不存在!";
		}
		//财务产品信息表
		int ticket = FinanceSellProductBean.queryTicket(dbOp, batch.getCode());	//是否含票 
		if(ticket == -1){
			return "入库:查询是否含发票异常，请与管理员联系！";
		}
		FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + product.getId());
		if(fProduct == null){
			return "入库:查询财务价格异常，请与管理员联系！";
		}
		float price5 = product.getPrice5();
		int totalCount = product.getStockAll() + product.getLockCountAll();
		float priceSum = Arith.mul(price5, totalCount);
		int _count = FinanceProductBean.queryCountIfTicket(dbOp, product.getId(), ticket);
		int stockinCount = batchCount;
		float priceHasticket = fProduct.getPriceHasticket();
		float priceSumHasticket = Arith.mul(priceHasticket,  _count);
		String set = "price =" + price5 + ", price_sum =" + priceSum + ", price_sum_hasticket =" + priceSumHasticket;
		if(!frfService.updateFinanceProductBean(set, "product_id = " + product.getId())){
		  return "数据库操作失败";
		}
		//财务进销存卡片
		int currentStock = FinanceStockCardBean.getCurrentStockCount(dbOp, batch.getStockArea(), batch.getStockType(), ticket,  product.getId());
		int stockAllType=FinanceStockCardBean.getCurrentStockCount(dbOp, -1, batch.getStockType(), ticket, product.getId());
		int stockAllArea=FinanceStockCardBean.getCurrentStockCount(dbOp, batch.getStockArea(), -1,ticket,  product.getId());
		FinanceStockCardBean fsc = new FinanceStockCardBean();
		fsc.setCardType(StockCardBean.CARDTYPE_GET);//StockCardBean.CARDTYPE_CANCELORDERSTOCKIN-->StockCardBean.CARDTYPE_GET
		fsc.setCode(code);
		fsc.setCreateDatetime(DateUtil.getNow());
		fsc.setStockType(batch.getStockType());
		fsc.setStockArea(batch.getStockArea());
		fsc.setProductId( product.getId());
		fsc.setStockId(stockId);
		fsc.setStockInCount(batchCount);	
		fsc.setCurrentStock(currentStock);	//只记录分库总库存
		fsc.setStockAllArea(stockAllArea);
		fsc.setStockAllType(stockAllType);
		fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
		fsc.setStockPrice(price5);
		
		fsc.setType(fsc.getCardType());
		fsc.setIsTicket(ticket);
		fsc.setStockBatchCode(batch.getCode());
		fsc.setBalanceModeStockCount(_count);
		fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(priceHasticket, stockinCount))));
		fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(priceHasticket)));
		double tmpPrice = Arith.add(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
		fsc.setAllStockPriceSum(tmpPrice);
		if(!frfService.addFinanceStockCardBean(fsc)){
		  return "数据库操作失败";
		}
		return null;
	}
	/**
	 * 添加财务的###出库###进销存卡片
	 * @param dbOp 数据源
	 * @param batch 库存批次对象
	 * @param stockId 库存Id 
	 * @param batchCount 操作数据量
	 * @param cardType 卡片类型
	 * @param code 单据号
	 * @return 成功返回null 
	 * @author syuf
	 */
	public String addFinanceStockCardOut(DbOperation dbOp,StockBatchBean batch,int stockId,int batchCount,int cardType,String code){
		WareService wareService = new WareService(dbOp);
		FinanceReportFormsService frfService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, dbOp);
		ProductStockServiceImpl psService = new ProductStockServiceImpl(IBaseService.CONN_IN_SERVICE,dbOp);
		voProduct product = wareService.getProduct(batch.getProductId());
		if(product == null){
			return "ID:" + batch.getProductId() + " 商品不存在!";
		}
		//财务产品信息表
		int ticket = FinanceSellProductBean.queryTicket(dbOp, batch.getCode());	//是否含票 
		if(ticket == -1){
			return "出库:查询是否含发票异常，请与管理员联系！";
		}
		FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + batch.getProductId());
		if(fProduct == null){
			return "出库:查询财务价格异常，请与管理员联系！";
		}
		int _count = FinanceProductBean.queryCountIfTicket(dbOp, batch.getProductId(), ticket);
		float price5 = product.getPrice5();
		int totalCount = product.getStockAll() + product.getLockCountAll();
		float priceSum = Arith.mul(price5, totalCount);
		float priceHasticket = fProduct.getPriceHasticket();
		float priceNoticket = fProduct.getPriceNoticket();
		float priceSumHasticket = 0;
		float priceSumNoticket = 0;
		String set = "price_sum =" + priceSum;
		if(ticket == 0){	//0-有票
			priceSumHasticket = Arith.mul(priceHasticket,  _count);
			set += ", price_sum_hasticket =" + priceSumHasticket;
		}
		if(ticket == 1){	//1-无票
			priceSumNoticket = Arith.mul(priceNoticket,  _count);
			set += ", price_sum_noticket =" + priceSumNoticket;
		}
		if(!frfService.updateFinanceProductBean(set, "product_id = " + product.getId())){
			return "数据库操作失败";
		}
		//财务进销存卡片
		product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,null));
		int currentStock = FinanceStockCardBean.getCurrentStockCount(dbOp, batch.getStockArea(), batch.getStockType(), ticket, batch.getProductId());
		int stockAllType=FinanceStockCardBean.getCurrentStockCount(dbOp, -1, batch.getStockType(), ticket, batch.getProductId());
		int stockAllArea=FinanceStockCardBean.getCurrentStockCount(dbOp,  batch.getStockArea(), -1,ticket, batch.getProductId());
		FinanceStockCardBean fsc = new FinanceStockCardBean();
		fsc.setCardType(cardType);
		fsc.setCode(code);
		fsc.setCreateDatetime(DateUtil.getNow());
		fsc.setStockType(batch.getStockType());
		fsc.setStockArea(batch.getStockArea());
		fsc.setProductId(batch.getProductId());
		fsc.setStockId(stockId);
		fsc.setStockInCount(batchCount);
		fsc.setStockAllArea(stockAllArea);
		fsc.setStockAllType(stockAllType);
		fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
		fsc.setStockPrice(product.getPrice5());
		fsc.setCurrentStock(currentStock);
		fsc.setType(fsc.getCardType());
		fsc.setIsTicket(ticket);
		fsc.setStockBatchCode(batch.getCode());
		fsc.setBalanceModeStockCount(_count);
		if(ticket == 0){
			fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), batchCount))));
			fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
		}
		if(ticket == 1){
			fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), batchCount))));
			fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
		}
		double tmpPrice = Arith.add(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(),fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
		fsc.setAllStockPriceSum(tmpPrice);
		if(!frfService.addFinanceStockCardBean(fsc)){
			return "添加财务进销存卡片失败";
		}
		return null;
	}
	/**
	 * 添加####入库####进销存卡片及货位进销存卡片
	 * @param dbOp 数据源
	 * @param outCount 出库量
	 * @param productId 商品ID
	 * @param cargoId 货位ID 
	 * @param cardType 卡片类型
	 * @param code 单据编号
	 * @param stockType 库类型
	 * @param stockArea 库地区
	 * @return 成功返回null
	 * @author syuf
	 */
	public String addStockCardIn(DbOperation dbOp,int inCount,int productId,int cargoId,int cardType,String code,int stockType,int stockArea){
		WareService wareService = new WareService(dbOp);
		ProductStockServiceImpl psService = new ProductStockServiceImpl(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		voProduct product = wareService.getProduct(productId);
		if(product == null){
			return "ID[" + productId + "]商品不存在!";
		}
		ProductStockBean productStock = psService.getProductStock("product_id=" + productId + " and area=" + stockArea + " and type=" +stockType);
		if(productStock == null){
			return "商品库存不存在!";
		}
		product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,null));
		CargoProductStockBean cargoProductStock = cargoService.getCargoProductStock("product_id=" + productId + " and cargo_id=" + cargoId);
		if(cargoProductStock == null){
			return "货位商品库存不存在!";
		}
		CargoProductStockBean cps = cargoService.getCargoAndProductStock("cps.id = " + cargoProductStock.getId());
		// 入库卡片
		StockCardBean sc = new StockCardBean();
		sc.setCardType(cardType);
		sc.setCode(code);

		sc.setCreateDatetime(DateUtil.getNow());
		sc.setStockType(stockType);
		sc.setStockArea(stockArea);
		sc.setProductId(productId);
		sc.setStockId(productStock.getId());
		sc.setStockInCount(inCount);
		sc.setStockInPriceSum(product.getPrice5()*inCount);
		sc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
		sc.setStockAllArea(product.getStock(sc.getStockArea()) + product.getLockCount(sc.getStockArea()));
		sc.setStockAllType(product.getStockAllType(sc.getStockType()) + product.getLockCountAllType(sc.getStockType()));
		sc.setAllStock(product.getStockAll() + product.getLockCountAll());
		sc.setStockPrice(product.getPrice5());// 新的库存价格
		sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
		if(!psService.addStockCard(sc)){
			return "进销存记录添加失败，请重新尝试操作！";
		}
		
		//货位入库卡片
		CargoStockCardBean csc = new CargoStockCardBean();
		csc.setCardType(CargoStockCardBean.CARDTYPE_GET);
		csc.setCode(code);
		csc.setCreateDatetime(DateUtil.getNow());
		csc.setStockType(stockType);
		csc.setStockArea(stockArea);
		csc.setProductId(productId);
		csc.setStockId(cps.getId());
		csc.setStockInCount(inCount);
		csc.setStockInPriceSum((new BigDecimal(inCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
		csc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
		csc.setAllStock(product.getStockAll() + product.getLockCountAll());
		csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
		csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
		csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
		csc.setStockPrice(product.getPrice5());
		csc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
		if(!cargoService.addCargoStockCard(csc)){
			return "货位进销存添加失败，请重新尝试操作！";
		}
		return null;
	}
	/**
	 * 添加####出库####进销存卡片及货位进销存卡片
	 * @param dbOp 数据源
	 * @param outCount 出库量
	 * @param productId 商品ID
	 * @param cargoId 货位ID 
	 * @param cardType 卡片类型
	 * @param code 单据编号
	 * @param stockType 库类型
	 * @param stockArea 库地区
	 * @return 成功返回null
	 * @author syuf
	 */
	public String addStockCardOut(DbOperation dbOp,int outCount,int productId,int cargoId,int cardType,String code,int stockType,int stockArea){
		WareService wareService = new WareService(dbOp);
		ProductStockServiceImpl psService = new ProductStockServiceImpl(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		voProduct product = wareService.getProduct(productId);
		if(product == null){
			return "ID[" + productId + "]商品不存在!";
		}
		ProductStockBean productStock = psService.getProductStock("product_id=" + productId + " and area=" + stockArea + " and type=" +stockType);
		if(productStock == null){
			return "商品库存不存在!";
		}
		product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,null));
		CargoProductStockBean cargoProductStock = cargoService.getCargoProductStock("product_id=" + productId + " and cargo_id=" + cargoId);
		if(cargoProductStock == null){
			return "货位商品库存不存在!";
		}
		CargoProductStockBean cps = cargoService.getCargoAndProductStock("cps.id = " + cargoProductStock.getId());
		// 出库卡片
		StockCardBean sc = new StockCardBean();
		sc.setCardType(cardType);//
		sc.setCode(code);

		sc.setCreateDatetime(DateUtil.getNow());
		sc.setStockType(stockType);
		sc.setStockArea(stockArea);
		sc.setProductId(productId);
		sc.setStockId(productStock.getId());
		sc.setStockOutCount(outCount);
		sc.setStockOutPriceSum((new BigDecimal(outCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
		sc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
		sc.setStockAllArea(product.getStock(sc.getStockArea()) + product.getLockCount(sc.getStockArea()));
		sc.setStockAllType(product.getStockAllType(sc.getStockType()) + product.getLockCountAllType(sc.getStockType()));
		sc.setAllStock(product.getStockAll() + product.getLockCountAll());
		sc.setStockPrice(product.getPrice5());
		sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
		if(!psService.addStockCard(sc)){
			return  "进销存记录添加失败，请重新尝试操作！";
		}
		
		//货位出库卡片
		CargoStockCardBean csc = new CargoStockCardBean();
		csc.setCardType(cardType);
		csc.setCode(code);
		csc.setCreateDatetime(DateUtil.getNow());
		csc.setStockType(stockType);
		csc.setStockArea(stockArea);
		csc.setProductId(productId);
		csc.setStockId(cps.getId());
		csc.setStockOutCount(outCount);
		csc.setStockOutPriceSum((new BigDecimal(outCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
		csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
		csc.setAllStock(product.getStockAll() + product.getLockCountAll());
		csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
		csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
		csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
		csc.setStockPrice(product.getPrice5());
		csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
		if(!cargoService.addCargoStockCard(csc)){
			return "货位进销存记录添加失败，请重新尝试操作！";
		}  
		return null;
	}
}

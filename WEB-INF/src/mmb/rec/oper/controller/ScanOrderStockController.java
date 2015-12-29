package mmb.rec.oper.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mmb.aftersale.OrderRefundService;
import mmb.common.service.MWareService;
import mmb.finance.balance.FinanceMailingBalanceBean;
import mmb.finance.stat.FinanceProductBean;
import mmb.finance.stat.FinanceReportFormsService;
import mmb.finance.stat.FinanceSellBean;
import mmb.finance.stat.FinanceSellProductBean;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.rec.oper.service.OrderStockService;
import mmb.rec.oper.service.StockService;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.stat.SortingBatchOrderBean;
import mmb.stock.stat.SortingInfoService;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceSaleBaseDataService;

import adultadmin.action.stock.OrderPackageAction;
import adultadmin.action.stock.PrintPackageAction;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderExtendInfo;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.OrderStockStatusBean;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.balance.MailingBalanceBean;
import adultadmin.bean.barcode.OrderCustomerBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.cargo.CargoStaffPerformanceBean;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAdminHistoryBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.supplier.SupplierCityBean;
import adultadmin.framework.IConstants;
import adultadmin.service.IAdminService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.StockServiceImpl;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IBalanceService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IBatchBarcodeService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.ISupplierService;
import adultadmin.util.Arith;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/ScanOrderStockController")
public class ScanOrderStockController {
	private String date = DateUtil.formatDate(new Date());
	public static byte[] orderStockLock = new byte[0];
	public Log stockLog = LogFactory.getLog("stock.Log");
	@Autowired
	public OrderStockService mOrderStockService;
	
	@Autowired
	public StockService mProductStockService;
	
	@Autowired
	public MWareService mWareService;
	
	@Autowired 
	public mmb.rec.oper.service.BalanceService mBalanceService;
	
	/**
	 *说明：复核完成
	 *@author 石远飞
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/completeOrderStock")
	@ResponseBody
	public Json completeOrderStock(HttpServletRequest request,HttpServletResponse response,
			String operId) throws ServletException, IOException {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(294)){
            request.setAttribute("msg", "你没有权限进行该操作，请与管理员联系!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
	 	}
		Json j = new Json();
		synchronized(orderStockLock){
			
			List<Map<String,String>> mapList = (List<Map<String,String>>)request.getSession().getAttribute("list");
			if(mapList != null && mapList.size() > 0){
				for(Map<String,String> m : mapList){
					if(!m.get("stockOutCount").equals(m.get("count"))){
						j.setMsg("该订单复核量不等于出库量,暂不能出库,请重新复核!");
						return j;
					}
				}
			}
			IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IBalanceService baService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			IAdminService adminService = ServiceFactory.createAdminServiceLBJ();
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE,service.getDbOp());
			FinanceReportFormsService frfService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			mmb.finance.balance.BalanceService balanceService = new mmb.finance.balance.BalanceService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			DbOperation db = service.getDbOp();
			DbOperation redb=new DbOperation();
			redb.init("adult_slave2");
			FinanceReportFormsService refrfService = new FinanceReportFormsService(IBaseService.CONN_IN_SERVICE, redb);
			IStockService reservice = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, redb);
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			ResultSet rs0 = null;

			HttpSession session = request.getSession();
			String key = operId + "stockout";
			try {
				//开始事务
				service.getDbOp().startTransaction();
				try{
					FinanceSaleBaseDataService financeBaseDataService = 
							FinanceBaseDataServiceFactory.constructFinanceSaleBaseDataService(
									FinanceStockCardBean.CARDTYPE_ORDERSTOCK, null);
					financeBaseDataService.acquireFinanceSaleBaseData(null, user.getId(), "", 
							DateUtil.getNow(), 0, 0, null);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				OrderStockBean bean = service.getOrderStock("id = " + operId);
				if(!CargoDeptAreaService. hasCargoDeptArea(request, bean.getStockArea(), bean.getStockType())){
					j.setMsg("只能扫描‘用户所属的库地区’的订单!");
					return j;
    			}
				if (bean.getStatus() == OrderStockBean.STATUS3) {
					j.setMsg("该操作已经完成，不能再更改!");
					return j;
				}
				Iterator itr = null;
				OrderStockProductBean sh = null;
				List logList = new ArrayList();	//存放StockBatchLogBean（财务报表用）
				if (bean.getStatus() != OrderStockBean.STATUS6) {
					service.releaseAll();
					j.setMsg("该操作的状态不是复核，不能确认出货!");
					return j;
				}

				//log记录
				voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
				if (admin == null) {
					j.setMsg("当前没有登录，添加失败!");
					return j;
				}
				StockAdminHistoryBean log = new StockAdminHistoryBean();
				log.setAdminId(admin.getId());
				log.setAdminName(admin.getUsername());
				log.setLogId(bean.getId());
				log.setLogType(StockAdminHistoryBean.ORDER_STOCK_STATUS3);
				log.setType(StockAdminHistoryBean.CHANGE);
				//添加扫描日志
				String scanProductBatchCode = StringUtil.convertNull(request.getParameter("scanProductBatcodeName"));
				//判断是否扫描进来
				if(scanProductBatchCode.length()>0){
					String tmp = null;
					BufferedReader reader = new BufferedReader(new StringReader(scanProductBatchCode));
					StringBuffer scanLog = new StringBuffer();
					scanLog.append("扫描复核出库：");
					log.setOperDatetime(DateUtil.getNow());
					int lineNum=0;//行数
					while((tmp=reader.readLine())!=null){
						lineNum++;
						String[] s = tmp .split(",");
						if(tmp.equals("reset")){
							scanLog.append("重新扫描：");
						}else{	
							if(s.length>2)
								scanLog.append("扫描").append(s[0]).append("(").append(s[1]).append(")，").append("手动修改(").append(s[2]).append(")</br>");
							else
								scanLog.append("扫描").append(s[0]).append("(").append(s[1]).append(")").append("</br>");
						}
						if(lineNum%2==0){//每5行添加一次日志
							log.setOperDatetime(DateUtil.getNow());
							log.setRemark(scanLog.toString());
							if(!service.addStockAdminHistory(log)){
								service.getDbOp().rollbackTransaction();
								j.setMsg("订单出库日志添加操作失败!");
								return j;
							}
							scanLog = new StringBuffer();
						}
					}
					if(!scanLog.toString().equals("")){
						log.setOperDatetime(DateUtil.getNow());
						log.setRemark(scanLog.toString());
						if(!service.addStockAdminHistory(log)){
							service.getDbOp().rollbackTransaction();
							j.setMsg("订单出库日志添加操作失败!");
							return j;
						}
					}
				}
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("完成订单出货操作：" + bean.getName());
				if(!service.addStockAdminHistory(log)){
					service.getDbOp().rollbackTransaction();
					j.setMsg("订单出库日志添加操作失败!");
					return j;
				}

				String condition = "order_stock_id = " + bean.getId();
				ArrayList shList = service.getOrderStockProductList(condition, 0, -1, "id");

				if(!service.updateOrderStock("status = " + OrderStockBean.STATUS3 + ", last_oper_time = now()", "id = " + bean.getId())){
					service.getDbOp().rollbackTransaction();
					j.setMsg("订单出库状态更新操作失败!");
					return j;
				}
				//如果该订单是货到付款分成推广订单，改为“预计可以分成”
				voOrder order = adminService.getOrder("code='" + bean.getOrderCode() + "'");
				if(order.getStatus() == 3){ //如果订单是代发货状态，才自动修改订单的状态为已发货或已结算，并添加订单状态修改日志
					int oriStatus = order.getStatus();
					int status = 6;
					switch(order.getBuyMode()){
					case 0:
						status = 6;
						break;
					case 1:
					case 2:
						//邮购"已到款"和上门自取"已发货"订单做了出货时,订单状态自动修改成"已妥投",以前是"已结算"
						status = 14;
						break;
					default:
						status = 6;
					}
					// 如果该订单状态是“待发货”（邮购是“已到款”），则将该订单状态改为“已发货”
					// 非邮购订单变为 已发货
					if(!service.getDbOp().executeUpdate("update user_order set status=" + status + " where status=3 and code='" + bean.getOrderCode() + "'")){
						service.getDbOp().rollbackTransaction();
						j.setMsg("订单状态更新操作失败!");
						return j;
					}
					order.setStatus(status);
					// 邮购订单变为 已结算
					//service.getDbOp().executeUpdate("update user_order set status=12 where buy_mode in (1,2) and status=3 and code='" + bean.getOrderCode() + "'");

					//订单发货状态 改为 发货成功
					if(!service.getDbOp().executeUpdate("update user_order set stockout_deal=" + OrderStockStatusBean.STATUS_STOCKOUT_SUCCESS + " where code='" + bean.getOrderCode() + "'")){
						service.getDbOp().rollbackTransaction();
						j.setMsg("订单发货状态更新操作失败!");
						return j;
					}
					order.setStockoutDeal(OrderStockStatusBean.STATUS_STOCKOUT_SUCCESS);

					StringBuilder logContent = new StringBuilder();
					// 如果修改了订单的状态，就记录操作日志
					logContent.append("[订单状态:");
					logContent.append(oriStatus);
					logContent.append("->");
					logContent.append(status);
					logContent.append("]");
					if(logContent.length() > 0){
						IAdminLogService logService = ServiceFactory.createAdminLogService(IBaseService.CONN_IN_SERVICE, null);
						try{
							OrderAdminLogBean oalog = new OrderAdminLogBean();
							oalog.setType(OrderAdminLogBean.ORDER_ADMIN_PROP);
							oalog.setUserId(admin.getId());
							oalog.setUsername(admin.getUsername());
							oalog.setOrderId(order.getId());
							oalog.setOrderCode(order.getCode());
							oalog.setCreateDatetime(DateUtil.getNow());
							oalog.setContent(logContent.toString());
							adminService.addOrderAdminStatusLog(logService, oriStatus, status, -1, -1, oalog);
							if(!logService.addOrderAdminLog(oalog)){
								service.getDbOp().rollbackTransaction();
								j.setMsg("订单日志添加操作失败!");
								return j;
							}
						} catch(Exception e) {
							e.printStackTrace();
						} finally {
							logService.releaseAll();
						}
					}
				}
				voProduct product = null;
				String set = null;
				itr = shList.iterator();
				while (itr.hasNext()) {
					sh = (OrderStockProductBean) itr.next();
					//出库
					product = adminService.getProduct(sh.getProductId(), service.getDbOp());
					product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
					ProductStockBean ps = psService.getProductStock("id=" + sh.getStockoutId());

					set = "status = " + OrderStockProductBean.DEALED
					+ ", remark = concat(remark, '(操作前锁定库存" + ps.getLockCount()
					+ ",操作后锁定库存" + (ps.getLockCount() - sh.getStockoutCount())
					+ ")'), deal_datetime = now()";
					if(!service.updateOrderStockProduct(set, "id = " + sh.getId())){
						service.getDbOp().rollbackTransaction();
						j.setMsg("数据库操作失败!");
						return j;
					}
					//psService.updateProductStock("lock_count=(lock_count - " + sh.getStockoutCount() + ")", "id=" + sh.getStockoutId());
					if(!psService.updateProductLockCount(sh.getStockoutId(), -sh.getStockoutCount())){
						service.getDbOp().rollbackTransaction();
						j.setMsg("库存操作失败，可能是库存不足，请与管理员联系!");
						return j;
					}

					//更新货位库存记录
					product.setPsList(psService.getProductStockList("product_id=" + sh.getProductId(), -1, -1, null));
					List ospcList = service.getOrderStockProductCargoList("order_stock_product_id = "+sh.getId(), -1, -1, "id asc");
					if(ospcList == null||ospcList.size() == 0){
						service.getDbOp().rollbackTransaction();
						j.setMsg("订单出库无相应货位信息，请与管理员联系!");
						return j;
					}
					Iterator ospcIter = ospcList.listIterator();
					while(ospcIter.hasNext()){
						OrderStockProductCargoBean ospc = (OrderStockProductCargoBean)ospcIter.next();
						CargoProductStockBean cps = cargoService.getCargoAndProductStock("cps.id = "+ospc.getCargoProductStockId());
						if(!cargoService.updateCargoProductStockLockCount(ospc.getCargoProductStockId(), -ospc.getCount())){
							service.getDbOp().rollbackTransaction();
							j.setMsg("货位库存操作失败，货位库存不足!");
							return j;
						}

						//货位出库卡片
						cps = cargoService.getCargoAndProductStock("cps.id = "+ospc.getCargoProductStockId());
						CargoStockCardBean csc = new CargoStockCardBean();
						csc.setCardType(CargoStockCardBean.CARDTYPE_ORDERSTOCK);
						csc.setCode(bean.getOrderCode());
						csc.setCreateDatetime(DateUtil.getNow());
						csc.setStockType(bean.getStockType());
						csc.setStockArea(bean.getStockArea());
						csc.setProductId(sh.getProductId());
						csc.setStockId(cps.getId());
						csc.setStockOutCount(ospc.getCount());
						csc.setStockOutPriceSum((new BigDecimal(ospc.getCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
						csc.setCurrentStock(product.getStock(bean.getStockArea(), bean.getStockType()) + product.getLockCount(bean.getStockArea(), bean.getStockType()));
						csc.setAllStock(product.getStockAll() + product.getLockCountAll());
						csc.setCurrentCargoStock(cps.getStockCount()+cps.getStockLockCount());
						csc.setCargoStoreType(cps.getCargoInfo().getStoreType());
						csc.setCargoWholeCode(cps.getCargoInfo().getWholeCode());
						csc.setStockPrice(product.getPrice5());
						csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
						if(!cargoService.addCargoStockCard(csc)){
							service.getDbOp().rollbackTransaction();
							j.setMsg("添加失败!");
							return j;
						}
					}

					//更新批次记录、添加订单出货批次记录
					List sbList = service.getStockBatchList("product_id="+sh.getProductId()+" and stock_type="+sh.getStockType()+" and stock_area="+sh.getStockArea(), -1, -1, "id asc");
					float stockOutPrice = 0;
					if(sbList!=null&&sbList.size()!=0){
						int batchCount = sh.getStockoutCount();
						int index = 0;
						int stockOutCount = 0;

						do {
							StockBatchBean batch = (StockBatchBean)sbList.get(index);
							int ticket = 0;	//是否含票 
							int _count = FinanceProductBean.queryCountIfTicket(db, sh.getProductId(), ticket);	//出库前库存总量
							if(batchCount>=batch.getBatchCount()){
								if(!service.deleteStockBatch("id="+batch.getId())){
									service.getDbOp().rollbackTransaction();
									j.setMsg("批次量更新操作失败!");
									return j;
								}
								stockOutCount = batch.getBatchCount();
							}else{
								if(!service.updateStockBatch("batch_count = batch_count-"+batchCount, "id="+batch.getId())){
									service.getDbOp().rollbackTransaction();
									j.setMsg("批次量更新操作失败!");
									return j;
								}
								stockOutCount = batchCount;
							}
							batchCount -= batch.getBatchCount();
							index++;

							//添加批次操作记录
							StockBatchLogBean batchLog = new StockBatchLogBean();
							batchLog.setCode(bean.getOrderCode());
							batchLog.setStockType(batch.getStockType());
							batchLog.setStockArea(batch.getStockArea());
							batchLog.setBatchCode(batch.getCode());
							batchLog.setBatchCount(stockOutCount);
							batchLog.setBatchPrice(batch.getPrice());
							batchLog.setProductId(batch.getProductId());
							batchLog.setRemark("订单出货");
							batchLog.setCreateDatetime(DateUtil.getNow());
							batchLog.setUserId(admin.getId());
							if(!service.addStockBatchLog(batchLog)){
								service.getDbOp().rollbackTransaction();
								j.setMsg("添加失败!");
								return j;
							}
							logList.add(batchLog);
							//财务产品信息表---liuruilan-----
							FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + sh.getProductId());
							if(fProduct == null){
								service.getDbOp().rollbackTransaction();
								j.setMsg("查询异常，请与管理员联系!");
								return j;
							}
							float price5 = product.getPrice5();
	    					int totalCount = product.getStockAll() + product.getLockCountAll();
							float priceSum = Arith.mul(price5, totalCount);
							float priceHasticket = fProduct.getPriceHasticket();
							float priceNoticket = fProduct.getPriceNoticket();
							float priceSumHasticket = 0;
							float priceSumNoticket = 0;
							set = "price_sum =" + priceSum;
							if(ticket == 0){	//0-有票
								//计算公式：(结存总额 + (发货时批次价 * 本批数量)) / (库存总数量 + 本批数量)
								priceSumHasticket = Arith.mul(priceHasticket,  _count - stockOutCount);
								set += ", price_sum_hasticket =" + priceSumHasticket;
							}
							if(ticket == 1){	//1-无票
								priceSumNoticket = Arith.mul(priceNoticket,  _count - stockOutCount);
								set += ", price_sum_noticket =" + priceSumNoticket;
							}
							frfService.updateFinanceProductBean(set, "product_id = " + product.getId());
							
							//财务进销存卡片
							product.setPsList(psService.getProductStockList("product_id=" + sh.getProductId(), -1, -1, null));
							int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), bean.getStockArea(), bean.getStockType(), ticket, sh.getProductId());
							int stockAllType = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, bean.getStockType(), ticket, sh.getProductId());
							int stockAllArea = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), bean.getStockArea(), -1, ticket, sh.getProductId());
							FinanceStockCardBean fsc = new FinanceStockCardBean();
							fsc.setCardType(StockCardBean.CARDTYPE_ORDERSTOCK);
							fsc.setCode(bean.getOrderCode());
							fsc.setCreateDatetime(DateUtil.getNow());
							fsc.setStockType(bean.getStockType());
							fsc.setStockArea(bean.getStockArea());
							fsc.setProductId(sh.getProductId());
							fsc.setStockId(sh.getStockoutId());
							fsc.setStockInCount(stockOutCount);
							fsc.setStockAllArea(stockAllArea);
							fsc.setStockAllType(stockAllType);
							fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
							fsc.setStockPrice(product.getPrice5());
							
							fsc.setCurrentStock(currentStock);
							fsc.setType(fsc.getCardType());
							fsc.setIsTicket(ticket);
							fsc.setStockBatchCode(batch.getCode());
							fsc.setBalanceModeStockCount(_count - stockOutCount);
							if(ticket == 0){
								fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockOutCount))));
								fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
							}
							if(ticket == 1){
								fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockOutCount))));
								fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
							}
							double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(),fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
							fsc.setAllStockPriceSum(tmpPrice);
							frfService.addFinanceStockCardBean(fsc);
							//---------------liuruilan-----------

							stockOutPrice = stockOutPrice + batch.getPrice() * stockOutCount;
						} while (batchCount>0&&index<sbList.size());

					}

					// 审核通过，就加 进销存卡片
					product.setPsList(psService.getProductStockList("product_id=" + sh.getProductId(), -1, -1, null));

					// 出库卡片
					StockCardBean sc = new StockCardBean();
					sc.setCardType(StockCardBean.CARDTYPE_ORDERSTOCK);
					sc.setCode(bean.getOrderCode());
					sc.setCreateDatetime(DateUtil.getNow());
					sc.setStockType(bean.getStockType());
					sc.setStockArea(bean.getStockArea());
					sc.setProductId(sh.getProductId());
					sc.setStockId(sh.getStockoutId());
					sc.setStockOutCount(sh.getStockoutCount());
					//						sc.setStockOutPriceSum(stockOutPrice);
					sc.setStockOutPriceSum((new BigDecimal(sh.getStockoutCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
					sc.setCurrentStock(product.getStock(sc.getStockArea(), sc.getStockType()) + product.getLockCount(sc.getStockArea(), sc.getStockType()));
					sc.setStockAllArea(product.getStock(bean.getStockArea()) + product.getLockCount(bean.getStockArea()));
					sc.setStockAllType(product.getStockAllType(bean.getStockType()) + product.getLockCountAllType(bean.getStockType()));
					sc.setAllStock(product.getStockAll() + product.getLockCountAll());
					sc.setStockPrice(product.getPrice5());
					sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
					if(!psService.addStockCard(sc)){
						service.getDbOp().rollbackTransaction();
						j.setMsg("添加失败!");
						return j;
					}
				}

				int deliver = order.getDeliver();
				int balanceType = 0;
				// 根据 快递公司，确定结算来源
				if(voOrder.deliverToBalanceTypeMap.containsKey(deliver+"")){
					balanceType=Integer.parseInt(voOrder.deliverToBalanceTypeMap.get(deliver+"").toString());
				}
				//添加一个空的订单结算信息， 以后导入结算信息的时候，直接在上面修改
				MailingBalanceBean mb = new MailingBalanceBean();
				mb.setOrderId(bean.getOrderId());
				mb.setOrderCode(bean.getOrderCode());
				mb.setPrice(order.getDprice());
				mb.setBalanceStatus(MailingBalanceBean.BALANCE_STATUS_UNDEFINE);
				mb.setOrderCreateDatetime(DateUtil.formatDate(order.getCreateDatetime()));
				mb.setStockoutDatetime(DateUtil.getNow());
				mb.setName(order.getName());
				mb.setBalanceCheck(MailingBalanceBean.BALANCE_NOIMPORT);
				mb.setPackagenum("");
				mb.setStockoutStatus(OrderStockBean.STATUS3);
				mb.setBuyMode(order.getBuyMode());
				mb.setBalanceType(balanceType);
				mb.setBalanceArea(-1);
				if(!baService.addMailingBalance(mb)){
					service.getDbOp().rollbackTransaction();
					j.setMsg("添加失败!");
					return j;
				}
				
				// 异常结算数据表里添加一条空记录
				FinanceMailingBalanceBean fmb = new FinanceMailingBalanceBean();
				fmb.setOrderId(order.getId());
				fmb.setOrderCode(bean.getOrderCode());
				fmb.setOrderPrice(order.getDprice());
				fmb.setBalanceStatus(MailingBalanceBean.BALANCE_STATUS_UNDEFINE);
				fmb.setStockoutDatetime(DateUtil.getNow());
				fmb.setBalanceCheck(MailingBalanceBean.BALANCE_NOIMPORT);
				fmb.setPackageNum("");
				fmb.setBuyMode(order.getBuyMode());
				fmb.setBalanceType(balanceType);
				fmb.setBalanceArea(-1);
				fmb.setDataType(0);			// 0-正常
				fmb.setCreateDateTime(DateUtil.getNow());
				if(!balanceService.addFinanceMailingBalanceBean(fmb)){
					service.getDbOp().rollbackTransaction();
					j.setMsg("添加失败!");
					return j;
				}

				if(order.getBuyMode() == Constants.BUY_TYPE_NIFFER || order.getCode().startsWith("S")){
					if(!OrderRefundService.modifyAfterSaleOrderStatus(order, admin,service.getDbOp())){
						service.getDbOp().rollbackTransaction();
						j.setMsg("售后换货单退款到钱包或者更新售后退款单状态操作失败!");
						return j;
					}
				}
				//修改核对包裹记录
				if(!service.updateAuditPackage("check_datetime='"+DateUtil.getNow()+"',check_user_name='"+user.getUsername()+"',status="+AuditPackageBean.STATUS3, "order_id="+order.getId())){
					service.getDbOp().rollbackTransaction();
					j.setMsg("核对包裹记录更新操作失败!");
					return j;
				}
				String province = "";
				String city = "";
				String county = "";
				String street = "";
				String address = "";
				String sqlAddress = "select b.add_id1,b.add_id2,b.add_id3,b.add_id4,b.add_5 from user_order a join user_order_extend_info  b on a.code=b.order_code where a.id=" + order.getId();
				ResultSet rs2 = adminService.getDbOperation().executeQuery(sqlAddress);
				int province_id = 0;
				int city_id = 0;
				int county_id = 0;
				int street_id = 0;
				while (rs2.next()) {
					province_id = rs2.getInt("b.add_id1");
					city_id = rs2.getInt("b.add_id2");
					county_id = rs2.getInt("b.add_id3");
					street_id = rs2.getInt("b.add_id4");
					address = rs2.getString("b.add_5");
				}
				rs2.close();
				// 查找省
				if (province_id!=0) {
					String sqlProvince = "select name from provinces where id=" + province_id;
					ResultSet rsProvince = adminService.getDbOperation().executeQuery(sqlProvince);
					while (rsProvince.next()) {
						province = rsProvince.getString("name");
					}
					rsProvince.close();
				}
				// 查找市
				if (city_id!=0) {
					String sqlProvince = "select city from province_city where id=" + city_id;
					ResultSet rsCity = adminService.getDbOperation().executeQuery(sqlProvince);
					while (rsCity.next()) {
						city = rsCity.getString("city");
					}
					rsCity.close();
				}
				// 查找区县
				if (county_id!=0) {
					String sqlProvince = "select area from city_area where id=" + county_id;
					ResultSet rsCounty = adminService.getDbOperation().executeQuery(sqlProvince);
					while (rsCounty.next()) {
						county = rsCounty.getString("area");
					}
					rsCounty.close();
				}
				// 查找街道
				if (street_id!=0) {
					String sqlStreet = "select street from area_street where id=" + street_id;
					ResultSet rsStreet = adminService.getDbOperation().executeQuery(sqlStreet);
					while (rsStreet.next()) {
						street = rsStreet.getString("street");
					}
					rsStreet.close();
				}
				street=street+address;
				//处理宅急送或贵州邮政的包裹单号(新复核流程)
				if(order.getDeliver()==10||order.getDeliver()==35){//新复核流程下的宅急送订单或贵州邮政订单
					DbOperation tempDbop=new DbOperation();
					tempDbop.init("adult");
					int packageId=0;
					String packageCode=null;
					ResultSet tempRs=tempDbop.executeQuery("select id,package_code from deliver_package_code where deliver="+order.getDeliver()+" and used=0 limit 1");
					if(tempRs.next()){
						packageId=tempRs.getInt("id");
						packageCode=tempRs.getString("package_code");
					}
					tempRs.close();
					if(packageCode==null){
						service.getDbOp().rollbackTransaction();
						tempDbop.release();
						j.setMsg(voOrder.deliverMapAll.get(order.getDeliver()+"")+"已经没有可用的包裹单!");
						return j;
					}
					if(order.getDeliver()==35){//宅急送暂时去掉提示
						ResultSet tempRs2=tempDbop.executeQuery("select count(id) from deliver_package_code where deliver="+order.getDeliver()+" and used=0");
						if(tempRs2.next()){//剩余包裹单小于5000时提示
							int count=tempRs2.getInt("count(id)");
							if(count<5000){
								request.setAttribute("tip", voOrder.deliverMapAll.get(order.getDeliver()+"")+"剩余包裹单已小于5000！");
							}
						}
						tempRs2.close();
					}
					
					tempDbop.executeUpdate("update deliver_package_code set used=1 where id="+packageId);
					tempDbop.release();
					
					OrderPackageAction orderPackage=new OrderPackageAction();
					int checkStatus=orderPackage.importPackage(order,packageCode,user,request,db,true);
					if(checkStatus==4){
						service.getDbOp().rollbackTransaction();
						j.setMsg("快递公司错误!");
						return j;
					}else if(checkStatus==5){
						service.getDbOp().rollbackTransaction();
						j.setMsg("没有设置结算周期!");
						return j;
					} else if (checkStatus == 6) {
						service.getDbOp().rollbackTransaction();
						j.setMsg("数据异常!");
						return j;
					}
				}else if(order.getDeliver()==12){//新复核流程下的顺丰订单
					DbOperation tempDbop=new DbOperation();
					tempDbop.init("adult_slave");
					//int packageId=0;
					String packageCode=null;
					ResultSet tempRs=tempDbop.executeQuery("select package_code from deliver_package_code2 where deliver=12 and order_id="+order.getId()+" limit 1");
					if(tempRs.next()){
						//packageId=tempRs.getInt("id");
						packageCode=tempRs.getString("package_code");
					}
					tempRs.close();
					if(packageCode==null){
						service.getDbOp().rollbackTransaction();
						tempDbop.release();
						j.setMsg("顺丰包裹单号错误!");
						return j;
					}
					tempDbop.release();
					OrderPackageAction orderPackage=new OrderPackageAction();
					int checkStatus=orderPackage.importPackage(order,packageCode,user,request,db,true);
					if(checkStatus==4){
						service.getDbOp().rollbackTransaction();
						j.setMsg("快递公司错误!");
						return j;
					}else if(checkStatus==5){
						service.getDbOp().rollbackTransaction();
						j.setMsg("没有设置结算周期!");
						return j;
					} else if (checkStatus == 6) {
						service.getDbOp().rollbackTransaction();
						j.setMsg("数据异常!");
						return j;
					}
				}else if(deliver==9||deliver==28||deliver==37||deliver==31||deliver==29){//新复核流程EMS省外订单
					String packageCode=null;
					//广速省外与江西邮政均取广速省外的包裹单号
					ResultSet tempRs= service.getDbOp().executeQuery("select package_code from deliver_package_code where deliver=9 and used=0 limit 1");
					if(tempRs.next()){
						packageCode=tempRs.getString("package_code");
					}
					tempRs.close();
					if(packageCode==null){
						service.getDbOp().rollbackTransaction();
						j.setMsg(order.getDeliverName()+"包裹单号数量不足!");
						return j;
					}
					//更新包裹单使用状态
					String upPackageStatus ="update deliver_package_code set used=1 where package_code='"+packageCode+"'";
					if(!service.getDbOp().executeUpdate(upPackageStatus)){
						service.getDbOp().rollbackTransaction();
						j.setMsg( order.getDeliverName()+"包裹单状态更新失败!");
						return j;
					}
					ResultSet packageCountRs= service.getDbOp().executeQuery("select count(id) from deliver_package_code where deliver=9 and used=0");
					if(packageCountRs.next()){
						int count = packageCountRs.getInt("count(id)");
						if(count<5000){
							request.setAttribute("swWarning", order.getDeliverName()+"包裹单号数量不足5000！");
						}
					}
					packageCountRs.close();
					OrderPackageAction orderPackage = new OrderPackageAction();
					int checkStatus = orderPackage.importPackage(order, packageCode, user, request, db,true);
					if (checkStatus == 4) {
						service.getDbOp().rollbackTransaction();
						j.setMsg("快递公司错误!");
						return j;
					} else if (checkStatus == 5) {
						service.getDbOp().rollbackTransaction();
						j.setMsg("没有设置结算周期!");
						return j;
					} else if (checkStatus == 6) {
						service.getDbOp().rollbackTransaction();
						j.setMsg("数据异常!");
						return j;
					}
					AuditPackageBean apBean = service.getAuditPackage("order_id=" + order.getId());
					if (StockServiceImpl.emsSwInterface(order, apBean.getPackageCode(),apBean.getWeight(),province, city, county, street, bean.getStockArea(),deliver) == false) {
						String sqlEms = "INSERT INTO ems_order_message (order_id,order_stock_id,confirm_datetime,last_oper_datetime)VALUES" + "(" + order.getId() + "," + bean.getId() + "," + "'" + DateUtil.getNow() + "'" + "," + "'" + DateUtil.getNow() + "'" + ")";
						if (!adminService.getDbOperation().executeUpdate(sqlEms)) {
							service.getDbOp().rollbackTransaction();
							j.setMsg("对订单" + order.getCode() + order.getDeliverName()+"订单信息操作失败!");
							return j;
						}
					}
				}else if(deliver==11){
					//新复核流程下的EMS省内订单
					//DbOperation tempDbop=new DbOperation();
					//tempDbop.init("adult_slave");
					String packageCode=null;
					ResultSet tempRs= service.getDbOp().executeQuery("select package_code from deliver_package_code where deliver=11 and used=0 limit 1");
					if(tempRs.next()){
						packageCode=tempRs.getString("package_code");
					}
					tempRs.close();
					if(packageCode==null){
						service.getDbOp().rollbackTransaction();
						j.setMsg("EMS省内包裹单号数量不足!");
						return j;
					}
					//更新包裹单使用状态
					String upPackageStatus ="update deliver_package_code set used=1 where package_code='"+packageCode+"'";
					if(!service.getDbOp().executeUpdate(upPackageStatus)){
						service.getDbOp().rollbackTransaction();
						j.setMsg("EMS包裹单状态更新失败!");
						return j;
					}
					ResultSet packageCountRs= service.getDbOp().executeQuery("select count(id) from deliver_package_code where deliver=11 and used=0");
					if(packageCountRs.next()){
						int count = packageCountRs.getInt("count(id)");
						if(count<5000){
							request.setAttribute("snWarning", "EMS(省内)包裹单号数量不足5000！");
						}
					}
					packageCountRs.close();
					OrderPackageAction orderPackage = new OrderPackageAction();
					int checkStatus = orderPackage.importPackage(order, packageCode, user, request, db,true);
					if (checkStatus == 4) {
						service.getDbOp().rollbackTransaction();
						j.setMsg("快递公司错误!");
						return j;
					} else if (checkStatus == 5) {
						service.getDbOp().rollbackTransaction();
						j.setMsg("没有设置结算周期!");
						return j;
					} else if (checkStatus == 6) {
						service.getDbOp().rollbackTransaction();
						j.setMsg("数据异常!");
						return j;
					}
					//if (stock.emsInterface3(order, apBean.getPackageCode(), province, city, county, street, bean.getStockArea(), orderType) == false) {
						String sqlEms = "INSERT INTO ems_order_message (order_id,order_stock_id,confirm_datetime,last_oper_datetime)VALUES" + "(" + order.getId() + "," + bean.getId() + "," + "'" + DateUtil.getNow() + "'" + "," + "'" + DateUtil.getNow() + "'" + ")";
						if (!adminService.getDbOperation().executeUpdate(sqlEms)) {
							service.getDbOp().rollbackTransaction();
							j.setMsg("对订单" + order.getCode() + "EMS订单信息操作失败!");
							return j;
						}
					//}
					
				}else {//新复核流程下的落地配
					OrderPackageAction orderPackage=new OrderPackageAction();
					int checkStatus=orderPackage.importPackage(order,order.getCode(),user,request,db,true);
					if(checkStatus==4){
						service.getDbOp().rollbackTransaction();
						j.setMsg("快递公司错误!");
						return j;
					}else if(checkStatus==5){
						service.getDbOp().rollbackTransaction();
						j.setMsg("没有设置结算周期!");
						return j;
					} else if (checkStatus == 6) {
						service.getDbOp().rollbackTransaction();
						j.setMsg("数据异常!");
						return j;
					}
				}
				//订单批次波次状态修改
				SortingBatchOrderBean sbo=siService.getSortingBatchOrderInfo("delete_status<>1 and order_id="+order.getId());
				if(sbo!=null){//该订单已经被添加进批次，修改分拣批次信息
					if(sbo.getStatus()!=SortingBatchOrderBean.STATUS2){
						service.getDbOp().rollbackTransaction();
						j.setMsg("已删除订单，需要重新分拣!");
						return j;
					}
					if(!siService.updateSortingBatchOrderInfo("status=3", "order_id="+order.getId())){
						service.getDbOp().rollbackTransaction();
						j.setMsg("对波次中的订单"+order.getCode()+"状态更新操作失败!");
						return j;
					}
					//分拣中订单数量
					if (sbo.getSortingGroupId() != 0) {
						int sortingOrderCount = siService.getSortingBatchOrderCount("delete_status<>1 and status<>3 and sorting_group_id=" + sbo.getSortingGroupId());// 计算该波次中未分拣完成的订单数量
						if (sortingOrderCount == 0) {
							if (!siService.updateSortingBatchGroupInfo("status=2,complete_datetime='" + DateUtil.getNow() + "'", "id=" + sbo.getSortingGroupId())) {
								service.getDbOp().rollbackTransaction();
								j.setMsg("对波次" + sbo.getSortingGroupCode() + "状态更新操作失败!");
								return j;
							}
							int groupCount = siService.getSortingBatchGroupCount("status<>2 and status<>3 and sorting_batch_id=" + sbo.getSortingBatchId());// 计算该批次中未分拣完成的波次数量
							if (groupCount == 0) {
								if (!siService.updateSortingBatchInfo("status=4,complete_datetime='" + DateUtil.getNow() + "'", "id=" + sbo.getSortingBatchId())) {
									service.getDbOp().rollbackTransaction();
									j.setMsg("对批次" + sbo.getSortingBatchCode() + "状态更新操作失败!");
									return j;
								}
							}
						}
					} else {
						int sortingOrderCount = siService.getSortingBatchOrderCount("delete_status<>1 and status<>3 and sorting_batch_id=" + sbo.getSortingBatchId());// 计算该波次中未分拣完成的订单数量
						if (sortingOrderCount == 0) {
							if (!siService.updateSortingBatchInfo("status=4,complete_datetime='" + DateUtil.getNow() + "'", "id=" + sbo.getSortingBatchId())) {
								service.getDbOp().rollbackTransaction();
								j.setMsg("对批次" + sbo.getSortingBatchCode() + "状态更新操作失败!");
								return j;
							}
						}
					}
				}
				//将订单数据写入发货信息表（财务统计用）----2012-11-22--挪至此位置---
				float dprices = 0.0f;
				int fsId = 0;

				FinanceSellBean fsBean = new FinanceSellBean();
				int deliverType = 0;
				if(voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()) != null){
					deliverType = Integer.parseInt(voOrder.deliverToBalanceTypeMap.get("" + order.getDeliver()).toString());
				}
				fsBean.setOrderId(order.getId());
				fsBean.setCode(order.getCode());
				fsBean.setPrice(dprices);
				fsBean.setCarriage(order.getPostage());
				fsBean.setCharge(0);
				fsBean.setBuyMode(order.getBuyMode());
				fsBean.setPayMode(order.getBuyMode()); //备用字段，取值赞同buyMode
				fsBean.setDeliverType(deliverType);
				fsBean.setCreateDatetime(DateUtil.getNow());
				fsBean.setPackageNum(order.getPackageNum());
				fsBean.setDataType(0);	//0-销售出库
				fsBean.setCount(1);
				rs = service.getDbOp().executeQuery("SELECT add_id1,add_id2,add_id3 FROM user_order_extend_info WHERE order_code = '" + order.getCode() + "'");
				if(rs.next()){
					fsBean.setAddrSub1(rs.getInt("add_id1"));
					fsBean.setAddrSub2(rs.getInt("add_id2"));
					fsBean.setAddrSub3(rs.getInt("add_id3"));
				}	// 订单三级地址
				frfService.addFinanceSellBean(fsBean);
				
				fsId = db.getLastInsertId();
				
				//将订单商品写入销售商品信息表
				//商品
				String sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
					+ "FROM product p JOIN user_order_product_split_history h ON p.id = h.product_id " 
					+ "WHERE h.order_id = ? ";
				db.prepareStatement(sql);
				pstmt = db.getPStmt();
				pstmt.setInt(1, order.getId());
				rs = pstmt.executeQuery();
				while(rs.next()){
					int pId = rs.getInt("id");
					int buyCount = rs.getInt("count");
					float price = rs.getFloat("price");
					float dPrice = rs.getFloat("dPrice");
					int parentId1 = rs.getInt("parent_id1");
					int parentId2 = rs.getInt("parent_id2");
					int parentId3 = rs.getInt("parent_id3");
					float price5 = rs.getFloat("price5");
					int productLine = 0;
					
//					FinanceProductBean fProduct = refrfService.getFinanceProductBean("product_id =" + pId);
					
					sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
					"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
					db.prepareStatement(sql);
					pstmt = db.getPStmt();
					pstmt.setInt(1,pId);
					rs0 = pstmt.executeQuery();
					if(rs0.next()){
						productLine = rs0.getInt("product_line_id");
					}
					for (int i = 0; i < logList.size(); i++) {
						if(buyCount <= 0){
							break;
						}
						int _buyCount = 0;
						StockBatchLogBean batchLog = (StockBatchLogBean) logList.get(i);
						if(batchLog != null && batchLog.getProductId() == pId){
							int batchCount = batchLog.getBatchCount();
							String batchCode = batchLog.getBatchCode();
							StockBatchBean batch = reservice.getStockBatch("code = '" + batchCode + "'");
							if(buyCount > batchCount){
								_buyCount = batchCount;
								batchLog.setBatchCount(0);
								buyCount -= batchCount;
							}else{
								_buyCount = buyCount;
								batchLog.setBatchCount(batchCount - buyCount);
								buyCount = 0;
							}
//							if(batch.getTicket() == 0){
//								price5 = fProduct.getPriceHasticket();
//							}
//							if(batch.getTicket() == 1){
//								price5 = fProduct.getPriceNoticket();
//							}
							FinanceSellProductBean fspBean = new FinanceSellProductBean();
							fspBean.setOrderId(order.getId());
							fspBean.setProductId(pId);
							fspBean.setBuyCount(_buyCount);
							fspBean.setPrice(price);
							fspBean.setDprice(dPrice);
							fspBean.setPrice5(price5);
							fspBean.setProductLine(productLine);	//财务用产品线
							fspBean.setParentId1(parentId1);
							fspBean.setParentId2(parentId2);
							fspBean.setParentId3(parentId3);
							fspBean.setCreateDatetime(DateUtil.getNow());
							fspBean.setDataType(0);	//0-商品销售出库
							
							fspBean.setBalanceMode(0);	//是否含票
							
							int supplierId = FinanceSellProductBean.querySupplier(db, batchCode);
							fspBean.setSupplierId(supplierId);
							fspBean.setFinanceSellId(fsId);
							
							frfService.addFinanceSellProductBean(fspBean);
						}
					}

					if(order.getCode().startsWith("S")){	//售后退货要算实际销售价
						dprices = Arith.add(dprices, Arith.mul(dPrice, rs.getInt("count")));
					}
				}

				//赠品
				sql = "SELECT  p.id, h.count, h.price, h.dprice, h.price5, p.parent_id1, p.parent_id2, p.parent_id3 "
					+ "FROM product p JOIN user_order_present_split_history h ON p.id = h.product_id " 
					+ "WHERE h.order_id = ? ";
				db.prepareStatement(sql);
				pstmt = db.getPStmt();
				pstmt.setInt(1, order.getId());
				rs = pstmt.executeQuery();
				while(rs.next()){
					int pId = rs.getInt("id");
					int buyCount = rs.getInt("count");
					float price = rs.getFloat("price");
					float dPrice = rs.getFloat("dPrice");
					int parentId1 = rs.getInt("parent_id1");
					int parentId2 = rs.getInt("parent_id2");
					int parentId3 = rs.getInt("parent_id3");
					float price5 = rs.getFloat("price5");
					int productLine = 0;
					
//					FinanceProductBean fProduct = refrfService.getFinanceProductBean("product_id =" + pId);
					
					sql = "SELECT fplc.product_line_id FROM product p JOIN finance_product_line_catalog fplc " +
					"ON (p.parent_id1 = fplc.catalog_id or p.parent_id2 = fplc.catalog_id) WHERE p.id = ? ";
					db.prepareStatement(sql);
					pstmt = db.getPStmt();
					pstmt.setInt(1,pId);
					rs0 = pstmt.executeQuery();
					if(rs0.next()){
						productLine = rs0.getInt("product_line_id");
					}
					for (int i = 0; i < logList.size(); i++) {
						if(buyCount <= 0){
							break;
						}
						int _buyCount = 0;
						StockBatchLogBean batchLog = (StockBatchLogBean) logList.get(i);
						if(batchLog != null && batchLog.getProductId() == pId){
							int batchCount = batchLog.getBatchCount();
							if(batchCount == 0){
								continue;
							}
							String batchCode = batchLog.getBatchCode();
							StockBatchBean batch = reservice.getStockBatch("code = '" + batchCode + "'");
							if(buyCount > batchCount){
								_buyCount = batchCount;
								buyCount -= batchCount;
							}else{
								_buyCount = buyCount;
								buyCount = 0;
							}
							//liubo临时注销，均价获取user_order_product_split_history
//							if(batch.getTicket() == 0){
//								price5 = fProduct.getPriceHasticket();
//							}
//							if(batch.getTicket() == 1){
//								price5 = fProduct.getPriceNoticket();
//							}
							FinanceSellProductBean fspBean = new FinanceSellProductBean();
							fspBean.setOrderId(order.getId());
							fspBean.setProductId(pId);
							fspBean.setBuyCount(_buyCount);
							fspBean.setPrice(price);
							fspBean.setDprice(dPrice);
							fspBean.setPrice5(price5);
							fspBean.setProductLine(productLine);	//财务用产品线
							fspBean.setParentId1(parentId1);
							fspBean.setParentId2(parentId2);
							fspBean.setParentId3(parentId3);
							fspBean.setCreateDatetime(DateUtil.getNow());
							fspBean.setDataType(1);	//1-赠品销售出库
							
							fspBean.setBalanceMode(0);	
							
							int supplierId = FinanceSellProductBean.querySupplier(db, batchCode);
							fspBean.setSupplierId(supplierId);
							fspBean.setFinanceSellId(fsId);
							
							frfService.addFinanceSellProductBean(fspBean);
						}
					}
				}
				if(!order.getCode().startsWith("S")){
					dprices = Arith.round(Arith.sub(order.getDprice(), order.getPostage()), 0);	//实际发生金额（不含运费）
				}
				frfService.updateFinanceSellBean(" price =" + dprices, "id =" + fsId);
				//物流员工绩效考核表操作 
				CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
				if(csBean == null){
					service.getDbOp().rollbackTransaction();
					j.setMsg("此账号不是物流员工 !");
					return j;
				}
				CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and type=1 and staff_id=" + csBean.getId() );
				int productCount=0;
				int operCount = 0;
				for(int i=0;i<shList.size();i++){
					OrderStockProductBean ospBean = (OrderStockProductBean)shList.get(i);
					productCount = productCount + ospBean.getStockoutCount();
				}
				if(cspBean != null){
					operCount = cspBean.getOperCount()+1;
					productCount = cspBean.getProductCount() + productCount;
					boolean flag = cargoService.updateCargoStaffPerformance(" oper_count=" + operCount+ ",product_count=" + productCount, " id=" + cspBean.getId());
					if(!flag){
						service.getDbOp().rollbackTransaction();
						j.setMsg("物流员工绩效考核更新操作失败 !");
						return j;
					}
				}else{
					CargoStaffPerformanceBean cargoStaffPerformanceBean = new CargoStaffPerformanceBean();
					cargoStaffPerformanceBean.setDate(date);
					cargoStaffPerformanceBean.setProductCount(productCount);
					cargoStaffPerformanceBean.setOperCount(1);
					cargoStaffPerformanceBean.setStaffId(csBean.getId());
					cargoStaffPerformanceBean.setType(1);  //1代表复核作业
					boolean flag = cargoService.addCargoStaffPerformance(cargoStaffPerformanceBean);
					if(!flag){
						service.getDbOp().rollbackTransaction();
						j.setMsg("物流员工绩效考核添加操作失败 !");
						return j;
					}
				}
				service.getDbOp().commitTransaction();
				j.setSuccess(true);
				j.setMsg("复核出库成功!");
				request.getSession().setAttribute("list", null);
			} catch (Exception e) {
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
			} finally {
				session.removeAttribute(key);
				service.releaseAll();
				redb.release();
				adminService.close();
			}
		}
		return j;
	}
	/**
	 *说明：打印包裹单
	 *@author 石远飞
	 */
	@RequestMapping("/printOrderStockPackage")
	public String printOrderStockPackage(HttpServletRequest request,HttpServletResponse response,
			String orderCode,String weight) throws ServletException, IOException {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(294)){
            request.setAttribute("msg", "你没有权限进行该操作，请与管理员联系!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
	 	}
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		WareService wareService = new WareService(service.getDbOp());
	    IBalanceService balanceService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE,service.getDbOp());
	    SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	    ISupplierService supplyService = ServiceFactory.createSupplierService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	    IBatchBarcodeService batchBarcodeService = ServiceFactory.createBatchBarcodeServcie(IBaseService.CONN_IN_SERVICE,service.getDbOp());
	    try {
			float weight_f=0;
			weight= StringUtil.checkNull(weight);
			if(weight!=null && weight.endsWith("kg")){
				weight = weight.substring(0, weight.length()-2).trim();
			}
			String weightReg="^\\s+\\d+\\s+\\d{1,5}\\.\\d{1,3}$";//电子秤输出格式
			String weightReg2="^\\d{1,5}\\.\\d{1,3}$";//人工输出格式
			
			if(weight.matches(weightReg)){//电子秤输出格式
				String weightReg3="\\s+\\d+\\s+";
				if(weight.split(weightReg3).length<2){
	        		request.setAttribute("msg", "重量格式错误!");
	    			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
	    			return null;
				}else{
					weight_f=StringUtil.toFloat(weight.split(weightReg3)[1]);
				}
			}else if (weight.matches(weightReg2)){//人工输出格式
				weight_f=StringUtil.toFloat(weight);
			}else{
	    		request.setAttribute("msg", "重量格式错误!");
    			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
    			return null;
			}
			weight_f*=1000;
			
			OrderStockBean orderStockBean = service.getOrderStock("code='"+ StringUtil.toSql(StringUtil.checkNull(orderCode)) +"'" + " and status!="+OrderStockBean.STATUS4);
			voOrder order = null;
			if(orderStockBean != null){
				order = wareService.getOrder("code='"+orderStockBean.getOrderCode()+"'");
			}else{
				order = wareService.getOrder("code='"+ StringUtil.toSql(StringUtil.checkNull(orderCode)) +"'");
			}
			if(order==null){
	    		request.setAttribute("msg", "订单号错误!");
    			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
    			return null;
			}
			voOrderExtendInfo orderExtInfo = service.getOrderExtendInfo("order_code='"+order.getCode()+"'");
			if(orderExtInfo == null){
				request.setAttribute("city", "");
			}else{
				SupplierCityBean cityBean = supplyService.getSupplierCityInfo("id="+orderExtInfo.getAddId2());
				if(cityBean == null){
					request.setAttribute("city","");
				}else{
					request.setAttribute("city",cityBean.getCity());
				}
			}
			OrderStockBean osBean = service
					.getOrderStock("order_code='" + order.getCode() + "' and status!=3");
			AuditPackageBean apBean = service
					.getAuditPackage("order_code='" + order.getCode() + "'");
			if(apBean==null){
				request.setAttribute("msg", "订单出库信息错误!");
    			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
    			return null;
			}
			apBean.setWeight(weight_f);
			OrderCustomerBean ocBean = batchBarcodeService.getOrderCustomerBean("order_code='" + order.getCode()+ "'");
			if (order != null) {
				if(order.getStatus()==3){
	    			request.setAttribute("msg", "该订单未复核，不允许打印包裹单!");
	    			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
	    			return null;
				}
				request.setAttribute("order", order);
				request.setAttribute("osBean", osBean);
				request.setAttribute("apBean", apBean);
				request.setAttribute("ocBean", ocBean);
				
				request.setAttribute("orderCode", apBean.getPackageCode());
				MailingBalanceBean mbBean =balanceService.getMailingBalance("order_code='"+ order.getCode() + "'");
				request.setAttribute("mbBean", mbBean);
				if(order.getDeliver()==9||order.getDeliver()==11){
					request.setAttribute("forward", "scanCheckOrderStock2");
				}else{
					request.setAttribute("forward", "scanCheckOrderStock2");
				}
				
				
				// 查询订单中产品是否包含电池
				boolean hasBattery = false;
				// 得到产品分类名称
				int orderType = 0;
				SortingBatchOrderBean sboBean=siService.getSortingBatchOrderInfo("order_id="+order.getId()+" and delete_status=0");
				if(sboBean!=null){
					orderType=sboBean.getOrderType();
				}
				String sql2 = "select name from user_order_package_type where type_id=?";
				service.getDbOp().prepareStatement(sql2);
				PreparedStatement ps2 = service.getDbOp().getPStmt();
				ps2.setInt(1, orderType);
				ResultSet rs2 = ps2.executeQuery();
				String orderTypeName = "";
				while (rs2.next()) {
					orderTypeName = rs2.getString("name");
				}
				if (hasBattery) {
					orderTypeName += "#";
				}
				if(orderTypeName.equals("护肤品")||orderTypeName.equals("保健品")
						||orderTypeName.equals("香水")||orderTypeName.equals("礼品")){
					request.setAttribute("color", "red");
				}else if(orderTypeName.equals("电子")||orderTypeName.equals("手机")
						||orderTypeName.equals("电脑")||orderTypeName.equals("玩具")){
					request.setAttribute("color", "green");
				}
				request.setAttribute("orderTypeName", orderTypeName);
				
				if(!service.updateAuditPackage("weight="+weight_f, "id="+apBean.getId())){
	    			request.setAttribute("msg", "修改订单出库信息时发生异常!");
	    			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
	    			return null;
				}
				
				PrintPackageAction ppa = new PrintPackageAction();
				request.setAttribute("dprice", ppa.toUpperNumber(order.getDprice()+""));
				
				//计算系统生成物流成本
				PrintPackageAction ppa1 = new PrintPackageAction();
				ppa1.calLogisticsCost(service, order, apBean);
				
				if(order.getDeliver()==12){//广州顺丰
					String cityCode="";
					String ccSql="select ca.code from user_order uo "+
						"join user_order_extend_info uoei on uoei.order_code=uo.code "+
						"join province_city ca on ca.id=uoei.add_id2 where uo.id="+order.getId();
					ResultSet rs=service.getDbOp().executeQuery(ccSql);
					if(rs.next()){
						if(rs.getString(1) != null){
							cityCode=rs.getString(1);
						}
					}
					rs.close();
					if(cityCode.length()>0){
						request.setAttribute("cityCode", cityCode);
					}else{
	        			request.setAttribute("msg", "顺丰区号错误!");
		    			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		    			return null;
					}
					return "admin/rec/oper/orderStock/gzsfPackage";
					
				}else if(order.getDeliver()==9&&order.getBuyMode()==0){//EMS省外货到付款
					return "admin/rec/oper/orderStock/gsswPackage";
				}else if(order.getDeliver()==9&&order.getBuyMode()!=0){//EMS省外非货到付款
					return "admin/rec/oper/orderStock/gsswPackage2";
				}else if(order.getDeliver()==37){//无锡邮政
					return "admin/rec/oper/orderStock/gsswPackage";
				}
				return "admin/rec/oper/orderStock/szzjPackage2";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return null;
	}
	
	/**
	 *说明：加载复核列表 已改为mybattis
	 *@author 石远飞
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping("/getScanOrderStockDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getScanOrderStockDatagrid(HttpServletRequest request,HttpServletResponse response,
			String osId,String scanCode) throws ServletException, IOException {
		EasyuiDataGridJson dategridJson = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		//DbOperation dbOp = new DbOperation();
		//dbOp.init("adult_slave");
		//IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		//IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		//IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		//IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		//IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		try {
			if(!"".equals(StringUtil.checkNull(scanCode))){
				List<Map<String,String>> mapList = (List<Map<String,String>>)request.getSession().getAttribute("list");
				if(mapList == null || mapList.size() == 0){
					request.setAttribute("msg", "没有可复核的数据,请重新操作!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				if("-1".equals(scanCode)){
					for(Map<String,String> m : mapList){
						m.put("count", "0");
					}
					dategridJson.setRows(mapList);
					return dategridJson;
				}
				for(Map<String,String> m : mapList){
					String productCode = m.get("productCode");
					String productBarcode = m.get("productBarcode");
					if(scanCode.equals(productCode) || scanCode.equals(productBarcode)){
						m.put("count", (Integer.parseInt(m.get("count"))+1) +"");
						break;
					}
				}
				dategridJson.setRows(mapList);
				return dategridJson;
			}
			OrderStockBean bean = mOrderStockService.getOrderStock("id = " + osId);
			if (bean == null) {
				request.setAttribute("msg", "当前记录不存在或者已经处理!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			//相关的出库记录
			String condition = "order_stock_id = " + bean.getId();
			List<OrderStockProductBean> outList = mOrderStockService.getOrderStockProductList(condition, 0, -1, "id");
			Iterator outIter = outList.listIterator();
			while (outIter.hasNext()) {
				OrderStockProductBean shb = (OrderStockProductBean) outIter.next();
				shb.setProduct(mWareService.getProduct("id=" + shb.getProductId()));
				shb.getProduct().setPsList(mProductStockService.getProductStockList("product_id=" + shb.getProductId(), -1, -1, null));
				// 文齐辉添加查找产品条形码
				shb.setProductBarcodeVO(mWareService.getProductBarcode("product_id="+shb.getProductId()+" and barcode_status=0"));
			}
			//相关的产品和赠品
			condition = "a.id in (select distinct product_id from order_stock_product where order_stock_id = " + bean.getId() + ")";
			voOrder order = mWareService.getUserOrder("code = '" + bean.getOrderCode() + "'");
			bean.setOrder(order);
			List orderProductList = mWareService.getOrderProductsSplit(order.getId());
			Iterator iter = orderProductList.listIterator();
			while(iter.hasNext()){
				voOrderProduct vop = (voOrderProduct)iter.next();
				vop.setPsList(mProductStockService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
			}
			int stockStatus = checkStock(orderProductList); //检查订单内商品的库存情况
			List orderPresentList = mWareService.getOrderPresentsSplit(order.getId());
			//如果订单内的商品库存充足，而且该订单有赠品，则判断赠品库存是否充足
			if (stockStatus == 0 && orderPresentList != null && orderPresentList.size() > 0) {
				iter = orderPresentList.listIterator();
				while(iter.hasNext()){
					voOrderProduct vop = (voOrderProduct)iter.next();
					vop.setPsList(mProductStockService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
				}
				stockStatus = checkStock(orderPresentList); //检查订单内赠品的库存情况
			}
			// 如果是广速待出货， 想从广分发货，而广分又缺货的时候，提供一个连接，自动生成调拨单
			if(bean.getStockArea() == ProductStockBean.AREA_GS && bean.getStatus() == OrderStockBean.STATUS2 && (stockStatus == 4 || stockStatus == 6)){
				request.setAttribute("gfExchange", "gfExchange");
			}
			orderProductList.addAll(orderPresentList);
			Iterator detailIter = orderProductList.listIterator();
			while(detailIter.hasNext()){
				voOrderProduct vop = (voOrderProduct)detailIter.next();
				voProduct product = mWareService.getProduct("id=" + vop.getProductId());
				if(product.getIsPackage() == 1){ // 如果这个产品是套装
					List ppList = mOrderStockService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
					Iterator ppIter = ppList.listIterator();
					while(ppIter.hasNext()){
						ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
						voOrderProduct tempVOP = new voOrderProduct();
						tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
						voProduct tempProduct = mWareService.getProduct("id=" + ppBean.getProductId());
						tempVOP.setProductId(ppBean.getProductId());
						tempVOP.setCode(tempProduct.getCode());
						tempVOP.setName(tempProduct.getName());
						tempVOP.setPrice(tempProduct.getPrice());
						tempVOP.setOriname(tempProduct.getOriname());
						order.setBaozhuangzhongliang(order.getBaozhuangzhongliang() + tempProduct.getBzzhongliang());
					}
				} else {
					order.setBaozhuangzhongliang(order.getBaozhuangzhongliang() + product.getBzzhongliang());
				}
			}
			List<Map<String,String>> list = new ArrayList<Map<String,String>>();
			if(outList != null && outList.size() > 0){
				for(OrderStockProductBean b : outList){
					Map<String,String> map = new HashMap<String, String>();
					map.put("productId", b.getProduct().getId() + "");
					map.put("productName", b.getProduct().getName());
					map.put("productOriname", b.getProduct().getOriname().replace("\r","").replace("\n","").replace("\"","”").replace("\'","‘"));
					map.put("productCode", b.getProduct().getCode());
					if(b.getProductBarcodeVO()!=null ||b.getProductBarcodeVO().getBarcode()!=null){
						map.put("productBarcode", b.getProductBarcodeVO().getBarcode());
					}
					map.put("stockOutCount", b.getStockoutCount() + "");
					map.put("count", "0");
					map.put("stockCountBJ", b.getProduct().getStock(ProductStockBean.AREA_BJ, ProductStockBean.STOCKTYPE_QUALIFIED)+ "");
					map.put("stockCountGF", b.getProduct().getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + "");
					map.put("stockCountGS", b.getProduct().getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + "");
					list.add(map);
				}
			}
			request.getSession().setAttribute("list", list);
			dategridJson.setRows(list);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//dbOp.release();
		}
		return dategridJson;
	}
	/**
	 * @return 加载出库单信息 已经改成mybatis
	 * @author syuf
	 */
	@RequestMapping("/getOrderStockBean")
	@ResponseBody
	public OrderStockBean getOrderStockBean(HttpServletRequest request,HttpServletResponse response,String orderCode) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(294)){
            request.setAttribute("msg", "您没有权限进行此操作!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
	 	}
		OrderStockBean orderStockBean = null ;
		try {
			orderStockBean = mOrderStockService.getOrderStock("code='" + StringUtil.toSql(StringUtil.checkNull(orderCode)) + "'" + " and status!="+OrderStockBean.STATUS4);
			voOrder order = null;
			if(orderStockBean != null){
				order = mWareService.getUserOrder("code='" + orderStockBean.getOrderCode() + "'");
			}else{
				order = mWareService.getUserOrder("code='" + StringUtil.toSql(StringUtil.checkNull(orderCode)) + "'");
			}
			if(order==null){
				request.setAttribute("msg", "订单号不存在!");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			StringBuilder condition = new StringBuilder();
			condition.append(" status = ");
			condition.append(OrderStockBean.STATUS6);
			condition.append(" and order_code='");
			condition.append(order.getCode());
			condition.append("' ");
			orderStockBean = mOrderStockService.getOrderStock(condition.toString());
			if (orderStockBean == null) {
				condition = new StringBuilder();
				condition.append(" status = ");
				condition.append(OrderStockBean.STATUS3);
				condition.append(" and order_code='");
				condition.append(order.getCode());
				condition.append("' ");
				orderStockBean = mOrderStockService.getOrderStock(condition.toString());
				if(orderStockBean!=null){
					request.setAttribute("msg", orderStockBean.getOrderCode()+" 您扫描的订单已复核过!");
					request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
					return null;
				}
				request.setAttribute("msg", "在等待复核的订单列表中，没有您扫描的订单");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			// 跳转到订单出货操作页面
			if(!CargoDeptAreaService. hasCargoDeptArea(request, orderStockBean.getStockArea(), orderStockBean.getStockType())){
				 request.setAttribute("msg", "只能扫描‘用户所属的库地区’的订单!");
				 request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				 return null;
			}
			AuditPackageBean apBean=mOrderStockService.getAuditPackage("order_code='"+order.getCode()+"'");
			if(apBean==null){
				 request.setAttribute("msg", "订单出库信息错误!");
				 request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				 return null;
			}
			orderStockBean.setOrderAddress(order.getAddress());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return orderStockBean;
	}
	/**
	 * @return 加载状态  已经改成mybatis
	 * @author syuf
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getOrderStockStatus")
	public void getOrderStockStatus(HttpServletRequest request,HttpServletResponse response,String operId) throws ServletException, IOException {
		response.setContentType("text/html; charset=utf-8");
		String result = null;
		try {
			OrderStockBean bean = mOrderStockService.getOrderStock("id = " + StringUtil.checkNull(operId));
			if (bean == null) {
				request.setAttribute("tip", "当前记录不存在或者已经处理！");
				request.setAttribute("result", "failure");
				return;
			}
			//相关的出库记录
			String condition = "order_stock_id = " + bean.getId();
			List<OrderStockProductBean> outList = mOrderStockService.getOrderStockProductList(condition, -1, -1, "id asc");
			Iterator outIter = outList.listIterator();
			while (outIter.hasNext()) {
				OrderStockProductBean shb = (OrderStockProductBean) outIter.next();
				shb.setProduct(mWareService.getProduct("id="+shb.getProductId()));
				shb.getProduct().setPsList(mProductStockService.getProductStockList("product_id=" + shb.getProductId(), -1, -1, null));
				// 文齐辉添加查找产品条形码
				shb.setProductBarcodeVO(mWareService.getProductBarcode("product_id="+shb.getProductId()+" and barcode_status=0"));
			}
			//相关的产品和赠品
			condition = "a.id in (select distinct product_id from order_stock_product where order_stock_id = " + bean.getId() + ")";
			voOrder order = mWareService.getUserOrder("code = '" + bean.getOrderCode() + "'");
			List orderProductList = mWareService.getOrderProductsSplit(order.getId());
			Iterator iter = orderProductList.listIterator();
			while(iter.hasNext()){
				voOrderProduct vop = (voOrderProduct)iter.next();
				vop.setPsList(mProductStockService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
			}
			int stockStatus = checkStock(orderProductList); //检查订单内商品的库存情况
			List orderPresentList = mWareService.getOrderPresentsSplit(order.getId());
			//如果订单内的商品库存充足，而且该订单有赠品，则判断赠品库存是否充足
			if (stockStatus == 0 && orderPresentList != null && orderPresentList.size() > 0) {
				iter = orderPresentList.listIterator();
				while(iter.hasNext()){
					voOrderProduct vop = (voOrderProduct)iter.next();
					vop.setPsList(mProductStockService.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
				}
				stockStatus = checkStock(orderPresentList); //检查订单内赠品的库存情况
			}
			if(bean.getStatus() == OrderStockBean.STATUS1){
				result = "<font color=\"red\">" + bean.getStatusName() + "</font> ";
				if(stockStatus == 0){
					result += OrderStockBean.getStockStatusName(stockStatus);
				}else{
					result += "<font color=\"red\">" +OrderStockBean.getStockStatusName(stockStatus) + "</font>";
				}
			}else if(bean.getStatus() == OrderStockBean.STATUS6){
				result = "<font color=\"blue\">"+ bean.getStatusName() + "</font> ";
			}else{
				result = bean.getStatusName();
			}
			// 如果是广速待出货， 想从广分发货，而广分又缺货的时候，提供一个连接，自动生成调拨单
			if(bean.getStockArea() == ProductStockBean.AREA_GS && bean.getStatus() == OrderStockBean.STATUS2 && (stockStatus == 4 || stockStatus == 6)){
				request.setAttribute("gfExchange", "gfExchange");
				result += " " + "<a href=\"${pageContext.request.contextPath}/productStock/createStockExchange.jsp?type=2&srcId=<%= bean.getId() %>&stockInArea=1&stockInType=0&stockOutArea=2&stockOutType=0&forward=auto\" target=\"_blank\" >生成调拨单(广速-广分)</a>";
			}
			response.getWriter().write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			
		}
	}
	/**
	 * @return 读取作业排名信息
	 * @author syuf
	 */
	@RequestMapping("/getCargoStaffPerformance")
	public void getCargoStaffPerformance(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		}
		response.setContentType("text/html; charset=utf-8");
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		String result = null;
		try {
			String firstCount = "";
			String oneselfCount = "";
			String ranking ="";
			String productCount="";
			String photoUrl = Constants.STAFF_PHOTO_URL + "/";
			int n = 1;
			
			CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
			if(csBean == null){
				request.setAttribute("msg", "此账号不是物流员工 !");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			}
			CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + csBean.getId() + " and type=1");
			List<CargoStaffPerformanceBean> cspList = cargoService.getCargoStaffPerformanceList(" date='" + date + "' and type=1", -1, -1, " oper_count DESC");
			if(cspBean != null){
				for(int i = 0;i < cspList.size();i++){
					CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
					bean = cspList.get(i);
					if(i==0){
						firstCount = bean.getOperCount() + "";
						CargoStaffBean winBean = cargoService.getCargoStaff(" id=" + bean.getStaffId());
						photoUrl += winBean.getPhotoUrl();//获取冠军头像的URl
						if(cspBean.getOperCount() >= bean.getOperCount()){
							productCount = cspBean.getProductCount()+"";
							ranking = "排名第" + n ;
							oneselfCount = cspBean.getOperCount()+"";
							break;
						}else{
							n++;
						}
					}else{
						if(cspBean.getOperCount() >= bean.getOperCount()){
							ranking = "排名第" + n ;
							productCount = cspBean.getProductCount()+"";
							oneselfCount = cspBean.getOperCount()+"";
							break;
						}else{
							n++;
						}
					}
				}
			}else{
				if(cspList != null && cspList.size() > 0){
					CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
					bean = cspList.get(0);
					CargoStaffBean winBean = cargoService.getCargoStaff(" id=" + bean.getStaffId());
					photoUrl += winBean.getPhotoUrl();//获取冠军头像URL
					firstCount = bean.getOperCount() + "";
					ranking = "尚无名次";
					oneselfCount = "0";
					productCount = "0";
				}else{
					firstCount = "0";
					photoUrl = null;
					ranking = "尚无名次";
					oneselfCount = "0";
					productCount = "0";
				}
			}
			if(photoUrl != null){
				result = "你已复核" + oneselfCount + "个,商品" + productCount + "件," + ranking + ",冠军" +
						"<img alt=\"\" width=\"83\" height=\"100\" src=\"" + photoUrl + "\"/>" + firstCount + "个,继续加油~";
			}else{
				result = "你已复核" + oneselfCount + "个,商品" + productCount + "件," + ranking + ",冠军" + firstCount + "个,继续加油~";
			}
			response.getWriter().write(result);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
	}
	/**
	 * 说明：检查订单商品库存是否充足。
	 * 
	 * 参数及返回值说明：
	 * 
	 * 0表示两地库存都充足。/三地库存充足
	 * 
	 * 1表示北京库存充足。/北库库存充足/广分缺货/广速缺货
	 * 
	 * 2表示广东库存充足。/广分库存充足/北库缺货/广速缺货
	 * 
	 * 3表示两地库存都不足。/三地库存都不足
	 * 
	 * 4表示广速库存充足/北库缺货/广分缺货
	 * 
	 * 5广速缺货
	 * 
	 * 6广分缺货
	 * 
	 * 7北库缺货
	 * 
	 * zt修改(20090509)
	 * 
	 * @param orderProductList
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public int checkStock(List orderProductList) {
		if (orderProductList == null) {
			return 3;
		}

		Iterator itr = orderProductList.iterator();
		boolean zc = true;
		boolean gd = false; //2012-05-30  芳村停止发货，只增城发货
		boolean gs = false;
		voOrderProduct op = null;
		while (itr.hasNext()) {
			op = (voOrderProduct) itr.next();
			if (op.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				zc = false;
			}
		}
		if (zc && gd && gs) {
			return 0;
		}
		if (zc && !gd && !gs) {
			return 1;
		}
		if (gd && !zc && !gs) {
			return 2;
		}
		if (gs && !zc && !gd) {
			return 4;
		}
		if (zc && gd && !gs){
			return 5;
		}
		if (zc && gs && !gd){
			return 6;
		}
		if (gd && gs && !zc){
			return 7;
		}
		return 3;
	}
	/**
	 * 
	 * 说明：判断订单出货地区的库存是否充足
	 * 参数及返回值说明：
	 * @param orderProductList
	 * @param area
	 *            0:北库 <br/>1:广分 <br/>2:广速<br/>
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public boolean checkStockInArea(List orderProductList, int area) {
		if (orderProductList == null) {
			return false;
		}

		Iterator itr = orderProductList.iterator();
		boolean result = true;
		voOrderProduct op = null;
		while (itr.hasNext()) {
			op = (voOrderProduct) itr.next();
			if (op.getStock(area, ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				result = false;
				return result;
			}
		}

		return result;
	}
}

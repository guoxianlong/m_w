package mmb.rec.pda.controller;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import mmb.finance.stat.FinanceStockCardBean;
import mmb.rec.oper.service.ConsignmentService;
import mmb.rec.pda.bean.JsonModel;
import mmb.rec.pda.util.CheckUser;
import mmb.rec.pda.util.JsonModelUtil;
import mmb.rec.pda.util.PDAUtil;
import mmb.rec.pda.util.ReceiveJson;
import mmb.stock.cargo.CargoOperationTodoBean;
import mmb.stock.cargo.CartonningInfoAction;
import mmb.stock.cargo.CartonningInfoBean;
import mmb.stock.cargo.CartonningInfoService;
import mmb.stock.cargo.CartonningProductInfoBean;
import mmb.stock.cargo.CartonningStandardCountBean;
import mmb.stock.stat.AbnormalCargoCheckBean;
import mmb.stock.stat.AbnormalCargoCheckProductBean;
import mmb.stock.stat.BuyStockinUpshelfBean;
import mmb.stock.stat.CheckStockinMissionService;
import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.stock.stat.ProductWarePropertyBean;
import mmb.stock.stat.ReturnedPackageService;
import mmb.stock.stat.ReturnedPackageServiceImpl;
import mmb.stock.stat.SortingAbnormalBean;
import mmb.stock.stat.SortingAbnormalDisposeService;
import mmb.stock.stat.SortingAbnormalProductBean;
import mmb.stock.stat.SortingAgainBean;
import mmb.stock.stat.SortingBatchBean;
import mmb.stock.stat.SortingBatchGroupBean;
import mmb.stock.stat.SortingBatchOrderBean;
import mmb.stock.stat.SortingBatchOrderProductBean;
import mmb.stock.stat.SortingInfoService;
import mmb.stock.stat.StatService;
import mmb.system.admin.AdminService;
import mmb.ware.WareService;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.bean.cargo.CargoDeptBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoOperLogBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationLogBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.cargo.CargoStaffPerformanceBean;
import adultadmin.bean.order.AuditPackageBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.MailingBatchBean;
import adultadmin.bean.stock.MailingBatchPackageBean;
import adultadmin.bean.stock.MailingBatchParcelBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockBatchBean;
import adultadmin.bean.stock.StockBatchLogBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.impl.CargoServiceImpl;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.Arith;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
/**
 * 说明：pda功能action
 * 
 * 时间：2013-08-01
 * 
 * 作者：石远飞
 */
@Controller
@RequestMapping("/StockOperationController")
public class StockOperationController {
	public static byte[] cargoLock = new byte[0];
	private String date = DateUtil.formatDate(new Date());
	public Log debugLog = LogFactory.getLog("debug.Log");
	/**
	 * 说明：异常货位盘点
	 * 
	 * 时间：2013-08-22
	 * 
	 * 作者：石远飞
	 */
	@RequestMapping("/inventoryAbnormalCargo")
	@ResponseBody
	public JsonModel inventoryAbnormalCargo(HttpServletRequest request){
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		SortingAbnormalDisposeService service = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,dbOp);
		List<AbnormalCargoCheckProductBean> accpBeanList = null;
		voUser user = null;
		JsonModel json =  new JsonModel();
		try{
			json = ReceiveJson.receiveJson(request);//从流中读取json数据
			if(json==null){
				return returnError("没有收到请求数据!");
			}
			if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){//验证用户名密码
				user = (voUser)request.getSession().getAttribute("userView");
				int areaId = StringUtil.toInt(json.getArea());//地区id
				String status = (Integer)json.getData().get("status")+"";
				String wholeCode = StringUtil.convertNull((String)json.getData().get("wholeCode"));
				String productCode = StringUtil.convertNull((String)json.getData().get("productCode"));
				int flag = (Integer)json.getData().get("flag");//盘点阶段标识
				int accpId = StringUtil.toInt((String)json.getData().get("accpId"));//id
				int count = -1;
				if(json.getData().get("count")!= null){
					count = (Integer)json.getData().get("count");//盘点数量
				}
				if(status != null && !"".equals(status)){
					if("1".equals(status)){
						synchronized (cargoLock) {
							dbOp.startTransaction();
							Map<String,List<AbnormalCargoCheckProductBean>>  map = service.getaccpMap(null,dbOp,flag,user,areaId);
							for(Map.Entry<String, List<AbnormalCargoCheckProductBean>> entry : map.entrySet()){
								if(!"success".equals(entry.getKey())){
									dbOp.rollbackTransaction();
									return returnError(entry.getKey());
								}else{
									accpBeanList = entry.getValue();
								}
							}
							if(accpBeanList == null ||  accpBeanList.size() == 0){
								dbOp.rollbackTransaction();
								return returnError("找不到计划相关联的盘点数据!");
							}
							dbOp.commitTransaction();							
							System.out.println("用户[" +user.getId() + "]领单 : Flag = " + flag);
							return returnInventoryAbnormalCargo(accpBeanList,flag+"",status);
						}
					}else if("2".equals(status)){
						synchronized (cargoLock) {
							if(count < 0){
								return returnError("盘点数量错误!");
							}
							AbnormalCargoCheckProductBean accpBean = service.getAbnormalCargoCheckProduct("id=" + accpId);
							if(accpBean == null){
								return returnError("盘点任务不存在!");
							}
							AbnormalCargoCheckBean accBean = service.getAbnormalCargoCheck("id=" + accpBean.getAbnormalCargoCheckId());
							if (accBean == null) {
								return returnError("盘点计划不存在!");
							}
							if (accBean.getStatus() != AbnormalCargoCheckBean.STATUS0 
									&& accBean.getStatus() != AbnormalCargoCheckBean.STATUS1
									&& accBean.getStatus() != AbnormalCargoCheckBean.STATUS2
									&& accBean.getStatus() != AbnormalCargoCheckBean.STATUS3) {
								return returnError("盘点计划已盘点完毕!");
							}
							
							voProduct product = service.getProductByCode(StringUtil.toSql(productCode));
							if(product == null){
								return returnError("此商品信息不存在!");
							}
							CargoInfoBean ciBean = cargoService.getCargoInfo("whole_code='" + StringUtil.toSql(wholeCode) + "'");
							if(ciBean == null){
								return returnError("此货位信息不存在!");
							}
							CargoProductStockBean cpsBean = cargoService.getCargoProductStock("product_id=" + product.getId() + " and cargo_id=" + ciBean.getId());
							if(cpsBean == null){
								return returnError("商品货位库存信息不存在!");
							}
							StringBuffer set = new StringBuffer();
							StringBuffer accBuff = new StringBuffer();
							dbOp.startTransaction(); //开启事务
							if(flag == 1){
								if((cpsBean.getStockCount() + cpsBean.getStockLockCount()) == count){
									set.append("first_check_count=" + count + ",first_check_user_id=" + user.getId() + ",first_check_user_name='" 
												+ user.getUsername() + "',status=" + AbnormalCargoCheckProductBean.STATUS_CHECK_FINISHED + ",final_check_count=" + count);
									System.out.println("accpId[" +accpId+ "], 一盘:商品盘点未完成, userid["+user.getId()+"], " + "数量[" + count + "]" );
								}else{
									set.append("first_check_count=" + count + ",first_check_user_id=" + user.getId() + ",first_check_user_name='" 
												+ user.getUsername()+ "',status=" + AbnormalCargoCheckProductBean.STATUS_WAIT_SECOND_CHECK);
									System.out.println("accpId[" +accpId+ "], 一盘:商品盘点【已】完成, userid["+user.getId()+"], " + "数量[" + count + "]" );
								}
								accBuff.append("status=" +  AbnormalCargoCheckBean.STATUS1);
								System.out.println("accId[" + accpBean.getAbnormalCargoCheckId() + "] 一盘中, userId:" + user.getId());
							}else if(flag == 2){
								if(accpBean.getFirstCheckCount() == count){
									set.append("second_check_count=" + count + ",second_check_user_id=" + user.getId() + ",second_check_user_name='" 
												 + user.getUsername() + "',status=" + AbnormalCargoCheckProductBean.STATUS_CHECK_FINISHED + ",final_check_count=" + count);
									System.out.println("accpId[" +accpId+ "], 二盘:商品盘点未完成, userid["+user.getId()+"], " + "数量[" + count + "]" );
								}else{
									set.append("second_check_count=" + count + ",second_check_user_id=" + user.getId() + ",second_check_user_name='"
												 + user.getUsername()+ "',status=" + AbnormalCargoCheckProductBean.STATUS_WAIT_THRID_CHECK);
									System.out.println("accpId[" +accpId+ "], 二盘:商品盘点【已】完成, userid["+user.getId()+"], " + "数量[" + count + "]" );
								}
								accBuff.append("status=" +  AbnormalCargoCheckBean.STATUS2);
								System.out.println("accId[" + accpBean.getAbnormalCargoCheckId() + "] 二盘中, userId:" + user.getId());
							}else if(flag == 3){
								set.append("third_check_count=" + count + ",third_check_user_id=" + user.getId() + ",third_check_user_name='" 
											+ user.getUsername() + "',status=" + AbnormalCargoCheckProductBean.STATUS_CHECK_FINISHED + ",final_check_count=" + count);
								System.out.println("accpId[" +accpId+ "], 三盘:商品盘点【已】完成, userid["+user.getId()+"], " + "数量[" + count + "]" );
								accBuff.append("status=" +  AbnormalCargoCheckBean.STATUS3);
								System.out.println("accId[" + accpBean.getAbnormalCargoCheckId() + "] 三盘中, userId:" + user.getId());
							}
							if(!service.updateAbnormalCargoCheckProduct(set.toString(), "id=" + accpId)){
								dbOp.rollbackTransaction();
								return returnError("盘点计划商品更新失败!");
							}
							if(!service.updateAbnormalCargoCheck(accBuff.toString(), "id="+ accpBean.getAbnormalCargoCheckId())){
								dbOp.rollbackTransaction();
								return returnError("盘点计划单更新失败!");
							}
							//更新余下未盘点的货位 先按绑定当前物流员工的查 查不到说明领到的单子 都已经完成了 							
							Map<String,List<AbnormalCargoCheckProductBean>>  map = service.getaccpMap(accBean,dbOp,flag,user,areaId);
							for(Map.Entry<String, List<AbnormalCargoCheckProductBean>> entry : map.entrySet()){
								if(!"success".equals(entry.getKey())){
									dbOp.rollbackTransaction();
									return returnError(entry.getKey());
								}else{
									accpBeanList = entry.getValue();
								}
							}
							//如果整个计划单的货位都盘点完成那么 查找下一计划单 
							if(accpBeanList == null || accpBeanList.size() == 0){
								accpBeanList = service.getAbnormalCargoCheckProductList("abnormal_cargo_check_id=" + accpBean.getAbnormalCargoCheckId() +" and status<>" + AbnormalCargoCheckProductBean.STATUS_CHECK_FINISHED, -1, -1, null);
								if(accpBeanList == null || accpBeanList.size() == 0){
									System.out.println("accId[" + accpBean.getAbnormalCargoCheckId() + "] 盘点已完成, userId:" + user.getId());
									if(!service.updateAbnormalCargoCheck("status=" + AbnormalCargoCheckBean.STATUS4, "id="+ accpBean.getAbnormalCargoCheckId())){
										dbOp.rollbackTransaction();
										return returnError("盘点计划状态【盘点已完成】更新失败!");
									}
								}
							}
							dbOp.commitTransaction();
							return returnInventoryAbnormalCargo(accpBeanList,flag+"",status);
						}
					}else{
						return returnError("数据异常!");
					}
				}
				return returnError("数据异常!");
			}else{
				return returnError("身份验证失败!"); 
			}	
			
		}catch(Exception e){
			e.printStackTrace();
			dbOp.rollbackTransaction();
			return returnError("操作失败,系统异常！");
		}finally{
			dbOp.release();
		}
	}
	private JsonModel returnInventoryAbnormalCargo(List<AbnormalCargoCheckProductBean> accpBeanList,String flag,String status) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,Object> data = new HashMap<String, Object>();
		if(accpBeanList != null && accpBeanList.size() > 0){
			for(AbnormalCargoCheckProductBean bean : accpBeanList){
				Map<String,String> map = new HashMap<String, String>();
				map.put("accpId", bean.getId()+"");
				map.put("productCode", bean.getProductCode());
				map.put("wholeCode", bean.getCargoWholeCode());
				map.put("productName", bean.getProductName()); 
				list.add(map);
			}
			data.put("flag",flag);
			data.put("status",status);
			data.put("list",list);
		}
		return returnResult(data);
	}
	/**
	 * 说明：异常订单处理
	 * 
	 * 时间：2013-08-21
	 * 
	 * 作者：石远飞
	 */
	@RequestMapping("/orderStockAbnormalHandle")
	@ResponseBody
	public JsonModel orderStockAbnormalHandle(HttpServletRequest request){
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		DbOperation dbOpSlave = new DbOperation();
		dbOpSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		SortingAbnormalDisposeService service = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE,dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, dbOp);
		
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, dbOp);
		SortingInfoService updateSortingInfoService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,dbOp);
		JsonModel json =  new JsonModel();
		try{
			json = ReceiveJson.receiveJson(request);
			if(debugLog.isInfoEnabled()){
				debugLog.info("orderStockAbnormalHandle:"+json);
			}
			if(json==null){
				return returnError("没有收到请求数据!");
			}
			//验证用户名密码
			if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
				synchronized (cargoLock) {
					voUser user = (voUser)request.getSession().getAttribute("userView");
					UserGroupBean group = user.getGroup();
					if(!group.isFlag(618)){
						return returnError("没有此操作的权限!");
					}
					int areaId = StringUtil.toInt(json.getArea());//地区id
					String flag = (Integer)json.getData().get("flag") + ""; //标识
					String ckCode = StringUtil.convertNull((String)json.getData().get("ckCode")); //出库单号
					String status = (Integer)json.getData().get("status") + ""; //状态
					String productCode = StringUtil.convertNull((String)json.getData().get("productCode")); //商品编号
					String wholeCode = StringUtil.convertNull((String)json.getData().get("wholeCode")); //货位编号
					SortingBatchOrderBean sboBean = null;
					List<SortingBatchOrderProductBean> sbopBeanList = null;
					List<SortingAgainBean> saBeanList = null;
					SortingBatchGroupBean sbgBean = null;
					SortingBatchBean sbBean = null;
					OrderStockBean osBean = null;
					SortingAbnormalBean saBean = null;
					if("".equals(StringUtil.checkNull(flag))){
						if(ckCode != null && !"".equals(ckCode)){
							osBean = stockService.getOrderStock("code='" + ckCode + "'");
							if(osBean != null) {
								if(osBean.getStatus() != OrderStockBean.STATUS4) {
									flag = "1";//再次分拣
								}else{
									flag = "2";//撤单
								}
							}else{
								return returnError("出库单号不正确!");
							}
						}else{
							return returnError("发送数据异常!");
						}
					}
					if(flag != null && "1".equals(flag)){//flag=1，分拣异常
						if(status != null && "1".equals(status)){//status=1，开始扫描
							if(ckCode != null && !"".equals(ckCode)){
								osBean = stockService.getOrderStock("code='" + StringUtil.toSql(ckCode) + "' and stock_area=" + areaId);
								if(osBean == null){
									return returnError("请扫描选择的库地区,正确出库单编号!");
								}
								sboBean = service.getSortingBatchOrder("order_id=" + osBean.getOrderId() + " and delete_status<>1");
								if(sboBean == null){
									return returnError("找不到出库单相关联的分拣波次!");
								}
								sbgBean = service.getSortingBatchGroupBean("id=" + sboBean.getSortingGroupId());
								if(sbgBean == null){
									return returnError("查不到该出库单的二次分拣状态  无法继续进行!");
								}
								if(sbgBean.getStatus2()!=2){
									return returnError("此出库单未结批!");
								}
								sbBean = service.getSortingBatch("id=" + sboBean.getSortingBatchId());
								if(sbBean == null){
									return returnError("分拣批次订单匹配不到分拣批次!");
								}
								sbopBeanList = service.getSortingBatchOrderProducttList("sorting_batch_order_id=" + sboBean.getId() + " and is_delete=0", -1, -1, null);
								if(sbopBeanList == null || sbopBeanList.size() <= 0){
									return returnError("找不到出库单相关联分拣波次商品信息!");
								}
								ResultSet results = service.getDbOp().executeQuery("select sbg.sorting_type  from sorting_batch_group sbg where id = " + sboBean.getSortingGroupId());
								int sortingType = 0;
								if (results.next()) {
									sortingType = results.getInt(1);
								}
								results.close();
								//查此出库单异常数据
								saBeanList = service.getSortingAgainList(sbopBeanList, osBean,dbOp,areaId,sortingType);
								if(saBeanList == null || saBeanList.size() == 0){
									return returnError("此出库单没有异常数据 请扫描其它Ck号!");
								}
								if("".equals(wholeCode) || "".equals(productCode)){
									return returnSortingAgainList(saBeanList,"");
								}
							}else{
								return returnError("没有收到出库单号!");
							}
							if(!"".equals(wholeCode) && !"".equals(productCode)){
								CargoInfoBean cargoInfo = cargoService.getCargoInfo("whole_code='" + StringUtil.toSql(wholeCode) + "' and stock_type=" 
										+ CargoInfoBean.STOCKTYPE_QUALIFIED + " and area_id=" +areaId + " and (store_type=0 or store_type=4)");
								if(cargoInfo == null){
									return returnError("货位不正确或不符合要求!");
								}
								ProductBarcodeVO tempVO = bcmService.getProductBarcode("barcode='"+StringUtil.toSql(productCode)+"'");
								voProduct product = null;
								if(tempVO == null){
									product = service.getProductByCode(StringUtil.toSql(productCode));
								}else{
									product = service.getProductById(tempVO.getProductId());
								}
								if(product == null){
									return returnError("请扫描正确的商品条码或编号!");
								}
								if(!service.checkProduct(saBeanList, product.getCode())){
									return returnError("此商品不在分拣异常商品列表中!");
								}
								dbOp.startTransaction(); //开启事务
								//判断扫的是本货位还是推荐货位 
								int sortingBatchOrderProductId = service.checkWholeAndProduct(saBeanList, cargoInfo.getWholeCode(),product.getCode());
								SortingBatchOrderProductBean sbopBean = service.getSortingBatchOrderProductt("id=" + sortingBatchOrderProductId);
								if(sortingBatchOrderProductId >= 0){
									sboBean = service.getSortingBatchOrder("order_id=" + osBean.getOrderId() + " and delete_status<>1");
									sbBean = service.getSortingBatch("id=" + sboBean.getSortingBatchId());
									ResultSet results = service.getDbOp().executeQuery("select sbg.sorting_type  from sorting_batch_group sbg where id = " + sboBean.getSortingGroupId());
									int sortingType = 0;
									if (results.next()) {
										sortingType = results.getInt(1);
									}
									results.close();
									if (sortingType == 1) {
										//更新库存
										if(!service.updateSortingBatchOrderProduct("complete_count=" + (sbopBean.getCompleteCount()+1) + ",sorting_count=" + (sbopBean.getSortingCount()+1), "id=" + sortingBatchOrderProductId)){
											dbOp.rollbackTransaction();
											return returnError("更新分拣批次订单商品完成数量失败！");
										}
										CargoProductStockBean outCargoProductStock = cargoService.getCargoAndProductStock("cargo_id = "+cargoInfo.getId() +" and product_id = "+product.getId());
										if (outCargoProductStock == null) {
											dbOp.rollbackTransaction();
											return returnError("没有找到源货位！");
										}
										product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
												null));
										if ((sbopBean.getSortingCount() + 1) == sbopBean.getCount()) {
											//状态更新为正常
											if (!updateSortingInfoService.updateSortingBatchGroupExceptionInfo("is_exception=0", "sorting_batch_group_id ="+sbopBean.getSortingBatchGroupId()+" and product_id="+product.getId()+" and cargo_id="+outCargoProductStock.getCargoId())) {
												dbOp.rollbackTransaction();
												return returnError("更新分拣货位商品异常表报错！");
											}
										}
										//减少源锁定量非作业区
										if(!cargoService.updateCargoProductStockLockCount(outCargoProductStock.getId(), -1)){
											dbOp.rollbackTransaction();
											return returnError("货位库存操作失败，源货位冻结库存不足！");
										}
										CargoInfoBean inCargoInfo = cargoService.getCargoInfo("stock_type = "+ProductStockBean.STOCKTYPE_QUALIFIED+" and area_id = "+areaId+" and store_type = "+CargoInfoBean.STORE_TYPE5+" and status <> "+CargoInfoBean.STATUS3);
										if (inCargoInfo == null) {
											dbOp.rollbackTransaction();
											return returnError("没有找到目的货位！");
										}
										CargoProductStockBean inCargoProductStock = cargoService.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+product.getId());
										if (inCargoProductStock == null) {
											CargoProductStockBean cpStockBean = new CargoProductStockBean();
											cpStockBean.setCargoId(inCargoInfo.getId());
											cpStockBean.setProductId(product.getId());
											cpStockBean.setStockCount(0);
											cpStockBean.setStockLockCount(0);
											if (!cargoService.addCargoProductStock(cpStockBean)) {
												dbOp.rollbackTransaction();
												return returnError("添加目的货位库存记录失败！");
											}
											inCargoProductStock = cargoService.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+product.getId());
										}
										//增加目的锁定量作业区
										if(!cargoService.updateCargoProductStockLockCount(inCargoProductStock.getId(), 1)){
											dbOp.rollbackTransaction();
											return returnError("货位库存操作失败，目的货位冻结库存不足！");
										}
										
										//添加货位进销存卡片
										//货位出库卡片
										CargoStockCardBean outcsc = new CargoStockCardBean();
										outcsc.setCardType(CargoStockCardBean.CARDTYPE_EXECOUTPRODUCT);
										outcsc.setCode(osBean.getCode());
										outcsc.setCreateDatetime(DateUtil.getNow());
										outcsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
										outcsc.setStockArea(areaId);
										outcsc.setProductId(product.getId());
										outcsc.setStockId(outCargoProductStock.getId());
										outcsc.setStockOutCount(1);
										outcsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
										outcsc.setCurrentStock(product.getStock(outcsc.getStockArea(), outcsc.getStockType()) + product.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
										outcsc.setAllStock(product.getStockAll() + product.getLockCountAll());
										outcsc.setCurrentCargoStock(outCargoProductStock.getStockCount()+outCargoProductStock.getStockLockCount());
										outcsc.setCargoStoreType(outCargoProductStock.getCargoInfo().getStoreType());
										outcsc.setCargoWholeCode(outCargoProductStock.getCargoInfo().getWholeCode());
										outcsc.setStockPrice(product.getPrice5());
										outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
										if(!cargoService.addCargoStockCard(outcsc)){
											dbOp.rollbackTransaction();
											return returnError("货位进销存记录添加失败，请重新尝试操作！");
										}
										//货位入库卡片
										CargoStockCardBean incsc = new CargoStockCardBean();
										incsc.setCardType(CargoStockCardBean.CARDTYPE_EXECINPRODUCT);
										incsc.setCode(osBean.getCode());
										incsc.setCreateDatetime(DateUtil.getNow());
										incsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
										incsc.setStockArea(areaId);
										incsc.setProductId(product.getId());
										incsc.setStockId(inCargoProductStock.getId());
										incsc.setStockInCount(1);
										incsc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
										incsc.setCurrentStock(product.getStock(incsc.getStockArea(), incsc.getStockType()) + product.getLockCount(incsc.getStockArea(), incsc.getStockType()));
										incsc.setAllStock(product.getStockAll() + product.getLockCountAll());
										incsc.setCurrentCargoStock(inCargoProductStock.getStockCount()+inCargoProductStock.getStockLockCount());
										incsc.setCargoStoreType(inCargoProductStock.getCargoInfo().getStoreType());
										incsc.setCargoWholeCode(inCargoProductStock.getCargoInfo().getWholeCode());
										incsc.setStockPrice(product.getPrice5());
										incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
										if(!cargoService.addCargoStockCard(incsc)){
											dbOp.rollbackTransaction();
											return returnError("货位进销存记录添加失败，请重新尝试操作！");
										}
									} else {
										//更新库存
										if(!service.updateSortingBatchOrderProduct("complete_count=" + (sbopBean.getCompleteCount()+1) , "id=" + sortingBatchOrderProductId)){
											dbOp.rollbackTransaction();
											return returnError("更新分拣批次订单商品完成数量失败!");
										}
									}
									sboBean = service.getSortingBatchOrder("order_id=" + osBean.getOrderId() + " and delete_status<>1");
									sbopBeanList = service.getSortingBatchOrderProducttList("sorting_batch_order_id=" + sboBean.getId() + " and is_delete=0", -1, -1, null);
									saBeanList = service.getSortingAgainList(sbopBeanList, osBean,dbOp,areaId,sortingType);
									dbOp.commitTransaction();//提交事务
									//判断是不是这个出库单下的所有任务都完成
									if(saBeanList == null || saBeanList.size() == 0){
										return returnSortingAgainList(null,"所有订单都已处理完毕!请扫描下一出库单!");
									}
									return returnSortingAgainList(saBeanList,"");
								}else{
									CargoProductStockBean cpsBean = cargoService.getCargoProductStock("cargo_id=" + cargoInfo.getId() + " and product_id=" + product.getId());
									if(cpsBean == null){
										return returnError("商品[" + product.getCode()+ "]不在货位[" + cargoInfo.getWholeCode() +"]");
									}
									String inCargoWholeCode = service.getInCargoWholeCode(saBeanList, product.getCode());
									//生成调拨单
									String result = service.generateDeploy(dbOp, areaId, user, inCargoWholeCode, cargoInfo.getWholeCode(), product.getId());
									if(!"success".equals(result)){
										return returnError(result);//子方法中已经回滚
									}
									//生成异常单
									String sortingAbnormalCode = service.getNewSortingAbnormalCode(osBean.getStockArea(),dbOp);//生成货位异常单编号
									if("FAIL".equals(sortingAbnormalCode)){
										dbOp.rollbackTransaction();
										return returnError("生成分拣异常单号失败!");
									}
									SortingAbnormalBean sortingAbnormalBean = new SortingAbnormalBean();
									sortingAbnormalBean.setCode(sortingAbnormalCode);
									sortingAbnormalBean.setOperCode(osBean.getCode());
									sortingAbnormalBean.setOperType(SortingAbnormalBean.OPERTYPE0);
									sortingAbnormalBean.setAbnormalType(SortingAbnormalBean.ABNORMALTYPE1);
									sortingAbnormalBean.setStatus(SortingAbnormalBean.STATUS3);
									sortingAbnormalBean.setCreateDatetime(DateUtil.getNow());
									sortingAbnormalBean.setCreateUserId(user.getId());
									sortingAbnormalBean.setCreateUserName(user.getUsername());
									sortingAbnormalBean.setWareArea(areaId);
									if(!service.addSortingAbnormal(sortingAbnormalBean)){
										dbOp.rollbackTransaction();
										return returnError("生成异分拣常单失败!");
									}
									//获取刚刚插入的异常单ID
									int sortingAbnormalId = dbOp.getLastInsertId();
									//SortingAgainBean saBean = service.getSortingAgainBean(saBeanList, product.getCode());
									SortingAbnormalProductBean sapBean = new SortingAbnormalProductBean();
									sapBean.setCargoWholeCode(inCargoWholeCode);
									sapBean.setCount(1);
									sapBean.setLockCount(1);
									sapBean.setSortingAbnormalId(sortingAbnormalId);
									sapBean.setProductCode(product.getCode());
									sapBean.setProductId(product.getId());
									sapBean.setStatus(SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK);
									sapBean.setLastOperDatetime(DateUtil.getNow());
									if(!service.addSortingAbnormalProduct(sapBean)){
										dbOp.rollbackTransaction();
										return returnError( "生成分拣异常单商品失败!");
									}
									sboBean = service.getSortingBatchOrder("order_id=" + osBean.getOrderId() + " and delete_status<>1");
									sbBean = service.getSortingBatch("id=" + sboBean.getSortingBatchId());
										//获取本货位code
										wholeCode = service.getInCargoWholeCode(saBeanList, product.getCode());
										//根据商品code和货位code获取异常商品ID
										sortingBatchOrderProductId = service.checkWholeAndProduct(saBeanList, wholeCode, product.getCode());
										sbopBean = service.getSortingBatchOrderProductt("id=" + sortingBatchOrderProductId);
										ResultSet results = service.getDbOp().executeQuery("select sbg.sorting_type  from sorting_batch_group sbg where id = " + sboBean.getSortingGroupId());
										int sortingType = 0;
										if (results.next()) {
											sortingType = results.getInt(1);
										}
										results.close();
										if (sortingType == 1) {
											if(!service.updateSortingBatchOrderProduct("complete_count=" + (sbopBean.getCompleteCount()+1)+", sorting_count="+ (sbopBean.getSortingCount()+1), "id=" + sortingBatchOrderProductId)){
												dbOp.rollbackTransaction();
												return returnError("更新分拣批次订单商品完成数量失败！");
											}
											CargoInfoBean outCargoInfo = cargoService.getCargoInfo("whole_code='"+inCargoWholeCode+"' and stock_type = "+ProductStockBean.STOCKTYPE_QUALIFIED+" and area_id = "+areaId+" and store_type <> "+CargoInfoBean.STORE_TYPE5+" and status <> "+CargoInfoBean.STATUS3);
											if (outCargoInfo == null) {
												dbOp.rollbackTransaction();
												return returnError("没有找到源货位！");
											}
											if ((sbopBean.getSortingCount() + 1) == sbopBean.getCount()) {
												//状态更新为正常
												if (!updateSortingInfoService.updateSortingBatchGroupExceptionInfo("is_exception=0", "sorting_batch_group_id ="+sbopBean.getSortingBatchGroupId()+" and product_id="+product.getId()+" and cargo_id="+outCargoInfo.getId())) {
													dbOp.rollbackTransaction();
													return returnError("更新分拣货位商品异常表报错！");
												}
											}
											product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
													null));
											CargoProductStockBean outCargoProductStock = cargoService.getCargoAndProductStock("cargo_id = "+outCargoInfo.getId() +" and product_id = "+product.getId());
											if (outCargoProductStock == null) {
												dbOp.rollbackTransaction();
												return returnError("没有源货位库存记录！");
											}
											//减少源锁定量非作业区
											if(!cargoService.updateCargoProductStockLockCount(outCargoProductStock.getId(), -1)){
												dbOp.rollbackTransaction();
												return returnError("货位库存操作失败，源货位冻结库存不足！");
											}
											CargoInfoBean inCargoInfo = cargoService.getCargoInfo("stock_type = "+ProductStockBean.STOCKTYPE_QUALIFIED+" and area_id = "+areaId+" and store_type = "+CargoInfoBean.STORE_TYPE5+" and status <> "+CargoInfoBean.STATUS3);
											if (inCargoInfo == null) {
												dbOp.rollbackTransaction();
												return returnError("没有找到目的货位！");
											}
											CargoProductStockBean inCargoProductStock = cargoService.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+product.getId());
											if (inCargoProductStock == null) {
												CargoProductStockBean cpStockBean = new CargoProductStockBean();
												cpStockBean.setCargoId(inCargoInfo.getId());
												cpStockBean.setProductId(product.getId());
												cpStockBean.setStockCount(0);
												cpStockBean.setStockLockCount(0);
												if (!cargoService.addCargoProductStock(cpStockBean)) {
													dbOp.rollbackTransaction();
													return returnError("添加目的货位库存记录失败！");
												}
												inCargoProductStock = cargoService.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+product.getId());
											}
											//增加目的锁定量作业区
											if(!cargoService.updateCargoProductStockLockCount(inCargoProductStock.getId(), 1)){
												dbOp.rollbackTransaction();
												return returnError("货位库存操作失败，目的货位冻结库存不足！");
											}
											
											//添加货位进销存卡片
											//货位出库卡片
											CargoStockCardBean outcsc = new CargoStockCardBean();
											outcsc.setCardType(CargoStockCardBean.CARDTYPE_EXECOUTPRODUCT);
											outcsc.setCode(osBean.getCode());
											outcsc.setCreateDatetime(DateUtil.getNow());
											outcsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
											outcsc.setStockArea(areaId);
											outcsc.setProductId(product.getId());
											outcsc.setStockId(outCargoProductStock.getId());
											outcsc.setStockOutCount(1);
											outcsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
											outcsc.setCurrentStock(product.getStock(outcsc.getStockArea(), outcsc.getStockType()) + product.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
											outcsc.setAllStock(product.getStockAll() + product.getLockCountAll());
											outcsc.setCurrentCargoStock(outCargoProductStock.getStockCount()+outCargoProductStock.getStockLockCount());
											outcsc.setCargoStoreType(outCargoProductStock.getCargoInfo().getStoreType());
											outcsc.setCargoWholeCode(outCargoProductStock.getCargoInfo().getWholeCode());
											outcsc.setStockPrice(product.getPrice5());
											outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
											if(!cargoService.addCargoStockCard(outcsc)){
												dbOp.rollbackTransaction();
												return returnError("货位进销存记录添加失败，请重新尝试操作！");
											}
											//货位入库卡片
											CargoStockCardBean incsc = new CargoStockCardBean();
											incsc.setCardType(CargoStockCardBean.CARDTYPE_EXECINPRODUCT);
											incsc.setCode(osBean.getCode());
											incsc.setCreateDatetime(DateUtil.getNow());
											incsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
											incsc.setStockArea(areaId);
											incsc.setProductId(product.getId());
											incsc.setStockId(inCargoProductStock.getId());
											incsc.setStockInCount(1);
											incsc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
											incsc.setCurrentStock(product.getStock(incsc.getStockArea(), incsc.getStockType()) + product.getLockCount(incsc.getStockArea(), incsc.getStockType()));
											incsc.setAllStock(product.getStockAll() + product.getLockCountAll());
											incsc.setCurrentCargoStock(inCargoProductStock.getStockCount()+inCargoProductStock.getStockLockCount());
											incsc.setCargoStoreType(inCargoProductStock.getCargoInfo().getStoreType());
											incsc.setCargoWholeCode(inCargoProductStock.getCargoInfo().getWholeCode());
											incsc.setStockPrice(product.getPrice5());
											incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
											if(!cargoService.addCargoStockCard(incsc)){
												dbOp.rollbackTransaction();
												return returnError("货位进销存记录添加失败，请重新尝试操作！");
											}
										} else {
											if(!service.updateSortingBatchOrderProduct("complete_count=" + (sbopBean.getCompleteCount()+1) , "id=" + sortingBatchOrderProductId)){
												dbOp.rollbackTransaction();
												return returnError("更新分拣批次订单商品完成数量失败!");
											}
										}
										sbopBeanList = service.getSortingBatchOrderProducttList("sorting_batch_order_id=" + sboBean.getId() + " and is_delete=0", -1, -1, null);
										saBeanList = service.getSortingAgainList(sbopBeanList,osBean,dbOp,areaId,sortingType);
									dbOp.commitTransaction();//提交事务
									if(saBeanList == null || saBeanList.size() == 0){
										return returnSortingAgainList(null,"所有订单都已处理完毕!请扫描下一出库单!");
									}
									return returnSortingAgainList(saBeanList,"");
								}
							}
						}else if(status != null && "2".equals(status)){//status=2，结束扫描
							if(ckCode != null && !"".equals(ckCode)){
								osBean = stockService.getOrderStock("code='" + StringUtil.toSql(ckCode) + "' and stock_area=" + areaId);
								if(osBean == null){
									return returnError("请扫描选择的库地区,正确出库单编号!");
								}
								
								if (!insertBISortingAbnormalOrder(osBean.getStockArea(), osBean.getCode(), dbOp)) {
									return returnError("保存BISortingAbnormalOrder失败!"); 
								}
								
								sboBean = service.getSortingBatchOrder("order_id=" + osBean.getOrderId() + " and delete_status<>1");
								if(sboBean == null){
									return returnError("找不到出库单相关联的分拣波次!");
								}
								sbBean = service.getSortingBatch("id=" + sboBean.getSortingBatchId());
								if(sbBean == null){
									return returnError("分拣批次订单匹配不到分拣批次!");
								}
								sbopBeanList = service.getSortingBatchOrderProducttList("sorting_batch_order_id=" + sboBean.getId() + " and is_delete=0", -1, -1, null);
								ResultSet results = service.getDbOp().executeQuery("select sbg.sorting_type  from sorting_batch_group sbg where id = " + sboBean.getSortingGroupId());
								int sortingType = 0;
								if (results.next()) {
									sortingType = results.getInt(1);
								}
								results.close();
								//查此出库单异常数据
								saBeanList = service.getSortingAgainList(sbopBeanList, osBean,dbOp,areaId,sortingType);
								//如果还有没有完成的异常处理则提示
								if(saBeanList != null && saBeanList.size() > 0){
									return returnSortingAgainList(null,"库存异常订单请线下处理！");
								}
								return returnSortingAgainList(null,"所有订单都已处理完毕!请扫描下一出库单!");
							}
							return returnError("没有收到要关闭的出库单号!");
						}
					}else if(flag != null && "2".equals(flag)){//flag=2，撤单
						if(status != null && "1".equals(status)){//status=1，开始扫描
							Object obj = null;
							if(ckCode != null && !"".equals(ckCode)){
								// 作为新开始 信号查询并初始化SortingAbnormal信息放入session
								osBean = stockService.getOrderStock("code='" + ckCode + "'");
								if(osBean != null) {
									if( osBean.getStockArea() != areaId ) {
										return returnError("出库单信息与操作地区不匹配!");
									}
								} else {
									return returnError("没有找到对应的出库单信息!");
								}
								obj = sortingAbnormalDisposeService.getSortingAbnormalAllInfo(ckCode);
								if( obj instanceof String ) {
									return returnError((String)obj);
								} else if( obj instanceof SortingAbnormalBean ) {
									saBean = (SortingAbnormalBean) obj;
									if( saBean.getStatus() != SortingAbnormalBean.STATUS0 && saBean.getStatus() != SortingAbnormalBean.STATUS1 ) {
										return returnError("异常单不是未处理，或处理中状态，不可以再操作!");
									}
									ResultSet results = dbOpSlave.executeQuery("select sbg.sorting_type  from sorting_batch_group sbg,sorting_batch_order sbo where sbg.id=sbo.sorting_group_id and sbo.order_stock_id="+osBean.getId());
									int sortingType = 0;
									if (results.next()) {
										sortingType = results.getInt(1);
									}
									results.close();
									if (sortingType == 1) {
										List<SortingAbnormalProductBean> list = saBean.getSortingAbnormalProductList();
										if (list != null && list.size() != 0) {
											int x = list.size();
											for (int i = 0; i < x; i ++) {
												SortingAbnormalProductBean sapBean = list.get(i);
												ResultSet rs = sortingAbnormalDisposeService.getDbOp().executeQuery("select ospc.cargo_whole_code  from order_stock_product_cargo ospc,order_stock_product osp, order_stock os , sorting_abnormal sa where sa.oper_code = os.code and osp.order_stock_id = os.id and ospc.order_stock_product_id=osp.id and sa.oper_code='"+saBean.getOperCode()+"' and osp.product_id="+sapBean.getProductId());
												if (rs.next()) {
													sapBean.setCargoWholeCode(rs.getString(1));
												}
												rs.close();
											}
										} 
									}
									boolean hasStatusChecking = sortingAbnormalDisposeService.hasStatusChecking(saBean.getSortingAbnormalProductList());
									if( hasStatusChecking ) {
										return returnError("存在异常商品已经是盘点中，或者盘点完成了，不能再操作这个异常单了，请结束!");
									}
									if( !sortingAbnormalDisposeService.updateSortingAbnormal("status=" + SortingAbnormalBean.STATUS1, "id=" + saBean.getId()) ) {
										return returnError("数据库操作失败!");
									}
									if("".equals(wholeCode) || "".equals(productCode)){
										List<SortingAbnormalProductBean> list = saBean.getSortingAbnormalProductList();
										if (list != null && list.size() > 0) {
											return returnorderStockDealForDelete(list,"");
										} else {
											return returnError("没有要撤单的数据!");
										}
									}
								}
							}else{
								return returnError("没有收到出库单号!");
							}
							if(!"".equals(wholeCode) && !"".equals(productCode)){
								boolean hasStatusChecking = sortingAbnormalDisposeService.hasStatusChecking(saBean.getSortingAbnormalProductList());
								if( hasStatusChecking ) {
									return returnError("存在异常商品已经是盘点中，或者盘点完成了，不能再操作这个异常单了，请结束!");
								}
								String cargoWholeCode = wholeCode;
								//根据货位号  找到所有的 符合商品货位信息 放倒session里
								List<SortingAbnormalProductBean> list = sortingAbnormalDisposeService.getIsCargoInList(saBean.getSortingAbnormalProductList(), cargoWholeCode);
								if( list.size() == 0 ) {
									return returnError("货位不正确!");
								}
								//按照产品编号 或者条码 
								voProduct product = null;
								ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(productCode)+"'");
								if( bBean == null || bBean.getBarcode() == null ) {
									product = wareService.getProduct(StringUtil.toSql(productCode));
								} else {
									product = wareService.getProduct(bBean.getProductId());
								}
								if( product == null ) {
									return returnError("没有找到商品信息!");
								}
								//整个异常单中根本就没有这个商品的  属于特殊异常了
								SortingAbnormalProductBean sapBean1 = sortingAbnormalDisposeService.getIsProductOnCargo(saBean.getSortingAbnormalProductList(), product.getId());
								if( sapBean1 == null ) {
									return returnError("请到‘分拣异常商品’模块处理该SKU!");
								}
								
								//找是否有 对应货位  且对应 商品信息的  条目
								SortingAbnormalProductBean sapBean = sortingAbnormalDisposeService.getIsProductOnCargo(list, product.getId());
								if( sapBean == null ) {
									return returnError("SKU不正确!");
								}
								if( sapBean.getLockCount() > 0 ) {
									// 真正实际操作库存的地方了 找到对应的库存记录，货位库存记录  记得操作完成要替换session中的 信息 让session中 和数据库中保持一致
									stockService.getDbOp().startTransaction();
									ProductStockBean psBean = null;
									CargoProductStockBean cpsBean = null;
									//寻找库存信息
									psBean = psService.getProductStock("product_id=" + sapBean.getProductId() + " and type=" + osBean.getStockType() + " and area=" + osBean.getStockArea());
									if( psBean == null ) {
										stockService.getDbOp().rollbackTransaction();
										return returnError("没有找到要操作的库存信息!");
									}
									ResultSet results = dbOpSlave.executeQuery("select sbg.sorting_type  from sorting_batch_group sbg,sorting_batch_order sbo where sbg.id=sbo.sorting_group_id and sbo.order_stock_id="+osBean.getId());
									int sortingType = 0;
									if (results.next()) {
										sortingType = results.getInt(1);
									}
									results.close();
									if (sortingType == 1) {
										product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1,
												null));
										CargoInfoBean ciBean = cargoService.getCargoInfo("stock_type = "+ProductStockBean.STOCKTYPE_QUALIFIED+" and area_id = "+osBean.getStockArea()+" and store_type = "+CargoInfoBean.STORE_TYPE5+" and status <> "+CargoInfoBean.STATUS3);
										//寻找货位库存信息
										if( ciBean == null ) {
											dbOp.rollbackTransaction();
											return returnError("没有找到要操作的货位信息！");
										} else {
											cpsBean = cargoService.getCargoAndProductStock("cargo_id=" + ciBean.getId() + " and product_id =" + sapBean.getProductId());
											if( cpsBean == null ) {
												dbOp.rollbackTransaction();
												return returnError("没有找到要操作的货位库存信息！");
											}
										}
										
										//减少源锁定量
										if(!cargoService.updateCargoProductStockLockCount(cpsBean.getId(), -1)){
											dbOp.rollbackTransaction();  
											return returnError("货位库存操作失败，源货位冻结库存不足！");
										}
										CargoInfoBean inCargoInfo = cargoService.getCargoInfo("whole_code = '" + sapBean.getCargoWholeCode()+"'");
										if (inCargoInfo == null) {
											dbOp.rollbackTransaction();
											return returnError("没有找到目的货位！");
										}
										CargoProductStockBean inCargoProductStock = cargoService.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+sapBean.getProductId());
										if (inCargoProductStock == null) {
											CargoProductStockBean cpStockBean = new CargoProductStockBean();
											cpStockBean.setCargoId(inCargoInfo.getId());
											cpStockBean.setProductId(product.getId());
											cpStockBean.setStockCount(0);
											cpStockBean.setStockLockCount(0);
											if (!cargoService.addCargoProductStock(cpStockBean)) {
												dbOp.rollbackTransaction();
												return returnError("添加目的货位库存记录失败！");
											}
											inCargoProductStock = cargoService.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+sapBean.getProductId());
										}
										//增加目的锁定量
										if(!cargoService.updateCargoProductStockLockCount(inCargoProductStock.getId(), 1)){
											dbOp.rollbackTransaction();
											return returnError("货位库存操作失败，目的货位冻结库存不足！");
										}
										
										//添加货位进销存卡片
										//货位出库卡片
										CargoStockCardBean outcsc = new CargoStockCardBean();
										outcsc.setCardType(CargoStockCardBean.CARDTYPE_EXECOUTPRODUCT);
										outcsc.setCode(osBean.getCode());
										outcsc.setCreateDatetime(DateUtil.getNow());
										outcsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
										outcsc.setStockArea(osBean.getStockArea());
										outcsc.setProductId(product.getId());
										outcsc.setStockId(cpsBean.getId());
										outcsc.setStockOutCount(1);
										outcsc.setStockOutPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
										outcsc.setCurrentStock(product.getStock(outcsc.getStockArea(), outcsc.getStockType()) + product.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
										outcsc.setAllStock(product.getStockAll() + product.getLockCountAll());
										outcsc.setCurrentCargoStock(cpsBean.getStockCount()+cpsBean.getStockLockCount());
										outcsc.setCargoStoreType(cpsBean.getCargoInfo().getStoreType());
										outcsc.setCargoWholeCode(cpsBean.getCargoInfo().getWholeCode());
										outcsc.setStockPrice(product.getPrice5());
										outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
										if(!cargoService.addCargoStockCard(outcsc)){
											dbOp.rollbackTransaction();
											return returnError("货位进销存记录添加失败，请重新尝试操作！");
										}
										//货位入库卡片
										CargoStockCardBean incsc = new CargoStockCardBean();
										incsc.setCardType(CargoStockCardBean.CARDTYPE_EXECINPRODUCT);
										incsc.setCode(osBean.getCode());
										incsc.setCreateDatetime(DateUtil.getNow());
										incsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
										incsc.setStockArea(osBean.getStockArea());
										incsc.setProductId(product.getId());
										incsc.setStockId(inCargoProductStock.getId());
										incsc.setStockInCount(1);
										incsc.setStockInPriceSum((new BigDecimal(1)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
										incsc.setCurrentStock(product.getStock(incsc.getStockArea(), incsc.getStockType()) + product.getLockCount(incsc.getStockArea(), incsc.getStockType()));
										incsc.setAllStock(product.getStockAll() + product.getLockCountAll());
										incsc.setCurrentCargoStock(inCargoProductStock.getStockCount()+inCargoProductStock.getStockLockCount());
										incsc.setCargoStoreType(inCargoProductStock.getCargoInfo().getStoreType());
										incsc.setCargoWholeCode(inCargoProductStock.getCargoInfo().getWholeCode());
										incsc.setStockPrice(product.getPrice5());
										incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
										if(!cargoService.addCargoStockCard(incsc)){
											dbOp.rollbackTransaction();
											return returnError("货位进销存记录添加失败，请重新尝试操作！");
										}
										//更改库存
										boolean rslt = sortingAbnormalDisposeService.rollStockBackForOne(psBean, inCargoProductStock);
										if( !rslt ) {
											dbOp.rollbackTransaction();
											return returnError("库存操作失败，可能是库存冻结量不足!");
										}
									} else {
										//寻找货位库存信息
										CargoInfoBean ciBean = cargoService.getCargoInfo("whole_code='" + sapBean.getCargoWholeCode() + "'");
										if( ciBean == null ) {
											stockService.getDbOp().rollbackTransaction();
											return returnError("没有找到要操作的货位信息!");
										}
										cpsBean = cargoService.getCargoProductStock("cargo_id=" + ciBean.getId() + " and product_id =" + sapBean.getProductId());
										if( cpsBean == null ) {
											stockService.getDbOp().rollbackTransaction();
											return returnError("没有找到要操作的货位库存信息!");
										}
										//更改库存
										boolean rslt = sortingAbnormalDisposeService.rollStockBackForOne(psBean, cpsBean);
										if( !rslt ) {
											stockService.getDbOp().rollbackTransaction();
											return returnError("库存操作失败，可能是库存冻结量不足!");
										}
									}
									//更改异常商品状态
									boolean updateResult = sortingAbnormalDisposeService.updateSortingAbnormalStatus(sapBean);
									if( !updateResult ) {
										stockService.getDbOp().rollbackTransaction();
										return returnError("修改异常单时， 数据库操作失败!");
									}
									stockService.getDbOp().commitTransaction();
									obj = sortingAbnormalDisposeService.getSortingAbnormalAllInfo(osBean.getCode());
									if( obj instanceof String ) {
										return returnError((String)obj);
									} else if( obj instanceof SortingAbnormalBean ) {
										saBean = (SortingAbnormalBean)obj;
									}
									list = saBean.getSortingAbnormalProductList();
									if (sortingType == 1) {
										if (list != null && list.size() != 0) {
											int x = list.size();
											for (int i = 0; i < x; i ++) {
												SortingAbnormalProductBean saProductBean = list.get(i);
												ResultSet rs = sortingAbnormalDisposeService.getDbOp().executeQuery("select ospc.cargo_whole_code  from order_stock_product_cargo ospc,order_stock_product osp, order_stock os , sorting_abnormal sa where sa.oper_code = os.code and osp.order_stock_id = os.id and ospc.order_stock_product_id=osp.id and sa.oper_code='"+saBean.getOperCode()+"' and osp.product_id="+saProductBean.getProductId());
												if (rs.next()) {
													saProductBean.setCargoWholeCode(rs.getString(1));
												}
												rs.close();
											}
											return returnorderStockDealForDelete(list,"");
										} else {
											return returnError("没有要撤单的数据拉!");
										}
									} else {
										if (list != null && list.size() != 0) {
											return returnorderStockDealForDelete(list,"");
										} else {
											return returnError("没有要撤单的数据拉!");
										}
									}
								} else {
									return returnError("请到‘分拣异常商品’模块处理该SKU!");
								}
							}
						}else if(status != null && "2".equals(status)){//status=2，结束
							if(ckCode != null && !"".equals(ckCode)){
								Object result = sortingAbnormalDisposeService.getSortingAbnormalAllInfo(ckCode);
								if( result instanceof String ) {
									return returnorderStockDealForDelete(null,(String)result);
								} else if( result instanceof SortingAbnormalBean ) {
									saBean = (SortingAbnormalBean)result;
									sortingAbnormalDisposeService.getDbOp().startTransaction();
									
									if (!insertBISortingAbnormalOrder(saBean.getWareArea(), saBean.getOperCode(), sortingAbnormalDisposeService.getDbOp())) {
										sortingAbnormalDisposeService.getDbOp().rollbackTransaction();
										return returnError("保存BISortingAbnormalOrder失败!"); 
									}
									
									//首先验证这个异常单到底是什么 ，异常情况，
									//对应的修改每个商品记录的状态，，， 
									int abnormalCount = sortingAbnormalDisposeService.getSortingAbnormalProductCount("sorting_abnormal_id=" + saBean.getId() + " and status !=" + SortingAbnormalProductBean.STATUS_NORMAL);
									if(abnormalCount > 0 ) {
										if( !sortingAbnormalDisposeService.updateSortingAbnormal("status = " + SortingAbnormalBean.STATUS3, "id=" + saBean.getId()) ) {
											stockService.getDbOp().rollbackTransaction();
											return returnError("数据库操作失败!");
										}
										if( !sortingAbnormalDisposeService.updateSortingAbnormalProduct("status = " + SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK, "sorting_abnormal_id=" + saBean.getId() + " and status !=" + SortingAbnormalProductBean.STATUS_NORMAL ) ) {
											stockService.getDbOp().rollbackTransaction();
											return returnError("数据库操作失败!");
										}
									} else {
										if( !sortingAbnormalDisposeService.updateSortingAbnormal("status = " + SortingAbnormalBean.STATUS2, "id=" + saBean.getId()) ) {
											stockService.getDbOp().rollbackTransaction();
											return returnError("数据库操作失败!");
										}
									}
									sortingAbnormalDisposeService.getDbOp().commitTransaction();
									return returnorderStockDealForDelete(null,"结束撤单操作成功");
								}
							}
							return returnError("没有收到要关闭的出库单号!");
						}
					}
					return returnError("发送数据异常!");
				}
			}else{
				return returnError("身份验证失败!"); 
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return returnError("操作失败,系统异常！");
		}finally{
			dbOp.release();
			dbOpSlave.release();
		}
	}
	private JsonModel returnorderStockDealForDelete(List<SortingAbnormalProductBean> saplist, String result) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,Object> data = new HashMap<String, Object>();
		if(saplist != null && saplist.size() > 0){
			for(SortingAbnormalProductBean bean : saplist){
				Map<String,String> map = new HashMap<String, String>();
				map.put("wholeCodeAll", bean.getCargoWholeCode());
				map.put("wholeCode", bean.getCargoCodePartly());
				map.put("productCode", bean.getProductCode());
				map.put("count", bean.getLockCount()+"");
				map.put("total", bean.getCount()+"");
				map.put("productName", bean.getProduct().getName());
				list.add(map); 
			}
			data.put("flag","2");
			data.put("status", "1");
			data.put("list", list);
		}else{
			data.put("result", result);
			data.put("flag","2");
			data.put("status", "2");
		}
		return returnResult(data);
	}
	private JsonModel returnSortingAgainList(List<SortingAgainBean> saBeanList, String result) {
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,Object> data = new HashMap<String, Object>();
		if(saBeanList != null && saBeanList.size() > 0){
			for(SortingAgainBean bean : saBeanList){
				Map<String,String> map = new HashMap<String, String>();
				StringBuilder sb = new StringBuilder();
				String newWholeCode = bean.getWholeCode().substring(bean.getWholeCode().indexOf("-")+1);
				if(newWholeCode.length()==6){
					sb.append(newWholeCode.substring(0,3));
					sb.append("-");
					sb.append(newWholeCode.substring(3,4));
					sb.append("-");
					sb.append(newWholeCode.substring(4,6));
				}else if(newWholeCode.length()==8){
					sb.append(newWholeCode.substring(0,3));
					sb.append("-");
					sb.append(newWholeCode.substring(3,5));
					sb.append("-");
					sb.append(newWholeCode.substring(5,6));
					sb.append("-");
					sb.append(newWholeCode.substring(6,8));
				}
				map.put("wholeCodeAll", bean.getWholeCode());
				map.put("wholeCode", sb.toString());
				map.put("productCode", bean.getProudctCode());
				map.put("count", bean.getCount()+"");
				map.put("total", bean.getTotal()+"");
				map.put("productName", bean.getProudctName());
				map.put("recommendWhole", bean.getRecommendWhole());
				list.add(map);
			}
			data.put("flag","1");
			data.put("status", "1");
			data.put("list", list);
		}else{
			data.put("result", result);
			data.put("flag","1");
			data.put("status", "2");
		}
		return returnResult(data);
	}
	/**
	 * 说明：订单出库
	 * 
	 * 时间：2013-08-19
	 * 
	 * 作者：石远飞
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/orderOutWare")
	@ResponseBody
	public JsonModel orderOutWare(HttpServletRequest request){
		DbOperation dbOp = new DbOperation();
		DbOperation dbOpSlave = new DbOperation();
		dbOp.init(DbOperation.DB);
		dbOpSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		IStockService stockSlaveService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOpSlave);
		JsonModel json =  new JsonModel();
		try{
			json = ReceiveJson.receiveJson(request);
			if(json==null){
				return returnError("没有收到请求数据!");
			}
			//验证用户名密码
			if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
				int area = StringUtil.toInt(json.getArea());//地区id
				int flag = (Integer)json.getData().get("flag"); //标识
				
				String batchCode = StringUtil.convertNull((String)json.getData().get("batchCode")); //发货波次编号
				String parcelCode = StringUtil.convertNull((String)json.getData().get("parcelCode"));//发货邮包code
				String packageCode = StringUtil.toSql(StringUtil.convertNull((String)json.getData().get("packageCode")));//包裹单编号
				
				Map<String,Object> data = new HashMap<String, Object>();
				MailingBatchBean mbBean = null;
				AuditPackageBean apBean = null;
				MailingBatchParcelBean mbpBean = null;
				if(!"".equals(packageCode)){
					apBean = stockService.getAuditPackage("package_code='" + packageCode+"'");
					if(apBean==null){
						return returnError("包裹信息错误!");
					}
				}
				if(!"".equals(parcelCode)){
					mbpBean=stockService.getMailingBatchParcel("code='" + parcelCode + "'");
					if(mbpBean==null){
						return returnError("该发货邮包不存在!");
					}
					mbBean = stockService.getMailingBatch("id="+mbpBean.getMailingBatchId());
					batchCode = mbBean.getCode();
				}
				if(!"".equals(batchCode)){
					mbBean = stockSlaveService.getMailingBatch(" code='" + StringUtil.toSql(batchCode) + "'");
					if(mbBean == null){
						return returnError("未找到此发货波次!");
					}
					if(area != mbBean.getArea()){
						return returnError("发货波次与选择的仓库不一致!");
					}
					if(mbBean.getStatus()==1){
						return returnError("发货波次已出库!");
					}
					if(mbBean.getStatus()==2){
						return returnError("发货波次已完成交接!");
					}
				}
				if(flag == 1){//第一次 扫描波次号
					
					List<String[]> list = new ArrayList<String[]>();
					ArrayList MBPlist = stockSlaveService.getMailingBatchParcelList(" mailing_batch_code='" + batchCode +"'", -1, -1,"id DESC");
					for(int i=0;i<MBPlist.size();i++){
						MailingBatchParcelBean pBean=(MailingBatchParcelBean)MBPlist.get(i);
						String[] s = new String[2];
						s[0] = pBean.getCode();
						s[1] = stockSlaveService.getMailingBatchPackageCount("mailing_batch_parcel_id="+pBean.getId()) + "";
						list.add(s);
					}
					data.put("batchCode", batchCode);
					data.put("list", list);
					
					return returnResult(data);
				}else if(flag ==21){//第二次创建邮包
					MailingBatchParcelBean parcel = new MailingBatchParcelBean();
					parcel.setMailingBatchId(mbBean.getId());
					parcel.setMailingBatchCode(mbBean.getCode());
					
					List parcelList=stockSlaveService.getMailingBatchParcelList("mailing_batch_code='"+ StringUtil.toSql(batchCode) + "'", -1, -1, "id desc");
					if(parcelList.size()>0){
						MailingBatchParcelBean parcelBean=(MailingBatchParcelBean)parcelList.get(0);
						if(parcelBean.getCode().substring(14,16).equals("99")){
							return returnError("邮包最多添加99个!");
						}
					}
					if(parcelList.size()==0){
						parcel.setCode(mbBean.getCode()+"01");
					}else{
						MailingBatchParcelBean firstParcel=(MailingBatchParcelBean)parcelList.get(0);
						String code = firstParcel.getCode();
						int parcelNum=Integer.parseInt(code.substring(code.length()-2,code.length()))+1;
						parcel.setCode(mbBean.getCode()+(parcelNum<10?"0":"")+parcelNum);
					}
					if(!stockService.addMailingBatchParcel(parcel)){
						return returnError("创建邮包失败!");
					}
					data.put("parcelCode", parcel.getCode());
					return returnResult(data);
				}else if(flag == 31){//第三次添加包裹
					if(apBean == null || mbpBean == null){
						return returnError("包裹单号不能为空!");
					}
					voOrder order=wareService.getOrder("code='" + apBean.getOrderCode() +"'");
					if(order==null){
						return returnError("无此订单!");
					}
					
					if(apBean.getAreano() != area){
						return returnError("包裹与选择的仓库不一致!");
					}
					
					if(order.getDeliver() != mbBean.getDeliver()){
						return returnError("该订单快递公司与波次物流不匹配!");
					}
					int packageCount=stockService.getMailingBatchPackageCount("order_code='"+apBean.getOrderCode()+"' and package_code='"+packageCode+"' and balance_status<>3");
					if(packageCount!=0){
						return returnError("该包裹已被添加!");
					}
					MailingBatchPackageBean packageBean=new MailingBatchPackageBean();
					packageBean.setMailingBatchId(mbBean.getId());
					packageBean.setMailingBatchCode(mbBean.getCode());
					packageBean.setMailingBatchParcelId(mbpBean.getId());
					packageBean.setMailingBatchParcelCode(mbpBean.getCode());
					packageBean.setOrderCode(apBean.getOrderCode() );
					packageBean.setPackageCode(packageCode);
					packageBean.setCreateDatetime(DateUtil.getNow());
					packageBean.setAddress(order.getAddress());
					packageBean.setWeight(apBean.getWeight());
					packageBean.setTotalPrice(order.getDprice());
					packageBean.setDeliver(order.getDeliver());
					packageBean.setOrderId(order.getId());
					packageBean.setStockInDatetime("1111-11-11 11:11:11");
					packageBean.setAssignTime("1111-11-11 11:11:11");
					packageBean.setPostStaffId(0);
					packageBean.setPostStaffName("");
					packageBean.setStockInAdminId(0);
					packageBean.setStockInAdminName("");
					packageBean.setMailingStatus(0);
					packageBean.setReturnStatus(0);
					packageBean.setBalanceStatus(0);
					if(stockService.addMailingBatchPackage(packageBean)){
						int count=stockService.getMailingBatchPackageCount("mailing_batch_parcel_code='"+parcelCode+"'");
						return returnResult("成功：包裹数量"+ count);
					}else{
						dbOp.rollbackTransaction();
						return returnError("失败：包裹单"+ packageCode + "未添加成功!"); 
					}
				}
				return returnError("发送数据异常!");
			}else{
				return returnError("身份验证失败!"); 
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return returnError("操作失败,系统异常！");
		}finally{
			dbOp.release();
			dbOpSlave.release();
		}
	}
	/**
	 * 说明：分拣异常商品处理
	 * 
	 * 时间：2013-08-19
	 * 
	 * 作者：石远飞
	 */
	@RequestMapping("/abnormalProductHandle")
	@ResponseBody
	public JsonModel abnormalProductHandle(HttpServletRequest request){
		String cargoWholeCode ="";//推荐货位编号
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		SortingAbnormalDisposeService abnormalService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		SortingInfoService sortingService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		JsonModel json =  new JsonModel();
		voUser user = null;
		try{
			//从流中读取json数据
			json = ReceiveJson.receiveJson(request);
			if(json==null){
				return returnError("没有收到请求数据!");
			}
			//验证用户名密码
			if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
				user = (voUser)request.getSession().getAttribute("userView");
				int areaId = StringUtil.toInt(json.getArea());//地区id
				String code = StringUtil.convertNull((String)json.getData().get("code"));//波次号或者出库单号
				String productCode = StringUtil.convertNull((String)json.getData().get("productCode"));//SKU编号
				String cwCode = StringUtil.convertNull((String)json.getData().get("cwCode"));//实际输入的货位编号
				Map<String,Object> data = new HashMap<String, Object>();
				if("".equals(code)){
					return returnError("发送的数据缺失!");
				}
				if("".equals(productCode)){
					return returnError("发送的数据缺失!");
				}
				if(!"".equals(cwCode)){
					synchronized (cargoLock) {
						CargoInfoBean ciBean = cargoService.getCargoInfo(" status = 0 and whole_code='"+cwCode+"'");
						if(ciBean==null){
							return returnError("找不到该货位!");
						}
						if(ciBean.getAreaId() != areaId){
							return returnError("该货位地区不符合要求!");
						}
						voProduct productBean = wareService.getProduct(productCode);
						if(productBean==null){
							return returnError("该商品不存在!");
						}
						CargoProductStockBean cpsBean = cargoService.getCargoProductStock("cargo_id="+ciBean.getId()+" and product_id="+productBean.getId());
						if(cpsBean==null){
							return returnError("该货位没有关联该商品!");
						}
						//添加异常处理单编号
						SortingAbnormalBean saBean = new SortingAbnormalBean();
						String saCode=abnormalService.getNewSortingAbnormalCode(areaId,wareService.getDbOp());
						if("FAIL".equals(saCode)){
							return returnError("添加异常处理单编号失败!");
						}else{
							wareService.getDbOp().startTransaction(); //开始事务
							saBean.setCode(saCode);
							saBean.setOperCode(code);
							saBean.setStatus(SortingAbnormalBean.STATUS3);
							saBean.setAbnormalType(SortingAbnormalBean.ABNORMALTYPE2);
							saBean.setWareArea(areaId);
							saBean.setCreateDatetime(DateUtil.getNow());
							saBean.setCreateUserId(user.getId());
							saBean.setCreateUserName(user.getUsername());
							if("FJ".equalsIgnoreCase(code.substring(0, 2))){
								saBean.setOperType(1);
							}else{
								saBean.setOperType(0);
							}
							if(abnormalService.addSortingAbnormal(saBean)==false){
								wareService.getDbOp().rollbackTransaction();
								return returnError("数据库操作失败!");
							}
							int saId=dbOp.getLastInsertId();
							saBean.setId(saId);
							SortingAbnormalProductBean sapBean = new SortingAbnormalProductBean();
							sapBean.setSortingAbnormalId(saId);
							sapBean.setProductId(productBean.getId());
							sapBean.setProductCode(productBean.getCode());
							sapBean.setCargoWholeCode(cwCode);
							sapBean.setCount(1);
							sapBean.setLockCount(0);
							sapBean.setStatus(SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK);
							sapBean.setLastOperDatetime(DateUtil.getNow());
							if(abnormalService.addSortingAbnormalProduct(sapBean)==false){
								wareService.getDbOp().rollbackTransaction();
								return returnError("数据库操作失败!");
							}
							
							if(!insertBISortingAbnormalOrder(ciBean.getAreaId(), code, wareService.getDbOp())){
								wareService.getDbOp().rollbackTransaction();
								return returnError("数据库操作失败!");
							}
							
							wareService.getDbOp().commitTransaction();
						}
						return returnResult("处理成功!");
					}
				}
				//判断传过来的编号是波次号还是订单编号
				if("FJ".equalsIgnoreCase(code.substring(0, 2))){
					SortingBatchGroupBean groupBean = sortingService.getSortingBatchGroupInfo("storage="+areaId+" and code='"+code+"'");
					if(groupBean==null){
						return returnError("错误的分拣波次号!");
					}
				}else{
					OrderStockBean osBean = stockService.getOrderStock("stock_area="+areaId+" and code='"+code+"'");
					if(osBean==null){
						return returnError("仓库中找不到此出库单号!");
					}
					SortingAbnormalBean abBean = abnormalService.getSortingAbnormal("oper_code='"+code+"' and (status=0 or status=1)");
					if(abBean!=null){
						return returnError("存在未处理的异常单!");
					}
				}
				voProduct productBean = null;
				ProductBarcodeVO tempVO = bcmService.getProductBarcode("barcode='" + productCode + "'");
				if(tempVO!=null){
					productBean = wareService.getProduct(tempVO.getProductId());
				}else{
					productBean = wareService.getProduct(productCode);
				}
				if(productBean==null){
					return returnError("该商品不存在!");
				}
				//查找该SKU存在异常数据的货位,必须是异常单状态是未完成的异常商品
				String str = "select b.cargo_whole_code from sorting_abnormal a join sorting_abnormal_product b on a.id=b.sorting_abnormal_id  where a.ware_area=" + areaId +" and a.status<>5 and b.product_code='"+productCode+"' and b.cargo_whole_code  <>'' limit 1";
				ResultSet rs = abnormalService.getDbOp().executeQuery(str);
				if (rs.next()) {
					cargoWholeCode = rs.getString("b.cargo_whole_code");
				}
				rs.close();
				if(cargoWholeCode.length()==0){//如果没有异常数据的货位，则查找货位库存最大的货位
					CargoProductStockBean cpsBean = cargoService.getCargoAndProductStock(" ci.area_id = " + areaId + " AND ci.stock_type=0 AND ci.store_type IN (" + CargoInfoBean.STORE_TYPE0 + "," + CargoInfoBean.STORE_TYPE4 + ") AND cps.product_id = " + productBean.getId()+" ORDER BY cps.stock_count DESC LIMIT 1");
					//得到推荐货位编号
					if(cpsBean!=null){
						CargoInfoBean ciBean = cargoService.getCargoInfo("id="+cpsBean.getCargoId());
						cargoWholeCode=ciBean.getWholeCode();
					}else{
						return returnError("该商品找不到推荐货位!");
					}
				}
				data.put("cargoWholeCode", cargoWholeCode);
				data.put("productCode", productCode);
				data.put("code",code);
				return returnResult(data);
			}else{
				return returnError("身份验证失败!"); 
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return returnError("操作失败,系统异常！");
		}finally{
			dbOp.release();
		}
	}
	/**
	 * 说明：上架作业
	 * 
	 * 时间：2013-08-06
	 * 
	 * 作者：石远飞
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/upProduct")
	@ResponseBody
	public JsonModel upProduct(HttpServletRequest request){
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IProductStockService productService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,dbOp);
		CheckStockinMissionService checkStockinMissionService = new CheckStockinMissionService(IBaseService.CONN_IN_SERVICE,dbOp);
		ConsignmentService consignmentService = new ConsignmentService(IBaseService.CONN_IN_SERVICE,dbOp);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE,dbOp);
		voUser user = null;
		JsonModel json =  new JsonModel();
		try{
			synchronized (cargoLock) {
			service.getDbOp().startTransaction();    //开启事务
			//从流中读取json数据
			json = ReceiveJson.receiveJson(request);
			if(json==null){
				return returnError("没有收到请求数据!");
			}
			//验证用户名密码
			if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
				 user = (voUser)request.getSession().getAttribute("userView");
				String cartonningCode = StringUtil.convertNull((String)json.getData().get("code"));
				int areaId = StringUtil.toInt(json.getArea());//地区id
				if(areaId == -1){
					return returnError("没有收到选择的地区!");
				}
				CartonningInfoBean cartonningInfo=cartonningService.getCartonningInfo("code='"+cartonningCode+"'");
				if(cartonningInfo==null){
					return returnError("未找到该装箱单!");
				}
				if(cartonningInfo.getStatus()==CartonningInfoBean.STATUS2){
					return returnError("该装箱单已作废!");
				}
				CargoOperationBean cargoOperation = service.getCargoOperation("id=" + cartonningInfo.getOperId());
				if(cargoOperation != null){
					if(cargoOperation.getStatus()!= 8 && cargoOperation.getStatus()!= 17 && cargoOperation.getStatus()!= 26 && cargoOperation.getStatus()!= 35
							&& cargoOperation.getStatus()!= 9 && cargoOperation.getStatus()!= 18 && cargoOperation.getStatus()!= 27 && cargoOperation.getStatus()!= 36
							&& cargoOperation.getEffectStatus()!=3 && cargoOperation.getEffectStatus()!=4){
						service.getDbOp().rollbackTransaction();
						return returnError("此装箱单关联的作业单为未完成状态!");
					}
				}
				CargoInfoBean cargoInfo = service.getCargoInfo("id="+cartonningInfo.getCargoId());
				if(cargoInfo==null){
				    service.getDbOp().rollbackTransaction();
				    return returnError("未找到装箱单关联货位!");
				}

				if(cargoInfo.getStoreType()==1){
					return returnError("装箱单关联货位为整件区，不能生成上架单!");
				}
				CargoInfoAreaBean ciaBean = service.getCargoInfoArea("old_id = " + areaId);
				if(cargoInfo.getAreaId()!= ciaBean.getId()){
				    service.getDbOp().rollbackTransaction();
				    return returnError("关联货位不属于这个仓库!");
				}
				CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningInfo.getId());
				if(cartonningProduct==null){
				    service.getDbOp().rollbackTransaction();
				    return returnError("找不到作业量相关信息!");
				}
				ProductWarePropertyBean pwpBean = statService.getProductWareProperty("product_id=" + cartonningProduct.getProductId());
				if( pwpBean == null ) {
				    service.getDbOp().rollbackTransaction();
				    return returnError("作业单中商品"+ cartonningProduct.getProductCode()+ "没有商品物流属性！");
				}
				
				//新采购计划编号：CGJIH20090601001
				String code = "HWS"+DateUtil.getNow().substring(2,10).replace("-", "");   
				//生成编号
				CargoOperationBean cargoOper = service.getCargoOperation("code like '"+code+"%' order by id desc limit 1");
				if(cargoOper == null){
					code = code + "00001";
				}else{//获取当日计划编号最大值
					String _code = cargoOper.getCode();
					int number = Integer.parseInt(_code.substring(_code.length()-5));
					number++;
					code += String.format("%05d",new Object[]{new Integer(number)});
				}
				//areaId=1为芳村即：GZF areaId=3为增城即：GZZ
				String storageCode = "";
				if(areaId == ProductStockBean.AREA_GF){
					storageCode ="GZF";
				}else if(areaId == ProductStockBean.AREA_ZC){
					storageCode ="GZZ";
				} else if ( areaId == ProductStockBean.AREA_WX) {
					storageCode = "JSW";
				} else if( areaId == ProductStockBean.AREA_GS) {
					storageCode = "GZS";
				} else if( areaId == ProductStockBean.AREA_BJ) {
					storageCode = "BJA";
				}
				//------------上面对应的 无锡库的 StorageCode 是什么
				//添加作业单
				cargoOper = new CargoOperationBean();
				cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS3);
				cargoOper.setCreateDatetime(DateUtil.getNow());
				cargoOper.setRemark("");
				cargoOper.setCreateUserId(user.getId());
				cargoOper.setAuditingDatetime(DateUtil.getNow());
				cargoOper.setAuditingUserId(user.getId());
				cargoOper.setCode(code);
				cargoOper.setSource("");
				cargoOper.setStorageCode(storageCode);
				cargoOper.setStockInType(CargoInfoBean.STORE_TYPE4);//111
				cargoOper.setStockOutType(CargoInfoBean.STORE_TYPE1);
				cargoOper.setCreateUserName(user.getUsername());
				cargoOper.setAuditingUserName(user.getUsername());
				cargoOper.setType(CargoOperationBean.TYPE0);
				cargoOper.setLastOperateDatetime(DateUtil.getNow());
				cargoOper.setConfirmDatetime(DateUtil.getNow());
				
				if(!service.addCargoOperation(cargoOper)){
					service.getDbOp().rollbackTransaction();
					return returnError("添加上架单失败!");
				}
				int operId = service.getDbOp().getLastInsertId();
				
				//在根据入库单生成上架单的时候 如果找到了在生成质检装箱单的单据时  修改当中的上架单关联字段， 确保关联的是最后一个上架单信息
				BuyStockinUpshelfBean bsuBean = checkStockinMissionService.getBuyStockinUpshelf("cartonning_info_id=" + cartonningInfo.getId());
				if( bsuBean != null ) {
					if( !checkStockinMissionService.updateBuyStockinUpshelf("cargo_operation_id=" + operId, "id=" + bsuBean.getId()) ) {
						service.getDbOp().rollbackTransaction();
						return returnError("关联装箱单和上架单失败！");
					} 
				}
				
				String inCargoWholeCode = "";
				List cargoAndProductStockList = new ArrayList();
				if( consignmentService.isProductConsignment(cartonningProduct.getProductId())) {
					cargoAndProductStockList = service.getCargoAndProductStockWithStockAreaCodeRestrictList("ci.store_type=" + CargoInfoBean.STORE_TYPE4 + 
							" and ci.stock_type=0 and cps.product_id=" +cartonningProduct.getProductId()+" and ci.area_id="+ ciaBean.getId() + 
							" and ci.status="+CargoInfoBean.STATUS0+" and ci.id!="+cartonningInfo.getCargoId()+" and ci.whole_code not like 'GZZ01-C%'", 
							-1, -1, "stock_count DESC", Constants.CONSIGNMENT_STOCK_AREA);//相同sku 库存最多的货位
				} else {
					cargoAndProductStockList = service.getCargoAndProductStockList("ci.store_type=" + CargoInfoBean.STORE_TYPE4 + 
							" and ci.stock_type=0 and cps.product_id=" +cartonningProduct.getProductId()+" and ci.area_id="+ ciaBean.getId() + 
							" and ci.status="+CargoInfoBean.STATUS0+" and ci.id!="+cartonningInfo.getCargoId()+" and ci.whole_code not like 'GZZ01-C%'", 
							-1, -1, "stock_count DESC");//相同sku 库存最多的货位
				}
				voProduct product = wareService.getProduct(cartonningProduct.getProductId());
				//123
				List<CargoInfoBean> relatedAvailList = new ArrayList<CargoInfoBean>();
				
				//查询合适的目的货位
				for(int i=0;i<cargoAndProductStockList.size();i++){
					CargoProductStockBean cpsBean = (CargoProductStockBean)cargoAndProductStockList.get(i);
					relatedAvailList.add(cpsBean.getCargoInfo());
				}
				//修改目的货位选择方式
				if(relatedAvailList.size()>0){//相同商品
					Random ran=new Random();
					inCargoWholeCode=relatedAvailList.get(ran.nextInt(relatedAvailList.size())).getWholeCode();
				}
				if(inCargoWholeCode.length()==0){//相同产品线
					voProductLine productLine=wareService.getProductLine("product_line_catalog.catalog_id="+product.getParentId1()+" or product_line_catalog.catalog_id="+product.getParentId2());
					if(productLine==null){
						service.getDbOp().rollbackTransaction();
						return returnError("产品："+product.getCode()+"产品线未知!");
					}
					List cargoList = new ArrayList();
					if( consignmentService.isProductConsignment(cartonningProduct.getProductId()) ) {
						cargoList = service.getCargoInfoWithStockAreaCodeRestrictList("ci.store_type=" + CargoInfoBean.STORE_TYPE4 + " and ci.product_line_id=" + productLine.getId()+" and ci.area_id="+cargoInfo.getAreaId()+" and ci.stock_type=0 and ci.status=" + CargoInfoBean.STATUS0+" and ci.id!="+cartonningInfo.getCargoId()+" and ci.whole_code not like 'GZZ01-C%'", -1, -1, null, Constants.CONSIGNMENT_STOCK_AREA);
					} else {
						cargoList = service.getCargoInfoList("store_type=" + CargoInfoBean.STORE_TYPE4 + " and product_line_id=" + productLine.getId()+" and area_id="+cargoInfo.getAreaId()+" and stock_type=0 and status=" + CargoInfoBean.STATUS0+" and id!="+cartonningInfo.getCargoId()+" and whole_code not like 'GZZ01-C%'", -1, -1, null);
					}
					if(cargoList.size()>0){
						Random ran=new Random();
						inCargoWholeCode=((CargoInfoBean)(cargoList.get(ran.nextInt(cargoList.size())))).getWholeCode();
					}
				}
				if(inCargoWholeCode.length()==0){//全仓随机
					List cargoList = new ArrayList();
					if( consignmentService.isProductConsignment(cartonningProduct.getProductId()) ) {
						cargoList = service.getCargoInfoWithStockAreaCodeRestrictList("ci.store_type=" + CargoInfoBean.STORE_TYPE4 + " and ci.area_id="+cargoInfo.getAreaId()+" and ci.stock_type=0 and ci.status=" + CargoInfoBean.STATUS0+" and ci.id!="+cartonningInfo.getCargoId()+" and ci.whole_code not like 'GZZ01-C%'", -1, -1, null, Constants.CONSIGNMENT_STOCK_AREA);
					} else {
						cargoList = service.getCargoInfoList("store_type=" + CargoInfoBean.STORE_TYPE4 + " and area_id="+cargoInfo.getAreaId()+" and stock_type=0 and status=" + CargoInfoBean.STATUS0+" and id!="+cartonningInfo.getCargoId()+" and whole_code not like 'GZZ01-C%'", -1, -1, null);
					}
					if(cargoList.size()>0){
						Random ran=new Random();
						inCargoWholeCode=((CargoInfoBean)(cargoList.get(ran.nextInt(cargoList.size())))).getWholeCode();
					}
				}
				//目的货位选择结束
				
				if(inCargoWholeCode.length()==0){
					service.getDbOp().rollbackTransaction();
					return returnError("没有货位可用!");
				}
				CargoInfoBean inCargoInfo = service.getCargoInfo("whole_code='" + inCargoWholeCode + "'");
				CargoInfoBean outCargoInfo = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
				CargoProductStockBean inCargoProductStock = service.getCargoProductStock("cargo_id=" + inCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
				if(inCargoProductStock==null){
					inCargoProductStock = new CargoProductStockBean();
					inCargoProductStock.setCargoId(inCargoInfo.getId());
					inCargoProductStock.setProductId(cartonningProduct.getProductId());
					inCargoProductStock.setStockCount(0);
					inCargoProductStock.setStockLockCount(0);
					if(!service.addCargoProductStock(inCargoProductStock)){
						service.getDbOp().rollbackTransaction();
						return returnError("数据库操作失败!");
					}
					inCargoProductStock.setId(dbOp.getLastInsertId());
				}
				CargoProductStockBean outCargoProductStock = service.getCargoProductStock("cargo_id=" + outCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
				CargoOperationCargoBean bean = new CargoOperationCargoBean();//添加 CargoOperationCargoBean
				bean.setOperId(operId);
				bean.setProductId(cartonningProduct.getProductId());
				bean.setInCargoProductStockId(0);
				bean.setInCargoWholeCode("");
				bean.setOutCargoProductStockId(outCargoProductStock.getId());
				bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
				bean.setStockCount(cartonningProduct.getProductCount());
				bean.setType(1);
				bean.setUseStatus(0);
				if(!service.addCargoOperationCargo(bean)){
					service.getDbOp().rollbackTransaction();
					return returnError("添加货位详细信息失败!");
				}
				bean.setOperId(operId);
				bean.setProductId(cartonningProduct.getProductId());
				bean.setInCargoProductStockId(inCargoProductStock.getId());
				bean.setInCargoWholeCode(inCargoInfo.getWholeCode());
				bean.setOutCargoProductStockId(outCargoProductStock.getId());
				bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
				bean.setStockCount(cartonningProduct.getProductCount());
				bean.setType(0);
				bean.setUseStatus(1);
				if(!service.addCargoOperationCargo(bean)){
					service.getDbOp().rollbackTransaction();
					return returnError("添加货位详细信息失败!");
				}
				if(!cartonningService.updateCartonningInfo("oper_id=" + operId, "code='" + cartonningCode + "'")){
					service.getDbOp().rollbackTransaction();
					return returnError("更新装箱单，数据库操作失败!");
				}
				//锁库存
				if(!service.updateCargoProductStockCount(outCargoProductStock.getId(), -cartonningProduct.getProductCount())){
					service.getDbOp().rollbackTransaction();
					return returnError("操作失败，货位冻结库存不足!");
				}
				if(!service.updateCargoProductStockLockCount(outCargoProductStock.getId(), cartonningProduct.getProductCount())){
					service.getDbOp().rollbackTransaction();
					return returnError("操作失败，货位冻结库存不足!");
				}
				if(inCargoInfo.getAreaId() != outCargoInfo.getAreaId() || inCargoInfo.getStockType() != outCargoInfo.getStockType()){
					CargoInfoAreaBean area=service.getCargoInfoArea("id="+outCargoInfo.getAreaId());
					ProductStockBean productStock = productService.getProductStock("product_id=" + cartonningProduct.getProductId()+" and area="+area.getOldId()+" and type="+outCargoInfo.getStockType());
					if(productStock == null){
						service.getDbOp().rollbackTransaction();
						return returnError("找不到商品库存信息!");
					}
					if(!productService.updateProductLockCount(productStock.getId(), cartonningProduct.getProductCount())){
						service.getDbOp().rollbackTransaction();
						return returnError("操作失败，合格库冻结库存不足!");
					}
					if(!productService.updateProductStockCount(productStock.getId(), -cartonningProduct.getProductCount())){
						service.getDbOp().rollbackTransaction();
						return returnError("操作失败，合格库库存不足!");
					}
				}
				
				//合格库待作业任务处理
				CargoOperationTodoBean cot=cartonningService.getCargoOperationTodo("product_id="+cartonningInfo.getId()+" and status in(0,1,2) and type=0");
				if(cot!=null){
					if(!cartonningService.updateCargoOperationTodo("status=2", "id="+cot.getId())){
						service.getDbOp().rollbackTransaction();
						return returnError("更新待作业任务状态，数据库操作失败!");
					}
				}
				//物流员工绩效考核操作
				CargoStaffBean csBean = service.getCargoStaff(" user_id=" + user.getId());
				if(csBean == null){
					service.getDbOp().rollbackTransaction();
					return returnError("此账号不是物流员工 !");
				}
	    		CargoStaffPerformanceBean cspBean = service.getCargoStaffPerformance(" date='" + date + "' and type=3 and staff_id=" + csBean.getId() );
	    		int operCount = 1;
	    		int performanceProductCount = cartonningProduct.getProductCount();
	    		if(cspBean != null){
	    			performanceProductCount = performanceProductCount + cspBean.getProductCount();
	    			operCount = operCount + cspBean.getOperCount();
					boolean flag = service.updateCargoStaffPerformance(" oper_count=" + operCount + ", product_count=" + performanceProductCount, " id=" + cspBean.getId());
					if(!flag){
						service.getDbOp().rollbackTransaction();
						return returnError("物流员工绩效考核更新操作失败 !");
					}
				}else{
					CargoStaffPerformanceBean newBean = new CargoStaffPerformanceBean();
					newBean.setDate(date);
					newBean.setProductCount(performanceProductCount);
					newBean.setOperCount(operCount);
					newBean.setStaffId(csBean.getId());
					newBean.setType(3);  //3代表上架作业
					boolean flag = service.addCargoStaffPerformance(newBean);
					if(!flag){
						service.getDbOp().rollbackTransaction();
						return returnError("物流员工绩效考核添加操作失败 !");
					}
				}
				service.getDbOp().commitTransaction();  //提交事务
				return returnResult("上架单:" + code + "已创建成功!");
			}else{
				return returnError("身份验证失败!");
			}
		}
			
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			return returnError("操作失败,系统异常！");
		}finally{
			dbOp.release();
		}
	}
	/**
	 * 说明：下架作业
	 * 
	 * 时间：2013-08-07
	 * 
	 * 作者：石远飞
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/downProduct")
	@ResponseBody
	public JsonModel downProduct(HttpServletRequest request){
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		IProductStockService productService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,dbOp);
		voUser user = null;
		JsonModel json =  new JsonModel();
		try{
			synchronized (cargoLock) {
				service.getDbOp().startTransaction();    //开启事务
				//从流中读取json数据
				json = ReceiveJson.receiveJson(request);
				if(json==null){
					return returnError("没有收到请求数据!");
				}
				//验证用户名密码
				if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
					user = (voUser)request.getSession().getAttribute("userView");
					String cartonningCode = StringUtil.convertNull((String)json.getData().get("code"));
					int areaId = StringUtil.toInt(json.getArea());//地区id
					if(areaId == -1){
						return returnError("没有收到选择的地区!");
					}
					CartonningInfoBean cartonningInfo=cartonningService.getCartonningInfo("code='"+cartonningCode+"'");
					if(cartonningInfo==null){
						return returnError("未找到该装箱单!");
					}
					if(cartonningInfo.getStatus()==CartonningInfoBean.STATUS2){
						return returnError("该装箱单已作废!");
					}
					CargoOperationBean cargoOperation = service.getCargoOperation("id=" + cartonningInfo.getOperId());
					if(cargoOperation != null){
						if(cargoOperation.getStatus()!= 8 && cargoOperation.getStatus()!= 17 && cargoOperation.getStatus()!= 26 && cargoOperation.getStatus()!= 35
								&& cargoOperation.getStatus()!= 9 && cargoOperation.getStatus()!= 18 && cargoOperation.getStatus()!= 27 && cargoOperation.getStatus()!= 36
								&& cargoOperation.getEffectStatus()!=3 && cargoOperation.getEffectStatus()!=4){
							return returnError("此装箱单关联的作业单为未完成状态!");
						}
					}
		 			CargoInfoBean cargoInfo = service.getCargoInfo("id="+cartonningInfo.getCargoId());
					if(cargoInfo==null){
						return returnError("未找到装箱单关联货位!");
					}
					if(cargoInfo.getAreaId()!=areaId){
						return returnError("关联货位不属于这个仓库!");
					}
					CartonningProductInfoBean cartonningProduct = cartonningService.getCartonningProductInfo("cartonning_id="+cartonningInfo.getId());
					if(cartonningProduct==null){
						return returnError("根据此装箱单号找不到库存信息!");
					}
					//新采购计划编号：CGJIH20090601001
					String code = "HWX"+DateUtil.getNow().substring(2,10).replace("-", "");   
					//生成编号
					CargoOperationBean cargoOper = service.getCargoOperation("code like '"+code+"%' order by id desc limit 1");
					if(cargoOper == null){
						code = code + "00001";
					}else{//获取当日计划编号最大值
						String _code = cargoOper.getCode();
						int number = Integer.parseInt(_code.substring(_code.length()-5));
						number++;
						code += String.format("%05d",new Object[]{new Integer(number)});
					}
					//areaId=1为芳村即：GZF areaId=3为增城即：GZZ
					String storageCode = "";
					if(areaId == 1){
						storageCode ="GZF";
					}else if(areaId == 3){
						storageCode ="GZZ";
					}
					//添加作业单
					cargoOper = new CargoOperationBean();
					cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS12);
					cargoOper.setCreateDatetime(DateUtil.getNow());
					cargoOper.setRemark("");
					cargoOper.setCreateUserId(user.getId());
					cargoOper.setAuditingDatetime(DateUtil.getNow());
					cargoOper.setAuditingUserId(user.getId());
					cargoOper.setCode(code);
					cargoOper.setSource("");
					cargoOper.setStorageCode(storageCode);
					cargoOper.setStockInType(CargoInfoBean.STORE_TYPE1);
					cargoOper.setStockOutType(CargoInfoBean.STORE_TYPE0);
					cargoOper.setCreateUserName(user.getUsername());
					cargoOper.setAuditingUserName(user.getUsername());
					cargoOper.setType(CargoOperationBean.TYPE1);
					cargoOper.setLastOperateDatetime(DateUtil.getNow());
					cargoOper.setConfirmDatetime(DateUtil.getNow());
					
					service.getDbOp().startTransaction();      //开启事务 
					if(!service.addCargoOperation(cargoOper)){
						service.getDbOp().rollbackTransaction();
						return returnError("添加下架单，数据库操作失败!");
					}
					int operId = service.getDbOp().getLastInsertId();
					String inCargoWholeCode = "";
					//目的货位选择条件为 目的货位为地区合格库缓存区唯一货位
					List cargoAndProductStockList = service.getCargoAndProductStockList("ci.store_type=2 and ci.stock_type=0 and ci.area_id="+cargoInfo.getAreaId(), -1, -1,null);
					if(cargoAndProductStockList.size() > 0){
						inCargoWholeCode = ((CargoProductStockBean)cargoAndProductStockList.get(0)).getCargoInfo().getWholeCode();
					}else{
						service.getDbOp().rollbackTransaction();
						return returnError("没有货位可用!");
					}
					CargoInfoBean inCargoInfo = service.getCargoInfo("whole_code='" + inCargoWholeCode + "'");
					CargoInfoBean outCargoInfo = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
					CargoProductStockBean inCargoProductStock = service.getCargoProductStock("cargo_id=" + inCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
					CargoProductStockBean outCargoProductStock = service.getCargoProductStock("cargo_id=" + outCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
					if(outCargoProductStock.getStockCount() <cartonningProduct.getProductCount()){
						service.getDbOp().rollbackTransaction();
						return returnError("可用库存量不足,不允许下架!");
					}
					CargoOperationCargoBean bean = new CargoOperationCargoBean();//添加 CargoOperationCargoBean
					bean.setOperId(operId);
					bean.setProductId(cartonningProduct.getProductId());
					bean.setInCargoProductStockId(0);
					bean.setInCargoWholeCode("");
					bean.setOutCargoProductStockId(outCargoProductStock.getId());
					bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
					bean.setStockCount(cartonningProduct.getProductCount());
					bean.setType(0);
					bean.setUseStatus(0);
					if(!service.addCargoOperationCargo(bean)){
						service.getDbOp().rollbackTransaction();
						return returnError("添加下架单详细信息，数据库操作失败!");
					}
					bean.setOperId(operId);
					bean.setProductId(cartonningProduct.getProductId());
					bean.setInCargoProductStockId(inCargoProductStock.getId());
					bean.setInCargoWholeCode(inCargoInfo.getWholeCode());
					bean.setOutCargoProductStockId(outCargoProductStock.getId());
					bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
					bean.setStockCount(cartonningProduct.getProductCount());
					bean.setType(1);
					bean.setUseStatus(1);
					if(!service.addCargoOperationCargo(bean)){
						service.getDbOp().rollbackTransaction();
						return returnError("添加下架单详细信息，数据库操作失败!");
					}
					if(!cartonningService.updateCartonningInfo("oper_id=" + operId, "code='" + cartonningCode + "'")){
						service.getDbOp().rollbackTransaction();
						return returnError("更新装箱单，数据库操作失败!");
					}
					//锁库存
					if(!service.updateCargoProductStockCount(outCargoProductStock.getId(), -cartonningProduct.getProductCount())){
						service.getDbOp().rollbackTransaction();
						return returnError("操作失败，货位冻结库存不足!");
					}
					if(!service.updateCargoProductStockLockCount(outCargoProductStock.getId(), cartonningProduct.getProductCount())){
						service.getDbOp().rollbackTransaction();
						return returnError("操作失败，货位冻结库存不足!");
					}
					if(inCargoInfo.getAreaId() != outCargoInfo.getAreaId() || inCargoInfo.getStockType() != outCargoInfo.getStockType()){
						CargoInfoAreaBean area=service.getCargoInfoArea("id="+outCargoInfo.getAreaId());
						ProductStockBean productStock = productService.getProductStock("product_id=" + cartonningProduct.getProductId()+" and area="+area.getOldId()+" and type="+outCargoInfo.getStockType());
						if(productStock == null){
							service.getDbOp().rollbackTransaction();
							return returnError("找不到商品库存信息!");
						}
						if(!productService.updateProductLockCount(productStock.getId(), cartonningProduct.getProductCount())){
							service.getDbOp().rollbackTransaction();
							return returnError("操作失败，合格库冻结库存不足!");
						}
						if(!productService.updateProductStockCount(productStock.getId(), -cartonningProduct.getProductCount())){
							service.getDbOp().rollbackTransaction();
							return returnError("操作失败，合格库库存不足!");
						}
					}
					
					//合格库待作业任务处理
					CargoOperationTodoBean cot=cartonningService.getCargoOperationTodo("cargo_product_stock_id="+outCargoProductStock.getId()+" and status in(0,1,2) and type=1");
					if(cot!=null){
						if(!cartonningService.updateCargoOperationTodo("status=2", "id="+cot.getId()))
						{
						  service.getDbOp().rollbackTransaction();
						  return returnError("数据库操作失败!");
						}
					}
					service.getDbOp().commitTransaction();// 提交事务
					return returnResult("下架单:" + code + "已创建成功!");
				}else{
					return returnError("身份验证失败!"); 
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			return returnError("操作失败,系统异常！");
		}finally{
			dbOp.release();
		}
	}
	/**
	 * 说明：调拨作业
	 * 
	 * 时间：2013-08-07
	 * 
	 * 作者：石远飞
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping("/createDeploy")
	@ResponseBody
	public JsonModel createDeploy(HttpServletRequest request){
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		IProductStockService productService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,dbOp);
		StatService statService = new StatService(IBaseService.CONN_IN_SERVICE,dbOp);
		ConsignmentService consignmentService = new ConsignmentService(IBaseService.CONN_IN_SERVICE,dbOp);
		JsonModel json =  new JsonModel();
		try{
			synchronized (cargoLock) {
				service.getDbOp().startTransaction();    //开启事务
				//从流中读取json数据
				json = ReceiveJson.receiveJson(request);
				if(json==null){
					return returnError("没有收到请求数据!");
				}
				//验证用户名密码
				if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
					voUser user = (voUser)request.getSession().getAttribute("userView");
					String cartonningCode = StringUtil.convertNull((String)json.getData().get("code"));
					cartonningCode = cartonningCode.replace("'", "");
					int areaId = StringUtil.toInt(json.getArea());//地区id
					if(areaId == -1){
						return returnError("没有收到选择的地区!");
					}
					CartonningInfoBean cartonningInfo=cartonningService.getCartonningInfo("code='"+cartonningCode+"'");
					if(cartonningInfo==null){
						return returnError("未找到该装箱单!");
					}
					CargoInfoBean cargoInfoBean = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
					if(cargoInfoBean == null){
						return returnError("未找到此装箱单关联的货位!");
					}
					
					if(cartonningInfo.getStatus()==CartonningInfoBean.STATUS2){
						return returnError("该装箱单已作废!");
					}
					CargoOperationBean cargoOperation = service.getCargoOperation("id=" + cartonningInfo.getOperId());
					if(cargoOperation != null){
						if(cargoOperation.getStatus()!= 8 && cargoOperation.getStatus()!= 17 && cargoOperation.getStatus()!= 26 && cargoOperation.getStatus()!= 35
								&& cargoOperation.getStatus()!= 9 && cargoOperation.getStatus()!= 18 && cargoOperation.getStatus()!= 27 && cargoOperation.getStatus()!= 36
								&& cargoOperation.getEffectStatus()!=3 && cargoOperation.getEffectStatus()!=4){
							return returnError("此装箱单关联的作业单为未完成状态!");
						}
					}
					CargoInfoBean cargoInfo=service.getCargoInfo("id="+cartonningInfo.getCargoId());
					if(cargoInfo==null){
						return returnError("未找到装箱单关联货位!");
					}
					if(cargoInfo.getAreaId()!=areaId){
						return returnError("关联货位不属于这个仓库!");
					}
					if(cargoInfo.getStoreType() == CargoInfoBean.STORE_TYPE7){
						return returnError("装箱单关联货位为下架区，不能进行货位间调拨!");
					}
					CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningInfo.getId());
					if(cartonningProduct==null){
						return returnError("根据此装箱单号找不到库存信息!");
					}
					voProduct product = wareService.getProduct(cartonningProduct.getProductId());
					if(product == null){
						return returnError("找不到商品的相关信息!");
					}
					//新采购计划编号：CGJIH20090601001
					String code = "HWD"+DateUtil.getNow().substring(2,10).replace("-", "");   
					//生成编号
					CargoOperationBean cargoOper = service.getCargoOperation("code like '"+code+"%' order by id desc limit 1");
					if(cargoOper == null){
						code = code + "00001";
					}else{//获取当日计划编号最大值
						String _code = cargoOper.getCode();
						int number = Integer.parseInt(_code.substring(_code.length()-5));
						number++;
						code += String.format("%05d",new Object[]{new Integer(number)});
					}
					//areaId=1为芳村即：GZF areaId=3为增城即：GZZ
					String storageCode = "";
					if(areaId == 1){
						storageCode ="GZF";
					}else if(areaId == 3){
						storageCode ="GZZ";
					}
					//添加作业单
					cargoOper = new CargoOperationBean();
					cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS30);
					cargoOper.setCreateDatetime(DateUtil.getNow());
					cargoOper.setRemark("");
					cargoOper.setCreateUserId(user.getId());
					cargoOper.setAuditingDatetime(DateUtil.getNow());
					cargoOper.setAuditingUserId(user.getId());
					cargoOper.setCode(code);
					cargoOper.setSource("");
					cargoOper.setStorageCode(storageCode);
					cargoOper.setStockInType(cargoInfoBean.getStoreType());
					cargoOper.setStockOutType(cargoInfoBean.getStoreType());
					
					cargoOper.setCreateUserName(user.getUsername());
					cargoOper.setAuditingUserName(user.getUsername());
					cargoOper.setType(CargoOperationBean.TYPE3);
					cargoOper.setLastOperateDatetime(DateUtil.getNow());
					cargoOper.setConfirmDatetime(DateUtil.getNow());
					
					service.getDbOp().startTransaction();   //开启事务
					if(!service.addCargoOperation(cargoOper)){
						service.getDbOp().rollbackTransaction();
						return returnError("添加调拨单，数据库操作失败!");
					}
					int operId = service.getDbOp().getLastInsertId();
					String inCargoWholeCode = "";
					List cargoInfoList = new ArrayList();
					if( consignmentService.isProductConsignment(cartonningProduct.getProductId())) {
						cargoInfoList = service.getCargoInfoWithStockAreaCodeAndCPSRestrictList("ci.store_type=4 and ci.status=0 and ci.stock_type=0  and ci.id != "+ cargoInfo.getId() +" and cps.product_id=" +cartonningProduct.getProductId() + " and ci.area_id=" + areaId, -1, -1, "cps.stock_count DESC", Constants.CONSIGNMENT_STOCK_AREA);
					} else {
						cargoInfoList = service.getCargoInfoBeanist("ci.store_type=4 and ci.status=0 and ci.stock_type=0  and ci.id != "+ cargoInfo.getId() +" and cps.product_id=" +cartonningProduct.getProductId() + " and ci.area_id=" + areaId, -1, -1, "cps.stock_count DESC");
					}
					if(cargoInfoList.size() > 0){
						//找一个容积差最 小 的货位
						ProductWarePropertyBean pwpBean = statService.getProductWareProperty("product_id=" + cartonningProduct.getProductId());
						if( pwpBean == null ) {
							service.getDbOp().rollbackTransaction();
							return returnError("装箱单中商品"+ cartonningProduct.getProductCode()+ "没有商品物流属性!");
						}
						CargoInfoBean ciBean = cartonningService.getMinLeftVolumeCargo(cargoInfoList, (pwpBean.calculateVolume() * cartonningProduct.getProductCount() ));
						if( ciBean == null ) {
							service.getDbOp().rollbackTransaction();
							return returnError("没有找到容积差最小的货位!");
						}
						inCargoWholeCode = ciBean.getWholeCode();
						
						//合格库待作业任务处理
						CargoInfoBean outCargoInfo = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
						CargoProductStockBean outCargoProductStock = service.getCargoProductStock("cargo_id=" + outCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
						CargoOperationTodoBean cot=cartonningService.getCargoOperationTodo("cargo_product_stock_id="+outCargoProductStock.getId()+" and status in(0,1,2) and type=3");
						if(cot!=null){
							if(!cartonningService.updateCargoOperationTodo("status=2", "id="+cot.getId()))
							{
							  service.getDbOp().rollbackTransaction();
							  return returnError("数据库操作失败!");
							}
						}
					}else{//全仓随机选取货位
						List allCargoList = new ArrayList();
						if( consignmentService.isProductConsignment(cartonningProduct.getProductId())) { 
							allCargoList = service.getCargoInfoWithStockAreaCodeRestrictList("ci.store_type=" + CargoInfoBean.STORE_TYPE4 + " and ci.area_id="+cargoInfo.getAreaId()+" and ci.stock_type=0 and ci.status=" + CargoInfoBean.STATUS0+" and ci.id!="+cartonningInfo.getCargoId(), -1, -1, null,Constants.CONSIGNMENT_STOCK_AREA);
						} else {
							allCargoList = service.getCargoInfoList("store_type=" + CargoInfoBean.STORE_TYPE4 + " and area_id="+cargoInfo.getAreaId()+" and stock_type=0 and status=" + CargoInfoBean.STATUS0+" and id!="+cartonningInfo.getCargoId(), -1, -1, null);
						}
						if(allCargoList.size()>0){
							Random ran=new Random();
							inCargoWholeCode=((CargoInfoBean)(allCargoList.get(ran.nextInt(allCargoList.size())))).getWholeCode();
						}else{
							service.getDbOp().rollbackTransaction();
							return returnError("没有找到合适的货位!");
						}
					}
					CargoInfoBean inCargoInfo = service.getCargoInfo("whole_code='" + inCargoWholeCode + "'");
					CargoInfoBean outCargoInfo = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
					CargoProductStockBean inCargoProductStock = service.getCargoProductStock("cargo_id=" + inCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
					if(inCargoProductStock==null){
						CargoInfoBean icf = service.getCargoInfo("whole_code='" + inCargoWholeCode + "'");
						inCargoProductStock = new CargoProductStockBean();
						inCargoProductStock.setCargoId(icf.getId());
						inCargoProductStock.setProductId(cartonningProduct.getProductId());
						inCargoProductStock.setStockCount(0);
						inCargoProductStock.setStockLockCount(0);
						if(!service.addCargoProductStock(inCargoProductStock)){
							service.getDbOp().rollbackTransaction();
							return returnError("关联货位信息，数据库操作失败!");
						}
						inCargoProductStock.setId(dbOp.getLastInsertId());
					}
					CargoProductStockBean outCargoProductStock = service.getCargoProductStock("cargo_id=" + outCargoInfo.getId()+" and product_id="+cartonningProduct.getProductId());
						
					CargoOperationCargoBean bean = new CargoOperationCargoBean();//添加 CargoOperationCargoBean
					bean.setOperId(operId);
					bean.setProductId(cartonningProduct.getProductId());
					bean.setInCargoProductStockId(0);
					bean.setInCargoWholeCode("");
					bean.setOutCargoProductStockId(outCargoProductStock.getId());
					bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
					bean.setStockCount(cartonningProduct.getProductCount());
					bean.setType(1);
					bean.setUseStatus(0);
					if(!service.addCargoOperationCargo(bean)){
						service.getDbOp().rollbackTransaction();
						return returnError("添加补货单详细信息，数据库操作失败!");
					}
					bean.setOperId(operId);
					bean.setProductId(cartonningProduct.getProductId());
					bean.setInCargoProductStockId(inCargoProductStock.getId());
					bean.setInCargoWholeCode(inCargoInfo.getWholeCode());
					bean.setOutCargoProductStockId(outCargoProductStock.getId());
					bean.setOutCargoWholeCode(outCargoInfo.getWholeCode());
					bean.setStockCount(cartonningProduct.getProductCount());
					bean.setType(0);
					bean.setUseStatus(1);
					if(!service.addCargoOperationCargo(bean)){
						service.getDbOp().rollbackTransaction();
						return returnError("添加补货单详细信息，数据库操作失败!");
					}
					if(!cartonningService.updateCartonningInfo("oper_id=" + operId, "code='" + cartonningCode + "'")){
						service.getDbOp().rollbackTransaction();
						return returnError("关联装箱单和调拨单，数据库操作失败!");
					}
					//锁库存
					if(!service.updateCargoProductStockCount(outCargoProductStock.getId(), -cartonningProduct.getProductCount())){
						service.getDbOp().rollbackTransaction();
						return returnError("操作失败，货位冻结库存不足!");
					}
					if(!service.updateCargoProductStockLockCount(outCargoProductStock.getId(), cartonningProduct.getProductCount())){
						service.getDbOp().rollbackTransaction();
						return returnError("操作失败，货位冻结库存不足!");
					}
					if(inCargoInfo.getAreaId() != outCargoInfo.getAreaId() || inCargoInfo.getStockType() != outCargoInfo.getStockType()){
						CargoInfoAreaBean area=service.getCargoInfoArea("id="+outCargoInfo.getAreaId());
						ProductStockBean productStock = productService.getProductStock("product_id=" + cartonningProduct.getProductId()+" and area="+area.getOldId()+" and type="+outCargoInfo.getStockType());
						if(productStock == null){
							service.getDbOp().rollbackTransaction();
							return returnError("找不到商品库存信息!");
						}
						if(!productService.updateProductLockCount(productStock.getId(), cartonningProduct.getProductCount())){
							service.getDbOp().rollbackTransaction();
							return returnError("操作失败，合格库冻结库存不足!");
						}
						if(!productService.updateProductStockCount(productStock.getId(), -cartonningProduct.getProductCount())){
							service.getDbOp().rollbackTransaction();
							return returnError("操作失败，合格库库存不足!");
						}
					}
					service.getDbOp().commitTransaction();  //提交事务
					return returnResult("调拨单:" + code + "已创建成功!");
				}else{
					return returnError("身份验证失败!"); 
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			return returnError("操作失败,系统异常！");
		}finally{
			dbOp.release();
		}
	}
	/**
	 * 说明：退货上架单完成
	 * 
	 * 时间：2013-08-29
	 * 
	 * 作者：石远飞
	 */
	@RequestMapping("/pdaConfirmOpShelf")
	@ResponseBody
	public JsonModel pdaConfirmOpShelf(HttpServletRequest request){
		JsonModel json =  new JsonModel();
		DbOperation dbOperation=new DbOperation();
		WareService wareService = new WareService(dbOperation);
		IProductStockService productservice = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(BaseServiceImpl.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			//从流中读取json数据
			json = ReceiveJson.receiveJson(request);
			if(json==null){
				return returnError("没有收到请求数据!");
			}
			//验证用户名密码
			if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
				voUser user = (voUser)request.getSession().getAttribute("userView");
				UserGroupBean group = user.getGroup();
				if(!group.isFlag(618)){
					return returnError("您没有操作该功能的权限!");
				}
				String operationCode = StringUtil.convertNull((String)json.getData().get("code"));
				String wholeCode = StringUtil.convertNull((String)json.getData().get("wholeCode"));
				if ("".equals(operationCode)) {
					return returnError("没有收到退货上架单编号!");
				}
				if ("".equals(wholeCode)) {
					return returnError("没有收到货位编号!");
				}
				int area = StringUtil.toInt(json.getArea());//地区id
				if(area == -1){
					return returnError("没有收到选择的地区!");
				}
				//判断是否为退货上架单
				CargoOperationBean coBean = service.getCargoOperation("code='" + StringUtil.toSql(operationCode) + "'");
				if(coBean == null){
					return returnError("该上架单不存在!");
				}
				if(!coBean.getCode().startsWith("HWTS")){
					return returnError("该上架单不是退货上架单!");
				}
				if((coBean.getStatus() != CargoOperationProcessBean.OPERATION_STATUS2 &&
						coBean.getStatus() != CargoOperationProcessBean.OPERATION_STATUS3 &&
						coBean.getStatus() != CargoOperationProcessBean.OPERATION_STATUS4 &&
						coBean.getStatus() != CargoOperationProcessBean.OPERATION_STATUS5 &&
						coBean.getStatus() != CargoOperationProcessBean.OPERATION_STATUS6 )||(
						coBean.getEffectStatus() != CargoOperationBean.EFFECT_STATUS0 &&
						coBean.getEffectStatus() != CargoOperationBean.EFFECT_STATUS1 )){
					//特殊原因 这块暂时写死
					String errStatus = "";
					if(coBean.getStatus() == CargoOperationProcessBean.OPERATION_STATUS1){
						errStatus = "未处理";
					}else if(coBean.getStatus() == CargoOperationProcessBean.OPERATION_STATUS7){
						errStatus = "已完成"; //7这是个时效状态 提示改成已完成
					}else if(coBean.getStatus() == CargoOperationProcessBean.OPERATION_STATUS8){
						errStatus = "作业成功";
					}else if(coBean.getStatus() == CargoOperationProcessBean.OPERATION_STATUS9){
						errStatus = "作业失败";
					}else if(coBean.getEffectStatus() == CargoOperationBean.EFFECT_STATUS2){
						errStatus = "复核中(时效)";
					}else if(coBean.getEffectStatus() == CargoOperationBean.EFFECT_STATUS3){
						errStatus = "作业成功(时效)";
					}else if(coBean.getEffectStatus() == CargoOperationBean.EFFECT_STATUS4){
						errStatus = "作业失败(时效)";
					}
					return returnError("该退货上架单的状态[" + errStatus + "]不正确!");
				}
				@SuppressWarnings("unchecked")
				List<CargoOperationCargoBean> cocBeanList = cargoService.getCargoOperationCargoList("oper_id=" + coBean.getId() + " and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE, -1, -1, null);
				if(cocBeanList == null || cocBeanList.isEmpty()){
					return returnError("找不到该退货上架单的目的货位信息!");
				}	
				if(!pdaCheckWholeOperation(cargoService, cocBeanList, area)){
					return returnError("退货上架单的目的货位归属与选择的仓库不一致!");
				}
				wareService.getDbOp().startTransaction(); //开启事务
				//判断货位是否是该退货装箱单的目的货位
				int flag = pdaCheckInWholeOperation(cocBeanList, wholeCode, group,cargoService,area);
				if (flag == 0) {
					wareService.getDbOp().rollbackTransaction();
					return returnError("扫描的货位与退货装箱单的目的货位不一致!");
				} else if (flag == 1) {
					//货位一致
				} else if (flag == 2) {
					//货位不一致，但所选货位可使用
					for(CargoOperationCargoBean bean : cocBeanList){
						CargoInfoBean ci = cargoService.getCargoInfo("whole_code='" + wholeCode+ "'");
						if (ci.getStatus() == CargoInfoBean.STATUS1) {
							if (!cargoService.updateCargoInfo("status=" + CargoInfoBean.STATUS0, "id=" + ci.getId())) {
								wareService.getDbOp().rollbackTransaction();
								return returnError("更换货位失败!");
							}
						}
						if (!cargoService.updateCargoOperation("stock_in_type=" + ci.getStoreType(), "id=" + coBean.getId())) {
							wareService.getDbOp().rollbackTransaction();
							return returnError("更换货位失败!");
						}
						CargoProductStockBean cpsbIn = cargoService.getCargoProductStock("product_id = " + bean.getProductId()+ " and cargo_id = " + ci.getId() );
						if (cpsbIn == null) {
							cpsbIn = new CargoProductStockBean();
							cpsbIn.setProductId(bean.getProductId());
							cpsbIn.setCargoId(ci.getId());
							cpsbIn.setStockCount(0);
							cpsbIn.setStockLockCount(0);
							if (!cargoService.addCargoProductStock(cpsbIn)) {
								wareService.getDbOp().rollbackTransaction();
								return returnError("添加新目的货位库存记录出错!");
							}
							cpsbIn.setId(cargoService.getDbOp().getLastInsertId());
						}
						if (!cargoService.updateCargoOperationCargo("in_cargo_product_stock_id=" + cpsbIn.getId() + ", in_cargo_whole_code='" + wholeCode + "'", "id=" + bean.getId())) {
							wareService.getDbOp().rollbackTransaction();
							return returnError("更换货位失败!");
						}
						CargoOperationLogBean logBean2=new CargoOperationLogBean();
						logBean2.setOperId(coBean.getId());
						logBean2.setOperDatetime(DateUtil.getNow());
						logBean2.setOperAdminId(user.getId());
						logBean2.setOperAdminName(user.getUsername());
						StringBuilder logRemark2 = new StringBuilder("退货上架单完成，更换目的货位从[" + bean.getInCargoWholeCode() + "]变为[" + wholeCode+ "]");
						logBean2.setRemark(logRemark2.toString());
						if(!cargoService.addCargoOperationLog(logBean2)){
							wareService.getDbOp().rollbackTransaction();
							return returnError("更换货位添加审核日志失败");
						}
					}
					cocBeanList = cargoService.getCargoOperationCargoList("oper_id=" + coBean.getId() + " and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE, -1, -1, null);
					if(cocBeanList == null || cocBeanList.isEmpty()){
						wareService.getDbOp().rollbackTransaction();
						return returnError("找不到该退货上架单的目的货位信息!");
					}	
				} else {
					wareService.getDbOp().rollbackTransaction();
					return returnError("扫描的货位与退货装箱单的目的货位不一致!");
				}
				List<voProduct> retProductList = new ArrayList<voProduct>();
				for(CargoOperationCargoBean bean : cocBeanList){
					voProduct voProduct = wareService.getProduct(bean.getProductId());
					voProduct.setCount(bean.getStockCount());
					retProductList.add(voProduct);
				}
				StringBuilder strPId = new StringBuilder();
				strPId.append("(");
				voProduct rp = null;
				for(int j=0; j<retProductList.size(); j++){
					rp = (voProduct) retProductList.get(j);
					if(j>0){
						strPId.append(",");
					}
					strPId.append(rp.getId());
				}
				strPId.append(")");
				@SuppressWarnings("rawtypes")
				List cargoOpList = new ArrayList();
				if(strPId.length()>0){
					cargoOpList = service.getCargoOperationCargoList(
						"oper_id ="+ coBean.getId() + 
						" and product_id in " + strPId.toString()+
						" and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE,0, -1, null);
				}
				// 更新货位库存完成商品数量，减少源货位锁定量，目的货位空间锁定量，目的货位库存，减少库存锁定量
				ReturnedPackageService rpService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
				String strFlag = rpService.updateStockInfoByProductList(retProductList, service, wareService, productservice, cargoOpList, user);
				String result = null;
				if (strFlag != null) {
					wareService.getDbOp().rollbackTransaction();
				    return returnError(result);
				}
				
				cargoOpList = service.getCargoOperationCargoList(
						"oper_id ="+ coBean.getId() + 
						" and product_id in " + strPId.toString()+
						" and type="+CargoOperationCargoBean.COC_WITH_INCARGOINFO_TYPE,0, -1, null);
				// 检测商品完成数量和库存数量是否一致，如果一致那么修改作业单状态为已完成
				for(int k=0; k<cargoOpList.size(); k++){
					CargoOperationCargoBean cocBean = (CargoOperationCargoBean) cargoOpList.get(k);
					//检测商品完成数量和库存数量是否一致，如果一致那么修改作业单状态为已完成
					if(cocBean.getCompleteCount()==cocBean.getStockCount()){
						if(!service.updateCargoOperation(
								"status=" + CargoOperationProcessBean.OPERATION_STATUS7
								+ ",complete_datetime='" + DateUtil.getNow() + "'"
								+ ",complete_user_name='" + user.getUsername() + "'"
								+ ",complete_user_id=" + user.getId(), 
								"id=" + cocBean.getOperId())){
							wareService.getDbOp().rollbackTransaction();
							return returnError("更新作业完成状态失败!");
						}
						CargoOperationLogBean logBean = new CargoOperationLogBean();
						logBean.setOperId(cocBean.getOperId());
						logBean.setOperDatetime(DateUtil.getNow());
						logBean.setOperAdminId(user.getId());
						logBean.setOperAdminName(user.getUsername());
						logBean.setRemark("作业单操作确认完成");
						if(!service.addCargoOperationLog(logBean)){
							wareService.getDbOp().rollbackTransaction();
							return returnError("添加操作记录失败!");
						}
					}else{
						CargoOperationLogBean logBean = new CargoOperationLogBean();
						logBean.setOperId(cocBean.getOperId());
						logBean.setOperDatetime(DateUtil.getNow());
						logBean.setOperAdminId(user.getId());
						logBean.setOperAdminName(user.getUsername());
						logBean.setRemark("作业单操作确认进行中");
						if(!service.addCargoOperationLog(logBean)){
							wareService.getDbOp().rollbackTransaction();
							return returnError("添加操作记录失败!");
						}
					}
				}
				wareService.getDbOp().commitTransaction(); //提交事务
				return returnResult("退货上架单完成!");
			}else{
				return returnError("身份验证失败!"); 
			}
		}catch(Exception e){
			wareService.getDbOp().rollbackTransaction();
			e.printStackTrace();
			return returnError("操作失败,系统异常！");
		}finally{
			wareService.releaseAll();
		}
	}
	/**
	 * 说明：生成退货上架汇总单
	 * 
	 * 时间：2013-08-30
	 * 
	 * 作者：石远飞
	 */
	@RequestMapping("/generateRetUpShelf")
	@ResponseBody
	public JsonModel generateRetUpShelf(HttpServletRequest request){
		JsonModel json =  new JsonModel();
		WareService wareService = new WareService();
		ReturnedPackageService pService = new ReturnedPackageServiceImpl(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			synchronized (cargoLock) {
				//从流中读取json数据
				json = ReceiveJson.receiveJson(request);
				if(json==null){
					return returnError("没有收到请求数据!");
				}
				//验证用户名密码
				if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
					voUser user = (voUser)request.getSession().getAttribute("userView");
					UserGroupBean group = user.getGroup();
					if( !group.isFlag(616)) {
						return returnError("您没有生成退货上架汇总单的权限!");
					}
					int area = StringUtil.toInt(json.getArea());//地区id
					if(area == -1){
						return returnError("没有收到选择的地区!");
					}
					@SuppressWarnings("unchecked")
					List<String> codes = (ArrayList<String>)json.getData().get("code");
					if(codes.size() == 0){
						return returnError("没有可以汇总的上架单!");
					}
					if(codes.size() < 5){
						return returnError("汇总单中至少要包含5个退货上架单!");
					}
					if(codes.size() > 30){
						return returnError("汇总单中最多包含30个退货上架单!");
					}
					Map<String,String> retUpShelfMap = new HashMap<String, String>();
					for(String retUpShelfCode : codes){
						if(!retUpShelfCode.contains("HWTS")){
							return returnError(retUpShelfCode + "该条码不是退货上架单条码!");
						}
						CargoOperationBean cocBean = cargoService.getCargoOperation("code='"+StringUtil.toSql(retUpShelfCode)+"'");
						if(cocBean == null){
							return returnError(retUpShelfCode + "该退货上架单不存在!");
						}
						
						CargoOperationCargoBean cargoOpBean = cargoService.getCargoOperationCargo("oper_id="+cocBean.getId()+" and type=0");
						CargoInfoBean inCi = cargoService.getCargoInfo("whole_code='"+cargoOpBean.getInCargoWholeCode()+"'");
						CargoInfoAreaBean ciaBean = cargoService.getCargoInfoArea("old_id="+area);
						if(inCi.getAreaId()!=ciaBean.getId()){
							return returnError(retUpShelfCode + "您汇总的退货上架单(目的)不属于该地区!");
						}
						
						CargoInfoBean outCi = cargoService.getCargoInfo("whole_code='"+cargoOpBean.getOutCargoWholeCode()+"'");
						if(outCi.getAreaId()!=ciaBean.getId()){
							return returnError(retUpShelfCode + "您汇总的退货上架单(源)不属于该地区!");
						}
						
						if(cocBean.getEffectStatus()==CargoOperationBean.EFFECT_STATUS4){
							return returnError(retUpShelfCode + "该退货上架单已作业失败!");
						}
						
						if(cocBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS2){
							
							if(cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS1){
								return returnError(retUpShelfCode + "该退货上架单还没有确认提交!");
							}
							
							if(cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS3
									|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS4
									|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS5
									|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS6){
								return returnError(retUpShelfCode + "该退货上架单已汇总!");
							}
							
							if(cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS7
									|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS8
									|| cocBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS9){
								return returnError(retUpShelfCode + "该退货上架单已完成!");
							}
						}
						retUpShelfMap.put(retUpShelfCode, retUpShelfCode);
					}
					String upShelfCode = pService.generateUpShelf(retUpShelfMap, user, date);
					if(upShelfCode != null && !upShelfCode.equals("") && !upShelfCode.contains("HWTS")){
						return returnError(upShelfCode);
					}
					return returnResult("该退货上架汇总单审核成功!");
				}else{
					return returnError("身份验证失败!"); 
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return returnError("操作失败,系统异常！");
		}finally{
			pService.releaseAll();
		}
	}
	/**
	 * 说明：作业审核
	 * 
	 * 时间：2013-08-09
	 * 
	 * 作者：石远飞
	 */
	@RequestMapping("/operationAudit")
	@ResponseBody
	public JsonModel operationAudit(HttpServletRequest request){
		voUser user = null;
		JsonModel json =  new JsonModel();
		ReturnedPackageService service = new ReturnedPackageServiceImpl();
		try{
			synchronized (cargoLock) {
				//从流中读取json数据
				json = ReceiveJson.receiveJson(request);
				if(json==null){
					return returnError("没有收到请求数据!");
				}
				//验证用户名密码
				if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
					user = (voUser)request.getSession().getAttribute("userView");
					String retShelfCode = StringUtil.convertNull((String)json.getData().get("code"));
					if(retShelfCode == null || retShelfCode.equals("")){
						return returnError("该汇总单号不存在!");
					}
					int area = StringUtil.toInt(json.getArea());//地区id
					if(area == -1){
						return returnError("没有收到选择的地区!");
					}
					if(service.pdaCheckCollectBill(request, retShelfCode, area)){
						return returnError("上架单的货位归属与选择的仓库不一致!");
					}
					String result = service.confirmRetShelf(retShelfCode,user);
					if(result.equals("1")){
						return returnError("该退货上架汇总单确认失败，请重新扫描确认!");
					}else if(result.equals("2")){
						return returnError("该退货上架汇总单已审核过!");
					}else if(result.equals("3")){
						return returnError("退货上架汇总单条码不能为空!");
					}else if(result.equals("4")){
						return returnError("该退货上架汇总单不存在!");
					}else if(result.equals("5")){
						return returnError("该退货上架汇总单已作业完成!");
					}
					return returnResult("该退货上架汇总单审核成功!");
				}else{
					return returnError("身份验证失败!"); 
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			return returnError("操作失败,系统异常！");
		} finally {
			service.releaseAll();
		}
	}
	/**
	 * 说明：交接作业完成
	 * 
	 * 时间：2013-08-09
	 * 
	 * 作者：石远飞
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/operationComplete")
	@ResponseBody
	public JsonModel operationComplete(HttpServletRequest request){
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp);
		CartonningInfoService CIService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService prudoctService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ConsignmentService consignmentService = new ConsignmentService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		voUser user = null;
		JsonModel json =  new JsonModel();
		try{
			synchronized (cargoLock) {
				service.getDbOp().startTransaction();    //开启事务
				FinanceBaseDataService fbdSevice = 
						FinanceBaseDataServiceFactory.constructFinanceBaseDataService(
								FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN, service.getDbOp().getConn());
				List<BaseProductInfo> baseList = null;
				BaseProductInfo baseProductInfo = null;
				//从流中读取json数据
				json = ReceiveJson.receiveJson(request);
				if(json==null){
					return returnError("没有收到请求数据!");
				}
				//验证用户名密码
				if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
					user = (voUser)request.getSession().getAttribute("userView");
					String cartonningCode = StringUtil.convertNull((String)json.getData().get("cartonningCode"));
					String cargoCode = StringUtil.convertNull((String)json.getData().get("cargoCode"));
					int fg = (Integer)json.getData().get("flag");
					int area = StringUtil.toInt(json.getArea());//地区id
					if(area == -1){
						return returnError("没有收到选择的地区!");
					}
					service.getDbOp().startTransaction();
					CartonningInfoBean cartonningInfo=null;
					CargoOperationBean cargoOperation=null;
					String inCiCode = null;
					if(!cartonningCode.equals("")){//装箱单号
						cartonningInfo=CIService.getCartonningInfo("code='"+cartonningCode+"'");
						if(cartonningInfo==null){
							return returnError("装箱单号不正确!");
						}
						CargoInfoBean cargoInfo=service.getCargoInfo("id="+cartonningInfo.getCargoId());
						if(cargoInfo==null){
							return returnError("装箱单未关联货位!");
						}
						if(area != cargoInfo.getAreaId()){
							return returnError("装箱单关联货位不属于选择的仓库!");
						}
						cargoOperation=service.getCargoOperation("id="+cartonningInfo.getOperId());
						if(cargoOperation==null){
							return returnError("装箱单未关联作业单!");
						}
						CargoOperationCargoBean coc=service.getCargoOperationCargo("oper_id="+cartonningInfo.getOperId()+" and in_cargo_product_stock_id>0");
						if(coc==null){
							return returnError("作业单信息错误!");
						}
						inCiCode = coc.getInCargoWholeCode();
					}
					if(!cargoCode.equals("")){//扫描了装箱单号和货位号
						if(fg == 1){
							if(!cargoCode.equalsIgnoreCase(inCiCode)){
								return returnError("货位不一致!",2);
							}
						}
						CargoInfoBean cargoInfo=service.getCargoInfo("whole_code='"+cargoCode+"'");//扫描的目的货位
						if(cargoInfo==null){
							return returnError("该货位不存在!");
						}
						if(area != cargoInfo.getAreaId()){
							return returnError("该货位不属于选择的仓库!");
						}
						//判断货位与装箱单关联作业单的目的货位是否一致
						cargoOperation=service.getCargoOperation("id="+cartonningInfo.getOperId());//作业单
						if(cargoOperation==null){
							return returnError("装箱单未关联作业单!");
						}
						CargoOperationCargoBean coc=service.getCargoOperationCargo("oper_id="+cartonningInfo.getOperId()+" and in_cargo_product_stock_id>0");
						if(coc==null){
							return returnError("作业单信息错误!");
						}
						if(!cargoCode.equals(coc.getInCargoWholeCode())){//扫描的货位号和作业单目的货位号不同
							//如果是快销商品 不可以随便的选择货位  需要至少是指定区的
							if( consignmentService.isProductConsignment(coc.getProductId()) ) {
								if ( !consignmentService.isCargoStockAreaCodeSub(cargoInfo.getId(), Constants.CONSIGNMENT_STOCK_AREA) ) {
									return returnError("作业单中有快销商品，当前指定的目的货位不是"+Constants.CONSIGNMENT_STOCK_AREA+"区，操作无效！");
								} 
							}
							CargoProductStockBean inCps=service.getCargoProductStock("cargo_id="+cargoInfo.getId()+" and product_id="+coc.getProductId());
							if(inCps==null){//目的货位上没有该商品库存记录，则添加库存记录
								CargoProductStockBean newCps=new CargoProductStockBean();
								newCps.setCargoId(cargoInfo.getId());
								newCps.setProductId(coc.getProductId());
								newCps.setStockCount(0);
								newCps.setStockLockCount(0);
								service.addCargoProductStock(newCps);
								inCps=newCps;
								inCps.setId(service.getDbOp().getLastInsertId());
								if(cargoInfo.getStatus()!=0){
									service.updateCargoInfo("status=0", "id="+cargoInfo.getId());
								}
							}
							service.updateCargoOperationCargo("in_cargo_product_stock_id="+inCps.getId()+",in_cargo_whole_code='"+cargoInfo.getWholeCode()+"'", "oper_id="+cartonningInfo.getOperId()+" and in_cargo_product_stock_id>0");
						}
					}
					int status = cargoOperation.getStatus();
					if(status == 1 || status == 2 || status == 7 ||status == 8 || status == 9
							|| status == 10 || status == 11 || status == 16 || status == 17 || status == 18 
								||status == 19 || status == 20 || status == 25 || status == 26 || status == 27 
									|| status == 28 || status == 29 || status == 34 || status == 35 || status == 36){
						return returnError("作业单状态不正确!");
					}
					int type = cargoOperation.getType();
					int operId = cargoOperation.getId();
					if(type == 0){	//上架
						if(cargoOperation.getStatus()>=CargoOperationProcessBean.OPERATION_STATUS7){
							return returnError("该作业单状态已修改，操作失败!");
						}
							baseList = new ArrayList<BaseProductInfo>();
							List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1", -1, -1, "id asc");
							for(int i=0;i<inCocList.size();i++){
								CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
								CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getOutCargoProductStockId());
								CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getOutCargoWholeCode()+"'");
								voProduct product = wareService.getProduct(inCoc.getProductId());
								product.setPsList(prudoctService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
								int stockOutCount = 0;//货位库存变动
								List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
								for(int j=0;j<outCocList.size();j++){
									CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
									CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
									CargoProductStockBean temp_inCps = service.getCargoProductStock("id = "+outCoc.getInCargoProductStockId());
									if(temp_inCps==null){
				                    	return returnError("商品"+product.getCode()+"货位"+outCoc.getInCargoWholeCode()+"已被清空或被其他商品占用，操作失败!");
									}
									CargoInfoBean outCi = service.getCargoInfo("id = "+temp_inCps.getCargoId());
									if(!service.updateCargoProductStockCount(temp_inCps.getId(), outCoc.getStockCount())){
				                    	service.getDbOp().rollbackTransaction();
				                    	return returnError("货位库存操作失败，货位冻结库存不足!");
				                    }
									if(!service.updateCargoProductStockLockCount(outCps.getId(), -outCoc.getStockCount())){
				                    	service.getDbOp().rollbackTransaction();
				                    	return returnError("货位库存操作失败，货位冻结库存不足!");
				                    }
									//调整合格库库存
									if(outCi.getAreaId()!=inCi.getAreaId()||outCi.getStockType()!=inCi.getStockType()){
										CargoInfoAreaBean cargoInfoArea1=service.getCargoInfoArea("id="+outCi.getAreaId());//目的货位地区
										CargoInfoAreaBean cargoInfoArea2=service.getCargoInfoArea("id="+inCi.getAreaId());//源货位地区
										
										ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea1.getOldId()+" and type="+outCi.getStockType());
										ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea2.getOldId()+" and type="+inCi.getStockType());
										
										if(psIn==null){
											service.getDbOp().rollbackTransaction();
											return returnError("合格库库存数据错误!");
										}
										if (!psService.updateProductStockCount(psIn.getId(),outCoc.getStockCount())) {
											service.getDbOp().rollbackTransaction();
											return returnError("库存操作失败，可能是库存不足，请与管理员联系!");
										}
										if(psOut==null){
											service.getDbOp().rollbackTransaction();
											return returnError("合格库库存数据错误!");
										}
										if (!psService.updateProductLockCount(psOut.getId(),-outCoc.getStockCount())) {
											service.getDbOp().rollbackTransaction();
											return returnError("库存操作失败，可能是库存不足，请与管理员联系!");
										}
										
										baseProductInfo = new BaseProductInfo();
										baseProductInfo.setId(outCoc.getProductId());
										//出库
										baseProductInfo.setProductStockOutId(psOut.getId());
										//入库
										baseProductInfo.setProductStockId(psIn.getId());
										baseProductInfo.setOutCount(outCoc.getStockCount());
										baseList.add(baseProductInfo);
										//批次修改开始
										//更新批次记录、添加调拨出、入库批次记录
//										List sbList = stockService.getStockBatchList("product_id="+outCoc.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId(), -1, -1, "id asc");
//										double stockinPrice = 0;
//										double stockoutPrice = 0;
//										if(sbList!=null&&sbList.size()!=0){
//											int stockExchangeCount = inCoc.getStockCount();
//											int index = 0;
//											int stockBatchCount = 0;
//											do {//出库
//												StockBatchBean batch = (StockBatchBean)sbList.get(index);
//												if(stockExchangeCount>=batch.getBatchCount()){
//													if(!stockService.deleteStockBatch("id="+batch.getId())){
//										                service.getDbOp().rollbackTransaction();
//										                return returnError("数据库操作失败!");
//													}
//													stockBatchCount = batch.getBatchCount();
//												}else{
//													if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
//										                service.getDbOp().rollbackTransaction();
//										                return returnError("数据库操作失败!");
//													}
//													stockBatchCount = stockExchangeCount;
//												}
//												StockBatchLogBean batchLog = new StockBatchLogBean();//添加批次操作记录
//												batchLog.setCode(cargoOperation.getCode());
//												batchLog.setStockType(batch.getStockType());
//												batchLog.setStockArea(batch.getStockArea());
//												batchLog.setBatchCode(batch.getCode());
//												batchLog.setBatchCount(stockBatchCount);
//												batchLog.setBatchPrice(batch.getPrice());
//												batchLog.setProductId(batch.getProductId());
//												batchLog.setRemark("调拨出库");
//												batchLog.setCreateDatetime(DateUtil.getNow());
//												batchLog.setUserId(user.getId());
//												batchLog.setSupplierId(batch.getSupplierId());
//												batchLog.setTax(batch.getTax());
//												if(!stockService.addStockBatchLog(batchLog)){
//										             service.getDbOp().rollbackTransaction();
//										             return returnError("添加失败!");
//												}
//												
//												stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//												//入库
//												StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+outCi.getStockType()+" and stock_area="+outCi.getAreaId());
//												if(batchBean!=null){
//													if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
//										                service.getDbOp().rollbackTransaction();
//										                return returnError("数据库操作失败!");
//													}
//												}else{
//													StockBatchBean newBatch = new StockBatchBean();
//													newBatch.setCode(batch.getCode());
//													newBatch.setProductId(batch.getProductId());
//													newBatch.setPrice(batch.getPrice());
//													newBatch.setBatchCount(stockBatchCount);
//													newBatch.setProductStockId(psIn.getId());
//													newBatch.setStockArea(outCi.getAreaId());
//													newBatch.setStockType(psIn.getType());
//													newBatch.setSupplierId(batch.getSupplierId());
//													newBatch.setTax(batch.getTax());
//													newBatch.setNotaxPrice(batch.getNotaxPrice());
//													newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
//													if(!stockService.addStockBatch(newBatch)){
//														service.getDbOp().rollbackTransaction();
//														return returnError("添加失败!");
//													}
//												}
//												batchLog = new StockBatchLogBean();//添加批次操作记录
//												batchLog.setCode(cargoOperation.getCode());
//												batchLog.setStockType(psIn.getType());
//												batchLog.setStockArea(outCi.getAreaId());
//												batchLog.setBatchCode(batch.getCode());
//												batchLog.setBatchCount(stockBatchCount);
//												batchLog.setBatchPrice(batch.getPrice());
//												batchLog.setProductId(batch.getProductId());
//												batchLog.setRemark("调拨入库");
//												batchLog.setCreateDatetime(DateUtil.getNow());
//												batchLog.setUserId(user.getId());
//												batchLog.setSupplierId(batch.getSupplierId());
//												batchLog.setTax(batch.getTax());
//												if(!stockService.addStockBatchLog(batchLog)){
//													service.getDbOp().rollbackTransaction();
//													return returnError("添加失败!");
//												}
//												stockExchangeCount -= batch.getBatchCount();
//												index++;
//												
//												stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//												double stockInPriceSum = (new BigDecimal(stockBatchCount)).multiply(
//														new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue();
//												int allStock = product.getStockAll() + product.getLockCountAll();
//												double allStockPriceSum = (new BigDecimal(allStock)).multiply(
//														new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue();
//												double tax = batch.getTax();
//												
//												// 添加财务进销存入库卡片
//												FinanceStockCardBean fsc = new FinanceStockCardBean();
//												fsc.setCardType(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//												fsc.setCode(cargoOperation.getCode());
//												fsc.setCreateDatetime(DateUtil.getNow());
//												fsc.setStockType(outCi.getStockType());
//												fsc.setStockArea(outCi.getAreaId());
//												fsc.setProductId(inCps.getProductId());
//												fsc.setStockId(psIn.getId());
//												fsc.setStockInCount(stockBatchCount);
//												fsc.setStockInPriceSum(Arith.mul(stockInPriceSum, 1 + tax));
//												fsc.setCurrentStock(product.getStock(outCi.getAreaId(), 
//														outCi.getStockType()) + product.getLockCount(outCi.getStockAreaId(), outCi.getStockType()));
//												fsc.setStockAllArea(product.getStock(outCi.getAreaId()) + product.getLockCount(outCi.getAreaId()));
//												fsc.setStockAllType(product.getStockAllType(outCi.getStockType()) + product.getLockCountAllType(outCi.getStockType()));
//												fsc.setAllStock(allStock);
//												fsc.setStockPrice(new BigDecimal(Arith.mul(product.getPrice5(), 1 + tax)).floatValue());// 新的库存价格
//												fsc.setAllStockPriceSum(Arith.mul(allStockPriceSum, 1 + tax));
//												fsc.setType(FinanceStockCardBean.TYPE_STOCKEXCHANGEIN3);
//												fsc.setTax(tax);
//												fsc.setStockBatchCode(batch.getCode());
//												fsc.setBalanceMode(0);
//												fsc.setIsTicket(0);
//												fsc.setBalanceModeStockCount(0);
//												fsc.setBalanceModeStockPrice(0);
//												fsc.setSupplierId(batch.getSupplierId());
//												psService.addFinanceStockCard(fsc);
//												
//												// 添加财务进销存出库卡片
//												FinanceStockCardBean fsc2 = new FinanceStockCardBean();
//												fsc2.setCardType(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
//												fsc2.setCode(cargoOperation.getCode());
//												fsc2.setCreateDatetime(DateUtil.getNow());
//												fsc2.setStockType(inCi.getStockType());
//												fsc2.setStockArea(inCi.getAreaId());
//												fsc2.setProductId(product.getId());
//												fsc2.setStockId(psOut.getId());
//												fsc2.setStockInCount(stockBatchCount);
//												fsc2.setStockInPriceSum(Arith.mul(stockInPriceSum, 1 + tax));
//												fsc2.setCurrentStock(product.getStock(inCi.getAreaId(), 
//														inCi.getStockType()) + product.getLockCount(inCi.getStockAreaId(), inCi.getStockType()));
//												fsc2.setStockAllArea(product.getStock(inCi.getAreaId()) + product.getLockCount(inCi.getAreaId()));
//												fsc2.setStockAllType(product.getStockAllType(inCi.getStockType()) + product.getLockCountAllType(inCi.getStockType()));
//												fsc2.setAllStock(allStock);
//												fsc2.setStockPrice(new BigDecimal(Arith.mul(product.getPrice5(), 1 + tax)).floatValue());// 新的库存价格
//												fsc2.setAllStockPriceSum(Arith.mul(allStockPriceSum, 1 + tax));
//												fsc2.setType(FinanceStockCardBean.TYPE_STOCKEXCHANGEOUT4);
//												fsc2.setTax(tax);
//												fsc2.setStockBatchCode(batch.getCode());
//												fsc2.setBalanceMode(0);
//												fsc2.setIsTicket(0);
//												fsc2.setBalanceModeStockCount(0);
//												fsc2.setBalanceModeStockPrice(0);
//												fsc2.setSupplierId(batch.getSupplierId());
//												psService.addFinanceStockCard(fsc2);
//											} while (stockExchangeCount>0&&index<sbList.size());
//										}
										StockCardBean sc = new StockCardBean();// 入库卡片\添加进销存卡片开始\批次修改结束
										sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
										sc.setCode(cargoOperation.getCode());
										sc.setCreateDatetime(DateUtil.getNow());
										sc.setStockType(outCi.getStockType());
										sc.setStockArea(outCi.getAreaId());
										sc.setProductId(inCps.getProductId());
										sc.setStockId(psIn.getId());
										sc.setStockInCount(inCoc.getStockCount());
										sc.setStockInPriceSum((new BigDecimal(inCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
										sc.setCurrentStock(product.getStock(outCi.getAreaId(), sc.getStockType())+ product.getLockCount(outCi.getStockAreaId(), sc.getStockType()));
										sc.setStockAllArea(product.getStock(outCi.getAreaId())+ product.getLockCount(outCi.getAreaId()));
										sc.setStockAllType(product.getStockAllType(sc.getStockType())+ product.getLockCountAllType(sc.getStockType()));
										sc.setAllStock(product.getStockAll() + product.getLockCountAll());
										sc.setStockPrice(product.getPrice5());// 新的库存价格
										sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
												new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
										psService.addStockCard(sc);
										
										StockCardBean sc2 = new StockCardBean();// 出库卡片
										sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
										sc2.setCode(cargoOperation.getCode());
										sc2.setCreateDatetime(DateUtil.getNow());
										sc2.setStockType(inCi.getStockType());
										sc2.setStockArea(inCi.getAreaId());
										sc2.setProductId(product.getId());
										sc2.setStockId(psOut.getId());
										sc2.setStockOutCount(inCoc.getStockCount());
										sc2.setStockOutPriceSum((new BigDecimal(inCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
										sc2.setCurrentStock(product.getStock(inCi.getAreaId(), sc2.getStockType())+ product.getLockCount(inCi.getAreaId(), sc2.getStockType()));
										sc2.setStockAllArea(product.getStock(inCi.getAreaId())+ product.getLockCount(inCi.getAreaId()));
										sc2.setStockAllType(product.getStockAllType(sc2.getStockType())+ product.getLockCountAllType(sc2.getStockType()));
										sc2.setAllStock(product.getStockAll() + product.getLockCountAll());
										sc2.setStockPrice(product.getPrice5());
										sc2.setAllStockPriceSum((new BigDecimal(sc2.getAllStock())).multiply(
												new BigDecimal(StringUtil.formatDouble2(sc2.getStockPrice()))).doubleValue());
										psService.addStockCard(sc2);
										//添加进销存卡片结束
									}
									temp_inCps = service.getCargoProductStock("id = "+temp_inCps.getId());	//货位入库卡片
									CargoStockCardBean csc = new CargoStockCardBean();
									csc.setCardType(CargoStockCardBean.CARDTYPE_UPSHELFSTOCKIN);
									csc.setCode(cargoOperation.getCode());
									csc.setCreateDatetime(DateUtil.getNow());
									csc.setStockType(outCi.getStockType());
									csc.setStockArea(outCi.getAreaId());
									csc.setProductId(product.getId());
									csc.setStockId(temp_inCps.getId());
									csc.setStockInCount(outCoc.getStockCount());
									csc.setStockInPriceSum((new BigDecimal(outCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
									csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
									csc.setAllStock(product.getStockAll() + product.getLockCountAll());
									csc.setCurrentCargoStock(temp_inCps.getStockCount()+temp_inCps.getStockLockCount());
									csc.setCargoStoreType(outCi.getStoreType());
									csc.setCargoWholeCode(outCi.getWholeCode());
									csc.setStockPrice(product.getPrice5());
									csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
									service.addCargoStockCard(csc);
									stockOutCount = stockOutCount + outCoc.getStockCount();
									
									//合格库待作业任务处理
									List cartonningList=CIService.getCartonningList("oper_id="+operId+" and status<>2", -1, -1, null);
									for(int c=0;c<cartonningList.size();c++){
										CartonningInfoBean cartonning=(CartonningInfoBean)cartonningList.get(c);
										CartonningProductInfoBean cartonningProduct=CIService.getCartonningProductInfo("cartonning_id="+cartonning.getId());
										if(cartonningProduct==null){
											continue;
										}
										if(cartonningProduct.getProductId()==outCoc.getProductId()){
											CargoOperationTodoBean cot=CIService.getCargoOperationTodo("product_id="+cartonning.getId()+" and status in(0,1,2) and type=0");
											if(cot!=null){
												CIService.updateCargoOperationTodo("status=3", "id="+cot.getId());
											}
										}
									}
									
								}
								inCps = service.getCargoProductStock("id = "+inCps.getId());//货位出库卡片
								CargoStockCardBean csc = new CargoStockCardBean();
								csc.setCardType(CargoStockCardBean.CARDTYPE_UPSHELFSTOCKOUT);
								csc.setCode(cargoOperation.getCode());
								csc.setCreateDatetime(DateUtil.getNow());
								csc.setStockType(inCi.getStockType());
								csc.setStockArea(inCi.getAreaId());
								csc.setProductId(product.getId());
								csc.setStockId(inCps.getId());
								csc.setStockOutCount(stockOutCount);
								csc.setStockOutPriceSum((new BigDecimal(stockOutCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
								csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
								csc.setAllStock(product.getStockAll() + product.getLockCountAll());
								csc.setCurrentCargoStock(inCps.getStockCount()+inCps.getStockLockCount());
								csc.setCargoStoreType(inCi.getStoreType());
								csc.setCargoWholeCode(inCi.getWholeCode());
								csc.setStockPrice(product.getPrice5());
								csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
								service.addCargoStockCard(csc);
							}
						CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
						CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS7);//下个阶段
						if(process==null){
							service.getDbOp().rollbackTransaction();
			    			return returnError("作业单流程信息错误!");
						}
						if(process2==null){
							service.getDbOp().rollbackTransaction();
			    			return returnError("作业单流程信息错误!");
						}
						//修改上一操作日志的时效
						CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
						if(lastLog != null && lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
							int effectTime=process.getEffectTime();//上阶段时效
							String lastOperateTime=lastLog.getOperDatetime();
							SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							long date1=sdf.parse(lastOperateTime).getTime();
							long date2=sdf.parse(DateUtil.getNow()).getTime();
							if(date1+effectTime*60*1000<date2){//已超时
								service.updateCargoOperLog("effect_time=2", "id="+lastLog.getId());
							}
						}
						if(!service.updateCargoOperation(
								"status="+CargoOperationProcessBean.OPERATION_STATUS8+"" +
										",effect_status="+CargoOperationBean.EFFECT_STATUS2+"" +
												",last_operate_datetime='"+DateUtil.getNow()+"'"+"" +
														",complete_datetime='"+DateUtil.getNow()+"'" +
																",complete_user_id="+user.getId()+"" +
																		",complete_user_name='"+user.getUsername()+"'", "id="+operId)){
							service.getDbOp().rollbackTransaction();
			    			return returnError("更新上架单状态失败!");
						}
						CargoOperLogBean operLog=new CargoOperLogBean();
						operLog.setOperName(process2.getOperName());
						operLog.setOperDatetime(DateUtil.getNow());
						operLog.setOperAdminId(user.getId());
						operLog.setOperAdminName(user.getUsername());
						operLog.setHandlerCode("");
						operLog.setEffectTime(2);
						operLog.setRemark("");
						operLog.setOperCode(cargoOperation.getCode());
						operLog.setPreStatusName(process.getStatusName());
						operLog.setNextStatusName(process2.getStatusName());
						service.addCargoOperLog(operLog);
						
						//修改相关调拨单关联的货位，改为关联目的货位
						CartonningInfoBean cartonning=CIService.getCartonningInfo("oper_id="+operId);
						if(cartonning!=null){
							CargoOperationCargoBean coc=service.getCargoOperationCargo("oper_id="+operId+" and in_cargo_product_stock_id>0");
							CargoInfoBean cargo=service.getCargoInfo("whole_code='"+coc.getInCargoWholeCode()+"'");
							if(cargo!=null){
								if(!CIService.updateCartonningInfo("cargo_id="+cargo.getId(), "id="+cartonning.getId())){
									service.getDbOp().rollbackTransaction();
					    			return returnError("数据库操作失败!");
								}
							}
						}
						//作废此装箱单
						if(!CIService.updateCartonningInfo(" status=2", " id=" + cartonningInfo.getId())){
							service.getDbOp().rollbackTransaction();
							return returnError("作废装箱单操作失败!");
						}
						//物流员工绩效考核操作
						CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
						CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningInfo.getId());
						ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
						CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
						if(csBean == null){
							service.getDbOp().rollbackTransaction();
							return returnError("此账号不是物流员工!");
						}
			    		CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and type=5 and staff_id=" + csBean.getId() );
			    		int operCount = 1;
			    		int performanceProductCount = cartonningProduct.getProductCount();
			    		if(cspBean != null){
			    			performanceProductCount = performanceProductCount + cspBean.getProductCount();
			    			operCount = operCount + cspBean.getOperCount();
							boolean flag = cargoService.updateCargoStaffPerformance(" oper_count=" + operCount + ", product_count=" + performanceProductCount, " id=" + cspBean.getId());
							if(!flag){
								service.getDbOp().rollbackTransaction();
								return returnError("物流员工绩效考核更新操作失败!");
							}
						}else{
							CargoStaffPerformanceBean newBean = new CargoStaffPerformanceBean();
							newBean.setDate(date);
							newBean.setProductCount(performanceProductCount);
							newBean.setOperCount(operCount);
							newBean.setStaffId(csBean.getId());
							newBean.setType(5);  //5代表完成上架作业
							boolean flag = cargoService.addCargoStaffPerformance(newBean);
							if(!flag){
								service.getDbOp().rollbackTransaction();
								return returnError("物流员工绩效考核添加操作失败!");
							}
						}
					//下架
					}else if(type == 1){
						if(cargoOperation.getStatus()>=CargoOperationProcessBean.OPERATION_STATUS16){
							service.getDbOp().rollbackTransaction();
							return returnError("该作业单未审核通过，操作失败!");
						}
							baseList = new ArrayList<BaseProductInfo>(); 
							List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 0", -1, -1, "id asc");
							for(int i=0;i<inCocList.size();i++){
								CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
								CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getOutCargoProductStockId());
								CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getOutCargoWholeCode()+"'");
								voProduct product = wareService.getProduct(inCoc.getProductId());
								product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
								int stockOutCount = 0;
								List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId()+" and type = 1 and use_status = 1", -1, -1, "id asc");
								for(int j=0;j<outCocList.size();j++){
									CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
									CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
									CargoProductStockBean temp_inCps = service.getCargoProductStock("id = "+outCoc.getInCargoProductStockId());
									CargoInfoBean outCi = service.getCargoInfo("id = "+temp_inCps.getCargoId());
									
									if(!service.updateCargoProductStockCount(temp_inCps.getId(), outCoc.getStockCount())){
				                    	service.getDbOp().rollbackTransaction();
				        				return returnError("货位库存操作失败，货位冻结库存不足!");
				                    }
									if(!service.updateCargoProductStockLockCount(outCps.getId(), -outCoc.getStockCount())){
				                    	service.getDbOp().rollbackTransaction();
				        				return returnError("货位库存操作失败，货位冻结库存不足!");
				                    }
									if(outCi.getAreaId()!=inCi.getAreaId()||outCi.getStockType()!=inCi.getStockType()){//调整合格库库存
										CargoInfoAreaBean cargoInfoArea1=service.getCargoInfoArea("id="+outCi.getAreaId());//目的货位地区
										CargoInfoAreaBean cargoInfoArea2=service.getCargoInfoArea("id="+inCi.getAreaId());//源货位地区
										
										ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea1.getOldId()+" and type="+outCi.getStockType());
										ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea2.getOldId()+" and type="+inCi.getStockType());
										
										if(psIn==null){
											service.getDbOp().rollbackTransaction();
											return returnError("合格库库存数据错误!");
										}
										if (!psService.updateProductStockCount(psIn.getId(),outCoc.getStockCount())) {
											service.getDbOp().rollbackTransaction();
											return returnError("库存操作失败，可能是库存不足，请与管理员联系!");
										}
										if(psOut==null){
											service.getDbOp().rollbackTransaction();
											return returnError("合格库库存数据错误!");
										}
										if (!psService.updateProductLockCount(psOut.getId(),-outCoc.getStockCount())) {
											service.getDbOp().rollbackTransaction();
											return returnError("库存操作失败，可能是库存不足，请与管理员联系!");
										}
										
										//调拨下架财务基础数据
										baseProductInfo = new BaseProductInfo();
										baseProductInfo.setId(outCoc.getProductId());
										//出库
										baseProductInfo.setProductStockOutId(psOut.getId());
										//入库
										baseProductInfo.setProductStockId(psIn.getId());
										baseProductInfo.setOutCount(outCoc.getStockCount());
										baseList.add(baseProductInfo);
										
										//更新批次记录、添加调拨出、入库批次记录 //批次修改开始
//										List sbList = stockService.getStockBatchList("product_id="+outCoc.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId(), -1, -1, "id asc");
//										double stockinPrice = 0;
//										double stockoutPrice = 0;
//										if(sbList!=null&&sbList.size()!=0){
//											int stockExchangeCount = inCoc.getStockCount();
//											int index = 0;
//											int stockBatchCount = 0;
//											do {//出库
//												StockBatchBean batch = (StockBatchBean)sbList.get(index);
//												if(stockExchangeCount>=batch.getBatchCount()){
//													if(!stockService.deleteStockBatch("id="+batch.getId())){
//										                service.getDbOp().rollbackTransaction();
//										                return returnError("数据库操作失败!");
//													}
//													stockBatchCount = batch.getBatchCount();
//												}else{
//													if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
//										                service.getDbOp().rollbackTransaction();
//										                return returnError("数据库操作失败!");
//													}
//													stockBatchCount = stockExchangeCount;
//												}
//												StockBatchLogBean batchLog = new StockBatchLogBean();	//添加批次操作记录
//												batchLog.setCode(cargoOperation.getCode());
//												batchLog.setStockType(batch.getStockType());
//												batchLog.setStockArea(batch.getStockArea());
//												batchLog.setBatchCode(batch.getCode());
//												batchLog.setBatchCount(stockBatchCount);
//												batchLog.setBatchPrice(batch.getPrice());
//												batchLog.setProductId(batch.getProductId());
//												batchLog.setRemark("调拨出库");
//												batchLog.setCreateDatetime(DateUtil.getNow());
//												batchLog.setUserId(user.getId());
//												batchLog.setSupplierId(batch.getSupplierId());
//												batchLog.setTax(batch.getTax());
//												if(!stockService.addStockBatchLog(batchLog)){
//										             service.getDbOp().rollbackTransaction();
//										             return returnError("添加失败!");
//												}
//												stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();//入库
//												StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+outCi.getStockType()+" and stock_area="+outCi.getAreaId());
//												if(batchBean!=null){
//													if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
//										                service.getDbOp().rollbackTransaction();
//										                return returnError("数据库操作失败!");
//													}
//												}else{
//													StockBatchBean newBatch = new StockBatchBean();
//													newBatch.setCode(batch.getCode());
//													newBatch.setProductId(batch.getProductId());
//													newBatch.setPrice(batch.getPrice());
//													newBatch.setBatchCount(stockBatchCount);
//													newBatch.setProductStockId(psIn.getId());
//													newBatch.setStockArea(outCi.getAreaId());
//													newBatch.setStockType(psIn.getType());
//													newBatch.setSupplierId(batch.getSupplierId());
//													newBatch.setTax(batch.getTax());
//													newBatch.setNotaxPrice(batch.getNotaxPrice());
//													newBatch.setSupplierId(batch.getSupplierId());
//													newBatch.setTax(batch.getTax());
//													newBatch.setNotaxPrice(batch.getNotaxPrice());
//													newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
//													if(!stockService.addStockBatch(newBatch)){
//														service.getDbOp().rollbackTransaction();
//														return returnError("添加失败!");
//													}
//												}
//												batchLog = new StockBatchLogBean();//添加批次操作记录
//												batchLog.setCode(cargoOperation.getCode());
//												batchLog.setStockType(psIn.getType());
//												batchLog.setStockArea(outCi.getAreaId());
//												batchLog.setBatchCode(batch.getCode());
//												batchLog.setBatchCount(stockBatchCount);
//												batchLog.setBatchPrice(batch.getPrice());
//												batchLog.setProductId(batch.getProductId());
//												batchLog.setRemark("调拨入库");
//												batchLog.setCreateDatetime(DateUtil.getNow());
//												batchLog.setUserId(user.getId());
//												batchLog.setSupplierId(batch.getSupplierId());
//												batchLog.setTax(batch.getTax());
//												if(!stockService.addStockBatchLog(batchLog)){
//													service.getDbOp().rollbackTransaction();
//													return returnError("添加失败!");
//												}
//												stockExchangeCount -= batch.getBatchCount();
//												index++;
//												stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//												
//												double stockInPriceSum = (new BigDecimal(stockBatchCount)).multiply(
//														new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue();
//												int allStock = product.getStockAll() + product.getLockCountAll();
//												double allStockPriceSum = (new BigDecimal(allStock)).multiply(
//														new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue();
//												double tax = batch.getTax();
//												
//												// 添加财务进销存入库卡片
//												FinanceStockCardBean fsc = new FinanceStockCardBean();
//												fsc.setCardType(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//												fsc.setCode(cargoOperation.getCode());
//												fsc.setCreateDatetime(DateUtil.getNow());
//												fsc.setStockType(outCi.getStockType());
//												fsc.setStockArea(outCi.getAreaId());
//												fsc.setProductId(inCps.getProductId());
//												fsc.setStockId(psIn.getId());
//												fsc.setStockInCount(stockBatchCount);
//												fsc.setStockInPriceSum(Arith.mul(stockInPriceSum, 1 + tax));
//												fsc.setCurrentStock(product.getStock(outCi.getAreaId(), 
//														outCi.getStockType()) + product.getLockCount(outCi.getStockAreaId(), outCi.getStockType()));
//												fsc.setStockAllArea(product.getStock(outCi.getAreaId()) + product.getLockCount(outCi.getAreaId()));
//												fsc.setStockAllType(product.getStockAllType(outCi.getStockType()) + product.getLockCountAllType(outCi.getStockType()));
//												fsc.setAllStock(allStock);
//												fsc.setStockPrice(new BigDecimal(Arith.mul(product.getPrice5(), 1 + tax)).floatValue());// 新的库存价格
//												fsc.setAllStockPriceSum(Arith.mul(allStockPriceSum, 1 + tax));
//												fsc.setType(FinanceStockCardBean.TYPE_STOCKEXCHANGEIN3);
//												fsc.setTax(tax);
//												fsc.setStockBatchCode(batch.getCode());
//												fsc.setBalanceMode(0);
//												fsc.setIsTicket(0);
//												fsc.setBalanceModeStockCount(0);
//												fsc.setBalanceModeStockPrice(0);
//												fsc.setSupplierId(batch.getSupplierId());
//												psService.addFinanceStockCard(fsc);
//												
//												// 添加财务进销存出库卡片
//												FinanceStockCardBean fsc2 = new FinanceStockCardBean();
//												fsc2.setCardType(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
//												fsc2.setCode(cargoOperation.getCode());
//												fsc2.setCreateDatetime(DateUtil.getNow());
//												fsc2.setStockType(inCi.getStockType());
//												fsc2.setStockArea(inCi.getAreaId());
//												fsc2.setProductId(product.getId());
//												fsc2.setStockId(psOut.getId());
//												fsc2.setStockInCount(stockBatchCount);
//												fsc2.setStockInPriceSum(Arith.mul(stockInPriceSum, 1 + tax));
//												fsc2.setCurrentStock(product.getStock(inCi.getAreaId(), 
//														inCi.getStockType()) + product.getLockCount(inCi.getStockAreaId(), inCi.getStockType()));
//												fsc2.setStockAllArea(product.getStock(inCi.getAreaId()) + product.getLockCount(inCi.getAreaId()));
//												fsc2.setStockAllType(product.getStockAllType(inCi.getStockType()) + product.getLockCountAllType(inCi.getStockType()));
//												fsc2.setAllStock(allStock);
//												fsc2.setStockPrice(new BigDecimal(Arith.mul(product.getPrice5(), 1 + tax)).floatValue());// 新的库存价格
//												fsc2.setAllStockPriceSum(Arith.mul(allStockPriceSum, 1 + tax));
//												fsc2.setType(FinanceStockCardBean.TYPE_STOCKEXCHANGEOUT4);
//												fsc2.setTax(tax);
//												fsc2.setStockBatchCode(batch.getCode());
//												fsc2.setBalanceMode(0);
//												fsc2.setIsTicket(0);
//												fsc2.setBalanceModeStockCount(0);
//												fsc2.setBalanceModeStockPrice(0);
//												fsc2.setSupplierId(batch.getSupplierId());
//												psService.addFinanceStockCard(fsc2);
//											} while (stockExchangeCount>0&&index<sbList.size());
//										}
										StockCardBean sc = new StockCardBean();//批次修改结束//添加进销存卡片开始	// 入库卡片
										sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
										sc.setCode(cargoOperation.getCode());
										sc.setCreateDatetime(DateUtil.getNow());
										sc.setStockType(outCi.getStockType());
										sc.setStockArea(outCi.getAreaId());
										sc.setProductId(inCps.getProductId());
										sc.setStockId(psIn.getId());
										sc.setStockInCount(inCoc.getStockCount());
										sc.setStockInPriceSum(0);
										sc.setCurrentStock(product.getStock(outCi.getAreaId(), sc.getStockType())
												+ product.getLockCount(outCi.getStockAreaId(), sc.getStockType()));
										sc.setStockAllArea(product.getStock(outCi.getAreaId())
												+ product.getLockCount(outCi.getAreaId()));
										sc.setStockAllType(product.getStockAllType(sc.getStockType())
												+ product.getLockCountAllType(sc.getStockType()));
										sc.setAllStock(product.getStockAll() + product.getLockCountAll());
										sc.setStockPrice(product.getPrice5());// 新的库存价格
										sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
												new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
										psService.addStockCard(sc);
										StockCardBean sc2 = new StockCardBean();	// 出库卡片
										sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
										sc2.setCode(cargoOperation.getCode());
										sc2.setCreateDatetime(DateUtil.getNow());
										sc2.setStockType(inCi.getStockType());
										sc2.setStockArea(inCi.getAreaId());
										sc2.setProductId(product.getId());
										sc2.setStockId(psOut.getId());
										sc2.setStockOutCount(inCoc.getStockCount());
										sc2.setStockOutPriceSum((new BigDecimal(inCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
										sc2.setCurrentStock(product.getStock(inCi.getAreaId(), sc2.getStockType())
												+ product.getLockCount(inCi.getAreaId(), sc2.getStockType()));
										sc2.setStockAllArea(product.getStock(inCi.getAreaId())
												+ product.getLockCount(inCi.getAreaId()));
										sc2.setStockAllType(product.getStockAllType(sc2.getStockType())
												+ product.getLockCountAllType(sc2.getStockType()));
										sc2.setAllStock(product.getStockAll() + product.getLockCountAll());
										sc2.setStockPrice(product.getPrice5());
										sc2.setAllStockPriceSum((new BigDecimal(sc2.getAllStock())).multiply(
												new BigDecimal(StringUtil.formatDouble2(sc2.getStockPrice()))).doubleValue());
										psService.addStockCard(sc2);//添加进销存卡片结束
									}
									temp_inCps = service.getCargoProductStock("id = "+temp_inCps.getId());	//货位入库卡片
									CargoStockCardBean csc = new CargoStockCardBean();
									csc.setCardType(CargoStockCardBean.CARDTYPE_DOWNSHELFSTOCKIN);
									csc.setCode(cargoOperation.getCode());
									csc.setCreateDatetime(DateUtil.getNow());
									csc.setStockType(outCi.getStockType());
									csc.setStockArea(outCi.getAreaId());
									csc.setProductId(product.getId());
									csc.setStockId(temp_inCps.getId());
									csc.setStockInCount(outCoc.getStockCount());
									csc.setStockInPriceSum((new BigDecimal(outCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
									csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
									csc.setAllStock(product.getStockAll() + product.getLockCountAll());
									csc.setCurrentCargoStock(temp_inCps.getStockCount()+temp_inCps.getStockLockCount());
									csc.setCargoStoreType(outCi.getStoreType());
									csc.setCargoWholeCode(outCi.getWholeCode());
									csc.setStockPrice(product.getPrice5());
									csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
									service.addCargoStockCard(csc);
									stockOutCount = stockOutCount + outCoc.getStockCount();
									
									//合格库待作业任务处理
									CargoOperationTodoBean cot=CIService.getCargoOperationTodo("cargo_product_stock_id="+outCps.getId()+" and status in(0,1,2) and type=1");
									if(cot!=null){
										CIService.updateCargoOperationTodo("status=3", "id="+cot.getId());
									}
								}
								//货位出库卡片
								inCps = service.getCargoProductStock("id = "+inCps.getId());
								CargoStockCardBean csc = new CargoStockCardBean();
								csc.setCardType(CargoStockCardBean.CARDTYPE_DOWNSHELFSTOCKOUT);
								csc.setCode(cargoOperation.getCode());
								csc.setCreateDatetime(DateUtil.getNow());
								csc.setStockType(inCi.getStockType());
								csc.setStockArea(inCi.getAreaId());
								csc.setProductId(product.getId());
								csc.setStockId(inCps.getId());
								csc.setStockOutCount(stockOutCount);
								csc.setStockOutPriceSum((new BigDecimal(stockOutCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
								csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
								csc.setAllStock(product.getStockAll() + product.getLockCountAll());
								csc.setCurrentCargoStock(inCps.getStockCount()+inCps.getStockLockCount());
								csc.setCargoStoreType(inCi.getStoreType());
								csc.setCargoWholeCode(inCi.getWholeCode());
								csc.setStockPrice(product.getPrice5());
								csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
								service.addCargoStockCard(csc);
							}
						CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
						CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS16);//下个阶段
						if(process==null){
							service.getDbOp().rollbackTransaction();
				    		return returnError("作业单流程信息错误!");
						}
						if(process2==null){
							service.getDbOp().rollbackTransaction();
				    		return returnError("作业单流程信息错误!");
						}
						//修改上一操作日志的时效
						CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
						if(lastLog != null && lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
							int effectTime=process.getEffectTime();//上阶段时效
							String lastOperateTime=lastLog.getOperDatetime();
							SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							long date1=sdf.parse(lastOperateTime).getTime();
							long date2=sdf.parse(DateUtil.getNow()).getTime();
							if(date1+effectTime*60*1000<date2){//已超时
								service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
							}
						}
						if(!service.updateCargoOperation(
								"status="+CargoOperationProcessBean.OPERATION_STATUS17+"" +
										",effect_status = 2,last_operate_datetime='"+DateUtil.getNow()+"'"+"" +
												",complete_datetime='"+DateUtil.getNow()+"'" +
														",complete_user_id="+user.getId()+"" +
																",complete_user_name='"+user.getUsername()+"'", "id="+operId)){
							service.getDbOp().rollbackTransaction();
				    		return returnError("更新下架单状态，数据库操作失败!");
						}
						CargoOperLogBean operLog=new CargoOperLogBean();
						operLog.setOperId(operId);
						operLog.setOperCode(cargoOperation.getCode());
						operLog.setOperName(process.getOperName());
						operLog.setOperDatetime(DateUtil.getNow());
						operLog.setOperAdminId(user.getId());
						operLog.setOperAdminName(user.getUsername());
						operLog.setHandlerCode("");
						operLog.setEffectTime(2);
						operLog.setRemark("");
						operLog.setPreStatusName(process.getStatusName());
						operLog.setNextStatusName(process2.getStatusName());
						service.addCargoOperLog(operLog);
						
						//修改相关调拨单关联的货位，改为关联目的货位
						CartonningInfoBean cartonning=CIService.getCartonningInfo("oper_id="+operId);
						if(cartonning!=null){
							CargoOperationCargoBean coc=service.getCargoOperationCargo("oper_id="+operId+" and in_cargo_product_stock_id>0");
							CargoInfoBean cargo=service.getCargoInfo("whole_code='"+coc.getInCargoWholeCode()+"'");
							if(cargo!=null){
								if(!CIService.updateCartonningInfo("cargo_id="+cargo.getId(), "id="+cartonning.getId())){
									service.getDbOp().rollbackTransaction();
						    		return returnError("数据库操作失败!");
								}
							}
						}
					}else if(type == 2){//补货
						if(cargoOperation.getStatus()>=CargoOperationProcessBean.OPERATION_STATUS25){
							return returnError("该作业单状态已修改，操作失败!");
						}
						baseList = new ArrayList<BaseProductInfo>();
						List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 0", -1, -1, "id asc");
						for(int i=0;i<inCocList.size();i++){
							CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
							CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
							CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getInCargoWholeCode()+"'");
							voProduct product = wareService.getProduct(inCoc.getProductId());
							product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
							int stockInCount = 0;
							List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and in_cargo_product_stock_id = "+inCoc.getInCargoProductStockId()+" and type = 1 and use_status = 1", -1, -1, "id asc");
							for(int j=0;j<outCocList.size();j++){
								CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
								CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
								CargoInfoBean outCi = service.getCargoInfo("whole_code = '"+outCoc.getOutCargoWholeCode()+"'");
								if(!service.updateCargoProductStockCount(inCps.getId(), outCoc.getStockCount())){
									service.getDbOp().rollbackTransaction();
									return returnError("操作失败，货位冻结库存不足!");
								}
								if(!service.updateCargoProductStockLockCount(outCps.getId(), -outCoc.getStockCount())){
									service.getDbOp().rollbackTransaction();
									return returnError("操作失败，货位冻结库存不足!");
								}
								if(outCi.getAreaId()!=inCi.getAreaId()||outCi.getStockType()!=inCi.getStockType()){	//调整合格库库存
									//CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
									CargoInfoAreaBean cargoInfoArea1=service.getCargoInfoArea("id="+inCi.getAreaId());//目的货位地区
									CargoInfoAreaBean cargoInfoArea2=service.getCargoInfoArea("id="+outCi.getAreaId());//源货位地区
									
									ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea1.getOldId()+" and type="+inCi.getStockType());
									ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea2.getOldId()+" and type="+outCi.getStockType());
									
									if(psIn==null){
										service.getDbOp().rollbackTransaction();
										return returnError("合格库库存数据错误!");
									}
									if (!psService.updateProductStockCount(psIn.getId(),outCoc.getStockCount())) {
										service.getDbOp().rollbackTransaction();
										return returnError("库存操作失败，可能是库存不足，请与管理员联系!");
									}
									if(psOut==null){
										service.getDbOp().rollbackTransaction();
										return returnError("合格库库存数据错误!");
									}
									if (!psService.updateProductLockCount(psOut.getId(),-outCoc.getStockCount())) {
										service.getDbOp().rollbackTransaction();
										return returnError("库存操作失败，可能是库存不足，请与管理员联系!");
									}
									
									baseProductInfo = new BaseProductInfo();
									baseProductInfo.setId(outCoc.getProductId());
									//出库
									baseProductInfo.setProductStockOutId(psOut.getId());
									//入库
									baseProductInfo.setProductStockId(psIn.getId());
									baseProductInfo.setOutCount(outCoc.getStockCount());
									baseList.add(baseProductInfo);
									//批次修改开始
									//更新批次记录、添加调拨出、入库批次记录
//									List sbList = stockService.getStockBatchList("product_id="+outCoc.getProductId()+" and stock_type="+outCi.getStockType()+" and stock_area="+outCi.getAreaId(), -1, -1, "id asc");
//									double stockinPrice = 0;
//									double stockoutPrice = 0;
//									if(sbList!=null&&sbList.size()!=0){
//										int stockExchangeCount = inCoc.getStockCount();
//										int index = 0;
//										int stockBatchCount = 0;
//										do {//出库
//											StockBatchBean batch = (StockBatchBean)sbList.get(index);
//											if(stockExchangeCount>=batch.getBatchCount()){
//												if(!stockService.deleteStockBatch("id="+batch.getId())){
//									                service.getDbOp().rollbackTransaction();
//									                return returnError("数据库操作失败!");
//												}
//												stockBatchCount = batch.getBatchCount();
//											}else{
//												if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
//									                service.getDbOp().rollbackTransaction();
//									                return returnError("数据库操作失败!");
//												}
//												stockBatchCount = stockExchangeCount;
//											}	//添加批次操作记录
//											StockBatchLogBean batchLog = new StockBatchLogBean();
//											batchLog.setCode(cargoOperation.getCode());
//											batchLog.setStockType(batch.getStockType());
//											batchLog.setStockArea(batch.getStockArea());
//											batchLog.setBatchCode(batch.getCode());
//											batchLog.setBatchCount(stockBatchCount);
//											batchLog.setBatchPrice(batch.getPrice());
//											batchLog.setProductId(batch.getProductId());
//											batchLog.setRemark("调拨出库");
//											batchLog.setCreateDatetime(DateUtil.getNow());
//											batchLog.setUserId(user.getId());
//											batchLog.setSupplierId(batch.getSupplierId());
//											batchLog.setTax(batch.getTax());
//											if(!stockService.addStockBatchLog(batchLog)){
//									             service.getDbOp().rollbackTransaction();
//									             return returnError("添加失败!");
//											}
//											stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//											//入库
//											StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId());
//											if(batchBean!=null){
//												if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
//									                service.getDbOp().rollbackTransaction();
//									                return returnError("数据库操作失败!");
//												}
//											}else{
//												StockBatchBean newBatch = new StockBatchBean();
//												newBatch.setCode(batch.getCode());
//												newBatch.setProductId(batch.getProductId());
//												newBatch.setPrice(batch.getPrice());
//												newBatch.setBatchCount(stockBatchCount);
//												newBatch.setProductStockId(psIn.getId());
//												newBatch.setStockArea(inCi.getAreaId());
//												newBatch.setStockType(psIn.getType());
//												newBatch.setSupplierId(batch.getSupplierId());
//												newBatch.setTax(batch.getTax());
//												newBatch.setNotaxPrice(batch.getNotaxPrice());
//												newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
//												if(!stockService.addStockBatch(newBatch)){
//													service.getDbOp().rollbackTransaction();
//													return returnError("添加失败!");
//												}
//											}
//											//添加批次操作记录
//											batchLog = new StockBatchLogBean();
//											batchLog.setCode(cargoOperation.getCode());
//											batchLog.setStockType(psIn.getType());
//											batchLog.setStockArea(inCi.getAreaId());
//											batchLog.setBatchCode(batch.getCode());
//											batchLog.setBatchCount(stockBatchCount);
//											batchLog.setBatchPrice(batch.getPrice());
//											batchLog.setProductId(batch.getProductId());
//											batchLog.setRemark("调拨入库");
//											batchLog.setCreateDatetime(DateUtil.getNow());
//											batchLog.setUserId(user.getId());
//											batchLog.setSupplierId(batch.getSupplierId());
//											batchLog.setTax(batch.getTax());
//											if(!stockService.addStockBatchLog(batchLog)){
//												service.getDbOp().rollbackTransaction();
//												return returnError("添加失败!");
//											}
//											stockExchangeCount -= batch.getBatchCount();
//											index++;
//											stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//											
//											double stockInPriceSum = (new BigDecimal(stockBatchCount)).multiply(
//													new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue();
//											int allStock = product.getStockAll() + product.getLockCountAll();
//											double allStockPriceSum = (new BigDecimal(allStock)).multiply(
//													new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue();
//											double tax = batch.getTax();
//											
//											// 添加财务进销存入库卡片
//											FinanceStockCardBean fsc = new FinanceStockCardBean();
//											fsc.setCardType(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//											fsc.setCode(cargoOperation.getCode());
//											fsc.setCreateDatetime(DateUtil.getNow());
//											fsc.setStockType(inCi.getStockType());
//											fsc.setStockArea(inCi.getAreaId());
//											fsc.setProductId(inCps.getProductId());
//											fsc.setStockId(psIn.getId());
//											fsc.setStockInCount(stockBatchCount);
//											fsc.setStockInPriceSum(Arith.mul(stockInPriceSum, 1 + tax));
//											fsc.setCurrentStock(product.getStock(inCi.getAreaId(), 
//													inCi.getStockType()) + product.getLockCount(inCi.getStockAreaId(), inCi.getStockType()));
//											fsc.setStockAllArea(product.getStock(inCi.getAreaId()) + product.getLockCount(inCi.getAreaId()));
//											fsc.setStockAllType(product.getStockAllType(inCi.getStockType()) + product.getLockCountAllType(inCi.getStockType()));
//											fsc.setAllStock(allStock);
//											fsc.setStockPrice(new BigDecimal(Arith.mul(product.getPrice5(), 1 + tax)).floatValue());// 新的库存价格
//											fsc.setAllStockPriceSum(Arith.mul(allStockPriceSum, 1 + tax));
//											fsc.setType(FinanceStockCardBean.TYPE_STOCKEXCHANGEIN3);
//											fsc.setTax(tax);
//											fsc.setStockBatchCode(batch.getCode());
//											fsc.setBalanceMode(0);
//											fsc.setIsTicket(0);
//											fsc.setBalanceModeStockCount(0);
//											fsc.setBalanceModeStockPrice(0);
//											fsc.setSupplierId(batch.getSupplierId());
//											psService.addFinanceStockCard(fsc);
//											
//											// 添加财务进销存出库卡片
//											FinanceStockCardBean fsc2 = new FinanceStockCardBean();
//											fsc2.setCardType(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
//											fsc2.setCode(cargoOperation.getCode());
//											fsc2.setCreateDatetime(DateUtil.getNow());
//											fsc2.setStockType(outCi.getStockType());
//											fsc2.setStockArea(outCi.getAreaId());
//											fsc2.setProductId(product.getId());
//											fsc2.setStockId(psOut.getId());
//											fsc2.setStockInCount(stockBatchCount);
//											fsc2.setStockInPriceSum(Arith.mul(stockInPriceSum, 1 + tax));
//											fsc2.setCurrentStock(product.getStock(outCi.getAreaId(), 
//													outCi.getStockType()) + product.getLockCount(outCi.getStockAreaId(), outCi.getStockType()));
//											fsc2.setStockAllArea(product.getStock(outCi.getAreaId()) + product.getLockCount(outCi.getAreaId()));
//											fsc2.setStockAllType(product.getStockAllType(outCi.getStockType()) + product.getLockCountAllType(outCi.getStockType()));
//											fsc2.setAllStock(allStock);
//											fsc2.setStockPrice(new BigDecimal(Arith.mul(product.getPrice5(), 1 + tax)).floatValue());// 新的库存价格
//											fsc2.setAllStockPriceSum(Arith.mul(allStockPriceSum, 1 + tax));
//											fsc2.setType(FinanceStockCardBean.TYPE_STOCKEXCHANGEOUT4);
//											fsc2.setTax(tax);
//											fsc2.setStockBatchCode(batch.getCode());
//											fsc2.setBalanceMode(0);
//											fsc2.setIsTicket(0);
//											fsc2.setBalanceModeStockCount(0);
//											fsc2.setBalanceModeStockPrice(0);
//											fsc2.setSupplierId(batch.getSupplierId());
//											psService.addFinanceStockCard(fsc2);
//										} while (stockExchangeCount>0&&index<sbList.size());
//									}
									//批次修改结束
									//添加进销存卡片开始
									// 入库卡片
									StockCardBean sc = new StockCardBean();
									sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
									sc.setCode(cargoOperation.getCode());
									sc.setCreateDatetime(DateUtil.getNow());
									sc.setStockType(inCi.getStockType());
									sc.setStockArea(inCi.getAreaId());
									sc.setProductId(inCps.getProductId());
									sc.setStockId(psIn.getId());
									sc.setStockInCount(inCoc.getStockCount());
									sc.setStockInPriceSum(0);
									sc.setCurrentStock(product.getStock(inCi.getAreaId(), sc.getStockType())
											+ product.getLockCount(inCi.getStockAreaId(), sc.getStockType()));
									sc.setStockAllArea(product.getStock(inCi.getAreaId())
											+ product.getLockCount(inCi.getAreaId()));
									sc.setStockAllType(product.getStockAllType(sc.getStockType())
											+ product.getLockCountAllType(sc.getStockType()));
									sc.setAllStock(product.getStockAll() + product.getLockCountAll());
									sc.setStockPrice(product.getPrice5());// 新的库存价格
									sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
											new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
									psService.addStockCard(sc);
									// 出库卡片
									StockCardBean sc2 = new StockCardBean();
									sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
									sc2.setCode(cargoOperation.getCode());
									sc2.setCreateDatetime(DateUtil.getNow());
									sc2.setStockType(outCi.getStockType());
									sc2.setStockArea(outCi.getAreaId());
									sc2.setProductId(product.getId());
									sc2.setStockId(psOut.getId());
									sc2.setStockOutCount(inCoc.getStockCount());
									sc2.setStockOutPriceSum((new BigDecimal(inCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
									sc2.setCurrentStock(product.getStock(outCi.getAreaId(), sc2.getStockType())
											+ product.getLockCount(outCi.getAreaId(), sc2.getStockType()));
									sc2.setStockAllArea(product.getStock(outCi.getAreaId())
											+ product.getLockCount(outCi.getAreaId()));
									sc2.setStockAllType(product.getStockAllType(sc2.getStockType())
											+ product.getLockCountAllType(sc2.getStockType()));
									sc2.setAllStock(product.getStockAll() + product.getLockCountAll());
									sc2.setStockPrice(product.getPrice5());
									sc2.setAllStockPriceSum((new BigDecimal(sc2.getAllStock())).multiply(
											new BigDecimal(StringUtil.formatDouble2(sc2.getStockPrice()))).doubleValue());
									psService.addStockCard(sc2);
									//添加进销存卡片结束
								}
								//货位出库卡片
								outCps = service.getCargoProductStock("id = "+outCps.getId());
								CargoStockCardBean csc = new CargoStockCardBean();
								csc.setCardType(CargoStockCardBean.CARDTYPE_REFILLSTOCKOUT);
								csc.setCode(cargoOperation.getCode());
								csc.setCreateDatetime(DateUtil.getNow());
								csc.setStockType(inCi.getStockType());
								csc.setStockArea(inCi.getAreaId());
								csc.setProductId(product.getId());
								csc.setStockId(outCps.getId());
								csc.setStockOutCount(outCoc.getStockCount());
								csc.setStockOutPriceSum((new BigDecimal(outCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
								csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
								csc.setAllStock(product.getStockAll() + product.getLockCountAll());
								csc.setCurrentCargoStock(outCps.getStockCount()+outCps.getStockLockCount());
								csc.setCargoStoreType(outCi.getStoreType());
								csc.setCargoWholeCode(outCi.getWholeCode());
								csc.setStockPrice(product.getPrice5());
								csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());

								stockInCount = stockInCount + outCoc.getStockCount();
								if(outCi.getAreaId()!=inCi.getAreaId()){
									//更新订单缺货状态
									this.updateLackOrder(outCoc.getProductId());
								}
								
								//合格库待作业任务处理
								CargoOperationTodoBean cot=CIService.getCargoOperationTodo("cargo_product_stock_id="+outCps.getId()+" and status in(0,1,2) and type=2");
								if(cot!=null){
									CIService.updateCargoOperationTodo("status=3", "id="+cot.getId());
								}
								
							}
							//货位入库卡片
							inCps = service.getCargoProductStock("id = "+inCps.getId());
							CargoStockCardBean csc = new CargoStockCardBean();
							csc.setCardType(CargoStockCardBean.CARDTYPE_REFILLSTOCKIN);
							csc.setCode(cargoOperation.getCode());
							csc.setCreateDatetime(DateUtil.getNow());
							csc.setStockType(inCi.getStockType());
							csc.setStockArea(inCi.getAreaId());
							csc.setProductId(product.getId());
							csc.setStockId(inCps.getId());
							csc.setStockInCount(stockInCount);
							csc.setStockInPriceSum((new BigDecimal(stockInCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
							csc.setAllStock(product.getStockAll() + product.getLockCountAll());
							csc.setCurrentCargoStock(inCps.getStockCount()+inCps.getStockLockCount());
							csc.setCargoStoreType(inCi.getStoreType());
							csc.setCargoWholeCode(inCi.getWholeCode());
							csc.setStockPrice(product.getPrice5());
							csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
							service.addCargoStockCard(csc);
						}
						CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
						CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS25);//下个阶段
						if(process==null){
							service.getDbOp().rollbackTransaction();
							return returnError("作业单流程信息错误!");
						}
						if(process2==null){
							service.getDbOp().rollbackTransaction();
							return returnError("作业单流程信息错误!");
						}
						//修改上一操作日志的时效
						CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
						if(lastLog != null && lastLog.getEffectTime()==1){//如果不是进行中，不需要再改时效
							int effectTime=process.getEffectTime();//上阶段时效
							String lastOperateTime=lastLog.getOperDatetime();
							SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							long date1=sdf.parse(lastOperateTime).getTime();
							long date2=sdf.parse(DateUtil.getNow()).getTime();
							if(date1+effectTime*60*1000<date2){//已超时
								service.updateCargoOperLog("effect_time=2", "id="+lastLog.getId());
							}
						}
						if(!service.updateCargoOperation(
								"status="+CargoOperationProcessBean.OPERATION_STATUS26+"" +
										",effect_status = 2,last_operate_datetime='"+DateUtil.getNow()+"'"+"" +
												",complete_datetime='"+DateUtil.getNow()+"'" +
														",complete_user_id="+user.getId()+"" +
																",complete_user_name='"+user.getUsername()+"'", 
																"id="+operId)){
							service.getDbOp().rollbackTransaction();
							return returnError("更新补货单状态，数据库操作失败!");
						}
						CargoOperLogBean operLog=new CargoOperLogBean();
						operLog.setOperId(operId);
						operLog.setOperCode(cargoOperation.getCode());
						operLog.setOperName(process2.getOperName());
						operLog.setOperDatetime(DateUtil.getNow());
						operLog.setOperAdminId(user.getId());
						operLog.setOperAdminName(user.getUsername());
						operLog.setHandlerCode("");
						operLog.setEffectTime(2);
						operLog.setRemark("");
						operLog.setPreStatusName(process.getStatusName());
						operLog.setNextStatusName(process2.getStatusName());
						service.addCargoOperLog(operLog);
						CartonningInfoBean cib = CIService.getCartonningInfo("oper_id=" + cargoOperation.getId());
						if(cib!=null){
							if(!CIService.updateCartonningInfo("status=2", "id=" + cib.getId())){
								service.getDbOp().rollbackTransaction();
								return returnError("更新装箱单状态，数据库操作失败!");
							}
						}
					}else if(type == 3){//调拨
						if(cargoOperation.getStatus()>=CargoOperationProcessBean.OPERATION_STATUS34){
							service.getDbOp().rollbackTransaction();
							return returnError("该作业单状态已被更新，操作失败!");
						}
						service.getDbOp().startTransaction();//完成货位库存量操作
						baseList = new ArrayList<BaseProductInfo>();
						List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1", -1, -1, "id asc");
						for(int i=0;i<outCocList.size();i++){
							CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);
							CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
							CargoInfoBean outCi = service.getCargoInfo("id = "+outCps.getCargoId());
							voProduct product = wareService.getProduct(outCoc.getProductId());
							product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
							int stockOutCount = 0;
							List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
							for(int j=0;j<inCocList.size();j++){
								CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(j);
								CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
								CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getInCargoWholeCode()+"'");
								if(inCps!=null&&outCps!=null){
									if(!service.updateCargoProductStockCount(inCps.getId(), inCoc.getStockCount())){
										service.getDbOp().rollbackTransaction();
										return returnError("操作失败，货位冻结库存不足!");
									}
									if(!service.updateCargoProductStockLockCount(outCps.getId(), -inCoc.getStockCount())){
										service.getDbOp().rollbackTransaction();
										return returnError("操作失败，货位冻结库存不足!");
									}
									//调整合格库库存，修改批次，添加进销存卡片
									if(inCi.getAreaId()!=outCi.getAreaId()||outCi.getStockType()!=inCi.getStockType()){
										CargoInfoAreaBean cargoInfoArea1=service.getCargoInfoArea("id="+inCi.getAreaId());//目的货位地区
										CargoInfoAreaBean cargoInfoArea2=service.getCargoInfoArea("id="+outCi.getAreaId());//源货位地区
										
										ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea1.getOldId()+" and type="+inCi.getStockType());
										ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+cargoInfoArea2.getOldId()+" and type="+outCi.getStockType());
										
										if(psIn==null){
											service.getDbOp().rollbackTransaction();
											return returnError("合格库库存数据错误!");
										}
										if (!psService.updateProductStockCount(psIn.getId(),outCoc.getStockCount())) {
											service.getDbOp().rollbackTransaction();
											return returnError("库存操作失败，可能是库存不足，请与管理员联系!");
										}
										if(psOut==null){
											service.getDbOp().rollbackTransaction();
											return returnError("合格库库存数据错误!");
										}
										if (!psService.updateProductLockCount(psOut.getId(),-outCoc.getStockCount())) {
											service.getDbOp().rollbackTransaction();
											return returnError("库存操作失败，可能是库存不足，请与管理员联系!");
										}
										baseProductInfo = new BaseProductInfo();
										baseProductInfo.setId(outCoc.getProductId());
										//出库
										baseProductInfo.setProductStockOutId(psOut.getId());
										//入库
										baseProductInfo.setProductStockId(psIn.getId());
										baseProductInfo.setOutCount(outCoc.getStockCount());
										baseList.add(baseProductInfo);
										//批次修改开始
										//更新批次记录、添加调拨出、入库批次记录
//										List sbList = stockService.getStockBatchList("product_id="+outCoc.getProductId()+" and stock_type="+outCi.getStockType()+" and stock_area="+outCi.getAreaId(), -1, -1, "id asc");
//										double stockinPrice = 0;
//										double stockoutPrice = 0;
//										if(sbList!=null&&sbList.size()!=0){
//											int stockExchangeCount = inCoc.getStockCount();
//											int index = 0;
//											int stockBatchCount = 0;
//											
//											do {
//												//出库
//												StockBatchBean batch = (StockBatchBean)sbList.get(index);
//												if(stockExchangeCount>=batch.getBatchCount()){
//													if(!stockService.deleteStockBatch("id="+batch.getId())){
//										                service.getDbOp().rollbackTransaction();
//										                return returnError("数据库操作失败!");
//													}
//													stockBatchCount = batch.getBatchCount();
//												}else{
//													if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
//										                service.getDbOp().rollbackTransaction();
//										                return returnError("数据库操作失败!");
//													}
//													stockBatchCount = stockExchangeCount;
//												}
//												
//												//添加批次操作记录
//												StockBatchLogBean batchLog = new StockBatchLogBean();
//												batchLog.setCode(cargoOperation.getCode());
//												batchLog.setStockType(batch.getStockType());
//												batchLog.setStockArea(batch.getStockArea());
//												batchLog.setBatchCode(batch.getCode());
//												batchLog.setBatchCount(stockBatchCount);
//												batchLog.setBatchPrice(batch.getPrice());
//												batchLog.setProductId(batch.getProductId());
//												batchLog.setRemark("调拨出库");
//												batchLog.setCreateDatetime(DateUtil.getNow());
//												batchLog.setUserId(user.getId());
//												batchLog.setSupplierId(batch.getSupplierId());
//												batchLog.setTax(batch.getTax());
//												if(!stockService.addStockBatchLog(batchLog)){
//										             service.getDbOp().rollbackTransaction();
//										             return returnError("添加失败!");
//												}
//												
//												stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//												
//												//入库
//												StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId());
//												if(batchBean!=null){
//													if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
//										                service.getDbOp().rollbackTransaction();
//										                return returnError("数据库操作失败!");
//													}
//												}else{
//													StockBatchBean newBatch = new StockBatchBean();
//													newBatch.setCode(batch.getCode());
//													newBatch.setProductId(batch.getProductId());
//													newBatch.setPrice(batch.getPrice());
//													newBatch.setBatchCount(stockBatchCount);
//													newBatch.setProductStockId(psIn.getId());
//													newBatch.setStockArea(inCi.getAreaId());
//													newBatch.setStockType(psIn.getType());
//													newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
//													newBatch.setSupplierId(batch.getSupplierId());
//													newBatch.setTax(batch.getTax());
//													newBatch.setNotaxPrice(batch.getNotaxPrice());
//													if(!stockService.addStockBatch(newBatch)){
//														service.getDbOp().rollbackTransaction();
//														return returnError("添加失败!");
//													}
//												}
//												
//												//添加批次操作记录
//												batchLog = new StockBatchLogBean();
//												batchLog.setCode(cargoOperation.getCode());
//												batchLog.setStockType(psIn.getType());
//												batchLog.setStockArea(inCi.getAreaId());
//												batchLog.setBatchCode(batch.getCode());
//												batchLog.setBatchCount(stockBatchCount);
//												batchLog.setBatchPrice(batch.getPrice());
//												batchLog.setProductId(batch.getProductId());
//												batchLog.setRemark("调拨入库");
//												batchLog.setCreateDatetime(DateUtil.getNow());
//												batchLog.setUserId(user.getId());
//												batchLog.setSupplierId(batch.getSupplierId());
//												batchLog.setTax(batch.getTax());
//												if(!stockService.addStockBatchLog(batchLog)){
//													service.getDbOp().rollbackTransaction();
//													return returnError("添加失败!");
//												}
//												
//												stockExchangeCount -= batch.getBatchCount();
//												index++;
//												
//												stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//												
//												double stockInPriceSum = (new BigDecimal(stockBatchCount)).multiply(
//														new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue();
//												int allStock = product.getStockAll() + product.getLockCountAll();
//												double allStockPriceSum = (new BigDecimal(allStock)).multiply(
//														new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue();
//												double tax = batch.getTax();
//												
//												// 添加财务进销存入库卡片
//												FinanceStockCardBean fsc = new FinanceStockCardBean();
//												fsc.setCardType(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//												fsc.setCode(cargoOperation.getCode());
//												fsc.setCreateDatetime(DateUtil.getNow());
//												fsc.setStockType(inCi.getStockType());
//												fsc.setStockArea(inCi.getAreaId());
//												fsc.setProductId(inCps.getProductId());
//												fsc.setStockId(psIn.getId());
//												fsc.setStockInCount(stockBatchCount);
//												fsc.setStockInPriceSum(Arith.mul(stockInPriceSum, 1 + tax));
//												fsc.setCurrentStock(product.getStock(inCi.getAreaId(), 
//														inCi.getStockType()) + product.getLockCount(inCi.getStockAreaId(), inCi.getStockType()));
//												fsc.setStockAllArea(product.getStock(inCi.getAreaId()) + product.getLockCount(inCi.getAreaId()));
//												fsc.setStockAllType(product.getStockAllType(inCi.getStockType()) + product.getLockCountAllType(inCi.getStockType()));
//												fsc.setAllStock(allStock);
//												fsc.setStockPrice(new BigDecimal(Arith.mul(product.getPrice5(), 1 + tax)).floatValue());// 新的库存价格
//												fsc.setAllStockPriceSum(Arith.mul(allStockPriceSum, 1 + tax));
//												fsc.setType(FinanceStockCardBean.TYPE_STOCKEXCHANGEIN3);
//												fsc.setTax(tax);
//												fsc.setStockBatchCode(batch.getCode());
//												fsc.setBalanceMode(0);
//												fsc.setIsTicket(0);
//												fsc.setBalanceModeStockCount(0);
//												fsc.setBalanceModeStockPrice(0);
//												fsc.setSupplierId(batch.getSupplierId());
//												psService.addFinanceStockCard(fsc);
//												
//												// 添加财务进销存出库卡片
//												FinanceStockCardBean fsc2 = new FinanceStockCardBean();
//												fsc2.setCardType(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
//												fsc2.setCode(cargoOperation.getCode());
//												fsc2.setCreateDatetime(DateUtil.getNow());
//												fsc2.setStockType(outCi.getStockType());
//												fsc2.setStockArea(outCi.getAreaId());
//												fsc2.setProductId(product.getId());
//												fsc2.setStockId(psOut.getId());
//												fsc2.setStockInCount(stockBatchCount);
//												fsc2.setStockInPriceSum(Arith.mul(stockInPriceSum, 1 + tax));
//												fsc2.setCurrentStock(product.getStock(outCi.getAreaId(), 
//														outCi.getStockType()) + product.getLockCount(outCi.getStockAreaId(), outCi.getStockType()));
//												fsc2.setStockAllArea(product.getStock(outCi.getAreaId()) + product.getLockCount(outCi.getAreaId()));
//												fsc2.setStockAllType(product.getStockAllType(outCi.getStockType()) + product.getLockCountAllType(outCi.getStockType()));
//												fsc2.setAllStock(allStock);
//												fsc2.setStockPrice(new BigDecimal(Arith.mul(product.getPrice5(), 1 + tax)).floatValue());// 新的库存价格
//												fsc2.setAllStockPriceSum(Arith.mul(allStockPriceSum, 1 + tax));
//												fsc2.setType(FinanceStockCardBean.TYPE_STOCKEXCHANGEOUT4);
//												fsc2.setTax(tax);
//												fsc2.setStockBatchCode(batch.getCode());
//												fsc2.setBalanceMode(0);
//												fsc2.setIsTicket(0);
//												fsc2.setBalanceModeStockCount(0);
//												fsc2.setBalanceModeStockPrice(0);
//												fsc2.setSupplierId(batch.getSupplierId());
//												psService.addFinanceStockCard(fsc2);
//											} while (stockExchangeCount>0&&index<sbList.size());
//										}
										//批次修改结束
										
										//添加进销存卡片开始
										// 入库卡片
										StockCardBean sc = new StockCardBean();
										sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
										sc.setCode(cargoOperation.getCode());

										sc.setCreateDatetime(DateUtil.getNow());
										sc.setStockType(inCi.getStockType());
										sc.setStockArea(inCi.getAreaId());
										sc.setProductId(inCps.getProductId());
										sc.setStockId(psIn.getId());
										sc.setStockInCount(inCoc.getStockCount());
										sc.setStockInPriceSum((new BigDecimal(inCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());

										sc.setCurrentStock(product.getStock(inCi.getAreaId(), sc.getStockType())
												+ product.getLockCount(inCi.getStockAreaId(), sc.getStockType()));
										sc.setStockAllArea(product.getStock(inCi.getAreaId())
												+ product.getLockCount(inCi.getAreaId()));
										sc.setStockAllType(product.getStockAllType(sc.getStockType())
												+ product.getLockCountAllType(sc.getStockType()));
										sc.setAllStock(product.getStockAll() + product.getLockCountAll());
										sc.setStockPrice(product.getPrice5());// 新的库存价格
										sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
												new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
										psService.addStockCard(sc);
										
										// 出库卡片
										StockCardBean sc2 = new StockCardBean();
										sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
										sc2.setCode(cargoOperation.getCode());
										sc2.setCreateDatetime(DateUtil.getNow());
										sc2.setStockType(outCi.getStockType());
										sc2.setStockArea(outCi.getAreaId());
										sc2.setProductId(product.getId());
										sc2.setStockId(psOut.getId());
										sc2.setStockOutCount(inCoc.getStockCount());
										sc2.setStockOutPriceSum((new BigDecimal(inCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
										sc2.setCurrentStock(product.getStock(outCi.getAreaId(), sc2.getStockType())
												+ product.getLockCount(outCi.getAreaId(), sc2.getStockType()));
										sc2.setStockAllArea(product.getStock(outCi.getAreaId())
												+ product.getLockCount(outCi.getAreaId()));
										sc2.setStockAllType(product.getStockAllType(sc2.getStockType())
												+ product.getLockCountAllType(sc2.getStockType()));
										sc2.setAllStock(product.getStockAll() + product.getLockCountAll());
										sc2.setStockPrice(product.getPrice5());
										sc2.setAllStockPriceSum((new BigDecimal(sc2.getAllStock())).multiply(
												new BigDecimal(StringUtil.formatDouble2(sc2.getStockPrice()))).doubleValue());
										psService.addStockCard(sc2);
										//添加进销存卡片结束
										
									}

									//货位入库卡片
									inCps = service.getCargoProductStock("id = "+inCps.getId());
									CargoStockCardBean csc = new CargoStockCardBean();
									csc.setCardType(CargoStockCardBean.CARDTYPE_CARGOEXCHAGESTOCKIN);
									csc.setCode(cargoOperation.getCode());
									csc.setCreateDatetime(DateUtil.getNow());
									csc.setStockType(inCi.getStockType());
									csc.setStockArea(inCi.getAreaId());
									csc.setProductId(product.getId());
									csc.setStockId(inCps.getId());
									csc.setStockInCount(inCoc.getStockCount());
									csc.setStockInPriceSum((new BigDecimal(inCoc.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
									csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
									csc.setAllStock(product.getStockAll() + product.getLockCountAll());
									csc.setCurrentCargoStock(inCps.getStockCount()+inCps.getStockLockCount());
									csc.setCargoStoreType(inCi.getStoreType());
									csc.setCargoWholeCode(inCi.getWholeCode());
									csc.setStockPrice(product.getPrice5());
									csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
									service.addCargoStockCard(csc);

									stockOutCount = stockOutCount + inCoc.getStockCount();
								}else{
									service.getDbOp().rollbackTransaction();
									return returnError("库存错误，无法提交!");
								}

								if(outCi.getAreaId()!=inCi.getAreaId()){
									//更新订单缺货状态
									this.updateLackOrder(outCoc.getProductId());
								}
								
								//合格库待作业任务处理
								CargoOperationTodoBean cot=CIService.getCargoOperationTodo("cargo_product_stock_id="+outCps.getId()+" and status in(0,1,2) and type=3");
								if(cot!=null){
									CIService.updateCargoOperationTodo("status=3", "id="+cot.getId());
								}
							}

							//货位出库卡片
							outCps = service.getCargoProductStock("id = "+outCps.getId());
							CargoStockCardBean csc = new CargoStockCardBean();
							csc.setCardType(CargoStockCardBean.CARDTYPE_CARGOEXCHAGESTOCKOUT);
							csc.setCode(cargoOperation.getCode());
							csc.setCreateDatetime(DateUtil.getNow());
							csc.setStockType(outCi.getStockType());
							csc.setStockArea(outCi.getAreaId());
							csc.setProductId(product.getId());
							csc.setStockId(outCps.getId());
							csc.setStockOutCount(stockOutCount);
							csc.setStockOutPriceSum((new BigDecimal(stockOutCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
							csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
							csc.setAllStock(product.getStockAll() + product.getLockCountAll());
							csc.setCurrentCargoStock(outCps.getStockCount()+outCps.getStockLockCount());
							csc.setCargoStoreType(outCi.getStoreType());
							csc.setCargoWholeCode(outCi.getWholeCode());
							csc.setStockPrice(product.getPrice5());
							csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
							service.addCargoStockCard(csc);
						}
						if(!service.updateCargoOperation(
								"status="+CargoOperationProcessBean.OPERATION_STATUS35+"" +
										",effect_status = 2,last_operate_datetime='"+DateUtil.getNow()+"'"+"" +
												",complete_datetime='"+DateUtil.getNow()+"'" +
														",complete_user_id="+user.getId()+"" +
																",complete_user_name='"+user.getUsername()+"'", 
																"id="+operId)){
							service.getDbOp().rollbackTransaction();
							return returnError("更新调拨单状态，数据库操作失败!");
						}
						//作废此装箱单
						if(!CIService.updateCartonningInfo(" status=2", " id=" + cartonningInfo.getId())){
							service.getDbOp().rollbackTransaction();
							return returnError("作废装箱单操作失败 !");
						}
						if(cargoOperation.getStockOutType()!=CargoInfoBean.STORE_TYPE0){
							//整件区调拨，修改装箱单相关装箱单关联货位
							//修改相关调拨单关联的货位，改为关联目的货位
							CartonningInfoBean cartonning=CIService.getCartonningInfo("oper_id="+operId);
							if(cartonning!=null){
								CargoOperationCargoBean coc=service.getCargoOperationCargo("oper_id="+operId+" and in_cargo_product_stock_id>0");
								CargoInfoBean cargo=service.getCargoInfo("whole_code='"+coc.getInCargoWholeCode()+"'");
								if(cargo!=null){
									CIService.updateCartonningInfo("cargo_id="+cargo.getId(), "id="+cartonning.getId());
								}
							}
						}
						
						
						CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
						CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS34);//下个阶段
						if(process==null){
							service.getDbOp().rollbackTransaction();
							return returnError("作业单流程信息错误!");
						}
						if(process2==null){
							service.getDbOp().rollbackTransaction();
							return returnError("作业单流程信息错误!");
						}
						//修改上一操作日志的时效
						CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
						if(lastLog!=null && lastLog.getEffectTime()==1){//如果不是进行中，不需要再改时效
							int effectTime=process.getEffectTime();//上阶段时效
							String lastOperateTime=lastLog.getOperDatetime();
							SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							long date1=sdf.parse(lastOperateTime).getTime();
							long date2=sdf.parse(DateUtil.getNow()).getTime();
							if(date1+effectTime*60*1000<date2){//已超时
								service.updateCargoOperLog("effect_time=2", "id="+lastLog.getId());
							}
						}
						CargoOperLogBean operLog=new CargoOperLogBean();
						operLog.setOperId(operId);
						operLog.setOperCode(cargoOperation.getCode());
						operLog.setOperName(process2.getOperName());
						operLog.setOperDatetime(DateUtil.getNow());
						operLog.setOperAdminId(user.getId());
						operLog.setOperAdminName(user.getUsername());
						operLog.setHandlerCode("");
						operLog.setEffectTime(2);
						operLog.setRemark("");
						operLog.setPreStatusName(process.getStatusName());
						operLog.setNextStatusName(process2.getStatusName());
						service.addCargoOperLog(operLog);
					}
					fbdSevice.acquireFinanceBaseData(baseList, cargoOperation.getCode(), user.getId(), -1, -1);
					service.getDbOp().commitTransaction();
					return returnResult("作业单确认完成成功!");
				}else{
					return returnError("身份验证失败!"); 
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			return returnError("系统异常，请联系管理员!");
		}finally{
			dbOp.release();
		}
	}
	/**
	 * 说明：创建&打印质检装箱单
	 * 
	 * 时间：2013-08-09
	 * 
	 * 作者：张晔
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/createQualityCartonning")
	@ResponseBody
	public JsonModel createQualityCartonning(HttpServletRequest request)
			throws Exception {
		JsonModel json = new JsonModel();
		WareService wareService = new WareService();
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService iStockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		Map<String, Object> data = null;
		try {
			//从流中读取json数据
			json = ReceiveJson.receiveJson(request);
			if(json==null){
				return returnError("没有收到请求数据!");
			}
			//验证用户名密码
			if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
				voUser user = (voUser)request.getSession().getAttribute("userView");
				String productCode = StringUtil.convertNull((String)json.getData().get("productCode"));
				int productCount = StringUtil.StringToId((String)json.getData().get("count"));
				String bsCode = StringUtil.convertNull((String)json.getData().get("bsCode"));
				int areaId = StringUtil.toInt(json.getArea());//地区id
				if (areaId == -1) {
					return returnError("没有收到选择的地区!");
				}
				synchronized (CartonningInfoAction.cargoLock) {
					if (productCount <= 0) {
						return returnError("商品数量必须大于0！");
					}
					if(!"".equals(bsCode)){
						BuyStockinBean bsBean = iStockService.getBuyStockin("code='" + StringUtil.toSql(bsCode) + "'");
						if(bsBean == null){
							return returnError("入库单不存在！");
						}
						if(bsBean.getStatus()!= 4 && bsBean.getStatus()!= 6 && bsBean.getStatus()!= 7){
							return returnError("入库单状态不正确！");
						}
					} else {
						return returnError("没有收到入库单号！");
					}
					if(!"".equals(productCode)){
						BuyStockinBean bsBean = iStockService.getBuyStockin("code='" + StringUtil.toSql(bsCode) + "'");
						voProduct pBean = wareService.getProduct2("code="+"'"+productCode+"'");
						if(pBean==null){
							ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+productCode+"'");
							if(bBean==null){
								return returnError("没有找到此商品！");
							}else{
								pBean = wareService.getProduct(bBean.getProductId());
								if(pBean==null){
									return returnError("没有找到此商品！");
								}
							}
						}
						BuyStockinProductBean bspBean = iStockService.getBuyStockinProduct(" buy_stockin_id=" + bsBean.getId() +" and product_id=" + pBean.getId());
						if(bspBean == null){
							return returnError("该商品与入库单中商品不一致！");
						}
					} else {
						return returnError("没有收到商品编码！");
					}
					CargoInfoBean cargoInfo = cargoService.getCargoInfo("stock_type=1 and area_id=" + areaId);
					if(cargoInfo == null){
						return returnError("未找到待验库货位！");
					}
					voProduct pBean = wareService.getProduct2("code="+"'"+productCode+"'");
					CargoProductStockBean cps=cargoService.getCargoProductStock("cargo_id="+cargoInfo.getId()+" and product_id="+pBean.getId());
					if(cps==null){
						return returnError("没有货位库存记录！");
					}
					if(cps.getStockCount()<productCount){
						return returnError("货位可用库存不足！");
					}
					BuyStockinBean bsBean = iStockService.getBuyStockin("code='" + StringUtil.toSql(bsCode) + "'");
					BuyStockinProductBean bspBean = iStockService.getBuyStockinProduct(" buy_stockin_id=" + bsBean.getId() +" and product_id=" + pBean.getId());
					List cartonningList = service.getCartonningList("buy_stockin_id=" + bsBean.getId() + " and status<>2 ", -1, -1, null);
					if(cartonningList.size()>0 ){
						int count =0;
						for(int i=0;i<cartonningList.size();i++){
							CartonningInfoBean ciBean = (CartonningInfoBean)cartonningList.get(i);
							CartonningProductInfoBean cipBean = service.getCartonningProductInfo("cartonning_id=" + ciBean.getId());
							count += cipBean.getProductCount();
						}
						if((count+ productCount)> bspBean.getStockInCount()){
							return returnError("该入库单累计装箱数量多于入库单中商品数量！");
						}
					}else{
						if(productCount > bspBean.getStockInCount()){
							return returnError("该入库单累计装箱数量多于入库单中商品数量！");
						}
					}
					String code = service.getZXCodeForToday();
			
					CartonningInfoBean bean =new CartonningInfoBean();
					bean.setCode(code);
					bean.setCreateTime(DateUtil.getNow());
					bean.setStatus(1);
					bean.setName(user.getUsername());
					bean.setCargoId(cargoInfo.getId());
					bean.setBuyStockInId(bsBean.getId());
					CartonningProductInfoBean productBean = new CartonningProductInfoBean();
					productBean.setProductCount(productCount);
					productBean.setProductCode(productCode);
					productBean.setProductName(pBean.getOriname());
					productBean.setProductId(pBean.getId());
					bean.setProductBean(productBean);
					service.getDbOp().startTransaction();  //开启事务
					if(!service.addCartonningInfo(bean)){
						service.getDbOp().rollbackTransaction();
						return returnError("添加装箱单，数据库操作失败！");
					}
					if( !service.fixCartonningInfoCode(code, service.getDbOp(), bean) ) {
						service.getDbOp().rollbackTransaction();
						request.setAttribute("result", "创建装箱单，数据库操作失败!");
						return returnError("添加装箱单，数据库操作失败！");
					}
					code = bean.getCode();
					CartonningInfoBean bean2 = service.getCartonningInfo("code="+"'"+code+"'");
					productBean.setCartonningId(bean2.getId());
					if(!service.addCartonningProductInfo(productBean)){
						service.getDbOp().rollbackTransaction();
						return returnError("添加装箱单产品信息，数据库操作失败！");
					}
			
					bean = service.getCartonningInfo("code="+"'"+code+"'");
					productBean = service.getCartonningProductInfo("cartonning_id="+bean.getId());
					bean.setProductBean(productBean);
					voProduct product = wareService.getProduct(productBean.getProductId());
					if(product == null){
						service.getDbOp().rollbackTransaction();
						return returnError("找不到商品信息！");
					}
				
					//处理装箱单历史装箱平均值
					CartonningStandardCountBean csc=service.getCartonningStandardCount("product_id="+product.getId());
					if(csc!=null){
						String lastOperDatetime=csc.getLastOperDatetime();
						String sql="select cpi.product_count from cartonning_info ci join cartonning_product_info cpi on ci.id=cpi.cartonning_id " +
								"where cpi.product_id="+product.getId()+" and ci.create_time>'"+lastOperDatetime.substring(0,19)+"' and ci.status<>2 and cause=0";
						ResultSet rs=service.getDbOp().executeQuery(sql);
						float standard=0;
						float cartonningCount=0;
						while(rs.next()){
							standard+=rs.getInt(1);
							cartonningCount++;
						}
						if(cartonningCount>=3){//加上这次添加的装箱单达到3个，修改标准装箱量
							int newStandard=Math.round(standard/cartonningCount);
							if(!service.updateCartonningStandardCount("standard="+newStandard+",last_oper_datetime='"+DateUtil.getNow()+"'", "id="+csc.getId()))
							{
							  service.getDbOp().rollbackTransaction();
							  return returnError("数据库操作失败！");
							}
							csc.setStandard(newStandard);
						}
					}else{
						CartonningStandardCountBean cBean=new CartonningStandardCountBean();
						cBean.setLastOperDatetime(DateUtil.getNow());
						cBean.setOperId(0);
						cBean.setOperName("");
						cBean.setProductId(product.getId());
						cBean.setStandard(productBean.getProductCount());
					
						if(!service.addCartonningStandardCount(cBean))
						{
						  service.getDbOp().rollbackTransaction();
						  return returnError("数据库操作失败！");
						}
						csc=cBean;
					}
				
					String result = "'质检装箱单:" + code + "创建并打印成功！";
					if(csc!=null){
						result+="标准装箱量："+csc.getStandard()+"。";
					}
					result+="'";
					//物流员工绩效考核操作
					CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
					if(csBean == null){
						service.getDbOp().rollbackTransaction();
						return returnError("此账号不是物流员工！");
					}
	        		CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and type=2 and staff_id=" + csBean.getId() );
	        		int operCount = 1;
	        		int performanceProductCount = productCount;
	        		if(cspBean != null){
	        			performanceProductCount = performanceProductCount + cspBean.getProductCount();
	        			operCount = operCount + cspBean.getOperCount();
						boolean flag = cargoService.updateCargoStaffPerformance(" oper_count=" + operCount + ", product_count=" + performanceProductCount, " id=" + cspBean.getId());
						if(!flag){
							service.getDbOp().rollbackTransaction();
							return returnError("物流员工绩效考核更新操作失败！");
						}
					}else{
						CargoStaffPerformanceBean newBean = new CargoStaffPerformanceBean();
						newBean.setDate(date);
						newBean.setProductCount(performanceProductCount);
						newBean.setOperCount(operCount);
						newBean.setStaffId(csBean.getId());
						newBean.setType(2);  //2代表质检装箱作业
						boolean flag = cargoService.addCargoStaffPerformance(newBean);
						if(!flag){
							service.getDbOp().rollbackTransaction();
							return returnError("物流员工绩效考核添加操作失败 ！");
						}
					}
					service.getDbOp().commitTransaction(); //提交事务
					data = new HashMap<String, Object>();
					data.put("productCode", bean.getProductBean().getProductCode());
					data.put("code", bean.getCode());
					data.put("productCount", bean.getProductBean().getProductCount());
					data.put("userName", ((voUser)request.getSession().getAttribute("userView")).getUsername());
					data.put("cartonningTime", bean.getCreateTime().substring(0, 19));
					data.put("result", result);
				}
			} else {
				return returnError("身份验证失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			if(service.getDbOp().getConn().getAutoCommit()==false){
				service.getDbOp().rollbackTransaction();
			}
			return returnError("程序异常,质检装箱单创建失败！");
		} finally {
			service.releaseAll();
		}
		return returnResult(data);
	}
	/**
	 * 说明：创建&打印作业装箱单
	 * 
	 * 时间：2013-08-09
	 * 
	 * 作者：张晔
	 */
	@RequestMapping("/createCartonning")
	@ResponseBody
	public JsonModel createCartonning(HttpServletRequest request)
			throws Exception {
		JsonModel json = new JsonModel();
		WareService wareService = new WareService();
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		Map<String, Object> data = null;
		try {
			//从流中读取json数据
			json = ReceiveJson.receiveJson(request);
			if(json==null){
				return returnError("没有收到请求数据!");
			}
			//验证用户名密码
			if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
				voUser user = (voUser)request.getSession().getAttribute("userView");
				int areaId = StringUtil.toInt(json.getArea());//地区id
				if (areaId == -1) {
					return returnError("没有收到选择的地区!");
				}
				//获取货位编号和商品编号
				String productCode = StringUtil.convertNull((String)json.getData().get("productCode"));
				String wholeCode = StringUtil.convertNull((String)json.getData().get("wholeCode"));
				
				
				synchronized (CartonningInfoAction.cargoLock) {
					CargoInfoBean cargoInfo = cargoService.getCargoInfo("whole_code='"+wholeCode+"'");
					if(cargoInfo == null){
						return returnError("货位号不正确！");
					}
					if(cargoInfo.getAreaId() != areaId){
						return returnError("此货位不属于这个仓！");
					}
					if(cargoInfo.getStatus() != 0){
						return returnError("此货位未被使用！");
					}
					if(cargoInfo.getStoreType() != 0&&cargoInfo.getStoreType() != 4){
						return returnError("此货位不是散件区或混合区货位！");
					}
					if(cargoInfo.getStockType() != 0){
						return returnError("此货位不是合格库货位！");
					}
					voProduct pBean = wareService.getProduct2("code='"+productCode+"'");
					if(pBean==null){
						ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+productCode+"'");
						if(bBean==null){
							return returnError("没有找到此商品");
						}else{
							pBean = wareService.getProduct2("a.id="+bBean.getProductId());
							if (pBean == null) {
								return returnError("没有找到此商品");
							}
						}
					}
					String code = service.getZXCodeForToday();
					CargoProductStockBean cargoProductStockBean = cargoService.getCargoProductStock("product_id="+pBean.getId()+" and cargo_id="+ cargoInfo.getId());
					if(cargoProductStockBean == null){
						return returnError("此商品不属于该货位!");
					}
					if(cargoProductStockBean.getStockCount() <= 0){
						return returnError("存货为0不允许装箱!");
					}
					CartonningInfoBean bean =new CartonningInfoBean();
					bean.setCode(code);
					bean.setCreateTime(DateUtil.getNow());
					bean.setStatus(1);
					bean.setName(user.getUsername());
					bean.setCargoId(cargoProductStockBean.getCargoId());
					CartonningProductInfoBean productBean=new CartonningProductInfoBean();
				    productBean.setProductCount(cargoProductStockBean.getStockCount());
					productBean.setProductCode(pBean.getCode());
					productBean.setProductName(pBean.getOriname());
					productBean.setProductId(pBean.getId());
					bean.setProductBean(productBean);
					
					service.getDbOp().startTransaction();  //开启事务
					if(!service.addCartonningInfo(bean)){
						service.getDbOp().rollbackTransaction();
						return returnError("创建装箱单，数据库操作失败!");
					}
					if( !service.fixCartonningInfoCode(code, service.getDbOp(), bean) ) {
						service.getDbOp().rollbackTransaction();
						return returnError("创建装箱单，数据库操作失败!");
					}
					code = bean.getCode();
					
					CartonningInfoBean bean2 = service.getCartonningInfo("code="+"'"+code+"'");
					productBean.setCartonningId(bean2.getId());
					if(!service.addCartonningProductInfo(productBean)){
						service.getDbOp().rollbackTransaction();
						return returnError("创建装箱单，数据库操作失败!");
					}
					
					bean = service.getCartonningInfo("code="+"'"+code+"'");
					productBean = service.getCartonningProductInfo("cartonning_id="+bean.getId());
					bean.setProductBean(productBean);
					voProduct product = wareService.getProduct(productBean.getProductId());
					if(product == null){
						service.getDbOp().rollbackTransaction();
						return returnError("找不到商品信息!");
					}
					voProductLine productLine = wareService.getProductLine("product_line_catalog.catalog_id=" + product.getParentId1()+" or product_line_catalog.catalog_id=" + product.getParentId2());
					if(productLine == null){
						service.getDbOp().rollbackTransaction();
						return returnError("查询不到商品线信息!");
					}
					service.getDbOp().commitTransaction(); //提交事务
					data = new HashMap<String, Object>();
					data.put("productCode", bean.getProductBean().getProductCode());
					data.put("code", bean.getCode());
					data.put("productCount", bean.getProductBean().getProductCount());
					data.put("userName", ((voUser)request.getSession().getAttribute("userView")).getUsername());
					data.put("cartonningTime", bean.getCreateTime().substring(0, 19));
					String result = "作业装箱单:" + code + "创建并打印成功！";
					data.put("result", result);
				}
			} else {
				return returnError("身份验证失败！");
			}
		} catch (Exception e) {
			boolean isAuto = service.getDbOp().getConn().getAutoCommit();
			if( !isAuto ) {
				service.getDbOp().rollbackTransaction();
			}
			e.printStackTrace();
			return returnError("操作异常！");
		} finally {
			service.releaseAll();
		}
		return returnResult(data);
	}
	/**
	 * 说明：作废装箱记录
	 * 
	 * 时间：2013-08-09
	 * 
	 * 作者：张晔
	 */
	@RequestMapping("/dropCartonning")
	@ResponseBody
	public JsonModel dropCartonning(HttpServletRequest request) throws Exception {
		JsonModel json = new JsonModel();
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			//从流中读取json数据
			json = ReceiveJson.receiveJson(request);
			if(json==null){
				return returnError("没有收到请求数据!");
			}
			//验证用户名密码
			if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
				String code = StringUtil.convertNull((String)json.getData().get("cartonningCode"));
				CartonningInfoBean cartonningInfo=cartonningService.getCartonningInfo("code='"+code+"'");
				if(cartonningInfo==null){
					return returnError(code+",没有找到该装箱单！");
				}
				int area = StringUtil.toInt(json.getArea());
				CargoInfoBean cargoInfoBean = service.getCargoInfo("id=" + cartonningInfo.getCargoId());
				if(cargoInfoBean!=null&&area != cargoInfoBean.getAreaId()){
					return returnError("装箱单所关联货位的仓库与选择的仓库不一致!");
				}
				CargoOperationBean cargoOperation = service.getCargoOperation("id=" + cartonningInfo.getOperId());
				if(cargoOperation != null){
					if(cargoOperation.getStatus()!= 7 
						&& cargoOperation.getStatus()!= 8 
						&& cargoOperation.getStatus()!= 9 
						&& cargoOperation.getStatus()!= 16 
						&& cargoOperation.getStatus()!= 17
						&& cargoOperation.getStatus()!= 18 
						&& cargoOperation.getStatus()!= 25 
						&& cargoOperation.getStatus()!= 26 
						&& cargoOperation.getStatus()!= 27 
						&& cargoOperation.getStatus()!= 34 
						&& cargoOperation.getStatus()!= 35 
						&& cargoOperation.getStatus()!= 36){
						return returnError("此装箱单关联的作业单为未完成状态！");
					}
				}
				synchronized (cargoLock) {
					CartonningInfoBean bean = cartonningService.getCartonningInfo("code='" + code + "'");
					if(bean != null){
						cartonningService.updateCartonningInfo("status=2", "code='"+ code + "'");
						return returnResult("装箱单：" + code + "已作废!");
					}else{
						return returnError("没有找到此装箱单！");
					}
				}
			} else {
				return returnError("身份验证失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("操作异常！");
		} finally {
			service.releaseAll();
		}
	}
	/**
	 * 说明：打印装箱单
	 * 
	 * 时间：2013-08-09
	 * 
	 * 作者：张晔
	 */
	@RequestMapping("/printCartonning")
	@ResponseBody
	public JsonModel printCartonning(HttpServletRequest request)
			throws Exception {
		JsonModel json = new JsonModel();
		WareService wareService = new WareService();
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		Map<String, Object> data = null;
		try {
			//从流中读取json数据
			json = ReceiveJson.receiveJson(request);
			if(json==null){
				return returnError("没有收到请求数据!");
			}
			//验证用户名密码
			if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
				synchronized (cargoLock) {
					String code = StringUtil.convertNull((String)json.getData().get("cartonningCode"));
					CartonningInfoBean bean = service.getCartonningInfo("code='" + code + "'");
					if(bean != null){
						int area = StringUtil.toInt(json.getArea());
						CargoInfoBean cargoInfoBean = cargoService.getCargoInfo("id=" + bean.getCargoId());
						if(cargoInfoBean!=null&&area != cargoInfoBean.getAreaId()){
							return returnError("装箱单所关联货位的仓库与选择的仓库不一致!");
						}
						if(bean.getStatus()==CartonningInfoBean.STATUS2){
							return returnError("该装箱单已作废!");
						}
						CartonningProductInfoBean cpib = service.getCartonningProductInfo("cartonning_id=" + bean.getId());
						voProduct product = wareService.getProduct(cpib.getProductId());
						if(product == null){
							return returnError("找不到商品信息,无法打印!");
						}
						voProductLine productLine = wareService.getProductLine("product_line_catalog.catalog_id=" + product.getParentId1()+" or product_line_catalog.catalog_id=" + product.getParentId2());
						if(productLine == null){
							return returnError("查询不到商品线信息,无法打印!");
						}
						CartonningProductInfoBean printProductBean = service.getCartonningProductInfo("cartonning_id=" + bean.getId());
						service.updateCartonningInfo("status=1", "id=" + bean.getId());
						bean.setProductBean(printProductBean);
						data = new HashMap<String, Object>();
						data.put("productCode", bean.getProductBean().getProductCode());
						data.put("code", bean.getCode());
						data.put("productCount", bean.getProductBean().getProductCount());
						data.put("userName", ((voUser)request.getSession().getAttribute("userView")).getUsername());
						data.put("cartonningTime", bean.getCreateTime().substring(0, 19));
						String result = "装箱单:" + code + "打印成功！";
						data.put("result", result);
						
					}else{
						return returnError("没有找到此装箱单,无法打印！");
					}
				}
			} else {
				return returnError("身份验证失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("程序异常!");
		} finally {
			service.releaseAll();
			wareService.releaseAll();
		}
		return returnResult(data);
	}
	
	/**
	 * 说明：查询装箱单
	 * 
	 * 时间：2013-08-12
	 * 
	 * 作者：张晔
	 */
	@RequestMapping("/findCartonningInfo")
	@ResponseBody
	public JsonModel findCartonningInfo(HttpServletRequest request)
			throws Exception {
		JsonModel json = new JsonModel();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			//从流中读取json数据
			json = ReceiveJson.receiveJson(request);
			if(json==null){
				return returnError("没有收到请求数据!");
			}
			//验证用户名密码
			if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
				String code = StringUtil.convertNull((String)json.getData().get("cartonningCode"));
				CartonningInfoBean cartonningBean = service.getCartonningInfo("code='" + code + "'");
				if(cartonningBean == null){
					return returnError("无法找到装箱记录");
				}
				int area =StringUtil.toInt(json.getArea());
				CargoInfoBean cargoInfoBean = cargoService.getCargoInfo("id=" + cartonningBean.getCargoId());
				if(cargoInfoBean == null){
					return returnError("无法找到关联货位信息!");
				}
				if(area != cargoInfoBean.getAreaId()){
					return returnError("装箱单所关联货位的仓库与选择的仓库不一致!");
				}
				CartonningProductInfoBean cartonningProductBean = service.getCartonningProductInfo("cartonning_id="+cartonningBean.getId());
				if(cartonningProductBean == null ){
					return returnError("装箱单商品信息不存在!");
				}
				cartonningBean.setProductBean(cartonningProductBean);
				data.put("SKU", cartonningBean.getProductBean().getProductCode());
				data.put("productCount", cartonningBean.getProductBean().getProductCount());
				data.put("wholeCode", cargoInfoBean.getWholeCode());
				CargoOperationBean cargoOperationBean = cargoService.getCargoOperation("id=" + cartonningBean.getOperId());
				if(cargoOperationBean != null){
					data.put("code", cargoOperationBean.getCode());
					if(cargoOperationBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS7 || 
							cargoOperationBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS8 || 
							cargoOperationBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS16 || 
							cargoOperationBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS17 || 
							cargoOperationBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS25 || 
							cargoOperationBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS26 || 
							cargoOperationBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS34 || 
							cargoOperationBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS35  ) {
						data.put("status", "完成");
					} else {
						data.put("status", "未完成");
					}
					CargoOperationCargoBean cargoOperationCargoBean = cargoService.getCargoOperationCargo("in_cargo_whole_code <> '' and oper_id=" + cargoOperationBean.getId());
					if(cargoOperationCargoBean != null){
						data.put("inCargoWholeCode", cargoOperationCargoBean.getInCargoWholeCode());
					} else {
						data.put("inCargoWholeCode", "未关联目的货位");
					}
				} else {
					data.put("code", "未关联作业单");
					data.put("inCargoWholeCode", "未关联目的货位");
					data.put("status", "未知");
				}
			} else {
				return returnError("身份验证失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("查询装箱单异常！");
		} finally {
			service.releaseAll();
		}
	    return returnResult(data);
		
	}
	/**
	 * 说明：删除装箱单
	 * 
	 * 时间：2013-08-28
	 * 
	 * 作者：石远飞
	 */
	@RequestMapping("/delCartonning")
	@ResponseBody
	public JsonModel delCartonning(HttpServletRequest request)throws Exception {
		JsonModel json = new JsonModel();
		
		CartonningInfoService service = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,null);
		WareService wareService = new WareService(service.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			//从流中读取json数据
			json = ReceiveJson.receiveJson(request);
			if(json==null){
				return returnError("没有收到请求数据!");
			}
			//验证用户名密码
			if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
				voUser user = (voUser) request.getSession().getAttribute("userView");
				int area =StringUtil.toInt(json.getArea());
				String code = StringUtil.convertNull((String)json.getData().get("code"));
				CartonningInfoBean bean = service.getCartonningInfo("code='" + code + "'");
				if(bean != null){
					CargoInfoBean cargoInfoBean = cargoService.getCargoInfo("id=" + bean.getCargoId());
					if(cargoInfoBean!=null&&area != cargoInfoBean.getAreaId()){
						return returnError("装箱单所关联货位的仓库与选择的仓库不一致!");
					}
					CartonningInfoBean ciBean = service.getCartonningInfo("code='"+ code + "'");
					if(ciBean == null){
						return returnError("装箱单[" + code + "不存在!");
					}
					service.getDbOp().startTransaction();//开启事务
					if(service.deleteCartonningInfo("id="+ ciBean.getId())){
						if(!service.deleteCartonningProductInfo("cartonning_id=" + ciBean.getId())){
							service.getDbOp().rollbackTransaction();
							return returnError("删除装箱单商品失败 !");
						}
						//物流员工绩效考核操作
						CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
						if(csBean == null){
							service.getDbOp().rollbackTransaction();
							return returnError("此账号不是物流员工!");
						}
						CartonningProductInfoBean cpiBean = service.getCartonningProductInfo(" cartonning_id=" + bean.getId());
						if(cpiBean != null){
							CargoStaffPerformanceBean cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and type=2 and staff_id=" + csBean.getId() );
							if(cspBean != null){
								int operCount = cspBean.getOperCount() - 1;
								int performanceProductCount = cspBean.getProductCount() - cpiBean.getProductCount();
								boolean flagDel = cargoService.updateCargoStaffPerformance(" oper_count=" + operCount + ", product_count=" + performanceProductCount, " id=" + cspBean.getId());
								if(!flagDel){
									service.getDbOp().rollbackTransaction();
									return returnError("物流员工绩效考核更新操作失败!");
								}
							}
						}
					}else{
						service.getDbOp().rollbackTransaction();
						return returnError("装箱单:" + code + "删除失败!");
					}
					service.getDbOp().commitTransaction(); //提交事务
					returnResult("装箱单:" + code + "成功删除!");
				}else{
					return returnError("没有找到此装箱单!");
				}
			} else {
				return returnError("身份验证失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return returnError("查询装箱单异常！");
		} finally {
			service.releaseAll();
		}
		return returnResult(data);
		
	}
	/**
 	 * 说明：获取物流员工作业效率排名
	 * 
	 * 日期：2013-08-06
	 *
	 * 作者：石远飞
	 */
	@RequestMapping("/getCargoStaffPerformance")
	@ResponseBody
	public JsonModel getCargoStaffPerformance(HttpServletRequest request){
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		JsonModel json = new JsonModel();
		try {
			//从流中读取json数据
			json = ReceiveJson.receiveJson(request);
			if(json==null){
				return returnError("没有收到请求数据!");
			}
			//验证用户名密码
			if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){
				voUser user = (voUser)request.getSession().getAttribute("userView");
				int type = (Integer)json.getData().get("flag");
				String firstCount = "";
				String oneselfCount = "";
				String ranking ="";
				String productCount="";
				String firstProductCount="";
				int n = 1;
				CargoStaffPerformanceBean cspBean = null;
				List<CargoStaffPerformanceBean> cspList = null;
				CargoStaffBean csBean = cargoService.getCargoStaff(" user_id=" + user.getId());
				if(csBean == null){
					return returnError("此账号不是物流员工!");
				}
				cspBean = cargoService.getCargoStaffPerformance(" date='" + date + "' and staff_id=" + csBean.getId() + " and type=" + type);
				cspList = cargoService.getCargoStaffPerformanceList(" date='" + date + "' and type=" + type, -1, -1, " oper_count DESC");
				if(cspBean != null){
					for(int i = 0;i < cspList.size();i++){
						CargoStaffPerformanceBean bean = new CargoStaffPerformanceBean();
						bean = cspList.get(i);
						if(i==0){
							firstCount = bean.getOperCount() + "";
							firstProductCount = bean.getProductCount() + "";
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
						firstCount = bean.getOperCount() + "";
						firstProductCount = bean.getProductCount()+"";
						ranking = "尚无名次";
						oneselfCount = "0";
						productCount = "0";
					}else{
						firstCount = "0";
						ranking = "尚无名次";
						oneselfCount = "0";
						productCount = "0";
						firstProductCount = "0";
					}
				}
				StringBuilder result = new StringBuilder();
				if(type == 2){
					result.append("你已装" + oneselfCount + "箱,商品" + productCount + "件," + ranking + ",冠军" + firstCount + "箱," + firstProductCount + "件~") ;
				}else if(type == 3){
					result.append("你已上架" + oneselfCount + "箱,商品" + productCount + "件," + ranking + ",冠军" + firstCount + "箱," + firstProductCount + "件~") ;
				}else if(type == 5){
					result.append("你已完成" + oneselfCount + "箱,商品" + productCount + "件," + ranking + ",冠军" + firstCount + "箱," + firstProductCount + "件~") ;
				}
				
				return returnResult(result.toString());
			}else{
				return returnError("身份验证失败!");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbSlave.release();
		}
		return json;
	}
	/**
	 * 商品查询
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/searchProduct")
	@ResponseBody
	public JsonModel searchProduct(HttpServletRequest request) {
		JsonModel jsonModel = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			//从流中读取json数据
			jsonModel = ReceiveJson.receiveJson(request);
			if(jsonModel==null){
				return returnError("没有收到请求数据!");
			}
			int area = StringUtil.toInt(jsonModel.getArea());//地区id
			//判断当前时间是不是盘点时间
			List<Map<String,String>> dynamicCheckCycle = bService.getDynamicCheckCycle(area);
			if (isCheckCycle(dynamicCheckCycle)) {
				return returnError("该功能在盘点后开放!");
			};
			//验证用户名密码
			if(CheckUser.checkUser(request,jsonModel.getUserName(), jsonModel.getPassword())){
				String code = (String)jsonModel.getData().get("code");//商品编号或条码，或者货位号
				code = StringUtil.toSql(code);
				if(!code.equals("")){
					String result="";//在页面显示的文本信息
					CargoInfoBean ciBean=cargoService.getCargoInfo("whole_code='"+code+"'");
					if(ciBean!=null){//扫描的编号是货位号
						if(ciBean.getAreaId()!=area){
							jsonModel.setMessage("货位地区错误");
							jsonModel.setFlag(0);
							jsonModel.setData(null);
							return jsonModel;
						}
						List cpsList=cargoService.getCargoProductStockList("cargo_id="+ciBean.getId()+" and (stock_count>0 or stock_lock_count>0)", -1, -1, null);
						for(int i=0;i<cpsList.size();i++){
							CargoProductStockBean cpsBean=(CargoProductStockBean)cpsList.get(i);
							int productId=cpsBean.getProductId();
							voProduct product=wareService.getProduct(productId);
							if(product!=null){
								result+=product.getCode();
								result+=",可用";
								result+=cpsBean.getStockCount();
								result+=",锁";
								result+=cpsBean.getStockLockCount();
								result+=";\r\n";
							}
						}
					}else{//查询商品
						voProduct product=null;
						ProductBarcodeVO bBean = bService.getProductBarcode("barcode="+"'"+code+"'");
						if(bBean == null){
							product = wareService.getProduct(code);
						}else{
							product = wareService.getProduct(bBean.getProductId());
						}
						if(product==null){
							return returnError("未查询到相关信息!");
						}
						List cpsList=cargoService.getCargoAndProductStockList("cps.product_id="+product.getId()+" and ci.area_id="+area+" and ci.stock_type=0 and (cps.stock_count>0 or cps.stock_lock_count>0)", -1, -1, null);
						for(int i=0;i<cpsList.size();i++){
							CargoProductStockBean cpsBean=(CargoProductStockBean)cpsList.get(i);
							CargoInfoBean tempCi=cpsBean.getCargoInfo();
							if(tempCi!=null){
								result+=tempCi.getWholeCode();
								result+=",可用";
								result+=cpsBean.getStockCount();
								result+=",锁";
								result+=cpsBean.getStockLockCount();
								result+=";\r\n";
							}
						}
					}
					if(result.equals("")){
						return returnError("未查询到相关信息!");
					}
					return returnResult(result);
				}
			}else{
				return returnError("身份验证失败!");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return jsonModel;
	}
	/** 
	 * @Description: 
	 * @return void 返回类型 
	 * @author 叶二鹏
	 * @date 2015年7月2日 上午9:40:02 
	 */
	private boolean isCheckCycle(List<Map<String, String>> dynamicCheckCycle)
			throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date now = new Date();
		String year = DateUtil.getYear(DateUtil.getNowDateStr())+"";
		for (Map<String,String> mp : dynamicCheckCycle) {
			String[] dateRang = mp.get("check_date_rang").split(",");
			String time_rang = mp.get("check_time_rang");
			String[] timeRang = null;
			//如果时间范围没有，按全天算
			if (StringUtils.isBlank(time_rang)) {
				timeRang = new String[]{"00:00:00~23:59:59"};
			} else {
				timeRang = mp.get("check_time_rang").split(",");
			}
			for (String d : dateRang) {
				String[] date = d.split("~");
				for (String t : timeRang) {
					String[] time = t.split("~");
					if (time.length == 2 && date.length == 2) {//当前时间在设置的时间范围内，不容许查询
						int daySub = DateUtil.getDaySub(year+"-"+date[0], year+"-"+date[1]);
						for (int i = 0; i <= daySub; i++) {//循环日期段内的每一天
							Date bd = sdf.parse(year+"-"+date[0] + " " +time[0]);
							Date ed = sdf.parse(year+"-"+date[0] + " " +time[1]);
							bd = DateUtils.addDays(bd, i);
							ed = DateUtils.addDays(ed, i);
							if (now.getTime() >= bd.getTime()
									&& now.getTime() <= ed.getTime()){
								return true;
							}
						}
					} else if (time.length == 2 && date.length == 1) {//当前时间在设置的时间范围内，不容许查询
						if (now.getTime() >= sdf.parse(year+"-"+date[0] + " " +time[0]).getTime()
								&& now.getTime() <= sdf.parse(year+"-"+date[0] + " " +time[1]).getTime()){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	/**
 	 * 说明：返回错误信息
	 * 
	 * 日期：2013-08-06
	 *
	 * 作者：石远飞
	 */
	private JsonModel returnError(String err){
		JsonModel json = new JsonModel();
		json.setFlag(0);
		json.setData(null);
		json.setMessage(err);
		return json;
	}
	/**
	 * 说明：返回错误信息
	 * 
	 * 日期：2013-08-29
	 *
	 * 作者：石远飞
	 */
	private JsonModel returnError(String err,int flag){
		JsonModel json = new JsonModel();
		json.setFlag(flag);
		json.setData(null);
		json.setMessage(err);
		return json;
	}
	/**
 	 * 说明：返回结果信息
	 * 
	 * 日期：2013-08-06
	 *
	 * 作者：石远飞
	 */
	private JsonModel returnResult(String result){
		JsonModel json = new JsonModel();
		Map<String,Object> data = new HashMap<String, Object>();
		data.put("result", result);
		json.setFlag(1);
		json.setData(data);
		json.setMessage("操作成功!");
		return json;
	}
	
	/**
 	 * 说明：返回结果信息（打印用）
	 * 
	 * 日期：2013-08-09
	 *
	 * 作者：张晔
	 */
	private JsonModel returnResult(Map<String, Object> data){
		JsonModel json = new JsonModel();
		json.setData(data);
		json.setFlag(1);
		json.setData(data);
		json.setMessage("操作成功!");
		return json;
	}
	/**
	 * 说明：验证该退货上架单的目的货位是否和选择的库地区匹配
	 * 
	 * 日期：2013-08-09
	 * 
	 * 作者：石远飞
	 */
	@SuppressWarnings("unchecked")
	public boolean pdaCheckWholeOperation(ICargoService cargoService, List<CargoOperationCargoBean> cocBeanList, int area){
		boolean result = true;
		for(CargoOperationCargoBean bean : cocBeanList){
			List<CargoInfoBean> ciBeanList = cargoService.getCargoInfoList("whole_code='"+bean.getInCargoWholeCode()+"'", -1, -1, null);
			if(ciBeanList != null && !ciBeanList.isEmpty()){
				for(CargoInfoBean ciBean : ciBeanList){
					if(area != ciBean.getAreaId()){
						result = false;
						break;
					}
				}
			}
		}
		
		return result;
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void updateLackOrder(int productId){
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		DbOperation dbOp2 = new DbOperation();
		dbOp2.init();
		WareService wareService = new WareService(dbOp);
		IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String productIds = productId+"";
			//查询父商品
			List ppList = ppService.getProductPackageList("product_id=" + productId, -1, -1, null);
			Iterator ppIter = ppList.listIterator();
			while(ppIter.hasNext()){
				ProductPackageBean ppBean = (ProductPackageBean)ppIter.next();
				productIds = productIds + "," + ppBean.getParentId();
			}

			List lackOrders = wareService.getOrdersByProducts("a.stockout_deal in (4,5,6) and b.product_id in ("+productIds+")", -1, -1, null);
			lackOrders.addAll(wareService.getOrdersByPresents("a.stockout_deal in (4,5,6) and b.product_id in ("+productIds+")", -1, -1, null));
			Iterator iter = lackOrders.listIterator();
			while(iter.hasNext()){
				voOrder order = (voOrder)iter.next();

				// 判断订单中商品的库存是否满足，根据库存状态，设置订单发货状态
				List orderProductList = wareService.getOrderProducts(order.getId());
				List orderPresentList = wareService.getOrderPresents(order.getId());
				orderProductList.addAll(orderPresentList);

				List detailList = new ArrayList();
				Iterator detailIter = orderProductList.listIterator();
				while (detailIter.hasNext()) {
					voOrderProduct vop = (voOrderProduct) detailIter.next();
					voProduct product = wareService.getProduct(vop.getProductId());
					if (product.getIsPackage() == 1) { // 如果这个产品是套装
						ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						ppIter = ppList.listIterator();
						while (ppIter.hasNext()) {
							ProductPackageBean ppBean = (ProductPackageBean) ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
							voProduct tempProduct = wareService.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							tempVOP.setPsList(service.getProductStockList("product_id=" + tempVOP.getProductId(), -1, -1, null));
							detailList.add(tempVOP);
						}
					} else {
						vop.setPsList(service.getProductStockList("product_id=" + vop.getProductId(), -1, -1, null));
						detailList.add(vop);
					}
				}
				orderProductList = detailList;

				if (checkStock(orderProductList,ProductStockBean.AREA_GF) || checkStock(orderProductList,ProductStockBean.AREA_ZC)) {
					updateOrderLackStatu(dbOp2,order.getId());
				}

			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
			dbOp2.release();
		}

	}
	@SuppressWarnings("rawtypes")
	public boolean checkStock(List orderProductList,int area) {
		if (orderProductList == null) {
			return false;
		}
		Iterator itr = orderProductList.iterator();
		boolean result = true;
		voOrderProduct op = null;
		while (itr.hasNext()) {
			op = (voOrderProduct) itr.next();
			if (op.getStock(ProductStockBean.STOCKTYPE_QUALIFIED,area) < op.getCount()) {
				result = false;
				return result;
			}
		}

		return result;
	}
	/**
	 * 作者：石远飞
	 * 
	 * 日期：2013-8-29
	 * 
	 * 说明：扫描的货位是否和验证该退货上架单的目的货位匹配
	 * 0失败  1货位一致 2货位不一致但可分给所选货位
	 */
	public int pdaCheckInWholeOperation(List<CargoOperationCargoBean> cocBeanList, String wholeCode, UserGroupBean group,ICargoService cargoService, int area){
		int result = 0;
		for(CargoOperationCargoBean bean : cocBeanList){
			if(bean.getInCargoWholeCode().equalsIgnoreCase(wholeCode)){
				result = 1;
				return result;
			}
		}
		
		if (group.isFlag(3066)) {
			CargoServiceImpl csi = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE, cargoService.getDbOp());
			WareService wareService = new WareService(cargoService.getDbOp());
			for(CargoOperationCargoBean bean : cocBeanList){
				voProduct product = wareService.getProduct(bean.getProductId());
				if( area == ProductStockBean.AREA_ZC ) {
					List list = csi.getListZC(product.getParent1().getName(), product.getCode());
					String returnrs = csi.getMatchPassageIds("GZZ01",area, (List<HashMap<String, String>>)list.get(1), (String)list.get(0));
					if("".equals(returnrs)){
						result = 0;
						break;
					}
					
					String sql = "select cps.product_id, ci.*  from cargo_info ci, cargo_product_stock cps where ci.id = cps.cargo_id and ci.area_id = "+ area
							+ " and ci.stock_type = 0 and ci.status = " + CargoInfoBean.STATUS0 + " and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") and cps.product_id = "
							+ product.getId() + " and " + returnrs + " and ci.whole_code='" + bean.getInCargoWholeCode() + "' and (cps.stock_count + cps.stock_lock_count) > 0 ";
					ResultSet rs = wareService.getDbOp().executeQuery(sql);
					try {
						if (rs.next()) {
							result = 0;
							rs.close();
							break;
						}
						rs.close();
						CargoInfoBean ci = cargoService.getCargoInfo("area_id = "+ area
							+ " and stock_type = 0 and status in (" + CargoInfoBean.STATUS0 + "," + CargoInfoBean.STATUS1 + ") and store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") "
							+ " and " + returnrs + " and whole_code='" + wholeCode + "'");
						if (ci != null) {
							result =2;
							break;
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						result = 0;
						break;
					} 
				} else if( area == ProductStockBean.AREA_CD ) {
					List list = csi.getListCD(product.getParent1().getName(), product.getCode());
					String returnrs = csi.getMatchPassageIds("SCC01",area, (List<HashMap<String, String>>)list.get(1), (String)list.get(0));
					if("".equals(returnrs)){
						result = 0;
						break;
					}
					
					String sql = "select cps.product_id, ci.*  from cargo_info ci, cargo_product_stock cps where ci.id = cps.cargo_id and ci.area_id = "+ area
							+ " and ci.stock_type = 0 and ci.status = " + CargoInfoBean.STATUS0 + " and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") and cps.product_id = "
							+ product.getId() + " and " + returnrs + " and ci.whole_code='" + bean.getInCargoWholeCode() + "' and (cps.stock_count + cps.stock_lock_count) > 0 ";
					ResultSet rs = wareService.getDbOp().executeQuery(sql);
					try {
						if (rs.next()) {
							result = 0;
							rs.close();
							break;
						}
						rs.close();
						CargoInfoBean ci = cargoService.getCargoInfo("area_id = "+ area
							+ " and stock_type = 0 and status in (" + CargoInfoBean.STATUS0 + "," + CargoInfoBean.STATUS1 + ") and store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") "
							+ " and " + returnrs + " and whole_code='" + wholeCode + "'");
						if (ci != null) {
							result =2;
							break;
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						result = 0;
						break;
					} 
				} else {
					String sql = "select cps.product_id, ci.*  from cargo_info ci, cargo_product_stock cps where ci.id = cps.cargo_id and ci.area_id = "+ area
							+ " and ci.stock_type = 0 and ci.status = " + CargoInfoBean.STATUS0 + " and ci.store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") and cps.product_id = "
							+ product.getId() + " and ci.whole_code not like 'GZZ01-C%'  and ci.whole_code='" + bean.getInCargoWholeCode() + "' and (cps.stock_count + cps.stock_lock_count) > 0 ";
					ResultSet rs = wareService.getDbOp().executeQuery(sql);
					try {
						if (rs.next()) {
							result = 0;
							rs.close();
							break;
						} 
						rs.close();
						CargoInfoBean ci = cargoService.getCargoInfo("area_id = "+ area
							+ " and stock_type = 0 and status in (" + CargoInfoBean.STATUS0 + "," + CargoInfoBean.STATUS1 + ") and store_type in ("+CargoInfoBean.STORE_TYPE0+","+CargoInfoBean.STORE_TYPE4+") "
							+ " and whole_code not like 'GZZ01-C%' and whole_code='" + wholeCode + "'");
						if (ci != null) {
							result =2;
							break;
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						result = 0;
						break;
					} 
				}
			}
		}
		
		return result;
	}
	/**
	 * 功能:缺货补货 时间判断 
	 * @param dbOp
	 * @param orderId
	 */
	public static void updateOrderLackStatu(DbOperation dbOp ,int orderId){
		dbOp.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 0,uold.stockout_deal = 0,uold.next_deal_datetime = null " +
				"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') <= 3600 and uo.is_olduser=1 and uo.id = "+orderId);
		dbOp.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 0,uold.stockout_deal = 0,uold.next_deal_datetime = null " +
				"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') <= 7200 and uo.id = "+orderId);
		dbOp.executeUpdate("update user_order uo,user_order_lack_deal uold,user_order_product uop set uo.stockout_deal = 7,uold.stockout_deal = 7,uold.next_deal_datetime = null " +
				"where uo.id = uold.id and uo.id = uop.order_id and uo.stockout_deal in (4,5,6) and TIMESTAMPDIFF(SECOND, uold.lack_datetime, '"+DateUtil.getNow()+"') > 7200 and uo.id = "+orderId);
	}

	private boolean insertBISortingAbnormalOrder(int areaId, String code, DbOperation db){
		String date = DateUtil.getNowDateStr();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT COUNT(*) FROM bi_sorting_abnormal_order WHERE ");
		sb.append(" area_id = ").append(areaId);
		sb.append(" AND datetime = '").append(date).append("' ");
		sb.append(" AND code = '").append(code).append("' ");
		
		if(db.getInt(sb.toString()) == 0){
			sb.setLength(0);
			sb.append(" INSERT INTO bi_sorting_abnormal_order ");
			sb.append(" (area_id, datetime, code ) VALUES ");
			sb.append(" ( ").append(areaId).append(" , '").append(date).append("' , '").append(code).append("' ) ");
						
			if(!db.executeUpdate(sb.toString())){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 获取快递公司列表
	 * @param request
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/getGeliverListForMailingBatch")
	@ResponseBody
	public JsonModel getGeliverListForMailingBatch(HttpServletRequest request){
		
		PDAUtil pdaUtil = new PDAUtil();		
		JsonModel json = pdaUtil.getModelAndCheck(request, -1);
		if(json.getFlag() == 0){
			return json;
		}
		
		List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		for (DeliverCorpInfoBean bean : voOrder.deliverInfoMapAll.values()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("id", Integer.toString(bean.getId()));
			map.put("content", bean.getName());
			list.add(map);
		}
		
		return JsonModelUtil.success("deliverList", list);
	}
	
	
	/**
	 * 新建发货波次
	 * @param request
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/addMailingBatch")
	@ResponseBody
	public JsonModel addMailingBatch(HttpServletRequest request){
		
		PDAUtil pdaUtil = new PDAUtil();		
		JsonModel json = pdaUtil.getModelAndCheck(request, -1);
		if(json.getFlag() == 0){
			return json;
		}
		voUser user = pdaUtil.getUser();		
		int deliver = JsonModelUtil.getInt(json, "deliverID");
		int area = StringUtil.StringToId(json.getArea());
		if(area == -1){
			return JsonModelUtil.error("请选择仓库");
		}
		if(deliver == -1){
			return JsonModelUtil.error("请输入快递公司id");
		}
		
		if (!voOrder.deliverInfoMapAll.containsKey(Integer.valueOf(deliver))) {
			return JsonModelUtil.error("请输入有效的快递公司id");
		}
		String carrier = ((DeliverCorpInfoBean)voOrder.deliverInfoMapAll.get(Integer.valueOf(deliver))).getName();
		
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, null);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		CargoDeptBean cdBean = null;
		try {
			// 仓库代码
			CargoStaffBean csBean = cargoService.getCargoStaff("status=0 and user_id=" + user.getId());
			if (csBean != null) {
				cdBean = cargoService.getCargoDept("id=" + csBean.getDeptId());
			}
			if (cdBean == null) {
				return JsonModelUtil.error("您不能进行添加操作！");
			}

			CargoDeptBean deptBean = cargoService.getCargoDept("id="+cdBean.getParentId0());
			String storageCode = CargoDeptBean.storeMap.get(deptBean.getCode()).toString();
			// 发货波次号
			String code = "SW" + DateUtil.getNow().substring(2, 4)+DateUtil.getNow().substring(5, 7)+DateUtil.getNow().substring(8, 10)+storageCode;
			// 计算位序号
			int maxid = service.getNumber("id", "mailing_batch", "max", "id > 0 and code like '" + code +"%'");
			MailingBatchBean lastMailingBatch = service.getMailingBatch("code like '" + code + "%'");
			if(lastMailingBatch == null){
				//当日第一份单据，编号最后三位 001
				code += "001";
			}else {
				//获取当日计划编号最大值
				lastMailingBatch = service.getMailingBatch("id =" + maxid); 
				String _code = lastMailingBatch.getCode();
				int number = Integer.parseInt(_code.substring(_code.length()-3));
					if(number==999){
						return JsonModelUtil.error("当天波次最大数不能超过999！");
					}
				number++;
				code+= String.format("%03d",new Object[]{new Integer(number)});
			}
			
			service.getDbOp().startTransaction();
			
			MailingBatchBean bean = new MailingBatchBean();
			bean.setCode(code);
			bean.setCreateDatetime(DateUtil.getNow());
			bean.setDeliver(deliver);
			bean.setCreateAdminId(user.getId());
			bean.setCreateAdminName(user.getUsername());
			if(carrier!=null && !carrier.equals("选填")){
				bean.setCarrier(carrier);
			}
			bean.setStatus(0);
			bean.setStore(cdBean.getName());
			bean.setArea(area);
			if(!service.addMailingBatch(bean)){
				service.getDbOp().rollbackTransaction();
				return JsonModelUtil.error("创建波次失败！");
			}
			bean.setId(service.getDbOp().getLastInsertId());

			MailingBatchParcelBean parcel = new MailingBatchParcelBean();
			parcel.setMailingBatchId(bean.getId());
			parcel.setMailingBatchCode(bean.getCode());
			parcel.setCode(bean.getCode() + "01");				
			if(!service.addMailingBatchParcel(parcel)){
				service.getDbOp().rollbackTransaction();
				return JsonModelUtil.error("创建邮包失败!");
			}

			service.getDbOp().commitTransaction();
			JsonModel result = JsonModelUtil.success("code", bean.getCode());
			result.getData().put("parcelCode", parcel.getCode());
			return result;		
		
		} catch (Exception e) {
			e.printStackTrace();
			service.getDbOp().rollbackTransaction();
			return JsonModelUtil.error("发生异常");
		} finally {
			service.releaseAll();
		}
	}

	
	/**
	 * 扫描/切换发货波次
	 * @param request
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/scanMailingCode")
	@ResponseBody
	public JsonModel scanMailingCode(HttpServletRequest request){

		PDAUtil pdaUtil = new PDAUtil();		
		JsonModel json = pdaUtil.getModelAndCheck(request, -1);
		if(json.getFlag() == 0){
			return json;
		}
		int area = StringUtil.toInt(json.getArea()); //地区id
		String batchCode = JsonModelUtil.getString(json, "batchCode"); //发货波次编号
		
		DbOperation dbOpSlave = new DbOperation();		
		dbOpSlave.init(DbOperation.DB_SLAVE);
		IStockService stockSlaveService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOpSlave);
		
		try{ 
			MailingBatchBean mbBean = stockSlaveService.getMailingBatch(" code='" + StringUtil.toSql(batchCode) + "'");
			if(mbBean == null){				
				return JsonModelUtil.error("未找到此发货波次!");
			}
			if(area != mbBean.getArea()){
				return JsonModelUtil.error("发货波次与选择的仓库不一致!");
			}
			if(mbBean.getStatus()==1){
				return JsonModelUtil.error("发货波次已出库!");
			}
			if(mbBean.getStatus()==2){
				return JsonModelUtil.error("发货波次已完成交接!");
			}

			MailingBatchParcelBean parcel = stockSlaveService.getMailingBatchParcel(" mailing_batch_code='" + batchCode +"' ORDER BY id DESC LIMIT 1 " );
			if(parcel == null){
				return JsonModelUtil.error("该发货波次中没有邮包\r\n请使用旧订单出库功能完成出库");
			}
			
			int packageCount = stockSlaveService.getMailingBatchPackageCount("mailing_batch_id=" + mbBean.getId());
			
			if (!voOrder.deliverInfoMapAll.containsKey(Integer.valueOf(mbBean.getDeliver()))) {
				return JsonModelUtil.error("请输入有效的快递公司id");
			}
			String deliverName = ((DeliverCorpInfoBean)voOrder.deliverInfoMapAll.get(Integer.valueOf(mbBean.getDeliver()))).getName();
			
			JsonModel result = JsonModelUtil.success("parcelCode", parcel.getCode());
			result.getData().put("count", Integer.toString(packageCount));
			result.getData().put("deliverName", deliverName);
			return result;
		}catch(Exception ex){
			ex.printStackTrace();
			return JsonModelUtil.error("发生异常");
		}finally{
			dbOpSlave.release();
		}
	}
	
	
	/**
	 * 获取下架单信息
	 * PDA下架 第一次扫描货位号
	 * @param request
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/getDownShelfInfo")
	@ResponseBody
	public JsonModel getDownShelfInfo(HttpServletRequest request){
		
		PDAUtil pdaUtil = new PDAUtil();		
		JsonModel json = pdaUtil.getModelAndCheck(request, -1);
		if(json.getFlag() == 0){
			return json;
		}

		voUser user = pdaUtil.getUser();
		String cargo = JsonModelUtil.getString(json, "cargoCode");
		if (StringUtil.isEmpty(cargo)) {
			return JsonModelUtil.error("请扫描货位号");
		}
		int areaId = StringUtil.toInt(json.getArea());
		if(areaId <= -1){
			return JsonModelUtil.error("请先选择作业地区");
		}
		
		synchronized (cargoLock) {		
			DbOperation db = new DbOperation();
			db.init(DbOperation.DB);
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, db);
			try {
				CargoInfoBean outCiBean = service.getCargoInfo(" whole_code = '" + cargo + "' ");
				if(outCiBean== null){
					return JsonModelUtil.error("货位不存在");
				} 
				
				if(outCiBean.getAreaId() != areaId){
					return JsonModelUtil.error("货位不在当前作业地区");
				}
				
				if(outCiBean.getStockType() != CargoInfoBean.STOCKTYPE_QUALIFIED){
					return JsonModelUtil.error("请扫描合格库货位");
				}
				
				if(!(outCiBean.getStoreType() == CargoInfoBean.STORE_TYPE4 
						|| outCiBean.getStoreType() == CargoInfoBean.STORE_TYPE0
						|| outCiBean.getStoreType() == CargoInfoBean.STORE_TYPE1))
				{
					return JsonModelUtil.error("源货位存放类型只能为整件区、散件区和混合区");
				}
	
				StringBuilder sb = new StringBuilder();
				sb.append(" create_user_id = ").append(user.getId());
				sb.append(" AND  status = ").append(CargoOperationProcessBean.OPERATION_STATUS12);
				CargoOperationBean coBean = service.getCargoOperation(sb.toString());
				
				if (coBean == null) {
					String code = "HWX"+DateUtil.getNow().substring(2,10).replace("-", "");   
					//生成编号
					CargoOperationBean cargoOper = service.getCargoOperation("code like '"+code+"%' order by id desc limit 1");
					if(cargoOper == null){
						code = code + "00001";
					}else{//获取当日计划编号最大值
						String _code = cargoOper.getCode();
						int number = Integer.parseInt(_code.substring(_code.length()-5));
						number++;
						code += String.format("%05d",new Object[]{new Integer(number)});
					}
					//areaId=1为芳村即：GZF areaId=3为增城即：GZZ
					String storageCode = "";
					if(areaId == ProductStockBean.AREA_GF){
						storageCode ="GZF";
					}else if(areaId == ProductStockBean.AREA_ZC){
						storageCode ="GZZ";
					} else if ( areaId == ProductStockBean.AREA_WX) {
						storageCode = "JSW";
					} else if( areaId == ProductStockBean.AREA_GS) {
						storageCode = "GZS";
					} else if( areaId == ProductStockBean.AREA_BJ) {
						storageCode = "BJA";
					}
					//添加作业单
					cargoOper = new CargoOperationBean();
					cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS12);
					cargoOper.setCreateDatetime(DateUtil.getNow());
					cargoOper.setRemark("");
					cargoOper.setCreateUserId(user.getId());
					cargoOper.setAuditingDatetime(DateUtil.getNow());
					cargoOper.setAuditingUserId(user.getId());
					cargoOper.setCode(code);
					cargoOper.setSource("");
					cargoOper.setStorageCode(storageCode);
					cargoOper.setStockInType(CargoInfoBean.STORE_TYPE7);
					cargoOper.setStockOutType(outCiBean.getStoreType());
					cargoOper.setCreateUserName(user.getUsername());
					cargoOper.setAuditingUserName(user.getUsername());
					cargoOper.setType(CargoOperationBean.TYPE1);
					cargoOper.setLastOperateDatetime(DateUtil.getNow());
					cargoOper.setConfirmDatetime(DateUtil.getNow());
					 
					if(!service.addCargoOperation(cargoOper)){
						return returnError("添加下架单，数据库操作失败!");
					}
					int tempId = service.getDbOp().getLastInsertId();
					cargoOper.setId(tempId);
					
					coBean = cargoOper;
				} 

				sb.setLength(0);
				sb.append(" oper_id = ").append(coBean.getId());
				sb.append(" AND type = ").append(0);
				
				CargoOperationCargoBean cocBean = service.getCargoOperationCargo(sb.toString());
				if(cocBean != null){
					if (!outCiBean.getWholeCode().equalsIgnoreCase(cocBean.getOutCargoWholeCode())) {
						return JsonModelUtil.error("您有一个未完成的下架任务\r\n请先完成下架\r\n货位号为：" + cocBean.getOutCargoWholeCode());
					}
				}else{
					cocBean = new CargoOperationCargoBean();
				}

				JsonModel result = JsonModelUtil.success("operId", coBean.getId());
				result.getData().put("count", cocBean.getStockCount());
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				return JsonModelUtil.error("发生异常");
			}
			finally{
				db.release();
			}	
		}
	}
	
	/**
	 * 添加下架商品
	 * @param request
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/addDownShelfProduct")
	@ResponseBody
	public JsonModel addDownShelfProduct(HttpServletRequest request){
		
		PDAUtil pdaUtil = new PDAUtil();		
		JsonModel json = pdaUtil.getModelAndCheck(request, -1);
		if(json.getFlag() == 0){
			return json;
		}

		voUser user = pdaUtil.getUser();
		String cargo = JsonModelUtil.getString(json, "cargoCode");
		if (StringUtil.isEmpty(cargo)) {
			return JsonModelUtil.error("请扫描货位号");
		}
		int operId = JsonModelUtil.getInt(json, "operId");
		if (operId <= 0) {
			return JsonModelUtil.error("缺少下架单参数");
		}
		String barCode = JsonModelUtil.getString(json, "productCode");
		if (StringUtil.isEmpty(barCode)) {
			return JsonModelUtil.error("请扫描商品条码");
		}
		int count = JsonModelUtil.getInt(json, "count");
		if (count <= 0) {
			return JsonModelUtil.error("缺少商品数量参数");
		}
		int areaId = StringUtil.toInt(json.getArea());
		if(areaId <= -1){
			return JsonModelUtil.error("请先选择作业地区");
		}
		
		synchronized (cargoLock) {		
			DbOperation db = new DbOperation();
			db.init(DbOperation.DB);		
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, db);
			
			try {
				CargoInfoBean outCiBean = service.getCargoInfo(" whole_code = '" + cargo + "' ");
				if(outCiBean== null){
					return JsonModelUtil.error("货位不存在");
				} 
				
				if(outCiBean.getAreaId() != areaId){
					return JsonModelUtil.error("货位不在当前作业地区");
				}
				
				if(outCiBean.getStockType() != CargoInfoBean.STOCKTYPE_QUALIFIED){
					return JsonModelUtil.error("请扫描合格库货位");
				}
				
				if(!(outCiBean.getStoreType() == CargoInfoBean.STORE_TYPE4 
						|| outCiBean.getStoreType() == CargoInfoBean.STORE_TYPE0
						|| outCiBean.getStoreType() == CargoInfoBean.STORE_TYPE1))
				{
					return JsonModelUtil.error("源货位存放类型只能为整件区、散件区和混合区");
				}
				
				int productId = db.getInt(" SELECT product_id FROM product_barcode WHERE barcode ='" + barCode + "' ");
				if(productId <= 0){
					voProduct tmpProduct = new WareService(db).getProduct(barCode);
					if(tmpProduct == null)
						return JsonModelUtil.error("商品条码不存在");
					productId = tmpProduct.getId();
				}

				StringBuilder sb = new StringBuilder();
				sb.append(" id = ").append(operId);
				sb.append(" AND create_user_id = ").append(user.getId());
				sb.append(" AND status = ").append(CargoOperationProcessBean.OPERATION_STATUS12);				
				CargoOperationBean coBean = service.getCargoOperation(sb.toString());				
				if (coBean == null) {
					 return JsonModelUtil.error("下架单不存在或下架单已作业完成");
				}

				sb.setLength(0);
				sb.append(" cargo_id = ").append(outCiBean.getId());
				sb.append(" AND product_id = ").append(productId);				
				CargoProductStockBean outCpsBean = service.getCargoProductStock(sb.toString());
				if (outCpsBean == null) {
					return JsonModelUtil.error("源货位库存不足");		 
				}
				
				// 开启事务  
				db.startTransaction();
				
				sb.setLength(0);
				sb.append(" oper_id = ").append(coBean.getId()).append(" LIMIT 1 ");				
				CargoOperationCargoBean cocBean = service.getCargoOperationCargo(sb.toString());
				if(cocBean != null){					
					// 判断货位是否一致
					if (!outCiBean.getWholeCode().equalsIgnoreCase(cocBean.getOutCargoWholeCode())) {
						db.rollbackTransaction();
						return JsonModelUtil.error("您有一个未完成的下架任务\r\n请先完成下架\r\n货位号为：" + cocBean.getOutCargoWholeCode());
					}
					
					// 判断商品id是否一致
					if(cocBean.getProductId() != productId){						
						voProduct tempProduct = new WareService(db).getProduct(cocBean.productId);
						if (tempProduct != null) {
							db.rollbackTransaction();
							return JsonModelUtil.error("您有一个未完成的下架任务\r\n请先完成下架\r\n商品编号为：" + tempProduct.getCode()); 
						}
						
						db.rollbackTransaction();
						return JsonModelUtil.error("您有一个未完成的下架任务\r\n请先完成下架\r\n商品id为：" + cocBean.productId);
					}					
				}else{
					
					// 防止PDA中断操作造成类型不一致
					if(coBean.getStockOutType() != outCiBean.getStoreType()){
						if(!service.updateCargoOperation(" stock_out_type = " + outCiBean.getStoreType(), " id = " + coBean.getId())){
							db.rollbackTransaction();
							return JsonModelUtil.error("数据库操作失败");
						}
					}
					
					sb.setLength(0);
					sb.append(" area_id = ").append(outCiBean.getAreaId());
					sb.append(" AND stock_type = ").append(outCiBean.getStockType());
					sb.append(" AND store_type = ").append(CargoInfoBean.STORE_TYPE7);
					sb.append(" AND status IN (0, 1) ");
					CargoInfoBean inCiBean = service.getCargoInfo(sb.toString());
					if (inCiBean == null) {
						db.rollbackTransaction();
						return JsonModelUtil.error("未查询到下架区货位");
					}
					
					if (inCiBean.getStatus() == CargoInfoBean.STATUS1) {
						if (!service.updateCargoInfo(" status = " + CargoInfoBean.STATUS0, " id = " + inCiBean.getId())) {
							db.rollbackTransaction();
							return JsonModelUtil.error("数据库操作失败:更新货位状态失败");
						}
					}
					
					sb.setLength(0);
					sb.append(" cargo_id = ").append(inCiBean.getId());
					sb.append(" AND product_id = ").append(productId);
					
					CargoProductStockBean inCpsBean = service.getCargoProductStock(sb.toString());
					if (inCpsBean == null) {
						inCpsBean = new CargoProductStockBean();
						inCpsBean.setCargoId(inCiBean.getId());
						inCpsBean.setProductId(productId);

						if(!service.addCargoProductStock(inCpsBean)){
							db.rollbackTransaction();
							return JsonModelUtil.error("数据库操作失败"); 
						}							
						inCpsBean.setId(db.getLastInsertId());
					} 
					
					CargoOperationCargoBean bean = new CargoOperationCargoBean();
					bean.setOperId(operId);
					bean.setProductId(productId);
					bean.setInCargoProductStockId(0);
					bean.setInCargoWholeCode("");
					bean.setOutCargoProductStockId(outCpsBean.getId());
					bean.setOutCargoWholeCode(outCiBean.getWholeCode());
					bean.setStockCount(0);
					bean.setType(0);
					bean.setUseStatus(0);
					if(!service.addCargoOperationCargo(bean)){
						db.rollbackTransaction();
						return returnError("添加下架单详细信息，数据库操作失败!");
					}
					bean.setOperId(operId);
					bean.setProductId(productId);
					bean.setInCargoProductStockId(inCpsBean.getId());
					bean.setInCargoWholeCode(inCiBean.getWholeCode());
					bean.setOutCargoProductStockId(outCpsBean.getId());
					bean.setOutCargoWholeCode(outCiBean.getWholeCode());
					bean.setStockCount(0);
					bean.setType(1);
					bean.setUseStatus(1);
					if(!service.addCargoOperationCargo(bean)){
						db.rollbackTransaction();
						return returnError("添加下架单详细信息，数据库操作失败!");
					}
				}

				// 冻结源货位库存 
				if (!service.updateCargoProductStockCount(outCpsBean.getId(), -count)) {
					db.rollbackTransaction();
					return JsonModelUtil.error("源货位库存不足");
				}
				
				if (!service.updateCargoProductStockLockCount(outCpsBean.getId(), count)) {
					db.rollbackTransaction();
					return JsonModelUtil.error("数据库操作失败");
				}
				
				// 增加下架量
				sb.setLength(0);
				sb.append(" UPDATE cargo_operation_cargo SET stock_count = stock_count + ").append(count);
				sb.append(" WHERE oper_id = ").append(operId);				
				if(!db.executeUpdate(sb.toString())){
					db.rollbackTransaction();
					return JsonModelUtil.error("数据库操作失败");
				}
								
				// 查询下架量
				int resultCount = db.getInt(" SELECT stock_count FROM cargo_operation_cargo WHERE oper_id = " + operId + " LIMIT 1 ");
				
				db.commitTransaction();
				
				return JsonModelUtil.success("count", resultCount);			
			} catch (Exception e) {
				e.printStackTrace();
				db.rollbackTransaction();
				return JsonModelUtil.error("发生异常");
			}
			finally{
				db.release();
			}	
		}
	}
		
	/**
	 * 完成下架
	 * @param request
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/finishDownShelf")
	@ResponseBody
	public JsonModel finishDownShelf(HttpServletRequest request){
		PDAUtil pdaUtil = new PDAUtil();		
		JsonModel json = pdaUtil.getModelAndCheck(request, -1);
		if(json.getFlag() == 0){
			return json;
		}

		voUser user = pdaUtil.getUser();
		int operId = JsonModelUtil.getInt(json, "operId");
		if (operId <= 0) {
			return JsonModelUtil.error("缺少下架单参数");
		}
		
		synchronized (cargoLock) {		
			DbOperation db = new DbOperation();
			db.init(DbOperation.DB);		
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, db);
			IProductStockService productService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, db);			
			WareService wareService = new WareService(db);
			try {
				StringBuilder sb = new StringBuilder();
				sb.append(" id = ").append(operId);
				sb.append(" AND create_user_id = ").append(user.getId());
				sb.append(" AND status = ").append(CargoOperationProcessBean.OPERATION_STATUS12);
				
				CargoOperationBean coBean = service.getCargoOperation(sb.toString());				
				if (coBean == null) {
					 return JsonModelUtil.error("下架单不存在或下架单已作业完成");
				}
				
				// 开启事务  
				db.startTransaction();
				
				sb.setLength(0);
				sb.append(" oper_id = ").append(coBean.getId()).append(" AND type = 1  LIMIT 1 ");				
				CargoOperationCargoBean cocBean = service.getCargoOperationCargo(sb.toString());
				if (cocBean != null && cocBean.getStockCount() > 0) {
					CargoInfoBean outCiBean = service.getCargoInfo(" whole_code = '" + cocBean.getOutCargoWholeCode() + "' ");
					if (outCiBean == null) {
						db.rollbackTransaction();
						return JsonModelUtil.error("未查询到源货位");
					}
					
					CargoInfoBean inCiBean = service.getCargoInfo(" whole_code = '" + cocBean.getInCargoWholeCode() + "' ");
					if (inCiBean == null) {
						db.rollbackTransaction();
						return JsonModelUtil.error("未查询到目的货位");
					}

					CargoProductStockBean outCpsBean = service.getCargoProductStock(" id = " + cocBean.getOutCargoProductStockId());
					if (outCpsBean == null) {
						db.rollbackTransaction();
						return JsonModelUtil.error("未查询到源货位商品库存");
					}
					
					CargoProductStockBean inCpsBean = service.getCargoProductStock(" id = " + cocBean.getInCargoProductStockId());
					if (inCpsBean == null) {
						db.rollbackTransaction();
						return JsonModelUtil.error("未查询到目的货位商品库存");
					}
					
					// 调库存
					// 源货位减冻结量
					if (!service.updateCargoProductStockLockCount(cocBean.getOutCargoProductStockId(), -cocBean.getStockCount())) {
						db.rollbackTransaction();
						return JsonModelUtil.error("源货位冻结量不足");
					}					
					// 目的货位加可用量
					if (!service.updateCargoProductStockCount(cocBean.getInCargoProductStockId(), cocBean.getStockCount())) {
						db.rollbackTransaction();
						return JsonModelUtil.error("源货位库存不足");
					}
					
					// 添加 货位进销存卡片
					voProduct vProduct = wareService.getProduct(cocBean.getProductId());
					vProduct.setPsList(productService.getProductStockList("product_id=" + cocBean.getProductId(), -1, -1, null));

					CargoStockCardBean outcsc = new CargoStockCardBean();
					outcsc.setCardType(CargoStockCardBean.CARDTYPE_PRODUCT_DOWN_SHELF);
					outcsc.setCode(coBean.getCode());
					outcsc.setCreateDatetime(DateUtil.getNow());
					outcsc.setStockType(outCiBean.getStockType());
					outcsc.setStockArea(outCiBean.getAreaId());
					outcsc.setProductId(vProduct.getId());
					outcsc.setStockId(cocBean.getOutCargoProductStockId());
					outcsc.setStockOutCount(cocBean.getStockCount());
					outcsc.setStockOutPriceSum((new BigDecimal(cocBean.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
					outcsc.setCurrentStock(vProduct.getStock(outcsc.getStockArea(), outcsc.getStockType()) + vProduct.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
					outcsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
					outcsc.setCurrentCargoStock(outCpsBean.getStockCount() + outCpsBean.getStockLockCount());
					outcsc.setCargoStoreType(outCiBean.getStoreType());
					outcsc.setCargoWholeCode(cocBean.getOutCargoWholeCode());
					outcsc.setStockPrice(vProduct.getPrice5());
					outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
					if (!service.addCargoStockCard(outcsc)) {
						db.rollbackTransaction();
						return JsonModelUtil.error("数据库操作失败");
					}
					
					// 货位入库卡片
					CargoStockCardBean incsc = new CargoStockCardBean();
					incsc.setCardType(CargoStockCardBean.CARDTYPE_PRODUCT_DOWN_SHELF);
					incsc.setCode(coBean.getCode());
					incsc.setCreateDatetime(DateUtil.getNow());
					incsc.setStockType(inCiBean.getStockType());
					incsc.setStockArea(inCiBean.getAreaId());
					incsc.setProductId(vProduct.getId());
					incsc.setStockId(cocBean.getInCargoProductStockId());
					incsc.setStockInCount(cocBean.getStockCount());
					incsc.setStockInPriceSum((new BigDecimal(cocBean.getStockCount())).multiply(new BigDecimal(StringUtil.formatDouble2(vProduct.getPrice5()))).doubleValue());
					incsc.setCurrentStock(vProduct.getStock(incsc.getStockArea(), incsc.getStockType()) + vProduct.getLockCount(incsc.getStockArea(), incsc.getStockType()));
					incsc.setAllStock(vProduct.getStockAll() + vProduct.getLockCountAll());
					incsc.setCurrentCargoStock(inCpsBean.getStockCount() + inCpsBean.getStockLockCount());
					incsc.setCargoStoreType(inCiBean.getStoreType());
					incsc.setCargoWholeCode(cocBean.getInCargoWholeCode());
					incsc.setStockPrice(vProduct.getPrice5());
					incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
					if (!service.addCargoStockCard(incsc)) {
						db.rollbackTransaction();
						return JsonModelUtil.error("数据库操作失败");
					}					
				}

				sb.setLength(0);
				sb.append(" status = ").append(CargoOperationProcessBean.OPERATION_STATUS17);
				sb.append(" , effect_status = 2 , complete_user_id = ").append(user.getId());
				sb.append(" , complete_user_name = '").append(user.getUsername()).append("' ");
				sb.append(" , complete_datetime = '").append(DateUtil.getNow()).append("' ");
				sb.append(" , last_operate_datetime = '").append(DateUtil.getNow()).append("' ");
				
				if (!service.updateCargoOperation(sb.toString(), " id = " + coBean.getId())) {					
					db.rollbackTransaction();
					return JsonModelUtil.error("数据库操作失败");
				}
				
				db.commitTransaction();
				
				return JsonModelUtil.success();			
			} catch (Exception e) {
				e.printStackTrace();
				db.rollbackTransaction();
				return JsonModelUtil.error("发生异常");
			}
			finally{
				db.release();
			}	
		}
	}
	
	
	/**
	 * 下架作业失败
	 * @param request
	 * @author mengqy
	 * @return
	 */
	@RequestMapping("/donwShelfFailed")
	@ResponseBody
	public JsonModel donwShelfFailed(HttpServletRequest request){
		PDAUtil pdaUtil = new PDAUtil();		
		JsonModel json = pdaUtil.getModelAndCheck(request, -1);
		if(json.getFlag() == 0){
			return json;
		}

		voUser user = pdaUtil.getUser();
		int operId = JsonModelUtil.getInt(json, "operId");
		if (operId <= 0) {
			return JsonModelUtil.error("缺少下架单参数");
		}
		
		synchronized (cargoLock) {		
			DbOperation db = new DbOperation();
			db.init(DbOperation.DB);		
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, db);
			
			try {
				StringBuilder sb = new StringBuilder();
				sb.append(" id = ").append(operId);
				sb.append(" AND create_user_id = ").append(user.getId());
				sb.append(" AND status = ").append(CargoOperationProcessBean.OPERATION_STATUS12);
				
				CargoOperationBean coBean = service.getCargoOperation(sb.toString());				
				if (coBean == null) {
					 return JsonModelUtil.error("下架单不存在或下架单已作业完成");
				}
				
				// 开启事务  
				db.startTransaction();
				
				sb.setLength(0);
				sb.append(" oper_id = ").append(coBean.getId()).append(" LIMIT 1 ");				
				CargoOperationCargoBean cocBean = service.getCargoOperationCargo(sb.toString());
				if (cocBean != null && cocBean.getStockCount() > 0) {
					// 还原库存					
					if (!service.updateCargoProductStockCount(cocBean.getOutCargoProductStockId(), cocBean.getStockCount())) {
						db.rollbackTransaction();
						return JsonModelUtil.error("数据库操作失败");
					}
					
					if (!service.updateCargoProductStockLockCount(cocBean.getOutCargoProductStockId(), -cocBean.getStockCount())) {
						db.rollbackTransaction();
						return JsonModelUtil.error("解冻库存失败，库存不足");
					}
				}

				sb.setLength(0);
				sb.append(" status = ").append(CargoOperationProcessBean.OPERATION_STATUS18);
				sb.append(" , effect_status = 4 , complete_user_id = ").append(user.getId());
				sb.append(" , complete_user_name = '").append(user.getUsername()).append("' ");
				sb.append(" , complete_datetime = '").append(DateUtil.getNow()).append("' ");
				sb.append(" , last_operate_datetime = '").append(DateUtil.getNow()).append("' ");
				
				if (!service.updateCargoOperation(sb.toString(), " id = " + coBean.getId())) {					
					db.rollbackTransaction();
					return JsonModelUtil.error("数据库操作失败");
				}
				
				db.commitTransaction();
				
				return JsonModelUtil.success();			
			} catch (Exception e) {
				e.printStackTrace();
				db.rollbackTransaction();
				return JsonModelUtil.error("发生异常");
			}
			finally{
				db.release();
			}	
		}
	}
	
}

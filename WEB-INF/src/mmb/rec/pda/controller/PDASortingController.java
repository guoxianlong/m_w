package mmb.rec.pda.controller;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import mmb.rec.pda.bean.JsonModel;
import mmb.rec.pda.util.CheckUser;
import mmb.rec.pda.util.ReceiveJson;
import mmb.stock.stat.SecondSortingSplitService;
import mmb.stock.stat.SortingBatchGroupBean;
import mmb.stock.stat.SortingBatchGroupExceptionBean;
import mmb.stock.stat.SortingBatchOrderBean;
import mmb.stock.stat.SortingBatchOrderProductBean;
import mmb.stock.stat.SortingInfoService;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
/**
 * 说明：pda分拣
 * 
 * 时间：2013-09-12
 * 
 * 作者：张晔
 */
@Controller
@RequestMapping("/PDASortingController")
public class PDASortingController {
	public static byte[] cargoLock_sorting = new byte[0];
	public static byte[] cargoLock_handle = new byte[0];
	public static byte[] cargoLock_process = new byte[0];
	
	@RequestMapping("/PDAProductSorting")
	@ResponseBody
	public JsonModel PDAProductSorting(HttpServletRequest request){
		synchronized (cargoLock_sorting) {
			voUser user = null;
			JsonModel json =  new JsonModel();
			DbOperation querydb = new DbOperation();
			querydb.init(DbOperation.DB);
			WareService wareService = new WareService(querydb);
			SortingInfoService sortingInfoService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			SecondSortingSplitService secondSortingSplitService = new SecondSortingSplitService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, querydb);
			DbOperation updateDB = new DbOperation();
			updateDB.init(DbOperation.DB);
			ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, updateDB);
			SecondSortingSplitService updateSecondSortingSplitService = new SecondSortingSplitService(IBaseService.CONN_IN_SERVICE, updateDB);
			SortingInfoService updateSortingInfoService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, updateDB);
			try{
				json = ReceiveJson.receiveJson(request);//从流中读取json数据
				if(json==null){
					return returnError("没有收到请求数据!");
				}
				if(CheckUser.checkUser(request,json.getUserName(), json.getPassword())){//验证用户名密码
					user = (voUser)request.getSession().getAttribute("userView");
					int areaId = StringUtil.toInt(json.getArea());//地区id
					String sortingBatchGroupCode = StringUtil.convertNull((String)json.getData().get("code"));
					Integer cargo_id = StringUtil.toInt((String)json.getData().get("cargo_id"));
					Integer product_id = StringUtil.toInt((String)json.getData().get("product_id"));
					//本次扫描数量
					int productCount = StringUtil.toInt((String)json.getData().get("productCount"));
//					String submitProductStatus = StringUtil.convertNull((String)json.getData().get("submitProductStatus"));
					//总数量（不包含撤单的）
					int count = 0;
					//已分拣数量包含撤单的
					int sortingCountDelete = 0;
					//已分拣数量不包含撤单的
					int sortingCount = 0;
					if ("".equals(sortingBatchGroupCode)) {
						return returnError("没有收到波次号!");
					}
					if (cargo_id == -1) {
						return returnError("没有收到货位号!");
					}
					CargoInfoBean outCargoInfo = cargoService.getCargoInfo("id=" + cargo_id);
					if (outCargoInfo == null) {
						return returnError("没有找到源货位!");
					}
					// sorting_type = 1 必须为PDA分拣波次 
					SortingBatchGroupBean sortingBatchGroupBean = sortingInfoService.getSortingBatchGroupInfo("code='" + sortingBatchGroupCode +"' and storage = "+ areaId + "  AND sorting_type = 1 ");
					if (sortingBatchGroupBean == null) {
						return returnError("波次不存在！");
					}
					if ( -1 == productCount) {
						return returnError("没有收到数量!");
					}
					if (product_id == -1) {
						return returnError("没有收到商品条码!");
					}
					voProduct product = null;
					product = wareService.getProduct(product_id);
					
					if( product == null ) {
						return returnError("产品不存在！");
					}
					
					//删除的不予处理
					String query = "select sum( case when sbop.is_delete=1 then 0 else sbop.count end),sum( case when sbop.is_delete=1 then 0 else sbop.sorting_count end),sum(sbop.sorting_count) from sorting_batch_order_product sbop where sbop.sorting_batch_group_id ="+sortingBatchGroupBean.getId()+" and sbop.product_id="+product.getId()+" and cargo_id="+outCargoInfo.getId()+"";
					ResultSet rs = querydb.executeQuery(query);
					if (rs.next()) {
						count = rs.getInt(1);
						sortingCount = rs.getInt(2);
						sortingCountDelete = rs.getInt(3);
					}
					//如果扫描总是过多返回报错
					if (productCount > count-sortingCount) {
						Map<String,Object> map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()));
						return returnResult(map, "分拣数量过多！", 2);
					}
					product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
					
					CargoProductStockBean outCargoProductStock = cargoService.getCargoAndProductStock("cargo_id = "+outCargoInfo.getId() +" and product_id = "+product.getId());
					if (outCargoProductStock == null) {
						Map<String,Object> map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()));
						return returnResult(map, "没有找到源货位库存记录！", 2);
					}
					CargoInfoBean inCargoInfo = cargoService.getCargoInfo("stock_type = "+ProductStockBean.STOCKTYPE_QUALIFIED+" and area_id = "+areaId+" and store_type = "+CargoInfoBean.STORE_TYPE5+" and status <> "+CargoInfoBean.STATUS3);
					if (inCargoInfo == null) {
						Map<String,Object> map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()));
						return returnResult(map, "没有找到目的货位！", 2);
					}
					CargoProductStockBean inCargoProductStock = cargoService.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+product.getId());
					updateDB.startTransaction();
					if (inCargoProductStock == null) {
						CargoProductStockBean cpsBean = new CargoProductStockBean();
						cpsBean.setCargoId(inCargoInfo.getId());
						cpsBean.setProductId(product.getId());
						cpsBean.setStockCount(0);
						cpsBean.setStockLockCount(0);
						if (!cargoService.addCargoProductStock(cpsBean)) {
							updateDB.rollbackTransaction();
							Map<String,Object> map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()));
							return returnResult(map, "添加目的货位库存记录！", 2);
						}
						inCargoProductStock = cargoService.getCargoAndProductStock("cargo_id = "+inCargoInfo.getId() +" and product_id = "+product.getId());
					}
					List<SortingBatchOrderProductBean> sortingBatchOrderProductBeanList = secondSortingSplitService.getSortingBatchOrderProductList("sorting_batch_group_id ="+sortingBatchGroupBean.getId()+" and product_id="+product.getId()+" and cargo_id="+outCargoInfo.getId()+" and is_delete=0 and sorting_count < count", -1, -1,  "count");
					if (sortingBatchOrderProductBeanList == null) {
						updateDB.rollbackTransaction();
						Map<String,Object> map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()));
						return returnResult(map, "没有找到货位商品异常信息！", 2);
					}
					//已扫描尚未更新分拣数量
					int editCount = productCount;
					int listSize = sortingBatchOrderProductBeanList.size();
					
					for (int i = 0; i < listSize; i ++) {
						SortingBatchOrderProductBean bean = sortingBatchOrderProductBeanList.get(i);
						int lackCount = bean.getCount() - bean.getSortingCount();
						//比较数量与缺失数量大小，若数量多则更新为订单商品数量，否则更新为小的
						if (editCount >= lackCount) {
							editCount = editCount - lackCount;
							String set = "sorting_count=count,sorting_user_id="+user.getId()+",sorting_username='"+user.getUsername()+"',sorting_datetime='"+DateUtil.getNow()+"'";
							if (!updateSecondSortingSplitService.updateSortingBatchOrderProduct(set, "id="+bean.getId())) {
								updateDB.rollbackTransaction();
								Map<String,Object> map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()));
								return returnResult(map, "更新分拣数量报错！", 2);
							}
							//状态更新为正常
							if (!updateSortingInfoService.updateSortingBatchGroupExceptionInfo("is_exception=0", "sorting_batch_group_id ="+sortingBatchGroupBean.getId()+" and product_id="+product.getId()+" and cargo_id="+outCargoInfo.getId())) {
								updateDB.rollbackTransaction();
								Map<String,Object> map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()));
								return returnResult(map, "更新分拣货位商品异常表报错！", 2);
							}
						} else {
							String set = "sorting_count=sorting_count+"+editCount+",sorting_user_id="+user.getId()+",sorting_username='"+user.getUsername()+"',sorting_datetime='"+DateUtil.getNow()+"'";
							if (!updateSecondSortingSplitService.updateSortingBatchOrderProduct(set, "id="+bean.getId())){
								updateDB.rollbackTransaction();
								Map<String,Object> map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()));
								return returnResult(map, "更新分拣数量报错！", 2);
							}
							//状态更新为异常
							if (!updateSortingInfoService.updateSortingBatchGroupExceptionInfo("is_exception=1", "sorting_batch_group_id ="+sortingBatchGroupBean.getId()+" and product_id="+product.getId()+" and cargo_id="+outCargoInfo.getId())) {
								updateDB.rollbackTransaction();
								Map<String,Object> map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()));
								return returnResult(map, "更新分拣货位商品异常表报错！", 2);
							}
							break;
						}
					}
					
					int changeCount = productCount;
					//减少源锁定量
					if(!cargoService.updateCargoProductStockLockCount(outCargoProductStock.getId(), -changeCount)){
						updateDB.rollbackTransaction();
						Map<String,Object> map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()));
						return returnResult(map, "货位库存操作失败，源货位冻结库存不足！", 2);
					}
					//增加目的锁定量
					if(!cargoService.updateCargoProductStockLockCount(inCargoProductStock.getId(), changeCount)){
						updateDB.rollbackTransaction();
						Map<String,Object> map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()));
						return returnResult(map, "货位库存操作失败，目的货位冻结库存不足！", 2);
					}
					
					product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
					
					//添加货位进销存卡片
					//货位出库卡片
					CargoStockCardBean outcsc = new CargoStockCardBean();
					outcsc.setCardType(CargoStockCardBean.CARDTYPE_PDASORTINGOUT);
					outcsc.setCode(sortingBatchGroupCode);
					outcsc.setCreateDatetime(DateUtil.getNow());
					outcsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
					outcsc.setStockArea(areaId);
					outcsc.setProductId(product.getId());
					outcsc.setStockId(outCargoProductStock.getId());
					outcsc.setStockOutCount(changeCount);
					outcsc.setStockOutPriceSum((new BigDecimal(changeCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
					outcsc.setCurrentStock(product.getStock(outcsc.getStockArea(), outcsc.getStockType()) + product.getLockCount(outcsc.getStockArea(), outcsc.getStockType()));
					outcsc.setAllStock(product.getStockAll() + product.getLockCountAll());
					outcsc.setCurrentCargoStock(outCargoProductStock.getStockCount()+outCargoProductStock.getStockLockCount());
					outcsc.setCargoStoreType(outCargoProductStock.getCargoInfo().getStoreType());
					outcsc.setCargoWholeCode(outCargoProductStock.getCargoInfo().getWholeCode());
					outcsc.setStockPrice(product.getPrice5());
					outcsc.setAllStockPriceSum((new BigDecimal(outcsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(outcsc.getStockPrice()))).doubleValue());
					if(!cargoService.addCargoStockCard(outcsc)){
						updateDB.rollbackTransaction();
						Map<String,Object> map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()));
						return returnResult(map, "货位进销存记录添加失败，请重新尝试操作！", 2);
					}
					//货位入库卡片
					CargoStockCardBean incsc = new CargoStockCardBean();
					incsc.setCardType(CargoStockCardBean.CARDTYPE_PDASORTINGIN);
					incsc.setCode(sortingBatchGroupCode);
					incsc.setCreateDatetime(DateUtil.getNow());
					incsc.setStockType(ProductStockBean.STOCKTYPE_QUALIFIED);
					incsc.setStockArea(areaId);
					incsc.setProductId(product.getId());
					incsc.setStockId(inCargoProductStock.getId());
					incsc.setStockInCount(changeCount);
					incsc.setStockInPriceSum((new BigDecimal(changeCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
					incsc.setCurrentStock(product.getStock(incsc.getStockArea(), incsc.getStockType()) + product.getLockCount(incsc.getStockArea(), incsc.getStockType()));
					incsc.setAllStock(product.getStockAll() + product.getLockCountAll());
					incsc.setCurrentCargoStock(inCargoProductStock.getStockCount()+inCargoProductStock.getStockLockCount());
					incsc.setCargoStoreType(inCargoProductStock.getCargoInfo().getStoreType());
					incsc.setCargoWholeCode(inCargoProductStock.getCargoInfo().getWholeCode());
					incsc.setStockPrice(product.getPrice5());
					incsc.setAllStockPriceSum((new BigDecimal(incsc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(incsc.getStockPrice()))).doubleValue());
					if(!cargoService.addCargoStockCard(incsc)){
						updateDB.rollbackTransaction();
						Map<String,Object> map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()));
						return returnResult(map, "货位进销存记录添加失败，请重新尝试操作！", 2);
					}
					updateDB.commitTransaction();
//					map = queryScanningList(sortingInfoService, String.valueOf(sortingBatchGroupBean.getId()), outCargoInfo.getId(),"end");
					return returnResult(null, "分拣成功！", 1);
				}else{
					return returnError("身份验证失败!"); 
				}	
				
			}catch(Exception e){
				e.printStackTrace();
				updateDB.rollbackTransaction();
				return returnError("操作失败,系统异常！");
			}finally{
				querydb.release();
				updateDB.release();
			}
		}
	}
	/**
	 * 扫描波次号的处理
	 * 2013-9-12
	 * 朱爱林
	 */
	@RequestMapping("/scanningWaveNumberProcess")
	@ResponseBody
	public JsonModel scanningWaveNumberProcess(HttpServletRequest request){
		DbOperation updateDb = new DbOperation();
		updateDb.init(DbOperation.DB);
		SortingInfoService  service = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, updateDb);
		SortingInfoService service2 = null;
		JsonModel json = new JsonModel();
		try{
			synchronized (cargoLock_process) {
			json = ReceiveJson.receiveJson(request);
			if(json==null){
				return returnError("没有收到请求数据!", 0);
			}
			if(CheckUser.checkUser(request, json.getUserName(), json.getPassword())){
				String status = StringUtil.convertNull((String)json.getData().get("status"));//扫描状态
				String code = StringUtil.convertNull((String)json.getData().get("code"));//波次号
				int areaId = StringUtil.toInt(json.getArea());//地区id
				//如果status为“”或者不为1，则表示分拣开始；如果为1则分拣扫描结束并将sorting_batch_grop
				//表中的sorting_status改成2（分拣完成）
				//select cs.user_name from sorting_batch_group sbg ,cargo_staff cs where sbg.staff_id = cs.id and sbg.`code`='FJ1309153003001';
				SortingBatchGroupBean sbgb = service.getSortingBatchGroupInfo(" code = '"+code+"' and storage = "+ areaId);
				if(sbgb==null){
					return returnError("此波次不存在!",0);
				}else{
					if(sbgb.getSortingType() != 1){
						return returnError("非PDA分拣波次，不可以用PDA进行分拣!",0);	
					}					
					// @mengqy 删除原有限定条件，需求： PDA分拣人和领单人 不必是同一个人，也可以多个人进行分拣
				}
				if("2".equals(status)){
					service2 = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, updateDb);
					service2.getDbOp().startTransaction();
					boolean boo = service2.updateSortingBatchGroupInfo(" sorting_status = 2", " code = '"+StringUtil.toSql(code)+"'");
					if(boo){
						boo = service2.updateSortingBatchGroupInfo(" sorting_complete_datetime = '"+DateUtil.formatTime(new Date())+"'", " code = '"+StringUtil.toSql(code)+"'");
						if(boo){
							service2.getDbOp().commitTransaction();
							return returnResult("修改成功!");
						}else{
							service2.getDbOp().rollbackTransaction();
							return returnError("修改分拣完成时间失败!", 0);
						}
					}else{
						service2.getDbOp().rollbackTransaction();
						return returnError("修改失败!", 0);
					}
				}else{//查询
					/*
					 * 1.先通过code查询出对应sorting_batch_group的id
					 */
//					SortingBatchGroupBean sbgb = service.getSortingBatchGroupInfo(" code = '"+code+"'");
//					SortingBatchGroupBean sbgb = service.getSortingBatchGroupInfo(" sorting_type = 1 ");//分拣类型为pda分拣

					// @mengqy 需求：PDA分拣完成后，还可以继续分拣					
//					if(sbgb.getSortingStatus()==2){
//						return returnError("此波次已pda分拣完成!",0);
//					}
					if(sbgb.getStatus()!=1){
						return returnError("此波次分拣状态必须是分拣中的!",0);
					}
					if(sbgb.getStatus2()==1||sbgb.getStatus2()==2){
						return returnError("此波次已开始分播,不能开始!",0);
					}
					//改成是未删除的订单
					int count = service.getSortingBatchOrderCount(" delete_status="+SortingBatchOrderBean.DELETE_STATUS+" and status = 3 and sorting_group_id = "+sbgb.getId());
					if(count>0){
						return returnError("此波次已有订单复核通过,不能开始!",0);
					}
					Map<String, Object> map = queryScanningList(service, sbgb.getId()+"");//获取需要扫描的信息列表
					return returnResult(map, "操作成功!", 1);
				}
			}else{
				return returnError("身份验证失败!", 0);
			}
			}
		}catch(Exception e){
			e.printStackTrace();
			return returnError("异常信息!", 0);
		}finally{
			if(service!=null){
				service.releaseAll();
			}
		}
	}
	/**
	 * pda分拣--处理提交异常
	 * 2013-9-14
	 * 朱爱林	
	 * @param request
	 * @return
	 */
	@RequestMapping("/sortingBatchHandleError")
	@ResponseBody
	public JsonModel sortingBatchHandleError(HttpServletRequest request){
		synchronized (cargoLock_handle) {
			SortingInfoService service = null;
			DbOperation db = new DbOperation();
			db.init(DbOperation.DB);
			try{
				JsonModel json = new JsonModel();
				json = ReceiveJson.receiveJson(request);
				if(json==null){
					return returnError("没有收到请求数据!", 0);
				}
				if(CheckUser.checkUser(request, json.getUserName(), json.getPassword())){
					db.startTransaction();
					service = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, db);
					String code = StringUtil.convertNull((String)json.getData().get("code"));//波次id
					Integer cargo_id = Integer.parseInt(StringUtil.convertNull((String)json.getData().get("cargo_id")));
					String product_id = StringUtil.convertNull((String)json.getData().get("product_id"));
					int areaId = StringUtil.toInt(json.getArea());//地区id
					
					// sorting_type = 1 必须为PDA分拣波次
					SortingBatchGroupBean sbgb = service.getSortingBatchGroupInfo(" code = '"+code+"' and storage = "+ areaId + " AND sorting_type = 1 ");
					if(sbgb!=null){
						/*
						 * 判断是否已经异常了
						 * 1.当未分拣量=0 已分拣完，不能提交异常
						 * 2.当未分拣量>0并且已分拣量>0，如果大于0则是异常
						 */
						StringBuilder sql = new StringBuilder();
						sql.append("select sum(case when sbop.is_delete=1 THEN 0 else sbop.count-sbop.sorting_count END), sum(sbop.sorting_count) ")
							.append(" from sorting_batch_order_product sbop where sbop.sorting_batch_group_id = "+sbgb.getId())
							.append(" and sbop.product_id = "+product_id).append(" and sbop.cargo_id ="+cargo_id)
							.append(" GROUP BY sbop.cargo_id,sbop.product_id");
						ResultSet rs = service.getDbOp().executeQuery(sql.toString());
						if(rs.next()){
							int unsortingcount = rs.getInt(1);//未分拣
							int sortingcount = rs.getInt(2);//已分拣
							if(unsortingcount <= 0 && sortingcount > 0){
								return returnResult(queryScanningList(service, sbgb.getId()+"") ,"此波次这一商品已分拣完成!",2);
							} else if (unsortingcount <= 0 && sortingcount <= 0) {
								return returnResult(queryScanningList(service, sbgb.getId()+"") ,"此波次这一商品所在订单不存在!",2);
							} else if (unsortingcount > 0 && sortingcount > 0){
								return returnResult(queryScanningList(service, sbgb.getId()+""),"此商品已是异常状态!", 2);
							}
						}else{
							return returnResult(queryScanningList(service, sbgb.getId()+""),"异常提交失败!", 2); 
						}
						
						String condition = " sorting_batch_group_id = "+sbgb.getId()+" and cargo_id = "+cargo_id+" and product_id = "+product_id;
						boolean boo = service.updateSortingBatchGroupExceptionInfo(" is_exception = 1", condition);
						if(boo){
							db.commitTransaction();
							return returnResult(null ,"异常提交成功",1);
							
						}else{
							db.rollbackTransaction();
							return returnResult(queryScanningList(service, sbgb.getId()+""),"异常提交失败!", 2);
						}
					}else{
						db.rollbackTransaction();
						return returnError("此波次不存在!",0);
					}
				}else{
					return returnError("身份验证失败!", 0);
				}
			}catch(Exception e){
				db.rollbackTransaction();
				e.printStackTrace();
				return returnError("异常信息!", 0);
			}finally{
				if(service!=null){
					service.releaseAll();
				}
			}
		}
	}
	/**
	 * 获取需要扫描的信息列表
	 * 2013-9-13
	 * @param service
	 * @param id  波次号对应的id
	 */
	private Map<String, Object> queryScanningList(IBaseService service, String id)  {
		SortingInfoService sortingInfoService = new SortingInfoService(IBaseService.CONN_IN_SERVICE, new DbOperation(DbOperation.DB));
		SortingInfoService sortingInfoService2 = new SortingInfoService(IBaseService.CONN_IN_SERVICE, new DbOperation(DbOperation.DB));
		Map<String,Object> map = new HashMap<String, Object>();
		try {
			List<Map<String,Object>> unSorting = new ArrayList<Map<String,Object>>();//未分拣
			List<Map<String,Object>> errorSorting = new ArrayList<Map<String,Object>>();//异常
			List<Map<String,Object>> sorting = new ArrayList<Map<String,Object>>();//已分拣
			int sid = Integer.parseInt(id);//sorting_batch_group 的id
			String sql = "";
			sql = "select p.name ,ci.whole_code ,sb.barcode ,sum(sb.sorting_count) ,"
					+" sum(case when sb.is_delete=1 then 0 ELSE sb.count-sb.sorting_count END)"
					+" ,sb.product_id,sb.cargo_id"
					+" from (select sbop.product_id,sbop.cargo_id ,sbop.count,sbop.sorting_count,sbop.is_delete, sbop.sorting_batch_group_id,pb.barcode"
					+" from sorting_batch_order_product sbop"
					+" left join product_barcode pb on sbop.product_id = pb.product_id and pb.barcode_status=0"
					+" where sbop.sorting_batch_group_id = "+ sid //这里加上通过波次号查询出来的sorting_batch_group id
					+" ) sb,product p,cargo_info ci,sorting_batch_group sbg"
					+" where sbg.id=sb.sorting_batch_group_id and sb.product_id = p.id and ci.id = sb.cargo_id"
					+" and sb.sorting_batch_group_id = "+sid//and sb.sorting_batch_group=''
					+" GROUP BY sb.cargo_id,sb.product_id order by ci.whole_code";
			ResultSet rs = sortingInfoService2.getDbOp().executeQuery(sql);
			/*
			 * 判断逻辑：
			 * 	1、rs.getObject(5)==0 说明是已分拣
			 * 	2、否则就与表exception匹配，如果能匹配说明是异常
			 *  3、否则就是未分拣的
			 */
			Map<String,Object> subMap = null;
			int sortingSize = 0;
			int unsortingSize = 0;
			int errorSortingSize = 0;
			StringBuilder ss = new StringBuilder();
			String subcode = "";
			while(rs.next()){
				ss.delete(0, ss.length());
				subMap = new HashMap<String, Object>();
				String name = (String)rs.getString(1);//商品名称
				subMap.put("product_name", name);
				String cargo_code = (String)rs.getString(2);//货位号
				subMap.put("cargo_code", cargo_code);
				subcode = cargo_code.substring(6);
				ss.append(subcode.substring(0, 3)).append("-").append(subcode.substring(3, 5)).append("-").append(subcode.substring(5,6))
					.append("-").append(subcode.substring(6));
				subMap.put("cargo_code_sub", ss.toString());
				String barcode = (String)rs.getString(3);//商品条码
				subMap.put("barcode", barcode);
				int sorting_count = (Integer)rs.getInt(4)<0?0:(Integer)rs.getInt(4);//已分拣
				subMap.put("sorting_count", sorting_count);
				Integer unsorting_count = (Integer)rs.getInt(5);//未分拣
				subMap.put("unsorting_count", unsorting_count);
				Integer product_id = (Integer)rs.getInt(6);//产品id
				subMap.put("product_id", product_id);
				Integer cargo_id2 = (Integer)rs.getInt(7);//货位id
				subMap.put("cargo_id", cargo_id2);
				if(unsorting_count==0){
					if ( sorting_count > 0) {
						sorting.add(subMap);
						sortingSize+=sorting_count;
					}
					continue;
				}
				List<SortingBatchGroupExceptionBean> sbgeBeanList = sortingInfoService.getSortingBatchGroupExceptionList("sorting_batch_group_id = " + sid + " and cargo_id = "+cargo_id2+" and product_id = "+product_id, -1, -1, null);
				if (sbgeBeanList != null && sbgeBeanList.size() != 0) {
					int x = sbgeBeanList.size();
					for ( int i = 0 ; i < x ; i ++) {
						int isException = sbgeBeanList.get(i).getIsException();
						if(isException==1){//异常
							errorSorting.add(subMap);
							errorSortingSize+=1;
						}else{
							unSorting.add(subMap);
							unsortingSize+=unsorting_count;
						}
					}
				}
			}
			rs.close();
			map.put("unSorting", unSorting);
			map.put("unSorting_size", unsortingSize);
			map.put("errorSorting", errorSorting);
			map.put("errorSorting_size", errorSortingSize);
			map.put("sorting", sorting);
			map.put("sorting_size", sortingSize);
			map.put("listType", 1);
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			sortingInfoService.releaseAll();
			sortingInfoService2.releaseAll();
		}
		return map;
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
 	 * 说明：返回结果信息
	 * 
	 * 日期：2013-08-09
	 *
	 * 作者：张晔
	 */
	private JsonModel returnResult(Map<String, Object> data,String message, int flag){
		JsonModel json = new JsonModel();
		json.setFlag(flag);
		json.setData(data);
		json.setMessage(message);
		return json;
	}
}

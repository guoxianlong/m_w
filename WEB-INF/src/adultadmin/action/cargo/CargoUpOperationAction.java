package adultadmin.action.cargo;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.oper.service.ConsignmentService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoOperLogBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationLogBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockExchangeProductBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IUserService;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
 

public class CargoUpOperationAction extends DispatchAction{

	public static byte[] cargoLock = new byte[0];	
	/**
	 * 调拨单列表
	 */
	public ActionForward showExChangeList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int countPerPage=20;
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ICargoService cargoService=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IUserService userService = ServiceFactory.createUserService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String exChangeCode= StringUtil.convertNull(request.getParameter("exChangeCode"));//调拨单编号
			String productCode = StringUtil.convertNull(request.getParameter("productCode"));//产品编号
			String stockInArea = request.getParameter("stockInArea");//调拨单目的地区
			if(stockInArea==null){
				stockInArea="-1";
			}
			
			
			StringBuilder query = new StringBuilder("select ex.* from stock_exchange ex, stock_exchange_product ep " +
					"where ex.id= ep.stock_exchange_id and ex.status=7 and ex.stock_in_type=0 " +
					"and ex.up_shelf_status <>2 and (ep.no_up_cargo_count > 0 or ep.up_cargo_lock_count > 0) " +
					"and ex.stock_in_area="+stockInArea);
			
			StringBuilder queryCount = new StringBuilder("select count(distinct ex.id) from stock_exchange ex, stock_exchange_product ep " +
					"where ex.id= ep.stock_exchange_id and ex.status=7 and ex.stock_in_type=0 " +
					"and ex.up_shelf_status <>2 and (ep.no_up_cargo_count > 0 or ep.up_cargo_lock_count > 0)" +
					" and ex.stock_in_area="+stockInArea); 
			
			StringBuilder paramBuf = new StringBuilder();
			
			paramBuf.append("&stockInArea="+stockInArea);
			int exChangeId = StringUtil.StringToId(request.getParameter("exChangeId")); //子列表
			if(!exChangeCode.equals("")){
				query.append(" and ex.code= '"+exChangeCode+"'");
				queryCount.append(" and ex.code= '"+exChangeCode+"'");
				paramBuf.append("&exChangeCode="+exChangeCode);
			}
			
			if(!productCode.equals("")){
				voProduct product = (voProduct) wareService.getProduct(productCode);
				int productId=0;
				if(product!=null){
					productId=product.getId();
				}
				queryCount.append(" and ep.product_id="+productId);
				query.append(" and ep.product_id="+productId);
				if(paramBuf.length() > 0){
					paramBuf.append("&");
				}
				paramBuf.append("productCode="+productCode);
			}
			
			query.append(" group by ex.id order by ex.id desc") ;
		
			int totalCount = cargoService.getTablesCount(queryCount.toString()); //页码
            int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
            PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
            List list = cargoService.getStockExchangeCascade(query.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage);
            paging.setPrefixUrl("admin/cargoUpOper.do?method=showExChangeList" + paramBuf.toString());

            Iterator iter = list.listIterator();
            while(iter.hasNext()){
            	StockExchangeBean se = (StockExchangeBean)iter.next();
            	StringBuilder operationSb = new StringBuilder("select count(distinct ca.id) from cargo_operation ca , cargo_operation_cargo op where " +
            			" ca.id=op.oper_id and op.type=0 and  op.use_status =1 and ca.source='"+se.getCode()+"'");//得到操作量 单子的数量
            	int operationNum= cargoService.getTablesCount(operationSb.toString());
            	se.setCreateUser(userService.getAdminUser("id=" + se.getCreateUserId()));
            	se.setAuditingUser(userService.getAdminUser("id=" + se.getAuditingUserId()));
            	se.setAuditingUser2(userService.getAdminUser("id=" + se.getAuditingUserId2()));
            	se.setStockOutOperUser(userService.getAdminUser("id=" + se.getStockOutOper()));
            	se.setStockInOperUser(userService.getAdminUser("id=" + se.getStockInOper()));
            	se.setOperationNum(operationNum);
            }
            
            if(exChangeId!=0){ //调拨单中产品
            	StockExchangeBean bean = service.getStockExchange("id = " + exChangeId);
                if(bean == null){
            		request.setAttribute("tip", "没有找到这条记录，操作失败！");
                    request.setAttribute("result", "failure");
                    return mapping.findForward(IConstants.FAILURE_KEY);
                }
    			String condition = "stock_exchange_id = " + bean.getId();
    			ArrayList sepList = service.getStockExchangeProductList(condition, 0, -1, "id asc");
                Map productMap = new HashMap(); //存放产品 名称  跟产品编号
                Iterator sepIter = sepList.listIterator();
                Set keySet = new HashSet();
                while (sepIter.hasNext()) {
                    StockExchangeProductBean sep = (StockExchangeProductBean) sepIter.next();
                    String productIdTemp = String.valueOf(sep.getProductId());
                    if (!keySet.contains(productIdTemp)) { //copy ProductStockAction  stockExchange 方法代码
                        keySet.add(productIdTemp);
                        voProduct product = (voProduct) wareService.getProduct(sep.getProductId());
                        if (product != null) {
                            productMap.put(Integer.valueOf(product.getId()), product);
                        }
                    }
                }
                List storageList=null;
                storageList=cargoService.getCargoInfoStorageList("area_id="+bean.getStockInArea(), -1, -1, null);//所有地区仓库列表
                request.setAttribute("storageList", storageList);
                request.setAttribute("sepList", sepList);
                request.setAttribute("productMap", productMap);
                request.setAttribute("exChangeId", String.valueOf(exChangeId));
                request.setAttribute("exChangeIdCode", bean.getCode());
            }
 
            request.setAttribute("paging", paging);
            request.setAttribute("list", list);
		}finally{
			service.releaseAll();
		}  
		 
		return mapping.findForward("success");
	}
	
	/**
	 * 添加上架单  作业单
	 */
	public ActionForward addUpOperation(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String[] sExProductId = request.getParameterValues("sExProductId");
		String storageCode=StringUtil.convertNull(request.getParameter("storageCode"));//目的仓库
		String storeType=StringUtil.convertNull(request.getParameter("storeType"));//目的存放类型
		StringBuilder sExProductIds = new StringBuilder("id in (");
		if(sExProductId==null|| sExProductId.length<=0){
			request.setAttribute("tip", "请至少选择一个产品");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		for(int i=0;i<sExProductId.length;i++){
			sExProductIds.append(sExProductId[i]);
			if(i!=sExProductId.length-1){
				sExProductIds.append(",");
			} 
		}
		sExProductIds.append(") ");
		int cargoOperId = 0;
		synchronized(cargoLock){
			WareService wareService = new WareService();
			IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			ICargoService cargoService=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, service.getDbOp()); //StockExchangeProductBean.java
			ConsignmentService consignmentService = new ConsignmentService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
			boolean bool = true;
			try{
				service.getDbOp().startTransaction();
				ArrayList stockExProdList = service.getStockExchangeProductList(sExProductIds.toString(),-1,-1,"id desc");
				
				if(stockExProdList.size()<0){
					request.setAttribute("tip", "添加失败，没获取到调拨单中商品信息");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				
				StockExchangeProductBean sep = (StockExchangeProductBean)stockExProdList.get(0);
				StockExchangeBean seb = service.getStockExchange(" id="+sep.getStockExchangeId());
				CargoOperationBean cargoOperationBean = new CargoOperationBean();
				cargoOperationBean.setSource(seb.getCode());
				cargoOperationBean.setStatus(CargoOperationProcessBean.OPERATION_STATUS1);
				cargoOperationBean.setStockOutType(2);//作业单来源存放类型 0 散件区 1 整件区 2缓存区 
				cargoOperationBean.setStockInType(Integer.parseInt(storeType));//作业单目的存放类型 0 散件区 1 整件区
				cargoOperationBean.setCreateDatetime(DateUtil.getNow());
				cargoOperationBean.setCreateUserId(user.getId());
				cargoOperationBean.setCreateUserName(user.getUsername());
				cargoOperationBean.setType(0);//作业类型  0上架单
				cargoOperationBean.setLastOperateDatetime(DateUtil.getNow());//最近操作时间
				cargoOperationBean.setStorageCode(storageCode);// 作业单操作的仓库 完整编号
				String code = cargoService.getCargoOperationMaxIdCode("HWS");//作业表编号 重新生成的
				cargoOperationBean.setLastOperateDatetime(DateUtil.getNow());
				if(code.equals("")){
					request.setAttribute("tip", "添加失败,编号获取异常");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				} 
				cargoOperationBean.setCode(code);
				cargoOperationBean.setStockOutArea(seb.getStockInArea());
				cargoOperationBean.setStockInArea(seb.getStockInArea());
				bool = cargoService.addCargoOperation(cargoOperationBean);
				if(!bool)  returnError("添加下架作业单异常");
				
				cargoOperId = service.getDbOp().getLastInsertId();
				
				String conditionCargo="status in (7,8,9) and type=0  and source='"+cargoOperationBean.getSource()+"'";
				String conditionCargoLock="status in(2,3,4,5,6) and type=0 and source='"+cargoOperationBean.getSource()+"'"; //得到上架的商品冻结的数量 
				Map cargoOperCarogMap = cargoService.getCoCNumOrLockNum(conditionCargo); 
				Map cargoOperLockMap= cargoService.getCoCNumOrLockNum(conditionCargoLock);
				
				
				String newCargoIds = "-1";
				for(int i=0;i<stockExProdList.size();i++){
					StockExchangeProductBean stockProductBean = (StockExchangeProductBean)stockExProdList.get(i);//上架单产品
					StockExchangeBean stockExchange = service.getStockExchange("id = "+stockProductBean.getStockExchangeId());//上架单
					//备选目的货位
					ArrayList cspList = new ArrayList();
					if( consignmentService.isProductConsignment(stockProductBean.getProductId()) )	{
						cargoService.getCargoAndProductStockWithStockAreaCodeRestrictList("ci.status=0 and ci.store_type="+storeType+" and ci.stock_type=0 and cps.product_id="+stockProductBean.getProductId()+" and ci.area_id = "+stockExchange.getStockInArea()+" and ci.whole_code like '"+storageCode+"%'", 0, -1, " ci.whole_code asc ", Constants.CONSIGNMENT_STOCK_AREA);
					} else {
						cargoService.getCargoAndProductStockList("ci.status=0 and ci.store_type="+storeType+" and ci.stock_type=0 and cps.product_id="+stockProductBean.getProductId()+" and ci.area_id = "+stockExchange.getStockInArea()+" and ci.whole_code like '"+storageCode+"%'", 0, -1, " ci.whole_code asc ");
					}
					ArrayList zanCunList = cargoService.getCargoAndProductStockList("ci.status=0 and ci.store_type = 2 and ci.stock_type=0 and cps.product_id="+stockProductBean.getProductId()+" and ci.area_id = "+stockExchange.getStockInArea(), 0, -1, " ci.store_type asc ");
					if(zanCunList==null || zanCunList.size()<=0){
						request.setAttribute("tip", "添加失败,没获取到商品的源库货位!");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					
					CargoOperationLogBean log = new CargoOperationLogBean();
					log.setOperId(cargoOperId);
					log.setOperAdminId(user.getId());
					log.setOperAdminName(user.getUsername());
					log.setOperDatetime(DateUtil.getNow());
					
					StringBuilder logRemark=new StringBuilder("制单：");
					voProduct product = wareService.getProduct(stockProductBean.getProductId());
					logRemark.append("商品");
					logRemark.append(product.getCode());
					logRemark.append("，");
					int productIdTemp = stockProductBean.getProductId();
					int stockCount=0,stockLockCount=0;
					 
					if(cargoOperCarogMap.containsKey(String.valueOf(productIdTemp))){//获取已经上架数量
						CargoOperationCargoBean beanTemp= (CargoOperationCargoBean)cargoOperCarogMap.get(String.valueOf(productIdTemp));
						stockCount=stockProductBean.getStockOutCount()-beanTemp.getStockCount();
					}else{
						stockCount=stockProductBean.getStockOutCount();
					}
					if(cargoOperLockMap.containsKey(String.valueOf(productIdTemp))){//获取锁定的数量
						CargoOperationCargoBean beanTemp= (CargoOperationCargoBean)cargoOperLockMap.get(String.valueOf(productIdTemp));
						stockLockCount=beanTemp.getStockCount();
						stockCount= stockCount-stockLockCount;
					}else{
						stockLockCount =0;
					}
					
					if(cspList!=null && cspList.size()>0){//存在已绑定该产品的货位
						int outId=0; //存取源货位 id 
							CargoProductStockBean cpsBean = (CargoProductStockBean)cspList.get(0) ;
							CargoInfoBean inCi  = cpsBean.getCargoInfo();//目的货位
							
							if(zanCunList!=null && zanCunList.size()>0){ //源 是否缓存区有该商品
								CargoProductStockBean zanCunCpsBean = (CargoProductStockBean)zanCunList.get(0) ;
								CargoOperationCargoBean cocOut = new CargoOperationCargoBean();
								cocOut.setOperId(cargoOperId);
								cocOut.setInCargoProductStockId(cpsBean.getId());
								cocOut.setOutCargoProductStockId(zanCunCpsBean.getId());
								outId=zanCunCpsBean.getId();
								cocOut.setOutCargoWholeCode(zanCunCpsBean.getCargoInfo().getWholeCode());
								cocOut.setProductId(stockProductBean.getProductId());
								cocOut.setType(1);
								bool = cargoService.addCargoOperationCargo(cocOut);
								if(!bool)  returnError("添加原货位数据异常");
							}
	 
							int mubiaoNum = inCi.getMaxStockCount()-inCi.getSpaceLockCount()-cpsBean.getStockCount()-cpsBean.getStockLockCount();
							if(mubiaoNum<0) mubiaoNum=0;
							 
							int tuijianNum = stockCount;
							if(tuijianNum<0) tuijianNum=0;
							
							CargoOperationCargoBean inCoc = new CargoOperationCargoBean(); //目的货源
							inCoc.setOperId(cargoOperId);
							inCoc.setInCargoProductStockId(cpsBean.getId());
							inCoc.setOutCargoProductStockId(outId);
							inCoc.setInCargoWholeCode(inCi.getWholeCode());
							inCoc.setProductId(stockProductBean.getProductId());
							if(tuijianNum==0){//如果作业量是0就不勾选了
								inCoc.setUseStatus(0);
							}else{
								inCoc.setUseStatus(1);
							}
							inCoc.setType(0);
							if(tuijianNum>mubiaoNum){
								inCoc.setStockCount(mubiaoNum);
								stockCount=stockCount-mubiaoNum; //有多个货位的时候需要计算  分配了多少数量的商品
							}else {
								inCoc.setStockCount(tuijianNum);
								stockCount=stockCount-tuijianNum;//有多个货位的时候需要计算  分配了多少数量的商品
							}
							if(inCoc.getOutCargoWholeCode()!=null){
								logRemark.append("源货位（");
								logRemark.append(inCoc.getOutCargoWholeCode());
								logRemark.append("），");
							}
							if(inCoc.getInCargoWholeCode()!=null){
								logRemark.append("目的货位（");
								logRemark.append(inCoc.getInCargoWholeCode());
								logRemark.append("），");
							}
							logRemark.append("上架量（");
							logRemark.append(inCoc.getStockCount());
							logRemark.append("）");
							bool = cargoService.addCargoOperationCargo(inCoc);
							if(!bool)  returnError("添加目的货位数据异常");
					}else{
						synchronized(CargoOperationAction.cargoAssignLock){
							voProductLine productLine = wareService.getProductLine("product_line_catalog.catalog_id in ("+product.getParentId1()+","+product.getParentId2()+")");
							CargoInfoBean cargo = null;
							if(productLine != null){
								if( consignmentService.isProductConsignment(product.getId())) {
									cargo = cargoService.getCargoInfoWithStockAreaCodeRestrict("ci.product_line_id = "+productLine.getId()+" and ci.stock_type=0 and ci.status = "+CargoInfoBean.STATUS1+" and ci.store_type="+storeType+" and ci.whole_code like '"+storageCode+"%' and ci.id not in ("+newCargoIds+") order by ci.whole_code asc limit 1", Constants.CONSIGNMENT_STOCK_AREA);
								} else {
									cargo = cargoService.getCargoInfo("product_line_id = "+productLine.getId()+" and stock_type=0 and status = "+CargoInfoBean.STATUS1+" and store_type="+storeType+" and whole_code like '"+storageCode+"%' and id not in ("+newCargoIds+") order by whole_code asc limit 1");
								}
								if(cargo == null){
									if( consignmentService.isProductConsignment(product.getId())) { 
										cargo = cargoService.getCargoInfoWithStockAreaCodeRestrict("ci.status = "+CargoInfoBean.STATUS1+" and ci.stock_type=0 and ci.store_type ="+storeType+" and ci.whole_code like '"+storageCode+"%' and ci.id not in ("+newCargoIds+") order by ci.whole_code asc limit 1", Constants.CONSIGNMENT_STOCK_AREA);
									} else {
										cargo = cargoService.getCargoInfo("status = "+CargoInfoBean.STATUS1+" and stock_type=0 and store_type ="+storeType+" and whole_code like '"+storageCode+"%' and id not in ("+newCargoIds+") order by whole_code asc limit 1");
									}
								}
							}else{
								if( consignmentService.isProductConsignment(product.getId())) { 
									cargo = cargoService.getCargoInfoWithStockAreaCodeRestrict("ci.status = "+CargoInfoBean.STATUS1+" and ci.stock_type=0 and ci.store_type ="+storeType+" and ci.whole_code like '"+storageCode+"%' and ci.id not in ("+newCargoIds+") order by ci.whole_code asc limit 1", Constants.CONSIGNMENT_STOCK_AREA);
								} else {
									cargo = cargoService.getCargoInfo("status = "+CargoInfoBean.STATUS1+" and stock_type=0 and store_type ="+storeType+" and whole_code like '"+storageCode+"%' and id not in ("+newCargoIds+") order by whole_code asc limit 1");
								}
							}
							//绑定产品，添加产品库存数据

							if(cargo!=null ){
								CargoProductStockBean cps = new CargoProductStockBean();
								cps.setCargoId(cargo.getId());
								cps.setProductId(product.getId());
								cargoService.addCargoProductStock(cps);
								cargoService.updateCargoInfo("status = 0", "id = "+cargo.getId());
								newCargoIds = newCargoIds+","+cargo.getId();
								
								int lastCpsId = service.getDbOp().getLastInsertId();
								int mubiaoNum = cargo.getMaxStockCount();
								if(mubiaoNum<0) mubiaoNum=0;
								int tuijianNum = stockCount-stockLockCount ;
								if(tuijianNum<0) tuijianNum=0;
								int cpsBeanOutId =0;
								String cpsBeanOutStr="";
								if(zanCunList!=null && zanCunList.size()>0){
									CargoProductStockBean zanCunCpsBean = (CargoProductStockBean)zanCunList.get(0) ;
									CargoOperationCargoBean cocOut = new CargoOperationCargoBean();
									cocOut.setOperId(cargoOperId);
									cocOut.setInCargoProductStockId(lastCpsId);
									cocOut.setOutCargoProductStockId(zanCunCpsBean.getId());
									cocOut.setOutCargoWholeCode(zanCunCpsBean.getCargoInfo().getWholeCode());
									cocOut.setProductId(stockProductBean.getProductId());
									cocOut.setType(1);
									bool = cargoService.addCargoOperationCargo(cocOut);
									if(!bool)  returnError("添加原货位数据异常");
									cpsBeanOutId= zanCunCpsBean.getId();
									cpsBeanOutStr = zanCunCpsBean.getCargoInfo().getWholeCode();
								}

								CargoOperationCargoBean inCoc = new CargoOperationCargoBean();//添加目的货位信息
								inCoc.setOperId(cargoOperId);
								inCoc.setInCargoProductStockId(lastCpsId);
								inCoc.setInCargoWholeCode(cargo.getWholeCode());
								inCoc.setOutCargoProductStockId(cpsBeanOutId);
								inCoc.setOutCargoWholeCode(cpsBeanOutStr);
								inCoc.setProductId(stockProductBean.getProductId());
								if(tuijianNum==0){
									inCoc.setUseStatus(0);
								}else{
									inCoc.setUseStatus(1);
								}
								inCoc.setType(0);
								if(tuijianNum>mubiaoNum){
									inCoc.setStockCount(mubiaoNum);
									stockCount=stockCount-mubiaoNum; //有多个货位的时候需要计算  分配了多少数量的商品
								}else {
									inCoc.setStockCount(tuijianNum);
									stockCount=stockCount-tuijianNum;
								}
								if(inCoc.getOutCargoWholeCode()!=null){
									logRemark.append("源货位（");
									logRemark.append(inCoc.getOutCargoWholeCode());
									logRemark.append("），");
								}
								if(inCoc.getInCargoWholeCode()!=null){
									logRemark.append("新分配目的货位（");
									logRemark.append(inCoc.getInCargoWholeCode());
									logRemark.append("），");
								}
								logRemark.append("上架量（");
								logRemark.append(inCoc.getStockCount());
								logRemark.append("），");
								bool =  cargoService.addCargoOperationCargo(inCoc);
								if(!bool)  returnError("添加目的货位数据异常");

								cargoService.updateCargoOperation("storage_code = '"+cargo.getWholeCode().substring(0,cargo.getWholeCode().indexOf("-"))+"'", "id = "+cargoOperId);
							}else{
								CargoProductStockBean zanCunCpsBean = (CargoProductStockBean)zanCunList.get(0) ;
								CargoOperationCargoBean cocOut = new CargoOperationCargoBean();
								cocOut.setOperId(cargoOperId);
								cocOut.setInCargoProductStockId(0);
								cocOut.setOutCargoProductStockId(zanCunCpsBean.getId());
								cocOut.setOutCargoWholeCode(zanCunCpsBean.getCargoInfo().getWholeCode());
								cocOut.setProductId(stockProductBean.getProductId());
								cocOut.setType(1);
								bool = cargoService.addCargoOperationCargo(cocOut);
								if(!bool)  returnError("添加无货位数据异常");
							}

						}
					}
					log.setRemark(logRemark.toString());
					bool = cargoService.addCargoOperationLog(log);
				}
				if(!bool)  returnError("添加日志文件异常");
				
				CargoOperLogBean operLog=new CargoOperLogBean();//员工操作日志
				operLog.setOperId(cargoService.getCargoOperation("code='"+cargoOperationBean.getCode()+"'").getId());
				operLog.setOperCode(cargoOperationBean.getCode());
				CargoOperationProcessBean process=cargoService.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS1);
				operLog.setOperName(process.getOperName());
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(CargoOperLogBean.EFFECT_TIME1);
				operLog.setRemark("");
				operLog.setPreStatusName("无");
				operLog.setNextStatusName(process.getStatusName());
				if(!cargoService.addCargoOperLog(operLog)){
					returnError("添加日志数据时发生异常");
				}
				
				request.setAttribute("tip","添加成功"); //success
				request.setAttribute("url", "cargoOper.do?method=showEditCargoOperation&operationId="+cargoOperId);
				service.getDbOp().commitTransaction();
			}catch(Exception e){
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
				request.setAttribute("tip",e.getMessage());
				request.setAttribute("url","cargo/changeOperationUp.jsp");
				return mapping.findForward("tip");
			}finally{
				service.releaseAll();
			} 
		}
		return mapping.findForward("tip");
	 
	}
	
	private static void returnError(String msg) throws SQLException{
		 throw new SQLException(msg);
	}
	
	/**
	 * 上架单列表
	 */
	public ActionForward shelfUpList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		int countPerPage=20;
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService productStockService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ArrayList operationList =  new ArrayList();
		try{
			String operationCode = StringUtil.convertNull(request.getParameter("operationCode"));
			String productCode = StringUtil.convertNull(request.getParameter("productCode"));  
			String userName = StringUtil.convertNull(request.getParameter("userName"));  
			String cargoCode=StringUtil.convertNull(request.getParameter("cargoCode"));
			String exChangeCode = StringUtil.convertNull(request.getParameter("exChangeCode"));
			String areaId = StringUtil.convertNull(request.getParameter("areaId"));
			String[] statuses = request.getParameterValues("status");
			
			StringBuilder paramBuf = new StringBuilder();
			StringBuilder operationQuery = new StringBuilder("select oo.* from  cargo_operation oo left join cargo_operation_cargo cc on cc.oper_id = oo.id where  oo.type=0");
			StringBuilder operationCount = new StringBuilder("select count(dd.num_id) from(select count(oo.id) num_id  from  cargo_operation oo left join cargo_operation_cargo cc on cc.oper_id = oo.id where oo.type=0");
			
			if(!operationCode.trim().equals("")){
				operationQuery.append(" and oo.code='"+operationCode+"'");
				operationCount.append(" and oo.code='"+operationCode+"'");
				paramBuf.append("&operationCode="+operationCode);
			}
			if(!"".equals(userName.trim())){
				operationQuery.append(" and oo.create_user_name='"+userName + "'");
				operationCount.append(" and oo.create_user_name='"+userName + "'");
				paramBuf.append("&userName="+userName);
			}
			if(areaId != null && !"-1".equals(areaId) && !"".equals(areaId.trim())){
				operationQuery.append(" and oo.stock_in_area=" + areaId);
				operationCount.append(" and oo.stock_in_area=" + areaId);
				paramBuf.append("&areaId="+areaId);
			}
			if(!productCode.trim().equals("")){
				voProduct product = (voProduct) wareService.getProduct(productCode);
				if(product == null){
					PagingBean paging = new PagingBean(1, 0,countPerPage);
					paging.setPrefixUrl("admin/cargoUpOper.do?method=shelfUpList"+paramBuf.toString());
					request.setAttribute("paging", paging);
					request.setAttribute("operationList", new ArrayList());
					request.setAttribute("para", paramBuf.toString());
					return mapping.findForward("list");
				}else{
					int productId= product.getId();
					operationQuery.append(" and cc.product_id="+productId);
					operationCount.append(" and cc.product_id="+productId);
					paramBuf.append("&productCode="+productCode);
				}
			}
			
			if(!exChangeCode.trim().equals("")){
				operationQuery.append(" and oo.source='"+exChangeCode+"'");
				operationCount.append(" and oo.source='"+exChangeCode+"'");
				paramBuf.append("&exChangeCode="+exChangeCode);
			}
			
			if(!cargoCode.trim().equals("")){
				operationQuery.append(" and cc.in_cargo_whole_code='"+cargoCode+"'");
				operationCount.append(" and cc.in_cargo_whole_code='"+cargoCode+"'");
				paramBuf.append("&cargoCode="+cargoCode);
			}
			if(statuses!=null && statuses.length>0){
				StringBuilder sb_temp = new StringBuilder(" and oo.status in (");
				for(int i=0;i<statuses.length;i++){
					if(statuses[i].equals("3")){
						sb_temp.append("3,4,5,6");
					}else if(statuses[i].equals("7")){
						sb_temp.append("7,8,9");
					}else{
						sb_temp.append(statuses[i]);
					}
					if(i!=statuses.length-1){
						sb_temp.append(",");
					}
					paramBuf.append("&status="+statuses[i]);
				}
				sb_temp.append(")");
				operationQuery.append(sb_temp.toString());
				operationCount.append(sb_temp.toString());
			}
			operationQuery.append(" GROUP by oo.id ");
			operationCount.append(" GROUP by oo.id ) dd");
			operationQuery.append(" order by oo.id desc");
			
			
			int totalCount = service.getTablesCount(operationCount.toString()); //根据条件得到 总数量
			int pageIndex = StringUtil.parstInt(request.getParameter("pageIndex"));
            PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
            operationList = service.getCargoOperationCascade(operationQuery.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage);//获取下架作业单列表
            
            Iterator itr= operationList.iterator(); //获取调拨单id 方便查询该调拨单明细
            while(itr.hasNext()){
            	CargoOperationBean bean = (CargoOperationBean)itr.next();
            	StockExchangeBean stockExchangeBean = productStockService.getStockExchange(" code='"+bean.getSource()+"'");
            	if(stockExchangeBean != null){
            		bean.setExChnageId(stockExchangeBean.getId());
            	}
            	CargoOperationProcessBean process=service.getCargoOperationProcess("id="+bean.getStatus());
            	if(process!=null){
            		bean.setStatusName(process.getStatusName());
            	}else{
            		bean.setStatusName("");
            	}
            }
            
			paging.setPrefixUrl("admin/cargoUpOper.do?method=shelfUpList"+paramBuf.toString());
			request.setAttribute("paging", paging);
			request.setAttribute("areaId", areaId);
			request.setAttribute("operationList", operationList);
			request.setAttribute("para", paramBuf.toString());
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}  
		 
		return mapping.findForward("list");
	}
	
	
	
}

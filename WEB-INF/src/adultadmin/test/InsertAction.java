package adultadmin.test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.mws.model.SubmitFeedResponse;
import com.amazonaws.mws.model.SubmitFeedResult;
import com.amazonaws.mws.samples.SubmitFeedSample;
import com.amazonservices.mws.orders._2013_09_01.samples.GetOrderSample;
import com.caucho.services.server.Service;

import freemarker.template.TemplateException;
import mmb.msg.TemplateMarker;
import mmb.stock.IMEI.IMEIService;
import mmb.stock.stat.DeliverAreaBean;
import mmb.stock.stat.DeliverService;
import mmb.stock.stat.ProductWarePropertyBean;
import mmb.stock.stat.ProductWarePropertyLogBean;
import mmb.stock.stat.ProductWarePropertyService;
import mmb.stock.stat.SortingBatchBean;
import mmb.stock.stat.SortingBatchGroupBean;
import mmb.stock.stat.SortingBatchOrderBean;
import mmb.stock.stat.SortingInfoService;
import mmb.stock.stat.StatService;
import mmb.ware.WareService;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoDeptBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInfoLogBean;
import adultadmin.bean.cargo.CargoInfoPassageBean;
import adultadmin.bean.cargo.CargoInfoShelfBean;
import adultadmin.bean.cargo.CargoInfoStockAreaBean;
import adultadmin.bean.cargo.CargoOperLogBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationLogBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.cargo.CargoStaffBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.CargoServiceImpl;
import adultadmin.service.impl.ProductStockServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import freemarker.template.TemplateException;


public class InsertAction{
	@SuppressWarnings("unchecked")
	public List<ProductStockBean> productLockCountStatistics(HttpServletRequest request,HttpServletResponse response){
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		CargoServiceImpl cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE,dbOp);
		List<ProductStockBean> list = new ArrayList<ProductStockBean>();
		List<CargoProductStockBean> cpsList = null;
		ResultSet rs = null;
		try {
			String areaId = request.getParameter("areaId");
			String stockType = request.getParameter("stockType");
			if(areaId == null){
				return null;
			}
			StringBuilder condition = new StringBuilder();
			condition.append("cps.stock_lock_count>0 ");
			if(areaId != null && !"".equals(areaId) && !"-1".equals(areaId)){
				condition.append(" and ci.area_id=" + areaId);
			}
			if(stockType != null && !"".equals(stockType) && !"-1".equals(stockType)){
				condition.append(" and ci.stock_type=" + stockType);
			}
			long startTime=System.currentTimeMillis();   //获取开始时间
			int total = cargoService.getCargoAndProductStockCount(condition.toString());
			int index = 0;
			int count = 10000;
			int page =  total/count;
			if(page == 0){
				page = 1;
			}
			for(int i=0;i<page;i++){
				index = i;
				cpsList = cargoService.getCargoAndProductStockList(condition.toString(), index*count, count, null);
				if(cpsList != null && cpsList.size() > 0){
					count = 0; 
					String query = null;
					for(CargoProductStockBean cpsBean : cpsList){
						CargoInfoBean ciBean = cpsBean.getCargoInfo();
						ProductStockBean bean = new ProductStockBean();
						int lockCount = 0;
						//作业单
				        query = "select coc.stock_count from cargo_operation co " + 
				        		"join cargo_operation_cargo coc on coc.oper_id=co.id and coc.use_status=1 " + 
				        		"join product p on p.id=coc.product_id " +
				        		"where co.status in (2,3,11,12,20,21,29,30) " +
				        		"and co.effect_status in (0,1) " +
				        		"and p.id=" + cpsBean.getProductId() +" and coc.out_cargo_whole_code='" + cpsBean.getCargoInfo().getWholeCode() +"' " ;
				        query = DbOperation.getPagingQuery(query, -1, -1);
				        rs = dbOp.executeQuery(query);
				        while(rs.next()){
				        	lockCount+=rs.getInt("coc.stock_count");
						}
				        //报损报溢
				        query = "SELECT bpc.count FROM bsby_operationnote bo join bsby_product_cargo bpc on bpc.bsby_oper_id=bo.id " + 
				        		"join product p on p.id=bpc.bsby_product_id join cargo_info ci on ci.id=bpc.cargo_id " +
				        		"where bo.current_type in (1,6) and p.id=" + cpsBean.getProductId() +
				        		" and ci.whole_code='" + cpsBean.getCargoInfo().getWholeCode() +"' " +
		        				" and bo.warehouse_type=" + ciBean.getStockType() + " and ci.area_id=" + ciBean.getAreaId();
				        query = DbOperation.getPagingQuery(query, -1, -1);
				        rs = dbOp.executeQuery(query);
				        while(rs.next()){
				        	lockCount+=rs.getInt("bpc.count");
				        }
				        //订单
				        query = "select osp.stockout_count FROM order_stock os join order_stock_product osp on osp.order_stock_id=os.id " + 
				        		"join order_stock_product_cargo ospc on ospc.order_stock_product_id=osp.id " +
				        		"join product p on p.id=osp.product_id where os.status=5 " + 
		        				"and p.id=" + cpsBean.getProductId() +" and ospc.cargo_whole_code='" + cpsBean.getCargoInfo().getWholeCode() +"' " +
		        				" and osp.stock_type=" + ciBean.getStockType() + " and osp.stock_area=" + ciBean.getAreaId();
				        query = DbOperation.getPagingQuery(query, -1, -1);
				        rs = dbOp.executeQuery(query);
				        while(rs.next()){
				        	lockCount+=rs.getInt("osp.stockout_count");
				        }
				        //调拨单
				        query = "select sep.stock_out_count from stock_exchange se join stock_exchange_product sep on sep.stock_exchange_id=se.id " +
				        		"join stock_exchange_product_cargo sepc on sepc.stock_exchange_id=se.id " +
				        		"join product p on p.id=sep.product_id join cargo_info ci on ci.id=sepc.cargo_info_id " +
				        		"where se.status in (2,3,5,6) "+ 
		        				" and p.id=" + cpsBean.getProductId() +" and ci.whole_code='" + cpsBean.getCargoInfo().getWholeCode() +"' " +
		        				" and se.stock_out_type=" + ciBean.getStockType() + " and se.stock_out_area=" + ciBean.getAreaId();
				        query = DbOperation.getPagingQuery(query, -1, -1);
				        rs = dbOp.executeQuery(query);
				        while(rs.next()){
				        	lockCount+=rs.getInt("sep.stock_out_count");
				        }
				        //货位异常单
				        query = "select sap.lock_count from sorting_abnormal sa join sorting_abnormal_product sap " +
				        		"on sa.id=sap.sorting_abnormal_id where sap.status in (0,1,2,3) " +
		        				"and sap.product_id=" + cpsBean.getProductId() +" and sap.cargo_whole_code='" + cpsBean.getCargoInfo().getWholeCode() +"' " +
		        				" and sa.ware_area=" + ciBean.getAreaId();
				        query = DbOperation.getPagingQuery(query, -1, -1);
				        rs = dbOp.executeQuery(query);
				        while(rs.next()){
				        	lockCount+=rs.getInt("sap.lock_count");
				        }
				        if(cpsBean.getStockLockCount() != lockCount){
				        	bean.setProductId(cpsBean.getProductId());
				        	bean.setWhole_code(cpsBean.getCargoInfo().getWholeCode());
				        	bean.setStock(cpsBean.getStockLockCount());
				        	bean.setLockCount(lockCount);
				        	bean.setAllStock(cpsBean.getStockLockCount()-lockCount);
				        	bean.setArea(ciBean.getAreaId());
				        	bean.setType(ciBean.getStockType());
				        	bean.setId(total);
				        	list.add(bean);
				        }
					}
					long endTime=System.currentTimeMillis(); //获取结束时间
					if(list != null && list.size()>0){
						list.get(0).setStatus(Integer.parseInt(endTime-startTime + ""));
					}
					if(rs != null){
						rs.close();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return list;
	}
	public List<ProductStockBean> productStockCompare(HttpServletRequest request,HttpServletResponse response){
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE);
		CargoServiceImpl cargoService = new CargoServiceImpl(IBaseService.CONN_IN_SERVICE,dbOp);
		ProductStockServiceImpl psService = new ProductStockServiceImpl(IBaseService.CONN_IN_SERVICE,dbOp);
		List<ProductStockBean> list = new ArrayList<ProductStockBean>();
		List<ProductStockBean> psList = null;
		List<CargoProductStockBean> cpsList = null;
		try {
			String areaId = request.getParameter("areaId");
			String stockType = request.getParameter("stockType");
			if(areaId == null){
				return null;
			}
			StringBuilder sql = new StringBuilder();
			sql.append("1=1 ");
			if(areaId != null && !"".equals(areaId) && !"-1".equals(areaId)){
				sql.append(" and area=" + areaId);
			}
			if(stockType != null && !"".equals(stockType) && !"-1".equals(stockType)){
				sql.append(" and type=" + stockType);
			}
			long startTime=System.currentTimeMillis();   //获取开始时间
			int total = psService.getProductStockCount(sql.toString());
			int index = 0;
			int count = 10000;
			int page =  total/count;
			if(page == 0){
				page = 1;
			}
			for(int i=0;i< page;i++){
				index = i-1;
				psList = psService.getProductStockList(sql.toString(),index*count ,count , null);
				if(psList != null && psList.size() > 0){
					for(ProductStockBean psBean : psList){
						ProductStockBean bean = new ProductStockBean();
						int psCount = psBean.getLockCount() + psBean.getStock();
						cpsList = cargoService.getCargoAndProductStockList("cps.product_id=" + psBean.getProductId() 
								+ " and ci.area_id=" + psBean.getArea() + " and ci.stock_type=" + psBean.getType(), -1, -1, null);
						if(cpsList == null || cpsList.size() == 0){
							continue;
						}
						int cpsCount = 0;
						for(CargoProductStockBean cpsBean : cpsList){
							cpsCount += (cpsBean.getStockCount() + cpsBean.getStockLockCount());
						}
						if(psCount != cpsCount){
							bean.setArea(psBean.getArea());
							bean.setType(psBean.getType());
							bean.setProductId(psBean.getProductId());
							bean.setStock(psCount);
							bean.setLockCount(cpsCount);
							bean.setAllStock(psCount-cpsCount);
							bean.setId(total);
							list.add(bean);
						}
					}
				}
			}
			long endTime=System.currentTimeMillis(); //获取结束时间
			if(list != null && list.size()>0){
				list.get(0).setStatus(Integer.parseInt(endTime-startTime + ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return list;
	}
	public void insert(HttpServletRequest request,HttpServletResponse response){
		InsertService is=new InsertService();
		ArrayList orderList=new ArrayList();
		ArrayList productList=new ArrayList();
		int count=Integer.parseInt(request.getParameter("count"));
		String pcode="";
		if(request.getParameter("select").equals("2")){
			pcode=request.getParameter("selected");
		}
		for(int i=0;i<count;i++){
			voOrder o=is.insertOrder(pcode);
			orderList.add(o);
			int orderId=o.getId();
			voProduct product=findProduct(orderId);
			productList.add(product);
		}
		request.setAttribute("orderList", orderList);
		request.setAttribute("productList", productList);
	}
	
	public boolean find(String code){
		boolean b=new InsertService().find(code);
		return b;
	}
	
	public voProduct findProduct(int orderId){
		InsertService is=new InsertService();
		voProduct product=is.findProduct(orderId);
		return product;
	}
	
//	public void addOrder(HttpServletRequest request,HttpServletResponse response,String count){
//		voUser user = (voUser)request.getSession().getAttribute("userView");
//		if(user == null){
//			return;
//		}
//		DbOperation dbOp = new DbOperation();
//		dbOp.init("adult");
//		WareService wareService = new WareService(dbOp);
//		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp); 
//		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,dbOp);
//		IProductPackageService packageService=ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, dbOp);
//		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
//        
//		int c=Integer.parseInt(count);
//		try {
//			//增城合格库散件区库存记录
//			List cpsList=service.getCargoAndProductStockList("ci.area_id=3 and ci.stock_type=0 and ci.store_type in (0,4)", -1, -1, null);
//			for(int i=0;i<cpsList.size();i++){
//				CargoProductStockBean cps=(CargoProductStockBean)cpsList.get(i);
//				if(cps.getId()==0){
//					cpsList.remove(i);
//					i--;
//					continue;
//				}
//			}
//			int newOrder=0;
//			for(int i=0;i<c;i++){
//				dbOp.startTransaction();
//				//添加user_order
//				voOrder order=new voOrder();
//				order.setUserId(user.getId());
//				order.setName(user.getUsername());
//				order.setPhone("13501103617");
//				order.setAddress("黑龙江省哈尔滨市松北区三电街道(电话通知)");
//				order.setPostcode("150000");
//				order.setBuyMode(0);
//				//order.setproductCount();
//				order.setFlag(0);
//				order.setPrice(15);
//				//order.sethide();
//				order.setDeliverType(0);
//				order.setRemitType(0);
//				order.setRemitDatetime(DateUtil.getNow());
//				order.setStatus(3);
//				String orderCode=CodeUtil.getOrderCode(1);
//				order.setCode(orderCode);
//				order.setRemark("");
//				order.setAdmin(0);
//				order.setDiscount(1);
//				order.setCp("");
//				order.setStockout(1);
//				order.setPhone2("");
//				order.setPrepayDeliver(0);
//				order.setOperator("");
//				order.setFr(0);
//				order.setAgent(0);
//				order.setAgentMark("");
//				order.setAgentRemark("");
//				order.setIsOrderReimburse(0);
//				order.setIsReimburse(0);
//				order.setRealPay(0);
//				order.setPostage(15);
//				order.setIsOrder(0);
//				order.setDprice(15);
//				order.setImages("");
//				order.setAreano(9);
//				order.setPrePayType(0);
//				order.setSuffix((float)0.11);
//				order.setContactTime(0);
//				order.setUnitedOrders("");
//				order.setFlat(0);
//				order.setGender(0);
//				order.setWebRemark("");
//				order.setEmail("");
//				order.setHasAddPoint(0);
//				order.setOriginOrderId(0);
//				order.setNewOrderId(0);
//				order.setSellerId(user.getId());
//				order.setDealDetail("");
//				order.setCpaStatus(0);
//				order.setCpaBonus(0);
//				order.setCpaPay(0);
//				order.setSellerCheckStatus(0);
//				order.setStockoutRemark("");
//				order.setConsigner("");
//				order.setLastOperTime(DateUtil.getNow());
//				order.setOrderType(4);
//				order.setProductType(3);
//				order.setDeliver(0);
//				order.setStockoutDeal(2);
//				order.setBalanceStatus(0);
//				
//				int orderId= adminService.addOrder(order);
//				order=adminService.getOrder(orderId);
//				
//				
//				//添加order_stock
//				OrderStockBean orderStock=new OrderStockBean();
//				String orderStockCode=CodeUtil.getOrderStockCode();
//				orderStock.setCode(orderStockCode);
//				orderStock.setName(order.getCode() + "_" + DateUtil.getNow().substring(0, 10) + "_出货");
//				orderStock.setOrderId(order.getId());
//				orderStock.setOrderCode(order.getCode());
//				orderStock.setCreateDatetime(DateUtil.getNow());
//				orderStock.setLastOperTime(DateUtil.getNow());
//				orderStock.setRemark("");
//				orderStock.setStockArea(3);
//				orderStock.setStockType(0);
//				orderStock.setStatus(1);
//				orderStock.setCreateUserId(user.getId());
//				orderStock.setStatusStock(4);
//				orderStock.setRealStatusStock(0);
//				orderStock.setDeliver(0);
//				
//				stockService.addOrderStock(orderStock);
//				orderStock=stockService.getOrderStock("code='"+orderStockCode+"'");
//				
//				//添加user_order_product
//				int cpsCount=cpsList.size();
//				if(cpsCount>1000){
//					cpsCount=1000;
//				}
//				
//				Random random=new Random();
//				int productCount=random.nextInt(2);productCount++;
//				for(int j=0;j<productCount;j++){
//					int cpsNum=random.nextInt(cpsCount);
//					CargoProductStockBean cps=(CargoProductStockBean)cpsList.get(cpsNum);
//					CargoInfoBean cargo=service.getCargoInfo("id="+cps.getCargoId());
//					if(cargo==null){
//						dbOp.rollbackTransaction();
//						i--;
//						break;
//					}
//					ProductStockBean productStock=psService.getProductStock("product_id="+cps.getProductId()+" and area=3 and type=0");
//					if(productStock==null){
//						dbOp.rollbackTransaction();
//						i--;
//						break;
//					}
//					voProduct product=adminService.getProduct(productStock.getProductId());
//					if(product==null){
//						dbOp.rollbackTransaction();
//						i--;
//						break;
//					}
//					
//					int stockOutCount=0;
//					if(j==0){//第一种商品1个
//						stockOutCount=1;
//					}else{//第二种商品开始每种两个
//						stockOutCount=2;
//					}
//					
//					//添加user_order_product
//					String addUserOrderProductSql="insert into user_order_product (order_id,product_id,count,name,status,price3,product_price,discount_price,product_discount_id,product_preference_id)"
//						+" values ("+orderId+","+product.getId()+","+stockOutCount+",'',0,50,100,100,0,0)";
//					adminService.executeUpdate(addUserOrderProductSql);
//					
//					String updateOrderStockOutSql="update user_order set stockout=stockout+"+stockOutCount+",order_type=4,product_type=3 where id="+order.getId();
//					adminService.executeUpdate(updateOrderStockOutSql);
//					
//					//添加order_stock_product
//					OrderStockProductBean ospBean=new OrderStockProductBean();
//					ospBean.setOrderStockId(orderStock.getId());
//					ospBean.setStockoutId(productStock.getId());
//					
//					psService.updateProductLockCount(productStock.getId(), stockOutCount);//锁定合格库库存
//					
//					ospBean.setStockoutCount(stockOutCount);
//					ospBean.setProductId(productStock.getProductId());
//					ospBean.setProductCode(product.getCode());
//					ospBean.setStatus(1);
//					ospBean.setCreateDatetime(DateUtil.getNow());
//					ospBean.setDealDatetime(DateUtil.getNow());
//					ospBean.setRemark("批量添加订单");
//					ospBean.setStockArea(3);
//					ospBean.setStockType(0);
//					
//					stockService.addOrderStockProduct(ospBean);
//				}
//				dbOp.commitTransaction();
//				newOrder++;
//			}
//			request.setAttribute("newOrder", newOrder+"");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//	}
	
	/**
	 * 添加三级地址编码
	 * @param request
	 * @param response
	 */
	public void addCityAreaCode(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp); 
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,dbOp);
		IProductPackageService packageService=ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
        
		String allCode=request.getParameter("areaCode");
		try {
			if(allCode!=null){
				dbOp.startTransaction();
				String errorMsg="";
				String errorMsg2="";//编码末位是0的，疑似是二级地址
				String[] codeList=allCode.split("\r\n");
				int count=0;//完成修改的数量
				for(int i=0;i<codeList.length;i++){
					String[] line=codeList[i].split("\t");
					if(line.length<2){
						errorMsg+=codeList[i];
						errorMsg+=",格式错误";
						errorMsg+="<br/>";
						continue;
					}
					String area=line[0].trim().replace(" ", "");
					String code=line[1].trim().replace(" ", "");
					
					String updateSql="update city_area set code='"+code+"' where area='"+area+"'";
					String querySql="select id from city_area where area='"+area+"'";
					ResultSet rs=dbOp.executeQuery(querySql);
					if(!rs.next()){
						if(code.endsWith("0")){
							errorMsg2+=area;
							errorMsg2+=",";
							errorMsg2+=code;
							errorMsg2+=",二级城市";
							errorMsg2+="<br/>";
						}else{
							errorMsg+=area;
							errorMsg+=",";
							errorMsg+=code;
							errorMsg+=",未找到该地区";
							errorMsg+="<br/>";
						}
						continue;
					}
					rs.close();
					dbOp.executeUpdate(updateSql);
					count++;
				}
				request.setAttribute("count", count+"");
				request.setAttribute("errorMsg", errorMsg);
				request.setAttribute("errorMsg2", errorMsg2);
				dbOp.commitTransaction();
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			dbOp.rollbackTransaction();
			e.printStackTrace();
		} finally{
			dbOp.release();
		}
	}
	/**
	 * 添加二级地址编码
	 * @param request
	 * @param response
	 */
	public void addCityAreaCode2(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp); 
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,dbOp);
		IProductPackageService packageService=ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
        
		String allCode=request.getParameter("areaCode");
		try {
			if(allCode!=null){
				dbOp.startTransaction();
				String errorMsg="";
				String errorMsg2="";//编码末位是0的，疑似是二级地址
				String[] codeList=allCode.split("\r\n");
				int count=0;//完成修改的数量
				for(int i=0;i<codeList.length;i++){
					String[] line=codeList[i].split("\t");
					if(line.length<2){
						errorMsg+=codeList[i];
						errorMsg+=",格式错误";
						errorMsg+="<br/>";
						continue;
					}
					String city=line[1].trim().replace(" ", "");
					String code=line[3].trim().replace(" ", "");
					
					String updateSql="update province_city set code='"+code+"' where city='"+city+"'";
					String querySql="select id from province_city where city='"+city+"'";
					ResultSet rs=dbOp.executeQuery(querySql);
					if(!rs.next()){
						if(code.endsWith("0")){
							errorMsg2+=city;
							errorMsg2+=",";
							errorMsg2+=code;
							errorMsg2+=",地级市";
							errorMsg2+="<br/>";
						}else{
							errorMsg+=city;
							errorMsg+=",";
							errorMsg+=code;
							errorMsg+=",未找到该地区";
							errorMsg+="<br/>";
						}
						continue;
					}
					rs.close();
					dbOp.executeUpdate(updateSql);
					count++;
				}
				request.setAttribute("count", count+"");
				request.setAttribute("errorMsg", errorMsg);
				request.setAttribute("errorMsg2", errorMsg2);
				dbOp.commitTransaction();
				
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			dbOp.rollbackTransaction();
			e.printStackTrace();
		} finally{
			dbOp.release();
		}
	}
	/**
	 * 区分四级城市
	 */
	public void deliverArea(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			return;
		}
		String data=request.getParameter("data");
		try {
			if(data!=null&&data.length()>0){
				String[] areaList=data.split("\r\n");
				List newList=new ArrayList();//分析的结果列表
				for(int i=0;i<areaList.length;i++){
					String areaData=areaList[i];
					String[] areaArray=areaData.split("\t");
					if(areaArray.length!=5){
						continue;
					}
					String province=areaArray[0].trim();//省
					String city=areaArray[1].trim();//市
					String area=areaArray[2].trim();//区县
					String fugai=areaArray[3].trim();//覆盖
					String street=areaArray[4].trim();//街道
					if(fugai.equals("未覆盖")){
						continue;
					}else if(fugai.equals("覆盖")){
						String[] newData=new String[4];
						newData[0]=province;
						newData[1]=city;
						newData[2]=area;
						newData[3]="全部";
						newList.add(newData);
					}else if(fugai.equals("未全覆盖")){
						String[] allArea=street.split("、");
						for(int j=0;j<allArea.length;j++){
							String[] newData=new String[4];
							newData[0]=province;
							newData[1]=city;
							newData[2]=area;
							newData[3]=allArea[j];
//							if(!allArea[j].endsWith("街道")&&!allArea[j].endsWith("镇")&&!allArea[j].endsWith("乡")){
//								newList.add(newData);
//							}
							if(allArea[j].endsWith("街道")||allArea[j].endsWith("镇")||allArea[j].endsWith("乡")){
								newList.add(newData);
							}
							
						}
					}
				}
				request.setAttribute("newList", newList);
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			
		}
	}
	
	/**
	 * 宅急送四级城市导入
	 */
	public void addDeliverArea(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		DbOperation dbOp1 = new DbOperation();
		dbOp1.init("adult");
		WareService service = new WareService(dbOp1);
		DeliverService deliverService = ServiceFactory.createDeliverService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		String data=request.getParameter("data");
		try {
			if(data!=null&&data.length()>0){
				String[] areaList=data.split("\r\n");
				String errMsg="";
				String msg="";
				for(int i=0;i<areaList.length;i++){
					String areaData=areaList[i];//每行的信息
					String[] areaArray=areaData.split("\t");//每行信息拆分
					if(areaArray.length!=4){
						continue;
					}
					String province=areaArray[0].trim();//省
					String city=areaArray[1].trim();//市
					String area=areaArray[2].trim();//区县
					String street=areaArray[3].trim();//街道
					DeliverAreaBean daBean = new DeliverAreaBean();
					if(street.equals("全部")){//四级地址全部覆盖
						String sql="select ca.id from provinces p join province_city pc on p.id=pc.province_id " +
								"join city_area ca on pc.id=ca.city_id join area_street ass on ca.id=ass.area_id" +
								" where p.name='"+province+"' and pc.city='"+city+"' and ca.area='"+area+"'";
						ResultSet rs=dbOp.executeQuery(sql);
						if(rs.next()){
							msg+=areaData;
							msg+="，全部覆盖，三级地址可以匹配";
							msg+="<br/>";
							daBean.setDeliverId(10);//10广州宅急送
							daBean.setAreaId(rs.getInt("ca.id"));
							daBean.setType(2);//2三级分类
							//deliverService.addDeliverAreaInfo(daBean);
						}else{
							errMsg+=areaData;
							errMsg+="，全部覆盖，三级地址不能匹配";
							errMsg+="<br/>";
						}
						rs.close();
					}else{//非全部覆盖
						String sql="select ass.id from provinces p join province_city pc on p.id=pc.province_id " +
						"join city_area ca on pc.id=ca.city_id join area_street ass on ca.id=ass.area_id" +
						" where p.name='"+province+"' and pc.city='"+city+"' and ca.area='"+area+"' and street='"+street+"'";
						ResultSet rs=dbOp.executeQuery(sql);
						if(rs.next()){
							msg+=areaData;
							msg+="，部分覆盖，四级地址可以匹配";
							msg+="<br/>";
							daBean.setDeliverId(10);//10广州宅急送
							daBean.setAreaId(rs.getInt("ass.id"));
							daBean.setType(3);
							//deliverService.addDeliverAreaInfo(daBean);
						}else{
//							errMsg+=areaData;
//							errMsg+="，部分覆盖，四级地址不能匹配";
//							errMsg+="<br/>";
							rs.close();
							String sql2="";
							if(street.endsWith("街道")){
								sql2="select ass.id from provinces p join province_city pc on p.id=pc.province_id " +
								"join city_area ca on pc.id=ca.city_id join area_street ass on ca.id=ass.area_id" +
								" where p.name='"+province+"' and pc.city='"+city+"' and ca.area='"+area+"' and street='"+street.replace("街道", "镇")+"'";
								rs=dbOp.executeQuery(sql2);
								if(rs.next()){
									msg+=areaData;
									msg+="，部分覆盖，四级地址可以匹配（街道->镇）";
									msg+="<br/>";
									daBean.setDeliverId(10);//10广州宅急送
									daBean.setAreaId(rs.getInt("ass.id"));
									daBean.setType(3);
									//deliverService.addDeliverAreaInfo(daBean);
								}else{
									errMsg+=areaData;
									errMsg+="，部分覆盖，四级地址不能匹配";
									errMsg+="<br/>";
								}
							}else if(street.endsWith("镇")){
								sql2="select ass.id from provinces p join province_city pc on p.id=pc.province_id " +
								"join city_area ca on pc.id=ca.city_id join area_street ass on ca.id=ass.area_id" +
								" where p.name='"+province+"' and pc.city='"+city+"' and ca.area='"+area+"' and street='"+street.replace("镇", "街道")+"'";
								rs=dbOp.executeQuery(sql2);
								if(rs.next()){
									msg+=areaData;
									msg+="，部分覆盖，四级地址可以匹配（镇->街道）";
									msg+="<br/>";
									daBean.setDeliverId(10);//10广州宅急送
									daBean.setAreaId(rs.getInt("ass.id"));
									daBean.setType(3);
									//deliverService.addDeliverAreaInfo(daBean);
								}else{
									errMsg+=areaData;
									errMsg+="，部分覆盖，四级地址不能匹配";
									errMsg+="<br/>";
								}
							}
						}
						if(rs!=null){
							rs.close();
						}
					}
				}
				request.setAttribute("msg", msg);
				request.setAttribute("errMsg", errMsg);
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			dbOp.release();
		}
	}
	
	/**
	 * 宅急送四级城市导入
	 */
	public void addDeliverArea2(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		DbOperation dbOp1 = new DbOperation();
		dbOp1.init("adult");
		WareService service = new WareService(dbOp1);
		DeliverService deliverService = ServiceFactory.createDeliverService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		String data=request.getParameter("data");
		try {
			if(data!=null&&data.length()>0){
				String[] areaList=data.split("\r\n");
				String errMsg="";
				String msg="";
				for(int i=0;i<areaList.length;i++){
					String areaData=areaList[i];//每行的信息
					String[] areaArray=areaData.split("\t");//每行信息拆分
					if(areaArray.length!=4){
						continue;
					}
					String province=areaArray[0].trim();//省
					String city=areaArray[1].trim();//市
					String area=areaArray[2].trim();//区县
					String street=areaArray[3].trim();//街道
					DeliverAreaBean daBean = new DeliverAreaBean();
						//非全部覆盖
						String sql="select ass.id from provinces p join province_city pc on p.id=pc.province_id " +
						"join city_area ca on pc.id=ca.city_id join area_street ass on ca.id=ass.area_id" +
						" where p.name='"+province+"' and pc.city='"+city+"' and ca.area='"+area+"' and street='"+street+"'";
						ResultSet rs=dbOp.executeQuery(sql);
						if(rs.next()){
							msg+=areaData;
							msg+="，部分覆盖，四级地址可以匹配";
							msg+="<br/>";
							daBean.setDeliverId(10);//10广州宅急送
							daBean.setAreaId(rs.getInt("ass.id"));
							daBean.setType(3);
							deliverService.addDeliverAreaInfo(daBean);
						}else{
							errMsg+=areaData;
							errMsg+="，部分覆盖，四级地址不能匹配";
							errMsg+="<br/>";
						}
						rs.close();
				}
				request.setAttribute("msg", msg);
				request.setAttribute("errMsg", errMsg);
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			dbOp.release();
		}
	}
	/**
	 * 重庆华宇Cqhy四级城市导入
	 */
	public void addDeliverAreaCqhy(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		DbOperation dbOp1 = new DbOperation();
		dbOp1.init("adult");
		WareService service = new WareService(dbOp1);
		DeliverService deliverService = ServiceFactory.createDeliverService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		String data=request.getParameter("data");
		try {
			if(data!=null&&data.length()>0){
				String[] areaList=data.split("\r\n");
				String errMsg="";
				String msg="";
				for(int i=0;i<areaList.length;i++){
					String areaData=areaList[i];//每行的信息
					String[] areaArray=areaData.split("\t");//每行信息拆分
					if(areaArray.length!=4){
						continue;
					}
					String province=areaArray[0].trim();//省
					String city=areaArray[1].trim();//市
					String area=areaArray[2].trim();//区县
					String street=areaArray[3].trim();//街道
					DeliverAreaBean daBean = new DeliverAreaBean();
						//非全部覆盖
						String sql="select ass.id from provinces p join province_city pc on p.id=pc.province_id " +
						"join city_area ca on pc.id=ca.city_id join area_street ass on ca.id=ass.area_id" +
						" where p.name='"+province+"' and pc.city='"+city+"' and ca.area='"+area+"' and street='"+street+"'";
						ResultSet rs=dbOp.executeQuery(sql);
						if(rs.next()){
							msg+=areaData;
							msg+="，部分覆盖，四级地址可以匹配";
							msg+="<br/>";
							daBean.setDeliverId(23);//10广州宅急送
							daBean.setAreaId(rs.getInt("ass.id"));
							daBean.setType(3);
							deliverService.addDeliverAreaInfo(daBean);
						}else{
							errMsg+=areaData;
							errMsg+="，部分覆盖，四级地址不能匹配";
							errMsg+="<br/>";
						}
						rs.close();
				}
				request.setAttribute("msg", msg);
				request.setAttribute("errMsg", errMsg);
			}
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			dbOp.release();
		}
	}
	/**
	 * 未完成退货上架单库存检查
	 */
	public void upShelfStockCheck(HttpServletRequest request,HttpServletResponse response){
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService stockService=ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			List lastList=new ArrayList();
			//所有未完成退货上架单
			List operList=service.getCargoOperationList("code like 'HWTS%' and status in (2,3,4,5,6) and effect_status in (0,1)", -1, -1, null);
			for(int operListNum=0;operListNum<operList.size();operListNum++){
				CargoOperationBean operBean=(CargoOperationBean)operList.get(operListNum);
				CargoOperationCargoBean coc2=service.getCargoOperationCargo("oper_id="+operBean.getId()+" and type=0");
				if(coc2==null){
					System.out.println("coc未找到，oper_id:"+operBean.getId());
					continue;
				}
				voProduct product=wareService.getProduct(coc2.getProductId());
				String cargoWholeCode=coc2.getOutCargoWholeCode();//源货位编号
				String cargoWholeCode2=coc2.getInCargoWholeCode();//目的货位编号
				CargoInfoBean ciBean=service.getCargoInfo("whole_code='"+coc2.getOutCargoWholeCode()+"'");//源货位
				CargoInfoBean ciBean2=service.getCargoInfo("whole_code='"+coc2.getInCargoWholeCode()+"'");//目的货位
				CargoProductStockBean cps=service.getCargoProductStock("id="+coc2.getOutCargoProductStockId());//源货位库存记录
				CargoProductStockBean cps2=service.getCargoProductStock("id="+coc2.getInCargoProductStockId());//目的货位库存近路
				
				if(product==null){
					System.out.println("coc未找到，oper_id:"+operBean.getId());
					continue;
				}
				if(ciBean==null){
					System.out.println("ciBean未找到，oper_id:"+operBean.getId());
					continue;
				}
				if(ciBean2==null){
					System.out.println("ciBean2未找到，oper_id:"+operBean.getId());
					continue;
				}
				if(cps==null){
					System.out.println("cps未找到，oper_id:"+operBean.getId());
					continue;
				}
				if(cps2==null){
					System.out.println("cps2未找到，oper_id:"+operBean.getId());
					continue;
				}
				
				//类型，stock是库存调拨单，stockLock是库存锁定量，spackLock是空间锁定量
				String type=StringUtil.convertNull(request.getParameter("type"));
				List orderList=new ArrayList();
				List countList=new ArrayList();
				if(operBean.getId()==1850){
					System.out.println();
				}
				if(true){//库存锁定量
					List refillList=service.getCargoOperationList("type=2 and status in (20,21,22,23,24) and effect_status in (0,1)", -1, -1, null);//补货单
					for(int i=0;i<refillList.size();i++){
						CargoOperationBean coBean=(CargoOperationBean)refillList.get(i);
						List cocList=service.getCargoOperationCargoList("oper_id="+coBean.getId()+" and out_cargo_whole_code='"+cargoWholeCode+"' and product_id="+product.getId(), -1, -1, null);
						int count=0;
						if(cocList.size()>0){
							for(int j=0;j<cocList.size();j++){
								CargoOperationCargoBean coc=(CargoOperationCargoBean)cocList.get(j);
								count+=coc.getStockCount();
							}
							orderList.add(coBean);
							countList.add(""+count);
						}
					}

					List exchangeList=service.getCargoOperationList("type=3 and status in(28,29,30,31,32,33) and effect_status in (0,1)", -1, -1, null);//调拨单
					for(int i=0;i<exchangeList.size();i++){
						CargoOperationBean coBean=(CargoOperationBean)exchangeList.get(i);
						if(ciBean.getStoreType()==CargoInfoBean.STORE_TYPE0||ciBean.getStoreType()==CargoInfoBean.STORE_TYPE4){//散件区或混合区
							//该货位是源货位，源库存全部锁定
							List outCocList=service.getCargoOperationCargoList("oper_id="+coBean.getId()+" and out_cargo_whole_code='"+cargoWholeCode+"' and product_id="+product.getId()+" and type=1", -1, -1, null);
							for(int j=0;j<outCocList.size();j++){//应该只有一项
								CargoOperationCargoBean outCoc=(CargoOperationCargoBean)outCocList.get(j);
								CargoProductStockBean cpsBean=service.getCargoProductStock("id="+outCoc.getOutCargoProductStockId());
								if(cpsBean!=null){
									orderList.add(coBean);
									countList.add(cpsBean.getStockLockCount()+"");
								}
							}
						}else if(ciBean.getStoreType()==CargoInfoBean.STORE_TYPE1){//整件区
							if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS29
									||coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS30
									||coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS31
									||coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS32
									||coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS33){//调拨单状态是已确认或已审核才有库存冻结
								List cocList=service.getCargoOperationCargoList("oper_id="+coBean.getId()+" and out_cargo_whole_code='"+cargoWholeCode+"' and product_id="+product.getId()+" and use_status=1", -1, -1, null);
								int count=0;
								for(int j=0;j<cocList.size();j++){
									CargoOperationCargoBean cocBean=(CargoOperationCargoBean)cocList.get(j);
									count+=cocBean.getStockCount();
								}
								if(count!=0){
									orderList.add(coBean);
									countList.add(count+"");
								}
							}
						}
					}

					List coList=service.getCargoOperationList("type not in(2,3) and status in (2,3,4,5,6,11,12,13,14,15) and effect_status in (0,1)", -1, -1, null);//非补货单非调拨单
					for(int i=0;i<coList.size();i++){
						CargoOperationBean coBean=(CargoOperationBean)coList.get(i);
						CargoOperationCargoBean cocBean=service.getCargoOperationCargo("oper_id="+coBean.getId()+" and out_cargo_whole_code='"+cargoWholeCode+"' and use_status=0 and product_id="+product.getId());
						if(cocBean!=null){
							int outCpsId=cocBean.getOutCargoProductStockId();
							List inCocList=service.getCargoOperationCargoList("oper_id="+coBean.getId()+" and out_cargo_product_stock_id="+outCpsId+" and use_status=1", -1, -1, null);
							int count=0;
							for(int j=0;j<inCocList.size();j++){
								CargoOperationCargoBean coc=(CargoOperationCargoBean)inCocList.get(j);
								count+=coc.getStockCount();
							}
							if(count!=0){
								orderList.add(coBean);
								countList.add(""+count);
							}
						}
					}
					int count=0;
					for(int j=0;j<countList.size();j++){
						count+=Integer.parseInt(countList.get(j).toString());
					}
					if(count!=cps.getStockLockCount()){
						if(!lastList.contains(operBean.getCode())){
							lastList.add(operBean.getCode());
						}
						
					}
					orderList=new ArrayList();
					countList=new ArrayList();
				}
				if(true){//库存冻结量
					List refillList=service.getCargoOperationList("type=2 and status in (20,21,22,23,24) and effect_status in (0,1)", -1, -1, null);//补货单
					for(int i=0;i<refillList.size();i++){
						CargoOperationBean coBean=(CargoOperationBean)refillList.get(i);
						CargoOperationCargoBean coc=service.getCargoOperationCargo("oper_id="+coBean.getId()+" and in_cargo_whole_code='"+cargoWholeCode2+"'");
						int count=0;
						if(coc!=null){
							int inCpsId=coc.getInCargoProductStockId();
							List cocList=service.getCargoOperationCargoList("oper_id="+coBean.getId()+" and use_status=1 and in_cargo_product_stock_id="+inCpsId, -1, -1, null);
							for(int j=0;j<cocList.size();j++){
								CargoOperationCargoBean cocBean=(CargoOperationCargoBean)cocList.get(j);
								count+=cocBean.getStockCount();
							}
							orderList.add(coBean);
							countList.add(""+count);
						}
					}
					List coList=service.getCargoOperationList("type<>2 and status in (2,3,4,5,6,11,12,13,14,15,29,30,31,32,33) and effect_status in (0,1)", -1, -1, null);//非补货单
					for(int i=0;i<coList.size();i++){
						CargoOperationBean coBean=(CargoOperationBean)coList.get(i);
						List inCocList=service.getCargoOperationCargoList("oper_id="+coBean.getId()+" and in_cargo_whole_code='"+cargoWholeCode2+"'", -1, -1, null);
						int count=0;
						for(int j=0;j<inCocList.size();j++){
							CargoOperationCargoBean inCoc=(CargoOperationCargoBean)inCocList.get(j);
							count+=inCoc.getStockCount();
						}
						if(count!=0){
							orderList.add(coBean);
							countList.add(""+count);
						}
					}
					int count=0;
					for(int j=0;j<countList.size();j++){
						count+=Integer.parseInt(countList.get(j).toString());
					}
					if(count!=ciBean2.getSpaceLockCount()){
						if(!lastList.contains(operBean.getCode())){
							lastList.add(operBean.getCode());
						}
					}
				}
			}
			request.setAttribute("lastList", lastList);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
			
	}
	
	public void makeSortingBatchGroupTest(HttpServletRequest request,HttpServletResponse response){
		voUser admin = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		UserGroupBean group = admin.getGroup();
		
		WareService service = new WareService();
		SortingInfoService siService = ServiceFactory.createSortingInfoService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		IStockService iService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		
		boolean isStartTransaction = false;
			try {
				int[] t=new int[5];
				t[0]=10;
				t[1]=30;
				t[2]=100;
				t[3]=500;
				t[4]=1000;
				for(int ti=0;ti<t.length;ti++){
					service.getDbOp().startTransaction();
				
				int batchId=1;
				String batchIdSql="select sorting_batch_id from sorting_batch_order2 order by id desc limit 1";
				ResultSet batchIdRs=service.getDbOp().executeQuery(batchIdSql);
				if(batchIdRs.next()){
					batchId=batchIdRs.getInt(1)+1;
				}
				batchIdRs.close();
				
				//List orderList=iService.getOrderStockList("status", -1, 300, "id desc");
				String orderStockSql="select uo.id,uo.code,uo.deliver,uo.product_type " +
						"from user_order uo join order_stock os on os.order_id=uo.id " +
						"where os.status=5 and uo.product_count=1 order by uo.id desc limit "+t[ti];
				ResultSet orderRs=service.getDbOp().executeQuery(orderStockSql);
				List sboList=new ArrayList();
				while(orderRs.next()){
					SortingBatchOrderBean sbo=new SortingBatchOrderBean();
					sbo.setOrderCode(orderRs.getString("uo.code"));
					sbo.setDeliver(orderRs.getInt("uo.deliver"));
					sbo.setOrderType(orderRs.getInt("uo.product_type"));
					sbo.setSortingBatchId(batchId);
					sbo.setSortingGroupId(0);
					sbo.setStatus(0);
					sbo.setGroupNum(0);
					sbo.setOrderId(orderRs.getInt("uo.id"));
					sbo.setDeleteStatus(0);
					sboList.add(sbo);
				}
				for(int i=0;i<sboList.size();i++){
					SortingBatchOrderBean sbo=(SortingBatchOrderBean)sboList.get(i);
					String addSboSql="insert into sorting_batch_order2 " +
							"(order_code,deliver,order_type,sorting_batch_id,sorting_group_id," +
							"status,group_num,order_id,sorting_batch_code,sorting_group_code,delete_status) " +
							"values('"+sbo.getOrderCode()+"',"+sbo.getDeliver()+","+sbo.getOrderType()+","+batchId+
							",0,0,0,"+sbo.getOrderId()+",'123','',0);";
					siService.getDbOp().executeUpdate(addSboSql);
				}
				
				//SortingBatchBean sbBean=siService.getSortingBatchInfo("id="+batchId);//分拣批次
				SortingBatchBean sbBean=new SortingBatchBean();
				sbBean.setId(batchId);
				sbBean.setCode("TestSbCode");
				sbBean.setStorage(3);
				//sbBean.setType1(0);
				
				String typeSql="select type_id from user_order_package_type where name='印刷品'";
				ResultSet typeRs=service.getDbOp().executeQuery(typeSql);
				int type=0;//属于印刷品的分类
				if(typeRs.next()){
					type=typeRs.getInt(1);
				}
				//批次下所有订单列表
				List orderListAll=new ArrayList();
				String orderListSql="select sbo.id,sbo.order_code,sbo.deliver,ospc.cargo_whole_code " +
				"from sorting_batch_order2 sbo "+
				"join order_stock os on os.order_code=sbo.order_code "+
				"join order_stock_product osp on osp.order_stock_id=os.id "+
				"join order_stock_product_cargo ospc on ospc.order_stock_product_id=osp.id "+
				"join product p on p.id=osp.product_id "+
				"left join product_ware_property pwp on pwp.product_id=p.id "+
				"where sbo.delete_status!=1 and os.status!=3 and (pwp.product_type_id!="+type+" or pwp.product_type_id is null) "+
				" and sbo.sorting_batch_id="+batchId+
				" order by ospc.cargo_whole_code asc";
				ResultSet rs=service.getDbOp().executeQuery(orderListSql);
				while(rs.next()){
					SortingBatchOrderBean sboBean=new SortingBatchOrderBean();
					sboBean.setId(rs.getInt("sbo.id"));
					sboBean.setCargoCode(rs.getString("ospc.cargo_whole_code"));
					sboBean.setOrderCode(rs.getString("sbo.order_code"));
					sboBean.setDeliver(rs.getInt("sbo.deliver"));
					orderListAll.add(sboBean);
				}
				rs.close();
				
				//所有订单按照巷道区分
				LinkedHashMap orderListMap=new LinkedHashMap();//key:巷道号,value:该巷道内订单list
				for(int i=0;i<orderListAll.size();i++){
					SortingBatchOrderBean sboBean=(SortingBatchOrderBean)orderListAll.get(i);
					String cargoCode=sboBean.getCargoCode();//货位号
					//GZZ01-A0101102
					String passageCode=cargoCode.substring(6,9);//巷道号
					List orderList=null;//巷道内订单列表
					if(orderListMap.containsKey(passageCode)){
						orderList=(List)orderListMap.get(passageCode);
					}else{
						orderList=new ArrayList();
					}
					orderList.add(sboBean);
					orderListMap.put(passageCode, orderList);
				}
				
				//确认巷道分组
				List passageList=new ArrayList();//element:排好序的巷道列表
				Iterator iter=orderListMap.keySet().iterator();
				while(iter.hasNext()){
					String passageCode=iter.next().toString();
					passageList.add(passageCode);
				}
				List areaOrderList=new ArrayList();//按巷道单双号排好序的订单列表（一个区域）
				for(int i=0;i<passageList.size();i++){
					String nextPassage="";//下一个巷道的巷道号
					if(i+1<passageList.size()){
						nextPassage=passageList.get(i+1).toString();
					}
					String passageCode=passageList.get(i).toString();//巷道号->A01
					String stockArea=passageCode.substring(0,1);//区域（字母）->A
					String passage=passageCode.substring(1,3);//巷道（数字）->01
					List orderList=(List)orderListMap.get(passageCode);//巷道内订单列表
					
					if(Integer.parseInt(passage)%2==1){	//巷道号是单数，货位正序排列
						for(int j=0;j<orderList.size();j++){
							areaOrderList.add(orderList.get(j));
						}
					}else{//巷道号是双数，货位倒序排列
						for(int j=orderList.size()-1;j>=0;j--){
							areaOrderList.add(orderList.get(j));
						}
					}
					
					//没有后面的巷道，或者后面巷道和该巷道不是一个区域，应生成波次
					if(nextPassage.length()==0||!nextPassage.substring(0,1).equals(stockArea)){
						addSortingBatchGroupByList(siService, sbBean, areaOrderList,request);
						areaOrderList=new ArrayList();
					}
					
//					//一个巷道可能单独生成波次的情况
//					
//					if(Integer.parseInt(passage)%2==1){	//巷道号是单数
//						String nextPassageCode=null;//下一个巷道的巷道号->A01
//						String nextStockArea=null;//下一个巷道的区域->A
//						String nextPassage=null;//下一个巷道的巷道->01
//						if(i+1<passageList.size()){
//							nextPassageCode=passageList.get(i+1).toString();
//							nextStockArea=nextPassageCode.substring(0,1);
//							nextPassage=nextPassageCode.substring(1,3);
//						}
//						if(nextPassageCode==null){//没有下一个巷道，该巷道应该直接生成波次
//							//生成波次（一个巷道）
//							addSortingBatchGroupByList(siService, sbBean, orderList,request);
//						}else if(!nextStockArea.equals(stockArea)||
//								Integer.parseInt(nextPassage)!=(Integer.parseInt(passage)+1)){//下一个巷道区域不同或下一个巷道不与该巷道相邻
//							//生成波次（一个巷道）
//							addSortingBatchGroupByList(siService, sbBean, orderList,request);
//						}
//					}else{	//巷道号是双数
//						String lastPassageCode=null;//上一个巷道的巷道号->A01
//						String lastStockArea=null;//上一个巷道的区域->A
//						String lastPassage=null;//上一个巷道的巷道->01
//						if(i>0){
//							lastPassageCode=passageList.get(i-1).toString();
//							lastStockArea=lastPassageCode.substring(0,1);
//							lastPassage=lastPassageCode.substring(1,3);
//						}
//						if(lastPassageCode==null){//没有上一个巷道，该巷道应该直接生成波次
//							//生成波次（一个巷道）
//							List tempList=new ArrayList();//该巷道订单倒序排列
//							for(int j=orderList.size();j>=0;j--){
//								tempList.add(orderList.get(j));
//							}
//							addSortingBatchGroupByList(siService, sbBean, tempList,request);
//						}else if(!lastStockArea.equals(stockArea)
//								||Integer.parseInt(lastPassage)!=(Integer.parseInt(passage)-1)){//上一个巷道区域不同或上一个巷道不与该巷道相邻
//							//生成波次（一个巷道）
//							List tempList=new ArrayList();//该巷道订单倒序排列
//							for(int j=orderList.size();j>=0;j--){
//								tempList.add(orderList.get(j));
//							}
//							addSortingBatchGroupByList(siService, sbBean, tempList,request);
//						}else{
//							//生成波次（两个巷道）
//							List lastOrderList=(List)orderListMap.get(lastPassageCode);//上一个巷道的订单列表
//							List tempList=new ArrayList();
//							tempList.addAll(lastOrderList);
//							for(int j=orderList.size()-1;j>=0;j--){
//								tempList.add(orderList.get(j));
//							}
//							addSortingBatchGroupByList(siService, sbBean, tempList,request);
//						}
//					}
				}
				service.getDbOp().commitTransaction();
				}
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				//siService.getDbOp().commitTransaction();
				siService.releaseAll();
			}
	}
	
	//添加波次，按照批次id和订单列表
	public int addSortingBatchGroupByList(SortingInfoService siService,SortingBatchBean sbBean,List orderList,HttpServletRequest request){
		
		List tempOrderList=new ArrayList();//参与生成波次的所有EMS订单列表
		List tempOrderList2=new ArrayList();//参与生成波次的所有非EMS订单列表
		for(int j=0;j<orderList.size();j++){
			SortingBatchOrderBean sboBean =(SortingBatchOrderBean)orderList.get(j);
			int deliver=sboBean.getDeliver();
			if(deliver==9||deliver==11){//EMS
				tempOrderList.add(sboBean);
			}else{//非EMS
				tempOrderList2.add(sboBean);
			}
		}
		List groupList=new ArrayList();//一个波次中的订单列表
		for(int j=0;j<tempOrderList.size();j++){
			SortingBatchOrderBean sboBean=(SortingBatchOrderBean)tempOrderList.get(j);
			groupList.add(sboBean);
			//序号是30的倍数且剩余订单大于15，或者末尾，生成波次
			if(((j+1)%30==0&&tempOrderList.size()-j-1>15)||j==tempOrderList.size()-1){
				//String code = "FJ" + DateUtil.getNow().substring(2, 4) + DateUtil.getNow().substring(5, 7) + DateUtil.getNow().substring(8, 10) + sbBean.getStorage() + sbBean.getId();
				String msg2="j="+j+",tempOrderList.size()="+tempOrderList.size()+"<br/>";
				if(request.getAttribute("msg")!=null){
					String msg=request.getAttribute("msg").toString();
					msg+=msg2;
					request.setAttribute("msg", msg);
				}else{
					request.setAttribute("msg", msg2);
				}
				String code = sbBean.getCode();
				int num=siService.getSortingBatchGroupCount("sorting_batch_id="+sbBean.getId());
				String bcount = Integer.toString(num+1);
				if (bcount.length() == 1) {
					bcount = "00" + bcount;
				}
				if (bcount.length() == 2) {
					bcount = "0" + bcount;
				}
				code = code + bcount;
				SortingBatchGroupBean dbgBean = new SortingBatchGroupBean();
				dbgBean.setCreateDatetime(DateUtil.getNow());
				dbgBean.setCode(code);
				dbgBean.setDeliver(0);
				dbgBean.setStatus(0);
				dbgBean.setStorage(sbBean.getStorage());
				dbgBean.setSortingBatchId(sbBean.getId());
				//dbgBean.setType1(sbBean.getType1());
				dbgBean.setType2(0);
				String addSbgSql="insert into sorting_batch_group2 " +
						"(code,create_datetime,deliver,staff_id,staff_name," +
						"storage,status,sorting_batch_id,type1,type2) " +
						"values ('"+dbgBean.getCode()+"','"+DateUtil.getNow()+"',"+"0,0,'',3,0,"
						+dbgBean.getSortingBatchId()+","+dbgBean.getType2()+");";
				siService.getDbOp().executeUpdate(addSbgSql);
				//siService.addSortingBatchGroupInfo(dbgBean);
				//SortingBatchGroupBean sbgBean = siService.getSortingBatchGroupInfo(" 1=1 order by id desc limit 1");
				int groupId=siService.getDbOp().getLastInsertId();
				for (int k = 0; k < groupList.size(); k++) {
					SortingBatchOrderBean tempSboBean = (SortingBatchOrderBean)groupList.get(k);
					int id=tempSboBean.getId();
					String updateSboSql="update sorting_batch_order2 " +
							"set sorting_group_id=" + groupId + ",sorting_group_code='" + code + "',status =1,group_num="+(k+1)+
							" where id="+id;
					siService.getDbOp().executeUpdate(updateSboSql);
					//siService.updateSortingBatchOrderInfo("sorting_group_id=" + groupId + ",sorting_group_code='" + code + "',status =1,group_num="+(k+1), "id=" + id);
				}
				//siService.updateSortingBatchInfo("status=2", "id=" + sbBean.getId());
				groupList=new ArrayList();
			}
		}
		groupList=new ArrayList();
		for(int j=0;j<tempOrderList2.size();j++){
			SortingBatchOrderBean sboBean=(SortingBatchOrderBean)tempOrderList2.get(j);
			groupList.add(sboBean);
			//序号是30的倍数且剩余订单大于15，或者末尾，生成波次
			if(((j+1)%30==0&&tempOrderList2.size()-j-1>15)||j==tempOrderList2.size()-1){
				//String code = "FJ" + DateUtil.getNow().substring(2, 4) + DateUtil.getNow().substring(5, 7) + DateUtil.getNow().substring(8, 10) + sbBean.getStorage() + sbBean.getId();
				String msg2="j="+j+",tempOrderList2.size()="+tempOrderList2.size()+"<br/>";
				if(request.getAttribute("msg")!=null){
					String msg=request.getAttribute("msg").toString();
					msg+=msg2;
					request.setAttribute("msg", msg);
				}else{
					request.setAttribute("msg", msg2);
				}
				String code = sbBean.getCode();
				int num=siService.getSortingBatchGroupCount("sorting_batch_id="+sbBean.getId());
				String bcount = Integer.toString(num+1);
				if (bcount.length() == 1) {
					bcount = "00" + bcount;
				}
				if (bcount.length() == 2) {
					bcount = "0" + bcount;
				}
				code = code + bcount;
				SortingBatchGroupBean dbgBean = new SortingBatchGroupBean();
				dbgBean.setCreateDatetime(DateUtil.getNow());
				dbgBean.setCode(code);
				dbgBean.setDeliver(0);
				dbgBean.setStatus(0);
				dbgBean.setStorage(sbBean.getStorage());
				dbgBean.setSortingBatchId(sbBean.getId());
				//dbgBean.setType1(sbBean.getType1());
				dbgBean.setType2(1);
				String addSbgSql="insert into sorting_batch_group2 " +
					"(code,create_datetime,deliver,staff_id,staff_name," +
					"storage,status,sorting_batch_id,type1,type2) " +
					"values ('"+dbgBean.getCode()+"','"+DateUtil.getNow()+"',"+"0,0,'',3,0,"
					+dbgBean.getSortingBatchId()+","+dbgBean.getType2()+");";
				siService.getDbOp().executeUpdate(addSbgSql);
				//siService.addSortingBatchGroupInfo(dbgBean);
				//SortingBatchGroupBean sbgBean = siService.getSortingBatchGroupInfo(" 1=1 order by id desc limit 1");
				int groupId=siService.getDbOp().getLastInsertId();
				for (int k = 0; k < groupList.size(); k++) {
					SortingBatchOrderBean tempSboBean = (SortingBatchOrderBean)groupList.get(k);
					int id=tempSboBean.getId();
					String updateSboSql="update sorting_batch_order2 " +
						"set sorting_group_id=" + groupId + ",sorting_group_code='" + code + "',status =1,group_num="+(k+1)+
						" where id="+id;
					siService.getDbOp().executeUpdate(updateSboSql);
					//siService.updateSortingBatchOrderInfo("sorting_group_id=" + groupId + ",sorting_group_code='" + code + "',status =1,group_num="+(k+1), "id=" + id);
				}
				//siService.updateSortingBatchInfo("status=2", "id=" + sbBean.getId());
				groupList=new ArrayList();
			}
		}
		
		
		return 0;
	}
	
	public void addWXCargoInfo(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		String stockAreaCode=request.getParameter("stockAreaCode");
		try {
			if(stockAreaCode!=null){
				dbOp.startTransaction();
				CargoInfoStockAreaBean stockArea=service.getCargoInfoStockArea("whole_code='"+stockAreaCode+"'");
				List<CargoInfoPassageBean> passageList=service.getCargoInfoPassageList("stock_area_id="+stockArea.getId(), -1, -1, null);
				for(int i=0;i<passageList.size();i++){
					CargoInfoPassageBean passage=passageList.get(i);
					List<CargoInfoShelfBean> shelfList=service.getCargoInfoShelfList("passage_id="+passage.getId(), -1, -1, null);
					for(int j=0;j<shelfList.size();j++){
						CargoInfoShelfBean shelf=shelfList.get(j);
						if(stockAreaCode.equals("JSW01-G")){//贵品区
							for(int floorNum=1;floorNum<=4;floorNum++){
								for(int cargoNum=1;cargoNum<=3;cargoNum++){
									CargoInfoBean cargo=new CargoInfoBean();
									String code="0"+cargoNum;
									cargo.setCode(code);
									cargo.setWholeCode(shelf.getWholeCode()+String.valueOf(floorNum)+code);
									cargo.setStoreType(4);//混合区
									cargo.setMaxStockCount(999);
									cargo.setWarnStockCount(999);
									cargo.setSpaceLockCount(0);
									cargo.setProductLineId(0);
									cargo.setType(1);//热销
									cargo.setLength(70);
									cargo.setWidth(70);
									cargo.setHigh(50);
									cargo.setFloorNum(floorNum);
									cargo.setStatus(0);//使用中
									cargo.setStockType(0);//合格库
									cargo.setShelfId(shelf.getId());
									cargo.setStockAreaId(shelf.getStockAreaId());
									cargo.setStorageId(shelf.getStorageId());
									cargo.setAreaId(shelf.getAreaId());
									cargo.setCityId(shelf.getCityId());
									cargo.setRemark("");
									cargo.setPassageId(shelf.getPassageId());
									service.addCargoInfo(cargo);
								}
							}
						}else if(stockAreaCode.equals("JSW01-A")){//普通区
							for(int floorNum=1;floorNum<=4;floorNum++){
								for(int cargoNum=1;cargoNum<=3;cargoNum++){
									CargoInfoBean cargo=new CargoInfoBean();
									String code="0"+cargoNum;
									cargo.setCode(code);
									cargo.setWholeCode(shelf.getWholeCode()+String.valueOf(floorNum)+code);
									cargo.setStoreType(4);//混合区
									cargo.setMaxStockCount(999);
									cargo.setWarnStockCount(999);
									cargo.setSpaceLockCount(0);
									cargo.setProductLineId(0);
									cargo.setType(0);//普通
									cargo.setLength(70);
									cargo.setWidth(70);
									cargo.setHigh(50);
									cargo.setFloorNum(floorNum);
									cargo.setStatus(0);//使用中
									cargo.setStockType(0);//合格库
									cargo.setShelfId(shelf.getId());
									cargo.setStockAreaId(shelf.getStockAreaId());
									cargo.setStorageId(shelf.getStorageId());
									cargo.setAreaId(shelf.getAreaId());
									cargo.setCityId(shelf.getCityId());
									cargo.setRemark("");
									cargo.setPassageId(shelf.getPassageId());
									service.addCargoInfo(cargo);
								}
							}
						}else if(stockAreaCode.equals("JSW01-B")){//托盘区
							CargoInfoBean cargo=new CargoInfoBean();
							String code="01";
							cargo.setCode(code);
							cargo.setWholeCode(shelf.getWholeCode()+"1"+code);
							cargo.setStoreType(4);//混合区
							cargo.setMaxStockCount(999);
							cargo.setWarnStockCount(999);
							cargo.setSpaceLockCount(0);
							cargo.setProductLineId(0);
							cargo.setType(0);//普通
							cargo.setLength(100);
							cargo.setWidth(120);
							cargo.setHigh(150);
							cargo.setFloorNum(1);
							cargo.setStatus(0);//使用中
							cargo.setStockType(0);//合格库
							cargo.setShelfId(shelf.getId());
							cargo.setStockAreaId(shelf.getStockAreaId());
							cargo.setStorageId(shelf.getStorageId());
							cargo.setAreaId(shelf.getAreaId());
							cargo.setCityId(shelf.getCityId());
							cargo.setRemark("");
							cargo.setPassageId(shelf.getPassageId());
							service.addCargoInfo(cargo);
						}
					}
				}
				dbOp.commitTransaction();
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	}
	
	/**
	 * 批量添加商品物流属性
	 * @param request
	 * @param response
	 */
	public void addProductWareProperty(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null||!user.getUsername().equals("liangkun6")){
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		ProductWarePropertyService pwpService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, dbOp);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, dbOp);
		
		String productWareProperty=request.getParameter("productWareProperty");
		try {
			String[] dataList=productWareProperty.split("\r\n");
			String errMsg="";
			for(int i=0;i<dataList.length;i++){
				dbOp.startTransaction();
				String areaData=dataList[i];//每行的信息
				String[] areaArray=areaData.split("\t");//每行信息拆分
				if(areaArray.length!=7){
					continue;
				}
				String productCode=areaArray[0].trim();//商品编号
				String productWareType=areaArray[1].trim();//物流属性
				String effectType=areaArray[2].trim();//质检分类
				String length=areaArray[3].trim();//长
				String width=areaArray[4].trim();//宽
				String hight=areaArray[5].trim();//高
				String weight=areaArray[6].trim();//重量
				
				voProduct product =wareService.getProduct(productCode);
				if(product==null){
					errMsg+=areaData;
					errMsg+="，商品编号错误";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}
				
				ProductWarePropertyBean pwp=new ProductWarePropertyBean();
				pwp.setProductId(product.getId());
				pwp.setCheckEffectId(Integer.parseInt(effectType));
				pwp.setLength(Integer.parseInt(length));
				pwp.setWidth(Integer.parseInt(width));
				pwp.setHeight(Integer.parseInt(hight));
				pwp.setProductTypeId(Integer.parseInt(productWareType));
				pwp.setWeight(Integer.parseInt(weight));
				
				if(!statService.addProductWareProperty(pwp)){
					errMsg+=areaData;
					errMsg+="，添加时异常";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}
				
				ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
				int productWarePropertyId = statService.getDbOp().getLastInsertId();
				pwplBean.setProductWarePropertyId(productWarePropertyId);
				pwplBean.setOperDetail("添加了商品" + product.getCode() + "的物流属性");
				pwplBean.setOperId(user.getId());
				pwplBean.setOperName(user.getUsername());
				pwplBean.setTime(DateUtil.getNow());
				if( !pwpService.addProductWarePropertyLog(pwplBean)) {
					errMsg+=areaData;
					errMsg+="，添加日志时异常";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}
				dbOp.commitTransaction();
			}
			request.setAttribute("errMsg", errMsg);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	}
	
	/**
	 * 修改无锡混合区货位为散件区
	 * @param request
	 * @param response
	 */
	public void changeWXCargo(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null||!user.getUsername().equals("liangkun6")){
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		ProductWarePropertyService pwpService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, dbOp);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, dbOp);
		
		String storeType=request.getParameter("storeType");
		try {
			String sql="update cargo_info set store_type=1 where whole_code like 'JSW01%' and store_type=0";
			boolean b=dbOp.executeUpdate(sql);
			request.setAttribute("success", b);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	}
	
	/**
	 * 批量添加B区和G区货位
	 * @param request
	 * @param response
	 */
	public void addCargoInStockAreaB(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null||!user.getUsername().equals("liangkun6")){
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		
		String stockAreaCode=request.getParameter("stockAreaCode");
		try {
			dbOp.startTransaction();
			if(stockAreaCode.equals("B")){//B区货位
				CargoInfoStockAreaBean stockArea=service.getCargoInfoStockArea("whole_code='GZZ01-B'");
				for(int passageNum=1;passageNum<=26;passageNum++){
					String passageCode=(passageNum<10?"0":"")+passageNum;//巷道号
					CargoInfoPassageBean passage=service.getCargoInfoPassage("whole_code='"+stockArea.getWholeCode()+passageCode+"'");
					if(passage==null){
						passage=new CargoInfoPassageBean();
						passage.setCode(passageCode);
						passage.setWholeCode(stockArea.getWholeCode()+passageCode);
						passage.setStockType(0);
						passage.setCityId(stockArea.getCityId());
						passage.setAreaId(stockArea.getAreaId());
						passage.setStorageId(stockArea.getStorageId());
						passage.setStockAreaId(stockArea.getId());
						service.addCargoInfoPassage(passage);
						passage.setId(dbOp.getLastInsertId());
						System.out.println("添加巷道："+passage.getWholeCode());
					}
					int shelfCount=0;
					if(passageNum<=14){
						shelfCount=14;
					}else{
						shelfCount=42;
					}
					for(int shelfNum=1;shelfNum<=shelfCount;shelfNum++){
						String shelfCode=(shelfNum<10?"0":"")+shelfNum;//货架号
						CargoInfoShelfBean shelf=service.getCargoInfoShelf("whole_code='"+passage.getWholeCode()+shelfCode+"'");
						if(shelf==null){
							shelf=new CargoInfoShelfBean();
							shelf.setCode(shelfCode);
							shelf.setWholeCode(passage.getWholeCode()+shelfCode);
							shelf.setStockType(0);
							shelf.setFloorCount(4);
							shelf.setStockAreaId(stockArea.getId());
							shelf.setStorageId(stockArea.getStorageId());
							shelf.setAreaId(stockArea.getAreaId());
							shelf.setCityId(stockArea.getCityId());
							shelf.setPassageId(passage.getId());
							service.addCargoInfoShelf(shelf);
							shelf.setId(dbOp.getLastInsertId());
							System.out.println("添加货架："+shelf.getWholeCode());
						}
						for(int floorNum=1;floorNum<=4;floorNum++){
							for(int cargoNum=1;cargoNum<=2;cargoNum++){
								String cargoCode="0"+cargoNum;
								CargoInfoBean cargo=new CargoInfoBean();
								cargo.setCode(cargoCode);
								cargo.setWholeCode(shelf.getWholeCode()+floorNum+cargoCode);
								cargo.setStoreType(1);//整件区
								cargo.setMaxStockCount(1000);
								cargo.setWarnStockCount(1);
								cargo.setSpaceLockCount(0);
								cargo.setProductLineId(0);//产品线暂定为0
								cargo.setType(0);//普通
								cargo.setLength(100);
								cargo.setWidth(70);
								cargo.setHigh(53);
								cargo.setFloorNum(floorNum);
								cargo.setStatus(1);
								cargo.setStockType(0);//合格库
								cargo.setShelfId(shelf.getId());
								cargo.setStockAreaId(stockArea.getId());
								cargo.setStorageId(stockArea.getStorageId());
								cargo.setAreaId(stockArea.getAreaId());
								cargo.setCityId(stockArea.getCityId());
								cargo.setRemark("");
								cargo.setPassageId(passage.getId());
								service.addCargoInfo(cargo);//添加货位
							}
						}
					}
				}
			}else if(stockAreaCode.equals("G")){//G区
				CargoInfoStockAreaBean stockArea=service.getCargoInfoStockArea("whole_code='GZZ01-G'");
				for(int passageNum=1;passageNum<=12;passageNum++){
					String passageCode=(passageNum<10?"0":"")+passageNum;//巷道号
					CargoInfoPassageBean passage=service.getCargoInfoPassage("whole_code='"+stockArea.getWholeCode()+passageCode+"'");
					if(passage==null){
						passage=new CargoInfoPassageBean();
						passage.setCode(passageCode);
						passage.setWholeCode(stockArea.getWholeCode()+passageCode);
						passage.setStockType(0);
						passage.setCityId(stockArea.getCityId());
						passage.setAreaId(stockArea.getAreaId());
						passage.setStorageId(stockArea.getStorageId());
						passage.setStockAreaId(stockArea.getId());
						service.addCargoInfoPassage(passage);
						passage.setId(dbOp.getLastInsertId());
						System.out.println("添加巷道："+passage.getWholeCode());
					}
					int shelfCount=0;
					if(passageNum<=3){
						shelfCount=26;
					}else{
						shelfCount=30;
					}
					for(int shelfNum=1;shelfNum<=shelfCount;shelfNum++){
						String shelfCode=(shelfNum<10?"0":"")+shelfNum;//货架号
						CargoInfoShelfBean shelf=service.getCargoInfoShelf("whole_code='"+passage.getWholeCode()+shelfCode+"'");
						if(shelf==null){
							shelf=new CargoInfoShelfBean();
							shelf.setCode(shelfCode);
							shelf.setWholeCode(passage.getWholeCode()+shelfCode);
							shelf.setStockType(0);
							shelf.setFloorCount(4);
							shelf.setStockAreaId(stockArea.getId());
							shelf.setStorageId(stockArea.getStorageId());
							shelf.setAreaId(stockArea.getAreaId());
							shelf.setCityId(stockArea.getCityId());
							shelf.setPassageId(passage.getId());
							service.addCargoInfoShelf(shelf);
							shelf.setId(dbOp.getLastInsertId());
							System.out.println("添加货架："+shelf.getWholeCode());
						}
						for(int floorNum=1;floorNum<=4;floorNum++){
							for(int cargoNum=1;cargoNum<=2;cargoNum++){
								String cargoCode="0"+cargoNum;
								CargoInfoBean cargo=new CargoInfoBean();
								cargo.setCode(cargoCode);
								cargo.setWholeCode(shelf.getWholeCode()+floorNum+cargoCode);
								cargo.setStoreType(1);//整件区
								cargo.setMaxStockCount(1000);
								cargo.setWarnStockCount(1);
								cargo.setSpaceLockCount(0);
								cargo.setProductLineId(0);//产品线暂定为0
								cargo.setType(0);//普通
								cargo.setLength(100);
								cargo.setWidth(70);
								cargo.setHigh(53);
								cargo.setFloorNum(floorNum);
								cargo.setStatus(1);
								cargo.setStockType(0);//合格库
								cargo.setShelfId(shelf.getId());
								cargo.setStockAreaId(stockArea.getId());
								cargo.setStorageId(stockArea.getStorageId());
								cargo.setAreaId(stockArea.getAreaId());
								cargo.setCityId(stockArea.getCityId());
								cargo.setRemark("");
								cargo.setPassageId(passage.getId());
								service.addCargoInfo(cargo);//添加货位
							}
						}
					}
				}
			}
			dbOp.commitTransaction();
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	}
	
	/**
	 * 批量添加货位间调拨单
	 * @param request
	 * @param response
	 */
	public void addCargoOperation(HttpServletRequest request,HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null||!user.getUsername().equals("liangkun6")){
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		ICargoService service=ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		ProductWarePropertyService pwpService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, dbOp);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, dbOp);
		
		String data=request.getParameter("data");
		try {
			String[] dataList=data.split("\r\n");
			String errMsg="";
			for(int i=0;i<dataList.length;i++){
				dbOp.startTransaction();
				String areaData=dataList[i];//每行的信息
				String[] areaArray=areaData.split("\t");//每行信息拆分
				if(areaArray.length!=4){
					continue;
				}
				String outCargoCode=areaArray[0].trim();//源货位
				String productCode=areaArray[1].trim();//商品编号
				String count=areaArray[2].trim();//调拨数量
				String inCargoCode=areaArray[3].trim();//目的货位
				
				voProduct product=wareService.getProduct(productCode);
				if(product==null){
					errMsg+=areaData;
					errMsg+="，商品编号错误";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}
				CargoInfoBean outCi=service.getCargoInfo("whole_code='"+outCargoCode+"'");
				if(outCi==null){
					errMsg+=areaData;
					errMsg+="，源货位号错误";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}
				CargoInfoBean inCi=service.getCargoInfo("whole_code='"+inCargoCode+"'");
				if(inCi==null){
					errMsg+=areaData;
					errMsg+="，目的货位号错误";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}
				if(inCi.getStatus()==2){
					errMsg+=areaData;
					errMsg+="，目的货位未开通";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}
				if(inCi.getStatus()==1){//目的货位状态是未使用
					service.updateCargoInfo("status=0", "id="+inCi.getId());
				}
				
				CargoProductStockBean outCps=service.getCargoProductStock("cargo_id="+outCi.getId()+" and product_id="+product.getId());
				if(outCps==null){
					errMsg+=areaData;
					errMsg+="，源货位不包含该商品";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}
				CargoProductStockBean inCps=service.getCargoProductStock("cargo_id="+inCi.getId()+" and product_id="+product.getId());
				if(inCps==null){
					inCps=new CargoProductStockBean();
					inCps.setCargoId(inCi.getId());
					inCps.setProductId(product.getId());
					inCps.setStockCount(0);
					inCps.setStockLockCount(0);
					service.addCargoProductStock(inCps);
					inCps.setId(dbOp.getLastInsertId());
					CargoInfoLogBean logBean=new CargoInfoLogBean();//操作记录
					logBean.setCargoId(inCi.getId());
					logBean.setOperDatetime(DateUtil.getNow());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					logBean.setRemark("货位绑定商品：商品"+product.getCode());
					service.addCargoInfoLog(logBean);
					
				}
				
				
				String code = "HWD"+DateUtil.getNow().substring(2,10).replace("-", "");   
				String storageCode = outCargoCode.substring(0,5);
				//生成编号
				CargoOperationBean cargoOper = service.getCargoOperation("code like '"+code+"%' order by id desc limit 1");
				if(cargoOper == null){
					code = code + "00001";
				}else{
					//获取当日计划编号最大值
					String _code = cargoOper.getCode();
					int number = Integer.parseInt(_code.substring(_code.length()-5));
					number++;
					code += String.format("%05d",new Object[]{new Integer(number)});
				}

				cargoOper = new CargoOperationBean();
				cargoOper.setCode(code);
				cargoOper.setCreateDatetime(DateUtil.getNow());
				cargoOper.setCreateUserId(user.getId());
				cargoOper.setCreateUserName(user.getUsername());
				cargoOper.setRemark("");
				cargoOper.setSource("");
				cargoOper.setStockInType(inCi.getStoreType());
				cargoOper.setStockOutType(outCi.getStoreType());
				cargoOper.setStorageCode(storageCode);
				cargoOper.setType(CargoOperationBean.TYPE3);
				cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS30);
				cargoOper.setLastOperateDatetime(DateUtil.getNow());
				cargoOper.setStockOutArea(inCi.getAreaId());
				cargoOper.setStockInArea(inCi.getAreaId());
				if(!service.addCargoOperation(cargoOper)){
					errMsg+=areaData;
					errMsg+="，添加调拨单错误";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}//添加cargo_operation

				int cargoOperId = service.getDbOp().getLastInsertId();
				
				CargoOperationCargoBean outCoc = new CargoOperationCargoBean();
				outCoc.setOperId(cargoOperId);
				outCoc.setInCargoProductStockId(0);
				outCoc.setProductId(product.getId());
				outCoc.setType(1);
				outCoc.setOutCargoProductStockId(outCps.getId());
				outCoc.setOutCargoWholeCode(outCi.getWholeCode());
				outCoc.setStockCount(Integer.parseInt(count));
				if(!service.addCargoOperationCargo(outCoc)){
					errMsg+=areaData;
					errMsg+="，添加作业单源货位信息错误";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}
				
				if(!service.updateCargoProductStockCount(outCps.getId(), -Integer.parseInt(count))){
					errMsg+=areaData;
					errMsg+="，货位库存锁定失败";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}
				if(!service.updateCargoProductStockLockCount(outCps.getId(), Integer.parseInt(count))){
					errMsg+=areaData;
					errMsg+="，货位库存锁定失败";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}
				
				CargoOperationLogBean logBean=new CargoOperationLogBean();
				logBean.setOperId(cargoOperId);
				logBean.setOperDatetime(DateUtil.getNow());
				logBean.setOperAdminId(user.getId());
				logBean.setOperAdminName(user.getUsername());
				StringBuilder logRemark=new StringBuilder("制单：");
				
				logRemark.append("商品");
				logRemark.append(product.getCode());
				logRemark.append("，");
				logRemark.append("源货位（");
				logRemark.append(outCi.getWholeCode());
				logRemark.append("）");
				
				CargoOperationCargoBean inCoc=new CargoOperationCargoBean();
				inCoc.setOperId(cargoOperId);
				inCoc.setProductId(product.getId());
				inCoc.setOutCargoProductStockId(outCps.getId());
				inCoc.setOutCargoWholeCode(outCi.getWholeCode());
				inCoc.setStockCount(Integer.parseInt(count));
				inCoc.setType(0);
				inCoc.setUseStatus(1);
				inCoc.setInCargoProductStockId(inCps.getId());
				inCoc.setInCargoWholeCode(inCi.getWholeCode());
				if(!service.addCargoOperationCargo(inCoc)){
					errMsg+=areaData;
					errMsg+="，添加作业单目的货位信息错误";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}//添加目的货位记录
				
				logRemark.append("，");
				logRemark.append("目的货位（");
				logRemark.append(inCi.getWholeCode());
				logRemark.append("）");
				logBean.setRemark(logRemark.toString());
				service.addCargoOperationLog(logBean);
				
				CargoOperLogBean operLog=new CargoOperLogBean();//员工操作日志
				operLog.setOperId(cargoOperId);
				operLog.setOperCode(cargoOper.getCode());
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS30);
				operLog.setOperName(process.getOperName());
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(CargoOperLogBean.EFFECT_TIME0);
				operLog.setRemark("");
				operLog.setPreStatusName("无");
				operLog.setNextStatusName(process.getStatusName());
				if(!service.addCargoOperLog(operLog)){
					errMsg+=areaData;
					errMsg+="，添加日志数据时发生异常";
					errMsg+="<br/>";
					dbOp.rollbackTransaction();
					continue;
				}
				dbOp.commitTransaction();
			}
			request.setAttribute("errMsg", errMsg);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	}
/*
 * 根据cargo_staff表中的code更新dept_id的值
 */
	public void updateStaffDeptId(HttpServletRequest request, HttpServletResponse response) {

		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());

		try {
			List staffList = service.getCargoStaffList("id>0", -1, -1, "id");
			for(int i=0;i<staffList.size();i++){
				CargoStaffBean staffBean = (CargoStaffBean)staffList.get(i);
				String code = staffBean.getCode();
			
			service.getDbOp().startTransaction();
			String deptCode0 = code.substring(0, 2);
			String deptCode1 = code.substring(2, 4);
			String deptCode2 = code.substring(4, 6);
			String deptCode3 = code.substring(6, 8);
			int deptId0 = 0;// 员工所属0级部门id
			int deptId1 = 0;// 员工所属1级部门id
			int deptId2 = 0;// 员工所属2级部门id
			int deptId3 = 0;// 员工所属3级部门id
			CargoDeptBean cargoDept0 = service.getCargoDept("code='" + deptCode0 + "' and parent_id0=0 and parent_id1=0 and parent_id2=0 and parent_id3=0");
			if (cargoDept0 != null) {
				deptId0 = cargoDept0.getId();
				CargoDeptBean cargoDept1 = service.getCargoDept("code='" + deptCode1 + "' and parent_id0=" + deptId0 + " and parent_id1=0 and parent_id2=0 and parent_id3=0");
				if (cargoDept1 != null) {
					deptId1 = cargoDept1.getId();
					CargoDeptBean cargoDept2 = service.getCargoDept("code='" + deptCode2 + "' and parent_id0=" + deptId0 + " and parent_id1=" + deptId1 + " and parent_id2=0 and parent_id3=0");
					if (cargoDept2 != null) {
						deptId2 = cargoDept2.getId();
						CargoDeptBean cargoDept3 = service.getCargoDept("code='" + deptCode3 + "' and parent_id0=" + deptId0 + " and parent_id1=" + deptId1 + " and parent_id2=" + deptId2 + " and parent_id3=0");
						if (cargoDept3 != null) {
							deptId3 = cargoDept3.getId();
						} else if (deptCode3.equals("00")) {// 如果查询不到3级部门，并且员工3级部门编号为"00"则员工所属部门id为2级部门id
							deptId3 = cargoDept2.getId();
						}
					} else if (deptCode2.equals("00")) {// 如果查询不到2级部门，并且员工2级部门编号为"00"则员工所属部门id为1级部门id
						deptId3 = cargoDept1.getId();
					}
				} else if (deptCode1.equals("00")) {// 如果查询不到1级部门，并且员工1级部门编号为"00"则员工所属部门id为0级部门id
					deptId3 = cargoDept0.getId();
				}
			}
			service.updateCargoStaff("dept_id =" + deptId3, " id =" + staffBean.getId());
			service.getDbOp().commitTransaction();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
	/*
	 * 更新cargo_staff表中的code长度，加两位
	 */
	public void updateStaffCodeLength(HttpServletRequest request, HttpServletResponse response) {

		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());

		try {
			List staffList = service.getCargoStaffList("id>0", -1, -1, "id");
			for (int i = 0; i < staffList.size(); i++) {
				CargoStaffBean staffBean = (CargoStaffBean) staffList.get(i);
				String code = staffBean.getCode();
				if (code.length() ==10) {
					service.getDbOp().startTransaction();
					String deptCode0 = code.substring(0, 8) + "00";
					String deptCode1 = code.substring(8, 10);
					String code1 = deptCode0 + deptCode1;

					service.updateCargoStaff("code ='" + code1+"'", " id =" + staffBean.getId());
					service.getDbOp().commitTransaction();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
	//贵州省邮政速递物流邮件详情单号码构成及算法
	public static String printGZEMSPackageCode(int num) {
		String code = String.format("%08d",new Object[]{new Integer(num)});
		int sum = Integer.valueOf(code.substring(0, 1))*8+
		          Integer.valueOf(code.substring(1, 2))*6+
		          Integer.valueOf(code.substring(2, 3))*4+
		          Integer.valueOf(code.substring(3, 4))*2+
		          Integer.valueOf(code.substring(4, 5))*3+
		          Integer.valueOf(code.substring(5, 6))*5+
		          Integer.valueOf(code.substring(6, 7))*9+
		          Integer.valueOf(code.substring(7, 8))*7;
		int i= 11-sum%11;
		int last =0 ;
		if(i==10){
			last=0;
		}
		else if(i==11){
			last=5;
		}else{
			last=i;
		}
		code="EE"+code+last+"GD";
		return code;
	}

	public static void main(String[] args) throws IOException {
		int start = 92518001;
		int end = 92698000;
		//		int start = 86880262;
		//		int end = 86881000;
		
		try{
			Class.forName("com.mysql.jdbc.Driver");
			Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/test?user=root&password=123456");
			Statement st = conn.createStatement();

			for(int i = start; i <= end; i++){
				System.out.println(printGZEMSPackageCode(i));
				st.executeUpdate("insert into packagenum values('"+printGZEMSPackageCode(i)+"')");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//		try {
		//			Map<String,Object> paramContent=new HashMap();
		//			paramContent.put("orderCode", "D12345");
		//			paramContent.put("totalPrice", "456.78");
		//			
		//			String templatePath=Constants.SHORT_MESSAGE;
		//			String templateName=Constants.SENT_OUT_MESSAGE;
		//			System.out.println(templatePath);
		//			System.out.println(templateName);
		//			TemplateMarker tm =TemplateMarker.getMarker();
		//			tm.initCfg(templatePath);
		//			String content=tm.getOutString(templateName, paramContent);
		//			System.out.println(content);
		//		} catch (TemplateException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
	}
	/**
	 * 比较三级地址
	 * @param request
	 * @param response
	 */
	public void compareThirdAddress(HttpServletRequest request, HttpServletResponse response) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			return;
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave2");
		String allCode = request.getParameter("areaCode");
		try {
			if (allCode != null) {
				List provList = new ArrayList();// 文本框中的省
				List cityList = new ArrayList();// 文本框中的市
				List areaList = new ArrayList();// 文本框中的区
				String[] codeList = allCode.split("\r\n");
				// 遍历文本框中的省市区
				String province = new String();// 省
				String city = new String();// 市
				String area = new String();// 区
				for (int i = 0; i < codeList.length; i++) {
					String[] line = codeList[i].split("\t");
					province = line[0].trim().replace(" ", "");// 省
					city = line[1].trim().replace(" ", "");// 市
					area = line[2].trim().replace(" ", "");// 区
					provList.add(province);
					cityList.add(city);
					if (!area.substring(area.length() - 1, area.length()).equals("*")) {
						areaList.add(area);
					}
				}
				List provList1 = new ArrayList();// 数据库中的省
				List cityList1 = new ArrayList();// 数据库中的市
				List areaList1 = new ArrayList();// 数据库中的区
				// 遍历数据库中的省
				String provSql = "select name from provinces ";
				ResultSet rs = dbOp.executeQuery(provSql);
				while (rs.next()) {
					provList1.add(rs.getString("name"));
				}
				rs.close();
				// 遍历数据库中的市
				String citySql = "select city from province_city ";
				ResultSet rs1 = dbOp.executeQuery(citySql);
				while (rs1.next()) {
					cityList1.add(rs1.getString("city"));
				}
				rs1.close();
				// 遍历数据库中的区
				String areaSql = "select area from city_area ";
				ResultSet rs2 = dbOp.executeQuery(areaSql);
				while (rs2.next()) {
					areaList1.add(rs2.getString("area"));
				}
				rs2.close();
				List list = new ArrayList();// 数据库中没有的二级地址
				List list11 = new ArrayList();
				//for (int i = 0; i < cityList1.size(); i++) {
					for (int j = 0; j < cityList.size(); j++) {
						if (!cityList1.contains(cityList.get(j))) {
							if (!list.contains(cityList.get(j))) {
								list.add(cityList.get(j));
								list11.add(provList.get(j));
							}
						}
					}
				//}
					List list6 = new ArrayList();
					for(int i=0;i<list.size();i++){
						list6.add(list11.get(i)+"-"+list.get(i));
					}
				List list1 = new ArrayList();// excel中没有的二级地址
				//for (int i = 0; i < cityList.size(); i++) {
					for (int j = 0; j < cityList1.size(); j++) {
						if (!cityList.contains(cityList1.get(j))) {
							if (!list1.contains(cityList1.get(j))) {
								list1.add(cityList1.get(j));
							}
						}
					}
				//}
				// 遍历数据库中的区
					List list4 = new ArrayList();
				for(int i=0;i<list1.size();i++){
					String sql = "select a.name,b.city from provinces a join province_city b on a.id=b.province_id where city='"+list1.get(i)+"'";
					ResultSet rs3 = dbOp.executeQuery(sql);
					while (rs3.next()) {
						list4.add(rs3.getString("a.name")+"-"+rs3.getString("b.city"));
					}
					rs3.close();
				}
				List list2 = new ArrayList();// 数据库中没有的三级地址
				List list22= new ArrayList();
				List list33= new ArrayList();
				//for (int i = 0; i < areaList1.size(); i++) {
					for (int j = 0; j < areaList.size(); j++) {
						if (!areaList1.contains(areaList.get(j))) {
							if (!list2.contains(areaList.get(j))) {
								list22.add(provList.get(j));
								list33.add(cityList.get(j));
								list2.add(areaList.get(j));
							}
						}
					}
					List list7 = new ArrayList();
					for(int i=0;i<list2.size();i++){
						list7.add(list22.get(i)+"-"+list33.get(i)+"-"+list2.get(i));
					}
				//}
				List list3 = new ArrayList();// excel中没有的三级地址
				//for (int i = 0; i < areaList.size(); i++) {
					for (int j = 0; j < areaList1.size(); j++) {
						if (!areaList.contains(areaList1.get(j))) {
							if (!list3.contains(areaList1.get(j))) {
								list3.add(areaList1.get(j));
							}
						}
					}
				//}
					List list5 = new ArrayList();
				for(int i=0;i<list3.size();i++){
					String sql = "select a.name,b.city ,c.area from provinces a join province_city b on a.id=b.province_id join city_area c on b.id=c.city_id where area='"+list3.get(i)+"'";
					ResultSet rs3 = dbOp.executeQuery(sql);
					while (rs3.next()) {
						list5.add(rs3.getString("a.name")+"-"+rs3.getString("b.city")+"-"+rs3.getString("c.area"));
						System.out.println(rs3.getString("a.name")+"-"+rs3.getString("b.city")+"-"+rs3.getString("c.area"));
					}
					rs3.close();
				}
				request.setAttribute("list", list6);
				request.setAttribute("list1", list4);
				request.setAttribute("list2", list7);
				request.setAttribute("list3", list5);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}
	
	/**
	 * 测试短信模板
	 * @param request
	 * @param response
	 */
	public void testTemplateMarker(HttpServletRequest request, HttpServletResponse response) {
		try {
			Map<String,Object> paramContent=new HashMap();
			paramContent.put("orderCode", "D12345");
			paramContent.put("totalPrice", "456.78");
			paramContent.put("deliverName", "测试物流");
			paramContent.put("days", "99-100");
			paramContent.put("packageNum", "EE1234567890DS");
			paramContent.put("phone", "010-12345678");
			
			String templateName=TemplateMarker.SENT_OUT_MESSAGE_NAME;
//			String templatePath=TemplateMarker.MESSAGE_PATH;
			
			TemplateMarker tm =TemplateMarker.getMarker();
//			tm.initCfg(templatePath);
			String content=tm.getOutString(templateName, paramContent);
			System.out.println(content);
			request.setAttribute("content", content);
		} catch (TemplateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试事务
	 * @param request
	 * @param response
	 */
	public void testTransaction(HttpServletRequest request, HttpServletResponse response) {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IMEIService iMEIService = new IMEIService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			dbOp.startTransaction();
//			String sql="update imei set status=2 where id=1";
//			boolean result=dbOp.executeUpdate(sql);
//			System.out.println("1:"+result);
//			if(!result){
//				System.out.println("1:"+false);
//			}
			if(!iMEIService.updateIMEI("status=11", "id=1")){
				System.out.println("1:操作失败");
			}
			System.out.println(dbOp.getConn().getAutoCommit());
			if(!iMEIService.updateIMEI("status=22", "id=2")){
				System.out.println("2:操作失败");
			}
			dbOp.commitTransaction();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			dbOp.release();
		}
	}
	
	/**
	 * 测试事务2
	 * @param request
	 * @param response
	 */
	public void testTransaction2(HttpServletRequest request, HttpServletResponse response) {
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		IMEIService iMEIService = new IMEIService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			dbOp.startTransaction();
			if(!iMEIService.updateIMEI("status=33", "id=1")){
				System.out.println("3:操作失败");
			}
			System.out.println(dbOp.getConn().getAutoCommit());
			if(!iMEIService.updateIMEI("status=33", "id=24")){
				System.out.println("4:操作失败");
			}
			dbOp.commitTransaction();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			dbOp.release();
		}
	}
	
	public Map<String ,String> testAmazonService(HttpServletRequest request, HttpServletResponse response) {
		Map<String,String> result = new HashMap<String,String>();
		
		try {
			String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
			s += "<AmazonEnvelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"amzn-envelope.xsd\">";
			s += "<Header>";
			s += "<DocumentVersion>1.01</DocumentVersion>";
			s += "<MerchantIdentifier>A1M6IFOE1QV3LD</MerchantIdentifier>";
			s += "</Header>";
			s += "<MessageType>OrderFulfillment</MessageType>";
			s += "<Message>";
			s += "<MessageID>1</MessageID>";
			s += "<OrderFulfillment>";
			s += "<AmazonOrderID>C01-8963515-1051812</AmazonOrderID>";
			s += "<MerchantFulfillmentID>158235</MerchantFulfillmentID>";
			s += "<FulfillmentDate>2014-05-22T11:09:14-06:00</FulfillmentDate>";
			s += "<FulfillmentData>";
			s += "<CarrierCode>FedEx</CarrierCode>";
			s += "<ShipperTrackingNumber>602631161336447</ShipperTrackingNumber>";
			s += "</FulfillmentData>";
			s += "<Item>";
			s += "<AmazonOrderItemCode>66020101005230</AmazonOrderItemCode>";
			s += "<Quantity>1</Quantity>";
			s += "</Item>";
			s += "</OrderFulfillment>";
			s += "</Message>";
			s += "</AmazonEnvelope>";
			SubmitFeedResponse sfsr = SubmitFeedSample.submitFulfillInfoToAmazon(s, "D:/abc.xml");
					SubmitFeedResult res = sfsr.getSubmitFeedResult();
			String id = res.getFeedSubmissionInfo().getFeedSubmissionId();
			if( id != null ) {
				result.put("submissionId", id);
			}
			List<String> list = new ArrayList<String>();
			list.add("C01-9202969-1314421");
			list.add("C01-8963515-1051812");
			Map<String,String> map = GetOrderSample.getOrderStatus(list);
			if( map != null ) {
				Set<Entry<String,String>> entrySet = map.entrySet();
				for( Entry<String,String> en : entrySet ) {
					result.put(en.getKey(), en.getValue());
				}
			}
		} catch ( Exception e ) {
			 e.printStackTrace();
			result.put("ExceptionInfo", e.getMessage());
		}
    	//System.out.println(map);
    	return  result;
	}
}
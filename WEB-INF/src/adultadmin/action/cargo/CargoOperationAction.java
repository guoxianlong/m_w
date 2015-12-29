package adultadmin.action.cargo;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.cargo.model.ReturnedProductVirtual;
import mmb.finance.stat.FinanceStockCardBean;
import mmb.rec.oper.service.ConsignmentService;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.cargo.CartonningInfoBean;
import mmb.stock.cargo.CartonningInfoService;
import mmb.stock.cargo.CartonningProductInfoBean;
import mmb.stock.cargo.StockOperationAction;
import mmb.util.excel.ExportExcel;
import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.cargo.CargoInfoStorageBean;
import adultadmin.bean.cargo.CargoOperLogBean;
import adultadmin.bean.cargo.CargoOperationBean;
import adultadmin.bean.cargo.CargoOperationCargoBean;
import adultadmin.bean.cargo.CargoOperationLogBean;
import adultadmin.bean.cargo.CargoOperationProcessBean;
import adultadmin.bean.cargo.CargoProductStockBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockCardBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockExchangeProductBean;
import adultadmin.bean.stock.StockExchangeProductCargoBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.Constants;
import adultadmin.util.DateUtil;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

import com.mmb.components.bean.BaseProductInfo;
import com.mmb.components.factory.FinanceBaseDataServiceFactory;
import com.mmb.components.service.FinanceBaseDataService;

public class CargoOperationAction extends DispatchAction {

	public static byte[] cargoLock = new byte[0];
	public static byte[] cargoAssignLock = new byte[0];
	public Log stockLog = LogFactory.getLog("stock.Log");

	/**
	 *	现行订单缺货产品列表 2011-09-30
	 */
	public ActionForward lackProductList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp()); 
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			//获取可发货订单产品信息
			List orderStockList = stockService.getOrderStockList("status in (0,1) and stock_area=1", -1, -1, "create_datetime asc");
			Iterator osIter = orderStockList.listIterator();
			HashMap cpsMap = new HashMap();
			HashMap productMap = new HashMap();
			HashMap lackTimeMap=new HashMap();//缺货时间
			while(osIter.hasNext()){
				OrderStockBean os = (OrderStockBean)osIter.next();
				voOrder order = wareService.getOrder(os.getOrderId());
				if(order==null||order.getStatus() != 3){
					continue;
				}
				List ospList = stockService.getOrderStockProductList("order_stock_id = "+os.getId(), -1, -1, "id asc");
				for(int i=0;i<ospList.size();i++){
					OrderStockProductBean osp = (OrderStockProductBean)ospList.get(i);
					voProduct product = wareService.getProduct(osp.getProductId());
					if(product!=null){
						product.setCargoPSList(service.getCargoAndProductStockList("cps.product_id = "+osp.getProductId(), -1, -1, "cps.id asc"));

						if(cpsMap.get(String.valueOf(product.getId()))==null){
							int stockCount = product.getCargoStock(CargoInfoBean.STORE_TYPE0,1)+product.getCargoStock(CargoInfoBean.STORE_TYPE4,1);
							cpsMap.put(String.valueOf(product.getId()), Integer.valueOf(stockCount-osp.getStockoutCount()));
							if(stockCount>=0&&stockCount-osp.getStockoutCount()<0){
								lackTimeMap.put(String.valueOf(product.getId()), os.getCreateDatetime());
							}
						}else{
							int stockCount = ((Integer)cpsMap.get(String.valueOf(product.getId()))).intValue();
							cpsMap.put(String.valueOf(product.getId()), Integer.valueOf(stockCount-osp.getStockoutCount()));
							if(stockCount>=0&&stockCount-osp.getStockoutCount()<0){
								lackTimeMap.put(String.valueOf(product.getId()), os.getCreateDatetime());
							}
						}

						productMap.put(String.valueOf(product.getId()), product);
					}
				}
			}
			List idList = new ArrayList();
			Iterator iter = cpsMap.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry entry = (Map.Entry)iter.next();
				idList.add(entry);
			}
			//重新排序
			Collections.sort(idList, new Comparator() {
				public int compare(Object o1, Object o2) {
					Map.Entry entry1 = (Map.Entry) o1;
					Map.Entry entry2 = (Map.Entry) o2;
					Integer count1 = (Integer)entry1.getValue();
					Integer count2 = (Integer)entry2.getValue();
					return (count1.compareTo(count2));
				}
			});

			request.setAttribute("lackTimeMap", lackTimeMap);
			request.setAttribute("productMap",productMap);
			request.setAttribute("cpsMap",cpsMap);
			request.setAttribute("idList", idList);

			//获取可发货订单产品信息
			List orderStockList2 = stockService.getOrderStockList("status in (0,1) and stock_area=3", -1, -1, "create_datetime asc");
			Iterator osIter2 = orderStockList2.listIterator();
			HashMap cpsMap2 = new HashMap();
			HashMap productMap2 = new HashMap();
			HashMap lackTimeMap2=new HashMap();//缺货时间
			while(osIter2.hasNext()){
				OrderStockBean os = (OrderStockBean)osIter2.next();
				voOrder order = wareService.getOrder(os.getOrderId());
				if(order==null||order.getStatus() != 3){
					continue;
				}
				List ospList = stockService.getOrderStockProductList("order_stock_id = "+os.getId(), -1, -1, "id asc");
				for(int i=0;i<ospList.size();i++){
					OrderStockProductBean osp = (OrderStockProductBean)ospList.get(i);
					voProduct product = wareService.getProduct(osp.getProductId());
					if(product!=null){
						product.setCargoPSList(service.getCargoAndProductStockList("cps.product_id = "+osp.getProductId(), -1, -1, "cps.id asc"));

						if(cpsMap2.get(String.valueOf(product.getId()))==null){
							int stockCount = product.getCargoStock(CargoInfoBean.STORE_TYPE0,3)+product.getCargoStock(CargoInfoBean.STORE_TYPE4,3);
							cpsMap2.put(String.valueOf(product.getId()), Integer.valueOf(stockCount-osp.getStockoutCount()));
							if(stockCount>=0&&stockCount-osp.getStockoutCount()<0){
								lackTimeMap2.put(String.valueOf(product.getId()), os.getCreateDatetime());
							}
						}else{
							int stockCount = ((Integer)cpsMap2.get(String.valueOf(product.getId()))).intValue();
							cpsMap2.put(String.valueOf(product.getId()), Integer.valueOf(stockCount-osp.getStockoutCount()));
							if(stockCount>=0&&stockCount-osp.getStockoutCount()<0){
								lackTimeMap2.put(String.valueOf(product.getId()), os.getCreateDatetime());
							}
						}

						productMap2.put(String.valueOf(product.getId()), product);
					}
				}
			}
			List idList2 = new ArrayList();
			Iterator iter2 = cpsMap2.entrySet().iterator();
			while(iter2.hasNext()){
				Map.Entry entry = (Map.Entry)iter2.next();
				idList2.add(entry);
			}
			//重新排序
			Collections.sort(idList2, new Comparator() {
				public int compare(Object o1, Object o2) {
					Map.Entry entry1 = (Map.Entry) o1;
					Map.Entry entry2 = (Map.Entry) o2;
					Integer count1 = (Integer)entry1.getValue();
					Integer count2 = (Integer)entry2.getValue();
					return (count1.compareTo(count2));
				}
			});

			request.setAttribute("lackTimeMap2", lackTimeMap2);
			request.setAttribute("productMap2",productMap2);
			request.setAttribute("cpsMap2",cpsMap2);
			request.setAttribute("idList2", idList2);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}

		return mapping.findForward("lackProductList");
	}

	/**
	 *	添加补货单缺货货位列表
	 */
	public ActionForward addRefillCargoList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		int stockCount1 = StringUtil.toInt(request.getParameter("stockCount1"));
		int stockCount2 = StringUtil.toInt(request.getParameter("stockCount2"));
		int type = StringUtil.StringToId(request.getParameter("type"));
		String orderType=StringUtil.convertNull(request.getParameter("orderType"));//排序方式，1为按缺货量，2为按销量
		String dayCount=StringUtil.convertNull(request.getParameter("dayCount"));//查询销量的天数
		String storageId=StringUtil.convertNull(request.getParameter("storageId"));//所属仓库
		if(orderType.equals("")){
			orderType="1";
		}
		if(dayCount.equals("")){
			dayCount="1";
		}
		if(storageId.equals("")){
			storageId="-1";
		}
		String params = "";
		String productOriName=StringUtil.convertNull(request.getParameter("productOriName"));
		String paraProductOriName="";
		String dbProductOriName="";
		if(Encoder.decrypt(productOriName)==null){//第一次查询，未编码
			paraProductOriName=Encoder.encrypt(productOriName);
			dbProductOriName=productOriName;
		}else{//后面的查询，已编码
			paraProductOriName=productOriName;
			dbProductOriName=Encoder.decrypt(productOriName);
		}
		String cargoType=StringUtil.convertNull(request.getParameter("cargoType"));
		int productLineId=StringUtil.toInt(request.getParameter("productLineId"));

		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
		try{

			String condition = "";
			StringBuilder buff = new StringBuilder();
			if(!cargoCode.equals("")){
				params = params + "cargoCode="+cargoCode+"&";
				buff.append("ci.whole_code like '"+cargoCode+"%'");
				buff.append(" and ");
			}
			if(!productCode.equals("")){
				params = params + "productCode="+productCode+"&";
				int productId = service.getNumber("id", "product", "max", "code = '"+productCode+"'");
				buff.append("cps.product_id = "+productId);
				buff.append(" and ");
			}
			if(stockCount1 >= 0){
				params = params + "stockCount1="+stockCount1+"&";
				buff.append("(cps.stock_count+cps.stock_lock_count) >= "+stockCount1);
				buff.append(" and ");
			}
			if(stockCount2 > 0){
				params = params + "stockCount2="+stockCount2+"&";
				buff.append("(cps.stock_count+cps.stock_lock_count) <= "+stockCount2);
				buff.append(" and ");
			}
			if(!productOriName.equals("")){
				params = params + "productOriName="+paraProductOriName+"&";
				voProduct product=wareService.getProduct2("a.oriname='"+dbProductOriName+"'");
				if(product==null){
					buff.append("1=2");
					buff.append(" and ");
				}else{
					buff.append("cps.product_id= "+product.getId());
					buff.append(" and ");
				}
			}
			if(productLineId > 0){
				params = params + "productLineId="+productLineId+"&";
				buff.append("ci.product_line_id= "+productLineId);
				buff.append(" and ");
			}
			if(!cargoType.equals("")){
				params = params + "cargoType="+cargoType+"&";
				buff.append("ci.type= "+cargoType);
				buff.append(" and ");
			}
			if(!storageId.equals("")){
				params = params + "storageId="+storageId+"&";
				buff.append("ci.storage_id= "+storageId);
				buff.append(" and ");
			}
			if(type>0){
				params = params + "type="+type+"&";
			}
			params=params+"orderType="+orderType+"&";
			params=params+"dayCount="+dayCount+"&";

			if(params.length()>0){
				condition = buff.toString();
				if(params.endsWith("&")){
					params = "&"+params.substring(0, params.length()-1);
				}
			}

			//过滤整件区有货商品
			String ids = "-1";
			List list = service.getCargoAndProductStockList("ci.store_type = 1 and cps.stock_count > 0 group by cps.product_id", -1, -1, "cps.product_id desc");
			Iterator iter = list.listIterator();
			while(iter.hasNext()){
				CargoProductStockBean cps = (CargoProductStockBean)iter.next();
				ids = ids + "," + cps.getProductId();
			}

			List productIdList = service.getFieldList("cps.product_id", "cargo_info ci join cargo_product_stock cps on ci.id = cps.cargo_id", "ci.stock_type = 0 and ci.store_type = 1 and cps.stock_count > 0", -1, -1, "cps.product_id", "cps.product_id desc", "String");
			condition = condition+"ci.store_type in (0,4) and cps.product_id in ("+ids+")";
			list = service.getCargoAndProductStockList(condition, -1, -1, "(ci.warn_stock_count - cps.stock_count) desc");
			iter = list.listIterator();
			while(iter.hasNext()){
				CargoProductStockBean cps = (CargoProductStockBean)iter.next();
				productIdList.remove(String.valueOf(cps.getProductId()));
			}

			//整件区有货所有商品id
			int countPerPage = 20;
			int totalCount = service.getCargoAndProductStockCount(condition); //根据条件得到 总数量
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
			List sellCountList=new ArrayList();//销量列表
			if(type == 0){
				if(orderType.equals("1")){//按缺货量排序
					list = service.getCargoAndProductStockList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "(ci.warn_stock_count - cps.stock_count) desc");

					String productIds = "0,";
					for(int i=0;i<list.size();i++){//查销量
						CargoProductStockBean cpsBean=(CargoProductStockBean)list.get(i);
						productIds = productIds + cpsBean.getProductId() + ",";
					}
					if(productIds.endsWith(",")){
						productIds = productIds.substring(0,productIds.length()-1);
					}
					Calendar cal1=Calendar.getInstance();//查询开始日期
					Calendar cal2=Calendar.getInstance();//查询结束日期
					cal1.add(Calendar.DATE, (-1)*Integer.parseInt(dayCount));
					cal2.add(Calendar.DATE, -1);
					String date1=cal1.get(Calendar.YEAR)+"-"+((cal1.get(Calendar.MONTH)+1)<10?("0"+(cal1.get(Calendar.MONTH)+1)):(cal1.get(Calendar.MONTH)+1)+"")+"-"+(cal1.get(Calendar.DATE)<10?"0"+cal1.get(Calendar.DATE):cal1.get(Calendar.DATE)+"")+" 00:00:00";
					String date2=cal2.get(Calendar.YEAR)+"-"+((cal2.get(Calendar.MONTH)+1)<10?("0"+(cal2.get(Calendar.MONTH)+1)):(cal2.get(Calendar.MONTH)+1)+"")+"-"+(cal2.get(Calendar.DATE)<10?"0"+cal2.get(Calendar.DATE):cal2.get(Calendar.DATE)+"")+" 23:59:59";
					ResultSet rs = psService.getDbOp().executeQuery("select product_id, sum(stock_out_count) s from stock_card where create_datetime between '"+date1+"' and '"+date2+"' and product_id in ("+productIds+") and card_type = 2 group by product_id");
					HashMap map = new HashMap();
					while(rs.next()){
						int productId = rs.getInt("product_id");
						int sellCount = rs.getInt("s");
						map.put(Integer.valueOf(productId), Integer.valueOf(sellCount));
					}
					for(int i=0;i<list.size();i++){
						CargoProductStockBean cpsBean=(CargoProductStockBean)list.get(i);
						Integer _sellCount = (Integer)map.get(Integer.valueOf(cpsBean.getProductId()));
						int sellCount = _sellCount == null?0:_sellCount.intValue();
						sellCountList.add(sellCount+"");
					}

				}else if(orderType.equals("2")){//按销量排序，效率低
					List wholeList = service.getCargoAndProductStockList(condition, -1, -1, null);//所有符合条件的产品
					HashMap listMap=new HashMap();//key=cpsId,value=销量
					for(int i=0;i<wholeList.size();i++){//查销量
						CargoProductStockBean cpsBean=(CargoProductStockBean)wholeList.get(i);
						int cpsId=cpsBean.getId();
						int productId=cpsBean.getProductId();
						Calendar cal1=Calendar.getInstance();//查询开始日期
						Calendar cal2=Calendar.getInstance();//查询结束日期
						cal1.add(Calendar.DATE, (-1)*Integer.parseInt(dayCount));
						cal2.add(Calendar.DATE, -1);
						String date1=cal1.get(Calendar.YEAR)+"-"+((cal1.get(Calendar.MONTH)+1)<10?("0"+(cal1.get(Calendar.MONTH)+1)):(cal1.get(Calendar.MONTH)+1)+"")+"-"+(cal1.get(Calendar.DATE)<10?"0"+cal1.get(Calendar.DATE):cal1.get(Calendar.DATE)+"")+" 00:00:00";
						String date2=cal2.get(Calendar.YEAR)+"-"+((cal2.get(Calendar.MONTH)+1)<10?("0"+(cal2.get(Calendar.MONTH)+1)):(cal2.get(Calendar.MONTH)+1)+"")+"-"+(cal2.get(Calendar.DATE)<10?"0"+cal2.get(Calendar.DATE):cal2.get(Calendar.DATE)+"")+" 23:59:59";
						List stockCardList=psService.getStockCardList("product_id="+productId+" and create_datetime>'"+date1+"' and create_datetime<'"+date2+"' and card_type=2", -1, -1, null);
						int count=0;//销量
						for(int j=0;j<stockCardList.size();j++){
							StockCardBean card=(StockCardBean)stockCardList.get(j);
							count+=card.getStockOutCount();
						}
						listMap.put(cpsId+"", count+"");
					}
					for(int i=0;i<wholeList.size()-1;i++){//从大到小排序
						for(int j=wholeList.size()-1;j>=i+1;j--){
							CargoProductStockBean cpsBean1=(CargoProductStockBean)wholeList.get(j);
							CargoProductStockBean cpsBean2=(CargoProductStockBean)wholeList.get(j-1);
							int cpsId1=cpsBean1.getId();
							int cpsId2=cpsBean2.getId();
							int sellCount1=Integer.parseInt(listMap.get(cpsId1+"").toString());
							int sellCount2=Integer.parseInt(listMap.get(cpsId2+"").toString());
							if(sellCount1>sellCount2){
								CargoProductStockBean temp=(CargoProductStockBean)wholeList.get(j-1);
								wholeList.set(j-1, (CargoProductStockBean)wholeList.get(j));
								wholeList.set(j, temp);
							}
						}
					}
					list=new ArrayList();
					for(int i=0;i<countPerPage;i++){//符合页码的记录加入list
						if(wholeList.size()>paging.getCurrentPageIndex()*countPerPage+i){
							CargoProductStockBean cpsBean=(CargoProductStockBean)wholeList.get(paging.getCurrentPageIndex()*countPerPage+i);
							list.add(cpsBean);
							sellCountList.add(listMap.get(cpsBean.getId()+""));
						}else{
							break;
						}
					}
				}
			}

			iter = list.listIterator();
			while(iter.hasNext()){
				CargoProductStockBean cps = (CargoProductStockBean)iter.next();
				productIdList.remove(String.valueOf(cps.getProductId()));
			}
			paging.setPrefixUrl("cargoOperation.do?method=addRefillCargoList"+params);

			if(type == 1){
				if(productIdList.size()>0 && buff.length()==0){
					String productIds = "";
					sellCountList = new ArrayList();
					for(int i=0;i<productIdList.size();i++){
						productIds = productIds + (String)productIdList.get(i)+",";
					}
					productIds = "("+productIds.substring(0, productIds.length()-1)+")";
					
					Calendar cal1=Calendar.getInstance();//查询开始日期
					Calendar cal2=Calendar.getInstance();//查询结束日期
					cal1.add(Calendar.DATE, (-1)*Integer.parseInt(dayCount));
					cal2.add(Calendar.DATE, -1);
					String date1=cal1.get(Calendar.YEAR)+"-"+((cal1.get(Calendar.MONTH)+1)<10?("0"+(cal1.get(Calendar.MONTH)+1)):(cal1.get(Calendar.MONTH)+1)+"")+"-"+(cal1.get(Calendar.DATE)<10?"0"+cal1.get(Calendar.DATE):cal1.get(Calendar.DATE)+"")+" 00:00:00";
					String date2=cal2.get(Calendar.YEAR)+"-"+((cal2.get(Calendar.MONTH)+1)<10?("0"+(cal2.get(Calendar.MONTH)+1)):(cal2.get(Calendar.MONTH)+1)+"")+"-"+(cal2.get(Calendar.DATE)<10?"0"+cal2.get(Calendar.DATE):cal2.get(Calendar.DATE)+"")+" 23:59:59";
					ResultSet rs = psService.getDbOp().executeQuery("select product_id, sum(stock_out_count) s from stock_card where create_datetime between '"+date1+"' and '"+date2+"' and product_id in "+productIds+" and card_type = 2 group by product_id");
					HashMap map = new HashMap();
					while(rs.next()){
						int productId = rs.getInt("product_id");
						int sellCount = rs.getInt("s");
						map.put(Integer.valueOf(productId), Integer.valueOf(sellCount));
					}
					for(int i=0;i<list.size();i++){
						CargoProductStockBean cpsBean=(CargoProductStockBean)list.get(i);
						Integer _sellCount = (Integer)map.get(Integer.valueOf(cpsBean.getProductId()));
						int sellCount = _sellCount == null?0:_sellCount.intValue();
						sellCountList.add(sellCount+"");
					}

					condition = "ci.store_type = "+CargoInfoBean.STORE_TYPE1+" and cps.product_id in "+productIds+" group by cps.product_id";
					list = service.getCargoAndProductStockList(condition, -1, -1, "cps.product_id desc");
					paging = null;
				}else{
					list = new ArrayList();
				}
			}

			iter = list.listIterator();
			while(iter.hasNext()){
				CargoProductStockBean cps = (CargoProductStockBean)iter.next();
				voProduct product = wareService.getProduct(cps.getProductId());
				voProductLine productLine = wareService.getProductLine("product_line.id = "+cps.getCargoInfo().getProductLineId());
				cps.setProduct(product);
				cps.getCargoInfo().setProductLine(productLine);

				//库存信息
				List cargoPSList = service.getCargoAndProductStockList("cps.product_id = "+product.getId(), -1, -1, "ci.id asc");
				product.setCargoPSList(cargoPSList);

			}
			List productLineList=wareService.getProductLineList("1=1");
			List areaList=CargoDeptAreaService.getCargoDeptAreaList(request);
			String areaStr="";//登陆用户能操作的地区列表
			for(int i=0;i<areaList.size();i++){
				areaStr+=areaList.get(i);
				areaStr+=",";
			}
			List storageList=service.getCargoInfoStorageList("area_id in ("+areaStr+"-1)", -1, -1, null);
			request.setAttribute("storageList", storageList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("list", list);
			request.setAttribute("paging", paging);
			request.setAttribute("sellCountList", sellCountList);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}

		return mapping.findForward("addRefillCargo");
	}

	/**
	 *	添加补货单
	 */
	public ActionForward addRefillCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int cargoOperId = 0;
		synchronized(cargoLock){

			String[] cargoProducStockIds = request.getParameterValues("cargoProducStockId");
			if(cargoProducStockIds == null || cargoProducStockIds.length == 0){
				request.setAttribute("tip", "请选择货位，再生成补货单！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}

			//新采购计划编号：CGJIH20090601001
			String code = "HWB"+DateUtil.getNow().substring(2,10).replace("-", "");   

			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{

				service.getDbOp().startTransaction();

				String storageCode = wareService.getString("whole_code", "cargo_info", "id = (select cargo_id from cargo_product_stock where id = "+cargoProducStockIds[0]+")");
				storageCode = storageCode.substring(0,storageCode.indexOf("-"));
				CargoInfoStorageBean storage=service.getCargoInfoStorage("whole_code='"+storageCode+"'");
				if(storage==null){
					request.setAttribute("tip", "地区错误！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
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
				//添加作业单
				cargoOper = new CargoOperationBean();
				cargoOper.setCode(code);
				cargoOper.setCreateDatetime(DateUtil.getNow());
				cargoOper.setCreateUserId(user.getId());
				cargoOper.setCreateUserName(user.getUsername());
				cargoOper.setRemark("");
				cargoOper.setSource("");
				cargoOper.setStockInType(CargoInfoBean.STORE_TYPE0);
				cargoOper.setStockOutType(CargoInfoBean.STORE_TYPE1);
				cargoOper.setStorageCode(storageCode);
				cargoOper.setType(CargoOperationBean.TYPE2);
				cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS19);
				cargoOper.setLastOperateDatetime(DateUtil.getNow());
				cargoOper.setStockInArea(storage.getAreaId());
				cargoOper.setStockOutArea(storage.getAreaId());
				if(!service.addCargoOperation(cargoOper)){
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				cargoOperId = service.getDbOp().getLastInsertId();

				String newCargoIds = "-1";
				for(int i=0;i<cargoProducStockIds.length;i++){
					String cpsId = cargoProducStockIds[i];
					CargoProductStockBean inCps = service.getCargoProductStock("id = "+cpsId);
					CargoInfoBean inCi = service.getCargoInfo("id = "+inCps.getCargoId());
					voProduct product = wareService.getProduct(inCps.getProductId());

					if(inCi.getStoreType() == CargoInfoBean.STORE_TYPE0||inCi.getStoreType() == CargoInfoBean.STORE_TYPE4){  //有散件区或混合区货位，直接添加
						if(inCi.getStoreType() == CargoInfoBean.STORE_TYPE0){
							//判断该货位是否有相关调拨单
							String sql="select * from cargo_operation co left join cargo_operation_cargo coc on " +
							"co.id=coc.oper_id where co.status in(28,29,30,31,32,33) and co.type=3 and coc.out_cargo_whole_code='"+
							inCi.getWholeCode()+"';";
							service.getDbOp().prepareStatement(sql);
							PreparedStatement ps = wareService.getDbOp().getPStmt();
							ResultSet rs = ps.executeQuery();
							if(rs.next()){
								request.setAttribute("tip", "货位"+inCi.getWholeCode()+"已生成调拨单，无法添加！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							rs.close();
						}
						//添加目的货位信息
						CargoOperationCargoBean inCoc = new CargoOperationCargoBean();
						inCoc.setOperId(cargoOperId);
						inCoc.setInCargoProductStockId(inCps.getId());
						inCoc.setInCargoWholeCode(inCi.getWholeCode());
						inCoc.setProductId(inCps.getProductId());
						inCoc.setType(0);
						if(!service.addCargoOperationCargo(inCoc)){
							request.setAttribute("tip", "添加失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}

						List cpsList = service.getCargoAndProductStockList(
								"ci.stock_type = 0 and ci.store_type = 1 and cps.product_id = "+inCps.getProductId()+" and cps.stock_count > 0", -1, -1, "ci.whole_code asc");
						if(cpsList==null||cpsList.size()==0){
							request.setAttribute("tip", "存在整件区无货产品，请重新添加！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}

						CargoOperationLogBean logBean = new CargoOperationLogBean();//作业单操作记录
						logBean.setOperId(cargoOperId);
						logBean.setOperAdminId(user.getId());
						logBean.setOperAdminName(user.getUsername());
						logBean.setOperDatetime(DateUtil.getNow());
						StringBuilder logRemark=new StringBuilder("制单：商品");
						logRemark.append(product.getCode());
						logRemark.append("，散件区货位（");
						logRemark.append(inCi.getWholeCode());
						logRemark.append("）");
						//添加源货位信息
						int refillCount = inCi.getMaxStockCount()-inCps.getStockCount()-inCps.getStockLockCount()-inCi.getSpaceLockCount();
						for(int j=0;j<cpsList.size();j++){
							CargoProductStockBean outCps = (CargoProductStockBean)cpsList.get(j);
							CargoInfoBean outCi = service.getCargoInfo("id = "+outCps.getCargoId());

							CargoOperationCargoBean cocOut = new CargoOperationCargoBean();
							cocOut.setOperId(cargoOperId);
							cocOut.setInCargoProductStockId(inCps.getId());
							cocOut.setOutCargoProductStockId(outCps.getId());
							cocOut.setOutCargoWholeCode(outCi.getWholeCode());
							cocOut.setProductId(outCps.getProductId());
							cocOut.setType(1);
							if(refillCount<=outCps.getStockCount()&&refillCount>0){
								cocOut.setStockCount(refillCount);
							}else if(refillCount > outCps.getStockCount()){
								cocOut.setStockCount(outCps.getStockCount());
								refillCount = refillCount - outCps.getStockCount();
							}
							logRemark.append("，整件区货位（");
							logRemark.append(outCi.getWholeCode());
							logRemark.append("），");
							logRemark.append("补货量（");
							logRemark.append(cocOut.getStockCount());
							logRemark.append("）");
							if(!service.addCargoOperationCargo(cocOut)){
								request.setAttribute("tip", "添加失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

						}
						logBean.setRemark(logRemark.toString());
						service.addCargoOperationLog(logBean);
					}else{ //无散件区货位，推荐货位
						synchronized(cargoAssignLock){
							voProductLine productLine = wareService.getProductLine("product_line_catalog.catalog_id in ("+product.getParentId1()+","+product.getParentId2()+")");
							CargoInfoBean cargo = null;
							if(productLine != null){
								cargo = service.getCargoInfo("product_line_id = "+productLine.getId()+" and status = "+CargoInfoBean.STATUS1+" and store_type = 0 and stock_type = 0 and area_id = "+inCi.getAreaId()+" and id not in ("+newCargoIds+") order by whole_code asc limit 1");
								if(cargo == null){
									cargo = service.getCargoInfo("status = "+CargoInfoBean.STATUS1+" and store_type = 0 and stock_type = 0 and area_id = "+inCi.getAreaId()+" and id not in ("+newCargoIds+") order by whole_code asc limit 1");
								}
							}else{
								cargo = service.getCargoInfo("status = "+CargoInfoBean.STATUS1+" and store_type = 0 and stock_type = 0 and area_id = "+inCi.getAreaId()+" and id not in ("+newCargoIds+") order by whole_code asc limit 1");
							}
							if(cargo == null){
								request.setAttribute("tip", "散件区无未使用的货位，添加失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							//目的货位是散件区或混合区，需要判断是否有相关调拨单
							if(cargo.getStoreType()==CargoInfoBean.STORE_TYPE0){
								//判断该货位是否有相关调拨单
								String sql2="select * from cargo_operation co left join cargo_operation_cargo coc on " +
								"co.id=coc.oper_id where co.status in(28,29,30,31,32,33) and co.type=3 and coc.out_cargo_whole_code='"+
								cargo.getWholeCode()+"';";
								service.getDbOp().prepareStatement(sql2);
								PreparedStatement ps2 = wareService.getDbOp().getPStmt();
								ResultSet rs2 = ps2.executeQuery();
								if(rs2.next()){
									request.setAttribute("tip", "货位"+cargo.getWholeCode()+"已生成调拨单，无法添加！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							}

							//绑定产品，添加产品库存数据
							CargoProductStockBean cps = new CargoProductStockBean();
							cps.setCargoId(cargo.getId());
							cps.setProductId(product.getId());
							service.addCargoProductStock(cps);
							service.updateCargoInfo("status = 0", "id = "+cargo.getId());
							newCargoIds = newCargoIds+","+cargo.getId();


							//添加目的货位信息
							int lastCpsId = service.getDbOp().getLastInsertId();
							CargoOperationCargoBean inCoc = new CargoOperationCargoBean();
							inCoc.setOperId(cargoOperId);
							inCoc.setInCargoProductStockId(lastCpsId);
							inCoc.setInCargoWholeCode(cargo.getWholeCode());
							inCoc.setProductId(inCps.getProductId());
							inCoc.setType(0);
							if(!service.addCargoOperationCargo(inCoc)){
								request.setAttribute("tip", "添加失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							CargoOperationLogBean logBean = new CargoOperationLogBean();//作业单操作记录
							logBean.setOperId(cargoOperId);
							logBean.setOperAdminId(user.getId());
							logBean.setOperAdminName(user.getUsername());
							logBean.setOperDatetime(DateUtil.getNow());
							StringBuilder logRemark=new StringBuilder("制单：商品");
							logRemark.append(product.getCode());
							logRemark.append("，新分配散件区货位（");
							logRemark.append(inCi.getWholeCode());
							logRemark.append("）");

							if(!service.updateCargoOperation(
									"storage_code = '"+cargo.getWholeCode().substring(0,cargo.getWholeCode().indexOf("-"))+"'", 
									"id = "+cargoOperId)){
								request.setAttribute("tip", "数据库操作失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							List cpsList = service.getCargoAndProductStockList(
									"ci.stock_type = 0 and ci.store_type = 1 and cps.product_id = "+inCps.getProductId()+" and cps.stock_count > 0", -1, -1, "ci.whole_code asc");
							if(cpsList==null||cpsList.size()==0){
								request.setAttribute("tip", "存在整件区无货产品，请重新添加！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							//添加源货位信息
							int refillCount = cargo.getMaxStockCount();
							for(int j=0;j<cpsList.size();j++){
								CargoProductStockBean outCps = (CargoProductStockBean)cpsList.get(j);
								CargoInfoBean outCi = service.getCargoInfo("id = "+outCps.getCargoId());

								CargoOperationCargoBean cocOut = new CargoOperationCargoBean();
								cocOut.setOperId(cargoOperId);
								cocOut.setInCargoProductStockId(lastCpsId);
								cocOut.setInCargoWholeCode(cargo.getWholeCode());
								cocOut.setOutCargoProductStockId(outCps.getId());
								cocOut.setOutCargoWholeCode(outCi.getWholeCode());
								cocOut.setProductId(outCps.getProductId());
								cocOut.setType(1);
								if(refillCount<=outCps.getStockCount()&&refillCount>0){
									cocOut.setStockCount(refillCount);
								}else if(refillCount > outCps.getStockCount()){
									cocOut.setStockCount(outCps.getStockCount());
									refillCount = refillCount - outCps.getStockCount();
								}
								logRemark.append("，整件区货位（");
								logRemark.append(outCi.getWholeCode());
								logRemark.append("），");
								logRemark.append("补货量（");
								logRemark.append(cocOut.getStockCount());
								logRemark.append("）");
								if(!service.addCargoOperationCargo(cocOut)){
									request.setAttribute("tip", "添加失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}

							}
							logBean.setRemark(logRemark.toString());
							service.addCargoOperationLog(logBean);
						}
					}
				}

				CargoOperLogBean operLog=new CargoOperLogBean();//员工操作日志
				operLog.setOperId(service.getCargoOperation("code='"+cargoOper.getCode()+"'").getId());
				operLog.setOperCode(cargoOper.getCode());
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS19);
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
					request.setAttribute("tip", "添加日志数据时发生异常！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				};
				
				service.getDbOp().commitTransaction();
			}catch (Exception e) {
				if(stockLog.isErrorEnabled()){
					stockLog.error("addRefillCargo", e);
				}
				service.getDbOp().rollbackTransaction();
				request.setAttribute("tip", "系统异常，请联系管理员");
				request.setAttribute("url", "cargoOperation.do?method=refillCargo&id="+cargoOperId);
				return mapping.findForward("tip");
			}finally{
				service.releaseAll();
			}
		}

		request.setAttribute("tip", "添加成功");
		request.setAttribute("url", "cargoOperation.do?method=refillCargo&id="+cargoOperId);
		return mapping.findForward("tip");
	}

	/**
	 *	根据缺货列表添加补货单
	 */
	public ActionForward addRefillCargo2(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int cargoOperId = 0;
		synchronized(cargoLock){

			String[] productIds = request.getParameterValues("productId");
			if(request.getParameterValues("productId2")!=null){
				productIds=request.getParameterValues("productId2");
			}
			if(productIds == null || productIds.length == 0){
				request.setAttribute("tip", "请选择缺货产品，再生成补货单！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int areaId = StringUtil.StringToId(request.getParameter("areaId"));

			//新采购计划编号：CGJIH20090601001
			String code = "HWB"+DateUtil.getNow().substring(2,10).replace("-", "");   

			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{

				service.getDbOp().startTransaction();

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
				//添加作业单
				cargoOper = new CargoOperationBean();
				cargoOper.setCode(code);
				cargoOper.setCreateDatetime(DateUtil.getNow());
				cargoOper.setCreateUserId(user.getId());
				cargoOper.setCreateUserName(user.getUsername());
				cargoOper.setRemark("");
				cargoOper.setSource("");
				cargoOper.setStockInType(CargoInfoBean.STORE_TYPE0);
				cargoOper.setStockOutType(CargoInfoBean.STORE_TYPE1);
				cargoOper.setStorageCode("");
				cargoOper.setType(CargoOperationBean.TYPE2);
				cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS19);
				cargoOper.setLastOperateDatetime(DateUtil.getNow());
				if(!service.addCargoOperation(cargoOper)){
					request.setAttribute("tip", "添加失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				cargoOperId = service.getDbOp().getLastInsertId();

				CargoInfoAreaBean area = service.getCargoInfoArea("old_id = "+areaId);
				String newCargoIds = "-1";
				for(int i=0;i<productIds.length;i++){
					int productId = StringUtil.StringToId(productIds[i]);
					//推荐货位
					CargoProductStockBean inCps = service.getCargoAndProductStock("ci.area_id = "+area.getId()+" and ci.store_type = 4 and cps.product_id = "+productId+" order by ci.whole_code asc");
					voProduct product = wareService.getProduct(productId);
					if(inCps != null){  //有混合区货位，直接添加
						//添加目的货位信息
						CargoOperationCargoBean inCoc = new CargoOperationCargoBean();
						inCoc.setOperId(cargoOperId);
						inCoc.setInCargoProductStockId(inCps.getId());
						inCoc.setInCargoWholeCode(inCps.getCargoInfo().getWholeCode());
						inCoc.setProductId(inCps.getProductId());
						inCoc.setType(0);
						if(!service.addCargoOperationCargo(inCoc)){
							request.setAttribute("tip", "添加失败！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}

						//更新操作库
						service.updateCargoOperation("storage_code = '"+inCps.getCargoInfo().getWholeCode().substring(0,inCps.getCargoInfo().getWholeCode().indexOf("-"))+"'", "id = "+cargoOperId);

						List cpsList = service.getCargoAndProductStockList(
								"ci.stock_type = 0 and ci.store_type = 1 and cps.product_id = "+inCps.getProductId()+" and cps.stock_count > 0", -1, -1, "ci.whole_code asc");
						if(cpsList==null||cpsList.size()==0){
							request.setAttribute("tip", "存在整件区无货产品，请重新添加！");
							request.setAttribute("result", "failure");
							return mapping.findForward(IConstants.FAILURE_KEY);
						}


						CargoOperationLogBean logBean = new CargoOperationLogBean();//作业单操作记录
						logBean.setOperId(cargoOperId);
						logBean.setOperAdminId(user.getId());
						logBean.setOperAdminName(user.getUsername());
						logBean.setOperDatetime(DateUtil.getNow());
						StringBuilder logRemark=new StringBuilder("制单：商品");
						logRemark.append(product.getCode());
						logRemark.append("，散件区货位（");
						logRemark.append(inCoc.getInCargoWholeCode());
						logRemark.append("）");

						//添加源货位信息
						int refillCount = StringUtil.StringToId(request.getParameter("lackCount"+productId));
						for(int j=0;j<cpsList.size();j++){
							CargoProductStockBean outCps = (CargoProductStockBean)cpsList.get(j);
							CargoInfoBean outCi = service.getCargoInfo("id = "+outCps.getCargoId());

							CargoOperationCargoBean cocOut = new CargoOperationCargoBean();
							cocOut.setOperId(cargoOperId);
							cocOut.setInCargoProductStockId(inCps.getId());
							cocOut.setOutCargoProductStockId(outCps.getId());
							cocOut.setOutCargoWholeCode(outCi.getWholeCode());
							cocOut.setProductId(outCps.getProductId());
							cocOut.setType(1);
							if(refillCount<=outCps.getStockCount()&&refillCount>0){
								cocOut.setStockCount(refillCount);
							}else if(refillCount > outCps.getStockCount()){
								cocOut.setStockCount(outCps.getStockCount());
								refillCount = refillCount - outCps.getStockCount();
							}
							logRemark.append("，整件区货位（");
							logRemark.append(outCi.getWholeCode());
							logRemark.append("），");
							logRemark.append("补货量（");
							logRemark.append(refillCount);
							logRemark.append("）");
							if(!service.addCargoOperationCargo(cocOut)){
								request.setAttribute("tip", "添加失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
						logBean.setRemark(logRemark.toString());
						service.addCargoOperationLog(logBean);
					}else{ //无散件区货位，推荐货位
						synchronized(cargoAssignLock){
							voProductLine productLine = wareService.getProductLine("product_line_catalog.catalog_id in ("+product.getParentId1()+","+product.getParentId2()+")");
							CargoInfoBean cargo = null;
							if(productLine != null){
								cargo = service.getCargoInfo("product_line_id = "+productLine.getId()+" and status = "+CargoInfoBean.STATUS1+" and id not in ("+newCargoIds+") order by whole_code asc limit 1");
								if(cargo == null){
									cargo = service.getCargoInfo("status = "+CargoInfoBean.STATUS1+" and id not in ("+newCargoIds+") order by whole_code asc limit 1");
								}
							}else{
								cargo = service.getCargoInfo("status = "+CargoInfoBean.STATUS1+" and id not in ("+newCargoIds+") order by whole_code asc limit 1");
							}

							if(cargo == null){
								request.setAttribute("tip", "散件区和混合区无未使用的货位，添加失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							//绑定产品，添加产品库存数据
							CargoProductStockBean cps = new CargoProductStockBean();
							cps.setCargoId(cargo.getId());
							cps.setProductId(product.getId());
							service.addCargoProductStock(cps);
							service.updateCargoInfo("status = 0", "id = "+cargo.getId());
							newCargoIds = newCargoIds+","+cargo.getId();

							CargoOperationLogBean logBean = new CargoOperationLogBean();//作业单操作记录
							logBean.setOperId(cargoOperId);
							logBean.setOperAdminId(user.getId());
							logBean.setOperAdminName(user.getUsername());
							logBean.setOperDatetime(DateUtil.getNow());
							StringBuilder logRemark=new StringBuilder("制单：商品");
							logRemark.append(product.getCode());
							logRemark.append("，新分配散件区货位（");
							logRemark.append(cargo.getWholeCode());
							logRemark.append("）");
							//添加目的货位信息
							int lastCpsId = service.getDbOp().getLastInsertId();
							CargoOperationCargoBean inCoc = new CargoOperationCargoBean();
							inCoc.setOperId(cargoOperId);
							inCoc.setInCargoProductStockId(lastCpsId);
							inCoc.setInCargoWholeCode(cargo.getWholeCode());
							inCoc.setProductId(productId);
							inCoc.setType(0);

							if(!service.addCargoOperationCargo(inCoc)){
								request.setAttribute("tip", "添加失败！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							service.updateCargoOperation("storage_code = '"+cargo.getWholeCode().substring(0,cargo.getWholeCode().indexOf("-"))+"'", "id = "+cargoOperId);

							List cpsList = service.getCargoAndProductStockList(
									"ci.stock_type = 0 and ci.store_type = 1 and cps.product_id = "+productId+" and cps.stock_count > 0", -1, -1, "ci.whole_code asc");
							if(cpsList==null||cpsList.size()==0){
								request.setAttribute("tip", "存在整件区无货产品，请重新添加！");
								request.setAttribute("result", "failure");
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							//添加源货位信息
							int refillCount = StringUtil.StringToId(request.getParameter("lackCount"+productId));
							for(int j=0;j<cpsList.size();j++){
								CargoProductStockBean outCps = (CargoProductStockBean)cpsList.get(j);
								CargoInfoBean outCi = service.getCargoInfo("id = "+outCps.getCargoId());

								CargoOperationCargoBean cocOut = new CargoOperationCargoBean();
								cocOut.setOperId(cargoOperId);
								cocOut.setInCargoProductStockId(lastCpsId);
								cocOut.setInCargoWholeCode(cargo.getWholeCode());
								cocOut.setOutCargoProductStockId(outCps.getId());
								cocOut.setOutCargoWholeCode(outCi.getWholeCode());
								cocOut.setProductId(outCps.getProductId());
								cocOut.setType(1);
								if(refillCount<=outCps.getStockCount()&&refillCount>0){
									cocOut.setStockCount(refillCount);
								}else if(refillCount > outCps.getStockCount()){
									cocOut.setStockCount(outCps.getStockCount());
									refillCount = refillCount - outCps.getStockCount();
								}
								logRemark.append("，整件区货位（");
								logRemark.append(outCi.getWholeCode());
								logRemark.append("），");
								logRemark.append("补货量（");
								logRemark.append(refillCount);
								logRemark.append("）");
								if(!service.addCargoOperationCargo(cocOut)){
									request.setAttribute("tip", "添加失败！");
									request.setAttribute("result", "failure");
									return mapping.findForward(IConstants.FAILURE_KEY);
								}

							}
							logBean.setRemark(logRemark.toString());
							service.addCargoOperationLog(logBean);
						}
					}
				}

				CargoOperLogBean operLog=new CargoOperLogBean();//员工操作日志
				operLog.setOperId(service.getCargoOperation("code='"+cargoOper.getCode()+"'").getId());
				operLog.setOperCode(cargoOper.getCode());
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS19);
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
					request.setAttribute("tip", "添加日志数据时发生异常！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				service.getDbOp().commitTransaction();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				service.releaseAll();
			}
		}

		request.setAttribute("tip", "添加成功");
		request.setAttribute("url", "cargoOperation.do?method=refillCargo&id="+cargoOperId);
		return mapping.findForward("tip");
	}

	/**
	 *	补货单列表
	 */
	public ActionForward refillCargoList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		String[] statuss = request.getParameterValues("status");
		String operCode = StringUtil.convertNull(request.getParameter("operCode"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode"));
		String status = "";
		if(statuss!=null){
			for(int i=0;i<statuss.length;i++){
				if(statuss[i].equals("21")){
					status=status+"21" +",";
					status=status+"22" +",";
					status=status+"23" +",";
					status=status+"24" +",";
				}else if(statuss[i].equals("25")){
					status=status+"25" +",";
					status=status+"26" +",";
					status=status+"27" +",";
				}else{
					status = status + statuss[i] +",";
				}
			}
			if(status.endsWith(",")){
				status = status.substring(0, status.length()-1);
			}
		}
		String params = "";

		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{

			String condition = null;
			StringBuilder buff = new StringBuilder();
			buff.append("type = "+CargoOperationBean.TYPE2);
			if(!status.equals("")){
				params = params + "status="+status+"&";
				buff.append(" and ");
				buff.append("status in ("+status+")");
			}
			if(!operCode.equals("")){
				params = params + "operCode="+operCode+"&";
				buff.append(" and ");
				buff.append("code = '"+operCode+"'");
			}
			if(!productCode.equals("")||!cargoCode.equals("")){
				buff.append(" and id in (select coc.oper_id from cargo_operation_cargo coc join product p on coc.product_id = p.id where ");
				if(!productCode.equals("")){
					params = params + "productCode="+productCode+"&";
					buff.append("p.code = '"+productCode+"'");
				}
				if(!cargoCode.equals("")){
					params = params + "cargoCode="+cargoCode+"&";
					if(!buff.toString().endsWith("where ")){
						buff.append(" and ");
					}
					buff.append("coc.in_cargo_whole_code = '"+cargoCode+"'");
				}
				buff.append(")");
			}

			if(buff.length()>0){
				condition = buff.toString();
				if(params.endsWith("&")){
					params = "&"+params.substring(0, params.length()-1);
				}
			}
			int countPerPage = 20;
			int totalCount = service.getCargoOperationCount(condition);
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
			List list = service.getCargoOperationList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");

			Iterator itr= list.iterator(); //获取调拨单id 方便查询该调拨单明细
			while(itr.hasNext()){
				CargoOperationBean bean = (CargoOperationBean)itr.next();
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+bean.getStatus());
				if(process!=null){
					bean.setStatusName(process.getStatusName());
				}else{
					bean.setStatusName("");
				}
			}

			paging.setPrefixUrl("cargoOperation.do?method=refillCargoList"+params);
			request.setAttribute("list", list);
			request.setAttribute("paging", paging);
			request.setAttribute("para", params);

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}

		return mapping.findForward("refillCargoList");
	}

	/**
	 *	补货单详细页
	 */
	public ActionForward refillCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int id = StringUtil.StringToId(request.getParameter("id"));
		String printFlag = StringUtil.convertNull(request.getParameter("printFlag"));

		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{

			CargoOperationBean cargoOperation = service.getCargoOperation("id = "+id);
			if(cargoOperation == null){
				request.setAttribute("tip", "该作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}

			List cocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and type = 0", -1, -1, "product_id desc");
			Iterator iter = cocList.listIterator();
			while(iter.hasNext()){
				CargoOperationCargoBean inCoc = (CargoOperationCargoBean)iter.next();
				CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
				CargoInfoBean ci = service.getCargoInfo("whole_code= '"+inCoc.getInCargoWholeCode()+"'");
				voProduct product = wareService.getProduct(inCoc.getProductId());
				inCoc.setCargoProductStock(inCps);
				inCoc.setCargoInfo(ci);
				inCoc.setProduct(product);

				List list = service.getCargoOperationCargoList(
						"in_cargo_product_stock_id = "+inCoc.getInCargoProductStockId()+" and oper_id = "+inCoc.getOperId()+" and product_id = "+inCoc.getProductId()+" and type = 1", -1, -1, "id asc");
				inCoc.setCocList(list);

				Iterator iter2 = list.listIterator();
				while(iter2.hasNext()){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)iter2.next();
					CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
					CargoInfoBean outCi = service.getCargoInfo("whole_code = '"+outCoc.getOutCargoWholeCode()+"'");
					product = wareService.getProduct(outCoc.getProductId());
					outCoc.setCargoProductStock(outCps);
					outCoc.setCargoInfo(outCi);
					outCoc.setProduct(product);
				}
			}

			CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());
			if(process!=null){
				cargoOperation.setStatusName(process.getStatusName());
			}
			CargoOperationProcessBean nextStatus=null;//下一个状态，传参用
			CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+(cargoOperation.getStatus()+1));//下一个状态
			CargoOperationProcessBean complete=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS25);//完成作业单状态
			if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS20
					||cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS21
					||cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS22
					||cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS23){
				if(process2.getUseStatus()==1){
					nextStatus=process2;
				}else{
					nextStatus=complete;
				}
			}
			if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS24){
				nextStatus=complete;
			}
			request.setAttribute("nextStatus", nextStatus);
			String effectTime="";
			Calendar cal1=Calendar.getInstance();
			if(cargoOperation.getLastOperateDatetime()==null){
				cargoOperation.setLastOperateDatetime(cargoOperation.getCreateDatetime());
			}
			cal1.setTime(DateUtil.parseDate(cargoOperation.getLastOperateDatetime(), "yyyy-MM-dd HH:mm:ss"));
			Calendar cal2=Calendar.getInstance();
			cal1.add(Calendar.MINUTE, process.getEffectTime());
			if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS25){
				effectTime="待复核";
			}else if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS26){
				effectTime="作业成功";
			}else if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS27){
				effectTime="作业失败";
			}else{
				if(cal1.before(cal2)){
					effectTime="已超时";
				}else{
					effectTime="进行中";
				}
			}
			request.setAttribute("effectTime", effectTime);

			request.setAttribute("cocList", cocList);
			request.setAttribute("cargoOperation", cargoOperation);

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}

		if("print".equals(printFlag)){
			return mapping.findForward("refillCargoPrint");
		}

		return mapping.findForward("refillCargo");
	}

	/**
	 *	打印补货单
	 */
	public ActionForward printRefillCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int id = StringUtil.StringToId(request.getParameter("id"));

		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{

			CargoOperationBean cargoOperation = service.getCargoOperation("id = "+id);
			if(cargoOperation == null){
				request.setAttribute("tip", "该作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}

			TreeMap printMap=new TreeMap();
			List cocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and type = 0", -1, -1, "product_id desc");
			Iterator iter = cocList.listIterator();
			while(iter.hasNext()){
				CargoOperationCargoBean inCoc = (CargoOperationCargoBean)iter.next();
				CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
				CargoInfoBean ci = service.getCargoInfo("whole_code= '"+inCoc.getInCargoWholeCode()+"'");
				voProduct product = wareService.getProduct(inCoc.getProductId());
				inCoc.setCargoProductStock(inCps);
				inCoc.setCargoInfo(ci);
				inCoc.setProduct(product);

				List list = service.getCargoOperationCargoList(
						"in_cargo_product_stock_id = "+inCoc.getInCargoProductStockId()+" and oper_id = "+inCoc.getOperId()+" and product_id = "+inCoc.getProductId()+" and type = 1 and use_status=1", -1, -1, "id asc");
				inCoc.setCocList(list);

				Iterator iter2 = list.listIterator();
				while(iter2.hasNext()){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)iter2.next();
					CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
					CargoInfoBean outCi = service.getCargoInfo("whole_code = '"+outCoc.getOutCargoWholeCode()+"'");
					product = wareService.getProduct(outCoc.getProductId());
					outCoc.setCargoProductStock(outCps);
					outCoc.setCargoInfo(outCi);
					outCoc.setProduct(product);

					CargoOperationCargoBean printCoc=outCoc;
					printCoc.setInCargoWholeCode(inCoc.getInCargoWholeCode());
					printMap.put(printCoc.getInCargoWholeCode()+"-"+printCoc.getId(), printCoc);
				}

				CargoInfoAreaBean stockArea = service.getCargoInfoArea("id = "+ci.getAreaId());
				request.setAttribute("stockAreaName", stockArea.getName());
				request.setAttribute("stockStockName", ci.getStockTypeName());
			}

			service.updateCargoOperation("print_count = print_count+1", "id = "+id);

			request.setAttribute("printMap", printMap);
			request.setAttribute("cocList", cocList);
			request.setAttribute("cargoOperation", cargoOperation);

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}

		return mapping.findForward("refillCargoPrint");
	}

	/**
	 *	 编辑补货单
	 */
	public ActionForward editRefillCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("operId"));
		String action = StringUtil.convertNull(request.getParameter("action"));

		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{

				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS19){
					request.setAttribute("tip", "该作业单已确认，无法编辑！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				service.getDbOp().startTransaction();
				List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 0", -1, -1, "id asc");
				for(int i=0;i<inCocList.size();i++){
					CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
					voProduct product=wareService.getProduct(inCoc.getProductId());
					CargoInfoBean cargoInfo=service.getCargoInfo("whole_code='"+inCoc.getInCargoWholeCode()+"'");
					CargoProductStockBean cpsBean=service.getCargoProductStock("id="+inCoc.getInCargoProductStockId());
					//该货位可补货量
					int count=cargoInfo.getMaxStockCount()-cargoInfo.getSpaceLockCount()-cpsBean.getStockCount()-cpsBean.getStockLockCount();

					CargoOperationLogBean logBean = new CargoOperationLogBean();//作业单操作记录
					logBean.setOperId(cargoOperation.getId());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					logBean.setOperDatetime(DateUtil.getNow());
					StringBuilder logRemark=new StringBuilder("保存编辑：商品");
					logRemark.append(product.getCode());
					logRemark.append("，散件区货位（");
					logRemark.append(inCoc.getInCargoWholeCode());
					logRemark.append("）");
					List outCocIds = service.getFieldList("id", "cargo_operation_cargo", 
							"oper_id = "+operId+" and type = 1 and in_cargo_product_stock_id = "+inCoc.getInCargoProductStockId(), -1, -1, null, null, "String");
					String[] checkedCocIds = request.getParameterValues("cargoOperationCargoId"+inCoc.getInCargoProductStockId());
					if(checkedCocIds == null||checkedCocIds.length == 0){
						request.setAttribute("tip", "产品必须至少选择一个货位");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					for(int j=0;j<checkedCocIds.length;j++){
						int refillCount = StringUtil.StringToId(request.getParameter("refillCount"+checkedCocIds[j]).trim());
						String checkedCocId=checkedCocIds[j];
						CargoOperationCargoBean outCoc=service.getCargoOperationCargo("id="+checkedCocId);
						if(refillCount<0){
							request.setAttribute("tip", "本次作业量，必须输入大于等于0的整数");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if(action.equals("confirm") &&refillCount<=0){
							request.setAttribute("tip", "本次作业量，必须输入大于0的整数");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}

						count-=refillCount;
						if((cargoInfo.getStoreType()==CargoInfoBean.STORE_TYPE0||cargoInfo.getStoreType()==CargoInfoBean.STORE_TYPE0)&&count<0){
							request.setAttribute("tip", product.getOriname()+"产品的目的货位"+cargoInfo.getWholeCode()+"容量已满，请重新分配货位！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}

						logRemark.append("，整件区货位（");
						logRemark.append(outCoc.getOutCargoWholeCode());
						logRemark.append("），");
						logRemark.append("补货量（");
						logRemark.append(outCoc.getStockCount());
						logRemark.append("-");
						logRemark.append(refillCount);
						logRemark.append("）");
						if(!service.updateCargoOperationCargo("stock_count = "+refillCount+",use_status = 1", "id = "+checkedCocIds[j])){
							request.setAttribute("tip", "更新补货单库存量失败");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						outCocIds.remove(String.valueOf(checkedCocIds[j]));
					}
					logBean.setRemark(logRemark.toString());
					service.addCargoOperationLog(logBean);
					boolean flag = false;
					for(int j=0;j<outCocIds.size();j++){
						if(!service.updateCargoOperationCargo("use_status = 0", "id = "+(String)outCocIds.get(j))){
							request.setAttribute("tip", "更新补货单库存量失败");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							flag = true;
							break;
						}
					}
					if(flag){
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				service.getDbOp().commitTransaction();

			}catch (Exception e) {
				if(stockLog.isErrorEnabled()){
					stockLog.error("editRefillCargo", e);
				}
				request.setAttribute("tip", "系统异常，请联系管理员！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}finally{
				service.releaseAll();
			}

			if(action.equals("confirm")){
				return confirmRefillCargo(mapping, form, request, response);
			}else{
				request.setAttribute("tip", "操作成功");
				request.setAttribute("url", "cargoOperation.do?method=refillCargo&id="+operId);
				return mapping.findForward("tip");
			}
		}
	}

	/**
	 *	删除补货单
	 */
	public ActionForward deleteRefillCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int id = StringUtil.StringToId(request.getParameter("id"));

		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{

				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+id);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(user.getId() != cargoOperation.getCreateUserId()){
					request.setAttribute("tip", "没有权限删除该作业单，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS19){
					request.setAttribute("tip", "该作业单已确认，无法删除！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				service.deleteCargoOperation("id = "+id);
				service.deleteCargoOperationCargo("oper_id = "+id);

				CargoOperationLogBean log = new CargoOperationLogBean();
				log.setOperId(cargoOperation.getId());
				log.setOperAdminId(user.getId());
				log.setOperAdminName(user.getUsername());
				log.setOperDatetime(DateUtil.getNow());
				log.setRemark("删除补货作业单："+cargoOperation.getCode());
				service.addCargoOperationLog(log);

			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				service.releaseAll();
			}
		}
		return new ActionForward("/admin/cargoOperation.do?method=refillCargoList");
	}

	/**
	 *	删除作业单产品
	 */
	public ActionForward deleteRefillCargoProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int id = StringUtil.StringToId(request.getParameter("id"));
		int operId = 0;

		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{

				CargoOperationCargoBean coc = service.getCargoOperationCargo("id = "+id);
				if(coc == null){
					request.setAttribute("tip", "该作业单产品不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+coc.getOperId());
				operId = coc.getOperId();
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS19){
					request.setAttribute("tip", "该作业单已确认，无法删除！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				voProduct product=wareService.getProduct(coc.getProductId());
				CargoOperationLogBean logBean = new CargoOperationLogBean();//作业单操作记录
				logBean.setOperId(cargoOperation.getId());
				logBean.setOperAdminId(user.getId());
				logBean.setOperAdminName(user.getUsername());
				logBean.setOperDatetime(DateUtil.getNow());
				logBean.setRemark("删除补货单商品，商品"+product.getCode());
				service.addCargoOperationLog(logBean);
				service.deleteCargoOperationCargo("id = "+coc.getId());
				if(coc.getType() == 0){
					service.deleteCargoOperationCargo("in_cargo_product_stock_id = "+coc.getInCargoProductStockId()+" and type = 1 and oper_id = "+coc.getOperId());
				}else{
					service.deleteCargoOperationCargo("out_cargo_product_stock_id = "+coc.getOutCargoProductStockId()+" and type = 0 and oper_id = "+coc.getOperId());
				}

			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}finally{
				service.releaseAll();
			}
		}
		return new ActionForward("/admin/cargoOperation.do?method=refillCargo&id="+operId);
	}

	/**
	 *	确认补货单
	 */
	public ActionForward confirmRefillCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("operId"));

		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{

				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS19){
					request.setAttribute("tip", "该作业单已确认，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				service.getDbOp().startTransaction();

				List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 0", -1, -1, "id asc");
				if(inCocList == null || inCocList.size() == 0){
					request.setAttribute("tip", "没有需要提交的信息，操作失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				for(int i=0;i<inCocList.size();i++){
					CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
					CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
					voProduct product = wareService.getProduct(inCoc.getProductId());
					if(inCps == null){
						request.setAttribute("tip", "商品"+product.getCode()+"货位"+inCoc.getInCargoWholeCode()+"已被清空或被其他商品占用，操作失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					CargoInfoBean inCi = service.getCargoInfo("id = "+inCps.getCargoId());
					CargoOperationLogBean logBean = new CargoOperationLogBean();//作业单操作记录
					logBean.setOperId(cargoOperation.getId());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					logBean.setOperDatetime(DateUtil.getNow());
					logBean.setRemark("确认提交");
					service.addCargoOperationLog(logBean);
					List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and in_cargo_product_stock_id = "+inCoc.getInCargoProductStockId()+" and type = 1 and use_status = 1", -1, -1, "id asc");
					for(int j=0;j<outCocList.size();j++){
						CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
						CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
						if(inCps!=null&&outCps!=null){
							if(!service.updateCargoProductStockCount(outCps.getId(), -outCoc.getStockCount())){
								request.setAttribute("tip", "操作失败，货位库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if(!service.updateCargoProductStockLockCount(outCps.getId(), outCoc.getStockCount())){
								request.setAttribute("tip", "操作失败，货位库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							//调整合格库库存
							CargoInfoBean outCi=service.getCargoInfo("id="+outCps.getCargoId());
							if(outCi.getAreaId()!=inCi.getAreaId()){
								CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
								ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+product.getId());
								if(outProductStock==null){
									request.setAttribute("tip", "合格库库存数据错误！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								if (!psService.updateProductStockCount(outProductStock.getId(),-outCoc.getStockCount())) {
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								if (!psService.updateProductLockCount(outProductStock.getId(),outCoc.getStockCount())) {
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							}
						}else{
							request.setAttribute("tip", "货位库存信息异常，请联系管理员！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
					}
					//删除coc无用数据
					service.deleteCargoOperationCargo("oper_id = "+operId+" and in_cargo_product_stock_id = "+inCoc.getInCargoProductStockId()+" and type = 1 and use_status = 0");
				}
				if(!service.updateCargoOperation(
						"status = "+CargoOperationProcessBean.OPERATION_STATUS20+"," +
								"effect_status = 0,confirm_datetime = '"+DateUtil.getNow()+"'," +
										"confirm_user_name = '"+user.getUsername()+"'," +
												"last_operate_datetime='"+DateUtil.getNow()+"'", "id = "+operId)){
					request.setAttribute("tip", "更新补货单状态失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
					CargoOperationProcessBean tempProcess=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//生成作业单
					int effectTime=tempProcess.getEffectTime();//生成阶段时效
					String lastOperateTime=lastLog.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
					}
				}

				CargoOperLogBean operLog=new CargoOperLogBean();
				operLog.setOperId(operId);
				operLog.setOperCode(cargoOperation.getCode());
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS19);
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS20);
				operLog.setOperName(process2.getOperName());
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(0);
				operLog.setRemark("");
				operLog.setPreStatusName(process.getStatusName());
				operLog.setNextStatusName(process2.getStatusName());
				service.addCargoOperLog(operLog);
				service.getDbOp().commitTransaction();
			}catch (Exception e) {
				request.setAttribute("tip", "系统异常，请联系管理员！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}finally{
				service.releaseAll();
			}
		}
		request.setAttribute("tip", "操作成功");
		request.setAttribute("url", "cargoOperation.do?method=refillCargo&id="+operId);
		return mapping.findForward("tip");
	}

	/**
	 *	补货单交接阶段
	 */
	public ActionForward auditingRefillCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("operId"));
		int nextStatus=StringUtil.toInt(request.getParameter("nextStatus"));//下一个状态
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{

				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()>=nextStatus){
					request.setAttribute("tip", "该作业单状态已被更新，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				service.getDbOp().startTransaction();

				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+nextStatus);//下个阶段
				if(process==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(process2==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				int handleType=process2.getHandleType();//操作方式，0人工确认，1设备确认
				if(handleType!=0){
					request.setAttribute("tip", "当前操作方式为设备确认！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
//				int confirmType=process2.getConfirmType();//作业判断，0不做判断，1源货位，2目的货位，人工确认不需要判断该条件
//				int deptId1=process2.getDeptId1();//职能归属，一级部门，人工确认不需要判断该条件
//				int deptId2=process2.getDeptId2();//职能归属，二级部门，人工确认不需要判断该条件
//				int storageId=process2.getStorageId();//所属仓库，人工确认不需要判断该条件

				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
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
						"status="+nextStatus+",effect_status = 0," +
								"last_operate_datetime='"+DateUtil.getNow()+"'"+(
										cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS20?("," +
												"auditing_datetime='"+DateUtil.getNow()+"'," +
														"auditing_user_id="+user.getId()+"," +
																"auditing_user_name='"+user.getUsername()+"'"):""), "id="+operId)){
					request.setAttribute("tip", "更新补货单状态失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				CargoOperLogBean operLog=new CargoOperLogBean();
				operLog.setOperId(operId);
				operLog.setOperCode(cargoOperation.getCode());
				operLog.setOperName(process2.getOperName());
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(0);
				operLog.setRemark("");
				operLog.setPreStatusName(process.getStatusName());
				operLog.setNextStatusName(process2.getStatusName());
				service.addCargoOperLog(operLog);

				service.getDbOp().commitTransaction();

			}catch (Exception e) {
				if(stockLog.isErrorEnabled()){
					stockLog.error("auditingRefillCargo error", e);
				}
				request.setAttribute("tip", "系统异常，请联系管理员！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}finally{
				service.releaseAll();
			}
		}
		request.setAttribute("tip", "操作成功");
		request.setAttribute("url", "cargoOperation.do?method=refillCargo&id="+operId);
		return mapping.findForward("tip");
	}

	/**
	 *	补货单作业完成
	 */
	public ActionForward completeRefillCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("operId"));

		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{

				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()>=CargoOperationProcessBean.OPERATION_STATUS25){
					request.setAttribute("tip", "该作业单状态已修改，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				service.getDbOp().startTransaction();
				List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>(); //存放财务接口数据
				
				//完成货位库存量操作
				List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 0", -1, -1, "id asc");
				for(int i=0;i<inCocList.size();i++){
					CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
					CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
					CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getInCargoWholeCode()+"'");
					voProduct product = wareService.getProduct(inCoc.getProductId());
					product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
					int stockInCount = 0;
					int productStockCount=0;
					List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and in_cargo_product_stock_id = "+inCoc.getInCargoProductStockId()+" and type = 1 and use_status = 1", -1, -1, "id asc");
					for(int j=0;j<outCocList.size();j++){
						CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
						CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
						CargoInfoBean outCi = service.getCargoInfo("whole_code = '"+outCoc.getOutCargoWholeCode()+"'");

						if(!service.updateCargoProductStockCount(inCps.getId(), outCoc.getStockCount())){
							request.setAttribute("tip", "操作失败，货位冻结库存不足！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if(!service.updateCargoProductStockLockCount(outCps.getId(), -outCoc.getStockCount())){
							request.setAttribute("tip", "操作失败，货位冻结库存不足！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}

						//调整合格库库存
						if(outCi.getAreaId()!=inCi.getAreaId()){
							CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
							ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+product.getId());
							if(outProductStock==null){
								request.setAttribute("tip", "合格库库存数据错误！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if (!psService.updateProductLockCount(outProductStock.getId(),-outCoc.getStockCount())) {
								request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							productStockCount+=outCoc.getStockCount();
							
							ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+inCi.getAreaId()+" and type="+inCi.getStockType());
							ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+outCi.getAreaId()+" and type="+outCi.getStockType());
							
							//组装财务接口需要的数据
							BaseProductInfo baseProductInfo = new BaseProductInfo();
							baseProductInfo.setId(inCoc.getProductId());
							baseProductInfo.setProductStockOutId(psOut.getId());
							baseProductInfo.setProductStockId(psIn.getId());
							baseProductInfo.setOutCount(inCoc.getStockCount());
							baseList.add(baseProductInfo);
							
							//批次修改开始
							/**
							//更新批次记录、添加调拨出、入库批次记录
							List sbList = stockService.getStockBatchList("product_id="+outCoc.getProductId()+" and stock_type="+outCi.getStockType()+" and stock_area="+outCi.getAreaId(), -1, -1, "id asc");
							double stockinPrice = 0;
							double stockoutPrice = 0;
							if(sbList!=null&&sbList.size()!=0){
								int stockExchangeCount = inCoc.getStockCount();
								int index = 0;
								int stockBatchCount = 0;
								
								do {
									//出库
									StockBatchBean batch = (StockBatchBean)sbList.get(index);
									if(stockExchangeCount>=batch.getBatchCount()){
										if(!stockService.deleteStockBatch("id="+batch.getId())){
											request.setAttribute("tip", "数据库操作失败！");
							                request.setAttribute("result", "failure");
							                service.getDbOp().rollbackTransaction();
							                return mapping.findForward(IConstants.FAILURE_KEY);
										}
										stockBatchCount = batch.getBatchCount();
									}else{
										if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
											request.setAttribute("tip", "数据库操作失败！");
							                request.setAttribute("result", "failure");
							                service.getDbOp().rollbackTransaction();
							                return mapping.findForward(IConstants.FAILURE_KEY);
										}
										stockBatchCount = stockExchangeCount;
									}
									
									//添加批次操作记录
									StockBatchLogBean batchLog = new StockBatchLogBean();
									batchLog.setCode(cargoOperation.getCode());
									batchLog.setStockType(batch.getStockType());
									batchLog.setStockArea(batch.getStockArea());
									batchLog.setBatchCode(batch.getCode());
									batchLog.setBatchCount(stockBatchCount);
									batchLog.setBatchPrice(batch.getPrice());
									batchLog.setProductId(batch.getProductId());
									batchLog.setRemark("调拨出库");
									batchLog.setCreateDatetime(DateUtil.getNow());
									batchLog.setUserId(user.getId());
									batchLog.setSupplierId(batch.getSupplierId());
									batchLog.setTax(batch.getTax());
									if(!stockService.addStockBatchLog(batchLog)){
										 request.setAttribute("tip", "添加失败！");
							             request.setAttribute("result", "failure");
							             service.getDbOp().rollbackTransaction();
							             return mapping.findForward(IConstants.FAILURE_KEY);
									}
									
									stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
									
									//入库
									StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId());
									if(batchBean!=null){
										if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
											request.setAttribute("tip", "数据库操作失败！");
							                request.setAttribute("result", "failure");
							                service.getDbOp().rollbackTransaction();
							                return mapping.findForward(IConstants.FAILURE_KEY);
										}
									}else{
										StockBatchBean newBatch = new StockBatchBean();
										newBatch.setCode(batch.getCode());
										newBatch.setProductId(batch.getProductId());
										newBatch.setPrice(batch.getPrice());
										newBatch.setBatchCount(stockBatchCount);
										newBatch.setProductStockId(psIn.getId());
										newBatch.setStockArea(inCi.getAreaId());
										newBatch.setStockType(psIn.getType());
										newBatch.setSupplierId(batch.getSupplierId());
										newBatch.setTax(batch.getTax());
										newBatch.setNotaxPrice(batch.getNotaxPrice());
										newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
										if(!stockService.addStockBatch(newBatch)){
											request.setAttribute("tip", "添加失败！");
											request.setAttribute("result", "failure");
											service.getDbOp().rollbackTransaction();
											return mapping.findForward(IConstants.FAILURE_KEY);
										}
									}
									
									//添加批次操作记录
									batchLog = new StockBatchLogBean();
									batchLog.setCode(cargoOperation.getCode());
									batchLog.setStockType(psIn.getType());
									batchLog.setStockArea(inCi.getAreaId());
									batchLog.setBatchCode(batch.getCode());
									batchLog.setBatchCount(stockBatchCount);
									batchLog.setBatchPrice(batch.getPrice());
									batchLog.setProductId(batch.getProductId());
									batchLog.setRemark("调拨入库");
									batchLog.setCreateDatetime(DateUtil.getNow());
									batchLog.setUserId(user.getId());
									batchLog.setSupplierId(batch.getSupplierId());
									batchLog.setTax(batch.getTax());
									if(!stockService.addStockBatchLog(batchLog)){
										request.setAttribute("tip", "添加失败！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									
									stockExchangeCount -= batch.getBatchCount();
									index++;
									
									stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
								} while (stockExchangeCount>0&&index<sbList.size());
							}
							*/
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
							int scId = service.getNumber("id", "stock_card", "max", "id > 0") + 1;
							sc2.setId(scId);

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
						service.addCargoStockCard(csc);

						stockInCount = stockInCount + outCoc.getStockCount();
						
						if(outCi.getAreaId()!=inCi.getAreaId()){
							//更新订单缺货状态
							this.updateLackOrder(outCoc.getProductId());
						}
					}

					//调整合格库库存
					CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+inCi.getAreaId());
					ProductStockBean inProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+product.getId());
					if(inProductStock==null){
						request.setAttribute("tip", "合格库库存数据错误！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if (!psService.updateProductStockCount(inProductStock.getId(),productStockCount)) {
						request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
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
				
				//调用财务接口
				if(!baseList.isEmpty()){
					FinanceBaseDataService baseService = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN, service.getDbOp().getConn());
					baseService.acquireFinanceBaseData(baseList, cargoOperation.getCode(), user.getId(), 0, 0);
				}

				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS25);//下个阶段
				if(process==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(process2==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog.getEffectTime()==1){//如果不是进行中，不需要再改时效
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
						"status="+CargoOperationProcessBean.OPERATION_STATUS25+"," +
								"effect_status = 2,last_operate_datetime='"+DateUtil.getNow()+"'"+"," +
										"complete_datetime='"+DateUtil.getNow()+"',complete_user_id="+user.getId()+"," +
												"complete_user_name='"+user.getUsername()+"'", "id="+operId)){
					request.setAttribute("tip", "更新补货单状态失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
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

				request.setAttribute("cargoOperation", cargoOperation);

				service.getDbOp().commitTransaction();

			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				if(stockLog.isErrorEnabled()){
					stockLog.error(StringUtil.getExceptionInfo(e));
				}
				request.setAttribute("tip", "系统异常，请联系管理员！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}finally{
				service.releaseAll();

			}
		}
		request.setAttribute("tip", "操作成功");
		request.setAttribute("url", "cargoOperation.do?method=refillCargo&id="+operId);
		return mapping.findForward("tip");
	}

	/**
	 *	复核补货单
	 */
	public ActionForward checkRefillCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("operId"));
		int status = StringUtil.toInt(request.getParameter("status"));
		if(status==-1){
			request.setAttribute("tip", "请选择复核成功或复核失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String remark = StringUtil.convertNull(request.getParameter("remark"));
		String msg="操作成功";
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{

				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()>CargoOperationProcessBean.OPERATION_STATUS25){
					request.setAttribute("tip", "该作业单状态已修改，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getEffectStatus()==CargoOperationBean.EFFECT_STATUS3||cargoOperation.getEffectStatus()==CargoOperationBean.EFFECT_STATUS4){
					request.setAttribute("tip", "该作业单时效状态已修改，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS19){
					request.setAttribute("tip", "该作业单还未提交，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				service.getDbOp().startTransaction();
				if(status==3){//作业成功
					if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS25){
						request.setAttribute("tip", "该作业单未结束，操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(!service.updateCargoOperation(
							"effect_status="+CargoOperationBean.EFFECT_STATUS3+"," +
									"last_operate_datetime='"+DateUtil.getNow()+"'", "id="+operId)){
						request.setAttribute("tip", "更新补货单时效状态失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}else if(status==4){//作业失败
					if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS25){//作业单未结束
						//还原货位库存
						List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 0", -1, -1, "id asc");
						for(int i=0;i<inCocList.size();i++){
							CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
							CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
							CargoInfoBean inCi = service.getCargoInfo("id = "+inCps.getCargoId());

							List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and in_cargo_product_stock_id = "+inCoc.getInCargoProductStockId()+" and type = 1 and use_status = 1", -1, -1, "id asc");
							for(int j=0;j<outCocList.size();j++){
								CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
								CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
								if(!service.updateCargoProductStockCount(outCps.getId(), outCoc.getStockCount())){
									request.setAttribute("tip", "操作失败，货位冻结库存不足！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								if(!service.updateCargoProductStockLockCount(outCps.getId(), -outCoc.getStockCount())){
									request.setAttribute("tip", "操作失败，货位冻结库存不足！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								//调整合格库库存
								CargoInfoBean outCi=service.getCargoInfo("id="+outCps.getCargoId());
								if(outCi.getAreaId()!=inCi.getAreaId()){
									CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
									ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+inCoc.getProductId());
									if(outProductStock==null){
										request.setAttribute("tip", "合格库库存数据错误！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductStockCount(outProductStock.getId(),outCoc.getStockCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductLockCount(outProductStock.getId(),-outCoc.getStockCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
								}
								if(outCi.getAreaId()!=inCi.getAreaId()){
									//更新订单缺货状态
									this.updateLackOrder(outCoc.getProductId());
								}
							}
						}
					}else if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS25){
						
					}
					if(!service.updateCargoOperation(
							"effect_status="+CargoOperationBean.EFFECT_STATUS4+"," +
									"last_operate_datetime='"+DateUtil.getNow()+"'", "id="+operId)){
						request.setAttribute("tip", "更新补货单时效状态失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}

				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS25);//下个阶段
				if(process==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(process2==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog!=null&&lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
					int effectTime=process.getEffectTime();//上阶段时效
					String lastOperateTime=lastLog.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
					}
				}

				CargoOperLogBean operLog=new CargoOperLogBean();
				operLog.setOperId(operId);
				operLog.setOperCode(cargoOperation.getCode());
				operLog.setOperName("作业复核");
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(status);
				operLog.setRemark(remark);
				operLog.setPreStatusName(process.getStatusName());
				operLog.setNextStatusName(process2.getStatusName());
				service.addCargoOperLog(operLog);

				service.getDbOp().commitTransaction();
			}catch (Exception e) {
				// TODO: handle exception
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
				if(stockLog.isErrorEnabled()){
					stockLog.error("checkRefillCargo error", e);
				}
				msg="操作失败";
			}finally{
				service.releaseAll();
			}
		}
		request.setAttribute("tip", msg);
		request.setAttribute("url", "cargoOperation.do?method=refillCargo&id="+operId);
		return mapping.findForward("tip");
	}

	/**
	 *	货位间调拨单列表
	 */
	public ActionForward exchangeCargoList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		request.setAttribute("user", user);
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService =new WareService(dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String operCode=StringUtil.convertNull(request.getParameter("operCode")).trim();//作业单编号
			String[] operStatus=request.getParameterValues("status");//作业单状态
			String productCode=StringUtil.convertNull(request.getParameter("productCode")).trim();//产品编号
			String cargoCode=StringUtil.convertNull(request.getParameter("cargoCode")).trim();//货位号

			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			}
			PagingBean paging = new PagingBean(pageIndex, 0,countPerPage);
			String para=(operCode.equals("")?"":("&operCode="+operCode))
			+(productCode.equals("")?"":("&productCode="+productCode))
			+(cargoCode.equals("")?"":("&cargoCode="+cargoCode))
			+(operStatus!=null&&operStatus.length>=1?("&status="+operStatus[0]):"")
			+(operStatus!=null&&operStatus.length>=2?("&status="+operStatus[1]):"")
			+(operStatus!=null&&operStatus.length>=3?("&status="+operStatus[2]):"")
			+(operStatus!=null&&operStatus.length>=4?("&status="+operStatus[3]):"")
			;

			paging.setCurrentPageIndex(pageIndex);
			paging.setPrefixUrl("cargoOperation.do?method=exchangeCargoList"+para);

			String condition="type=3";
			if(!operCode.equals("")){
				condition+=" and code='";
				condition+=operCode;
				condition+="'";
			}
			if(!productCode.equals("")){
				voProduct product=wareService.getProduct(productCode);
				int productId=0;
				if(product!=null){
					productId=product.getId();
				}
				List cocList=service.getCargoOperationCargoList("product_id="+productId, -1, -1, "id asc");
				String condition2="(";
				for(int i=0;i<cocList.size();i++){
					CargoOperationCargoBean coc=(CargoOperationCargoBean)cocList.get(i);
					condition2+=coc.getOperId();
					if(i!=(cocList.size()-1)){
						condition2+=",";
					}
				}
				condition2+=")";
				if(!condition2.equals("()")){
					condition+=" and id in ";
					condition+=condition2;
				}else{
					condition+=" and id=0";
				}
			}

			if(!cargoCode.equals("")){
				List cocList=service.getCargoOperationCargoList("in_cargo_whole_code='"+cargoCode+"' or out_cargo_whole_code='"+cargoCode+"'", -1, -1, "id asc");
				String condition2="(";
				for(int i=0;i<cocList.size();i++){
					CargoOperationCargoBean coc=(CargoOperationCargoBean)cocList.get(i);
					condition2+=coc.getOperId();
					if(i!=(cocList.size()-1)){
						condition2+=",";
					}
				}
				condition2+=")";
				if(!condition2.equals("()")){
					condition+=" and id in ";
					condition+=condition2;
				}else{
					condition+=" and id=0";
				}
			}
			if(operStatus!=null&&operStatus.length>0){
				String condition2="(";
				for(int i=0;i<operStatus.length;i++){
					if(operStatus[i].equals("30")){
						condition2+="30,31,32,33";
					}else{
						condition2+=operStatus[i];
					}
					if(i!=(operStatus.length-1)){
						condition2+=",";
					}
				}
				condition2+=")";
				if(!condition.equals("()")){
					condition+=" and status in ";
					condition+=condition2;
				}
			}

			//所有符合条件的作业单
			List list=service.getCargoOperationList(condition, paging.getCurrentPageIndex() * countPerPage,countPerPage, "id desc");
			Iterator itr= list.iterator(); 
			while(itr.hasNext()){
				CargoOperationBean bean = (CargoOperationBean)itr.next();
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+bean.getStatus());
				if(process!=null){
					bean.setStatusName(process.getStatusName());
				}else{
					bean.setStatusName("");
				}
			}

			List list2 = service.getCargoOperationList(condition, -1, -1, "id asc");
			int totalCount = list2.size(); //根据条件得到 总数量
			paging.setTotalCount(totalCount);
			paging.setTotalPageCount(list2.size()%countPerPage==0?list2.size()/countPerPage:list2.size()/countPerPage+1);
			request.setAttribute("paging", paging);
			request.setAttribute("list", list);
			request.setAttribute("para", para);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}

		return mapping.findForward("exchangeCargoList");
	}

	/**
	 *	添加新的调拨单(所有货位列表)
	 */
	public ActionForward addExchangeCargoList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation db=new DbOperation();
		db.init("adult_slave");
		WareService wareService = new WareService(db);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String condition = "ci.status=0 and cps.id!=0 and ci.stock_type=0 and cps.stock_count!=0 and ci.store_type!=2";
			String cargoCode=StringUtil.convertNull(request.getParameter("cargoCode")).trim();//货位编号
			StringBuilder para=new StringBuilder();//分页参数
			String storeType=StringUtil.convertNull(request.getParameter("storeType"));//货位类型
			String productCode=StringUtil.convertNull(request.getParameter("productCode")).trim();//产品编号
			String stockCount1=StringUtil.convertNull(request.getParameter("stockCount1")).trim();//最小库存
			String stockCount2=StringUtil.convertNull(request.getParameter("stockCount2")).trim();//最大库存
			String area=StringUtil.convertNull(request.getParameter("area")).trim();//库地区
			if(area.equals("")){
				area="-1";
			}
			
			condition+=" and ci.area_id="+area;
			para.append("&area=");
			para.append(area);
			
			if(!cargoCode.equals("")){
				condition+=" and ci.whole_code like '";
				condition+=cargoCode;
				condition+="%'";
				para.append("&cargoCode=");
				para.append(cargoCode);
			}
			if(!storeType.equals("")){
				condition+=" and ci.store_type=";
				condition+=storeType;
				para.append("&storeType=");
				para.append(storeType);
			}else{
				condition+=" and ci.store_type in (0,1,4)";
			}
			if(!productCode.equals("")){
				voProduct product=wareService.getProduct(productCode);
				int productId=0;
				if(product!=null){
					productId=wareService.getProduct(productCode).getId();
				}
				condition+=" and cps.product_id=";
				condition+=productId;
				para.append("&productCode=");
				para.append(productCode);
			}
			if(!stockCount1.equals("")){
				condition+=" and cps.stock_count>=";
				condition+=stockCount1;
				para.append("&stockCount1=");
				para.append(stockCount1);
			}
			if(!stockCount2.equals("")){
				condition+=" and cps.stock_count<=";
				condition+=stockCount2;
				para.append("&stockCount2=");
				para.append(stockCount2);
			}
			int countPerPage = 20;
			int totalCount = service.getCargoAndProductStockCount(condition); //根据条件得到 总数量
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
			List list = service.getCargoAndProductStockList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "ci.whole_code desc");
			Iterator iter = list.listIterator();
			List canCheckList=new ArrayList();//能否选择
			List operCountList=new ArrayList();//未完成作业单数
			while(iter.hasNext()){
				CargoProductStockBean cps = (CargoProductStockBean)iter.next();
				voProduct product = wareService.getProduct(cps.getProductId());
				voProductLine productLine = wareService.getProductLine("product_line.id = "+cps.getCargoInfo().getProductLineId());
				cps.setProduct(product);
				cps.getCargoInfo().setProductLine(productLine);

				//库存信息
				List cargoPSList = service.getCargoAndProductStockList("cps.product_id = "+product.getId(), -1, -1, "ci.id asc");
				product.setCargoPSList(cargoPSList);

				boolean canCheck=true;
				canCheckList.add(canCheck+"");

				int count=0;
				operCountList.add(""+count);
			}

			paging.setPrefixUrl("cargoOperation.do?method=addExchangeCargoList"+para.toString());
			request.setAttribute("list", list);
			request.setAttribute("paging", paging);
			request.setAttribute("canCheckList", canCheckList);
			request.setAttribute("operCountList", operCountList);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}

		return mapping.findForward("addExchangeCargo");
	}

	/**
	 *	添加调拨单
	 */
	public ActionForward addExchangeCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int cargoOperId = 0;
		synchronized(cargoLock){

			String[] cargoProducStockIds = request.getParameterValues("cargoProducStockId");

			if(cargoProducStockIds == null || cargoProducStockIds.length == 0){
				request.setAttribute("tip", "请选择货位，再生成补货单！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}

			//新作业单编号：HW1103110001
			String code = "HWD"+DateUtil.getNow().substring(2,10).replace("-", "");   
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			ConsignmentService consignmentService = new ConsignmentService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
				service.getDbOp().startTransaction();
				String storageCode = wareService.getString("whole_code", "cargo_info", "id = (select cargo_id from cargo_product_stock where id = "+cargoProducStockIds[0]+")");
				storageCode = storageCode.substring(0,storageCode.indexOf("-"));
				CargoInfoStorageBean storage=service.getCargoInfoStorage("whole_code='"+storageCode+"'");
				if(storage==null){
					request.setAttribute("tip", "地区错误！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
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
				cargoOper.setStockInType(CargoInfoBean.STORE_TYPE0);
				cargoOper.setStockOutType(CargoInfoBean.STORE_TYPE0);
				cargoOper.setStorageCode(storageCode);
				cargoOper.setType(CargoOperationBean.TYPE3);
				cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS28);
				cargoOper.setLastOperateDatetime(DateUtil.getNow());
				cargoOper.setStockOutArea(storage.getAreaId());
				cargoOper.setStockInArea(storage.getAreaId());
				if(!service.addCargoOperation(cargoOper)){
					request.setAttribute("tip", "添加补货单失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}//添加cargo_operation

				cargoOperId = service.getDbOp().getLastInsertId();
				for(int i=0;i<cargoProducStockIds.length;i++){//添加cargo_operation_cargo
					String cpsId = cargoProducStockIds[i];
					CargoProductStockBean bean = service.getCargoProductStock("id = "+cpsId);
					if(bean==null){
						request.setAttribute("tip", "库存错误，无法添加调拨单！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}

					CargoInfoBean ci=service.getCargoInfo("id="+bean.getCargoId());
					CargoOperationCargoBean coc = new CargoOperationCargoBean();
					voProduct product=wareService.getProduct(bean.getProductId());
					coc.setOperId(cargoOperId);
					coc.setInCargoProductStockId(0);
					coc.setProductId(bean.getProductId());
					coc.setType(1);
					coc.setOutCargoProductStockId(Integer.parseInt(cargoProducStockIds[i]));
					coc.setOutCargoWholeCode(ci.getWholeCode());
					coc.setStockCount(bean.getStockLockCount());
					if(!service.addCargoOperationCargo(coc)){
						request.setAttribute("tip", "添加补货单详细信息失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
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
					logRemark.append(ci.getWholeCode());
					logRemark.append("）");
					
					//分配目的货位
					CargoInfoBean inCi = null;
					if(ci.getStoreType() == CargoInfoBean.STORE_TYPE0||ci.getStoreType() == CargoInfoBean.STORE_TYPE4){
						//----初步看来决定目的货位的就是这一块儿。。。
						//加上判断 如果是 快销商品的话，就单独的分配逻辑
						List inCpsList = new ArrayList();
						if( consignmentService.isProductConsignment(coc.getProductId()) ) {
							inCpsList = service.getCargoAndProductStockWithStockAreaCodeRestrictList("ci.area_id="+ci.getAreaId()+" and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="+ci.getStoreType()+" and ci.space_lock_count=0 and cps.product_id="+coc.getProductId(), -1, -1, null,Constants.CONSIGNMENT_STOCK_AREA);
						} else {
							
							inCpsList=service.getCargoAndProductStockList("ci.area_id="+ci.getAreaId()+" and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="+ci.getStoreType()+" and ci.space_lock_count=0 and cps.product_id="+coc.getProductId(), -1, -1, null);
						}
						if(inCpsList.size()>0){
							CargoProductStockBean cps=(CargoProductStockBean)inCpsList.get(0);
							CargoInfoBean cargoInfo=cps.getCargoInfo();
							inCi=cargoInfo;
						}
						if(inCi==null){//查询一个未使用的货位
							//加上判断 如果是 快销商品的话，就单独的分配逻辑
							CargoInfoBean tempInCi = null;
							if( consignmentService.isProductConsignment(coc.getProductId()) ) {
								tempInCi = service.getCargoInfoWithStockAreaCodeRestrict("ci.area_id="+ci.getAreaId()+" and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="+ci.getStoreType()+" and ci.status=1", Constants.CONSIGNMENT_STOCK_AREA);
							} else {
								tempInCi=service.getCargoInfo("area_id="+ci.getAreaId()+" and stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and store_type="+ci.getStoreType()+" and status=1");
							}
							if(tempInCi!=null){
								if(!service.updateCargoInfo("status=0", "id="+tempInCi.getId())){
									request.setAttribute("tip", "数据库操作失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								CargoProductStockBean newCps=new CargoProductStockBean();
								newCps.setCargoId(tempInCi.getId());
								newCps.setProductId(coc.getProductId());
								newCps.setStockCount(0);
								newCps.setStockLockCount(0);
								if(!service.addCargoProductStock(newCps)){
									request.setAttribute("tip", "数据库操作失败！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								inCi=tempInCi;
							}
						}
					}else if(ci.getStoreType() == CargoInfoBean.STORE_TYPE1){
						//----初步看来决定目的货位的就是这一块儿。。。
						//加上判断 如果是 快销商品的话，就单独的分配逻辑
						List inCpsList = new ArrayList();
						if( consignmentService.isProductConsignment(coc.getProductId()) ) {
							inCpsList = service.getCargoAndProductStockWithStockAreaCodeRestrictList("ci.area_id="+ci.getAreaId()+" and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="+ci.getStoreType()+" and ci.space_lock_count=0 and cps.product_id="+coc.getProductId(),-1,-1,null, Constants.CONSIGNMENT_STOCK_AREA);
						} else{
							inCpsList=service.getCargoAndProductStockList("ci.area_id="+ci.getAreaId()+" and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="+ci.getStoreType()+" and ci.space_lock_count=0 and cps.product_id="+coc.getProductId(),-1,-1,null);
						}
						if(inCpsList.size()>0){
							CargoProductStockBean cps=(CargoProductStockBean)inCpsList.get(i);
							CargoInfoBean cargoInfo=cps.getCargoInfo();
							inCi=cargoInfo;
						}
						if(inCi==null){ //查询非此SKU整件区货位
							//加上判断 如果是 快销商品的话，就单独的分配逻辑
							if( consignmentService.isProductConsignment(coc.getProductId()) ) {
								inCpsList = service.getCargoAndProductStockWithStockAreaCodeRestrictList("ci.area_id="+ci.getAreaId()+" and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="+ci.getStoreType()+" and ci.space_lock_count=0",-1,-1,null, Constants.CONSIGNMENT_STOCK_AREA);
							} else {
								inCpsList=service.getCargoAndProductStockList("ci.area_id="+ci.getAreaId()+" and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="+ci.getStoreType()+" and ci.space_lock_count=0",-1,-1,null);
							}
							if(inCpsList.size()>0){
								CargoProductStockBean cps=(CargoProductStockBean)inCpsList.get(i);
								CargoInfoBean cargoInfo=cps.getCargoInfo();
								inCi=cargoInfo;
								if(cargoInfo.getStatus()!=0){
									if(!service.updateCargoInfo("status=0", "id="+cargoInfo.getId())){
										request.setAttribute("tip", "数据库操作失败！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
								}
							}
						}
					}
					if( inCi == null ) {
						request.setAttribute("tip", "分配目的货位失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					CargoOperationCargoBean inCoc=new CargoOperationCargoBean();
					inCoc.setOperId(cargoOperId);
					inCoc.setProductId(coc.getProductId());
					inCoc.setOutCargoProductStockId(bean.getId());
					inCoc.setOutCargoWholeCode(ci.getWholeCode());
					inCoc.setStockCount(coc.getStockCount());
					inCoc.setType(0);
					inCoc.setUseStatus(1);
					CargoProductStockBean inCps=service.getCargoProductStock("cargo_id="+inCi.getId()+" and product_id="+coc.getProductId());
					if(inCps==null){//如果该货位没有库存记录，则添加新的库存记录
						inCps=new CargoProductStockBean();
						inCps.setCargoId(inCi.getId());
						inCps.setProductId(coc.getProductId());
						inCps.setStockCount(0);
						inCps.setStockLockCount(0);
						service.addCargoProductStock(inCps);
						inCps.setId(service.getDbOp().getLastInsertId());
					}
					inCoc.setInCargoProductStockId(inCps.getId());
					inCoc.setInCargoWholeCode(inCi.getWholeCode());
					if(!service.addCargoOperationCargo(inCoc)){
						request.setAttribute("tip", "数据库操作失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}//添加目的货位记录
					
					logRemark.append("，");
					logRemark.append("目的货位（");
					logRemark.append(inCi.getWholeCode());
					logRemark.append("）");
					

					logBean.setRemark(logRemark.toString());
					service.addCargoOperationLog(logBean);
				}

				CargoOperLogBean operLog=new CargoOperLogBean();//员工操作日志
				operLog.setOperId(service.getCargoOperation("code='"+cargoOper.getCode()+"'").getId());
				operLog.setOperCode(cargoOper.getCode());
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS28);
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
					request.setAttribute("tip", "添加日志数据时发生异常！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				service.getDbOp().commitTransaction();

			}catch (Exception e) {
				if(stockLog.isErrorEnabled()){
					stockLog.error("addExchangeCargo error",e);
				}
				service.getDbOp().rollbackTransaction();
				request.setAttribute("tip", "系统异常，请联系管理员！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}finally{
				service.releaseAll();
			}
		}
		return new ActionForward("/admin/cargoOperation.do?method=exchangeCargo&cargoOperId="+cargoOperId);
	}


	/**
	 *	调拨单详细页
	 */
	public ActionForward exchangeCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		//文齐辉添加打印时跳转到打印页
		String printFlag=request.getParameter("flag");//打印flag
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String cargoOperId=request.getParameter("cargoOperId");//作业单Id
			CargoOperationBean coBean=service.getCargoOperation("id="+cargoOperId);
			if(coBean == null){
				request.setAttribute("tip", "该调拨单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			List inCocList=new ArrayList();
			List outCocList=new ArrayList();
			List cargoOperationCargoList=service.getCargoOperationCargoList("oper_id="+cargoOperId+" and in_cargo_product_stock_id=0", -1, -1, "id asc");//源货位作业单信息表
			for(int i=0;i<cargoOperationCargoList.size();i++){
				CargoOperationCargoBean outCocBean=(CargoOperationCargoBean)cargoOperationCargoList.get(i);//源货位cocBean
				int outCpsId=outCocBean.getOutCargoProductStockId();//源货位cpsId
				CargoProductStockBean outCpsBean=(CargoProductStockBean)service.getCargoProductStock("id="+outCpsId);
				CargoInfoBean outBean=service.getCargoInfo("whole_code='"+outCocBean.getOutCargoWholeCode()+"'");
				voProduct vo=wareService.getProduct(outCocBean.getProductId());
				outCocBean.setCargoInfo(outBean);
				outCocBean.setProduct(vo);
				if(outCpsBean!=null){
					outCocBean.setCargoProductStock(outCpsBean);
				}else{
					outCocBean.setCargoProductStock(new CargoProductStockBean());
				}
				outCocList.add(outCocBean);
				//与源货位匹配的目的货位作业单信息表
				List cocList=service.getCargoOperationCargoList("out_cargo_product_stock_id="+outCpsId+" and oper_id="+cargoOperId+" and type=0", -1, -1, "id asc");
				List inCocList2=new ArrayList();
				for(int j=0;j<cocList.size();j++){
					CargoOperationCargoBean inCocBean=(CargoOperationCargoBean)cocList.get(j);
					int inCpsId=inCocBean.getInCargoProductStockId();//目的货位cpsId
					CargoProductStockBean cpsBean=service.getCargoProductStock("id="+inCpsId);
					CargoInfoBean ci=service.getCargoInfo("whole_code='"+inCocBean.getInCargoWholeCode()+"'");
					voProduct product=wareService.getProduct(inCocBean.getProductId());
					inCocBean.setCargoInfo(ci);
					inCocBean.setProduct(product);
					if(cpsBean!=null){
						inCocBean.setCargoProductStock(cpsBean);
					}else{
						inCocBean.setCargoProductStock(new CargoProductStockBean());
					}
					inCocList2.add(inCocBean);
					if(j==0){
						CargoInfoAreaBean area = service.getCargoInfoArea("id = "+ci.getAreaId());
						if (area == null) {
							request.setAttribute("areaName", "");
						} else {
							request.setAttribute("areaName", area.getName());
						}
						request.setAttribute("stockTypeName", ci.getStockTypeName());
					}
				}
				inCocList.add(inCocList2);
			}

			CargoOperationProcessBean process=service.getCargoOperationProcess("id="+coBean.getStatus());
			if(process!=null){
				coBean.setStatusName(process.getStatusName());
			}
			CargoOperationProcessBean nextStatus=null;//下一个状态，传参用
			CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+(coBean.getStatus()+1));//下一个状态
			CargoOperationProcessBean complete=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS34);//完成作业单状态
			if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS29
					||coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS30
					||coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS31
					||coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS32){
				if(process2.getUseStatus()==1){
					nextStatus=process2;
				}else{
					nextStatus=complete;
				}
			}
			if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS33){
				nextStatus=complete;
			}
			request.setAttribute("nextStatus", nextStatus);
			String effectTime="";
			Calendar cal1=Calendar.getInstance();
			if(coBean.getLastOperateDatetime()==null){
				coBean.setLastOperateDatetime(coBean.getCreateDatetime());
			}
			cal1.setTime(DateUtil.parseDate(coBean.getLastOperateDatetime(), "yyyy-MM-dd HH:mm:ss"));
			Calendar cal2=Calendar.getInstance();
			if(process!=null){
				cal1.add(Calendar.MINUTE, process.getEffectTime());
			}
			if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS34){
				effectTime="待复核";
			}else if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS35){
				effectTime="作业成功";
			}else if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS36){
				effectTime="作业失败";
			}else{
				if(cal1.before(cal2)){
					effectTime="已超时";
				}else{
					effectTime="进行中";
				}
			}
			request.setAttribute("effectTime", effectTime);

			request.setAttribute("inCocList", inCocList);
			request.setAttribute("outCocList", outCocList);
			request.setAttribute("coBean", coBean);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		if("print".equals(printFlag))
			return mapping.findForward("exchangeCargoPrint");
		return mapping.findForward("exchangeCargo");
	}

	/**
	 *	调拨单保存编辑
	 */
	public ActionForward editExchangeCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("operId"));//调拨单id
		String action = StringUtil.convertNull(request.getParameter("action"));
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			ConsignmentService consignmentService = new ConsignmentService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);//调拨单bean
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS28){
					request.setAttribute("tip", "该作业单已确认，无法编辑！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				List areaList=CargoDeptAreaService.getCargoDeptAreaList(request);
				service.getDbOp().startTransaction();
				//只有源货位的信息列表
				List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1 and in_cargo_product_stock_id=0", -1, -1, "id asc");
				if(outCocList.size()==0){
					request.setAttribute("tip", "单据里没有任何源货位信息，不能提交！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				for(int i=0;i<outCocList.size();i++){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);//只有源货位的cocBean
					String[] checkedCocIds = request.getParameterValues("cocId"+outCoc.getId());//传入的目的货位CpsId列表
					if(checkedCocIds==null){
						request.setAttribute("tip", "有产品未选择目的货位信息，不能提交！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					CargoInfoBean ci=service.getCargoInfo("whole_code='"+outCoc.getOutCargoWholeCode()+"'");
					if (ci == null) {
						request.setAttribute("tip", "货位不存在！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(!areaList.contains(ci.getAreaId()+"")){
						request.setAttribute("tip", "只能操作本地区作业单！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}

				for(int i=0;i<outCocList.size();i++){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);//只有源货位的cocBean
					voProduct product=wareService.getProduct(outCoc.getProductId());
					CargoInfoBean outCiBean=service.getCargoInfo("whole_code='"+outCoc.getOutCargoWholeCode()+"'");//源货位
					CargoOperationLogBean logBean=new CargoOperationLogBean();//操作人员记录
					logBean.setOperId(operId);
					logBean.setOperDatetime(DateUtil.getNow());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					StringBuilder logRemark=new StringBuilder("保存编辑：");
					logRemark.append("商品");
					logRemark.append(product.getCode());
					logRemark.append("，");
					logRemark.append("源货位（");
					logRemark.append(outCiBean.getWholeCode());
					logRemark.append("），");
					//该作业单，该源货位，所有cocId列表
					List inCocIds = service.getFieldList("id", "cargo_operation_cargo", 
							"oper_id = "+operId+" and type = 0 and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()
							+" and in_cargo_product_stock_id!=0", -1, -1, null, null, "String");
					String[] checkedCocIds = request.getParameterValues("cocId"+outCoc.getId());//传入的目的货位CocId列表
					if(checkedCocIds!=null){
						for(int j=0;j<checkedCocIds.length;j++){
							int exchangeCount = StringUtil.StringToId(request.getParameter("exchangeCount"+checkedCocIds[j]));
							if(exchangeCount==0){
								request.setAttribute("tip", "作业量输入错误，只能输入大于0的整数");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}

						for(int j=0;j<checkedCocIds.length;j++){
							String cocId=checkedCocIds[j];//目的货位cocId
							CargoOperationCargoBean cocBean=service.getCargoOperationCargo("id="+cocId);
							CargoInfoBean inCiBean=service.getCargoInfo("whole_code='"+cocBean.getInCargoWholeCode()+"'");//目的货位
							if( consignmentService.isProductConsignment(cocBean.getProductId()) ) {
								if( !consignmentService.isCargoStockAreaCodeSub(inCiBean.getId(), Constants.CONSIGNMENT_STOCK_AREA) ) {
									request.setAttribute("tip", "商品"+product.getCode()+"为快销商品，货位"+cocBean.getInCargoWholeCode()+"不是"+Constants.CONSIGNMENT_STOCK_AREA+"区的货位！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							}
							CargoProductStockBean cpsBean=service.getCargoProductStock("id="+cocBean.getInCargoProductStockId());
							if(cpsBean==null){//库存信息改变，散件区
								request.setAttribute("tip", "商品"+product.getCode()+"货位"+cocBean.getInCargoWholeCode()+"已被清空或被其他商品占用，操作失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							int exchangeCount = StringUtil.StringToId(request.getParameter("exchangeCount"+cocId));

							//该货位可上架量
							int count=inCiBean.getMaxStockCount()-inCiBean.getSpaceLockCount()-cpsBean.getStockCount()-cpsBean.getStockLockCount();
							if((inCiBean.getStoreType()==CargoInfoBean.STORE_TYPE0||inCiBean.getStoreType()==CargoInfoBean.STORE_TYPE4)&&exchangeCount>count){
								request.setAttribute("tip", product.getOriname()+"产品的目的货位"+inCiBean.getWholeCode()+"容量已满，请重新分配货位！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							logRemark.append("目的货位（");
							logRemark.append(inCiBean.getWholeCode());
							logRemark.append("），");
							logRemark.append("上架量（");
							logRemark.append(cocBean.getStockCount());
							logRemark.append("-");
							logRemark.append(exchangeCount);
							logRemark.append("）");
							if(j!=checkedCocIds.length-1){
								logRemark.append("，");
							}
							if(!service.updateCargoOperationCargo("stock_count = "+exchangeCount+",use_status = 1", "id = "+cocId)){
								request.setAttribute("tip", "更新货位调拨单调拨数量失败！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							CargoOperationCargoBean checkedCoc = service.getCargoOperationCargo("id = "+cocId);
							inCocIds.remove(String.valueOf(checkedCoc.getId()));
						}
					}
					logBean.setRemark(logRemark.toString());
					service.addCargoOperationLog(logBean);
					StringBuilder errMsg = new StringBuilder();
					for(int j=0;j<inCocIds.size();j++){
						CargoOperationCargoBean inCoc = service.getCargoOperationCargo("id = "+inCocIds.get(j));
						if(!service.updateCargoOperationCargo("use_status = 0", "out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and in_cargo_product_stock_id = "+inCoc.getInCargoProductStockId()+" and oper_id = "+cargoOperation.getId())){
							errMsg.append("数据库操作异常！");
							service.getDbOp().rollbackTransaction();
							break;
						}
					}
					if(errMsg.length()>0){
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}

				request.setAttribute("success", "1");//保存编辑成功
				service.getDbOp().commitTransaction();
			}catch (Exception e) {
				if(stockLog.isErrorEnabled()){
					stockLog.error("editExchangeCargo error",e);
				}
				request.setAttribute("tip", "系统异常，请联系管理员！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}finally{
				service.releaseAll();
			}

			if(action.equals("confirm")){
				return confirmExchangeCargo(mapping, form, request, response);
			}else{
				return new ActionForward("/admin/cargoOperation.do?method=exchangeCargo&cargoOperId="+operId+"&action=success");
			}
		}
	}
	/**
	 *	分配货位
	 */
	public ActionForward usefulCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		int countPerPage = 20;
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String operId=StringUtil.convertNull(request.getParameter("operId"));//作业单Id
			String outCpsId=StringUtil.convertNull(request.getParameter("outCpsId"));//源货位cpsId

			String otherPagePass = StringUtil.convertNull(request.getParameter("otherPagePass")); //如果是从下架单过的 只显示整件区数据

			String cargoCode=StringUtil.convertNull(request.getParameter("cargoCode"));//货位号
			String mode=StringUtil.convertNull(request.getParameter("mode"));//查询条件，0为精确查询，1为左精确右模糊查询
			String status=StringUtil.convertNull(request.getParameter("status"));//货位状态
			String storageId=StringUtil.convertNull(request.getParameter("storageId"));//仓库代号
			String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));//所属区域
			String shelfId=StringUtil.convertNull(request.getParameter("shelfId"));//货架代号
			String floorNum=StringUtil.convertNull(request.getParameter("floorNum"));//第几层
			String storeType=StringUtil.convertNull(request.getParameter("storeType"));//存放类型
			String productLineId=StringUtil.convertNull(request.getParameter("productLineId"));//产品线Id
			String type=StringUtil.convertNull(request.getParameter("type"));//货位类型
			String minMaxStockCount=StringUtil.convertNull(request.getParameter("minMaxStockCount"));//最小货位最大容量
			String maxMaxStockCount=StringUtil.convertNull(request.getParameter("maxMaxStockCount"));//最大货位最大容量
			String otherCargo=StringUtil.convertNull(request.getParameter("otherCargo"));//其他货位
			StringBuilder condition= new StringBuilder(" stock_type=0 ");
			StringBuilder paramBuf = new StringBuilder("&operId="+operId+"&outCpsId="+outCpsId+"&otherPagePass="+otherPagePass+"&mode="+mode); 
			if(!cargoCode.equals("")){
				if(mode.equals("0")){
					condition.append(" and whole_code='");
					condition.append(cargoCode);
					condition.append("'");
				}else if(mode.equals("1")){
					condition.append(" and whole_code like '");
					condition.append(cargoCode);
					condition.append("%'");
				}
				paramBuf.append("&whole_code="+cargoCode);
			}
			if((!status.equals(""))&&(!status.equals("2"))){

				condition.append(" and status=");
				condition.append(status);
				paramBuf.append("&status="+status); 
			}else{
				if(otherPagePass.equals("upShelf") || otherPagePass.equals("downShelf")){

					condition.append(" and status in(0,1)"); 
				} 
			}
			if(!storageId.equals("")){
				condition.append(" and storage_id=");
				condition.append(storageId);
				paramBuf.append("&storageId="+storageId); 
			}
			if(!stockAreaId.equals("")){
				condition.append(" and stock_area_id=");
				condition.append(stockAreaId);
				paramBuf.append("&stockAreaId="+stockAreaId); 
			}
			if(!shelfId.equals("")){
				condition.append(" and shelf_id=");
				condition.append(shelfId);
				paramBuf.append("&shelfId="+shelfId); 
			}
			if(!floorNum.equals("")){
				condition.append(" and floor_num=");
				condition.append(floorNum);
				paramBuf.append("&floorNum="+floorNum); 
			}
			if(!storeType.equals("")){
				if(otherPagePass.equals("downShelf")){
					condition.append(" and store_type=1");
					storeType="1";
				}else{
					condition.append(" and store_type=");
					condition.append(storeType);
				}
				paramBuf.append("&storeType="+storeType); 
			}else{
				if(otherPagePass.equals("upShelf")){
					condition.append(" and store_type in");
					condition.append("(0,1,4)");
				}else if(otherPagePass.equals("downShelf")){
					condition.append(" and store_type=1");
				} 
			}
			if(!productLineId.equals("")){
				condition.append(" and product_line_id=");
				condition.append(productLineId);
				paramBuf.append("&productLineId="+productLineId); 
			}
			if(!type.equals("")){
				condition.append(" and type=");
				condition.append(type);
				paramBuf.append("&type="+type); 
			}
			if(!minMaxStockCount.equals("")){
				condition.append(" and max_stock_count>=");
				condition.append(minMaxStockCount);
				paramBuf.append("&minMaxStockCount="+minMaxStockCount); 
			}
			if(!maxMaxStockCount.equals("")){
				condition.append(" and max_stock_count<=");
				condition.append(maxMaxStockCount);
				paramBuf.append("&maxMaxStockCount="+maxMaxStockCount); 
			}
			CargoOperationBean opBean=service.getCargoOperation("id="+operId);
			CargoProductStockBean outCpsBean=service.getCargoProductStock("id="+StringUtil.StringToId(outCpsId));//原货位cps
			if(outCpsBean==null ){
				request.setAttribute("tip", "原货位数据受损.");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int outCargoId=0;
			int productId=0;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			outCargoId=outCpsBean.getCargoId();
			productId=outCpsBean.getProductId();
			voProduct product = wareService.getProduct(productId);
			voProductLine productLine = wareService.getProductLine("product_line_catalog.catalog_id in ("+product.getParentId1()+","+product.getParentId2()+")");
			CargoInfoBean outCiBean=service.getCargoInfo("id="+outCpsBean.getCargoId());//原货位 

			String upShelfQuery = "";
			if(otherCargo.equals("1")){
				//符合条件的cps列表
				List tempCpsList=service.getCargoAndProductStockList("ci.status=0 and ci.store_type="+opBean.getStockInType()+" and ci.stock_type=0"+" and cps.product_id="+outCpsBean.getProductId()+" and ci.whole_code like '"+opBean.getStorageCode()+"%'", -1, -1, null);
				upShelfQuery="id in (";
				for(int i=0;i<tempCpsList.size();i++){
					CargoProductStockBean tempCps=(CargoProductStockBean)tempCpsList.get(i);
					upShelfQuery+=tempCps.getCargoId();
					upShelfQuery+=",";
				}
				upShelfQuery=upShelfQuery.substring(0,upShelfQuery.length()-1);
				upShelfQuery+=")";
				//已添加的coc列表
				List tempCocList=service.getCargoOperationCargoList("oper_id="+operId+" and out_cargo_product_stock_id="+outCpsId, -1, -1, null);
				upShelfQuery+=" and whole_code not in (";
				for(int i=0;i<tempCocList.size();i++){
					CargoOperationCargoBean coc=(CargoOperationCargoBean)tempCocList.get(i);
					upShelfQuery+="'";
					upShelfQuery+=coc.getInCargoWholeCode();
					upShelfQuery+="',";
				}
				upShelfQuery=upShelfQuery.substring(0,upShelfQuery.length()-1);
				upShelfQuery+=") and storage_id=";
				upShelfQuery+=storageId;
				upShelfQuery+=" and store_type=";
				upShelfQuery+=storeType;
			}else{
				upShelfQuery= "status=1 and store_type="+(storeType==""?"0":storeType)+" and stock_type=0 "+(productLine!=null?" and product_line_id="+productLine.getId():" ")+(storageId==""?"":(" and storage_id="+storageId));
			}
			String downShelfQuery = "store_type=1 and stock_type=0 and status <>2";
			List inCargoBeanList=new ArrayList();//目的货位列表

			int totalCount = 20;
			if(mode.equals("")){ //计算数据条目的总数 分别判断
				if(otherPagePass.equals("downShelf")){
					totalCount = service.getCargoInfoCount(downShelfQuery);
				}else if(otherPagePass.equals("upShelf")){
					totalCount=service.getCargoInfoCount(upShelfQuery);
				} 
			}else{
				totalCount = service.getCargoInfoCount(condition.toString()); //根据条件得到 总数量
			}
			PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);

			if(mode.equals("")){//点连接到页面，没有mode
				//List inCargoBeanList2 = new ArrayList();
				if(otherPagePass.equals("downShelf")){
					inCargoBeanList=service.getCargoInfoList(downShelfQuery, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id asc");
				}else if(otherPagePass.equals("upShelf")){
					if(otherCargo.equals("1")){
						inCargoBeanList = service.getCargoInfoList(upShelfQuery, paging.getCurrentPageIndex() * countPerPage, countPerPage, "whole_code asc");
					}else{
						inCargoBeanList = service.getCargoInfoList(upShelfQuery, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id asc");
					}
					request.setAttribute("firstStatus", otherCargo.equals("1")?"0":"1");
					request.setAttribute("firstProductLineId",String.valueOf(productLine==null?0:productLine.getId()));
					request.setAttribute("firstStoreType", otherCargo.equals("1")?opBean.getStockInType()+"":"0");
				} 
			}else{//点查询到页面，mode有值
				inCargoBeanList=service.getCargoInfoList(condition.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "id asc");
				for(int i=0;i<inCargoBeanList.size();i++){
					CargoInfoBean bean=(CargoInfoBean)inCargoBeanList.get(i);
					if(bean.getId()==outCargoId){
						inCargoBeanList.remove(inCargoBeanList.size()-1);
					}
				}
			}
			List inCpsList=new ArrayList();
			for(int i=0;i<inCargoBeanList.size();i++){//得到目的货位产品列表
				CargoInfoBean inCiBean=(CargoInfoBean)inCargoBeanList.get(i);
				List inCps=service.getCargoAndProductStockList("cargo_id="+inCiBean.getId(), -1, -1, null);
				for(int j=0;j<inCps.size();j++){
					CargoProductStockBean cpsBean=(CargoProductStockBean)inCps.get(j);
					voProduct inProduct=wareService.getProduct(cpsBean.getProductId());
					cpsBean.setProduct(inProduct);
				}
				inCpsList.add(inCps);
			}
			if(otherCargo.equals("1")){
				paging.setPrefixUrl("cargoOperation.do?method=usefulCargo"+paramBuf.toString()+"&otherCargo=1");
			}else{
				paging.setPrefixUrl("cargoOperation.do?method=usefulCargo"+paramBuf.toString());
			}

			List productLineList=wareService.getProductLineList("1=1 ");//所有产品线列表
			List storageList=service.getCargoInfoStorageList(storageId.equals("")?"1=1":"id="+storageId, -1, -1, null);
			request.setAttribute("inCargoBeanList", inCargoBeanList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("storageList", storageList);
			request.setAttribute("operId", operId);
			request.setAttribute("outCpsId", outCpsBean!=null?""+outCpsBean.getId():null);
			request.setAttribute("outCiBean", outCiBean);
			request.setAttribute("productId", ""+productId);
			request.setAttribute("paging", paging);
			request.setAttribute("inCpsList", inCpsList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("usefulCargo");
	}


	/**
	 *	调拨单分配货位
	 */
	public ActionForward exchangeUsefulCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		int countPerPage = 20;
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ConsignmentService consignmentService = new ConsignmentService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String operId=StringUtil.convertNull(request.getParameter("operId"));//作业单Id
			String outCpsId=StringUtil.convertNull(request.getParameter("outCpsId"));//源货位cpsId
			String cargoCode=StringUtil.convertNull(request.getParameter("cargoCode")).trim();//货位号
			String mode=StringUtil.convertNull(request.getParameter("mode"));//查询条件，0为精确查询，1为左精确右模糊查询
			String status=StringUtil.convertNull(request.getParameter("status"));//货位状态
			String storageId=StringUtil.convertNull(request.getParameter("storageId"));//仓库代号
			String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));//所属区域
			String shelfId=StringUtil.convertNull(request.getParameter("shelfId"));//货架代号
			String floorNum=StringUtil.convertNull(request.getParameter("floorNum"));//第几层
			String storeType=StringUtil.convertNull(request.getParameter("storeType"));//存放类型
			String productLineId=StringUtil.convertNull(request.getParameter("productLineId"));//产品线Id
			String type=StringUtil.convertNull(request.getParameter("type"));//货位类型
			String minMaxStockCount=StringUtil.convertNull(request.getParameter("minMaxStockCount")).trim();//最小货位最大容量
			String maxMaxStockCount=StringUtil.convertNull(request.getParameter("maxMaxStockCount")).trim();//最大货位最大容量

			String stockType=StringUtil.convertNull(request.getParameter("stockType"));//地区Id
			CargoOperationBean coBean=service.getCargoOperation("id="+operId);
			if(coBean==null){
				request.setAttribute("tip", "作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			StringBuilder condition= new StringBuilder("1=1 ");
			StringBuilder paramBuf = new StringBuilder("&operId="+operId+"&outCpsId="+outCpsId+"&mode="+mode); 
			if(!cargoCode.equals("")){
				if(mode.equals("0")){
					condition.append(" and whole_code='");
					condition.append(cargoCode);
					condition.append("'");
				}else if(mode.equals("1")){
					condition.append(" and whole_code like '");
					condition.append(cargoCode);
					condition.append("%'");
				}
				paramBuf.append("&whole_code="+cargoCode);
			}
			if((!status.equals(""))&&(!status.equals("2"))){
				condition.append(" and status=");
				condition.append(status);
				paramBuf.append("&status="+status); 
			}else{
				condition.append(" and status in(0,1)");
				paramBuf.append("&status=2");
			}
			if(!storageId.equals("")){
				condition.append(" and storage_id=");
				condition.append(storageId);
				paramBuf.append("&storageId="+storageId); 
			}
			if(!stockAreaId.equals("")){
				condition.append(" and stock_area_id=");
				condition.append(stockAreaId);
				paramBuf.append("&stockAreaId="+stockAreaId); 
			}
			if(!shelfId.equals("")){
				condition.append(" and shelf_id=");
				condition.append(shelfId);
				paramBuf.append("&shelfId="+shelfId); 
			}
			if(!floorNum.equals("")){
				condition.append(" and floor_num=");
				condition.append(floorNum);
				paramBuf.append("&floor_num="+floorNum); 
			}
			if(!storeType.equals("")){
				condition.append(" and store_type=");
				condition.append(storeType);
				paramBuf.append("&storeType="+storeType); 
			}
			if(!productLineId.equals("")){
				condition.append(" and product_line_id=");
				condition.append(productLineId);
				paramBuf.append("&productLineId="+productLineId); 
			}
			if(!type.equals("")){
				condition.append(" and type=");
				condition.append(type);
				paramBuf.append("&type="+type); 
			}
			if(!minMaxStockCount.equals("")){
				condition.append(" and max_stock_count>=");
				condition.append(minMaxStockCount);
				paramBuf.append("&minMaxStockCount="+minMaxStockCount); 
			}
			if(!maxMaxStockCount.equals("")){
				condition.append(" and max_stock_count<=");
				condition.append(maxMaxStockCount);
				paramBuf.append("&maxMaxStockCount="+maxMaxStockCount); 
			}
			if(!stockType.equals("")){
				condition.append(" and stock_type=");
				condition.append(stockType);
				paramBuf.append("&stockType="+stockType);
			}
			CargoProductStockBean outCpsBean=service.getCargoProductStock("id="+StringUtil.StringToId(outCpsId));//源货位cps
			int productId=0;
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));

			CargoInfoBean outCiBean=new CargoInfoBean();

			if(outCpsBean!=null){
				productId=outCpsBean.getProductId();
				outCiBean=service.getCargoInfo("id="+outCpsBean.getCargoId());
			}

			List inCargoBeanList=new ArrayList();//目的货位列表

			int totalCount = 20;

			if(mode.equals("")){ //计算数据条目的总数 分别判断
				if(outCiBean.getStoreType()==0||outCiBean.getStoreType()==4){//调拨单，散件区或混合区
					if( consignmentService.isProductConsignment(productId)) {
						totalCount=service.getCargoInfoWithStockAreaCodeRestrictCount("ci.status=1 and ci.store_type="+outCiBean.getStoreType()+" and ci.stock_type="+outCiBean.getStockType(), Constants.CONSIGNMENT_STOCK_AREA);
					} else {
						totalCount=service.getCargoInfoCount("status=1 and store_type="+outCiBean.getStoreType()+" and stock_type="+outCiBean.getStockType());
					}
				}else if(outCiBean.getStoreType()==1){//调拨单，整件区，不用判断相关调拨单
					if( consignmentService.isProductConsignment(productId)) {
						
						totalCount=service.getCargoInfoWithStockAreaCodeRestrictCount("ci.status in(0,1) and ci.store_type=1",Constants.CONSIGNMENT_STOCK_AREA);
					} else {
						totalCount=service.getCargoInfoCount("status in(0,1) and store_type=1");
					}
				}
			}else{
				totalCount = service.getCargoInfoCount(condition.toString()); //根据条件得到 总数量
			}

			PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
			if(mode.equals("")){//点连接到页面，没有mode
				List inCargoBeanList2 = new ArrayList();
				if(request.getParameter("otherCargo")!=null&&request.getParameter("otherCargo").equals("1")){//点击其他货位链接而来
					int stockInType=coBean.getStockInType();//目的货位存放类型
					String storageCode=coBean.getStorageCode();//目的货位仓库代号
					List tempCpsList = new ArrayList();
					if( consignmentService.isProductConsignment(productId) ) {
						tempCpsList=service.getCargoAndProductStockWithStockAreaCodeRestrictList("ci.store_type="+stockInType+" and ci.whole_code like '"+storageCode+"%' and cps.product_id="+outCpsBean.getProductId(), -1, -1, null, Constants.CONSIGNMENT_STOCK_AREA);
					} else {
						tempCpsList=service.getCargoAndProductStockList("ci.store_type="+stockInType+" and ci.whole_code like '"+storageCode+"%' and cps.product_id="+outCpsBean.getProductId(), -1, -1, null);
					}
					for(int i=0;i<tempCpsList.size();i++){
						CargoProductStockBean tempCps=(CargoProductStockBean)tempCpsList.get(i);
						inCargoBeanList.add(tempCps.getCargoInfo());
					}
				}else{
					if(outCiBean.getStoreType()==0||outCiBean.getStoreType()==4){//调拨单，散件区
						if( consignmentService.isProductConsignment(productId)) {
							inCargoBeanList2=service.getCargoInfoWithStockAreaCodeRestrictList("ci.status=1 and ci.store_type="+outCiBean.getStoreType()+" and ci.stock_type="+outCiBean.getStockType(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "ci.whole_code asc", Constants.CONSIGNMENT_STOCK_AREA);
						} else {
							inCargoBeanList2=service.getCargoInfoList("status=1 and store_type="+outCiBean.getStoreType()+" and stock_type="+outCiBean.getStockType(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "whole_code asc");
						}
					}else if(outCiBean.getStoreType()==1){//调拨单，整件区
						if( consignmentService.isProductConsignment(productId)) {
							inCargoBeanList2=service.getCargoInfoWithStockAreaCodeRestrictList("ci.status in(0,1) and ci.store_type=1"+" and ci.stock_type="+outCiBean.getStockType(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "ci.whole_code asc", Constants.CONSIGNMENT_STOCK_AREA);
						} else {
							inCargoBeanList2=service.getCargoInfoList("status in(0,1) and store_type=1"+" and stock_type="+outCiBean.getStockType(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "whole_code asc");
						}
					}
					for(int i=0;i<inCargoBeanList2.size();i++){
						CargoInfoBean ciBean=(CargoInfoBean)inCargoBeanList2.get(i);
						inCargoBeanList.add(ciBean);
					}
				}
			}else{//点查询到页面，mode有值
				inCargoBeanList=service.getCargoInfoList(condition.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "whole_code asc");
			}
			List inCpsList=new ArrayList();
			for(int i=0;i<inCargoBeanList.size();i++){//得到目的货位产品列表
				CargoInfoBean inCiBean=(CargoInfoBean)inCargoBeanList.get(i);
				List inCps=service.getCargoAndProductStockList("cargo_id="+inCiBean.getId(), -1, -1, null);
				for(int j=0;j<inCps.size();j++){
					CargoProductStockBean cpsBean=(CargoProductStockBean)inCps.get(j);
					voProduct inProduct=wareService.getProduct(cpsBean.getProductId());
					cpsBean.setProduct(inProduct);
				}
				inCpsList.add(inCps);
			}
			paging.setPrefixUrl("cargoOperation.do?method=exchangeUsefulCargo"+paramBuf.toString());
			List productLineList=wareService.getProductLineList("1=1");//所有产品线列表
			List storageList=service.getCargoInfoStorageList("1=1", -1, -1, "id asc");//所有仓库列表
			request.setAttribute("inCargoBeanList", inCargoBeanList);
			request.setAttribute("productLineList", productLineList);
			request.setAttribute("storageList", storageList);
			request.setAttribute("operId", operId);
			request.setAttribute("outCpsId", outCpsBean!=null?""+outCpsBean.getId():null);
			request.setAttribute("productId", ""+productId);
			request.setAttribute("paging", paging);
			request.setAttribute("outCiBean", outCiBean);
			request.setAttribute("inCpsList", inCpsList);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("exchangeUsefulCargo");
	}

	/**
	 *	确定自动分配的货位
	 */
	public ActionForward submitUsefulCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(cargoLock){
			String otherPagePass = StringUtil.convertNull(request.getParameter("otherPagePass"));
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			ConsignmentService consignmentService = new ConsignmentService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			String cargoOperId="";
			try{
				service.getDbOp().startTransaction();
				cargoOperId=request.getParameter("operId");//作业单id
				String outCpsId=request.getParameter("outCpsId");//源货位cpsId
				String productId=StringUtil.convertNull(request.getParameter("productId"));//产品id
				voProduct product=wareService.getProduct(Integer.parseInt(productId));//产品
				String[] inCargoIdList=request.getParameterValues("inCargoId");//目的货位ciId列表

				Map cargoIdMap = new HashMap();
				CargoOperationBean operBean = service.getCargoOperation(" id="+cargoOperId);

				if(operBean==null || (operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS1 && operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS10 && operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS19 && operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS28)){
					request.setAttribute("tip", "该作业单状态已经改变。无法添加货位！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(productId.trim().equals("")){
					request.setAttribute("tip", "产品编号错误，请重新加载页面");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				CargoProductStockBean outCpsBean=service.getCargoProductStock("id="+outCpsId);//源货位cps
				CargoInfoBean outCiBean=new CargoInfoBean();
				if(outCpsBean!=null){
					outCiBean=service.getCargoInfo("id="+outCpsBean.getCargoId());//源货位ci
				}else{
					request.setAttribute("tip", "源货位库存出错！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				if(inCargoIdList.length==0){
					request.setAttribute("tip", "没有选择目的货位！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				if(otherPagePass.equals("upShelf")){
					List inCocList = service.getCargoOperationCargoList("oper_id = "+cargoOperId+" and type = 1", -1, -1, "id asc");
					for(int i=0;i<inCocList.size();i++){
						CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
						List outCocList = service.getCargoOperationCargoList("oper_id = "+cargoOperId+" and product_id="+productId+" and out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId()+" and type = 0 ", -1, -1, "id asc");
						for(int j=0;j<outCocList.size();j++){
							CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
							CargoProductStockBean temp_inCps = service.getCargoProductStock("id = "+outCoc.getInCargoProductStockId());
							if(temp_inCps!=null){
								CargoInfoBean outCi = service.getCargoInfo("id = "+temp_inCps.getCargoId());
								cargoIdMap.put(String.valueOf(outCi.getId()), outCi);
							}
						}
					}
				}else if(otherPagePass.equals("downShelf")){
					List inCocList = service.getCargoOperationCargoList("oper_id = "+cargoOperId+" and type = 0", -1, -1, "id asc");
					for(int i=0;i<inCocList.size();i++){
						CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
						List outCocList = service.getCargoOperationCargoList("oper_id = "+cargoOperId+" and product_id="+productId+"  and out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId()+" and type = 1 ", -1, -1, "id asc");
						for(int j=0;j<outCocList.size();j++){
							CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
							CargoProductStockBean temp_inCps = service.getCargoProductStock("id = "+outCoc.getInCargoProductStockId());
							CargoInfoBean outCi = service.getCargoInfo("id = "+temp_inCps.getCargoId());
							cargoIdMap.put(String.valueOf(outCi.getId()), outCi);
						}
					}
				}else{
					List inCocList = service.getCargoOperationCargoList("oper_id = "+cargoOperId+" and type = 0 and out_cargo_product_stock_id="+outCpsBean.getId(), -1, -1, "id asc");
					for(int i=0;i<inCocList.size();i++){
						CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
						CargoInfoBean ci=service.getCargoInfo("whole_code='"+inCoc.getInCargoWholeCode()+"'");
						cargoIdMap.put(""+ci.getId(), ""+ci.getId());
					}
				}


				if(operBean.getType()==3){//调拨单
					for(int i=0;i<inCargoIdList.length;i++){
						String inCargoId=inCargoIdList[i];//目的货位ciId
						if(cargoIdMap.containsKey(inCargoId)){
							request.setAttribute("tip", "不能选择已经包含的货位！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						CargoInfoBean inCiBean=service.getCargoInfo("id="+inCargoId);
						if(inCiBean.getStoreType()!=outCiBean.getStoreType()){
							request.setAttribute("tip", "不能选择不同存放类型的货位！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if( consignmentService.isProductConsignment(StringUtil.parstInt(productId)) ) {
							if( !consignmentService.isCargoStockAreaCodeSub(inCiBean.getId(), Constants.CONSIGNMENT_STOCK_AREA) ) {
								request.setAttribute("tip", "该商品为快销商品，不能分配非"+Constants.CONSIGNMENT_STOCK_AREA+"区的货位！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
						CargoProductStockBean inCpsBean=service.getCargoProductStock("cargo_id="+inCargoId+" and product_id="+productId);
						if(inCpsBean==null){
							if(inCiBean.getStoreType()==0){
								CargoProductStockBean inCpsBean2=service.getCargoProductStock("cargo_id="+inCargoId);
								if(inCpsBean2!=null){
									request.setAttribute("tip", "该货位内商品已经变动，不能提交！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							}
						}
					}
				} else if (operBean.getType() == CargoOperationBean.TYPE0 ) {  //上架单
					for(int i=0;i<inCargoIdList.length;i++){
						String inCargoId=inCargoIdList[i];//目的货位ciId
						CargoInfoBean inCiBean=service.getCargoInfo("id="+inCargoId);
						if( consignmentService.isProductConsignment(StringUtil.parstInt(productId)) ) {
							if( !consignmentService.isCargoStockAreaCodeSub(inCiBean.getId(), Constants.CONSIGNMENT_STOCK_AREA) ) {
								request.setAttribute("tip", "该商品为快销商品，不能分配非"+Constants.CONSIGNMENT_STOCK_AREA+"区的货位！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
						}
					}
				}

				CargoOperationLogBean logBean=new CargoOperationLogBean();
				logBean.setOperId(Integer.parseInt(cargoOperId));
				logBean.setOperAdminId(user.getId());
				logBean.setOperAdminName(user.getUsername());
				logBean.setOperDatetime(DateUtil.getNow());
				StringBuilder logRemark=new StringBuilder("分配货位：商品");
				logRemark.append(product.getCode());
				logRemark.append("，");
				//开始添加
				synchronized(CargoOperationAction.cargoAssignLock){
					for(int i=0;i<inCargoIdList.length;i++){
						String inCargoId=inCargoIdList[i];//目的货位ciId
						CargoProductStockBean inCpsBean=service.getCargoProductStock("cargo_id="+inCargoId+" and product_id="+productId);
						if(inCpsBean==null){
							CargoInfoBean ciBean=service.getCargoInfo("id="+inCargoId);
							if(ciBean.getStatus() ==CargoInfoBean.STATUS0 && ciBean.getStoreType()==CargoInfoBean.STORE_TYPE0){
								request.setAttribute("tip", "货位号"+ciBean.getWholeCode()+"已被占用，请重新选择其他货位!");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							CargoProductStockBean cpsBean=new CargoProductStockBean();
							cpsBean.setCargoId(Integer.parseInt(inCargoId));
							cpsBean.setProductId(Integer.parseInt(productId));
							if(!service.addCargoProductStock(cpsBean)){
								request.setAttribute("tip", "数据库操作失败!");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							cpsBean.setId(service.getDbOp().getLastInsertId()); 
							inCpsBean=cpsBean;
						}
						CargoInfoBean inCiBean=service.getCargoInfo("id="+inCpsBean.getCargoId());
						if(operBean.getType()==0&&!inCiBean.getWholeCode().startsWith(operBean.getStorageCode())){//不属于上架单的目的仓库
							request.setAttribute("tip", "货位"+inCiBean.getWholeCode()+"不属于上架单的目的仓库，分配失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if(operBean.getType()==0&&inCiBean.getStoreType()!=operBean.getStockInType()){//上架单，存放类型不匹配
							request.setAttribute("tip", "货位"+inCiBean.getWholeCode()+"不属于上架单的存放类型，分配失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if(otherPagePass.equals("upShelf") || otherPagePass.equals("downShelf")){
							if(cargoIdMap.containsKey(String.valueOf(inCargoId))){
								request.setAttribute("tip", "不能选择已经包含的货位！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							CargoInfoBean cargoInoBean = service.getCargoInfo(" id="+inCargoId);
							if(otherPagePass.equals("upShelf")){
								if(cargoInoBean.getStoreType()==CargoInfoBean.STORE_TYPE2){
									request.setAttribute("tip", "目的货位不能选择缓存区！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							}else if(otherPagePass.equals("downShelf")) { 
								if(cargoInoBean.getStoreType()==CargoInfoBean.STORE_TYPE2 || cargoInoBean.getStoreType()==CargoInfoBean.STORE_TYPE0 || cargoInoBean.getStoreType()==CargoInfoBean.STORE_TYPE4){
									request.setAttribute("tip", "目的货位只能选择整件区！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							}
						}

						logRemark.append("手动新分配目的货位（");
						logRemark.append(inCiBean.getWholeCode());
						logRemark.append("）");
						if(i!=inCargoIdList.length-1){
							logRemark.append("，");
						}
						if(inCiBean.getStatus()==0 &&inCpsBean.getProductId()!=outCpsBean.getProductId() && inCiBean.getStoreType()!= CargoInfoBean.STORE_TYPE1){
							request.setAttribute("tip", "不能选择非本货的货位");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if(inCiBean.getStatus() ==CargoInfoBean.STATUS2 ){
							request.setAttribute("tip", "货位"+inCiBean.getWholeCode()+"未开通，不能添加");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if(inCpsBean.getId()==0){
							request.setAttribute("tip", "分配货位发生错误");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}

						service.updateCargoInfo("status=0", "id="+inCiBean.getId());

						CargoOperationCargoBean cocBean=new CargoOperationCargoBean();
						cocBean.setOperId(Integer.parseInt(cargoOperId));
						cocBean.setProductId(Integer.parseInt(productId));
						cocBean.setOutCargoProductStockId(Integer.parseInt(outCpsId));
						cocBean.setOutCargoWholeCode(outCiBean.getWholeCode());
						cocBean.setInCargoProductStockId(inCpsBean.getId());
						cocBean.setInCargoWholeCode(inCiBean.getWholeCode());
						if(operBean.getType()==CargoOperationBean.TYPE3&&(outCiBean.getStoreType()==CargoInfoBean.STORE_TYPE0||outCiBean.getStoreType()==CargoInfoBean.STORE_TYPE4)){//调拨单，散件区
							int stockLockCount=outCpsBean.getStockLockCount();//源货位库存锁定量，应调拨的总量
							int count1=stockLockCount;//应调拨量
							int count2=inCiBean.getMaxStockCount()-inCpsBean.getStockCount()-inCpsBean.getStockLockCount()-inCiBean.getSpaceLockCount();//该货位能调拨量
							List cocList=service.getCargoOperationCargoList("oper_id="+cargoOperId+" and out_cargo_whole_code='"+outCiBean.getWholeCode()+"' and type=0", -1, -1, null);//已添加的目的货位
							for(int j=0;j<cocList.size();j++){
								CargoOperationCargoBean coc=(CargoOperationCargoBean)cocList.get(j);
								count1-=coc.getStockCount();
							}
							if(count2<0){
								count2=0;
							}
							if(count1>=count2){
								cocBean.setStockCount(count2);
							}else if(count1<count2){
								cocBean.setStockCount(count1);
							}
							if(count2==0){
								cocBean.setUseStatus(0);
							}else{
								cocBean.setUseStatus(1);
							}
						}else{
							cocBean.setUseStatus(0);
							cocBean.setStockCount(0);
						}
						if(otherPagePass.equals("downShelf")){ //如果是下架 分配货位是目标地址
							cocBean.setType(1);
						}else{
							cocBean.setType(0);
						}
						if(!service.addCargoOperationCargo(cocBean)){
							request.setAttribute("tip", "数据库操作失败！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}//添加cargo_operation_cargo
					}
				}
				logBean.setRemark(logRemark.toString());
				service.addCargoOperationLog(logBean);
				service.getDbOp().commitTransaction();
			}catch(Exception e){
				e.printStackTrace();
				request.setAttribute("tip", "系统异常，请联系管理员！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}finally{
				service.releaseAll();
			}
			if( otherPagePass.equals("upShelf")){
				return new ActionForward("/admin/cargoOper.do?method=showEditCargoOperation&operationId="+cargoOperId);
			}else if(otherPagePass.equals("downShelf")){
				return new ActionForward("/admin/cargoDownShelf.do?method=showDownShel&id="+cargoOperId);
			}else{
				return new ActionForward("/admin/cargoOperation.do?method=exchangeCargo&cargoOperId="+cargoOperId);
			}
		}
	}

	/**
	 *	删除调拨单
	 */
	public ActionForward deleteExchangeCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int id = StringUtil.StringToId(request.getParameter("cargoOperId"));//作业单Id
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
				service.getDbOp().startTransaction();
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+id);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS28){
					request.setAttribute("tip", "该作业单已确认，无法删除！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				List areaList=CargoDeptAreaService.getCargoDeptAreaList(request);
				//只有源货位的信息列表
				List outCocList = service.getCargoOperationCargoList("oper_id = "+id+" and type = 1", -1, -1, "id asc");
//				2014-9-2取消限制
//				if(outCocList.size()==0){
//					request.setAttribute("tip", "单据里没有任何源货位信息，不能提交！");
//					request.setAttribute("result", "failure");
//					service.getDbOp().rollbackTransaction();
//					return mapping.findForward(IConstants.FAILURE_KEY);
//				}
				for(int i=0;i<outCocList.size();i++){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);//只有源货位的cocBean
					CargoInfoBean ci=service.getCargoInfo("whole_code='"+outCoc.getOutCargoWholeCode()+"'");
					if (ci == null) {
						request.setAttribute("tip", "货位不存在！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(!areaList.contains(ci.getAreaId()+"")){
						request.setAttribute("tip", "只能操作本地区作业单！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				//还原源货位冻结量
				service.deleteCargoOperation("id = "+id);
				service.deleteCargoOperationCargo("oper_id = "+id);
				request.setAttribute("delete","1");
				service.getDbOp().commitTransaction();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				service.getDbOp().rollbackTransaction();
			}finally{
				service.releaseAll();
			}
		}
		return mapping.findForward("toExchangeCargoList");
	}

	/**
	 *	删除调拨单商品
	 */
	public ActionForward deleteExchangeCargoProduct(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(cargoLock){
			int id = StringUtil.StringToId(request.getParameter("id"));//源货位cocId
			int operId = StringUtil.StringToId(request.getParameter("operId"));//作业单Id
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
				service.getDbOp().startTransaction();
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS28){
					request.setAttribute("tip", "该作业单已确认，无法删除！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				CargoOperationCargoBean coc = service.getCargoOperationCargo("id = "+id);
				if(coc == null){
					request.setAttribute("tip", "该作业单产品不存在！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				int productId=coc.getProductId();
				voProduct product=wareService.getProduct(productId);
				CargoOperationLogBean logBean=new CargoOperationLogBean();
				logBean.setOperId(operId);
				logBean.setOperDatetime(DateUtil.getNow());
				logBean.setOperAdminId(user.getId());
				logBean.setOperAdminName(user.getUsername());
				logBean.setRemark("删除调拨单商品，商品"+product.getCode());
				service.addCargoOperationLog(logBean);

				service.deleteCargoOperationCargo("out_cargo_product_stock_id = "+coc.getOutCargoProductStockId()+" and oper_id = "+coc.getOperId());
				request.setAttribute("delete", "1");
				service.getDbOp().commitTransaction();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				service.getDbOp().rollbackTransaction();
			}finally{
				service.releaseAll();
			}
			request.setAttribute("tip", "操作成功");
			request.setAttribute("url", "cargoOperation.do?method=exchangeCargo&cargoOperId="+operId);
		}
		return mapping.findForward("tip");
	}

	/**
	 *	确认提交调拨单
	 */
	public ActionForward confirmExchangeCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("operId"));
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS28){
					request.setAttribute("tip", "该作业单已确认，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				service.getDbOp().startTransaction();
				//只有源货位的coc列表
				List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1 and in_cargo_product_stock_id=0", -1, -1, "id asc");
				for(int i=0;i<outCocList.size();i++){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);
					CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
					voProduct product=wareService.getProduct(outCoc.getProductId());//商品
					CargoInfoBean outCargoBean=service.getCargoInfo("whole_code='"+outCoc.getOutCargoWholeCode()+"'");//源货位
					CargoOperationLogBean logBean=new CargoOperationLogBean();//作业单操作记录 
					logBean.setOperId(operId);
					logBean.setOperDatetime(DateUtil.getNow());
					logBean.setOperAdminId(user.getId());
					logBean.setOperAdminName(user.getUsername());
					logBean.setRemark("确认提交");
					service.addCargoOperationLog(logBean);

					int productCount=0;
					//源货位为outCoc的目的货位列表
					List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "
							+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
					for(int j=0;j<inCocList.size();j++){
						CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(j);
						CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
						CargoInfoBean inCi=service.getCargoInfo("whole_code = '"+inCoc.getInCargoWholeCode()+"'");
						if(inCps!=null&&outCps!=null){
							//散件区调拨单提交时不用锁定源货位
								if(!service.updateCargoProductStockCount(outCps.getId(), -inCoc.getStockCount())){
									request.setAttribute("tip", "操作失败，货位库存不足！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								if(!service.updateCargoProductStockLockCount(outCps.getId(), inCoc.getStockCount())){
									request.setAttribute("tip", "操作失败，货位库存不足！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
							//调整合格库库存
							if(outCargoBean.getAreaId()!=inCi.getAreaId()){
								productCount+=inCoc.getStockCount();//计算调拨的产品总数
							}
						}else{
							request.setAttribute("tip", "货位库存信息异常，请联系管理员！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}

					}
					//调整合格库库存
					CargoInfoBean outCi=service.getCargoInfo("id="+outCps.getCargoId());
					CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
					ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+product.getId());
					if(outProductStock==null){
						request.setAttribute("tip", "合格库库存数据错误！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if (!psService.updateProductStockCount(outProductStock.getId(),-productCount)) {
						request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if (!psService.updateProductLockCount(outProductStock.getId(),productCount)) {
						request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}

					//删除coc无用数据
					service.deleteCargoOperationCargo("oper_id = "+operId+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 0");
				}
				if(!service.updateCargoOperation(
						"status = "+CargoOperationProcessBean.OPERATION_STATUS29+"," +
								"effect_status=0,last_operate_datetime='"+DateUtil.getNow()+"'," +
										"confirm_datetime = '"+DateUtil.getNow()+"'," +
												"confirm_user_name = '"+user.getUsername()+"'", "id = "+operId)){
					request.setAttribute("tip", "更新调拨单状态失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog!=null&&lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
					CargoOperationProcessBean tempProcess=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//生成作业单
					int effectTime=tempProcess.getEffectTime();//生成阶段时效
					String lastOperateTime=lastLog.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
					}
				}

				CargoOperLogBean operLog=new CargoOperLogBean();
				operLog.setOperId(operId);
				operLog.setOperCode(cargoOperation.getCode());
				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS28);
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS29);
				operLog.setOperName(process2.getOperName());
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(0);
				operLog.setRemark("");
				operLog.setPreStatusName(process.getStatusName());
				operLog.setNextStatusName(process2.getStatusName());
				service.addCargoOperLog(operLog);

				service.getDbOp().commitTransaction();
				request.setAttribute("tip", "操作成功");
				request.setAttribute("url", "cargoOperation.do?method=exchangeCargo&cargoOperId="+operId);
				return mapping.findForward("tip");
			}catch (Exception e) {
				if(stockLog.isErrorEnabled()){
					stockLog.error("confirmExchangeCargo error", e);
				}
				request.setAttribute("tip", "系统异常，请联系管理员！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}finally{
				service.releaseAll();
			}
		}
	}

	/**
	 *	调拨单交接阶段
	 */
	public ActionForward auditingExchangeCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(cargoLock){
			int operId = StringUtil.StringToId(request.getParameter("operId"));
			int nextStatus=StringUtil.toInt(request.getParameter("nextStatus"));//下一个状态

			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
				service.getDbOp().startTransaction();
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()>=nextStatus){
					request.setAttribute("tip", "该作业单状态已被更新，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				List areaList=CargoDeptAreaService.getCargoDeptAreaList(request);
				//只有源货位的信息列表
				List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1", -1, -1, "id asc");
				if(outCocList.size()==0){
					request.setAttribute("tip", "单据里没有任何源货位信息，不能提交！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				for(int i=0;i<outCocList.size();i++){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);//只有源货位的cocBean
					CargoInfoBean ci=service.getCargoInfo("whole_code='"+outCoc.getOutCargoWholeCode()+"'");
					if (ci == null) {
						request.setAttribute("tip", "货位不存在！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(!areaList.contains(ci.getAreaId()+"")){
						request.setAttribute("tip", "只能操作本地区作业单！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}
				CargoOperationLogBean logBean=new CargoOperationLogBean();
				logBean.setOperId(operId);
				logBean.setOperDatetime(DateUtil.getNow());
				logBean.setOperAdminId(user.getId());
				logBean.setOperAdminName(user.getUsername());

				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+nextStatus);//下个阶段
				if(process==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(process2==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				int handleType=process2.getHandleType();//操作方式，0人工确认，1设备确认
				if(handleType!=0){
					request.setAttribute("tip", "当前操作方式为设备确认！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog!=null&&lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
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
						"status="+nextStatus+",effect_status = 0," +
								"last_operate_datetime='"+DateUtil.getNow()+"'"+(
										cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS29?("," +
												"auditing_datetime='"+DateUtil.getNow()+"'," +
														"auditing_user_id="+user.getId()+"," +
																"auditing_user_name='"+user.getUsername()+"'"):""), "id="+operId)){
					request.setAttribute("tip", "更新调拨单状态失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				CargoOperLogBean operLog=new CargoOperLogBean();
				operLog.setOperId(operId);
				operLog.setOperCode(cargoOperation.getCode());
				operLog.setOperName(process2.getOperName());
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(0);
				operLog.setRemark("");
				operLog.setPreStatusName(process.getStatusName());
				operLog.setNextStatusName(process2.getStatusName());
				service.addCargoOperLog(operLog);
				service.getDbOp().commitTransaction();
				request.setAttribute("tip", "操作成功");
				request.setAttribute("url", "cargoOperation.do?method=exchangeCargo&cargoOperId="+operId);
			}catch (Exception e) {
				if(stockLog.isErrorEnabled()){
					stockLog.error("auditingExchangeCargo", e);
				}
				request.setAttribute("tip", "系统异常，请联系管理员！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}finally{
				service.releaseAll();
			}
		}
		return mapping.findForward("tip");
	}

	/**
	 *	完成调拨单
	 */
	public ActionForward completeExchangeCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		synchronized(cargoLock){
			int operId = StringUtil.StringToId(request.getParameter("operId"));
			int nextStatus=StringUtil.toInt(request.getParameter("nextStatus"));//下一个状态

			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{
				service.getDbOp().startTransaction();
				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()>=nextStatus){
					request.setAttribute("tip", "该作业单状态已被更新，操作失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				List areaList=CargoDeptAreaService.getCargoDeptAreaList(request);
				/*if(!areaList.contains(cargoOperation.getStockInArea()+"")){
					request.setAttribute("tip", "只能操作本地区作业单！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}*/
				service.getDbOp().startTransaction();
				List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>(); //存放财务接口数据
				
				//完成货位库存量操作
				List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1", -1, -1, "id asc");
				for(int i=0;i<outCocList.size();i++){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);
					CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
					CargoInfoBean outCi = service.getCargoInfo("id = "+outCps.getCargoId());
					
					voProduct product = wareService.getProduct(outCoc.getProductId());
					product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
					int stockOutCount = 0;
					int productStockCount=0;
					List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
					for(int j=0;j<inCocList.size();j++){
						CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(j);
						CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
						CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getInCargoWholeCode()+"'");
						//添加判断操作用户是否有 目的货位的地区权限
						int stockAreaId = service.getOldAreaIdOfCargo(inCi);
						boolean isUserHasRight = CargoDeptAreaService.hasStockAreaRight(areaList,stockAreaId);
						if( !isUserHasRight ) {
							request.setAttribute("tip", "你没有目的货位："+inCi.getWholeCode()+"的地区权限，不可以操作！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if(inCps!=null&&outCps!=null){
							if(!service.updateCargoProductStockCount(inCps.getId(), inCoc.getStockCount())){
								request.setAttribute("tip", "操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							if(!service.updateCargoProductStockLockCount(outCps.getId(), -inCoc.getStockCount())){
								request.setAttribute("tip", "操作失败，货位冻结库存不足！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}

							//调整合格库库存，修改批次，添加进销存卡片
							if(inCi.getAreaId()!=outCi.getAreaId()){
								CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+inCi.getAreaId());
								ProductStockBean inProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+product.getId());
								if(inProductStock==null){
									request.setAttribute("tip", "合格库库存数据错误！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								if (!psService.updateProductStockCount(inProductStock.getId(),inCoc.getStockCount())) {
									request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								productStockCount+=inCoc.getStockCount();
								
								ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+inCi.getAreaId()+" and type="+inCi.getStockType());
								ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+outCi.getAreaId()+" and type="+outCi.getStockType());
								
								//组装财务接口需要的数据
								BaseProductInfo baseProductInfo = new BaseProductInfo();
								baseProductInfo.setId(inCoc.getProductId());
								baseProductInfo.setProductStockOutId(psOut.getId());
								baseProductInfo.setProductStockId(psIn.getId());
								baseProductInfo.setOutCount(inCoc.getStockCount());
								baseList.add(baseProductInfo);
								
								//批次修改开始
								/**
								//更新批次记录、添加调拨出、入库批次记录
								List sbList = stockService.getStockBatchList("product_id="+outCoc.getProductId()+" and stock_type="+outCi.getStockType()+" and stock_area="+outCi.getAreaId(), -1, -1, "id asc");
								double stockinPrice = 0;
								double stockoutPrice = 0;
								if(sbList!=null&&sbList.size()!=0){
									int stockExchangeCount = inCoc.getStockCount();
									int index = 0;
									int stockBatchCount = 0;
									
									do {
										//出库
										StockBatchBean batch = (StockBatchBean)sbList.get(index);
										if(stockExchangeCount>=batch.getBatchCount()){
											if(!stockService.deleteStockBatch("id="+batch.getId())){
												request.setAttribute("tip", "数据库操作失败！");
								                request.setAttribute("result", "failure");
								                service.getDbOp().rollbackTransaction();
								                return mapping.findForward(IConstants.FAILURE_KEY);
											}
											stockBatchCount = batch.getBatchCount();
										}else{
											if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
												request.setAttribute("tip", "数据库操作失败！");
								                request.setAttribute("result", "failure");
								                service.getDbOp().rollbackTransaction();
								                return mapping.findForward(IConstants.FAILURE_KEY);
											}
											stockBatchCount = stockExchangeCount;
										}
										
										//添加批次操作记录
										StockBatchLogBean batchLog = new StockBatchLogBean();
										batchLog.setCode(cargoOperation.getCode());
										batchLog.setStockType(batch.getStockType());
										batchLog.setStockArea(batch.getStockArea());
										batchLog.setBatchCode(batch.getCode());
										batchLog.setBatchCount(stockBatchCount);
										batchLog.setBatchPrice(batch.getPrice());
										batchLog.setProductId(batch.getProductId());
										batchLog.setRemark("调拨出库");
										batchLog.setCreateDatetime(DateUtil.getNow());
										batchLog.setUserId(user.getId());
										batchLog.setSupplierId(batch.getSupplierId());
										batchLog.setTax(batch.getTax());
										if(!stockService.addStockBatchLog(batchLog)){
											 request.setAttribute("tip", "添加失败！");
								             request.setAttribute("result", "failure");
								             service.getDbOp().rollbackTransaction();
								             return mapping.findForward(IConstants.FAILURE_KEY);
										}
										
										stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
										
										//入库
										StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId());
										if(batchBean!=null){
											if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
												request.setAttribute("tip", "数据库操作失败！");
								                request.setAttribute("result", "failure");
								                service.getDbOp().rollbackTransaction();
								                return mapping.findForward(IConstants.FAILURE_KEY);
											}
										}else{
											StockBatchBean newBatch = new StockBatchBean();
											newBatch.setCode(batch.getCode());
											newBatch.setProductId(batch.getProductId());
											newBatch.setPrice(batch.getPrice());
											newBatch.setBatchCount(stockBatchCount);
											newBatch.setProductStockId(psIn.getId());
											newBatch.setStockArea(inCi.getAreaId());
											newBatch.setStockType(psIn.getType());
											newBatch.setSupplierId(batch.getSupplierId());
											newBatch.setTax(batch.getTax());
											newBatch.setNotaxPrice(batch.getNotaxPrice());
											newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
											if(!stockService.addStockBatch(newBatch)){
												request.setAttribute("tip", "添加失败！");
												request.setAttribute("result", "failure");
												service.getDbOp().rollbackTransaction();
												return mapping.findForward(IConstants.FAILURE_KEY);
											}
										}
										
										//添加批次操作记录
										batchLog = new StockBatchLogBean();
										batchLog.setCode(cargoOperation.getCode());
										batchLog.setStockType(psIn.getType());
										batchLog.setStockArea(inCi.getAreaId());
										batchLog.setBatchCode(batch.getCode());
										batchLog.setBatchCount(stockBatchCount);
										batchLog.setBatchPrice(batch.getPrice());
										batchLog.setProductId(batch.getProductId());
										batchLog.setRemark("调拨入库");
										batchLog.setCreateDatetime(DateUtil.getNow());
										batchLog.setUserId(user.getId());
										batchLog.setSupplierId(batch.getSupplierId());
										batchLog.setTax(batch.getTax());
										if(!stockService.addStockBatchLog(batchLog)){
											request.setAttribute("tip", "添加失败！");
											request.setAttribute("result", "failure");
											service.getDbOp().rollbackTransaction();
											return mapping.findForward(IConstants.FAILURE_KEY);
										}
										
										stockExchangeCount -= batch.getBatchCount();
										index++;
										
										stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
									} while (stockExchangeCount>0&&index<sbList.size());
								}
								*/
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
								sc.setStockInPriceSum(product.getPrice5());

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
								int scId = service.getNumber("id", "stock_card", "max", "id > 0") + 1;
								sc2.setId(scId);

								sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
								sc2.setCode(cargoOperation.getCode());

								sc2.setCreateDatetime(DateUtil.getNow());
								sc2.setStockType(outCi.getStockType());
								sc2.setStockArea(outCi.getAreaId());
								sc2.setProductId(product.getId());
								sc2.setStockId(psOut.getId());
								sc2.setStockOutCount(inCoc.getStockCount());
//								sc2.setStockOutPriceSum(stockOutPrice);
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
							request.setAttribute("tip", "库存错误，无法提交！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}

						if(outCi.getAreaId()!=inCi.getAreaId()){
							//更新订单缺货状态
							this.updateLackOrder(outCoc.getProductId());
						}
					}

					//调整合格库库存
					CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
					ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+product.getId());
					if(outProductStock==null){
						request.setAttribute("tip", "合格库库存数据错误！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if (!psService.updateProductLockCount(outProductStock.getId(),-productStockCount)) {
						request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
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
				
				//调用财务接口
				if(!baseList.isEmpty()){
					FinanceBaseDataService baseService = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN, service.getDbOp().getConn());
					baseService.acquireFinanceBaseData(baseList, cargoOperation.getCode(), user.getId(), 0, 0);
				}

				if(!service.updateCargoOperation(
						"status="+CargoOperationProcessBean.OPERATION_STATUS34+"," +
								"effect_status = 2,last_operate_datetime='"+DateUtil.getNow()+"'"+"," +
										"complete_datetime='"+DateUtil.getNow()+"'," +
												"complete_user_id="+user.getId()+"," +
														"complete_user_name='"+user.getUsername()+"'", "id="+operId)){
					request.setAttribute("tip", "更新调拨单状态失败！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS34);//下个阶段
				if(process==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(process2==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog!=null&&lastLog.getEffectTime()==1){//如果不是进行中，不需要再改时效
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

				service.getDbOp().commitTransaction();
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				if(stockLog.isErrorEnabled()){
					stockLog.error("completeExchangeCargo error", e);
				}
				service.getDbOp().rollbackTransaction();
				request.setAttribute("tip", "系统异常，请联系管理员！");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}finally{
				service.releaseAll();
			}
			request.setAttribute("tip", "操作成功");
			request.setAttribute("url", "cargoOperation.do?method=exchangeCargo&cargoOperId="+operId);
		}
		return mapping.findForward("tip");
	}

	/**
	 *	复核调拨单
	 */
	public ActionForward checkExchangeCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int operId = StringUtil.StringToId(request.getParameter("operId"));
		int status = StringUtil.toInt(request.getParameter("status"));
		String remark = StringUtil.convertNull(request.getParameter("remark"));
		String msg="操作成功";
		synchronized(cargoLock){
			WareService wareService = new WareService();
			ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
			try{

				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+operId);
				if(cargoOperation == null){
					request.setAttribute("tip", "该作业单不存在！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()>CargoOperationProcessBean.OPERATION_STATUS34){
					request.setAttribute("tip", "该作业单状态已修改，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS28){
					request.setAttribute("tip", "该作业单还未提交，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(cargoOperation.getEffectStatus()==CargoOperationBean.EFFECT_STATUS3||cargoOperation.getEffectStatus()==CargoOperationBean.EFFECT_STATUS4){
					request.setAttribute("tip", "该作业单时效状态已修改，操作失败！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				List areaList=CargoDeptAreaService.getCargoDeptAreaList(request);
				if(!areaList.contains(cargoOperation.getStockInArea()+"")){
					request.setAttribute("tip", "只能操作本地区作业单！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				service.getDbOp().startTransaction();
				if(status==3){//作业成功
					if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS34){
						request.setAttribute("tip", "该作业单未结束，操作失败！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					if(!service.updateCargoOperation(
							"effect_status="+CargoOperationBean.EFFECT_STATUS3+"," +
									"last_operate_datetime='"+DateUtil.getNow()+"'", "id="+operId)){
						request.setAttribute("tip", "更新调拨单状态失败！");
						request.setAttribute("result", "failure");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
				}else if(status==4){//作业失败
					if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS34){
						//还原货位库存
						List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1", -1, -1, "id asc");
						for(int i=0;i<outCocList.size();i++){
							CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);
							CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
							CargoInfoBean outCi=service.getCargoInfo("whole_code='"+outCoc.getOutCargoWholeCode()+"'");
							List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
							for(int j=0;j<inCocList.size();j++){
								CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(j);
								CargoInfoBean inCi=service.getCargoInfo("whole_code='"+inCoc.getInCargoWholeCode()+"'");
								if(outCps!=null){
									if(!service.updateCargoProductStockCount(outCps.getId(), inCoc.getStockCount())){
										request.setAttribute("tip", "操作失败，货位冻结库存不足！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if(!service.updateCargoProductStockLockCount(outCps.getId(), -inCoc.getStockCount())){
										request.setAttribute("tip", "操作失败，货位冻结库存不足！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
								}else{
									request.setAttribute("tip", "库存错误，无法提交！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								//调整合格库库存
								CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
								if(inCi.getAreaId()!=outCi.getAreaId()){
									ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+outCps.getProductId());
									if(outProductStock==null){
										request.setAttribute("tip", "合格库库存数据错误！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductStockCount(outProductStock.getId(),inCoc.getStockCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductLockCount(outProductStock.getId(),-inCoc.getStockCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
								}
								
								if(outCi.getAreaId()!=inCi.getAreaId()){
									//更新订单缺货状态
									this.updateLackOrder(outCoc.getProductId());
								}
							}


						}
					}else if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS34){
						
					}
					service.updateCargoOperation("effect_status="+CargoOperationBean.EFFECT_STATUS4+",last_operate_datetime='"+DateUtil.getNow()+"'", "id="+operId);
					
				}

				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS34);//下个阶段
				if(process==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				if(process2==null){
					request.setAttribute("tip", "作业单流程信息错误！");
					request.setAttribute("result", "failure");
					service.getDbOp().rollbackTransaction();
					return mapping.findForward(IConstants.FAILURE_KEY);
				}

				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog!=null&&lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
					int effectTime=process.getEffectTime();//上阶段时效
					String lastOperateTime=lastLog.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
					}
				}

				CargoOperLogBean operLog=new CargoOperLogBean();
				operLog.setOperId(operId);
				operLog.setOperCode(cargoOperation.getCode());
				operLog.setOperName("作业复核");
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(status);
				operLog.setRemark(remark);
				operLog.setPreStatusName(process.getStatusName());
				operLog.setNextStatusName(process2.getStatusName());
				service.addCargoOperLog(operLog);

				service.getDbOp().commitTransaction();
			}catch (Exception e) {
				// TODO: handle exception
				service.getDbOp().rollbackTransaction();
				e.printStackTrace();
				if(stockLog.isErrorEnabled()){
					stockLog.error(StringUtil.getExceptionInfo(e));
				}
				msg="操作失败";
			}finally{
				service.releaseAll();
			}
		}
		request.setAttribute("tip", msg);
		request.setAttribute("url", "cargoOperation.do?method=exchangeCargo&cargoOperId="+operId);
		return mapping.findForward("tip");
	}

	/**
	 *	打印货位间调拨单
	 */
	public ActionForward printExchangeCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int id = StringUtil.StringToId(request.getParameter("id"));

		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		CartonningInfoService cartonningService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		try{
			CargoOperationBean cargoOperation = service.getCargoOperation("id = "+id);//查找调拨单
			if(cargoOperation == null){
				request.setAttribute("tip", "该作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}

			TreeMap printMap=new TreeMap();
			//查找调拨单货位信息
			List cocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and type = 0 and use_status = 1", -1, -1, "product_id desc");
			Iterator iter = cocList.listIterator();
			while(iter.hasNext()){
				CargoOperationCargoBean outCoc = (CargoOperationCargoBean)iter.next();//源货位
				voProduct product = wareService.getProduct(outCoc.getProductId());
				outCoc.setProduct(product);
				if(printMap.get(outCoc.getProductId()+"")!=null){//该产品已添加
					continue;
				}else{
					CargoOperationCargoBean inCoc=service.getCargoOperationCargo("oper_id="+id+" and type=0 and product_id="+outCoc.getProductId());
					CargoInfoBean cargoInfo=service.getCargoInfo("whole_code='"+inCoc.getInCargoWholeCode()+"'");

					if(cargoInfo==null){
						request.setAttribute("tip", "货位错误！");
						request.setAttribute("result", "failure");
						return mapping.findForward(IConstants.FAILURE_KEY);
					}
					List cartonningList=cartonningService.getCartonningList("oper_id="+id+" and cargo_id="+cargoInfo.getId(), -1, -1, null);
					List cartonningList2=new ArrayList();//要传的list
					for(int i=0;i<cartonningList.size();i++){
						CartonningInfoBean cartonningInfo=(CartonningInfoBean)cartonningList.get(i);
						CartonningProductInfoBean cartonningProduct=cartonningService.getCartonningProductInfo("cartonning_id="+cartonningInfo.getId());
						if(cartonningProduct!=null&&cartonningProduct.getProductId()==outCoc.getProductId()){
							cartonningInfo.setProductBean(cartonningProduct);
							cartonningInfo.setCargoWholeCode(service.getCargoInfo("id="+cartonningInfo.getCargoId()).getWholeCode());
							cartonningList2.add(cartonningInfo);
						}
					}
					outCoc.setCartonningList(cartonningList2);
					printMap.put(outCoc.getProductId()+"", outCoc);
				}
			}
			service.updateCargoOperation("print_count = print_count+1", "id = "+id);
			request.setAttribute("cargoOperation", cargoOperation);
			request.setAttribute("printMap", printMap);

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}

		return mapping.findForward("exchangeCargoPrint");
	}

	/**
	 *	打印上架单
	 */
	public ActionForward printUpShelfCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int id = StringUtil.StringToId(request.getParameter("id"));

		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{

			CargoOperationBean cargoOperation = service.getCargoOperation("id = "+id);
			if(cargoOperation == null){
				request.setAttribute("tip", "该作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			TreeMap printMap=new TreeMap();
			List cocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and type = 1", -1, -1, "product_id desc");
			Iterator iter = cocList.listIterator();
			while(iter.hasNext()){
				CargoOperationCargoBean outCoc = (CargoOperationCargoBean)iter.next();
				CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
				CargoInfoBean ci = service.getCargoInfo("whole_code = '"+outCoc.getOutCargoWholeCode()+"'");
				voProduct product = wareService.getProduct(outCoc.getProductId());
				outCoc.setCargoProductStock(outCps);
				outCoc.setCargoInfo(ci);
				outCoc.setProduct(product);

				List list = service.getCargoOperationCargoList(
						"out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and oper_id = "+outCoc.getOperId()+" and product_id = "+outCoc.getProductId()+" and type = 0 and use_status=1", -1, -1, "id asc");
				outCoc.setCocList(list);

				Iterator iter2 = list.listIterator();
				while(iter2.hasNext()){
					CargoOperationCargoBean inCoc = (CargoOperationCargoBean)iter2.next();
					CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
					CargoInfoBean inCi = service.getCargoInfo("whole_code= '"+inCoc.getInCargoWholeCode()+"'");
					product = wareService.getProduct(inCoc.getProductId());
					inCoc.setCargoProductStock(inCps);
					inCoc.setCargoInfo(inCi);
					inCoc.setProduct(product);

					CargoOperationCargoBean printCoc=inCoc;
					printCoc.setOutCargoWholeCode(outCoc.getOutCargoWholeCode());
					printMap.put(printCoc.getInCargoWholeCode()+"-"+printCoc.getId(), printCoc);
				}

				CargoInfoAreaBean stockArea = service.getCargoInfoArea("id = "+ci.getAreaId());
				request.setAttribute("stockAreaName", stockArea.getName());
				request.setAttribute("stockStockName", ci.getStockTypeName());
			}

			service.updateCargoOperation("print_count = print_count+1", "id = "+id);

			request.setAttribute("cocList", cocList);
			request.setAttribute("printMap", printMap);
			request.setAttribute("cargoOperation", cargoOperation);

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}

		return mapping.findForward("upShelfCargoPrint");
	}

	/**
	 *	打印下架单
	 */
	public ActionForward printDownShelfCargo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int id = StringUtil.StringToId(request.getParameter("id"));

		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{

			CargoOperationBean cargoOperation = service.getCargoOperation("id = "+id);
			if(cargoOperation == null){
				request.setAttribute("tip", "该作业单不存在！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}

			TreeMap printMap=new TreeMap();
			List cocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and type = 0", -1, -1, "product_id desc");
			Iterator iter = cocList.listIterator();
			while(iter.hasNext()){
				CargoOperationCargoBean outCoc = (CargoOperationCargoBean)iter.next();
				CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
				CargoInfoBean ci = service.getCargoInfo("whole_code = '"+outCoc.getOutCargoWholeCode()+"'");
				voProduct product = wareService.getProduct(outCoc.getProductId());
				outCoc.setCargoProductStock(outCps);
				outCoc.setCargoInfo(ci);
				outCoc.setProduct(product);

				List list = service.getCargoOperationCargoList(
						"out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and oper_id = "+outCoc.getOperId()+" and product_id = "+outCoc.getProductId()+" and type = 1 and use_status=1", -1, -1, "id asc");
				outCoc.setCocList(list);

				Iterator iter2 = list.listIterator();
				while(iter2.hasNext()){
					CargoOperationCargoBean inCoc = (CargoOperationCargoBean)iter2.next();
					CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
					CargoInfoBean inCi = service.getCargoInfo("whole_code= '"+inCoc.getInCargoWholeCode()+"'");
					product = wareService.getProduct(inCoc.getProductId());
					inCoc.setCargoProductStock(inCps);
					inCoc.setCargoInfo(inCi);
					inCoc.setProduct(product);

					CargoOperationCargoBean printCoc=inCoc;
					printCoc.setOutCargoWholeCode(outCoc.getOutCargoWholeCode());
					printMap.put(printCoc.getInCargoWholeCode()+"-"+printCoc.getId(), printCoc);
				}

				CargoInfoAreaBean stockArea = service.getCargoInfoArea("id = "+ci.getAreaId());
				request.setAttribute("stockAreaName", stockArea.getName());
				request.setAttribute("stockStockName", ci.getStockTypeName());
			}

			service.updateCargoOperation("print_count = print_count+1", "id = "+id);

			request.setAttribute("printMap", printMap);
			request.setAttribute("cocList", cocList);
			request.setAttribute("cargoOperation", cargoOperation);

		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}

		return mapping.findForward("downShelfCargoPrint");
	}


	/**
	 *	作业单人员操作记录
	 */
	public ActionForward operationLog(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService wareService = new WareService(dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String operId=StringUtil.convertNull(request.getParameter("operId"));
			CargoOperationBean operBean=service.getCargoOperation("id="+operId);
			if(operBean==null){
				request.setAttribute("tip", "该作业单已被删除！");
				request.setAttribute("result", "failure");
				return mapping.findForward(IConstants.FAILURE_KEY);
			}
			int countPerPage=20;
			int pageIndex=0;
			if(request.getParameter("pageIndex")!=null){
				pageIndex=Integer.parseInt(request.getParameter("pageIndex"));
			}
			String para="&operId="+operId;
			int totalCount=service.getCargoOperationLogCount("oper_id="+operId);
			PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
			paging.setCurrentPageIndex(pageIndex);
			paging.setTotalPageCount(totalCount%countPerPage==0?totalCount/countPerPage:totalCount/countPerPage+1);
			paging.setPrefixUrl("cargoOperation.do?method=operationLog"+para);
			List operLogList=service.getCargoOperationLogList("oper_id="+operId, pageIndex*countPerPage, countPerPage, "id asc");
			request.setAttribute("operLogList", operLogList);
			request.setAttribute("paging", paging);
			request.setAttribute("operBean", operBean);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}

		return mapping.findForward("operLog");
	}

	/**
	 *	相关单据
	 */
	public ActionForward stockExchangeList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOpSlave = new DbOperation();
		dbOpSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOpSlave);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService stockService=ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
			String cargoWholeCode=StringUtil.convertNull(request.getParameter("cargoWholeCode"));//货位编号
			String productCode=StringUtil.convertNull(request.getParameter("productCode"));//产品编号
			CargoInfoBean ciBean=service.getCargoInfo("whole_code='"+cargoWholeCode+"'");
			voProduct product=wareService.getProduct(productCode);

			//类型，stock是库存调拨单，stockLock是库存锁定量，spackLock是空间锁定量
			String type=StringUtil.convertNull(request.getParameter("type"));
			List orderList=new ArrayList();
			List countList=new ArrayList();
			if(type.equals("stock")){
				List sepcList=stockService.getStockExchangeProductCargoList("cargo_info_id="+ciBean.getId()+" and type=1", -1, -1, null);
				for(int i=0;i<sepcList.size();i++){
					StockExchangeProductCargoBean sepc=(StockExchangeProductCargoBean)sepcList.get(i);
					int stockExchangeProductId=sepc.getStockExchangeProductId();
					StockExchangeProductBean sep=stockService.getStockExchangeProduct("id="+stockExchangeProductId);
					if(sep.getProductId()==product.getId()){
						StockExchangeBean se=stockService.getStockExchange("id="+sep.getStockExchangeId());
						if(sep.getNoUpCargoCount()!=0){
							orderList.add(se);
							countList.add(""+sep.getNoUpCargoCount());
						}
					}
				}
			}else if(type.equals("stockLock")){//货位库存锁定量
				
				/****************调拨冻结**************************/
				//开启数据库连接
				DbOperation dbOp = new DbOperation();
				dbOp.init("adult_slave");
				String query = "select se.code,sepc.stock_count "
						+" from stock_exchange se "
						+" join stock_exchange_product sep on se.id=sep.stock_exchange_id and sep.product_id="+product.getId()
						+" join stock_exchange_product_cargo sepc on sepc.type=0 and sepc.stock_exchange_product_id=sep.id and sepc.cargo_info_id="+ciBean.getId()
						+" where (se.status in(2,3,5,6,8) or (se.status=1 and sep.status=1))";
				//System.out.println("1-->"+query);
				ResultSet rs = dbOp.executeQuery(query);
				try {
					while (rs.next()) {
						orderList.add(rs.getString(1));
						countList.add(rs.getInt(2));
					}
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					dbOp.release();//释放所有资源
				}
				
				/****************销售出库冻结**************************/
				//开启数据库连接
				DbOperation dbOp1 = new DbOperation();
				dbOp1.init("adult_slave");
				String query1 = "select os.code,ospc.count "
						+" from order_stock os"
						+" join order_stock_product osp on osp.order_stock_id=os.id and osp.product_id= "+product.getId()
						+" join order_stock_product_cargo ospc on ospc.order_stock_product_id=osp.id and ospc.cargo_whole_code= '"+ciBean.getWholeCode()+"'"
						+" where os.status=5";

				ResultSet rs1 = dbOp1.executeQuery(query1);
				try {
					while (rs1.next()) {
						orderList.add(rs1.getString(1));
						countList.add(rs1.getInt(2));
					}
					rs1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally { 
					dbOp1.release();//释放所有资源 
				}
				
				
				/****************报损单冻结**************************/
				//开启数据库连接
				DbOperation dbOp2 = new DbOperation();
				dbOp2.init("adult_slave");
				String query2="select bo.receipts_number,bpc.count "
					+" from bsby_operationnote bo"
					+" join bsby_product bp on bp.operation_id=bo.id and bp.product_id="+product.getId()
					+" join bsby_product_cargo bpc on bpc.bsby_product_id=bp.id and bpc.cargo_id="+ciBean.getId()
					+" where (bo.current_type = 1 or bo.current_type=6) and bo.type = 0";
				ResultSet rs2 = dbOp2.executeQuery(query2); 
				try {
					while (rs2.next()) {
						orderList.add(rs2.getString(1));
						countList.add(rs2.getInt(2));
					}
					rs2.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally { 
					dbOp2.release();//释放所有资源 
				}
				
				/****************仓内作业单冻结**************************/
				//开启数据库连接
				DbOperation dbOp3 = new DbOperation();
				dbOp3.init("adult_slave");
				String query3 = "select distinct co.code,coc.stock_count "
						+" from cargo_operation co "
						+" join cargo_operation_cargo coc on coc.oper_id=co.id and coc.use_status=1 and coc.product_id= "
						+product.getId()+" and coc.out_cargo_whole_code= '"+ciBean.getWholeCode()+"'"
						+" where co.status in(2,3,11,12,20,21,29,30) "
						+" and co.effect_status in (0,1)" ;
				ResultSet rs3 = dbOp3.executeQuery(query3);
				try {
					while (rs3.next()) {
						orderList.add(rs3.getString(1));
						countList.add(rs3.getInt(2));
					}
					rs3.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					dbOp3.release();//释放所有资源
				}
				/****************货位异常单冻结**************************/
				//开启数据库连接
				DbOperation dbOp4 = new DbOperation();
				dbOp4.init("adult_slave");
				String query4 = "select sa.code,sap.lock_count "
						+" from sorting_abnormal_product sap "
						+" join cargo_info ci on ci.whole_code=sap.cargo_whole_code and ci.whole_code= '"+ciBean.getWholeCode()+"'"
						+" join sorting_abnormal sa on sa.id=sap.sorting_abnormal_id"
						+" where sap.product_id= "+product.getId()
						+" and sap.lock_count>0 and sap.status in (0,1,2,3)";
				ResultSet rs4 = dbOp4.executeQuery(query4);
				try {
					while (rs4.next()) {
						orderList.add(rs4.getString(1));
						countList.add(rs4.getInt(2));
					}
					rs4.close();
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					dbOp4.release();//释放所有资源
				}
				request.setAttribute("orderList", orderList);
				request.setAttribute("countList", countList);
				
			}else if(type.equals("spaceLock")){//空间冻结量
				List refillList=service.getCargoOperationList("type=2 and status in (20,21,22,23,24) and effect_status in (0,1)", -1, -1, null);//补货单
				for(int i=0;i<refillList.size();i++){
					CargoOperationBean coBean=(CargoOperationBean)refillList.get(i);
					CargoOperationCargoBean coc=service.getCargoOperationCargo("oper_id="+coBean.getId()+" and in_cargo_whole_code='"+cargoWholeCode+"'");
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
					List inCocList=service.getCargoOperationCargoList("oper_id="+coBean.getId()+" and in_cargo_whole_code='"+cargoWholeCode+"'", -1, -1, null);
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
			}
			request.setAttribute("orderList", orderList);
			request.setAttribute("countList", countList);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("stockExchangeList");
	}

	/**
	 *	批量打印货位调拨单
	 */

	public ActionForward exchangeCargoListPrint(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		int id = StringUtil.StringToId(request.getParameter("id"));
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService serviceBar = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		voUser admin = (voUser) request.getSession().getAttribute("userView");
		UserGroupBean group =admin.getGroup();
		if(!group.isFlag(509)){
			request.setAttribute("tip", "您没有此权限!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		try {
			List productBarLists = new ArrayList();
			List cargoOperationList = new ArrayList();
			List operationBarList = new ArrayList();//记录调拨单每页的调拨单条码
			List printMapList = new ArrayList();
			List cocListList = new ArrayList();
			List errorList = new ArrayList();
			String code = request.getParameter("list");// 作业单Id
			String[] codes = code.split("\r\n");
			CargoOperationBean cargoOperation = service.getCargoOperation("id = " + id);// 查找调拨单
			ProductBarcodeVO productBarbean = null;
			for (int k = 0; k < codes.length; k++) {
				cargoOperation = service.getCargoOperation("code = '" + codes[k].trim() + "'");
				if (cargoOperation == null) {
					errorList.add(codes[k]);
					continue;
				}
				cargoOperationList.add(cargoOperation);// 调拨单列表
				TreeMap printMap = new TreeMap();
				// 查找调拨单货位信息
				List cocList = service.getCargoOperationCargoList("oper_id = " + cargoOperation.getId() + " and type = 1", -1, -1, "product_id desc");
				Iterator iter = cocList.listIterator();
				List productBarList = new ArrayList();
				while (iter.hasNext()) {
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean) iter.next();
					CargoProductStockBean outCps = service.getCargoProductStock("id = " + outCoc.getOutCargoProductStockId());
					CargoInfoBean ci = service.getCargoInfo("whole_code = '" + outCoc.getOutCargoWholeCode() + "'");
					voProduct product = wareService.getProduct(outCoc.getProductId());
					outCoc.setCargoProductStock(outCps);
					outCoc.setCargoInfo(ci);
					outCoc.setProduct(product);
					List list = service.getCargoOperationCargoList("out_cargo_product_stock_id = " + outCoc.getOutCargoProductStockId() + " and oper_id = " + outCoc.getOperId() + " and product_id = " + outCoc.getProductId() + " and type = 0 and use_status=1", -1, -1, "id asc");
					outCoc.setCocList(list);

					Iterator iter2 = list.listIterator();
					while (iter2.hasNext()) {
						CargoOperationCargoBean inCoc = (CargoOperationCargoBean) iter2.next();
						CargoProductStockBean inCps = service.getCargoProductStock("id = " + inCoc.getInCargoProductStockId());
						CargoInfoBean inCi = service.getCargoInfo("whole_code= '" + inCoc.getInCargoWholeCode() + "'");
						product = wareService.getProduct(inCoc.getProductId());
						inCoc.setCargoProductStock(inCps);
						inCoc.setCargoInfo(inCi);
						inCoc.setProduct(product);
						CargoOperationCargoBean printCoc = inCoc;
						printCoc.setOutCargoWholeCode(outCoc.getOutCargoWholeCode());
						printMap.put(printCoc.getInCargoWholeCode() + "-" + printCoc.getId(), printCoc);

						if(productBarList.size()==5){//调拨单每页最多显示5个商品，所以每页用一个list
							productBarLists.add(productBarList);
							operationBarList.add(cargoOperation.getCode());
							productBarList=new ArrayList();
						}
						productBarbean = serviceBar.getProductBarcode("product_id=" + inCoc.getProductId());
						productBarList.add(productBarbean);
					}

					CargoInfoAreaBean stockArea = service.getCargoInfoArea("id = " + ci.getAreaId());
					request.setAttribute("stockAreaName", stockArea.getName());
					request.setAttribute("stockStockName", ci.getStockTypeName());
				}
				service.updateCargoOperation("print_count = print_count+1", "id = " + id);

				printMapList.add(printMap);
				cocListList.add(cocList);
				productBarLists.add(productBarList);
				operationBarList.add(cargoOperation.getCode());
			}
			request.setAttribute("productBarList", productBarLists);
			request.setAttribute("cargoOperationList", cargoOperationList);
			request.setAttribute("printMapList", printMapList);
			request.setAttribute("cocListList", cocListList);
			request.setAttribute("operationBarList", operationBarList);
			request.setAttribute("errorList", errorList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}

		return mapping.findForward("exchangeCargoListPrint");
	}

	/**
	 *	货位调拨审核扫描页
	 */
	public ActionForward exchangeOperationAudit(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());

		String exchangeCode=StringUtil.convertNull(request.getParameter("exchangeCode"));//调拨单号
		String productCode=StringUtil.convertNull(request.getParameter("productCode"));//商品条码
		try{
			if(!exchangeCode.equals("")&&!productCode.equals("")){
				CargoOperationBean exchangeBean =service.getCargoOperation("code='"+exchangeCode+"' and type="+CargoOperationBean.TYPE3);
				if(exchangeBean==null){
					request.setAttribute("tip", "没有找到该调拨单！");
					request.setAttribute("tip2", "没有找到该调拨单"+"<br/><br/>"+"调拨单号："+exchangeCode+"<br/>"+"商品条码："+productCode);
					return mapping.findForward("exchangeOperationAudit");
				}
				if(exchangeBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS29&&
						exchangeBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS30&&
						exchangeBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS31&&
						exchangeBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS32&&
						exchangeBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS33){
					request.setAttribute("tip", "调拨单状态错误！");
					request.setAttribute("tip2", "调拨单状态错误"+"<br/><br/>"+"调拨单号："+exchangeCode+"<br/>"+"商品条码："+productCode);
					return mapping.findForward("exchangeOperationAudit");
				}
				ProductBarcodeVO productBarcode=bcmService.getProductBarcode("barcode='"+productCode+"'");
				if(productBarcode==null){
					request.setAttribute("tip", "无此商品条码！");
					request.setAttribute("tip2", "无此商品条码"+"<br/><br/>"+"调拨单号："+exchangeCode+"<br/>"+"商品条码："+productCode);
					return mapping.findForward("exchangeOperationAudit");
				}
				if(service.getCargoOperationCargoCount("oper_id="+exchangeBean.getId()+" and product_id="+productBarcode.getProductId())==0){
					request.setAttribute("tip", "该调拨单无此商品！");
					request.setAttribute("tip2", "该调拨单无此商品"+"<br/><br/>"+"调拨单号："+exchangeCode+"<br/>"+"商品条码："+productCode);
					return mapping.findForward("exchangeOperationAudit");
				}
				List cargoProcessList=service.getCargoOperationProcessList("operation_type=4 and process in (3,4,5,6) and use_status=1",-1,-1,"id desc");
				if(cargoProcessList.size()==0){
					request.setAttribute("tip", "时效设置错误！");
					request.setAttribute("tip2", "时效设置错误"+"<br/><br/>"+"调拨单号："+exchangeCode+"<br/>"+"商品条码："+productCode);
					return mapping.findForward("exchangeOperationAudit");
				}
				CargoOperationProcessBean cargoProcess=(CargoOperationProcessBean)cargoProcessList.get(0);
				service.updateCargoOperation("status="+cargoProcess.getId()+",last_operate_datetime='"+DateUtil.getNow()+"',auditing_datetime='"+DateUtil.getNow()+"',auditing_user_id="+user.getId()+",auditing_user_name='"+user.getUsername()+"'", "id="+exchangeBean.getId());
				request.setAttribute("tip2", "审核成功！"+"<br/>"+"调拨单号："+exchangeCode+"<br/>"+"商品条码："+productCode);
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return mapping.findForward("exchangeOperationAudit");
	}

	/**
	 *	货位调拨确认核扫描页
	 */
	public ActionForward exchangeOperationConfirm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bcmService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		String exchangeCode=StringUtil.convertNull(request.getParameter("exchangeCode"));//调拨单号
		String productCode=StringUtil.convertNull(request.getParameter("productCode"));//商品条码
		synchronized (cargoLock) {
			try{
				if(!exchangeCode.equals("")&&!productCode.equals("")){
					wareService.getDbOp().startTransaction();
					CargoOperationBean exchangeBean =service.getCargoOperation("code='"+exchangeCode+"' and type="+CargoOperationBean.TYPE3);
					if(exchangeBean==null){
						request.setAttribute("tip", "没有找到该调拨单！");
						request.setAttribute("tip2", "没有找到该调拨单"+"<br/><br/>"+"调拨单号："+exchangeCode+"<br/>"+"商品条码："+productCode);
						return mapping.findForward("exchangeOperationConfirm");
					}
					List cargoProcessList=service.getCargoOperationProcessList("operation_type=4 and process in (3,4,5,6) and use_status=1",-1,-1,"id desc");
					if(cargoProcessList.size()==0){
						request.setAttribute("tip", "时效设置错误！");
						request.setAttribute("tip2", "时效设置错误"+"<br/><br/>"+"调拨单号："+exchangeCode+"<br/>"+"商品条码："+productCode);
						return mapping.findForward("exchangeOperationConfirm");
					}
					CargoOperationProcessBean cargoProcess=(CargoOperationProcessBean)cargoProcessList.get(0);
					if(exchangeBean.getStatus()!=cargoProcess.getId()){
						request.setAttribute("tip", "调拨单状态错误！");
						request.setAttribute("tip2", "调拨单状态错误"+"<br/><br/>"+"调拨单号："+exchangeCode+"<br/>"+"商品条码："+productCode);
						return mapping.findForward("exchangeOperationConfirm");
					}
					ProductBarcodeVO productBarcode=bcmService.getProductBarcode("barcode='"+productCode+"'");
					if(productBarcode==null){
						request.setAttribute("tip", "无此商品条码！");
						request.setAttribute("tip2", "无此商品条码"+"<br/><br/>"+"调拨单号："+exchangeCode+"<br/>"+"商品条码："+productCode);
						return mapping.findForward("exchangeOperationConfirm");
					}
					if(service.getCargoOperationCargoCount("oper_id="+exchangeBean.getId()+" and product_id="+productBarcode.getProductId())==0){
						request.setAttribute("tip", "该调拨单无此商品！");
						request.setAttribute("tip2", "该调拨单无此商品"+"<br/><br/>"+"调拨单号："+exchangeCode+"<br/>"+"商品条码："+productCode);
						return mapping.findForward("exchangeOperationConfirm");
					}

					if(!service.updateCargoOperation(
							"status="+CargoOperationProcessBean.OPERATION_STATUS34+"," +
									"last_operate_datetime='"+DateUtil.getNow()+"'," +
											"complete_datetime='"+DateUtil.getNow()+"'," +
													"complete_user_id="+user.getId()+"," +
															"complete_user_name='"+user.getUsername()+"'", 
															"id="+exchangeBean.getId())){
						request.setAttribute("tip", "更新调拨单状态失败！");
						request.setAttribute("tip2", "更新调拨单状态失败！");
						service.getDbOp().rollbackTransaction();
						return mapping.findForward("exchangeOperationConfirm");
					}
					request.setAttribute("tip2", "确认成功！"+"<br/>"+"调拨单号："+exchangeCode+"<br/>"+"商品条码："+productCode);

					List<BaseProductInfo> baseList = new ArrayList<BaseProductInfo>(); //存放财务接口数据
					
					//完成货位库存量操作
					List outCocList = service.getCargoOperationCargoList("oper_id = "+exchangeBean.getId()+" and type = 1", -1, -1, "id asc");
					for(int i=0;i<outCocList.size();i++){
						CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);
						CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
						CargoInfoBean outCi = service.getCargoInfo("id = "+outCps.getCargoId());
						voProduct product = wareService.getProduct(outCoc.getProductId());
						product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
						int stockOutCount = 0;

						List inCocList = service.getCargoOperationCargoList("oper_id = "+exchangeBean.getId()+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
						for(int j=0;j<inCocList.size();j++){
							CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(j);
							CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
							CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getInCargoWholeCode()+"'");

							if(inCps!=null&&outCps!=null){
								if(!service.updateCargoProductStockCount(inCps.getId(), inCoc.getStockCount())){
									request.setAttribute("tip", "操作失败，货位冻结库存不足！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}
								if(!service.updateCargoProductStockLockCount(outCps.getId(), -inCoc.getStockCount())){
									request.setAttribute("tip", "操作失败，货位冻结库存不足！");
									request.setAttribute("result", "failure");
									service.getDbOp().rollbackTransaction();
									return mapping.findForward(IConstants.FAILURE_KEY);
								}

								//调整合格库库存
								if(inCi.getAreaId()!=outCi.getAreaId()){
									CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+inCi.getAreaId());
									ProductStockBean inProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+product.getId());
									if(inProductStock==null){
										request.setAttribute("tip", "合格库库存数据错误！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									if (!psService.updateProductStockCount(inProductStock.getId(),inCoc.getStockCount())) {
										request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
										request.setAttribute("result", "failure");
										service.getDbOp().rollbackTransaction();
										return mapping.findForward(IConstants.FAILURE_KEY);
									}
									
									ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+inCi.getAreaId()+" and type="+inCi.getStockType());
									ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+outCi.getAreaId()+" and type="+outCi.getStockType());
									
									//组装财务接口需要的数据
									BaseProductInfo baseProductInfo = new BaseProductInfo();
									baseProductInfo.setId(inCoc.getProductId());
									baseProductInfo.setProductStockOutId(psOut.getId());
									baseProductInfo.setProductStockId(psIn.getId());
									baseProductInfo.setOutCount(inCoc.getStockCount());
									baseList.add(baseProductInfo);
									
									//批次修改开始
									/**
									//更新批次记录、添加调拨出、入库批次记录
									List sbList = stockService.getStockBatchList("product_id="+outCoc.getProductId()+" and stock_type="+outCi.getStockType()+" and stock_area="+outCi.getAreaId(), -1, -1, "id asc");
									double stockinPrice = 0;
									double stockoutPrice = 0;
									if(sbList!=null&&sbList.size()!=0){
										int stockExchangeCount = inCoc.getStockCount();
										int index = 0;
										int stockBatchCount = 0;
										
										do {
											//出库
											StockBatchBean batch = (StockBatchBean)sbList.get(index);
											if(stockExchangeCount>=batch.getBatchCount()){
												if(!stockService.deleteStockBatch("id="+batch.getId())){
													request.setAttribute("tip", "数据库操作失败！");
									                request.setAttribute("result", "failure");
									                service.getDbOp().rollbackTransaction();
									                return mapping.findForward(IConstants.FAILURE_KEY);
												}
												stockBatchCount = batch.getBatchCount();
											}else{
												if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
													request.setAttribute("tip", "数据库操作失败！");
									                request.setAttribute("result", "failure");
									                service.getDbOp().rollbackTransaction();
									                return mapping.findForward(IConstants.FAILURE_KEY);
												}
												stockBatchCount = stockExchangeCount;
											}
											
											//添加批次操作记录
											StockBatchLogBean batchLog = new StockBatchLogBean();
											batchLog.setCode(exchangeBean.getCode());
											batchLog.setStockType(batch.getStockType());
											batchLog.setStockArea(batch.getStockArea());
											batchLog.setBatchCode(batch.getCode());
											batchLog.setBatchCount(stockBatchCount);
											batchLog.setBatchPrice(batch.getPrice());
											batchLog.setProductId(batch.getProductId());
											batchLog.setRemark("调拨出库");
											batchLog.setCreateDatetime(DateUtil.getNow());
											batchLog.setUserId(user.getId());
											batchLog.setSupplierId(batch.getSupplierId());
											batchLog.setTax(batch.getTax());
											if(!stockService.addStockBatchLog(batchLog)){
												 request.setAttribute("tip", "添加失败！");
									             request.setAttribute("result", "failure");
									             service.getDbOp().rollbackTransaction();
									             return mapping.findForward(IConstants.FAILURE_KEY);
											}
											
											stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
											
											//入库
											StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId());
											if(batchBean!=null){
												if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
													request.setAttribute("tip", "数据库操作失败！");
									                request.setAttribute("result", "failure");
									                service.getDbOp().rollbackTransaction();
									                return mapping.findForward(IConstants.FAILURE_KEY);
												}
											}else{
												StockBatchBean newBatch = new StockBatchBean();
												newBatch.setCode(batch.getCode());
												newBatch.setProductId(batch.getProductId());
												newBatch.setPrice(batch.getPrice());
												newBatch.setBatchCount(stockBatchCount);
												newBatch.setProductStockId(psIn.getId());
												newBatch.setStockArea(inCi.getAreaId());
												newBatch.setStockType(psIn.getType());
												newBatch.setSupplierId(batch.getSupplierId());
												newBatch.setTax(batch.getTax());
												newBatch.setNotaxPrice(batch.getNotaxPrice());
												newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
												if(!stockService.addStockBatch(newBatch)){
													request.setAttribute("tip", "添加失败！");
													request.setAttribute("result", "failure");
													service.getDbOp().rollbackTransaction();
													return mapping.findForward(IConstants.FAILURE_KEY);
												}
											}
											
											//添加批次操作记录
											batchLog = new StockBatchLogBean();
											batchLog.setCode(exchangeBean.getCode());
											batchLog.setStockType(psIn.getType());
											batchLog.setStockArea(inCi.getAreaId());
											batchLog.setBatchCode(batch.getCode());
											batchLog.setBatchCount(stockBatchCount);
											batchLog.setBatchPrice(batch.getPrice());
											batchLog.setProductId(batch.getProductId());
											batchLog.setRemark("调拨入库");
											batchLog.setCreateDatetime(DateUtil.getNow());
											batchLog.setUserId(user.getId());
											batchLog.setSupplierId(batch.getSupplierId());
											batchLog.setTax(batch.getTax());
											if(!stockService.addStockBatchLog(batchLog)){
												request.setAttribute("tip", "添加失败！");
												request.setAttribute("result", "failure");
												service.getDbOp().rollbackTransaction();
												return mapping.findForward(IConstants.FAILURE_KEY);
											}
											
											stockExchangeCount -= batch.getBatchCount();
											index++;
											
											stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
										} while (stockExchangeCount>0&&index<sbList.size());
									}
									*/
									//批次修改结束
									
									//添加进销存卡片开始
									// 入库卡片
									StockCardBean sc = new StockCardBean();
									sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
									sc.setCode(exchangeBean.getCode());

									sc.setCreateDatetime(DateUtil.getNow());
									sc.setStockType(inCi.getStockType());
									sc.setStockArea(inCi.getAreaId());
									sc.setProductId(inCps.getProductId());
									sc.setStockId(psIn.getId());
									sc.setStockInCount(inCoc.getStockCount());
									sc.setStockInPriceSum(product.getPrice5());

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
									int scId = service.getNumber("id", "stock_card", "max", "id > 0") + 1;
									sc2.setId(scId);

									sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
									sc2.setCode(exchangeBean.getCode());

									sc2.setCreateDatetime(DateUtil.getNow());
									sc2.setStockType(outCi.getStockType());
									sc2.setStockArea(outCi.getAreaId());
									sc2.setProductId(product.getId());
									sc2.setStockId(psOut.getId());
									sc2.setStockOutCount(inCoc.getStockCount());
//									sc2.setStockOutPriceSum(stockOutPrice);
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
								csc.setCode(exchangeBean.getCode());
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
								request.setAttribute("tip", "库存错误，无法提交！");
								request.setAttribute("result", "failure");
								service.getDbOp().rollbackTransaction();
								return mapping.findForward(IConstants.FAILURE_KEY);
							}
							
							if(outCi.getAreaId()!=inCi.getAreaId()){
								//更新订单缺货状态
								this.updateLackOrder(outCoc.getProductId());
							}
						}

						//调整合格库库存
						CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
						ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+product.getId());
						if(outProductStock==null){
							request.setAttribute("tip", "合格库库存数据错误！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}
						if (!psService.updateProductLockCount(outProductStock.getId(),-stockOutCount)) {
							request.setAttribute("tip", "库存操作失败，可能是库存不足，请与管理员联系！");
							request.setAttribute("result", "failure");
							service.getDbOp().rollbackTransaction();
							return mapping.findForward(IConstants.FAILURE_KEY);
						}

						//货位出库卡片
						outCps = service.getCargoProductStock("id = "+outCps.getId());
						CargoStockCardBean csc = new CargoStockCardBean();
						csc.setCardType(CargoStockCardBean.CARDTYPE_CARGOEXCHAGESTOCKOUT);
						csc.setCode(exchangeBean.getCode());
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
					
					//调用财务接口
					if(!baseList.isEmpty()){
						FinanceBaseDataService baseService = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN, service.getDbOp().getConn());
						baseService.acquireFinanceBaseData(baseList, exchangeBean.getCode(), user.getId(), 0, 0);
					}
					
					wareService.getDbOp().commitTransaction();
				}
			}catch (Exception e) {
				if(stockLog.isErrorEnabled()){
					stockLog.error("exchangeOperationConfirm error", e);
				}
				request.setAttribute("tip", "系统异常，请与管理员联系！");
				request.setAttribute("result", "failure");
				service.getDbOp().rollbackTransaction();
				return mapping.findForward(IConstants.FAILURE_KEY);
			}finally{
				service.releaseAll();
			}
		}
		return mapping.findForward("exchangeOperationConfirm");
	}
	public void updateLackOrder(int productId){
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		DbOperation dbOp2 = new DbOperation();
		dbOp2.init();
		WareService wareService = new WareService(dbOp);
		IProductPackageService ppService = ServiceFactory.createProductPackageService(IBaseService.CONN_IN_SERVICE, dbOp);
		IProductStockService service = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
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
					StockOperationAction.updateOrderLackStatu(dbOp2,order.getId());
				}

			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
			dbOp2.release();
		}

	}
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
	 * 批量添加退货上架单
	 * 
	 * 文本域中输入的格式：产品编号（或条码）\t数量
	 * 最大20行
	 * 每种商品生成一个上架单，编号HWTS，状态已审核
	 * 生成上架单之后要有成功或失败的文字提示
	 * 打印汇总单
	 * 
	 */
	public ActionForward allAddCargoUpShelf(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		IBarcodeCreateManagerService bService = ServiceFactory.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,wareService.getDbOp());
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		String allData=request.getParameter("data");//所有数据
		String msg="";//成功提示
		String errMsg="";//失败提示
		String data = "";
		synchronized (cargoLock) {
		try{
			List operList=new ArrayList();//作业单列表
			if(allData!=null){
				int wareArea  = StringUtil.toInt(request.getParameter("wareArea"));
				if(wareArea==-1){
					request.setAttribute("msg", "您没有选择所属地区或者您没有权限批量添加退货上架单");
					return mapping.findForward("allAddCargoUpShelf");
				}
				//退货库源货位
				CargoInfoAreaBean ciaBean = cargoService.getCargoInfoArea("old_id="+wareArea);
				if(ciaBean==null){
					request.setAttribute("msg", "您选择所属地区不存在相关货位信息");
					return mapping.findForward("allAddCargoUpShelf");
				}
				CargoInfoBean outCi=cargoService.getCargoInfo("area_id="+ciaBean.getId()+" and stock_type=4 and status=0");
				String tCode="";//新添的退货上架单号
				String[] dataArray=allData.split("\r\n");
				if(dataArray.length>20){
					errMsg+="最多输入20行！";
					request.setAttribute("msg", msg);
					request.setAttribute("errMsg", errMsg);
					return mapping.findForward("allAddCargoUpShelf");
				}else if(outCi==null){
					errMsg+="未找到退货库货位";
					request.setAttribute("msg", msg);
					request.setAttribute("errMsg", errMsg);
					return mapping.findForward("allAddCargoUpShelf");
				}else{
					
					for(int i=0;i<dataArray.length;i++){
						dbOp.startTransaction();
						data=dataArray[i];//每行数据
						String[] productArray=data.split("\t");//商品和数量
						if(productArray.length!=2){
							errMsg+=data;
							errMsg+=",格式错误<br/>";
							dbOp.rollbackTransaction();
							continue;
						}
						String code=productArray[0].trim();//商品编号或条码
						String count=productArray[1].trim();//数量
						if(!StringUtil.isNumeric(count)){//非数字
							errMsg+=data;
							errMsg+=",数字格式错误<br/>";
							dbOp.rollbackTransaction();
							continue;
						}
						voProduct product=null;
						ProductBarcodeVO bBean= bService.getProductBarcode("barcode="+"'"+code+"'");
						if(bBean!=null){
							product=wareService.getProduct(bBean.getProductId());
						}else{
							product=wareService.getProduct(code);
						}
						if(product==null){
							errMsg+=data;
							errMsg+=",未找到商品<br/>";
							dbOp.rollbackTransaction();
							continue;
						}
						
						//生成上架单
						CargoOperationBean coBean=new CargoOperationBean();
						String coCode = "HWTS"+DateUtil.getNow().substring(2,10).replace("-", "");
						//生成编号
						CargoOperationBean cargoOper = cargoService.getCargoOperation("code like '"+coCode+"%' order by id desc limit 1");
						if(cargoOper == null){
							coCode = coCode + "00001";
						}else{
							//获取当日计划编号最大值
							String _code = cargoOper.getCode();
							int number = Integer.parseInt(_code.substring(_code.length()-5));
							number++;
							coCode += String.format("%05d",new Object[]{new Integer(number)});
						}
						if(i==0){
							tCode=coCode+"H";
						}
						coBean.setStatus(CargoOperationProcessBean.OPERATION_STATUS3);
						coBean.setCreateDatetime(DateUtil.getNow());
						coBean.setCreateUserId(user.getId());
						coBean.setRemark("");
						coBean.setConfirmDatetime(DateUtil.getNow());
						coBean.setCode(coCode);
						coBean.setSource(tCode);//source为退货上架汇总单号
						
						coBean.setStorageCode(ciaBean.getWholeCode()); //取货位地区的缩写-例如成都-SCC
						coBean.setStockInType(0);//目的货位：散件区
						coBean.setStockOutType(2);//源货位：暂时记成缓存区
						coBean.setCreateUserName(user.getUsername());
						coBean.setType(0);
						coBean.setPrintCount(0);
						coBean.setLastOperateDatetime(DateUtil.getNow());
						coBean.setAuditingUserName("");
						coBean.setConfirmUserName("");
						coBean.setEffectStatus(0);
						if(!cargoService.addCargoOperation(coBean)){
							errMsg+=data;
							errMsg+=",生成上架单失败！<br/>";
							dbOp.rollbackTransaction();
							continue;
						}
						
						
						//选出目的货位
						CargoInfoBean targetCargo=cargoService.getTargetCargoInfo(product, Integer.parseInt(count), wareArea);
						if(targetCargo == null){
							errMsg+=data;
							errMsg+=",商品没有对应的目的货位<br/>";
							dbOp.rollbackTransaction();
							continue;
						}
						
						//目的货位是未开通或已删除
						if(targetCargo.getStatus()==2||targetCargo.getStatus()==3){
							errMsg+=data;
							errMsg+=",商品对应目的货位错误<br/>";
							dbOp.rollbackTransaction();
							continue;
						}
						if(targetCargo.getStatus()==1){//目的货位是未使用，应改成使用中
							if(!cargoService.updateCargoInfo("status=0", "id="+targetCargo.getId())){
								errMsg+=data;
								errMsg+=",修改目的货位状态时发生错误<br/>";
								dbOp.rollbackTransaction();
								continue;
							}
						}
						//找目的货位
						CargoInfoBean inCi=targetCargo;
						CargoProductStockBean inCps=null;
						CargoProductStockBean targetCps=cargoService.getCargoProductStock("cargo_id="+targetCargo.getId()+" and product_id="+product.getId());
						if(targetCps==null){//对应货位没有该商品库存记录
							CargoProductStockBean newCps=new CargoProductStockBean();
							newCps.setProductId(product.getId());
							newCps.setCargoId(inCi.getId());
							newCps.setStockCount(0);
							newCps.setStockLockCount(0);
							if(!cargoService.addCargoProductStock(newCps)){
								errMsg+=data;
								errMsg+=",添加目的货位库存信息时发生错误<br/>";
								dbOp.rollbackTransaction();
								continue;
							}
							
							inCps=newCps;
							inCps.setId(dbOp.getLastInsertId());
						}else{
							inCps=targetCps;
						}
						
						
						CargoProductStockBean outCps=cargoService.getCargoProductStock("product_id="+product.getId()+" and cargo_id="+outCi.getId());
						int coBeanId=cargoService.getCargoOperation("code='"+coCode+"'").getId();//作业单id
						
						//上架单源货位信息记录
						CargoOperationCargoBean coc1=new CargoOperationCargoBean();
						coc1.setOperId(coBeanId);
						coc1.setProductId(product.getId());
						coc1.setInCargoProductStockId(0);
						coc1.setOutCargoProductStockId(outCps.getId());
						coc1.setOutCargoWholeCode(outCi.getWholeCode());
						coc1.setStockCount(0);
						coc1.setType(1);
						coc1.setUseStatus(0);
						coc1.setCompleteCount(0);
						if(!cargoService.addCargoOperationCargo(coc1)){
							errMsg+=data;
							errMsg+=",生成上架单源货位信息失败！<br/>";
							dbOp.rollbackTransaction();
							continue;
						}
						
						//上架单目的货位信息记录
						CargoOperationCargoBean coc2=new CargoOperationCargoBean();
						coc2.setOperId(coBeanId);
						coc2.setProductId(product.getId());
						coc2.setInCargoProductStockId(inCps.getId());
						coc2.setInCargoWholeCode(inCi.getWholeCode());
						coc2.setOutCargoProductStockId(outCps.getId());
						coc2.setOutCargoWholeCode(outCi.getWholeCode());
						coc2.setStockCount(Integer.parseInt(count));
						coc2.setType(0);
						coc2.setUseStatus(1);
						coc2.setCompleteCount(0);
						if(!cargoService.addCargoOperationCargo(coc2)){
							errMsg+=data;
							errMsg+=",生成上架单目的货位信息失败！<br/>";
							dbOp.rollbackTransaction();
							continue;
						}
						
						//修改货位库存
	 					if(!cargoService.updateCargoProductStockCount(outCps.getId(), -Integer.parseInt(count))){
	 						errMsg+=data;
							errMsg+=",源货位库存不足<br/>";
							dbOp.rollbackTransaction();
							continue;
	                    }
						if(!cargoService.updateCargoProductStockLockCount(outCps.getId(), Integer.parseInt(count))){
							errMsg+=data;
							errMsg+=",源货位库存不足<br/>";
							dbOp.rollbackTransaction();
							continue;
	                    }
						
						//修改退货库库存
						CargoInfoAreaBean cargoInfoArea=cargoService.getCargoInfoArea("id="+outCi.getAreaId());
						ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_RETURN+" and product_id="+product.getId());
						if(outProductStock==null){
							errMsg+=data;
							errMsg+=",退货库库存错误<br/>";
							dbOp.rollbackTransaction();
							continue;
						}
						if (!psService.updateProductStockCount(outProductStock.getId(),-Integer.parseInt(count))) {
							errMsg+=data;
							errMsg+=",退货库库存不足<br/>";
							dbOp.rollbackTransaction();
							continue;
						}
						if (!psService.updateProductLockCount(outProductStock.getId(),Integer.parseInt(count))) {
							errMsg+=data;
							errMsg+=",退货库库存不足<br/>";
							dbOp.rollbackTransaction();
							continue;
						}
						
						msg+=coCode;
						msg+=",创建成功<br/>";
						coc2.setCargoOperation(coBean);
						coc2.setProduct(product);
						operList.add(coc2);
						
						//添加退货上架临时表数据
						ReturnedProductVirtual returnedProductVirtual = new ReturnedProductVirtual();
						returnedProductVirtual.setOperId(coBeanId);
						returnedProductVirtual.setCargoId(targetCargo.getId());
						returnedProductVirtual.setProductId(product.getId());
						cargoService.addReturnedProductVirtual(returnedProductVirtual);
						
						dbOp.commitTransaction();
					}
					
					//按货位号排序
					List newList=new ArrayList();
					for(int i=0;i<operList.size();i++){
						CargoOperationCargoBean coc=(CargoOperationCargoBean)operList.get(i);
						String cocCode=coc.getInCargoWholeCode();
						if(newList.size()==0){
							newList.add(coc);
							continue;
						}
						for(int j=newList.size()-1;j>=0;j--){
							CargoOperationCargoBean newCoc=(CargoOperationCargoBean)newList.get(j);
							String newCode=newCoc.getInCargoWholeCode();
							if(cocCode.compareTo(newCode)>0){
								newList.add(j+1, coc);
								break;
							}
							if(j==0){
								newList.add(0,coc);
								break;
							}
						}
					}
					request.setAttribute("tCode", tCode);
					request.setAttribute("operList", newList);
					request.setAttribute("msg", msg);
					request.setAttribute("errMsg", errMsg);
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			errMsg+=data;
			errMsg+=",系统异常<br/>";
			request.setAttribute("errMsg", errMsg);
			dbOp.rollbackTransaction();
			return mapping.findForward("allAddCargoUpShelf");
		}finally{
			wareService.releaseAll();
		}
		}
		return mapping.findForward("allAddCargoUpShelf");
		
	}
	
	/**
	 * 批量完成退货上架单
	 */
	public ActionForward allCompleteCargoUpShelf(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		String code=request.getParameter("code");//退货上架单号
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		WareService wareService = new WareService(dbOp);
		
		//财务基础数据
		List<BaseProductInfo> financeBaseData = null;
		try{
			
			if(code!=null&&code.trim().length()>0){
				//判断是否有权限
				synchronized (cargoLock) {
				IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
				ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
				IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
				String msg="";//成功提示
				String errMsg="";//失败提示
				List operList=service.getCargoOperationList("source='"+code+"' and status in (2,3,4,5,6) and effect_status in (0,1)", -1, -1, null);
				if(operList == null || operList.isEmpty()){
					request.setAttribute("msg", "该汇总单下上架单已经作业结束！");
					return mapping.findForward("allCompleteCargoUpShelf");
				}
				for(int operNum=0;operNum<operList.size();operNum++){
					financeBaseData = new ArrayList<BaseProductInfo>();
					service.getDbOp().startTransaction();
					CargoOperationBean cargoOperation=(CargoOperationBean)operList.get(operNum);
					//完成货位库存量操作
					List inCocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and type = 1", -1, -1, "id asc");
					boolean nextOperation=false;
					for(int i=0;i<inCocList.size();i++){
						CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(i);
						
						CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getOutCargoProductStockId());
						CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getOutCargoWholeCode()+"'");
						voProduct product = wareService.getProduct(inCoc.getProductId());
						product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
						
						//更新完成量
						if(!service.updateCargoOperationCargo("complete_count="+inCoc.getStockCount(), "id="+inCoc.getId())){
							errMsg+=cargoOperation.getCode();
		        			errMsg+=",";
		        			errMsg+="商品"+product.getCode()+"更新货位库存完成量失败!";
		        			errMsg+="<br/>";
		        			nextOperation=true;
		        			dbOp.rollbackTransaction();
		        			break;
						}
						int stockOutCount = 0;//货位库存变动
						List outCocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and out_cargo_product_stock_id = "+inCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
						for(int j=0;j<outCocList.size();j++){
							CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(j);
							CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
							//CargoProductStockBean nowInCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
							CargoProductStockBean temp_inCps = service.getCargoProductStock("id = "+outCoc.getInCargoProductStockId());
							if(temp_inCps==null){
			        			errMsg+=cargoOperation.getCode();
			        			errMsg+=",";
			        			errMsg+="商品"+product.getCode()+"货位"+outCoc.getInCargoWholeCode()+"已被清空或被其他商品占用，操作失败!";
			        			errMsg+="<br/>";
			        			nextOperation=true;
			        			dbOp.rollbackTransaction();
			        			break;
							}
							
							CargoInfoBean outCi = service.getCargoInfo("id = "+temp_inCps.getCargoId());
							if(!service.updateCargoProductStockCount(temp_inCps.getId(), (outCoc.getStockCount()-outCoc.getCompleteCount()))){
			        			errMsg+=cargoOperation.getCode();
			        			errMsg+=",";
			        			errMsg+="货位库存操作失败，货位冻结库存不足！";
			        			errMsg+="<br/>";
			        			nextOperation=true;
			        			dbOp.rollbackTransaction();
			        			break;
			                }
							if(!service.updateCargoProductStockLockCount(outCps.getId(), -(outCoc.getStockCount()-outCoc.getCompleteCount()))){
			        			errMsg+=cargoOperation.getCode();
			        			errMsg+=",";
			        			errMsg+="货位库存操作失败，货位冻结库存不足！";
			        			errMsg+="<br/>";
			        			nextOperation=true;
			        			dbOp.rollbackTransaction();
			        			break;
			                }
								
							//调整合格库库存
							if(outCi.getAreaId()!=inCi.getAreaId()||outCi.stockType!=inCi.stockType){
								CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
								//目的库库存
								ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+outCi.getStockType()+" and product_id="+inCoc.getProductId());
								if(outProductStock==null){
									errMsg+=cargoOperation.getCode();
				        			errMsg+=",";
				        			errMsg+="合格库库存数据错误！";
				        			errMsg+="<br/>";
				        			nextOperation=true;
				        			dbOp.rollbackTransaction();
				        			break;
								}
								if (!psService.updateProductStockCount(outProductStock.getId(),outCoc.getStockCount()-outCoc.getCompleteCount())) {
									errMsg+=cargoOperation.getCode();
				        			errMsg+=",";
				        			errMsg+="库存操作失败，可能是库存不足，请与管理员联系！";
				        			errMsg+="<br/>";
				        			nextOperation=true;
				        			dbOp.rollbackTransaction();
				        			break;
								}
								
								CargoInfoAreaBean outCargoInfoArea=service.getCargoInfoArea("id="+inCi.getAreaId());
								//源库库存
								ProductStockBean inProductStock=psService.getProductStock("area="+outCargoInfoArea.getOldId()+" and type="+inCi.getStockType()+" and product_id="+inCoc.getProductId());
								if(inProductStock==null){
									errMsg+=cargoOperation.getCode();
				        			errMsg+=",";
				        			errMsg+="合格库库存数据错误！";
				        			errMsg+="<br/>";
				        			nextOperation=true;
				        			dbOp.rollbackTransaction();
				        			break;
								}
								if (!psService.updateProductLockCount(inProductStock.getId(),-(outCoc.getStockCount()-outCoc.getCompleteCount()))) {
									errMsg+=cargoOperation.getCode();
				        			errMsg+=",";
				        			errMsg+="库存操作失败，可能是库存不足，请与管理员联系！";
				        			errMsg+="<br/>";
				        			nextOperation=true;
				        			dbOp.rollbackTransaction();
				        			break;
								}
								
								ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+inCi.getAreaId()+" and type="+inCi.getStockType());
								ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+outCi.getAreaId()+" and type="+outCi.getStockType());
								
								
								//财务基础数据
								BaseProductInfo base = new BaseProductInfo();
								base.setId(outCoc.getProductId());
								base.setProductStockId(psOut.getId());
								base.setProductStockOutId(psIn.getId());
								base.setOutCount(inCoc.getStockCount()-inCoc.getCompleteCount());
								financeBaseData.add(base);
								//批次修改开始
								//更新批次记录、添加调拨出、入库批次记录
//								List sbList = stockService.getStockBatchList("product_id="+outCoc.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId(), -1, -1, "id asc");
//								double stockinPrice = 0;
//								double stockoutPrice = 0;
//								if(sbList!=null&&sbList.size()!=0){
//									int stockExchangeCount = inCoc.getStockCount()-inCoc.getCompleteCount();
//									int index = 0;
//									int stockBatchCount = 0;
//										
//									do {
//										//出库
//										StockBatchBean batch = (StockBatchBean)sbList.get(index);
//										int ticket = FinanceSellProductBean.queryTicket(service.getDbOp(), batch.getCode());	//是否含票 
//										if(ticket == -1){
//											errMsg+=cargoOperation.getCode();
//						        			errMsg+=",";
//						        			errMsg+="财务数据查询异常，数据库操作失败！";
//						        			errMsg+="<br/>";
//						        			nextOperation=true;
//						        			dbOp.rollbackTransaction();
//						        			break;
//										}
//										int _count = FinanceProductBean.queryCountIfTicket(service.getDbOp(), batch.getProductId(), ticket);
//										
//										if(stockExchangeCount>=batch.getBatchCount()){
//											if(!stockService.deleteStockBatch("id="+batch.getId())){
//									            errMsg+=cargoOperation.getCode();
//							        			errMsg+=",";
//							        			errMsg+="数据库操作失败！";
//							        			errMsg+="<br/>";
//							        			nextOperation=true;
//							        			dbOp.rollbackTransaction();
//							        			break;
//											}
//											stockBatchCount = batch.getBatchCount();
//										}else{
//											if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
//									            errMsg+=cargoOperation.getCode();
//							        			errMsg+=",";
//							        			errMsg+="数据库操作失败！";
//							        			errMsg+="<br/>";
//							        			nextOperation=true;
//							        			dbOp.rollbackTransaction();
//							        			break;
//											}
//											stockBatchCount = stockExchangeCount;
//										}
//											
//										//添加批次操作记录
//										StockBatchLogBean batchLog = new StockBatchLogBean();
//										batchLog.setCode(cargoOperation.getCode());
//										batchLog.setStockType(batch.getStockType());
//										batchLog.setStockArea(batch.getStockArea());
//										batchLog.setBatchCode(batch.getCode());
//										batchLog.setBatchCount(stockBatchCount);
//										batchLog.setBatchPrice(batch.getPrice());
//										batchLog.setProductId(batch.getProductId());
//										batchLog.setRemark("调拨出库");
//										batchLog.setCreateDatetime(DateUtil.getNow());
//										batchLog.setUserId(user.getId());
//										if(!stockService.addStockBatchLog(batchLog)){
//									         errMsg+=cargoOperation.getCode();
//							        			errMsg+=",";
//							        			errMsg+="添加失败！";
//							        			errMsg+="<br/>";
//							        			nextOperation=true;
//							        			dbOp.rollbackTransaction();
//							        			break;
//										}
//										
//										//财务进销存卡片---liuruilan----2012-11-07---
//										FinanceProductBean fProduct = frfService.getFinanceProductBean("product_id =" + batch.getProductId());
//										if(fProduct == null){
//											errMsg+=cargoOperation.getCode();
//						        			errMsg+=",";
//						        			errMsg+="财务数据查询异常，数据库操作失败！";
//						        			errMsg+="<br/>";
//						        			nextOperation=true;
//						        			dbOp.rollbackTransaction();
//						        			break;
//										}
//										int currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), inCi.getType(), inCi.getAreaId(), ticket, batch.getProductId());
//										int stockAllArea = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), inCi.getAreaId(), -1, ticket, batch.getProductId());
//										int stockAllType = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, inCi.getType(), ticket, batch.getProductId());
//										FinanceStockCardBean fsc = new FinanceStockCardBean();
//										fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//										fsc.setCode(cargoOperation.getCode());
//										fsc.setCreateDatetime(DateUtil.getNow());
//										fsc.setStockType(inCi.getType());
//										fsc.setStockArea(inCi.getAreaId());
//										fsc.setProductId(batch.getProductId());
//										fsc.setStockId(psIn.getId());
//										fsc.setStockInCount(stockBatchCount);
//										fsc.setStockAllArea(stockAllArea);
//										fsc.setStockAllType(stockAllType);
//										fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//										fsc.setStockPrice(product.getPrice5());
//										
//										fsc.setCurrentStock(currentStock);
//										fsc.setType(fsc.getCardType());
//										fsc.setIsTicket(ticket);
//										fsc.setStockBatchCode(batch.getCode());
//										fsc.setBalanceModeStockCount(_count - stockBatchCount);
//										if(ticket == 0){
//											fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockBatchCount))));
//											fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
//										}
//										if(ticket == 1){
//											fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockBatchCount))));
//											fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
//										}
//										double tmpPrice = Arith.sub(Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(),fProduct.getPriceSumNoticket()))), fsc.getStockInPriceSum());
//										fsc.setAllStockPriceSum(tmpPrice);
//										if(!frfService.addFinanceStockCardBean(fsc)){
//											errMsg+=cargoOperation.getCode();
//						        			errMsg+=",";
//						        			errMsg+="添加财务卡片失败！";
//						        			errMsg+="<br/>";
//						        			nextOperation=true;
//						        			dbOp.rollbackTransaction();
//						        			break;
//										}
//										//---------------liuruilan-----------
//										
//											stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//											
//											//入库
//											StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+outCi.getStockType()+" and stock_area="+outCi.getAreaId());
//											if(batchBean!=null){
//												if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
//									                errMsg+=cargoOperation.getCode();
//								        			errMsg+=",";
//								        			errMsg+="数据库操作失败！";
//								        			errMsg+="<br/>";
//								        			nextOperation=true;
//								        			dbOp.rollbackTransaction();
//								        			break;
//												}
//											}else{
//												int _ticket = FinanceSellProductBean.queryTicket(stockService.getDbOp(), batch.getCode());
//												StockBatchBean newBatch = new StockBatchBean();
//												newBatch.setCode(batch.getCode());
//												newBatch.setProductId(batch.getProductId());
//												newBatch.setPrice(batch.getPrice());
//												newBatch.setBatchCount(stockBatchCount);
//												newBatch.setProductStockId(psOut.getId());
//												newBatch.setStockArea(outCi.getAreaId());//???
//												newBatch.setStockType(psOut.getType());//???
//												newBatch.setTicket(_ticket);
//												newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
//												if(!stockService.addStockBatch(newBatch)){
//													errMsg+=cargoOperation.getCode();
//								        			errMsg+=",";
//								        			errMsg+="添加失败！";
//								        			errMsg+="<br/>";
//								        			nextOperation=true;
//								        			dbOp.rollbackTransaction();
//								        			break;
//												}
//											}
//											
//											//添加批次操作记录
//											batchLog = new StockBatchLogBean();
//											batchLog.setCode(cargoOperation.getCode());
//											batchLog.setStockType(psOut.getType());//???
//											batchLog.setStockArea(outCi.getAreaId());//???
//											batchLog.setBatchCode(batch.getCode());
//											batchLog.setBatchCount(stockBatchCount);
//											batchLog.setBatchPrice(batch.getPrice());
//											batchLog.setProductId(batch.getProductId());
//											batchLog.setRemark("调拨入库");
//											batchLog.setCreateDatetime(DateUtil.getNow());
//											batchLog.setUserId(user.getId());
//											if(!stockService.addStockBatchLog(batchLog)){
//												errMsg+=cargoOperation.getCode();
//							        			errMsg+=",";
//							        			errMsg+="添加失败！";
//							        			errMsg+="<br/>";
//							        			nextOperation=true;
//							        			dbOp.rollbackTransaction();
//							        			break;
//											}
//											
//											//财务进销存卡片---liuruilan-----2012-11-07-----
//											int stockinCount = stockBatchCount;
//											currentStock = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), outCi.getType(), outCi.getAreaId(), ticket, batch.getProductId());
//											stockAllArea = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), outCi.getAreaId(), -1, ticket, batch.getProductId());
//											stockAllType = FinanceStockCardBean.getCurrentStockCount(service.getDbOp(), -1, outCi.getType(), ticket, batch.getProductId());
//					    					fsc = new FinanceStockCardBean();
//					    					fsc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
//					    					fsc.setCode(cargoOperation.getCode());
//					    					fsc.setCreateDatetime(DateUtil.getNow());
//					    					fsc.setStockType(batchLog.getStockType());
//					    					fsc.setStockArea(batchLog.getStockArea());
//					    					fsc.setProductId(batch.getProductId());
//					    					fsc.setStockId(psIn.getId());
//					    					fsc.setStockInCount(stockinCount);	
//					    					fsc.setCurrentStock(currentStock);	//只记录分库总库存
//					    					fsc.setStockAllArea(stockAllArea);
//					    					fsc.setStockAllType(stockAllType);
//					    					fsc.setAllStock(product.getStockAll() + product.getLockCountAll());
//					    					fsc.setStockPrice(product.getPrice5());
//					    					
//					    					fsc.setType(fsc.getCardType());
//					    					fsc.setIsTicket(ticket);
//					    					fsc.setStockBatchCode(batchLog.getBatchCode());
//					    					fsc.setBalanceModeStockCount(_count - stockBatchCount + stockinCount);
//					    					if(ticket == 0){
//												fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceHasticket(), stockinCount))));
//												fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceHasticket())));
//											}
//											if(ticket == 1){
//												fsc.setStockInPriceSum(Double.parseDouble(String.valueOf(Arith.mul(fProduct.getPriceNoticket(), stockinCount))));
//												fsc.setBalanceModeStockPrice(Double.parseDouble(String.valueOf(fProduct.getPriceNoticket())));
//											}
//					    					tmpPrice = Double.parseDouble(String.valueOf(Arith.add(fProduct.getPriceSumHasticket(), fProduct.getPriceSumNoticket())));
//					    					fsc.setAllStockPriceSum(tmpPrice);
//					    					if(!frfService.addFinanceStockCardBean(fsc)){
//					    						errMsg+=cargoOperation.getCode();
//							        			errMsg+=",";
//							        			errMsg+="添加财务卡片失败！";
//							        			errMsg+="<br/>";
//							        			nextOperation=true;
//							        			dbOp.rollbackTransaction();
//							        			break;
//					    					}
//					    					//-----------liuruilan-------------
//											
//											stockExchangeCount -= batch.getBatchCount();
//											index++;
//											
//											stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
//										} while (stockExchangeCount>0&&index<sbList.size());
//									}
									if(nextOperation){
										break;
									}
									//批次修改结束
									
									//添加进销存卡片开始
									// 入库卡片
									StockCardBean sc = new StockCardBean();
									sc.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEIN);
									sc.setCode(cargoOperation.getCode());

									sc.setCreateDatetime(DateUtil.getNow());
									sc.setStockType(outCi.getStockType());
									sc.setStockArea(outCi.getAreaId());
									sc.setProductId(outCps.getProductId());
									sc.setStockId(psOut.getId());
									sc.setStockInCount(outCoc.getStockCount()-outCoc.getCompleteCount());
									sc.setStockInPriceSum(product.getPrice5());

									sc.setCurrentStock(product.getStock(outCi.getAreaId(), sc.getStockType())
											+ product.getLockCount(outCi.getAreaId(), sc.getStockType()));
									sc.setStockAllArea(product.getStock(outCi.getAreaId())
											+ product.getLockCount(outCi.getAreaId()));
									sc.setStockAllType(product.getStockAllType(sc.getStockType())
											+ product.getLockCountAllType(sc.getStockType()));
									sc.setAllStock(product.getStockAll() + product.getLockCountAll());
									sc.setStockPrice(product.getPrice5());// 新的库存价格
									sc.setAllStockPriceSum((new BigDecimal(sc.getAllStock())).multiply(
											new BigDecimal(StringUtil.formatDouble2(sc.getStockPrice()))).doubleValue());
									if(!psService.addStockCard(sc)){
										errMsg+=cargoOperation.getCode();
					        			errMsg+=",";
					        			errMsg+="商品"+product.getCode()+"添加入库卡片失败!";
					        			errMsg+="<br/>";
					        			nextOperation=true;
					        			dbOp.rollbackTransaction();
					        			break;
									}
									
									// 出库卡片
									StockCardBean sc2 = new StockCardBean();

									sc2.setCardType(StockCardBean.CARDTYPE_STOCKEXCHANGEOUT);
									sc2.setCode(cargoOperation.getCode());

									sc2.setCreateDatetime(DateUtil.getNow());
									sc2.setStockType(inCi.getStockType());
									sc2.setStockArea(inCi.getAreaId());
									sc2.setProductId(product.getId());
									sc2.setStockId(psIn.getId());
									sc2.setStockOutCount(outCoc.getStockCount()-outCoc.getCompleteCount());
//									sc2.setStockOutPriceSum(stockOutPrice);
									sc2.setStockOutPriceSum((new BigDecimal(inCoc.getStockCount()-inCoc.getCompleteCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
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
									if(!psService.addStockCard(sc2)){
										errMsg+=cargoOperation.getCode();
					        			errMsg+=",";
					        			errMsg+="商品"+product.getCode()+"添加出库卡片失败!";
					        			errMsg+="<br/>";
					        			nextOperation=true;
					        			dbOp.rollbackTransaction();
					        			break;
									}
									//添加进销存卡片结束
								}
								
								//货位入库卡片
								temp_inCps = service.getCargoProductStock("id = "+temp_inCps.getId());
								CargoStockCardBean csc = new CargoStockCardBean();
								csc.setCardType(CargoStockCardBean.CARDTYPE_UPSHELFSTOCKIN);
								csc.setCode(cargoOperation.getCode());
								csc.setCreateDatetime(DateUtil.getNow());
								csc.setStockType(outCi.getStockType());
								csc.setStockArea(outCi.getAreaId());
								csc.setProductId(product.getId());
								csc.setStockId(temp_inCps.getId());
								csc.setStockInCount(outCoc.getStockCount()-outCoc.getCompleteCount());
								csc.setStockInPriceSum((new BigDecimal(outCoc.getStockCount()-outCoc.getCompleteCount())).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
								csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
								csc.setAllStock(product.getStockAll() + product.getLockCountAll());
								csc.setCurrentCargoStock(temp_inCps.getStockCount()+temp_inCps.getStockLockCount());
								csc.setCargoStoreType(outCi.getStoreType());
								csc.setCargoWholeCode(outCi.getWholeCode());
								csc.setStockPrice(product.getPrice5());
								csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
								if(!service.addCargoStockCard(csc)){
									errMsg+=cargoOperation.getCode();
				        			errMsg+=",";
				        			errMsg+="商品"+product.getCode()+"添加入库货位卡片失败!";
				        			errMsg+="<br/>";
				        			nextOperation=true;
				        			dbOp.rollbackTransaction();
				        			break;
								}
								
								stockOutCount = stockOutCount + (outCoc.getStockCount()-outCoc.getCompleteCount());
								//更新完成量
								if(!service.updateCargoOperationCargo("complete_count="+outCoc.getStockCount(), "id="+outCoc.getId())){
									errMsg+=cargoOperation.getCode();
				        			errMsg+=",";
				        			errMsg+="商品"+product.getCode()+"更新货位库存完成量失败!";
				        			errMsg+="<br/>";
				        			nextOperation=true;
				        			dbOp.rollbackTransaction();
				        			break;
								}
							}
							if(nextOperation==true){
								break;
							}
							
							//货位出库卡片
							inCps = service.getCargoProductStock("id = "+inCps.getId());
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
							if(!service.addCargoStockCard(csc)){
								errMsg+=cargoOperation.getCode();
			        			errMsg+=",";
			        			errMsg+="商品"+product.getCode()+"添加出库货位卡片失败!";
			        			errMsg+="<br/>";
			        			nextOperation=true;
			        			dbOp.rollbackTransaction();
			        			break;
							}
						}
					if(nextOperation==true){
						continue;
					}
					
					//财务数据
					if(financeBaseData != null && !financeBaseData.isEmpty()){
						if(financeBaseData != null && financeBaseData.size() > 0){
							FinanceBaseDataService baseService = FinanceBaseDataServiceFactory.constructFinanceBaseDataService(FinanceStockCardBean.CARDTYPE_STOCKEXCHANGEIN, service.getDbOp().getConn());
							baseService.acquireFinanceBaseData(financeBaseData, cargoOperation.getCode(), user.getId(), 0, 0);
						}
					}
					
					CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
					CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS7);//下个阶段
					if(process==null){
		    			errMsg+=cargoOperation.getCode();
	        			errMsg+=",";
	        			errMsg+="作业单流程信息错误！";
	        			errMsg+="<br/>";
	        			nextOperation=true;
	        			dbOp.rollbackTransaction();
	        			continue;
					}
					if(process2==null){
		    			errMsg+=cargoOperation.getCode();
	        			errMsg+=",";
	        			errMsg+="作业单流程信息错误！";
	        			errMsg+="<br/>";
	        			nextOperation=true;
	        			dbOp.rollbackTransaction();
	        			continue;
					}
					
					//修改上一操作日志的时效
					CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+cargoOperation.getId()+" order by id desc limit 1");//当前作业单的最后一条日志
					if(lastLog!=null&&lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
						int effectTime=process.getEffectTime();//上阶段时效
						String lastOperateTime=lastLog.getOperDatetime();
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						long date1=sdf.parse(lastOperateTime).getTime();
						long date2=sdf.parse(DateUtil.getNow()).getTime();
						if(date1+effectTime*60*1000<date2){//已超时
							service.updateCargoOperLog("effect_time=2", "id="+lastLog.getId());
						}
					}
					
					service.updateCargoOperation("status="+CargoOperationProcessBean.OPERATION_STATUS7+",effect_status="+CargoOperationBean.EFFECT_STATUS2+",last_operate_datetime='"+DateUtil.getNow()+"'"+",complete_datetime='"+DateUtil.getNow()+"',complete_user_id="+user.getId()+",complete_user_name='"+user.getUsername()+"'", "id="+cargoOperation.getId());
					
					CargoOperLogBean operLog=new CargoOperLogBean();
					operLog.setOperId(cargoOperation.getId());
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
					
					msg+=cargoOperation.getCode();
					msg+="，作业完成";
					msg+="<br/>";
					service.getDbOp().commitTransaction();
				}
				request.setAttribute("msg", msg);
				request.setAttribute("errMsg", errMsg);
		
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			request.setAttribute("tip", "系统异常，请联系管理员！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}finally{
			wareService.releaseAll();
		}
		return mapping.findForward("allCompleteCargoUpShelf");
		
	}
	
	/**
	 * 批量确认提交退货上架单
	 * 
	 * 
	 */
	public ActionForward allConfirmCargoOperation(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
//		UserGroupBean group = user.getGroup();
		WareService wareService = new WareService();
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService prudoctService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		
		String allData=request.getParameter("data");//所有数据
		synchronized (cargoLock) {
		try{
			String msg="";//成功提示
			String errMsg="";//失败提示
			
			if(allData!=null){
				String[] dataArray=allData.split("\r\n");
				for(int d=0;d<dataArray.length;d++){
					service.getDbOp().startTransaction();
					boolean nextOperation=false;
					String data=dataArray[d];//每行数据
					CargoOperationBean cargoOperation = service.getCargoOperation("code = '"+data+"'");
					if(cargoOperation == null){
						errMsg+=data;
						errMsg+=",作业单不存在<br/>";
						service.getDbOp().rollbackTransaction();
						continue;
					}
					if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS1){
						errMsg+=data;
						errMsg+=",已确认<br/>";
						service.getDbOp().rollbackTransaction();
						continue;
					}
					int operId=cargoOperation.getId();
					StockExchangeBean seb = prudoctService.getStockExchange(" code ='"+cargoOperation.getSource()+"'");	
					service.updateCargoOperation("status = "+CargoOperationProcessBean.OPERATION_STATUS2+",effect_status = "+CargoOperationBean.EFFECT_STATUS0+",confirm_datetime='"+DateUtil.getNow()+"',confirm_user_name='"+user.getUsername()+"',last_operate_datetime='"+DateUtil.getNow()+"'", "id = "+operId);
					List inCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and type = 1", -1, -1, "id asc");
					for(int i=0;i<inCocList.size();i++){
						CargoOperationCargoBean outCoc = (CargoOperationCargoBean)inCocList.get(i);
						//CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getOutCargoProductStockId());
						CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+outCoc.getOutCargoWholeCode()+"'");
//						voProduct outProduct=wareService.getProduct(outCoc.getProductId());//商品
						CargoOperationLogBean logBean = new CargoOperationLogBean();//作业单操作记录
						logBean.setOperId(cargoOperation.getId());
						logBean.setOperAdminId(user.getId());
						logBean.setOperAdminName(user.getUsername());
						logBean.setOperDatetime(DateUtil.getNow());
						logBean.setRemark("确认提交");
						service.addCargoOperationLog(logBean);
						List outCocList = service.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
						for(int j=0;j<outCocList.size();j++){
							CargoOperationCargoBean inCoc = (CargoOperationCargoBean)outCocList.get(j);
							 
							CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getOutCargoProductStockId());//原货位商品cps
							CargoProductStockBean temp_inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId()); //目的货位商品cps
							if(temp_inCps==null){
								errMsg+=data;
								errMsg+=",没有目的货位库存记录<br/>";
								service.getDbOp().rollbackTransaction();
								nextOperation=true;
								break;
							}
							CargoInfoBean outCi = service.getCargoInfo("id = "+temp_inCps.getCargoId()); //目标货位
		 					if(!service.updateCargoProductStockCount(inCps.getId(), -inCoc.getStockCount())){
		        				errMsg+=data;
								errMsg+=",源货位库存锁定失败<br/>";
								service.getDbOp().rollbackTransaction();
								nextOperation=true;
								break;
		                    }
							if(!service.updateCargoProductStockLockCount(inCps.getId(), inCoc.getStockCount())){
		        				errMsg+=data;
								errMsg+=",源货位冻结量锁定失败<br/>";
								service.getDbOp().rollbackTransaction();
								nextOperation=true;
								break;
		                    }
							//改变调拨单的冻结数量
							if(seb!=null){
								StockExchangeProductBean sepBean = prudoctService.getStockExchangeProduct("stock_exchange_id ="+seb.getId()+" and product_id="+temp_inCps.getProductId());
								 // 未上架量 少于 将要上架量 则上架失败
								if((sepBean.getNoUpCargoCount()-inCoc.getStockCount())<0 || (sepBean.getNoUpCargoCount()-sepBean.getUpCargoLockCount()-inCoc.getStockCount())< 0){
			        				errMsg+=data;
									errMsg+=",调拨单中商品数量不足<br/>";
									service.getDbOp().rollbackTransaction();
									nextOperation=true;
									break;
								}
								
								service.updateProductStockLockCount(sepBean.getId(),inCoc.getStockCount());
							}
							
							
							//调整合格库库存
							if(outCi.getAreaId()!=inCi.getAreaId()||outCi.getStockType()!=inCi.getStockType()){
								CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+inCi.getAreaId());
								ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+inCi.getStockType()+" and product_id="+inCoc.getProductId());
								if(outProductStock==null){
									errMsg+=data;
									errMsg+=",没有合格库库存数据<br/>";
									service.getDbOp().rollbackTransaction();
									nextOperation=true;
									break;
								}
								if (!psService.updateProductStockCount(outProductStock.getId(),-inCoc.getStockCount())) {
									errMsg+=data;
									errMsg+=",源库库存减少失败<br/>";
									service.getDbOp().rollbackTransaction();
									nextOperation=true;
									break;
								}
								if (!psService.updateProductLockCount(outProductStock.getId(),inCoc.getStockCount())) {
									errMsg+=data;
									errMsg+=",源库库存锁定失败<br/>";
									service.getDbOp().rollbackTransaction();
									nextOperation=true;
									break;
								}
							}
						}
						if(nextOperation==true){
							break;
						}
						//删除coc无用数据
						service.deleteCargoOperationCargo("oper_id = "+operId+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 0");
					}
					if(nextOperation==true){
						continue;
					}
					
					//修改上一操作日志的时效
					CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+operId+" order by id desc limit 1");//当前作业单的最后一条日志
					if(lastLog!=null&&lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
						CargoOperationProcessBean tempProcess=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//生成作业单
						int effectTime=tempProcess.getEffectTime();//生成阶段时效
						String lastOperateTime=lastLog.getOperDatetime();
						SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						long date1=sdf.parse(lastOperateTime).getTime();
						long date2=sdf.parse(DateUtil.getNow()).getTime();
						if(date1+effectTime*60*1000<date2){//已超时
							service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
						}
					}
					
					
					CargoOperLogBean operLog=new CargoOperLogBean();
					operLog.setOperId(operId);
					operLog.setOperCode(cargoOperation.getCode());
					CargoOperationProcessBean process=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS1);
					CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS2);
					operLog.setOperName(process2.getOperName());
					operLog.setOperDatetime(DateUtil.getNow());
					operLog.setOperAdminId(user.getId());
					operLog.setOperAdminName(user.getUsername());
					operLog.setHandlerCode("");
					operLog.setEffectTime(0);
					operLog.setRemark("");
					operLog.setPreStatusName(process.getStatusName());
					operLog.setNextStatusName(process2.getStatusName());
					service.addCargoOperLog(operLog);
					
					service.getDbOp().commitTransaction();
		
					msg+=data;
					msg+=",已确认提交<br/>";
				}
				request.setAttribute("msg", msg);
				request.setAttribute("errMsg", errMsg);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		}
		return mapping.findForward("allConfirmCargoOperation");
	}
	
	/**
	 * 采购上架统计
	 * @author 刘人华
	 */
	public ActionForward stockUpShelfStatics(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if("".equals(StringUtil.convertNull(request.getParameter("qryFlag")))){
			return mapping.findForward("stockUpShelfStatics");
		}
		request.setAttribute("list", this.getStaticQryRs(request,this.getStaticQrySql(request, ProductStockBean.STOCKTYPE_CHECK)));
		return mapping.findForward("stockUpShelfStatics");
	}
	
	/**
	 * 退货上架统计
	 * @author 刘人华
	 */
	public ActionForward retShelfStatics(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		if("".equals(StringUtil.convertNull(request.getParameter("qryFlag")))){
			return mapping.findForward("retShelfStatics");
		}
		request.setAttribute("list", this.getStaticQryRs(request,this.getStaticQrySql(request, ProductStockBean.STOCKTYPE_RETURN)));
		return mapping.findForward("retShelfStatics");
	}
	
	/**
	 * 采购上架统计导出
	 * @author 刘人华
	 */
	public ActionForward stockUpShelfStaticsExport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		this.exportStaticsData(response, this.getStaticQryRs(request,this.getStaticQrySql(request, ProductStockBean.STOCKTYPE_CHECK)),"采购上架统计");
		return null;
	}
	
	/**
	 * 退货上架统计导出
	 * @author 刘人华
	 */
	public ActionForward retShelfStaticsExport(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		this.exportStaticsData(response, this.getStaticQryRs(request,this.getStaticQrySql(request, ProductStockBean.STOCKTYPE_RETURN)),"退货上架统计");
		return null;
	}	

	/**
	 * poi导出上架统计数据
	 */
	private void exportStaticsData(HttpServletResponse response,List<Map<String,String>> list,String fileName){
		
		ExportExcel excel = new ExportExcel(1);
		//设置表头
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		header.add("序号");
		header.add("库地区");
		header.add("制单人");
		header.add("制单时间");
		header.add("操作人");
		header.add("确认完成时间");
		header.add("上架单数量");
		header.add("SKU数量");
		header.add("商品件数");
		headers.add(header);
		//设置body
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		if(list!=null&&list.size()>0){
			int upSelfNumSum = 0,skuNumSum = 0,productNum = 0,count=0;
			for(Map<String,String> map:list){
				//合计数据
				upSelfNumSum+=Integer.valueOf(map.get("upSelfNum")).intValue();
				skuNumSum+=Integer.valueOf(map.get("skuNum")).intValue();
				productNum+=Integer.valueOf(map.get("productNum")).intValue();
				//每行数据
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(String.valueOf(++count));
				temp.add(map.get("areaName"));
				temp.add(map.get("create_user_name"));
				temp.add(map.get("create_datetime"));
				temp.add(map.get("complete_user_name"));
				temp.add(map.get("complete_datetime"));
				temp.add(map.get("upSelfNum"));
				temp.add(map.get("skuNum"));
				temp.add(map.get("productNum"));
				bodies.add(temp);
			}
			//添加合计行
			ArrayList<String> temp = new ArrayList<String>();
			temp.add("合计：");
			for(int i=1;i<header.size()-3;i++){
				temp.add("");
			}
			temp.add(String.valueOf(upSelfNumSum));
			temp.add(String.valueOf(skuNumSum));
			temp.add(String.valueOf(productNum));
			bodies.add(temp);			
		}
		
		// 调用填充表头方法
		excel.buildListHeader(headers);
		// 调用填充数据区方法
		excel.buildListBody(bodies);
		// 文件输出
		try {
			excel.exportToExcel(fileName, response, "");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 获取统计数据
	 * @author 刘人华
	 * @return
	 */
	private List<Map<String,String>> getStaticQryRs(HttpServletRequest request,String sql){
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		if("".equals(sql)){
			return list;
		}
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		try {
			ResultSet rs = wareService.getDbOp().executeQuery(sql);
			while(rs.next()){
				Map<String,String> map = new HashMap<String,String>();
				map.put("areaName", ProductStockBean.areaMap.get(rs.getInt("area_id")));
				map.put("create_user_name", StringUtil.convertNull(rs.getString("create_user_name")));
				map.put("create_datetime", DateUtil.formatDate(DateUtil.parseDate(rs.getString("create_datetime"))));
				map.put("complete_user_name", StringUtil.convertNull(rs.getString("complete_user_name")));
				map.put("complete_datetime", DateUtil.formatDate(DateUtil.parseDate(rs.getString("complete_datetime"))));
				map.put("upSelfNum", StringUtil.convertNull(rs.getString("upSelfNum")));
				map.put("skuNum", StringUtil.convertNull(rs.getString("skuNum")));
				map.put("productNum", StringUtil.convertNull(rs.getString("productNum")));
				list.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return list;
	}	
	
	/**
	 * 获取上架统计sql
	 * stockType 1:采购上架统计 4：退货上架统计
	 * @author 刘人华
	 */
	private String getStaticQrySql(HttpServletRequest request,int stockType){
		//库地区
		String wareArea = StringUtil.convertNull(request.getParameter("wareArea"));
		//制单人
		String createUserName = StringUtil.convertNull(request.getParameter("createUserName"));
		//制单开始时间
		String createDateStart = StringUtil.convertNull(request.getParameter("createDateStart"));
		//制单结束时间
		String createDateEnd = StringUtil.convertNull(request.getParameter("createDateEnd"));
		//操作人
		String userName = StringUtil.convertNull(request.getParameter("userName"));
		//确认开始时间
		String completeDateStart = StringUtil.convertNull(request.getParameter("completeDateStart"));
		//确认结束时间
		String completeDateEnd = StringUtil.convertNull(request.getParameter("completeDateEnd"));
		//输入限制验证
		if(!(!"".equals(wareArea)
				&&((!"".equals(createUserName)&&!"".equals(createDateStart)&&!"".equals(createDateEnd)
						&&"".equals(userName)&&"".equals(completeDateStart)&&"".equals(completeDateEnd))||
						("".equals(createUserName)&&"".equals(createDateStart)&&"".equals(createDateEnd)
								&&!"".equals(userName)&&!"".equals(completeDateStart)&&!"".equals(completeDateEnd))))){
			return "";
		}
		//产品编号
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));

		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT");
		sb.append(" 	ci.area_id,");
		if(!"".equals(createUserName)){
			sb.append(" 	co.create_user_name,");
			sb.append(" 	co.create_datetime,");
			sb.append(" 	'' as complete_user_name,");
			sb.append(" 	'' as complete_datetime,");			
		}
		if(!"".equals(userName)){
			sb.append(" 	'' as create_user_name,");
			sb.append(" 	'' as create_datetime,");
			sb.append(" 	co.complete_user_name,");
			sb.append(" 	co.complete_datetime,");
		}
		sb.append(" 	count(DISTINCT co.id) upSelfNum,");
		sb.append(" 	count(DISTINCT coc.product_id) skuNum,");
		sb.append(" 	sum(coc.stock_count) productNum");
		sb.append(" FROM");
		sb.append(" 	cargo_operation co");
		sb.append(" JOIN cargo_operation_cargo coc ON co.id = coc.oper_id");
		sb.append(" JOIN cargo_info ci ON ci.whole_code = coc.out_cargo_whole_code");
		sb.append(" AND ci.stock_type = '"+stockType+"'");
		sb.append(" WHERE");
		sb.append(" 	1 = 1");
		sb.append(" AND co. STATUS IN (7, 8)");
		sb.append(" AND coc.use_status = 1");
		if(!"".equals(productCode)){
			sb.append(" AND coc.product_id in ( SELECT DISTINCT id FROM product p WHERE p.CODE = '"+productCode+"')");
		}
		if(!"".equals(wareArea)&&!"-1".equals(wareArea)){
			sb.append(" AND ci.area_id = "+wareArea);
		}
		if(!"".equals(createUserName)){
			sb.append(" AND co.create_user_name = '"+createUserName + "'");
		}
		if(!"".equals(createDateStart)){
			sb.append(" AND co.create_datetime >= '"+createDateStart + " 00:00:00'");
		}
		if(!"".equals(createDateEnd)){
			sb.append(" AND co.create_datetime <= '"+createDateEnd + " 23:59:59'");
		}
		if(!"".equals(userName)){
			sb.append(" AND co.complete_user_name = '"+userName + "'");
		}
		if(!"".equals(completeDateStart)){
			sb.append(" AND co.complete_datetime >= '"+completeDateStart + " 00:00:00'");
		}
		if(!"".equals(completeDateEnd)){
			sb.append(" AND co.complete_datetime <= '"+completeDateEnd + " 23:59:59'");
		}
		sb.append(" GROUP BY");
		sb.append(" 	ci.area_id,");
		if(!"".equals(createUserName)){
			sb.append(" 	left(co.create_datetime,10)");
		}
		if(!"".equals(userName)){
			sb.append(" 	left(co.complete_datetime,10)");
		}
		sb.append(" ORDER BY");
		if(!"".equals(createUserName)){
			sb.append(" 	co.create_datetime");
		}
		if(!"".equals(userName)){
			sb.append(" 	co.complete_datetime");
		}
		return sb.toString();
	}
}

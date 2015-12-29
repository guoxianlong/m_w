package mmb.stock.stat;

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

import mmb.stock.cargo.CargoOperationTodoBean;
import mmb.stock.cargo.CartonningInfoService;
import mmb.ware.WareService;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.bybs.BsbyOperationRecordBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.bybs.BsbyProductCargoBean;
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
import adultadmin.bean.order.OrderStockProductCargoBean;
import adultadmin.bean.stock.CargoStockCardBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.IAdminService;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 分播异常处理Service
 * @author Administrator
 *
 */
public class SortingAbnormalDisposeService extends BaseServiceImpl {
	
	public SortingAbnormalDisposeService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public SortingAbnormalDisposeService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	//分播异常信息
	public boolean addSortingAbnormal(SortingAbnormalBean bean) {
		return addXXX(bean, "sorting_abnormal");
	}

	public List<SortingAbnormalBean> getSortingAbnormalList(String condition, int index, int count, String orderBy) {
		return (List<SortingAbnormalBean>)getXXXList(condition, index, count, orderBy, "sorting_abnormal", "mmb.stock.stat.SortingAbnormalBean");
	}
	
	public int getSortingAbnormalCount(String condition) {
		return getXXXCount(condition, "sorting_abnormal", "id");
	}

	public SortingAbnormalBean getSortingAbnormal(String condition) {
		return (SortingAbnormalBean) getXXX(condition, "sorting_abnormal",
		"mmb.stock.stat.SortingAbnormalBean");
	}

	public boolean updateSortingAbnormal(String set, String condition) {
		return updateXXX(set, condition, "sorting_abnormal");
	}

	public boolean deleteSortingAbnormal(String condition) {
		return deleteXXX(condition, "sorting_abnormal");
	}
	
	//分播异常商品 信息
	public boolean addSortingAbnormalProduct(SortingAbnormalProductBean bean) {
		return addXXX(bean, "sorting_abnormal_product");
	}

	public List<SortingAbnormalProductBean> getSortingAbnormalProductList(String condition, int index, int count, String orderBy) {
		return (List<SortingAbnormalProductBean>)getXXXList(condition, index, count, orderBy, "sorting_abnormal_product", "mmb.stock.stat.SortingAbnormalProductBean");
	}
	
	public int getSortingAbnormalProductCount(String condition) {
		return getXXXCount(condition, "sorting_abnormal_product", "id");
	}

	public SortingAbnormalProductBean getSortingAbnormalProduct(String condition) {
		return (SortingAbnormalProductBean) getXXX(condition, "sorting_abnormal_product",
		"mmb.stock.stat.SortingAbnormalProductBean");
	}

	public boolean updateSortingAbnormalProduct(String set, String condition) {
		return updateXXX(set, condition, "sorting_abnormal_product");
	}

	public boolean deleteSortingAbnormalProduct(String condition) {
		return deleteXXX(condition, "sorting_abnormal_product");
	}
	
	//异常货位盘点
	public boolean addAbnormalCargoCheck(AbnormalCargoCheckBean bean) {
		return addXXX(bean, "abnormal_cargo_check");
	}

	public List<AbnormalCargoCheckBean> getAbnormalCargoCheckList(String condition, int index, int count, String orderBy) {
		return (List<AbnormalCargoCheckBean>)getXXXList(condition, index, count, orderBy, "abnormal_cargo_check", "mmb.stock.stat.AbnormalCargoCheckBean");
	}
	
	public int getAbnormalCargoCheckCount(String condition) {
		return getXXXCount(condition, "abnormal_cargo_check", "id");
	}

	public AbnormalCargoCheckBean getAbnormalCargoCheck(String condition) {
		return (AbnormalCargoCheckBean) getXXX(condition, "abnormal_cargo_check",
		"mmb.stock.stat.AbnormalCargoCheckBean");
	}

	public boolean updateAbnormalCargoCheck(String set, String condition) {
		return updateXXX(set, condition, "abnormal_cargo_check");
	}

	public boolean deleteAbnormalCargoCheckBean(String condition) {
		return deleteXXX(condition, "abnormal_cargo_check");
	}
	
	//异常货位盘点商品
	public boolean addAbnormalCargoCheckProduct(AbnormalCargoCheckProductBean bean) {
		return addXXX(bean, "abnormal_cargo_check_product");
	}

	public List<AbnormalCargoCheckProductBean> getAbnormalCargoCheckProductList(String condition, int index, int count, String orderBy) {
		return (List<AbnormalCargoCheckProductBean>)getXXXList(condition, index, count, orderBy, "abnormal_cargo_check_product", "mmb.stock.stat.AbnormalCargoCheckProductBean");
	}
	
	public int getAbnormalCargoCheckProductCount(String condition) {
		return getXXXCount(condition, "abnormal_cargo_check_product", "id");
	}

	public AbnormalCargoCheckProductBean getAbnormalCargoCheckProduct(String condition) {
		return (AbnormalCargoCheckProductBean) getXXX(condition, "abnormal_cargo_check_product",
		"mmb.stock.stat.AbnormalCargoCheckProductBean");
	}

	public boolean updateAbnormalCargoCheckProduct(String set, String condition) {
		return updateXXX(set, condition, "abnormal_cargo_check_product");
	}

	public boolean deleteAbnormalCargoCheckProductBean(String condition) {
		return deleteXXX(condition, "abnormal_cargo_check_product");
	}
	public boolean updateSortingBatchOrderProduct(String set, String condition) {
		return updateXXX(set, condition, "sorting_batch_order_product");
	}
	
	/**
	 * 根据出库单号，找到对应的出库单的信息， 重点是组织数据时以货位为主导。
	 * @param code
	 * @return
	 */
	public Object getOrderStockAllInfoByCode(String code) {
		IStockService istockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,this.dbOp);
		OrderStockBean osBean = istockService.getOrderStock("code='" + code + "'");
		if( osBean != null ) {
			//以货位为主导的 存放出库物品 
			List<OrderStockProductCargoBean> orderStockProductCargos = istockService.getOrderStockProductCargoList("order_stock_id=" + osBean.getId(), -1, -1, "cargo_whole_code desc");
			int x = orderStockProductCargos.size();
			for( int i =0 ; i < x ; i ++ ) {
				OrderStockProductCargoBean ospcBean = orderStockProductCargos.get(i);
				OrderStockProductBean ospBean = istockService.getOrderStockProduct("id=" + ospcBean.getOrderStockProductId());
				ospcBean.setOrderStockProductBean(ospBean);
			}
			osBean.setOrderStockProductCargList(orderStockProductCargos);
		} else {
			return "没有找到对应的出库单信息！";
		}
		
		return osBean;
	}

	//
	public OrderStockBean getOrderStock (String condition) {
		return (OrderStockBean) getXXX(condition, "order_stock",
		"adultadmin.bean.order.OrderStockBean");
	}
	public SortingBatchGroupBean getSortingBatchGroupBean (String condition) {
		return (SortingBatchGroupBean) getXXX(condition, "sorting_batch_group",
		"mmb.stock.stat.SortingBatchGroupBean");
	}
	public SortingBatchBean getSortingBatch (String condition) {
		return (SortingBatchBean) getXXX(condition, "sorting_batch",
		"mmb.stock.stat.SortingBatchBean");
	}
	public SortingBatchOrderBean getSortingBatchOrder (String condition) {
		return (SortingBatchOrderBean) getXXX(condition, "sorting_batch_order",
		"mmb.stock.stat.SortingBatchOrderBean");
	}
	public SortingBatchOrderProductBean getSortingBatchOrderProductt(String condition) {
		return (SortingBatchOrderProductBean)getXXX(condition ,"sorting_batch_order_product", "mmb.stock.stat.SortingBatchOrderProductBean");
	}
	@SuppressWarnings("unchecked")
	public List<SortingBatchOrderProductBean> getSortingBatchOrderProducttList(String condition, int index, int count, String orderBy) {
		return (List<SortingBatchOrderProductBean>)getXXXList(condition, index, count, orderBy, "sorting_batch_order_product", "mmb.stock.stat.SortingBatchOrderProductBean");
	}
	/**
	 * 根据CK  code得到对应类型为 ‘撤单’的异常单的信息
	 * @param code
	 * @return
	 */
	public Object getSortingAbnormalAllInfo(String code) {
		
		SortingAbnormalBean saBean = this.getSortingAbnormal("oper_code='" + code + "' and oper_type=" + SortingAbnormalBean.OPERTYPE0 + " and abnormal_type =" + SortingAbnormalBean.ABNORMALTYPE0 );
		if( saBean != null ) {
			List<SortingAbnormalProductBean> sortingAbnormalProducts = this.getSortingAbnormalProductList("sorting_abnormal_id=" + saBean.getId(), -1, -1, "last_oper_datetime desc, cargo_whole_code desc");
			int x = sortingAbnormalProducts.size();
			for( int i = 0 ; i < x; i++ ) {
				SortingAbnormalProductBean sapBean = sortingAbnormalProducts.get(i);
				voProduct product = this.getProductById(sapBean.getProductId());
				sapBean.setProduct(product);
			}
			saBean.setSortingAbnormalProductList(sortingAbnormalProducts);
		} else {
			return "没有找到对应的异常单！";
		}
		return saBean;
	}

	/**
	 * 找到对应的异常商品记录中，货位号与 所传货位号相同的所有货位号，并组成一个list 
	 * @param sortingAbnormalProductList
	 * @param cargoWholeCode
	 * @return
	 */
	public List<SortingAbnormalProductBean> getIsCargoInList(
			List<SortingAbnormalProductBean> sortingAbnormalProductList,
			String cargoWholeCode) {
		List<SortingAbnormalProductBean> result = new ArrayList<SortingAbnormalProductBean>();
		
		int x = sortingAbnormalProductList.size();
		for( int i = 0 ;  i < x; i ++ ) {
			SortingAbnormalProductBean sapBean = sortingAbnormalProductList.get(i);
			if( sapBean.getCargoWholeCode().equals(cargoWholeCode) ) {
				result.add(sapBean);
			}
		}
		return result;
	}

	public SortingAbnormalProductBean getIsProductOnCargo(
			List<SortingAbnormalProductBean> list, int id) {
		SortingAbnormalProductBean sapBean = null;
		int x = list.size();
		for( int i = 0 ; i < x; i ++ ) {
			SortingAbnormalProductBean temp = list.get(i);
			if( temp.getProductId() == id ) {
				sapBean = temp;
			}
		}
		return sapBean;
	}
	/**
	 * 作者：石远飞
	 * 
	 * 日期：2013-6-07
	 * 
	 * 说明：根据扫描的商品获取SortingAgainBean
	 */
	public SortingAgainBean getSortingAgainBean(List<SortingAgainBean> saBeanList,String productCode){
		for(SortingAgainBean bean : saBeanList){
			if(productCode.equals(bean.getProudctCode())){
				return bean;
			}
		}
		return null;
	}
	/**
	 * 作者：石远飞
	 * 
	 * 日期：2013-6-07
	 * 
	 * 说明：根据扫描的商品获取这个商品对应的货位
	 */
	public String getInCargoWholeCode(List<SortingAgainBean> saBeanList,String productCode){
		String inCargoWholeCode = null;
		for(SortingAgainBean bean : saBeanList){
			if(productCode.equals(bean.getProudctCode())){
				inCargoWholeCode = bean.getWholeCode();
				break;
			}
		}
		return inCargoWholeCode;
	}
	/**
	 * 作者：石远飞
	 * 
	 * 日期：2013-6-07
	 * 
	 * 说明：检查扫描的商品和货位是不是配对的 并返回分拣波次订单商品ID
	 */
	public int checkWholeAndProduct(List<SortingAgainBean> saBeanList,String wholeCode,String productCode){
		int sortingBatchOrderProductId = -1;
		for(SortingAgainBean bean : saBeanList){
			if(wholeCode.equals(bean.getWholeCode()) && productCode.equals(bean.getProudctCode())){
				sortingBatchOrderProductId = bean.getSortingBatchOrderProductId();
				break;
			}
		}
		return sortingBatchOrderProductId;
	}
	/**
	 * 作者：石远飞
	 * 
	 * 日期：2013-6-07
	 * 
	 * 说明：检查扫描的商品是否在列表中
	 */
	public boolean checkProduct(List<SortingAgainBean> saBeanList,String prodcutCode){
		boolean flag = false;
		for(SortingAgainBean bean : saBeanList){
			if(prodcutCode.equals(bean.getProudctCode())){
				flag = true;
				break;
			}
		}
		return flag;
	}
	/**
	 * 说明：获取货位异常数据
	 * 
	 * 日期：2013-6-15
	 * 
	 * 作者：石远飞
	 */
	@SuppressWarnings("unchecked")
	public List<SortingAgainBean> getSortingAgainListByOSP(List<OrderStockProductBean> ospBeanList,DbOperation dbOp,int areaId ){
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoServie = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,dbOp);
		List<SortingAgainBean> saBeanList = new ArrayList<SortingAgainBean>();
		try {
			for(OrderStockProductBean ospBean : ospBeanList){
				voProduct vpBean = this.getProductById(ospBean.getProductId());
				if(vpBean == null){
					continue;
				}
				OrderStockProductCargoBean ospcBean = stockService.getOrderStockProductCargo("order_stock_product_id=" + ospBean.getId());
				if(ospcBean == null){
					continue;
				}
				CargoInfoBean ciBean = cargoServie.getCargoInfo("whole_code='" + ospcBean.getCargoWholeCode() + "'");
				if(ciBean == null){
					continue;
				}
				//查找可替代货位
				List<CargoProductStockBean> cpsBeanList = cargoServie.getCargoProductStockList("product_id=" + ospBean.getProductId() 
						+ " and cargo_id<>" + ciBean.getId() + " and stock_count>0", -1, -1, " stock_count DESC");
				String recommendWhole = "";
				if(cpsBeanList != null && cpsBeanList.size() > 0){
					int count = 0;
					for(CargoProductStockBean bean : cpsBeanList){
						CargoInfoBean cargoInfo = cargoServie.getCargoInfo("id=" + bean.getCargoId() + " and area_id=" + areaId + " and stock_type=" 
							+ CargoInfoBean.STOCKTYPE_QUALIFIED + " and (store_type=0 or store_type=4)"); //看这些放着相同商品的货位是不是一个地区的 如果不是 则不符合要求
						if(cargoInfo != null){
							if(cargoInfo.getWholeCode() != null ){
								StringBuffer sb = new StringBuffer();
								String newWholeCode = cargoInfo.getWholeCode().substring(cargoInfo.getWholeCode().indexOf("-")+1);
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
								recommendWhole += sb.toString();
								recommendWhole += "(" + bean.getStockCount() + ")";
								recommendWhole += "||";
								count ++;
							}
						}
						if(count == 2){//只显示两个替代货位 所以当够两个之后就跳出循环
							break;
						}
					}
					if(count == 0){
						recommendWhole += "没有可替代的货位！";
					}
				}else{
					recommendWhole += "没有可替代的货位！";
				}
				SortingAgainBean saBean = new SortingAgainBean();
				saBean.setSortingBatchOrderProductId(0);//因为这种单sku的不涉及这个属性 所以设置0填充
				saBean.setCount(ospBean.getStockoutCount());
				saBean.setProudctCode(vpBean.getCode());
				saBean.setWholeCode(ospcBean.getCargoWholeCode());
				saBean.setRecommendWhole(recommendWhole);
				saBean.setTotal(ospBean.getStockoutCount());
				if(vpBean.getName().length() > 20){
					saBean.setProudctName(vpBean.getOriname().substring(0,20));
				}else{
					saBean.setProudctName(vpBean.getOriname());
				}
				saBeanList.add(saBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return saBeanList;
	}
	/**
	 * 说明：获取货位异常数据
	 * 
	 * 日期：2013-6-15
	 * 
	 * 作者：石远飞
	 */
	@SuppressWarnings("unchecked")
	public List<SortingAgainBean> getSortingAgainList(List<SortingBatchOrderProductBean> sbopBeanList,OrderStockBean osBean,
			DbOperation dbOp,int areaId, int sortingType ){
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoServie = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,dbOp);
		List<SortingAgainBean> saBeanList = new ArrayList<SortingAgainBean>();
		try {
			for(SortingBatchOrderProductBean sbopBean : sbopBeanList){
				voProduct vpBean = this.getProductById(sbopBean.getProductId());
				if(vpBean == null){
					continue;
				}
				OrderStockProductBean ospBean = stockService.getOrderStockProduct("order_stock_id=" + osBean.getId() + " and product_id=" + sbopBean.getProductId());
				if(ospBean == null){
					continue;
				}
				OrderStockProductCargoBean ospcBean = stockService.getOrderStockProductCargo("order_stock_product_id=" + ospBean.getId());
				if(ospcBean == null){
					continue;
				}
				CargoInfoBean ciBean = cargoServie.getCargoInfo("whole_code='" + ospcBean.getCargoWholeCode() + "'");
				if(ciBean == null){
					continue;
				}
				//查找可替代货位
				List<CargoProductStockBean> cpsBeanList = cargoServie.getCargoProductStockList("product_id=" + sbopBean.getProductId() 
						+ " and cargo_id<>" + ciBean.getId() + " and stock_count>0", -1, -1, " stock_count DESC");
				String recommendWhole = "";
				if(cpsBeanList != null && cpsBeanList.size() > 0){
					int count = 0;
					for(CargoProductStockBean bean : cpsBeanList){
						CargoInfoBean cargoInfo = cargoServie.getCargoInfo("id=" + bean.getCargoId() + " and area_id=" + areaId + " and stock_type=" 
							+ CargoInfoBean.STOCKTYPE_QUALIFIED + " and (store_type=0 or store_type=4)"); //看这些放着相同商品的货位是不是一个地区的 如果不是 则不符合要求
						if(cargoInfo != null){
							if(cargoInfo.getWholeCode() != null ){
								StringBuffer sb = new StringBuffer();
								String newWholeCode = cargoInfo.getWholeCode().substring(cargoInfo.getWholeCode().indexOf("-")+1);
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
								recommendWhole += sb.toString();
								recommendWhole += "(" + bean.getStockCount() + ")";
								recommendWhole += "||";
								count ++;
							}
						}
						if(count == 2){//只显示两个替代货位 所以当够两个之后就跳出循环
							break;
						}
					}
					if(count == 0){
						recommendWhole += "没有可替代的货位！";
					}
				}else{
					recommendWhole += "没有可替代的货位！";
				}
				//count为未处理数，pda分拣则已分拣数为准
				int count = 0;
				if (sortingType == 1) {
					count = sbopBean.getCount()-sbopBean.getSortingCount();
				} else {
					count = sbopBean.getCount()-sbopBean.getCompleteCount();
				}
				if(count > 0){
					SortingAgainBean saBean = new SortingAgainBean();
					saBean.setSortingBatchOrderProductId(sbopBean.getId());
					saBean.setCount(count);
					saBean.setProudctCode(vpBean.getCode());
					saBean.setWholeCode(ospcBean.getCargoWholeCode());
					saBean.setRecommendWhole(recommendWhole);
					saBean.setTotal(sbopBean.getCount());
					if(vpBean.getOriname().length() > 20){
						saBean.setProudctName(vpBean.getOriname().substring(0,20));
					}else{
						saBean.setProudctName(vpBean.getOriname());
					}
					saBeanList.add(saBean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return saBeanList;
	}
	/**
	 * 说明：生成调拨单（货位异常单功能）
	 * 
	 * 日期：2013-6-15
	 * 
	 * 作者：石远飞
	 */
	@SuppressWarnings("unchecked")
	public String generateDeploy(DbOperation dbOp,int areaId,voUser user,
							String inCargoWholeCode,String outCargoWholeCode,int productId) {
		String eorr = null;
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		CartonningInfoService CIService = new CartonningInfoService(IBaseService.CONN_IN_SERVICE,dbOp);
		try {
			//新采购计划编号：CGJIH20090601001
			String code = "HWD"+DateUtil.getNow().substring(2,10).replace("-", "");   
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
			//areaId=1为芳村即：GZF areaId=3为增城即：GZZ
			String storageCode = "";
			if(areaId == 1){
				storageCode ="GZF";
			}else if(areaId == 3){
				storageCode ="GZZ";
			}else if(areaId == 4){
				storageCode ="JSW";
			}else{
				storageCode ="";
			}
			CargoInfoBean inCIBean = cargoService.getCargoInfo("whole_code='" + inCargoWholeCode + "'");
			CargoInfoBean outCIBean = cargoService.getCargoInfo("whole_code='" + outCargoWholeCode + "'");
			//添加作业单
			cargoOper = new CargoOperationBean();
			cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS35);
			cargoOper.setCreateDatetime(DateUtil.getNow());
			cargoOper.setRemark("货位异常生成的调拨单");
			cargoOper.setCreateUserId(user.getId());
			cargoOper.setAuditingDatetime(DateUtil.getNow());
			cargoOper.setAuditingUserId(user.getId());
			cargoOper.setCode(code);
			cargoOper.setSource("");
			cargoOper.setStorageCode(storageCode);
			cargoOper.setStockInType(inCIBean.getStoreType());
			cargoOper.setStockOutType(outCIBean.getStoreType());
			cargoOper.setCreateUserName(user.getUsername());
			cargoOper.setConfirmUserName(user.getUsername());
			cargoOper.setAuditingUserName(user.getUsername());
			cargoOper.setType(CargoOperationBean.TYPE3);
			cargoOper.setLastOperateDatetime(DateUtil.getNow());
			cargoOper.setConfirmDatetime(DateUtil.getNow());
			dbOp.startTransaction(); //开启事务
			if(!cargoService.addCargoOperation(cargoOper)){
				dbOp.rollbackTransaction();
				return eorr = "添加调拨单，数据库操作失败！";
			}
			int operId = dbOp.getLastInsertId();
			CargoProductStockBean inCargoProductStock = cargoService.getCargoProductStock("cargo_id=" + inCIBean.getId()+" and product_id=" + productId);
			if(inCargoProductStock==null){
				CargoInfoBean icf = cargoService.getCargoInfo("whole_code='" + inCargoWholeCode + "'");
				inCargoProductStock = new CargoProductStockBean();
				inCargoProductStock.setCargoId(icf.getId());
				inCargoProductStock.setProductId(productId);
				inCargoProductStock.setStockCount(0);
				inCargoProductStock.setStockLockCount(0);
				if(!cargoService.addCargoProductStock(inCargoProductStock)){
					dbOp.rollbackTransaction();
					return eorr = "关联货位信息，数据库操作失败!";
				}
				inCargoProductStock.setId(dbOp.getLastInsertId());
			}
			CargoProductStockBean outCargoProductStock = cargoService.getCargoProductStock("cargo_id=" + outCIBean.getId()+" and product_id="+productId);
				
			CargoOperationCargoBean bean = new CargoOperationCargoBean();//添加 CargoOperationCargoBean
			bean.setOperId(operId);
			bean.setProductId(productId);
			bean.setInCargoProductStockId(0);
			bean.setInCargoWholeCode("");
			bean.setOutCargoProductStockId(outCargoProductStock.getId());
			bean.setOutCargoWholeCode(outCIBean.getWholeCode());
			bean.setStockCount(1);
			bean.setType(1);
			bean.setUseStatus(0);
			if(!cargoService.addCargoOperationCargo(bean)){
				dbOp.rollbackTransaction();
				return eorr = "添加补货单详细信息，数据库操作失败!";
			}
			bean.setOperId(operId);
			bean.setProductId(productId);
			bean.setInCargoProductStockId(inCargoProductStock.getId());
			bean.setInCargoWholeCode(inCIBean.getWholeCode());
			bean.setOutCargoProductStockId(outCargoProductStock.getId());
			bean.setOutCargoWholeCode(outCIBean.getWholeCode());
			bean.setStockCount(1);
			bean.setType(0);
			bean.setUseStatus(1);
			if(!cargoService.addCargoOperationCargo(bean)){
				dbOp.rollbackTransaction();
				return eorr = "添加调拨单详细信息，数据库操作失败!";
			}
			if(!cargoService.updateCargoProductStockCount(inCargoProductStock.getId(), 1)){
				dbOp.rollbackTransaction();
				return eorr =  "更新目的货位锁定库存失败！";
			}
			if(!cargoService.updateCargoProductStockCount(outCargoProductStock.getId(), -1)){
				dbOp.rollbackTransaction();
				return eorr = "更新原货位库存失败！";
			}
			//锁定合格库库存
			ProductStockBean psBean=psService.getProductStock("area="+areaId+" and type=0 and product_id="+productId);
			if(!psService.updateProductLockCount(psBean.getId(), 1)){
				dbOp.rollbackTransaction();
				return eorr = "更新合格库库存锁定量失败！";
			}
			if(!psService.updateProductStockCount(psBean.getId(), -1)){
				dbOp.rollbackTransaction();
				return eorr = "更新合格库库存可用量失败！";
			}
			CargoOperationBean cargoOperation = cargoService.getCargoOperation("id=" + operId);
			List<CargoOperationCargoBean> outCocList = cargoService.getCargoOperationCargoList("oper_id = "+operId+" and type = 1", -1, -1, "id asc");
			for(CargoOperationCargoBean cocBean : outCocList){
				CargoProductStockBean cpsBean = cargoService.getCargoProductStock("id = "+cocBean.getOutCargoProductStockId());
				CargoInfoBean ciBean = cargoService.getCargoInfo("id = "+cpsBean.getCargoId());
				voProduct product = this.getProductById(cocBean.getProductId());
				product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
				int stockOutCount = 0;
				List<CargoOperationCargoBean> inCocList = cargoService.getCargoOperationCargoList("oper_id = "+operId+" and out_cargo_product_stock_id = "+cocBean.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
				for(CargoOperationCargoBean inCoc : inCocList){
					CargoProductStockBean inCps = cargoService.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
					CargoInfoBean inCi = cargoService.getCargoInfo("whole_code = '"+inCoc.getInCargoWholeCode()+"'");
					if(inCps!=null&&cpsBean!=null){
						//货位入库卡片
						inCps = cargoService.getCargoProductStock("id = "+inCps.getId());
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
						cargoService.addCargoStockCard(csc);
						stockOutCount = stockOutCount + inCoc.getStockCount();
					}else{
						dbOp.rollbackTransaction();
						return eorr = "库存错误，无法提交！";
					}
					if(ciBean.getAreaId()!=inCi.getAreaId()){
						//更新订单缺货状态
						this.updateLackOrder(cocBean.getProductId());
					}
					//合格库待作业任务处理
					CargoOperationTodoBean cot=CIService.getCargoOperationTodo("cargo_product_stock_id="+cpsBean.getId()+" and status in(0,1,2) and type=3");
					if(cot!=null){
						CIService.updateCargoOperationTodo("status=3", "id="+cot.getId());
					}
				}
				//货位出库卡片
				cpsBean = cargoService.getCargoProductStock("id = "+cpsBean.getId());
				CargoStockCardBean csc = new CargoStockCardBean();
				csc.setCardType(CargoStockCardBean.CARDTYPE_CARGOEXCHAGESTOCKOUT);
				csc.setCode(cargoOperation.getCode());
				csc.setCreateDatetime(DateUtil.getNow());
				csc.setStockType(ciBean.getStockType());
				csc.setStockArea(ciBean.getAreaId());
				csc.setProductId(product.getId());
				csc.setStockId(cpsBean.getId());
				csc.setStockOutCount(stockOutCount);
				csc.setStockOutPriceSum((new BigDecimal(stockOutCount)).multiply(new BigDecimal(StringUtil.formatDouble2(product.getPrice5()))).doubleValue());
				csc.setCurrentStock(product.getStock(csc.getStockArea(), csc.getStockType()) + product.getLockCount(csc.getStockArea(), csc.getStockType()));
				csc.setAllStock(product.getStockAll() + product.getLockCountAll());
				csc.setCurrentCargoStock(cpsBean.getStockCount()+cpsBean.getStockLockCount());
				csc.setCargoStoreType(ciBean.getStoreType());
				csc.setCargoWholeCode(ciBean.getWholeCode());
				csc.setStockPrice(product.getPrice5());
				csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
				cargoService.addCargoStockCard(csc);
			}
			if(!cargoService.updateCargoOperation(
					"status="+CargoOperationProcessBean.OPERATION_STATUS35+"" +
							",effect_status = 2,last_operate_datetime='"+DateUtil.getNow()+"'"+"" +
									",complete_datetime='"+DateUtil.getNow()+"'" +
											",complete_user_id="+user.getId()+"" +
													",complete_user_name='"+user.getUsername()+"'", 
													"id="+operId)){
				dbOp.rollbackTransaction();
				return eorr = "更新调拨单状态，数据库操作失败！";
			}
			CargoOperationProcessBean process=cargoService.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
			CargoOperationProcessBean process2=cargoService.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS34);//下个阶段
			if(process==null){
				dbOp.rollbackTransaction();
				return eorr = "作业单流程信息错误！";
			}
			if(process2==null){
				dbOp.rollbackTransaction();
				return eorr = "作业单流程信息错误！";
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
			cargoService.addCargoOperLog(operLog);
			dbOp.commitTransaction();  //提交事务
			eorr = "success";
		} catch (Exception e) {
			dbOp.rollbackTransaction();
			eorr = "程序异常！";
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return eorr;
	}
	
	public voProduct getProductByCode(String code) {
		voProduct product = null;
		// 数据库操作类
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return product;
		}
		ResultSet rs = null;
		// 构建查询语句
		String query = "select id, code, name, oriname from product";
		if (!"".equals(code) && code != null) {
			query += " where code='" + code + "'";
		}else {
			return product;
		}
		// 执行查询
		rs = dbOp.executeQuery(query);
		if (rs == null) {
			release(dbOp);
			return product;
		}
		try {
			if (rs.next()) {
				product = new voProduct();
				product.setId(rs.getInt("id"));
				product.setCode(rs.getString("code"));
				product.setName(rs.getString("name"));
				product.setOriname(rs.getString("oriname"));
			} else {
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return product;
	}
	/**
	 * 简单的获得部分商品信息的方法  oriname， name， code  根据id
	 * @param id
	 * @return
	 */
	public voProduct getProductById(int id) {
		voProduct product = null;
		// 数据库操作类
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return product;
		}
		ResultSet rs = null;
		// 构建查询语句
		String query = "select id, code, name, oriname from product";
		if (id != -1 && id != 0) {
			query += " where id = " + id;
		}else {
			return product;
		}
		// 执行查询
		rs = dbOp.executeQuery(query);
		if (rs == null) {
			release(dbOp);
			return product;
		}
		try {
			if (rs.next()) {
				product = new voProduct();
				product.setId(rs.getInt("id"));
				product.setCode(rs.getString("code"));
				product.setName(rs.getString("name"));
				product.setOriname(rs.getString("oriname"));
			} else {
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return product;
	}
	
	/**
	 * 添加code 的共用方法  注意：如果有地区找不到 会返回 “FAIL”
	 * @param stockArea
	 * @param dbOp
	 * @return
	 */
	public String getNewSortingAbnormalCode(int stockArea, DbOperation dbOp) {
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		String storageCode = "";
		CargoInfoStorageBean cargoInfoStorage = cargoService.getCargoInfoStorage("area_id=" + stockArea);
		if (cargoInfoStorage != null) {
			storageCode = cargoInfoStorage.getWholeCode();
		}
		if( storageCode.equals("") ) {
			return "FAIL";
		}
		String code = "YC"+ "-" + storageCode + "-" + DateUtil.getNow().substring(2,10).replace("-", "");   
		int maxid = service.getNumber("id", "sorting_abnormal", "max", "id > 0");
		SortingAbnormalBean codeBean;
		codeBean = sortingAbnormalDisposeService.getSortingAbnormal("code like '" + code + "%'");
		if(codeBean == null){
			code += "000001";
		}else {
			codeBean = sortingAbnormalDisposeService.getSortingAbnormal("id =" + maxid); 
			String _code = codeBean.getCode();
			int number = Integer.parseInt(_code.substring(_code.length()-6));
			number++;
			code += String.format("%06d",new Object[]{new Integer(number)});
		}
		return code;
	}
	/**
	 * 添加异常货位盘点计划code  注意：如果有地区找不到 会返回 “FAIL”
	 * @param stockArea
	 * @param dbOp
	 * @return
	 */
	public String getNewAbnormalCargoCheckCode(int stockArea, DbOperation dbOp) {
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, dbOp);
		String storageCode = "";
		CargoInfoStorageBean cargoInfoStorage = cargoService.getCargoInfoStorage("area_id=" + stockArea);
		if (cargoInfoStorage != null) {
			storageCode = cargoInfoStorage.getWholeCode();
		}
		if( storageCode.equals("") ) {
			return "FAIL";
		}
		String code = "YCPD"+ "-" + storageCode + "-" + DateUtil.getNow().substring(2,10).replace("-", "");   
		int maxid = service.getNumber("id", "abnormal_cargo_check", "max", "id > 0");
		AbnormalCargoCheckBean codeBean;
		codeBean = sortingAbnormalDisposeService.getAbnormalCargoCheck("code like '" + code + "%'");
		if(codeBean == null){
			//当日第一条数据，编号最后三位 001
			code += "0001";
		}else {
			//获取当日编号最大值
			codeBean = sortingAbnormalDisposeService.getAbnormalCargoCheck("id =" + maxid); 
			String _code = codeBean.getCode();
			int number = Integer.parseInt(_code.substring(_code.length()-4));
			number++;
			code += String.format("%04d",new Object[]{new Integer(number)});
		}
		return code;
	}
	public boolean rollStockBackForOne(ProductStockBean psBean,
			CargoProductStockBean cpsBean) {
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		boolean result = true;
		if(!psService.updateProductStockCount(psBean.getId(), 1)){
			result = false;
		}
		if(!psService.updateProductLockCount(psBean.getId(), -1)){
			result = false;
		}
		if(!cargoService.updateCargoProductStockCount(cpsBean.getId(), 1)){
			result = false;
		}
		if(!cargoService.updateCargoProductStockLockCount(cpsBean.getId(), -1)){
			result = false;
		}
		return result;
	}

	public boolean updateSortingAbnormalStatus(
			SortingAbnormalProductBean sapBean) {
		boolean result = true;
		//顺序很重要， 如果lockCOunt（）刚好是1， 就会先改成 处理中， 紧接着就改成完成了
		if( sapBean.getLockCount() == sapBean.getCount() ) {
			if( !this.updateSortingAbnormalProduct("status=" + SortingAbnormalProductBean.STATUS_DEALING, "id=" + sapBean.getId()) ) {
				result = false;
			}
		}
		if( sapBean.getLockCount() == 1 ) {
			if( !this.updateSortingAbnormalProduct("status=" + SortingAbnormalProductBean.STATUS_NORMAL, "id=" + sapBean.getId()) ) {
				result = false;
			}
		}
		if( !this.updateSortingAbnormalProduct("lock_count = (lock_count - 1), last_oper_datetime='" +DateUtil.getNow() + "'", "id=" + sapBean.getId()) ) {
			result = false;
		}
		
		return result;
	}

	public int getSortingAbnormalCount2(String sqlCount) {
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

	public List getSortingAbnormal2(String sql, int index,
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
				SortingAbnormalBean saBean = new SortingAbnormalBean();
				saBean.setId(rs.getInt("id"));
				saBean.setAbnormalType(rs.getInt("abnormal_type"));
				saBean.setCode(rs.getString("code"));
				saBean.setCreateDatetime(rs.getString("create_datetime"));
				saBean.setCreateUserId(rs.getInt("create_user_id"));
				saBean.setCreateUserName(rs.getString("create_user_name"));
				saBean.setOperCode(rs.getString("oper_code"));
				saBean.setOperType(rs.getInt("oper_type"));
				saBean.setStatus(rs.getInt("status"));
				saBean.setWareArea(rs.getInt("ware_area"));
				result.add(saBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}
	
	public List getSortingAbnormalProductList2(String sql, int index,
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
				SortingAbnormalProductBean sapBean = new SortingAbnormalProductBean();
				sapBean.setId(rs.getInt("id"));
				sapBean.setSortingAbnormalId(rs.getInt("sorting_abnormal_id"));
				sapBean.setProductId(rs.getInt("product_id"));
				sapBean.setProductCode(rs.getString("product_code"));
				sapBean.setCargoWholeCode(rs.getString("cargo_whole_code"));
				sapBean.setCount(rs.getInt("count"));
				sapBean.setLockCount(rs.getInt("total_lock"));
				sapBean.setStatus(rs.getInt("status"));
				sapBean.setLastOperDatetime(rs.getString("last_oper_datetime"));
				result.add(sapBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}
	
	public List getSortingAbnormalProductList3(String sql, int index,
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
				SortingAbnormalProductBean sapBean = new SortingAbnormalProductBean();
				sapBean.setId(rs.getInt("id"));
				sapBean.setSortingAbnormalId(rs.getInt("sorting_abnormal_id"));
				sapBean.setProductId(rs.getInt("product_id"));
				sapBean.setProductCode(rs.getString("product_code"));
				sapBean.setCargoWholeCode(rs.getString("cargo_whole_code"));
				sapBean.setCount(rs.getInt("count"));
				sapBean.setLockCount(rs.getInt("lock_count"));
				sapBean.setStatus(rs.getInt("status"));
				sapBean.setLastOperDatetime(rs.getString("last_oper_datetime"));
				result.add(sapBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return result;
	}

	/**
	 * 计算两个yyyy-MM-dd格式的时间差 是否在31天以上
	 * @param startTime
	 * @param endTime
	 * @return  true代表大于 31天了
	 */
	public boolean isDateMoreThanThirtyOne(String startTime, String endTime) {
		boolean result = false;
		
		long thirtyOneDayOfMiliis = (long)31*24*60*60*1000;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			Date date1 = sdf.parse(startTime);
			Date date2 = sdf.parse(endTime);
			long start = date1.getTime();
			long end = date2.getTime();
			long period = end - start;
			if( period > thirtyOneDayOfMiliis ) {
				result = true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * 根据异常货位盘点计划生成 调拨，平库存， 生成报损报溢单等操作。。
	 * @param id
	 * @param user 
	 * @return
	 * @throws Exception 
	 */
	public String generateBsBy(int id, voUser user ) throws Exception {
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		String result = "success";
		//是否有货位锁定量和整体锁定量 相等的情况。
		boolean hasEqualLock = false;
		//是否有盘点与实物不同的情况
		boolean hasNeedDeal = false;
		//是否有生成了调拨单的情况
		boolean hasExchange = false;
		//是否有生成了报损报溢单的情况
		boolean hasBsby = false;
		AbnormalCargoCheckBean accBean = this.getAbnormalCargoCheck("id=" + id);
		if(accBean == null ) {
			result = "没有找到对应的盘点单信息！";
			return result;
		} else {
			//首先状态验证一下子。。。
			//?????????????????????
			if( accBean.getStatus() != AbnormalCargoCheckBean.STATUS4 ) {
				result = "该盘点计划还没有终盘完成！";
				return result;
			}
			int accpCount = this.getAbnormalCargoCheckProductCount("abnormal_cargo_check_id=" + accBean.getId());
			if( accpCount == 0 ) {
				result = "盘点计划下无盘点明细！";
				return result;
			}
			//????????????????????
			List<AbnormalCargoCheckProductBean> tempList = this.getAbnormalCargoCheckProductList("abnormal_cargo_check_id=" + id + " and status in ("+ AbnormalCargoCheckProductBean.STATUS_WAIT_FIRST_CHECK + "," + AbnormalCargoCheckProductBean.STATUS_WAIT_SECOND_CHECK + "," + AbnormalCargoCheckProductBean.STATUS_WAIT_THRID_CHECK + ")", -1, -1, null);
			if( tempList.size() > 0 ) {
				result = "盘点计划下还有未完成的盘点！";
				return result;
			}
			//????????????????????????
			//排除无效的 盘点单
			List<AbnormalCargoCheckProductBean> accpList = this.getAbnormalCargoCheckProductList("abnormal_cargo_check_id=" + id + " and status <> " + AbnormalCargoCheckProductBean.STATUS_UNAFFECTIVE, -1, -1, null);
			int x = accpList.size();
			//存放差异量的两个MAP
			Map<Integer, List<AbnormalCargoCheckProductBean>> moreMap = new HashMap<Integer, List<AbnormalCargoCheckProductBean>>();
			Map<Integer, List<AbnormalCargoCheckProductBean>> lessMap = new HashMap<Integer, List<AbnormalCargoCheckProductBean>>();
			for( int i = 0 ; i < x; i ++ ) {
				AbnormalCargoCheckProductBean accpBean = accpList.get(i);
				
				//确定最终盘点量  赋给 accpBean 中的一个变量
				//UNRelease
				int eventuallyCheckCount = accpBean.getFinalCheckCount();
				if( eventuallyCheckCount < 0 ) {
					result = "有最终盘点量为负的条目！";
					return result;
				}
				accpBean.setEventuallyCheckCount(eventuallyCheckCount);
				// 根据 货位，还有 商品信息 找到CargoProductStock  并且放在 accpBean里。
				CargoInfoBean ciBean = cargoService.getCargoInfo("whole_code='" + accpBean.getCargoWholeCode() + "'");
				if( ciBean == null ) {
					result = "没有找到货位信息！";
					return result;
				} else {
					accpBean.setCargoInfoBean(ciBean);
					CargoProductStockBean cpsBean = cargoService.getCargoProductStock("cargo_id=" + ciBean.getId() + " and product_id=" + accpBean.getProductId());
					if( cpsBean == null ) {
						result = "没有找到货位库存信息！";
						return result;
					} else {
						accpBean.setCargoProductStockBean(cpsBean);
					}
				}
				// ??????????????
				List<SortingAbnormalProductBean> sortingAbnormals = this.getSortingAbnormalProductList("cargo_whole_code='" + accpBean.getCargoWholeCode() + "' and product_id=" + accpBean.getProductId() + " and status = " + SortingAbnormalProductBean.STATUS_CHECKING, -1, -1, null);
				
				int xx = sortingAbnormals.size();
				if( xx == 0 ) {
					result = "当前计划盘点并没有对应的异常单商品信息匹配！";
					return result;
				}
				int abnormalLockCount = 0;
				for( int j = 0; j < xx; j++ ) {
					SortingAbnormalProductBean sapBean = sortingAbnormals.get(j);
					abnormalLockCount += sapBean.getLockCount();
				}
				ProductStockBean psBean = psService.getProductStock("area=" + accBean.getArea() + " and type=" + ProductStockBean.STOCKTYPE_QUALIFIED + " and product_id=" + accpBean.getProductId() );
				//加上了 整个库存额锁定连的验证 如果整个库存的锁定量 还不如一个货位的锁定量多的话 也给这写盘点计划作废掉
				//所有的异常单的 锁定量的和， 不同于货位上对应商品的锁定量 这时.... 作废这个货位盘点计划明细
				if( abnormalLockCount != accpBean.getCargoProductStockBean().getStockLockCount() || abnormalLockCount > psBean.getLockCount() ) {
					for( int j = 0 ; j < xx; j++ ) {
						SortingAbnormalProductBean sapBean = sortingAbnormals.get(j);
						if( sapBean.getStatus() == SortingAbnormalProductBean.STATUS_CHECKING ) {
							//????????????????????
							if( !this.updateSortingAbnormalProduct("status=" + SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK, "id=" + sapBean.getId()) ) {
								result = "数据库操作失败！";
								return result;
							}
							if( !this.updateAbnormalCargoCheckProduct("status=" + AbnormalCargoCheckProductBean.STATUS_UNAFFECTIVE, "id=" + accpBean.getId()) ) {
								result = "修改盘点明细状态数据库操作失败！";
								return result;
							}
						}
					}
				} else {
					hasEqualLock = true;
					//如果锁定量和 冻结量 对的上， 这是就把锁定量恢复为可用量了
					//------------------ 根据 对应的商品id 和货位 得到 对应的所有的 异常单的信息....  具体怎么得到....
					//把所有的锁定量 还原到 可用量中去先
					//ProductStockBean psBean = psService.getProductStock("area=" + accBean.getArea() + " and type=" + ProductStockBean.STOCKTYPE_QUALIFIED + " and product_id=" + accpBean.getProductId() );
					if( !this.resetLockCountToAvailable(accpBean.getCargoProductStockBean(), psBean) ) {
						// 是否需要加什么标记.................
						result = "恢复锁定量时，数据库操作失败！";
						return result;
					}
					for( int j = 0 ; j < xx; j++ ) {
						SortingAbnormalProductBean sapBean = sortingAbnormals.get(j);
						if( sapBean.getStatus() == SortingAbnormalProductBean.STATUS_CHECKING ) {
							//????????????????????
							if( !this.updateSortingAbnormalProduct("status=" + SortingAbnormalProductBean.STATUS_CHECKED, "id=" + sapBean.getId()) ) {
								result = "数据库操作失败！";
								return result;
							}
							//通过了检查 货位锁定量和 总锁定两相等的 将异常单里的锁定量都化为0
							if( sapBean.getLockCount() > 0 ) {
								if( !this.updateSortingAbnormalProduct("lock_count=0", "id=" + sapBean.getId()) ) {
									result = "修改异常单商品锁定量时，数据库操作失败！";
									return result;
								}
							}
						}
					}
				}
			} 
			
			//查询状态仍然为已完成的。。
			List<AbnormalCargoCheckProductBean> accpList2 = this.getAbnormalCargoCheckProductList("abnormal_cargo_check_id=" + id + " and status=" + AbnormalCargoCheckProductBean.STATUS_CHECK_FINISHED , -1, -1, null);
			int y = accpList2.size();
			if( x > 0  &&  y == 0 ) {
				result = "所有的盘点计划明细锁定量异常，货位操作将不会进行！";
			}
			for( int i = 0 ; i < y; i ++ ) {
				AbnormalCargoCheckProductBean accpBean = accpList2.get(i);
				//确定最终盘点量  赋给 accpBean 中的一个变量
				int eventuallyCheckCount = accpBean.getFinalCheckCount();
				if( eventuallyCheckCount < 0 ) {
					result = "有最终盘点量为负的条目！";
					return result;
				}
				accpBean.setEventuallyCheckCount(eventuallyCheckCount);
				// 根据 货位，还有 商品信息 找到CargoProductStock  并且放在 accpBean里。
				CargoInfoBean ciBean = cargoService.getCargoInfo("whole_code='" + accpBean.getCargoWholeCode() + "'");
				if( ciBean == null ) {
					result = "没有找到货位信息!";
					return result;
				} else {
					accpBean.setCargoInfoBean(ciBean);
					CargoProductStockBean cpsBean = cargoService.getCargoProductStock("cargo_id=" + ciBean.getId() + " and product_id=" + accpBean.getProductId());
					if( cpsBean == null ) {
						result = "没有找到货位库存信息！";
						return result;
					} else {
						accpBean.setCargoProductStockBean(cpsBean);
					}
				}
				//-----注意 由于在上一个循环中， 已经还原了一些 锁定量 到可用量上去了
				// 计算 cps中的 可用量， 和 锁定量的和， 与盘点量的差值， 放在 accpBean 中  一个叫做 offCount；
				// 根据 ProductId 区分的
				// 根据计算值的 正或负  进行分组  放在两个List中  正的一个moreList  负的一个lessList
				int offCount = accpBean.getEventuallyCheckCount() - (accpBean.getCargoProductStockBean().getStockCount() + accpBean.getCargoProductStockBean().getStockLockCount());
				//如果等于0  就对上了  就不用再算了
				//如果盘点量大于 理论量， 就是说， 需要把别处的理论量调到这里才能使盘点量和理论量接近
				if(  offCount > 0 ) {
					hasNeedDeal = true;
					accpBean.setOffCount(offCount);
					accpBean.setAbsCount(offCount);
					List<AbnormalCargoCheckProductBean> moreList = moreMap.get(accpBean.getProductId());
					if( moreList == null ) {
						moreList = new ArrayList<AbnormalCargoCheckProductBean>();
					}
					moreList.add(accpBean);
					moreMap.put(accpBean.getProductId(), moreList);
				}
				
				if( offCount < 0  ) {
					hasNeedDeal = true;
					accpBean.setOffCount(offCount);
					accpBean.setAbsCount(Math.abs(offCount));
					List<AbnormalCargoCheckProductBean> lessList = lessMap.get(accpBean.getProductId());
					if( lessList == null ) {
						lessList = new ArrayList<AbnormalCargoCheckProductBean>();
					}
					lessList.add(accpBean);
					lessMap.put(accpBean.getProductId(), lessList);
				}
				if( offCount == 0 ) {
					if ( !this.updateAbnormalCargoCheckProduct("status=" + AbnormalCargoCheckProductBean.STATUS_NORMAL, "id=" + accpBean.getId()) ) {
						result = "修改盘点计划详情状态，数据库操作失败！";
						return result;
					}
				}
			}
			
			//第三次  主要是进行  多与少的对冲, 以调入方为主   
			for( int i = 0 ; i < y; i ++ ) {
				AbnormalCargoCheckProductBean accpBean = accpList.get(i);
				int productId = accpBean.getProductId();
				if( lessMap.get(productId) == null && moreMap.get(productId) == null ) {
					continue;
				} else if (lessMap.get(productId)!= null && moreMap.get(productId) == null ) {
					List<AbnormalCargoCheckProductBean> lessList = lessMap.get(productId);
					for( int k = 0 ; k < lessList.size(); k ++ ) {
						AbnormalCargoCheckProductBean bsaccpBean = lessList.get(k);
						Object resu = this.addBsByOperationnote(user, accBean.getArea(), 0, ProductStockBean.STOCKTYPE_QUALIFIED, bsaccpBean.getProductId(), bsaccpBean.getCargoWholeCode(), bsaccpBean.getAbsCount());
						if( resu instanceof String ) {
							result = (String)resu;
							return result;
						} else if ( resu instanceof BsbyOperationnoteBean) {
							if ( !this.updateAbnormalCargoCheckProduct("status=" + AbnormalCargoCheckProductBean.STATUS_BS, "id=" + bsaccpBean.getId()) ) {
								result = "修改盘点计划详情状态，数据库操作失败！";
								return result;
							}
							BsbyOperationnoteBean bsbyBean = (BsbyOperationnoteBean)resu;
							if( ! this.updateAbnormalCargoCheckProduct("bsby_id = " + bsbyBean.getId(), "id=" + bsaccpBean.getId()) ) {
								result = "报损报溢单与盘点计划关联时发生了问题！";
								return result;
							}
							
						}
						hasBsby = true;
					}
					lessMap.put(productId, null);
				} else if (lessMap.get(productId) == null && moreMap.get(productId)!= null ) {
					List<AbnormalCargoCheckProductBean> moreList = moreMap.get(productId);
					for( int k = 0 ; k < moreList.size(); k ++ ) {
						AbnormalCargoCheckProductBean byaccpBean = moreList.get(k);
						Object resu = this.addBsByOperationnote(user, accBean.getArea(), 1, ProductStockBean.STOCKTYPE_QUALIFIED, byaccpBean.getProductId(), byaccpBean.getCargoWholeCode(), byaccpBean.getAbsCount());
						if( resu instanceof String ) {
							result = (String)resu;
							return result;
						} else if ( resu instanceof BsbyOperationnoteBean) {
							if ( !this.updateAbnormalCargoCheckProduct("status=" + AbnormalCargoCheckProductBean.STATUS_BY, "id=" + byaccpBean.getId()) ) {
								result = "修改盘点计划详情状态，数据库操作失败！";
								return result;
							}
							BsbyOperationnoteBean bsbyBean = (BsbyOperationnoteBean)resu;
							if( ! this.updateAbnormalCargoCheckProduct("bsby_id = " + bsbyBean.getId(), "id=" + byaccpBean.getId()) ) {
								result = "报损报溢单与盘点计划关联时发生了问题！";
								return result;
							}
						}
						hasBsby = true;
					}
					moreMap.put(productId, null);
				} else {
					while (lessMap.get(productId).size() > 0 && moreMap.get(productId).size() > 0) {
						//开始对冲
						//在这时排序吧...
						List<AbnormalCargoCheckProductBean> lessList = lessMap.get(productId);
						List<AbnormalCargoCheckProductBean> moreList = moreMap.get(productId);
						AbnormalCargoCheckProductBean[] moreArray = this.sortMoreOrLessList(moreList);
						AbnormalCargoCheckProductBean[] lessArray = this.sortMoreOrLessList(lessList);
						AbnormalCargoCheckProductBean lessBean = lessArray[0];
						AbnormalCargoCheckProductBean moreBean = moreArray[0];
						// 如果 被调方 大于 需调方， 调入 需调量
						//如果被调方 和 需调方 一样， 刚好
						// 如果被调方，小于需调方量， 全部调走
						int count = this.getHowManyShouldExchange(lessBean, moreBean);
						//注意调拨方向 ，  是需要  是盘点量多了的  需要调来， 盘点量少的  需要调出
						String res = this.generateDeploy3(this.dbOp, accBean.getArea(), user,moreBean.getCargoInfoBean(), lessBean.getCargoInfoBean(), moreBean.getCargoProductStockBean(), lessBean.getCargoProductStockBean(), productId, count);
						if( !res.equals("success") ) {
							result = res;
							return result;
						}
						hasExchange = true;
						lessBean.setOffCount(lessBean.getOffCount() + count);
						lessBean.setAbsCount(lessBean.getAbsCount() - count);
						moreBean.setOffCount(moreBean.getOffCount() - count);
						moreBean.setAbsCount(moreBean.getAbsCount() - count);
						if( lessBean.getAbsCount() == 0) {
							//清除掉 lessBean
							if ( !this.updateAbnormalCargoCheckProduct("status=" + AbnormalCargoCheckProductBean.STATUS_NORMAL, "id=" + lessBean.getId()) ) {
								result = "修改盘点计划详情状态，数据库操作失败！";
								return result;
							}
							lessList.remove(lessBean);
							lessMap.put(productId, lessList);
							
						}
						if( moreBean.getAbsCount() == 0 ) {
							if ( !this.updateAbnormalCargoCheckProduct("status=" + AbnormalCargoCheckProductBean.STATUS_NORMAL, "id=" + moreBean.getId()) ) {
								result = "修改盘点计划详情状态，数据库操作失败！";
								return result;
							}
							moreList.remove(moreBean);
							moreMap.put(productId, moreList);
						}
					}
					if ( lessMap.get(productId).size() > 0 && moreMap.get(productId).size() == 0 ) {
						//仍然盘点量少于理论量  报损
						List<AbnormalCargoCheckProductBean> lessList = lessMap.get(productId);
						for( int k = 0 ; k < lessList.size(); k ++ ) {
							AbnormalCargoCheckProductBean bsaccpBean = lessList.get(k);
							Object resu = this.addBsByOperationnote(user, accBean.getArea(), 0, ProductStockBean.STOCKTYPE_QUALIFIED, bsaccpBean.getProductId(), bsaccpBean.getCargoWholeCode(), bsaccpBean.getAbsCount());
							if( resu instanceof String ) {
								result = (String)resu;
								return result;
							}  else if ( resu instanceof BsbyOperationnoteBean) {
								if ( !this.updateAbnormalCargoCheckProduct("status=" + AbnormalCargoCheckProductBean.STATUS_BS, "id=" + bsaccpBean.getId()) ) {
									result = "修改盘点计划详情状态，数据库操作失败！";
									return result;
								}
								BsbyOperationnoteBean bsbyBean = (BsbyOperationnoteBean)resu;
								if( ! this.updateAbnormalCargoCheckProduct("bsby_id = " + bsbyBean.getId(), "id=" + bsaccpBean.getId()) ) {
									result = "报损报溢单与盘点计划关联时发生了问题！";
									return result;
								}
							}
							hasBsby = true;
						}
						lessMap.put(productId, null);
						continue;
					}
					if ( lessMap.get(productId).size() == 0 && moreMap.get(productId).size() > 0 ) {
						//仍然 盘点量 多于理论量  报溢
						List<AbnormalCargoCheckProductBean> moreList = moreMap.get(productId);
						for( int k = 0 ; k < moreList.size(); k ++ ) {
							AbnormalCargoCheckProductBean byaccpBean = moreList.get(k);
							Object resu = this.addBsByOperationnote(user, accBean.getArea(), 1, ProductStockBean.STOCKTYPE_QUALIFIED, byaccpBean.getProductId(), byaccpBean.getCargoWholeCode(), byaccpBean.getAbsCount());
							if(  resu instanceof String ) {
								result = (String)resu;
								return result;
							}  else if ( resu instanceof BsbyOperationnoteBean) {
								if ( !this.updateAbnormalCargoCheckProduct("status=" + AbnormalCargoCheckProductBean.STATUS_BY, "id=" + byaccpBean.getId()) ) {
									result = "修改盘点计划详情状态，数据库操作失败！";
									return result;
								}
								BsbyOperationnoteBean bsbyBean = (BsbyOperationnoteBean)resu;
								if( ! this.updateAbnormalCargoCheckProduct("bsby_id = " + bsbyBean.getId(), "id=" + byaccpBean.getId()) ) {
									result = "报损报溢单与盘点计划关联时发生了问题！";
									return result;
								}
							}
							hasBsby = true;
						}
						moreMap.put(productId, null);
						continue;
					}
				}
			}
			if( ! this.updateAbnormalCargoCheck("status=" + AbnormalCargoCheckBean.STATUS5, "id=" + accBean.getId())) {
				result = "修改盘点计划状态失败！";
				return result;
			}
			
		}
		if( !hasEqualLock ) {
			//就是说所有的锁定量都对不上不能进行报损报溢操作了。
			result = "success1";
		}
		if ( hasEqualLock && !hasNeedDeal ) {
			//就是有锁定量相同的 但是盘点量和实际量都对上了
			result = "success2";
		}
		if ( hasEqualLock && hasNeedDeal && !hasBsby && hasExchange ) {
			//都通过 调拨的方式改掉了
			result = "success3";
		}
		if ( hasEqualLock && hasNeedDeal && !hasExchange && hasBsby ) {
			//都通过 报损报溢的方式改掉了
			result = "success4";
		}
		if (hasEqualLock && hasNeedDeal && hasExchange && hasBsby ) {
			//生成了调拨单 也生成了报损报溢单的情况
			result = "success5";
		}
		if (hasEqualLock && hasNeedDeal && !hasExchange && !hasBsby ) {
			//生成了调拨单 也生成了报损报溢单的情况
			result = "success6";
		}
		
		return result;
	}

	/**
	 * 得到可以调拨量
	 * @param lessBean
	 * @param moreBean
	 * @return
	 */
	private int getHowManyShouldExchange(
			AbnormalCargoCheckProductBean lessBean,
			AbnormalCargoCheckProductBean moreBean) {
		int exchangeCount = 0;
		int result = lessBean.getAbsCount() - moreBean.getAbsCount();
		if( result == 0 ) {
			exchangeCount = moreBean.getOffCount();
		} else if( result > 0 ) {
			exchangeCount = moreBean.getOffCount();
		} else if( result < 0 ) {
			exchangeCount = lessBean.getAbsCount();
		}
		return exchangeCount;
	}

	/**
	 * 按照差值的绝对值 由大到小排列
	 * @param list
	 * @return
	 */
	private AbnormalCargoCheckProductBean[] sortMoreOrLessList(List<AbnormalCargoCheckProductBean> list) {
		
		AbnormalCargoCheckProductBean[] accps = new AbnormalCargoCheckProductBean[list.size()];
		for( int i = 0 ; i < accps.length; i ++ ) {
			accps[i] = list.get(i);
		}
		int x = accps.length;
		for( int i = 0; i < x; i++ ) {
			for( int j = x - 1; j > i; j --) {
				if( accps[j].getAbsCount() > accps[j - 1 ].getAbsCount() ) {
					AbnormalCargoCheckProductBean temp = accps[j];
					accps[j] = accps[j-1];
					accps[j-1] = temp;
				}
			}
		}
		return accps;
	}
	
	private boolean resetLockCountToAvailable(
			CargoProductStockBean cpsBean, ProductStockBean psBean) {
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		boolean result = true;
		try {
			if( cpsBean.getStockLockCount() > 0 ) {
				if(!psService.updateProductStockCount(psBean.getId(), cpsBean.getStockLockCount())){
					result = false;
					return result;
				}
				if(!psService.updateProductLockCount(psBean.getId(), -cpsBean.getStockLockCount())){
					result = false;
					return result;
				}
				if(!cargoService.updateCargoProductStockCount(cpsBean.getId(), cpsBean.getStockLockCount())){
					result = false;
					return result;
				}
				if(!cargoService.updateCargoProductStockLockCount(cpsBean.getId(), -cpsBean.getStockLockCount())){
					result = false;
					return result;
				}
			}
		} catch( Exception e ) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}
	
	/**
	 *	说明：生成报损报溢单 加商品  一步到审核
	 *	日期：2013-6-13
	 *	
	 */
	public Object addBsByOperationnote(voUser user,int wareArea,
			int bsbyType,int wareType, int productId, String cargoCode, int count )throws Exception {
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		WareService wareService = new WareService(this.dbOp);
		Object result = "";
		voProduct product = wareService.getProduct(productId);
		if (product == null) {
			result = "有商品不存在!";
			return result;
		}
		if (product.getParentId1() == 106) {
			result = "[" + product.getCode()+ "]该商品为新商品,请先修改该产品的分类";
			return result;
		}
		if (product.getIsPackage() == 1) {
			result = "[" + product.getCode()+ "]该产品为套装产品,不能添加!";
			return result;
		}
		String receipts_number = "";
		String title = "";// 日志的内容
		int typeString = 0;
		int bsbyId = -1;
		if (bsbyType == 0) {
			// 报损
			String code = "BS" + DateUtil.getNow().substring(0, 10).replace("-", "");
			receipts_number = createCode(code, service);// BS+年月日+3位自动增长数
			title = "创建新的报损表" + receipts_number;
			typeString = 0;
		} else {
			String code = "BY" + DateUtil.getNow().substring(0, 10).replace("-", "");
			receipts_number = createCode(code,service);// BY+年月日+3位自动增长数
			title = "创建新的报溢表" + receipts_number;
			typeString = 1;
		}
		String nowTime = DateUtil.getNow();
		BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();
		bsbyOperationnoteBean.setAdd_time(nowTime);
		bsbyOperationnoteBean.setCurrent_type(0);
		bsbyOperationnoteBean.setOperator_id(user.getId());
		bsbyOperationnoteBean.setOperator_name(user.getUsername());
		bsbyOperationnoteBean.setReceipts_number(receipts_number);
		bsbyOperationnoteBean.setWarehouse_area(wareArea);
		bsbyOperationnoteBean.setWarehouse_type(wareType);
		bsbyOperationnoteBean.setType(typeString);
		bsbyOperationnoteBean.setIf_del(0);
		bsbyOperationnoteBean.setFinAuditId(0);
		bsbyOperationnoteBean.setFinAuditName("");
		bsbyOperationnoteBean.setFinAuditRemark("");
		bsbyOperationnoteBean.setRemark("异常货位盘点功能");
		
			int maxid = service.getNumber("id", "bsby_operationnote", "max", "id > 0");
			bsbyOperationnoteBean.setId(maxid + 1);
			if (service.addBsbyOperationnoteBean(bsbyOperationnoteBean)) {
				bsbyId = Integer.valueOf(bsbyOperationnoteBean.getId());
				// 添加操作日志
				BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
				bsbyOperationRecordBean.setOperator_id(user.getId());
				bsbyOperationRecordBean.setOperator_name(user.getUsername());
				bsbyOperationRecordBean.setTime(nowTime);
				bsbyOperationRecordBean.setInformation(title);
				bsbyOperationRecordBean.setOperation_id(bsbyOperationnoteBean.getId());
				if(!service.addBsbyOperationRecord(bsbyOperationRecordBean)){
					result = "添加报损报溢日志失败!";
					return result;
				}
			}else{
				result = "添加报损报溢表失败！";
				return result;
			}
			//-----------------加商品
			
			BsbyOperationnoteBean bean = service.getBsbyOperationnoteBean("id = " + bsbyId);
			if(bean.getCurrent_type()!=BsbyOperationnoteBean.dispose && bean.getCurrent_type()!=BsbyOperationnoteBean.audit_Fail){
				result =  "单据已提交审核,无法修改!";
				return result;
			}
			
			if (service.getBsbyProductBean("operation_id = " + bsbyId + " and product_code = " + product.getCode()) != null) {
				result = "[" + product.getCode()+ "]该产品已经添加,直接修改即可,不用重复添加!";
				return result;
			}
			if (service.getBsbyProductBean("operation_id = " + bsbyId ) != null) {
				result = "只能添加一个商品 所以不能重复提交!";
				return result;
			}
			BsbyOperationnoteBean ben = service.getBsbyOperationnoteBean("id=" + bsbyId);
			int x = getProductCount(product.getId(), ben.getWarehouse_area(), ben.getWarehouse_type());
			int n = updateProductCount(x, ben.getType(), count);
			if (n < 0 ) {
				result = "您所添加商品的库存不足!";
				return result;
			}
			//新货位管理判断
			CargoProductStockBean cps = null;
			if(ben.getType()==0){
		        CargoInfoAreaBean outCargoArea = cargoService.getCargoInfoArea("old_id = "+ben.getWarehouse_area());
		        List cpsOutList = cargoService.getCargoAndProductStockList("ci.stock_type = "+ben.getWarehouse_type()+" and ci.area_id = "+outCargoArea.getId()+" and cps.product_id = "+product.getId()+" and ci.whole_code = '"+cargoCode+"'", -1, -1, "ci.id asc");
		        if(cpsOutList == null || cpsOutList.size()==0){
		        	result = "货位号"+cargoCode+"无效,请重新输入!";
		        	return result;
		        }
		        cps = (CargoProductStockBean)cpsOutList.get(0);
		        if(ben.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED && cps.getCargoInfo().getStoreType() == CargoInfoBean.STORE_TYPE2){
		        	result = "合格库缓存区暂时不能进行报损报溢操作!";
		        	return result;
		        }
		        if(count > cps.getStockCount()){
		        	result = "该货位"+cargoCode+"库存为" + cps.getStockCount() + ",库存不足!";
		        	return result;
		        }
			}else{
				CargoInfoAreaBean inCargoArea = cargoService.getCargoInfoArea("old_id = "+ben.getWarehouse_area());
				CargoInfoBean cargo = cargoService.getCargoInfo("stock_type = "+ben.getWarehouse_type()+" and area_id = "+inCargoArea.getId()+" and whole_code = '"+cargoCode+"' and status <> "+CargoInfoBean.STATUS3);
		        if(cargo == null){
		        	result = "货位号"+cargoCode+"无效,请重新输入!";
		        	return result;
		        }
		        if(cargo.getStatus() == CargoInfoBean.STATUS2){
		        	result = "货位"+cargoCode+"未开通,请重新输入!";
		        	return result;
		        }
		        if(ben.getWarehouse_type() == ProductStockBean.STOCKTYPE_QUALIFIED && cargo.getStoreType() == CargoInfoBean.STORE_TYPE2){
		        	result = "合格库缓存区暂时不能进行报损报溢操作!";
		        	return result;
		        }
				List cpsOutList = cargoService.getCargoAndProductStockList("cps.product_id = "+product.getId()+" and cps.cargo_id = "+cargo.getId(), -1, -1, "ci.id asc");
		        if(cpsOutList == null || cpsOutList.size()==0){
		        	if(cargo.getStatus() == CargoInfoBean.STATUS0 && (cargo.getStoreType() == CargoInfoBean.STORE_TYPE0||cargo.getStoreType() == CargoInfoBean.STORE_TYPE4)){
		        		result = "货位"+cargoCode+"被其他商品使用中,添加失败!";
		        		return result;
		        	}
		        	cps = new CargoProductStockBean();
		        	cps.setCargoId(cargo.getId());
		        	cps.setProductId(product.getId());
		        	cps.setStockCount(0);
		        	cps.setStockLockCount(0);
		        	if(!cargoService.addCargoProductStock(cps)){
		        		result = "生成报损报溢单数据库异常!(cargoService.addCargoProductStock(cps))";
		        		return result;
		        	}
		        	cps.setId(cargoService.getDbOp().getLastInsertId());
		        	if(!cargoService.updateCargoInfo("status = "+CargoInfoBean.STATUS0, "id = "+cargo.getId())){
		        		result = "生成报损报溢单数据库异常!(cargoService.updateCargoInfo())";
		        		return result;
		        	}
		        }else{
		        	cps = (CargoProductStockBean)cpsOutList.get(0);
		        }
			}
	        

			BsbyProductBean bsbyProductBean1 = new BsbyProductBean();
			bsbyProductBean1.setBsby_count(count);
			bsbyProductBean1.setOperation_id(bsbyId);
			bsbyProductBean1.setProduct_code(product.getCode());
			bsbyProductBean1.setProduct_id(product.getId());
			bsbyProductBean1.setProduct_name(product.getName());
			bsbyProductBean1.setOriname(product.getOriname());
			bsbyProductBean1.setAfter_change(n);
			bsbyProductBean1.setBefore_change(x);
			if(service.addBsbyProduct(bsbyProductBean1)) {
			}else{
				result = "商品添加失败!";
				return result;
			}
			BsbyProductCargoBean bsbyCargo1 = new BsbyProductCargoBean();
			bsbyCargo1.setBsbyOperId(ben.getId());
			bsbyCargo1.setBsbyProductId(service.getDbOp().getLastInsertId());
			bsbyCargo1.setCount(count);
			bsbyCargo1.setCargoProductStockId(cps.getId());
			bsbyCargo1.setCargoId(cps.getCargoId());
			service.addBsbyProductCargo(bsbyCargo1);
			// 添加日志
			BsbyOperationRecordBean bsbyOperationRecordBean = new BsbyOperationRecordBean();
			bsbyOperationRecordBean.setOperator_id(user.getId());
			bsbyOperationRecordBean.setOperator_name(user.getUsername());
			bsbyOperationRecordBean.setTime(DateUtil.getNow());
			bsbyOperationRecordBean.setInformation("给单据(id):" + bsbyId+ "添加商品:" + product.getCode() + "数量：" + count);
			bsbyOperationRecordBean.setOperation_id(bsbyId);
			if(!service.addBsbyOperationRecord(bsbyOperationRecordBean)){
				result = "日志添加失败!";
				return result;
			}
			
			//-------------------审核
			BsbyOperationnoteBean bean2 = service.getBsbyOperationnoteBean("id=" + bsbyId);
			if(bean2 == null){
				result = "报损报溢单不存在!";
				return result;
			}
			//报损单中的所有产品
			List bsbyList = service.getBsbyProductList("operation_id=" + bean2.getId(), -1, -1, null);
			Iterator it = bsbyList.iterator();
			if(bean2.getType() == 0){
				for (; it.hasNext();) {
					BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
					BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
					if(bsbyCargo == null){
						result = "货位信息异常,操作失败,请与管理员联系!";
						return result;
					}
					String sql = "product_id = " + bsbyProductBean.getProduct_id() + " and "
					+ "area = " + bean2.getWarehouse_area() + " and type = "
					+ bean2.getWarehouse_type();
					ProductStockBean psBean = psService.getProductStock(sql);
					//减少库存
					if(!psService.updateProductStockCount(psBean.getId(), -bsbyProductBean.getBsby_count())){
						result = "库存操作失败,可能是库存不足,请与管理员联系!";
						return result;
					}
					//增加库存锁定量
					if (!psService.updateProductLockCount(psBean.getId(), bsbyProductBean.getBsby_count())) {
						result = "库存操作失败,可能是库存不足,请与管理员联系!";
						return result;
					}

					//锁定货位库存
					//出库
					if(!cargoService.updateCargoProductStockCount(bsbyCargo.getCargoProductStockId(), -bsbyCargo.getCount())){
						result = "货位库存操作失败,货位库存不足!";
						return result;
					}
					if(!cargoService.updateCargoProductStockLockCount(bsbyCargo.getCargoProductStockId(), bsbyCargo.getCount())){
						result = "货位库存操作失败，货位库存不足!";
						return result;
					}
				}
			}else if(bean2.getType() == 1){
				for (; it.hasNext();) {
					BsbyProductBean bsbyProductBean = (BsbyProductBean) it.next();
					BsbyProductCargoBean bsbyCargo = service.getBsbyProductCargo("bsby_product_id = "+bsbyProductBean.getId());
					if(bsbyCargo == null){
						result = "货位信息异常,操作失败,请与管理员联系!";
						return result;
					}

					//锁定货位空间
					if(cargoService.getCargoInfo("id = "+bsbyCargo.getCargoId()+" and status = 0")==null){
						result = "目的货位不存在或已被清空，操作失败，请与管理员联系!";
						return result;
						
					}
					if(!cargoService.updateCargoInfo("space_lock_count = space_lock_count + "+bsbyCargo.getCount(),"id = "+bsbyCargo.getCargoId())){
						result = "操作失败!";
						return result;
					}
				}
			}
			if(!service.updateBsbyOperationnoteBean(" current_type=1", " id=" + bsbyId)){
				result = "更新报损报溢单状态时失败!";
				return result;
			}
			result = bean2;
		return result;
	}
	
	public String createCode(String code, IBsByServiceManagerService service) {
		int maxid = service.getNumber("id", "bsby_operationnote", "max", "id > 0 and receipts_number like '" + code + "%'");
		BsbyOperationnoteBean plan;
		plan = service.getBuycode("receipts_number like '" + code + "%'");
		if (plan == null) {
			// 当日第一份计划,编号最后三位 001
			code += "0001";
		} else {
			// 获取当日计划编号最大值
			plan = service.getBuycode("id =" + maxid);
			String _code = plan.getReceipts_number();
			int number = Integer.parseInt(_code.substring(_code.length() - 4));
			number++;
			code += String.format("%04d", new Object[] { new Integer(number) });
		}
		return code;
	}
	
	/**
	 * 	说明：1.根据不同的区域的不同类型的库和不同商品得到指定区域中的库类型的可用商品和锁定商品的和 
	 * 
	 * 	日期：2013-04-17
	 * 
	 * 	作者：石远飞
	 */
	public int getProductCount(int productid, int area, int type) {
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		WareService wareService = new WareService(this.dbOp);
		int x = 0;
		voProduct product = wareService.getProduct(productid);
		product.setPsList(psService.getProductStockList("product_id=" + productid, -1, -1, null));
		x = product.getStock(area, type);
		return x;

	}
	/**
	 * 	说明：1.得到报损或者报溢后的产品的数量 
	 * 
	 * 	日期：2013-4-15
	 * 
	 * 	作者：石远飞
	 */
	public static int updateProductCount(int x, int type, int count) {
		int result = 0;
		if (type == 0) {
			// 报损
			result = x - count;
		} else {
			result = x + count;
		}
		return result;
	}
	
	/**
	 * 生成货位间调拨单的完整流程  除了交接处  有省略
	 * @param dbOp
	 * @param areaId
	 * @param user
	 * @param inCargoInfoBean
	 * @param outCargoInfoBean
	 * @param inCargoProductStockBean
	 * @param outCargoProductStockBean
	 * @param productId
	 * @param count
	 * @return
	 */
	public String generateDeploy3(DbOperation dbOp, int areaId, voUser user,
			CargoInfoBean inCargoInfoBean, CargoInfoBean outCargoInfoBean, CargoProductStockBean inCargoProductStockBean, CargoProductStockBean outCargoProductStockBean, int productId, int count) {

		String result = "success";
		//String[] cargoProducStockIds = new String[3];

		//新作业单编号：HW1103110001
		String code = "HWD"+DateUtil.getNow().substring(2,10).replace("-", "");   
		WareService wareService = new WareService(this.dbOp);
		ICargoService service = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		IStockService stockService = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		try{
			String storageCode = wareService.getString("whole_code", "cargo_info", "id = (select cargo_id from cargo_product_stock where id = "+outCargoProductStockBean.getId()+")");
			storageCode = storageCode.substring(0,storageCode.indexOf("-"));
			CargoInfoStorageBean storage=service.getCargoInfoStorage("whole_code='"+storageCode+"'");
			if(storage==null){
				result = "地区错误！";
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
			cargoOper.setRemark("货位盘点异常调拨平仓");
			cargoOper.setSource("");
			cargoOper.setStockInType(inCargoInfoBean.getStoreType());
			cargoOper.setStockOutType(outCargoInfoBean.getStoreType());
			cargoOper.setStorageCode(storageCode);
			cargoOper.setType(CargoOperationBean.TYPE3);
			cargoOper.setStockInArea(areaId);
			cargoOper.setStockOutArea(areaId);
			cargoOper.setStatus(CargoOperationProcessBean.OPERATION_STATUS28);
			cargoOper.setLastOperateDatetime(DateUtil.getNow());
			if(!service.addCargoOperation(cargoOper)){
				result = "添加补货单失败！";
				return result;
			}//添加cargo_operation

			int cargoOperId = service.getDbOp().getLastInsertId();
			
			//-----
			int cpsId = outCargoProductStockBean.getId();
			CargoProductStockBean bean = service.getCargoProductStock("id = "+cpsId);
			if(bean==null){
				result = "库存错误，无法添加调拨单！";
				return result;
			}

			CargoInfoBean ci=service.getCargoInfo("id="+bean.getCargoId());
			CargoOperationCargoBean coc = new CargoOperationCargoBean();
			voProduct product1=wareService.getProduct(bean.getProductId());
			coc.setOperId(cargoOperId);
			coc.setInCargoProductStockId(0);
			coc.setProductId(bean.getProductId());
			coc.setType(1);
			coc.setOutCargoProductStockId(outCargoProductStockBean.getId());
			coc.setOutCargoWholeCode(ci.getWholeCode());
			coc.setStockCount(count);
			if(!service.addCargoOperationCargo(coc)){
				result = "添加补货单详细信息失败！";
				return result;
			}
			
			CargoOperationLogBean logBean1=new CargoOperationLogBean();
			logBean1.setOperId(cargoOperId);
			logBean1.setOperDatetime(DateUtil.getNow());
			logBean1.setOperAdminId(user.getId());
			logBean1.setOperAdminName(user.getUsername());
			StringBuilder logRemark=new StringBuilder("制单：");
			
			logRemark.append("商品");
			logRemark.append(product1.getCode());
			logRemark.append("，");
			logRemark.append("源货位（");
			logRemark.append(ci.getWholeCode());
			logRemark.append("）");
			
			//分配目的货位
			/*CargoInfoBean inCi = null;
			if(ci.getStoreType() == 0||ci.getStoreType() == 4){
				List inCpsList=service.getCargoAndProductStockList("ci.area_id="+ci.getAreaId()+" and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="+ci.getStoreType()+" and ci.space_lock_count=0 and cps.product_id="+coc.getProductId(), -1, -1, null);
				if(inCpsList.size()>0){
					CargoProductStockBean cps=(CargoProductStockBean)inCpsList.get(0);
					CargoInfoBean cargoInfo=cps.getCargoInfo();
					inCi=cargoInfo;
				}
				if(inCi==null){//查询一个未使用的货位
					CargoInfoBean tempInCi=service.getCargoInfo("area_id="+ci.getAreaId()+" and stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and store_type="+ci.getStoreType()+" and status=1");
					if(tempInCi!=null){
						if(!service.updateCargoInfo("status=0", "id="+tempInCi.getId())){
							result = "数据库操作失败！";
							return result;
						}
						CargoProductStockBean newCps=new CargoProductStockBean();
						newCps.setCargoId(tempInCi.getId());
						newCps.setProductId(coc.getProductId());
						newCps.setStockCount(0);
						newCps.setStockLockCount(0);
						if(!service.addCargoProductStock(newCps)){
							result = "数据库操作失败！";
							return result;
						}
						inCi=tempInCi;
					}
				}
			}else if(ci.getStoreType() == 1){
				List inCpsList=service.getCargoAndProductStockList("ci.area_id="+ci.getAreaId()+" and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="+ci.getStoreType()+" and ci.space_lock_count=0 and cps.product_id="+coc.getProductId(),-1,-1,null);
				if(inCpsList.size()>0){
					CargoProductStockBean cps=(CargoProductStockBean)inCpsList.get(i);
					CargoInfoBean cargoInfo=cps.getCargoInfo();
					inCi=cargoInfo;
				}
				if(inCi==null){ //查询非此SKU整件区货位
					inCpsList=service.getCargoAndProductStockList("ci.area_id="+ci.getAreaId()+" and ci.stock_type="+CargoInfoBean.STOCKTYPE_QUALIFIED+" and ci.store_type="+ci.getStoreType()+" and ci.space_lock_count=0",-1,-1,null);
					if(inCpsList.size()>0){
						CargoProductStockBean cps=(CargoProductStockBean)inCpsList.get(i);
						CargoInfoBean cargoInfo=cps.getCargoInfo();
						inCi=cargoInfo;
						if(cargoInfo.getStatus()!=0){
							if(!service.updateCargoInfo("status=0", "id="+cargoInfo.getId())){
								result = "数据库操作失败！";
								return result;
							}
						}
					}
				}
			}*/
			CargoInfoBean inCi1 = inCargoInfoBean;
			CargoOperationCargoBean inCoc1=new CargoOperationCargoBean();
			inCoc1.setOperId(cargoOperId);
			inCoc1.setProductId(coc.getProductId());
			inCoc1.setOutCargoProductStockId(bean.getId());
			inCoc1.setOutCargoWholeCode(ci.getWholeCode());
			inCoc1.setStockCount(count);
			inCoc1.setType(0);
			inCoc1.setUseStatus(1);
			//CargoProductStockBean inCps=service.getCargoProductStock("cargo_id="+inCi.getId()+" and product_id="+coc.getProductId());
			/*if(inCps==null){//如果该货位没有库存记录，则添加新的库存记录
				inCps=new CargoProductStockBean();
				inCps.setCargoId(inCi.getId());
				inCps.setProductId(coc.getProductId());
				inCps.setStockCount(0);
				inCps.setStockLockCount(0);
				service.addCargoProductStock(inCps);
				inCps.setId(service.getDbOp().getLastInsertId());
			}*/
			inCoc1.setInCargoProductStockId(inCargoProductStockBean.getId());
			inCoc1.setInCargoWholeCode(inCi1.getWholeCode());
			if(!service.addCargoOperationCargo(inCoc1)){
				result = "数据库操作失败！";
				return result;
			}//添加目的货位记录
			
			logRemark.append("，");
			logRemark.append("目的货位（");
			logRemark.append(inCi1.getWholeCode());
			logRemark.append("）");
			

			logBean1.setRemark(logRemark.toString());
			service.addCargoOperationLog(logBean1);
//----
			CargoOperLogBean operLog1=new CargoOperLogBean();//员工操作日志
			operLog1.setOperId(service.getCargoOperation("code='"+cargoOper.getCode()+"'").getId());
			operLog1.setOperCode(cargoOper.getCode());
			CargoOperationProcessBean process1=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS28);
			operLog1.setOperName(process1.getOperName());
			operLog1.setOperDatetime(DateUtil.getNow());
			operLog1.setOperAdminId(user.getId());
			operLog1.setOperAdminName(user.getUsername());
			operLog1.setHandlerCode("");
			operLog1.setEffectTime(CargoOperLogBean.EFFECT_TIME0);
			operLog1.setRemark("");
			operLog1.setPreStatusName("无");
			operLog1.setNextStatusName(process1.getStatusName());
			if(!service.addCargoOperLog(operLog1)){
				result = "添加日志数据时发生异常！";
				return result;
			}
			//---添加结束   该加 确认的地方了
			cargoOper.setId(cargoOperId);
			
			if(!service.updateCargoOperation(
					"status = "+CargoOperationProcessBean.OPERATION_STATUS29+"," +
							"effect_status=0,last_operate_datetime='"+DateUtil.getNow()+"'," +
									"confirm_datetime = '"+DateUtil.getNow()+"'," +
											"confirm_user_name = '"+user.getUsername()+"'", "id = "+cargoOper.getId())){
				result = "更新调拨单状态失败！";
				return result;
			}
			
			//----------------这里把锁货位库存的地方全给省了
			
			//修改上一操作日志的时效
			CargoOperLogBean lastLog1=service.getCargoOperLog("oper_id="+cargoOper.getId()+" order by id desc limit 1");//当前作业单的最后一条日志
			if(lastLog1!=null&&lastLog1.getEffectTime()==0){//如果不是进行中，不需要再改时效
				CargoOperationProcessBean tempProcess=service.getCargoOperationProcess("id="+cargoOper.getStatus());//生成作业单
				int effectTime=tempProcess.getEffectTime();//生成阶段时效
				String lastOperateTime=lastLog1.getOperDatetime();
				SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				long date1=sdf.parse(lastOperateTime).getTime();
				long date2=sdf.parse(DateUtil.getNow()).getTime();
				if(date1+effectTime*60*1000<date2){//已超时
					service.updateCargoOperLog("effect_time=1", "id="+lastLog1.getId());
				}
			}

			CargoOperLogBean operLog2=new CargoOperLogBean();
			operLog2.setOperId(cargoOper.getId());
			operLog2.setOperCode(cargoOper.getCode());
			CargoOperationProcessBean process3=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS28);
			CargoOperationProcessBean process4=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS29);
			operLog2.setOperName(process4.getOperName());
			operLog2.setOperDatetime(DateUtil.getNow());
			operLog2.setOperAdminId(user.getId());
			operLog2.setOperAdminName(user.getUsername());
			operLog2.setHandlerCode("");
			operLog2.setEffectTime(0);
			operLog2.setRemark("");
			operLog2.setPreStatusName(process3.getStatusName());
			operLog2.setNextStatusName(process4.getStatusName());
			service.addCargoOperLog(operLog2);

			
		   // 确认阶段完成 
			
			// 交接阶段
			//		int status = StringUtil.toInt(request.getParameter("status"));
			//		String remark = StringUtil.convertNull(request.getParameter("remark")).trim();
			int nextStatus=CargoOperationProcessBean.OPERATION_STATUS30;//下一个状态
				CargoOperationBean cargoOperation1 = service.getCargoOperation("id = "+cargoOper.getId());
				if(cargoOperation1 == null){
					result = "该作业单不存在！";
					return result;
				}
				if(cargoOperation1.getStatus()>=nextStatus){
					result = "该作业单状态已被更新，操作失败！";
					return result;
				}
				CargoOperationLogBean logBean = new CargoOperationLogBean();
				logBean.setOperId(cargoOper.getId());
				logBean.setOperDatetime(DateUtil.getNow());
				logBean.setOperAdminId(user.getId());
				logBean.setOperAdminName(user.getUsername());

				CargoOperationProcessBean process5=service.getCargoOperationProcess("id="+cargoOperation1.getStatus());//当前阶段
				CargoOperationProcessBean process6=service.getCargoOperationProcess("id="+nextStatus);//下个阶段
				if(process5==null){
					result ="作业单流程信息错误！";
					return result;
				}
				if(process6==null){
					result = "作业单流程信息错误！";
					return result;
				}
				int handleType=process6.getHandleType();//操作方式，0人工确认，1设备确认
				if(handleType!=0){
					result = "当前操作方式为设备确认！";
					return result;
				}
//				int confirmType=process2.getConfirmType();//作业判断，0不做判断，1源货位，2目的货位，人工确认不需要判断该条件
//				int deptId1=process2.getDeptId1();//职能归属，一级部门，人工确认不需要判断该条件
//				int deptId2=process2.getDeptId2();//职能归属，二级部门，人工确认不需要判断该条件
//				int storageId=process2.getStorageId();//所属仓库，人工确认不需要判断该条件

				//修改上一操作日志的时效
				CargoOperLogBean lastLog2=service.getCargoOperLog("oper_id="+cargoOper.getId()+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog2!=null&&lastLog2.getEffectTime()==0){//如果不是进行中，不需要再改时效
					int effectTime=process5.getEffectTime();//上阶段时效
					String lastOperateTime=lastLog2.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						service.updateCargoOperLog("effect_time=1", "id="+lastLog2.getId());
					}
				}

				if(!service.updateCargoOperation(
						"status="+nextStatus+",effect_status = 0," +
								"last_operate_datetime='"+DateUtil.getNow()+"'"+(
										cargoOperation1.getStatus()==CargoOperationProcessBean.OPERATION_STATUS29?("," +
												"auditing_datetime='"+DateUtil.getNow()+"'," +
														"auditing_user_id="+user.getId()+"," +
																"auditing_user_name='"+user.getUsername()+"'"):""), "id="+cargoOper.getId())){
					result = "更新调拨单状态失败！";
					return result;
				}

				CargoOperLogBean operLog3=new CargoOperLogBean();
				operLog3.setOperId(cargoOper.getId());
				operLog3.setOperCode(cargoOperation1.getCode());
				operLog3.setOperName(process6.getOperName());
				operLog3.setOperDatetime(DateUtil.getNow());
				operLog3.setOperAdminId(user.getId());
				operLog3.setOperAdminName(user.getUsername());
				operLog3.setHandlerCode("");
				operLog3.setEffectTime(0);
				operLog3.setRemark("");
				operLog3.setPreStatusName(process5.getStatusName());
				operLog3.setNextStatusName(process6.getStatusName());
				service.addCargoOperLog(operLog3);
			//交接阶段完成
			
			//完成阶段

				CargoOperationBean cargoOperation = service.getCargoOperation("id = "+cargoOper.getId());
				if(cargoOperation.getStatus()>=CargoOperationProcessBean.OPERATION_STATUS34){
					result = "该作业单状态已被更新，操作失败！";
					return result;
				}
				//完成货位库存量操作
				List outCocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and type = 1", -1, -1, "id asc");
				for(int i=0;i<outCocList.size();i++){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)outCocList.get(i);
					CargoProductStockBean outCps = service.getCargoProductStock("id = "+outCoc.getOutCargoProductStockId());
					CargoInfoBean outCi = service.getCargoInfo("id = "+outCps.getCargoId());
					voProduct product = wareService.getProduct(outCoc.getProductId());
					product.setPsList(psService.getProductStockList("product_id = "+product.getId(), -1, -1, "id asc"));
					int stockOutCount = 0;
					List inCocList = service.getCargoOperationCargoList("oper_id = "+cargoOperation.getId()+" and out_cargo_product_stock_id = "+outCoc.getOutCargoProductStockId()+" and type = 0 and use_status = 1", -1, -1, "id asc");
					for(int j=0;j<inCocList.size();j++){
						CargoOperationCargoBean inCoc = (CargoOperationCargoBean)inCocList.get(j);
						CargoProductStockBean inCps = service.getCargoProductStock("id = "+inCoc.getInCargoProductStockId());
						CargoInfoBean inCi = service.getCargoInfo("whole_code = '"+inCoc.getInCargoWholeCode()+"'");
						//因为冻结的步骤 被省略了 所以这里不是从锁定量来加了
						if(inCps!=null&&outCps!=null){
							if(!service.updateCargoProductStockCount(inCps.getId(), inCoc.getStockCount())){
								result =  "操作失败，货位冻结库存不足！";
								return result;
							}
							if(!service.updateCargoProductStockCount(outCps.getId(), -inCoc.getStockCount())){
								result = "操作失败，货位冻结库存不足！";
								return result;
							}

							//调整合格库库存，修改批次，添加进销存卡片
							/*if(inCi.getAreaId()!=outCi.getAreaId()){
								CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+inCi.getAreaId());
								ProductStockBean inProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+product.getId());
								if(inProductStock==null){
									result = "合格库库存数据错误！";
									return result;
								}
								if (!psService.updateProductStockCount(inProductStock.getId(),inCoc.getStockCount())) {
									result = "库存操作失败，可能是库存不足，请与管理员联系！";
									return result;
								}
								productStockCount+=inCoc.getStockCount();
								
								ProductStockBean psIn = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+inCi.getAreaId()+" and type="+inCi.getStockType());
								ProductStockBean psOut = psService.getProductStock("product_id="+outCoc.getProductId()+" and area="+outCi.getAreaId()+" and type="+outCi.getStockType());
								//批次修改开始
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
												result = "数据库操作失败！";
								               return result;
											}
											stockBatchCount = batch.getBatchCount();
										}else{
											if(!stockService.updateStockBatch("batch_count = batch_count-"+stockExchangeCount, "id="+batch.getId())){
												result =  "数据库操作失败！";
								                return result;
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
										if(!stockService.addStockBatchLog(batchLog)){
											 result = "添加失败！";
								             return result;
										}
										
										stockoutPrice = stockoutPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
										
										//入库
										StockBatchBean batchBean = stockService.getStockBatch("code='"+batch.getCode()+"' and product_id="+batch.getProductId()+" and stock_type="+inCi.getStockType()+" and stock_area="+inCi.getAreaId());
										if(batchBean!=null){
											if(!stockService.updateStockBatch("batch_count = batch_count+"+stockBatchCount, "id="+batchBean.getId())){
												result = "数据库操作失败！";
								                return result;
											}
										}else{
											int ticket = FinanceSellProductBean.queryTicket(stockService.getDbOp(), batch.getCode());
											StockBatchBean newBatch = new StockBatchBean();
											newBatch.setCode(batch.getCode());
											newBatch.setProductId(batch.getProductId());
											newBatch.setPrice(batch.getPrice());
											newBatch.setBatchCount(stockBatchCount);
											newBatch.setProductStockId(psIn.getId());
											newBatch.setStockArea(inCi.getAreaId());
											newBatch.setStockType(psIn.getType());
											newBatch.setTicket(ticket);
											newBatch.setCreateDateTime(stockService.getStockBatchCreateDatetime(batch.getCode(),batch.getProductId()));
											if(!stockService.addStockBatch(newBatch)){
												result = "添加失败！";
												return result;
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
										if(!stockService.addStockBatchLog(batchLog)){
											result =  "添加失败！";
											return result;
										}
										
										stockExchangeCount -= batch.getBatchCount();
										index++;
										
										stockinPrice = stockinPrice + batchLog.getBatchCount()*batchLog.getBatchPrice();
									} while (stockExchangeCount>0&&index<sbList.size());
								}
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
								
							}*/

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
							csc.setCurrentCargoStock(inCps.getStockCount());
							csc.setCargoStoreType(inCi.getStoreType());
							csc.setCargoWholeCode(inCi.getWholeCode());
							csc.setStockPrice(product.getPrice5());
							csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
							service.addCargoStockCard(csc);

							stockOutCount = stockOutCount + inCoc.getStockCount();
						}else{
							result =  "库存错误，无法提交！";
							return result;
						}

						/*if(outCi.getAreaId()!=inCi.getAreaId()){
							//更新订单缺货状态
							this.updateLackOrder(outCoc.getProductId());
						}*/
					}

					//调整合格库库存
					/*CargoInfoAreaBean cargoInfoArea=service.getCargoInfoArea("id="+outCi.getAreaId());
					ProductStockBean outProductStock=psService.getProductStock("area="+cargoInfoArea.getOldId()+" and type="+ProductStockBean.STOCKTYPE_QUALIFIED+" and product_id="+product.getId());
					if(outProductStock==null){
						result = "合格库库存数据错误！";
						return result;
					}
					if (!psService.updateProductLockCount(outProductStock.getId(),-productStockCount)) {
						result = "库存操作失败，可能是库存不足，请与管理员联系！";
						return result;
					}*/

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
					csc.setCurrentCargoStock(outCps.getStockCount());
					csc.setCargoStoreType(outCi.getStoreType());
					csc.setCargoWholeCode(outCi.getWholeCode());
					csc.setStockPrice(product.getPrice5());
					csc.setAllStockPriceSum((new BigDecimal(csc.getAllStock())).multiply(new BigDecimal(StringUtil.formatDouble2(csc.getStockPrice()))).doubleValue());
					service.addCargoStockCard(csc);
				}	
				if(!service.updateCargoOperation(
						"status="+CargoOperationProcessBean.OPERATION_STATUS34+"," +
								"effect_status = 2,last_operate_datetime='"+DateUtil.getNow()+"'"+"," +
										"complete_datetime='"+DateUtil.getNow()+"'," +
												"complete_user_id="+user.getId()+"," +
														"complete_user_name='"+user.getUsername()+"'", "id="+cargoOperation.getId())){
					result =  "更新调拨单状态失败！";
					return result;
				}

				CargoOperationProcessBean process=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcessBean process2=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS34);//下个阶段
				if(process==null){
					result =  "作业单流程信息错误！";
					return result;
				}
				if(process2==null){
					result = "作业单流程信息错误！";
					return result;
				}

				//修改上一操作日志的时效
				CargoOperLogBean lastLog5=service.getCargoOperLog("oper_id="+cargoOperation.getId()+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog5!=null&&lastLog5.getEffectTime()==1){//如果不是进行中，不需要再改时效
					int effectTime=process.getEffectTime();//上阶段时效
					String lastOperateTime=lastLog5.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						service.updateCargoOperLog("effect_time=2", "id="+lastLog5.getId());
					}
				}

				CargoOperLogBean operLog5=new CargoOperLogBean();
				operLog5.setOperId(cargoOperation.getId());
				operLog5.setOperCode(cargoOperation.getCode());
				operLog5.setOperName(process2.getOperName());
				operLog5.setOperDatetime(DateUtil.getNow());
				operLog5.setOperAdminId(user.getId());
				operLog5.setOperAdminName(user.getUsername());
				operLog5.setHandlerCode("");
				operLog5.setEffectTime(2);
				operLog5.setRemark("");
				operLog5.setPreStatusName(process.getStatusName());
				operLog5.setNextStatusName(process2.getStatusName());
				service.addCargoOperLog(operLog5);
				
				//复核s
				//作业成功
				
				if(!service.updateCargoOperation(
						"effect_status="+CargoOperationBean.EFFECT_STATUS3+"," +
								"last_operate_datetime='"+DateUtil.getNow()+"'", "id="+cargoOper.getId())){
					result = "更新调拨单状态失败！";
					return result;
				}
				
				CargoOperationProcessBean process7=service.getCargoOperationProcess("id="+cargoOperation.getStatus());//当前阶段
				CargoOperationProcessBean process8=service.getCargoOperationProcess("id="+CargoOperationProcessBean.OPERATION_STATUS34);//下个阶段
				if(process7==null){
					result = "作业单流程信息错误！";
					return result;
				}
				if(process8==null){
					result = "作业单流程信息错误！";
					return result;
				}

				//修改上一操作日志的时效
				CargoOperLogBean lastLog=service.getCargoOperLog("oper_id="+cargoOper.getId()+" order by id desc limit 1");//当前作业单的最后一条日志
				if(lastLog!=null&&lastLog.getEffectTime()==0){//如果不是进行中，不需要再改时效
					int effectTime=process7.getEffectTime();//上阶段时效
					String lastOperateTime=lastLog.getOperDatetime();
					SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					long date1=sdf.parse(lastOperateTime).getTime();
					long date2=sdf.parse(DateUtil.getNow()).getTime();
					if(date1+effectTime*60*1000<date2){//已超时
						service.updateCargoOperLog("effect_time=1", "id="+lastLog.getId());
					}
				}

				CargoOperLogBean operLog=new CargoOperLogBean();
				operLog.setOperId(cargoOper.getId());
				operLog.setOperCode(cargoOperation.getCode());
				operLog.setOperName("作业复核");
				operLog.setOperDatetime(DateUtil.getNow());
				operLog.setOperAdminId(user.getId());
				operLog.setOperAdminName(user.getUsername());
				operLog.setHandlerCode("");
				operLog.setEffectTime(3);
				operLog.setRemark("货位盘点调拨单");
				operLog.setPreStatusName(process7.getStatusName());
				operLog.setNextStatusName(process8.getStatusName());
				service.addCargoOperLog(operLog);

		}catch (Exception e) {
			result = "系统异常！";
		}
		return result;
	}
	/**
	 * 判定 列表中是否有 盘点中 或者已盘点的， 不能在操作
	 * @param sortingAbnormalProductList
	 * @return
	 */
	public boolean hasStatusChecking(
			List<SortingAbnormalProductBean> sortingAbnormalProductList) {
		boolean result = false;
		if( sortingAbnormalProductList == null ) {
			result = false;
		} else {
			int x = sortingAbnormalProductList.size();
			for( int i = 0 ; i < x; i ++ ) {
				SortingAbnormalProductBean sapBean = sortingAbnormalProductList.get(i);
				if( sapBean.getStatus() == SortingAbnormalProductBean.STATUS_CHECKING || sapBean.getStatus() == SortingAbnormalProductBean.STATUS_CHECKED ) {
					result = true;
				}
			}
		}
		return result;
	}
	public void updateLackOrder(int productId){
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult");
		DbOperation dbOp2 = new DbOperation();
		dbOp2.init();
		IAdminService adminService = ServiceFactory.createAdminService(dbOp);
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

			List lackOrders = adminService.getOrdersByProducts("a.stockout_deal in (4,5,6) and b.product_id in ("+productIds+")", -1, -1, null);
			lackOrders.addAll(adminService.getOrdersByPresents("a.stockout_deal in (4,5,6) and b.product_id in ("+productIds+")", -1, -1, null));
			Iterator iter = lackOrders.listIterator();
			while(iter.hasNext()){
				voOrder order = (voOrder)iter.next();

				// 判断订单中商品的库存是否满足，根据库存状态，设置订单发货状态
				List orderProductList = adminService.getOrderProducts(order.getId());
				List orderPresentList = adminService.getOrderPresents(order.getId());
				orderProductList.addAll(orderPresentList);

				List detailList = new ArrayList();
				Iterator detailIter = orderProductList.listIterator();
				while (detailIter.hasNext()) {
					voOrderProduct vop = (voOrderProduct) detailIter.next();
					voProduct product = adminService.getProduct(vop.getProductId());
					if (product.getIsPackage() == 1) { // 如果这个产品是套装
						ppList = ppService.getProductPackageList("parent_id=" + product.getId(), -1, -1, null);
						ppIter = ppList.listIterator();
						while (ppIter.hasNext()) {
							ProductPackageBean ppBean = (ProductPackageBean) ppIter.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount() * ppBean.getProductCount());
							voProduct tempProduct = adminService.getProduct(ppBean.getProductId());
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
	/**
	 * 说明：获取盘点货位列表
	 * 
	 * 日期：2013-6-15
	 * 
	 * 作者：石远飞
	 */
	public Map<String,List<AbnormalCargoCheckProductBean>> getaccpMap(AbnormalCargoCheckBean accBean,DbOperation dbOp,int inventoryFlag,voUser user ,int areaId){
		List<AbnormalCargoCheckProductBean> accpBeanList = new ArrayList<AbnormalCargoCheckProductBean>();
		Map<String,List<AbnormalCargoCheckProductBean>> map = new HashMap<String, List<AbnormalCargoCheckProductBean>>();
		StringBuffer accpBuffer = new StringBuffer();
		StringBuffer binding = new StringBuffer();
		StringBuffer set = new StringBuffer();
		accpBuffer.append(" and 1=1");
		binding.append(" and 1=1");
		String error = null;
		try {
			if(inventoryFlag == 1){
				if(accBean == null){
					accBean = this.getAbnormalCargoCheck("(status=" + AbnormalCargoCheckBean.STATUS0 + " or status=" + AbnormalCargoCheckBean.STATUS1 + ") and area=" + areaId);
				}
				if(accBean == null){
					error = "没有未开始的盘点任务!";
					map.put(error, accpBeanList);
					return map;
				}
				binding.append(" and status=" + AbnormalCargoCheckProductBean.STATUS_WAIT_FIRST_CHECK + " and first_check_user_id=" + user.getId());
				accpBuffer.append(" and status=" + AbnormalCargoCheckProductBean.STATUS_WAIT_FIRST_CHECK + " and first_check_user_id=0");
				set.append("first_check_user_id=" + user.getId());
			}else if(inventoryFlag == 2){
				if(accBean == null){
					accBean = this.getAbnormalCargoCheck("status in(" + AbnormalCargoCheckBean.STATUS1+","+AbnormalCargoCheckBean.STATUS2+")" + " and area=" + areaId);
				}
				if(accBean == null){
					error= "没有一盘中或二盘中的盘点任务!";
					map.put(error, accpBeanList);
					return map;
				}
				binding.append(" and status=" + AbnormalCargoCheckProductBean.STATUS_WAIT_SECOND_CHECK + " and first_check_user_id<>" + user.getId() + " and second_check_user_id=" + user.getId());
				accpBuffer.append(" and status=" + AbnormalCargoCheckProductBean.STATUS_WAIT_SECOND_CHECK + " and first_check_user_id<>" + user.getId() + " and second_check_user_id=0");
				set.append("second_check_user_id=" + user.getId());
			}else if(inventoryFlag == 3){
				if(accBean == null){
					accBean = this.getAbnormalCargoCheck("status in(" + AbnormalCargoCheckBean.STATUS2 + ","+AbnormalCargoCheckBean.STATUS3+")"  + " and area=" + areaId);
				}
				if(accBean == null){
					error = "没有二盘中或终盘中的盘点任务!";
					map.put(error, accpBeanList);
					return map;
				}
				binding.append(" and status="+ AbnormalCargoCheckProductBean.STATUS_WAIT_THRID_CHECK + " and first_check_user_id<>" + user.getId() + " and second_check_user_id<>" + user.getId() + " and third_check_user_id=" + user.getId());
				accpBuffer.append(" and status="+ AbnormalCargoCheckProductBean.STATUS_WAIT_THRID_CHECK + " and first_check_user_id<>" + user.getId() + " and second_check_user_id<>" + user.getId()  + " and third_check_user_id=0");
				set.append("third_check_user_id=" + user.getId());
			}
			List<AbnormalCargoCheckProductBean> list = this.getAbnormalCargoCheckProductList("abnormal_Cargo_Check_id=" + accBean.getId()+ binding.toString(), -1, 5, "cargo_whole_code");
			boolean flag =false;
			if(list == null || list.size() == 0){
				flag = true;
				list = this.getAbnormalCargoCheckProductList("abnormal_Cargo_Check_id=" + accBean.getId()+ accpBuffer.toString(), -1, 5, "cargo_whole_code");
			}
//			dbOp.startTransaction();  //开启事务
			if(list != null && list.size() > 0){
				for(AbnormalCargoCheckProductBean b : list){
					AbnormalCargoCheckProductBean accpBean = new AbnormalCargoCheckProductBean();
					voProduct product = this.getProductById(b.getProductId());
					if(product == null){
						continue;
					}
					accpBean.setCargoWholeCode(b.getCargoWholeCode());
					accpBean.setProductCode(product.getCode());
					accpBean.setProductName(product.getOriname());
					accpBean.setId(b.getId());
					accpBeanList.add(accpBean);
					if(flag){
						if(!this.updateAbnormalCargoCheckProduct(set.toString(), "id=" + accpBean.getId())){
							dbOp.rollbackTransaction();
							error = "领取盘点任务失败!";
							map.put(error, accpBeanList);
							return map;
						}
					}
				}
			}
//			dbOp.commitTransaction(); //提交事务
			error = "success";
			map.put(error, accpBeanList);
		} catch (Exception e) {
			dbOp.rollbackTransaction();
			e.printStackTrace();
		}finally{
			
		}
		
		return map;
	}

	public String getCargoWholeCodes(int productId, int wareArea) {
		SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, this.dbOp);
		String result = "";
		String subSql = "select sap.* from sorting_abnormal sa, sorting_abnormal_product sap where sa.id = sap.sorting_abnormal_id and sap.status=" + SortingAbnormalProductBean.STATUS_WAIT_FOR_CHECK;
		if( wareArea != -1 ) {
			subSql += " and sa.ware_area = " + wareArea ;
		} else {
			subSql += " and sa.ware_area = 1000" ;
		}
		subSql += " and sap.product_id = "+productId+" group by sap.cargo_whole_code";
		List<SortingAbnormalProductBean> list = sortingAbnormalDisposeService.getSortingAbnormalProductList3(subSql, -1, -1, null);
		if(list.size() == 0 ) { 
			
		} else {
			for(int j = 0 ; j < list.size(); j++ ) {
				SortingAbnormalProductBean sapBean2 = (SortingAbnormalProductBean)list.get(j);
				if( j == (list.size() -1 )) {
					result += sapBean2.getCargoWholeCode();
				} else {
					result += sapBean2.getCargoWholeCode() + ",";
				}
			}
		}
		return result;
	}
}

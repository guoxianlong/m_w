package mmb.stock.stat;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import mmb.stock.cargo.CargoDeptAreaService;
import mmb.stock.cargo.CartonningInfoService;
import mmb.stock.cargo.CartonningStandardCountBean;
import mmb.ware.WareService;
import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.order.UserOrderPackageTypeBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.util.DateUtil;
import adultadmin.util.MyRuntimeException;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class ProductWarePropertyService extends BaseServiceImpl {
	
	static Pattern pattern = Pattern.compile("\\d{1,9}");
	
	
	
	public ProductWarePropertyService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public ProductWarePropertyService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	
	//商品物流属性日志
	public boolean addProductWarePropertyLog(ProductWarePropertyLogBean bean) {
		return addXXX(bean, "product_ware_property_log");
	}

	public List getProductWarePropertyLogList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "product_ware_property_log", "mmb.stock.stat.ProductWarePropertyLogBean");
	}
	
	public int getProductWarePropertyLogCount(String condition) {
		return getXXXCount(condition, "product_ware_property_log", "id");
	}

	public ProductWarePropertyBean getProductWarePropertyLog(String condition) {
		return (ProductWarePropertyBean) getXXX(condition, "product_ware_property_log",
		"mmb.stock.stat.ProductWarePropertyLogBean");
	}

	public boolean updateProductWarePropertyLog(String set, String condition) {
		return updateXXX(set, condition, "product_ware_property_log");
	}

	public boolean deleteProductWarePropertyLog(String condition) {
		return deleteXXX(condition, "product_ware_property_log");
	}
	
	//商品物流分类
	public boolean addProductWareType(ProductWareTypeBean bean) {
		return addXXX(bean, "product_ware_type");
	}

	public List getProductWareTypeList(String condition, int index, int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "product_ware_type", "mmb.stock.stat.ProductWareTypeBean");
	}
	
	public int getProductWareTypeCount(String condition) {
		return getXXXCount(condition, "product_ware_type", "id");
	}

	public ProductWareTypeBean getProductWareType(String condition) {
		return (ProductWareTypeBean) getXXX(condition, "product_ware_type",
		"mmb.stock.stat.ProductWareTypeBean");
	}

	public boolean updateProductWareType(String set, String condition) {
		return updateXXX(set, condition, "product_ware_type");
	}

	public boolean deleteProductWareType(String condition) {
		return deleteXXX(condition, "product_ware_type");
	}
	/**
	 * 根据月在编人数 计算日在编人数
	 * @param number
	 * @return
	 */
	public int calculateWorkProportion( int number ) {
		double result = (number*5)/7;
		return (int) result;
	}
	
	/**
	 * 根据月添加排班计划
	 * @param monthCount
	 * @param list
	 * @param statService
	 * @throws Exception
	 */
	public void addCheckStaffMonth(int monthCount, List list, StatService statService,int areaId) throws Exception {
		int dayCount = calculateWorkProportion(monthCount);
		int x = list.size();
		for( int i = 0; i < x; i ++ ) {
			String temp = (String)list.get(i);
			String time = temp + " 00:00:00";
			CheckStaffBean checkStaffBean = (CheckStaffBean)statService.getCheckStaff("date='" + time + "' and area_id=" + areaId);
			//保证修改只进行一次
			if( checkStaffBean == null ) {
				checkStaffBean = new CheckStaffBean();
				checkStaffBean.setDate(time);
				checkStaffBean.setMonthCount(monthCount);
				checkStaffBean.setDayCount(dayCount);
				checkStaffBean.setNikeName(temp);
				checkStaffBean.setAreaId(areaId);
				if( !statService.addCheckStaffBean(checkStaffBean)) {
					throw new MyRuntimeException("数据库操作失败！");
				}
			} else {
				continue;
			}
		}
	}

	/**
	 * 判断当前要修改的月份是否早于本月
	 * @param monthDate
	 * @return
	 */
	public boolean isChangeAvailForMonth(String monthDate) {
		
		Date target = DateUtil.parseDate(monthDate, "yyyy-MM");
		long targetMillis = target.getTime();
		Date now = new Date();
		String nowTemp = DateUtil.formatDate(now, "yyyy-MM");
		Date nowMonth = DateUtil.parseDate(nowTemp, "yyyy-MM");
		long nowMillis = nowMonth.getTime();
		if( targetMillis >= nowMillis) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 判断要修改的日期是否 早于当日
	 * @param dayDate
	 * @return
	 */
	public boolean isChangeAvailForDay(String dayDate) {
		
		Date target = DateUtil.parseDate(dayDate, "yyyy-MM-dd");
		long targetMillis = target.getTime();
		Date now = new Date();
		String nowTemp = DateUtil.formatDate(now, "yyyy-MM-dd");
		Date nowMonth = DateUtil.parseDate(nowTemp, "yyyy-MM-dd");
		long nowMillis = nowMonth.getTime();
		//当天的也是不可以修改的
		nowMillis += 24*60*60*1000;
		if( targetMillis >= nowMillis) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 添加日排班记录
	 * @param dayCount
	 * @param temp
	 * @param statService
	 * @return
	 */
	public boolean addCheckStaffDay(int dayCount, String temp, int areaId,StatService statService) {
		String time = temp+" 00:00:00";
		CheckStaffBean checkStaffBean = (CheckStaffBean)statService.getCheckStaff("date='" + time + "' and area_id=" + areaId);
		//保证修改只进行一次
		if( checkStaffBean == null ) {
			checkStaffBean = new CheckStaffBean();
			checkStaffBean.setDate(time);
			checkStaffBean.setMonthCount(0);
			checkStaffBean.setDayCount(dayCount);
			checkStaffBean.setNikeName(temp);
			checkStaffBean.setAreaId(areaId);
			if( !statService.addCheckStaffBean(checkStaffBean)) {
				return false;
			}
		} else if( checkStaffBean.getDayCount() == dayCount) {
			
		} else {
			if( !statService.updateCheckStaff("day_count=" + dayCount , "date='" + time + "' and area_id=" + areaId )) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 去掉 重复的 商品物流分类信息
	 * @param productWareTypeList
	 * @return
	 */
	public List removeDuplicateInList(List productWareTypeList) {
		List result = new ArrayList();
		Map map = new HashMap();
		int x = productWareTypeList.size();
		for(int i = 0; i < x; i++ ) {
			UserOrderPackageTypeBean uoptb = (UserOrderPackageTypeBean) productWareTypeList.get(i);
			if( map.containsKey(new Integer(uoptb.getTypeId()))){
				
			} else {
				result.add(uoptb);
				map.put(new Integer(uoptb.getTypeId()), "");
			}
		}
		
		return result;
	}

	/**
	 * 检验 所传的 productCode  和 productBarCode 是否 属于同一个商品 返回商品类
	 * @param productCode
	 * @param productBarCode
	 * @param bService
	 * @param statService
	 * @param adminService
	 * @return
	 * @throws Exception
	 */
	public voProduct isCodeAndBarCodeMatched(String productCode,
			String productBarCode, IBarcodeCreateManagerService bService, StatService statService,
			WareService wareService) throws Exception {
		voProduct product = null;
		ProductBarcodeVO bBean = null;
		if( !productCode.equals("") ) {
			product = wareService.getProduct(productCode);
			if( product == null ) {
				throw new MyRuntimeException("商品编号有误！");
			}
		}
		if( !productBarCode.equals("") ) {
			bBean= bService.getProductBarcode("barcode="+"'"+StringUtil.toSql(productBarCode)+"'");
			if( bBean == null ) {
				throw new MyRuntimeException ("商品条码有误！");
			}
		}
		if( product.getId() != bBean.getProductId() ) {
			throw new MyRuntimeException("商品编号和条码不匹配");
		}
		
		return product;
	}

	public CartonningStandardCountBean manageCartonningStandardCount(voUser user, voProduct product,
			int standardCount, CartonningInfoService cartonningInfoService) throws Exception {
		CartonningStandardCountBean cscount = cartonningInfoService.getCartonningStandardCount("product_id="+product.getId());
		if( cscount != null ) {
			if( cscount.getStandard()!= standardCount ) {
				if( !cartonningInfoService.updateCartonningStandardCount("standard=" + standardCount +",oper_id = " + user.getId() + ",oper_name='"+user.getUsername() +"'", "id = " + cscount.getId())) {
					throw new MyRuntimeException("修改标准装箱数量时，数据库操作失败！");
				}
			}
		} else {
			cscount = new CartonningStandardCountBean();
			cscount.setLastOperDatetime(DateUtil.getNow());
			cscount.setOperId(user.getId());
			cscount.setOperName(user.getUsername());
			cscount.setProductId(product.getId());
			cscount.setStandard(standardCount);
			if( !cartonningInfoService.addCartonningStandardCount(cscount)) {
				throw new MyRuntimeException("添加标准装箱数量时，数据库操作失败！");
			}
		}
		return cscount;
	}

	public boolean saveProductWareProperty(voProduct product, voUser user,
			CheckEffectBean cfBean, ProductWareTypeBean pwtBean, int length, int width, int height, int weight, String identityInfo, StatService statService, int binning) throws Exception {
		
		ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
		ProductWarePropertyBean pwpBean = new ProductWarePropertyBean();
		pwpBean.setCheckEffectId(cfBean.getId());
		pwpBean.setProductId(product.getId());
		pwpBean.setProductTypeId(pwtBean.getId());
		pwpBean.setHeight(height);
		pwpBean.setLength(length);
		pwpBean.setWidth(width);
		pwpBean.setWeight(weight);
		pwpBean.setIdentityInfo(identityInfo);
		pwpBean.setCartonningStandardCount(binning);
		if( !statService.addProductWareProperty(pwpBean)) {
			throw new MyRuntimeException("存入商品物流属性时，数据库操作失败！");
		}
		int productWarePropertyId = statService.getDbOp().getLastInsertId();
		pwplBean.setProductWarePropertyId(productWarePropertyId);
		pwplBean.setOperDetail("添加了商品" + product.getCode() + "的物流属性");
		pwplBean.setOperId(user.getId());
		pwplBean.setOperName(user.getUsername());
		pwplBean.setTime(DateUtil.getNow());
		if( !addProductWarePropertyLog(pwplBean)) {
			throw new MyRuntimeException("添加商品物流属性日志失败！");
		}
		return true;
	}
	
//	/**
//	 * 添加商品物流属性，多了一个标准装箱量
//	 * @author zhuguofu
//	 * @param product
//	 * @param user
//	 * @param cfBean
//	 * @param pwtBean
//	 * @param length
//	 * @param width
//	 * @param height
//	 * @param weight
//	 * @param statService
//	 * @param binning
//	 * @return
//	 * @throws Exception
//	 */
//	public boolean saveProductWareProperty(voProduct product, voUser user,
//			CheckEffectBean cfBean, ProductWareTypeBean pwtBean, int length, int width, int height, int weight, StatService statService, int binning) throws Exception {
//		
//		ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
//		ProductWarePropertyBean pwpBean = new ProductWarePropertyBean();
//		pwpBean.setCheckEffectId(cfBean.getId());
//		pwpBean.setProductId(product.getId());
//		pwpBean.setProductTypeId(pwtBean.getId());
//		pwpBean.setHeight(height);
//		pwpBean.setLength(length);
//		pwpBean.setWidth(width);
//		pwpBean.setWeight(weight);
//		pwpBean.setCartonningStandardCount(binning);
//		if( !statService.addProductWareProperty(pwpBean)) {
//			throw new Exception("存入商品物流属性时，数据库操作失败！");
//		}
//		int productWarePropertyId = statService.getDbOp().getLastInsertId();
//		pwplBean.setProductWarePropertyId(productWarePropertyId);
//		pwplBean.setOperDetail("添加了商品" + product.getCode() + "的物流属性");
//		pwplBean.setOperId(user.getId());
//		pwplBean.setOperName(user.getUsername());
//		pwplBean.setTime(DateUtil.getNow());
//		if( !addProductWarePropertyLog(pwplBean)) {
//			throw new Exception("添加商品物流属性日志失败！");
//		}
//		return true;
//	}

	public void updateProductWareProperty(ProductWarePropertyBean pwpBean,voUser user,
			voProduct product, CheckEffectBean cfBean,
			ProductWareTypeBean pwtBean, int cartonningStandardCount, int length, int width,
			int height, int weight, String identityInfo, StatService statService, int binning) throws Exception {
		if( pwpBean.getCheckEffectId() != cfBean.getId() ) {
			ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
			pwplBean.setProductWarePropertyId(pwpBean.getId());
			pwplBean.setOperDetail("质检分类:" + pwpBean.getCheckeEffect().getName() + "->" + cfBean.getName());
			pwplBean.setOperId(user.getId());
			pwplBean.setOperName(user.getUsername());
			pwplBean.setTime(DateUtil.getNow());
			if( !addProductWarePropertyLog(pwplBean)) {
				throw new MyRuntimeException("添加商品物流属性日志失败！");
			}
		}
		if(pwpBean.getProductTypeId() != pwtBean.getId() ) {
			ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
			pwplBean.setProductWarePropertyId(pwpBean.getId());
			pwplBean.setOperDetail("商品物流分类：" + pwpBean.getProductWareType().getName() +"->" + pwtBean.getName());
			pwplBean.setOperId(user.getId());
			pwplBean.setOperName(user.getUsername());
			pwplBean.setTime(DateUtil.getNow());
			if( !addProductWarePropertyLog(pwplBean)) {
				throw new MyRuntimeException("添加商品物流属性日志失败！");
			}
		}
		if( pwpBean.getCartonningStandardCount() != cartonningStandardCount ) {
			ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
			pwplBean.setProductWarePropertyId(pwpBean.getId());
			pwplBean.setOperDetail("标准装箱量：" + pwpBean.getCartonningStandardCount() +"->" + cartonningStandardCount);
			pwplBean.setOperId(user.getId());
			pwplBean.setOperName(user.getUsername());
			pwplBean.setTime(DateUtil.getNow());
			if( !addProductWarePropertyLog(pwplBean)) {
				throw new MyRuntimeException("添加商品物流属性日志失败！");
			}
		}
		if( pwpBean.getLength() != length || pwpBean.getWidth() != width || pwpBean.getHeight() != height) {
			ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
			pwplBean.setProductWarePropertyId(pwpBean.getId());
			pwplBean.setOperDetail("最小包装尺寸：" + pwpBean.getLength()+"*"+pwpBean.getWidth()+"*"+pwpBean.getHeight()+"->"+length+"*"+width+"*"+height);
			pwplBean.setOperId(user.getId());
			pwplBean.setOperName(user.getUsername());
			pwplBean.setTime(DateUtil.getNow());
			if( !addProductWarePropertyLog(pwplBean)) {
				throw new MyRuntimeException("添加商品物流属性日志失败！");
			}
		}
		if( pwpBean.getWeight() != weight ) {
			ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
			pwplBean.setProductWarePropertyId(pwpBean.getId());
			pwplBean.setOperDetail("重量：" + pwpBean.getWeight() + "g "+"->"+ weight + "g ");
			pwplBean.setOperId(user.getId());
			pwplBean.setOperName(user.getUsername());
			pwplBean.setTime(DateUtil.getNow());
			if( !addProductWarePropertyLog(pwplBean)) {
				throw new MyRuntimeException("添加商品物流属性日志失败！");
			}
		}
		if( !pwpBean.getIdentityInfo().equals(identityInfo) ) {
			ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
			pwplBean.setProductWarePropertyId(pwpBean.getId());
			pwplBean.setOperDetail("可辨识信息：" + pwpBean.getIdentityInfo() + "->" + identityInfo );
			pwplBean.setOperId(user.getId());
			pwplBean.setOperName(user.getUsername());
			pwplBean.setTime(DateUtil.getNow());
			if( !addProductWarePropertyLog(pwplBean)) {
				throw new MyRuntimeException("添加商品物流属性日志失败！");
			}
		}
		if( pwpBean.getCartonningStandardCount() != binning ) {
			ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
			pwplBean.setProductWarePropertyId(pwpBean.getId());
			pwplBean.setOperDetail("标准装箱量：" + pwpBean.getCartonningStandardCount() +"->"+ binning );
			pwplBean.setOperId(user.getId());
			pwplBean.setOperName(user.getUsername());
			pwplBean.setTime(DateUtil.getNow());
			if( !addProductWarePropertyLog(pwplBean)) {
				throw new MyRuntimeException("添加商品物流属性日志失败！");
			}
		}
		
		if(!statService.updateProductWareProperty("product_id="+product.getId() + ",check_effect_id="+ cfBean.getId()+",product_type_id="+ pwtBean.getId() + ",length=" + length + ",height="+ height+", width="+width + ", weight="+weight + ", identity_info = '" + identityInfo + "',cartonning_standard_count="+binning, "id = " + pwpBean.getId())) {
			 throw new MyRuntimeException("在修改商品物流属性时，数据库操作失败！");
		}
		
	}
	
//	/**
//	 * 编辑商品物流属性
//	 * @author zhuguofu
//	 * @param pwpBean
//	 * @param user
//	 * @param product
//	 * @param cfBean
//	 * @param pwtBean
//	 * @param length
//	 * @param width
//	 * @param height
//	 * @param weight
//	 * @param statService
//	 * @param binning
//	 * @throws Exception
//	 */
//	public void updateProductWareProperty(ProductWarePropertyBean pwpBean,voUser user,
//			voProduct product, CheckEffectBean cfBean,
//			ProductWareTypeBean pwtBean, int length, int width,
//			int height, int weight, StatService statService,int binning) throws Exception {
//		if( pwpBean.getCheckEffectId() != cfBean.getId() ) {
//			ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
//			pwplBean.setProductWarePropertyId(pwpBean.getId());
//			pwplBean.setOperDetail("质检分类:" + pwpBean.getCheckeEffect().getName() + "->" + cfBean.getName());
//			pwplBean.setOperId(user.getId());
//			pwplBean.setOperName(user.getUsername());
//			pwplBean.setTime(DateUtil.getNow());
//			if( !addProductWarePropertyLog(pwplBean)) {
//				throw new Exception("添加商品物流属性日志失败！");
//			}
//		}
//		if(pwpBean.getProductTypeId() != pwtBean.getId() ) {
//			ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
//			pwplBean.setProductWarePropertyId(pwpBean.getId());
//			pwplBean.setOperDetail("商品物流分类：" + pwpBean.getProductWareType().getName() +"->" + pwtBean.getName());
//			pwplBean.setOperId(user.getId());
//			pwplBean.setOperName(user.getUsername());
//			pwplBean.setTime(DateUtil.getNow());
//			if( !addProductWarePropertyLog(pwplBean)) {
//				throw new Exception("添加商品物流属性日志失败！");
//			}
//		}
//		if( pwpBean.getLength() != length || pwpBean.getWidth() != width || pwpBean.getHeight() != height) {
//			ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
//			pwplBean.setProductWarePropertyId(pwpBean.getId());
//			pwplBean.setOperDetail("最小包装尺寸：" + pwpBean.getLength()+"*"+pwpBean.getWidth()+"*"+pwpBean.getHeight()+"->"+length+"*"+width+"*"+height);
//			pwplBean.setOperId(user.getId());
//			pwplBean.setOperName(user.getUsername());
//			pwplBean.setTime(DateUtil.getNow());
//			if( !addProductWarePropertyLog(pwplBean)) {
//				throw new Exception("添加商品物流属性日志失败！");
//			}
//		}
//		if( pwpBean.getWeight() != weight ) {
//			ProductWarePropertyLogBean pwplBean = new ProductWarePropertyLogBean();
//			pwplBean.setProductWarePropertyId(pwpBean.getId());
//			pwplBean.setOperDetail("重量：" + pwpBean.getWeight() + "g "+"->"+ weight + "g ");
//			pwplBean.setOperId(user.getId());
//			pwplBean.setOperName(user.getUsername());
//			pwplBean.setTime(DateUtil.getNow());
//			if( !addProductWarePropertyLog(pwplBean)) {
//				throw new Exception("添加商品物流属性日志失败！");
//			}
//		}
//		
//		if(!statService.updateProductWareProperty("product_id="+product.getId() + ",check_effect_id="+ cfBean.getId()+",product_type_id="+ pwtBean.getId() + ",length=" + length + ",height="+ height+", width="+width + ", weight="+weight+",cartonning_standard_count="+binning, "id = " + pwpBean.getId())) {
//			 throw new Exception("在修改商品物流属性时，数据库操作失败！");
//		}
//		
//	}
	
	public static int toInt(String id){
    	if(id==null) return -1;
    	Matcher m = pattern.matcher(id);
		if(m.matches()){
			return Integer.parseInt(id);
		}else{
			return -1;
		}
    }
	
	/**
	 * 根据request获得一个map map里放了当前用户有权限的地区id和名称
	 * @param request
	 * @return
	 */
	public static Map<String, String> getWeraAreaMap(HttpServletRequest request) {
		Map<String,String> wareAreaMap = new HashMap<String, String>();
		List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
		if( cdaList.size() == 0 ) { 
			wareAreaMap.put("-1", "无地区权限");
  		}else{ 
  			for ( int i = 0; i < cdaList.size(); i ++ ) {
  				Integer areaId = new Integer(Integer.parseInt(cdaList.get(i)));
  				wareAreaMap.put(areaId + "", ProductStockBean.areaMap.get(areaId) + "");
			}
		}
		return wareAreaMap;
	}
	
	/**
	 * 根据request 得到当前用户的可以选择的权限地区select 标签。 有无地区权限提示， 无请选择选项
	 * @param request
	 * @return
	 */
	public static String getWeraAreaOptions(HttpServletRequest request) {
		List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
		String selectLable = "<select name='wareArea' id='wareArea' >";
		if( cdaList.size() == 0 ) { 
			selectLable +="<option value='-1'>无地区权限</option>";
  		}else{ 
  		
  			for ( int i = 0; i < cdaList.size(); i ++ ) {
  				Integer areaId = new Integer(Integer.parseInt(cdaList.get(i)));
  				selectLable += "<option value='"+areaId+"'>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
			}
		}
		selectLable += "</select>";
		return selectLable;
	}

	/**
	 * 根据request 得到当前用户的可以选择的权限地区select 标签。 无请选择选项， 会选中传入的int 数值对应地区id 对应的地区
	 * @param request
	 * @return
	 */
	public static String getWeraAreaOptions(HttpServletRequest request, int needToSelect) {
		List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
		String selectLable = "<select name='wareArea' id='wareArea' >";
		if( cdaList.size() == 0 ) { 
			selectLable +="<option value='-1'>无地区权限</option>";
		}else{ 
			
			for ( int i = 0; i < cdaList.size(); i ++ ) {
				Integer areaId = new Integer(Integer.parseInt(cdaList.get(i)));
				if( needToSelect == areaId) {
					selectLable += "<option value='"+areaId+"' selected>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
				} else {
					selectLable += "<option value='"+areaId+"'>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
				}
			}
		}
		selectLable += "</select>";
		return selectLable;
	}
	
	/**
	 * 没有地区权限遮罩，获得现有所有地区的Select
	 * @param request
	 * @return
	 */
	public static String getWeraAreaOptionsAll(int needToSelect) {
		Iterator itr = ProductStockBean.areaMap.keySet().iterator();
		String selectLable = "<select name='wareArea' id='wareArea' >";
		
			
		selectLable +="<option value='-1'>请选择地区</option>";
		for ( ; itr.hasNext(); ) {
			Integer areaId = (Integer)itr.next();
			if( needToSelect == areaId) {
				selectLable += "<option value='"+areaId+"' selected>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
			} else {
				selectLable += "<option value='"+areaId+"'>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
			}
		}
		
		selectLable += "</select>";
		return selectLable;
	}
	
	
	
	/**
	 * 有地区权限遮罩，显示请选择地区， 根据int 选中对应地区。
	 * @param request
	 * @param needToSelect
	 * @return
	 */
	public static String getWeraAreaOptionsAllWithRight(HttpServletRequest request, int needToSelect) {
		List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
		String selectLable = "<select name='wareArea' id='wareArea' >";
		 
			selectLable +="<option value='-1'>请选择地区</option>";
			for ( int i = 0; i < cdaList.size(); i ++ ) {
				Integer areaId = new Integer(Integer.parseInt(cdaList.get(i)));
				if( needToSelect == areaId) {
					selectLable += "<option value='"+areaId+"' selected>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
				} else {
					selectLable += "<option value='"+areaId+"'>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
				}
			}
		
		selectLable += "</select>";
		return selectLable;
	}
	
	/**最终版， 可以根据传来的name，id来生成，并且 多加了 无地区权限的 特殊值
	 * 根据request 得到当前用户的可以选择的权限地区select 标签。 无请选择选项， 会选中传入的int 数值对应地区id 对应的地区
	 * @param request
	 * @return
	 */
	public static String getWeraAreaOptionsCustomized(String name, String id ,HttpServletRequest request, int needToSelect, boolean withDefaultValue,String defaultValue) {
		List<String> cdaList = CargoDeptAreaService.getCargoDeptAreaList(request);
		String selectLable = "<select name='"+name+"' id='"+id+"' >";
		if( cdaList.size() == 0 ) { 
			selectLable +="<option value='1000'>无地区权限</option>";
		}else{ 
			if( withDefaultValue ) {
				selectLable += "<option value='"+defaultValue+"'>请选择地区</option>";
			}
			for ( int i = 0; i < cdaList.size(); i ++ ) {
				Integer areaId = new Integer(Integer.parseInt(cdaList.get(i)));
				if( needToSelect == areaId) {
					selectLable += "<option value='"+areaId+"' selected>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
				} else {
					selectLable += "<option value='"+areaId+"'>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
				}
			}
		}
		selectLable += "</select>";
		return selectLable;
	}
	/**最终版，无权限遮罩，  可以根据传来的name，id来生成，并且 多加了 无地区权限的 特殊值
	 * 根据request 得到当前用户的可以选择的权限地区select 标签。 无请选择选项， 会选中传入的int 数值对应地区id 对应的地区
	 * @param request
	 * @return
	 */
	public static String getWeraAreaAllCustomized(String name, String id ,int needToSelect, boolean withDefaultValue, String defaultValue) {
		
		Iterator itr = ProductStockBean.areaMap.keySet().iterator();
		String selectLable = "<select name='"+name+"' id='"+id+"' >";
		
			
		if( withDefaultValue ) {
			selectLable += "<option value='"+defaultValue+"'>请选择地区</option>";
		}
		for ( ; itr.hasNext(); ) {
			Integer areaId = (Integer)itr.next();
			if( needToSelect == areaId) {
				selectLable += "<option value='"+areaId+"' selected>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
			} else {
				selectLable += "<option value='"+areaId+"'>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
			}
		}
		
		selectLable += "</select>";
		return selectLable;
	}
	/** 只有发货仓库地区最终版，无权限遮罩，  可以根据传来的name，id来生成，并且 多加了 无地区权限的 特殊值
	 * 根据request 得到当前用户的可以选择的权限地区select 标签。 无请选择选项， 会选中传入的int 数值对应地区id 对应的地区
	 * @param request
	 * @return
	 */
	public static String getStockoutWeraAreaCustomized(String name, String id ,int needToSelect, boolean withDefaultValue, String defaultValue) {
		
		Iterator itr = ProductStockBean.stockoutAvailableAreaMap.keySet().iterator();
		String selectLable = "<select name='"+name+"' id='"+id+"' >";
		
		
		if( withDefaultValue ) {
			selectLable += "<option value='"+defaultValue+"'>请选择地区</option>";
		}
		for ( ; itr.hasNext(); ) {
			Integer areaId = (Integer)itr.next();
			if( needToSelect == areaId) {
				selectLable += "<option value='"+areaId+"' selected>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
			} else {
				selectLable += "<option value='"+areaId+"'>"+ ProductStockBean.areaMap.get(areaId)+"</option>";
			}
		}
		
		selectLable += "</select>";
		return selectLable;
	}
}

package mmb.util.combobox;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.bi.model.BiOrderInStockDuration;
import mmb.common.dao.CommonDao;
import mmb.delivery.domain.PopBussiness;
import mmb.hessian.ware.DeliverOrderInfoBean;
import mmb.rec.oper.bean.ProductSellPropertyBean;
import mmb.rec.sys.easyui.EasyuiComBoBoxBean;
import mmb.stock.aftersale.AfStockService;
import mmb.stock.aftersale.AfterSaleBackSupplier;
import mmb.stock.aftersale.AfterSaleBackSupplierProduct;
import mmb.stock.aftersale.AfterSaleCycleStatBean;
import mmb.stock.aftersale.AfterSaleDetectPackageBean;
import mmb.stock.aftersale.AfterSaleDetectProductBean;
import mmb.stock.aftersale.AfterSaleDetectTypeBean;
import mmb.stock.aftersale.AfterSaleDetectTypeDetailBean;
import mmb.stock.aftersale.AfterSaleStockin;
import mmb.stock.aftersale.SysDict;
import mmb.stock.fitting.model.AfterSaleReceiveFitting;
import mmb.stock.fitting.model.FittingBuyStockInBean;
import mmb.stock.spare.model.SpareStockCard;
import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.stock.stat.ProductLineBean;
import mmb.stock.stat.ProductLineService;
import mmb.tms.model.ProvinceCity;
import mmb.tms.model.Provinces;
import mmb.tms.service.IDeliverService;
import mmb.ware.WareService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.OrderTypeBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyReason;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAreaBean;
import adultadmin.bean.stock.StockExchangeBean;
import adultadmin.bean.stock.StockTypeBean;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.CatalogCache;
import cache.ProductLinePermissionCache;

/**
 * @return 专门加载combobox的controller
 * @author syuf
 */
@Controller
@RequestMapping("/Combobox")
public class ComboboxController {
	@Autowired
	public CommonDao commonMapper;
	@Autowired
	private IDeliverService service;

	/**
	 * @return 加载不合格原因
	 * @author syuf
	 */
	@RequestMapping("/getUnqualifiedReason")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getUnqualifiedReason(HttpServletRequest request, HttpServletResponse response,
			String afterSaleDetectProductId)throws Exception{
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		WareService wareService = new WareService(dbOp);
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			if("".equals(StringUtil.checkNull(afterSaleDetectProductId))){
				return comboBoxList;
			}
			AfterSaleDetectProductBean detect = afService.getAfterSaleDetectProduct("id=" + afterSaleDetectProductId);
			if(detect == null){
				return comboBoxList;
			}
			voProduct product = wareService.getProduct(detect.getProductId());
			if(product == null){
				return comboBoxList;
			}
			AfterSaleDetectTypeDetailBean.initContentMap(17, product.getParentId1());
			Map<String,String> map = AfterSaleDetectTypeDetailBean.contentMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			for(Map.Entry<String, String> entry : map.entrySet()){
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey()+"");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			dbOp.release();
		}
		return comboBoxList;
	}
	/**
	 * @return 加载商品一级分类
	 * @author syuf
	 */
	@RequestMapping("/getParentId1")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getParentId1(HttpServletRequest request, HttpServletResponse response)throws Exception{
		HashMap map = (HashMap)CatalogCache.catalogLevelList.get(0);
		List list = (List)map.get(Integer.valueOf(0));
		Iterator iter = list.listIterator();
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		while(iter.hasNext()){
			voCatalog catalog = (voCatalog)iter.next();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("" + catalog.getId());
			bean.setText(catalog.getName());
			easyuilist.add(bean);
		}
		return easyuilist;
	}
	/**
	 * @return 加载商品二级分类(联动)
	 * @author syuf
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getParentId2")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getParentId2(HttpServletRequest request, HttpServletResponse response)throws Exception{
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		
		boolean permission = StringUtil.toBoolean(request.getParameter("permission"));
		
		int parentId1 = StringUtil.toInt(request.getParameter("parentId1"));
		if (parentId1 == -1 ) {
			return easyuilist;
		}
		//产品线权限
		String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(user);
		String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(user);
		String catalogIdsTemp = "";
		if(!catalogIds2.equals("")){
			String[] splits = catalogIds2.split(",");
			for(int i=0;i<splits.length;i++){
				voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
				if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
					catalogIdsTemp = catalogIdsTemp + catalog.getId() + ",";
				}
			}
			catalogIds1 = catalogIdsTemp + catalogIds1;
			if(catalogIds1.endsWith(",")){
				catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
			}
		}
		
		EasyuiComBoBoxBean bean = null;
		HashMap secondMap = CatalogCache.getSecondMap();
		int level = 0;
			
		if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
			level = 2;
		}else{
			level = 1;
		}
		if(permission && !StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(parentId1))){
			return easyuilist;
		}
		
		//二级
		List secondList = (List)secondMap.get(Integer.valueOf(parentId1));
		if(secondList !=null && secondList.size() > 0){
			for(int j=0;j<secondList.size();j++){
				voCatalog catalog = (voCatalog)secondList.get(j);
				if(permission && level == 2 && !StringUtil.hasStrArray(catalogIds2.split(","),String.valueOf(catalog.getId()))){
					continue;
				}
				bean = new EasyuiComBoBoxBean();
				bean.setId("" + catalog.getId());
				bean.setText(catalog.getName());
				easyuilist.add(bean);
			}
		}
		return easyuilist;
	}
	/**
	 * @return 加载商品二级分类(多选联动)
	 * @author syuf
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getParentId2s")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getParentId2s(HttpServletRequest request, HttpServletResponse response,String parentId1s)throws Exception{
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		boolean permission = StringUtil.toBoolean(request.getParameter("permission"));
		if("".equals(StringUtil.checkNull(parentId1s))) {
			return easyuilist;
		}
		for(String parentId1 : parentId1s.split(",")){
			//产品线权限
			String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(user);
			String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(user);
			String catalogIdsTemp = "";
			if(!catalogIds2.equals("")){
				String[] splits = catalogIds2.split(",");
				for(int i=0;i<splits.length;i++){
					voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
					if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
						catalogIdsTemp = catalogIdsTemp + catalog.getId() + ",";
					}
				}
				catalogIds1 = catalogIdsTemp + catalogIds1;
				if(catalogIds1.endsWith(",")){
					catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
				}
			}
			
			EasyuiComBoBoxBean bean = null;
			HashMap secondMap = CatalogCache.getSecondMap();
			int level = 0;
			
			if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
				level = 2;
			}else{
				level = 1;
			}
			if(permission && !StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(parentId1))){
				return easyuilist;
			}
			
			//二级
			List secondList = (List)secondMap.get(Integer.valueOf(parentId1));
			if(secondList !=null && secondList.size() > 0){
				for(int j=0;j<secondList.size();j++){
					voCatalog catalog = (voCatalog)secondList.get(j);
					if(permission && level == 2 && !StringUtil.hasStrArray(catalogIds2.split(","),String.valueOf(catalog.getId()))){
						continue;
					}
					bean = new EasyuiComBoBoxBean();
					bean.setId("" + catalog.getId());
					bean.setText(catalog.getName());
					easyuilist.add(bean);
				}
			}
		}
		return easyuilist;
	}
	/**
	 * @return 加载商品三级分类(多选联动)
	 * @author syuf
	 */
	@RequestMapping("/getParentId3s")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getParentId3s(HttpServletRequest request, HttpServletResponse response,String parentId2s)throws Exception{
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		if("".equals(StringUtil.checkNull(parentId2s))) {
			return easyuilist;
		}
		for(String parentId2 : parentId2s.split(",")){
			HashMap thirdMap = CatalogCache.getThirdMap();
			//三级
			List thirdList = (List)thirdMap.get(Integer.valueOf(parentId2));
			if(thirdList != null && thirdList.size() > 0){
				for(int k=0;k<thirdList.size();k++){
					voCatalog catalog = (voCatalog)thirdList.get(k);
					EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
					bean.setId("" + catalog.getId());
					bean.setText(catalog.getName());
					easyuilist.add(bean);
				}
			}
		}
		return easyuilist;
	}
	/**
	 * @return 加载商品三级分类(联动)
	 * @author syuf
	 */
	@RequestMapping("/getParentId3")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getParentId3(HttpServletRequest request, HttpServletResponse response)throws Exception{
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		int parentId2 = StringUtil.toInt(request.getParameter("parentId2"));
		if (parentId2 == -1 ) {
			return easyuilist;
		}
		HashMap thirdMap = CatalogCache.getThirdMap();
		//三级
		List thirdList = (List)thirdMap.get(Integer.valueOf(parentId2));
		if(thirdList != null && thirdList.size() > 0){
			for(int k=0;k<thirdList.size();k++){
				voCatalog catalog = (voCatalog)thirdList.get(k);
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId("" + catalog.getId());
				bean.setText(catalog.getName());
				easyuilist.add(bean);
			}
		}
		return easyuilist;
	}
	/**
	 * @return 加载寄回方式comboBox
	 * @author syuf
	 */
	@RequestMapping("/getReturnType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getReturnType(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer,String> map = AfterSaleDetectPackageBean.typeMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey()+"");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	/**
	 * @return 加载商品类型(售后库区分)comboBox
	 * @author syuf
	 */
	@RequestMapping("/getProductType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getProductType(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			@SuppressWarnings("unchecked")
			Map<Integer,String> map = CargoInfoBean.stockTypeMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				if(entry.getKey() == CargoInfoBean.STOCKTYPE_AFTER_SALE || entry.getKey() == CargoInfoBean.STOCKTYPE_CUSTOMER){
					EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
					bean.setId(entry.getKey()+"");
					bean.setText(entry.getValue());
					comboBoxList.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	/**
	 * @return 获取配件领用单状态
	 * @author syuf
	 */
	@RequestMapping("/getReceiveStatus")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getReceiveStatus(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Byte,String> map = AfterSaleReceiveFitting.statusMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			for(Map.Entry<Byte, String> entry : map.entrySet()){
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey()+"");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	/**
	 * @return 获取售后配件用途
	 * @author syuf
	 */
	@RequestMapping("/getFittingTarget")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getFittingTarget(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Byte,String> map = AfterSaleReceiveFitting.targetMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			//默认值
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择用途");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Byte, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey()+"");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	/**
	 * @return 加载售后库地区
	 * @author syuf
	 */
	@RequestMapping("/getAfterSaleArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAfterSaleArea(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer,List<StockAreaBean>> map = ProductStockBean.getTypeToAreaMap();
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			List<StockAreaBean> areaList = map.get(ProductStockBean.STOCKTYPE_AFTER_SALE);
			//默认值
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择库区");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(StockAreaBean area : areaList){
				bean = new EasyuiComBoBoxBean();
				bean.setId(area.getId()+"");
				bean.setText(area.getName());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 加载全部库地区
	 * @author hp
	 */
	@RequestMapping("/getAllArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAllArea(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer,String> map = ProductStockBean.areaMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			//默认值
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择库区");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
					EasyuiComBoBoxBean bean1 = new EasyuiComBoBoxBean();
					bean1.setId(entry.getKey()+"");
					bean1.setText(entry.getValue());
					comboBoxList.add(bean1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		return comboBoxList;
	}
	/**
	 * @return 加载签收地区combobox
	 * @author syuf
	 */
	@RequestMapping("/getSignArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getSignArea(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer,List<StockAreaBean>> map = ProductStockBean.getTypeToAreaMap();
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			List<StockAreaBean> areaList = map.get(ProductStockBean.STOCKTYPE_AFTER_SALE);
			EasyuiComBoBoxBean bean = null;
			for(StockAreaBean area : areaList){
				bean = new EasyuiComBoBoxBean();
				bean.setId(area.getId()+"");
				bean.setText(area.getName());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 加载报溢单库地区(仅有深圳、无锡)combobox
	 * @author ahc
	 */
	@RequestMapping("/getByArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getByArea(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer,String> map = ProductStockBean.getAreaMap();
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			//默认值
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择库区");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				if( entry.getKey() == ProductStockBean.AREA_SZ 
						|| entry.getKey() == ProductStockBean.AREA_WX
						|| entry.getKey() == ProductStockBean.AREA_ZC
						|| entry.getKey() == ProductStockBean.AREA_CD
						){
					EasyuiComBoBoxBean bean1 = new EasyuiComBoBoxBean();
					bean1.setId(entry.getKey()+"");
					bean1.setText(entry.getValue());
					comboBoxList.add(bean1);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 加载报损报溢原因 combobox
	 * @author ahc
	 */
	@RequestMapping("/getReason")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getReason(HttpServletRequest request,HttpServletResponse response,String type) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		IBsByServiceManagerService iBsByservice = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		
		try {
			//默认值
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择");
			bean.setSelected(true);
			easyuilist.add(bean);
			if(type!=null){
				List<BsbyReason> list =iBsByservice.getBsbyReasonList(Integer.parseInt(type));
				for(BsbyReason bsbyReason:list){
					EasyuiComBoBoxBean bean2 = new EasyuiComBoBoxBean();
					bean2.setId(bsbyReason.getId()+"");
					bean2.setText(bsbyReason.getReason());
					easyuilist.add(bean2);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			dbOp.release();
		}
		return easyuilist;
	}
	/**
	 * @return 加载快递公司comboBox
	 * @author syuf
	 */
	@RequestMapping("/getDeliver")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDeliver(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer,String> map = SysDict.deliverMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey()+"");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 加载快递公司comboBox
	 * @author ahc
	 */
	@RequestMapping("/getDeliverAll")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDeliverAll(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		WareService wareService = new WareService(); 
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		try {
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "id,name");
			paramMap.put("table", " deliver_corp_info");
			paramMap.put("condition", " 1=1");
			List listRows = commonMapper.getCommonInfo(paramMap);
			if(listRows!=null && listRows.size()!=0){
				EasyuiComBoBoxBean bean2 = new EasyuiComBoBoxBean();
				bean2.setId("-1");
				bean2.setText("请选择");
				bean2.setSelected(true);
				easyuilist.add(bean2);
				for(int i = 0;i<listRows.size();i++){
					HashMap map = (HashMap)listRows.get(i);
						EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
						bean.setId(map.get("id")+"");
						bean.setText(map.get("name")+"");
						easyuilist.add(bean);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return easyuilist;
	}
	/**
	 * @return 加载返厂商品列表状态comboBox
	 * @author syuf
	 */
	@RequestMapping("/getBackSupplierStatus")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getBackSupplierStatus(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer,String> map = AfterSaleBackSupplierProduct.statusMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey()+"");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	/**
	 * @return 加载部分返厂商品列表状态comboBox
	 * @author zy
	 */
	@RequestMapping("/getPartBackSupplierStatus")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getPartBackSupplierStatus(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer,String> map = AfterSaleBackSupplierProduct.statusMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId(AfterSaleBackSupplierProduct.STATUS4 + "");
			bean.setText(map.get(Integer.valueOf(bean.getId())));
			comboBoxList.add(bean);
			bean = new EasyuiComBoBoxBean();
			bean.setId(AfterSaleBackSupplierProduct.STATUS0 + "");
			bean.setText(map.get(Integer.valueOf(bean.getId())));
			comboBoxList.add(bean);
			bean = new EasyuiComBoBoxBean();
			bean.setId(AfterSaleBackSupplierProduct.STATUS1 + "");
			bean.setText(map.get(Integer.valueOf(bean.getId())));
			comboBoxList.add(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	/**
	 * @return 根据商品一级分类和检测类别加载检测选项comboBox
	 * @author zy
	 */
	@RequestMapping("/getAfterDetectDetail")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAfterDetectDetail(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		int parentId1 = StringUtil.toInt(request.getParameter("parentId1"));
		int afterSaleDetectTypeId = StringUtil.toInt(request.getParameter("afterSaleDetectTypeId"));
		try {
			AfterSaleDetectTypeDetailBean asBean = afStockService.getAfterSaleDetectTypeDetail(" after_sale_detect_type_id=" + afterSaleDetectTypeId + " and parent_id1=" + parentId1);
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("");
			bean.setText("");
			bean.setSelected(true);
			comboBoxList.add(bean);
			if (asBean != null) {
				String[] content = asBean.getContent().split("\n");
				for(String con : content){
					bean = new EasyuiComBoBoxBean();
					bean.setId(con);
					bean.setText(con);
					comboBoxList.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 根据商品一级分类和检测类别加载问题一级分类comboBox
	 * @author ahc
	 */
	@RequestMapping("/getAfterDetectDetailForOneGrade")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAfterDetectDetailForOneGrade(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		int parentId1 = StringUtil.toInt(request.getParameter("parentId1"));
		int afterSaleDetectTypeId = StringUtil.toInt(request.getParameter("afterSaleDetectTypeId"));
		try {
			List<AfterSaleDetectTypeDetailBean> list = afStockService.getAfterSaleDetectTypeDetailList(" after_sale_detect_type_id=" + afterSaleDetectTypeId + " and parent_id1="+ parentId1 +" and detect_type_parent_id1 = 0", -1, -1, null);
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("");
			bean.setText("请选择");
			bean.setSelected(true);
			comboBoxList.add(bean);
			if (list != null) {
				for(AfterSaleDetectTypeDetailBean afterSaleDetectTypeDetailBean : list){
					bean = new EasyuiComBoBoxBean();
					bean.setId(afterSaleDetectTypeDetailBean.getId()+"");
					bean.setText(afterSaleDetectTypeDetailBean.getContent());
					comboBoxList.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 根据商品一级分类和检测类别加载问题二级分类comboBox
	 * @author ahc
	 */
	@RequestMapping("/getAfterDetectDetailForTwoGrade")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAfterDetectDetailForTwoGrade(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		int id = StringUtil.toInt(request.getParameter("id"));
		int afterSaleDetectTypeId = StringUtil.toInt(request.getParameter("afterSaleDetectTypeId"));
		try {
			List<AfterSaleDetectTypeDetailBean> list = afStockService.getAfterSaleDetectTypeDetailList(" after_sale_detect_type_id=" + afterSaleDetectTypeId + " and detect_type_parent_id1=" + id + " and detect_type_parent_id2 = 0", -1, -1, null);
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("");
			bean.setText("请选择");
			bean.setSelected(true);
			comboBoxList.add(bean);
			if (list != null) {
				for(AfterSaleDetectTypeDetailBean afterSaleDetectTypeDetailBean2 : list){
					bean = new EasyuiComBoBoxBean();
					bean.setId(afterSaleDetectTypeDetailBean2.getId()+"");
					bean.setText(afterSaleDetectTypeDetailBean2.getContent());
					comboBoxList.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 根据商品一级分类和检测类别加载问题三级分类comboBox
	 * @author ahc
	 */
	@RequestMapping("/getAfterDetectDetailForThreeGrade")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAfterDetectDetailForThreeGrade(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		int id = StringUtil.toInt(request.getParameter("id"));
		int afterSaleDetectTypeId = StringUtil.toInt(request.getParameter("afterSaleDetectTypeId"));
		try {
			List<AfterSaleDetectTypeDetailBean> list = afStockService.getAfterSaleDetectTypeDetailList(" after_sale_detect_type_id=" + afterSaleDetectTypeId + " and detect_type_parent_id2=" + id, -1, -1, null);
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("");
			bean.setText("请选择");
			bean.setSelected(true);
			comboBoxList.add(bean);
			if (list != null) {
				for(AfterSaleDetectTypeDetailBean afterSaleDetectTypeDetailBean2 : list){
					bean = new EasyuiComBoBoxBean();
					bean.setId(afterSaleDetectTypeDetailBean2.getId()+"");
					bean.setText(afterSaleDetectTypeDetailBean2.getContent());
					comboBoxList.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return comboBoxList;
	}
	/**
	 * @return 加载返厂厂商comboBox
	 * @author syuf
	 */
	@RequestMapping("/getBackSupplier")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getBackSupplier(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			List<AfterSaleBackSupplier> list = afService.getAfterSaleBackSupplierList("1=1",-1,-1,null);
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("");//2015-05-23 由于默认为-1导致数据无法加载问题进行调整 从-1改为空
			bean.setText("请选择");
			bean.setSelected(true);
			comboBoxList.add(bean);
			if(list != null && list.size() > 0){
				for(AfterSaleBackSupplier supplier : list){
					bean = new EasyuiComBoBoxBean();
					bean.setId(supplier.getId()+"");
					bean.setText(supplier.getName());
					comboBoxList.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return comboBoxList;
	}
	/**
	 * @return 获取售后厂商类型下拉框 2014-2-18
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getSupplierType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getSupplierType(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer,String> map = AfterSaleBackSupplier.typeMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择厂商类型");
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey()+"");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	/**
	 * @return 加载检测选项 comboBox
	 * @author zhangxiaolei
	 */
	@RequestMapping("/afterSaleDetectTypeList")
	@ResponseBody
	public List<EasyuiComBoBoxBean> afterSaleDetectTypeList(HttpServletRequest request,HttpServletResponse response,String id,String parent_id1
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			if(!parent_id1.equals("undefined")){
				List<AfterSaleDetectTypeDetailBean> list = afService.getAfterSaleDetectTypeDetailList("after_sale_detect_type_id="+id+" and parent_id1="+parent_id1,-1,-1,null);
				if(list != null && list.size() > 0){
					Set<String> set = new HashSet<String>();
					comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
					for(AfterSaleDetectTypeDetailBean bean : list){
						if(bean.getContent() != null){
							for(String s : bean.getContent().split("\n")){
								set.add(s.trim());
							}
						}
					}
					if(set.size() > 0){
						for(String s : set){
							EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
							bean.setId(s);
							bean.setText(s);
							comboBoxList.add(bean);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return comboBoxList;
	}
	/**
	 * @return 获取售后处理单状态下拉框 2014-2-18
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getAfterSaleDetectProductStatus")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAfterSaleDetectProductStatus(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer,String> map = AfterSaleDetectProductBean.statusMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey()+"");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 获取售后处理单状态下拉框 2014-5-14(不要0)
	 * @author zy
	 */
	@RequestMapping("/getPartAfterSaleDetectProductStatus")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getPartAfterSaleDetectProductStatus(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer,String> map = AfterSaleDetectProductBean.statusMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				if (entry.getKey() != 0) {
					bean.setId(entry.getKey()+"");
					bean.setText(entry.getValue());
					comboBoxList.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	/**
	 * @return 加载申报状态comboBox
	 * @author syuf
	 */
	@RequestMapping("/getDeclareStatus")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDeclareStatus(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afService = new AfStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		try {
			@SuppressWarnings("unchecked")
			List<AfterSaleDetectTypeDetailBean> list = afService.getAfterSaleDetectTypeDetailList("after_sale_detect_type_id=" + AfterSaleDetectTypeBean.REPORT_STATUS,-1,-1,null);
			if(list != null && list.size() > 0){
				Set<String> set = new HashSet<String>();
				comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
				for(AfterSaleDetectTypeDetailBean bean : list){
					if(bean.getContent() != null){
						for(String s : bean.getContent().split("\n")){
							set.add(s.trim());
						}
					}
				}
				if(set.size() > 0){
					for(String s : set){
						EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
						bean.setId(s);
						bean.setText(s);
						comboBoxList.add(bean);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return comboBoxList;
	}
	/**
	 * @describe 获取快递公司 列表 不带全部的
	 * @author syuf
	 * @date 2013-08-09
	 */
	@RequestMapping("/getDelivers")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDelivers(HttpServletRequest request, HttpServletResponse response)throws Exception{
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		List<DeliverCorpInfoBean> list = service.getDelvierList(new HashMap<String, Integer>());
		if(list!=null && list.size()>0){
			for(DeliverCorpInfoBean info : list){
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId("" + info.getId());
				bean.setText(info.getName());
				easyuilist.add(bean);
			}
		}
		return easyuilist;
	}
	/**
	 * @describe 获取发货地区 只限 增城和无锡
	 * @author syuf
	 * @date 2014-04-11
	 */
	@RequestMapping("/getAreasLimit")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAreasLimit(HttpServletRequest request, HttpServletResponse response)throws Exception{
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("3");
		bean.setText("增城");
		easyuilist.add(bean);
		bean = new EasyuiComBoBoxBean();
		bean.setId("4");
		bean.setText("无锡");
		easyuilist.add(bean);
		return easyuilist;
	}
	
	/**
	 * @return 获取处理意见下拉框
	 * @author zy
	 */
	@RequestMapping("/getHandles")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getHandles(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<String, Integer> map = AfterSaleDetectProductBean.handleMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			for(Map.Entry<String, Integer> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey());
				bean.setText(entry.getKey());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 获取主商品状态单选框
	 * @author zy
	 */
	@RequestMapping("/getMainProductStatus")
	public void getMainProductStatus(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		StringBuffer sb = new StringBuffer();
		try {
			Map<Integer, String> map = AfterSaleDetectProductBean.mainProductStatusMap;
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				sb.append("<input type=\"radio\" id=\"productStatus" + entry.getKey() + "\" name=\"mainProductStatus\" value=\"" + entry.getValue() + "\">").append(entry.getValue());
			}
			response.setContentType("text/html;charset=UTF-8");
			response.setCharacterEncoding( "UTF-8");
			response.getWriter().write(sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}
	
	/**
	 * @return 获取主商品状态下拉框
	 * @author zy
	 */
	@RequestMapping("/getMainProductStatusSelect")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getMainProductStatusSelect(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer, String> map = AfterSaleDetectProductBean.mainProductStatusMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("");
			bean.setText("全部");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getValue());
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 获取发票下拉框
	 * @author zy
	 */
	@RequestMapping("/getDebitNoteSelect")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDebitNoteSelect(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer, String> map = AfterSaleDetectProductBean.debitNoteMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getValue());
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 获取商品销售属性下拉框
	 * @author zy
	 */
	@RequestMapping("/getSellTypeSelect")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getSellTypeSelect(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer, String> map = ProductSellPropertyBean.typeMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("全部");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey() + "");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 获取入库类型下拉框
	 * @author zy
	 */
	@RequestMapping("/getAfterSaleStockinTypeSelect")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAfterSaleStockinTypeSelect(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer, String> map = AfterSaleStockin.typeMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("全部");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey() + "");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 获取报损报溢单据类型
	 * @author zy
	 */
	@RequestMapping("/getAfBsbyType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAfBsbyType(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer, String> map = BsbyOperationnoteBean.typeMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("全部");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey() + "");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 获取售后报损报溢库类型
	 * @author zy
	 */
	@RequestMapping("/getAfBsbyStockType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAfBsbyStockType(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("全部");
			bean.setSelected(true);
			comboBoxList.add(bean);
			bean = new EasyuiComBoBoxBean();
			bean.setId(ProductStockBean.STOCKTYPE_AFTER_SALE + "");
			bean.setText("售后库");
			comboBoxList.add(bean);
			bean = new EasyuiComBoBoxBean();
			bean.setId(ProductStockBean.STOCKTYPE_CUSTOMER + "");
			bean.setText("客户库");
			comboBoxList.add(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 获取售后报损报溢状态
	 * @author zy
	 */
	@RequestMapping("/getBsbyCurrentType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getBsbyCurrentType(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer, String> map = BsbyOperationnoteBean.current_typeMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("全部");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey() + "");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 库地区
	 * @author gw
	 */
	@RequestMapping("/getAreaType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAreaType(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
		try {
			Map<Integer, String> map = ProductStockBean.areaMap;
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey() + "");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 库类型
	 * @author gw
	 */
	@RequestMapping("/getStockType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getStockType(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
		try {
			Map<Integer, String> map = ProductStockBean.stockTypeMap;
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey() + "");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	

	/**
	 * @return 获取所有库地区（售后）
	 * @author zy
	 */
	@RequestMapping("/getAllStockArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAllStockArea(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer, String> map = ProductStockBean.areaMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey() + "");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 根据库地区获取库类型（售后）
	 * @author zy
	 */
	@RequestMapping("/getStockTypeByStockArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getStockTypeByStockArea(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		int stockArea = StringUtil.StringToId(StringUtil.convertNull(request.getParameter("stockArea")));//库地区
		try {
			List<StockTypeBean> typeList = ProductStockBean.getStockTypeByArea(stockArea);
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择库类型");
			bean.setSelected(true);
			comboBoxList.add(bean);
			if (typeList != null) {
				for(StockTypeBean stockTypeBean : typeList){
					if (stockTypeBean.getId() == ProductStockBean.STOCKTYPE_BACK 
							|| stockTypeBean.getId() == ProductStockBean.STOCKTYPE_QUALIFIED 
							|| stockTypeBean.getId() == ProductStockBean.STOCKTYPE_DEFECTIVE
							|| stockTypeBean.getId() == ProductStockBean.STOCKTYPE_RETURN
							|| stockTypeBean.getId() == ProductStockBean.STOCKTYPE_CHECK
							|| stockTypeBean.getId() == ProductStockBean.STOCKTYPE_AFTER_SALE) {
						bean = new EasyuiComBoBoxBean();
						bean.setId(stockTypeBean.getId() + "");
						bean.setText(stockTypeBean.getName());
						comboBoxList.add(bean);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @describe 获取产品线列表
	 * @author aohaichen
	 * @date 2014-06-05
	 */
	@RequestMapping("/getProductLineName")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getProductLineName(HttpServletRequest request, HttpServletResponse response)throws Exception{
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("3");
		bean.setText("增城");
		easyuilist.add(bean);
		bean = new EasyuiComBoBoxBean();
		bean.setId("4");
		bean.setText("无锡");
		easyuilist.add(bean);
		return easyuilist;
	}
	
	/***
	 * @Describe 获取商品产品线
	 * @param request
	 * @param response
	 */
	@RequestMapping("/getProductLine")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getProductLine(HttpServletRequest request,HttpServletResponse response){
		WareService wareService = new WareService(); 
		ProductLineService productLineService = new ProductLineService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		List<ProductLineBean> list = new ArrayList<ProductLineBean>();
		try {
			list = productLineService.getProductLineName();
			for(int i = 0 ; i < list.size() ; i++){
				ProductLineBean plb = new ProductLineBean();
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();	
				plb=list.get(i);
				bean.setId(plb.getId()+"");
				bean.setText(plb.getName());
				easyuilist.add(bean);
			}		
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return easyuilist;
	}
	
	/**
	 * @return 获取bi库地区
	 * @author zy
	 */
	@RequestMapping("/getBIStockArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getBIStockArea(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer, String> map = ProductStockBean.stockoutAvailableAreaMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey() + "");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 仓储处理时效的环节
	 * @author zy
	 */
	@RequestMapping("/getBIDividePart")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getBIDividePart(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer, String> map = BiOrderInStockDuration.partMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey() + "");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 仓储处理时效的小时划分
	 * @author zy
	 */
	@RequestMapping("/getBIDivideTime")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getBIDivideTime(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer, String> map = BiOrderInStockDuration.timeTypeMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择");
			bean.setSelected(true);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey() + "");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
//	/**
//	 * @return 加载产品线
//	 * @author zhangxiaolei
//	 */
//	@RequestMapping("/getProductLine")
//	@ResponseBody
//	public List<EasyuiComBoBoxBean> getProductLine(HttpServletRequest request,HttpServletResponse response
//			) throws ServletException, IOException {
//		List<EasyuiComBoBoxBean> comboBoxList = null;
//		try {
//			WareService service =  new WareService();
//			HashMap<Integer, String> map = new HashMap<Integer, String>();
//			List<voProductLine> productLineList =service.getProductLineList("product_line.id>0");
//			for(int i=0;i<productLineList.size();i++){
//				voProductLine p = (voProductLine)productLineList.get(i);
//				map.put(p.getId(), p.getName());
//			}
//			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
//			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
//			bean.setId("-1");
//			bean.setText("请选择");
//			bean.setSelected(true);
//			comboBoxList.add(bean);
//			for(Map.Entry<Integer, String> entry : map.entrySet()){
//				bean = new EasyuiComBoBoxBean();
//				bean.setId(entry.getKey() + "");
//				bean.setText(entry.getValue());
//				comboBoxList.add(bean);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return comboBoxList;
//	}
	/**
	 * @return 加载省份
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getProvinces")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getProvinces(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WareService service =  new WareService(db);
		try {
			HashMap<Integer, String> map = new HashMap<Integer, String>();
			List<Provinces> provincesList = new ArrayList<Provinces>();
			List<EasyuiComBoBoxBean> childrenList = new ArrayList<EasyuiComBoBoxBean>();
			String sql = "select * from provinces";
			ResultSet rs = service.getDbOp().executeQuery(sql);
			while (rs.next()) {
				Provinces province = new Provinces();
				province.setId(rs.getInt("id"));
				province.setName(rs.getString("name"));
				provincesList.add(province);
			}
			rs.close();
			for(int i=0;i<provincesList.size();i++){
				Provinces p = (Provinces)provincesList.get(i);
				map.put(p.getId(), p.getName());
			}
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("(全选)");
			bean.setSelected(true);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				EasyuiComBoBoxBean childrenBean = new EasyuiComBoBoxBean();
				childrenBean.setId(entry.getKey() + "");
				childrenBean.setText(entry.getValue());
				childrenList.add(childrenBean);
			}
			bean.setChildren(childrenList);
			comboBoxList.add(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return comboBoxList;
	}
	/**
	 * @return 加载全国区域划分
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getChinaArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getChinaArea(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		List<EasyuiComBoBoxBean> childrenList = null;
		try {
			HashMap<Integer, String> children = new HashMap<Integer, String>();
			children.put(1, "华北");
			children.put(2, "东北");
			children.put(3, "华东");
			children.put(4, "华中");
			children.put(5, "华南");
			children.put(6, "西南");
			children.put(7, "西北");
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			childrenList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("全国合计");
			bean.setSelected(true);
			for (Entry<Integer, String> entry : children.entrySet()) {
				EasyuiComBoBoxBean childrenBean = new EasyuiComBoBoxBean();
				childrenBean.setId(entry.getKey() + "");
				childrenBean.setText(entry.getValue());
				childrenList.add(childrenBean);
			}
			bean.setChildren(childrenList);
			comboBoxList.add(bean);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 加载配送状态comboBox
	 * @author zy
	 */
	@RequestMapping("/getDeliverState")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDeliverState(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer,String> map = DeliverOrderInfoBean.deliverStateMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey()+"");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 加载发货省份
	 * @author zhangye
	 */
	@RequestMapping("/getSaleProvinces")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getSaleProvinces(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WareService service =  new WareService(db);
		try {
			HashMap<Integer, String> map = new HashMap<Integer, String>();
			List<Provinces> provincesList = new ArrayList<Provinces>();
			List<EasyuiComBoBoxBean> childrenList = new ArrayList<EasyuiComBoBoxBean>();
			String sql = "select * from area_delivery_priority";
			ResultSet rs = service.getDbOp().executeQuery(sql);
			while (rs.next()) {
				Provinces province = new Provinces();
				province.setId(rs.getInt("id"));
				province.setName(rs.getString("province"));
				provincesList.add(province);
			}
			rs.close();
			for(int i=0;i<provincesList.size();i++){
				Provinces p = (Provinces)provincesList.get(i);
				map.put(p.getId(), p.getName());
			}
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("(全选)");
			bean.setSelected(true);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				EasyuiComBoBoxBean childrenBean = new EasyuiComBoBoxBean();
				childrenBean.setId(entry.getKey() + "");
				childrenBean.setText(entry.getValue());
				childrenList.add(childrenBean);
			}
			bean.setChildren(childrenList);
			comboBoxList.add(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 获取可发货库地区
	 * @author zy
	 */
	@RequestMapping("/getStockoutAvailableArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getStockoutAvailableArea(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
		int popId = StringUtil.toInt(request.getParameter("popId"));
		try {
			if(popId == PopBussiness.POP_JD){
				EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
				bean.setId("1");
				bean.setText("京东仓");
				comboBoxList.add(bean);
				return comboBoxList;
			}
		
			Map<Integer, String> map = ProductStockBean.stockoutAvailableAreaMap;
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey() + "");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	/**
	 * @return 获取可发货库地区2
	 * @author zy
	 */
	@RequestMapping("/getStockoutAvailableArea2")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getStockoutAvailableArea2(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			Map<Integer, String> map = ProductStockBean.stockoutAvailableAreaMap;
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("全部");
			bean.setSelected(false);
			comboBoxList.add(bean);
			for(Map.Entry<Integer, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				bean.setId(entry.getKey() + "");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 获取调拨单状态
	 * @author lining
	 */
	@RequestMapping("/getStockExchangeStatus")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getStockExchangeStatus(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择");
			bean.setSelected(true);
			comboBoxList.add(bean);
			bean = new EasyuiComBoBoxBean();
			bean.setId(StockExchangeBean.STATUS0+"");
			bean.setText("未处理");
			comboBoxList.add(bean);
			
			bean = new EasyuiComBoBoxBean();
			bean.setId(StockExchangeBean.STATUS1+"");
			bean.setText("出库处理中");
			comboBoxList.add(bean);
			
			bean = new EasyuiComBoBoxBean();
			bean.setId(StockExchangeBean.STATUS2+"");
			bean.setText("出库审核中");
			comboBoxList.add(bean);
			
			bean = new EasyuiComBoBoxBean();
			bean.setId(StockExchangeBean.STATUS3+"");
			bean.setText("已审核待入库");
			comboBoxList.add(bean);
			
			bean = new EasyuiComBoBoxBean();
			bean.setId(StockExchangeBean.STATUS4+"");
			bean.setText("出库审核未通过");
			comboBoxList.add(bean);
			
			bean = new EasyuiComBoBoxBean();
			bean.setId(StockExchangeBean.STATUS5+"");
			bean.setText("入库处理中");
			comboBoxList.add(bean);
			
			bean = new EasyuiComBoBoxBean();
			bean.setId(StockExchangeBean.STATUS6+"");
			bean.setText("入库审核中");
			comboBoxList.add(bean);
			
			bean = new EasyuiComBoBoxBean();
			bean.setId(StockExchangeBean.STATUS7+"");
			bean.setText("调拨完成");
			comboBoxList.add(bean);
			
			bean = new EasyuiComBoBoxBean();
			bean.setId(StockExchangeBean.STATUS8+"");
			bean.setText("入库审核未通过");
			comboBoxList.add(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 加载省份下拉框
	 * @author zhangye
	 */
	@RequestMapping("/getProvincesCombobox")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getProvincesCombobox(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WareService service =  new WareService(db);
		try {
			List<Provinces> provincesList = new ArrayList<Provinces>();
			String sql = "select * from provinces";
			ResultSet rs = service.getDbOp().executeQuery(sql);
			while (rs.next()) {
				Provinces province = new Provinces();
				province.setId(rs.getInt("id"));
				province.setName(rs.getString("name"));
				provincesList.add(province);
			}
			rs.close();
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean childrenBean = new EasyuiComBoBoxBean();
			childrenBean.setId("0");
			childrenBean.setText("其他省");
			comboBoxList.add(childrenBean);
			for(int i=0;i<provincesList.size();i++){
				Provinces p = (Provinces)provincesList.get(i);
				childrenBean = new EasyuiComBoBoxBean();
				childrenBean.setId(p.getId() + "");
				childrenBean.setText(p.getName());
				comboBoxList.add(childrenBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 根据省份加载市下拉框
	 * @author zhangye
	 */
	@RequestMapping("/getProvinceCity")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getProvinceCity(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		int provinceId = StringUtil.toInt(request.getParameter("provinceId"));
		DbOperation db = new DbOperation(DbOperation.DB_SLAVE);
		WareService service =  new WareService(db);
		try {
			List<ProvinceCity> pcList = new ArrayList<ProvinceCity>();
			String sql = "select * from province_city where province_id=" + provinceId;
			ResultSet rs = service.getDbOp().executeQuery(sql);
			while (rs.next()) {
				ProvinceCity pc = new ProvinceCity();
				pc.setId(rs.getInt("id"));
				pc.setCity(rs.getString("city"));
				pcList.add(pc);
			}
			rs.close();
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean childrenBean = new EasyuiComBoBoxBean();
			childrenBean.setId("0");
			childrenBean.setText("其他市");
			comboBoxList.add(childrenBean);
			for(int i=0;i<pcList.size();i++){
				ProvinceCity p = (ProvinceCity)pcList.get(i);
				childrenBean = new EasyuiComBoBoxBean();
				childrenBean.setId(p.getId() + "");
				childrenBean.setText(p.getCity());
				comboBoxList.add(childrenBean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			service.releaseAll();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 获取配件入库类型
	 * @author lining
	 */
	@RequestMapping("/getFittingBuyStockInType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getFittingBuyStockInType(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		try {
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			Map<Byte,String> map = FittingBuyStockInBean.typeMap;
			if("query".equals(flag)){
				bean.setId("-1");
				bean.setText("请选择");
				bean.setSelected(true);
				comboBoxList.add(bean);
			}
			for(Map.Entry<Byte, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				if(!("query".equals(flag)) && entry.getKey()==FittingBuyStockInBean.TYPE1){
					bean.setSelected(true);
				}
				bean.setId(entry.getKey()+"");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 获取配件类别
	 * @author lining
	 */
	@RequestMapping("/getFittingBuyStockInProductType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getFittingBuyStockInProductType(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		String flag = StringUtil.convertNull(request.getParameter("flag"));
		try {
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			Map<Byte,String> map = FittingBuyStockInBean.fittingTypeMap;
			if("query".equals(flag)){
				bean.setId("-1");
				bean.setText("请选择");
				bean.setSelected(true);
				comboBoxList.add(bean);
			}
			for(Map.Entry<Byte, String> entry : map.entrySet()){
				bean = new EasyuiComBoBoxBean();
				if(!("query".equals(flag)) && entry.getKey()==FittingBuyStockInBean.FITTING_TYPE1){
					bean.setSelected(true);
				}
				bean.setId(entry.getKey()+"");
				bean.setText(entry.getValue());
				comboBoxList.add(bean);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @describe 获取售后周期统计数据的售后类型
	 * @author syuf
	 * @date 2014-04-11
	 */
	@RequestMapping("/getAfterSaleType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getAfterSaleType(HttpServletRequest request, HttpServletResponse response)throws Exception{
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("0");
		bean.setText("不限");
		bean.setSelected(true);
		easyuilist.add(bean);
		
		bean = new EasyuiComBoBoxBean();
		bean.setId(String.valueOf(AfterSaleCycleStatBean.AFTER_SALE_TYPE_RETURN));
		bean.setText("退货");
		easyuilist.add(bean);
		
		bean = new EasyuiComBoBoxBean();
		bean.setId(String.valueOf(AfterSaleCycleStatBean.AFTER_SALE_TYPE_REPLACE));
		bean.setText("换货");
		easyuilist.add(bean);
		
		bean = new EasyuiComBoBoxBean();
		bean.setId(String.valueOf(AfterSaleCycleStatBean.AFTER_SALE_TYPE_REPAIR));
		bean.setText("维修");
		easyuilist.add(bean);
		
		bean = new EasyuiComBoBoxBean();
		bean.setId(String.valueOf(AfterSaleCycleStatBean.AFTER_SALE_TYPE_ORIGINAL));
		bean.setText("原品返回");
		easyuilist.add(bean);
		
		bean = new EasyuiComBoBoxBean();
		bean.setId(String.valueOf(AfterSaleCycleStatBean.AFTER_SALE_TYPE_COMPENSATION_REFUND));
		bean.setText("补偿退款");
		easyuilist.add(bean);
		
		bean = new EasyuiComBoBoxBean();
		bean.setId(String.valueOf(AfterSaleCycleStatBean.AFTER_SALE_TYPE_REISSUE));
		bean.setText("漏发补发");
		easyuilist.add(bean);
		return easyuilist;
	}
	
	/**
	 * @describe 获取备用机库地区
	 * @author lining
	 * @date 2014-10-21
	 */
	@RequestMapping("/getSpareArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getSpareArea(HttpServletRequest request, HttpServletResponse response)throws Exception{
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId(ProductStockBean.AREA_SZ+"");
		bean.setText("深圳");
		bean.setSelected(true);
		easyuilist.add(bean);
		bean = new EasyuiComBoBoxBean();
		bean.setId(ProductStockBean.AREA_ZC+"");
		bean.setText("增城");
		bean.setSelected(false);
		easyuilist.add(bean);
		bean = new EasyuiComBoBoxBean();
		bean.setId(ProductStockBean.AREA_CD+"");
		bean.setText("成都");
		bean.setSelected(false);
		easyuilist.add(bean);		
		return easyuilist;
	}
	
	/**
	 * 获取出入库卡片类型
	 * @param request
	 * @param response
	 * @param flag
	 * @return
	 * 2014-11-3
	 * lining
	 */
	@RequestMapping("/getSpareStockCardType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getSpareStockCardType(HttpServletRequest request, HttpServletResponse response,String flag){
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setText("请选择");
		bean.setSelected(true);
		easyuilist.add(bean);
		flag = StringUtil.convertNull(flag);
		if("in".equalsIgnoreCase(flag)){
			bean = new EasyuiComBoBoxBean();
			bean.setId(String.valueOf(SpareStockCard.TYPE_STOCKIN));
			bean.setText(SpareStockCard.typeMap.get(SpareStockCard.TYPE_STOCKIN));
			easyuilist.add(bean);
			
			bean = new EasyuiComBoBoxBean();
			bean.setId(String.valueOf(SpareStockCard.TYPE_REPLACE_STOCKIN));
			bean.setText(SpareStockCard.typeMap.get(SpareStockCard.TYPE_REPLACE_STOCKIN));
			easyuilist.add(bean);
			
			bean = new EasyuiComBoBoxBean();
			bean.setId(String.valueOf(SpareStockCard.TYPE_UNQUALIFIED_REPLACE_STOUCTIN));
			bean.setText(SpareStockCard.typeMap.get(SpareStockCard.TYPE_UNQUALIFIED_REPLACE_STOUCTIN));
			easyuilist.add(bean);
		}else if("out".equalsIgnoreCase(flag)){
			bean = new EasyuiComBoBoxBean();
			bean.setId(String.valueOf(SpareStockCard.TYPE_BACK_SUPPLIER));
			bean.setText(SpareStockCard.typeMap.get(SpareStockCard.TYPE_BACK_SUPPLIER));
			easyuilist.add(bean);
			
			bean = new EasyuiComBoBoxBean();
			bean.setId(String.valueOf(SpareStockCard.TYPE_REPLACE_STOCKOUT));
			bean.setText(SpareStockCard.typeMap.get(SpareStockCard.TYPE_REPLACE_STOCKOUT));
			easyuilist.add(bean);
			
			bean = new EasyuiComBoBoxBean();
			bean.setId(String.valueOf(SpareStockCard.TYPE_UNQUALIFIED_REPLACE_STOUCTOUT));
			bean.setText(SpareStockCard.typeMap.get(SpareStockCard.TYPE_UNQUALIFIED_REPLACE_STOUCTOUT));
			easyuilist.add(bean);
		}
		return easyuilist;
	}
	/***
	 * @Describe 获取订单分类
	 * @param request
	 * @param response
	 */
	@RequestMapping("/getOrderType")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getOrderType(HttpServletRequest request,HttpServletResponse response){
		WareService wareService = new WareService(); 
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		List<OrderTypeBean> list = new ArrayList<OrderTypeBean>();
		try {
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "id,name");
			paramMap.put("table", " user_order_type");
			paramMap.put("condition", "id>0");
			List listRows = commonMapper.getCommonInfo(paramMap);
			if(listRows!=null && listRows.size()!=0){
				for(int i = 0;i<listRows.size();i++){
					HashMap map = (HashMap)listRows.get(i);
						EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
						bean.setId(map.get("id")+"");
						bean.setText(map.get("name")+"");
						easyuilist.add(bean);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return easyuilist;
	}
	
	/***
	 * @Describe 获取干线公司
	 * @param request
	 * @param response
	 */
	@RequestMapping("/getTrunkCorpInfo")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getTrunkCorpInfo(HttpServletRequest request,HttpServletResponse response){
		WareService wareService = new WareService(); 
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		try {
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", " id,name");
			paramMap.put("table", " trunk_corp_info");
			paramMap.put("condition", " 1=1 and status=0");
			List listRows = commonMapper.getCommonInfo(paramMap);
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择");
			bean.setSelected(true);
			easyuilist.add(bean);
			if(listRows!=null && listRows.size()!=0){
				for(int i = 0;i<listRows.size();i++){
					HashMap map = (HashMap)listRows.get(i);
						EasyuiComBoBoxBean bean2 = new EasyuiComBoBoxBean();
						bean2.setId(map.get("id")+"");
						bean2.setText(map.get("name")+"");
						easyuilist.add(bean2);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return easyuilist;
	}
	
	/***
	 * @Describe 根据干线公司id获取干线公司用户
	 * @param request
	 * @param response
	 */
	@RequestMapping("/getDeliverAdminUser")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDeliverAdminUser(HttpServletRequest request,HttpServletResponse response,String deliverId){
		WareService wareService = new WareService(); 
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		try {
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", " id,username");
			paramMap.put("table", " deliver_admin_user");
			if(deliverId==null){
				paramMap.put("condition", " 1=1 and status=1 and type=1");
			}else{
				paramMap.put("condition", " 1=1 and status=1 and deliver_id ="+deliverId+" and type=1");
			}
			
			List listRows = commonMapper.getCommonInfo(paramMap);
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择");
			bean.setSelected(true);
			easyuilist.add(bean);
			if(listRows!=null && listRows.size()!=0){
				for(int i = 0;i<listRows.size();i++){
					HashMap map = (HashMap)listRows.get(i);
						EasyuiComBoBoxBean bean2 = new EasyuiComBoBoxBean();
						bean2.setId(map.get("id")+"");
						bean2.setText(map.get("username")+"");
						easyuilist.add(bean2);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return easyuilist;
	}
	
	/***
	 * @Describe 根据发货仓id获取目的地
	 * @param request
	 * @param response
	 */
	@RequestMapping("/getDeliverByStockAreaId")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDeliverByStockAreaId(HttpServletRequest request,HttpServletResponse response,String stockAreaId){
		WareService wareService = new WareService(); 
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		try {
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", " dci.id,dci.name as deliver_name");
			paramMap.put("table", " trunk_effect te left join deliver_corp_info dci on te.deliver_id = dci.id");
			paramMap.put("condition", " 1=1 and te.status=0 and te.stock_area_id ="+stockAreaId);
			
			List listRows = commonMapper.getCommonInfo(paramMap);
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean.setId("-1");
			bean.setText("请选择");
			bean.setSelected(true);
			easyuilist.add(bean);
			if(listRows!=null && listRows.size()!=0){
				for(int i = 0;i<listRows.size();i++){
					HashMap map = (HashMap)listRows.get(i);
						EasyuiComBoBoxBean bean2 = new EasyuiComBoBoxBean();
						bean2.setId(map.get("id")+"");
						bean2.setText(map.get("deliver_name")+"");
						easyuilist.add(bean2);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return easyuilist;
	}
	
	/**
	 * @describe 获取备用机库地区
	 * @author lining
	 * @date 2014-10-21
	 */
	@RequestMapping("/getTrunkMode")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getTrunkMode(HttpServletRequest request, HttpServletResponse response)throws Exception{
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setText("请选择");
		bean.setSelected(true);
		easyuilist.add(bean);
		bean = new EasyuiComBoBoxBean();
		bean.setId("1");
		bean.setText("公路");
		easyuilist.add(bean);
		bean = new EasyuiComBoBoxBean();
		bean.setId("2");
		bean.setText("铁路");
		easyuilist.add(bean);	
		bean = new EasyuiComBoBoxBean();
		bean.setId("3");
		bean.setText("空运");
		easyuilist.add(bean);
		return easyuilist;
	}
	
	/**
	 * 
	 * @descripion 获取干线单配送状态
	 * @author 刘仁华
	 * @time  2015年4月22日
	 */
	@RequestMapping("/getTrunkOrderStatus")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getTrunkOrderStatus(HttpServletRequest request, HttpServletResponse response)throws Exception{
		List<EasyuiComBoBoxBean> easyuilist = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("-1");
		bean.setText("请选择");
		bean.setSelected(true);
		easyuilist.add(bean);
		bean = new EasyuiComBoBoxBean();
		bean.setId("1");
		bean.setText("已交接");
		easyuilist.add(bean);
		bean = new EasyuiComBoBoxBean();
		bean.setId("2");
		bean.setText("正常配载");
		easyuilist.add(bean);	
		bean = new EasyuiComBoBoxBean();
		bean.setId("3");
		bean.setText("运输中");
		easyuilist.add(bean);
		bean = new EasyuiComBoBoxBean();
		bean.setId("4");
		bean.setText("派送中");
		easyuilist.add(bean);
		bean = new EasyuiComBoBoxBean();
		bean.setId("5");
		bean.setText("派送完成");
		easyuilist.add(bean);
		return easyuilist;
	}
}

package mmb.stock.spare.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.easyui.EasyuiDataGridBean;
import mmb.easyui.EasyuiPageBean;
import mmb.easyui.Json;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.aftersale.AfterSaleBackSupplier;
import mmb.stock.spare.model.AfterSaleReplaceNewProductRecord;
import mmb.stock.spare.model.SpareBackSupplier;
import mmb.stock.spare.model.SpareBackSupplierForMap;
import mmb.stock.spare.model.SpareBackSupplierProduct;
import mmb.stock.spare.model.SpareBean;
import mmb.stock.spare.model.SpareCargoProductStock;
import mmb.stock.spare.model.SpareProductDetailed;
import mmb.stock.spare.model.SpareProductStock;
import mmb.stock.spare.model.SpareStockCard;
import mmb.stock.spare.model.SpareStockinBean;
import mmb.stock.spare.model.SpareStockinProductBean;
import mmb.stock.spare.model.SpareUpShelves;
import mmb.stock.spare.service.SpareService;
import mmb.stock.spare.service.SpareStockService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

@Controller
@RequestMapping("spareManagerController")
public class SpareManagerController {
	Lock lock = new ReentrantLock();
	@Autowired
	private SpareService spareService;
	
	@Autowired
	private SpareStockService stockService;
	
	/**
	 * 说明：打印备用机单号
	 * 时间：2014-10-21
	 * @author ahc
	 */
	@RequestMapping("/createSpareCode")
	@ResponseBody
	public Json createSpareCode(HttpServletRequest request,HttpServletResponse response,int count){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(3015)) {
			j.setMsg("您没有打印备用机号的操作权限！");
			return j;
		}
		try {
			if(count <= 0){
				j.setMsg("数量必须大于0!");
				return j;
			}
			List<String> codes = spareService.createSpareCode(count);
			if(codes == null || codes.size() == 0){
				j.setMsg("没有生成处理单号!");
				return j;
			}
			j.setSuccess(true);
			j.setObj(codes);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg("操作失败!");
		}
		return j;
	}
	
	/**
	 * 说明：获取备用机表
	 * 时间：2014-10-22
	 * @author ahc
	 */
	@RequestMapping("/getSpareStockProduct")
	@ResponseBody
	public Json getSpareStockProduct(HttpServletRequest request,HttpServletResponse response,String code,String oldImei){
		Json j = new Json();
		code = StringUtil.checkNull(code).trim();
		oldImei = StringUtil.checkNull(oldImei).trim();
		if(!code.equals("") && !oldImei.equals("")){
			j.setMsg("原备用机号、IMEI码请任意输入一个!");
			return j;
		}
		HashMap<String,String> map = new HashMap<String,String>();
		if(!code.equals("")){
			map.put("code", code);
		}
		if(!oldImei.equals("")){
			map.put("imei", oldImei);
		}
		SpareBean bean = spareService.getSpare(map);
		j.setObj(bean);
		return j;
	}
	
	/**
	 * 说明：备用机检测不合格更换
	 * 时间：2014-10-22
	 * @author ahc
	 */
	@RequestMapping("/replacement")
	@ResponseBody
	public Json replacement(HttpServletRequest request,HttpServletResponse response,String oldSpareCode,String oldImei,String newSpareCode,String newImei){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(3013)) {
			j.setMsg("您没有备用机更换操作的权限！");
			return j;
		}
		//备用机更换
		try {
			spareService.replacement(request, response,oldSpareCode,oldImei,newSpareCode,newImei,user);
			j.setSuccess(true);
			j.setMsg("操作成功！");
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
		return j;
	}
	
	/**
	 * 说明：备用机返厂删除一条记录
	 * 时间：2014-12-17
	 * @author ahc
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/delete")
	@ResponseBody
	public Json delete(HttpServletRequest request,HttpServletResponse response,int productId)throws Exception{
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败!");
			return j;
		}
		Map<Integer,SpareProductDetailed> productDetailMap = (Map<Integer, SpareProductDetailed>) request.getSession().getAttribute("productDetailMap");
		//spareCode有可能为多条,分号分隔
		String[] spareCodes = new String[0];
		if(productDetailMap.containsKey(productId)){
			SpareProductDetailed spd = productDetailMap.remove(productId);
			if(spd!=null){
				spareCodes = spd.getSpareCode().split(";");
			}
		}
		LinkedHashSet<String> set = (LinkedHashSet<String>) request.getSession().getAttribute("SpareCodeSet");
		for(int i=0;i<spareCodes.length;i++){
			set.remove(spareCodes[i]);
		}
		request.getSession().setAttribute("SpareCodeSet", set);
		request.getSession().setAttribute("productDetailMap", productDetailMap);
		if(productDetailMap.size()==0){
			request.getSession().removeAttribute("spareBackSupplier");
		}
		j.setSuccess(true);
		return j;
	}
	
	
	/**
	 * 说明：备用机返厂页面重置
	 * 时间：2014-12-17
	 * @author ahc
	 */
	@RequestMapping("/clean")
	@ResponseBody
	public Json clean(HttpServletRequest request,HttpServletResponse response )throws Exception{
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败!");
			return j;
		}
		//重置
		request.getSession().removeAttribute("productDetailMap");
		request.getSession().removeAttribute("SpareCodeSet");
		request.getSession().removeAttribute("spareBackSupplier");
		j.setSuccess(true);
		return j;
		
	}
	
	/**
	 * 说明：备用机返厂
	 * 时间：2014-10-23
	 * @author ahc
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/returnFactory")
	@ResponseBody
	public Json returnFactory(HttpServletRequest request,HttpServletResponse response ,String flag,String productId,String spareCode)throws Exception{
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败!");
			return j;
		}
		
		String spareCodeStr = StringUtil.checkNull(request.getParameter("spareCodes")).trim();
		if(spareCodeStr.length()<0){
			j.setMsg("请输入备用机号!");
			return j;
		}
		String[] spareCodes = spareCodeStr.split("\n");
		//去重备用机号
		LinkedHashSet<String> set = (LinkedHashSet<String>) request.getSession().getAttribute("SpareCodeSet");
		if(set == null){
			set = new LinkedHashSet<String>();
		}
		//检查是否有错误的备用机单号，并且显示供应商等相关信息
		String result = spareService.cheakSpareCode(spareCodes,set,request,user);
		if(result!=null){
			j.setMsg(result);
			return j;
		}
		//备用机返厂明细清单
		Map<Integer,SpareProductDetailed> map = (Map<Integer, SpareProductDetailed>) request.getSession().getAttribute("productDetailMap");
		if(map==null){
			map = new HashMap<Integer,SpareProductDetailed>();
		}
		spareService.addSpareCode(spareCodes,map);
		request.getSession().setAttribute("productDetailMap", map);
		j.setSuccess(true);
		return j;
	}
	
	/**
	 * 说明：备用机返厂打印清单
	 * 时间：2014-10-27
	 * @author ahc
	 */
	@RequestMapping("/printReturnFactory")
	@ResponseBody
	public Json printReturnFactory(HttpServletRequest request,HttpServletResponse response,
			String deleverId,String packageCode,String price,String ourAddress,String zipCode,String receiverName,String phone,
			String total,String remark)throws Exception{
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败!");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(3014)) {
			j.setMsg("您没有备用机返还厂商的操作权限！");
			return j;
		}
		try {
			int spareBackSupplierId = spareService.printReturnFactory(request,response,deleverId,packageCode,price,ourAddress,zipCode,receiverName,phone,total,remark,user);
			j.setMsg("操作成功！");
			j.setSuccess(true);
			j.setObj(spareBackSupplierId);
		} catch (Exception e) {
			j.setMsg(e.getMessage());
		}
		return j;
		
	}
	
	
	/**
	 * 说明：打印清单
	 * 时间：2014-10-28
	 * @author ahc
	 */
	@RequestMapping("/see")
	@ResponseBody
	public EasyuiDataGridJson see(HttpServletRequest request,HttpServletResponse response,String id)throws Exception{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Map<String,String> map = new HashMap<String,String>();
		try {
			map.put("condition", "sbsp.spare_back_supplier_id = "+id);
			List<SpareBackSupplierProduct> list = spareService.getSpareBackSupplierproductJoinProduct(map);
			datagrid.setRows(list);
			datagrid.setTotal((long)list.size());
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} 
		return datagrid;
	}
	
	/**
	 * 说明：打印清单
	 * 时间：2014-10-28
	 * @author ahc
	 */
	@RequestMapping("/print")
	@ResponseBody
	public String print(HttpServletRequest request,HttpServletResponse response,String flag,String id)throws Exception{
		String result="/admin/spare/spareReturnFactoryPrint.jsp";
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		
		//《返还厂商备用机包裹列表》---补打的入口
		Map<String,String> map = new HashMap<String,String>();
		Integer SpareBackSupplierId = 0;
		if(!"".equals(StringUtil.checkNull(id))){
			SpareBackSupplierId = Integer.parseInt(id);
		}else{
			request.setAttribute("msg", "非法参数！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
		}
		SpareBackSupplier sbs  =spareService.getSpareBackSupplier(SpareBackSupplierId);
		if(sbs==null){
			request.setAttribute("msg", "没有找到相应的出库单记录");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		
		map.put("condition", " sbsp.spare_back_supplier_id = "+id+" GROUP BY id");
		List<Map<String,String>> list =spareService.getSpareBackSupplierproductByCondition(map);
		Map<String,SpareProductDetailed> productDetailMap = new HashMap<String,SpareProductDetailed>();
		for(Map<String,String> m : list){
			SpareProductDetailed spd = new SpareProductDetailed();
			spd.setProductId(Integer.parseInt(String.valueOf(m.get("id"))));
			spd.setProductCode(m.get("code"));
			spd.setProductName(m.get("oriname"));
			String imeis = StringUtil.checkNull(m.get("imeis")).trim();
			if(imeis.startsWith(",")){
				imeis = "";
			}
			spd.setImei(imeis);
			int count = StringUtil.checkNull(m.get("codes")).trim().split(",").length;
			spd.setCount(count);
			productDetailMap.put(spd.getProductId()+"", spd);
		}
		
		Map<String,String> printDetailed = new HashMap<String,String>();
		AfterSaleBackSupplier asbs =spareService.getAfterSaleBackSupplier(sbs.getSupplierId());//获取供应商bean
		printDetailed.put("supplierName",asbs.getName());
		printDetailed.put("supplierAddress",asbs.getName());
		printDetailed.put("userName",user.getUsername());
		printDetailed.put("dateTime",DateUtil.getNow().substring(0, 10));
		printDetailed.put("remark",sbs.getRemark());
		printDetailed.put("ourAddress",sbs.getOurAddress());
		printDetailed.put("zipCode",sbs.getOurPost());
		printDetailed.put("receiverName",sbs.getReceiverName());
		printDetailed.put("phone",sbs.getContractPhone());
		
		request.setAttribute("printDetailedMap", printDetailed);
		request.setAttribute("productDetailMap", productDetailMap);
		request.setAttribute("PackageCode", sbs.getPackageCode());
		
		request.getRequestDispatcher(result).forward(request, response);
		return null;
	}
	
	/**
	 * 说明：备用机上架列表
	 * 时间：2014-10-30
	 * @author ahc
	 */
	@RequestMapping("/spareShelves")
	@ResponseBody
	public EasyuiDataGridJson spareShelves(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,
			String spareCode,String productCode,String startTime,String endTime,String areaId) throws Exception{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		List<SpareUpShelves> list = new ArrayList<SpareUpShelves>();
		try {
			StringBuffer buff = new StringBuffer();
			buff.append(" ci.store_type = "+CargoInfoBean.STORE_TYPE2 +" and s.`status` != "+SpareBean.STATUS_BACK_SUPPLIER);
			if(!"".equals(StringUtil.checkNull(spareCode))){
				buff.append(" and s.`code` ='" +spareCode+"'");
			}
			if(!"".equals(StringUtil.checkNull(productCode))){
				buff.append(" and p.`code` ='" +productCode+"'");
			}
			if (!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and ci.area_id =" + areaId + "");
			}
			if(!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))){
				buff.append(" and ss.create_datetime between '" + startTime+" 00:00:00"+"'");
				buff.append(" and '" + endTime+" 23:59:59"+ "'");
			}
			
			Map<String,String> map = new HashMap<String,String>();
			map.put("condition",buff.toString()+" order by ss.create_datetime desc");
			list = spareService.getSpareUpShelfList(map);
			datagrid.setRows(list);
			datagrid.setTotal((long)list.size());
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} 
		return datagrid;
	}
	
	/**
	 * 说明：返还厂商备用机包裹列表 
	 * 时间：2014-10-30
	 * @author ahc
	 */
	@RequestMapping("/spareBackSupplierList")
	@ResponseBody
	public EasyuiDataGridJson getSpareBackSupplierList(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,
			String packageCode,String supplierId,String spareCode,String startTime,String endTime,String areaId,EasyuiPageBean page) throws Exception{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Map<String,String> map = new HashMap<String,String>();
		try {
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.checkNull(packageCode))){
				buff.append(" and sbs.package_code ='" +packageCode+"'");
			}
			if(!"".equals(StringUtil.checkNull(supplierId)) && !"-1".equals(supplierId)){
				buff.append(" and sbs.supplier_id ='" +supplierId+"'");
			}
			if (!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and sbs.area_id =" + areaId + "");
			}
			if(!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))){
				buff.append(" and sbs.create_datetime between '" + startTime+" 00:00:00"+"'");
				buff.append(" and '" + endTime+" 23:59:59"+ "'");
			}else if(("".equals(StringUtil.checkNull(endTime))) && (!"".equals(StringUtil.checkNull(startTime)))){//结束日期未填
				buff.append(" and sbs.create_datetime between '" + startTime+" 00:00:00"+"'");
				buff.append(" and '" + DateUtil.getNow()+"'");
			}else if((!"".equals(StringUtil.checkNull(endTime))) && ("".equals(StringUtil.checkNull(startTime)))){//开始日期未填
				buff.append(" and sbs.create_datetime = '" + DateUtil.getNow()+"'");
			}
			List<SpareBackSupplierForMap> list2 = new ArrayList<SpareBackSupplierForMap>();
			
			map.put("condition", buff.toString());	
			map.put("start", (page.getPage()-1) * page.getRows() + "");
			map.put("count", page.getRows() + "");
			map.put("order", "sbs.create_datetime desc");
			List<Map<String,String>> list = spareService.getSpareBackSupplierByCondition(map);
			for(Map<String,String> m :list){
				SpareBackSupplierForMap sbs = new SpareBackSupplierForMap();
				sbs.setId(Integer.parseInt(String.valueOf(m.get("id")).toString()));
				sbs.setSupplierId(m.get("supplierId"));
				sbs.setPackageCode(m.get("packageCode"));
				sbs.setDeliveryCost(String.valueOf(m.get("deliveryCost")).toString());
				sbs.setDeliveryId(m.get("deliveryId"));
				sbs.setOperateUserName(m.get("operateUserName"));
				sbs.setOurAddress(m.get("ourAddress"));
				sbs.setAreaId(m.get("areaId"));
				sbs.setCreateDatetime(String.valueOf(m.get("createDatetime")).toString());
				list2.add(sbs);
			}
			datagrid.setRows(list2);
			int total = spareService.getSpareBackSupplierByConditionForCount(map);
			datagrid.setTotal((long)total);
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} 
		return datagrid;
	}
	
	/**
	 * 说明：返还厂商备用机包裹列表打印功能
	 * 时间：2014-10-30
	 * @author ahc
	 */
	@RequestMapping("/spareBackSupplierListPrint")
	@ResponseBody
	public EasyuiDataGridJson spareBackSupplierListPrint(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid easyuiPage,
			String packageCode,String supplierId,String spareCode,String startTime,String endTime,String areaId) throws Exception{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Map<String,String> map = new HashMap<String,String>();
		try {
			StringBuffer buff = new StringBuffer();
			if(!"".equals(StringUtil.checkNull(packageCode))){
				buff.append(" and sbs.package_code ='" +packageCode+"'");
			}
			if(!"".equals(StringUtil.checkNull(supplierId)) && !"-1".equals(supplierId)){
				buff.append(" and sbs.supplier_id ='" +supplierId+"'");
			}
			if (!"".equals(StringUtil.checkNull(areaId))) {
				buff.append(" and sbs.area_id =" + areaId + "");
			}
			if(!"".equals(StringUtil.checkNull(startTime)) && !"".equals(StringUtil.checkNull(endTime))){
				buff.append(" and sbs.create_datetime between '" + startTime+" 00:00:00"+"'");
				buff.append(" and '" + endTime+" 23:59:59"+ "'");
			}else if(("".equals(StringUtil.checkNull(endTime))) && (!"".equals(StringUtil.checkNull(startTime)))){//结束日期未填
				buff.append(" and sbs.create_datetime between '" + startTime+" 00:00:00"+"'");
				buff.append(" and '" + DateUtil.getNow()+"'");
			}else if((!"".equals(StringUtil.checkNull(endTime))) && ("".equals(StringUtil.checkNull(startTime)))){//开始日期未填
				buff.append(" and sbs.create_datetime = '" + DateUtil.getNow()+"'");
			}
			if(buff.length()!=0){
				buff.delete(0,4);
				map.put("condition", buff.toString());
			}else{
				buff=null;
			}
			
			List<Map<String,String>> list = spareService.getSpareBackSupplierByCondition(map);
			List<SpareBackSupplierForMap> list2 = new ArrayList<SpareBackSupplierForMap>();
			for(Map<String,String> m :list){
				SpareBackSupplierForMap sbs = new SpareBackSupplierForMap();
				sbs.setSupplierId(m.get("supplierId"));
				sbs.setPackageCode(m.get("packageCode"));
				sbs.setDeliveryCost(String.valueOf(m.get("deliveryCost")).toString());
				sbs.setDeliveryId(m.get("deliveryId"));
				sbs.setOperateUserName(m.get("operateUserName"));
				sbs.setOurAddress(m.get("ourAddress"));
				sbs.setAreaId(m.get("areaId"));
				sbs.setCreateDatetime(String.valueOf(m.get("createDatetime")).toString());
				list2.add(sbs);
			}
			datagrid.setRows(list2);
			datagrid.setTotal((long)list.size());
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} 
		return datagrid;
	}
	
	/**
	 * 新建备用机入库单
	 * @param request
	 * @param response
	 * @throws IOException
	 * @author lining
	 */
	@RequestMapping("/addSpareStockIn")
	@ResponseBody
	public Json addSpareStockIn(HttpServletRequest request, HttpServletResponse response) throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(3011)) {
			j.setMsg("您没有新建备用机入库单的权限！");
			return j;
		}
		String productCode = StringUtil.convertNull(request.getParameter("productCode")).trim();
		int backSupplierId = StringUtil.toInt(request.getParameter("backSupplierId"));
		int areaId = StringUtil.toInt(request.getParameter("areaId"));
		String spareCode = StringUtil.convertNull(request.getParameter("spareCode")).trim();
		String imeiCode = StringUtil.convertNull(request.getParameter("imeiCode")).trim();
		String[] spareCodeAdd = request.getParameterValues("spareCodeAdd");
		String[] imeiCodeAdd = request.getParameterValues("imeiCodeAdd");
		int count = StringUtil.StringToId(StringUtil.checkNull(request.getParameter("count")));
		if(productCode.equals("")){
			j.setMsg("商品编号不能为空!");
			return j;
		}
		if(backSupplierId==-1){
			j.setMsg("请选择供应商!");
			return j;
		}
		if(areaId==-1){
			j.setMsg("请选择库地区!");
			return j;
		}
		if(spareCode.equals("")){
			j.setMsg("备用机号不能为空!");
			return j;
		}
		try {
			HashSet<String> spareCodeSet = new HashSet<String>();
			HashSet<String> imeiSet = new HashSet<String>();
			List<String> spareCodeList = new ArrayList<String>();
			List<String> imeiList = new ArrayList<String>();
			
			//是否是IMEI码商品
			boolean hasIMEI = spareService.getImeiProduct(productCode);
			if(hasIMEI){
				if(imeiCode.equals("")){
					j.setMsg("此备用机关联的商品是IMEI商品,imei码不能为空!");
					return j;
				}
			}else{
				if(!imeiCode.equals("")){
					j.setMsg("非IMEI商品不能添写IMEI码!");
					return j;
				}
			}
			spareCodeSet.add(spareCode);
			spareCodeList.add(spareCode);
			if(!imeiCode.equals("")){
				imeiSet.add(imeiCode);
			}
			imeiList.add(imeiCode);
			
			//去重并把所有的备用机号和imei码放到一起
			if(spareCodeAdd!=null && spareCodeAdd.length>0 && imeiCodeAdd!=null && imeiCodeAdd.length>0 && spareCodeAdd.length==imeiCodeAdd.length){
				for(int i=0;i<spareCodeAdd.length;i++){
					String spareCodeItem = StringUtil.checkNull(spareCodeAdd[i]).trim();
					String imeiCodeItem = StringUtil.checkNull(imeiCodeAdd[i]).trim();
					if(spareCodeItem.equals("")){
						j.setMsg("备用机号不能为空!");
						return j;
					}
					if(hasIMEI){
						if(imeiCodeItem.equals("")){
							j.setMsg("此备用机关联的商品是IMEI商品,imei码不能为空!");
							return j;
						}
					}else{
						if(!imeiCodeItem.equals("")){
							j.setMsg("非IMEI商品不能添写IMEI码!");
							return j;
						}
					}
					if(spareCodeSet.contains(spareCodeItem)){
						j.setMsg("有重复的备用机编号：" + spareCodeItem + ",请仔细检查!");
						return j;
					}
					if(imeiSet.contains(imeiCodeItem)){
						j.setMsg("有重复的IMEI码：" + imeiCodeItem + ",请仔细检查!");
						return j;
					}
					spareCodeSet.add(spareCodeItem);
					if(!imeiCodeItem.equals("")){
						imeiSet.add(imeiCodeItem);
					}
					spareCodeList.add(spareCodeItem);
					imeiList.add(imeiCodeItem);
				}
			}
			SpareStockinBean bean = new SpareStockinBean();
			bean.setAreaId(areaId);
			bean.setCount(count);
			bean.setCreateUserId(user.getId());
			bean.setCreateUserName(user.getUsername());
			bean.setStatus(SpareStockinBean.STATUS_WAIT_AUDIT);
			bean.setSupplierId(backSupplierId);
			synchronized (request.getSession().getId()) {
				String msg = spareService.addSpareStockIn(bean, productCode,spareCodeList,imeiList, user,hasIMEI);
				j.setMsg(msg);
				j.setSuccess(true);
				return j;
			}
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
	}

	/**
	 * 获取入库单列表
	 * 2014-10-23
	 * lining
	 * @throws Exception 
	 * @throws ServletException 
	 */
	@RequestMapping("/getSpareStockInList")
	@ResponseBody
	public EasyuiDataGridBean getSpareStockInList(HttpServletRequest request, HttpServletResponse response,EasyuiPageBean pageBean) throws Exception{
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "请先登录再进行操作!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		int areaId = StringUtil.toInt(request.getParameter("areaId"));
		String stockInCode = StringUtil.checkNull(request.getParameter("stockInCode")).trim();
		String productCode = StringUtil.checkNull(request.getParameter("productCode")).trim();
		String spareCode = StringUtil.checkNull(request.getParameter("spareCode")).trim();
		int backSupplierId = StringUtil.toInt(request.getParameter("backSupplierId"));
		String status = StringUtil.checkNull(request.getParameter("status")).trim();
		String createStartTime = StringUtil.checkNull(request.getParameter("createStartTime"));
		String createEndTime = StringUtil.checkNull(request.getParameter("createEndTime"));
		String auditStartTime = StringUtil.checkNull(request.getParameter("auditStartTime"));
		String auditEndTime = StringUtil.checkNull(request.getParameter("auditEndTime"));
		try{
			StringBuilder condition = new StringBuilder();
			if(areaId>-1){
				condition.append(" and ss.area_id=").append(areaId);
			}
			if(!(stockInCode.equals(""))){
				condition.append(" and ss.code='").append(StringUtil.toSql(stockInCode)).append("'");
			}
			if(!(productCode.equals(""))){
				condition.append(" and p.code='").append(StringUtil.toSql(productCode)).append("'");
			}
			if(!(spareCode.equals(""))){
				condition.append(" and ss.id=(select spare_stockin_id from spare_stockin_product where `code`='").append(spareCode).append("')");
			}
			if(backSupplierId>-1){
				condition.append(" and ss.supplier_id=").append(backSupplierId);
			}
			if(!(status.equals(""))){
				condition.append(" and ss.status in (").append(status).append(")");
			}
			if(!createStartTime.equals("") && !createEndTime.equals("")){
				condition.append(" and ss.create_datetime between '").append(createStartTime).append(" 00:00:00' and '").append(createEndTime).append(" 23:59:59'");
			}
			if(!auditStartTime.equals("") && !auditEndTime.equals("")){
				condition.append(" and ss.audit_datetime between '").append(auditStartTime).append(" 00:00:00' and '").append(auditEndTime).append(" 23:59:59'");
			}
			int total = spareService.getSpareStockInCount(condition.toString());
			List<SpareStockinBean> rows = spareService.getSpareStockInList(condition.toString(),(pageBean.getPage()-1)*pageBean.getRows(),pageBean.getRows()," ss.id desc");
			datagrid.setRows(rows);
			datagrid.setTotal((long)total);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return datagrid;
	}
	
	/**
	 * 获取入库单里备用机列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * 2014-10-24
	 * lining
	 */
	@RequestMapping("/getSpareStockinProductList")
	@ResponseBody
	public EasyuiDataGridBean getSpareStockinProductList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "请先登录再进行操作!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		int stockinId = StringUtil.StringToId(request.getParameter("stockinId"));
		List<SpareStockinProductBean> rows = spareService.getSpareStockinProductList(stockinId);
		if(rows!=null && rows.size()>0){
			datagrid.setRows(rows);
			datagrid.setTotal((long)rows.size());
		}else{
			datagrid.setRows(new ArrayList<SpareStockinProductBean>());
			datagrid.setTotal(0l);
		}
		return datagrid;
	}
	
	/**
	 * 审核入库单
	 * @param request
	 * @param response
	 * @return
	 * 2014-10-27
	 * lining
	 */
	@RequestMapping("/auditStockIn")
	public String auditStockIn(HttpServletRequest request, HttpServletResponse response){
		try{
			voUser user = (voUser)request.getSession().getAttribute("userView");
			if(user == null){
				request.setAttribute("tip", "当前没有登录，操作失败！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(3012)) {
				request.setAttribute("tip", "您没有审核权限！");
				request.setAttribute("result", "failure");
				return "/admin/error";
			}
			String audit = StringUtil.checkNull(request.getParameter("audit"));
			String remark = StringUtil.checkNull(request.getParameter("remark"));
			int stockinId = StringUtil.StringToId(request.getParameter("stockinId"));
			spareService.auditSocktIn(audit, remark, stockinId, user);
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			request.setAttribute("tip", e.getMessage());
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		return "forward:/admin/spare/spareStockInList.jsp";
	}
	
	/**
	 * 获取待报价、待更换、已更换换新机列表
	 * @param request
	 * @param response
	 * @param pageBean
	 * @return
	 * @throws Exception
	 * 2014-10-28
	 * lining
	 */
	@RequestMapping("/getReplaceNewProductWaitQuoteList")
	@ResponseBody
	public EasyuiDataGridBean getReplaceNewProductWaitQuoteList(HttpServletRequest request, HttpServletResponse response,EasyuiPageBean pageBean) throws Exception{
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "请先登录再进行操作!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		String query = StringUtil.checkNull(request.getParameter("query"));
		UserGroupBean group = user.getGroup();
		//报价、无商品可更换操作权限
		boolean quotePriceFlag = group.isFlag(3016);
		//换新机更换号码操作权限
		boolean replaceCodeFlag = group.isFlag(3017);
		String detectCode = StringUtil.checkNull(request.getParameter("detectCode")).trim();
		String afterSaleOrderCode = StringUtil.checkNull(request.getParameter("afterSaleOrderCode")).trim();
		String productCode = StringUtil.checkNull(request.getParameter("productCode")).trim();
		String startTime = StringUtil.checkNull(request.getParameter("startTime"));
		String endTime = StringUtil.checkNull(request.getParameter("endTime"));
		String status = StringUtil.checkNull(request.getParameter("status"));
		try{
			StringBuilder condition = new StringBuilder();
			if(!(detectCode.equals(""))){
				condition.append(" and asrnpr.after_sale_detect_product_code='").append(StringUtil.toSql(detectCode)).append("'");
			}
			if(!(afterSaleOrderCode.equals(""))){
				condition.append(" and asrnpr.after_sale_order_code='").append(StringUtil.toSql(afterSaleOrderCode)).append("'");
			}
			if(!(productCode.equals(""))){
				condition.append(" and p.code='").append(StringUtil.toSql(productCode)).append("'");
			}
			if(!startTime.equals("") && !endTime.equals("")){
				condition.append(" and asrnpr.last_operate_time between '").append(startTime).append(" 00:00:00' and '").append(endTime).append(" 23:59:59'");
			}
			if(!(status.equals(""))){
				condition.append(" and asrnpr.status in (").append(StringUtil.toSql(status)).append(")");
				Map<String,Boolean> map = new HashMap<String,Boolean>();
				if(status.equals("1,3")){
					map.put("quotePriceFlag", quotePriceFlag);
				}else if(status.equals("2")){
					map.put("replaceCodeFlag", replaceCodeFlag);
				}
				List<Map<String,Boolean>> list = new ArrayList<Map<String,Boolean>>();
				list.add(map);
				datagrid.setFooter(list);
			}
			int total = spareService.getAfterSaleReplaceNewProductRecordCout(condition.toString());
			List<AfterSaleReplaceNewProductRecord> rows = spareService.getAfterSaleReplaceNewProductRecordList(condition.toString(),
					(pageBean.getPage()-1)*pageBean.getRows(),pageBean.getRows()," asrnpr.id desc");
				datagrid.setRows(rows);
			datagrid.setTotal((long)total);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return datagrid;
	}
	
	/**
	 * 待报价、无商品可更换操作
	 * @param request
	 * @param response
	 * @return
	 * 2014-10-31
	 * lining
	 */
	@RequestMapping("/replaceNewProductQuotePrice")
	@ResponseBody
	public Json replaceNewProductQuotePrice(HttpServletRequest request, HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(3016)) {
			j.setMsg("您没有报价、无商品可更换操作的权限！");
			return j;
		}
		try{
			int recordId = StringUtil.StringToId(request.getParameter("recordId"));
			int status = StringUtil.StringToId(request.getParameter("status"));
			if(status==0){
				j.setMsg("待报价的换新机记录的状态不对");
				return j;
			}
			String quoteItem = StringUtil.convertNull(request.getParameter("quoteItem")).trim();
			String quote = StringUtil.convertNull(request.getParameter("quote")).trim();
			String[] quoteItemAdd = request.getParameterValues("quoteItemadd");
			String[] quoteAdd = request.getParameterValues("quoteadd");
			StringBuffer quoteItemBuf = new StringBuffer();
			
			if (StringUtil.convertNull(quoteItem).trim().equals("") && !StringUtil.convertNull(quote).trim().equals("")) {
				j.setMsg("报价项为空，报价也得为空！");
				return j;
			} else if (!StringUtil.convertNull(quoteItem).trim().equals("") && StringUtil.convertNull(quote).trim().equals("")) {
				j.setMsg("报价为空，报价项也得为空！");
				return j;
			} else if (StringUtil.convertNull(quoteItem).trim().equals("") && StringUtil.convertNull(quote).trim().equals("")) {
			} else {
				quoteItemBuf.append(StringUtil.convertNull(quoteItem).trim()).append(" ").append(StringUtil.convertNull(quote).trim()).append("\n");
			}
			if (quoteItemAdd != null) {
				for (int i = 0; i < quoteItemAdd.length; i ++) {
					if (StringUtil.convertNull(quoteItemAdd[i]).trim().equals("") && !StringUtil.convertNull(quoteAdd[i]).trim().equals("")) {
						j.setMsg("报价项为空，报价也得为空！");
						return j;
					} else if (!StringUtil.convertNull(quoteItemAdd[i]).trim().equals("") && StringUtil.convertNull(quoteAdd[i]).trim().equals("")) {
						j.setMsg("报价为空，报价项也得为空！");
						return j;
					} else if (StringUtil.convertNull(quoteItemAdd[i]).trim().equals("") && StringUtil.convertNull(quoteAdd[i]).trim().equals("")) {
						continue;
					} else {
						quoteItemBuf.append(StringUtil.convertNull(quoteItemAdd[i]).trim()).append(" ").append(quoteAdd[i].trim()).append("\n");
					}
				}
			}
			spareService.quotePrice(recordId, status,user,quoteItemBuf.toString());
			j.setMsg("操作成功!");
			j.setSuccess(true);
			return j;
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
	}
	
	/**
	 * 更换备用机号码操作
	 * @param request
	 * @param response
	 * @return
	 * 2014-10-31
	 * lining
	 */
	@RequestMapping("/replaceNewProductCode")
	@ResponseBody
	public Json replaceNewProductCode(HttpServletRequest request, HttpServletResponse response){
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json j = new Json();
		if(user == null){
			j.setMsg("当前没有登录，操作失败！");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(3017)) {
			j.setMsg("您没有换新机号码更换操作的权限！");
			return j;
		}
		try{
			int recordId = StringUtil.StringToId(request.getParameter("recordId"));
			String replaceCode = StringUtil.checkNull(request.getParameter("replaceCode"));
			if(replaceCode.equals("")){
				j.setMsg("请输入或扫描做替换的备用机号!");
				return j;
			}
			spareService.replaceNewCode(recordId, replaceCode,user);
			j.setMsg("操作成功!");
			j.setSuccess(true);
			return j;
		}catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
	}

	/**
	 * 说明：备用机库存列表
	 */
	@RequestMapping("/getSpareProductStockList")
	@ResponseBody
	public EasyuiDataGridJson getSpareProductStockList(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid page,
			String parentId1, String parentId2, String parentId3, String supplierId, String productCode) throws Exception{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		try {
			StringBuffer buff = new StringBuffer();
			if(StringUtil.toInt(parentId1) > 0){
				buff.append(" p.parent_id1 =  ").append(parentId1);
			}
			if(StringUtil.toInt(parentId2) > 0){
				if(buff.length() > 0)
					buff.append(" AND ");
				buff.append(" p.parent_id2 =  ").append(parentId2);
			}
			if(StringUtil.toInt(parentId3) > 0){
				if(buff.length() > 0)
					buff.append(" AND ");
				buff.append(" p.parent_id3 =  ").append(parentId3);
			}
			if(StringUtil.toInt(supplierId) > 0){
				if(buff.length() > 0)
					buff.append(" AND ");
				buff.append(" p.id IN ( SELECT DISTINCT product_id FROM spare_stockin WHERE `status` = 1 AND supplier_id = ").append(supplierId).append(" ) ");				
			}			
			if(StringUtil.convertNull(productCode).length() > 0){
				if(buff.length() > 0)
					buff.append(" AND ");
				buff.append(" p.`code` = '").append(productCode).append("' ");				
			}
			
			List<SpareProductStock> list = stockService.getProductStockList(buff.toString(), (page.getPage()-1) * page.getRows(), page.getRows(), null);
			datagrid.setRows(list);
			datagrid.setTotal((long)stockService.getProductStockListCount(buff.toString()));		
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} 
		return datagrid;
	}
	
	/**
	 * 获取备用机历史出入库列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * 2014-11-3
	 * lining
	 */
	@RequestMapping("/getHistoryStockList")
	@ResponseBody
	public EasyuiDataGridBean getHistoryStockList(HttpServletRequest request, HttpServletResponse response,EasyuiPageBean pageBean) throws Exception{
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "请先登录再进行操作!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		int areaId = StringUtil.toInt(request.getParameter("areaId"));
		String operateItemCode = StringUtil.checkNull(request.getParameter("operateItemCode")).trim();
		String productCode = StringUtil.checkNull(request.getParameter("productCode")).trim();
		String spareCode = StringUtil.checkNull(request.getParameter("spareCode")).trim();
		int backSupplierId = StringUtil.toInt(request.getParameter("backSupplierId"));
		String startTime = StringUtil.checkNull(request.getParameter("startTime"));
		String endTime = StringUtil.checkNull(request.getParameter("endTime"));
		int stockInOutType = StringUtil.toInt(request.getParameter("stockInOutType"));
		String type = StringUtil.checkNull(request.getParameter("type"));
		int parentId1 = StringUtil.toInt(request.getParameter("parentId1"));
		int parentId2 = StringUtil.toInt(request.getParameter("parentId2"));
		int parentId3 = StringUtil.toInt(request.getParameter("parentId3"));
		try{
			StringBuilder condition = new StringBuilder();
			if(!type.equals("")){
				condition.append(" and stc.type in (").append(StringUtil.toSql(type)).append(") ");
			}
			if(areaId>-1){
				condition.append(" and stc.area_id=").append(areaId);
			}
			if(!(operateItemCode.equals(""))){
				condition.append(" and stc.operate_item_code='").append(StringUtil.toSql(operateItemCode)).append("'");
			}
			if(!(productCode.equals(""))){
				condition.append(" and p.code='").append(StringUtil.toSql(productCode)).append("'");
			}
			if(!(spareCode.equals(""))){
				condition.append(" and stc.spare_code='").append(StringUtil.toSql(spareCode)).append("'");
			}
			if(backSupplierId>-1){
				condition.append(" and stc.supplier_id=").append(backSupplierId);
			}
			if(!startTime.equals("") && !endTime.equals("")){
				condition.append(" and stc.create_datetime between '").append(startTime).append(" 00:00:00' and '").append(endTime).append(" 23:59:59'");
			}
			if(stockInOutType>-1){
				condition.append(" and stc.type=").append(stockInOutType);
			}
			if(parentId1>-1){
				condition.append(" and p.parent_id1=").append(parentId1);
			}
			if(parentId2>-1){
				condition.append(" and p.parent_id2=").append(parentId2);
			}
			if(parentId3>-1){
				condition.append(" and p.parent_id3=").append(parentId3);
			}
			if(condition.length()>0){
				int total = spareService.getHistoryStockCount(condition.toString());
				List<SpareStockCard> rows = spareService.getHistoryStockList(condition.toString(),
						(pageBean.getPage()-1)*pageBean.getRows(),pageBean.getRows()," stc.create_datetime desc");
				if(rows!=null && rows.size()>0){
					datagrid.setRows(rows);
				}else{
					datagrid.setRows(new ArrayList<SpareStockCard>());
				}
				datagrid.setTotal((long)total);
			}else{
				datagrid.setRows(new ArrayList<SpareStockCard>());
				datagrid.setTotal((long)0);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return datagrid;
	}
	
	/**
	 * 获取备用机列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 * 2014-10-24
	 * lining
	 */
	@RequestMapping("/getSpareList")
	@ResponseBody
	public EasyuiDataGridBean getSpareList(HttpServletRequest request, HttpServletResponse response) throws Exception{
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "请先登录再进行操作!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		int operateItemId = StringUtil.StringToId(request.getParameter("operateItemId"));
		int type = StringUtil.StringToId(request.getParameter("type"));
		if(operateItemId<=0 && type<=0){
			request.setAttribute("msg", "操作错误!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		List<SpareBean> rows = spareService.getSpareList(operateItemId, type);
		if(rows!=null && rows.size()>0){
			datagrid.setRows(rows);
			datagrid.setTotal((long)rows.size());
		}else{
			datagrid.setRows(new ArrayList<SpareBean>());
			datagrid.setTotal(0l);
		}
		return datagrid;
	}

	
	/**
	 * 说明：备用机货位列表
	 */
	@RequestMapping("/getSpareCargoProductStockList")
	@ResponseBody
	public EasyuiDataGridJson getSpareCargoProductStockList(HttpServletRequest request, HttpServletResponse response, EasyuiDataGrid page,
			String cargoCode, String spareCode, String productCode, String areaId) throws Exception{
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		try {
			StringBuffer buff = new StringBuffer();
			if(StringUtil.convertNull(spareCode).length() > 0){
				buff.append(" AND s.`code` = '").append(spareCode).append("' ");
			}
			if(StringUtil.convertNull(productCode).length() > 0){
				buff.append(" AND p.`code` = '").append(productCode).append("' ");
			}
			if(StringUtil.convertNull(cargoCode).length() > 0){
				buff.append(" AND ci.`whole_code` = '").append(cargoCode).append("' ");
			}
			if(StringUtil.toInt(areaId) > -1){
				buff.append(" AND ci.area_id = ").append(areaId).append(" ");
			}
			
			List<SpareCargoProductStock> list = stockService.getCargoProductStockList(buff.toString(), (page.getPage()-1) * page.getRows(), page.getRows(), null);
			datagrid.setRows(list);
			datagrid.setTotal((long)stockService.getCargoProductStockListCount(buff.toString()));			
		} catch (Exception e) {
			System.out.print(DateUtil.getNow());e.printStackTrace();
		} 
		return datagrid;
	}
	
}

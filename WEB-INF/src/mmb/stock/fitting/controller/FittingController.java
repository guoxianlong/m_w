package mmb.stock.fitting.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.easyui.EasyuiDataGridBean;
import mmb.easyui.EasyuiPageBean;
import mmb.easyui.Json;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.stock.aftersale.AfStockService;
import mmb.stock.fitting.model.AfterSaleReceiveFitting;
import mmb.stock.fitting.model.FittingBuyStockInBean;
import mmb.stock.fitting.model.FittingStockinBean;
import mmb.stock.fitting.service.FittingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.buy.BuyAdminHistoryBean;
import adultadmin.bean.buy.BuyStockinBean;
import adultadmin.bean.cargo.CargoInfoBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.NumberUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@RequestMapping("fittingController")
@Controller
public class FittingController {
	
	@Autowired
	private FittingService fittingService;
	/**
	 * 说明：加载缓存起来的配件列表(提交审核之前)
	 * 时间：2014-07-03
	 * @author syuf
	 */
	@RequestMapping("getCacheFitting")
	@ResponseBody
	public EasyuiDataGridBean getCacheFitting(HttpServletRequest request,String flag){
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		try {
			flag = StringUtil.checkNull(flag);
			datagrid = fittingService.getCacheAddFitting(request,flag);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datagrid;
	}
	/**
	 * 说明：缓存配件列表(提交审核之前)
	 * 时间：2014-07-03
	 * @author syuf
	 */
	@RequestMapping("addCacheFitting")
	@ResponseBody
	public Json addCacheFitting(HttpServletRequest request, String fittingCounts,String detectCode,String fittingCodes,String target){
		Json j = new Json();
		try {
			detectCode = StringUtil.checkNull(detectCode);
			fittingCodes = StringUtil.checkNull(fittingCodes);
			fittingCounts = StringUtil.checkNull(fittingCounts);
			target = StringUtil.checkNull(target);
			j = fittingService.addCacheAddFitting(request,detectCode,fittingCodes,fittingCounts,target);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return j;
	}
	/**
	 * 说明：删除缓存起来的配件列表(提交审核之前)
	 * 时间：2014-07-03
	 * @author syuf
	 */
	@RequestMapping("delCacheFitting")
	@ResponseBody
	public Json delCacheFitting(HttpServletRequest request,String detectCode,String fittingCode){
		Json j = new Json();
		try {
			detectCode = StringUtil.checkNull(detectCode);
			fittingCode = StringUtil.checkNull(fittingCode);
			fittingService.delCacheFitting(request,detectCode,fittingCode);
			j.setMsg("成功!");
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg("删除失败!");
		}
		return j;
	}
	/**
	 * 说明：获取配件领用单列表
	 * 时间：2014-07-03
	 * @author syuf
	 * @throws IOException 
	 * @throws ServletException 
	 */
	@RequestMapping("getReceiveFittingDatagrid")
	@ResponseBody
	public EasyuiDataGridBean getReceiveFittingDatagrid(HttpServletRequest request,HttpServletResponse response,EasyuiPageBean page, String fittingName,String areaId,String status,String createUserName) throws ServletException, IOException{
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(2106)){
			request.setAttribute("msg", "你没有查询权限!");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		try {
			datagrid = fittingService.getReceiveFittingDatagrid(page, fittingName,areaId,status,createUserName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datagrid;
	}
	/**
	 * 说明：审核领用单
	 * 时间：2014-07-06
	 * @author syuf
	 */
	@RequestMapping("auditReceiveFitting")
	@ResponseBody
	public Json auditReceiveFitting(HttpServletRequest request, String receiveId,String type,String remark){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("用户未登陆或超时!");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(2103)){
			j.setMsg("你没有审核领用单权限!");
			return j;
		}
		try {
			j = fittingService.auditReceiveFitting(user,StringUtil.checkNull(receiveId),StringUtil.checkNull(type),StringUtil.checkNull(remark));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return j;
	}
	/**
	 * 说明： 编辑加载时获取配件领用单详细信息
	 * 时间：2014-07-06
	 * @author syuf
	 */
	@RequestMapping("getReceiveFittingEdit")
	@ResponseBody
	public Json getReceiveFittingEdit(HttpServletRequest request,String receiveId){
		Json j = new Json();
		try {
			j = fittingService.getReceiveFittingEdit(request,StringUtil.checkNull(receiveId));
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		return j;
	}
	/**
	 * 说明：获取配件领用单详细datagrid
	 * 时间：2014-07-06
	 * @author syuf
	 */
	@RequestMapping("getReceiveFittingDetailDatagrid")
	@ResponseBody
	public EasyuiDataGridBean getReceiveFittingDetailDatagrid(String receiveId){
		EasyuiDataGridBean grid = new EasyuiDataGridBean();
		try {
			List<Map<String,Object>> rows = fittingService.getReceiveFittingDetailDatagrid(StringUtil.checkNull(receiveId));
			grid.setRows(rows);
		} catch (Exception e) {
			e.printStackTrace(); 
		}
		return grid;
	}
	/**
	 * 说明：获取配件领用单相信信息
	 * 时间：2014-07-06
	 * @author syuf
	 */
	@RequestMapping("getReceiveFittingInfo")
	@ResponseBody
	public Json getReceiveFittingInfo(String receiveId){
		Json j = new Json();
		try {
			j = fittingService.getReceiveFittingInfo(StringUtil.checkNull(receiveId));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return j;
	}
	/**
	 * 说明：添加配件领用单 及明细表
	 * 时间：2014-07-03
	 * @author syuf
	 */
	@RequestMapping("addReceiveFitting")
	@ResponseBody
	public Json addReceiveFitting(HttpServletRequest request, String fittingIds,String detectCodes,String fittingCounts,
			String target,String areaId){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("用户未登陆或超时!");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if(!group.isFlag(2102)){
			j.setMsg("你没有添加领用单权限!");
			return j;
		}
		fittingIds = StringUtil.checkNull(fittingIds);
		detectCodes = StringUtil.checkNull(detectCodes);
		fittingCounts = StringUtil.checkNull(fittingCounts);
		target = StringUtil.checkNull(target);
		areaId = StringUtil.checkNull(areaId);
		try {
			fittingService.addReceiveFitting(user,fittingIds,detectCodes,fittingCounts,target,areaId);
		} catch (RuntimeException e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		j.setSuccess(true);
		return j;
	}
	/**
	 * 说明：编辑配件领用单 
	 * 时间：2014-07-03
	 * @author syuf
	 */
	@RequestMapping("editReceiveFitting")
	@ResponseBody
	public Json editReceiveFitting(HttpServletRequest request,String receiveId, String fittingIds,String detectCodes,String fittingCounts,
			String target,String areaId){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("用户未登陆或超时!");
			return j;
		}
		receiveId = StringUtil.checkNull(receiveId);
		fittingIds = StringUtil.checkNull(fittingIds);
		detectCodes = StringUtil.checkNull(detectCodes);
		fittingCounts = StringUtil.checkNull(fittingCounts);
		target = StringUtil.checkNull(target);
		areaId = StringUtil.checkNull(areaId);
		try {
			fittingService.editReceiveFitting(user,receiveId,fittingIds,detectCodes,fittingCounts,target,areaId);
		} catch (RuntimeException e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		j.setSuccess(true);
		return j;
	}
	/**
	 * @Description: 获取配件入库单列表
	 * @auth aohaichen
	 */
	@RequestMapping("/buyStockinList")
	@ResponseBody
	public Object buyStockinList(HttpServletRequest request,HttpServletResponse response,
			String area,String code,String beginDatetime,String endDatetime,String Productcode,String status,String type,EasyuiPageBean page) throws ServletException, IOException{

		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		boolean chakan = group.isFlag(2098);
		boolean queren = group.isFlag(2096);
		boolean shenhe = group.isFlag(2097);
		boolean bianji = group.isFlag(2095);
		if(!chakan){ 
			request.setAttribute("msg", "你无权查看这个采购入库！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> map2 = new HashMap<String,Object>();
		
		EasyuiDataGridJson dataGrid = new EasyuiDataGridJson();
		List<FittingStockinBean> list2 = new ArrayList<FittingStockinBean>();
		StringBuffer condition = new StringBuffer();
		
		if(!"".equals(StringUtil.checkNull(area))){
			condition.append(" and bs.stock_area="+area+"");
		}
		if(!"".equals(StringUtil.checkNull(code))){
			condition.append(" and bs.code= '"+code+"'");
		}
		if(!"".equals(StringUtil.checkNull(beginDatetime)) && !"".equals(StringUtil.checkNull(endDatetime))){
			condition.append(" and bs.create_datetime between '" + beginDatetime+" 00:00:00"+"'");
			condition.append(" and '" + endDatetime+" 23:59:59"+ "'");
		}else if(("".equals(StringUtil.checkNull(endDatetime))) && (!"".equals(StringUtil.checkNull(beginDatetime)))){//结束日期未填
			condition.append(" and bs.create_datetime between '" + beginDatetime+" 00:00:00"+"'");
			condition.append(" and '" + DateUtil.getNow()+"'");
		}else if((!"".equals(StringUtil.checkNull(endDatetime))) && ("".equals(StringUtil.checkNull(beginDatetime)))){//开始日期未填
			condition.append(" and bs.create_datetime = '" + DateUtil.getNow()+"'");
		}
		
		if(!"-1".equals(StringUtil.checkNull(type)) && !"".equals(StringUtil.checkNull(type))){
			condition.append(" and fbs.type=" + StringUtil.toSql(type));
		}
				
		if(!"".equals(StringUtil.checkNull(status))){
			String statuss[] =status.split(",");
			
			if(statuss.length==1){
				condition.append(" and bs.status= '"+statuss[0]+"'");
			}else{
				for(int i = 0 ; i < statuss.length ; i++){
					if(i==0){
						condition.append(" and (bs.status= '"+statuss[i]+"'");
					}
					condition.append(" or bs.status= '"+statuss[i]+"'");
					
					if(i == (statuss.length)-1){
						condition.append(")");
					}
				}
			}				
		}
		map.put("group", "bs.`code`");
		if(!"".equals(StringUtil.checkNull(Productcode))){
			condition.append(" and bsp.product_code= '"+Productcode+"'");
		}
		
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		map.put("order", "bs.create_datetime desc");
		map.put("condition", " bs.stock_type="+ProductStockBean.STOCKTYPE_AFTER_SALE_FIITING+" "+condition.toString());
		
		List<Map<String,Object>> list =fittingService.getBuyStockin(map);	
		int rowCount = fittingService.getSelectBuyStockinListCount(map);	
		for(int i = 0 ; i < list.size() ; i++){			
			map2 =(Map<String,Object>)list.get(i);
			FittingStockinBean fsb = new FittingStockinBean();
			fsb.setChakan(chakan);
			fsb.setQueren(queren);
			fsb.setShenhe(shenhe);
			fsb.setBianji(bianji);
			fsb.setCode(map2.get("code").toString());
			int stockArea =(Integer.parseInt(String.valueOf(map2.get("stock_area"))));			
			fsb.setArea(ProductStockBean.getAreaName(stockArea));
			fsb.setCount(map2.get("stockin_count")+"");
			String statusStr =map2.get("status").toString();
			fsb.setStatus(fittingService.getStatusStr(statusStr));
			fsb.setCreateDatetime(map2.get("create_datetime").toString());
			fsb.setCreateUserName(map2.get("username")+"");
			fsb.setAffirmUserId(map2.get("affirm_user_id")+"");//确认人
			fsb.setAuditingUserId(map2.get("auditing_user_id")+"");	//审核人
			if(map2.get("type")!=null){
				fsb.setType(StringUtil.StringToId(map2.get("type").toString()));
			}
			if(map2.get("fitting_type")!=null){
				fsb.setFittingType(StringUtil.StringToId(map2.get("fitting_type").toString()));
			}
			list2.add(fsb);
		}
		
		dataGrid.setRows(list2);
		dataGrid.setTotal((long)rowCount);
		return dataGrid;
	}
	/**
	 * @Description: 获取配件“入库确认”页
	 * @auth aohaichen
	 */
	@RequestMapping("/buyStockinListconfirm")
	@ResponseBody
	public Object buyStockinListconfirm(HttpServletRequest request,HttpServletResponse response,String code,EasyuiPageBean page) throws ServletException, IOException{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		UserGroupBean group = user.getGroup();
		boolean queren = group.isFlag(2096);
		if(!queren){ 
			request.setAttribute("msg", "你无权查看这个采购入库！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> map2 = new HashMap<String,Object>();
		
		EasyuiDataGridJson dataGrid = new EasyuiDataGridJson();
		List<FittingStockinBean> list2 = new ArrayList<FittingStockinBean>();
		StringBuffer condition = new StringBuffer();
		condition.append(" bs.`code`='"+code+"'");
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		map.put("condition",condition.toString());
		List<Map<String,Object>> list =fittingService.getBuyStockinConfirm(map);	
		int rowCount = fittingService.getSelectBuyStockinListCount(map);
		
		for(int i = 0 ; i < list.size() ; i++){			
			map2 =(Map<String,Object>)list.get(i);
			FittingStockinBean fsb = new FittingStockinBean();
			fsb.setCode(map2.get("code").toString());
			int stockArea =(Integer.parseInt(String.valueOf(map2.get("stock_area"))));			
			fsb.setArea(ProductStockBean.getAreaName(stockArea));
			fsb.setCount(map2.get("stockin_count")+"");
			String statusStr =map2.get("status").toString();
			fsb.setStatus(fittingService.getStatusStr(statusStr));
			fsb.setCreateDatetime(map2.get("create_datetime").toString());
			fsb.setProductCode(map2.get("product_code")+"");
			fsb.setOriname(map2.get("oriname")+"");//
			fsb.setProductCount(map2.get("product_count")+"");
			list2.add(fsb);
		}
		
		dataGrid.setRows(list2);
		dataGrid.setTotal((long)rowCount);
		return dataGrid;
	}
	/**
	 * @Description: 获取配件“入库审核”页
	 * @auth aohaichen
	 */
	@RequestMapping("/buyStockinListAudit")
	@ResponseBody
	public Object buyStockinListAudit(HttpServletRequest request,HttpServletResponse response,String code,EasyuiPageBean page){

		Map<String,Object> map = new HashMap<String,Object>();
		Map<String,Object> map2 = new HashMap<String,Object>();
		
		EasyuiDataGridJson dataGrid = new EasyuiDataGridJson();
		List<FittingStockinBean> list2 = new ArrayList<FittingStockinBean>();
		StringBuffer condition = new StringBuffer();
		condition.append(" bs.`code`='"+code+"'");
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		map.put("condition",condition.toString());
		List<Map<String,Object>> list =fittingService.getBuyStockinAudit(map);	
		int rowCount = fittingService.getSelectBuyStockinListCount(map);
		
		for(int i = 0 ; i < list.size() ; i++){			
			map2 =(Map<String,Object>)list.get(i);
			FittingStockinBean fsb = new FittingStockinBean();
			fsb.setCode(map2.get("code").toString());
			int stockArea =(Integer.parseInt(String.valueOf(map2.get("stock_area"))));			
			fsb.setArea(ProductStockBean.getAreaName(stockArea));
			fsb.setCount(map2.get("stockin_count")+"");
			String statusStr =map2.get("status").toString();
			fsb.setStatus(fittingService.getStatusStr(statusStr));
			fsb.setCreateDatetime(map2.get("create_datetime").toString());
			fsb.setProductCode(map2.get("product_code")+"");
			fsb.setOriname(map2.get("oriname")+"");//
			fsb.setProductCount(map2.get("product_count")+"");
			fsb.setPrice(map2.get("price")+"");
			fsb.setName(map2.get("name")+"");
			list2.add(fsb);
		}
		
		dataGrid.setRows(list2);
		dataGrid.setTotal((long)rowCount);
		return dataGrid;
	}
	/**
	 * @throws IOException 
	 * @throws ServletException 
	 * @Description: 更新”配件入库单“状态
	 * @auth aohaichen
	 */
	@RequestMapping("/updateStockinListconfirm")
	@ResponseBody
	@Transactional(rollbackFor=Exception.class)
	public Json updateStockinListconfirm(HttpServletRequest request,HttpServletResponse response,String operation,FittingStockinBean fsb,EasyuiPageBean page) throws ServletException, IOException{
		Json j = new Json();
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录，操作失败");
			return j;
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(2095);
		if(!viewAll){ 
			j.setMsg("你无权确认这个采购入库！");
			return j;
		}

		Map<String,Object> map = new HashMap<String,Object>();
		StringBuffer condition = new StringBuffer();
		StringBuffer set = new StringBuffer();
		condition.append("`code`='"+fsb.getCode()+"'");
		String strtusStr =fittingService.getStatusStr(fsb.getStatus());

		try {

			if (strtusStr == (BuyStockinBean.STATUS4)+"" || strtusStr == (BuyStockinBean.STATUS6)+"") {				
				j.setMsg("该操作已经完成，不能再更改！");
				return j;
			}
			if("confirm".equals(operation)){//状态改变：待确认->(确认)->待审核
				set.append("`remark`='"+fsb.getRemark()+"', `status`='3' ,`affirm_datetime`='"+DateUtil.getNow()+"' ,affirm_user_id='"+user.getId()+"'");
			}
			if("UNconfirm".equals(operation)){//状态改变：待确认->(不确认)->确认未通过
				set.append("`remark`='"+fsb.getRemark()+"', `status`='1' ,affirm_user_id='"+user.getId()+"'");
			}
			map.put("condition", condition);
			map.put("set",set);

			if(fittingService.updateBuyStockinConfirm(map)==0){			
				j.setMsg("更新采购入库单状态失败！");
				return j;
			}
			
			BuyAdminHistoryBean log = new BuyAdminHistoryBean();
			log.setAdminId(user.getId());
			log.setAdminName(user.getUsername());
			Map<String,Object> map2 = new HashMap<String,Object>();
			map2.put("condition", " bs.code='"+fsb.getCode()+"'");
			List<Map<String,Object>> list =fittingService.getBuyStockin(map2);
			int id=0;
			for(int i =0; i < list.size(); i++){
				map2 =(Map<String,Object>)list.get(i);
				id =(Integer.parseInt(String.valueOf(map2.get("id"))));
			}
			log.setLogId(id);
			log.setLogType(BuyAdminHistoryBean.LOGTYPE_BUY_STOCKIN);
			log.setOperDatetime(DateUtil.getNow());
			log.setRemark("确认采购入库单");
			log.setType(BuyAdminHistoryBean.TYPE_MODIFY);
			if (fittingService.addBuyAdminHistory(log)==0) {
				j.setMsg("添加采购入库单更新日志失败！");
				return j;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("更新失败!");
		}
		j.setSuccess(true);
		return j;
	}
	/**
	 * 获取领用单的详细信息
	 * @return
	 * @author lining
	 * @throws IOException 
	 * @throws ServletException 
	* @date 2014-7-4
	 */
	@RequestMapping("/getReceiveFittingDetail")
	public String getReceiveFittingDetail(HttpServletRequest request,HttpServletResponse response,String receiveFittingId){
		try{
			int id = StringUtil.StringToId(StringUtil.checkNull(receiveFittingId));
			AfterSaleReceiveFitting receiveFitting = fittingService.getReceiveFitting(id);
			String createDatetime = receiveFitting.getCreateDatetime();
			if(createDatetime!=null && createDatetime.length()>0){
				receiveFitting.setCreateDatetime(createDatetime.substring(0, 10));
			}
			int totalCount = 0;
			float totalPrice = 0f;
			if(receiveFitting!=null){
				receiveFitting.setTargetName(AfterSaleReceiveFitting.targetMap.get(receiveFitting.getTarget()));
				List<Map<String,Object>> fittingList = fittingService.getReceiveFittingDetails(receiveFitting.getId());
				if(fittingList!=null && fittingList.size()>0){
					for(int i=0;i<fittingList.size();i++){
						Map<String,Object> row = fittingList.get(i);
						int count = ((Integer)row.get("count")).intValue();
						totalCount += count;
						float price = ((Float)row.get("fittingPrice")).floatValue();
						if(price>0 && count>0){
							totalPrice += price * count; 
						}
					}
				}
				request.setAttribute("fittingList", fittingList);
			}
			request.setAttribute("receiveFitting", receiveFitting);
			request.setAttribute("totalCount", totalCount);
			request.setAttribute("totalPrice", NumberUtil.priceOrder(totalPrice));
			return "/admin/fitting/afterSaleReceiveFittingPrint";
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 售后、用户货位配件列表
	 * @param request
	 * @param response
	 * @param page
	 * @param cargoCode
	 * @param fittingCode
	 * @param fittingName
	 * @param areaId
	 * @param shelfCode
	 * @param floorNum
	 * @param stockType
	 * @return
	 * @author lining
	* @date 2014-7-10
	 */
	@RequestMapping("/getFittingDatagrid")
	@ResponseBody
	public EasyuiDataGridBean getFittingDatagrid(HttpServletRequest request,HttpServletResponse response,EasyuiPageBean page,
			String cargoCode,String fittingCode,String fittingName,String areaId,String shelfCode,String floorNum,String stockType,String fittingType){
		EasyuiDataGridBean datagrid = new EasyuiDataGridBean();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		AfStockService afStockService = new AfStockService(IBaseService.CONN_IN_SERVICE,dbOp);
		try{
			StringBuffer condition = new StringBuffer();
			if(!"".equals(StringUtil.checkNull(cargoCode))){
				condition.append(" and ci.whole_code like '").append(cargoCode).append("%'");
			}
			if(!"".equals(StringUtil.checkNull(fittingCode))){
				condition.append(" and p.code='").append(fittingCode).append("'");
			}
			if(!"".equals(StringUtil.checkNull(fittingName))){
				condition.append(" and p.name='").append(fittingName).append("'");
			}
			if(!"".equals(StringUtil.checkNull(areaId))){
				condition.append(" and ci.area_id=").append(areaId);
			}
			if(!"".equals(StringUtil.checkNull(shelfCode))){
				condition.append(" and cis.code='").append(shelfCode).append("'");
			}
			if(!"".equals(StringUtil.checkNull(floorNum))){
				condition.append(" and ci.floor_num=").append(floorNum);
			}
			if(!"".equals(StringUtil.checkNull(stockType))){
				condition.append(" and ci.stock_type=").append(stockType);
			}
			if(!"-1".equals(StringUtil.checkNull(fittingType)) && !"".equals(StringUtil.checkNull(fittingType))){
				if(fittingType.equals(String.valueOf(FittingBuyStockInBean.FITTING_TYPE1))){
					condition.append(" and ci.type=").append(CargoInfoBean.TYPE3);
				}else if(fittingType.equals(String.valueOf(FittingBuyStockInBean.FITTING_TYPE2))){
					condition.append(" and ci.type=").append(CargoInfoBean.TYPE4);
				}else if(fittingType.equals(String.valueOf(FittingBuyStockInBean.FITTING_TYPE3))){
					condition.append(" and ci.type=").append(CargoInfoBean.TYPE5);
				}
			}
			int total = afStockService.getFittingCargoStockListSize(condition.toString());
			datagrid.setTotal((long)total);
			List<Map<String, String>> rows = afStockService.getFittingCargoStockList(condition.toString(),(page.getPage()-1) * page.getRows(),page.getRows());
			datagrid.setRows(rows);
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
		return datagrid;
	}
}

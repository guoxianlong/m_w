package mmb.dcheck.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.common.dao.CommonDao;
import mmb.common.service.CommonService;
import mmb.dcheck.dao.DynamicCheckCargoDifferenceBeanDao;
import mmb.dcheck.dao.mappers.DynamicCheckExceptionDataMapper;
import mmb.dcheck.dao.mappers.DynamicCheckLogMapper;
import mmb.dcheck.model.DynamicCheckBean;
import mmb.dcheck.model.DynamicCheckCargoBean;
import mmb.dcheck.model.DynamicCheckCargoDifferenceBean;
import mmb.dcheck.service.DCheckDisposeService;
import mmb.dcheck.service.DCheckService;
import mmb.dcheck.service.DynamicCheckLogServiceImpl;
import mmb.easyui.Json;
import mmb.rec.sys.easyui.EasyuiComBoBoxBean;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.EasyuiPageBean;
import mmb.rec.sys.util.BeanColumnToTableColumn;
import mmb.stock.cargo.CargoDeptAreaService;
import mmb.ware.cargo.dao.CargoInfoStockAreaDao;
import mmb.ware.cargo.model.CargoInfoStockArea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.IConstants;
import adultadmin.util.DateUtil;
import adultadmin.util.MyRuntimeException;
import adultadmin.util.StringUtil;


@Controller
@RequestMapping("/dCheckController")
public class DCheckController {
	@Autowired
	public DCheckService dCheckService;
	@Autowired
	private CargoInfoStockAreaDao cargoInfoStockAreaMapper;
	@Autowired
	private DynamicCheckCargoDifferenceBeanDao dynamicCheckCargoDifferenceBeanMapper;
	@Autowired
	private DCheckDisposeService dCheckDisposeService;
	@Autowired
	private DynamicCheckExceptionDataMapper dynamicCheckExceptionDataMapper;
	@Autowired
	private CommonDao commonMapper;
	@Autowired
	private DynamicCheckLogMapper dynamicCheckLogMapper;
	@Autowired
	private DynamicCheckLogServiceImpl dynamicCheckLogServiceImpl;
	
	/**
	 * @return 加载权限遮罩库地区comboBox
	 * @author syuf
	 */
	@RequestMapping("/getDeptAreaComboBox")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDeptAreaComboBox(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		try {
			List<String> areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
			if(areaList != null && areaList.size() > 0){
				ProductStockBean psBean = new ProductStockBean();
				comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
				EasyuiComBoBoxBean bean2 = new EasyuiComBoBoxBean();
				bean2.setId("");
				bean2.setText("请选择库地区");
				comboBoxList.add(bean2);
				for(String s : areaList){
					EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
					bean.setId(s);
					bean.setText(StringUtil.convertNull(psBean.getAreaName(StringUtil.toInt(s))));
					comboBoxList.add(bean);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/**
	 * @return 加载权限遮罩库地区comboBox
	 * @author haoyabin
	 */
	@RequestMapping("/getStockAreaForArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getStockAreaForArea(HttpServletRequest request,HttpServletResponse response,
			@RequestParam("areaId") String areaId
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
		try {
			EasyuiComBoBoxBean bean2 = new EasyuiComBoBoxBean();
			bean2.setId("");
			bean2.setText("仓库区域不限");
			comboBoxList.add(bean2);
			if( areaId != null && !"".equals(areaId) ) {
				int intAreaId = StringUtil.toInt(areaId);
				if( intAreaId != -1 ) {
					Map<String,String> paramMap = CommonService.constructSelectMap("area_id="+intAreaId, -1, -1, "id asc");
					List<CargoInfoStockArea> list = cargoInfoStockAreaMapper.selectCargoInfoStockAreaListSlave(paramMap);
					for(CargoInfoStockArea cisa : list ){
						EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
						bean.setId(cisa.getId().toString());
						bean.setText(cisa.getName());
						comboBoxList.add(bean);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
	
	/***
	 * @Describe 获取盘点计划列表
	 * @param request
	 * @param response
	 * @param page
	 *            easyui分页bean
	 * @param dynamicCheckBean
	 * @return json格式的EasyuiDataGridJson
	 */
	@RequestMapping("/getDynamicChecks")
	@ResponseBody
	public EasyuiDataGridJson getDynamicChecks(HttpServletRequest request,
			HttpServletResponse response, EasyuiPageBean page,
			DynamicCheckBean dynamicCheckBean) {


		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer condition = new StringBuffer();
		condition.append("1=1");
		if (!"".equals(StringUtil.checkNull(dynamicCheckBean.getCode()))) {
			condition.append(" and code='" + dynamicCheckBean.getCode() + "'");
		}
		if (dynamicCheckBean.getCheckType() != 0) {
			condition.append(" and check_type='"
					+ dynamicCheckBean.getCheckType() + "'");
		}
		if (dynamicCheckBean.getAreaId() != -1) {
			condition.append(" and area_id='" + dynamicCheckBean.getAreaId()
					+ "'");
		}
		// 时间查询
		if (!"".equals(StringUtil.checkNull(dynamicCheckBean.getBeginDatetime()))&& !"".equals(StringUtil.checkNull(dynamicCheckBean.getEndDatetime()))) {
			condition.append(" and create_time between '"
					+ dynamicCheckBean.getBeginDatetime() + " 00:00:00" + "'");
			condition.append(" and '" + dynamicCheckBean.getEndDatetime()
					+ " 23:59:59" + "'");
		}

		map.put("condition", condition.toString());
		int rowCount = dCheckService.getDynamicCheckBeanCount(map);
		datagrid.setTotal((long) rowCount);
		map.put("start", (page.getPage() - 1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		String order = null;
		if (page.getSort() != null && !"".equals(page.getSort().trim())) {// 设置排序
			order = "";
			order += BeanColumnToTableColumn.Transform(page.getSort()) + " "
					+ page.getOrder();
		}
		if (order == null || "".equals(order)) {
			order = " id desc";
		}
		map.put("order", order);
		List<DynamicCheckBean> dynamicCheckBeans = dCheckService
				.getDynamicCheckBeans(map);
		datagrid.setRows(dynamicCheckBeans);
		return datagrid;
	}

	/**
	 * 结束盘点
	 */
	@RequestMapping("/endDCheck")
	@ResponseBody
	public Json endDCheck(HttpServletRequest request,
			HttpServletResponse response) {
		String idstr = request.getParameter("id");
		Json json = new Json();
		voUser user = (voUser)request.getSession(false).getAttribute("userView");
		if(user == null) {
			json.setMsg("你没有登录！");
			return json;
		}
		UserGroupBean group = user.getGroup();
		boolean right = group.isFlag(2153);
		if (!right) {
			json.setMsg("你没有结束盘点的权限！");
			return json;
		}
		if (idstr != null && !"".equals(idstr)) {
			Integer id = new Integer(idstr);
			DynamicCheckBean dynamicCheckBean=new DynamicCheckBean();
			dynamicCheckBean.setId(id);
			dynamicCheckBean.setCompleteTime(DateUtil.formatTime(new Date()));
			dynamicCheckBean.setCompleteUserId(user.getId());
			dynamicCheckBean.setCompleteUsername(user.getUsername());
			if (dCheckService.endDCheck(dynamicCheckBean)>0) {
				json.setMsg("操作成功!");
			} else {

				json.setMsg("操作失败!");
			}
		} else {

			json.setMsg("操作失败!");
		}
		return json;
	}
	/***
	 * @Describe 获取盘点明细列表
	 * @param request
	 * @param response
	 * @param page
	 *            easyui分页bean
	 * @param dynamicCheckBean
	 * @return json格式的EasyuiDataGridJson
	 */
	@RequestMapping("/getDCheckDetails")
	@ResponseBody
	public EasyuiDataGridJson getDCheckDetails(HttpServletRequest request,
			HttpServletResponse response, EasyuiPageBean page,
			DynamicCheckCargoBean dynamicCheckCargoBean) {
        String code="";
        String codesession="";
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer condition = new StringBuffer();
		
		condition.append("1=1");
		if(request.getSession(false)!=null){
		codesession=(String) request.getSession(false).getAttribute("codesession");
		}
		code=request.getParameter("code");
		if(!"".equals(StringUtil.checkNull(code))){
			condition.append(" and dynamic_check_id='" +code+ "'");
			request.getSession(true).setAttribute("codesession", code);
		}else if(!"".equals(StringUtil.checkNull(codesession))){
			condition.append(" and dynamic_check_id='" +codesession+ "'");
			request.getSession(true).setAttribute("codesession", codesession);
		}
		
		
	    if (!"".equals(StringUtil.checkNull(dynamicCheckCargoBean.getProductCode()))) {
			condition.append(" and product_code='" +dynamicCheckCargoBean.getProductCode()+ "'");
		}
		if (!"".equals(StringUtil.checkNull(dynamicCheckCargoBean.getCargoWholeCode()))) {
			condition.append(" and cargo_whole_code='" +dynamicCheckCargoBean.getCargoWholeCode()+ "'");
		}
		if (dynamicCheckCargoBean.getCheckResult() != -1) {
			condition.append(" and check_result='"
					+ dynamicCheckCargoBean.getCheckResult() + "'");
		}
		if (dynamicCheckCargoBean.getStatus() != -1) {
			condition.append(" and status='"
					+ dynamicCheckCargoBean.getStatus() + "'");
		}
		if (dynamicCheckCargoBean.getCargoInfoStockAreaId() != -1) {
			condition.append(" and cargo_info_stock_area_id='" + dynamicCheckCargoBean.getCargoInfoStockAreaId()
					+ "'");
		}
		

		map.put("condition", condition.toString());
		int rowCount = dCheckService.getDynamicCheckCargoBeanCount(map);
		datagrid.setTotal((long) rowCount);
		map.put("start", (page.getPage() - 1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		String order = null;
		if (page.getSort() != null && !"".equals(page.getSort().trim())) {// 设置排序
			order = "";
			order += BeanColumnToTableColumn.Transform(page.getSort()) + " "
					+ page.getOrder();
		}
		if (order == null || "".equals(order)) {
			order = " id desc";
		}
		map.put("order", order);
		List<DynamicCheckCargoBean> dynamicCheckCargoBeans = dCheckService
				.getDynamicCheckCargoBeans(map);
		datagrid.setRows(dynamicCheckCargoBeans);
		request.getSession(true).setAttribute("dynamicCheckCargoBeans", dynamicCheckCargoBeans);
		return datagrid;
	}
	/***
	 * @Describe 导出 盘点明细列表
	 * @param request
	 * @param response
	 * @param page
	 *            easyui分页bean
	 * @param dynamicCheckBean
	 * @return json格式的EasyuiDataGridJson
	 */
	@RequestMapping("/exportDCheckDetails")
	public String exportDCheckDetails(HttpServletRequest request,
			HttpServletResponse response, EasyuiPageBean page,
			DynamicCheckCargoBean dynamicCheckCargoBean) {
		 	String code="";
	        String codesession="";
			Map<String, String> map = new HashMap<String, String>();
			StringBuffer condition = new StringBuffer();
			
			condition.append("1=1");
			if(request.getSession(false)!=null){
				codesession=(String) request.getSession(false).getAttribute("codesession");
			}
			code=request.getParameter("code");
			if(!"".equals(StringUtil.checkNull(code))){
				condition.append(" and dynamic_check_id='" +code+ "'");
				request.getSession(true).setAttribute("codesession", code);
			}else if(!"".equals(StringUtil.checkNull(codesession))){
				condition.append(" and dynamic_check_id='" +codesession+ "'");
				request.getSession(true).setAttribute("codesession", codesession);
			}
			
		    if (!"".equals(StringUtil.checkNull(dynamicCheckCargoBean.getProductCode()))) {
				condition.append(" and product_code='" +dynamicCheckCargoBean.getProductCode()+ "'");
			}
			if (!"".equals(StringUtil.checkNull(dynamicCheckCargoBean.getCargoWholeCode()))) {
				condition.append(" and cargo_whole_code='" +dynamicCheckCargoBean.getCargoWholeCode()+ "'");
			}
			if (dynamicCheckCargoBean.getCheckResult() != -1) {
				condition.append(" and check_result='"
						+ dynamicCheckCargoBean.getCheckResult() + "'");
			}
			if (dynamicCheckCargoBean.getStatus() != -1) {
				condition.append(" and status='"
						+ dynamicCheckCargoBean.getStatus() + "'");
			}
			if (dynamicCheckCargoBean.getCargoInfoStockAreaId() != -1) {
				condition.append(" and cargo_info_stock_area_id='" + dynamicCheckCargoBean.getCargoInfoStockAreaId()
						+ "'");
			}
			

			map.put("condition", condition.toString());
			int rowCount = dCheckService.getDynamicCheckCargoBeanCount(map);
			map.put("start", "0");
			map.put("count", rowCount+"");
			String order = null;
			if (page.getSort() != null && !"".equals(page.getSort().trim())) {// 设置排序
				order = "";
				order += BeanColumnToTableColumn.Transform(page.getSort()) + " "
						+ page.getOrder();
			}
			if (order == null || "".equals(order)) {
				order = " id desc";
			}
			map.put("order", order);
			List<DynamicCheckCargoBean> dynamicCheckCargoBeans = dCheckService
					.getDynamicCheckCargoBeans(map);
			request.setAttribute("dynamicCheckCargoBeans", dynamicCheckCargoBeans);
			return "forward:/admin/dcheck/exportDCheckCargoExcel.jsp";
	 }
	
	@RequestMapping("/afreshDCheck")
	@ResponseBody
	public Json afreshDCheck(HttpServletRequest request,
			HttpServletResponse response) {
		String idstr = request.getParameter("checkids");
		Json json = new Json();
		if(idstr!=null && !"".equals(idstr)){
			String[]  checkids=new String[idstr.split(",").length];
			checkids=idstr.split(",");
			
			for (String str : checkids) {
				Integer id = new Integer(str);
				int count=dCheckService.afreshDCheck(id);
				if (count>0) {
					json.setMsg("操作成功!");	
				}else if(count==-1){
					json.setMsg("盘点计划已经结束,不可以重新终盘!");	
				}else{
					 json.setMsg("请选择盘点完成的数据!");
				}
				
			}
			
		} else {
             json.setMsg("操作失败!");
		}
	          return json;
	}
	@RequestMapping("/addDCheck")
	@ResponseBody
	public Json addDCheck(HttpServletRequest request,
			HttpServletResponse response) {
		Json json = new Json();
		voUser user = (voUser)request.getSession(false).getAttribute("userView");
		if(user == null) {
			json.setMsg("你没有登录！");
			return json;
		}
		String type = request.getParameter("type");
		String areaid = request.getParameter("areaid");
		UserGroupBean group = user.getGroup();
		boolean right = group.isFlag(2151);
		if (!right && "2".equals(type)) {
			json.setMsg("你没有生成大盘盘点的权限！");
			return json;
		}
		boolean right2 = group.isFlag(2152);
		if (!right2 && "1".equals(type) ) {
			json.setMsg("你没有生成动碰盘点的权限！");
			return json;
		}
		if(areaid!=null && !"".equals(areaid) && type!=null && !"".equals(type)){
		   DynamicCheckBean  dynamicCheckBean=new DynamicCheckBean();
		   
		   dynamicCheckBean.setCheckType(Integer.parseInt(type));
		   dynamicCheckBean.setAreaId(Integer.parseInt(areaid));
		   dynamicCheckBean.setCreateTime(DateUtil.formatTime(new Date()));
		   dynamicCheckBean.setCreateUserId(user.getId());
		   dynamicCheckBean.setCreateUsername(user.getUsername());
		  
			StringBuffer condition = new StringBuffer();
		 //盘点计划编号：PD20140731001
			String code = "PD"+DateUtil.getNow().substring(2,10).replace("-", ""); 
			condition.append("1=1  order by id desc ");
			
			//生成编号
			DynamicCheckBean  dynamicCheckbycode= dCheckService.getDynamicCheckBean(condition.toString());
			if(dynamicCheckbycode == null){
				code = code + "001";
			}else{
				//获取当日计划编号最大值
				String _code = dynamicCheckbycode.getCode();
				int number = Integer.parseInt(_code.substring(_code.length()-3));
				number++;
				code += String.format("%03d",new Object[]{new Integer(number)});
			}
			dynamicCheckBean.setCode(code);
		     try {
				int resultcount=dCheckService.addDynamicCheckBean(dynamicCheckBean);
				if(resultcount ==1){
					json.setMsg("操作成功!");
				}else if(resultcount >1){
					String msgtype="大盘";
					if(dynamicCheckBean.getCheckType() ==1){
						msgtype="动碰盘";
					}
					json.setMsg("当天还有未完成的"+msgtype+"盘点计划哦!");
				}else{
					 json.setMsg("操作失败!");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		     
		} else {
             json.setMsg("操作失败!");
		}
	          return json;
	}

	/**
	 * @param easyuiPage 分页bean
	 * @param stockArea	仓库区域
	 * @param prouctCode 产品编号
	 * @param area	库地区
	 * @param cargoCode	货位号
	 * @return 差异量明细
	 * @author 郝亚斌
	 */
	@RequestMapping("/getDCheckCargoDifferenceDatagrid")
	@ResponseBody
	public EasyuiDataGridJson getDCheckCargoDifferenceDatagrid (HttpServletRequest request,HttpServletResponse response,
			EasyuiDataGrid easyuiPage,String area, String stockArea, String cargoCode, String productCode) throws ServletException, IOException{
		
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		EasyuiDataGridJson datagridJson = new EasyuiDataGridJson();
		List<DynamicCheckCargoDifferenceBean> list = new ArrayList<DynamicCheckCargoDifferenceBean>();
		datagridJson.setRows(list);
		datagridJson.setTotal(0l);
		if(user == null) {
			datagridJson.setTip("你没有登录！");
			return datagridJson;
		}
		UserGroupBean group = user.getGroup();
		boolean right = group.isFlag(2156);
		if (!right) {
			datagridJson.setTip("你没有盘点差异量查看的权限！");
			return datagridJson;
		}
		try {
			StringBuffer condition = new StringBuffer();
			condition.append(" status = ");
			condition.append(DynamicCheckCargoDifferenceBean.STATUS1);
			if(!"".equals(StringUtil.convertNull(cargoCode)) ) {
					condition.append(" and cargo_whole_code='");
					condition.append(cargoCode);
					condition.append("' ");
			}
			if( !"".equals(StringUtil.checkNull(productCode)) ) {
				condition.append(" and product_code = ");
				condition.append(productCode);
			}
			if (!"".equals(StringUtil.checkNull(stockArea)) && !"-1".equals(StringUtil.convertNull(stockArea))) {
				condition.append(" and cargo_info_stock_area_id = ");
				condition.append(stockArea);
			}
			if (!"".equals(StringUtil.checkNull(area))) {
				condition.append(" and area_id = ");
				condition.append(area);
			}
			
			//总数
			int totalCount = dynamicCheckCargoDifferenceBeanMapper.selectCount(condition.toString());
			datagridJson.setTotal((long)totalCount);
			list = dynamicCheckCargoDifferenceBeanMapper.selectList(condition.toString(), (easyuiPage.getPage()-1)*easyuiPage.getRows(),easyuiPage.getRows(), "status, id desc");
			datagridJson.setRows(list);
			datagridJson.setFooter(null);
		} finally {
		}
		return datagridJson;
	}
	
	/**
	 * 手动平账功能
	 * @param request
	 * @param response
	 * @param area
	 * @return
	 * @Autor 郝亚斌
	 */
	@RequestMapping("disposeCargoCheckManual")
	@ResponseBody
	public EasyuiDataGridJson disposeCargoCheckManual(HttpServletRequest request, HttpServletResponse response, String area) {
		EasyuiDataGridJson result = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null) {
			result.setTip("你没有登录！");
			return result;
		}
		UserGroupBean group = user.getGroup();
		boolean right = group.isFlag(2154);
		if (!right) {
			result.setTip("你没有手动平账的权限！");
			return result;
		}
		if( "".equals(StringUtil.convertNull(area)) || "-1".equals(area)) {
			result.setTip("请选择地区！");
			return result;
		}
		try {
			dCheckDisposeService.disposeCurrentDifferenceWithExchange(area,user);
			result.setTip("操作成功！");
			return result;
		} catch (MyRuntimeException mre) {
			result.setTip(mre.getMessage());
			return result;
		} catch( Exception e ) {
			e.printStackTrace();
			result.setTip("系统异常！");
			return result;
		} finally {
			
		}
	}
	
	/**
	 * 
	 * @param request
	 * @param response
	 * @param area
	 * @return
	 *  @Autor 敖海晨
	 */
	@RequestMapping("generateBsby")
	@ResponseBody
	public EasyuiDataGridJson generateBsby(HttpServletRequest request, HttpServletResponse response, String area) {
		EasyuiDataGridJson result = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null) {
			result.setTip("你没有登录！");
			return result;
		}
		UserGroupBean group = user.getGroup();
		boolean right = group.isFlag(2157);
		if (!right) {
			result.setTip("你没有盘点生成报损报溢的权限！");
			return result;
		}
		if( "".equals(StringUtil.convertNull(area)) || "-1".equals(area)) {
			result.setTip("请选择地区！");
			return result;
		}
		try {
			dCheckDisposeService.disposeCurrentDifferenceWithBsby(area,user);
			result.setTip("操作成功！");
			return result;
		} catch (MyRuntimeException mre) {
			result.setTip(mre.getMessage());
			return result;
		} catch( Exception e ) {
			e.printStackTrace();
			result.setTip("系统异常！");
			return result;
		} finally {
			
		}
	}
	/**
	 * 
	 * @descripion 获取盘点数据异常列表
	 * @author 刘仁华
	 * @time  2015年7月2日
	 */
	@RequestMapping(value = "/getDCheckExceptionData", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public EasyuiDataGridJson getDCheckExceptionData(HttpServletRequest request, HttpServletResponse response,EasyuiDataGrid easyuiPage,
			@RequestParam(value = "area", required = false) String area,
			@RequestParam(value = "dCheckCode", required = false) String dCheckCode,
			@RequestParam(value = "stockArea", required = false) String stockArea,
			@RequestParam(value = "passage", required = false) String passage,
			@RequestParam(value = "cargoCode", required = false) String cargoCode,
			@RequestParam(value = "productCode", required = false) String productCode
			){
		EasyuiDataGridJson result = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null) {
			return result;
		}
		//封装查询条件
		HashMap<String,Object> condition=new HashMap<String,Object>();
		condition.put("area", area);
		condition.put("dCheckCode", dCheckCode);
		condition.put("stockArea", stockArea);
		condition.put("passage", passage);
		condition.put("cargoCode", cargoCode);
		condition.put("productCode", productCode);
		condition.put("index", (easyuiPage.getPage()-1)*easyuiPage.getRows());
		condition.put("count", easyuiPage.getRows());
		//查询总数
		result.setTotal(dynamicCheckExceptionDataMapper.getExceptionDataCount(condition));
		//查询列表
		result.setRows(dynamicCheckExceptionDataMapper.getExceptionDataLst(condition));
		return result;
	}
	
	/**
	 * 
	 * @descripion 打印盘点数据异常列表
	 * @author 刘仁华
	 * @time  2015年7月2日
	 */
	@RequestMapping(value = "/printDCheckExceptionData", method = { RequestMethod.GET,RequestMethod.POST })
	public String printDCheckExceptionData(HttpServletRequest request, HttpServletResponse response,ModelMap map,
			@RequestParam(value = "area", required = false) String area,
			@RequestParam(value = "dCheckCode", required = false) String dCheckCode,
			@RequestParam(value = "stockArea", required = false) String stockArea,
			@RequestParam(value = "passage", required = false) String passage,
			@RequestParam(value = "cargoCode", required = false) String cargoCode,
			@RequestParam(value = "productCode", required = false) String productCode
			) throws ServletException, IOException{
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null) {
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		//封装查询条件
		HashMap<String,Object> condition=new HashMap<String,Object>();
		condition.put("area", area);
		condition.put("dCheckCode", dCheckCode);
		condition.put("stockArea", stockArea);
		condition.put("passage", passage);
		condition.put("cargoCode", cargoCode);
		condition.put("productCode", productCode);
		//异常数据列表（无分页）
		map.put("rsLst",dynamicCheckExceptionDataMapper.getExceptionDataLst(condition));
		return "admin/dcheck/dCheckExceptionDataPrint";
	}
	/**
	 * 
	 * @descripion 根据仓库区域获取巷道
	 * @author 刘仁华
	 * @time  2015年7月3日
	 */
	@RequestMapping("/getPassageForStockArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getPassageForStockArea(HttpServletRequest request,HttpServletResponse response,String stockAreaId
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("");
		bean.setText("巷道不限");
		comboBoxList.add(bean);
		if(!"-1".equals(stockAreaId)){
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "p.id,p.code");
			paramMap.put("table", "cargo_info_passage p");
			paramMap.put("condition", "p.stock_area_id='"+stockAreaId+"'");
			List<HashMap<String, String>> passageLs = commonMapper.getCommonInfo(paramMap);
			if(passageLs != null && passageLs.size() > 0){
				for(HashMap<String, String> passageMap: passageLs){
					bean = new EasyuiComBoBoxBean();
					bean.setId(String.valueOf(passageMap.get("id")));
					bean.setText(String.valueOf(passageMap.get("code")));
					comboBoxList.add(bean);
				}
			}
		}
		return comboBoxList;
	}
	
	/**
	 * 
	 * @descripion 选择库地区（限定无锡、成都）
	 * @author 刘仁华
	 * @time  2015年7月4日
	 */
	@RequestMapping("/getDeptAreaComboBoxB")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getDeptAreaComboBoxB(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
		EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
		bean.setId("");
		bean.setText("请选择库地区");
		comboBoxList.add(bean);
		
		bean = new EasyuiComBoBoxBean();
		bean.setId("4");
		bean.setText("无锡");
		comboBoxList.add(bean);
		
		bean = new EasyuiComBoBoxBean();
		bean.setId("9");
		bean.setText("成都");
		comboBoxList.add(bean);
		return comboBoxList;
	}
	
	/**
	 * 
	 * @descripion 选择仓库区域
	 * @author 刘仁华
	 * @time  2015年7月4日
	 */
	@RequestMapping("/getStockAreaForAreaB")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getStockAreaForAreaB(HttpServletRequest request,HttpServletResponse response,
			@RequestParam("areaId") String areaId
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
		try {
			EasyuiComBoBoxBean bean2 = new EasyuiComBoBoxBean();
			bean2.setId("");
			bean2.setText("仓库区域不限");
			comboBoxList.add(bean2);
			if( areaId != null && !"".equals(areaId) ) {
				int intAreaId = StringUtil.toInt(areaId);
				if( intAreaId != -1 ) {
					Map<String,String> paramMap = CommonService.constructSelectMap("area_id="+intAreaId, -1, -1, "code asc");
					List<CargoInfoStockArea> list = cargoInfoStockAreaMapper.selectCargoInfoStockAreaListSlave(paramMap);
					for(CargoInfoStockArea cisa : list ){
						EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
						bean.setId(cisa.getId().toString());
						bean.setText(cisa.getCode());
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
	 * 
	 * @descripion 获取盘点日志列表
	 * @author 刘仁华
	 * @time  2015年7月2日
	 */
	@RequestMapping(value = "/getDCheckAssignLog", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public EasyuiDataGridJson getDCheckAssignLog(HttpServletRequest request, HttpServletResponse response,EasyuiDataGrid easyuiPage,
			@RequestParam(value = "area", required = false) String area,
			@RequestParam(value = "dCheckCode", required = false) String dCheckCode,
			@RequestParam(value = "stockArea", required = false) String stockArea,
			@RequestParam(value = "passage", required = false) String passage,
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "groupId", required = false) String groupId
			){
		EasyuiDataGridJson result = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null) {
			return result;
		}
		//封装查询条件
		HashMap<String,Object> condition=new HashMap<String,Object>();
		condition.put("area", area);
		condition.put("dCheckCode", dCheckCode);
		condition.put("stockArea", stockArea);
		condition.put("passage", passage);
		condition.put("username", username);
		condition.put("groupId", groupId);
		condition.put("index", (easyuiPage.getPage()-1)*easyuiPage.getRows());
		condition.put("count", easyuiPage.getRows());
		//查询总数
		result.setTotal(dynamicCheckLogMapper.getDynamicCheckLogCount(condition));
		//查询列表
		result.setRows(dynamicCheckLogMapper.getDynamicCheckLogLst(condition));
		return result;
	}
	/**
	 * 
	 * @descripion 删除盘点日志
	 * @author 刘仁华
	 * @throws IOException 
	 * @throws ServletException 
	 * @time  2015年7月4日
	 */
	@RequestMapping(value = "/delDCheckAssignLog", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public Json delDCheckAssignLog(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "logId", required = false) Long logId) throws ServletException, IOException{
		voUser user = (voUser)request.getSession().getAttribute(IConstants.USER_VIEW_KEY);
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Json json = new Json();
		Long flag=dynamicCheckLogMapper.getCheckDataCountByLogId(logId);
		if(flag.longValue()>0){
			json.setSuccess(false);
			json.setMsg("存在盘点记录，不可删除！");
		}else{
			if(dynamicCheckLogServiceImpl.delDynamicCheckLog(logId)==1){
				json.setSuccess(true);
				json.setMsg("删除成功！");
			}else{
				json.setSuccess(false);
				json.setMsg("删除失败！");
			}
		}
		return json;
	}
}

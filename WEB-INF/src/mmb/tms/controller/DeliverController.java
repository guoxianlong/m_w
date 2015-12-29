package mmb.tms.controller;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.EasyuiPageBean;
import mmb.rec.sys.easyui.Json;
import mmb.rec.sys.util.BeanColumnToTableColumn;
import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.tms.model.DeliverBalanceType;
import mmb.tms.model.DeliverKpi;
import mmb.tms.model.DeliverLog;
import mmb.tms.model.DeliverMail;
import mmb.tms.model.ProvinceCityDeliverTreeBean;
import mmb.tms.service.IDeliverService;
import mmb.ware.WareService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@RequestMapping("deliverController")
@Controller
public class DeliverController {
	private static byte[] lock = new byte[0];
	@Autowired
	private IDeliverService deliverService;
	
	/**
	 * @describe 快递公司效率统计
	 * @author syuf
	 * @throws UnsupportedEncodingException 
	 * @date 2014-03-29
	 */
	@RequestMapping("/getHighstockData")
	@ResponseBody
	public long[][] getHighstockData(HttpServletRequest request,HttpServletResponse response,String startDate,String endDate) {
		long[][] data = null;
		try {
			
			String parameter = request.getParameter("parameter");
			String[] parameters = StringUtil.checkNull(parameter).split("_");
			int deliverId = deliverService.getDeliverIdByName(parameters[1]);
			int areaId = -1;
			if("增城".equals(parameters[2])){
				areaId = ProductStockBean.AREA_ZC;
			} else if("无锡".equals(parameters[2])){
				areaId = ProductStockBean.AREA_WX;
			}
			startDate = StringUtil.checkNull(startDate);
			endDate = StringUtil.checkNull(endDate);
			if("intimeTransite".equals(parameters[0])){
				data = deliverService.getDeliverTransitIntimeRate(deliverId,startDate,endDate,areaId);
			} else if("transite".equals(parameters[0])){
				data = deliverService.getDeliverTransitRate(deliverId,startDate,endDate,areaId);
			} else if("collect".equals(parameters[0])){
				data = deliverService.getDeliverCollectRate(deliverId,startDate,endDate,areaId);
			} else if("arrive".equals(parameters[0])){
				data = deliverService.getDeliverArriveRate(deliverId,startDate,endDate,areaId);
			} else if("mailing".equals(parameters[0])){
				data = deliverService.getDeliverMailingRate(deliverId,startDate,endDate,areaId);
			}  else if("audit".equals(parameters[0])){
				data = deliverService.getAuditOrderCount(deliverId,startDate,endDate,areaId);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
	/**
	 * @describe 生成TreeGrid
	 * @author syuf
	 * @date 2014-03-29
	 */
	@RequestMapping("/getConfigDeliverTreeGrid")
	@ResponseBody
	public List<ProvinceCityDeliverTreeBean> getConfigDeliverTreeGrid(HttpServletRequest request,String id) {
		List<ProvinceCityDeliverTreeBean> treeList = null;
		try {
			treeList = deliverService.getProvincesCitys(id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return treeList;
	}
	/***
	 * @Describe 获取邮件发送列表
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getDeliverMailList")
	@ResponseBody
	public EasyuiDataGridJson getDeliverMailList(HttpServletRequest request,HttpServletResponse response,
			 EasyuiDataGrid page,String date){
		
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		HashMap<String,String> map = new HashMap<String,String>();
		StringBuffer condition = new StringBuffer();
		condition.append(" a.id>0 ");
		if(!"".equals(StringUtil.checkNull(date))){
			condition.append(" and a.date='" + date + "'");
		}else{
			condition.append(" and a.date='" + DateUtil.getNow().substring(0,10) + "'");
		}
		map.put("condition",condition.toString());
		int rowCount = 0;
		String order = null;
		if (page.getSort() != null && !"".equals(page.getSort().trim())) {// 设置排序
			order = "";
			order+=BeanColumnToTableColumn.Transform(page.getSort()) + " " + page.getOrder();
		}
		if(order == null || "".equals(order)){
			order = " b.id asc";
		}
		java.util.List<DeliverMail> list = null;
		java.util.List<DeliverCorpInfoBean> list1 = null;
		list = deliverService.getDeliverMailList(map);
		HashMap<String,String> map1 = new HashMap<String,String>();
		map1.put("condition","b.id>0");
		map1.put("start", (page.getPage()-1) * page.getRows() + "");
		map1.put("count", page.getRows() + "");
		map1.put("order", order);
		list1 = deliverService.getDeliverMailList1(map1);
		if (list1 != null && list1.size() > 0 && list != null && list.size() > 0) {
			for (int i = 0; i < list1.size(); i++) {
				for (int j = 0; j < list.size(); j++) {
					DeliverCorpInfoBean mail1 = (DeliverCorpInfoBean) list1.get(i);
					DeliverMail mail = (DeliverMail) list.get(j);
					if (mail1.getId() == mail.getDeliverId()) {
						mail1.setSendTime(mail.getSendTime());
						mail1.setStatus(mail.getStatus());
						mail1.setTransitCount(mail.getTransitCount());
					}
				}
			}
		}
		HashMap<String,String> map2 = new HashMap<String,String>();
		map2.put("condition","b.id>1");
		rowCount = deliverService.getDeliverMailCount(map2);
		datagrid.setTotal((long)rowCount);
		datagrid.setRows(list1);
		return datagrid;
	}
	/**
	 * @describe 删除快递公司配置
	 * @author syuf
	 * @date 2014-04-01
	 */
	@RequestMapping("/delDeliverConfig")
	@ResponseBody
	public Json delDeliverConfig(HttpServletRequest request,String ids,String parentId) {
		Json j = new Json();
		try {
			ids = StringUtil.checkNull(ids);
			parentId = StringUtil.checkNull(parentId);
			//根据上一级父id判断是不是删除全国的配置
			if("-1".equals(parentId.split("_")[2])){//全国
				deliverService.delDeliverConfigDefault(ids);
			} else {
				deliverService.delDeliverConfig(ids);
			}
			j.setMsg("删除成功!");
			j.setSuccess(true);
		} catch (RuntimeException e) {
			j.setMsg(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return j;
	}
	/**
	 * @describe 添加及编辑快递公司配置
	 * @author syuf
	 * @date 2014-04-01
	 */
	@RequestMapping("/configDeliver")
	@ResponseBody
	public Json configDeliver(HttpServletRequest request,String id,String deliver,String zc,String wx,String parentId,String index) {
		Json j = new Json();
		try {
			List<ProvinceCityDeliverTreeBean> addList = new ArrayList<ProvinceCityDeliverTreeBean>();
			List<ProvinceCityDeliverTreeBean> editList = new ArrayList<ProvinceCityDeliverTreeBean>();
			if(!"".equals(StringUtil.checkNull(id))){
				ProvinceCityDeliverTreeBean bean = null;
				for(int i = 0; i < id.split("\\*").length; i++){
					if("temp".equalsIgnoreCase(id.split("\\*")[i].split("_")[0])){
						bean = new ProvinceCityDeliverTreeBean();
						bean.setParentId(parentId.split("\\*")[i]);
						bean.setWx(wx.split("\\*")[i]);
						bean.setZc(zc.split("\\*")[i]);
						bean.setDeliver(deliver.split("\\*")[i]);
						bean.setIndex(index.split("\\*")[i]);
						addList.add(bean);
					}else{
						bean = new ProvinceCityDeliverTreeBean();
						bean.setId(id.split("\\*")[i]);
						bean.setParentId(parentId.split("\\*")[i]);
						bean.setWx(wx.split("\\*")[i]);
						bean.setZc(zc.split("\\*")[i]);
						bean.setDeliver(deliver.split("\\*")[i]);
						bean.setIndex(index.split("\\*")[i]);
						editList.add(bean);
					}
				}
			}
			deliverService.configDeliver(addList,editList);
			j.setMsg("配置成功!");
			j.setSuccess(true);
		} catch (RuntimeException e) {
			j.setMsg(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return j;
	}
	/***
	 * @Describe 包裹单号监控页面
	 * @author zhangxiaolei
	 */
	@RequestMapping("/getDeliverPackageCodeList")
	@ResponseBody
	public EasyuiDataGridJson getDeliverPackageCodeList(HttpServletRequest request,HttpServletResponse response,
			EasyuiPageBean page,DeliverMail city){
		
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("condition", "a.id>0");
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		String order = null;
		if (page.getSort() != null && !"".equals(page.getSort().trim())) {// 设置排序
			order = "";
			order+=BeanColumnToTableColumn.Transform(page.getSort()) + " " + page.getOrder();
		}
		if(order == null || "".equals(order)){
			order = " a.id asc";
		}
		map.put("order", order);
		java.util.List<DeliverMail> list = deliverService.getDeliverPackageCodeList(map);
		HashMap<String, String> map2 = new HashMap<String, String>();
		map2.put("condition", "b.id>0");
		int rowCount = deliverService.getDeliverMailCount(map2);
		datagrid.setTotal((long)rowCount);
		datagrid.setRows(list);
		return datagrid;
	}
	/***
	 * @批量添加包裹单号
	 * @author zhangxiaolei
	 */
	@RequestMapping("/insertPackageCode")
	public String insertPackageCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String data = request.getParameter("packageCode");
		String deliver = request.getParameter("delivers");
		voUser loginUser = (voUser) request.getSession().getAttribute("userView");
		if (loginUser == null) {
			request.setAttribute("msg", "当前没有登录，添加失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		if ("0".equals(deliver)||"".equals(deliver)) {
			request.setAttribute("msg", "请指定一个快递公司！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		synchronized (lock) {
			int result = 0, failure = 0;
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			WareService wareService = new WareService(dbOp);
			try {
				StringBuffer error = new StringBuffer();
				if (data == null || data.length() == 0) {
					request.setAttribute("msg", "包裹单号不能为空！");
					return "admin/rec/err";
				} else {
					BufferedReader read = new BufferedReader(new StringReader(data));
					String tmp = null;
					wareService.getDbOp().startTransaction();
					while ((tmp = read.readLine()) != null) {
						String[] str = tmp.trim().split("\\s");
						if (str.length >= 1) {
							String packageCode = str[0];
							if (packageCode != null && deliver != null) {
								String queryCode = "select * from deliver_package_code where deliver=" + deliver + " and package_code='"
										+ packageCode + "'";
								ResultSet rs =wareService.getDbOp().executeQuery(queryCode);
								if (rs.next()) {
									error.append("<font color=\"red\">包裹单号重复").append(packageCode).append("</font><br/>");
									failure++;
								} else {
									wareService.release(wareService.getDbOp());
									String sql = "INSERT INTO deliver_package_code  (deliver, package_code,used) VALUES (" + deliver + ",'"+ packageCode + "',0)";
									if (wareService.executeUpdate(sql) != true) {
										wareService.getDbOp().rollbackTransaction();
										request.setAttribute("msg", "数据库操作失败！");
										return "admin/rec/err";
									}else{
										result++;
									}
									wareService.release(wareService.getDbOp());
								}
								wareService.release(wareService.getDbOp());
							}
						}
					}
					String upsql = "UPDATE deliver_corp_info SET add_package_code_datetime = '"+DateUtil.getNow()+"', add_package_code_count = "+result+" where id="+deliver;
					if (wareService.executeUpdate(upsql) != true) {
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("msg", "数据库操作失败！");
						return "admin/rec/err";
					}
					wareService.release(wareService.getDbOp());
					String insertSqlLog = "INSERT INTO deliver_log (deliver_id, create_datetime,user_id,user_name,content,type) VALUES " +
							"("+deliver+",'"+DateUtil.getNow().substring(0,19)+"',"+loginUser.getId()+",'"+loginUser.getUsername()+"','添加包裹单',1)";
					if (wareService.executeUpdate(insertSqlLog) != true) {
						wareService.getDbOp().rollbackTransaction();
						request.setAttribute("msg", "数据库操作失败！");
						return "admin/rec/err";
					}
					wareService.release(wareService.getDbOp());
					wareService.getDbOp().commitTransaction();
					String info = "<font color='red'>快递公司修改成功" + result + "个，失败" + failure + "个！</font><br/>";
					request.setAttribute("info", info);
					request.setAttribute("error", error+"");
				}
			} catch (Exception e) {
				wareService.getDbOp().rollbackTransaction();
				e.printStackTrace();
				request.setAttribute("info", e.getMessage());
			} finally {
				dbOp.release();
			}
		}
		return  "admin/tms/insertPackageCode";
	}
	/**
	 * 添加快递公司
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/addDeliver")
	@ResponseBody
	@Transactional(rollbackFor=Exception.class)
	public Json addDeliver(HttpServletRequest request,HttpServletResponse response)throws Exception{
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user==null){
			j.setMsg("请先登录!");
			return j;
		}
		int changeable = StringUtil.parstInt(request.getParameter("changeable"));
		String name = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("name")));
		String phone = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("phone")));
		String webAddress = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("webAddress")));
		int balanceCorpId = StringUtil.StringToId(request.getParameter("balanceCompany"));//结算公司id
		int status = StringUtil.parstInt(request.getParameter("status"));//快递公司状态
		int buyModeType = StringUtil.parstInt(request.getParameter("buyModeType"));
		int channel = StringUtil.parstInt(request.getParameter("channel"));
		int formType = StringUtil.parstInt(request.getParameter("formType"));
		int packageType = StringUtil.parstInt(request.getParameter("packageType"));
		String address = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("address")));
		int codPackageType = StringUtil.parstInt(request.getParameter("codPackageType"));
		String codAccount = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("codAccount")));
		String codPassword = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("codPassword")));
		int paidPackageType = StringUtil.parstInt(request.getParameter("paidPackageType"));
		String paidAccount = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("paidAccount")));
		String paidPassword = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("paidPassword")));
		String mail = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("mail")));
		int deliveryRate = StringUtil.parstInt(StringUtil.dealParam(request.getParameter("deliveryRate")));
		int overtimeRate = StringUtil.parstInt(StringUtil.dealParam(request.getParameter("overtimeRate")));
		synchronized(lock){
			//增加快递公司
			DeliverCorpInfoBean deliver = new DeliverCorpInfoBean();
			deliver.setName(name);
			deliver.setAddress(address);
			deliver.setChangeable(changeable);
			deliver.setPhone(phone);
			deliver.setWebAddress(webAddress);
			deliver.setBuyModeType(buyModeType);
			deliver.setChannel(channel);
			deliver.setFormType(formType);
			deliver.setPackageType(packageType);
			deliver.setCodPackageType(codPackageType);
			deliver.setCodAccount(codAccount);
			deliver.setCodPassword(codPassword);
			deliver.setPaidPackageType(paidPackageType);
			deliver.setPaidAccount(paidAccount);
			deliver.setPaidPassword(paidPassword);
			deliver.setMail(mail);
			String nowTime = DateUtil.getNow();
			deliver.setLastOperDatetime(nowTime);
			deliver.setCreateDatetime(nowTime);
			deliver.setStatus(status);
			deliver.setDeliveryRate(deliveryRate);
			deliver.setOvertimeRate(overtimeRate);
			deliver.setToken("token");
			deliver.setDays("3-5");
			deliver.setPinyin("pinyin");
			deliver.setIsems(0);
			deliver.setSendsms("000");
			deliver.setNameWap("name");
			int deliverId = deliverService.addDeliverCorpInfoBean(deliver);
			
			if(deliverId<0){
				j.setMsg("添加快递公司失败");
				throw new RuntimeException("添加快递公司失败");
			}
			//增加快递公司log
			DeliverLog log = new DeliverLog();
			log.setCreateDatetime(nowTime);
			log.setDeliverId(deliverId);
			log.setUserId(user.getId());
			log.setUserName(user.getUsername());
			log.setContent("添加" + name + "快递公司");
			log.setType(0);
			int count = deliverService.addDeliverLog(log);
			if(count<=0){
				j.setMsg("添加快递公司操作日志失败!");
				throw new RuntimeException("添加快递公司操作日志失败!");
			}
			
			//增加快递公司与结算公司之间的关系
			DeliverBalanceType deliverBalanceType = new DeliverBalanceType();
			deliverBalanceType.setDeliverId(deliverId);
			deliverBalanceType.setBalanceTypeId(balanceCorpId);
			count = deliverService.addDeliverBalanceType(deliverBalanceType);
			if(count<=0){
				j.setMsg("添加结算公司与快递公司关系失败!");
				throw new RuntimeException("添加结算公司与快递公司关系失败!");
			}
			
			//增加快递公司kpi指标
			HashMap<Integer,String> areaMap = ProductStockBean.areaMap;
			if(areaMap!=null && areaMap.size()>0){
				for(Integer key : areaMap.keySet()){
					String lastTransitTime = StringUtil.convertNull(request.getParameter("lastTransitTime" + key.toString()));
					String collectTime = StringUtil.convertNull(request.getParameter("collectTime" + key.toString()));
					String arriveTime = StringUtil.convertNull(request.getParameter("arriveTime" + key.toString()));
					String mailingTime = StringUtil.convertNull(request.getParameter("mailingTime" + key.toString()));
					String sendTime = StringUtil.convertNull(request.getParameter("sendTime" + key.toString()));
					DeliverKpi kpi = new DeliverKpi();
					kpi.setAreaId(key.shortValue());
					kpi.setDeliverId(deliverId);
					kpi.setLastestTransitTime(lastTransitTime);
					kpi.setCollectTime(collectTime);
					kpi.setArriveTime(arriveTime);
					kpi.setMailingTime(mailingTime);
					kpi.setSendTime(sendTime);
					count = deliverService.addDeliverKPI(kpi);
					if(count<=0){
						j.setMsg("添加快递公司kpi指标失败!");
						throw new RuntimeException("添加快递公司kpi指标失败!");
					}
				}
			}
		}
		j.setMsg("添加成功");
		j.setSuccess(true);
		return j;
	}
	/**
	 * 获取快递公司信息进入编辑页面
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/editDeliver")
	public String editDeliver(HttpServletRequest request,HttpServletResponse response)throws Exception{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user==null){//需要登录
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		
		int deliverId = StringUtil.StringToId(request.getParameter("deliverId"));
		DeliverCorpInfoBean deliverInfo = deliverService.getDeliverCorpInfo(deliverId);
		if(deliverInfo==null){//不存在此快递公司
			request.setAttribute("tip", "不存在此快递公司!");
			return "/admin/tms/editDeliver";
		}
		request.setAttribute("deliverInfo", deliverInfo);
		DeliverBalanceType deliverBalanceType = deliverService.getDeliverBalanceTypeByDeliverId(deliverId);
		if(deliverBalanceType==null){
			request.setAttribute("tip", "此快递公司不存在结算公司!");
			return "/admin/tms/editDeliver";
		}
		request.setAttribute("deliverBalanceType", deliverBalanceType);
		//根据快递公司id查询快递公司kpi
		HashMap<String,String> map = new HashMap<String,String>();
		map.put("deliverId", ""+deliverId);
		List<DeliverKpi> kpiList = deliverService.getDeliverKpiList(map);
		HashMap<Integer,DeliverKpi> kpiMap = new HashMap<Integer,DeliverKpi>();
		if(kpiList!=null && kpiList.size()>0){
			for(int i=0;i<kpiList.size();i++){
				kpiMap.put(Integer.valueOf(kpiList.get(i).getAreaId()), kpiList.get(i));
			}
		}
		request.setAttribute("kpiMap", kpiMap);
		return "/admin/tms/editDeliver";
	}
	/**
	 * 保存编辑信息
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/saveDeliver")
	@ResponseBody
	@Transactional(rollbackFor=Exception.class)
	public Json saveDeliver(HttpServletRequest request,HttpServletResponse response){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user==null){
			j.setMsg("请先登录!");
			return j;
		}
		int deliverId = StringUtil.StringToId(request.getParameter("deliverId"));
		int changeable = StringUtil.parstInt(request.getParameter("changeable"));
		String name = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("name")));
		String phone = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("phone")));
		String webAddress = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("webAddress")));
		int balanceCorpId = StringUtil.StringToId(request.getParameter("balanceCompany"));//结算公司id
		int status = StringUtil.parstInt(request.getParameter("status"));//快递公司状态
		int buyModeType = StringUtil.parstInt(request.getParameter("buyModeType"));
		int channel = StringUtil.parstInt(request.getParameter("channel"));
		int formType = StringUtil.parstInt(request.getParameter("formType"));
		int packageType = StringUtil.parstInt(request.getParameter("packageType"));
		String address = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("address")));
		int codPackageType = StringUtil.parstInt(request.getParameter("codPackageType"));
		String codAccount = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("codAccount")));
		String codPassword = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("codPassword")));
		int paidPackageType = StringUtil.parstInt(request.getParameter("paidPackageType"));
		String paidAccount = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("paidAccount")));
		String paidPassword = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("paidPassword")));
		String mail = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("mail")));
		int deliveryRate = StringUtil.parstInt(StringUtil.dealParam(request.getParameter("deliveryRate")));
		int overtimeRate = StringUtil.parstInt(StringUtil.dealParam(request.getParameter("overtimeRate")));
		synchronized (lock) {
			DeliverCorpInfoBean deliver = deliverService.getDeliverCorpInfo(deliverId);
			deliver.setId(deliverId);
			deliver.setName(name);
			deliver.setAddress(address);
			deliver.setChangeable(changeable);
			deliver.setPhone(phone);
			deliver.setWebAddress(webAddress);
			deliver.setBuyModeType(buyModeType);
			deliver.setChannel(channel);
			deliver.setFormType(formType);
			deliver.setPackageType(packageType);
			deliver.setCodPackageType(codPackageType);
			deliver.setCodAccount(codAccount);
			deliver.setCodPassword(codPassword);
			deliver.setPaidPackageType(paidPackageType);
			deliver.setPaidAccount(paidAccount);
			deliver.setPaidPassword(paidPassword);
			deliver.setMail(mail);
			deliver.setLastOperDatetime(DateUtil.getNow());
			deliver.setStatus(status);
			deliver.setDeliveryRate(deliveryRate);
			deliver.setOvertimeRate(overtimeRate);
			//更新快递公司信息
			int count = deliverService.updateDeliverCorpInfo(deliver);
			if(count<=0){
				j.setMsg("更新快递公司信息失败!");
				throw new RuntimeException("更新快递公司信息失败!");
			}
			
			//增加操作日志
			DeliverLog log = new DeliverLog();
			log.setDeliverId(deliverId);
			log.setUserId(user.getId());
			log.setUserName(user.getUsername());
			log.setContent("修改" + deliver.getName() + "快递公司信息!");
			log.setCreateDatetime(DateUtil.getNow());
			log.setType(0);
			count = deliverService.addDeliverLog(log);
			if(count<=0){
				j.setMsg("添加快递公司操作日志失败!");
				throw new RuntimeException("添加快递公司操作日志失败!");
			}
			//更新快递公司与结算公司关系
			DeliverBalanceType deliverBalanceType = deliverService.getDeliverBalanceTypeByDeliverId(deliverId);

			deliverBalanceType.setBalanceTypeId(balanceCorpId);
			count = deliverService.updateDeliverBalanceType(deliverBalanceType);
			if(count<=0){
				j.setMsg("更新结算公司与快递公司关系失败!");
				throw new RuntimeException("更新结算公司与快递公司关系失败!");
			}
			
			//更新快递公司kpi
			//根据快递公司id查询快递公司kpi
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("deliverId", ""+deliverId);
			List<DeliverKpi> kpiList = deliverService.getDeliverKpiList(map);
			HashMap<Integer,DeliverKpi> kpiMap = new HashMap<Integer,DeliverKpi>();
			if(kpiList!=null && kpiList.size()>0){
				for(int i=0;i<kpiList.size();i++){
					kpiMap.put(Integer.valueOf(kpiList.get(i).getAreaId()), kpiList.get(i));
				}
			}
			HashMap<Integer,String> areaMap = ProductStockBean.areaMap;
			for(Integer key : areaMap.keySet()){
				String lastTransitTime = StringUtil.convertNull(request.getParameter("lastTransitTime" + key.toString()));
				String collectTime = StringUtil.convertNull(request.getParameter("collectTime" + key.toString()));
				String arriveTime = StringUtil.convertNull(request.getParameter("arriveTime" + key.toString()));
				String mailingTime = StringUtil.convertNull(request.getParameter("mailingTime" + key.toString()));
				String sendTime = StringUtil.convertNull(request.getParameter("sendTime" + key.toString()));
				if(kpiMap.containsKey(key)){//更新
					DeliverKpi kpi = kpiMap.get(key);
					kpi.setLastestTransitTime(lastTransitTime);
					kpi.setCollectTime(collectTime);
					kpi.setArriveTime(arriveTime);
					kpi.setMailingTime(mailingTime);
					kpi.setSendTime(sendTime);
					count = deliverService.updateDeliverKpi(kpi);
					if(count<=0){
						j.setMsg("更新快递公司kpi指标失败!");
						throw new RuntimeException("更新快递公司kpi指标失败!");
					}
				}else{//新增
					DeliverKpi kpi = new DeliverKpi();
					kpi.setAreaId(key.shortValue());
					kpi.setDeliverId(deliverId);
					kpi.setLastestTransitTime(lastTransitTime);
					kpi.setCollectTime(collectTime);
					kpi.setArriveTime(arriveTime);
					kpi.setMailingTime(mailingTime);
					kpi.setSendTime(sendTime);
					count = deliverService.addDeliverKPI(kpi);
					if(count<=0){
						j.setMsg("添加快递公司kpi指标失败!");
						throw new RuntimeException("添加快递公司kpi指标失败!");
					}
				}
			}
		}
		j.setMsg("更新成功!");
		j.setSuccess(true);
		return j;
	}
	
	/**
	 * 查询快递公司列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/queryDeliver")
	public String queryDeliver(HttpServletRequest request,HttpServletResponse response)throws Exception{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		int deliverId = StringUtil.StringToId(StringUtil.dealParam(request.getParameter("deliverId")));
		int status = StringUtil.parstBackMinus((StringUtil.dealParam(request.getParameter("status"))));
		//拼接查询条件
		HashMap<String,Integer> map = new HashMap<String,Integer>();
		if(deliverId!=0){
			map.put("deliverId", deliverId);
		}
		if(status!=-1){
			map.put("status", status);
		}
		List<DeliverCorpInfoBean> deliverList = deliverService.getDelvierList(map);
		//获取快递公司的配送区域
		if(deliverList!=null && deliverList.size()>0){
			for(int i=0;i<deliverList.size();i++){
				DeliverCorpInfoBean bean = deliverList.get(i);
				StringBuilder deliverArea =  new StringBuilder();
				deliverArea.append("增城仓:");
				String provinces = deliverService.getDeliverProviences("dss.deliver_corp_id="+ bean.getId() +" and dss.stock_area_id=3 and dss.source=1");
				if(provinces!=null && provinces.length()>0){
					deliverArea.append(provinces);
				}
				String cities = deliverService.getDeliverCities("dss.deliver_corp_id="+ bean.getId() +" and dss.stock_area_id=3 and dss.source=2");
				if(cities!=null && cities.length()>0){
					deliverArea.append(",").append(cities);
				}
				deliverArea.append(",无锡仓:");
				provinces = deliverService.getDeliverProviences("dss.deliver_corp_id="+ bean.getId() +" and dss.stock_area_id=4 and dss.source=1");
				if(provinces!=null && provinces.length()>0){
					deliverArea.append(provinces);
				}
				cities = deliverService.getDeliverCities("dss.deliver_corp_id="+ bean.getId() +" and dss.stock_area_id=4 and dss.source=2");
				if(cities!=null && cities.length()>0){
					deliverArea.append(",").append(cities);
				}
				bean.setDeliverArea(deliverArea.toString());
			}
		}
		request.setAttribute("deliverList", deliverList);
		request.setAttribute("status", status);
		
		//查询操作日志
		String[] lastWeek = DateUtil.getLastNaturalWeek();
		StringBuilder condition = new StringBuilder();
		condition.append("create_datetime between '").append(lastWeek[0]).append(" 00:00:00' and '")
		.append(lastWeek[1]).append(" 23:59:59'");
		List<DeliverLog> logList = deliverService.getDeliverLogList(condition.toString());
		request.setAttribute("logList", logList);
		return "/admin/tms/deliverList";
	}
	/**
	 * 查询某个快递公司的操作日志
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/queryDeliverLog")
	public String queryDeliverLog(HttpServletRequest request,HttpServletResponse response)throws Exception{
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录,操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		int deliverId = StringUtil.StringToId(StringUtil.dealParam(request.getParameter("deliverId")));
		int type = StringUtil.StringToId(StringUtil.dealParam(request.getParameter("type")));
		StringBuilder condition = new StringBuilder();
		condition.append("deliver_id=").append(deliverId);
		condition.append(" and type=").append(type);
		List<DeliverLog> logList = deliverService.getDeliverLogList(condition.toString());
		request.setAttribute("logList", logList);
		return "/admin/tms/deliverLog";
	}
}

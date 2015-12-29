package mmb.tms.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.common.dao.CommonDao;
import mmb.easyui.Json;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.tms.model.DeliverAdminUser;
import mmb.tms.model.DeliverAdminUserLog;
import mmb.tms.model.TrunkCorpInfo;
import mmb.tms.model.TrunkEffect;
import mmb.tms.model.TrunkEffectLog;
import mmb.tms.model.TrunkOrder;
import mmb.tms.service.IDeliverAdminUserLogService;
import mmb.tms.service.IDeliverAdminUserService;
import mmb.tms.service.ITrunkEffectLogService;
import mmb.tms.service.ITrunkEffectService;
import mmb.tms.service.ITrunkLineService;
import mmb.tms.service.ITrunkOrderService;
import mmb.util.excel.ExportExcel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

@RequestMapping("TrunkLineController")
@Controller
public class TrunkLineController {
	Lock lock = new ReentrantLock();
	@Autowired
	private ITrunkLineService iTrunkLineService;
	
	@Autowired
	private IDeliverAdminUserService iDeliverAdminUserService;
	
	@Autowired
	private IDeliverAdminUserLogService iDeliverAdminUserLogService;
	
	@Autowired
	private ITrunkEffectService iTrunkEffectService;
	
	@Autowired
	public CommonDao commonMapper;
	
	@Autowired
	public ITrunkEffectLogService iTrunkEffectLogService;
	
	@Autowired
	public ITrunkOrderService trunkOrderService;
	
	/**
	 * @Description: 干线运单查询
	 */
	@RequestMapping(value = "/qryTrunkOrder", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public EasyuiDataGridJson qryTrunkOrder(HttpServletRequest request,HttpServletResponse response,int page,int rows) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		//分页
		map.put("rows", rows);
		map.put("startRow", (page - 1) * rows);
		//查询条件
		map.put("code", request.getParameter("code"));
		map.put("status", request.getParameter("status"));
		map.put("isTimeout", request.getParameter("isTimeout"));
		map.put("trunkCorpId", Integer.valueOf(request.getParameter("trunkCorpId")));
		map.put("deliverAdminUser", Integer.valueOf(request.getParameter("deliverAdminUser")));
		map.put("stockArea", Integer.valueOf(request.getParameter("stockArea")));
		map.put("deliver", Integer.valueOf(request.getParameter("deliver")));
		map.put("transDatetimeStart", request.getParameter("transDatetimeStart"));
		map.put("transDatetimeEnd", request.getParameter("transDatetimeEnd"));
		map.put("finDatetimeStart", request.getParameter("finDatetimeStart"));
		map.put("finDatetimeEnd", request.getParameter("finDatetimeEnd"));
		return trunkOrderService.qryTrunkOrder(map);
	}
	
	/**
	 * @Description: 干线运单详情
	 */
	@RequestMapping(value = "/qryTrunkOrderDetail", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public EasyuiDataGridJson qryTrunkOrderDetail(HttpServletRequest request,
			HttpServletResponse response,int page,int rows,Integer mailingBatchId) throws ServletException, IOException{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		//分页
		map.put("rows", rows);
		map.put("startRow", (page - 1) * rows);
		//查询条件
		map.put("mailingBatchId", mailingBatchId);
		return trunkOrderService.qryMailingBatchPackage(map);
	}
	
	/**
	 * 
	 * @descripion 修改干线单
	 */
	@RequestMapping(value = "/modifyTrunkInfo", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public Json modifyTrunkInfo(HttpServletRequest request,HttpServletResponse response,
			Integer trunkOrderId,String status,Float size,Float weight,Integer mode) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Json json = new Json();
		
		if(size!=null){
			if(!Pattern.compile("([1-9]+[0-9]*|0)(\\.[\\d]{1,2})?").matcher(String.valueOf(size)).matches()){
				json.setSuccess(false);
				json.setMsg("立方数必须为整数或者1到2位小数");
				return json;
			}else if(Float.valueOf(String.valueOf(size))>10000f){
				json.setSuccess(false);
				json.setMsg("立方数不可超过5位数");
				return json;
			}
		}
		if(weight!=null){
			if(!Pattern.compile("([1-9]+[0-9]*|0)(\\.[\\d]{1,2})?").matcher(String.valueOf(weight)).matches()){
				json.setSuccess(false);
				json.setMsg("重量必须为整数或者1到2位小数");
				return json;
			}else if(Float.valueOf(String.valueOf(weight))>10000f){
				json.setSuccess(false);
				json.setMsg("重量不可超过5位数");
				return json;
			}
		}

		Map<String,Object> map = new HashMap<String,Object>();
		map.put("sysOpUser", user.getId());
		map.put("trunkOrderId", trunkOrderId);
		map.put("status", status);
		map.put("size", size);
		map.put("weight", weight);
		map.put("mode", mode);
		return trunkOrderService.modifyTrunkInfo(map);
	}
	
	/**
	 * 
	 * @descripion 导出干线单
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/exportTrunkOrder")
	public void exportTrunkOrder(HttpServletRequest request,HttpServletResponse response) throws Exception{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		//查询条件
		String qryType = request.getParameter("qryType");
		map.put("qryType", qryType);
		if("2".equals(qryType)){
			map.put("trunkOrderIdStr", request.getParameter("trunkOrderIdStr").split(","));
		}else{
			map.put("code", request.getParameter("code1"));
			map.put("status", request.getParameter("status1"));
			map.put("isTimeout", request.getParameter("isTimeout1"));
			map.put("trunkCorpId", Integer.valueOf(request.getParameter("trunkCorpId1")));
			map.put("deliverAdminUser", Integer.valueOf(request.getParameter("deliverAdminUser1")));
			map.put("stockArea", Integer.valueOf(request.getParameter("stockArea1")));
			map.put("deliver", Integer.valueOf(request.getParameter("deliver1")));
			map.put("transDatetimeStart", request.getParameter("transDatetimeStart1"));
			map.put("transDatetimeEnd", request.getParameter("transDatetimeEnd1"));
			map.put("finDatetimeStart", request.getParameter("finDatetimeStart1"));
			map.put("finDatetimeEnd", request.getParameter("finDatetimeEnd1"));
		}
		EasyuiDataGridJson json = trunkOrderService.qryTrunkOrder(map);
		List<TrunkOrder> rsList = json.getRows();
		
		String fileName = DateUtil.getNow().substring(0, 10);
		//设置表头
	    ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		
		header.add("序号");
		header.add("干线单号");
		header.add("订单数量");
		header.add("配送状态");
		header.add("是否超时");
		header.add("干线公司");
		header.add("用户名");
		header.add("发货仓");
		header.add("目的地");
		header.add("交接时间");
		header.add("预计时间");
		header.add("时效H");
		header.add("剩余时间");
		header.add("完成时间");
		header.add("体积m³");
		header.add("重量kg");
		header.add("配送方式");
		
		int size = header.size();
		if (rsList != null && rsList.size() > 0) {
			int x = rsList.size();
			for (int i = 0; i < x; i++) {
				TrunkOrder trunkOrder = rsList.get(i);
				ArrayList<String> tmp = new ArrayList<String>();
				tmp.add(i+1 +"");
				tmp.add(trunkOrder.getCode());
				tmp.add(String.valueOf(trunkOrder.getOrderCount()));
				tmp.add(trunkOrder.getStatusName());
				if("1".equals(trunkOrder.getOverTime())){
					tmp.add("是");
				}else{
					tmp.add("否");
				}
				tmp.add(trunkOrder.getTrunkCorpName());
				tmp.add(trunkOrder.getOpUserName());
				tmp.add(trunkOrder.getStockAreaName());
				tmp.add(trunkOrder.getDeliverName());
				tmp.add(DateUtil.formatTime(trunkOrder.getReceiveTime()));
				tmp.add(DateUtil.formatTime(trunkOrder.getExpectTime()));
				tmp.add(StringUtil.checkNull(String.valueOf(trunkOrder.getTime())));
				tmp.add(trunkOrder.getRemainTime());
				if("5".equals(trunkOrder.getStatus())){
					tmp.add(DateUtil.formatTime(trunkOrder.getNodeTime()));
				}else{
					tmp.add("");
				}
				tmp.add(StringUtil.checkNull(String.valueOf(trunkOrder.getSize())));
				tmp.add(StringUtil.checkNull(String.valueOf(trunkOrder.getWeight())));
				tmp.add(trunkOrder.getModeName());
				bodies.add(tmp);
			}
		}
		headers.add(header);

		/*允许合并列,下标从0开始，即0代表第一列*/
		List<Integer> mayMergeColumn = new ArrayList<Integer>();
		excel.setMayMergeColumn(mayMergeColumn);
		
		/*允许合并行,下标从0开始，即0代表第一行*/
		List<Integer> mayMergeRow = new ArrayList<Integer>();
        excel.setMayMergeRow(mayMergeRow);
        
		/*
		 * 该行为固定写法  （设置该值为导出excel最大列宽 ,下标从1开始）
		 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
		 * 
		 * */
		excel.setColMergeCount(size);
		/*
		 * 设置需要自己设置样式的行，以每个bodies为参照
		 * 具体的样式设置参考 DemoExcel.java中的setStyle方法
		 * 具体可以参照执行后导出的excel样式及DemoExcel中的setStyle方法
		 */
        List<Integer> row  = new ArrayList<Integer>();
        
        /*设置需要自己设置样式的列，以每个bodies为参照*/
        List<Integer> col  = new ArrayList<Integer>();
        
        excel.setRow(row);
        excel.setCol(col);
        
        //调用填充表头方法
        excel.buildListHeader(headers);
        
        //调用填充数据区方法
        excel.buildListBody(bodies);
        //文件输出
        excel.exportToExcel(fileName, response, "");
	}
	
	/**
	 * 
	 * @descripion 干线单日志
	 */
	@RequestMapping(value = "/qryTrunkOrderInfo", method = { RequestMethod.GET,RequestMethod.POST })
	@ResponseBody
	public EasyuiDataGridJson qryTrunkOrderInfo(HttpServletRequest request,
			HttpServletResponse response,int page,int rows,Integer trunkOrderId,
			String trunkOrderCode,String orderCode) throws ServletException, IOException{
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Map<String,Object> map = new HashMap<String,Object>();
		//分页
		map.put("rows", rows);
		map.put("startRow", (page - 1) * rows);
		//查询条件
		map.put("trunkOrderId", trunkOrderId);
		map.put("trunkOrderCode", trunkOrderCode);
		map.put("orderCode", orderCode);
		return trunkOrderService.qryTrunkOrderInfo(map);
	}
	
	/**
	 * @Description: 添加干线公司
	 * @author ahc
	 */
	@RequestMapping("/addTrunk")
	@ResponseBody
	public Json addTrunk(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String trunkName) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json json = new Json();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		try {
			lock.lock(); 
			StringBuffer condition = new StringBuffer();
			condition.append(" and status = 0");
			Map<String,String> map = new HashMap<String,String>();
			map.put("condition",condition.toString());
			List<TrunkCorpInfo> list = iTrunkLineService.getTrunk(map);
			for(TrunkCorpInfo tci:list){
				if(trunkName.equals(tci.getName())){
					json.setMsg("数据已存在！");
					json.setSuccess(false);
					return json;
				}
			}
			condition.append(" and status = 1");
			Map<String,String> map2 = new HashMap<String,String>();
			map.put("condition", condition.toString());
			List<TrunkCorpInfo> list2 = iTrunkLineService.getTrunk(map2);
			for(TrunkCorpInfo tci:list2){
				if(trunkName.equals(tci.getName())){
					json.setMsg("不能添加已删除过数据！");
					json.setSuccess(false);
					return json;
				}
			}
			TrunkCorpInfo t = new TrunkCorpInfo();
			t.setName(trunkName);
			t.setAddTime(DateUtil.getNow());
			t.setStatus(0);
			int result =iTrunkLineService.addTrunk(t);
			if(result>0){
				json.setMsg("添加成功！");
				json.setSuccess(true);
			}else{
				json.setMsg("添加失败！");
			}
		} catch (Exception e) {
			json.setMsg("处理异常！");
			e.printStackTrace();
		} finally{
			lock.unlock(); 
		}
		return json;
	}
	
	/**
	 * @Description: 获取干线公司
	 * @author ahc
	 */
	@RequestMapping("/getTrunkCorpInfo")
	@ResponseBody
	public EasyuiDataGridJson getTrunkCorpInfo(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String trunkName2) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		EasyuiDataGridJson DataGrid = new EasyuiDataGridJson();
		StringBuffer condition = new StringBuffer();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		if (trunkName2!=null && !"请选择".equals(trunkName2)) {
			condition.append(" and name = '"+trunkName2+"' and status = 0");
		}
		condition.append(" and status = 0");
		Map<String,String> map = new HashMap<String,String>();
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		map.put("condition", condition.toString());
		try {
			List<TrunkCorpInfo> list = iTrunkLineService.getTrunk(map);
			int count =iTrunkLineService.getTrunkCount(map);
			DataGrid.setRows(list);
			DataGrid.setTotal((long)count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DataGrid;
	}
	
	/**
	 * @Description: 删除干线公司
	 * @author ahc
	 */
	@RequestMapping("/upDateTrunkCorpInfo")
	@ResponseBody
	public Json upDateTrunkCorpInfo(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String id) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json json = new Json();
		StringBuffer condition = new StringBuffer();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Map<String,String> map = new HashMap<String,String>();
		if (id!=null && !"".equals(id)) {
			map.put("status", " 1");
			map.put("id", id);
		}
		try {
			lock.lock(); 
			int result = iTrunkLineService.upDateTrunkCorpInfo(map);
			if(result>0){
				json.setMsg("添加成功！");
				json.setSuccess(true);
			}else{
				json.setMsg("添加失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			lock.unlock();
		}
		return json;
	}
	
	/**
	 * @Description: 导入干线时效excel
	 * @author ahc
	 */
	@RequestMapping("/uploadTrunkEffect")
	public String uploadTrunkEffect(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String id) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		iTrunkLineService.uploadTrunkEffectExcel(user,request);
		return "admin/orderStock/uploadTrunkEffect";
	}
	
	/**
	 * @Description: 下载干线时效excel
	 * @author ahc
	 */
	@RequestMapping("/downloadTrunkEffect")
	public void downloadTrunkEffect(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		iTrunkLineService.downloadTrunkEffectExcel(user,request,response);
	}
	
	/**
	 * @Description: 添加干线用户
	 * @author ahc
	 */
	@RequestMapping("/addTrunkUser")
	@ResponseBody
	public Json addTrunkUser(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String trunkEffectId,String trunkEffectName,String username,String password,String phone) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json json = new Json();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		try {
			lock.lock(); 
			if ("-1".equals(trunkEffectId)) {
				json.setMsg("请选择干线公司！");
				json.setSuccess(false);
				return json;
			}
			StringBuffer condition = new StringBuffer();
			condition.append(" ");
			Map<String,String> map = new HashMap<String,String>();
			map.put("condition",condition.toString());
			List<Map<String,String>> list = iDeliverAdminUserService.getDeliverAdminUser(map);
			for(Map<String,String> dau:list){
				if(trunkEffectName.equals(dau.get("name")+"") && username.equals(dau.get("username")+"") && "1".equals(String.valueOf(dau.get("type")))){
					json.setMsg("数据已存在！");
					json.setSuccess(false);
					return json;
				}
			}
			
			DeliverAdminUser deliverAdminUser = new DeliverAdminUser();
			deliverAdminUser.setUsername(username);
			deliverAdminUser.setPassword(password);
			deliverAdminUser.setPhone(phone);
			deliverAdminUser.setCreateDatetime(DateUtil.getNow());
			deliverAdminUser.setLastModifyDatetime(DateUtil.getNow());
			byte a =1;
			byte b =0;
			deliverAdminUser.setStatus(a);
			deliverAdminUser.setFlag(b);
			deliverAdminUser.setName("干线用户");
			deliverAdminUser.setDeliverId(Integer.parseInt(trunkEffectId));
			deliverAdminUser.setType(1);
			deliverAdminUser.setPvLimit(999999);
			int result =iDeliverAdminUserService.addDeliverAdminUser(deliverAdminUser);
			if(result>0){
				json.setMsg("添加成功！");
				json.setSuccess(true);
				
				DeliverAdminUserLog log = new DeliverAdminUserLog();
				log.setOperationUserId(user.getId());
				log.setOperationUserName(user.getUsername());
				log.setDeliverAdminId(result);
				log.setTrunkId(Integer.parseInt(trunkEffectId));
				log.setPassword(password);
				log.setPhone(phone);
				log.setAddTime(DateUtil.getNow());
				log.setType(1);
				int result2 =iDeliverAdminUserLogService.AddDeliverAdminUserLog(log);
				if(result2<0){
					json.setMsg("添加干线用户日志失败！");
				}
			}else{
				json.setMsg("添加失败！");
			}
		} catch (Exception e) {
			json.setMsg("处理异常！");
			e.printStackTrace();
		} finally{
			lock.unlock(); 
		}
		return json;
	}
	
	/**
	 * @Description: 修改干线用户
	 * @author ahc
	 */
	@RequestMapping("/updateTrunkUser")
	@ResponseBody
	public Json updateTrunkUser(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String trunkEffectId,String username,String password,String phone,String trunkId) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json json = new Json();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		try {
			lock.lock(); 
			if ("".equals(username.trim())) {
				json.setMsg("用户名不能为空！");
				return json;
			}
			if ("".equals(password.trim())) {
				json.setMsg("密码不能为空！");
				return json;
			}
			StringBuffer condition = new StringBuffer();
			if (!"".equals(trunkId.trim())) {
				condition.append(" and dau.deliver_id ="+trunkId);
			}
			if (!"".equals(username.trim())) {
				condition.append(" and dau.username = '"+username+"'");
			}
			if (!"".equals(phone.trim())) {
				//condition.append(" and dau.phone = '"+phone+"'");
			}
			
			condition.append(" and dau.type=1 and dau.status =1");
			Map<String,String> map = new HashMap<String,String>();
			map.put("condition", condition.toString());
			//List<Map<String,String>> list = iDeliverAdminUserService.getDeliverAdminUser(map);
			//if(list.size()>0){
			//	json.setMsg("数据已存在！");
			//	json.setSuccess(false);
			//	return json;
			//}
			
			StringBuffer set = new StringBuffer();
			StringBuffer id = new StringBuffer();
			set.append(" set username='"+username+"' ,password ='"+password+"' ,phone ='"+phone+"' ,last_modify_datetime='"+DateUtil.getNow()+"'");
			id.append(trunkEffectId);
			map.put("set",set.toString());
			map.put("id",id.toString());
			int result =iDeliverAdminUserService.updateDeliverAdminUser(map);
			if(result>0){
				json.setMsg("添加成功！");
				json.setSuccess(true);
				
				DeliverAdminUserLog log = new DeliverAdminUserLog();
				log.setOperationUserId(user.getId());
				log.setOperationUserName(user.getUsername());
				log.setDeliverAdminId(Integer.parseInt(trunkEffectId));
				log.setTrunkId(Integer.parseInt(trunkId));
				log.setPassword(password);
				log.setPhone(phone);
				log.setAddTime(DateUtil.getNow());
				log.setType(1);
				int result2 =iDeliverAdminUserLogService.AddDeliverAdminUserLog(log);
				if(result2<0){
					json.setMsg("添加干线用户日志失败！");
				}
			}else{
				json.setMsg("添加失败！");
			}
		} catch (Exception e) {
			json.setMsg("处理异常！");
			e.printStackTrace();
		} finally{
			lock.unlock(); 
		}
		return json;
	}
	
	/**
	 * @Description: 删除干线用户
	 * @author ahc
	 */
	@RequestMapping("/delTrunkUser")
	@ResponseBody
	public Json delTrunkUser(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String id) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json json = new Json();
		StringBuffer condition = new StringBuffer();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Map<String,String> map = new HashMap<String,String>();
		if (id!=null && !"".equals(id)) {
			map.put("set"," set status='0'");
			map.put("id", id);
		}
		try {
			lock.lock(); 
			int result = iDeliverAdminUserService.updateDeliverAdminUser(map);
			if(result>0){
				json.setMsg("添加成功！");
				json.setSuccess(true);
			}else{
				json.setMsg("添加失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			lock.unlock();
		}
		return json;
	}
	
	/**
	 * @Description: 获取干线用户
	 * @author ahc
	 */
	@RequestMapping("/getTrunkUser")
	@ResponseBody
	public EasyuiDataGridJson getTrunkUser(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String trunkName2,String username2,String phone2) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		EasyuiDataGridJson DataGrid = new EasyuiDataGridJson();
		StringBuffer condition = new StringBuffer();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		if (trunkName2!=null && !"请选择".equals(trunkName2) && !"-1".equals(trunkName2)) {
			condition.append(" and deliver_id ="+trunkName2);
		}
		if (username2!=null && !"请选择".equals(username2) && !"".equals(username2)) {
			condition.append(" and username = '"+username2+"'");
		}
		if (phone2!=null && !"".equals(phone2)) {
			condition.append(" and phone = '"+phone2+"'");
		}
		
		condition.append(" and dau.type=1 and dau.`status` =1");
		Map<String,String> map = new HashMap<String,String>();
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		map.put("order", " tci.name");
		map.put("condition", condition.toString());
		
		try {
			List<Map<String,String>> list = iDeliverAdminUserService.getDeliverAdminUser(map);
			for(Map<String,String> m :list){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String createDatetime =sdf.format(m.get("createDatetime"));
				m.put("createDatetime", createDatetime);
				
				String lastModifyDatetime =sdf.format(m.get("lastModifyDatetime"));
				m.put("lastModifyDatetime", lastModifyDatetime);
				
			}
			int count =iDeliverAdminUserService.getDeliverAdminUserCount(map);
			DataGrid.setRows(list);
			DataGrid.setTotal((long)count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DataGrid;
	}
	
	/**
	 * @Description: 获取干线用户日志
	 * @author ahc
	 */
	@RequestMapping("/getTrunkUserLog")
	@ResponseBody
	public EasyuiDataGridJson getTrunkUserLog(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String TrunkId,String username) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		EasyuiDataGridJson DataGrid = new EasyuiDataGridJson();
		StringBuffer condition = new StringBuffer();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		if (TrunkId!=null && !"".equals(TrunkId.trim()) && !"-1".equals(TrunkId)) {
			condition.append(" and tci.id = "+TrunkId+" ");
		}
		
		if (username!=null && !"".equals(username.trim()) && !"请选择".equals(username)) {
			condition.append(" and dau.username='"+username+"'");
		}
		
		if(TrunkId==null && username==null || "-1".equals(TrunkId) && "请选择".equals(username)){
			List list = new ArrayList();
			DataGrid.setRows(list);
			DataGrid.setTotal((long)0);
			return DataGrid;
		}
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		map.put("order", " daul.add_time DESC");
		map.put("condition", condition.toString());
		
		try {
			List<Map<String,String>> list = iDeliverAdminUserLogService.getDeliverAdminUserLog(map);
			for(Map<String,String> m : list){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String addTime =sdf.format(m.get("addTime"));
				m.put("addTime", addTime);
			}
			int count =iDeliverAdminUserLogService.getDeliverAdminUserCount(map);
			DataGrid.setRows(list);
			DataGrid.setTotal((long)count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DataGrid;
	}
	
	/**
	 * @Description: 添加路线关系
	 * @author ahc
	 */
	@RequestMapping("/addTrunkEffect")
	@ResponseBody
	public Json addTrunkEffect(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String trunkId,String deliverAdminId,String stockAreaId,String deliverId) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json json = new Json();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		try {
			lock.lock(); 
			if ("-1".equals(trunkId)) {
				json.setMsg("请选择干线公司");
				json.setSuccess(false);
				return json;
			}
			if ("-1".equals(deliverAdminId)) {
				json.setMsg("请选择用户名");
				json.setSuccess(false);
				return json;
			}
			if ("-1".equals(stockAreaId)) {
				json.setMsg("请选择发货仓");
				json.setSuccess(false);
				return json;
			}
			if ("-1".equals(deliverId)) {
				json.setMsg("请选择目的地");
				json.setSuccess(false);
				return json;
			}
			StringBuffer condition = new StringBuffer();
			StringBuffer condition2 = new StringBuffer();
			StringBuffer condition3 = new StringBuffer();
			if (trunkId!=null && !"".equals(trunkId.trim()) && !"-1".equals(trunkId)) {
				condition.append(" and te.trunk_id = "+trunkId+" ");
				condition3.append(" and trunk_id = "+trunkId+" ");
			}
			if (deliverAdminId!=null && !"".equals(deliverAdminId.trim()) && !"-1".equals(deliverAdminId)) {
				condition.append(" and te.deliver_admin_id = "+deliverAdminId+" ");
				condition3.append(" and deliver_admin_id = "+deliverAdminId+" ");
			}
			if (stockAreaId!=null && !"".equals(stockAreaId.trim()) && !"-1".equals(stockAreaId)) {
				condition.append(" and te.stock_area_id = "+stockAreaId+" ");
				condition2.append(" and stock_area_id = "+stockAreaId+" ");
				
			}
			if (deliverId!=null && !"".equals(deliverId.trim()) && !"-1".equals(deliverId)) {
				condition.append(" and te.deliver_id = "+deliverId+" ");
				condition2.append(" and deliver_id = "+deliverId+" ");
			}
			Map<String,String> map = new HashMap<String,String>();
			map.put("condition",condition.toString());
			List<Map<String,String>> list = iTrunkEffectService.getTrunkEffectForLineList(map);
			if(list.size()>0){
				json.setMsg("数据已存在！");
				json.setSuccess(false);
				return json;
			}
			Map<String,String> map2 = new HashMap<String,String>();
			map2.put("condition",condition2.toString());
			List<Map<String,String>> list2 = iTrunkEffectService.getTrunkEffectByAreaAndDeliver(map2);
			if(list2.size()>0){
				json.setMsg("线路已存在！");
				json.setSuccess(false);
				return json;
			}
			TrunkEffect trunkEffect = new TrunkEffect();
			trunkEffect.setTrunkId(Integer.parseInt(trunkId));
			trunkEffect.setDeliverAdminId(Integer.parseInt(deliverAdminId));
			trunkEffect.setStockAreaId(Integer.parseInt(stockAreaId));
			trunkEffect.setDeliverId(Integer.parseInt(deliverId));
			trunkEffect.setAddTime(DateUtil.getNow());
			trunkEffect.setStatus(0);
			int result =iTrunkEffectService.addTrunkEffect(trunkEffect);
			if(result>0){
				json.setMsg("添加成功！");
				json.setSuccess(true);
				TrunkEffectLog log = new TrunkEffectLog();
				log.setOperationUserId(user.getId());
				log.setOperationUserName(user.getUsername());
				log.setTrunkId(Integer.parseInt(trunkId));
				log.setDeliverAdminId(Integer.parseInt(deliverAdminId));
				log.setStockArea(Integer.parseInt(stockAreaId));
				log.setDeliverId(Integer.parseInt(deliverId));
				log.setAddTime(DateUtil.getNow());
				int result2 =iTrunkEffectLogService.addTrunkEffectLog(log);
				if(result2<0){
					json.setMsg("添加干线日志失败！");
				}
				
			}else{
				json.setMsg("添加失败！");
			}
		} catch (Exception e) {
			json.setMsg("处理异常！");
			e.printStackTrace();
		} finally{
			lock.unlock(); 
		}
		return json;
	}
	
	/**
	 * @Description: 获取路线关系
	 * @author ahc
	 */
	@RequestMapping("/getTrunkEffectForLine")
	@ResponseBody
	public EasyuiDataGridJson getTrunkEffect(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String trunkId,String deliverAdminId,String stockAreaId,String deliverId) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		EasyuiDataGridJson DataGrid = new EasyuiDataGridJson();
		StringBuffer condition = new StringBuffer();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		if (trunkId!=null && !"".equals(trunkId.trim()) && !"-1".equals(trunkId)) {
			condition.append(" and te.trunk_id = "+trunkId+" ");
		}
		
		if (deliverAdminId!=null && !"".equals(deliverAdminId.trim()) && !"-1".equals(deliverAdminId)) {
			condition.append(" and te.deliver_admin_id='"+deliverAdminId+"'");
		}
		
		if (stockAreaId!=null && !"".equals(stockAreaId.trim()) && !"-1".equals(stockAreaId)) {
			condition.append(" and te.stock_area_id='"+stockAreaId+"'");
		}
		
		if (deliverId!=null && !"".equals(deliverId.trim()) && !"-1".equals(deliverId)) {
			condition.append(" and te.deliver_id='"+deliverId+"'");
		}
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		map.put("order", " tci.name");
		map.put("condition", condition.toString());
		
		try {
			List<Map<String,String>> list = iTrunkEffectService.getTrunkEffectForLineList(map);
			int count =iTrunkEffectService.getTrunkEffectCount(map);
			DataGrid.setRows(list);
			DataGrid.setTotal((long)count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DataGrid;
	}
	
	/**
	 * @Description: 删除线路关系
	 * @author ahc
	 */
	@RequestMapping("/delTrunkEffect")
	@ResponseBody
	public Json delTrunkEffect(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String id) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json json = new Json();
		StringBuffer condition = new StringBuffer();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Map<String,String> map = new HashMap<String,String>();
		if (id!=null && !"".equals(id)) {
			map.put("set"," set status='1'");
			map.put("id", id);
		}
		try {
			lock.lock(); 
			int result = iTrunkEffectService.updateTrunkEffect(map);
			if(result>0){
				json.setMsg("添加成功！");
				json.setSuccess(true);
			}else{
				json.setMsg("添加失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			lock.unlock();
		}
		return json;
	}
	
	/**
	 * @Description: 获取干线时效列表
	 * @author ahc
	 */
	@RequestMapping("/getTrunkEffectForTime")
	@ResponseBody
	public EasyuiDataGridJson getTrunkEffectList(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String trunkId,String deliverAdminId,String stockAreaId,String deliverId,String mode,String time) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		EasyuiDataGridJson DataGrid = new EasyuiDataGridJson();
		StringBuffer condition = new StringBuffer();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		if (trunkId!=null && !"请选择".equals(trunkId) && !"-1".equals(trunkId)) {
			condition.append(" and te.`trunk_id` ="+trunkId);
		}
		if (deliverAdminId!=null && !"请选择".equals(deliverAdminId) && !"-1".equals(deliverAdminId) && !"".equals(deliverAdminId)) {
			condition.append(" and te.deliver_admin_id = '"+deliverAdminId+"'");
		}
		if (stockAreaId!=null && !"请选择".equals(stockAreaId) && !"-1".equals(stockAreaId) && !"".equals(stockAreaId)) {
			condition.append(" and te.stock_area_id = '"+stockAreaId+"'");
		}
		if (deliverId!=null && !"请选择".equals(deliverId) && !"-1".equals(deliverId) && !"".equals(deliverId)) {
			condition.append(" and te.deliver_id = '"+deliverId+"'");
		}
		if (mode!=null && !"请选择".equals(mode) && !"-1".equals(mode) && !"".equals(mode)) {
			condition.append(" and te.mode = '"+mode+"'");
		}
		if (time!=null && !"请选择".equals(StringUtil.checkNull(time)) && !"-1".equals(StringUtil.checkNull(time)) && !"".equals(StringUtil.checkNull(time))) {
			condition.append(" and te.time = '"+time+"'");
		}
		
		condition.append(" and sa.type = 1 and tci.`status` = 0 and dau.`status` = 1 and te.status = 0");
		Map<String,String> map = new HashMap<String,String>();
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		map.put("order", " tci.name");
		map.put("condition", condition.toString());
		
		try {
			List<Map<String,String>> list = iTrunkEffectService.getTrunkEffectList(map);
			for(Map<String,String> map2 :list){
				String mode2 =map2.get("mode");
				if("1".equals(mode2)){
					map2.put("mode", "公路");
				}
				if("2".equals(mode2)){
					map2.put("mode", "铁路");
				}
				if("3".equals(mode2)){
					map2.put("mode", "空运");
				}
			}
			int count =iTrunkEffectService.getTrunkEffectCount(map);
			DataGrid.setRows(list);
			DataGrid.setTotal((long)count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DataGrid;
	}
	
	/**
	 * @Description: 修改干线时效
	 * @author ahc
	 */
	@RequestMapping("/updateTrunkEffect")
	@ResponseBody
	public Json updateTrunkEffect(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String trunkEffectId,String trunkId,String deliverAdminId,String trunkName,String username,String stockAreaId,String deliverId,String mode,String time) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json json = new Json();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		try {
			lock.lock(); 
			if ("-1".equals(mode.trim())) {
				json.setMsg("请选择配送方式！");
				return json;
			}
			if ("".equals(time.trim())) {
				json.setMsg("请选择时效！");
				return json;
			}
			
			if(!"0".equals(trunkId) && !"0".equals(deliverId) && !"0".equals(stockAreaId) && !"0".equals(username)){
				Map<String,String> paramMap = new HashMap<String,String>();
				paramMap.put("column", "id");
				paramMap.put("table", "trunk_effect");
				paramMap.put("condition", " trunk_id ="+trunkId+" and deliver_admin_id = "+deliverAdminId+" and stock_area_id="+stockAreaId+" and deliver_id="+deliverId+" and mode="+mode+" and time="+time+" and status =0");
				List r =commonMapper.getCommonInfo(paramMap);
				if(r.size()>0){
					json.setMsg("数据已存在！");
					json.setSuccess(false);
					return json;
				}
				
			}
			
			Map<String,String> map = new HashMap<String,String>();
			map.put("table", " trunk_effect");
			map.put("set", " stock_area_id='"+stockAreaId+"' ,deliver_id ='"+deliverId+"' ,mode ='"+mode+"' ,time ='"+time+"'");
			map.put("condition", " id="+trunkEffectId);
			int result =commonMapper.updateCommon(map);
			if(result>0){
				json.setMsg("添加成功！");
				json.setSuccess(true);
				
				TrunkEffectLog log = new TrunkEffectLog();
				log.setOperationUserId(user.getId());
				log.setOperationUserName(user.getUsername());
				log.setTrunkId(Integer.parseInt(trunkId));
				log.setDeliverAdminId(Integer.parseInt(deliverAdminId));
				log.setStockArea(Integer.parseInt(stockAreaId));
				log.setDeliverId(Integer.parseInt(deliverId));
				log.setAddTime(DateUtil.getNow());
				log.setMode(Integer.parseInt(mode));
				log.setTime(Integer.parseInt(time));
				int result2 =iTrunkEffectLogService.addTrunkEffectLog(log);
				if(result2<0){
					json.setMsg("添加干线日志失败！");
				}
			}else{
				json.setMsg("添加失败！");
			}
			
		} catch (Exception e) {
			json.setMsg("处理异常！");
			e.printStackTrace();
		} finally{
			lock.unlock(); 
		}
		return json;
	}
	
	/**
	 * @Description: 删除干线时效
	 * @author ahc
	 */
	@RequestMapping("/delTrunkEffectForTime")
	@ResponseBody
	public Json delTrunkEffectForTime(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String id) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		Json json = new Json();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		Map<String,String> map = new HashMap<String,String>();
		map.put("table", " trunk_effect");
		map.put("set", " status='1'");
		map.put("condition", " id="+id);
		try {
			lock.lock(); 
			int result =commonMapper.updateCommon(map);
			if(result>0){
				json.setMsg("添加成功！");
				json.setSuccess(true);
			}else{
				json.setMsg("添加失败！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			lock.unlock();
		}
		return json;
	}
	
	/**
	 * @Description: 获取干线时效日志
	 * @author ahc
	 */
	@RequestMapping("/getTrunkEffectLog")
	@ResponseBody
	public EasyuiDataGridJson getTrunkEffectLog(EasyuiDataGrid page,HttpServletRequest request,HttpServletResponse response,
			String TrunkId,String deliverAdminId,String stockAreaId) throws ServletException, IOException {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		EasyuiDataGridJson DataGrid = new EasyuiDataGridJson();
		StringBuffer condition = new StringBuffer();
		if(user == null){
			request.setAttribute("msg", "当前没有登录，操作失败！");
			request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
			return null;
		}
		if (TrunkId!=null && !"".equals(TrunkId.trim()) && !"-1".equals(TrunkId)) {
			condition.append(" and tel.trunk_id = "+TrunkId+" ");
		}
		
		if (deliverAdminId!=null && !"".equals(deliverAdminId.trim()) && !"-1".equals(deliverAdminId)) {
			condition.append(" and tel.deliver_admin_id='"+deliverAdminId+"'");
		}
		
		if (stockAreaId!=null && !"".equals(stockAreaId.trim()) && !"-1".equals(stockAreaId)) {
			condition.append(" and tel.stock_area='"+stockAreaId+"'");
		}
		
		if(TrunkId==null && deliverAdminId==null && stockAreaId==null || "-1".equals(TrunkId) && "请选择".equals(deliverAdminId) && "请选择".equals(stockAreaId)){
			List list = new ArrayList();
			DataGrid.setRows(list);
			DataGrid.setTotal((long)0);
			return DataGrid;
		}
		
		Map<String,String> map = new HashMap<String,String>();
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		map.put("order", " tel.add_time DESC");
		map.put("condition", condition.toString());
		
		try {
			List<Map<String,String>> list = iTrunkEffectLogService.getTrunkEffectLog(map);
			for(Map<String,String> m : list){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String addTime =sdf.format(m.get("addTime"));
				m.put("addTime", addTime);
			}
			int count =iTrunkEffectLogService.getTrunkEffectLogCount(map);
			DataGrid.setRows(list);
			DataGrid.setTotal((long)count);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return DataGrid;
	}
	
	
	
}

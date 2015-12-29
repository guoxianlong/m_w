package mmb.tp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.common.dao.CommonDao;
import mmb.hessian.ware.DeliverOrderInfoBean;
import mmb.rec.sys.easyui.EasyuiDataGrid;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;
import mmb.tp.bean.DeliverAdminUser;
import mmb.util.excel.ExportExcel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

@Service
public class TPservice {
	public DeliverAdminUser getDeliverAdminUser(String username) {
		DeliverAdminUser vo = null;
		try {
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", " concat(dau.id,'') id,dau.username username,date_format(dau.last_search_time,'%Y-%m-%d %T') lastSearchtime,dau.password pwd,date_format(dau.create_datetime,'%Y-%m-%d %T') createDatetime,date_format(dau.last_modify_datetime,'%Y-%m-%d %T') lastModifyDatetime,"
					+ "concat(dau.flag,'') flag,dau.name name,concat(dau.deliver_id,'') deliverId,dau.phone phone, concat(dau.pv_limit,'') pvlimit, dci.name deliverName, concat(dau.status,'') status ");
			paramMap.put("table", " deliver_admin_user dau join deliver_corp_info dci on dau.deliver_id = dci.id ");
			paramMap.put("condition", " dau.username = '" + StringUtil.toSql(username) + "' ");
			List<HashMap<String, String>> userList = commonMapper.getCommonInfo(paramMap);
			if (userList != null && userList.size() > 0 && userList.get(0) != null) {
				vo = new DeliverAdminUser();
				HashMap<String, String> map = userList.get(0);
				vo.setId(StringUtil.toInt(map.get("id")));
				vo.setUsername(map.get("username"));
				vo.setPassword(map.get("pwd"));
				vo.setCreateDatetime(map.get("createDatetime"));
				vo.setLastModifyDatetime(map.get("lastModifyDatetime"));
				vo.setFlag(StringUtil.toInt(map.get("flag")));
				vo.setName(map.get("name"));
				vo.setDeliverId(StringUtil.toInt(map.get("deliverId")));
				vo.setPvLimit(StringUtil.toInt(map.get("pvlimit")));
				vo.setDeliverName(map.get("deliverName"));
				vo.setPhone(map.get("phone"));
				vo.setStatus(StringUtil.toInt(map.get("status")));
				vo.setLastSearchtime(map.get("lastSearchtime"));
			}

		} catch (Exception sqle) {
			sqle.printStackTrace();
		}
		return vo;
	}

	// 获取第三方用户列表
	@Autowired
	public CommonDao commonMapper;

	public Json getdAdminUserInfo(HttpServletRequest request) {
		Json j = new Json();
		try {
			voUser user = (voUser) request.getSession(false).getAttribute("userView");
			if (user == null) {
				j.setMsg("当前没有登录,操作失败！");
				return j;
			}
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(3003)) {
				j.setMsg("没有权限！");
				return j;
			}
			String deliver = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("deliver")));
			String userCode = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("userCode")));
			String phone = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("phone")));
			String startDate = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startDate")));
			String endDate = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endDate")));
			StringBuffer condition = new StringBuffer();
			condition.append("1=1  and a.type is null ");
			if (!"-1".equals(deliver) && !"".equals(deliver)) {
				condition.append(" and a.deliver_id=" + deliver);
			}
			if (userCode != "") {
				condition.append(" and a.username='" + userCode + "'");
			}
			if (phone != "") {
				condition.append(" and a.phone='" + phone + "'");
			}
			if (startDate != "" && endDate != "") {
				condition.append(" and a.create_datetime between' " + startDate + " 00:00:00" + "' AND '" + endDate + " 23:59:59'");
			}
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "a.id,b.id deliverId,a.status,b.name,a.username,a.phone,a.pv_limit,a.all_search_count,ROUND(a.all_search_count/((TIMESTAMPDIFF(day,a.create_datetime,now()))+1),0) ave,a.current_search_count,date_format(a.create_datetime,'%Y-%m-%d %T') createDatetime ,date_format(a.last_modify_datetime,'%Y-%m-%d %T') lastModifyDate");
			paramMap.put("table", " deliver_admin_user a left join deliver_corp_info b on a.deliver_id=b.id ");
			paramMap.put("condition", condition.toString());
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			List<HashMap<String, String>> listRows = commonMapper.getCommonInfo(paramMap);
			easyuiDataGridJson.setRows(listRows);
			j.setSuccess(true);
			j.setObj(easyuiDataGridJson);
			return j;
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
	}

	// 添加第三方账号信息
	public Json addAdminUser(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession(false).getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(3004)) {
			j.setMsg("没有权限！");
			return j;
		}
		String username = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("username")));
		String pwd = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("pwd")));
		String deliverId = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("deliverId")));
		String pvLimit = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("pvLimit")));
		String userphone = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("userphone")));
		String status = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("status")));
		HashMap<String, String> paramMap = new HashMap<String, String>();
		StringBuffer condition = new StringBuffer();
		try {
			DeliverAdminUser userBean = getDeliverAdminUser(username);
			if (userBean != null) {
				j.setMsg("用户名已经存在!");
				return j;
			} else if (username == "") {
				j.setMsg("用户名不能为空!");
				return j;
			} else if (pwd == null || pwd.length() < 6) {
				j.setMsg("新密码至少6位!");
				return j;
			} else if ("-1".equals(deliverId) || "".equals(deliverId)) {
				j.setMsg("请选择快递公司!");
				return j;
			} else if (pvLimit == "") {
				j.setMsg("请输入日访问量限制!");
				return j;
			} else {
				condition.append("'" + username + "',");
				condition.append("'" + mmb.util.Secure.encryptPwd(pwd) + "',");
				condition.append("'" + DateUtil.getNow() + "',");
				condition.append("'" + DateUtil.getNow() + "',");
				if (status != "" && status.equals("0")) {
					condition.append("0,");
				} else {
					condition.append("1,");
				}
				condition.append(deliverId + ",");
				condition.append("'" + userphone + "',");
				condition.append(pvLimit + ",");
				condition.append("0,");
				condition.append("0");
				paramMap.put("set", "(" + condition.toString() + ")");
				paramMap.put("table", " deliver_admin_user(username,password,create_datetime,last_modify_datetime,status,deliver_id,phone,pv_limit,current_search_count,all_search_count)");
				if (commonMapper.insertCommon(paramMap) == 1) {
					j.setMsg("添加成功!");
					j.setSuccess(true);
					return j;
				} else {
					j.setMsg("添加失败!");
					return j;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
	}

	// 修改用户状态
	public Json changeUserStatus(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession(false).getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(3004)) {
			j.setMsg("没有权限！");
			return j;
		}
		String id = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("id")));
		String status = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("status")));
		HashMap<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sbr = new StringBuffer();
		try {
			if (id == "") {
				j.setMsg("ID不能为空!");
				return j;
			} else {
				if (status != "" && status.equals("0")) {
					sbr.append("status=1,");
				} else {
					sbr.append("status=0,");
				}
				sbr.append("last_modify_datetime='" + DateUtil.getNow() + "'");
				paramMap.put("condition", "id=" + id);
				paramMap.put("set", sbr.toString());
				paramMap.put("table", "deliver_admin_user");
				if (commonMapper.updateCommon(paramMap) == 1) {
					j.setMsg("修改成功!");
					j.setSuccess(true);
					return j;
				} else {
					j.setMsg("修改失败!");
					return j;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
	}

	//更新用户信息
	public Json updataUserInfo(HttpServletRequest request) {
		Json j = new Json();
		voUser user = (voUser) request.getSession(false).getAttribute("userView");
		if (user == null) {
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		UserGroupBean group = user.getGroup();
		if (!group.isFlag(3004)) {
			j.setMsg("没有权限！");
			return j;
		}
		String id = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("id")));
		String pvLimit = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("pvLimit")));
		String phone = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("phone")));
		String deliver = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("deliver")));
		HashMap<String, String> paramMap = new HashMap<String, String>();
		StringBuffer sbr = new StringBuffer();
		try {
			if (id == "") {
				j.setMsg("ID不能为空!");
				return j;
			} else {
				if ("-1".equals(deliver) || "".equals(deliver)) {
					j.setMsg("请选择快递公司!");
					return j;
				}else if (pvLimit == "") {
					j.setMsg("请输入日访问量限制!");
					return j;
				}
				sbr.append("deliver_id="+deliver+",");
				sbr.append("pv_limit="+pvLimit+",");
				sbr.append("phone='"+phone+"',");
				sbr.append("last_modify_datetime='" + DateUtil.getNow() + "'");
				paramMap.put("condition", "id=" + id);
				paramMap.put("set", sbr.toString());
				paramMap.put("table", "deliver_admin_user");
				if (commonMapper.updateCommon(paramMap) == 1) {
					j.setMsg("修改成功!");
					j.setSuccess(true);
					return j;
				} else {
					j.setMsg("修改失败!");
					return j;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
	}
	// 统计监控
	public Json getStaticInfo(HttpServletRequest request, EasyuiDataGrid easyUiPage) {
		Json j = new Json();
		try {
			voUser user = (voUser) request.getSession(false).getAttribute("userView");
			String userId = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("userId")));
			String deliver = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("deliver")));
			String startDate = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startDate")));
			String endDate = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endDate")));
			if (user == null) {
				j.setMsg("当前没有登录,操作失败！");
				return j;
			}
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(3003)) {
				j.setMsg("没有权限！");
				return j;
			}
			StringBuffer condition = new StringBuffer();
			condition.append("1=1");
			if (!"".equals(userId)) {
				condition.append(" and a.user_id=" + userId);
			}
			if (!"-1".equals(deliver) && !"".equals(deliver)) {
				condition.append(" and b.deliver_id=" + deliver);
			}
			if (startDate != "" && endDate != "") {
				condition.append(" and a.create_datetime between' " + startDate + " 00:00:00" + "' AND '" + endDate + " 23:59:59'");
			}
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "d.name orderTypeName,c.name deliverName,b.username userName,date_format(a.create_datetime,'%Y-%m-%d %T') createDateTime,a.ip_address ip,a.package_code packageCode,a.order_code orderCode,a.order_type orderType,concat(a.buy_mode,'') buyMode,a.dprice dprice,concat(a.order_status,'') orderStatus");
			paramMap.put("table", " deliver_info_search_log a left join deliver_admin_user b on a.user_id=b.id left join deliver_corp_info c on b.deliver_id=c.id left join user_order_package_type d on a.order_type=d.type_id");
			paramMap.put("condition", condition.toString() + " limit " + (easyUiPage.getPage() - 1) * easyUiPage.getRows() + "," + easyUiPage.getRows());
			EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
			List<HashMap<String, String>> listRows = commonMapper.getCommonInfo(paramMap);
			if (listRows != null) {
				for(int i=0;i<listRows.size();i++){
					//取得付款方式中文
					HashMap<String, String> map = (HashMap<String, String>)listRows.get(i);
					if(map.get("buyMode")!=null){
						if(voOrder.buyModeMap.get(map.get("buyMode"))!=null){
							map.put("buyModeName", voOrder.buyModeMap.get(map.get("buyMode")).toString());
						}
					}
					//取得投递状态中文
					if(map.get("orderStatus")!=null){
						if(DeliverOrderInfoBean.deliverStateMap.get(StringUtil.StringToId(map.get("orderStatus")))!=null){
							map.put("orderStatusName", DeliverOrderInfoBean.deliverStateMap.get(StringUtil.StringToId(map.get("orderStatus"))));
						}
					}
				}
			}
			easyuiDataGridJson.setRows(listRows);
			paramMap.clear();
			//计算数据总数
			paramMap.put("column", "c.name deliverName,b.username userName,a.create_datetime createDateTime,a.ip_address ip,a.package_code packageCode,a.order_code orderCode,a.order_type orderType,a.buy_mode buyMode,a.dprice dprice,a.order_status orderStatus");
			paramMap.put("table", " deliver_info_search_log a left join deliver_admin_user b on a.user_id=b.id left join deliver_corp_info c on b.deliver_id=c.id ");
			paramMap.put("condition", condition.toString());
			listRows = commonMapper.getCommonInfo(paramMap);
			easyuiDataGridJson.setTotal((long)listRows.size());
			//计算图标数据
			paramMap.clear();
			paramMap.put("column", "count(a.id) idCount ,LEFT(a.create_datetime,10) dateTime");
			paramMap.put("table", " deliver_info_search_log a left join deliver_admin_user b on a.user_id=b.id");
			if (!"".equals(userId)) {
				paramMap.put("condition", condition.toString()+" GROUP BY a.user_id,LEFT(a.create_datetime,10) ");
			}else{
				paramMap.put("condition", condition.toString()+" GROUP BY LEFT(a.create_datetime,10) ");
			}
			listRows = commonMapper.getCommonInfo(paramMap);
			StringBuffer countBf = new StringBuffer();
			StringBuffer dateBf = new StringBuffer();
			if(listRows!=null){
				for(int i=0;i<listRows.size();i++){
					HashMap<?, ?> map = listRows.get(i);
					countBf.append(map.get("idCount")+",");
					dateBf.append(map.get("dateTime")+",");
				}
			}
			List<HashMap<String, StringBuffer>> footList = new ArrayList<HashMap<String, StringBuffer>>();
			HashMap footMap = new HashMap();
			if(countBf!=null && countBf.length()>0 && dateBf!=null && dateBf.length()>0){
				footMap.put("count", countBf.substring(0, countBf.length()-1));
				footMap.put("time", dateBf.substring(0, dateBf.length()-1));
			}else{
				footMap.put("count", "0");
				footMap.put("time", "0");
			}
			footList.add(footMap);
			easyuiDataGridJson.setFooter(footList);
			j.setSuccess(true);
			j.setObj(easyuiDataGridJson);
			return j;
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
	}
	/**
	 * @return 导出excel用户查询日志
	 * @author zhangxiaolei
	 * @throws Exception 
	 */
	public Json exportUserLogList(HttpServletRequest request,HttpServletResponse response) throws Exception {
		Json j = new Json();
		try {
			voUser user = (voUser) request.getSession(false).getAttribute("userView");
			String userId = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("userId")));
			String deliver = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("deliver")));
			String startDate = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("startDate")));
			String endDate = StringUtil.dealParam(StringUtil.convertNull(request.getParameter("endDate")));
			if (user == null) {
				j.setMsg("当前没有登录,操作失败！");
				return j;
			}
			UserGroupBean group = user.getGroup();
			if (!group.isFlag(3003)) {
				j.setMsg("没有权限！");
				return j;
			}
			StringBuffer condition = new StringBuffer();
			condition.append("1=1");
			if (!"".equals(userId) && !"null".equals(userId)) {
				condition.append(" and a.user_id=" + userId);
			}
			if (!"-1".equals(deliver) && !"".equals(deliver)) {
				condition.append(" and b.deliver_id=" + deliver);
			}
			if (startDate != "" && endDate != "") {
				condition.append(" and a.create_datetime between' " + startDate + " 00:00:00" + "' AND '" + endDate + " 23:59:59'");
			}
			HashMap<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("column", "distinct(a.id),d.name orderTypeName,c.name deliverName,b.username userName,date_format(a.create_datetime,'%Y-%m-%d %T') createDateTime,a.ip_address ip,a.package_code packageCode,a.order_code orderCode,a.order_type orderType,concat(a.buy_mode,'') buyMode,a.dprice dprice,concat(a.order_status,'') orderStatus");
			paramMap.put("table", " deliver_info_search_log a left join deliver_admin_user b on a.user_id=b.id left join deliver_corp_info c on b.deliver_id=c.id left join user_order_package_type d on a.order_type=d.type_id");
			paramMap.put("condition", condition.toString());
			List<HashMap<String, String>> listRows = commonMapper.getCommonInfo(paramMap);
			if (listRows != null) {
				for(int i=0;i<listRows.size();i++){
					//取得付款方式中文
					HashMap<String, String> map = (HashMap<String, String>)listRows.get(i);
					if(map.get("buyMode")!=null){
						if(voOrder.buyModeMap.get(map.get("buyMode"))!=null){
							map.put("buyModeName", voOrder.buyModeMap.get(map.get("buyMode")).toString());
						}
					}
					//取得投递状态中文
					if(map.get("orderStatus")!=null){
						if(DeliverOrderInfoBean.deliverStateMap.get(StringUtil.StringToId(map.get("orderStatus")))!=null){
							map.put("orderStatusName", DeliverOrderInfoBean.deliverStateMap.get(StringUtil.StringToId(map.get("orderStatus"))));
						}
					}
				}
			}
			orderExportPrint(listRows, response);
			j.setSuccess(true);
			return j;
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
	}

	public void orderExportPrint(List list, HttpServletResponse response) throws Exception {
		int size = 0;
		String now = DateUtil.getNow().substring(0, 10);
		String fileName = now;
		//设置表头
	    ExportExcel excel = new ExportExcel();
		List<ArrayList<String>> headers = new ArrayList<ArrayList<String>>();
		ArrayList<String> header = new ArrayList<String>();
		header.add("序号");
		header.add("快递公司");
		header.add("用户名");
		header.add("查询时间");
		header.add("访问IP");
		header.add("包裹单号");
		header.add("订单号");
		header.add("订单品类");
		header.add("付款方式");
		header.add("订单金额");
		header.add("订单投递状态");
		headers.add(header);
		List<ArrayList<String>> bodies = new ArrayList<ArrayList<String>>();
		for(int i=0;i<list.size();i++){
			HashMap<String, Object> rowMap = (HashMap<String, Object>)list.get(i);
			ArrayList<String> tmp = new ArrayList<String>();
			tmp.add(i+1+"");
			tmp.add(rowMap.get("deliverName").toString());
			tmp.add(rowMap.get("userName").toString());
			tmp.add(rowMap.get("createDateTime").toString());
			tmp.add(rowMap.get("ip").toString());
			if(rowMap.get("packageCode")!=null){
				tmp.add(rowMap.get("packageCode")+"");
			}else{
				tmp.add("");
			}
			if(rowMap.get("orderCode")!=null){
				tmp.add(rowMap.get("orderCode")+"");
			}else{
				tmp.add("");
			}
			if(rowMap.get("orderTypeName")!=null){
				tmp.add(rowMap.get("orderTypeName")+"");
			}else{
				tmp.add("");
			}
			if(rowMap.get("buyModeName")!=null){
				tmp.add(rowMap.get("buyModeName")+"");
			}else{
				tmp.add("");
			}
			if(rowMap.get("dprice")!=null){
				tmp.add(rowMap.get("dprice")+"");
			}else{
				tmp.add("");
			}
			if(rowMap.get("orderStatusName")!=null){
				tmp.add(rowMap.get("orderStatusName")+"");
			}else{
				tmp.add("");
			}
			bodies.add(tmp);
		}

		/* 允许合并列,下标从0开始，即0代表第一列 */
		List<Integer> mayMergeColumn = new ArrayList<Integer>();
		excel.setMayMergeColumn(mayMergeColumn);

		/* 允许合并行,下标从0开始，即0代表第一行 */
		List<Integer> mayMergeRow = new ArrayList<Integer>();
		excel.setMayMergeRow(mayMergeRow);

		/*
		 * 该行为固定写法 （设置该值为导出excel最大列宽 ,下标从1开始）
		 * 需要注意，如果没有headers,只有bodies,则该行中setColMergeCount 为bodies的最大列数
		 */
		excel.setColMergeCount(size);

		/*
		 * 设置需要自己设置样式的行，以每个bodies为参照 具体的样式设置参考 DemoExcel.java中的setStyle方法
		 * 具体可以参照执行后导出的excel样式及DemoExcel中的setStyle方法
		 */
		List<Integer> row = new ArrayList<Integer>();

		/* 设置需要自己设置样式的列，以每个bodies为参照 */
		List<Integer> col = new ArrayList<Integer>();

		excel.setRow(row);
		excel.setCol(col);

		// 调用填充表头方法
		excel.buildListHeader(headers);

		// 调用填充数据区方法
		excel.buildListBody(bodies);
		// 文件输出
		excel.exportToExcel(fileName, response, "");
	}
}

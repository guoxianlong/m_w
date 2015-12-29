package mmb.tms.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.common.dao.CommonDao;
import mmb.rec.sys.easyui.EasyuiComBoBoxBean;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.Json;
import mmb.tms.service.IDeliverService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.StringUtil;

@RequestMapping("confDeliverController")
@Controller
public class ConfDeliverController {
	private static byte[] lock = new byte[0];
	@Autowired
	private CommonDao commonDao;
	@Autowired
	private IDeliverService deliverService;
	
	/**
	 * 查询发货快递公司分配规则列表
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/getDeliverSendConfList")
	@ResponseBody
	public EasyuiDataGridJson getDeliverSendConfList(HttpServletRequest request,HttpServletResponse response)throws Exception{
		EasyuiDataGridJson easyuiDataGridJson = new EasyuiDataGridJson();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			easyuiDataGridJson.setTip("当前没有登录,操作失败！");
			return easyuiDataGridJson;
		}
		StringBuffer condition = new StringBuffer();
		int area = StringUtil.toInt(request.getParameter("area"));
		int provinceId = StringUtil.toInt(request.getParameter("provinceId"));
		if (area == -1) {
			easyuiDataGridJson.setTip("必须填写地区！");
			return easyuiDataGridJson;
		}
		condition.append(" sa.id=" + area );
		if (provinceId != -1) {
			condition.append(" and p.id=" + provinceId);
		}
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("column", " sa.id areaId,p.id provinceId,sa.name areaName,case dsc.province_id when 0 then '其他省' else p.name end provinceName ,dci.id deliverId,dci.name deliverName,concat(dsc.count_limit,'') countLimit,concat(dsc.priority,'') priority,concat(dsc.whole_area, '') wholeArea,case dsc.whole_area when 0 then '非全境' when 1 then '全境' else '' end as wholeAreaName ");
		paramMap.put("table", " provinces p join stock_area sa  left join deliver_send_conf dsc on p.id=dsc.province_id and dsc.area=sa.id left join deliver_corp_info dci on dsc.deliver_id=dci.id  ");
		paramMap.put("condition", condition.toString() + " order by areaId,provinceId");
		List<HashMap<String, String>> list = commonDao.getCommonInfo(paramMap);
		
		HashMap<String, String> addItem = null;
		HashMap<Integer,String> stockoutAvailableAreaMap = ProductStockBean.stockoutAvailableAreaMap;
		for (int key : stockoutAvailableAreaMap.keySet()) {
			if (key == area) {
				if (provinceId == -1 || provinceId == 0) {
					paramMap.put("column", " sa.id areaId,dsc.province_id provinceId,sa.name areaName,'其他省' provinceName ,dci.id deliverId,dci.name deliverName,concat(dsc.count_limit,'') countLimit,concat(dsc.priority,'') priority,concat(dsc.whole_area, '') wholeArea,case dsc.whole_area when 0 then '非全境' when 1 then '全境' else '' end as wholeAreaName ");
					paramMap.put("table", " deliver_send_conf dsc join stock_area sa on dsc.area=sa.id join deliver_corp_info dci on dsc.deliver_id=dci.id  ");
					paramMap.put("condition", " dsc.area=" + key + " and dsc.province_id=0");
					List<HashMap<String, String>> info = commonDao.getCommonInfo(paramMap);
					if (info != null && info.size() > 0) {
						int i = 0;
						for (HashMap<String, String> map : info) {
							list.add(i, info.get(i));
							i ++;
						}
					} else {
						addItem = new HashMap<String, String>();
						addItem.put("areaId", key + "");
						addItem.put("provinceId", "0");
						addItem.put("areaName", stockoutAvailableAreaMap.get(key));
						addItem.put("provinceName", "其他省");	
						list.add(0, addItem);
					}
				}
			}
		}
		easyuiDataGridJson.setRows(list);
		return easyuiDataGridJson;
	}
	
	/**
	 * 分配快递公司
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/editDeliverSendConf")
	@ResponseBody
	public Json editDeliverSendConf(HttpServletRequest request,HttpServletResponse response)throws Exception{
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			j.setMsg("当前没有登录,操作失败！");
			return j;
		}
		synchronized (lock) {
			try{
				deliverService.editDeliverSendConf(request);
				j.setSuccess(true);
				return j;
			} catch (Exception e) {
				e.printStackTrace();
				j.setMsg(e.getMessage());
				return j;
			} 
		}
	}
	
	@RequestMapping("/getWholeArea")
	@ResponseBody
	public List<EasyuiComBoBoxBean> getWholeArea(HttpServletRequest request,HttpServletResponse response
			) throws ServletException, IOException {
		List<EasyuiComBoBoxBean> comboBoxList = null;
		int wholeArea = StringUtil.StringToId(request.getParameter("wholeArea"));
		try {
			comboBoxList = new ArrayList<EasyuiComBoBoxBean>();
			EasyuiComBoBoxBean bean = new EasyuiComBoBoxBean();
			bean = new EasyuiComBoBoxBean();
			bean.setId("0");
			bean.setText("非全境");
			bean.setSelected(wholeArea == 0 ? true : false);
			comboBoxList.add(bean);
			bean = new EasyuiComBoBoxBean();
			bean.setId("1");
			bean.setText("全境");
			bean.setSelected(wholeArea == 1 ? true : false);
			comboBoxList.add(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return comboBoxList;
	}
}

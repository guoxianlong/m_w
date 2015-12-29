package cn.mmb.delivery.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.common.dao.UserOrderDao;
import mmb.easyui.Json;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.EasyuiPageBean;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voUser;
import adultadmin.util.StringUtil;
import cn.mmb.delivery.application.DeliverApplication;
import cn.mmb.delivery.domain.model.vo.DeliverSwitchBean;

@Controller
@RequestMapping("/deliversController")
public class DeliversController {
	
	@Resource
	private DeliverApplication deliverFacade;
	
	@Resource
	private UserOrderDao userOrderDao;
	
	/**
	 * 获取快递公司切换记录
	 * @param ahc
	 * @return
	 */
	@RequestMapping("/getDeliverSwitch")
	@ResponseBody
	public EasyuiDataGridJson getDeliverSwitch(HttpServletRequest request,HttpServletResponse response, EasyuiPageBean page,
			DeliverSwitchBean deliverSwitchBean,String beginDatetime,String endDatetime){
		
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		Map<String, String> map = new HashMap<String, String>();
		StringBuffer condition = new StringBuffer();
		List<DeliverSwitchBean> deliverSwitchBeans = new ArrayList<DeliverSwitchBean>();
		if (deliverSwitchBean.getStockArea()==null && deliverSwitchBean.getOrderCode()==null && deliverSwitchBean.getOriginDeliverName()==null) {
			datagrid.setTotal((long) 0);
			datagrid.setRows(deliverSwitchBeans);
			return datagrid;
		}
		condition.append("1=1");
		if (!"全部".equals(deliverSwitchBean.getStockArea())&& deliverSwitchBean.getStockArea()!=null) {
			condition.append(" and stock_area='" + deliverSwitchBean.getStockArea()+"' ");
		}
		if (!"".equals(StringUtil.checkNull(deliverSwitchBean.getOrderCode()))) {
			condition.append(" and order_code='" + deliverSwitchBean.getOrderCode() + "' ");
		}
		if (!"请选择".equals(deliverSwitchBean.getOriginDeliverName()) && deliverSwitchBean.getOriginDeliverName() !=null) {
			condition.append(" and origin_deliver_name='" + deliverSwitchBean.getOriginDeliverName() + "' ");
		}
		if (!"".equals(StringUtil.checkNull(beginDatetime)) && !"".equals(StringUtil.checkNull(endDatetime))) {
			condition.append(" and create_datetime between '" + beginDatetime + " 00:00:00" + "'");
			condition.append(" and '" + endDatetime + " 23:59:59" + "'");
		}
		map.put("condition", condition.toString());
		int rowCount = deliverFacade.getDeliverSwitchCount(map);
		datagrid.setTotal((long) rowCount);
		map.put("start", (page.getPage() - 1) * page.getRows() + "");
		map.put("count", page.getRows() + "");
		map.put("sort", page.getSort());
		map.put("order", page.getOrder());
		
		deliverSwitchBeans = deliverFacade.getDeliverSwitchList(map);
		datagrid.setRows(deliverSwitchBeans);
		return datagrid;
	}
	
	/**
	 * 获取快递公司和运单号
	 * @param ahc
	 * @return
	 */
	@RequestMapping("/getDeliver")
	@ResponseBody
	public Json getDeliver(HttpServletRequest request,String orderCode){
		Json j = new Json();
		try {
			voOrder Order = userOrderDao.getUserOrder(" code = '"+orderCode+"' ");
			if(Order==null){
				j.setMsg("订单号不存在！");
				j.setSuccess(false);
				return j;
			}
			Map<String,Object> map =deliverFacade.getDeliver(orderCode);
			j.setObj(map);
			j.setSuccess(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return j;
	}
	
	/**
	 * 添加快递公司切换记录
	 * @param ahc
	 * @return
	 */
	@RequestMapping("/addDeliverSwitch")
	@ResponseBody
	@Transactional
	public Json addDeliverSwitch(HttpServletRequest request,String orderCodes[],String delivers[],String remarks[],String[] nos){
		Json j = new Json();
		List<Integer> y = new ArrayList<Integer>();//记录操作成功的行号
		voUser user = (voUser)request.getSession(false).getAttribute("userView");
		if(user == null) {
			j.setMsg("你没有登录！");
			return j;
		}
		for( int i = 0 ; i < nos.length ; i++){
			int num = Integer.parseInt(nos[i]);//行号
			String orderCode = orderCodes[i];
			String targetDeliver = delivers[i];
			String remark = StringUtil.changStrToXml(remarks[i]);
			
			if("".equals(orderCode.trim())){
				j.setMsg("第"+num+"行-订单号不能为空！");
				if(i >= 1){
					j.setObj(y);
				}
				return j;
			}
			if("-1".equals(targetDeliver)){
				j.setMsg("第"+num+"行-请选择变更快递公司！");
				if(i >= 1){
					j.setObj(y);
				}
				return j;
			}
			
			voOrder order = userOrderDao.getUserOrder(" code = '"+orderCode+"' ");
			if(order.getDeliver()==0){
				j.setMsg("第"+num+"行-该订单目前没有分配快递公司！");
				if(i >= 1){
					j.setObj(y);
				}
				return j;
			}
			if(targetDeliver.equals(order.getDeliver()+"")){
				j.setMsg("第"+num+"行-快递公司必须不同！");
				if(i >= 1){
					j.setObj(y);
				}
				return j;
			}
			
			try {
				deliverFacade.switchDeliver(order.getDeliver(),Integer.parseInt(targetDeliver),remark,order,user);
				y.add(num);
			} catch (Exception e) {
				e.printStackTrace();
				j.setMsg("第"+num+"行-"+e.getMessage());
				j.setObj(y);
				return j;
			}
		}
		if(j.getMsg()==null || "".equals(j.getMsg())){
			j.setMsg("操作成功！");
		}
		j.setObj(y);
		return j;
	}
	
}

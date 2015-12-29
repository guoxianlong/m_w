package adultadmin.action.orderSale;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.order.EachProductLineBean;
import adultadmin.framework.IConstants;
import adultadmin.util.DateUtil;
import adultadmin.util.StatUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.ProductLinePermissionCache;

public class EachProudctLineAction extends DispatchAction{
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		String userProductIds = ProductLinePermissionCache.getProductLineIds(user); //当前用户权限下的产品线
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		
		if((startTime.equals("") && !endTime.equals(""))|| !startTime.equals("") && endTime.equals("")){
			request.setAttribute("tip", "不能只输入一个日期");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		} 
		int temps = DateUtil.getDaySub(startTime,endTime);
		if(temps>30){
			request.setAttribute("tip", "时间段不得大于31天!");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		if(startTime.equals("") && endTime.equals("")){
			startTime = DateUtil.getFirstDayOfMonth();
			endTime = DateUtil.getNowDateStr();
			request.setAttribute("startTime", startTime);
			request.setAttribute("endTime", endTime);
		}
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOp);
		try{
			int minId = StatUtil.getDayFirstOrderId(startTime);
			if(minId <= 0){
			    minId = StatUtil.getDayOrderId(startTime);
			}
			String condition = "uo.id>="+minId+" and uo.create_datetime >= '"+startTime+"' and uo.create_datetime <='"+endTime+" 23:59:59'";
			ResultSet rs=null;
			StringBuilder bs=  new StringBuilder(" and plc.product_line_id  in("+userProductIds+") "); 
			List productLineList = wareService.getProductLineList(" product_line.id  in("+userProductIds+") ");
		 
			//统计非套餐  加不统计换货订单
			String sql ="select plc.product_line_id pli ,sum(uop.count) s ,sum(uop.dprice*uop.count) sprice ,sum(p.price5*uop.count) sprice5 from user_order uo join user_order_product_split_history uop on uo.id=uop.order_id left join product p on uop.product_id = p.id " 
				+"join product_line_catalog plc on (p.parent_id1=plc.catalog_id or p.parent_id2=plc.catalog_id) where "+
				condition+" and p.is_package=0 "+bs.toString()+
				" and uo.code rlike '^[^S+]' "; //去掉 S单换货
			String groupBy=" group by pli";
			rs = wareService.getDbOp().executeQuery(sql+" and uo.status in(3,6,9,12,14) "+groupBy);  //成交
			Map map = new HashMap();
			while(rs.next()){
				String key = rs.getString("pli");
				EachProductLineBean bean =new EachProductLineBean();
				bean.setDealOrder(rs.getInt("s"));
				bean.setDealOrderSale(rs.getFloat("sprice"));
				bean.setDealOrderCost(rs.getFloat("sprice5"));
				map.put(key, bean);
			}
			rs.close();
		
			rs = wareService.getDbOp().executeQuery(sql+" and uo.status in (6,11,12,13,14)"+groupBy);	//已发货
			while(rs.next()){
				String key = rs.getString("pli");
				if(map.containsKey(key)){
					EachProductLineBean eachProductBean =(EachProductLineBean)map.get(key);
					eachProductBean.setShipped(rs.getInt("s"));
					eachProductBean.setShippedSale(rs.getFloat("sprice"));
					eachProductBean.setShippedCost(rs.getFloat("sprice5"));
				}else{
					EachProductLineBean eachProductBean =new EachProductLineBean();
					eachProductBean.setShipped(rs.getInt("s"));
					eachProductBean.setShippedSale(rs.getFloat("sprice"));
					eachProductBean.setShippedCost(rs.getFloat("sprice5"));
					map.put(key, eachProductBean);
				}
			}
			rs.close();
			
			rs = wareService.getDbOp().executeQuery(sql+" and uo.status in (11,13)"+groupBy);	//退单
			while(rs.next()){
				String key = rs.getString("pli");
				if(map.containsKey(key)){
					EachProductLineBean eachProductBean =(EachProductLineBean)map.get(key);
					eachProductBean.setBackOrder(rs.getInt("s"));
					eachProductBean.setBackOrderSale(rs.getFloat("sprice"));
					eachProductBean.setBackOrderCost(rs.getFloat("sprice5"));
				}else{
					EachProductLineBean eachProductBean =new EachProductLineBean();
					eachProductBean.setBackOrder(rs.getInt("s"));
					eachProductBean.setBackOrderSale(rs.getFloat("sprice"));
					eachProductBean.setBackOrderCost(rs.getFloat("sprice5"));
					map.put(key, eachProductBean);
				}
				
			}
			rs.close();
			 
			//统计赠品
			sql ="select plc.product_line_id pli ,sum(uop.count) s ,sum(p.price5*uop.count) sprice5 from user_order uo join user_order_present_split_history uop on uo.id=uop.order_id left join product p on uop.product_id = p.id " 
				+"join product_line_catalog plc on (p.parent_id1=plc.catalog_id or p.parent_id2=plc.catalog_id) where "+
				condition+" and p.is_package=0 "+bs.toString()+
				" and uo.code rlike '^[^S+]' "; //去掉 S单换货
			rs = wareService.getDbOp().executeQuery(sql+" and uo.status in(3,6,9,12,14) ");  //成交
			while(rs.next()){
				String key = rs.getString("pli");
				if(map.containsKey(key)){
					EachProductLineBean eachProductBean =(EachProductLineBean)map.get(key);
					eachProductBean.setDealOrder(eachProductBean.getDealOrder()+rs.getInt("s"));
	 				eachProductBean.setDealOrderCost(eachProductBean.getDealOrderCost()+rs.getFloat("sprice5"));
				}else{
					EachProductLineBean eachProductBean =new EachProductLineBean();
					eachProductBean.setDealOrder(rs.getInt("s"));
	 				eachProductBean.setDealOrderCost(rs.getFloat("sprice5"));
					map.put(key, eachProductBean);
				}
				
			}
			rs.close();
		
			rs = wareService.getDbOp().executeQuery(sql+" and uo.status in (6,11,12,13,14) ");	//已发货
			while(rs.next()){
				String key = rs.getString("pli");
				if(map.containsKey(key)){
					EachProductLineBean eachProductBean =(EachProductLineBean)map.get(key);
					eachProductBean.setShipped(eachProductBean.getShipped()+rs.getInt("s"));
					eachProductBean.setShippedCost(eachProductBean.getShippedCost()+rs.getFloat("sprice5"));
				}else{
					EachProductLineBean eachProductBean =new EachProductLineBean();
					eachProductBean.setShipped(rs.getInt("s"));
					eachProductBean.setShippedCost(rs.getFloat("sprice5"));
					map.put(key, eachProductBean);
				}
			}
			rs.close();
			
			rs = wareService.getDbOp().executeQuery(sql+" and uo.status in (11,13) ");	//退单
			while(rs.next()){
				String key = rs.getString("pli");
				if(map.containsKey(key)){
					EachProductLineBean eachProductBean =(EachProductLineBean)map.get(key);
					eachProductBean.setBackOrder(eachProductBean.getBackOrder()+rs.getInt("s"));
					eachProductBean.setBackOrderCost(eachProductBean.getBackOrderCost()+rs.getFloat("sprice5"));
				}else{
					EachProductLineBean eachProductBean =new EachProductLineBean();
					eachProductBean.setBackOrder(rs.getInt("s"));
					eachProductBean.setBackOrderCost(rs.getFloat("sprice5"));
					map.put(key, eachProductBean);
				}
				
			}
			rs.close();
			
			List list = new ArrayList();
			for(Iterator i = productLineList.iterator();i.hasNext();){
				voProductLine productLine = (voProductLine)i.next();
				String key = String.valueOf(productLine.getId());
				if(map.containsKey(key)){
					EachProductLineBean bean =(EachProductLineBean)map.get(key);
					bean.setProductLineName(productLine.getName());
					bean.setProductLineId(productLine.getId());
					list.add(bean);
				}else{
					EachProductLineBean bean =new EachProductLineBean();
					bean.setProductLineName(productLine.getName());
					bean.setProductLineId(productLine.getId());
					list.add(bean);
				}
			}
 			
			request.setAttribute("eachList", list);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			wareService.releaseAll();
		}
		return mapping.findForward("success");
	}
	
}


		


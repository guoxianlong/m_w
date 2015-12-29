package mmb.stock.stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.ware.WareService;

import org.apache.struts.actions.DispatchAction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/abnormalQuery")
public class AbnormalQueryAcion extends DispatchAction {
	
	/***
	 * @Describe 获取配送异常查询
	 * @param request
	 * @param response
	 */
	@RequestMapping("/getAbnormalQueryList")
	@ResponseBody
	public EasyuiDataGridJson getAbnormalQueryList(HttpServletRequest request,HttpServletResponse response,AbnormalQueryBean aqb){
		voUser user = (voUser)request.getSession().getAttribute("userView");		
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		Map<String,String> map = new HashMap<String,String>();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.WARE);
		WareService wareService = new WareService(dbOp); 
		AbnormalQueryService abnormalQueryService = new AbnormalQueryService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());		
		List<AbnormalQueryBean> list = new ArrayList<AbnormalQueryBean>();
		try {
			if(user == null){
				request.setAttribute("msg", "当前没有登录，操作失败！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			
			if(aqb.getCode()==null){
				datagrid.setRows(list);
				datagrid.setTotal((long)list.size());
				return datagrid;
			}
			map.put("condition", aqb.getCode());
			if(abnormalQueryService.getProductstatus(map)){
				request.setAttribute("msg", "查询失败，该订单状态不允许查询！");
				request.getRequestDispatcher("/admin/rec/err.jsp").forward(request, response);
				return null;
			}
			
			list =abnormalQueryService.getAbnormalQueryList(map);
			if(list.size()!=0){
				AbnormalOrderSearchLogBean aosl = new AbnormalOrderSearchLogBean();
				aosl.setSearchUserId(user.getId());
				aosl.setSearchUserName(user.getUsername());
				aosl.setSearchDatetime(DateUtil.getNow());
				aosl.setOrderCode(aqb.getCode());
				abnormalQueryService.addAbnormalOrderSearchLog(aosl);
			}
			datagrid.setRows(list);
			datagrid.setTotal((long)list.size());
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			abnormalQueryService.releaseAll();
		}
		
		return datagrid;
	}
	
	/***
	 * @Describe 获取商品信息
	 * @param request
	 * @param response
	 */
	@RequestMapping("/getProductList")
	@ResponseBody
	public EasyuiDataGridJson getProductList(HttpServletRequest request,HttpServletResponse response,AbnormalQueryBean aqb){
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		Map<String,String> map = new HashMap<String,String>();
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.WARE_SLAVE);
		WareService wareService = new WareService(dbOp); 
		AbnormalQueryService abnormalQueryService = new AbnormalQueryService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());		
		List<AbnormalQueryProductBean> list = new ArrayList<AbnormalQueryProductBean>();
		try {
			if(aqb.getCode()==null){
				datagrid.setRows(list);
				return datagrid;
			}
			map.put("condition", aqb.getCode());
			list =abnormalQueryService.getProductList(map);
			datagrid.setRows(list);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			abnormalQueryService.releaseAll();
		}
		return datagrid;
	}
}

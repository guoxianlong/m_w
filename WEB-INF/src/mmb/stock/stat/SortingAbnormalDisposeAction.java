package mmb.stock.stat;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.cargo.CargoDeptAreaService;
import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;


/**
 * 分播异常处理 Action
 * @author Administrator
 *
 */
public class SortingAbnormalDisposeAction extends DispatchAction {

	private static Object lock = new Object();
	
	/**
	 * 
	 * 查询分拣异常单
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return
	 */
	public ActionForward getSortingAbnormalInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		/*UserGroupBean group = user.getGroup();
		if( !group.isFlag(745) ) {
			request.setAttribute("tip", "您没有理赔核销权限！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}*/
		int wareArea = StringUtil.toInt(request.getParameter("wareArea"));
		int status = StringUtil.toInt(request.getParameter("status"));
		String startTime = StringUtil.toSql(StringUtil.convertNull(request.getParameter("startTime")));
		String endTime = StringUtil.toSql(StringUtil.convertNull(request.getParameter("endTime")));
		int type = StringUtil.toInt(request.getParameter("type"));
		String code = StringUtil.toSql(StringUtil.convertNull(request.getParameter("code")));
		
		int pageIndex = StringUtil.parstInt(request.getParameter("pageIndex"));
		int countPerPage = 50;
		List sortingAbnormalList = new ArrayList();
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlCount = new StringBuilder();
		StringBuilder params = new StringBuilder();
		DbOperation dbOperation = new DbOperation();
		dbOperation.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbOperation);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, dbOperation);
		SortingAbnormalDisposeService sortingAbnormalDisposeService = new SortingAbnormalDisposeService(IBaseService.CONN_IN_SERVICE, dbOperation);
		try {
			PagingBean paging = null;
			List<String> cdaList =  CargoDeptAreaService.getCargoDeptAreaList(request);
			String availAreaIds = "100";
			int t = cdaList.size();
			for(int i = 0; i < t; i++ ) {
				availAreaIds += "," + cdaList.get(i);
			}
			
			if( code != null && !code.equals("") && !code.equals("货位号/商品编号")) {
				sql.append("select sa.* from sorting_abnormal sa, sorting_abnormal_product sap where sa.id = sap.sorting_abnormal_id");
				sqlCount.append("select count(sa.id) from sorting_abnormal sa, sorting_abnormal_product sap where sa.id = sap.sorting_abnormal_id");
			} else {
				sql.append("select sa.* from sorting_abnormal sa where sa.id > 0");
				sqlCount.append("select count(sa.id) from sorting_abnormal sa where sa.id > 0");
			}
			
			if( wareArea != -1 ) {
				params.append("&");
				params.append("wareArea=" + wareArea);
				
			    sql.append(" and sa.ware_area = " + wareArea );
			    sqlCount.append(" and sa.ware_area = " + wareArea );
			} else {
				
			    sql.append(" and sa.ware_area in (" + availAreaIds + ")" );
			    sqlCount.append(" and sa.ware_area in (" + availAreaIds + ")" );
			}
			
			if( !startTime.equals("") && !endTime.equals("")) {
				if( sortingAbnormalDisposeService.isDateMoreThanThirtyOne(startTime, endTime) ) {
					request.setAttribute("tip", "两个时间的差距需在31天之内！");
					request.setAttribute("result", "failure");
					return mapping.findForward(IConstants.FAILURE_KEY);
				}
				params.append("&");
				params.append("startTime=" + startTime);
				params.append("&");
				params.append("endTime=" + endTime);
				
				String createTimeStart = startTime + " 00:00:00";
				String createTimeEnd = endTime + " 23:59:59";
	        	sql.append(" and").append(" sa.create_datetime between '").append(createTimeStart).append("' and '").append(createTimeEnd).append("'");
	        	sqlCount.append(" and").append(" sa.create_datetime between '").append(createTimeStart).append("' and '").append(createTimeEnd).append("'");
			} else {
				String currentTime = DateUtil.getNow();
				currentTime = currentTime.substring(0,10);
				params.append("&");
				params.append("startTime=" + currentTime);
				params.append("&");
				params.append("endTime=" + currentTime);
				
				String createTimeStart = currentTime + " 00:00:00";
				String createTimeEnd = currentTime + " 23:59:59";
	        	sql.append(" and").append(" sa.create_datetime between '").append(createTimeStart).append("' and '").append(createTimeEnd).append("'");
	        	sqlCount.append(" and").append(" sa.create_datetime between '").append(createTimeStart).append("' and '").append(createTimeEnd).append("'");
			}
 			
			if( status != -1 ) {
				params.append("&");
				params.append("status=" + status);
				
				sql.append(" and sa.status = " + status);
				sqlCount.append(" and sa.status = " + status);
			} else {
				params.append("&");
				params.append("status=" + SortingAbnormalBean.STATUS0);
				
				sql.append(" and sa.status = " + SortingAbnormalBean.STATUS0);
				sqlCount.append(" and sa.status = " + SortingAbnormalBean.STATUS0);
			}
			if( type != -1 ) {
				params.append("&");
				params.append("type=" + type);
				
				sql.append(" and sa.abnormal_type = " + type);
				sqlCount.append(" and sa.abnormal_type = " + type);
			}
			
			if (!code.equals("")) {
				params.append("&");
				params.append("code=" + code);
				code = code.trim();
				if (code.matches("[0-9]{1,20}")) {
					sql.append(" and sap.product_code = '" + code + "'");
					sqlCount.append(" and sap.product_code = '" + code + "'");
				} else if (code.matches("[A-Z]{3}[0-9]{2}-[A-Z]{1}[0-9]{5,8}")) {
					sql.append(" and sap.cargo_whole_code = '" + code + "'");
					sqlCount.append(" and sap.cargo_whole_code = '" + code
							+ "'");
				} else if (code.matches("[A-Z]{1}[0-9]{5,8}")) {
					sql.append(" and sap.cargo_whole_code like '%" + code + "'");
					sqlCount.append(" and sap.cargo_whole_code like '%" + code
							+ "'");
				} 
			}
			
			int totalCount = sortingAbnormalDisposeService.getSortingAbnormalCount2(sqlCount.toString());
			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			sortingAbnormalList = sortingAbnormalDisposeService.getSortingAbnormal2(sql.toString(), paging.getCurrentPageIndex()*countPerPage, countPerPage, "create_datetime desc");
			int x = sortingAbnormalList.size();
			for ( int i = 0; i < x; i++) {
				SortingAbnormalBean saBean = (SortingAbnormalBean) sortingAbnormalList.get(i);
				List sortingAbnormalProductList = sortingAbnormalDisposeService.getSortingAbnormalProductList("sorting_abnormal_id = " + saBean.getId(), -1, -1, "lock_count asc");
				saBean.setSortingAbnormalProductList(sortingAbnormalProductList);
			}
			paging.setPrefixUrl("sortingAbnormalDispose.do?method=getSortingAbnormalInfo" + params.toString());
			request.setAttribute("paging", paging);
			//request.setAttribute("recordNum", totalCount+"");
			request.setAttribute("list", sortingAbnormalList);	
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return new ActionForward("/admin/cargo/sortingAbnormalList.jsp?mes=1" + params.toString());
	}
	
}

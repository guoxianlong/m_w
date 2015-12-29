package adultadmin.action.admin;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.action.vo.voUser;
import adultadmin.bean.order.*;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;

public class SearchOrderStockAmountAction extends BaseAction{
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		String startDate=StringUtil.convertNull(request.getParameter("startDate"));
		String endDate=StringUtil.convertNull(request.getParameter("endDate"));
		String startTime=StringUtil.convertNull(request.getParameter("startTime"));
		String endTime=StringUtil.convertNull(request.getParameter("endTime"));

		if(startDate.equals("")||endDate.equals("")){
			request.setAttribute("tip", "日期不能为空！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}

		Calendar c1=Calendar.getInstance();
		Calendar c2=Calendar.getInstance();
		Calendar c3=Calendar.getInstance();
		startTime = startTime+":00";
		endTime = endTime+":00";
		String start=startDate+" "+startTime;
		String end=endDate+" "+endTime;
		c1.setTime(DateUtil.parseDate(start, DateUtil.normalTimeFormat));
		c2.setTime(DateUtil.parseDate(end, DateUtil.normalTimeFormat));
		c3.setTime(DateUtil.parseDate(start, DateUtil.normalTimeFormat));
		c3.add(Calendar.DAY_OF_YEAR, 30);
		if(c3.compareTo(c2)<0){
			request.setAttribute("tip", "时间范围不得超出30天！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		if(c1.compareTo(c2)>0){
			request.setAttribute("tip", "截止时间不得小于开始时间！");
			request.setAttribute("result", "failure");
			return mapping.findForward(IConstants.FAILURE_KEY);
		}
		
		List halfHourList=DateUtil.getHalfHourDateList(start, end, 8, 0);
		List halfHourListAmount=new ArrayList();
		for(int i=0;i<halfHourList.size();i++){
			String[] halfHour=(String[])halfHourList.get(i);
			String halfHourStart=halfHour[0];//半个小时的起始时间
			String halfHourEnd=halfHour[1];//半个小时的结束时间
			IStockService service = ServiceFactory.createStockService(
					IBaseService.CONN_IN_SERVICE, null);
			try{
				int count = service.getNumber("id", "order_stock", "count", "create_datetime>='"+halfHourStart
						+"' and create_datetime<'"+halfHourEnd+"' and status != "+OrderStockBean.STATUS4);
				halfHourListAmount.add(Integer.valueOf(count));
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				service.releaseAll();
			}
		}
		request.setAttribute("orderStockList", halfHourList);
		request.setAttribute("orderstockamount", halfHourListAmount);

		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
}

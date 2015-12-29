/*
 * Created on 2009-4-28
 *
 */
package adultadmin.action.system;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.system.TextResBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ISystemService;
import adultadmin.util.StringUtil;

public class TextResAction {

	/**
	 * 
	 * 作者：张陶
	 * 创建日期：2009-5-17
	 * 说明：文本资源列表
	 * 参数及返回值说明：
	 * @param request
	 * @param response
	 */
	public void textResList(HttpServletRequest request, HttpServletResponse response) {
        int countPerPage = 30;
        int type = StringUtil.toInt(request.getParameter("type"));
        if(request.getAttribute("type")!=null && !"".equalsIgnoreCase("type")){
        	type = Integer.parseInt(request.getAttribute("type").toString());
        }
        ISystemService service = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, null);
        try {
            String condition = null;
            if(type > 0){
            	condition = "type=" + type;
            }
            //总数
            int totalCount = service.getTextResCount(condition);
            //页码
            int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
            PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
            List list = service.getTextResList(condition, paging.getCurrentPageIndex() * countPerPage, countPerPage, "id desc");
            
            for(int i = 0 ; i < list.size() ; i++){
            	TextResBean bean = (TextResBean)list.get(i);
            	int id = bean.getId();
            	 String sql = "select brp.buy_return_id,brp.reason_id from buy_return_product brp,buy_return br" +
          		" where brp.buy_return_id = br.id and br.status=6 and brp.reason_id=?";
     			 service.getDbOp().prepareStatement(sql);
     			 PreparedStatement plps = service.getDbOp().getPStmt();
     			 plps.setInt(1, id);
     			 ResultSet rs = plps.executeQuery();
     			 int buy_return_id = 0 ; 
     			 while(rs.next()){
     				buy_return_id = rs.getInt(1);
     			 }
     			 if(buy_return_id != 0){
     				 bean.setFlag(true);
     			 }
            }
            if(type==2){
            	 paging.setPrefixUrl("disposeSuggestList.jsp?type=2");
            }
            else if(type==5){
            	paging.setPrefixUrl("buyReturnReasonList.jsp?type=5");
            }
            else if(type == 1){
            	paging.setPrefixUrl("textResList.jsp?type=1");
            }
            request.setAttribute("paging", paging);
            request.setAttribute("list", list);
        } 
        catch(Exception e){
        	e.printStackTrace();
        }
        finally {
            service.releaseAll();
        }
    }

	/**
	 * 售后选项在 text_res 中的type
	 */
	private final static String  AFTERSALETYPE="2,3,4,6,7,8,9,10,11,12";
	
	/**
	 * 
	 * 功能:获取售后单的列表
	 * <p>作者 李双 Mar 23, 2012 10:17:01 AM
	 * @param request
	 * @param response
	 */
	public void getAfterSaleTextResList(HttpServletRequest request, HttpServletResponse response) {
		int countPerPage = 20;
		int type = StringUtil.toInt(request.getParameter("type"));
		StringBuilder condition = new StringBuilder("type in("+AFTERSALETYPE+")");
		String prefixUrl="";
		if(type>0){
			condition.delete(0, condition.length());
			condition.append("type=");
			condition.append(type);
			prefixUrl="?type="+type;
		}
		ISystemService service = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, null);
		try {
			int totalCount = service.getTextResCount(condition.toString());
			// 页码
			int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
			PagingBean paging = new PagingBean(pageIndex, totalCount,countPerPage);
			List list = service.getTextResList(condition.toString(), paging.getCurrentPageIndex()* countPerPage, countPerPage, "id desc");
			List selectList=service.getTextResList(" type in("+AFTERSALETYPE+") group by type", -1, -1, null);
			paging.setPrefixUrl("disposeSuggestList.jsp"+prefixUrl);
			request.setAttribute("paging", paging);
			request.setAttribute("list", list);
			request.setAttribute("selectList", selectList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
	
	public void showAfterSaleTextRes(HttpServletRequest request, HttpServletResponse response) {
		int id = StringUtil.StringToId(request.getParameter("id"));
		if(id==0){
			return;
		}
		ISystemService service = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, null);
		try{
			TextResBean bean = service.getTextRes("id = " + id);
			 request.setAttribute("bean", bean);
		}finally {
			service.releaseAll();
		}
	}
	
    /**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-17
	 * 
	 * 说明：修改文本资源
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
    public void editTextRes(HttpServletRequest request, HttpServletResponse response) {
    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
		UserGroupBean group = user.getGroup();
		int textResId = StringUtil.StringToId(request.getParameter("textResId"));
		String content = StringUtil.dealParam(request.getParameter("content"));
		int type = StringUtil.toInt(request.getParameter("type"));
        String back = request.getParameter("back");
		ISystemService service = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, null);
		try {
			TextResBean bean = service.getTextRes("id = " + textResId);
			if(bean == null){
				request.setAttribute("tip", "没有这个文本资源，操作失败！");
	            request.setAttribute("result", "failure");
	            return;
			}
			// 开始事务
			service.getDbOp().startTransaction();
			content = StringUtil.toWml(content);
			service.updateTextRes("content = '" + StringUtil.toSql(content) + "'", "id = " + textResId);
			// 提交事务
			service.getDbOp().commitTransaction();
			request.setAttribute("back", back);
		} 
		finally {
			service.releaseAll();
		}
    }
    /**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-17
	 * 
	 * 说明：添加文本资源
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
    public void addTextRes(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
    	UserGroupBean group = user.getGroup();
        String content = StringUtil.dealParam(request.getParameter("content"));
        int type = StringUtil.toInt(request.getParameter("type"));
        String back = request.getParameter("back");
		if (type <= 0) {
			request.setAttribute("tip", "请选择正确的类型！");
			request.setAttribute("result", "failure");
			return;
		}

		if (StringUtil.isNull(content)) {
			request.setAttribute("tip", "请输入文本内容！");
			request.setAttribute("result", "failure");
			return;
		}

		ISystemService service = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, null);
		try {
			service.getDbOp().startTransaction();

			int id = service.getNumber("id", "text_res", "max", "id > 0") + 1;
			TextResBean bean = new TextResBean();
			bean.setId(id);
			bean.setType(type);
			bean.setContent(content);
			bean.setContentPy();
			if (!service.addTextRes(bean)) {
				request.setAttribute("tip", "添加失败！");
				request.setAttribute("result", "failure");
				return;
			}
			service.getDbOp().commitTransaction();

			request.setAttribute("textResId", String.valueOf(id));
			request.setAttribute("back", back );
		} finally {
			service.releaseAll();
		}
    }

    /**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-5-17
	 * 
	 * 说明：查看、编辑文本资源
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param request
	 * @param response
	 */
    public void textRes(HttpServletRequest request, HttpServletResponse response) {
    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
    	UserGroupBean group = user.getGroup();

    	int textResId = StringUtil.StringToId(request.getParameter("textResId"));
    	ISystemService service = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, null);
        try {
            TextResBean bean = service.getTextRes("id = " + textResId);
            if(bean == null){
        		request.setAttribute("tip", "没有找到这条记录，操作失败！");
                request.setAttribute("result", "failure");
                return;
            }
            if(bean.getType()==5){
	            String sql = "select brp.buy_return_id,brp.reason_id from buy_return_product brp,buy_return br" +
	     		" where brp.buy_return_id = br.id and br.status=6 and brp.reason_id=?";
				 service.getDbOp().prepareStatement(sql);
				 PreparedStatement plps = service.getDbOp().getPStmt();
				 plps.setInt(1, textResId);
				 ResultSet rs = plps.executeQuery();
				 int buy_return_id = 0 ; 
				 while(rs.next()){
					 buy_return_id = rs.getInt(1);
				 }
				 rs.close();
				 plps.close();
				 if(buy_return_id != 0 ){
					 request.setAttribute("tip", "该采购退货原因  ,已经被使用  无法编辑");
					 request.setAttribute("result", "failure");
					 return;
				 }
            }
            request.setAttribute("bean", bean);
        } 
        catch(Exception e){
        	e.printStackTrace();
        }
        finally {
            service.releaseAll();
        }
    }

    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2009-5-17
     * 
     * 说明：删除文本资源
     * 
     * 参数及返回值说明：
     * 
     * @param request
     * @param response
     */
    public void deleteTextRes(HttpServletRequest request, HttpServletResponse response) {

    	voUser user = (voUser)request.getSession().getAttribute("userView");
    	if(user == null){
    		request.setAttribute("tip", "当前没有登录，操作失败！");
            request.setAttribute("result", "failure");
            return;
    	}
    	UserGroupBean group = user.getGroup();
    	int textResId = StringUtil.StringToId(request.getParameter("textResId"));
    	String[] ids = request.getParameterValues("id");
    	ISystemService service = ServiceFactory.createSystemService(IBaseService.CONN_IN_SERVICE, null);
		try {
			if(textResId > 0){//删除一个
				TextResBean bean = service.getTextRes("id = " + textResId);
	            if(bean == null){
	        		request.setAttribute("tip", "没有找到这条记录，操作失败！");
	                request.setAttribute("result", "failure");
	                return;
	            }
	            String sql =null;String msg=null;
	            if(bean.getType()==5){
	            	sql="select brp.buy_return_id,brp.reason_id from buy_return_product brp,buy_return br" +
	            		" where brp.buy_return_id = br.id and br.status=6 and brp.reason_id=?";
	            	msg="该采购退货原因,";
	            	
	            }else if(bean.getType()==2){// 2处理意见,3, 7,8 售后 4客户要求 6退换货原因 
	            	sql="select id from problem_and_solver_record where processing_suggest_id= ? limit 1";
	            	msg="处理意见";	
	            }else if(bean.getType()==3 || bean.getType()==8 ){
	            	String temp=null;
	            	if(bean.getType()==3){
	            		temp="complaint_type_id";
	            		msg="投诉分类";
	            	}else{
	            		temp="customer_bank_name";
	            		msg="银行名称";
	            	}
	            	sql="select id from after_sale_order where "+temp+"= ? limit 1";
	            }else if(bean.getType()==6){
	            	sql="select id from after_sale_cost_list where type_res= ? limit 1";
	            	msg="退换货原因";
	            }else if(bean.getType()==7){
	            	sql="select id from after_sale_back_package where deliver= ? limit 1";
	            	msg="快递名称";
	            }
	            
	            if(sql!=null){
		            service.getDbOp().prepareStatement(sql);
					PreparedStatement plps = service.getDbOp().getPStmt();
		            plps.setInt(1, textResId);
		            ResultSet rs = plps.executeQuery();
		            int buy_return_id = 0 ; 
		            if(rs.next()){
		            	buy_return_id = rs.getInt(1);
		            }
		            rs.close();
		            plps.close();
		            if(buy_return_id != 0 ){
		            	request.setAttribute("tip", msg+"已经被使用  无法删除");
		                request.setAttribute("result", "failure");
		                return;
		            }
	            }
				service.getDbOp().startTransaction();
				service.deleteTextRes("id = " + textResId);
				service.getDbOp().commitTransaction();
            } 
			else{
				 if(ids != null && ids.length > 0){//批量删除
					 	StringBuilder sb = new StringBuilder();
		            	for(int i = 0 ; i < ids.length ; i++){
		            		sb.append(ids[i]).append(",");
		            	}
		            	sb.delete(sb.length()-1, sb.length());
		            	String[] sql =new String[6];String[] msg=new String[6];
			            
		            	sql[0]="select brp.buy_return_id,brp.reason_id from buy_return_product brp,buy_return br" +
		            		" where brp.buy_return_id = br.id and br.status=6 and brp.reason_id in(?)";
		            	msg[0]="该采购退货原因,";
		            	sql[1]="select id from problem_and_solver_record where processing_suggest_id in(?) limit 1";
		            	msg[1]="处理意见";	
		               	sql[2]="select id from after_sale_order where complaint_type_id in(?) limit 1";
		            	msg[2]="投诉分类";
		            	sql[3]="select id from after_sale_order where customer_bank_name in(?) limit 1";
		            	msg[3]="银行名称";
		            	sql[4]="select id from after_sale_cost_list where type_res in (?) limit 1";
		            	msg[4]="退换货原因";
		            	sql[5]="select id from after_sale_back_package where deliver in(?) limit 1";
		            	msg[5]="快递名称";
			            for(int i=0;i<sql.length;i++){
		            		service.getDbOp().prepareStatement(sql[i]);
		            		PreparedStatement plps = service.getDbOp().getPStmt();
		            		plps.setString(1, sb.toString());
		            		ResultSet rs = plps.executeQuery();
		            		int buy_return_id = 0 ; 
		            		if(rs.next()){
		            			buy_return_id = rs.getInt(1);
		            		}
		            		rs.close();
		            		plps.close();
		            		if(buy_return_id != 0 ){
		            			request.setAttribute("tip", "当前选择的有  已被使用的  "+msg[i]+"  无法删除  请从新选择");
		            			request.setAttribute("result", "failure");
		            			return;
		            		}
			            }
		            	
						service.getDbOp().startTransaction();
						for(int i=0; i<ids.length; i++){
							int id = StringUtil.toInt(ids[i]);
							if(id > 0){
								service.deleteTextRes("id = " + id);
							}
						}
						service.getDbOp().commitTransaction();
		            }
				}
			}catch(Exception e) {
				e.printStackTrace();
			}finally {
				service.releaseAll();
			
			}
	}
}
 
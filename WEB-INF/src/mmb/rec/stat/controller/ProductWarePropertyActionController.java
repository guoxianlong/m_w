package mmb.rec.stat.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.stock.stat.ProductWarePropertyService;
import mmb.stock.stat.ProductWareTypeBean;
import mmb.stock.stat.StatService;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

@Controller
@RequestMapping("/productWarePropertyActionController")
public class ProductWarePropertyActionController {
	private static byte[] lock = {};
	/**
	 * 商品物流分类优先级调整 -- 查询
	 * 2013-9-11
	 * 朱爱林
	 */
	@RequestMapping("/getProductWareTypeInfo")
	@ResponseBody
	public Object getProductWareTypeInfo(HttpServletRequest request,HttpServletResponse response){ 
		
		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		Map<String,Object> resultMap = new HashMap<String, Object>();
		List<Map<String,String>> lists = new ArrayList<Map<String,String>>();
		Map<String,String> map = null;
		resultMap.put("total","0");
		resultMap.put("rows",lists);
//		int pageIndex = StringUtil.parstInt(request.getParameter("pageIndex"));
//		int countPerPage = 20;
		List productWareTypeList = new ArrayList();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			String page1 = StringUtil.convertNull(request.getParameter("page"));
			String rows1 = StringUtil.convertNull(request.getParameter("rows"));
			int page = page1.equals("")?1:Integer.parseInt(page1);
			int rows = rows1.equals("")?20:Integer.parseInt(rows1);
			int start = (page-1)*rows;//开始下标
//			PagingBean paging = null;
			int totalCount = productWarePropertyService.getProductWareTypeCount("id<>0");
			resultMap.put("total", totalCount+"");
//			paging = new PagingBean(pageIndex, totalCount, countPerPage);
			productWareTypeList = productWarePropertyService.getProductWareTypeList("id<>0", start, rows, "id asc");
			if(productWareTypeList!=null&&productWareTypeList.size()>0){
				for( int i = 0; i < productWareTypeList.size(); i++ ) {
					ProductWareTypeBean pwtBean = (ProductWareTypeBean) productWareTypeList.get(i);
					map = new HashMap<String, String>();
					map.put("product_ware_id", pwtBean.getId()+"");//id 
					map.put("product_ware_name", pwtBean.getName());//商品物流分类 
					map.put("product_ware_sequence", pwtBean.getSequence()+"");//优先级
					lists.add(map);
				}
				resultMap.put("rows", lists);
			}
//			paging.setPrefixUrl("productWarePropertyAction.do?method=getProductWareTypeInfo");
//			request.setAttribute("paging", paging);
//			request.setAttribute("list", productWareTypeList);	
		} catch(Exception e ) {
			e.printStackTrace();
		} finally {
			statService.releaseAll();
		}
		return resultMap;
	}
	/**
	 * 商品物流分类优先级调整 -- 修改
	 * 2013-9-11
	 * 朱爱林
	 */
	@RequestMapping("/editProductWareTypeSequence")
	public void editProductWareTypeSequence(HttpServletRequest request,HttpServletResponse response)
		throws Exception{ 
	
		voUser user = (voUser)request.getSession().getAttribute("userView");
		StringBuilder result = new StringBuilder();
		response.setContentType("text/html;charset=UTF-8");
		if(user == null){
//			request.setAttribute("tip", "当前没有登录，操作失败！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'当前没有登录，操作失败！'}");
			response.getWriter().write(result.toString());
			return;
		}
		UserGroupBean group = user.getGroup();
		boolean editProductWarePropertyRight = group.isFlag(708);
		if( !editProductWarePropertyRight ) {
//			request.setAttribute("tip", "您没有修改商品物流分类优先级的权限！");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'您没有修改商品物流分类优先级的权限！'}");
			response.getWriter().write(result.toString());
			return;
		}
		String[] change = request.getParameterValues("change");
		boolean hasChange = false;
		WareService wareService = new WareService(); 
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			synchronized (lock) {
				if( change != null ) {
					productWarePropertyService.getDbOp().startTransaction();
					int changeCount = change.length;
					for ( int i = 0; i < changeCount; i++ ) {
						String temp = change[i];
						int id = StringUtil.parstInt(temp);
						int sequence = StringUtil.parstInt(request.getParameter("sequence_" + id));
						if( id == 0 || sequence <= 0 ) {
//							request.setAttribute("tip", "所传参数有误!");
//							request.setAttribute("result", "failure");
							productWarePropertyService.getDbOp().rollbackTransaction();
//							return mapping.findForward(IConstants.FAILURE_KEY);
							result.append("{result:'failure',tip:'所传参数有误!'}");
							response.getWriter().write(result.toString());
							return;
						}
						//查找是否还存在要修改的项目
						ProductWareTypeBean pwtBean = productWarePropertyService.getProductWareType("id="+id);
						if( pwtBean == null ) {
//							request.setAttribute("tip", "未找到要修改的项目!");
//							request.setattribute("result", "failure");
							productWarePropertyService.getDbOp().rollbackTransaction();
//							return mapping.findForward(IConstants.FAILURE_KEY);
							result.append("{result:'failure',tip:'未找到要修改的项目!'}");
							response.getWriter().write(result.toString());
							return;
						}
						if( pwtBean.getSequence() != sequence ) {
							//执行修改
							if(!productWarePropertyService.updateProductWareType("sequence=" + sequence, "id=" + id)) {
//								request.setAttribute("tip", "修改时，数据库操作失败!");
//								request.setAttribute("result", "failure");
								productWarePropertyService.getDbOp().rollbackTransaction();
//								return mapping.findForward(IConstants.FAILURE_KEY);
								result.append("{result:'failure',tip:'修改时，数据库操作失败!'}");
								response.getWriter().write(result.toString());
								return;
							}
							hasChange = true;
						}
					}
					productWarePropertyService.getDbOp().commitTransaction();
					productWarePropertyService.getDbOp().getConn().setAutoCommit(true);
				}
			}
		} catch(Exception e ) {
			e.printStackTrace();
//			request.setAttribute("tip", "修改商品物流分类优先级时发生了错误！");
//			request.setAttribute("result", "failure");
			productWarePropertyService.getDbOp().rollbackTransaction();
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'修改商品物流分类优先级时发生了错误！'}");
			response.getWriter().write(result.toString());
			return;
		} finally {
			productWarePropertyService.releaseAll();
		}
		if( hasChange ) {
			request.setAttribute("tip", "修改优先级成功!");
//			request.setAttribute("url", request.getContextPath()+"/admin/productWarePropertyAction.do?method=getProductWareTypeInfo");
//			return mapping.findForward("tip");
			result.append("{result:'success',tip:'修改优先级成功!'}");
			response.getWriter().write(result.toString());
			return;
		} else {
//			request.setAttribute("tip", "并没有任何修改!");
//			request.setAttribute("result", "failure");
//			return mapping.findForward(IConstants.FAILURE_KEY);
			result.append("{result:'failure',tip:'并没有任何修改!'}");
			response.getWriter().write(result.toString());
			return;
		}
		
	}
}

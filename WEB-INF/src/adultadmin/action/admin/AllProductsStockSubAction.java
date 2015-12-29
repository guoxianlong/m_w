/**
 * 
 */
package adultadmin.action.admin;

import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.Encoder;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cache.CatalogCache;
import cache.ProductLinePermissionCache;

/**
 * @author Bomb
 *
 */
public class AllProductsStockSubAction  extends BaseAction{
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		voUser adminUser = (voUser)request.getSession().getAttribute("userView");
		
		int countPerPage = 20;
		request.setCharacterEncoding("utf-8");
		int status = StringUtil.StringToId(request.getParameter("status"));//状态
		int proxy = StringUtil.toInt(request.getParameter("proxy"));//代理商	
		String code =StringUtil.dealParam(request.getParameter("code"));//编号
		if (code==null) code="";
		String name =StringUtil.dealParam(request.getParameter("name"));//名称
	
		name = Encoder.decrypt(name);//解码为中文
		if(name==null){//解码失败,表示已经为中文,则返回默认
			 name =StringUtil.dealParam(request.getParameter("name"));//名称
		}
		if (name==null) name="";
		int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));//分类1
		int parentId2 = StringUtil.StringToId(request.getParameter("parentId2"));//分类2
		int parentId3 = StringUtil.StringToId(request.getParameter("parentId3"));//分类2
		String toExcel =StringUtil.dealParam(request.getParameter("toExcel"));//编号
		
		//产品线权限限制
		String catalogIds1 = ProductLinePermissionCache.getCatalogIds1(adminUser);
		String catalogIds2 = ProductLinePermissionCache.getCatalogIds2(adminUser);
		String catalogIdsTemp = "";
		if(!catalogIds2.equals("")){
			String[] splits = catalogIds2.split(",");
			for(int i=0;i<splits.length;i++){
				voCatalog catalog = CatalogCache.getParentCatalog(Integer.parseInt(splits[i]));
				if(!StringUtil.hasStrArray(catalogIds1.split(","),String.valueOf(catalog.getId()))){
					catalogIdsTemp = catalogIdsTemp + catalog.getId() + ",";
				}
			}
			
			if(catalogIds1.endsWith(",")){
				catalogIds1 = catalogIds1.substring(0,catalogIds1.length()-1);
			}
		}
		
		StringBuffer condition = new StringBuffer();
		if(code!=null&&!code.trim().equals("")){
			condition.append(" and a.code='"+code+"'");
		}
		//名称匹配
		if(code.trim().equals("")&&name!=null&&!name.trim().equals("")){
			condition.append(" and ( a.oriname like '%"+name+"%' or a.name like '%"+name+"%')");
		}
		if(proxy>0){
			condition.append(" and d.id="+proxy);
		}
		if(status > 0){
			condition.append( " and a.status ="+status);
		}
		if(parentId1>0){
			condition.append(" and a.parent_id1="+parentId1);
		}else{
			condition.append(" and (a.parent_id1 in ("+catalogIds1+")");
		}
		if(parentId2>0){
			condition.append(" and a.parent_id2="+parentId2);
		}else{
			if(parentId1 > 0){
				if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
					condition.append(" and a.parent_id2 in ("+catalogIds2+")");
				}
			}else{
				condition.append(" or a.parent_id2 in ("+catalogIds2+"))");
			}
		}
		if(parentId3>0){
			condition.append(" and a.parent_id3="+parentId3);
		}
//		else{
//			if(parentId1 > 0){
//				if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1))){
//					condition.append(" and a.parent_id3 in ("+catalogIds2+")");
//				}
//			}else{
//				condition.append(" or a.parent_id3 in ("+catalogIds2+"))");
//			}
//		}	 
		DbOperation dbOperation = new DbOperation();
		dbOperation.init("adult_slave");
		WareService service = new WareService(dbOperation);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOperation);
		try {
			List totalList = service.getProductList2(condition.toString(), -1, -1, "a.id asc");
	        Iterator it = totalList.listIterator();
			while(it.hasNext()){
				voProduct product = (voProduct)it.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
			}
			request.setAttribute("totalList", totalList);//计算出要显示的总数			
			 //总数
	        int totalCount = totalList.size();
	        //页码
	        int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
	        PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
	        //为了分页显示
	        List list = service.getProductList2(condition.toString(), paging.getCurrentPageIndex() * countPerPage, countPerPage, "a.id asc");
	        Iterator iter = list.listIterator();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				product.setPsList(psService.getProductStockList("product_id=" + product.getId(), -1, -1, null));
			}
	        
			request.setAttribute("productList", list);
			request.setAttribute("status",""+status);			
			paging.setPrefixUrl("allProductsStockSub.do?status="+status+"&proxy="+proxy+"&code="
					+code+"&name="+Encoder.encrypt(name)+"&parentId1="+parentId1+"&parentId2="+parentId2+"&parentId3="+parentId3);//对中文进行编码,否则get提交为乱码
            request.setAttribute("paging", paging);
			request.setAttribute("date","status="+status+"&proxy="+proxy+"&code="+code+"&name="+Encoder.encrypt(name)+"&parentId1="+parentId1+"&parentId2="+parentId2+"&parentId3="+parentId3);
		} finally {
			psService.releaseAll();
		}
		//导出数据
		if(toExcel!=null&&toExcel.equals("to")){
			return mapping.findForward("toExcel");
		} 
		//显示数据
		return mapping.findForward(IConstants.SUCCESS_KEY);
	}
}

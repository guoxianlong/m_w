/**
 * 
 */
package adultadmin.action.admin;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * @author Bomb
 *  
 */
public class SearchProductAction extends BaseAction {

	/**
	 *  
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {

		voUser loginUser = (voUser)request.getSession().getAttribute("userView");
		UserGroupBean group = loginUser.getGroup();
		//销售类型
		int type = StringUtil.toInt(request.getParameter("type"));
		
		String proxyId = StringUtil.convertNull(request.getParameter("proxy"));
		String name = request.getParameter("name");
		String code = request.getParameter("code");
		//增加条码查询
		String barcode= StringUtil.dealParam(request.getParameter("barcode"));
		//增加产品ID查询
		int productId= StringUtil.toInt(StringUtil.dealParam(request.getParameter("productId")));
		String price = request.getParameter("price");
		String minPrice = request.getParameter("minPrice");
		String maxPrice = request.getParameter("maxPrice");
		String minPrice5 = StringUtil.convertNull(request.getParameter("minPrice5"));
		String maxPrice5 = StringUtil.convertNull(request.getParameter("maxPrice5"));
		int parentId1 = StringUtil.toInt(request.getParameter("parentId1"));
		int parentId2 = StringUtil.toInt(request.getParameter("parentId2"));
		int parentId3 = StringUtil.toInt(request.getParameter("parentId3"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		int brand = StringUtil.toInt(request.getParameter("brand")); //满折扣过来的有品牌值
		String catalogIds = StringUtil.convertNull(request.getParameter("catalogIds")); //满折扣过来的有一级分类多选
		//----cxq
		int minProductStock=StringUtil.StringToId(request.getParameter("minProductStock"));
		int maxProductStock=StringUtil.StringToId(request.getParameter("maxProductStock"));
		//主子商品，查询------flag=0或者2为主商品，1,3为子商品
		int productType=StringUtil.StringToId(request.getParameter("products"));

		if (name == null)
			name = "";
		if (code == null)
			code = "";
		if (barcode == null)
			barcode = "";
		if (price == null)
			price = "";
		if (minPrice == null)
			minPrice = "";
		if (maxPrice == null)
			maxPrice = "";

		String forward = StringUtil.dealParam(request.getParameter("forward"));

		String[] strProductIds = request.getParameterValues("id");
		String[] product_status = request.getParameterValues("product_status");
		int commodityStatus = StringUtil.parstBackMinus(request.getParameter("commodityStatus"));//售后过来的查询，需要查询订单状态少于100的 限制不要
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		WareService service = new WareService(dbOp);
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, psService.getDbOp());
		//        IPresentProductService presentService = ServiceFactory.createPresentProductService(IBaseService.CONN_IN_SERVICE, null);
		String a="0";
		request.setAttribute("listSum", a);
		try {
			List proxyList = service.getSelects("supplier_standard_info ", "where status=1 order by id");
			List statusList = service.getSelects("product_status","order by id");
			request.setAttribute("statusList", statusList);
			request.setAttribute("proxyList", proxyList);
			if (productId<0&&name.length() == 0 && code.length() == 0 && barcode.length() == 0 && price.length() == 0 && minPrice5.length() == 0 && maxPrice5.length() == 0 
					&& minPrice.length() == 0 && maxPrice.length() == 0 && parentId1 <= 0 && parentId2 <= 0 && parentId3 <= 0 && (strProductIds == null || strProductIds.length==0) 
					&& startTime.length() == 0 && endTime.length() == 0 && product_status == null && brand<=0 && catalogIds.length()==0 &&  proxyId.equals("")){
				if(StringUtil.isNull(forward)){
					return mapping.findForward(IConstants.SUCCESS_KEY);
				} else {
					request.getRequestDispatcher("/admin/searchProduct" + forward + ".jsp").forward(request, response);
					return null;
				}
			}

			StringBuilder buf = new StringBuilder();
			buf.append(" (pb.barcode_status is null or pb.barcode_status=0) ");
			if(strProductIds == null || strProductIds.length == 0){
				if(!StringUtil.isNull(name)){
					name = StringUtil.toSqlLike(request.getParameter("name"));
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" (a.name like '%");
					buf.append(name);
					buf.append("%' or a.oriname like '%");
					buf.append(name);
					buf.append("%') ");
				}
				if (!StringUtil.isNull(code)){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" a.code='");
					buf.append(code);
					buf.append("' ");
				}
				if (!StringUtil.isNull(barcode)){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" pb.barcode='");
					buf.append(barcode.trim());
					buf.append("'");
				}
				if (!StringUtil.isNull(price)) {
					if(buf.length() > 0){
						buf.append(" and ");
					}
					price = StringUtil.toSqlLike(request.getParameter("price"));
					buf.append(" (round(a.price,2) like '%");
					buf.append(price);
					buf.append("%' or round(a.price2,2) like '%");
					buf.append(price);
					buf.append("%')");
				}
				if (!StringUtil.isNull(minPrice)) {
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" (a.price >= ");
					buf.append(minPrice);
					buf.append(")");
				}
				if (!StringUtil.isNull(maxPrice)) {
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" (a.price <= ");
					buf.append(maxPrice);
					buf.append(")");
				}
				if (!minPrice5.equals("")) {
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" (a.price5 >= ");
					buf.append(minPrice5);
					buf.append(")");
				}
				if (!maxPrice5.equals("")) {
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" (a.price5 <= ");
					buf.append(maxPrice5);
					buf.append(")");
				}
				if(productId >= 0){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" a.id=");
					buf.append(productId);
				}
				if(parentId1 > 0){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" a.parent_id1=");
					buf.append(parentId1);
				}
				if(parentId2 > 0){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" a.parent_id2=");
					buf.append(parentId2);
				}
				if(parentId3 > 0){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" a.parent_id3=");
					buf.append(parentId3);
				}

				if (!StringUtil.isNull(startTime) &&!StringUtil.isNull(endTime)) {
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" (left(a.create_datetime,10) between '");
					buf.append(startTime);
					buf.append("' and '");
					buf.append(endTime);
					buf.append("')");
				}
				if(!group.isFlag(89)){
					if(buf.length() > 0){
						buf.append(" and ");
					}
					buf.append(" a.status <> 100");
				}

				if(product_status != null && product_status.length>0){
					buf.append(" and ");
					buf.append("b.id in (");
					for(int i=0; i<product_status.length; i++){
						if(i != 0){
							buf.append(",");
						}
						buf.append(product_status[i]);
					}
					buf.append(")");
				}
				if(brand>0){
					buf.append(" and ");
					buf.append("a.brand=");
					buf.append(brand);
				}
				if(parentId1 < 0){
					if(!catalogIds.trim().equals("")&&catalogIds.length()>0){
						buf.append(" and ");
						buf.append("a.parent_id1 in(");
						buf.append(catalogIds);
						buf.append(")");
					}
				}
				if(!proxyId.equals("")){
					buf.append(" and d.id = ");
					buf.append(proxyId);
					//params = params + "&proxy="+proxyId;
				}
	            if(commodityStatus>0){//缺货和废弃状态的商品不能被添加和搜索到
	            	buf.append(" and a.status<").append(commodityStatus);
	            }
	            //销售类型
	            if(type == 1 || type == 2){
	            	buf.append(" and psp.type = ").append(type);
	            }
        	} else {
				if(buf.length() > 0){
					buf.append(" and ");
				}
				buf.append("a.id in (");
				for(int i=0; i<strProductIds.length; i++){
					if(i != 0){
						buf.append(",");
					}
					buf.append(strProductIds[i]);
				}
				buf.append(")");
			}
			String flagPresent = request.getParameter("flagPresent");
			List list = null;

			if(flagPresent!=null && flagPresent.equals("present")){
				list = service.searchProduct(buf.toString(), 0, 0, "a.status asc,a.id desc",flagPresent);
			}else{
				list = service.searchProduct(buf.toString(), 0, 0, "a.status asc,a.id desc");
			}
			if(list==null){
				String b="0";
				request.setAttribute("listSum", b);
			}
			else{
				String listSum=String.valueOf(list.size());
				request.setAttribute("listSum", listSum);
			}
			Iterator iter = list.listIterator();
			List newList=new ArrayList();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				List psList = psService.getProductStockList("product_id=" + product.getId(), -1, -1, null);
				product.setPsList(psList);
				//--------------------------------
				//可发货总数
				if(maxProductStock>0){
					int stock=product.getStock(0, 0) + product.getStock(1, 0) + product.getStock(2, 0)+ product.getStock(3, 0);
					if(stock<=maxProductStock&&stock>=minProductStock){
						newList.add(product);
					}else{
						continue;
					}
				}
				//********************************
				product.setCargoPSList(cargoService.getCargoAndProductStockList("cps.product_id = "+product.getId(), -1, -1, "ci.whole_code asc"));
				//如果供应商查询条件不为空，则查找该产品所有的供应商并显示出来
				ResultSet rs = null;
				String sqlSupplier = "select GROUP_CONCAT(d.name)sName from product p left outer join product_supplier c on p.id=c.product_id left join supplier_standard_info d on d.id=c.supplier_id and d.status=1 " + " where  p.id=" + product.getId();
				rs = service.getDbOp().executeQuery(sqlSupplier);
				while (rs.next()) {
					String proxyName = StringUtil.convertNull(rs.getString("sName"));
					product.setProxyName(proxyName);
				}
				rs.close();
			}
			//cxq-----
			if(maxProductStock>0){
				list=newList;
			}
			if(maxProductStock>0){
				request.setAttribute("minProductStock", minProductStock+"");
				request.setAttribute("minProductStock", maxProductStock+"");
			}
			request.setAttribute("products", String.valueOf(productType));
				//-------主子商品
			String listSum=String.valueOf(list.size());
			request.setAttribute("listSum", listSum);
			//----***
			request.setAttribute("productList", list);

		}catch(Exception e){
			e.printStackTrace();
		}finally {
			psService.releaseAll();
		}
		//如果是从报损报溢跳转来的
		String frombybString = StringUtil.dealParam(request.getParameter("frombybs"));
		if(frombybString!=null)
		{
			if(StringUtil.isNull(forward)){
				return mapping.findForward("bsby");
			} else {
				request.getRequestDispatcher("/admin/searchProduct" + forward + ".jsp").forward(request, response);
				return null;
			}
		}else
		{
			if(StringUtil.isNull(forward)){
				return mapping.findForward(IConstants.SUCCESS_KEY);
			} else {
				request.getRequestDispatcher("/admin/searchProduct" + forward + ".jsp").forward(request, response);
				return null;
			}
		}
	}
}

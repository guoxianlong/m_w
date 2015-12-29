/**
 * 
 */
package adultadmin.action.admin;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import cache.CatalogCache;
import cache.ProductLinePermissionCache;

import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICatalogService;
import adultadmin.util.StatUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * @author Bomb
 *  
 */
public class SearchOrderProductHistoryAction extends BaseAction {

    /**
     *  
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    	
    	voUser adminUser = (voUser)request.getSession().getAttribute("userView");

        String name = request.getParameter("name");
        String code = request.getParameter("code");
        String oriname = request.getParameter("oriname");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
    	String areaGroup = request.getParameter("areaGroup");
    	String productLine = StringUtil.convertNull(request.getParameter("productLine"));
        if (name == null)
            name = "";
        if (code == null)
            code = "";
        if (oriname == null)
            oriname = "";
        if (startDate == null)
            startDate = "";
        if (endDate == null)
            endDate = "";
        int status = StringUtil.StringToId(request.getParameter("status"));
        //int parentId1 = StringUtil.StringToId(request.getParameter("parentId1"));
		List productLineList =ProductLinePermissionCache.getProductList(adminUser); //获取当前用户下 产品线 
		request.setAttribute("productLineList", productLineList);
		
        String[] parentId1s = new String[]{};  
        if(request.getParameterValues("parentId1")!=null){
        	parentId1s=request.getParameterValues("parentId1");
        }
        if (name.length() == 0 && code.length() == 0 && oriname.length() == 0
                && startDate.length() == 0 && endDate.length() == 0)
            return mapping.findForward(IConstants.SUCCESS_KEY);

        Date date1 = null;
        Date date2 = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (startDate.length() > 0) {
            try {
                date1 = sdf.parse(startDate);
            } catch (Exception e) {
                request.setAttribute("tip", "开始时间格式不对！");
                return mapping.findForward(IConstants.FAILURE_KEY);
            }
        }
        if (endDate.length() > 0) {
            try {
                date2 = sdf.parse(endDate);
            } catch (Exception e) {
                request.setAttribute("tip", "结束时间格式不对！");
                return mapping.findForward(IConstants.FAILURE_KEY);
            }
        }
        if (date1 != null && date2 != null) {
            if (date2.before(date1)) {
                request.setAttribute("tip", "结束时间必须在开始时间之后！");
                return mapping.findForward(IConstants.FAILURE_KEY);
            }
        }
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
        //产品的condition
        StringBuffer buf = new StringBuffer(128);
        StringBuilder orderBuf = new StringBuilder();
        orderBuf.append(" uo.status <> 10 ");
        if (startDate.length() > 0) {
        	int minId = StatUtil.getDateTimeFirstOrderId(startDate);
        	orderBuf.append(" and uo.id >= ");
        	orderBuf.append(minId);
        	orderBuf.append(" and left(uo.create_datetime, 10) >= '");
        	orderBuf.append(startDate);
        	orderBuf.append("'");
        }
        if (endDate.length() > 0) {
        	orderBuf.append(" and left(uo.create_datetime, 10) <= '");
        	orderBuf.append(endDate);
        	orderBuf.append("'");
        }
      //判断是否为无锡单
        String wuxiCondition="";
		
		if(areaGroup!=null && areaGroup.equals("0")){
			
		}
        if(areaGroup!=null && areaGroup.equals("1")){
        	orderBuf.append(" and uo.areano<>9 ");
		}
        if(areaGroup!=null && areaGroup.equals("2")){
        	orderBuf.append(" and uo.areano=9 ");
        }
        String orderCondition = orderBuf.toString();
        if (code.length() > 0) {
            buf.append(" and p.code = '");
            buf.append(code);
            buf.append("'");
        }
        if (oriname.length() > 0) {
            buf.append(" and p.oriname like '%");
            buf.append(oriname);
            buf.append("%'");
        }
        if (name.length() > 0) {
            buf.append(" and p.name like '%");
            buf.append(name);
            buf.append("%'");
        }
        if (status == 1) {
            buf.append(" and p.status >= 100");
        } else if(status == 0) {
            buf.append(" and p.status < 100");
        }
        
        
      //当产品分类数不为0且没有选中全部时查询
        if (parentId1s.length != 0 && !parentId1s[0].equals("0")) {
			String[] catalogIds2Array = catalogIds2.split(",");
			String ids2 = "-1";
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < parentId1s.length; i++) {
				if(StringUtil.hasStrArray(catalogIdsTemp.split(","),String.valueOf(parentId1s[i]))){
					for(int j = 0; j < catalogIds2Array.length; j++){
						if(CatalogCache.getParentCatalog(Integer.parseInt(catalogIds2Array[j])).getId()==Integer.parseInt(parentId1s[i])){
							ids2 = ids2+","+catalogIds2Array[j];
						}
					}
					continue;
				}
				sb.append(parentId1s[i] + ",");
			}
			if(sb.length()>0){
				sb.deleteCharAt(sb.length() - 1);
			}else{
				sb.append("-1");
			}
			String parentId1 = sb.toString();
			buf.append(" and (p.parent_id1 in (" + parentId1 + ") or p.parent_id2 in (" + ids2 + "))");
		}else{
			buf.append(" and (p.parent_id1 in (" + catalogIds1 + ") or p.parent_id2 in (" + catalogIds2 + "))");
		}
        
        if(productLine.length()>0){//产品线判断
        	buf.append(" "+ProductLinePermissionCache.getCarglogIdsByProudctId(productLine));
		}
        
        // 构造查询条件
        //货品
        String query = "select p.id, p.name, p.oriname, p.code, p.price, p.price3, p.parent_id1, p.parent_id2, sum(uop.count) s from user_order_product uop join user_order uo on uop.order_id=uo.id join product p on uop.product_id=p.id ";
        query = query + " where "+orderCondition;
        if(buf.length() > 0){
        	query = query + buf.toString();
        }
        query += " group by p.id";
        //System.out.println("--> "+query);
        DbOperation dbOp = new DbOperation();
        try {
            dbOp.init("adult_slave2");
            ResultSet rs = dbOp.executeQuery(query);
            LinkedHashMap productMap = new LinkedHashMap();
            voProduct product = null;
            while (rs.next()) {
                product = new voProduct();
                product.setId(rs.getInt("p.id"));
                product.setName(rs.getString("p.name"));
                product.setOriname(rs.getString("p.oriname"));
                product.setBuyCount(rs.getInt("s"));
                product.setCode(rs.getString("p.code"));
                product.setPrice(rs.getFloat("p.price"));
                product.setPrice3(rs.getFloat("p.price3"));
                product.setParentId1(rs.getInt("p.parent_id1"));
                product.setParentId2(rs.getInt("p.parent_id2"));
                productMap.put(Integer.valueOf(product.getId()), product);
            }
            
            query = "select p.id, p.name, p.oriname, p.code, p.price, p.price3, p.parent_id1, p.parent_id2, sum(uop.count) s from user_order_present uop join user_order uo on uop.order_id=uo.id join product p on uop.product_id=p.id ";
            query = query + " where "+orderCondition;
            if(buf.length() > 0){
            	query = query + buf.toString();
            }
            query += " group by p.id";
            rs = dbOp.executeQuery(query);
            while (rs.next()) {
            	product = (voProduct)productMap.get(Integer.valueOf(rs.getInt("p.id")));
            	if(product != null){
            		product.setBuyCount(product.getBuyCount()+rs.getInt("s"));
            	}else{
            		product = new voProduct();
                    product.setId(rs.getInt("p.id"));
                    product.setName(rs.getString("p.name"));
                    product.setOriname(rs.getString("p.oriname"));
                    product.setBuyCount(rs.getInt("s"));
                    product.setCode(rs.getString("p.code"));
                    product.setPrice(rs.getFloat("p.price"));
                    product.setPrice3(rs.getFloat("p.price3"));
                    product.setParentId1(rs.getInt("p.parent_id1"));
                    product.setParentId2(rs.getInt("p.parent_id2"));
                    productMap.put(Integer.valueOf(product.getId()), product);
            	}
            }
            
            ICatalogService catalogService = ServiceFactory
                    .createCatalogService(IBaseService.CONN_IN_SERVICE, dbOp);
            List catalogList = catalogService.getCatalogList(null, -1, -1,
                    "id asc");
            HashMap catalogMap = new HashMap();
            Iterator iter = catalogList.listIterator();
            while (iter.hasNext()) {
                voCatalog catalog = (voCatalog) iter.next();
                catalogMap.put(Integer.valueOf(catalog.getId()), catalog);
            }
            request.setAttribute("productMap", productMap);
            request.setAttribute("catalogMap", catalogMap);
        } finally {
            dbOp.release();
        }

        return mapping.findForward(IConstants.SUCCESS_KEY);
    }
}
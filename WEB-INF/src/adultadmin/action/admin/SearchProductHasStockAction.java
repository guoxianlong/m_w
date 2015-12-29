/**
 * 
 */
package adultadmin.action.admin;

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

import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.cargo.CargoInfoAreaBean;
import adultadmin.bean.cargo.CargoInfoBean;
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
public class SearchProductHasStockAction extends BaseAction {

    /**
     *  
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response)
            throws Exception {

    	voUser loginUser = (voUser)request.getSession().getAttribute("userView");
    	UserGroupBean group = loginUser.getGroup();

    	String name = request.getParameter("name");
        String code = request.getParameter("code");
        String price = request.getParameter("price");
        String minPrice = request.getParameter("minPrice");
        String maxPrice = request.getParameter("maxPrice");
        int parentId1 = StringUtil.toInt(request.getParameter("parentId1"));
        int parentId2 = StringUtil.toInt(request.getParameter("parentId2"));
        int stockArea = StringUtil.toInt(request.getParameter("stockArea"));
        int stockType = StringUtil.toInt(request.getParameter("stockType"));
        if (name == null)
            name = "";
        if (code == null)
            code = "";
        if (price == null)
            price = "";
        if (minPrice == null)
            minPrice = "";
        if (maxPrice == null)
            maxPrice = "";

        String forward = StringUtil.dealParam(request.getParameter("forward"));

        if (name.length() == 0 && code.length() == 0 && price.length() == 0
                && minPrice.length() == 0 && maxPrice.length() == 0 && parentId1 <= 0 && parentId2 <= 0 && stockArea < 0 && stockType < 0){
            if(StringUtil.isNull(forward)){
            	return mapping.findForward(IConstants.SUCCESS_KEY);
            } else {
            	request.getRequestDispatcher("/admin/searchProduct" + forward + ".jsp").forward(request, response);
            	return null;
            }
        }

        DbOperation dbOp = new DbOperation();
        dbOp.init("adult_slave");
        WareService service = new WareService(dbOp);
        IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
        ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE, psService.getDbOp());
        try {

        	StringBuilder buf = new StringBuilder();
        	if(!StringUtil.isNull(name)){
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
            if (!StringUtil.isNull(price)) {
        		if(buf.length() > 0){
        			buf.append(" and ");
        		}
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
            if(!group.isFlag(89)){
            	if(buf.length() > 0){
        			buf.append(" and ");
        		}
            	buf.append(" a.status <> 100");
            }
            if(stockArea >= 0){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append(" b.area=" + stockArea);
            }
            if(stockArea >= 0){
            	if(buf.length() > 0){
            		buf.append(" and ");
            	}
            	buf.append(" b.type=" + stockType);
            }

            String sql = "select a.*, c.name, sum(b.stock) stock_count from product a join product_stock b on a.id=b.product_id join product_status c on a.status=c.id where " + buf.toString() + " group by a.id having stock_count > 0 order by a.status asc,a.id desc";
            ResultSet rs = psService.getDbOp().executeQuery(sql);
            List list = new ArrayList();
            while (rs.next()) {
                list.add(getProductsItem(rs));
            }

            Map psMap = new HashMap();
            Iterator iter = list.listIterator();
            while(iter.hasNext()){
            	voProduct product = (voProduct)iter.next();
            	List psList = psService.getProductStockList("product_id=" + product.getId(), -1, -1, null);
            	product.setPsList(psList);
            }
            
            //查询产品源库货位库存信息
            HashMap cpsListMap = new HashMap();
            StringBuilder cpsBuff = new StringBuilder();
            if(stockArea >= 0){
            	CargoInfoAreaBean outArea = cargoService.getCargoInfoArea("old_id = "+stockArea);
            	if(cpsBuff.length()>0){
            		cpsBuff.append(" and ");
            	}
            	cpsBuff.append("ci.area_id = " + outArea.getId());
            }
            if(stockType >= 0){
            	if(cpsBuff.length()>0){
            		cpsBuff.append(" and ");
            	}
            	cpsBuff.append("ci.stock_type = " + stockType);
            	if(stockType == 0){
            		cpsBuff.append(" and ci.store_type <> " + CargoInfoBean.STORE_TYPE2);
            	}
            }
            if(cpsBuff.length()>0){
        		cpsBuff.append(" and ");
        	}
            cpsBuff.append("cps.stock_count > 0");
            iter = list.listIterator();
            while(iter.hasNext()){
            	voProduct product = (voProduct)iter.next();
            	String cpsCondition = "";
            	if(cpsBuff.length()>0){
            		cpsCondition = cpsBuff.toString()+" and cps.product_id = "+product.getId();
            	}else{
            		cpsCondition = " cps.product_id = "+product.getId();
            	}
            	List cpsOutList = cargoService.getCargoAndProductStockList(cpsCondition, -1, -1, "ci.whole_code asc");
            	cpsListMap.put(String.valueOf(product.getId()), cpsOutList);
            }

            request.setAttribute("productList", list);
            request.setAttribute("cpsListMap", cpsListMap);

        } catch(Exception e){
        	e.printStackTrace();
        } finally {
            service.releaseAll();
        }

        if(StringUtil.isNull(forward)){
        	return mapping.findForward(IConstants.SUCCESS_KEY);
        } else {
        	request.getRequestDispatcher("/admin/searchProduct" + forward + ".jsp").forward(request, response);
        	return null;
        }
    }

    public voProduct getProductsItem(ResultSet rs) {
        voProduct vo = new voProduct();
        try {
            vo.setId(rs.getInt("a.id"));
            vo.setName(rs.getString("a.name"));
            vo.setOriname(rs.getString("a.oriname"));
            vo.setPrice(rs.getFloat("a.price"));
            vo.setPrice2(rs.getFloat("a.price2"));
            vo.setPrice3(rs.getFloat("a.price3"));
            vo.setPrice4(rs.getFloat("a.price4"));
            vo.setPrice5(rs.getFloat("a.price5"));
            vo.setDeputizePrice(rs.getFloat("a.deputize_price"));
            vo.setPic(rs.getString("a.pic"));
            vo.setPic2(rs.getString("a.pic2"));
            vo.setPic3(rs.getString("a.pic3"));
            vo.setCommentCount(rs.getInt("a.comment_count"));
            vo.setClickCount(rs.getInt("a.click_count"));
            vo.setBuyCount(rs.getInt("a.buy_count"));
            vo.setCreateDatetime(rs.getTimestamp("a.create_datetime"));
            vo.setStatus(rs.getInt("a.status"));
            vo.setCode(rs.getString("a.code"));
            vo.setProxyId(rs.getInt("a.proxy_id"));
            vo.setProxysName(rs.getString("a.proxys_name"));
            vo.setIsPackage(rs.getInt("a.is_package"));
            vo.setHasPresent(rs.getInt("a.has_present"));
            vo.setStatusName(rs.getString("c.name"));
            vo.setStock(rs.getInt("a.stock"));
            vo.setStockGd(rs.getInt("a.stock_gd"));
            vo.setImages(rs.getString("a.images"));
            vo.setRank(rs.getInt("a.rank"));
            vo.setDisplayOrder(rs.getInt("a.display_order"));
            vo.setTopOrder(rs.getInt("a.top_order"));
            vo.setStockLineBj(rs.getInt("a.stock_line_bj"));
            vo.setStockLineGd(rs.getInt("a.stock_line_gd"));
            vo.setStockStandardBj(rs.getInt("a.stock_standard_bj"));
            vo.setStockStandardGd(rs.getInt("a.stock_standard_gd"));
            vo.setStockStatus(rs.getInt("a.stock_status"));
            vo.setParentId1(rs.getInt("a.parent_id1"));
            vo.setParentId2(rs.getInt("a.parent_id2"));
            vo.setBrand(rs.getInt("a.brand"));
            vo.setUnit(rs.getString("a.unit"));
            vo.setShowPackage(rs.getInt("a.show_package"));
            vo.setBjStockin(rs.getString("a.bj_Stockin"));
            vo.setGdStockin(rs.getString("a.gd_Stockin"));
            vo.setStockDayBj(rs.getInt("a.stock_day_bj"));
            vo.setStockDayGd(rs.getInt("a.stock_day_gd"));
            vo.setStockBjBad(rs.getInt("a.stock_bj_bad"));
            vo.setStockBjRepair(rs.getInt("a.stock_bj_repair"));
            vo.setStockGdBad(rs.getInt("a.stock_gd_bad"));
            vo.setStockGdRepair(rs.getInt("a.stock_gd_repair"));
            vo.setBaozhuangzhongliang(rs.getString("a.baozhuangzhongliang"));
            vo.setChanpinzhongliang(rs.getString("a.chanpinzhongliang"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vo;
    }
}

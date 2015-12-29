package mmb.rec.stat.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mmb.stock.stat.CheckEffectBean;
import mmb.stock.stat.ProductWarePropertyBean;
import mmb.stock.stat.ProductWarePropertyService;
import mmb.stock.stat.ProductWareTypeBean;
import mmb.stock.stat.StatService;
import mmb.ware.WareService;
import net.sf.json.JSONArray;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.framework.IConstants;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBarcodeCreateManagerService;
import adultadmin.service.infc.IBaseService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 商品物流属性控制器
 * 
 * @author zhuguofu
 * 
 */
@Controller
@RequestMapping("admin/productWareProperty")
public class ProductWarepropertyController {

	@RequestMapping("openProductWarePropertyPage")
	public String openProductWarePropertyPage(HttpServletRequest request) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return "/admin/error";
		}
		return "/admin/rec/stat/productwareproperty/productWarePropertyList";
	}

	@RequestMapping("getPropertyListAll")
	@ResponseBody
	public String getPropertyList(HttpServletRequest request, int rows, int page) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
		}

		UserGroupBean group = user.getGroup();
		boolean seeProductWarePropertyRight = group.isFlag(695);
		if (!seeProductWarePropertyRight) {
			request.setAttribute("tip", "您没有查看商品物流属性的权限！");
			request.setAttribute("result", "failure");
		}

		String productCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productCode")));
		String productBarCode = StringUtil.toSql(StringUtil.convertNull(request
				.getParameter("productBarCode")));
		int checkEffectId = StringUtil.parstInt(request
				.getParameter("checkEffect"));
		int wareType = StringUtil.parstInt(request.getParameter("wareType"));

		List productWarePropertyList = new ArrayList();
		List checkEffectList = new ArrayList();
		List productWareTypeList = new ArrayList();
		StringBuilder condition = new StringBuilder();
		StringBuilder params = new StringBuilder();
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		ProductWarePropertyService productWarePropertyService = new ProductWarePropertyService(
				IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		IBarcodeCreateManagerService bService = ServiceFactory
				.createBarcodeCMServcie(IBaseService.CONN_IN_SERVICE,
						wareService.getDbOp());
		try {
			PagingBean paging = null;
			if (!productCode.equals("")) {
				params.append("&");
				params.append("productCode=" + productCode);

				voProduct product = wareService.getProduct(productCode);
				if (product == null) {
					if (condition.length() > 0) {
						condition.append(" and");
						condition.append(" id = -1");
					} else {
						condition.append(" id = -1");
					}
				} else {
					if (condition.length() > 0) {
						condition.append(" and");
						condition.append(" product_id = " + product.getId());
					} else {
						condition.append(" product_id = " + product.getId());
					}
				}
			}

			if (!productBarCode.equals("")) {
				params.append("&");
				params.append("productBarCode=" + productBarCode);

				ProductBarcodeVO bBean = bService.getProductBarcode("barcode="
						+ "'" + StringUtil.toSql(productBarCode) + "'");
				if (bBean == null) {
					if (condition.length() > 0) {
						condition.append(" and");
						condition.append(" id = -1");
					} else {
						condition.append(" id = -1");
					}
				} else {
					voProduct product = wareService.getProduct(bBean
							.getProductId());
					if (product == null) {
						if (condition.length() > 0) {
							condition.append(" and");
							condition.append(" id = -1");
						} else {
							condition.append(" id = -1");
						}
					} else {
						if (condition.length() > 0) {
							condition.append(" and");
							condition
									.append(" product_id = " + product.getId());
						} else {
							condition
									.append(" product_id = " + product.getId());
						}
					}
				}
			}

			if (checkEffectId != 0 && checkEffectId != -1) {
				params.append("&");
				params.append("checkEffect=" + checkEffectId);

				if (condition.length() > 0) {
					condition.append(" and");
					condition.append(" check_effect_id = " + checkEffectId);
				} else {
					condition.append(" check_effect_id = " + checkEffectId);
				}

			}

			if (wareType != 0 && wareType != -1) {
				params.append("&");
				params.append("wareType=" + wareType);

				if (condition.length() > 0) {
					condition.append(" and");
					condition.append(" product_type_id = " + wareType);
				} else {
					condition.append(" product_type_id = " + wareType);
				}

			}
			String conditionS = "id<>0";
			if (condition.length() > 0) {
				conditionS = condition.toString();
			}

			int totalCount = statService
					.getProductWarePropertyCount(conditionS);
			productWarePropertyList = statService.getProductWarePropertyList(
					conditionS, (page-1) * rows,
					rows, "id asc");
			int x = productWarePropertyList.size();
			for (int i = 0; i < x; i++) {
				ProductWarePropertyBean pwpBean = (ProductWarePropertyBean) productWarePropertyList
						.get(i);
				voProduct temp = wareService.getProduct(pwpBean.getProductId());
				if (temp == null) {
					temp = new voProduct();
				}
				pwpBean.setProduct(temp);
				CheckEffectBean cfBean = statService.getCheckEffect("id="
						+ pwpBean.getCheckEffectId());
				if (cfBean == null) {
					cfBean = new CheckEffectBean();
				}
				pwpBean.setCheckeEffect(cfBean);
				ProductWareTypeBean pwtBean = productWarePropertyService
						.getProductWareType("id=" + pwpBean.getProductTypeId());
				if (pwtBean == null) {
					pwtBean = new ProductWareTypeBean();
					pwtBean.setName("");
				}
				pwpBean.setProductWareType(pwtBean);
			}
			checkEffectList = statService.getCheckEffectList("id<>0", -1, -1,
					"id asc");
			productWareTypeList = productWarePropertyService
					.getProductWareTypeList("id<>0", -1, -1, "id asc");

			request.setAttribute("productWareTypeList", productWareTypeList);
			request.setAttribute("checkEffectList", checkEffectList);
			
			request.setAttribute("paging", paging);
			request.setAttribute("list", productWarePropertyList);
			StringBuilder sb = new StringBuilder();
			sb.append("{\"total\":" + totalCount);
			sb.append(",\"rows\":"+JSONArray.fromObject(productWarePropertyList).toString());
			sb.append("}");
			System.out.println(sb.toString());
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			wareService.getDbOp().release();
		}
		return "";
	}
	
	@RequestMapping("getPropertyDeatil")
	@ResponseBody
	public String getPropertyDeatil(HttpServletRequest request,int id){
		DbOperation dbSlave = new DbOperation();
		dbSlave.init(DbOperation.DB_SLAVE);
		WareService wareService = new WareService(dbSlave);
		StatService statService = ServiceFactory.createStatServiceStat(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try{
		ProductWarePropertyBean pwpBean = statService.getProductWareProperty("id = " + id);
		return JSONArray.fromObject(pwpBean).toString();
		}catch (Exception e) {
			// TODO: handle exception
		}finally{
			wareService.getDbOp().release();
		}
		return "";
	}

}

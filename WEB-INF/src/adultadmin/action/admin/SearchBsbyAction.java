package adultadmin.action.admin;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.action.bybs.ByBsAction;
import adultadmin.action.vo.voUser;
import adultadmin.bean.PagingBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.cargo.CargoInventoryBean;
import adultadmin.framework.BaseAction;
import adultadmin.framework.IConstants;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import mmb.ware.WareService;

public class SearchBsbyAction extends BaseAction  {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
	throws Exception {
		

		voUser user = (voUser)request.getSession().getAttribute("userView");
		if(user == null){
			request.setAttribute("tip", "当前没有登录，操作失败！");
			request.setAttribute("result", "failure");
			return mapping.findForward("failure");
		}
		UserGroupBean group = user.getGroup();
		boolean viewAll = group.isFlag(75);
		
		String code = StringUtil.convertNull(request.getParameter("code"));
		String startTime = StringUtil.convertNull(request.getParameter("startTime"));
		String endTime = StringUtil.convertNull(request.getParameter("endTime"));
		String auditStartTime = StringUtil.convertNull(request.getParameter("auditStartTime"));
		String auditEndTime = StringUtil.convertNull(request.getParameter("auditEndTime"));
		String[] statuss = request.getParameterValues("status");
		String type = StringUtil.convertNull(request.getParameter("type"));
		String remark = StringUtil.convertNull(request.getParameter("remark"));
		String warehouseArea = StringUtil.convertNull(request.getParameter("warehouseArea"));
		String warehouseType = StringUtil.convertNull(request.getParameter("warehouseType"));
		String productName = StringUtil.convertNull(request.getParameter("productName"));
		String productCode = StringUtil.convertNull(request.getParameter("productCode"));
		String sourceCode = StringUtil.convertNull(request.getParameter("sourceCode"));
		String status = "";
		if(statuss!=null){
			for(int i=0;i<statuss.length;i++){
				status = status + statuss[i] +",";
			}
			if(status.endsWith(",")){
				status = status.substring(0, status.length()-1);
			}
		}
		String ids = "";
		String params = "";
		
		DbOperation dbOp = new DbOperation();
		dbOp.init("adult_slave");
		IBsByServiceManagerService service = ServiceFactory.createBsByServiceManagerService(IBaseService.CONN_IN_SERVICE,dbOp);
		ICargoService cargoService = ServiceFactory.createCargoService(IBaseService.CONN_IN_SERVICE,dbOp);
		WareService wareService = new WareService(dbOp);
		try{
			StringBuilder buff = new StringBuilder();
			if(!code.equals("")){
				params = params + "code="+code+"&";
				buff.append("receipts_number = '"+code+"'");
				buff.append(" and ");
			}
			if(!remark.equals("")){
				params = params + "remark="+remark+"&";
				buff.append("remark = '"+remark+"'");
				buff.append(" and ");
			}
			if(!startTime.equals("")&&!endTime.equals("")){
				params = params + "startTime="+startTime+"&endTime="+endTime+"&";
				startTime = startTime + " 00:00:00";
				endTime = endTime + " 23:59:59";
				buff.append("add_time between '"+startTime+"' and '"+endTime+"'");
				buff.append(" and ");
			}
			if(!auditStartTime.equals("") && !auditEndTime.equals("")){
				params = params + "auditStartTime="+auditStartTime+"&auditEndTime="+auditEndTime+"&";
				auditStartTime = auditStartTime + " 00:00:00";
				auditEndTime = auditEndTime + " 23:59:59";
				buff.append("end_time between '"+auditStartTime+"' and '"+auditEndTime+"'");
				buff.append(" and ");
			}
			if(!status.equals("")){
				params = params + "status="+status+"&";
				buff.append("current_type in ("+status+")");
				buff.append(" and ");
			}
			if(!type.equals("")){
				params = params + "type="+type+"&";
				buff.append("type in ("+type+")");
				buff.append(" and ");
			}
			if(!warehouseArea.equals("")&&!warehouseArea.equals("-1")){
				params = params + "warehouseArea="+warehouseArea+"&";
				buff.append("warehouse_area in ("+warehouseArea+")");
				buff.append(" and ");
			}
			if(!warehouseType.equals("")){
				params = params + "warehouseType="+warehouseType+"&";
				buff.append("warehouse_type in ("+warehouseType+")");
				buff.append(" and ");
			}
			if(!sourceCode.equals("")){
				CargoInventoryBean inventory = cargoService.getCargoInventory("code = '"+sourceCode+"'");
				if(inventory!=null){
					buff.append("source =");
					buff.append(inventory.getId());
					buff.append(" and ");
				}else{
					buff.append("source =");
					buff.append(-1);
					buff.append(" and ");
				}
				params = params + "sourceCode="+sourceCode+"&";
			}
			if(!(productName.equals("")&&productCode.equals(""))&&!ids.equals("-1,")){
				params = params + "productName="+productName+"&";
				List idList = null;
				if(!ids.equals("")){
					if(ids.endsWith(",")){
						ids = ids.substring(0, ids.length()-1);
					}
					idList = service.getFieldList(
								"operation_id", "bsby_product", "product_id in (select id from product where"+" operation_id in ("+ids+")"+(productName.equals("")?"":" and oriname like '%"+productName+"%'")+(productCode.equals("")?"":" and code='"+productCode+"'")+")", -1, -1, "operation_id", "operation_id", "int");
					ids = "";
				}else{
					idList = service.getFieldList(
								"operation_id", "bsby_product", "product_id in (select id from product where 1=1"+(productName.equals("")?"":" and oriname like '%"+productName+"%'")+(productCode.equals("")?"":" and code='"+productCode+"'")+")", -1, -1, "operation_id", "operation_id", "int");
				}
				
				for(int i=0;i<idList.size();i++){
					int id = ((Integer)idList.get(i)).intValue();
					if(ids.indexOf(id+",")==-1){
						ids = ids + id + ",";
					}
				}
				if(ids.indexOf("-1,")==-1){
					ids = ids + "-1,";
				}
			}

			if(!ids.equals("")){
				ids = ids.substring(0, ids.length()-1);
				buff.append("id in ("+ids+")");
			}
			String condition = "if_del=0 ";
			if(buff.length() > 0){
				condition = condition + " and " + buff.toString();
				if(condition.endsWith(" and ")){
					condition = condition.substring(0,condition.lastIndexOf(" and "));
				}
				if(params.endsWith("&")){
					params = "?"+params.substring(0, params.length()-1);
				}
			}
//			String sql = "select bo.receipts_number,bo.warehouse_type,bo.warehouse_area,bo.id,bo.add_time,bo.operator_name,bo.type,bo.end_time,bo.end_oper_name,(GROUP_CONCAT)ps.supplier_name as name from bsby_operationnote bo " +
//			"join product pt on pt .code =bo.id join product_supplier ps on ps.product_id=bo.id where "+condition+
//			" group by ps.supplier_name order by add_time desc limit 0, 50";
//	ResultSet rs = service.getDbOp().executeQuery(sql);
//	List bsbyOperationnoteList = new ArrayList();
//    while (rs.next()) {
//		BsbyOperationnoteBean bsbyOperationnoteBean = new BsbyOperationnoteBean();
//		voProductSupplier productSupplierBean = new voProductSupplier();
//		bsbyOperationnoteBean.setReceipts_number(rs.getString("bo.receipts_number"));
//		bsbyOperationnoteBean.setWarehouse_area(rs.getInt("bo.warehouse_area"));
//		bsbyOperationnoteBean.setWarehouse_type(rs.getInt("bo.warehouse_type"));
//		bsbyOperationnoteBean.setId(rs.getInt("bo.id"));
//		bsbyOperationnoteBean.setAdd_time(rs.getString("bo.add_time"));
//		bsbyOperationnoteBean.setOperator_name(rs.getString("bo.operator_name"));
//		bsbyOperationnoteBean.setType(rs.getInt("bo.type"));
//		bsbyOperationnoteBean.setEnd_time(rs.getString("bo.end_time"));
//		bsbyOperationnoteBean.setEnd_oper_name(rs.getString("bo.end_oper_name"));
//		productSupplierBean.setSupplier_name(rs.getString("name"));
//		bsbyOperationnoteBean.setProductSupplierBean(productSupplierBean);
//		bsbyOperationnoteList.add(bsbyOperationnoteBean);
//	}
//	////////////////////////////////////////////////////////////////////
			int countPerPage = 50;
			// 分页显示所有的报溢报损的单据 状态是if_del=0 就是没有被删除的单据
			try {
				int totalCount = service.getByBsOperationnoteCount(condition);
				// 页码
				int pageIndex = StringUtil.StringToId(request.getParameter("pageIndex"));
				PagingBean paging = new PagingBean(pageIndex, totalCount, countPerPage);
				List list=null;
				if(StringUtil.convertNull(request.getParameter("excel")).equals("0")||StringUtil.convertNull(request.getParameter("excel")).equals("")){
					 list= service.getByBsOperationnoteList(condition, paging.getCurrentPageIndex() * countPerPage,
							countPerPage, "add_time desc");
				}
				if(StringUtil.convertNull(request.getParameter("excel")).equals("1")){
					list= service.getByBsOperationnoteList(condition,-1,
							-1, "add_time desc");
					List alist=new ArrayList();
					Map map=null;
					if(list!=null&&list.size()>0){
						ResultSet rs=null;
						for(int i=0;i<list.size();i++){
							map=new HashMap();
							
							BsbyOperationnoteBean bean=(BsbyOperationnoteBean)list.get(i);
							String sqlcargocode="select count,whole_code from bsby_product_cargo " +
									" join cargo_info on cargo_id=cargo_info.id " +
									"where bsby_oper_id="+bean.getId();
							map.put("billCode", bean.getReceipts_number());
							map.put("stockType", bean.getWarehouse_type()+"");
							map.put("stockArea", bean.getWarehouse_area()+"");
							map.put("id", bean.getId()+"");
							map.put("userName", bean.getOperator_name());
							map.put("currentType", bean.getCurrent_type()+"");
							map.put("type", bean.getType()+"");
							int count=0;
							String wholeCode="";
							service.getDbOp().prepareStatement(sqlcargocode);
							rs=service.getDbOp().getPStmt().executeQuery();
							if(rs.next()){
								count=rs.getInt("count");
								wholeCode=rs.getString("whole_code");
							}
							map.put("count", count+"");
							map.put("wholeCode", wholeCode);
							alist.add(map);
							
							
						}
						request.setAttribute("alist", alist);
						
					}
				}
				
				for(int i=0;i<list.size();i++){
					BsbyOperationnoteBean bean = (BsbyOperationnoteBean)list.get(i);
					CargoInventoryBean inventory = cargoService.getCargoInventory("id = "+bean.getSource());
					HashMap pmap = ByBsAction.getProductListByOperationId(bean.getId(),bean.getCurrent_type());
					bean.setProductCode(pmap.get("pCode").toString());
					bean.setBsbyCount(pmap.get("pCount").toString());
					bean.setPrice(pmap.get("ptax").toString());
					bean.setAllPrice(pmap.get("pSumTax").toString());
					bean.setOriname(pmap.get("oriName").toString());
					bean.setCargoCode(pmap.get("cargoCode").toString());
					bean.setPriceNotOfTax(pmap.get("pNotax").toString());
					bean.setAllPriceNotOfTax(pmap.get("pSumNoTax").toString());
					bean.setParentName1(pmap.get("parentName1").toString());
					bean.setParentName2(pmap.get("parentName2").toString());
					bean.setParentName3(pmap.get("parentName3").toString());
					bean.setProductLine(pmap.get("productLine").toString());
					if(inventory != null){
						bean.setSourceCode(inventory.getCode());
					}
				}
				paging.setPrefixUrl("searchBsby.do"+params);
				request.setAttribute("list", list);
		
				request.setAttribute("paging", paging);
				
				List bsbyReasonList = null;
				bsbyReasonList= service.getBsbyReasonListDistinct();
				request.setAttribute("bsbyReasonList", bsbyReasonList);
				
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
			
		}finally{
			service.releaseAll();
		}
		if(StringUtil.convertNull(request.getParameter("excel")).equals("1")){
			return mapping.findForward("excelbsbylist");
		}else{
			return mapping.findForward(IConstants.SUCCESS_KEY);
		}
	}
}

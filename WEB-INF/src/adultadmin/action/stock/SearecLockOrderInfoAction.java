package adultadmin.action.stock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import adultadmin.bean.stock.LockOderBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.framework.BaseAction;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;

public class SearecLockOrderInfoAction extends BaseAction {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String area = StringUtil.dealParam(request.getParameter("area"));//地区
		int productId = StringUtil.toInt(request.getParameter("productId"));//产品ID
		int type = StringUtil.toInt(request.getParameter("type"));//类型
	
			IProductStockService service=ServiceFactory.createProductStockService();
			ProductStockBean psb=service.getProductStock("product_id= "+productId+" and area="+area+" and type="+type);
			service.releaseAll();
			//System.out.println("==>"+psb.getId()+"--"+psb.getProductId());
			List list=new ArrayList();
			
			/****************调拨冻结**************************/
			//开启数据库连接
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult_slave");
			String query = "select se.code scode,sep.stock_out_count scount,se.id,se.create_datetime "
				+" from stock_exchange_product sep,stock_exchange se "
				+" where  sep.stock_out_id="+psb.getId()
				+" and sep.stock_exchange_id=se.id"
				+" and (se.status in(2,3,5,6,8) or (se.status=1 and sep.status=1) )";
			//System.out.println("1-->"+query);
			ResultSet rs = dbOp.executeQuery(query);
			try {
				while (rs.next()) {
					LockOderBean lob =new LockOderBean();
					lob.setCode(rs.getString(1));
					lob.setCount(rs.getInt(2));
					lob.setExchangeId(rs.getInt(3));
					lob.setCreatDate(rs.getString(4).substring(0, 10));
					lob.setType("调拨");
					list.add(lob);
				}
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				dbOp.release();//释放所有资源
			}
			
			/****************销售出库冻结**************************/
			//开启数据库连接
			DbOperation dbOp1 = new DbOperation();
			dbOp1.init("adult_slave");
			String query1 = "select os.code ocode,osp.stockout_count ocount,os.order_id order_id,user_order.create_datetime "+
							"from order_stock_product osp,order_stock os, user_order "+
							"where osp.stockout_id= "+psb.getId()+" and osp.order_stock_id=os.id and os.status in(1,5) "+
							"and user_order.id=os.order_id";

			//System.out.println("2-->"+query1);
			ResultSet rs1 = dbOp1.executeQuery(query1);
			try {
				while (rs1.next()) {
					LockOderBean lob =new LockOderBean();
					lob.setCode(rs1.getString(1));
					lob.setCount(rs1.getInt(2));
					lob.setOrderId(rs1.getInt(3));
					lob.setCreatDate(rs1.getString(4).substring(0, 10));
					lob.setType("销售出库");
					list.add(lob);
				}
				rs.close();
				
				/****************报损单冻结**************************/
				StringBuffer sb = new StringBuffer();
				sb.append("select b.id,receipts_number,bsby_count,add_time from bsby_operationnote b ,bsby_product p");
				sb.append(" where b.id=p.operation_id and p.product_id=").append(productId);
				sb.append(" and (current_type = 1 or current_type=6) and type = 0");
				sb.append(" and b.warehouse_area=").append(area); 
				sb.append(" and b.warehouse_type=").append(type); 
				ResultSet rs2 = dbOp1.executeQuery(sb.toString()); 
				while (rs2.next()) {
					LockOderBean lob =new LockOderBean();
					lob.setOrderId(rs2.getInt(1));
					lob.setCode(rs2.getString(2));
					lob.setCount(rs2.getInt(3));
					lob.setCreatDate(rs2.getString(4).substring(0, 10));
					lob.setType("报损");
					list.add(lob);
				}
				rs2.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally { 
				dbOp1.release();//释放所有资源 
			}
			/****************仓内作业单冻结**************************/
			//开启数据库连接
			DbOperation dbOp2 = new DbOperation();
			dbOp2.init("adult_slave");
			String query2 = "select co.code,coc.stock_count,co.id,co.create_datetime from cargo_operation co join cargo_operation_cargo coc on coc.oper_id=co.id " +
					        "join product p on p.id=coc.product_id join cargo_info c1 on coc.in_cargo_whole_code = c1.whole_code " +
					        "join cargo_info c2 on  coc.out_cargo_whole_code = c2.whole_code " +
					        "where (c1.area_id<>c2.area_id or c1.stock_type <> c2.stock_type) and co.status in(2,3,11,12,20,21,29,30) " +
					        "and co.effect_status in (0,1) and p.id="+productId+" and c2.area_id="+area+" and c2.stock_type="+type;
			ResultSet rs2 = dbOp2.executeQuery(query2);
			try {
				while (rs2.next()) {
					LockOderBean lob =new LockOderBean();
					lob.setCode(rs2.getString("co.code"));
					lob.setCount(rs2.getInt("coc.stock_count"));
					lob.setExchangeId(rs2.getInt("co.id"));
					lob.setCreatDate(rs2.getString(4).substring(0, 10));
					lob.setType("仓内作业");
					list.add(lob);
				}
				rs2.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				dbOp2.release();//释放所有资源
			}
			/****************货位异常单冻结**************************/
			//开启数据库连接
			DbOperation dbOp3 = new DbOperation();
			dbOp3.init("adult_slave");
			String query3 = "select sa.code,sap.lock_count,sap.id,sa.create_datetime from sorting_abnormal_product sap "+
					" join cargo_info ci on ci.whole_code=sap.cargo_whole_code and ci.area_id="+area+" and ci.stock_type="+type+
					" join sorting_abnormal sa on sa.id=sap.sorting_abnormal_id"+
					" where sap.product_id="+productId+" and sap.lock_count>0 and sap.status in (0,1,2,3)";
			ResultSet rs3 = dbOp3.executeQuery(query3);
			try {
				while (rs3.next()) {
					LockOderBean lob =new LockOderBean();
					lob.setCode(rs3.getString("sa.code"));
					lob.setCount(rs3.getInt("sap.lock_count"));
					lob.setExchangeId(rs3.getInt("sap.id"));
					lob.setCreatDate(rs3.getString("sa.create_datetime").substring(0, 10));
					lob.setType("分拣异常单");
					list.add(lob);
				}
				rs3.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				dbOp3.release();//释放所有资源
			}
			request.setAttribute("list", list);
			
		return mapping.findForward("sucess");
	}
		
}

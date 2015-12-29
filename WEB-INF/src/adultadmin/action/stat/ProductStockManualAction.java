package adultadmin.action.stat;

import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;

import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class ProductStockManualAction extends DispatchAction  {

	/**
	 * 作者：赵林
	 * 
	 * 说明：库存统计(手动)
	 */
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Log log = LogFactory.getLog("stat.Log");

		log.info("productStockManual start...");

		String date = StringUtil.convertNull(request.getParameter("date"));

		DbOperation dbOp = new DbOperation();
        dbOp.init();
        try {
            //该日数据已经导入
            String query = "select id from product_stock_history where log_date = '"
                    + date + "'";
            ResultSet rs = dbOp.executeQuery(query);
            if (rs.next()) {
                System.out.println(date + " is logged.");
                //rs.close();
                dbOp
                        .executeUpdate("delete from product_stock_history where log_date = '"
                                + date + "'");
                //dbOp.release();
                //return;
            }
            rs.close();

            System.out.println(date + " log start.");

            /*
            query = "insert into product_stock_history(log_date, product_id, stock, stock_gd) (select '"
                    + date
                    + "', id as product_id, stock, stock_gd from product where (stock + stock_gd) > 0)";
            */
            query = "insert into product_stock_history(log_date, product_id, stock, stock_gd) (select '"
            	+ date 
            	+ "', product_id, sum(case when area=0 then (stock + lock_count) else 0 end) stock_bj, sum(case when area=1 then (stock + lock_count) when area=2 then (stock + lock_count) else 0 end) stock_gd from product_stock group by product_id having (stock_bj > 0 or stock_gd > 0))";
            dbOp.executeUpdate(query);

            System.out.println(date + " log success.");
            
            request.setAttribute("tip", "统计完成，可进行相应查询");
			request.setAttribute("result", "success");
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
        	dbOp.release();
        }
        
		log.info("productStockStatManual finished...");
		

		return mapping.findForward("productStockStat");
	}
	
}

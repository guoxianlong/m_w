package adultadmin.action.stock;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import adultadmin.bean.stock.ProductStockBean;
import adultadmin.util.db.DbOperation;

public class StockUtil {
	/**
	 * 获取地区
	 * @description:
	 * @returnType: List<ProductStockBean>
	 * @throws 
	 * @create:2014-5-29 下午4:06:44
	 * 
	 */
	public static List<ProductStockBean> getProductStockArea(int attribute){
		List<ProductStockBean> list = new ArrayList<ProductStockBean>();
		DbOperation dbOperation = new DbOperation();
		dbOperation.init(DbOperation.DB_SLAVE);
		ResultSet rst = null;
		try {
			rst = dbOperation.executeQuery("select * from stock_area where attribute=" + attribute);		
			while (rst.next()) {
				ProductStockBean bean = new ProductStockBean();
				bean.setId(rst.getInt("id"));
				bean.setAreaName(rst.getString("name"));
				list.add(bean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (rst != null) {
					rst.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				dbOperation.release();
			}
		}
		return list;
	}

}

package mmb.stock.stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;

public class ProductLineService extends BaseServiceImpl {
	public ProductLineService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public ProductLineService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	public List<ProductLineBean> getProductLineName(){
		List<ProductLineBean> list = new ArrayList<ProductLineBean>();		
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return list;
		}
		rs = dbOp.executeQuery("select * from product_line");
		try {
			while (rs.next()) {
				ProductLineBean plBean = new ProductLineBean();
				plBean.setId(rs.getInt("id"));
				plBean.setName(rs.getString("name"));
				plBean.setRemark(rs.getString("remark"));
				plBean.setStatus(rs.getInt("status"));
				plBean.setPermissionId(rs.getInt("permission_id"));
				list.add(plBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return list;
	}
}

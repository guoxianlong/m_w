package mmb.stock.stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;

public class ClaimsVerificationProduct extends BaseServiceImpl {
	
	public ClaimsVerificationProduct(int useConnType, DbOperation dbOp){
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}
	
	public ClaimsVerificationProduct(){
		this.useConnType = CONN_IN_SERVICE;
	}
	
	public List getClaimsVerificationProductClaimsType(String sql) {
		List list = new ArrayList();
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return list;
		}
		rs = dbOp.executeQuery(sql);
		try {
			while (rs.next()) {
				ClaimsVerificationProductBean cvpBean = new ClaimsVerificationProductBean();
				cvpBean.setId(rs.getInt("id"));
				cvpBean.setClaimsVerificationId(rs.getInt("claims_verification_id"));
				cvpBean.setProductCode(rs.getString("product_code"));
				cvpBean.setCount(rs.getInt("count"));
				cvpBean.setExist(rs.getInt("exist"));
				cvpBean.setClaimsType(rs.getInt("claims_type"));
				list.add(cvpBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return list;
	}
}

package mmb.stock.stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;

public class ClaimsPackagePriceService extends BaseServiceImpl {
	public ClaimsPackagePriceService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public ClaimsPackagePriceService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	/**
	 * 获取包装理赔费用设置列表
	 * @Description: TODO 
	 * @param @return
	 * @return List<ClaimsPackagePriceBean>
	 * @auth aohaichen
	 */
	public List<ClaimsPackagePriceBean> getclaimsPackagePriceList(Map<String,String> map){
		List<ClaimsPackagePriceBean> list = new ArrayList<ClaimsPackagePriceBean>();
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select * from claims_package_price cpp LEFT JOIN product_line pl ON cpp.product_line_id = pl.id");
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return list;
		}
		String start = map.get("start");
		int count = Integer.parseInt(map.get("count"));
		if(!"".equals(start)){
			sql.append(" limit "+start);
		}
		if(count > 0 ){
			sql.append(","+count);
		}
		rs = dbOp.executeQuery(sql.toString());
		try {
			while (rs.next()) {
				ClaimsPackagePriceBean cppBean = new ClaimsPackagePriceBean();
				cppBean.setId(rs.getInt("id"));
				cppBean.setProductLineId(rs.getInt("product_line_id"));
				cppBean.setPrice(rs.getDouble("price"));
				cppBean.setCreateUserName(rs.getString("create_user_name"));
				cppBean.setCreateUserId(rs.getInt("create_user_id"));
				cppBean.setCreateDateTime(rs.getString("create_datetime"));
				cppBean.setProductLineName(rs.getString("name"));
				list.add(cppBean);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return list;
	}
	/**
	 * 保存理赔列表
	 * @Description: TODO 
	 * @param @param cpp
	 * @param @return
	 * @return boolean
	 * @auth aohaichen
	 */
	public boolean saveclaimsPackagePrice(ClaimsPackagePriceBean cpp){
		return addXXX(cpp, "claims_package_price");
	}
	/**
	 * 删除理赔列表
	 * @Description: TODO 
	 * @param @param condition
	 * @param @param tableName
	 * @param @return
	 * @return boolean
	 * @auth aohaichen
	 */
	public boolean deleteclaimsPackagePrice(String condition,String tableName){
		return deleteXXX(condition, tableName);
	}
	/**
	 * 查找是否已经添加过产品线记录
	 * @Description: TODO 
	 * @param @param id
	 * @param @return
	 * @return boolean
	 * @auth aohaichen
	 */
	public boolean findDuplicate(int id){
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return false;
		}
		rs = dbOp.executeQuery("select * from claims_package_price where product_line_id="+id);
		
		try {
			if(rs.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			release(dbOp);
		}
		return false;				
	}
	/**
	 * 获取包装理赔列表总记录数
	 * @Description: TODO 
	 * @param @return
	 * @return int
	 * @auth aohaichen
	 */
	public int getClaimsExpensesCount(){
		int count=0;
		ResultSet rs = null;
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return count;
		}
		rs = dbOp.executeQuery("select count(*) from claims_package_price");
		try {
			while (rs.next()) {
				count = rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return count;
	}
}

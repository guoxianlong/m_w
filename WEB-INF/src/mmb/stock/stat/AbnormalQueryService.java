package mmb.stock.stat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.db.DbOperation;

public class AbnormalQueryService extends BaseServiceImpl {
	public AbnormalQueryService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public AbnormalQueryService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	/**
	 * 获取配送异常查询列表
	 * @Description: TODO 
	 * @param @return
	 * @auth aohaichen
	 */
	public List<AbnormalQueryBean> getAbnormalQueryList(Map<String,String> map){
		List<AbnormalQueryBean> list = new ArrayList<AbnormalQueryBean>();
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		String code =map.get("condition");
		sql.append("select id,code,create_datetime,status,phone,name,address from user_order where code = '"+code+"'");
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return list;
		}
		rs = dbOp.executeQuery(sql.toString());
		try {
			while (rs.next()) {
				AbnormalQueryBean aqb = new AbnormalQueryBean();
				aqb.setId(rs.getInt("id"));
				aqb.setCode(rs.getString("code"));
				aqb.setCreateDatetime(rs.getString("create_datetime"));
				aqb.setStatus(rs.getInt("status"));
				aqb.setPhone(rs.getString("phone"));
				aqb.setName(rs.getString("name"));
				aqb.setAddress(rs.getString("address"));								
				list.add(aqb);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return list;
	}
	
	/**
	 * 获取商品查询列表
	 * @Description: TODO 
	 * @param @return
	 * @auth aohaichen
	 */
	public List<AbnormalQueryProductBean> getProductList(Map<String,String> map){
		List<AbnormalQueryProductBean> list = new ArrayList<AbnormalQueryProductBean>();
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		String code =map.get("condition");
		sql.append("SELECT p.`code` ,p.`name`,osp.stockout_count from order_stock os " +
				"LEFT JOIN order_stock_product osp ON os.id = osp.order_stock_id " +
				"LEFT JOIN product p ON osp.product_id = p.id " +
				"WHERE os.`status` !=3 and os.order_code ='"+code+"'");
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return list;
		}
		rs = dbOp.executeQuery(sql.toString());
		try {
			while (rs.next()) {
				AbnormalQueryProductBean aqb = new AbnormalQueryProductBean();
				aqb.setCode(rs.getString("code"));	
				aqb.setName(rs.getString("name"));
				aqb.setBuyCount(rs.getInt("stockout_count"));
				list.add(aqb);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return list;
	}
	
	/**
	 * 获取订单查询状态
	 * @Description: TODO 
	 * @param @return
	 * @auth aohaichen
	 */
	public boolean getProductstatus(Map<String,String> map){
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		
		String code =map.get("condition");
		sql.append("SELECT status from user_order where code= '"+code+"'");
		DbOperation dbOp = getDbOp();
		if (!dbOp.init()) {
			return false;
		}
		rs = dbOp.executeQuery(sql.toString());
		try {
			while (rs.next()) {
				AbnormalQueryBean aqb = new AbnormalQueryBean();
				aqb.setStatus(rs.getInt("status"));
				
				if(aqb.getStatus()==0 || aqb.getStatus()==1 || aqb.getStatus() ==2 || aqb.getStatus()==7 || aqb.getStatus() ==8 || aqb.getStatus()==14){
					return true;
				}
			}
						
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			release(dbOp);
		}
		return false;
	}
	
	/**
	 * 添加查询人记录
	 * @Description: TODO 
	 * @param @return
	 * @auth aohaichen
	 */
	public boolean addAbnormalOrderSearchLog(AbnormalOrderSearchLogBean aosl){
		return addXXX(aosl, "abnormal_order_search_log");
	}
}

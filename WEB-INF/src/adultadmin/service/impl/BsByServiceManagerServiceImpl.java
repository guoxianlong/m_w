package adultadmin.service.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mmb.finance.stat.FinanceProductBean;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.bybs.BsbyOperationRecordBean;
import adultadmin.bean.bybs.BsbyOperationnoteBean;
import adultadmin.bean.bybs.BsbyProductBean;
import adultadmin.bean.bybs.BsbyProductCargoBean;
import adultadmin.bean.bybs.BsbyReason;
import adultadmin.bean.stock.ProductGroupBean;
import adultadmin.bean.stock.StockTypeBean;
import adultadmin.service.IBsByServiceManagerService;
import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;

public class BsByServiceManagerServiceImpl extends BaseServiceImpl implements
		IBsByServiceManagerService {
	public BsByServiceManagerServiceImpl(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public BsByServiceManagerServiceImpl() {
		this.useConnType = CONN_IN_SERVICE;
	}

	public int getByBsOperationnoteCount(String condition) {
		int count = 0;
		count = getXXXCount(condition, "bsby_operationnote", "id");
		return count;
	}

	public ArrayList getByBsOperationnoteList(String condition, int index,
			int count, String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy,
				"bsby_operationnote",
				"adultadmin.bean.bybs.BsbyOperationnoteBean");
		return (ArrayList) queryList;
	}

	public BsbyOperationnoteBean getBuycode(String condition) {
		return (BsbyOperationnoteBean) getXXX(condition, "bsby_operationnote",
				"adultadmin.bean.bybs.BsbyOperationnoteBean");
	}

	public boolean addBsbyOperationnoteBean(BsbyOperationnoteBean bean) {

		return addXXX(bean, "bsby_operationnote");

	}

	public BsbyOperationnoteBean getBsbyOperationnoteBean(String condition) {
		Object queryObject = null;
		queryObject = (BsbyOperationnoteBean) getXXX(condition,
				"bsby_operationnote",
				"adultadmin.bean.bybs.BsbyOperationnoteBean");

		return (BsbyOperationnoteBean) queryObject;
	}

	public boolean updateBsbyOperationnoteBean(String set, String condition) {
		return updateXXX(set, condition, "bsby_operationnote");
	}
	/**
	 * 修改单据商品的数量
	 * 2010-02-23
	 * @param set
	 * @param condition
	 * @return
	 */
	public boolean updateBsbyProductBean(String set, String condition) {
		return updateXXX(set, condition, "bsby_product");
	}
	/**
	 * 添加日志 2010-02-21
	 * 
	 * @param bean
	 * @return
	 */
	public boolean addBsbyOperationRecord(BsbyOperationRecordBean bean) {

		return addXXX(bean, "bsby_operation_record");

	}
	/**
	 * 删除日志
	 * 2010-02-23
	 * @param condition
	 * @return
	 */
	public boolean deleteBsbyOperationRecord(String condition) {
		return deleteXXX(condition, "bsby_operation_record");
	}
	/**
	 * 查询单据的操作记录列表 2010-02-22
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public ArrayList getBsbyOperationRecordList(String condition, int index,
			int count, String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy,
				"bsby_operation_record",
				"adultadmin.bean.bybs.BsbyOperationRecordBean");
		return (ArrayList) queryList;
	}

	/**
	 * 检查商品是否已经在这个单据中存在 2010-02-21
	 * 
	 * @param condition
	 * @return
	 */
	public BsbyProductBean getBsbyProductBean(String condition) {
		Object queryObject = null;
		queryObject = (BsbyProductBean) getXXX(condition, "bsby_product",
				"adultadmin.bean.bybs.BsbyProductBean");

		return (BsbyProductBean) queryObject;
	}

	/**
	 * 给报损报溢单据添加商品 2010-02-21
	 */
	public boolean addBsbyProduct(BsbyProductBean bean) {

		return addXXX(bean, "bsby_product");

	}
	/**
	 * 删除单据商品
	 * 2010-02-22
	 */
	public boolean deleteBsbyProduct(String condition) {
		return deleteXXX(condition, "bsby_product");
	}
	/**
	 * 删除单据
	 * 2010-02-22
	 * @param condition
	 * @return
	 */
	public boolean deleteBsbyOperationnote(String condition) {
		return deleteXXX(condition, "bsby_operationnote");
	}
	/**
	 * 得到单据的所有添加的产品
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public ArrayList getBsbyProductList(String condition, int index, int count,
			String orderBy) {
		Object queryList = getXXXList(condition, index, count, orderBy,
				"bsby_product", "adultadmin.bean.bybs.BsbyProductBean");
		return (ArrayList) queryList;
	}
	
	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean addBsbyProductCargo(BsbyProductCargoBean bean) {
		return addXXX(bean, "bsby_product_cargo");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean deleteBsbyProductCargo(String condition) {
		return deleteXXX(condition, "bsby_product_cargo");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public BsbyProductCargoBean getBsbyProductCargo(String condition) {
		return (BsbyProductCargoBean) getXXX(condition, "bsby_product_cargo",
				"adultadmin.bean.bybs.BsbyProductCargoBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public int getBsbyProductCargoCount(String condition) {
		return getXXXCount(condition, "bsby_product_cargo", "id");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public ArrayList getBsbyProductCargoList(String condition, int index,
			int count, String orderBy) {
		return getXXXList(condition, index, count, orderBy, "bsby_product_cargo",
				"adultadmin.bean.bybs.BsbyProductCargoBean");
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public boolean updateBsbyProductCargo(String set, String condition) {
		return updateXXX(set, condition, "bsby_product_cargo");
	}
	/*
	 * 获取报损报溢原因列表
	 */
	public List<BsbyReason> getBsbyReasonList(int type) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<BsbyReason> list = new ArrayList<BsbyReason>();
		try {
			pst = this.dbOp.getConn()
					.prepareStatement("select * from bsby_reason  where type="+type+" order by id");
			
			rs = pst.executeQuery();
			while (rs.next()) {
				BsbyReason bean = new BsbyReason();
				bean.setId(rs.getInt("id"));
				bean.setReason(rs.getString("reason"));
				list.add(bean);
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}
	
	/*
	 * 获取报损报溢原因列表(去除重复原因)
	 */
	public List<BsbyReason> getBsbyReasonListDistinct() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<BsbyReason> list = new ArrayList<BsbyReason>();
		try {
			pst = this.dbOp.getConn()
					.prepareStatement("select distinct reason from bsby_reason order by id");
			
			rs = pst.executeQuery();
			while (rs.next()) {
				BsbyReason bean = new BsbyReason();
				//bean.setId(rs.getInt("id"));
				bean.setReason(rs.getString("reason"));
				list.add(bean);
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}
	
	/*
	 * 获取报损报溢原因
	 */
	@Override
	public BsbyReason getBsbyReasonByCondition(String condition) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		BsbyReason bean =null;
		try {
			pst = this.dbOp.getConn().prepareStatement("select * from bsby_reason  where "+condition+" order by id");
			rs = pst.executeQuery();
			while (rs.next()) {
				bean = new BsbyReason();
				bean.setId(rs.getInt("id"));
				bean.setReason(rs.getString("reason"));
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return bean;
	}
	
	/**
	 * 返回商品不含税金额
	 */
	@Override
	public float returnFinanceProductPrice(int productId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		FinanceProductBean bean = null;
		try {
			pst = this.dbOp.getConn()
					.prepareStatement("select * from finance_product where product_id = ?");
			pst.setInt(1, productId);

			rs = pst.executeQuery();
			if (rs.next()) {
				bean = new FinanceProductBean();
				bean.setId(rs.getInt("id"));
				bean.setNotaxPrice(rs.getFloat("notax_price"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return bean.getNotaxPrice();
	}
	/**
	 * 返回货位号
	 */
	@Override
	public String returnCargoCode(int id) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		String sqlcargocode = "select count,whole_code from bsby_product_cargo " +
				" join cargo_info on cargo_id=cargo_info.id " +
				"where bsby_oper_id=?";
		String wholeCode = "";
		try {
			pst =  this.dbOp.getConn().prepareStatement(sqlcargocode);
			pst.setInt(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				wholeCode = rs.getString("whole_code");
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return wholeCode;
	}
}

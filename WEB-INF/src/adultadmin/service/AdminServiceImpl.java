/**
 * 
 */
package adultadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mmb.sale.order.stat.OrderAdminStatusLogBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import adultadmin.action.vo.ProductBarcodeVO;
import adultadmin.action.vo.SupplierProductLine;
import adultadmin.action.vo.voCatalog;
import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderExtendInfo;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voProductLineCatalog;
import adultadmin.action.vo.voProductProperty;
import adultadmin.action.vo.voProductPropertyInfo;
import adultadmin.action.vo.voSelect;
import adultadmin.action.vo.voUser;
import adultadmin.bean.OrderStatusBean;
import adultadmin.bean.buy.BuyStockinProductBean;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.productProperty.CashTicketInfoBean;
import adultadmin.framework.exceptions.DatabaseException;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductDiscountService;
import adultadmin.service.infc.ISMSService;
import adultadmin.util.MobileProductUtil;
import adultadmin.util.NumberUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;

/**
 * @author Bomb
 *  
 */
public class AdminServiceImpl implements IAdminService {

	public Log stockLog = LogFactory.getLog("stock.Log");

	protected Connection conn = null;

	protected boolean autoCommit = true;

	static protected String NEARLY_SAME = " between -0.0001 and 0.0001";

	private DbOperation dbOp = null;

	public int executeUpdate(String sql) {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql);

			return pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		return -1;
	}

	/**
	 * 
	 * @throws DatabaseException
	 */
	public AdminServiceImpl() throws DatabaseException {
		super();
		init();
	}

	public AdminServiceImpl(String datebaseName) {
		conn = DbUtil.getConnection(datebaseName);
		if (conn == null) {
			conn = DbUtil.getDirectConnection();
		}
		if(conn != null){
			this.dbOp = new DbOperation();
			this.dbOp.init(conn);
		}
	}

	public AdminServiceImpl(DbOperation dbOp){
		this.conn = dbOp.getConn();
		this.dbOp = dbOp;
	}

	protected void init() throws DatabaseException {
		conn = DbUtil.getConnection("adult");
		if (conn == null) {
			conn = DbUtil.getDirectConnection();
		}
		if(conn != null){
			this.dbOp = new DbOperation();
			this.dbOp.init(conn);
		}
	}

	protected void closeStatement(Statement st) {
		DbUtil.closeStatement(st);
	}

	protected void closeResultSet(ResultSet rs) {
		DbUtil.closeResultSet(rs);
	}

	public void close() {
		DbUtil.closeConnection(conn);
	}

	public int getNumber(int id) {
		int number = 0;
		Statement st = null;

		try {
			st = conn.createStatement();
			ResultSet rs = st
			.executeQuery("select number from number where id=" + id);
			if (rs.next())
				number = rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
		return number;
	}

	public void setNumber(int id, int number) {
		Statement st = null;
		try {
			st = conn.createStatement();
			st.executeUpdate("update number set number=" + number
					+ " where id=" + id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
	}

	public int getLastInsertId(String table) {
		Statement st = null;
		int id = 0;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery("select last_insert_id()");
			if (rs.next())
				id = rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return id;
	}

	public int getInt(String sql) {
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();

			rs = st.executeQuery(sql);
			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getProduct(int)
	 */
	public voProduct getProduct(int id) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		voProduct vo = null;
		try {
		pst = conn.prepareStatement("select *, (select group_concat(supplier_name) from product_supplier where product_supplier.product_id=product.id) proxy_name, (select name from product_status where product_status.id=product.status) product_status_name from product where id=?");
			pst.setInt(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				vo = new voProduct();
				vo.setId(rs.getInt("id"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setCreateTime(rs.getString("create_datetime"));
				vo.setName(rs.getString("name"));
				vo.setOriname(rs.getString("oriname"));
				vo.setPrice(rs.getFloat("price"));
				vo.setPrice2(rs.getFloat("price2"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setPrice4(rs.getFloat("price4"));
				vo.setPrice5(rs.getFloat("price5"));
				vo.setDeputizePrice(rs.getFloat("deputize_price"));
				vo.setGroupBuyPrice(rs.getFloat("group_buy_price"));
				vo.setPic(rs.getString("pic"));
				vo.setPic2(rs.getString("pic2"));
				vo.setPic3(rs.getString("pic3"));
				vo.setIntro(rs.getString("intro"));
				vo.setIntro2(rs.getString("intro2"));
				vo.setParentId1(rs.getInt("parent_id1"));
				vo.setParentId2(rs.getInt("parent_id2"));
				vo.setParentId3(rs.getInt("parent_id3"));
				vo.setFlag(rs.getInt("flag"));
				vo.setCommentCount(rs.getInt("comment_count"));
				vo.setClickCount(rs.getInt("click_count"));
				vo.setBuyCount(rs.getInt("buy_count"));
				vo.setProxyId(rs.getInt("proxy_id"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setProxyName(rs.getString("proxy_name"));
				vo.setProxysName(rs.getString("proxys_name"));
				vo.setRemark(rs.getString("remark"));
				vo.setUnit(rs.getString("unit"));
				vo.setStock(rs.getInt("stock"));
				vo.setStockGd(rs.getInt("stock_gd"));
				vo.setImages(rs.getString("images"));
				vo.setGuanggao(rs.getString("guanggao"));
				vo.setGongxiao(rs.getString("gongxiao"));
				vo.setShiyongrenqun(rs.getString("shiyongrenqun"));
				vo.setShiyongfangfa(rs.getString("shiyongfangfa"));
				vo.setZhuyishixiang(rs.getString("zhuyishixiang"));
				vo.setTebietishi(rs.getString("tebietishi"));
				vo.setBaozhuangzhongliang(rs.getString("baozhuangzhongliang"));
				vo.setBaozhuangdaxiao(rs.getString("baozhuangdaxiao"));
				vo.setChanpinzhongliang(rs.getString("chanpinzhongliang"));
				vo.setChanpinchicun(rs.getString("chanpinchicun"));
				vo.setChanpinchengfen(rs.getString("chanpinchengfen"));
				vo.setChangshang(rs.getString("changshang"));
				vo.setBaozhiqi(rs.getString("baozhiqi"));
				vo.setChucangfangfa(rs.getString("chucangfangfa"));
				vo.setPizhunwenhao(rs.getString("pizhunwenhao"));
				vo.setChangshangjieshao(rs.getString("changshangjieshao"));
				vo.setFuwuchengnuo(rs.getString("fuwuchengnuo"));
				vo.setRank(rs.getInt("rank"));
				vo.setDisplayOrder(rs.getInt("display_order"));
				vo.setTopOrder(rs.getInt("top_order"));
				vo.setStockLineBj(rs.getInt("stock_line_bj"));
				vo.setStockLineGd(rs.getInt("stock_line_gd"));
				vo.setStockStandardBj(rs.getInt("stock_standard_bj"));
				vo.setStockStandardGd(rs.getInt("stock_standard_gd"));
				vo.setStockStatus(rs.getInt("stock_status"));
				vo.setIsPackage(rs.getInt("is_package"));
				vo.setHasPresent(rs.getInt("has_present"));
				vo.setBrand(rs.getInt("brand"));
				vo.setShowPackage(rs.getInt("show_package"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setStockDayBj(rs.getInt("stock_day_bj"));
				vo.setStockDayGd(rs.getInt("stock_day_gd"));
				vo.setStockBjBad(rs.getInt("stock_bj_bad"));
				vo.setStockBjRepair(rs.getInt("stock_bj_repair"));
				vo.setStockGdBad(rs.getInt("stock_gd_bad"));
				vo.setStockGdRepair(rs.getInt("stock_gd_repair"));
				vo.setStatusName(rs.getString("product_status_name"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return vo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getProduct(int)
	 */
	public voProduct getProductSimple(int id) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		voProduct vo = null;
		try {
			pst = conn
			.prepareStatement("select id,code,name,oriname,parent_id1 from product where id=?");
			pst.setInt(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				vo = new voProduct();
				vo.setId(rs.getInt("id"));
				vo.setCode(rs.getString("code"));
				vo.setName(rs.getString("name"));
				vo.setOriname(rs.getString("oriname"));
				vo.setParentId1(rs.getInt("parent_id1"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return vo;
	}

	public voProduct getProduct(int id, DbOperation dbOp) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		voProduct vo = null;
		try {
			dbOp
			.prepareStatement("select *, (select name from product_proxy where product_proxy.id=product.proxy_id) proxy_name from product where id=?");
			pst = dbOp.getPStmt();
			pst.setInt(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				vo = new voProduct();
				vo.setId(rs.getInt("id"));
				vo.setName(rs.getString("name"));
				vo.setOriname(rs.getString("oriname"));
				vo.setPrice(rs.getFloat("price"));
				vo.setPrice2(rs.getFloat("price2"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setPrice4(rs.getFloat("price4"));
				vo.setPrice5(rs.getFloat("price5"));
				vo.setDeputizePrice(rs.getFloat("deputize_price"));
				vo.setGroupBuyPrice(rs.getFloat("group_buy_price"));
				vo.setPic(rs.getString("pic"));
				vo.setPic2(rs.getString("pic2"));
				vo.setPic3(rs.getString("pic3"));
				vo.setIntro(rs.getString("intro"));
				vo.setIntro2(rs.getString("intro2"));
				vo.setParentId1(rs.getInt("parent_id1"));
				vo.setParentId2(rs.getInt("parent_id2"));
				vo.setParentId3(rs.getInt("parent_id3"));
				vo.setFlag(rs.getInt("flag"));
				vo.setCommentCount(rs.getInt("comment_count"));
				vo.setClickCount(rs.getInt("click_count"));
				vo.setBuyCount(rs.getInt("buy_count"));
				vo.setProxyId(rs.getInt("proxy_id"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setProxyName(rs.getString("proxy_name"));
				vo.setProxysName(rs.getString("proxys_name"));
				vo.setRemark(rs.getString("remark"));
				vo.setUnit(rs.getString("unit"));
				vo.setStock(rs.getInt("stock"));
				vo.setStockGd(rs.getInt("stock_gd"));
				vo.setImages(rs.getString("images"));
				vo.setGuanggao(rs.getString("guanggao"));
				vo.setGongxiao(rs.getString("gongxiao"));
				vo.setShiyongrenqun(rs.getString("shiyongrenqun"));
				vo.setShiyongfangfa(rs.getString("shiyongfangfa"));
				vo.setZhuyishixiang(rs.getString("zhuyishixiang"));
				vo.setTebietishi(rs.getString("tebietishi"));
				vo.setBaozhuangzhongliang(rs.getString("baozhuangzhongliang"));
				vo.setBaozhuangdaxiao(rs.getString("baozhuangdaxiao"));
				vo.setChanpinzhongliang(rs.getString("chanpinzhongliang"));
				vo.setChanpinchicun(rs.getString("chanpinchicun"));
				vo.setChanpinchengfen(rs.getString("chanpinchengfen"));
				vo.setChangshang(rs.getString("changshang"));
				vo.setBaozhiqi(rs.getString("baozhiqi"));
				vo.setChucangfangfa(rs.getString("chucangfangfa"));
				vo.setPizhunwenhao(rs.getString("pizhunwenhao"));
				vo.setChangshangjieshao(rs.getString("changshangjieshao"));
				vo.setFuwuchengnuo(rs.getString("fuwuchengnuo"));
				vo.setRank(rs.getInt("rank"));
				vo.setDisplayOrder(rs.getInt("display_order"));
				vo.setTopOrder(rs.getInt("top_order"));
				vo.setStockLineBj(rs.getInt("stock_line_bj"));
				vo.setStockLineGd(rs.getInt("stock_line_gd"));
				vo.setStockStandardBj(rs.getInt("stock_standard_bj"));
				vo.setStockStandardGd(rs.getInt("stock_standard_gd"));
				vo.setStockStatus(rs.getInt("stock_status"));
				vo.setIsPackage(rs.getInt("is_package"));
				vo.setHasPresent(rs.getInt("has_present"));
				vo.setBrand(rs.getInt("brand"));
				vo.setShowPackage(rs.getInt("show_package"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setStockDayBj(rs.getInt("stock_day_bj"));
				vo.setStockDayGd(rs.getInt("stock_day_gd"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return vo;
	}

	public voProduct getProduct(String code) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		voProduct vo = null;
		try {
			pst = conn.prepareStatement("select * from product where code=?");
			pst.setString(1, code);

			rs = pst.executeQuery();
			if (rs.next()) {
				vo = new voProduct();
				vo.setId(rs.getInt("id"));
				vo.setName(rs.getString("name"));
				vo.setOriname(rs.getString("oriname"));
				vo.setPrice(rs.getFloat("price"));
				vo.setPrice2(rs.getFloat("price2"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setPrice4(rs.getFloat("price4"));
				vo.setPrice5(rs.getFloat("price5"));
				vo.setDeputizePrice(rs.getFloat("deputize_price"));
				vo.setGroupBuyPrice(rs.getFloat("group_buy_price"));
				vo.setPic(rs.getString("pic"));
				vo.setPic2(rs.getString("pic2"));
				vo.setPic3(rs.getString("pic3"));
				vo.setIntro(rs.getString("intro"));
				vo.setIntro2(rs.getString("intro2"));
				vo.setParentId1(rs.getInt("parent_id1"));
				vo.setParentId2(rs.getInt("parent_id2"));
				vo.setParentId3(rs.getInt("parent_id3"));
				vo.setFlag(rs.getInt("flag"));
				vo.setCommentCount(rs.getInt("comment_count"));
				vo.setClickCount(rs.getInt("click_count"));
				vo.setBuyCount(rs.getInt("buy_count"));
				vo.setProxyId(rs.getInt("proxy_id"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setProxysName(rs.getString("proxys_name"));
				vo.setRemark(rs.getString("remark"));
				vo.setUnit(rs.getString("unit"));
				vo.setStock(rs.getInt("stock"));
				vo.setStockGd(rs.getInt("stock_gd"));
				vo.setImages(rs.getString("images"));
				vo.setGuanggao(rs.getString("guanggao"));
				vo.setGongxiao(rs.getString("gongxiao"));
				vo.setShiyongrenqun(rs.getString("shiyongrenqun"));
				vo.setShiyongfangfa(rs.getString("shiyongfangfa"));
				vo.setZhuyishixiang(rs.getString("zhuyishixiang"));
				vo.setTebietishi(rs.getString("tebietishi"));
				vo.setBaozhuangzhongliang(rs.getString("baozhuangzhongliang"));
				vo.setBaozhuangdaxiao(rs.getString("baozhuangdaxiao"));
				vo.setChanpinzhongliang(rs.getString("chanpinzhongliang"));
				vo.setChanpinchicun(rs.getString("chanpinchicun"));
				vo.setChanpinchengfen(rs.getString("chanpinchengfen"));
				vo.setChangshang(rs.getString("changshang"));
				vo.setBaozhiqi(rs.getString("baozhiqi"));
				vo.setChucangfangfa(rs.getString("chucangfangfa"));
				vo.setPizhunwenhao(rs.getString("pizhunwenhao"));
				vo.setChangshangjieshao(rs.getString("changshangjieshao"));
				vo.setFuwuchengnuo(rs.getString("fuwuchengnuo"));
				vo.setRank(rs.getInt("rank"));
				vo.setDisplayOrder(rs.getInt("display_order"));
				vo.setTopOrder(rs.getInt("top_order"));
				vo.setStockLineBj(rs.getInt("stock_line_bj"));
				vo.setStockLineGd(rs.getInt("stock_line_gd"));
				vo.setStockStandardBj(rs.getInt("stock_standard_bj"));
				vo.setStockStandardGd(rs.getInt("stock_standard_gd"));
				vo.setStockStatus(rs.getInt("stock_status"));
				vo.setIsPackage(rs.getInt("is_package"));
				vo.setHasPresent(rs.getInt("has_present"));
				vo.setBrand(rs.getInt("brand"));
				vo.setShowPackage(rs.getInt("show_package"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setStockDayBj(rs.getInt("stock_day_bj"));
				vo.setStockDayGd(rs.getInt("stock_day_gd"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return vo;
	}

	public voProduct getProduct2(String condition) {
		if(StringUtil.isNull(condition)){
			return null;
		}
		Statement st = null;
		ResultSet rs = null;
		voProduct vo = null;
		try {
			st = conn.createStatement();
			if(condition != null && condition.length() > 0){
				rs = st.executeQuery("select *,GROUP_CONCAT(distinct d.name) sName from product a left outer join product_status b on a.status=b.id left outer join product_supplier c on a.id=c.product_id  left join supplier_standard_info d on d.id=c.supplier_id and d.status=1 where " + condition + " group by a.id");
				if (rs.next()) {
					vo = getProductsItem1(rs);
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return vo;
	}

	/* 
	 * 说明：仅根据简单条件查询产品
	 */
	public List getProducts(String condition) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			st = conn.createStatement();
			rs = st.executeQuery("select * from product a left outer join product_status b on status=b.id left outer join product_proxy c on proxy_id=c.id " + StringUtil.convertNull(condition));
			while (rs.next()) {
				list.add(getProductsItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getProductsForPresent(String condition){
		List tempList = new ArrayList(); 
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery("select id,code,has_present from product where 1=1 "+condition);
			while (rs.next()) {
				voProduct p = new voProduct();
				p.setId(rs.getInt("id"));
				p.setCode(rs.getString("code"));
				p.setHasPresent(rs.getInt("has_present"));
				tempList.add(p);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return tempList;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getProducts(int)
	 */
	public List getProducts(String condition, int start, int limit) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			String sql ="select * ,GROUP_CONCAT(distinct d.name) sName from product a left outer join product_status b on a.status=b.id left outer join " +
			"product_supplier c on a.id=c.product_id left join supplier_standard_info d on d.id=c.supplier_id and d.status=1 where  "
			+ condition+ " group by a.id order by rank desc, display_order asc, a.status asc, click_count desc,a.id desc limit ?,?";
			pst = conn
			.prepareStatement(sql);
			pst.setInt(1, start);
			pst.setInt(2, limit);

			rs = pst.executeQuery();
			while (rs.next()) {
				list.add(getProductsItem1(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	public List getProductList(String condition, int start, int limit,
			String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			StringBuffer buf = new StringBuffer();
			buf
			.append("select *,ifnull((select (to_days(now()) - to_days(sh.deal_datetime)) from stock_history sh where sh.product_id=a.id and sh.oper_type=0 and sh.stock_type=0 and sh.deal_datetime is not null order by sh.id desc limit 1), -1) days from product a left outer join product_status b on status=b.id left outer join product_proxy c on proxy_id=c.id ");
			if (condition != null && condition.length() > 0) {
				buf.append(" where ");
				buf.append(condition);
			}
			if (orderBy != null && orderBy.length() > 0) {
				buf.append(" order by ");
				buf.append(orderBy);
			}
			if (start >= 0 && limit > 0) {
				buf.append(" limit ");
				buf.append(start);
				buf.append(",");
				buf.append(limit);
			}
			String sql = buf.toString();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				voProduct product = getProductsItem(rs);
				product.setTopOrder(rs.getInt("days"));
				list.add(product);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getProductStockList(String condition, int start, int limit,
			String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			StringBuffer buf = new StringBuffer();
			buf
			.append("select *,ifnull((select sum(sh.stock_bj) from stock_history sh join stock_operation so on sh.oper_id=so.id where so.status=1 and sh.product_id=a.id and sh.oper_type=0 and sh.stock_type=0 and sh.status=1 order by sh.id desc), 0) stock_out_bj,ifnull((select sum(sh.stock_gd) from stock_history sh join stock_operation so on sh.oper_id=so.id where so.status=1 and sh.product_id=a.id and sh.oper_type=0 and sh.stock_type=0 and sh.status=1 order by sh.id desc), 0) stock_out_gd,ifnull((select (to_days(now()) - to_days(sh.deal_datetime)) from stock_history sh where sh.product_id=a.id and sh.oper_type=0 and sh.stock_type=0 and sh.deal_datetime is not null order by sh.id desc limit 1), -1) days from product a left outer join product_status b on status=b.id left outer join product_proxy c on proxy_id=c.id ");
			if (condition != null && condition.length() > 0) {
				buf.append(" where ");
				buf.append(condition);
			}
			if (orderBy != null && orderBy.length() > 0) {
				buf.append(" order by ");
				buf.append(orderBy);
			}
			if (start >= 0 && limit > 0) {
				buf.append(" limit ");
				buf.append(start);
				buf.append(",");
				buf.append(limit);
			}
			String sql = buf.toString();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				voProduct product = getProductsItem(rs);
				product
				.setStock(product.getStock()
						+ rs.getInt("stock_out_bj"));
				product.setStockGd(product.getStockGd()
						+ rs.getInt("stock_out_gd"));
				product.setTopOrder(rs.getInt("days"));
				list.add(product);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getProductStockList2(String condition, int start, int limit,
			String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			StringBuffer buf = new StringBuffer();
			buf
			.append("select *,ifnull((select sum(sh.stock_bj) from stock_history sh join stock_operation so on sh.oper_id=so.id where so.status=1 and sh.product_id=a.id and sh.oper_type=0 and sh.stock_type=0 and sh.status=1 order by sh.id desc), 0) stock_out_bj,ifnull((select sum(sh.stock_gd) from stock_history sh join stock_operation so on sh.oper_id=so.id where so.status=1 and sh.product_id=a.id and sh.oper_type=0 and sh.stock_type=0 and sh.status=1 order by sh.id desc), 0) stock_out_gd,ifnull((select (to_days(now()) - to_days(sh.deal_datetime)) from stock_history sh where sh.product_id=a.id and sh.oper_type=0 and sh.stock_type=0 and sh.deal_datetime is not null order by sh.id desc limit 1), -1) days from product a left outer join product_status b on status=b.id left outer join product_proxy c on proxy_id=c.id ");
			if (condition != null && condition.length() > 0) {
				buf.append(" where ");
				buf.append(condition);
			}
			if (orderBy != null && orderBy.length() > 0) {
				buf.append(" order by ");
				buf.append(orderBy);
			}
			if (start >= 0 && limit > 0) {
				buf.append(" limit ");
				buf.append(start);
				buf.append(",");
				buf.append(limit);
			}
			String sql = buf.toString();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				voProduct product = getProductsItem(rs);
				product
				.setStock(product.getStock()
						+ rs.getInt("stock_out_bj"));
				product.setStockGd(product.getStockGd()
						+ rs.getInt("stock_out_gd"));
				product.setTopOrder(rs.getInt("days"));
				list.add(product);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}
	//	获取已经处于处理中和待处理状态下的产品个数
	public Hashtable getOrderStockProductList(String sql) {
		Statement st = null;
		ResultSet rs = null;
		Hashtable hm=new Hashtable();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				OrderStockProductBean osp =null;
				if(hm.get(""+rs.getInt(1))==null){        		
					osp=new OrderStockProductBean();
					osp.setProductId(rs.getInt(1));
					osp.setStockoutCount(rs.getInt(2));
					osp.setCreateDatetime(rs.getString(3));
					hm.put(""+rs.getInt(1), osp); 
				}else{
					OrderStockProductBean opb=(OrderStockProductBean)hm.get(""+rs.getInt(1));
					opb.setStockoutCount(opb.getStockoutCount()+rs.getInt(2));        		
				} 	
				//				System.out.println("集合大小:"+hm.size());
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return hm;
	}
	public Hashtable getOrderStockProductList(List list) {
		Statement st = null;
		PreparedStatement psRelation2 = null;
		ResultSet rs = null;
		String sql = "";
		HashMap dealingCount = new HashMap();
		Hashtable hm=new Hashtable();
		try {
			st = conn.createStatement();
			Iterator iter = list.listIterator();
			while(iter.hasNext()){
				voProduct product = (voProduct)iter.next();
				//订单中的商品
				sql = "SELECT uop.product_id, sum(uop.count), a.is_package FROM user_order uo join user_order_product uop on uo.id=uop.order_id join product a on uop.product_id=a.id where uo.status = 3 and a.id = "+product.getId()+" group by uop.product_id";
				rs = st.executeQuery(sql);
				while(rs.next()){
					int productId = rs.getInt(1);
					int count = rs.getInt(2);
					int isPackage = rs.getInt(3);
					if(isPackage == 0){
						dealingCount.put(Integer.valueOf(productId), Integer.valueOf(count));
					}
				}
				//订单中的赠品
				sql = "SELECT uop.product_id, sum(uop.count), a.is_package FROM user_order uo join user_order_present uop on uo.id=uop.order_id join product a on uop.product_id=a.id where uo.status = 3 and a.id = "+product.getId()+" group by uop.product_id";
				rs = st.executeQuery(sql);
				while(rs.next()){
					int productId = rs.getInt(1);
					int count = rs.getInt(2);
					int isPackage = rs.getInt(3);
					if(isPackage == 0){
						Integer key = Integer.valueOf(productId);
						if(dealingCount.get(key) != null){
							dealingCount.put(key, Integer.valueOf(NumberUtil.sum(dealingCount.get(key), Integer.valueOf(count))));
						} else {
							dealingCount.put(key, Integer.valueOf(count));
						}
					}
				}

				sql = "select parent_id, product_id, product_count from product_package where product_id=?";
				psRelation2 = conn.prepareStatement(sql);
				psRelation2.setInt(1, product.getId());
				rs = psRelation2.executeQuery();
				ResultSet rsTemp = null;
				while(rs.next()){
					int parentId = rs.getInt("parent_id");
//					int childProductId = rs.getInt("product_id");
					int productCount = rs.getInt("product_count");

					// 查找父产品 处理中数量（发货状态为 未处理、失败、缺货）——订单中的商品
					sql = "SELECT uop.product_id, sum(uop.count) FROM user_order uo join user_order_product uop on uo.id=uop.order_id join product a on uop.product_id=a.id where uop.product_id=" + parentId + " and uo.status = 3 group by uop.product_id";
					rsTemp = st.executeQuery(sql);
					if(rsTemp.next()){
						int dcpGd = rsTemp.getInt(2) * productCount;
						if(dealingCount.get(Integer.valueOf(product.getId())) != null){
							dcpGd = NumberUtil.sum(Integer.valueOf(dcpGd), (Integer)dealingCount.get(Integer.valueOf(product.getId())));
							dealingCount.put(Integer.valueOf(product.getId()), Integer.valueOf(dcpGd));
						} else {
							dealingCount.put(Integer.valueOf(product.getId()), Integer.valueOf(dcpGd));
						}
					}
					// 查找父产品 处理中数量（发货状态为 未处理、失败、缺货）——订单中的赠品
					sql = "SELECT uop.product_id, sum(uop.count) FROM user_order uo join user_order_present uop on uo.id=uop.order_id join product a on uop.product_id=a.id where uop.product_id=" + parentId + " and uo.status = 3 group by uop.product_id";
					rsTemp = st.executeQuery(sql);
					if(rsTemp.next()){
						int dcpGd = rsTemp.getInt(2) * productCount;
						if(dealingCount.get(Integer.valueOf(product.getId())) != null){
							dcpGd = NumberUtil.sum(Integer.valueOf(dcpGd), (Integer)dealingCount.get(Integer.valueOf(product.getId())));
							dealingCount.put(Integer.valueOf(product.getId()), Integer.valueOf(dcpGd));
						} else {
							dealingCount.put(Integer.valueOf(product.getId()), Integer.valueOf(dcpGd));
						}
					}
				}

				OrderStockProductBean osp =null;
				osp=new OrderStockProductBean();
				osp.setProductId(product.getId());
				osp.setStockoutCount(NumberUtil.sum(dealingCount.get(Integer.valueOf(product.getId())), Integer.valueOf(0)));
				hm.put(""+product.getId(), osp); 
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeStatement(psRelation2);
			closeResultSet(rs);
		}
		return hm;
	}
	public Hashtable getBuyStockinProductList(String sql){
		Statement st = null;
		ResultSet rs = null;
		Hashtable hm=new Hashtable();
		BuyStockinProductBean ospb=null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				ospb=new BuyStockinProductBean();
				ospb.setBuyStockinId(rs.getInt(1));
				ospb.setCreateDatetime(rs.getString(2));
				hm.put(""+rs.getInt(1), ospb);
			}   	
		}catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return hm; 	
	}
	public List getProductList1(String condition, int start, int limit,
			String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			StringBuffer buf = new StringBuffer();
			buf	       //查出所有库存总数不为0的产品集合
			.append("select a.*,b.*,GROUP_CONCAT(distinct d.name)sName ,sum(ps.stock)+sum(ps.lock_count) s from product a join product_status b on " +
					"a.status = b.id left outer join product_supplier c on a.id=c.product_id left join supplier_standard_info d on " +
			"d.id=c.supplier_id and d.status=1 join product_stock ps on a.id = ps.product_id");
			if (condition != null && condition.length() > 0) {
				buf.append(" where a.id>0 ");
				buf.append(condition);
			}
			buf.append(" group by a.id having s > 0");
			if (orderBy != null && orderBy.length() > 0) {
				buf.append(" order by ");
				buf.append(orderBy);
			}
			if (start >= 0 && limit > 0) {
				buf.append(" limit ");
				buf.append(start);
				buf.append(",");
				buf.append(limit);
			}
			String sql = buf.toString();
			//			System.out.println("==>>"+sql);
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				list.add(getProductsItem1(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}
	public List getProductList2(String condition, int start, int limit,
			String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			StringBuffer buf = new StringBuffer();
			buf	       //查出所有库存总数不为0的产品集合
			.append("select a.id,a.code,a.name,a.oriname,a.price,a.price2,a.price3,a.price4,a.price5,a.deputize_price,a.deputize_price,a.parent_id1,a.parent_id2,a.parent_id3,a.status,sum(ps.stock)+sum(ps.lock_count) s from product a" +
					" left outer join product_supplier c on a.id=c.product_id left join supplier_standard_info d on d.id=c.supplier_id and d.status=1 join product_stock ps on a.id = ps.product_id");
			if (condition != null && condition.length() > 0) {
				buf.append(" where a.id>0 ");
				buf.append(condition);
			}
			buf.append(" group by a.id having s > 0");
			if (orderBy != null && orderBy.length() > 0) {
				buf.append(" order by ");
				buf.append(orderBy);
			}
			if (start >= 0 && limit > 0) {
				buf.append(" limit ");
				buf.append(start);
				buf.append(",");
				buf.append(limit);
			}
			String sql = buf.toString();
			//			System.out.println("==>>"+sql);
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				list.add(getProductsItem2(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}
	/**
	 * 获取购买排名前100的产品
	 */
	public List getProducts(String condition, int start, int limit,
			String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			StringBuffer buf = new StringBuffer();
			buf
			.append("select * from product a left outer join product_status b on status=b.id left outer join product_proxy c on proxy_id=c.id where a.status < 100 ");
			if (condition != null && condition.length() > 0) {
				buf.append(" and ");
				buf.append(condition);
			}
			if (orderBy != null && orderBy.length() > 0) {
				buf.append(" order by ");
				buf.append(orderBy);
			}
			if (start >= 0 && limit > 0) {
				buf.append(" limit ");
				buf.append(start);
				buf.append(",");
				buf.append(limit);
			}

			String sql = buf.toString();
			st = conn.createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				list.add(getProductsItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getProductCount(int)
	 */
	public int getProductCount(String condition) {
		return getInt("select count(*) from product where " + condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getOrderIdByProduct(int)
	 */
	public int getOrderIdByProduct(int id) {
		return getInt("select order_id from user_order_product where id=" + id);
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getOrderIdByProduct(int)
	 */
	public int getOrderIdByPromotionProduct(int id) {
		return getInt("select order_id from user_order_promotion_product where id=" + id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#addProduct(adultadmin.action.vo.voProduct)
	 */
	public void addProduct(voProduct vo) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("INSERT INTO product(name,price,parent_id1,parent_id2,parent_id3,intro,pic,pic2,status,proxy_id,price2,price3,proxys_name,remark,unit,intro2,oriname,pic3,stock,deputize_price, images"
					+ ", guanggao, gongxiao, shiyongrenqun, shiyongfangfa, zhuyishixiang, tebietishi, baozhuangzhongliang, baozhuangdaxiao, chanpinchengfen, changshang, baozhiqi, chucangfangfa, pizhunwenhao, changshangjieshao, fuwuchengnuo, rank, display_order, stock_gd, price4,stock_standard_bj,stock_standard_gd,stock_line_bj,stock_line_gd,stock_status,is_package,brand,show_package,chanpinzhongliang,chanpinchicun,bj_stockin,gd_stockin,has_present"
					+ ") VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			pst.setString(1, vo.getName());
			pst.setFloat(2, vo.getPrice());
			pst.setInt(3, vo.getParentId1());
			pst.setInt(4, vo.getParentId2());
			pst.setInt(5, vo.getParentId3());
			pst.setString(6, vo.getIntro());
			pst.setString(7, vo.getPic());
			pst.setString(8, vo.getPic2());
			pst.setInt(9, vo.getStatus());
			pst.setInt(10, vo.getProxyId());
			pst.setFloat(11, vo.getPrice2());
			pst.setFloat(12, vo.getPrice3());
			pst.setString(13, vo.getProxysName());
			pst.setString(14, vo.getRemark());
			pst.setString(15, vo.getUnit());
			pst.setString(16, vo.getIntro2());
			pst.setString(17, vo.getOriname());
			pst.setString(18, vo.getPic3());
			pst.setInt(19, vo.getStock());
			pst.setFloat(20, vo.getDeputizePrice());
			pst.setString(21, vo.getImages());
			pst.setString(22, vo.getGuanggao());
			pst.setString(23, vo.getGongxiao());
			pst.setString(24, vo.getShiyongrenqun());
			pst.setString(25, vo.getShiyongfangfa());
			pst.setString(26, vo.getZhuyishixiang());
			pst.setString(27, vo.getTebietishi());
			pst.setString(28, vo.getBaozhuangzhongliang());
			pst.setString(29, vo.getBaozhuangdaxiao());
			pst.setString(30, vo.getChanpinchengfen());
			pst.setString(31, vo.getChangshang());
			pst.setString(32, vo.getBaozhiqi());
			pst.setString(33, vo.getChucangfangfa());
			pst.setString(34, vo.getPizhunwenhao());
			pst.setString(35, vo.getChangshangjieshao());
			pst.setString(36, vo.getFuwuchengnuo());
			pst.setInt(37, vo.getRank());
			pst.setInt(38, vo.getDisplayOrder());
			pst.setInt(39, vo.getStockGd());
			pst.setFloat(40, vo.getPrice4());
			pst.setInt(41, vo.getStockStandardBj());
			pst.setInt(42, vo.getStockStandardGd());
			pst.setInt(43, vo.getStockLineBj());
			pst.setInt(44, vo.getStockLineGd());
			pst.setInt(45, vo.getStockStatus());
			pst.setInt(46, vo.getIsPackage());
			pst.setInt(47, vo.getBrand());
			pst.setInt(48, vo.getShowPackage());
			pst.setString(49, vo.getChanpinzhongliang());
			pst.setString(50, vo.getChanpinchicun());
			pst.setString(51, vo.getBjStockin());
			pst.setString(52, vo.getGdStockin());
			pst.setInt(53, vo.getHasPresent());
			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}

		// 生成产品编号
		int id = getLastInsertId("product");

		vo.setId(id);

		updateProductCode(id);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#updateProductCode(int)
	 */
	public void updateProductCode(int id) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update product set code=concat((select code from catalog where id=product.parent_id1),(ifnull((select code from catalog where id=product.parent_id2),0)),product.id) where id="
					+ String.valueOf(id));

			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#deleteProduct(int)
	 */
	public void deleteProduct(int id) {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("delete from product where id=?");
			pst.setInt(1, id);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	/*
	 * (non-Javadoc)
	 * 该方法已经废弃，移动到AdminService中了
	 */
	public voUser getAdmin(String username, String password) {
//		PreparedStatement pst = null;
//		ResultSet rs = null;
//		voUser vo = null;
//		try {
//			pst = conn
//			.prepareStatement("select * from user u join user_permission up on u.id=up.user_id where username=? and password=? and security_level>=5");
//			pst.setString(1, username);
//			pst.setString(2, password);
//
//			rs = pst.executeQuery();
//			if (rs.next()) {
//				vo = new voUser();
//				vo.setId(rs.getInt("id"));
//				vo.setFlag(rs.getInt("flag"));
//				vo.setNick(rs.getString("nick"));
//				vo.setSecurityLevel(rs.getInt("security_level"));
//				vo.setPermission(rs.getInt("permission"));
//				vo.setGroupId(rs.getInt("group_id"));
//				vo.setGroupId2(rs.getInt("group_id2"));
//				vo.setGroupId3(rs.getInt("group_id3"));
//				vo.setGroups(rs.getString("groups").split(","));
//				vo.setBinaryFlag(new BinaryFlag(rs.getBytes("flags")));
//				vo.setUsername(username);
//				vo.setPassword(rs.getString("password"));
//			}
//
//		} catch (SQLException sqle) {
//			sqle.printStackTrace();
//		} finally {
//			closeStatement(pst);
//			closeResultSet(rs);
//		}
//		return vo;
		return null;
	}

	public voUser getUser(int userId) {
		Statement st = null;
		ResultSet rs = null;
		voUser vo = null;
		String query = "select * from user u left outer join user_permission up on u.id=up.user_id where u.id = "
			+ userId;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()) {
				vo = new voUser();
				vo.setId(rs.getInt("u.id"));
				vo.setFlag(rs.getInt("flag"));
				vo.setUsername(rs.getString("username"));
				vo.setAddress(rs.getString("address"));
				vo.setCp(rs.getString("cp"));
				vo.setCreateDatetime(rs.getString("create_datetime"));
				vo.setName(rs.getString("name"));
				vo.setNick(rs.getString("nick"));
				vo.setPhone(rs.getString("phone"));
				vo.setPostcode(rs.getString("postcode"));
				vo.setUa(rs.getString("ua"));
				vo.setReimburse(rs.getFloat("reimburse"));
				vo.setOrderReimburse(rs.getFloat("order_reimburse"));
				vo.setSecurityLevel(rs.getInt("security_level"));
				vo.setPermission(rs.getInt("permission"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return vo;
	}

	public voUser getAdminUser(int userId) {
		Statement st = null;
		ResultSet rs = null;
		voUser vo = null;
		String query = "select * from admin_user u left outer join user_permission up on u.id=up.user_id where u.id = "
			+ userId;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()) {
				vo = new voUser();
				vo.setId(rs.getInt("u.id"));
				vo.setFlag(rs.getInt("flag"));
				vo.setUsername(rs.getString("username"));
//				vo.setAddress(rs.getString("address"));
//				vo.setCp(rs.getString("cp"));
				vo.setCreateDatetime(rs.getString("create_datetime"));
				vo.setName(rs.getString("name"));
				vo.setNick(rs.getString("nick"));
//				vo.setPhone(rs.getString("phone"));
//				vo.setPostcode(rs.getString("postcode"));
//				vo.setUa(rs.getString("ua"));
//				vo.setReimburse(rs.getFloat("reimburse"));
//				vo.setOrderReimburse(rs.getFloat("order_reimburse"));
				vo.setSecurityLevel(rs.getInt("security_level"));
				vo.setPermission(rs.getInt("permission"));
				vo.setUsername2(rs.getString("username2"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return vo;
	}
	
	public voUser getUser(String username) {
		Statement st = null;
		ResultSet rs = null;
		voUser vo = null;
		String query = "select * from user u left outer join user_permission up on u.id=up.user_id where u.username = '"
			+ username + "'";
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()) {
				vo = new voUser();
				vo.setId(rs.getInt("u.id"));
				vo.setFlag(rs.getInt("flag"));
				vo.setUsername(rs.getString("username"));
				vo.setAddress(rs.getString("address"));
				vo.setCp(rs.getString("cp"));
				vo.setCreateDatetime(rs.getString("create_datetime"));
				vo.setName(rs.getString("name"));
				vo.setNick(rs.getString("nick"));
				vo.setPhone(rs.getString("phone"));
				vo.setPostcode(rs.getString("postcode"));
				vo.setUa(rs.getString("ua"));
				vo.setReimburse(rs.getFloat("reimburse"));
				vo.setOrderReimburse(rs.getFloat("order_reimburse"));
				vo.setSecurityLevel(rs.getInt("security_level"));
				vo.setPermission(rs.getInt("permission"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return vo;
	}

	public voUser getAdminUser(String username) {
		Statement st = null;
		ResultSet rs = null;
		voUser vo = null;
		String query = "select * from admin_user u left outer join user_permission up on u.id=up.user_id where u.username = '"
			+ username + "'";
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			if (rs.next()) {
				vo = new voUser();
				vo.setId(rs.getInt("u.id"));
				vo.setFlag(rs.getInt("flag"));
				vo.setUsername(rs.getString("username"));
//				vo.setAddress(rs.getString("address"));
//				vo.setCp(rs.getString("cp"));
				vo.setCreateDatetime(rs.getString("create_datetime"));
				vo.setName(rs.getString("name"));
				vo.setNick(rs.getString("nick"));
//				vo.setPhone(rs.getString("phone"));
//				vo.setPostcode(rs.getString("postcode"));
//				vo.setUa(rs.getString("ua"));
//				vo.setReimburse(rs.getFloat("reimburse"));
//				vo.setOrderReimburse(rs.getFloat("order_reimburse"));
				vo.setSecurityLevel(rs.getInt("security_level"));
				vo.setPermission(rs.getInt("permission"));
				vo.setUsername2(rs.getString("username2"));
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return vo;
	}
	
	public List getAdminList(String query) {
		Statement st = null;
		ResultSet rs = null;
		voUser vo = null;
		List list = new ArrayList();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			while (rs.next()) {
				vo = new voUser();
				vo.setId(rs.getInt("u.id"));
				vo.setFlag(rs.getInt("flag"));
				vo.setUsername(rs.getString("username"));
				vo.setCreateDatetime(rs.getString("create_datetime"));
				vo.setName(rs.getString("name"));
				vo.setNick(rs.getString("nick"));
				vo.setSecurityLevel(rs.getInt("security_level"));
				vo.setPermission(rs.getInt("permission"));
				vo.setUsername2(rs.getString("username2"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getCatalogs()
	 */
	public List getCatalogs() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = conn
			.prepareStatement("select * from catalog order by parent_id, 0 + code ");

			rs = pst.executeQuery();
			while (rs.next()) {
				voCatalog vo = new voCatalog();
				vo.setId(rs.getInt("id"));
				vo.setParentId(rs.getInt("parent_id"));
				vo.setName(rs.getString("name"));
				vo.setHide(rs.getInt("hide"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	public List getCatalogs(String condition) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();

		String sql = "select * from catalog";
		if(condition!=null&&!condition.equals("")){
			sql = sql + " where "+condition;
		}
		sql = sql+" order by parent_id, 0 + code ";

		try {
			pst = conn
			.prepareStatement(sql);

			rs = pst.executeQuery();
			while (rs.next()) {
				voCatalog vo = new voCatalog();
				vo.setId(rs.getInt("id"));
				vo.setParentId(rs.getInt("parent_id"));
				vo.setName(rs.getString("name"));
				vo.setHide(rs.getInt("hide"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	public float getOrderPrice3(int orderId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		float price3 = 0;
		try {
			Statement st = conn.createStatement();
			rs = st.executeQuery("select sum(price3 * count) from user_order_product where order_id=" + orderId);
			if(rs.next()) {
				price3 += rs.getFloat(1);
			}
			rs.close();
			rs = st.executeQuery("select sum(price3 * count) from user_order_present where order_id=" + orderId);
			if(rs.next()) {
				price3 += rs.getFloat(1);
			}
			rs.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		return price3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getOrder(int)
	 */
	public voOrder getOrder(int id) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		voOrder vo = null;
		try {
			pst = conn
			.prepareStatement("select * from user_order a left outer join user_order_status b on a.status=b.id left join user_order_lack_deal uold on a.id = uold.id where a.id=?");
			pst.setInt(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				vo = new voOrder();
				vo.setId(rs.getInt("a.id"));
				vo.setName(rs.getString("a.name"));
				vo.setPhone(rs.getString("phone"));
				vo.setAddress(rs.getString("address"));
				vo.setPostcode(rs.getString("postcode"));
				vo.setBuyMode(rs.getInt("buy_mode"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setCode(rs.getString("code"));
				vo.setUserId(rs.getInt("user_id"));
				vo.setStatus(rs.getInt("status"));
				vo.setOperator(rs.getString("operator"));
				vo.setRemark(rs.getString("remark"));
				vo.setDiscount(rs.getFloat("discount"));
				vo.setPrice(rs.getFloat("price"));
				vo.setDeliverType(rs.getInt("deliver_type"));
				vo.setRemitType(rs.getInt("remit_type"));
				vo.setCp(rs.getString("cp"));
				vo.setPhone2(rs.getString("phone2"));
				vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
				vo.setFr(rs.getInt("fr"));
				vo.setStatusName(rs.getString("b.name"));
				vo.setAgent(rs.getInt("agent"));
				vo.setAgentMark(rs.getString("agent_mark"));
				vo.setAgentRemark(rs.getString("agent_remark"));
				vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
				vo.setIsReimburse(rs.getInt("is_reimburse"));
				vo.setRealPay(rs.getFloat("real_pay"));
				vo.setPostage(rs.getInt("postage"));
				vo.setIsOrder(rs.getInt("is_order"));
				vo.setImages(rs.getString("images"));
				vo.setAreano(rs.getInt("areano"));
				vo.setPrePayType(rs.getInt("pre_pay_type"));
				vo.setIsOlduser(rs.getInt("is_olduser"));
				vo.setSuffix(rs.getFloat("suffix"));
				vo.setStockout(rs.getInt("stockout"));
				vo.setContactTime(rs.getInt("contact_time"));
				vo.setUnitedOrders(rs.getString("united_orders"));
				vo.setFlat(rs.getInt("flat"));
				vo.setHasAddPoint(rs.getInt("has_add_point"));
				vo.setGender(rs.getInt("gender"));
				vo.setWebRemark(rs.getString("web_remark"));
				vo.setEmail(rs.getString("email"));
				vo.setOriginOrderId(rs.getInt("origin_order_id"));
				vo.setNewOrderId(rs.getInt("new_order_id"));
				vo.setSellerId(rs.getInt("seller_id"));

				vo.setDealDetail(rs.getString("deal_detail"));
				vo.setCpaBonus(rs.getInt("cpa_bonus"));
				vo.setCpaPay(rs.getInt("cpa_pay"));
				vo.setCpaStatus(rs.getInt("cpa_status"));

				vo.setSellerCheckStatus(rs.getInt("seller_check_status"));
				vo.setStockoutRemark(rs.getString("stockout_remark"));
				vo.setConsigner(rs.getString("consigner"));

				vo.setDeliver(rs.getInt("deliver"));
				vo.setProductType(rs.getInt("product_type"));
				vo.setStockoutDeal(rs.getInt("stockout_deal"));
				vo.setBalanceStatus(rs.getInt("balance_status"));

				vo.setDprice(rs.getFloat("dprice"));

				vo.setOrderType(rs.getInt("order_type"));
				
				vo.setNextLackDealDatetime(rs.getString("next_deal_datetime"));
				vo.setLackDealAdminId(rs.getInt("uold.admin_id"));
			}

		} catch (Exception sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return vo;
	}

	public voOrder getOrder(String condition) {
		Statement st = null;
		ResultSet rs = null;
		voOrder vo = null;
		try {
			st = conn.createStatement();
			String query = "select *,(select b.name from user_order_status b where b.id=a.status) as status_name from user_order a where "
				+ condition;

			rs = st.executeQuery(query);
			if (rs.next()) {
				vo = new voOrder();
				vo.setId(rs.getInt("a.id"));
				vo.setName(rs.getString("name"));
				vo.setPhone(rs.getString("phone"));
				vo.setAddress(rs.getString("address"));
				vo.setPostcode(rs.getString("postcode"));
				vo.setBuyMode(rs.getInt("buy_mode"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setCode(rs.getString("code"));
				vo.setUserId(rs.getInt("user_id"));
				vo.setStatus(rs.getInt("status"));
				vo.setOperator(rs.getString("operator"));
				vo.setRemark(rs.getString("remark"));
				vo.setDiscount(rs.getFloat("discount"));
				vo.setPrice(rs.getFloat("price"));
				vo.setDeliverType(rs.getInt("deliver_type"));
				vo.setDprice(rs.getFloat("dprice"));
				vo.setRemitType(rs.getInt("remit_type"));
				vo.setCp(rs.getString("cp"));
				vo.setPhone2(rs.getString("phone2"));
				vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
				vo.setFr(rs.getInt("fr"));
				vo.setAgent(rs.getInt("agent"));
				vo.setAgentMark(rs.getString("agent_mark"));
				vo.setAgentRemark(rs.getString("agent_remark"));
				vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
				vo.setIsReimburse(rs.getInt("is_reimburse"));
				vo.setRealPay(rs.getFloat("real_pay"));
				vo.setPostage(rs.getInt("postage"));
				vo.setIsOrder(rs.getInt("is_order"));
				vo.setImages(rs.getString("images"));
				vo.setAreano(rs.getInt("areano"));
				vo.setPrePayType(rs.getInt("pre_pay_type"));
				vo.setIsOlduser(rs.getInt("is_olduser"));
				vo.setSuffix(rs.getFloat("suffix"));
				vo.setStockout(rs.getInt("stockout"));
				vo.setContactTime(rs.getInt("contact_time"));
				vo.setUnitedOrders(rs.getString("united_orders"));
				vo.setFlat(rs.getInt("flat"));
				vo.setHasAddPoint(rs.getInt("has_add_point"));
				vo.setGender(rs.getInt("gender"));
				vo.setWebRemark(rs.getString("web_remark"));
				vo.setEmail(rs.getString("email"));
				vo.setStatusName(rs.getString("status_name"));
				vo.setOriginOrderId(rs.getInt("origin_order_id"));
				vo.setNewOrderId(rs.getInt("new_order_id"));

				vo.setDealDetail(rs.getString("deal_detail"));
				vo.setCpaBonus(rs.getInt("cpa_bonus"));
				vo.setCpaPay(rs.getInt("cpa_pay"));
				vo.setCpaStatus(rs.getInt("cpa_status"));

				vo.setSellerCheckStatus(rs.getInt("seller_check_status"));
				vo.setConsigner(rs.getString("consigner"));

				vo.setDeliver(rs.getInt("deliver"));
				vo.setProductType(rs.getInt("product_type"));
				vo.setStockoutDeal(rs.getInt("stockout_deal"));
				vo.setBalanceStatus(rs.getInt("balance_status"));
				vo.setOrderType(rs.getInt("order_type"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return vo;
	}

	public int getOrderCount(String condition) {
		if(StringUtil.isNull(condition)){
			return 0;
		}
		return getInt("select count(*) from user_order where " + condition);
	}
	
	public int getLackOrderCount(String condition) {
		if(StringUtil.isNull(condition)){
			return 0;
		}
		return getInt("select count(distinct(a.id)) from user_order a join user_order_lack_deal uold on a.id = uold.id join user_order_product uop on a.id = uop.order_id join product p on uop.product_id = p.id where " + condition);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getOrderCount(int)
	 */
	public int getOrderCount(int status, int buymode) {
		return getInt("select count(*) from user_order where status=" + status
				+ " and buy_mode=" + buymode);
	}

	public int getOrderCount(int status, int buymode, int flat) {
		return getInt("select count(*) from user_order where status=" + status
				+ " and buy_mode=" + buymode + " and flat = " + flat);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getOrderProducts(int)
	 */
	public List getOrderProducts(int orderId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = conn
			.prepareStatement("select *,GROUP_CONCAT(distinct d.name) sName from user_order_product a,(product b join product_status ps on b.status=ps.id) " +
					"left  join  product_supplier c on b.id=c.product_id left join supplier_standard_info d on d.id=c.supplier_id and d.status=1 " +
			"where a.order_id=? and a.product_id=b.id group by b.id order by b.rank asc, b.display_order asc, a.count desc;");
			pst.setInt(1, orderId);

			rs = pst.executeQuery();
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setOriname(rs.getString("b.oriname"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("sName"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				vo.setProductPrice(rs.getFloat("a.product_price"));
				vo.setDiscountPrice(rs.getFloat("a.discount_price"));
				vo.setProductDiscountId(rs.getInt("a.product_discount_id"));
				vo.setProductPreferenceId(rs.getInt("a.product_preference_id"));
				vo.setLackRemark(rs.getString("b.remark"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getOrderProducts(int)
	 */
	public List getOrderProducts1(int orderId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = conn
			.prepareStatement("select *,GROUP_CONCAT(distinct d.name) sName from user_order_promotion_product a,(product b join product_status ps on b.status=ps.id) " +
					"left  join  product_supplier c on b.id=c.product_id left join supplier_standard_info d on d.id=c.supplier_id and d.status=1 " +
			"where a.order_id=? and a.product_id=b.id group by b.id order by b.rank asc, b.display_order asc, a.count desc;");
			pst.setInt(1, orderId);
			
			rs = pst.executeQuery();
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setOriname(rs.getString("b.oriname"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("sName"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				vo.setProductPrice(rs.getFloat("a.product_price"));
				vo.setDiscountPrice(rs.getFloat("a.discount_price"));
//				vo.setProductDiscountId(rs.getInt("a.product_discount_id"));
//				vo.setProductPreferenceId(rs.getInt("a.product_preference_id"));
				vo.setLackRemark(rs.getString("b.remark"));
				vo.setFlag(rs.getInt("flag"));
				vo.setPromotionId(rs.getInt("promotion_id"));
				list.add(vo);
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	public List getOrderProducts(String condition) {
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		List list = new ArrayList();
		try {
			st = conn.createStatement();
			sql = "select * from user_order_product a,(product b join product_status ps on b.status=ps.id) left outer join product_proxy c on b.proxy_id=c.id where a.product_id=b.id and ";
			sql = sql+condition;

			rs = st.executeQuery(sql);
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setDiscountPrice(rs.getFloat("a.discount_price"));
				vo.setName(rs.getString("b.name"));
				vo.setOriname(rs.getString("b.oriname"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("c.name"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				vo.setProductPrice(rs.getFloat("a.product_price"));
				vo.setDiscountPrice(rs.getFloat("a.discount_price"));
				vo.setProductDiscountId(rs.getInt("a.product_discount_id"));
				vo.setLackRemark(rs.getString("b.remark"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}


	public List getOrderProductsSplit(int orderId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = conn
			.prepareStatement("select *,GROUP_CONCAT(distinct ssi.name)supplierName from user_order_product_split a,(product b join product_status ps on b.status=ps.id) left outer join product_supplier c on b.id=c.product_id left outer join supplier_standard_info ssi on ssi.id = c.supplier_id and ssi.status=1 where a.order_id=?  and a.product_id=b.id group by b.id order by b.rank asc, b.display_order asc, a.count desc");
			pst.setInt(1, orderId);

			rs = pst.executeQuery();
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setOriname(rs.getString("b.oriname"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("supplierName"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				vo.setLackRemark(rs.getString("b.remark"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	public voOrderProduct getOrderProduct(int orderId, String productCode) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn
			.prepareStatement("select * from user_order_product a,(product b join product_status ps on b.status=ps.id) left outer join product_proxy c on b.proxy_id=c.id where a.order_id=? and b.code=? and a.product_id=b.id order by a.count desc");
			pst.setInt(1, orderId);
			pst.setString(2, productCode);

			rs = pst.executeQuery();
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("c.name"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				return vo;
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return null;
	}
	public voOrderProduct getOrderPromotionProduct(int orderId, String productCode) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn
			.prepareStatement("select * from user_order_promotion_product a,(product b join product_status ps on b.status=ps.id) left outer join product_proxy c on b.proxy_id=c.id where a.order_id=? and b.code=? and a.product_id=b.id and a.point=0 order by a.count desc");
			pst.setInt(1, orderId);
			pst.setString(2, productCode);
			
			rs = pst.executeQuery();
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("c.name"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				return vo;
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return null;
	}

	public voOrderProduct getOrderProduct(int orderProductId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select * from user_order_product a,(product b join product_status ps on b.status=ps.id) left outer join product_proxy c on b.proxy_id=c.id where a.id=? and a.product_id=b.id order by a.count desc");
			pst.setInt(1, orderProductId);

			rs = pst.executeQuery();
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("c.name"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				vo.setDiscountPrice(rs.getFloat("a.discount_price"));
				return vo;
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return null;
	}
	public voOrderProduct getOrderPromotionProduct(int orderProductId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select * from user_order_promotion_product a,(product b join product_status ps on b.status=ps.id) left outer join product_proxy c on b.proxy_id=c.id where a.id=? and a.product_id=b.id order by a.count desc");
			pst.setInt(1, orderProductId);
			
			rs = pst.executeQuery();
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("c.name"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				vo.setDiscountPrice(rs.getFloat("a.discount_price"));
				vo.setFlag(rs.getInt("a.flag"));
				return vo;
			}
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return null;
	}
	public voOrderProduct getOrderProduct(String condition) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select * from user_order_product  where "+condition);
			rs = pst.executeQuery();
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("id"));
				vo.setCount(rs.getInt("count"));
				return vo;
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return null;
	}

	public voOrderProduct getOrderProductSplit(int orderId, String productCode) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn
			.prepareStatement("select * from user_order_product_split a,(product b join product_status ps on b.status=ps.id) left outer join product_proxy c on b.proxy_id=c.id where a.order_id=? and b.code=? and a.product_id=b.id order by a.count desc");
			pst.setInt(1, orderId);
			pst.setString(2, productCode);

			rs = pst.executeQuery();
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("c.name"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				return vo;
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return null;
	}

	public List getOrderPresents(int orderId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = conn
			.prepareStatement("select *,GROUP_CONCAT(distinct ssi.name)supplierName from user_order_present a,(product b join product_status ps on b.status=ps.id) left outer join product_supplier c on b.id=c.product_id left outer join supplier_standard_info ssi on ssi.id = c.supplier_id and ssi.status=1 where a.order_id=?  and a.product_id=b.id group by b.id order by a.count desc");
			pst.setInt(1, orderId);

			rs = pst.executeQuery();
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("supplierName"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setOriname(rs.getString("b.oriname"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				vo.setLackRemark(rs.getString("b.remark"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}


	public List getOrderPresents(String condition) {
		ResultSet rs = null;
		Statement st = null;
		List list = new ArrayList();
		try {
			st=conn.createStatement();
			String sql = "select * from user_order_present a,(product b join product_status ps on b.status=ps.id) left outer join product_proxy c on b.proxy_id=c.id where a.product_id=b.id and ";
			sql = sql+condition;
			rs = st.executeQuery(sql);
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("c.name"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setOriname(rs.getString("b.oriname"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getOrderPresentsSplit(int orderId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = conn
			.prepareStatement("select *,GROUP_CONCAT(distinct ssi.name)supplierName from user_order_present_split a,(product b join product_status ps on b.status=ps.id) left outer join product_supplier c on b.id=c.product_id left outer join supplier_standard_info ssi on ssi.id = c.supplier_id and ssi.status=1 where a.order_id=?  and a.product_id=b.id group by b.id order by a.count desc");
			pst.setInt(1, orderId);

			rs = pst.executeQuery();
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("supplierName"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setOriname(rs.getString("b.oriname"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				vo.setLackRemark(rs.getString("b.remark"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	public voOrderProduct getOrderPresent(int orderId, String productCode) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn
			.prepareStatement("select * from user_order_present a,(product b join product_status ps on b.status=ps.id) left outer join product_proxy c on b.proxy_id=c.id where a.order_id=? and b.code=? and a.product_id=b.id order by a.count desc");
			pst.setInt(1, orderId);
			pst.setString(2, productCode);

			rs = pst.executeQuery();
			if (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("c.name"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				return vo;
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return null;
	}

	public voOrderProduct getOrderPresent(int orderPresentId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn
			.prepareStatement("select * from user_order_present a,(product b join product_status ps on b.status=ps.id) left outer join product_proxy c on b.proxy_id=c.id where a.id=? and a.product_id=b.id order by a.count desc");
			pst.setInt(1, orderPresentId);

			rs = pst.executeQuery();
			if (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("c.name"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				return vo;
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return null;
	}


	public voOrderProduct getOrderPresentSplit(int orderId, String productCode) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn
			.prepareStatement("select * from user_order_present_split a,(product b join product_status ps on b.status=ps.id) left outer join product_proxy c on b.proxy_id=c.id where a.order_id=? and b.code=? and a.product_id=b.id order by a.count desc");
			pst.setInt(1, orderId);
			pst.setString(2, productCode);

			rs = pst.executeQuery();
			if (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setId(rs.getInt("a.id"));
				vo.setCount(rs.getInt("a.count"));
				vo.setName(rs.getString("b.name"));
				vo.setProductId(rs.getInt("b.id"));
				vo.setPrice(rs.getFloat("b.price"));
				vo.setGroupBuyPrice(rs.getFloat("b.group_buy_price"));
				vo.setCode(rs.getString("b.code"));
				vo.setProxyName(rs.getString("c.name"));
				vo.setStock(rs.getInt("b.stock"));
				vo.setStockGd(rs.getInt("b.stock_gd"));
				vo.setProductStatus(rs.getInt("ps.id"));
				vo.setProductStatusName(rs.getString("ps.name"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setParentId1(rs.getInt("b.parent_id1"));
				vo.setParentId2(rs.getInt("b.parent_id2"));
				return vo;
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getOrders(int, int, int)
	 */
	public List getOrders(int status, int buymode, int start, int limit) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		String query = null;
		try {
			st = conn.createStatement();
			query = "select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products from user_order a  left outer join user_order_status b on a.status=b.id where status="
				+ status
				+ " and buy_mode="
				+ buymode
				+ " order by a.id desc limit " + start + ", " + limit;
			rs = st.executeQuery(query);
			while (rs.next()) {
				list.add(getOrdersItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getOrders2(int status, int buymode, int start, int limit, int minId) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		String query = null;
		try {
			st = conn.createStatement();
			query = "select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products from user_order a  left outer join user_order_status b on a.status=b.id where status="
				+ status
				+ " and buy_mode="
				+ buymode
				+ " and a.id > "
				+ minId
				+ " order by a.id desc limit " + start + ", " + limit;
			rs = st.executeQuery(query);
			while (rs.next()) {
				list.add(getOrdersItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getOrders(int status, int buymode, int flat, int start,
			int limit) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		String query = null;
		try {
			st = conn.createStatement();
			query = "select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products from user_order a  left outer join user_order_status b on a.status=b.id where status="
				+ status
				+ " and buy_mode="
				+ buymode
				+ " and flat = "
				+ flat
				+ " order by a.id desc limit "
				+ start
				+ ", "
				+ limit;
			rs = st.executeQuery(query);
			while (rs.next()) {
				list.add(getOrdersItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getOrders(int status, int buymode, int flat, int start,
			int limit, int minId) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		String query = null;
		try {
			st = conn.createStatement();
			query = "select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products from user_order a  left outer join user_order_status b on a.status=b.id where status="
				+ status
				+ " and buy_mode="
				+ buymode
				+ " and flat = "
				+ flat
				+ " and a.id > "
				+ minId
				+ " order by a.id desc limit "
				+ start
				+ ", "
				+ limit;
			rs = st.executeQuery(query);
			while (rs.next()) {
				list.add(getOrdersItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getOrdersWithProductName(String condition, int start,
			int limit, String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		
		StringBuffer buf = new StringBuffer();
		buf
		.append("select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products from user_order a  left outer join user_order_status b on a.status=b.id ");
		if (condition != null && condition.length() > 0) {
			buf.append(" where ");
			buf.append(condition);
		}
		if (limit >= 0 && start > 0) {
			buf.append(" limit ");
			buf.append(start);
			buf.append(" , ");
			buf.append(limit);
		}
		if (orderBy != null && orderBy.length() > 0) {
			buf.append(" order by ");
			buf.append(orderBy);
		}
		//            query = "select *,(select GROUP_CONCAT(d.name) from
		// user_order_product c,product d where c.product_id=d.id and
		// c.order_id=a.id) products from user_order a left outer join
		// user_order_status b on a.status=b.id where status="
		//                    + status
		//                    + " and buy_mode="
		//                    + buymode
		//                    + " order by a.id desc limit " + start + ", " + limit;
		String query = buf.toString();
		
		try {
			st = conn.createStatement();

			rs = st.executeQuery(query);
			while (rs.next()) {
				list.add(getOrdersItem(rs));
			}
			rs.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
		}
		return list;
	}

	public List getOrders(int status, int buymode, int start, int limit,
			String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		String query = null;
		try {
			st = conn.createStatement();
			StringBuffer buf = new StringBuffer();
			buf
			.append("select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products from user_order a  left outer join user_order_status b on a.status=b.id where status=");
			buf.append(status);
			buf.append(" and buy_mode=");
			buf.append(buymode);
			buf.append(" order by ");
			buf.append(orderBy);
			buf.append(" limit ");
			buf.append(start);
			buf.append(", ");
			buf.append(limit);
			//            query = "select *,(select GROUP_CONCAT(d.name) from
			// user_order_product c,product d where c.product_id=d.id and
			// c.order_id=a.id) products from user_order a left outer join
			// user_order_status b on a.status=b.id where status="
			//                    + status
			//                    + " and buy_mode="
			//                    + buymode
			//                    + " order by a.id desc limit " + start + ", " + limit;
			query = buf.toString();
			rs = st.executeQuery(query);
			while (rs.next()) {
				list.add(getOrdersItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getOrders2(int status, int buymode, int start, int limit, int minId,
			String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		String query = null;
		try {
			st = conn.createStatement();
			StringBuffer buf = new StringBuffer();
			buf
			.append("select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products from user_order a  left outer join user_order_status b on a.status=b.id where status=");
			buf.append(status);
			buf.append(" and buy_mode=");
			buf.append(buymode);
			buf.append(" and a.id > ");
			buf.append(minId);
			buf.append(" order by ");
			buf.append(orderBy);
			buf.append(" limit ");
			buf.append(start);
			buf.append(", ");
			buf.append(limit);
			//            query = "select *,(select GROUP_CONCAT(d.name) from
			// user_order_product c,product d where c.product_id=d.id and
			// c.order_id=a.id) products from user_order a left outer join
			// user_order_status b on a.status=b.id where status="
			//                    + status
			//                    + " and buy_mode="
			//                    + buymode
			//                    + " order by a.id desc limit " + start + ", " + limit;
			query = buf.toString();
			rs = st.executeQuery(query);
			while (rs.next()) {
				list.add(getOrdersItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getOrders(int status, int buymode, int flat, int start,
			int limit, String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		String query = null;
		try {
			st = conn.createStatement();
			StringBuffer buf = new StringBuffer();
			buf
			.append("select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products from user_order a  left outer join user_order_status b on a.status=b.id where status=");
			buf.append(status);
			buf.append(" and buy_mode=");
			buf.append(buymode);
			buf.append(" and flat=");
			buf.append(flat);
			buf.append(" order by ");
			buf.append(orderBy);
			buf.append(" limit ");
			buf.append(start);
			buf.append(", ");
			buf.append(limit);
			//            query = "select *,(select GROUP_CONCAT(d.name) from
			// user_order_product c,product d where c.product_id=d.id and
			// c.order_id=a.id) products from user_order a left outer join
			// user_order_status b on a.status=b.id where status="
			//                    + status
			//                    + " and buy_mode="
			//                    + buymode
			//                    + " order by a.id desc limit " + start + ", " + limit;
			query = buf.toString();
			rs = st.executeQuery(query);
			while (rs.next()) {
				list.add(getOrdersItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getOrders(int status, int buymode, int flat, int start,
			int limit,int minId, String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		String query = null;
		try {
			st = conn.createStatement();
			StringBuffer buf = new StringBuffer();
			buf
			.append("select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products from user_order a  left outer join user_order_status b on a.status=b.id where status=");
			buf.append(status);
			buf.append(" and buy_mode=");
			buf.append(buymode);
			buf.append(" and flat=");
			buf.append(flat);
			buf.append(" and a.id > ");
			buf.append(minId);
			buf.append(" order by ");
			buf.append(orderBy);
			buf.append(" limit ");
			buf.append(start);
			buf.append(", ");
			buf.append(limit);
			//            query = "select *,(select GROUP_CONCAT(d.name) from
			// user_order_product c,product d where c.product_id=d.id and
			// c.order_id=a.id) products from user_order a left outer join
			// user_order_status b on a.status=b.id where status="
			//                    + status
			//                    + " and buy_mode="
			//                    + buymode
			//                    + " order by a.id desc limit " + start + ", " + limit;
			query = buf.toString();
			rs = st.executeQuery(query);
			while (rs.next()) {
				list.add(getOrdersItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getOrders(String condition, int index, int count, String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select * from user_order a ");
		if (condition != null && condition.length() > 0) {
			buf.append(" where ");
			buf.append(condition);
		}
		if (orderBy != null && orderBy.length() > 0) {
			buf.append(" order by ");
			buf.append(orderBy);
		}
		if (index >= 0 && count > 0) {
			buf.append(" limit ");
			buf.append(index);
			buf.append(", ");
			buf.append(count);
		}
		String query = buf.toString();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			voOrder vo = null;
			while (rs.next()) {
				vo = new voOrder();
				vo.setId(rs.getInt("a.id"));
				vo.setName(rs.getString("name"));
				vo.setPhone(rs.getString("phone"));
				vo.setAddress(rs.getString("address"));
				vo.setPostcode(rs.getString("postcode"));
				vo.setBuyMode(rs.getInt("buy_mode"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setUserId(rs.getInt("user_id"));
				vo.setConfirmDatetime(rs.getTimestamp("confirm_datetime"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setPrice(rs.getFloat("price"));
				vo.setDiscount(rs.getFloat("discount"));
				vo.setDprice(rs.getFloat("dprice"));
				vo.setDeliver(rs.getInt("deliver"));
				vo.setDeliverType(rs.getInt("deliver_type"));
				vo.setRemitType(rs.getInt("remit_type"));
				vo.setStockout(rs.getInt("stockout"));
				vo.setPhone2(rs.getString("phone2"));
				vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
				vo.setOperator(rs.getString("operator"));
				vo.setConsigner(rs.getString("consigner"));
				vo.setAgent(rs.getInt("agent"));
				vo.setAgentMark(rs.getString("agent_mark"));
				vo.setAgentRemark(rs.getString("agent_remark"));
				vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
				vo.setIsReimburse(rs.getInt("is_reimburse"));
				vo.setRealPay(rs.getFloat("real_pay"));
				vo.setPostage(rs.getInt("postage"));
				vo.setIsOrder(rs.getInt("is_order"));
				vo.setAreano(rs.getInt("areano"));
				vo.setPrePayType(rs.getInt("pre_pay_type"));
				vo.setIsOlduser(rs.getInt("is_olduser"));
				vo.setSuffix(rs.getFloat("suffix"));
				vo.setContactTime(rs.getInt("contact_time"));
				vo.setUnitedOrders(rs.getString("united_orders"));
				vo.setFlat(rs.getInt("flat"));
				vo.setHasAddPoint(rs.getInt("has_add_point"));
				vo.setGender(rs.getInt("gender"));
				vo.setWebRemark(rs.getString("web_remark"));
				vo.setEmail(rs.getString("email"));
				vo.setOriginOrderId(rs.getInt("origin_order_id"));
				vo.setNewOrderId(rs.getInt("new_order_id"));
				vo.setRemark(rs.getString("remark"));

				vo.setDealDetail(rs.getString("deal_detail"));
				vo.setCpaBonus(rs.getInt("cpa_bonus"));
				vo.setCpaPay(rs.getInt("cpa_pay"));
				vo.setCpaStatus(rs.getInt("cpa_status"));

				vo.setOrderType(rs.getInt("order_type"));

				vo.setSellerCheckStatus(rs.getInt("seller_check_status"));
				vo.setLastOperTime(rs.getString("last_deal_time"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-8-27
	 * 
	 * 说明：根据条件获取订单内容， 同时获取订单内产品名称
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param condition
	 * @param index
	 * @param count
	 * @param orderBy
	 * @return
	 */
	public List getOrdersWithProName(String condition, int index, int count, String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append(
				"select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products from user_order a  left outer join user_order_status b on a.status=b.id"
				+" join user_order_lack_deal uold on a.id = uold.id");
		if (condition != null && condition.length() > 0) {
			buf.append(" where ");
			buf.append(condition);
		}
		if (orderBy != null && orderBy.length() > 0) {
			buf.append(" order by ");
			buf.append(orderBy);
		}
		if (index >= 0 && count > 0) {
			buf.append(" limit ");
			buf.append(index);
			buf.append(", ");
			buf.append(count);
		}
		String query = buf.toString();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			voOrder vo = null;
			while (rs.next()) {
				vo = new voOrder();
				vo.setId(rs.getInt("a.id"));
				vo.setName(rs.getString("a.name"));
				vo.setProducts(rs.getString("products"));
				vo.setPhone(rs.getString("phone"));
				vo.setAddress(rs.getString("address"));
				vo.setPostcode(rs.getString("postcode"));
				vo.setBuyMode(rs.getInt("buy_mode"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setUserId(rs.getInt("user_id"));
				vo.setConfirmDatetime(rs.getTimestamp("confirm_datetime"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setPrice(rs.getFloat("price"));
				vo.setDiscount(rs.getFloat("discount"));
				vo.setDeliverType(rs.getInt("deliver_type"));
				vo.setRemitType(rs.getInt("remit_type"));
				vo.setStockout(rs.getInt("stockout"));
				vo.setPhone2(rs.getString("phone2"));
				vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
				vo.setOperator(rs.getString("operator"));
				vo.setAgent(rs.getInt("agent"));
				vo.setAgentMark(rs.getString("agent_mark"));
				vo.setAgentRemark(rs.getString("agent_remark"));
				vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
				vo.setIsReimburse(rs.getInt("is_reimburse"));
				vo.setRealPay(rs.getFloat("real_pay"));
				vo.setPostage(rs.getInt("postage"));
				vo.setIsOrder(rs.getInt("is_order"));
				vo.setAreano(rs.getInt("areano"));
				vo.setPrePayType(rs.getInt("pre_pay_type"));
				vo.setIsOlduser(rs.getInt("is_olduser"));
				vo.setSuffix(rs.getFloat("suffix"));
				vo.setContactTime(rs.getInt("contact_time"));
				vo.setUnitedOrders(rs.getString("united_orders"));
				vo.setFlat(rs.getInt("flat"));
				vo.setHasAddPoint(rs.getInt("has_add_point"));
				vo.setGender(rs.getInt("gender"));
				vo.setWebRemark(rs.getString("web_remark"));
				vo.setEmail(rs.getString("email"));
				vo.setOriginOrderId(rs.getInt("origin_order_id"));
				vo.setNewOrderId(rs.getInt("new_order_id"));
				vo.setRemark(rs.getString("remark"));

				vo.setDealDetail(rs.getString("deal_detail"));
				vo.setCpaBonus(rs.getInt("cpa_bonus"));
				vo.setCpaPay(rs.getInt("cpa_pay"));
				vo.setCpaStatus(rs.getInt("cpa_status"));

				vo.setSellerCheckStatus(rs.getInt("seller_check_status"));

				vo.setOrderType(rs.getInt("order_type"));
				vo.setLastOperTime(rs.getString("last_deal_time"));
				vo.setStockoutDeal(rs.getInt("stockout_deal"));
				vo.setLackDealAdminId(rs.getInt("uold.admin_id"));

				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	/**
	 * 根据产品，及其他条件，查找订单
	 */
	public List getOrders(String condition, int productId, int index,
			int count, String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf
		.append("select * from user_order a join user_order_product b on a.id=b.order_id where b.product_id=");
		buf.append(productId);
		buf.append(" and ");
		buf.append(condition);
		if (orderBy != null && orderBy.length() > 0) {
			buf.append(" order by ");
			buf.append(orderBy);
		}
		if (index >= 0 && count > 0) {
			buf.append(" limit ");
			buf.append(index);
			buf.append(", ");
			buf.append(count);
		}
		//        String query = "select * from user_order a join user_order_product b
		// on a.id=b.order_id where " + condition
		//                + " order by " + orderBy + " limit " + index + ", " + count;
		String query = buf.toString();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			voOrder vo = null;
			while (rs.next()) {
				vo = new voOrder();
				vo.setId(rs.getInt("a.id"));
				vo.setName(rs.getString("name"));
				vo.setPhone(rs.getString("phone"));
				vo.setAddress(rs.getString("address"));
				vo.setPostcode(rs.getString("postcode"));
				vo.setBuyMode(rs.getInt("buy_mode"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setUserId(rs.getInt("user_id"));
				vo.setConfirmDatetime(rs.getTimestamp("confirm_datetime"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setPrice(rs.getFloat("price"));
				vo.setDiscount(rs.getFloat("discount"));
				vo.setDeliverType(rs.getInt("deliver_type"));
				vo.setRemitType(rs.getInt("remit_type"));
				vo.setStockout(rs.getInt("stockout"));
				vo.setPhone2(rs.getString("phone2"));
				vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
				vo.setOperator(rs.getString("operator"));
				vo.setAgent(rs.getInt("agent"));
				vo.setAgentMark(rs.getString("agent_mark"));
				vo.setAgentRemark(rs.getString("agent_remark"));
				vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
				vo.setIsReimburse(rs.getInt("is_reimburse"));
				vo.setRealPay(rs.getFloat("real_pay"));
				vo.setPostage(rs.getInt("postage"));
				vo.setIsOrder(rs.getInt("is_order"));
				vo.setAreano(rs.getInt("areano"));
				vo.setPrePayType(rs.getInt("pre_pay_type"));
				vo.setIsOlduser(rs.getInt("is_olduser"));
				vo.setSuffix(rs.getFloat("suffix"));
				vo.setContactTime(rs.getInt("contact_time"));
				vo.setUnitedOrders(rs.getString("united_orders"));
				vo.setFlat(rs.getInt("flat"));
				vo.setHasAddPoint(rs.getInt("has_add_point"));
				vo.setGender(rs.getInt("gender"));
				vo.setWebRemark(rs.getString("web_remark"));
				vo.setEmail(rs.getString("email"));
				vo.setOriginOrderId(rs.getInt("origin_order_id"));
				vo.setNewOrderId(rs.getInt("new_order_id"));

				vo.setDealDetail(rs.getString("deal_detail"));
				vo.setCpaBonus(rs.getInt("cpa_bonus"));
				vo.setCpaPay(rs.getInt("cpa_pay"));
				vo.setCpaStatus(rs.getInt("cpa_status"));

				vo.setSellerCheckStatus(rs.getInt("seller_check_status"));
				vo.setLastOperTime(rs.getString("last_deal_time"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getOrdersByPresent(String condition, int productId, int index,
			int count, String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf
		.append("select * from user_order a join user_order_present b on a.id=b.order_id where b.product_id=");
		buf.append(productId);
		buf.append(" and ");
		buf.append(condition);
		if (orderBy != null && orderBy.length() > 0) {
			buf.append(" order by ");
			buf.append(orderBy);
		}
		if (index >= 0 && count > 0) {
			buf.append(" limit ");
			buf.append(index);
			buf.append(", ");
			buf.append(count);
		}
		//        String query = "select * from user_order a join user_order_product b
		// on a.id=b.order_id where " + condition
		//                + " order by " + orderBy + " limit " + index + ", " + count;
		String query = buf.toString();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			voOrder vo = null;
			while (rs.next()) {
				vo = new voOrder();
				vo.setId(rs.getInt("a.id"));
				vo.setName(rs.getString("name"));
				vo.setPhone(rs.getString("phone"));
				vo.setAddress(rs.getString("address"));
				vo.setPostcode(rs.getString("postcode"));
				vo.setBuyMode(rs.getInt("buy_mode"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setUserId(rs.getInt("user_id"));
				vo.setConfirmDatetime(rs.getTimestamp("confirm_datetime"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setPrice(rs.getFloat("price"));
				vo.setDiscount(rs.getFloat("discount"));
				vo.setDeliverType(rs.getInt("deliver_type"));
				vo.setRemitType(rs.getInt("remit_type"));
				vo.setStockout(rs.getInt("stockout"));
				vo.setPhone2(rs.getString("phone2"));
				vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
				vo.setOperator(rs.getString("operator"));
				vo.setAgent(rs.getInt("agent"));
				vo.setAgentMark(rs.getString("agent_mark"));
				vo.setAgentRemark(rs.getString("agent_remark"));
				vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
				vo.setIsReimburse(rs.getInt("is_reimburse"));
				vo.setRealPay(rs.getFloat("real_pay"));
				vo.setPostage(rs.getInt("postage"));
				vo.setIsOrder(rs.getInt("is_order"));
				vo.setAreano(rs.getInt("areano"));
				vo.setPrePayType(rs.getInt("pre_pay_type"));
				vo.setIsOlduser(rs.getInt("is_olduser"));
				vo.setSuffix(rs.getFloat("suffix"));
				vo.setContactTime(rs.getInt("contact_time"));
				vo.setUnitedOrders(rs.getString("united_orders"));
				vo.setFlat(rs.getInt("flat"));
				vo.setHasAddPoint(rs.getInt("has_add_point"));
				vo.setGender(rs.getInt("gender"));
				vo.setWebRemark(rs.getString("web_remark"));
				vo.setEmail(rs.getString("email"));
				vo.setOriginOrderId(rs.getInt("origin_order_id"));
				vo.setNewOrderId(rs.getInt("new_order_id"));

				vo.setDealDetail(rs.getString("deal_detail"));
				vo.setCpaBonus(rs.getInt("cpa_bonus"));
				vo.setCpaPay(rs.getInt("cpa_pay"));
				vo.setCpaStatus(rs.getInt("cpa_status"));

				vo.setSellerCheckStatus(rs.getInt("seller_check_status"));
				vo.setLastOperTime(rs.getString("last_deal_time"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}
	
	/**
	 * 根据产品，及其他条件，查找订单
	 */
	public List getOrdersByProducts(String condition, int index,
			int count, String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf
		.append("select * from user_order a join user_order_product b on a.id=b.order_id where ");
		buf.append(condition);
		if (orderBy != null && orderBy.length() > 0) {
			buf.append(" order by ");
			buf.append(orderBy);
		}
		if (index >= 0 && count > 0) {
			buf.append(" limit ");
			buf.append(index);
			buf.append(", ");
			buf.append(count);
		}
		//        String query = "select * from user_order a join user_order_product b
		// on a.id=b.order_id where " + condition
		//                + " order by " + orderBy + " limit " + index + ", " + count;
		String query = buf.toString();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			voOrder vo = null;
			while (rs.next()) {
				vo = new voOrder();
				vo.setId(rs.getInt("a.id"));
				vo.setName(rs.getString("name"));
				vo.setPhone(rs.getString("phone"));
				vo.setAddress(rs.getString("address"));
				vo.setPostcode(rs.getString("postcode"));
				vo.setBuyMode(rs.getInt("buy_mode"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setUserId(rs.getInt("user_id"));
				vo.setConfirmDatetime(rs.getTimestamp("confirm_datetime"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setPrice(rs.getFloat("price"));
				vo.setDiscount(rs.getFloat("discount"));
				vo.setDeliverType(rs.getInt("deliver_type"));
				vo.setRemitType(rs.getInt("remit_type"));
				vo.setStockout(rs.getInt("stockout"));
				vo.setPhone2(rs.getString("phone2"));
				vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
				vo.setOperator(rs.getString("operator"));
				vo.setAgent(rs.getInt("agent"));
				vo.setAgentMark(rs.getString("agent_mark"));
				vo.setAgentRemark(rs.getString("agent_remark"));
				vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
				vo.setIsReimburse(rs.getInt("is_reimburse"));
				vo.setRealPay(rs.getFloat("real_pay"));
				vo.setPostage(rs.getInt("postage"));
				vo.setIsOrder(rs.getInt("is_order"));
				vo.setAreano(rs.getInt("areano"));
				vo.setPrePayType(rs.getInt("pre_pay_type"));
				vo.setIsOlduser(rs.getInt("is_olduser"));
				vo.setSuffix(rs.getFloat("suffix"));
				vo.setContactTime(rs.getInt("contact_time"));
				vo.setUnitedOrders(rs.getString("united_orders"));
				vo.setFlat(rs.getInt("flat"));
				vo.setHasAddPoint(rs.getInt("has_add_point"));
				vo.setGender(rs.getInt("gender"));
				vo.setWebRemark(rs.getString("web_remark"));
				vo.setEmail(rs.getString("email"));
				vo.setOriginOrderId(rs.getInt("origin_order_id"));
				vo.setNewOrderId(rs.getInt("new_order_id"));

				vo.setDealDetail(rs.getString("deal_detail"));
				vo.setCpaBonus(rs.getInt("cpa_bonus"));
				vo.setCpaPay(rs.getInt("cpa_pay"));
				vo.setCpaStatus(rs.getInt("cpa_status"));

				vo.setSellerCheckStatus(rs.getInt("seller_check_status"));
				vo.setLastOperTime(rs.getString("last_deal_time"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public List getOrdersByPresents(String condition, int index,
			int count, String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf
		.append("select * from user_order a join user_order_present b on a.id=b.order_id where ");
		buf.append(condition);
		if (orderBy != null && orderBy.length() > 0) {
			buf.append(" order by ");
			buf.append(orderBy);
		}
		if (index >= 0 && count > 0) {
			buf.append(" limit ");
			buf.append(index);
			buf.append(", ");
			buf.append(count);
		}
		//        String query = "select * from user_order a join user_order_product b
		// on a.id=b.order_id where " + condition
		//                + " order by " + orderBy + " limit " + index + ", " + count;
		String query = buf.toString();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			voOrder vo = null;
			while (rs.next()) {
				vo = new voOrder();
				vo.setId(rs.getInt("a.id"));
				vo.setName(rs.getString("name"));
				vo.setPhone(rs.getString("phone"));
				vo.setAddress(rs.getString("address"));
				vo.setPostcode(rs.getString("postcode"));
				vo.setBuyMode(rs.getInt("buy_mode"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setUserId(rs.getInt("user_id"));
				vo.setConfirmDatetime(rs.getTimestamp("confirm_datetime"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setPrice(rs.getFloat("price"));
				vo.setDiscount(rs.getFloat("discount"));
				vo.setDeliverType(rs.getInt("deliver_type"));
				vo.setRemitType(rs.getInt("remit_type"));
				vo.setStockout(rs.getInt("stockout"));
				vo.setPhone2(rs.getString("phone2"));
				vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
				vo.setOperator(rs.getString("operator"));
				vo.setAgent(rs.getInt("agent"));
				vo.setAgentMark(rs.getString("agent_mark"));
				vo.setAgentRemark(rs.getString("agent_remark"));
				vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
				vo.setIsReimburse(rs.getInt("is_reimburse"));
				vo.setRealPay(rs.getFloat("real_pay"));
				vo.setPostage(rs.getInt("postage"));
				vo.setIsOrder(rs.getInt("is_order"));
				vo.setAreano(rs.getInt("areano"));
				vo.setPrePayType(rs.getInt("pre_pay_type"));
				vo.setIsOlduser(rs.getInt("is_olduser"));
				vo.setSuffix(rs.getFloat("suffix"));
				vo.setContactTime(rs.getInt("contact_time"));
				vo.setUnitedOrders(rs.getString("united_orders"));
				vo.setFlat(rs.getInt("flat"));
				vo.setHasAddPoint(rs.getInt("has_add_point"));
				vo.setGender(rs.getInt("gender"));
				vo.setWebRemark(rs.getString("web_remark"));
				vo.setEmail(rs.getString("email"));
				vo.setOriginOrderId(rs.getInt("origin_order_id"));
				vo.setNewOrderId(rs.getInt("new_order_id"));

				vo.setDealDetail(rs.getString("deal_detail"));
				vo.setCpaBonus(rs.getInt("cpa_bonus"));
				vo.setCpaPay(rs.getInt("cpa_pay"));
				vo.setCpaStatus(rs.getInt("cpa_status"));

				vo.setSellerCheckStatus(rs.getInt("seller_check_status"));
				vo.setLastOperTime(rs.getString("last_deal_time"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#modifyProduct(int,
	 *      adultadmin.action.vo.voProduct)
	 */
	public void modifyProduct(int id, voProduct vo) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update product set name=?,price=?,intro=?,pic=?,pic2=?,flag=?,parent_id1=?,parent_id2=?,parent_id3=?,status=?,proxy_id=?,price2=?,price3=?,proxys_name=?,remark=?,unit=?,intro2=?,oriname=?,pic3=?,deputize_price=?, "
					+ "guanggao=?,gongxiao=?,shiyongrenqun=?,shiyongfangfa=?,zhuyishixiang=?,tebietishi=?,baozhuangzhongliang=?,baozhuangdaxiao=?,chanpinchengfen=?,changshang=?,baozhiqi=?,chucangfangfa=?,pizhunwenhao=?,changshangjieshao=?,images=?,fuwuchengnuo=?,rank=?,display_order=?,top_order=?,price4=?,stock_standard_bj=?,stock_standard_gd=?,stock_line_bj=?,stock_line_gd=?,stock_status=?,is_package=?,brand=?,show_package=?,chanpinzhongliang=?,chanpinchicun=?,bj_stockin=?,gd_stockin=?,has_present=?, group_buy_price=? where id=?");
			pst.setString(1, vo.getName());
			pst.setFloat(2, vo.getPrice());
			pst.setString(3, vo.getIntro());
			pst.setString(4, vo.getPic());
			pst.setString(5, vo.getPic2());
			pst.setInt(6, vo.getFlag());
			pst.setInt(7, vo.getParentId1());
			pst.setInt(8, vo.getParentId2());
			pst.setInt(9, vo.getParentId3());
			pst.setInt(10, vo.getStatus());
			pst.setInt(11, vo.getProxyId());
			pst.setFloat(12, vo.getPrice2());
			pst.setFloat(13, vo.getPrice3());
			pst.setString(14, vo.getProxysName());
			pst.setString(15, vo.getRemark());
			pst.setString(16, vo.getUnit());
			pst.setString(17, vo.getIntro2());
			pst.setString(18, vo.getOriname());
			pst.setString(19, vo.getPic3());
			pst.setFloat(20, vo.getDeputizePrice());
			pst.setString(21, vo.getGuanggao());
			pst.setString(22, vo.getGongxiao());
			pst.setString(23, vo.getShiyongrenqun());
			pst.setString(24, vo.getShiyongfangfa());
			pst.setString(25, vo.getZhuyishixiang());
			pst.setString(26, vo.getTebietishi());
			pst.setString(27, vo.getBaozhuangzhongliang());
			pst.setString(28, vo.getBaozhuangdaxiao());
			pst.setString(29, vo.getChanpinchengfen());
			pst.setString(30, vo.getChangshang());
			pst.setString(31, vo.getBaozhiqi());
			pst.setString(32, vo.getChucangfangfa());
			pst.setString(33, vo.getPizhunwenhao());
			pst.setString(34, vo.getChangshangjieshao());
			pst.setString(35, vo.getImages());
			pst.setString(36, vo.getFuwuchengnuo());
			pst.setInt(37, vo.getRank());
			pst.setInt(38, vo.getDisplayOrder());
			pst.setInt(39, vo.getTopOrder());
			pst.setFloat(40, vo.getPrice4());
			pst.setInt(41, vo.getStockStandardBj());
			pst.setInt(42, vo.getStockStandardGd());
			pst.setInt(43, vo.getStockLineBj());
			pst.setInt(44, vo.getStockLineGd());
			pst.setInt(45, vo.getStockStatus());
			pst.setInt(46, vo.getIsPackage());
			pst.setInt(47, vo.getBrand());
			pst.setInt(48, vo.getShowPackage());
			pst.setString(49, vo.getChanpinzhongliang());
			pst.setString(50, vo.getChanpinchicun());
			pst.setString(51, vo.getBjStockin());
			pst.setString(52, vo.getGdStockin());
			pst.setInt(53, vo.getHasPresent());
			pst.setFloat(54, vo.getGroupBuyPrice());
			pst.setInt(55, vo.getId());
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		// 修改产品信息，不再修改产品的 code
		// updateProductCode(vo.getId());
	}

	public void modifyProductMark(int id, voProduct vo) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update product set stock_bj_bad=?, stock_bj_repair=?, stock_gd_bad=?, stock_gd_repair=? where id=?");
			pst.setInt(1, vo.getStockBjBad());
			pst.setInt(2, vo.getStockBjRepair());
			pst.setInt(3, vo.getStockGdBad());
			pst.setInt(4, vo.getStockGdRepair());
			pst.setInt(5, vo.getId());
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getCommentCount(int, int)
	 */
	public int getCommentCount(int productId, int flag) {
		return getInt("select count(*) from comment where product_id="
				+ String.valueOf(productId) + " and flag="
				+ String.valueOf(flag));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#modifyComment(int, int)
	 */
	public void modifyComment(int id, int flag) {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("update comment set flag=? where id=?");
			pst.setInt(1, flag);
			pst.setInt(2, id);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getProxys()
	 */
	public List getSelects(String table, String condition) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = conn.prepareStatement("select * from " + table + " " + condition);

			rs = pst.executeQuery();
			while (rs.next()) {
				voSelect vo = new voSelect();
				vo.setId(rs.getInt("id"));
				vo.setName(rs.getString("name"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	public voSelect getSelect(String table, String condition) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		voSelect vo = null;
		try {

			pst = conn.prepareStatement("select * from " + table + " " +condition);

			rs = pst.executeQuery();
			while (rs.next()) {
				vo = new voSelect();
				vo.setId(rs.getInt("id"));
				vo.setName(rs.getString("name"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return vo;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getProxys()
	 */
	public String getString(String field, String table, String condition) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn.prepareStatement("select " + field + " from " + table
					+ " where " + condition);
			rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getString(field);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#searchOrderCode(java.lang.String)
	 */
	public List searchOrder(String condition) {
		Statement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = conn.createStatement();
			String sql = "select a.*,b.name,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products,(ifnull((select sum(e.price3 * e.count) from user_order_product e where e.order_id=a.id),0) + ifnull((select sum(e.price3 * e.count) from user_order_present e where e.order_id=a.id),0)) order_price3,uocp.stockout_remark psremark from user_order a left join user_order_phone_index uopi on a.id=uopi.order_id left outer join user_order_status b on a.status=b.id left join user_order_lack_deal uold on a.id = uold.id left join mailing_balance mb on a.id = mb.order_id left join user_order_common_properties uocp on a.id=uocp.order_id where 1=1 "
				+ condition;

			rs = pst.executeQuery(sql);
			while (rs.next()) {
				voOrder order = getOrdersItem(rs);
				order.setPrice3(rs.getFloat("order_price3"));
				order.setStockoutRemark(rs.getString("psremark"));
				list.add(order);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}
	
	public List searchOrder(String condition, String useOrderIndex) {
		Statement pst = null;
		ResultSet rs = null;
		useOrderIndex = StringUtil.convertNull(useOrderIndex);
		if(!useOrderIndex.equals("")){
			useOrderIndex = " force index("+useOrderIndex+") ";
		}
		List list = new ArrayList();
		try {
			pst = conn.createStatement();
			String sql = "select a.*,b.name,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products,(ifnull((select sum(e.price3 * e.count) from user_order_product e where e.order_id=a.id),0) + ifnull((select sum(e.price3 * e.count) from user_order_present e where e.order_id=a.id),0)) order_price3,uocp.stockout_remark psremark from user_order a "+useOrderIndex+" left join user_order_phone_index uopi on a.id=uopi.order_id left outer join user_order_status b on a.status=b.id left join user_order_lack_deal uold on a.id = uold.id left join order_stock os on a.id = os.order_id left join mailing_balance mb on a.id = mb.order_id left join user_order_common_properties uocp on a.id=uocp.order_id left join user_order_promotion_product uopp on a.id=uopp.order_id where 1=1 "
				+ condition;
//			System.out.println(sql);
			rs = pst.executeQuery(sql);
			while (rs.next()) {
				voOrder order = getOrdersItem(rs);
				order.setPrice3(rs.getFloat("order_price3"));
				order.setStockoutRemark(rs.getString("psremark"));
				list.add(order);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	public List searchOrder2(String condition) {
		Statement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = conn.createStatement();
			String sql = "select a.code,a.dprice,a.remark,a.consigner,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products,(ifnull((select sum(e.price3 * e.count) from user_order_product e where e.order_id=a.id),0) + ifnull((select sum(e.price3 * e.count) from user_order_present e where e.order_id=a.id),0)) order_price3  from user_order a left join user_order_phone_index uopi on a.id=uopi.order_id left outer join user_order_status b on a.status=b.id left join user_order_lack_deal uold on a.id = uold.id left join mailing_balance mb on a.id = mb.order_id where 1=1 "
				+ condition;

			rs = pst.executeQuery(sql);
			while (rs.next()) {
				voOrder order = new voOrder();
				order.setCode(rs.getString("a.code"));
				order.setProducts(rs.getString("products"));
				order.setDprice(rs.getFloat("a.dprice"));
				order.setRemark(rs.getString("a.remark"));
				order.setConsigner(rs.getString("a.consigner"));
				list.add(order);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}
	
	public List searchOrder2(String condition, String useOrderIndex) {
		Statement pst = null;
		ResultSet rs = null;
		useOrderIndex = StringUtil.convertNull(useOrderIndex);
		if(!useOrderIndex.equals("")){
			useOrderIndex = " force index("+useOrderIndex+") ";
		}
		List list = new ArrayList();
		try {
			pst = conn.createStatement();
			String sql = "select a.code,a.dprice,a.remark,a.consigner,uocp.stockout_remark,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products,(ifnull((select sum(e.price3 * e.count) from user_order_product e where e.order_id=a.id),0) + ifnull((select sum(e.price3 * e.count) from user_order_present e where e.order_id=a.id),0)) order_price3  from user_order a "+useOrderIndex+" left join user_order_phone_index uopi on a.id=uopi.order_id left outer join user_order_status b on a.status=b.id left join user_order_lack_deal uold on a.id = uold.id left join order_stock os on a.id = os.order_id left join mailing_balance mb on a.id = mb.order_id left join user_order_common_properties uocp on a.id=uocp.order_id where 1=1 "
				+ condition;

			rs = pst.executeQuery(sql);
			while (rs.next()) {
				voOrder order = new voOrder();
				order.setCode(rs.getString("a.code"));
				order.setProducts(rs.getString("products"));
				order.setDprice(rs.getFloat("a.dprice"));
				order.setRemark(rs.getString("a.remark"));
				order.setConsigner(rs.getString("a.consigner"));
				order.setStockoutRemark(rs.getString("uocp.stockout_remark"));
				list.add(order);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#searchOrderByProductId(int)
	 */
	public List searchOrderByProductId(int productId, int status, int buymode) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			String sql = "select * from user_order a where a.status=? and a.buy_mode=? and a.id in (select order_id from user_order_product where product_id=? group by order_id) order by a.id desc";

			pst = conn.prepareStatement(sql);
			pst.setInt(1, status);
			pst.setInt(2, buymode);
			pst.setInt(3, productId);
			rs = pst.executeQuery();
			while (rs.next()) {
				voOrder vo = new voOrder();

				vo.setId(rs.getInt("a.id"));
				vo.setName(rs.getString("a.name"));
				vo.setPhone(rs.getString("phone"));
				vo.setAddress(rs.getString("address"));
				vo.setPostcode(rs.getString("postcode"));
				vo.setBuyMode(rs.getInt("buy_mode"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setUserId(rs.getInt("user_id"));
				vo.setConfirmDatetime(rs.getTimestamp("confirm_datetime"));
				vo.setCode(rs.getString("code"));
				vo.setAgent(rs.getInt("agent"));
				vo.setAgentMark(rs.getString("agent_mark"));
				vo.setAgentRemark(rs.getString("agent_remark"));
				vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
				vo.setIsReimburse(rs.getInt("is_reimburse"));
				vo.setRealPay(rs.getFloat("real_pay"));
				vo.setPostage(rs.getInt("postage"));
				vo.setIsOrder(rs.getInt("is_order"));
				vo.setUnitedOrders(rs.getString("united_orders"));
				vo.setOriginOrderId(rs.getInt("origin_order_id"));
				vo.setNewOrderId(rs.getInt("new_order_id"));

				vo.setDealDetail(rs.getString("deal_detail"));
				vo.setCpaBonus(rs.getInt("cpa_bonus"));
				vo.setCpaPay(rs.getInt("cpa_pay"));
				vo.setCpaStatus(rs.getInt("cpa_status"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#searchOrderByUserId(int)
	 */
	public List searchOrderByUserId(int userId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			String sql = "select * from user_order a left outer join user_order_status b on a.status=b.id where user_id="
				+ userId;

			pst = conn.prepareStatement(sql);

			rs = pst.executeQuery();
			while (rs.next()) {
				list.add(getOrdersItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#searchProduct(java.lang.String,
	 *      java.lang.String)
	 */
	public List searchProduct(String code, String name, String price) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			String sql = "select * from product a left outer join product_status b on status=b.id left outer join product_proxy c on proxy_id=c.id where 1=1";
			if (code.length() > 0)
				sql += " and a.code='" + code + "'";

			if (name.length() > 0) {
				sql += " and (a.name like '%" + name
				+ "%' or a.oriname like '%" + name + "%')";
			}

			if (price.length() > 0) {
				sql += " and (round(a.price,2) like '%" + price
				+ "%' or round(a.price2,2) like '%" + price + "%')";
			}

			sql += " order by status asc,a.id desc";

			pst = conn.prepareStatement(sql);

			rs = pst.executeQuery();
			while (rs.next()) {

				list.add(getProductsItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	public List searchProduct(String code, String name, String price,
			String minPrice, String maxPrice) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			String sql = "select * from product a left outer join product_status b on status=b.id left outer join product_proxy c on proxy_id=c.id where 1=1";
			if (code.length() > 0)
				sql += " and a.code='" + code + "'";

			if (name.length() > 0) {
				sql += " and (a.name like '%" + name
				+ "%' or a.oriname like '%" + name + "%')";
			}

			if (price.length() > 0) {
				sql += " and (round(a.price,2) like '%" + price
				+ "%' or round(a.price2,2) like '%" + price + "%')";
			}

			if (minPrice.length() > 0) {
				sql += " and (a.price >= " + minPrice + ")";
			}

			if (maxPrice.length() > 0) {
				sql += " and (a.price <= " + maxPrice + ")";
			}

			sql += " order by status asc,a.id desc";

			pst = conn.prepareStatement(sql);

			rs = pst.executeQuery();
			while (rs.next()) {

				list.add(getProductsItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	public List searchProduct(String condition, int startIndex, int count, String orderBy ,String present) {
		List list = new ArrayList();
		if(condition == null || condition.length() == 0){
			return list;
		}
		PreparedStatement pst = null;
		ResultSet rs = null;
		//条码SQL
		String barcodeSql=" left join product_barcode pb on a.id=pb.product_id ";
		//是否从赠品表中读取数据
		String presentSqlStart="";
		String presentSqlEnd="";
		if(present.equals("present")){
			presentSqlStart=" present_product pp left join ";
			presentSqlEnd=" on pp.product_id = a.id ";
		}
		try {
			String sql = "select *,GROUP_CONCAT(distinct d.name)sName from "+presentSqlStart+" product a " + presentSqlEnd+ " left outer join product_status b on a.status=b.id left outer join product_supplier c on a.id=c.product_id left join supplier_standard_info d on d.id=c.supplier_id and d.status=1 ";
			sql+=barcodeSql;
			sql +="where "+ condition;
			if(startIndex >=0 && count > 0){
				sql += " limit " + startIndex + "," + count;
			}

			if(!StringUtil.isNull(orderBy)){
				sql += "  group by a.id order by " + orderBy;
			}
			pst = conn.prepareStatement(sql);

			rs = pst.executeQuery();
			int i=0;
			while (rs.next()) {
				list.add(getProductsItem1(rs));
				ProductBarcodeVO pbvo= new ProductBarcodeVO();
				pbvo.setId(rs.getInt("id"));
				pbvo.setBarcode(rs.getString("barcode"));
				pbvo.setBarcodeSource(rs.getInt("barcode_source"));
				((voProduct)list.get(i)).setProductBarcodeVO(pbvo);
				i++;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);

		}
		return list;
	}    
	public List searchProduct(String condition, int startIndex, int count, String orderBy) {
		List list = new ArrayList();
		if(condition == null || condition.length() == 0){
			return list;
		}
		PreparedStatement pst = null;
		ResultSet rs = null;
		//条码SQL
		String barcodeSql=" left join product_barcode pb on a.id=pb.product_id ";
		try {
			String sql = "select *,GROUP_CONCAT(distinct d.name)sName from product a  left outer join product_status b on a.status=b.id left outer join product_supplier c on a.id=c.product_id left join supplier_standard_info d on d.id=c.supplier_id and d.status=1 ";
			sql+=barcodeSql;
			sql +="where "+ condition;
			
			sql +=" group by a.id";
			
			if(!StringUtil.isNull(orderBy)){
				sql += " order by " + orderBy;
			}
			
			if(startIndex >=0 && count > 0){
				sql += " limit " + startIndex + "," + count;
			}
			
			pst = conn.prepareStatement(sql);

			rs = pst.executeQuery();
			int i=0;
			while (rs.next()) {
				list.add(getProductsItem1(rs));
				ProductBarcodeVO pbvo= new ProductBarcodeVO();
				pbvo.setId(rs.getInt("id"));
				pbvo.setBarcode(rs.getString("barcode"));
				pbvo.setBarcodeSource(rs.getInt("barcode_source"));
				((voProduct)list.get(i)).setProductBarcodeVO(pbvo);
				i++;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);

		}
		return list;
	}    
	public voProduct getProductsItem(ResultSet rs) {
		voProduct vo = new voProduct();
		try {
			vo.setId(rs.getInt("a.id"));
			vo.setName(rs.getString("a.name"));
			vo.setOriname(rs.getString("a.oriname"));
			vo.setPrice(rs.getFloat("price"));
			vo.setPrice2(rs.getFloat("price2"));
			vo.setPrice3(rs.getFloat("price3"));
			vo.setPrice4(rs.getFloat("price4"));
			vo.setPrice5(rs.getFloat("price5"));
			vo.setDeputizePrice(rs.getFloat("deputize_price"));
			vo.setGroupBuyPrice(rs.getFloat("group_buy_price"));
			vo.setPic(rs.getString("pic"));
			vo.setPic2(rs.getString("pic2"));
			vo.setPic3(rs.getString("pic3"));
			vo.setCommentCount(rs.getInt("comment_count"));
			vo.setClickCount(rs.getInt("click_count"));
			vo.setBuyCount(rs.getInt("buy_count"));
			vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
			vo.setStatus(rs.getInt("status"));
			vo.setCode(rs.getString("code"));
			vo.setProxyId(rs.getInt("proxy_id"));
			vo.setProxysName(rs.getString("proxys_name"));
			vo.setIsPackage(rs.getInt("is_package"));
			vo.setHasPresent(rs.getInt("has_present"));
			vo.setStatusName(rs.getString("b.name"));
			vo.setProxyName(rs.getString("c.name"));
			vo.setStock(rs.getInt("a.stock"));
			vo.setStockGd(rs.getInt("a.stock_gd"));
			vo.setImages(rs.getString("images"));
			vo.setRank(rs.getInt("rank"));
			vo.setDisplayOrder(rs.getInt("display_order"));
			vo.setTopOrder(rs.getInt("top_order"));
			vo.setStockLineBj(rs.getInt("stock_line_bj"));
			vo.setStockLineGd(rs.getInt("stock_line_gd"));
			vo.setStockStandardBj(rs.getInt("stock_standard_bj"));
			vo.setStockStandardGd(rs.getInt("stock_standard_gd"));
			vo.setStockStatus(rs.getInt("stock_status"));
			vo.setParentId1(rs.getInt("parent_id1"));
			vo.setParentId2(rs.getInt("parent_id2"));
			vo.setBrand(rs.getInt("brand"));
			vo.setUnit(rs.getString("a.unit"));
			vo.setShowPackage(rs.getInt("show_package"));
			vo.setBjStockin(rs.getString("bj_Stockin"));
			vo.setGdStockin(rs.getString("gd_Stockin"));
			vo.setStockDayBj(rs.getInt("stock_day_bj"));
			vo.setStockDayGd(rs.getInt("stock_day_gd"));
			vo.setStockBjBad(rs.getInt("stock_bj_bad"));
			vo.setStockBjRepair(rs.getInt("stock_bj_repair"));
			vo.setStockGdBad(rs.getInt("stock_gd_bad"));
			vo.setStockGdRepair(rs.getInt("stock_gd_repair"));
			vo.setBaozhuangzhongliang(rs.getString("baozhuangzhongliang"));
			vo.setChanpinzhongliang(rs.getString("chanpinzhongliang"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	public voProduct getProductsItem1(ResultSet rs) {
		voProduct vo = new voProduct();
		try {
			vo.setId(rs.getInt("a.id"));
			vo.setName(rs.getString("a.name"));
			vo.setOriname(rs.getString("a.oriname"));
			vo.setPrice(rs.getFloat("price"));
			vo.setPrice2(rs.getFloat("price2"));
			vo.setPrice3(rs.getFloat("price3"));
			vo.setPrice4(rs.getFloat("price4"));
			vo.setPrice5(rs.getFloat("price5"));
			vo.setDeputizePrice(rs.getFloat("deputize_price"));
			vo.setGroupBuyPrice(rs.getFloat("group_buy_price"));
			vo.setPic(rs.getString("pic"));
			vo.setPic2(rs.getString("pic2"));
			vo.setPic3(rs.getString("pic3"));
			vo.setCommentCount(rs.getInt("comment_count"));
			vo.setClickCount(rs.getInt("click_count"));
			vo.setBuyCount(rs.getInt("buy_count"));
			vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
			vo.setStatus(rs.getInt("status"));
			vo.setCode(rs.getString("code"));
			vo.setProxyId(rs.getInt("proxy_id"));
			vo.setProxysName(rs.getString("sName"));
			vo.setIsPackage(rs.getInt("is_package"));
			vo.setHasPresent(rs.getInt("has_present"));
			vo.setStatusName(rs.getString("b.name"));
			vo.setProxyName(rs.getString("sName"));
			vo.setStock(rs.getInt("a.stock"));
			vo.setStockGd(rs.getInt("a.stock_gd"));
			vo.setImages(rs.getString("images"));
			vo.setRank(rs.getInt("rank"));
			vo.setDisplayOrder(rs.getInt("display_order"));
			vo.setTopOrder(rs.getInt("top_order"));
			vo.setStockLineBj(rs.getInt("stock_line_bj"));
			vo.setStockLineGd(rs.getInt("stock_line_gd"));
			vo.setStockStandardBj(rs.getInt("stock_standard_bj"));
			vo.setStockStandardGd(rs.getInt("stock_standard_gd"));
			vo.setStockStatus(rs.getInt("stock_status"));
			vo.setParentId1(rs.getInt("parent_id1"));
			vo.setParentId2(rs.getInt("parent_id2"));
			vo.setParentId3(rs.getInt("parent_id3"));
			vo.setBrand(rs.getInt("brand"));
			vo.setUnit(rs.getString("a.unit"));
			vo.setShowPackage(rs.getInt("show_package"));
			vo.setBjStockin(rs.getString("bj_Stockin"));
			vo.setGdStockin(rs.getString("gd_Stockin"));
			vo.setStockDayBj(rs.getInt("stock_day_bj"));
			vo.setStockDayGd(rs.getInt("stock_day_gd"));
			vo.setStockBjBad(rs.getInt("stock_bj_bad"));
			vo.setStockBjRepair(rs.getInt("stock_bj_repair"));
			vo.setStockGdBad(rs.getInt("stock_gd_bad"));
			vo.setStockGdRepair(rs.getInt("stock_gd_repair"));
			vo.setBaozhuangzhongliang(rs.getString("baozhuangzhongliang"));
			vo.setChanpinzhongliang(rs.getString("chanpinzhongliang"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}
	public voProduct getProductsItem2(ResultSet rs) {
		voProduct vo = new voProduct();
		try {
			vo.setId(rs.getInt("a.id"));
			vo.setName(rs.getString("a.name"));
			vo.setOriname(rs.getString("a.oriname"));
			vo.setPrice(rs.getFloat("price"));
	     	vo.setPrice2(rs.getFloat("price2"));
			vo.setPrice3(rs.getFloat("price3"));
			vo.setPrice4(rs.getFloat("price4"));
			vo.setPrice5(rs.getFloat("price5"));
			vo.setDeputizePrice(rs.getFloat("deputize_price"));
			vo.setGroupBuyPrice(rs.getFloat("deputize_price"));
//			vo.setPic(rs.getString("pic"));
//			vo.setPic2(rs.getString("pic2"));
//			vo.setPic3(rs.getString("pic3"));
//			vo.setCommentCount(rs.getInt("comment_count"));
//			vo.setClickCount(rs.getInt("click_count"));
//			vo.setBuyCount(rs.getInt("buy_count"));
//			vo.setCreateDatetime(rs.getTimestamp("create_datetime"));	
			vo.setStatus(rs.getInt("a.status"));
			vo.setCode(rs.getString("code"));
//			vo.setProxyId(rs.getInt("proxy_id"));
//			vo.setProxysName(rs.getString("sName"));
//			vo.setIsPackage(rs.getInt("is_package"));
//			vo.setHasPresent(rs.getInt("has_present"));
//			vo.setStatusName(rs.getString("b.name"));
//			vo.setProxyName(rs.getString("sName"));
//			vo.setStock(rs.getInt("a.stock"));
//			vo.setStockGd(rs.getInt("a.stock_gd"));
//			vo.setImages(rs.getString("images"));
//			vo.setRank(rs.getInt("rank"));
//			vo.setDisplayOrder(rs.getInt("display_order"));
//			vo.setTopOrder(rs.getInt("top_order"));
//			vo.setStockLineBj(rs.getInt("stock_line_bj"));
//			vo.setStockLineGd(rs.getInt("stock_line_gd"));
//			vo.setStockStandardBj(rs.getInt("stock_standard_bj"));
//			vo.setStockStandardGd(rs.getInt("stock_standard_gd"));
//			vo.setStockStatus(rs.getInt("stock_status"));
			vo.setParentId1(rs.getInt("parent_id1"));
			vo.setParentId2(rs.getInt("parent_id2"));
			vo.setParentId3(rs.getInt("parent_id3"));
//			vo.setBrand(rs.getInt("brand"));
//			vo.setUnit(rs.getString("a.unit"));
//			vo.setShowPackage(rs.getInt("show_package"));
//			vo.setBjStockin(rs.getString("bj_Stockin"));
//			vo.setGdStockin(rs.getString("gd_Stockin"));
//			vo.setStockDayBj(rs.getInt("stock_day_bj"));
//			vo.setStockDayGd(rs.getInt("stock_day_gd"));
//			vo.setStockBjBad(rs.getInt("stock_bj_bad"));
//			vo.setStockBjRepair(rs.getInt("stock_bj_repair"));
//			vo.setStockGdBad(rs.getInt("stock_gd_bad"));
//			vo.setStockGdRepair(rs.getInt("stock_gd_repair"));
//			vo.setBaozhuangzhongliang(rs.getString("baozhuangzhongliang"));
//			vo.setChanpinzhongliang(rs.getString("chanpinzhongliang"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}
	public voOrder getOrdersItem(ResultSet rs) {
		voOrder vo = new voOrder();
		try {
			vo.setId(rs.getInt("a.id"));
			vo.setName(rs.getString("a.name"));
			vo.setPhone(rs.getString("phone"));
			vo.setAddress(rs.getString("address"));
			vo.setPostcode(rs.getString("postcode"));
			vo.setBuyMode(rs.getInt("buy_mode"));
			vo.setOperator(rs.getString("operator"));
			vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
			vo.setUserId(rs.getInt("user_id"));
			vo.setConfirmDatetime(rs.getTimestamp("confirm_datetime"));
			vo.setStatus(rs.getInt("status"));
			vo.setCode(rs.getString("code"));
			vo.setStatusName(rs.getString("b.name"));
			vo.setPrice(rs.getFloat("price"));
			vo.setDiscount(rs.getFloat("a.discount"));
			vo.setDeliverType(rs.getInt("deliver_type"));
			vo.setRemitType(rs.getInt("remit_type"));
			vo.setProducts(rs.getString("products"));
			vo.setStockout(rs.getInt("stockout"));
			vo.setPhone2(rs.getString("phone2"));
			vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
			vo.setFr(rs.getInt("fr"));
			vo.setAgent(rs.getInt("agent"));
			vo.setAgentMark(rs.getString("agent_mark"));
			vo.setAgentRemark(rs.getString("agent_remark"));
			vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
			vo.setIsReimburse(rs.getInt("is_reimburse"));
			vo.setRealPay(rs.getFloat("real_pay"));
			vo.setPostage(rs.getInt("postage"));
			vo.setIsOrder(rs.getInt("is_order"));
			vo.setImages(rs.getString("images"));
			vo.setAreano(rs.getInt("areano"));
			vo.setPrePayType(rs.getInt("pre_pay_type"));
			vo.setIsOlduser(rs.getInt("is_olduser"));
			vo.setSuffix(rs.getFloat("suffix"));
			vo.setContactTime(rs.getInt("contact_time"));
			vo.setUnitedOrders(rs.getString("united_orders"));
			vo.setRemark(rs.getString("remark"));
			vo.setFlat(rs.getInt("flat"));
			vo.setHasAddPoint(rs.getInt("has_add_point"));
			vo.setGender(rs.getInt("gender"));
			vo.setWebRemark(rs.getString("web_remark"));
			vo.setEmail(rs.getString("email"));
			vo.setOriginOrderId(rs.getInt("origin_order_id"));
			vo.setNewOrderId(rs.getInt("new_order_id"));

			vo.setDealDetail(rs.getString("deal_detail"));
			vo.setCpaBonus(rs.getInt("cpa_bonus"));
			vo.setCpaPay(rs.getInt("cpa_pay"));
			vo.setCpaStatus(rs.getInt("cpa_status"));

			vo.setSellerCheckStatus(rs.getInt("seller_check_status"));
			vo.setConsigner(rs.getString("consigner"));

			vo.setDeliver(rs.getInt("deliver"));
			vo.setProductType(rs.getInt("product_type"));
			vo.setDprice(rs.getFloat("dprice"));

			vo.setStockoutDeal(rs.getInt("stockout_deal"));
			vo.setLastOperTime(rs.getString("last_deal_time"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vo;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#setOrderStatus(int, int)
	 */
	public boolean setOrderStatus(int id, int status) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update user_order set status=? where id=?");
			pst.setInt(1, status);
			pst.setInt(2, id);
			pst.executeUpdate();
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return false;
		} finally {
			closeStatement(pst);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#replaceOrderStatus(int, int)
	 */
	public void replaceOrderStatus(int oldstatus, int status, int buymode) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update user_order set status=? where status=? and buy_mode=?");
			pst.setInt(1, status);
			pst.setInt(2, oldstatus);
			pst.setInt(3, buymode);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}
	////by hdy:根据商品编码返回商品ID，查找不到则返回0
	public int getProductIdByCode(String code){
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn
			.prepareStatement("select id from product where code=?");
			pst.setString(1, code);
			rs = pst.executeQuery();
			if(rs!=null && rs.next()){
				return rs.getInt(1);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeResultSet(rs);
			closeStatement(pst);
		}
		return 0;
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#addOrderProduct(int,
	 *      java.lang.String, int)
	 */
	public void addOrderProduct(int orderId, String code, int count) {
		PreparedStatement pst = null;
		try {
			int productId = getProductIdByCode(code);
			pst = conn
			.prepareStatement("insert into user_order_product (order_id,product_id,count)values(?,?,?)");
			pst.setInt(1, orderId);
			pst.setInt(2, productId);
			pst.setInt(3, count);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public boolean addOrderProductSplit(int orderId, String code, int count) {
		PreparedStatement pst = null;
		try {
			int productId = getProductIdByCode(code);
			pst = conn
			.prepareStatement("insert into user_order_product_split (order_id,product_id,count)values(?,?,?)");
			pst.setInt(1, orderId);
			pst.setInt(2, productId);
			pst.setInt(3, count);
			int rows = pst.executeUpdate();
			if(rows == 0){
				return false;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(sqle));
			return false;
		} finally {
			closeStatement(pst);
		}

		return true;
	}

	public boolean updateOrderProductSplit(String set, int id) {
		if(StringUtil.isNull(set)){
			return false;
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			StringBuilder buf = new StringBuilder();
			buf.append("update user_order_product_split set ").append(set).append(" where id=").append(id);
			String sql = buf.toString();
			int rows = st.executeUpdate(sql);
			if(rows == 0){
				return false;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(sqle));
			return false;
		} finally {
			closeStatement(st);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#deleteOrderProduct(int)
	 */
	public void deleteOrderProduct(int id) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("delete from user_order_product where id="
					+ id);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}
	
	public void deleteOrderProduct(int orderId, int productId) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("delete from user_order_product where order_id="
					+ orderId + " and product_id=" + productId);
			pst.executeUpdate();
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void deleteOrderProducts(int orderId) {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("delete from user_order_product where order_id=" + orderId);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void deleteOrderProductSplit(int id) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("delete from user_order_product_split where id="
					+ id);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void deleteOrderProductsSplit(int orderId) {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("delete from user_order_product_split where order_id=" + orderId);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void updateOrderImage(voOrder vo) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update user_order set images=? where id= ?");
			pst.setString(1, vo.getImages());
			pst.setInt(2, vo.getId());
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#modifyOrderProduct(int,
	 *      java.lang.String, int)
	 */
	public void modifyOrderProduct(int id, String code, int count) {
		PreparedStatement pst = null;
		try {
			int productId = getProductIdByCode(code);
			pst = conn
			.prepareStatement("update user_order_product set product_id=?,count=? where id=?");
			pst.setInt(1, productId);
			pst.setInt(2, count);
			pst.setInt(3, id);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}
	public void modifyOrderPromotionProduct(int id, String code, int count) {
		PreparedStatement pst = null;
		try {
			int productId = getProductIdByCode(code);
			pst = conn
			.prepareStatement("update user_order_promotion_product set product_id=?,count=? where id=?");
			pst.setInt(1, productId);
			pst.setInt(2, count);
			pst.setInt(3, id);
			pst.executeUpdate();
			
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#modifyOrder(adultadmin.action.vo.voOrder)
	 */
	public void modifyOrder(voOrder vo) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update user_order set name=?,phone=?,address=?,postcode=?,remark=?,status=?,discount=?,buy_mode=?,remit_type=?,deliver_type=?,prepay_deliver=?,operator=?,phone2=?,real_pay=?,postage=?,images=concat(images, ?),areano=?,pre_pay_type=?,is_olduser=?,contact_time=?,flat=?,gender=?,email=?,web_remark=?, origin_order_id=?, new_order_id=?, cpa_status=?, deal_detail=?, cpa_bonus=?, seller_check_status=?, stockout_remark=?, consigner=?, deliver=?, product_type=?, stockout_deal=? where id=?");
			pst.setString(1, vo.getName());
			pst.setString(2, vo.getPhone());
			pst.setString(3, vo.getAddress());
			pst.setString(4, vo.getPostcode());
			pst.setString(5, vo.getRemarks());
			pst.setInt(6, vo.getStatus());
			pst.setFloat(7, vo.getDiscount());
			pst.setInt(8, vo.getBuyMode());
			pst.setInt(9, vo.getRemitType());
			pst.setInt(10, vo.getDeliverType());
			pst.setFloat(11, vo.getPrepayDeliver());
			pst.setString(12, vo.getOperator());
			pst.setString(13, vo.getPhone2());
			pst.setFloat(14, vo.getRealPay());
			pst.setFloat(15, vo.getPostage());
			pst.setString(16, vo.getImages());
			pst.setInt(17, vo.getAreano());
			pst.setInt(18, vo.getPrePayType());
			pst.setInt(19, vo.getIsOlduser());
			pst.setInt(20, vo.getContactTime());
			pst.setInt(21, vo.getFlat());
			pst.setInt(22, vo.getGender());
			pst.setString(23, vo.getEmail());
			pst.setString(24, vo.getWebRemark());
			pst.setInt(25, vo.getOriginOrderId());
			pst.setInt(26, vo.getNewOrderId());
			pst.setInt(27, vo.getCpaStatus());
			pst.setString(28, vo.getDealDetail());
			pst.setInt(29, vo.getCpaBonus());
			pst.setInt(30, vo.getSellerCheckStatus());
			pst.setString(31, vo.getStockoutRemark());
			pst.setString(32, vo.getConsigner());
			pst.setInt(33, vo.getDeliver());
			pst.setInt(34, vo.getProductType());
			pst.setInt(35, vo.getStockoutDeal());
			pst.setInt(36, vo.getId());
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#addOrder(adultadmin.action.vo.voOrder)
	 */
	public int addOrder(voOrder vo) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("insert into user_order (name,phone,address,postcode,remark,status,code,user_id,discount,buy_mode,remit_type,deliver_type,price,phone2,prepay_deliver,operator,postage,areano,pre_pay_type,is_olduser,suffix,flat,origin_order_id, new_order_id, seller_id, consigner, last_deal_time, fr,flag,web_remark, email, deal_detail,dprice)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, now(), ?,?, '', '', '',?)");
			pst.setString(1, vo.getName());
			pst.setString(2, vo.getPhone());
			pst.setString(3, vo.getAddress());
			pst.setString(4, vo.getPostcode());
			pst.setString(5, vo.getRemarks());
			pst.setInt(6, vo.getStatus());
			pst.setString(7, vo.getCode());
			pst.setInt(8, vo.getUserId());
			pst.setFloat(9, vo.getDiscount());
			pst.setInt(10, vo.getBuyMode());
			pst.setInt(11, vo.getRemitType());
			pst.setInt(12, vo.getDeliverType());
			pst.setFloat(13, vo.getPrice());
			pst.setString(14, vo.getPhone2());
			pst.setFloat(15, vo.getPrepayDeliver());
			pst.setString(16, vo.getOperator());
			pst.setFloat(17, vo.getPostage());
			pst.setInt(18, vo.getAreano());
			pst.setInt(19, vo.getPrePayType());
			pst.setInt(20, vo.getIsOlduser());
			pst.setFloat(21, vo.getSuffix());
			pst.setInt(22, vo.getFlat());
			pst.setInt(23, vo.getOriginOrderId());
			pst.setInt(24, vo.getNewOrderId());
			pst.setInt(25, vo.getSellerId());
			pst.setString(26, vo.getConsigner());
			pst.setInt(27, vo.getFr());
			pst.setInt(28, vo.getFlag());
			pst.setFloat(29, vo.getDprice());
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}

		int id = getLastInsertId("user_order");
		String orderCode = null;
		if(id > 999999){
			String strId = String.valueOf(id);
			orderCode = strId.substring(strId.length()- 6, strId.length());
		} else {
			orderCode = String.valueOf(id);
		}

		StringBuilder updateBuf = new StringBuilder();
		updateBuf.append("update user_order set code=concat(code,'").append(orderCode).append("') where id=").append(id);
		dbOp.executeUpdate(updateBuf.toString());

		// TODO: 后台下的订单，直接分配
		//		updateBuf.delete(0, updateBuf.length());
		//		updateBuf.append("insert into order_dist_pool(id, order_code, create_datetime, phone) (select id, code, create_datetime, phone from user_order where id=").append(id).append(")");
		//		dbOp.executeUpdate(updateBuf.toString());

		vo.setCode(vo.getCode() + orderCode);

		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#modifyOrder(int, int)
	 */
	public void modifyOrder(int id, int status) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update user_order set status=? where id=?");
			pst.setInt(1, status);
			pst.setInt(2, id);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void modifyOrder(String set, String condition) {
		Statement st = null;
		try {
			st = conn.createStatement();
			String query = "update user_order set " + set + " where "
			+ condition;
			st.executeUpdate(query);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getOrderStatus(int)
	 */
	public int getOrderStatus(int id) {
		return getInt("select status from user_order where id=" + id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getCollectProductList(int)
	 */
	public List getCollectProductList(int status, int buymode) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = conn
			.prepareStatement("select *,sum(count) totalcount from user_order a,user_order_product b,product c,product_proxy d where a.status=? and buy_mode=? and a.id=b.order_id and c.id=b.product_id and d.id=c.proxy_id group by c.id order by totalcount desc, c.proxy_id asc");
			pst.setInt(1, status);
			pst.setInt(2, buymode);

			rs = pst.executeQuery();
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setProductId(rs.getInt("c.id"));
				vo.setCount(rs.getInt("totalcount"));
				vo.setCode(rs.getString("c.code"));
				vo.setName(rs.getString("c.name"));
				vo.setOriname(rs.getString("c.oriname"));
				vo.setPrice(rs.getFloat("c.price3"));
				vo.setProxyName(rs.getString("d.name"));
				vo.setStock(rs.getInt("c.stock"));
				vo.setStockGd(rs.getInt("c.stock_gd"));
				vo.setPrice3(rs.getFloat("price3"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	public List getCollectPresentList(int status, int buymode) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = conn
			.prepareStatement("select *,sum(count) totalcount from user_order a,user_order_present b,product c,product_proxy d where a.status=? and buy_mode=? and a.id=b.order_id and c.id=b.product_id and d.id=c.proxy_id group by c.id order by totalcount desc, c.proxy_id asc");
			pst.setInt(1, status);
			pst.setInt(2, buymode);

			rs = pst.executeQuery();
			while (rs.next()) {
				voOrderProduct vo = new voOrderProduct();
				vo.setProductId(rs.getInt("c.id"));
				vo.setCount(rs.getInt("totalcount"));
				vo.setCode(rs.getString("c.code"));
				vo.setName(rs.getString("c.name"));
				vo.setOriname(rs.getString("c.oriname"));
				vo.setPrice(rs.getFloat("c.price3"));
				vo.setProxyName(rs.getString("d.name"));
				vo.setStock(rs.getInt("c.stock"));
				vo.setStockGd(rs.getInt("c.stock_gd"));
				vo.setPrice3(rs.getFloat("price3"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getNextOrderId(int)
	 */
	public int getNextOrderId() {

		int id = getInt("select id from user_order where status=0 and buy_mode=0 order by last_deal_time asc, id asc limit 1");

		setOrderStatus(id, 1);

		return id;
	}


	public int getNextOrderId(int status, int buymode) {
		StringBuilder buf = new StringBuilder(128);
		buf.append("select id from user_order where status=");
		buf.append(status);
		if(buymode >= 0){
			buf.append(" and buy_mode=");
			buf.append(buymode);
		}
		buf.append(" order by last_deal_time asc, id asc limit 1");
		int id = getInt(buf.toString());

		buf.delete(0, buf.length());
		//setOrderStatus(id, StringUtil.StringToId(status) + 1);
		buf.append("last_deal_time=now(), status=");
		buf.append(status + 1);
		this.modifyOrder(buf.toString(), "id=" + id);

		return id;
	}

	public int getNextOrderId(String status, String buymode) {
		StringBuilder buf = new StringBuilder(128);
		buf.append("select id from user_order where status in (");
		buf.append(status);
		buf.append(")");
		if(buymode != null && buymode.length() > 0){
			buf.append(" and buy_mode in (");
			buf.append(buymode);
			buf.append(")");
		}
		buf.append(" order by last_deal_time asc, id asc limit 1");
		int id = getInt(buf.toString());

		buf.delete(0, buf.length());
		//setOrderStatus(id, StringUtil.StringToId(status) + 1);
		buf.append("last_deal_time=now(), status=");
		buf.append(StringUtil.StringToId(status) + 1);
		this.modifyOrder(buf.toString(), "id=" + id);

		return id;
	}

	public int getNextOrderId(String status, String buymode, String orderType) {
		StringBuilder buf = new StringBuilder(128);
		buf.append("select id from user_order where status in (");
		buf.append(status);
		buf.append(")");
		if(buymode != null && buymode.length() > 0){
			buf.append(" and buy_mode in (");
			buf.append(buymode);
			buf.append(")");
		}
		if(orderType != null && orderType.length() > 0){
			buf.append(" and order_type in (");
			buf.append(orderType);
			buf.append(")");
		}
		buf.append(" order by last_deal_time asc, id asc limit 1");
		int id = getInt(buf.toString());

		buf.delete(0, buf.length());
		//setOrderStatus(id, StringUtil.StringToId(status) + 1);
		buf.append("last_deal_time=now(), status=");
		buf.append(getNextStatus(StringUtil.StringToId(status)));
		this.modifyOrder(buf.toString(), "id=" + id);

		return id;
	}


	/**
	 * 
	 * @param status
	 * @param buymode
	 * @param orderType 传入差数：order_type in(1,2,3,4)
	 * @param stockoutDeal
	 * @param orderType2  // 订单类型 Q单 非Q单
	 * @return
	 */




	/**
	 * 缺货订单在补货的时候 会 发货状态 和缺货状态设置为0 
	 */
	public int getNextOrderAndLackId(String orderType){
		StringBuilder buf = new StringBuilder(128);
		buf.append("select  uo.id from user_order uo join user_order_lack_deal uold on uo.id = uold.id  ") 
			.append("where uo.stockout_deal=0 and uold.stockout_deal=0 ");
		
		if(orderType.length()>0){
			buf.append(" and ");
			buf.append(orderType);
		}
		
		buf.append(" order by id asc limit 1");
		int id = getInt(buf.toString());
		
		buf.delete(0, buf.length());
		if(id > 0){
			buf.append("last_deal_time=now(), stockout_deal=1");
 			this.modifyOrder(buf.toString(), "id=" + id);
		}
		return id;
	}
	
	public int getNextOrderAndOnLine(String lastDealTime, String operator){
		StringBuilder buf = new StringBuilder(128);
		buf.append(" status=3 and buy_mode = 1 ").append(" and stockout_deal=0 and last_deal_time <='")
		   .append(lastDealTime).append("' ");
		if(!StringUtil.isNull(operator)){
			buf.append(" and operator = '").append(operator).append("'");
		}
		buf.append(" order by id asc limit 1");
		
		String sql = "select id from user_order where " + buf.toString();
		int id = getInt(sql);
		
		buf.delete(0, buf.length());
		if(id > 0){
			buf.append("last_deal_time=now(),stockout_deal=3");
 			this.modifyOrder(buf.toString(), "id=" + id);
		}
		
		return id;
	}
	
	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-3-15
	 * 
	 * 说明：处理订单的 下一个状态
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param s
	 * @return
	 */
	public int getNextStatus(int s){
		int status = 0;
		switch(s){
		case 0:
			status = 1;
			break;
		case 1:
			status = 2;
			break;
		case 3:
			status = 3;
			break;
		default:
			status = s;
		}
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#updateOrderPrice(int)
	 */
	public void modifyOrderPrice(int id, float price) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update user_order set price=? where id="
					+ id);
			pst.setFloat(1, price);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void modifyCpaBonus(int orderId, int bonus) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update user_order set cpa_bonus=? where id="
					+ orderId);
			pst.setFloat(1, bonus);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void modifyOrderPrice(int id, float price, float dprice) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update user_order set price=?, dprice=? where id="
					+ id);
			pst.setFloat(1, price);
			pst.setFloat(2, dprice);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void modifyOrderPrice(int id, float price, float dprice,
			float postage) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update user_order set price=?, dprice=?, postage=? where id="
					+ id);
			pst.setFloat(1, price);
			pst.setFloat(2, dprice);
			pst.setFloat(3, postage);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#calcOrderPrice(int)
	 */
	public float calcOrderPrice(int id) {
		float sum = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn
			.prepareStatement("select sum(c.price*b.count) from user_order_product b,product c where b.order_id=? and b.product_id=c.id");
			pst.setInt(1, id);
			rs = pst.executeQuery();
			if (rs.next()) {
				sum = rs.getFloat(1);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		return sum;
	}

	/*
	 * 请查看父类或接口对应的注释。
	 */
	public float calcOrderPrice(int id, int flat) {
		float sum = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			//WAP订单
			if (flat == 0) {
				pst = conn
				.prepareStatement("select sum(c.price*b.count) from user_order_product b,product c where b.order_id=? and b.product_id=c.id");
			}
			//WEB订单
			else {
				pst = conn
				.prepareStatement("select sum(c.price*b.count) from user_order_product b,wproduct c where b.order_id=? and b.product_id=c.id");
			}
			pst.setInt(1, id);
			rs = pst.executeQuery();
			if (rs.next()) {
				sum = rs.getFloat(1);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		return sum;
	}

	public float calcOrderPrice(int id, int flat, boolean isGroupRate) {
		float sum = 0;
		PreparedStatement pst = null;
		PreparedStatement pstNotGroupBuy = null;
		PreparedStatement pstGroupBuy = null;
		ResultSet rs = null;
		try {
			//WAP订单
			if (flat == 0) {
				pst = conn
				.prepareStatement("select sum(c.price * b.count) from user_order_product b,product c where b.order_id=? and b.product_id=c.id");
				pstNotGroupBuy = conn.prepareStatement("select sum(c.price * b.count) from user_order_product b,product c where b.order_id=? and b.product_id=c.id and c.group_buy_price=0");
				pstGroupBuy = conn.prepareStatement("select sum(c.group_buy_price * b.count) from user_order_product b,product c where b.order_id=? and b.product_id=c.id and c.group_buy_price>0");
			}
			//WEB订单
			else {
				pst = conn
				.prepareStatement("select sum(c.price*b.count) from user_order_product b,wproduct c where b.order_id=? and b.product_id=c.id");
			}
			if(flat != 0){
				pst.setInt(1, id);
				rs = pst.executeQuery();
				if (rs.next()) {
					sum = rs.getFloat(1);
				}
			} else {
				if(!isGroupRate){
					pst.setInt(1, id);
					rs = pst.executeQuery();
					if (rs.next()) {
						sum = rs.getFloat(1);
					}
				} else {
					pstNotGroupBuy.setInt(1, id);
					rs = pstNotGroupBuy.executeQuery();
					if (rs.next()) {
						sum = rs.getFloat(1);
					}
					pstGroupBuy.setInt(1, id);
					rs = pstGroupBuy.executeQuery();
					if (rs.next()) {
						sum += rs.getFloat(1);
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		return sum;
	}


	public float calcOrderPrice(int id, int flat, boolean isGroupRate, boolean isFrequentUser) {
		float sum = 0;
		PreparedStatement pst = null;
		PreparedStatement pstFrequentUserBuy = null;
		PreparedStatement pstNotGroupBuy = null;
		PreparedStatement pstGroupBuy = null;
		PreparedStatement pstGroupFrequentUserBuy = null;
		PreparedStatement pstNotGroupFrequentUserBuy =null;
		ResultSet rs = null;
		try {
			//WAP订单
			if (flat == 0) {
				pst = conn.prepareStatement("select sum(c.price * b.count) from user_order_product b,product c where b.order_id=? and b.product_id=c.id");
				pstFrequentUserBuy = conn.prepareStatement("select sum(c.price * b.count * cd.discount) from user_order_product b,product c, catalog_discount cd where b.order_id=? and b.product_id=c.id and c.parent_id1=cd.catalog_id");
				pstNotGroupBuy = conn.prepareStatement("select sum(c.price * b.count) from user_order_product b,product c where b.order_id=? and b.product_id=c.id and c.group_buy_price=0");
				pstNotGroupFrequentUserBuy = conn.prepareStatement("select sum(c.price * b.count * cd.discount) from user_order_product b,product c,catalog_discount cd where b.order_id=? and b.product_id=c.id and c.parent_id1=cd.catalog_id and c.group_buy_price=0");
				pstGroupBuy = conn.prepareStatement("select sum(c.group_buy_price * b.count) from user_order_product b,product c where b.order_id=? and b.product_id=c.id and c.group_buy_price>0");
				pstGroupFrequentUserBuy = conn.prepareStatement("select sum(c.group_buy_price * b.count * cd.discount) from user_order_product b,product c,catalog_discount cd where b.order_id=? and b.product_id=c.id and c.parent_id1=cd.catalog_id and c.group_buy_price>0");
			}
			//WEB订单
			else {
				pst = conn
				.prepareStatement("select sum(c.price*b.count) from user_order_product b,wproduct c where b.order_id=? and b.product_id=c.id");
			}
			if(flat != 0){
				pst.setInt(1, id);
				rs = pst.executeQuery();
				if (rs.next()) {
					sum = rs.getFloat(1);
				}
			} else {
				if(!isGroupRate){
					if(isFrequentUser){
						pstFrequentUserBuy.setInt(1, id);
						rs = pstFrequentUserBuy.executeQuery();
						if (rs.next()) {
							sum = rs.getFloat(1);
						}
					} else {
						pst.setInt(1, id);
						rs = pst.executeQuery();
						if (rs.next()) {
							sum = rs.getFloat(1);
						}
					}
				} else {
					if(isFrequentUser){
						pstNotGroupFrequentUserBuy.setInt(1, id);
						rs = pstNotGroupFrequentUserBuy.executeQuery();
						if (rs.next()) {
							sum = rs.getFloat(1);
						}
						pstGroupFrequentUserBuy.setInt(1, id);
						rs = pstGroupFrequentUserBuy.executeQuery();
						if (rs.next()) {
							sum += rs.getFloat(1);
						}
					} else {
						pstNotGroupBuy.setInt(1, id);
						rs = pstNotGroupBuy.executeQuery();
						if (rs.next()) {
							sum = rs.getFloat(1);
						}
						pstGroupBuy.setInt(1, id);
						rs = pstGroupBuy.executeQuery();
						if (rs.next()) {
							sum += rs.getFloat(1);
						}
					}
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		return sum;
	}

	/**
	 * 作者：李北金
	 * 
	 * 创建日期：2008-5-8
	 * 
	 * 说明：计算分成广告的分成费用。
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param orderId
	 * @param siteId
	 * @return
	 */
	public int calCpaBonus(int orderId, int siteId, float voPrice) {
		String adultCatalogIds = "83,2,4,5,6,7,1,8,43,93";
		String mobileIds = MobileProductUtil.REAL_MOBILE_IDS;
		float rate = 0.35f; //成人用品的比例
		int mobileBonus = 100; //手机的单笔提成

		int sum = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;

		int mobileCount = 0;
		float mobilePrice = 0;
		float notAdultPrice = 0;
		try {
			//计算其中非成人用品的金额
			pst = conn
			.prepareStatement("select ifnull(sum(c.price*b.count), 0) from user_order_product b,product c where b.order_id=? and b.product_id=c.id and c.parent_id1 not in ("
					+ adultCatalogIds + ")");
			pst.setInt(1, orderId);
			rs = pst.executeQuery();
			if (rs.next()) {
				notAdultPrice = rs.getFloat(1);
			}

			pst = conn
			.prepareStatement("select sum(count) from user_order_product where order_id = "
					+ orderId
					+ " and product_id in ("
					+ mobileIds
					+ ")");
			rs = pst.executeQuery();
			if (rs.next()) {
				mobileCount = rs.getInt(1);
			}
			rs.close();

			//订单没包含手机
			if (mobileCount == 0) {
				sum = (int) ((voPrice - notAdultPrice) * rate);
			}
			//订单包含有手机
			else {
				pst = conn
				.prepareStatement("select sum(c.price*b.count) from user_order_product b,product c where b.order_id=? and b.product_id=c.id and b.product_id in ("
						+ mobileIds + ")");
				pst.setInt(1, orderId);
				rs = pst.executeQuery();
				if (rs.next()) {
					mobilePrice = rs.getFloat(1);
				}
				rs.close();
				sum = (int) ((voPrice - notAdultPrice) * rate + mobileCount
						* mobileBonus);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}

		return sum;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#addPoolProduct(int, int, int)
	 */
	public void addPoolProduct(int type, int productId, int seq) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("insert into product_pool (pool_id, product_id,seq) values (?,?,?)");
			pst.setInt(1, type);
			pst.setInt(2, productId);
			pst.setInt(3, seq);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#addPoolInfo(java.lang.String,
	 *      java.lang.String, int)
	 */
	public void addPoolInfo(String name, String link, int type) {

		executeUpdate("update info_pool set seq=seq+1 where type=" + type);

		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("insert into info_pool (name,link,type,seq) values (?,?,?,0)");
			pst.setString(1, name);
			pst.setString(2, link);
			pst.setInt(3, type);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}
	//检查这个添加的id是否存在
	public boolean checkProductId(String id)
	{
		boolean flag = false;
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("select id from product where id="+id);

			ResultSet rs = pst.executeQuery();
			if(rs.next())
			{
				flag=true;
			} 
		} catch (SQLException sqle) {
			//sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		return flag;
	}
	public void addClothingPool(int type, String pids,String title,String link,int hiden) {
		executeUpdate("update clothing_pool set seq=seq+1 where type=" + type);

		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("insert into clothing_pool (type,product_id,name,link,hide) values (?,?,?,?,?)");
			pst.setInt(1, type);
			pst.setString(2, pids);
			pst.setString(3, title);
			pst.setString(4, link);
			pst.setInt(5, hiden);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}
	public void modifyPoolInfo(int id, String name, String link, int type) {
		//executeUpdate("update info_pool set seq=seq+1 where type=" + type);
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update info_pool set name=?,link=?,type=? where id=?");
			pst.setString(1, name);
			pst.setString(2, link);
			pst.setInt(3, type);
			pst.setInt(4, id);
			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}
	/**
	 * 修改服装普通池子单个信息
	 * @param ids
	 * @param id
	 * @param name
	 * @param link
	 * @param type
	 */
	public void modifyClothingInfo(String ids ,int id, String name, String link, int type,int hiden) {
		//executeUpdate("update info_pool set seq=seq+1 where type=" + type);
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update clothing_pool set name=?,link=?,type=?,hide=?,product_id=? where id=?");
			pst.setString(1, name);
			pst.setString(2, link);
			pst.setInt(3, type);
			pst.setInt(4, hiden);
			pst.setString(5, ids);
			pst.setInt(6, id);
			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#clearPoolProduct(int)
	 */
	public void clearPool(int type, String table) {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("delete from " + table
					+ " where pool_id=" + type);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getCollectProductList(java.lang.String)
	 */
	public List getCollectProductList(String selects) {
		// 已废弃，移动到orderstatservice
		return null;
	}

	public List getCollectPresentList(String selects) {
		// 已废弃，移动到orderstatservice
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getOrders(java.lang.String)
	 */
	public List getOrders(String selects) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = conn
			.prepareStatement("select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id)products from user_order a left outer join user_order_status b on a.status=b.id where a.id in("
					+ selects + ") order by a.id desc");

			rs = pst.executeQuery();
			while (rs.next()) {
				list.add(getOrdersItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#deletePool(int)
	 */
	public void deletePool(int id) {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("delete from pool where id=" + id);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#modifyPoolSeq(int, int)
	 */
	public void modifyPoolSeq(int type, int seqadd, String table) {
		executeUpdate("update " + table + " set seq=seq+" + seqadd
				+ " where pool_id=" + type);

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#stockOut(int)
	 */
	public void stockOut(int orderId) {
		//executeUpdate("update product a,user_order_product b,user_order c set
		// a.stock=a.stock-b.count where a.id=b.product_id and b.order_id=c.id
		// and c.id=" + orderId);
		executeUpdate("update user_order set stockout=1 where id=" + orderId);
		//executeUpdate("insert into stockout_product (product_id, count)
		// (select b.product_id,b.count from user_order_product b where
		// b.order_id=" + orderId + ")");
	}

	public void stockOutProduct(int orderId, int productId, int count, int type) {
		/*
		 * if (type == 1) { executeUpdate("update product a,user_order_product
		 * b,user_order c set a.stock=a.stock-" + count + " where
		 * a.id=b.product_id and b.order_id=c.id and c.id=" + orderId + " and
		 * b.product_id=" + productId); } else if (type == 2) {
		 * executeUpdate("update product a,user_order_product b,user_order c set
		 * a.stock_gd=a.stock_gd-" + count + " where a.id=b.product_id and
		 * b.order_id=c.id and c.id=" + orderId + " and b.product_id=" +
		 * productId); } executeUpdate("insert into stockout_product
		 * (product_id, count, type) values (" + productId + "," + count + "," +
		 * type + ")");
		 */
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#deleteStockinProduct(int)
	 */
	public void deleteStockinProduct(int id) {
		executeUpdate("delete from stockin_product where id=" + id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#getStockouts(java.lang.String, int,
	 *      int)
	 */
	public List getStockouts(String condition, int start, int limit) {
		if (condition.length() > 0)
			condition = "where " + condition;

		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = conn
			.prepareStatement("select distinct(left(create_datetime,10)) a from stockout_product "
					+ condition + " order by a desc limit ?,?");
			pst.setInt(1, start);
			pst.setInt(2, limit);
			rs = pst.executeQuery();
			while (rs.next()) {
				String vo = rs.getString(1);
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#deleteProductPic(java.lang.String)
	 */
	public void deleteProductPic(String condition) {
		executeUpdate("delete from product_pic where " + condition);
	}

	public voCatalog getCatalog(String condition) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		voCatalog vo = null;
		try {
			pst = conn.prepareStatement("select * from catalog where "
					+ condition);

			rs = pst.executeQuery();
			if (rs.next()) {
				vo = new voCatalog();
				vo.setId(rs.getInt("id"));
				vo.setParentId(rs.getInt("parent_id"));
				vo.setName(rs.getString("name"));
				vo.setCode(rs.getString("code"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return vo;
	}

	public boolean startTransaction() {
		if (this.conn == null) {
			return false;
		}
		try {
			this.autoCommit = this.conn.getAutoCommit();
			this.conn.setAutoCommit(false);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean commitTransaction() {
		if (this.conn == null) {
			return false;
		}
		try {
			this.conn.commit();
			this.conn.setAutoCommit(this.autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean rollbackTransaction() {
		if (this.conn == null) {
			return false;
		}
		try {
			this.conn.rollback();
			this.conn.setAutoCommit(this.autoCommit);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void addPoolArticle(int type, int articleId, int seq) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("insert into article_pool (pool_id, article_id,seq) values (?,?,?)");
			pst.setInt(1, type);
			pst.setInt(2, articleId);
			pst.setInt(3, seq);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}
	public List getSupplierProductLineList(){
		List list=new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = conn
			.prepareStatement("select * from supplier_product_line ");
			rs = pst.executeQuery();
			while (rs.next()) {
				SupplierProductLine vo = new SupplierProductLine();
				vo.setProductId(rs.getInt("product_line_id"));
				vo.setSupplierId(rs.getInt("supplier_id"));
				list.add(vo);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return list;
	}


	public void modifyOrderPresent(int orderProductId, String productCode,
			int orderProductCount) {
		PreparedStatement pst = null;
		try {
			int productId = getProductIdByCode(productCode);
			pst = conn
			.prepareStatement("update user_order_present set product_id=?,count=? where id=?");
			pst.setInt(1, productId);
			pst.setInt(2, orderProductCount);
			pst.setInt(3, orderProductId);
			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void deleteOrderPresent(int id) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("delete from user_order_present where id="
					+ id);
			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void deleteOrderPresents(int orderId) {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("delete from user_order_present where order_id=" + orderId);
			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}


	public void deleteOrderPresentSplit(int id) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("delete from user_order_present_split where id="
					+ id);
			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void deleteOrderPresentsSplit(int orderId) {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("delete from user_order_present_split where order_id=" + orderId);
			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void addOrderPresent(int orderId, String productCode,
			int orderPresentCount) {
		PreparedStatement pst = null;
		try {
			int productId = getProductIdByCode(productCode);
			pst = conn
			.prepareStatement("insert into user_order_present (order_id,product_id,count)values(?,?,?)");
			pst.setInt(1, orderId);
			pst.setInt(2, productId);
			pst.setInt(3, orderPresentCount);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public boolean addOrderPresentSplit(int orderId, String productCode,
			int orderPresentCount) {
		PreparedStatement pst = null;
		try {
			int productId = getProductIdByCode(productCode);
			pst = conn
			.prepareStatement("insert into user_order_present_split (order_id,product_id,count)values(?,?,?)");
			pst.setInt(1, orderId);
			pst.setInt(2, productId);
			pst.setInt(3, orderPresentCount);
			int rows = pst.executeUpdate();
			if(rows == 0){
				return false;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(sqle));
			return false;
		} finally {
			closeStatement(pst);
		}
		return true;
	}

	public boolean updateOrderPresentSplit(String set, int id) {
		if(StringUtil.isNull(set)){
			return false;
		}
		Statement st = null;
		try {
			st = conn.createStatement();
			StringBuilder buf = new StringBuilder();
			buf.append("update user_order_present_split set ").append(set).append(" where id=").append(id);
			String sql = buf.toString();
			int rows = st.executeUpdate(sql);
			if(rows == 0){
				return false;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(sqle));
			return false;
		} finally {
			closeStatement(st);
		}

		return true;
	}

	public int getOrderIdByPresent(int id) {
		return getInt("select order_id from user_order_present where id=" + id);
	}

	public List getOrderStatusList() {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			st = conn.createStatement();
			rs = st.executeQuery("select * from user_order_status order by id");
			while (rs.next()) {
				OrderStatusBean vo = new OrderStatusBean();
				vo.setId(rs.getInt("id"));
				vo.setName(rs.getString("name"));
				vo.setSec(rs.getInt("sec"));
				vo.setVisible(rs.getInt("visible"));
				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}
	/**
	 * 通过产品id查询产品编号 从而查询是否缺货
	 */
	public String getProductCode(String pid) {
		Statement st = null;
		ResultSet rs = null;
		String code = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery("select code from product where id="+pid);
			if(rs.next())           		
				code = rs.getString(1);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return code;
	}

	public void mClothingInfo(int id, int hiden) {
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement("update clothing_pool set hide=? where id="+id);
			pst.setInt(1, hiden);
			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}

	}

	public DbOperation getDbOperation() {
		return this.dbOp;
	}


	public int getDistOrderId(int userId) {

		int id = getInt("select id from order_dist_pool_dist where status=0 and user_id=" + userId + " order by seq asc limit 1");
		setOrderStatus(id, 1);
		try {
			Statement st = this.conn.createStatement();
			st.executeUpdate("update order_dist_pool_dist set status=1 where id=" + id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return id;
	}


	public voProductProperty searchProductPropertyByProductId(int id){
		String sql="select * from product_property where product_id=?";
		PreparedStatement pst=null;
		voProductProperty vpp=null;
		try {
			pst=conn.prepareStatement(sql);
			pst.setInt(1, id);
			pst.execute();
			ResultSet rs=pst.getResultSet();
			while(rs.next()){
				vpp=new voProductProperty();
				vpp.setId(rs.getInt("id"));
				vpp.setProductId(rs.getInt("product_id"));
				vpp.setProductParentId1(rs.getInt("product_parent_id1"));
				vpp.setProductParentId2(rs.getInt("product_parent_id2"));
				vpp.setDrove(rs.getInt("drove"));
				vpp.setSize(rs.getInt("size"));
				vpp.setModel(rs.getString("model"));
				vpp.setColor(rs.getInt("color"));
				vpp.setMailingType(rs.getInt("mailing_type"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeStatement(pst);
		}
		return vpp;

	}

	public voProductProperty getProductProperty(String condition){
		String sql = "select * from product_property";
		if(!StringUtil.convertNull(condition).equals("")){
			sql = sql + " where "+condition;
		}

		Statement st = null;
		voProductProperty vpp = null;
		try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);
			if(rs.next()){
				vpp = new voProductProperty();
				vpp.setId(rs.getInt("id"));
				vpp.setProductId(rs.getInt("product_id"));
				vpp.setProductParentId1(rs.getInt("product_parent_id1"));
				vpp.setProductParentId2(rs.getInt("product_parent_id2"));
				vpp.setDrove(rs.getInt("drove"));
				vpp.setSize(rs.getInt("size"));
				vpp.setModel(rs.getString("model"));
				vpp.setColor(rs.getInt("color"));
				vpp.setMailingType(rs.getInt("mailing_type"));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeStatement(st);
		}
		return vpp;

	}

	public void updateProductProperty(voProductProperty vpp){
		int id=vpp.getId();
		int parentId1=vpp.getProductParentId1();
		int parentId2=vpp.getProductParentId2();
		int drove=vpp.getDrove();
		int size=vpp.getSize();
		String model=vpp.getModel();
		int color=vpp.getColor();
		int mailingType=vpp.getMailingType();
		String sql="update product_property set product_parent_id1=?,product_parent_id2=?,drove=?,size=?,model=?," +
		"color=?,mailing_type=? where id=?";
		PreparedStatement pst=null;
		try {
			pst=conn.prepareStatement(sql);
			pst.setInt(1, parentId1);
			pst.setInt(2, parentId2);
			pst.setInt(3, drove);
			pst.setInt(4, size);
			pst.setString(5, model);
			pst.setInt(6, color);
			pst.setInt(7, mailingType);
			pst.setInt(8, id);
			pst.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeStatement(pst);
		}
	}

	public void addProductProperty(voProductProperty vpp){
		int productId=vpp.getProductId();
		int parentId1=vpp.getProductParentId1();
		int parentId2=vpp.getProductParentId2();
		int drove=vpp.getDrove();
		int size=vpp.getSize();
		String model=vpp.getModel();
		int color=vpp.getColor();
		int mailingType=vpp.getMailingType();
		String sql="insert into product_property (product_id,product_parent_id1," +
		"product_parent_id2,drove,size,model,color,mailing_type) values(?,?,?,?,?,?,?,?)";
		PreparedStatement pst=null;
		try {
			pst=conn.prepareStatement(sql);
			pst.setInt(1, productId);
			pst.setInt(2, parentId1);
			pst.setInt(3, parentId2);
			pst.setInt(4, drove);
			pst.setInt(5, size);
			pst.setString(6, model);
			pst.setInt(7, color);
			pst.setInt(8, mailingType);
			pst.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			closeStatement(pst);
		}
	}

	public voProductLine getProductLine(String sql) {
		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("select distinct product_line.id, product_line.name, product_line.remark, ");
		sqlStr.append("product_line.status, product_line_catalog.product_line_id plid from product_line ");
		sqlStr.append("left join product_line_catalog on product_line_catalog.product_line_id ");
		sqlStr.append("= product_line.id where ").append(sql);
		voProductLine proLineBean = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sqlStr.toString());
			if(rs.next()) {
				proLineBean = new voProductLine();
				proLineBean.setId(rs.getInt(1));
				proLineBean.setName(rs.getString(2));
				proLineBean.setRemark(rs.getString(3));
				proLineBean.setStatus(rs.getInt(4));
				proLineBean.setIsexist(rs.getString(5) == null ? false : true);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return proLineBean;
	}

	public List getProductLineList(String sql) {
		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("select distinct product_line.id, product_line.name, product_line.remark, ");
		sqlStr.append("product_line.status, product_line.permission_id, product_line_catalog.product_line_id plid from product_line ");
		sqlStr.append("left join product_line_catalog on product_line_catalog.product_line_id ");
		sqlStr.append("= product_line.id where ").append(sql);
		List proLine = new ArrayList();
		voProductLine proLineBean = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sqlStr.toString());
			while(rs.next()) {
				proLineBean = new voProductLine();
				proLineBean.setId(rs.getInt(1));
				proLineBean.setName(rs.getString(2));
				proLineBean.setRemark(rs.getString(3));
				proLineBean.setStatus(rs.getInt(4));
				proLineBean.setPermissionId(rs.getInt(5));
				proLineBean.setIsexist(rs.getString(6) == null ? false : true);
				proLine.add(proLineBean);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return proLine;
	}

	public void addProductLine(voProductLine vpl) {
		StringBuffer sql = new StringBuffer("insert into product_line ");
		sql.append("(name, remark, status, permission_id) values (?, ?, ?, ?)");
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql.toString());
			pst.setString(1, vpl.getName());
			pst.setString(2, vpl.getRemark());
			pst.setInt(3, vpl.getStatus());
			pst.setInt(4, vpl.getPermissionId());
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void updateProductLine(voProductLine vpl) {
		StringBuffer sql = new StringBuffer("update product_line set ");
		sql.append("name = ?, remark = ?, status = ?, permission_id = ? where id = ?");
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql.toString());
			pst.setString(1, vpl.getName());
			pst.setString(2, vpl.getRemark());
			pst.setInt(3, vpl.getStatus());
			pst.setInt(4, vpl.getPermissionId());
			pst.setInt(5, vpl.getId());
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void deleteProductLine(String sql) {
		String sqlStr = "delete from product_line where " + sql;
		Statement st = null;
		try {
			st = conn.createStatement();
			st.executeUpdate(sqlStr);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(st);
		}
	}

	public List getProductLineListCatalog(String sql) {
		StringBuffer sqlStr = new StringBuffer();
		sqlStr.append("select product_line_catalog.id, product_line_catalog.product_line_id, ");
		sqlStr.append("product_line_catalog.catalog_id, product_line_catalog.catalog_type, ");
		sqlStr.append("catalog.name from product_line_catalog, catalog where catalog.id = ");
		sqlStr.append("product_line_catalog.catalog_id and ").append(sql);
		List proLineCatalog = new ArrayList();
		voProductLineCatalog proLineCatalogBean = null;
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(sqlStr.toString());
			while(rs.next()) {
				proLineCatalogBean = new voProductLineCatalog();
				proLineCatalogBean.setId(rs.getInt(1));
				proLineCatalogBean.setProduct_line_id(rs.getInt(2));
				proLineCatalogBean.setCatalog_id(rs.getInt(3));
				proLineCatalogBean.setCatalog_type(rs.getInt(4));
				proLineCatalogBean.setProduct_line_name(rs.getString(5));
				proLineCatalog.add(proLineCatalogBean);
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return proLineCatalog;
	}

	public void addProductLineCatalog(voProductLineCatalog vplc) {
		StringBuffer sql = new StringBuffer("insert into product_line_catalog ");
		sql.append("(product_line_id, catalog_id, catalog_type) values (?, ?, ?)");
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql.toString());
			pst.setInt(1, vplc.getProduct_line_id());
			pst.setInt(2, vplc.getCatalog_id());
			pst.setInt(3, vplc.getCatalog_type());
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void updateProductLineCatalog(voProductLineCatalog vplc) {
		StringBuffer sql = new StringBuffer("update product_line_catalog set ");
		sql.append("product_line_id = ?, catalog_id = ?, catalog_type = ? where id = ?");
		PreparedStatement pst = null;
		try {
			pst = conn.prepareStatement(sql.toString());
			pst.setInt(1, vplc.getProduct_line_id());
			pst.setInt(2, vplc.getCatalog_id());
			pst.setInt(3, vplc.getCatalog_type());
			pst.setInt(4, vplc.getId());
			pst.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	public void deleteProductLineCatalog(String sql) {
		String sqlStr = "delete from product_line_catalog where " + sql;
		Statement st = null;
		try {
			st = conn.createStatement();
			st.executeUpdate(sqlStr);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(st);
		}
	}


	public void addOrderProduct(int orderId, voOrderProduct orderProduct) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("insert into user_order_product (order_id,product_id,count,product_price,discount_price,product_discount_id)values(?,?,?,?,?,?)");
			pst.setInt(1, orderId);
			pst.setInt(2, orderProduct.getProductId());
			pst.setInt(3, orderProduct.getCount());
			pst.setFloat(4, orderProduct.getProductPrice());
			pst.setFloat(5, orderProduct.getDiscountPrice());
			pst.setInt(6, orderProduct.getProductDiscountId());
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}

	//获取全部品牌
	public List getAllBrandList(){
		List brandList = new ArrayList();
		Statement st=null;
		ResultSet rs=null;
		try{
			st=conn.createStatement();
			rs = st.executeQuery("select distinct brand, pb.name name from product p join product_brand pb on p.brand=pb.id order by p.brand");
			while(rs.next()){
				voSelect vs=new voSelect();
				vs.setId(rs.getInt("brand"));
				vs.setName(rs.getString("name"));
				brandList.add(vs);
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			closeResultSet(rs);
			closeStatement(st);
		}
		return brandList;
	}
	/**
	 * 
	 * 功能:获取 发货人的发货订单
	 * <p>作者 李双 Oct 19, 2011 6:35:17 PM
	 * @param condition
	 * @return
	 */
	public List getOrdersAndOrderStock(String condition) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select  a.id,a.dprice,a.order_type from user_order a, order_stock s where a.id=s.order_id and s.status<>3 ");
		if (condition != null && condition.length() > 0) {
			buf.append(condition);
		}
		buf.append(" group by a.id");
		String query = buf.toString();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			voOrder vo = null;
			while (rs.next()) {
				vo = new voOrder();
				vo.setId(rs.getInt("a.id"));
				vo.setDprice(rs.getFloat("a.dprice"));
				vo.setOrderType(rs.getInt("a.order_type"));
				list.add(vo);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	/**
	 * 
	 * 功能:获取 销售人的成交数量
	 * <p>作者 李双 Oct 19, 2011 6:35:17 PM
	 * @param condition
	 * @return
	 */
	public List getOrdersTypesOlny(String condition) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append("select a.id,a.dprice,a.order_type from user_order a ");
		if (condition != null && condition.length() > 0) {
			buf.append("where ");
			buf.append(condition);
		}
		String query = buf.toString();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			voOrder vo = null;
			while (rs.next()) {
				vo = new voOrder();
				vo.setId(rs.getInt("a.id"));
				vo.setDprice(rs.getFloat("a.dprice"));
				vo.setOrderType(rs.getInt("a.order_type"));
				list.add(vo);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}

	public String getPoolIds(String poolName, String poolIds) {
		String sql="select id from pool where  name like '%"+poolName+"%' and id in "+poolIds;
		DbOperation dbOp = new DbOperation();
		ResultSet rs=dbOp.executeQuery(sql);
		StringBuffer sb=new StringBuffer();
		sb.append("(");
		try {
			while(rs.next()){
				sb.append(rs.getInt("id")).append(",");       	 
			}       
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			dbOp.releaseWithoutConn();
		}
		String sbstr="";
		if(sb.length()>2){
			sbstr=sb.toString().substring(0, sb.length()-1)+")";
		}else{
			sbstr="";
		}
		return sbstr;
	}

	public int getLiShiXiaoLiang(List productIdstr){
		DbOperation dbOp = new DbOperation(DbOperation.DB_SLAVE2);
		int count = 0;
		for(int i=0;i<productIdstr.size();i++){
			String pid=(String) productIdstr.get(i);
			String query = "select sum(stock_out_count) from stock_card where product_id = "
				+ pid
				+ " and card_type = 2";
			ResultSet rs = dbOp.executeQuery(query);
			try {
			if (rs.next()) {
				count = rs.getInt(1);
			}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				dbOp.release();
			}
		}
		return count;
	}
	
	public boolean isContianUserOrderPhoneIndex(int orderId,String phone){
		boolean flag = false;
		PreparedStatement pst=null;
		ResultSet rs = null;
		try {
			pst=conn.prepareStatement("select order_id from user_order_phone_index where order_id=? and phone=? ");
			pst.setInt(1, orderId);
			pst.setString(2, phone);
			rs=pst.executeQuery();
			if(rs.next()){
				flag=true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return flag;
	}
	
	public void addUserOrderPhoneIndex(int orderId,String name,String phone){
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("insert into user_order_phone_index (order_id,order_name,phone) values (?,?,?)");
			pst.setInt(1, orderId);
			pst.setString(2, name);
			pst.setString(3, phone);
			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
	}
	
	public boolean delUserOrderPhoneIndex(int orderId,String phone){
		boolean flag = false;
		PreparedStatement pst=null;
		try {
			pst=conn.prepareStatement("delete from user_order_phone_index where order_id=? and phone=? ");
			pst.setInt(1, orderId);
			pst.setString(2, phone);
			flag=pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		return flag;
	}
	
	/**
	 * @deprecated
	 * 注意： condition 是id串，不能直接使用为where 的条件
	 * 根据订单来源查询主商品
	 */
	public voOrder getMajorOrderByFlag(String condition,long flag){
		PreparedStatement pst = null;
		PreparedStatement pst1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		voOrder vo = null;
		int orderId = 0;
		try {
			pst = conn
			.prepareStatement("select max(create_datetime),uo.id from order_source_info osi, user_order uo where uo.id=osi.order_id and uo.id in("+ condition + ") and osi.order_flag=" + flag + " group by uo.create_datetime");
			pst1 = conn
			.prepareStatement("select * from user_order a left outer join user_order_status b on a.status=b.id left join user_order_lack_deal uold on a.id = uold.id where a.id=?");
			rs1 = pst.executeQuery();
			if (rs1.next()) {
				orderId = rs1.getInt("id");
				pst1.setInt(1, orderId);
				rs = pst1.executeQuery();
				if(rs.next()){
					vo = new voOrder();
					vo.setId(rs.getInt("a.id"));
					vo.setName(rs.getString("a.name"));
					vo.setPhone(rs.getString("phone"));
					vo.setAddress(rs.getString("address"));
					vo.setPostcode(rs.getString("postcode"));
					vo.setBuyMode(rs.getInt("buy_mode"));
					vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
					vo.setCode(rs.getString("code"));
					vo.setUserId(rs.getInt("user_id"));
					vo.setStatus(rs.getInt("status"));
					vo.setOperator(rs.getString("operator"));
					vo.setRemark(rs.getString("remark"));
					vo.setPrice(rs.getFloat("price"));
					vo.setDeliverType(rs.getInt("deliver_type"));
					vo.setRemitType(rs.getInt("remit_type"));
					vo.setCp(rs.getString("cp"));
					vo.setPhone2(rs.getString("phone2"));
					vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
					vo.setFr(rs.getInt("fr"));
					vo.setStatusName(rs.getString("b.name"));
					vo.setAgent(rs.getInt("agent"));
					vo.setAgentMark(rs.getString("agent_mark"));
					vo.setAgentRemark(rs.getString("agent_remark"));
					vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
					vo.setIsReimburse(rs.getInt("is_reimburse"));
					vo.setRealPay(rs.getFloat("real_pay"));
					vo.setPostage(rs.getInt("postage"));
					vo.setIsOrder(rs.getInt("is_order"));
					vo.setImages(rs.getString("images"));
					vo.setAreano(rs.getInt("areano"));
					vo.setPrePayType(rs.getInt("pre_pay_type"));
					vo.setIsOlduser(rs.getInt("is_olduser"));
					vo.setSuffix(rs.getFloat("suffix"));
					vo.setStockout(rs.getInt("stockout"));
					vo.setContactTime(rs.getInt("contact_time"));
					vo.setUnitedOrders(rs.getString("united_orders"));
					vo.setFlat(rs.getInt("flat"));
					vo.setHasAddPoint(rs.getInt("has_add_point"));
					vo.setGender(rs.getInt("gender"));
					vo.setWebRemark(rs.getString("web_remark"));
					vo.setEmail(rs.getString("email"));
					vo.setOriginOrderId(rs.getInt("origin_order_id"));
					vo.setNewOrderId(rs.getInt("new_order_id"));
					vo.setSellerId(rs.getInt("seller_id"));

					vo.setDealDetail(rs.getString("deal_detail"));
					vo.setCpaBonus(rs.getInt("cpa_bonus"));
					vo.setCpaPay(rs.getInt("cpa_pay"));
					vo.setCpaStatus(rs.getInt("cpa_status"));

					vo.setSellerCheckStatus(rs.getInt("seller_check_status"));
					vo.setStockoutRemark(rs.getString("stockout_remark"));
					vo.setConsigner(rs.getString("consigner"));

					vo.setDeliver(rs.getInt("deliver"));
					vo.setProductType(rs.getInt("product_type"));
					vo.setStockoutDeal(rs.getInt("stockout_deal"));
					vo.setBalanceStatus(rs.getInt("balance_status"));
					vo.setDiscount(rs.getFloat("discount"));

					vo.setDprice(rs.getFloat("dprice"));

					vo.setOrderType(rs.getInt("order_type"));
					
					vo.setNextLackDealDatetime(rs.getString("next_deal_datetime"));
					vo.setLackDealAdminId(rs.getInt("uold.admin_id"));
				}
			}
		} catch (Exception sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return vo;
	}
	public float calcOrderByGroupRateProductPrice(int id, int flat) {
		float sum = 0;
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			//WAP订单
			if (flat == 0) {
				pst = conn
				.prepareStatement("select sum(b.discount_price*b.count) from user_order_product b,product c where b.order_id=? and b.product_id=c.id");
			}
			//WEB订单
			else {
				pst = conn
				.prepareStatement("select sum(b.discount_price*b.count) from user_order_product b,wproduct c where b.order_id=? and b.product_id=c.id");
			}
			pst.setInt(1, id);
			rs = pst.executeQuery();
			if (rs.next()) {
				sum = rs.getFloat(1);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		return sum;
	}	
	public List getOrdersByProduct(String condition, int index, int count, String orderBy) {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		StringBuffer buf = new StringBuffer();
		buf.append(
				"select *,count(distinct(a.id)) from user_order a  left outer join user_order_status b on a.status=b.id left join user_order_product uop on a.id = uop.order_id left join product p on uop.product_id = p.id"
				+" join user_order_lack_deal uold on a.id = uold.id");
		if (condition != null && condition.length() > 0) {
			buf.append(" where ");
			buf.append(condition);
		}
		if (orderBy != null && orderBy.length() > 0) {
			buf.append(" order by ");
			buf.append(orderBy);
		}
		if (index >= 0 && count > 0) {
			buf.append(" limit ");
			buf.append(index);
			buf.append(", ");
			buf.append(count);
		}
		String query = buf.toString();
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			voOrder vo = null;
			while (rs.next()) {
				vo = new voOrder();
				vo.setId(rs.getInt("a.id"));
				vo.setName(rs.getString("a.name"));
				vo.setPhone(rs.getString("phone"));
				vo.setAddress(rs.getString("address"));
				vo.setPostcode(rs.getString("postcode"));
				vo.setBuyMode(rs.getInt("buy_mode"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setUserId(rs.getInt("user_id"));
				vo.setConfirmDatetime(rs.getTimestamp("confirm_datetime"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setPrice(rs.getFloat("price"));
				vo.setDiscount(rs.getFloat("discount"));
				vo.setDeliverType(rs.getInt("deliver_type"));
				vo.setRemitType(rs.getInt("remit_type"));
				vo.setStockout(rs.getInt("stockout"));
				vo.setPhone2(rs.getString("phone2"));
				vo.setPrepayDeliver(rs.getFloat("prepay_deliver"));
				vo.setOperator(rs.getString("operator"));
				vo.setAgent(rs.getInt("agent"));
				vo.setAgentMark(rs.getString("agent_mark"));
				vo.setAgentRemark(rs.getString("agent_remark"));
				vo.setIsOrderReimburse(rs.getInt("is_order_reimburse"));
				vo.setIsReimburse(rs.getInt("is_reimburse"));
				vo.setRealPay(rs.getFloat("real_pay"));
				vo.setPostage(rs.getInt("postage"));
				vo.setIsOrder(rs.getInt("is_order"));
				vo.setAreano(rs.getInt("areano"));
				vo.setPrePayType(rs.getInt("pre_pay_type"));
				vo.setIsOlduser(rs.getInt("is_olduser"));
				vo.setSuffix(rs.getFloat("suffix"));
				vo.setContactTime(rs.getInt("contact_time"));
				vo.setUnitedOrders(rs.getString("united_orders"));
				vo.setFlat(rs.getInt("flat"));
				vo.setHasAddPoint(rs.getInt("has_add_point"));
				vo.setGender(rs.getInt("gender"));
				vo.setWebRemark(rs.getString("web_remark"));
				vo.setEmail(rs.getString("email"));
				vo.setOriginOrderId(rs.getInt("origin_order_id"));
				vo.setNewOrderId(rs.getInt("new_order_id"));
				vo.setRemark(rs.getString("remark"));

				vo.setDealDetail(rs.getString("deal_detail"));
				vo.setCpaBonus(rs.getInt("cpa_bonus"));
				vo.setCpaPay(rs.getInt("cpa_pay"));
				vo.setCpaStatus(rs.getInt("cpa_status"));

				vo.setSellerCheckStatus(rs.getInt("seller_check_status"));

				vo.setOrderType(rs.getInt("order_type"));
				vo.setLastOperTime(rs.getString("last_deal_time"));
				vo.setStockoutDeal(rs.getInt("stockout_deal"));
				vo.setLackDealAdminId(rs.getInt("uold.admin_id"));

				list.add(vo);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return list;
	}
	
	/**
	 * 查询指定条件的id的list集合
	 * @param condition String：查询条件
	 * @return String：指定条件的id的list集合
	 */
	public String getOrderIds(String condition) {
		Statement st = null;
		ResultSet rs = null;
		StringBuffer buf = new StringBuffer();
		buf.append("select id from user_order where user_order.order_type in (").append(condition).append(")");
		String query = buf.toString();
		StringBuffer ids = new StringBuffer();
		String result = null;
		try {
			st = conn.createStatement();
			rs = st.executeQuery(query);
			int index = 0;
			while (rs.next()) {
				if(index == 10) {
					break;
				}
				ids.append(rs.getInt("id")).append(",");
				index++;
			}
			result = ids.toString();
			result = result.substring(0, result.length()-1);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return result;
	}
	
	/**
	 * 说明：添加订单扩展信息
	 * 作者：赵林
	 * 创建时间：2012-09-24
	 * 
	 */
	public boolean addOrderExtendInfo(voOrderExtendInfo vo) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("insert into user_order_extend_info values(?,?,?,?,?,?,?,?,?,?)");
			pst.setInt(1, vo.getId());
			pst.setString(2, vo.getOrderCode());
			pst.setFloat(3, vo.getOrderPrice());
			pst.setInt(4, vo.getPayMode());
			pst.setInt(5, vo.getPayStatus());
			pst.setInt(6, vo.getAddId1());
			pst.setInt(7, vo.getAddId2());
			pst.setInt(8, vo.getAddId3());
			pst.setInt(9, vo.getAddId4());
			pst.setString(10, vo.getAdd5());
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return false;
		} finally {
			closeStatement(pst);
		}
		return true;
	}
	
	/**
	 * 说明：添加订单扩展信息
	 * 作者：赵林
	 * 创建时间：2012-09-24
	 * 
	 */
	public boolean updateOrderExtendInfo(voOrderExtendInfo vo) {
		PreparedStatement pst = null;
		try {
			pst = conn
			.prepareStatement("update user_order_extend_info set order_code=?,order_price=?,pay_mode=?,pay_status=?,add_id1=?,add_id2=?,add_id3=?,add_id4=?,add_5=? where id=?");
			pst.setString(1, vo.getOrderCode());
			pst.setFloat(2, vo.getOrderPrice());
			pst.setInt(3, vo.getPayMode());
			pst.setInt(4, vo.getPayStatus());
			pst.setInt(5, vo.getAddId1());
			pst.setInt(6, vo.getAddId2());
			pst.setInt(7, vo.getAddId3());
			pst.setInt(8, vo.getAddId4());
			pst.setString(9, vo.getAdd5());
			pst.setInt(10, vo.getId());
			if(pst.executeUpdate()==0){
				return false;
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return false;
		} finally {
			closeStatement(pst);
		}
		
		return true;
	}
	
	public voOrderExtendInfo getOrderExtendInfo(int id){
		PreparedStatement pst = null;
		ResultSet rs = null;
		voOrderExtendInfo vo = null;
		try {
			pst = conn
			.prepareStatement("select * from user_order_extend_info where id=?");
			pst.setInt(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				vo = new voOrderExtendInfo();
				vo.setId(rs.getInt("id"));
				vo.setOrderCode(rs.getString("order_code"));
				vo.setOrderPrice(rs.getFloat("order_price"));
				vo.setPayMode(rs.getInt("pay_mode"));
				vo.setPayStatus(rs.getInt("pay_status"));
				vo.setAddId1(rs.getInt("add_id1"));
				vo.setAddId2(rs.getInt("add_id2"));
				vo.setAddId3(rs.getInt("add_id3"));
				vo.setAddId4(rs.getInt("add_id4"));
				vo.setAdd5(rs.getString("add_5"));
			}

		} catch (Exception sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return vo;
	}

	public voProduct getProduct2(int id) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		voProduct vo = null;
		try {
			pst = conn.prepareStatement("select * from product where id=?");
			pst.setInt(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				vo = new voProduct();
				vo.setId(rs.getInt("id"));
				vo.setName(rs.getString("name"));
				vo.setOriname(rs.getString("oriname"));
				vo.setPrice(rs.getFloat("price"));
				vo.setPrice2(rs.getFloat("price2"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setPrice4(rs.getFloat("price4"));
				vo.setPrice5(rs.getFloat("price5"));
				vo.setDeputizePrice(rs.getFloat("deputize_price"));
				vo.setGroupBuyPrice(rs.getFloat("group_buy_price"));
				vo.setPic(rs.getString("pic"));
				vo.setPic2(rs.getString("pic2"));
				vo.setPic3(rs.getString("pic3"));
				vo.setIntro(rs.getString("intro"));
				vo.setIntro2(rs.getString("intro2"));
				vo.setParentId1(rs.getInt("parent_id1"));
				vo.setParentId2(rs.getInt("parent_id2"));
				vo.setParentId3(rs.getInt("parent_id3"));
				vo.setFlag(rs.getInt("flag"));
				vo.setCommentCount(rs.getInt("comment_count"));
				vo.setClickCount(rs.getInt("click_count"));
				vo.setBuyCount(rs.getInt("buy_count"));
				vo.setProxyId(rs.getInt("proxy_id"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setProxysName(rs.getString("proxys_name"));
				vo.setRemark(rs.getString("remark"));
				vo.setUnit(rs.getString("unit"));
				vo.setStock(rs.getInt("stock"));
				vo.setStockGd(rs.getInt("stock_gd"));
				vo.setImages(rs.getString("images"));
				vo.setGuanggao(rs.getString("guanggao"));
				vo.setGongxiao(rs.getString("gongxiao"));
				vo.setShiyongrenqun(rs.getString("shiyongrenqun"));
				vo.setShiyongfangfa(rs.getString("shiyongfangfa"));
				vo.setZhuyishixiang(rs.getString("zhuyishixiang"));
				vo.setTebietishi(rs.getString("tebietishi"));
				vo.setBaozhuangzhongliang(rs.getString("baozhuangzhongliang"));
				vo.setBaozhuangdaxiao(rs.getString("baozhuangdaxiao"));
				vo.setChanpinzhongliang(rs.getString("chanpinzhongliang"));
				vo.setChanpinchicun(rs.getString("chanpinchicun"));
				vo.setChanpinchengfen(rs.getString("chanpinchengfen"));
				vo.setChangshang(rs.getString("changshang"));
				vo.setBaozhiqi(rs.getString("baozhiqi"));
				vo.setChucangfangfa(rs.getString("chucangfangfa"));
				vo.setPizhunwenhao(rs.getString("pizhunwenhao"));
				vo.setChangshangjieshao(rs.getString("changshangjieshao"));
				vo.setFuwuchengnuo(rs.getString("fuwuchengnuo"));
				vo.setRank(rs.getInt("rank"));
				vo.setDisplayOrder(rs.getInt("display_order"));
				vo.setTopOrder(rs.getInt("top_order"));
				vo.setStockLineBj(rs.getInt("stock_line_bj"));
				vo.setStockLineGd(rs.getInt("stock_line_gd"));
				vo.setStockStandardBj(rs.getInt("stock_standard_bj"));
				vo.setStockStandardGd(rs.getInt("stock_standard_gd"));
				vo.setStockStatus(rs.getInt("stock_status"));
				vo.setIsPackage(rs.getInt("is_package"));
				vo.setHasPresent(rs.getInt("has_present"));
				vo.setBrand(rs.getInt("brand"));
				vo.setShowPackage(rs.getInt("show_package"));
				vo.setBjStockin(rs.getString("bj_Stockin"));
				vo.setGdStockin(rs.getString("gd_Stockin"));
				vo.setStockDayBj(rs.getInt("stock_day_bj"));
				vo.setStockDayGd(rs.getInt("stock_day_gd"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
			closeResultSet(rs);
		}
		return vo;
	}
	

	public List getProductBrandList(String condition,int start,int limit,String orderBy){
		StringBuilder sqlStr= new StringBuilder();
		sqlStr.append("select * from product_brand");

		if(!StringUtil.convertNull(condition).equals("")){
			sqlStr.append(" where ");
			sqlStr.append(condition);
		}
		if(orderBy != null){
			sqlStr.append(" order by ");
			sqlStr.append(orderBy);
		}
		if (start >= 0 && limit > 0) {
			sqlStr.append(" limit ");
			sqlStr.append(start);
			sqlStr.append(",");
			sqlStr.append(limit);
		}
		Statement st=null;
		ResultSet rs=null;
		List list=new ArrayList();
		try {
			st=conn.createStatement();
			rs=st.executeQuery(sqlStr.toString());
			while(rs.next()){
				voSelect vs=new voSelect();
				vs.setId(rs.getInt("id"));
				vs.setName(rs.getString("name"));
				list.add(vs);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeStatement(st);
		}
		return list;
	}

	public List getProductPropertyInfoList(String condition, int start, int limit,
			String orderBy){
		StringBuilder sqlStr= new StringBuilder();
		sqlStr.append("select * from product_property_info");

		if(!StringUtil.convertNull(condition).equals("")){
			sqlStr.append(" where ");
			sqlStr.append(condition);
		}
		if(orderBy != null){
			sqlStr.append(" order by ");
			sqlStr.append(orderBy);
		}
		if (start >= 0 && limit > 0) {
			sqlStr.append(" limit ");
			sqlStr.append(start);
			sqlStr.append(",");
			sqlStr.append(limit);
		}

		Statement st=null;
		ResultSet rs=null;
		List list=new ArrayList();
		try {
			st=conn.createStatement();
			rs=st.executeQuery(sqlStr.toString());
			while(rs.next()){
				voProductPropertyInfo vppi=new voProductPropertyInfo();
				vppi.setId(rs.getInt("id"));
				vppi.setName(rs.getString("name"));
				vppi.setValue(rs.getInt("value"));
				vppi.setType(rs.getInt("type"));
				vppi.setSortId(rs.getInt("sort_id"));
				list.add(vppi);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			closeStatement(st);
		}
		return list;
	}
	
	/**
	 * 功能:根据订单状态及发货状态是否变化，添加订单状态日志中，用于日志统计
	 * @param oriStatus   
	 *	原订单状态
	 *@param status   
	 *	修改后订单状态
	 *@param oriStockoutDeal   
	 *	原发货状态
	 *@param stockoutDeal   
	 *	修改后发货状态
	 */
	public void addOrderAdminStatusLog(IAdminLogService logService,int oriStatus,int status,int oriStockoutDeal,int stockoutDeal,OrderAdminLogBean log){
		OrderAdminStatusLogBean statusLog  = new OrderAdminStatusLogBean();
		statusLog.setCreateDatetime(log.getCreateDatetime());
		statusLog.setOrderId(log.getOrderId());
		statusLog.setUsername(log.getUsername());
		if(oriStatus >=0 && status>=0 && oriStatus != status){
			if(status==1||status==2||status==3||status==6||status==7||status==8||status==11||status==14){
				//新增订单状态变化日志
				statusLog.setType(1);
				statusLog.setOriginStatus(oriStatus);
				statusLog.setNewStatus(status);
				logService.addOrderAdminStatusLog(statusLog);
				if(status==7&&oriStatus==3){
					statusLog.setType(2);
					logService.addOrderAdminStatusLog(statusLog);
				}
			}
		}
		if(oriStockoutDeal >=0 && stockoutDeal>=0 && oriStockoutDeal != stockoutDeal){
			if(stockoutDeal==1||stockoutDeal==2||stockoutDeal==9||stockoutDeal==5||stockoutDeal==6||stockoutDeal==8){
				//新增发货状态变化日志
				statusLog.setType(2);
				statusLog.setOriginStatus(oriStockoutDeal);
				statusLog.setNewStatus(stockoutDeal);
				logService.addOrderAdminStatusLog(statusLog);
			}
		}
	}
	
	public Map getCashTickets(IProductDiscountService ipds){
		Map m=new HashMap();
		List cts=ipds.getResultInfoList(null, 0, -1, null);//所有的优惠券活动
		if(cts==null||cts.size()<=0){return null;}
		for(int i=0;i<cts.size();i++){
			CashTicketInfoBean ctib=(CashTicketInfoBean) cts.get(i);
			if(ctib==null){continue;}
			m.put(ctib.getCode(), ctib.getId()+"");
		}
		return m;
	}
	
	public void deleteSendMsgAndAutolibrary(voOrder order){
    	if(order!=null && order.getIsOlduser()==1 || order.getOrderType()==4 ||order.getOrderType()==17){//以前是老用户，现在加了服装订单 再加老用户订单-ls
	    	DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB_SMS);
			ISMSService smsService = ServiceFactory.createSMSService(IBaseService.CONN_IN_SERVICE, dbOp);
			try{
				smsService.deleteSendMessageOrderId("order_id =" +order.getId());
			}catch (Exception e) {
				e.printStackTrace();
			}finally{
				dbOp.release();
			}
    	}
    }
	
	@Override
	public boolean addSubOrder(voOrder vo) {
		PreparedStatement pst = null;
		boolean result = false;
		try {
			pst = conn
			.prepareStatement("insert into user_order (name,phone,address,postcode,remark,status,code,user_id,discount,buy_mode,remit_type,deliver_type,price,phone2,prepay_deliver,operator,postage,areano,pre_pay_type,is_olduser,suffix,flat,origin_order_id, new_order_id, seller_id, consigner, last_deal_time, fr, web_remark, email, deal_detail,create_datetime,dprice,gender,order_type)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?, now(), ?, ?,?,?,?,?,?,?)");
			pst.setString(1, vo.getName());
			pst.setString(2, vo.getPhone());
			pst.setString(3, vo.getAddress());
			pst.setString(4, vo.getPostcode());
			pst.setString(5, vo.getRemarks());
			pst.setInt(6, vo.getStatus());
			pst.setString(7, vo.getCode());
			pst.setInt(8, vo.getUserId());
			pst.setFloat(9, vo.getDiscount());
			pst.setInt(10, vo.getBuyMode());
			pst.setInt(11, vo.getRemitType());
			pst.setInt(12, vo.getDeliverType());
			pst.setFloat(13, vo.getPrice());
			pst.setString(14, vo.getPhone2());
			pst.setFloat(15, vo.getPrepayDeliver());
			pst.setString(16, vo.getOperator());
			pst.setFloat(17, vo.getPostage());
			pst.setInt(18, vo.getAreano());
			pst.setInt(19, vo.getPrePayType());
			pst.setInt(20, vo.getIsOlduser());
			pst.setFloat(21, vo.getSuffix());
			pst.setInt(22, vo.getFlat());
			pst.setInt(23, vo.getOriginOrderId());
			pst.setInt(24, vo.getNewOrderId());
			pst.setInt(25, vo.getSellerId());
			pst.setString(26, vo.getConsigner());
			pst.setInt(27, vo.getFr());
			pst.setString(28, vo.getWebRemark());
			pst.setString(29, vo.getEmail());
			pst.setString(30, vo.getDealDetail());
			pst.setTimestamp(31, vo.getCreateDatetime());
			pst.setFloat(32, vo.getDprice());
			pst.setInt(33, vo.getGender());
			pst.setInt(34, vo.getOrderType());
			pst.executeUpdate();
			result = true;
		} catch (SQLException sqle) {
			result = false;
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		int id = getLastInsertId("user_order");
		vo.setId(id);
		return result;
	}
	
	public boolean copyUserOrderPromotionToSubOrder(int newId ,int oldId,  int productId){
		PreparedStatement pst = null;
		boolean result = false;
		try {
			StringBuilder sb = new StringBuilder(300);
			sb.append("insert into user_order_promotion_product (order_id,user_id,product_id,flag,promotion_id,product_price,discount_price,count,point,update_time)")
			.append(" select ?,user_id,product_id,flag,promotion_id,product_price,discount_price,count,point,update_time from")
			.append(" user_order_promotion_product where order_id = ? and product_id = ?");
			pst = conn.prepareStatement(sb.toString());
			pst.setInt(1, newId);
			pst.setInt(2, oldId);
			pst.setInt(3,productId);
			pst.executeUpdate();
			result = true;
		} catch (SQLException sqle) {
			result = false;
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		return result;
	}
	
	public boolean copyUserOrderProductToSubOrder(int newId ,int oldId,  int productId){
		PreparedStatement pst = null;
		boolean result = false;
		try {
			StringBuilder sb = new StringBuilder(300);
			sb.append("insert into user_order_product (order_id,product_id,count,status,price3,product_price,discount_price,product_discount_id,product_preference_id)")
			.append(" select ?,product_id,count,status,price3,product_price,discount_price,product_discount_id,product_preference_id from")
			.append(" user_order_product where order_id = ? and product_id = ?");
			pst = conn.prepareStatement(sb.toString());
			pst.setInt(1, newId);
			pst.setInt(2, oldId);
			pst.setInt(3,productId);
			pst.executeUpdate();
			result = true;
		} catch (SQLException sqle) {
			result = false;
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		return result;
	}
}
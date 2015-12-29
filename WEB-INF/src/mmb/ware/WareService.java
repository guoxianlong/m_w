/**
 * 
 */
package mmb.ware;

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
import mmb.stock.stat.DeliverCorpInfoBean;

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
import adultadmin.action.vo.voSelect;
import adultadmin.bean.OrderStatusBean;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.order.OrderStockProductBean;
import adultadmin.bean.productProperty.CashTicketInfoBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.stock.StockAreaBean;
import adultadmin.bean.stock.StockCardTypeBean;
import adultadmin.bean.stock.StockTypeBean;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.service.infc.IAdminLogService;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductDiscountService;
import adultadmin.service.infc.ISMSService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;

/**
 * @author Bomb
 *  
 */
public class WareService extends BaseServiceImpl {

	public Log stockLog = LogFactory.getLog("stock.Log");

	public WareService() {
		this.dbOp = new DbOperation();
		this.dbOp.init(DbOperation.DB_WARE);
	}

	public WareService(DbOperation dbOp){
		this.dbOp = dbOp;
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
			pst = this.dbOp.getConn().prepareStatement("select *, (select name from product_status where product_status.id=product.status) product_status_name from product where id=?");
			pst.setInt(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				vo = new voProduct();
				vo.setId(rs.getInt("id"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setName(rs.getString("name"));
				vo.setOriname(rs.getString("oriname"));
				vo.setPrice(rs.getFloat("price"));
				vo.setPrice2(rs.getFloat("price2"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setPrice4(rs.getFloat("price4"));
				vo.setPrice5(rs.getFloat("price5"));
				vo.setParentId1(rs.getInt("parent_id1"));
				vo.setParentId2(rs.getInt("parent_id2"));
				vo.setParentId3(rs.getInt("parent_id3"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setStock(rs.getInt("stock"));
				vo.setStockGd(rs.getInt("stock_gd"));
				vo.setStockLineBj(rs.getInt("stock_line_bj"));
				vo.setStockLineGd(rs.getInt("stock_line_gd"));
				vo.setStockStandardBj(rs.getInt("stock_standard_bj"));
				vo.setStockStandardGd(rs.getInt("stock_standard_gd"));
				vo.setIsPackage(rs.getInt("is_package"));
				vo.setBrand(rs.getInt("brand"));
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
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
			pst = this.dbOp.getConn()
					.prepareStatement("select id,code,name,oriname,parent_id1,parent_id2,parent_id3 from product where id=?");
			pst.setInt(1, id);

			rs = pst.executeQuery();
			if (rs.next()) {
				vo = new voProduct();
				vo.setId(rs.getInt("id"));
				vo.setCode(rs.getString("code"));
				vo.setName(rs.getString("name"));
				vo.setOriname(rs.getString("oriname"));
				vo.setParentId1(rs.getInt("parent_id1"));
				vo.setParentId2(rs.getInt("parent_id1"));
				vo.setParentId3(rs.getInt("parent_id3"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return vo;
	}

	public voProduct getProduct(String code) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		voProduct vo = null;
		try {
			pst = this.dbOp.getConn().prepareStatement("select *, (select name from product_status where product_status.id=product.status) product_status_name from product where code = ?");
			pst.setString(1, code);

			rs = pst.executeQuery();
			if (rs.next()) {
				vo = new voProduct();
				vo.setId(rs.getInt("id"));
				vo.setCreateDatetime(rs.getTimestamp("create_datetime"));
				vo.setName(rs.getString("name"));
				vo.setOriname(rs.getString("oriname"));
				vo.setPrice(rs.getFloat("price"));
				vo.setPrice2(rs.getFloat("price2"));
				vo.setPrice3(rs.getFloat("price3"));
				vo.setPrice4(rs.getFloat("price4"));
				vo.setPrice5(rs.getFloat("price5"));
				vo.setParentId1(rs.getInt("parent_id1"));
				vo.setParentId2(rs.getInt("parent_id2"));
				vo.setParentId3(rs.getInt("parent_id3"));
				vo.setStatus(rs.getInt("status"));
				vo.setCode(rs.getString("code"));
				vo.setStock(rs.getInt("stock"));
				vo.setStockGd(rs.getInt("stock_gd"));
				vo.setStockLineBj(rs.getInt("stock_line_bj"));
				vo.setStockLineGd(rs.getInt("stock_line_gd"));
				vo.setStockStandardBj(rs.getInt("stock_standard_bj"));
				vo.setStockStandardGd(rs.getInt("stock_standard_gd"));
				vo.setIsPackage(rs.getInt("is_package"));
				vo.setBrand(rs.getInt("brand"));
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
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
			st = this.dbOp.getConn().createStatement();
			if(condition != null && condition.length() > 0){
				rs = st.executeQuery("select *,GROUP_CONCAT(distinct d.name) sName from product a left outer join product_status b on a.status=b.id left outer join product_supplier c on a.id=c.product_id  left join supplier_standard_info d on d.id=c.supplier_id and d.status=1 where " + condition + " group by a.id");
				if (rs.next()) {
					vo = getProductsItem1(rs);
				}
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
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
			st = this.dbOp.getConn().createStatement();
			rs = st.executeQuery("select * from product a left outer join product_status b on status=b.id left outer join product_proxy c on proxy_id=c.id " + StringUtil.convertNull(condition));
			while (rs.next()) {
				list.add(getProductsItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}

	//	获取已经处于处理中和待处理状态下的产品个数
	public Hashtable getOrderStockProductList(String sql) {
		Statement st = null;
		ResultSet rs = null;
		Hashtable hm=new Hashtable();
		try {
			st = this.dbOp.getConn().createStatement();
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
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
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
			st = this.dbOp.getConn().createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				list.add(getProductsItem1(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}
	public List getProductList2(String condition, int start, int limit, String orderBy) {
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
			st = this.dbOp.getConn().createStatement();
			rs = st.executeQuery(sql);
			while (rs.next()) {
				list.add(getProductsItem2(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
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
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}
	/*
	 * 获取库类型方法
	 */
	public List<StockTypeBean> getStockTypeList() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<StockTypeBean> list = new ArrayList<StockTypeBean>();
		try {
			pst = this.dbOp.getConn()
					.prepareStatement("select * from stock_type order by id ");
			
			rs = pst.executeQuery();
			while (rs.next()) {
				StockTypeBean bean = new StockTypeBean();
				bean.setId(rs.getInt("id"));
				bean.setName(rs.getString("name"));
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
	 * 获取库类型对应所有的库地区方法
	 */
	public Map<Integer,List<StockAreaBean>> getTypeToAreaMap() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<StockAreaBean> list = null;
		Map<Integer,List<StockAreaBean>> map = null;
		try {
			//获取所以的库类型及对应的库区域
			pst = this.dbOp.getConn()
					.prepareStatement("select st.id st_id,sa.id sa_id,sa.name sa_name from stock_type st,stock_area_type sat,stock_area sa where st.id = sat.type_id and sat.area_id = sa.id order by st.id,sa.id ");
			
			rs = pst.executeQuery();
			map = new HashMap<Integer, List<StockAreaBean>>();
			StockAreaBean bean = null;
			Integer id = 0;
			while (rs.next()) {
				id = rs.getInt("st_id");
				bean = new StockAreaBean();
				if(map.containsKey(id)){
					list = map.get(id);
				}else{
					 list = new ArrayList<StockAreaBean>();
				}
				bean.setId(rs.getInt("sa_id"));
				bean.setName(rs.getString("sa_name"));
				list.add(bean);
				map.put(id, list);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return map;
	}
	/*
	 * 获取库地区方法
	 */
	public List<StockAreaBean> getStockAreaList() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<StockAreaBean> list = new ArrayList<StockAreaBean>();
		try {
			pst = this.dbOp.getConn()
					.prepareStatement("select * from stock_area order by id ");
			
			rs = pst.executeQuery();
			while (rs.next()) {
				StockAreaBean bean = new StockAreaBean();
				bean.setId(rs.getInt("id"));
				bean.setName(rs.getString("name"));
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
	 * 获取Mdc库地区方法
	 */
	public List<StockAreaBean> getMdcStockAreaList() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<StockAreaBean> list = new ArrayList<StockAreaBean>();
		try {
			pst = this.dbOp.getConn()
					.prepareStatement("select * from stock_area where mdc=1 order by id ");
			
			rs = pst.executeQuery();
			while (rs.next()) {
				StockAreaBean bean = new StockAreaBean();
				bean.setId(rs.getInt("id"));
				bean.setName(rs.getString("name"));
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
	 * 获取可发货库地区方法
	 */
	public List<StockAreaBean> getStockoutAvailableAreaList() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<StockAreaBean> list = new ArrayList<StockAreaBean>();
		try {
			pst = this.dbOp.getConn()
			.prepareStatement("select * from stock_area where type = 1 order by id ");
			
			rs = pst.executeQuery();
			while (rs.next()) {
				StockAreaBean bean = new StockAreaBean();
				bean.setId(rs.getInt("id"));
				bean.setName(rs.getString("name"));
				bean.setType(rs.getInt("type"));
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
	 * 获取库存卡片的类型List
	 */
	public List<StockCardTypeBean> getStockCardTypeList() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<StockCardTypeBean> list = new ArrayList<StockCardTypeBean>();
		try {
			pst = this.dbOp.getConn()
					.prepareStatement("select * from stock_card_type order by id ");
			
			rs = pst.executeQuery();
			while (rs.next()) {
				StockCardTypeBean bean = new StockCardTypeBean();
				bean.setId(rs.getInt("id"));
				bean.setName(rs.getString("name"));
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
	 * 获取库地区对应所有的库类型方法
	 */
	public Map<Integer,List<StockTypeBean>> getAreaToTypeMap() {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List<StockTypeBean> list = null;
		Map<Integer,List<StockTypeBean>> map = null;
		try {
			//获取所以的库地区及对应的库类型
			pst = this.dbOp.getConn()
					.prepareStatement("select sa.id sa_id,sa.name sa_name,st.id st_id,st.name st_name from stock_type st,stock_area_type sat,stock_area sa where st.id = sat.type_id and sat.area_id = sa.id order by sa.id,st.id ");
			
			rs = pst.executeQuery();
			map = new HashMap<Integer, List<StockTypeBean>>();
			StockTypeBean bean = null;
			Integer id = 0;
			while (rs.next()) {
				id = rs.getInt("sa_id");
				bean = new StockTypeBean();
				if(map.containsKey(id)){
					list = map.get(id);
				}else{
					 list = new ArrayList<StockTypeBean>();
				}
				bean.setId(rs.getInt("st_id"));
				bean.setName(rs.getString("st_name"));
				list.add(bean);
				map.put(id, list);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return map;
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
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return list;
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
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return vo;
	}

	public voOrder getOrder(String condition) {
		Statement st = null;
		ResultSet rs = null;
		voOrder vo = null;
		try {
			st = this.dbOp.getConn().createStatement();
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
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
		}
		return vo;
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
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
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
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}

	public List getOrderProducts(String condition) {
		Statement st = null;
		ResultSet rs = null;
		String sql = "";
		List list = new ArrayList();
		try {
			st = this.dbOp.getConn().createStatement();
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
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}


	public List getOrderProductsSplit(int orderId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}

	public voOrderProduct getOrderProduct(int orderId, String productCode) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return null;
	}

	public voOrderProduct getOrderProduct(String condition) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = this.dbOp.getConn().prepareStatement("select * from user_order_product  where "+condition);
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return null;
	}

	public voOrderProduct getOrderProductSplit(int orderId, String productCode) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return null;
	}

	public List getOrderPresents(int orderId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}


	public List getOrderPresents(String condition) {
		ResultSet rs = null;
		Statement st = null;
		List list = new ArrayList();
		try {
			st=this.dbOp.getConn().createStatement();
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
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}

	public List getOrderPresentsSplit(int orderId) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}

	public voOrderProduct getOrderPresent(int orderId, String productCode) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return null;
	}

	public voOrderProduct getOrderPresentSplit(int orderId, String productCode) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return null;
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
			st = this.dbOp.getConn().createStatement();
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
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
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
			st = this.dbOp.getConn().createStatement();
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
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
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
			st = this.dbOp.getConn().createStatement();
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
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
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
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
		}
		// 修改产品信息，不再修改产品的 code
		// updateProductCode(vo.getId());
	}

	public void modifyProductMark(int id, voProduct vo) {
		PreparedStatement pst = null;
		try {
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
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
			pst = this.dbOp.getConn().prepareStatement("select * from " + table + " " + condition);

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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}

	public voSelect getSelect(String table, String condition) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		voSelect vo = null;
		try {

			pst = this.dbOp.getConn().prepareStatement("select * from " + table + " " +condition);

			rs = pst.executeQuery();
			while (rs.next()) {
				vo = new voSelect();
				vo.setId(rs.getInt("id"));
				vo.setName(rs.getString("name"));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
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
			pst = this.dbOp.getConn().prepareStatement("select " + field + " from " + table
					+ " where " + condition);
			rs = pst.executeQuery();
			if (rs.next()) {
				return rs.getString(field);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
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
			pst = this.dbOp.getConn().createStatement();
			String sql = "select a.*,b.name,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=a.id) products,(ifnull((select sum(e.price3 * e.count) from user_order_product e where e.order_id=a.id),0) + ifnull((select sum(e.price3 * e.count) from user_order_present e where e.order_id=a.id),0)) order_price3,uocp.stockout_remark psremark,mb.packagenum packagenum from user_order a left join user_order_phone_index uopi on a.id=uopi.order_id left outer join user_order_status b on a.status=b.id left join user_order_lack_deal uold on a.id = uold.id left join mailing_balance mb on a.id = mb.order_id left join order_stock os on a.id = os.order_id left join  user_order_common_properties uocp on a.id=uocp.order_id where 1=1 "
					+ condition;

			rs = pst.executeQuery(sql);
			while (rs.next()) {
				voOrder order = getOrdersItem(rs);
				order.setPrice3(rs.getFloat("order_price3"));
				order.setStockoutRemark(rs.getString("psremark"));
				order.setPackageNum(StringUtil.convertNull(rs.getString("packagenum")));
				list.add(order);
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}

	public List searchOrder2(String condition) {
		Statement pst = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			pst = this.dbOp.getConn().createStatement();
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
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

			pst = this.dbOp.getConn().prepareStatement(sql);
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
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

			pst = this.dbOp.getConn().prepareStatement(sql);

			rs = pst.executeQuery();
			while (rs.next()) {
				list.add(getOrdersItem(rs));
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

			pst = this.dbOp.getConn().prepareStatement(sql);

			rs = pst.executeQuery();
			while (rs.next()) {

				list.add(getProductsItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
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

			pst = this.dbOp.getConn().prepareStatement(sql);

			rs = pst.executeQuery();
			while (rs.next()) {

				list.add(getProductsItem(rs));
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
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
		
		//销售类型
		String sellSql=" left join product_sell_property psp on a.id=psp.product_id ";
		
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
			sql += sellSql;
			
			sql +="where "+ condition;
			if(startIndex >=0 && count > 0){
				sql += " limit " + startIndex + "," + count;
			}
			
			if(!StringUtil.isNull(orderBy)){
				sql += "  group by a.id order by " + orderBy;
			}
			pst = this.dbOp.getConn().prepareStatement(sql);
			
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
			
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
		
		//销售类型
		String sellSql=" left join product_sell_property psp on a.id=psp.product_id ";
				
		try {
			String sql = "select *,GROUP_CONCAT(distinct d.name)sName from product a  left outer join product_status b on a.status=b.id left outer join product_supplier c on a.id=c.product_id left join supplier_standard_info d on d.id=c.supplier_id and d.status=1 ";
			sql+=barcodeSql;
			sql += sellSql;
			
			sql +="where "+ condition;
			
			sql +=" group by a.id";
			
			if(!StringUtil.isNull(orderBy)){
				sql += " order by " + orderBy;
			}
			
			if(startIndex >=0 && count > 0){
				sql += " limit " + startIndex + "," + count;
			}
			
			pst = this.dbOp.getConn().prepareStatement(sql);
			
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
			
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
			pst = this.dbOp.getConn()
					.prepareStatement("update user_order set status=? where id=?");
			pst.setInt(1, status);
			pst.setInt(2, id);
			pst.executeUpdate();
			return true;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return false;
		} finally {
			DbUtil.closeStatement(pst);
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
			pst = this.dbOp.getConn()
					.prepareStatement("update user_order set status=? where status=? and buy_mode=?");
			pst.setInt(1, status);
			pst.setInt(2, oldstatus);
			pst.setInt(3, buymode);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
		}
	}
	////by hdy:根据商品编码返回商品ID，查找不到则返回0
	public int getProductIdByCode(String code){
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = this.dbOp.getConn()
					.prepareStatement("select id from product where code=?");
			pst.setString(1, code);
			rs = pst.executeQuery();
			if(rs!=null && rs.next()){
				return rs.getInt(1);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(pst);
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
			pst = this.dbOp.getConn()
					.prepareStatement("insert into user_order_product (order_id,product_id,count)values(?,?,?)");
			pst.setInt(1, orderId);
			pst.setInt(2, productId);
			pst.setInt(3, count);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
		}
	}
	
	public boolean addOrderProductSplit(int orderId, String code, int count) {
		PreparedStatement pst = null;
		try {
			int productId = getProductIdByCode(code);
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
		}

		return true;
	}

	public boolean updateOrderProductSplit(String set, int id) {
		if(StringUtil.isNull(set)){
			return false;
		}
		Statement st = null;
		try {
			st = this.dbOp.getConn().createStatement();
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
			DbUtil.closeStatement(st);
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
			pst = this.dbOp.getConn()
					.prepareStatement("delete from user_order_product where id="
							+ id);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
		}
	}

	public void deleteOrderProduct(int orderId, int productId) {
		PreparedStatement pst = null;
		try {
			pst = this.dbOp.getConn()
					.prepareStatement("delete from user_order_product where order_id="
							+ orderId + " and product_id=" + productId);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
		}
	}

	public void deleteOrderProducts(int orderId) {
		PreparedStatement pst = null;
		try {
			pst = this.dbOp.getConn().prepareStatement("delete from user_order_product where order_id=" + orderId);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
		}
	}

	public void deleteOrderProductSplit(int id) {
		PreparedStatement pst = null;
		try {
			pst = this.dbOp.getConn()
					.prepareStatement("delete from user_order_product_split where id="
							+ id);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
		}
	}

	public void deleteOrderProductsSplit(int orderId) {
		PreparedStatement pst = null;
		try {
			pst = this.dbOp.getConn().prepareStatement("delete from user_order_product_split where order_id=" + orderId);
			pst.executeUpdate();

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see adultadmin.service.IAdminService#modifyOrder(adultadmin.action.vo.voOrder)
	 */
	public boolean modifyOrder(voOrder vo) {
		boolean result=true;
		PreparedStatement pst = null;
		try {
			pst = this.dbOp.getConn()
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
			return result;
		} catch (SQLException sqle) {
			sqle.printStackTrace();
			return false;
		} finally {
			DbUtil.closeStatement(pst);
		}
	}
	public void modifyOrder(String set, String condition) {
		Statement st = null;
		try {
			st = this.dbOp.getConn().createStatement();
			String query = "update user_order set " + set + " where "
					+ condition;
			st.executeUpdate(query);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(st);
		}
	}

	public voCatalog getCatalog(String condition) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		voCatalog vo = null;
		try {
			pst = this.dbOp.getConn().prepareStatement("select * from catalog where "
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return vo;
	}

	public List getSupplierProductLineList(){
		List list=new ArrayList();
		PreparedStatement pst = null;
		ResultSet rs = null;
		try {
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}

	public void deleteOrderPresentSplit(int id) {
		PreparedStatement pst = null;
		try {
			pst = this.dbOp.getConn()
					.prepareStatement("delete from user_order_present_split where id="
							+ id);
			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
		}
	}

	public void deleteOrderPresentsSplit(int orderId) {
		PreparedStatement pst = null;
		try {
			pst = this.dbOp.getConn().prepareStatement("delete from user_order_present_split where order_id=" + orderId);
			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
		}
	}

	public boolean addOrderPresentSplit(int orderId, String productCode,
			int orderPresentCount) {
		PreparedStatement pst = null;
		try {
			int productId = getProductIdByCode(productCode);
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
		}
		return true;
	}

	public boolean updateOrderPresentSplit(String set, int id) {
		if(StringUtil.isNull(set)){
			return false;
		}
		Statement st = null;
		try {
			st = this.dbOp.getConn().createStatement();
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
			DbUtil.closeStatement(st);
		}

		return true;
	}


	public List getOrderStatusList() {
		Statement st = null;
		ResultSet rs = null;
		List list = new ArrayList();
		try {
			st = this.dbOp.getConn().createStatement();
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
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
		}
		return list;
	}
	/**
	 * 通过产品id查询产品编号
	 */
	public String getProductCode(String pid) {
		Statement st = null;
		ResultSet rs = null;
		String code = null;
		try {
			st = this.dbOp.getConn().createStatement();
			rs = st.executeQuery("select code from product where id="+pid);
			if(rs.next())           		
				code = rs.getString(1);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
		}
		return code;
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
			pst=this.dbOp.getConn().prepareStatement("select order_id from user_order_phone_index where order_id=? and phone=? ");
			pst.setInt(1, orderId);
			pst.setString(2, phone);
			rs=pst.executeQuery();
			if(rs.next()){
				flag=true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return flag;
	}

	public void addUserOrderPhoneIndex(int orderId,String name,String phone){
		PreparedStatement pst = null;
		try {
			pst = this.dbOp.getConn()
					.prepareStatement("insert into user_order_phone_index (order_id,order_name,phone) values (?,?,?)");
			pst.setInt(1, orderId);
			pst.setString(2, name);
			pst.setString(3, phone);
			pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
		}
	}

	public boolean delUserOrderPhoneIndex(int orderId,String phone){
		boolean flag = false;
		PreparedStatement pst=null;
		try {
			pst=this.dbOp.getConn().prepareStatement("delete from user_order_phone_index where order_id=? and phone=? ");
			pst.setInt(1, orderId);
			pst.setString(2, phone);
			flag=pst.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DbUtil.closeStatement(pst);
		}
		return flag;
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
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
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
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
		}

		return true;
	}

	public voOrderExtendInfo getOrderExtendInfo(int id){
		PreparedStatement pst = null;
		ResultSet rs = null;
		voOrderExtendInfo vo = null;
		try {
			pst = this.dbOp.getConn()
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return vo;
	}

	public voProduct getProduct2(int id) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		voProduct vo = null;
		try {
			pst = this.dbOp.getConn().prepareStatement("select * from product where id=?");
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
			DbUtil.closeStatement(pst);
			DbUtil.closeResultSet(rs);
		}
		return vo;
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
	public static void addOrderAdminStatusLog(IAdminLogService logService,int oriStatus,int status,int oriStockoutDeal,int stockoutDeal,OrderAdminLogBean log){
		OrderAdminStatusLogBean statusLog  = new OrderAdminStatusLogBean();
		statusLog.setCreateDatetime(log.getCreateDatetime());
		statusLog.setOrderId(log.getOrderId());
		statusLog.setUsername(log.getUsername());
		if(oriStatus >=0 && status>=0 && oriStatus != status){
			if(status==1||status==2||status==3||status==6||status==7||status==8){
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
	
	/**
	 * 作者：
	 * 
	 * 创建日期：
	 * 
	 * 说明：获取产品线相关信息
	 * 
	 */
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
			st = this.getDbOp().getConn().createStatement();
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
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
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
			st = this.getDbOp().getConn().createStatement();
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
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
		}
		return proLine;
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
			st = this.getDbOp().getConn().createStatement();
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
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
		}
		return proLineCatalog;
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
			st=this.getDbOp().getConn().createStatement();
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
			DbUtil.closeStatement(st);
		}
		return list;
	}
	public boolean pushDeliverOrder(OrderStockBean os){
		try{
			boolean result=true;
			DeliverCorpInfoBean deliverCorpInfo = os.getOrder().getDeliverInfoMapAll().get(os.getDeliver());
			String pinyin = deliverCorpInfo.getPinyin();
			String token = deliverCorpInfo.getToken();
			if(!pinyin.equals("")){
				ResultSet rs = dbOp.executeQuery("select city from province_city where id = "+os.getOrder().getOrderExtendInfo().getAddId2());
				String city = "";
				if(rs.next()){
					city = rs.getString(1);
				}
				
				String insert = "";
				if(!token.equals("")){
					insert = "insert into deliver_order(order_id,deliver_no,deliver_info,add_time) "+
							 "values("+os.getOrderId()+",'"+os.getOrder().getAuditPakcageBean().getPackageCode()+"','','"+DateUtil.getNow()+"')"; 
					
				}else{
					if(!os.getOrderCode().equals(os.getOrder().getAuditPakcageBean().getPackageCode())){
						insert = "insert into deliver_order_sent(order_id,deliver_no,deliver_pinyin,deliver_local) "+
								"values("+os.getOrderId()+",'"+os.getOrder().getAuditPakcageBean().getPackageCode()+"','"+pinyin+"','"+city+"')";
					}
				}
				
				if(!insert.equals("")){
					dbOp.executeUpdate(insert);
				}
			}
			return result;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
    *
    * 功能:在待发货状态修改了商品信息。如果是缺货。则删掉一分钟后发送出库短信  和自动申请出库
    * <p>作者 李双 Sep 14, 2012 11:31:06 AM
    * @param orderId
    */
   public static void deleteSendMsgAndAutolibrary(voOrder order){
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
	
	/**
	 * 
	 *方法描述：获取dervliver_package_code表(特定条件)的总记录数
	 *
	 *创建人：aohaichen
	 *
	 * 时间：2014-3-12
	 */
	public int getDeliverPackageCodeCount(int deliver) {
		Statement st = null;
		ResultSet rs = null;
		int count = 0;
		try {
			st = this.dbOp.getConn().createStatement();
			rs = st.executeQuery("select count(*) from deliver_package_code where deliver="+deliver+" and used=0");
			if(rs.next()){
				count = rs.getInt(1);	
			}           					
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeStatement(st);
			DbUtil.closeResultSet(rs);
		}
		return count;
	}
	
	
	/**
	 * 
	 *方法描述：获取product_stock表product_id没有库存地区、类型的记录集合并插入
	 *
	 *创建人：aohaichen
	 *
	 */
	public int addProductStockNoAreaList(DbOperation dbOp) {
		Statement st = null;
		Statement stSlave = null;
		Statement st2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		int result = 0;
		List list = new ArrayList();
		try {
			st = this.dbOp.getConn().createStatement();
			stSlave = this.dbOp.getConn().createStatement();
			rs2 = stSlave.executeQuery("select * from stock_area_type");
			st2 = dbOp.getConn().createStatement();
			while(rs2.next()){
				int stockArea = rs2.getInt("area_id");
				int stockType = rs2.getInt("type_id");
				rs = st.executeQuery("select p.id from product p where not exists (select ps.product_id from product_stock ps where ps.area = "+stockArea+" and ps.type = "+stockType+" and p.id = ps.product_id)");
				int count = 0;
				while(rs.next()){
					st2.executeUpdate("INSERT INTO `product_stock` (`product_id`, `area`, `type`, stock, lock_count, name, `status`) VALUES ("+rs.getInt("p.id")+", "+stockArea+", "+stockType+",0,0,'',0)");
					count++;
					//				ProductStockBean psb = new ProductStockBean();
					//				psb.setProductId(rs.getInt("id"));
					//				psb.setArea(rs.getInt("area_id"));
					//				psb.setType(rs.getInt("type_id"));
					//				result ++;
					//				list.add(psb);
				} 
				rs.close();
				System.out.println("area:"+stockArea+", type:"+stockType+"  处理完毕, 共处理"+count+"个");
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(st2);
			DbUtil.closeStatement(st);
			
		}
		return result;
	}
	
}
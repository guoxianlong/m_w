package mmb.rec.stat.controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.util.comparator.ComparatorDpproductStat;
import mmb.ware.WareService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ormap.ProductLineMap;

import cache.ProductLinePermissionCache;

import adultadmin.action.vo.voOrder;
import adultadmin.action.vo.voOrderProduct;
import adultadmin.action.vo.voProduct;
import adultadmin.action.vo.voProductLine;
import adultadmin.action.vo.voUser;
import adultadmin.bean.OrderTypeBean;
import adultadmin.bean.ProductPackageBean;
import adultadmin.bean.UserGroupBean;
import adultadmin.bean.buy.BuyOrderBean;
import adultadmin.bean.buy.BuyOrderProductBean;
import adultadmin.bean.buy.BuyStockBean;
import adultadmin.bean.stat.DpproductStatBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.bean.supplier.SupplierStandardInfoBean;
import adultadmin.service.IAdminService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.impl.UserServiceImpl;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductPackageService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.service.infc.IStockService;
import adultadmin.service.infc.IUserService;
import adultadmin.util.DateUtil;
import adultadmin.util.DbLock;
import adultadmin.util.Encoder;
import adultadmin.util.HttpUtil;
import adultadmin.util.StatUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;

@Controller
@RequestMapping("/admin/stat")
public class OrderStockStatProductController {

	/**
	 * 跳转到动碰商品之前准备部分信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("toTouchProductStat")
	public String toTouchProductStat(HttpServletRequest request,
			HttpServletResponse response) {
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Paragma", "no-cache");
		response.setDateHeader("Expires", 0);
		voUser adminUser = (voUser) request.getSession().getAttribute(
				"userView");
		if (adminUser == null) {
			request.setAttribute("tip", "未登录操作失败!");
			return "/admin/error";
		}
		// 数据库大查询锁，等待3秒
		if (!DbLock.slaveServerQueryLocked(100)) {
			request.setAttribute("tip", "其他人正在查询，请稍后再查！");
			return "redirect:/tip.jsp";
		}
		Connection conn = null;
		try {
			DbLock.slaveServerOperator = adminUser.getUsername() + "_动碰商品统计_"
					+ DateUtil.getNow();
			conn = DbUtil.getConnection(DbOperation.DB_SLAVE2);
			Statement st = conn.createStatement();
			ResultSet rs = null;
			rs = st.executeQuery("select id, name from supplier_standard_info where status=1 order by id");
			List<SupplierStandardInfoBean> supplierList = new ArrayList<SupplierStandardInfoBean>();
			while (rs.next()) {
				SupplierStandardInfoBean ssiBean = new SupplierStandardInfoBean();
				ssiBean.setId(rs.getInt(1));
				ssiBean.setName(rs.getString(2));
				supplierList.add(ssiBean);
			}
			rs.close();
			st.close();
			request.setAttribute("supplierList", supplierList);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbLock.slaveServerQueryLock.unlock();
			if (conn != null)
				try {
					conn.close();
				} catch (SQLException e) {
				}
		}
		return "forward:/admin/rec/stat/dongpengProduct/dpproduct.jsp";
	}

	/**
	 * 得到动碰商品信息
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("searchTouchProductStat")
	@ResponseBody
	public Map<String, Object> SearecDpproductStat(HttpServletRequest request,
			HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		resultMap.put("total", "0");
		resultMap.put("rows", resultList);
		resultMap.put("totalCount1", "0");
		resultMap.put("totalCount2", "0");
		String startDate = StringUtil.dealParam(request
				.getParameter("startDate"));// 开始时间
		if (startDate == null)
			startDate = "";
		String endDate = StringUtil.dealParam(request.getParameter("endDate"));// 结束时间
		if (endDate == null)
			endDate = "";
		int proxy = StringUtil.toInt(request.getParameter("proxy"));// 代理商
		int parentId1 = StringUtil
				.StringToId(request.getParameter("parentId1"));// 分类1
		int parentId2 = StringUtil
				.StringToId(request.getParameter("parentId2"));// 分类2
		if (startDate.equals("") && endDate.equals("") && proxy == -1
				&& parentId1 == 0 && parentId2 == 0) {
			return resultMap;
		}
		// 时间格式校验
		Date date1 = null;
		Date date2 = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if (startDate.length() > 0) {
			try {
				date1 = sdf.parse(startDate);
			} catch (Exception e) {
				resultMap.put("tip", "开始时间格式不对！");
				return resultMap;
			}
		}
		if (endDate.length() > 0) {
			try {
				date2 = sdf.parse(endDate);
			} catch (Exception e) {
				resultMap.put("tip", "结束时间格式不对！");
				return resultMap;
			}
		}
		if (date1 != null && date2 != null) {
			if (date2.before(date1)) {
				resultMap.put("tip", "结束时间必须在开始时间之后！");
				return resultMap;
			}
		}

		// 拼装查询条件
		StringBuffer condition = new StringBuffer();
		if (startDate != null) {
			condition.append(" and sc.create_datetime>='");
			condition.append(startDate);
			condition.append("'");
			request.setAttribute("startDate", startDate);
		}
		if (endDate != null) {
			condition.append(" and sc.create_datetime<='");
			condition.append(endDate);
			condition.append("'");
			request.setAttribute("endDate", endDate);
		}
		if (proxy > 0) {
			condition.append(" and d.id=" + proxy);
		}
		if (parentId1 > 0) {
			condition.append(" and p.parent_id1=" + parentId1);
			request.setAttribute("parentId1", "" + parentId1);
		}
		if (parentId2 > 0) {
			condition.append(" and p.parent_id2=" + parentId2);
			request.setAttribute("parentId2", "" + parentId2);
		}
		// 开启数据库连接
		DbOperation dbOp = new DbOperation();
		IProductStockService psService = ServiceFactory
				.createProductStockService(IBaseService.CONN_IN_SERVICE, null);
		dbOp.init("adult_slave2");
		List dpproductList = new ArrayList();
		String query = "select sc.product_id,p.code,p.oriname,sc.stock_in_count,sc.stock_out_count,sc.create_datetime,sc.stock_price,sc.card_type from stock_card sc join product p on sc.product_id=p.id"
				+ (proxy > 0 ? " join product_supplier c on p.id=c.product_id join supplier_standard_info d on d.id=c.supplier_id"
						: "")
				+ " where sc.card_type in (1,2,5)"
				+ condition
				+ " order by create_datetime desc";
		ResultSet rs = dbOp.executeQuery(query);
		try {
			HashMap dppMap = new HashMap();
			while (rs.next()) {
				int productId = rs.getInt(1);
				String productCode = rs.getString(2);
				String productName = rs.getString(3);
				int stockInCount = rs.getInt(4);
				int stockOutCount = rs.getInt(5);
				String createDatetime = rs.getString(6);
				float stockPrice = rs.getFloat(7);// 库存单价
				int cardType = rs.getInt(8);// 进销存卡片类型

				if (dppMap.get(productId + "") == null) {
					DpproductStatBean bean = new DpproductStatBean();

					bean.setProductId(productId);// 商品ID
					bean.setCode(productCode);// 编号
					bean.setName(productName);// 名称
					if (cardType == 1) {// 采购入库
						bean.setOutCount(0);// 销量
						bean.setStockinCount(stockInCount);// 采购入库量
						bean.setOutReturnCount(0);// 销量退货量
					} else if (cardType == 2) {// 销售出库
						bean.setOutCount(stockOutCount);// 销量
						bean.setStockinCount(0);// 采购入库量
						bean.setOutReturnCount(0);// 销量退货量
					} else if (cardType == 5) {// 销售退货入库
						bean.setOutCount(0);// 销量
						bean.setStockinCount(0);// 采购入库量
						bean.setOutReturnCount(stockInCount);// 销量退货量
					}
					bean.setLastTime(createDatetime);// 产品最近出货时间
					bean.setPrice5(stockPrice);
					bean.setFrequencyCount(1);
					dpproductList.add(bean);
					dppMap.put(productId + "", bean);
				} else {
					DpproductStatBean bean = (DpproductStatBean) dppMap
							.get(productId + "");
					if (cardType == 1) {// 采购入库
						bean.setStockinCount(bean.getStockinCount()
								+ stockInCount);// 采购入库量
					} else if (cardType == 2) {// 销售出库
						bean.setOutCount(bean.getOutCount() + stockOutCount);// 销量
					} else if (cardType == 5) {// 销售退货入库
						bean.setOutReturnCount(bean.getOutReturnCount()
								+ stockInCount);// 销量退货量
					}
					bean.setFrequencyCount(bean.getFrequencyCount() + 1);
				}
			}
			request.setAttribute("count", "" + dpproductList.size());
			// 关联各个库存数量
			Iterator iter = dpproductList.listIterator();
			while (iter.hasNext()) {
				DpproductStatBean dpproductStat = (DpproductStatBean) iter
						.next();
				dpproductStat.setPsList(psService.getProductStockList(
						"product_id=" + dpproductStat.getProductId(), -1, -1,
						null));
			}
			ComparatorDpproductStat comparator = new ComparatorDpproductStat();
			Collections.sort(dpproductList, comparator);
			float totalCount1 = 0;
			int totalCount2 = 0;
			for (int i = 0; i < dpproductList.size(); i++) {
				DpproductStatBean dpproduct = (DpproductStatBean) dpproductList
						.get(i);
				Map<String, String> map = new HashMap<String, String>();
				map.put("product_code", "<a href='" + request.getContextPath()
						+ "/admin/fproduct.do?id=" + dpproduct.getProductId()
						+ "' >" + dpproduct.getCode() + "</a>");
				map.put("product_name", "<a href='" + request.getContextPath()
						+ "/admin/fproduct.do?id=" + dpproduct.getProductId()
						+ "' >" + dpproduct.getName() + "</a>");
				map.put("buy_stockin_count",
						new Integer(dpproduct.getStockinCount()).toString());
				map.put("sale_count",
						new Integer(dpproduct.getOutCount()).toString());
				map.put("sale_return_count",
						new Integer(dpproduct.getOutReturnCount()).toString());
				map.put("touch_time",
						new Integer(dpproduct.getFrequencyCount()).toString());
				map.put("check_stock",
						new Integer(dpproduct.getStock(0, 1)
								+ dpproduct.getLockCount(0, 1)
								+ dpproduct.getStock(1, 1)
								+ dpproduct.getLockCount(1, 1)
								+ dpproduct.getStock(3, 1)
								+ dpproduct.getLockCount(3, 1)).toString());
				map.put("qualify_stock",
						new Integer(dpproduct.getStock(0, 0)
								+ dpproduct.getLockCount(0, 0)
								+ dpproduct.getStock(1, 0)
								+ dpproduct.getLockCount(1, 0)
								+ dpproduct.getStock(2, 0)
								+ dpproduct.getLockCount(2, 0)
								+ dpproduct.getStock(3, 0)
								+ dpproduct.getLockCount(3, 0)).toString());
				map.put("return_stock",
						new Integer(dpproduct.getStock(0, 4)
								+ dpproduct.getLockCount(0, 4)
								+ dpproduct.getStock(1, 4)
								+ dpproduct.getLockCount(1, 4)
								+ dpproduct.getStock(2, 4)
								+ dpproduct.getLockCount(2, 4)
								+ dpproduct.getStock(3, 4)
								+ dpproduct.getLockCount(3, 4)).toString());
				map.put("back_stock",
						new Integer(dpproduct.getStock(0, 3)
								+ dpproduct.getLockCount(0, 3)
								+ dpproduct.getStock(1, 3)
								+ dpproduct.getLockCount(1, 3)
								+ dpproduct.getStock(3, 3)
								+ dpproduct.getLockCount(3, 3)).toString());
				map.put("fix_stock",
						new Integer(dpproduct.getStock(0, 2)
								+ dpproduct.getLockCount(0, 2)
								+ dpproduct.getStock(1, 2)
								+ dpproduct.getLockCount(1, 2)
								+ dpproduct.getStock(3, 2)
								+ dpproduct.getLockCount(3, 2)).toString());
				map.put("bad_stock",
						new Integer(dpproduct.getStock(0, 5)
								+ dpproduct.getLockCount(0, 5)
								+ dpproduct.getStock(1, 5)
								+ dpproduct.getLockCount(1, 5)
								+ dpproduct.getStock(2, 5)
								+ dpproduct.getLockCount(2, 5)
								+ dpproduct.getStock(3, 5)
								+ dpproduct.getLockCount(3, 5)).toString());
				map.put("sample_stock",
						new Integer(dpproduct.getStock(0, 6)
								+ dpproduct.getLockCount(0, 6)
								+ dpproduct.getStock(1, 6)
								+ dpproduct.getLockCount(1, 6)
								+ dpproduct.getStock(3, 6)
								+ dpproduct.getLockCount(3, 6)).toString());
				if ((dpproduct.getStockAll() + dpproduct.getLockCountAll()) > 0) {
					totalCount2++;
				}
				totalCount1 = totalCount1
						+ (dpproduct.getStockAll() + dpproduct
								.getLockCountAll()) * dpproduct.getPrice5();
				resultList.add(map);
			}
			resultMap.put("totalCount1", new Float(totalCount1).toString());
			resultMap.put("totalCount2", new Integer(totalCount2).toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();// 释放所有资源
			psService.releaseAll();
		}
		// 按照动碰频率排序
		request.setAttribute("dpproductList", dpproductList);
		resultMap.put("total", new Integer(dpproductList.size()));
		resultMap.put("rows", resultList);
		return resultMap;
	}

	@RequestMapping("toOrderStockStatRealTime")
	public String toOrderStockStatRealTime(HttpServletRequest request,
			HttpServletResponse response) {

		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			request.setAttribute("tip", "未登录，操作失败！");
			return "/admin/error";
		}

		// 数据库大查询锁，等待3秒
		if (!DbLock.slaveServerQueryLocked(100)) {
			return "redirect:/tip.jsp?db=adult_slave";
		}

		Connection conn = null;
		Statement st = null;
		IStockService service = null;
		IProductPackageService ppService = null;
		IProductStockService psService = null;
		WareService wareService = null;
		IUserService userService = null;
		try {

			String productLine = StringUtil.convertNull(request
					.getParameter("productLine"));
			productLine = Encoder.decrypt(productLine);// 解码为中文
			if (productLine == null) {// 解码失败,表示已经为中文,则返回默认
				productLine = StringUtil.dealParam(request
						.getParameter("productLine"));// 名称
			}
			if (productLine == null) {
				productLine = "";
				
			}
			boolean markProductList = StringUtil.toBoolean(request
					.getParameter("markProductList"));

			DbLock.slaveServerOperator = user.getUsername() + "_即时发货状态统计_"
					+ DateUtil.getNow();
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB_SLAVE2);
			conn = dbOp.getConn();
			st = conn.createStatement();
			service = ServiceFactory.createStockService(
					IBaseService.CONN_IN_SERVICE, dbOp);
			ppService = ServiceFactory.createProductPackageService(
					IBaseService.CONN_IN_SERVICE, service.getDbOp());
			psService = ServiceFactory.createProductStockService(
					IBaseService.CONN_IN_SERVICE, service.getDbOp());
			wareService = new WareService(service.getDbOp());
			userService = new UserServiceImpl(IBaseService.CONN_IN_SERVICE,
					service.getDbOp());
			UserGroupBean group = user.getGroup();

			boolean padmin = group.isFlag(74);
			String date = DateUtil
					.getBackFromDate(DateUtil.getNowDateStr(), 40);
			int id = StatUtil.getDateTimeFirstOrderId(date);
			if (id <= 0) {
				id = StatUtil.getDayOrderId(date);
			}

			String sql;
			ResultSet rs;
			System.out.println("start ... " + DateUtil.getNow());
			HashMap orderTypeMap = new HashMap();
			rs = st.executeQuery("select id,name from user_order_type");
			while (rs.next()) {
				String type = String.valueOf(rs.getInt(1));
				String name = rs.getString(2);
				orderTypeMap.put(type, name);
			}

			// 成交但没有发货的总订单：成交订单中没有出库的订单（订单状态为：3,6,9,12,14、发货状态为：为处理、处理中、复核）
			sql = "select count(*) from user_order uo left outer join order_stock os on uo.id=os.order_id where uo.id > "
					+ id
					+ " and uo.status in (3,6,9,12,14) and (os.status is null or os.status in (0,1,5))";
			int noStockoutCount = 0;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				noStockoutCount = rs.getInt(1);
			}

			// 没有“申请出库”的订单：还没有操作“申请出货”的订单（订单状态：3、没有发货记录）
			sql = "select count(*) from user_order uo left outer join order_stock os on uo.id=os.order_id where uo.id > "
					+ id
					+ " and uo.status in (3,6,9,12,14) and (os.status is null)";
			int noOrderStockCount = 0;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				noOrderStockCount = rs.getInt(1);
			}

			// 能发货的待出库订单：库存满足但没有发货的订单（订单发货状态：待发货、排除复核状态的）
			sql = "select count(*) from user_order uo join order_stock os on uo.code=os.order_code where uo.id > "
					+ id
					+ " and uo.status in (3,6,9,12,14) and os.status in (1)";
			int stockReadyCount = 0;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				stockReadyCount = rs.getInt(1);
			}

			// 复核中的订单：导完订单，取完货正在复核中的订单（订单发货状态：复核）
			sql = "select count(*) from user_order uo join order_stock os on uo.code=os.order_code where uo.id > "
					+ id
					+ " and uo.status in (3,6,9,12,14) and os.status in (5)";
			int stockRecheckCount = 0;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				stockRecheckCount = rs.getInt(1);
			}

			// 计算全库 能否发货
			int noStockCount = 0;
			int hasStockCount = 0;
			int hasStockGSCount = 0;
			sql = "select * from user_order uo join user_order_status uos on uo.status=uos.id left outer join order_stock os on uo.code=os.order_code where uo.id > "
					+ id
					+ " and uo.status in (3,6,9,12,14) and (os.status is null or os.status=0) order by uo.id asc";
			rs = st.executeQuery(sql);
			List orderList = new ArrayList();
			while (rs.next()) {
				voOrder order = new voOrder();
				order.setId(rs.getInt("uo.id"));
				order.setCode(rs.getString("uo.code"));
				order.setOrderType(rs.getInt("uo.order_type"));
				orderList.add(order);
			}

			// 订单类型
			sql = "select id,name from user_order_type order by id asc";
			rs = st.executeQuery(sql);
			List orderTypeList = new ArrayList();
			while (rs.next()) {
				OrderTypeBean type = new OrderTypeBean();
				type.setId(rs.getInt("id"));
				type.setName(rs.getString("name").substring(
						0,
						rs.getString("name").indexOf("订单") == -1 ? rs
								.getString("name").length() : rs.getString(
								"name").indexOf("订单")));
				orderTypeList.add(type);
			}
			OrderTypeBean type = new OrderTypeBean();
			type.setId(9);
			type.setName("保健品及其他");
			orderTypeList.add(type);
			rs.close();
			Iterator orderIter = orderList.listIterator();
			List hasStockCountList = new ArrayList();
			List hasStockCountGSList = new ArrayList();
			List noStockCountList = new ArrayList();
			HashMap orderProductMap = new HashMap();
			HashMap orderProductGSMap = new HashMap();
			HashMap orderProductGFMap = new HashMap();
			HashMap orderProductZCMap = new HashMap();
			HashMap hasStockMap = new HashMap();
			HashMap hasStockGSMap = new HashMap();
			HashMap noStockMap = new HashMap();
			HashMap orderProductStockMap = new HashMap();
			HashMap orderCodeMap = new HashMap();
			HashMap orderIdMap = new HashMap();
			HashMap productMap = new HashMap();
			while (orderIter.hasNext()) {
				voOrder order = (voOrder) orderIter.next();

				List orderProductList = wareService.getOrderProducts(order
						.getId());
				List orderPresentList = wareService.getOrderPresents(order
						.getId());
				orderProductList.addAll(orderPresentList);
				List detailList = new ArrayList();
				Iterator detailIter = orderProductList.listIterator();
				while (detailIter.hasNext()) {
					voOrderProduct vop = (voOrderProduct) detailIter.next();
					voProduct product = wareService.getProduct(vop
							.getProductId());

					if (product.getIsPackage() == 1) { // 如果这个产品是套装
						List ppList = ppService.getProductPackageList(
								"parent_id=" + product.getId(), -1, -1, null);
						Iterator ppIter = ppList.listIterator();
						while (ppIter.hasNext()) {
							ProductPackageBean ppBean = (ProductPackageBean) ppIter
									.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount()
									* ppBean.getProductCount());
							voProduct tempProduct = wareService
									.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							tempVOP.setPsList(psService.getProductStockList(
									"product_id=" + tempVOP.getProductId(), -1,
									-1, null));
							detailList.add(tempVOP);

							if (productMap.get(tempProduct.getCode()) == null) {
								productMap.put(tempProduct.getCode(),
										tempProduct);
							}
						}
					} else {
						vop.setPsList(psService.getProductStockList(
								"product_id=" + vop.getProductId(), -1, -1,
								null));
						detailList.add(vop);

						if (productMap.get(product.getCode()) == null) {
							productMap.put(product.getCode(), product);
						}
					}
				}
				// int ss = checkStock(orderProductList);
				// 库存检验

				int result = 1;
				if (detailList != null) {
					Iterator itr = detailList.iterator();
					voOrderProduct op = null;
					while (itr.hasNext()) {
						op = (voOrderProduct) itr.next();

						// 总出货量
						if (orderProductStockMap.get(String.valueOf(op
								.getProductId())) == null) {
							orderProductStockMap.put(
									String.valueOf(op.getProductId()),
									String.valueOf(op.getCount()));
						} else {
							int count = Integer.parseInt(String
									.valueOf(orderProductStockMap.get(String
											.valueOf(op.getProductId()))));
							orderProductStockMap.put(
									String.valueOf(op.getProductId()),
									String.valueOf(count + op.getCount()));
						}

						if (orderProductMap.get(String.valueOf(op
								.getProductId())) == null) {
							orderProductMap
									.put(String.valueOf(op.getProductId()),
											String.valueOf(op
													.getStockAllType(ProductStockBean.STOCKTYPE_QUALIFIED)));
						}
						if (orderProductGSMap.get(String.valueOf(op
								.getProductId())) == null) {
							orderProductGSMap
									.put(String.valueOf(op.getProductId()),
											String.valueOf(op
													.getStock(
															ProductStockBean.AREA_GS,
															ProductStockBean.STOCKTYPE_QUALIFIED)));
						}
						if (orderProductGFMap.get(String.valueOf(op
								.getProductId())) == null) {
							orderProductGFMap
									.put(String.valueOf(op.getProductId()),
											String.valueOf(op
													.getStock(
															ProductStockBean.AREA_GF,
															ProductStockBean.STOCKTYPE_QUALIFIED)));
						}
						if (orderProductZCMap.get(String.valueOf(op
								.getProductId())) == null) {
							orderProductZCMap
									.put(String.valueOf(op.getProductId()),
											String.valueOf(op
													.getStock(
															ProductStockBean.AREA_ZC,
															ProductStockBean.STOCKTYPE_QUALIFIED)));
						}

						// 总库存
						int stockCount = Integer
								.parseInt((String) orderProductMap.get(String
										.valueOf(op.getProductId())));
						stockCount = stockCount - op.getCount();
						orderProductMap.put(String.valueOf(op.getProductId()),
								String.valueOf(stockCount));

						// 广速库存
						int stockGSCount = Integer
								.parseInt((String) orderProductGSMap.get(String
										.valueOf(op.getProductId())));
						int _stockGSCount = stockGSCount - op.getCount();
						orderProductGSMap.put(
								String.valueOf(op.getProductId()),
								String.valueOf(_stockGSCount));

						// 芳村库存
						int stockGFCount = Integer
								.parseInt((String) orderProductGFMap.get(String
										.valueOf(op.getProductId())));
						int _stockGFCount = Integer
								.parseInt((String) orderProductGFMap.get(String
										.valueOf(op.getProductId())));
						if (stockGSCount > 0 && _stockGSCount < 0) {
							stockGFCount = stockGFCount + _stockGSCount;
						} else if (stockGSCount < 0) {
							stockGFCount = stockGFCount - op.getCount();
						}
						_stockGFCount = _stockGFCount - op.getCount();
						orderProductGFMap.put(
								String.valueOf(op.getProductId()),
								String.valueOf(stockGFCount));

						// 增城库存
						int stockZCCount = Integer
								.parseInt((String) orderProductZCMap.get(String
										.valueOf(op.getProductId())));
						int _stockZCCount = stockZCCount - op.getCount();
						orderProductZCMap.put(
								String.valueOf(op.getProductId()),
								String.valueOf(_stockZCCount));

						if (_stockGSCount < 0 && _stockGFCount < 0
								&& _stockZCCount < 0) {
							if (stockCount < 0) {
								result = -1;

								// 订单编号 orderIdMap
								if (orderCodeMap.get(String.valueOf(op
										.getProductId())) == null) {
									orderCodeMap.put(
											String.valueOf(op.getProductId()),
											order.getCode());
								} else {
									String code = String.valueOf(orderCodeMap
											.get(String.valueOf(op
													.getProductId())));
									orderCodeMap.put(
											String.valueOf(op.getProductId()),
											code + "<br/>" + order.getCode());
								}

								if (orderIdMap.get(String.valueOf(op
										.getProductId())) == null) {
									orderIdMap.put(
											String.valueOf(op.getProductId()),
											String.valueOf(order.getId()));
								} else {
									String idStr = String.valueOf(orderIdMap
											.get(String.valueOf(op
													.getProductId())));
									orderIdMap.put(
											String.valueOf(op.getProductId()),
											idStr + "," + order.getId());
								}
							} else {
								result = 0;
							}
						}
					}
				}
				if (result == 1) {
					hasStockGSCount++;
					hasStockCountGSList.add(order);
					if (hasStockGSMap.get(String.valueOf(order.getOrderType())) == null) {
						hasStockGSMap.put(String.valueOf(order.getOrderType()),
								String.valueOf(1));
					} else {
						int count = Integer.parseInt((String) hasStockGSMap
								.get(String.valueOf(order.getOrderType())));
						count++;
						hasStockGSMap.put(String.valueOf(order.getOrderType()),
								String.valueOf(count));
					}
				} else if (result == 0) {
					hasStockCount++;
					hasStockCountList.add(order);

					if (hasStockMap.get(String.valueOf(order.getOrderType())) == null) {
						hasStockMap.put(String.valueOf(order.getOrderType()),
								String.valueOf(1));
					} else {
						int count = Integer.parseInt((String) hasStockMap
								.get(String.valueOf(order.getOrderType())));
						count++;
						hasStockMap.put(String.valueOf(order.getOrderType()),
								String.valueOf(count));
					}
				} else {
					noStockCount++;
					noStockCountList.add(order);

					if (noStockMap.get(String.valueOf(order.getOrderType())) == null) {
						noStockMap.put(String.valueOf(order.getOrderType()),
								String.valueOf(1));
					} else {
						int count = Integer.parseInt((String) noStockMap
								.get(String.valueOf(order.getOrderType())));
						count++;
						noStockMap.put(String.valueOf(order.getOrderType()),
								String.valueOf(count));
					}
				}
			}

			// 将查询出的信息放入的request中去
			request.setAttribute("noStockoutCount", noStockoutCount);
			request.setAttribute("noOrderStockCount", noOrderStockCount);
			request.setAttribute("hasStockGSCount", hasStockGSCount);
			request.setAttribute("hasStockCount", hasStockCount);
			request.setAttribute("noStockCount", noStockCount);
			request.setAttribute("stockReadyCount", stockReadyCount);
			request.setAttribute("stockRecheckCount", stockRecheckCount);
			// 2第二个表格的内容------------------------------------------------
			int hasStockGSTotal = 0;
			int hasStockTotal = 0;
			int noStockTotal = 0;
			int allTotal = 0;
			Iterator orderTypeIter = orderTypeList.listIterator();
			List<Map<String, String>> stockList = new ArrayList<Map<String, String>>();
			while (orderTypeIter.hasNext()) {
				Map<String, String> map = new HashMap<String, String>();
				OrderTypeBean orderType = (OrderTypeBean) orderTypeIter.next();
				int hasStockGS = 0;
				int hasStock = 0;
				int noStock = 0;

				if (hasStockGSMap.get(String.valueOf(orderType.getId())) != null) {
					hasStockGS = Integer.parseInt((String) hasStockGSMap
							.get(String.valueOf(orderType.getId())));
				}
				if (hasStockMap.get(String.valueOf(orderType.getId())) != null) {
					hasStock = Integer.parseInt((String) hasStockMap.get(String
							.valueOf(orderType.getId())));
				}
				if (noStockMap.get(String.valueOf(orderType.getId())) != null) {
					noStock = Integer.parseInt((String) noStockMap.get(String
							.valueOf(orderType.getId())));
				}

				hasStockGSTotal = hasStockGSTotal + hasStockGS;
				hasStockTotal = hasStockTotal + hasStock;
				noStockTotal = noStockTotal + noStock;
				allTotal = allTotal + (hasStockGS + hasStock + noStock);
				map.put("orderName", orderType.getName());
				map.put("hasStockGS", new Integer(hasStockGS).toString());
				map.put("hasStock", new Integer(hasStock).toString());
				map.put("noStock", new Integer(noStock).toString());
				map.put("total",
						new Integer(hasStockGS + hasStock + noStock).toString());
				stockList.add(map);
			}
			Map<String, String> totalMap = new HashMap<String, String>();
			totalMap.put("hasStockGSTotal",
					new Integer(hasStockGSTotal).toString());
			totalMap.put("hasStockTotal", new Integer(hasStockTotal).toString());
			totalMap.put("noStockTotal", new Integer(noStockTotal).toString());
			totalMap.put("allTotal", new Integer(allTotal).toString());
			request.setAttribute("stockList", stockList);
			request.setAttribute("totalMap", totalMap);
			// 2------------------------------
			st.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// 释放 大查询锁
			DbLock.slaveServerQueryLock.unlock();
			if (service != null)
				service.releaseAll();
		}
		return "forward:/admin/rec/stat/orderStockStat/orderStockStatRealTime.jsp";
	}

	/**
	 * 获得在各库中的状态
	 * 
	 * @param orderProductList
	 * @return
	 */
	int checkStock(List orderProductList) {
		if (orderProductList == null) {
			return 3;
		}
		Iterator itr = orderProductList.iterator();
		boolean zc = true;
		boolean gd = true;
		boolean gs = true;
		voOrderProduct op = null;
		while (itr.hasNext()) {
			op = (voOrderProduct) itr.next();
			if (op.getStock(ProductStockBean.AREA_ZC,
					ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				zc = false;
			}
			if (op.getStock(ProductStockBean.AREA_GF,
					ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				gd = false;
			}
			if (op.getStock(ProductStockBean.AREA_GS,
					ProductStockBean.STOCKTYPE_QUALIFIED) < op.getCount()) {
				gs = false;
			}
		}
		if (zc && gd && gs) {
			return 0;
		}
		if (zc && !gd && !gs) {
			return 1;
		}
		if (gd && !zc && !gs) {
			return 2;
		}
		if (gs && !zc && !gd) {
			return 4;
		}
		if (zc && gd && !gs) {
			return 5;
		}
		if (zc && gs && !gd) {
			return 6;
		}
		if (gd && gs && !zc) {
			return 7;
		}
		return 3;
	}

	/**
	 * 获得缺货商品列表的详情
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("getOrderStockStatDetailInfo")
	@ResponseBody
	public Map<String, Object> getOrderStockStatDetailInfo(
			HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List<Map<String, String>> resultList = new ArrayList<Map<String, String>>();
		//resultMap.put("total", "0");
		resultMap.put("rows", resultList);
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if (user == null) {
			resultMap.put("tip", "未登录，操作失败！");
			return resultMap;
		}

		// 数据库大查询锁，等待3秒
		if (!DbLock.slaveServerQueryLocked(100)) {
			resultMap.put("tip", "有人正在查询" + DbLock.slaveServerOperator);
			return resultMap;
		}
		Connection conn = null;
		Statement st = null;
		IStockService service = null;
		IProductPackageService ppService = null;
		IProductStockService psService = null;
		WareService wareService = null;
		IUserService userService = null;
		String productLine = StringUtil.convertNull(request
				.getParameter("productLine"));
		productLine = Encoder.decrypt(productLine);// 解码为中文
		if (productLine == null) {// 解码失败,表示已经为中文,则返回默认
			productLine = StringUtil.dealParam(request
					.getParameter("productLine"));// 名称
		}
		if (productLine == null)
			productLine = "";
		boolean markProductList = StringUtil.toBoolean(request
				.getParameter("markProductList"));

		DbLock.slaveServerOperator = user.getUsername() + "_即时发货状态统计_"
				+ DateUtil.getNow();
		UserGroupBean group = user.getGroup();
		boolean padmin = group.isFlag(74);
		try {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB_SLAVE2);
			conn = dbOp.getConn();
			st = conn.createStatement();
			service = ServiceFactory.createStockService(
					IBaseService.CONN_IN_SERVICE, dbOp);
			ppService = ServiceFactory.createProductPackageService(
					IBaseService.CONN_IN_SERVICE, service.getDbOp());
			psService = ServiceFactory.createProductStockService(
					IBaseService.CONN_IN_SERVICE, service.getDbOp());
			wareService = new WareService(service.getDbOp());
			userService = new UserServiceImpl(IBaseService.CONN_IN_SERVICE,
					service.getDbOp());
			String date = DateUtil
					.getBackFromDate(DateUtil.getNowDateStr(), 40);
			int id = StatUtil.getDateTimeFirstOrderId(date);
			if (id <= 0) {
				id = StatUtil.getDayOrderId(date);
			}

			String sql;
			ResultSet rs;
			System.out.println("start ... " + DateUtil.getNow());
			HashMap orderTypeMap = new HashMap();
			rs = st.executeQuery("select id,name from user_order_type");
			while (rs.next()) {
				String type = String.valueOf(rs.getInt(1));
				String name = rs.getString(2);
				orderTypeMap.put(type, name);
			}

			// 成交但没有发货的总订单：成交订单中没有出库的订单（订单状态为：3,6,9,12,14、发货状态为：为处理、处理中、复核）
			sql = "select count(*) from user_order uo left outer join order_stock os on uo.id=os.order_id where uo.id > "
					+ id
					+ " and uo.status in (3,6,9,12,14) and (os.status is null or os.status in (0,1,5))";
			int noStockoutCount = 0;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				noStockoutCount = rs.getInt(1);
			}

			// 没有“申请出库”的订单：还没有操作“申请出货”的订单（订单状态：3、没有发货记录）
			sql = "select count(*) from user_order uo left outer join order_stock os on uo.id=os.order_id where uo.id > "
					+ id
					+ " and uo.status in (3,6,9,12,14) and (os.status is null)";
			int noOrderStockCount = 0;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				noOrderStockCount = rs.getInt(1);
			}

			// 能发货的待出库订单：库存满足但没有发货的订单（订单发货状态：待发货、排除复核状态的）
			sql = "select count(*) from user_order uo join order_stock os on uo.code=os.order_code where uo.id > "
					+ id
					+ " and uo.status in (3,6,9,12,14) and os.status in (1)";
			int stockReadyCount = 0;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				stockReadyCount = rs.getInt(1);
			}

			// 复核中的订单：导完订单，取完货正在复核中的订单（订单发货状态：复核）
			sql = "select count(*) from user_order uo join order_stock os on uo.code=os.order_code where uo.id > "
					+ id
					+ " and uo.status in (3,6,9,12,14) and os.status in (5)";
			int stockRecheckCount = 0;
			rs = st.executeQuery(sql);
			if (rs.next()) {
				stockRecheckCount = rs.getInt(1);
			}

			// 计算全库 能否发货
			int noStockCount = 0;
			int hasStockCount = 0;
			int hasStockGSCount = 0;
			sql = "select * from user_order uo join user_order_status uos on uo.status=uos.id left outer join order_stock os on uo.code=os.order_code where uo.id > "
					+ id
					+ " and uo.status in (3,6,9,12,14) and (os.status is null or os.status=0) order by uo.id asc";
			rs = st.executeQuery(sql);
			List orderList = new ArrayList();
			while (rs.next()) {
				voOrder order = new voOrder();
				order.setId(rs.getInt("uo.id"));
				order.setCode(rs.getString("uo.code"));
				order.setOrderType(rs.getInt("uo.order_type"));
				orderList.add(order);
			}

			// 订单类型
			sql = "select id,name from user_order_type order by id asc";
			rs = st.executeQuery(sql);
			List orderTypeList = new ArrayList();
			while (rs.next()) {
				OrderTypeBean type = new OrderTypeBean();
				type.setId(rs.getInt("id"));
				type.setName(rs.getString("name").substring(
						0,
						rs.getString("name").indexOf("订单") == -1 ? rs
								.getString("name").length() : rs.getString(
								"name").indexOf("订单")));
				orderTypeList.add(type);
			}
			OrderTypeBean type = new OrderTypeBean();
			type.setId(9);
			type.setName("保健品及其他");
			orderTypeList.add(type);
			rs.close();
			Iterator orderIter = orderList.listIterator();
			List hasStockCountList = new ArrayList();
			List hasStockCountGSList = new ArrayList();
			List noStockCountList = new ArrayList();
			HashMap orderProductMap = new HashMap();
			HashMap orderProductGSMap = new HashMap();
			HashMap orderProductGFMap = new HashMap();
			HashMap orderProductZCMap = new HashMap();
			HashMap hasStockMap = new HashMap();
			HashMap hasStockGSMap = new HashMap();
			HashMap noStockMap = new HashMap();
			HashMap orderProductStockMap = new HashMap();
			HashMap orderCodeMap = new HashMap();
			HashMap orderIdMap = new HashMap();
			HashMap productMap = new HashMap();
			while (orderIter.hasNext()) {
				voOrder order = (voOrder) orderIter.next();

				List orderProductList = wareService.getOrderProducts(order
						.getId());
				List orderPresentList = wareService.getOrderPresents(order
						.getId());
				orderProductList.addAll(orderPresentList);
				List detailList = new ArrayList();
				Iterator detailIter = orderProductList.listIterator();
				while (detailIter.hasNext()) {
					voOrderProduct vop = (voOrderProduct) detailIter.next();
					voProduct product = wareService.getProduct(vop
							.getProductId());

					if (product.getIsPackage() == 1) { // 如果这个产品是套装
						List ppList = ppService.getProductPackageList(
								"parent_id=" + product.getId(), -1, -1, null);
						Iterator ppIter = ppList.listIterator();
						while (ppIter.hasNext()) {
							ProductPackageBean ppBean = (ProductPackageBean) ppIter
									.next();
							voOrderProduct tempVOP = new voOrderProduct();
							tempVOP.setCount(vop.getCount()
									* ppBean.getProductCount());
							voProduct tempProduct = wareService
									.getProduct(ppBean.getProductId());
							tempVOP.setProductId(ppBean.getProductId());
							tempVOP.setCode(tempProduct.getCode());
							tempVOP.setName(tempProduct.getName());
							tempVOP.setPrice(tempProduct.getPrice());
							tempVOP.setOriname(tempProduct.getOriname());
							tempVOP.setPsList(psService.getProductStockList(
									"product_id=" + tempVOP.getProductId(), -1,
									-1, null));
							detailList.add(tempVOP);

							if (productMap.get(tempProduct.getCode()) == null) {
								productMap.put(tempProduct.getCode(),
										tempProduct);
							}
						}
					} else {
						vop.setPsList(psService.getProductStockList(
								"product_id=" + vop.getProductId(), -1, -1,
								null));
						detailList.add(vop);

						if (productMap.get(product.getCode()) == null) {
							productMap.put(product.getCode(), product);
						}
					}
				}
				// int ss = checkStock(orderProductList);
				// 库存检验

				int result = 1;
				if (detailList != null) {
					Iterator itr = detailList.iterator();
					voOrderProduct op = null;
					while (itr.hasNext()) {
						op = (voOrderProduct) itr.next();

						// 总出货量
						if (orderProductStockMap.get(String.valueOf(op
								.getProductId())) == null) {
							orderProductStockMap.put(
									String.valueOf(op.getProductId()),
									String.valueOf(op.getCount()));
						} else {
							int count = Integer.parseInt(String
									.valueOf(orderProductStockMap.get(String
											.valueOf(op.getProductId()))));
							orderProductStockMap.put(
									String.valueOf(op.getProductId()),
									String.valueOf(count + op.getCount()));
						}

						if (orderProductMap.get(String.valueOf(op
								.getProductId())) == null) {
							orderProductMap
									.put(String.valueOf(op.getProductId()),
											String.valueOf(op
													.getStockAllType(ProductStockBean.STOCKTYPE_QUALIFIED)));
						}
						if (orderProductGSMap.get(String.valueOf(op
								.getProductId())) == null) {
							orderProductGSMap
									.put(String.valueOf(op.getProductId()),
											String.valueOf(op
													.getStock(
															ProductStockBean.AREA_GS,
															ProductStockBean.STOCKTYPE_QUALIFIED)));
						}
						if (orderProductGFMap.get(String.valueOf(op
								.getProductId())) == null) {
							orderProductGFMap
									.put(String.valueOf(op.getProductId()),
											String.valueOf(op
													.getStock(
															ProductStockBean.AREA_GF,
															ProductStockBean.STOCKTYPE_QUALIFIED)));
						}
						if (orderProductZCMap.get(String.valueOf(op
								.getProductId())) == null) {
							orderProductZCMap
									.put(String.valueOf(op.getProductId()),
											String.valueOf(op
													.getStock(
															ProductStockBean.AREA_ZC,
															ProductStockBean.STOCKTYPE_QUALIFIED)));
						}

						// 总库存
						int stockCount = Integer
								.parseInt((String) orderProductMap.get(String
										.valueOf(op.getProductId())));
						stockCount = stockCount - op.getCount();
						orderProductMap.put(String.valueOf(op.getProductId()),
								String.valueOf(stockCount));

						// 广速库存
						int stockGSCount = Integer
								.parseInt((String) orderProductGSMap.get(String
										.valueOf(op.getProductId())));
						int _stockGSCount = stockGSCount - op.getCount();
						orderProductGSMap.put(
								String.valueOf(op.getProductId()),
								String.valueOf(_stockGSCount));

						// 芳村库存
						int stockGFCount = Integer
								.parseInt((String) orderProductGFMap.get(String
										.valueOf(op.getProductId())));
						int _stockGFCount = Integer
								.parseInt((String) orderProductGFMap.get(String
										.valueOf(op.getProductId())));
						if (stockGSCount > 0 && _stockGSCount < 0) {
							stockGFCount = stockGFCount + _stockGSCount;
						} else if (stockGSCount < 0) {
							stockGFCount = stockGFCount - op.getCount();
						}
						_stockGFCount = _stockGFCount - op.getCount();
						orderProductGFMap.put(
								String.valueOf(op.getProductId()),
								String.valueOf(stockGFCount));

						// 增城库存
						int stockZCCount = Integer
								.parseInt((String) orderProductZCMap.get(String
										.valueOf(op.getProductId())));
						int _stockZCCount = stockZCCount - op.getCount();
						orderProductZCMap.put(
								String.valueOf(op.getProductId()),
								String.valueOf(_stockZCCount));

						if (_stockGSCount < 0 && _stockGFCount < 0
								&& _stockZCCount < 0) {
							if (stockCount < 0) {
								result = -1;

								// 订单编号 orderIdMap
								if (orderCodeMap.get(String.valueOf(op
										.getProductId())) == null) {
									orderCodeMap.put(
											String.valueOf(op.getProductId()),
											order.getCode());
								} else {
									String code = String.valueOf(orderCodeMap
											.get(String.valueOf(op
													.getProductId())));
									orderCodeMap.put(
											String.valueOf(op.getProductId()),
											code + "<br/>" + order.getCode());
								}

								if (orderIdMap.get(String.valueOf(op
										.getProductId())) == null) {
									orderIdMap.put(
											String.valueOf(op.getProductId()),
											String.valueOf(order.getId()));
								} else {
									String idStr = String.valueOf(orderIdMap
											.get(String.valueOf(op
													.getProductId())));
									orderIdMap.put(
											String.valueOf(op.getProductId()),
											idStr + "," + order.getId());
								}
							} else {
								result = 0;
							}
						}
					}
				}
				if (result == 1) {
					hasStockGSCount++;
					hasStockCountGSList.add(order);
					if (hasStockGSMap.get(String.valueOf(order.getOrderType())) == null) {
						hasStockGSMap.put(String.valueOf(order.getOrderType()),
								String.valueOf(1));
					} else {
						int count = Integer.parseInt((String) hasStockGSMap
								.get(String.valueOf(order.getOrderType())));
						count++;
						hasStockGSMap.put(String.valueOf(order.getOrderType()),
								String.valueOf(count));
					}
				} else if (result == 0) {
					hasStockCount++;
					hasStockCountList.add(order);

					if (hasStockMap.get(String.valueOf(order.getOrderType())) == null) {
						hasStockMap.put(String.valueOf(order.getOrderType()),
								String.valueOf(1));
					} else {
						int count = Integer.parseInt((String) hasStockMap
								.get(String.valueOf(order.getOrderType())));
						count++;
						hasStockMap.put(String.valueOf(order.getOrderType()),
								String.valueOf(count));
					}
				} else {
					noStockCount++;
					noStockCountList.add(order);

					if (noStockMap.get(String.valueOf(order.getOrderType())) == null) {
						noStockMap.put(String.valueOf(order.getOrderType()),
								String.valueOf(1));
					} else {
						int count = Integer.parseInt((String) noStockMap
								.get(String.valueOf(order.getOrderType())));
						count++;
						noStockMap.put(String.valueOf(order.getOrderType()),
								String.valueOf(count));
					}
				}
			}
			List productList = new ArrayList(productMap.entrySet());
			for (int i = 0; i < productList.size(); i++) {
				Map.Entry entry = (Map.Entry) productList.get(i);
				voProduct product = (voProduct) entry.getValue();

				// 产品类别
				String productType = null;
				productType = (String) ProductLineMap.getProductLineMap().get(
						Integer.valueOf(product.getParentId1()));
				if (productType == null) {
					productType = (String) ProductLineMap.getProductLineMap()
							.get(Integer.valueOf(product.getParentId2()));
				}
				if (productType == null) {
					productType = "无";
				}

				// 产品线

				product.setProductLineName(productType);
			}

			// 根据缺货量排序
			final HashMap tempMap = orderProductMap;
			Collections.sort(productList, new Comparator() {
				public int compare(Object o1, Object o2) {

					Map.Entry obj1 = (Map.Entry) o1;
					voProduct p1 = (voProduct) obj1.getValue();
					Map.Entry obj2 = (Map.Entry) o2;
					voProduct p2 = (voProduct) obj2.getValue();
					int c1 = Integer.parseInt(String.valueOf(tempMap.get(String
							.valueOf(p1.getId()))));
					int c2 = Integer.parseInt(String.valueOf(tempMap.get(String
							.valueOf(p2.getId()))));
					if (c1 > c2) {
						return 1;
					} else {
						return -1;
					}
				}
			});
			// 产品线筛选
			if (!productLine.equals("")) {
				List productListTemp = new ArrayList();
				for (int i = 0; i < productList.size(); i++) {
					Map.Entry entry = (Map.Entry) productList.get(i);
					voProduct product = (voProduct) entry.getValue();

					if (product.getProductLineName().equals(productLine)) {
						productListTemp.add(entry);
					}
				}
				productList = productListTemp;
			}

			Iterator productIter = productList.iterator();
			int index = 0;
			while (productIter.hasNext()) {

				Map.Entry entry = (Map.Entry) productIter.next();
				voProduct product = (voProduct) entry.getValue();
				product.setPsList(psService.getProductStockList("product_id = "
						+ product.getId(), -1, -1, null));

				if (Integer.parseInt(String.valueOf(orderProductMap.get(String
						.valueOf(product.getId())))) >= 0) {
					continue;
				}

				// 在途量
				int buyCountGD = 0;
				String condition = "product_id="
						+ product.getId()
						+ " and buy_order_id in (select id from buy_order where "
						+ "status = " + BuyOrderBean.STATUS3 + " or status ="
						+ BuyOrderBean.STATUS5 + ")";
				ArrayList bopList = service.getBuyOrderProductList(condition,
						-1, -1, null);
				Iterator bopIterator = bopList.listIterator();
				while (bopIterator.hasNext()) {
					BuyOrderProductBean bop = (BuyOrderProductBean) bopIterator
							.next();
					buyCountGD += (bop.getOrderCountGD()
							- bop.getStockinCountGD() - bop.getStockinCountBJ()) > 0 ? (bop
							.getOrderCountGD() - bop.getStockinCountGD() - bop
							.getStockinCountBJ()) : 0;
				}

				// 如果在途量大于0，需要查找 该采购产品的 预计到货时间 及 采购负责人
				String expectArrivalDatetime = "";
				String createUserName = "";
				if (buyCountGD > 0) {
					StringBuilder sb = new StringBuilder();
					for (int i = 0; i < bopList.size(); i++) {
						BuyOrderProductBean bop = (BuyOrderProductBean) (bopList
								.get(i));
						sb.append(bop.getBuyOrderId() + ",");
					}
					String buyOrderIds = null;
					if (sb.length() > 0) {
						buyOrderIds = sb.substring(0, sb.length() - 1);
					}
					if (buyOrderIds != null && buyOrderIds.trim().length() > 0) {
						BuyStockBean buyStock = service
								.getBuyStock(" buy_order_id in ("
										+ buyOrderIds
										+ ") and expect_arrival_datetime >= '"
										+ DateUtil.getNow()
										+ "' and status != "
										+ BuyStockBean.STATUS8
										+ " and status != "
										+ BuyStockBean.STATUS6
										+ " order by expect_arrival_datetime asc limit 0,1 ");
						if (buyStock == null) {
							buyStock = service
									.getBuyStock(" buy_order_id in ("
											+ buyOrderIds
											+ ") and status != "
											+ BuyStockBean.STATUS8
											+ " and status != "
											+ BuyStockBean.STATUS6
											+ " order by expect_arrival_datetime desc limit 0,1 ");
						}
						if (buyStock != null) {
							expectArrivalDatetime = buyStock
									.getExpectArrivalDatetime();
							if (expectArrivalDatetime != null
									&& expectArrivalDatetime.trim().length() > 0) {
								expectArrivalDatetime = expectArrivalDatetime
										.substring(0, 10);
							} else {
								expectArrivalDatetime = "";
							}
							// 操作人
							voUser creatUser = userService.getAdminUser("id = "
									+ buyStock.getCreateUserId());
							if (creatUser != null) {
								createUserName = creatUser.getUsername();
							}
						}
					}
				}
				index++;

				// 缺货处理意见
				String lackRemark = "";
				String remarks = product.getRemark();
				if (remarks != null) {
					String[] remarksArr = remarks.split("\r\n");
					lackRemark = remarksArr[0];
				}
				if (lackRemark.indexOf(".  ") > 0) {
					lackRemark = lackRemark.substring(
							lackRemark.indexOf(".  ") + 3, lackRemark.length());
				}
				Map<String, String> map = new HashMap<String, String>();
				map.put("check_info",
						"<input type='checkbox' name='productId' value='"
								+ product.getId() + "'/>"+index);
				map.put("product_line", product.getProductLineName());
				String product_name = "<a href='../fproduct.do?id="
						+ product.getId() + "'>" + product.getName() + "</a>";
				if (padmin && product.getStatus() < 100) {
					product_name += "<a href='#' onclick='return pstatus(this,"
							+ product.getId() + ",\'"
							+ product.getName().replace("'", "‘")
							+ "\')' style='color:red'>下架</a>";
				}
				map.put("product_name", product_name);
				map.put("lack_suggest", lackRemark);
				map.put("product_oriname", product.getOriname());
				map.put("product_code", product.getCode());
				map.put("GS_sum", (String) orderProductStockMap.get(String
						.valueOf(product.getId())));
				int c = Integer.parseInt(String.valueOf(orderProductMap
						.get(String.valueOf(product.getId()))));
				if (c >= 0) {
					c = 0;
				} else {
					c = Math.abs(c);
				}
				map.put("GS_lack_sum", new Integer(c).toString());
				map.put("ZC_qualify_avail",
						product.getStock(ProductStockBean.AREA_ZC,
								ProductStockBean.STOCKTYPE_QUALIFIED)
								+ "("
								+ product.getLockCount(
										ProductStockBean.AREA_ZC,
										ProductStockBean.STOCKTYPE_QUALIFIED)
								+ ")");
				map.put("ZC_check_avail",
						product.getStock(ProductStockBean.AREA_ZC,
								ProductStockBean.STOCKTYPE_CHECK)
								+ "("
								+ product.getLockCount(
										ProductStockBean.AREA_ZC,
										ProductStockBean.STOCKTYPE_CHECK) + ")");
				map.put("buy_stock_time", expectArrivalDatetime);
				map.put("buy_charge_user", createUserName);
				map.put("on_the_way_sum", new Integer(buyCountGD).toString());
				map.put("order", HttpUtil.getOrderDetailsHref(
						orderCodeMap.get(String.valueOf(product.getId())),
						orderIdMap.get(String.valueOf(product.getId()))));
				resultList.add(map);
			}
			resultMap.put("rows", resultList);
			//resultMap.put("total", productList.size());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DbLock.slaveServerQueryLock.unlock();
			if (service != null)
				service.releaseAll();
		}
		return resultMap;
	}

}

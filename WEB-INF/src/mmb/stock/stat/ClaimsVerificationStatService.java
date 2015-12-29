package mmb.stock.stat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.util.Assert;

import adultadmin.action.vo.voOrderProduct;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.Arith;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class ClaimsVerificationStatService   extends BaseServiceImpl {

	public ClaimsVerificationStatService(int useConnType, DbOperation dbOp) {
		this.useConnType = useConnType;
		this.dbOp = dbOp;
	}

	public ClaimsVerificationStatService() {
		this.useConnType = CONN_IN_SERVICE;
	}
	
	public Map<Integer, List<voOrderProduct>> getProAvgePrice(List<voOrderProduct> list) {
		// key为SKU id,value为商品均价（一个SKU可以对应多个均价）
		Map<Integer, List<voOrderProduct>> priceMap = Collections.emptyMap();
		int len = list.size();
		if (len > 0) {
			// 按出库时间排序
			Collections.sort(list, new Comparator<voOrderProduct>() {
				@Override
				public int compare(voOrderProduct o1, voOrderProduct o2) {
					boolean isComplete = true;
					Date date1 = null;
					Date date2 = null;
					try {
						date1 = DateUtils.parseDate(o1.getBjStockin(), new String[] { "yyyy-MM-dd" });
						date2 = DateUtils.parseDate(o2.getBjStockin(), new String[] { "yyyy-MM-dd" });
					} catch (ParseException e) {
						isComplete = false;
					}
					if (isComplete) {
						return date1.compareTo(date2);
					} else {
						return -1;
					}
				}
			});

			priceMap = new HashMap<Integer, List<voOrderProduct>>();
			// 获取出库的开始时间和截至时间
			String startDate = DateUtil.getBackFromDate(list.get(0).getBjStockin(), 3);
			String endDate = list.get(len - 1).getBjStockin();
			List<Object[]> paramAndTypeMap = new LinkedList<Object[]>();
			for (int i = 0; i < len; i++) {
				voOrderProduct vp = list.get(i);
				paramAndTypeMap.add(new Object[] { vp.getId(), Types.INTEGER });
				priceMap.put(vp.getId(), new ArrayList<voOrderProduct>(5));
			}

			addPreParamAndType(paramAndTypeMap, new Object[] { startDate, endDate }, Types.VARCHAR);

			StringBuilder sqlBuilder = new StringBuilder(
					"select product_id,log_date,sum((stock + lock_count) * average_price) / sum(stock + lock_count) average_price from	product_stock_log where stock_type = 0 ");
			sqlBuilder.append(getWildCard(" and product_id ", len)).append(" and log_date >=? and log_date <= ? and average_price != 0 group by product_id,log_date");
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				Integer[] types = (Integer[]) values(paramAndTypeMap, Integer.class, 1);
				Object[] values = values(paramAndTypeMap, Object.class, 0);
				dbOp.prepareStatement(sqlBuilder.toString());
				ps = setPrepareParam(dbOp.getPStmt(), types, values);
				rs = ps.executeQuery();
				while (rs.next()) {
					voOrderProduct vp = new voOrderProduct();
					//库存均价
					vp.setPrice(Arith.round(rs.getFloat("average_price"), 2));
					//出库时间
					vp.setBjStockin(DateFormatUtils.format(rs.getDate("log_date"), "yyyy-MM-dd"));
					priceMap.get(rs.getInt("product_id")).add(vp);
				}
			} catch (SQLException e) {
				throw new RuntimeException(e);
			} finally {
				try {
					if (ps != null) {
						ps.close();
					}
				} catch (SQLException e) {
					e.printStackTrace();
				} finally {
					try {
						if (rs != null)
							rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return priceMap;
	}
	
	protected Object[] values(List<Object[]> list, Class<?> type, int index) {
		if (index < 0 || index > 1) {
			throw new IllegalArgumentException();
		}
		int size = list.size();
		Object[] objects = null;
		if (type.equals(Integer.class)) {
			objects = new Integer[list.size()];
			for (int i = 0; i < size; i++) {
				objects[i] = (Integer) list.get(i)[index];
			}
		} else {
			objects = new Object[list.size()];
			for (int i = 0; i < size; i++) {
				objects[i] = list.get(i)[index];
			}
		}
		return objects;
	}
	
	// 拼接通配符
		protected String getWildCard(String columnName, int length) {
			if (length > 0) {
				StringBuilder wildCar = new StringBuilder(" ");
				wildCar.append(columnName).append("in (");
				for (int i = 0; i < length; i++) {
					wildCar.append("?,");
				}
				wildCar.deleteCharAt(wildCar.length() - 1).append(")");
				return wildCar.toString();
			}
			return "";
		}
		
		// prepareStatement设置参数
		protected PreparedStatement setPrepareParam(PreparedStatement pst, Integer[] types, Object[] params) throws SQLException {
			if (ArrayUtils.isNotEmpty(types) && ArrayUtils.isNotEmpty(params)) {
				if (types.length != params.length) {
					throw new IllegalArgumentException("参数错误!");
				}
				int len = types.length;
				for (int i = 0; i < len; i++) {
					int type = types[i];
					Object value = params[i];
					if (type == Types.INTEGER) {
						pst.setInt(i + 1, Integer.parseInt(value.toString()));
					} else if (type == Types.FLOAT) {
						pst.setFloat(i + 1, Float.parseFloat(value.toString()));
					} else if (type == Types.DOUBLE) {
						pst.setDouble(i + 1, Double.parseDouble(value.toString()));
					} else if (type == Types.VARCHAR) {
						pst.setString(i + 1, value.toString());
					} else if (type == Types.DATE) {
						pst.setDate(i + 1, new java.sql.Date(((java.util.Date) value).getTime()));
					}
				}
			}
			return pst;
		}
	
		//设置参数和参数类型,每次只能存一种类型的参数
		protected List<Object[]> addPreParamAndType(List<Object[]> list, Object[] param, int type) {
			Assert.notNull(list);
			int len = param.length;
			for (int i = 0; i < len; i++) {
				Object obj = param[i];
				if (obj != null && (!obj.equals(""))) {
					Object[] arr = new Object[] { obj, type };
					list.add(arr);
				}
			}
			return list;
		}
}

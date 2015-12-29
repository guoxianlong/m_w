package adultadmin.action.stock;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 
 * 作者：李宁
 * 
 * 创建日期：2011-02-22
 * 
 * 说明：内部使用，添加商品库存
 */
public class AddProductStock {

	private static byte[] addProStockinLock = new byte[0];

	/**
	 * 作者：李宁
	 * 
	 * 创建日期：2011-02-22
	 * 
	 * 说明：添加商品库存
	 * 
	 * @param request
	 * @param response
	 */
	public void AddProductStocks(HttpServletRequest request,
			HttpServletResponse response) {
		String method = StringUtil.convertNull(request.getParameter("method"));
		if (method.equalsIgnoreCase("Add")) {
			String code = StringUtil.convertNull(request.getParameter("code"))
					.trim();// 产品编号
			int area = StringUtil.StringToId(request.getParameter("area"));// 库区域值
			int type = StringUtil.StringToId(request.getParameter("type"));// 库类别值
			synchronized (addProStockinLock) {
				DbOperation dbOp = new DbOperation();
				List codeList = new ArrayList();
				StringBuffer insertSql = new StringBuffer();
				dbOp.init();
				try {
					if (StringUtil.isNull(code)) {
						StringBuffer seleSql = new StringBuffer();// 查询复核条件的商品信息SQL
						seleSql.append("select p.id, p.code from product p where not exists ");
						seleSql.append("(select ps.product_id from product_stock ps where ");
						seleSql.append("ps.area = ? and ps.type = ? and p.id = ps.product_id)");
						dbOp.prepareStatement(seleSql.toString());
						PreparedStatement ps = dbOp.getPStmt();
						ps.setInt(1, area);
						ps.setInt(2, type);
						ResultSet rs = ps.executeQuery();
						insertSql
								.append("insert into product_stock (product_id, stock, ");
						insertSql
								.append("lock_count, area, type, status) values (?, ?, ?, ?, ?, ?)");
						dbOp.prepareStatement(insertSql.toString());
						PreparedStatement psAdd = dbOp.getPStmt();
						while (rs.next()) {
							psAdd.setInt(1, rs.getInt(1));
							psAdd.setInt(2, 0);
							psAdd.setInt(3, 0);
							psAdd.setInt(4, area);
							psAdd.setInt(5, type);
							psAdd.setInt(6, 0);
							psAdd.addBatch();
							codeList.add(rs.getString(2));
						}
						psAdd.executeBatch();
					} else {
						code = code.replace("，", ",");
						String isExist = "select p.code from product_stock ps,product p "
								+ "where p.id = ps.product_id and p.code in ("
								+ code + ") and area = ? and type = ?";// 查询已经存在的code
						dbOp.prepareStatement(isExist);
						PreparedStatement ps = dbOp.getPStmt();
						ps.setInt(1, area);
						ps.setInt(2, type);
						ResultSet rs = ps.executeQuery();
						code = code + ",";
						StringBuffer codeBuffer = new StringBuffer(code);
						while (rs.next()) {
							String rsCode = rs.getString(1) + ",";
							int i = codeBuffer.indexOf(rsCode);
							codeBuffer.delete(i, i + rsCode.length());// 把已经存在的code在字符串中删除
						}
						if (codeBuffer.length() > 0) {
							code = codeBuffer.toString().substring(0,
									codeBuffer.toString().length() - 1);
							insertSql
									.append("insert into product_stock (product_id, stock, ");
							insertSql
									.append("lock_count, area, type, status) ");
							insertSql
									.append("select id, 0, 0, ?, ?, 0 from product ");
							insertSql.append("where code in (").append(code)
									.append(")");
							dbOp.prepareStatement(insertSql.toString());
							PreparedStatement psAdd = dbOp.getPStmt();
							psAdd.setInt(1, area);
							psAdd.setInt(2, type);
							psAdd.executeUpdate();
							String[] codeArray = code.split(",");
							codeList = Arrays.asList(codeArray);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					dbOp.release();
				}
				request.setAttribute("CodeList", codeList);
				request.setAttribute("area", String.valueOf(area));
				request.setAttribute("type", String.valueOf(type));
			}
		}
	}
}

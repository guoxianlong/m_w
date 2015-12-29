package adultadmin.action.stat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.ware.WareService;
import adultadmin.action.vo.voProduct;
import adultadmin.bean.stat.NoDpproductStatBean;
import adultadmin.framework.BaseAction;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IProductStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;

public class SearecNoDpproductAction extends BaseAction {
	public void searchNoDpproductStat(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		boolean search = StringUtil.toBoolean(request
				.getParameter("search"));
		
		int lastOutDaysAgo = StringUtil.StringToId(request
				.getParameter("lastOutDaysAgo"));// 距离上次出货时间几天
		int proxy = StringUtil.toInt(request.getParameter("proxy"));// 代理商
		int parentId1 = StringUtil
				.StringToId(request.getParameter("parentId1"));// 分类1
		int parentId2 = StringUtil
				.StringToId(request.getParameter("parentId2"));// 分类2
		int status = StringUtil.toInt(request.getParameter("status"));// 状态
		int lastDaysAgo = StringUtil.StringToId(request
				.getParameter("lastDaysAgo"));         //近期n天
		int stockDateLong = StringUtil.StringToId(request
				.getParameter("stockDateLong"));       //库存天数
		int stockType = StringUtil.toInt(request.getParameter("stockType"));
		String statDate = DateUtil.getBackFromDate(DateUtil.getNowDateStr(), 1);
		
		//判断
		if(parentId1 <= 0 && search){
			request.setAttribute("tip", "为了保证查询效率，必须选择一个一级分类进行查询");
			request.setAttribute("result", "failure");
			return;
		}
		if(lastOutDaysAgo < 0 || lastDaysAgo < 0 || stockDateLong < 0){
			request.setAttribute("tip", "天数不能小于0!");
			request.setAttribute("result", "failure");
			return;
		}
		if(lastOutDaysAgo > 0 && lastDaysAgo > 0 && lastOutDaysAgo > lastDaysAgo){
			request.setAttribute("tip", "输入的距上次出货时长，不得大于近期天数!");
			request.setAttribute("result", "failure");
			return;
		}
		if(stockDateLong > 0 && lastDaysAgo == 0){
			request.setAttribute("tip", "根据库存天数查询，必须设置近期天数!");
			request.setAttribute("result", "failure");
			return;
		}
		
		// 拼装查询条件
		StringBuilder condition = new StringBuilder();
		
		//new
		String query = "select sum(h.total_sales) tsa, sum(h.total_returns) tr, sum(h.total_stock) ts, p.id, p.code, p.name, p.oriname, p.price5," +
				" c1.name, c2.name from historical_statistics h join shop.product p on h.product_id = p.id join shop.catalog c1 on p.parent_id1 = c1.id join shop.catalog c2 on p.parent_id2 = c2.id" +
				" where h.stat_datetime = '"+statDate+"' and h.product_id in (select p.id from shop.product p join shop.catalog c1 on c1.id = p.parent_id1 join " +
				" shop.catalog c2 on c2.id = p.parent_id2 join shop.product_supplier c on p.id=c.product_id join " +
				" shop.supplier_standard_info d on d.id=c.supplier_id";
		if(proxy > 0){
			if(condition.length()>0){
				condition.append(" and ");
			}
			condition.append("d.id=");
			condition.append(proxy);
		}
		if(parentId1 > 0){
			if(condition.length()>0){
				condition.append(" and ");
			}
			condition.append("p.parent_id1=");
			condition.append(parentId1);
		}
		if(parentId2 > 0){
			if(condition.length()>0){
				condition.append(" and ");
			}
			condition.append("p.parent_id2=");
			condition.append(parentId2);
		}
		if(status > 0){
			if(condition.length()>0){
				condition.append(" and ");
			}
			condition.append("p.status=");
			condition.append(status);
		}
		if(condition.length()>0){
			query = query + " where " + condition.toString();
		}
		query = query + ") group by h.product_id";
		
		// 开启数据库连接
		Connection conn = adultadmin.util.db.DbUtil.getConnection("sms");
		Statement st = null;
		PreparedStatement pst = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		String sql = "";
		WareService wareService = new WareService();
		IProductStockService psService = ServiceFactory.createProductStockService(IBaseService.CONN_IN_SERVICE, wareService.getDbOp());
		try {
			
			List proxyList = wareService.getSelects("supplier_standard_info ", "where status=1 order by id");
			request.setAttribute("proxyList", proxyList);
			
			if(!search){
				return;
			}
			
			NoDpproductStatBean bean = null;
			List dpproductList = new ArrayList();
			st = conn.createStatement();
			rs = st.executeQuery(query);
			float total = 0;  //库存总金额
			int totalCount=0;//总数	
			while (rs.next()) {
				
				int productId = rs.getInt("p.id");
				
				//按条件过滤
				//出货时长
				String lastOutTime = "";
				int outTimeDays = 0;
				sql = "select last_stockout_datetime d from historical_statistics where product_id = ? and stat_datetime = ?";
				pst = conn.prepareStatement(sql);
				pst.setInt(1, productId);
				pst.setString(2, statDate);
				rs2 = pst.executeQuery();
				if(rs2.next()){
					lastOutTime = rs2.getString("d");
				}
				pst.close();
				if(!StringUtil.convertNull(lastOutTime).equals("")){
					outTimeDays = DateUtil.getDaySub(lastOutTime, DateUtil.formatDate(DateUtil.getNowDate()));
				}
				
				if(lastOutDaysAgo > 0 && outTimeDays!=-1){
					if(outTimeDays < lastOutDaysAgo){
						continue;
					}
				}

				int soonOutCount = 0;
				int soonReturnCount = 0;
				if(lastDaysAgo > 0 ){
					//近期销量等数据
					String nowDate = DateUtil.formatDate(DateUtil.getNowDate());
					sql = "select sum(total_sales) tsa, sum(total_returns) tr from historical_statistics where product_id = ? and stat_datetime between ? and ?";
					pst = conn.prepareStatement(sql);
					pst.setInt(1, productId);
					pst.setString(2, DateUtil.getBackFromDate(nowDate, lastDaysAgo-1));
					pst.setString(3, DateUtil.getNow());
					rs2 = pst.executeQuery();
					if(rs2.next()){
						soonOutCount = rs2.getInt("tsa");
						soonReturnCount = rs2.getInt("tr");
					}
					pst.close();
				}
				
				bean = new NoDpproductStatBean();
				bean.setProductId(rs.getInt("p.id"));// 商品ID
				bean.setCode(rs.getString("p.code"));// 编号
				bean.setName(rs.getString("p.name"));// 名称
				bean.setOriName(rs.getString("p.oriname"));// 原名称
				bean.setLevelOneName(rs.getString("c1.name"));//一级分类
				bean.setLevelTwoName(rs.getString("c2.name"));//二级分类
				bean.setHistoryInCount(rs.getInt("ts"));
				bean.setHistoryOutCount(rs.getInt("tsa")-rs.getInt("tr"));
				bean.setStockType(stockType);
				bean.setDaysOfOut(outTimeDays);
				bean.setSoonOutCount(soonOutCount);
				bean.setSoonReturnCount(soonReturnCount);
				
				//库存
				voProduct product = new voProduct();
				List psList = psService.getProductStockList("product_id=" + productId, -1, -1, "id desc");
				product.setId(productId);
				product.setPrice5(rs.getFloat("p.price5"));
				product.setPsList(psList);
				bean.setProduct(product);
				
				//最后入库时间
				String lastInTime = "";
				sql = "select max(last_stockin_datetime) d from historical_statistics where product_id = ? and stat_datetime = ?";
				pst = conn.prepareStatement(sql);
				pst.setInt(1, productId);
				pst.setString(2, statDate);
				rs2 = pst.executeQuery();
				if(rs2.next()){
					lastInTime = rs2.getString("d");
				}
				pst.close();
				bean.setLastInTime(lastInTime);
				
				dpproductList.add(bean);
			}
			
			request.setAttribute("count", "" + dpproductList.size());
			request.setAttribute("dpproductList", dpproductList);
			request.setAttribute("total", String.valueOf(total));
			request.setAttribute("totalCount", String.valueOf(totalCount));
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			
			wareService.releaseAll();
			
			if(rs!=null){
				rs.close();
			}
			if(rs2!=null){
				rs2.close();
			}
			if(st!=null){
				st.close();
			}
			if(pst!=null){
				pst.close();
			}
			if(conn!=null){
				conn.close();
			}
		}
	}
}

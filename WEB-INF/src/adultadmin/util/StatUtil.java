/*
 * Created on 2009-7-15
 *
 */
package adultadmin.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import mmb.ware.WareService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;

public class StatUtil {

	/*
	 * 订单ID缓存表：order_id_cache
	 * 说明：
	 * 		cache_datetime : 缓存时间(订单创建时间)
	 * 		order_id1 : 缓存时间起始ID
	 * 		order_id2 : 缓存时间当天最大ID
	 */

	private static Map sqlMap = new HashMap();
	public static Map dayOrderIdMap = new HashMap();
	public static Map dayOrderIdMap2 = new HashMap();
	public static Map dateTimeOrderIdMap = new HashMap();
	private static Map dayIdMap = new HashMap();
	public static int todayOrderId = 0;
	private static String date = "";
	private static Date oldestDate = DateUtil.parseDate("2006-10-27");
	private static int minOrderId = 194;
	public static Log statLog = LogFactory.getLog("stat.Log");

	static {
		// 成交但没有发货的总订单：成交订单中没有出库的订单（订单状态为：3,6,9,12,14、发货状态为：为处理、处理中、复核）
		String sql = "select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=uo.id) products from user_order uo join user_order_status uos on uo.status=uos.id left outer join order_stock os on uo.id=os.order_id where uo.id > 172908 and uo.status in (3,6,9,12,14) and (os.status is null or os.status in (0,1,5))";
		String key = "orderStockStatRealTime_noStockout";
		sqlMap.put(key, sql);

		// 没有“申请出库”的订单：还没有操作“申请出货”的订单（订单状态：3、没有发货记录）
		sql = "select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=uo.id) products from user_order uo join user_order_status uos on uo.status=uos.id left outer join order_stock os on uo.id=os.order_id where uo.id > 172908 and uo.status in (3,6,9,12,14) and (os.status is null)";
		key = "orderStockStatRealTime_noAddStockout";
		sqlMap.put(key, sql);

		// 全库之和可发货的订单：通过库房各地域之间的调拨能发货的订单数（订单状态：3,6、全库存数量大于等于订单中货品数量）
		sql = "select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=uo.id) products from user_order uo join user_order_status uos on uo.status=uos.id left outer join order_stock os on uo.code=os.order_code where uo.id > 172908 and uo.status in (3,6,9,12,14) and (os.status is null or os.status=0)";
		key = "orderStockStatRealTime_hasStock";
		sqlMap.put(key, sql);

		// 全库缺货的订单：库存不足，需要通过采购入库才能发货的订单（订单状态：3，6、全库存数量小于订单中货品数量）
		sql = "select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=uo.id) products from user_order uo join user_order_status uos on uo.status=uos.id left outer join order_stock os on uo.code=os.order_code where uo.id > 172908 and uo.status in (3,6,9,12,14) and (os.status is null or os.status=0)";
		key = "orderStockStatRealTime_noStock";
		sqlMap.put(key, sql);

		// 待出货订单
		sql = "select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=uo.id) products from user_order uo join user_order_status uos on uo.status=uos.id join order_stock os on uo.code=os.order_code where uo.id > 172908 and uo.status in (3,6,9,12,14) and os.status in (1)";
		key = "orderStockStatRealTime_stockReady";
		sqlMap.put(key, sql);

		// 复核的订单
		sql = "select *,(select GROUP_CONCAT(d.name) from user_order_product c,product d where c.product_id=d.id and c.order_id=uo.id) products from user_order uo join user_order_status uos on uo.status=uos.id join order_stock os on uo.code=os.order_code where uo.id > 172908 and uo.status in (3,6,9,12,14) and os.status in (5)";
		key = "orderStockStatRealTime_stockRecheck";
		sqlMap.put(key, sql);
	}

	/**	
	 * 作者：赵林
	 * 
	 * 说明：获取当日首个订单ID
	 */
	public static synchronized int getTodayOrderId(){
		String curDate = DateUtil.formatDate(new Date());

		if (!curDate.equals(date)||todayOrderId==0) {   //查询内存中是否存在缓存
			Connection conn = DbUtil.getConnection();
			Statement st = null;
			PreparedStatement pst = null;
			PreparedStatement pst2 = null;
			ResultSet rs = null;
			ResultSet rs2 = null;
			try {

				st = conn.createStatement();
				String sql = "select order_id1 from order_id_cache where cache_datetime = '"+curDate+"'";
				rs = st.executeQuery(sql);
				if (rs.next()) { //查询数据库中是否存在缓存
					//有缓存记录
					int id = rs.getInt(1);

					if(id == 0){ //无order_id1记录,直接查询order_id并更新数据库缓存和内存缓存
						sql = "select order_id1 from order_id_cache where cache_datetime < ? order by cache_datetime desc limit 1";
						pst = conn.prepareStatement(sql);
						pst.setString(1,curDate);
						rs2 = pst.executeQuery();
						int lastId = 0;
						if (rs2.next()) {
							lastId = rs2.getInt(1);
						}
						
						sql = "select id from user_order where create_datetime >= ? and id > ? order by id asc limit 1";
						pst = conn.prepareStatement(sql);
						pst.setString(1,curDate);
						pst.setInt(2, lastId);
						rs2 = pst.executeQuery();
						if (rs2.next()) {
							todayOrderId = rs2.getInt(1)-1;
							date = curDate;

							sql = "update order_id_cache set order_id1 = ? where cache_datetime = ?";
							pst2 = conn.prepareStatement(sql);
							pst2.setInt(1, rs2.getInt(1));
							pst2.setString(2, curDate);
							pst2.executeUpdate();
							pst2.close();

						}
						pst.close();
					}else{ //有order_id1记录，直接更新内存缓存
						todayOrderId = rs.getInt(1)-1;
						date = curDate;
					}
				}else{
					//无缓存记录，直接查询order_id并更新数据库缓存和内存缓存
					sql = "select order_id1 from order_id_cache where cache_datetime < ? order by cache_datetime desc limit 1";
					pst = conn.prepareStatement(sql);
					pst.setString(1,curDate);
					rs2 = pst.executeQuery();
					int lastId = 0;
					if (rs2.next()) {
						lastId = rs2.getInt(1);
					}
					
					sql = "select id from user_order where create_datetime>= ? and id > ? order by id asc limit 1";
					pst = conn.prepareStatement(sql);
					pst.setString(1,curDate);
					pst.setInt(2, lastId);
					rs2 = pst.executeQuery();
					if (rs2.next()) {
						todayOrderId = rs2.getInt(1)-1;
						date = curDate;

						sql = "insert into order_id_cache(cache_datetime, order_id1) values(?,?)";
						pst2 = conn.prepareStatement(sql);
						pst2.setString(1, curDate);
						pst2.setInt(2, rs2.getInt(1));
						pst2.executeUpdate();
						pst2.close();

					}
					pst.close();
				}

			} catch (Exception e) {
				e.printStackTrace();
				statLog.error(StringUtil.getExceptionInfo(e));
			} finally {
				try{
					if(st!=null){
						st.close();
					}
					if(pst!=null){
						pst.close();
					}
					if(pst2!=null){
						pst2.close();
					}
					if(conn!=null){
						conn.close();
					}
				}catch (Exception e) {
					e.printStackTrace();
					statLog.error(StringUtil.getExceptionInfo(e));
				}
			}
		}

		return todayOrderId;
	}

	/**
	 * 作者：赵林
	 * 
	 * 说明：获取某个时间前的最后一个ID
	 */
	public static synchronized int getDayOrderId(String date){
		int id = 0;
		if(!date.equals("0000-00-00")&&!date.equals("0000-00-00 00:00:00")){
			if(DateUtil.parseDate(date).before(oldestDate)){
				dayOrderIdMap.put(date, Integer.valueOf(minOrderId-1));
			}else{
				if (dayOrderIdMap.get(date) == null || ((Integer) dayOrderIdMap.get(date)).intValue()==0) {
					//查询内存中是否存在缓存
					Connection conn = DbUtil.getConnection();
					Statement st = null;
					PreparedStatement pst = null;
					PreparedStatement pst2 = null;
					ResultSet rs = null;
					ResultSet rs2 = null;
					ResultSet rs3 = null;
					try {

						st = conn.createStatement();
						String sql = "select order_id1 from order_id_cache where cache_datetime = '"+date+"'";
						rs = st.executeQuery(sql);
						if (rs.next()) { //查询数据库中是否存在缓存
							//有缓存记录
							id = rs.getInt(1);

							if(id == 0){ //无order_id1记录,直接查询order_id并更新数据库缓存和内存缓存
								sql = "select order_id1 from order_id_cache where cache_datetime < ? order by cache_datetime desc limit 1";
								pst = conn.prepareStatement(sql);
								pst.setString(1,date);
								rs2 = pst.executeQuery();
								int lastId = 0;
								if (rs2.next()) {
									lastId = rs2.getInt(1);
								}

								sql = "select id from user_order where create_datetime >= ? and id > ? order by id asc limit 1";
								pst = conn.prepareStatement(sql);
								pst.setString(1,date);
								pst.setInt(2, lastId);
								rs2 = pst.executeQuery();
								if (rs2.next()) {
									dayOrderIdMap.put(date, Integer.valueOf(rs2.getInt(1)-1));

									sql = "update order_id_cache set order_id1 = ? where cache_datetime = ?";
									pst2 = conn.prepareStatement(sql);
									pst2.setInt(1, rs2.getInt(1));
									pst2.setString(2, date);
									pst2.executeUpdate();
									pst2.close();

								}else{
									int maxId = 0;
									String sql2 = "select max(id) from user_order where id >= ?";
									pst2 = conn.prepareStatement(sql2);
									pst2.setInt(1, lastId);
									pst2.executeQuery();
									rs3 = pst2.executeQuery();
									if(rs3.next()){
										maxId = rs3.getInt(1);
									}
									return maxId;
								}
								pst.close();
							}else{ //有order_id1记录，直接更新内存缓存
								dayOrderIdMap.put(date, Integer.valueOf(rs.getInt(1)-1));
							}
						}else{
							//无缓存记录，直接查询order_id并更新数据库缓存和内存缓存
							sql = "select order_id1 from order_id_cache where cache_datetime < ? order by cache_datetime desc limit 1";
							pst = conn.prepareStatement(sql);
							pst.setString(1,date);
							rs2 = pst.executeQuery();
							int lastId = 0;
							if (rs2.next()) {
								lastId = rs2.getInt(1);
							}

							sql = "select id from user_order where create_datetime >= ? and id > ? order by id asc limit 1";
							pst = conn.prepareStatement(sql);
							pst.setString(1,date);
							pst.setInt(2, lastId);
							rs2 = pst.executeQuery();
							if (rs2.next()) {
								dayOrderIdMap.put(date, Integer.valueOf(rs2.getInt(1)-1));

								sql = "insert into order_id_cache(cache_datetime, order_id1) values(?,?)";
								pst2 = conn.prepareStatement(sql);
								pst2.setString(1, date);
								pst2.setInt(2, rs2.getInt(1));
								pst2.executeUpdate();
								pst2.close();

							}else{
								int maxId = 0;
								String sql2 = "select max(id) from user_order where id >= ?";
								pst2 = conn.prepareStatement(sql2);
								pst2.setInt(1, lastId);
								pst2.executeQuery();
								rs3 = pst2.executeQuery();
								if(rs3.next()){
									maxId = rs3.getInt(1);
								}
								return maxId;
							}
							pst.close();
						}

					} catch (Exception e) {
						e.printStackTrace();
						statLog.error(StringUtil.getExceptionInfo(e));
					} finally {
						try{
							if(st!=null){
								st.close();
							}
							if(pst!=null){
								pst.close();
							}
							if(pst2!=null){
								pst2.close();
							}
							if(conn!=null){
								conn.close();
							}
						}catch (Exception e) {
							e.printStackTrace();
							statLog.error(StringUtil.getExceptionInfo(e));
						}
					}
				}
			}
			if (dayOrderIdMap.get(date) != null) {
				id = ((Integer) dayOrderIdMap.get(date)).intValue();
			}
		}
		return id;
	}

	/**
	 * 作者：赵林
	 * 
	 * 说明：获取某个时间前的某个表的最后一个ID
	 */
	public static synchronized int getDayId(String date, String column, String table) {

		int id = 0;
		if (dayIdMap.get(table+","+date) == null) {
			DbOperation dbOp = new DbOperation();
			dbOp.init(DbOperation.DB_SLAVE);
			WareService service = new WareService(dbOp);
			try {
				StringBuilder buff = new StringBuilder(); 
				buff.append("select id from ");
				buff.append(table);
				buff.append(" where ");
				buff.append(column);
				buff.append(" < '");
				buff.append(date);
				buff.append("' order by id desc limit 1");

				ResultSet rs = service.getDbOp().executeQuery(buff.toString());
				if (rs.next()) {
					dayIdMap.put(table+","+date, Integer.valueOf(rs.getInt(1)));
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				service.releaseAll();
			}
		}

		id = ((Integer) dayIdMap.get(table+","+date)).intValue();

		return id;
	}

	/**
	 * 作者：赵林
	 * 
	 * 说明：获取某个时间第一个ID
	 */
	public static synchronized int getDayFirstOrderId(String date){
		int id = 0;
		if(!date.equals("0000-00-00")&&!date.equals("0000-00-00 00:00:00")){
			if(DateUtil.parseDate(date).before(oldestDate)){
				dayOrderIdMap2.put(date, Integer.valueOf(minOrderId-1));
			}else{
				if (dayOrderIdMap2.get(date) == null || ((Integer) dayOrderIdMap2.get(date)).intValue()==0) {
					//查询内存中是否存在缓存
					Connection conn = DbUtil.getConnection();
					Statement st = null;
					PreparedStatement pst = null;
					PreparedStatement pst2 = null;
					ResultSet rs = null;
					ResultSet rs2 = null;
					ResultSet rs3 = null;
					try {

						st = conn.createStatement();
						String sql = "select order_id1 from order_id_cache where cache_datetime = '"+date+"'";
						rs = st.executeQuery(sql);
						if (rs.next()) { //查询数据库中是否存在缓存
							//有缓存记录
							id = rs.getInt(1);

							if(id == 0){ //无order_id1记录,直接查询order_id并更新数据库缓存和内存缓存
								sql = "select order_id1 from order_id_cache where cache_datetime < ? order by cache_datetime desc limit 1";
								pst = conn.prepareStatement(sql);
								pst.setString(1,date);
								rs2 = pst.executeQuery();
								int lastId = 0;
								if (rs2.next()) {
									lastId = rs2.getInt(1);
								}

								sql = "select id from user_order where create_datetime >= ? and id > ? order by id asc limit 1";
								pst = conn.prepareStatement(sql);
								pst.setString(1,date);
								pst.setInt(2, lastId);
								rs2 = pst.executeQuery();
								if (rs2.next()) {
									dayOrderIdMap2.put(date, Integer.valueOf(rs2.getInt(1)));

									sql = "update order_id_cache set order_id1 = ? where cache_datetime = ?";
									pst2 = conn.prepareStatement(sql);
									pst2.setInt(1, rs2.getInt(1));
									pst2.setString(2, date);
									pst2.executeUpdate();
									pst2.close();

								}else{
									int maxId = 0;
									String sql2 = "select max(id) from user_order where id >= ?";
									pst2 = conn.prepareStatement(sql2);
									pst2.setInt(1, lastId);
									pst2.executeQuery();
									rs3 = pst2.executeQuery();
									if(rs3.next()){
										maxId = rs3.getInt(1);
									}
									return maxId;
								}
								pst.close();
							}else{ //有order_id1记录，直接更新内存缓存
								dayOrderIdMap2.put(date, Integer.valueOf(rs.getInt(1)));
							}
						}else{
							//无缓存记录，直接查询order_id并更新数据库缓存和内存缓存
							sql = "select order_id1 from order_id_cache where cache_datetime < ? order by cache_datetime desc limit 1";
							pst = conn.prepareStatement(sql);
							pst.setString(1,date);
							rs2 = pst.executeQuery();
							int lastId = 0;
							if (rs2.next()) {
								lastId = rs2.getInt(1);
							}

							sql = "select id from user_order where create_datetime >= ? and id > ? order by id asc limit 1";
							pst = conn.prepareStatement(sql);
							pst.setString(1,date);
							pst.setInt(2, lastId);
							rs2 = pst.executeQuery();
							if (rs2.next()) {
								dayOrderIdMap2.put(date, Integer.valueOf(rs2.getInt(1)));

								sql = "insert into order_id_cache(cache_datetime, order_id1) values(?,?)";
								pst2 = conn.prepareStatement(sql);
								pst2.setString(1, date);
								pst2.setInt(2, rs2.getInt(1));
								pst2.executeUpdate();
								pst2.close();

							}else{
								int maxId = 0;
								String sql2 = "select max(id) from user_order where id >= ?";
								pst2 = conn.prepareStatement(sql2);
								pst2.setInt(1, lastId);
								pst2.executeQuery();
								rs3 = pst2.executeQuery();
								if(rs3.next()){
									maxId = rs3.getInt(1);
								}
								return maxId;
							}
							pst.close();
						}

					} catch (Exception e) {
						e.printStackTrace();
						statLog.error(StringUtil.getExceptionInfo(e));
					} finally {
						try{
							if(st!=null){
								st.close();
							}
							if(pst!=null){
								pst.close();
							}
							if(pst2!=null){
								pst2.close();
							}
							if(conn!=null){
								conn.close();
							}
						}catch (Exception e) {
							e.printStackTrace();
							statLog.error(StringUtil.getExceptionInfo(e));
						}
					}
				}
			}
			if (dayOrderIdMap2.get(date) != null) {
				id = ((Integer) dayOrderIdMap2.get(date)).intValue();
			}
		}
		return id;
	}

	/**
	 * 获取某个时间点以后的订单起始ID<br/>
	 * 如果找到了这个ID，就缓存起来；<br/>
	 * 如果没找到，就返回0。<br/>
	 * 该ID保存在 order_id_cache表中的order_id2字段，可能为0。
	 * @param datetime
	 * @return
	 */
	public static synchronized int getDateTimeFirstOrderId(String datetime){
		int id = 0;
		if(!datetime.equals("0000-00-00")&&!datetime.equals("0000-00-00 00:00:00")){
			if(DateUtil.parseDate(datetime).before(oldestDate)){
				dateTimeOrderIdMap.put(date, Integer.valueOf(minOrderId-1));
			}else{
				if (dateTimeOrderIdMap.get(datetime) == null || (id = ((Integer) dateTimeOrderIdMap.get(datetime)).intValue())==0) {
					//查询内存中是否存在缓存
					Connection conn = DbUtil.getConnection();
					Statement st = null;
					PreparedStatement pst = null;
					PreparedStatement pst2 = null;
					ResultSet rs = null;
					ResultSet rs2 = null;
					ResultSet rs3 = null;
					try {
						st = conn.createStatement();
						String sql = "select order_id2 from order_id_cache where cache_datetime = '"+datetime+"'";
						rs = st.executeQuery(sql);
						if (rs.next()) { //查询数据库中是否存在缓存
							//有缓存记录
							id = rs.getInt(1);

							if(id == 0){ //无order_id2记录,直接查询order_id并更新数据库缓存和内存缓存
								sql = "select order_id2 from order_id_cache where cache_datetime < ? order by cache_datetime desc limit 1";
								pst = conn.prepareStatement(sql);
								pst.setString(1,datetime);
								rs2 = pst.executeQuery();
								int lastId = 0;
								if (rs2.next()) {
									lastId = rs2.getInt(1);
								}

								sql = "select id from user_order where create_datetime >= ? and id > ? order by id asc limit 1";
								pst = conn.prepareStatement(sql);
								pst.setString(1,datetime);
								pst.setInt(2, lastId);
								rs2 = pst.executeQuery();
								if (rs2.next()) {
									dateTimeOrderIdMap.put(datetime, Integer.valueOf(rs2.getInt(1)));

									sql = "update order_id_cache set order_id2 = ? where cache_datetime = ?";
									pst2 = conn.prepareStatement(sql);
									pst2.setInt(1, rs2.getInt(1));
									pst2.setString(2, datetime);
									pst2.executeUpdate();
									pst2.close();

								}else{
									int maxId = 0;
									String sql2 = "select max(id) from user_order where id >= ?";
									pst2 = conn.prepareStatement(sql2);
									pst2.setInt(1, lastId);
									pst2.executeQuery();
									rs3 = pst2.executeQuery();
									if(rs3.next()){
										maxId = rs3.getInt(1);
									}
									return maxId;
								}
								pst.close();
							}else{ //有order_id1记录，直接更新内存缓存
								dateTimeOrderIdMap.put(datetime, Integer.valueOf(id));
							}
						}else{
							//无缓存记录，直接查询order_id并更新数据库缓存和内存缓存
							sql = "select order_id2 from order_id_cache where cache_datetime < ? order by cache_datetime desc limit 1";
							pst = conn.prepareStatement(sql);
							pst.setString(1,datetime);
							rs2 = pst.executeQuery();
							int lastId = 0;
							if (rs2.next()) {
								lastId = rs2.getInt(1);
							}

							sql = "select id from user_order where create_datetime >= ? and id > ? order by id asc limit 1";
							pst = conn.prepareStatement(sql);
							pst.setString(1,datetime);
							pst.setInt(2, lastId);
							rs2 = pst.executeQuery();
							if (rs2.next()) {
								dateTimeOrderIdMap.put(date, Integer.valueOf(rs2.getInt(1)));

								sql = "insert into order_id_cache(cache_datetime, order_id2) values(?,?)";
								pst2 = conn.prepareStatement(sql);
								pst2.setString(1, datetime);
								pst2.setInt(2, rs2.getInt(1));
								pst2.executeUpdate();
								pst2.close();

							}else{
								int maxId = 0;
								String sql2 = "select max(id) from user_order where id >= ?";
								pst2 = conn.prepareStatement(sql2);
								pst2.setInt(1, lastId);
								pst2.executeQuery();
								rs3 = pst2.executeQuery();
								if(rs3.next()){
									maxId = rs3.getInt(1);
								}
								return maxId;
							}
							pst.close();
						}
					} catch (Exception e) {
						e.printStackTrace();
						statLog.error(StringUtil.getExceptionInfo(e));
					} finally {
						try{
							if(st!=null){
								st.close();
							}
							if(pst!=null){
								pst.close();
							}
							if(pst2!=null){
								pst2.close();
							}
							if(conn!=null){
								conn.close();
							}
						}catch (Exception e) {
							e.printStackTrace();
							statLog.error(StringUtil.getExceptionInfo(e));
						}
					}
				}
			}
		}
		if (dateTimeOrderIdMap.get(datetime) != null) {
			id = ((Integer) dateTimeOrderIdMap.get(datetime)).intValue();
		}
		
		return id;
	}

	public static String getSql(String key){
		return (String) sqlMap.get(key);
	}
	
	public static void clearOrderIdCache(){
		dayOrderIdMap = new HashMap();
		dayOrderIdMap2 = new HashMap();
		dateTimeOrderIdMap = new HashMap();
		dayIdMap = new HashMap();
		todayOrderId = 0;
		String date = "";
	}
}
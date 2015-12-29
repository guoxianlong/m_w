package adultadmin.util.timedtask;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.ware.WareService;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.bean.order.OrderStockBean;
import adultadmin.bean.stock.ProductStockBean;
import adultadmin.service.IAdminService;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.IStockService;
import adultadmin.util.DateUtil;
import adultadmin.util.HttpKit;
import adultadmin.util.MD5Util;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;
import cn.mmb.delivery.application.DeliveryOrderFacade;

import com.mmb.framework.support.SpringHandler;

public class GISGetDeliverJob implements Job {

	public static int neFr = 67509;// 19e订单友链ID
	public static int nmtFr = 135011;// 糯米团订单友链ID

	public static int searchCount = 50;

	private static int backHours = 10;

	private static String url = "http://g7.huoyunren.com/rest/index.php";
	private static String app_key = "mmbservice";
	private static String app_secret = "92c609a91d19c4ba87eef59a1c3d5a77";
	private static String method = "ips2.api.orderaddr_mmb";
	private static String t_orgcode = "mmb";
	private static int t_tasktype = 3;
	private static int o_intval5 = 5;
	public static byte[] StockExceptionJobLock = new byte[0];

	static String[] adds=new String[51];
	static {
		adds[0]="河北省廊坊市文安县县医院(电话通知)";
		adds[1]="河北省邢台市桥东区玉溪市场(电话通知)";
		adds[2]="河北省廊坊市安次区葛渔城镇世纪华联(电话通知)";
		adds[3]="河北省唐山市路南区国广道与陵园路交叉口(电话通知)";
		adds[4]="河北省石家庄市行唐县只里乡贝村 (电话通知)";
		adds[5]="河北省石家庄市长安区胜利北大街古运码头23号楼1单元402室(电话通知)";
		adds[6]="河北省张家口市宣化区钢鑫小区19号楼11单元(电话通知)";
		adds[7]="河北省石家庄市新华区天翼路与西三庄街交叉口南行200米有一手快修(电话通知)";
		adds[8]="河北省沧州市沧县沧县中学13—18班(电话通知)";
		adds[9]="河北省沧州市沧县沧县中学13—18班(电话通知)";
		adds[10]="河北省邢台市广宗县件只乡金塔寨村(电话通知)";
		adds[11]="河北省石家庄市无极县东侯坊乡南侯坊村(电话通知)";
		adds[12]="河北省保定市高阳县高阳中学(电话通知)";
		adds[13]="河北省唐山市滦县雷庄镇雷庄邮政支局(电话通知)";
		adds[14]="河北省张家口市张北县锦绣花城小区18号楼四单(电话通知)";
		adds[15]="河北省保定市雄县南市场新新书店(电话通知)";
		adds[16]="河北省沧州市孟村回族自治县孟村镇梧桐花园(电话通知)";
		adds[17]="河北省沧州市任丘市七间房乡西大坞一村(电话通知)";
		adds[18]="河北省保定市高碑店市张八屯乡柳屯村(电话通知)";
		adds[19]="河北省张家口市桥西区至善街前进婚纱影楼(电话通知)";
		adds[20]="河北省保定市易县易州镇营房村(电话通知)";
		adds[21]="河北省保定市曲阳县欣平家园门卫处(电话通知)";
		adds[22]="河北省邢台市桥西区中兴西大街366号人民政府(电话通知)";
		adds[23]="河北省保定市高碑店市桥刘凡村汇通代理点(电话通知)";
		adds[24]="河北省邢台市隆尧县固城镇孟村邮局(电话通知)";
		adds[25]="河北省唐山市丰润区王官营中学(电话通知)";
		adds[26]="河北省唐山市玉田县林西镇黄土坎村(电话通知)";
		adds[27]="河北省石家庄市栾城县圣雪路50号(电话通知)";
		adds[28]="河北省保定市唐县王京镇拔茄村圆通快递(电话通知)";
		adds[29]="河北省唐山市丰南区黑沿子镇申通快递(电话通知)";
		adds[30]="河北省廊坊市文安县北光州村民安小区(电话通知)";
		adds[31]="河北省廊坊市霸州市信安镇《同一首歌》KTV电话通知";
		adds[32]="河北省沧州市沧县大褚村何陈庄东道口(电话通知)";
		adds[33]="河北省石家庄市栾城县楼底镇东尹村(电话通知)";
		adds[34]="河北省石家庄市藁城市梅花镇西白露村(电话通知)";
		adds[35]="河北省秦皇岛市海港区人民广场食神坊海洋之星(电话通知)";
		adds[36]="河北省沧州市沧县杜生镇商贸街(电话通知)";
		adds[37]="河北省廊坊市文安县滩里镇五棵松木业有限公司(电话通知)";
		adds[38]="河北省廊坊市三河市燕郊镇马起乏村(电话通知)";
		adds[39]="河北省唐山市丰南区胥各庄街道白石家园3-2-2701（电话通知）";
		adds[40]="河北省邯郸市广平县平固店镇北下堡村(电话通知)";
		adds[41]="河北省石家庄市高邑县中韩乡赵村(电话通知)";
		adds[42]="河北省沧州市肃宁县火车站(电话通知)";
		adds[43]="河北省廊坊市文安县大柳河镇琉庄村(电话通知)";
		adds[44]="河北省石家庄市井陉矿区冯家沟社区(电话通知)";
		adds[45]="河北省邢台市平乡县油召乡听西口加油站附近(电话通知)";
		adds[46]="河北省唐山市乐亭县马头营镇套里西地村(电话通知)";
		adds[47]="河北省唐山市遵化市华明南路西高级中学(电话通知)";
		adds[48]="河北省唐山市乐亭县大清河盐场9棟3单元(电话通知)";
		adds[49]="河北省邯郸市大名县天雄路东337号回蒙家宴饭店(电话通知)";
		adds[50]="河北省廊坊市大厂回族自治县王必屯村(电话通知)";
	}
	
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		System.out.println(DateUtil.getNow() + "GIS分配快递公司定时任务开始");

		String now = DateUtil.getNow();
		String startDate = DateUtil.getBackHourFromDate(now, backHours);
		String endDate = now;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB);
		WareService wareService = new WareService(dbOp);

		List<OrderStockBean> osBeanList = new ArrayList<OrderStockBean>();
		OrderStockBean osBean = null;

		try {
			ResultSet rs = dbOp
					.executeQuery("select os.order_id,uo.address,os.stock_area,uo.flat,uo.fr,os.id from order_stock os join user_order uo on os.order_id=uo.id  where os.create_datetime >='"
							+ startDate
							+ "' and os.create_datetime <='"
							+ endDate
							+ "' and os.deliver=0 and os.status <> "
							+ OrderStockBean.STATUS4);
			while (rs.next()) {
				osBean = new OrderStockBean();
				osBean.setId(rs.getInt(6));
				osBean.setOrderId(rs.getInt(1));
				osBean.setName(rs.getString(2));// 暂时存放地址
				osBean.setStockArea(rs.getInt(3));
				osBean.setStockType(rs.getInt(4));// 暂时存放flat
				osBean.setProductCount(rs.getInt(5));// 暂时存放fr
				osBeanList.add(osBean);
			}
			rs.close();
			List<OrderStockBean> gisOsBeanList = new ArrayList<OrderStockBean>();
			for (OrderStockBean os : osBeanList) {
				// 判断是否需要gis，不需要则更新快递公司id
				int deliver = needGetGIS(os.getStockType(), os.getName(),
						os.getProductCount(), os.getStockArea(),
						os.getOrderId(), dbOp);
				if (deliver == -1) {
					gisOsBeanList.add(os);
				} else {
					updateDeliver(os.getOrderId(), os.getId(), deliver, dbOp);
				}
			}

			int len = gisOsBeanList.size();
			int looptime = len / searchCount;
			int left = len % searchCount;

			for (int t = 0; t < looptime; t++) {
				Map<String, Object> params = new HashMap<String, Object>();
				JSONArray array = new JSONArray();
				JSONObject object = null;
				for (int i = searchCount * t; i < searchCount * (t + 1); i++) {
					OrderStockBean os = gisOsBeanList.get(i);
					object = new JSONObject();
					object.put("o_pcode", os.getOrderId());
					object.put("t_orgcode", t_orgcode);
					object.put("t_tasktype", t_tasktype);
					object.put("o_consigneeaddr", os.getName());
					object.put("o_intval5", o_intval5);
					array.add(object);
				}
				String signKey = app_secret + "app_key" + app_key + "data"
						+ array.toString() + "method" + method + "timestamp"
						+ now + app_secret;
				String sign = MD5Util.getKeyedDigest(signKey, "");
				params.put("app_key", app_key);
				params.put("method", method);
				params.put("timestamp", now);
				params.put("sign", sign.toUpperCase());
				params.put("data", array.toString());
				String result = HttpKit.post(url, params);
				try {
					JSONObject resultJson = JSONObject.fromObject(result);
					if (0 != resultJson.getInt("code")) {
						// 返回异常 按照mmb逻辑
						for (int i = searchCount * t; i < searchCount * (t + 1); i++) {
							OrderStockBean os = gisOsBeanList.get(i);
							int deliver = getMMBDeliver(os.getName(),
									os.getStockArea(), dbOp);
							updateDeliver(os.getOrderId(), os.getId(), deliver,
									dbOp);
						}
					} else {
						JSONObject dataJson = JSONObject.fromObject(resultJson
								.get("data"));
						JSONObject resultObj = JSONObject.fromObject(dataJson
								.get("result"));
						for (int i = searchCount * t; i < searchCount * (t + 1); i++) {
							try {
								OrderStockBean os = gisOsBeanList.get(i);
								JSONObject json = null;
								if (resultObj.get(os.getOrderId() + "") != null) {
									json = getResultJson(resultObj.getJSONArray(os.getOrderId() + ""));
								} 
								if (json != null && StringUtil.toInt(json.get("ilevel") + "") >= 5) {
									int deliver = getGISDeliver(os.getName(),
											os.getStockArea(),
											json.getString("t_orgcode"), dbOp);
									if (os.getName().startsWith("河北省保定"))
										System.out.println("调用gis，deliverId为" + deliver + ",gis接收数据为" + json.getString("t_orgcode"));
									updateDeliver(os.getOrderId(), os.getId(),
											deliver, dbOp);
								} else {
									int deliver = getMMBDeliver(os.getName(),
											os.getStockArea(), dbOp);
									updateDeliver(os.getOrderId(), os.getId(),
											deliver, dbOp);
								}
							} catch (Exception e) {
								System.out.println("通过gis信息更新deliver异常");
								e.printStackTrace();
								OrderStockBean os = gisOsBeanList.get(i);
								int deliver = getMMBDeliver(os.getName(),
										os.getStockArea(), dbOp);
								System.out.println("mmb:"+deliver+"订单id:"+os.getOrderId());
								updateDeliver(os.getOrderId(), os.getId(),
										deliver, dbOp);
							}
						}
					}

				} catch (Exception e) {
					System.out.println("解析gis数据异常");
					e.printStackTrace();
					// 异常 按照mmb逻辑
					for (int i = searchCount * t; i < searchCount * (t + 1); i++) {
						OrderStockBean os = gisOsBeanList.get(i);
						int deliver = getMMBDeliver(os.getName(),
								os.getStockArea(), dbOp);
						updateDeliver(os.getOrderId(), os.getId(), deliver,
								dbOp);
					}
				}
			}
			if (left > 0) {
				// 剩余的
				Map<String, Object> params = new HashMap<String, Object>();
				JSONArray array = new JSONArray();
				JSONObject object = null;
				for (int i = searchCount * looptime; i < len; i++) {
					OrderStockBean os = gisOsBeanList.get(i);
					object = new JSONObject();
					object.put("o_pcode", os.getOrderId());
					object.put("t_orgcode", t_orgcode);
					object.put("t_tasktype", t_tasktype);
					object.put("o_consigneeaddr", os.getName());
					object.put("o_intval5", o_intval5);
					array.add(object);
				}
				String signKey = app_secret + "app_key" + app_key + "data"
						+ array.toString() + "method" + method + "timestamp" + now
						+ app_secret;
				String sign = MD5Util.getKeyedDigest(signKey, "");
				params.put("app_key", app_key);
				params.put("method", method);
				params.put("timestamp", now);
				params.put("sign", sign.toUpperCase());
				params.put("data", array.toString());
				String result = HttpKit.post(url, params);
				try {
					JSONObject resultJson = JSONObject.fromObject(result);
					if (0 != resultJson.getInt("code")) {
						// 返回异常 按照mmb逻辑
						for (int i = searchCount * looptime; i < len; i++) {
							OrderStockBean os = gisOsBeanList.get(i);
							int deliver = getMMBDeliver(os.getName(),
									os.getStockArea(), dbOp);
							System.out.println("mmb:"+deliver+"订单id:"+os.getOrderId());
							updateDeliver(os.getOrderId(), os.getId(), deliver,
									dbOp);
						}
					} else {
						JSONObject dataJson = JSONObject.fromObject(resultJson
								.get("data"));
						JSONObject resultObj = JSONObject.fromObject(dataJson
								.get("result"));
						for (int i = searchCount * looptime; i < len; i++) {
							try {
								OrderStockBean os = gisOsBeanList.get(i);
								JSONObject json = null;
								if (resultObj.get(os.getOrderId() + "") != null) {
									json = getResultJson(resultObj.getJSONArray(os.getOrderId() + ""));
								} 
								if (json != null && StringUtil.toInt(json.get("ilevel") + "") >= 5) {
									int deliver = getGISDeliver(os.getName(),
											os.getStockArea(),
											json.getString("t_orgcode"), dbOp);
									System.out.println("gis:"+deliver+"订单id:"+os.getOrderId());
									if (os.getName().startsWith("河北省保定"))
										System.out.println("调用gis，deliverId为" + deliver + ",gis接收数据为" + json.getString("t_orgcode"));
									updateDeliver(os.getOrderId(), os.getId(),
											deliver, dbOp);
								} else {
									int deliver = getMMBDeliver(os.getName(),
											os.getStockArea(), dbOp);
									System.out.println("mmb:"+deliver+"订单id:"+os.getOrderId());
									updateDeliver(os.getOrderId(), os.getId(),
											deliver, dbOp);
								}
							} catch (Exception e) {
								System.out.println("通过gis信息更新deliver异常");
								e.printStackTrace();
								OrderStockBean os = gisOsBeanList.get(i);
								int deliver = getMMBDeliver(os.getName(),
										os.getStockArea(), dbOp);
								System.out.println("mmb:"+deliver+"订单id:"+os.getOrderId());
								updateDeliver(os.getOrderId(), os.getId(), deliver,
										dbOp);
							}
						}
					}
	
				} catch (Exception e) {
					System.out.println("解析gis数据异常");
					e.printStackTrace();
					// 异常 按照mmb逻辑
					for (int i = searchCount * looptime; i < len; i++) {
						OrderStockBean os = gisOsBeanList.get(i);
						int deliver = getMMBDeliver(os.getName(),
								os.getStockArea(), dbOp);
						System.out.println("mmb:"+deliver+"订单id:"+os.getOrderId());
						updateDeliver(os.getOrderId(), os.getId(), deliver, dbOp);
					}
				}
			}

			System.out.println(DateUtil.getNow() + "GIS分配快递公司定时任务结束");

			/**
			 * 获取前十个小时之内所有分配给韵达快递的订单
			 * delivers[]数组加入响应的快递公司id，就可以将order_code和快递公司绑定在deliver_relation表中
			 */
			int delivers[] = {DeliverCorpInfoBean.DELIVER_ID_YD,DeliverCorpInfoBean.DELIVER_ID_YT_WX,DeliverCorpInfoBean.DELIVER_ID_YT_CD,DeliverCorpInfoBean.DELIVER_ID_RFD_SD,DeliverCorpInfoBean.DELIVER_ID_JD_CD,DeliverCorpInfoBean.DELIVER_ID_JD_WX};
			DeliveryOrderFacade deliveryOrderFacade= SpringHandler.getBean("DeliveryOrderFacade");
			deliveryOrderFacade.relationDeliver(delivers, startDate, endDate);
			
		} catch (Exception e) {
			e.printStackTrace();
			wareService.getDbOp().rollbackTransaction();
			System.out.println(DateUtil.getNow() + "GIS分配快递公司定时任务异常");
		} finally {
			 wareService.releaseAll();
		}
	}

	private int getDeliverAmazom(String address, int stockArea) {
		int deliver = 0;
		if (stockArea == 4) {// 无锡分配快递公司
			deliver = 37;// 无锡邮政
		} else if (stockArea == 3) {// 增城分配快递公司
			if (address.substring(0, 4).contains("广东")) {// 广东省内
				deliver = 11;
			} else {// 光速省外
				deliver = 9;
			}
		}
		return deliver;
	}

	private int getDeliverTB3C(String address, int stockArea) {
		int deliver = 0;
		if (stockArea == 0) {// 北京分配快递公司
			deliver = 45;// 北京宅急送
		} else if (stockArea == 8) {// 西安分配快递公司
			deliver = 50;// 西安宅急送
		} else if (stockArea == 4) {// 无锡分配快递公司
			deliver = 37;// 无锡邮政
		} else if (stockArea == 3) {// 增城分配快递公司
			if (address.substring(0, 4).contains("广东")) {// 广东省内
				deliver = 11;
			} else {// 光速省外
				deliver = 9;
			}
		} else if (stockArea == 9) {// 成都分配快递公司
			deliver = 52;// 四川邮政
		}
		return deliver;
	}

	/**
	 * 判断是不是亚马逊的EMS订单 2014-05-20
	 * 
	 * @param order
	 * @return boolean
	 * @author syuf
	 */
	private static boolean checkOrderAmazom(int orderId, int flat) {
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		int number = -1;
		try {
			if (flat == 3) {
				ResultSet rs = dbOp
						.executeQuery("SELECT uop.order_ship FROM user_order_pop AS uop where type = 1 and order_id="
								+ orderId);
				if (rs.next()) {
					number = rs.getInt(1);
				}
				if (rs != null) {
					rs.close();
				}
				if (number == 1) {
					return true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return false;
	}

	/**
	 * 分配快递公司 -1 第一个可分的为非全境快递公司 其他 快递公司id
	 * 
	 * @return
	 */
	public int getDeliver(String address, int stockArea, DbOperation dbOp) {
		int deliver = 0;
		IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());

		try {
			List<HashMap<String, String>> deliverSendConfList = OrderStockBean.deliverSendConfMap.get(stockArea);
			String specialAddr = OrderStockBean.specialAddrMap.get(stockArea);
			// 非特殊地区（特殊地区按照其他省分配）
			if (!address.contains(specialAddr)) {
				for (HashMap<String, String> map : deliverSendConfList) {
					for (String province : map.keySet()) {
						if (address.startsWith(province)) {
							if (map.get(province).startsWith("0,")) {
								// 0结尾 非全境
								if (map.get(province).endsWith(",0")) {
									deliver = -1;
									return deliver;
								} else {
									deliver = StringUtil.StringToId(map.get(
											province).split(",")[1]);
									return deliver;
								}
							} else {
								String[] tempArray = map.get(province).split(
										",");
								int count = getOrderStockCount(service,StringUtil.toInt(tempArray[1]),stockArea);
								if (count < StringUtil.StringToId(tempArray[0])) {// 单量限制判断
									// 单量没超过
									// 0结尾 非全境
									if (map.get(province).endsWith(",0")) {
										deliver = -1;
										return deliver;
									} else {
										deliver = StringUtil
												.StringToId(tempArray[1]);
										return deliver;
									}
								}
							}
						}
					}
				}
			}
			for (HashMap<String, String> map : deliverSendConfList) {
				if(map.get("其他省") != null) {
					if (map.get("其他省").startsWith("0,")) {
						// 0结尾 非全境
						if (map.get("其他省").endsWith(",0")) {
							deliver = -1;
							return deliver;
						} else {
							deliver = StringUtil.StringToId(map.get("其他省").split(
									",")[1]);
							return deliver;
						}
					} else {
						String[] tempArray = map.get("其他省").split(",");
						int count = getOrderStockCount(service,StringUtil.toInt(tempArray[1]),stockArea);
						if (count < StringUtil.StringToId(tempArray[0])) {// 单量限制判断
							// 单量没超过
							// 0结尾 非全境
							if (map.get("其他省").endsWith(",0")) {
								deliver = -1;
								return deliver;
							} else {
								deliver = StringUtil.StringToId(tempArray[1]);
								return deliver;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deliver;
	}

	/**
	 * 非全境不能用
	 * @param address
	 * @param stockArea
	 * @param dbOp
	 * @return
	 */
	public int getMMBDeliver(String address, int stockArea, DbOperation dbOp) {
		int deliver = 0;
		IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		IStockService service = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());

		try {
			List<HashMap<String, String>> deliverSendConfList = OrderStockBean.deliverSendConfMap
					.get(stockArea);
			String specialAddr = OrderStockBean.specialAddrMap.get(stockArea);
			//无锡仓出库的 河北保定的订单 ，给如风达
			if (stockArea == ProductStockBean.AREA_WX && address.startsWith("河北省保定")) {
				return 16;
			}
			// 非特殊地区（特殊地区按照其他省分配）
			if (!address.contains(specialAddr)) {
				for (HashMap<String, String> map : deliverSendConfList) {
					for (String province : map.keySet()) {
						if (address.startsWith(province)) {
							if (map.get(province).startsWith("0,") && !map.get(province)
									.endsWith(",0")) {
								deliver = StringUtil.StringToId(map.get(
										province).split(",")[1]);
								return deliver;
							} else {
								String[] tempArray = map.get(province).split(
										",");
								int count = getOrderStockCount(service,StringUtil.toInt(tempArray[1]),stockArea);
								if (count < StringUtil.StringToId(tempArray[0]) && !map.get(province)
										.endsWith(",0")) {// 单量限制判断
									deliver = StringUtil
											.StringToId(tempArray[1]);
									return deliver;
								}
							}
						}
					}
				}
			}
			for (HashMap<String, String> map : deliverSendConfList) {
				if(map.get("其他省") != null) {
					if (map.get("其他省").startsWith("0,") && !map.get("其他省")
							.endsWith(",0")) {
						deliver = StringUtil
								.StringToId(map.get("其他省").split(",")[1]);
						return deliver;
					} else {
						String[] tempArray = map.get("其他省").split(",");
						int count = getOrderStockCount(service,StringUtil.toInt(tempArray[1]),stockArea);
						if (count < StringUtil.StringToId(tempArray[0]) && !map.get("其他省")
								.endsWith(",0")) {// 单量限制判断
							deliver = StringUtil.StringToId(tempArray[1]);
							return deliver;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deliver;
	}

	private int getSpecialDeliver(int flat, String address, int fr,
			int stockArea, int orderId) {
		int deliver = 0;
		int jdDeliver = 0;
		if (checkOrderAmazom(orderId, flat)) {
			deliver = getDeliverAmazom(address, stockArea);
		} else if (flat == 10 || flat == 7 || flat == 12) {
			deliver = getDeliverTB3C(address, stockArea);
		} else if (fr == neFr || fr == nmtFr) {
			if (stockArea == 3) {
				if (address.substring(0, 4).contains("广东")) {// 广东省速递局
					deliver = 11;
				} else {// 广速省外
					deliver = 9;
				}
			} else if (stockArea == 4) {
				deliver = 37;
			} else if (stockArea == 9) {
				deliver = 52;
			}
		} else if ((stockArea==4||stockArea==9)&&(jdDeliver = getNeedJdDeliver(stockArea, orderId,address))>0) {
			deliver = jdDeliver;
		}
		return deliver;
	}
	
	/** 
	 * @Description: 增城无锡一级分类是手机或电脑的分配给京东
	 * @return int 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月10日 下午5:58:20 
	 */
	public int getNeedJdDeliver(int stockArea, int orderId, String address){
		int deliver = 0;
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		try {
			List<String> removeProvinceListWuXiJD=getRemoveProvince(dbOp,4,62);//无锡京东快递公司需要被剔除的省
			List<String> removeProvinceListChengDuJD=getRemoveProvince(dbOp,9,63);//成都京东快递公司需要被剔除的省
			
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT p.parent_id1,p.parent_id2 FROM order_stock_product osp JOIN order_stock os ON osp.order_stock_id=os.id JOIN product p ON osp.product_id=p.id WHERE os.status<>3 and os.order_id=");
			sql.append(orderId);
			ResultSet rs = dbOp.executeQuery(sql.toString());
			while(rs.next()) {
				int parentId2 = rs.getInt("parent_id2");
				int parentId1 = rs.getInt("parent_id1");
				if (parentId2!=107 && parentId2!=302 && parentId2!=1605 && parentId2!=1606 && parentId2!=1608 && parentId2!=2430 && (parentId1==111 || parentId1==130)) {
					if (stockArea == 4) {
						boolean flag=true;
						if(removeProvinceListWuXiJD != null && removeProvinceListWuXiJD.size() > 0){
							for(int i=0;i<removeProvinceListWuXiJD.size();i++){
								if(address.substring(0, 4).contains(removeProvinceListWuXiJD.get(i))){
									flag=false;
									break;
								}
						}
						}
						if(flag){
							deliver = DeliverCorpInfoBean.DELIVER_ID_JD_WX;//无锡京东
						}
					}
					
					if (stockArea == 9) {
						boolean flag=true;
						if(removeProvinceListChengDuJD != null && removeProvinceListChengDuJD.size() > 0){
							for(int i=0;i<removeProvinceListChengDuJD.size();i++){
								if(address.substring(0, 4).contains(removeProvinceListChengDuJD.get(i))){
									flag=false;
									break;
								}
							}
						}
						if(flag){
							deliver = DeliverCorpInfoBean.DELIVER_ID_JD_CD;//成都京东
						}
					}
					
					break;
				}
			}
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		return deliver;
	}
	
	
	public List<String> getRemoveProvince(DbOperation dbOp,int stockArea,int deliverId){
		
		List<String> removeProvinceList=new ArrayList<String>();
		
		try {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT province FROM deliver_assign_rule WHERE stock_area="+stockArea+" AND deliver_id="+deliverId);
		
			ResultSet rs = dbOp.executeQuery(sql.toString());
		
		while(rs.next()) {
			String province = rs.getString("province");	
			removeProvinceList.add(province);	
		}
		
		if (rs != null) {
			rs.close();
		}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return removeProvinceList;
		
	}

	/**
	 * deliver == -1 有非全境快递公司可用 需要调用GIS接口
	 * 
	 * @param flat
	 * @param address
	 * @param fr
	 * @param stockArea
	 * @param orderId
	 * @param dbOp
	 * @return
	 */
	private int needGetGIS(int flat, String address, int fr, int stockArea,
			int orderId, DbOperation dbOp) {
		int deliver = getSpecialDeliver(flat, address, fr, stockArea, orderId);
		if (deliver == 0) {
			deliver = this.getAssignSpecial(orderId);
			if(deliver == 0){
				deliver = getDeliver(address, stockArea, dbOp);
			}

		}
		return deliver;
	}

	private void updateDeliver(int orderId, int orderStockId, int deliver,
			DbOperation dbOp) {
		dbOp.executeUpdate("update user_order set deliver = " + deliver
				+ " where id=" + orderId);
		dbOp.executeUpdate("update order_stock set deliver = " + deliver
				+ " where id=" + orderStockId);
	}

	public int getGISDeliver(String address, int stockArea, String gisDeliver,
			DbOperation dbOp) {
		int deliver = 0;
		IAdminService adminService = ServiceFactory.createAdminService(dbOp);
		IStockService service = ServiceFactory.createStockService(
				IBaseService.CONN_IN_SERVICE, adminService.getDbOperation());

		try {
			List<HashMap<String, String>> deliverSendConfList = OrderStockBean.deliverSendConfMap
					.get(stockArea);
			String specialAddr = OrderStockBean.specialAddrMap.get(stockArea);
			// 非特殊地区（特殊地区按照其他省分配）
			if (!address.contains(specialAddr)) {
				for (HashMap<String, String> map : deliverSendConfList) {
					for (String province : map.keySet()) {
						if (address.startsWith(province)) {
							if (map.get(province).startsWith("0,")) {
								// 没单量限制
								// gis里面包含该快递公司则返回该值，否则deliver为0且是全境时将deliver记下（最后都没匹配到时使用）
								if (gisDeliver.contains(","
										+ StringUtil.StringToId(map.get(
												province).split(",")[1]) + ",")) {
									deliver = StringUtil.StringToId(map.get(
											province).split(",")[1]);
									return deliver;
								} else {
									if (deliver == 0
											&& !map.get(province)
													.endsWith(",0")) {
										deliver = StringUtil.StringToId(map
												.get(province).split(",")[1]);
									}
								}
							} else {
								String[] tempArray = map.get(province).split(
										",");
								int count = getOrderStockCount(service,StringUtil.toInt(tempArray[1]),stockArea);
								if (count < StringUtil.StringToId(tempArray[0])) {// 单量限制判断
									// 单量没超过
									// gis里面包含该快递公司则返回该值，否则deliver为0时将deliver记下（最后都没匹配到时使用）
									if (gisDeliver.contains(","
											+ StringUtil
													.StringToId(tempArray[1])
											+ ",")) {
										deliver = StringUtil
												.StringToId(tempArray[1]);
										return deliver;
									} else {
										if (deliver == 0
												&& !map.get(province).endsWith(
														",0")) {
											deliver = StringUtil
													.StringToId(tempArray[1]);
										}
									}
								}
							}
						}
					}
				}
			}
			for (HashMap<String, String> map : deliverSendConfList) {
				if(map.get("其他省") != null) {
					if (map.get("其他省").startsWith("0,")) {
						// gis里面包含该快递公司则返回该值，否则deliver为0且是全境时将deliver记下（最后都没匹配到时使用）
						if (gisDeliver.contains(","
								+ StringUtil
										.StringToId(map.get("其他省").split(",")[1])
								+ ",")) {
							deliver = StringUtil.StringToId(map.get("其他省").split(
									",")[1]);
							return deliver;
						} else {
							if (deliver == 0 && !map.get("其他省").endsWith(",0")) {
								deliver = StringUtil.StringToId(map.get("其他省")
										.split(",")[1]);
							}
						}
					} else {
						String[] tempArray = map.get("其他省").split(",");
						int count = getOrderStockCount(service,StringUtil.toInt(tempArray[1]),stockArea);
						if (count < StringUtil.StringToId(tempArray[0])) {// 单量限制判断
							// 单量没超过
							// gis里面包含该快递公司则返回该值，否则deliver为0时将deliver记下（最后都没匹配到时使用）
							if (gisDeliver.contains(","
									+ StringUtil.StringToId(tempArray[1]) + ",")) {
								deliver = StringUtil.StringToId(tempArray[1]);
								return deliver;
							} else {
								if (deliver == 0 && !map.get("其他省").endsWith(",0")) {
									deliver = StringUtil.StringToId(tempArray[1]);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return deliver;
	}
	
	private int getOrderStockCount(IStockService service,int deliver,int stockArea) {
		int count = service.getOrderStockCount("create_datetime between '"
				+ StringUtil.cutString(
						DateUtil.getNow(), 10)
				+ " 00:00:00' and '"
				+ DateUtil.getNow()
				+ "' and deliver="
				+ deliver
				+ " and stock_area = " + stockArea +" and status<>3");
		return count;
	}
	
	private static JSONObject getResultJson(JSONArray array) {
		if (array == null) {
			return null;
		} else {
			JSONObject returnjson = new JSONObject();
			int ilevel = 0;
			StringBuffer t_orgcode = new StringBuffer(",");
			for (int i = 0,len = array.size(); i < len; i ++) {
				JSONObject json = array.optJSONObject(i);
				if (json != null) {
					ilevel = StringUtil.toInt(json.get("ilevel") + "");
					t_orgcode.append(json.getString("t_orgcode")).append(",");
				}
			}
			returnjson.put("ilevel", ilevel);
			returnjson.put("t_orgcode", t_orgcode.toString());
			return returnjson;
		}
	}

	/*
	 * 分配特定快递公司
	 */
	private int getAssignSpecial(int orderId){
		int deliver = 0;
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		try {
			StringBuilder sql = new StringBuilder();
			sql.append(" select das.deliver");
			sql.append(" from order_stock os");
			sql.append(" join user_order uo on os.order_id=uo.id");
			sql.append(" join deliver_assign_special das on os.stock_area=das.stock_area and uo.buy_mode=das.buy_mode");
			sql.append(" where uo.id=").append(orderId);
			sql.append(" and os.status <>3");
			ResultSet rs = dbOp.executeQuery(sql.toString());
			if (rs.next()) {
				deliver = rs.getInt("deliver");
			}
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			deliver = 0;
		} finally {
			dbOp.release();
		}
		return deliver;
	}
	
	public static void main(String[] args) throws UnsupportedEncodingException {
//		for(int i=0; i<5; i++){
			test2();
//		}
		System.out.println("end");
	}
	
	public static void test1() throws UnsupportedEncodingException{
		String now = DateUtil.getNow();
		Map<String, Object> params = new HashMap<String, Object>();
		JSONArray array = new JSONArray();
		JSONObject object = null;
		object = new JSONObject();
		object.put("o_pcode", "23234");
		object.put("t_orgcode", t_orgcode);
		object.put("t_tasktype", t_tasktype);
		object.put("o_consigneeaddr", "河北省");
		//object.put("o_intval5", o_intval5);
		array.add(object);
		object = new JSONObject();
		object.put("o_pcode", "2323434");
		object.put("t_orgcode", t_orgcode);
		object.put("t_tasktype", t_tasktype);
		object.put("o_consigneeaddr", "add");
		//object.put("o_intval5", o_intval5);
		array.add(object);
		String signKey = app_secret + "app_key" + app_key + "data"
				+ array.toString() + "method" + method + "timestamp"
				+ now + app_secret;
		String sign = MD5Util.getKeyedDigest(signKey, "");
		params.put("app_key", app_key);
		params.put("method", method);
		params.put("timestamp", now);
		params.put("sign", sign.toUpperCase());
		params.put("data", array.toString());
		long time1=new Date().getTime();
		String result = HttpKit.post(url, params);
		long time2=new Date().getTime();
		System.out.println(time2-time1);
		System.out.println(result);
		JSONObject resultJson = JSONObject.fromObject(result);
		JSONObject dataJson = JSONObject.fromObject(resultJson
				.get("data"));
		//System.out.println(dataJson.get("result"));
		JSONObject resultObj = JSONObject.fromObject(dataJson
				.get("result"));
		JSONObject json = null;
		if (resultObj.get("23234") != null) {
			json = getResultJson(resultObj.getJSONArray("23234"));
		} 
		//System.out.println(json);
		json = null;
		if (resultObj.get("2323434") != null) {
			json = getResultJson(resultObj.getJSONArray("2323434"));
		} 
		//System.out.println(json);
	}
	
	public static void test2() throws UnsupportedEncodingException{
		String now = DateUtil.getNow();
		Map<String, Object> params = new HashMap<String, Object>();
		JSONArray array = new JSONArray();
//		for(int i=0;i<=49;i++){
			JSONObject object = new JSONObject();
			
			object.put("o_pcode", "D150409349146");
			object.put("t_orgcode", t_orgcode);
			object.put("t_tasktype", t_tasktype);
			object.put("o_consigneeaddr", "江苏省盐城市东台市溱东镇罗一村(电话通知)");
			
			array.add(object);
//		}
		String signKey = app_secret + "app_key" + app_key + "data"
				+ array.toString() + "method" + method + "timestamp"
				+ now + app_secret;
		String sign = MD5Util.getKeyedDigest(signKey, "");
		params.put("app_key", app_key);	
		params.put("method", method);
		params.put("timestamp", now);
		params.put("sign", sign.toUpperCase());
		params.put("data", array.toString());
		FileWriter outStream = null;
		try{
			outStream = new FileWriter("d:\\1.txt",true);
			long time1= System.currentTimeMillis();
			System.out.println("Gis接口调用开始时间:"+DateUtil.getNow());
			outStream.write(("Gis接口调用开始时间:"+DateUtil.getNow()+"\r\n"));
			String result = HttpKit.post(url, params);
			System.out.println("Gis接口调用结束时间:"+DateUtil.getNow());
			outStream.write(("Gis接口调用结束时间:"+DateUtil.getNow()+"\r\n"));
			System.out.println("Gis接口调用耗时:"+(System.currentTimeMillis()-time1));
			outStream.write(("Gis接口调用耗时:"+(System.currentTimeMillis()-time1)+"\r\n"));
			System.out.println(result);
			outStream.write(result);
			outStream.flush();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try {
				outStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
//		long time1= System.currentTimeMillis();
//		System.out.println("Gis接口调用开始时间:"+DateUtil.getNow());
//		String result = HttpKit.post(url, params);
//		System.out.println("Gis接口调用结束时间:"+DateUtil.getNow());
//		System.out.println("Gis接口调用耗时:"+(System.currentTimeMillis()-time1));
//		System.out.println(result);
	}
}

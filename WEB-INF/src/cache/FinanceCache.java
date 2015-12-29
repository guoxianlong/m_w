package cache;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import adultadmin.util.Arith;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * 说明：财务相关信息缓存
 * 
 * 创建日期：2013-4-25
 * 
 * 作者：liuruilan
 *
 */
public class FinanceCache {
	
	/**
	 * 运费收取规则
	 */
	public static List<Map<String,Map<String,String>>> ruleList = new ArrayList<Map<String,Map<String,String>>>();		
	
	/**
	 * 目的地运费不按所属省规则收取的城市id列表
	 */
	public static String [] citys;	
	
	/**
	 * 有偏远费的城市
	 */
	public static String[] remoteCitys;
	
	/**
	 * 省id-name对应map
	 */
	public static LinkedHashMap<Integer, String> provinceMap = new LinkedHashMap<Integer, String>();

	/**
	 * 城市id-name对应map
	 */
	public static LinkedHashMap<Integer, String> cityMap = new LinkedHashMap<Integer, String>();
	
	/**
	 * 县区id--name对应map
	 */
	public static LinkedHashMap<Integer, String> areaMap = new LinkedHashMap<Integer, String>();
	
	static{
		init();
	}
	
	static void clearAll(){
		citys = null;
		ruleList.clear();
		provinceMap.clear();
		cityMap.clear();
		areaMap.clear();
	}

	public static void init(){
		clearAll();
		
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE2);
		
		Statement stmt = null;
		ResultSet rs = null;
		Connection conn = null;
		String sql = ""; 
		try {
			conn = dbOp.getConn();
			stmt = conn.createStatement();
			
			// 取得运费规则数据
			Map<String,Map<String,String>> ruleMap0 = new HashMap<String, Map<String,String>>();
			Map<String,Map<String,String>> ruleMap1 = new HashMap<String, Map<String,String>>();
			//偏远运费规则
			Map<String,Map<String,String>> remoteFeeMap = new HashMap<String, Map<String,String>>();
			//退件费规则
			Map<String,Map<String,String>> untreadMap = new HashMap<String,Map<String,String>>();
			
			
			sql = "SELECT * FROM " + DbOperation.SCHEMA_WARE + "finance_express_province fep"
				+ " LEFT JOIN " + DbOperation.SCHEMA_WARE + "finance_express fe ON fep.fe_express_id = fe.express_id" 
				+ " LEFT JOIN " + DbOperation.SCHEMA_WARE + "finance_added_weight_special fs ON fep.id = fs.fep_id"; 
				//+ " WHERE fep.buy_mode = 0";
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Map<String,String> _map = new HashMap<String, String>();
				int addRule = rs.getInt("add_rule");
				String expressId = rs.getString("express_id");
				String balanceArea = rs.getString("balance_area_id");
				String destProvince = rs.getString("dest_province_id");
				String destCity = rs.getString("dest_city_id");
				String addStart = rs.getString("add_start");
				
				_map.put("expressId", expressId);
				_map.put("balanceArea", balanceArea);
				_map.put("destProvince", destProvince);
				_map.put("destCity", destCity);
				_map.put("firstWeight", rs.getString("first_weight"));
				_map.put("firstWeightPrice", rs.getString("first_weight_price"));
				_map.put("addedWeight", rs.getString("added_weight"));
				_map.put("addedWeightPrice", rs.getString("added_weight_price"));
				_map.put("addRule", "" + addRule);
				_map.put("discount", rs.getString("discount"));
				_map.put("expressName", rs.getString("express_name"));
				_map.put("insureRate", rs.getString("insure_rate"));
				_map.put("insurePriceRate", rs.getString("insure_price_rate"));
				_map.put("insureCritical", rs.getString("insure_critical"));
				_map.put("balanceMin", rs.getString("balance_min"));
				_map.put("balanceRate", rs.getString("balance_rate"));
				_map.put("mailingMin", rs.getString("mailing_min"));
				_map.put("mailingRate", rs.getString("mailing_rate"));
				_map.put("untreadMin", rs.getString("untread_min"));
				_map.put("untreadRate", rs.getString("untread_rate"));
				_map.put("addStart", addStart);
				_map.put("addEnd", rs.getString("add_end"));
				_map.put("specialAddedWeightPrice", rs.getString("special_added_weight_price"));
				String key = "";
					if(addRule == 0){
						key = expressId + "-" + balanceArea + "-" + destProvince + "-" + destCity;
						ruleMap0.put(key, _map);
					}
					if(addRule == 1){
						key = expressId + "-" + balanceArea + "-" + destProvince + "-" + destCity + "-" + addStart;
						ruleMap1.put(key, _map);
					}
			}
			
			//获得偏远运费
			rs = stmt.executeQuery("select * from "+DbOperation.SCHEMA_WARE+"remote_fee_express rfe where rfe.state = 0");
			while(rs.next()){
				Map<String,String> _map = new HashMap<String, String>();
				_map.put("expressId", rs.getString("express_id"));
				_map.put("sendAreaId",rs.getString("send_area_id"));
				_map.put("destProvinceId",rs.getString("dest_province_id"));
				_map.put("destCityId",rs.getString("dest_city_id"));
				_map.put("destAreaId",rs.getString("dest_area_id"));
				_map.put("buyMode",rs.getString("buy_mode"));
				_map.put("remoteFee",rs.getString("remote_fee"));
				StringBuilder key = new StringBuilder(100);
				key.append(rs.getString("express_id")).append("-").append(rs.getString("send_area_id")).append("-").append(rs.getString("dest_province_id")).append("-");
				key.append(rs.getString("dest_city_id")).append("-").append(rs.getString("dest_area_id"));
				remoteFeeMap.put(key.toString(),_map);
			}
			
			//获得退件费
			rs = stmt.executeQuery("select * from "+DbOperation.SCHEMA_WARE+"express_untread_info eui where eui.state = 0");
			while(rs.next()){
				Map<String,String> _map = new HashMap<String, String>();
				_map.put("expressId", rs.getString("express_id"));
				_map.put("provinceId",rs.getString("province_id"));
				_map.put("untreadFee",rs.getString("untread_fee"));
				_map.put("type",rs.getString("type"));
				StringBuilder key = new StringBuilder(100);
				key.append(rs.getString("express_id")).append("-").append(rs.getString("province_id"));
				untreadMap.put(key.toString(), _map);
			}
			ruleList.add(ruleMap0);
			ruleList.add(ruleMap1);
			ruleList.add(remoteFeeMap);
			ruleList.add(untreadMap);
			
			// 取得目的地有特殊城市的城市id
			sql = "SELECT DISTINCT dest_city_id FROM " + DbOperation.SCHEMA_WARE + "finance_express_province WHERE dest_city_id > 0";
			rs = stmt.executeQuery(sql);
			String str = "";
			while (rs.next()) {
				str += rs.getString("dest_city_id") + ",";
			}
		
			if(str.endsWith(",")){
				str = str.substring(0, str.length() - 1);
			}
			
			citys = str.split(",");
			
			//取得偏远的地区的目的城市
			sql = "SELECT DISTINCT dest_city_id from " + DbOperation.SCHEMA_WARE + "remote_fee_express";
			rs = stmt.executeQuery(sql);
			str = "";
			while(rs.next()){
				str += rs.getString("dest_city_id") + ",";
			}
			
			if(str.endsWith(",")){
				str = str.substring(0, str.length() - 1);
			}
			remoteCitys = str.split(",");
			// 省map
			sql = "SELECT id, name FROM " + DbOperation.SCHEMA_WARE + "provinces ORDER BY id";
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				provinceMap.put(rs.getInt("id"), rs.getString("name"));
			}
			
			// 市map
			sql = "SELECT id, city FROM " + DbOperation.SCHEMA_WARE + "province_city ORDER BY id";
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				cityMap.put(rs.getInt("id"), rs.getString("city"));
			}
			
			//县区map
			sql = "SELECT id,area FROM " + DbOperation.SCHEMA_WARE + "city_area ORDER BY id";
			rs = stmt.executeQuery(sql);
			while(rs.next()){
				areaMap.put(rs.getInt("id"), rs.getString("area"));
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
	}
	
	/**
	 * 运费计算并添加
	 * 
	 * @param argMap存储订单数据，key为：orderId、price、weight(单位：g)、express（MailingBalancebean里对应id）、
	 * 
	 * balanceArea（发货地点id）、destProvince（目的省id）、destCity（目的城市id）、destArea（目的区）,buyMode（购买方式）
	 * 
	 * @param dbOp为可修改连接
	 * 
	 * @return -1-发生错误，0-规则不存在，但不中止程序，1-正常
	 * 
	 * @throws SQLException 
	 */
	public static int addCharge(Map<String,String> argMap, DbOperation dbOp) throws SQLException{
		if(argMap == null){
			return -1;
		}

		String orderId = argMap.get("orderId");
		float price = StringUtil.toFloat(argMap.get("price"));
		float weight = StringUtil.toFloat(argMap.get("weight"));
		String express = argMap.get("express");//快递公司id
		String balanceArea = argMap.get("balanceArea");
		String destProvince = argMap.get("destProvince");
		String destArea = argMap.get("destArea");
		String _destCity = argMap.get("destCity");			//有可能是225这些特殊城市，有可能是其他值，不大可能是0、
		String destCity = citys == null ? "0" : (StringUtil.hasStrArray(citys, _destCity) ? _destCity : "0");	// 只能是225等特殊城市或0	
		//偏远费城市
		String remoteDestCity = remoteCitys == null ? "0" : (StringUtil.hasStrArray(remoteCitys, _destCity) ? _destCity : "0");
		
		int buyMode = StringUtil.StringToId(argMap.get("buyMode"));
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> chargeMap = new HashMap<String, String>();					// value为运费相关数据
		chargeMap.put("orderId", orderId);
		chargeMap.put("balanceType", express);
		String key = express + "-" + balanceArea + "-" + destProvince + "-" + destCity;
		int addRule = judgeMailingRule(dbOp, StringUtil.toInt(express), StringUtil.toInt(balanceArea),
				StringUtil.toInt(destProvince), StringUtil.toInt(destCity),StringUtil.toInt(remoteDestCity),StringUtil.toInt(destArea), buyMode);
		if(addRule == -1){
			return 0;
		}
		if(addRule == 1){		// 顺丰等续重规则比较特殊
			Iterator<Map<String,String>> iter = ruleList.get(1).values().iterator();
			while(iter.hasNext()){
				Map<String,String> _map = iter.next();
				if(_map == null){
					continue;
				}
				if(express.equals(_map.get("expressId")) 
						&& balanceArea.equals(_map.get("balanceArea"))
						&& destProvince.equals(_map.get("destProvince"))
						&& (destCity.equals(_map.get("destCity")) || "0".equals(_map.get("destCity")))){
					
					float carriage = StringUtil.toFloat(_map.get("firstWeightPrice"));
					if(weight > StringUtil.StringToId(_map.get("firstWeight"))){
						int _weight = StringUtil.StringToId(_map.get("firstWeight"));
						int addedWeight = StringUtil.StringToId(_map.get("addedWeight"));
						float specialAddedWeightPrice = StringUtil.toFloat(_map.get("specialAddedWeightPrice"));
						int addStart = StringUtil.StringToId(_map.get("addStart"));
						int addEnd = StringUtil.StringToId(_map.get("addEnd"));
						if(weight > addStart && (addEnd == 0 || weight <= addEnd)){
							double temp = Math.ceil(Arith.div(weight - _weight, addedWeight));
							carriage = Arith.add(carriage, Arith.mul(specialAddedWeightPrice, StringUtil.toFloat(""+temp)));
						}
						carriage = Arith.mul(carriage, StringUtil.toFloat(_map.get("discount")));
					}
					float insureCharge = 0;
					float insurePriceCharge = 0;
					if(price > StringUtil.toFloat(_map.get("insureCritical"))){
						insureCharge = Arith.mul(price, StringUtil.toFloat(_map.get("insureRate")));
						insurePriceCharge = Arith.round(Arith.mul(price, StringUtil.toFloat(_map.get("insurePriceRate"))), 0);
					}
					float balanceCharge =  Math.max(StringUtil.toFloat(_map.get("balanceMin")), Arith.mul(price, StringUtil.toFloat(_map.get("balanceRate"))));
					float mailingCharge = Math.max(StringUtil.toFloat(_map.get("mailingMin")), Arith.mul(price, StringUtil.toFloat(_map.get("mailingRate"))));
					//修改后计算退件费
					Map<String,Float> untreadMap = getExpressUntreadValue(Integer.parseInt(_map.get("expressId")),Integer.parseInt(_map.get("destProvince")));
					float untreatedCharge = Math.max(untreadMap.get("untreadMin"), Arith.mul(carriage,untreadMap.get("untreadRate")));
					//获取偏远费信息
					float remoteFee = getRemoteFee(argMap);
					
					chargeMap.put("carriage", "" + carriage);
					chargeMap.put("insureCharge", "" + insureCharge);
					chargeMap.put("insurePriceCharge", "" + insurePriceCharge);
					chargeMap.put("balanceCharge", "" + balanceCharge);
					chargeMap.put("mailingCharge", "" + mailingCharge);
					chargeMap.put("untreatedCharge", "" + untreatedCharge);
					chargeMap.put("remoteFee", "" + remoteFee);
					break;
				}
			}
		}else if(addRule == 0){							// 其他快递公司续重线性增长
			Map<String,Map<String,String>> ruleMap0 = ruleList.get(0);
			Map<String,String> _map = ruleMap0.get(key);
			float carriage = 0;
			if(_map != null){
				carriage = StringUtil.toFloat(_map.get("firstWeightPrice"));
				int _weight = StringUtil.StringToId(_map.get("firstWeight"));
				float addedWeightPrice = StringUtil.toFloat(_map.get("addedWeightPrice"));
				int addedWeight = StringUtil.StringToId(_map.get("addedWeight"));
				if(_weight > 0 && weight > _weight){
					double temp = Math.ceil(Arith.div(weight - _weight, addedWeight));
					carriage = Arith.add(carriage, Arith.mul(addedWeightPrice, StringUtil.toFloat(""+temp)));
				}
				carriage = Arith.mul(carriage, StringUtil.toFloat(_map.get("discount")));
				
				float insureCharge = 0;
				float insurePriceCharge = 0;
				if(price > StringUtil.toFloat(_map.get("insureCritical"))){
					insureCharge = Arith.mul(price, StringUtil.toFloat(_map.get("insureRate")));
					insurePriceCharge = Arith.mul(price, StringUtil.toFloat(_map.get("insurePriceRate")));
				}
				//if("8".equals(express) || "9".equals(express) || "11".equals(express)){
					insureCharge = Arith.round(insureCharge, 3);
				//}
				float balanceCharge =  Math.max(StringUtil.toFloat(_map.get("balanceMin")), Arith.mul(price, StringUtil.toFloat(_map.get("balanceRate"))));
				float mailingCharge = Math.max(StringUtil.toFloat(_map.get("mailingMin")), Arith.mul(price, StringUtil.toFloat(_map.get("mailingRate"))));
				
				//修改后计算退件费
				Map<String,Float> untreadMap = getExpressUntreadValue(Integer.parseInt(_map.get("expressId")),Integer.parseInt(_map.get("destProvince")));
				float untreatedCharge = Math.max(untreadMap.get("untreadMin"), Arith.mul(carriage,untreadMap.get("untreadRate")));
				//获取偏远费信息
				float remoteFee = getRemoteFee(argMap);
				
				chargeMap.put("carriage", "" + carriage);
				chargeMap.put("insureCharge", "" + insureCharge);
				chargeMap.put("insurePriceCharge", "" + insurePriceCharge);
				chargeMap.put("balanceCharge", "" + balanceCharge);
				chargeMap.put("mailingCharge", "" + mailingCharge);
				chargeMap.put("untreatedCharge", "" + untreatedCharge);
				chargeMap.put("remoteFee", "" + remoteFee);
			
			}
			
			
		
		}
		list.add(chargeMap);	// list中只有一个元素，为的是和批量方法相匹配
		
		return !execSqlBatch(list, dbOp) ? -1 : 1;
	}

	
	/**
	 * 运费计算并添加——批量
	 * 
	 * @param agrList存储订单数据argMap，key为：orderId、price、weight(单位：g)、express（MailingBalancebean里对应id）、
	 * 
	 * balanceArea（发货地点id）、destProvince（目的省id）、destCity（目的城市id）、buyMode（购买方式）
	 * 
	 * @param dbOp为可修改连接
	 */
	public static void addChargeBatch(List<Map<String,String>> argList, DbOperation dbOp) throws SQLException{
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		for (int i = 0; i < argList.size(); i++) {
			Map<String,String> argMap = argList.get(i);
			if(argMap == null){
				continue;
			}
			
			String orderId = argMap.get("orderId");
			float price = StringUtil.toFloat(argMap.get("price"));
			float weight = StringUtil.toFloat(argMap.get("weight"));
			String express = argMap.get("express");
			String balanceArea = argMap.get("balanceArea");
			String destProvince = argMap.get("destProvince");
			String destArea = argMap.get("destArea");
			String _destCity = argMap.get("destCity");			//有可能是225这些特殊城市，有可能是其他值，不大可能是0、
			String destCity = citys == null ? "0" : (StringUtil.hasStrArray(citys, _destCity) ? _destCity : "0");	// 只能是225等特殊城市或0	
			//偏远费城市
			String remoteDestCity = remoteCitys == null ? "0" : (StringUtil.hasStrArray(remoteCitys, _destCity) ? _destCity : "0");
			
			int buyMode = StringUtil.StringToId(argMap.get("buyMode"));
			
			Map<String,String> chargeMap = new HashMap<String, String>();					// value为运费相关数据
			chargeMap.put("orderId", orderId);
			chargeMap.put("balanceType", express);
			String key = express + "-" + balanceArea + "-" + destProvince + "-" + destCity;
			int addRule = judgeMailingRule(dbOp, StringUtil.toInt(express), StringUtil.toInt(balanceArea),
					StringUtil.toInt(destProvince), StringUtil.toInt(destCity),StringUtil.toInt(remoteDestCity),StringUtil.toInt(destArea), buyMode);
			if(addRule == -1){
				continue;
			}
			if(addRule == 1){		// 顺丰续重规则比较特殊
				Iterator<Map<String,String>> iter = ruleList.get(1).values().iterator();
				while(iter.hasNext()){
					Map<String,String> _map = iter.next();
					if(_map == null){
						continue;
					}
					if(express.equals(_map.get("expressId")) 
							&& balanceArea.equals(_map.get("balanceArea"))
							&& destProvince.equals(_map.get("destProvince"))
							&& (destCity.equals(_map.get("destCity")) || "0".equals(_map.get("destCity")))){
						
						float carriage = StringUtil.toFloat(_map.get("firstWeightPrice"));
						if(weight > StringUtil.StringToId(_map.get("firstWeight"))){
							int _weight = StringUtil.StringToId(_map.get("firstWeight"));
							int addedWeight = StringUtil.StringToId(_map.get("addedWeight"));
							float specialAddedWeightPrice = StringUtil.toFloat(_map.get("specialAddedWeightPrice"));
							int addStart = StringUtil.StringToId(_map.get("addStart"));
							int addEnd = StringUtil.StringToId(_map.get("addEnd"));
							if(weight > addStart && (addEnd == 0 || weight <= addEnd)){
								double temp = Math.ceil(Arith.div(weight - _weight, addedWeight));
								carriage = Arith.add(carriage, Arith.mul(specialAddedWeightPrice, StringUtil.toFloat(""+temp)));
							}
							carriage = Arith.mul(carriage, StringUtil.toFloat(_map.get("discount")));
						}
						float insureCharge = 0;
						float insurePriceCharge = 0;
						if(price > StringUtil.toFloat(_map.get("insureCritical"))){
							insureCharge = Arith.mul(price, StringUtil.toFloat(_map.get("insureRate")));
							insurePriceCharge = Arith.round(Arith.mul(price, StringUtil.toFloat(_map.get("insurePriceRate"))), 0);
						}
						float balanceCharge =  Math.max(StringUtil.toFloat(_map.get("balanceMin")), Arith.mul(price, StringUtil.toFloat(_map.get("balanceRate"))));
						float mailingCharge = Math.max(StringUtil.toFloat(_map.get("mailingMin")), Arith.mul(price, StringUtil.toFloat(_map.get("mailingRate"))));
						
						//修改后计算退件费
						Map<String,Float> untreadMap = getExpressUntreadValue(Integer.parseInt(_map.get("expressId")),Integer.parseInt(_map.get("destProvince")));
						float untreatedCharge = Math.max(untreadMap.get("untreadMin"), Arith.mul(carriage,untreadMap.get("untreadRate")));
						//获取偏远费信息
						float remoteFee = getRemoteFee(argMap);
						
						chargeMap.put("carriage", "" + carriage);
						chargeMap.put("insureCharge", "" + insureCharge);
						chargeMap.put("insurePriceCharge", "" + insurePriceCharge);
						chargeMap.put("balanceCharge", "" + balanceCharge);
						chargeMap.put("mailingCharge", "" + mailingCharge);
						chargeMap.put("untreatedCharge", "" + untreatedCharge);
						chargeMap.put("remoteFee", "" + remoteFee);
						break;
					}
				}
			}else if(addRule == 0){							// 其他快递公司续重线性增长
				Map<String,Map<String,String>> ruleMap0 = ruleList.get(0);
				Map<String,String> _map = ruleMap0.get(key);
				float carriage = 0;
				if(_map != null){
				    carriage = StringUtil.toFloat(_map.get("firstWeightPrice"));
					int _weight = StringUtil.StringToId(_map.get("firstWeight"));
					float addedWeightPrice = StringUtil.toFloat(_map.get("addedWeightPrice"));
					int addedWeight = StringUtil.StringToId(_map.get("addedWeight"));
					if(_weight > 0 && weight > _weight){
						double temp = Math.ceil(Arith.div(weight - _weight, addedWeight));
						carriage = Arith.add(carriage, Arith.mul(addedWeightPrice, StringUtil.toFloat(""+temp)));
					}
					carriage = Arith.mul(carriage, StringUtil.toFloat(_map.get("discount")));
					
					float insureCharge = 0;
					float insurePriceCharge = 0;
					if(price > StringUtil.toFloat(_map.get("insureCritical"))){
						insureCharge = Arith.mul(price, StringUtil.toFloat(_map.get("insureRate")));
						insurePriceCharge = Arith.mul(price, StringUtil.toFloat(_map.get("insurePriceRate")));
					}
					//if("8".equals(express) || "9".equals(express) || "11".equals(express)){
						insureCharge = Arith.round(insureCharge, 3);
					//}
					float balanceCharge =  Math.max(StringUtil.toFloat(_map.get("balanceMin")), Arith.mul(price, StringUtil.toFloat(_map.get("balanceRate"))));
					float mailingCharge = Math.max(StringUtil.toFloat(_map.get("mailingMin")), Arith.mul(price, StringUtil.toFloat(_map.get("mailingRate"))));
					//修改后计算退件费
					Map<String,Float> untreadMap = getExpressUntreadValue(Integer.parseInt(_map.get("expressId")),Integer.parseInt(_map.get("destProvince")));
					float untreatedCharge = Math.max(untreadMap.get("untreadMin"), Arith.mul(carriage,untreadMap.get("untreadRate")));
					//获取偏远费信息
					float remoteFee = getRemoteFee(argMap);
					
					chargeMap.put("carriage", "" + carriage);
					chargeMap.put("insureCharge", "" + insureCharge);
					chargeMap.put("insurePriceCharge", "" + insurePriceCharge);
					chargeMap.put("balanceCharge", "" + balanceCharge);
					chargeMap.put("mailingCharge", "" + mailingCharge);

					chargeMap.put("untreatedCharge", "" + untreatedCharge);
					chargeMap.put("remoteFee", "" + remoteFee);
					
				}
				
			}
			list.add(chargeMap);
		}
		execSqlBatch(list, dbOp);
		
	}
	
	/**
	 * 批量插入数据库
	 */
	public static boolean execSqlBatch(List<Map<String,String>> list, DbOperation dbOp){
		boolean tag = true;
		Connection conn = null; 
        PreparedStatement ps = null; 
        try { 
            conn = dbOp.getConn();
            String sql = "INSERT INTO " + DbOperation.SCHEMA_WARE + "mailing_balance_estimated_charge (order_id,balance_type,carriage,insure_charge,insure_price_charge," +
            		" balance_charge,mailing_charge,untread_charge,remote_fee) VALUES (?,?,?,?,?,?,?,?,?) " + 
            		" ON DUPLICATE KEY UPDATE order_id=?,balance_type=?,carriage=?,insure_charge=?," +
            		" insure_price_charge=?,balance_charge=?,mailing_charge=?,untread_charge=?,remote_fee=?";
            ps = conn.prepareStatement(sql); 
            for (int i = 0; i < list.size(); i++) { 
            	Map<String,String> map = list.get(i);
            	if(map != null){
            		ps.setInt(1, StringUtil.StringToId(map.get("orderId")));
            		ps.setInt(2, StringUtil.StringToId(map.get("balanceType")));
            		ps.setFloat(3, StringUtil.toFloat(map.get("carriage")));
            		ps.setFloat(4, StringUtil.toFloat(map.get("insureCharge")));
            		ps.setFloat(5, StringUtil.toFloat(map.get("insurePriceCharge")));
            		ps.setFloat(6, StringUtil.toFloat(map.get("balanceCharge")));
            		ps.setFloat(7, StringUtil.toFloat(map.get("mailingCharge")));
            		ps.setFloat(8, StringUtil.toFloat(map.get("untreatedCharge")));
            		ps.setFloat(9, StringUtil.toFloat(map.get("remoteFee")));
            		
            		ps.setInt(10, StringUtil.StringToId(map.get("orderId")));
            		ps.setInt(11, StringUtil.StringToId(map.get("balanceType")));
            		ps.setFloat(12, StringUtil.toFloat(map.get("carriage")));
            		ps.setFloat(13, StringUtil.toFloat(map.get("insureCharge")));
            		ps.setFloat(14, StringUtil.toFloat(map.get("insurePriceCharge")));
            		ps.setFloat(15, StringUtil.toFloat(map.get("balanceCharge")));
            		ps.setFloat(16, StringUtil.toFloat(map.get("mailingCharge")));
            		ps.setFloat(17, StringUtil.toFloat(map.get("untreatedCharge")));
            		ps.setFloat(18, StringUtil.toFloat(map.get("remoteFee")));
            		
            		ps.addBatch(); 
            	}
            } 
            int[] result = ps.executeBatch(); 
            for (int i = 0; i < result.length; i++) {
				if(result[i] < 0){
					tag = false;
					break;
				}
			}
        } catch (Exception e) {
        	e.printStackTrace();
        	return false;
		}
        return tag;
	}
	
	/**
	 * 判断物流成本规则表中是否有此规则
	 * @param dbOp
	 * @param express
	 * @param balanceArea	结算地（结算导入前取发货地）
	 * @param destProvince
	 * @param destCity
	 * @param buyMode
	 * @return	返回-1-无此规则,0-规则0,1-规则1
	 * @throws SQLException
	 */
	public static int judgeMailingRule(DbOperation dbOp, int express,
			int balanceArea, int destProvince, int destCity, int remoteDestCity, int destArea,
			int buyMode) throws SQLException {
		int addRule = 0;				// 续重规则
		int _destCity = citys == null ? 0 : (StringUtil.hasStrArray(citys, destCity+"") ? destCity : 0);	// 只能是225等特殊城市或0	
		String sql = "SELECT add_rule,buy_mode FROM " + DbOperation.SCHEMA_WARE + "finance_express_province " + 
		" WHERE fe_express_id = " + express +
		" AND balance_area_id = " + balanceArea +
		" AND dest_province_id = " + destProvince +
		" AND dest_city_id = " + _destCity;
		ResultSet rs = dbOp.getConn().createStatement().executeQuery(sql);
		if(!rs.next()){
			
			//看偏远费
			StringBuilder key = new StringBuilder(50);
			key.append(express).append("-").append(balanceArea).append("-").append(destProvince).append("-");
			key.append(remoteDestCity).append("-").append(destArea);
			Map<String,String> remoteFeeMap = FinanceCache.ruleList.get(2).get(key.toString());
			  
			 if((!("".equals("destArea") || "0".equals("destArea"))) && remoteFeeMap == null){
				 key.delete(key.lastIndexOf("-"),key.length());
				 key.append("-0");
				 remoteFeeMap = FinanceCache.ruleList.get(2).get(key.toString());
			 }
			 
			//看退件费
			 StringBuilder key_untread = new StringBuilder();
			 key_untread.append(express).append("-").append(destProvince);
			 Map<String,String> untreadMap = FinanceCache.ruleList.get(3).get(key_untread.toString());
				 
			 if(untreadMap != null || remoteFeeMap != null ){
				 return 0;
			  }  
			
			return -1;
		}
		int _buyMode = rs.getInt("buy_mode");
		if(_buyMode != 99 && _buyMode != buyMode){
			return -1;
		}
		addRule = rs.getInt("add_rule");
		
		return addRule;
	}

	/**
	 * 
	 * @description: 获取退件费
	 * @param expressId 快递企业id
	 * @param provinceId 目的省id
	 * @return
	 * @returnType: Map<String,Float>
	 * @create:2013-10-17 下午04:24:12
	 * @author:hanquan
	 */
	public static Map<String,Float> getExpressUntreadValue(int expressId,int provinceId){
		
		 Map<String,Float> map = new HashMap<String,Float>();
		 map.put("untreadRate", (float)0.0);
		 map.put("untreadMin", (float)0.0);
//		 ExpressUntreadInfoService euis = new ExpressUntreadInfoService();
//		 ExpressUntreadInfoBean eui = euis.getExpressUntreadInfo(expressId, provinceId);
		 StringBuilder key = new StringBuilder();
		 key.append(expressId).append("-").append(provinceId);
		 Map<String,String> untreadMap = FinanceCache.ruleList.get(3).get(key.toString());
		 
		 
		 if(untreadMap != null){
			 map = new HashMap<String,Float>();
			 if(Integer.parseInt(untreadMap.get("type")) == 0){
				 map.put("untreadRate", Arith.div(Float.parseFloat(untreadMap.get("untreadFee")),100));
				 map.put("untreadMin", (float)0.0);
			 }else{
				 map.put("untreadMin", Float.parseFloat(untreadMap.get("untreadFee")));
				 map.put("untreadRate", (float)0.0);
			 }
		 }
		 return map;
		 
	}
	 
	
	/**
	 * 
	 * @description:获取快递企业应收取的偏远费
	 * @param map
	 * @return
	 * @returnType: float
	 * @create:2013-10-17 下午06:56:24
	 * @author:hanquan
	 */
		public static float getRemoteFee(Map<String,String> map){
		  float value = 0;
//		  RemoteFeeExpressService rfes = new RemoteFeeExpressService();
//		  RemoteFeeExpressBean rfeb = rfes.getRemoteBean(map);
		  String express = map.get("express");//快递公司id
		  String balanceArea = map.get("balanceArea");//发件地
		  String destProvince = map.get("destProvince");//收件省
		  String destCity = map.get("destCity");//收件市
		  String destArea = map.get("destArea");//收件区县
		  StringBuilder key = new StringBuilder(50);
		
		  key.append(express).append("-").append(balanceArea).append("-").append(destProvince).append("-");
		  key.append(destCity).append("-").append(destArea);
		  Map<String,String> remoteFeeMap = FinanceCache.ruleList.get(2).get(key.toString());
		  
		  
		  
		 if(!("".equals("destArea") || "0".equals("destArea")) && remoteFeeMap == null){
			 key.delete(key.lastIndexOf("-"),key.length());
			 key.append("-0");
			 remoteFeeMap = FinanceCache.ruleList.get(2).get(key.toString());
		 }
		  
		  if(remoteFeeMap != null){
			  if(map.get("buyMode").equals(remoteFeeMap.get("buyMode")) || "99".equals(remoteFeeMap.get("buyMode")) ){
				  value = Float.parseFloat(remoteFeeMap.get("remoteFee"));
			  }
		  }
		  return value;
		 
	}
	
	
}

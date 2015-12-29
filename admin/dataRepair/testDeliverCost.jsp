<%@ page contentType="text/html;charset=utf-8" %>
<%@page import="java.util.*,java.sql.*"%>
<%@page import="adultadmin.bean.stock.*,adultadmin.bean.balance.*,cache.*"%>
<%@page import="adultadmin.bean.order.*"%>
<%@page import="adultadmin.util.db.*,adultadmin.service.infc.*,adultadmin.service.*,adultadmin.util.*"%>

<%
	String orderCode = request.getParameter("code");

//估算各项物流成本
	DbOperation dbOp = new DbOperation();
	dbOp.init("adult_slave");
	IStockService service = ServiceFactory.createStockService(IBaseService.CONN_IN_SERVICE, dbOp);
	IBalanceService baService = ServiceFactory.createBalanceService(IBaseService.CONN_IN_SERVICE, service.getDbOp());
	
	try{
		MailingBalanceBean mb = baService.getMailingBalance("order_code = '"+orderCode+"'");
		AuditPackageBean pBean = service.getAuditPackage("order_id =" + mb.getOrderId());
		ResultSet rs = service.getDbOp().executeQuery("SELECT add_id1,add_id2,add_id3 FROM user_order_extend_info WHERE order_code = '" + orderCode + "'");
		int addrSub1 = 0;
		int addrSub2 = 0;
		if(rs.next()){
			addrSub1 = rs.getInt(1);
			addrSub2 = rs.getInt(2);
		}
		
		Map<String,String> argMap = new HashMap<String, String>();
		argMap.put("orderId", ""+mb.getOrderId());
		argMap.put("price", ""+mb.getPrice());
		argMap.put("weight", ""+pBean.getWeight());
		argMap.put("express", ""+mb.getBalanceType());
		argMap.put("balanceArea", ""+pBean.getAreano());
		argMap.put("destProvince", ""+addrSub1);
		argMap.put("destCity", ""+addrSub2);
		argMap.put("buyMode", ""+mb.buyMode);
		//当抽取为接口的时候需要注意仓储调用和财务调用为一个方法，需要区分。因仓储有快递公司到财务快递公司的对应。
		//FinanceCache.addCharge(argMap, service.getDbOp());
		

		String orderId = argMap.get("orderId");
		float price = StringUtil.toFloat(argMap.get("price"));
		float weight = StringUtil.toFloat(argMap.get("weight"));
		String express = argMap.get("express");//快递公司id
		String balanceArea = argMap.get("balanceArea");
		String destProvince = argMap.get("destProvince");
		String _destCity = argMap.get("destCity");			//有可能是225这些特殊城市，有可能是其他值，不大可能是0、
		String destCity = FinanceCache.citys == null ? "0" : (StringUtil.hasStrArray(FinanceCache.citys, _destCity) ? _destCity : "0");	// 只能是225等特殊城市或0	
		int buyMode = StringUtil.StringToId(argMap.get("buyMode"));
		
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> chargeMap = new HashMap<String, String>();					// value为运费相关数据
		chargeMap.put("orderId", orderId);
		chargeMap.put("balanceType", express);
		String key = express + "-" + balanceArea + "-" + destProvince + "-" + destCity;
		int addRule = FinanceCache.judgeMailingRule(dbOp, StringUtil.toInt(express), StringUtil.toInt(balanceArea),
				StringUtil.toInt(destProvince), StringUtil.toInt(destCity), buyMode);

		if(addRule == 1){		// 顺丰等续重规则比较特殊
			Iterator<Map<String,String>> iter = FinanceCache.ruleList.get(1).values().iterator();
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
					float untreatedCharge = Math.max(StringUtil.toFloat(_map.get("untreadMin")), Arith.mul(carriage, StringUtil.toFloat(_map.get("untreadRate"))));
					
					chargeMap.put("carriage", "" + carriage);
					chargeMap.put("insureCharge", "" + insureCharge);
					chargeMap.put("insurePriceCharge", "" + insurePriceCharge);
					chargeMap.put("balanceCharge", "" + balanceCharge);
					chargeMap.put("mailingCharge", "" + mailingCharge);
					chargeMap.put("untreatedCharge", "" + untreatedCharge);
					break;
				}
			}
		}else if(addRule == 0){							// 其他快递公司续重线性增长
			Map<String,Map<String,String>> ruleMap0 = FinanceCache.ruleList.get(0);
			Map<String,String> _map = ruleMap0.get(key);
			if(_map != null){
				float carriage = StringUtil.toFloat(_map.get("firstWeightPrice"));
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
				float untreatedCharge = Math.max(StringUtil.toFloat(_map.get("untreadMin")), Arith.mul(carriage, StringUtil.toFloat(_map.get("untreadRate"))));
			
				chargeMap.put("carriage", "" + carriage);
				chargeMap.put("insureCharge", "" + insureCharge);
				chargeMap.put("insurePriceCharge", "" + insurePriceCharge);
				chargeMap.put("balanceCharge", "" + balanceCharge);
				chargeMap.put("mailingCharge", "" + mailingCharge);
				chargeMap.put("untreatedCharge", "" + untreatedCharge);
			}
		}

%>
预计运费：<%=chargeMap.get("carriage") %>
<%
		
	}catch(Exception e){
		e.printStackTrace();
	}finally{
		service.releaseAll();
	}
%>
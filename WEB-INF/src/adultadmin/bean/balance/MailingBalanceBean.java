/*
 * Created on 2009-9-25
 *
 */
package adultadmin.bean.balance;

import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

import adultadmin.util.db.DbOperation;

public class MailingBalanceBean {

	/**
	 * 妥投已结算<br/>
	 * 跟“导入包裹单号”功能里面的 操作方式 一起编号， 使用相同的操作日志来记录操作信息
	 */
	public static int IMPORT_TYPE_MAILING = 14;
	/**
	 * 退回已结算<br/>
	 * 跟“导入包裹单号”功能里面的 操作方式 一起编号， 使用相同的操作日志来记录操作信息
	 */
	public static int IMPORT_TYPE_UNTREAD = 15;

	/**
	 * 结算状态：还没有导入包裹单号
	 */
	public static int BALANCE_STATUS_UNDEFINE = 0;
	/**	
	 * 结算状态：已导入包裹单号，没有导入结算数据
	 */
	public static int BALANCE_STATUS_UNDEAL = 1;
	/**
	 * 结算状态：妥投已结算
	 */
	public static int BALANCE_STATUS_MAILING = 2;
	/**
	 * 结算状态：退回已结算
	 */
	public static int BALANCE_STATUS_UNTREAD = 3;
	
	
	/**
	 * 结算来源：北速
	 */
	public static int BALANCE_TYPE_BS = 1;
	/**
	 * 结算来源：广速省内
	 */
	public static int BALANCE_TYPE_GSSN = 2;
	/**
	 * 结算来源：广州宅急送
	 */
	public static int BALANCE_TYPE_GZZJS = 3;
	/**
	 * 结算来源：广速省外
	 */
	public static int BALANCE_TYPE_GSSW = 4;

	/**
	 * 结算来源：广东省速递局
	 */
	public static int BALANCE_TYPE_GDSSDJ = 5;

	/**
	 * 结算来源： 广州顺丰
	 */
	public static int BALANCE_TYPE_GDSF = 6;
	
	/**
	 * 结算来源： 深圳自建
	 */
	public static int BALANCE_TYPE_SZZJ = 7;
	
	/**
	 * 结算来源： 通路速递
	 */
	public static int BALANCE_TYPE_TLSD = 8;
	
	/**
	 * 结算来源： 赛奥递
	 */
	public static int BALANCE_TYPE_SAD = 9;
	
	/**
	 * 结算来源： 如风达
	 */
	public static int BALANCE_TYPE_RFD = 10;
	
	/**
	 * 结算来源：通路速递广东
	 */
	public static int BALANCE_TYPE_TLSDGD = 11;
	
//	/**
//	 * 结算来源：通路速递浙江
//	 */
//	public static int BALANCE_TYPE_TLSDZJ = 12;
	
	/**
	 * 结算来源：银捷速递
	 */
	public static int BALANCE_TYPE_YJSD = 13;
	
	
	
	/**
	 * 结算来源：重庆华宇
	 */
	public static int BALANCE_TYPE_CQHY = 14;
	
	/**
	 * 结算来源：四川立即送
	 */
	public static int BALANCE_TYPE_SCLJS = 15;
	
	/**
	 * 结算来源：广西邮政
	 */
	public static int BALANCE_TYPE_GXYZ = 16;
	
	/**
	 * 结算来源：宅急送四川
	 */
	public static int BALANCE_TYPE_ZJSSC = 17;
	
	/**
	 * 结算来源：宅急送重庆
	 */
	public static int BALANCE_TYPE_ZJSCQ = 18;
	
	/**
	 * 结算来源：江西邮政
	 */
	public static int BALANCE_TYPE_JXYZ = 19;
	
	/**
	 * 结算来源：上海无疆
	 */
	public static int BALANCE_TYPE_SHWJ = 20;
	
	/**
	 * 结算来源：上海宅急送
	 */
	public static int BALANCE_TYPE_SHZJS = 21;
	
	/**
	 * 结算来源：湖南邮政
	 */
	public static int BALANCE_TYPE_HNYZ = 22;
	
	/**
	 * 结算来源：湖北邮政
	 */
	public static int BALANCE_TYPE_HBYZ = 23;
	
	/**
	 * 结算来源：飞狐快递
	 */
	public static int BALANCE_TYPE_FHKD = 24;
	
	/**
	 * 结算来源：陕西邮政
	 */
	public static int BALANCE_TYPE_SXYZ = 25;
	
	/**
	 * 结算信息复核状态： 结算信息还未导入，只是一个空的结算信息记录
	 */
	public static int BALANCE_NOIMPORT = 0;
	/**
	 * 结算信息复核状态： 结算信息已提交，财务未核对或核对未通过
	 */
	public static int BALANCE_NOCHECK = 1;
	/**
	 * 结算信息复核状态： 结算信息已核对通过
	 */
	public static int BALANCE_CHECKED = 2;
	
	/**
	 * 结算单对应订单的订单发货状态:已确认
	 * @see adultadmin.bean.order.OrderStockBean#STATUS3
	 */
	public static final int STOCKOUT_STATUS2 = 2;
	
	/**
	 * 导入方式Map
	 */
	public static Map importTypeMap = new LinkedHashMap();
	/**
	 * 结算来源Map
	 */
	public static Map balanceTypeMap = new LinkedHashMap();
	
	
	static {
		importTypeMap.put(Integer.valueOf(MailingBalanceBean.IMPORT_TYPE_MAILING), "妥投已结");
		importTypeMap.put(Integer.valueOf(MailingBalanceBean.IMPORT_TYPE_UNTREAD), "退单已结");

////		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_BS), "北速");
////		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_GSSN), "广速省内");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_GSSW), "广速省外");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_GZZJS), "广宅");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_GDSSDJ), "广东省速递局");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_GDSF), "广州顺丰");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_SZZJ), "深圳自建");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_TLSD), "通路速递");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_SAD), "赛澳递");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_RFD), "如风达");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_TLSDGD), "通路速递广东");
//		//balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_TLSDZJ), "通路速递浙江");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_YJSD), "银捷速递");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_CQHY), "重庆华宇");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_SCLJS), "四川立即送");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_GXYZ), "广西邮政");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_ZJSSC), "宅急送四川");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_ZJSCQ), "宅急送重庆");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_JXYZ), "江西邮政");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_SHWJ), "上海无疆");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_SHZJS), "上海宅急送");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_HNYZ), "湖南邮政");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_HBYZ), "湖北邮政");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_FHKD), "飞狐快递");
//		balanceTypeMap.put(Integer.valueOf(MailingBalanceBean.BALANCE_TYPE_SXYZ), "陕西邮政");
		
	}
	
	/**
	 * 初始化结算来源
	 */
	static{
		if(balanceTypeMap.size()==0){
			initBalanceTypeMap();
		}
	}
	
	/**
	 * 初始化结算来源
	 */
	public static void initBalanceTypeMap(){
		balanceTypeMap.clear();
		DbOperation dbOp=new DbOperation();
		dbOp.init("adult_slave");
		try{
			String balanceTypeMapsql="select id,name from balance_corp_info";
			ResultSet rs=dbOp.executeQuery(balanceTypeMapsql);
			while(rs.next()){
				int id=rs.getInt(1);
				String name=rs.getString(2);
				balanceTypeMap.put(Integer.valueOf(id), name);
			}
			rs.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	}
	
	public int id;

	/**
	 * 结算单对应的订单Id
	 */
	public int orderId;

	/**
	 * 结算单对应的订单编号
	 */
	public String orderCode;

	/**
	 * 结算单对应订单的发货时间
	 */
	public String stockoutDatetime;

	/**
	 * 结算单对应订单的订单发货状态
	 * 订单的发货、退货，都会对这个状态造成影响
	 */
	public int stockoutStatus;

	/**
	 * 结算单对应订单的创建时间
	 */
	public String orderCreateDatetime;

	/**
	 * 收货人姓名
	 */
	public String name;

	/**
	 * 包裹单号
	 */
	public String packagenum;

	/**
	 * 订单金额
	 */
	public float price;

	/**
	 * 成品重量（单位：g）
	 */
	public int weight;

	/**
	 * 运费
	 */
	public float carriage;

	/**
	 * 妥投费
	 */
	public float mailingCharge;

	/**
	 * 退回费
	 */
	public float untreadCharge;

	/**
	 * 结算费
	 */
	public float balanceCharge;

	/**
	 * 保险费
	 */
	public float insureCharge;

	/**
	 * 单册费
	 */
	public float billsCharge;
	
	/**
	 * 保价费
	 */
	public float insurePriceCharge;

	/**
	 * 物流成本<br/>
	 * 物流成本=运费+妥投费+退回费+结算费+保险费+单册费
	 */
	public float mailingCost;

	/**
	 * 结算来源
	 */
	public int balanceType;

	/**
	 * 结算周期起始日期
	 */
	public String balanceCycleStart;

	/**
	 * 结算周期结束日期
	 */
	public String balanceCycleEnd;

	/**
	 * 结算周期，（格式：08-09-05~08-09-15）
	 */
	public String balanceCycle;

	/**
	 * 结算时间点
	 */
	public String balanceDate;

	/**
	 * 实结时间
	 */
	public String balanceRealtime;

	/**
	 * 结算状态
	 */
	public int balanceStatus;

	/**
	 * 是否符合<br/>
	 * 0：未复核<br/>
	 * 1：已复核<br/>
	 */
	public int balanceCheck;

	/**
	 * 导入操作的类型<br/>
	 */
	public int importType;
	
	/**
	 * 订单购买类型
	 */
	public int buyMode;
	
	/**
	 * 结算数据确认ID
	 */
	public int mailingBalanceAuditingId;
	
	/**
	 * 结算地点：0-北库，1-芳村，2-广速，3-增城，4-无锡
	 */
	public int balanceArea;

	public float getBalanceCharge() {
		return balanceCharge;
	}

	public void setBalanceCharge(float balanceCharge) {
		this.balanceCharge = balanceCharge;
	}

	public String getBalanceCycle() {
		return balanceCycle;
	}

	public void setBalanceCycle(String balanceCycle) {
		this.balanceCycle = balanceCycle;
	}

	public String getBalanceCycleEnd() {
		return balanceCycleEnd;
	}

	public void setBalanceCycleEnd(String balanceCycleEnd) {
		this.balanceCycleEnd = balanceCycleEnd;
	}

	public String getBalanceCycleStart() {
		return balanceCycleStart;
	}

	public void setBalanceCycleStart(String balanceCycleStart) {
		this.balanceCycleStart = balanceCycleStart;
	}

	public String getBalanceDate() {
		return balanceDate;
	}

	public void setBalanceDate(String balanceDate) {
		this.balanceDate = balanceDate;
	}

	public String getBalanceRealtime() {
		return balanceRealtime;
	}

	public void setBalanceRealtime(String balanceRealtime) {
		this.balanceRealtime = balanceRealtime;
	}

	public int getBalanceStatus() {
		return balanceStatus;
	}

	public void setBalanceStatus(int balanceStatus) {
		this.balanceStatus = balanceStatus;
	}

	public int getBalanceType() {
		return balanceType;
	}

	public void setBalanceType(int balanceType) {
		this.balanceType = balanceType;
	}

	public float getBillsCharge() {
		return billsCharge;
	}

	public void setBillsCharge(float billsCharge) {
		this.billsCharge = billsCharge;
	}

	public float getCarriage() {
		return carriage;
	}

	public void setCarriage(float carriage) {
		this.carriage = carriage;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getInsureCharge() {
		return insureCharge;
	}

	public void setInsureCharge(float insureCharge) {
		this.insureCharge = insureCharge;
	}

	public float getMailingCharge() {
		return mailingCharge;
	}

	public void setMailingCharge(float mailingCharge) {
		this.mailingCharge = mailingCharge;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOrderCreateDatetime() {
		return orderCreateDatetime;
	}

	public void setOrderCreateDatetime(String orderCreateDatetime) {
		this.orderCreateDatetime = orderCreateDatetime;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public String getPackagenum() {
		return packagenum;
	}

	public void setPackagenum(String packagenum) {
		this.packagenum = packagenum;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public String getStockoutDatetime() {
		return stockoutDatetime;
	}

	public void setStockoutDatetime(String stockoutDatetime) {
		this.stockoutDatetime = stockoutDatetime;
	}

	public float getUntreadCharge() {
		return untreadCharge;
	}

	public void setUntreadCharge(float untreadCharge) {
		this.untreadCharge = untreadCharge;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getBalanceCheck() {
		return balanceCheck;
	}

	public void setBalanceCheck(int balanceCheck) {
		this.balanceCheck = balanceCheck;
	}

	public static Map getImportTypeMap() {
		return importTypeMap;
	}

	public static void setImportTypeMap(Map importTypeMap) {
		MailingBalanceBean.importTypeMap = importTypeMap;
	}
	
	public float getMailingCost() {
		return mailingCost;
	}

	public void setMailingCost(float mailingCost) {
		this.mailingCost = mailingCost;
	}

	public int getStockoutStatus() {
		return stockoutStatus;
	}

	public void setStockoutStatus(int stockoutStatus) {
		this.stockoutStatus = stockoutStatus;
	}

	public int getImportType() {
		return importType;
	}

	public void setImportType(int importType) {
		this.importType = importType;
	}

	public static Map getBalanceTypeMap() {
		return balanceTypeMap;
	}

	public int getMailingBalanceAuditingId() {
		return mailingBalanceAuditingId;
	}

	public void setMailingBalanceAuditingId(int mailingBalanceAuditingId) {
		this.mailingBalanceAuditingId = mailingBalanceAuditingId;
	}

	public int getBuyMode() {
		return buyMode;
	}

	public void setBuyMode(int buyMode) {
		this.buyMode = buyMode;
	}

	public float getInsurePriceCharge() {
		return insurePriceCharge;
	}

	public void setInsurePriceCharge(float insurePriceCharge) {
		this.insurePriceCharge = insurePriceCharge;
	}
	
	public int getBalanceArea() {
		return balanceArea;
	}

	public void setBalanceArea(int balanceArea) {
		this.balanceArea = balanceArea;
	}

	public static String getBalanceTypeName(int balanceType){
		String balanceTypeName = "";
		if(balanceTypeMap.get(Integer.valueOf(balanceType)) != null){
			balanceTypeName = balanceTypeMap.get(Integer.valueOf(balanceType)).toString();
		}
		return balanceTypeName;
	}
	
	public String getBalanceTypeNameTitle(){
		return (String)balanceTypeMap.get(Integer.valueOf(balanceType));
	}
	
	public String getImportTypeName(){
		return (String)importTypeMap.get(Integer.valueOf(importType));
	}
}

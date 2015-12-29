package mmb.stock.stat;

import java.util.HashMap;
import java.util.Map;


import adultadmin.bean.buy.BuyStockBean;
import adultadmin.bean.stock.ProductStockBean;

/**
 * @name 质检入库任务单
 * @author HYB
 *
 */
public class CheckStockinMissionBean {

	public static final String INLOAD = "0";//产能内
	public static final String OUTLOAD = "1";//产能外
	public String buyStockinCode;	//预计单号
	public int buyStockinId; //预计单id
	public String code; //任务单编号
	public String completeDatetime; //完成时间
	public String createDatetime; //生成时间
	public int createOperId; //生成人id
	public String createOperName; //生成人姓名
	public int id; //质检任务单id
	public int priorStatus; //紧急程度
	public int status; //任务单状态
	public int checkEffect;//质检效率，每小时多少件
	public String productLoad;//产能负荷
//	private Map batchMap = new HashMap();//批次任务列表
	private CheckStockinMissionBatchBean csmBean;//任务详细信息
	private int batchSize;//批次中商品数量
	public BuyStockBean buyStockBean;
	private String priorStatusName;//紧急程度名称
	private String statusName;//状态名称
	public float realConsumTime;
	public int wareArea;  //库地区

	
	public static Map productLoadMap = new HashMap();
	
	public static Map priorityMap = new HashMap();
	
	static{
		productLoadMap.put("0", "产能内");
		productLoadMap.put("1", "产能外");
		
		priorityMap.put("0", "很高");
		priorityMap.put("1", "高");
		priorityMap.put("2", "一般");
		priorityMap.put("3", "低");
		priorityMap.put("4", "很低");
	}
	
	/**
	 * 未处理
	 */
	public static int STATUS0 = 0;

	/**
	 * 已确认到货
	 */
	public static int STATUS1 = 1;
	
	/**
	 * 质检入库中
	 */
	public static int STATUS2 = 2;
	
	
	/**
	 * 已完成
	 */
	public static int STATUS3 = 3;
	
	
	/**
	 * 已删除
	 */
	public static int STATUS4 = 4;
	
	
	
	//优先级
	public static int PRIOR_HEIGHER = 0;//很高
	public static int PRIOR_HEIGH = 1;//高
	public static int PRIOR_NORMAL = 2;//一般
	public static int PRIOR_LOW = 3;//低
	public static int PRIOR_LOWER = 4;//很低
	
	
	public BuyStockBean getBuyStockBean() {
		return buyStockBean;
	}
	public void setBuyStockBean(BuyStockBean buyStockBean) {
		this.buyStockBean = buyStockBean;
	}
	public String getBuyStockinCode() {
		return buyStockinCode;
	}
	public int getBuyStockinId() {
		return buyStockinId;
	}
	public String getCode() {
		return code;
	}
	public String getCompleteDatetime() {
		return completeDatetime;
	}
	public String getCreateDatetime() {
		return createDatetime;
	}
	public int getCreateOperId() {
		return createOperId;
	}
	public String getCreateOperName() {
		return createOperName;
	}
	public int getId() {
		return id;
	}
	public int getPriorStatus() {
		return priorStatus;
	}
	public int getStatus() {
		return status;
	}
	public void setBuyStockinCode(String buyStockinCode) {
		this.buyStockinCode = buyStockinCode;
	}
	public void setBuyStockinId(int buyStockinId) {
		this.buyStockinId = buyStockinId;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public void setCompleteDatetime(String completeDatetime) {
		this.completeDatetime = completeDatetime;
	}
	public void setCreateDatetime(String createDatetime) {
		this.createDatetime = createDatetime;
	}
	public void setCreateOperId(int createOperId) {
		this.createOperId = createOperId;
	}
	public void setCreateOperName(String createOperName) {
		this.createOperName = createOperName;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setPriorStatus(int priorStatus) {
		this.priorStatus = priorStatus;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getBatchSize() {
		return batchSize;
	}
	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}
	public String getPriorStatusName() {
		return priorStatusName;
	}
	public void setPriorStatusName(String priorStatusName) {
		this.priorStatusName = priorStatusName;
	}
	public String getStatusName() {
		return statusName;
	}
	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}
	public int getCheckEffect() {
		return checkEffect;
	}
	public void setCheckEffect(int checkEffect) {
		this.checkEffect = checkEffect;
	}
	public String getProductLoad() {
		return productLoad;
	}
	public void setProductLoad(String productLoad) {
		this.productLoad = productLoad;
	}
	public float getRealConsumTime() {
		return realConsumTime;
	}
	public void setRealConsumTime(float realConsumTime) {
		this.realConsumTime = realConsumTime;
	}
	public CheckStockinMissionBatchBean getCsmBean() {
		return csmBean;
	}
	public void setCsmBean(CheckStockinMissionBatchBean csmBean) {
		this.csmBean = csmBean;
	}
	
	public static String getStatusName(int status){
		if(CheckStockinMissionBean.STATUS0==status){
			return "未处理";
		}else if(CheckStockinMissionBean.STATUS1==status){
			return "已确认到货";
		}else if(CheckStockinMissionBean.STATUS2==status){
			return "质检入库中";
		}else if(CheckStockinMissionBean.STATUS3==status){
			return "已完成";
		}else{
			return "已删除";
		}
	}
	public int getWareArea() {
		return wareArea;
	}
	public void setWareArea(int wareArea) {
		this.wareArea = wareArea;
	}
	public String getWareAreaName() {
		return ProductStockBean.getAreaName(this.getWareArea());
	}
}

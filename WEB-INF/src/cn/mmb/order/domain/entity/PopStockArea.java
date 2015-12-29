package cn.mmb.order.domain.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * POP发货仓
 * @author likaige
 * @create 2015年9月16日 下午2:21:00
 */
public class PopStockArea {
	
	private int id;
	private String code;//编码
	private String area;//区域
	private int popec;//pop商家
	private int addPId;//provinces中的id
	private int addCId;//province_city中的id
	private int addAId;//city_area中的id
	private int addSId;//area_street中的id
	private String addEnd;//地址详情
	private String addressAll;//完整地址
	private String zip;//邮编
	
	/**商品库存状态*/
	private Map<Integer, Integer> productStockStateMap = new HashMap<Integer, Integer>();
	
	public String getStockArea(){
		return this.addPId + "_" + this.addCId + "_" + this.addAId + "_" + this.addSId;
	}
	
	/**
	 * 判断是否全部都是‘有货’商品
	 * @return
	 * @author likaige
	 * @create 2015年9月16日 下午3:30:09
	 */
	public boolean allHaveGoods(){
		boolean allHaveGoods = true;
		for(Integer state : productStockStateMap.values()){
			if(state != 33){
				allHaveGoods = false;
				break;
			}
		}
		return allHaveGoods;
	}
	
	/**
	 * 判断是否包含‘无货’商品
	 * @return
	 * @author likaige
	 * @create 2015年9月16日 下午3:30:09
	 */
	public boolean containsNoGoods(){
		boolean isContainsNoGoods = false;
		for(Integer state : productStockStateMap.values()){
			if(state == 34){
				isContainsNoGoods = true;
				break;
			}
		}
		return isContainsNoGoods;
	}
	
	/**
	 * 计算发货优先级，数字越小优先级越高
	 * @return
	 * @author likaige
	 * @create 2015年9月16日 下午3:52:10
	 */
	public int calPriority(){
		/**
		 * 33 有货 现货-下单立即发货
		 * 39 有货 在途-正在内部配货，预计2~6天到达本仓库
		 * 40 有货 可配货-下单后从有货仓库配货
		 * 36 预订
		 * 34 无货
		 */
		int priority = 0;
		for(Integer state : productStockStateMap.values()){	
			if(state == 33){
				priority += 1;
			}else if(state == 39){
				priority += 2;
			}else if(state == 40){
				priority += 3;
			}else if(state == 36){
				priority += 4;
			}
		}
		
		return priority;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public int getPopec() {
		return popec;
	}

	public void setPopec(int popec) {
		this.popec = popec;
	}

	public int getAddPId() {
		return addPId;
	}

	public void setAddPId(int addPId) {
		this.addPId = addPId;
	}

	public int getAddCId() {
		return addCId;
	}

	public void setAddCId(int addCId) {
		this.addCId = addCId;
	}

	public int getAddAId() {
		return addAId;
	}

	public void setAddAId(int addAId) {
		this.addAId = addAId;
	}

	public int getAddSId() {
		return addSId;
	}

	public void setAddSId(int addSId) {
		this.addSId = addSId;
	}

	public String getAddEnd() {
		return addEnd;
	}

	public void setAddEnd(String addEnd) {
		this.addEnd = addEnd;
	}

	public String getAddressAll() {
		return addressAll;
	}

	public void setAddressAll(String addressAll) {
		this.addressAll = addressAll;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public Map<Integer, Integer> getProductStockStateMap() {
		return productStockStateMap;
	}

	public void setProductStockStateMap(Map<Integer, Integer> productStockStateMap) {
		this.productStockStateMap = productStockStateMap;
	}
	
}

/*
 * Created on 2008-11-5
 *
 */
package adultadmin.bean.stat;

import java.util.Iterator;
import java.util.List;

import adultadmin.bean.stock.ProductStockBean;

/**
 * 作者：史明松
 * 
 * 创建日期：2009-7-8
 * 
 * 说明：
 */
public class DpproductStatBean {

	public int productId;//商品Id
	
    public String code; //编号
    
    public String name; //名称
    
    public String lastTime;//最近出货完成时间
    
    public String lastInTime;//最近采购完成时间
    
    public long outDayCount; //最近一次出货时常

    public int stockinCount; //采购入库量

    public int returnCount; //采购退货量

    public int outCount; //销量
    
    public int outReturnCount;//销量退货量

    public int sunCount; //报损量

    public int daiYanCount; //待验库

    public int heGeCount; //合格库

    public int tuiHuoCoun; //退货库

    public int fanChangCount; //返厂库

    public int weiXiuCount; //维修库
    
    public int canCiPinCount; //残次品库
    
    public int yangPinCount; //样品库
    
    private float price5;//库存价格
    
    public int frequencyCount;   //动碰次数
    /**
     * 该产品的库存列表，所有地区的，所有库的<br/>
     * 需要单独查询出来，放在这个voProduct实例中
     */
    private List psList;

	public List getPsList() {
		return psList;
	}

	public void setPsList(List psList) {
		this.psList = psList;
	}

	public int getProductId() {
		return productId;
	}

	public void setProductId(int productId) {
		this.productId = productId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getStockinCount() {
		return stockinCount;
	}

	public void setStockinCount(int stockinCount) {
		this.stockinCount = stockinCount;
	}

	public int getReturnCount() {
		return returnCount;
	}

	public void setReturnCount(int returnCount) {
		this.returnCount = returnCount;
	}

	public int getOutCount() {
		return outCount;
	}

	public void setOutCount(int outCount) {
		this.outCount = outCount;
	}

	public int getSunCount() {
		return sunCount;
	}

	public void setSunCount(int sunCount) {
		this.sunCount = sunCount;
	}

	public int getDaiYanCount() {
		return daiYanCount;
	}

	public void setDaiYanCount(int daiYanCount) {
		this.daiYanCount = daiYanCount;
	}

	public int getHeGeCount() {
		return heGeCount;
	}

	public void setHeGeCount(int heGeCount) {
		this.heGeCount = heGeCount;
	}

	public int getTuiHuoCoun() {
		return tuiHuoCoun;
	}

	public void setTuiHuoCoun(int tuiHuoCoun) {
		this.tuiHuoCoun = tuiHuoCoun;
	}

	public int getFanChangCount() {
		return fanChangCount;
	}

	public void setFanChangCount(int fanChangCount) {
		this.fanChangCount = fanChangCount;
	}

	public int getWeiXiuCount() {
		return weiXiuCount;
	}

	public void setWeiXiuCount(int weiXiuCount) {
		this.weiXiuCount = weiXiuCount;
	}

	public int getCanCiPinCount() {
		return canCiPinCount;
	}

	public void setCanCiPinCount(int canCiPinCount) {
		this.canCiPinCount = canCiPinCount;
	}

	public int getYangPinCount() {
		return yangPinCount;
	}

	public void setYangPinCount(int yangPinCount) {
		this.yangPinCount = yangPinCount;
	}

	public int getOutReturnCount() {
		return outReturnCount;
	}

	public void setOutReturnCount(int outReturnCount) {
		this.outReturnCount = outReturnCount;
	}
	
	public int getStockAll(){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			result += ps.getStock();
		}
		return result;
	}

	public int getStock(int area){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getArea() == area){
				result += ps.getStock();
			}
		}
		return result;
	}

	public int getStock(int area, int type){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getArea() == area && ps.getType() == type){
				result += ps.getStock();
			}
		}
		return result;
	}



	public int getLockCountAll(){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			result += ps.getLockCount();
		}
		return result;
	}

	public int getLockCount(int area){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getArea() == area){
				result += ps.getLockCount();
			}
		}
		return result;
	}

	public int getLockCount(int area, int type){
		int result = 0;
		if(psList == null){
			return result;
		}
		Iterator iter = psList.listIterator();
		while(iter.hasNext()){
			ProductStockBean ps = (ProductStockBean) iter.next();
			if(ps.getArea() == area && ps.getType() == type){
				result += ps.getLockCount();
			}
		}
		return result;
	}

	public String getLastTime() {
		return lastTime;
	}

	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}

	public String getLastInTime() {
		return lastInTime;
	}

	public void setLastInTime(String lastInTime) {
		this.lastInTime = lastInTime;
	}

	public long getOutDayCount() {
		return outDayCount;
	}

	public void setOutDayCount(long outDayCount) {
		this.outDayCount = outDayCount;
	}

	public float getPrice5() {
		return price5;
	}

	public void setPrice5(float price5) {
		this.price5 = price5;
	}

	public int getFrequencyCount() {
		return frequencyCount;
	}

	public void setFrequencyCount(int frequencyCount) {
		this.frequencyCount = frequencyCount;
	}
	
}

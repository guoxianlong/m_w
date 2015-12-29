package adultadmin.action.vo;

/**
 *  <code>OrderMatchDeliverVO.java</code>
 *  <p>功能:订单自动匹配快递公司vo类
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-6-28 下午03:53:41	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class OrderMatchDeliverVO {

	/**
	 * 查找总记数量
	 */
	private int count;
	
	/**
	 * 匹配数量广宅
	 */
	private int matchCountGZ;
	
	/**
	 * 匹配数量广外
	 */
	private int matchCountGW;
	
	/**
	 * 匹配数量广速
	 */
	private int matchCountGS;
	
	/**
	 * 人工指定数量广宅
	 */
	private int countGZ;
	/**
	 * 人工指定数量广外
	 */
	private int countGW;
	/**
	 * 人工指定数量广速
	 */
	private int countGS;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getMatchCountGZ() {
		return matchCountGZ;
	}
	public void setMatchCountGZ(int matchCountGZ) {
		this.matchCountGZ = matchCountGZ;
	}
	public int getMatchCountGW() {
		return matchCountGW;
	}
	public void setMatchCountGW(int matchCountGW) {
		this.matchCountGW = matchCountGW;
	}
	public int getMatchCountGS() {
		return matchCountGS;
	}
	public void setMatchCountGS(int matchCountGS) {
		this.matchCountGS = matchCountGS;
	}
	public int getCountGZ() {
		return countGZ;
	}
	public void setCountGZ(int countGZ) {
		this.countGZ = countGZ;
	}
	public int getCountGW() {
		return countGW;
	}
	public void setCountGW(int countGW) {
		this.countGW = countGW;
	}
	public int getCountGS() {
		return countGS;
	}
	public void setCountGS(int countGS) {
		this.countGS = countGS;
	}
	
	
}

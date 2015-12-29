package adultadmin.service.infc;

import java.util.List;

/**
 *  <code>ICityAreaService.java</code>
 *  <p>功能:订单地址街道级联接口
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-2-25 下午06:09:11	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public interface ICityAreaService {

	/**
	 * 功能:批量添加省 城市 信息
	 * <p>作者文齐辉 2011-2-25 下午06:12:13
	 * @param sql
	 * @return
	 */
	public int addBatchProvinceCity(String sql);
	
	/**
	 * 功能:批量添加城市区域信息
	 * <p>作者文齐辉 2011-2-25 下午06:12:13
	 * @param sql
	 * @return
	 */
	public int addBatchCityArea(String sql);

	/**
	 * 功能:批量添加区域街道信息
	 * <p>作者文齐辉 2011-2-25 下午06:12:13
	 * @param sql
	 * @return
	 */
	public int addBatchAreaStreet(String sql);
	
	/**
	 * 功能:根据城市名查找街道信息
	 * <p>作者文齐辉 2011-2-28 上午11:56:55
	 * @param city
	 * @return
	 */
	public List getAreaStreetList(String city);
	
	/**
	 * 添加省份
	 * @param sql
	 * @return
	 */
	public int addBatchProvinces(String sql);
	
}

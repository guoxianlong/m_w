package adultadmin.service.infc;

import java.util.ArrayList;

import adultadmin.action.vo.voOrder;
import adultadmin.bean.UserOrderProductHistoryBean;
import adultadmin.bean.UserOrderProductSplitHistoryBean;
import adultadmin.bean.order.UserOrderCommonPropertiesBean;
import adultadmin.bean.order.UserOrderPackageTypeBean;

/**
 * 作者：张陶
 * 
 * 创建日期：2010-08-12
 * 
 * 说明：
 */
public interface IUserOrderService extends IBaseService {
	/**
	 * 功能:根据产品一级分类id得到（订单）包裹单的产品分类
	 * <p>作者文齐辉 2011-5-27 上午11:10:38
	 * @return
	 */
	public UserOrderPackageTypeBean getUserOrderPackageType(String condition);
	
	public boolean addUserOrderCommonProperties(UserOrderCommonPropertiesBean bean);
	public boolean updateUserOrderCommonProperties(String set, String condition);
	public UserOrderCommonPropertiesBean getUserOrderCommonProperties(String condition);
	public ArrayList getUserOrderProductSplitHistoryList(String condition, int index, int count,
			String orderBy);
	
	public int getUserOrderProductHistoryCount(String condition);
	public ArrayList getUserOrderProductHistoryList(String condition, int index, int count,
			String orderBy);
	public boolean updateUserOrderProductHistory(String set, String condition);
	public boolean addUserOrderProductHistory(UserOrderProductHistoryBean uoph);
	public boolean deleteUserOrderProductHistory(String condition);
	public boolean deleteUserOrderProductSplitHistory(String condition);
	public boolean updateUserOrderProductSplitHistory(String set, String condition);
	public boolean addUserOrderProductSplitHistory(UserOrderProductSplitHistoryBean uoph);
	public ArrayList getUserOrderPresentHistoryList(String condition, int index, int count,
			String orderBy);
	public ArrayList getUserOrderPresentSplitHistoryList(String condition, int index, int count,
			String orderBy);
	public boolean updateUserOrderPresentHistory(String set, String condition);
	public boolean addUserOrderPresentHistory(UserOrderProductHistoryBean uoph);
	public boolean updateUserOrderPresentSplitHistory(String set, String condition);
	public boolean addUserOrderPresentSplitHistory(UserOrderProductSplitHistoryBean uoph);
	public boolean deleteUserOrderPresentHistory(String condition);
	public boolean deleteUserOrderPresentSplitHistory(String condition);
	public UserOrderProductHistoryBean getUserOrderProductHistory(String condition);
	public UserOrderProductSplitHistoryBean getUserOrderProductSplitHistory(String condition);
	public UserOrderProductHistoryBean getUserOrderPresentHistory(String condition);
	public UserOrderProductSplitHistoryBean getUserOrderPresentSplitHistory(String condition);
}

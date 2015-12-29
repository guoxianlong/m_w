package mmb.common.dao.mappers;

import java.util.Map;

import mmb.common.dao.UserOrderDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import com.mmb.framework.support.DynamicDataSource;

import adultadmin.action.vo.voOrder;
import adultadmin.bean.log.OrderAdminLogBean;
import adultadmin.util.DateUtil;
@Repository
public class UserOrderMapper extends AbstractDaoSupport implements UserOrderDao {

	@Override
	public voOrder getUserOrder(String condition) {
		return getSession(DynamicDataSource.SLAVE).selectOne(condition);
	}
	
	/** 
	 * @Description: 更新订单状态
	 * @return int 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月7日 下午4:32:34 
	 */
	public int updateOrderStatus(String sql) {
		return this.getJdbcTemplate().update(sql);
	}

	/** 
	 * @Description: 获取订单信息
	 * @return Map<String,Object> 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月12日 下午5:16:45 
	 */
	public Map<String, Object> getUserOrderInfo(int orderId) {
		String sql = "select id,code from user_order where id="+orderId;
		return this.getJdbcTemplate().queryForMap(sql);
	}

	/** 
	 * @Description: 添加订单修改信息日志
	 * @return void 返回类型 
	 * @author 叶二鹏
	 * @date 2015年8月12日 下午5:20:29 
	 */
	public void addOrderAdminLog(OrderAdminLogBean log) {
		// TODO Auto-generated method stub
		String sql = "INSERT INTO `order_admin_log` (`create_datetime`, `content`, `type`, `user_id`, `username`, `order_id`, `order_code`) VALUES (?,?,?,?,?,?,?)";
		Object[] args = {DateUtil.getNow(), log.getContent(), log.getType(), log.getUserId(), log.getUsername(), log.getOrderId(), log.getOrderCode()};
		this.getJdbcTemplate().update(sql, args);
	}

}

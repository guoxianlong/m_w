package mmb.common.dao.mappers;

import java.util.List;
import java.util.Map;

import mmb.common.dao.UserOrderExtendInfoDao;

import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.stereotype.Repository;

import adultadmin.action.vo.voOrderExtendInfo;
@Repository
public class UserOrderExtendInfoMapper extends AbstractDaoSupport implements UserOrderExtendInfoDao {

	@Override
	public int addUserOrderExtendInfo(voOrderExtendInfo orderExtendInfo) {
		getSession().insert(orderExtendInfo);
		return orderExtendInfo.getId();
	}

	@Override
	public voOrderExtendInfo getUserOrderExtendInfo(String condition) {
		return getSession().selectOne(condition);
	}

	@Override
	public List<voOrderExtendInfo> getUserOrderExtendInfoList(
			Map<String, String> paramMap) {
		return getSession().selectList(paramMap);
	}

}

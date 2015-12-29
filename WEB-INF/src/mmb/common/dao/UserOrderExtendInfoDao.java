package mmb.common.dao;

import java.util.List;
import java.util.Map;

import adultadmin.action.vo.voOrderExtendInfo;

public interface UserOrderExtendInfoDao {
	
	public int addUserOrderExtendInfo(voOrderExtendInfo orderExtendInfo);
	
	public voOrderExtendInfo getUserOrderExtendInfo(String condition);
	
	public List<voOrderExtendInfo> getUserOrderExtendInfoList(Map<String,String> paramMap);

}

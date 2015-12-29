package mmb.cargo.dao;

import java.util.List;
import java.util.Map;

import mmb.cargo.model.CargoInfoCity;
import mmb.cargo.model.HelpContext;

public interface HelpContextDao {
	
	int deleteByPrimaryKey(Integer id);

	int insert(HelpContext record);

	int updateByPrimaryKey(HelpContext record);

	int getHelpContextCount(Map<String,String> condition);
	
	List<HelpContext> getHelpConetxtList(Map<String,String> map);
	
	HelpContext getHelpContext(Map<String,String> map);
}

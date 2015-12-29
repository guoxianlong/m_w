package mmb.cargo.service;

import java.util.List;
import java.util.Map;

import mmb.cargo.model.HelpContext;

public interface IHelpConetxtService {
	
	public List<HelpContext> getHelpConetxts(Map<String,String> condition);
	
	public int getHelpContextCount(Map<String,String> condition);
	
	public boolean addHelpContext(HelpContext helpContext) throws Exception;
	
	public boolean editHelpContext(HelpContext helpContext) throws Exception;
	
	public boolean delHelpContext(Integer id) throws Exception;
	
	public HelpContext getHelpContext(Map<String,String> map);
	
}

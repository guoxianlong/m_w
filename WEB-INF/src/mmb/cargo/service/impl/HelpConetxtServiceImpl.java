package mmb.cargo.service.impl;

import java.util.List;
import java.util.Map;

import mmb.cargo.dao.HelpContextDao;
import mmb.cargo.model.HelpContext;
import mmb.cargo.service.IHelpConetxtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
@Service
public class HelpConetxtServiceImpl implements IHelpConetxtService {
	@Autowired
	public HelpContextDao helpConetxtDao;

	@Override
	@Transactional(propagation=Propagation.NOT_SUPPORTED,readOnly=true)
	public List<HelpContext> getHelpConetxts(Map<String, String> condtion) {
		
		List<HelpContext> hc = helpConetxtDao.getHelpConetxtList(condtion);		
		return hc;
	}

	@Override
	public int getHelpContextCount(Map<String, String> condition) {
		// TODO Auto-generated method stub
		return helpConetxtDao.getHelpContextCount(condition);
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public boolean addHelpContext(HelpContext helpContext) throws Exception {
		int helpContextId = helpConetxtDao.insert(helpContext);
		if(helpContextId == 0){
			throw new RuntimeException("添加失败");
		}
		return true;
	}

	@Override
	@Transactional(rollbackFor=Exception.class)
	public boolean editHelpContext(HelpContext helpContext) throws Exception {
		int helpContextId = helpConetxtDao.updateByPrimaryKey(helpContext);
		if(helpContextId == 0){
			throw new RuntimeException("更新失败");
		}
		return true;
	}

	@Override
	public boolean delHelpContext(Integer id) throws Exception {
		int helpContextId= helpConetxtDao.deleteByPrimaryKey(id);

		if(helpContextId != 1){
			throw new RuntimeException("删除失败");
		}
		return true;
	}

	@Override
	public HelpContext getHelpContext(Map<String, String> map) {
		
		return helpConetxtDao.getHelpContext(map);
	}
	

}

package mmb.cargo.service;

import java.util.List;
import java.util.Map;

import adultadmin.action.vo.voUser;

import mmb.cargo.model.ViewTree;
import mmb.rec.sys.easyui.EasyuiTreeNode;

public interface IViewTreeService {
	
	public List<EasyuiTreeNode> getViewTrees(int pId,voUser user);
	
	public ViewTree getViewTreeForName(Map<String,String> condition);
}

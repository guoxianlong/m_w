package mmb.cargo.dao;

import java.util.List;
import java.util.Map;

import mmb.cargo.model.ViewTree;

public interface ViewTreeDao {
	
	List<ViewTree> getViewTreeList(Map<String,String> map);
	
	ViewTree getViewTreeForName(Map<String,String> map);
}

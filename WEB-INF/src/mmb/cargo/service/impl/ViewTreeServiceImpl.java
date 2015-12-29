package mmb.cargo.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mmb.cargo.dao.ViewTreeDao;
import mmb.cargo.model.ViewTree;
import mmb.cargo.service.IViewTreeService;
import mmb.rec.sys.easyui.EasyuiTreeNode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.util.StringUtil;
@Service
public class ViewTreeServiceImpl implements IViewTreeService{
	
	@Autowired
	public ViewTreeDao vtd;
	
	/**
	 * 说明：获取菜单树列表
	 * 
	 * 时间：2013-03-04
	 * 
	 * 作者：aohaichen
	 */
	@Override
	@Transactional(propagation=Propagation.NOT_SUPPORTED,readOnly=true)
	public List<EasyuiTreeNode> getViewTrees(int pId, voUser user) {

		List<EasyuiTreeNode> treeNodeList = new ArrayList<EasyuiTreeNode>();
		StringBuffer condition = new StringBuffer();
		
		Map<String,String> map = new HashMap<String,String>();
		Map<String,String> map2 = new HashMap<String,String>();
		if (pId != -1) {
			condition.append("parent_id=" + pId);
		}else{
			condition.append("parent_id=0");
		}
		map.put("condition", condition.toString());
		map.put("order","id");

		List<ViewTree> menus = vtd.getViewTreeList(map);

		Set<ViewTree> resourcesMenus = new LinkedHashSet<ViewTree>();
		
		UserGroupBean group = user.getGroup();
		
		if(menus != null && menus.size() > 0){
			for(ViewTree menu : menus){
				if(!"".equals(menu.getLimits())&& menu.getLimits()!=null){
					if(menu.getLimits().contains(",")){
						for(String s : menu.getLimits().split(",")){
							if(!"".equals(s.trim())){

								if (group.isFlag(StringUtil.toInt(s))) {
									resourcesMenus.add(menu);
								}
							}
						}
					}else if(menu.getLimits().contains("|")){
						for(String s : menu.getLimits().split("\\|")){
							if(!"".equals(s.trim())){

								if (group.isFlag(StringUtil.toInt(s))) {
									resourcesMenus.add(menu);
								}
							}
						}
					}else{
						if (group.isFlag(StringUtil.toInt(menu.getLimits()))) {
							resourcesMenus.add(menu);
						}
					}
					
				}else{
					resourcesMenus.add(menu);
				}
			}
		}
		
		if(resourcesMenus != null && resourcesMenus.size() > 0){
			
			for(ViewTree menu : resourcesMenus){
				
				map2.put("condition","parent_id=" + menu.getId());
				map2.put("order","id");

				List<ViewTree> menuList = vtd.getViewTreeList(map2);
				if(menuList != null && menuList.size() > 0){
					menu.setMenus(menuList);
				}
			}
			for (ViewTree menu : resourcesMenus) {
				treeNodeList.add(toTree(menu, false));
			}
		}
		
		return treeNodeList;
	}
	
	/**
	 * 说明：easyUI功能Bean转换方法--将存放树信息的Bean转成easyUI需要的属性Bean
	 * 
	 * 时间：2013-03-04
	 * 
	 * 作者：aohaichen
	 */
	private EasyuiTreeNode toTree(ViewTree menu, boolean recursive) {
		EasyuiTreeNode node = new EasyuiTreeNode();

		try {
			node.setId(menu.getId() + "");
			node.setText(menu.getName());
			//node.setIconCls(menu.getIconcls());
			Map<String, Object> attributes = new HashMap<String, Object>();
			if(!"".equals(menu.getUrl())){
				
				attributes.put("url","helpcontext.jsp?id="+menu.getId());
			}else{
				attributes.put("url", "");
			}			
			
			node.setAttributes(attributes);
			if (menu.getMenus() != null && menu.getMenus().size() > 0) {
				node.setState("closed");
				if (recursive) {// 递归查询子节点
					List<ViewTree> menus = new ArrayList<ViewTree>(menu.getMenus() );

					List<EasyuiTreeNode> children = new ArrayList<EasyuiTreeNode>();
					for (ViewTree m : menus) {
						EasyuiTreeNode t = toTree(m, true);
						children.add(t);
					}
					node.setChildren(children);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return node;
	}

	@Override
	public ViewTree getViewTreeForName(Map<String, String> condition) {
		
		return vtd.getViewTreeForName(condition);
	}

}

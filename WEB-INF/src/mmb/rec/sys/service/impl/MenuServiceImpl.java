package mmb.rec.sys.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import mmb.rec.sys.bean.WareMenuBean;
import mmb.rec.sys.bean.WareMenuTreeBean;
import mmb.rec.sys.easyui.EasyuiTreeNode;
import mmb.rec.sys.util.MenuComparator;
import adultadmin.action.vo.voUser;
import adultadmin.bean.UserGroupBean;
import adultadmin.service.impl.BaseServiceImpl;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;


/**
 * 说明：具有 生成左侧树 编辑左侧树功能 service实现类
 * 
 * 时间：2013-08-09
 * 
 * 作者：石远飞
 */

public class MenuServiceImpl extends BaseServiceImpl {
    public MenuServiceImpl(DbOperation dbOp) {
        this.dbOp = dbOp;
    }
    /**
     * 说明：获取表中的树信息
     * 
     * 时间：2013-08-09
     * 
     * 作者：石远飞
     */
	public List<EasyuiTreeNode> getMenuTree(int pId,voUser user) {
		List<EasyuiTreeNode> treeNodeList = new ArrayList<EasyuiTreeNode>();
		try {
			StringBuffer condition = new StringBuffer();
			if (pId != -1) {
				condition.append("PID=" + pId);
			}else{
				condition.append("PID=0");
			}
			List<WareMenuBean> menus = this.getMenuList(condition.toString(), -1, -1, "SEQ");
			List<WareMenuBean> resourcesMenus = new ArrayList<WareMenuBean>();
			
			UserGroupBean group = user.getGroup();
			if(menus != null && menus.size() > 0){
				for(WareMenuBean menu : menus){
					if(!"".equals(menu.getResources())&&menu.getResources()!=null){
						if (group.isFlag(StringUtil.toInt(menu.getResources()))) {
							resourcesMenus.add(menu);
						}
					}else{
						resourcesMenus.add(menu);
					}
				}
			}
			if(resourcesMenus != null && resourcesMenus.size() > 0){
				for(WareMenuBean menu : resourcesMenus){
					List<WareMenuBean> menuList = this.getMenuList("PID=" + menu.getId(), -1, -1, "SEQ");
					if(menuList != null && menuList.size() > 0){
						menu.setMenus(menuList);
					}
				}
				for (WareMenuBean menu : resourcesMenus) {
					treeNodeList.add(toTree(menu, false));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return treeNodeList;
	}
	/**
	 * 说明：easyUI功能Bean转换方法--将存放树信息的Bean转成easyUI需要的属性Bean
	 * 
	 * 时间：2013-08-09
	 * 
	 * 作者：石远飞
	 */
	private EasyuiTreeNode toTree(WareMenuBean menu, boolean recursive) {
		EasyuiTreeNode node = new EasyuiTreeNode();
		try {
			node.setId(menu.getId() + "");
			node.setText(menu.getText());
			node.setIconCls(menu.getIconcls());
			Map<String, Object> attributes = new HashMap<String, Object>();
			attributes.put("url", menu.getUrl());
			node.setAttributes(attributes);
			if (menu.getMenus() != null && menu.getMenus().size() > 0) {
				node.setState("closed");
				if (recursive) {// 递归查询子节点
					List<WareMenuBean> menus = new ArrayList<WareMenuBean>(menu.getMenus() );
					Collections.sort(menus, new MenuComparator());// 排序
					List<EasyuiTreeNode> children = new ArrayList<EasyuiTreeNode>();
					for (WareMenuBean m : menus) {
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
	/**
	 * 说明：生成菜单列表
	 * 
	 * 时间：22013-08-09
	 * 
	 * 作者：石远飞
	 */
	public List<WareMenuTreeBean> getMenuTreeGrid(int pId) {
		List<WareMenuTreeBean> menuTreeList = new ArrayList<WareMenuTreeBean>();
		try {
			StringBuffer condition = new StringBuffer();
			if (pId != -1) {
				condition.append("PID=" + pId);
			}else{
				condition.append("PID = 0");
			}
			List<WareMenuBean> menuList = this.getMenuList(condition.toString(), -1, -1, "SEQ");
			if(menuList != null && menuList.size() > 0){
				for(WareMenuBean menu : menuList){
					List<WareMenuBean> menus = this.getMenuList("PID=" + menu.getId(), -1, -1, "SEQ");
					if(menus != null && menus.size() > 0){
						menu.setMenus(menus);
					}
					if(menu!= null && menu.getpId() != 0){
						WareMenuBean b = this.getMenu("ID=" + menu.getpId());
						menu.setMenu(b);
					}
				}
			}
			for (WareMenuBean menu : menuList) {
				WareMenuTreeBean menuTree = new WareMenuTreeBean();
				BeanUtils.copyProperties(menuTree, menu);
				if (menu.getMenu() != null) {
					menuTree.setParentId(menu.getMenu().getId() + "");
					menuTree.setParentText(menu.getMenu().getText());
				}
				menuTree.setIconcls(menu.getIconcls());
				if (menu.getMenus() != null && menu.getMenus().size() > 0) {
					menuTree.setState("closed");
				}
				menuTreeList.add(menuTree);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return menuTreeList;
	}
	/**
	 * 说明：添加Tree
	 * 
	 * 时间：2013-08-09
	 * 
	 * 作者：石远飞
	 */
	public String addMenuTreeGrid(WareMenuTreeBean menuTree) {
		WareMenuBean menu = new WareMenuBean();
		String result = null;
		try {
			BeanUtils.copyProperties(menu, menuTree);
			if(menuTree.getParentId() != null && !"".equals(menuTree.getParentId())){
				menu.setpId(StringUtil.toInt(menuTree.getParentId()));
			}
			if(this.addMenu(menu)){
				result = "success";
			}else{
				result = "添加失败!";
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
	/**
	 * 说明：编辑Tree
	 * 
	 * 时间：2013-08-09
	 * 
	 * 作者：石远飞
	 */
	public String editMenuTreeGrid(WareMenuTreeBean menuTree) {
		WareMenuBean menu = new WareMenuBean();
		String result = null;
		try {
			BeanUtils.copyProperties(menu, menuTree);
			if(menuTree.getParentId() != null && !"".equals(menuTree.getParentId())){
				WareMenuBean m = this.getMenu("id="+ menuTree.getId());
				if(m != null && m.getpId() == 0){
					if(menuTree.getParentId() != null && !"".equals(menuTree.getParentId())){
						return result = "根节点不可以修改上级节点";
					}
				}
			}
			StringBuffer set = new StringBuffer();
			if(menuTree.getParentId() != null && !"".equals(menuTree.getParentId())){// 根节点不可以修改上级节点
				WareMenuBean b = this.getMenu("ID='" + menu.getpId() + "'");
				menu.setMenu(b);
				set.append("ICONCLS='" + menu.getIconcls() + "',pid=" + menuTree.getParentId() + ",text='" + menu.getText() + "',"
						+ "url='" + menu.getUrl() + "',seq=" + menu.getSeq() + ",resources='" + menu.getResources() + "'");
			}else{
				set.append("ICONCLS='" + menu.getIconcls() + "',text='" + menu.getText() + "',"
						+ "url='" + menu.getUrl() + "',seq=" + menu.getSeq() + ",resources='" + menu.getResources() + "'");
			}
			if(this.updateMenu(set.toString(), "id=" + menu.getId())){
				result = "success";
			}else{
				result = "编辑失败";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 说明：删除Tree
	 * 
	 * 时间：2013-08-09
	 * 
	 * 作者：石远飞
	 */
	public String delMenuTreeGrid(WareMenuBean menu) {
		String result = null;
		try {
			if(this.deleteMenu("id=" + menu.getId())){
				result = "success";
			}else{
				result = "删除失败!";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public boolean deleteMenu(String condition) {
    	return deleteXXX(condition, "ware_menu");
    }
	public WareMenuBean getMenu(String condition) {
        return (WareMenuBean) getXXX(condition, "ware_menu", "mmb.rec.sys.bean.WareMenuBean");
    }
	public boolean addMenu(WareMenuBean menu) {
        return addXXX(menu, "ware_menu");
	}
	public boolean updateMenu(String set, String condition) {
    	return updateXXX(set, condition, "ware_menu");
    }
	@SuppressWarnings("unchecked")
	public List<WareMenuBean> getMenuList(String condition,int index,int count,String orderBy){
		return getXXXList(condition, index, count, orderBy, "ware_menu", "mmb.rec.sys.bean.WareMenuBean");
	}
}

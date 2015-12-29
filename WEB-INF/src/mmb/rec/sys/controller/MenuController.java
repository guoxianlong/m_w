package mmb.rec.sys.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mmb.rec.sys.bean.WareMenuBean;
import mmb.rec.sys.bean.WareMenuTreeBean;
import mmb.rec.sys.easyui.EasyuiTreeNode;
import mmb.rec.sys.easyui.Json;
import mmb.rec.sys.service.impl.MenuServiceImpl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;


@Controller
@RequestMapping("/MenuController")
public class MenuController {

	@RequestMapping("/getMenuTree")
	@ResponseBody
	public List<EasyuiTreeNode> getMenuTree(HttpServletRequest request,String id) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		MenuServiceImpl menuService = new MenuServiceImpl(dbOp);
		List<EasyuiTreeNode> treeNodeList = null;
		try {
			treeNodeList = menuService.getMenuTree(StringUtil.toInt(id),user);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			menuService.releaseAll();
		}
		return treeNodeList;
	}
	/**
	 * 说明：生成TreeGrid
	 * 
	 * 时间：2013-08-09
	 * 
	 * 作者：石远飞
	 */
	@RequestMapping("/getMenuTreeGrid")
	@ResponseBody
	public List<WareMenuTreeBean> getMenuTreeGrid(HttpServletRequest request,String id) {
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		MenuServiceImpl menuService = new MenuServiceImpl(dbOp);
		List<WareMenuTreeBean> menuTreeList = null;
		try {
			menuTreeList = menuService.getMenuTreeGrid(StringUtil.toInt(id));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			menuService.releaseAll();
		}
		return menuTreeList;
	}
	/**
	 * 说明：添加或编辑Tree
	 * 
	 * 时间：2013-08-09
	 * 
	 * 作者：石远飞
	 */
	@RequestMapping("/addOrEditMenuTreeGrid")
	@ResponseBody
	public Json addOrEditMenuTreeGrid(HttpServletRequest request,WareMenuTreeBean menuTree){
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		MenuServiceImpl menuService = new MenuServiceImpl(dbOp);
		Json j = new Json();
		String result = null;
		try { 
			if(menuTree.getId()==0){
				//menuTree.setId(0);
				result = menuService.addMenuTreeGrid(menuTree);
				if("success".equals(result)){
					j.setSuccess(true);
					j.setMsg("添加成功!");
				}else{
					j.setMsg("添加失败!");
				}
			}else{
				result = menuService.editMenuTreeGrid(menuTree);
				if("success".equals(result)){
					j.setSuccess(true);
					j.setMsg("编辑成功!");
				}else{
					j.setMsg(result);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			menuService.releaseAll();
		}
		return j;
	}
	/**
	 * 说明：删除Tree
	 * 
	 * 时间：2013-08-09
	 * 
	 * 作者：石远飞
	 */
	@RequestMapping("/delMenuTreeGrid")
	@ResponseBody
	public Json delMenuTreeGrid(HttpServletRequest request,WareMenuBean menu){
		DbOperation dbOp = new DbOperation(DbOperation.DB);
		MenuServiceImpl menuService = new MenuServiceImpl(dbOp);
		Json j = new Json();
		try {
			
			String result = menuService.delMenuTreeGrid(menu);
			if("success".equals(result)){
				j.setSuccess(true);
				j.setMsg("删除成功!");
			}else{
				j.setMsg(result);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			menuService.releaseAll();
		}
		return j;
	}
}

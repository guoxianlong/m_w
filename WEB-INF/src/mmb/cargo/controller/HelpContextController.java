package mmb.cargo.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.cargo.helpcontext.util.CargoUtil;
import mmb.cargo.model.CargoInfoCity;
import mmb.cargo.model.HelpContext;
import mmb.cargo.model.ViewTree;
import mmb.cargo.service.IHelpConetxtService;
import mmb.cargo.service.IViewTreeService;
import mmb.rec.sys.easyui.EasyuiDataGridJson;
import mmb.rec.sys.easyui.EasyuiPageBean;
import mmb.rec.sys.easyui.EasyuiTreeNode;
import mmb.rec.sys.easyui.Json;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import adultadmin.action.vo.voUser;
import adultadmin.util.StringUtil;

@Controller
@RequestMapping("/HelpContext")
public class HelpContextController {
	Lock lock = new ReentrantLock();
	@Autowired
	public IHelpConetxtService helpConetxtService;
	
	@Autowired
	public IViewTreeService ivts;
	
	
	/***
	 * @Describe 获取菜单列表
	 */
	@RequestMapping("/getViewTrees")
	@ResponseBody
	public List getHelpContextsDatagrid(HttpServletRequest request,String id){
		voUser user = (voUser) request.getSession().getAttribute("userView");
		
		List<EasyuiTreeNode> treeNodeList = ivts.getViewTrees(StringUtil.toInt(id),user);
		
		return treeNodeList;
	
	}
	/***
	 * @Describe 获取帮助文档列表
	 * @param request
	 * @param response
	 * @param page easyui分页bean
	 * @param city 
	 * @return json格式的EasyuiDataGridJson
	 */
	@RequestMapping("/getHelpContexts")
	@ResponseBody
	public EasyuiDataGridJson getHelpContextsDatagrid(HttpServletRequest request,HttpServletResponse response,
			EasyuiPageBean page,CargoInfoCity city,String menuId){
		EasyuiDataGridJson datagrid = new EasyuiDataGridJson();
		Map<String,String> map = new HashMap<String,String>();
		StringBuffer condition = new StringBuffer();
		
		condition.append("menu_id="+menuId);
		map.put("condition",condition.toString());
		int rowCount = helpConetxtService.getHelpContextCount(map);
		datagrid.setTotal((long)rowCount);
		map.put("start", (page.getPage()-1) * page.getRows() + "");
		map.put("count", page.getRows() + "");

		
		
		List<HelpContext> helpContexts = helpConetxtService.getHelpConetxts(map);
		
		datagrid.setRows(helpContexts);
		return datagrid;
	
	}
	/***
	 * @Describe 页面通过ajax获取“所属”信息
	 * @param menuId 
	 * @return json
	 */
	@RequestMapping("/loadMenuInfo")
	@ResponseBody
	public Json loadMenuInfo(HttpServletRequest request,HttpServletResponse response,String menuId){
		Json j = new Json();
		boolean condition = true;
		StringBuffer fromStr = new StringBuffer();
		Map<String,String> map1 = new HashMap<String, String>();
		map1.put("condition", "id=" + menuId);
		ViewTree tree =  ivts.getViewTreeForName(map1);
		
		fromStr.append(tree.getName());
		
		while(condition){
			if(0!=tree.getParentId()){
				map1.put("condition", "id=" + tree.getParentId());
				tree =  ivts.getViewTreeForName(map1);
				fromStr.append("<-"+tree.getName());
			}else{
				condition = false;
			}
			
		}
		
		Map<String,String> map = new HashMap<String, String>();
		map.put("fromStr", fromStr.toString());
		
		j.setObj(map);
		return j;
	
	}
	
	/***
	 * @Describe 页面通过ajax获取随机生成“UUID”
	 * @param menuId 
	 * @return json
	 */
	@RequestMapping("/getUUID")
	@ResponseBody
	public Json getUUID(HttpServletRequest request,HttpServletResponse response,String menuId){
		Json j = new Json();	
		Map<String,String> map = new HashMap<String, String>();
		String uuid = CargoUtil.uuid();
		map.put("UUID", uuid);
		
		j.setObj(map);
		return j;
	
	}
	
	/***
	 * @Describe 添加和编辑帮助文档
	 * @param city 
	 * @return json
	 */
	@RequestMapping("/addAndEditHelpContext")
	@ResponseBody
	public Json addAndEditHelpContext(HttpServletRequest request, HelpContext helpContext ,String menuId,String id){
		Json j = new Json();
		voUser user = (voUser) request.getSession().getAttribute("userView");	

		if(helpContext == null){
			j.setMsg("接收数据异常!");
			return j;
		}
		try {
			try{ 
				lock.lock(); 
				helpContext.setLastUpdateOne(user.getUsername());//设置当前操作人	
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");										
				helpContext.setLastUpdateDate(sdf.format(new Date()));//保存当前时间
				helpContext.setMenuId(Integer.parseInt(menuId));
				
				if(helpContext.getId()==null){//id为空，则执行添加记录，否则执行编辑
					
					helpConetxtService.addHelpContext(helpContext);
					
					j.setMsg("添加成功!");
				}else{
					helpContext.setId(Integer.parseInt(id));	
					
					helpConetxtService.editHelpContext(helpContext);
					
					j.setMsg("更新成功!");
				}
				
			}finally{ 
				lock.unlock(); 
			} 
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
		return j;
	}
	
	/***
	 * @Describe 删除帮助文档
	 * @param city 
	 * @return json
	 */
	@RequestMapping("/delHelpContext")
	@ResponseBody
	public Json delHelpContext(HttpServletRequest request, HelpContext helpContext,String id){
		Json j = new Json();

		if(helpContext == null){
			j.setMsg("接收数据异常!");
			return j;
		}
		try {
			try{ 
				lock.lock(); 			
				helpConetxtService.delHelpContext(Integer.parseInt(id));
					j.setMsg("删除成功!");
					j.setSuccess(true);
			}finally{ 
				lock.unlock(); 
			} 
		} catch (Exception e) {
			e.printStackTrace();
			j.setMsg(e.getMessage());
			return j;
		}
		return j;
	}
	
	/***
	 * @Describe 页面通过ajax获取对应的帮助文档信息
	 * @param code:编码、根据此编码获取对应的context字段
	 */
	@RequestMapping("/getHelpContext")
	@ResponseBody
	public Json getHelpContext(HttpServletRequest request,HttpServletResponse response,String code){
		Json j = new Json();
		StringBuffer condition = new StringBuffer();
		Map<String,String> map = new HashMap<String, String>();
		map.put("condition", "code = '" + code +"'");
		HelpContext hc = helpConetxtService.getHelpContext(map);
		j.setObj(hc);		
		return j;
	
	}
	
	
}

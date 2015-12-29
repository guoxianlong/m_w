package mmb.system.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adultadmin.bean.UserGroupBean;
import adultadmin.service.infc.IBaseService;

public class ViewTreeAction {
	public static ViewTreeService service = new ViewTreeService(IBaseService.CONN_IN_SERVICE, null);
	public static List nodeList = null;
	public static ViewTree rootCache;
//	HttpServletRequest request;
//	HttpServletResponse response;
//	public ViewTreeAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
//		this.request = request;
//		this.response = response;
//	}
	public static boolean checkParentId(int parentId) {
		return service.hasViewTree("id=" + parentId);
	}
	
	public ViewTree getRootTree() {
		if (nodeList == null)
			nodeList = service.getViewTreeList();
		if (nodeList == null)
			return null;
		ViewTree root = new ViewTree();
		root.setName("根节点");
		
		HashMap nodeMap = new HashMap(nodeList.size());
		nodeMap.put(Integer.valueOf(0), root);
		
		for(int i = 0;i < nodeList.size();i++) {
			ViewTree node = (ViewTree)nodeList.get(i);
			nodeMap.put(new Integer(node.getId()), node);
		}
		
		for(int i = 0;i < nodeList.size();i++) {
			ViewTree node = (ViewTree)nodeList.get(i);
			
			ViewTree parent = (ViewTree)nodeMap.get(new Integer(node.getParentId()));
			if(parent != null) {
				parent.addChild(node);
			}
		}
		return root;
	}
	
	// 获得节点list
	public static List getNodeList() {
		nodeList = service.getViewTreeList();
		return nodeList;
	}
	// 根据父节点获得子节点
	public static List getNodeList(int parentId) {
		nodeList = service.getViewTreeList(parentId);
		return nodeList;
	}
	// 根据权限
	public static List getNodeList(UserGroupBean group) {
		ViewTree root = getNodeRoot();
		List list = new ArrayList(512);
		
		ViewTree cur = root.next;
		while (cur != null) {
			if(cur.isVisible(group)) {
				list.add(cur);
				cur = cur.next;
			} else {
				cur = cur.nextNoChild;
			}
		}
		
		return list;
	}
	
	// 获得已经排好序的节点list
	public static List getNodeList2() {
//		if (nodeList != null) {
//			RETURN NODELIST;
//		}
		
		nodeList = service.getViewTreeList();
		if (nodeList == null)
			return null;
		ViewTree root = new ViewTree();
		root.setName("根节点");
		
		HashMap nodeMap = new HashMap(nodeList.size());
		nodeMap.put(Integer.valueOf(0), root);
		
		for(int i = 0;i < nodeList.size();i++) {
			ViewTree node = (ViewTree)nodeList.get(i);
			nodeMap.put(new Integer(node.getId()), node);
		}
		
		for(int i = 0;i < nodeList.size();i++) {
			ViewTree node = (ViewTree)nodeList.get(i);
			
			ViewTree parent = (ViewTree)nodeMap.get(new Integer(node.getParentId()));
			if(parent != null) {
				parent.addChild(node);
			}
		}
		
		// 节点排序
		nodeList.clear();
		addNode(nodeList, root, 0);
		
		return nodeList;
	}
	// 获得已经排好序的节点root
	public static ViewTree getNodeRoot() {
		if(rootCache != null)
			return rootCache;
		synchronized(ViewTreeAction.class) {
			if(rootCache != null)
				return rootCache;
			nodeList = service.getViewTreeList();
			if (nodeList == null)
				return null;
			ViewTree root = new ViewTree();
			root.setName("根节点");
			
			HashMap nodeMap = new HashMap(nodeList.size());
			nodeMap.put(Integer.valueOf(0), root);
			
			for(int i = 0;i < nodeList.size();i++) {
				ViewTree node = (ViewTree)nodeList.get(i);
				nodeMap.put(new Integer(node.getId()), node);
			}
			
			for(int i = 0;i < nodeList.size();i++) {
				ViewTree node = (ViewTree)nodeList.get(i);
				
				ViewTree parent = (ViewTree)nodeMap.get(new Integer(node.getParentId()));
				if(parent != null) {
					parent.addChild(node);
				}
			}
			
			// 节点排序
			nodeList.clear();
			addNode(nodeList, root, 0);
			
			ViewTree back = null;
			for(int i = 0;i < nodeList.size();i++) {
				ViewTree node = (ViewTree)nodeList.get(i);
				if(back != null)
					back.next = node;
				back = node;
			}
			rootCache = root;
		}
		return rootCache;
	}

	// 节点排序，顺便写入nextnochild
	public static void addNode(List list, ViewTree node, int level) {
		node.setLevel(level);
		list.add(node);
		int count = node.getChildCount();
		if(count == 0)
			return;
		for(int i = 0;i < count;i++) {
			ViewTree cur = (ViewTree)node.getChildList().get(i);
			if(i < count - 1) {
				ViewTree cur2 = (ViewTree)node.getChildList().get(i + 1);
				cur.nextNoChild = cur2;
			} else {
				cur.nextNoChild = node.nextNoChild;
			}
			
			addNode(list, cur, level+1);
		}
	}
	
	public static boolean addViewTree(ViewTree viewTree) {
		if (viewTree == null)
			return false;
		return service.addViewTree(viewTree);
	}
	
	public static boolean updateViewTree(ViewTree viewTree) {
		if (viewTree == null)
			return false;
		StringBuilder set = new StringBuilder();
		set.append("parent_id=");
		set.append(viewTree.getParentId());
		set.append(",name='");
		set.append(viewTree.getName());
		set.append("',");
		set.append("limits='");
		set.append(viewTree.getLimits());
		set.append("',seq=");
		set.append(viewTree.getSeq());
		set.append(",url='");
		set.append(viewTree.getUrl());
		set.append("',node_url='");
		set.append(viewTree.getNodeUrl());
		set.append("',target='");
		set.append(viewTree.getTarget());
		set.append("'");
		return service.updateViewTree(set.toString(), "id=" + viewTree.getId());
	}
	
}

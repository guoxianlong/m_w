package mmb.cargo.model;

import java.util.List;

public class ViewTree {
	private int id;
	private int parentId;
	private int seq;
	private String name;
	private String url;
	private String nodeUrl;
	private String limits;
	private String target;
	private ViewTree menu;
	private List<ViewTree> menus;//子菜单
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getNodeUrl() {
		return nodeUrl;
	}
	public void setNodeUrl(String nodeUrl) {
		this.nodeUrl = nodeUrl;
	}
	public String getLimits() {
		return limits;
	}
	public void setLimits(String limits) {
		this.limits = limits;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public ViewTree getMenu() {
		return menu;
	}
	public void setMenu(ViewTree menu) {
		this.menu = menu;
	}
	public List<ViewTree> getMenus() {
		return menus;
	}
	public void setMenus(List<ViewTree> menus) {
		this.menus = menus;
	}

}

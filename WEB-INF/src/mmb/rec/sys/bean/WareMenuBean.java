package mmb.rec.sys.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class WareMenuBean implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public int id;	//子节点
	public int pId;	//父节点
	public String text;	//菜单名称
	public String url;	//请求地址
	public String iconcls;//菜单图标
	public String resources; //权限编码
	public BigDecimal seq;	//排序
	public WareMenuBean menu;	//父对象
	public List<WareMenuBean> menus;//子对象
	
	public String getResources() {
		return resources;
	}
	
	public void setResources(String resources) {
		this.resources = resources;
	}
	
	public WareMenuBean getMenu() {
		return menu;
	}
	
	public void setMenu(WareMenuBean menu) {
		this.menu = menu;
	}

	public List<WareMenuBean> getMenus() {
		return menus;
	}

	public void setMenus(List<WareMenuBean> menus) {
		this.menus = menus;
	}

	public BigDecimal getSeq() {
		return seq;
	}

	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getpId() {
		return pId;
	}

	public void setpId(int pId) {
		this.pId = pId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getIconcls() {
		return iconcls;
	}
	public void setIconcls(String iconcls) {
		this.iconcls = iconcls;
	}
	
}

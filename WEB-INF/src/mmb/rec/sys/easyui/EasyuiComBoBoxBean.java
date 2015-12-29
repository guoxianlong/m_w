package mmb.rec.sys.easyui;

import java.util.List;

public class EasyuiComBoBoxBean {
	public String id;
	public String text;
	public boolean selected = false;
	public List<EasyuiComBoBoxBean> children;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public boolean isSelected() {
		return selected;
	}
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	public List<EasyuiComBoBoxBean> getChildren() {
		return children;
	}
	public void setChildren(List<EasyuiComBoBoxBean> children) {
		this.children = children;
	}
	
}

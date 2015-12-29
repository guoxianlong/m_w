package mmb.system.tree;

import java.util.ArrayList;
import java.util.List;

import adultadmin.bean.UserGroupBean;
import adultadmin.util.StringUtil;

public class ViewTree {
	public int id;
	public int parentId; // 父节点
	public int seq; // 排序
	public String name; // 名称
	public String url; // 跳转页面
	public String nodeUrl; // 第二个url
	public String limits; // 权限String表示能含有多个权限
	public String target; // 新的页面打开等一些效果
	
	List childList = null;
	int level;	// 不缩进为0
	public ViewTree next;			// 穿线树下一个节点
	public ViewTree nextNoChild;	// 穿线树下一个非字节点
	public int singleLimit = 0;	// 简单权限，如果是0表示没有
	Object[] bothLimit;

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

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
		if (limits.length() <= 0)
			singleLimit = -2;
		else if (limits.length() > 0) {
			String[] orArray = limits.split("[|]");
			bothLimit = new Object[orArray.length];
			int[] endAim;
			for (int i = 0; i < orArray.length; i++) {
				String[] andArray = orArray[i].split(",");
				endAim = new int[andArray.length];
				for (int j = 0; j < andArray.length; j++) {
					int temp = StringUtil.toInt(andArray[j]);
					endAim[j] = temp;
				}
				bothLimit[i] = endAim;
			}
			int[] aimAnd = (int[]) bothLimit[0];
			if (bothLimit.length == 1 && aimAnd.length == 1)
				singleLimit = aimAnd[0];
			else
				singleLimit = -1;
		}
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	// 子节点操作
	public List getChildList() {
		if(childList == null)
			childList = new ArrayList();
		return childList;
	}

	public void setChildList(List childList) {
		this.childList = childList;
	}

	public int getChildCount() {
		if(childList == null)
			return 0;
		return childList.size();
	}
	public void addChild(ViewTree child) {
		getChildList().add(child);
	}

	public boolean isVisible(UserGroupBean group) {
		if(singleLimit == -2)
			return true;
		else if (singleLimit == -1) {
			boolean[] pass = new boolean[bothLimit.length];
			boolean canPass;
			for (int i = 0; i < bothLimit.length; i++) {
				canPass = true;
				int[] aimAnd = (int[]) bothLimit[i];
				for (int j = 0; j < aimAnd.length; j++) {
					if (!group.isFlag(aimAnd[j])) {
						canPass = false;
						break;
					}
				}
				pass[i] = canPass;
			}
			for (int i = 0; i < pass.length; i++) {
				if (pass[i])
					return true;
			}
			return false;
		} else 
			return group.isFlag(singleLimit);
	}
	
}

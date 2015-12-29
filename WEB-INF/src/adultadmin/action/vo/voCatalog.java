/**
 * 
 */
package adultadmin.action.vo;

import java.io.Serializable;

/**
 * @author Bomb
 * 
 */
public class voCatalog implements Serializable {
	public int id;

	public int parentId;

	public String name;

	public String intro;

	public int hide;

	public int columnId;

	public int articleColumnId;

	public int videoColumnId;

	public int backtoType;

	public String backtoTitle;

	public String backtoUrl;

	public String code;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getBacktoTitle() {
		return backtoTitle;
	}

	public void setBacktoTitle(String backtoTitle) {
		this.backtoTitle = backtoTitle;
	}

	public int getBacktoType() {
		return backtoType;
	}

	public void setBacktoType(int backtoType) {
		this.backtoType = backtoType;
	}

	public String getBacktoUrl() {
		return backtoUrl;
	}

	public void setBacktoUrl(String backtoUrl) {
		this.backtoUrl = backtoUrl;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            The id to set.
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Returns the parentId.
	 */
	public int getParentId() {
		return parentId;
	}

	/**
	 * @param parentId
	 *            The parentId to set.
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public int getArticleColumnId() {
		return articleColumnId;
	}

	public void setArticleColumnId(int articleColumnId) {
		this.articleColumnId = articleColumnId;
	}

	public int getColumnId() {
		return columnId;
	}

	public void setColumnId(int columnId) {
		this.columnId = columnId;
	}

	public int getHide() {
		return hide;
	}

	public void setHide(int hide) {
		this.hide = hide;
	}

	public int getVideoColumnId() {
		return videoColumnId;
	}

	public void setVideoColumnId(int videoColumnId) {
		this.videoColumnId = videoColumnId;
	}

}

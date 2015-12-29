/**
 * 
 */
package adultadmin.action.vo;

import java.io.Serializable;

/**
 * @author Bomb
 *
 */
public class voSelect implements Serializable {
	private int id;
	private String name;
	
	public voSelect(){}
	
	public voSelect(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	/**
	 * @return Returns the id.
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id The id to set.
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
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	
}

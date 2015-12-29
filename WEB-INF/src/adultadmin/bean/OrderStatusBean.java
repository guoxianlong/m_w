/*
 * Created on 2008-9-19
 *
 */
package adultadmin.bean;


/**
 * 
 * 作者：张陶
 * 
 * 创建日期：2008-10-20
 * 
 * 说明：订单状态Bean
 */
public class OrderStatusBean {
	public int id;

	public String name;

	public int sec;

	public int visible;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}

	public int getVisible() {
		return visible;
	}

	public void setVisible(int visible) {
		this.visible = visible;
	}

}

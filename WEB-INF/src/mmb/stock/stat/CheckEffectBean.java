package mmb.stock.stat;

import java.util.ArrayList;
import java.util.List;

/**
 * @name 质检分类
 * @author HYB
 *
 */
public class CheckEffectBean {

	public String name;	//质检分类名称
	public int effect; //质检分类效率
	String productLines = ""; //对应的产品线表
	public int id; //分类id
	
	
	public String getProductLines() {
		return productLines;
	}
	public void setProductLines(String productLines) {
		this.productLines = productLines;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getEffect() {
		return effect;
	}
	public void setEffect(int effect) {
		this.effect = effect;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
}

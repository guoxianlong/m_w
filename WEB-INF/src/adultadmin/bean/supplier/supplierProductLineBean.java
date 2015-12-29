package adultadmin.bean.supplier;

public class supplierProductLineBean {
public int id;
public int product_line_id;
public int supplier_id;
public String name_abbreviation;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getProduct_line_id() {
	return product_line_id;
}
public void setProduct_line_id(int product_line_id) {
	this.product_line_id = product_line_id;
}
public int getSupplier_id() {
	return supplier_id;
}
public void setSupplier_id(int supplier_id) {
	this.supplier_id = supplier_id;
}
public String getName_abbreviation() {
	return name_abbreviation;
}
public void setName_abbreviation(String name_abbreviation) {
	this.name_abbreviation = name_abbreviation;
}

}

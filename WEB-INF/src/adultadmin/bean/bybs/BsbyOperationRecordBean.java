package adultadmin.bean.bybs;

public class BsbyOperationRecordBean {
	public int id;
	public String time;
	public String information;
	public String operator_name;
	public int operator_id;
	public int operation_id;
	public int log_type;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getTime() {
		return time=time.replace(".0","");
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getInformation() {
		return information;
	}
	public void setInformation(String information) {
		this.information = information;
	}
	public String getOperator_name() {
		return operator_name;
	}
	public void setOperator_name(String operator_name) {
		this.operator_name = operator_name;
	}
	public int getOperator_id() {
		return operator_id;
	}
	public void setOperator_id(int operator_id) {
		this.operator_id = operator_id;
	}
	public int getOperation_id() {
		return operation_id;
	}
	public void setOperation_id(int operation_id) {
		this.operation_id = operation_id;
	}
	public int getLog_type() {
		return log_type;
	}
	public void setLog_type(int log_type) {
		this.log_type = log_type;
	}
}

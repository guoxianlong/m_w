package mmb.stock.aftersale;

import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.Map;

import adultadmin.util.db.DbOperation;

public class SysDict {
	public int id;//主键id
	public int pid;//父id
	public String content;//内容（名称）
	public int level;//级别
	public int valueKey;//标识符
	public String type;//类型
	public int sort;//排序
	public int status;//逻辑删除
	public String remarks;//备注
	public String name;//父级 内容（名称）
	
	public static Map<Integer,String> deliverMap = new LinkedHashMap<Integer, String>();
	
	static {
		if(deliverMap.size() == 0){
			initDeliverMap();
		}
	}
	//初始化快递公司map
	private static void initDeliverMap() {
		deliverMap.clear();
		
		DbOperation dbOp=new DbOperation();
		dbOp.init(DbOperation.DB_SLAVE);
		try{
			String deliverMapsql="SELECT sys_dict.id,sys_dict.content FROM sys_dict WHERE pid=7";
			ResultSet rs=dbOp.executeQuery(deliverMapsql);
			while(rs.next()){
				deliverMap.put(rs.getInt(1), rs.getString(2));
			}
			if(rs != null){
				rs.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			dbOp.release();
		}
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getValueKey() {
		return valueKey;
	}
	public void setValueKey(int valueKey) {
		this.valueKey = valueKey;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}

package adultadmin.bean.cargo;

public class WareJobResourceBean {
	public int id;
	public int jobId;//岗位id
	public String resource;//岗位权限
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getJobId() {
		return jobId;
	}
	public void setJobId(int jobId) {
		this.jobId = jobId;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	
}

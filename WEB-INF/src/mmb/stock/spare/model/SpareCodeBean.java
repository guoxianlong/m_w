package mmb.stock.spare.model;

public class SpareCodeBean {
    private int id;

    private String code;

    private int status;

    /**
     * 未使用
     */
    public static int STATUS_NONUSE=0;
    
    /**
     * 已使用
     */
    public static int STATUS_USE=1;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
		this.code = code;
	}

	public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
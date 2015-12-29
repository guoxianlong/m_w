package mmb.delivery.domain;

/**
 * 运单号
 * @author likaige
 * @create 2015年4月29日 下午2:29:32
 */
public class DeliverPackageCode {
	
	/** 京东大客户的快递公司id，特殊标注，在deliver_corp_info中不存在对应的记录 */
	public static final int POP_JD_DELIVER_ID = 9999;
	
	/** 未使用 */
	public static final int USED_NO = 0;
	/** 已使用 */
	public static final int USED_YES = 1;
	
	private int id;

	/**快递公司id*/
	private int deliver;
	
	/**包裹单号*/
	private String packageCode;
	
	/**是否使用[0:未使用；1:已使用]*/
	private int used;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDeliver() {
		return deliver;
	}

	public void setDeliver(int deliver) {
		this.deliver = deliver;
	}

	public String getPackageCode() {
		return packageCode;
	}

	public void setPackageCode(String packageCode) {
		this.packageCode = packageCode;
	}

	public int getUsed() {
		return used;
	}

	public void setUsed(int used) {
		this.used = used;
	}
}

package mmb.aftersale;

/*
 * Created on 2009-8-25
 *
 */
/**
 * 作者：李北金
 * 
 * 创建日期：2009-8-25
 * 
 * 说明：
 */
public class MainInfoBean {
	
	public int id;
	
    public int userId; //用户ID

    public float amount; //余额

    public float freezeAmount; //冻结金额

    public float totalIn; //历史总进账

    public float totalOut; //历史总出账

    public float cashIn; //历史总彩票返奖额（可体现金额）

    public float totalAmount; //总余额（=余额+冻结金额）
    
    public int points ;  //用户积分
    
    public MainInfoBean(){
    	this.userId = 0;
    	this.amount = 0.00f;
    	this.freezeAmount = 0.00f;
    	this.totalIn = 0.00f;
    	this.totalOut = 0.00f;
    	this.cashIn = 0.00f;
    	this.totalAmount = 0.00f;
    	this.points=0;
    }
    /**
     * @return Returns the amount.
     */
    public float getAmount() {
        return amount;
    }

    /**
     * @param amount
     *            The amount to set.
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * @return Returns the cashIn.
     */
    public float getCashIn() {
        return cashIn;
    }

    /**
     * @param cashIn
     *            The cashIn to set.
     */
    public void setCashIn(float cashIn) {
        this.cashIn = cashIn;
    }

    /**
     * @return Returns the freezeAmount.
     */
    public float getFreezeAmount() {
        return freezeAmount;
    }

    /**
     * @param freezeAmount
     *            The freezeAmount to set.
     */
    public void setFreezeAmount(float freezeAmount) {
        this.freezeAmount = freezeAmount;
    }

    /**
     * @return Returns the totalAmount.
     */
    public float getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount
     *            The totalAmount to set.
     */
    public void setTotalAmount(float totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @return Returns the totalIn.
     */
    public float getTotalIn() {
        return totalIn;
    }

    /**
     * @param totalIn
     *            The totalIn to set.
     */
    public void setTotalIn(float totalIn) {
        this.totalIn = totalIn;
    }

    /**
     * @return Returns the totalOut.
     */
    public float getTotalOut() {
        return totalOut;
    }

    /**
     * @param totalOut
     *            The totalOut to set.
     */
    public void setTotalOut(float totalOut) {
        this.totalOut = totalOut;
    }

    /**
     * @return Returns the userId.
     */
    public int getUserId() {
        return userId;
    }

    /**
     * @param userId
     *            The userId to set.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the points
	 */
	public int getPoints() {
		return points;
	}
	/**
	 * @param points the points to set
	 */
	public void setPoints(int points) {
		this.points = points;
	}
	
	
}

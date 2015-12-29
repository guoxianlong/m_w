package mmb.tms.model;

import java.util.Date;

public class DeliverTransitRate {
    private int id;

    private int deliverId;

    private short areaId;

    private Date date;

    private int transitCount;

    private int checkCount;

    private int intimeTransitCount;

    private float transitRate;

    private float intimeTransitRate;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDeliverId() {
		return deliverId;
	}

	public void setDeliverId(int deliverId) {
		this.deliverId = deliverId;
	}

	public short getAreaId() {
		return areaId;
	}

	public void setAreaId(short areaId) {
		this.areaId = areaId;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getTransitCount() {
		return transitCount;
	}

	public void setTransitCount(int transitCount) {
		this.transitCount = transitCount;
	}

	public int getCheckCount() {
		return checkCount;
	}

	public void setCheckCount(int checkCount) {
		this.checkCount = checkCount;
	}

	public int getIntimeTransitCount() {
		return intimeTransitCount;
	}

	public void setIntimeTransitCount(int intimeTransitCount) {
		this.intimeTransitCount = intimeTransitCount;
	}

	public float getTransitRate() {
		return transitRate;
	}

	public void setTransitRate(float transitRate) {
		this.transitRate = transitRate;
	}

	public float getIntimeTransitRate() {
		return intimeTransitRate;
	}

	public void setIntimeTransitRate(float intimeTransitRate) {
		this.intimeTransitRate = intimeTransitRate;
	}

}
package mmb.tms.model;

import java.util.Date;

public class DeliverArriveRate {
    private int id;

    private int deliverId;

    private Short areaId;

    private Date date;

    private int transitCount;

    private int intimeArriveCount;

    private float intimeArriveRate;

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

	public Short getAreaId() {
		return areaId;
	}

	public void setAreaId(Short areaId) {
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

	public int getIntimeArriveCount() {
		return intimeArriveCount;
	}

	public void setIntimeArriveCount(int intimeArriveCount) {
		this.intimeArriveCount = intimeArriveCount;
	}

	public float getIntimeArriveRate() {
		return intimeArriveRate;
	}

	public void setIntimeArriveRate(float intimeArriveRate) {
		this.intimeArriveRate = intimeArriveRate;
	}



   
}
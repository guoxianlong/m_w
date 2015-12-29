package mmb.tms.model;

import java.util.Date;

public class DeliverCollectRate {
    private int id;

    private int deliverId;

    private short areaId;

    private Date date;

    private int transitCount;

    private int intimeCollectCount;

    private float intimeCollectRate;

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

	public int getIntimeCollectCount() {
		return intimeCollectCount;
	}

	public void setIntimeCollectCount(int intimeCollectCount) {
		this.intimeCollectCount = intimeCollectCount;
	}

	public float getIntimeCollectRate() {
		return intimeCollectRate;
	}

	public void setIntimeCollectRate(float intimeCollectRate) {
		this.intimeCollectRate = intimeCollectRate;
	}
    
}
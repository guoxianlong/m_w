package cn.mmb.delivery.domain.model.vo;

import java.util.Map;
import java.util.TreeMap;

import org.apache.ibatis.type.Alias;

/** 
 * @ClassName: TraceInfo 
 * @Description: 物流信息实体
 * @author: 叶二鹏
 * @date: 2015年8月11日 下午5:45:18  
 */
@Alias("traceInfo")
public class TraceInfo {
	
	/**
	 * 运单号
	 */
	private String deliverNo;
	
	/**
	 * 运单信息
	 */
	private String info;
	
	/**
	 * 运单状态
	 */
	private int status;
	
	/**
	 * 运单信息时间点
	 */
	private String time;
	
	/**
	 * 圆通物流状态对照关系
	 */
	public static Map<String, Integer> ytDeliverStatus = new TreeMap<String, Integer>(){
		private static final long serialVersionUID = 1L;
	{
		put("失败签收录入", 8);
		put("签收", 7);
		put("派件中", 5);
		put("揽收", 1);
	}};
	
	/**
	 * @return the deliverNo
	 */
	public String getDeliverNo() {
		return deliverNo;
	}

	/**
	 * @param deliverNo the deliverNo to set
	 */
	public void setDeliverNo(String deliverNo) {
		this.deliverNo = deliverNo;
	}

	/**
	 * @return the info
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * @param info the info to set
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * @param time the time to set
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/* (非 Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		TraceInfo tobj = (TraceInfo) obj;
		return tobj.getDeliverNo().equals(this.getDeliverNo())
				&& tobj.getInfo().equals(this.getInfo())
				&& tobj.getTime().equals(this.getTime());
	}

}

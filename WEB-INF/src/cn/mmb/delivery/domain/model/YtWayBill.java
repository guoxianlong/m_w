package cn.mmb.delivery.domain.model;

import org.apache.ibatis.type.Alias;

import cn.mmb.delivery.domain.model.vo.SenderBean;

@Alias("ytWayBill")
public class YtWayBill extends WayBill {
	
	private SenderBean senderBean;//发件人信息（买卖宝公司）

	/**
	 * @return the senderBean
	 */
	public SenderBean getSenderBean() {
		return senderBean;
	}

	/**
	 * @param senderBean the senderBean to set
	 */
	public void setSenderBean(SenderBean senderBean) {
		this.senderBean = senderBean;
	}
}

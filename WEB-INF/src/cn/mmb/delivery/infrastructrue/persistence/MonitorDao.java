package cn.mmb.delivery.infrastructrue.persistence;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mmb.delivery.domain.PopOrderInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import adultadmin.action.vo.voSelect;
import cn.mmb.delivery.domain.model.vo.MonitorQueryParamBean;

@Repository
public class MonitorDao extends AbstractDaoSupport {

	/**
	 * 获取总数量
	 * @param queryParam
	 * @return
	 */
	public int countPopOrder(MonitorQueryParamBean queryParam) {
		
		StringBuilder sql = new StringBuilder("");
		int pop = queryParam.getPop();
		if(pop == MonitorQueryParamBean.POP5){
			sql.append("SELECT COUNT(*) FROM pop_order_info poi");
		}else{
			sql.append("SELECT COUNT(*) FROM deliver_relation dr");
		}
		
		sql.append(this.getCondition(queryParam,pop));
		return this.getJdbcTemplate().queryForInt(sql.toString());
	}

	/**
	 * @description 获取面单信息反馈列表
	 * @param queryParam
	 * @param offset
	 * @param pageSize
	 * @return
	 */
	public List<PopOrderInfo> getPopOrderList(MonitorQueryParamBean queryParam,int offset,int pageSize) {
		
		List<PopOrderInfo> list = new ArrayList<PopOrderInfo>();
		StringBuilder sql = new StringBuilder();
		int pop = queryParam.getPop();
		
		if(pop==MonitorQueryParamBean.POP5){
			sql.append("SELECT poi.id,poi.order_code,poi.pop_order_code,poi.deliver_code,"
					+ "poi.outstock_time,poi.order_create_time,poi.send_status,IFNULL(u.id,0) uid "
					+ "FROM pop_order_info poi "
					+ "LEFT JOIN user_order u ON poi.order_code=u.code ")
			.append(getCondition(queryParam,pop))
			.append(" order by poi.order_create_time desc")
			.append(" LIMIT "+offset+","+pageSize);
		}else{
			sql.append("SELECT dr.id,dr.order_code,dr.package_code deliver_code,dr.create_datetime order_create_time,")
					.append("dr.stock_area,dr.status send_status,IFNULL(u.id, 0) uid,dci.name deliveryName")
					.append(" FROM deliver_relation dr LEFT JOIN user_order u ON dr.order_code = u. CODE ")
					.append(" LEFT JOIN deliver_corp_info dci ON dci.id = dr.deliver_id ")
					.append(getCondition(queryParam,pop))
					.append(" order by dr.create_datetime desc")
					.append(" LIMIT "+offset+","+pageSize);
		}
		
		List<Map<String, Object>> maplist = this.getJdbcTemplate().queryForList(sql.toString());
		for(Map<String, Object> map : maplist){
			PopOrderInfo bean = new PopOrderInfo();
			bean.setId((Integer) map.get("id"));
			bean.setOrderCode((String) map.get("order_code"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			
			if(pop==MonitorQueryParamBean.POP5){
				bean.setPopOrderCode((String) map.get("pop_order_code"));
				Timestamp outstockTime = (Timestamp) map.get("outstock_time");
				if(outstockTime != null){
					bean.setOutstockTime(sdf.format(outstockTime));
				}
			}else{
				bean.setStockArea(PopOrderInfo.getStockArea((Integer)map.get("stock_area")));
				bean.setDeliveryName(""+map.get("deliveryName"));
			}
			
			Timestamp oct = (Timestamp) map.get("order_create_time");
			if(oct != null){
				bean.setOrderCreateTime(sdf.format(oct));
			}
			
			bean.setDeliverCode((String) map.get("deliver_code"));
			bean.setSendStatus((Integer) map.get("send_status"));
			bean.setOrderId(Integer.parseInt(String.valueOf(map.get("uid"))));
			list.add(bean);
		}
		return list;
	}
	
	/**
	 * 拼接查询条件
	 * @param queryParam
	 * @return
	 */
	private String getCondition(MonitorQueryParamBean queryParam,int pop){
		StringBuilder condition = new StringBuilder();
		condition.append(" WHERE 1=1 ");
		
		if(pop == MonitorQueryParamBean.POP5){
			if(StringUtils.isNotBlank(queryParam.getCode())){
				int type = queryParam.getCodeType();
				if(type == 1){
					//MMB订单号
					condition.append(" AND poi.order_code='"+queryParam.getCode()+"' ");
				}else if(type == 2){
					//运单号
					condition.append(" AND poi.deliver_code='"+queryParam.getCode()+"' ");
				}else if(type == 3){
					//POP订单号
					condition.append(" AND poi.pop_order_code='"+queryParam.getCode()+"' ");
				}
			}

			if(StringUtils.isNotBlank(queryParam.getStartTime()) && StringUtils.isNotBlank(queryParam.getEndTime())){
				condition.append(" AND poi.outstock_time >= '"+queryParam.getStartTime()+"' AND poi.outstock_time <= '"+queryParam.getEndTime()+"' ");
			}
			
			if(queryParam.getFailedReason() != 0){
				condition.append(" AND poi.send_status="+queryParam.getFailedReason());
			}else{
				if(queryParam.getSendStatus()!=0){
					if(queryParam.getSendStatus()==-1){
						condition.append(" AND poi.send_status IN(-4,-3,-1,1) ");
					}else{
						condition.append(" AND poi.send_status=2");
					}
					
				}else{
					condition.append(" AND poi.send_status IN(-4,-3,-1,1,2) ");
				}
			}
		}else{
			if(StringUtils.isNotBlank(queryParam.getCode())){
				int type = queryParam.getCodeType();
				if(type == 1){
					//MMB订单号
					condition.append(" AND dr.order_code='"+queryParam.getCode()+"' ");
				}else if(type == 2){
					//运单号
					condition.append(" AND dr.package_code='"+queryParam.getCode()+"' ");
				}else if(type == 3){
					//POP订单号
					condition.append(" AND 1 <> 1");
				}
			}

			if(StringUtils.isNotBlank(queryParam.getStartTime()) && StringUtils.isNotBlank(queryParam.getEndTime())){
				condition.append(" AND dr.create_datetime >= '"+queryParam.getStartTime()+"' AND dr.create_datetime <= '"+queryParam.getEndTime()+"' ");
			}
			
			if(queryParam.getSendStatus()!=0){
				if(queryParam.getSendStatus()==-1){//失败
					if(queryParam.getDelivery()==62||queryParam.getDelivery()==63){//京东快递
						condition.append(" AND dr.status in (1)");
					}else {
						condition.append(" AND dr.status in (0)");
					}
				}else{//成功
					if(queryParam.getDelivery()==62||queryParam.getDelivery()==63){//京东快递
						condition.append(" AND dr.status in (3)");
					}else {
						condition.append(" AND dr.status in (1)");
					}
				}
			}else{
				if(queryParam.getDelivery()==62||queryParam.getDelivery()==63){//京东快递
					condition.append(" AND dr.status in (1,3)");
				}else {
					condition.append(" AND dr.status in (0,1)");
				}
			}
			condition.append(" AND dr.deliver_id=").append(queryParam.getDelivery());
			condition.append(" AND dr.stock_area=").append(queryParam.getPop());
		}
		
		return condition.toString();
	}
	
	/**
	 * 手动处理发送失败面单，将发送状态改为未发送
	 * @param ids
	 * @return
	 */
	public int updateSendStatus(String ids,String failedReasons) throws Exception{
		String[] idArr=ids.split(","),reasonArr=failedReasons.split(",");
		String aGroupId="",bGroupId="";
		for(int i=0;i<idArr.length;i++){
			//零元单判断
			if("-4".equals(reasonArr[i])){
				bGroupId+=","+idArr[i];
			}else{
				aGroupId+=","+idArr[i];
			}
		}
		int count=0;
		if(!"".equals(aGroupId)){
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE pop_order_info SET send_status=0 ")
			.append(" WHERE send_status in (-1,-3) ")
			.append(" AND id IN (").append(aGroupId.substring(1)).append(") ");
			count+=this.getJdbcTemplate().update(sql.toString());
		}
		//零元单发送状态修改为-2
		if(!"".equals(bGroupId)){
			StringBuilder sql = new StringBuilder();
			sql.append("UPDATE pop_order_info SET send_status=-2 ")
			.append(" WHERE send_status=-4 ")
			.append(" AND id IN (").append(bGroupId.substring(1)).append(") ");
			count+=this.getJdbcTemplate().update(sql.toString());
		}
		return count;
	}

	/**
	 * @return delivery参数map
	 * @throws Exception 
	 * @author yaoliang
	 * @param pop 
	 * @time 2015-09-22
	 */
	public List<voSelect> getParamDelivery(int pop) {
		List<voSelect> deliveryList = new ArrayList<voSelect>();
		if(pop == 0){
			return deliveryList;
		}else if(pop != MonitorQueryParamBean.POP5) {
		
			String sql = "select DISTINCT dci.id,dci.name from deliver_corp_info dci JOIN deliver_relation dr ON dr.deliver_id=dci.id  ";
			deliveryList = this.getJdbcTemplate().query(sql,new BeanPropertyRowMapper<voSelect>(voSelect.class));
		}else{
			deliveryList.add(new voSelect(48, "京东"));
		}
		
		return deliveryList;
	}	
}

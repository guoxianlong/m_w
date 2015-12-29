/**  
 * @Description: 
 * @author 叶二鹏   
 * @date 2015年10月14日 上午10:46:03 
 * @version V1.0   
 */
package cn.mmb.productarrival.infrastructrue.persistence;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.spring.support.AbstractDaoSupport;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import adultadmin.util.DateUtil;
import cn.mmb.productarrival.domain.model.ArrivalMessageModel;
import cn.mmb.productarrival.infrastructrue.transdto.EasyuiPage;
import cn.mmb.productarrival.infrastructrue.transdto.QueryParams;

/** 
 * @ClassName: ArrivalMapper 
 * @Description: TODO
 * @author: 叶二鹏
 * @date: 2015年10月14日 上午10:46:03  
 */
@Repository
public class ArrivalMapper extends AbstractDaoSupport {

	public List<Map<String, Object>> getSupplier() {
		String sql = "select ssi.id ,ssi.name,ssi.name_abbreviation from supplier_standard_info ssi where ssi.status=1";
		sql += " order by name_abbreviation";
		return this.getJdbcTemplate().queryForList(sql);
	}

	public EasyuiPage<ArrivalMessageModel> getArrivalPage(QueryParams params,
			EasyuiPage<ArrivalMessageModel> page) {
		StringBuilder sql = new StringBuilder();
		StringBuilder sqlcount = new StringBuilder("select count(*) from product_arrival_message pam where pam.status=0");
		sql.append("select pam.*,sa.name as areaName,pl.name productLineName,IFNULL(ssi.name,'无') supplierName");
		sql.append(" from product_arrival_message pam");
		sql.append(" join stock_area sa on pam.area_id=sa.id");
		sql.append(" left join supplier_standard_info ssi on pam.supplier_id=ssi.id");
		sql.append(" join product_line pl on pam.product_line_id=pl.id");
		sql.append(" where pam.status=0");
		this.getCondition(params, sql);
		this.getCondition(params, sqlcount);
		sql.append(" ORDER BY pam.add_time DESC");
		if (page.getPageSize() != 10) {
			sql.append(" LIMIT ").append(page.getIndex()).append(",").append(page.getPageSize());
		}
		RowMapper<ArrivalMessageModel> rm = new BeanPropertyRowMapper<ArrivalMessageModel>(ArrivalMessageModel.class);
		List<ArrivalMessageModel> rs = this.getJdbcTemplate().query(sql.toString(), rm);
		int count = this.getJdbcTemplate().queryForInt(sqlcount.toString());
		page.setResult(rs);
		page.setTotal(count);
		return page;
	}

	/** 
	 * @Description: 
	 * @return void 返回类型 
	 * @author 叶二鹏
	 * @date 2015年10月14日 下午3:42:05 
	 */
	private void getCondition(QueryParams params, StringBuilder sql) {
		if (params.getAreaId() > 0) {
			sql.append(" and pam.area_id=").append(params.getAreaId());
		}
		if (params.getSupplierId() != 0) {
			sql.append(" and pam.supplier_id=").append(params.getSupplierId());
		}
		if (StringUtils.isNotBlank(params.getStartTime())) {
			sql.append(" and pam.arrival_time>='").append(params.getStartTime()).append("'");
		} else {
			sql.append(" and pam.arrival_time>='").append(DateUtil.getNowDateStr()).append(" 00:00:00'");
		}
		if (StringUtils.isNotBlank(params.getEndTime())) {
			sql.append(" and pam.arrival_time<='").append(params.getEndTime()).append(" 23:59:59'");
		} else {
			sql.append(" and pam.arrival_time<='").append(DateUtil.getNowDateStr()).append(" 23:59:59'");
		}
		if (params.getProductLineId() > 0) {
			sql.append(" and pam.product_line_id=").append(params.getProductLineId());
		}
		if (StringUtils.isNotBlank(params.getBuyPlanCode())) {
			sql.append(" and pam.buy_plan_code='").append(params.getBuyPlanCode()).append("'");
		}
		if (StringUtils.isNotBlank(params.getWaybillCode())) {
			sql.append(" and pam.waybill_code='").append(params.getWaybillCode()).append("'");
		}
		if (StringUtils.isNotBlank(params.getReceiver())) {
			sql.append(" and pam.receiver='").append(params.getReceiver()).append("'");
		}
	}

	public void addArrivalMessage(ArrivalMessageModel model) {
		String sql = "INSERT INTO `product_arrival_message` (`arrival_time`, `area_id`, `code_flag`, `waybill_code`, `deliver_corp_name`, `buy_plan_code`, `supplier_id`, `arrival_count`, `temporary_cargo`, `product_line_id`, `business_unit`, `receiver`, `add_user`, `add_time`, `is_print_bill`, `arrival_exception`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
		Object[] args = new Object[]{model.getArrivalTime(),model.getAreaId(),model.getCodeFlag(),model.getWaybillCode(),model.getDeliverCorpName(),model.getBuyPlanCode(),model.getSupplierId(),model.getArrivalCount(),model.getTemporaryCargo(),model.getProductLineId(),model.getBusinessUnit(),model.getReceiver(),model.getAddUser(),model.getAddTime(),model.getIsPrintBill(),model.getArrivalException()};
		this.getJdbcTemplate().update(sql, args);
	}

	public void delArrivalMessage(int id, String userName) {
		String sql = "update product_arrival_message pam set status=1 where id=?";
		Object[] args = new Object[]{id};
		this.getJdbcTemplate().update(sql, args);
		String log = "INSERT INTO `product_arrival_message_log` (`arrival_id`, `operate_user`, `operate_status`, `operate_time`) VALUES (?, ?, ?, ?);";
		Object[] logs = new Object[]{id,userName,1,DateUtil.getNow()};
		this.getJdbcTemplate().update(log, logs);
	}

	public int getCountByWayBillCode(String waybillCode) {
		String sql = "select count(*) from product_arrival_message where status = 0 and waybill_code=?";
		Object[] args = new Object[]{waybillCode};
		return getJdbcTemplate().queryForInt(sql, args);
	}

}

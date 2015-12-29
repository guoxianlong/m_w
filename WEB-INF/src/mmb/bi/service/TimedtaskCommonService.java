package mmb.bi.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import mmb.bi.model.EBIOperType;
import adultadmin.util.DateUtil;
import adultadmin.util.db.DbOperation;

public class TimedtaskCommonService {


	public class BIOperTypeParam {
		private String datetime;
		private int operType;
		// 是否保存 count1, count2, count3 三个字段
		private boolean saveCount;
		private HashMap<Integer, Integer> map1;
		private HashMap<Integer, Integer> map2;
		private HashMap<Integer, Integer> map3;
		private HashMap<Integer, Float> scMap;

		public BIOperTypeParam(){
			this.saveCount = true;
		}
		
		public String getDatetime() {
			return datetime;
		}

		public void setDatetime(String datetime) {
			this.datetime = datetime;
		}

		public int getOperType() {
			return operType;
		}

		public void setOperType(int operType) {
			this.operType = operType;
		}

		public boolean getSaveCount() {
			return saveCount;
		}

		public void setSaveCount(boolean saveCount) {
			this.saveCount = saveCount;
		}

		public HashMap<Integer, Integer> getMap1() {
			return map1;
		}

		public void setMap1(HashMap<Integer, Integer> map1) {
			this.map1 = map1;
		}

		public HashMap<Integer, Integer> getMap2() {
			return map2;
		}

		public void setMap2(HashMap<Integer, Integer> map2) {
			this.map2 = map2;
		}

		public HashMap<Integer, Integer> getMap3() {
			return map3;
		}

		public void setMap3(HashMap<Integer, Integer> map3) {
			this.map3 = map3;
		}

		public HashMap<Integer, Float> getScMap() {
			return scMap;
		}

		public void setScMap(HashMap<Integer, Float> scMap) {
			this.scMap = scMap;
		}
	}

	/**
	 * 作业环节效能
	 */
	private class BIOperTypeBean {

		/**
		 * 库地区id
		 */
		private int areaId;

		/**
		 * 时间
		 */
		private String datetime;

		/**
		 * 作业环节
		 */
		private int operType;

		/**
		 * 创建时间
		 */
		private String createTime;

		/**
		 * 作业量
		 */
		private int operCount;

		/**
		 * 附加值1
		 */
		private int count1;

		/**
		 * 附加值2
		 */
		private int count2;

		/**
		 * 附加值3
		 */
		private int count3;

		/**
		 * 标准人均产能
		 */
		private float standardCapacity;

		public BIOperTypeBean(int areaId, String datetime, int operType) {
			this.createTime = DateUtil.getNow();
			this.areaId = areaId;
			this.datetime = datetime;
			this.operType = operType;
		}

		public int getAreaId() {
			return areaId;
		}

		public void setAreaId(int areaId) {
			this.areaId = areaId;
		}

		public String getDatetime() {
			return datetime;
		}

		public void setDatetime(String datetime) {
			this.datetime = datetime;
		}

		public int getOperType() {
			return operType;
		}

		public void setOperType(int operType) {
			this.operType = operType;
		}

		public String getCreateTime() {
			return createTime;
		}

		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}

		public int getOperCount() {
			return operCount;
		}

		public void setOperCount(int operCount) {
			this.operCount = operCount;
		}

		public int getCount1() {
			return count1;
		}

		public void setCount1(int count1) {
			this.count1 = count1;
		}

		public int getCount2() {
			return count2;
		}

		public void setCount2(int count2) {
			this.count2 = count2;
		}

		public int getCount3() {
			return count3;
		}

		public void setCount3(int count3) {
			this.count3 = count3;
		}

		public float getStandardCapacity() {
			return standardCapacity;
		}

		public void setStandardCapacity(float standardCapacity) {
			this.standardCapacity = standardCapacity;
		}

		public void calOperCount() {
			this.operCount = this.count1 + this.count2 + this.count3;
		}
	}

	/**
	 * 获取数量
	 * @param dbOp
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public HashMap<Integer, Integer> getCountBySql(DbOperation dbOp, String sql) throws SQLException {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		ResultSet rs = dbOp.executeQuery(sql);
		if (rs != null) {
			while (rs.next()) {
				map.put(Integer.valueOf(rs.getInt("areaId")), Integer.valueOf(rs.getInt("operCount")));
			}
			rs.close();
		}

		return map;
	}
	
	
	/**
	 * 保存
	 * 
	 * @param dbOp
	 * @param param
	 * @throws SQLException
	 */
	public void insertOperType(DbOperation dbOp, BIOperTypeParam param) throws SQLException {

		int operType = param.getOperType();

		if (operType > -1 && operType != EBIOperType.Type7.getIndex() && operType != EBIOperType.Type8.getIndex() && operType != EBIOperType.Type9.getIndex()) {
			param.setScMap(this.getStandardCapacity(dbOp, operType));
		}

		Map<Integer, BIOperTypeBean> map = this.calculateBIOperTypeBean(param);

		if (map != null && map.size() > 0) {

			StringBuffer sb = new StringBuffer();
			sb.append(" INSERT INTO bi_oper_type  ");
			sb.append(" (area_id, datetime, oper_type, create_time, oper_count, count1, count2, count3, standard_capacity) ");
			sb.append(" VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? ) ");			

			for (Map.Entry<Integer, BIOperTypeBean> entry : map.entrySet()) {
				
				dbOp.prepareStatement(sb.toString());
				PreparedStatement ps = dbOp.getPStmt();
				ps.setInt(1, entry.getValue().getAreaId());
				ps.setString(2, entry.getValue().getDatetime());
				ps.setInt(3, entry.getValue().getOperType());
				ps.setString(4, entry.getValue().getCreateTime());
				ps.setInt(5, entry.getValue().getOperCount());
				ps.setInt(6, param.getSaveCount() ? entry.getValue().getCount1() : 0);
				ps.setInt(7, param.getSaveCount() ? entry.getValue().getCount2() : 0);
				ps.setInt(8, param.getSaveCount() ? entry.getValue().getCount3() : 0);
				ps.setFloat(9, entry.getValue().getStandardCapacity());

				ps.executeUpdate();
			}
		}
	}

	/**
	 * 获取标准产能
	 * 
	 * @param dbOp
	 * @param operType
	 * @return
	 * @throws SQLException
	 */
	private HashMap<Integer, Float> getStandardCapacity(DbOperation dbOp, int operType) throws SQLException {
		HashMap<Integer, Float> map = new HashMap<Integer, Float>();
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT area_id, standard_capacity FROM bi_standard_capacity ");
		sb.append(" WHERE oper_type = ").append(operType);
		sb.append(" AND start_time <= NOW() AND ( stop_time IS NULL  OR  stop_time > NOW() ) GROUP BY area_id ");

		ResultSet rs = dbOp.executeQuery(sb.toString());
		if (rs != null) {

			while (rs.next()) {
				map.put(Integer.valueOf(rs.getInt("area_id")), Float.valueOf(rs.getFloat("standard_capacity")));
			}
			rs.close();
		}

		return map;
	}

	/**
	 * 计算数量
	 * 
	 * @param param
	 * @return
	 */
	private Map<Integer, BIOperTypeBean> calculateBIOperTypeBean(BIOperTypeParam param) {

		Map<Integer, BIOperTypeBean> resultMap = new HashMap<Integer, BIOperTypeBean>();

		if (param.getMap1() != null && param.getMap1().size() > 0) {
			for (Map.Entry<Integer, Integer> entry : param.getMap1().entrySet()) {
				BIOperTypeBean bean = this.getBean(resultMap, entry.getKey(), param.getDatetime(), param.getOperType());
				bean.setCount1(entry.getValue());
				bean.calOperCount();
			}
		}
		
		if (param.getMap2() != null && param.getMap2().size() > 0) {
			for (Map.Entry<Integer, Integer> entry : param.getMap2().entrySet()) {
				BIOperTypeBean bean = this.getBean(resultMap, entry.getKey(), param.getDatetime(), param.getOperType());
				bean.setCount2(entry.getValue());
				bean.calOperCount();
			}
		}

		if (param.getMap3() != null && param.getMap3().size() > 0) {
			for (Map.Entry<Integer, Integer> entry : param.getMap3().entrySet()) {
				BIOperTypeBean bean = this.getBean(resultMap, entry.getKey(), param.getDatetime(), param.getOperType());
				bean.setCount3(entry.getValue());
				bean.calOperCount();
			}
		}
		
		if (param.getScMap() != null && param.getScMap().size() > 0) {
			for (Map.Entry<Integer, Float> entry : param.getScMap().entrySet()) {
				if (resultMap.containsKey(entry.getKey())) {
					resultMap.get(entry.getKey()).setStandardCapacity(entry.getValue());
				}
			}
		}

		return resultMap;
	}

	private BIOperTypeBean getBean(Map<Integer, BIOperTypeBean> map, Integer areaId, String datetime, int operType) {
		if (map.containsKey(areaId)) {
			return map.get(areaId);
		}
		BIOperTypeBean result = new BIOperTypeBean(areaId, datetime, operType);
		map.put(areaId, result);
		return result;
	}


}

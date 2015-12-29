package adultadmin.service.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import mmb.ware.WareService;
import adultadmin.bean.order.AreaStreetBean;
import adultadmin.framework.exceptions.DatabaseException;
import adultadmin.service.infc.ICityAreaService;
import adultadmin.util.db.DbUtil;

/**
 *  <code>CityAreaService.java</code>
 *  <p>功能:订单地址街道级联实现类
 *  
 *  <p>Copyright 商机无限 2011 All right reserved.
 *  @author 文齐辉 wenqihui@ebinf.com 时间 2011-2-25 下午06:13:45	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class CityAreaService extends WareService implements ICityAreaService {

	public CityAreaService() throws DatabaseException {
		super();
	}

	public int addBatchCityArea(String sql) {
		Statement st = null;
		try {
			st = this.getDbOp().getConn().createStatement();
			return st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}finally{
			DbUtil.closeStatement(st);
		}
	}

	public int addBatchProvinceCity(String sql) {
		Statement st = null;
		try {
			st = this.getDbOp().getConn().createStatement();
			return st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}finally{
			DbUtil.closeStatement(st);
		}
	}
	
	public int addBatchProvinces(String sql) { //添加省份
		Statement st = null;
		try {
			st = this.getDbOp().getConn().createStatement();
			return st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}finally{
			DbUtil.closeStatement(st);
		}
	}
	
	public int addBatchAreaStreet(String sql) {
		Statement st = null;
		try {
			st = this.getDbOp().getConn().createStatement();
			return st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}finally{
			DbUtil.closeStatement(st);
		}
	}

	public List getAreaStreetList(String city) {
		Statement st = null;
		ResultSet rs = null;
		StringBuffer sql = new StringBuffer();
		sql.append("select a.id,a.street from province_city p,city_area c,area_street a where ");
		sql.append(" p.id=c.city_id and c.id=a.area_id ");
		if(city!=null)
			sql.append("and "+city);
		//System.out.println(sql+" **********");
		try {
			st = this.getDbOp().getConn().createStatement();
			rs = st.executeQuery(sql.toString());
			List streetList=new ArrayList();
			while(rs.next()){
				AreaStreetBean street = new AreaStreetBean();
				street.setId(rs.getInt("id"));
				street.setStreet(rs.getString("street"));
				streetList.add(street);
			}
			return streetList;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}finally{
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(st);
		}
	}

	
	public List getProvinceList(){
    	List list =null;
    	StringBuffer sql = new StringBuffer("select * from provinces");
    	Statement stmt = null;
		ResultSet rs = null;
    	try {
    		 
    		stmt = this.getDbOp().getConn().createStatement();
    		rs=stmt.executeQuery(sql.toString());
			list= new ArrayList();
			while(rs.next()){
				AreaStreetBean t = new AreaStreetBean();
				t.setId(rs.getInt("id"));
				t.setStreet(rs.getString("name"));
				list.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace(); // 
		}finally{
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(stmt);
		}
    	
    	return list;
    } 
    
    
    public List getCityList(AreaStreetBean city){
    	List list =null;
    	PreparedStatement pstmt =null;
    	ResultSet rs = null;
    	StringBuffer sql = new StringBuffer("select * from province_city where province_id=?");
    	try {
    		 
    		pstmt = this.getDbOp().getConn().prepareStatement(sql.toString());
    		pstmt.setInt(1, city.getAreaId());
    		rs=pstmt.executeQuery();
			list= new ArrayList();
			while(rs.next()){
				AreaStreetBean t = new AreaStreetBean();
				t.setId(rs.getInt("id"));
				t.setStreet(rs.getString("city"));
				list.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace(); // 
		}finally{
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(pstmt);
		}
		
    	
    	return list;
    } 
    
    public List getAreaList(AreaStreetBean city){
    	PreparedStatement pstmt =null;
    	ResultSet rs = null;
    	List list =null;
    	StringBuffer sql = new StringBuffer("select * from city_area where city_id=?");
    	try {
    		 
    		pstmt = this.getDbOp().getConn().prepareStatement(sql.toString());
    		pstmt.setInt(1, city.getAreaId());
    		rs=pstmt.executeQuery();
			list= new ArrayList();
			while(rs.next()){
				AreaStreetBean t = new AreaStreetBean();
				t.setId(rs.getInt("id"));
				t.setStreet(rs.getString("area"));
				list.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace(); // 
		}finally{
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(pstmt);
		}
		
    	
    	return list;
    }
    
    public List getStreetList(AreaStreetBean city){
    	PreparedStatement pstmt =null;
    	ResultSet rs = null;
    	List list =null;
    	StringBuffer sql = new StringBuffer("select * from area_street where area_id=?");
    	try {
    		 
    		pstmt = this.getDbOp().getConn().prepareStatement(sql.toString());
    		pstmt.setInt(1, city.getAreaId());
    		rs=pstmt.executeQuery();
			list= new ArrayList();
			while(rs.next()){
				AreaStreetBean t = new AreaStreetBean();
				t.setId(rs.getInt("id"));
				t.setStreet(rs.getString("street"));
				list.add(t);
			}
		} catch (SQLException e) {
			e.printStackTrace(); // 
		}finally{
			DbUtil.closeResultSet(rs);
			DbUtil.closeStatement(pstmt);
		}
		
    	return list;
    }
	
}

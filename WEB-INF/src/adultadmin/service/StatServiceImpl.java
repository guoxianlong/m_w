/**
 * 
 */
package adultadmin.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import adultadmin.framework.exceptions.DatabaseException;
import adultadmin.util.db.DbOperation;
import adultadmin.util.db.DbUtil;

/**
 * @author Bomb
 * 
 */
public class StatServiceImpl implements IStatService {

	private Connection conn = null;
	
	static private String NEARLY_SAME = " between -0.0001 and 0.0001";

	public int executeUpdate(String sql) {
		PreparedStatement pst = null;
		try {
			pst = conn
					.prepareStatement(sql);
			
			return pst.executeUpdate();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(pst);
		}
		return -1;
	}
	/**
	 * 
	 * @throws DatabaseException
	 */
	public StatServiceImpl() throws DatabaseException {
		super();
		init();
	}

	private void init() throws DatabaseException {

		conn = DbUtil.getConnection(DbOperation.DB);

	}

	private void closeStatement(Statement st) {
		DbUtil.closeStatement(st);
	}

	private void closeResultSet(ResultSet rs) {
		DbUtil.closeResultSet(rs);
	}

	public void close() {
		DbUtil.closeConnection(conn);
	}
    public int getNumber(int id) {
        int number = 0;
       	Statement st = null;

        try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery("select number from number where id=" + id);
			if(rs.next())
				number = rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
        close();
		return number;
    }
    
    public void setNumber(int id, int number) {
       	Statement st = null;
        try {
			st = conn.createStatement();
			st.executeUpdate("update number set number=" + number + " where id=" + id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		close();
    }
    public int getLastInsertId(String table) {
    	Statement st = null;
        int id = 0;
        try {
			st = conn.createStatement();
			ResultSet rs = st.executeQuery("select last_insert_id()");
			if(rs.next())
		        id = rs.getInt(1);
		} catch (Exception e) {
			e.printStackTrace();
		}
        
		return id;
    }

	public int getInt(String sql) {
		Statement st = null;
		ResultSet rs = null;
		try {
			st = conn.createStatement();

			rs = st.executeQuery(sql);
			if (rs.next()) {
				return rs.getInt(1);
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		} finally {
			closeStatement(st);
			closeResultSet(rs);
		}
		return 0;
	}

	
}

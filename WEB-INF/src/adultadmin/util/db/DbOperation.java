/*
 * Created on 2005-5-21
 *
 */
package adultadmin.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import adultadmin.util.StringUtil;

/**
 * @author a
 *  
 */
public class DbOperation {
	public static String DB_SLAVE = "adult_slave";
	public static String DB_SLAVE2 = "adult_slave2";
	public static String DB_SMS = "sms";
	public static String DB_FORUM = "adult_forum";
	public static String DB = "adult";
	public static String DB_WARE = "adult";
	public static String DB_FRONT = "db_front";
	public static String WARE = "adult";
	public static String WARE_SLAVE = "adult_slave";
	public static String WARE_SLAVE2 = "adult_slave2";
	
	public static String STAT = "stat";
	public static String STAT_SLAVE = "stat_slave";
	
	public static String SCHEMA_SHOP = "shop.";
	public static String SCHEMA_SMS = "sms.";
	public static String SCHEMA_FORUM = "forum.";
	public static String SCHEMA_WARE = "shop.";
	
	public Log stockLog = LogFactory.getLog("stock.Log");
	
	private Connection conn;

	private Statement stmt;

	private PreparedStatement pstmt;

	private ResultSet rs;
	
	///是否调用过相关的初始化操作，获取了数据库链接，如果是，则不会允许再进行第二次。
	///从而保证一个dboperation对象只能按照指定的方式init，而不会在第一次init失败后，还会调用主库的链接
	boolean isInit;

	public DbOperation() {
		
	}
	public DbOperation(boolean startInit) {
		if(startInit)
			init();
	}
	public DbOperation(String db) {
		init(db);
	}
	public int getLastInsertId() {
		int id = 0;
		ResultSet rs = executeQuery("select last_insert_id()");
		try {
			if (rs.next())
				id = rs.getInt(1);
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
		}

		return id;
	}
	/**
	 * 鍒濆鍖栥�
	 * 
	 * @return
	 */
	public boolean init() {
		if(isInit)	return this.conn!=null;	
		isInit = true;
		
		if (conn == null) {
			conn = DbUtil.getConnection();
		} else {
			return true;
		}
		if (conn != null) {
			return true;
		}
		return false;		
	}

	public boolean init(Connection conn) {
		if(isInit)	return this.conn!=null;		
		isInit = true;
		
		if(conn != null){
			this.conn = conn;
			return true;
		} else {
			return false;
		}
	}

	public boolean init(String databaseName) {
		if(isInit)	return this.conn!=null;		
		isInit = true;
		
		if (conn == null) {
			conn = DbUtil.getConnection(databaseName);
		}
		if (conn != null) {
			return true;
		}
		return false;
	}

	public boolean init(String dbServer, String db, String dbUser, String dbPassword){
		if(isInit)	return this.conn!=null;
		isInit = true;
		
		if (conn == null) {
			conn = DbUtil.getDirectConnection(dbServer, db, dbUser, dbPassword);
		}
		if (conn != null) {
			return true;
		}
		return false;
	}
	
	/**
	 * 寮�涓�浜嬪姟銆�
	 * 
	 * @return
	 */
	public boolean startTransaction() {
		if (!init()) {
			return false;
		}
		try {
			conn.setAutoCommit(false);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
			return false;
		}
	}

	/**
	 * 浜嬪姟瀹屾垚銆�
	 * 
	 * @return
	 */
	public boolean commitTransaction() {
		if (!init()) {
			return false;
		}
		try {
			if(!conn.getAutoCommit()){
				conn.commit();
				conn.setAutoCommit(true);
			}
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
			return false;
		}
	}
	
    /**
     * 浣滆�锛氭潕鍖楅噾
     * 
     * 鍒涘缓鏃ユ湡锛�007-1-24
     * 
     * 璇存槑锛氬洖婊氫簨鍔�
     * 
     * 鍙傛暟鍙婅繑鍥炲�璇存槑锛�
     * 
     * @return
     */
    public boolean rollbackTransaction() {
    	if(!init()){
    		return false;
    	}
        try {
            conn.rollback();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            stockLog.error(StringUtil.getExceptionInfo(e));
            return false;
        }
    }

	/**
	 * 鍒濆鍖朣tatement銆�
	 * 
	 * @return
	 */
	public boolean initStatement() {
		if (!init()) {
			return false;
		}
		if (stmt != null) {
			return true;
		}
		try {
			stmt = conn.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
			return false;
		}
		return true;
	}

	/**
	 * 鎵ц鏌ヨ锛岃繑鍥炵粨鏋滈泦銆�
	 * 
	 * @param query
	 * @return
	 */
	public ResultSet executeQuery(String query) {
		if (!initStatement()) {
			return null;
		}
		try {
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
		}
		return rs;
	}

	/**
	 * 鎵ц鏇存柊锛岃繑鍥炵粨鏋溿�
	 * 
	 * @param query
	 * @return
	 */
	public boolean executeUpdate(String query) {
		if (!initStatement()) {
			return false;
		}
		//System.out.println(query);
		try {
			int rows = stmt.executeUpdate(query);
			if(rows == 0){
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
			return false;
		}
		return true;
	}

	/**
	 * 鍑嗗PreparedStatement銆�
	 * 
	 * @param query
	 * @return
	 */
	public boolean prepareStatement(String query) {
		if (!this.init()) {
			return false;
		}
		try {
			pstmt = conn.prepareStatement(query);
		} catch (SQLException e) {
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
			return false;
		}
		return true;
	}

	public PreparedStatement getPStmt() {
		return pstmt;
	}

	public boolean setFetchSize(int size) {
		if (!initStatement()) {
			return false;
		}
		try {
			stmt.setFetchSize(size);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
			return false;
		}
	}

	public boolean executePstmt() {
		if (pstmt != null) {
			try {
				pstmt.execute();
			} catch (SQLException e) {
				e.printStackTrace();
				stockLog.error(StringUtil.getExceptionInfo(e));
				return false;
			}
		}
		return true;
	}

	/**
	 * 閲婃斁璧勬簮銆�
	 */
	public void release() {
		this.isInit = false;  ///一旦调用release，则认为没有初始化。		避免出现复用dboperation不能再次获取连接的问题。
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
			if (conn != null) {
				if(!conn.getAutoCommit()){
					conn.rollback();
					conn.setAutoCommit(true);
				}
				conn.close();
				conn = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
		}	
	}

	/**
	 * 閲婃斁璧勬簮锛屼絾涓嶉噴鏀炬暟鎹簱杩炴帴銆�
	 */
	public void releaseWithoutConn() {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
			if (pstmt != null) {
				pstmt.close();
				pstmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			stockLog.error(StringUtil.getExceptionInfo(e));
		}
	}

	public int getSeqCurrVal(String seqName) {
		int result = 0;
		try {
			String query = "SELECT " + seqName + ".currval as c FROM dual";
			rs = executeQuery(query);
			if (rs.next()) {
				result = rs.getInt("c");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		releaseWithoutConn();
		return result;
	}

	public int getSeqNextVal(String seqName) {
		int result = 0;
		try {
			String query = "SELECT " + seqName + ".nextval as c FROM dual";
			rs = executeQuery(query);
			if (rs.next()) {
				result = rs.getInt("c");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		releaseWithoutConn();
		return result;
	}

	public static String getPagingQuery(String query, int index, int count) {
		if (query == null) {
			return null;
		}

		if (index < 0) {
			index = 0;
		}

		String result = null;

		if (count == -1) {
			count = 99999999;
			result = query + " limit " + index + ", " + count;
		} else {
			result = query + " limit " + index + ", " + count;
		}
		
		return result;
	}
	
	public static String modifySql(boolean add, String set, int id) {
		String query;
		if(add) {
			query = "insert into " + set;
			if(id > 0)
				query  += ",id=" + id;
		} else
			query = "update " + set + " where id=" + id;
		return query;
	}
	public Connection getConn() {
		return conn;
	}

    public int getInt(String sql) {
        try {
            rs = executeQuery(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

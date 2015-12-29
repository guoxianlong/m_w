/*
 * Created on 2005-5-21
 *
 */
package adultadmin.util.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * @author a
 *  
 */
public class DbUtil {
    public static Context initContext;

    public static Context ctx;

    public static DataSource ds;	// 废弃不使用的

    public static boolean hasInited = false;
    
    public static String DBServer = "192.168.0.37";

    public static String DB = "shop";

    public static String DBUser = "test";

    public static String DBPassword = "jishubutest1234509876";


    //统计服务器的数据库连接
    public static String Stat_DBServer = "211.157.107.142";
    public static String Stat_DB = "shop";
    public static String Stat_DBUser = "remoteroot";
    public static String Stat_DBPassword = "yytuncn-pl,";

    public static void init() {
        try {
            initContext = new InitialContext();
            ctx = (Context) initContext.lookup("java:comp/env");
//            ds = (DataSource) ctx.lookup("jdbc/sjwx");
            hasInited = true;
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取得数据库连接。
     * 
     * @return
     */
    public static Connection getConnection() {
        try {
            initContext = new InitialContext();
            ctx = (Context) initContext.lookup("java:comp/env");
            DataSource ds = (DataSource) ctx.lookup("jdbc/adult");
            Connection conn = ds.getConnection();
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getConnection(String databaseName) {
        try {
            initContext = new InitialContext();
            ctx = (Context) initContext.lookup("java:comp/env");
            DataSource ds = (DataSource) ctx.lookup("jdbc/" + databaseName);
            Connection conn = ds.getConnection();
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static Connection getDirectConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection("jdbc:mysql://" + DBServer
                    + ":3306/" + DB, DBUser, DBPassword);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Connection getDirectConnection(String dbServer, String db, String dbUser, String dbPassword){
    	try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection("jdbc:mysql://" + dbServer
                    + ":3306/" + db, dbUser, dbPassword);
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    

	public static void closeStatement(Statement st) {
		try {

			if (st != null) {
				st.close();
				st = null;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	public static void closeResultSet(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}

		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	public static void closeConnection(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}
	
	public static void dropConnection(Connection conn, Statement st, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
		} catch (SQLException se) {
			se.printStackTrace();;
		}
		try {
			if (st != null) {
				st.close();
				st = null;
			}
		} catch (SQLException se) {
			se.printStackTrace();;
		}
		try {
			if (conn != null) {
				conn.close();
				conn = null;
			}
		} catch (SQLException se) {
			se.printStackTrace();;
		}
	}
}

package adultadmin.util.db;

import java.sql.*;


public class MSSQLUtil {
	private static MSSQLUtil instance = new MSSQLUtil() ;
	private Connection con;
	private Statement  stat;
	private ResultSet  rs;
	
	public static MSSQLUtil getInstance(){
		return instance ;
	}
	private MSSQLUtil(){
		String driver = "com.microsoft.jdbc.sqlserver.SQLServerDriver";
		String url = "jdbc:microsoft:sqlserver://192.168.0.10:1433;DatabaseName=printdata";
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url,"sa","print1234");
			stat = con.createStatement();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean update(String sql){
		boolean status = false;
		try {
			stat.executeUpdate(sql);
			status=true;
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			this.release();
		}
		return status;
	}
	
	public void release(){
		try {
			rs.close();
			stat.close();
			con.close();
		} catch (Exception e) {
			
		}
	}
	
}

package adultadmin.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;


public class MyUtil {


	public static Connection getConnection(String jndi) {
		try {
			Connection conn = null;
			Context envContext = new InitialContext();
			DataSource ds = (DataSource) envContext.lookup(jndi);
			conn = ds.getConnection();
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Connection getDirectConnection(){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			return DriverManager
					.getConnection(
							"jdbc:mysql://localhost:3306/shop?autoReconnect=true&useUnicode=true",
							"root", "123456");
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

	public static Connection getConnection() {
		try {
			Connection conn = null;
			Context envContext = (Context) new InitialContext()
					.lookup("java:comp/env");
			DataSource ds = (DataSource) envContext.lookup("jdbc/webadmin");
//			Context ct = new InitialContext();
//			DataSource ds = (DataSource)ct.lookup("jdbc/webadmin");
			conn = ds.getConnection();
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
	
	public static int getInt(Connection conn, String sql) throws SQLException {
		Statement st = conn.createStatement();
	    try {
			ResultSet rs = st.executeQuery(sql);
			if(rs.next())
				return rs.getInt(1);
			rs.close();
		} catch (Exception e) {e.printStackTrace();}
		return 0;
	}
	
	public static void executeUpdate(Connection conn, String sql) throws SQLException {
		Statement st = conn.createStatement();
	    try {
			st.executeUpdate(sql);
		} catch (Exception e) {e.printStackTrace();}
	}
	
	public static void completePhone(String date){
		String curDate = date;
		if(curDate == null)
			curDate = DateUtil.formatDate(new Date());

		Connection conn = getDirectConnection();
		File phoneFile = new File("c:\\number.txt");
		String phone = null;
		String area = null;
		int areano = 0;
		int cityno = 0;
		long phoneCount = 0;
		long index = 0;
		try{
			BufferedReader in = new BufferedReader(new FileReader(phoneFile));
		   	PreparedStatement psQuery = conn.prepareStatement("select * from phonemap where prefixno = ?");
		   	PreparedStatement psCityQuery = conn.prepareStatement("select distinct placeno, cityno from phonemap where cityname = ?");
		   	PreparedStatement psInsert = conn.prepareStatement("insert into phonemap(prefixno, placeno, phonetype, cityname, cityno, bupdated) values (?, ?, ?, ?, ?, ?)");
			ResultSet rs = null;
			String line = in.readLine();
			while(line != null){
				index++;
				if(index%100 == 0){
					System.out.println();
					System.out.print(index / 100);
				}
				System.out.print(".");
				String [] lines = line.split(",");
				phone = lines[0];
				area = lines[1].replaceAll("市", "");
				//查找是否存在该手机号
				psQuery.setString(1, phone);
				rs = psQuery.executeQuery();
				if(rs.next()){
				} else {
					System.out.println("got one ... ...");
					phoneCount++;
					try{
						rs.close();
					}catch(Exception e){
					}
					psCityQuery.setString(1, area);
					rs = psCityQuery.executeQuery();
					if(rs.next()){
						areano = rs.getInt("placeno");
						cityno = rs.getInt("cityno");
					} else {
						System.out.println("area : " + area + " not found");
						areano = 0;
					}
					try{
						psInsert.setString(1, phone);
						psInsert.setInt(2, areano);
						psInsert.setInt(3, 0);
						psInsert.setString(4, area);
						psInsert.setInt(5, cityno);
						psInsert.setInt(6, 0);
						psInsert.executeUpdate();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				try{
					rs.close();
				}catch(Exception e){
				}
				try{
					Thread.sleep(30);
				}catch(Exception e){
				}
				line = in.readLine();
			}
			System.out.println("Total : " + phoneCount);
			if(psQuery != null){
				try{
					psQuery.close();
				} catch(Exception e){
					e.printStackTrace();
				}finally{
					psQuery = null;
				}
			}
			if(psInsert != null){
				try{
					psInsert.close();
				} catch(Exception e){
					e.printStackTrace();
				}finally{
					psInsert = null;
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		} finally {
			if(conn != null){
				try{
					conn.close();
				} catch(Exception e){
					e.printStackTrace();
				} finally {
					conn = null;
				}
			}
		}
	}
	
	public static void main(String [] args){
		//MyUtil.completePhone(null);
		String temp = "臭猪\"1号\"臭猪";
		System.out.println(temp);
		temp = temp.replaceAll("\"","#34;");
		System.out.println(temp);
	}
}

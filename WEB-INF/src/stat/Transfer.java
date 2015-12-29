/*
 * Created on 2008-7-5
 *
 */
package stat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

/**
 * 作者：李北金
 * 
 * 创建日期：2008-7-5
 * 
 * 说明：
 */
public class Transfer {

    public static void main(String[] args) {
        transferCpaData();
    }

    public static void transferUserStatus() {
        System.out.println("transferUserStatus: "
                + Calendar.getInstance().getTime().toString());
        try {
            Connection serverConn = getServerConnection();
            Connection dataServerConn = getDataServerConnection();

            Statement serverStat = serverConn.createStatement();
            Statement dataServerStat = dataServerConn.createStatement();

            String dsSql = "select max(id) from user_status";
            ResultSet dsRs = dataServerStat.executeQuery(dsSql);
            dsRs.next();
            int maxId = dsRs.getInt(1);
            dsRs.close();

            String sSql = "select * from user_status where id > " + maxId
                    + " order by id";
            ResultSet sRs = serverStat.executeQuery(sSql);
            StringBuffer sb = new StringBuffer();
            int index = 0;
            int totalIndex = 0;
            while (sRs.next()) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append("(" + sRs.getInt("id") + ", "
                        + sRs.getInt("login_count") + ", '"
                        + sRs.getString("last_login_time") + "', '"
                        + sRs.getString("last_logout_time") + "', "
                        + sRs.getInt("total_online_time") + ", '"
                        + sRs.getString("phone") + "','"
                        + sRs.getString("create_datetime") + "', '"
                        + sRs.getString("ua") + "', " + sRs.getInt("first_fr")
                        + ", '" + sRs.getString("real_mobile") + "')");
                index++;
                totalIndex ++;
                if (index == 500) {
                    //System.out.println(sb.toString());
                    System.out.println("transferUserStatus: " + totalIndex);
                    index = 0;
                    dsSql = "insert into user_status VALUES " + sb.toString();
                    dataServerStat.execute(dsSql);
                    sb = new StringBuffer();                    
                }
            }

            if (sb.length() > 0) {
                dsSql = "insert into user_status VALUES " + sb.toString();
                dataServerStat.execute(dsSql);
            }
            System.out.println("transferUserStatus: " + totalIndex);

            sRs.close();

            serverStat.close();
            dataServerStat.close();

            serverConn.close();
            dataServerConn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void transferUser() {
        System.out.println("transferUser: "
                + Calendar.getInstance().getTime().toString());
        try {
            Connection serverConn = getServerConnection();
            Connection dataServerConn = getDataServerConnection();

            Statement serverStat = serverConn.createStatement();
            Statement dataServerStat = dataServerConn.createStatement();

            String dsSql = "select max(id) from user";
            ResultSet dsRs = dataServerStat.executeQuery(dsSql);
            dsRs.next();
            int maxId = dsRs.getInt(1);
            dsRs.close();

            String sSql = "select * from user where id > " + maxId
                    + " order by id";
            ResultSet sRs = serverStat.executeQuery(sSql);
            StringBuffer sb = new StringBuffer();
            int index = 0;
            int totalIndex = 0;
            while (sRs.next()) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append("(" + sRs.getInt("id") + ", '"
                        + sRs.getString("username") + "', '"
                        + sRs.getString("password") + "', '"
                        + sRs.getString("create_datetime") + "', "
                        + sRs.getInt("flag") + ", '" + sRs.getString("name")
                        + "','" + sRs.getString("phone") + "', '"
                        + sRs.getString("address") + "','"
                        + sRs.getString("postcode") + "', '" + sRs.getString("cp")
                        + "', '" + sRs.getString("ua") + "', '"
                        + sRs.getString("nick") + "', " + sRs.getInt("vip")
                        + ", '" + sRs.getString("vip_phone") + "', "
                        + sRs.getInt("agent") + ", " + sRs.getFloat("discount")
                        + ", " + sRs.getInt("order_reimburse") + ", "
                        + sRs.getInt("reimburse") + ")");
                index++;
                totalIndex ++;
                if (index == 500) {
                    //System.out.println(sb.toString());
                    System.out.println("transferUser: " + totalIndex);
                    index = 0;
                    dsSql = "insert into user VALUES " + sb.toString();
                    dataServerStat.execute(dsSql);
                    sb = new StringBuffer();                    
                }
            }

            if (sb.length() > 0) {
                dsSql = "insert into user VALUES " + sb.toString();
                dataServerStat.execute(dsSql);
            }
            System.out.println("transferUser: " + totalIndex);

            sRs.close();

            serverStat.close();
            dataServerStat.close();

            serverConn.close();
            dataServerConn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 作者：李北金
     * 
     * 创建日期：2008-12-9
     * 
     * 说明：导入CPA数据
     * 
     * 参数及返回值说明：
     * 
     * 
     */
    public static void transferCpaData(){
        System.out.println("transferCpaData: "
                + Calendar.getInstance().getTime().toString());
        try {
            Connection serverConn = getServerConnection();
            Connection dataServerConn = getDataServerConnection();

            Statement serverStat = serverConn.createStatement();
            Statement dataServerStat = dataServerConn.createStatement();

            String dsSql = "select max(id) from adv_cpa";
            ResultSet dsRs = dataServerStat.executeQuery(dsSql);
            dsRs.next();
            int maxId = dsRs.getInt(1);
            dsRs.close();

            String sSql = "select * from adv_cpa where id > " + maxId
                    + " order by id";
            ResultSet sRs = serverStat.executeQuery(sSql);
            StringBuffer sb = new StringBuffer();
            int index = 0;
            int totalIndex = 0;
            while (sRs.next()) {
                if (sb.length() > 0) {
                    sb.append(",");
                }
                sb.append("(" + sRs.getInt("id") + ", '"
                        + sRs.getString("phone") + "', '"
                        + sRs.getString("log_date") + "', "
                        + sRs.getString("fr") + ", '"
                        + sRs.getString("create_datetime") + "', " + sRs.getString("user_id")
                        + "," + sRs.getString("product_id") + ")");
                index++;
                totalIndex ++;
                if (index == 500) {
                    //System.out.println(sb.toString());
                    System.out.println("transferCpaData: " + totalIndex);
                    index = 0;
                    dsSql = "insert into adv_cpa VALUES " + sb.toString();
                    dataServerStat.execute(dsSql);
                    sb = new StringBuffer();                    
                }
            }

            if (sb.length() > 0) {
                dsSql = "insert into adv_cpa VALUES " + sb.toString();
                dataServerStat.execute(dsSql);
            }
            System.out.println("transferCpaData: " + totalIndex);

            sRs.close();

            serverStat.close();
            dataServerStat.close();

            serverConn.close();
            dataServerConn.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Connection getServerConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection(
                    "jdbc:mysql://211.157.107.135:3306/shop", "remoteroot",
                    "rt55betterdb");
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

    public static Connection getDataServerConnection() {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            return DriverManager.getConnection(
                    "jdbc:mysql://211.157.107.142:3306/shop", "remoteroot",
                    "yytuncn-pl,");
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
}

package adultadmin.util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import adultadmin.util.db.DbOperation;

/**
 * @author zhaolin
 * 
 * 说明：访问控制
 *
 */
public class UserControlUtil {

	public static List allowedIp;
	public static List allowedHessianIp;

	public static boolean initAllowedIp(){
		boolean result = false;

		allowedIp = new ArrayList();

		DbOperation oper = new DbOperation();
		oper.init("adult_slave2");
		ResultSet rs = null;
		try{
			rs = oper.executeQuery("select ip from ip_group where `group`='allow'");
			while(rs.next()){
				String s = (String)rs.getString("ip");
				IP ip = new IP(s);
				allowedIp.add(ip);
			}
			result = true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			oper.release();
		}

		return result;
	}

	public static boolean initAllowedHessianIp(){
		boolean result = false;

		allowedHessianIp = new ArrayList();

		String allowedHessianIps = Constants.config.getProperty("allowed_hessian_ip");
		if(allowedHessianIps != null && !allowedHessianIps.equals("")){
			String[] ips = allowedHessianIps.split(";");
			for(String ipStr:ips){
				IP ip = new IP(ipStr);
				allowedHessianIp.add(ip);
			}
			result = true;
		}
		return result;
	}

	public static boolean isAllowedIp(String ip) {
		long ipl = IP.ipToLong(ip);
		for(int i = 0;i < allowedIp.size();i++) {
			IP m = (IP)allowedIp.get(i);
			if(m.isInScope(ipl))
				return true;
		}
		return false;
	}

	public static boolean isAllowedHessianIp(String ip) {
		long ipl = IP.ipToLong(ip);
		for(int i = 0;i < allowedHessianIp.size();i++) {
			IP m = (IP)allowedHessianIp.get(i);
			if(m.isInScope(ipl))
				return true;
		}
		return false;
	}
}

/**
 * 
 */
package mmb.user.secure;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.CRC32;

import javax.servlet.http.HttpServletRequest;

import adultadmin.servlet.InitServlet;
import adultadmin.util.IP;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * @author Administrator
 *
 */
public class LoginUtil {
    //public static Logger log = Logger.getLogger("security.Log");
    public static final long START_TIME = System.currentTimeMillis(); ///记录一个启动时间
    
    public static final int USER_NAME_ERROR_FORBIDDEN_TIME = 10*60*1000; ///等待时间
    public static final int IP_ERROR_FORBIDDEN_TIME = 1*60*1000;                  ///等待时间
    
    public static final int MIN_NO_ERRRO_TIME = 10*1000;   //如果两次的错误间隔大于这个时间，则清除之前的错误次数
     
    public static final int USER_NAME_ERROR__TIMES = 10;  ///失败次数
    public static final int IP_ERROR_TIMES = 30;                   ///失败次数
    
    public static List ipWhitelist = new ArrayList();
    public static List ipBlacklist = new ArrayList();
    static{
    	//loadIpWhitelist();
    	//loadIpBlacklist();
    }
    
    public static final int MAX_SIZE = 1<<17; //一共128K初始空间
    public static HashMap errorLoginPool = new HashMap(MAX_SIZE,1.0f); 
    
    /**
     * 检查是否允许一个用户的登陆（用户的账号以及用户的IP)
     * 返回0表示允许登陆，其他各自有代表的意思
     * @param userName
     * @param request
     * @return
     */
    public static int loginCheck(String userName,HttpServletRequest request){
    	Object userKey = getKey(userName);
    	LoginErrorItem userItem = (LoginErrorItem)errorLoginPool.get(userKey);
    	    	
    	int now = (int)(System.currentTimeMillis()-START_TIME);
    	
    	//同一个账号登陆失败次数超过限制
    	boolean userNameAllowed = isUserNameAllowed(now,userItem);
    	if(!userNameAllowed){
    		return 1;
    	}
    	
    	///IP属于黑名单，所有的登陆都不允许
    	boolean ipInBlacklist = ipInBlacklist(adultadmin.util.IP.getRemoteAddr(request));
    	if(ipInBlacklist){
    		return 2;
    	}
    	
    	Object ipKey = getKey(adultadmin.util.IP.getRemoteAddr(request));
    	LoginErrorItem ipItem = (LoginErrorItem)errorLoginPool.get(ipKey);
    	
    	///IP登陆错误次数太多
    	if(!isIpAllowed(now,ipItem)){
    		return 3;
    	}
    	
    	return 0;
    }
    
    /**
     * 检查是否允许用户的账号的登陆
     * @param userName
     * @param request
     * @return
     */
    public static boolean isUserNameAllowed(String userName,HttpServletRequest request){
    	Object userKey = getKey(userName);
    	LoginErrorItem userItem = (LoginErrorItem)errorLoginPool.get(userKey);
    	
    	int now = (int)(System.currentTimeMillis()-START_TIME);
    	
    	return isUserNameAllowed(now,userItem);
    }
    
    private  static boolean isUserNameAllowed(int now,LoginErrorItem userItem){
    	if(userItem==null) return true;
    	if(userItem.getErrorTimes()<USER_NAME_ERROR__TIMES){ 
			return true;
		}else{
			///大于等于5次，则判断时间 
			if(now - userItem.getLastErrorTime()>USER_NAME_ERROR_FORBIDDEN_TIME){
				userItem.setErrorTimes((byte)0); ///重新计算失败次数
				//userItem.setLastErrorTime(now);
				return true;
			}else{
				///时间内，不允许
				return false;
			}
		}
    }
    
    private  static boolean isIpAllowed(int now,LoginErrorItem ipItem){
    	if(ipItem==null) return true;
  		if(ipItem.getErrorTimes()<IP_ERROR_TIMES){ 
			return true;
		}else{
			///大于等于20次，则判断时间 
			if(now - ipItem.getLastErrorTime()>IP_ERROR_FORBIDDEN_TIME){
				ipItem.setErrorTimes((byte)0); ///重新计算失败次数
				//userItem.setLastErrorTime(now);
				return true;
			}else{
				///时间内，不允许
				return false;
			}
		}
    }
    
    /**
     * 记录一次登录失败
     * @param userName
     * @param password
     * @param request
     */
    public static void recordError(String userName,HttpServletRequest request){    	    	
    	int now = (int)(System.currentTimeMillis()-START_TIME);

    	Object userKey = getKey(userName);
    	LoginErrorItem userItem = (LoginErrorItem)errorLoginPool.get(userKey);

    	logUserNameError(now,userKey,userItem);
    	
    	String ip = adultadmin.util.IP.getRemoteAddr(request);
    	boolean ipInWhiteList = ipInWhitelist(ip);
    	if(ipInWhiteList){
    		//不做处理
    	}else{
           	Object ipKey = getKey(ip);
        	LoginErrorItem ipItem = (LoginErrorItem)errorLoginPool.get(ipKey);	
        	logIpError(now,ipKey,ipItem);
    	}
    	
    	if(errorLoginPool.size()>=MAX_SIZE){
    		synchronized(LOCK){
    			if(errorLoginPool.size()>=MAX_SIZE){
    				clear();
    			}
    		}
    	}
    }
    
    /**
     * 记录一次登录失败
     * @param userName
     * @param password
     * @param request
     */
    public static void recordUserNameError(String userName,HttpServletRequest request){    	    	
    	int now = (int)(System.currentTimeMillis()-START_TIME);

    	Object userKey = getKey(userName);
    	LoginErrorItem userItem = (LoginErrorItem)errorLoginPool.get(userKey);

    	logUserNameError(now,userKey,userItem);
    	
    	if(errorLoginPool.size()>=MAX_SIZE){
    		synchronized(LOCK){
    			if(errorLoginPool.size()>=MAX_SIZE){
    				clear();
    			}
    		}
    	}
    }
    
    /**
     * 记录一次登录成功，则删除失败的记录
     * @param userName
     * @param password
     * @param request
     */
    public static void recordUserNameSuccess(String userName,HttpServletRequest request){    	    	
    	Object userKey = getKey(userName);
    	errorLoginPool.remove(userKey);
    }
    
    
    public static final Object LOCK = new Object();
    
    /**
     * 注意，仅仅在用户登陆失败的情况下记录，否则密码为明文
     * @param type 登陆检查的结果码，1为用户名失败过多，2为IP是黑名单，3为单IP失败过多，-1表示登陆失败
     * @param userName
     * @param password
     * @param request
     */
    public static void log(int type,String userName,String password,HttpServletRequest request){
    	StringBuffer sb = new StringBuffer();
    	//时间 ip 用户名 密码 ua reffered
    	sb.append("loginError").append("\t");
    	sb.append(type).append("\t");
    	sb.append(adultadmin.util.IP.getRemoteAddr(request)).append("\t");
    	sb.append(StringUtil.isNull(userName)?"-":userName).append("\t");
    	sb.append(StringUtil.isNull(password)?"-":password).append("\t");
    	
    	String ua = request.getHeader("User-Agent");
    	sb.append(StringUtil.isNull(ua)?"-":ua).append("\t");
    	
    	String referer = request.getHeader("Referer");
    	sb.append(StringUtil.isNull(referer)?"-":referer);    	
    	
    	//DebugLogUtil.logDebug(sb.toString());
    	
    	//System.out.println(sb.toString());
    }
    
    public static void logUserNameError(int now,Object key,LoginErrorItem userItem){
    	if(userItem==null){
    		userItem = new LoginErrorItem();
    		userItem.setErrorTimes((byte)1);
    		userItem.setLastErrorTime(now);
    		errorLoginPool.put(key, userItem);
    	}else{
    		if(now-userItem.getLastErrorTime()>MIN_NO_ERRRO_TIME){
    			userItem.setErrorTimes((byte)(1));
    			userItem.setLastErrorTime(now);
    		}else{
    			userItem.setErrorTimes((byte)(userItem.getErrorTimes()+1));
    			userItem.setLastErrorTime(now);
    		}
    	}
    }
    
    private static void logIpError(int now,Object key,LoginErrorItem ipItem){
    	if(ipItem==null){
    		ipItem = new LoginErrorItem();
    		ipItem.setErrorTimes((byte)1);
    		ipItem.setLastErrorTime(now);
    		errorLoginPool.put(key, ipItem);
    	}else{
    		if(now-ipItem.getLastErrorTime()>MIN_NO_ERRRO_TIME){
        		ipItem.setErrorTimes((byte)1);
        		ipItem.setLastErrorTime(now);
    		}else{
    			ipItem.setErrorTimes((byte)(ipItem.getErrorTimes()+1));
        		ipItem.setLastErrorTime(now);
    		}    		
    	}
    }
    
    /**
     * 符合一条规则，则认为是IP属于白名单
     * @param ip
     */
    public static boolean ipInWhitelist(String ip){
    	if(ipWhitelist!=null){
    		for(int i=0;i<ipWhitelist.size();i++){
    			IP whiteIp = (IP)ipWhitelist.get(i);
    			if(whiteIp.isInScope(ip)){
    				return true;
    			}
    		}
    	}
    	return false;
    }
   
    /**
     * 符合一条规则，则认为是IP属于黑名单
     * @param ip
     */
    public static boolean ipInBlacklist(String ip){
    	if(ipBlacklist!=null){
    		for(int i=0;i<ipBlacklist.size();i++){
    			IP blackIp = (IP)ipBlacklist.get(i);
    			if(blackIp.isInScope(ip)){
    				return true;
    			}
    		}
    	}
    	return false;
    }
       
    /**
     * 可以将一个用户名计算为crc，防止过长的用户名产生的内存占用
     * @param str
     * @return
     */
	private static final Object getKey(String str){
		CRC32 crc = new CRC32();
		crc.update(str.getBytes());
		Long key = Long.valueOf(crc.getValue());
		return key;
	}
    
	/**
	 * 加载IP白名单。每一行是一个IP对象的文本表示，如127.0.0.1/8
	 * @return
	 */
	public static boolean loadIpWhitelist(){
		List list = new ArrayList();
		
		String txt = "";//InitServlet.CONFIG_PATH+"ip_whitelist.txt";
		
		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(txt)));
			String line = null;
			while((line=br.readLine())!=null){
				if(!StringUtil.isNull(line)){
					if(line.startsWith("#")) continue;  ////#开头的作为注释行
					list.add(new IP(line));
				}
			}
			ipWhitelist = list;
			System.out.println("IP white list loaded... size of list is: "+list.size());
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				br=null;
			}
		}

		return false;
	}
	/**
	 * 加载IP黑名单。每一行是一个IP对象的文本表示，如127.0.0.1/8
	 * @return
	 */
	public static boolean loadIpBlacklist(){
		List list = new ArrayList();
		
		String txt = "";//InitServlet.CONFIG_PATH+"ip_blacklist.txt";
		
		
		BufferedReader br = null;
		try{
			br = new BufferedReader(new InputStreamReader(new FileInputStream(txt)));
			String line = null;
			while((line=br.readLine())!=null){
				if(!StringUtil.isNull(line)){
					if(line.startsWith("#")) continue;  ////#开头的作为注释行
					list.add(new IP(line));
				}
			}
			ipBlacklist = list;
			System.out.println("IP blacklist loaded... size of list is: "+list.size());
			return true;
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(br!=null){
				try {
					br.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				br=null;
			}
		}

		return false;
	}
	
	/**
	 * 简单化操作，如果上限达到，则全部清除记录。()
	 */
	public static void clear(){
		HashMap map = new HashMap(MAX_SIZE);  //一共128K初始空间
		errorLoginPool = map;
	}
	
    public static class LoginErrorItem{
    	byte errorTimes;  ///失败次数
    	int lastErrorTime; ///上一次失败的时间，取和该类第一次调用的时间差
		public byte getErrorTimes() {
			return errorTimes;
		}
		public void setErrorTimes(byte errorTimes) {
			this.errorTimes = errorTimes;
		}
		public int getLastErrorTime() {
			return lastErrorTime;
		}
		public void setLastErrorTime(int lastErrorTime) {
			this.lastErrorTime = lastErrorTime;
		}
    } 
    
	public static void log2Db(HttpServletRequest request,String username,String password,int result){
		String sql = "insert into admin_login (username,ip,host,create_time,password,result) values('"
					+StringUtil.toSql(username)+"','"+adultadmin.util.IP.getRemoteAddr(request)+"','"+request.getServerName()+"',now(),'-',"+result+")";
		DbOperation dbOp = null;
		try{
			dbOp = new DbOperation(DbOperation.DB_SMS);
			dbOp.executeUpdate(sql);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if(dbOp!=null){
				dbOp.release();
			}
		}
	}
    
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println(1<<16);
	}

}

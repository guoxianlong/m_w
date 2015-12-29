package mmb.util;

import java.io.IOException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.impl.KetamaMemcachedSessionLocator;
import net.rubyeye.xmemcached.utils.AddrUtil;
import adultadmin.util.Constants;

/**
 * 使用 xmemcached 的memcached 客户端封装
 * @author Huodongyun
 *
 */
public class CacheUtil {
	public static final int EXP_SEC = 1;   //1秒过期
	public static final int EXP_MIN = 60;   // 1分钟过期
	public static final int EXP_HOUR = 3600;
	public static final int EXP_DAY = 24*3600;
	public static final int EXP_WEEK = 7*EXP_DAY;
	public static final int EXP_MONTH = 30*EXP_DAY;
	public static final int EXP_YEAR = 365*EXP_DAY;
	public static final int EXP_NEVER = 0;                      //永远不过期
	
	public static final int TIMEOUT = 5000;                      //超时时间5s
	
	private static MemcachedClient client;
	static{
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(
				AddrUtil.getAddresses(Constants.MEMCACHED_SERVERS));
		///使用一致性hash，多个前台的hash保持一致
		builder.setSessionLocator(new KetamaMemcachedSessionLocator());
		try {
			client = builder.build();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * 往缓存中放入一个对象
	 * @param key 
	 * @param exp 过期时间，以秒为单位
	 * @param value
	 * @return
	 */
	public static boolean add(String key, int exp, Object value) {
		try{
			return client.add(key,exp, value, TIMEOUT);
		}catch(Exception e){
			return false;
		}
	}
	
	/**
	 * 往缓存中放入一个对象
	 * @param key 
	 * @param exp 过期时间，以秒为单位
	 * @param value
	 * @return
	 */
	public static boolean addOrUpdate(String key, int exp, Object value) {
		try{
			if(!client.add(key,exp, value, TIMEOUT)){
				return client.set(key,exp, value, TIMEOUT);
			}			
		}catch(Exception e){
			return false;
		}
		return false;
	}
	
	/**
	 * 获取一个对象
	 * @param key
	 * @return
	 */
	public static Object get(String key) {
		try{
			return client.get(key, TIMEOUT);
		}catch(Exception e){
			return null;
		}
	}
	/**
	 * 删除一个对象
	 * @param key
	 * @return
	 */
	public static boolean delete(String key) {
		try{
			return client.delete(key, (long)TIMEOUT);
		}catch(Exception e){
			return false;
		}
	}
	/**
	 * 更新对象的过期时间
	 * @param key
	 * @param newExp 新的过期时间
	 * @return
	 */
	public static boolean touch(String key, int newExp){
		try{
			return client.touch(key, newExp,TIMEOUT);
		}catch(Exception e){
			return false;
		}
	}
	
	public static synchronized void shutdown(){
		try {
			client.shutdown();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

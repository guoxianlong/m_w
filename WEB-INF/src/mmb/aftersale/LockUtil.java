package mmb.aftersale;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 作者：曹续
 *
 * 为每个用户非配一个锁
 *
 * 创建日期：2009-9-5
 * 
 * LockUtil.java
 *
 */
public class LockUtil {
	static HashMap groups = new HashMap();

	public static Object getLock(Object groupKey, Object key) {
		HashMap group = (HashMap) groups.get(groupKey);
		if (group == null)
			synchronized (groups) {
				group = (HashMap) groups.get(groupKey);
				if (group == null) {
					group = new HashMap();
					groups.put(groupKey, group);
				}
			}

		Object lock = group.get(key);
		if (lock == null)
			synchronized (group) {
				lock = group.get(key);
				if (lock == null) {
					lock = new byte[0];
					group.put(key, lock);
				}
			}

		return lock;
	}
	
	HashMap group = new HashMap();	// 实例lockutil之后的方式来获得锁，更为快速
	public Object getLock(Object key) {
		Object lock = group.get(key);
		if(lock == null)
			synchronized(group) {
				lock = group.get(key);
				if(lock == null) {
					lock = new byte[0];
					group.put(key, lock);
				}
			}
		
		return lock;
	}
	
	public Object getLock(int key) {
		return getLock(Integer.valueOf(key));
	}
	
	public static LockUtil userLock = new LockUtil();
	
	static HashMap rGroup = new HashMap();
	/**
	 * 作者：曹续
	 *
	 * 创建时间：2009-9-5
	 *
	 * ReentrantLock 保留方法，现在不使用。
	 *
	 * 参数及返回值说明：
	 *
	 * @param key
	 * @return
	 */
	public static ReentrantLock getRLock(Object key) {
		ReentrantLock lock = (ReentrantLock)rGroup.get(key);
		if(lock == null) {
			synchronized(rGroup) {
				lock = (ReentrantLock)rGroup.get(key);
				if(lock == null) {
					lock = new ReentrantLock();
					rGroup.put(key, lock);
				}
			}
		}
		return lock;
	}
	public static ReentrantLock getRLock(int id) {
		return getRLock(Integer.valueOf(id));
	}
}

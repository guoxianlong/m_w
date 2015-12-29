package adultadmin.framework;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import adultadmin.action.vo.voUser;

public class AccessController {
	// 对每个session的线程数进行限制
	static class SessionLock implements Serializable {
		transient int count;
		transient Lock lock = new ReentrantLock();
		transient Condition toManyConnections = lock.newCondition();
		
		// start thread
		public boolean get() {
			if(lock == null) {
				synchronized(this) {
					if(lock == null) {
						lock = new ReentrantLock();
						toManyConnections = lock.newCondition();
					}
				}
			}
			lock.lock();
			try {
				if (count < maxCount) {
					count++;
					return true;
				} else {
					boolean ret = toManyConnections.await(2000, TimeUnit.MILLISECONDS);
					if(!ret)
						return false;
					count++;
					return true;
				}
			} catch (InterruptedException e) {
				return false;
			} finally {
				lock.unlock();
			}
		}

		// end thread, release
		public boolean put() {
			lock.lock();

			if(count > 0)
				count--;

			toManyConnections.signal();
			lock.unlock();

			return true;
		}
	}

	// 检查时间间隔：20秒
	public static long checkTime = 20 * 1000;

	// 访问次数上限：100次
	public static long allowAccessCount = 100;

	// 封禁时间：30秒
	public static long forbiddenTime = 1000 * 30;

	private static boolean canAccess = false;

	private static Log acLog = LogFactory.getLog("accessController.Log");

	public static void setCanAccess(boolean flag){
		canAccess = flag;
	}

	public static boolean isCanAccess() {
		return canAccess;
	}

	public static void log(String log){
		acLog.info(log);
	}

	public static boolean isNormalAccess(HttpServletRequest request) {
		voUser user = (voUser) request.getSession().getAttribute("userView");
		if(user == null){
			return true;
		}
		long now = System.currentTimeMillis();
		if(user.getNextAccessTime() != 0){
			if(now < user.getNextAccessTime()){
				return false;
			} else {
				user.setNextAccessTime(0);
			}
		}
		if(user.getLastCheckTime() <= 0){
			user.setLastCheckTime(now);
			user.addAccessCount();
			return true;
		} else if((now - user.getLastCheckTime()) >= checkTime){
			user.setLastCheckTime(now);
			user.setAccessCount(1);
			return true;
		} else if(user.getAccessCount() > allowAccessCount){
			user.setAccessCount(0);
			user.setLastCheckTime(0);
			user.setNextAccessTime(now + forbiddenTime);
			StringBuilder buf = new StringBuilder(128);
			buf.append(user.getUsername()).append(" - ").append(request.getRequestURI());
			log(buf.toString());
			return false;
		} else {
			user.addAccessCount();
			return true;
		}
	}



	static int maxCount = 2;

	/**
	 * @param request
	 * @param url
	 * @return
	 */
	public static boolean isLimitTimeAccessStart(HttpServletRequest request, String url) {
		HttpSession session = request.getSession();
		SessionLock sl = (SessionLock) session.getAttribute(url);
		if (sl == null) {
			synchronized (session) {
				if (sl == null) {
					sl = new SessionLock();
					request.getSession().setAttribute(url, sl);
				}
			}
		}
		return sl.get();
	}

	public static boolean isLimitTimeAccessEnd(HttpServletRequest request, String url) {
		SessionLock sl = (SessionLock) request.getSession().getAttribute(url);
		if (sl == null)
			return false;
		return sl.put();
	}
}

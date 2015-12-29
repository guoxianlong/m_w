/*
 * Created on 2008-12-29
 *
 */
package adultadmin.util;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 作者：李北金
 * 
 * 创建日期：2008-12-29
 * 
 * 说明：
 */
public class DbLock2 {
    public static ReentrantLock bigQueryLock = new ReentrantLock();
    public static boolean bigQueryLocked(long timeout) {
        try {
            return bigQueryLock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return false;
        }
    }
    public static String operator = null;


    /**
     * 查询统计服务器时用的 大查询锁
     */
    public static ReentrantLock statServerQueryLock = new ReentrantLock();
    /**
     * 统计服务器大查询，加锁
     * @param timeout
     * @return
     */
    public static boolean statServerQueryLocked(long timeout) {
        try {
            return statServerQueryLock.tryLock(timeout, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * 统计服务器大查询的操作人
     */
    public static String statServerOperator = null;


    /**
     * 查询从数据库服务器时用的 大查询锁
     */
    public static ReentrantLock slaveServerQueryLock = new ReentrantLock();
    /**
     * 从数据库服务器大查询，加锁
     * @param timeout
     * @return
     */
    public static boolean slaveServerQueryLocked(long timeout) {
        try {
        	boolean result = slaveServerQueryLock.tryLock(timeout, TimeUnit.MILLISECONDS);
        	long now = System.currentTimeMillis();
        	if(result){
	    		slaveServerOperateTime = now;
	    		if(slaveServerOperator != null)
	    			slaveServerOperator += " ended";		// 重置以免残留
        	} else {
        		// 卡了超过3分钟自动强制解除
        		if(now - slaveServerOperateTime > 180000) {
        			synchronized(DbLock2.class) {
        				if(now - slaveServerOperateTime > 180000) {
        					slaveServerQueryLock = new ReentrantLock();

        					Log log = LogFactory.getLog("stat.Log");
        					log.info("SlaveServerQueryLocked timeout: " + slaveServerOperator);

        		    		slaveServerOperateTime = now;
        		    		slaveServerOperator = null;		// 重置以免残留
        				}
        			}
        		}
        	}
            return result;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * 从数据库服务器大查询的操作人和操作时间
     */
    public static String slaveServerOperator = null;
    public static long slaveServerOperateTime = 0;

    
    /**
     * SMS短信通道锁
     */
    public static ReentrantLock smsLock = new ReentrantLock();
    /**
     * sms通道，发送短信，加锁
     * @param timeout
     * @return
     */
    public static boolean smsLocked(long timeout) {
        try {
        	boolean result = smsLock.tryLock(timeout, TimeUnit.MILLISECONDS);
        	long now = System.currentTimeMillis();
        	if(result){
        		smsOperateTime = now;
	    		if(smsOperator != null)
	    			smsOperator += " ended";		// 重置以免残留
        	} else {
        		// 卡了超过3分钟自动强制解除
        		if(now - smsOperateTime > 180000) {
        			synchronized(DbLock2.class) {
        				if(now - smsOperateTime > 180000) {
        					smsLock = new ReentrantLock();

        					Log log = LogFactory.getLog("stat.Log");
        					log.info("SMSLocked timeout: " + smsOperator);

        					smsOperateTime = now;
        					smsOperator = null;		// 重置以免残留
        				}
        			}
        		}
        	}
            return result;
        } catch (Exception e) {
            return false;
        }
    }
    /**
     * sms短信通道的操作人和操作时间
     */
    public static String smsOperator = null;
    public static long smsOperateTime = 0;
}

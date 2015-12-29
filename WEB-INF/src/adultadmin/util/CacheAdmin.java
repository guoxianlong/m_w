/**
 *作者：李北金
 *创建日期：2006-08-07
 *说明：本类用于保存缓存。
 */
package adultadmin.util;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * @author lbj
 *  
 */
public class CacheAdmin {
    /**
     * 用于保存所有缓存。
     */
    public static Hashtable cacheMap = new Hashtable();

    public static Object cacheMapLock = new Object();

    // 默认的缓存群
    public static String DEFAULT_GROUP = "default";

    // 默认的缓存过期时间,以秒计
    public static long DEFAULT_LIFE_TIME = 24 * 60 * 60;

    // 最大缓存过期时间,以秒计
    public static long MAX_EXPIRE_TIME = 24 * 60 * 60;

    /**
     * 每组缓存最大容量（条）。
     */
    public static int MAX_SIZE = 50000;

    /**
     * 取得所有缓存。
     * 
     * @return
     */
    public static Hashtable getCacheMap() {
        return cacheMap;
    }

    /**
     * 取得一组缓存。
     * 
     * @param group
     * @return
     */
    public static Hashtable getCacheMap(String group) {
        if (group == null) {
            group = DEFAULT_GROUP;
        }
        Hashtable ht = (Hashtable) cacheMap.get(group);
        if (ht == null) {
            synchronized (cacheMapLock) {
                ht = (Hashtable) cacheMap.get(group);
                if (ht == null) {
                    ht = new Hashtable();
                    cacheMap.put(group, ht);
                }
            }
        }
        return ht;
    }

    /**
     * 保存缓存。
     * 
     * @param key
     * @param value
     * @param group
     */
    public static void putInCache(String key, Object value, String group) {
        putInCache(key, value, group, DEFAULT_LIFE_TIME);
    }

    /**
     * liuyi 2006-09-15 保存缓存。
     * 
     * @param key
     * @param value
     * @param group
     */
    public static void putInCache(String key, Object value, String group,
            long flushPeriod) {
        if (key == null || value == null) {
            return;
        }
        if (group == null) {
            group = DEFAULT_GROUP;
        }
        Hashtable ht = getCacheMap(group);
        if(ht == null){
            return;
        }
        if (ht.size() > MAX_SIZE) {
            ht.clear();
        }

        CacheEntry cache = new CacheEntry();
        cache.setObject(value);
        cache.setLifeTime(flushPeriod);
        cache.setCacheTime(System.currentTimeMillis() / 1000);

        ht.put(key, cache);
    }

    /**
     * 作者：李北金
     * 
     * 创建日期：2006-12-20
     * 
     * 说明：更新缓存
     * 
     * 参数及返回值说明：
     * 
     * @param key
     * @param value
     * @param group
     */
    public static void updateCache(String key, Object value, String group){
        updateCache(key, value, group, DEFAULT_LIFE_TIME);
    }
    /**
     * 作者：李北金
     * 
     * 创建日期：2006-12-20
     * 
     * 说明：更新缓存
     * 
     * 参数及返回值说明：
     * 
     * @param key
     * @param value
     * @param group
     * @param flushPeriod
     */
    public static void updateCache(String key, Object value, String group,
            long flushPeriod) {
        if (key == null) {
            return;
        }
        if (group == null) {
            group = DEFAULT_GROUP;
        }
        Hashtable ht = getCacheMap(group);
        if(ht == null){
            return;
        }
        if (ht.size() > MAX_SIZE) {
            ht.clear();
        }

        if (value != null) {
            CacheEntry cache = new CacheEntry();
            cache.setObject(value);
            cache.setLifeTime(flushPeriod);
            cache.setCacheTime(System.currentTimeMillis() / 1000);

            ht.put(key, cache);
        }
        else {
            ht.remove(key);
        }
    }

    /**
     * 取出缓存。
     * 
     * @param key
     * @param group
     * @param flushPeriod
     * @return
     */
    public static Object getFromCache(String key, String group, int flushPeriod) {
        if (key == null) {
            return null;
        }
        if (group == null) {
            group = DEFAULT_GROUP;
        }
        Hashtable ht = getCacheMap(group);
        if(ht == null){
            return null;
        }
        CacheEntry cache = (CacheEntry) ht.get(key);
        if (cache == null) {
            return null;
        } else if ((System.currentTimeMillis() / 1000 - cache.getCacheTime()) > flushPeriod) {
            ht.remove(key);
            return null;
        }

        return cache.getObject();
    }

    /**
     * 取出缓存。
     * 
     * @param key
     * @param flushPeriod
     * @return
     */
    public static Object getFromCache(String key, int flushPeriod) {
        return getFromCache(key, DEFAULT_GROUP, flushPeriod);
    }

    /**
     * 清空一组缓存。
     * 
     * @param group
     */
    public static void flushGroup(String group) {
        if (group == null) {
            return;
        }
        try {
            getCacheMap(group).clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 清空所有缓存。
     */
    public static void flushAll() {
        Hashtable cm = getCacheMap();
        Hashtable ht = null;
        try {
            Collection list = cm.values();
            if (list != null) {
                Iterator itr = list.iterator();
                while (itr.hasNext()) {
                    ht = (Hashtable) itr.next();
                    ht.clear();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
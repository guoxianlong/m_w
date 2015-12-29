/*
 * Created on 2006-8-7
 *
 */
package cache;

/**
 * @author lbj
 *  
 */
public class CacheEntry {
    Object object;

    long cacheTime;

    //liuyi 2006-09-15 增加缓存 start
    //缓存的过期时间,以秒计
    long lifeTime = 60 * 60;

    //liuyi 2006-09-15 增加缓存 end

    /**
     * @return Returns the cacheTime.
     */
    public long getCacheTime() {
        return cacheTime;
    }

    /**
     * @param cacheTime
     *            The cacheTime to set.
     */
    public void setCacheTime(long cacheTime) {
        this.cacheTime = cacheTime;
    }

    /**
     * @return Returns the object.
     */
    public Object getObject() {
        return object;
    }

    /**
     * @param object
     *            The object to set.
     */
    public void setObject(Object object) {
        this.object = object;
    }

    public long getLifeTime() {
        return lifeTime;
    }

    public void setLifeTime(long lifeTime) {
        this.lifeTime = lifeTime;
    }
}
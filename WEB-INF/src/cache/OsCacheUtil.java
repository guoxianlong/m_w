/*
 * Created on 2008-6-11
 *
 */
package cache;

import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import com.opensymphony.oscache.base.Cache;
import com.opensymphony.oscache.web.ServletCacheAdministrator;

/**
 * 作者：李北金
 * 
 * 创建日期：2008-6-11
 * 
 * 说明：
 */
public class OsCacheUtil {
    public static void clearCache(HttpServletRequest request) {
        Cache cache = ServletCacheAdministrator.getInstance(
                request.getSession().getServletContext()).getCache(request,
                PageContext.APPLICATION_SCOPE);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 5);
        cache.flushAll(cal.getTime());
    }
}

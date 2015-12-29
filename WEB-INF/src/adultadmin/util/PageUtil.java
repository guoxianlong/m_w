/*
 * Created on 2005-11-24
 *
 */
package adultadmin.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import adultadmin.bean.PagingBean;

/**
 * @author bomb
 *  
 */
public class PageUtil {
	
	
	/** 输出排序
	 * @return
	 */
	public static String order(HttpServletRequest request, String query, String order, String label) {
		StringBuffer sb = new StringBuffer(64);
		sb.append("<a href=\"");
		sb.append(request.getRequestURI());
		sb.append("?");
		sb.append(query);
		if(order != null) {
			sb.append("&");
			sb.append(order);
		}
		sb.append("\">");
		sb.append(label);
		sb.append("</a>");
		return sb.toString();
	}
	
	
    /**
     * 作者：
     * 
     * 创建日期：2006-8-21
     * 
     * 说明：数字分页，输出一段wml代码。
     * 
     * 参数及返回值说明：
     * 
     * @param totalPageCount：总页数
     * @param currentPageIndex：当前页码，最小为0
     * @param prefixUrl：分页URL的前缀
     * @param addAnd：是否加&
     * @param separator：分隔符
     * @param pagingParam：页码参数名称
     * @param pageCount
     * @return
     */
    public static String shuzifenye(int totalPageCount, int currentPageIndex,
            String prefixUrl, boolean addAnd, String separator,
            String pagingParam, int pageCount) {
        if (pageCount < 1) {
            pageCount = 5;
        }

        if (totalPageCount == 1) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        if (addAnd) {
            prefixUrl += "&" + pagingParam + "=";
        } else {
            prefixUrl += "?" + pagingParam + "=";
        }

        int hasPrevPage = 0;
        int hasNextPage = 0;
        int startIndex = (currentPageIndex / pageCount) * pageCount;
        int endIndex = (currentPageIndex / pageCount + 1) * pageCount - 1;
        if (endIndex > totalPageCount - 1) {
            endIndex = totalPageCount - 1;
        }

        if (startIndex > 0) {
            hasPrevPage = 1;
        }
        if (endIndex < totalPageCount - 1) {
            hasNextPage = 1;
        }

        if (hasPrevPage == 1) {
            sb.append("<a href=\"" + prefixUrl + (startIndex - 1));
            sb.append("\">&lt;&lt;</a>");
        }
        for (int i = startIndex; i <= endIndex; i++) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if (i != currentPageIndex) {
                sb.append("<a href=\"" + prefixUrl + i);
                sb.append("\">" + (i + 1) + "</a>");
            } else {
                sb.append((i + 1));
            }
        }
        if (hasNextPage == 1) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append("<a href=\"" + prefixUrl + (endIndex + 1));
            sb.append("\">&gt;&gt;</a>");
        }
        sb.append("<br/>");
        return sb.toString();
    }

    /**
     * 作者：
     * 
     * 创建日期：2006-8-21
     * 
     * 说明：上下分页，输出一段wml代码。
     * 
     * 参数及返回值说明：
     * 
     * @param totalPageCount
     * @param currentPageIndex
     * @param prefixUrl
     * @param addAnd
     * @param separator
     * @param pagingParam
     * @return
     */
    public static String shangxiafenye(int totalPageCount,
            int currentPageIndex, String prefixUrl, boolean addAnd,
            String separator, String pagingParam) {
        if (totalPageCount == 1) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        if (addAnd) {
            prefixUrl += "&" + pagingParam + "=";
        } else {
            prefixUrl += "?" + pagingParam + "=";
        }

        int hasPrevPage = 0;
        int hasNextPage = 0;

        if (currentPageIndex > 0) {
            hasPrevPage = 1;
        }
        if (currentPageIndex < totalPageCount - 1) {
            hasNextPage = 1;
        }

        if (hasPrevPage == 1) {
            sb.append("<a href=\"" + prefixUrl + (currentPageIndex - 1));
            sb.append("\">上一页</a>");
        }
        if (hasNextPage == 1) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append("<a href=\"" + prefixUrl + (currentPageIndex + 1));
            sb.append("\">下一页</a>");
        }

        return sb.toString();
    }

    public static String getCurrentPageURL(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (queryString != null) {
            return request.getRequestURL().toString() + "?" + queryString;
        } else {
            return request.getRequestURL().toString();
        }
    }

    /**
     * 作者：
     * 
     * 创建日期：2006-8-21
     * 
     * 说明：数字分页，输出一段wml代码。
     * 
     * 参数及返回值说明：
     * 
     * @param totalPageCount：总页数
     * @param currentPageIndex：当前页码，最小为0
     * @param prefixUrl：分页URL的前缀
     * @param addAnd：是否加&
     * @param separator：分隔符
     * @param pagingParam：页码参数名称
     * @param pageCount
     * @return
     */
    public static String shuzifenye(PagingBean paging, boolean addAnd,
            String separator, String pagingParam, int pageCount) {
        int totalPageCount = paging.getTotalPageCount();
        int currentPageIndex = paging.getCurrentPageIndex();
        String prefixUrl = paging.getPrefixUrl();

        if (pageCount < 1) {
            pageCount = 5;
        }

        if (totalPageCount == 1) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        if (addAnd) {
            prefixUrl += "&" + pagingParam + "=";
        } else {
            prefixUrl += "?" + pagingParam + "=";
        }

        int hasPrevPage = 0;
        int hasNextPage = 0;
        int startIndex = (currentPageIndex / pageCount) * pageCount;
        int endIndex = (currentPageIndex / pageCount + 1) * pageCount - 1;
        if (endIndex > totalPageCount - 1) {
            endIndex = totalPageCount - 1;
        }

        if (startIndex > 0) {
            hasPrevPage = 1;
        }
        if (endIndex < totalPageCount - 1) {
            hasNextPage = 1;
        }

        if (hasPrevPage == 1) {
            sb.append("<a href=\"" + prefixUrl + (startIndex - 1));
            sb.append("\">&lt;&lt;</a>");
        }
        for (int i = startIndex; i <= endIndex; i++) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if (i != currentPageIndex) {
                sb.append("<a href=\"" + prefixUrl + i);
                sb.append("\">" + (i + 1) + "</a>");
            } else {
                sb.append((i + 1));
            }
        }
        if (hasNextPage == 1) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append("<a href=\"" + prefixUrl + (endIndex + 1));
            sb.append("\">&gt;&gt;</a>");
        }
        sb.append("<br/>");
        return sb.toString();
    }

    
    public static int PAGE_COUNT = 5;

    public static String shuzifenye(int totalPageCount, int currentPageIndex,
            String prefixUrl, boolean addAnd, String separator,
            HttpServletResponse response) {
        if (totalPageCount == 1) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        if (addAnd) {
            prefixUrl += "&pageIndex=";
        } else {
            prefixUrl += "?pageIndex=";
        }

        int hasPrevPage = 0;
        int hasNextPage = 0;
        int startIndex = (currentPageIndex / PAGE_COUNT) * PAGE_COUNT;
        int endIndex = (currentPageIndex / PAGE_COUNT + 1) * PAGE_COUNT - 1;
        if (endIndex > totalPageCount - 1) {
            endIndex = totalPageCount - 1;
        }

        if (startIndex > 0) {
            hasPrevPage = 1;
        }
        if (endIndex < totalPageCount - 1) {
            hasNextPage = 1;
        }

        if (hasPrevPage == 1) {
            sb.append("<a href=\""
                    + response.encodeURL(prefixUrl + (startIndex - 1)));
            sb.append("\">&lt;&lt;</a>");
        }
        for (int i = startIndex; i <= endIndex; i++) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if (i != currentPageIndex) {
                sb.append("<a href=\"" + response.encodeURL(prefixUrl + i));
                sb.append("\">" + (i + 1) + "</a>");
            } else {
                sb.append((i + 1));
            }
        }
        if (hasNextPage == 1) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append("<a href=\""
                    + response.encodeURL(prefixUrl + (endIndex + 1)));
            sb.append("\">&gt;&gt;</a>");
        }
        sb.append("<br/>");
        return sb.toString();
    }

    public static String shangxiafenye(PagingBean page, boolean addAnd,
            String separator) {
        if (page.getTotalPageCount() == 1) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        String prefix = page.getPrefixUrl();
        if (addAnd) {
        	prefix += "&pageIndex=";
        } else {
        	prefix += "?pageIndex=";
        }

        int hasPrevPage = 0;
        int hasNextPage = 0;

        if (page.getCurrentPageIndex() > 0) {
            hasPrevPage = 1;
        }
        if (page.getCurrentPageIndex() < page.getTotalPageCount() - 1) {
            hasNextPage = 1;
        }
        
        if (hasNextPage == 1) {            
            sb.append("<a href=\""
                    + prefix + (page.getCurrentPageIndex() + 1));
            sb.append("\">下一页</a>");
        } else 
        	sb.append("下一页");
        sb.append(separator);
        if (hasPrevPage == 1) {
            sb.append("<a href=\""
                    + prefix + (page.getCurrentPageIndex() - 1));
            sb.append("\">上一页</a>");
        } else 
        	sb.append("上一页");
        
        sb.append("<br/>");

        return sb.toString();
    }

    
    /**
     * 作者：
     * 
     * 创建日期：2006-8-21
     * 
     * 说明：上下分页，输出一段wml代码。
     * 
     * 参数及返回值说明：
     * 
     * @param totalPageCount
     * @param currentPageIndex
     * @param prefixUrl
     * @param addAnd
     * @param separator
     * @param pagingParam
     * @return
     */
    public static String shangxiafenye(PagingBean paging, boolean addAnd,
            String separator, String pagingParam) {
        int totalPageCount = paging.getTotalPageCount();
        int currentPageIndex = paging.getCurrentPageIndex();
        String prefixUrl = paging.getPrefixUrl();

        if (totalPageCount == 1) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        if (addAnd) {
            prefixUrl += "&" + pagingParam + "=";
        } else {
            prefixUrl += "?" + pagingParam + "=";
        }

        int hasPrevPage = 0;
        int hasNextPage = 0;

        if (currentPageIndex > 0) {
            hasPrevPage = 1;
        }
        if (currentPageIndex < totalPageCount - 1) {
            hasNextPage = 1;
        }

        if (hasPrevPage == 1) {
            sb.append("<a href=\"" + prefixUrl + (currentPageIndex - 1));
            sb.append("\">上一页</a>");
        }
        if (hasNextPage == 1) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            sb.append("<a href=\"" + prefixUrl + (currentPageIndex + 1));
            sb.append("\">下一页</a>");
        }

        return sb.toString();
    }

    /**
     * 作者：
     * 
     * 创建日期：2006-8-21
     * 
     * 说明：数字分页，输出一段wml代码。
     * 
     * 参数及返回值说明：
     * 
     * @param totalPageCount：总页数
     * @param currentPageIndex：当前页码，最小为0
     * @param prefixUrl：分页URL的前缀
     * @param addAnd：是否加&
     * @param separator：分隔符
     * @param pagingParam：页码参数名称
     * @param pageCount
     * @return
     */
    public static String shuzifenye(PagingBean paging, boolean addAnd,
            String separator, String pagingParam, int pageCount, String method) {
        int totalPageCount = paging.getTotalPageCount();
        int currentPageIndex = paging.getCurrentPageIndex();
        String prefixUrl = paging.getPrefixUrl();

        if (pageCount < 1) {
            pageCount = 5;
        }

        if (totalPageCount == 1) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        if (addAnd) {
            prefixUrl += "&" + pagingParam + "=";
        } else {
            prefixUrl += "?" + pagingParam + "=";
        }

        int hasPrevPage = 0;
        int hasNextPage = 0;
        int startIndex = (currentPageIndex / pageCount) * pageCount;
        int endIndex = (currentPageIndex / pageCount + 1) * pageCount - 1;
        if (endIndex > totalPageCount - 1) {
            endIndex = totalPageCount - 1;
        }

        if (startIndex > 0) {
            hasPrevPage = 1;
        }
        if (endIndex < totalPageCount - 1) {
            hasNextPage = 1;
        }

        if (hasPrevPage == 1) {
            if ("post".equalsIgnoreCase(method)) {
                sb.append("<anchor title=\"go\">&lt;&lt;");
                sb.append("<go href=\"" + prefixUrl + (startIndex - 1)
                        + "\" method=\"post\"/>");
                sb.append("</anchor>");
            } else {
                sb.append("<a href=\"" + prefixUrl + (startIndex - 1));
                sb.append("\">&lt;&lt;</a>");
            }
        }
        for (int i = startIndex; i <= endIndex; i++) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if (i != currentPageIndex) {
                if ("post".equalsIgnoreCase(method)) {
                    sb.append("<anchor title=\"go\">" + (i + 1));
                    sb.append("<go href=\"" + prefixUrl + i
                            + "\" method=\"post\"/>");
                    sb.append("</anchor>");
                } else {
                    sb.append("<a href=\"" + prefixUrl + i);
                    sb.append("\">" + (i + 1) + "</a>");
                }
            } else {
                sb.append((i + 1));
            }
        }
        if (hasNextPage == 1) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if ("post".equalsIgnoreCase(method)) {
                sb.append("<anchor title=\"go\">&gt;&gt;");
                sb.append("<go href=\"" + prefixUrl + (endIndex + 1)
                        + "\" method=\"post\"/>");
                sb.append("</anchor>");
            } else {
                sb.append("<a href=\"" + prefixUrl + (endIndex + 1));
                sb.append("\">&gt;&gt;</a>");
            }

        }
        sb.append("<br/>");
        return sb.toString();
    }

    /**
     * 作者：
     * 
     * 创建日期：2006-8-21
     * 
     * 说明：上下分页，输出一段wml代码。
     * 
     * 参数及返回值说明：
     * 
     * @param totalPageCount
     * @param currentPageIndex
     * @param prefixUrl
     * @param addAnd
     * @param separator
     * @param pagingParam
     * @return
     */
    public static String shangxiafenye(PagingBean paging, boolean addAnd,
            String separator, String pagingParam, String method) {
        int totalPageCount = paging.getTotalPageCount();
        int currentPageIndex = paging.getCurrentPageIndex();
        String prefixUrl = paging.getPrefixUrl();

        if (totalPageCount == 1) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        if (addAnd) {
            prefixUrl += "&" + pagingParam + "=";
        } else {
            prefixUrl += "?" + pagingParam + "=";
        }

        int hasPrevPage = 0;
        int hasNextPage = 0;

        if (currentPageIndex > 0) {
            hasPrevPage = 1;
        }
        if (currentPageIndex < totalPageCount - 1) {
            hasNextPage = 1;
        }

        if (hasPrevPage == 1) {
            if ("post".equalsIgnoreCase(method)) {
                sb.append("<anchor title=\"go\">上一页");
                sb.append("<go href=\"" + prefixUrl + (currentPageIndex - 1)
                        + "\" method=\"post\"/>");
                sb.append("</anchor>");
            } else {
                sb.append("<a href=\"" + prefixUrl + (currentPageIndex - 1));
                sb.append("\">上一页</a>");
            }
        }
        if (hasNextPage == 1) {
            if (sb.length() > 0) {
                sb.append(separator);
            }

            if ("post".equalsIgnoreCase(method)) {
                sb.append("<anchor title=\"go\">下一页");
                sb.append("<go href=\"" + prefixUrl + (currentPageIndex + 1)
                        + "\" method=\"post\"/>");
                sb.append("</anchor>");
            } else {
                sb.append("<a href=\"" + prefixUrl + (currentPageIndex + 1));
                sb.append("\">下一页</a>");
            }
        }

        return sb.toString();
    }

    public static String shangxiafenye(int totalPageCount,
			int currentPageIndex, String prefixUrl, boolean addAnd,
			String separator, HttpServletResponse response) {
		if (totalPageCount == 1) {
			return "";
		}

		StringBuffer sb = new StringBuffer();

		if (addAnd) {
			prefixUrl += "&pageIndex=";
		} else {
			prefixUrl += "?pageIndex=";
		}

		int hasPrevPage = 0;
		int hasNextPage = 0;

		if (currentPageIndex > 0) {
			hasPrevPage = 1;
		}
		if (currentPageIndex < totalPageCount - 1) {
			hasNextPage = 1;
		}

		if (hasNextPage == 1) {
			sb.append("<a href=\""
					+ response.encodeURL(prefixUrl + (currentPageIndex + 1)));
			sb.append("\">下一页</a>");
		}

		if (hasPrevPage == 1) {
			if (sb.length() > 0) {
				sb.append(separator);
			}
			sb.append("<a href=\""
					+ response.encodeURL(prefixUrl + (currentPageIndex - 1)));
			sb.append("\">上一页</a>");
		}

		return sb.toString();
	}

    public static String getBackTo(HttpServletRequest request) {
        try {
            return URLEncoder.encode(getCurrentPageURL(request), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String fenye(PagingBean paging, boolean addAnd,
            String separator, String pagingParam, int pageCount) {
        int totalPageCount = paging.getTotalPageCount();
        int currentPageIndex = paging.getCurrentPageIndex();
        String prefixUrl = paging.getPrefixUrl();

        if (pageCount < 1) {
            pageCount = 5;
        }

        if (totalPageCount <= 1) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        if (prefixUrl.indexOf("?") != -1&&!prefixUrl.equals("#")) {
            prefixUrl += "&" + pagingParam + "=";
        } else if(!prefixUrl.equals("#")){
            prefixUrl += "?" + pagingParam + "=";
        }

        int hasPrevPage = 0;
        int hasNextPage = 0;
        int startIndex = (currentPageIndex / pageCount) * pageCount;
        int endIndex = (currentPageIndex / pageCount + 1) * pageCount - 1;
        if (endIndex > totalPageCount - 1) {
            endIndex = totalPageCount - 1;
        }

        if (startIndex > 0) {
            hasPrevPage = 1;
        }
        if (endIndex < totalPageCount - 1) {
            hasNextPage = 1;
        }
        sb.append("第" + (currentPageIndex == 0 && totalPageCount == 0 ? 0 : (currentPageIndex + 1)) + "页" + separator + "共" + totalPageCount + "页");
        
        if (paging.getCurrentPageIndex() != 0) {//当前不是第一页，要加"首页"链接
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if(prefixUrl.equals("#")){
            	sb.append("<a href=\"" + prefixUrl + "\"");
            	sb.append("onclick=\"");
            	sb.append(paging.getJsFunction().replace("pageIndex",0 +""));
            	sb.append("\"");
            	sb.append(">首页</a>");
            }else{
            	sb.append("<a href=\"" + prefixUrl + "0\">首页</a>");
            }
             
        }
        if (hasPrevPage == 1) {//当前不是前n页，要加"<<"链接
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if(prefixUrl.equals("#")){
            	sb.append("<a href=\"" + prefixUrl + "\"");
            	sb.append("onclick=\"");
            	sb.append(paging.getJsFunction().replace("pageIndex",(startIndex - 1) +""));
            	sb.append("\"");
            	sb.append(">&lt;&lt;</a>");
            }else{
            	sb.append("<a href=\"" + prefixUrl + (startIndex - 1));
                sb.append("\">&lt;&lt;</a>");
            }
        }
        if (paging.getCurrentPageIndex() != 0) {//当前不是第一页，要加"上一页"链接
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if(prefixUrl.equals("#")){
            	sb.append("<a href=\"" + prefixUrl + "\"");
            	sb.append("onclick=\"");
            	sb.append(paging.getJsFunction().replace("pageIndex",(paging.getCurrentPageIndex() - 1) +""));
            	sb.append("\"");
            	sb.append(">上一页</a>");
            }else{
            	sb.append("<a href=\"" + prefixUrl + ""
                        + (paging.getCurrentPageIndex() - 1) + "\">上一页</a>");
            }
        }
        for (int i = startIndex; i <= endIndex; i++) {//页码链接
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if (i != currentPageIndex) {
            	if(prefixUrl.equals("#")){
            		sb.append("<a href=\"" + prefixUrl +"\"");
            	}else{
            		sb.append("<a href=\"" + prefixUrl + i+"\"");
            	}
                
                if(!StringUtil.convertNull(paging.getJsFunction()).equals("")){
                	sb.append("onclick=\"");
                	sb.append(paging.getJsFunction().replace("pageIndex",i +""));
                	sb.append("\"");
                }
                sb.append(">" + (i + 1) + "</a>");
            } else {
                sb.append((i + 1));
            }
        }
        if (paging.getCurrentPageIndex() != (paging.getTotalPageCount() - 1)) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if(prefixUrl.equals("#")){
            	sb.append("<a href=\"" + prefixUrl + "\"");
            	sb.append("onclick=\"");
            	sb.append(paging.getJsFunction().replace("pageIndex",(paging.getCurrentPageIndex() + 1) +""));
            	sb.append("\"");
            	sb.append(">下一页</a>");
            }else{
            	sb.append("<a href=\"" + prefixUrl + ""
                        + (paging.getCurrentPageIndex() + 1) + "\">下一页</a>");
            }
        }
        if (hasNextPage == 1) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if(prefixUrl.equals("#")){
            	sb.append("<a href=\"" + prefixUrl + "\"");
            	sb.append("onclick=\"");
            	sb.append(paging.getJsFunction().replace("pageIndex",(endIndex + 1) +""));
            	sb.append("\"");
            	sb.append(">&gt;&gt;</a>");
            }else{
            	sb.append("<a href=\"" + prefixUrl + (endIndex + 1));
                sb.append("\">&gt;&gt;</a>");
            }
        }
        if (paging.getCurrentPageIndex() != (paging.getTotalPageCount() - 1)) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if(prefixUrl.equals("#")){
            	sb.append("<a href=\"" + prefixUrl + "\"");
            	sb.append("onclick=\"");
            	sb.append(paging.getJsFunction().replace("pageIndex",(paging.getTotalPageCount() - 1) +""));
            	sb.append("\"");
            	sb.append(">尾页</a>");
            }else{
            	sb.append("<a href=\"" + prefixUrl + ""
                        + (paging.getTotalPageCount() - 1) + "\">尾页</a>");
            }
        }

        return sb.toString();
    }
    
    /**
     * 分页ajax方式的改为在loadPage了
     * @param paging
     * @param addAnd
     * @param separator
     * @param pagingParam
     * @param pageCount
     * @return
     */
    public static String fenyeAjax(PagingBean paging, boolean addAnd,
            String separator, int pageCount) {
        int totalPageCount = paging.getTotalPageCount();
        int currentPageIndex = paging.getCurrentPageIndex();
        String prefixUrl = paging.getPrefixUrl();

        if (pageCount < 1) {
            pageCount = 5;
        }

        if (totalPageCount <= 1) {
            return "";
        }

        StringBuffer sb = new StringBuffer();

        int hasPrevPage = 0;
        int hasNextPage = 0;
        int startIndex = (currentPageIndex / pageCount) * pageCount;
        int endIndex = (currentPageIndex / pageCount + 1) * pageCount - 1;
        if (endIndex > totalPageCount - 1) {
            endIndex = totalPageCount - 1;
        }

        if (startIndex > 0) {
            hasPrevPage = 1;
        }
        if (endIndex < totalPageCount - 1) {
            hasNextPage = 1;
        }
        sb.append("第" + (currentPageIndex == 0 && totalPageCount == 0 ? 0 : (currentPageIndex + 1)) + "页" + separator + "共" + totalPageCount + "页");
        
        if (paging.getCurrentPageIndex() != 0) {//当前不是第一页，要加"首页"链接
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if(prefixUrl.equals("#")){
            	sb.append("<a href=\"" + prefixUrl + ");\"");
            	sb.append("onclick=\"");
            	sb.append(paging.getJsFunction().replace("pageIndex",0 +""));
            	sb.append("\"");
            	sb.append(">首页</a>");
            }else{
            	sb.append("<a href=\"" + prefixUrl + "0);\">首页</a>");
            }
             
        }
        if (hasPrevPage == 1) {//当前不是前n页，要加"<<"链接
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if(prefixUrl.equals("#")){
            	sb.append("<a href=\"" + prefixUrl + ");\"");
            	sb.append("onclick=\"");
            	sb.append(paging.getJsFunction().replace("pageIndex",(startIndex - 1) +""));
            	sb.append("\"");
            	sb.append(">&lt;&lt;</a>");
            }else{
            	sb.append("<a href=\"" + prefixUrl + (startIndex - 1));
                sb.append(");\">&lt;&lt;</a>");
            }
        }
        if (paging.getCurrentPageIndex() != 0) {//当前不是第一页，要加"上一页"链接
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if(prefixUrl.equals("#")){
            	sb.append("<a href=\"" + prefixUrl + ");\"");
            	sb.append("onclick=\"");
            	sb.append(paging.getJsFunction().replace("pageIndex",(paging.getCurrentPageIndex() - 1) +""));
            	sb.append("\"");
            	sb.append(">上一页</a>");
            }else{
            	sb.append("<a href=\"" + prefixUrl + ""
                        + (paging.getCurrentPageIndex() - 1) + ");\">上一页</a>");
            }
        }
        for (int i = startIndex; i <= endIndex; i++) {//页码链接
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if (i != currentPageIndex) {
            	if(prefixUrl.equals("#")){
            		sb.append("<a href=\"" + prefixUrl +");\"");
            	}else{
            		sb.append("<a href=\"" + prefixUrl + i+");\"");
            	}
                
                if(!StringUtil.convertNull(paging.getJsFunction()).equals("")){
                	sb.append("onclick=\"");
                	sb.append(paging.getJsFunction().replace("pageIndex",i +""));
                	sb.append("\"");
                }
                sb.append(">" + (i + 1) + "</a>");
            } else {
                sb.append((i + 1));
            }
        }
        if (paging.getCurrentPageIndex() != (paging.getTotalPageCount() - 1)) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if(prefixUrl.equals("#")){
            	sb.append("<a href=\"" + prefixUrl + ");\"");
            	sb.append("onclick=\"");
            	sb.append(paging.getJsFunction().replace("pageIndex",(paging.getCurrentPageIndex() + 1) +""));
            	sb.append("\"");
            	sb.append(">下一页</a>");
            }else{
            	sb.append("<a href=\"" + prefixUrl + ""
                        + (paging.getCurrentPageIndex() + 1) + ");\">下一页</a>");
            }
        }
        if (hasNextPage == 1) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if(prefixUrl.equals("#")){
            	sb.append("<a href=\"" + prefixUrl + ");\"");
            	sb.append("onclick=\"");
            	sb.append(paging.getJsFunction().replace("pageIndex",(endIndex + 1) +""));
            	sb.append("\"");
            	sb.append(">&gt;&gt;</a>");
            }else{
            	sb.append("<a href=\"" + prefixUrl + (endIndex + 1));
                sb.append(");\">&gt;&gt;</a>");
            }
        }
        if (paging.getCurrentPageIndex() != (paging.getTotalPageCount() - 1)) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if(prefixUrl.equals("#")){
            	sb.append("<a href=\"" + prefixUrl + ");\"");
            	sb.append("onclick=\"");
            	sb.append(paging.getJsFunction().replace("pageIndex",(paging.getTotalPageCount() - 1) +""));
            	sb.append("\"");
            	sb.append(">尾页</a>");
            }else{
            	sb.append("<a href=\"" + prefixUrl + ""
                        + (paging.getTotalPageCount() - 1) + ");\">尾页</a>");
            }
        }

        return sb.toString();
    }
}

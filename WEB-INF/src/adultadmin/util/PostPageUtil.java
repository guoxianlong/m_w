/*
 * Created on 2009-11-27
 *
 */
package adultadmin.util;

import java.util.Random;

import adultadmin.bean.PagingBean;

/**
 * 作者：李北金
 * 
 * 创建日期：2009-11-27
 * 
 * 说明：
 */
public class PostPageUtil {
    /**
     * 作者：李北金
     * 
     * 创建日期：2009-11-27
     * 
     * 说明：处理URL里的queryString的编码问题
     * 
     * 参数及返回值说明：
     * 
     * @param encoding
     */
    public static String[] toPostUrl(String url, String title) {
        Random rand = new Random();
        String name = rand.nextInt(10) + "" + rand.nextInt(10) + ""
                + rand.nextInt(10) + "" + rand.nextInt(10) + ""
                + rand.nextInt(10) + "" + rand.nextInt(10) + ""
                + rand.nextInt(10) + "" + rand.nextInt(10) + ""
                + rand.nextInt(10) + "" + rand.nextInt(10);
        try {
            if (url == null || title == null) {
                return null;
            }
            if (url.indexOf("?") == -1) {
                return null;
            }

            String qs = url.substring(url.indexOf("?") + 1);
            System.out.println(qs);
            String[] ss = qs.split("&");
            if (ss == null) {
                return null;
            }
            String n, p = null;
            String[] sss = null;
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < ss.length; i++) {
                if (ss[i].indexOf("=") == -1) {
                    continue;
                }
                n = ss[i].substring(0, ss[i].indexOf("="));
                p = ss[i].substring(ss[i].indexOf("=") + 1);
                sb.append("<input type='hidden' name='" + n + "' value='" + p
                        + "'/>");
            }
            url = url.substring(0, url.indexOf("?"));
            StringBuffer sb1 = new StringBuffer();
            sb1.append("<form method='post' action='" + url + "' id='" + name
                    + "'>");
            sb1.append(sb);
            sb1.append("</form>");

            String submit = "<input type='button' value='" + title
                    + "' onclick='document.getElementById(\"" + name + "\").submit();'/>";
            String[] ssss = new String[2];
            ssss[0] = sb1.toString();
            ssss[1] = submit;
            return ssss;
        } catch (Exception e) {
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

        if (prefixUrl.indexOf("?") != -1) {
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
        sb.append("第"
                + (currentPageIndex == 0 && totalPageCount == 0 ? 0
                        : (currentPageIndex + 1)) + "页" + separator + "共"
                + totalPageCount + "页");

        StringBuffer sb1 = new StringBuffer();
        String[] ss = null;
        if (paging.getCurrentPageIndex() != 0) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            ss = toPostUrl(prefixUrl + "0", "首页");
            if (ss != null && ss.length == 2) {
                sb1.append(ss[0]);
                sb.append(ss[1]);
            }
        }
        if (hasPrevPage == 1) {
            if (sb.length() > 0) {
                sb.append(separator);
            }

            ss = toPostUrl(prefixUrl + (startIndex - 1), "&lt;&lt;");
            if (ss != null && ss.length == 2) {
                sb1.append(ss[0]);
                sb.append(ss[1]);
            }
        }
        if (paging.getCurrentPageIndex() != 0) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            ss = toPostUrl(prefixUrl + (paging.getCurrentPageIndex() - 1),
                    "上一页");
            if (ss != null && ss.length == 2) {
                sb1.append(ss[0]);
                sb.append(ss[1]);
            }
        }
        for (int i = startIndex; i <= endIndex; i++) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            if (i != currentPageIndex) {
                ss = toPostUrl(prefixUrl + i, (i + 1) + "");
                if (ss != null && ss.length == 2) {
                    sb1.append(ss[0]);
                    sb.append(ss[1]);
                }
            } else {
                sb.append((i + 1));
            }
        }
        if (paging.getCurrentPageIndex() != (paging.getTotalPageCount() - 1)) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            ss = toPostUrl(prefixUrl + (paging.getCurrentPageIndex() + 1),
                    "下一页");
            if (ss != null && ss.length == 2) {
                sb1.append(ss[0]);
                sb.append(ss[1]);
            }
        }
        if (hasNextPage == 1) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            ss = toPostUrl(prefixUrl + (endIndex + 1), "&gt;&gt;");
            if (ss != null && ss.length == 2) {
                sb1.append(ss[0]);
                sb.append(ss[1]);
            }
        }
        if (paging.getCurrentPageIndex() != (paging.getTotalPageCount() - 1)) {
            if (sb.length() > 0) {
                sb.append(separator);
            }
            ss = toPostUrl(prefixUrl + (paging.getTotalPageCount() - 1), "尾页");
            if (ss != null && ss.length == 2) {
                sb1.append(ss[0]);
                sb.append(ss[1]);
            }
        }

        return sb1.toString() + sb.toString();
    }
}

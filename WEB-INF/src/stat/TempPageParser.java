/*
 * Created on 2008-7-18
 *
 */
package stat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import adultadmin.util.StringUtil;

/**
 * 作者：李北金
 * 
 * 创建日期：2008-7-18
 * 
 * 说明：
 */
public class TempPageParser {

    public static void main(String[] args) {
        parse("http://wap.mmb.cn/log/access.log", "2008-07-18 15:00:00", "2008-07-18 16:00:00");
    }

    public static Hashtable parse(String address, String startTime, String endTime) {
        try {
            String str = null;
            URL url = new URL(address);
            BufferedReader br = new BufferedReader(new InputStreamReader(url
                    .openStream()));
            str = br.readLine();
            int fr;
            String sessionId;
            Hashtable ht = new Hashtable();
            Hashtable ht1 = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date et = null;
            if (endTime != null) {
                et = sdf.parse(endTime);
            }
            Date st = null;
            if (startTime != null) {
                st = sdf.parse(startTime);
            }
            String time = null;
            while (str != null) {
                if (st != null || et != null) {
                    time = getTime(str);
                    if (time != null) {
                        if(st != null){
                            if (sdf.parse(time).before(st)) {
                                str = br.readLine();
                                continue;
                            }
                        }
                        if (et != null) {
                            if (sdf.parse(time).after(et)) {
                                break;
                            }
                        }
                    }
                }
                fr = getFr(str);
                if (fr <= 0) {
                    str = br.readLine();
                    continue;
                }
                sessionId = getSessionId(str);
                if (sessionId == null) {
                    str = br.readLine();
                    continue;
                }

                ht1 = (Hashtable) ht.get("" + fr);
                if (ht1 == null) {
                    ht1 = new Hashtable();
                }
                ht1.put(sessionId, "");
                ht.put("" + fr, ht1);
                str = br.readLine();
            }

            Enumeration enu = ht.keys();
            Hashtable results = new Hashtable();
            while (enu.hasMoreElements()) {
                fr = StringUtil.toInt((String) enu.nextElement());
                System.out.println(fr + "\t"
                        + ((Hashtable) ht.get("" + fr)).size());
                results.put("" + fr, "" + ((Hashtable) ht.get("" + fr)).size());
            }
            
            return results;
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public static int getFr(String str) {
        int fr = 0;
        if (str == null) {
            return 0;
        }
        try {
            fr = StringUtil.toInt(str.substring(str.indexOf("fr:") + 3, str
                    .indexOf(":fr")));
        } catch (Exception e) {
        }

        return fr;
    }

    public static String getSessionId(String str) {
        String sessionId = null;
        if (str == null) {
            return null;
        }
        try {
            sessionId = str.substring(str.indexOf("sessionId:") + 10, str
                    .indexOf(":sessionId"));
        } catch (Exception e) {
        }

        return sessionId;
    }

    public static String getTime(String str) {
        String sessionId = null;
        if (str == null) {
            return null;
        }
        try {
            sessionId = str.substring(str.indexOf("[") + 1, str.indexOf("]"));
        } catch (Exception e) {
        }

        return sessionId;
    }
}

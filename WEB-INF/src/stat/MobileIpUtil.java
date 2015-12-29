/*
 * Created on 2009-5-15
 *
 */
package stat;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：李北金
 * 
 * 创建日期：2009-5-15
 * 
 * 说明：
 */
public class MobileIpUtil {

    public static void main(String[] args) {
        System.out.println(isMobileIp("218.202.227.177"));
    }

    public static List mobileIps = null;

    public static boolean isMobileIp(String ip) {
        long ipl = IP.ipToLong(ip);
        for (int i = 0; i < getMobileIps().size(); i++) {
            IP m = (IP) getMobileIps().get(i);
            if (m.isInScope(ipl))
                return true;
        }
        return false;
    }

    public static String[] mobileIPString = { "211.103.0.0/16", //cmwap
            "211.136.0.0/13", //cmwap
            "218.200.0.0/13", //cmwap
            "221.130.0.0/15", //cmwap
            "117.128.0.0/10", //cmnet
            "120.192.0.0/10", //cmnet
            "112.0.0.0/10", //cmnet
            "111.0.0.0/10" //cmnet
    };

    public static List getMobileIps() {
        if (mobileIps != null) {
            return mobileIps;
        }

        mobileIps = new ArrayList();
        for (int i = 0; i < mobileIPString.length; i++) {
            mobileIps.add(new IP(mobileIPString[i]));
        }

        return mobileIps;
    }
}

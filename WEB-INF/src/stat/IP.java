package stat;

/**
 * @author bomb ip 相关工具，例如判断一个ip是否在区段内
 */
public class IP {
    long ip;

    long mask;

    static IP[] localIps = { new IP("127.0.0.1/8"), new IP("192.168.0.1/16"),
            new IP("10.0.0.1/8"), };

    // exsample: IP("211.151.44.133", "255.255.255.0");
    public IP(String ip, String mask) {
        this.ip = ipToLong(ip);
        this.mask = ipToLong(mask);
        this.ip = this.ip & this.mask;
    }

    //	 exsample: IP("211.151.44.133", 24);
    public IP(String ip, int maskLen) {
        this.ip = ipToLong(ip);
        this.mask = maskToLong(maskLen);
        this.ip = this.ip & this.mask;
    }

    //	 exsample: IP("211.151.44.133");
    //				IP("211.151.44.133/24");
    public IP(String ipMask) {
        String[] seg = ipMask.split("[/]", 2);
        if (seg.length == 0)
            return;
        this.ip = ipToLong(seg[0]);
        if (seg.length == 2) {
            int maskLen = Integer.parseInt(seg[1]);
            if (maskLen >= 0 && maskLen <= 32)
                this.mask = maskToLong(maskLen);
        } else if (ip != 0)
            this.mask = maskToLong(32);
        this.ip = this.ip & this.mask;
    }

    public boolean isInScope(String ip) {
        return isInScope(ipToLong(ip));
    }

    public boolean isInScope(long ip) {
        return (ip & mask) == this.ip;
    }

    public static long ipToLong(String ipString) {
        try {
            String[] segment = ipString.split("[.]");
            if (segment.length == 4) {
                return (Long.parseLong(segment[0]) << 24)
                        + (Long.parseLong(segment[1]) << 16)
                        + (Long.parseLong(segment[2]) << 8)
                        + (Long.parseLong(segment[3]) << 0);
            }
            return 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static String longToIp(long ip) {
        return ((ip & 0xFF000000) >> 24) + "." + ((ip & 0xFF0000) >> 16) + "."
                + ((ip & 0xFF00) >> 8) + "." + ((ip & 0xFF) >> 0);
    }

    public static long maskToLong(int maskLen) {
        return ((1l << maskLen) - 1) << (32 - maskLen);
    }

    public static int longToMask(long mask) {
        int maskLen = 32;
        for (; maskLen > 0 && (mask & 1) == 0; maskLen--)
            mask = mask >> 1;
        return maskLen;
    }

    public long getIp() {
        return ip;
    }

    public void setIp(long ip) {
        this.ip = ip;
    }

    public long getMask() {
        return mask;
    }

    public void setMask(long mask) {
        this.mask = mask;
    }

    public String toString() {
        int maskLen = longToMask(mask);
        if (maskLen == 32 || maskLen == 0)
            return longToIp(ip);
        return longToIp(ip) + "/" + maskLen;
    }

    // 如果mask == 0则为无效地址0.0.0.0
    public boolean isValid() {
        return mask != 0;
    }

    public long getIplStart() {
        return ip;
    }

    public long getIplEnd() {
        return ip | ((1l << 32) - 1 - mask);
    }

    public String getIpStart() {
        return longToIp(ip);
    }

    public String getIpEnd() {
        return longToIp(getIplEnd());
    }

    public String getIpRange() {
        return getIpStart() + " - " + getIpEnd();
    }

    public static boolean isLocalIp(String ip) {
        return isLocalIp(ipToLong(ip));
    }

    public static boolean isLocalIp(long ip) {
        for (int i = 0; i < localIps.length; i++) {
            if (localIps[i].isInScope(ip))
                return true;
        }
        return false;
    }
}
/*
 * Created on 2006-9-1
 *
 */
package adultadmin.util;

import java.util.Hashtable;

/**
 * 作者：李北金
 * 
 * 创建日期：2006-9-1
 * 
 * 说明：地区相关。
 */
public class AreaFrk {
    public static int CHINA = 1;

    public static Hashtable provinceList = null;

    public static Hashtable provinceList2 = null;

    public static Hashtable cityList = null;

    public static Hashtable getProvinceList() {
        if (provinceList == null) {
            provinceList = new Hashtable();
            provinceList.put(String.valueOf(1), "广东");
            provinceList.put(String.valueOf(2), "江苏");
            provinceList.put(String.valueOf(3), "浙江");
            provinceList.put(String.valueOf(4), "北京");
            provinceList.put(String.valueOf(5), "上海");
            provinceList.put(String.valueOf(6), "辽宁");
            provinceList.put(String.valueOf(7), "福建");
            provinceList.put(String.valueOf(8), "天津");
            provinceList.put(String.valueOf(9), "山东");
            provinceList.put(String.valueOf(10), "湖北");
            provinceList.put(String.valueOf(11), "甘肃");
            provinceList.put(String.valueOf(12), "河北");
            provinceList.put(String.valueOf(13), "重庆");
            provinceList.put(String.valueOf(14), "四川");
            provinceList.put(String.valueOf(15), "陕西");
            provinceList.put(String.valueOf(16), "安徽");
            provinceList.put(String.valueOf(17), "海南");
            provinceList.put(String.valueOf(18), "广西");
            provinceList.put(String.valueOf(19), "江西");
            provinceList.put(String.valueOf(20), "山西");
            provinceList.put(String.valueOf(21), "湖南");
            provinceList.put(String.valueOf(22), "河南");
            provinceList.put(String.valueOf(23), "青海");
            provinceList.put(String.valueOf(24), "贵州");
            provinceList.put(String.valueOf(25), "宁夏");
            provinceList.put(String.valueOf(26), "云南");
            provinceList.put(String.valueOf(27), "吉林");
            provinceList.put(String.valueOf(28), "内蒙古");
            provinceList.put(String.valueOf(29), "新疆");
            provinceList.put(String.valueOf(30), "黑龙江");
            provinceList.put(String.valueOf(31), "西藏");
        }
        return provinceList;
    }
    
    public static Hashtable getProvinceList2() {
        if (provinceList2 == null) {
            provinceList2 = new Hashtable();
            provinceList2.put("广东", String.valueOf(1));
            provinceList2.put("江苏", String.valueOf(2));
            provinceList2.put("浙江", String.valueOf(3));
            provinceList2.put("北京", String.valueOf(4));
            provinceList2.put("上海", String.valueOf(5));
            provinceList2.put("辽宁", String.valueOf(6));
            provinceList2.put("福建", String.valueOf(7));
            provinceList2.put("天津", String.valueOf(8));
            provinceList2.put("山东", String.valueOf(9));
            provinceList2.put("湖北", String.valueOf(10));
            provinceList2.put("甘肃", String.valueOf(11));
            provinceList2.put("河北", String.valueOf(12));
            provinceList2.put("重庆", String.valueOf(13));
            provinceList2.put("四川", String.valueOf(14));
            provinceList2.put("陕西", String.valueOf(15));
            provinceList2.put("安徽", String.valueOf(16));
            provinceList2.put("海南", String.valueOf(17));
            provinceList2.put("广西", String.valueOf(18));
            provinceList2.put("江西", String.valueOf(19));
            provinceList2.put("山西", String.valueOf(20));
            provinceList2.put("湖南", String.valueOf(21));
            provinceList2.put("河南", String.valueOf(22));
            provinceList2.put("青海", String.valueOf(23));
            provinceList2.put("贵州", String.valueOf(24));
            provinceList2.put("宁夏", String.valueOf(25));
            provinceList2.put("云南", String.valueOf(26));
            provinceList2.put("吉林", String.valueOf(27));
            provinceList2.put("内蒙古", String.valueOf(28));
            provinceList2.put("新疆", String.valueOf(29));
            provinceList2.put("黑龙江", String.valueOf(30));
            provinceList2.put("西藏", String.valueOf(31));
        }
        return provinceList2;
    }

    public static Hashtable getCityList() {
        if (cityList == null) {
            cityList = new Hashtable();
        }
        return cityList;
    }
}

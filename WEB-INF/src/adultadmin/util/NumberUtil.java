/*
 * Created on 2005-7-28
 *
 */
package adultadmin.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

import adultadmin.action.vo.voOrder;

/**
 * @author bomb
 *  
 */
public class NumberUtil {

    private static DecimalFormat dfDiscount = new DecimalFormat("0.0");

    private static DecimalFormat dfprice = new DecimalFormat("0.##");

    private static DecimalFormat priceOrder = new DecimalFormat("0.00");
    
    private static DecimalFormat threedfprice = new DecimalFormat("0.###");
    
    static public String priceOrder(float number){
    	if(number==0)
    		return "0";	
    	return priceOrder.format(number);
    }
    
    static public String priceOrder(double number){
    	if(number==0)
    		return "0";	
    	return priceOrder.format(number);
    }
    
    static public String priceOrderZero(float number){
    	if(number==0)
    		return "0.00";	
    	return priceOrder.format(number);
    }
    
    static public String priceOrderZero(double number){
    	if(number==0)
    		return "0.00";	
    	return priceOrder.format(number);
    }
    /**
     * 
     * 功能:需求不同 为整数时 不显示 .00 格式
     * <p>作者 李双 Jul 25, 2011 5:16:23 PM
     * @param number
     * @return
     */
    static public String priceOrder1(double number){
    	if(number==0)
    		return "0";
    	
    	String str = String.valueOf(number);
    	String[] split = str.split("\\.");
    	if(split.length>1){
	    	if(split[split.length-1].equals("00")||split[split.length-1].equals("0")){
	    		return split[0];
	    	}
    	}else{
    		return str;
    	}
    	
    	
    	return priceOrder.format(number);
    }
    
     public static String discount(float number) {

        return dfDiscount.format(number);
    }


    /**
     * 
     * 作者：张陶
     * 
     * 创建日期：2008-11-3
     * 
     * 说明：自动计算订单的折扣
     * 
     * 参数及返回值说明：
     * 
     * @param order
     * @param totalPrice
     * @return
     */
    public static float calDiscount(voOrder order, float totalPrice) {
    	if(order.getBuyMode() == 1 //邮购订单
            	&& (((order.getAreano() == 1 || order.getAreano() == 2 || order.getAreano() == 3) && totalPrice > 50) //广州/广东其他/北京三个地区，额度大于50元 
            		|| (order.getAreano() == 0 && totalPrice >= 50) //其他地区，额度大于50元
            	)
        ){
           	//return 0.95f;//自动打95折
    		return 1f; //不再打折
        } else {
        	return 1f;
        }
    }

    static public String price(float number) {

        return dfprice.format(number);
    }

    static public String threePrice(float number) {

        return threedfprice.format(number);
    }
    
    public static float getPriceChange() { // 返回随机的小于1的小数
        Random rand = new Random();
        return (float) ((int) (100 * rand.nextFloat())) / 100;
    }

    public static String div(float a, float b) {
        if (b < 0.00001f)
            return "0";
        return dfprice.format(a / b);
    }
    
    public static float percentDiv(float a,float b){
    	 if (b < 0.00001f)
             return 0f;
    	 return a/b;
    }

    public static int sum(Object one, Object two){
    	int o = 0;
    	int t = 0;
    	if(one != null){
    		try{
    			o = ((Integer)one).intValue();
    		} catch(ClassCastException e){
    		}
    	}
    	if(two != null){
    		try{
    			t = ((Integer)two).intValue();
    		} catch(ClassCastException e){
    		}
    	}
    	return o + t;
    }

    public static int ceiling(int i, int j){
    	int result = i/j;
    	int l = i%j;
    	if(l > 0){
    		result += 1;
    	}
    	return result;
    }

    public static double toDouble(double d, int c){
    	BigDecimal db = new BigDecimal(d);
    	BigDecimal db2 = db.setScale(c, BigDecimal.ROUND_HALF_UP);
    	d = db2.doubleValue();
    	return d;
    }

    public static float toFloat(float d, int c){
    	BigDecimal db = new BigDecimal(d);
    	BigDecimal db2 = db.setScale(c, BigDecimal.ROUND_HALF_UP);
    	d = db2.floatValue();
    	return d;
    }

    public static float parseFloat(String s){
    	float f = 0;
    	try{
    		f = Float.parseFloat(s);
    	}catch (Exception e) {
			return f;
		}
    	return f;
    }
    
    //浮点数转换为字符串。不带后面的.
    public static String parseFloatToStr(float f){
    	if(f==0.0) return "0";
    	String s = String.valueOf(f);
    	s = s.replaceAll("\\.\\d?", "");
    	
    	return s;
    }
    
    static Random random = new Random();
    public static int getRandomInt(int rang){
    	return random.nextInt(rang);
    }
    
    public static int compare(int n1, int n2){
    	int result = 1;
    	if(n1 == n2){
    		result = 0;
    	}else if(n1 < n2){
    		result = -1;
    	}
    	return result;
    }
    
    /**
	 * 
	 * 功能:生成一定长度随机密码  密码包括数字和字母 
	 * <p>作者 李双 Mar 11, 2013 4:41:46 PM
	 * @param size
	 * @return
	 */
	public static String generatePassWord(int size){
		String[] s = new String[size];
		int tt=0;
		while(true){
			int t = getRandomInt(size);
			if(s[t]==null || s[t].equals("")){
				if(getRandomInt(10)%2==0){
					s[t] = String.valueOf(getRandomLowChar());
				}else{
					s[t]=String.valueOf(getRandomInt(10));
				}
				tt++;
			}else{
				if(tt==size){
					StringBuilder sb = new StringBuilder();
					for(int i=0;i<s.length;i++){
						sb.append(s[i]);
						if(s[i]==null)
							break;
						if(i==s.length-1){
							return sb.toString();
						}
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * 功能:获取随机的小写字符[a-z]
	 * <p>作者 李双 Mar 11, 2013 4:41:29 PM
	 * @return
	 */
	public static char getRandomLowChar(){
		return (char) (97 + (int) (Math.random() * 26));
	}
}

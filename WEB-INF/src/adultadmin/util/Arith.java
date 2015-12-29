package adultadmin.util;

import java.math.BigDecimal;
/**
 * 作者：史明松
 * 
 * 创建日期：2009-11-12
 * 
 * 说明：
 * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精
 * 确的浮点数运算，包括加减乘除和四舍五入。
 */
public class Arith{
    //默认除法运算精度
    private static final int DEF_DIV_SCALE = 10;
    //这个类不能实例化
    private Arith(){
    }
 
    /**
     * 提供精确加法运算。
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static float add(float v1,float v2){
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.add(b2).floatValue();
    }
    
    /**
     * 提供精确加法运算。
     * @param v1 被加数
     * @param v2 加数
     * @return 两个参数的和
     */
    public static double add(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }
    /**
     *  提供精确加法运算。
     * @param v
     * @return 参数的和
     */
    public static float add(float[] v){
    	float temp=0; 	
    	for(int i=0;i<v.length;i++){
    		BigDecimal a = new BigDecimal(Float.toString(v[i]));
    		BigDecimal b= new BigDecimal(Float.toString(temp));
    		temp=b.add(a).floatValue();
    	}
        return temp;
    }
    
    /**
     *  提供精确加法运算。
     * @param v
     * @return 参数的和
     */
    public static double add(double[] v){
    	double temp=0; 	
    	for(int i=0;i<v.length;i++){
    		BigDecimal a = new BigDecimal(Double.toString(v[i]));
    		BigDecimal b= new BigDecimal(Double.toString(temp));
    		temp=b.add(a).doubleValue();
    	}
        return temp;
    }
    /**
     * 提供精确减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static float sub(float v1,float v2){
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.subtract(b2).floatValue();
    } 
    
    /**
     * 提供精确减法运算。
     * @param v1 被减数
     * @param v2 减数
     * @return 两个参数的差
     */
    public static double sub(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }
    /**
     * 提供精确减法运算。
     * @param v[0] 被减数
     * @param v[1] 之后的元素为减数
     * @return
     */
    public static float sub(float[] v){
    	float temp=v[0]; 	
    	for(int i=1;i<v.length;i++){
    		BigDecimal a = new BigDecimal(Float.toString(v[i]));
    		BigDecimal b= new BigDecimal(Float.toString(temp));
    		temp=b.subtract(a).floatValue();
    	}
        return temp;
    }
    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static float mul(float v1,float v2){
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.multiply(b2).floatValue();
    }
    /**
     * 提供精确的乘法运算。
     * @param v1 被乘数
     * @param v2 乘数
     * @return 两个参数的积
     */
    public static double mul(double v1,double v2){
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }
 
    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static float div(float v1,float v2){
        return div(v1,v2,DEF_DIV_SCALE);
    }
    
    /**
     * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
     * 小数点以后10位，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @return 两个参数的商
     */
    public static double div(double v1,double v2){
        return div(v1,v2,DEF_DIV_SCALE);
    }
 
    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static float div(float v1,float v2,int scale){
    	if(v2==0){
    		return 0;
    	}
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Float.toString(v1));
        BigDecimal b2 = new BigDecimal(Float.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).floatValue();
    }
    
    /**
     * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
     * 定精度，以后的数字四舍五入。
     * @param v1 被除数
     * @param v2 除数
     * @param scale 表示表示需要精确到小数点以后几位。
     * @return 两个参数的商
     */
    public static double div(double v1,double v2,int scale){
    	if(v2==0){
    		return 0;
    	}
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    }
 
    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static float round(float v,int scale){
        if(scale<0){
            throw new IllegalArgumentException(
                "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Float.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).floatValue();
    }
    
    /**
     * 提供精确的小数位四舍五入处理。
     * @param v 需要四舍五入的数字
     * @param scale 小数点后保留几位
     * @return 四舍五入后的结果
     */
    public static double round(double v,int scale){
    	if(scale<0){
    		 throw new IllegalArgumentException(
             "The scale must be a positive integer or zero");
    	}
    	 BigDecimal b = new BigDecimal(Double.toString(v));
         BigDecimal one = new BigDecimal("1");
         return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
    	
    }
//    public static void main(String[] args){
//    	//float a=Arith.sub(new float[] {(float) 0.666,(float)0.222,(float)0.3331});
//
//    	System.out.println("==>"+Arith.div(0.7f, 0.3f));
//    }
}

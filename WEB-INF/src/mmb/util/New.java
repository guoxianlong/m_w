package mmb.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.formula.functions.T;


/**
 * 
 *  <code>New.java</code>
 *  <p>功能:泛型new简单话
 *  
 *  <p>Copyright 商机无限 2013 All right reserved.
 *  @author 李双 lishuang@ebinf.com 时间 May 13, 2013 9:49:14 AM	
 *  @version 1.0 
 *  </br>最后修改人 无
 */
public class New {
	
	public static <K,V>  Map<K,V> map(){
		return new HashMap<K,V>();
	}
	
	public static <T> List<T> list(){
		return new ArrayList<T>();
	}
  	
}

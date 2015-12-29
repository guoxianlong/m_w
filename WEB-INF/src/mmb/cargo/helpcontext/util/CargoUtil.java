package mmb.cargo.helpcontext.util;

import java.util.UUID;

public class CargoUtil {
	
	public static String uuid(){		
		
		String s = UUID.randomUUID()+"";
						
		return s.substring(0, s.indexOf("-"));
				
	}
   
}

package cache;

import java.util.ArrayList;
import java.util.List;
import adultadmin.service.ServiceFactory;
import adultadmin.service.infc.IBaseService;
import adultadmin.service.infc.ICargoService;

/**
 * 货位作业单流程的缓存
 *
 */
public class CargoOperationProcessCache {
	
	public static List processList;
	static{
		init();
	}
	
	public static void init() {
		ICargoService service = ServiceFactory.createCargoService(
				IBaseService.CONN_IN_SERVICE, null);
		try {
			processList=new ArrayList();
			processList=service.getCargoOperationProcessList("1=1", -1, -1, null);
		}catch (Exception e) {
			e.printStackTrace();
		} finally {
			service.releaseAll();
		}
	}
}

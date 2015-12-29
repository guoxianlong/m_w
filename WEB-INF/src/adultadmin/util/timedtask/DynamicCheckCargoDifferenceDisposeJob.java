package adultadmin.util.timedtask;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mmb.dcheck.dao.DynamicCheckCargoDifferenceBeanDao;
import mmb.dcheck.dao.mappers.DynamicCheckCargoDifferenceBeanMapper;
import mmb.dcheck.model.DynamicCheckCargoDifferenceBean;
import mmb.dcheck.service.DCheckDisposeService;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import adultadmin.action.vo.voUser;
import adultadmin.util.MyRuntimeException;

import com.mmb.framework.support.SpringHandler;

public class DynamicCheckCargoDifferenceDisposeJob implements Job  {

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		System.out.println("盘点差异量平账定时任务开始！");
		DCheckDisposeService service = SpringHandler.getBean(DCheckDisposeService.class);
		DynamicCheckCargoDifferenceBeanDao dccdbd = SpringHandler.getBean(DynamicCheckCargoDifferenceBeanMapper.class);
		
		List<DynamicCheckCargoDifferenceBean> list = dccdbd.selectList(" status= "+DynamicCheckCargoDifferenceBean.STATUS1, -1, -1, "area_id asc");
		Map<Integer, String > areaMap = new HashMap<Integer,String>();
		for( DynamicCheckCargoDifferenceBean dccdb: list ) {
			if( !areaMap.containsKey(dccdb.getAreaId()) ) {
				areaMap.put(dccdb.getAreaId(), "");
			}
		}
		Set<Integer> keySet = areaMap.keySet();
		voUser user = new voUser();
		user.setId(0);
		user.setUsername("系统平账");
		for( Integer key : keySet ) {
			try {
				service.disposeCurrentDifferenceWithExchange(key.toString(), user);
			} catch(MyRuntimeException mre){
				System.out.println(mre.getMessage());
			}catch (ParseException e) {
				e.printStackTrace();
			}
		}
		System.out.println("盘点差异量平账定时任务结束！");
	}

}

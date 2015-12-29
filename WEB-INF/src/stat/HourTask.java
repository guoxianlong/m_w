package stat;

import java.util.Calendar;
import java.util.Date;

/**
 * @author bomb
 * 
 */
public class HourTask extends Thread {	

	Calendar lastRun = null;	// 上一次执行

	public void run() {
		
		lastRun = Calendar.getInstance();
		lastRun.set(Calendar.MINUTE, 10);
		lastRun.set(Calendar.SECOND, 0);

		
		while(true) {
			try{
				Thread.sleep(30);

				lastRun.add(Calendar.HOUR_OF_DAY, 1);		// 得到下一个运行时间
				long delay = lastRun.getTime().getTime() - new Date().getTime();

				Thread.sleep(delay);

				hourRun(lastRun);
			} catch(InterruptedException e){	// 应用关闭了
				return;
			} catch(Exception e){}
		}
	}
	
	public void hourRun(Calendar cal) {
		int hour = lastRun.get(Calendar.HOUR_OF_DAY);

		if(hour == 1){
			//不再处理积分
		    //PointImporter.importPoint();
//		    StatImporter.importProductStock(null);
		}		
	}
}

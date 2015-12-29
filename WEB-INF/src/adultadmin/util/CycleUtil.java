package adultadmin.util;

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * @author shimingsong
 * 
 */
public class CycleUtil {

	/*
	 * 来源/结算周期/结算点 计算 第几次结算 的方法 
	 * type:1.北速,广速省外 2.广州宅急送 3.广速省内
	 */
	public static int getIndex(int type, String cycle, String date) {
		String end = cycle.split("~")[1];
		int y = Integer.parseInt(end.split("-")[0]);
		int m = Integer.parseInt(end.split("-")[1]);
		int d = Integer.parseInt(end.split("-")[2]);
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();

		if (type == 1) {// 北速,广速省外
			if (m != 12)
				cal.set(y, m, d);
			if (m == 12) {
				cal.roll(cal.YEAR, 1);
				cal.set(cal.MONTH, 0);
				cal.set(cal.DAY_OF_MONTH, d);
			}
			// System.out.println("-->>>"+df.format(cal.getTime()));
			if (df.format(cal.getTime()).equals(date))
				return 1;
			// 结算周期末月+1后 大于结算点时间
			if (java.sql.Date.valueOf(df.format(cal.getTime()))
					.after(java.sql.Date.valueOf(date))) {
				int day = cal.get(cal.DAY_OF_MONTH);
				if (day == 5) {
					if (cal.get(Calendar.MONTH) == 0) {
						cal.set(Calendar.MONTH, -1);
					} else {
						cal.roll(Calendar.MONTH, -1);
					}
					cal.roll(Calendar.DAY_OF_MONTH, 20);
				} else if (day == 15) {
					cal.roll(Calendar.DAY_OF_MONTH, -10);
				} else if (day == 25) {
					cal.roll(Calendar.DAY_OF_MONTH, -10);
				}
				// System.out.println(df.format(cal.getTime()));
				if (df.format(cal.getTime()).equals(date))
					return 0;// 第0次结算
			} else if (java.sql.Date.valueOf(df.format(cal.getTime())).before(
					java.sql.Date.valueOf(date))) { // 结算周期末月+1后 小于结算点时间
				int count = 1;
				for (int i = 0; i < 8; i++) {
					int day = cal.get(cal.DAY_OF_MONTH);
					if (day < 5) {
						cal.roll(Calendar.DAY_OF_MONTH, 5 - day);
					} else if (day < 15) {
						cal.roll(Calendar.DAY_OF_MONTH, 15 - day);
					} else if (day < 25) {
						cal.roll(Calendar.DAY_OF_MONTH, 25 - day);
					} else {
						if (cal.get(Calendar.MONTH) == 11) {
							cal.roll(Calendar.YEAR, 1);
						}
						cal.roll(Calendar.MONTH, 1);
						cal.set(Calendar.DAY_OF_MONTH, 5);
					}
					// System.out.println("000-"+df.format(cal.getTime()));
					String d1 = df.format(cal.getTime());
					count++;
					if (d1.equals(date)) {
						return count;
					}
				}
			}
		}

		if (type == 2) {// 广州宅急送
			if (d != 30 || d != 28 | d != 31)
				cal.set(y, m, d);
			if (d == 30 || d == 28 || d == 31) {
				cal.set(y, m - 1, d);
				if (cal.get(Calendar.MONTH) == 11) {
					cal.roll(Calendar.YEAR, 1);
					cal.set(Calendar.DAY_OF_MONTH, cal
							.getActualMaximum(cal.DAY_OF_MONTH));
				}
				cal.roll(Calendar.MONTH, 1);
				cal.set(Calendar.DAY_OF_MONTH, cal
						.getActualMaximum(cal.DAY_OF_MONTH));
			}
			if (df.format(cal.getTime()).equals(date))
				return 1;
			if (java.sql.Date.valueOf(df.format(cal.getTime()))
					.after(java.sql.Date.valueOf(date))) {
				int day = cal.get(cal.DAY_OF_MONTH);
				if (day == 10) {
					if (cal.get(Calendar.MONTH) == 0) {
						cal.set(Calendar.MONTH, -1);
					} else {
						cal.roll(Calendar.MONTH, -1);
					}
					cal.set(Calendar.DAY_OF_MONTH, cal
							.getActualMaximum(cal.DAY_OF_MONTH));
				} else if (day == 20) {
					cal.roll(Calendar.DAY_OF_MONTH, -10);
				} else if (day == 28 || day == 30 || day == 31) {
					cal.set(Calendar.DAY_OF_MONTH, 20);
				}
				//System.out.println(df.format(cal.getTime()));
				if (df.format(cal.getTime()).equals(date))
					return 0;// 第0次结算
			}
			if (java.sql.Date.valueOf(df.format(cal.getTime())).before(
					java.sql.Date.valueOf(date))) {
				// System.out.println("---"+day);
				int count = 1;
				for (int i = 0; i < 8; i++) {
					int day = cal.get(cal.DAY_OF_MONTH);
					if (day == 10) {
						cal.roll(Calendar.DAY_OF_MONTH, 10);
					} else if (day == 20) {
						int num = cal.getActualMaximum(cal.DAY_OF_MONTH);
						cal.roll(Calendar.DAY_OF_MONTH, num - 20);
					} else if (day == 28 || day == 30 || day == 31) {
						if (cal.get(Calendar.MONTH) == 11) {
							cal.roll(Calendar.YEAR, 1);
							cal.roll(Calendar.MONTH, 1);
							cal.set(Calendar.DAY_OF_MONTH, 10);
						} else {
							cal.roll(Calendar.MONTH, 1);
							cal.set(Calendar.DAY_OF_MONTH, 10);
						}
					}
					count++;
					if (df.format(cal.getTime()).equals(date))
						return count;
					// System.out.println(df.format(cal.getTime())+"==");
				}
			}
		}

		if (type == 3) {// 广速省内
			cal.set(y, m - 1, d);
			int count = -1;
			for (int i = 0; i < 22; i++) {
				cal.add(cal.DAY_OF_YEAR, -2);
				count++;
				//System.out.println(df.format(cal.getTime()) + "---");
				if (df.format(cal.getTime()).equals(date))
					return count;
				cal.add(cal.DAY_OF_YEAR, 9);
			}

		}
		return -1;
	}

	/*
	 * 根据所导入订单的出货时间与结算来源,确定其所属时间周期,返回该时间周期
	 */
	public static Date[] getBalanceCycle(Date soDate, int balanceType) {
		Date[] result = new Date[2];

		Calendar c = Calendar.getInstance();
		c.setTime(soDate);

		if(balanceType == 1 || balanceType == 4){
			//选择北速，广速省外时，结算周期固定为每月6日~15日；B、16日~25日；C、26日~次月5日；5号、15号、25号是结算时间点。
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			if(day >= 6 && day <= 15){
				// 结算周期的起始时间、结束时间是本月的 6号 和 15号
				c.set(Calendar.DAY_OF_MONTH, 6);
				result[0] = new Date(c.getTimeInMillis());
				c.set(Calendar.DAY_OF_MONTH, 15);
				result[1] = new Date(c.getTimeInMillis());
			} else if(day >= 16 && day <= 25){
				// 结算周期的起始时间、结束时间是 本月的 16号 和 25号
				c.set(Calendar.DAY_OF_MONTH, 16);
				result[0] = new Date(c.getTimeInMillis());
				c.set(Calendar.DAY_OF_MONTH, 25);
				result[1] = new Date(c.getTimeInMillis());
			} else if(day >= 26) {
				// 结算周期的起始时间、结束时间是 本月的 26号 和 下个月的 5号
				c.set(Calendar.DAY_OF_MONTH, 26);
				result[0] = new Date(c.getTimeInMillis());
				if(month != 12){
					c.set(Calendar.MONTH, month + 1);
					c.set(Calendar.DAY_OF_MONTH, 5);
				} else {
					c.set(Calendar.YEAR, year + 1);
					c.set(Calendar.MONTH, 1);
					c.set(Calendar.DAY_OF_MONTH, 5);
				}
				result[1] = new Date(c.getTimeInMillis());
			} else if(day <= 5) {
				// 结算周期的起始时间、结束时间是 上个月的 26号 和 本月的 5号

				//先计算本月 5号
				c.set(Calendar.DAY_OF_MONTH, 5);
				result[1] = new Date(c.getTimeInMillis());

				//再计算 上个月 26号
				if(month != 1){
					c.set(Calendar.MONTH, month - 1);
					c.set(Calendar.DAY_OF_MONTH, 26);
				} else {
					c.set(Calendar.YEAR, year - 1);
					c.set(Calendar.MONTH, 12);
					c.set(Calendar.DAY_OF_MONTH, 26);
				}
				result[0] = new Date(c.getTimeInMillis());
			}
		} else if(balanceType == 2){
			//选择广速省内时，时间周期固定为每周一至周日，周五为结算时间。
			int day = c.get(Calendar.DAY_OF_WEEK);
			c.set(Calendar.DAY_OF_WEEK, 2);
			result[0] = new Date(c.getTimeInMillis());
			c.add(Calendar.DAY_OF_YEAR, 6);
			result[1] = new Date(c.getTimeInMillis());
		} else if(balanceType == 3){
			//选择广州宅急送时，时间周期固定为每月1日~10日，11日~20日，21日~当月最后一天，10号、20号和当月最后一天是结算时间。
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			if(day >= 1 && day <= 10) {
				c.set(Calendar.DAY_OF_MONTH, 1);
				result[0] = new Date(c.getTimeInMillis());
				c.set(Calendar.DAY_OF_MONTH, 10);
				result[1] = new Date(c.getTimeInMillis());
			} else if(day >= 11 && day<= 20) {
				c.set(Calendar.DAY_OF_MONTH, 11);
				result[0] = new Date(c.getTimeInMillis());
				c.set(Calendar.DAY_OF_MONTH, 20);
				result[1] = new Date(c.getTimeInMillis());
			} else if(day >= 21){
				c.set(Calendar.DAY_OF_MONTH, 21);
				result[0] = new Date(c.getTimeInMillis());
				c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
				result[1] = new Date(c.getTimeInMillis());
			}
		}

		return result;
	}

	/**
	 * 
	 * 作者：张陶
	 * 
	 * 创建日期：2009-9-25
	 * 
	 * 说明：根据所选时间点,查询出所属周期,从这些时间周期中取最接近当天的时间周期,向前推8个时间周期,返回时间周期集合
	 * 
	 * 参数及返回值说明：
	 * 
	 * @param soDate	一个周期的结束日期
	 * @param balanceType
	 * @param cycleCount
	 * @return
	 */
	public static List getCycleList(Date soDate, int balanceType, int cycleCount) {
		List result = new ArrayList();

		if(cycleCount <= 0){
			cycleCount = 8;
		}
		// 结算时间点
		Calendar c = Calendar.getInstance();
		c.setTime(soDate);
		if(balanceType == 1 || balanceType == 4){
			//选择北速，广速省外时
			for(int i=0; i<cycleCount; i++){
				Date[] cycle = getBalanceCycle(soDate, balanceType);
				result.add(DateUtil.formatDate(cycle[0]) + "~" + DateUtil.formatDate(cycle[1]));
				c.setTime(cycle[0]);
				c.add(Calendar.DAY_OF_YEAR, -1);
				soDate = c.getTime();
			}
		} else if(balanceType == 2){
			//选择广速省内时
			for(int i=0; i<cycleCount; i++){
				Date[] cycle = getBalanceCycle(soDate, balanceType);
				result.add(DateUtil.formatDate(cycle[0]) + "~" + DateUtil.formatDate(cycle[1]));
				c.setTime(cycle[0]);
				c.add(Calendar.DAY_OF_YEAR, -1);
				soDate = c.getTime();
			}
		} else if(balanceType == 3){
			//选择广州宅急送。
			for(int i=0; i<cycleCount; i++){
				Date[] cycle = getBalanceCycle(soDate, balanceType);
				result.add(DateUtil.formatDate(cycle[0]) + "~" + DateUtil.formatDate(cycle[1]));
				c.setTime(cycle[0]);
				c.add(Calendar.DAY_OF_YEAR, -1);
				soDate = c.getTime();
			}
		}
		return result;
	}

	public static boolean isBalanceDate(Date date, int balanceType){
		boolean result = false;
		// 结算时间点
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if(balanceType == 1 || balanceType == 4){
			//选择北速，广速省外时
			int day = c.get(Calendar.DAY_OF_MONTH);
			if(day == 5 || day == 15 || day == 25){
				result = true;
			}
		} else if(balanceType == 2){
			//选择广速省内时
			int day = c.get(Calendar.DAY_OF_WEEK);
			// 周日为 1， 周五为6
			if(day == 6){
				result = true;
			}
		} else if(balanceType == 3){
			//选择广州宅急送。
			int day = c.get(Calendar.DAY_OF_MONTH);
			if(day == 10 || day == 20 || day == c.getMaximum(Calendar.DAY_OF_MONTH)){
				result = true;
			}
		}
		return result;
	}

	public static void main(String[] args){
//		Date[] cycle;
//		cycle = CycleUtil.getBalanceCycle("2009-08-05", 3);
//		System.out.println(DateUtil.formatDate(cycle[0]) + "~" + DateUtil.formatDate(cycle[1]));
//		cycle = CycleUtil.getBalanceCycle("2009-08-15", 3);
//		System.out.println(DateUtil.formatDate(cycle[0]) + "~" + DateUtil.formatDate(cycle[1]));
//		cycle = CycleUtil.getBalanceCycle("2009-08-25", 3);
//		System.out.println(DateUtil.formatDate(cycle[0]) + "~" + DateUtil.formatDate(cycle[1]));
//		cycle = CycleUtil.getBalanceCycle("2009-08-31", 3);
//		System.out.println(DateUtil.formatDate(cycle[0]) + "~" + DateUtil.formatDate(cycle[1]));
//		cycle = CycleUtil.getBalanceCycle("2009-09-28", 3);
//		System.out.println(DateUtil.formatDate(cycle[0]) + "~" + DateUtil.formatDate(cycle[1]));
		System.out.println("--->"
				+ getIndex(2, "2009-01-01~2009-12-20", "2010-01-10"));
	}
}

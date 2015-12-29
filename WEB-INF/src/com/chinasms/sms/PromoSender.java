package com.chinasms.sms;

import java.util.Random;

import adultadmin.util.SmsSender;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Company: 买卖宝
 * </p>
 * 
 * @author 姚兰
 * @version 1.0 Feb 15, 2012
 */

public class PromoSender {
	public static int PROMOTION_POWER = 427;
	public static final int ANSWER_RIGHT = 1;
	public static final int ANSWER_FALSE = 2;
	public static final int ANSWER_REPEAT = 3;
	public static final int APPLY_BY_WORKER = 4;
	public static final int RE_SEND = 5;

	public static String getRanStr() {
		String s_rand = "";
		Random random = new Random();
		for (int i = 0; i < 4; i++) {
			String rand = String.valueOf(random.nextInt(10));
			s_rand += rand;
		}
		return s_rand;
	}

	public static boolean sendDmMessOne(String dst, String code, int SMStype) {
		StringBuilder buf = new StringBuilder();
		try {
			buf.delete(0, buf.length());
			String msg = "";
			switch (SMStype) {
			case ANSWER_RIGHT:

				msg = "恭喜您答对了，领奖码" + code
						+ "，凭码在活动现场可领取奖品一份。买卖宝正品折扣商城，全国货到付款，保真包邮。q.mmb.cn";
				break;
			case ANSWER_FALSE:
				msg = "正确答案为三处，为感谢您参与，增礼品一份，领奖码" + code
						+ "，凭码活动现场可领取奖品。买卖宝正品折扣商城，全国货到付款包邮。q.mmb.cn";
				break;
			case ANSWER_REPEAT:
				msg = "您好，感谢您对买卖宝活动的支持，您的手机号码已经成功申领过礼品。q.mmb.cn";
				break;
			case APPLY_BY_WORKER:
			case RE_SEND:
				msg = "恭喜您的礼品申领通过，领奖码" + code
						+ "。买卖宝正品折扣商城，每天都有超低价，全国货到付款，正品保真包邮q.mmb.cn";
				break;
			}
			boolean res = SmsSender.sendSMS(dst, msg, 52);
			if (res) {
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
			return false;
		} finally {
		}
	}
}

package adultadmin.util;

import adultadmin.util.db.DbOperation;

public class SmsSender {

	// 新的短信发送通道，需要填写子通道号
	public static boolean sendSMS(String phone, String content, int sub){
		return sendSMS(phone, content, "shortmessage" + sub, 0);
	}
	
	// userId仅用于日志记录
	public static boolean sendSMS(String phone, String content, int sub, int userId){
		return sendSMS(phone, content, "shortmessage" + sub, userId);
	}
	
	public static boolean sendSMS(String phone, String content, String table, int userId){
		StringBuilder buf = new StringBuilder(128);
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SMS);
		try {
			buf.append("insert into ").append(table).append(" set addtime=now(),mobile='").append(phone).append("',content='").append(content).append("'");
			boolean res = dbOp.executeUpdate(buf.toString());
			if(res){
				// 写入debug_log
				buf = new StringBuilder(128);
				buf.append("insert into debug_log set create_datetime=now(),content='").append(content).append("',user_id=").append(userId);
				dbOp.executeUpdate(buf.toString());
				
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
		    return false;
		} finally {
			dbOp.release();
		}
	}
	
	public static boolean sendSMS(String dst, String msg){
		StringBuilder buf = new StringBuilder(128);
		DbOperation dbOp = new DbOperation();
		dbOp.init(DbOperation.DB_SMS);
		try {
			buf.delete(0, buf.length());
			buf.append("insert into shortmessage set addtime=now(),mobile='").append(dst).append("',content='").append(msg).append("'");
			boolean res = dbOp.executeUpdate(buf.toString());
			if(res){
				return true;
			} else {
				return false;
			}
		} catch (Exception e) {
		    return false;
		} finally {
			dbOp.release();
		}
	}
}

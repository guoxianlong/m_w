package adultadmin.util.timedtask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mmb.stock.stat.DeliverCorpInfoBean;
import mmb.tms.model.Attachment;
import mmb.tms.service.IDeliverService;
import mmb.tms.service.IMailingBatchPackageService;
import mmb.util.Mail;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import adultadmin.action.vo.voOrder;
import adultadmin.util.DateUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

public class DeliverMailJob implements Job {
	@Autowired
	public IMailingBatchPackageService iMailingBatchPackageService;
	@Autowired
	public IDeliverService deliverService;
	private static byte[] lock = new byte[0];

	public void execute(JobExecutionContext context) throws JobExecutionException {
		synchronized (lock) {
			DbOperation dbOp = new DbOperation();
			dbOp.init("adult");
			try {
				String sql = "select id,mail from deliver_corp_info";
				ResultSet rs = dbOp.executeQuery(sql);
				List<DeliverCorpInfoBean> list = new ArrayList<DeliverCorpInfoBean>();
				while (rs.next()) {
					DeliverCorpInfoBean deliverBean = new DeliverCorpInfoBean();
					deliverBean.setId(rs.getInt("id"));
					deliverBean.setMail(rs.getString("mail"));
					list.add(deliverBean);
				}
				rs.close();
				if (list != null && list.size() > 0) {
					for (int i = 0; i < list.size(); i++) {
						DeliverCorpInfoBean bean = (DeliverCorpInfoBean)list.get(i);
						HashMap map = sendEmail(dbOp,StringUtil.cutString(DateUtil.getNow(), 0, 10), bean.getId() + "");
						if(map.get("returnInfo")=="true"){
							int  count =  Integer.valueOf(map.get("packageCodeCount")+"");
							String mailSql = "select id from deliver_mail where deliver_id="+bean.getId()+" and date='"+DateUtil.getNow().substring(0,10)+"'";
							ResultSet mailRs = dbOp.executeQuery(mailSql);
							if(mailRs.next()){
								String updateSql = "UPDATE deliver_mail SET send_time = '"+DateUtil.getNow()+"',status=1,transit_count="+count+",mail='"+bean.getMail()+"' WHERE deliver_id = "+bean.getId();
								dbOp.executeUpdate(updateSql);
							}else{
								String insertSql = "INSERT INTO deliver_mail (deliver_id, mail,date,transit_count,status,send_time) VALUES " +
										"("+bean.getId()+", '"+bean.getMail()+"','"+DateUtil.getNow().substring(0,10)+"',"+count+",1,'"+DateUtil.getNow()+"')";
								dbOp.executeUpdate(insertSql);
							}
							mailRs.close();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				dbOp.release();
			}
		}
	}

	public HashMap sendEmail(DbOperation dbOp, String transitDatetime, String deliverId) throws SQLException {
		int packageCodeCount = 0;
		HashMap map =new HashMap();
		String returnInfo = "true";
		try {
			String condition = " where b.transit_datetime between '" + transitDatetime + " 00:00:00' and '" + transitDatetime
					+ " 23:59:59' AND b.deliver='" + deliverId + "'";
			dbOp.init("adult");
			String sql = " select distinct c.mail,b.create_datetime ,a.order_code,a.total_price,a.weight,"
					+ " e.`name`,a.address,a.package_code,c.name,a.pay_type,b.store from mailing_batch_package a "
					+ " join mailing_batch b  on a.mailing_batch_id = b.id" + " join deliver_corp_info c on c.id=b.deliver"
					+ " join order_stock d on d.order_id=a.order_id" + " join user_order_package_type e on d.product_type=e.type_id";
			
			sql +=condition;
			ResultSet rs = dbOp.executeQuery(sql);
			List<Attachment> content = new ArrayList<Attachment>();
			String mail = "";
			String DeliveName = "";
			while (rs.next()) {
				packageCodeCount++;
				Attachment attachment = new Attachment();
				Timestamp ss = rs.getTimestamp("b.create_datetime");
				DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				mail = (String) rs.getString("c.mail");
				DeliveName = rs.getString("c.name");
				attachment.setCreateDatetime(sdf.format(ss));
				attachment.setOrderCode(rs.getString("a.order_code"));
				attachment.setTotalPrice(rs.getFloat("a.total_price"));
				attachment.setWeight((rs.getFloat("a.weight")));
				attachment.setName(rs.getString("e.name"));
				attachment.setAddress(rs.getString("a.address"));
				attachment.setPackageCode(rs.getString("a.package_code"));
				attachment.setDeliveName(rs.getString("c.name"));
				attachment.setPayType((rs.getInt("a.pay_type")));
				attachment.setStore(rs.getString("b.store"));
				content.add(attachment);
			}
			rs.close();

			String smtp = "mail.ebinf.com";
			String from = "mmbsystem@ebinf.com";
			String subject = transitDatetime + "_" + DeliveName + "_交接信息";
			String cont = "[系统邮件,请勿回复]";
			String username = "admin";
			String password = "admin";
			String fileName = "info.csv";// 附件名

			String url = this.getClass().getResource("").getPath();
			String filePath = url.substring(1, url.indexOf("WEB-INF")) + "WEB-INF/config/temp";
			if(content!=null && content.size()!=0 && mail!=null && !"".equals(mail)){
				CreateAttachments(filePath, fileName, content);// 创建附件
				if (Mail.send(smtp, from, mail, subject, cont, username, password, filePath +"\\"+ fileName)) {// 发送邮件
					DelAttachments(filePath, fileName);// 删除附件所在目录
				}else{
					returnInfo = "false"; 
					 
				}
			}else{
				returnInfo = "false"; 
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dbOp.release();
		}
		map.put("returnInfo", returnInfo);
		map.put("packageCodeCount", packageCodeCount);
		return map;
	}

	public void CreateAttachments(String filePath, String fileName, List data) {
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				file.mkdir();
			}

			PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file + "\\" + fileName), "gb2312"));
			out.println("序号,日期,订单号,订单金额,包裹重量,订单分类,收件人地址,包裹单号,归属物流,付款方式,发货仓");
			for (int i = 0; i < data.size(); i++) {
				Attachment a = (Attachment) data.get(i);
				out.println(a.getId() + "," + a.getCreateDatetime() + "," + a.getOrderCode() + "," + a.getTotalPrice() + "," + a.getWeight() + ","
						+ a.getName() + "," + a.getAddress() + "," + a.getPackageCode() + " " + "," + a.getDeliveName() + ","
						+ voOrder.buyModeMap.get(a.getPayType() + "") + "," + a.getStore());
			}
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void DelAttachments(String filePath, String fileName) {
		File file = new File(filePath);

		try {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isFile() && files[i].exists()) {
					files[i].delete();
				}
			}
			file.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
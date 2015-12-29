package mmb.tms.controller;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mmb.tms.model.Attachment;
import mmb.tms.model.MailingBatchPackage;
import mmb.tms.service.IMailingBatchPackageService;
import mmb.util.Mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("mailingBatchPackageController")
@Controller
public class MailingBatchPackageController {
	@Autowired
	public IMailingBatchPackageService iMailingBatchPackageService;
	private static byte[] lock = new byte[0];

	/**
	 * 
	 * 方法描述：发送邮件
	 * 
	 * @param: transitDatetime 交接时间
	 * @param: deliverId 快递公司id 创建人：敖海晨
	 * 
	 *         时间：2014-3-31
	 */
	@RequestMapping("/sendEmail")
	@ResponseBody
	public boolean sendEmail(HttpServletRequest request,HttpServletResponse response, String transitDatetime,String deliverId) {

		Map<String, String> condition = new HashMap<String, String>();
		condition.put("condition", "b.transit_datetime between '"
				+ transitDatetime + " 00:00:00' and '" + transitDatetime
				+ " 23:59:59' AND b.deliver='" + deliverId + "'");
		java.util.List<MailingBatchPackage> list = iMailingBatchPackageService
				.getMailAttachments(condition);
		List<Attachment> content = new ArrayList<Attachment>();
		String mail = "";
		String DeliveName = "";
		for (int i = 0; i < list.size(); i++) {
			Attachment attachment = new Attachment();
			Map map = (Map) list.get(i);
			Timestamp ss = (Timestamp) map.get("create_datetime");
			DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			try {
				mail = (String) map.get("mail");
				DeliveName = (String) map.get("d_name");
				attachment.setCreateDatetime(sdf.format(ss));
				attachment.setOrderCode((String) map.get("order_code"));
				attachment.setTotalPrice((Float) map.get("total_price"));
				attachment.setWeight(((Float) map.get("weight")).floatValue());
				attachment.setName((String) map.get("name"));
				attachment.setAddress((String) map.get("address"));
				attachment.setPackageCode((String) map.get("package_code"));
				attachment.setDeliveName(DeliveName);
				attachment.setPayType(((Long) map.get("pay_type")).intValue());
				attachment.setStore((String) map.get("store"));
				content.add(attachment);

				String smtp = "mail.ebinf.com";
				String from = "system@ebinf.com";
				String subject = transitDatetime + "_" + DeliveName + "_交接信息";
				String cont = "[系统邮件,请勿回复]";
				String username = "admin";
				String password = "admin";
				String fileName = "info.csv";// 附件名
				String filePath = request.getSession().getServletContext().getRealPath("/WEB-INF/config/temp/");// 临时目录

				iMailingBatchPackageService.CreateAttachments(filePath,fileName, content);// 创建附件
				if (Mail.send(smtp, from, mail, subject, cont, username,password, filePath + "\\" + fileName)) {
					iMailingBatchPackageService.DelAttachments(filePath,fileName);// 删除附件所在目录
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return false;
	}
}

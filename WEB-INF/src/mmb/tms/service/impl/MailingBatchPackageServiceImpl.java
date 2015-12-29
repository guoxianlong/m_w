package mmb.tms.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import mmb.tms.dao.MailingBatchPackageDao;
import mmb.tms.model.Attachment;
import mmb.tms.model.MailingBatchPackage;
import mmb.tms.service.IMailingBatchPackageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import adultadmin.action.vo.voOrder;

@Service
public class MailingBatchPackageServiceImpl implements IMailingBatchPackageService {
	@Autowired
	private MailingBatchPackageDao mailingBatchPackageDao;
	
	@Override
	public List<MailingBatchPackage> getMailAttachments(Map<String, String> condition) {
		
		return  mailingBatchPackageDao.getMailAttachments(condition);
	}
	/**
	 * 
	 *方法描述：生成附件、目录
	 *
	 *创建人：敖海晨
	 *
	 * 时间：2014-3-31
	 */
	@Override
	public void CreateAttachments(String filePath, String fileName, List data) {
		PrintWriter out =null;
		try {
			File file = new File(filePath);
			if(!file.exists()){
				file.mkdir();
			}

			out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file+"\\"+fileName), "gb2312"));
			out.println("序号,日期,订单号,订单金额,包裹重量,订单分类,收件人地址,包裹单号,归属物流,付款方式,发货仓");
			for(int i = 0 ; i < data.size(); i++){
				Attachment a =(Attachment) data.get(i);
				out.println(a.getId()+","+a.getCreateDatetime()+","+a.getOrderCode()+","+a.getTotalPrice()+","+a.getWeight()+","+a.getName()+","+a.getAddress()+","+a.getPackageCode()+" "+","+a.getDeliveName()+","+voOrder.buyModeMap.get(a.getPayType()+"")+","+a.getStore());
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(out!=null){
				out.flush();
				out.close();
			}
		}
		
	}
	/**
	 * 
	 *方法描述：删除附件所在目录、文件
	 *
	 *创建人：敖海晨
	 *
	 * 时间：2014-3-31
	 */
	@Override
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

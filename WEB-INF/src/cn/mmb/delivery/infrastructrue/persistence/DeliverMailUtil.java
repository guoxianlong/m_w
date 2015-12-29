package cn.mmb.delivery.infrastructrue.persistence;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mmb.util.Base64;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;


import adultadmin.util.HttpKit;
import adultadmin.util.MD5Util;
import cn.mmb.delivery.domain.model.dao.YdMailingDao;

public class DeliverMailUtil {

	/**
	*获取韵达接口面单信息：
	*
	*url:请求接口的地址
	*partnerid:韵达指定的合作商id
	* password:韵达指定的密码
	* version:韵达指定版本号
	* request:请求类型
	* xmldata：请求数据，格式为：Base64（xmldata）
	* validation：校验，格式为：URL编码（Md5加密（xmldata+partnerid+password））
	* @author ahc
	 * @throws UnsupportedEncodingException 接口异常
	*/
	public static String getYdInterface(String url,String partnerid,String password,String version,String request,String xmldata) throws UnsupportedEncodingException {
		String xml ="";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("partnerid",partnerid);
		params.put("version",version);
		params.put("request",request);
		params.put("xmldata",Base64.getBASE64(xmldata));
		params.put("validation",MD5Util.getKeyedDigest(Base64.getBASE64(xmldata)+partnerid+password, ""));
		xml = HttpKit.post(url, params);
		return xml;
	}
	
	/**
	 * 保存韵达接口返回的快递单信息
	* @Description: 
	* @author ahc
	 * @throws DocumentException 解析异常
	 */
	public static List addYunDaInfo(String xml) throws DocumentException{
		
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		Iterator<?> iter = root.elementIterator("response");
		List<YdMailingDao> list = new ArrayList<YdMailingDao>();
		// 遍历responses节点  
        while (iter.hasNext()) {
        	YdMailingDao ydMailingBean = new YdMailingDao();
            Element recordEle = (Element) iter.next();  
            // 拿到response节点下的所有子节点值  
            //String id = recordEle.elementTextTrim("id");
            ydMailingBean.setId(recordEle.elementTextTrim("id"));
            ydMailingBean.setStatus(recordEle.elementTextTrim("status"));
            ydMailingBean.setReach(recordEle.elementTextTrim("reach"));
            ydMailingBean.setDistricenterCode(recordEle.elementTextTrim("districenter_code"));
            ydMailingBean.setDistricenterName(recordEle.elementTextTrim("districenter_name"));
            ydMailingBean.setBigpenCode(recordEle.elementTextTrim("bigpen_code"));
            ydMailingBean.setPosition(recordEle.elementTextTrim("position"));
            ydMailingBean.setPositionNo(recordEle.elementTextTrim("position_no"));
            ydMailingBean.setOneCode(recordEle.elementTextTrim("one_code"));
            ydMailingBean.setTwoCode(recordEle.elementTextTrim("two_code"));
            ydMailingBean.setThreeCode(recordEle.elementTextTrim("three_code"));
            ydMailingBean.setPadMailno(recordEle.elementTextTrim("pad_mailno"));
            ydMailingBean.setMsg(recordEle.elementTextTrim("msg"));
            list.add(ydMailingBean);         
        }
		return list;
	}
}

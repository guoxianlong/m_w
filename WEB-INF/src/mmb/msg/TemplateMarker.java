package mmb.msg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import adultadmin.util.Constants;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class TemplateMarker {
	private static Logger logger = Logger.getLogger(TemplateMarker.class);
    private static  TemplateMarker tm = new TemplateMarker();
	private static  Configuration cfg =null;
	
	public static String MESSAGE_PATH="";
	public static String SENT_OUT_MESSAGE_NAME="";
	public static String SENT_OUT_DAQ_MESSAGE_NAME="";
	public static String SENT_OUT_AMAZON_MESSAGE_NAME="";
	public static String SENT_OUT_DANGDANG_MESSAGE_NAME="";
	public static String SENT_OUT_JD_MESSAGE_NAME="";
	public static String SENT_OUT_JD_ADULT_MESSAGE_NAME="";
	public static String EASE_MESSAGE_NAME="";
	public static String EASE_MESSAGE_DAQ_NAME = "";
	public static String EASE_MESSAGE_ADULT_NAME="";
	public static String EASE_MESSAGE_NAME2="";
	public static String DELIVER_WARNING_NAME="" ;
	public static String BACK_USER_PRODUCT_MESSAGE_NAME="";
	public static String BACK_SUPPLIER_MESSAGE_NAME = "";
	public static String ORDER_STOCK_COUNT_MESSAGE_NAME = "";
	public static String DETECT_PACKAGE_MESSAGE_NAME="";
	public static String REPAIR_PRODUCT_MESSAGE_NAME="";
	public static String PRIZE_MESSAGE_NAME="";
	
	private TemplateMarker() {
    }

	public static TemplateMarker getMarker() throws TemplateException, IOException{
	    return tm;
	}
	/**
	 * 初始化 Configuration
	 * 
	 * @param templatePath 加载模板路径
	 * @throws TemplateException 模板异常
	 * @throws IOException IO处理异常
	 */
	public void initCfg(final String templatePath) throws TemplateException, IOException{
		initPath();
	    cfg = new Configuration();
        cfg.setSetting(Configuration.CACHE_STORAGE_KEY, "strong:20, soft:100");
        cfg.setEncoding(Locale.CHINA, "UTF-8");   
        try {
            cfg.setDirectoryForTemplateLoading(new File(templatePath));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        cfg.setObjectWrapper(new BeansWrapper()); 
	}
	
	/**
	 * 初始化模板参数
	 */
	public void initPath(){
		MESSAGE_PATH=Constants.MESSAGE_PATH;
		SENT_OUT_MESSAGE_NAME=Constants.SENT_OUT_MESSAGE_NAME;
		SENT_OUT_DAQ_MESSAGE_NAME = Constants.SENT_OUT_DAQ_MESSAGE_NAME;
		SENT_OUT_AMAZON_MESSAGE_NAME = Constants.SENT_OUT_AMAZON_MESSAGE_NAME;
		SENT_OUT_DANGDANG_MESSAGE_NAME = Constants.SENT_OUT_DANGDANG_MESSAGE_NAME;
		SENT_OUT_JD_MESSAGE_NAME = Constants.SENT_OUT_JD_MESSAGE_NAME;
		SENT_OUT_JD_ADULT_MESSAGE_NAME = Constants.SENT_OUT_JD_ADULT_MESSAGE_NAME;
		EASE_MESSAGE_NAME=Constants.EASE_MESSAGE_NAME;
		EASE_MESSAGE_DAQ_NAME=Constants.EASE_MESSAGE_DAQ_NAME;
		EASE_MESSAGE_ADULT_NAME=Constants.EASE_MESSAGE_ADULT_NAME;
		EASE_MESSAGE_NAME2=Constants.EASE_MESSAGE_NAME2;
		DELIVER_WARNING_NAME=Constants.DELIVER_WARNING_NAME;
		BACK_USER_PRODUCT_MESSAGE_NAME = Constants.BACK_USER_PRODUCT_MESSAGE_NAME;
		BACK_SUPPLIER_MESSAGE_NAME = Constants.BACK_SUPPLIER_MESSAGE_NAME;
		ORDER_STOCK_COUNT_MESSAGE_NAME = Constants.ORDER_STOCK_COUNT_MESSAGE_NAME;
		DETECT_PACKAGE_MESSAGE_NAME=Constants.DETECT_PACKAGE_MESSAGE_NAME;
		REPAIR_PRODUCT_MESSAGE_NAME=Constants.REPAIR_PRODUCT_MESSAGE_NAME;
		PRIZE_MESSAGE_NAME=Constants.PRIZE_MESSAGE_NAME;
	}
	
	/**
	 * 
	 * 模板装配 字符
	 * 
	 * @param templateName 模板名
	 * @param paramContent 数据
	 * @return 字符 
	 * @throws TemplateException 模板装配
	 * @throws IOException IO处理异常
	 */
	public String getOutString(String templateName, Map<String,Object> paramContent){
	    StringWriter stringWriter = new StringWriter();  
	    BufferedWriter writer = new BufferedWriter(stringWriter);  
	    try{
	    Template t = cfg.getTemplate(templateName,Locale.CHINA);
        t.setEncoding("UTF-8");
        t.process(paramContent, writer);
        writer.flush();
        writer.close();
	    }catch (IOException e) {
	    	e.printStackTrace();
	        logger.error("IO异常:" + e.toString(),e);
        }catch(TemplateException e){
        	e.printStackTrace();
            logger.error("模板异常:" + e.toString(),e);
        }
	    return stringWriter.toString();
	}
	/**
	 * 模板装配输出流
	 * 
	 * @param templateName 模板名称
	 * @param paramContent 数据
	 * @param os 输出流
	 * @return 输出流
	 */
    public  OutputStream fileCreator (String templateName, Map<String,Object> paramContent,OutputStream os) {
		try {
		    OutputStreamWriter osw = new OutputStreamWriter(os);
			Template t = cfg.getTemplate(templateName,Locale.CHINA);
			t.setEncoding("UTF-8");
			t.process(paramContent, osw);
			osw.flush();
			osw.close();
		} catch (UnsupportedEncodingException e) {
			logger.error("不支持的编码:" + e.toString(),e);
		} catch (FileNotFoundException e) {
			logger.error("文件未找到:" + e.toString(),e);
		} catch (IOException e) {
			logger.error("IO异常:" + e.toString(),e);
		} catch (TemplateException e) {
			logger.error("模板异常:" + e.toString(),e);
		}
		return os;
	}
}

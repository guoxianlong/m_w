/*
 * Created on 2005-5-16
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package adultadmin.util.filter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import mmb.msg.TemplateMarker;

import org.apache.log4j.PropertyConfigurator;

import adultadmin.action.vo.voUser;
import adultadmin.framework.rpc.RPCClient;
import adultadmin.util.Constants;
import adultadmin.util.LogUtil;
import adultadmin.util.StringUtil;
import adultadmin.util.db.DbOperation;

/**
 * @author ���
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SetCharacterEncodingFilter implements Filter {

    // ----------------------------------------------------- Instance Variables

    /**
     * The default character encoding to set for requests that pass through this
     * filter.
     */
    protected String encoding = null;

    /**
     * The filter configuration object we are associated with. If this value is
     * null, this filter instance is not currently configured.
     */
    protected FilterConfig filterConfig = null;

    /**
     * Should a character encoding specified by the client be ignored?
     */
    protected boolean ignore = true;

    // --------------------------------------------------------- Public Methods

    /**
     * Take this filter out of service.
     */
    public void destroy() {

        this.encoding = null;
        this.filterConfig = null;

    }

    /**
     * Select and set (if specified) the character encoding to be used to
     * interpret request parameters for this request.
     * 
     * @param request
     *            The servlet request we are processing
     * @param result
     *            The servlet response we are creating
     * @param chain
     *            The filter chain we are processing
     * 
     * @exception IOException
     *                if an input/output error occurs
     * @exception ServletException
     *                if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        // Conditionally select and set the character encoding to be used
        if (ignore || (request.getCharacterEncoding() == null)) {
            String encoding = selectEncoding(request);
            if (encoding != null)
                request.setCharacterEncoding(encoding);
        }

        //用户访问记录
        try {
            HttpServletRequest hsr = (HttpServletRequest) request;
            String url = hsr.getRequestURL().toString();
            if (url.indexOf("GetChatUserList.do") != -1
                    || url.indexOf("ChatMessages.do") != -1) {
                chain.doFilter(request, response);
                return;
            }
            
            logRequest(hsr); 
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Pass control on to the next filter
        chain.doFilter(request, response);

    }

    public static void logRequest(HttpServletRequest request) {
		String url = request.getRequestURL().toString();
		if ((url.endsWith(".jsp") || url.endsWith(".do") || url.endsWith(".mmx")) && !url.endsWith("login.do")) {
			String qs = request.getQueryString();
			HttpSession session = request.getSession(false);
			if (qs != null) {
				url += "?" + qs;
			}
			voUser user = null;
			voUser myUser = null;
			if (session != null) {
				user = (voUser) session.getAttribute("userView");
				myUser = (voUser) session.getAttribute("myUser");
			}
            int userId = 0;
            String userName = "null";
            if (myUser != null) {
                userId = myUser.getId();
                userName = myUser.getUsername()+"("+user.getUsername()+")";
            }else if(user!=null){
            	userId = user.getId();
                userName = user.getUsername();
            }
            StringBuffer sb = new StringBuffer();
            sb.append(userName).append("\t").append(userId).append("\t").append(url).append("\t").append(adultadmin.util.IP.getRemoteAddr(request)).append("\t").append(request.getMethod());
            
            if("post".equals(request.getMethod().toLowerCase())){
                String ct = request.getContentType();
                if(ct==null || !ct.contains("multipart")){
               	 Enumeration<String> names = request.getParameterNames();
                    if(names!=null){
                    	StringBuffer sb1 = new StringBuffer();
                         while(names.hasMoreElements()){
                              String name = names.nextElement();
                              String value = request.getParameter(name);
                              if(!StringUtil.isNull(value)){
	                           	   if(name.startsWith("password")){
	                           		   sb1.append(name).append("=").append("xxxxxxxxxx").append("&");
	                           	   }else{
	                           		   sb1.append(name).append("=").append(value).append("&");
	                           	   }                         	
                              }
                         }
                         if(sb1.length()>0){
                       	  	sb.append("\t").append(sb1);
                         }
                    }
                }else{
                	sb.append("\t").append("multipart_data");
                }
                
            }
            LogUtil.logAccess(sb.toString());
        }      
    }
    
    /**
     * Place this filter into service.
     * 
     * @param filterConfig
     *            The filter configuration object
     */
    public void init(FilterConfig filterConfig) throws ServletException {

        this.filterConfig = filterConfig;
        this.encoding = filterConfig.getInitParameter("encoding");
        String value = filterConfig.getInitParameter("ignore");
        if (value == null)
            this.ignore = true;
        else if (value.equalsIgnoreCase("true"))
            this.ignore = true;
        else if (value.equalsIgnoreCase("yes"))
            this.ignore = true;
        else
            this.ignore = false;

        loadOther(filterConfig.getServletContext());
    }
    
    private static void loadOther(ServletContext context) {
		
		String conf = context.getInitParameter("conf");
		if(conf != null) {
			Constants.CONFIG_PATH = conf;
		}
		
		// 载入编译信息
		try {
			InputStream is = Constants.class.getResourceAsStream("/buildinfo");
			Constants.configCompile.load(is);
			is.close();
			String project=Constants.configCompile.getProperty("project");
			String branch=Constants.configCompile.getProperty("branch");
			String revision=Constants.configCompile.getProperty("revision");
			if(project != null) {
				System.out.print("[BUILD INFO] ");
				System.out.print(project);
				if(branch != null) {
					System.out.print(", ");
					System.out.print(branch);
				}
				if(revision != null) {
					System.out.print(", r");
					System.out.print(revision);
				}
				System.out.println(".");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			loadConfig();
		} catch (Exception e) {
			System.out.println("[ERROR]config file load failed : " + Constants.CONFIG_PATH + "conf.properties");
			e.printStackTrace();
		}

    	String upload = context.getInitParameter("upload");
		if(upload != null) {
			Constants.UPLOAD_ROOT = upload;
			Constants.UPLOAD_PRODUCT_IMAGE = upload + "productImage/";
		}
    	String rep = context.getInitParameter("rep");
		if(rep != null) {
			Constants.RESOURCE_PRODUCT_IMAGE = rep + "/upload/productImage/";
		}
	}
    
    public static void loadConfig() throws Exception {
    	if(Constants.CONFIG_PATH == null) {
    		// 默认配置文件在项目路径下
    		Constants.CONFIG_PATH = Constants.class.getResource("/").toURI().resolve("../config").getPath() + "/";
    	}

		// 修改log4j配置文件位置
		PropertyConfigurator.configure(Constants.CONFIG_PATH + "log4j.properties");
		
		FileInputStream fis;

		fis = new FileInputStream(Constants.CONFIG_PATH + "conf.properties");
		Constants.config.load(fis);
		fis.close();
		
		// 如果有配置文件，读取数据库名称
    	String schemaShop = Constants.config.getProperty("schema_shop");
		if(schemaShop != null) {
			DbOperation.SCHEMA_SHOP = schemaShop + ".";
		}
		String schemaSMS = Constants.config.getProperty("schema_sms");
		if(schemaSMS != null) {
			DbOperation.SCHEMA_SMS = schemaSMS + ".";
		}
		String schemaForum = Constants.config.getProperty("schema_forum");
		if(schemaForum != null) {
			DbOperation.SCHEMA_FORUM = schemaForum + ".";
		}
		
		String schemaWare = Constants.config.getProperty("schema_ware");
		if(schemaWare != null) {
			DbOperation.SCHEMA_WARE = schemaWare + ".";
		}
		
		
		// 如果有配置文件，以配置文件为主覆盖
    	String upload = Constants.config.getProperty("upload");
		if(upload != null) {
			Constants.UPLOAD_ROOT = upload;
			Constants.UPLOAD_PRODUCT_IMAGE = upload + "productImage/";
		}
		String upload2 = Constants.config.getProperty("upload2");
		String upload3=Constants.config.getProperty("upload3");
		if(upload3!=null){
			Constants.IMPREST_DETAIL_IMAGES_ROOT= upload3;
		}
		//物流图像配置
		String wareUpload = Constants.config.getProperty("ware.upload.local.base");
		if( wareUpload != null ) {
			Constants.WARE_UPLOAD = wareUpload;
		}
		String wareSoundError = Constants.config.getProperty("ware.sound.error");
		if( wareSoundError != null ) {
			Constants.WARE_SOUND_ERROR = wareSoundError;
		}
		String wareSoundSJYC = Constants.config.getProperty("ware.sound.sjyc");
		if( wareSoundSJYC  != null ) {
			Constants.WARE_SOUND_SJYC  = wareSoundSJYC ;
		}
		String wareSoundKXP = Constants.config.getProperty("ware.sound.kxp");
		if( wareSoundKXP != null ) {
			Constants.WARE_SOUND_KXP = wareSoundKXP;
		}
		String wareUrl = Constants.config.getProperty("ware.upload.url.base");
		if( wareUrl != null ) {
			Constants.STAFF_PHOTO_URL = wareUrl;
		}
		// 是否强制外部登录为HTTPS模式		String OUT_HTTPS = Constants.config.getProperty("OUT_HTTPS");		if (OUT_HTTPS != null && OUT_HTTPS.equalsIgnoreCase("true")) {			Constants.OUT_HTTPS = true;		}
    	String rep = Constants.config.getProperty("rep");
		if(rep != null) {
			Constants.RESOURCE_PRODUCT_IMAGE = rep + "/upload/productImage/";
			Constants.RESOURCE_ANDRIOD_IMAGE = rep + "/upload/client/";
			Constants.RESOURCE_TOUCH_IMAGE = rep + "/upload/touch/";
			Constants.RESOURCE_MEMBER_ACTIVE_IMAGE = rep + "/upload/member/active/";
		}
    	String web = Constants.config.getProperty("web");	// 用于从前台获取页面预览用
		if(web != null) {
			Constants.WEB_ROOT = web;
		}
		String rpc = Constants.config.getProperty("rpc");	// 用于调用前台清理缓存等操作
		if(rpc != null) {
			RPCClient.shopServers = rpc.split(";");
		} else {
			RPCClient.shopServers = new String[]{web};	// 兼容旧的web参数（没集群的情况下）
		}
		String adminRpc = Constants.config.getProperty("admin-rpc");	// 用于调用后台清理缓存等操作
		if(adminRpc != null) {
			RPCClient.adminShopServers = adminRpc.split(";");
		}
    	String download = Constants.config.getProperty("download");
		if(download != null) {
			Constants.DOWNLOAD_PATH = download;
		}
		//设置索引服务器地址
		String indexServer = Constants.config.getProperty("index_server");
		if(!StringUtil.isNull(indexServer)){
			Constants.INDEX_SERVER = indexServer;
		}
		//设置索引服务器端口
		String indexPort = Constants.config.getProperty("index_port");
		if(!StringUtil.isNull(indexPort)){
			Constants.INDEX_SERVER_PORT = StringUtil.toInt(indexPort);
		}
		//设置索引服务器协议
		String indexServerProtocol = Constants.config.getProperty("index_server_protocol");
		if(!StringUtil.isNull(indexServerProtocol)){
			Constants.INDEX_SERVER_PROTOCOL = indexServerProtocol;
		}
		
		// 比对商品图片上传路径
		String contrastUpload = Constants.config.getProperty("PRODUCT_CONTRAST_IMAGE_URL");
		if(contrastUpload != null){
			Constants.PRODUCT_CONTRAST_IMAGE_URL = contrastUpload;
		}
		
		// 比对商品图片上传路径
				String memcachedServers = Constants.config.getProperty("memcached.servers");
				if(memcachedServers != null){
					Constants.MEMCACHED_SERVERS = memcachedServers;
				}		
		
		// 比对短信模板路径
		String messagePath = Constants.config.getProperty("message_path");
		if(messagePath != null){
			Constants.MESSAGE_PATH = messagePath;
		}
		
		//比对发货短信模板
		String sentOutMessageName = Constants.config.getProperty("sent_out_message_name");
		if(sentOutMessageName != null){
			Constants.SENT_OUT_MESSAGE_NAME = sentOutMessageName;
		}
		//比对大Q发货短信模板
		String sentOutDaqMessageName = Constants.config.getProperty("sent_out_daq_message_name");
		if(sentOutDaqMessageName != null){
			Constants.SENT_OUT_DAQ_MESSAGE_NAME = sentOutDaqMessageName;
		}
		
		//比对亚马逊发货短信模板
		String sentOutAmazonMessageName = Constants.config.getProperty("sent_out_amazon_message_name");
		if(sentOutAmazonMessageName != null){
			Constants.SENT_OUT_AMAZON_MESSAGE_NAME = sentOutAmazonMessageName;
		}
		//比对当当发货短信模板
		String sentOutDangdangMessageName = Constants.config.getProperty("sent_out_dangdang_message_name");
		if(sentOutDangdangMessageName != null){
			Constants.SENT_OUT_DANGDANG_MESSAGE_NAME = sentOutDangdangMessageName;
		}
		//比对京东发货短信模板
		String sentOutJdMessageName = Constants.config.getProperty("sent_out_jd_message_name");
		if(sentOutJdMessageName != null){
			Constants.SENT_OUT_JD_MESSAGE_NAME = sentOutJdMessageName;
		}
		
		//比对京东（成人）发货短信模板
		String sentOutJdAdultMessageName = Constants.config.getProperty("sent_out_jd_adult_message_name");
		if(sentOutJdMessageName != null){
			Constants.SENT_OUT_JD_ADULT_MESSAGE_NAME = sentOutJdAdultMessageName;
		}
		
		//比对安心短信模板
		String easeMessageName = Constants.config.getProperty("ease_message_name");
		if(easeMessageName != null){
			Constants.EASE_MESSAGE_NAME = easeMessageName;
		}
		//比对大Q安心短信模板
		String easeMessageDaqName = Constants.config.getProperty("ease_message_daq_name");
		if(easeMessageDaqName != null){
			Constants.EASE_MESSAGE_DAQ_NAME = easeMessageDaqName;
		}
		
		//比对成人用品安心短信模板
		String easeMessageAdultName = Constants.config.getProperty("ease_message_adult_name");
		if(easeMessageAdultName != null){
			Constants.EASE_MESSAGE_ADULT_NAME = easeMessageAdultName;
		}
		
		//比对安心短信模板2
		String easeMessageName2 = Constants.config.getProperty("ease_message_name2");
		if(easeMessageName2 != null){
			Constants.EASE_MESSAGE_NAME2 = easeMessageName2;
		}
		
		//包裹单预警短信模板
		String deliverWarningName = Constants.config.getProperty("deliver_warning_name");
		if(deliverWarningName !=null){
			Constants.DELIVER_WARNING_NAME = deliverWarningName;
		}
		
		//维修商品寄回短信模板
		String bcakUserProductMessageName = Constants.config.getProperty("back_user_product_message_name");
			if(bcakUserProductMessageName != null){
				Constants.BACK_USER_PRODUCT_MESSAGE_NAME = bcakUserProductMessageName;
		}
		
		// 添加返厂模板
		String backSupplierMessage = Constants.config.getProperty("back_supplier_message_name");
		if(backSupplierMessage != null){
			Constants.BACK_SUPPLIER_MESSAGE_NAME = backSupplierMessage;
		}
			
		// 发单量定时短信模板
		String orderStockCount = Constants.config.getProperty("order_stock_count_message_name");
		if(orderStockCount != null){
			Constants.ORDER_STOCK_COUNT_MESSAGE_NAME = orderStockCount;
		}
		
		//签收包裹短信模板
		String detectPackageMessageName = Constants.config.getProperty("detect_package_message_name");
		if(detectPackageMessageName != null){
			Constants.DETECT_PACKAGE_MESSAGE_NAME = detectPackageMessageName;
		}
		
		//添加维修报价或点击可以维修操作模板名称
		String repairProductMessageName = Constants.config.getProperty("repair_product_message_name");
		if(repairProductMessageName != null){
			Constants.REPAIR_PRODUCT_MESSAGE_NAME = repairProductMessageName;
		}
		
		//评论有奖短信模板
		String prizeMessageName = Constants.config.getProperty("prize_message_name");
		if(prizeMessageName !=null){
			Constants.PRIZE_MESSAGE_NAME = prizeMessageName;
		}
		
		TemplateMarker tm =TemplateMarker.getMarker();
		tm.initCfg(Constants.MESSAGE_PATH);
			
		System.out.println("[MSG]config file loaded : " + Constants.CONFIG_PATH + "conf.properties");
    }

    // ------------------------------------------------------ Protected Methods

    /**
     * Select an appropriate character encoding to be used, based on the
     * characteristics of the current request and/or filter initialization
     * parameters. If no character encoding should be set, return
     * <code>null</code>.
     * <p>
     * The default implementation unconditionally returns the value configured
     * by the <strong>encoding </strong> initialization parameter for this
     * filter.
     * 
     * @param request
     *            The servlet request we are processing
     */
    protected String selectEncoding(ServletRequest request) {

        return (this.encoding);

    }

}

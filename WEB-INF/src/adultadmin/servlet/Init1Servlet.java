/**
 * 正式服务器。
 */
package adultadmin.servlet;

// import smscenter.util.EsmsThread;

import java.io.FileInputStream;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import adultadmin.util.Constants;
import adultadmin.util.StringUtil;
import adultadmin.util.timedtask.TimedTaskDispatcher;


/**
 * @author Bomb
 * 
 */
public class Init1Servlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3000163169526807135L;
	ServletContext servlet = null;
	TimedTaskDispatcher ttd = null;
//	HourTask task;
	public void init(ServletConfig config) throws ServletException {
		
		String timeTask = config.getServletContext().getInitParameter("timeTask");
		System.out.println("init1:"+timeTask);
		if(timeTask != null && timeTask.equalsIgnoreCase("true")){
			System.out.println("[MSG]timer task started");
			
//		    task = new HourTask();
//	        task.start();
	        
	        // Quartz 定时任务
	        ttd = TimedTaskDispatcher.getInstance();
	        ttd.start();
	        
	        NodeList list = null;
	        try {
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setIgnoringElementContentWhitespace(true);
				DocumentBuilder builder = factory.newDocumentBuilder();
				FileInputStream fis;
	
				fis = new FileInputStream(Constants.CONFIG_PATH + "jobs.xml");
				Document document = builder.parse(fis);
				fis.close();
				
				Element rootElement = document.getDocumentElement();
				if(rootElement.getNodeName().equals("jobs")) {
					list = rootElement.getElementsByTagName("job");
				}
	        } catch (Exception e) {
	        	System.out.println("[WARNING]jobs config load failed");
	        	e.printStackTrace();
	        }
	        
	        if(list != null) {
		        for(int i = 0;i < list.getLength();i++) {
		        	Node node = list.item(i);
		        	Node name = node.getAttributes().getNamedItem("name");
	        		Node class1 = node.getAttributes().getNamedItem("class");
	        		Node cron = node.getAttributes().getNamedItem("cron");
	        		Node interval = node.getAttributes().getNamedItem("interval");	// 与cron冲突，表示间隔秒数
	        		
		        	try {
		        		Class c = Class.forName(class1.getNodeValue());
		        		if(cron != null)
		        			ttd.addTimedTask(name.getNodeValue(), null, c, cron.getNodeValue());
		        		else
		        			ttd.addTimedTask(name.getNodeValue(), null, c, StringUtil.toInt(interval.getNodeValue()));
		        		System.out.println("[MSG]job " + name.getNodeValue() + " started - " + c.getName());
		        	} catch(Exception e) {
		        		System.out.println("[ERROR]job " + name.getNodeValue() + " failed");
		        		e.printStackTrace();
		        	}
		        }
	        }
	        
	        
	        // 排班定时任务
	        //TimedTaskDispatcher.getInstance().addTimedTask("NewOrderJob", "OrderDistGroup", NewOrderJob.class, "0 0/1 * * * ?");
	        // 订单分配任务
	        //TimedTaskDispatcher.getInstance().addTimedTask("OrderDispatchJob", "OrderDistGroup", OrderDispatchJob.class, "0/10 * * * * ?");
	        
	        //品牌商品销售统计
	       // TimedTaskDispatcher.getInstance().addTimedTask("OrderAcountSaleJob", "OrderAcountSaleJob", OrderAcountSaleJob.class, "0 0/30 * * * ?");
	        
//	        TimedTaskDispatcher.getInstance().addTimedTask("OrderSMSAutoJob1", "OrderSMSAutoJobGroup", OrderSMSAutoJob.class, "0 50 7 * * ?");
//	        TimedTaskDispatcher.getInstance().addTimedTask("OrderSMSAutoJob2", "OrderSMSAutoJobGroup", OrderSMSAutoJob.class, "0 50 8 * * ?");
//	        TimedTaskDispatcher.getInstance().addTimedTask("OrderSMSAutoJob3", "OrderSMSAutoJobGroup", OrderSMSAutoJob.class, "0 50 9 * * ?");

		}

		// 允许用户访问
//		AccessController.setCanAccess(true);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.GenericServlet#destroy()
	 */
	public void destroy() {

		// 停止Quartz 定时任务
		if(ttd != null)
			ttd.shutdown();
		System.out.println("[MSG]timer task stopped");

//		if(task != null)
//			task.interrupt();
		if (servlet != null) {

		}

		super.destroy();
	}

}

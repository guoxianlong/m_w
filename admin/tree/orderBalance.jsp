<%@ page contentType="text/xml;charset=utf-8" %><%@ page import="org.jdom.*" %><%@ page import="org.jdom.output.*" %><%@ page import="java.util.*" %><%@ page import="adultadmin.service.WAdminServiceImpl" %><%
	response.setHeader("Pragma", "No-cache");//HTTP 1.1
	response.setHeader("Cache-Control", "no-cache");//HTTP 1.0
	
	Map map = adultadmin.bean.OrderBalanceStatusBean.orderBalanceStatusMap;	

// 创建xml文件的根目录tree
Element rootElement = new Element("tree");
// 创建树的属性
Document myDocument = new Document(rootElement);
Iterator iter = map.entrySet().iterator();
while (iter.hasNext()) {
	Map.Entry bean = (Map.Entry) iter.next();
	Element result = new Element("tree");

	result.setAttribute("action", "balanceOrders.do?orderBalanceStatus=" + bean.getKey());
	result.setAttribute("text", bean.getValue().toString()+"订单");
	result.setAttribute("target", "mainFrame");
	rootElement.addContent(result);
}

// Output the xml将此页面转化为xml
XMLOutputter outputter = new XMLOutputter();

org.jdom.output.Format format = org.jdom.output.Format.getCompactFormat();
outputter.setFormat(format);

outputter.output(myDocument, out);
%>

<%@ page contentType="text/xml;charset=utf-8" %><%@ page import="adultadmin.action.vo.*" %><%@ page import="adultadmin.util.StringUtil" %><%@ page import="org.jdom.*" %><%@ page import="org.jdom.output.*" %><%@ page import="java.util.*" %><%-- <%@ page import="adultadmin.service.WAdminServiceImpl" %> --%><%
	response.setHeader("Pragma", "No-cache");//HTTP 1.1
	response.setHeader("Cache-Control", "no-cache");//HTTP 1.0
	
	voUser user = (voUser)session.getAttribute("userView");
	adultadmin.bean.UserGroupBean group = user.getGroup();
	
	int mode=StringUtil.toInt(request.getParameter("mode"));
	if(mode<0||mode>10)
		return;
	
	List list=new ArrayList();
	/*
	WAdminServiceImpl service = new WAdminServiceImpl();
	try {
		list = service.getSelects("user_order_status", "where visible=1 order by sec");
	} finally {
		service.close();
	}
	*/

// 创建xml文件的根目录tree
Element rootElement = new Element("tree");
// 创建树的属性
Document myDocument = new Document(rootElement);
Iterator iter = list.iterator();
while (iter.hasNext()) {
	voSelect vo = (voSelect) iter.next();
	Element result = new Element("tree");
	if(vo.getId()==0&&!group.isFlag(29)) continue;
	if(mode==0) {	// 不同类型的订单，有不同的使用状态
		if(vo.getId()!=0 && vo.getId()!=1 && vo.getId()!=2 && vo.getId()!=3 && vo.getId()!=9) continue;
	} else if(mode==1){
		if(vo.getId()!=0 && vo.getId()!=1 && vo.getId()!=2 && vo.getId()!=3 && vo.getId()!=9) continue;
	} else {
		if(vo.getId()!=0 && vo.getId()!=1 && vo.getId()!=2 && vo.getId()!=9) continue;
	}

	result.setAttribute("action", "orders.do?buymode="+mode+"&status=" + vo.getId());
	if(mode==0 && vo.getId()==3)	// 对于货到付款订单，已到款的状态其实对应的是待发货
		result.setAttribute("text", "待发货订单");
	else
		result.setAttribute("text", vo.getName()+"订单");
	result.setAttribute("target", "mainFrame");
	rootElement.addContent(result);
}

// Output the xml将此页面转化为xml
XMLOutputter outputter = new XMLOutputter();

org.jdom.output.Format format = org.jdom.output.Format.getCompactFormat();
outputter.setFormat(format);

outputter.output(myDocument, out);
%>

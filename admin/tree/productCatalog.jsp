<%@ page contentType="text/xml;charset=utf-8" %><%@ page import="adultadmin.action.vo.*" %><%@ page import="org.jdom.*" %><%@ page import="org.jdom.output.*" %><%@ page import="java.util.*" %><%@ page import="adultadmin.service.WAdminServiceImpl" %><%
	response.setHeader("Pragma", "No-cache");//HTTP 1.1
	response.setHeader("Cache-Control", "no-cache");//HTTP 1.0
	
	List list=null;
	WAdminServiceImpl service = new WAdminServiceImpl();
	try {
		list = service.getCatalogs();
	} finally {
		service.close();
	}
	

	HashMap elementMap = new HashMap(list.size());	// 用id来快速找到element
// 创建xml文件的根目录tree
Element rootElement = new Element("tree");
// 创建树的属性
Document myDocument = new Document(rootElement);

for (int i = 0; i < list.size(); i += 1) {
	voCatalog vo = (voCatalog) list.get(i);
	Element result = new Element("tree");
	// 如果不是 但是目录
	if (vo != null&&vo.getHide()==0) {

//		result.setAttribute("src", "fAjaxPoolTwo.do?type=1&parantId=" + vo.getId());// 设置src的参数
		result.setAttribute("action", "products.do?catalogId=" + vo.getId());
		result.setAttribute("text", vo.getName());
		result.setAttribute("target", "mainFrame");
		if(vo.getParentId()==0) {
			rootElement.addContent(result);
			elementMap.put(new Integer(vo.getId()),result);
		} else {
			Element e = (Element)elementMap.get(new Integer(vo.getParentId()));
			if(e!=null) {
				e.addContent(result);
				elementMap.put(new Integer(vo.getId()),result);
			}
		}
	}
}

// Output the xml将此页面转化为xml
XMLOutputter outputter = new XMLOutputter();

org.jdom.output.Format format = org.jdom.output.Format.getCompactFormat();
outputter.setFormat(format);

outputter.output(myDocument, out);
%>

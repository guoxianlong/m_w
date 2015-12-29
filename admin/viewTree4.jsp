<%@ page contentType="text/html;charset=utf-8"%><%@ page import="mmb.system.tree.*"%><%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.bean.*" %><%@ page import="adultadmin.util.*"%><%@ page import="java.util.*"%><%
	response.setHeader("Cache-Control","no-cache");
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();

	int parentId = StringUtil.toInt(request.getParameter("id"));
	if(parentId<0)
		parentId=0;
	List nodeList = ViewTreeAction.getNodeList(parentId);
%><html>
<head><title>买卖宝简版菜单</title>
</head>
<body>
<p align="left">
[<a href="../wap/index.jsp">主菜单</a><%if(parentId!=0){%>&gt;<a href="tree.jsp">根目录</a><%}%>]<br/>
<%for(int i=0;i<nodeList.size();i++){
ViewTree node = (ViewTree)nodeList.get(i);
if(!node.isVisible(group)) continue;
	if(node.getUrl().length()>0){
%><a href="<%=node.getUrl()%>"><%=node.getName()%></a>
<%
	}else{
%>+<a href="viewTree4.jsp?id=<%=node.getId()%>"><%=node.getName()%></a>
<%
}%><br/>
<%}%>
<br/>
<a href="../wap/index.jsp">&lt;&lt;返回主菜单</a><br/>
</p>
</body>
</html>
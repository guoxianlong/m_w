<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="mmb.system.tree.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<% 
	int idx = StringUtil.toInt(request.getParameter("i"));
 	List nodeList = ViewTreeAction.getNodeList();
 	if (idx < 0 || idx >= nodeList.size()) {response.sendRedirect("viewTree3.jsp");return;}
 	ViewTree node = (ViewTree)nodeList.get(idx);
 	String notice = "节点修整";
 	if (request.getParameter("id") != null) {
 		node.setParentId(StringUtil.StringToId(request.getParameter("parentId")));
 		node.setName(StringUtil.toSql(request.getParameter("name")));
 		node.setLimits(StringUtil.toSql(request.getParameter("limits")));
 		node.setSeq(StringUtil.StringToId(request.getParameter("seq")));
 		node.setTarget(StringUtil.toSql(request.getParameter("target")));
 		node.setUrl(StringUtil.toSql(request.getParameter("url")));
 		node.setNodeUrl(StringUtil.toSql(request.getParameter("nodeUrl")));
 		if (ViewTreeAction.updateViewTree(node)) {
 			notice = "操作成功!";
 		}	
 	}
 	
 %>
<html>
<head>
<link href="../../css/global.css" rel="stylesheet" type="text/css">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>view tree edit</title>
</head>
<body>
<form action="viewEdit.jsp" method="post">
	<input type="hidden" name="i" value="<%=idx%>"/>
	<input type="hidden" readonly name="id" value="<%=node.getId()%>"/>
	<table align="center" border="1">
		<tr align="center"><td align="center" colspan="2"><font color="red"><%=notice%></font></td></tr>
		<tr align="center"><td align="center">名称</td><td align="center"><input type="text" name="name" value="<%=node.getName()%>"/></td></tr>
		<tr align="center"><td align="center">父节点</td><td align="center"><input type="text" name="parentId" value="<%=node.getParentId()%>"/></td></tr>
		<tr align="center"><td align="center">权限</td><td align="center"><input type="text" name="limits" value="<%=node.getLimits()%>"/></td></tr>
		<tr align="center"><td align="center">顺序</td><td align="center"><input type="text" name="seq" value="<%=node.getSeq()%>"/></td></tr>
		<tr align="center"><td align="center">连接</td><td align="center"><input type="text" name="url" value="<%=node.getUrl()%>"/></td></tr>
		<tr align="center"><td align="center">高级展开</td><td align="center"><input type="text" name="nodeUrl" value="<%=node.getNodeUrl()%>"/></td></tr>
		<tr align="center"><td align="center">打开方式</td><td align="center"><input type="radio" name="target" value="_blank" <% if("_blank".equals(node.getTarget())){%>checked<%}%>>新窗口打开<input type="radio" name="target" value="" <% if(!"_blank".equals(node.getTarget())){%>checked<%}%>>当前页打开</td></tr>
		<tr align="center"><td align="center"><input type="submit" value="提交"></td><td align="center"><a href="viewTree3.jsp">返回</a></td></tr>
	</table>
</form>	
</body>
</html>

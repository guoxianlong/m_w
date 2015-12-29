<%@ page contentType="text/html;charset=utf-8" %>
<%@page import="java.util.HashMap"%>
<%@include file="../taglibs.jsp"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="mmb.system.tree.*"%>

<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
	
	String notice = "添加新节点";
	if (request.getParameter("y") != null) {
 		notice = "添加成功!";
	}
	if(request.getParameter("clear")!=null){
		ViewTreeAction.rootCache=null;
		response.sendRedirect("viewTree3.jsp");
		return;
	}
	
	String name = request.getParameter("name");
	if (name != null){
		if (name.length() > 0) {
			int parentId = StringUtil.StringToId(request.getParameter("parentId"));
			if (ViewTreeAction.checkParentId(parentId) || parentId==0) {
				ViewTree addView = new ViewTree();
		 		addView.setParentId(parentId);
		 		addView.setName(StringUtil.toSql(request.getParameter("name")));
		 		addView.setLimits(StringUtil.toSql(request.getParameter("limits")));
		 		addView.setSeq(StringUtil.StringToId(request.getParameter("seq")));
		 		addView.setTarget(StringUtil.toSql(request.getParameter("target")));
		 		addView.setUrl(StringUtil.toSql(request.getParameter("url")));
		 		addView.setNodeUrl(StringUtil.toSql(request.getParameter("nodeUrl")));
		 		if (ViewTreeAction.addViewTree(addView)) {
		 			response.sendRedirect("viewTree3.jsp?y=1");return;
		 		}
			} else {
				notice = "不存在的父节点!";
			}
		} else {
			notice = "节点名称不能为空!";
		}
	}
	
 	List nodeList = ViewTreeAction.getNodeList();
 	
%><html>
<head>
<title>功能树管理</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="../js/xtree2.js"></script> 
<script type="text/javascript" src="../js/xmlextras.js"></script>
<script type="text/javascript" src="../js/xloadtree2.js"></script> 
<script type="text/javascript" src="../js/js_tree_viewer.js"></script> 

<link type="text/css" rel="stylesheet" href="../js/xtree2.css" />

<style type="text/css">

body {
	background:	"#F0F0F0";
	color:		black;
	font-size: 18px;
	border-right:1px solid gray;
}

</style>
</head>
<body>
<div style="float:left;position:absolute;left:600px;">
<form action="viewTree3.jsp" method="post">
<table border="1">
	<tr align="center"><td align="center" colspan="2"><font color="red"><%=notice%></font></td></tr>
	<tr align="center"><td align="center">名称</td><td align="center"><input type="text" name="name"/></td></tr>
	<tr align="center"><td align="center">父节点</td><td align="center"><input type="text" name="parentId"/></td></tr>
	<tr align="center"><td align="center">权限</td><td align="center"><input type="text" name="limits"/></td></tr>
	<tr align="center"><td align="center">顺序</td><td align="center"><input type="text" name="seq"/></td></tr>
	<tr align="center"><td align="center">链接</td><td align="center"><input type="text" name="url"/></td></tr>
	<tr align="center"><td align="center">高级展开</td><td align="center"><input type="text" name="nodeUrl"/></td></tr>
	<tr align="center"><td align="center">打开方式</td><td align="center"><input type="radio" name="target" value="_blank">新窗口打开<input type="radio" name="target" value="" checked>当前页打开</td></tr>
	<tr align="center"><td align="center" colspan="2"><input type="submit" value="确认添加"/> - <a href="viewTree3.jsp?clear=1" onclick="return confirm('确定清除功能树缓存?')">清除缓存</a></td></tr>
</table>
</form>
</div>
<div>
<fieldset id="moveNodeFieldSet" style="display:none;">
</fieldset>
<div id="menuTree">
<script type="text/javascript">

/// XP Look
webFXTreeConfig.rootIcon				= "../js/images/root.gif";
webFXTreeConfig.fileIcon				= "../js/images/xp/file.png";
webFXTreeConfig.lMinusIcon			= "../js/images/xp/Lminus.png";
webFXTreeConfig.lPlusIcon				= "../js/images/xp/Lplus.png";
webFXTreeConfig.tMinusIcon			= "../js/images/xp/Tminus.png";
webFXTreeConfig.tPlusIcon				= "../js/images/xp/Tplus.png";
webFXTreeConfig.iIcon					= "../js/images/xp/I.png";
webFXTreeConfig.lIcon					= "../js/images/xp/L.png";
webFXTreeConfig.tIcon					= "../js/images/xp/T.png";
webFXTreeConfig.blankIcon				= "../js/images/blank.png";


function insertNode(parentNode,childNode,url, target)
{
	if(target==null)
		childNode.target="mainFrame";
	else
		childNode.target=target;
	childNode.action=url;
	if(parentNode)
		parentNode.add(childNode);
}

var vt0 = new WebFXTree("<b>买卖宝后台</b>");
<%for(int i=0;i<nodeList.size();i++){
ViewTree node = (ViewTree)nodeList.get(i);
%>var vt<%=node.getId()%>=new WebFXTreeItem("<font color=black><%=node.getName()%>(<%=node.getId()%>)</font> - <font color=red><%=node.getLimits()%></font> - <font color=blue><%=node.getUrl()==null?"":StringUtil.toWml(node.getUrl())%></font> - <font color=gray><%=StringUtil.toWml(node.getNodeUrl())%> [<%=node.getTarget()%>]-顺序<%=node.getSeq()%></font>");insertNode(vt<%=node.getParentId()%>,vt<%=node.getId()%>, "viewEdit.jsp?i="+<%=i%>, "_self");
<%}%>

vt0.write();
vt0.expand();

</script>
</div>

</div>
</body>
</html>
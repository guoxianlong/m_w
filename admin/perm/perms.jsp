<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voOrder"%><%@ page import="java.util.*"%>
<%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.framework.*" %><%@ page import="adultadmin.bean.*" %>
<%
	voUser user = (voUser)session.getAttribute("userView");
	adultadmin.bean.UserGroupBean group = user.getGroup();
	if(!group.isFlag(0)) {
		response.sendRedirect("error.jsp");
		return;
	}
	CustomAction action = new CustomAction(request);
	if(!action.isMethodGet()) {
		PermissionBean g = new PermissionBean();
		g.setId(action.getParameterInt("id"));
		g.setParent(action.getParameterInt("parent"));
		g.setName(request.getParameter("name"));
		g.setBak(request.getParameter("bak"));
		PermissionFrk.updatePermission(g, true);
		session.setAttribute("promptMsg", "添加权限成功");
		response.sendRedirect("perms.jsp");
		return;
	}
	List permList = PermissionFrk.getPermissionList(" 1 order by id");
%>
<html>
<title>买卖宝后台</title>
<script>
</script>
<script type="text/javascript" src="../js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="../js/xtree2.js"></script> 
<script type="text/javascript" src="../js/xmlextras.js"></script>
<script type="text/javascript" src="../js/xloadtree2.js"></script> 
<script type="text/javascript" src="../js/js_tree_viewer.js"></script> 

<link type="text/css" rel="stylesheet" href="../js/xtree2.css" />
<body style="margin-left:12px;">
<%@include file="../../header.jsp"%>
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
var childNode= new WebFXTreeItem('超级管理员');
childNode.action='perm.jsp?id=0';
vt0.add(childNode);
<%
for(int i=1;i<permList.size();i++){
	PermissionBean node = (PermissionBean)permList.get(i);%>
	var vt<%=node.getId()%>=new WebFXTreeItem("<%=node.getName()%>(<%=node.getId()%>)");<%
	}
for(int i=1;i<permList.size();i++){
	PermissionBean node = (PermissionBean)permList.get(i);
	%>insertNode(vt<%=node.getParent()%>,vt<%=node.getId()%>, "perm.jsp?id="+<%=node.getId()%>, "_self");<%
	}
%>
vt0.write();
vt0.expand();

</script>
</div>
<br><hr>
<form action="perms.jsp" method=post>
权限名称:<input type=text name="name" value="">(id : <input type=text name="id" value="">)<br/>
父权限:<input type=text name="catalog" value="0">(可以不填)<br/>
备注:<textarea name="bak"></textarea><br/><br/>
<input type="submit" value="确认添加权限">
</form>
<br>
</body>
</html>
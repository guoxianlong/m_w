<%@ page contentType="text/html;charset=utf-8" %>
<%@page import="java.util.HashMap"%>
<%@include file="taglibs.jsp"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="mmb.system.tree.*"%>
<%@page isELIgnored="false" %>

<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
 	List nodeList = ViewTreeAction.getNodeList(group);
%><html>
<head>
<title>功能树</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<script type="text/javascript" src="js/xtree2.js"></script> 
<script type="text/javascript" src="js/xmlextras.js"></script>
<script type="text/javascript" src="js/xloadtree2.js"></script> 
<script type="text/javascript" src="js/js_tree_viewer.js"></script> 

<link type="text/css" rel="stylesheet" href="js/xtree2.css" />

<style>
.popup1 {
	cursor: default;
	text-decoration: none;
	color: #000000;
	width: 100px;
	border: 1px solid #ffffff;
}

.popup1 img{
	border:0;
	filter:	Alpha(Opacity=70);
}

.popup1:hover {
	/*	border-top: 1px solid buttonhighlight;
	border-left: 1px solid buttonhighlight;
	border-bottom: 1px solid buttonshadow;
	border-right: 1px solid buttonshadow;*/
	border: 1px solid #0A246A;
	cursor: default;
	background-color: #FFEEC2;
	text-decoration: none;
	color: #000000;
	width: 100px;
}

.popup1_hover {
	/*	border-top: 1px solid buttonhighlight;
	border-left: 1px solid buttonhighlight;
	border-bottom: 1px solid buttonshadow;
	border-right: 1px solid buttonshadow;*/
	border: 1px solid #0A246A;
	cursor: default;
	background-color: #FFEEC2;
	text-decoration: none;
	color: #000000;
	width: 100px;
}

.popup1_hover img{
	border:0;
	filter:	Alpha(Opacity=100);
}

.msviLocalToolbar{
border:solid 1px #999;
background:#F1F1F1;

padding:2px 0px 1px 0px;

}
</style>

<style type="text/css">

body {
	background:	"#F0F0F0";
	color:		black;
	font-size: 18px;
	border-right:1px solid gray;
}

</style>
</head>
<body style=font-size:14>
<div id="mainBody" style="display:block; " >
  <fieldset id="moveNodeFieldSet" style="display:none;">
  <legend>Move Node</legend>
	From:<div id="fromNode"></div>
	To:<div id="toNode"></div>
	<input type="button" value="ok" onclick="moveNodeOk();" />
	<input type="button" value="cancel" onclick="moveNodeCancel();"/><br/>
</fieldset>

<div id="menuTree">
<script type="text/javascript">

/// XP Look
webFXTreeConfig.rootIcon				= "js/images/root.gif";
webFXTreeConfig.openRootIcon		= "js/images/xp/openfolder.png";
webFXTreeConfig.folderIcon			= "js/images/xp/folder.png";
webFXTreeConfig.openFolderIcon		= "js/images/xp/openfolder.png";
webFXTreeConfig.fileIcon				= "js/images/xp/file.png";
webFXTreeConfig.lMinusIcon			= "js/images/xp/Lminus.png";
webFXTreeConfig.lPlusIcon				= "js/images/xp/Lplus.png";
webFXTreeConfig.tMinusIcon			= "js/images/xp/Tminus.png";
webFXTreeConfig.tPlusIcon				= "js/images/xp/Tplus.png";
webFXTreeConfig.iIcon					= "js/images/xp/I.png";
webFXTreeConfig.lIcon					= "js/images/xp/L.png";
webFXTreeConfig.tIcon					= "js/images/xp/T.png";
webFXTreeConfig.blankIcon				= "js/images/blank.png";


function insertNode(parentNode,childNode,url, target)
{
	if(target==null)
		childNode.target="mainFrame";
	else
		childNode.target=target;
	childNode.action=url;
	parentNode.add(childNode);
}

var node_root = new WebFXTree("<font size=3><b>买卖宝后台</b></font>");
var vt0=node_root;

<%for(int i=0;i<nodeList.size();i++){
ViewTree node = (ViewTree)nodeList.get(i);
	if(node.getNodeUrl().length()==0){
%>var vt<%=node.getId()%>=new WebFXTreeItem("<%=node.getName()%>");insertNode(vt<%=node.getParentId()%>, vt<%=node.getId()%><%if(node.getUrl().length()>0){%>, "<%=node.getUrl()%>"<%}%><%if(node.getTarget().length()>0){%>,"<%=node.getTarget()%>"<%}%>);
<%
	}else{
%>var vt<%=node.getId()%>=new WebFXLoadTreeItem("<%=node.getName()%>","<%=node.getNodeUrl()%>");insertNode(vt<%=node.getParentId()%>, vt<%=node.getId()%><%if(node.getUrl().length()>0){%>, "<%=node.getUrl()%>"<%}%>);
<%
}%>
<%}%>

vt0.write();
vt0.expand();

</script>
</div>
<table class="msviLocalToolbar" id="popup" width="100"
	style="display: none; position:absolute; z-index:100;
	background-color:white; left: 231px; top: 349px; ">

<tr>
<td style="font-size: 12px;" onclick="hidePopup();refreshNode();" class="popup1" onMouseOver="this.className='popup1_hover';"
            onMouseOut="this.className='popup1';" title="刷新选择的节点">
<img src="js/images/refresh.png" width="16" height="16" align="absmiddle" />&nbsp;刷新</td>
</tr>
<tr>
<td style="font-size: 12px;" onclick="hidePopup();openNode();" class="popup1" onMouseOver="this.className='popup1_hover';"
            onMouseOut="this.className='popup1';" title="在新窗口里打开">
<img src="js/images/file.png" width="16" height="16" align="absmiddle" />&nbsp;新窗口打开</td>
</tr>
</table>
</div>
<script type="text/javascript">
/**
 * 注册事件
 */
try {
	var sssdf = document.getElementById('menuTree');
   document.getElementById('menuTree').oncontextmenu = oncontextmenu;
   document.onclick = OnDocumentClick;
} catch(ex) {
}

</script>
</body>
</html>
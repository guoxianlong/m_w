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
	int id = action.getParameterInt("id");
	UserGroupBean g = PermissionFrk.getUserGroup(id);
	if(g==null){
		response.sendRedirect("groups.jsp");
		return;
	}
	
	if(!action.isMethodGet()) {
		g.setFlags(mmb.util.BinaryFlag.fromStrings(action.getParameterString("flag")));
		g.setName(request.getParameter("name"));
		g.setBak(request.getParameter("bak"));
		PermissionFrk.updateUserGroup(g, false);
		session.setAttribute("promptMsg", "修改权限组成功");
		response.sendRedirect("groups.jsp");
		return;
	}
	
	List permList = PermissionFrk.getPermissionList(" 1 order by id");
%>
<html>
<head>
<title>买卖宝后台</title>
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.ztree.core-3.1.min.js"></script> 
<script type="text/javascript" src="<%=request.getContextPath()%>/js/jquery.ztree.excheck-3.1.min.js"></script> 
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<link href="<%=request.getContextPath()%>/css/zTreeStyle.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
	<!--
		var zTreeObj;
		var setting = {
			check: {
				enable: true
			},
			view: {
				showIcon: false
			},
			data: {
				simpleData: {
					enable: true
				}
			}
		};
		setting.check.chkboxType = { "Y" : "", "N" : "" };
		var zNodes =[<%
		PermissionBean perm = (PermissionBean)permList.get(0);%>
		{ id:-1, pId:<%=perm.getParent()%>, name:"<%=perm.getName()%>(0)"<%if(g.isFlag(perm.getId())){%>, checked:true<%}%>},<%
		for(int i = 1;i < permList.size();i++){
			perm = (PermissionBean)permList.get(i);%>
			{ id:<%=perm.getId()%>, pId:<%=perm.getParent()%>, name:"<%=perm.getName()%>(<%=perm.getId()%>)"<%if(g.isFlag(perm.getId())){%>, checked:true<%}%>}<%=(i==permList.size()-1?"":",")%>
		<%}%>
		];
		$(document).ready(function(){
			zTreeObj=$.fn.zTree.init($("#treeDemo"), setting, zNodes);
		});
		
		function addvaluetohidden() {
			var nodes = zTreeObj.getCheckedNodes();
			var s = '';//选中节点ids
			//遍历选中的节点，为s赋值
			for(var i=0; i<nodes.length; i++){
				if (s != '')
					s += ',';
				if(nodes[i].id==-1){
					s +='0';
				}else{
					s += nodes[i].id;
				}
			}
			$("#flag").val(s);
		}
		//-->
</script>
</head>
<body style="margin-left:12px;">
<%@include file="../../header.jsp"%>
<br>
<form action="group.jsp?id=<%=g.getId()%>" method=post onsubmit="addvaluetohidden()">
名称:<input type=text name="name" value="<%=g.getName()%>">(id : <%=g.getId()%>)<br/>
备注:<textarea name="bak" rows=5 cols=60><%=g.getBak()%></textarea><br/><br/>
权限:<br>
<ul id="treeDemo" class="ztree"></ul>
<input id="flag" type="hidden" name="flag" value="-1"/>
<input type="submit" value="确认修改">
</form>
<br>
</body>
</html>
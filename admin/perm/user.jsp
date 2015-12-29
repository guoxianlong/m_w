<%@ include file="../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voOrder"%><%@ page import="java.util.*"%><%@ page import="adultadmin.util.*"%>
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
	UserPermissionBean p = null;
	voUser u = PermissionFrk.getAdminUser("id="+id);
	List pl = PermissionFrk.getUserPermissionList("user_id="+id);
	
	if(u!=null&&pl.size()>0) {
		p = (UserPermissionBean)pl.get(0);
		if(!action.isMethodGet()) {
			p.setGroups(PermissionFrk.string2ints(request.getParameterValues("groups")));
			if(!"null".equals(request.getParameter("flag")))
				p.setFlag(mmb.util.BinaryFlag.fromStrings(action.getParameterString("flag")));
			PermissionFrk.updateUserPermission(p, false);
		}
	}
	List glist = new ArrayList(PermissionFrk.getGroupMap().values());
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
	<%if(request.getParameter("detail")!=null){%>
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
		{ id:-1, pId:<%=perm.getParent()%>, name:"<%=perm.getName()%>(0)"<%if(p.isInFlag(0)){%>, checked:true<%}%>},<%
		for(int i = 1;i < permList.size();i++){
			perm = (PermissionBean)permList.get(i);%>
			{ id:<%=perm.getId()%>, pId:<%=perm.getParent()%>, name:"<%=perm.getName()%>(<%=perm.getId()%>)"<%if(p.isInFlag(perm.getId())){%>, checked:true<%}%>}<%=(i==permList.size()-1?"":",")%>
		<%}%>
		];
		$(document).ready(function(){
			zTreeObj=$.fn.zTree.init($("#treeDemo"), setting, zNodes);
		});
	<%}%>
		function addvaluetohidden() {
<%if(request.getParameter("detail")!=null){%>
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
<%}%>
		}
		//-->
</script>
</head>
<body style="margin-left:12px;">
<%@include file="../../header.jsp"%>
<br><%if(p!=null){
%><form action="user.jsp?id=<%=u.getId()%>" method="post" onsubmit="addvaluetohidden()">
<table align="left" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr><td height="30" align="center" bgcolor="#F8F8F8">用户名</td><td height="30" bgcolor="#F8F8F8"><%=u.getUsername()%>(id : <%=u.getId()%>)</td></tr>
<tr><td height="30" align="center" bgcolor="#F8F8F8">权限组</td><td height="30" bgcolor="#F8F8F8"><%
int br=0;
int[] groups = p.getGroups();
for(int i=0;i<groups.length;i++){
	UserGroupBean g = PermissionFrk.getUserGroup(groups[i]);
	if(g==null) continue;
	%><%if(i!=0){%>,<%}%><%=g.getName()%><%
}
%></td></tr>
<tr><td height="30" align="center" bgcolor="#F8F8F8">独立权限</td><td height="30" bgcolor="#F8F8F8"><%
int br1=0;
for(int i = 0;i < permList.size();i++){
	PermissionBean perm = (PermissionBean)permList.get(i);
	if(p.isInFlag(perm.getId())){
		%><%=perm.getName()%>,<%
		if(++br1>4&&br1%5==0){
			%><br/><%
		}
	}
}
%></td></tr>
<tr><td height="30" align="center" bgcolor="#F8F8F8">
权限组:</td><td height="30" bgcolor="#F8F8F8"><%
for(int i=0;i<glist.size();i++){
	UserGroupBean g = (UserGroupBean)glist.get(i);
	%><input type="checkbox" name="groups" value="<%=g.getId()%>" <%if(p.isInGroups(g.getId())){%>checked<%}%> ><%if(p.isInGroups(g.getId())){%><font color="red"><%=g.getName()%></font><%}else{%><%=g.getName()%><%}%></input>&nbsp;<%
	if(i>4&&i%5==0){
		%><br/><%
	}
}
%>
<tr>
<td height="30" align="center" bgcolor="#F8F8F8">
</td><td height="30" bgcolor="#F8F8F8"><ul id="treeDemo" class="ztree"></ul>
<%if(request.getParameter("detail")==null){%><a href="user.jsp?id=<%=id%>&detail=1">设置独立权限</a><%}%>
<input id="flag" type="hidden" name="flag" value="null"/></td></tr>
<tr><td height="30" align="center" bgcolor="#F8F8F8" colspan=2><input type="submit" value="确认修改"></td></tr>
</table>
</form>
<br>
<%}else{%>
用户不存在<br/>
<%}%>
</body>
</html>
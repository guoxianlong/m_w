<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.text.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.framework.*" %>
<%@ page import="adultadmin.bean.*" %>
<%
	voUser adminUser = (voUser)session.getAttribute("userView");
	UserGroupBean group = adminUser.getGroup();
    //判断有没有权限
    if(!PermissionFrk.hasPermission(request, PermissionFrk.USER_ADMIN)){
		return;
    }

boolean isSystem = (adminUser.getSecurityLevel() == 10);	//系统管理员
SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
SimpleDateFormat sdfTime = new SimpleDateFormat("kk:mm:ss");
%>
<html>
<title>买卖宝后台</title>
<script>
function del_user(page)
{	 
	if(confirm('确认要删除选中的注册用户吗?')) {
		
		document.userForm.action="duser.do?type=1&currentPage="+page;
		return document.userForm.submit();
	} else {
		return false;
	}
}
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
<form action="<%=request.getContextPath()%>/admin/users.do" method="post">
<%@include file="../pageNum.jsp"%>
</form>
          <br><form method=post action="" name="userForm">
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td align="center"><font color="#FFFFFF">选项</font></td>
			  <td align="center"><font color="#FFFFFF">ID</font></td>
              <td align="center"><font color="#FFFFFF">用户名</font></td>
              <td align="center"><font color="#FFFFFF">用户昵称</font></td>
			  <td align="center"><font color="#FFFFFF">姓名</font></td>
			  <td align="center"><font color="#FFFFFF">等级</font></td>
              <td align="center"><font color="#FFFFFF">电话号码</font></td>
              <td align="center"><font color="#FFFFFF">代理商</font></td>
			  <td align="center"><font color="#FFFFFF">退货款</font></td>
			  <td align="center"><font color="#FFFFFF">应返款</font></td>
              <td align="center"><font color="#FFFFFF">折扣</font></td>
			  <td align="center"><font color="#FFFFFF">客服负责人</font></td>
			  <td align="center"><font color="#FFFFFF">操作</font></td>
            </tr>
<logic:present name="userList" scope="request"> 
<logic:iterate name="userList" id="item" > 
<%adultadmin.action.vo.voUser user = (adultadmin.action.vo.voUser)item;%>
			<tr bgcolor='#F8F8F8'>
				<td align='center'><input type='checkbox' name='id' value='<bean:write name="item" property="id" />'></td>
				<td align=left><a href="searchorder.do?userId=<bean:write name="item" property="id" />" target="_blank"><bean:write name="item" property="id" /></a></td>
				<td align=left><bean:write name="item" property="username" /></td>
				<td align=left><bean:write name="item" property="nick" /></td>
				<td align=left><bean:write name="item" property="name" /></td>
				<td align=left><%if(user.getUserInfo() != null && user.getUserInfo().getRank() == 1){%><font color="red">普通会员</font><%} else if(user.getUserInfo() != null && user.getUserInfo().getRank() == 2){%><font color="blue">VIP会员</font><%} else {%>普通用户<%}%></td>
				<td align=left><bean:write name="item" property="phone" /></td>
				<td align=left><%= (user.getAgent()==1)?"是":"否" %></td>
				<td align=left><%if(user.getAgent()==1){%><bean:write name="item" property="orderReimburse" format="0.00"/><%}else{%>无<%}%></td>
				<td align=left><%if(user.getAgent()==1){%><bean:write name="item" property="reimburse" format="0.00"/><%}else{%>无<%}%></td>
				<td align=left><%if(user.getAgent()==1){%><bean:write name="item" property="discount" /><%}else{%>无<%}%></td>
				<td align=left><%if(user.getUserInfo() != null && user.getUserInfo().getRank() > 0){ if(user.getUserInfo().getAdminId() == 0){%>未设置<%}else{%><a href="users.do?adminId=<%=user.getUserInfo().getAdminId()%>"><%=user.getUserInfo().getAdminName()%></a><%}} else {%>无<%}%></td>
				<td align=right><a href="fuser.do?id=<bean:write name="item" property="id" />">查看</a></td>
			</tr>
</logic:iterate>
</logic:present> 
          </table>
          </form>
          <table width="80%" cellspacing="0" cellpadding="0">
            <tr>
              <td height="35"><input type="button" onClick="window.navigate('fuser.do')" value="添 加">
<%if(group.isFlag(0)) /*if(isSystem)*/{%>
              <input type="button" value=" 删 除 " onClick='return del_user(<%=request.getAttribute("currentPage")%>)'>
<%}%>
              </td>
            </tr>
          </table>
<%@include file="../page.jsp"%>
          <br>  
	</body>
</html>
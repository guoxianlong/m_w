<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.text.*,adultadmin.action.vo.*, adultadmin.util.*, adultadmin.framework.UserFrk, java.util.*" %>
<%@ page import="adultadmin.framework.*" %>
<%@ page import="adultadmin.bean.*" %>
<%
	voUser adminUser = (voUser)session.getAttribute("userView");
	UserGroupBean group = adminUser.getGroup();
    //判断有没有权限
    if(!PermissionFrk.hasPermission(request, PermissionFrk.USER_ADMIN)){
		return;
    }

	voUser user = (voUser)request.getAttribute("user");
	boolean isSystem = (adminUser.getSecurityLevel() == 10);	//系统管理员

	if(!isSystem && (user.getSecurityLevel() != 0 || user.getPermission() != 0)){
		out.println("您无权查看或修改。");
		return;
	}

	UserInfoBean userInfo = (UserInfoBean) request.getAttribute("userInfo");
	int point = 0;
	int rank = 0;
	int totalPoint = 0;
	if(userInfo != null){
		point = userInfo.getPoint();
		rank = userInfo.getRank();
		totalPoint = userInfo.getTotalPoint();
	}

	//客户负责人列表，客服部的人员
	List adminList = UserFrk.getAdminList(3);
%>
<html>
<title>买卖宝后台</title>
<link href="../css/global.css" rel="stylesheet" type="text/css">
<body>
<script>
function checksubmit()
{
	with(muserForm){
		if(username.value.length==0){
			alert("用户名不能为空！");
			return false;
		}
		if(discount.value.length==0){
			alert("折扣不能为空！");
			return false;
		}
	}
	return true;
}
</script>
<%@include file="../../header.jsp"%>
        <form name="muserForm" method="post" action="madminuser.do" >
          <table width="80%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8">
            <tr> 
              <td><table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
                  <tr>
                    <td height="30" align="center" bgcolor="#F8F8F8">用户名：</td>
                    <td bgcolor="#F8F8F8"><input type="text" name="username" value="<%= user.getUsername() %>"/></td>
                  </tr>
<%if(group.isFlag(0)) /*if(adminUser.getSecurityLevel() == 10)*/{%>
                  <tr> 
                    <td align="center" bgcolor="#F8F8F8">重置密码：</td>
                    <td bgcolor="#F8F8F8">
                    <input type="text" name="password"/>
                    	再次输入:<input type="text" name="password2"/>(至少输入六位才有效，留空则表示不修改密码)
                    </td>
                  </tr>
<%}%>

                  <tr> 
                    <td align="center" bgcolor="#F8F8F8">昵称：</td>
                    <td bgcolor="#F8F8F8"><input type="text" name="nick" value="<%= user.getNick() %>" /></td>
                  </tr>
<%--                  
				  <tr> 
                    <td align="center" bgcolor="#F8F8F8">电话号码：</td>
                    <td bgcolor="#F8F8F8"><font color="red"><input type="text" name="phone" value="<%=user.getPhone()%>" /></font></td>
                  </tr>

				  <tr> 
                    <td align="center" bgcolor="#F8F8F8">积分：</td>
                    <td bgcolor="#F8F8F8"><input type="text" name="point" value="<%=point%>" /></td>
                  </tr>

				  <tr> 
                    <td align="center" bgcolor="#F8F8F8">历史积分：</td>
                    <td bgcolor="#F8F8F8"><font color="red"><%=totalPoint%></font> <a href="userPointHistory.jsp?userId=<%=user.getId()%>" target="_blank">历史记录</a> <a href="clearPoint.jsp?userId=<%=user.getId()%>" onclick="return confirm('确认清空？不可恢复！')">清空积分</a></td>
                  </tr>
				  <tr> 
                    <td align="center" bgcolor="#F8F8F8">等级：</td>
                    <td bgcolor="#F8F8F8"><font color="red"><%=UserFrk.getRankName(rank)%></font></td>
                  </tr>
--%>                  
				  <tr> 
                    <td align="center" bgcolor="#F8F8F8">姓名：</td>
                    <td bgcolor="#F8F8F8"><input type="text" name="name" value="<%= user.getName() %>" /></td>
                  </tr>
                  <tr> 
                    <td height="30" align="center" bgcolor="#F8F8F8">用户级别：</td>
                    <td bgcolor="#F8F8F8">
                    	<select name="securityLevel">
<%if(group.isFlag(0)) /*if(adminUser.getSecurityLevel() == 10)*/{%>
                    		<option value="5" <%= (user.getSecurityLevel()==5)?"selected=\"selected\"":"" %>>普通管理员</option>
                    		<option value="9" <%= (user.getSecurityLevel()==9)?"selected=\"selected\"":"" %>>高级管理员</option>
                    		<option value="10" <%= (user.getSecurityLevel()==10)?"selected=\"selected\"":"" %>>超级管理员</option>
<%}%>
                    	</select>
                    </td>
                  </tr>
                  <tr> 
                    <td height="30" align="center" bgcolor="#F8F8F8">隶属部门：</td>
                    <td bgcolor="#F8F8F8">
                    	<select name="permission">
                    		<option value="0" <%= (user.getSecurityLevel()==0)?"selected=\"selected\"":"" %>>无</option>
<%if(group.isFlag(0)) /*if(adminUser.getSecurityLevel() == 10)*/{%>
							<option value="2" <%= (user.getPermission()==2)?"selected=\"selected\"":"" %>>物流库存部</option>
							<option value="3" <%= (user.getPermission()==3)?"selected=\"selected\"":"" %>>客服部</option>
                    		<option value="4" <%= (user.getPermission()==4)?"selected=\"selected\"":"" %>>运营中心</option>
                    		<option value="5" <%= (user.getPermission()==5)?"selected=\"selected\"":"" %>>推广部</option>
                    		<option value="6" <%= (user.getPermission()==6)?"selected=\"selected\"":"" %>>商品采购部</option>
                    		<option value="7" <%= (user.getPermission()==7)?"selected=\"selected\"":"" %>>销售部</option>
                    		<option value="8" <%= (user.getPermission()==8)?"selected=\"selected\"":"" %>>平台运维部</option>
<%}%>
                    		<%--
                    		<option value="9" <%= (user.getPermission()==9)?"selected=\"selected\"":"" %>>高级管理员</option>
                    		<option value="10" <%= (user.getPermission()==10)?"selected=\"selected\"":"" %>>超级管理员</option>
                    		--%>
                    	</select>
                    </td>
                  </tr>
<%--                  
                  <tr> 
                    <td height="30" align="center" bgcolor="#F8F8F8">op访问级别：</td>
                    <td bgcolor="#F8F8F8">
                    	<select name="flag">
                    		<option value="0" <%= (user.getFlag()==0)?"selected=\"selected\"":"" %>>普通用户</option>
<%if(group.isFlag(0)) /*if(adminUser.getSecurityLevel() == 10)*/{%>
                    		<option value="5" <%= (user.getFlag()==5)?"selected=\"selected\"":"" %>>OPAdmin</option>
                    		<option value="10" <%= (user.getFlag()==10)?"selected=\"selected\"":"" %>>管理员</option>
<%}%>
                    	</select>
                    </td>
                  </tr>
                  <tr style="display:none"> 
                    <td height="30" align="center" bgcolor="#F8F8F8">是否VIP：</td>
                    <td bgcolor="#F8F8F8">
                    	<select name="vip">
                    		<option value="1" <%= (user.getVip()==1)?"selected=\"selected\"":"" %> >是</option>
                    		<option value="0" <%= (user.getVip()==0)?"selected=\"selected\"":"" %> >否</option>
                    	</select>
                    </td>
                  </tr>
                  <tr style="display:none"> 
                    <td align="center" bgcolor="#F8F8F8">VIP手机号：</td>
                    <td bgcolor="#F8F8F8"><input type="text" name="vipPhone" value="<%= user.getVipPhone() %>" /></td>
                  </tr>
                  <tr> 
                    <td height="30" align="center" bgcolor="#F8F8F8">是否为代理商：</td>
                    <td bgcolor="#F8F8F8">
                    	<select name="agent">
                    		<option value="1" <%= (user.getAgent()==1)?"selected=\"selected\"":"" %> >是</option>
                    		<option value="0" <%= (user.getAgent()==0)?"selected=\"selected\"":"" %> >否</option>
                    	</select>
                    </td>
                  </tr>
                  <tr> 
                    <td align="center" bgcolor="#F8F8F8">折扣：</td>
                    <td bgcolor="#F8F8F8"><input type="text" name="discount" value="<%= StringUtil.formatFloat(user.getDiscount()) %>" /></td>
                  </tr>
				  <tr> 
                    <td align="center" bgcolor="#F8F8F8">退货款：</td>
                    <td bgcolor="#F8F8F8"><input type="text" name="sssssss1" value="<%= StringUtil.formatFloat(user.getOrderReimburse()) %>" readonly/></td>
                  </tr>
				  <tr> 
                    <td align="center" bgcolor="#F8F8F8">应返款：</td>
                    <td bgcolor="#F8F8F8"><input type="text" name="sssssss2" value="<%= StringUtil.formatFloat(user.getReimburse()) %>" readonly/></td>
                  </tr>
 --%>                  
                  <tr> 
                    <td align="center" bgcolor="#F8F8F8">OA姓名：</td>
                    <td bgcolor="#F8F8F8"><input type="text" name="username2" value="<%=user.getUsername2() %>"/></td>
                  </tr>
                  <tr> 
                    <td height="35" colspan="2" align="center" bgcolor="#F8F8F8">
                    <input type="submit" value=" 确 定 " onclick="return checksubmit();" >
                    </td>
                  </tr>
                </table></td>
            </tr>
          </table>
          <br>

<input type="hidden" name="id" value="<%=user.getId()%>">
        </form>

<%if(group.isFlag(0)){
List<String> list = (List<String>)request.getAttribute("bindMobile");
for(String str:list){
	%><%=str%>,<a href="madminuserbindmobile.do?cmd=i&id=<%=user.getId()%>&pn=<%=str%>" onclick="return confirm('即将下发说明，请确认');">下发说明</a>,
	<a href="madminuserbindmobile.do?cmd=o&id=<%=user.getId()%>&pn=<%=str%>" onclick="return confirm('即将下发动态口令，请确认');">下发动态口令</a>,
	<a href="madminuserbindmobile.do?cmd=k&id=<%=user.getId()%>&pn=<%=str%>" onclick="return confirm('即将下发手机令牌，如果用户已绑定令牌则旧令牌失效，请确认');">下发手机令牌</a>,
	<a href="madminuserbindmobile.do?cmd=d&id=<%=user.getId()%>&pn=<%=str%>" onclick="return confirm('即将删除，请确认');">删除</a><br/><%
}
%><form method="post" action="madminuserbindmobile.do?cmd=a" onsubmit="return confirm('即将 添加绑定手机号，请确认');">
		绑定手机号：	
    	<input type="text" name="bindMobile" value="" />
		<input type="submit" value=" 确 定 " />
		<input type="hidden" name="id" value="<%=user.getId()%>">
	</form><%
	if(user.getOtpKey().length()>5){
		%><font size="5">当前用户已绑定手机令牌</font><%
	}
}%>
<%@include file="../../footer.jsp"%>
</body>
</html>
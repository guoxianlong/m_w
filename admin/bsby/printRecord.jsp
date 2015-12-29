<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.bybs.*, java.util.List, adultadmin.bean.buy.*,adultadmin.util.*, adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.bybs.*" %>
<%
voUser user = (voUser)session.getAttribute("userView");
//UserGroupBean group = user.getGroup();

/* 没有权限，无法查看——人员操作记录
if(!group.isFlag(183)){
	return;
}*/

ByBsAction action = new ByBsAction();
action.getOperationPrintRecord(request, response);


List list = (List) request.getAttribute("list");
int i, count;
BsbyOperationRecordBean log = null;

%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">

<table width="100%" border="1">
<tr>
  <td>序号</td>
  <td>时间</td>  
  <td>操作人员</td>
</tr>
<%
count = list.size();
for(i = 0; i < count; i ++){
	log = (BsbyOperationRecordBean) list.get(i);
%>
<tr>
  <td><%=(i + 1)%></td>
  <td><%=log.getTime() %></td> 
  <td><%=log.getOperator_name() %></td>
  <td>
</tr>
<%
}
%>
</table>
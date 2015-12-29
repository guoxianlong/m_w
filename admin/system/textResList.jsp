<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*, java.util.*" %>
<%@ page import="adultadmin.action.system.*" %>
<%@ page import="adultadmin.bean.system.*" %>
<%
TextResAction action = new TextResAction();
action.textResList(request, response);
List list = (List) request.getAttribute("list");
PagingBean paging = (PagingBean) request.getAttribute("paging");
int i, count;
TextResBean bean = null;
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<script language="JavaScript">

</script>
<p align="center">文本资源列表</p>
<form name="textResForm" method="post" action="deleteTextRes.jsp">
<table width="100%" border="1">
<tr>
  <td>选择</td>
  <td>序号</td>
  <td>类型</td>
  <td>内容</td>
  <td>操作</td>
</tr>
<%
count = list.size();
for(i = 0; i < count; i ++){
	bean = (TextResBean) list.get(i);
%>
<tr>
  <td><input type='checkbox' name='id' value='<%= bean.getId() %>'></td>
  <td><%= bean.getId() %></td>
  <td><%=bean.getTypeName()%></td>
  <td><%= bean.getContent() %></td>
  <td><a href="textRes.jsp?textResId=<%=bean.getId()%>">编辑</a>|<a href="deleteTextRes.jsp?textResId=<%=bean.getId()%>" onclick="return confirm('确认删除？')">删除</a></td>
</tr>
<%
}
%>
</table>
<br/>
<input type='checkbox' name='selectAll' value='' onclick="setAllCheck(document.forms[0], 'id', this.checked);">全选&nbsp;&nbsp;&nbsp;&nbsp;
<input type="button" value="添加" onclick="window.location.href='textRes.jsp';" />&nbsp;<input type="submit" value="删除" onclick="return confirm('确认删除选中的信息？');" />
</form>
<p align="right"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
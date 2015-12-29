<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="adultadmin.bean.stock.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.framework.*" %>
<%
	PagingBean paging = (PagingBean) request.getAttribute("paging");
%>
<html>
<title>买卖宝后台</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "currentPage", 10)%>
          <br><form method=post action="" name="catalogForm">
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td align="center"><font color="#FFFFFF">ID</font></td>
              <td align="center"><font color="#FFFFFF">操作名称</font></td>
              <td align="center"><font color="#FFFFFF">操作人员</font></td>
              <td align="center"><font color="#FFFFFF">操作时间</font></td>
			  <td align="center"><font color="#FFFFFF">操作</font></td>
            </tr>
<logic:present name="logList" scope="request"> 
<logic:iterate name="logList" id="item" scope="request" > 
			<tr bgcolor='#F8F8F8'>
				<td align=left><bean:write name="item" property="id" /></td>
				<td align=left><bean:write name="item" property="typeName" /></td>
				<td align=left><bean:write name="item" property="user.username" /></td>
				<td align=left><bean:write name="item" property="createDatetime" /></td>
				<td align=right><a href="forderImportLog.do?id=<bean:write name="item" property="id" />">查看</a></td>
			</tr>
</logic:iterate>
</logic:present> 
          </table>
          </form>
          <br />
</body>
</html>
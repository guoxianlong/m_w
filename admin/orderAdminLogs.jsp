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
<logic:present name="orderStatusList" scope="request">
<p style="margin: 0px">订单状态:&nbsp;&nbsp;
<logic:iterate indexId="indexId" name="orderStatusList" id="item" scope="request" >
<logic:equal name="item" property="visible" value="1"><bean:write name="item" property="id" />——<bean:write name="item" property="name" />
<logic:equal name="item" property="id" value="3">(待发货)</logic:equal>,</logic:equal>
<logic:equal name="indexId" value="7"><br/></logic:equal>
</logic:iterate>
</p><br/>
</logic:present>
<p style="margin: 0px">购买方式:&nbsp;&nbsp;0——货到付款,1——邮购,2——上门自取</p>
<p style="margin: 0px">发货处理状态:&nbsp;&nbsp;0——发货未处理,1——发货失败,2——发货成功,3——空白,4——缺货未处理,5——缺货电话失败,6——缺货电话成功,7——缺货已补货</p>
<p style="margin: 0px">性别:&nbsp;&nbsp;0--未识别,1--男,2--女</p>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "currentPage", 10)%>
          <br><form method=post action="" name="catalogForm">
          <table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td align="center"><font color="#FFFFFF">ID</font></td>
              <td align="center"><font color="#FFFFFF">操作名称</font></td>
              <td align="center"><font color="#FFFFFF">操作人员</font></td>
              <td align="center"><font color="#FFFFFF">操作时间</font></td>
              <td align="center"><font color="#FFFFFF">操作订单</font></td>
			  <td align="center"><font color="#FFFFFF">操作内容</font></td>
            </tr>
<logic:present name="logList" scope="request"> 
<logic:iterate name="logList" id="item" scope="request" > 
			<tr bgcolor='#F8F8F8'>
				<td align=left><bean:write name="item" property="id" /></td>
				<td align=left><bean:write name="item" property="typeName" /></td>
				<td align=left><bean:write name="item" property="username" /></td>
				<td align=left><bean:write name="item" property="createDatetime" /></td>
				<td align=left><bean:write name="item" property="orderCode" /></td>
				<td align="left"><bean:write name="item" property="content" /></td>
			</tr>
</logic:iterate>
</logic:present> 
          </table>
          </form>
          <br />
</body>
</html>
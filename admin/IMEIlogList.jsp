<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page contentType="text/html;charset=utf-8"%>
<%@ page import="java.util.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.PagingBean"%>
<%@ page import="adultadmin.util.PageUtil"%>
<%@ page import="java.text.DecimalFormat"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.*"%>
<%@ page import="mmb.stock.IMEI.*"%>

<html>
<%
voUser user = (voUser) request.getAttribute("userView");
List IMEIlogList=(List)request.getAttribute("IMEIlogList");
PagingBean paging = (PagingBean) request.getAttribute("paging");
String code = (String) request.getParameter("code");
%>
<head>
<title>EMEI码日志查询列表</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script> 
<script language="JavaScript" src="<%=request.getContextPath() %>/js/JS_functions.js"></script>
</head>
<body bgcolor="#FFCC00">
<fieldset>
 <form  method="post" action="<%=request.getContextPath()%>/admin/IMEIAction.do?method=IMEIlogList" onSubmit="return checksubmit();">
   <div align="center">
     <input style="height:22px;font-size:18px;" name="code" id="code" <%if(code!=null) {%>value="<%=code%>"<%} %>type="text"/>&nbsp;
     <input type="submit" style="height:22px;font-size:18px;" name="button" id="button" value="查询" />
   </div>
</form>
</fieldset>
<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
  <tr bgcolor="#00ccff">
   		<td><div align="center"><span class="STYLE2"><font color="#00000">操作编号</font></span></div></td>
   		<td><div align="center"><span class="STYLE2"><font color="#00000">操作类型</font></span></div></td>
   		<td><div align="center"><span class="STYLE2"><font color="#00000">IMEI码</font></span></div></td>
    	<td><div align="center"><span class="STYLE2"><font color="#00000">操作内容</font></span></div></td>
   	 	<td><div align="center"><span class="STYLE2"><font color="#00000">用户名称</font></span></div></td>
   		<td><div align="center"><span class="STYLE2"><font color="#00000">创建时间</font></span></div></td>
  </tr>
  <% if(IMEIlogList!=null){
    	 for(int i=0;i<IMEIlogList.size();i++){ %>
  <% IMEILogBean logBean=(IMEILogBean)IMEIlogList.get(i); %>
  <tr>
   		<td height="30"><div align="center"><%=StringUtil.convertNull(logBean.getOperCode()) %></div></td>
   		<td><div align="center"><%=StringUtil.convertNull(IMEILogBean.operTypeMap.get(logBean.getOperType())+"")%></div></td>
   		<td><div align="center"><%=logBean.getIMEI()%></div></td>
    	<td><div align="center"><%=logBean.getContent() %></div></td>
   	 	<td><div align="center"><%=logBean.getUserName()%></div></td>
   		<td><div align="center"><%=StringUtil.convertNull(StringUtil.cutString(logBean.getCreateDatetime(), 0, 19) ) %></div></td>
  </tr><%}} %>
</table>
<%if (paging!=null){%>
<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
<%} %>
</body>
</html>
<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="adultadmin.bean.PagingBean" %>
<%@ page import="mmb.stock.stat.*" %>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*,adultadmin.bean.bybs.*" %>
<%@ page import="mmb.stock.stat.formbean.ReturnedPackageFBean" %>
<%
AbnormalCargoCheckBean accBean = (AbnormalCargoCheckBean) request.getAttribute("accBean");
List<AbnormalCargoCheckProductBean> accpBeanList = (ArrayList<AbnormalCargoCheckProductBean>)request.getAttribute("accpBeanList");
List areaList = CargoDeptAreaService.getCargoDeptAreaList(request); 
PagingBean paging = (PagingBean) request.getAttribute("paging");
voUser user = (voUser)request.getSession().getAttribute("userView");
 	int userid = user.getId();
 	UserGroupBean group = user.getGroup();

%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>货位异常盘点明细</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<style type="text/css" >
	#submitButton {
		width:80px;
	}
</style>
</head>
<body >
&nbsp;&nbsp;&nbsp;&nbsp;异常货位盘点计划<font color="red"> <%= accBean.getCode()%></font>详细页  
<div style="margin-left:72%;"><button style="width:240px;" onclick="javascript:window.location='<%= request.getContextPath()%>/admin/abnormalCargoCheck.do?method=generatedBsby&id=<%= accBean.getId()%>'">生成报损报溢单</button></div>
<form id="form2" name="form2" method="post" action="<%=request.getContextPath()%>/admin/abnormalCargoCheck.do?method=getAbnormalCargoCheckDetail">
<input type="hidden" name="id" value="<%=request.getParameter("id") %>" />
  <table width="60%" border="0">
    <tr>
      <td width="30%">
      	SKU:<input type="text" id="" name="productCode"  value='<%=request.getParameter("productCode")==null?"":request.getParameter("productCode")%>'/>
       	货位:<input type="text" id="" name="cargoCode"  value='<%=request.getParameter("cargoCode")==null?"":request.getParameter("cargoCode")%>'/>
       &nbsp;&nbsp;&nbsp;
   	  <select name="status" id="status">
           <option value="-1">全部</option>
           <option value="0" <%if("0".equals(request.getParameter("status"))){ %>selected="selected"<%} %>>待一盘</option>
           <option value="1" <%if("1".equals(request.getParameter("status"))){ %>selected="selected"<%} %>>待二盘</option>
           <option value="2" <%if("2".equals(request.getParameter("status"))){ %>selected="selected"<%} %>>待终盘</option>
           <option value="3" <%if("3".equals(request.getParameter("status"))){ %>selected="selected"<%} %>>盘点已完成</option>
           <option value="4" <%if("4".equals(request.getParameter("status"))){ %>selected="selected"<%} %>>无效盘点</option>
  </select>&nbsp;&nbsp;&nbsp;
  	<input type="hidden" id="export" name="export" value="-1"/>
     <input type="submit" name="button2" id="button2" value="查询" onclick="document.getElementById('export').value='-1';" />     
     <input type="submit" name="button2" id="button2" value="导出" onclick="document.getElementById('export').value='1';" />     
     </td>
    </tr>
  </table>
<table  width="99%" border="1" cellspacing="0" bordercolor="#00000">
  <tr bgcolor="#00ccff">
    <td><div align="center"><strong><font color="#00000">SKU</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">货位</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">货位可用库存</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">货位冻结量</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">异常处理单冻结量</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">一盘</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">一盘操作人</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">二盘</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">二盘操作人</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">终盘</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">终盘操作人</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">报损</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">报溢</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">报损报溢单</font></strong></div></td>
    <td><div align="center"><strong><font color="#00000">状态</font></strong></div></td>
  </tr>
<%if(accpBeanList!=null){
		for(AbnormalCargoCheckProductBean bean : accpBeanList){%>
  <tr bgcolor="#EEE9D9">
    <td style="color: #000"><div align="center"><%=bean.getProductCode() %></div></td>
    <td style="color: #000"><div align="center"><%=bean.getCargoWholeCode() %></div></td>
    <td style="color: #000"><div align="center"><%=bean.getCargoProductStockBean()==null?"-":bean.getCargoProductStockBean().getStockCount() %></div></td>
    <td style="color: #000"><div align="center"><%=bean.getCargoProductStockBean()==null?"-":bean.getCargoProductStockBean().getStockLockCount() %></div></td>
    <td style="color: #000"><div align="center"><%=bean.getLockCount() %></div></td>
    <td style="color: #000"><div align="center"><%=bean.getFirstCheckCount() %></div></td>
    <td style="color: #000"><div align="center"><% if(bean.getFirstCheckUserName() != null){%><%=bean.getFirstCheckUserName() %><%} %></div></td>
    <td style="color: #000"><div align="center"><%=bean.getSecondCheckCount() %></div></td>
    <td style="color: #000"><div align="center"><% if(bean.getSecondCheckUserName() != null){%><%=bean.getSecondCheckUserName() %><%} %></div></td>
    <td style="color: #000"><div align="center"><%=bean.getThirdCheckCount() %></div></td>
    <td style="color: #000"><div align="center"><% if(bean.getThirdCheckUserName() != null){%><%=bean.getThirdCheckUserName() %><%} %></div></td>
    <td style="color: #000"><div align="center"><% if(bean.getBsbyBean() != null ) { if(bean.getBsbyBean().getType() == 0 ) { %><%=bean.getBsbyBean().getBsbyProductBeans().get(0).getBsby_count()%><% } else { %>-<% } } else { %>-<% }%></div></td>
    <td style="color: #000"><div align="center"><% if(bean.getBsbyBean() != null ) { if(bean.getBsbyBean().getType() == 1 ) { %><%=bean.getBsbyBean().getBsbyProductBeans().get(0).getBsby_count()%><% } else { %>-<% } } else { %>-<% }%></div></td>
    <td style="color: #000"><div align="center">
    <% if(bean.getBsbyBean() != null ) {
		BsbyOperationnoteBean boBean = bean.getBsbyBean();
		if( boBean.getIf_del() == 1 ) {
			%>
				<%= boBean.getReceipts_number()%>
			<% 
				} else {
			%>
				<%int type = boBean.getCurrent_type();
						if((type==0||type==1||type==2||type==5)&&(userid==boBean.getOperator_id()||group.isFlag(413))){%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%= boBean.getId()%>" target="_blank"><%= boBean.getReceipts_number()%></a><br/>
						<%}else if(type==6&&(userid==boBean.getOperator_id()||group.isFlag(229))){%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%= boBean.getId()%>" target="_blank"><%= boBean.getReceipts_number()%></a><br/>
						<%}else {%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&lookup=1&opid=<%= boBean.getId()%>" target="_blank"><%= boBean.getReceipts_number()%></a><br/>
						<%}%>
			<%
				}
    		    } else { 
    		%>
    		&nbsp;<%
    		 }
    		 %>
    
    </div></td>
    <td style="color: #000"><div align="center"><%=AbnormalCargoCheckProductBean.getStatusName(bean.getStatus()) %></div></td>
  </tr>
<%}}%>
</table>
</form>
<%if(paging!=null){ %>
		<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
<%} %>
</body>
</html>
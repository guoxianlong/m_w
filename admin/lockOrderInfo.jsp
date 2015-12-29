<%@ include file="../taglibs.jsp"%><%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
	List list = (List)request.getAttribute("list");
	int totalCount=0;
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <title>冻结量详细信息</title>
    <script language="JavaScript" src="js/soft.js"></script>
  </head>
  
  <body>
   <table cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center  width="70%" id="listTable">
    <thead>
	   	<tr bgcolor='#F8F8F8' align=center>
	   		<td>序号</td><td>单据号</td><td onclick="sortTable('listTable', 2, 'int');numberCells();">冻结量</td><td>类型</td>
	   		<td  style="display:none" >单子生成时间</td>
	   	</tr>
   	</thead>
   	<tbody>
    <logic:present name="list" scope="request">
		<logic:iterate name="list" id="item" type="adultadmin.bean.stock.LockOderBean" indexId="index">
		<%totalCount+=item.getCount(); %>
		<tr bgcolor='#F8F8F8' align=center >
			<td><%=list.size()-Integer.valueOf(pageContext.getAttribute("index")+"").intValue()%> </td>
			<td>
<%if(item.getType().equals("销售出库")){ %>
	<%if(group.isFlag(8)){ %>
			<a href="order.do?id=<%=item.getOrderId() %>"><%=item.getCode()%></a>
	<%} else { %><%=item.getCode()%><%} %>
<%}else if(item.getType().equals("调拨")){ %>
	<%if(group.isFlag(79)){ %>
			<a href="productStock/stockExchange.jsp?exchangeId=<%=item.getExchangeId() %>"><%=item.getCode()%></a>
<%}else { %><%=item.getCode()%><%}} else if(item.getType().equals("报损")){
if(group.isFlag(230)){%>
<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%=item.getOrderId() %>"><%=item.getCode()%></a>
<%}else { %><%=item.getCode()%><%}} else if(item.getType().equals("仓内作业")){%>
<%=item.getCode()%>
<%}else { %><%=item.getCode()%><%} %>
</td>
			<td><%=item.getCount() %></td><td><%=item.getType() %></td>
			<td style="display:none" ><%=item.getCreatDate().replaceAll("-","/") %></td>
		</tr>
		</logic:iterate>
	</logic:present>
	<tr bgcolor='#F8F8F8' align=center >
		<td colspan="2">总计</td>
		<td><%=totalCount %></td>
		<td>&nbsp;</td>
	</tr>
	</tbody>
   </table>
   <script type="text/javascript">
  sortTable('listTable', 4, 'date');
  // numberCells();
   function numberCells() {
	  var count=document.getElementById('listTable').rows.length;
	  var a=1;
	  for(var i=document.getElementById('listTable').rows.length;i>0;i--){
	   document.getElementById('listTable').rows(a).cells(0).innerText = count-1;  	
	   count--;a++;
	  }
   }
   </script>
  </body>
</html>

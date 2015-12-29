<%@ page contentType="text/html;charset=utf-8"%>

<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.balance.MailingBalanceAuditingBean"%>
<%@page import="adultadmin.bean.balance.MailingBalanceBean"%>
<%@page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.UserGroupBean"%>
<%@page import="adultadmin.action.vo.voOrder"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="adultadmin.bean.PagingBean"%>
<%@page import="adultadmin.util.PageUtil"%><html>
<head>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<title>订单应收款结算管理</title>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
List mbaList=(List)request.getAttribute("mbaList");
List mbaList2=(List)request.getAttribute("mbaList2");
List mbaList3=(List)request.getAttribute("mbaList3");
List mbaList4=(List)request.getAttribute("mbaList4");
List mbaList5=(List)request.getAttribute("mbaList5");
List mbaList6=(List)request.getAttribute("mbaList6");
PagingBean pageBean1=(PagingBean)request.getAttribute("paging1");
PagingBean pageBean2=(PagingBean)request.getAttribute("paging2");
PagingBean pageBean3=(PagingBean)request.getAttribute("paging3");
PagingBean pageBean4=(PagingBean)request.getAttribute("paging4");
PagingBean pageBean5=(PagingBean)request.getAttribute("paging5");
PagingBean pageBean6=(PagingBean)request.getAttribute("paging6");
String orderCount=request.getAttribute("orderCount").toString();
DecimalFormat dcmFmt = new DecimalFormat("0.00");
%>
<script type="text/javascript">
function submitMailingBalanceAuditing(id){
	window.location="<%=request.getContextPath()%>/admin/mailingBalance.do?method=submitMailingBalanceAuditing&id="+id+"&page=mailingBatch&balanceType=7";
}
function deleteBatch(id){
	window.location="<%=request.getContextPath()%>/admin/mailingBalance.do?method=deleteMailingBalanceAuditing&id="+id+"&page=mailingBatch&balanceType=7";
}
function deletePackage(id){
	window.location="<%=request.getContextPath()%>/admin/mailingBatch.do?method=deleteMailingBalancePackage&id="+id+"&balanceType=7";
}
function showPackage(id){
	$("#"+id).slideToggle("slow");
}
function selectstatus(){
	var sta=document.getElementById("sta").value;
	document.getElementById("list1").style.display="none";
	document.getElementById("list2").style.display="none";
	document.getElementById("list3").style.display="none";
	document.getElementById("list4").style.display="none";
	document.getElementById("list5").style.display="none";
	document.getElementById("list6").style.display="none";
	
	document.getElementById("list"+sta).style.display="block";
}
function focusKeyword(){
	var keyword=document.getElementById("keyword");
	if(keyword.value=="请输入关键字..."){
		keyword.value="";
	}
	var condition=document.getElementById("condition");
	if(condition.value=="3"){
		SelectDate(keyword,'yyyy-MM-dd');
	}
}
function blurKeyword(){
	var keyword=document.getElementById("keyword");
	if(keyword.value==""){
		keyword.value="请输入关键字...";
	}
}
function check(){
	var keyword=document.getElementById("keyword");
	if(keyword.value==""||keyword.value=="请输入关键字..."){
		alert("请输入查询条件！");
		return false;
	}
	var condition=document.getElementById("condition");
	if(condition.value==""){
		alert("请选择查询条件！");
		return false;
	}
}
</script>
</head>
<body bgcolor="#FFCC00">
<table>
	<tr>
		<td>归属物流渠道：<%=MailingBalanceBean.getBalanceTypeMap().get(Integer.valueOf(request.getParameter("balanceType"))) %></td>
		<td>已提交财务：<%=mbaList3.size() %></td>
		<td>未提交财务：<%=mbaList2.size() %></td>
		<td>审核未通过：<%=mbaList4.size() %></td>
	</tr>
	<tr>
		<td>已结算：<%=mbaList5.size() %></td>
		<td>已作废：<%=mbaList6.size() %></td>
		<td colspan="2">未申请结算订单：<%=orderCount %>&nbsp;&nbsp;&nbsp;&nbsp;
			<%if(group.isFlag(478)){ %>
			<a href="<%=request.getContextPath()%>/admin/mailingBatch.do?method=unBalanceOrderList">查看未结算订单列表</a>
			<%} %>
		</td>
	</tr>
</table>
<hr/>
<form action="<%=request.getContextPath()%>/admin/mailingBatch.do?method=mailingBalanceAuditingList" method="post">
	<select id="sta" onchange="selectstatus();">
		<option value="1">全部状态</option>
		<option value="2">未提交至财务</option>
		<option value="3">已提交至财务</option>
		<option value="4">审核未通过</option>
		<option value="5">已结算</option>
		<option value="6">已作废</option>
	</select>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<%if(group.isFlag(479)){ %>
	<input type="text" id="keyword" name="keyword" value="请输入关键字..."  onfocus="focusKeyword();" onblur="blurKeyword();">&nbsp;&nbsp;
	<select id="condition" name="condition">
		<option value="">查询条件</option>
		<option value="1">发货波次号</option>
		<option value="2">订单编号</option>
		<option value="3">入库日期</option>
		<option value="4">投递员姓名</option>
		<option value="5">接货人账号</option>
		<option value="6">发货仓</option>
	</select>
	<input type="submit" value="查询" onclick="return check();">
	<input type="hidden" name="balanceType" value='<%=request.getParameter("balanceType") %>'/>
	<%} %>
</form>
<%if(request.getParameter("keyword")!=null){ %>
	<script type="text/javascript">
		document.getElementById("keyword").value='<%=request.getParameter("keyword")%>';
	</script>
<%} %>
<%if(request.getParameter("condition")!=null){ %>
	<script type="text/javascript">
		selectOption(document.getElementById("condition"),'<%=request.getParameter("condition")%>');
	</script>
<%} %>
<div id="list1" style="display:block;">
<table width="80%">
  <%for(int i=0;i<mbaList.size();i++){ %>
  	<%MailingBalanceAuditingBean mba=(MailingBalanceAuditingBean)mbaList.get(i); %>
  <tr>
  	<td bgcolor="#FFFFCC">
  		<table style="border-style: solid;border-color: black;" width="100%">
  			<tr>
  				<td><strong>结算批次号：<font color="blue"><%=mba.getCode() %></font></strong></td>
  				<td><a href="javascript:showPackage('package1<%=mba.getId() %>');"><font color="blue">查看明细</font></a></td>
  				<td>结算状态：<%=mba.getStatusName() %></td>
  				<td><strong>归属物流：</strong><%=MailingBalanceBean.getBalanceTypeMap().get(Integer.valueOf(request.getParameter("balanceType"))) %></td>
  				<td><%if((mba.getStatus()==0||mba.getStatus()==2)&&group.isFlag(480)){ %><input type="button" value="确认提交至财务" onclick="submitMailingBalanceAuditing(<%=mba.getId() %>);"/><%} %></td>
  			</tr>
  			<tr>
  				
  				<td><strong>订单包裹数量：<font color="blue"><span id="packageCount0-<%=mba.getId() %>"><%=mba.getPackageList()==null?"0":mba.getPackageList().size()%></span></font></strong></td>
  				<td><strong>代收货款总额：<font color="blue"><span id="totalPrice0-<%=mba.getId() %>"><%=dcmFmt.format(mba.getShouldPay()) %></span></font></strong></td>
  				<td><strong>支付方式：<font color="blue">
  				<% if(mba.getPayType()==0){%>
  					<span id="totalPrice0-<%=mba.getId() %>">未选择</span></font></strong></td>
  				<%}else if(mba.getPayType()==1){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">现金支付</span></font></strong></td>
  				<%}else if(mba.getPayType()==2){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">pos机刷卡</span></font></strong></td>
  				<%} %></span></font></strong></td>
  				<td><strong><font color="blue"></span></font></strong></td>
  				<td><%if((mba.getStatus()==0||mba.getStatus()==1||mba.getStatus()==2)&&group.isFlag(481)){ %><input type="button" value="作废本批结算" onclick="deleteBatch('<%=mba.getId() %>')"><%} %></td>
  				
  			</tr>
  			<tr>
  				<td colspan="4">
  					<div id="package1<%=mba.getId() %>" style="display:none">
  						<hr/>
  						<%if(group.isFlag(482)){ %><div align="right"><a href="mailingBatch.do?method=printBalanceCount&mbaId=<%=mba.getId() %>" target="_blank"><strong><font color="blue">打印财务对账单</font></strong></a></div><%} %>
  						<table border="1" bordercolor="#00000" cellspacing="0" width="100%" >
  							<tr bgcolor="#00ccff" align="center">
  								<td>序号</td>
  								<td>订单号</td>
  								<td>添加时间</td>
  								<td>归属物流</td>
  								<td>收件地址</td>
  								<td>订单总价</td>
  								<td>包裹重量</td>
  								<td>操作</td>
  							</tr>
  							<%List packageList=mba.getPackageList(); %>
  							<%for(int j=0;j<packageList.size();j++){ %>
  								<%MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j); %>
  							<tr bgcolor="#ffffff" align="center">
  								<td><%=j+1 %></td>
  								<td><%if(group.isFlag(441)){ %><a href="<%=request.getContextPath()%>/admin/order.do?id=<%=packageBean.getOrderId() %>" target="_blank"><%=packageBean.getOrderCode() %></a><%}else{ %><%=packageBean.getOrderCode() %><%} %></td>
  								<td><%=packageBean.getCreateDatetime().substring(0,19) %></td>
  								<td><%=voOrder.deliverMapAll.get(packageBean.getDeliver()+"") %></td>
  								<td><%=packageBean.getAddress().indexOf("自治区")>0?packageBean.getAddress().substring(0,12):packageBean.getAddress().substring(0,8) %></td>
  								<td><%=dcmFmt.format(packageBean.getTotalPrice()) %></td>
  								<td><%=packageBean.getWeight()/1000 %>KG</td>
  								<td>
  									<%if((mba.getStatus()==0||mba.getStatus()==2)&&group.isFlag(483)){ %>
  										<a href="javascript:deletePackage(<%=packageBean.getId() %>);">删除</a>
  									<%} %>
  								</td>
  							</tr>
  							<%} %>
  						</table>
  					</div>
  				</td>
  			</tr>
  		</table>
  	</td>
  </tr>
  <%} %>
  <tr>
  	<td>
  	<%if(pageBean1!=null){ %>
		<p align="center"><%=PageUtil.fenye(pageBean1, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
<%} %>
  	</td>
  </tr>
</table>
</div>
<div id="list2" style="display:none;">
<table width="80%">
  <%for(int i=0;i<mbaList2.size();i++){ %>
  	<%MailingBalanceAuditingBean mba=(MailingBalanceAuditingBean)mbaList2.get(i); %>
  <tr>
  	<td bgcolor="#FFFFCC">
  		<table style="border-style: solid;border-color: black;" width="100%">
  			<tr>
  				<td><strong>结算批次号：<font color="blue"><%=mba.getCode() %></font></strong></td>
  				<td><a href="javascript:showPackage('package2<%=mba.getId() %>');"><font color="blue">查看明细</font></a></td>
  				<td>结算状态：<%=mba.getStatusName() %></td>
  				<td><strong>归属物流：</strong><%=MailingBalanceBean.getBalanceTypeMap().get(Integer.valueOf(request.getParameter("balanceType"))) %></td>
  				<td><%if((mba.getStatus()==0||mba.getStatus()==2)&&group.isFlag(480)){ %><input type="button" value="确认提交至财务" onclick="submitMailingBalanceAuditing(<%=mba.getId() %>);"/><%} %></td>
  			</tr>
  			<tr>
  				<td><strong>订单包裹数量：<font color="blue"><span id="packageCount0-<%=mba.getId() %>"><%=mba.getPackageList()==null?"0":mba.getPackageList().size()%></span></font></strong></td>
  				<td><strong>代收货款总额：<font color="blue"><span id="totalPrice0-<%=mba.getId() %>"><%=dcmFmt.format(mba.getShouldPay()) %></span></font></strong></td>
  				<td><strong>支付方式：<font color="blue">
  				<% if(mba.getPayType()==0){%>
  					<span id="totalPrice0-<%=mba.getId() %>">未选择</span></font></strong></td>
  				<%}else if(mba.getPayType()==1){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">现金支付</span></font></strong></td>
  				<%}else if(mba.getPayType()==2){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">pos机刷卡</span></font></strong></td>
  				<%} %></span></font></strong></td>
  				<td><strong><font color="blue"></span></font></strong></td>
  				<td><%if((mba.getStatus()==0||mba.getStatus()==1||mba.getStatus()==2)&&group.isFlag(481)){ %><input type="button" value="作废本批结算" onclick="deleteBatch('<%=mba.getId() %>')"><%} %></td>
  			</tr>
  			<tr>
  				<td colspan="4">
  					<div id="package2<%=mba.getId() %>" style="display:none">
  						<hr/>
  						<%if(group.isFlag(482)){ %><div align="right"><a href="mailingBatch.do?method=printBalanceCount&mbaId=<%=mba.getId() %>" target="_blank"><strong><font color="blue">打印财务对账单</font></strong></a></div><%} %>
  						<table border="1" bordercolor="#00000" cellspacing="0" width="100%" >
  							<tr bgcolor="#00ccff" align="center">
  								<td>序号</td>
  								<td>订单号</td>
  								<td>添加时间</td>
  								<td>归属物流</td>
  								<td>收件地址</td>
  								<td>订单总价</td>
  								<td>包裹重量</td>
  								<td>操作</td>
  							</tr>
  							<%List packageList=mba.getPackageList(); %>
  							<%for(int j=0;j<packageList.size();j++){ %>
  								<%MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j); %>
  							<tr bgcolor="#ffffff" align="center">
  								<td><%=j+1 %></td>
  								<td><%if(group.isFlag(441)){ %><a href="<%=request.getContextPath()%>/admin/order.do?id=<%=packageBean.getOrderId() %>" target="_blank"><%=packageBean.getOrderCode() %></a><%}else{ %><%=packageBean.getOrderCode() %><%} %></td>
  								<td><%=packageBean.getCreateDatetime().substring(0,19) %></td>
  								<td><%=voOrder.deliverMapAll.get(packageBean.getDeliver()+"") %></td>
  								<td><%=packageBean.getAddress().indexOf("自治区")>0?packageBean.getAddress().substring(0,12):packageBean.getAddress().substring(0,8) %></td>
  								<td><%=dcmFmt.format(packageBean.getTotalPrice()) %></td>
  								<td><%=packageBean.getWeight()/1000 %>KG</td>
  								<td>
  									<%if((mba.getStatus()==0||mba.getStatus()==2)&&group.isFlag(483)){ %>
  										<a href="javascript:deletePackage(<%=packageBean.getId() %>);">删除</a>
  									<%} %>
  								</td>
  							</tr>
  							<%} %>
  						</table>
  					</div>
  				</td>
  			</tr>
  		</table>
  	</td>
  </tr>
  <%} %>
   <tr>
  	<td>
  	<%if(pageBean2!=null){ %>
		<p align="center"><%=PageUtil.fenye(pageBean2, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
<%} %>
  	</td>
  </tr>
</table>
</div>
<div id="list3" style="display:none;">
<table width="80%">
  <%for(int i=0;i<mbaList3.size();i++){ %>
  	<%MailingBalanceAuditingBean mba=(MailingBalanceAuditingBean)mbaList3.get(i); %>
  <tr>
  	<td bgcolor="#FFFFCC">
  		<table style="border-style: solid;border-color: black;" width="100%">
  			<tr>
  				<td><strong>结算批次号：<font color="blue"><%=mba.getCode() %></font></strong></td>
  				<td><a href="javascript:showPackage('package3<%=mba.getId() %>');"><font color="blue">查看明细</font></a></td>
  				<td>结算状态：<%=mba.getStatusName() %></td>
  				<td><strong>归属物流：</strong><%=MailingBalanceBean.getBalanceTypeMap().get(Integer.valueOf(request.getParameter("balanceType"))) %></td>
  				<td><%if((mba.getStatus()==0||mba.getStatus()==2)&&group.isFlag(480)){ %><input type="button" value="确认提交至财务" onclick="submitMailingBalanceAuditing(<%=mba.getId() %>);"/><%} %></td>
  			</tr>
  			<tr>
  				<td><strong>订单包裹数量：<font color="blue"><span id="packageCount0-<%=mba.getId() %>"><%=mba.getPackageList()==null?"0":mba.getPackageList().size()%></span></font></strong></td>
  				<td><strong>代收货款总额：<font color="blue"><span id="totalPrice0-<%=mba.getId() %>"><%=dcmFmt.format(mba.getShouldPay()) %></span></font></strong></td>
  				<td><strong>支付方式：<font color="blue">
  				<% if(mba.getPayType()==0){%>
  					<span id="totalPrice0-<%=mba.getId() %>">未选择</span></font></strong></td>
  				<%}else if(mba.getPayType()==1){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">现金支付</span></font></strong></td>
  				<%}else if(mba.getPayType()==2){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">pos机刷卡</span></font></strong></td>
  				<%} %></span></font></strong></td>
  				<td><strong><font color="blue"></span></font></strong></td>
  				<td><%if((mba.getStatus()==0||mba.getStatus()==1||mba.getStatus()==2)&&group.isFlag(481)){ %><input type="button" value="作废本批结算" onclick="deleteBatch('<%=mba.getId() %>')"><%} %></td>
  			</tr>
  			<tr>
  				<td colspan="4">
  					<div id="package3<%=mba.getId() %>" style="display:none">
  						<hr/>
  						<%if(group.isFlag(482)){ %><div align="right"><a href="mailingBatch.do?method=printBalanceCount&mbaId=<%=mba.getId() %>" target="_blank"><strong><font color="blue">打印财务对账单</font></strong></a></div><%} %>
  						<table border="1" bordercolor="#00000" cellspacing="0" width="100%" >
  							<tr bgcolor="#00ccff" align="center">
  								<td>序号</td>
  								<td>订单号</td>
  								<td>添加时间</td>
  								<td>归属物流</td>
  								<td>收件地址</td>
  								<td>订单总价</td>
  								<td>包裹重量</td>
  								<td>操作</td>
  							</tr>
  							<%List packageList=mba.getPackageList(); %>
  							<%for(int j=0;j<packageList.size();j++){ %>
  								<%MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j); %>
  							<tr bgcolor="#ffffff" align="center">
  								<td><%=j+1 %></td>
  								<td><%if(group.isFlag(441)){ %><a href="<%=request.getContextPath()%>/admin/order.do?id=<%=packageBean.getOrderId() %>" target="_blank"><%=packageBean.getOrderCode() %></a><%}else{ %><%=packageBean.getOrderCode() %><%} %></td>
  								<td><%=packageBean.getCreateDatetime().substring(0,19) %></td>
  								<td><%=voOrder.deliverMapAll.get(packageBean.getDeliver()+"") %></td>
  								<td><%=packageBean.getAddress().indexOf("自治区")>0?packageBean.getAddress().substring(0,12):packageBean.getAddress().substring(0,8) %></td>
  								<td><%=dcmFmt.format(packageBean.getTotalPrice()) %></td>
  								<td><%=packageBean.getWeight()/1000 %>KG</td>
  								<td>
  									<%if((mba.getStatus()==0||mba.getStatus()==2)&&group.isFlag(483)){ %>
  										<a href="javascript:deletePackage(<%=packageBean.getId() %>);">删除</a>
  									<%} %>
  								</td>
  							</tr>
  							<%} %>
  						</table>
  					</div>
  				</td>
  			</tr>
  		</table>
  	</td>
  </tr>
  <%} %>
   <tr>
  	<td>
  	<%if(pageBean3!=null){ %>
		<p align="center"><%=PageUtil.fenye(pageBean3, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
<%} %>
  	</td>
  </tr>
</table>
</div>
<div id="list4" style="display:none;">
<table width="80%">
  <%for(int i=0;i<mbaList4.size();i++){ %>
  	<%MailingBalanceAuditingBean mba=(MailingBalanceAuditingBean)mbaList4.get(i); %>
  <tr>
  	<td bgcolor="#FFFFCC">
  		<table style="border-style: solid;border-color: black;" width="100%">
  			<tr>
  				<td><strong>结算批次号：<font color="blue"><%=mba.getCode() %></font></strong></td>
  				<td><a href="javascript:showPackage('package4<%=mba.getId() %>');"><font color="blue">查看明细</font></a></td>
  				<td>结算状态：<%=mba.getStatusName() %></td>
  				<td><strong>归属物流：</strong><%=MailingBalanceBean.getBalanceTypeMap().get(Integer.valueOf(request.getParameter("balanceType"))) %></td>
  				<td><%if((mba.getStatus()==0||mba.getStatus()==2)&&group.isFlag(480)){ %><input type="button" value="确认提交至财务" onclick="submitMailingBalanceAuditing(<%=mba.getId() %>);"/><%} %></td>
  			</tr>
  			<tr>
  				<td><strong>订单包裹数量：<font color="blue"><span id="packageCount0-<%=mba.getId() %>"><%=mba.getPackageList()==null?"0":mba.getPackageList().size()%></span></font></strong></td>
  				<td><strong>代收货款总额：<font color="blue"><span id="totalPrice0-<%=mba.getId() %>"><%=dcmFmt.format(mba.getShouldPay()) %></span></font></strong></td>
  				<td><strong>支付方式：<font color="blue">
  				<% if(mba.getPayType()==0){%>
  					<span id="totalPrice0-<%=mba.getId() %>">未选择</span></font></strong></td>
  				<%}else if(mba.getPayType()==1){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">现金支付</span></font></strong></td>
  				<%}else if(mba.getPayType()==2){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">pos机刷卡</span></font></strong></td>
  				<%} %></span></font></strong></td>
  				<td><strong><font color="blue"></span></font></strong></td>
  				<td><%if((mba.getStatus()==0||mba.getStatus()==1||mba.getStatus()==2)&&group.isFlag(481)){ %><input type="button" value="作废本批结算" onclick="deleteBatch('<%=mba.getId() %>')"><%} %></td>
  			</tr>
  			<tr>
  				<td colspan="4">
  					<div id="package4<%=mba.getId() %>" style="display:none">
  						<hr/>
  						<%if(group.isFlag(482)){ %><div align="right"><a href="mailingBatch.do?method=printBalanceCount&mbaId=<%=mba.getId() %>" target="_blank"><strong><font color="blue">打印财务对账单</font></strong></a></div><%} %>
  						<table border="1" bordercolor="#00000" cellspacing="0" width="100%" >
  							<tr bgcolor="#00ccff" align="center">
  								<td>序号</td>
  								<td>订单号</td>
  								<td>添加时间</td>
  								<td>归属物流</td>
  								<td>收件地址</td>
  								<td>订单总价</td>
  								<td>包裹重量</td>
  								<td>操作</td>
  							</tr>
  							<%List packageList=mba.getPackageList(); %>
  							<%for(int j=0;j<packageList.size();j++){ %>
  								<%MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j); %>
  							<tr bgcolor="#ffffff" align="center">
  								<td><%=j+1 %></td>
  								<td><%if(group.isFlag(441)){ %><a href="<%=request.getContextPath()%>/admin/order.do?id=<%=packageBean.getOrderId() %>" target="_blank"><%=packageBean.getOrderCode() %></a><%}else{ %><%=packageBean.getOrderCode() %><%} %></td>
  								<td><%=packageBean.getCreateDatetime().substring(0,19) %></td>
  								<td><%=voOrder.deliverMapAll.get(packageBean.getDeliver()+"") %></td>
  								<td><%=packageBean.getAddress().indexOf("自治区")>0?packageBean.getAddress().substring(0,12):packageBean.getAddress().substring(0,8) %></td>
  								<td><%=dcmFmt.format(packageBean.getTotalPrice()) %></td>
  								<td><%=packageBean.getWeight()/1000 %>KG</td>
  								<td>
  									<%if((mba.getStatus()==0||mba.getStatus()==2)&&group.isFlag(483)){ %>
  										<a href="javascript:deletePackage(<%=packageBean.getId() %>);">删除</a>
  									<%} %>
  								</td>
  							</tr>
  							<%} %>
  						</table>
  					</div>
  				</td>
  			</tr>
  		</table>
  	</td>
  </tr>
  <%} %>
   <tr>
  	<td>
  	<%if(pageBean4!=null){ %>
		<p align="center"><%=PageUtil.fenye(pageBean4, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
<%} %>
  	</td>
  </tr>
</table>
</div>
<div id="list5" style="display:none;">
<table width="80%">
  <%for(int i=0;i<mbaList5.size();i++){ %>
  	<%MailingBalanceAuditingBean mba=(MailingBalanceAuditingBean)mbaList5.get(i); %>
  <tr>
  	<td bgcolor="#FFFFCC">
  		<table style="border-style: solid;border-color: black;" width="100%">
  			<tr>
  				<td><strong>结算批次号：<font color="blue"><%=mba.getCode() %></font></strong></td>
  				<td><a href="javascript:showPackage('package5<%=mba.getId() %>');"><font color="blue">查看明细</font></a></td>
  				<td>结算状态：<%=mba.getStatusName() %></td>
  				<td><strong>归属物流：</strong><%=MailingBalanceBean.getBalanceTypeMap().get(Integer.valueOf(request.getParameter("balanceType"))) %></td>
  				<td><%if((mba.getStatus()==0||mba.getStatus()==2)&&group.isFlag(480)){ %><input type="button" value="确认提交至财务" onclick="submitMailingBalanceAuditing(<%=mba.getId() %>);"/><%} %></td>
  			</tr>
  			<tr>
  				<td><strong>订单包裹数量：<font color="blue"><span id="packageCount0-<%=mba.getId() %>"><%=mba.getPackageList()==null?"0":mba.getPackageList().size()%></span></font></strong></td>
  				<td><strong>代收货款总额：<font color="blue"><span id="totalPrice0-<%=mba.getId() %>"><%=dcmFmt.format(mba.getShouldPay()) %></span></font></strong></td>
  				<td><strong>支付方式：<font color="blue">
  				<% if(mba.getPayType()==0){%>
  					<span id="totalPrice0-<%=mba.getId() %>">未选择</span></font></strong></td>
  				<%}else if(mba.getPayType()==1){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">现金支付</span></font></strong></td>
  				<%}else if(mba.getPayType()==2){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">pos机刷卡</span></font></strong></td>
  				<%} %></span></font></strong></td>
  				<td><strong><font color="blue"></span></font></strong></td>
  				<td><%if((mba.getStatus()==0||mba.getStatus()==1||mba.getStatus()==2)&&group.isFlag(481)){ %><input type="button" value="作废本批结算" onclick="deleteBatch('<%=mba.getId() %>')"><%} %></td>
  			</tr>
  			<tr>
  				<td colspan="4">
  					<div id="package5<%=mba.getId() %>" style="display:none">
  						<hr/>
  						<%if(group.isFlag(482)){ %><div align="right"><a href="<%=request.getContextPath()%>/admin/mailingBatch.do?method=printBalanceCount&mbaId=<%=mba.getId() %>" target="_blank"><strong><font color="blue">打印财务对账单</font></strong></a></div><%} %>
  						<table border="1" bordercolor="#00000" cellspacing="0" width="100%" >
  							<tr bgcolor="#00ccff" align="center">
  								<td>序号</td>
  								<td>订单号</td>
  								<td>添加时间</td>
  								<td>归属物流</td>
  								<td>收件地址</td>
  								<td>订单总价</td>
  								<td>包裹重量</td>
  								<td>操作</td>
  							</tr>
  							<%List packageList=mba.getPackageList(); %>
  							<%for(int j=0;j<packageList.size();j++){ %>
  								<%MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j); %>
  							<tr bgcolor="#ffffff" align="center">
  								<td><%=j+1 %></td>
  								<td><%if(group.isFlag(441)){ %><a href="<%=request.getContextPath()%>/admin/order.do?id=<%=packageBean.getOrderId() %>" target="_blank"><%=packageBean.getOrderCode() %></a><%}else{ %><%=packageBean.getOrderCode() %><%} %></td>
  								<td><%=packageBean.getCreateDatetime().substring(0,19) %></td>
  								<td><%=voOrder.deliverMapAll.get(packageBean.getDeliver()+"") %></td>
  								<td><%=packageBean.getAddress().indexOf("自治区")>0?packageBean.getAddress().substring(0,12):packageBean.getAddress().substring(0,8) %></td>
  								<td><%=dcmFmt.format(packageBean.getTotalPrice()) %></td>
  								<td><%=packageBean.getWeight()/1000 %>KG</td>
  								<td>
  									<%if((mba.getStatus()==0||mba.getStatus()==2)&&group.isFlag(483)){ %>
  										<a href="javascript:deletePackage(<%=packageBean.getId() %>);">删除</a>
  									<%} %>
  								</td>
  							</tr>
  							<%} %>
  						</table>
  					</div>
  				</td>
  			</tr>
  		</table>
  	</td>
  </tr>
  <%} %>
   <tr>
  	<td>
  	<%if(pageBean5!=null){ %>
		<p align="center"><%=PageUtil.fenye(pageBean5, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
<%} %>
  	</td>
  </tr>
</table>
</div>
<div id="list6" style="display:none;">
<table width="80%">
  <%for(int i=0;i<mbaList6.size();i++){ %>
  	<%MailingBalanceAuditingBean mba=(MailingBalanceAuditingBean)mbaList6.get(i); %>
  <tr>
  	<td bgcolor="#FFFFCC">
  		<table style="border-style: solid;border-color: black;" width="100%">
  			<tr>
  				<td><strong>结算批次号：<font color="blue"><%=mba.getCode() %></font></strong></td>
  				<td><a href="javascript:showPackage('package6<%=mba.getId() %>');"><font color="blue">查看明细</font></a></td>
  				<td>结算状态：<%=mba.getStatusName() %></td>
  				<td><strong>归属物流：</strong><%=MailingBalanceBean.getBalanceTypeMap().get(Integer.valueOf(request.getParameter("balanceType"))) %></td>
  				<td><%if((mba.getStatus()==0||mba.getStatus()==2)&&group.isFlag(480)){ %><input type="button" value="确认提交至财务" onclick="submitMailingBalanceAuditing(<%=mba.getId() %>);"/><%} %></td>
  			</tr>
  			<tr>
  				<td><strong>订单包裹数量：<font color="blue"><span id="packageCount0-<%=mba.getId() %>"><%=mba.getPackageList()==null?"0":mba.getPackageList().size()%></span></font></strong></td>
  				<td><strong>代收货款总额：<font color="blue"><span id="totalPrice0-<%=mba.getId() %>"><%=dcmFmt.format(mba.getShouldPay()) %></span></font></strong></td>
  				<td><strong>支付方式：<font color="blue">
  				<% if(mba.getPayType()==0){%>
  					<span id="totalPrice0-<%=mba.getId() %>">未选择</span></font></strong></td>
  				<%}else if(mba.getPayType()==1){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">现金支付</span></font></strong></td>
  				<%}else if(mba.getPayType()==2){ %>
  					<span id="totalPrice0-<%=mba.getId() %>">pos机刷卡</span></font></strong></td>
  				<%} %></span></font></strong></td>
  				<td><strong><font color="blue"></span></font></strong></td>
  				<td><%if((mba.getStatus()==0||mba.getStatus()==1||mba.getStatus()==2)&&group.isFlag(481)){ %><input type="button" value="作废本批结算" onclick="deleteBatch('<%=mba.getId() %>')"><%} %></td>
  			</tr>
  			<tr>
  				<td colspan="4">
  					<div id="package6<%=mba.getId() %>" style="display:none">
  						<hr/>
  						<%if(group.isFlag(482)){ %><div align="right"><a href="mailingBatch.do?method=printBalanceCount&mbaId=<%=mba.getId() %>" target="_blank"><strong><font color="blue">打印财务对账单</font></strong></a></div><%} %>
  						<table border="1" bordercolor="#00000" cellspacing="0" width="100%" >
  							<tr bgcolor="#00ccff" align="center">
  								<td>序号</td>
  								<td>订单号</td>
  								<td>添加时间</td>
  								<td>归属物流</td>
  								<td>收件地址</td>
  								<td>订单总价</td>
  								<td>包裹重量</td>
  								<td>操作</td>
  							</tr>
  							<%List packageList=mba.getPackageList(); %>
  							<%for(int j=0;j<packageList.size();j++){ %>
  								<%MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j); %>
  							<tr bgcolor="#ffffff" align="center">
  								<td><%=j+1 %></td>
  								<td><%if(group.isFlag(441)){ %><a href="<%=request.getContextPath()%>/admin/order.do?id=<%=packageBean.getOrderId() %>" target="_blank"><%=packageBean.getOrderCode() %></a><%}else{ %><%=packageBean.getOrderCode() %><%} %></td>
  								<td><%=packageBean.getCreateDatetime().substring(0,19) %></td>
  								<td><%=voOrder.deliverMapAll.get(packageBean.getDeliver()+"") %></td>
  								<td><%=packageBean.getAddress().indexOf("自治区")>0?packageBean.getAddress().substring(0,12):packageBean.getAddress().substring(0,8) %></td>
  								<td><%=dcmFmt.format(packageBean.getTotalPrice()) %></td>
  								<td><%=packageBean.getWeight()/1000 %>KG</td>
  								<td>
  									<%if((mba.getStatus()==0||mba.getStatus()==2)&&group.isFlag(483)){ %>
  										<a href="javascript:deletePackage(<%=packageBean.getId() %>);">删除</a>
  									<%} %>
  								</td>
  							</tr>
  							<%} %>
  						</table>
  					</div>
  				</td>
  			</tr>
  		</table>
  	</td>
  </tr>
  <%} %>
   <tr>
  	<td>
  	<%if(pageBean6!=null){ %>
		<p align="center"><%=PageUtil.fenye(pageBean6, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
<%} %>
  	</td>
  </tr>
</table>
</div>
<%if(request.getParameter("select")!=null){%>
	<script type="text/javascript">
	selectOption(document.getElementById("sta"),'<%=request.getParameter("select")%>');
	selectstatus();
	blurKeyword();
	</script>
<%}%>
</body>
</html>
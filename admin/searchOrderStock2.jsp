<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.stock.*"%>
<%@ page import="adultadmin.bean.order.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.bean.*" %>
<%
	response.setHeader("Cache-Control","no-cache");
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();

	boolean isSystem = (user.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (user.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (user.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (user.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (user.getPermission() == 7);	//销售部
	boolean isShangpin = (user.getPermission() == 6);	//商品部
	boolean isTuiguang = (user.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (user.getPermission() == 4);	//运营中心
	boolean isKefu = (user.getPermission() == 3);	//客服部

	voOrder vo = null;

	String areano = StringUtil.convertNull(request.getParameter("areano"));
	String buymode = StringUtil.convertNull(request.getParameter("buymode"));
	String stockState = StringUtil.convertNull(request.getParameter("stockState"));
	String action = StringUtil.convertNull(request.getParameter("action"));
	String printType = StringUtil.convertNull(request.getParameter("printType"));
	String now = DateUtil.getNow().substring(0, 10);
	String startDate = StringUtil.convertNull(request.getParameter("startDate"));
	String endDate = StringUtil.convertNull(request.getParameter("endDate"));
	String orderCount = (String)request.getAttribute("orderCount");
	String hasDPT = StringUtil.convertNull(request.getParameter("hasDPT"));
	int sdeliver = StringUtil.StringToId(request.getParameter("sdeliver"));
	int savePercent = StringUtil.toInt(request.getParameter("savePercent"));
	int sortType = StringUtil.toInt(request.getParameter("sortType"));
	Map deliverMapAll = voOrder.deliverMapAll;
%>
<html>
<title>买卖宝后台</title>
<script type="text/javascript" src="js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script type="text/javascript">
function updateDPTSelected(uForm){
	uForm.action = "mOrders.do?updateAction=selected";
	uForm.target = "";
	uForm.submit();
}
function updateDPTAll(uForm){
	uForm.action = "mOrders.do?updateAction=all";
	uForm.target = "";
	uForm.submit();
}
//打印选定提交
function printSelected(){
	var arrID = document.getElementsByName("id");
	if(arrID.length==0){alert("还没有单子，请先查找！");return ;}
	var lengthNum = arrID.length;
	for(var i=0;i<lengthNum;i+=1){
		if(arrID[i].checked){
			break;
		}else if(i==lengthNum-1){
			alert("请至少选择一个订单！");
			return ;
		}
	}
	document.getElementById('sum_action').value='print'; 
	document.getElementById('linePrintId2').value='lineprint';
	document.forms[2].submit();
}

function typeChangeHandle(obj){
	var printSelObj = document.getElementById("printSelBtn");
	var printBtnObj = document.getElementById("printBtn");
	if(obj.value==0){
		printBtnObj.value="导  出";
		printSelObj.value="导出选定";
		printBtnObj.onclick=function outOrderHandler(){
			document.getElementById('printAction').value='print';
			document.getElementById('linePrintId').value=''; 
			this.form.target='_blank'; this.form.submit();
		};
		printSelObj.onclick=function outselOrderHandler(){
			document.getElementById('sum_action').value='print';
			document.getElementById('linePrintId').value=''; 
			document.forms[2].target='_blank'; document.forms[2].submit();
		};
	}else{
		printBtnObj.value="打  印";
		printSelObj.value="打印选定";
		printBtnObj.onclick=function onclickHandler(){
			document.getElementById('printAction').value='print'; 
			document.getElementById('linePrintId').value='lineprint'; 
			this.form.target='_blank'; this.form.submit();
		};
		printSelObj.onclick=function printSelclick(){printSelected()};
	}
}

function isSubmit(){
	var ordersDeliver = document.getElementById("ordersDeliver");
	if(ordersDeliver.value==""){
		alert("订单号和快递信息不能为空！");
		ordersDeliver.focus();
		return false;
	}
	return true;
	
}

</script>
<logic:present name="tip"  scope="request">
	<script>alert('<%=request.getAttribute("tip")%>');</script>
</logic:present>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body onload="typeChangeHandle(document.getElementById('printType'));">
<%@include file="../header.jsp"%>
<table width="99%" cellpadding="0" cellspacing="5" bgcolor="#E8E8E8" align=center><tr><td>
<table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
<tr bgcolor="#F8F8F8"><td>
<form method=post action="searchOrderStock2.do"><table>
<tr><td>
地点：</td><td>
<select id="areano" name="areano">
<%if(group.isFlag(32)){ %>
	<option value="-1">全部</option>
<%} %>
	<option value="0">北京</option>
	<option value="1" selected>芳村</option>
	<option value="2">广速</option>
	<option value="3" selected>增城</option>
</select>
<script type="text/javascript">
	selectOption(document.getElementById('areano'), "<%= areano %>");
</script>
</td></tr>
<tr><td>
快递公司：</td><td>

			<select name="sdeliver" id="sdeliver">
				<option value="0">全部</option>
				<%
				Iterator deliverIter = deliverMapAll.entrySet().iterator();
				while (deliverIter.hasNext()) {
					Map.Entry entry = (Map.Entry) deliverIter.next();
				%>
					<option value=<%=entry.getKey()%>><%=entry.getValue()%></option>
				<%}%>
			</select>
<script type="text/javascript">
	selectOption(document.getElementById('sdeliver'), "<%= sdeliver %>");
</script>
</td></tr>
<tr><td>
购买方式：</td><td><select id="buymode" name="buymode">
	<option value="-1"></option>
    <option value="2">银行汇款</option>
	<option value="0">货到付款</option>
	<option value="1">钱包支付</option>
	<option value="3">售后订单</option>
</select>
<script type="text/javascript">
	selectOption(document.getElementById('buymode'), "<%= buymode %>");
</script>
</td></tr>
<tr><td>
能否发货：</td><td><select id="stockState" name="stockState">
<%if(group.isFlag(32)) /*if(isSystem || isXiaoshou)*/{ %>
    <option value="-1"></option>
<%} %>
<%if(group.isFlag(32)) /*if(isSystem || isXiaoshou)*/{ %>
	<option value="0">否</option>
<%} %>
<%if(group.isFlag(14)) /*if(isSystem || isShangpin || isXiaoshou)*/{ %>
	<option value="1">是</option>
<%} %>
<%if(group.isFlag(0)) /*if(isSystem)*/{ %>
	<option value="2">已发货</option>
<%} %>
</select>
<script type="text/javascript">
	selectOption(document.getElementById('stockState'), "<%= stockState %>");
</script>
</td></tr>
<tr><td>
是否选择快递/分类:</td><td><select id="hasDPT" name="hasDPT">
	<option value="-1"></option>
	<option value="1">是</option>
	<option value="0">否</option>
</select>
<script type="text/javascript">
	selectOption(document.getElementById('hasDPT'), "<%= hasDPT %>");
</script>
</td></tr>
<tr><td>
导出方式：</td><td><select id="printType" name="printType" onchange="typeChangeHandle(this);">
	<option value="0">按地址</option>
</select>
<script type="text/javascript">
	selectOption(document.getElementById('printType'), "<%= printType %>");
</script>
</td></tr>
<%if(group.isFlag(0)) /*if(isSystem)*/{ %>
<tr><td>
时间：</td><td><input type=text name="startDate" size="20" value="<%=startDate%>" onclick="SelectDate(this,'yyyy-MM-dd');" readonly="readonly" />到<input type=text name="endDate" size="20" value="<%=endDate%>" onclick="SelectDate(this,'yyyy-MM-dd');" readonly="readonly" />
</td></tr>
<%} %>
<tr><td>
排序方式：</td><td><select id="sortType" name="sortType">
	<option value="0">确认申请出库时间</option>
	<option value="1">客户地址</option>
</select>
<script type="text/javascript">
	selectOption(document.getElementById('sortType'), "<%= sortType %>");
</script>
</td></tr>
<%--<tr><td>
比例值：</td><td><input type="text" name="savePercent" value="<%= (savePercent>0)?String.valueOf(savePercent):"" %>" size="3" />%</td></tr>--%>
</table>
<input type="hidden" id="linePrintId" name="linePrint" value=""/>
<input type="hidden" id="printAction" name="printAction" value="" />
<input type="button" value=" 查 询 " onclick="document.getElementById('printAction').value=''; this.form.target='_self'; this.form.submit();" />&nbsp;&nbsp;&nbsp;&nbsp;
<input id="printBtn" type="button" value=" 打 印 " onclick="document.getElementById('printAction').value='print'; document.getElementById('linePrintId').value='lineprint'; this.form.target='_blank'; this.form.submit();" />&nbsp;&nbsp;&nbsp;&nbsp;
<input type="button" value=" 汇 总 " onclick="document.getElementById('printAction').value='sum'; this.form.target='_blank'; this.form.submit();" />&nbsp;&nbsp;&nbsp;&nbsp;
</form>
<!-- 批量修改订单快递公司 -->
<form action="batchUOrderDeliver.do" name="batchUOrderDeliverForm" method="post" onsubmit="return isSubmit();">
	输入格式：(可从excel中复制两列，粘贴至下面输入框中)<br/>
	<div style="color: red;">QD090101249181(空格)广州宅急送<br/>D090101240066(空格)广东省外<br/>…	</div>
	<textarea rows="6" cols="40" name="ordersDeliver" id="ordersDeliver"><%=StringUtil.convertNull(request.getParameter("ordersDeliver")) %></textarea>
	<input type="submit" value="批量修改快递公司">
	<br/>
	<logic:present name="result" scope="request">
		<p><%=request.getAttribute("result")%></p>
	</logic:present>
</form>
</td></tr>
</table>
</td></tr></table>
          <br/>
<%if(orderCount != null){ %>查询结果<%= orderCount %>条<br/><%} %>
<form name="msellerCheckStatus" action="searchOrderStock2.do" target="_blank" method="post" >
<input type="hidden" id="linePrintId2" name="linePrint" value=""/>
<input type="hidden" id="sum_action" name="printAction" value="" />
<input type="hidden" name="printType" value="<%= StringUtil.convertNull(request.getParameter("printType")) %>" />
<input type="hidden" name="areano" value="<%= StringUtil.convertNull(request.getParameter("areano")) %>" />
<input type="hidden" name="stockState" value="<%= StringUtil.convertNull(request.getParameter("stockState")) %>" />
<%if(areano.equals("0") || areano.equals("1") || areano.equals("2") || areano.equals("3") || areano.equals("-1")){ %>
          <table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td width="10" align="center"><font color="#FFFFFF">&nbsp;</font></td>
              <td width="100" align="center"><font color="#FFFFFF">订单号</font></td>
              <td width="100" align="center"><font color="#FFFFFF">出库单号</font></td>
              <td width="60" align="center"><font color="#FFFFFF">收货人</font></td>
              <td width="120" align="center"><font color="#FFFFFF">产品名称</font></td>
              <%--
              <td width="100" align="center"><font color="#FFFFFF">电话</font></td>
              <td width="100" align="center"><font color="#FFFFFF">电话1</font></td>
              <td width="100" align="center"><font color="#FFFFFF">邮政编码</font></td>
              --%>
              <%if(stockState.equals("1") || stockState.equals("2")){ %>
              <td width="50" align="center"><font color="#FFFFFF">产品分类</font></td>
              <%} %>
              <td width="60" align="center"><font color="#FFFFFF">应收货款</font></td>
              <td align="center"><font color="#FFFFFF">客户地址</font></td>
              <td align="center"><font color="#FFFFFF">邮编</font></td>
              <td width="60" align="center"><font color="#FFFFFF">寄达<br/>地点</font></td>
              <td width="60" align="center"><font color="#FFFFFF">成品<br/>重量</font></td>
              <%--
              <td width="60" align="center"><font color="#FFFFFF">北速<br/>邮资</font></td>
              <td width="60" align="center"><font color="#FFFFFF">广速<br/>邮资</font></td>
              <td width="60" align="center"><font color="#FFFFFF">邮资<br/>差值</font></td>
              <td width="60" align="center"><font color="#FFFFFF">节约<br/>比例</font></td>
              --%>
              <td width="60" align="center"><font color="#FFFFFF">发货重量<br/>的平均值</font></td>
              <%if(stockState.equals("1") || stockState.equals("2")){ %>
              <td width="80" align="center"><font color="#FFFFFF">快递公司</font></td>
              <%} %>
              <td width="80" align="center"><font color="#FFFFFF">确认申请<br/>出库时间</font></td>
			  <%if(stockState.equals("1")){ %>
			  <td align="center" width="100"><font color="#FFFFFF">发货备注</font></td>
			  <%} else { %>
			  <td align="center" width="100"><font color="#FFFFFF">备注信息</font></td>
			  <%} %>
			  <td align="center" width="80"><font color="#FFFFFF">操作</font></td>
            </tr>
<logic:present name="orderList" scope="request"> 
<logic:iterate name="orderList" id="item" > 
<%
	vo = (voOrder) item;
%>
		<tr bgcolor='#F8F8F8'>
		<td align='center'><input type="checkbox" name="id" value="<bean:write name="item" property="id" />" /></td>
		<td align='center'><a href="order.do?id=<bean:write name="item" property="id" />" >
			<%if(vo.getFlat() == 1){%>
				<font color="blue">
			<%} else if(vo.isStockDeleted()){%>
				<font color="red">
			<%} else {%>
				<font color="">
			<%} %>
			<bean:write name="item" property="code" /></font></a></td>
		<td align="center"><bean:write name="item" property="orderStock.code" /></td>
		<td align="center"><bean:write name="item" property="name" /></td>
		<td align="center"><bean:write name="item" property="products" /></td>
		<%--
		<td align="left"><a href="stat/pvphone.jsp?phone=<bean:write name="item" property="phone" />"><bean:write name="item" property="phone" /></a></td>
		<td align="left"><bean:write name="item" property="phone2" /></td>
		<td align="center"><bean:write name="item" property="postcode" /></td>
		--%>
        <%if(stockState.equals("1") || stockState.equals("2")){ %>
		<td align="center">
			<select id="productType<bean:write name="item" property="id" />" name="productType<bean:write name="item" property="id" />" class="bd">
<%if(vo.getProductType()==0){ %>
				<option value="0"></option>
<%} %>
				<logic:iterate id="productType" name="item" property="productTypeMap">
					<option value="<bean:write name="productType" property="key" />"><bean:write name="productType" property="value" /></option>
				</logic:iterate>
			</select>
			<script>selectOption(document.getElementById("productType<bean:write name="item" property="id" />"), '<bean:write name="item" property="productType"/>')</script>
		</td>
        <%} %>
		<td align="right"><bean:write name="item" property="dprice" format="0.00"/></td>
		<td align="left"><bean:write name="item" property="address" /></td>
		<td align="left"><bean:write name="item" property="postcode" /></td>
		<td align="left"><bean:write name="item" property="destination" /></td>
		<td align="right"><bean:write name="item" property="BZZL" format="0" /></td>
		<%--
		<td align="right"><bean:write name="item" property="postageBj" format="0.00" /></td>
		<td align="right"><bean:write name="item" property="postageGd" format="0.00" /></td>
		<td align="right"><bean:write name="item" property="postageDif" format="0.00" /></td>
		<td align="right"><bean:write name="item" property="postageSavePercent" format="0.00" />%</td>
		--%>
		<td align="right"><%if(vo.getDeliver() == 7){ %><bean:write name="perWeightBJ" scope="request" /><%} else if(vo.getDeliver() == 8){ %><bean:write name="perWeightGD" scope="request" /><%} %></td>
        <%if(stockState.equals("1") || stockState.equals("2")){ %>
		<td align="center">
			<select id="deliver<bean:write name="item" property="id" />" name="deliver<bean:write name="item" property="id" />" class="bd">
<%if(vo.getDeliver() == 0){ %>
				<option value="0"></option>
<%} %>
				<%--if(areano.equalsIgnoreCase("0")){ %>
				<logic:iterate id="deliver" name="item" property="deliverMap">
					<option value="<bean:write name="deliver" property="key" />"><bean:write name="deliver" property="value" /></option>
				</logic:iterate>
				<%} else if(areano.equalsIgnoreCase("1")){ %>
				<logic:iterate id="deliver" name="item" property="deliverGdMap">
					<option value="<bean:write name="deliver" property="key" />"><bean:write name="deliver" property="value" /></option>
				</logic:iterate>
				<%} else { %>
				<logic:iterate id="deliver" name="item" property="deliverMap">
					<option value="<bean:write name="deliver" property="key" />"><bean:write name="deliver" property="value" /></option>
				</logic:iterate>
				<logic:iterate id="deliver" name="item" property="deliverGdMap">
					<option value="<bean:write name="deliver" property="key" />"><bean:write name="deliver" property="value" /></option>
				</logic:iterate>
				<%} --%>
				<logic:iterate id="deliver" name="item" property="deliverMapAll">
					<option value="<bean:write name="deliver" property="key" />"><bean:write name="deliver" property="value" /></option>
				</logic:iterate>
			</select>
			<script>selectOption(document.getElementById("deliver<bean:write name="item" property="id" />"), '<bean:write name="item" property="deliver"/>')</script>
		</td>
        <%} %>
		<td align="center"><bean:write name="item" property="orderStock.lastOperTime" /></td>
		<%if(stockState.equals("1")){ %>
		<td align=left><bean:write name="item" property="stockoutRemark"/></td>
		<%} else { %>
		<td align=left><bean:write name="item" property="remark"/></td>
		<%} %>
		<td align="left">
			<%--if(vo.getSellerCheckStatus() == 0){ %>
			<a style="color:red;" href="msellerCheckStatus.do?id=<bean:write name="item" property="id" />&status=1" target="_blank" >确认可以出库</a><br />
			<%} --%>
			<a href="forder.do?id=<bean:write name="item" property="id" />" >修改</a><br />
			<%
			voOrder order =(voOrder)item;
			if(order.getOrderStock() == null){ if(order.getStockout() == 0){%>
			<a href="orderStock/addOrderStock.jsp?orderCode=<bean:write name="item" property="code" />"  onclick="return confirm('确认要出库？');"  target="_blank"><font color=red>申请出货</font></a>
			<%} else {%>已出货<%}}else{%><% if(order.getOrderStock().getStatus() == OrderStockBean.STATUS3){ %>库存<%=order.getOrderStock().getStatusName()%><%} else { %><a href="orderStock/orderStock.jsp?id=<%=order.getOrderStock().getId()%>" target="_blank"><font color=blue>库存<%=order.getOrderStock().getStatusName()%></font></a><%}}%>
		</td>
		</tr>
</logic:iterate></logic:present>
          </table>
<%
	} else if(areano.equals("1") || areano.equals("2")){
		Map productMap = (Map)request.getAttribute("productMap");
%>
          <table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
            <tr bgcolor="#4688D6">
              <td width="10" align="center"><font color="#FFFFFF">&nbsp;</font></td>
			  <td width="80" align="center"><font color="#FFFFFF">日期</font></td>
              <td width="100" align="center"><font color="#FFFFFF">购买方式</font></td>
              <td width="60" align="center"><font color="#FFFFFF">金额</font></td>
              <td width="100" align="center"><font color="#FFFFFF">订单号</font></td>
              <td align="center"><font color="#FFFFFF">姓名</font></td>
              <td width="150" align="center"><font color="#FFFFFF">产品</font></td>
              <td width="60" align="center"><font color="#FFFFFF">数量</font></td>
              <td width="60" align="center"><font color="#FFFFFF">收货地址</font></td>
			  <td align="center"><font color="#FFFFFF">邮编</font></td>
			  <td align="center"><font color="#FFFFFF">联系电话</font></td>
			  <td align="center"><font color="#FFFFFF">电话1</font></td>
              <td width="100" align="center"><font color="#FFFFFF">确认申请<br/>出库时间</font></td>
              <%if(stockState.equals("1")){ %>
			  <td align="center"><font color="#FFFFFF">发货备注</font></td>
			  <%} else { %>
			  <td align="center"><font color="#FFFFFF">备注</font></td>
			  <%} %>
			  <td align="center"><font color="#FFFFFF">操作</font></td>
            </tr>
<logic:present name="orderList" scope="request">
<logic:iterate name="orderList" id="item" >
<%
	vo = (voOrder) item;
	List productList = (List)productMap.get(Integer.valueOf(vo.getId()));
	if(productList != null){
		Iterator iter = productList.listIterator();
		int index = 0;
		while(iter.hasNext()){
			OrderStockProductBean op = (OrderStockProductBean) iter.next();
			if(index==0){
%>
		<tr bgcolor='#F8F8F8'>
		<td align='center'><input type="checkbox" name="id" value="<bean:write name="item" property="id" />" /></td>
		<td align='center'><%= now %></td>
		<td align='center'>
			<%switch(((voOrder)item).getBuyMode()) {
				case 0:%>货到付款<%break;
				case 1:%>邮购<%break;
				case 2:%>上门自取<%break;
				}%>
		</td>
		<td align=left><bean:write name="item" property="dprice" format="0.00"/></td>
		<td align=left>
			<%if(vo.isStockDeleted()){ %>
			<font color="red"><bean:write name="item" property="code" /></font>
			<%} else { %>
			<bean:write name="item" property="code" />
			<%} %>
		</td>
		<td align=left><bean:write name="item" property="name" /></td>
		<td align=left><%= op.getProduct().getOriname() %></td>
		<td align=left><%= op.getStockoutCount() %></td>
		<td align=left><bean:write name="item" property="address" /></td>
		<td align=left><bean:write name="item" property="postcode"/></td>
		<td align=left><bean:write name="item" property="phone"/></td>
		<td align="left"><bean:write name="item" property="phone2" /></td>
		<td align="center"><bean:write name="item" property="orderStock.lastOperTime" /></td>
		<%if(stockState.equals("1")){ %>
		<td align=left><bean:write name="item" property="stockoutRemark"/></td>
		<%} else { %>
		<td align=left><bean:write name="item" property="remark"/></td>
		<%} %>
		<td align="left">
			<%--if(vo.getSellerCheckStatus() == 0){ %> 
			<a style="color:red;" href="msellerCheckStatus.do?id=<bean:write name="item" property="id" />&status=1" target="_blank" >确认可以出库</a><br />
			<%} --%>
			<a href="forder.do?id=<bean:write name="item" property="id" />" >修改</a><br />
			<a href="javascript:void(0);" onclick="document.getElementById('sum_action').value='print';  document.forms[2].target='_blank'; document.forms[2].submit();" >打印</a><br />
			<%
			voOrder order =(voOrder)item;
			if(order.getOrderStock() == null){ if(order.getStockout() == 0){%>
			<a href="orderStock/addOrderStock.jsp?orderCode=<bean:write name="item" property="code" />"  onclick="return confirm('确认要出库？');"  target="_blank"><font color=red>申请出货</font></a>
			<%} else {%>已出货<%}}else{%><% if(order.getOrderStock().getStatus() == OrderStockBean.STATUS3){ %>库存<%=order.getOrderStock().getStatusName()%><%} else { %><a href="orderStock/orderStock.jsp?id=<%=order.getOrderStock().getId()%>" target="_blank"><font color=blue>库存<%=order.getOrderStock().getStatusName()%></font></a><%}}%>
		</td>
		</tr>
<%
		} else {
%>
		<tr bgcolor='#F8F8F8'>
		<td align='center'>&nbsp;</td>
		<td align='center'>&nbsp;</td>
		<td align='center'>&nbsp;</td>
		<td align=left>&nbsp;</td>
		<td align=left>&nbsp;</td>
		<td align=left>&nbsp;</td>
		<td align=left><%= op.getProduct().getOriname() %></td>
		<td align=left><%= op.getStockoutCount() %></td>
		<td align=left>&nbsp;</td>
		<td align=left>&nbsp;</td>
		<td align=left>&nbsp;</td>
		<td align=left>&nbsp;</td>
		<td align=left>&nbsp;</td>
		<td align=left>&nbsp;</td>
		<td align=left>&nbsp;</td>
		</tr>
<%
			}
			index++;
		}
	}
%>
</logic:iterate></logic:present>
          </table>
<%} %>

<input type="button" value="全选" onclick="setAllCheck(document.forms[2], 'id', true);" />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;

<input id="printSelBtn" type="button" value="打印选定" onclick="printSelected();" >
<input type="button" value="汇总选定" onclick="document.getElementById('sum_action').value='sum';  document.forms[2].target='_blank'; document.forms[2].submit();" />
<input type="hidden" name="status" value="1" />
<input type="button" value="确认修改选定订单的分类/快递" onclick="updateDPTSelected(this.form);" />&#160;&#160;&#160;

产品分类:<select name="productTypeAll" class="bd">
	<option value="0"></option>
	<logic:iterate id="productType" name="productTypeMap">
	<option value="<bean:write name="productType" property="key" />"><bean:write name="productType" property="value" /></option>
	</logic:iterate>
</select>&#160;&#160;&#160;
快递公司:<select name="deliverAll" class="bd">
	<option value="0"></option>
	<%--if(areano.equalsIgnoreCase("0")){ %>
	<logic:iterate id="deliver" name="deliverMap">
	<option value="<bean:write name="deliver" property="key" />"><bean:write name="deliver" property="value" /></option>
	</logic:iterate>
	<%} else if(areano.equalsIgnoreCase("1")){ %>
	<logic:iterate id="deliver" name="deliverGdMap">
	<option value="<bean:write name="deliver" property="key" />"><bean:write name="deliver" property="value" /></option>
	</logic:iterate>
	<%} else { %>
	<logic:iterate id="deliver" name="deliverMap">
	<option value="<bean:write name="deliver" property="key" />"><bean:write name="deliver" property="value" /></option>
	</logic:iterate>
	<logic:iterate id="deliver" name="deliverGdMap">
	<option value="<bean:write name="deliver" property="key" />"><bean:write name="deliver" property="value" /></option>
	</logic:iterate>
	<%} --%>
	<logic:iterate id="deliver" name="deliverMapAll">
	<option value="<bean:write name="deliver" property="key" />"><bean:write name="deliver" property="value" /></option>
	</logic:iterate>
</select>
<input type="button" value="确认批量修改分类/快递" onclick="updateDPTAll(this.form);" />
</form>
          <br/>
</body>
</html>
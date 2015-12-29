
<%@page import="adultadmin.logic.order.NormalOrderPriceCalculator"%><%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.vo.voOrder,mmb.promotion.*"%><%@ page import="adultadmin.bean.order.*"%><%@ page import="adultadmin.bean.*"%><%@ page import="java.text.*"%><%@ page import="adultadmin.bean.stock.*"%><%@ page import="adultadmin.util.*"%><%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.framework.*" %>
<%!
static SimpleDateFormat sdf = new SimpleDateFormat("M月d H:mm");
%><%

	//判断有没有权限
    if(!PermissionFrk.hasPermission(request, PermissionFrk.ORDER_ADMIN)){
		return;
    }
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

PagingBean paging = (PagingBean) request.getAttribute("paging");
String buymode=request.getParameter("buymode");
if(buymode==null)
	buymode="0";
String status=request.getParameter("status");
if(status==null)
	status="0";
String flat=request.getParameter("flat");
if(flat==null)
	flat="-1";

voOrder vo = null;
%>
<html>
<title>买卖宝后台</title>
<script>
function collect()
{
		document.orderForm.action="collect3.do?buymode=<%=buymode%>&status=<%=status%>";
		return document.orderForm.submit();
}
function collect2()
{
		document.orderForm.action="collect2.do?buymode=<%=buymode%>&status=<%=status%>";
		return document.orderForm.submit();
}
function reload(time){
	var contactTime = time.options[time.selectedIndex].value;
	if(contactTime == 0){
		window.location.href='orders.do?buymode=<%= buymode %>&status=<%= status %>&orderBy=contact_time&contactTime=0&flat=<%= flat %>';
	} else if(contactTime == 1){
		window.location.href='orders.do?buymode=<%= buymode %>&status=<%= status %>&orderBy=contact_time&contactTime=1&flat=<%= flat %>';
	} else if(contactTime == 2){
		window.location.href='orders.do?buymode=<%= buymode %>&status=<%= status %>&orderBy=contact_time&contactTime=2&flat=<%= flat %>';
	} else if(contactTime == 3){
		window.location.href='orders.do?buymode=<%= buymode %>&status=<%= status %>&orderBy=contact_time&contactTime=3&flat=<%= flat %>';
	} else if(contactTime == 4){
		window.location.href='orders.do?buymode=<%= buymode %>&status=<%= status %>&orderBy=contact_time&contactTime=4&flat=<%= flat %>';
	}
}
function uniteOrder(){
	var uniteBox = document.getElementsByName("select");
	var i = 0;
	var url = "uniteOrder.do?1=1";
	if(confirm("确认要合并选中的订单吗？")){
		for(i=0; i<uniteBox.length; i++){
			if(uniteBox[i].checked){
				url += "&uniteOrderId=" + uniteBox[i].value;
			}
		}
		window.location.href=url;
	}
}
</script>
<script type="text/javascript" src="js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%--@include file="../page.jsp"--%>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "currentPage", 10)%> <input type="button" value="只看WAP订单" onclick="window.location.href='orders.do?buymode=<%= buymode %>&status=<%= status %>&contactTime=-1&flat=0';" /> <input type="button" value="只看WEB订单" onclick="window.location.href='orders.do?buymode=<%= buymode %>&status=<%= status %>&contactTime=-1&flat=1';" /> <input type="button" value="所有订单" onclick="window.location.href='orders.do?buymode=<%= buymode %>&status=<%= status %>&contactTime=-1&flat=-1';" />
<%if(status.equals("0") || status.equals("1")){ %>
<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="button" value="按下订单时间排序" onclick="window.location.href='orders.do?buymode=<%= buymode %>&status=<%= status %>&orderBy=id';" />
<%--
			<input type="button" value="按方便联系时间排序" onclick="window.location.href='orders.do?buymode=<%= buymode %>&status=<%= status %>&orderBy=contact_time&contactTime=-1&flat=<%= flat %>';" />
			<select name="contactTime" class="bd" onchange="reload(this);">
				<option value="-1">请选择方便联系时间</option>
				<option value="0">随时</option>
				<option value="1">9:00-12:00</option>
				<option value="2">12:00-14:00</option>
				<option value="3">14:00-18:00</option>
				<option value="4">18:00-24:00</option>
			</select>
--%>
<%} %>
          <br><form method=post action="" name="orderForm" target="_blank">
          <input type=hidden name=flag value="<%=request.getParameter("flag")%>">
          <table width="99%" cellpadding="2" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td width="15" align="center"><font color="#FFFFFF">选</font></td>
              <td align="center"><font color="#FFFFFF">订单号</font></td>
              <td align="center"><font color="#FFFFFF">姓名</font></td>
              <td align="center"><font color="#FFFFFF">电话</font></td>
              <td align="center"><font color="#FFFFFF">地址</font></td>
              <td width="100" align="center"><font color="#FFFFFF">产品名称</font></td>
			  <td width="60" align="center"><font color="#FFFFFF">折扣前</font></td>
              <td width="60" align="center"><font color="#FFFFFF">折扣后</font></td>
<%if(buymode.equals("1")){%>
			  <td align="center"><font color="#FFFFFF">配送费</font></td>
<%} %>
			  <td align="center"><font color="#FFFFFF">已到款</font></td>
<%if(status.equals("0") || status.equals("1")){ %>
			  <td align="center"><font color="#FFFFFF">联系时间</font></td>
<%} %>
			  <%--td align="center"><font color="#FFFFFF">类型</font></td--%>
<%--if(buymode.equals("0")){%>
			  <td width="60" align="center"><font color="#FFFFFF">预付费方式</font></td>
<%}--%>
<%if(buymode.equals("1")){%>
              <td width="60" align="center"><font color="#FFFFFF">汇款方式</font></td>
              <%--
              <td width="60" align="center"><font color="#FFFFFF">邮寄方式</font></td>
              --%>
<%}%>
              <td width="55" align="center"><font color="#FFFFFF">生成<br>时间</font></td>
			  <td align="center"><font color="#FFFFFF">出货地点</font></td>
			  <td width="60" align="center"><font color="#FFFFFF">出货时间</font></td>
			  <td width="80" align="center"><font color="#FFFFFF">备注</font></td>
<%if(group.isFlag(36)) /*if(isSystem || isXiaoshou || isKefu || isShangpin)*/{%>
              <td width="40" align="center"><font color="#FFFFFF">操作</font></td>
<%}%>
            </tr>           
<logic:present name="orderList" scope="request"> 
<logic:iterate name="orderList" id="item" > 
<%
	vo = (voOrder) item;
 String creattime = DateUtil.formatDate(vo.getCreateDatetime(),DateUtil.normalTimeFormat);
 String condition = " begin_time<='" + creattime	+ "' and (end_time>='" + creattime + "' or end_time is null )";
 float noServicePrice = NormalOrderPriceCalculator.noServicePrice;
 float postagePrice = NormalOrderPriceCalculator.postagePrice;
//检索指定时间的有效邮费。
 PostageRuleBean  prb = new PostageService().getPostageRuleBean(condition);
  if(prb!=null){
   noServicePrice = prb.getRequirement();
   postagePrice = prb.getPostage();
  }
	 float descprice2 = vo.getPrice()>=noServicePrice+postagePrice?vo.getPrice()-vo.getPostage():vo.getPrice();
	
%>
		<tr bgcolor='#F8F8F8'>
		<td align='center' width="10" style="padding:0px"><input type=checkbox name="select" value="<bean:write name="item" property="id" />"></td>
		<td align='center'><a href="order.do?id=<bean:write name="item" property="id" />" ><%if(vo.getFlat() == 1){%><font color="red"><%} else {%><font color="blue"><%}%><bean:write name="item" property="code" /></font></a></td>
		<td align=left><bean:write name="item" property="name" /><%= (((voOrder)item).getAgent()==1)?"(代理)":"" %></td>
		<td align=left><%= StringUtil.cutString(vo.getPhone(), 7) %><%if(vo.getPhone() != null && vo.getPhone().length() > 7){ %>****<%} %></td>
		<td align=left><%= StringUtil.cutString(vo.getAddress(), 4) %><%if(vo.getAddress() != null && vo.getAddress().length() > 4){ %>...<%} %></td>
		<td align=left width="100"><bean:write name="item" property="products" /></td>
		<td align=left> <%= StringUtil.formatFloat(descprice2) %></td>
		<td align=left><bean:write name="item" property="dprice" format="0.00"/></td>
<%if(buymode.equals("1")){%>
		<td align="center"><bean:write name="item" property="postage" format="0.00"/></td>
<%} %>
		<td align="center"><bean:write name="item" property="realPay" format="0.00"/></td>
<%if(status.equals("0") || status.equals("1")){ %>
		<td align="center">
			<logic:equal name="item" property="contactTime" value="0">随时</logic:equal>
			<logic:equal name="item" property="contactTime" value="1">9:00-12:00</logic:equal>
			<logic:equal name="item" property="contactTime" value="2">12:00-14:00</logic:equal>
			<logic:equal name="item" property="contactTime" value="3">14:00-18:00</logic:equal>
			<logic:equal name="item" property="contactTime" value="4">18:00-24:00</logic:equal>
		</td>
<%} %>
		<%--td align=left><logic:equal name="item" property="agent" value="1"><logic:equal name="item" property="isOrder" value="0"><font color="red">代理进货</font></logic:equal><logic:equal name="item" property="isOrder" value="1"><font color="red">代理退货</font></logic:equal></logic:equal><logic:equal name="item" property="agent" value="0">普通</logic:equal></td--%>
<%--if(buymode.equals("0")){%>
		<td align=center width="60">
<%if(((voOrder)item).getIsOlduser() == 0){
	switch(((voOrder)item).getPrePayType()) {
	case 0:%>工商银行<%break;
	case 1:%>建设银行<%break;
	case 2:%>招商银行<%break;
	case 3:%>广发银行<%break;
	case 4:%>中国银行<%break;
	case 5:%>农业银行<%break;
	case 6:%>邮政储蓄<%break;
	case 7:%>神州行卡充值<%break;
	}
} else {%>无<%} %>
		</td>
<%} --%>
<%if(buymode.equals("1")){%>
		<td align=center width="60">
<%
	switch(((voOrder)item).getRemitType()) {
	case 0:%>工商银行<%break;
	case 1:%>建设银行<%break;
	case 2:%>招商银行<%break;
	case 3:%>广发银行<%break;
	case 4:%>中国银行<%break;
	case 5:%>农业银行<%break;
	case 6:%>邮政储蓄<%break;
	case 7:%>钱包支付<%break;
	}
%>
		</td>
<%}%>
<%--
<%if(buymode.equals("1")){%>
		<td align=center width="60">
	<%switch(((voOrder)item).getDeliverType()) {
	case 0:%>普通邮寄<%break;
	case 1:%>特快专递<%break;
	case 2:%>全国快递<%break;
	}%>
		</td>
<%}%>
--%>
		<td align=left width="55"><%=sdf.format(new java.util.Date(vo.getCreateDatetime().getTime()))%></td>
<%
		//出货记录
		OrderStockBean oper = vo.getOrderStock();
	    if(oper != null && oper.getStatus() == 2){
			String areaName = "<font color=blue>" + ProductStockBean.getAreaName(oper.getStockArea()) + "</font>";
%>
		<td align="center"><%=areaName%></td>
		<td width="60" align="center"><%=oper.getLastOperTime().substring(5, 16)%></td>
<%
		}
        else {
%>
        <td width="25" align="center">无</td>
		<td width="80" align="center">无</td>
<%
		}
%>
		<td width="80" align="center"><%= StringUtil.toSecurityHtml(vo.getRemark()) %></td>
<%if(group.isFlag(36)) /*if(isSystem || isXiaoshou || isKefu || isShangpin)*/{%>
		<td align=left width="40"><%if(vo.getCode().startsWith("T")){ if(group.isFlag(190)){%><a href="forder.do?id=<bean:write name="item" property="id" />" >修改</a><%}} else { %><a href="forder.do?id=<bean:write name="item" property="id" />" >修改</a><%} %>
		<br>
		
		<%
			voOrder order =(voOrder)item;
			if(order.getOrderStock() == null){ if(order.getStockout() == 0){%>
			<%if(order.getStatus() == 3){ %>
			<a href="orderStock/addOrderStock.jsp?orderCode=<bean:write name="item" property="code" />"  onclick="return confirm('确认要出库？');"  target="_blank"><font color=red>申请出货</font></a>
			<%} else { %><font color=red>申请出货</font><%} %>
			<%} else {%>已出货<%}}else{%><% if(order.getOrderStock().getStatus() == OrderStockBean.STATUS3){ %>库存<%=order.getOrderStock().getStatusName()%><%} else { %><a href="orderStock/orderStock.jsp?id=<%=order.getOrderStock().getId()%>" target="_blank"><font color=blue>库存<%=order.getOrderStock().getStatusName()%></font></a><%}}%>
		</td>
<%}%>
		</tr>
</logic:iterate> </logic:present> 
          </table>
          </form>
          <table width="80%" cellspacing="0" cellpadding="0" align=center>
            <tr>
              <td height="35">
            <input type=checkbox onclick="setAllCheck(orderForm,'select',this.checked)">全选
              </td>
              <td height="35">
<%if(group.isFlag(61)){ %>
            <input type=button onclick="return collect()" value=" 对选中的订单进行产品统计 ">
            <input type=button onclick="return collect2()" value=" 对本分类的订单进行产品统计 ">
<%} %>
<%if(!status.equals("3") && !status.equals("6") && !status.equals("9") && !status.equals("10")){ %>
<%if(group.isFlag(36)) /*if(isSystem || isXiaoshou || isKefu || isShangpin)*/{%>
            <input type="button" onclick="uniteOrder();" value=" 合并订单 " />
<%}%>
<%}%>
              </td>
            </tr>
          </table>
          <br>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "currentPage", 10)%>
</body>
</html>
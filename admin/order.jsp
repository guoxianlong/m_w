<%@page import="adultadmin.bean.afterSales.AfterSaleNifferRecordBean"%>
<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,adultadmin.action.vo.*" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.framework.*" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%@ page import="adultadmin.bean.order.*" %>
<%@ page import="adultadmin.bean.*,adultadmin.service.infc.*" %>
<%@ page import="java.util.*,adultadmin.service.ServiceFactory" %>
<%
	voUser adminUser = (voUser)session.getAttribute("userView");
	UserGroupBean group = adminUser.getGroup();

	voOrder order = (voOrder)request.getAttribute("order");
	voUser seller = (voUser)request.getAttribute("seller");
	voUser user = (voUser) request.getAttribute("user");
	Boolean isSuperQBuy =  (Boolean)request.getAttribute("isSuperQBuy");
	boolean sqqFlag = false;
	if(isSuperQBuy != null){
		sqqFlag = isSuperQBuy.booleanValue();
	}
	UserInfoBean userInfo = (UserInfoBean) request.getAttribute("userInfo");
	List cardList = (List) request.getAttribute("cardList");
	String propmt = StringUtil.convertNull((String)request.getAttribute("propmt"));
	int point = 0;
	int rank = 0;
	if(userInfo != null){
		point = userInfo.getPoint();
		rank = userInfo.getRank();
	}
	voOrderProduct p = null;
	
	boolean isWalletPay = false ;
	String walletPayStatus = "";
	float[] discountPrices = (float[]) request.getAttribute("discountPrices");
String creattime = DateUtil.formatDate(order.getCreateDatetime(),DateUtil.normalTimeFormat);
String condition = " begin_time<='" + creattime	+ "' and (end_time>='" + creattime + "' or end_time is null )";
%>
<%@page import="adultadmin.util.Constants"%><html>
<head>
<title>买卖宝后台</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="js/JS_functions.js"></script>
</head>
<body>
<%@include file="../header.jsp"%>
<script type="text/javascript">
function checkUniteSubimt(uniteForm){
	if(confirm("确认要合并选中的订单吗？")){
		return true;
	} else {
		return false;
	}
}
function selectAllUnite(isChecked){
	var uniteBox = document.getElementsByName("uniteOrderId");
	var i = 0;
	for(i = 0; i<uniteBox.length; i++){
		uniteBox[i].checked=isChecked;
	}
}
function heimingdan(){
	if(!<%=group.isFlag(380)%> && <%=order.getBuyMode()==2%> ){
		alert("您无权修改银行汇款的订单!");
		return false;
	}else{
		if(confirm('确认加入黑名单？'))
          window.open("mBlackList.do?orderCode=<%=order.getCode()%>")
		
    }
}
</script>
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center  width="100">订单编号</td>
		<td ><bean:write name="order" property="code" scope="request"/><%if(isWalletPay){%>&nbsp;&nbsp;&nbsp;<font color='red'><%=walletPayStatus%></font><%}%>&nbsp;&nbsp;&nbsp;(下单时间<bean:write name="order" property="createDatetime" scope="request"/>)</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center  width="100">订单来源</td>
		<td ><%if(order.getFlat() == 0){%><font color="blue">WAP</font><%} else {%><font color="red">WEB</font><%}%></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center  width="100">订单状态</td>
		<td ><bean:write name="order" property="statusName" scope="request"/></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center  width="100">购买方式</td>
		<td ><%switch(order.getBuyMode()) {
				case 0:%>货到付款<%break;
				case 1:%>钱包支付<%break;
				case 2:%>银行汇款<%break;
				}%>
		</td>
	</tr>
<%
	if(seller != null){
%>
	<tr bgcolor='#F8F8F8'>
		<td align=center  width="100">销售人员</td>
		<td ><%= seller.getUsername() %></td>
	</tr>
<%
	}
%>
    <tr bgcolor="#4688D6">
    	<td align="center" colspan=2><font color="#FFFFFF">用户信息</font></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center  width="100">姓名</td>
		<td ><%=StringUtil.toSecurityHtml(order.getName()) %></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center >手机号</td>
		<td >
			<%
				if(order.getPhone() != null){
					if(order.getPhone().length()>=11){
			%>
				<%=order.getPhone().substring(0, 3)+"****"+order.getPhone().substring(7) %>
			<%			
					}else{
			%>
					<%=order.getPhone() %>
			<%		
					}
				}
			%>
		</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center >电话2</td>
		<td ><%=StringUtil.toSecurityHtml(order.getPhone2()) %></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center >地址</td>
		<td ><%=StringUtil.toSecurityHtml(order.getAddress()) %></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center >邮编</td>
		<td ><bean:write name="order" property="postcode" scope="request"/></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center >总价</td>
		<td ><bean:write name="order" property="dprice" scope="request"/></td>
	</tr>
	<tr bgcolor='#F8F8F8'>-
		<td align=center >邮费</td>
		<td ><bean:write name="order" property="postage" scope="request"/></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center >已到款</td>
		<td ><font color="red"><bean:write name="order" property="realPay" scope="request"/></font></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center >包裹单号码</td>
		<td ><bean:write name="order" property="packageNum" scope="request"/></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center >处理人</td>
		<td ><bean:write name="order" property="operator" scope="request"/></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center  width="100">备注</td>
		<td ><bean:write name="order" property="remark" filter="true" scope="request"/></td>
	</tr>
</table>
<br />
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr><td colspan="7" align="left">货品列表</td></tr>
	<tr bgcolor="#4688D6">
		<td align="center" width=80><font color="#FFFFFF">编号</font></td>
		<td align="center"><font color="#FFFFFF">名称</font></td>
<%if(order.getCode().startsWith("T")){ %>
		<td width="60" align="center"><font color="#FFFFFF">团购价</font></td>
<%} %>
		<td width="60" align="center"><font color="#FFFFFF">价格</font></td>
		<td width="100" align="center"><font color="#FFFFFF">代理商</font></td>
		<td width="40" align="center"><font color="#FFFFFF">数量</font></td>
		<td width="40" align="center"><font color="#FFFFFF">库存<br/>(广东)</font></td>
		<td width="40" align="center"><font color="#FFFFFF">补货周期</font></td>
	</tr>           
<logic:present name="opList" scope="request"> 
<logic:iterate name="opList" id="item" >
<%
    p = (voOrderProduct) item;
	if(order.getFlat() == 1){
		
	}
    //WAP订单
	else {
%>
	<tr bgcolor='#F8F8F8'>
		<td align='center'><bean:write name="item" property="code" /></td>
		<td align='center'>
			<a href="fproduct.do?id=<bean:write name="item" property="productId" />" ><%if(p.getPrice()<0){%><font color="red"><bean:write name="item" property="name" /></font><%}else{ %><bean:write name="item" property="name" /><%} %></a>
		</td>
<%if(order.getCode().startsWith("T")){ %>
		<td align='right'><bean:write name="item" property="groupBuyPrice" /></td>
<%} %>
		<td align='right'><bean:write name="item" property="price" /></td>
		<td align='center'><bean:write name="item" property="proxyName" /></td>
		<td align=right width="40"><bean:write name="item" property="count" /></td>
		<td align=right width="40"><%= p.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + p.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + p.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED)  %></td>
		<td align="center" width="40"><%if((p.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + p.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + p.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED))<p.getCount()){ %><%= StringUtil.toWml(p.getGdStockin()) %><%} else { %>-<%} %></td>
	</tr>
<%
	}
%>
</logic:iterate></logic:present>
</table><br />
<logic:present name="persentList" scope="request">
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr><td colspan="7" align="left">赠品列表</td></tr>
<logic:iterate name="persentList" id="item" >
<%
    p = (voOrderProduct) item;
	//WEB订单
	if(order.getFlat() == 1){
	}
    //WAP订单
	else {
%>
	<tr bgcolor='#F8F8F8'>
		<td align='center'><bean:write name="item" property="code" /></td>
		<td align='center'>
			<a href="fproduct.do?id=<bean:write name="item" property="productId" />" ><bean:write name="item" property="name" /></a>
		</td>
<%if(order.getCode().startsWith("T")){ %>
		<td align='right'><bean:write name="item" property="groupBuyPrice" /></td>
<%} %>
		<td align='right'><bean:write name="item" property="price" /></td>
		<td align='center'><bean:write name="item" property="proxyName" /></td>
		<td align=right width="40"><bean:write name="item" property="count" /></td>
		<td align=right width="40"><%= p.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + p.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + p.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED)  %></td>
		<td align="center" width="40"><%if((p.getStock(ProductStockBean.AREA_GF, ProductStockBean.STOCKTYPE_QUALIFIED) + p.getStock(ProductStockBean.AREA_GS, ProductStockBean.STOCKTYPE_QUALIFIED) + p.getStock(ProductStockBean.AREA_ZC, ProductStockBean.STOCKTYPE_QUALIFIED) )<p.getCount()){ %><%= StringUtil.toWml(p.getGdStockin()) %><%} else { %>-<%} %></td>
	</tr>
<%
	}
%>
</logic:iterate></logic:present>
</table>
<br/>   
<%
if(order.getUnitedOrders() != null && order.getUnitedOrders().length() > 0){
%>
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr style="color:red;">
		<td colspan="13" align="center">已经合并的订单</td>
	</tr>
	<tr bgcolor="#4688D6">
		<td align="center" width=80><font color="#FFFFFF">订单号</font></td>
		<td align="center"><font color="#FFFFFF">姓名</font></td>
		<td width="60" align="center"><font color="#FFFFFF">电话</font></td>
		<td width="100" align="center"><font color="#FFFFFF">地址</font></td>
		<td width="100" align="center"><font color="#FFFFFF">产品名称</font></td>
		<td width="40" align="center"><font color="#FFFFFF">总价</font></td>
		<td width="40" align="center"><font color="#FFFFFF">配送费</font></td>
		<td width="40" align="center"><font color="#FFFFFF">已到款</font></td>
		<td width="40" align="center"><font color="#FFFFFF">类型</font></td>
		<td width="100" align="center"><font color="#FFFFFF">生成时间</font></td>
		<td width="40" align="center"><font color="#FFFFFF">状态</font></td>
		<td width="40" align="center"><font color="#FFFFFF">购买方式</font></td>
	</tr>
<logic:iterate id="unitedItem" name="unitedOrderList" scope="request">
<% voOrder unitedOrder = (voOrder)unitedItem; 
%>
	<tr bgcolor='#F8F8F8'>
		<td align='center'><bean:write name="unitedItem" property="code" /></td>
		<td align='center'><%=StringUtil.toSecurityHtml(unitedOrder.getName()) %></td>
		<td align='center'><%=StringUtil.toSecurityHtml(unitedOrder.getPhone()) %></td>
		<td align='center'><%=StringUtil.toSecurityHtml(unitedOrder.getAddress()) %></td>
		<td align='center'><bean:write name="unitedItem" property="products" /></td>
		<td align='center'><%=unitedOrder.getDprice() %></td>
		<td align='center'><bean:write name="unitedItem" property="postage" /></td>
		<td align='center'><bean:write name="unitedItem" property="realPay" /></td>
		<td align='center'><%= (unitedOrder.getIsOrder()==1)?"退货":"进货" %></td>
		<td align='center'><bean:write name="unitedItem" property="createDatetime" format="yyyy-MM-dd kk:mm:ss" /></td>
		<td align='center'>
<%
	switch(unitedOrder.getStatus()){
		case 0:
%><%= "未处理" %><%
		break;
		case 1:
%><%= "电话失败" %><%
		break;
		case 2:
%><%= "电话成功" %><%
		break;
		case 3:
%><%= "已到款" %><%
		break;
		case 4:
%><%= "正在汇总" %><%
		break;
		case 5:
%><%= "已汇总" %><%
		break;
		case 6:
%><%= "已发货" %><%
		break;
		case 7:
%><%= "已取消" %><%
		break;
		case 8:
%><%= "废弃" %><%
		break;
		case 9:
%><%= "待查款" %><%
		break;
		case 10:
%><%= "重复" %><%
		break;
		case 11:
%><%= "已退回" %><%
		break;
		case 12:
%><%= "已结算" %><%
		break;
		case 13:
%><%= "待退回" %><%
		break;
		case 14:
%><%= "已妥投" %><%
	}
%>
		</td>
		<td align='center'>
<%
	switch(unitedOrder.getBuyMode()){
		case 0:
%><%= "货到付款" %><%
		break;
		case 1:
%><%= "钱包支付" %><%
		break;
		case 2:
%><%= "上门自取" %><%
	}
%>
		</td>
	</tr>
</logic:iterate>
</table>
<%
}
%>

<logic:notEmpty name="samePhoneOrderList" scope="request">
<form method="post" action="uniteOrder.do" name="uniteOrderForm" onsubmit="return checkUniteSubimt(this);">
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr style="color:red;">
		<td colspan="13" align="center">同一个手机号下的订单</td>
	</tr>
	<tr bgcolor="#4688D6">
		<td align="center" width=10></td>
		<td align="center" width=80><font color="#FFFFFF">订单号</font></td>
		<td align="center"><font color="#FFFFFF">姓名</font></td>
		<td width="60" align="center"><font color="#FFFFFF">电话</font></td>
		<td width="100" align="center"><font color="#FFFFFF">地址</font></td>
		<td width="100" align="center"><font color="#FFFFFF">产品名称</font></td>
		<td width="40" align="center"><font color="#FFFFFF">总价</font></td>
		<td width="100" align="center"><font color="#FFFFFF">生成时间</font></td>
		<td width="40" align="center"><font color="#FFFFFF">状态</font></td>
		<td width="40" align="center"><font color="#FFFFFF">购买方式</font></td>
	</tr>
<logic:iterate id="samePhoneItem" name="samePhoneOrderList" scope="request">
<% voOrder sameOrder = (voOrder)samePhoneItem; 
creattime = DateUtil.formatDate(sameOrder.getCreateDatetime(),DateUtil.normalTimeFormat);
 condition = " begin_time<='" + creattime	+ "' and (end_time>='" + creattime + "' or end_time is null )";

%>
	<tr bgcolor='#F8F8F8'>
		<td align='center'>
		</td>
		<td align='center'><bean:write name="samePhoneItem" property="code" /></td>
		<td align='center'><bean:write name="samePhoneItem" property="name" /></td>
		<td align='center'><bean:write name="samePhoneItem" property="phone" /></td>
		<td align='center'><bean:write name="samePhoneItem" property="address" /></td>
		<td align='center'><bean:write name="samePhoneItem" property="products" /></td>
		<td align='center'><bean:write name="samePhoneItem" property="dprice" /></td>
		<td align='center'><bean:write name="samePhoneItem" property="createDatetime" format="yyyy-MM-dd kk:mm:ss" /></td>
		<td align='center'>
<%
	switch(sameOrder.getStatus()){
		case 0:
%><%= "未处理" %><%
		break;
		case 1:
%><%= "电话失败" %><%
		break;
		case 2:
%><%= "电话成功" %><%
		break;
		case 3:
%><%= "已到款" %><%
		break;
		case 4:
%><%= "正在汇总" %><%
		break;
		case 5:
%><%= "已汇总" %><%
		break;
		case 6:
%><%= "已发货" %><%
		break;
		case 7:
%><%= "已取消" %><%
		break;
		case 8:
%><%= "废弃" %><%
		break;
		case 9:
%><%= "待查款" %><%
		break;
		case 10:
%><%= "重复" %><%
		break;
		case 11:
%><%= "已退回" %><%
		break;
		case 12:
%><%= "已结算" %><%
		break;
		case 13:
%><%= "待退回" %><%
		break;
		case 14:
%><%= "已妥投" %><%
	}
%>
		</td>
		<td align='center'>
<%
	switch(sameOrder.getBuyMode()){
		case 0:
%><%= "货到付款" %><%
		break;
		case 1:
%><%= "钱包支付" %><%
		break;
		case 2:
%><%= "上门自取" %><%
	}
%>
		</td>
	</tr>
</logic:iterate>
</table>
</form>
</logic:notEmpty>
</body>
</html>
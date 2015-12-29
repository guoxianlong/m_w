<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,adultadmin.action.vo.*,adultadmin.bean.*" %>
<%

voUser user = (voUser)session.getAttribute("userView");
UserGroupBean group = user.getGroup();
	voOrder order = (voOrder)request.getAttribute("order");
	String manualDist = (String)request.getAttribute("manualDist");
%>
<html>
<title>买卖宝后台</title>
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
</script>
<script language="JavaScript" src="js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
<%if(request.getAttribute("order")!=null){

if(true){
	if(order.getCode().startsWith("T") && !group.isFlag(190)){
		response.sendRedirect("order.do?id="+order.getId());
	} else {
		response.sendRedirect("forder.do?id="+order.getId());
	}
	return;
}

%>  
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center  width="100">订单编号</td>
		<td ><bean:write name="order" property="code" scope="request"/></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center  width="100">订单状态</td>
		<td ><bean:write name="order" property="statusName" scope="request"/></td>
	</tr>
    <tr bgcolor="#4688D6">
    	<td align="center" colspan=2><font color="#FFFFFF">用户信息</font></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center  width="100">姓名</td>
		<td ><bean:write name="order" property="name" scope="request"/></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center >手机号</td>
		<td ><bean:write name="order" property="phone" scope="request"/></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center >地址</td>
		<td ><bean:write name="order" property="address" scope="request"/></td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center >邮编</td>
		<td ><bean:write name="order" property="postcode" scope="request"/></td>
	</tr>
</table>
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr><td colspan="5" align="left">货品列表</td></tr>
	<tr bgcolor="#4688D6">
		<td align="center"><font color="#FFFFFF">名称</font></td>
		<td width="60" align="center"><font color="#FFFFFF">价格</font></td>
		<td width="100" align="center"><font color="#FFFFFF">代理商</font></td>
		<td align="center" width=80><font color="#FFFFFF">编号</font></td>
		<td width="80" align="center"><font color="#FFFFFF">数量</font></td>
	</tr>           
<logic:present name="opList" scope="request"> 
<logic:iterate name="opList" id="item" > 
	<tr bgcolor='#F8F8F8'>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="productId" />"  target="_blank"><bean:write name="item" property="name" /></a></td>
		<td align='right'><bean:write name="item" property="price" /></td>
		<td align='center'><bean:write name="item" property="proxyName" /></td>
		<td align='center'><bean:write name="item" property="code" /></td>
		<td align=left><bean:write name="item" property="count" /></td>
	</tr>
</logic:iterate></logic:present>
</table><br />
<logic:present name="persentList" scope="request">
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr><td colspan="5" align="left">赠品列表</td></tr>
<logic:iterate name="persentList" id="item" >
	<tr bgcolor='#F8F8F8'>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="productId" />"  target="_blank"><bean:write name="item" property="name" /></a></td>
		<td align='right'><bean:write name="item" property="price" /></td>
		<td align='center'><bean:write name="item" property="proxyName" /></td>
		<td align='center'><bean:write name="item" property="code" /></td>
		<td align=left><bean:write name="item" property="count" /></td>
	</tr>
</logic:iterate></logic:present>
</table>
<br/>
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor="#F8F8F8">
		<td><a href="forder.do?id=<bean:write name="order" property="id"/>" target=_blank>修改订单</a></td>
	</tr>
</table>
<br/>   
<br/>
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor="#F8F8F8">
		<td>
			<input type=button value="把该订单标记为已处理" onclick="window.location='transact.do?id=<bean:write name="order" property="id"/>&status=2'">
			<input type=button value="取消该订单的处理" onclick="window.location='transact.do?id=<bean:write name="order" property="id"/>&status=0'">
			<input type=button value="把该订单标记为无效" onclick="window.location='transact.do?id=<bean:write name="order" property="id"/>&status=6'">
		</td>
	</tr>
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
<% voOrder unitedOrder = (voOrder)unitedItem; %>
	<tr bgcolor='#F8F8F8'>
		<td align='center'><bean:write name="unitedItem" property="code" /></td>
		<td align='center'><bean:write name="unitedItem" property="name" /></td>
		<td align='center'><bean:write name="unitedItem" property="phone" /></td>
		<td align='center'><bean:write name="unitedItem" property="address" /></td>
		<td align='center'><bean:write name="unitedItem" property="products" /></td>
		<td align='center'><bean:write name="unitedItem" property="price" format="0.00" /></td>
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
%><%= "邮购" %><%
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
		<td width="40" align="center"><font color="#FFFFFF">配送费</font></td>
		<td width="40" align="center"><font color="#FFFFFF">已到款</font></td>
		<td width="40" align="center"><font color="#FFFFFF">类型</font></td>
		<td width="100" align="center"><font color="#FFFFFF">生成时间</font></td>
		<td width="40" align="center"><font color="#FFFFFF">状态</font></td>
		<td width="40" align="center"><font color="#FFFFFF">购买方式</font></td>
	</tr>
<logic:iterate id="samePhoneItem" name="samePhoneOrderList" scope="request">
<% voOrder sameOrder = (voOrder)samePhoneItem; %>
	<tr bgcolor='#F8F8F8'>
		<td align='center'>
<%if(sameOrder.getStatus() != 3 && sameOrder.getStatus() != 6 && sameOrder.getStatus() != 9 && sameOrder.getStatus() != 10){ %>
			<input type="checkbox" name="uniteOrderId" value="<bean:write name="samePhoneItem" property="id" />" />
<% } else { %>
			<input type="checkbox" name="uniteOrderId" value="<bean:write name="samePhoneItem" property="id" />" disabled="disabled" />
<% } %>
		</td>
		<td align='center'><bean:write name="samePhoneItem" property="code" /></td>
		<td align='center'><bean:write name="samePhoneItem" property="name" /></td>
		<td align='center'><bean:write name="samePhoneItem" property="phone" /></td>
		<td align='center'><bean:write name="samePhoneItem" property="address" /></td>
		<td align='center'><bean:write name="samePhoneItem" property="products" /></td>
		<td align='center'><bean:write name="samePhoneItem" property="price" format="0.00" /></td>
		<td align='center'><bean:write name="samePhoneItem" property="postage" /></td>
		<td align='center'><bean:write name="samePhoneItem" property="realPay" /></td>
		<td align='center'><%= (sameOrder.getIsOrder()==1)?"退货":"进货" %></td>
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
%><%= "邮购" %><%
		break;
		case 2:
%><%= "上门自取" %><%
	}
%>
		</td>
	</tr>
</logic:iterate>
	<tr>
		<td colspan="13" align="center">
			<input type="button" value=" 全 选 " onclick="selectAllUnite(true);" />
			<input type="button" value=" 全不选 " onclick="selectAllUnite(false);" />
			<input type="submit" value="合并订单" />
		</td>
	</tr>
</table>
</form>
</logic:notEmpty>
<script>
<%--
if(!confirm("确定要开始处理下一个订单吗？"))
{
	window.location='transact.do?id=<bean:write name="order" property="id"/>&status=0'
}
--%>

</script>
<%}else{%>
<%
	int startHour = StringUtil.toInt(request.getParameter("startHour"));
	int endHour = StringUtil.toInt(request.getParameter("endHour"));
	if(startHour > 0 || endHour > 0){
%>
			<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
            <tr bgcolor="#F8F8F8">
              <td>处理未处理订单，请点击左侧”处理订单“菜单下”未处理订单“</td>
            </tr>
          </table>
<%
	}
%>
			<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
            <tr bgcolor="#F8F8F8">
              <td>
<%
	String status = request.getParameter("status");
	String day = request.getParameter("day");
	String result = StringUtil.convertNull((String)request.getAttribute("result"));
	String tip = "没有需要处理的订单  <input type=button onclick=\"window.location.reload()\" value=\"刷新\">";
	
	if(result.equals("lack order no permission")){
		tip = "对不起，您没有权限操作缺货订单处理！";
	}
	if(result.equals("flush too fast")){
		tip = "对不起，请不要频繁刷新，间隔时间不能小于5秒！  <input type=button onclick=\"window.location.reload()\" value=\"刷新\">";
	}
	
%>
<%if("3".equals(status) && "yesterday".equals(day)){ 
%>
<%=result.equals("flush too fast")?"对不起，请不要频繁刷新，间隔时间不能小于5秒！  <input type=button onclick=\"window.location.reload()\" value=\"刷新\">":"已没有昨天发货未处理的订单，请您处理当天待发货的订单！" %>
<%} else { %>
<%=tip %>
<%} %>
              </td>
            </tr>
          </table>
<%}%>
<%@include file="../footer.jsp"%>
</body>
</html>
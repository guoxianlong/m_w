<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*"%>
<%@ page import="adultadmin.framework.*" %>
<%
    //判断有没有权限
    if(!PermissionFrk.hasPermission(request, PermissionFrk.ORDER_ADMIN)){
		return;
    }
List orderList = (List)request.getAttribute("orderList");
List collectList = (List)request.getAttribute("collectList");
%>
<html>
<title>汇总</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
>>汇总单(订单数量：<%=orderList.size()%>)
          <br><br>

      <table width="90%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
        <tr bgcolor="#4688D6" align="center">
			<td width="10%" align="center"><font color="#FFFFFF">编号</font></td>
			<td width="30%" align="center"><font color="#FFFFFF">商品原名称</font></td>
			<td width="10%" align="center"><font color="#FFFFFF">批发价</font></td>
			<td width="10%" align="center"><font color="#FFFFFF">总量</font></td>
			<td width="10%" align="center"><font color="#FFFFFF">库存<br/>(北京)</font></td>
			<td width="10%" align="center"><font color="#FFFFFF">库存<br/>(广东)</font></td>
			<td width="10%" align="center"><font color="#FFFFFF">代理商</font></td>
			<td width="10%" align="center"><font color="#FFFFFF">操作</font></td>
        </tr>
<logic:present name="collectList" scope="request"> 
<logic:iterate name="collectList" id="item" > 
		<tr bgcolor='#F8F8F8'>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="productId" />"  target=_blank><bean:write name="item" property="code" /></a></td>
		<td align=left><bean:write name="item" property="oriname" /></td>
		<td align=right><bean:write name="item" property="price" format="0.00" /></td>
		<td align=right width="40"><bean:write name="item" property="count" /></td>
		<td align=right width="40"><bean:write name="item" property="stock" /></td>
		<td align=right width="40"><bean:write name="item" property="stockGd" /></td>
		<td align=left><bean:write name="item" property="proxyName" /></td>
		<td align=left><%--<a href="ordersbp.do?productId=<bean:write name="item" property="productId" />&status=3&buymode=<%=request.getParameter("buymode")%>" target="_blank">搜索订单</a> --%></td>
		</tr>
</logic:iterate></logic:present>
<logic:present name="collectPresentList" scope="request">
<logic:iterate name="collectPresentList" id="item" >
		<tr bgcolor='#F8F8F8'>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="productId" />"  target=_blank><bean:write name="item" property="code" /></a></td>
		<td align=left><bean:write name="item" property="oriname" /></td>
		<td align=right><bean:write name="item" property="price" format="0.00" /></td>
		<td align=right width="40"><bean:write name="item" property="count" /></td>
		<td align=right width="40"><bean:write name="item" property="stock" /></td>
		<td align=right width="40"><bean:write name="item" property="stockGd" /></td>
		<td align=left><bean:write name="item" property="proxyName" /></td>
		<td align=left><%--<a href="ordersbp.do?productId=<bean:write name="item" property="productId" />&status=3&buymode=<%=request.getParameter("buymode")%>" target="_blank">搜索订单</a> --%></td>
		</tr>
</logic:iterate></logic:present>
		<tr ><td height=10 colspan=10></td></tr>
<logic:present name="proxyPriceList" scope="request"> 
<logic:iterate name="proxyPriceList" id="item" >
		<tr bgcolor='#F8F8F8'>
		<td align='center'>总计</td>
		<td></td>
		<td align=right><bean:write name="item" property="price" format="0.00"/></td>
		<td></td>
		<td align=left><bean:write name="item" property="name"/></td>
		<td></td>
		<td></td>
		<td></td>
		</tr>
</logic:iterate> </logic:present>
		<tr bgcolor='#F8F8F8'>
		<td align='left' colspan="8">
			<input type="button" value="按产品导出" onclick="window.location.href=window.location.href + '&action=download';" />
			<input type="button" value="按订单导出" onclick="window.location.href=window.location.href + '&action=download2';" />
		</td>
		</tr>
       </table>
	<br>
      <table width="90%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
          <tr bgcolor="#4688D6">
          <td width="80" align="center"><font color="#FFFFFF">订单号</font></td>
          <td width="60" align="center"><font color="#FFFFFF">姓名</font></td>
          <td width="100" align="center"><font color="#FFFFFF">电话</font></td>
          <td align="center"><font color="#FFFFFF">地址</font></td>
          <td width="60" align="center"><font color="#FFFFFF">邮编</font></td>
          <td width="60" align="center"><font color="#FFFFFF">折扣价</font></td>
              <td width="60" align="center"><font color="#FFFFFF">汇款方式</font></td>
              <td width="60" align="center"><font color="#FFFFFF">邮寄方式</font></td>
          <td width="40" align="center"><font color="#FFFFFF">操作</font></td>
        </tr>           
<logic:present name="orderList" scope="request"> 
<logic:iterate name="orderList" id="item" > 
		<tr bgcolor='#F8F8F8'>
		<td align='center'><a href="order.do?id=<bean:write name="item" property="id" />"   target=_blank><bean:write name="item" property="code" /></a></td>
		<td align=left><bean:write name="item" property="name" /></td>
		<td align=left><bean:write name="item" property="phone" /></td>
		<td align=left><bean:write name="item" property="address" /></td>
		<td align=left><bean:write name="item" property="postcode" /></td>
		<td align=right><bean:write name="item" property="dprice" format="0.00" /></td>
		<td align=center width="60">
<%
if(((adultadmin.action.vo.voOrder)item).getBuyMode() == adultadmin.util.Constants.BUY_TYPE_YOUGOU){
	switch(((adultadmin.action.vo.voOrder)item).getRemitType()) {
		case 0:%>工商银行<%break;
		case 1:%>建设银行<%break;
		case 2:%>招商银行<%break;
		case 3:%>广发银行<%break;
		case 4:%>中国银行<%break;
		case 5:%>农业银行<%break;
		case 6:%>邮政储蓄<%break;
	}
}else{
%>&nbsp;<%}%>
		</td>
		<td align=center width="60">
<%switch(((adultadmin.action.vo.voOrder)item).getDeliverType()) {
case 0:%>普通邮寄<%break;
case 1:%>特快专递<%break;
case 2:%>全国快递<%break;
}%>
		</td>
		<td align=left ><a href="forder.do?id=<bean:write name="item" property="id" />"   target=_blank>修改</a></td>
		</tr>
</logic:iterate> </logic:present> 
		<tr><td height=10 colspan=10></td></tr>
		<tr bgcolor='#F8F8F8'>
		<td align='center'>总计</td>
		<td></td><td></td><td></td><td></td>
		<td align=right><bean:write name="totalprice2" format="0.00"/></td>
		<td></td>
		</tr>
       </table>
	<br>
          <table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
            <tr bgcolor="#F8F8F8">
              <td>
              <input type=button value="把这些订单标记为已完成" onclick="window.location='endcollect2.do?buymode=<%=request.getParameter("buymode")%>'">
              </td>
            </tr>
          </table>
          <br>   
<%@include file="../footer.jsp"%>
</body>
</html>
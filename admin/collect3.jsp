<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*"%>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.framework.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%
	voUser adminUser = (voUser)session.getAttribute("userView");
	UserGroupBean group = adminUser.getGroup();
    //判断有没有权限
    if(!PermissionFrk.hasPermission(request, PermissionFrk.ORDER_ADMIN)){
		return;
    }

	boolean isSystem = (adminUser.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (adminUser.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (adminUser.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (adminUser.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (adminUser.getPermission() == 7);	//销售部
	boolean isShangpin = (adminUser.getPermission() == 6);	//商品部
	boolean isTuiguang = (adminUser.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (adminUser.getPermission() == 4);	//运营中心
	boolean isKefu = (adminUser.getPermission() == 3);	//客服部

List orderList = (List)request.getAttribute("orderList");
List collectList = (List)request.getAttribute("collectList");
String selects = (String)request.getAttribute("select");
if(selects == null){
	selects = "";
}
%>
<html>
<title>汇总</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
var dot = "";
var url = window.location.href;
if(url.indexOf("?") > 0){
	dot = "&";
} else {
	dot = "?";
}
</script>
<body>
<%@include file="../header.jsp"%>
>>汇总单(订单数量：<%=orderList.size()%>&nbsp;&nbsp;订单总金额：<bean:write name="totalprice2" format="0.00"/>元)
          <br><br>
      <table width="90%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="center">
        <tr bgcolor="#4688D6" align="center">
			<td width="8%" align="center"><font color="#FFFFFF">编号</font></td>
			<td width="5%" align="center"><font color="#FFFFFF">一级分类</font></td>
			<td width="5%" align="center"><font color="#FFFFFF">二级分类</font></td>
			<td width="30%" align="center"><font color="#FFFFFF">商品原名称</font></td>
			<td width="5%" align="center"><font color="#FFFFFF">买卖宝价</font></td>
			<td width="5%" align="center"><font color="#FFFFFF">产品<br/>状态</font></td>
<%if(group.isFlag(66)){ %>
			<td width="5%" align="center"><font color="#FFFFFF">库存价格</font></td>
<%} %>
			<td width="5%" align="center"><font color="#FFFFFF">总量</font></td>
			<td width="5%" align="center"><font color="#FFFFFF">待验库库存</font></td>
			<td width="5%" align="center"><font color="#FFFFFF">合格库可发货库存</font></td>
			<td width="5%" align="center"><font color="#FFFFFF">退货库库存</font></td>
			<td width="25%" align="center"><font color="#FFFFFF">代理商</font></td>
			<td width="10%" align="center"><font color="#FFFFFF">操作</font></td>
        </tr>
<logic:present name="collectList" scope="request">
<logic:iterate name="collectList" id="item" >
<%voOrderProduct p = (voOrderProduct) item; %>
		<tr bgcolor="#F8F8F8">
			<td align="center"><a href="fproduct.do?id=<bean:write name="item" property="productId" />"  target=_blank><bean:write name="item" property="code" /></a></td>
			<td align="left"><bean:write name="item" property="parentId1Name" /></td>
			<td align="left"><bean:write name="item" property="parentId2Name" /></td>
			<td align="left"><bean:write name="item" property="oriname" /></td>
			<td align="left"><bean:write name="item" property="price" format="0.00" /></td>
			<td align="left"><bean:write name="item" property="productStatusName" /></td>
<%if(group.isFlag(66)){ %>
			<td align="right"><bean:write name="item" property="price5" format="0.00" /></td>
<%} %>
			<td align="right"><bean:write name="item" property="count" /></td>
			<td align="right"><%= p.getStockAllType(ProductStockBean.STOCKTYPE_CHECK) %></td>
			<td align="right"><%= p.getStockAllType(ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
			<td align="right"><%= p.getStockAllType(ProductStockBean.STOCKTYPE_RETURN) %></td>
			<td align="left"><bean:write name="item" property="proxyName" /></td>
			<td align="left">无</td>
		</tr>
</logic:iterate></logic:present>
<logic:present name="collectPresentList" scope="request">
<logic:iterate name="collectPresentList" id="item" >
<%voOrderProduct p = (voOrderProduct) item; %>
		<tr bgcolor="#F8F8F8">
			<td align="center"><a href="fproduct.do?id=<bean:write name="item" property="productId" />"  target=_blank><bean:write name="item" property="code" /></a></td>
			<td align="left"><bean:write name="item" property="parentId1Name" /></td>
			<td align="left"><bean:write name="item" property="parentId2Name" /></td>
			<td align="left"><bean:write name="item" property="oriname" /></td>
			<td align="left"><bean:write name="item" property="price" format="0.00" /></td>
			<td align="left"><bean:write name="item" property="productStatusName" /></td>
<%if(group.isFlag(66)){ %>
			<td align="right"><bean:write name="item" property="price5" format="0.00" /></td>
<%} %>
			<td align="right"><bean:write name="item" property="count" /></td>
			<td align="right"><%= p.getStockAllType(ProductStockBean.STOCKTYPE_CHECK) %></td>
			<td align="right"><%= p.getStockAllType(ProductStockBean.STOCKTYPE_QUALIFIED) %></td>
			<td align="right"><%= p.getStockAllType(ProductStockBean.STOCKTYPE_RETURN) %></td>
			<td align="left"><bean:write name="item" property="proxyName" /></td>
			<td align="left">无</td>
		</tr>
</logic:iterate></logic:present>
		<tr><td height="10" colspan="8" width="100%"></td></tr>
<logic:present name="proxyPriceList" scope="request"> 
<logic:iterate name="proxyPriceList" id="item" >
		<tr bgcolor="#F8F8F8">
		<td align="center">总计</td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td align="right"><bean:write name="item" property="price" format="0.00"/></td>
		<td></td>
		<td></td>
		<td></td>
		<td></td>
		<td align="left"><bean:write name="item" property="name"/></td>
		<td></td>
		</tr>
</logic:iterate> </logic:present>
		<tr bgcolor='#F8F8F8'>
		<td align='left' colspan="13">
			<input type="button" value="按产品导出" onclick="window.location.href=window.location.href + dot + 'action=download&select=<%= selects %>';" />
			<input type="button" value="按订单导出" onclick="window.location.href=window.location.href + dot + 'action=download2&select=<%= selects %>';" />
		</td>
		</tr>
       </table>
	<br>
<%-- 
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
<%if(group.isFlag(36)) /*if(isSystem || isXiaoshou || isKefu || isShangpin)*/{%>
          <td width="40" align="center"><font color="#FFFFFF">操作</font></td>
<%}%>
        </tr>           
<logic:present name="orderList" scope="request"> 
<logic:iterate name="orderList" id="item" > 
		<tr bgcolor='#F8F8F8'>
		<td align='center'><a href="order.do?id=<bean:write name="item" property="id" />"   target=_blank><bean:write name="item" property="code" /></a></td>
		<td align=left><bean:write name="item" property="name" /></td>
		<td align=left><bean:write name="item" property="phone" /></td>
		<td align=left><bean:write name="item" property="address" /></td>
		<td align=left><bean:write name="item" property="postcode" /></td>
		<td align=right><bean:write name="item" property="dprice" format="0.00"/></td>
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
<%if(group.isFlag(36)) /*if(isSystem || isXiaoshou || isKefu || isShangpin)*/{%>
		<td align=left ><a href="forder.do?id=<bean:write name="item" property="id" />"   target=_blank>修改</a></td>
<%}%>
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
 --%>
	<br>
          <table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
            <tr bgcolor="#F8F8F8">
              <td>
             
              </td>
            </tr>
          </table>
          <br>   
<%@include file="../footer.jsp"%>
</body>
</html>
<%@ include file="../taglibs.jsp"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*" %>
<%@ page import="adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.action.vo.voOrder"%>
<%@ page import="adultadmin.bean.stock.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.order.*" %>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
	voOrder vo = null;
%>
<%
	String code = (String)request.getParameter("code");
	if(code == null)code="";
	// 价格
	String price = (String)request.getParameter("price");
	if(price == null)price="";
	
	String name = (String)request.getParameter("name");
	if(name == null)name="";
	String phone = (String)request.getParameter("phone");
	if(phone == null)phone="";else phone=phone.trim();
	String operator = (String)request.getParameter("operator");
	if(operator == null)operator="";
	String prepayDeliver = (String)request.getParameter("prepayDeliver");
	if(prepayDeliver == null)prepayDeliver="";
	String isOrderStr = request.getParameter("isOrder");
	int isOrder = 0;
	if(isOrderStr == null){
		isOrder = 2;
	}
	else {
	    isOrder = StringUtil.StringToId(isOrderStr);
	}
	String consigner = StringUtil.convertNull(request.getParameter("consigner"));
	List orderList = (List)request.getAttribute("orderList");
	
%>
<html>
<head>
<%
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	String dateStr = sdf.format(new Date()).toString();
	String fileName = "订单查询导出列表";
	response.setHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes("GBK"), "iso8859-1") + dateStr + ".xls\"");
	response.setContentType("application/vnd.ms-excel;charset=gb2312");
%>
<meta http-equiv="Content-Type" content="text/html; charset=GB2312" />
<%@include file="../header.jsp"%>
</head>
<body>
 <table width="90%" cellpadding="3" cellspacing="1" border="1">
              <tr>
              <td width="100" align="center">订单号</td>
              <td width="60" align="center">姓名</td>
              <td width="100" align="center">电话</td>
              <td align="center">地址</td>
              <td width="150" align="center">产品名称</td>
              <td width="60" align="center">折扣前</td>
              <td width="60" align="center">折扣后</td>
			  <td align="center">配送费</td>
<%if(group.isFlag(66)){ %>
			  <td align="center">库存价格</td>
<%} %>
			  <td align="center">已到款</td>
			  <td align="center">类型</td>
              <td width="60" align="center">汇款方式</td>
              <td width="60" align="center">购买方式</td>
              <td width="60" align="center">包裹单号</td>
              <td width="60" align="center">包裹重量</td>
              <td width="50" align="center">订单状态</td>
              <td width="80" align="center">生成时间</td>
              <td width="40" align="center">友链id</td>
              <td width="40" align="center">处理人</td>
              <td width="40" align="center">发货人</td>
			  <td width="80" align="center">出货地点</td>
			  <td width="80" align="center">出货时间</td>
			  <td width="40" align="center">快递公司</td>
			  <td width="80" align="center">备注</td>
            </tr>           
<logic:present name="orderList" scope="request"> 
<logic:iterate name="orderList" id="item" >
<%if((((voOrder)item).getStatus() != 0) || group.isFlag(29) || (!StringUtil.isNull(phone) && phone.trim().length() >= 7)){  //只有高级管理员才能看 未处理订单%>
<%
	vo = (voOrder) item;
%>
		<tr>
		<td align=center width="100"><bean:write name="item" property="code" /></td>
		<td align=left width="60"><bean:write name="item" property="name" /></td>
		<td align=left width="100">
		<%
				if(vo.getPhone() != null){
					if(vo.getPhone().length()>=11){
			%>
				<%=vo.getPhone().substring(0, 3)+"****"+vo.getPhone().substring(7) %>
			<%			
					}else{
			%>
					<%=vo.getPhone() %>
			<%		
					}
				}
			%>
		</td>
		<td align=left><%= StringUtil.cutString(vo.getAddress(), 4) %><%if(vo.getAddress() != null && vo.getAddress().length() > 4){ %>...<%} %></td>
		<td align=left width="150"><bean:write name="item" property="products" /></td>
		<td align=left><bean:write name="item" property="price" format="0.00"/></td>
		<td align=left><bean:write name="item" property="dprice" format="0.00"/></td>
		<td align="center">
<%
	if(((voOrder)item).getBuyMode() == Constants.BUY_TYPE_HUODAOFUKUAN && ((voOrder)item).getAreano() == Constants.AREA_NO_QITA && ((voOrder)item).getIsOlduser() == 0){ 
%>
			<bean:write name="item" property="prepayDeliver" format="0.00"/>
<%
	} else {
%>
			<bean:write name="item" property="postage" format="0.00"/>
<%}%>
		</td>
<%if(group.isFlag(66)){ %>
		<td align="center"><bean:write name="item" property="price3" format="0.000"/></td>
<%} %>
		<td align="center"><bean:write name="item" property="realPay" format="0.00"/></td>
		<td align=left><logic:equal name="item" property="agent" value="1"><logic:equal name="item" property="isOrder" value="0">代理进货</logic:equal><logic:equal name="item" property="isOrder" value="1">代理退货</logic:equal></logic:equal><logic:equal name="item" property="agent" value="0">普通</logic:equal></td>
		<td align=center width="60">
<%
if(((voOrder)item).getBuyMode() == Constants.BUY_TYPE_HUODAOFUKUAN){
%>
<%--
	if(((voOrder)item).getAreano() == Constants.AREA_NO_QITA && ((voOrder)item).getIsOlduser() == 0){
		switch(((voOrder)item).getRemitType()) {
		case 0:%>工商银行<%break;
		case 1:%>建设银行<%break;
		case 2:%>招商银行<%break;
		case 3:%>广发银行<%break;
		case 4:%>中国银行<%break;
		case 5:%>农业银行<%break;
		case 6:%>邮政储蓄<%break;
		}
	} else {
--%>无<%--
	}--%>
<%
} else if(((voOrder)item).getBuyMode() == Constants.BUY_TYPE_SHANGMENZIQU) {
%>无<%
} else {
	switch(((voOrder)item).getRemitType()) {
	case 0:%>工商银行<%break;
	case 1:%>建设银行<%break;
	case 2:%>招商银行<%break;
	case 3:%>广发银行<%break;
	case 4:%>中国银行<%break;
	case 5:%>农业银行<%break;
	case 6:%>邮政储蓄<%break;
	}
} %>
		</td>
		<td align=center width="60">
<%switch(((voOrder)item).getBuyMode()) {
case 0:%>货到付款<%break;
case 1:%>钱包支付<%break;
case 2:%>银行汇款<%break;
}%>
		</td>
		<td align=center width="60"><%= ((voOrder)item).getPackageNum() %></td>
		<td align=center width="60"><%= ((voOrder)item).getAuditPakcageBean()!=null?((voOrder)item).getAuditPakcageBean().getWeight()/1000+"KG":"" %></td>
		<td align=center width="50">
<% if(((voOrder)item).getBuyMode() == Constants.BUY_TYPE_HUODAOFUKUAN){ %>
			<logic:equal name="item" property="statusName" value="已到款">待发货</logic:equal>
			<logic:notEqual name="item" property="statusName" value="已到款"><bean:write name="item" property="statusName" /></logic:notEqual>
<% } else { %>
			<bean:write name="item" property="statusName" />
<% } %>
		</td>
		<td align=left width="80" style="mso-number-format:'\@'"><%=((voOrder)item).getCreateDatetime()%></td>
		<td align=left width="40"><bean:write name="item" property="fr" /></td>
		<td align=left width="40"><bean:write name="item" property="operator" /></td>
		<td align=left width="40"><bean:write name="item" property="consigner" /></td>
<%
		//出货记录
		//StockOperationBean oper = vo.getStockOper();
		OrderStockBean oper = vo.getOrderStock();
	    if(oper != null && oper.getStatus() != 3){
			String areaName =ProductStockBean.getAreaName(oper.getStockArea());
%>
		<td width="80" align="center"><%=areaName%></td>
		<td width="80" align="center"><%=oper.getLastOperTime().substring(0, 16)%></td>
<%
		}else {
%>
        <td width="80" align="center">无</td>
        <td width="80" align="center">无</td>
<%
		}
%>
		<td width="40" align="center"><%=vo.deliverMapAll.get(String.valueOf(vo.getDeliver()))!=null?vo.deliverMapAll.get(String.valueOf(vo.getDeliver())):""%></td>
		<td width="80" align="center"><%= StringUtil.convertNull(vo.getRemark()) %></td>
		</tr>
<%} %>
</logic:iterate> </logic:present> 
        </table>
</body>
</html>
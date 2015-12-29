<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %><%@ taglib uri="/tags/struts-html" prefix="html" %><%@ taglib uri="/tags/struts-logic" prefix="logic" %><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.stock.*"%>
<%@ page import="adultadmin.util.*,adultadmin.util.StringUtil"%>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="java.util.*" %>
<% 
	response.setContentType("application/vnd.ms-excel");
	voOrder vo = null;
	int bianjie=0;
	
	String areano = StringUtil.convertNull(request.getParameter("areano"));
	String buymode = StringUtil.convertNull(request.getParameter("buymode"));
	String stockState = StringUtil.convertNull(request.getParameter("stockState"));
	String action = StringUtil.convertNull(request.getParameter("action"));
	int printType = StringUtil.StringToId(request.getParameter("printType"));
	String now = DateUtil.getNow().substring(0, 10);
	String area = null;
	if(areano.equals("0")){
		area = "北京";
	} else {
		area = "广东";
	}
	String strBuymode = null;
	if(buymode.equals("0")){
		strBuymode = "货到付款";
	} else if(buymode.equals("1")){
		strBuymode = "邮购";
	} else if(buymode.equals("2")){
		strBuymode = "上门自取";
	} else {
		strBuymode = "";
	}
	String fileName = now;
	response.setHeader("Content-disposition","attachment; filename=\"" + fileName + ".xls\"");
%>
<%if((areano.equals("0") || areano.equals("1") || areano.equals("2") || areano.equals("3")) && printType == 0){ 
	//FileUtil.writeOrderExcel(request,response);
%>
         <table width="100%" cellpadding="3" cellspacing="1" border="1">
              <tr>
              <td width="50" align="center">序号</td>
              <td width="100" align="center">订单号码</td>
              <td width="50" align="center">产品分类</td>
              <td width="60" align="center">应收货款</td>
              <td width="60" align="center">客户姓名</td>
              <td align="left">客户地址</td>
              <td width="100" align="center">联系电话</td>
              <td width="100" align="center">邮政编码</td>
              <td width="100" align="center">支付方式</td>
              <td width="100" align="center">交寄日期</td>
              <td width="100" align="center">快递公司</td>
              <td width="100" align="center">收寄局</td>
              <td width="100" align="center">寄达城市</td>
              <td width="100" align="center">邮件号码</td>
              <td width="100" align="center">邮件重量</td>
			  <td align="center">备注信息</td>
			  <td width="60" align="center">成品重量</td>
            </tr>
<logic:present name="orderList" scope="request">
<logic:iterate name="orderList" id="item" >
<%
	vo = (voOrder) item;
%>
		<tr bgcolor='#F8F8F8'>
		<%if(request.getParameter("fromPage")==null&&request.getParameter("flag")==null){ %><td></td><%}else{ %><td align="center">&nbsp;<%=vo.getSerialNumber()==0?"":vo.getBatchNum()+"-"+vo.getSerialNumber() %></td><%} %>
		<td align='center'><bean:write name="item" property="code" /></td>
		<td align="center"><bean:write name="item" property="productTypeName" /></td>
		<td align="right"><bean:write name="item" property="dprice" format="0.00"/></td>
		<td align=""><bean:write name="item" property="name" /></td>
		<td align="left"><bean:write name="item" property="address" /></td>
		<td align="left">&nbsp;<bean:write name="item" property="phone" /></td>
		<td align="center">&nbsp;<bean:write name="item" property="postcode" /></td>
		<td align="center">&nbsp;</td>
		<td align="center">&nbsp;</td>
		<td align="center"><bean:write name="item" property="deliverName" /></td>
		<td align="center">&nbsp;</td>
		<td align="center">&nbsp;</td>
		<td align="center">&nbsp;</td>
		<td align="center">&nbsp;</td>
		<td align="right"><bean:write name="item" property="remark"/></td>
		<td align="right"><bean:write name="item" property="BZZL"/></td>
		</tr>
</logic:iterate></logic:present>
          </table> 
<%
	} else if((areano.equals("0") || areano.equals("1") || areano.equals("2") || areano.equals("3") || areano.equals("-1")) && printType == 1){
		Map productMap = (Map)request.getAttribute("productMap");
		int orderIndex = 0;
		int pageIndex = 0;
		String date = DateUtil.getNow().substring(0, 10);
		int totalCount=0;
		float totalPrice=0;
%>
<logic:present name="orderList" scope="request">
<logic:iterate name="orderList" id="item" >
<%
	vo = (voOrder) item;
	List productList = (List)productMap.get(Integer.valueOf(vo.getId()));
	int index = 0;
	if(productList != null){
		Iterator iter = productList.listIterator();
		while(iter.hasNext()){
			voOrderProduct op = (voOrderProduct) iter.next();
			if(index % 8 == 0){
				pageIndex++;
%>
    <table cellpadding="3" cellspacing="1" border="1">
    <tr><td align="center" rowspan="2" colspan="8"><span style="font-size:24px">买卖宝（mmb）发货清单</span></td></tr>
    <tr></tr>
              <tr>
              	<td align="left" colspan="2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%= date %></td>
              	<td align='left' colspan="1"><strong>订单号:<bean:write name="item" property="code" /></strong></td>
              	<td align='left' colspan="1"><strong>姓名:<%=new StringUtil().getString(vo.getName(),8)%></strong></td>
				<td align="left" colspan="4"><%= vo.getDeliverName()%></td>
              </tr>
              <tr>
              <td width="30" align="center">序</td>    
              <td width="55" align="center"><strong>编号</strong></td>
              <td width="" align="center" colspan="2">产品名称</td>
              <td width="50" align="center">数量</td>
              <td width="40" align="center">金额</td>         
              <td width="" align="center" colspan="2">
					<%switch(((voOrder)item).getBuyMode()) {
						case 0:%>货到付款<%break;
						case 1:%>邮购<%break;
						case 2:%>上门自取<%break;
						}%>
				</td>
            </tr>
		<tr bgcolor='#F8F8F8'>
		<td align='center' valign="top" rowspan="8"><%= orderIndex+1 %></td>	
		<td align="left"><strong><%= op.getCode() %></strong></td>
		<td align="center"  colspan="2"><%= op.getOriname()%></td>
		<td align="left"><%= op.getCount() %></td><%totalCount=totalCount+op.getCount(); %>
		<td align="center"><%= NumberUtil.price(op.getCount() * op.getPrice()) %></td><%totalPrice=totalPrice+op.getCount()*op.getPrice(); %>
		<td  align="center" rowspan="2">取货</td>
		<td  align="center" rowspan="2"></td>
		</tr>
<%
			} else {
%>
		<tr bgcolor='#F8F8F8'>
		<td align="left"><%= op.getCode() %></td>
		<td align="center" colspan="2"><%= op.getOriname() %></td>
		<td align="left"><%= op.getCount() %></td><%totalCount=totalCount+op.getCount(); %>
		<td align="center"><%= NumberUtil.price(op.getCount() * op.getPrice()) %></td><%totalPrice=totalPrice+op.getCount()*op.getPrice(); %>
<%if(index % 8 < 2){ %>
 
<%} else if(index % 8 == 2){ %>
		<td align="left" valign="top" rowspan="2">包货</td>
		<td rowspan="2"></td>
<%}  else if(index % 8 == 4){ %>
		<td align="left" valign="top" rowspan="2">重量</td>
		<td rowspan="2"></td>
<%} else if(index % 8 == 6) { %>
		<td align="left" valign="top" rowspan="2">核查</td>
		<td rowspan="2"></td>
<%} %>
		</tr>
<%
			}
			index++;
%>
<%
if(index % 8 == 0){
bianjie++;
%>
</table><table border="0"><tr>
<td colspan="9" align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7*24小时客服电话（免长话费）：400-620-6966&nbsp;&nbsp;&nbsp;&nbsp;买卖宝站点：mmb.cn</td></tr>
<tr></tr><tr></tr><tr></tr></table>
<%
}
%>
<%
		}
	}
	if(index % 8 != 0){
		for(; index % 8 != 0; index++){
%>
		<tr bgcolor='#F8F8F8'>
		<td align="center"></td>
		<td align="center" colspan="2"><%if(index % 8==7){ %>合计<%} %></td>
		<td align="left"><%if(index % 8==7){ %><%=totalCount %><%} %></td>
		<td align="center"><%if(index % 8==7){ %><%=NumberUtil.price(totalPrice) %><%} %></td>
<%if(index % 8 < 2){ %>
		
<%}  else if(index % 8 == 2){ %>
		<td  align="center" rowspan="2">包货</td>
		<td   rowspan="2"></td>
<%}  else if(index % 8 == 4){ %>
		<td align="center" rowspan="2" >重量</td>
		<td rowspan="2"></td>
<%} else if(index % 8 == 6) { %>
		<td align="center" rowspan="2">核查</td>
		<td rowspan="2"></td>
<%} %>
		</tr>
<%
		}
totalCount=0;
totalPrice=0;
	}
%>
<% if(productList.size()!=0){
orderIndex++;
bianjie++;
%>
</table><table border="0"><tr>
<td colspan="9" align="center">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7*24小时客服电话（免长话费）：400-620-6966&nbsp;&nbsp;&nbsp;&nbsp;买卖宝站点：mmb.cn</td></tr>
<tr></tr><tr></tr><tr></tr></table>
<%} %>
</logic:iterate></logic:present>
<%
	} else if((areano.equals("1") || areano.equals("2") || areano.equals("3")) && printType == 0){
		Map productMap = (Map)request.getAttribute("productMap");
		int orderIndex = 0;
		int pageIndex = 0;
		String date = DateUtil.getNow().substring(0, 10);
%>
<table cellpadding="3" cellspacing="1" border="1">
      <tr>
          <td width="30" align="center">序号</td>
          <td width="80" align="center">支付方式</td>
          <td width="100" align="center">订单号</td>
          <td width="80" align="center">产品分类</td>
          <td width="100" align="center">应收货款</td>
          <td width="80" align="center">客户名称</td>
          <td width="200" align="center">客户地址</td>
          <td width="80" align="center">快递公司</td>
          <td width="100" align="center">联系电话</td>
          <td width="150" align="center">产品名称</td>
          <td width="50" align="center">数量</td>
		  <td width="80" align="center">邮件号码</td>
		  <td width="80" align="center">邮件重量</td>
      </tr>
<logic:present name="orderList" scope="request">
<logic:iterate name="orderList" id="item" >
<%
	orderIndex++;
	vo = (voOrder) item;
	List productList = (List)productMap.get(Integer.valueOf(vo.getId()));
	int index = 0;
	if(productList != null){
		Iterator iter = productList.listIterator();
		while(iter.hasNext()){
			voOrderProduct op = (voOrderProduct) iter.next();
			if(index == 0){
				pageIndex++;
%>

		<tr bgcolor='#F8F8F8'>
			<td align='center' valign="top" rowspan="<%= productList.size() %>"><%= orderIndex %></td>
			<td align='center' valign="top" rowspan="<%= productList.size() %>">
					<%switch(((voOrder)item).getBuyMode()) {
						case 0:%>货到付款<%break;
						case 1:%>邮购<%break;
						case 2:%>上门自取<%break;
						}%>
			</td>
			<td align='center' valign="top" rowspan="<%= productList.size() %>"><bean:write name="item" property="code" /></td>
			<td align='center' valign="top" rowspan="<%= productList.size() %>"><bean:write name="item" property="productTypeName" /></td>
			<td align="center" valign="top" rowspan="<%= productList.size() %>"><bean:write name="item" property="dprice" /></td>
			<td align="center" valign="top" rowspan="<%= productList.size() %>"><bean:write name="item" property="name" /></td>
			<td align="left" valign="top" rowspan="<%= productList.size() %>"><bean:write name="item" property="address" /></td>
			<td align="center" valign="top" rowspan="<%= productList.size() %>"><%= vo.getDeliverName() %></td>
			<td align="center" valign="top" rowspan="<%= productList.size() %>"><bean:write name="item" property="phone" /></td>
			<td align="center"><%= op.getOriname() %></td>
			<td align="left"><%= op.getCount() %></td>
			<td align="center" valign="top" rowspan="<%= productList.size() %>"></td>
			<td align="center" valign="top" rowspan="<%= productList.size() %>"></td>
		</tr>
<%
			} else {
%>
		<tr bgcolor='#F8F8F8'>
		<td align="center"><%= op.getOriname() %></td>
		<td align="left"><%= op.getCount() %></td>
		</tr>
<%
			}
			index++;
%>
<%
		}
	}
%>
</logic:iterate></logic:present>
</table>
<%
	} else if(areano.equals("1") || areano.equals("2") || areano.equals("3")){
		Map productMap = (Map)request.getAttribute("productMap");
%>
          <table width="100%" cellpadding="3" cellspacing="1" border="1">
            <tr>
			  <td width="80" align="center">日期</td>
			  <td width="20" align="center">地区</td>
              <td width="100" align="center">购买方式</td>
              <td width="60" align="center">金额</td>
              <td width="100" align="center">订单号</td>
              <td align="center">姓名</td>
              <td width="150" align="center">产品</td>
              <td width="60" align="center">数量</td>
              <td width="60" align="center">收货地址</td>
			  <td align="center">联系电话</td>
			  <td align="center">备注</td>
			  <td align="center">发货备注</td>
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
			voOrderProduct op = (voOrderProduct) iter.next();
			if(index==0){
%>
		<tr bgcolor='#F8F8F8'>
		<td align='center'><%= now %></td>
		<td align='center'>广东</td>
		<td align='center'>
			<%switch(((voOrder)item).getBuyMode()) {
				case 0:%>货到付款<%break;
				case 1:%>邮购<%break;
				case 2:%>上门自取<%break;
				}%>
		</td>
		<td align=left><bean:write name="item" property="dprice" format="0.00"/></td>
		<td align=left><bean:write name="item" property="code" /></td>
		<td align=left><bean:write name="item" property="name" /></td>
		<td align=left><%= op.getOriname() %></td>
		<td align=left><%= op.getCount() %></td>
		<td align=left><bean:write name="item" property="address" /></td>
		<td align=left><bean:write name="item" property="phone"/></td>
		<td align=left><bean:write name="item" property="remark"/></td>
		<td align=left><bean:write name="item" property="stockoutRemark"/></td>
		</tr>
<%
		} else {
%>
		<tr>
		<td align='center'>&nbsp;</td>
		<td align='center'>广东</td>
		<td align='center'>&nbsp;</td>
		<td align=left>&nbsp;</td>
		<td align=left>&nbsp;</td>
		<td align=left>&nbsp;</td>
		<td align=left><%= op.getOriname() %></td>
		<td align=left><%= op.getCount() %></td>
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
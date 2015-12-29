<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %><%@ taglib uri="/tags/struts-html" prefix="html" %><%@ taglib uri="/tags/struts-logic" prefix="logic" %><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.stock.*"%>
<%@ page import="adultadmin.util.*,adultadmin.util.StringUtil"%>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="java.util.*" %>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<% 
int i =1;
	response.setContentType("application/vnd.ms-excel");
	voOrder vo = null;
	int bianjie=0;
	String batchCode = request.getAttribute("batchCode").toString();
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
	response.setHeader("Content-disposition","attachment; filename=\"" + batchCode + ".xls\"");
%>

         <table width="100%" cellpadding="3" cellspacing="1" border="1">
              <tr>
              <td width="50" align="center">序号</td>
              <td width="100" align="center">订单号码</td>
              <td align="left">客户地址</td>
              <td width="50" align="center">价格</td>
              <td width="50" align="center">产品分类</td>
              <td width="100" align="center">邮政编码</td>
              <td width="100" align="center">快递公司</td>
              <td width="100" align="center">客户姓名</td>
              <td width="100" align="center">电话</td>
             
            </tr>
<logic:present name="orderList" scope="request">
<logic:iterate name="orderList" id="item" >
<%
	vo = (voOrder) item;
%>
		<tr bgcolor='#F8F8F8'>
		<td align="center">&nbsp;<%if(vo.getBatchNum()!=0){%><%=vo.getBatchNum()%>-<%=vo.getSerialNumber() %><%} %></td>
		<td align='center'><bean:write name="item" property="code" /></td>
		<td align="center"><bean:write name="item" property="address" /></td>
		<td align="right"><bean:write name="item" property="dprice" format="0.00"/></td>
		<td align="center"><bean:write name="item" property="productTypeName" /></td>
		<td align="center">&nbsp;<bean:write name="item" property="postcode" /></td>
		<td align="center"><bean:write name="item" property="deliverName" /></td>
		<td align="center"><bean:write name="item" property="name" /></td>
		<td align="center"><bean:write name="item" property="phone" /></td>
		</tr><%i++; %>
</logic:iterate></logic:present>
</table> 


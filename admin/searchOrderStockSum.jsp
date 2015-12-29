<%@ page contentType="text/html;charset=utf-8" %>
<%@ taglib uri="/tags/struts-bean" prefix="bean" %><%@ taglib uri="/tags/struts-html" prefix="html" %><%@ taglib uri="/tags/struts-logic" prefix="logic" %><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.stock.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="java.util.*" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
	response.setContentType("application/vnd.ms-excel");

	String now = DateUtil.getNow().substring(0, 10);

	voOrder vo = null;

	String areano = StringUtil.convertNull(request.getParameter("areano"));
	String buymode = StringUtil.convertNull(request.getParameter("buymode"));
	String stockState = StringUtil.convertNull(request.getParameter("stockState"));
	String action = StringUtil.convertNull(request.getParameter("action"));
	int printType = StringUtil.StringToId(request.getParameter("printType"));
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

	List orderList = (List)request.getAttribute("orderList");
	int orderCount = orderList.size();
	Map productMap = (Map)request.getAttribute("productMap");
	Map productOrderMap = new HashMap();
	Map pMap = new HashMap();
	Iterator iter = orderList.listIterator();
	while(iter.hasNext()){
		voOrder order = (voOrder)iter.next();
		List orderProductList = (List)productMap.get(Integer.valueOf(order.getId()));
		if(orderProductList == null){
			continue;
		}
		Iterator opIter = orderProductList.listIterator();
		while(opIter.hasNext()){
			voOrderProduct tempProduct = (voOrderProduct)opIter.next();
			if(productOrderMap.containsKey(tempProduct.getCode())){
				String orders = (String)productOrderMap.get(tempProduct.getCode());
				voOrderProduct temp = (voOrderProduct)pMap.get(tempProduct.getCode());
				temp.setCount(temp.getCount() + tempProduct.getCount());
				orders += "," + order.getCode();
				productOrderMap.put(tempProduct.getCode(), orders);
			} else {
				pMap.put(tempProduct.getCode(), tempProduct);
				productOrderMap.put(tempProduct.getCode(), order.getCode());
			}
		}
	}
	List pList = new ArrayList(pMap.values());
	List pSortList = new LinkedList();
	for(int i=0; i<pList.size(); i++){
		voOrderProduct temp = (voOrderProduct)pList.get(i);
		boolean has = false;
		if(pSortList.size() > 0){
			for(int j=0; j<pSortList.size();j++){
				voOrderProduct tempSort = (voOrderProduct)pSortList.get(j);
				if(tempSort.getCount() <= temp.getCount()){
					pSortList.add(j, temp);
					has = true;
					break;
				}
			}
			if(!has){
				pSortList.add(temp);
			}
		} else {
			pSortList.add(temp);
		}
	}

	String fileName = now;
	response.setHeader("Content-disposition","attachment; filename=\"" + fileName + "\"");
%>
</head>
<body>
<%
	if(pSortList.size() > 0){
%>
<table width="100%" cellpadding="3" cellspacing="1" border="1">
            <tr>
              <td colspan="4" align="left"><%= DateUtil.getNow().substring(0, 10) %>&nbsp;&nbsp;&nbsp;订单数量:<%= orderCount %></td>
            </tr>
            <tr>
              <td width="100" align="center">编号</td>
              <td width="100" align="center">原名称</td>
              <td width="100" align="center">小店名称</td>
              <td width="60" align="center">数量</td>
              <td align="center">订单号</td>
            </tr>
<%
	for(int i=0; i<pSortList.size(); i++){
		voOrderProduct temp = (voOrderProduct) pSortList.get(i);
%>
		<tr bgcolor='#F8F8F8'>
		<td align='center'><%= temp.getCode() %></td>
		<td align='center'><%= StringUtil.toWml(temp.getOriname()) %></td>
		<td align='center'><%= StringUtil.toWml(temp.getName()) %></td>
		<td align="right"><%= temp.getCount() %></td>
		<td align="left">&nbsp;<%= (String)productOrderMap.get(temp.getCode()) %></td>
		</tr>
<%
	}
%></table>
<%}%>
</body>
</html>
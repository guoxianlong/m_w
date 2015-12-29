<%@page import="adultadmin.util.StringUtil"%>
<%@ page contentType="text/html; charset=utf-8" language="java" import="java.sql.*" errorPage=""%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.action.vo.voProduct"%>
<%@ page import="adultadmin.action.vo.voProductLine"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html >
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<%
    voUser user = (voUser)session.getAttribute("userView");
    StatStockAction action = new StatStockAction();
 	action.fcEnchangeOrderList(request, response);
 	List list = (List)request.getAttribute("list");
 	String tip=(String)request.getAttribute("tip");
 	if(tip!=null && tip.length()>0){
 		%><script>alert('<%= tip %>');</script>
 <%
 	}
 %>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/JS_functions.js"></script>
<script language="JavaScript">
function checksubmit(){
     var checked=false;
		var ids= document.getElementsByName("checkBox");
		for(var i=0;i<ids.length;i++){
			if(ids[i].checked){
				checked=true;
			}
		}
		if(!checked){
			   alert("请至少选择一个您要导出的产品！");
			return false;
		}
}
</script>
<title>可调拨发货的订单列表</title>
<style type="text/css">
<!--
.STYLE1 {color: #FF0000}
.STYLE2 {font-weight: bold}
.STYLE3 {font-weight: bold}


-->
</style>
</head>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">

<body>&nbsp;&nbsp;&nbsp;可调拨发货的订单列表&nbsp;&nbsp;<span class="STYLE2"><a href="./fcEnchangeOrderList.jsp">芳村地区</a>&nbsp;</span>|&nbsp;<a href="./zcEnchangeOrderList.jsp">增城地区</a>
<form name="thisform" action="./enchangeOrderExcel.jsp?flag=fc" method="post" onsubmit="return checksubmit();" >
  <table width="99%" cellpadding="3" cellspacing="0"    border="0">
    <tr bgcolor="#4688D6">
      <td><div align="center"><font color="#FFFFFF">选</font>
       <input type=checkbox onclick="setAllCheck(thisform,'checkbox',this.checked)">
      </div></td>
      <td><div align="center"><font color="#FFFFFF">订单号</font></div></td>
      <td><div align="center"><font color="#FFFFFF">产品编号</font></div></td>
      <td><div align="center"><font color="#FFFFFF">原名称</font></div></td>
      <td><div align="center"><font color="#FFFFFF">订购量</font></div></td>
      <td><div align="center"><font color="#FFFFFF">芳村缺货量</font></div></td>
      <td><div align="center"><font color="#FFFFFF">需增城调拨量</font></div></td>
      <td><div align="center"><font color="#FFFFFF">芳村可发货库存</font></div></td>
      <td><div align="center"><font color="#FFFFFF">增城可发货库存</font></div></td>
      <td><div align="center"><font color="#FFFFFF">一级分类</font></div></td>
      <td><div align="center"><font color="#FFFFFF">产品名称</font></div></td>
    </tr>
    <%if(list!=null && list.size()>0) {
      	for(int i=0;i<list.size();i++){
      		HashMap map = (HashMap) list.get(i);
      		Iterator iter = map.entrySet().iterator(); 
      		while (iter.hasNext()) { 
      			java.util.Map.Entry  entry = (java.util.Map.Entry) iter.next(); 
      			Object key =entry.getKey(); 
      			List valueList= (List)entry.getValue(); 
      			if(valueList!=null && valueList.size()>0){
      				for(int j=0;j<valueList.size();j++){
      					ProductStockBean p = (ProductStockBean)valueList.get(j);
      					if(p.getProduct() == null || p.getProduct().getOrderProduct() == null){
      						continue;
      					}
      					int qhCount=p.getProduct().getOrderProduct().getCount()-p.getStock();//缺货量
      					int count=p.getProduct().getOrderProduct().getCount();//购买数量
      					int stock = p.getProduct().getStock(3,0);//增城库存量
    %>
   <tr <%if(i%2==0){%> bgcolor="#EEE9D9"<%}%>>
      <td  ><div align="center"> <%if(qhCount<=0){%>
        <%} else{%>
       <input type="checkbox" name="checkbox"  id="checkbox" value="<%=p.getProduct().getId()+"-"+qhCount+"-"+stock%>" ><%} %>
      </div></td>
      <%if(j==0){ %><td rowspan="<%=valueList.size()%>"  ><div align="center"><%=key%></div></td><%} %>
      <td  ><div align="center"><%=p.getProduct().getCode()%></div></td>
      <td  ><div align="center"><%=p.getProduct().getOriname()%></div></td>
      <td  ><div align="center"><%=p.getProduct().getOrderProduct().getCount()%></div></td>
      <td  ><div align="center"><span class="STYLE3">
        <%if(qhCount>0) {%>
        <span class="STYLE1"><%=qhCount%></span></span><strong>
          <%}else{%>0<%} %>
        </strong></div></td>
      <td  ><div align="center"> <%if(qhCount>0) {%><%=qhCount%><%}else{ %>0<%} %></div></td>
      <td  ><div align="center"><%=p.getStock() %></div></td>
      <td  ><div align="center"><%=p.getProduct().getStock(3,0)%></div></td>
      <td  ><div align="center"><%=p.getProduct().getParent1().getName() %></div></td>
      <td  ><div align="center"><%=p.getProduct().getName()%></div></td>
    </tr>
    <%}}}}}%>
  </table>
<p><input type=submit name="Submit" value="导出选中商品的调拨量" /></p>

</form>
</body>
</html>

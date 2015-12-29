<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<html>
<title>买卖宝后台</title>
<script>
function addproduct(code,pid){
	document.aproduct.productId.value=pid;
	if(confirm('将添加产品id为'+pid+'的产品，数量'+document.aproduct.count.value+'，确认？'))
		document.aproduct.submit();
}
function check(input, productId, type){
	return true;
	var inputCount;
	if(type == 1){
		inputCount = document.getElementById(productId + "stock");
	} else if(type == 2){
		inputCount = document.getElementById(productId + "stockGd");
	} else {
		alert('错误的库存类型');
		input.focus();
		return false;
	}
	var count = inputCount.value;
	var outCount = input.value;
	if(outCount > count){
		alert('库存不够，请检查');
		input.focus();
		return false;
	}
	return true;
}
</script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<%@include file="../header.jsp"%>
<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
	<tr bgcolor='#F8F8F8'>
		<td align=center  width="100">订单编号</td>
		<td ><a href="order.do?id=<bean:write name="order" property="id" scope="request"/>"><bean:write name="order" property="code" scope="request"/></a>&nbsp;&nbsp;&nbsp;(下单时间<bean:write name="order" property="createDatetime" scope="request"/>)</td>
	</tr>
	<tr bgcolor='#F8F8F8'>
		<td align=center >包裹单号码</td>
		<td ><bean:write name="order" property="packageNum" scope="request"/></td>
	</tr>
</table>

<logic:equal name="order" property="stockout" value="0">
<form action="stockout.do" method=post onsubmit="return confirm('确认要出库吗？');">
</logic:equal>
<logic:equal name="order" property="stockout" value="1">
<form action="stockout.do" method=post onsubmit="alert('已经出库！');return false;">
</logic:equal>
<input type="hidden" name="orderId" value="<bean:write name="order" property="id" scope="request"/>" />
          <table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
            <tr bgcolor="#4688D6">
              <td align="center"><font color="#FFFFFF">名称</font></td>
		      <td width="60" align="center"><font color="#FFFFFF">价格</font></td>
		      <td width="100" align="center"><font color="#FFFFFF">代理商</font></td>
              <td align="center" width=80><font color="#FFFFFF">编号</font></td>
              <td width="40" align="center"><font color="#FFFFFF">数量</font></td>
              <td width="40" align="center"><font color="#FFFFFF">库存<br/>(北京)</font></td>
              <td width="40" align="center"><font color="#FFFFFF">库存<br/>(广东)</font></td>
              <td width="60" align="center"><font color="#FFFFFF">出库量<br/>(北京)</font></td>
              <td width="60" align="center"><font color="#FFFFFF">出库量<br/>(广东)</font></td>
            </tr>
<logic:present name="orderProductList" scope="request"> 
<logic:iterate name="orderProductList" id="item" >
		<input type="hidden" name="productId" value="<bean:write name="item" property="productId" />" />
		<tr bgcolor='#F8F8F8'>
		<td align='center'><a href="fproduct.do?id=<bean:write name="item" property="productId" />" ><bean:write name="item" property="name" /></a></td>
		<td align='right'><bean:write name="item" property="price" /></td>
		<td align='center'><bean:write name="item" property="proxyName" /></td>
		<td align='center'><bean:write name="item" property="code" /></td>
		<td align=right width="40"><bean:write name="item" property="count" /></td>
		<td align=right width="40"><bean:write name="item" property="stock" />
			<input type="hidden" id="<bean:write name="item" property="productId" />stock" value="<bean:write name="item" property="stock" />" />
		</td>
		<td align=right width="40"><bean:write name="item" property="stockGd" />
			<input type="hidden" id="<bean:write name="item" property="productId" />stockGd" value="<bean:write name="item" property="stockGd" />" />
		</td>
		<td align=right width="60"><input name="beijing" value="<bean:write name="item" property="count" />" size="6" onlostfocus="check(this,<bean:write name="item" property="productId" />, 1)"></td>
		<td align=right width="60"><input name="guangdong" value="0" size="6" onlostfocus="check(this, <bean:write name="item" property="productId" />, 2)"></td>
		</tr>
</logic:iterate></logic:present>
			<tr bgcolor="#4688D6">
              <td align="center" colspan="7">
              <logic:equal name="order" property="stockout" value="0">
              	<input type="submit" value="出库" />
              	<input type="reset" value="重填" />
              </logic:equal>
              <logic:equal name="order" property="stockout" value="1">
              	<input type="submit" value="出库" disabled="disabled" />
              	<input type="reset" value="重填" disabled="disabled" />
              </logic:equal>
              </td>
            </tr>
          </table>
</form>
		  <form method=post action="mstockinproduct.do" name="aproduct">
          <table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>
            <tr bgcolor="#F8F8F8">
              <td>
              </td>
            </tr>
          </table>
          </form>
          
          <br>   
</body>
</html>
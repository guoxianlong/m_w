<%@ include file="../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.bean.stock.*"%>
<%@ page import="adultadmin.bean.order.OrderStockBean"%>
<%@ page import="adultadmin.action.vo.voOrder" %>
<%@ page import="adultadmin.util.*" %>
<html>
	<head>
		<title>发货单导出和打印</title>
		<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript">
	function checksubmit(flag){
		var reg=new RegExp("^(\\d)|(\\n)$");
		var orders = document.getElementById("orders");
		if(orders.value==""){
			alert("订单编号不能为空！");
			orders.focus();
			return false;
		}
		var str = orders.value.split("\n");
		var numlen = str.length;
		var count=0;
		if(numlen>300){
			for(var i=0;i<numlen;i+=1){
				if(str[i] && trim(str[i]).length>0)
					count++;
			}
		}
		if(count>300){
			alert("订单编号至多允许输入300个\n已输入"+count+"个！");
			orders.focus();	
			return false;
		}
		var orderForm = document.getElementById("searchOrders");
		document.getElementById("flag").value=flag;
		if(flag==1){
			document.getElementById("printType").value=0;
			orderForm.target="";
		}else if(flag==2){
			document.getElementById("printType").value=1;
			orderForm.target="_blank";
		}else{
			orderForm.target="";
		}
			
		orderForm.submit();
		return true;
	}
	function trim(str){   
		return str.replace(/^\s+|\s+$/g,"");   
	}
	window.onload=function (){
		document.getElementById("orders").focus();
	};
</script>
<%
	String orders = request.getParameter("orders");
%>
	</head>
	<body>
		<p><b>发货单导出及打印</b></p>
		<form action="<%=request.getContextPath() %>/admin/orderStockExportPrint.do" id="searchOrders" method="post">
		<div style="width: 100%; height: 100px;">
			<div style="width:53%; vertical-align:middle; float: left;">订单编号：<textarea style="vertical-align:middle; " rows="8" cols="50" name="orders" id="orders"><%=orders==null?"":orders %></textarea>
			<input type="hidden" name="stockState" value="1"/>
			</div>
			<div style="width:46%; #position: absolute; _position:absolute; vertical-align:top;  color: red; float: right;">
			<span style="color: black;"> 输入格式：</span><br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;订单编号1 <br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;订单编号2<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.....
			</div>
		</div>
		<div style="clear: none; "> 
			<input type="button" value="按地址导出excel文件" onclick="checksubmit(1);">
			<input type="button" value="打印发货清单" onclick="checksubmit(2);">
			<input type="button" value="查询" onclick="checksubmit(3);">
			<input type="hidden" value="" name="flag" id="flag">
			<input type="hidden" value="0" name="areano">
			<input type="hidden" value="" name="printType" id="printType">
			<a href="<%=request.getContextPath()%>/admin/orderStockBatchPrint.do?date=<%=DateUtil.getNowDateStr() %>"><font color="red">按批次打印</font></a>
		</div>
		</form>
           <table width="100%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8">
              <tr bgcolor="#4688D6">
              <td width="10" align="center"><font color="#FFFFFF">序号</font></td>
              <td width="100" align="center"><font color="#FFFFFF">订单号</font></td>
              <td width="60" align="center"><font color="#FFFFFF">收货人</font></td>
              <td width="120" align="center"><font color="#FFFFFF">产品名称</font></td>
              <td width="50" align="center"><font color="#FFFFFF">产品分类</font></td>
              <td width="60" align="center"><font color="#FFFFFF">应收货款</font></td>
              <td align="center"><font color="#FFFFFF">客户地址</font></td>
              <td align="center"><font color="#FFFFFF">邮编</font></td>
              <td width="60" align="center"><font color="#FFFFFF">寄达<br/>地点</font></td>
              <td width="60" align="center"><font color="#FFFFFF">成品<br/>重量</font></td>
              <%--
              <td width="60" align="center"><font color="#FFFFFF">北速<br/>邮资</font></td>
              <td width="60" align="center"><font color="#FFFFFF">广速<br/>邮资</font></td>
              <td width="60" align="center"><font color="#FFFFFF">邮资<br/>差值</font></td>
              <td width="60" align="center"><font color="#FFFFFF">节约<br/>比例</font></td>
              --%>
              <td width="60" align="center"><font color="#FFFFFF">发货重量<br/>的平均值</font></td>
              <td width="80" align="center"><font color="#FFFFFF">快递公司</font></td>
              <td width="80" align="center"><font color="#FFFFFF">确认申请<br/>出库时间</font></td>
			  <td align="center" width="100"><font color="#FFFFFF">发货备注</font></td>
			  </tr>
<logic:present name="orderList" scope="request"> 
<logic:iterate name="orderList" id="item" > 
<%
	voOrder vo = (voOrder) item;
%>
		<tr bgcolor='#F8F8F8'>
		<td width="30px"><%=vo.getSerialNumber()==0?"": vo.getBatchNum()+"-"+vo.getSerialNumber()%></td>
		<td align='center'><a href="order.do?id=<bean:write name="item" property="id" />" >
			<%if(vo.getFlat() == 1){%><font color="blue"><%} else if(vo.isStockDeleted()){%><font color="red">
			<%} else {%><font color=""><%} %><bean:write name="item" property="code" /></font></a>
		</td>
		<td align="center"><bean:write name="item" property="name" /></td>
		<td align="center"><bean:write name="item" property="products" /></td>
		<td align="center"><bean:write name="item" property="productTypeName"/></td>
		<td align="right"><bean:write name="item" property="dprice" format="0.00"/></td>
		<td align="left"><bean:write name="item" property="address" /></td>
		<td align="left"><bean:write name="item" property="postcode" /></td>
		<td align="left"><bean:write name="item" property="destination" /></td>
		<td align="right"><bean:write name="item" property="BZZL" format="0" /></td>
		<td align="right"><%if(vo.getDeliver() == 7){ %><bean:write name="perWeightBJ" scope="request" /><%} else if(vo.getDeliver() == 8){ %><bean:write name="perWeightGD" scope="request" /><%} %></td>
        <td align="center"><bean:write name="item" property="deliverName"/></td>
		<td align="center"><bean:write name="item" property="orderStock.lastOperTime" /></td>
		<td align=left><bean:write name="item" property="stockoutRemark"/></td>
		</tr>
	</logic:iterate></logic:present>
	</table>
	</body>
</html>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.order.OrderStockBean"%>
<%@ page import="adultadmin.bean.cargo.CargoInfoBean"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page import="mmb.stock.stat.SortingAgainBean"%>
<%@ page import="adultadmin.action.vo.voUser"%><html>
<head>
<title>分拣异常单再次分拣</title>
<%
ProductStockBean psBean = new ProductStockBean();
OrderStockBean osBean = (OrderStockBean)request.getSession().getAttribute("osBean");
CargoInfoBean cargoInfo = (CargoInfoBean)request.getSession().getAttribute("cargoInfoBean");
List<SortingAgainBean> saBeanList = (ArrayList<SortingAgainBean>)request.getSession().getAttribute("saBeanList");
int areaId = StringUtil.toInt((String)request.getSession().getAttribute("area"));
String error = (String)request.getAttribute("error");
%>
<script type="text/javascript">
<%if(error != null){%>
	alert("<%=error%>");
<%}%>
function getFocus(){
	document.getElementById("code").value="";
	document.getElementById("code").focus();
}
</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="getFocus()">
<form action="<%=request.getContextPath()%>/admin/stockOperation.do?method=sortingAgain" method="post">
<div style="width:200px;height:270px;border-style:solid;border-width:0px;border-color:#000000;margin-top:-10px;">
<table width="200px" height="240px" border="0" cellspacing="0" tyle="overflow:scroll;">
	<tr height="15%">
		<td colspan="2">
			<table border="0" width="97%">
				<tr>
					<td align="left"><font size="3" style="font-weight:bold">异常订单处理</font><font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></td></td>
					<td align="right" width="60%"><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
					[<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout"><font color="red" size="2">注销</a></font>]</td>
				</tr>
				<tr>
					<td align="left"><input type="text" id="code" name="code" value="" size="20"/></td>
					<td align="right"><input type="submit"  value="查  询" size="8"/></td>
				</tr>
			</table>
		</td>
	</tr>
	<%if(osBean != null){%>
	<tr height="5%">
		<td align="left">
			<font color="red"><span><%=osBean.getCode() %></font> 分拣异常!</span>
		</td>
		<td align="right">
			 请分拣:&nbsp;&nbsp;&nbsp;
		</td>
	</tr>
	<%} %>
	<tr height="100%" >
		<td colspan="2">
			<div style="width:97%; height:100%; overflow:scroll;">
			<table border="0" width="100%" style="overflow:scroll;">
				<tr align="center"  bgcolor="#8B4513" >
					<td align="center" width="35%"><font size="2">货位</font></td>
					<td align="center" width="31%"><font size="2">商品编号</font></td>
					<td align="center" width="20%"><font size="2">未处理</font></td>
					<td align="center" width="15%"><font size="2">订购</font></td>
				</tr>
				<% 
					if(saBeanList != null && saBeanList.size() > 0){
						for(SortingAgainBean bean : saBeanList){ 
							StringBuffer sb = new StringBuffer();
							String newWholeCode = bean.getWholeCode().substring(bean.getWholeCode().indexOf("-")+1);
							if(newWholeCode.length()==6){
								sb.append(newWholeCode.substring(0,3));
								sb.append("-");
								sb.append(newWholeCode.substring(3,4));
								sb.append("-");
								sb.append(newWholeCode.substring(4,6));
							}else if(newWholeCode.length()==8){
								sb.append(newWholeCode.substring(0,3));
								sb.append("-");
								sb.append(newWholeCode.substring(3,5));
								sb.append("-");
								sb.append(newWholeCode.substring(5,6));
								sb.append("-");
								sb.append(newWholeCode.substring(6,8));
							}
						%>
				<tr align="center" <%if(bean.getCount()==bean.getTotal()){ %> bgcolor="#FF4500" <% }else
									 if(bean.getCount()<bean.getTotal() && bean.getCount()>0){ %>bgcolor="#FFD700" <% }else{%> bgcolor="#FFFFFF" <%} %>>
					<td align="center" width="35%"><font size="2"><%=sb.toString() %></font></td>
					<td align="center" width="31%"><font size="2"><%=bean.getProudctCode() %></font></td>
					<td align="center" width="20%"><font size="2"><%=bean.getCount() %></font></td>
					<td align="center" width="15%"><font size="2"><%=bean.getTotal() %></font></td>
				</tr>
				<tr align="left" <%if(bean.getCount()==bean.getTotal()){ %> bgcolor="#FF4500" <% }else
									 if(bean.getCount()<bean.getTotal() && bean.getCount()>0){ %>bgcolor="#FFD700" <% }else{%> bgcolor="#FFFFFF" <%} %>>
					<td colspan="4"><font size="2"> <%=bean.getProudctName() %></font></td>
				</tr>
				<tr>
					<td colspan="4"<%if(bean.getCount()==bean.getTotal()){ %> bgcolor="#FF4500" <% }else
									 if(bean.getCount()<bean.getTotal() && bean.getCount()>0){ %>bgcolor="#FFD700" <% }else{%> bgcolor="#FFFFFF" <%} %>>
									 <font size="2"><%=bean.getRecommendWhole() %></font></td>
				</tr>
				<%}	}%>
			</table>
			</div>
		</td>
	</tr>
</table>
<div align="right" style="width:220px ">
	<input type="button" value="返回" style="height:20px" size="9" onclick="window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=stockOperation&toPage=huoweiyichang&flag=sortingAgain';"/>
	</div>
</div>
</form>
</body>
</html>
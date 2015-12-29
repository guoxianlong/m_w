<%@page import="adultadmin.bean.cargo.CargoProductStockBean"%>
<%@page import="mmb.stock.cargo.CargoDeptAreaService"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.util.StringUtil"%>
<%@ page import="java.util.Map,adultadmin.action.vo.voOrder"%>

<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@page import="adultadmin.bean.UserGroupBean"%>
<%@page import="mmb.stock.stat.*"%>
<html>
<head>
<title>添加波次页面</title>
</head>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
if(!group.isFlag(428)){%>
	<script type="text/javascript">
	alert("您没有权限进行此操作！");
	window.close();
	</script>
<%}
	String carrier = StringUtil.convertNull(request.getParameter("carrier"));
    String carrier1 = StringUtil.convertNull(request.getParameter("carrier1"));
	Map deliverMapAll = voOrder.deliverMapAll;
	List areaList = CargoDeptAreaService.getCargoDeptAreaList(request);
	String reload = (String)request.getAttribute("load");
	String areaLable = ProductWarePropertyService.getWeraAreaOptionsCustomized("area","area",request,-1,true,"-1");
%>
<script type="text/javascript"> 
function reload(){
	<%if(reload!=null&&reload.equals("load")){%>
	   window.opener.location.reload();
	   window.close();
	   <%}%>
}
function checksubmit(){
  //  if(confirm("如果确认提交申请,请单击'确定',反之请单击'取消'！"))
 //	{
       document.getElementById("flag").value = 1
 //	   return true;
 //	}
 //	else
 //	{
 //	   return false;
 //	}
}
</script>  
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<body>
<form name="addMainlingBatch" id="addMainlingBatch" action=<%=request.getContextPath()+"/admin/mailingBatch.do?method=addMailingBatch" %> method="post" onsubmit="return checksubmit();" >
	<table border="3" style="border: solid thin black" cellspacing="0"
		bgcolor="#FFCC00" bordercolor="#FFCC00">
		<tr>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td><div align="left">
					<strong>指定归属仓库</strong>：
				</div>
			</td>
			<td>

				<div align="left">
					<%= areaLable%>
				</div>
			</td>
			<td>&nbsp;&nbsp;&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td><div align="left">
					<strong>指定归属配送渠道</strong>：
				</div>
			</td>
			<td>

				<div align="left">
					<select name="deliver" id="deliver">
						<%
							Iterator deliverIter = deliverMapAll.entrySet().iterator();
							int i = 0;
							while (deliverIter.hasNext()) {
								i++;
								Map.Entry entry = (Map.Entry) deliverIter.next();
						%>
						<option value=<%=entry.getKey()%>><%=entry.getValue()%></option>
						<%
							}
						%>
					</select>
				</div>
			</td>
			<td>&nbsp;&nbsp;&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td><div align="left">
					<strong>指定归属承运商：</strong>
				</div>
			</td>
			<td>
				<div align="left">
					<input name="carrier1" id="carrier1" type="text" width="200px"
						onfocus="if(this.value=='选填'){this.value=''}"> <input
						name="carrier" id="carrier" type="hidden"
						<%if (carrier.equals("") || carrier.length() == 0) {%> value="选填"
						<%} else {%> value="<%=carrier1%>" <%}%> />
				</div>
			</td>
			<td>&nbsp;&nbsp;&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td>
				<div align="center">
					<input type="submit" name="Submit" value="确定" />
					<input type="hidden" name="flag"  id="flag" value=""/>
				</div></td>
			<td>
				<div align="center">
					<input type="submit" name="Submit2" value="取消"
						onClick="window.close()" />
				</div></td>
			<td>&nbsp;&nbsp;&nbsp;</td>
		</tr>
		<tr>
			<td>&nbsp;&nbsp;&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;</td>
			<td>&nbsp;&nbsp;&nbsp;</td>
		</tr>
	</table>
	</form>
	<script type="text/javascript">
		document.getElementById("carrier1").value = document.getElementById("carrier").value;
		reload();
	</script>
</body>
</html>
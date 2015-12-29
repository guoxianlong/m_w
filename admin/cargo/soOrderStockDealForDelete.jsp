<%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.action.vo.voUser,mmb.stock.stat.*,adultadmin.bean.order.*"%>
<% String area = (String)request.getSession().getAttribute("area"); 
	int areaId = StringUtil.toInt(area);
	ProductStockBean psBean = new ProductStockBean();
	voUser user = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	boolean flag = true;
	if(!group.isFlag(618)){
		flag = false;
	}
	int isCache = 0;
	SortingAbnormalBean saBean = (SortingAbnormalBean) session.getAttribute("EXSortingAbnormalInfo");
	if( saBean != null ) {
		isCache = 1;
	}
	OrderStockBean osBean = (OrderStockBean) session.getAttribute("EXOrderStockInfo");
	String tip = (String)request.getAttribute("tip");
	String error = (String) request.getAttribute("error");
%>
<head>
<title>异常订单处理--撤单</title>
<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<style type="text/css">
		table {
			margin:0;
			padding:0;
		}
	</style>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
<script type="text/javascript">
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		function clear() {
			document.getElementById("code").value = "";
			document.getElementById("code").focus();
		}
		function checkIsSubmit() {
			var isCache = '<%= isCache%>';
			var code = document.getElementById('code').value;
			code = code.trim();
			if( code == "" ) {
				alert("输入值不能为空！");
				return false;
			}
			if( code.indexOf('CK') != -1 ) {
				if( isCache == '1' ) {
					if( window.confirm("确认完成异常处理？") ) {
						return true;
					}
					return false;
				} else {
					return true;
				} 
			} else {
				return true;
			}
			
		}
		function goToUpLevel() {
			window.location="<%= request.getContextPath()%>//admin/stockOperation.do?method=stockOperation&toPage=huoweiyichang";
		}
		function alertSomething() {
			<%
				if( tip == null || tip.equals("") ) { 
			%>
			
			<%
			 	} else { 
			%>
				alert('<%= tip%>');
			<%
				}
			%>
			
			<%
				if( error == null || error.equals("") ) { 
			%>
			
			<%
			 	} else { 
			%>
				alert('<%= error%>');
			<%
				}
			%>
			
		}
	</script>
</head>
<body background="<%=request.getContextPath() %>/image/soBg.jpg"  style="overflow:hidden" onload="javascript:clear();alertSomething();">
<div style="width:230px;height:260px;border-style:solid;border-width:1px;border-color:#000000;margin-top:-10px;">
   		<div style="width:230px;height:250px;margin-top:0px;">
   			<div>
   				<strong><font color="black" size="4">异常订单处理</font></strong>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<strong><font color="red" size="4"><%=psBean.getAreaName(areaId) %></font></strong>
				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
				<%=((voUser)request.getSession().getAttribute("userView")).getUsername() %>
				[<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout"><font color="red" size="2">注销</a></font>]
   			</div>
   			<div style="margin-top:-8px;">
   					<form name="form1" action="<%= request.getContextPath()%>/admin/stockOperation.do?method=orderStockDealForDelete" method="post" onsubmit="return checkIsSubmit();">
   						<input type="text" size="13" name="code" id="code" /> 
   						<input type="hidden" name="wareArea" id="wareArea" value="<%= areaId%>" />
   						<input type="submit" name="buttonS" id="buttonS" value="查询" style="height:22px;width:40px;" />
   					</form>
   			</div>
   			<div style="margin-top:-8px;">
   				<font color="black" size="2"><%= osBean == null ?  "" : osBean.getCode()%></font>
   				<strong><font color="red" size="5"><%= osBean == null ?  "" : "撤单！"%></font></strong>
   				<font color="red" size="3"><%= osBean == null ?  "" : "请还原实物："%></font>
   			</div>
   			<%
   				if( saBean != null ) {
   				%>
   			<div style="margin-top:-2px;overflow:scroll;width:230px;height:130px;">
   				<%
   					List<SortingAbnormalProductBean> list = saBean.getSortingAbnormalProductList();
   					int x = list.size();
   					for( int i = 0 ; i < x; i++ ) {
   					SortingAbnormalProductBean sapBean = list.get(i);
   					if( i == 0 ) {
   			%>
   				<table align= "center" width= "98%" border= "0" cellspacing= "1px" bgcolor="#D8D8D5" cellpadding="1px">
   					<tr bgcolor="<%= sapBean.getLockCount()  == 0 ? "#28FF28" : sapBean.getLockCount() > 0 && sapBean.getLockCount() < sapBean.getCount() ? "yellow" : "#FFFFFF"%>">
   						<td align="center" width="37%">货位</td>
   						<td align="center" width="37%">商品编号</td>
   						<td align="center" width="17%">未处理</td>
   						<td align="center" width="9%">订购</td>
   					</tr>
   					<tr bgcolor="<%= sapBean.getLockCount()  == 0 ? "#28FF28" : sapBean.getLockCount() > 0 && sapBean.getLockCount() < sapBean.getCount() ? "yellow" : "#FFFFFF"%>">
   						<td align="center" width="37%"><strong><font color="red" size="3"><%= sapBean.getCargoCodePartly()%></font></strong></td>
   						<td align="center" width="37%"><%= sapBean.getProductCode()%></td>
   						<td align="center" width="17%"><strong><font color="red" size="3"><%= sapBean.getLockCount()%></font></strong></td>
   						<td align="center" width="9%"><%= sapBean.getCount()%></td>
   					</tr>
   					<tr bgcolor="<%= sapBean.getLockCount()  == 0 ? "#28FF28" : sapBean.getLockCount() > 0 && sapBean.getLockCount() < sapBean.getCount() ? "yellow" : "#FFFFFF"%>">
   						<td colspan="4" ><%= sapBean.getProduct().getName()%></td>
   					</tr>
   				</table>
   				<%
   					} else {
   				%>
   				<div style="height:2px;"></div>
   				<table  align= "center" width= "98%" border= "0" cellspacing= "1px" bgcolor="#D8D8D5" cellpadding="1px">
   					<tr bgcolor="<%= sapBean.getLockCount()  == 0 ? "#28FF28" : sapBean.getLockCount() > 0 && sapBean.getLockCount() < sapBean.getCount() ? "yellow" : "#FFFFFF"%>">
   						<td align="center" width="37%"><strong><font color="red" size="3"><%= sapBean.getCargoCodePartly()%></font></strong></td>
   						<td align="center" width="37%"><%= sapBean.getProductCode()%></td>
   						<td align="center" width="17%"><strong><font color="red" size="3"><%= sapBean.getLockCount()%></font></strong></td>
   						<td align="center" width="9%"><%= sapBean.getCount()%></td>
   					</tr>
   					<tr bgcolor="<%= sapBean.getLockCount()  == 0 ? "#28FF28" : sapBean.getLockCount() > 0 && sapBean.getLockCount() < sapBean.getCount() ? "yellow" : "#FFFFFF"%>">
   						<td colspan="4" ><%= sapBean.getProduct().getName()%></td>
   					</tr>
   				</table>
   				<%
   					}
   					}
   				%>
   			</div>
   		<%
   			}
   		%>
   		<div style="margin-left:150px;"><button style="height:22px;width:80px;" onclick="goToUpLevel();">返回</button></div>
   	</div>
   	</div>

</body>
</html>

<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page import="mmb.stock.stat.*" %>
<%
	String monthDate = StringUtil.convertNull(request.getParameter("monthDate"));
	int areaId = StringUtil.toInt(StringUtil.convertNull(request.getParameter("areaId")));
	List list = (List) request.getAttribute("dateList");
%>
<html>
  <head>
    
    <title>质检排班计划</title>
    
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
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script type="text/javascript">
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		function updateAreaId(){
			var areaId = document.getElementById("areaId").value;
			document.getElementById("areaIdHidden").value = areaId;
		}
		function checkNumber(obj) {
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   		}	
   		
   		function synMonthDate() {
   			var val = document.getElementById("monthDate").value;
   			alert(val);
   			document.getElementById("monthDate2").value=val;
   			document.getElementById("monthDate3").value=val;
   		}

   		function checkChangeMonth() {
   			var val = document.getElementById("monthDate").value;
   			var count = document.getElementById("mStaffCount").value;
   			if( count == null || count == "" ) {
   				alert("请填写月在编人数再提交！");
   				return false;
   			}
   			if( val == null || val == "") {
   				return false;
   			} else {
   				document.getElementById("monthDate2").value=val;
   				return true;
   			}
   		}
   		
   		function addToSubmit(obj, tail) {
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    			document.getElementById("dStaffCount_" + tail).value = obj.value;
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   		}	
	</script>

  </head>
  <body>
  	<div align="center">
  		<table align="center" width="40%" border="0" cellspacing="1px" bgcolor="#000000" cellpadding="1px">
  			<tr align="center">
  				<td bgcolor="#FFFFFF">
  				<a href="productWarePropertyAction.do?method=getCheckEffectInfo">商品质检分类与效率</a>
  				</td>
  				<td bgcolor="#4DFFFF">
  					<a href="productWarePropertyAction.do?method=getCheckStaffWorkPlanInfo">质检排班计划</a>
  				</td>
  			</tr>
  		</table>
  	</div>
  	<div align="center">
  	<h2>质检排班计划————在编人数</h2>
  	<form action="productWarePropertyAction.do?method=getCheckStaffWorkPlanInfo" method="post">
  	地区:
  	<% List areaList = CargoDeptAreaService.getCargoDeptAreaList(request); 
		%>
  	<select id="areaId" name="areaId" onchange="updateAreaId()">
			<%
				for(int i = 0;i<areaList.size();i++){
					ProductStockBean psBean = new ProductStockBean();
					int area = Integer.parseInt(areaList.get(i).toString());
			%>
			<option value="<%=areaList.get(i)%>" <%if(areaId == area){ %> selected="selected" <%} %>><%=psBean.getAreaName(area) %></option>
			<%
				}
			%>
  	</select>
  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;月份:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  	<input type="text" id="monthDate" name="monthDate" onfocus="WdatePicker({dateFmt:'yyyy-MM'})" class="Wdate" value="<%= monthDate %>" />
  		<input type="submit" value="查看在编人数"/><br/>
	</form>
	<form action="productWarePropertyAction.do?method=addMonthStaffCount" method="post" onsubmit="return checkChangeMonth();">
	当月在编:<input type="text" id="mStaffCount" name="mStaffCount" onblur="checkNumber(this);" />
			<input type="hidden" name="monthDate2" id="monthDate2" value="<%= monthDate %>"/>
			<input type="hidden" name="areaIdHidden" id="areaIdHidden" value="<%= areaId %>"/>
	<input type="submit" value="确认提交"/><br/>
	</form>
	每日在岗人数：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<br/>
	<span id="numberTip"></span>
	<form action="productWarePropertyAction.do?method=editDayStaffCount" method="post" onsubmit="" >
		<input type="hidden" name="monthDate3" id="monthDate3" value="<%= monthDate %>"/>
		<input type="hidden" name="area" id="area" value="<%= areaId %>"/>
	<table align="center" width="20%" border="0" cellspacing="1px" bgcolor="#000000" cellpadding="1px" >
		<tr bgcolor="#95CACA" >
			<td align="center">
			日期
			</td>
			<td align="center">
			在编人数
			</td>
		</tr>
		<% for (int i = 0; i < list.size(); i ++ )  {
			CheckStaffBean csb = (CheckStaffBean)list.get(i);
			
		%>
		<tr bgcolor="#FFFFFF">
			<td align="center">
			<%= csb.getNikeName() %>
			</td>
			<td align="center">
				<input type="text" size="9" value="<%= csb.getDayCount()%>" onchange="addToSubmit(this,'<%= csb.getNikeName() %>');" />
				<input type="hidden" name="dStaffCount_<%= csb.getNikeName() %>" id="dStaffCount_<%= csb.getNikeName() %>" value="-1" />
			</td>
		</tr>
		<% 
		} 
		%>
	</table>
	<input type="submit" value="确认" />
	</form>
	</div>
  </body>
  
</html>

<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="adultadmin.bean.PagingBean" %>
<%@ page import="mmb.stock.stat.*,java.text.*" %>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*" %>
<%
	int productLine = 0;
	String completeTime1 = "";
	String completeTime2 = "";
	String buyStockinCode = "";
		String productCode = "";
		String checkStockinMissionCode = "";
	int wareArea = -1;
	List productLineList = (List) request.getAttribute("productLineList");
	List list = (List) request.getAttribute("checkBatchList");
	PagingBean paging = (PagingBean)request.getAttribute("paging");
	productLine = StringUtil.toInt(request.getParameter("productLine"));
	completeTime1 = StringUtil.convertNull(request.getParameter("completeTime1"));
	completeTime2 = StringUtil.convertNull(request.getParameter("completeTime2"));
	buyStockinCode = StringUtil.convertNull(request.getParameter("buyStockinCode"));
	productCode = StringUtil.convertNull(request.getParameter("productCode"));
	String productName = StringUtil.convertNull(request.getParameter("productName"));
	String supplierName = StringUtil.convertNull(request.getParameter("supplierName"));
	String unqualifyNumber1 = StringUtil.convertNull(request.getParameter("unqualifyNumber1"));
	String unqualifyNumber2 = StringUtil.convertNull(request.getParameter("unqualifyNumber2"));
	String unqualifyRate1 = StringUtil.convertNull(request.getParameter("unqualifyRate1"));
	String unqualifyRate2 = StringUtil.convertNull(request.getParameter("unqualifyRate2"));
	checkStockinMissionCode = StringUtil.convertNull(request.getParameter("checkStockinMissionCode"));
	wareArea = StringUtil.toInt(request.getParameter("wareArea"));
	DecimalFormat df = new DecimalFormat("0.00");//格式化小数，不足的补0
	String wareAreaLable = ProductWarePropertyService.getWeraAreaOptionsAllWithRight(request,wareArea);
%>
<html>
<head>
<title>My JSP 'returnOrderInfo.jsp' starting page</title>
    
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
	<script type="text/javascript" >
		
	function compareDate() {
	   var time1 = document.getElementById("completeTime1_id");
	   var time2 = document.getElementById("completeTime2_id");
	   if( (time1.value == "" && time2.value != "") || (time1.value != "" && time2.value == "")) {
		document.getElementById("time_span_compare").innerHTML="<font color='red'>开始时间和结束时间需要成对填写</font>";
		return false;
	   }else {
	   	if ( time1.value != "" && time2.value != "" && time2.value >= time1.value) {
	    return true;
	   } else if( time1.value != "" && time2.value != "" && time1.value > time2.value ) {
	   	document.getElementById("time_span_compare").innerHTML="<font color='red'>后面的日期需要大于或等于前面的日期</font>";
	   	return false;
   		}
	   }
	   
 	} 
 	
 	function compareUnqualifyNumber() {
 		var number1 = document.getElementById("unqualifyNumber1").value;
 		var number2 = document.getElementById("unqualifyNumber2").value;
 		if( (number1 != "" && number2 == "" ) || (number1 == "" && number2 != "" )) {
 			document.getElementById("unqualifiedNumber_span").innerHTML="<font color='red'>该数值需要成对填写</font>";
 			return false;
 		} else {
 			return true;
 		}
 		
 	}
 	
 	function compareUnqualifyRate() {
 		var number1 = document.getElementById("unqualifyRate1").value;
 		var number2 = document.getElementById("unqualifyRate2").value;
 		if( (number1 != "" && number2 == "" ) || (number1 == "" && number2 != "" )) {
 			document.getElementById("unqualifiedRate_span").innerHTML="<font color='red'>该数值需要成对填写</font>";
 			return false;
 		} else {
 			return true;
 		}
 	}
 	
 	function clearTimeSpan() {
 		document.getElementById("time_span_compare").innerHTML="";
 	}
 	
 	function clearNumberSpan() {
 		document.getElementById("unqualifiedNumber_span").innerHTML="";
 	}
 	
 	function clearRateSpan() {
 		document.getElementById("unqualifiedRate_span").innerHTML="";
 	}
 	
 	function checkForm1() {
 		
 		var time1 = document.getElementById("completeTime1_id");
	    var time2 = document.getElementById("completeTime2_id");
	    if( (time1.value == "" && time2.value != "") || (time1.value != "" && time2.value == "")) {
		 document.getElementById("time_span_compare").innerHTML="<font color='red'>开始时间和结束时间需要成对填写</font>";
		 return false;
	    } else if( time1.value != "" && time2.value != "" && time1.value > time2.value ) {
	    	document.getElementById("time_span_compare").innerHTML="<font color='red'>后面的日期需要大于或等于前面的日期</font>";
	    	return false;
   		 }
   		 
   		 
	    
	    var number1 = document.getElementById("unqualifyNumber1").value;
 		var number2 = document.getElementById("unqualifyNumber2").value;
 		if( (number1 != "" && number2 == "" ) || (number1 == "" && number2 != "" )) {
 			document.getElementById("unqualifiedNumber_span").innerHTML="<font color='red'>该数值需要成对填写</font>";
 			return false;
 		}
	    
	    
	   	var rate1 = document.getElementById("unqualifyRate1").value;
 		var rate2 = document.getElementById("unqualifyRate2").value;
 		if( (rate1 != "" && rate2 == "" ) || (rate1 == "" && rate2 != "" )) {
 			document.getElementById("unqualifiedRate_span").innerHTML="<font color='red'>该数值需要成对填写</font>";
 			return false;
 		}
 		
 		return true;
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
   		
   		function checkNumberRate(obj) {
   			var pattern = /^[0-9]{1,3}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    			
	    			if(parseInt(number) >= 0 && parseInt(number) <= 100 ) {
	    			
	    			} else {
		    			obj.value="";
			   			obj.focus();
	    				alert("请填写 在 0 到 100 间的数值！");
	    			}
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
<table align="center" width="95%">
		<tr><td align="left">
		<b>质检结果查询:</b>			
		</td></tr>
	</table>
<div style="border-style:solid;border-color:#000000;border-width:1px;width:95%;">
	<form action="checkStockinMissionAction.do?method=getCheckReportInfo" method="post" onsubmit="return checkForm1();">
	<table cellspacing="8px"><tr><td align="left">
	产品编号：
	<input type="text" size="13" name="productCode" id="productCode" value="<%= productCode %>" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	原名称：
	<input type="text" size="20" name="productName" id="productName" value="<%= productName %>" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	供应商名称：
	<input type="text" size="20" name="supplierName" id="supplierName" value="<%= supplierName %>" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
		</td></tr>
		<tr><td align="left">
	产品线：
		<select name="productLine">
								<option value="0">请选择</option>
							<%
							for (int p = 0; productLineList != null && p < productLineList.size(); p++) {
								voProductLine proLineBean = (voProductLine) productLineList.get(p);
							%>
								<option value="<%= proLineBean.getId() %>" <%= proLineBean.getId() == productLine ? "selected" : "" %> ><%= proLineBean.getName() %></option>
							<%
							}
							%>
						</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	
	预计单号：
		<input type="text" name="buyStockinCode" value="<%= buyStockinCode %>" size="13"  />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	质检任务单号：
		<input type="text" name="checkStockinMissionCode" value="<%= checkStockinMissionCode %>" size="13" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		库地区: 
		<%= wareAreaLable%>
		
		</td></tr>
		<tr>
		<td align="left">
			任务完成时间：
		<input type="text" size="9" name="completeTime1" id="completeTime1_id" value="<%= completeTime1%>"  onclick="WdatePicker();" onfocus="clearTimeSpan();"/>
		到
		<input type="text" size="9" name="completeTime2" id="completeTime2_id" value="<%= completeTime2%>" onclick="WdatePicker();" onfocus="clearTimeSpan();" />
		<span id="time_span_compare"></span>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		不合格数：
			<input size="4" type="text" name="unqualifyNumber1" id="unqualifyNumber1" value="<%= unqualifyNumber1%>" onfocus="clearNumberSpan();" onblur="checkNumber(this);" />
		 到
		 	<input size="4" type="text" name="unqualifyNumber2" id="unqualifyNumber2" value="<%= unqualifyNumber2%>" onfocus="clearNumberSpan();" onblur="checkNumber(this);" />
		 	<span id="unqualifiedNumber_span"></span>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		不合格率：
			<input size="4" type="text" name="unqualifyRate1" id="unqualifyRate1" value="<%= unqualifyRate1%>" onfocus="clearRateSpan();" onblur="checkNumberRate(this);" />%
		 到
		 	<input size="4" type="text" name="unqualifyRate2" id="unqualifyRate2" value="<%= unqualifyRate2%>" onfocus="clearRateSpan();" onblur="checkNumberRate(this);"/>%
		 	<span id="unqualifiedRate_span"></span>
			&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			
		
		<input type="submit"  value="  查 询   " />
		
		</td>
		</tr>
		</table>
		</form>
	</div>
	</br>
	<table align="center" width="95%">
		<tr><td align="left">
		<b>生成质检报表：</b>			
		</td></tr>
	</table>
	<div style="border-style:solid;border-color:#000000;border-width:1px;width:95%;">
	<form action="checkStockinMissionAction.do?method=exportCheckReportInfo" method="post">
	商品线：
		<select name="productLine2">
								<option value="0">请选择</option>
							<%
							for (int p = 0; productLineList != null && p < productLineList.size(); p++) {
								voProductLine proLineBean = (voProductLine) productLineList.get(p);
							%>
								<option value="<%= proLineBean.getId() %>" ><%= proLineBean.getName() %></option>
							<%
							}
							%>
						</select>
						&nbsp;&nbsp;&nbsp;
		
	任务完成时间：
		<input type="text" size="13" name="completeTime" onclick="WdatePicker();" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="submit" value="生成质检报表" />
		&nbsp;&nbsp;&nbsp;
		 说明：已质检的任务单才能生成质检报表
		</form>
	</div>
	<br>
	<table align="center" width="95%">
		<tr><td align="left">
		<h3>质检结果列表：</h3>			
		</td></tr>
	</table>
	<table align="center" width="95%" border="0" cellspacing="1px" bgcolor="#000000" cellpadding="1px" >
		<tr bgcolor="#81C0C0" >
			<td align="center">
			产品编号
			</td>
			<td align="center">
			供应商名称
			</td>
			<td align="center">
			小店名称
			</td>
			<td align="center">
			原名称
			</td>
			<td align="center">
			预计到货单号
			</td>
			<td align="center">
			地区
			</td>
			<td align="center">
			商品线
			</td>
			<td align="center">
			预计到货量
			</td>
			<td align="center">
			实际到货量
			</td>
			<td align="center">
			合格数量
			</td>
			<td align="center">
			不合格数量
			</td>
			<td align="center">
			不合格率
			</td>
			<td align="center">
			不合格原因说明
			</td>
		</tr>
		
		<%
			if( list != null && list.size() > 0 ) {
			
				for(int i = 0; i < list.size(); i++ ) {
					CheckStockinMissionBatchBean csmbb = (CheckStockinMissionBatchBean) list.get(i);
		%> 
		<tr bgcolor="#FFFFB9" >
			<td align="center">
			<%= csmbb.getProduct().getCode()%>
			</td>
			<td align="center">
			<%= csmbb.getSupplierName()%>
			</td>
			<td align="center">
			<%= csmbb.getProduct().getName()%>
			</td>
			<td align="center">
			<%= csmbb.getProduct().getOriname()%>
			</td>
			<td align="center">
			<%= csmbb.getCheckStockinMission().getBuyStockinCode()%>
			</td>
			<td align="center">
			<%= csmbb.getCheckStockinMission().getWareAreaName()%>
			</td>
			<td align="center">
			<%= csmbb.getProduct().getProductLineName()%>
			</td>
			<td align="center">
			<%= csmbb.getBuyStockProduct().getBuyCount()%>
			</td>
			<td align="center">
			<%= csmbb.getStockinCount()%>
			</td>
			<td align="center">
			<%= csmbb.getQualifiedCount()%>
			</td>
			<td align="center">
			<%= csmbb.getUnqualifiedNumber()%>
			</td>
			<td align="center">
			<%= csmbb.getCheckCount() == 0 ? "" : df.format(((double)csmbb.getUnqualifiedNumber()/(double)csmbb.getCheckCount())*100) +"%"%>
			</td>
			<td align="center">
			<%= csmbb.getUnqualifiedReasons()%>
			</td>
		</tr>
		<%
			}
		} else {
		%>
		<tr bgcolor="#FFFF93" >
			<td align="center" colspan="13">
				没有不合格记录或没有符合查询条件的记录
			</td>
		</tr>
		<%
			}
		%>
	</table>
	<br>
	<%if(paging!=null){ %>
		<p align="center" style="MARGIN-LEFT: 20px"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
</div>
</body>
</html>

<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.order.UserOrderPackageTypeBean" %>
<%@ page import="java.util.*"%>
<%@ page import="mmb.stock.stat.*,adultadmin.bean.stock.*,adultadmin.bean.bybs.*" %>
<%@ page import="adultadmin.bean.*" %>
<%
	int rightMark = 0;
	voUser user = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	if( group.isFlag(760) ) {
		rightMark = 1;
	}
	ClaimsVerificationBean cvBean = (ClaimsVerificationBean) request.getAttribute("claimsVerificationBean");
	voOrder vorder = (voOrder) request.getAttribute("userOrder");
 	int userid = user.getId();
 	String orderPrice = (String)request.getAttribute("orderPrice");
 	String skuPrice =  (String)request.getAttribute("skuPrice");
 	String mailPrice = (String)request.getAttribute("mailPrice");
 	String packPrice = (String)request.getAttribute("packPrice");
%>
<html>
  <head>
    
    <title>审核理赔核销单</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<style type="text/css">
		form{margin:0px;display: inline}
	</style>
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script type="text/javascript">
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
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
   		
   		function backUp() {
   			var productCount = document.getElementById("productCount").value;
   			var count = parseInt(productCount, 10);
   			if( count > 0) {
   				alert("请在删除完理赔核销单的现有商品后，再更改订单！");
   				return;
   			}
   		}
   		
   		function auditClaims(id, yesOrNo) {
   			var target = '<%= cvBean.getPrice()%>';
   			if( yesOrNo == "1" ) {
	   			if( target <= "0.00" ) {
	   				alert("当前的理赔金额为0，请输入理赔金额并保存！");
	   				return;
	   			}
   			} 
   			if( window.confirm("你确认要审核理赔单么？")) {
   				window.location="<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=auditClaimsVerification&id="+id+"&yesno="+yesOrNo;
   				return;
   			} else {
   				return;
   			}
   		}
   		
   		function completeClaims(id) {
   			var target = '<%= cvBean.getPrice()%>';
   			if( target <= "0.00" ) {
   				alert("当前的理赔金额为0，请输入理赔金额并保存！");
   				return;
   			}
   			if( window.confirm("你确认要完成理赔单么？")) {
   				window.location="<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=completeClaimsVerification&id="+id;
				return; 
	   		} else {
	   			return;
	   		}
   		}
   		
   		function changePrice(mark) {
   			if( mark == 0 ) {
   				$("#cvPrice").attr("value", "<%= orderPrice%>");
   			} else if ( mark == 1 ) {
   				$("#cvPrice").attr("value", "<%= skuPrice%>");
   			} else if( mark == 2 ) {
   				$("#cvPrice").attr("value", "<%= mailPrice%>");
   			}else if( mark == 3 ) {
   				$("#cvPrice").attr("value", "<%= packPrice%>");
   			}
   			if( mark != '<%= cvBean.getType() %>') {
   				remindSave();
   			} else {
   				cleanRemind();
   			}
   		}
   		
   		function checkSubmit2() {
   			/*
   			if( !($("#cvType0").attr("checked") || $("#cvType1").attr("checked") || $("#cvType2").attr("checked")) ) {
   				alert("请选择理赔方式！");
   				return false;
   			}
   			*/
   			if( $("#cvPrice").val() == "" )  {
   				alert("请填写或选择价格！");
   				return false;
   			}
   			if( !($("#cvIsTicket0").attr("checked") || $("#cvIsTicket1").attr("checked")) ) {
   				alert("请选择是开具发票！");
				return false;
   			}
   			return true;
   		}
   		function checkRight() {
   			<%
   				if( rightMark == 0 ) {
   				
   			%>
   				alert("您没有理赔金额修改的权限！");
   				document.getElementById("cvPrice").blur();
   			<%
   				}
   			%>
   		}
   		
   		function checkFloat(obj) {
   			var pattern = /^[0-9]{1,10}[\.]{0,1}[0-9]{0,2}$/;
   			var pattern2 = /^[0-9]{1,10}[\.]{1}$/;
   			var pattern3 = /^[0-9]{11,}$/;
   			var pattern4 = /^[0-9]{1,10}[\.]{1}[0-9]{2,}$/;
   			var number = obj.value;
   			if( number != "" ) {
   				if (pattern2.exec(number)) {
   					obj.value="";
	   				obj.focus();
	   				alert("输入格式有误！");
	   				return;
	   			} else if(pattern3.exec(number)) {
	   				obj.value="";
	   				obj.focus();
	   				alert("整数部分大于10位了！");
	   				return;
	   			} else if(pattern4.exec(number)) {
	   				var x = Math.round(number*100)/100;
	   				obj.value = x;
	   			} else if (pattern.exec(number)) {
	    			return;
	   			} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入正确的数值（小数点后不多于两位整数位不大于十位）");
	    			return;
	   			}
   			}
   			if( number != '<%= cvBean.getPrice()%>' ){
   				remindSave();
   			} else {
   				cleanRemind();
   			}
   		}
   		function changeIsTicket(mark) {
   			if( mark != '<%= cvBean.getIsTicket()%>' ) {
   				remindSave();
   			} else {
   				cleanRemind();
   			}
   		}
   		function remindSave() {
   			document.getElementById("remindSave").innerHTML="<font color='red'>当前理赔信息有未保存的编辑!</font>";
   		}
   		function cleanRemind() {
   			document.getElementById("remindSave").innerHTML="";
   		}
   		
   		function changeClaimsType(obj) {
   			var value = obj.value;
   			if( value == "0" ) {
   				var list  = $('.claimsTypeSelect');
   				for( var i = 0; i < list.length; i++ ) {
   					var sel = list[i];
   					sel.value="0"
   				}
   			} else if ( value == "1" ) {
   				var list  = $('.claimsTypeSelect');
   				for( var i = 0; i < list.length; i++ ) {
   					var sel = list[i];
   					if( sel.value == "0" || sel.value == "2" ) {
   						sel.value = "1";
   					}
   				}
   			} else if ( value == "2" ) {
   				var list  = $('.claimsTypeSelect');
   				for( var i = 0; i < list.length; i++ ) {
   					var sel = list[i];
   					sel.value="2"
   				}
   			} else if ( value == "3" ) {
   				var list  = $('.claimsTypeSelect');
   				for( var i = 0; i < list.length; i++ ) {
   					var sel = list[i];
   					if( sel.value == "0" || sel.value == "2" ) {
   						sel.value = "3";
   					}
   				}
   			}
   			remindSaveClaimsType();
   			return;
   		}
   		
   		function remindSaveClaimsType() {
   			document.getElementById("buttonDiv").innerHTML="<button onclick='document.recalculateForm.submit();'>保存重新计算理赔金额</button>&nbsp;&nbsp;&nbsp;&nbsp;<font color='red'>需要保存编辑!</font>";
   		}
	</script>
  </head>
  <body>
  <br/>
  	<input type="hidden" name="id" id="id_id" value="<%= cvBean.getId()%>" />
  	<div style="width:80%;margin-left:10%;">
  	理赔单号： <%= cvBean.getCode()%>
  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  	库地区： <%= StringUtil.convertNull((String)(ProductStockBean.areaMap.get(new Integer(cvBean.getWareArea()))))%>
  	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
  	状态：<%= cvBean.getStatusName()%> <%= cvBean.getStatus() == ClaimsVerificationBean.CLAIMS_CONFIRM ? "<button onclick='auditClaims("+cvBean.getId()+", 1);'>审核通过</button>&nbsp;&nbsp;&nbsp;<button onclick='auditClaims("+cvBean.getId()+", 0);'>审核不通过</button>" : cvBean.getStatus() == ClaimsVerificationBean.CLAIMS_AUDIT_FAIL ? "" : cvBean.getStatus() == ClaimsVerificationBean.CLAIMS_AUDIT ? "<button onclick='completeClaims(" + cvBean.getId() + ")' >确认完成</button>" : ""%>
	<% if(cvBean.getStatus() == ClaimsVerificationBean.CLAIMS_AUDIT || cvBean.getStatus() == ClaimsVerificationBean.CLAIMS_AUDIT_FAIL || cvBean.getStatus() == ClaimsVerificationBean.CLAIMS_COMPLETE ) { %>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	审核人：<%= cvBean.getAuditUserName()%>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	审核时间：<%= StringUtil.cutString(cvBean.getAuditTime(), 0, 19)%>
  	<% } %>
  	</div>
  	<br/>
	<%
		List list = cvBean.getClaimsVerificationProductList();
		int x = list.size();
		if( x > 0 ){
	%>

	<div id="formDiv" align="center">
		<form name="recalculateForm" action="<%= request.getContextPath()%>/admin/claimsVerificationAction.do?method=recalculateClaimsPrice" method="post" >
			<input type="hidden" name="id" value="<%= cvBean.getId()%>"/>
			<table align='center' width='82%' border='0' cellspacing='1px' bgcolor='#D8D8D5' cellpadding='1px' id='orderInfoTable' >
			<tbody>
			<tr bgcolor='#484891' >
			<td align='center'>
			<font color="#FFFFFF">订单号</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">包裹单号</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">快递公司</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">原名称</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">产品线</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">产品编号</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">数量</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">有无实物</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">理赔方式</font>
			</td>
			</tr>
			<tr bgcolor='#FFFFFF' >
			<td align='center' rowspan='<%= x%>'>
			<%= vorder.getCode()%>
			</td>
			<td align='center' rowspan='<%= x%>'>
			<%= cvBean.getPackageCode()%>
			</td>
			<td align='center' rowspan='<%= x%>'>
			<%= voOrder.deliverMapAll.get(""+vorder.getDeliver())%>
			</td>
			<%
				for( int i = 0; i < x; i++ ) {
				ClaimsVerificationProductBean cvpBean = (ClaimsVerificationProductBean) list.get(i);
				if( i == 0 ) {
					
				} else {
			
			%>
			<tr bgcolor='#FFFFFF' >
			<%
				}
			%>
				<td align='center'>
				<%= cvpBean.getProduct().getOriname()%>
				</td>
				<td align='center'>
				<%= cvpBean.getProductLineName()%>
				</td>
				<td align='center'>
				<%= cvpBean.getProduct().getCode()%>
				</td>
				<td align='center'>
				<%= cvpBean.getCount()%>
				</td>
				<td align='center'>
				<%= cvpBean.getExist() == 0 ? "无" : "有"%>
				</td>
				<% if( cvBean.getStatus() == ClaimsVerificationBean.CLAIMS_COMPLETE || cvBean.getStatus() == ClaimsVerificationBean.CLAIMS_AUDIT_FAIL ) { %>
				<td align='center'>
				<%= cvpBean.getClaimsTypeName() %>
				</td>
				<% } else { %>
				<td align='center'>
				<select name='claims_type_<%= cvpBean.getProduct().getId()%>' id='claims_type_<%= cvpBean.getProduct().getId()%>' onchange='changeClaimsType(this);' class='claimsTypeSelect'>
				<option value='0' <%= cvpBean.getClaimsType() == 0 ? "selected" : ""%>>
				整单理赔</option>
				<option value='1' <%= cvpBean.getClaimsType() == 1 ? "selected" : ""%>>
				按sku理赔</option>
				<option value='2' <%= cvpBean.getClaimsType() == 2 ? "selected" : ""%>>
				按三倍运费理赔</option>
				<option value='3' <%= cvpBean.getClaimsType() == 3 ? "selected" : ""%>>
				包装理赔</option>
				</select>
				</td>
				<% }%>
				</tr>
			<%
				}
			%>
			</tbody>
			</table>
			</form>
		</div>
		<br/>
		<br/>
		<div id="buttonDiv" align="center"></div>
	<%
		} else {
	%>
	<div id="formDiv" align="center">
			<table align='center' width='82%' border='0' cellspacing='1px' bgcolor='#D8D8D5' cellpadding='1px' id='orderInfoTable' >
			<tbody>
			<tr bgcolor='#484891' >
			<td align='center'>
			<font color="#FFFFFF">订单号</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">包裹单号</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">快递公司</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">原名称</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">产品线</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">产品编号</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">数量</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">有无实物</font>
			</td>
			<td align='center'>
			<font color="#FFFFFF">理赔方式</font>
			</td>
			</tr>
			<tr bgcolor="#FFFFFF">
				<td colspan="8">
				理赔单中无商品
				</td>
			</tr>
			</tbody>
			</table>
	</div>
			<%
				}
			%>
	<div align="left" style="margin-left:8%;">
		<br/>
				<br/>
				<%
					if( cvBean.getStatus() != ClaimsVerificationBean.CLAIMS_COMPLETE ) {
				%>
				<fieldset style="width:64%;">
				<legend>理赔信息</legend>
				理赔原因： <%= cvBean.getReasonTypeName() %> &nbsp;&nbsp;&nbsp;备注：<%= cvBean.getReasonRemark() %><br/>
				<form name="form2" action="<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=addClaimsInfo" method="post" onsubmit="return checkSubmit2();" >
				<input type="hidden" value="<%= cvBean.getId()%>" name="id" />
				<!-- 
				理赔方式：   <input type="radio" name="cvType" id="cvType0" value="0" <%= cvBean.getType() == 0 ? "checked" : ""%> onclick="changePrice(0);"/>整单理赔&nbsp;&nbsp;&nbsp;
							<input type="radio" name="cvType" id="cvType1" value="1" <%= cvBean.getType() == 1 ? "checked" : ""%> onclick="changePrice(1);"/>按sku理赔&nbsp;&nbsp;&nbsp;
							<input type="radio" name="cvType" id="cvType2" value="2" <%= cvBean.getType() == 2 ? "checked" : ""%> onclick="changePrice(2);"/>运费3倍理赔&nbsp;&nbsp;&nbsp;
							<input type="radio" name="cvType" id="cvType3" value="3" <%= cvBean.getType() == 3 ? "checked" : ""%> onclick="changePrice(3);"/>包装理赔
						<br/>
				 -->
				理赔金额：<input type="text" value="<%= cvBean.getPrice()%>" name="cvPrice" id="cvPrice" onfocus="checkRight();" onchange="checkFloat(this);remindSave();"/>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					 是否需要开具发票: <input type="radio" name="cvIsTicket" id="cvIsTicket1" value="1" <%= cvBean.getIsTicket() == 1 ? "checked" : ""%> onclick="changeIsTicket(1);"/>是&nbsp;&nbsp;&nbsp;
									<input type="radio" name="cvIsTicket" id="cvIsTicket0" value="0" <%= cvBean.getIsTicket() == 0 ? "checked" : ""%> onclick="changeIsTicket(0);"/>否&nbsp;&nbsp;&nbsp;
									<br/>
				<input type="submit" value=" 保存 "/>&nbsp;&nbsp;&nbsp;<span id="remindSave" ></span>
				</form>
				</fieldset>
				<%
					}else{ 
				%>
				<fieldset style="width:38%;">
				<legend>理赔信息</legend>
					理赔原因： <%= cvBean.getReasonTypeName() %> &nbsp;&nbsp;&nbsp;备注：<%= cvBean.getReasonRemark() %><br/>
					理赔金额：<%= cvBean.getPrice()%>
					&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
					是否需要开具发票：<%= cvBean.getIsTicket() == 0 ? "否" : cvBean.getIsTicket() == 1 ? "是" : ""%>
				</fieldset>
				<%
					}
				%>
				<br/>
				<br/>
	</div>		
			
	<div align="left" style="margin-left:9%;">
			<%
				List<BsbyOperationnoteBean> bsbyList = cvBean.getBsbyList();
				if( bsbyList.size() > 0 ) { 
				
			%>
				
				<br/><br/>
				对应的报损单号<br/>
				<table align='left' width='27%' border='0' cellspacing='1px' bgcolor='#D8D8D5' cellpadding='1px' id='orderInfoTable' >
				<tr bgcolor='#0080FF' >
				<td align='center'>
				<font color="#FFFFFF">报损单号</font>
				</td>
				<td align='center'>
				<font color="#FFFFFF">状态</font>
				</td>
				</tr>
			<%
				for ( int j =0; j < bsbyList.size(); j ++ ) { 
					BsbyOperationnoteBean boBean = bsbyList.get(j);
			%>
			
			
				<tr bgcolor='#FFFFFF' >
				<td align="center">
				<%
					if( boBean.getIf_del() == 1 ) {
				%>
					<%= boBean.getReceipts_number()%>
				<%
					} else  {
				%>
				<%int type = boBean.getCurrent_type();
						if((type==0||type==1||type==2||type==5)&&(userid==boBean.getOperator_id()||group.isFlag(413))){%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%= boBean.getId()%>" target="_blank"><%= boBean.getReceipts_number()%></a><br/>
						<%}else if(type==6&&(userid==boBean.getOperator_id()||group.isFlag(229))){%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%= boBean.getId()%>" target="_blank"><%= boBean.getReceipts_number()%></a><br/>
						<%}else {%>
							<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&lookup=1&opid=<%= boBean.getId()%>" target="_blank"><%= boBean.getReceipts_number()%></a><br/>
						<%}%>
				<%
					}
				%>
				</td>
				<td align="center">
				<%
					if( boBean.getIf_del() == 1 ) {
				%>
					已删除 
				<%
					} else {
				%>
					<%=boBean.current_typeMap.get(Integer.valueOf(boBean.getCurrent_type()))%>
				<%
					}
				%>
				</td>
				</tr>
			<%
				}
				}
			%>
		
	</div>
</body>
</html>
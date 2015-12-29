<%@ page language="java" import="java.util.*,adultadmin.bean.stock.*,adultadmin.util.*,adultadmin.action.vo.*" pageEncoding="UTF-8"%>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*,mmb.stock.stat.*" %>
<% 
	voUser user = (voUser)request.getSession().getAttribute("userView");
	CargoStaffBean csBean = user.getCargoStaffBean();

	CookieUtil cu = new CookieUtil(request, response);
	String appraisalMark = null;
	String areaMark = null;
	if( user != null ){
		appraisalMark = cu.getCookieValue("Appraisal_result_mark_" + user.getId());
		areaMark = cu.getCookieValue("Appraisal_area_mark_" + user.getId());
	}
	int appMark = 0;
	if( appraisalMark != null) {
		appMark = Integer.parseInt(appraisalMark);
	}
	int wareArea = -1;
	if( areaMark != null) {
		wareArea = Integer.parseInt(areaMark);
	}
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request,wareArea);
	//物流员工考核
	String ranking =(String) request.getAttribute("ranking");
	String firstCount =(String) request.getAttribute("firstCount");
	String oneselfCount =(String) request.getAttribute("oneselfCount");
	String productCount =(String) request.getAttribute("productCount");
	String firstProductCount =(String) request.getAttribute("firstProductCount");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    
    <title>质检</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script type="text/javascript">
		function focusProductCode(){
			var productCode=trim(document.getElementById("returnedProductCode").value);
			if(productCode=="商品条码扫描"){
				document.getElementById("returnedProductCode").value="";
				document.getElementById("returnedProductCode").style.color="#000000";
			}
		}
		function blurProductCode(){
			var productCode=trim(document.getElementById("returnedProductCode").value);
			if(productCode==""){
				document.getElementById("returnedProductCode").value="商品条码扫描";
				document.getElementById("returnedProductCode").style.color="#cccccc";
			} else {
				document.getElementById("returnedProductCode").style.color="#000000";
			}
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
		
		function check(){
			var returnedProductCode = trim(document.getElementById("returnedProductCode").value);
			if(returnedProductCode==""||returnedProductCode=="商品条码扫描"){
				alert("必须输入商品条码！");
				return false;
			}
			var wareArea = document.getElementById("wareArea").value;
			if( wareArea == "-1" ) {
				alert("请先选择库地区！");
				return false;
			}
			var checkNum = trim(document.getElementById("appraisalNumber").value);
			if(checkNum==""){
				alert("请输入质检数量");
				return false;
			}
			if( checkNum == "0") {
				document.getElementById("appraisalNumber").value = 1;
			}
			//document.getElementById("appraisalNumber").value=1;
			//document.getElementById("appraisalNumber2").value=1;
			document.getElementById("generateConfirm").disabled="true";
			return true;
		}
		function setAppraisalResult() {
			document.getElementById("appraisalResult").value='<%= appMark%>';
		}
		function changeNumber() {
			var code = trim(document.getElementById("returnedProductCode").value);
			if(code == "") {
				document.getElementById("appraisalNumber2").value="0";
			}
		}

	</script>

  </head>
  
  <body onload="document.getElementById('returnedProductCode').focus();$('#generateConfirm').removeAttr('disabled');">
  	<div style="margin-left:15px;margin-top:15px;">
  	<h2>&nbsp;质检结果录入:</h2>
   	<fieldset style="width:500px;">
   		<div style="background-color:#FFFF93;width:500px;height:200px;border-style:solid;border-width:1px;border-color:#000000;">
   		<div style="margin-left:12px;">
   			<br>
   			
   			<form action="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=appraisalReturnedProduct" method="post" onsubmit="return check();" >
   			<div align="left">&nbsp;&nbsp;&nbsp;<font size="3" color="red">你目前已打单<%=oneselfCount %>张,商品<%=productCount %>件,<%=ranking %>,冠军<%=firstCount %>张,<%=firstProductCount %>件,继续加油哦~</font><br><br></div>
   			<table border="0" cellspacing="12">
   			<tr>
   				<td>产品编号：</td>
   				<td><input type="text" id="returnedProductCode" name="returnedProductCode" onblur="blurProductCode();" onfocus="focusProductCode();"  onchange="changeNumber();"/></td>
   				<td align="right" style="display:none">数&nbsp;&nbsp;&nbsp;&nbsp;量：</td>
   				<td align="left" style="display:none"><input type="text" size="3" id="appraisalNumber" name="appraisalNumber" value="0" onblur="checkNumber(this);" />
   					<!-- <input type="hidden" id="appraisalNumber" name="appraisalNumber" value="1" /> -->
   				</td>
   			</tr>
   			
   			<tr>
   				<td colspan="2" >
					质检结果：&nbsp;&nbsp;&nbsp;&nbsp;<select id="appraisalResult" name="appraisalResult">
						<option value="0" <%= appMark == 0 ? "selected" : ""%>>合格</option>
						<option value="1" <%= appMark == 1 ? "selected" : ""%>>不合格</option>
					</select>	   					
   				</td>
   				<td colspan="2" >
					库地区：&nbsp;&nbsp;&nbsp;&nbsp;
					<%= wareAreaSelectLable%>	   					
   				</td>
   			</tr>
   			<tr>
   				<td colspan="4"></td>
   			</tr>
   			<tr>
   				<td></td>
   				<td align="left" ><input type="submit" id="generateConfirm" value="   生成上架单    " /></td>
   				<td align="center" colspan="2"><input type="reset" value="    取消     " /></td>
   			</tr>
   			
   			</table>
   			</form>
   			</div>
   		</div>
   		<br>
   		<div style="margin-left:12px;">
   		操作说明：<br>
   		1.质检完毕,扫描商品条码,生成并打印上架单</br>
   		<font color="red">2.每个退货上架单只能加一件商品 </font>
   		</div>
   	</fieldset>
   	</div>
  </body>
</html>

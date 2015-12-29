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
    
    <title>生成退货上架单</title>
    
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
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript">
	jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
		function focusProductCode(){
			var productCode=document.getElementById("returnedProductCode").value;
			productCode = productCode.trim();
			if(productCode=="商品条码扫描"){
				document.getElementById("returnedProductCode").value="";
				document.getElementById("returnedProductCode").style.color="#000000";
			}
		}
		function blurProductCode(){
			var productCode=document.getElementById("returnedProductCode").value;
			productCode = productCode.trim();
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
		   		 	$.messager.alert("提示","请不要输入大于9位的数字!");
		   		} else {
		   			obj.value="";
		   			obj.focus();
		   			$.messager.alert("提示","请填入整数！！");
		   		}
  		 	}
  
  		}
		
		function check(){
			var returnedProductCode = document.getElementById("returnedProductCode").value;
			returnedProductCode = returnedProductCode.trim();
			if(returnedProductCode==""||returnedProductCode=="商品条码扫描"){
				$.messager.alert("提示","必须输入商品条码！");
				return false;
			}
			var wareArea = document.getElementById("wareArea").value;
			if( wareArea == "-1" ) {
				$.messager.alert("提示","请先选择库地区！");
				return false;
			}
			var checkNum = document.getElementById("appraisalNumber").value;
			checkNum = checkNum.trim();
			if(checkNum==""){
				$.messager.alert("提示","请输入质检数量");
				return false;
			}
			if( checkNum == "0") {
				document.getElementById("appraisalNumber").value = 1;
			}
			//document.getElementById("appraisalNumber").value=1;
			//document.getElementById("appraisalNumber2").value=1;
			$("#wareArea2").val($("#wareArea").val());
			$("#appraisalNumber2").val($("#appraisalNumber").val());
			$("#appraisalResult2").val($("#appraisalResult").val());
			$("#returnedProductCode2").val($("#returnedProductCode").val());
			$("#returnedProductCode").val("");
			document.getElementById("generateConfirm").disabled="true";
			document.form1.submit();
			return false;
		}
		function setAppraisalResult() {
			document.getElementById("appraisalResult").value='<%= appMark%>';
		}
		function changeNumber() {
			var code =document.getElementById("returnedProductCode").value;
			code = code.trim();
			if(code == "") {
				document.getElementById("appraisalNumber2").value="0";
			}
		}
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
	</script>

  </head>
  
  <body onload="document.getElementById('returnedProductCode').focus();$('#generateConfirm').removeAttr('disabled');">
<center>
<h5 style="margin-left: 10px;">&nbsp;生成退货上架单</h5>
<div style="width:450px;height:300px;padding:10px;" class="easyui-panel" title="生成退货上架单">
	<center>
	<div style="margin-left:12px;">
   			<br>
   			
   			<div align="left">&nbsp;&nbsp;&nbsp;<font size="3" color="red">你目前已打单<%=oneselfCount %>张,商品<%=productCount %>件,<%=ranking %>,冠军<%=firstCount %>张,<%=firstProductCount %>件,继续加油哦~</font><br><br></div>
   			<table border="0" cellspacing="12">
   			<tr>
   				<td>产品编号：</td>
   				<form name="form1" action="<%=request.getContextPath()%>/admin/appraisalReturnedProduct.mmx" method="post" onsubmit="return check();" >
   				<td>
   				<input type="text" id="returnedProductCode" name="returnedProductCode" onblur="blurProductCode();" onfocus="focusProductCode();"  onchange="changeNumber();"/>
   					<input type="hidden" id="returnedProductCode2" name="returnedProductCode2"/>
   					<input type="hidden" id="appraisalNumber2" name="appraisalNumber2" />
   					<input type="hidden" id="appraisalResult2" name="appraisalResult2" />
   					<input type="hidden" id="wareArea2" name="wareArea2" />
    				</td>
   				</form>
   				<td align="right">数&nbsp;&nbsp;&nbsp;&nbsp;量：</td>
   				<td align="left"><input type="text" size="3" id="appraisalNumber" name="appraisalNumber" value="0" onblur="checkNumber(this);" />
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
   				<td align="left" ><a class="easyui-linkbutton" data-options="iconCls:'icon-ok'" id="generateConfirm" href="javascript:check();" >生成上架单</a></td>
   				<td align="center" colspan="2"><a class="easyui-linkbutton" data-options="iconCls:'icon-reload'" href="javascript:document.form1.reset();"  >取消</a></td>
   			</tr>
   			</table>
   			</div>
    </center>
</div>
	<div style="margin-left:31%;" align="left">
   		操作说明：<br>
   		1.质检完毕,扫描商品条码,生成并打印上架单</br>
   		2.多件商品时,请先输入数量,后扫描条码 
   	</div>
</center>
   	
  </body>
</html>

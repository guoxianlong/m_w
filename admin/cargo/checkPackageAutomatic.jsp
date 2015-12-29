<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@ taglib uri="/tags/struts-bean" prefix="bean"%>
<%@ taglib uri="/tags/struts-html" prefix="html"%>
<%@ taglib uri="/tags/struts-logic" prefix="logic"%>
<%@ page import="adultadmin.bean.stock.*,adultadmin.bean.order.*"%>
<%@ page import="adultadmin.util.*,adultadmin.util.StringUtil"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="java.util.*,mmb.stock.stat.*"%>
<%@ page import="mmb.stock.stat.SortingBatchBean"%>
<%@ page import="adultadmin.bean.cargo.CargoStaffBean"%>
<%@ page import="mmb.stock.stat.SortingBatchGroupBean"%>
<%
	String successTip = request.getParameter("successTip");
	String soundSrcSJYC = Constants.WARE_SOUND_SJYC;
	String soundSrcKXP = Constants.WARE_SOUND_KXP;
%>
<html>
  <head>
    
    <title>检查包裹</title>
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
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script type="text/javascript">
		function focusCode(){
			var productCode=trim(document.getElementById("productCode").value);
			var condition = document.getElementById("current_condition").value;
			if( condition == "0" ) {
				if(productCode=="请扫描包裹单号"){
					document.getElementById("productCode").value="";
					document.getElementById("productCode").style.color="#000000";
				}
			} else if( condition == "1" ) {
				if(productCode=="请扫描商品编号或订单号或出库单号"){
					document.getElementById("productCode").value="";
					document.getElementById("productCode").style.color="#000000";
				}
			} 
			
		}
		function blurCode(){
			var productCode=trim(document.getElementById("productCode").value);
			var condition = document.getElementById("current_condition").value;
			if( condition == "0" ) {
				if(productCode==""){
					document.getElementById("productCode").value="请扫描包裹单号";
					document.getElementById("productCode").style.color="#cccccc";
				} else {
					document.getElementById("productCode").style.color="#000000";
				}
			} else if( condition == "1" ) {
				if(productCode==""){
					document.getElementById("productCode").value="请扫描商品编号或订单号或出库单号";
					document.getElementById("productCode").style.color="#cccccc";
				} else {
					document.getElementById("productCode").style.color="#000000";
				}
			}
		}
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		function check(){
			
		}
		
		String.prototype.replaceAll = function (s1, s2) { 
    		return this.replace(new RegExp(s1,"gm"),s2);
		}
		
		function checkCode() {
			var code = document.getElementById("productCode").value;
   			if( code == null || code == "" ) {
   				alert("不能提交空值！");
   				return false;
   			}
   			
   				$.ajax({
                        type: "GET", //调用方式  post 还是 get
                        url: "<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=checkPackageAutomatic&code="+code+"&changeMark="+Math.random(), //访问的地址
                        dataType: "text", //返回的数据的形式
                        success: function(data, textStatus ) {  //访问成功后调用的方法 其中 data就是返回的数据 就是response写回的数据， textStatus是本次访问的状态  有 success 最常见的 
                        	var json = eval('(' + data + ')');
                        	var x = json['tip'].indexOf("。");
                        	if( x != -1 ) {
                        		document.getElementById("current_condition").value="0";
                        	} else {
                        		document.getElementById("current_condition").value="1";
                        	}
                        	//var tip = document.getElementById("infoArea").value;
                        	var tip = $("#infoArea").html();
                        	if( tip == null ) {
                        		if( json['tip'] == "包裹单对应订单状态不是已退回。" ) {
                        			alert(json['tip']);
                        		}
                        		if( json['status'] == "shujuyichang" ) {
                        			tip = "<font color='red'>" + json['tip'].replaceAll("r-n", "<br/>") + "</font>";
                        			//tip = json['tip'].replaceAll("r-n", "\r\n");
                        		} else {
                        			//tip = json['tip'].replaceAll("r-n", "\r\n");
                        			tip = json['tip'].replaceAll("r-n", "<br/>");
                        		}
                        	} else {
                        		if( json['tip'] == "包裹单对应订单状态不是已退回。" ) {
                        			alert(json['tip']);
                        		}
                        		if( json['status'] == "shujuyichang" ) {
                        			//tip += '\r'+'\n'+ "<font color='red'>" + json['tip'].replaceAll("r-n", "\r\n") + "</font>";
                        			tip += "<br/><font color='red'>"+ json['tip'].replaceAll("r-n", "<br/>") + "</font>"; 
                        		} else {
                        			tip += "<br/>"+ json['tip'].replaceAll("r-n", "<br/>");
                        		}
                        	}
                        	//document.getElementById("infoArea").value=tip;
                        	$("#infoArea").html(tip);
                        	document.getElementById("productCode").value="";
                        	document.getElementById('infoArea').scrollTop = document.getElementById('infoArea').scrollHeight
                        	if( json['OBIB'] == "normal" ) {
                        	} else if( json['OBIB'] == "clear" ) {
                        		$("#OBIB").html("");
                        	} else {
                        		$("#OBIB").html(json['OBIB']);
                        	}
                        	if( json['status'] == "shujuyichang" ) {
                        		Play('<%= soundSrcSJYC %>');
                        	}
                        	if( json['consignment'] == "1") {
                        		var tips = $("#infoArea").html();
                        		tips += "<br/><font color='red'>这个商品是快销商品</font>"; 
                        		if( json['status'] == "shujuyichang" ) {
                        		//若已经是数据异常了就不报快消品了
                        		} else {
                        			Play('<%= soundSrcKXP %>');
                        		}
                        		$("#infoArea").html(tips);
                        	}
                        },
                        error: function() {          //如果过程中出错了调用的方法
                             alert("验证出错");
                        }
                  });
		
			return false;
		}
		function clearPackage() {
			$.ajax({
                        type: "GET", //调用方式  post 还是 get
                        url: "<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=clearPackageCache"+"&changeMark="+Math.random(), //访问的地址
                        dataType: "text", //返回的数据的形式
                        success: function(data, textStatus ) {  //访问成功后调用的方法 其中 data就是返回的数据 就是response写回的数据， textStatus是本次访问的状态  有 success 最常见的 
                        	document.getElementById("current_condition").value="0";
                        	//document.getElementById("infoArea").value="";
                        	$("#infoArea").html("");
                        	document.getElementById("productCode").value="";
                        	alert(data);
                        	document.getElementById("OBIB").innerHTML="";
                        },
                        error: function() {          //如果过程中出错了调用的方法
                             alert("验证出错");
                        }
                  });
		}
		function clearPackageNoAlert() {
				$.ajax({
                        type: "GET", //调用方式  post 还是 get
                        url: "<%=request.getContextPath()%>/admin/claimsVerificationAction.do?method=clearPackageCache"+"&changeMark="+Math.random(), //访问的地址
                        dataType: "text", //返回的数据的形式
                        success: function(data, textStatus ) {  //访问成功后调用的方法 其中 data就是返回的数据 就是response写回的数据， textStatus是本次访问的状态  有 success 最常见的 
                        	document.getElementById("OBIB").innerHTML="";
                        },
                        error: function() {          //如果过程中出错了调用的方法
                        }
                  });
		}
		
		function ableButton() {
			document.getElementById("generateConfirm").enabled="true";
		}
		
		function resetPage() {
		}
		
		function jumpToProductCode() {
			if(document.getElementById("buyStockinCode").value != null && document.getElementById("buyStockinCode").value != "") {
				document.getElementById("productCode").focus();
			}
			return false;
		}
		function clear() {
			document.getElementById("codes").value = "";
		}
		
		function  Play(sound) {
     		 if(navigator.appName == "Microsoft Internet Explorer")
	      	{
	       		 var snd = document.createElement("bgsound");
		         document.getElementsByTagName("body")[0].appendChild(snd);
		         snd.src = sound;
	     	}
	     	else
	     	{
	            var obj = document.createElement("object");
	            obj.width="0px";
	            obj.height="0px";
	            obj.type = "audio/x-wav";
	            obj.data = sound;            
	            var body = document.getElementsByTagName("body")[0];
	            body.appendChild(obj);
     		}
		}
	</script>
  </head>
<body onload="javascript:document.getElementById('productCode').focus();clearPackageNoAlert();">
<!--  
 <button onclick="Play('<%= request.getContextPath()%>/admin/js/Error.wav');">click</button>
 -->
<input type="hidden" id="current_condition" value="0" />
<div style="margin-left:15px;margin-top:15px;">
  	<table width="400px"><tr align="center"><td><h2>&nbsp;核查包裹:</h2></td></tr></table>
   	<fieldset style="width:400px;">
   		<div style="background-color:#CCFF80;width:400px;height:300px;border-style:solid;border-width:1px;border-color:#000000;">
   		<div style="margin-left:0px;">
   			<br>
   			<table border="0" cellspacing="12" width="400px">
   			<tr>
   				<td colspan="3" align="center">
   				<!-- 
   				<textarea rows="8" cols="50" id="infoArea"></textarea>
   				 -->
   				 <div id="infoArea" style="width:300xp;height:130px;background-color:white;z-index:2;overflow:scroll;align:left;">&nbsp;</div>
   				</td>
   			</tr>
   			<tr>
   			<form action="" method="post" onsubmit="return checkCode();" >
   				<td colspan="3" align="center" >
   					<input type="text" size="25" name="productCode" id="productCode" onblur="blurCode();" onfocus="focusCode();"/>
   				</td>
   			</form>
   			</tr>
   			<tr>
   				<td align="center" colspan="3">
   				<input type="button" value="取消当前包裹检查" onclick="clearPackage();" />
   				<input type="button" value="  确定  " onclick="checkCode();"/>
   				</td>
   			</tr>
   			</div>
   		</div>
   		<form id="temp" action="<%=request.getContextPath()%>/admin/returnStorageAction.do?method=generateWholeAreaCargoOperation" method="post" >
   			<input type="hidden" value="" name="codes" id="codes" />
   		</form>
   		<br>
   			</table>
   		<div style="margin-left:12px;">
   		说明：<br>
   		1.依次扫描包裹单号，商品条码，订单编号（或出库单号）;</br>
   		2.输入模式下请点击“确认”，以核对包裹内商品信息。<br/>
   		</div>
   	</fieldset>
   	</div>

<div id="OBIB"></div>
</body>
</html>

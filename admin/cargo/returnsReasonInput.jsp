<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.util.StringUtil" %>
<%@ page import="adultadmin.util.Encoder" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>
<%@ page import="adultadmin.action.stock.*" %>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*" %>
<%@ page import="mmb.stock.stat.*"%>
<%
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request);
%>
<html>
<head>
<title>录入退货原因</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function returnsReasonOnFocus(obj) {
	var reasonCode = obj.value;
	if( reasonCode == "请扫描退货原因条码" ) {
		obj.value = "";
		obj.style.color="#000000";
		return;
	}
}
function returnsReasonOnBlur(obj) {
	var reasonCode = obj.value;
	if( reasonCode == null || reasonCode == "" ) {
		obj.value = "请扫描退货原因条码";
		obj.style.color="#cccccc";
		return;
	} else {
		obj.style.color="#000000";
	}
}
function checkSubmit(){
	var code = document.getElementById("code2").value;
	if( code != null || code != "" ) {
		document.getElementById("code").value= code;
	}
	var wareArea = document.getElementById("wareArea").value;
	if( wareArea == null || wareArea == "" || wareArea == "-1" ) {
		alert("请选择操作的库地区！");
		return false;
	} else {
		document.getElementById('wareArea1').value = wareArea;
	}
	if($("#returnsReasonCode").val()==""){
			alert("请扫描退货原因条码！");
			return false;
	}
	var orderCode = $("#code").val();
	if( orderCode == "" ) {
		alert("请扫描订单编号或包裹单号！");
		return false;
	}
	var x = "0";
	$.ajax({
        type: "POST",
        url: "<%= request.getContextPath() %>/admin/returnStorageAction.do?method=getDoReturnedPackageHasReason&code="+ orderCode + "&wareArea=" + wareArea,
        dataType: "text",
        success: function(data, textStatus) {
             var json = eval('(' + data + ')');
             if( json['status'] == "failure" ) {
             	alert(json['tip']);
             } else if ( json['status'] == "success" ) {
             	if( json['hasReason'] == "1" ) {
             		if(window.confirm("该订单已录入过原因，确认要修改？")) {
             			window.form1.submit();
             		} else {
             			$("#code").attr("value", "");
             			$("#code2").attr("value", "");
             			$("#wareArea1").attr("value", "");
             			$("#returnsReasonCode").attr("value", "");
             			$("#code2").focus();
             		}
             	} else {
             		window.form1.submit();
             	}
             }
             
        },
        error: function ( ) {
        	alert("通讯错误！");
        }
    });
    return false;
}
	function addCode() {
		var code = document.getElementById("code2").value;
		document.getElementById("code").value=code;
		document.getElementById("returnsReasonCode").focus();
		return false;
	}
</script>
</head>
<body onload="document.getElementById('code2').focus();returnsReasonOnBlur(document.getElementById('returnsReasonCode'));">
<b>&nbsp;&nbsp;录入退货原因</b>
<div style="margin-top:5px">
			  &nbsp;&nbsp;库地区:
					<%= wareAreaSelectLable%>
</div>
<%if(request.getAttribute("reasonAdded")!=null&&request.getAttribute("reasonAdded").toString().equals("success")){ %>
<script type="text/javascript" > 
	alert('操作成功！');
</script>
<%
	}
%>
<form action="" onsubmit="return addCode();">
<div id="scanType1" style="display:block">
	订单编号(包裹单号)：<input type="text" id="code2" name="code2" size="20"/>&nbsp;&nbsp;
</div>
</form>
<div id="">
<form name="form1" action="<%= request.getContextPath()%>/admin/returnStorageAction.do?method=returnsReasonInput" method="post" onsubmit="return checkSubmit();">
<input type="hidden" id="wareArea1" name="wareArea1" size="20"/>&nbsp;&nbsp;
<input type="hidden" id="code" name="code" size="20"/>&nbsp;&nbsp;
&nbsp;&nbsp;退货原因条码：<input type="text" id="returnsReasonCode" name="returnsReasonCode" size="20" onfocus="returnsReasonOnFocus(this);" onblur="returnsReasonOnBlur(this);"/>&nbsp;&nbsp;
</div>
<input type="submit" value="确定" />
</form>
</body>
</html>
<%@page import="mmb.stock.stat.WarehousingAbnormalService"%>
<%@page import="adultadmin.bean.stat.WarehousingAbnormalBean"%>
<%@ page import="mmb.stock.stat.ProductWarePropertyService"%>
<%@ page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.order.AuditPackageBean,adultadmin.action.vo.voProduct,adultadmin.action.vo.voOrder"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>添加异常入库单</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="javascript" type="text/javascript"src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<%
List<voProduct> vpList = (ArrayList<voProduct>)request.getSession().getAttribute("vpList");
AuditPackageBean apBean = (AuditPackageBean)request.getSession().getAttribute("apBean");
String wareArea = (String)request.getSession().getAttribute("wareArea");
%>
<script type="text/javascript">
$(document).ready(function(){
	<%if(vpList!=null){for(int i=1;i<=vpList.size();i++){%>
		$('#tr_<%=i%>').hide();
	<%}}%>
	//提交，最终验证。
     $('#send').click(function(){
    	//验证订单号 
    	 var code = $("#code").val();
    	 var wareArea = $('#wareArea').val();
	     if(code != ""){ 
	     	window.location='<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=findProductInfoList&code='+code +'&wareArea=' + wareArea;
	     }else{ 
	        alert("订单号或包裹单号不能为空！");
	     } 
     });
     document.onkeydown = function(event_e){  
     if( window.event )  
         event_e = window.event;  
         var int_keycode = event_e.charCode||event_e.keyCode;  
         if(int_keycode ==13){ 
        	 var code = $("#code").val();
	    	 var wareArea = $('#wareArea').val();
	    	 if(code != ""){ 
 		     	window.location='<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=findProductInfoList&code='+code +'&wareArea=' + wareArea;
 		     }else{ 
 		        alert("订单号或包裹单号不能为空！");
 		     } 
    	}
     } 
	//保存
     $('#save').click(function(){
    	 window.location='<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=addWarehousingAbnormal';
      });
	//添加确认
	$('#add').click(function(){
		var chk_value =[];    
		  $('input[id="box"]:checked').each(function(){    
		   chk_value.push($(this).val()); 
		  $('#'+ $(this).val()).show();
		  });  
		  if(chk_value.length==0){
			  alert('你还没有选择任何内容！'); 
		  }
	});
	//实际退回商品按钮
	$('#ajaxSend').click(function(){
		var realCode = $('#realCode').val();
		var realCount =$('#realCount').val();
		if(realCode != "" && realCount != ""){
			$.ajax({
				type: "GET",
				url: "<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=addRealProduct&selectIndex=14&realCode=" + realCode + "&realCount=" + realCount,
				cache: false,
				data: {type: "1"},
				success: function(msg, reqStatus){
					$("#real_td").empty();
					$("#real_td").html(msg);
					$('#realCode').val("");
					$('#realCount').val("");
					$('#realCode').focus();
				}
			});
		}else{
			alert('商品编号或商品数量不能为空！');
		}
	});
});
</script>
</head>
<body >
<div style="width: 95%"><h1 align="center">添加异常入库单</h1></div>
<%if(wareArea!=null){
	String whAreaSelection = WarehousingAbnormalService.getWeraAreaOptions(request,Integer.parseInt(wareArea),true);%>
	<div align="right" style="width: 95%;color: red;font-size:14px"> 库地区:<%=whAreaSelection %></div><hr width="95%" align="left">
<%}else{
	String whAreaSelection = ProductWarePropertyService.getWeraAreaOptions(request); %>
	<div align="right" style="width: 95%;color: red;font-size:14px"> 库地区:<%=whAreaSelection %></div><hr width="95%" align="left">
<% }%>	
<font size="4" style=""><strong>添加商品：请填入包裹内实际退回商品，确认提交后，将通过报损报益单对库存数据进行修正</strong></font>
<DIV style=" font-size:16px;font-weight:bold;left:200px; BORDER-RIGHT: #787878 2px dashed ; BORDER-TOP: #787878 2px dashed; BORDER-LEFT: #787878 2px dashed; BORDER-BOTTOM: #787878 2px dashed;height: auto ;width: 95%;">
	<br>&nbsp;&nbsp;&nbsp;&nbsp;
	<font size="2" style=""><strong>订单号/包裹单号:</strong></font>
	<%if(wareArea!=null){%>
		<input type="text" id="code" value="" name="code"  disabled="disabled" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" id="send" value="查看订单" disabled="disabled" style="height: 20px;width: 60px">
	<%}else{%>
		<input type="text" id="code" name="code"  value="" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="button" id="send" value="查看订单" style="height: 20px;width: 60px">
	<% }%>	
	
	<br>
	<br>
	<% if(vpList!=null &&vpList.size()>0){%>
	<table border="1"   width="95%" align="center"  cellspacing="0" bgcolor="FFFFE0">
		<tr bgcolor="#00ccff" align="center">
			<td>序号</td><td>产品编号</td><td>产品原名称</td><td>小店名称</td><td>数量</td>
		</tr>
		<% for(int i=0;i<vpList.size();i++){ 
			voProduct vpBean = vpList.get(i);
		%>
		<tr align="center">
			<td><%=i+1 %><input type="checkbox" id="box"  value="tr_<%=i+1 %>" checked="checked" disabled="disabled"></td><td><%=vpBean.getCode() %></td><td><%=vpBean.getOriname() %></td><td><%=vpBean.getName() %></td><td><%=vpBean.getCount() %></td>
		</tr>
		<%} %>
	</table>
	<div align="right"><input type="button" id="add"value="确认添加">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
	&nbsp;
	<div align="center">
	<div style="line-height:60px; border-style:solid;border-color:#000000;border-width:1px;height:60px;width:95%;font-size:16px;font-weight:bold; " align="left">
	<table border="0"   width="100%" align="center"  cellspacing="0" >
		<tr><td>&nbsp;</td></tr>
		<tr align="center" style="height: 30px;padding-top: 20px;">
			<td align="right" width="20%">实际退回商品:</td>
			<td align="left" width="15%"><input type="text" id="realCode"  value=""></td>
			<td align="right" width="10%">商品数量:</td>
			<td align="left" width="10%"><input type="text" size="6" id="realCount" ></td>
			<td></td><td align="left"><input type="button" id="ajaxSend" value="确认添加"></td>
		</tr>
	</table></div></div>
	<br>
	<%} %>
</div>
<br>
<% if(vpList!=null &&vpList.size()>0 && apBean != null){%>
<DIV style="font-size:16px;font-weight:bold;left:200px; BORDER-RIGHT: #787878 2px double ; BORDER-TOP: #787878 2px double; BORDER-LEFT: #787878 2px double; BORDER-BOTTOM: #787878 2px double;height: auto ;width: 95%">
<br>
<table width="100%">
	<tr>
		<td colspan="2" width="80%" align="center">
			<table  border="1"  width="97%"  cellspacing="0" >
				<tr style="height: 40px ;font-size:16px;font-weight:bold;" align="center">
					<td width="10%">订单号</td><td width="25%"><%=apBean.getOrderCode() %></td><td width="10%">包裹单号</td><td width="25%"><%=apBean.getPackageCode() %></td><td width="10%">快递公司</td><td width="20%"><%=voOrder.deliverMapAll.get(apBean.getDeliver()+"") %></td></tr>
			</table></td></tr>
	<tr><td>&nbsp;&nbsp;</td><td>&nbsp;&nbsp;</td></tr>
	<tr>
		<td width="50%">
			<table border="1" id="table_hide"  width="92%" align="center"  cellspacing="0" bgcolor="FFFFE0">
				<tr bgcolor="#00ccff" align="center">
					<td>订单中商品</td><td>原名称</td><td>商品名称</td><td>数量</td></tr>
				<% for(int i=0;i<vpList.size();i++){ 
						voProduct vpBean = vpList.get(i);
				%>
				<tr id="tr_<%=i+1%>" align="center">
					<td><%=vpBean.getCode() %></td><td><%=vpBean.getOriname() %></td><td><%=vpBean.getName() %></td><td><%=vpBean.getCount() %></td>
				</tr>
				<%} %>
			</table></td>
		<td  width="50%"><div id="real_td">
			<table border="1"   width="95%" align="center"  cellspacing="0" bgcolor="FFFFE0">
				<tr bgcolor="#00ccff" align="center">
					<td>实际退回商品</td><td>原名称</td><td>商品名称</td><td>数量</td><td>操作</td></tr>
				<tr align="center">
			</table>
			</div></td>
	</tr>
</table>
<br>
</DIV>
<br>
&nbsp;&nbsp;&nbsp;<input type="button" id="save" value="保 存" style="width: 70px;height: 25px ;font-size:14px;font-weight:bold;">
<%} %>
</body>
</html>

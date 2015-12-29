<%@ page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.order.AuditPackageBean,adultadmin.action.vo.voProduct,adultadmin.action.vo.voOrder,
				adultadmin.bean.stat.WarehousingAbnormalBean,mmb.stock.stat.WarehousingAbnormalService"%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>添加异常入库单</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="javascript" type="text/javascript"src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
List<voProduct> vpList = (ArrayList<voProduct>)request.getSession().getAttribute("vpList");
List<voProduct> rpList = (ArrayList<voProduct>)request.getSession().getAttribute("rpList");
WarehousingAbnormalBean anormalBean = (WarehousingAbnormalBean)request.getAttribute("anormalBean");
AuditPackageBean apBean = (AuditPackageBean)request.getSession().getAttribute("apBean");
String wareArea = (String)request.getSession().getAttribute("wareArea");
%>
<script type="text/javascript">
$(document).ready(function(){
	 $("#save").hide();
	//提交，最终验证。
     $('#send').click(function(){
    	//验证订单号 
    	 var code = $("#code").val();
	     if(code != ""){ 
	     	window.location='<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=findOrderByCode&code='+code;
	     }else{ 
	        alert("订单号或包裹单号不能为空！");
	     } 
    	 //$("form:first").submit();
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
			$("#save").show();
			$("#submit").hide();
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
	//删除实际退回商品
	$("input[name='del']").click(function(){
		$("#save").show();
		$("#submit").hide();
		var realCode = this.id;
		$.ajax({
			type: "GET",
			url: "<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=delRealProduct&selectIndex=14&realCode=" + realCode,
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
	});
	//保存
	$('#save').click(function(){
		window.location='<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=updateWarehousingAbnormal&abnormalId=<%=anormalBean.getId()%>';
      });
	//修改数量
	$("input[name='realName']").change( function() {
		$("#save").show();
		$("#submit").hide();
		var realCount = $(this).val();
		var realCode = this.id;
		$.ajax({
			type: "GET",
			url: "<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=upRealProduct&selectIndex=14&realCode=" + realCode +"&realCount=" + realCount,
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
	});
	//提交上审核
	$('#submit').click(function(){
		if(confirm('确认审核通过？')) {
			window.location='<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=submitWarehousingAbnormal&abnormalId=<%=anormalBean.getId()%>';
		}
	});
	//确认提交
	$('#reallySubmit').click(function(){
		if(confirm('确认提交？')) {
			window.location='<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=statusToSubmitted&abnormalId=<%=anormalBean.getId()%>';
		}
	});
	//审核不通过
	$('#auditNoPassed').click(function(){
		if(confirm('确认审核不通过？')) {
			window.location='<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=statusToUntreated&abnormalId=<%=anormalBean.getId()%>';
		}
	});
});
</script>
</head>
<body >
<div style="width:  95%"><h1 align="center">编辑异常入库单</h1></div>
<%if(wareArea!=null){
	String whAreaSelection = WarehousingAbnormalService.getWeraAreaOptions(request,Integer.parseInt(wareArea),true);%>
	<div align="right" style="width:95%;color: red;font-size:14px"> 库地区:<%=whAreaSelection %></div><hr width="95%" align="left">
<%}%>
<form action="<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=updateWarehousingAbnormal" method="post">
<font size="3" color="red">异常入库单号：<%if(anormalBean != null){%><%=anormalBean.getCode() %>&nbsp;&nbsp;
										<input type="hidden" name="abnormalId" value="<%=anormalBean.getId() %>">
	状态：<%=WarehousingAbnormalBean.statusMap.get(anormalBean.getStatus())%><%} %></font>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<%if(anormalBean.getStatus()==0){ 
		if(group.isFlag(755)){
	%>
		<input type="button" id="reallySubmit" value="确认提交" style="height: 22px;width: 60px">
	<%	}
	}else if(anormalBean.getStatus()==1){ 
		if(group.isFlag(858)){
	%>
		<input type="button" id="submit" value="审核通过" style="height: 22px;width: 70px">&nbsp;&nbsp;&nbsp;
		<input type="button" id="auditNoPassed" value="审核不通过" style="height: 22px;width: 70px"> 
	<%}
	} %>
<br>
<font size="4" style=""><strong>添加商品：请填入包裹内实际退回商品，确认提交后，将通过报损报益单对库存数据进行修正</strong></font>
<DIV style=" font-size:16px;font-weight:bold;left:200px; BORDER-RIGHT: #787878 2px dashed ; BORDER-TOP: #787878 2px dashed; BORDER-LEFT: #787878 2px dashed; BORDER-BOTTOM: #787878 2px dashed;height: auto ;width: 95%;">
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
	<%if(anormalBean.getStatus()==0 && group.isFlag(755)){ %>
	<div align="right"><input type="button" id="add"value="确认添加">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</div>
	
	&nbsp;
	<div align="center">
	<div style="line-height:60px; border-style:solid;border-color:#000000;border-width:1px;height:60px;width:95%;font-size:16px;font-weight:bold; " align="left">
	<table border="0"   width="95%" align="center"  cellspacing="0" >
		<tr><td>&nbsp;</td></tr>
		<tr align="center" style="height: 30px;padding-top: 20px;">
			<td align="right" width="20%">实际退回商品:</td>
			<td align="left" width="15%"><input type="text" id="realCode"  value=""></td>
			<td align="right" width="10%">商品数量:</td>
			<td align="left" width="10%"><input type="text" size="6" id="realCount" ></td>
			<td></td><td align="left"><input type="button" id="ajaxSend" value="确认添加"></td>
		</tr>
	</table></div></div>
	<%} %>
	<br>
	<%} %>
</div>
<br>
<% if(vpList!=null &&vpList.size()>0 && apBean != null){%>
<DIV style="font-size:16px;font-weight:bold;left:200px; BORDER-RIGHT: #787878 2px double ; BORDER-TOP: #787878 2px double; BORDER-LEFT: #787878 2px double; BORDER-BOTTOM: #787878 2px double;height: auto ;width: 95%">
<br>
<table>
	<tr>
		<td colspan="2" width="2000px" align="center">
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
			<% if(rpList!= null && rpList.size()>0){for (voProduct bean:rpList) { %>
			<tr align="center">
				<td><%=bean.getCode() %></td><td><%=bean.getOriname() %></td><td><%=bean.getName() %></td>
				<%if(anormalBean.getStatus()==0 && group.isFlag(755)){ %>
				<td><input type="text" id="<%=bean.getCode() %>"name="realName" size="2" value="<%=bean.getCount()%>"></td><td><input id="<%=bean.getCode() %>" type="button" value="删除" name="del"></td></tr>
				<%}else{ %>
				<td><input type="text" id="<%=bean.getCode() %>"name="realName" size="2" readonly="readonly" value="<%=bean.getCount()%>"></td><td></td></tr>
				<%} %>
			<tr align="center">
			<%} }%>
		</table>
			</div></td>
	</tr>
</table>
<br>
</DIV>
<br>
&nbsp;&nbsp;&nbsp;<input type="button" id="save" value="保 存" style="width: 70px;height: 25px ;font-size:14px;font-weight:bold;">
<%}%>
</form>
</body>
</html>

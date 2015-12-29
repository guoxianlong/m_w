<%@page pageEncoding="utf-8" %>
<html>
<head>
<%@include file="../rec/inc/easyui.jsp" %>
<link rel="stylesheet" type="text/css" href="${path}/admin/orderStock/searchPOPDeliverInfoList.css" /> 
<script>
//查询
function searchData(dom){
	if(!validDate()){
		return false;
	}
	
	$(dom).attr('disabled', true);
	var url = sysPath+'/OrderStockController/getPOPDeliverInfoList.mmx?'+$('#searchForm').serialize();
	$('#dataListDiv').html('数据加载中...').load(url, function(){
		$(dom).attr('disabled', false);
	});
}
function validDate(){
	var startDate = $('#startDate').val();
	var endDate = $('#endDate').val();
	var orderCodes = $('#orderCodes').val();
	var scanType = $('#scanType').val();
	if(startDate == "" && endDate == ""){
		alert("请选择发货时间！");
		return false;
	}
	if(startDate == ""||endDate == ""){
		alert("发货时间必须成对出现！");
		return false;
	}
	var begin = new Date(startDate);
	var end = new Date(endDate);
	var d1 = end.getTime() - begin.getTime();
	var d2 = 1000*3600*24*31;
	if(d1 - d2 > 0 ){
		alert("时间范围不能大于31天！");
		return false;
	}
	if((orderCodes==null||orderCodes=="") && startDate.length==0 && endDate.length==0){
		alert("d");
		$.messager.show({
			msg : "请填写出库日期或订单号或包裹单号！",
			title : '提示'
		});
		return false;
	}
	var str = orderCodes.split("\n");
	var numlen = str.length;
	var count=0;
	if(numlen>1000){
		for(var i=0;i<numlen;i+=1){
			if(str[i] && trim(str[i]).length>0)
				count++;
		}
	}
	if(count>1000){
		$.messager.show({
			msg : "一次最多可扫描1000个" + (scanType == 1 ? '包裹单号' : '订单号') +"！",
			title : '提示'
		});
		return false;
	}
	return true;
}

function exportData(dom) {
	if(!validDate()){
		return false;
	}
	var url = sysPath+'/OrderStockController/getPOPDeliverInfoList.mmx?'+$('#searchForm').serialize()+'&exportFlag=1';
	window.location.href = url;
}

</script>
</head>
<body>
<div class="mainBody">

<fieldset>
<legend>查询条件</legend>
<form id="searchForm">
<table>
	<tr>
		
	</tr>
	<tr>
		<td rowspan="2">
			<textarea style="width:200px; height:100px;" id="orderCodes" name="orderCodes" placeholder="多个编号请保持每行一个编号"></textarea>
		</td>
		<td class="tdLabel">POP商家:</td>
		<td>
			<select id="popId" name="popId" style="width: 100px">
				<option value="-1">全部</option>
				<option value="2">京东</option>
			</select>
		</td>
		<td class="tdLabel" style="width: 100px">出库时间<font color="red">*</font>:</td>
		<td colspan="4">
			<input type="text" name="startDate" id="startDate" size="12" class="WDate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',maxDate:'%y-%M-{%d-1}'});"/> -
			<input type="text" name="endDate" id="endDate" size="12" class="WDate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startDate\')}'});"/>
			<span style="font-size:11px; color:red;">请保持时间在31天以内</span>
	    </td>
	</tr>	
	<tr>
		<td class="tdLabel">发货仓:</td>
		<td>
		    <select id="storageId" name="storageId" style="width: 100px">
				<option value="-1"></option>
				<option value="1">京东</option>
			</select>
		</td>
		<td class="tdLabel">快递公司:</td>
		<td>
			<select name="deliveryId" style="width: 100px">
				<option value="-1"></option>
				<option value="1">京东快递</option>
			</select> 
		</td>
		<td class="tdLabel">状态:</td>
		<td>
			<select id="deliverState" name="deliverState" style="width: 100px">
				<option value="-1"></option>
				<option value="0">已出库</option>
				<option value="1">已揽收</option>
				<option value="2">在途</option>
				<option value="7">已签收</option>
				<option value="8">未妥投开始退回</option>
			</select>
		</td>
	    <td align="right" >
			<button type="button" class="searchBut" onclick="searchData(this);">查 询</button>
		</td>
		<td align="right" >
			<button type="button" class="searchBut" onclick="exportData(this);">导出</button>
		</td>
	</tr>
	<tr>
		<td>
			<input type="radio" name="scanType" value="1" checked="checked"/>包裹单号
			<input type="radio" name="scanType" value="2" />mmb订单号
			<input type="radio" name="scanType" value="3" />京东订单号
		</td>
	</tr>
</table>
</form>
</fieldset>
 
<div id="dataListDiv"></div>

 
</div>
</body>
</html>
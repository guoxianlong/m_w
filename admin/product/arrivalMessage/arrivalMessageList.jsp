<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<%@page isELIgnored="false" %>
<c:set var="path" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${path}/easyui/jquery-easyui-1.3.4/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="${path}/easyui/jquery-easyui-1.3.4/themes/icon.css">
<script type="text/javascript" src="${path}/easyui/jquery-easyui-1.3.4/jquery.min.js" charset="utf-8"></script>
<script type="text/javascript" src="${path}/easyui/jquery-easyui-1.3.4/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${path}/easyui/jquery-easyui-1.3.4/locale/easyui-lang-zh_CN.js"></script>
<link rel="stylesheet" type="text/css" href="${path}/admin/js/select2/select2.css" />
<script type="text/javascript" src="${path}/admin/js/select2/select2.js"></script>
<script type="text/javascript" src="${path}/admin/rec/js/My97DatePicker/WdatePicker.js" charset="utf-8"></script>
<style type="text/css">
.datagrid-header-row {
	border-style: solid;
	border-width: 0 0 1px;
	cursor: default;
	overflow: hidden;
	background-color: #9FB6CD;
}
.datagrid-header-row td {
	border-style: solid;
	border-color:#FFFFFF;
}
.datagrid-row td {
	border-style: solid;
	border-color:#8DB6CD;
}
.datagrid-header td.datagrid-header-over {
	background-color: #9FB6CD;
}
.datagrid-header td {
	background: none!important;
}
a.l-btn-plain:hover{
	border:solid 1px #228B22;
	border-radius:0px;
	background-color: #FFFFFF;
	cursor: pointer;
}
.ax_h2 {
    color: #333333;
    font-family: "Arial Negreta","Arial";
    font-size: 24px;
    font-style: normal;
    font-weight: 700;
    line-height: normal;
    text-align: left;
}
</style>
<script type="text/javascript">
var showTitle = function(value,row,index){
	if (value != null) {
		var rs = '<div style="text-overflow:ellipsis; white-space:nowrap;overflow:hidden; font-size: 12px" title="'+value+'">'+value+'</div>';
		return rs;
	} else {
		return "";
	}
}
var operate = function(value,row,index){
	if (value != null) {
		var rs = '<a href="javascript:;" onclick="delMessage(\''+value+'\');">删除</a>';
		return rs;
	} else {
		return "";
	}
}
var selfRowStyle = function(rowIndex, rowData){
	if (parseInt(rowIndex)%2 == 1) {
		return 'background-color:#E0EEEE;';
	} else {
		return 'background-color:#F7F7F7;';
	}
}
var onLoadSuccess=function(data) {
	if(data.total==0) {
		$("#tip").html("<font color='red'>提示：未找到符合条件的数据</font>");
	} else {
		$("#tip").html(data);
	}
}
function addArrivalMessage(){
	$('#button-add').show();
	$('#addArrivalMessage').html("");
	$('#addArrivalMessage').append("<iframe frameborder='0' style='width:770px;height:550px;' src='<%=request.getContextPath()%>/productArrivalController/toAddArrivalMessage.mmx'></iframe>");
	$('#addArrivalMessage').window('open');
}
function searchData(){
	$('#arrivalList').datagrid('load',{
		areaId : $("#areaId").val(),
		supplierId : $("#supplierId").val(),
		startTime : $("#startTime").val(),
		endTime : $("#endTime").val(),
		productLineId : $("#productLineId").val(),
		buyPlanCode : $("#buyPlanCode").val(),
		waybillCode : $("#waybillCode").val(),
		receiver : $("#receiver").val()
	});
}
function exportList(){
	window.location.href="<%=request.getContextPath()%>/productArrivalController/exportList.mmx?"+"areaId="+$("#areaId").val()
			+"&supplierId="+$("#supplierId").val()+"&startTime="+$("#startTime").val()
			+"&endTime="+$("#endTime").val()+"&productLineId="+$("#productLineId").val()
			+"&buyPlanCode="+$("#buyPlanCode").val()+"&waybillCode="+$("#waybillCode").val()
			+"&receiver="+$("#receiver").val();
}
function delMessage(id) {
	if (confirm("确定删除该行数据吗？")) {
		$.ajax({
			type: "POST",
			url : "<%=request.getContextPath()%>/productArrivalController/delArrivalMessage.mmx",
			cache : false,
			data : {
				id:id
			},
			success : function(rs) {
					alert("删除成功");
					$('#arrivalList').datagrid('reload');
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("系统错误，请刷新后重试或联系管理员");
			}
		});
	}
}
function endMax(){
	if ($("#startTime").val() != "") {
		var begin = new Date($("#startTime").val().replace(/-/g,"/"));
		var date=new Date();
		var beginlastDay = addDate(begin,31);
		var year = beginlastDay.getFullYear();
        var month =(beginlastDay.getMonth() + 1).toString();
        var day = (beginlastDay.getDate()).toString();
        var hours = (beginlastDay.getHours()).toString();
        var minutes = (beginlastDay.getMinutes()).toString();
        if (hours.length == 1) {
        	hours = "0" + hours;
        }
        if (minutes.length == 1) {
        	minutes = "0" + minutes;
        }
        if (month.length == 1) {
            month = "0" + month;
        }
        if (day.length == 1) {
            day = "0" + day;
        }
        return dateTime = year +'-'+ month +'-'+ day+' '+hours+":"+minutes;
	}
}
function addDate(dd,dadd){
	var a = new Date(dd)
	a = a.valueOf()
	a = a + dadd * 24 * 60 * 60 * 1000
	a = new Date(a)
	return a;
	}
function startMin(){
	if ($("#endTime").val() != "") {
		var begin = new Date($("#endTime").val().replace(/-/g,"/"));
		var date=new Date();
		var beginlastDay = addDate(begin,-31);
		var year = beginlastDay.getFullYear();
        var month =(beginlastDay.getMonth() + 1).toString();
        var day = (beginlastDay.getDate()).toString();
        var hours = (beginlastDay.getHours()).toString();
        var minutes = (beginlastDay.getMinutes()).toString();
        if (hours.length == 1) {
        	hours = "0" + hours;
        }
        if (minutes.length == 1) {
        	minutes = "0" + minutes;
        }
        if (month.length == 1) {
            month = "0" + month;
        }
        if (day.length == 1) {
            day = "0" + day;
        }
        return dateTime = year +'-'+ month +'-'+ day+' '+hours+":"+minutes;
	}
}
$(document).ready(function() {
	$("#supplierId").select2();
});
</script>
</head>
<body>
<div id="toolbar">
	<table style="width: 100%;">
		<tr>
			<td colspan="10" class="ax_h2">
				商品到货信息列表
			</td>
		</tr>
		<tr>
			<td style="width: 45px;text-align: right;">库区域:</td><td style="width: 110px;">
			<select id="areaId" name="areaId" style="width: 110px;">
				<option value="0">请选择</option>
				<option value="4">无锡</option>
				<option value="9">成都</option>
			</select>
			</td>
			<td style="width: 90px;text-align: right;">供应商名称:</td><td style="width: 110px;">
			<select name="supplierId" id="supplierId" style="width: 100%;">
				<option value="0">请选择</option>
				<c:forEach items="${supplierList}" var="supplier">
					<option value="${supplier.id}">${supplier.name_abbreviation}（${supplier.name}）</option>
				</c:forEach>
			</select>
			</td>
			<td style="width: 60px;text-align: right;">到货时间:</td><td style="width: 360px;">
				<input style="width: 100px;" type="text" id="startTime" size="10" class="Wdate" onclick="var min=startMin();WdatePicker({minDate:min,dateFmt:'yyyy-MM-dd',maxDate:'#F{$dp.$D(\'endTime\')}'});" readonly="readonly"/> -
				<input style="width: 100px;" type="text" id="endTime" size="10" class="Wdate" onclick="var max=endMax();WdatePicker({maxDate:max,dateFmt:'yyyy-MM-dd',minDate:'#F{$dp.$D(\'startTime\')}'});" readonly="readonly"/>
				（时间不超过31天）
			</td>
			<td rowspan="2"><a href="javascript:;" class="easyui-linkbutton" onclick="searchData()" iconCls="icon-search">查询</a></td>
		</tr>
		<tr>
			<td style="width: 45px;text-align: right;">产品线:</td><td style="width: 110px;">
			<select id="productLineId" style="width: 110px;">
				<option value="0">请选择</option>
				<c:forEach items="${productLineList}" var="productLine">
					<option value="${productLine.id}">${productLine.name }</option>
				</c:forEach>
			</select>
			</td>
			<td style="width: 90px;text-align: right;">预计到货单编号:</td><td style="width: 110px;"><input id="buyPlanCode"/></td>
			<td style="width: 60px;text-align: right;">运单号:</td>
			<td style="width: 350px;">
				<input style="width: 110px;" id="waybillCode"/>&nbsp;&nbsp;收货人:<input style="width: 110px;" id="receiver"/>
			</td>
		</tr>
		<tr>
			<td colspan="10">
				<a href="javascript:;" class="easyui-linkbutton" plain="true" iconCls="icon-exportArrival" onclick="exportList()">导出</a>
				<mmb:permit value="3665">
					<a href="javascript:;" class="easyui-linkbutton" plain="true" iconCls="icon-addArrival" onclick="addArrivalMessage()">添加</a>
				</mmb:permit>
				<span id="tip"></span>
			</td>
		</tr>
	</table>
</div>
	<table id="arrivalList" class="easyui-datagrid"
	url="${path}/productArrivalController/getArrivalPage.mmx"
	data-options="rownumbers:true,pagination:true,pageSize:30,pageList:[20,30,50,100],method:'post',fitColumns:true,rowStyler:selfRowStyle,singleSelect:true,toolbar:'#toolbar',onLoadSuccess:onLoadSuccess">
	<thead>
		<tr>
			<th data-options="field:'arrivalTime',width:0.15,formatter:showTitle,title:'到货时间',align:'center'"></th>
			<th data-options="field:'areaName',width : 0.08,formatter:showTitle,title : '库区域',align : 'center'"></th>
			<th data-options="field:'codeFlagName',width : 0.15,formatter:showTitle, title : '有无标示单号',align : 'center'"></th>
			<th data-options="field:'waybillCode',width : 0.08,formatter:showTitle, title : '运单号',align : 'center'"></th>
			<th data-options="field:'deliverCorpName',width : 0.08,formatter:showTitle,title : '物流公司',align : 'center'"></th>
			<th data-options="field:'buyPlanCode',width : 0.12,formatter:showTitle,title : '预计到货单号',align : 'center'"></th>
			<th data-options="field:'supplierName',width : 0.10,formatter:showTitle,title : '供应商名称',align : 'center'"></th>
			<th data-options="field:'arrivalCount',width : 0.12,formatter:showTitle,title : '到货箱/件数',align : 'center'"></th>
			<th data-options="field:'temporaryCargo',width : 0.15,formatter:showTitle,title : '暂存货位号',align : 'center'"></th>
			<th data-options="field:'productLineName',width : 0.08,formatter:showTitle,title : '产品线',align : 'center'"></th>
			<th data-options="field:'businessUnit',width : 0.10,formatter:showTitle,title : '事业部',align : 'center'"></th>
			<th data-options="field:'receiver',width : 0.08,formatter:showTitle,title : '收货人',align : 'center'"></th>
			<th data-options="field:'addUser',width : 0.08,formatter:showTitle,title : '添加人',align : 'center'"></th>
			<th data-options="field:'addTime',width : 0.12,formatter:showTitle,title : '添加时间',align : 'center'"></th>
			<th data-options="field:'isPrintBillName',width : 0.10,formatter:showTitle,title : '是否打单',align : 'center'"></th>
			<th data-options="field:'arrivalException',width : 0.12,formatter:showTitle,title : '到货异常描述',align : 'center'"></th>
			<mmb:permit value="3666">
				<th data-options="field:'id',width : 0.08,formatter:operate, title : '操作',align : 'center'"></th>
			</mmb:permit>
		</tr>
	</thead>
	</table>
	<div id="addArrivalMessage" title=" " class="easyui-window" closed="true" style="width:810px;height:610px;padding:5px;">
	</div>
</body>
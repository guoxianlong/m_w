<%@ page language="java" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<%@ taglib prefix="pg" uri="http://jsptags.com/tags/navigation/pager" %>
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
$(document).ready(function() {
	$("#supplierId").select2();
});
function cancel(){
	parent.$('#addArrivalMessage').window('close');
}
function add(){
	if (validate()) {
		$.ajax({
			type: "POST",
			url : "<%=request.getContextPath()%>/productArrivalController/addArrivalMessage.mmx",
			cache : false,
			data : {
				areaId : $("#areaId").val(),
				arrivalTime : $("#arrivalTime").val(),
				waybillCode : $("#waybillCode").val(),
				codeFlag : $("#codeFlag").val(),
				deliverCorpName : $("#deliverCorpName").val(),
				buyPlanCode : $("#buyPlanCode").val(),
				supplierId : $("#supplierId").val(),
				arrivalCount : $("#arrivalCount").val(),
				temporaryCargo : $("#temporaryCargo").val(),
				productLineId : $("#productLineId").val(),
				businessUnit : $("#businessUnit").val(),
				receiver : $("#receiver").val(),
				isPrintBill : $("#isPrintBill").val(),
				arrivalException : $("#arrivalException").val()
			},
			success : function(rs) {
				if (rs == "codeIsEx") {
					alert("运单号重复请核实后再试");
				} else if (rs == "success") {
					alert("添加成功");
					parent.$('#arrivalList').datagrid('reload');
					parent.$('#addArrivalMessage').window('close');
				} else {
					alert(rs);
				}
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert("系统错误，请刷新后重试或联系管理员");
			}
		});
	}
}
function validate(){
	if ($("#areaId").val() == 0) {
		alert("请选择库区域");
		return false;
	}
	if ($("#arrivalTime").val() == "") {
		alert("请填写到货时间");
		return false;
	}
	if ($("#codeFlag").val() == 0) {
		alert("请选择有无标示单号");
		return false;
	}
	if ($("#waybillCode").val() == "") {
		alert("请填写运单号");
		return false;
	}
	if ($("#deliverCorpName").val() == "") {
		alert("请填写物流公司");
		return false;
	}
	if ($("#codeFlag").val() == 1) {
		if ($("#buyPlanCode").val() == "") {
			alert("请填写预计到货单号");
			return false;
		}
	}
	if ($("#supplierId").val() == 0) {
		alert("请选择供应商");
		return false;
	}
	if ($("#arrivalCount").val() == "") {
		alert("请填写到货数量");
		return false;
	} else if (isNaN($("#arrivalCount").val()) || parseInt($("#arrivalCount").val()) != $("#arrivalCount").val()){
		alert("到货数量必须为整数");
		return false;
	}
	if ($("#temporaryCargo").val() == "") {
		alert("请填写暂存货位号");
		return false;
	}
	if ($("#productLineId").val() == 0) {
		alert("请选择产品线");
		return false;
	}
	if ($("#businessUnit").val() == "") {
		alert("请填写事业部");
		return false;
	}
	if ($("#receiver").val() == "") {
		alert("请填写收货人");
		return false;
	}
	if ($("#isPrintBill").val() == 0) {
		alert("请选择是否打单");
		return false;
	}
	return true;
}
</script>
</head>
<body>
<table id="toolbar" style="width: 100%;">
		<tr>
			<td colspan="10" class="ax_h2">
				商品到货信息录入
			</td>
		</tr>
		<tr>
			<td style="width: 90px;text-align: right;">库区域:</td><td style="width: 110px;">
			<select name="areaId" id="areaId" style="width: 110px;">
				<option value="0">请选择</option>
				<option value="4">无锡</option>
				<option value="9">成都</option>
			</select>
			</td>
		</tr>
		<tr>
			<td style="width: 90px;text-align: right;">到货时间:</td><td style="width: 150px;">
				<input name="arrivalTime" id="arrivalTime" style="width: 140px;" type="text" size="12" class="Wdate" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm'});" readonly="readonly"/>
				（备注：精确到分钟）
			</td>
		</tr>
		<tr>
			<td style="width: 90px;text-align: right;">有无标示单号:</td><td style="width: 110px;">
			<select name="codeFlag" id="codeFlag" style="width: 110px;">
				<option value="0">请选择</option>
				<option value="1">有</option>
				<option value="2">无</option>
			</select>
			</td>
		</tr>
		<tr>
			<td style="width: 90px;text-align: right;">运单号:</td>
			<td style="width: 110px;">
				<input name="waybillCode" id="waybillCode"/>
			</td>
		</tr>
		<tr>
			<td style="width: 90px;text-align: right;">物流公司:</td>
			<td style="width: 110px;">
				<input name="deliverCorpName" id="deliverCorpName"/>
			</td>
		</tr>
		<tr>
			<td style="width: 90px;text-align: right;">预计到货单号:</td>
			<td style="width: 110px;">
				<input name="buyPlanCode" id="buyPlanCode"/>
			</td>
		</tr>
		<tr>
			<td style="width: 90px;text-align: right;">供应商名称:</td><td style="width: 110px;">
			<select name="supplierId" id="supplierId" style="width: 200px;">
				<option value="0">请选择</option>
				<c:forEach items="${supplierList}" var="supplier">
					<option value="${supplier.id}">${supplier.name_abbreviation}（${supplier.name}）</option>
				</c:forEach>
			</select>
			</td>
		</tr>
		<tr>
			<td style="width: 90px;text-align: right;">到货箱/件数:</td>
			<td style="width: 110px;">
				<input name="arrivalCount" id="arrivalCount"/>
			</td>
		</tr>
		<tr>
			<td style="width: 90px;text-align: right;">暂存货位号:</td>
			<td style="width: 110px;">
				<input name="temporaryCargo" id="temporaryCargo"/>
			</td>
		</tr>
		<tr>
			<td style="width: 45px;text-align: right;">产品线:</td><td style="width: 110px;">
			<select name="productLineId" id="productLineId" style="width: 110px;">
				<option value="0">请选择</option>
				<c:forEach items="${productLineList}" var="productLine">
					<option value="${productLine.id}">${productLine.name }</option>
				</c:forEach>
			</select>
			</td>
		</tr>
		<tr>
			<td style="width: 90px;text-align: right;">事业部:</td>
			<td style="width: 110px;">
				<input name="businessUnit" id="businessUnit"/>
			</td>
		</tr>
		<tr>
			<td style="width: 90px;text-align: right;">收货人:</td>
			<td style="width: 110px;">
				<input name="receiver" id="receiver"/>
			</td>
		</tr>
		<tr>
			<td style="width: 90px;text-align: right;">是否打单:</td><td style="width: 110px;">
			<select name="isPrintBill" id="isPrintBill" style="width: 110px;">
				<option value="0">请选择</option>
				<option value="1">是</option>
				<option value="2">否</option>
			</select>
			</td>
		</tr>
		<tr>
			<td style="width: 90px;text-align: right;">到货异常描述:</td><td style="width: 110px;">
			<textarea name="arrivalException" id="arrivalException" rows="4" cols="40"></textarea>
			</td>
		</tr>
	</table>
	<div region="south" border="false" style="text-align:right;">
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-ok" onclick="add()">添加</a>&nbsp;&nbsp;
		<a href="javascript:void(0)" class="easyui-linkbutton" iconCls="icon-cancel" onclick="cancel()">关闭</a>
	</div>
</body>
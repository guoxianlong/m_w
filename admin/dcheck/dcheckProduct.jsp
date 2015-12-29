<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<% 
String code=request.getParameter("code");
%>
<!DOCTYPE html>
<html>
<head>
<title>盘点商品</title>
<style type="text/css">
html { overflow-x: hidden; overflow-y: auto; }
div {overflow:auto;}
</style>
<jsp:include page="../rec/inc/easyui-base.jsp"></jsp:include>
</head>
<body style="margin: 0px">
	<div id="searchAre" style="background-color: #F7F7F7">
		<input type="hidden" id="code" value="${param.code}">
		<input type="hidden" id="stockArea" value="${param.stockArea}">
		<input type="hidden" id="passage" value="${param.passage}">
		<input type="hidden" id="area" value="${param.area}">
		<input type="hidden" id="areaId" value="${param.areaId}">
		<input type="hidden" id="group" value="${param.group}">
		<input type="hidden" id="cargo" value="${param.cargo}">
		<input type="hidden" id="dynamicCheckId" value="${param.dynamicCheckId}">
		<table style="table-layout:fixed;">
			<tr>
				<td>大盘：<c:out value="${param.code}"/></td>
				<td style="text-align: right;width: 50px;">一盘</td>
			</tr>
			<tr>
				<td colspan="2">货位：<c:out value="${param.cargo}"/></td>
			</tr>
			<tr>
				<td><input style="width: 98%;" type="text" id="productCode" value="请扫描商品" onfocus="javascript:if(this.value=='请扫描商品')this.value='';"
					onkeypress="javascript:return checkCodeEnter(event)" onkeydown="initCode();"></td>
				<td ><input style="width: 45px;" type="text" onkeypress="javascript:return checkNumEnter(event)" id="productCount"
					onkeyup='this.value=this.value.replace(/[^0-9]\D*$/,"")' ondragenter="return false" 
					onpaste="return !clipboardData.getData('text').match(/\D/)" style="ime-mode:disabled;"
					onkeydown="initCount();"></td>
			</tr>
		</table>
	</div>
	<div id="listTitle">
		<table id="productListTitle" style="border-collapse: collapse;border-width:thin;border:solid 1px #EAEAEA;margin-left: 1px;background-color: #EAEAEA">
			<tr>
				<td style="border: solid 1px;width: 110px;">商品编号</td>
				<td style="border: solid 1px;">商品名称</td>
				<td style="border: solid 1px;width: 35px;">数量</td>
			</tr>
		</table>
	</div>
	<div id="list">
		<table id="productList" style="border-collapse: collapse;border-width:thin;border:solid 1px #EAEAEA;margin-left: 1px;table-layout:fixed;word-break:break-all;">
			<c:forEach var="pList" items="${cargoProduct}">
				<tr onclick="selectTr(this)">
					<td style="border: solid 1px;width: 110px;"><c:out value="${pList.barcode}"/></td>
					<td style="border: solid 1px;"><c:out value="${pList.name}"/></td>
					<td style="border: solid 1px;width: 35px;"></td>
				</tr>
			</c:forEach>
		</table>
	</div>
	<div id="operBtn" style="background-color: #F7F7F7">
		<table style="text-align: center;width: 100%;margin-top: 2px;">
			<tr>
				<td><button style="width: 50px;height:25px;line-height:20px;" onclick="backUp();">返回</button></td>
				<td><button style="width: 50px;height:25px;line-height:20px;" onclick="reCheck();">重盘</button></td>
				<td><button style="width: 50px;height:25px;line-height:20px;" onclick="setZero();">无</button></td>
				<td><button style="width: 50px;height:25px;line-height:20px;" onclick="submitData();">提交</button></td>
			</tr>
		</table>
	</div>
<script type="text/javascript">
var cols = 3;
var selectedRowIndex=-1;
var scaned = [];
var inputCodeValue = "请扫描商品编号";
var inputCountValue = "数量";
$(document).ready(function(){
	$("#list").height($(window).height()-$("#operBtn").height()-$("#searchAre").height()-$("#listTitle").height()-5);
	$("#productList").width($("#list").width()-20);
	$("#productListTitle").width($("#list").width()-20);
	initInput();
	$("#productCode").focus();
});
onresize = function(){
	$("#list").height($(window).height()-$("#operBtn").height()-$("#searchAre").height()-$("#listTitle").height()-5);
	$("#productList").width($("#list").width()-20);
	$("#productListTitle").width($("#list").width()-20);
}
function initInput(){
	$("#productCode").val(inputCodeValue);
	$("#productCount").val(inputCountValue);
}
/**
 * 选中列表某行的事件
 */
function selectTr(obj){
	selectedRowIndex = obj.rowIndex;
	dealCheckTable();
}
function initCode(){
	if (inputCodeValue != "") {
		inputCodeValue = "";
		$("#productCode").val("");
		$("#productCount").val("数量");
	}
}
function initCount(){
	if (isNaN($("#productCount").val())) {
		$("#productCount").val("");
	}
}
/**
 * 处理列表每行数据的选中状态，通过背景色区分（白色：未扫描未选中，蓝色：选中，绿色：已扫描未选中）
 */
function dealCheckTable(){
	var tabObj = $("#productList tr");
	for (var i=0;i<tabObj.length;i++) {
		if (i == selectedRowIndex) {
			tabObj[i].style.backgroundColor="#4876FF";
		} else {
			var other = false;
			for (var j = 0;j<scaned.length;j++) {
				if (scaned[j] == i) {
					other = true;
					tabObj[i].style.backgroundColor="#006400";
					break;
				}
			}
			if (!other) {
				tabObj[i].style.backgroundColor="#FFFFFF";
			}
		}
	}
}
/**
 * 将选中的商品数量置为0
 */
 function setZero(){
	if (selectedRowIndex == -1) {
		alert("请先选择商品");
	} else {
		if (confirm("是否确定未找到该商品?")) {
			$("#productList tr td")[cols*(selectedRowIndex+1)-1].innerHTML=0;
			var isScaned = false;
			for (var s = 0;s<scaned.length;s++) {//如果商品未扫描过，添加到已扫描列表
				if (scaned[s] == selectedRowIndex) {
					isScaned = true;
					break;
				}
			}
			if (!isScaned) {
				scaned.push(selectedRowIndex);
			}
		}
		selectedRowIndex = -1;
		dealCheckTable();
	}
	$("#productCode").focus();
}
/**
 * 重盘
 */
function reCheck(){
	window.location.reload();
}
function backUp(){
	window.location.href="${pageContext.request.contextPath}/dCheckPDAController/toSelectCheckCargoPage.mmx?area="
			+$("#area").val()+"&stockArea="+$("#stockArea").val()
			+"&pdCode="+$("#code").val()
			+"&dynamicCheckId="+$("#dynamicCheckId").val()
			+"&group="+$("#group").val()
			+"&passage="+$("#passage").val()
			+"&areaId="+$("#areaId").val()
			+"&isBack=1";
}
/**
 * 提交
 */
function submitData(){
	var tabObj = $("#productList tr");
	if (tabObj.length != scaned.length) {
		alert("请先盘点完所有商品");
	} else {
		var codes = [];
		var counts = [];
		for (var i=0;i<tabObj.length;i++) {
			codes[i] = $("#productList tr td")[cols*(i+1)-3].innerHTML;
			counts[i] = $("#productList tr td")[cols*(i+1)-1].innerHTML;
		}
		$.ajax({
			url : '${pageContext.request.contextPath}/dCheckPDAController/finishDynamicCheck2.mmx',
			type : "POST",
			dataType : 'json',
			data : "codes=" + codes
			+ "&counts=" + counts
			+ "&code=" + $("#code").val()
			+ "&stockArea=" + $("#stockArea").val()
			+ "&passage=" + $("#passage").val()
			+ "&area=" + $("#area").val()
			+ "&areaId=" + $("#areaId").val()
			+ "&group=" + $("#group").val()
			+ "&cargo=" + $("#cargo").val()
			+ "&dynamicCheckId=" + $("#dynamicCheckId").val(),
			success : function(rs) {
				if (rs.flag == 1) {
					window.location.href="${pageContext.request.contextPath}/dCheckPDAController/toSelectCheckCargoPage.mmx?group=" + $("#group").val() + "&areaId="+$("#areaId").val()+"&passage="+$("#passage").val()+"&area="+$("#area").val()+"&stockArea="+$("#stockArea").val()+"&isBack=1";
				}
				else {
					alert(rs.message);
				}
			}
		});
	}
}
/**
 * 商品编号输入框按回车键事件
 */
function checkCodeEnter(evt){
	evt = evt ? evt : (window.event ? window.event : null);
	if (evt.keyCode == 13) {
		var code = $("#productCode").val();
		var count = $("#productCount").val();
		code = code.replace(/\s/g,"");
		inputCodeValue = code;
		if (count == "" || isNaN(count)) {
			count = 1;
		}
		if (code == "") {
			alert("请输入商品编号");
		} else if (code == $("#cargo").val()) {//扫描的是货位号，直接提交
			submitData();
		} else {
			var hasChecked=addExistedProdutA(code,count);
			if (!hasChecked) {
				$.ajax({
					url : '${pageContext.request.contextPath}/dCheckPDAController/getProductNameByCode.mmx',
					type : "POST",
					async : false,
					dataType : 'json',
					data : "code=" + code + "&cargo=" + $("#cargo").val(),
					success : function(rs) {
						if (rs.flag == 1) {
							//二次判断商品是否已在列表存在
							hasChecked=addExistedProdutA(code,count);
							if(!hasChecked){
								var pcargo = rs.data.productName.whole_code;
								if ($("#cargo").val() == pcargo) {
									var trHtml = "<tr onclick=\"selectTr(this)\"><td style=\"border: solid 1px;width: 110px;\">"
											+ code
											+ "</td><td style=\"border: solid 1px;\">"
											+ rs.data.productName.name
											+ "</td><td style=\"border: solid 1px;width: 35px;\">"
											+ parseInt(count) + "</td></tr>";
									$("#productList").append(trHtml);
									$("#productCode").focus();
									inputCountValue = parseInt(count);
									$("#productCount").val(inputCountValue);
									scaned.push($("#productList tr").length - 1);
									dealCheckTable();
								} else {
									if (confirm("是否确定在该货位发现了该SKU？")) {
										var trHtml = "<tr onclick=\"selectTr(this)\"><td style=\"border: solid 1px;width: 110px;\">"
											+ code
											+ "</td><td style=\"border: solid 1px;\">"
											+ rs.data.productName.name
											+ "</td><td style=\"border: solid 1px;width: 35px;\">"
											+ parseInt(count) + "</td></tr>";
									$("#productList").append(trHtml);
									$("#productCode").focus();
									inputCountValue = parseInt(count);
									$("#productCount").val(inputCountValue);
									scaned.push($("#productList tr").length - 1);
									dealCheckTable();
									}
								}
							}
						}
						else {
							alert(rs.message);
						}
					}
				});
			} else {
				dealCheckTable();
			}
		}
	}
}
/**
 * 商品编号输入框按回车键事件-增加已扫描商品数量
 */
function addExistedProdutA(code,count){
	var hasChecked = false;
	var tabObj = $("#productList tr");
	for (var i=0;i<tabObj.length;i++) {
		var productTrCode = $("#productList tr td")[cols*(i+1)-3].innerHTML;
		if (code.replace(/\s/g, "") == productTrCode.replace(/\s/g, "")) {//如果商品已在列表中，增加数量
			var isScaned = false;
			for (var s = 0;s<scaned.length;s++) {//如果商品未扫描过，添加到已扫描列表
				if (scaned[s] == i) {
					isScaned = true;
					break;
				}
			}
			if (!isScaned) {
				scaned.push(i);
			}
			hasChecked = true;
			var productTrCount = $("#productList tr td")[cols*(i+1)-1].innerHTML;
			if (productTrCount == "") {
				productTrCount = 0;
			}
			$("#productList tr td")[cols*(i+1)-1].innerHTML = parseInt(count)+parseInt(productTrCount);
			$("#productCode").focus();
			inputCountValue = parseInt(count)+parseInt(productTrCount);
			$("#productCount").val(inputCountValue);
			break;
		}
	}
	return hasChecked;
}


/**
 * 商品数量输入框按回车键事件
 */
function checkNumEnter(evt) {
	evt = evt ? evt : (window.event ? window.event : null);
	if (evt.keyCode == 13) {
		var code = $("#productCode").val();
		var count = $("#productCount").val();
		code = code.replace(/\s/g,"");
		inputCodeValue = code;
		if (count == "" || isNaN(count)) {
			alert("数量不能为空");
		} else if (code == "") {
			alert("请输入商品编号");
		} else {
			var hasChecked = addExistedProdutB(code,count);
			if (!hasChecked) {
				$.ajax({
					url : '${pageContext.request.contextPath}/dCheckPDAController/getProductNameByCode.mmx',
					type : "POST",
					dataType : 'json',
					async : false,
					data : "code=" + code + "&cargo=" + $("#cargo").val(),
					success : function(rs) {
						if (rs.flag == 1) {
							//二次判断商品是否已在列表存在
							hasChecked=addExistedProdutB(code,count);
							if(!hasChecked){
								var pcargo = rs.data.productName.whole_code;
								if ($("#cargo").val() == pcargo) {
									var trHtml = "<tr onclick=\"selectTr(this)\"><td style=\"border: solid 1px;width: 110px;\">"
											+ code
											+ "</td><td style=\"border: solid 1px;\">"
											+ rs.data.productName.name
											+ "</td><td style=\"border: solid 1px;width: 35px;\">"
											+ parseInt(count) + "</td></tr>";
									$("#productList").append(trHtml);
									$("#productCode").focus();
									inputCountValue = parseInt(count);
									$("#productCount").val(inputCountValue);
									scaned.push($("#productList tr").length - 1);
									dealCheckTable();
								} else {
									if (confirm("是否确定在该货位发现了该SKU？")) {
										var trHtml = "<tr onclick=\"selectTr(this)\"><td style=\"border: solid 1px;width: 110px;\">"
											+ code
											+ "</td><td style=\"border: solid 1px;\">"
											+ rs.data.productName.name
											+ "</td><td style=\"border: solid 1px;width: 35px;\">"
											+ parseInt(count) + "</td></tr>";
									$("#productList").append(trHtml);
									$("#productCode").focus();
									inputCountValue = parseInt(count);
									$("#productCount").val(inputCountValue);
									scaned.push($("#productList tr").length - 1);
									dealCheckTable();
									}
								}
							}
						}
						else {
							alert(rs.message);
						}
					}
				});
			} else {
				dealCheckTable();
			}
		}
	}
}

/**
 * 商商品数量输入框按回车键事件-增加已扫描商品数量
 */
function addExistedProdutB(code,count){
	var hasChecked = false;
	var tabObj = $("#productList tr");
	for ( var i = 0; i < tabObj.length; i++) {
		var productTrCode = $("#productList tr td")[cols * (i + 1) - 3].innerHTML;
		if (code.replace(/\s/g, "") == productTrCode.replace(/\s/g, "")) {//如果商品已在列表中，设置数量
			var isScaned = false;
			for (var s = 0;s<scaned.length;s++) {//如果商品未扫描过，添加到已扫描列表
				if (scaned[s] == i) {
					isScaned = true;
					break;
				}
			}
			if (!isScaned) {
				scaned.push(i);
			}
			hasChecked = true;
			$("#productList tr td")[cols * (i + 1) - 1].innerHTML = parseInt(count);
			inputCountValue = parseInt(count);
			$("#productCode").focus();
			break;
		}
	}
	return hasChecked;
}
</script>
</body>
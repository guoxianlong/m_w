<%@ page language="java" pageEncoding="utf-8"%>
<%@include file="/taglibs.jsp"%>
<%@include file="/header.jsp"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<title>选择盘点货位</title>
<script type="text/javascript" src="../js/jquery-1.6.1.js" charset="utf-8"></script>
<head>
<style type="text/css">
ul {margin:0;padding:0;}
li {list-style-type:none;}
html { overflow-x: hidden; overflow-y: auto; }
div {
overflow:auto;
}
.hr{ height:1px;border:none;border-top:1px dashed #0066CC;}
</style>
</head>
<body>
	<div id="content">
		<input type="hidden" id="paramPassage" value="${passage}"/>
		<input type="hidden" id="isBack" value="${isBack}"/>
		<form id="searchForm">
			<span>大盘${pdCode}</span>
			<input type="hidden" id="areaId" name="areaId" value="${areaId}"/>
			<input type="hidden" id="pdCode" name="code" value="${pdCode}"/>
			<input type="hidden" id="stockArea" name="stockArea" value="${stockArea}"/>
			<input type="hidden" id="dynamicCheckId" name="dynamicCheckId" value="${dynamicCheckId}"/>
					<select id="group" name="group" style="float:right;">
									<option value="0" <c:if test="${group eq 0}">selected="selected"</c:if>>A</option>
									<option value="1" <c:if test="${group eq 1}">selected="selected"</c:if>>B</option>
								</select><span style="float:right;">分组</span><br/>
			<span>区域</span>	<select id="area" name="area" onchange="changeArea();">
									<option></option>
									<c:forEach var="map" items="${areaMap}">
										<option value="${map.key }" <c:if test="${map.key eq area}">selected="selected"</c:if>>${map.value}</option>
									</c:forEach>
								</select>
			<span>巷道</span>	<select id="passage" name="passage">
									<option></option>
									<c:forEach var="map" items="${passageMap}">
										<option value="${map.key }" <c:if test="${map.key eq passage}">selected="selected"</c:if>>${map.value}</option>
									</c:forEach>
								</select>
		<input type="button" style="float:right;" onclick="queryData();" value="确定"/>
		</form>
		<input type="text" id="cargoId" name="cargoId" value="请扫描要盘点的货位" onfocus="javascript:if(this.value=='请扫描要盘点的货位')this.value='';" onkeypress="javascript:return checkEnter(event)"/>
		<hr class="hr"/>
		<ul id="cargoList"></ul>
	</div>
</body>
<script>
$(document).ready(function(){
	$("#cargoId").focus();
	if ($("#isBack").val() == "1") {
		changeArea();
		$("#passage").val($("#paramPassage").val());
		queryCargoList();
	}
});

function changeArea() {
	$("#passage").find("option").remove();
	var area = $("#area").val();
	var passages = "";
	// 处理联动 ，先查出此区域的所有巷道编号，再过滤
	<c:forEach var="map" items="${map}">
		if(area == '${map.key}'){
			passages = '${map.value}';
		}
	</c:forEach>
	var str = '';
	passages = passages.replace('[',',').replace(']',',').replace(/\s/g,'');
	var str2 = '';
	<c:forEach var="map2" items="${passageMap}">
		if(passages.indexOf(',' + '${map2.key}' + ',') != -1){
			str += '<option value="${map2.key }">${map2.value}</option>';
		}
	</c:forEach>
	$("#passage").append(str);
}
//获取货位列表
function queryCargoList(){
	$.ajax({
		 type : 'POST',
		 url : 'getCheckCargoList2.mmx?' + $('#searchForm').serialize(),
		 dataType : 'JSON',
		 success : function(data){
		     if(data){
		         var dataInner = '';
		         for(var i=0; i<data.length; i++){
		             dataInner += '<li>'+ data[i] +'</li>';
		         };
		         $('#cargoList').find("li").remove(); 
		         $('#cargoList').append(dataInner);
		     }
		 },
		 error : function () {
            alert('fail');
        }
	});
}
// 获取货位编号列表
function queryData(){
	if(!validForm()){
		return ;
	}
	
	$("#cargoId").focus();
	$.ajax({
		 type : 'POST',
		 url : 'divideIntoGroup.mmx?' + $('#searchForm').serialize(),
		 success : function(data){
		     if(data == "分组已被他人使用盘点过！" || data == "已使用另一分组盘点过！"){
		     	 alert(data);
		     } else {
		    	 queryCargoList();
		     }
		 },
		 error : function (XMLHttpRequest, textStatus, errorThrown) {
             alert(textStatus);
         }
	})
}

// 校验	
function validForm(){
	if($("#area").val()==''){
		alert("请选择区域！");
		return false;
	}
	
	if($("#passage").val()==''){
		alert("请选择巷道！");
		return false;
	}
	
	return true;
}
function checkEnter(evt){
	// 仓库对应区
	var areaId = $("#areaId").val(); 
	// 计划单编码
	var pdCode = $("#pdCode").val();
	// 计划单ID
	var dynamicCheckId = $("#dynamicCheckId").val();
	// 区域ID
	var area = $("#area").val();
	// 巷道ID
	var passage = $("#passage").val();
	// 货位
	var cargoId = $("#cargoId").val();
	// 分组
	var group = $("#group").val();
	// 库区域
	var stockArea = $("#stockArea").val();
	 
	evt = evt ? evt : (window.event ? window.event : null);
	 
	if(evt.keyCode == 13){
		var result = '';
		// 检查此区域货位是否存在此货位
		$.ajax({
		 type : 'POST',
		 async : false,
		 url : 'checkCargoExist.mmx?area=' + area + "&cargo=" + cargoId + "&passageId=" + passage + "&areaId=" + areaId,
		 success : function(data){
		     if(data == ''){
		     	 
		     } else {
			     alert(data);
			     result = data;
		     }
		 },
		 error : function (XMLHttpRequest, textStatus, errorThrown) {
             alert(textStatus);
         }
		});
		
		if(result != ''){
			return;
		}
		
		// 分组判断
		$.ajax({
		 type : 'POST',
		 url : 'divideIntoGroup.mmx?' + $('#searchForm').serialize(),
		 success : function(data){
		     if(data == "分组已被他人使用盘点过！" || data == "已使用另一分组盘点过！"){
		     	 alert(data);
		     } else {
			     // 跳转到盘点页面
				 var url = "${pageContext.request.contextPath}/dCheckPDAController/toCargoProductList.mmx?flag=1&areaId=" + areaId + "&code=" + pdCode + "&dynamicCheckId=" + dynamicCheckId + "&area=" + area + "&passage=" + passage + "&cargo=" + cargoId + "&group=" + group + "&stockArea=" + stockArea;
				 window.document.location = url;
		     }
		 },
		 error : function () {
             alert('fail');
         }
		});
	}
}
</script>
</html>

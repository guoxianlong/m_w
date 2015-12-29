<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>
<link href="<%=request.getContextPath()%>/css/globalTable.css" rel="stylesheet" type="text/css">
<title>封箱</title>
<script>
	
</script>
</head>
<body>
	<fieldset>
		<div style="background-color: #dedede;padding:8px;">
			<form id="addForm" method="post" action="<%=request.getContextPath()%>/admin/AfStock/seal.mmx" onsubmit="return submitForm();">
				<span style="font-size: 12px;">售后处理单号：</span>
				<textarea id="detectCode" name="detectCode" style="height:60px;"></textarea>&nbsp;&nbsp;
				<input type="submit" value="添加"></input>&nbsp;&nbsp;  
				<input type="button" value="重置" onclick="clearSession();"/>
				<span style="font-size: 12px;">扫描售后处理单号，或者手动输入售后处理单号，然后单击‘添加’按钮，添加商品到装箱清单。</span>
			</form>
		</div>
		<div style="padding:8px;">
			 <table class="gridtable" align="center">
			 		<tr>
			 			<th colspan="8" align="center">封箱商品清单</th>
			 		</tr>
			 		<tr>
			 			<td colspan="3">操作人：${operator}</td>
			 			<td colspan="3">封箱日期：${sealDate}</td>
			 			<td colspan="2">封箱编号：${sealCode}</td>
			 		</tr>
			 		<tr>
			 			<th colspan="8">封箱商品明细</th>
			 		</tr>
			 		<tr>
				 		<th>序号</th>
				 		<th>商品名称</th>
				 		<th>型号</th>
				 		<th>IMEI</th>
				 		<th>售后单号</th>
				 		<th>封箱时售后单状态</th>
				 		<th>售后处理单号</th>
				 		<th>封箱时售后处理单状态</th>
				 	</tr>
				 	<c:if test="${!empty tip}">
				 		<script language="JavaScript">
				 			$.messager.show({
								msg : '${tip}',
								title : '提示'
							});
						</script>
				 	</c:if>
				 	<c:forEach items="${productList}" var="sealPro" varStatus="status">
				 		<tr>
				 			<td>${status.count}</td>
				 			<td>${sealPro.productName}</td>
				 			<td>${sealPro.productOriname}</td>
				 			<td>${sealPro.imei}</td>
				 			<td>${sealPro.afterSaleOrderCode}</td>
				 			<td>${sealPro.afterSaleOrderStatusName}</td>
				 			<td>${sealPro.afterSaleDetectProductCode}</td>
				 			<td>${sealPro.afterSaleOrderDetectProductStatusName}</td>
				 		</tr>
				 	</c:forEach>
			 		<tr>
			 			<td colspan="8" align="right">
			 				封箱人签字：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			 				日期：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
			 			</td>
			 		</tr>
			 	</table>
		 	</div>
		 	<br/>
		 	<div align="center"><a id="addAndPrint" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add'">添加完成&nbsp;打印清单</a></div>
	</fieldset>
</body>
<script type="text/javascript">
	function submitForm(){
		var detectCode = $('#detectCode').val();
		if(detectCodes==''){
			$.messager.show({
				msg : '售后处理单号不能为空!',
				title : '提示'
			});
			return false;
		}
		var str = detectCode.split("\n");
		var numlen = str.length;
		var count=0;
		if(numlen>300){
			for(var i=0;i<numlen;i+=1){
				if(str[i] && trim(str[i]).length>0)
					count++;
			}
		}
		if(count>300){
			$.messager.show({
				msg : '售后处理单号至多允许输入300个\n已输入'+count+'个!',
				title : '提示'
			});
			return false;
		}
		return true;
	}
	
	
	$("#addAndPrint").click(function(){
		window.open("${pageContext.request.contextPath}/admin/AfStock/addSealInventory.mmx");
		window.location.href="${pageContext.request.contextPath}/admin/afStock/addAfterSaleSeal.jsp";
	});
	
	function clearSession(){
		$.ajax({
			url : '${pageContext.request.contextPath}/admin/AfStock/clearSession.mmx',
			data : {'paramName':'detectCodes'},
			type : 'post',
			success : function(){
				window.location.href="${pageContext.request.contextPath}/admin/afStock/addAfterSaleSeal.jsp";
			}
		});
	}
</script>
</html>

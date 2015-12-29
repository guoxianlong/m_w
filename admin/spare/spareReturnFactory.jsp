<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@page import="java.util.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>返还供应商</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<link href="<%=request.getContextPath()%>/css/globalTable.css" rel="stylesheet" type="text/css">
</head>
<body>
	<fieldset>
		<div style="background-color: #dedede;padding:8px;">
				<table>
					<tr>
						<td align="right">快递公司：</td>
						<td><input id="deliver" name="deliverId"  required="required" style="width: 155px;" /></td>
					</tr>
					<tr>
						<td align="right">扫描包裹单号：</td>
						<td><input id="packageCode" name="packageCode"  style="width:200px;"></input></td>
					</tr>
					<tr>
						<td align="right">运费金额：</td>
						<td><input id="price" name="price"  style="width:200px;"></input></td>
					</tr>
				</table>
			
		</div>
		<div style="background-color: #dedede;padding:8px;">
				<table>
						<table>
							<tr>
								<td align="right">备用机单号：</td>
								<td><textarea id="spareCodes" name="spareCodes" rows="5" style="width:200px;"></textarea></td>
								<td><input type="button" onclick="addSpareCode()" value="添加"/><br/><input type="button" value="重置" onclick="clearContext()"/></td>
							</tr>
						</table>
				</table>
			
		</div>
		<br/>
		<div style="padding:8px;">
			<form id="backSupplierForm" method="post">
				<table class="gridtable">
					<tr>
						<th colspan="10" align="center">无锡买卖宝备用机返还厂家清单</th>
					</tr>
					<tr>
						<td colspan="3">经销商：无锡买卖宝信息技术有限公司深圳售后服务中心</td>
						<td colspan="2">供应商：${spareBackSupplier.supplierName}</td>
						<td colspan="5">供应商地址：${spareBackSupplier.supplierAddress}</td>
					</tr>
					<tr>
						<td colspan="3">填单人：${spareBackSupplier.operateUserName}</td>
						<td colspan="2">填单日期：${spareBackSupplier.createDate}</td>
						<td colspan="5"></td>
					</tr>
					<tr>
						<th align="left" colspan="10">备用机返还厂家清单明细</th>
					</tr>
					<tr>
						<th>序号</th>
				 			<th>商品编号</th>
				 			<th>商品原名称</th>
				 			<th>IMEI码</th>
				 			<th>返还数量</th>
				 			<th>操作</th>
					</tr>
					<c:if test="${!empty tip}">
					<script language="JavaScript">
						$.messager.show({
							msg : '${tip}',
							title : '提示'
						});
					</script>
				</c:if>
					<c:forEach var="item" items="${productDetailMap}" varStatus="ststus">
						<tr>
							<td>${ststus.count}</td>
							<td>${item.value.productCode}</td>
							<td>${item.value.productName}</td>
							<c:choose>
							   <c:when test="${fn:startsWith(item.value.imei, '-')}">  
							        <td></td>      
							   </c:when>
							   <c:otherwise> 
							   		<td>${item.value.imei}</td>
							   </c:otherwise>
							</c:choose>
							<td><div class="allCount">${item.value.count}</div></td>
							<td><a href="javascript:void(0);" onclick="deleteProductDetai('${item.value.productId}','${item.value.spareCode}')" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-remove'">删除</a></td>
						</tr>
					</c:forEach>
					<tr>
						<th colspan="10" align="left">SKU种类共计：<c:out value="${fn:length(productDetailMap)}"></c:out>&nbsp返还数量共计：<a id="total"></a></th>
					</tr>
					<tr>
						<td colspan="10">备注：<textarea id="remark" name="remark" style="width:400px;"></textarea></td>
					</tr>
					<tr>
						<td colspan="10">
							敬爱的厂商：您好，备用机返还清单如上所示，请及时查实，如果数量与实物不符， 请在24H内通知我司核实，感谢您的支持！
						</td>
					</tr>
					<tr>
						<td colspan="10">
								<span>我司地址：</span>
								<input type="text" name="ourAddress" id="ourAddress" size="50" maxlength="49"/>
								<span>邮编：</span><input type="text" name="zipCode" id="zipCode"   maxlength="6"/><br/>
								收件人:<span></span><input type="text" name="receiverName" id="receiverName" size="10"  maxlength="10"/ >
								电话: <span></span><input type="text" name="phone" id="phone" size="20"  maxlength="30"/ >
								</span>
						</td>
					</tr>
					<tr>
						<td colspan="10" align="center"><a href="javascript:void(0);" id="addButton" onclick="addAndPrint2()" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add',disabled:'true'">添加完成&nbsp;打印清单</a></td>
					</tr>
				</table>
			</form>
		</div>
	</fieldset>
</body>
</html>
<script type="text/javascript" charset="UTF-8">
	var deliverIds;
	var total = 0;
	$(function(){
		
		var session ='${productDetailMap}';
		if(session!='' && session!='{}'){
			$('#addButton').linkbutton('enable');
		}
		
		$('#deliver').combobox({
	      	url : '${pageContext.request.contextPath}/Combobox/getDeliver.mmx',
	      	valueField:'id',
			textField:'text',
			onLoadSuccess : function(){
				deliverIds = $('#deliver').combobox('getData');
			}});
		
		$(".gridtable .allCount").each(function(){
			 total = total + ($(this).text() - 0);			
		})
		$("#total").append(total);
		
		window.onload = function(){
			document.getElementById('ourAddress').onkeydown = function(){
				if(this.value.length == 49)
			    	$.messager.show({
						msg : '"我司地址"已超过上限字数50字，请重新填写！',
						title : '提示'
					});
			      event.returnValue = false;
			}
			document.getElementById('remark').onkeydown = function(){
				if(this.value.length == 100)
			    	$.messager.show({
						msg : '"备注"已超过上限字数100字，请重新填写！',
						title : '提示'
					});
			      event.returnValue = false;
			}
		} 
	});
	
	function addButton(){
		var deliver = $('#deliver').combobox('getText')
		var packageCode = $('#packageCode').val();
		var price = $('#price').val();
	}
	
	function addSpareCode() {
		var codes =$('#spareCodes').val();
		if(codes==''){
			$.messager.show({
				msg :'备用机号不能为空',
				title : '提示'
			});
			return;
		}
		$.ajax({
			type : "POST",
			cache : false,
			url : "${pageContext.request.contextPath}/spareManagerController/returnFactory.mmx",
			data : {spareCodes : codes},
			dataType : 'json',
			success : function(j) {
				$('#spareCodes').val('');
				if(j.success==false){
					$.messager.show({
						msg : j.msg,
						title : '提示'
					});
				}else{
					window.location.reload();
				}
			}
		});
	}
	
	function deleteProductDetai(productId){
		$.ajax({
			type : "POST",
			cache : false,
			url : "${pageContext.request.contextPath}/spareManagerController/delete.mmx",
			data : {productId:productId},
			dataType : 'json',
			success : function(j) {
				if(j.success==false){
					$.messager.show({
						msg : j.msg,
						title : '提示'
					});
				}else{
					window.location.reload();
				}
			}
		});
	}
	
	function checkDetectCode(){
		var spareCodes = $("#spareCodes").val();
		if(spareCodes==''){
			$.messager.show({
				msg : '备用机单号不能为空!',
				title : '提示'
			});
			return false;
		}
		var str = spareCodes.split("\n");
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
				msg : '备用机单号至多允许输入300个\n已输入'+count+'个!',
				title : '提示'
			});
			return false;
		}
		return true;
	}
	
	function submitForm(){
		var flag = checkDetectCode();
		return flag;
	}
	
	function addAndPrint2(){
		var spareCodes = $("#spareCodes").val();
		var deliver = $('#deliver').combobox('getValue');
		var price = $('#price').val();
		var packageCode = $('#packageCode').val();
		var ourAddress = $('#ourAddress').val();
		var zipCode = $('#zipCode').val();
		var receiverName = $('#receiverName').val();
		var phone = $('#phone').val();
		var remark = $('#remark').val();
		if(isNaN(deliver)){
        	$.messager.show({
				msg : '请选择快递公司!',
				title : '提示'
			});
        	return false;
        }
        if(deliver==0 ){
        	$.messager.show({
				msg : '请选择快递公司!',
				title : '提示'
			});
         	return false;
        }
        if(price=='' ){
        	$.messager.show({
				msg : '请选择金额!',
				title : '提示'
			});
         	return false;
        }
         if(packageCode==''){
         	$.messager.show({
				msg : '包裹单号不能为空!',
				title : '提示'
			});
         	return false;
         }
         if(ourAddress==''){
          	$.messager.show({
 				msg : '我司地址不能为空!',
 				title : '提示'
 			});
          	return false;
          }
         
         if(receiverName==""){
          	$.messager.show({
 				msg : '收件人不能为空!',
 				title : '提示'
 			});
          	return false;
          }
         if(phone==""){
           	$.messager.show({
  				msg : '电话不能为空!',
  				title : '提示'
  			});
           	return false;
           }
         
         if(zipCode==""){
         	$.messager.show({
				msg : '邮编不能为空!',
				title : '提示'
			});
         	return false;
         }else{
         	var currency = /[0-9]{6}/;
			if(!currency.test(zipCode)){
			   $.messager.show({
					msg : '请输入正确的邮编格式!',
					title : '提示'
				});
         		return false;
			  }
         }
		$.ajax({
			type : "POST",
			cache : false,
			url : "${pageContext.request.contextPath}/spareManagerController/printReturnFactory.mmx",
			data : {spareCodes : spareCodes,
				deleverId : deliver,
				price : price, 
				total:total,
				packageCode:packageCode,
				ourAddress:ourAddress,
				zipCode:zipCode,
				receiverName:receiverName,
				phone:phone,
				remark:remark
			},
			dataType : 'json',
			success : function(obj) {
				if(obj.success){
					$("#spareCodes").val('');
					$('#deliver').combobox('clear');
					$('#price').val('');
					$('#packageCode').val('');
					$('#ourAddress').val('');
					$('#zipCode').val('');
					$('#receiverName').val('');
					$('#phone').val('');
					$('#remark').val('');
					window.open('${pageContext.request.contextPath}/spareManagerController/print.mmx?id=' + obj.obj);
					window.location.href = '${pageContext.request.contextPath}/admin/spare/spareReturnFactory.jsp';
				}else{
					$.messager.show({
						msg : obj.msg,
						title : '提示'
					});
				}
			}
		});
	}
	
	function clearContext(){
		$("#deliver").combobox('clear');
		$('#packageCode').val('');
		$('#price').val('');
		$('#spareCodes').val('');
		$.ajax({
			type : "POST",
			cache : false,
			url : "${pageContext.request.contextPath}/spareManagerController/clean.mmx",
			dataType : 'json',
			success : function(j) {
				if(j.success==false){
					$.messager.show({
						msg : j.msg,
						title : '提示'
					});
				}else{
					window.location.reload();
				}
			}
		});
	}
</script>

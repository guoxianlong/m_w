<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@page import="java.util.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>添加返厂商品</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<link href="<%=request.getContextPath()%>/css/globalTable.css" rel="stylesheet" type="text/css">
</head>
<body>
	<fieldset>
		<div style="background-color: #dedede;padding:8px;">
			<form id="form" action="${pageContext.request.contextPath}/admin/AfStock/preAddbackSupplierProduct.mmx" method="post" onsubmit="return submitForm();">
				<table>
					<tr>
						<td align="right">售后处理单号：</td>
						<td><textarea id="detectCodes" name="detectCodes" rows="5" style="width:200px;"></textarea></td>
						<td><input type="submit" value="添加"/><br/><input type="button" value="重置" onclick="clearSession();"/></td>
					</tr>
				</table>
			</form>
		</div>
		<br/>
		<div style="padding:8px;">
			<form id="backSupplierForm" method="post">
				<table class="gridtable">
					<tr>
						<th colspan="10" align="center">无锡买卖宝送修清单</th>
					</tr>
					<tr>
						<td colspan="3">经销商：无锡买卖宝</td>
						<td colspan="2">发货日期：</td>
						<td colspan="5">返回厂家名称：<input type="text" id="backSupplierId" name="backSupplierId"  ></td>
					</tr>
					<tr>
						<td colspan="3">联系人：<input  type="text" name="contract" id="contract"/></td>
						<td colspan="2">联系电话：<input type="text" name="contractPhone"  id="contractPhone"
						 value="<c:choose><c:when test='${areaId==1}'>0510-88576956-807</c:when><c:when test='${areaId==7}'>13728817991</c:when><c:otherwise></c:otherwise></c:choose>"/></td>
						<td colspan="5">运输单号：<input type="text" name="packageCode" id="packageCode"></td>
					</tr>
					<tr>
						<th align="left" colspan="10">送修商品明细</th>
					</tr>
					<tr>
						<th>序号</th>
				 			<th>型号</th>
				 			<th>返厂商品类型</th>
				 			<th>售后处理单号</th>
				 			<th>IMEI</th>
				 			<th>是否返修</th>
				 			<th>故障代码</th>
				 			<th>故障描述</th>
				 			<th>申报状态</th>
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
					<c:forEach var="productDetail" items="${productDetailList}" varStatus="ststus">
						<tr>
							<td>${ststus.count}</td>
							<td><input type="text" name="oriname_${productDetail.afterSaleDetectProductId}" value="${productDetail.productOriname}" /></td>
							<td>
								<c:choose>
									<c:when test="${productDetail.backRepairProductType==9}">售后机</c:when>
									<c:when test="${productDetail.backRepairProductType==10}">客户机</c:when>
									<c:otherwise></c:otherwise>
								</c:choose>
							</td>
							<td>${productDetail.detectCode}</td>
							<td>
								<c:choose>
									<c:when test="${not empty productDetail.imei}">
										${productDetail.imei}
									</c:when>
									<c:otherwise>
										<input name="imei_${productDetail.afterSaleDetectProductId}" />
									</c:otherwise>
								</c:choose>
							</td>
							<td>${productDetail.firstRepairName}</td>
							<td>${productDetail.faultCode}</td>
							<td>${productDetail.faultDescript}</td>
							<td>${productDetail.reportStatus}</td>
							<td><a href="javascript:void(0);" onclick="deleteSupplierProduct('${productDetail.detectCode}');" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-remove'">删除</a></td>
						</tr>
					</c:forEach>
					<tr>
						<th colspan="10" align="left">总计：${totalCount}</th>
					</tr>
					<tr>
						<td colspan="10">备注：<textarea id="remark" name="remark" style="width:400px;"></textarea></td>
					</tr>
					<tr>
						<td colspan="10">
							敬爱的客户：请您在收到包裹后核实无误签名将此单发回我司，数量如与实物不符，
							请在24H内通知我司核实，如在收货后未做答复，表示默认此包裹帐实相符，
							我司不再作答，感谢支持！
						</td>
					</tr>
					<tr>
						<td colspan="10" align="right">
							发货方签字：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							日期：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					</tr>
					<tr>
						<td colspan="10" align="right">
							我已确认收到的实物与装箱单的明细准确无误&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							收货方签名/盖章：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							日期：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					</tr>
					<tr>
						<td colspan="10">
								<span>维修好请返回以下地址：</span>
								<input type="text" name="deliveryAddress" id="deliveryAddress" value="<c:choose><c:when test='${areaId==1}'>江苏省无锡市锡山区东亭镇友谊北路321号南方物流园B2-1（近芙蓉四路）</c:when><c:when test='${areaId==7}'>深圳市龙华新区和平工业园昌永路金星大厦8楼A801</c:when><c:when test='${areaId==4}'>江苏省无锡市锡山区东亭镇友谊北路321号南方物流园买卖宝B3-2仓</c:when><c:otherwise></c:otherwise></c:choose>"  size="60"/ >
								<span>邮编：</span><input type="text" name="zipCode" id="zipCode" value="<c:choose><c:when test='${areaId==1}'>214101</c:when><c:when test='${areaId==7}'>518000</c:when><c:when test='${areaId==4}'>214101</c:when><c:otherwise></c:otherwise></c:choose>" / ><br/>
								收件人:<span id="receiverName"></span>&nbsp;&nbsp;&nbsp;&nbsp;
								电话: <span id="phone">
								<c:choose>
									<c:when test='${areaId==1}'>0510-88576956-807</c:when>
									<c:when test='${areaId==7}'>13728817991</c:when>
									<c:when test='${areaId==4}'>0510-88576956-832/15852766623</c:when>
									<c:otherwise></c:otherwise>
								</c:choose>
								</span>
						</td>
					</tr>
					<tr>
						<td colspan="10" align="center"><a href="javascript:void(0);" onclick="addAndPrint();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add'">添加完成&nbsp;打印清单</a></td>
					</tr>
				</table>
			</form>
		</div>
		<div>
			包裹单号：<input type="text" id="packageNumber"/>
			<a href="javascript:void(0);" onclick="print();" class="easyui-linkbutton" plain="true" data-options="iconCls:'icon-add'">打印送修清单</a>
		</div>
	</fieldset>
</body>
</html>
<script type="text/javascript" charset="UTF-8">
	$(function(){
		$("#backSupplierId").combobox({
			url : '${pageContext.request.contextPath}/Combobox/getBackSupplier.mmx',
			valueField:'id',
			textField:'text',
			delay:500
		});
		
		$("#contract").blur(function(){
			$("#receiverName").html($("#contract").val());
		});
		
		$("#contractPhone").blur(function(){
			$("#phone").html($("#contractPhone").val());
		});
		
	});
	
	function checkDetectCode(){
		var detectCodes = $("#detectCodes").val();
		if(detectCodes==''){
			$.messager.show({
				msg : '售后处理单号不能为空!',
				title : '提示'
			});
			return false;
		}
		var str = detectCodes.split("\n");
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
	
	function submitForm(){
		var flag = checkDetectCode();
		return flag;
	}
	
	function addAndPrint(){
		$("#backSupplierForm").form("submit",{
			url : '${pageContext.request.contextPath}/admin/AfStock/addbackSupplierProduct.mmx',
			onSubmit: function(){  
		        //进行表单验证  
		        //如果返回false阻止提交 
		        var backSupplierId = $("#backSupplierId").combobox('getValue');
		        if(backSupplierId=="-1"){
		        	$.messager.show({
						msg : '请选择返回厂家!',
						title : '提示'
					});
		        	return false;
		        }
		        if(backSupplierId==0 ){
		        	$.messager.show({
						msg : '请选择返回厂家!',
						title : '提示'
					});
		         	return false;
		        }
		         var contract = $("#contract").val();
		         if(contract==""){
		         	$.messager.show({
						msg : '联系人不能为空!',
						title : '提示'
					});
		         	return false;
		         }
		         var packageCode = $("#packageCode").val();
		         if(packageCode==''){
		         	$.messager.show({
						msg : '运输包裹单号不能为空!',
						title : '提示'
					});
		         	return false;
		         }
		         
		         var contractPhone = $("#contractPhone").val();
		         if(contractPhone==""){
		         	$.messager.show({
						msg : '联系电话不能为空!',
						title : '提示'
					});
		         	return false;
		         }
		         
		         var deliveryAddress = $("#deliveryAddress").val();
		         if(deliveryAddress==""){
		         	$.messager.show({
						msg : '收货地址不能为空!',
						title : '提示'
					});
		         	return false;
		         }
		         
		         var zipCode = $("#zipCode").val();
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
   			 },
			success : function(d) {
				var obj = jQuery.parseJSON(d);
				if (obj) {
					if(obj.success){
						window.open('${pageContext.request.contextPath}/admin/AfStock/printRepairList.mmx');
						window.location.href="${pageContext.request.contextPath}/admin/afStock/addBackSupplierProduct.jsp";
					}else{
						$.messager.show({
							msg : obj.msg,
							title : '提示'
						});
					}
				}
			} 
		});
	}
	
		
	
	function print(){
		var packageCode = $("#packageNumber").val();
		if(packageCode!=''){
			window.open('${pageContext.request.contextPath}/admin/AfStock/printRepairListByPackageCode.mmx?packageCode=' + packageCode);
		}else{
			$.messager.show({
				msg : '请输入包裹单号!',
				title : '提示'
			});
		}
	}
	
	function clearSession(){
		$.ajax({
			url : '${pageContext.request.contextPath}/admin/AfStock/clearSession.mmx',
			data : {
					'paramName':'backSupplierProductDetectCodesSet',
					'stockType':'stockType',
					'areaId':'areaId'},
			type : 'post',
			success : function(){
				window.location.href="${pageContext.request.contextPath}/admin/afStock/addBackSupplierProduct.jsp";
			}
		});
	}
	
	function deleteSupplierProduct(detectCode){
		window.location.href='${pageContext.request.contextPath}/admin/AfStock/preAddbackSupplierProduct.mmx?flag=delete&detectCodes=' + detectCode;
	}
</script>

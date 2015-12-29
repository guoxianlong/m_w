<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<title>厂商寄回商品检测</title>
<script type="text/javascript" charset="UTF-8">
function getDetectProductInfo(){
	if($("#afterSaleDetectProductCode").val() == "") {
		$.messager.show({
			title : '提示',
			msg : '必须填写售后处理单号！'
		});
		return false;
	}
	$.ajax({
		url : '${pageContext.request.contextPath}/admin/AfStock/getDetectProductInfo.mmx',
		data :  {'afterSaleDetectProductCode' : $("#afterSaleDetectProductCode").val(),
					'flag' : '2',
					'operate' : '2'
				},
		type : 'post',
		dataType : 'json',
		success : function(result){
			if (result.success) {
				$("#productOriname").html(result.obj.productOriname);
				
				initCombobox('faultDescriptId',4,result.obj.parentId1,false,false);
				initCombobox('faultDescriptId2',4,-1,true,false);
				initCombobox('faultDescriptId3',4,-1,true,false);
				
			    var backSupplierProductId = result.obj.backSupplierProductId;
			    if(backSupplierProductId==null || backSupplierProductId=='' || backSupplierProductId==0){
			    	$.messager.show({
						title : '提示',
						msg : '售后处理单对应的返厂商品不存在!'
					});
			    }else{
			    	 $("#backSupplierProductId").val(result.obj.backSupplierProductId);
			    }
			}else{
				$.messager.show({
					title : '提示',
					msg : result.msg
				});
			}
		}
	});
}

function initCombobox(inputId,afterSaleDetectTypeId, parentId1,disabled,editable) {
	$('#' + inputId).combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getAfterDetectDetailForOneGrade.mmx?afterSaleDetectTypeId=' + afterSaleDetectTypeId + '&parentId1=' + parentId1,
      	valueField:'id',
		textField:'text',
		delay:500,
		disabled:disabled,
		editable:editable,
		onSelect : function(record){
			if(record.id==""){
				$('#' + inputId +'2').combobox('setText','请选择');
				$('#' + inputId +'2').combobox('setValue','');
				$('#' + inputId +'3').combobox('setText','请选择');
				$('#' + inputId +'3').combobox('setValue','');
				$('#' + inputId +'2').combobox('disable');
				$('#' + inputId +'3').combobox('disable');
				return false;
			}
			$('#' + inputId +'2').combobox({
		      	url : '${pageContext.request.contextPath}/Combobox/getAfterDetectDetailForTwoGrade.mmx?afterSaleDetectTypeId=' + afterSaleDetectTypeId + '&id=' + record.id,
		      	valueField:'id',
				textField:'text',
				delay:500,
				disabled:disabled,
				editable:editable,
				onSelect : function(record){
					if(record.id==""){
						$('#' + inputId +'3').combobox('setText','请选择');
						$('#' + inputId +'3').combobox('setValue','');
						$('#' + inputId +'3').combobox('disable');
						return false;
					}
					$('#' + inputId +'3').combobox({
				      	url : '${pageContext.request.contextPath}/Combobox/getAfterDetectDetailForThreeGrade.mmx?afterSaleDetectTypeId=' + afterSaleDetectTypeId + '&id=' + record.id,
				      	valueField:'id',
						textField:'text',
						delay:500,
						disabled:disabled,
						editable:editable
				    });
				}
		    });
		}
    });
}

function detectFun(flag){
	getDetectProductInfo();
	$.ajax({
	    url : "${pageContext.request.contextPath}/admin/AfStock/detectAfterSaleProduct.mmx",
		type : "POST",
		dataType : 'json',
		cache: false,
		data : {
			flag : flag,
			backSupplierProductId : $("#backSupplierProductId").val(),	
			faultDescription : $('#faultDescriptId').combobox('getText')+"/"+$('#faultDescriptId2').combobox('getText')+"/"+$('#faultDescriptId3').combobox('getText')
		},
		success: function(d){
			$.messager.show({
					msg : d.msg,
					title : '提示'
				});
			if (d.success) {
				window.location.href="${pageContext.request.contextPath}/admin/afStock/detectAfterSaleBackSupplier.jsp";
			}
		}
	});
}
</script>
</head>
<body>
	<table align="center" class="tableForm">
		<tr>
			<th>售后处理单号：</th>
			<td><input type="text" name="afterSaleDetectProductCode" id="afterSaleDetectProductCode" onblur="getDetectProductInfo();"/></td>
		</tr>
		<tr>
			<th>商品原名称：</th>
			<td><span id="productOriname"></span></td>
		</tr>
		<tr>
			<th>故障描述：</th>
			<td>
				<input type="text" name="faultDescriptId" id="faultDescriptId"/>
				<input type="text" name="faultDescriptId2" id="faultDescriptId2"/>
				<input type="text" name="faultDescriptId3" id="faultDescriptId3"/>
				<input type="hidden" name="backSupplierProductId" id="backSupplierProductId" />
				<input type="button"  onclick="detectFun('1')" value="检测合格" />
				<input type="button"  onclick="detectFun('2')" value="检测不合格" />
			</td>
		</tr>
	</table>
</body>
</html>
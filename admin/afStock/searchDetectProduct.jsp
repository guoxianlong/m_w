<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>售后处理单查询</title>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	datagrid = $('#detectProductsDatagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/AfStock/searchDetectProduct.mmx',
	    toolbar : '#tb',
	    idField : 'id',
	    view: myview,
		emptyMsg: '没有查询到匹配的售后处理单',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : true,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    pageSize : 20,
	    pageList : [ 10, 20, 30, 40, 50 ],
	    columns:[[  
			{field:'afterSaleDetectProductCode',title:'售后处理单号',width:60,align:'center',
				formatter : function(value, row, index) {
				    return '<a href="javascript:void(0);" class="editbutton" onclick="afterSaleDetectProduct('+row.id+')">'+row.afterSaleDetectProductCode+'</a>';
			}},
			{field:'afterSaleOrderCode',title:'售后单号',width:60,align:'center'},
			{field:'orderCode',title:'订单编号',width:60,align:'center'},
			{field:'productCode',title:'商品编号',width:60,align:'center'},
			{field:'productName',title:'小店名称',width:60,align:'center'},
			{field:'nextStep',title:'下一步操作',width:60,align:'center'},
			{field:'parentId1Name',title:'商品一级分类',width:60,align:'center'},
			{field:'problemDescription',title:'客户问题描述',width:60,align:'center'},
			{field:'customerName',title:'姓名',width:60,align:'center'},
			{field:'customerPhone',title:'联系电话',width:60,align:'center'},
			{field:'createUserName',title:'一检人',width:60,align:'center'},
			{field:'createDatetime',title:'一检时间',width:60,align:'center',
				formatter : function(value, row, index) {
        			return value != null && value != undefined ? value.substring(0, 19) : '';
				}
			},
			{field:'orderConfirmTime',title:'订单签收时间',width:60,align:'center',
				formatter : function(value, row, index) {
        			return value != null && value != undefined ? value.substring(0, 11) : '';
				}
			},
			{field:'content',title:'处理建议',width:60,align:'center'},
			{field:'asoStatusName',title:'售后单状态',width:80,align:'center'},
			{field:'asdpStatusName',title:'售后处理单状态',width:80,align:'center'},
			{field:'cargoWholeCode',title:'货位号',width:80,align:'center'}
	    ] ],
		onLoadSuccess : function(data) {
		}
	}); 
	$('#areaId').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getSignArea.mmx',
      	valueField:'id',
		textField:'text',
		editable:false
    });
	$('#content').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getHandles.mmx',
      	valueField:'id',
		textField:'text',
		editable : false
	});
	
	$('#nextStep').combobox({
      	valueField:'id',
		textField:'text',
		data: [{
			id: '1',
			text: '待再次检测'
		},{
			id: '2',
			text: '返厂'
		},{
			id: '3',
			text: '解封'
		},{
			id: '4',
			text: '入售后库'
		},{
			id: '5',
			text: '寄回用户'
		}],
		editable : false
	});
	
	$('#detectProductStatus').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getPartAfterSaleDetectProductStatus.mmx',
      	valueField:'id',
		textField:'text',
		editable : false
	});
	
	$('#parentId1').combobox({
      	url : '${pageContext.request.contextPath}/Combobox/getParentId1.mmx',
      	valueField:'id',
		textField:'text',
		editable : false,
	});
});

function getDateFromString(strDate){
    var arrYmd   =  strDate.split("-");
    var numYear  =  parseInt(arrYmd[0],10);
    var numMonth =  parseInt(arrYmd[1],10)-1;
    var numDay   =  parseInt(arrYmd[2],10);
    var leavetime=new Date(numYear,  numMonth,  numDay);
    return leavetime;

}

function searchFun() {
	if (checksubmit()) {
		datagrid.datagrid("load", {
			afterSaleDetectProductCode:$("#tb textarea[id=afterSaleDetectProductCode]").val(),
			afterSaleOrderCode:$("#tb textarea[id=afterSaleOrderCode]").val(),
			orderCode:$("#tb textarea[id=orderCode]").val(),
			productCode:$("#tb input[id=productCode]").val(),
			detectProductUserName:$("#tb input[id=detectProductUserName]").val(),
			nextStep:$("#tb input[id=nextStep]").combobox("getValue"),
			detectProductStatus:$("#tb input[id=detectProductStatus]").combobox("getValue"),
			parentId1:$("#tb input[id=parentId1]").combobox("getValue"),
			phone:$("#tb input[id=phone]").val(),
			startTime:$("#tb input[id=startTime]").datebox("getValue"),
			endTime:$("#tb input[id=endTime]").datebox("getValue"),
			orderConfirmStartTime:$("#tb input[id=orderConfirmStartTime]").datebox("getValue"),
			orderConfirmEndTime:$("#tb input[id=orderConfirmEndTime]").datebox("getValue"),
			content:$("#tb input[id=content]").combobox("getValue"),
			areaId : $('#areaId').combobox('getValue')
		});
	}
}
//导出excel
function exportFun(){
	if (checksubmit()) {
		window.location.href = "${pageContext.request.contextPath}/admin/AfStock/searchDetectProductExport.mmx?"
			+"afterSaleDetectProductCode="+$("#tb textarea[id=afterSaleDetectProductCode]").val()
			+"&afterSaleOrderCode="+$("#tb textarea[id=afterSaleOrderCode]").val()
			+"&orderCode="+$("#tb textarea[id=orderCode]").val()
			+"&productCode="+$("#tb input[id=productCode]").val()
			+"&detectProductUserName="+$("#tb input[id=detectProductUserName]").val()
			+"&nextStep="+$("#tb input[id=nextStep]").combobox("getValue")
			+"&detectProductStatus="+$("#tb input[id=detectProductStatus]").combobox("getValue")
			+"&parentId1="+$("#tb input[id=parentId1]").combobox("getValue")
			+"&phone="+$("#tb input[id=phone]").val()
			+"&startTime="+$("#tb input[id=startTime]").datebox("getValue")
			+"&endTime="+$("#tb input[id=endTime]").datebox("getValue")
			+"&orderConfirmStartTime="+$("#tb input[id=orderConfirmStartTime]").datebox("getValue")
			+"&orderConfirmEndTime="+$("#tb input[id=orderConfirmEndTime]").datebox("getValue")				
			+"&content="+$("#tb input[id=content]").combobox("getValue")
			+"&areaId="+$('#areaId').combobox('getValue');
	}
}

function checksubmit(){
	var afterSaleDetectProductCode =$("#tb textarea[id=afterSaleDetectProductCode]").val();
	if(afterSaleDetectProductCode!=""){
		if (!splitCount(afterSaleDetectProductCode, 100, "最多输入100个售后处理单号！")) {
			return false;
		}
	}
	var afterSaleOrderCode = $("#tb textarea[id=afterSaleOrderCode]").val();
	if(afterSaleOrderCode!=""){
		if(!splitCount(afterSaleOrderCode, 100, "最多输入100个售后单号！") ) {
			return false;
		}
	}
	var orderCode = $("#tb textarea[id=orderCode]").val();
	if(orderCode!=""){
		if (!splitCount(orderCode, 100, "最多输入100个订单号！")) {
			return false;
		}
	}
	var productCode = $.trim($("#tb input[id=productCode]").val());
	var detectProductUserName = $.trim($("#tb input[id=detectProductUserName]").val());
	var phone = $.trim($("#tb input[id=phone]").val());
	var startTime=$("#tb input[id=startTime]").datebox("getValue");
	var endTime=$("#tb input[id=endTime]").datebox("getValue");
	var orderConfirmStartTime=$("#tb input[id=orderConfirmStartTime]").datebox("getValue");
	var orderConfirmEndTime=$("#tb input[id=orderConfirmEndTime]").datebox("getValue");
    if (!checkDate(startTime, endTime)) {
    	return false;
    }
    if (!checkDate(orderConfirmStartTime,orderConfirmEndTime)) {
    	return false;
    }
    var orderConfirmStartTime=$("#tb input[id=orderConfirmStartTime]").datebox("getValue");
    var orderConfirmEndTime=$("#tb input[id=orderConfirmEndTime]").datebox("getValue");
    var content=$("#tb input[id=content]").combobox("getValue");
	var nextStep=$("#tb input[id=nextStep]").combobox("getValue");
	var detectProductStatus=$("#tb input[id=detectProductStatus]").combobox("getValue");
	var parentId1=$("#tb input[id=parentId1]").combobox("getValue");
	var areaId = $('#areaId').combobox('getValue');
	if (afterSaleDetectProductCode == "" && afterSaleOrderCode == "" && orderCode=="" &&
			productCode == "" && phone == "" && detectProductUserName == "" && startTime=="" && endTime == ""
			&& orderConfirmStartTime=="" && orderConfirmEndTime=="" && content=="" && nextStep=="" && detectProductStatus=="" && parentId1=="" && areaId == "") {
		$.messager.show({
			msg : "请输入查询条件！",
			title : '提示'
		});
		return false;
	} else if (afterSaleDetectProductCode == "" && afterSaleOrderCode == "" && orderCode=="" &&
		productCode == "" && phone == "" && detectProductUserName == "" && startTime=="" && endTime == "") {
		var nowDate = new Date();
		var str =nowDate.getFullYear()+"-"+((nowDate.getMonth() + 1) < 10 ? '0' +(nowDate.getMonth() + 1) : (nowDate.getMonth() + 1))+"-"+nowDate.getDate();
		nowDate.setTime(nowDate.getTime()-60*24*3600*1000);
		var str2 = nowDate.getFullYear()+"-"+((nowDate.getMonth() + 1) < 10 ? '0' +(nowDate.getMonth() + 1) : (nowDate.getMonth() + 1))+"-"+nowDate.getDate();
		$("#tb input[id=startTime]").datebox("setValue", str2);
		$("#tb input[id=endTime]").datebox("setValue", str);
	}
	return true;
}

function splitCount(value, theCount, tip) {
	var str = value.split("\n");
	var numlen = str.length;
	var count=0;
	if(numlen>theCount){
		for(var i=0;i<numlen;i+=1){
			if(str[i] && $.trim(str[i]).length>0)
				count++;
		}
	}
	if(count>theCount){
		$.messager.show({
			msg : tip,
			title : '提示'
		});
		return false;
	}
	return true;
}
function afterSaleDetectProduct(id){ 
	window.location.href ='${pageContext.request.contextPath}/admin/afStock/afterSaleDetectProductInfo.jsp?id='+id;
}
function checkDate(startTime, endTime) {
	var r = new RegExp("^[1-2]\\d{3}-(0?[1-9]||1[0-2])-(0?[1-9]||[1-2][0-9]||3[0-1])$");
	 if(startTime.length!=0 && endTime.length!=0){
			if((startTime.length!=0 && startTime.length!=10) || !r.test(startTime)){
				$.messager.show({
					msg : "添加时间，请输入正确的格式！如：2011-08-10",
					title : '提示'
				});
				return false;
		    }

		    if((endTime.length!=0 && endTime.length!=10) || !r.test(endTime)){
		    	$.messager.show({
					msg : "添加时间，请输入正确的格式！如：2011-08-10",
					title : '提示'
				});
				return false;
			}
        }
	     var day = (getDateFromString(endTime)-getDateFromString(startTime))/(1000*60*60*24);
		 if(day<0){
			 $.messager.show({
					msg : "起始日期不能大于截止日期。\n请重新输入！",
					title : '提示'
				});
				return false;
		 }
		 if(day>60){
			 $.messager.show({
					msg : "两个日期之差最多为2个月。\n请重新输入！",
					title : '提示'
				});
				return false;
		 }
		 if(startTime.length!=0&&endTime.length==0 ){
			 $.messager.show({
					msg : "请输入截止日期",
					title : '提示'
				});
				return false;
		 }
	     if(startTime.length==0&&endTime.length!=0 ){
	    	 $.messager.show({
					msg : "请输入起始日期",
					title : '提示'
				});
			return false;
		 }
	     return true;
}
var myview = $.extend({},$.fn.datagrid.defaults.view,{
	onAfterRender:function(target){
		$.fn.datagrid.defaults.view.onAfterRender.call(this,target);
		var opts = $(target).datagrid('options');
		var vc = $(target).datagrid('getPanel').children('div.datagrid-view');
		vc.children('div.datagrid-empty').remove();
		if (!$(target).datagrid('getRows').length){
			var d = $('<div class="datagrid-empty"></div>').html(opts.emptyMsg || 'no records').appendTo(vc);
			d.css({
				position:'absolute',
				left:0,
				top:50,
				width:'100%',
				textAlign:'center'
			});
		}
	}
});
</script>
</head>
<body>
	<table id="detectProductsDatagrid"></table> 
	<div id="tb"  style="height: auto;display: none;">
		<input type="hidden" name="id" value="-1"/>
		<fieldset>
			<legend>筛选</legend>
			<table class="tableForm">
				<tr align="center" >
					<th>售后处理单号：</th>
					<td align="left">
						<textArea id="afterSaleDetectProductCode" name="afterSaleDetectProductCode" cols="18" rows="2" ></textArea>
					</td>
					<th>售后单号：</th>
					<td align="left">
						<textArea id="afterSaleOrderCode" name="afterSaleOrderCode" cols="18" rows="2" ></textArea>
					</td>
					<th>订单号：</th>
					<td align="left">
						<textArea id="orderCode" name="orderCode" cols="18" rows="2" ></textArea>
					</td>
				</tr>
				<tr>
					<th>商品编号：</th>
					<td align="left">
						<input id="productCode" name="productCode" style="width: 116px;"/>
					</td>
					<th>一检人：</th>
					<td align="left">
						<input id="detectProductUserName" name="detectProductUserName" style="width: 116px;"/>
					</td>
					<th>下一步操作：</th>
					<td align="left">
						<input id="nextStep" name="nextStep" style="width: 121px;"/>
					</td>
				</tr>
				<tr>
					<th>售后处理单状态：</th>
					<td align="left">
						<input id="detectProductStatus" name="detectProductStatus" style="width: 121px;"/>
					</td>
					<th>商品一级分类</th>
					<td align="left">
						<input id="parentId1" name="parentId1" style="width: 121px;"/>
					</td>
					<th>一检时间：</th>
					<td align="left"  colspan="3">
						<input id="startTime" name="startTime" style="width:121px" class="easyui-datebox"/>
						--
						<input id="endTime" name="endTime" style="width:121px" class="easyui-datebox"/>
					</td>
				</tr>
				<tr>
					<th>处理建议：</th>
					<td align="left">
						<input id="content" name="content" style="width: 121px;"/>
					</td>
					<th>手机号：</th>
					<td align="left">
						<input id="phone" name="phone" style="width: 116px;"/>
					</td>
					<th>订单签收时间：</th>
					<td align="left"  colspan="3">
						<input id="orderConfirmStartTime" name="orderConfirmStartTime" style="width:121px" class="easyui-datebox"/>
						--
						<input id="orderConfirmEndTime" name="orderConfirmStartTime" style="width:121px" class="easyui-datebox"/>
					</td>
					<th >售后地区</th>
					<td align="left">
						<input id="areaId" name="areaId" style="width: 121px" /></td>
				</tr>
				<tr>
					<td align="center"  colspan="6">
						<a class="easyui-linkbutton"  data-options="iconCls:'icon-search',plain:true" onclick="searchFun();" href="javascript:void(0);">售后处理单查询</a>
						&nbsp;
						<a class="editbutton" onclick="exportFun();" href="javascript:void(0);">导出Excel表格</a>
					</td>
				</tr>
			</table>
		</fieldset>
	</div>
</body>
</html>
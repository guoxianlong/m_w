<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core" %>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
function updateFun(data){
	var arr=data.split(',');
	$("#updCode").html(arr[1]);
	if(arr[2]!='null'){
		$("#updStatus").combobox('setValue', arr[2]);
	}else{
		$("#updStatus").combobox('setValue', "-1");
	}
	if(arr[3]!='null'){
		$("#updSize").val(arr[3]);
	}else{
		$("#updSize").val("");
	}
	if(arr[4]!='null'){
		$("#updWeight").val(arr[4]);
	}else{
		$("#updWeight").val("");
	}	
	if(arr[5]!='null'){
		$("#updMode").combobox('setValue', arr[5]);
	}else{
		$("#updMode").combobox('setValue', "-1");
	}
	$('#dialogDiv').dialog({    
	    title: '更新',    
	    width: 280,    
	    height: 200,    
	    closed: false,    
	    cache: false,    
	    modal: true,
	    buttons:[{
			text:'确定',
			handler:function(){
				if($("#updStatus").combobox("getValue")=="-1"){
					$.messager.show({
						title : '提示',
						msg : '请选择配送状态'
					});
					return;
				}	
				var reg =/^([1-9]+[0-9]*|0)(\.\d{1,2})?$/;
				var updSize=$.trim($("#updSize").val());
				if(updSize!=""){
					if(!reg.test(updSize)){
						$.messager.show({
							msg : '立方数必须为整数或者1到2位小数',
							title : '提示'
						});
						return;
					}
					updSize=parseFloat(updSize);
					if(updSize>10000){
						$.messager.show({
							msg : '立方数不可超过5位数',
							title : '提示'
						});
						return;
					}	
				}
				var updWeight=$.trim($("#updWeight").val());
				if(updWeight!=""){
					if(!reg.test(updWeight)){
						$.messager.show({
							msg : '立方数必须为整数或者1到2位小数',
							title : '提示'
						});
						return;
					}
					updWeight=parseFloat(updWeight);
					if(updWeight>10000){
						$.messager.show({
							msg : '立方数不可超过5位数',
							title : '提示'
						});
						return;
					}	
				}				
			
				if($("#updMode").combobox("getValue")=="-1"){
					$.messager.show({
						title : '提示',
						msg : '请选择配送方式'
					});
					return;
				}
				$.ajax({
					url : '${pageContext.request.contextPath}/TrunkLineController/modifyTrunkInfo.mmx',
					data : {'trunkOrderId':arr[0],
							'status' : $("#updStatus").combobox("getValue"),	
							'size': $("#updSize").val(),
							'weight': $("#updWeight").val(),
							'mode':$("#updMode").combobox("getValue")
					},
					type : 'post',
					dataType : 'json',
					success : function(data){
						if (data.success==true) {
							$.messager.show({
								title : '提示',
								msg : data.msg
							});
							$('#datagrid').datagrid("reload");
						} else {
							$.messager.show({
								title : '提示',
								msg : data.msg
							});
						}
						$('#dialogDiv').dialog('close');
					}
				})
			}
	    }]
	}); 		
}
$(function(){
	 Date.prototype.format = function(format) {
		    var o = {
		      "M+": this.getMonth() + 1, // month
		      "d+": this.getDate(), // day
		      "h+": this.getHours(), // hour
		      "m+": this.getMinutes(), // minute
		      "s+": this.getSeconds(), // second
		      "q+": Math.floor((this.getMonth() + 3) / 3), // quarter
		      "S": this.getMilliseconds()
		      // millisecond
		    }
		    if (/(y+)/.test(format))
		      format = format.replace(RegExp.$1, (this.getFullYear() + "")
		        .substr(4 - RegExp.$1.length));
		    for (var k in o)
		      if (new RegExp("(" + k + ")").test(format))
		        format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
		    return format;
	};
	
	function parseToDate(value) {  
	    if (value == null || value == '') {  
	        return undefined;   
	    }  
	  
	    var dt;  
	    if (value instanceof Date) {  
	        dt = value;  
	    }  
	    else {  
	        if (!isNaN(value)) {  
	            dt = new Date(value);  
	        }  
	        else if (value.indexOf('/Date') > -1) {  
	            value = value.replace(/\/Date(−?\d+)\//, '$1');  
	            dt = new Date();  
	            dt.setTime(value);  
	        } else if (value.indexOf('/') > -1) {  
	            dt = new Date(Date.parse(value.replace(/-/g, '/')));  
	        } else {  
	            dt = new Date(value);  
	        }  
	    }  
	    return dt;  
	}
	$('#datagrid').datagrid({
		url : '${pageContext.request.contextPath}/TrunkLineController/qryTrunkOrder.mmx',
		toolbar : '#toolbar',
	    idField : 'id',
	    fit : true,
	    fitColumns : true,
	    striped : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    pageSize : 20,
	    singleSelect: false,
	    selectOnCheck: false,
	    checkOnSelect: false,
	    pageList : [ 10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    onBeforeLoad:function(){
	    	if($("#searchFlag").val()=="0"){
	    		return false;
	    	}else{
	    		return true;
	    	}
	    },
	    columns:[[  
			{field:'id',align:'center',checkbox:true},
	        {field:'code',title:'干线单号',width:120,align:'center',
	        	formatter:function(val,rec){ 
	        		return "<a target='_blank' style='color:#0000ff' "
	        						+"href='${pageContext.request.contextPath}/admin/orderStock/trunkOrderDetail.jsp?mailingBatchId="
	        						+rec.mailingBatchId+"&code="+val+"'>"+val+"</a>";
	        	}					
	        },
	        {field:'orderCount',title:'订单数量',width:60,align:'center'},  
	        {field:'statusName',title:'配送状态',width:60,align:'center'},  
	        {field:'overTime',title:'是否超时',width:60,align:'center',
	        	formatter:function(val,rec){ 
	        		if(val=='1'){
	        			return "<font style='color:#ff0000'>是</font>";
	        		}else{
	        			return "否";
	        		}
	        	}	
	        }, 
	        {field:'trunkCorpName',title:'干线公司',width:60,align:'center'},
	        {field:'opUserName',title:'用户名',width:60,align:'center'},
	        {field:'stockAreaName',title:'发货仓',width:60,align:'center'},
	        {field:'deliverName',title:'目的地',width:60,align:'center'},  
	        {field:'receiveTime',title:'交接时间',width:80,align:'center',
	        	formatter:function(val,rec){ 
	        		if(val!=null&&val!=""){
	        			var dt = parseToDate(val);  
	        			return dt.format("yyyy-MM-dd hh:mm:ss");
	        		}else{
	        			return "";
	        		}
	        	}	
	        },
	        {field:'expectTime',title:'预计时间',width:80,align:'center',
	        	formatter:function(val,rec){ 
	        		if(val!=null&&val!=""){
	        			var dt = parseToDate(val);  
	        			return "<font style='color:#00ff00'>"+dt.format("yyyy-MM-dd hh:mm:ss")+"</font>";
	        		}else{
	        			return "";
	        		}
	        	}		
	        }, 
	        {field:'time',title:'时效H',width:50,align:'center'}, 
	        {field:'remainTime',title:'剩余时间',width:60,align:'center',
	        	formatter:function(val,rec){ 
	        		return  "<font style='color:#ff0000'>"+val+"</font>";
	        	}
	        },
	        {field:'nodeTime',title:'完成时间',width:80,align:'center',
	        	formatter:function(val,rec){ 
	        		if(rec.status==5){
		        		if(val!=null&&val!=""){
		        			var dt = parseToDate(val);  
		        			return dt.format("yyyy-MM-dd hh:mm:ss");
		        		}else{
		        			return "";
		        		}
	        		}
	        	}	
	        },
	        {field:'size',title:'体积m³',width:50,align:'center'},
	        {field:'weight',title:'重量kg',width:50,align:'center'},
	        {field:'modeName',title:'配送方式',width:60,align:'center'},
	        {field:'update',title:'更新',width:40,align:'center',
	        	formatter: function(value,rec,index){
	        		return '<a href="javascript:updateFun(\''+rec.id+','+rec.code+','+rec.status+','+rec.size+','+rec.weight+','+rec.mode+'\');">更新</a>';
				}	
	        },
	        {field:'view',title:'查看',width:40,align:'center',
	        	formatter: function(value,rec,index){
	        		return '<a href="${pageContext.request.contextPath}/admin/orderStock/trunkOrderInfo.jsp?trunkOrderId='+rec.id+'" target="_blank">查看</a>';
	        	}
	        }
	    ]]
	}); 
	
	$('#trunkCorpId').combobox({
      	url : '<%=request.getContextPath()%>/Combobox/getTrunkCorpInfo.mmx',
      	valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		onSelect : function(record){
			var id = record.id;
			if(record.text!='请选择'){
				$('#deliverAdminUser').combobox({
					url : '<%=request.getContextPath()%>/Combobox/getDeliverAdminUser.mmx?deliverId='+id,
					valueField:'id',
					textField:'text',
					width : 150,
					editable :false,
					disabled:false,
					onSelect : function(record){
						
					}
					
			    });
			}else{
				$('#deliverAdminUser').combobox({
					width : 150,
					disabled:true
			    });
			}
			
		}
    });
	
	$('#deliverAdminUser').combobox({
		valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		disabled:true
    });
	
	$('#stockArea').combobox({
		url : '<%=request.getContextPath()%>/Combobox/getBIStockArea.mmx',
		valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		onSelect : function(record){
			var id = record.id;
			if(record.text!='请选择'){
				$('#deliver').combobox({
					url : '<%=request.getContextPath()%>/Combobox/getDeliverByStockAreaId.mmx?stockAreaId='+id,
					valueField:'id',
					textField:'text',
					width : 150,
					editable :false,
					disabled:false
					
			    });
			}else{
				$('#deliver').combobox({
					width : 150,
					disabled:true
			    });
			}
		}
    });
	
	$('#deliver').combobox({
		valueField:'id',
		textField:'text',
		editable :false,
		width : 150,
		disabled:true
    });
	
	$('#status').combobox({
      	url : '<%=request.getContextPath()%>/Combobox/getTrunkOrderStatus.mmx',
      	valueField:'id',
		textField:'text'
    });
	
	$('#updStatus').combobox({
      	url : '<%=request.getContextPath()%>/Combobox/getTrunkOrderStatus.mmx',
      	valueField:'id',
		textField:'text'
    });
	
	$('#updMode').combobox({
      	url : '<%=request.getContextPath()%>/Combobox/getTrunkMode.mmx',
      	valueField:'id',
		textField:'text'
    });
	
});
function searchFun() {
	$("#searchFlag").val("1");
	var deliverAdminUser=$('#deliverAdminUser').combobox('getValue');
	if(deliverAdminUser==""){
		deliverAdminUser="-1";
	}
	var deliver = $('#deliver').combobox('getValue');
	if(deliver==""){
		deliver="-1";
	}	
	$('#datagrid').datagrid('load', {
		code: $.trim($('#code').val()),
		status:$('#status').combobox('getValue'),
		isTimeout:$('#isTimeout').combobox('getValue'),
		trunkCorpId:$('#trunkCorpId').combobox('getValue'),
		deliverAdminUser:deliverAdminUser,
		stockArea:$('#stockArea').combobox('getValue'),
		deliver:deliver,
		transDatetimeStart:$('#transDatetimeStart').datebox("getValue"),
		transDatetimeEnd:$('#transDatetimeEnd').datebox("getValue"),
		finDatetimeStart:$('#finDatetimeStart').datebox("getValue"),
		finDatetimeEnd:$('#finDatetimeEnd').datebox("getValue")
	});
}
function exportFun() {
	var deliverAdminUser=$('#deliverAdminUser').combobox('getValue');
	if(deliverAdminUser==""){
		deliverAdminUser="-1";
	}
	var deliver = $('#deliver').combobox('getValue');
	if(deliver==""){
		deliver="-1";
	}
	var checkedItems = $('#datagrid').datagrid('getChecked');
	if(checkedItems.length>0){
		$('#qryType').val("2");
		var ids = [];
		$.each(checkedItems, function(index, item){
			ids.push(item.id);
		});       
		$('#trunkOrderIdStr').val(ids.join(","));
	}else{
		$('#qryType').val("1");
		$('#code1').val($.trim($('#code').val()));
		$('#status1').val($('#status').combobox('getValue'));
		$('#isTimeout1').val($('#isTimeout').combobox('getValue'));
		$('#trunkCorpId1').val($('#trunkCorpId').combobox('getValue'));
		$('#deliverAdminUser1').val(deliverAdminUser);
		$('#stockArea1').val($('#stockArea').combobox('getValue'));
		$('#deliver1').val(deliver);
		$('#transDatetimeStart1').val($('#transDatetimeStart').datebox("getValue"));
		$('#transDatetimeEnd1').val($('#transDatetimeEnd').datebox("getValue"));
		$('#finDatetimeStart1').val($('#finDatetimeStart').datebox("getValue"));
		$('#finDatetimeEnd1').val($('#finDatetimeEnd').datebox("getValue"));
	}
	$("#qryForm").submit();
}
</script>
</head>
<body>
<input type="hidden" value="0" id="searchFlag">
<form id="qryForm" action="${pageContext.request.contextPath}/TrunkLineController/exportTrunkOrder.mmx" method="post">
	<input type="hidden" name="qryType" id="qryType">	
	<input type="hidden" name="trunkOrderIdStr" id="trunkOrderIdStr">	
	<input type="hidden" name="code1" id="code1">
	<input type="hidden" name="status1" id="status1">
	<input type="hidden" name="isTimeout1" id="isTimeout1">
	<input type="hidden" name="trunkCorpId1" id="trunkCorpId1">
	<input type="hidden" name="deliverAdminUser1" id="deliverAdminUser1">
	<input type="hidden" name="stockArea1" id="stockArea1">
	<input type="hidden" name="deliver1" id="deliver1">
	<input type="hidden" name="transDatetimeStart1" id="transDatetimeStart1">
	<input type="hidden" name="transDatetimeEnd1" id="transDatetimeEnd1">
	<input type="hidden" name="finDatetimeStart1" id="finDatetimeStart1">
	<input type="hidden" name="finDatetimeEnd1" id="finDatetimeEnd1">
</form>
<div id="toolbar" class="datagrid-toolbar" style="height: auto;">
			
			<fieldset>
			<table>
				<tr>
					<td>干线单号/订单号:</td>
					<td><input id='code' name='code' type="text"/>&nbsp;&nbsp;</td>
					<td>配送状态:</td>
					<td><input id='status' name='status'/></td>					
					<td>是否超时:</td>
					<td colspan="3">
						<select id="isTimeout" class="easyui-combobox">
							<option value="-1">请选择</option>
							<option value="1">是</option>
							<option value="0">否</option>
						</select>
					</td>	
				</tr>
			    <tr>
					<td>干线公司:</td>
					<td><input id='trunkCorpId' name='trunkCorpId'/>&nbsp;&nbsp;</td>			    	
					<td>用户名:</td>
					<td><input id='deliverAdminUser' name='deliverAdminUser'/></td>
					<td>发货仓:</td>
					<td><input id='stockArea' name='stockArea'/>&nbsp;&nbsp;</td>
					<td>目的地:</td>
					<td><input id='deliver' name='deliver'/></td>													    
			    </tr>
				<tr>
					<td>交接时间:</td>
					<td><input id="transDatetimeStart" name="transDatetimeStart" class="easyui-datebox" editable="false" style="width: 120px;" />-
						<input id="transDatetimeEnd" name="transDatetimeEnd" class="easyui-datebox" editable="false" style="width: 120px;" />&nbsp;&nbsp;</td>
					<td>完成时间:</td>
					<td><input id="finDatetimeStart" name="finDatetimeStart" class="easyui-datebox" editable="false" style="width: 120px;" />-
						<input id="finDatetimeEnd" name="finDatetimeEnd" class="easyui-datebox" editable="false" style="width: 120px;" /></td>
					<td colspan="4">&nbsp;&nbsp;<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查询</a></td>
				</tr>			    
				<tr>
					<td><a class="easyui-linkbutton" iconCls="icon-print" plain="true" onclick="exportFun();" href="javascript:void(0);">导出</a></td>
				</tr>
			</table>
			</fieldset>			
	</div>
	<table id="datagrid"></table> 
	<div id="messager" style="display:none;">Dialog Content.</div> 	
	<div style="display:none;">
	<div id="dialogDiv">
		<table>
			<tr>
				<td>干线单号:</td>
				<td id="updCode"></td>
			</tr>
			<tr>
				<td>配送状态:</td>
				<td><input id="updStatus" ></td>
			</tr>
			<tr>
				<td>立方数:</td>
				<td><input type="text" id='updSize' maxlength="10"/>m³</td>
			</tr>
			<tr>
				<td>重量:</td>
				<td><input type="text" id='updWeight' maxlength="10"/>kg</td>
			</tr>			
			<tr>
				<td>配送方式:</td>
				<td><input id="updMode"></td>
			</tr>
		</table>
	</div>  	
	</div>  	
</body>
</html>
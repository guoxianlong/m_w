<%@page import="mmb.rec.sys.easyui.Json"%>
<%@page import="mmb.rec.sys.easyui.EasyuiDataGridJson"%>

<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>

<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>

<script type="text/javascript" charset="UTF-8">
var datagrid;
$(function(){
	   datagrid = $('#datagrid').datagrid({
	    url:'${pageContext.request.contextPath}/admin/IMEI/queryCodeOrIMEI.mmx',
	    //queryParams:{ radio_type:radio,multiIMEICode:IMEICode},
        idField : 'id',
	    height:480,
	    fitColumns : true,
	    striped : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    rownumbers : true,
	    singleSelect : true,
	    pageSize:100,
	    pageList: [100,200],
	    columns:[[   
		       	{field:'code',title:'IMEI码',width:$(this).width() * 0.5,align:'center'},
       	      {field:'orderCode',title:'订单号',width:$(this).width() * 0.5,align:'center'}
		        ]],          		
		      
	   }); 
	
	});
function initForm() {
   $("#saveMultiIMEIForm textarea[id=multiIMEICode]").val("");
}

function saveIMEIFun() {
	 
	//判断单选按钮是否选择
	var radio=$('input[name="radio_type"]:checked').val();
	if(radio==null){
		alert("请选择导入数据类型!");
		return false;
	}
	var isValid = $("#saveMultiIMEIForm").form('validate');
	if (!isValid) {
		return false;
	}
	var IMEICode=$("#saveMultiIMEIForm textarea[id=multiIMEICode]").val();

	$('#datagrid').datagrid('reload', {
		radio_type:radio,
		multiIMEICode:IMEICode,
	});
	$('#datagrid').datagrid({
		 onBeforeLoad:function(){
			 document.getElementById("result").style.display='block';  
		 },
		   onLoadSuccess:function(data){
			   if(data.total==0){
			   document.getElementById("result").style.display='block'; 
			   }else{
				   document.getElementById("result").style.display='none';    
			   }
		    }
		
		});  
}
//$('#datagrid').datagrid('reload');    



</script>
</head>
<body > 

		<h3>售后订单与IMEI码互查询</h3>
		<br/>
		<fieldset>

			<form id="saveMultiIMEIForm">
				<table id="table" >
					<tr align="right" >
						<td>请选择导入数据类型：</td>
						<td align="left">
						<input id='radio_code' name='radio_type' type='radio' value='code'>订单	
						</td>
						<td align="left">
						<input id='radio_imei' name='radio_type' type='radio' value='imei' >IMEI码
						</td>
					</tr>
					
					<tr align="right">
						<td>数据导入：</td>
						<td align="left" colspan="3">
							<textarea cols="50" rows="4" id="multiIMEICode" name="multiIMEICode" class="easyui-validatebox" data-options="required:true"></textarea>
						</td>
					</tr>
					<tr align="right">
						<td align="right">
							<a class="easyui-linkbutton" data-options="iconCls:'icon-save',plain:false" onclick="saveIMEIFun();" href="javascript:void(0);">保存</a>
							</td>
						<td align="center">
						<a class="easyui-linkbutton" data-options="iconCls:'icon-remove',plain:false" onclick="initForm();" href="javascript:void(0);">清空</a>
						</td>
						
					</tr>
					<tr align="right">
					<td colspan="3" align="right">
						<font color='red'>提示:</font>1.选择查询条件为‘订单’,数据导入格式则为‘IMEI码’,选择查询条件为‘IMEI码’,数据导入格式则为订单。
						<td>
					</tr>
					<tr align="right">
					<td colspan="3" align="left">
						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2.支持导入多条数据,每条数据为一行,上限200行。
						</td>
				    </tr>
				</table>
			</form>
			<Div id='result'  style='display:none'><font color='red' >导入数据超过200条数量限制</font></Div>
		</fieldset>
		<table id="datagrid"  title="IMEI码与订单互查" ></table> 
		
</body>
</html>
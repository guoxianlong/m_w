<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/core"  prefix="c" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>货位指向动态列表</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">    
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<link href="${pageContext.request.contextPath}/css/global.css" rel="stylesheet" type="text/css">
</head>
<body>
	<form id="queryForm" action="" method="post"> 
		<fieldset style="width:1000px;">
			<legend>货位指向动态查询</legend>
			<table border="0" width="98%">	
				<tr>
					<td align="right" style="white-space:nowrap;">所属仓库：</td>
					<td>
	  	 				<select class="singleSelect" id="storage"  name="storage" >
	  	 					<option value="">不限</option>
      	   					<c:choose>
      	    				<c:when test="${fn:length(storageList)>0}">
      	    					<c:forEach var="item" items="${storageList}">
      	    					<option value="${item.id}">${item.wholeCode}</option>
      	    					</c:forEach>
      	    				</c:when>
      	    				<c:otherwise>
      	    	 				<option value="">无地区权限</option>
      	    				</c:otherwise>
      	    	 			</c:choose>
       	 				</select>					
					</td>
					<td align="right" style="white-space:nowrap;">退货上架单作业编号：</td>
					<td>
						<input type="text" class="ui-state-default" value="" id="operCode" name="operCode">
					</td>
					<td align="right" style="white-space:nowrap;">退货上架单状态：</td>
					<td>
						<select class="singleSelect" id="operStatus"  name="operStatus">
							<option value="" >不限</option>
							<option value="1" >未处理</option>
							<option value="2" >提交并确认</option>
							<option value="3" >交接阶段</option>
							<option value="7" >作业结束</option>
						</select>
						</select>
					</td>					
					<td align="right" style="white-space:nowrap;">源货位号：</td>
					<td>
						<input type="text" class="ui-state-default" value="" id="originCargo" name="originCargo">
					</td>						
				</tr>			
				<tr>				
					<td align="right" style="white-space:nowrap;">目的货位号：</td>
					<td>
						<input type="text" class="ui-state-default" value="" id="targetCargo" name="targetCargo">
					</td>
					<td align="right" style="white-space:nowrap;">退货上架单创建时间：</td>
					<td style="white-space:nowrap;">
						<input type="text" class="ui-state-default"  readonly name="createDateStart" id="createDateStart" size="10" value=""
	  	   					onClick="WdatePicker({maxDate:'#F{$dp.$D(\'createDateEnd\')}'});" />
	  	   					&nbsp;-&nbsp;
						<input type="text" class="ui-state-default"  readonly name="createDateEnd" id="createDateEnd" size="10" value=""
	  	   					onClick="WdatePicker({minDate:'#F{$dp.$D(\'createDateStart\')}'});" />
					</td>
					<td colspan="4">
						<input type="button" value="查询" id="queryBtn">
					</td>										
				</tr>					
			</table>
		</fieldset>
	</form>
	<div id="tempDiv"></div>
	<div id="toolBar">    
    <a href="#" class="easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="javascript:delVirtual()">删除已勾选的对象</a>    
	</div>    		
	<table id="datagrid"></table>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/jquery-1.7.1.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/ui/jquery-ui-1.8.17.custom.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/jquery/multiselect/jquery.multiselect.js"></script>
<link href="${pageContext.request.contextPath}/jquery/themes/ui-lightness/jquery-ui-1.8.17.custom.css"" rel="stylesheet" type="text/css" />
<link href="${pageContext.request.contextPath}/jquery/multiselect/jquery.multiselect.css"" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="${pageContext.request.contextPath}/js/My97DatePicker/WdatePicker.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/js/easyui/themes/icon.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/easyui/locale/easyui-lang-zh_CN.js" charset="utf-8"></script>
<style type="text/css">
select {
	width:100px;
}
.datagrid-header-rowxxxx {
  height: 25px;
}
</style>
<script type="text/javascript">
function selAll(obj){
	if($(obj).is(':checked')){
		$("[name='selBox']").attr("checked",'true');
	}else{
		$("[name='selBox']").removeAttr("checked");
	}
}
$(function($) {
	//单选select初始化
	$(".singleSelect").multiselect(
			{ 
				minWidth:120,
				selectedList:1,
				header:'请选择',
				multiple:false
			}
	);	
	$(".singleSelect").multiselect("uncheckAll");	
	//查询结果  title:'<div class="datagrid-header-check"><input type="checkbox">&nbsp;序号</div>出错'
	$('#datagrid').datagrid({
		width : 1200,
		border: true,
		fitColumns : true,
	    nowrap : false,
	    loadMsg : '正在努力为您加载..',
	    pagination : true,
	    singleSelect : false,
	    collapsible:true,
	    checkOnSelect:false,
	    pageSize : 10,
	    pageList : [10, 20, 30, 40, 50, 60, 70, 80, 90, 100 ],
	    toolbar: '#toolBar',
	    onBeforeLoad:function(param){
    		var panel = $(this).datagrid('getPanel');
    		var tr = panel.find('div.datagrid-header tr');
    		tr.each(function(){   
    			var td = $(this).children('td[field="virtualId"]');
    			td.children("div").height("25px");
    		});
	    },
	    columns:[[
	        {field:'virtualId',title:'<div><input type="checkbox" onclick="selAll(this)">&nbsp;序号</div>',width:40,align:'center',
	        	formatter:function(val,rec){ 
	        		return "<div><input name='selBox' type='checkbox' value='"+val+"'>&nbsp;"+rec.rownum+"</div>";
	        	}
	        },  
	        {field:'operCode',title:'退货上架单作业编号',width:100,align:'center'},      
	        {field:'storageCode',title:'仓库',width:60,align:'center'},
	        {field:'productCode',title:'产品编号',width:60,align:'center',
	        	formatter:function(val,rec){ 
	        		return "<a style='color:#0000ff' href='${pageContext.request.contextPath}/admin/fproduct.do?id="+rec.productId+"' target='_blank'>"+rec.productCode+"</a>";
	        	}
	        },
	        {field:'productName',title:'产品名称',width:75,align:'center'},
	        {field:'outCargoWholeCode',title:'源货位号',width:60,align:'center'},
	        {field:'inCargoWholeCode',title:'目的货位号',width:60,align:'center'},
	        {field:'tempItem',title:'上架量',width:30,align:'center',
	        	formatter:function(val,rec){ 
	        		return "1";
	        	}
	        },
	        {field:'stockCount',title:'目的货位库存量',width:60,align:'center'},
	        {field:'skuCount',title:'目的货位SKU数',width:60,align:'center'},
	        {field:'virtualStockCount',title:'目的货位库存预存放总量',width:60,align:'center'},
	        {field:'virtualSkuCount',title:'目的货位SKU预存放总数',width:60,align:'center'},
	        {field:'createDatetime',title:'退货上架单创建时间',width:70,align:'center'},
	        {field:'statusName',title:'退货上架单状态',width:60,align:'center'}
	    ]]
	});	
	$('#queryBtn').click(
			function(){
				var storage="";
				if($("#storage").multiselect("getChecked").length>0){
					storage=$("#storage").multiselect("getChecked")[0].value;
				}
				var operStatus="";
				if($("#operStatus").multiselect("getChecked").length>0){
					operStatus=$("#operStatus").multiselect("getChecked")[0].value;
					if(operStatus==3){
						operStatus="3,4,5,6";
					}else if(operStatus==7){
						operStatus="7,8,9";
					}
				}
				$('#datagrid').datagrid({
					url : '${pageContext.request.contextPath}/returnedProductDirect/queryVirtual.mmx',
					queryParams: {
						storage: storage,
						operCode:$('#operCode').val(),
						operStatus:operStatus,
						originCargo:$('#originCargo').val(),
						targetCargo:$('#targetCargo').val(),
						createDateStart:$('#createDateStart').val(),
						createDateEnd:$('#createDateEnd').val()
					}
				});
			}		
	);
});
function delVirtual(){
	var arr="";
	$("input[name='selBox']").each(function() {
        if ($(this).is(":checked")) {
        	arr+=","+$(this).val();
        }
	});
	if(arr.length==0||arr.length==1){
		alert("请先选择要删除对象！");
		return;
	}
	if(confirm("确定删除选中对象？")){
		$.ajax({
			type: "get",
			url: "${pageContext.request.contextPath}/returnedProductDirect/deleteVirtual.mmx?virtualId="+arr.substr(1),
			cache: false,
			dataType: "json",
			error: function(){
				alert("网络错误!");
			},
			success: function(items){
				if(items.success){
					alert(items.msg);
					$('#datagrid').datagrid("reload");
				}else{
					alert(items.msg);
				}
			}
		});
	}
}
</script>    
</body>
</html>
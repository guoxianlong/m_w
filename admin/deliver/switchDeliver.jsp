<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib prefix="mmb" uri="http://www.maimaibao.com/core"%>
<%@ taglib prefix="c"  uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<title>快递公司切换功能</title>
<jsp:include page="../rec/inc/easyui-base.jsp"></jsp:include>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/timeAndOther.js"></script>

<script type="text/javascript" charset="UTF-8">


	var datagrid;
	var num = 1;
	$(function(){
		datagrid = $('#datagrid').datagrid(
				{	url : '${pageContext.request.contextPath}/deliversController/getDeliverSwitch.mmx',
					toolbar : '#tb',
					idField : 'id',
					width : 800,
					height : 350,
					fit : true,
					fitColumns : true,
					striped : true,
					nowrap : false,
					loadMsg : '正在努力为您加载..',
					pagination : true,
					rownumbers : true,
					singleSelect : true,
					pageSize : 10,
					pageList : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10],
					frozenColumns : [ [ {
						field : 'id',
						title : 'ID',
						width : 5,
						hidden : true
					}, ] ],
					columns : [ [ {
						field : 'stockArea',
						title : '仓库',
						width : 10,
						align : 'center',
						sortable : true
					}, {
						field : 'orderCode',
						title : '订单号',
						width : 10,
						align : 'center',
						sortable : true
					}, {
						field : 'createDateTime',
						title : '创建时间',
						width : 10,
						align : 'center',
						sortable : true,
						formatter : function(value, rowData, rowIndex) {
							if (value != null) {
								return value.substring(0, 19);
							}
						}
					}, {
						field : 'originDeliverName',
						title : '原快递公司',
						width : 10,
						align : 'center',
						sortable : true
					}, {
						field : 'originPackageCode',
						title : '原运单号',
						width : 10,
						align : 'center',
						sortable : true
					}, {
						field : 'deliverName',
						title : '快递公司',
						width : 10,
						align : 'center',
						sortable : true
					}, {
						field : 'packageCode',
						title : '运单号',
						align : 'center',
						width : 10,
						sortable : true
					},{
						field : 'modifyDatetime',
						title : '修改时间',
						align : 'center',
						width : 10,
						sortable : true,
						formatter : function(value, rowData, rowIndex) {
							if (value != null) {
								return value.substring(0, 19);
							}
						}
					},{
						field : 'remark',
						title : '备注',
						width : 10,
						align : 'center',
						sortable : true,
					} ] ]
				});
		
		 $("#p").hide();
		 
		 $('#deliver' + num).combobox({
			 	url:'${pageContext.request.contextPath}/Combobox/getDeliverAll.mmx',
				valueField : 'id',
				textField : 'text'
		});
		 
		 var curr_time = new Date();
		 var strDate = curr_time.getFullYear()+"-";
		 strDate += curr_time.getMonth()+1+"-";
		 strDate += curr_time.getDate();
		 $("#endDatetime").datebox("setValue", strDate); 
		 
	});
	
	function searchFun() {
		var stockArea = $("#stockArea").combobox("getText");
		var originDeliverName = $("#originDeliverName").combobox("getText");
		var orderCode = $('#tb input[name=orderCode]').val();
		var beginDatetime = $('#tb input[name=beginDatetime]').val();
		var endDatetime = $('#tb input[name=endDatetime]').val()
		if(stockArea=='-1' && originDeliverName=="请选择" && orderCode=='' && beginDatetime=='' && endDatetime==''){
			$.messager.show({
				msg : '请输入一个查询条件',
				title : '提示'
			});
			return;
		}

		if (beginDatetime != '' && endDatetime == '') {
			$.messager.show({
				msg : '请选择结束日期',
				title : '提示'
			});
			return;
		}
		if (beginDatetime == '' && endDatetime != '') {
			$.messager.show({
				msg : '请选择开始日期',
				title : '提示'
			});
			return;
		}
		if (beginDatetime != '' && endDatetime != '') {
			if(!validateDate(endDatetime, beginDatetime)){		
				$.messager.show({
					msg : '结束时间必须大于开始时间',
					title : '提示'
				});
				return;
			}
		}
		
		datagrid.datagrid('load', {		
			stockArea : stockArea,
			originDeliverName : originDeliverName,
			orderCode : orderCode,
			beginDatetime : beginDatetime,
			endDatetime : endDatetime
		});
	}
	
	function clearFun(){
		$('#orderCode').attr("value",'');
		$('#stockArea').combobox({    
			url:'${pageContext.request.contextPath}/Combobox/getStockoutAvailableArea2.mmx',  
			width:100,
		    valueField:'id',    
		    textField:'text',
		    value:'无锡'
		    
		}); 
		$('#originDeliverName').combobox({    
			url:'${pageContext.request.contextPath}/Combobox/getDeliverAll.mmx',
			width:130,
		    valueField:'id',    
		    textField:'text'   
		}); 
		
		$('#beginDatetime').datebox('setValue', '');
		var curr_time = new Date();
		 var strDate = curr_time.getFullYear()+"-";
		 strDate += curr_time.getMonth()+1+"-";
		 strDate += curr_time.getDate();
		 $("#endDatetime").datebox("setValue", strDate);
		
		datagrid.datagrid('load', {});
	}
	
	function show() {
		if($("#p").is(":hidden")){
		       $("#p").slideDown();
		}else{
		      $("#p").slideUp(); 
		}
	}
	
	function getDeliverInfo(OrderCode,packageCode,name){
		if(OrderCode==''){
			$.messager.show({
				msg : '请输入订单号',
				title : '提示'
			});
			
			$("#" + packageCode).val('');
			$("#" + name).val('');
			return;
		}
		$.ajax({
			url : '${pageContext.request.contextPath}/deliversController/getDeliver.mmx',
			data : {
				orderCode : OrderCode
			},
			cache : false,
			type:"post",
			dataType : "json",
			success : function(r) {
				if (r.success) {
					$("#" + packageCode).val(r.obj.package_code);
					$("#" + name).val(r.obj.name);
					
				} else {
					$.messager.show({
						msg : '订单不存在',
						title : '提示'
					});
					
				}
			}
		});
	}
	
	function addFun(){
		
		$.messager.confirm('确认','您确认想要修改吗？',function(r){    
		    if (r){    
		    	$('#ff').form('submit', {    
				    url:'${pageContext.request.contextPath}/deliversController/addDeliverSwitch.mmx',  
				    onSubmit: function(){
				    	//加载操作进度条
				    	$("<div class=\"datagrid-mask\"></div>").css({ display: "block", width: "100%", height: $(window).height() }).appendTo("body"); 
				        $("<div class=\"datagrid-mask-msg\"></div>").html("正在操作中，请稍候。。。").appendTo("body").css({ display: "block", left: ($(document.body).outerWidth(true) - 190) / 2, top: ($(window).height() - 45) / 2 });  
				    },    
				    success:function(obj){
				    	//取消操作进度条
				    	$(".datagrid-mask").remove(); 
				        $(".datagrid-mask-msg").remove();  
				    	var r = jQuery.parseJSON(obj);
				    	//console.info(r);
				    	$.messager.show({
							msg : r.msg ,
							title : '提示'
						});
			    		$.each(r.obj, function(i, n){
			    			console.info(n);
			    			$("#tRow" + n).find("td").eq(0).find("input").attr("disabled",true);
			    			$("#tRow" + n).find("td").eq(1).find("input").attr("disabled",true);
			    			$("#deliver" + n).combobox('disable'); 
			    			$("#tRow" + n).find("td").eq(5).find("input").attr("disabled",true);
			    		});
				    }    
				});     
		    }    
		});
	}
	
	function addLineFun(){
		if(num=='10'){
			$.messager.show({
				msg : '一次最多可以添加10条' ,
				title : '提示'
			});
			
			return;
		}
		num++; 
		
		//克隆 tr 并重新给定ID,装到table
		$("#tRow1").clone().attr("id", "tRow" + num).appendTo("#tt"); 
		$("#tRow" + num).find("td").eq(0).find("input").removeAttr("disabled");//启动序号可用
		$("#tRow" + num).find("td").eq(1).find("input").removeAttr("disabled");//启动orderCode可用
		$("#tRow" + num).find("td").eq(5).find("input").removeAttr("disabled");//启动remark可用

		//给序号增加id
		$("#tt").find("tr").last().find("td").eq(0).find("input").attr("id", "no" + num);
		$("#tt").find("tr").last().find("td").eq(0).find("input").val(num)
		//console.info($("#tt").find("tr").last().find("td").eq(0).find("input").val(num));
		
		//给运单号增加id
		$("#tt").find("tr").last().find("td").eq(1).find("input").attr("id", "orderCode" + num);
		
		//给运单号增加id
		$("#tt").find("tr").last().find("td").eq(2).find("input").attr("id", "packageCode" + num);
		
		//给快递公司增加id
		$("#tt").find("tr").last().find("td").eq(3).find("input").attr("id", "name" + num);
		
		//给commbox增加id
		$("#tt").find("tr").last().find("td").eq(4).find("input").attr("id", "deliver" + num);
		$("#tt").find("tr").last().find("td").eq(4).find("span").remove();
		
		$('#deliver' + num).combobox({
		 	url:'${pageContext.request.contextPath}/Combobox/getDeliverAll.mmx',
			valueField : 'id',
			textField : 'text'
		});
		$('#deliver' + num).combobox('enable');
		
		$("#tt").find("tr").last().find("td").eq(0).find("a").html(num);//设置行号
		$("#tt").find("tr").last().find("td").eq(1).find("input").val("");//清除订单号
		$("#tt").find("tr").last().find("td").eq(2).find("input").val("");//清除运单号
		$("#tt").find("tr").last().find("td").eq(3).find("input").val("");//清除快递公司
		$("#tt").find("tr").last().find("td").eq(5).find("input").val("");//清除备注
		
	}
</script>

</head>
<body>
	<div id="tb"style="height: auto;">
		<fieldset>
			<legend><h2>快递公司切换功能</h2></legend>
			<table class="tableForm"  border="0">
				<tr align="center" >
					<th >仓库</th>
					<td align="left">
						<input id="stockArea" class="easyui-combobox" name="stockArea" data-options="valueField:'id',textField:'text',value:'无锡',width:'100',url:'${pageContext.request.contextPath}/Combobox/getStockoutAvailableArea2.mmx'" />
					</td>
					<th>订单号</th>
					<td>
						<input id="orderCode" name="orderCode"/>
					</td>
					<th >原快递公司</th>
					<td align="left">
						<input id="originDeliverName" class="easyui-combobox" name="originDeliverName" data-options="valueField:'id',textField:'text',width:'130',url:'${pageContext.request.contextPath}/Combobox/getDeliverAll.mmx'" />
					</td>
					<th >订单创建时间</th>
					<td align="left">
						<input id="beginDatetime" name="beginDatetime" class="easyui-datebox" />&nbsp-
						<input id="endDatetime" name="endDatetime" class="easyui-datebox" /></td>
					</td>
					<th ></th>
					<td align="left">
						<a class="easyui-linkbutton" iconCls="icon-search" plain="true" onclick="searchFun();" href="javascript:void(0);">查找</a>
						<a class="easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="show();" href="javascript:void(0);">编辑</a>
						<a class="easyui-linkbutton" iconCls="icon-cancel" plain="true" onclick="clearFun();" href="javascript:void(0);">清空</a>
					</td>					
				</tr>
				<tr align="center"> 
					<th></th>
					<td></td>
					<th></th>
					<td></td>
					<th></th>
					<td></td>
					<th></th>
					<td></td>
					<td></td>
				</tr>
			</table>
			</fieldset>
			
			<div id="p" style="padding:0px;"> 
			<fieldset>
			<legend><h2>编辑区域</h2></legend>  
				<a id="btn1" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="addLineFun();">追加行</a>
    			<a id="btn2" class="easyui-linkbutton" iconCls="icon-ok" plain="true" onclick="addFun();">修改</a> 
    			<form id="ff" method="post">
	    			<table id ="tt" cellpadding="3" cellspacing="1" border="1" style="border-collapse:collapse;" width="100%">
	    				<tr bgcolor="#CCCCCC">
	    					<td style="text-align: center;width: 3%">序号</td>
	    					<td style="text-align: center;width: 15%">订单号</td>
	    					<td style="text-align: center;width: 20%">运单号</td>
	    					<td style="text-align: center;width: 20%">快递公司</td>
	    					<td style="text-align: center;width: 18%">变更快递公司</td>
	    					<td style="text-align: center;width: 20%">备注</td>
	    				</tr>
	    				<tr id="tRow1">
	    					<td style="text-align: center;"><a id="no">1</a><input type="hidden" id="no1" name="nos" value="1"/></td>
	    					<td style="text-align: center;"><input id="orderCode1" name="orderCodes" maxlength="15" type="text" value="" onblur="getDeliverInfo($(this).val(),$(this).parent('td').next('td').find('input').attr('id'),$(this).parent('td').next('td').next('td').find('input').attr('id'));"/></td>
	    					<td style="text-align: center;"><input id="packageCode1" name='packageCode1' disabled="disabled" ></input></td></td>
	    					<td style="text-align: center;"><input id ="name1" name='name1' disabled="disabled"></input></td>
	    					<td style="text-align: center;"><input id="deliver1" name="delivers"/></td>
	    					<td style="text-align: center;"><input id="remarks1" name="remarks" maxlength="30" type="text" value="" /></td>
	    				</tr>
	    			</table> 
    			</form>
    			</fieldset>
			</div> 
		
	</div>
	<table id="datagrid"></table>
	
</body>
</html>
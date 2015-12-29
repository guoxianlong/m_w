<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String date=request.getAttribute("date").toString();
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>每小时贴单量统计</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<body>
<div id="tb" style="padding:3px;height: auto;">
	<fieldset>
		<legend>每小时贴单量统计</legend>
		<span style="font-size: 12px;">贴单日期：</span>
		<input name="date" id="date" type="text" class="easyui-datebox" editable="false">
		<span style="font-size: 12px;">库地区：</span> 
		<input type="text" class="easyui-combobox" editable="false" id="area" name="area" style="width: 88px;">
		
		<a href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true" id="query">查询</a>
		<input type="hidden" id="isExport" name="isExport" value="0"/>
	</fieldset>
</div>
<table id="groupStatList" title="每小时贴单量统计列表" class="easyui-datagrid" style="width:'auto';height:'auto'"
        singleSelect="true" fitColumns="true" iconCls="icon-ok">
</table>
</body>
<script type="text/javascript">
	$(function(){
		$("#date").datebox('setValue',"<%=date%>");
		$("#area").combobox({
			url:'<%=request.getContextPath()%>/sortingGroupStatController/querySelectStoreArea.mmx',
			valueField:'id',
			textField:'text'
		});
		$.ajax({
			url:'<%=request.getContextPath()%>/sortingGroupStatController/auditPackageStatByHour.mmx',
			cache:false,
			dataType:'text',
			type:'post',
			data:{date:$("#date").datebox('getValue')},
			success:function(dd){
				var result = eval('('+dd+')');
				if(result['result']=='failure'){
					$.messager.show({
						title:'提示',
						msg:result['tip'],
						timeout:3000,
						showType:'slide'
					});
				}else{
					var row = result['rows'];
//	 				var rr = eval('('+row+')');
					var column = result['columns'];
//	 				var cc = eval('('+column+')');
					$("#groupStatList").datagrid({
						columns:column
					});
					$("#groupStatList").datagrid('loadData',row);
				}
			}
		});
		// 条件查询
		$("#query").click(function(){
			$.ajax({
				url:'<%=request.getContextPath()%>/sortingGroupStatController/auditPackageStatByHour.mmx',
				cache:false,
				dataType:'text',
				type:'post',
				data:{
					date:$("#date").datebox('getValue'),
					area:$("#area").combobox('getValue')
				},
				success:function(dd){
					var result = eval('('+dd+')');
					if(result['result']=='failure'){
						$.messager.show({
							title:'提示',
							msg:result['tip'],
							timeout:3000,
							showType:'slide'
						});
					}else{
						var row = result['rows'];
//		 				var rr = eval('('+row+')');
						var column = result['columns'];
//		 				var cc = eval('('+column+')');
						$("#groupStatList").datagrid({
							columns:column
						});
						$("#groupStatList").datagrid('loadData',row);
					}
				}
			});
		});
	});
</script>
</html>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
int month=Integer.parseInt(request.getAttribute("month").toString());
int thisYear=Integer.parseInt(request.getAttribute("year").toString());
%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>每日发货贴单量统计</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<body>

<form id="form" action="" method="post">
<div id="tb" style="padding:3px;height: auto;">
	<fieldset>
		<legend>每日发货贴单量统计</legend>
		<span style="font-size: 12px;">贴单月份：</span>
		<input type="text" class="easyui-combobox" editable="false" id="year" name="year" style="width: 88px;">
		<span style="font-size: 12px;">年</span> 
		<input type="text" class="easyui-combobox" editable="false" id="month" name="month" style="width: 88px;">
		<span style="font-size: 12px;">月</span> 
		<span style="font-size: 12px;">库地区：</span> 
		<input type="text" class="easyui-combobox" editable="false" id="area" name="area" style="width: 88px;">
		
		<a href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true" id="query">查询</a>
		<a href="#" class="easyui-linkbutton" iconCls="icon-back" plain="true" id="export">导出excel文件</a>
		<input type="hidden" id="isExport" name="isExport" value="0"/>
	</fieldset>
</div>
</form>
<table id="auditPackageStatList" title="每日发货贴单量统计列表" class="easyui-datagrid" style="width:'auto';height:'auto'"
        singleSelect="true" fitColumns="true" iconCls="icon-ok">
</table>
<script type="text/javascript">
	$(function(){
		var years = "[{id:'<%=thisYear-1%>',text:'<%=thisYear-1%>'},{id:'<%=thisYear%>',text:<%=thisYear%>,selected:'true'}]";
		var year = eval('('+years+')');
		var months = "[";
		for(var i=1;i<=12;i++){
			if(i=="<%=month%>"){
				months+="{id:'"+i+"',text:'"+i+"',selected:'true'},";
			}else{
				months+="{id:'"+i+"',text:'"+i+"'},";
			}
		}
		months = months.substring(0,months.length-1);
		months+="]";
		var month = eval('('+months+')');
		$("#area").combobox({
			url:'<%=request.getContextPath()%>/sortingGroupStatController/querySelectStoreArea.mmx',
			valueField:'id',
			textField:'text'
		});
		$("#year").combobox({
			valueField:'id',
			textField:'text',
			data:year
		});
		$("#month").combobox({
			valueField:'id',
			textField:'text',
			data:month
		});
		$.ajax({
			url:'<%=request.getContextPath()%>/sortingGroupStatController/auditPackageStatByDate.mmx',
			cache:false,
			dataType:'text',
			type:'post',
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
	// 				var rr = eval('('+row+')');
					var column = result['columns'];
	// 				var cc = eval('('+column+')');
					$("#auditPackageStatList").datagrid({
						columns:column
					});
					$("#auditPackageStatList").datagrid('loadData',row);
				}
			}
		});
		// 条件查询
		$("#query").click(function(){
			$.ajax({
				url:'<%=request.getContextPath()%>/sortingGroupStatController/auditPackageStatByDate.mmx',
				cache:false,
				dataType:'text',
				type:'post',
				data:{
					year:$("#year").combobox('getValue'),
					month:$("#month").combobox('getValue'),
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
	//	 				var rr = eval('('+row+')');
						var column = result['columns'];
	//	 				var cc = eval('('+column+')');
						$("#auditPackageStatList").datagrid({
							columns:column
						});
						$("#auditPackageStatList").datagrid('loadData',row);
					}
				}
			});
		});
		//导出
		$("#export").click(function(){
			var params = "year="+$("#year").combobox('getValue')
				+"&month="+$("#month").combobox('getValue')
				+"&area="+$("#area").combobox('getValue')+"&isExport=1"
			window.location.href="<%=request.getContextPath()%>/sortingGroupStatController/auditPackageStatByDate.mmx?"+params;
		});
	});
</script>
</body>
</html>
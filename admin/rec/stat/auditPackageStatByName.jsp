<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
String userName=request.getParameter("userName")==null?"":request.getParameter("userName");
userName = new String(userName.getBytes("ISO8859-1"),"UTF-8");
String dateStart=request.getParameter("dateStart")==null?"":request.getParameter("dateStart");
String dateEnd=request.getParameter("dateEnd")==null?"":request.getParameter("dateEnd");
String status=request.getParameter("status")==null?"":request.getParameter("status");
%>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>个人贴单量统计</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
</head>
<body>

<div id="tb" style="padding:3px;height: auto;">
	<fieldset>
		<legend>个人贴单量统计</legend>
		<span style="font-size: 12px;">贴单人：</span>
		<input type="text" name="userName" value="<%=userName %>" id="userName" maxlength="45"/>
		<span style="font-size: 12px;">贴单日期：</span>
		<input name="dateStart" id="dateStart" value="<%=dateStart %>" type="text" class="easyui-datebox" editable="false">
		<span style="font-size: 12px;">至</span> 
		<input name="dateEnd" id="dateEnd" type="text" value="<%=dateEnd %>" class="easyui-datebox" editable="false">
		<span style="font-size: 12px;">库地区：</span> 
		<input type="text" class="easyui-combobox" editable="false" id="area" name="area" style="width: 88px;">
		
		<a href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true" id="query">查询</a>
	</fieldset>
</div>

<table id="groupStatList" title="个人贴单量统计列表" class="easyui-datagrid" style="width:'auto';height:'auto'"
        singleSelect="true" fitColumns="true" showFooter="false" iconCls="icon-ok">
</table>
</body>
<script type="text/javascript">
	$(function(){
		$("#area").combobox({
			url:'<%=request.getContextPath()%>/sortingGroupStatController/querySelectStoreArea.mmx',
			valueField:'id',
			textField:'text'
		});
		// 条件查询
		$("#query").click(function(){
			var userName = $.trim($("#userName").val());
			var dateStart = $.trim($("#dateStart").datebox('getValue')); 
			var dateEnd = $.trim($("#dateEnd").datebox('getValue'));
			if(userName==""){
				$.messager.show({
					title:'提示',
					msg:'请输入分播人姓名！',
					timeout:3000,
					showType:'slide'
				});
				return false;
			}
			if(dateStart==""&&dateEnd!=""){
				$.messager.show({
					title:'提示',
					msg:'分播日期的起始日期不能为空！',
					timeout:3000,
					showType:'slide'
				});
				return false;
			}
			if(dateStart!=""&&dateEnd==""){
				$.messager.show({
					title:'提示',
					msg:'分播日期的截止日期不能为空！',
					timeout:3000,
					showType:'slide'
				});
				return false;
			}
			$.ajax({
				url:'<%=request.getContextPath()%>/sortingGroupStatController/auditPackageStatByName.mmx',
				cache:false,
				dataType:'text',
				type:'post',
				data:{
					userName:userName,
					dateStart:dateStart,
					dateEnd:dateEnd,
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
		if("<%=status%>"=="1"){
			$("#query").trigger('click');
		}
	});
</script>
</html>
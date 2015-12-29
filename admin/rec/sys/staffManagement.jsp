<%@page import="adultadmin.bean.PagingBean"%>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.UserGroupBean" %>
<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
%>

<html>
<head>
<title>物流员工管理</title>
<jsp:include page="../inc/easyui.jsp"></jsp:include>
<style>
td{
	font-family: 微软雅黑; 
	font-size: 13px; 
	font-weight: normal; 
	font-style: normal; 
	text-decoration: none; 
	color: #333333;
 }
</style>
</head>
<body>
	<table width="100%" >
		<tr>
			<td width="200px">
				<ul id="ul"></ul>
				<%if(group.isFlag(417)){ %>
				<a href="<%=request.getContextPath()%>/CargoController/addStaffJsp.mmx">添加员工档案</a>
					<br/>
				<%} %>
				<a href="<%=request.getContextPath()%>/admin/rec/sys/staffManagement.jsp">全部档案列表</a>
				<br/>
				<%if(group.isFlag(416)){ %>
					<a href="<%=request.getContextPath()%>/admin/rec/sys/deptManagement.jsp">维护组织结构</a>
					<br/>
					<a href="<%=request.getContextPath()%>/CargoController/departmentAreaStockType.mmx">部门地区库类型</a>
				<%} %>
			</td>
			<td valign="top" align="center">
				<table width="100%">
					<tr>
						<td style="font-family:微软雅黑;font-size:20px;font-weight:bold;font-style:normal;text-decoration:none;color:#333333;">
							<table width="100%">
								<tr>
									<td width="30%" align="right">
										员工档案列表
									</td>
									<td width="10%"></td>
									<td align="left">
										<input name="kw" id="kw" type="text" value="关键字" style="color:#cccccc;" 
										onfocus="if(this.value=='关键字'){this.value='';this.style.color='#000000';}"
										onblur="if(this.value==''){this.value='关键字';this.style.color='#cccccc';}"/>&nbsp;&nbsp;
										<input name="condition" style="width:80px;" class="easyui-combobox" editable="false" id="condition">&nbsp;&nbsp;
										<a href="#" class="easyui-linkbutton" iconCls="icon-search" plain="true" id="querystaff">查找</a>
									</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td><table id="stafflist"></table></td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
</body>
<script type="text/javascript">
$.messager.defaults = { ok: "确认", cancel: "取消" };
	$(function(){
		//一开始就加载
		$("#stafflist").datagrid({
			title:"员工档案列表",
			iconCls:'icon-ok',
			width:'100%',
			height:'auto',
			fitColumns:true,
			pageNumber:1,
			pageSize:20,
			pageList:[5,10,15,20],
			url:'<%=request.getContextPath()%>/CargoController/queryStaffManagement.mmx',
				showFooter:true,
				striped:true,
				collapsible:true,
				loadMsg:'数据加载中...',
				rownumbers:true,
				singleSelect:true,//只选择一行后变色
				pagination:true,
				fitColumns:true,
				columns:[[
				        {field:'cargo_staff_name',title:'姓名',width:100,align:'center'},
				        {field:'cargo_staff_username',title:'后台账号',width:120,align:'center'},
				        {field:'cargo_staff_code',title:'员工编号',width:150,align:'center'},
				        {field:'cargo_staff_createdatetime',title:'创建时间',width:120,align:'center'},
				        {field:'cargo_staff_detpname',title:'归属部门',width:220,align:'center'},
				        {field:'cargo_staff_phone',title:'电话',width:80,align:'center'},
				        {field:'cargo_staff_control',title:'操作',align:'center'}
				        ]]
		});
		$("#condition").combobox({
			valueField:'id',
			textField:'text',
			data:[{id:'',text:'指定条件',selected:'true'},
			      {id:'name',text:'姓名'},
			      {id:'user_name',text:'后台帐号'},
			      {id:'code',text:'员工编号'},
			      {id:'create_datetime',text:'加入日期'},
			      {id:'phone',text:'电话'}
			      ]
		});
		$.ajax({
			url:'<%=request.getContextPath()%>/CargoController/staffManagement.mmx',
			cache:false,
			data:{address:'queryStaffManagement'},
			dataType:'text',
			success:function(result){
				var re = eval('('+result+')');
				if(re['result']=="failure"){
					$.messager.show({
						title:'结果提示',
						msg:re['tip'],
						showType:'slide'
					});
					return;
				}
				if(re['result']=="success"){
					$("#ul").tree({
						data:re['tip']
					});
				}
			}
		});
		$("#ul").tree({
			lines:true,
			onClick:function(node){
// 				alert(node.attributes.url);
				$("#stafflist").datagrid({
					url:node.attributes.url
				});
				displayMsg();
			}
		});
		displayMsg();
		$("#querystaff").click(function(){
			var kw = $.trim($("#kw").val());
			var condition = $.trim($("#condition").combobox("getValue"));
			if(kw=="关键字"||condition==""){
				$.messager.show({
					title:'提示',
					msg:'请指定查询条件',
					timeout:3000,
					showType:'slide'
				});
				return false;
			}
			$("#stafflist").datagrid({
				url:'<%=request.getContextPath()%>/CargoController/queryStaffManagement.mmx',
				queryParams:{
					kw:kw,
					condition:condition
				}
			});
			displayMsg();
		});
	});
	function displayMsg(){
		var p = $("#stafflist").datagrid('getPager');
		p.pagination({
			displayMsg:'当前显示 {from} - {to} 条记录   共 {total} 条记录',
			onBeforeRefresh:function(){
				$(this).pagination('loading');
				$(this).pagination('loaded');
			}
		});
	}
	//delStaff 删除
	function deleteStaff(id){
		$.messager.confirm('询问','确认该员工【已离职】并【删除】吗？',function(boo){
			if(boo){
				//选择true
				$.ajax({
					url:'<%=request.getContextPath()%>/CargoController/delStaff.mmx',
					data:{staffId:id},
					cache:false,
					dataType:'text',
					success:function(result){
						var re = eval('('+result+')');
						if(re['result']=="success"){
							datagrid = $("#stafflist").datagrid('reload');
							displayMsg();
						}
						$.messager.show({
							title:'结果提示',
							msg:re['tip'],
							showType:'slide'
						});
					}
				});
			}
		});
	}
	//恢复
	function recoverStaff(id){
// 		$.messager.confirm('询问','确认该员工【已离职】并【删除】吗？',function(boo){
// 			if(boo){
				//选择true
		$.ajax({
			url:'<%=request.getContextPath()%>/CargoController/recoverStaff.mmx',
			data:{id:id},
			cache:false,
			dataType:'text',
			success:function(result){
				var re = eval('('+result+')');
				if(re['result']=="success"){
					datagrid = $("#stafflist").datagrid('reload');
					displayMsg();
				}
				$.messager.show({
					title:'结果提示',
					msg:re['tip'],
					showType:'slide'
				});
			}
		});
// 			}
// 		});
	}
</script>
</html>
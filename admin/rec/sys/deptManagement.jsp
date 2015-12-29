<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<%@page import="java.util.*"%>
<%@page import="adultadmin.bean.cargo.CargoDeptBean"%>
<html>
<%
List deptList=new ArrayList();
if(request.getAttribute("deptList")!=null){
	deptList=(List)request.getAttribute("deptList");
}
CargoDeptBean dept=(CargoDeptBean)request.getAttribute("dept");
List detailDeptList=new ArrayList();
if(request.getAttribute("detailDeptList")!=null){
	detailDeptList=(List)request.getAttribute("detailDeptList");
}
%>
<head>
<title>组织结构管理</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/productStock.js"></script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/themes/icon.css">
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/demo/demo.css">
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/rec/js/jquery-easyui-1.3.1/jquery.easyui.min.js"></script>
<script type="text/javascript">
function newDept(){
	var depts=document.getElementById("depts");
	var newRow=depts.insertRow();
	var newCell0 = newRow.insertCell();
    var newCell1 = newRow.insertCell();
    newCell0.innerHTML = "<input type='text' name='deptName'/>";
    newCell1.innerHTML = "<input type='text' name='deptCode'/>";
}
function checkDept(){
	var nameList=document.getElementsByName("deptName");
	var codeList=document.getElementsByName("deptCode");
	var reg=/^[0-9]{2}$/;
	for(var i=0;i<nameList.length;i++){
		var name=trim(nameList[i].value).length;
		var code=trim(codeList[i].value).length;
		if((name==0&&code!=0)||(name!=0&&code==0)){
			alert("部门名称和部门代码必须填写完整！");
			return false;
		}
		if(name>0&&!reg.exec(trim(codeList[i].value))){
			alert("部门代码只能是两位数字！");
			return false;
		}
	}
	<%if(detailDeptList!=null){%>
		var changed=false;
		var name="";
		var code="";
		<%for(int i=0;i<detailDeptList.size();i++){%>
			<%CargoDeptBean cd=(CargoDeptBean)detailDeptList.get(i); %>
			name=trim(nameList[<%=i%>].value);
			code=trim(codeList[<%=i%>].value);
			if(name!='<%=cd.getName()%>'){
				changed=true;
			}
			if(code!='<%=cd.getCode()%>'){
				changed=true;
			}
		<%}%>
		if(changed==true){
			var b=confirm("修改部门编号或名称会影响部门下所有员工，确定修改？");
			if(b==false){
				return false;
			}
		}
	<%}%>
	return true;
}
</script>
</head>
<body>
<table>
	<tr>
		<td colspan="2">物流中心组织结构管理</td>
	</tr>
	<tr>
		<td width="40%" valign="top">
			<table border="1">
				<tr>
					<td>维护组织结构</td>
				</tr>
				<tr>
					<td>
						<ul id="ul"></ul>
					</td>
				</tr>
			</table>
		</td>
		<td>
			<table>
				<tr>
					<td>
						<div id="title"></div>
					</td>
				</tr>
				<tr>
					<td>
						<table id="deptlist">
							<thead>
								<tr>
									<th data-options="field:'cargo_dept_name',width:250,editor:'text',align:'center'">部门名称</th>
									<th data-options="field:'cargo_dept_code',width:250,editor:'text',align:'center'">部门代码</th>
								</tr>
							</thead>
						</table>
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
<form id="form" method="post">
	<input type="hidden" name="deptId" id="deptId" value=""/>
</form>
</body>
<script type="text/javascript">
	
	var reg=/^[0-9]{2}$/;
	var lastIndex;
	var boo = true;
	$.messager.defaults = {ok:"确认", cancel:"取消"};
	$(function(){
		$("#form").hide();
		//获取左侧树
		leftTree();
		//加载部门
		$("#deptlist").datagrid({
			title:"部门列表",
			iconCls:'icon-ok',
			width:500,
			height:'auto',
			toolbar:[{
				text:'增设部门',
				iconCls:'icon-add',
				handler:function(){
					$('#deptlist').datagrid('endEdit', lastIndex);
					$('#deptlist').datagrid('appendRow',{
						cargo_dept_name:'',
						cargo_dept_code:''
					});
					lastIndex = $('#deptlist').datagrid('getRows').length-1;
					$('#deptlist').datagrid('selectRow', lastIndex);
					$('#deptlist').datagrid('beginEdit', lastIndex);
				}
			},'-',{
				text:'保存',
				iconCls:'icon-save',
				handler:function(){
					$('#deptlist').datagrid('endEdit', lastIndex);
					lastIndex = null;
// 					判断最后一个editor
// 					if(boo&&lastIndex>=0){
// 						var namedd = $("#deptlist").datagrid('getEditor',{index:lastIndex,field:'cargo_dept_name'});
// 						var codedd = $("#deptlist").datagrid('getEditor',{index:lastIndex,field:'cargo_dept_code'});
// 						boo = checkedit($(namedd.target).val(),$(codedd.target).val());
// 					}
					var rows = $("#deptlist").datagrid("getRows");
					var p;
					var q;
					for(var i=0;i<rows.length;i++){
// 						alert(rows[i].cargo_dept_name+rows[i].cargo_dept_code);
						if(boo){
							boo = checkedit(rows[i].cargo_dept_name,rows[i].cargo_dept_code);
						}
						if(boo){
							p = "<input type='text' name='deptName' value='"+rows[i].cargo_dept_name+"'/>"
							q = "<input type='text' name='deptCode' value='"+rows[i].cargo_dept_code+"'/>"
							$("#form").append(p).append(q);
						}
					}
					if(boo){
						$.messager.confirm('提示','修改部门编号或名称会影响部门下所有员工，确定修改？',function(choice){
							if(choice){
								$("#form").form('submit',({
									url:'<%=request.getContextPath()%>/CargoController/updateDept.mmx',
									success:function(data){
										var json = eval('('+data+')');
										if(json['result']=='success'){
// 											window.location.reload;//刷新当前页
<%-- 											window.location="<%=request.getContextPath()%>/admin/rec/sys/deptManagement.jsp"; --%>
											leftTree();
										}
										$("input[name='deptName']").remove();
										$("input[name='deptCode']").remove();
										$.messager.show({
											title:'提示',
											msg:json['tip'],
											timeout:3000,
											showType:'slide'
										});
									}
								}));
							}
						});
					}
					return boo;
				}
			},'-',{
				text:'取消',
				iconCls:'icon-cancel',
				handler:function(){
					window.location="<%=request.getContextPath()%>/admin/rec/sys/deptManagement.jsp";
				}
			}],
			onClickRow:function(rowIndex, rowData){
				if (lastIndex != rowIndex){
					$('#deptlist').datagrid('endEdit', lastIndex);
					$('#deptlist').datagrid('beginEdit', rowIndex);
				}
				lastIndex = rowIndex;
			},
			onAfterEdit:function(rowIndex, rowData, changes){
// 				alert(rowIndex+","+rowData+","+changes['cargo_dept_name']);
				//这里只能判断以前编辑过的，当前编辑的则需要到点击“保存”按钮时判断
				boo = checkedit(trim(rowData['cargo_dept_name']),trim(rowData['cargo_dept_code']));
			},
			url:'<%=request.getContextPath()%>/CargoController/deptDetailList.mmx',
				showFooter:true,
				striped:true,
				collapsible:true,
				loadMsg:'数据加载中...',
				rownumbers:true,
				singleSelect:true,//只选择一行后变色
				fitColumns:true,
// 				columns:[[
// 				        {field:'cargo_dept_name',title:'部门名称',width:50,align:'center'},
// 				        {field:'cargo_dept_code',title:'部门代码',width:50,align:'center'}
// 				        ]],
			onLoadSuccess:function(data){
				$("#title").text(data['title']);
				$("#deptId").val(data['deptId']);
			}
		});
	});
	function checkedit(name1 ,code1){
		var name = trim(name1).length;
		var code = trim(code1).length;
		if((name==0&&code!=0)||(name!=0&&code==0)){
			$.messager.show({
				title:'提示',
				msg:'部门名称和部门代码必须填写完整！',
				timeout:3000,
				showType:'slide'
			});
			return false;
		}
		if(name>0&&!reg.test(trim(code1))){
			$.messager.show({
				title:'提示',
				msg:'部门代码只能是两位数字！',
				timeout:3000,
				showType:'slide'
			});
			return false;
		}
		return true;
	}
	//点击“下级部门”触发
	function nextDept(id){
		$("#deptlist").datagrid({
			url:'<%=request.getContextPath()%>/CargoController/deptDetailList.mmx',
			queryParams:{
				deptId:id
			}
		});
		lastIndex = null;
	}
	//点击“删除部门”触发
	function deleteDept(id){
		$.messager.confirm('提示','确定要删除当前所选中部门？',function(choice){
			if(choice){
				$.ajax({
					url:'<%=request.getContextPath()%>/CargoController/deleteDept.mmx',
					cache:false,
					data:{deptId:id},
					dataType:'text',
					success:function(result){
						var re = eval('('+result+')');
						$.messager.show({
							title:'结果提示',
							msg:re['tip'],
							showType:'slide'
						});
						if(re['result']=="success"){
							window.location="<%=request.getContextPath()%>/admin/rec/sys/deptManagement.jsp";
						}
					}
				});
			}
		});
	}
	function leftTree(){
		$.ajax({
			url:'<%=request.getContextPath()%>/CargoController/staffManagementOther.mmx',
			cache:false,
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
						data:re['tip'],
						lines:true,
						onBeforeSelect:function(node){return false;}
					});
				}
			}
		});
	}
</script>
</html>
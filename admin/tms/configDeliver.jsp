<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>配置快递公司</title>

<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
<script type="text/javascript" charset="UTF-8">
	var i = 0;
	var operNode;
	var editNode;
	var editRow;
	var treegrid;
	$(function() {
		treegrid = $('#treegrid').treegrid({
			url : '${pageContext.request.contextPath}/deliverController/getConfigDeliverTreeGrid.mmx',
			toolbar : [ {
				text : '展开',
				iconCls : 'icon-redo',
				handler : function() {
					var node = treegrid.treegrid('getSelected');
					if (node) {
						treegrid.treegrid('expandAll', node.id);
					} else {
						treegrid.treegrid('expandAll');
					}
				}
			}, '-', {
				text : '折叠',
				iconCls : 'icon-undo',
				handler : function() {
					var node = treegrid.treegrid('getSelected');
					if (node) {
						treegrid.treegrid('collapseAll', node.id);
					} else {
						treegrid.treegrid('collapseAll');
					}
				}
			}, '-', {
				text : '增加',
				iconCls : 'icon-add',
				handler : function() {
					append();
				}
			}, '-', {
				text : '删除',
				iconCls : 'icon-remove',
				handler : function() {
					delFun();
				}
			}, '-', {
				text : '编辑',
				iconCls : 'icon-edit',
				handler : function() {
					editFun();
				}
			}, '-', {
				text : '保存',
				iconCls : 'icon-save',
				handler : function() {
					if (operNode) {
						submitFun();
					}
				}
			}],
			title : '',
			fit : true,
			fitColumns : true,
			nowrap : true,
			idField : 'id',
			treeField : 'deliver',
			columns : [ [
			{
				title : 'id',
				field : 'id',
				width : 100,
				hidden : true
			}, {
				field : 'deliver',
				title : '快递公司',
				width : 100,
				formatter : function(value,rowData,rowIndex){
					return rowData.text;
				},
				editor : {
					type : 'combobox',
					options : {
						url : '${pageContext.request.contextPath}/Combobox/getDelivers.mmx',
						valueField : 'id',
						textField : 'text',
						editable : false,
						animate : false,
					}
				}
			},{
				field : 'zc',
				title : '增城仓',
				width : 100,
				align : 'center',
				formatter : function(value,rowData,rowIndex){
					if(value != null){
						return value + "%";
					}
				},
				editor : {
					type : 'numberbox',
					options : {
						min : 0,
						max : 100,
					}
				}
			}, {
				field : 'wx',
				title : '无锡仓',
				width : 100,
				align : 'center',
				formatter : function(value,rowData,rowIndex){
					if(value != null){
						return value + "%";
					}
				},
				editor : {
					type : 'numberbox',
					options : {
						min : 0,
						max : 100,
					}
				}
			}, {
				field : 'index',
				title : '优先级',
				width : 30,
				align : 'center',
				editor : {
					type : 'numberbox',
					options : {
						min : 1,
					}
				}
			}, {
				field : 'oper',
				title : '操作',
				width : 30,
				align : 'center',
				formatter : function(value,rowData,rowIndex){
					if(rowData.id.split('_')[1] == 2){
						if(rowData.state == 'closed'){
							return '<a href=\"javascript:void(0);\" class=\"addButton\" onclick=\"append(\''+ rowData.id + '\')\"></a>'
								+	'<a href=\"javascript:void(0);\" class=\"editButton\" onclick=\"editFun(\''+ rowData.id + '\')\"></a>'
						 		+	'<a href=\"javascript:void(0);\" class=\"delButton\" onclick=\"delFun(\''+ rowData.id + '\')\"></a>';
						}else {
							return '<a href=\"javascript:void(0);\" class=\"addButton\" onclick=\"append(\''+ rowData.id + '\')\"></a>';
						}
					} else if(rowData.id.split('_')[1] == 3){
						if(editNode != null){
							return '<a href=\"javascript:void(0);\" class=\"deleteButton\" onclick=\"removeFun(\''+ rowData.id + '\')\"></a>';
						}
					}
				}
			}, {
				field : 'parentId',
				title : '上级菜单',
				hidden : true
			}] ],
			onLoadSuccess : function(data){
				$(".deleteButton").linkbutton({
					iconCls:'icon-cancel',
					text:"",
					plain:true,
				});
				$(".editButton").linkbutton({
					iconCls:'icon-edit',
					text:"",
					plain:true,
				});
				$(".delButton").linkbutton({
					iconCls:'icon-remove',
					text:"",
					plain:true,
				});
				$(".addButton").linkbutton({
					iconCls:'icon-add',
					text:"",
					plain:true,
				});
				if(editNode != null){
					var childrens = treegrid.treegrid('getChildren',editNode.id);
					$.each(childrens, function(i, child) {
						treegrid.treegrid('beginEdit',child.id);
					});
					editNode = null;
				}
			},
			onContextMenu : function(e, row) {
				e.preventDefault();
				$(this).treegrid('unselectAll');
				$(this).treegrid('select', row.id);
				$('#menu').menu('show', {
					left : e.pageX,
					top : e.pageY
				});
			},
			onExpand : function(row) {
				treegrid.treegrid('unselectAll');
			},
			onCollapse : function(row) {
				treegrid.treegrid('unselectAll');
			}
		});

	});
	function append(id) {
		var node;
		if(id != null){
			node = treegrid.treegrid('find',id);
		} else {
			node = treegrid.treegrid('getSelected');
			if(node == null){
				$.messager.show({msg : '先选中要配置快递公司的市' , title : '提示'});
				return;
			}
		}
		if(node.id.split('_')[1] == 2){
			if(operNode != null){
				if(node.id != operNode.id){
					$.messager.alert('提示消息','先保存当前编辑,再做其它操作！','info');
					return;
				}
			} else {
				operNode = treegrid.treegrid('find',node.id);
			}
			editNode = operNode;
			if(operNode.id.split('_')[0] == -1){
				if(!checkProvinces()){
					operNode = null;
					return;
				}
			} else {
				if(!checkOtherCity()){
					operNode = null;
					return;
				}
			}
			if(editRow) {
				if(!check()){
					return;
				}
			}
			var data = [ {
				id : 'temp_3_' + i,
				deliver : '请选择',
				zc : '',
				wx : '',
				parentId : (node ? node.id : '')
			} ];
			var opts = {
				parent : data[0].parentId,
				data : data
			};
			treegrid.treegrid('append', opts);
			treegrid.treegrid('beginEdit', data[0].id);
			editRow = data[0];
		}
	}
	function removeFun(id){
		var parentNode = treegrid.treegrid('getParent',id);
		if(id.split("_")[0] != 'temp'){
			alert("删除");
			$.ajax({
				url : '${pageContext.request.contextPath}/deliverController/delDeliverConfig.mmx',
				data : {
					ids : id,
					parentId : parentNode.id
				},
				cache : false,
				dataType : "json",
				success : function(r) {
					if (r.success) {
						treegrid.treegrid('remove',id);
						editRow = null;
						$.messager.show({msg : r.msg , title : '提示'});
					} else {
						$.messager.alert('提示消息','删除失败！','info');
					}
				}
			});
		} else {
			treegrid.treegrid('remove',id);
			editRow = null;
		}
	}
	function delFun(id) {
		if(id == null){
			var node = treegrid.treegrid('getSelected');
			if(node != null){
				id = node.id;
			}else {
				$.messager.show({msg : '先选中要删除的市' , title : '提示'});
				return;
			}
		}
		treegrid.treegrid('select',id);
		var node = treegrid.treegrid('getSelected');
		if(node.id.split('_')[1] == 2){
			$.messager.confirm('询问', '您确定要删除【' + node.text + '】下的所有快递公司？', function(r) {
				if (r) {
					var childrens = treegrid.treegrid('getChildren',id);
					var ids = '';
					$.each(childrens, function(i, child) {
						if(child.id.split("_")[0] != 'temp'){
							if(ids != ''){
								ids += '*' +  child.id;
							} else {
								ids = child.id;
							}
						} else {
							treegrid.treegrid('remove',child.id);
						}
					});
					$.ajax({
						url : '${pageContext.request.contextPath}/deliverController/delDeliverConfig.mmx',
						data : {
							ids : ids,
							parentId : node.id
						},
						cache : false,
						dataType : "json",
						success : function(r) {
							if (r.success) {
								treegrid.treegrid('reload',node.id);
								treegrid.treegrid('refresh',node.id);
								$.messager.show({msg : r.msg , title : '提示'});
								operNode = null;
							} else {
								$.messager.alert('提示消息',r.msg,'info');
							}
						}
					});
				}
			});
		}
	}
	function editFun(id){
		var node;
		if(id != null){
			node = treegrid.treegrid('find',id);
		} else {
			node = treegrid.treegrid('getSelected');
			if(node == null){
				$.messager.show({msg : '先选中要编辑的项！' , title : '提示'});
				return;
			}
		}
		if(node.id.split('_')[1] == 2){
			if(operNode != null){
				if(node.id != operNode.id){
					$.messager.alert('提示消息','先保存当前编辑,再编辑其它！','info');
					return;
				}
			} else {
				operNode = treegrid.treegrid('find',node.id);
			}
			editNode = operNode;
			treegrid.treegrid('reload', operNode.id);
		}
	}
	function submitFun(){
		var count_zc = 0;
		var count_wx = 0;
		var tempDeliver = [];
		var tempIndex = [];
		var id = '';
		var deliver = '';
		var zc = '';
		var wx = '';
		var index = '';
		var parentId = '';
		var flag = true;
		if(operNode == null){
			$.messager.show({msg : "当前编辑选项未知!" , title : '提示'});
			return;
		}
		var nodes = treegrid.treegrid('getChildren',operNode.id);
		$.each(nodes, function(i, val) {
			treegrid.treegrid('endEdit', val.id);
		});
		nodes = treegrid.treegrid('getChildren',operNode.id);
		$.each(nodes, function(i, val) {
			treegrid.treegrid('beginEdit', val.id);
		});
		console.info(nodes);
		$.each(nodes, function(i, val) {
			if($.inArray(val.deliver, tempDeliver) > -1){
				$.messager.alert('提示消息','有重复的快递公司!','info');
				flag = false;
			}
			tempDeliver[i] = val.deliver;
			if($.inArray(val.index, tempIndex) > -1){
				$.messager.alert('提示消息','有重复的优先级!','info');
				flag = false;
			}
			tempIndex[i] = val.index;
			if(val.zc == ''){
				val.zc == '0'
			}
			if(val.wx == ''){
				val.wx == '0'
			}
			if(val.zc == '0' && val.wx == '0'){
				$.messager.alert('提示消息','至少要配置一个地区!','info');
				flag = false;
			}
			if(val.index == ''){
				$.messager.alert('提示消息','优先级不能为空!','info');
				flag = false;
			}
			if(val.zc != ''){
				count_zc = parseInt(count_zc) + parseInt( val.zc);
			}
			if(val.wx != ''){
				count_wx =  parseInt(count_wx) +  parseInt(val.wx);
			}
			if(id != ''){
				id += '*' + val.id;
			} else {
				id = val.id;
			}
			if(deliver != ''){
				deliver += '*' + val.deliver;
			} else {
				deliver = val.deliver;
			}
			if(index != ''){
				index += '*' + val.index;
			} else {
				index = val.index;
			}
			if(zc != ''){
				zc = zc + '*' + val.zc;
			} else {
				zc = val.zc;
			}
			if(wx != ''){
				wx = wx + '*' + val.wx;
			} else {
				wx = val.wx;
			}
			if(parentId != ''){
				parentId += '*' + val.parentId;
			} else {
				parentId = val.parentId;
			}
         }); 
		if(!flag){
			return;
		}
		if(count_wx != 0 && count_wx != 100){
			$.messager.alert('提示消息','无锡地区分配比例不符合要求！','info');
			return ;
		}
		if(count_zc != 0 && count_zc != 100){
			$.messager.alert('提示消息','增城地区分配比例不符合要求！','info');
			return ;
		}
		$.ajax({
			url : '${pageContext.request.contextPath}/deliverController/configDeliver.mmx',
			data : {
				id : id,
				deliver : deliver,
				zc : zc,
				wx : wx,
				parentId : parentId,
				index : index
			},
			cache : false,
			dataType : "json",
			success : function(r) {
				if(r.success){
					$.messager.show({msg : r.msg,title : '提示'});
					operNode = null;
				} else {
					$.messager.alert('提示消息',r.msg,'info');
				}
				reload();//刷新
			}
		});
	}
	function checkProvinces(){
		var flag = true;
		var childNodes = treegrid.treegrid('getChildren',operNode.parentId);
		$.each(childNodes, function(i, node) {
			if(node.id.split('_')[0] != -1){
				var nextNodes = treegrid.treegrid('getChildren',node.id);
				if(node.state == 'closed' || nextNodes.length > 0){
					$.messager.alert('提示消息','先删除其它市配置,再配置全境！','info');
					flag =  false;
					return;
				}
			}
		});
		return flag;
	}
	function checkOtherCity(){
		var flag = true;
		var childNodes = treegrid.treegrid('getChildren',operNode.parentId +'');
		$.each(childNodes, function(i, node) {
			if(node.id.split('_')[0] == -1){
				var nextNodes = treegrid.treegrid('getChildren',node.id);
				if(node.state == 'closed' || nextNodes.length > 0){
					$.messager.alert('提示消息','先删除全境配置,再配置其它市！','info');
					flag = false;
					return;
				}
			}
		});
		return flag;
	}
	function check(){
		treegrid.treegrid('endEdit', editRow.id)
		var row = treegrid.treegrid('find', editRow.id);
		console.info(row);
		if(row.zc == '' && row.wx == ''){
			$.messager.alert('提示消息','至少得配一个地区!','info');
			treegrid.treegrid('beginEdit', editRow.id)
			return false;
		}
		if(row.deliver == '请选择'){
			$.messager.alert('提示消息','请选择快递公司!','info');
			treegrid.treegrid('beginEdit', editRow.id)
			return false;
		}
		i++;
		treegrid.treegrid('beginEdit', editRow.id)
		return true;
	}
	function reload() {
		editRow = undefined;
		treegrid.treegrid('reload');
	}
</script>
</head>
<body class="easyui-layout" fit="true">
	<div region="center" border="false" style="overflow: hidden;">
		<table id="treegrid"></table>
	</div>

	<div id="menu" class="easyui-menu" style="width:140px;display: none;">
		<div onclick="append();" iconCls="icon-add">增加</div>
		<div onclick="editFun();" iconCls="icon-edit">编辑</div>
		<div onclick="del();" iconCls="icon-remove">删除</div>
	</div>
</body>
</html>
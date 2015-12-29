<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript" charset="UTF-8">
	var tree;
	$(function() {
		tree = $('#tree').tree({
			url : '${pageContext.request.contextPath}/MenuController/getMenuTree.mmx',
			animate : false,
			onClick : function(node) {
				if (node.attributes && node.attributes.url && node.attributes.url != '') {
					var href;
					if (/^\//.test(node.attributes.src)) {/*以"/"符号开头的,说明是本项目地址*/
						href = node.attributes.url.substr(1);
						$.messager.progress({
							text : '请求数据中....',
							interval : 100
						});
					} else {
						href = '${pageContext.request.contextPath}/' + node.attributes.url;
					}
					addTabFun({
						src : href,
						title : node.text
					});
				}
			}
			,
			 onContextMenu: function(e,node){  
	                e.preventDefault();  
	                $(this).tree('select',node.target);  
	                $('#mm').menu('show',{  
	                    left: e.pageX,  
	                    top: e.pageY  
	                });  
	            }
		});

	});
	function collapseAll() {
		var node = tree.tree('getSelected');
		if (node) {
			tree.tree('collapseAll', node.target);
		} else {
			tree.tree('collapseAll');
		}
	}
	function expandAll() {
		var node = tree.tree('getSelected');
		if (node) {
			tree.tree('expandAll', node.target);
		} else {
			tree.tree('expandAll');
		}
	}
	function newOpen() {
		var node = tree.tree('getSelected');
		if (node) {
			tree.tree('expandAll', node.target);
		} else {
			tree.tree('expandAll');
		}
		if(node.attributes.url != ''){
			window.open('${pageContext.request.contextPath}/' + node.attributes.url,"_blank");
		}
	}
</script>
<div class="easyui-panel" fit="true" border="false">
	<div class="easyui-accordion" fit="true" border="false">
		<div title="系统菜单" iconCls="icon-tip">
			<div class="easyui-layout" fit="true">
				<div region="north" border="false" style="overflow: hidden;">
					<a href="javascript:void(0);" class="easyui-linkbutton" plain="true" iconCls="icon-redo" onclick="expandAll();">展开</a>
					<a href="javascript:void(0);" class="easyui-linkbutton" plain="true" iconCls="icon-undo" onclick="collapseAll();">折叠</a>
					<a href="javascript:void(0);" class="easyui-linkbutton" plain="true" iconCls="icon-reload" onclick="tree.tree('reload');">刷新</a>
					<hr style="border-color: #fff;" />
				</div>
				<div region="center" border="false">
					<ul id="tree" style="margin-top: 5px;"></ul>
				</div>
			</div>
		</div>
	</div>
</div>
 <div id="mm" class="easyui-menu" style="width:120px;">  
        <div onclick="newOpen()" data-options="iconCls:'icon-ok'">新页面打开</div>  
    </div>  
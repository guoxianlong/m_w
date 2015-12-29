<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
</head>
<body>
	<div id="matchFailDialog" style="overflow-y:auto; overflow-x:auto;">
			<fieldset>
				<div>
					<h3>添加商品</h3>
					<hr/>
				</div>
				<table class="tableForm" align="center">
					<tr>
						<td>
							商品编号：<input id="productCode" name="productCode" />
							<a class="easyui-linkbutton"  data-options="iconCls:'icon-add',plain:true" onclick="addMatchFailFun();" href="javascript:void(0);">添加</a>
						</td>
					</tr>
				</table>
			</fieldset>
			<input type='hidden' id="id" name="id"/>
			<div style='height:250px'>
				<h3>签售商品记录</h3>
				<div id="failPackageProductsToolbar"  style="height: auto;">
				<div style="height: auto;">
					<a class="easyui-linkbutton"  data-options="iconCls:'icon-ok',plain:true" onclick="addMatchFailDetectProducts();" href="javascript:void(0);">提交</a>
				</div>
				
			</div>
			<table id='failPackageProductsDataGrid'></table>
	</div>
</body>
</html>

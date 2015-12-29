<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
$("#editReason").focus();
</script>
<div align="center">
	<form id="returnsReasonEditForm" method="post">
		<table class="tableForm">
			<tr>
				<th style="width: 100px;">退货原因条码：</th>
				<td><input name="code" class="easyui-validatebox" data-options="required:true" />
				</td>
			</tr>
			<tr>
				<th>销售退货原因：</th>
				<td><input name="reason" class="easyui-validatebox" data-options="required:true"  style="width: 150px;" />
				</td>
			</tr>
		</table>
		<input name="id" id="id" type='hidden'/>  
	</form>
</div>
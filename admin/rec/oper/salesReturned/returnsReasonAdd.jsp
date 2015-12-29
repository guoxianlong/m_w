<%@ page language="java" pageEncoding="UTF-8"%>
<script type="text/javascript">
$("#addReason").focus();
$("#returnsReasonAddForm [name=reason]").keypress(function(e) {
      var key = window.event ? e.keyCode : e.which;
      if (key.toString() == "13") {
      	$("#addReason").click();
          return false;
      }
 });
</script>
<div align="center">
	<form id="returnsReasonAddForm" method="post">
		<table class="tableForm">
			<tr>
				<th style="width: 100px;">销售退货原因：</th>
				<td><input name="reason" id="reason" class="easyui-validatebox" data-options="required:true" />
			</tr>
		</table>
	</form>
</div>
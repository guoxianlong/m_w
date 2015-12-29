<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
</head>
<body>
	<div style="height:520px">
		<div id="confDeliver"  style="height: auto;">
			<fieldset>
			<form id="confDeliverForm" method="post">
				<hr/>
				<input type="hidden" id="areaId" name="areaId"/>
				<input type="hidden" id="provinceId" name="provinceId"/>
				<table id="table" class="tableForm">
					<tr align="center">
						<th>地区：</th>
						<td align="left">
							<input id="areaName" name="areaName" style="width: 150px;" disabled="disabled"/>
						</td>
						<th>省：</th>
						<td align="left">
							<input id="provinceName" name="provinceName" style="width: 150px;"  disabled="disabled"/>
						</td>
						<td>
							<a class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-add'" onclick="addQuoteHtml(-1,0,1);" href="javascript:void(0);"></a>
						</td>
					</tr>
					<input id="addItem" type="hidden"/>
				</table>
			</form>
			</fieldset>
		</div>
	</div>
</body>
</html>
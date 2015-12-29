<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
</head>
<body>
	<div style="height: 1000px;">
	<table id="afterSaleDataGrid"></table> 
	<div id="afterSaletb"  style="height: auto;display: none;">
		<fieldset>
			<h3>快递单号：<label id="packageCode"></label></h3>
			<form id="afterSaleForm" method="post">
			<input type="hidden" id="id" name="id"/>
			<table class="tableForm" align="center">
				<tr align="center" >
					<th>订单号：</th>
					<td align="left">
						<textArea id="orderCode" name="orderCode" cols="25" rows="1" ></textArea>
					</td>
					<td>
						扫描发货清单，或者手动输入发货清单号
					</td>
				</tr>
				<tr align="center">
					<th>手机号：</th>
					<td align="left">
						<textArea id="phone" name="phone" cols="25" rows="1" ></textArea>
					</td>
					<td>
						若没有填写订单号，使用手机号码查询匹配的售后单
					</td>
				</tr>
				<tr align="center">
					<th>寄件人姓名：</th>
					<td align="left">
						<input id="senderName" name="senderName" style="width: 116px;"/>
					</td>
					<td></td>
				</tr>
				<tr align="center">
					<th>寄件人地址：</th>
					<td align="left">
						<input id="senderAddress" name="senderAddress" style="width: 116px;"/>
					</td>
					<td></td>
				</tr>
				<tr align="center">
					<td></td>
					<td>
						<a class="easyui-linkbutton"  data-options="iconCls:'icon-ok',plain:true" onclick="saveFun();" href="javascript:void(0);">提交</a>
					</td>
					<td></td>
				</tr>
			</table>
		</form>
		</fieldset>
		<h3>售后单记录：</h3>
		<a class="easyui-linkbutton"  data-options="iconCls:'icon-add',plain:true" onclick="addAfterSaleDetectProducts();" href="javascript:void(0);">确认匹配</a>
		<a class="easyui-linkbutton"  data-options="iconCls:'icon-add',plain:true" onclick="matchFailFun();" href="javascript:void(0);">添加匹配失败包裹</a>
	</div>
	</div>
</body>
</html>
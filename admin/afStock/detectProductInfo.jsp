<%@ page language="java" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<jsp:include page="../rec/inc/easyui.jsp"></jsp:include>
</head>
<body>
	<div style="height:520px">
		<div id="detectProduct"  style="height: auto;">
			<fieldset>
			<form id="detectProductForm" method="post">
				<h3>检测记录</h3>
				<hr/>
				<input type="hidden" id="index"/>
				<input type="hidden" id="afterSaleDetectPackageId" name="afterSaleDetectPackageId"/>
				<input type="hidden" id="afterSaleOrderId" name="afterSaleOrderId"/>
				<input type="hidden" id="id" name="id"/>
				<table id="table" class="tableForm">
					<tr align="center">
						<th>问题分类：</th>
						<td align="left">
							<input id="questionDescription" name="questionDescription" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="questionDescription2" name="questionDescription2" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="questionDescription3" name="questionDescription3" style="width: 116px;"/>
						</td>
						<th>故障描述：</th>
						<td align="left">
							<input id="faultDescription" name="faultDescription" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="faultDescription2" name="faultDescription2" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="faultDescription3" name="faultDescription3" style="width: 116px;"/>
						</td>
						<td></td>
						<td></td>
					</tr>
					<tr align="center" >
						<th>包装：</th>
						<td align="left">
							<input id="damaged" name="damaged" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="damaged2" name="damaged2" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="damaged3" name="damaged3" style="width: 116px;"/>
						</td>
						<th>赠品：</th>
						<td align="left">
							<input id="giftAll" name="giftAll" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="giftAll2" name="giftAll2" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="giftAll3" name="giftAll3" style="width: 116px;"/>
						</td>
					</tr>
					<tr align="center" >
						<th>申报状态：</th>
						<td align="left">
							<input id="reportStatus" name="reportStatus" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="reportStatus2" name="reportStatus2" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="reportStatus3" name="reportStatus3" style="width: 116px;"/>
						</td>
						<th>发票：</th>
						<td align="left">
							<input id="debitNote" name="debitNote" style="width: 116px;"/>
						</td>
					</tr>
					<tr align="center" >
						<th>故障代码：</th>
						<td align="left">
							<input id="faultCode" name="faultCode" style="width: 116px;"/>
						</td>
						<th>IMEI：</th>
						<td align="left">
							<input id="IMEI" name="IMEI" style="width: 116px;"/>
						</td>
					</tr>
					<tr align="center" >
						<th><input type="checkbox" id="detectException" name="detectException" onclick="exeGetChecked();" value="1"/>检测异常</th>
						<th>异常原因：</th>
						<td align="left">
							<input id="exceptionReason" name="exceptionReason" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="exceptionReason2" name="exceptionReason2" style="width: 116px;"/>
						</td>
						<td align="left">
							<input id="exceptionReason3" name="exceptionReason3" style="width: 116px;"/>
						</td>
						<td></td>
						<td></td>
					</tr>
					<tr align="center">
						<th>主商品</th>
						<td align="left" colspan="3"><div id="productStatusDiv"></div></td>
					</tr>
					<tr align="center">
						<th>报价项：</th>
						<td align="left" colspan="3">
							<input id="quoteItem1" name="quoteItem1" style="width: 300px;"/>
						</td>
						<th>报价：</th>
						<td align="left">
							<input id="quote1" name="quote1" style="width: 116px;" class="easyui-numberbox" data-options="precision:2,max:99999999.99"/>
							<input id="parentId1" name="parentId1" type="hidden"/>
							<a class="easyui-linkbutton" data-options="plain:true,iconCls:'icon-add'" onclick="addQuoteHtml(1);" href="javascript:void(0);"></a>
						</td>
					</tr>
					<tr align="center">
						<th>备注：</th>
						<td align="left" colspan="5">
							<textarea id="remark" name="remark" cols="50" rows="4"></textarea>
						</td>
					</tr>
				</table>
				<hr/>
				<table  class="tableForm" align="center">
					<tr align="center" >
						<th>处理意见：</th>
						<td>
							<input id="handle" name="handle" style="width: 116px;"/>
						</td>
					</tr>
					<tr align="center">
						<td colspan="2">
							<a class="easyui-linkbutton"  onclick="detectProductFun();" href="javascript:void(0);">添加本条检测记录</a>
						</td>
					</tr>
				</table>
			</form>
			</fieldset>
		</div>
	</div>
</body>
</html>
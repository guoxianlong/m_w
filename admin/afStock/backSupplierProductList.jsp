<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>返厂清单</title>
<link href="<%=request.getContextPath()%>/css/globalTable.css" rel="stylesheet" type="text/css">
</head>
<body>
		<div id="repairList" style="padding:8px;">
				<table class="gridtable">
					<tr>
						<th colspan="10" align="center">无锡买卖宝送修清单</th>
					</tr>
					<tr>
						<td colspan="3">经销商：无锡买卖宝</td>
						<td colspan="2">发货日期：${repairListBean.shipDate}</td>
						<td colspan="5">返回厂家名称：${repairListBean.supplierName}</td>
					</tr>
					<tr>
						<td colspan="3">联系人：${repairListBean.contract}</td>
						<td colspan="2">联系电话：${repairListBean.contractPhone}</td>
						<td colspan="5">运输单号：${repairListBean.packageCode}</td>
					</tr>
					<tr>
						<th align="left" colspan="10">送修商品明细</th>
					</tr>
					<tr>
						<th>序号</th>
				 			<th>商品名称</th>
				 			<th>型号</th>
				 			<th>售后处理单号</th>
				 			<th>IMEI</th>
				 			<th>是否返修</th>
				 			<th>故障代码</th>
				 			<th>故障描述</th>
				 			<th>申报状态</th>
					</tr>
					<c:forEach var="productDetail" items="${productDetailList}" varStatus="ststus">
						<tr>
							<td>${ststus.count}</td>
							<td>${productDetail.productName}</td>
							<td>${productDetail.productOriname}</td>
							<td>${productDetail.detectCode}</td>
							<td>${productDetail.imei}</td>
							<td>${productDetail.firstRepairName}</td>
							<td>${productDetail.faultCode}</td>
							<td>${productDetail.faultDescript}</td>
							<td>${productDetail.reportStatus}</td>
						</tr>
					</c:forEach>
					<tr>
						<th colspan="10" align="left">总计：${totalCount}</th>
					</tr>
					<tr>
						<td colspan="10">备注：${repairListBean.remark}</td>
					</tr>
					<tr>
						<td colspan="10">
							敬爱的客户：请您在收到包裹后核实无误签名将此单发回我司，数量如与实物不符，
							请在24H内通知我司核实，如在收货后未做答复，表示默认此包裹帐实相符，
							我司不再作答，感谢支持！
						</td>
					</tr>
					<tr>
						<td colspan="10" align="right">
							发货方签字：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							日期：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					</tr>
					<tr>
						<td colspan="10" align="right">
							我已确认收到的实物与装箱单的明细准确无误&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							收货方签名/盖章：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
							日期：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
					</tr>
					<tr>
						<td colspan="10">
								<span>维修好请返回以下地址：${repairListBean.deliveryAddress}&nbsp;&nbsp;邮编:${repairListBean.zipCode}</span><br/>
								收件人:&nbsp;&nbsp;${repairListBean.contract}&nbsp;&nbsp;&nbsp;&nbsp;电话: ${repairListBean.contractPhone}
						</td>
					</tr>
				</table>
		</div>
</body>
</html>

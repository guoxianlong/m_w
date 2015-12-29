<%@ page language="java" pageEncoding="UTF-8"%>
<%@include file="/monitor/commons/taglibs.jsp"%>
<%@include file="/monitor/commons/header.jsp"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<link rel="stylesheet" type="text/css" href="${path}/monitor/commons/common.css"/>
	<script type="text/javascript" src="${path}/monitor/js/popWaybillFeedback.js"></script>
</head>
<div class="main">
	<div class="topDiv">
		<h2>pop面单信息反馈监控</h2>
	</div>
	<div class="searchDiv">
		<form action="${path}/monitoringController/popWaybillFeedback.mmx" method="post" id="searchForm">
			<input type="hidden" name="pageNum" id="pageNum" value="1"/>
			<input type="hidden" name="flag" id="flag" value="${flag}"/>
			<select name="codeType">
				<option value="1" ${queryParam.codeType == 1 ? "selected":""}>MMB订单号</option>
				<option value="3" ${queryParam.codeType == 3 ? "selected":""}>POP订单号</option>
				<option value="2" ${queryParam.codeType == 2 ? "selected":""}>运单号</option>
			</select>：
			<input name="code" type="text" value="${queryParam.code }"/>&nbsp;&nbsp;&nbsp;&nbsp;
			推送时间：<input id="startTime" type="text" value="${queryParam.startTime }" name="startTime" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">&nbsp;至&nbsp;<input id="endTime" type="text" name="endTime" value="${queryParam.endTime }" onclick="WdatePicker({dateFmt:'yyyy-MM-dd HH:mm:ss'})">&nbsp;&nbsp;&nbsp;&nbsp;
				</br>
				</br>
			状态：<select id="sendStatus" name="sendStatus">
					<c:choose>
						<c:when test="${queryParam.sendStatus == 2 }">
							<option value="0">请选择</option>
							<option value="2" selected="selected">成功</option>
							<option value="-1">失败</option>
						</c:when>
						<c:when test="${queryParam.sendStatus == -1 }">
							<option value="0">请选择</option>
							<option value="2">成功</option>
							<option value="-1" selected="selected">失败</option>
						</c:when>
						<c:otherwise>
							<option value="0" selected="selected">请选择</option>
							<option value="2">成功</option>
							<option value="-1">失败</option>
						</c:otherwise>
					</c:choose>
				</select>&nbsp;&nbsp;&nbsp;&nbsp;				
				失败原因：<select id="failedReason" name="failedReason">
							<option value="0">请选择</option>
							<option value="-4" ${queryParam.failedReason == -4 ? "selected":""}>零元单</option>
							<option value="-3" ${queryParam.failedReason == -3 ? "selected":""}>采销数据异常</option>
							<option value="-1" ${queryParam.failedReason == -1 ? "selected":""}>其他</option>
						 </select>
				&nbsp;&nbsp;&nbsp;&nbsp;
			
		</form>
		<div class="handle" style="text-align:right;">
			<input type="button" id="searchBtn" value="查询"/>&nbsp;&nbsp;
			<input type="button" id="resetBtn" value="重置"/>&nbsp;&nbsp;
			<input type="button" id="handleBtn" value="手动处理"/>
		</div>
	</div>
	<div class="tabDiv">
		<table class="tableClass">
			<tr>
				<th><input type="checkbox" id="checkAll">全选</th>
				<th>序号</th>
				<th>订单创建时间</th>
				<th>推送时间</th>
				<th>运单号</th>
				<th>MMB订单号</th>
				<th>京东订单号</th>
				<th>失败原因</th>
				<th>状态标识</th>
				<th>状态</th>
			</tr>
			<c:forEach items="${page.list }" var="bean" varStatus="status">
				<tr>
					<input type="hidden" name="failedReasonRs" id="failedReasonRs${bean.id}" value="${bean.sendStatus}">
					<c:choose>
						<c:when test="${bean.sendStatus == 2 }">
							<td><input type="checkbox" disabled="disabled"/></td>
							<td>${status.count }</td>
							<td>${bean.orderCreateTime }</td>
							<td>${bean.outstockTime }</td>
							<td>${bean.deliverCode }</td>
							<td>${bean.orderCode }</td>
							<td>${bean.popOrderCode }</td>
							<td>&nbsp;</td>
							<td>T</td>
							<td>成功</td>
						</c:when>
						<c:otherwise>
							<td><input type="checkbox" name="popOrderId" value="${bean.id }"/></td>
							<td><font color="red">${status.count }</font></td>
							<td><font color="red">${bean.orderCreateTime }</font></td>
							<td><font color="red">${bean.outstockTime }</font></td>
							<td><font color="red">${bean.deliverCode }</font></td>
							<td><font color="red">${bean.orderCode }</font></td>
							<td><font color="red">
								<c:choose>
									<c:when test="${bean.orderId == 0 }">
										${bean.popOrderCode }
									</c:when>
									<c:otherwise>
										<a style="color:#ff0000" href="/ware/admin/order.do?id=${bean.orderId}" target="_blank">${bean.popOrderCode }</a>
									</c:otherwise>
								</c:choose>
							</font></td>
							<td><font color="red">
								<c:choose>
									<c:when test="${bean.sendStatus == -4 }">
										零元单
									</c:when>
									<c:when test="${bean.sendStatus == -3 }">
										采销数据异常
									</c:when>
									<c:otherwise>
										其他
									</c:otherwise>
								</c:choose>
							</font></td>
							<td><font color="red">F</font></td>
							<td><font color="red">失败</font></td>
						</c:otherwise>						
					</c:choose>
				</tr>
			</c:forEach>
		</table>
	</div>
	<div class="pageDiv">
		<c:if test="${page.pageNum > 1 }">
			<a class="pageJump" pageNum="1" style="width: 30px" href="javascript:;">首页</a>&nbsp;&nbsp;
		</c:if>
	   	<c:if test="${page.pageNum-1 >= 1 }">
	   		<a class="pageJump" pageNum="${page.pageNum-1 }" style="width: 30px" href="javascript:;">上页</a>&nbsp;&nbsp;
	   	</c:if>
	   	<c:forEach begin="${page.startPage}" end="${page.endPage}" var="num">
	   		<c:choose>
	   			<c:when test="${num == page.pageNum }">
	   				&nbsp;<a class="pageJump" pageNum="${num }" href="javascript:;"><font color="red">${num }</font></a>&nbsp;
	   			</c:when>
	   			<c:otherwise>
	   				&nbsp;<a class="pageJump" pageNum="${num }" href="javascript:;">${num }</a>&nbsp;
	   			</c:otherwise>
	   		</c:choose>
	   	</c:forEach>
	   	<c:if test="${page.pageNum+1 <= page.totalPage }">
	   		&nbsp;&nbsp;<a class="pageJump" style="width: 30px" pageNum="${page.pageNum+1 }" href="javascript:;">下页</a>
	   	</c:if>&nbsp;&nbsp;
	   	<c:if test="${page.pageNum < page.totalPage }">
	   		<a class="pageJump" pageNum="${page.totalPage }" style="width: 30px" href="javascript:;">尾页</a>&nbsp;&nbsp;
	   	</c:if>
	   	(当前页${page.pageNum }/共${page.totalPage }页)
	</div>
</div>
</body>
</html>
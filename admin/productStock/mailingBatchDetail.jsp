<%@ page contentType="text/html;charset=utf-8"%>
<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@page import="java.util.*"%>

<%@page import="adultadmin.action.vo.voOrder"%>
<%@page import="adultadmin.bean.stock.MailingBatchParcelBean"%>
<%@page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.UserGroupBean"%>
<%@page import="adultadmin.bean.PagingBean"%>
<%@page import="adultadmin.util.PageUtil"%>
<%@page import="java.text.DecimalFormat"%><html>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
MailingBatchBean batch=(MailingBatchBean)request.getAttribute("batch");
List parcelList=batch.getMailingBatchParcelList();
PagingBean pageBean=(PagingBean)request.getAttribute("paging");
DecimalFormat dcmFmt = new DecimalFormat("0.00");

%>
<head>
<title>发货波次详细页</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
<script type="text/javascript">
function print( a){
	window.open("productStock/mailingBatchPrintLine.jsp?code="+a,"_blank")
}
function transit(){
	var b=confirm("确认交接完成");
	if(b==true){
		window.location='mailingBatch.do?method=transitMailingBatch&mailingBatchId=<%=batch.getId() %>';
	}
}
function deleteParcel(id){
	var b=confirm("是否删除该邮包？");
	if(b==true){
		window.location='mailingBatch.do?method=deleteParcel&parcelId='+id+'&pageIndex='+<%=pageBean.getCurrentPageIndex()%>;
	}
}
function addMailingParcel(){
	window.location='mailingBatch.do?method=addMailingBatchParcel&mailingBatchId=<%=batch.getId() %>';
}
function showPackage(id){
	$("#parcel"+id).slideToggle("slow");
}
function updateMailingBatchOrderCount(){
	$.ajax({
		type: "GET",
		url: "mailingBatch.do?method=updateMailingBatchOrderCount&mailingBatchId="+<%=batch.getId()%>,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("orderCount").innerHTML = msg;
		}
	});
}
function updateMailingBatchTotalWeight(){
	$.ajax({
		type: "GET",
		url: "mailingBatch.do?method=updateMailingBatchTotalWeight&mailingBatchId="+<%=batch.getId()%>,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("totalWeight").innerHTML = msg;
		}
	});
}
function updateMailingBatchParcelOrderCount(parcelId){
	$.ajax({
		type: "GET",
		url: "mailingBatch.do?method=updateMailingBatchParcelOrderCount&parcelId="+parcelId,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("parcelOrderCount"+parcelId).innerHTML = msg;
		}
	});
}
function updateMailingBatchParcelTotalWeight(parcelId){
	$.ajax({
		type: "GET",
		url: "mailingBatch.do?method=updateMailingBatchParcelTotalWeight&parcelId="+parcelId,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("parcelTotalWeight"+parcelId).innerHTML = msg;
		}
	});
}
function updateMailingBatchParcelTotalPrice(parcelId){
	$.ajax({
		type: "GET",
		url: "mailingBatch.do?method=updateMailingBatchParcelTotalPrice&parcelId="+parcelId,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("parcelTotalPrice"+parcelId).innerHTML = msg;
		}
	});
}
function updateMailingBatchPackageList(parcelId){
	$.ajax({
		type: "GET",
		url: "mailingBatch.do?method=updateMailingBatchPackageList&parcelId="+parcelId,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("parcel"+parcelId).innerHTML = msg;
		}
	});
}
function deletePackage(parcelId,packageId){
	$.ajax({
		type: "GET",
		url: "mailingBatch.do?method=deletePackage&packageId="+packageId,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			window.updateMailingBatchOrderCount();
			window.updateMailingBatchTotalWeight();
			window.updateMailingBatchParcelOrderCount(parcelId);
			window.updateMailingBatchParcelTotalWeight(parcelId);
			window.updateMailingBatchParcelTotalPrice(parcelId);
			window.updateMailingBatchPackageList(parcelId);
		}
	});
}
<%if(request.getAttribute("tip")!=null){%>
alert("<%=request.getAttribute("tip")%>");
<%}%>

</script>
</head>
<body bgcolor="#FFCC00">
<table width="99%" border="0">
  <tr>
    <td><strong>发货波次编号:</strong><font color="blue"><%=batch.getCode() %></font></td>
    <td><strong>创建人:</strong><font color="blue"><%=batch.getCreateAdminName() %></font></td>
    <td><strong>承运商:</strong><%=batch.getCarrier() %></td>
    <td><strong>发货状态:</strong><font color='<%=batch.getStatus()==0?"red":"green" %>'><%=batch.getStatusName(batch.getStatus()) %></font></td>
  </tr>
  <tr>
    <td><strong>归属物流渠道:</strong><font color="blue"><%=voOrder.deliverMapAll.get(batch.getDeliver()+"") %></font></td>
    <td><strong>订单总数:</strong><font color="blue"><span id="orderCount"><%=batch.getOrderCount() %></span></font></td>
    <td><strong>总重量:</strong><font color="blue"><span id="totalWeight"><%=batch.getTotalWeight()/1000 %></span>KG</font></td>
    <td><%if(batch.getStatus()==0&&group.isFlag(433)){ %><input type="button" value="交接完成确认" onclick="transit();"><%} %></td>
  </tr>
</table>
<hr/>
<table width="99%" border="0" cellspacing="10">
  <tr>

    <td>
    	<div align="right">
    		<%if(batch.getStatus()==0&&group.isFlag(434)){ %><a href="javascript:addMailingParcel();"><strong>添加发货邮包</strong></a><%} %>&nbsp;&nbsp;&nbsp;&nbsp;
    		<%if(group.isFlag(435)){ %><a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=cargoTransportDeliveryReceiptPrint&id=<%=batch.getCode() %>' target="_blank"><strong>打印承运交接单</strong></a><%} %>
    	</div>
    </td>
  </tr>
  <%for(int i=0;i<parcelList.size();i++){ %>
  	<%MailingBatchParcelBean parcel=(MailingBatchParcelBean)parcelList.get(i); %>
  	<%List packageList=parcel.getMailingBatchPackageList(); %>
  <tr>
  	<td bgcolor="#FFFFCC">
  		<table style="border-style: solid;border-color: black;" width="100%">
  			<tr>
  				<td><strong>发货邮包编号：<font color="blue"><%=parcel.getCode() %></font></strong></td>
  				<td><%if(group.isFlag(436)){ %><a href="javascript:showPackage(<%=parcel.getId() %>);"><font color="blue">查看明细</font></a><%} %></td>
  				<td></td>
  				<td><%if(batch.getStatus()==0&&group.isFlag(437)){ %><input type="button" value="删除邮包" onclick="deleteParcel(<%=parcel.getId() %>);"/><%} %></td>
  			</tr>
  			<tr>
  				<td><strong>订单包裹数量：<font color="blue"><span id="parcelOrderCount<%=parcel.getId() %>"><%=parcel.getPackageCount() %></span></font></strong></td>
  				<td><strong>邮包总重量：<font color="blue"><span id="parcelTotalWeight<%=parcel.getId() %>"><%=parcel.getTotalWeight()/1000 %></span>KG</font></strong></td>
  				<td><a href="<%=request.getContextPath()%>/admin/mailingBatch.do?method=checkParcelWeight&parcelId=<%=parcel.getId() %>" target="_blank" <%if(parcel.getStatus()==1){ %> onclick="return confirm('该邮包已复核过，确定要进行重新复核？');"<%}%>>重量复核</a></td>
  				<td><%if(group.isFlag(438)){ %><input type="button" value="打印邮包条码" onclick="print('<%=parcel.getCode() %>')"><%} %></td>
  			</tr>
  			<tr>
  				<td>发货邮包状态：<font <%if(parcel.getStatus()==1){ %>color="green"<%}else{ %>color="red"<%} %>><%=parcel.getStatusName() %></font></td>
  				<td><strong>代收货款总额：<font color="blue"><span id="parcelTotalPrice<%=parcel.getId() %>"><%=dcmFmt.format(parcel.getTotalPrice()) %></span></font></strong></td>
  				<td></td>
  				<td></td>
  			</tr>
  			<tr>
  				<td colspan="4">
  					<div id="parcel<%=parcel.getId() %>" style="display:none">
  						<hr/>
  						&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%if(batch.getStatus()==0&&group.isFlag(439)){ %><a href="mailingBatch.do?method=toAddPackage&parcelId=<%=parcel.getId() %>" target="_blank"><strong><font color="blue">添加包裹</font></strong></a><%} %>&nbsp;&nbsp;&nbsp;&nbsp;
  						<%if(group.isFlag(440)){ %><a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=mailingParcelDetailPrint&id=<%=parcel.getCode() %>' target="_blank"><strong><font color="blue">打印邮包明细单</font></strong></a><%} %>&nbsp;&nbsp;
  						                           <a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=mailingParcelDetailExcel&id=<%=parcel.getCode() %>' target="_blank"><strong><font color="blue">导出邮包明细单</font></strong></a><br/>
  						<table border="1" bordercolor="#00000" cellspacing="0" width="100%" >
  							<tr bgcolor="#00ccff" align="center">
  								<td>序号</td>
  								<td>订单号</td>
  								<td>包裹单号</td>
  								<td>添加时间</td>
  								<td>归属物流</td>
  								<td>收件地址</td>
  								<td>订单金额</td>
  								<td>付款方式</td>
  								<td>包裹重量</td>
  								<td>操作</td>
  							</tr>
  							
  							<%for(int j=0;j<packageList.size();j++){ %>
  								<%MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j); %>
  							<tr bgcolor="#ffffff" align="center">
  								<td><%=j+1 %></td>
  								<td><%if(group.isFlag(441)){ %><a href="<%=request.getContextPath()%>/admin/order.do?id=<%=packageBean.getOrderId() %>" target="_blank"><%=packageBean.getOrderCode() %></a><%}else{ %><%=packageBean.getOrderCode() %><%} %></td>
  								<td><%=packageBean.getPackageCode()%></td>
  								<td><%=packageBean.getCreateDatetime().substring(0,19) %></td>
  								<td><%=voOrder.deliverMapAll.get(packageBean.getDeliver()+"") %></td>
  								<td><%=packageBean.getAddress().indexOf("自治区")>0?packageBean.getAddress().substring(0,12):packageBean.getAddress().substring(0,8) %></td>
  								<td><%=dcmFmt.format(packageBean.getTotalPrice()) %></td>
  								<td><%if(packageBean.getBuyMode()==0){%>货到付款<%} else{%>已到款<%} %></td>
  								<td><%=packageBean.getWeight()/1000 %>KG</td>
  								<td>
  									<%if(batch.getStatus()==0){ %>
  										<%if(group.isFlag(442)){ %><a href="javascript:deletePackage(<%=parcel.getId() %>,<%=packageBean.getId() %>);">删除</a>&nbsp;&nbsp;<%} %>
  										<%if(group.isFlag(443)){ %><a href="mailingBatch.do?method=toChangePackageWeight&packageId=<%=packageBean.getId() %>" target="_blank">重量修正</a><%} %>
  									<%} %>
  								</td>
  							</tr>
  							<%} %>
  						</table>
  					</div>
  				</td>
  			</tr>
  		</table>
  	</td>
  </tr>
  <%} %>
</table>
	<%if(pageBean!=null){ %>
		<p align="center"><%=PageUtil.fenye(pageBean, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
</body>
</html>
<%@ page contentType="text/html;charset=utf-8"%>
<%@page import="java.util.List"%>
<%@page import="adultadmin.bean.stock.MailingBatchPackageBean"%>
<%@page import="adultadmin.action.vo.voOrder"%>
<%@page import="adultadmin.bean.stock.MailingBatchBean"%>
<%@page import="adultadmin.bean.stock.MailingBatchParcelBean"%>
<%@page import="adultadmin.action.vo.voUser"%>
<%@page import="adultadmin.bean.UserGroupBean"%>
<%@page import="java.text.DecimalFormat"%>
<%
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
MailingBatchBean batch=(MailingBatchBean)request.getAttribute("batch");
MailingBatchParcelBean parcel=(MailingBatchParcelBean)request.getAttribute("parcel");
DecimalFormat dcmFmt = new DecimalFormat("0.00");
%>



<hr/>
  						<%if(batch.getStatus()==0&&group.isFlag(439)){ %><a href="mailingBatch.do?method=toAddPackage&parcelId=<%=parcel.getId() %>" target="_blank">添加包裹</a><%} %>&nbsp;&nbsp;&nbsp;&nbsp;
  						<%if(group.isFlag(440)){ %><a href='<%=request.getContextPath()%>/admin/mailingBatch.do?method=mailingParcelDetailPrint&id=<%=parcel.getCode() %>' target="_blank"><strong><font color="blue">打印邮包明细单</font></strong></a><%} %><br/>
  						<table border="1" bordercolor="#00000" cellspacing="0" width="100%">
  							<tr bgcolor="#00ccff" align="center">
  								<td>序号</td>
  								<td>订单号</td>
  								<td>添加时间</td>
  								<td>归属物流</td>
  								<td>收件地址</td>
  								<td>订单总价</td>
  								<td>包裹重量</td>
  								<td>操作</td>
  							</tr>
  							<%List packageList=parcel.getMailingBatchPackageList(); %>
  							<%for(int j=0;j<packageList.size();j++){ %>
  								<%MailingBatchPackageBean packageBean=(MailingBatchPackageBean)packageList.get(j); %>
  							<tr bgcolor="#ffffff" align="center">
  								<td><%=j+1 %></td>
  								<td><%if(group.isFlag(441)){ %><a href="<%=request.getContextPath()%>/admin/order.do?id=<%=packageBean.getOrderId() %>" target="_blank"><%=packageBean.getOrderCode() %></a><%}else{ %><%=packageBean.getOrderCode() %><%} %></td>
  								<td><%=packageBean.getCreateDatetime().substring(0,19) %></td>
  								<td><%=voOrder.deliverMapAll.get(packageBean.getDeliver()+"") %></td>
  								<td><%=packageBean.getAddress().indexOf("自治区")>0?packageBean.getAddress().substring(0,12):packageBean.getAddress().substring(0,8) %></td>
  								<td><%=dcmFmt.format(packageBean.getTotalPrice()) %></td>
  								<td><%=packageBean.getWeight()/1000 %>KG</td>
  								<td>
  									<%if(batch.getStatus()==0){ %>
  										<a href="javascript:deletePackage(<%=parcel.getId() %>,<%=packageBean.getId() %>);">删除</a>&nbsp;&nbsp;
  										<a href="mailingBatch.do?method=toChangePackageWeight&packageId=<%=packageBean.getId() %>" target="_blank">重量修正</a>
  									<%} %>
  								</td>
  							</tr>
  							<%} %>
  						</table>
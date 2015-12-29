<%@page import="org.apache.commons.collections.map.LinkedMap"%>
<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.PagingBean"%>
<%@ page import="mmb.stock.stat.*,adultadmin.bean.stock.*,adultadmin.bean.bybs.*,adultadmin.bean.PagingBean,
				adultadmin.bean.stat.WarehousingAbnormalBean,adultadmin.util.*,adultadmin.bean.*,adultadmin.action.vo.voOrder,
				adultadmin.bean.bybs.BsbyOperationnoteBean"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<title>入库异常单列表</title>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="javascript" type="text/javascript"src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<%
	PagingBean pageBean = (PagingBean)request.getAttribute("pageBean");
	List<WarehousingAbnormalBean> abnormalList = (ArrayList<WarehousingAbnormalBean>)request.getAttribute("abnormalList");
	List<WarehousingAbnormalBean> bsbyList = (ArrayList<WarehousingAbnormalBean>)request.getAttribute("bsbyList");
	voUser user = (voUser) request.getSession().getAttribute("userView");
	String recordNum = (String)request.getAttribute("recordNum");
	UserGroupBean group = user.getGroup();
	String bsbyResult = StringUtil.convertNull((String)request.getAttribute("bsbyResult"));
	String startTime = StringUtil.convertNull((String)request.getAttribute("startTime"));
	String endTime = StringUtil.convertNull((String)request.getAttribute("endTime"));
	String deliver = StringUtil.convertNull((String)request.getAttribute("deliver" ));
	String status = StringUtil.convertNull((String)request.getAttribute("status"));
	String area = StringUtil.convertNull((String)request.getAttribute("area"));
%>
<script type="text/javascript">
$(function(){
	<%if(!"".equals(bsbyResult)){%>
		alert("<%=bsbyResult%>");
	<%}%>
});
$(document).ready(function(){
     $('#add').click(function(){
	     window.location='<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=openAddWareAbnoraml';
     });
});
</script>
</head>
<body >
<div align="center">
		<h2>异常入库单列表</h2>
</div>
<div style="float:right">共有（<%= recordNum %>）条记录&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; </div>
<br/>
	<div align="center">
		<form action="<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=selectAbnormalList" method="post">
			<table style="border-style: solid; border-color: #000000; border-width: 1px; width: 98%; border: 1px dotted #000000" align="center" bgcolor="EE9A49">
				<tr>
					<td align="right" >异常入库单:</td>
					<td align="left" ><input type="text"  name="abnormalCode"  value="" /></td>
					<td align="right" >添加时间:</td>
					<td align="left" ><input type="text" size="15" name="startTime" value="<%=startTime %>" onclick="WdatePicker();" /> &nbsp;
					到&nbsp; <input type="text" size="15" name="endTime" value="<%=endTime %>" onclick="WdatePicker();" /></td>
					<td align="right" >快递公司: </td>
					<td align="left" ><select name="deliver">
					<% Map<String,String> deliverMapAll = (Map<String,String>)new voOrder().getDeliverMapAll();
							for (Map.Entry<String, String> entry : deliverMapAll.entrySet()) {%>
							<option value="<%=entry.getKey()%>" <%if(!"".equals(deliver)){ if(deliver.equals(entry.getKey()+"")){%> selected="selected" <%}} %>><%=entry.getValue() %></option>
							<%} %>
					</select></td>
				</tr>
				<tr>
					<td align="right" >订单号: </td>
					<td align="left" ><input type="text" size=""name="orderCode"  value="" /></td>
					<td align=right >包裹单号:</td>
					<td align="left"><input type="text" size=""name="packageCode"  value="" /></td>
					<td align="right" >异常单状态:</td>
					<td align="left" >
					<select name="status">
					<%Map<Integer,String> statusMap = new WarehousingAbnormalBean().getStatusMap();
						for (Map.Entry<Integer, String> entry : statusMap.entrySet()) {%>
							<option  value="<%=entry.getKey()%>"  <%if(!"".equals(status)){ if(status.equals(entry.getKey()+"")){%> selected="selected" <%}} %>><%=entry.getValue() %></option>
					<%} %>							  
					</select></td>
				</tr >
				<tr >
					<td align=right >添加人:</td>
					<td align="left" ><input type="text" size=""name="operator"  value="" /></td>	
					<td align="right" >库地区: </td>
					<td align="left" >
						<%if(!"".equals(area) && !"-1".equals(area) && !area.contains(",")){ 
							String whAreaSelection = WarehousingAbnormalService.getWeraAreaOptions(request,Integer.parseInt(area),false);%>
							<%=whAreaSelection %>
						<%}else{ 
							String whAreaSelection = WarehousingAbnormalService.getWeraAreaOptions(request); %>
							<%=whAreaSelection %>
						<%} %>
						</td>
					<td align="right" > &nbsp; </td>
					<td align="left">&nbsp; </td>
				</tr >
				<tr >
					<td align="right" >&nbsp; </td>
					<td align="left" >&nbsp; </td>
					<td align="right" >&nbsp; </td>
					<td align="left" >&nbsp;  </td>
					<td align="right" >&nbsp; </td>
					<td align="left"><input type="submit" value="查  询" style="width: 65px;height: 22px" /></td>
				</tr>
				
			</table>
			<br>
			<div align="left">
			<%if(group.isFlag(754)){ %>
				<input type="button" value="添加异常入库单"  id="add"/>
			<%} %> </div>
			<table border="1"   width="98%" align="center"  cellspacing="0" bgcolor="FFFFE0">
				<tr style="font-weight: bold;" bgcolor="#00ccff" height="25xp">
					<td align=center width="9%">异常入库单</td>
					<td align=center width="9%">订单号/出库单</td>
					<td align=center width="5%">包裹单</td>
					<td align=center width="8%">快递公司</td>
					<td align=center width="7%">发货日期</td>
					<td align=center width="7%">添加人</td>
					<td align=center width="7%">添加时间</td>
					<td align=center width="4%">状态</td>
					<td align=center width="10%">报损报溢单</td>
					<td align=center width="6%">操作</td>
				</tr>
				<% if(abnormalList != null && abnormalList.size()>0){
						for(WarehousingAbnormalBean bean : abnormalList){ %>
				<tr >
					<td align=center width="10%" >
						<a href="<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=readyEditWarehousingAbnormal&abnormalId=<%=bean.getId()%>"><%=bean.getCode()%></a>
					</td>
					<td align=center ><%=bean.getOrderCode() %></td>
					<td align=center ><%if(!"".equals( bean.getPackageCode())){%><%=bean.getPackageCode() %><%}else{%>无<%}%></td>
					<td align=center ><%=voOrder.deliverMapAll.get(bean.getDeliver()+"") %></td>
					<td align=center ><%=bean.getSortingDatetime() %></td>
					<td align=center ><%=bean.getOperatorName() %></td>
					<td align=center ><%=bean.getCreateTime() %></td>
					<td align=center ><%=WarehousingAbnormalBean.statusMap.get(bean.getStatus()) %></td>
					<td align=center >
					<%if(bsbyList != null && bsbyList.size()>0){
						for(WarehousingAbnormalBean bsbyBean : bsbyList){ 
							if(bean.getId() == bsbyBean.getId()){%>
							<% int type = bsbyBean.getBsbyStatus();
					  		   if(bsbyBean.getReceiptsNumber() != null){ 
									if((type==0||type==1||type==2||type==5)&&(user.getId()==bsbyBean.getBsbyOperatorId()||group.isFlag(413))){%>
									<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%= bsbyBean.getBsbyId()%>" target="_blank"><%= bsbyBean.getReceiptsNumber()%></a><br/>
									<%}else if(type==6&&(user.getId()==bean.getBsbyOperatorId()||group.isFlag(229))){%>
									<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&opid=<%= bsbyBean.getBsbyId()%>" target="_blank"><%= bsbyBean.getReceiptsNumber()%></a><br/>
									<%}else {%>
									<a href="<%=request.getContextPath()%>/admin/bybs.do?method=getByOpid&lookup=1&opid=<%= bsbyBean.getBsbyId()%>" target="_blank"><%= bsbyBean.getReceiptsNumber()%></a><br/>
									<%}
								}
					  		}
						}
					}%>
					</td>
					<td align=center ><%if(bean.getStatus()==0){ 
													if(group.isFlag(755)){ %>
														<a href="<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=readyEditWarehousingAbnormal&abnormalId=<%=bean.getId() %>">编辑</a>&nbsp;
														<a href="<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=delWarehousingAbnormal&abnormalId=<%=bean.getId() %>">删除</a>
													<%}else{ %>
														编辑&nbsp;删除
													<%} 
												}else if(bean.getStatus()==1){
														if(group.isFlag(858)){ %>
															<a href="<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=readyEditWarehousingAbnormal&auditFlg=1&abnormalId=<%=bean.getId() %>">审核</a>&nbsp;
													   <%}else{ %>
															审核
													   <%}
												}else{ %>
													<a href="<%=request.getContextPath()%>/admin/warehousingAbnormalAction.do?method=printWarehousingAbnormal&abnormalId=<%=bean.getId() %>&bsbyId=<%=bean.getBsbyId()%>">打印</a>
												<%}%> </td>
				</tr>
				<%}} %>
			</table>
		</form>
		<%if (pageBean!=null){%>
		<p align="center"><%=PageUtil.fenye(pageBean, true, "&nbsp;&nbsp;", "pageIndex", 10)%></p>
		<%} %>
	</div>
</body>
</html>

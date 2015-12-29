<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.bean.cargo.*"%>
<%@ page import="adultadmin.action.vo.voUser"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%	
	List inCocList=(List)request.getAttribute("inCocList");
	List outCocList=(List)request.getAttribute("outCocList");
	CargoOperationBean coBean=new CargoOperationBean();
	if(request.getAttribute("coBean")!=null){
		coBean=(CargoOperationBean)request.getAttribute("coBean");
	}
	CargoOperationProcessBean nextStatus=(CargoOperationProcessBean)request.getAttribute("nextStatus");
	if(request.getAttribute("effectTime")!=null){
		String effectTime=request.getAttribute("effectTime").toString();
	}
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
	<head>
		<title>买卖宝后台</title>
		<script type="text/javascript"
			src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
		<script language="JavaScript"
			src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
		<script language="JavaScript"
			src="<%=request.getContextPath()%>/js/jquery.js"></script>
		<link href="<%=request.getContextPath()%>/css/global.css"
			rel="stylesheet" type="text/css">
		<script type="text/javascript">
			<%if(request.getAttribute("delete")!=null){%>
				alert("删除成功！");
			<%}%>
			<%if(request.getAttribute("success")!=null){%>
				alert("操作成功！");
			<%}%>
		</script>
		
		<script type="text/javascript">
		function confirmSubmit(){
			document.getElementById('action').value = 'confirm';
			document.cargoOperationForm.submit();
		}
		function editSubmit(){
			document.getElementById('action').value = 'edit';
			document.cargoOperationForm.submit();
		}
		function auditingSubmit(){
			document.getElementById('method').value = 'auditingExchangeCargo';
			document.cargoOperationForm.submit();
		}
		function completeSubmit(){
			document.getElementById('method').value = 'completeExchangeCargo';
			document.cargoOperationForm.submit();
		}
		function checkAll(name,name2) {     
		    var checkChagen =document.getElementsByName(name);
		    var cargoProducId = document.getElementsByName(name2);
		    for(var i=0;i<cargoProducId.length;i++){
		    	cargoProducId[i].checked =checkChagen[0].checked ;
		    }
		}
		function checkSubmit(){
			document.getElementById('method').value = 'checkExchangeCargo';
			document.cargoOperationForm.submit();
		}
		</script>
	</head>
	<body>
<%@include file="../../header.jsp"%>
<p>作业单操作页</p>
作业单编号：<%=coBean!=null?coBean.getCode():"" %>&nbsp;&nbsp;&nbsp;
作业单状态：<font color="red"><%=coBean.getStatusName() %></font>&nbsp;&nbsp;&nbsp;
<%if(nextStatus!=null){ %>
  	 <%if(((nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS30&&group.isFlag(534))
  	 		||(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS31&&group.isFlag(535))
  	 		||(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS32&&group.isFlag(536))
  	 		||(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS33&&group.isFlag(537)))
			&&(coBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS3
	 	  	 	  		&&coBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS4)){ %>
  	 	<input type="button" value="<%=nextStatus.getOperName()%>" onclick="auditingSubmit();"/>
  	 <%}else if(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS34&&(coBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS3
				&&coBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS4)&&group.isFlag(541)){ %>
  	 				<input type="button" value="<%=nextStatus.getOperName()%>" onclick="completeSubmit();"/>
  	 <%} %>
<%} %>
<a href="../admin/cargoOperation.do?method=printExchangeCargo&id=<%=coBean.getId()%>" target="_blank">打印作业单</a>&nbsp;&nbsp;&nbsp;
<a href="../admin/cargoOperation.do?method=operationLog&operId=<%=coBean.getId() %>" target="_blank">人员操作记录</a>&nbsp;&nbsp;&nbsp;
  当前时效状态：<%=coBean.getEffectTimeName() %>
<br/>
作业单类型：货位间调拨（散件区—>散件区或整件区—>整件区）<br/>
<form action="../admin/cargoOperation.do?operId=<%=coBean.getId() %>" name="cargoOperationForm" method="post">
<table cellpadding="3" cellspacing="1" border=1>
	<tr  align="center">
		<td>序号</td>
		<td>产品原名称</td>
		<td>产品编号</td>
		<td>源货位号</td>
		<td>源货位量（其中冻结量）</td>
		<td>目的货位号及作业量</td>
		<%if(coBean.getStatus()==0||coBean.getStatus()==1){ %>
			<td>操作</td>
		<%} %>
	</tr>
	<%for(int i=0;i<outCocList.size();i++){ %>
		<%List inCocList2=(List)inCocList.get(i); %>
		<%CargoOperationCargoBean outCocBean=(CargoOperationCargoBean)outCocList.get(i); %>
		<tr align="center">
			<td><%=i+1 %></td>
			<td><a href="../admin/fproduct.do?id=<%=outCocBean.getProductId() %>" target="_blank"><%=outCocBean.getProduct().getOriname() %></a></td>
			<td><a href="../admin/fproduct.do?id=<%=outCocBean.getProductId() %>" target="_blank"><%=outCocBean.getProduct().getCode() %></a></td>
			<td><b><%=outCocBean.getOutCargoWholeCode() %></b></td>
			<td><%=outCocBean.getCargoProductStock().getStockCount()+outCocBean.getCargoProductStock().getStockLockCount() %>(<%if(outCocBean.getCargoProductStock().getStockLockCount()!=0){ %><a href="cargoOperation.do?method=stockExchangeList&cargoWholeCode=<%=outCocBean.getOutCargoWholeCode() %>&productCode=<%=outCocBean.getProduct().getCode() %>&type=stockLock" target="_blank"><%=outCocBean.getCargoProductStock().getStockLockCount() %></a><%}else{ %>0<%} %>)</td>
			<td><%if(((List)(inCocList.get(i))).size()==0&&(coBean.getStatus()==0||coBean.getStatus()==1)){ %>
				请单击‘分配货位’，选择目的货位
				<%}else{ %>
				<table border=1>
					<tr align="center">
						<%if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS28){ %>
							<td>选<input name="checkAll<%=outCocBean.getOutCargoProductStockId() %>" type="checkbox" onclick="checkAll('checkAll<%=outCocBean.getOutCargoProductStockId() %>','cocId<%=outCocBean.getId() %>');"/></td>
						<%} %>
						<td>存放类型</td>
						<td>货位号</td>
						<td>当前库存量</td>
						<td>空间冻结</td>
						<td>货位最大容量</td>
						<td>本次作业量</td>
					</tr>
					<%for(int j=0;j<inCocList2.size();j++){ %>
						<%CargoOperationCargoBean inCocBean=(CargoOperationCargoBean)inCocList2.get(j);
						CargoProductStockBean subCpsBean =  inCocBean.getCargoProductStock();
						if(subCpsBean==null) subCpsBean= new CargoProductStockBean();
						CargoInfoBean cargoInfoBean = inCocBean.getCargoInfo();%>
						<tr align="center">
							<%if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS28){ %>
								<td><input type="checkbox" name="cocId<%=outCocBean.getId() %>" value="<%=inCocBean.getId() %>" <%if(inCocBean.getUseStatus()==1){ %>checked=checked<%} %>/></td>
							<%} %>
							<td><%=inCocBean.getCargoInfo().getStoreTypeName() %>
							</td>
							<td <%if(inCocBean.getStockCount()==0&&inCocBean.getUseStatus()==1){ %>style="color:red;"<%} %>><b><%=inCocBean.getInCargoWholeCode() %></b></td>
							<td><%=inCocBean.getCargoProductStock().getStockCount()+inCocBean.getCargoProductStock().getStockLockCount() %></td>
							<td><%if(inCocBean.getCargoInfo().getSpaceLockCount()!=0){%><a href="cargoOperation.do?method=stockExchangeList&cargoWholeCode=<%=inCocBean.getInCargoWholeCode() %>&productCode=<%=inCocBean.getProduct().getCode() %>&type=spaceLock" target="_blank"><%=inCocBean.getCargoInfo().getSpaceLockCount() %></a><%}else{ %>0<%} %></td>
							<td><%=inCocBean.getCargoInfo().getMaxStockCount() %></td>
							<%if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS28){ %>
								<td><input type="text" name="exchangeCount<%=inCocBean.getId() %>" size=5 value="<%=inCocBean.getStockCount() %>"/></td>
							<%}else{ %>
								<td><%=inCocBean.getStockCount() %></td>
							<%} %>
						</tr>
						
					<%} %>
					<tr align="center">
						<td></td>
						<td>合计</td>
						<td></td>
						<%if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS28){ %>
						<td></td>
						<%} %>
						<td></td>
						<td></td>
						<td>
						<%int total=0; %>
						<%for(int j=0;j<inCocList2.size();j++){
							total+=(((CargoOperationCargoBean)inCocList2.get(j)).getStockCount());%>
						<%} %>
						<%=total%></td>
					</tr>
				</table>
				<%} %>
			</td>
			<%if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS28){ %>
				<td><a onclick="javascript:return confirm('如果确认删除，请单击‘确定’，反之，请单击‘取消’！')" href="../admin/cargoOperation.do?method=deleteExchangeCargoProduct&id=<%=outCocBean.getId() %>&operId=<%=coBean.getId() %>">删除</a>&nbsp;&nbsp;&nbsp;
					<a href="../admin/cargoOperation.do?method=exchangeUsefulCargo&operId=<%=coBean.getId() %>&outCpsId=<%=outCocBean.getOutCargoProductStockId() %>&storeType=<%=outCocBean.getCargoInfo().getStoreType() %>">分配货位</a></td>
			<%} %>
		</tr>
		<%} %>
	
</table>
 <%if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS28&&group.isFlag(520)){ %>
  		<input type="submit" value="保存编辑" onclick="return editSubmit();"/>
<%}%>
 <%if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS28&&group.isFlag(521)){ %>
  		&nbsp;&nbsp;&nbsp;<input type="button" value="确认提交" onclick="return confirmSubmit();"/>
<%}%>
<%if(nextStatus!=null){ %>
			<input type="hidden" name="nextStatus" value="<%=nextStatus.getId() %>" />
<%} %>
<%--
<%else if(coBean.getStatus() == CargoOperationBean.STATUS2){ %>
  		审核状态：<input type="radio" name="status" value="0" checked="checked"/>审核通过&nbsp;&nbsp;<input type="radio" name="status" value="1"/>审核不通过<br/>
  		批注：<textarea name="remark"><%=coBean.getRemark() %></textarea><br/>
  		<input type="button" value="提交" onclick="return auditingSubmit();"/>
<%}else if(coBean.getStatus() == CargoOperationBean.STATUS3){ %>
  		作业完成情况：<input type="radio" name="status" value="0" checked="checked"/>上架成功&nbsp;&nbsp;<input type="radio" name="status" value="1"/>上架失败<br/>
  		批注：<textarea name="remark"><%=coBean.getRemark() %></textarea><br/>
  		<input type="button" value="提交" onclick="return completeSubmit();"/>
<%} %>
 --%>
<%if(coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS28
  	&&coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS34
  	&&coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS35
  	&&coBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS36
	&&coBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS3
	&&coBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS4&&group.isFlag(542)){ %>
  	作业复核：
  	<input type="radio" name="status" value="3" checked="checked"/>作业成功&nbsp;&nbsp;
  	<input type="radio" name="status" value="4"/>作业失败<br/>
  	备注：<textarea name="remark"><%=coBean.getRemark() %></textarea>
  	<input type="button" value="复核提交" onclick="return checkSubmit();"/>
<%} %>
<input type="hidden" name="operId" value="<%=coBean.getId()%>"/>
<input type="hidden" id="action" name="action" value="edit">
<input type="hidden" id="method" name="method" value="editExchangeCargo"/>
</form>
<%if(coBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS28){ %>
<p>注：1、如果要保存编辑的信息，请单击‘保存编辑’。如果想提交审核，请单击‘确认提交’</p>
<%} %>
<%@include file="../../footer.jsp"%>
	</body>
</html>
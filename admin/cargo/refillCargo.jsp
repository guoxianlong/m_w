<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*, adultadmin.bean.cargo.*, adultadmin.action.vo.*,adultadmin.util.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="ormap.ProductLineMap"%>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
	
	CargoOperationBean cargoOperation = (CargoOperationBean)request.getAttribute("cargoOperation");
	List cocList = (List)request.getAttribute("cocList");
	CargoOperationProcessBean nextStatus=(CargoOperationProcessBean)request.getAttribute("nextStatus");
	String effectTime=request.getAttribute("effectTime").toString();
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script>
function checkAll(name,name2) {     
    var checkChagen =document.getElementsByName(name);
    var cargoProducId = document.getElementsByName(name2);
    for(var i=0;i<cargoProducId.length;i++){
    	cargoProducId[i].checked =checkChagen[0].checked ;
    }
}

function confirmSubmit(){
	document.getElementById('action').value = 'confirm';
	document.cargoOperationForm.submit();
}
function editSubmit(){
	document.getElementById('action').value = 'edit';
	document.cargoOperationForm.submit();
}
function auditingSubmit(){
	document.getElementById('method').value = 'auditingRefillCargo';
	document.cargoOperationForm.submit();
}
function completeSubmit(){
	document.getElementById('method').value = 'completeRefillCargo';
	document.cargoOperationForm.submit();
}
function checkSubmit(){
	document.getElementById('method').value = 'checkRefillCargo';
	document.cargoOperationForm.submit();
}
</script>
<form method="post" action="cargoOperation.do" name="cargoOperationForm">
作业单操作页<br/>
作业单编号：<%=cargoOperation.getCode() %>&nbsp;&nbsp;
作业单状态：<font color="red"><%=cargoOperation.getStatusName() %></font>&nbsp;&nbsp;
<%if(nextStatus!=null){ %>
  	 <%if(((nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS21&&group.isFlag(530))
  	 		||(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS22&&group.isFlag(531))
  	 		||(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS23&&group.isFlag(532))
  	 		||(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS24&&group.isFlag(533)))
			&&(cargoOperation.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS3
	 	  				&&cargoOperation.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS4)){ %>
  	 	<input type="button" value="<%=nextStatus.getOperName()%>" onclick="auditingSubmit();"/>
  	 <%}else if(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS25&&(cargoOperation.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS3
  				&&cargoOperation.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS4&&group.isFlag(540))){ %>
  	 	<input type="button" value="<%=nextStatus.getOperName()%>" onclick="completeSubmit();"/>
  	 <%} %>
<%} %>
<a href="../admin/cargoOperation.do?method=printRefillCargo&id=<%=cargoOperation.getId()%>" target="_blank">打印作业单</a>&nbsp;&nbsp;&nbsp;&nbsp;
<a href="../admin/cargoOperation.do?method=operationLog&operId=<%=cargoOperation.getId() %>" target="_blank">人员操作记录</a>&nbsp;&nbsp;&nbsp;&nbsp;
当前时效状态：<%=cargoOperation.getEffectTimeName() %><br/>
作业单类型：<%=cargoOperation.getTypeName() %>(<%=CargoInfoBean.storeTypeMap.get(Integer.valueOf(cargoOperation.getStockOutType())) %>&nbsp;——>&nbsp;<%=CargoInfoBean.storeTypeMap.get(Integer.valueOf(cargoOperation.getStockInType())) %>)
<table width="95%" border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5">
<tr>
  <td align="center">序号</td>
  <td align="center">产品编号</td>
  <td align="center">产品原名称</td>
  <td align="center">混合区货位号</td>
  <td align="center">货位库存</td>
  <td align="center">空间冻结</td>
  <td align="center">警戒线</td>
  <td align="center">最大容量</td>
  <td align="center">整件区货位号以及作业量</td>
  <td align="center">操作</td>
</tr>
<%
	if(cocList!=null){
		for(int i=0;i<cocList.size();i++){
			CargoOperationCargoBean inCoc = (CargoOperationCargoBean)cocList.get(i);
%>
<tr>
  <td align="center"><%=i+1 %></td>
  <td align="center"><a href="fproduct.do?id=<%=inCoc.getProductId() %>" target="_blank"><%=inCoc.getProduct().getCode() %></a></td>
  <td align="center"><a href="fproduct.do?id=<%=inCoc.getProductId() %>" target="_blank"><%=inCoc.getProduct().getOriname() %></a></td>
  <td align="center"><%=inCoc.getCargoInfo().getWholeCode() %></td>
  <td align="center"><%=inCoc.getCargoProductStock()==null?0:(inCoc.getCargoProductStock().getStockCount()+inCoc.getCargoProductStock().getStockLockCount()) %></td>
  <td align="center"><%if(inCoc.getCargoInfo().getSpaceLockCount()!=0){ %><a href="cargoOperation.do?method=stockExchangeList&cargoWholeCode=<%=inCoc.getInCargoWholeCode() %>&productCode=<%=inCoc.getProduct().getCode() %>&type=spaceLock" target="_blank"><%=inCoc.getCargoInfo().getSpaceLockCount() %></a><%}else{ %>0<%} %></td>
  <td align="center"><%=inCoc.getCargoInfo().getWarnStockCount() %></td>
  <td align="center"><%=inCoc.getCargoInfo().getMaxStockCount() %></td>
  <td align="center">
  	<table width="95%" border="1" style="border-collapse:collapse;" bordercolor="#D8D8D5">
  		<tr>
  			<td align="center">选<input type="checkBox" name="checkID<%=inCoc.getInCargoProductStockId() %>" id="checkID<%=inCoc.getInCargoProductStockId() %>" onclick="checkAll('checkID<%=inCoc.getInCargoProductStockId() %>','cargoOperationCargoId<%=inCoc.getInCargoProductStockId() %>')" <%if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS19){ %>disabled="disabled"<%} %>/></td>
  			<td align="center">货位号</td>
  			<td align="center">本次补货量</td>
  			<td align="center">该货位库存(冻结量)</td>
		</tr>
		<%
			List list = inCoc.getCocList();
			Iterator iter = list.listIterator();
			while(iter.hasNext()){
				CargoOperationCargoBean outCoc = (CargoOperationCargoBean)iter.next();
				CargoProductStockBean subCpsBean =  outCoc.getCargoProductStock();
				if(subCpsBean==null) subCpsBean= new CargoProductStockBean();
				CargoInfoBean cargoInfoBean = outCoc.getCargoInfo();
		%>
		<tr>
  			<td align="center"><input type="checkbox" id="cargoOperationCargoId<%=inCoc.getInCargoProductStockId() %>" name="cargoOperationCargoId<%=inCoc.getInCargoProductStockId() %>" value="<%=outCoc.getId()%>" <%if(outCoc.getUseStatus()==1){ %>checked="checked"<%} %> <%if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS19){ %>disabled="disabled"<%} %>/></td>
  			<td align="center" <%if(outCoc.getStockCount()==0&&outCoc.getUseStatus()==1){ %>style="color:red;"<%} %>><%=outCoc.getCargoInfo().getWholeCode() %></td>
  			<td align="center"><input type="text" size="5" name="refillCount<%=outCoc.getId() %>" value="<%=outCoc.getStockCount() %>" <%if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS19){ %>disabled="disabled"<%} %>/></td>
  			<td align="center"><%=outCoc.getCargoProductStock()==null?0:outCoc.getCargoProductStock().getStockCount()%>(<%if(outCoc.getCargoProductStock()!=null&&outCoc.getCargoProductStock().getStockLockCount()!=0){%><a href="cargoOperation.do?method=stockExchangeList&cargoWholeCode=<%=outCoc.getOutCargoWholeCode() %>&productCode=<%=outCoc.getProduct().getCode() %>&type=stockLock" target="_blank"><%=outCoc.getCargoProductStock().getStockLockCount()%></a><%}else{ %>0<%} %>)</td>
		</tr>
		<%
			}
		%>
  	</table>
  </td>
  <td align="center"><%if(cargoOperation.getStatus() == CargoOperationProcessBean.OPERATION_STATUS19){ %><a href="cargoOperation.do?method=deleteRefillCargoProduct&id=<%=inCoc.getId() %>" onclick="javascript:return confirm('如果确认删除，请单击‘确定’，反之，请单击‘取消’！')">删除</a><%} %></td>
</tr>
<%
		}
	}
%>
<tr>
  <td colspan="10" align="left">
  		<%if(nextStatus!=null){ %>
			<input type="hidden" name="nextStatus" value="<%=nextStatus.getId() %>" />
		<%} %>
  		<%if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS19&&group.isFlag(518)){ %>
  			<input type="submit" value="保存编辑" onclick="return editSubmit();"/>
  		<%}%>
  		<%if(cargoOperation.getStatus()==CargoOperationProcessBean.OPERATION_STATUS19&&group.isFlag(519)){ %>
  			&nbsp;&nbsp;&nbsp;<input type="button" value="确认提交" onclick="return confirmSubmit();"/>
  		<%}%>
  		<%--
  		<%else if(cargoOperation.getStatus() == CargoOperationBean.STATUS2){ %>
  		审核状态：<input type="radio" name="status" value="0" checked="checked"/>审核通过&nbsp;&nbsp;<input type="radio" name="status" value="1"/>审核不通过<br/>
  		批注：<textarea name="remark"><%=cargoOperation.getRemark() %></textarea><br/>
  		<input type="button" value="提交" onclick="return auditingSubmit();"/>
  		<%}else if(cargoOperation.getStatus() == CargoOperationBean.STATUS3){ %>
  		作业完成情况：<input type="radio" name="status" value="0" checked="checked"/>上架成功&nbsp;&nbsp;<input type="radio" name="status" value="1"/>上架失败<br/>
  		批注：<textarea name="remark"><%=cargoOperation.getRemark() %></textarea><br/>
  		<input type="button" value="提交" onclick="return completeSubmit();"/>
  		<%} %>
  		 --%>
  		 <%if(cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS19
  				&&cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS25
  				&&cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS26
  				&&cargoOperation.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS27
  				&&cargoOperation.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS3
  				&&cargoOperation.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS4&&group.isFlag(542)){ %>
  			作业复核：
  			<input type="radio" name="status" value="3" checked="checked"/>作业成功&nbsp;&nbsp;
  			<input type="radio" name="status" value="4"/>作业失败<br/>
  			备注：<textarea name="remark"><%=cargoOperation.getRemark() %></textarea>
  			<input type="button" value="复核提交" onclick="return checkSubmit();"/>
  		<%} %>
  </td>
</tr>
</table>
<input type="hidden" name="operId" value="<%=cargoOperation.getId()%>"/>
<input type="hidden" name="method" value="editRefillCargo"/>
<input type="hidden" id="action" name="action" value="edit"/>
</form>

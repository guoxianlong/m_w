<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*,adultadmin.util.*,adultadmin.action.vo.voProduct" %>
<%@ page import="adultadmin.action.vo.voUser" %>
<%@ page import="adultadmin.bean.UserGroupBean" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
CargoOperationBean operBean  = (CargoOperationBean)request.getAttribute("cargoOperation");
CargoInfoStorageBean storageBean=(CargoInfoStorageBean)request.getAttribute("storageBean");
List cocList = (List)request.getAttribute("cocList");
List currentStockCountList = (List)request.getAttribute("currentStockCountList");
HashMap otherCargoMap=(HashMap)request.getAttribute("otherCargoMap");
CargoOperationProcessBean nextStatus=(CargoOperationProcessBean)request.getAttribute("nextStatus");
voUser user = (voUser) request.getSession().getAttribute("userView");
UserGroupBean group = user.getGroup();
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    <title>上架单详细页</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
 	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
 	<script type="text/javascript">
 		function checkAll(names) {     
		    var ckeckBoxAll = document.getElementsByName(names)
    		for(var j=0;j<ckeckBoxAll.length;j++){
				//cargoProducId[i].checked = ckeckBoxAll[j].checked;
				if(ckeckBoxAll[j].checked==true){
					var cargoProducId = document.getElementsByName(ckeckBoxAll[j].value);
					for(var i=0;i<cargoProducId.length;i++){
						cargoProducId[i].checked=true;
					}
				}else{
					var cargoProducId = document.getElementsByName(ckeckBoxAll[j].value);
					for(var i=0;i<cargoProducId.length;i++){
						cargoProducId[i].checked=false;
					}
				}
			}	 
				 
		     
		}
		
		function checkForm(){
			 var cargoProducId = document.getElementsByName('cargoProducId');
			 for(var i=0;i<cargoProducId.length;i++){
		    	if(cargoProducId[i].checked ==true){
		    		return true;
		    	}
		    }
		    alert("请选择货位，再生成货位调拨单");
		    return false;
		}
		
		function editSubmit(){
			document.getElementById('action').value = 'edit';
			document.cargoOperationForm.submit();
		}
		
		function confirmSubmit(){
			document.getElementById('action').value = 'confirm';
			document.cargoOperationForm.submit();
		}
		
		function auditingSubmit(){
			document.getElementById('method').value = 'auditingUpCargo';
			document.cargoOperationForm.submit();
		}
		
		function completeSubmit(){
			document.getElementById('method').value = 'completeUpCargo';
			document.cargoOperationForm.submit();
		}

		function checkSubmit(){
			document.getElementById('method').value = 'checkUpCargo';
			document.cargoOperationForm.submit();
		}
		
		function submitEdit(outCpsId){
			document.getElementById('action').value = 'submitEdit';
			document.getElementById('outCpsId').value = outCpsId;
			document.cargoOperationForm.submit();
			 
		}
 	</script>
  </head>
  <body >
  <%if(operBean!=null){ %>	
  	 <div style="border-collapse:collapse;border:1px solid black;width:90%;margin:0px auto;">
  	 	<form action="<%=basePath%>admin/cargoOper.do" id="cargoOperationForm" name="cargoOperationForm" method="post">
  	 	<div style="margin:5px">作业单操作页</div>
  	 	<div style="margin:5px">
  	 		作业单编号：<%=operBean.getCode()%>&nbsp;&nbsp;作业单状态: <font color="red"><%=operBean.getStatusName()%></font>&nbsp;&nbsp;
  	 		<%if(nextStatus!=null){ %>
  	 			<%if(((nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS3&&group.isFlag(522))
  	 					||(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS4&&group.isFlag(523))
  	 					||(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS5&&group.isFlag(524))
  	 					||(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS6&&group.isFlag(525)))
  	 					&&(operBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS3
  	 	  				&&operBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS4)){ %>
  	 				<input type="button" value="<%=nextStatus.getOperName()%>" onclick="auditingSubmit();"/>
  	 			<%}else if((nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS7&&group.isFlag(538))&&(operBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS3
  	 	  				&&operBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS4)){ %>
  	 				<input type="button" value="<%=nextStatus.getOperName()%>" onclick="completeSubmit();"/>
  	 			<%} %>
  	 		<%} %>
  	 		<a href="admin/cargoOperation.do?method=printUpShelfCargo&id=<%=operBean.getId()%>" target="_blank">打印作业单</a>&nbsp;&nbsp;
  	 	    <a href="admin/cargoOperation.do?method=operationLog&operId=<%=operBean.getId() %>" target="_blank">人员操作记录</a>&nbsp;&nbsp;
  	 	    当前时效状态：<%if(operBean.getEffectStatus() == CargoOperationBean.EFFECT_STATUS1 || operBean.getEffectStatus() == CargoOperationBean.EFFECT_STATUS4){%><font color="red"><%=operBean.getEffectTimeName()%></font><%}else{ %><%=operBean.getEffectTimeName()%><%} %>
  	 	</div>
  	 	<div style="margin:5px">作业单类型：<%=operBean.getTypeName()%>
		<%if(operBean.getStockInType()==0){ %>
			（缓存区->散件区）
		<%}else if(operBean.getStockInType()==1){ %>
			（缓存区->整件区）
		<%} %>
		&nbsp;&nbsp;&nbsp;&nbsp;
		目的仓库：<%=operBean.getStorageCode() %>
		&nbsp;&nbsp;&nbsp;&nbsp;
		调拨单号：<%=operBean.getSource() %>
		</div>
 		<div style="margin:5px">
 			<table style="border-collapse:collapse" bordercolor="black" border="1" width="98%">
 			<tr>
 				<td align="center">序号</td>
 				<td align="center">产品编号</td>
 				<td align="center">产品原名称</td>
 				<td align="center">源货位号</td>
 				<td align="center">源货位量（其中，冻结量）</td>
 				<td align="center">目的货位号以及作业量</td>
 				<%if(nextStatus!=null && nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS1){ %>
 					<td align="center">操作</td>
 				<% }%>	
 			</tr>
 		<%
		if(cocList!=null && cocList.size()>0){
			for(int i=0;i<cocList.size();i++){
				CargoOperationCargoBean cocBean = (CargoOperationCargoBean)	cocList.get(i);
				CargoProductStockBean cpsBean = cocBean.getCargoProductStock();
				voProduct product = cocBean.getProduct();
				List cpsList = cocBean.getCocList();
				int sumNum=0;
				List currentStockCountList2=(List)currentStockCountList.get(i);
 		 %>	
 			<tr>
 				<td align="center"><%=i+1%></td>
 				<td align="center"><a href="<%=basePath%>admin/fproduct.do?id=<%= product.getId()%>"><%=product.getCode()%></a></td>
 				<td align="center"><a href="<%=basePath%>admin/fproduct.do?id=<%= product.getId()%>"><%=product.getOriname()%></a></td>
 				<td align="center"><%=cocBean.getOutCargoWholeCode()%></td>
 				<td align="center"><%if(cpsBean.getStockCount()+cpsBean.getStockLockCount()!=0){ %><a href="admin/cargoOperation.do?method=stockExchangeList&cargoWholeCode=<%=cocBean.getOutCargoWholeCode() %>&productCode=<%=product.getCode() %>&type=stock" target="_blank"><%=(cpsBean.getStockCount()+cpsBean.getStockLockCount())%></a><%}else{ %>0<%} %>
 					(<%if(cpsBean.getStockLockCount()!=0){ %><a href="admin/cargoOperation.do?method=stockExchangeList&cargoWholeCode=<%=cocBean.getOutCargoWholeCode() %>&productCode=<%=product.getCode() %>&type=stockLock" target="_blank"><%=cpsBean.getStockLockCount()%></a><%}else{ %>0<%} %>)</td>
 				<%if(cpsList!=null && cpsList.size()>0){%>	
 				<td align="center">	
 					<table style="border-collapse:collapse" border="1"  bordercolor="black" width="99%">
 						<tr>
 							<td align="center">选<input type="checkbox" name="ckeckBoxAll<%=cocBean.getId()%>"  value="upOpertionId<%=cocBean.getId()%>" onclick="checkAll('ckeckBoxAll<%=cocBean.getId()%>')" <%if(operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS1){ %>disabled="disabled"<%} %>/></td>
 							<td align="center">存放类型</td>
 							<td align="center">货位号</td>
 							<td align="center">当前库存量</td>
 							<td align="center">空间冻结</td>
 							<td align="center">货位最大容量(货位总库存量)</td>
 							<td align="center">本次作业量</td>
 						</tr>
				
				<%for(int j=0;j<cpsList.size();j++){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)cpsList.get(j);
					CargoProductStockBean subCpsBean =  outCoc.getCargoProductStock();
					if(subCpsBean==null) subCpsBean= new CargoProductStockBean();
					CargoInfoBean cargoInfoBean = outCoc.getCargoInfo();
					sumNum+=outCoc.getStockCount();
				%>
 						<tr>
 							<td align="center">
 								<input type="checkbox" name="upOpertionId<%=cocBean.getId()%>" value="<%=outCoc.getId()%>"  <%if(outCoc.getUseStatus()==1){ %>checked="checked"<%} %> <%if(operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS1){ %>disabled="disabled"<%} %>/>
 							</td>
 							<td align="center"><%=cargoInfoBean.getStoreTypeName()%></td>
 							<td align="center" <%if(outCoc.getStockCount()==0&&outCoc.getUseStatus()==1){ %>style="color:red;"<%} %>><%=cargoInfoBean.getWholeCode()%></td>
 							<td align="center"><%=subCpsBean.getStockCount()+subCpsBean.getStockLockCount()%></td>
 							<td align="center"><%if(cargoInfoBean.getSpaceLockCount()!=0){ %><a href="admin/cargoOperation.do?method=stockExchangeList&cargoWholeCode=<%=cargoInfoBean.getWholeCode() %>&productCode=<%=product.getCode() %>&type=spaceLock" target="_blank"><%=cargoInfoBean.getSpaceLockCount()%></a><%}else{ %>0<%} %></td>
 							<td align="center"><%=cargoInfoBean.getMaxStockCount()%>(<%=currentStockCountList2.get(j) %>)</td>
 							<td align="center">
 								<input type="text" size="5" maxlength="15" name="operationNum<%=outCoc.getId()%>" value="<%=outCoc.getStockCount()%>" <%if(operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS1){ %>disabled="disabled"<%} %>/>
 							</td>
 						</tr>
 					<%
 					if(j==cpsList.size()-1){//合计
 						%>
 						<tr>
 							<td align="center">&nbsp;</td>
 							<td align="center">合计</td>
 							<td align="center">&nbsp;</td>
 							<td align="center">&nbsp;</td>
 							<td align="center">&nbsp;</td>
 							<td align="center">&nbsp;</td>
 							<td align="center"><input type="text" size="5" maxlength="15" readonly="readonly"   value="<%=sumNum%>"/>
 							</td>
 						</tr>
 						<%		
 					}	
 					 %>	
				<%}%>
				</table>
				<%}else{
					%>
						<td align="center"><font color="red">
						<%if(operBean.getStockInType()==0){ %>
							目前，散件区没有空闲货位！分配失败！
						<%}else if(operBean.getStockInType()==1){ %>
							请单击‘分配货位’，选择目的货位
						<%} %></font></td>
					<%
				}%>	
				<%if(operBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS1){ %>
 					<td align="center">
 						<a href="<%=basePath%>admin/cargoOper.do?method=deleteOperProduct&cocId=<%=cocBean.getId()%>" onclick="javascript:return confirm('如果确认删除，请单击‘确定’，反之，请单击‘取消’！')">删除</a>&nbsp;
 						<%if(otherCargoMap!=null&&"1".equals(otherCargoMap.get(cocBean.getId()+""))){ %>
 							<a href="<%=basePath%>admin/cargoOperation.do?method=usefulCargo&operId=<%=operBean.getId()%>&outCpsId=<%=cpsBean.getId()%>&otherPagePass=upShelf&otherCargo=1&storageId=<%=storageBean.getId() %>&storeType=<%=operBean.getStockInType() %>">其他货位</a>
 						<%} %>
 						<a href="<%=basePath%>admin/cargoOperation.do?method=usefulCargo&operId=<%=operBean.getId()%>&outCpsId=<%=cpsBean.getId()%>&otherPagePass=upShelf&storageId=<%=storageBean.getId() %>&storeType=<%=operBean.getStockInType() %>">分配货位</a>
 					</td>
 				<% }%>
 			</tr>
 		<%	}
		} %>
 		</table></div>
 		<br /><br/> 
 		<input type="hidden" name="operationId" value="<%=operBean.getId()%>"/>
		<input type="hidden" name="method" id="method" value="editCargoOperation"/>
		<input type="hidden" id="action" name="action" value="edit"/>
		<input type="hidden" id="outCpsId" name="outCpsId" value="0"/>
		<%if(nextStatus!=null){ %>
			<input type="hidden" name="nextStatus" value="<%=nextStatus.getId() %>" />
		<%} %>
		
 		<%if(operBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS1&&group.isFlag(514)){ %>
  		<input type="button" value="保存编辑" onclick="return editSubmit();"/>
  		<%}%>
  		<%if(operBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS1&&group.isFlag(515)){ %>
  		&nbsp;&nbsp;&nbsp;<input type="button" value="确认提交" onclick="return confirmSubmit();"/>
  		<%}%>
  		<%-- 
 		<%else if(operBean.getStatus() == CargoOperationBean.STATUS2){ %>
  		审核状态：<input type="radio" name="status" value="0" checked="checked"/>审核通过&nbsp;&nbsp;<input type="radio" name="status" value="1"/>审核不通过<br/>
  		批注：<textarea name="remark"><%=operBean.getRemark() %></textarea><br/>
  		<input type="button" value="提交" onclick="return auditingSubmit();"/>
  		<%}else if(operBean.getStatus() == CargoOperationBean.STATUS3){ %>
  		作业完成情况：<input type="radio" name="status" value="0" checked="checked"/>上架成功&nbsp;&nbsp;<input type="radio" name="status" value="1"/>上架失败<br/>
  		批注：<textarea name="remark"><%=operBean.getRemark() %></textarea><br/>
  		<input type="button" value="提交" onclick="return completeSubmit();"/>
  		<%} %>
  		--%>
  		<%if(operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS1
  				&&operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS7
  				&&operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS8
  				&&operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS9
  				&&operBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS3
  				&&operBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS4&&group.isFlag(542)){ %>
  			作业复核：
  			<input type="radio" name="status" value="3" checked="checked"/>作业成功&nbsp;&nbsp;
  			<input type="radio" name="status" value="4"/>作业失败<br/>
  			备注：<textarea name="remark"><%=operBean.getRemark() %></textarea>
  			<input type="button" value="复核提交" onclick="return checkSubmit();"/>
  		<%} %>
 		</form>
  	 </div>
  <%}else{
  	%>错误！！！！！！！获取数据发生了异常。。<%
  } %>
  </body>
</html>

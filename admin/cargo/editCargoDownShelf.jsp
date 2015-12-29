 <%@page import="adultadmin.bean.stock.ProductStockBean"%>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="java.util.*, adultadmin.bean.cargo.*, adultadmin.action.vo.*,adultadmin.util.*,adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="ormap.ProductLineMap"%>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
	
	CargoOperationBean operBean = (CargoOperationBean)request.getAttribute("cargoOperation");
	List cocList = (List)request.getAttribute("cocList");
	CargoOperationProcessBean nextStatus=(CargoOperationProcessBean)request.getAttribute("nextStatus");
	String effectTime=request.getAttribute("effectTime").toString();
%>
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
	<script>
	
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
		
		function confirmSubmit(){
			document.getElementById('action').value = 'confirm';
			document.cargoOperationForm.submit();
		}
		function editSubmit(){
			document.getElementById('action').value = 'edit';
			document.cargoOperationForm.submit();
		}
		function auditingSubmit(){
			document.getElementById('method').value = 'auditingDownCargo';
			document.cargoOperationForm.submit();
		}
		function completeSubmit(){
			document.getElementById('method').value = 'completeDownCargo';
			document.cargoOperationForm.submit();
		}
		function checkSubmit(){
			document.getElementById('method').value = 'checkDownCargo';
			document.cargoOperationForm.submit();
		}
	</script>
<body>
 <%if(operBean!=null){ %>
  	 <div style="border-collapse:collapse;border:1px solid black;width:90%;margin:0px auto;">
  	 	<form action="<%=basePath%>admin/cargoDownShelf.do" id="cargoOperationForm" name="cargoOperationForm" method="post">
  	 	<div style="margin:5px">作业单操作页</div>
  	 	<div style="margin:5px">作业单编号：<%=operBean.getCode()%>&nbsp;&nbsp;作业单状态 <font color="red"><%=operBean.getStatusName()%></font>&nbsp;&nbsp;
  	 	<%if(nextStatus!=null){ %>
  	 			<%if(((nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS12&&group.isFlag(526))
  	 					||(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS13&&group.isFlag(527))
  	 					||(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS14&&group.isFlag(528))
  	 					||(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS15&&group.isFlag(529)))
  	 					&&(operBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS3
  	 	  	 	  		&&operBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS4)){ %>
  	 				<input type="button" value="<%=nextStatus.getOperName()%>" onclick="auditingSubmit();"/>
  	 			<%}else if(nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS16&&group.isFlag(539)&&(operBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS3
  	 	  				&&operBean.getEffectStatus()!=CargoOperationBean.EFFECT_STATUS4)){ %>
  	 				<input type="button" value="<%=nextStatus.getOperName()%>" onclick="completeSubmit();"/>
  	 			<%} %>
  	 		<%} %>
  	 	<a href="cargoOperation.do?method=printDownShelfCargo&id=<%=operBean.getId()%>" target="_blank">打印作业单</a>&nbsp;&nbsp;&nbsp;&nbsp;
  	 	<a href="../admin/cargoOperation.do?method=operationLog&operId=<%=operBean.getId() %>" target="_blank">人员操作记录</a>&nbsp;&nbsp;&nbsp;&nbsp;
  	 	当前时效状态：<%=operBean.getEffectTimeName() %>
  	 	</div>
  	 	<div style="margin:5px">作业单类型：<%=operBean.getTypeName()%>(<%= (String)CargoInfoBean.storeTypeMap.get(Integer.valueOf(operBean.getStockOutType()))%>-&gt;<%= (String)CargoInfoBean.storeTypeMap.get(Integer.valueOf(operBean.getStockInType())) %>)</div>
 		<table style="margin:5px;border-collapse:collapse" bordercolor="black"  border="1" width="98%">
 			<tr>
 				<td align="center">序号</td>
 				<td align="center">产品编号</td>
 				<td align="center">产品原名称</td>
 				<td align="center">源货位号</td>
 				<td align="center">源货位量（其中，冻结量）</td>
 				<td align="center">目的货位号以及作业量</td>
 				<%if(nextStatus!=null&&nextStatus.getId()==CargoOperationProcessBean.OPERATION_STATUS10){ %>
 					<td align="center">操作</td>
 				<% }%>	
 			</tr>
 		<%
		if(cocList!=null && cocList.size()>0){
			for(int i=0;i<cocList.size();i++){
				CargoOperationCargoBean cocBean = (CargoOperationCargoBean)	cocList.get(i);
				CargoProductStockBean cpsBean = cocBean.getCargoProductStock();
				if(cpsBean==null ) cpsBean = new CargoProductStockBean();
				voProduct product = cocBean.getProduct();
				List cpsList = cocBean.getCocList();
				int sumNum=0;
 		 %>	
 			<tr>
 				<td align="center"><%=i+1%></td>
 				<td align="center"><a href="<%=basePath%>admin/fproduct.do?id=<%= product.getId()%>"><%=product.getCode()%></a></td>
 				<td align="center"><a href="<%=basePath%>admin/fproduct.do?id=<%= product.getId()%>"><%=product.getOriname()%></a></td>
 				<td align="center"><%=cocBean.getOutCargoWholeCode()%></td>
 				<td align="center"><%=(cpsBean.getStockCount()+cpsBean.getStockLockCount())%>(<%if(cpsBean.getStockLockCount()!=0){%><a href="cargoOperation.do?method=stockExchangeList&cargoWholeCode=<%=cocBean.getOutCargoWholeCode() %>&productCode=<%=cocBean.getProduct().getCode() %>&type=stockLock" target="_blank"><%=cpsBean.getStockLockCount()%></a><%}else{ %>0<%} %>)</td>
 				<%if(cpsList!=null && cpsList.size()>0){%>	
 				<td align="center">	
 					<table style="border-collapse:collapse" border="1" width="99%" bordercolor="black">
 						<tr>
 							<td align="center">选<input type="checkbox" name="ckeckBoxAll<%=cocBean.getId()%>" onclick="checkAll('ckeckBoxAll<%=cocBean.getId()%>')" value="upOpertionId<%=cocBean.getId()%>" <%if(operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS10){ %>disabled="disabled"<%} %>/></td>
 							<td align="center">存放类型</td>
 							<td align="center">货位号</td>
 							<td align="center">当前库存量</td>
 							<td align="center">空间冻结</td>
 							<td align="center">货位最大容量</td>
 							<td align="center">本次作业量</td>
 						</tr>
				
				<%for(int j=0;j<cpsList.size();j++){
					CargoOperationCargoBean outCoc = (CargoOperationCargoBean)cpsList.get(j);
					CargoProductStockBean subCpsBean =  outCoc.getCargoProductStock();
					if(subCpsBean==null) subCpsBean = new CargoProductStockBean();
					CargoInfoBean cargoInfoBean = outCoc.getCargoInfo();
					sumNum+=outCoc.getStockCount();
					boolean flagDisable = operBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS10;
				%>
 						<tr>
 							<td align="center">
 								<input type="checkbox"  name="upOpertionId<%=cocBean.getId()%>" value="<%=outCoc.getId()%>" <%=flagDisable?"checked='checked'":""%>  <%if(outCoc.getUseStatus()==1){ %>checked="checked"<%} %> <%if(operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS10){ %>disabled="disabled"<%} %>/>
 							</td>
 							<td align="center"><%=cargoInfoBean.getStoreTypeName()%></td>
 							<td align="center" <%if(outCoc.getStockCount()==0&&outCoc.getUseStatus()==1){ %>style="color:red;"<%} %>><%=cargoInfoBean.getWholeCode()%></td>
 							<td align="center"><%=subCpsBean.getStockCount()%></td>
 							<td align="center"><%if(cargoInfoBean.getSpaceLockCount()!=0){%><a href="cargoOperation.do?method=stockExchangeList&cargoWholeCode=<%=outCoc.getInCargoWholeCode() %>&productCode=<%=outCoc.getProduct().getCode() %>&type=spaceLock" target="_blank"><%=cargoInfoBean.getSpaceLockCount()%></a><%}else{ %>0<%} %></td>
 							<td align="center"><%=cargoInfoBean.getMaxStockCount()%></td>
 							<td align="center">
 								<input type="text" size="5" maxlength="15" name="operationNum<%=outCoc.getId()%>" value="<%=outCoc.getStockCount()%>" <%if(operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS10){ %>disabled="disabled"<%} %>/>
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
				<%}
					%></table><%
				}else{
					%>
						<td align="center">请单击‘分配货位’，选择目的货位！</td>
					<%
				}%>	
				<%if(operBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS10){ %>
 					<td align="center">
 						<a href="<%=basePath%>admin/cargoDownShelf.do?method=deleteOperProduct&cocId=<%=cocBean.getId()%>" onclick="javascript:return confirm('如果确认删除，请单击‘确定’，反之，请单击‘取消’！')">删除</a>&nbsp;
 						<a href="<%=basePath%>admin/cargoOperation.do?method=usefulCargo&operId=<%=operBean.getId()%>&outCpsId=<%=cpsBean.getId()%>&otherPagePass=downShelf">分配货位</a>
 					</td>
 				<% }%>
 			</tr>
 		<%	}
		} %>
 		</table> 
 		<br /><br/> 
 		<input type="hidden" name="id" value="<%=operBean.getId()%>"/>
		<input type="hidden" name="method" id="method" value="editDownShelf"/>
		<input type="hidden" id="action" name="action" value="edit"/>
		<%if(nextStatus!=null){ %>
			<input type="hidden" name="nextStatus" value="<%=nextStatus.getId() %>" />
		<%} %>
 		<%if(operBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS10&&group.isFlag(516)){ %>
  			<input type="submit" value="保存编辑" onclick="return editSubmit();"/>
  		<%}%>
  		<%if(operBean.getStatus()==CargoOperationProcessBean.OPERATION_STATUS10&&group.isFlag(517)){ %>
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
  		 <%if(operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS10
  				&&operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS16
  				&&operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS17
  				&&operBean.getStatus()!=CargoOperationProcessBean.OPERATION_STATUS18
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
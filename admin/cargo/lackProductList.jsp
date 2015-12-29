<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.bean.cargo.*"%>
<%@ page import="adultadmin.action.vo.*"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%
	HashMap productMap = (HashMap)request.getAttribute("productMap");
	HashMap cpsMap = (HashMap)request.getAttribute("cpsMap");
	HashMap productMap2 = (HashMap)request.getAttribute("productMap2");
	HashMap cpsMap2 = (HashMap)request.getAttribute("cpsMap2");
	List idList = (List)request.getAttribute("idList");
	List idList2 = (List)request.getAttribute("idList2");
	List exchangeCountList = (List)request.getAttribute("exchangeCountList");
	List exchangeCountList2 = (List)request.getAttribute("exchangeCountList2");
	HashMap lackTimeMap=(HashMap)request.getAttribute("lackTimeMap");
	HashMap lackTimeMap2=(HashMap)request.getAttribute("lackTimeMap2");
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
	</head>
	<body>
	<script type="text/javascript">
 		function checkAll(name) {     
		    var checkChagen =document.getElementsByName(name);
		    if(name=="checkID"){
			    var cargoProducId = document.getElementsByName('productId');
			    for(var i=0;i<cargoProducId.length;i++){
			    	cargoProducId[i].checked =checkChagen[0].checked ;
			    }
		    }
		    if(name=="checkID2"){
			    var cargoProducId2 = document.getElementsByName('productId2');
			    for(var i=0;i<cargoProducId2.length;i++){
			    	cargoProducId2[i].checked =checkChagen[0].checked ;
			    }
		    }
		}
 		
 		function check(){
 	 		if(document.getElementById("yishenqing").style.display=="block"){
				 var cargoProducId = document.getElementsByName('productId');
				 for(var i=0;i<cargoProducId.length;i++){
			    	if(cargoProducId[i].checked ==true){
			    		return true;
			    	}
			    }
			    alert("请选择货位，再生成补货单");
			    return false;
 	 		}else if(document.getElementById("weishenqing").style.display=="block"){
 	 			var cargoProducId = document.getElementsByName('productId2');
				 for(var i=0;i<cargoProducId.length;i++){
			    	if(cargoProducId[i].checked ==true){
			    		return true;
			    	}
			    }
			    alert("请选择货位，再生成补货单");
			    return false;
 	 		}else{
 	 	 		alert("cuowu");
				return false;
 	 		}
		}
		function yishenqing(){
			document.getElementById("head1").style.display="block";
			document.getElementById("yishenqing").style.display="block";
			document.getElementById("head2").style.display="none";
			document.getElementById("weishenqing").style.display="none";
		}
		function weishenqing(){
			document.getElementById("head2").style.display="block";
			document.getElementById("weishenqing").style.display="block";
			document.getElementById("head1").style.display="none";
			document.getElementById("yishenqing").style.display="none";
		}
		function submitProductId1(){
			var productId2List=document.getElementsByName("productId2");
			for(var i=0;i<productId2List.length;i++){
				var productId=productId2List[i];
				productId.checked=false;
			}
		}
		function submitProductId2(){
			var productId1List=document.getElementsByName("productId1");
			for(var i=0;i<productId1List.length;i++){
				var productId=productId1List[i];
				productId.checked=false;
			}
		}
 	</script>
		<%@include file="../../header.jsp"%>
		<div>散件区缺货列表</div>
		
		<div id='head1' style="display:block">芳村散件区缺货&nbsp;&nbsp;&nbsp;&nbsp;<a href="javascript:weishenqing()">增城散件区缺货</a></div>
		<div id='head2' style="display:none"><a href="javascript:yishenqing()">芳村散件区缺货</a>&nbsp;&nbsp;&nbsp;&nbsp;增城散件区缺货</div>
		
		<form name="cargoForm" action="cargoOperation.do" method="post" onSubmit="return check();">
			<input type="hidden" name="method" value="addRefillCargo2"/>
			<input type="hidden" name="areaId" value="1"/>
			<table id="yishenqing" cellpadding="3" border=1 style="border-collapse:collapse;display:block;" bordercolor="#D8D8D5" width="98%" >
		<tr bgcolor="#4688D6">
			<td align="center"><font color="#FFFFFF">选</font><input type="checkBox" name="checkID" id="checkID" onclick="checkAll('checkID')"/></td>
			<td align="center"><font color="#FFFFFF">序号</font></td>
			<td align="center"><font color="#FFFFFF">缺货时间</font></td>
			<td align="center"><font color="#FFFFFF">产品一级分类</font></td>
			<td align="center"><font color="#FFFFFF">产品编号</font></td>
			<td align="center"><font color="#FFFFFF">产品原名称</font></td>
			<td align="center"><font color="#FFFFFF">当前散件区缺货量</font></td>
			<%--  
			<td align="center"><font color="#FFFFFF">散件区货位间调拨中</font></td>
			--%>
			<td align="center"><font color="#FFFFFF">整件区库存(其中冻结量)</font></td>
			<td align="center"><font color="#FFFFFF">缓存区库存(其中冻结量)</font></td>
		</tr>
				<%
					if(idList!=null){
						int i=0;
						Iterator iter = idList.listIterator();
						while(iter.hasNext()){
							Map.Entry entry = (Map.Entry)iter.next();
							String productId = (String)entry.getKey();
							int lackCount = -((Integer)entry.getValue()).intValue();
							voProduct product = (voProduct)productMap.get(productId);
							
							if(lackCount<=0){
								continue;
							}
							i++;
				%>
				<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
					<td>
						<%if(product.getCargoStock(CargoInfoBean.STORE_TYPE1)+product.getCargoLockStock(CargoInfoBean.STORE_TYPE1)!=0){ %>
							<input type="checkbox" id="productId" name="productId" value="<%=product.getId()%>"/>
						<%}else{ %>
							<input type="checkbox" disabled="disabled"/>
						<%} %>
						<input type="hidden" id="lackCount<%=product.getId() %>" name="lackCount<%=product.getId() %>" value="<%=lackCount%>"/>
					</td>
					<td align="center">
						<%=i %>
					</td>
					<td align="center">
					<%if(lackTimeMap!=null&&lackTimeMap.get(productId)!=null){ %><%=lackTimeMap.get(productId).toString().substring(0,19)%><%} %>
					</td>
					<td align="center">
						<%=product.getParent1().getName() %>
					</td>
					<td align="center">
						<%=product.getCode() %>
					</td>
					<td align="center">
						<%=product.getOriname() %>
					</td>
					<td align="center">
						<%=lackCount %>
					</td>
					<%--
					<td align="center">
						<%if(exchangeCountList.size()>=i){ %>
						<%=exchangeCountList.get(i-1) %>
						<%}else{ %>lkforce!<%} %>
					</td>
					 --%>
					<td align="center">
						<%=product.getCargoStock(CargoInfoBean.STORE_TYPE1)+product.getCargoLockStock(CargoInfoBean.STORE_TYPE1)%>(<%=product.getCargoLockStock(CargoInfoBean.STORE_TYPE1)%>)
					</td>
					<td align="center">
						<%=product.getCargoStockByType(CargoInfoBean.STORE_TYPE2,CargoInfoBean.STOCKTYPE_QUALIFIED)+product.getCargoLockStockByType(CargoInfoBean.STORE_TYPE2,CargoInfoBean.STOCKTYPE_QUALIFIED)%>(<%=product.getCargoLockStockByType(CargoInfoBean.STORE_TYPE2,CargoInfoBean.STOCKTYPE_QUALIFIED)%>)
					</td>
				</tr>
				<%			
						}
				%>
					
				<%  } %>
				<tr>
					<td align="left" colspan="14"><input type="submit" value="对勾选货位生成补货单" onclick="submitProductId1();"/></td>
				</tr>
			</table>
			<%--  
			<table id="weishenqing" cellpadding="3" border=1 style="border-collapse:collapse;display:none;" bordercolor="#D8D8D5" width="98%" >
		<tr bgcolor="#4688D6">
			<td align="center"><font color="#FFFFFF">选</font><input type="checkBox" name="checkID2" id="checkID2" onclick="checkAll('checkID2')"/></td>
			<td align="center"><font color="#FFFFFF">序号</font></td>
			<td align="center"><font color="#FFFFFF">产品一级分类</font></td>
			<td align="center"><font color="#FFFFFF">产品编号</font></td>
			<td align="center"><font color="#FFFFFF">产品原名称</font></td>
			<td align="center"><font color="#FFFFFF">当前散件区缺货量</font></td>
			<td align="center"><font color="#FFFFFF">散件区货位间调拨中</font></td>
			<td align="center"><font color="#FFFFFF">整件区库存(其中冻结量)</font></td>
			<td align="center"><font color="#FFFFFF">缓存区库存(其中冻结量)</font></td>
		</tr>
				<%
					if(idList2!=null){
						int i=0;
						Iterator iter = idList2.listIterator();
						while(iter.hasNext()){
							Map.Entry entry = (Map.Entry)iter.next();
							String productId = (String)entry.getKey();
							int lackCount = -((Integer)entry.getValue()).intValue();
							voProduct product = (voProduct)productMap2.get(productId);
							
							if(lackCount<=0){
								continue;
							}
							i++;
				%>
				<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
					<td>
						<%if(product.getCargoStock(CargoInfoBean.STORE_TYPE1)+product.getCargoLockStock(CargoInfoBean.STORE_TYPE1)!=0){ %>
							<input type="checkbox" id="productId2" name="productId2" value="<%=product.getId()%>"/>
						<%}else{ %>
							<input type="checkbox" disabled="disabled"/>
						<%} %>
						<input type="hidden" id="lackCount<%=product.getId() %>" name="lackCount<%=product.getId() %>" value="<%=lackCount%>"/>
					</td>
					<td align="center">
						<%=i %>
					</td>
					<td align="center">
						<%=product.getParent1().getName() %>
					</td>
					<td align="center">
						<%=product.getCode() %>
					</td>
					<td align="center">
						<%=product.getOriname() %>
					</td>
					<td align="center">
						<%=lackCount %>
					</td>
					<td align="center">
						<%if(exchangeCountList2.size()>=i){ %>
						<%=exchangeCountList2.get(i-1) %>
						<%}else{ %>lkforce!<%} %>
					</td>
					<td align="center">
						<%=product.getCargoStock(CargoInfoBean.STORE_TYPE1)+product.getCargoLockStock(CargoInfoBean.STORE_TYPE1)%>(<%=product.getCargoLockStock(CargoInfoBean.STORE_TYPE1)%>)
					</td>
					<td align="center">
						<%=product.getCargoStockByType(CargoInfoBean.STORE_TYPE2,CargoInfoBean.STOCKTYPE_QUALIFIED)+product.getCargoLockStockByType(CargoInfoBean.STORE_TYPE2,CargoInfoBean.STOCKTYPE_QUALIFIED)%>(<%=product.getCargoLockStockByType(CargoInfoBean.STORE_TYPE2,CargoInfoBean.STOCKTYPE_QUALIFIED)%>)
					</td>
				</tr>
				<%			
						}
				%>
					
				<%  } %>
				<tr>
					<td align="left" colspan="14"><input type="submit" value="对勾选货位生成补货单" onclick="submitProductId2();"/></td>
				</tr>
			</table>
			--%>
		</form>
		<form name="cargoForm" action="cargoOperation.do" method="post" onSubmit="return check();">
			<input type="hidden" name="method" value="addRefillCargo2"/>
			<input type="hidden" name="areaId" value="3"/>
			<table id="weishenqing" cellpadding="3" border=1 style="border-collapse:collapse;display:none;" bordercolor="#D8D8D5" width="98%" >
		<tr bgcolor="#4688D6">
			<td align="center"><font color="#FFFFFF">选</font><input type="checkBox" name="checkID" id="checkID" onclick="checkAll('checkID')"/></td>
			<td align="center"><font color="#FFFFFF">序号</font></td>
			<td align="center"><font color="#FFFFFF">缺货时间</font></td>
			<td align="center"><font color="#FFFFFF">产品一级分类</font></td>
			<td align="center"><font color="#FFFFFF">产品编号</font></td>
			<td align="center"><font color="#FFFFFF">产品原名称</font></td>
			<td align="center"><font color="#FFFFFF">当前散件区缺货量</font></td>
			<%--  
			<td align="center"><font color="#FFFFFF">散件区货位间调拨中</font></td>
			--%>
			<td align="center"><font color="#FFFFFF">整件区库存(其中冻结量)</font></td>
			<td align="center"><font color="#FFFFFF">缓存区库存(其中冻结量)</font></td>
		</tr>
				<%
					if(idList2!=null){
						int i=0;
						Iterator iter = idList2.listIterator();
						while(iter.hasNext()){
							Map.Entry entry = (Map.Entry)iter.next();
							String productId = (String)entry.getKey();
							int lackCount = -((Integer)entry.getValue()).intValue();
							voProduct product = (voProduct)productMap2.get(productId);
							
							if(lackCount<=0){
								continue;
							}
							i++;
				%>
				<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
					<td>
						<%if(product.getCargoStock(CargoInfoBean.STORE_TYPE1)+product.getCargoLockStock(CargoInfoBean.STORE_TYPE1)!=0){ %>
							<input type="checkbox" id="productId2" name="productId2" value="<%=product.getId()%>"/>
						<%}else{ %>
							<input type="checkbox" disabled="disabled"/>
						<%} %>
						<input type="hidden" id="lackCount<%=product.getId() %>" name="lackCount<%=product.getId() %>" value="<%=lackCount%>"/>
					</td>
					<td align="center">
						<%=i %>
					</td>
					<td align="center">
					<%if(lackTimeMap2!=null&&lackTimeMap2.get(productId)!=null){ %><%=lackTimeMap2.get(productId).toString().substring(0,19)%><%} %>
					</td>
					<td align="center">
						<%=product.getParent1().getName() %>
					</td>
					<td align="center">
						<%=product.getCode() %>
					</td>
					<td align="center">
						<%=product.getOriname() %>
					</td>
					<td align="center">
						<%=lackCount %>
					</td>
					<td align="center">
						<%=product.getCargoStock(CargoInfoBean.STORE_TYPE1)+product.getCargoLockStock(CargoInfoBean.STORE_TYPE1)%>(<%=product.getCargoLockStock(CargoInfoBean.STORE_TYPE1)%>)
					</td>
					<td align="center">
						<%=product.getCargoStockByType(CargoInfoBean.STORE_TYPE2,CargoInfoBean.STOCKTYPE_QUALIFIED)+product.getCargoLockStockByType(CargoInfoBean.STORE_TYPE2,CargoInfoBean.STOCKTYPE_QUALIFIED)%>(<%=product.getCargoLockStockByType(CargoInfoBean.STORE_TYPE2,CargoInfoBean.STOCKTYPE_QUALIFIED)%>)
					</td>
				</tr>
				<%			
						}
				%>
					
				<%  } %>
				<tr>
					<td align="left" colspan="14"><input type="submit" value="对勾选货位生成补货单" onclick="submitProductId2();"/></td>
				</tr>
			</table>
		</form>
		<%@include file="../../footer.jsp"%>
	</body>
</html>
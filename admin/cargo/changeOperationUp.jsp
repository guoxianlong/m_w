<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean ,adultadmin.bean.stock.*,adultadmin.util.*,adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.cargo.CargoInfoStorageBean,mmb.stock.stat.*" %>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

List list = (List)request.getAttribute("list");
List storageList = (List)request.getAttribute("storageList");
PagingBean paging = (PagingBean) request.getAttribute("paging");
String exChangeId =StringUtil.convertNull((String) request.getAttribute("exChangeId"));

String exChangeCode= StringUtil.convertNull(request.getParameter("exChangeCode"));
String productCode = StringUtil.convertNull(request.getParameter("productCode"));

String wareAreaLable = ProductWarePropertyService.getWeraAreaOptionsCustomized("stockInArea", "stockInArea", request, -1, false,"");
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<%@page import="mmb.stock.cargo.CargoDeptAreaService"%><html>
  <head>
    <base href="<%=basePath%>">
    <title>转换新的上架作业单</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="转换新的上架作业单">
 	<script type="text/javascript">
 	  
		function checkAll(name) {     
		    var checkChagen =document.getElementsByName(name);
		    var sExProductId =document.getElementsByName('sExProductId');
		    for(var i=0;i<sExProductId.length;i++){
		    	if(sExProductId[i].disabled==false){
		    		sExProductId[i].checked =checkChagen[0].checked ;
		    	}	
		    }
		}
		
		function checkForm(){
			var storageCode=document.getElementById("storageCode").value;
			if(storageCode==""){
				alert('请先选择目的仓库和货位存放类型！');
				return false;
			}
			var storeType=document.getElementById("storeType").value;
			if(storeType==""){
				alert('请先选择目的仓库和货位存放类型！');
				return false;
			}
			var checkChagen =document.getElementsByName('sExProductId');
		    for(var i=0;i<checkChagen.length;i++){
		    	if(checkChagen[i].checked ==true){
		    		return true;
		    	}
		    }
		    alert('请至少选择一个产品');
		    return false;
		}
		
		function onloadSubList(id){
			document.getElementById('exChangeId').value=id;
			document.getElementById('pageIndex').value='<%=StringUtil.StringToId(request.getParameter("pageIndex")) %>';
			
			document.cargoUpOperForm.submit();
		}
		
	</script>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
 	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
  </head>
  <body>
  	<form action="<%=basePath%>admin/cargoUpOper.do?method=showExChangeList" method="post" name="cargoUpOperForm" id="cargoUpOperForm">  
	  	转换新的上架作业单<br/>
	  	<fieldset style="width:850px;"> 
	    调拨单编号<input type="text" size="15" name="exChangeCode" value="<%=exChangeCode%>"/>精确 &nbsp;&nbsp;&nbsp;
		产品编号:<input type="text" size="15" name="productCode"  value="<%=productCode%>" />精确&nbsp;&nbsp;&nbsp;
				<input type="hidden" name="exChangeId" id="exChangeId" value="">
				<input type="hidden" name="pageIndex" id="pageIndex" value="">
				目的库地区：<%= wareAreaLable %>
		<input type="submit" value="查询"/> 
	  	</fieldset>	
	</form>	 	
	<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5" width="95%" >
		<tr bgcolor="#4688D6">
			<td align="center"><font color="#FFFFFF">序号</font></td>
			<td align="center"><font color="#FFFFFF">调拨单编号</font></td>
			<td align="center"><font color="#FFFFFF">源库</font></td>
			<td align="center"><font color="#FFFFFF">目的库</font></td>
			<td align="center"><font color="#FFFFFF">出库操作</font></td>
			<td align="center"><font color="#FFFFFF">出库审核</font></td>
			<td align="center"><font color="#FFFFFF">入库操作</font></td>
			<td align="center"><font color="#FFFFFF">入库审核</font></td>
			<td align="center"><font color="#FFFFFF">状态</font></td>
			<td align="center"><font color="#FFFFFF">调拨单生成时间</font></td>
			<td align="center"><font color="#FFFFFF">调拨单完成时间</font></td>
			<td align="center"><font color="#FFFFFF">已转换作业单个数</font></td>
		</tr>
		<%
		if(list!=null && list.size()>0){
			for(int i=0;i<list.size();i++){
				StockExchangeBean bean = (StockExchangeBean) list.get(i);
				%>
			  <tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>	 
				  <td align="center"><%=(i + 1)+(paging.getCurrentPageIndex()*paging.getCountPerPage())%></td>
				  <td align="center"><a style="cursor:pointer;"  onclick="onloadSubList('<%=bean.getId()%>')"><%=bean.getCode()%></a></td>
				  <td align="center"><%=ProductStockBean.getAreaName(bean.getStockOutArea())%>-<%= ProductStockBean.getStockTypeName(bean.getStockOutType())  %> </td>
				  <td align="center"><%=ProductStockBean.getAreaName(bean.getStockInArea())%>-<%=ProductStockBean.getStockTypeName(bean.getStockInType()) %></td>
				  <td align="center"><%=(!bean.getCreateUserName().equals(""))?bean.getCreateUserName():(bean.getCreateUser()!=null?bean.getCreateUser().getUsername():"&nbsp") %></td>
				  <td align="center"><%=(!bean.getAuditingUserName().equals(""))?bean.getAuditingUserName():(bean.getAuditingUser()!=null?bean.getAuditingUser().getUsername():"&nbsp") %></td>
				  <td align="center"><%=(!bean.getStockInOperName().equals(""))?bean.getStockInOperName():(bean.getStockInOperUser()!=null?bean.getStockInOperUser().getUsername():"&nbsp") %></td>
				  <td align="center"><%=(!bean.getAuditingUserName2().equals(""))?bean.getAuditingUserName2():(bean.getAuditingUser2()!=null?bean.getAuditingUser2().getUsername():"&nbsp") %></td>
				  <td align="center"><%if(bean.getStatus() == StockExchangeBean.STATUS0){%><font color="red"><%=bean.getStatusName()%></font><%}else{%><%=bean.getStatusName()%><%}%></td>
				  <td align="center"><%=StringUtil.convertNull(StringUtil.cutString(bean.getCreateDatetime(),  16))%>&nbsp;</td>
  				  <td align="center"><%=StringUtil.convertNull(StringUtil.cutString(bean.getConfirmDatetime(), 16))%> &nbsp;</td>	
  				  <td align="center"><%=bean.getOperationNum()%></td>	  
			  </tr>	
				<%
			}
		}	
		%>
	</table>
	<%if(paging!=null){ %>
	<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
    <br/><br/><br/>
<%if(!exChangeId.equals("")){
    List sepList = (List)request.getAttribute("sepList");
    Map productMap =(Map)request.getAttribute("productMap");
    String exChangeIdCode = StringUtil.convertNull((String)request.getAttribute("exChangeIdCode"));
    %>
 	<div id="exChangeDiv" >
  		<form action="<%=basePath%>admin/cargoUpOper.do?method=addUpOperation" method="post">
  		&nbsp;&nbsp;编号 :<span id="exChangeCode"><%=exChangeIdCode%></span>&nbsp;&nbsp;&nbsp;&nbsp;
  		目的仓库：
  		<select id="storageCode" name="storageCode">
  			<%if(storageList!=null){ %>
  			<%for(int i=0;i<storageList.size();i++){ %>
  				<%CargoInfoStorageBean storage=(CargoInfoStorageBean)storageList.get(i); %>
  				<option value="<%=storage.getWholeCode() %>"><%=storage.getWholeCode() %></option>
  			<%}} %>
  		</select>
  		货位存放类型：
  		<select id="storeType" name="storeType">
  			<option value=""></option>
  			<option value="0">散件区</option>
  			<option value="1">整件区</option>
  			<option value="4">混合区</option>
  		</select>
  		<br/>
  		<table cellpadding="0" cellspacing="0" border="1"  style="border-collapse:collapse;" width="70%">
  			<tr>
  				<td align="center"><input type="checkbox" id="checkId" onclick="checkAll('checkId')" name="checkId"/></td>
  				<td align="center">序号</td>
  				<td align="center">产品一级分类</td>
  				<td align="center">产品原名称</td>
  				<td align="center">产品编号</td>
  				<td align="center">调拨量</td>
  				<td align="center">已上架</td>
  				<td align="center">未上架(其中冻结量)</td>
  			</tr>
	<%
	 if(sepList!=null && sepList.size()>0){
	 	int operNum=0, operUnop=0,storeNum=0;
	 	int count= sepList.size();
	 	for(int i=0;i<count;i++){
	 		StockExchangeProductBean sep = (StockExchangeProductBean) sepList.get(i);
	 		voProduct product = (voProduct) productMap.get(Integer.valueOf(sep.getProductId()));
	 		int tempStockOutCount =sep.getStockOutCount(); //调拨单的数量
	 		int tempOperNoUpNum = sep.getNoUpCargoCount();  // 未上架量
	 		int tempOperLockNum = sep.getUpCargoLockCount();//锁定数量
	 		
	 		storeNum+=tempStockOutCount;
	 		operUnop+=tempOperNoUpNum; //未上架了总和
	 		operNum+=(tempStockOutCount- tempOperNoUpNum);//已上架量总和
	 		boolean flagDisable =true;
	 		if(tempOperNoUpNum<=0 || tempStockOutCount== tempOperLockNum+(tempStockOutCount-tempOperNoUpNum)){//如果未上架数量少于等于0 则不能再选
	 			flagDisable=false;
	 		}
	 		if(product!=null){
	 		%>
	 		<tr>
	 		   <td align="center"><input type="checkbox" id="sExProductId" name="sExProductId" value="<%=sep.getId()%>" <%=flagDisable?"checked='checked'":"disabled='disabled'"%> /></td>
	 		   <td align="center"><%=i+1%></td>
	 		   <td align="center"><%=product.getParent1().getName()%></td>
	 		   <td align="center"><a href="<%=basePath%>admin/fproduct.do?id=<%= product.getId()%>"><%=product.getOriname()%></a></td>
	 		   <td align="center"><a href="<%=basePath%>admin/fproduct.do?id=<%= product.getId()%>"><%=product.getCode()%></a></td>
	 		   <td align="center"><%=tempStockOutCount%></td>
	 		   <td align="center"><%=tempStockOutCount-tempOperNoUpNum%></td>
	 		   <td align="center"><%=tempOperNoUpNum%>(<%=tempOperLockNum%>)</td>
	 		</tr>
	 		<%
	 		}
	 		 if(i==count-1){
	 		 %>
	 		 <tr>
	 		 	<td align="center">&nbsp;</td>
	 		 	<td align="center">&nbsp;</td>
	 		 	<td align="center">合计</td>
	 		 	<td align="center">&nbsp;</td>
	 		 	<td align="center">&nbsp;</td>
	 		 	<td align="center"><%=storeNum %></td>
	 		 	<td align="center"><%=operNum %></td>
	 		 	<td align="center"><%=operUnop %></td>
	 		 </tr>
	 		 <%
	 		 }
	 	}
	 }else{
	 	%>
	 		<tr><td align="center" colspan="8">该调拨单无数据</td></tr>
	 	<%
	 }
	 %>
  		</table>
  		<div><input type="submit" value="对选中的产品生成上架作业单" onclick="return checkForm();" /></div>
  	   </form>
 	</div>
<%}%> 	
   </body>
</html>

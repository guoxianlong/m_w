<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List,adultadmin.util.PageUtil,adultadmin.bean.PagingBean " %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.action.vo.*,adultadmin.util.*" %>
<html>
<head>
<title>分配货位</title>

<%
List inCargoBeanList=null;
List productLineList=null;
List storageList=null;
List inCpsList=null;
if(request.getAttribute("inCpsList")!=null){
	inCpsList=(List)request.getAttribute("inCpsList");
}
if(request.getAttribute("inCargoBeanList")!=null){
	inCargoBeanList=(List)request.getAttribute("inCargoBeanList");
}
if(request.getAttribute("productLineList")!=null){
	productLineList=(List)request.getAttribute("productLineList");
}
if(request.getAttribute("storageList")!=null){
	storageList=(List)request.getAttribute("storageList");
}
String operId=request.getParameter("operId");
String outCpsId=request.getParameter("outCpsId");
String productId=StringUtil.convertNull((String)request.getAttribute("productId"));
String otherPagePass = StringUtil.convertNull(request.getParameter("otherPagePass"));
String status = StringUtil.convertNull(request.getParameter("status"));
String storeType=StringUtil.convertNull(request.getParameter("storeType"));
String istorageId = StringUtil.convertNull(request.getParameter("storageId"));
String storageCode=StringUtil.convertNull(request.getParameter("storageCode"));
String otherCargo=StringUtil.convertNull(request.getParameter("otherCargo"));
if(status.equals("") || status==""){
	status = String.valueOf(request.getAttribute("firstStatus"));
}
if(storeType.equals("") || storeType==""){
	storeType = String.valueOf(request.getAttribute("firstStoreType"));
}
 
String productLineId =  StringUtil.convertNull(request.getParameter("productLineId"));
if(productLineId.equals("")|| productLineId==""){
	productLineId = String.valueOf(request.getAttribute("firstProductLineId"));
}

CargoInfoBean outCiBean = (CargoInfoBean)request.getAttribute("outCiBean");
PagingBean paging = (PagingBean) request.getAttribute("paging");
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function selectstorage(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&storageId="+document.getElementById("storageId").value +"&stockType=<%=outCiBean.getStockType()%>", 
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("stockArea").innerHTML = msg;
			selectOption(document.getElementById("stockAreaId"),"<%=request.getParameter("stockAreaId")%>");
			$.ajax({
				type: "GET",
				url: "cargoInfo.do?method=selection&stockAreaId="+document.getElementById("stockAreaId").value,
				cache: false,
				dataType: "html",
				data: {type: "1"},
				success: function(msg, reqStatus){
					document.getElementById("shelf").innerHTML = msg;
					selectOption(document.getElementById("shelfId"),"<%=request.getParameter("shelfId")%>");
					$.ajax({
						type: "GET",
						url: "cargoInfo.do?method=selection&shelfId="+document.getElementById("shelfId").value,
						cache: false,
						dataType: "html",
						data: {type: "1"},
						success: function(msg, reqStatus){
							document.getElementById("floor").innerHTML = msg;
							selectOption(document.getElementById("floorNum"),"<%=request.getParameter("floorNum")%>");
						}
					});
				}
			});
		}
	});
}
function selectstockarea(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&stockAreaId="+document.getElementById("stockAreaId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("shelf").innerHTML = msg;
			selectOption(document.getElementById("shelfId"),"<%=request.getParameter("shelfId")%>");
			$.ajax({
				type: "GET",
				url: "cargoInfo.do?method=selection&shelfId="+document.getElementById("shelfId").value,
				cache: false,
				dataType: "html",
				data: {type: "1"},
				success: function(msg, reqStatus){
					document.getElementById("floor").innerHTML = msg;
					selectOption(document.getElementById("floorNum"),"<%=request.getParameter("floorNum")%>");
				}
			});
		}
	});
}
function selectshelf(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&shelfId="+document.getElementById("shelfId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("floor").innerHTML = msg;
			selectOption(document.getElementById("floorNum"),"<%=request.getParameter("floorNum")%>");
		}
	});
}
function check(){
	 var cargoProducId = document.getElementsByName('inCargoId');
	 for(var i=0;i<cargoProducId.length;i++){
   	if(cargoProducId[i].checked ==true){
   		return true;
   	}
   }
   alert("请选择货位!");
   return false;
}
</script>
</head>
<body>
	<div style="margin:30px">
	<p>分配货位</p>
	<form action="../admin/cargoOperation.do?method=usefulCargo" method="post">
	<fieldset style="width:700px;"><legend>查询栏</legend>
		货位编号：<input type="text" id="cargoCode" size=20 name="cargoCode" <%if(request.getParameter("cargoCode")!=null){ %>value='<%=request.getParameter("cargoCode") %>'<%} %>/>
		<input type="radio" name="mode" value="0" checked=checked/>精确查询&nbsp;&nbsp;&nbsp;
		<input type="radio" name="mode" value="1"  <%if(request.getParameter("mode")!=null&&request.getParameter("mode").equals("1")){ %>checked=checked<%} %>/>左精确右模糊查询&nbsp;&nbsp;&nbsp;
		货位状态：<select name="status">
					<option value="">请选择</option>
					<option value=0 <%if(status!=null && status.equals("0")){ %>selected=selected<%} %>>使用中</option>
					<option value=1 <%if(status!=null && status.equals("1")){ %>selected=selected<%} %>>未使用</option>
				</select><br/>
		仓库代号：<select id="storageId" name="storageId" onchange="selectstorage();">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){%>
					<%CargoInfoStorageBean storageBean=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storageBean.getId() %>" <%if(istorageId.equals(""+storageBean.getId())){ %>selected=selected<%} %>><%=storageBean.getWholeCode() %></option>
				<%} %>
				</select>
		所属区域：<span id="stockArea">
				<select name="stockArea">
					<option value="">请选择</option>
				</select>
				</span>
		货架代号：<span id="shelf">
				<select name="shelfId">
					<option value="">请选择</option>
				</select>
				</span>
		第几层：<span id="floor">
				<select name="floorNum">
					<option value="">请选择</option>
				</select>
				</span>	<br/>
		存放类型：<select name="storeType" <%if(otherPagePass.equals("downShelf")){ %> disabled="disabled" <%} %>>
					<option value="">请选择</option>
					<option value="0" <%if(storeType!=null && storeType.equals("0")){ %>selected=selected<%} %>>散件区</option>
					<option value="1" <%if(storeType!=null && storeType.equals("1")){ %>selected=selected<%} %>  <%if(otherPagePass.equals("downShelf")){ %> selected="selected" <%} %>>整件区</option>
					<option value="4" <%if(storeType!=null && storeType.equals("4")){ %>selected=selected<%} %>>混合区</option>
				</select>
		产品线：<select name="productLineId">
					<option value="">请选择</option>
					<%for(int i=0;i<productLineList.size();i++){%>
					<%voProductLine productLine=(voProductLine)productLineList.get(i); %>
					<option value="<%=productLine.getId() %>"<%if(productLineId!=null&&productLineId.equals(""+productLine.getId())){ %>selected=selected<%} %>><%=productLine.getName() %></option>
				<%} %>
				</select>
		货位类型：<select name="type">
					<option value="">请选择</option>
					<option value="0" <%if(request.getParameter("type")!=null&&request.getParameter("type").equals("0")){ %>selected=selected<%} %>>普通</option>
					<option value="1" <%if(request.getParameter("type")!=null&&request.getParameter("type").equals("1")){ %>selected=selected<%} %>>热销</option>
					<option value="2" <%if(request.getParameter("type")!=null&&request.getParameter("type").equals("2")){ %>selected=selected<%} %>>滞销</option>
				</select>
		货位最大容量：<input type="text" size=2 name="minMaxStockCount" <%if(request.getParameter("minMaxStockCount")!=null){ %>value='<%=request.getParameter("minMaxStockCount") %>'<%} %>/>
		至<input type="text" size=2 name="maxMaxStockCount" <%if(request.getParameter("maxMaxStockCount")!=null){ %>value='<%=request.getParameter("maxMaxStockCount") %>'<%} %>/><br/>
		<input type="hidden" name="operId" value="<%=operId%>"/>
		<input type="hidden" name="outCpsId" value="<%=outCpsId%>"/>
		<input type="hidden" name="otherPagePass" value="<%=otherPagePass%>"/>
		<input type="submit" value="查询">
	</fieldset>
	</form>
	<form action="../admin/cargoOperation.do?method=submitUsefulCargo" method="post">
	<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5">
		<tr bgcolor="#4688D6" >
			<td><font color="#FFFFFF">选择</font></td>
			<td><font color="#FFFFFF">货位编号</font></td>
			<td><font color="#FFFFFF">库存类型</font></td>
			<td><font color="#FFFFFF">货位尺寸/cm</font></td>
			<td><font color="#FFFFFF">货位最大容量</font></td>
			<td><font color="#FFFFFF">警戒线</font></td>
			<td><font color="#FFFFFF">存放类型</font></td>
			<td><font color="#FFFFFF">货位状态</font></td>
			<td><font color="#FFFFFF">备注</font></td>
			<td><font color="#FFFFFF">产品</font></td>
		</tr>
		<%for(int i=0;i<inCargoBeanList.size();i++){ %>
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<%CargoInfoBean ciBean=(CargoInfoBean)inCargoBeanList.get(i); %>
				<%List inCpsList2=(List)inCpsList.get(i); %>
				<td><input type="checkbox" name="inCargoId" value="<%=ciBean.getId() %>"/></td>
				<td><%=ciBean.getWholeCode() %></td>
				<td>
				<%if(ciBean.getStockType()==0){ %>合格库
					<%}else if(ciBean.getStockType()==1){ %>待验库
					<%}else if(ciBean.getStockType()==2){ %>维修库
					<%}else if(ciBean.getStockType()==3){ %>返厂库
					<%}else if(ciBean.getStockType()==4){ %>退货库
					<%}else if(ciBean.getStockType()==5){ %>残次品库
					<%}else if(ciBean.getStockType()==6){ %>样品库
					<%}else if(ciBean.getStockType()==7){ %>质检库（虚拟库）
					<%}else if(ciBean.getStockType()==8){ %>换货库（虚拟库）
					<%}else if(ciBean.getStockType()==9){ %>售后库
				<%} %>		
				</td>
				<td><%=ciBean.getLength() %>/<%=ciBean.getWidth() %>/<%=ciBean.getHigh() %></td>
				<td><%=ciBean.getMaxStockCount() %></td>
				<td><%=ciBean.getWarnStockCount() %></td>
				<td>
					<%=ciBean.getStoreTypeName() %>
				</td>
				<td><font color="green"><%if(ciBean.getStatus()==0){ %>
						使用中
					<%}else if(ciBean.getStatus()==1){ %>
						未使用
					<%}else if(ciBean.getStatus()==2){ %>
						未开通
					<%} %></font>
				</td>
				<td><%=ciBean.getRemark().length()>20?ciBean.getRemark().substring(0,20)+"...":ciBean.getRemark()%></td>
				<td><%for(int j=0;j<inCpsList2.size();j++){ %>
				<%CargoProductStockBean inCps=(CargoProductStockBean)inCpsList2.get(j); %>
					<%if(inCps.getProduct()!=null){ %>
						<%=inCps.getProduct().getCode() %>
						<%=inCps.getProduct().getOriname() %>
						(<%=inCps.getStockCount()+inCps.getStockLockCount() %>)
						<%if(j!=inCpsList2.size()-1){ %><%=","%><br/><%} %>
					<%} %>
				<%} %>
				</td>
			</tr>
		<%} %>
	</table>
	<input type="hidden" name="otherPagePass" value="<%=otherPagePass%>"/>
	<input type="hidden" name="operId" value="<%=operId%>">
	<input type="hidden" name="outCpsId" value="<%=outCpsId %>">
	<input type="hidden" name="productId" value="<%=productId %>">
	<input type="submit" onclick="return check();" value="确定"/>&nbsp;&nbsp;<input type="reset" value="重置"/>&nbsp;
	<%if(otherPagePass.equals("")){ %><input type="button" value="返回" onclick="window.location='../admin/cargoOperation.do?method=exchangeCargo&Cargo&cargoOperId=<%=operId %>'" />
	<%}else if(otherPagePass.equals("upShelf")){ %><input type="button" value="返回" onclick="window.location='../admin/cargoOper.do?method=showEditCargoOperation&operationId=<%=operId %>'" />
	<%}else if(otherPagePass.equals("downShelf")){ %><input type="button" value="返回" onclick="window.location='../admin/cargoDownShelf.do?method=showDownShel&id=<%=operId %>'" />
	<%} %>
	</form>
	<%if(paging!=null){ %>
		<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
	</div>
	<script type="text/javascript">selectstorage();</script>
</body>
</html>
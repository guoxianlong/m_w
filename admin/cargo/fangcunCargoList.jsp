<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.util.StringUtil" %>
<%@ page import="adultadmin.util.Encoder" %>
<%@ page import="adultadmin.util.PageUtil,adultadmin.bean.PagingBean" %>

<%@page import="mmb.stock.cargo.CartonningInfoBean"%>
<%@page import="mmb.stock.cargo.CartonningProductInfoBean"%><html>
<head>
<title>芳村仓货位列表</title>
<%
List list=null;
List productLineNameList=null;
List productLineList=null;
List storageList=null;
List operCountList=null;
if(request.getAttribute("operCountList")!=null){
	operCountList=(List)request.getAttribute("operCountList");
}
if(request.getAttribute("storageList")!=null){
	storageList=(List)request.getAttribute("storageList");
}
if(request.getAttribute("productLineList")!=null){
	productLineList=(List)request.getAttribute("productLineList");
}
if(request.getAttribute("list")!=null){
	list=(List)request.getAttribute("list");
}
if(request.getAttribute("productLineNameList")!=null){
	productLineNameList=(List)request.getAttribute("productLineNameList");
}
String[] status=request.getParameterValues("status");
PagingBean paging = (PagingBean) request.getAttribute("paging");
String storeType=StringUtil.convertNull(request.getParameter("storeType"));
String wholeCode=StringUtil.convertNull(request.getParameter("wholeCode"));
String productCode=StringUtil.convertNull(request.getParameter("productCode"));
String stockCountStart=StringUtil.convertNull(request.getParameter("stockCountStart"));
String stockCountEnd=StringUtil.convertNull(request.getParameter("stockCountEnd"));
String shelfId=StringUtil.convertNull(request.getParameter("shelfId"));
String productLineId=StringUtil.convertNull(request.getParameter("productLineId"));
String productName=StringUtil.convertNull(request.getParameter("productName"));
if(Encoder.decrypt(productName)!=null){
	productName=Encoder.decrypt(productName);
}
String type=StringUtil.convertNull(request.getParameter("type"));
String storageId=StringUtil.convertNull(request.getParameter("storageId"));
String stockAreaId=StringUtil.convertNull(request.getParameter("stockAreaId"));
String shelfCode=StringUtil.convertNull(request.getParameter("shelfCode"));
String floorNum=StringUtil.convertNull(request.getParameter("floorNum"));
String cartonningCode=StringUtil.convertNull(request.getParameter("cartonningCode"));
%>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="../js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
<%if(request.getAttribute("add")!=null){%>
alert("操作成功！");
<%}%>
</script>
<script type="text/javascript">
function selectstorage(){
	$.ajax({
		type: "GET",
		url: "cargoInfo.do?method=selection&storageId="+document.getElementById("storageId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("stockArea").innerHTML = msg;
			<%if(request.getParameter("stockAreaId")!=null){%>
				selectOption(document.getElementById('stockAreaId'), '<%= request.getParameter("stockAreaId") %>');
			<%}%>
		}
	});
}
</script>
</head>
<body>
	<%if(request.getAttribute("fangcun")!=null){ %>
		芳村仓货位列表
	<%}else{ %>
		货位列表
	<%} %>
	<form action="../admin/cargoInfo.do?method=selectFangcunList" method="post">
		<fieldset style="width:780px;"><legend>查询栏</legend>
		货&nbsp;&nbsp;位&nbsp;号：<input type="text" size=12 name="wholeCode" <%if(wholeCode!=null){ %>value="<%=wholeCode%>"<%}%> />左精确右模糊查询&nbsp;&nbsp;&nbsp;&nbsp;
		产品编号：<input type="text" size=12 name="productCode" <%if(productCode!=null){ %>value="<%=productCode%>"<%} %> />&nbsp;&nbsp;&nbsp;&nbsp;
		产品原名称：<input type="text" size=20 name="productName" <%if(productName!=null){ %>value="<%=productName %>"<%} %>/>精确<br/>
		存放类型：<select name="storeType">
					<option value="">请选择</option>
					<option value="0" <%if(storeType!=null&&storeType.equals("0")){ %>selected=selected<%} %>>散件区</option>
					<option value="1" <%if(storeType!=null&&storeType.equals("1")){ %>selected=selected<%} %>>整件区</option>
					<option value="4" <%if(storeType!=null&&storeType.equals("4")){ %>selected=selected<%} %>>混合区</option>
					<option value="2" <%if(storeType!=null&&storeType.equals("2")){ %>selected=selected<%} %>>缓存区</option>
				</select>&nbsp;&nbsp;&nbsp;&nbsp;
		货位产品线：<select name="productLineId">
					<option value="">请选择</option>
					<%for(int i=0;i<productLineList.size();i++){ %>
						<%voProductLine productLine=(voProductLine)productLineList.get(i); %>
					<option value="<%=productLine.getId() %>" <%if(productLineId!=null&&(!productLineId.equals(""))&&Integer.parseInt(productLineId)==productLine.getId()){ %>selected=selected<%} %>><%=productLine.getName() %></option>
					<%} %>
		           </select>&nbsp;&nbsp;&nbsp;&nbsp;
		货位类型：<select name="type">
					<option value="">请选择</option>
					<option value=0 <%if(type!=null&&type.equals("0")){ %>selected=selected<%} %>>普通</option>
					<option value=1 <%if(type!=null&&type.equals("1")){ %>selected=selected<%} %>>热销</option>
					<option value=2 <%if(type!=null&&type.equals("2")){ %>selected=selected<%} %>>滞销</option>
		         </select><br/>
		
		仓&nbsp;&nbsp;库&nbsp;号：<select id="storageId" name="storageId" onchange="selectstorage()">
				<option value="">请选择</option>
				<%for(int i=0;i<storageList.size();i++){ %>
						<%CargoInfoStorageBean storageBean=(CargoInfoStorageBean)storageList.get(i); %>
					<option value="<%=storageBean.getId() %>" <%if(storageId!=null&&(!storageId.equals(""))&&Integer.parseInt(storageId)==storageBean.getId()){ %>selected=selected<%} %>><%=storageBean.getWholeCode() %></option>
					<%} %>
		       </select>&nbsp;&nbsp;&nbsp;&nbsp;
		仓&nbsp;库&nbsp;区&nbsp;域：<span id="stockArea">
					<select name="stockAreaId">
						<option value="">请选择</option>
					</select>
				</span>&nbsp;&nbsp;&nbsp;&nbsp;
		货位当前库存：<input type="text" name="stockCountStart" size=3 <%if(stockCountStart!=null){ %>value="<%=stockCountStart%>"<%} %>/>
						至<input type="text" name="stockCountEnd" size=3 <%if(stockCountEnd!=null){ %>value="<%=stockCountEnd%>"<%} %>/><br/>
		货架代号：<input type="text" size=10 name="shelfCode" <%if(shelfCode!=null){ %>value='<%=shelfCode%>'<%} %>/>（如：01）&nbsp;&nbsp;&nbsp;&nbsp;
		第几层：<input type="text" size=5 name="floorNum" <%if(floorNum!=null){ %>value='<%=floorNum%>'<%} %> />&nbsp;&nbsp;&nbsp;&nbsp;
		货位状态：<input type="checkbox" name="status" value="0" />使用中
				<input type="checkbox" name="status" value="1" />未使用
				<input type="checkbox" name="status" value="2" />未开通&nbsp;&nbsp;&nbsp;&nbsp;<br>
		装箱单编号：<input type="text"  name="cartonningCode" <%if(cartonningCode!=null){ %>value='<%=cartonningCode%>'<%} %> />&nbsp;&nbsp;&nbsp;&nbsp;		
		<input type="submit" value="查询">
		</fieldset>
	</form>
	<script type="text/javascript">selectstorage();</script>
	<table cellpadding="3" border=1 style="border-collapse:collapse;" bordercolor="#D8D8D5">
		<tr bgcolor="#4688D6" >
			<td><font color="#FFFFFF">序号</font></td>
			<td width="100"><font color="#FFFFFF">货位号</font></td>
			<td><font color="#FFFFFF">货位产品线</font></td>
			<td><font color="#FFFFFF">产品编号</font></td>
			<td><font color="#FFFFFF">产品原名称</font></td>
			<td width="90"><font color="#FFFFFF">当前货位库存（其中冻结量）</font></td>
			<td><font color="#FFFFFF">装箱单号</font></td>
			<td><font color="#FFFFFF">装箱数量</font></td>
			<td><font color="#FFFFFF">货位空间冻结</font></td>
			<td><font color="#FFFFFF">货位警戒线</font></td>
			<td><font color="#FFFFFF">货位最大容量</font></td>
			<td><font color="#FFFFFF">存放类型</font></td>
			<td><font color="#FFFFFF">货位类型</font></td>
			<td><font color="#FFFFFF">货位状态</font></td>
			<td><font color="#FFFFFF">备注</font></td>
			<td><font color="#FFFFFF">未完成作业单数</font></td>
			<td><font color="#FFFFFF">操作</font></td>
			<td><font color="#FFFFFF">进销存</font></td>
		</tr>
		<%for(int i=0;i<list.size();i++){ %>
			<tr <%if(i%2==0){ %>bgcolor="#eee9d9"<%}else{ %>bgcolor="#ffffff"<%} %>>
				<%CargoProductStockBean bean=(CargoProductStockBean)list.get(i); %>
				<td><%=paging.getCurrentPageIndex()*paging.getCountPerPage()+i+1 %></td>
				<td><a href="../admin/cargoInfo.do?method=updateCargoPage&cargoProductStockId=<%=bean.getId() %>&cargoId=<%=bean.getCargoInfo().getId()%>" target="_blank"><%=bean.getCargoInfo().getWholeCode() %></a></td>
				<td><%=productLineNameList.get(i) %></td>
				<td><%if(bean.getProduct().getCode()!=null){ %><a href="../admin/fproduct.do?id=<%=bean.getProductId() %>" target="_blank"><%=bean.getProduct().getCode() %></a><%}else{ %>-<%} %></td>
				<td><%if(bean.getProduct().getOriname()!=null){ %><a href="../admin/fproduct.do?id=<%=bean.getProductId() %>" target="_blank"><%=bean.getProduct().getOriname() %></a><%}else{ %>-<%} %></td>
				<td><%=bean.getStockCount()+bean.getStockLockCount() %>(<%=bean.getStockLockCount() %>)</td>
				<td>
					<%if(bean.getCartonningList()!=null){ %>
						<%for(int j=0;j<bean.getCartonningList().size();j++){ %>
							<%CartonningInfoBean cartonningBean=(CartonningInfoBean)(bean.getCartonningList().get(j)); %>
							<%=cartonningBean.getCode() %><br/>
						<%} %>
					<%} %>
				</td>
				<td>
					<%if(bean.getCartonningList()!=null){ %>
						<%for(int j=0;j<bean.getCartonningList().size();j++){ %>
							<%CartonningInfoBean cartonningBean=(CartonningInfoBean)(bean.getCartonningList().get(j)); %>
							<%CartonningProductInfoBean cartonningProduct=cartonningBean.getProductBean(); %>
							<%=cartonningProduct.getProductCount() %><br/>
						<%} %>
					<%} %>
				</td>
				<td><%=bean.getCargoInfo().getSpaceLockCount() %></td>
				<td><%=bean.getCargoInfo().getWarnStockCount() %></td>
				<td><%=bean.getCargoInfo().getMaxStockCount() %></td>
				<td><%=bean.getCargoInfo().getStoreTypeName() %></td>
				<td><%=bean.getCargoInfo().getTypeName() %></td>
				<td><%=bean.getCargoInfo().getStatusName() %></td>
				<td><%if(bean.getCargoInfo().getRemark().length()>3){ %>
						<%=bean.getCargoInfo().getRemark().substring(0,3) %>...
					<%}else{ %>
						<%=bean.getCargoInfo().getRemark() %>
					<%} %>
				</td>
				<td><%=operCountList.get(i).toString() %></td>
				<td><%if(bean.getCargoInfo().getStatus()==1&&bean.getCargoInfo().getSpaceLockCount()==0){ %>
						<form action="../admin/cargoInfo.do?method=closeCargo" method="post">
							<input type="hidden" name="pageIndex" value="<%=paging.getCurrentPageIndex() %>" />
							<input type="hidden" name="cargoId" value="<%=bean.getCargoInfo().getId() %>" />
							<input type="hidden" name="wholeCode" value="<%=wholeCode%>" />
							<input type="hidden" name="productCode" value="<%=productCode%>" />
							<input type="hidden" name="stockCountStart" value="<%=stockCountStart %>" />
							<input type="hidden" name="stockCountEnd" value="<%=stockCountEnd %>" />
							<input type="hidden" name="shelfId" value="<%=shelfId %>" />
							<input type="hidden" name="storeType" value="<%=storeType %>" />
							<%if(status!=null){ %>
								<%for(int j=0;j<status.length;j++){ %>
									<input type="hidden" name="status" value="<%=status[j] %>" />
								<%} %>
							<%} %>
							<input type="hidden" name="productName" value="<%=productName %>" />
							<input type="hidden" name="productLineId" value="<%=productLineId %>" />
							<input type="hidden" name="type" value="<%=type %>" />
							<input type="hidden" name="storageId" value="<%=storageId %>" />
							<input type="hidden" name="stockAreaId" value="<%=stockAreaId %>" />
							<input type="hidden" name="shelfCode" value="<%=shelfCode %>" />
							<input type="hidden" name="floorNum" value="<%=floorNum %>" />
							<input type="submit" name="closeCargo" value="关闭货位" onclick="return confirm('如果确认关闭该货位，请单击‘确定’，反之请单击‘取消’！');"/>
						</form>
					<%}else if(bean.getCargoInfo().getStatus()==0&&bean.getStockCount()==0&&bean.getStockLockCount()==0&&bean.getCargoInfo().getSpaceLockCount()==0&&operCountList.get(i).toString().equals("0")){ %>
						<form action="../admin/cargoInfo.do?method=clearCargo" method="post">
							<input type="hidden" name="pageIndex" value="<%=paging.getCurrentPageIndex() %>" />
							<input type="hidden" name="cargoId" value="<%=bean.getCargoInfo().getId() %>" />
							<input type="hidden" name="productId" value="<%=bean.getProductId() %>" />
							
							<input type="hidden" name="wholeCode" value="<%=wholeCode%>" />
							<input type="hidden" name="productCode" value="<%=productCode%>" />
							<input type="hidden" name="stockCountStart" value="<%=stockCountStart %>" />
							<input type="hidden" name="stockCountEnd" value="<%=stockCountEnd %>" />
							<input type="hidden" name="shelfId" value="<%=shelfId %>" />
							<input type="hidden" name="storeType" value="<%=storeType %>" />
							<%if(status!=null){ %>
								<%for(int j=0;j<status.length;j++){ %>
									<input type="hidden" name="status" value="<%=status[j] %>" />
								<%} %>
							<%} %>
							<input type="hidden" name="productName" value="<%=productName %>" />
							<input type="hidden" name="productLineId" value="<%=productLineId %>" />
							<input type="hidden" name="type" value="<%=type %>" />
							<input type="hidden" name="storageId" value="<%=storageId %>" />
							<input type="hidden" name="stockAreaId" value="<%=stockAreaId %>" />
							<input type="hidden" name="shelfCode" value="<%=shelfCode %>" />
							<input type="hidden" name="floorNum" value="<%=floorNum %>" />
							<%if(bean.getCargoInfo().getStoreType()!=2){ %>
							<input type="submit" name="clearCargo" value="清空货位" onclick="return confirm('如果确认清空该货位，请单击‘确定’，反之请单击‘取消’！');"/>
							<%} %>
						</form>
					<%} %>
				</td>
				<td><%if(bean.getProductId()>0){ %><a href="../admin/cargoInfo.do?method=cargoStockCard&productCode=<%=bean.getProduct().getCode()%>&cargoWholeCode=<%=bean.getCargoInfo().getWholeCode() %>" target="_blank">查</a><%} %></td>
			</tr>
		<%} %>
	</table>
	<form action="../admin/cargoInfo.do?method=cargoListPrint" method="post">
	<input type="hidden" name="wholeCode" value="<%=wholeCode%>" />
	<input type="hidden" name="productCode" value="<%=productCode%>" />
	<input type="hidden" name="stockCountStart" value="<%=stockCountStart %>" />
	<input type="hidden" name="stockCountEnd" value="<%=stockCountEnd %>" />
	<input type="hidden" name="areaId" value="1" />
	<input type="hidden" name="shelfId" value="<%=shelfId %>" />
	<input type="hidden" name="storeType" value="<%=storeType %>" />
	<%if(status!=null){ %>
		<%for(int j=0;j<status.length;j++){ %>
			<input type="hidden" name="status" value="<%=status[j] %>" />
		<%} %>
	<%} %>
	<input type="hidden" name="productName" value="<%=productName %>" />
	<input type="hidden" name="productLineId" value="<%=productLineId %>" />
	<input type="hidden" name="type" value="<%=type %>" />
	<input type="hidden" name="storageId" value="<%=storageId %>" />
	<input type="hidden" name="stockAreaId" value="<%=stockAreaId %>" />
	<input type="hidden" name="shelfCode" value="<%=shelfCode %>" />
	<input type="hidden" name="floorNum" value="<%=floorNum %>" />
	<input type="submit" value="导出全部货位"/>
	</form>
	<%if(paging!=null){ %>
		<p align="center"><%=PageUtil.fenye(paging, true, "&nbsp;&nbsp;", "pageIndex", 20)%></p>
	<%} %>
	<script type="text/javascript">
		<%if(status!=null){%>
			<%for(int i=0;i<status.length;i++){%>
				document.getElementsByName("status")[<%=status[i]%>].checked=true;
			<%}%>
		<%}%>
	</script>
</body>
</html>
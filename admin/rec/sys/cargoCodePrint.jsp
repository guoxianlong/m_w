<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ page import="java.util.List" %>
<%@ page import="adultadmin.bean.cargo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.PagingBean,adultadmin.util.*" %>
<html>
<head>
<title>货位条码打印</title>
<%
	String cityId = "";
	String areaId = "";
	String storageId = "";
	if (request.getParameter("cityId") != null) {
		cityId = "&cityId=" + request.getParameter("cityId");
	}
	if (request.getParameter("areaId") != null) {
		areaId = "&areaId=" + request.getParameter("areaId");
	}
	if (request.getParameter("storageId") != null) {
		storageId = "&storageId=" + request.getParameter("storageId");
	}
	String passageId = StringUtil.convertNull(request.getParameter("passageId"));
	String shelfId = StringUtil.convertNull(request.getParameter("shelfId"));
	String cargoCode = StringUtil.convertNull(request.getParameter("cargoCode"));
%>
<%
	List cargoList = (List) request.getAttribute("cargoList");
%>
<%
	List wholeStorageList = (List) request.getAttribute("wholeStorageList");
%>
<%
	List wholeCityList = (List) request.getAttribute("wholeCityList");
%>
<%
	List stockAreaCodeList = (List) request.getAttribute("stockAreaCodeList");
%>
<%
	List cargoCountList = (List) request.getAttribute("cargoCountList");
%>
<%
	PagingBean paging = (PagingBean) request.getAttribute("paging");
%>

<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script language="javascript" src="<%=request.getContextPath()%>/admin/barcodeManager/LodopFuncs.js"></script>
<object id="LODOP" classid="clsid:2105C259-1E0C-4534-8141-A753534CB4CA"width=0 height=0> <embed id="LODOP_EM"
		type="application/x-print-lodop" width=0 height=0
		pluginspage="install_lodop.exe"></embed> </object>
<script>var textname = 'proxytext';</script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/count2.js"></script>
<script type="text/javascript">
function selectcity(){
	$.ajax({
		type: "GET",
		url: "<%=request.getContextPath()%>/CargoController/selection.mmx?cityId="+document.getElementById("cityId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("area").innerHTML = msg;
			selectOption(document.getElementById('areaId'), '<%=request.getParameter("areaId")%>');
			selectOption(document.getElementById('areaId'), '<%=request.getAttribute("areaId")%>');
			$.ajax({
				type: "GET",
				url: "<%=request.getContextPath()%>/CargoController/selection.mmx?areaId="+document.getElementById("areaId").value,
				cache: false,
				dataType: "html",
				data: {type: "1"},
				success: function(msg, reqStatus){
					document.getElementById("storage").innerHTML = msg;
					selectOption(document.getElementById('storageId'), '<%=request.getParameter("storageId")%>');
					selectOption(document.getElementById('storageId'), '<%=request.getAttribute("storageId")%>');
					$.ajax({
						type: "GET",
						url: "<%=request.getContextPath()%>/CargoController/selection.mmx?storageId="+document.getElementById("storageId").value,
						cache: false,
						dataType: "html",
						data: {type: "1"},
						success: function(msg, reqStatus){
							document.getElementById("stockArea").innerHTML = msg;
							selectOption(document.getElementById('stockAreaId'), '<%=request.getParameter("stockAreaId")%>');
							selectOption(document.getElementById('stockAreaId'), '<%=request.getAttribute("stockAreaId")%>');
							$.ajax({
								type: "GET",
								url: "<%=request.getContextPath()%>/CargoController/selection.mmx?stockAreaId="+document.getElementById("stockAreaId").value,
								cache: false,
								dataType: "html",
								data: {type: "1"},
								success: function(msg, reqStatus){
									document.getElementById("passage").innerHTML = msg;
									selectOption(document.getElementById('passageId'), '<%= request.getParameter("passageId") %>');
									selectOption(document.getElementById('passageId'), '<%= request.getAttribute("passageId") %>');
							        $.ajax({
										type: "GET",
										url: "<%=request.getContextPath()%>/CargoController/selection.mmx?passageId="+document.getElementById("passageId").value,
										cache: false,
										dataType: "html",
										data: {type: "1"},
										success: function(msg, reqStatus){
											document.getElementById("shelf").innerHTML = msg;
											selectOption(document.getElementById('shelfId'), '<%= request.getParameter("shelfId") %>');
											selectOption(document.getElementById('shelfId'), '<%= request.getAttribute("shelfId") %>');
										}
									});
								}
							});
						}
					});
				}
			});
		}
	});
}
function selectarea(){
	$.ajax({
		type: "GET",
		url: "<%=request.getContextPath()%>/CargoController/selection.mmx?areaId="+document.getElementById("areaId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("storage").innerHTML = msg;
			selectOption(document.getElementById('storageId'), '<%=request.getParameter("storageId")%>');
			$.ajax({
				type: "GET",
				url: "<%=request.getContextPath()%>/CargoController/selection.mmx?storageId="+document.getElementById("storageId").value,
				cache: false,
				dataType: "html",
				data: {type: "1"},
				success: function(msg, reqStatus){
					document.getElementById("stockArea").innerHTML = msg;
					selectOption(document.getElementById('stockAreaId'), '<%=request.getParameter("stockAreaId")%>');
				}
			});
		}
	});
}
function selectstorage(){
	$.ajax({
		type: "GET",
		url: "<%=request.getContextPath()%>/CargoController/selection.mmx?storageId="+document.getElementById("storageId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("stockArea").innerHTML = msg;
		}
	});
}
 
function selectstockarea(){
	$.ajax({
		type: "GET",
		url: "<%=request.getContextPath()%>/CargoController/selection.mmx?stockAreaId="+document.getElementById("stockAreaId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("passage").innerHTML = msg;
		}
	});
}
function selectpassage(){
	$.ajax({
		type: "GET",
		url: "<%=request.getContextPath()%>/CargoController/selection.mmx?passageId="+document.getElementById("passageId").value,
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			document.getElementById("shelf").innerHTML = msg;
		}
	});
}
function clearPassageCode(){
	document.getElementById("passage").value=""
}
function clearCargoCode(){
	document.getElementById("cargoId").value=""
}
function initPrint(){
	var LODOP;
	cssStyle = "<style>table{font-size:55px;}</style>";
	var LODOP = getLodop(document.getElementById('LODOP'),document.getElementById('LODOP_EM'));
	
	if(LODOP.PRINT_INIT("")){
	<%if(cargoList!=null){
	for (int i = 0; i < cargoList.size(); i++) {
				CargoInfoBean cargoBean = (CargoInfoBean) cargoList.get(i);%>
		        LODOP.ADD_PRINT_TABLE("-0.3cm","0.01cm","2cm","12cm",cssStyle+document.getElementById("tableDiv<%=i%>").innerHTML);
				LODOP.ADD_PRINT_BARCODE("17mm","8mm","90mm","20mm","CODE93","<%=cargoBean.getWholeCode()%>");
				LODOP.SET_PRINTER_INDEX(-1);
				LODOP.NEWPAGE();
// 				LODOP.PREVIEWB();
			    LODOP.PRINTB();
		        LODOP.SET_PRINT_PAGESIZE(0,"100mm","40mm","");

	<%}}%>
	}
}
    function IsNum(num){
	  var reNum=/^\d*$/;
	  return(reNum.test(num));
	}
	function checksubmit(){
		if(!document.getElementById("cargoId").value.length==0&& document.getElementById("cargoId").value!="货位编号" &&(document.getElementById("cargoId").value.length!=12 && document.getElementById("cargoId").value.length!=14)){
			alert("货位长度必须为12或者14")
 			return false;
		}
	}
	function click(){
		document.getElementById("cargoId").value="";
	}
</script>
<style type="text/css">
<!--
.STYLE2 {
	font-size: 18px;
	font-weight: bold;
}

-->
</style>
</head>
<body>
<span class="STYLE2">货位条码打印</span>
<form action="<%=request.getContextPath()%>/CargoController/cargoCodePrint.mmx" method="post" onsubmit="return checksubmit();">
	<fieldset style="width:800px;"><legend>查询栏</legend>
	所属城市:
	<select id="cityId" name="cityId" onChange="selectcity();">
		<option value="">请选择</option>
		<%if(wholeCityList!=null){
			for (int i = 0; i < wholeCityList.size(); i++) {
				CargoInfoCityBean cityBean = (CargoInfoCityBean) wholeCityList.get(i);
		%>
			<option value="<%=cityBean.getId()%>" <%if (request.getParameter("cityId") != null && (!request.getParameter("cityId").equals("")) && Integer.parseInt(request.getParameter("cityId")) == cityBean.getId()) {%>selected=selected<%}%>>
				<%=cityBean.getCode()%>--<%=cityBean.getName()%>
			</option>
		<%
			}}
		%>
	</select>
	所属地区:
	<span id="area">
	<select name="areaId">
		<option value=""></option>
	</select>
	</span>
	所属仓库:
	<span id="storage">
	<select name="storageId">
		<option value=""></option>
	</select>
	</span>
	所属区域:
	<span id="stockArea">
	<select name="stockAreaId" id="stockAreaId" >
		<option value=""></option>
	</select>
	</span><br/><br/>
	所属巷道
    <span id='passage'>
	<select id="passageId" name="passageId">
		<option value="">请选择</option>
	</select>
    </span>
         货架代号
    <span id='shelf'>
	<select id="shelfId" name="shelfId">
		<option value="">请选择</option>
	</select>
    </span>
	货位编号:
	<input name="cargoCode" id="cargoId" type="text" value="货位编号" size=20 value='<%=cargoCode%>' onfocus="if(this.value=='货位编号'){this.value=''}">
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<input type="submit" value="打印货位条码"/>
	</fieldset>
</form>

	说明:不指定货位编号则为批量打印
	<%
	if (request.getAttribute("cityId") != null) {
%>
		<script type="text/javascript">
			selectOption(document.getElementById("cityId"),<%=request.getAttribute("cityId")%>);
		</script>
	<%
		}
	%>
	<%
		if (cargoList != null) {
	%>
	<%
		for (int i = 0; i < cargoList.size(); i++) {
				CargoInfoBean cargoBean = (CargoInfoBean) cargoList.get(i);
	%>
	<div id="tableDiv<%=i%>" align="center" >

		<table width='350' style="visibility:hidden">
			<tr >
				<td align="center">
				<%if(cargoBean.getWholeCode().length()==14){%>
					<b><%=cargoBean.getWholeCode().substring(6,9)%>-<%=cargoBean.getWholeCode().substring(9,11)%>-<%=cargoBean.getWholeCode().substring(11,12)%>-<%=cargoBean.getWholeCode().substring(12)%></b>
				<%} %>
				<%if(cargoBean.getWholeCode().length()==12){%>
					<b><%=cargoBean.getWholeCode().substring(6,9)%>-<%=cargoBean.getWholeCode().substring(9,11)%>-<%=cargoBean.getWholeCode().substring(11,12)%></b>
				<%} %>
				</td>
			</tr>
			<tr >
				<td align="center"></td>
			</tr>
		</table>
	</div>

	<%
		}
		}
	%>
    <script type="text/javascript">initPrint();</script>
	<script type="text/javascript">selectcity();</script>
 
</body>
</html>
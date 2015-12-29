<%@page import="adultadmin.util.db.DbOperation"%>
<%@page import="java.util.Map.Entry"%><%@ include file="../../../../taglibs.jsp"%><%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.util.*,adultadmin.util.db.*" %>
<%@ page import="java.sql.Connection,java.sql.ResultSet,java.sql.Statement" %>
<%@ page import="java.util.*" %>
<%@ page import="cache.*" %>
<%@ page import="adultadmin.action.vo.*,ormap.ProductLineMap" %>
<%@ page import="adultadmin.bean.*, adultadmin.bean.order.*, adultadmin.bean.stock.*,adultadmin.bean.buy.*"%>
<%@ page import="adultadmin.service.*, adultadmin.service.infc.*,cache.ProductLinePermissionCache" %>
<%
	response.setHeader("Cache-Control", "no-cache");
response.setHeader("Paragma", "no-cache");
response.setDateHeader("Expires", 0);

	voUser user = (voUser)session.getAttribute("userView");

	//数据库大查询锁，等待3秒
	/* if (!DbLock.slaveServerQueryLocked(100)) {
		response.sendRedirect(request.getContextPath()+"/tip.jsp?db=adult_slave");
		return;
	} */
    String productLine = StringUtil.convertNull(request.getParameter("productLine"));
	productLine = Encoder.decrypt(productLine);//解码为中文
	if(productLine==null){//解码失败,表示已经为中文,则返回默认
		productLine = StringUtil.dealParam(request.getParameter("productLine"));//名称
	}
	if (productLine==null) productLine="";
	boolean markProductList = StringUtil.toBoolean(request.getParameter("markProductList"));

    /* DbLock.slaveServerOperator = user.getUsername() + "_即时发货状态统计_" + DateUtil.getNow();*/
    UserGroupBean group = user.getGroup(); 

	boolean isSystem = (user.getSecurityLevel() == 10);	//系统管理员
	boolean isGaojiAdmin = (user.getSecurityLevel() == 9);	//高级管理员
	boolean isAdmin = (user.getSecurityLevel() == 5);	//普通管理员

	boolean isPingtaiyunwei = (user.getPermission() == 8);	//平台运维部
	boolean isXiaoshou = (user.getPermission() == 7);	//销售部
	boolean isShangpin = (user.getPermission() == 6);	//商品部
	boolean isTuiguang = (user.getPermission() == 5);	//推广部
	boolean isYunyingzhongxin = (user.getPermission() == 4);	//运营中心
	boolean isKefu = (user.getPermission() == 3);	//客服部

	boolean padmin = group.isFlag(74);
%>
<html>
<title>买卖宝后台</title>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="<%= request.getContextPath()%>/admin/ajax/js/jquery.js"></script>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/demo/demo.css">
<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/locale/easyui-lang-zh_CN.js"></script>
<style>
.popup1 {
	font-size: 12px;
	cursor: default;
	text-decoration: none;
	color: #0000FF;
	width: 100px;
	border: 1px solid #ffffff;
}

.popup1 img{
	border:0;
	filter:	Alpha(Opacity=70);
}

.popup1:hover {
	/*	border-top: 1px solid buttonhighlight;
	border-left: 1px solid buttonhighlight;
	border-bottom: 1px solid buttonshadow;
	border-right: 1px solid buttonshadow;*/
	border: 1px solid #0A246A;
	cursor: default;
	background-color: #FFEEC2;
	text-decoration: none;
	color: #000000;
	width: 100px;
}

.popup1_hover {
	/*	border-top: 1px solid buttonhighlight;
	border-left: 1px solid buttonhighlight;
	border-bottom: 1px solid buttonshadow;
	border-right: 1px solid buttonshadow;*/
	border: 1px solid #0A246A;
	cursor: default;
	background-color: #FFEEC2;
	text-decoration: none;
	color: #000000;
	width: 100px;
}

.popup1_hover img{
	border:0;
	filter:	Alpha(Opacity=100);
}

.msviLocalToolbar{
border:solid 1px #999;
background:#F1F1F1;

padding:2px 0px 1px 0px;

}
</style>
<style type="text/css">
	body {
		padding: 0px;
	}
	form {
		padding: 0px;
		margin-left: 0px;
		margin-right:0px;
	}
</style>
<body>

<script type="text/javascript">
var markOrderList = false;
function orderList(){
	$('#orderList').css("display","block");    // 修改div由不显示，改为显示。
	//$('.ui-dialog').css('position', "fixed");
	$('#orderList').dialog({  
	    title: '可发货订单类别统计',  // dialog的标题
	    width: 800,   	//宽度
	    height: 420,   //高度
	    closed: false,   // 关闭状态
	    cache: false,   //缓存,暂时不明白是要缓存什么东西但是与想象的有出入
	    modal: true,
	    buttons:[{
			text:'关闭',
			handler:function(){
				$("#orderList").dialog("close");
			}
		}]
	}); 
}
var markProductList = false;
function productList(){
	if(!markProductList){
		document.getElementById('info_table1').style.display = 'block';
		markProductList = true;
	}else if(markProductList){
		document.getElementById('info_table1').style.display = 'none';
		markProductList = false;
	}
}
function selectProductLine(){
	$("#info_table1").datagrid('load',{  
		    markProductList:true,
	   		productLine:$("#productLine").val()
		});
}

function reserveCheck(name){
  var names=document.getElementsByName(name);
  var len=names.length;
 if(len>0){
 	var i=0;
    for(i=0;i<len;i++){
     names[i].checked=true;
    }
 } 
}
<%if(padmin){	// ajax 快捷商品下架
%>
function hidePopup() {
	document.getElementById('popup').style.display = "none";
}
var fromA,fromAid;
function pstatus(from,uid,name){
//	if(!from||!confirm('确认要下架商品['+name+']?'))
//		return;
	fromA=from;
	fromAid=uid;
	
	var e = window.event;
	var popup = document.getElementById('popup');
	document.getElementById('popupTitle').innerText=name
	
    popup.style.left = e.clientX + document.body.scrollLeft;
    popup.style.top = e.clientY + document.body.scrollTop;
    popup.style.display = "";
	
	return false;
}
function pstatus2(st){
	fromA.style.color='orange'
	$.get('../ajax/stat/productStatus.jsp?id='+fromAid+'&status='+st, function(data) {
  		fromA.style.display='none'
	});
}
function showProductList() {
	$('#productListDetail').css("display","block");
	$('#info_table1').datagrid({   
	    title:'缺货产品列表', 
	    iconCls:'icon-ok',
	    fitColumns : true,
	    border : true,    
	    pageNumber:1,  
	    pageSize:20,    
	    pageList:[5,10,15,20], 
	    nowrap:false,  
	    striped: true,
	    collapsible:true,
	    loadMsg:'数据装载中......', 
	    url:'<%= request.getContextPath()%>/admin/stat/getOrderStockStatDetailInfo.mmx',
	    remoteSort:false, 
	    columns:[[
	     {title:'<a id="allCheckButton" onclick="javascript:reserveCheck(\'productId\')" >全选</a>',field:'check_info',width:80,rowspan:3,align:'center'},
	     {title:'产品线',field:'product_line',width:70,rowspan:3,align:'center'},
	     {title:'产品名称',field:'product_name',width:160,rowspan:3,align:'center'},
	     {title:'缺货处理意见',field:'lack_suggest',width:90,rowspan:3,align:'center'},
	     {title:'原名称',field:'product_oriname',width:170,rowspan:3,align:'center'},
	     {title:'产品编号',field:'product_code',width:75,rowspan:3,align:'center'},
	     {title:'广速出库总量',field:'GS_sum',width:30,rowspan:3,align:'center'},
	     {title:'广速缺货量',field:'GS_lack_sum',width:30,rowspan:3,align:'center'},
	     {title:'增城合格库可用量(冻结量)',field:'ZC_qualify_avail',width:35,rowspan:3,align:'center'},
	     {title:'增城待验库可用量(冻结量)',field:'ZC_check_avail',width:35,rowspan:3,align:'center'},
	     {title:'预计到货时间',field:'buy_stock_time',width:90,rowspan:3,align:'center'},
	     {title:'采购负责人',field:'buy_charge_user',width:80,rowspan:3,align:'center'},
    	 {title:'在途量',field:'on_the_way_sum',width:40,rowspan:3,align:'center'},
		 {title:'订单',field:'order',width:90,rowspan:3,align:'center'}
	    ]],
	    rownumbers:false, //是否有行号。
	    singleSelect:true,
	    onLoadSuccess:function(data) {
    		if( data['tip'] != null ) {
	    		jQuery.messager.alert("提示", data['tip']);
	    	}
	    }
	   });
	$("#allCheckButton").linkbutton();
}
<%}%>
</script>
<a href="#" onclick="javascript:orderList();" class="easyui-linkbutton" data-options="iconCls:'icon-tip'">可发货订单类别统计</a>&nbsp;&nbsp;
<%if(group.isFlag(258)){ %>
 
<a href="#" onclick="javascript:showProductList();" class="easyui-linkbutton" data-options="iconCls:'icon-tip'">缺货产品列表</a>

<%} %>
<table class="msviLocalToolbar" id="popup" width="100"
	style="display: none; position:absolute; z-index:100;
	background-color:white;">

<tr><tr><td id="popupTitle">商品名</td></tr>
<tr>
<td onclick="hidePopup();pstatus2(120);" class="popup1" onMouseOver="this.className='popup1_hover';"
            onMouseOut="this.className='popup1';">
隐藏该商品</td>
</tr>
<tr>
<td onclick="hidePopup();pstatus2(100);" class="popup1" onMouseOver="this.className='popup1_hover';"
            onMouseOut="this.className='popup1';">
下架该商品</td>
</tr>
<tr>
<td onclick="hidePopup();" class="popup1" onMouseOver="this.className='popup1_hover';"
            onMouseOut="this.className='popup1';">
取消</td>
</tr>
</table>
<osCache:cache scope="application" time="3600">
<%@include file="../../../../header.jsp"%>
<%
	int noStockoutCount = ((Integer)request.getAttribute("noStockoutCount")).intValue();
	int noOrderStockCount = ((Integer)request.getAttribute("noOrderStockCount")).intValue();
	int hasStockGSCount = ((Integer)request.getAttribute("hasStockGSCount")).intValue();
	int hasStockCount = ((Integer)request.getAttribute("hasStockCount")).intValue();
	int noStockCount = ((Integer)request.getAttribute("noStockCount")).intValue();
	int stockReadyCount = ((Integer)request.getAttribute("stockReadyCount")).intValue();
	int stockRecheckCount = ((Integer)request.getAttribute("stockRecheckCount")).intValue();
%>
<div class="easyui-panel" data-options="title:'发货状态统计',height:305,collapsible:true">
<table class="easyui-datagrid" data-options="fitColumns:true,border:true,striped:true,collapsible:true,rownumbers:true">
	<thead>
	<tr>
		<th data-options="field:'a',width:110,align:'center'">成交但没有发货的总订单</th>
		<th data-options="field:'b',width:110,align:'center'">没有“申请出库”的订单</th>
		<th data-options="field:'c',width:110,align:'center'">可直接发货的订单</th>
		<th data-options="field:'d',width:110,align:'center'">可调拨发货的订单</th>
		<th data-options="field:'e',width:110,align:'center'">全库缺货的订单</th>
		<th data-options="field:'f',width:110,align:'center'">能发货的待出库订单</th>
		<th data-options="field:'g',width:110,align:'center'">复核中的订单</th>
	</tr>
	</thead>
	<tr>
	    <td ><%= noStockoutCount %></td>
		<td ><%= noOrderStockCount %></td>
		<td ><%= hasStockGSCount %></td>
		<td ><%= hasStockCount %></td>
		<td ><%= noStockCount %></td>
		<td ><%= stockReadyCount %></td>
		<td ><%= stockRecheckCount %></td>
	</tr>
</table>
<pre>
概念说明：
1、成交但没有发货的总订单：成交订单中没有出库的订单
2、没有“申请出库”的订单：还没有操作“申请出货”的订单
3、可直接发货订单：广速库存满足订单中商品的需求量，可以直接发货的订单数<br/>（订单状态：待发货,已发货、订单未申请出货或订单出货状态为未处理、广速库存数量大于等于订单中货品数量）；<br/>订单中商品的需求量根据订单的生成时间累积计算
4、调拨可发货的订单：增城库存不满足订单中的商品需求量，但是通过库房各地域之间的调拨能发货的订单数<br/>（订单状态：待发货,已发货、订单未申请出货或订单出货状态为未处理、增城库存数量小于订单中货品数量、全库存数量大于等于订单中货品数量）；<br/>订单中商品的需求量根据订单的生成时间累积计算
5、全库缺货的订单：库存不足，需要通过采购入库才能发货的订单；订单中商品的需求量根据订单的生成时间累积计算
6、能发货的待出库订单：已冻结库存但没有出库的订单(不包含复核中的)
7、复核中的订单：导完订单，取完货正在复核中的订单
</pre>
</div>
<div id="orderList" style="display:none;">
<center>
 <table class="easyui-datagrid" data-options="title:'可发货订单类别统计',width:750,iconCls:'icon-ok',striped: true,collapsible:true,rownumbers:true">
	<thead>
	<tr >
        <th data-options="field:'a',width:110,align:'center'">订单商品类型</th>
        <th data-options="field:'b',width:110,align:'center'">可直接发货</th>
        <th data-options="field:'c',width:150,align:'center'">可调拨发货</th>
        <th data-options="field:'d',width:150,align:'center'">全库缺货</th>
        <th data-options="field:'e',width:150,align:'center'">总计</th>
	</tr>
	</thead>
	<%
		List<Map<String,String>> stockList = (List<Map<String,String>>)request.getAttribute("stockList");
		Map<String,String> totalMap = (Map<String,String>)request.getAttribute("totalMap");
		for( int i = 0; i < stockList.size(); i++ ) {
			Map<String,String> map = (Map<String,String>)stockList.get(i);
	%>
	
	<tr>
		<td ><%=map.get("orderName") %></td>
        <td ><%=map.get("hasStockGS") %></td>
        <td ><%= map.get("hasStock") %></td>
        <td ><%= map.get("noStock") %></td>
        <td ><%= map.get("total") %></td>
	</tr>
	<%
    	}
	%>
	<tr>
		<td >合计</td>
        <td ><%= totalMap.get("hasStockGSTotal")%></td>
        <td ><%= totalMap.get("hasStockTotal") %></td>
        <td ><%= totalMap.get("noStockTotal") %></td>
        <td ><%= totalMap.get("allTotal") %></td>
        
	</tr>
</table>
</center>
</div>
<div id="productListDetail" style="display:none;">
<%
if(group.isFlag(258)){ 
	List lineList=ProductLinePermissionCache.getAllProductLineList();
%>
<form name="formPrint" action="<%= request.getContextPath()%>/admin/rec/stat/orderStockStat/printOrderStockStatRealTime.jsp" method="post">
	<select name="productLine" id="productLine" onChange="selectProductLine();">
					<option value="">全部</option>
				<%for(Iterator i=lineList.iterator();i.hasNext();){
					voProductLine line = (voProductLine)i.next();
				%>	
					<option value="<%=Encoder.encrypt(line.getName())%>"><%=line.getName()%></option>
				<%}%>	
				</select>
	<script>selectOption(document.getElementById('productLine'), '<%=Encoder.encrypt(productLine)%>');</script>
	<a class="easyui-linkbutton" href="javascript:document.formPrint.submit();" data-options="iconCls:'icon-print'">导出勾选项</a>
	<table align="center" id="info_table1" >
	</table>
  </form>
<%} %>
  </div>
<%if(markProductList){ %>
<script type="text/javascript">productList();</script>
<% } %>
</osCache:cache>
</body>
</html>
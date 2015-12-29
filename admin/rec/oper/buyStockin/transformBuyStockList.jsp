<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.buy.*, adultadmin.action.vo.*,adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.voUser" %><%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.stock.*" %>
<%@ page import="ormap.ProductLineMap"%>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<%@ page import="adultadmin.bean.PagingBean, adultadmin.util.PageUtil" %>
<%

String supplierIds = cache.ProductLinePermissionCache.getProductLineSupplierIds(user);
supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;

PagingBean paging = (PagingBean) request.getAttribute("paging");
List buyStockList = (List) request.getAttribute("buyStockList");
List buyStockProductList = (List) request.getAttribute("buyStockProductList");
String proxyName = (String)request.getAttribute("proxyName");
BuyStockBean stock = (BuyStockBean)request.getAttribute("stock");
String stockCode = StringUtil.convertNull(request.getParameter("stockCode"));
String startDate = StringUtil.convertNull(request.getParameter("startDate"));
String endDate = StringUtil.convertNull(request.getParameter("endDate"));
String productCode = StringUtil.convertNull(request.getParameter("productCode"));
String oriName = StringUtil.convertNull(request.getParameter("oriName"));
String createUser = StringUtil.convertNull(request.getParameter("createUser"));
String affirmUser = StringUtil.convertNull(request.getParameter("affirmUser"));
int productLine = StringUtil.StringToId(request.getParameter("productLine"));
int supplierId = StringUtil.StringToId(request.getParameter("supplierId"));
List supplierList = (List) request.getAttribute("supplierList");//供货商信息
List productLineList = (List) request.getAttribute("productLineList");//产品线信息
oriName = Encoder.decrypt(oriName);
if(oriName == null) {
	oriName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("oriName")));
}
int i, count;
voProduct product = null;
BuyStockProductBean bpp = null;

//审核权限
boolean shenhe = group.isFlag(57);
//查看全部的计划
boolean viewAll = group.isFlag(54);
boolean assign = group.isFlag(59);

boolean check = false;
if(request.getParameter("check") != null){
	check = true;
}

String result = (String) request.getAttribute("result");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
history.back(-1);
</script>
<%
	return;
} else { 
%>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath() %>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/supplierNames.js"></script>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
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
<script language="JavaScript">
jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
function check(){
	var names = document.getElementsByName("buyStockProductId");
	var i;
	var check = false;
	for(i=0; i<names.length; i++){
		if(names[i].checked){
			check = true;
		}
		
	}
	if(!check){
		$.messager.alert('提示',"未选择任何产品");
		return;
	}
	document.form3.submit();
	return;
}
document.onkeydown = function(event_e){  
    if( window.event )  
        event_e = window.event;  
        var int_keycode = event_e.charCode||event_e.keyCode;  
        if(int_keycode ==13){ 
        	searchLoadPage();
   		}
    }
function submitSearch() {
	var startDate = document.searchForm.startDate.value;
	var endDate = document.searchForm.endDate.value;
	if((startDate == '' && endDate != '')||(startDate != '' && endDate == '')) {
		$.messager.alert('提示','添加日期必须填写完整');
		return;
	}

	var re = /^((((((0[48])|([13579][26])|([2468][048]))00)|([0-9][0-9]((0[48])|([13579][26])|([2468][048]))))-02-29)|(((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30))|(((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-8]))))))$/i;	
	if(startDate != '' && endDate != '') {
	    if (!re.test(startDate) || !re.test(endDate)) {
	    	$.messager.alert('提示','日期格式不合法');
	         return;
	    }
	    if(startDate > endDate) {
	    	$.messager.alert('提示','开始时间不能大于结束时间');
			return;
		}
	}
	if($("#word").val().length==0){document.getElementById('supplierId').value=0}
	return;
}
function select(){
	document.getElementById('supplierId').value = document.getElementById('supplierId').value;
}

function searchLoadPage() {
	var startDate = document.searchForm.startDate.value;
	var endDate = document.searchForm.endDate.value;
	if((startDate == '' && endDate != '')||(startDate != '' && endDate == '')) {
		$.messager.alert('提示','添加日期必须填写完整');
		return;
	}

	var re = /^((((((0[48])|([13579][26])|([2468][048]))00)|([0-9][0-9]((0[48])|([13579][26])|([2468][048]))))-02-29)|(((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30))|(((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-8]))))))$/i;	
	if(startDate != '' && endDate != '') {
	    if (!re.test(startDate) || !re.test(endDate)) {
	         $.messager.alert('提示','日期格式不合法');
	         return;
	    }
	    if(startDate > endDate) {
			$.messager.alert('提示','开始时间不能大于结束时间');
			return;
		}
	}
	if($("#word").val().length==0){document.getElementById('supplierId').value=0}
	var vstockCode = $("#stockCode").val().trim();
	$("#hstockCode").val(vstockCode);
	var vstartDate = $("#startDate").val();
	$("#hstartDate").val(vstartDate);
	var vendDate = $("#endDate").val();
	$("#hendDate").val(vendDate);
	var vproductCode = $("#productCode").val().trim();
	$("#hproductCode").val(vproductCode);
	var voriName = $("#oriName").val().trim();
	$("#horiName").val(voriName);
	var vcreateUser = $("#createUser").val().trim();
	$("#hcreateUser").val(vcreateUser);
	var vaffirmUser = $("#affirmUser").val().trim();
	$("#haffirmUser").val(vaffirmUser);
	var vproductLine = $("#productLine").val().trim();
	$("#hproductLine").val(vproductLine);
	var vword = $("#word").val();
	$("#hword").val(vword);
	var vcondition = $("#condition").val();
	$("#hcondition").val(vcondition);
	var vsupplierId = $("#supplierId").val();
	$("#hsupplierId").val(vsupplierId);
		$("#info_table").datagrid('load',{  
			stockCode:$("#hstockCode").val(),
			startDate:$("#hstartDate").val(),
			endDate:$("#hendDate").val(),
			productCode:$("#hproductCode").val(),
			oriName:$("#horiName").val(),
			createUser:$("#hcreateUser").val(),
			affirmUser:$("#haffirmUser").val(),
			productLine:$("#hproductLine").val(),
			word:$("#hword").val(),
			condition:$("#hcondition").val(),
			supplierId:$("#hsupplierId").val()
		});
		return;
	}
String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
function getInfo(stockId) {
	//alert("test");
	jQuery.ajax({
		  type: 'POST',
		  url: '../admin/getBuyStockForTransform.mmx?stockId='+stockId,
		  success: function (data, textStatus) {
				var json = eval('(' + data + ')');
				if( json['status'] == "success" ) {
					$("#dStockCode").html(json['dStockCode']);
					$("#dProxyName").html(json['dProxyName']);
					$("#dWareArea").html(json['dWareArea']);
					$("#hiddenInfo").html(json['hiddenInfo']);
					$('#info_table2').datagrid('loadData', json['products']);
				}
		  },
		  error: function() {
			alert("没找到");  
		  },
		  dataType:'text'
		});
}

</script>
<div id="tb">
	<fieldset>
	<legend>筛选预计到货列表</legend>
	<form action="transformBuyStockList.jsp" method="post" name="searchForm" onsubmit="return submitSearch()" >
			采购预计到货单编号：<input type="text" name="stockCode" id="stockCode" size="15" value="<%=stockCode %>" />&nbsp;&nbsp;
			<input type="hidden" id="hstockCode" value="<%=stockCode %>" />
			预计到货日期：<input type="text" name="startDate" id="startDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=startDate %>" />
			<input type="hidden" id="hstartDate" value="<%=startDate %>" />
			至<input type="text" name="endDate" id="endDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=endDate %>" />&nbsp;&nbsp;
			<input type="hidden" id="hendDate" value="<%=endDate %>" />
			产品编号：<input type="text" name="productCode" id="productCode" size="14" value="<%=productCode %>" />&nbsp;&nbsp;
			<input type="hidden" id="hproductCode"  value="<%=productCode %>" />
			产品原名称：<input type="text" name="oriName" id="oriName" size="14" value="<%=oriName %>" />模糊<br/>
			<input type="hidden" id="horiName"  value="<%=oriName %>" />
			生成人：<input type="text" name="createUser" id="createUser" size="10" value="<%=createUser %>" />模糊&nbsp;&nbsp;
			<input type="hidden" id="hcreateUser" value="<%=createUser %>" />
			确认人：<input type="text" name="affirmUser" id="affirmUser" size="10" value="<%=affirmUser %>" />&nbsp;&nbsp;
			<input type="hidden" id="haffirmUser" value="<%=affirmUser %>" />
			产品线：<select name="productLine" id="productLine">
					<option value="0">全部</option>
				<%
				for (int p = 0; productLineList != null && p < productLineList.size(); p++) {
					voProductLine proLineBean = (voProductLine) productLineList.get(p);
				%>
					<option value="<%= proLineBean.getId() %>" <%if(productLine == proLineBean.getId()) {%> selected <%} %>><%= proLineBean.getName() %></option>
				<%
				}
				%>
			</select>&nbsp;
			<input type="hidden" id="hproductLine" value="0"/>
			供应商：<div id="auto" style="position: absolute; left: 100px; top: 110px;"></div>
				<input type="text" name="suppliertext" id="word" style="width: 100px; font-size: 10pt;height:20px;" />
				<input type="hidden" id="hword" value=""/>
				
				<input type="hidden" name="condition" id="condition" value="status = 1 and id in (<%=supplierIds %>)">
				<input type="hidden" id="hcondition" value="status = 1 and id in (<%=supplierIds %>)">
				<span style="width:18px;border:0px solid red;margin-left:-8px;margin-bottom:-6px;">
				<select name="supplierId" id="supplierId" onChange="javascript:select();document.all.suppliertext.value=document.getElementById('supplierId').options[document.getElementById('supplierId').selectedIndex].text" style="margin-left:-100px;width:118px;">
					<option value="0"></option>
					<%
						for(int k = 0; supplierList != null && k < supplierList.size(); k++) {
							voSelect ssInfoBean = (voSelect) supplierList.get(k);
					%>
					 		<option value="<%= ssInfoBean.getId() %>" <%if(supplierId == ssInfoBean.getId()) {%> selected <%} %>><%= ssInfoBean.getName() %></option>
					<%
						} 
					%>
				</select>
				<input type="hidden" id="hsupplierId" value="0"/>
			</span>&nbsp;
			<script>document.searchForm.suppliertext.value=document.getElementById('supplierId').options[document.getElementById('supplierId').selectedIndex].text;</script>
			<script>selectOption(document.getElementById('supplierId'), '<%=supplierId%>');</script>
			<a href="javascript:searchLoadPage();" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a>
			<a href="<%= request.getContextPath()%>/admin/toBuyStockinList.mmx" class="easyui-linkbutton" data-options="iconCls:'icon-back'">返回采购入库列表</a>
	</form>
	</fieldset>
</div>
<script type="text/javascript" >
		$(function(){
		   $('#info_table').datagrid({   
		   	fit:true,
		    fitColumns : true,
		    border : true,   
		    pageNumber:1,  
		    pageSize:20,    
		    pageList:[5,10,15,20,50], 
		    nowrap:false,  
		    striped: true,
		    collapsible:true,
		    singleSelect:true,
		    url:'../admin/transformBuyStockList.mmx', 
		    loadMsg:'数据装载中......', 
		    remoteSort:false, 
		    columns:[[
		     {title:'序号',field:'count',width:40,rowspan:3,align:'center'},
		     {title:'编号',field:'buy_stock_code',width:180,rowspan:3,align:'center'},
		     {title:'添加时间',field:'create_datetime',width:165,rowspan:3,align:'center'},
		     {title:'产品线',field:'product_line',width:200,rowspan:3,align:'center'},
		     {title:'代理商',field:'supplier_name',width:140,rowspan:3,align:'center'},
		     {title:'地区',field:'ware_area',width:95,rowspan:3,align:'center'},
		     {title:'状态',field:'status',width:130,rowspan:3,align:'center'},
		     {title:'生成人/确认人',field:'create_username',width:160,rowspan:3,align:'center'}
		    ]],
		    toolbar:'#tb',
		    pagination:true,
		    rownumbers:false, //是否有行号。
		    onLoadSuccess: function() {
		    	var vstockCode = $("#hstockCode").val();
		    	$("#stockCode").val(vstockCode);
		    	var vstartDate = $("#hstartDate").val();
		    	$("#startDate").val(vstartDate);
		    	var vendDate = $("#hendDate").val();
		    	$("#endDate").val(vendDate);
		    	var vproductCode = $("#hproductCode").val();
		    	$("#productCode").val(vproductCode);
		    	var voriName = $("#horiName").val();
		    	$("#oriName").val(voriName);
		    	var vcreateUser = $("#hcreateUser").val();
		    	$("#createUser").val(vcreateUser);
		    	var vaffirmUser = $("#haffirmUser").val();
		    	$("#affirmUser").val(vaffirmUser);
		    	var vproductLine = $("#hproductLine").val();
		    	$("#productLine").val(vproductLine);
		    	var vword = $("#hword").val();
		    	$("#word").val(vword);
		    	var vcondition = $("#hcondition").val();
		    	$("#condition").val(vcondition);
		    	var vsupplierId = $("#hsupplierId").val();
		    	$("#supplierId").val(vsupplierId);
		    	$('#info_table').datagrid('getPager').pagination({
				    displayMsg:'当前显示从{from}到{to}共{total}记录',
				    onBeforeRefresh:function(pageNumber, pageSize){
				     $(this).pagination('loading');
				     $(this).pagination('loaded');
				    }
		    	 });
		    }
		   });
		  });
	</script>
	<table align="center" id="info_table" >
	</table>
<%} %>
<script type="text/javascript">
function testDialog() {
	$('#dd').css("display","block");    // 修改div由不显示，改为显示。
	//$('.ui-dialog').css('position', "fixed");
	$('#dd').dialog({  
	    title: '转换新的采购入库单',  // dialog的标题
	    width: 800,   	//宽度
	    height: 420,   //高度
	    closed: false,   // 关闭状态
	    cache: false,   //缓存,暂时不明白是要缓存什么东西但是与想象的有出入
	    //href: 'javascript:getInfo();',   //可以在dialog 中打开页面，哲理就需要填写对应的页面的地址。
	    modal: true,
	    buttons:[{
			text:'关闭',
			handler:function(){
				$("#dd").dialog("close");
			}
		}]
	});  
	//$('#dd').dialog('refresh', '<%= request.getContextPath()%>/admin/rec/oper/transformNewBuyStockin.jsp');
}
</script>
<div id="dd" style="width:400px;height:300px;display:none;">
<form name="form3" method="post" action="stock2/addBuyStockin.jsp" onsubmit="return check();">
	编号：<span id="dStockCode"></span><br/>
	代理商：<span id="dProxyName"></span>&nbsp;&nbsp;&nbsp;地区：<span id="dWareArea"></span>
	<div id="tableString" > 
	</div>
<script type="text/javascript" >
		$(function(){
		   $('#info_table2').datagrid({   
		    title:'预计单中商品列表', 
		    iconCls:'icon-ok',
		    width:750,    
		    //height:400,   
		    pageNumber:1,  
		    pageSize:10,    
		    pageList:[5,10,15,20,50], 
		    nowrap:false,  
		    striped: true,
		    collapsible:true,
		    //url:'../admin/transformBuyStockList.mmx', 
		    loadMsg:'数据装载中......', 
		    remoteSort:false, 
		    columns:[[
		     {title:'选择',field:'check_option',width:40,rowspan:3,align:'center'},
		     {title:'序号',field:'count',width:110,rowspan:3,align:'center'},
		     {title:'产品线',field:'product_line',width:105,rowspan:3,align:'center'},
		     {title:'产品编号',field:'product_code',width:150,rowspan:3,align:'center'},
		     {title:'产品名称',field:'product_name',width:70,rowspan:3,align:'center'},
		     {title:'原名称',field:'ori_name',width:55,rowspan:3,align:'center'},
		     {title:'预计进货量(已入库量)',field:'buy_stock_sum',width:70,rowspan:3,align:'center'},
		     {title:'进货前库存',field:'stockin_stock',width:130,rowspan:3,align:'center'}
		    ]],
		    pagination:false,
		    rownumbers:false, //是否有行号。
		    onLoadSuccess: function() {
		    	$('#info_table2').datagrid('getPager').pagination({
				    displayMsg:'当前显示从{from}到{to}共{total}记录',
				    onBeforeRefresh:function(pageNumber, pageSize){
				     $(this).pagination('loading');
				     $(this).pagination('loaded');
				    }
		    
		    	 });
		    }
		   });
		   
		  });
	</script>
	<table align="center" id="info_table2" >
	</table>
	<span id="hiddenInfo"></span>
<script type="text/javascript">
function reserveCheck(name){
  var names=document.getElementsByName(name);
  var len=names.length;
 if(len>0){
 	var i=0;
    for(i=0;i<len;i++){
     if(names[i].checked)
     names[i].checked=false;
     else
     names[i].checked=true;
    }
 } 
}
</script>
<a href="javascript:reserveCheck('buyStockProductId');" class="easyui-linkbutton" >反选</a>&nbsp;&nbsp;
<a href="javascript:check();" class="easyui-linkbutton" data-options="iconCls:'icon-ok'" >对选中的产品生成采购入库单</a>
</form>
<a ></a>
</div>
<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.List, adultadmin.bean.buy.*, adultadmin.bean.stock.*, adultadmin.bean.PagingBean, adultadmin.util.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%!
static java.text.DecimalFormat df = new java.text.DecimalFormat("0.##");
%>
<%
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
%>
<%
BuyStockinAction action = new BuyStockinAction();
String search = StringUtil.convertNull(request.getParameter("search"));
action.buyStockinList(request, response);

String supplierIds = cache.ProductLinePermissionCache.getProductLineSupplierIds(user);
supplierIds = StringUtil.isNull(supplierIds) ? "-1" : supplierIds;

List list = (List) request.getAttribute("list");
PagingBean paging = (PagingBean) request.getAttribute("paging");
String stockinCode = StringUtil.convertNull(request.getParameter("stockinCode"));
String buyStockCode = StringUtil.convertNull(request.getParameter("buyStockCode"));
String productCode = StringUtil.convertNull(request.getParameter("productCode"));
String productName = StringUtil.convertNull(request.getParameter("productName"));
productName = Encoder.decrypt(productName);
if(productName == null) {
	productName = StringUtil.convertNull(StringUtil.dealParam(request.getParameter("productName")));
}
String sCreateDate = StringUtil.convertNull(request.getParameter("sCreateDate"));
String eCreateDate = StringUtil.convertNull(request.getParameter("eCreateDate"));
String createUser = StringUtil.convertNull(request.getParameter("createUser"));
String auditUser = StringUtil.convertNull(request.getParameter("auditUser"));
String sBuyDate = StringUtil.convertNull(request.getParameter("sBuyDate"));
String eBuyDate = StringUtil.convertNull(request.getParameter("eBuyDate"));
String[] statuss = request.getParameterValues("status");
int productLine = StringUtil.StringToId(request.getParameter("productLine"));
int supplierId = StringUtil.StringToId(request.getParameter("supplierId"));// 供应商id
List supplierList = (List) request.getAttribute("supplierList");//供货商信息
List productLineList = (List) request.getAttribute("productLineList");//产品线信息
String status = "";
if(statuss!=null){
	for(int i=0;i<statuss.length;i++){
		status = status + statuss[i]+",";
	}
}
int i, count;
BuyStockinBean bean = null;

//查看全部计划权限
boolean viewAll = group.isFlag(56);
boolean transform = group.isFlag(114);
boolean bianji = group.isFlag(169);
String result = (String) request.getAttribute("result");
if("failure".equals(result)){
	String tip = (String) request.getAttribute("tip");
%>
<script>
alert("<%=tip%>");
document.location = "buyOrderList.jsp";
</script>
<%
	return;
} else { 
%>
<script type="text/javascript" src="<%=request.getContextPath()%>/admin/js/JS_functions.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/WebCalendar.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery.js"></script>
<script language="JavaScript" src="<%=request.getContextPath()%>/admin/js/supplierNames.js"></script>
<link rel="stylesheet" type="text/css" href="../js/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="../js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="../js/easyui/demo/demo.css">
    <script type="text/javascript" src="../js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="../js/easyui/jquery.easyui.min.js"></script>
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
<script type="text/javascript">
jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
document.onkeydown = function(event_e){  
    if( window.event )  
        event_e = window.event;  
        var int_keycode = event_e.charCode||event_e.keyCode;  
        if(int_keycode ==13){ 
        	submitSearch();
   		}
    }
function checkAll(name,id) {     
    var el = document.getElementsByTagName('input');
    var len = el.length;
    for(var i=0; i<len; i++){
        if((el[i].type=="checkbox") && (el[i].name==name) && (el[i].id==id)){
	    el[i].checked = true;         
	}     
    } 
}

function clearAll(name) {
    var el = document.getElementsByTagName('input');
    var len = el.length;
    for(var i=0; i<len; i++){
        if((el[i].type=="checkbox") && (el[i].name==name)){
	    el[i].checked = false;
	}
    }
}
function checkboxChecked(checkbox,value){
	values = value.split(",");
	for(var j = 0; j < values.length; j++){
		for(var i = 0; i < checkbox.length; i++){
			if(checkbox[i].value == values[j]){
				checkbox[i].checked = true;
			}
		}
	}
}
function submitSearch() {
	var sCreateDate = document.searchForm.sCreateDate.value;
	var eCreateDate = document.searchForm.eCreateDate.value;
	var sBuyDate = document.searchForm.sBuyDate.value;
	var eBuyDate = document.searchForm.eBuyDate.value;
	if((sCreateDate == '' && eCreateDate != '')||(sCreateDate != '' && eCreateDate == '')) {
		$.messager.alert('提示','添加日期必须填写完整');
		return;
	}
	if((sBuyDate == '' && eBuyDate != '')||(sBuyDate != '' && eBuyDate == '')) {
		$.messager.alert('提示','采购完成日期必须填写完整');
		return;
	}
	var re = /^((((((0[48])|([13579][26])|([2468][048]))00)|([0-9][0-9]((0[48])|([13579][26])|([2468][048]))))-02-29)|(((000[1-9])|(00[1-9][0-9])|(0[1-9][0-9][0-9])|([1-9][0-9][0-9][0-9]))-((((0[13578])|(1[02]))-31)|(((0[1,3-9])|(1[0-2]))-(29|30))|(((0[1-9])|(1[0-2]))-((0[1-9])|(1[0-9])|(2[0-8]))))))$/i;	
	if(sCreateDate != '' && eCreateDate != '') {
	    if (!re.test(sCreateDate) || !re.test(eCreateDate)) {
	    	$.messager.alert('提示','日期格式不合法');
	         return;
	    }
	    if(sCreateDate > eCreateDate) {
	    	$.messager.alert('提示','开始时间不能大于结束时间');
			return;
		}
	}
	if(sBuyDate != '' && eBuyDate != '') {
		if(!re.test(sBuyDate) || !re.test(eBuyDate)) {
			$.messager.alert('提示','日期格式不合法');
         	return;
		}
		if(sBuyDate > eBuyDate) {
			$.messager.alert('提示','开始日期不能大于结束日期');
			return;
		}
	}
	if($("#word").val().length==0){document.getElementById('supplierId').value=0}
	searchLoadPage();
	return;
}
function select(){
	document.getElementById('supplierId').value = document.getElementById('supplierId').value;
}

function searchLoadPage() {
	
	var vstockinCode = $("#stockinCode").val().trim();
	$("#hstockinCode").val(vstockinCode);
	var vsCreateDate = $("#sCreateDate").val().trim();
	$("#hsCreateDate").val(vsCreateDate);
	var veCreateDate = $("#eCreateDate").val().trim();
	$("#heCreateDate").val(veCreateDate);
	var vcreateUser = $("#createUser").val().trim();
	$("#hcreateUser").val(vcreateUser);
	var vauditUser = $("#auditUser").val().trim();
	$("#hauditUser").val(vauditUser);
	var vsBuyDate = $("#sBuyDate").val();
	$("#hsBuyDate").val(vsBuyDate);
	var veBuyDate = $("#eBuyDate").val();
	$("#heBuyDate").val(veBuyDate);
	var vstatus0 = $("#status0").val();
	if( $("#status0").attr("checked") == "checked") {
		$("#hstatus0").val(vstatus0);
	} else {
		$("#hstatus0").val("-1");
	}
	var vstatus3 = $("#status3").val();
	if( $("#status3").attr("checked") == "checked") {
		$("#hstatus3").val(vstatus3);
	} else {
		$("#hstatus3").val("-1");
	}
	var vstatus5 = $("#status5").val();
	if( $("#status5").attr("checked") == "checked") {
		$("#hstatus5").val(vstatus5);
	} else {
		$("#hstatus5").val("-1");
	}
	var vstatus6 = $("#status6").val();
	if( $("#status6").attr("checked") == "checked") {
		$("#hstatus6").val(vstatus6);
	} else {
		$("#hstatus6").val("-1");
	}
	var vstatus4 = $("#status4").val();
	if( $("#status4").attr("checked") == "checked" ) {
		$("#hstatus4").val(vstatus4);
	} else {
		$("#hstatus4").val("-1");
	}
	var vbuyStockCode = $("#buyStockCode").val().trim();
	$("#hbuyStockCode").val(vbuyStockCode);
	var vproductCode = $("#productCode").val().trim();
	$("#hproductCode").val(vproductCode);
	var vproductName = $("#productName").val().trim();
	$("#hproductName").val(vproductName);
	var vproductLine = $("#productLine").val();
	$("#hproductLine").val(vproductLine);
	var vword = $("#word").val();
	$("#hword").val(vword);
	var vcondition = $("#condition").val();
	$("#hcondition").val(vcondition);
	var vsupplierId = $("#supplierId").val();
	$("#hsupplierId").val(vsupplierId);
		if( $("#status0").attr("checked") == "checked" || $("#status3").attr("checked") == "checked" ||$("#status5").attr("checked") == "checked" ||$("#status6").attr("checked") == "checked" || $("#status4").attr("checked") == "checked" ) {
			$("#info_table").datagrid('load',{  
				stockinCode:$("#hstockinCode").val(),
				sCreateDate:$("#hsCreateDate").val(),
				eCreateDate:$("#heCreateDate").val(),
				createUser:$("#hcreateUser").val(),
				auditUser:$("#hauditUser").val(),
				sBuyDate:$("#hsBuyDate").val(),
				eBuyDate:$("#heBuyDate").val(),
				status:$("#hstatus0").val()+','+$("#hstatus3").val()+','+$("#hstatus5").val()+','+$("#hstatus6").val()+','+$("#hstatus4").val(),
				//status:$("#hstatus3").val(),
				//status:$("#hstatus5").val(),
				//status:$("#hstatus6").val(),
				//status:$("#hstatus4").val(),
				buyStockCode:$("#hbuyStockCode").val(),
				productCode:$("#hproductCode").val(),
				productName:$("#hproductName").val(),
				productLine:$("#hproductLine").val(),
				word:$("#hword").val(),
				condition:$("#hcondition").val(),
				supplierId:$("#hsupplierId").val()
			});	
		} else {
			$("#info_table").datagrid('load',{  
				stockinCode:$("#hstockinCode").val(),
				sCreateDate:$("#hsCreateDate").val(),
				eCreateDate:$("#heCreateDate").val(),
				createUser:$("#hcreateUser").val(),
				auditUser:$("#hauditUser").val(),
				sBuyDate:$("#hsBuyDate").val(),
				eBuyDate:$("#heBuyDate").val(),
				buyStockCode:$("#hbuyStockCode").val(),
				productCode:$("#hproductCode").val(),
				productName:$("#hproductName").val(),
				productLine:$("#hproductLine").val(),
				word:$("#hword").val(),
				condition:$("#hcondition").val(),
				supplierId:$("#hsupplierId").val()
			});
		}
	}

function checkCheckBox() {
	alert("start");
	var status0 = $("#status0").val();
	alert(status0);
	var status4 = $("#status4").val();
	alert(status4);
	var status3 = $("#status3").val();
	alert(status3);
	var status5 = $("#status5").val();
	alert(status5);
	var status6 = $("#status6").val();
	alert(status6);
	alert("end");
}
String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
</script>
<div id="tb">
<fieldset>
<legend>采购入库单查询</legend>
<form action="buyStockinList.jsp" method="post" name="searchForm" onsubmit="return submitSearch()"> 
	采购&nbsp;&nbsp;入库单&nbsp;&nbsp;编号：<input type="text" name="stockinCode" id="stockinCode" size="15" value="<%=stockinCode %>" />&nbsp;&nbsp;
	<input type="hidden" id="hstockinCode" size="15" value="<%=stockinCode %>" />&nbsp;&nbsp;
	添加日期：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp&nbsp;&nbsp;<input type="text" name="sCreateDate" id="sCreateDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=sCreateDate %>" />
	<input type="hidden" id="hsCreateDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=sCreateDate %>" />
	至<input type="text" name="eCreateDate" id="eCreateDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=eCreateDate %>" />
	<input type="hidden" id="heCreateDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=eCreateDate %>" />
	&nbsp;&nbsp;&nbsp;
	生成人：<input type="text" name="createUser" id="createUser" size="12" value="<%=createUser %>" />&nbsp;&nbsp;
	<input type="hidden" id="hcreateUser" value="<%=createUser %>" />&nbsp;&nbsp;<br/>
	采购预计到货单编号：<input type="text" size="12" name="buyStockCode" id="buyStockCode" value="<%=buyStockCode %>"/>&nbsp;&nbsp;
	<input type="hidden" id="hbuyStockCode" value="<%=buyStockCode %>"/>&nbsp;&nbsp;
	采购完成时间：<input type="text" name="sBuyDate" id="sBuyDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=sBuyDate %>" />
	<input type="hidden" id="hsBuyDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=sBuyDate %>" />
	至<input type="text" name="eBuyDate" id="eBuyDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=eBuyDate %>" />
	<input type="hidden" id="heBuyDate" size="10" onClick="SelectDate(this,'yyyy-MM-dd');" value="<%=eBuyDate %>" />
	&nbsp;&nbsp;&nbsp;
	审核人：<input type="text" name="auditUser" id="auditUser" size="12" value="<%=auditUser %>" />&nbsp;&nbsp;
	<input type="hidden" id="hauditUser" size="10" value="<%=auditUser %>" />&nbsp;&nbsp;
	<br/>
	采购入库单状态：
	<input type="checkbox" name="status" id="status0" value="0">未处理&nbsp;
	<input type="checkbox" name="status" id="status3" value="3">入库处理中&nbsp;
	<input type="checkbox" name="status" id="status6" value="6">已审核&nbsp;
	<input type="checkbox" name="status" id="status5" value="5">审核未通过&nbsp;
	<input type="checkbox" name="status" id="status4" value="4">采购已完成<br/>
	<input type="hidden" id="hstatus0" value="-1"/>
	<input type="hidden" id="hstatus3" value="-1"/>
	<input type="hidden" id="hstatus6" value="-1"/>
	<input type="hidden" id="hstatus5" value="-1"/>
	<input type="hidden" id="hstatus4" value="-1"/>
	
	
	产品编号：<input type="text" name="productCode" id="productCode" value="<%=productCode %>"/>&nbsp;&nbsp;
	<input type="hidden" id="hproductCode" value="<%=productCode %>"/>&nbsp;&nbsp;
	产品原名称：<input type="text" name="productName" id="productName" value="<%=productName %>"/>模糊&nbsp;&nbsp;
	<input type="hidden" id="hproductName" value="<%=productName %>"/>
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
	供应商：<div id="auto" style="position: absolute; left: 100px; top: 150px;"></div>
		<input type="text" name="suppliertext" id="word" style="width: 100px; font-size: 10pt;height:20px;" />
		<input type="hidden" id="hword" value="" />
		<input type="hidden" name="condition" id="condition" value="status = 1 and id in (<%=supplierIds %>)">
		<input type="hidden" id="hcondition" value="status = 1 and id in (<%= supplierIds %>)" />
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
		<input type="hidden" id="hsupplierId" value="0" />
	</span>&nbsp;
	<script>document.searchForm.suppliertext.value=document.getElementById('supplierId').options[document.getElementById('supplierId').selectedIndex].text;</script>
	<script>selectOption(document.getElementById('supplierId'), '<%=supplierId%>');</script>
	<script>checkboxChecked(document.getElementsByName('status'),'<%=status%>');</script>
	<input type="hidden" name="search" value="search" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	<a href="javascript:submitSearch();" class="easyui-linkbutton" data-options="iconCls:'icon-search'">查询</a>
</form>
</fieldset>
<%if(transform){ %>
<a href= "javascript:void(0);" class="easyui-linkbutton" iconCls="icon-add" plain="true" onclick="javascript:document.formJump.submit();">转换新的采购入库单</a>
<%} %>
<a href= "javascript:void(0);" class= "easyui-linkbutton" iconCls="icon-edit" plain="true" onclick="bsEdit();"> 编辑</a> 
<a href= "javascript:void(0);" class= "easyui-linkbutton" iconCls="icon-print" plain="true" onclick="bsPrint();">打印</a> 
<a href= "javascript:void(0);" class= "easyui-linkbutton" iconCls="icon-search" plain="true" onclick="bsPrice();"> 查看入库价格</a> 
<a href= "javascript:void(0);" class= "easyui-linkbutton" iconCls="icon-remove" plain="true" onclick="bsDell();"> 删除</a> 
</div>
<div id= "menu" class= "easyui-menu" style="width:120px;display: none;">
			<%if(transform){ %>
             <div onclick= "javascript:document.formJump.submit();" iconCls= "icon-add">转换新入库单</div>
             <%
             	}
             %>
             <div onclick= "bsEdit();" iconCls= "icon-edit">编辑 </div>
             <div onclick= "bsPrint();" iconCls= "icon-print">打印</div>
             <div onclick= "bsPrice();" iconCls= "icon-search">查看入库价格 </div>
             <div onclick= "bsDell();" iconCls= "icon-remove">删除 </div>
                </div>
<form method="post" name="formJump" action="<%= request.getContextPath() %>/admin/toTransformBuyStockList.mmx">
</form>
<script type="text/javascript" >
			
		$(function(){
			$( '#info_table').datagrid({
                   onRowContextMenu : function(e, rowIndex, rowData) {
                              e.preventDefault();
                              $( this).datagrid('unselectAll' );
                              $( this).datagrid('selectRow' , rowIndex);
                              $( '#menu').menu('show' , {
                                    left : e.pageX,
                                    top : e.pageY
                              });
                        }
                  });
			
		   $('#info_table').datagrid({   
		   	fit:true,
		    fitColumns : true,
			border : true,   
		    pageNumber:1,  
		    pageSize:15,    
		    pageList:[5,10,15,20,50], 
		    nowrap:false,  
		    striped: true,
		    collapsible:true,
		    url:'../admin/buyStockinList.mmx?search=search', 
		    loadMsg:'数据装载中......', 
		    remoteSort:false, 
		    singleSelect:true,
		    columns:[[
		     {title:'序号',field:'count',width:40,rowspan:3,align:'center'},
		     {title:'编号',field:'buy_stockin_code',width:110,rowspan:3,align:'center'},
		     {title:'添加时间',field:'create_datetime',width:105,rowspan:3,align:'center'},
		     {title:'产品线',field:'product_line',width:150,rowspan:3,align:'center'},
		     {title:'代理商',field:'supplier_name',width:70,rowspan:3,align:'center'},
		     {title:'预计到货单编号',field:'buy_stock_code',width:110,rowspan:3,align:'center'},
		     {title:'地区',field:'ware_area',width:55,rowspan:3,align:'center'},
		     {title:'状态',field:'status',width:70,rowspan:3,align:'center'},
		     {title:'生成人/审核人',field:'create_username',width:130,rowspan:3,align:'center'},
		     {title:'打印次数',field:'print_count',width:195,rowspan:3,align:'center'}
		    ]],
		    toolbar:'#tb',
		    pagination:true,
		    rownumbers:false, //是否有行号。
		    onLoadSuccess: function() {
		    	var vstockinCode = $("#hstockinCode").val();
		    	$("#stockinCode").val(vstockinCode);
		    	var vsCreateDate = $("#hsCreateDate").val();
		    	$("#sCreateDate").val(vsCreateDate);
		    	var veCreateDate = $("#heCreateDate").val();
		    	$("#eCreateDate").val(veCreateDate);
		    	var vcreateUser = $("#hcreateUser").val();
		    	$("#createUser").val(vcreateUser);
		    	var vauditUser = $("#hauditUser").val();
		    	$("#auditUser").val(vauditUser);
		    	var vsBuyDate = $("#hsBuyDate").val();
		    	$("#sBuyDate").val(vsBuyDate);
		    	var veBuyDate = $("#heBuyDate").val();
		    	$("#eBuyDate").val(veBuyDate);
		    	var vstatus0 = $("#hstatus0").val();
		    	if( vstatus0 == "-1" ) {
		    		$("#status0").removeAttr("checked");
		    	} else {
		    		$("#status0").attr("checked", "checked");
		    	}
		    	var vstatus3 = $("#hstatus3").val();
		    	if( vstatus3 == "-1" ) {
		    		$("#status3").removeAttr("checked");
		    	} else {
		    		$("#status3").attr("checked", "checked");
		    	}
		    	var vstatus6 = $("#hstatus6").val();
		    	if( vstatus6 == "-1" ) {
		    		$("#status6").removeAttr("checked");
		    	} else {
		    		$("#status6").attr("checked", "checked");
		    	}
		    	var vstatus5 = $("#hstatus5").val();
		    	if( vstatus5 == "-1" ) {
		    		$("#status5").removeAttr("checked");
		    	} else {
		    		$("#status5").attr("checked", "checked");
		    	}
		    	var vstatus4 = $("#hstatus4").val();
		    	if( vstatus4 == "-1" ) {
		    		$("#status4").removeAttr("checked");
		    	} else {
		    		$("#status4").attr("checked", "checked");
		    	}
		    	
		    	
		    	var vbuyStockCode = $("#hbuyStockCode").val();
		    	$("#buyStockCode").val(vbuyStockCode);
		    	var vproductCode = $("#hproductCode").val();
		    	$("#productCode").val(vproductCode);
		    	var vproductName = $("#hproductName").val();
		    	$("#productName").val(vproductName);
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
		    /* onSelectPage :function(pageNumer,pageSize){
		    	
		    	$.messager.alert('New tip', pageNumber);
		    	$.messager.alert('New tip', pageSize);
		    	$.messager.alert('New tip', 'This is something new!!');
		    } */
		  
		   
		  });
		  function bsEdit() {
		  	var rowselect = $("#info_table" ).datagrid("getSelected");
	         if( rowselect == null ) {
	               $.messager.alert("提示" , "没有选择任何条目！" );
	        }
		  	window.location="<%=request.getContextPath()%>/admin/stock2/buyStockin.jsp?id="+rowselect['buy_stockin_id'];
		  }
		  function bsPrint() {
		  	var rowselect = $("#info_table" ).datagrid("getSelected");
                                                 if( rowselect == null ) {
                                                       $.messager.alert("提示" , "没有选择任何条目！");
                                                }
            if( rowselect['is_price_available'] == "0" ) {
		  		$.messager.alert("提示", "由于权限和状态限制该条目目前不能打印！");
		  	} else { 
		  		window.open("<%=request.getContextPath()%>/admin/rec/oper/buyStockin/buyStockinPrint.jsp?stockinId=" + rowselect['buy_stockin_id'], "_blank");
		  	}
		  }
		  function bsPrice() {
		  	var rowselect = $("#info_table" ).datagrid("getSelected");
                                                 if( rowselect == null ) {
                                                       $.messager.alert("提示" , "没有选择任何条目！" );
                                                }
            if( rowselect['is_price_available'] == "0" ) {
		  		$.messager.alert("提示", "由于权限和状态限制该条目目前不能查看价格！");
		  	} else { 
		  		window.open("<%=request.getContextPath()%>/admin/stock2/batchPrice.jsp?id="+rowselect['buy_stockin_id'],"_blank");
		  	}
		  }
		  function bsDell() {
		  	var rowselect = $("#info_table" ).datagrid("getSelected");
                                                 if( rowselect == null ) {
                                                       $.messager.alert("提示" , "没有选择任何条目！" );
                                                }
		  	if( rowselect['is_delete_available'] == "0" ) {
		  		$.messager.alert("提示", "由于权限和状态限制该条目不能删除！");
		  	} else {
			  	$.messager.confirm("确认框", "确认删除？", function (r) {
			  		if( r ) {
			  			window.location="<%=request.getContextPath()%>/admin/deleteBuyStockin.mmx?buyStockinId="+rowselect['buy_stockin_id'];
			  		} else {
			  			
			  		}
			  	});
		  	}
		  }
	</script>
<!--  
 <form method="post" action="/admin/stock2/collectBuyStockin.jsp">
 -->
	<table align="center" id="info_table" >
	</table>
<!--  
</form>
 -->
<%}%>
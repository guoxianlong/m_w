<%@page pageEncoding="UTF-8" import="java.util.*" contentType="text/html; charset=UTF-8" %>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.action.vo.voProductLine" %>
<%@ page import="adultadmin.bean.PagingBean" %>
<%@ page import="mmb.stock.stat.*,java.text.*" %>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,adultadmin.bean.stock.*" %>
<%@ page import="adultadmin.action.vo.*" %>
<%@ page import="adultadmin.bean.*" %>
<%
	int wareArea = -1;
	String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptionsAllWithRight(request, wareArea);
%>
<%
	String completeDatetime = StringUtil.convertNull(request.getParameter("completeDatetime"));
	if(completeDatetime==null||completeDatetime.equals("")){
		completeDatetime = DateUtil.getNowDateStr();
	}
	request.setAttribute("completeDatetime", completeDatetime);
	int areaId2 = StringUtil.StringToId(request.getParameter("area")==null?"-1":request.getParameter("area"));
	voUser user = (voUser)session.getAttribute("userView");
	UserGroupBean group = user.getGroup();
	boolean formexsit= group.isFlag(885);
%>
<html>
<head>
<title>采购上架单列表统计</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->
	<link href="<%=request.getContextPath()%>/css/global.css" rel="stylesheet" type="text/css">
	<script language="javascript" type="text/javascript" src="<%=request.getContextPath()%>/js/My97DatePicker/WdatePicker.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath()%>/js/easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" >
		
	function compareDate() {
	   var time1 = document.getElementById("checkinDatetime1_id");
	   var time2 = document.getElementById("checkinDatetime2_id");
	   if( (time1.value == "" && time2.value != "") || (time1.value != "" && time2.value == "")) {
		document.getElementById("time_span_compare").innerHTML="<font color='red'>开始时间和结束时间需要成对填写</font>";
		return false;
	   }else {
	   	if ( time1.value != "" && time2.value != "" && time2.value >= time1.value) {
	    return true;
	   } else if( time1.value != "" && time2.value != "" && time1.value > time2.value ) {
	   	document.getElementById("time_span_compare").innerHTML="<font color='red'>后面的日期需要大于或等于前面的日期</font>";
	   	return false;
   		}
	   }
	   
 	} 

 	function clearTimeSpan() {
 		document.getElementById("time_span_compare").innerHTML="";
 	}
 	
 	function check() {
 		
 		var time1 = document.getElementById("checkinDatetime1_id");
	    var time2 = document.getElementById("checkinDatetime2_id");
	    if( (time1.value == "" && time2.value != "") || (time1.value != "" && time2.value == "")) {
				 document.getElementById("time_span_compare").innerHTML="<font color='red'>开始时间和结束时间需要成对填写</font>";
		 		return false;
	    } else if( time1.value != "" && time2.value != "" && time1.value > time2.value ) {
	    		document.getElementById("time_span_compare").innerHTML="<font color='red'>后面的日期需要大于或等于前面的日期</font>";
	    		return false;
    	}
 		return true;
 	}
 	function checkNumber(obj) {
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于9位的数字!");
		   		} else {
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   	}
 	
 	function searchLoadPage() {
 		var time1 = document.getElementById("checkinDatetime1_id");
	    var time2 = document.getElementById("checkinDatetime2_id");
	    if( (time1.value == "" && time2.value != "") || (time1.value != "" && time2.value == "")) {
				 document.getElementById("time_span_compare").innerHTML="<font color='red'>开始时间和结束时间需要成对填写</font>";
		 		return;
	    } else if( time1.value != "" && time2.value != "" && time1.value > time2.value ) {
	    		document.getElementById("time_span_compare").innerHTML="<font color='red'>后面的日期需要大于或等于前面的日期</font>";
	    		return;
    	} else {
    		var wareArea = $("#wareArea").val();
    		$("#hwareArea").val(wareArea);
    		var checkinDatetime1 = $("#checkinDatetime1_id").val();
    		$("#hcheckinDatetime1_id").val(checkinDatetime1);
    		var checkinDatetime2 = $("#checkinDatetime2_id").val();
    		$("#hcheckinDatetime2_id").val(checkinDatetime2);
    		var productCode = $("#productCode").val();
    		$("#hproductCode").val(productCode);
    		var cartonningCode = $("#cartonningCode").val();
    		$("#hcartonningCode").val(cartonningCode);
    		var buyStockinCode = $("#buyStockinCode").val();
    		$("#hbuyStockinCode").val(buyStockinCode);
    		var upShelfCode = $("#upShelfCode").val();
    		$("#hupShelfCode").val(upShelfCode);
    		var upShelfStatus = $("#upShelfStatus").val();
    		$("#hupShelfStatus").val(upShelfStatus);
    		var upShelfUsername = $("#upShelfUsername").val();
    		$("#hupShelfUsername").val(upShelfUsername);
    		var cartonningUsername = $("#cartonningUsername").val();
    		$("#hcartonningUsername").val(cartonningUsername);
    		loadPage(-1);
    	}
 	}
   		
   		
   	 function loadPage(pageIdx) {
   		var time1 = document.getElementById("hcheckinDatetime1_id");
	    var time2 = document.getElementById("hcheckinDatetime1_id");
	    if( (time1.value == "" && time2.value != "") || (time1.value != "" && time2.value == "")) {
				 document.getElementById("time_span_compare").innerHTML="<font color='red'>开始时间和结束时间需要成对填写</font>";
		 		return;
	    } else if( time1.value != "" && time2.value != "" && time1.value > time2.value ) {
	    		document.getElementById("time_span_compare").innerHTML="<font color='red'>后面的日期需要大于或等于前面的日期</font>";
	    		return;
    	} else {
    		var vwareArea = $("#hwareArea").val();
    		var vcheckinDatetime1 = $("#hcheckinDatetime1_id").val();
    		var vcheckinDatetime2 = $("#hcheckinDatetime2_id").val();
    		var vproductCode = $("#hproductCode").val();
    		var vcartonningCode = $("#hcartonningCode").val();
    		var vbuyStockinCode = $("#hbuyStockinCode").val();
    		var vupShelfCode = $("#hupShelfCode").val();
    		var vupShelfStatus = $("#hupShelfStatus").val();
    		var vupShelfUsername = $("#hupShelfUsername").val();
    		var vcartonningUsername = $("#hcartonningUsername").val();
    		$("#wareArea").val(vwareArea);
    		$("#checkinDatetime1_id").val(vcheckinDatetime1);
    		$("#checkinDatetime2_id").val(vcheckinDatetime2);
    		$("#productCode").val(vproductCode);
    		$("#cartonningCode").val(vcartonningCode);
    		$("#buyStockinCode").val(vbuyStockinCode);
    		$("#upShelfCode").val(vupShelfCode);
    		$("#upShelfStatus").val(vupShelfStatus);
    		$("#upShelfUsername").val(vupShelfUsername);
    		$("#cartonningUsername").val(vcartonningUsername);
    		$.ajax({
    	   		  type: 'POST',
    	   		  url: '<%= request.getContextPath()%>/admin/getBuyStockinUpShelfOtherInfo.mmx',
    	   		  data: {pageIndex: pageIdx,
    	   			wareArea:vwareArea,
    	    		checkinDatetime1:vcheckinDatetime1,
    	    		checkinDatetime2:vcheckinDatetime2,
    	    		productCode:vproductCode,
    	    		cartonningCode:vcartonningCode,
    	    		buyStockinCode:vbuyStockinCode,
    	    		upShelfCode:vupShelfCode,
    	    		upShelfStatus:vupShelfStatus,
    	    		upShelfUsername:vupShelfUsername,
    	    		cartonningUsername:vcartonningUsername
    	   		  		},
    	   		  success: function (data, textStatus) {
    	   			var json = eval('(' + data + ')');
    	        	if( json['status'] == "fail" ) {
    	        		alert(json['tip']);
    	        	} else if ( json['status'] == "success" ) {
    	        		document.getElementById("totalSpan").innerHTML=json['totalCount'];
    	        		document.getElementById("pageLine").innerHTML=json['pageLine'];
    	        	}
    	   		  },
    	   		  dataType:'text'
    	   		});	
    			  $('#info_table').datagrid({
    					queryParams: {
    						pageIndex: pageIdx,
    						wareArea:vwareArea,
    	    	    		checkinDatetime1:vcheckinDatetime1,
    	    	    		checkinDatetime2:vcheckinDatetime2,
    	    	    		productCode:vproductCode,
    	    	    		cartonningCode:vcartonningCode,
    	    	    		buyStockinCode:vbuyStockinCode,
    	    	    		upShelfCode:vupShelfCode,
    	    	    		upShelfStatus:vupShelfStatus,
    	    	    		upShelfUsername:vupShelfUsername,
    	    	    		cartonningUsername:vcartonningUsername
    					},
    					url:'<%= request.getContextPath()%>/admin/getBuyStockinUpShelfList.mmx'
    				});
    			  $('#info_table').datagrid('getPager').pagination({
    				    displayMsg:'当前显示从{from}到{to}共{total}记录',
    				    onBeforeRefresh:function(pageNumber, pageSize){
    				     $(this).pagination('loading');
    				     $(this).pagination('loaded');
    				    }
    			  });
    	}
  		
	  }

	function testLoad() {
		alert(1);
		$.ajax({
  		  type: 'POST',
  		  url: '<%= request.getContextPath()%>/admin/getBuyStockinUpShelfList.mmx',
  		  data: {a:'a', b:'b'},
  		  success: function (data, textStatus) {
  			  alert(2);
  			  alert(data);
  		  },
  		  dataType:'text'
  		});
	}

	

	</script>
	<script type="text/javascript">
		$(function(){
			$("#result2").hide();
			$("#stockShelfCount").click(function(){
				var completeDatetime = $.trim($("#completeDatetime").val());
				var completeUsename = $.trim($("#completeUsename").val());
				var productCode = $.trim($("#productCode2").val());
				var area = $.trim($("#area").val());
				if(completeDatetime==""){
					alert("请选择时间");
					return false;
				}
				$.post('<%= request.getContextPath()%>/admin/stockShelfCount.mmx',{completeDatetime:completeDatetime,completeUsename:completeUsename,
						productCodes:productCode,area:area},function(data){
							var json = eval('('+data+')');
							if(json['status']=="success"){
								$("#result2").val(json['result']);
								$("#result2").show();
							}else if(json['status']=="failure"){
								alert(json['result']);
							}else if(json['status']=="needLogin"){
								alert(json['result']);
							}
				});
			});	
		});
	</script>
</head>
<body>


<div align="center"><h1>采购上架列表</h1></div>
<table align="center" width="95%">
		<tr><td align="left">
		<b>查询栏:</b>			
		</td></tr>
</table>
<div id="totalDiv" style="margin-left:88%;">共有(<span id="totalSpan">0</span>)条记录</div>
<div align="center">
<div style="border-style:dashed;border-color:#000000;border-width:1px;width:95%;">
	<form action="checkStockinMissionAction.do?method=getCheckReportInfo" method="post" onsubmit="return checkForm1();">
	<table cellspacing="8px"><tr><td align="left">
	入库时间：
	<input type="text" size="9" name="checkinDatetime1" id="checkinDatetime1_id" onclick="WdatePicker();" onfocus="clearTimeSpan();" value="${completeDatetime}"/>
	<input type="hidden" size="9" name="hcheckinDatetime1" id="hcheckinDatetime1_id" value=""/>
	到
	<input type="text" size="9" name="checkinDatetime2" id="checkinDatetime2_id" onclick="WdatePicker();" onfocus="clearTimeSpan();" value="${completeDatetime}"/>
	<input type="hidden" size="9" name="hcheckinDatetime2" id="hcheckinDatetime2_id" value=""/>
	<span id="time_span_compare"></span>
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	产品编号：
	<input type="text" size="13" name="productCode" id="productCode" value="" />
	<input type="hidden" size="13" name="hproductCode" id="hproductCode" value="" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	入库单:
	<input type="text" size="20" name="buyStockinCode" id="buyStockinCode" value="" />
	<input type="hidden" size="20" name="hbuyStockinCode" id="hbuyStockinCode" value="" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	装箱单:
	<input type="text" size="20" name="cartonningCode" id="cartonningCode" value="" />
	<input type="hidden" size="20" name="hcartonningCode" id="hcartonningCode" value="" />
	&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 
	</td></tr>
	<tr><td align="left">
	
	上架单：
		<input type="text" name="upShelfCode" id="upShelfCode" value="" size="13" />
		<input type="hidden" name="hupShelfCode" id="hupShelfCode" value="" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	上架状态：
		<select name="upShelfStatus" id="upShelfStatus">
			<option value="-1">请选择</option>
			<option value="1">待上架</option>
			<option value="2">上架中</option>
			<option value="3">已完成</option>
		</select>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
		<input type="hidden" name="hupShelfStatus" id="hupShelfStatus" value="-1" />
	装箱员：
		<input type="text" name="cartonningUsername" id="cartonningUsername" value="" size="13" />
		<input type="hidden" name="hcartonningUsername" id="hcartonningUsername" value="" />
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	上架 员：
		<input type="text" name="upShelfUsername" id="upShelfUsername" value="" size="13"/>
		<input type="hidden" name="hupShelfUsername" id="hupShelfUsername" value=""/>
		&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
	库地区:<%= wareAreaSelectLable %>
		<input type="hidden" name="hwareArea" id="hwareArea" value="-1" />
		</td></tr>
		<tr>
		<td align="right">
		<a href="#" class="easyui-linkbutton" iconCls="icon-search" onclick="searchLoadPage();" >查询</a>
		</td>
		</tr>
		</table>
		</form>
	</div>
	<br/>
	<%if(formexsit){ %>
	<form action="" method="post">
		<fieldset style="width:95%;">
			<legend>采购上架统计</legend>
			上架时间：<input type="text" name="completeDatetime" id="completeDatetime" size="10" onClick="WdatePicker();" onfocus="clearTimeSpan();" value="${completeDatetime}"/>&nbsp;&nbsp;
			&nbsp;&nbsp;上架员：<input type="text" name="completeUsename" id="completeUsename" size="20" value="${completeUsename }"/>
			&nbsp;&nbsp;产品编号：<input type="text" name="productCodes" id="productCode2" size="" value="${productCodes }"/>
			&nbsp;&nbsp;地区：
			<% List areaList = CargoDeptAreaService.getCargoDeptAreaList(request); 
			%>
			<select name="area" id="area">
				<%
					for(int j = 0;j<areaList.size();j++){
						ProductStockBean psBean = new ProductStockBean();
						int areaId = Integer.parseInt(areaList.get(j).toString());
				%>
				<option value="<%=areaList.get(j)%>" <%if(areaId==areaId2){%>selected<%}%>><%=psBean.getAreaName(areaId) %></option>
				<%
					}
				%>
			</select>
			&nbsp;&nbsp;<input type="button" id="stockShelfCount" value="统计">
		</fieldset>
	</form>
	&nbsp;&nbsp;&nbsp;<input type="text" size="100%" id="result2"  value=""/>
	<%} %>
	<br/>
	<table align="center" width="95%">
		<tr><td align="left">
		<h3>质检结果列表：</h3>			
		</td></tr>
	</table>
	
	<script type="text/javascript" >
	$(function(){
		   $('#info_table').datagrid({   
		    title:'采购上架列表', 
		    iconCls:'icon-ok',
		    width:1100,    
		    height:400,   
		    pageNumber:1,  
		    pageSize:20,    
		    pageList:[5,10,15,20], 
		    nowrap:false,  
		    striped: true,
		    collapsible:true,
		    //url:'../admin/easy/getStudentInfo', 
		    loadMsg:'数据装载中......', 
		    remoteSort:false, 
		    columns:[[
		     {title:'入库单号',field:'buy_stockin_code',width:'105',rowspan:2,align:'center'},
		     {title:'产品编号',field:'product_code',width:'75',rowspan:2,align:'center'},
		     {title:'入库时间',field:'buy_stockin_datetime',width:'90',rowspan:2,align:'center'},
		     {title:'装箱单',field:'cartonning_code',width:'105',rowspan:2,align:'center'},
		     {title:'装箱量',field:'cartonning_sum',width:'50',rowspan:2,align:'center'},
		     {title:'装箱员',field:'cartonning_username',width:'90',rowspan:2,align:'center'},
		     {title:'上架单',field:'cargo_operation_code',width:'130',rowspan:2,align:'center'},
		     {title:'上架员',field:'cargo_operation_username',width:'90',rowspan:2,align:'center'},
		     {title:'目的货位',field:'target_cargo_code',width:'140',rowspan:2,align:'center'},
		     {title:'上架状态',field:'cargo_operation_status',width:'60',rowspan:2,align:'center'},
		     {title:'上架时间',field:'cargo_operation_datetime',width:'105',rowspan:2,align:'center'}
		    ]],
		    //pagination:true,
		    rownumbers:true //是否有行号。
		   });
		   //$('#info_table').datagrid('getPager').pagination({
		   // displayMsg:'当前显示从{from}到{to}共{total}记录',
		  //  onBeforeRefresh:function(pageNumber, pageSize){
		   //  $(this).pagination('loading');
		  //   $(this).pagination('loaded');
		  //  }
		  // });
		   
		  });
	</script>
	<table align="center" id="info_table" >
	</table>
	<br>
	
	<span id="pageLine" style="MARGIN-LEFT: 20px"></span>
	</div>

</body>
</html>

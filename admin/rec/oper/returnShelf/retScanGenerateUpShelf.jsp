<%@ page contentType="text/html;charset=utf-8" %>
<%@ page import="adultadmin.action.stock.*, java.util.*, adultadmin.bean.stock.*, adultadmin.bean.order.*, adultadmin.action.vo.*, adultadmin.util.*" %>
<%@ page import="adultadmin.bean.*" %>
<%@ page import="adultadmin.bean.barcode.ProductBatchBarcodeBean" %>
<%@ page import="mmb.stock.cargo.*" %>
<%@ page import="adultadmin.bean.cargo.*,mmb.stock.stat.*" %>
<%
//物流员工考核
String wareAreaSelectLable = ProductWarePropertyService.getWeraAreaOptions(request);
String ranking =(String) request.getAttribute("ranking");
String firstCount =(String) request.getAttribute("firstCount");
String oneselfCount =(String) request.getAttribute("oneselfCount");
String productCount =(String) request.getAttribute("productCount");
%>
<html>
  <head>
    <title>生成退货上架汇总单</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<link href="../css/global.css" rel="stylesheet" type="text/css">
	<style type="text/css">
		.btn {
			BORDER-RIGHT: #7b9ebd 1px solid; 
			PADDING-RIGHT: 2px; 
			BORDER-TOP: #7b9ebd 1px solid; 
			PADDING-LEFT: 2px; 
			FONT-SIZE: 12px; 
			FILTER: progid:DXImageTransform.Microsoft.Gradient(GradientType=0, StartColorStr=#ffffff, EndColorStr=#cecfde); 
			BORDER-LEFT: #7b9ebd 1px solid; 
			CURSOR: hand; 
			COLOR: black; 
			PADDING-TOP: 2px; 
			height:25px;
			BORDER-BOTTOM: #7b9ebd 1px solid
		}
	</style>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
	<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/default/easyui.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/themes/icon.css">
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/js/easyui/demo/demo.css">
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
    <script type="text/javascript" src="<%=request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript">
jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
function ajaxCargoStaffPerformance(){
	$.ajax({
		type: "GET",
		url: "<%=request.getContextPath()%>/admin/ajaxCargoStaffPerformance.mmx?selectIndex=13",
		cache: false,
		dataType: "html",
		data: {type: "1"},
		success: function(msg, reqStatus){
			$("#performance").empty();
			$("#performance").html(msg);
		}
	});
}	
		var pathName=window.document.location.pathname;
		var projectName=pathName.substring(1,pathName.substr(1).indexOf('/')+1);
		function focusProductCode(){
			var productCode=document.getElementById("productCode").value;
			productCode = productCode.trim();
			if(productCode=="条码扫描区"){
				document.getElementById("productCode").value="";
				document.getElementById("productCode").style.color="#000000";
			}
		}
		
		function blurProductCode(){
			var productCode=document.getElementById("productCode").valu;
			productCode = productCode.trim();
			if(productCode==""){
				document.getElementById("productCode").value="条码扫描区";
				document.getElementById("productCode").style.color="#cccccc";
			} else {
				document.getElementById("productCode").style.color="#000000";
			}
			ajaxCargoStaffPerformance();
		}
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		
		function check(){
			var productCode = document.getElementById("productCode").value;
			if(productCode==""){
				document.getElementById("scanResult").value="退货上架单条码不能为空";
				return false;
			}

			var wareArea = document.getElementById("wareArea").value;
			$.post(
				"/"+projectName+"/admin/cacheProductCode.mmx",
				{productCode:productCode,wareArea:wareArea},
				function(result){
					var res = new Array();
					res = result.split(";");
					if(res.length>1 && res[1]=="0"){
						document.getElementById("preProductCode").value=productCode;
					}
					document.getElementById("scanResult").value=res[0];
					document.getElementById("productCode").value="";
				}
			);	
			return false;
		}
		function resetPage() {
			$.post("/"+projectName+"/admin/clearCachedProductCode.mmx",function(result){
				if(result=="0"){
					document.getElementById("scanResult").value="重置成功！";
				}else{
					document.getElementById("scanResult").value=result;
				}
			});	
			document.getElementById("productCode").value="";
			document.getElementById("productCode").focus();
		}

		function cancelPreProduct(){
			var preProduct = document.getElementById("preProductCode").value;
			if(preProduct == null || preProduct==""){
				document.getElementById("scanResult").value="上架单已经取消或者没有扫描，如果需要删除其它，请点击删除！";
			}else{
				$.post("/"+projectName+"/admin/cancelCacheProduct.mmx",
						{preProductCode:preProduct},
				function(result){
					if(result=="0"){
						document.getElementById("scanResult").value="删除成功,请扫描上架单！";
					}else{
						document.getElementById("scanResult").value=result;
					}
				});	
				document.getElementById("preProductCode").value="";
			}
		}



		function deleteProduct() {
		    var deleteCode = prompt("请输入上架单条码","")
		    if (deleteCode != null && deleteCode != "") {
		      document.getElementById("scanResult").value="请扫描上架单条码！";
		      $.post("/"+projectName+"/admin/deleteCacheUpShelf.mmx?retUpShelfCode="+deleteCode,function(result){
		    	  document.getElementById("scanResult").value=result;
			      });
		    } else if (deleteCode == "") {
		    	document.getElementById("scanResult").value="没有输入上架单条码";
		    }
		}

		function generateRetShelf(){
			var wareArea = document.getElementById("wareArea").value;
			$.post("/"+projectName+"/admin/generateRetUpShelf.mmx?wareArea=" + wareArea,
			function(result){
				if(result.indexOf("HWTS")!=-1){
					var rnum = Math.random()*10
					window.open("/"+projectName+"/admin/printRetShelf.mmx?upShelfCode="+result+"&ram="+rnum);
					document.getElementById("scanResult").value="生成汇总单成功，请扫描上架单条码！";
				}else{
					document.getElementById("scanResult").value=result;
				}
			ajaxCargoStaffPerformance();
			});	
		}
		
	</script>

  </head>	
<body onload="blurProductCode();" >
  	<div style="margin-left:15px;margin-top:15px;width:400px;height:200px;">
  	<h2 style="text-align:center;">生成退货上架汇总单</h2>
		<div id="performance" align="center"></div>
   		<div style="width:480px;height:270px;padding:10px;" class="easyui-panel" title="生成退货上架汇总单">
			<center>
   			<div style="text-align:center;margin-top:20px;">
   					库地区：&nbsp;&nbsp;&nbsp;&nbsp;
					<%= wareAreaSelectLable%>
					<br/>
					<br/>
   					<textarea rows="" cols="" style="width:90%;text-align:center;height:50px" id="scanResult">请扫描上架单条码</textarea>
   					<input type="hidden" id="preProductCode"></input>
   					<br/>
   					<form action="" method="post" onsubmit="return check();" >
	   					<div style="margin-top:10px;text-align:center;">
		   					<input style="width:80%"type="text" id="productCode" name="productCode" onblur="blurProductCode();" onfocus="focusProductCode();" />
		   				</div>
					</form>
					<div style="float:left; margin-top:1px;">
						<a style="margin-left:100px;" type="button" class="easyui-linkbutton" data-options="iconCls:'icon-reload'"  href="javascript:resetPage();" >重置 </a>
		   				<a style="margin-left:20px;" class="easyui-linkbutton" data-options="iconCls:'icon-undo'" href="javascript:deleteProduct();">删除指定上架单</a>
					</div>
					<div style="float:left; margin-top:10px;">
						<a style="margin-left:100px;"  class="easyui-linkbutton" data-options="iconCls:'icon-cancel'" href="javascript:cancelPreProduct();" >取消</a>
		   				<a style="margin-left:20px;" class="easyui-linkbutton" data-options="iconCls:'icon-ok'"  href="javascript:generateRetShelf();">生成退货上架汇总单</a>&nbsp;&nbsp;&nbsp;&nbsp;
	   				</div>
   			</div>
   			</center>
   			<br/>
   		</div>
   		
   		<p style="font-size:12px">
		说明：<br>
   		1、点击取消，取消上次扫描上架单。
   		2、点击重置，取消全部扫描上架单。
		</p>
   	</div>
  </body>
</html>

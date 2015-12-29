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
<script type="text/javascript">
$(function(){
	getCachedProductCodeCount(); //获取缓存的上架单数量
});

//播放音频文件
function Play(sound) {
	if(navigator.appName == "Microsoft Internet Explorer") {
		var snd = document.createElement("bgsound");
		document.getElementsByTagName("body")[0].appendChild(snd);
		snd.src = sound;
	}else{
		var obj = document.createElement("object");
		obj.width="0px";
		obj.height="0px";
		obj.type = "audio/x-wav";
		obj.data = sound;
		var body = document.getElementsByTagName("body")[0];
		body.appendChild(obj);
	}
}

//获取缓存的上架单数量
function getCachedProductCodeCount(){
	var url = '<%=request.getContextPath()%>/admin/returnStorageAction.do?method=getCachedProductCodeCount';
	$.post(url, function(data){
		$('#cachedCount').html(data);
	});
}

function ajaxCargoStaffPerformance(){
	$.ajax({
		type: "GET",
		url: "<%=request.getContextPath()%>/admin/returnStorageAction.do?method=ajaxCargoStaffPerformance&selectIndex=13",
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
			var productCode=trim(document.getElementById("productCode").value);
			if(productCode=="条码扫描区"){
				document.getElementById("productCode").value="";
				document.getElementById("productCode").style.color="#000000";
			}
		}
		
		function blurProductCode(){
			var productCode=trim(document.getElementById("productCode").value);
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
				"/"+projectName+"/admin/returnStorageAction.do?method=cacheProductCode",
				{productCode:productCode,wareArea:wareArea},
				function(result){
					var res = new Array();
					res = result.split(";");
					if(res.length>1 && res[1]=="0"){
						document.getElementById("preProductCode").value=productCode;
						getCachedProductCodeCount();
					}else{
						Play('<%=Constants.WARE_SOUND_SJYC %>');
					}
					document.getElementById("scanResult").value=res[0];
					document.getElementById("productCode").value="";
				}
			);	
			return false;
		}
		function resetPage() {
			$.post("/"+projectName+"/admin/returnStorageAction.do?method=clearCachedProductCode",function(result){
				if(result=="0"){
					document.getElementById("scanResult").value="重置成功！";
					getCachedProductCodeCount();
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
				$.post("/"+projectName+"/admin/returnStorageAction.do?method=cancelCacheProduct",
						{preProductCode:preProduct},
				function(result){
					if(result=="0"){
						document.getElementById("scanResult").value="删除成功,请扫描上架单！";
						getCachedProductCodeCount();
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
		      $.post("/"+projectName+"/admin/returnStorageAction.do?method=deleteCacheUpShelf&retUpShelfCode="+deleteCode,function(result){
		    	  document.getElementById("scanResult").value=result;
		    	  if(result == '该上架单删除成功！'){
		    		  getCachedProductCodeCount();
		    	  }else{
		    		  Play('<%=Constants.WARE_SOUND_SJYC %>');
		    	  }
			  });
		    } else if (deleteCode == "") {
		    	document.getElementById("scanResult").value="没有输入上架单条码";
		    }
		}

		function generateRetShelf(){
			var wareArea = document.getElementById("wareArea").value;
			$.post("/"+projectName+"/admin/returnStorageAction.do?method=generateRetUpShelf&wareArea=" + wareArea,
			function(result){
				if(result.indexOf("HWTS")!=-1){
					var rnum = Math.random()*10
					window.open("/"+projectName+"/admin/printShelfAction.do?method=printRetShelf&upShelfCode="+result+"&ram="+rnum);
					document.getElementById("scanResult").value="生成汇总单成功，请扫描上架单条码！";
					getCachedProductCodeCount();
				}else{
					document.getElementById("scanResult").value=result;
					Play('<%=Constants.WARE_SOUND_SJYC %>');
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
   		<div style="background-color:#FFFF93;width:400px;height:230px;border-style:solid;border-width:1px;border-color:#000000;">
   			<div style="text-align:center;margin-top:5px;">
   				<div style="color:black;height:35px;font-size:18px;margin-bottom:10px;">
   					<span>已扫描退货上架单数:</span>
   					<span id="cachedCount" style="font-weight:bold;color:red;font-size:30px;">0</span>
   				</div>
   					库地区：&nbsp;&nbsp;&nbsp;&nbsp;
					<%= wareAreaSelectLable%>
   					<textarea rows="" cols="" style="width:90%;text-align:center;height:50px" id="scanResult">请扫描上架单条码</textarea>
   					<input type="hidden" id="preProductCode"></input>
   					<form action="" method="post" onsubmit="return check();" >
	   					<div style="margin-top:10px;text-align:center;">
		   					<input style="width:80%"type="text" id="productCode" name="productCode" onblur="blurProductCode();" onfocus="focusProductCode();" />
		   				</div>
					</form>
					<div style="float:left; margin-top:1px;">
						<input style="margin-left:100px;" type="button" class="btn" value="重置 " onclick="resetPage();" />
		   				<input style="width:130px;margin-left:20px;" type="button" class="btn" value="删除指定上架单" onclick="deleteProduct();"/>
					</div>
					<div style="float:left; margin-top:10px;">
						<input style="margin-left:100px;" type="button" class="btn" value="取消 " onclick="cancelPreProduct();" />
		   				<input style="margin-left:20px;width:130px;" type="button" class="btn" value="生成退货上架汇总单" onclick="generateRetShelf();"/>&nbsp;&nbsp;&nbsp;&nbsp;
	   				</div>
   			</div>
   		</div>
   		<p style="font-size:12px">
		说明：<br>
   		1、点击取消，取消上次扫描上架单。
   		2、点击重置，取消全部扫描上架单。
		</p>
   	</div>
  </body>
</html>

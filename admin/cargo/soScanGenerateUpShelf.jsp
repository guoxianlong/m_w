<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="adultadmin.bean.cargo.*"%>
<%@ page import="mmb.stock.cargo.*"%>
<%@ page import="adultadmin.util.*"%>
<%@ page import="adultadmin.bean.UserGroupBean"%>
<%@ page import="adultadmin.action.vo.voUser"%><html>
  <head>
    <title>pda生成退货上架汇总单</title>
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<style type="text/css">
		form {margin:0; padding:0;}
	</style>
	<%
	response.setHeader("Cache-Control","no-store");
	response.setHeader("Pragrma","no-cache");
	response.setDateHeader("Expires",0);
	String result = (String)request.getAttribute("result");
	String area = (String)request.getSession().getAttribute("area");
	voUser user = (voUser)request.getSession().getAttribute("userView");
	UserGroupBean group = user.getGroup();
	boolean flag = true;
	if(!group.isFlag(616)){
		flag = false;
	}
%>
	<script language="JavaScript" src="<%=request.getContextPath()%>/js/jquery-1.6.1.js"></script>
	<script language="JavaScript" src="../js/JS_functions.js"></script>
	<script type="text/javascript">
		<%if(!flag){%>
		alert("您没有操作该功能的权限！");
		window.location='<%=request.getContextPath()%>/admin/stockOperation.do?method=logout';
		<% }%>
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
			document.getElementById("productCode").focus();
			var productCode=trim(document.getElementById("productCode").value);
			if(productCode==""){
				document.getElementById("productCode").style.color="#cccccc";
			} else {
				document.getElementById("productCode").style.color="#000000";
			}
		}
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		
		function check(){
			var productCode = document.getElementById("productCode").value;
			if(productCode==""){
				document.getElementById("scanResult").value="上架单条码不能为空";
				return false;
			}
			$.post(
				"/"+projectName+"/admin/returnStorageAction.do?method=cacheProductCode",
				{productCode:productCode},
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
			$.post("/"+projectName+"/admin/returnStorageAction.do?method=clearCachedProductCode",function(result){
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
				$.post("/"+projectName+"/admin/returnStorageAction.do?method=cancelCacheProduct",
						{preProductCode:preProduct},
				function(result){
					if(result=="0"){
						document.getElementById("scanResult").value="取消成功，请扫描上架单！";
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
			      });
		    } else if (deleteCode == "") {
		    	document.getElementById("scanResult").value="没有输入上架单条码";
		    }
		}

		function generateRetShelf(){
			$.post("/"+projectName+"/admin/returnStorageAction.do?method=generateRetUpShelf",
			function(result){
				if(result.indexOf("HWTS")!=-1){
					window.open("/"+projectName+"/admin/printShelfAction.do?method=printRetShelf&upShelfCode="+result);
					document.getElementById("scanResult").value="生成汇总单成功，请扫描上架单！";
				}else{
					document.getElementById("scanResult").value=result;
				}
			});	
		}
		function returnBack(){
			window.location="/"+projectName+"/admin/stockOperation.do?method=stockOperation&toPage=zuoyejiaojie";
		}
	</script>

  </head>	
<body onload="blurProductCode();" style="overflow:hidden;width:220px;height:220px;">
<div style="text-align:center;width:220px;">
<font size="4" style="font-weight:bold;color:black">生成退货上架汇总单</font>
<% if("1".equals(area)){ %>
<font color="red" size="4">芳村</font><% }else if("3".equals(area)){%>
<font color="red" size="4">增城</font><% }else{%>
<font color="red" size="4">未选</font><% }%><br></br>
<font style="color:black"><%=((voUser)request.getSession().getAttribute("userView")).getUsername() %></font>
<a href="<%=request.getContextPath()%>/admin/stockOperation.do?method=logout">
[<font color="red" size="2">注销</font>]</a>
</div>
<div style="text-align:center;width:220px;">
<font style="color:black"><%=DateUtil.getNow() %></font>	
</div>					
  	<div style="margin-top:5px;margin-left:5px;width:220px;height:220px;">
   		<div>
				<textarea rows="" cols="" style="width:90%;text-align:center;height:50px" id="scanResult">请扫描上架单条码</textarea>
   					<input type="hidden" id="preProductCode"></input>
   					<form action="" method="post" onsubmit="return check();" >
	   					<div style="margin-top:10px;margin-left:10px">
		   					<input style="width:80%"type="text" id="productCode" name="productCode" onblur="blurProductCode();" onfocus="focusProductCode();" />
		   				</div>
					</form>
					<div style="margin-top:1px;">
						<input style="width:80px;" type="button" value="重置 " onclick="resetPage();" />
		   				<input style="width:120px;" type="button" value="删除指定上架单" onclick="deleteProduct();"/>
					</div>
					<div style="margin-top:1px;">
						<input style="width:80px;" type="button" value="取消 " onclick="cancelPreProduct();" />
		   				<input style="width:120px;" type="button" value="生成退货上架汇总单" onclick="generateRetShelf();"/>
	   				</div>
	   				<div style="margin-top:1px;">
 						<input style="width:203px;" type="button" value="返回" onclick="returnBack();"/>
   					</div>
   		</div>
   		<p style="font-size:12px">
		说明：
   		1、点击取消，将取消上次操作。
   		2、点击重置，将取消之前所有操作。
		</p>
   	</div>
  </body>
  <script type="text/javascript">
  document.body.style.backgroundImage="url(/"+projectName+"/image/soBg.jpg)";
  </script>
</html>

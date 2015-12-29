<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="adultadmin.util.Constants" %>
<!DOCTYPE html>
<%
	String soundSrcSJYC = Constants.WARE_SOUND_SJYC;
%>
<html>
<head>
	<meta charset="UTF-8">
	<title>质检结果初录</title>
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/icon.css">
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/demo/demo.css">
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery-1.8.0.min.js"></script>
	<script type="text/javascript" src="<%= request.getContextPath() %>/js/easyui/jquery.easyui.min.js"></script>
	<script>
	jQuery.messager.defaults = {ok:"确认", cancel:"取消"};
	//设置同步
	$.ajaxSetup({  
   		async : false  
	});
	$.extend($.fn.validatebox.defaults.rules, { 
		minLength: { 
			validator: function(value, param){ 
			return value.length >= param[0]; 
			}, 
			message: '最少输入 {0} 个字符.' 
		},
		sign_integer:{
			validator:function(value){
				return /^[1-9][0-9]{0,}$/.test(value);
			},
			message:'请输入正整数！'
		} 
	});
//点击入库按钮
function submitForm(){
			var csmcode = $("#csmcode").text();
	    	   var productCount=$("#productCount").val();		    	
		    	var binning=parseInt($("#binning").val(),10);
		    	var batchId=$("#batchId").val();
		    	var missionId=$("#missionId").val();
		    	var product_Code=$("#product_Code").val();
		    	var buyStockinCode = $("#buyStockinCode").val();
		    	var currentCount = $("#currentCount").val();
	    		if(productCount==""){
	    			Play('<%= soundSrcSJYC%>');
	    			alert("扫描商品件数不能为空!");
	    			return;
	    		}
	    		if(isNaN(productCount)){
	    			Play('<%= soundSrcSJYC%>');
	    			alert("商品件数只能是数字!");
	    			return;
	    		}
	    		var product_Code=$("#product_Code").val();
	    		if(productCount=="0"||productCount==0){
	    			Play('<%= soundSrcSJYC%>');
	    			alert("请至少扫描一件商品!");
	    			return;
	    		}
	    		var batchId=$("#batchId").val();
	    		var secondStatus = $("#secondStatus").val();
	    		$("#productCode").val("");
	    		$.post('checkStatus2.mmx',{batchId:batchId,type:2},function(result){
	    		var rs = result.split(",");
	    			if(rs[0]=="0"){//先录入的是复录
		    		$.post('updateFristCheck2.mmx',{csmCode:csmcode,productCount:productCount,batchId:batchId,type:2},function(msg){
		    			var msg1 = msg.split(",")[0];
		    			var msg2 = msg.split(",")[1];
						 if(msg1=="1"){
						 	 $("#result").html("初录成功!");
						 }else if(msg1=="-1"){
							 	if( msg2 == "0" ) {
							 		Play('<%= soundSrcSJYC%>');
							 		$.messager.alert("提示","采购已完成，不允许执行入库操作!");
							 	} else  if ( msg2 == "1" ) {
							 		Play('<%= soundSrcSJYC%>');
							 		$.messager.alert("提示","未找到质检入库信息！");
							 	} else  if ( msg2 == "2" ) {
							 		Play('<%= soundSrcSJYC%>');
							 		$.messager.alert("提示","质检入库任务为已完成，不可以继续录入！");
							 	} else if( msg2 == "3" )  {
							 		Play('<%= soundSrcSJYC%>');
							 		$.messager.alert("提示","质检入库任务为已删除，不可以继续录入！");
							 	} else {
							 		Play('<%= soundSrcSJYC%>');
							 		$.messager.alert("提示","系统异常！");
							 	}
						}else{
						 	Play('<%= soundSrcSJYC%>');
						 	alert("初录失败!");
						 }
						
						 $("#productCount").val(msg.split(",")[1]);
				    	$("#product_count").val(msg.split(",")[1]);		    		
		    		});
	    		}else{//入库操作
	    			Play('<%= soundSrcSJYC%>');
	    			$("#error").html("复录已提交!");
	    			$("#result").html("");
	    		}
	    		});
	    		
	    	   
		}

function validProduct(obj,csmcode){
	if(obj==""){
	    		Play('<%= soundSrcSJYC%>');
	    		alert("商品编号不能为空或输入终止码!");
	    		return;
	    	}
	$.post('validProduct2.mmx',{productCode:obj,csmCode:csmcode},function(msg){
		var productId=$("#productId").val();
	    		if (msg == "-1") {
			     	//alert('商品不存在!');
			     	Play('<%= soundSrcSJYC%>');
			     	$("#error").html(obj+"不存在!<br>"+$("#error").html());
	    		}
			    else if(msg != "-1" && msg != productId){
			    	//alert("第"+(parseInt($("#product_count").val(),10)+1)+"个商品不属于该任务!");
			    	Play('<%= soundSrcSJYC%>');
			    	$("#error").html("第"+(parseInt($("#product_count").val(),10)+1)+"个商品不属于该任务!<br>"+$("#error").html());
			    	$("#productCode").val("");
			    }else{
			        var count = $("#productCount").val();
			        if(count==""){
				    	var productcount = parseInt($("#product_count").val(),10);		    	
				    	$("#productCount").val(productcount+1);			    	
				    	$("#product_Code").val($("#productCode").val());			    	
				    	$("#productCode").val("");	  
			    	}else{
			    		count = parseInt(count,10);
			    		$("#productCount").val(count+1);
			    		$("#product_Code").val($("#productCode").val());
				    	$("#productCode").val("");
				    	$("#product_count").val(parseInt($("#product_count").val(),10)+1);
			    	}  	
			    }
	    	});
	}
	
//复录数是否>=预定数
function validComplete(missionid){
	var batchId=$("#batchId").val();
	var missionId=$("#missionId").val();
	$.post('validComplete2.mmx',{missionId:missionid},function(msg){
		if(msg=="0"){
		    	if(confirm("请确认是否存在未完成的入库单!")){
		    		
		    	}else{
		    		//完成采购
		    		confirmCom(batchId,missionId);
		    	}
		    }
	});
}
//确认完成采购
function confirmCom(batchId,missionId){
jQuery.post("judgeExistUnqualifiedInfo2.mmx",{missionId:missionId},
				function(result){
					if(result=="0"){
					var t1 = window.showModalDialog("cargo/valComplete1.jsp","请确认","dialogWidth:200px;dialogHeight:150px;status=no;help=no;location=no");
						if(t1){
							//alert("到货差异量为：" + diffCount);
							window.location.href="confirmComCheckStockin2.mmx?batchId="+batchId+"&missionId="+missionId+"&type=1";
						}
					}else{
						window.location.href="confirmComCheckStockin2.mmx?batchId="+batchId+"&missionId="+missionId+"&type=1";
					}
				}
			);
}
function init(){
	var batchId = $("#batchId").val();
	$.post('initLeftCount2.mmx',{batchId:batchId},function(result){
	    if(result=="-1"){
	    	Play('<%= soundSrcSJYC%>');
	    	alert("未找到对应的数据!");
	    }
	    else if(result=="error"){
	   		Play('<%= soundSrcSJYC%>');
	   		alert("初始化时出现异常，请联系管理员!");
	    }
	   	else {
			$("#productCount").val(result);
	   	}
	});
}
	</script>
</head>
<body onunload="javascript:disposeRes();" onload="javascript:pageLoadInit();">
    <div id="restart" style="position: absolute; width: 100px; height: 36px; right: 0px; top: 0px; cursor: pointer; background-color: red; font-size: 36px;font-weight: bold;text-align: center;line-height: 36px;" onclick="javascript:reStart();" >
		开启
	</div>
	<center>	
		<h3>质检结果初录录入</h3>
		<div class="easyui-panel" style="width:500px;" align="center">
			<br/>
			<table>
				<tr>
					<td align="left"><b>预计单号：</b></td>
					<td align="left">
					<form action="" method="post" onsubmit="return checkBuyStockCode();">
					<input id="buyStockCode" name="buyStockCode" class="easyui-validatebox" />
					</form>
					</td>
					<td align="left"><span id="buyStockInfo"></span></td>
				</tr>
				<tr>
					<td align="left">
					<b>SKU:</b> 
					</td>
					<td align="left">
					<form action="" method="post" onsubmit="return checkProductCodeAndGetCheckStockinInfo();">
					<input id="productCode" name="productCode" class="easyui-validatebox" />
					</form>
					</td>
					<td align="left"><b>数量:</b><input size="5" id="checkCount" name="checkCount" class="easyui-validatebox" value="0" onchange="checkNumber(this);"/></td>
				</tr>
				<tr>
					<td align="left">
					</td>
					<td align="left">
					<br/>
					</td>
					<td align="left">
					</td>
				</tr>
				<tr >
					<td colspan="3">
					
					扫描顺序：预计单号,商品条码,终止码(CMDYJ);并进行计数<br/>
					初录完成后才可以点击【重新初录】按钮，复录完成的不允许再重新初录
					</td>
				</tr>
			</table>
			<input type="hidden" name="hideBuyStockCode" id="hideBuyStockCode" value="" />
			<input type="hidden" name="hideProductCode" id="hideProductCode" value="" />
			<input type="hidden" name="hideProductId" id="hideProductId" value="" />
			<input type="hidden" name="hideCheckStockinMissionId" id="hideCheckStockinMissionId" value="" />
			<input type="hidden" name="hideCheckStockinMissionBatchId" id="hideCheckStockinMissionBatchId" value="" />
			<br/>
		</div>
			<div id="tipInfo" class="easyui-panel" style="width:500px;height:200px;background:green;">
			
			</div>	
	</center>
		<input type="hidden" id="checkStockinMissionMark" value="0"  />
	<script type="text/javascript">
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		var plugin = null;
		function checkNumber(obj) {
   			var pattern = /^[0-9]{1,9}$/;
   			var pattern2 = /^[0-9]{1,}$/;
   			var number = obj.value;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    
	   			} else if (pattern2.exec(number)) {
		   		 	Play('<%= soundSrcSJYC%>');
		   			obj.value="";
		   			obj.focus();
		   		 	alert("请不要输入大于9位的数字!");
		   		} else {
	    			Play('<%= soundSrcSJYC%>');
	   				obj.value="";
	   				obj.focus();
	    			alert("请填入整数！！");
	   			}
   			}
   		}
		
		function checkBuyStockCode() {
			var buyStockCode = $("#buyStockCode").val();
			buyStockCode = buyStockCode.trim();
			if( buyStockCode  == null || buyStockCode == "" ) {
				Play('<%= soundSrcSJYC%>');
				$.messager.alert("提示", "预计单号不能为空！",'error', function () {
					$("#buyStockCode").val("");
					$("#buyStockCode").focus();
				}); 
				return false;
			}
		 	$("#buyStockCode").attr("disabled", true);
		 	$('#productCode').removeAttr('disabled');
	 		$("#hideBuyStockCode").val(buyStockCode);
		 	$("#productCode").focus();
			return false;
		}
		
		function checkProductCodeAndGetCheckStockinInfo() {
			var buyStockCode = $("#hideBuyStockCode").val();
			buyStockCode = buyStockCode.trim();
			if( buyStockCode == "" || buyStockCode == null ) {
				Play('<%= soundSrcSJYC%>');
				$.messager.alert("提示", "还没有录入合适的预计到货单!",'error', function () {
				});
				return false;
			}
			var checkCount2 = $("#checkCount").val();
	        var tempCount2 = parseInt(checkCount2,10);
	        var tempCountError = tempCount2 + 1;
			var productCode = $("#productCode").val();
			productCode = productCode.trim();
			var pattern = /^[\w\d]{1,30}$/;
   			var number = productCode;
   			if( number != "" ) {
	   			if(pattern.exec(number)) {
	    			
	   			} else if ( number == "CMDYJ" ) {
	   				
	   			} else {
	   				$("#tipInfo").css("background","#FF0000");
	   				//$("#tipInfo").css("font-color","black");
	   				var sht = $("#tipInfo").html();
					sht = "<font size='5' color='white'>"+"第"+tempCountError+"商品编号格式错误！"+"</font><br/>" + sht;
					$("#tipInfo").html(sht);
					Play('<%= soundSrcSJYC%>'); 
	   				$.messager.alert("提示", "第"+tempCountError+"商品编号格式错误！",'error', function () {
		                 	$("#productCode").removeAttr('disabled');
							$("#productCode").val("");
							$("#productCode").focus();
						}); 
						return false;
	   			}
   			} else {
				Play('<%= soundSrcSJYC%>');
   				$.messager.alert("提示", "商品编号不能为空！",'error', function () {
		                 	$("#productCode").removeAttr('disabled');
							$("#productCode").val("");
							$("#productCode").focus();
						});  
						return false;
   			}
			var checkStockinMissionMark = $("#checkStockinMissionMark").val();
			var hideProductCode = $("#hideProductCode").val();
			hideProductCode = hideProductCode.trim();
			if( checkStockinMissionMark == "0" ) {
				$.post( 
				"<%= request.getContextPath()%>/admin/check/checkProductCodeAndGetCheckStockinInfo.mmx",
				{
					buyStockCode:buyStockCode,
					productCode:productCode
				}
				,
				function (data,textStatus) {
	                 if( data['status'] == "success" ) {
						$("#buyStockInfo").html("入库任务单号：<a href='<%= request.getContextPath()%>/admin/editCheckStockinMission.mmx?missionId="+data['check_stockin_mission_id']+"' target='_blank' >"+data['check_stockin_code']+"</a>");
						$("#productCode").removeAttr('disabled');
						$("#hideProductCode").val(productCode);
			             $("#checkStockinMissionMark").val("1");
			             $("#hideCheckStockinMissionId").val(data['check_stockin_mission_id']);
			             $("#hideCheckStockinMissionBatchId").val(data['check_stockin_mission_batch_id']);
			             $("#hideProductId").val(data['product_id']);
			             var checkCount = $("#checkCount").val();
			             var iCheckCount = parseInt(checkCount,10);
			             iCheckCount ++;
			             $("#checkCount").val(iCheckCount);
			             $("#tipInfo").css("background","green");
	                 } else if (data['status'] == "created" ) {
	                 	$("#tipInfo").css("background","green");
	                 	var sht = $("#tipInfo").html();
						sht = "<font size='5' color='black'>创建了新的入库单！"+data['check_stockin_code']+"</font><br/>" + sht;
						$("#tipInfo").html(sht);
						$("#buyStockInfo").html("入库任务单号：<a href='<%= request.getContextPath()%>/admin/editCheckStockinMission.mmx?missionId="+data['check_stockin_mission_id']+"' target='_blank' >"+data['check_stockin_code']+"</a>");
	                 	$("#productCode").removeAttr('disabled');
	                 	$("#hideProductCode").val(productCode);
			             $("#checkStockinMissionMark").val("1");
			             $("#hideCheckStockinMissionId").val(data['check_stockin_mission_id']);
			             $("#hideCheckStockinMissionBatchId").val(data['check_stockin_mission_batch_id']);
			             $("#hideProductId").val(data['product_id']);
			             var checkCount = $("#checkCount").val();
			             var iCheckCount = parseInt(checkCount,10);
			             iCheckCount ++;
			             $("#checkCount").val(iCheckCount);
			             $("#tipInfo").css("background","green");
	                 } else if ( data['status'] == "fail" ) {
	   					//$("#tipInfo").css("font-color","white");
	                 	$("#tipInfo").css("background","#FF0000");
	                 	var sht = $("#tipInfo").html();
						sht = "<font size='5' color='white'>"+data['tip']+"</font><br/>" + sht;
						$("#tipInfo").html(sht);
	                 	//document.getElementById("tipInfo").style="background-color:red";
						Play('<%= soundSrcSJYC%>');
						$.messager.alert("提示", data['tip'],'error', function () {
		                 	$("#productCode").removeAttr('disabled');
							$("#productCode").val("");
							$("#productCode").focus();
						});                 	
	                 }
	            },
	             "json");
	             $("#productCode").val("");
				 $("#productCode").focus();
			} else {
				if( hideProductCode == "" ) {
					$("#tipInfo").css("background","#FF0000");
						Play('<%= soundSrcSJYC%>');
					$.messager.alert("提示", "当前还没有找到质检任务单！",'error', function () {
						//$("#productCode").removeAttr('disabled');
						$("#productCode").val("");
						$("#productCode").focus();
					});
					var sht = $("#tipInfo").html();
					sht = "<font size='5' color='white'>"+"当前还没有找到质检任务单！"+"</font><br/>" + sht;
					$("#tipInfo").html(sht);
				} else if ( productCode == "CMDYJ") {
					//这是结束的标志，需要相应的去修改对应任务单的初录状态。
					var hCheckStockinMissionId = $("#hideCheckStockinMissionId").val();
					var hCheckStockinMissionBatchId = $("#hideCheckStockinMissionBatchId").val();
					var hProductId = $("#hideProductId").val();
					var hBuyStockCode = $("#hideBuyStockCode").val();
					if( tempCount2 == 0 ) {
							Play('<%= soundSrcSJYC%>');
						$.messager.alert("提示", "当前录入数量为0不可以提交！",'error', function () {
							$("#productCode").val("");
							$("#productCode").focus();
						});
						return false;
					} 
					if( hCheckStockinMissionId == "" ) {
							Play('<%= soundSrcSJYC%>');
						$.messager.alert("提示", "当前没有对应的质检任务单，不可以提交初录结果！",'error', function () {
							$("#productCode").val("");
							$("#productCode").focus();
						});
						return false;
					}
					if( hProductId == "" ) {
							Play('<%= soundSrcSJYC%>');
						$.messager.alert("提示", "当前没有可录入的商品信息！",'error', function () {
							$("#productCode").val("");
							$("#productCode").focus();
						});
						return false;
					}
					//修改了
					$.post( "<%= request.getContextPath()%>/admin/check/newFirstCheckAutomatic.mmx",
					{
							checkStockinMissionId:hCheckStockinMissionId,
							productId:hProductId,
							firstCheckCount:checkCount2,
							checkStockinMissionBatchId:hCheckStockinMissionBatchId,
							buyStockCode:hBuyStockCode
					},
					function (data,textStatus) {
						if ( data['status'] == "success" ) {
							$("#tipInfo").css("background","green");
							$.messager.alert("提示", "质检初录成功！",'info', function () {
								$("#productCode").val("");
								$("#productCode").focus();
								resetInfos();
							});
							return false;
						} else {
							$("#tipInfo").css("background","#FF0000");
								Play('<%= soundSrcSJYC%>');
							$.messager.alert("提示", data['tip'],'error', function () {
								$("#productCode").val("");
								$("#productCode").focus();
							});
							return false;
						} 
            		}, 
            		"json");
					return false;
				} else if ( productCode != hideProductCode && productCode != "CMDYJ" ) {
	                $("#tipInfo").css("background","#FF0000");
						Play('<%= soundSrcSJYC%>');
					$.messager.alert("提示", "第" + tempCountError + "个商品不属于该任务单!",'error', function () {
						//$("#productCode").removeAttr('disabled');
						$("#productCode").val("");
						$("#productCode").focus();
						});
						var sht = $("#tipInfo").html();
						sht = "<font size='5' color='white'>"+"第" + tempCountError + "个商品不属于该任务单!"+"</font><br/>" + sht;
						$("#tipInfo").html(sht);
				} else if ( productCode == hideProductCode ) {
		            $("#tipInfo").css("background","green");
		            var iCheckCount = parseInt(checkCount2,10);
		            iCheckCount ++;
		            //alert(iCheckCount);
		            $("#checkCount").val(iCheckCount);
		            $("#productCode").val("");
					$("#productCode").focus();
				}
			}
			return false;
		}
		
		function resetInfos() {
			$("#buyStockCode").attr('disabled', true);
			$("#hideProductCode").val("");
			$("#hideProductId").val("");
			$("#hideCheckStockinMissionId").val("");
			$("#hideCheckStockinMissionBatchId").val("");
			
			$("#checkCount").val("0");
			$("#checkStockinMissionMark").val("0");
			$("#tipInfo").html("");
			$("#buyStockInfo").html("");
			$("#productCode").focus();
			$("#tipInfo").css("background","green");
		}
		function resetFirstCheck() {
			var hCheckStockinMissionId = $("#hideCheckStockinMissionId").val();
			var hCheckStockinMissionBatchId = $("#hideCheckStockinMissionBatchId").val();
			var hBuyStockCode = $("#hideBuyStockCode").val();
			var hProductCode = $("#hideProductCode").val();
			
			 $.post( "<%= request.getContextPath()%>/admin/check/resetFirstCheck.mmx",
			 {
			 	checkStockinMissionId:hCheckStockinMissionId,
				checkStockinMissionBatchId:hCheckStockinMissionBatchId,
				buyStockCode:hBuyStockCode
			 },
			 function (data,textStatus) {
                       if( data['status'] == "fail" ) {
                       Play('<%= soundSrcSJYC%>');
                       	$.messager.alert("提示", data['tip'],'error', function () {
							window.location.href='<%= request.getContextPath()%>/admin/editCheckStockinMission.mmx?missionId="+hCheckStockinMissionId+"';	
						});
                       } else if (data['status'] == "success" ) {
                       		beginCheck(hCheckStockinMissionId,hCheckStockinMissionBatchId,hBuyStockCode,hProductCode);
                       }
            }, "json");
			
		}
		
	function beginCheck(missionId,batchId,buyStockinCode,productCode){
    	window.location.href="beginCheck.mmx?batchId="+batchId+"&missionId="+missionId+"&buyStockinCode="+buyStockinCode+"&productCode="+productCode;
    }
    function  Play(sound) {
     		 if(navigator.appName == "Microsoft Internet Explorer")
	      	{
	       		 var snd = document.createElement("bgsound");
		         document.getElementsByTagName("body")[0].appendChild(snd);
		         snd.src = sound;
	     	}
	     	else
	     	{
	            var obj = document.createElement("object");
	            obj.width="0px";
	            obj.height="0px";
	            obj.type = "audio/x-wav";
	            obj.data = sound;            
	            var body = document.getElementsByTagName("body")[0];
	            body.appendChild(obj);
     		}
     		 
     		stopLine();
		}
    
    // 断开传送带
    function allClose() {    	
 		if ( plugin == null ) {
 			plugin = new ActiveXObject("PlaySound.Plugin");
 			plugin.OpenPort();
 		}
 		plugin.AllClose();
 		$("#checkCount").attr("readonly","readonly");
 		$("#productCode").attr("readonly","readonly");
 		$("#buyStockCode").attr("readonly","readonly");
    }
    
    // 闭合传送带
    function allOpen() { 		
 		if ( plugin == null ) {
 			plugin = new ActiveXObject("PlaySound.Plugin");
 			plugin.OpenPort();
 		}
 		plugin.AllOpen();
    }
    
    function pageLoadInit(){
    	$('#buyStockCode').removeAttr('disabled');
    	$('#buyStockCode').val('');
    	$('#buyStockCode').focus();
    	$('#productCode').attr('disabled', true);
 		
    	$("#buyStockCode").val('  ');
    	$("#buyStockCode").val('');
 		$("#buyStockCode").attr("readonly","readonly"); 
 		
 		$("#productCode").val('  ');
    	$("#productCode").val('');
 		$("#productCode").attr("readonly","readonly");

 		$("#checkCount").attr("readonly","readonly");
    }
    var isOpened = false;
    function reStart(){
    	if (!isOpened) {
        	$("#checkCount").removeAttr('readonly');
     		$("#productCode").removeAttr('readonly');
     		$("#buyStockCode").removeAttr('readonly');

     		// 设置焦点
    		if (typeof($("#buyStockCode").attr("disabled")) == "undefined") {
    			$("#buyStockCode").val('');
    			$("#buyStockCode").focus();
    		} else {
    			$("#productCode").val('');
    			$("#productCode").focus();
    		}
     		
     		allOpen(); 
     		$("#restart").text("关闭");
     		$("#restart").css("background", "green");
     		isOpened = true;
    	} else {    		
    		stopLine();
    	}
    }
    
    function stopLine() {
    	allClose();
		$("#restart").text("开启");
		$("#restart").css("background", "red");
		isOpened = false;
    }
    
    // 释放资源
    function disposeRes() {
    	if(plugin == null)
    		return;
    	plugin.DisposeRes();
    }
    
	</script>
</body>
</html>
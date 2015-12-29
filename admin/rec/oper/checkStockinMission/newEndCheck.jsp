<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@page import="adultadmin.util.Constants" %>
<!DOCTYPE html>
<%
	String soundSrcSJYC = Constants.WARE_SOUND_SJYC;
%>
<html>
<head>
	<meta charset="UTF-8">
	<title>质检结果复录</title>
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
	</script>
</head>
<body onload="$('#buyStockCode').removeAttr('disabled');$('#buyStockCode').val('');$('#buyStockCode').focus();$('#productCode').attr('disabled', true);">
	<center>
		<h3>质检结果复录录入</h3>
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
					<br>
					</td>
					<td align="left">
					</td>
				</tr>
				<tr >
					<td colspan="3">
					
					扫描顺序：开始码(KAI)，商品条码进行计数，终止码(CMDYJ)<br/>
					1)按照标准装箱数量入库并装箱，生成若干张已审核的采购入库单<br/>
					2)每张入库单生成并打印一张对应的装箱单
					</td>
				</tr>
			</table>
			<div id="changeBinning" style="display:none;">
				商品编号: <span id="binningProductCode"></span>
						标准装箱量:<input id="binn" name="binn" class="easyui-numberbox" required value="" style="width:40px">
						<a class="easyui-linkbutton" href="javascript:void(0)" onclick="changeBinning();">确定</a>
						<span id="changeResult" name="changeResult"></span>
			</div>
			<input type="hidden" name="hideBuyStockCode" id="hideBuyStockCode" value="" />
			<input type="hidden" name="hideProductCode" id="hideProductCode" value="" />
			<input type="hidden" name="hideProductId" id="hideProductId" value="" />
			<input type="hidden" name="hideCheckStockinMissionId" id="hideCheckStockinMissionId" value="" />
			<input type="hidden" name="hideCheckStockinMissionBatchId" id="hideCheckStockinMissionBatchId" value="" />
			<input type="hidden" name="binning" id="binning" value=""/>
			<br/>
		</div>
			<div id="tipInfo" class="easyui-panel" style="width:500px;height:200px;background-color:green;">
			
			</div>
	</center>
		<input type="hidden" id="checkStockinMissionMark" value="0"  />
	<script type="text/javascript">
		function changeBinning(){
			var binning = $("#binn").val();
			var binn = $("#binning").val();//初始装箱量
			//alert(binning);
			var productId=$("#hideProductId").val();
			$.post('<%= request.getContextPath()%>/admin/changeBinning2.mmx',{binning:binning,productId:productId},function(result){
				if(result=="-1"){
					$.messager.alert("错误","您还未登录,操作失败!","error");
				}else if(result=="1"){
					$("#changeResult").html("<font color='red' size='-1'>修改标准装箱量成功!</font>");
					$("#binning").val(binning);
				}			
				else{
					Play('<%= soundSrcSJYC%>');
					$("#changeResult").html("<font color='red' size='-1'>修改标准装箱量失败!</font>");
					$("#binn").val(binn);
				}
					
			});
		}
	
		String.prototype.trim=function(){return this.replace(/(^\s*)|(\s*$)/g,"");}
		
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
	   				var sht = $("#tipInfo").html();
					sht = "<font size='5' color='white'>"+"第"+tempCountError+"商品编号格式错误！"+"</font><br/>" + sht;
					$("#tipInfo").html(sht);
							Play('<%= soundSrcSJYC%>');
	   				$.messager.alert("提示", "第"+ tempCountError +"个商品编号格式错误！",'error', function () {
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
				"<%= request.getContextPath()%>/admin/check/checkProductCodeAndGetFirstCheckStockinInfo.mmx",
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
			             $("#binning").val(data['binning']);
			             $("#binn").val(data['binning']);
			             var checkCount = $("#checkCount").val();
			             var iCheckCount = parseInt(checkCount,10);
			             iCheckCount ++;
			             $("#checkCount").val(iCheckCount);
			             $("#changeBinning").css('display', 'block');
			             $("#binningProductCode").html(productCode);
			             $("#tipInfo").css("background","green");
	                 } else if ( data['status'] == "fail" ) {
	                 	$("#tipInfo").css("background","#FF0000");
	                 	var sht = $("#tipInfo").html();
						sht = "<font size='5' color='white'>"+data['tip']+"</font><br/>" + sht;
						$("#tipInfo").html(sht);
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
					var hProductCode = $("#hideProductCode").val();
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
						$.messager.alert("提示", "当前没有对应的质检任务单，不可以提交复录结果！",'error', function () {
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
					$.post( "<%= request.getContextPath()%>/admin/check/newSecondCheckAutomatic.mmx",
					{
							checkStockinMissionId:hCheckStockinMissionId,
							productId:hProductId,
							secondCheckCount:checkCount2,
							checkStockinMissionBatchId:hCheckStockinMissionBatchId,
							buyStockCode:hBuyStockCode,
							productCode:hProductCode
					},
					function (data,textStatus) {
						if ( data['status'] == "success" ) {
							$("#tipInfo").css("background","green");
							var missionId = $("#hideCheckStockinMissionId").val();
							var batchId = $("#hideCheckStockinMissionBatchId").val();
							var msg3 = data['codelist'].split(",");
				    	   	var showmessage = "";
			    	   	  for(var i=0;i<msg3.length-1;i++){
			    	   	  		 showmessage = showmessage+"<font size='5' color='black'>"+"入库单为："+ msg3[i].split("-")[0] + "  " + "对应的装箱单为：" + msg3[i].split("-")[1]+"</font><br>";
							 }
							var sht = $("#tipInfo").html();
							sht = showmessage + sht;
							$("#tipInfo").html(sht);
							 //入库成功才进行判断
							 $.post('<%= request.getContextPath()%>/admin/validComplete.mmx',{missionId:missionId},function(msg1){
							    if(msg1=="1"){
							    	autocomplete(hBuyStockCode);
							 	}else if(msg1=="2"){
							 		/*
							 		var t = window.showModalDialog("cargo/valComplete.jsp","请确认","dialogWidth:200px;status=no;dialogHeight:150px;help=no;location=no;resizable");							 	
									if(t){
									    	
									 }else{
										 */
									   autocomplete(hBuyStockCode);
									   //整个复录已经完成对页面缓存数据清零
									   resetInfos();
									   /*
									 }		
									*/
								}
							 });
					      var msg4 = data['codelist'].split(",");;
				    	  var codelist = "";
			    	   	  for(var i=0;i<msg4.length-1;i++){
			    	   	  	codelist = codelist+msg4[i].split("-")[1]+",";			    	   	  		
							 }
							window.open("<%= request.getContextPath()%>/admin/printCartonningInfo3.mmx?code="+codelist+"&missionId="+missionId+"&batchId="+batchId,'打印装箱单','height=100,width=400,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
							//整个复录已经完成对页面缓存数据清零
							resetInfos();
							return false;
						} else {
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
			$("#tipInfo").css("background","green");
			$("#buyStockInfo").html("");
			$("#productCode").focus();
			$("#changeBinning").css('display', 'none');
			$("#binning").val("0");
			$("#binn").val("0");
			$("#binningProductCode").html("");
			$("#productCode").val("");
			$("#productCode").focus();
		}
		
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
function validComplete(missionid){
	var batchId=$("#batchId").val();
	var missionId=$("#missionId").val();
	$.post('validComplete2.mmx',{missionId:missionid},function(msg){
		if(msg=="0"){
		    		//完成采购
		    		confirmCom(batchId,missionId);
		    }
	});
}
function autocomplete(obj){
	$.post('autocompleteorder2.mmx',{buyStockinCode:obj},function(result){
		if(result=="-1"){
			Play('<%= soundSrcSJYC%>');
			alert("查询采购单时发生异常，请与管理员联系!");
		}else if(result=="0"){
			Play('<%= soundSrcSJYC%>');
			alert("自动完成订单失败，请与管理员联系!");
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
		}
	</script>
</body>
</html>
<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>质检结果初录</title>
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/js/easyui/themes/icon.css">
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
$(document).ready(function(){   
$("#productCode").focus();
  $("#productCode").keydown(function(e){  
    if(e.keyCode==13){   
    	var csmcode = $("#csmcode").text();
        var productCode = $("#productCode").val();
        if(productCode=="KAI"){//扫描开始码
        	productCode = $("#product_Code").val();
        	var buyStockinCode = $("#buyStockinCode").val();
        	var batchId=$("#batchId").val();
		    	var missionId=$("#missionId").val();
        	window.location.href="beginCheck2.mmx?productCode="+productCode+"&batchId="+batchId+"&missionId="+missionId+"&buyStockinCode="+buyStockinCode;
        }
        else if(productCode!="CMDYJ"&&productCode!="KAI")
    		validProduct(productCode,csmcode);
    	else{
		    	var productCount=parseInt($("#productCount").val(),10);
		    	
		    	var binning=parseInt($("#binning").val(),10);
		    	var batchId=$("#batchId").val();
		    	var missionId=$("#missionId").val();
		    	var product_Code=$("#product_Code").val();
		    	var buyStockinCode = $("#buyStockinCode").val();
		    	var currentCount = $("#currentCount").val();
	    		if(productCount==""){
	    			alert("扫描商品件数不能为空!");
	    			return;
	    		}
	    		if(isNaN(productCount)){
	    			alert("商品件数只能是数字!");
	    			return;
	    		}
	    		var product_Code=$("#product_Code").val();
	    		if(productCount==0){
	    			alert("请至少扫描一件商品!");
	    			return;
	    		}
	    		var batchId=$("#batchId").val();
	    		var secondStatus = $("#secondStatus").val();
	    		$("#productCode").val("");
	    		$.post('checkStatus2.mmx',{batchId:batchId,type:2},function(result){
	    		var rs = result.split(",");
	    		if(rs[0]=="0"){//先录入的是初录
		    		$.post('updateFristCheck2.mmx',{csmCode:csmcode,productCount:productCount,batchId:batchId,type:2},function(msg){
		    		    
		    			var msg1 = msg.split(",")[0];
		    			var msg2 = msg.split(",")[1];
						 if(msg1=="1"){
						 	 $("#result").html("初录成功!");
						 }else if(msg1=="-1"){
						 	if( msg2 == "0" ) {
						 		$.messager.alert("提示","采购已完成，不允许执行入库操作!");
						 	} else  if ( msg2 == "1" ) {
						 		$.messager.alert("提示","未找到质检入库信息！");
						 	} else  if ( msg2 == "2" ) {
						 		$.messager.alert("提示","质检入库任务为已完成，不可以继续录入！");
						 	} else if( msg2 == "3" )  {
						 		$.messager.alert("提示","质检入库任务为已删除，不可以继续录入！");
						 	} else {
						 		$.messager.alert("提示","系统异常！");
						 	}
						 }else{
						 	alert("初录失败!");
						 }
						 $("#productCode").val("");
						 $("#productCount").val(msg.split(",")[1]);
				    	$("#product_count").val(msg.split(",")[1]);		    		
		    		});
	    		}else{//入库操作
	    			$("#error").html("复录已提交!");
	    			$("#result").html("");
	    			/*var firstCount=parseInt(rs[1],10);//取复录的数据
	    			if(firstCount==productCount){
		    			if(productCount>=binning){
				    		$.post('secondCheck.mmx',{csmCode:csmcode,productCount:productCount,firstCount:firstCount,binning:binning,batchId:batchId,missionId:missionId,productCode:product_Code,buyStockinCode:buyStockinCode},
					    	   function(msg){
					    	   	var msg2 = msg.split(",")[0]; 
								if(msg2=="入库成功!"){
				    	   	var msg3 = msg.split(",");
				    	   	var showmessage = "";
			    	   	  	for(var i=2;i<msg3.length-1;i++){
			    	   	  		 showmessage = showmessage+"入库单为："+ msg3[i].split("-")[0] + "  " + "对应的装箱单为：" + msg3[i].split("-")[1]+"<br>";
							 }
							 $("#result").html(msg2+"<br>"+showmessage);
					    }else{
					    	alert(msg2);
					    	}
									 $.post('validComplete.mmx',{missionId:missionId},function(msg1){
									 	if(msg1=="1"){
									 	var t = window.showModalDialog("cargo/valComplete.jsp","请确认","dialogWidth:200px;dialogHeight:150px;status=no;help=no;location=no");
											    	if(t){
											    	
											    	}else{
											    		confirmCom(batchId,missionId);
											    	}								    	
											    }
									 });	
									  $("#productCode").val("");					
									 $("#productCount").val(msg.split(",")[1]);
							    	$("#product_count").val(msg.split(",")[1]);
							    	$("#secondStatus").val(0);	
					    	   });
				    	   }else{
				    	   	alert("本次扫描数量不足一箱，请点击\"入库并打印装箱单\"进行手动提交!");
				    	   }
				}else{
				  alert("两次计数不一致!");
				}*/
	    		}
	    		});
		}
    }
  });     
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
	    			alert("扫描商品件数不能为空!");
	    			return;
	    		}
	    		if(isNaN(productCount)){
	    			alert("商品件数只能是数字!");
	    			return;
	    		}
	    		var product_Code=$("#product_Code").val();
	    		if(productCount=="0"||productCount==0){
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
							 		$.messager.alert("提示","采购已完成，不允许执行入库操作!");
							 	} else  if ( msg2 == "1" ) {
							 		$.messager.alert("提示","未找到质检入库信息！");
							 	} else  if ( msg2 == "2" ) {
							 		$.messager.alert("提示","质检入库任务为已完成，不可以继续录入！");
							 	} else if( msg2 == "3" )  {
							 		$.messager.alert("提示","质检入库任务为已删除，不可以继续录入！");
							 	} else {
							 		$.messager.alert("提示","系统异常！");
							 	}
						}else{
						 	alert("初录失败!");
						 }
						
						 $("#productCount").val(msg.split(",")[1]);
				    	$("#product_count").val(msg.split(",")[1]);		    		
		    		});
	    		}else{//入库操作
	    			$("#error").html("复录已提交!");
	    			$("#result").html("");
	    			/*productCount = parseInt(productCount,10);
	    			var firstCount=parseInt(rs[1],10);//取复录的数据
	    			$("#productCode").val("");
	    			if(firstCount==productCount){		    			
				    		$.post('noAutoCheck.mmx',{csmCode:csmcode,productCount:productCount,firstCount:firstCount,binning:binning,batchId:batchId,missionId:missionId,productCode:product_Code,buyStockinCode:buyStockinCode},
					    	   function(msg){
					    	   	var msg2 = msg.split(",")[0]; 
								if(msg2=="入库成功!"){
						    	   	var msg3 = msg.split(",");
						    	   	var showmessage = "";
					    	   	  for(var i=2;i<msg3.length-1;i++){
					    	   	  		 showmessage = showmessage + "入库单为："+ msg3[i].split("-")[0] + "  " + "对应的装箱单为：" + msg3[i].split("-")[1]+"<br>";
									 }
									 $("#result").html(msg2+"<br>"+showmessage);
							    }else{
							    	alert(msg2);
							    	}
									 $.post('validComplete.mmx',{missionId:missionId},function(msg1){
									 	if(msg1=="1"){
									 	var t = window.showModalDialog("cargo/valComplete.jsp","请确认","dialogWidth:200px;dialogHeight:150px;status=no;help=no;location=no");
											    	if(t){
											    	
											    	}else{
											    		confirmCom(batchId,missionId);
											    	}								    	
											    }
									 });
									 $("#productCode").val("");						
									 $("#productCount").val(msg.split(",")[1]);
							    	$("#product_count").val(msg.split(",")[1]);
							    	$("#secondStatus").val(0);	
					    	   });
				    	   }else{
				    	   	alert("两次计数不一致!");
				    	   }*/				
	    		}
	    		});
	    		
	    	   
		}

function validProduct(obj,csmcode){
	if(obj==""){
	    		alert("商品编号不能为空或输入终止码!");
	    		return;
	    	}
	$.post('validProduct2.mmx',{productCode:obj,csmCode:csmcode},function(msg){
		var productId=$("#productId").val();
	    		if(msg=="-1")
			     	//alert('商品不存在!');
			     	$("#error").html(obj+"不存在!<br>"+$("#error").html());
			    else if(msg!="-1"&&msg!=productId){
			    	//alert("第"+(parseInt($("#product_count").val(),10)+1)+"个商品不属于该任务!");
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
jQuery.post(
					"judgeExistUnqualifiedInfo2.mmx",{missionId:missionId},
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
	    if(result=="-1")
	    	alert("未找到对应的数据!");
	    else if(result=="error")
	   		alert("初始化时出现异常，请联系管理员!");
	   	else
			$("#productCount").val(result);
	});
}
	</script>
</head>
<body>
<center>
	<h2>质检结果初录</h2>
	<div class="demo-info">
		<div class="demo-tip icon-tip"></div>
		
	</div>
	<div style="margin:10px 0;"></div>
	<div class="easyui-panel" title="" style="width:400px">
		<div style="padding:10px 0 10px 60px">
	    <form id="ff" method="post" action="updateFristCheck2.mmx">
	    	<table>
	    		<tr>
	    			<td><h3>预计单号:</h3></td>
	    			<td><h3><span id="csmcode" name="csmcode">${buyStockinCode}</span></h3></td>
	    		</tr>
	    		<tr>
	    			<td>产品编号/商品条码:</td>
	    			<td><input class="easyui-validatebox" type="text" name="productCode" id="productCode" data-options="required:true"></input>
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>数量：</td>
	    			<td><input class="easyui-validatebox" type="text"  name="productCount" id="productCount" value="${firstStatus=='1'?firstCount:leftCount}" data-options="required:true,validType:'sign_integer'"></input>
	    				
	    			</td>
	    		</tr>
	    		<tr>
	    			<td>质检结果:</td>
	    			<td>
	    				<select class="easyui-combobox" name="result"><option value="">合格</option></select>
	    				    				
	    			</td>
	    		</tr>
	    	</table>
	    </form>
	    </div>
	    <div style="text-align:center;padding:5px">
	    	${primission=="1"?'<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()">入库并打印装箱单</a>':""}
	    	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="clearForm()">取消</a>
	    	
	    </div>
	</div>
						<input type="hidden" name="product_count" id="product_count" value="0"/>
						<input type="hidden" name="batchId" id="batchId" value="${batchId}"/>
	    				<input type="hidden" name="missionId" id="missionId" value="${missionId}"/>
	    				<input type="hidden" name="product_Code" id="product_Code" value="${productCode}"/>
	    				<input type="hidden" name="binning" id="binning" value="${binning}"/>
	    				<input type="hidden" name="firstCount" id="firstCount" value="${firstCount}"/>	    				
	    				<input type="hidden" name="buyStockinCode" id="buyStockinCode" value="${buyStockinCode}"/>
	    				<input type="hidden" name="currentCount" id="currentCount" value="${currentCount}"/>
	    				<input type="hidden" name="secondStatus" id="secondStatus" value="${secondStatus}"/>
	    				<input type="hidden" name="secondCount" id="secondCount" value="${secondCount}"/>
	    				<input type="hidden" name="firstStatus" id="firstStatus" value="${firstStatus}"/>
	    				<input type="hidden" name="productId" id="productId" value="${productId}"/>
	    				<br>
	 <div class="easyui-panel" style="width:400px ">
		<div class="demo-tip icon-tip"></div>
		扫描顺序：开始码(KAI),商品条码,终止码(CMDYJ);并进行计数
			</div>
			<br>
			<div class="demo-info">
		<div class="demo-tip icon-tip"></div>
			<div id="error" name="error"></div>
			<div id="result" name="result"></div>
			</div>
	<script>
		
		function clearForm(){
			$('#ff').form('clear');
			$("#error").html("");
		}
	</script>
	</center>
</body>
</html>
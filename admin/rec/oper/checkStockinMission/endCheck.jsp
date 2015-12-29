<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>质检结果复录</title>
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
        	window.location.href="endCheck2.mmx?productCode="+productCode+"&batchId="+batchId+"&missionId="+missionId+"&buyStockinCode="+buyStockinCode;
        }
        else  if(productCode!="CMDYJ" && productCode!="KAI")
    		validProduct(productCode,csmcode);
    	else{
    	
    	if($("#productCount").val()==""){
	    		alert("复录商品件数不能为空!");
	    		return;
	    	}
	    	
	    	var productCount=parseInt($("#productCount").val(),10);
	    	if(isNaN(productCount)){
	    			alert("商品件数只能是数字!");
	    			return;
	    		}
	    	var firstCount=parseInt($("#firstCount").val(),10);
	    	var binning=parseInt($("#binning").val(),10);
	    	var batchId=$("#batchId").val();
	    	var missionId=$("#missionId").val();
	    	var product_Code=$("#product_Code").val();
	    	var buyStockinCode = $("#buyStockinCode").val();
	    	var currentCount = $("#currentCount").val();//本次录入数
	    	var firstStatus = $("#firstStatus").val();//初录状态
	    	
	    	if(productCount==0){
	    			alert("请至少扫描一件商品!");
	    			return;
	    		}
	    		$("#productCode").val("");
	    	$.post('checkStatus2.mmx',{batchId:batchId,type:1},function(result){
	    	var rs = result.split(",");
	    		if(rs[0]=="1"){
	    			firstCount = parseInt(rs[1],10);
			    	if(firstCount==productCount){
				    	if(productCount>=binning){
				    	   $.post('secondCheck2.mmx',{csmCode:csmcode,productCount:productCount,firstCount:firstCount,binning:binning,batchId:batchId,missionId:missionId,productCode:product_Code,buyStockinCode:buyStockinCode},
				    	   function(msg){
				    	   	var msg2 = msg.split(",")[0]; 
				    	   	if(msg2=="入库成功!"){
					    	   	var msg3 = msg.split(",");
					    	   	var showmessage = "";
				    	   	  for(var i=2;i<msg3.length-1;i++){			    	   	  		
				    	   	  		 showmessage =showmessage + "入库单为："+ msg3[i].split("-")[0] + "  " + "对应的装箱单为：" + msg3[i].split("-")[1]+"<br>";
								 }
								 $("#result").html(msg2+"<br>"+showmessage);
								 //入库成功才进行判断
								 $.post('validComplete2.mmx',{missionId:missionId},function(msg1){
							    if(msg1=="1"){
							    	autocomplete(buyStockinCode);
							 	}else if(msg1=="2"){
							 		var t = window.showModalDialog("<%= request.getContextPath() %>/admin/cargo/valComplete.jsp","请确认","dialogWidth:200px;status=no;dialogHeight:150px;help=no;location=no;resizable");							 	
									    	if(t){
									    	
									    	}else{
									    		autocomplete(buyStockinCode);
									    		window.location.href="editCheckStockinMission2.mmx?missionId="+missionId;
									    	}								    	
									    }
							 });
								 $("#productCount").val(msg.split(",")[1]);
						    	$("#product_count").val(msg.split(",")[1]);	
						    	$("#fristStatus").val(0);
						    		var msg4 = msg.split(",");
					    	   	var codelist = "";
				    	   	  for(var i=2;i<msg4.length-1;i++){
				    	   	  	codelist = codelist+msg4[i].split("-")[1]+",";			    	   	  		
				    	   	  		 //window.location.href="printCartonningInfo3.mmx?code="+msg4[i].split("-")[1]+"&missionId="+missionId+"&batchId="+batchId;
								 }
								 window.open("printCartonningInfo3.mmx?code="+codelist+"&missionId="+missionId+"&batchId="+batchId,'打印装箱单','height=100,width=400,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
					    	
						    }else{
						    	alert(msg2);
						    	}
				    	   });		    	   
						}else{
							alert("本次扫描数量不足一箱，请点击\"入库并打印装箱单\"进行手动提交!");
						}				
					}else{
					  alert("两次计数不一致!");
					}
				}else{
					$("#error").html("还未进行初录!");
					$("#result").html("");
					/*$.post('updateFristCheck.mmx',{csmCode:csmcode,productCount:productCount,batchId:batchId,type:1},function(msg){
			    			var msg1 = msg.split(",")[0];
							 if(msg1=="1"){
							 	 $("#result").html("初录成功!");
							 }else{
							 	alert("初录失败!");
							 }
							
							 $("#productCount").val(msg.split(",")[1]);
					    	$("#product_count").val(msg.split(",")[1]);
					    			    		
			    		});*/
				}
		    	});
	    	
		}
    }
  });     
});  
//修改标准装箱量
function changeBinning(){
	var binning = $("#binn").val();
	var binn = $("#binning").val();//初始装箱量
	//alert(binning);
	var productId=$("#productId").val();
	$.post('<%= request.getContextPath()%>/admin/changeBinning2.mmx',{binning:binning,productId:productId},function(result){
		if(result=="-1"){
			$.messager.alert("错误","您还未登录,操作失败!","error");
		}else if(result=="1"){
			$("#changeResult").html("<font color='red' size='-1'>修改标准装箱量成功!</font>");
			$("#binning").val(binning);
		}			
		else{
			$("#changeResult").html("<font color='red' size='-1'>修改标准装箱量失败!</font>");
			$("#binn").val(binn);
		}
			
	});
}
  
//点击入库按钮
function submitForm(){
			var csmcode = $("#csmcode").text();
        var productCode = $("#productCode").val();
    		//validProduct(productCode,csmcode);
    		if($("#productCount").val()==""){
	    		alert("复录商品件数不能为空!");
	    		return;
	    	}
	    	
	    	var productCount=parseInt($("#productCount").val(),10);
	    	var firstCount=parseInt($("#firstCount").val(),10);
	    	var binning=$("#binning").val();
	    	var batchId=$("#batchId").val();
	    	var missionId=$("#missionId").val();
	    	var product_Code=$("#product_Code").val();
	    	var buyStockinCode = $("#buyStockinCode").val();
	    	var currentCount = $("#currentCount").val();
	    	var secondStatus = $("#firstStatus").val();
	    	if(isNaN(productCount)){
	    			alert("商品件数只能是数字!");
	    			return;
	    		}
	    		if(productCount==0){
	    			alert("请至少扫描一件商品!");
	    			return;
	    		}
	    		$("#productCode").val("");
	    		$.post('checkStatus2.mmx',{batchId:batchId,type:1},function(result){
	    		var rs = result.split(",");
	    			if(rs[0]=="1"){
		    		firstCount = parseInt(rs[1],10);
		    		if(firstCount==productCount){
		    		$.post('noAutoCheck2.mmx',{csmCode:csmcode,productCount:productCount,firstCount:firstCount,binning:binning,batchId:batchId,missionId:missionId,productCode:product_Code,buyStockinCode:buyStockinCode},
			    	   function(msg){
			    	   	var msg2 = msg.split(",")[0]; 
						if(msg2=="入库成功!"){
				    	   	var msg3 = msg.split(",");
				    	   	var showmessage = "";
			    	   	  for(var i=2;i<msg3.length-1;i++){
			    	   	  		 showmessage = showmessage+"入库单为："+ msg3[i].split("-")[0] + "  " + "对应的装箱单为：" + msg3[i].split("-")[1]+"<br>";
							 }
							 $("#result").html(msg2+"<br>"+showmessage);
							 //入库成功才进行判断
							 $.post('validComplete2.mmx',{missionId:missionId},function(msg1){
							    if(msg1=="1"){
							    	autocomplete(buyStockinCode);
							 	}else if(msg1=="2"){
							 		var t = window.showModalDialog("<%= request.getContextPath() %>/admin/cargo/valComplete.jsp","请确认","dialogWidth:200px;status=no;dialogHeight:150px;help=no;location=no;resizable");							 	
									    	if(t){
									    	
									    	}else{
									    		autocomplete(buyStockinCode);
									    		window.location.href="editCheckStockinMission2.mmx?missionId="+missionId;
									    	}								    	
									    }
							 });
							 $("#productCount").val(msg.split(",")[1]);
					    	$("#product_count").val(msg.split(",")[1]);	
					    	$("#fristStatus").val(0);
					    		var msg4 = msg.split(",");
				    	   	var codelist = "";
			    	   	  for(var i=2;i<msg4.length-1;i++){
			    	   	  	codelist = codelist+msg4[i].split("-")[1]+",";			    	   	  		
			    	   	  		 //window.location.href="printCartonningInfo3.mmx?code="+msg4[i].split("-")[1]+"&missionId="+missionId+"&batchId="+batchId;
							 }
							 window.open("printCartonningInfo3.mmx?code="+codelist+"&missionId="+missionId+"&batchId="+batchId,'打印装箱单','height=100,width=400,toolbar=no,menubar=no,scrollbars=no, resizable=no,location=no, status=no');
					    	
					    }else{
					    	alert(msg2);
					    	}
			    	   });
						}else{
						  alert("两次计数不一致!");
						}
					}else{//执行初录
						$("#error").html("还未进行初录!");
						$("#result").html("");
						/*$.post('updateFristCheck.mmx',{csmCode:csmcode,productCount:productCount,batchId:batchId,type:1},function(msg){
		    			var msg1 = msg.split(",")[0];
						 if(msg1=="1"){
						 	 $("#result").html("录入成功!");
						 }else{
						 	alert("录入失败!");
						 }
						
						 $("#productCount").val(msg.split(",")[1]);
				    	$("#product_count").val(msg.split(",")[1]);		    		
		    		});*/
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
			     	$("#error").html(obj+"不存在!<br>"+$("#error").html());
			    else if(msg!="-1"&&msg!=productId){
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

function autocomplete(obj){
	$.post('${pageContext.request.contextPath}/CheckStockinMissionControllerNew/operautocompleteorder2.mmx',{buyStockinCode:obj},function(result){
		if(result=="-1"){
			alert("查询采购单时发生异常，请与管理员联系!");
		}else if(result=="0"){
			alert("自动完成订单失败，请与管理员联系!");
		}
	});
}
	</script>
</head>
<body>
<center>
	<h2>质检结果复录</h2>
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
	    			<td><input class="easyui-validatebox" type="text" name="productCount" id="productCount" value="${secondStatus=='1'?secondCount:leftCount}"  data-options="required:true,validType:'sign_integer'"></input>
	    			
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
	    <input type="hidden" name="product_count" id="product_count" value="0"/>
	    				<input type="hidden" name="batchId" id="batchId" value="${batchId}"/>
	    				<input type="hidden" name="missionId" id="missionId" value="${missionId}"/>
	    				<input type="hidden" name="binning" id="binning" value="${binning}"/>
	    				<input type="hidden" name="firstCount" id="firstCount" value="${firstCount}"/>
	    				<input type="hidden" name="product_Code" id="product_Code" value="${productCode}"/>
	    				<input type="hidden" name="buyStockinCode" id="buyStockinCode" value="${buyStockinCode}"/>
	    				<input type="hidden" name="currentCount" id="currentCount" value="${currentCount}"/>
	    				<input type="hidden" name="firstStatus" id="firstStatus" value="${firstStatus}"/>
	    				<input type="hidden" name="secondCount" id="secondCount" value="${secondCount}"/>
	    				<input type="hidden" name="productId" id="productId" value="${productId}"/>
	    <div style="text-align:center;padding:5px">
	    	${primission=="1"?'<a href="javascript:void(0)" class="easyui-linkbutton" onclick="submitForm()">入库并打印装箱单</a>':""}
	    	<a href="javascript:void(0)" class="easyui-linkbutton" onclick="clearForm()">取消</a>
	    	
	    </div>
	</div>
		<br>
		<div class="easyui-panel" style="width:400px ">
		<div class="demo-tip icon-tip"></div>
		
		商品编号:${productCode }  标准装箱量:<input id="binn" name="binn" class="easyui-numberbox" required value="${binning }" style="width:40px"><a class="easyui-linkbutton" href="javascript:void(0)" onclick="changeBinning();">确定</a>
		<span id="changeResult" name="changeResult"></span>
			</div>
			<br>
	 	<div class="easyui-panel" style="width:400px ">
		<div class="demo-tip icon-tip"></div>
		
		扫描顺序：开始码(KAI),商品条码,终止码(CMDYJ);并进行计数<br>
		1)按照标准装箱数量入库并装箱，生成若干张已审核的采购入库单,不足一箱的未入库余量会计入下次计数的初始值<br>
		2)每张入库单生成并打印一张对应的装箱单<br>
		3)不足标准装箱量时如确认需要入库装箱，请手动点击"入库并打印装箱单"按钮
		
			</div>
			<br>
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
//  js 包括原品返回  售后换货   售后维修

//退货入库  并页面显示 跳转到财务..
function replaceInAndShowForward(){
	$.ajax({
		type:"post",
		url:"afterSales.do?method=entryStorage",
		dataType:'json',
		data:$('#entryStorageForm').serialize(),
		success:function(json){ //退货入库售后库
			var msg=json.msg;
			if(msg=="success"){
				dialogClose('returnInStock');
				showConfigReplaceDiv();
			}else if(msg=="fail"){
				alert(json.context);
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
	});
}

function showConfigReplaceDiv(){
	$('#commonDivHtml').html('您需要继续更换商品吗？<br/> <br/><br/> 当前售后单状态：换货中');
	$("#commonDiv").dialog({  
     	modal: true,
     	bgiframe: true,
     	width:330,
     	height:300,
     	title:'商品已成功换货入库',
     	buttons: {
	       "是": function(){
                $(this).dialog('close');
                document.location.href=$('#getContextPaths').val()+'/admin/afterSales.do?method=replaceProduct&id='+$('#afterSaleId').val();
        	},
           	"否": function() {
          	  	document.location.href=document.location.href;			 
    		 }
    	},
    	dialogClass: 'alert',
    	close:function(event, ui) {
	       	if(event.clientY !='undefined' && event.clientY>0){
	       		document.location.href=document.location.href;
	       	} 
	    }
     });
}

function nifferAndNewOrder(id,backType){
	 var parma='id='+id+'&backType='+backType;
	 $.ajax({
	 	type:'post',
	 	url:'afterSales.do?method=nifferAndNewOrder',
	 	dataType:'json',
	 	data:parma,
	 	success:function(json){
	 		if(json!=null){
		 		var msg=json.msg;
		 		if(msg=='success'){
		 			submitTypeForm(16)
		 		}else{
		 			alert(json.context);
		 		}
		 	}else{
		 		alert('发生异常');
		 	}
	 	},
	 	error:function(XMLHttpRequest,textStatus,errorThrown){
	 		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	 	}
	 });
}

function submitTypeForm(typeValue){
	if($.trim($('#ccACustomerName').val())==''){
		alert('请输入用户名!');
		return;
	}
	if($.trim($('#ccACustomerPhone').val())==''){
		alert('请输电话!');
		return;
	}
	if(!isTelephone($('#ccACustomerPhone').val())){
		alert('请输入正确的电话号码！');
		return;
	}
	$('#customerName').val($('#ccACustomerName').val());
	$('#customerPhone').val($('#ccACustomerPhone').val());
	
	var sheng = document.getElementById("sheng");
	var shi = document.getElementById("shi");
	var qu = document.getElementById("qu");
	var street = document.getElementById("streetId");
	var road = document.getElementById("road").value;
	var shengSubmit = document.getElementById("shengSubmit").value;
	var shiSubmit = document.getElementById("shiSubmit").value;
	var quSubmit = document.getElementById("quSubmit").value;
	var streetIdSubmit = document.getElementById("streetIdSubmit").value;
	var roadSubmit = document.getElementById("roadSubmit").value;
	var addre='';
	if(sheng.selectedIndex!=0){
		addre = sheng.options[sheng.selectedIndex].text + shi.options[shi.selectedIndex].text;
		if(qu.options != null && qu.options.length > 0){
			addre += qu.options[qu.selectedIndex].text;
			document.getElementById("shengSubmit").value = sheng.options[sheng.selectedIndex].value;
			document.getElementById("shiSubmit").value = shi.options[shi.selectedIndex].value;
			document.getElementById("quSubmit").value = qu.options[qu.selectedIndex].value;
		}
		if(street.options!=null && street.options.length>0){
			addre+=street.options[street.selectedIndex].text;
			document.getElementById("streetIdSubmit").value = street.options[street.selectedIndex].value;
		}
		$('#customerAddress').val(addre+road);
		document.getElementById("roadSubmit").value = road;
	}else{
		$('#customerAddress').val($('#ccACustomerAddress').val());
	}
	
	$('#typeId').val(typeValue);
  	$('#typForm').submit();
}

function showLoading(){
	$('#loadingDiv').show();
}
function hideLoading(){
	$('#loadingDiv').hide();
}

function showReceivables(type){//先是确认收款信息
	
	$("#receivablesDiv").dialog({  
	   	modal: true,
	    bgiframe: true,
	    width:400,
	    height:420,
	    bgiframe: true,
	    resizable: false,
	    buttons: {
		  "确定": function() {
	        	//$(this).dialog('close');
	        	
	        	if($('#rdAddCreateTime').val()==''){
	        		alert('请输入汇款日期');
	        		return ;
	        	}
	        	if($('#rdBankName').val()==''){
	        		alert('请选择银行');
	        		return ;
	        	}
 				$.ajax({
				 	type:'post',
				 	url:'afterSales.do?method=addReceivables',
				 	dataType:'json',
				 	data:$('#receivablesForm').serialize(),
				 	success:function(json){
				 		if(json!=null){
					 		var msg=json.msg;
					 		if(msg=='success'){
					 			dialogClose('receivablesDiv');
					 			if(type==16){
					 				$('#commonDivHtml').html('您确认财务已经收到钱了吗？<br/><br/>确认后，系统将生成S订单，由物流部直接发货，'+
					 					'并且收货单状态将变成“换货发货中”！');
					 			}else if(type==406){
					 				$('#commonDivHtml').html('您确定将此售后单进行收款确认吗？<br/><br/>确认后该售货单状态将变更为“原品待发货”状态！');
					 			}else if(type==466){ 
					 				$('#commonDivHtml').html('您确定将此售后单进行收款确认吗？<br/><br/><font color="red">确定后该售后单状态将变更为“售后维修”状态！</font>');
					 			}else if(type==9){
					 				$('#commonDivHtml').html('您确定将此售后单进行收款确认吗？<br/><br/><font color="red">确定后该售后单状态将变更为“厂商维修”状态！</font>');
					 			}
					 			$("#commonDiv").dialog({  
								   	modal: true,
								    bgiframe: true,
								    width:300,
								    height:400,
								    title:'收款确认',
								    buttons: {
									  "确定": function() {
									  	   $('#typeId').val(type);
	          							   $('#typForm').submit();
									  },
									  "取消":function(){
									  	   $(this).dialog('close');
									  }
									},
									dialogClass: 'alert'
								});	 
					 		}else{
					 			alert(json.context);
					 		}
					 	}else{
					 		alert('发生异常');
					 	}
				 	},
				 	error:function(XMLHttpRequest,textStatus,errorThrown){
				 		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
				 	}
				 });
	        },
	        "取消": function() {
	           		$(this).dialog('close');
	         	}
	    	 } ,
	   	dialogClass: 'alert'
	 });
}
///  原品返回 js 存放地方........

function orignalCostList(type){//原品返回差价 费用单登记
	showOrignalCost('Aoc');
	$("#addOrignalCost").dialog({  
     	modal: true,
     	bgiframe: true,
     	width:550,
     	height:300,
    	dialogClass: 'alert'
     });
}

function addOrignalCost(){
	if($('#returnResAoc').val()==''){
		alert('请选择原品返回原因');
		return ;
	}
	if($('#frankingAoc').val()!='' && !floatExec($('#frankingAoc').val()) ){
		alert('请输入正确的金额');
		$('#fankingAoc').focus();
		return ;
	}
	if($('#shippingAoc').val()!='' && !floatExec($('#shippingAoc').val()) ){
		alert('请输入正确的金额');
		$('#shippingAoc').focus();
		return ;
	}
	if($('#frayAoc').val()!='' && !floatExec($('#frayAoc').val()) ){
		alert('请输入正确的金额');
		$('#frayAoc').focus();
		return ;
	}
	if($('#payFrankingAoc').val()!='' && !floatExec($('#payFrankingAoc').val()) ){
		alert('请输入正确的金额');
		$('#payFrankingAoc').focus();
		return ;
	}
	if($('#payShippingAoc').val()!='' && !floatExec($('#payShippingAoc').val()) ){
		alert('请输入正确的金额');
		$('#payShippingAoc').focus();
		return ;
	}
	if($('#payShippingAoc').val()!='' && !floatExec($('#payShippingAoc').val()) ){
		alert('请输入正确的金额');
		$('#payShippingAoc').focus();
		return ;
	}
	if($('#frankingAoc').val()!=''||$('#shippingAoc').val()!=''||$('#frayAoc').val()!=''
		||$('#payFrankingAoc').val()!=''||$('#payShippingAoc').val()!=''||$('#payFrayAoc').val()!=''){
		if($('#costTypeAoc').val()==''){
			alert('请选择差额用户支付方式');
			return ;
		}
	}
	$.ajax({
		type:"post",
		url:'afterSales.do?method=addOrignalCost',
		dataType:'json',
		data:$('#addOrignalCostForm').serialize(),
		success:function(json){
			var msg=json.msg;
			if(msg=='success'){
				var id= json.afterSaleCostId;
				var balance  = json.balance;
				if(id==0){
					alert('添加费用单信息发生异常。请检查网络连接');
				}else{
					$('#afterSaleCostIdAoc').val(id); 
				 	if(balance>0){
				 		$('#confrimDialogContext').html('<div style="float: left;">当前售后单状态：检测中</div> <div style="width:200px;clear:both" algin="center">用户需要向买卖宝支付： '+balance
				 		+'</div><br/><div style="float: left; ,margin:20px">您确定要将原品返回差价转财务确认吗？</div>');
				 		$(this).dialog('close');
						openConfrimDialog(407);
				 	}else if(balance<0){
				 		alert('应付金额不能大于应收金额！');
				 	}else{// 没有差额需要确认
				 		 $('#typeId').val(406);//状态变成原品待发货
		            	 $('#typForm').submit();
				 	}
				}
			}else if(msg=='fail'){
				var text=json.text;
				alert(text);
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
	});
}
function showOrignalCost(temp){//显示历史费用单 --原品返回
    var parms="afterSaleId="+$('#afterSaleId').val();
	$.ajax({
		type:"post",
		url:'afterSales.do?method=getAfterCost',
		dataType:'json',
		data:parms,
		success:function(json){
			if(json!=null){
				var bean = json.costBean;
				if(bean!=null){
 					$('#returnRes'+temp).val(bean.typeRes);
 					$('#costType'+temp).val(bean.costType);
 					
     				$('#franking'+temp).val(bean.franking!=0?bean.franking:'');
					$('#payFranking'+temp).val(bean.payFranking!=0?bean.payFranking:'');
					$('#shipping'+temp).val(bean.shipping!=0?bean.shipping:'');
					$('#payShipping'+temp).val(bean.payShipping!=0?bean.payShipping:'');
					$('#fray'+temp).val(bean.fray!=0?bean.fray:'');
					$('#payFray'+temp).val(bean.payFray!=0?bean.payFray:'');
					
					$('#afterSaleCostId'+temp).val(bean.id); 
				}
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert('显示历史费用单异常!');
	    }
	});
}

function addShippingPackage(type){
	$("#shippingPackageLogin").dialog({  
	   	modal: true,
	   	bgiframe: true,
	   	width:540,
	   	buttons: {
	      "确认原品发货": function() {
	      		var flag = validateAddPackage(2);
	      		 if(flag===false){
	      		 	return;
	      		 }else{
	      		 	$('#packageType').val(3);
	      		 	addPackageComment(type);
	      		 }
	      	 },
	         	"取消": function() {
	          		$(this).dialog('close');
	      	 	}
	  		 } ,
	  	dialogClass: 'alert'
	});
}

function addPackageComment(type){
	$.ajax({
		type:"post",
		url:'afterSales.do?method=addPackage',
		dataType:'text',
		data:$('#shippingPackageLoginForm').serialize(),
		success:function(text){
			if($.trim(text)=='fail'){
				alert('添加包裹单信息发生异常。请联系管理员');
			}else{
				$('#typeId').val(type);
				$('#typForm').submit();
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
	});	
}

//--- 售后维修包裹登记 　厂商维修包裹登记。　 packageType 4  5 
function addRepairePackage(packageType,type){
	$("#shippingPackageLogin").dialog({  
	   	modal: true,
	   	bgiframe: true,
	   	width:540,
	   	buttons: {
	      "确认返回用户": function() {
	      		var flag = validateAddPackage(2);
	      		 if(flag===false){
	      		 	return;
	      		 }else{
	      		 	$('#packageType').val(packageType);
	      		 	addPackageComment(type);
	      		 }
	      	 },
	       "取消": function() {
	          		$(this).dialog('close');
	      		}
	  	} ,
	  	dialogClass: 'alert'
	});
}

//  ----------------售后维修流程存放js地方--------------

function serviceCostList(type){// 费用单登记
	if(type==1){
		$('#adcSpanHtml').html('厂商');
		$('#adcSpan11').html('');
		$('#adcSpanSale').html('厂商');
	}else if(type==2){
		$('#adcSpanHtml').html('售后');
		$('#adcSpan12').html('');
		$('#adcSpanSale').html('售后');
	}
	$('#afterSaleStatus').val(type);
	$("#addServiceCost").dialog({  
     	modal: true,
     	bgiframe: true,
     	width:550,
     	height:300,
    	dialogClass: 'alert'
     });
     
     showOrignalCost('Asc');
}

function addServiceCost(){
 	
 	if($('#returnResAsc').val()==''){
 		alert('请选择维修费用原因');
 		$('#typeResAsc').focus();
		return ;
 	}
 	
	if($('#frankingAsc').val()!='' && !floatExec($('#frankingAsc').val()) ){
		alert('请输入正确的金额');
		$('#fankingAsc').focus();
		return ;
	}
	if($('#shippingAsc').val()!='' && !floatExec($('#shippingAsc').val()) ){
		alert('请输入正确的金额');
		$('#shippingAsc').focus();
		return ;
	}
	if($('#frayAsc').val()!='' && !floatExec($('#frayAsc').val()) ){
		alert('请输入正确的金额');
		$('#frayAsc').focus();
		return ;
	}
	if($('#payFrankingAsc').val()!='' && !floatExec($('#payFrankingAsc').val()) ){
		alert('请输入正确的金额');
		$('#payFrankingAsc').focus();
		return ;
	}
	if($('#payShippingAsc').val()!='' && !floatExec($('#payShippingAsc').val()) ){
		alert('请输入正确的金额');
		$('#payShippingAsc').focus();
		return ;
	}
	if($('#payShippingAsc').val()!='' && !floatExec($('#payShippingAsc').val()) ){
		alert('请输入正确的金额');
		$('#payShippingAsc').focus();
		return ;
	}
	if($('#frankingAsc').val()!=''||$('#shippingAsc').val()!=''||$('#frayAsc').val()!=''
		||$('#payFrankingAsc').val()!=''||$('#payShippingAsc').val()!=''){
		if($('#costTypeAsc').val()==''){
			alert('请选择用户支付方式');
			return ;
		}
	}
	$.ajax({
		type:"post",
		url:'afterSales.do?method=addOrignalCost',
		dataType:'json',
		data:$('#addServiceCostForm').serialize(),
		success:function(json){
			var msg=json.msg;
			if(msg=='success'){
				var id= json.afterSaleCostId;
				var balance  = json.balance;
				if(id==0){
					alert('添加费用单信息发生异常。请检查网络连接');
				}else{
					$('#afterSaleCostIdAsc').val(id); 
					var type=$('#afterSaleStatus').val();
					 
			 		$('#confrimDialogContext').html('<div style="float: left;">当前售后单状态：质检确认</div> <div style="width:200px;clear:both" algin="center">用户需要向买卖宝支付： '+balance
			 		+'</div><br/><div style="float: left; ,margin:20px"><font color="red"> 确认维修费用后售后单状态将转入“售后联系中”</font></div>');
			 		$('#addServiceCost').dialog('close');
					openConfrimDialog(1);
					 
				}
			}else if(msg=='fail'){
				var text=json.text;
				alert(text);
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
	});
}


function changeToOrignal(type){
	$("#changeToOrignalDiv").dialog({  
     	modal: true,
     	bgiframe: true,
     	buttons: {
	       "确定": function() {
	       		if($('#returnResCtd').val()==''){
	       			alert('请选择原品返回原因');
	       			return;
	       		}
	       		var parmas = "returnRes="+$('#returnResCtd').val()+"&afterSaleId="+$('#afterSaleId').val();
	       		$.ajax({
					type:"post",
					url:'afterSales.do?method=updateCost',
					dataType:'json',
					data:parmas,
					success:function(json){
						var msg=json.msg;
						if(msg=='success'){
							$('#typeId').val(type);
			               	$('#typForm').submit();
						}else if(msg==''){
							alert('数据异常!');
						}
					},
					error:function(XMLHttpRequest, textStatus, errorThrown){
				   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
				    }
				});
	       		 
        	 },
           	"取消": function() {
            		$(this).dialog('close');
        	 	}
    		 } ,
    	dialogClass: 'alert'
     });
}

function changeToPayMark(type){//标记售后单为 维修的状态 0售后付费维修 1 商家付费维修 2厂家保修 3 售后保修 
	var parmas = "type="+type+"&afterSaleId="+$('#afterSaleId').val();
	$.ajax({
		type:"post",
		url:'afterSales.do?method=updateFielColumn',
		dataType:'json',
		data:parmas,
		success:function(json){
			 return true;
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
	});	
}
function factoryCanService(type,category){
	if(category==1){
		$('#resendRes').val('9');
	}else{
		$('#resendRes').val('8');
	}
	$("#fatoryResendDiv").dialog({  
     	modal: true,
     	bgiframe: true,
     	buttons: {
	       "确定": function() {
	       		if($('#resendRes').val()==''){
	       			alert('厂商寄回原因');
	       			return;
	       		}
	       		var parmas = "type="+$('#resendRes').val()+"&afterSaleId="+$('#afterSaleId').val();
	       		$.ajax({
					type:"post",
					url:'afterSales.do?method=updateFielColumn',
					dataType:'json',
					data:parmas,
					success:function(json){
						var msg=json.msg;
						if(msg=='success'){
							$('#typeId').val(type);
			               	$('#typForm').submit();
						}else if(msg==''){
							alert('数据异常!');
						}
					},
					error:function(XMLHttpRequest, textStatus, errorThrown){
				   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
				    }
				});
	       		 
        	 },
           	"取消": function() {
            		$(this).dialog('close');
        	 	}
    		 } ,
    	dialogClass: 'alert'
     });
	
}

var map=new Map();
map.put("1","售后联系中");
map.put("2","包裹返途中");
map.put("0","质检支撑处理");
//map.put("1","漏发审核中");
map.put("3","检测中");
map.put("11","退货中");
map.put("13","换货中");
map.put("30","售后已完成");
map.put("4","维修质检确认");
map.put("6","维修实质确认");
map.put("464","等待厂商寄回");
map.put("465","厂商已寄回");
map.put("466","售后维修");
map.put("467","售后维修已完成");
map.put("9","厂商维修");
map.put("8","已返厂");
map.put("7","等待返厂");

//改变状态
function changeType(type,name){
	if(name=='售后取消'){
		$("#cancelAfterSale").dialog({  
	     	modal: true,
	     	bgiframe: true,
	     	buttons: {
		       "确定": function() {
		       		if( $.trim($('#afterSaleMark').val())===''){
		       			alert('取消备注是必填选项!');
		       			return;
		       		}
		       		if($('#afterSaleMark').val().length>155){
		       			alert('输入的字数不能超过155个!');
		       			return;
		       		}
		       		$('#remarks').val($('#afterSaleMark').val());
		       		$('#typeId').val(type);
	               	$(this).dialog('close');
	               	//document.typForm.submit();
	               	$('#typForm').submit();
	        	 },
	           	"取消": function() {
	            		$(this).dialog('close');
	        	 	}
	    		 } ,
	    	dialogClass: 'alert'
	     });
	}else if(name=='确认收货并开始检测'){
		$("#backPackageLogin").dialog({  
	     	modal: true,
	     	bgiframe: true,
	     	width:540,
	     	buttons: {
		       "开始检测": function() {
		       		var flag = validateAddPackage(1);
		       		 if(flag===false){
		       		 	return;
		       		 }else{
		       		 	$(this).dialog('close');
		       		 	addBackPackageInfo(type);	
		       		 }
	        	 },
	           	"取消": function() {
	            		$(this).dialog('close');
	        	 	}
	    		 } ,
	    	dialogClass: 'alert'
	     });
	}else if(name=='可以退货'){
		$("#addRetrunCost").dialog({  
	     	modal: true,
	     	bgiframe: true,
	     	width:450,
	    	dialogClass: 'alert'
	     });
	     
	     showAfterSaleCost('return');
	     //toSelect2('returnResHid');
	}else if(name=='退货余额转组长确认'){
	    computerAfterSaleCost(1);
		$("#configReturnFinance").dialog({  
	     	modal: true,
	     	bgiframe: true,
	     	width:330,
	     	height:300,
	     	buttons: {
		       "退货余额转组长确认": function() {
			         if(Number($('#crf_backMonery').val())<0){
	                 	alert('退货余额是负数，请检查结算费用输入是否正确！');
	                 	return ; 	
	                 }
	                 $(this).dialog('close'); //页面提交并确认 改变售后单状态
		             $('#typeId').val(474);
		             $('#typForm').submit();
	        	 },
	        	 "取消": function() {
	        	 		$('#crf_saveOrMondiry').html('<a href="javascript:void(0);" onclick="financeSumMonery()">修改</a>');
	            		$(this).dialog('close');
	        	 	}
	    		 } ,
	        dialogClass: 'alert'
	     });
	        	 
	}else if(name=='组长确认转财务确认'){
		$("#verifyDialogContext").html("<br/>您确定将此售后单进行审核确认吗?<br/><br/><font color='red'>确认后该售后单状态将变成\"退款财务确认\"状态!</font>");
		$("#verifyDialog").dialog({  
		   	modal: true,
		    bgiframe: true,
		    width:330,
		    height:250,
		    buttons: {
			  "确定": function() {
		        	$(this).dialog('close');
		             $('#typeId').val(type);
		             $('#typForm').submit();
		        },
		        "取消": function() {
		           		$(this).dialog('close');
		           		$('#typeFormForward').val(0);
		         	}
		    	 } ,
		   	dialogClass: 'alert'
		 });
	}else if(name=='财务已确认'){
		if($('#costBeanCostTYpe').val()==2){
			$('#confrimDialogContext').html('您确定财务已经退款给用户了吗？<br/><br/>售后单状态将变成“售后已完成”');	
		}else{
			$('#confrimDialogContext').html('您确定要系统自动向用户钱包退款'+$('#costBeanBalance').html()+'元吗？售后单状态将变成“售后已完成“');
		}
		$('#typeFormForward').val(12);
		$("#confrimDialog").dialog({  
		   	modal: true,
		    bgiframe: true,
		    width:330,
		    height:250,
		    buttons: {
			  "确定": function() {
		        	$(this).dialog('close');
		             $('#typeId').val(type);
		             $('#typForm').submit();
		        },
		        "取消": function() {
		           		$(this).dialog('close');
		           		$('#typeFormForward').val(0);
		         	}
		    	 } ,
		   	dialogClass: 'alert'
		 });
	}else if(name=='可以换货'){
		showAfterSaleCost('replace');
	    //toSelect2('returnResHidR');
		$("#addReplaceCost").dialog({  
	     	modal: true,
	     	bgiframe: true,
	     	width:480,
	     	height:300,
	    	dialogClass: 'alert'
	     });
	}else if(name=='确认收款'||name=='确认退款'){
		showReceivables(type);
	}else if(name=='调换换货物品'){
		var text=map.get(type);
		if(text==null || text=='undefined' ){
			text=name;
		}
		$('#confrimDialogContext').html('您确定要取消订单'+$('#newOrderCodeSpan').html()+'发货吗？<br/>确认要将此售后订单改为"'+text+'"?');
		openConfrimDialog(type);
	}else if(name=='财务已经付款'){
		$('#confrimDialogContext').html('您确定财务已经退款给用户了吗？<br/>售后订单状态将变成售后已完成?');
		openConfrimDialog(type);
	}else if(name=='原品返回'){
		orignalCostList(type);
	}else if(name=='原品发货'){
		addShippingPackage(type);
	}else if(name=='售后付费维修'){
		changeToPayMark(0);
		serviceCostList(2);
	}else if(name=='转原品返回'){
		changeToOrignal(type);
	}else if(name=='厂家付费维修'){
		changeToPayMark(1);
		$('#confrimDialogContext').html('确认要将此售后订单改为"实质处理"?');
		openConfrimDialog(type);
	}else if(name=='等待厂商寄回'){
		factoryCanService(type,1);
	}else if(name=='可以保修'){
		changeToPayMark(0);
		prepareocd(type ,name);
	}else if(name=='寄回厂商修理'){
		changeToPayMark(2);
		prepareocd(type ,name);
	}else if(name=='售后维修'){
		changeToPayMark(3);
		prepareocd(type ,name);
	}else if(name=='厂商维修'){
		if($('#asoPayMark').val()=='1'){
			serviceCostList(1);
		}else{
			prepareocd(type ,name);
		}
	}else if(name=='维修完成回等待厂商寄回'){
		factoryCanService(type,2);
	}else if(name=='维修完成等待返回用户'){
		addRepairePackage(4,type);
	}else if(name=='返回用户'){
		addRepairePackage(5,type);
	}else{
		prepareocd(type ,name);
	}
}
function prepareocd(type ,name){
	var text=map.get(type);
	if(text==null || text=='undefined' ){
		text=name;
	}
	$('#confrimDialogContext').html('确认要将此售后订单改为"'+text+'"?');
	openConfrimDialog(type);
}
function openConfrimDialog(type){
	$("#confrimDialog").dialog({  
	   	modal: true,
	    bgiframe: true,
	    width:330,
	    height:200,
	    buttons: {
		  "确定": function() {
	        	$(this).dialog('close');
	             $('#typeId').val(type);
	             $('#typForm').submit();
	        },
	        "取消": function() {
	           		$(this).dialog('close');
	           		$('#typeFormForward').val(0);
	         	}
	    	 } ,
	   	dialogClass: 'alert'
	 });
}

function dialogClose(id){//关闭弹出框
	$('#'+id).dialog('close');
}
function dialogOpen(id){//关闭弹出框
	$('#'+id).dialog('open');
}

function financeSumMonery(){
	var value= $('#configReturnFinance_sumMonery').html();
	$('#configReturnFinance_sumMonery').html('<input type="text" size=3 name="financeSumMonery" id="financeSumMonery"  value="'+value+'"/>');
	$('#crf_saveOrMondiry').html('<a href="javascript:void(0);" onclick="saveFinanceSumMonery()">保存</a>');
}

function saveFinanceSumMonery(){
	if($('#financeSumMonery').val()!=='' && !floatTest($('#financeSumMonery').val() )){
		alert('请输入正确的数字');
		$('#financeSumMonery').focus();
		return false;
	}
	var parms ='id='+$('#afterSaleCostId').val()+'&backMonery='+$('#financeSumMonery').val(); 
	$.ajax({
		type:"get",
		url:'afterSales.do?method=modifyComputeCost',
		dataType:'json',
		data:parms,
		success:function(json){
			  if(json!==null){
				  if(json.msg=="success"){
				  	  var bean = json.costBean;
					  var sumMonery=bean.returnProductPrice; //退回商品价格
					  var backMonery=json.backMonery;
					  var text='<font color="red">我们应该向</font><font color="blue">';
					  $('#configReturnFinance_sumMonery').html(sumMonery);
					  var userText="";
					  if(bean.backType==1){
					    userText='用户钱包('+bean.backUserName+')退还';
					  }else if(bean.backType==2){
						userText='用户银行账号打款';
					  }
					  text+=userText+'</font><font color="red">'+backMonery+'</font>';
					  $('#configReturnFinance_text').html(text);
					  $('#crf_backMonery').val(backMonery);
					  $('#crf_saveOrMondiry').html('<a href="javascript:void(0);" onclick="financeSumMonery()">修改</a>');
				  }else{
				  	  alert(json.context);
				  }
			  }
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert('修改退回商品价格发生异常!');
	    }
	});
}

function showUserName(backUserName){//显示用户账号
	if($('#'+backUserName).val()!==""){
		return;
	}
	var parms="afterSaleId="+$('#afterSaleId').val();
	$.ajax({
		type:"get",
		url:'afterSales.do?method=isRegisterUser',
		dataType:'json',
		data:parms,
		success:function(json){
			  if(json!==null){
				  var name=json.username;
				  if(name!==""){
				  	 $('#'+backUserName).val(name);
				  }
			  }
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert('获取注册用户发生异常!');
	    }
	});
}

function chooseBackUserType(backType,divName,backUser){//费用单  根据单选的值看是否需要显示钱包文本
	 var value= getRadiosValue(backType);
	 if(value==1){
	 	$('#'+divName).show();
	 	showUserName(''+backUser);
	 }else{
	 	$('#'+divName).hide();
	 }
}
function chooseBankOrUserWallt(backType,divName1,divName2,backUser){
	var value= getRadiosValue(backType);
	 if(value==1){
	 	$('#'+divName1).show();
	 	$('#'+divName2).hide();
	 	showUserName(''+backUser);
	 }else{
	 	$('#'+divName1).hide();
	 	$('#'+divName2).show();
	 }
}


function returnResChoose(id){//选中改变按钮的值。。
//	var selectO = $('#'+id).val();
	
	if(id==1){
		$('#returnButton').attr('value','错发替换');
	}else{
		$('#returnButton').attr('value','办理退货');
	}
	
//	if(selectO=='其他'){
//		$('#'+spanId).show();
//	}else{
//		$('#'+spanId).hide();
//	}
}

function replaceResChoose(id){//选中改变按钮的值。。
	if(id==1){
		//document.getElementById('returnButton').value='错发替换';
		$('#replaceButton').attr('value','错发替换');
	}else{
		//document.getElementById('returnButton').value='办理换货';
		$('#replaceButton').attr('value','办理换货');
	}
//	
//	if(selectO=='其他'){
//		$('#'+spanId).show();
//	}else{
//		$('#'+spanId).hide();
//	}
}

//快递 和银行选择其他的时候
function showOtherText(id,spanId){
	var selectO = $('#'+id).val();
	if(selectO=='其他'){
		$('#'+spanId).show();
	}else{
		$('#'+spanId).hide();
	}
}
//编辑保存
function validate(){
	var address = document.getElementById("customerBankAddress");
	var sheng = document.getElementById("sheng");
	var shi = document.getElementById("shi");
	var qu = document.getElementById("qu");
	var street = document.getElementById("streetId");
	var road = document.getElementById("road").value;
	var addre='';
	if(sheng.selectedIndex!=0){
		addre = sheng.options[sheng.selectedIndex].text + shi.options[shi.selectedIndex].text;
		if(qu.options != null && qu.options.length > 0){
			addre += qu.options[qu.selectedIndex].text;
		}
		if(street.options!=null && street.options.length>0){
			addre+=street.options[street.selectedIndex].text;
		}
		address.value = addre+road;
	}
	$('#confrimDialogContext').html('确定保存以上信息');
	$("#confrimDialog").dialog({  
     	modal: true,
     	bgiframe: true,
     	buttons: {
	       "确定": function() {
               	$(this).dialog('close');
               	$('#afterSaleFormEdit').submit();
        	 },
           	"取消": function() {
            		$(this).dialog('close');
        	 	}
    		 } ,
    		 dialogClass: 'alert'
     });
}

//如果选择的是其他。。
function selectOptionWithOther(select,value){
	var flag=false;
	for(var i = 0; i < select.length; i++){
		if(select.options[i].value == value){
			select.options[i].selected = true;
			break;
		}
		if(i==select.length-1){
			flag=true;
		}
	}
	if(flag){
		select.options[select.length-1].selected = true;
		$('#otherBankName').val(value);
	}
	showOtherText('customerBankName','BankNameSpan');
}

//验证添加包裹单。
function validateAddPackage(type){
	var temp="";var people = '签收';
	if(type==1){
		temp='B';
	}else if(type==2){
		temp='S';
		people='发货';
	}
	
	 if($('#deliver'+temp).val()=='0'){
	 	alert('请选择快递!');
	 	$('#deliver'+temp).focus();
	 	return false;
	 }
	 if(type==1){
	 	if($('#deliver'+temp).val()=='其他'){
		 	 if($.trim($('#otherDeilver'+temp).val())==""){
			 	alert('请输入其他的快递名称!');
			 	$('#otherDeilver'+temp).focus();
			 	return false;
			 }
		}
		if($('#returnType'+temp).val()==0){
		 	alert('请选择寄回方式!');
		 	$('#returnType'+temp).focus();
		 	return false;
		}
	 }else if(type==2){
	 	if($.trim($('#coseMonery'+temp).val())==""){
 			alert('请输入运费金额!');
		 	$('#coseMonery'+temp).focus();
		 	return false;
	 	}
	 }
	 if($.trim($('#packageNum'+temp).val())==""){
	 	alert('请输入快递单号!');
	 	$('#packageNum'+temp).focus();
	 	return false;
	 }
	 if($.trim($('#signPeople'+temp).val())==""){
	 	alert('请输入'+people+'人!');
	 	$('#packageNum'+temp).focus();
	 	return false;
	 }
	 
	 if($.trim($('#signDate'+temp).val())==""){
	 	alert('请输入'+people+'日期!');
	 	$('#signDate'+temp).focus();
	 	return false;
	 }
	 if(!validateDate($('#getNowDate').val(),$('#signDate'+temp).val())){
	 	 alert(''+people+'日期必须小于等于今天!');
	 	 $('#signDate'+temp).focus();
	 	 return false;
	 }
	  
	 if(!floatTest($('#coseMonery'+temp).val())){
	 	alert('运费金额格式有问题!');
	 	$('coseMonery'+temp).focus();
	 	return false;
	 }
	 if(!CheckDate($('#signDate'+temp).val())){
	 	$('#signDate'+temp).focus();
	 	return false;
	 }
	 if($('#remark'+temp).val().length>100){
	 	alert('备注信息不能超过100个汉字');
	 	$('#remark'+temp).focus();
	 	return false;
	 }
	  
	 return true;
}
//异步提交退回包裹信息
function addBackPackageInfo(type){
	$.ajax({
		type:"post",
		url:'afterSales.do?method=addPackage',
		dataType:'text',
		data:$('#backPackageLoginForm').serialize(),
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

//异步提交 退回费用单
function addAfterSaleCost(formValue){
	$.ajax({
		type:"post",
		url:'afterSales.do?method=addAfterCost',
		dataType:'json',
		data:$('#'+formValue).serialize(),
		success:function(json){
			var msg=json.msg;
			if(msg=='success'){
				var id= json.afterSaleCostId;
				if(id==0){
					alert('添加费用单信息发生异常。请检查网络连接');
				}else{
					 if(formValue=='addAfterSaleCostForm'){
					 	$('#afterSaleCostId').val(id);
					 	preJudgeCostForward(1);
					 }else if(formValue=='addReplaceCostForm'){
					 	$('#afterSaleCostIdR').val(id);
					 	preJudgeCostForward(2);
					 }else{
					 	alert('修改成功！');
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

function showAfterSaleCost(type){//显示历史费用单
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
					var temp ='';
					if(type=='replace'){
						temp='R';
						if(bean.typeRes==1){
							$('#replaceButton').attr('value','错发替换');
						}
						$('#arcTextSpan').html('换');
						toSelect2('returnResHidR',bean.typeResText);
					}else{
						selectRadiosOption('backType',bean.backType);
						if(bean.backType==1){
							$('#userWalletName').show();
							$('#backUserName').val(bean.backUserName);
						}
						if(bean.typeRes==1){
							//document.getElementById('returnButton').value='错发替换';
							$('#returnButton').attr('value','错发替换');
						}
						if($('#otherReturnRes').val()!=''){
							$('#returnRes').attr('value','其他');
						}
						$('#arcTextSpan').html('退');
						toSelect2('returnResHid',bean.typeResText);
					}
					
					$('#returnRes'+temp).val(bean.typeRes);
					if(bean.costType==1){
						$('#reciveFranking'+temp).val(bean.franking!=0?bean.franking:'');
					}else if(bean.costType==2){
						$('#payFranking'+temp).val(bean.franking!=0?bean.franking:'');
					}
					$('#shipping'+temp).val(bean.shipping!=0?bean.shipping:'');
					$('#payShipping'+temp).val(bean.payShipping!=0?bean.payShipping:'');
					$('#fray'+temp).val(bean.fray!=0?bean.fray:'');
					
					$('#afterSaleCostId'+temp).val(bean.id); 
					
				}
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert('显示历史费用单异常!');
	    }
	});
}

function returnSubmit(formValue){//费用单提交  弹出的div
    var temp="";
    if(formValue=='addAfterSaleCostEdit' || formValue=='editCostFrom'){
    	temp="Edit";
    }else if(formValue=='addReplaceCostForm'){
    	temp='R';//换货 费用单添加
    } 
    if(temp!='R' && formValue!='editCostFrom'&& getRadiosValue('backType'+temp)===''){
		alert('款项退给为必选项!');
		return;
	}
	if($('#returnRes'+temp).val()==""){
		alert('请选择退货原因');
		return ;
	}
	if($('#returnRes'+temp).val()=="0"){
		 if($.trim($('#otherReturnRes'+temp).val())==''){
		 	alert('请输入其他的退货原因');
		 	return;
		 }
	}
	var payFranking=$('#payFranking'+temp).val();
	var reciveFranking=$('#reciveFranking'+temp).val();
	var shipping=$('#shipping'+temp).val();
	var fray=$('#fray'+temp).val();
	var payShipping=$('#payShipping'+temp).val();
	if(payShipping=='undefined' || payShipping==null ){
		payShipping='';
	} 
	if(payFranking!='' && reciveFranking !='' ){
		alert('应付和应收的寄回运费不能全部填写');
		$('#payFranking'+temp).focus();
		return;
	}
	if(shipping!='' && payShipping !='' ){
		alert('应付和应收的发货运费不能全部填写');
		$('#payShipping'+temp).focus();
		return;
	}
	
	if(payFranking!='' && !floatExec(payFranking) ){
		alert('请输入正确的金额');
		$('#payFranking'+temp).focus();
		return ;
	}
	if(reciveFranking!='' && !floatExec(reciveFranking) ){
		alert('请输入正确的金额');
		$('#reciveFranking'+temp).focus();
		return ;
	} 
	if(shipping!='' && !floatExec(shipping) ){
		alert('请输入正确的金额');
		$('#shipping'+temp).focus();
		return ;
	} 
	if(fray!='' && !floatExec(fray) ){
		alert('请输入正确的金额');
		$('#fray'+temp).focus();
		return ;
	} 
	if(payShipping!='' && !floatExec(payShipping) ){
		alert('请输入正确的金额');
		$('#payShipping'+temp).focus();
		return ;
	} 
	addAfterSaleCost(formValue);//添加
}

function floatExec(num){//判断是否为浮点数 或为整数 1.0 0.2
    var reFloat=/^\d+(|\.\d{1})+$/;
    if(!reFloat.exec(num))return false;
	return true;
}

//费用单添加后 添加退货单，并将商品显示出来
function preJudgeCostForward(type){
	var parms="id="+$('#afterSaleId').val();
	$.ajax({
		type:"post",
		url:'afterSales.do?method=areorder',
		dataType:'json',
		data:parms,
		success:function(json){
			if(json!=null){
				var asob=json.asob;
				var list= json.asropList;
				var refundCod=json.refundOrderCode;
				$('#refundCodeSpan').html(refundCod);
				$('#orderCodeSpan').html(asob.orderCode);
				$('#afterSaleCodeSapn').html(asob.afterSaleOrderCode);
				if(type==1)
					$('#returnShippingResSpan').html($("#returnRes").find("option:selected").text()=='其他'?$('#otherReturnRes').val():$("#returnRes").find("option:selected").text() );
				else
					$('#returnShippingResSpan').html($("#returnResR").find("option:selected").text()=='其他'?$('#otherReturnResR').val():$("#returnResR").find("option:selected").text() );	
				var html='<table width="80%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="left" style="margin-left:10px" >';
				html+='<tr bgcolor="#4688D6" ><td><font color="#FFFFFF">商品编号</font></td><td><font color="#FFFFFF">商品名称</font></td>'
						+'<td><font color="#FFFFFF">商品分类</font></td>	<td><font color="#FFFFFF">商品金额</font></td>'	
				         +'<td><font color="#FFFFFF">退货数量</font></td> <td><font color="#FFFFFF">退货地区</font></td>'
				         +'<td><font color="#FFFFFF">批次号/批次价</font></td></tr>'
				for(var i=0;i<list.length;i++){
					var p=list[i];
					var stockArea='&nbsp;';
					var batchCode='&nbsp;';
					var batchPrice='&nbsp;';
					if(p.stockBatchLog!='null'&&p.stockBatchLog!=null){
						stockArea=json.arr[p.stockBatchLog.stockArea];
						batchCode=p.stockBatchLog.batchCode;
						batchPrice=p.stockBatchLog.batchPrice;
					}
					html+='<tr bgcolor="#F8F8F8"><td>'+p.product.code+'</td>';
					html+='<td>'+p.product.oriname+'</td>';
					html+='<td>'+p.product.parent1.name+'</td>';
					html+='<td>'+p.product.price+'</td>';
					html+='<td><input type="text" size="2" name="count'+p.id+'" value="1"/></td>';
					html+='<td>'+stockArea+'</td>';
					html+='<td>'+batchCode+'/'+batchPrice+'</td></tr>';
				}
				html+="</table>"
				$('#returnInStockInnerDiv').html(html);
				$('#entryStorageFormId').val($('#afterSaleId').val());
				judgeCostForward(type);
			}else{
				alert('显示退货信息错误!');
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		///alert('显示退货信息异常');
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
	});	
}

function judgeCostForward(type){ //费用单添加后  判断执行方向
	var buttonValue='';
	if(type==1){
		buttonValue=$('#returnButton').attr('value');
		//document.getElementById('returnButton').value;
		dialogClose('addRetrunCost');
	}else if(type==2){
		buttonValue=$('#replaceButton').attr('value');
		dialogClose('addReplaceCost');
	}
	
	if(buttonValue=='错发替换'){
		$("#returnInStock").dialog({  
	     	modal: true,
	     	bgiframe: true,
	     	title:'请先确认退货商品和数量',
	     	width:780,
	     	buttons: {
		       "查找发错商品": function() {
		       		var inputs = $('#entryStorageForm input[type=text]');
					for(var i=0;i<inputs.length;i++){
						var inpu = inputs[i];
						if(!intTest(inpu.value)){
							alert('请输入正确的数字');
							return ;
						}
					}
		       		$.ajax({
						type:"post",
						url:"afterSales.do?method=updateReplaceCount",
						dataType:'json',
						data:$('#entryStorageForm').serialize(),
						success:function(json){//显示计算后的金额
							if(json!=null){
								if(json.msg=='success'){
									dialogClose('returnInStock');
									$('#replaceWrongProduct').data("type", type);
				               		showReplaceWorngProduct();								
								}else{
									alert(json.context);
								}
							} 
						},
						error:function(XMLHttpRequest, textStatus, errorThrown){
							alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
						}
					});
	        	 },
	           	"返回上一步": function() {//返回检测中，费用单添加标签
	           			$(this).dialog('close');
	           			if(type==1){
	           				dialogOpen('addRetrunCost');
	           			}else if(type==2){
	           				dialogOpen('addReplaceCost');
	           			}
	        	 	}
	    		 },
	    	dialogClass: 'alert'
	     });
	}else if(buttonValue=='办理退货'){
		$("#returnInStock").dialog({  
	     	modal: true,
	     	bgiframe: true,
	     	title:'正在办理退货入库',
	     	width:780,
	     	buttons: {
		       "退货入库": function() {
		       		var inputs = $('#entryStorageForm input[type=text]');
					for(var i=0;i<inputs.length;i++){
						var inpu = inputs[i];
						if(!pIntText(inpu.value)){
							alert('请输入正确的数字');
							return ;
						}
					}
					$('#entryForward').val(4);
	               	inStockAndShowForward();
	        	 },
	           	"返回上一步": function() {//返回检测中，费用单添加标签
	           			$(this).dialog('close');
	            		dialogOpen('addRetrunCost');
	        	 	}
	    		 },
	    	dialogClass: 'alert'
	     });
	}else if(buttonValue=='办理换货'){
		$("#returnInStock").dialog({  
	     	modal: true,
	     	bgiframe: true,
	     	title:'正在办理换货入库',
	     	width:780,
	     	buttons: {
		       "换货入库": function() {
		       		var inputs = $('#entryStorageForm input[type=text]');
					for(var i=0;i<inputs.length;i++){
						var inpu = inputs[i];
						if(!pIntText(inpu.value)){
							alert('请输入正确的数字');
							return ;
						}
					}
					$('#entryForward').val(1);
	               	replaceInAndShowForward();
	        	 },
	           	"返回上一步": function() {//返回检测中，费用单添加标签
	           			$(this).dialog('close');
	            		dialogOpen('addReplaceCost');
	        	 	}
	    		 },
	    	dialogClass: 'alert'
	     });
	}
}

//退货入库  并页面显示 跳转到财务..
function inStockAndShowForward(){
	$.ajax({
		type:"post",
		url:"afterSales.do?method=entryStorage",
		dataType:'json',
		data:$('#entryStorageForm').serialize(),
		success:function(json){ //退货入库售后库
			var msg=json.msg;
			if(msg=="success"){
				dialogClose('returnInStock');
				//计算退款金额
				computerAfterSaleCost(2);
			}else if(msg=="fail"){
				alert(json.context);
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
	});
}

function computerAfterSaleCost(type){ //退款金额
	$.ajax({
		type:"post",
		url:"afterSales.do?method=computeCost",
		dataType:'json',
		data:"id="+$('#afterSaleId').val(),
		success:function(json){//显示计算后的金额
			if(json!=null){
				if(json.msg=="success"){
					var afterBean =json.afterBean;
					var costBean = json.costBean;
					var sumMonery=json.sumMonery;
					var backMonery=json.backMonery;
					var text='<font color="red">我们应该向</font><font color="blue">';
					$('#configReturnFinance_span').html(afterBean.statusName);
					$('#configReturnFinance_sumMonery').html(sumMonery);
					var userText="";
					if(costBean.backType==1){
						userText='用户钱包('+costBean.backUserName+')退还';
					}else if(costBean.backType==2){
						userText='用户银行账号打款';
					}
					text+=userText+'</font><font color="red">'+backMonery+'</font>';
					$('#crf_backMonery').val(backMonery);
					$('#configReturnFinance_text').html(text);
					if(type==2){
						showFinancial();
					}
				}else{
					alert(json.context);
				}
			}else{
				alert('获取金额异常！！');
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
		}
	});
}

//显示是否确认财务退款
function showFinancial(){
	$("#configReturnFinance").dialog({  
     	modal: true,
     	bgiframe: true,
     	width:330,
     	height:500,
     	buttons: {
	       "退货余额转组长确认": function() {
                 if(Number($('#crf_backMonery').val())<0){
                 	alert('退货余额是负数，请检查结算费用输入是否正确！');
                 	return ; 	
                 }
                 //页面提交并确认 改变售后单状态
                 $(this).dialog('close');
	             $('#typeId').val(474);
	             $('#typForm').submit();
        	 },
           	"返回上一步": function() {
           			$('#crf_saveOrMondiry').html('<a href="javascript:void(0);" onclick="financeSumMonery()">修改</a>');
            		var parmar="afterSaleOrderID="+$('#AfterSaleOrderID').val()+"&type=3"
            		$.ajax({
            			type:"post",
						url:"afterSales.do?method=changBackStatus",
						dataType:'json',
						data:parmar,
						success:function(json){
							if(json!=null){
								if(json.msg=="success"){
									$('#configReturnFinance').dialog('close');
									$('#returnInStock').dialog('open');//退回正在办理退货入库标签。。
								}else{
									alert(json.msg);
								}
							}else{
								alert('修改状态失败');
							}
						},
						error:function(XMLHttpRequest, textStatus, errorThrown){
					   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
					    }
            		}); 
        	 	}
    		 } ,
    	dialogClass: 'alert',
    	close:function(event, ui) {
	       	if(event.clientY !='undefined' && event.clientY>0){
	       		document.location.href=document.location.href;
	       	} 
	    }
     });
}
//产找替换商品
function showReplaceWorngProduct(){
	$('#replaceWrongProduct').dialog({
		modal:true,
		bgiframe:true,
		width:900,
		height:650,
		buttons:{
			"返回":function(){
				$(this).dialog('close');
				dialogOpen('returnInStock');
			}
		}
	});
}

//iframe 中点击添加商品
function addproduct(code,id,price,num){
	var procutCodes =""  ;
	var countTypesName="";
 	if($('#flagPresent').val()=='present'){
 		procutCodes="rwpPresentCodes";
 		countTypesName="presentCount";
 	}else{
 		procutCodes='rwpProductCodes';
 	 	countTypesName="productCount"
 	}
	var value; 
	if(id!=0){
		value = prompt('将添加产品编号为'+code+'的产品，请输入数量，并确认', 1);
		if(value==null) return;
	}else{
		value =$('#rwpAmount').val();
		code=$('#rwpProductCode').val();
		if($.trim(code)==""){
			alert('请输入产品编号');
			return ;
		}
	}
	
	if($('#'+procutCodes).val()!=""){
		var arrayCode = $('#'+procutCodes).val().split(',');
		for(var i=0;i<arrayCode.length;i++){
			if(code==arrayCode[i]){
				alert('该商品已经添加!');
				return;
			}
		}
	}
	var parmar="productCode="+code+"&type="+num+'&procutType='+$('#flagPresent').val()+'&pCount='+value;
	$.ajax({
    	type:"post",
		url:"afterSales.do?method=showProducts",
		dataType:'json',
		data:parmar,
		success:function(json){
			if(json!=null){
				 if(json.msg=='success'){
					 var  list = json.productList;
					 var presnetList =json.presnetList;
					 var persentSb = json.persentSb;
					 if(list!=null && list.length>0){
						 addProductExpend(list,procutCodes,countTypesName,value,1);
					 }
					 if(presnetList!=null && presnetList.length>0) {
					 	addProductExpend(presnetList,"rwpPresentCodes","presentCount",0,2);
					 }
					 if(persentSb!=null){
					 	var ps =persentSb.split("T");
					 	$('#tr'+ps[0]).data('ptr'+ps[0],ps[1]);		
					 }
				 }else if(json.msg=='fail'){
				 	if(json.context==1){
				 		alert('该产品不是赠品，不能添加');
				 	}
				 }else{
				 	alert('对不起，您添加的商品已'+json.context+'，请添加其他商品！');
				 }
			}else{
				alert('显示列表失败');
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
     }); 
}

function addProductExpend(list,procutCodes,countTypesName,value,flag){
	 var html='';
	 for(var i=0;i<list.length;i++){
	 	var p = list[i];
	 	var fangcun=0,beisu=0,zengcheng=0;
	 	for(var j=0;j<p.psList.length;j++){//获取北速2 芳村1 的库存数量
	 		var ps = p.psList[j];
	 		if(ps.type!=7&& ps.type!=8)
		 		if(ps.area==1){
	 				fangcun+=ps.stock;
		 		}else if(ps.area==2){
		 			beisu+=ps.stock;
		 		}else if(ps.area==3){
		 			zengcheng+=ps.stock;
		 		}
	 	}
	 	var activity='';
	 	if(p.guanggao!=""){
	 		activity='<font color="red">('+p.guanggao+')</font>';
	 	}
	 	html+='<tr id="tr'+p.id+'"><td align="center">'+$('#replaceWrongProductTable tr').length+'</td>';
	 	html+='<td align="center">'+p.code+'</td>';
	 	html+='<td align="center">'+p.oriname+activity+'</td>';
	 	html+='<td align="center">'+p.parent1.name+'</td>';
	 	html+='<td align="center">'+p.price+'</td>';
	 	html+='<td align="center">'+fangcun+'</td>';
	 	html+='<td align="center">'+beisu+'</td>';
	 	html+='<td align="center">'+zengcheng+'</td>';
	 	html+='<td align="center"><input type="text" size="5" name="'+countTypesName+p.id+'" value='+(value!=0?value:p.brand)+' /></td>';
	 	html+='<td align="center"><a href="javascript:void(0);" onclick="delAddProduct(\'tr'+p.id+'\',\''+p.code+'\',\''+procutCodes+'\')">删除</a></td></tr>';
	 	
	 	if($('#'+procutCodes).val()!="") 
	 		$('#'+procutCodes).val($('#'+procutCodes).val()+','+p.code);
	 	else
	 		$('#'+procutCodes).val(p.code);
		 
	 }
	 if(flag==1){
		 if($('#flagPresent').val()=='present'){
		 	$('#replaceWrongPresentTable').append(html);
		 }else{
		 	$('#replaceWrongProductTable').append(html);
		 	$('#rwpSuerDiv').show();
		 }
	 }else{
	 	$('#replaceWrongPresentTable').append(html);
	 }
}

function delAddProduct(id,code,typeName){ //删除替换商品中的产品
	if(confirm('您确定删除该商品?')){
		if($('#'+id).data('p'+id)!=null){
			var present=$('#'+id).data('p'+id).split(',');
			for(var i=0;i<present.length;i++){
				$('#tr'+present[i]).remove();//rwpPresentCodes
				var str="";
				var arrayCodeP = $('#rwpPresentCodes').val().split(',');
				for(var j=0;j<arrayCodeP.length;j++){
					var s = arrayCodeP[j].substring(arrayCodeP[j].length-present[i].length,arrayCodeP[j].length);
					if(s==present[i]){
						continue;
					}
					str+=arrayCodeP[j]+",";
				}
				if(str.length>0)str=str.substring(0,str.length-1);
				if(str!=null && str!='undefined') $('#rwpPresentCodes').val(str);
			}
		}
		$('#'+id).remove();
		if($('#replaceWrongProductTable tr').length==1){
			$('#rwpSuerDiv').hide();
		}
		var arrayCode = $('#'+typeName).val().split(',');
		var str="";
		for(var i=0;i<arrayCode.length;i++){
			if(code==arrayCode[i]){
				continue;
			}
			str+=arrayCode[i]+",";
		}
		if(str.length>0)str=str.substring(0,str.length-1);
		$('#'+typeName).val(str);
	}	
}	

function preRwpSuerReplace(){
	var inputs = $('#rwProductForm input[type=text]');
	for(var i=0;i<inputs.length;i++){
		var inpu = inputs[i];
		if(!intTest(inpu.value)){
			alert('请输入正确的数字');
			return ;
		}
	}
	$.ajax({
    	type:"post",
		url:"afterSales.do?method=showProducts",
		dataType:'json',
		data:$('#rwProductForm').serialize(),
		success:function(json){
			if(json!=null){
				var asrob=json.asrob;
				var list= json.productList;
				$('#risRefundCode').html(asrob.code);
				$('#risOrderCode').html(asrob.orderCode);
				$('#risAfterCode').html(asrob.afterSaleOrderCode);
				//$('#risReturnRes').html($("#returnRes").find("option:selected").text());
				if($('#replaceWrongProduct').data("type")==1)
					$('#risReturnRes').html($("#returnRes").find("option:selected").text()=='其他'?$('#otherReturnRes').val():$("#returnRes").find("option:selected").text() );
				else
					$('#risReturnRes').html($("#returnResR").find("option:selected").text()=='其他'?$('#otherReturnResR').val():$("#returnResR").find("option:selected").text() );
				var html='<table width="96%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align="left" style="margin-left:10px" >';
				html+='<tr bgcolor="#4688D6" ><td><font color="#FFFFFF">商品编号</font></td><td><font color="#FFFFFF">商品名称</font></td>'
						+'<td><font color="#FFFFFF">商品分类</font></td>	<td><font color="#FFFFFF">商品金额</font></td>'	
				         +'<td><font color="#FFFFFF">退货数量</font></td> <td><font color="#FFFFFF">退货地区</font></td>'
				         +'<td><font color="#FFFFFF">批次号/批次价</font></td></tr>'
				var codes="";         
				for(var i=0;i<list.length;i++){
					var p=list[i];
					html+='<tr bgcolor="#F8F8F8"><td>'+p.code+'</td>';
					html+='<td>'+p.oriname+'</td>';
					html+='<td>'+p.parent1.name+'</td>';
					html+='<td>'+p.price+'</td>';
					html+='<td><input type="text" size="2" name="count'+p.id+'" value="'+(p.brand!=0?p.brand:1)+'"/></td>';
					html+='<td>&nbsp;</td>';
					html+='<td>&nbsp;</td></tr>';
					codes+=p.code+',';
				}
				codes=codes.substring(0,codes.length-1);
				html+="</table>"
				$('#risInnerDiv').html(html);
				$('#risProductCodes').val(codes);
				$('#risEntryStorageFormId').val($('#afterSaleId').val());
				dialogClose('replaceWrongProduct');
				rwpSuerReplace();
			}
	 	},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
	 });
}

function rwpSuerReplace(){
 	$("#replaceInStock").dialog({  
    	modal: true,
    	bgiframe: true,
    	title:'发错商品退货入库',
    	width:750,
    	buttons: {
      		 "退货入库": function() {
      		 	var inputs = $('#risEntryStorageForm input[type=text]');
				for(var i=0;i<inputs.length;i++){
					var inpu = inputs[i];
					if(!pIntText(inpu.value)){
						alert('请输入正确的数字');
						return ;
					}
				}
              	$.ajax({
			    	type:"post",
					url:"afterSales.do?method=repalceRefundProducts",
					dataType:'json',
					data:$('#risEntryStorageForm').serialize(),
					success:function(json){
						if(json.msg=='success'){
							dialogClose('replaceInStock');
							var type=$('#replaceWrongProduct').data("type");
							if(type==1){
								$('#entryForward').val(4);
								inStockAndShowForward();
							}else{
								$('#entryForward').val(1);//换货	
								replaceInAndShowForward();
							}	
						}else{
							alert(json.context);
						}
					},
					error:function(XMLHttpRequest, textStatus, errorThrown){
				   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
				    }
				 });
       	 	},
          	"返回上一步": function() {//返回检测中，费用单添加标签
          		$(this).dialog('close');
          		$('#crf_saveOrMondiry').html('<a href="javascript:void(0);" onclick="financeSumMonery()">修改</a>');
           		dialogOpen('replaceWrongProduct');
       	 	}
   		 },
   	dialogClass: 'alert'
    });
}

function deleteAfterSaleProduct(){//删除售后列表中商品
	var parmar="afterSaleOrderId="+$('#afterSaleId').val()+"&productIds=";
	var productIds="";
	var arrys=new Array();
	$("input[name='afterSaleProductId']:checkbox").each(function () { 
		if ($(this).attr("checked")) { 
			productIds+=$(this).attr("value")+","; 
			arrys.push($(this).attr("value"));
		}
	 });
	 if(productIds==""||productIds.length<=0){
	 	alert('请选择商品！');
	 	return ;
	 }
	 if(!confirm('确认删除售后商品吗？ ')){
	 	return null;
	 }
	 productIds=productIds.substring(0,productIds.length-1);
	 parmar+=productIds;
	 $.ajax({
    	type:"post",
		url:"afterSales.do?method=delUserOrderProducts",
		dataType:'json',
		data:parmar,
		success:function(json){
			if(json!=null){
				 if(json.msg=='success'){
				 	for(var i=0;i<arrys.length;i++){
				 		$('#asPtr'+arrys[i]).remove();
				 	}
				 }else if(json.msg=='more'){
				 	alert('不能全部删除！请先添加商品再删除。');
				 }else if(json.context=='entryStock'){
				 	alert('售后单已经执行入库操作。无法更换商品');	
				 }else{
				 	alert('数据库异常。请检查网络连接！');
				 }
			}else{
				alert('删除失败');
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
     });
}

function showAfterSaleProduct(orderId){
	var parmar="orderId="+orderId;
	$.ajax({
    	type:"post",
		url:"afterSales.do?method=showUserOrderProducts",
		dataType:'json',
		data:parmar,
		success:function(json){
			if(json!=null){
				 if(json.msg=='success'){
					 var  list = json.productList;
					 var html = showAddAfterDate(list);
					 var  presentList = json.presentList;
					 var pHtml = showAddAfterDate(presentList);
					 $('#aspdProduct').html('货品列表<br/>'+html);
					 if(pHtml!=''){
					 	$('#aspdPresent').html('赠品列表<br/>'+pHtml);
					 }
					 $("#afterSaleProductDiv").dialog({  
				    	modal: true,
				    	bgiframe: true,
				    	title:'添加售后商品',
				    	width:750,
				    	buttons: {
				      		 "确认": function() {
				      		 	 addAfterSaleProduct();	
				       	 	},
				          	"取消": function() { 
				          		$(this).dialog('close');
				       	 	}
				   		 },
				   	dialogClass: 'alert'
				    });	
				 }else{
				 	alert('显示列表失败');
				 }
			}else{
				alert('显示列表失败');
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
     }); 
}

function showAddAfterDate(list){
	if(list.length>0){
		 var html='<table width="95%" cellpadding="3" cellspacing="1" bgcolor="#e8e8e8" align=center>';
		 html+='<tr bgcolor="#4688D6"><td></td><td align="center"><font color="#FFFFFF">名称</font></td>';
		 html+='<td width="60" align="center"><font color="#FFFFFF">价格</font></td>';
		 html+='<td width="100" align="center"><font color="#FFFFFF">状态</font></td>';
		 html+='<td align="center" width=80><font color="#FFFFFF">编号</font></td>';
		 html+='<td align="center" width=80><font color="#FFFFFF">数量</font></td>';
		 html+='<td width="40" align="center"><font color="#FFFFFF">广速<br/>合格库</font></td>';
		 html+='<td width="40" align="center"><font color="#FFFFFF">芳村<br/>合格库</font></td>';
		 html+='<td width="40" align="center"><font color="#FFFFFF">增城<br/>合格库</font></td></tr>';
		 for(var i=0;i<list.length;i++){
		 	var p = list[i];
		 	var fangcun=0,beisu=0,zengcheng=0;
		 	for(var j=0;j<p.psList.length;j++){//获取北速2 芳村1 的库存数量
		 		var ps = p.psList[j];
		 		if(ps.type!=7&& ps.type!=8)
			 		if(ps.area==1){
		 				fangcun+=ps.stock;
			 		}else if(ps.area==2){
			 			beisu+=ps.stock;
			 		}else if(ps.area==3){
			 			zengcheng+=ps.stock;
			 		}
		 	}
		 	html+='<tr id="tr'+p.id+'"></td>';
		 	html+='<td><input type="checkBox" name="aspdProductId" value="'+p.id+'"/></td>'
		 	html+='<td align="center">'+p.name+'</td>';
		 	html+='<td align="center">'+p.price+'</td>';
		 	html+='<td align="center">'+p.statusName+'</td>';
		 	html+='<td align="center">'+p.code+'</td>';
		 	html+='<td align="center">'+p.rank+'</td>';
		 	html+='<td align="center">'+fangcun+'</td>';
		 	html+='<td align="center">'+beisu+'</td>';
		 	html+='<td align="center">'+zengcheng+'</td></tr>';
		 	//html+='<td align="center"><a href="javascript:void(0);" onclick="delAddProduct(\'tr'+p.id+'\',\''+p.code+'\')">删除</a></td></tr>';
		 }
		 return html;
	}else{
		return '';
	}
}
function addAfterSaleProduct(){
	if(!checkValidate('aspdProductId')){
		alert('请选择产品');
		return null;
	}
	$.ajax({
    	type:"post",
		url:"afterSales.do?method=addUserOrderProducts",
		dataType:'json',
		data:$('#aspdForm').serialize(),
		success:function(json){
			if(json!=null){
				 if(json.msg=='success'){
					 var  list = json.product;
					 var html="";
					 for(var i=0;i<list.length;i++){
					 	var p = list[i];
					 	html+='<tr id="asPtr'+p.id+'"><td> <input type="checkBox" name="afterSaleProductId" id="afterSaleProductId" value="'+p.id+'" /> </td>';
					 	html+='<td>'+p.code+'</td>'
					 	html+='<td>'+p.name+'</td>'
					 	html+='<td>'+p.parent+'</td>'
					 	html+='<td>'+p.price+'</td></tr>'
					}
					$('#afterSaleProductTable').append(html);
					dialogClose('afterSaleProductDiv');
				 }else{
				 	if(json.context=='exception'){
					 	alert('数据库异常。请检查网络连接!');
					 }else if(json.context=='exist'){
					 	alert('添上商品中有已经添加进售后列表中！请核对！');	
					 }else if(json.context=='entryStock'){
					 	alert('售后单已经执行入库操作。无法更换商品');	
					 }else{
					 	alert('错误');
					 }
				 } 
			}else{
				alert('显示列表失败');
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
     }); 
}

function checkResult(updateP,orderId,orderCode,afterSaleOrderId,afterSaleOrderCode){
	var param = "productCode="+updateP.split("&")[1]+"&afterSaleOrderCode="+updateP.split("&")[3];
	var historyStr = "";
	$.ajax({
		type:"post",
		url:"afterSales.do?method=getCheckResultHistory",
		dataType:'json',
		data:param,
		success:function(json){
			if(json!=null){
				if(json.msg=="success"){
					var history =json.history;
					for(var i=history.length;i>0;i--){
						historyStr += i+" "+history[i-1].checkResult+"    "+history[i-1].checkerName+"    "+history[i-1].operateTime.substring(0,16)+"\r\n";
					}
					$("#checkResultHistory").val(historyStr);
				}else{
					alert(json.context);
				}
			}else{
				alert('获取检测结果历史记录异常！！');
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
			alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
		}
	});
	$("#ly").dialog({
     	modal: true,
     	bgiframe: true,
     	width:600,
     	height:400,
     	title:"编辑检测结果",
     	buttons: {
			"添加检测结果": function() {
				var checkResult = $("#checkResult").val();
				var productId = updateP.split("&")[0];
				var productCode = updateP.split("&")[1];
				var productName = updateP.split("&")[2];
				if(trim(checkResult)==""){
					alert("请填写检测结果");
					return false;
				}
				$.post("afterSales.do?method=afterSaleCheckResult",
					{"orderId":orderId,"orderCode":orderCode,"afterSaleOrderId":afterSaleOrderId,"afterSaleOrderCode":afterSaleOrderCode,"productId":productId,"productCode":productCode,"productName":productName,"checkResult":checkResult},
					function(data){
						if(data<0){
							alert("添加检测结果失败！");
						}else{
							alert('添加检测结果成功！');
							$('#ly').dialog('close');
						}
					}
				);
				$("#checkResult").val(null);
			},
			"关闭": function() {
				$(this).dialog('close');
			}
     	},
     	dialogClass: 'alert'
     });
}
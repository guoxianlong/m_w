function change(dom){
	
	var value = $(dom).val();
	$.ajax({
		url:sysPath+'/monitoringController/getParamDelivery.mmx?pop='+value,    
		type:'post',
		success:function(data){
			$('#delivery').html("");
			//var returnData = $.parseJSON(data);//将字符串转换成对象
			var returnData = eval(data);//将字符串转换成对象
			$.each(returnData, function(i, obj){
				$('#delivery').append('<option value='+obj.id+'>'+obj.name+'</option>');
			});
			
		}
	});
	if(value==5){
		$("#delivery").prop("disabled",true);
		$("#failedReason").prop("disabled",false);
	}else{
		$("#delivery").prop("disabled",false);
		$("#failedReason").prop("disabled",true);
	}
}

$(function(){
	(function(){
		//查询按钮
		$("#searchBtn").on("click",function(){
			var pop = $.trim($("#pop").val());
			if(pop == 0){
				alert("请选择pop商家");
				return false;
			} 
			
			var startTime = $.trim($("#startTime").val());
			var endTime = $.trim($("#endTime").val());
			if((startTime=='' && endTime!='') || (startTime!='' && endTime=='')){
				alert('查询日期必须成对出现！');
				return false;
			}else if(startTime !='' && endTime !=''){
				 var startDate=new Date(startTime.replace("-", "/").replace("-", "/"));  
    			 var endDate=new Date(endTime.replace("-", "/").replace("-", "/"));  
    			 if(endDate < startDate){
    			 	alert('开始时间不能大于结束时间！');
    			 	return false;
    			 }
			}
			if($("#sendStatus").val()==2&&$("#failedReason").val()!=0){
				alert("成功状态时，无法选择失败原因！");
				return false;
			}
			$("#searchForm").submit();
		});
		
		//重置按钮
		$("#resetBtn").on("click",function(){
			$("input[name='code']").val("");
			$("input[name='startTime']").val("");
			$("input[name='endTime']").val("");
			$("select[name='sendStatus'] option:first").prop("selected","selected");
			$("select[name='failedReason'] option:first").prop("selected","selected");
			$("select[name='pop'] option:first").prop("selected","selected");
			$("select[name='delivery']").html("");
		});
		
		
		//全选
		$("#checkAll").on("click",function(){
			if($(this).prop("checked")){
				$("input[name='popOrderId']").prop("checked",true);
			}else{
				$("input[name='popOrderId']").prop("checked",false);
			}
		});
		
		$("input[name='popOrderId']").on("click",function(){
			if($(this).prop("checked")){
				$("input[name='popOrderId']").each(function(){
					if(!this.checked){
						$("#checkAll").prop("checked",false);
						return false;
					}
					$("#checkAll").prop("checked",true);
				});
			}else{
				$("#checkAll").prop("checked",false);
			}
		});

		
		//手动处理按钮
		$("#handleBtn").on("click",function(){
			var arr = new Array();
			var index = 0;
			//传送失败原因
			var failedReasons="";
			$("input[name='popOrderId']").each(function(){
				if(this.checked){
					arr[index] = $(this).val();
					failedReasons+=","+$("#failedReasonRs"+$(this).val()).val();
					index++;
				}
			});
			var size = arr.length;
			if(size == 0){
				alert("请选择要处理的面单！");
				return false;
			}
			
			var ids = "";
			for(index in arr){
				ids = ids + arr[index]+",";
			}
			
			if(ids.length > 0){
				ids = ids.substring(0,ids.length-1);
			}
			if($("#pop").val()!=5){
				alert("请选择京东的面单");
				return;
			}
			failedReasons=failedReasons.substr(1);
			$.ajax({
				type:"post",
				url:sysPath+"/monitoringController/manualHandleOrder.mmx",
				data:{'ids':ids,'failedReasons':failedReasons},
				success:function(str){
					if(str == "success"){
						alert("处理成功！");
						$("#searchForm").submit();
					}else if(str == "fail"){
						alert("处理失败！");
					}else if(str == "dataError"){
						alert("数据异常，请重新操作！");
					}
				}
			});
		});
		
		
		//页面跳转
		$(".pageJump").on("click",function(){
			var pageNum = $(this).attr("pageNum");
			$("#pageNum").val(pageNum);
			$("#searchForm").submit();
		});
	})();
})
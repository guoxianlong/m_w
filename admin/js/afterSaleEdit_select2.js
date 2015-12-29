var globalDiv ;

function movieFormatResult(textRes) {
    return textRes.content;
}

function movieFormatSelection(textRes,container) {
	if(globalDiv=='returnResHidR'){
		$('#returnResR').val(textRes.id);
		replaceResChoose(textRes.id)
	}else if(globalDiv=='returnResHid'){
		$('#returnRes').val(textRes.id);
 		returnResChoose(textRes.id);
	}else if('returnResHidE'==globalDiv){ //globalDiv 是搜索显示框的值
		$('#returnResEdit').val(textRes.id);
	}
 	   	
 	return textRes.content;
}
 
   
function toSelect2(div,content){
	globalDiv = div;
	var pathName=window.document.location.pathname;
	var projectName=pathName.substring(1,pathName.substr(1).indexOf('/')+1);
	if(content ==null || content=='undefined'){
		content ='请输入退换货原因';
	}
	var value ;
   	$("#"+div).select2({
       	   allowClear: false,
           placeholder:content,
           minimumInputLength: 2,
           ajax:{
			url: "/"+projectName+"/admin/afterSales.do?method=getBackTextRes",
			type:"post",
			dataType : 'json',
			quietMillis : 500,
			data : function(term, page) {
				value = term;
				return {
					qt : term, // 用户输入的字符串 getParameter("qt")
					page_limit : 10,
					apikey : "NoUse" // jsp页面中getParameter("apikey")可以得到 
				};
			},
			results : function(data, page) {
				if(data==null || data.list==null || data.list.length==0){//如果没有搜索记录 则用户输入的为原因
					var selection =$('#s2id_'+div);
					var container=selection.find("span");
					container.empty();
					selection.removeClass("select2-default");
					container.append(value);
					if(div=='returnResHidR'){
						$('#returnResR').val(0);
						$('#otherReturnResR').val(value);
					}else if(div=='returnResHid'){
						$('#returnRes').val(0);
						$('#otherReturnRes').val(value);
					}else if('returnResHidE'==div){
						$('#returnRes').val(0);
						$('#otherReturnResEdit').val(value);
					}
					
				}
				return {
					results : data.list
				};
			}
		},
		formatResult : movieFormatResult,
		formatSelection : movieFormatSelection

	});
}
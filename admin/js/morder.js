function showUnSaleSelect(val,type){
	var parma="type="+type;
	$.ajax({
		type:"post",
		url:"set/textDict.do?method=listDict",
		dataType:'json',
		data:parma,
		success:function(json){
			if(json!=null){
				var list = json.dictList;
				 
				var html='<select id="unSaleSelect" name="unSaleSelect" onchange="setRemark(document.getElementById(\'remark\'),\''+val+'\','+(type-8)+',\'unSaleSelect\')" >';
				html+='<option value="">请选择原因</option>';
				for(var i=0;i<list.length;i++){
					var bean=list[i];
					html+='<option value="'+bean.id+'">'+bean.content+'</option>';
				}
				html+='</select>';
				$('#unSaleTextDiv').html(val+'原因<font color="red">*</font>:');
				$('#unSaleSelectDiv').html(html);
			}else{
				alert('加载数据失败');
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
	});
}

function getUnSaleOption(){
	var value = parseInt($('#status').val())+8;
	if(value==9||value==10||value==15||value==16||value==11){ //状态
		showUnSaleSelect($('#status option:selected').text(),value);
	}else{
		$('#unSaleTextDiv').html('');
		$('#unSaleSelectDiv').html('');
	}
}

function showUnShippingSelect(val,type){
	var parma="type="+type;
	$.ajax({
		type:"post",
		url:"set/textDict.do?method=listDict",
		dataType:'json',
		data:parma,
		success:function(json){
			if(json!=null){
				var list = json.dictList;
				 
				var html='<select id="unShippingSelect" name="unShippingSelect" onchange="setRemark(document.getElementById(\'stockoutListRemark\'),\''+val+'\','+(type-1)+',\'unShippingSelect\')" >';
				html+='<option value="">请选择原因</option>';
				for(var i=0;i<list.length;i++){
					var bean=list[i];
					html+='<option value="'+bean.id+'">'+bean.content+'</option>';
				}
				html+='</select>';
				$('#unShippingTextDiv').html(val+'原因<font color="red">*</font>:');
				$('#unShippingSelectDiv').html(html);
			}else{
				alert('加载数据失败');
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	    }
	});
}

function getUnShipping(){
	var value = parseInt($('#stockoutDeal').val())+1;
	if(value==2||value==6||value==7){
		showUnShippingSelect($('#stockoutDeal option:selected').text(),value);
	}else{
		$('#unShippingTextDiv').html('');
		$('#unShippingSelectDiv').html('');
	}
}
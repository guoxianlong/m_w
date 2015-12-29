function getProductList(url,condition,divName){
	
    $.ajax({
	   type: "post",
	   url: url,
	   data: condition,
	   success: function(xml){
	      var boolFlag=true;
	   		
	   	  $(xml).find("success").each(function(i){
	   	  		if($(this).children("text").text()=='none'){
	   	  			$('#'+divName).html("没有找到该产品!");
	   	  			boolFlag=false;
	   	  			return;
	   	  		}else if($(this).children("text").text()=='fail'){
	   	  			$('#'+divName).html("数据异常!");
	   	  			boolFlag=false;
	   	  			return;
	   	  		}
	   	  });
	   	  
	   	  var html="<table width='400px' style='border:1px solid black;border-top:0px;border-left:0px;'>"; 	
          $(xml).find("product").each(function(i){ 
         		var id=$(this).attr("id");
         		var code=$(this).attr("code");
         		var name=$(this).attr("name");
         		var productLine=$(this).attr("productLine");
         		html+="<tr><td width='10%' align='center'><input type='checkBox' name='productId' id='productId' value='"+id+"'/>"+code+"</td>";
         		html+="<td width='10%' align='center'><a href='../fproduct.do?id="+id+"' target='_blank'>"+name+"</a></td>";
         		html+="<td width='10%' align='center'>"+productLine+"</td></tr>";
          });
          html+="</table>";
          if(boolFlag)
          	$('#'+divName).html(html);
	   },
	   error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert("数据解析有误!无法显示");
	   }
	});
}
 
 function changeType(index){
	 var firstType  = document.getElementById("firstType");
	 var secondType = document.getElementById("secondType");
	 var thirdType  = document.getElementById("thirdType");
	 var endType    = document.getElementById("endType");
	 secondType.options.length=0;
	 for(i=0;i<firstType.length;i++){ 
		if(firstType[i].selected==true){
	       if(firstType.options[0].value==0){
		      secondType.style.display="none";
		      thirdType.style.display="none";
		      endType.style.display="none";
	       }
	       if(firstType.options[1].value==i){
		      secondType.options.add(new Option("不限","0"),1); 
		      secondType.options.add(new Option("售前咨询","1"),2); 
		      secondType.options.add(new Option("售后咨询","2"),3); 
	          secondType.style.display="";
	       }
	       if(firstType.options[2].value==i){
		      secondType.options.add(new Option("不限","0"),1); 
		      secondType.options.add(new Option("订购查询","1"),2); 
		      secondType.options.add(new Option("售后查询","2"),3);
		     //secondType.options.add(new Option("发货查询","3"),4);  
	          secondType.style.display="";
	       }
	       if(firstType.options[3].value==i){
		      secondType.options.add(new Option("不限","0"),1); 
		      secondType.options.add(new Option("商品投诉","1"),2); 
		      secondType.options.add(new Option("服务投诉","2"),3); 
		      secondType.options.add(new Option("泄密投诉","3"),4);
		      secondType.options.add(new Option("在线支付投诉","4"),5); 
	          secondType.style.display="";
	       }
	       if(firstType.options[4].value==i){
		      secondType.options.add(new Option("不限","0"),1); 
		      secondType.options.add(new Option("投诉回访","1"),2); 
		      secondType.options.add(new Option("服务回访","2"),3); 
		      secondType.options.add(new Option("调查回访","3"),4); 
	          secondType.style.display="";
	       }
        }   
	}
	if(index!=null&&index!='undefined' && index!=0)
		secondType.options[index].selected=true;
}
function changeType1(index){
	 var secondType = document.getElementById("secondType");
	 var thirdType = document.getElementById("thirdType");
	 var endType = document.getElementById("endType");
	 thirdType.options.length=0;
	 for(j=0;j<secondType.length;j++){
		if(secondType[j].selected==true){
	       if(secondType.options[0].value==0){
	    	   thirdType.style.display="none";
			   endType.style.display="none";
	       }
	       if(secondType.options[2].value==j && secondType.options[2].text=="售后查询"){
	    	   thirdType.options.add(new Option("不限","0"),1); 
	    	   thirdType.options.add(new Option("退货查询","1"),2); 
	    	   thirdType.options.add(new Option("换货查询","2"),3); 
	    	   thirdType.options.add(new Option("维修查询","3"),4);
	    	   thirdType.options.add(new Option("补发查询","4"),5);  
	           thirdType.style.display="";
	       }
	       if(secondType.options[1].value==j && secondType.options[1].text=="商品投诉"){
	    	   thirdType.options.add(new Option("不限","0"),1); 
	    	   thirdType.options.add(new Option("质量投诉","1"),2); 
	    	   thirdType.options.add(new Option("外观投诉","2"),3); 
	    	   thirdType.options.add(new Option("功能投诉","3"),4);
	    	   thirdType.options.add(new Option("客户原因","4"),5); 
	           thirdType.style.display="";
	       }
	       if(secondType.options[2].value==j && secondType.options[2].text=="服务投诉"){
	    	   thirdType.options.add(new Option("不限","0"),1); 
	    	   thirdType.options.add(new Option("物流服务投诉","1"),2); 
	    	   thirdType.options.add(new Option("售后服务投诉","2"),3); 
	    	   thirdType.options.add(new Option("销售服务投诉","3"),4); 
	    	   thirdType.options.add(new Option("前台服务投诉","4"),5); 
	    	   thirdType.options.add(new Option("在线服务投诉","5"),6); 
	           thirdType.style.display="";
	       }
       }   
	}
	if(index!=null&&index!='undefined' && index!=0)
		thirdType.options[index].selected=true;
}

function changeType2(index){
	 /**
	 var thirdType = document.getElementById("thirdType");
	 var endType = document.getElementById("endType");
	 endType.options.length=0;
	 for(j=0;j<thirdType.length;j++){
		if(thirdType[j].selected==true){
	       if(thirdType.options[0].value==0){
			  endType.style.display="none";
	       }
	       if(thirdType.options[3].value==j ){
	    	   endType.options.add(new Option("不限","0"),1); 
	    	   endType.options.add(new Option("网站内容投诉","1"),2); 
	    	   endType.options.add(new Option("服务坐席投诉","2"),3); 
	           endType.style.display="";
	       }
        }   
	}
	if(index!=null&&index!='undefined' && index!=0)
		endType.options[index].selected=true;
	**/	
}

function checkboxChecked(checkbox,value){
	var values = value.split(",");
	for(var j = 0; j < values.length; j++){
		if(values[j]!=null && values[j]!="" && values[j]!='undefined'){
			for(var i = 0; i < checkbox.length; i++){
				if(checkbox[i].value == values[j]){
					checkbox[i].checked = true;
				}
			}
		}
	}
}

function changCheck(start,index,end){
	var one = document.getElementsByName(start);
	var more = document.getElementsByName(end);
	for(var i=0;i<more.length;i++){
		more[i].checked=one[index].checked;
	}
}

function changCheckByMore(start,index,end){
	var one = document.getElementsByName(start);
	var more = document.getElementsByName(end);
	for(var i=0;i<more.length;i++){
		if(more[i].checked){
			one[index].checked=more[i].checked; //只改变成为选中状态
			break;
		}
	}
}

function showReport(divName,titleNames,xAxisNames,jsonValue,type){
	var chart = new Highcharts.Chart({
		chart: {
			renderTo: divName,
			defaultSeriesType: 'bar'
		},
		title: {
			text: titleNames
		},
		xAxis: {
			categories:xAxisNames,
			 min: 0
		},
		yAxis: {
			min: 0,
			title: {
				text: ''
			}
		},
		legend: {
			backgroundColor: '#FFFFFF',
			reversed: false
		},
		credits:{
			href:'http://www.maimaibao.com',
			text:'mmb'
		},
		plotOptions: {
			bar: {
				dataLabels: {
					enabled: true
				}
			},
			series: {
	            cursor: 'pointer',
	            point: {
	                events: {
	                    click: function() {
	                        //sort(this.category,type);
	                    }
	                }
	            }
	        }
		},
		tooltip: {
			formatter: function() {
				return ''+ this.y +'';
			}
		},
		series: [{
			data:jsonValue
		}]
	});
}

function submitForm(formname){
	$.ajax({
		type:"POST",
		url:'callWork.do?method=callWorkReport',
		dataType:'json',
		data:$('#'+formname).serialize(),
		success:function(json){
			json = eval(json);
			var msg = json.message;
			var type=json.type;
			if(msg=="success"){
				var divName="";
				var titleName="";
				if(type==1){
					divName='totalDateShow';
					titleName='';
					var xAxisName=['<a href="javascript:sort(\'咨询类\',1)">咨询类</a>','<a href="javascript:sort(\'查询类\',1)">查询类</a>',
					    '<a href="javascript:sort(\'投诉类\',1)">投诉类</a>','<a href="javascript:sort(\'用户回访\',1)">用户回访</a>','销售中心','补发票'];
					showReport(divName,titleName,xAxisName,json.series,type);
					$('#bigTotalDateShow').show();
					$('#noLimitDiv').hide();
					$('#storDivShow').hide();
					$('#storDivShow1').hide();
					$('#storDivShow2').hide();
					$('#midDiv1').hide();
					$('#midDiv2').hide();
					$('#midDiv3').hide();
					$('#midDiv').hide();
					$('#thridDivShow').hide()
				}else if(type==2){
					divName='storDivShow';
					titleName='';
					showReport(divName,titleName,json.xAxisName,json.series,type);
					$('#storDivShow').show();
					$('#midDiv').show();
					$('#thridDivShow').hide()
				}else if(type==3){
					$('#bigTotalDateShow').hide();
					$('#noLimitDiv').show();
					$('#storDivShow').hide();
					$('#storDivShow1').hide();
					$('#storDivShow2').hide();
					$('#midDiv').hide();
					$('#thridDivShow').hide()
					showReport('totalDateShow1',titleName,json.xAxisName0,json.series0,type);
					showReport('totalDateShow2',titleName,json.xAxisName1,json.series1,type);
					showReport('totalDateShow3',titleName,json.xAxisName2,json.series2,type);
					showReport('totalDateShow4',titleName,json.xAxisName3,json.series3,type);
				}else if(type==4){
					$('#storDivShow').show();
					$('#storDivShow1').show();
					$('#thridDivShow').hide()
					showReport('storDivShow',titleName,json.xAxisName0,json.series0,type);
					showReport('storDivShow1',titleName,json.xAxisName1,json.series1,type);
					if((json.series2!='undefined'||json.series2!=null || json.series2!="" )&& json.series2.length>0){
						$('#storDivShow2').show();
						showReport('storDivShow2',titleName,json.xAxisName2,json.series2,type);
					} 
				}else if(type==5){
					titleName='';
					showReport('thridDivShow1',titleName,json.xAxisName,json.series,type);
				}else if(type==6){
					titleName='';
					showReport('thridDivShow1',titleName,json.xAxisName0,json.series0,type);
					showReport('thridDivShow2',titleName,json.xAxisName1,json.series1,type);
					showReport('thridDivShow3',titleName,json.xAxisName2,json.series2,type);
					showReport('thridDivShow4',titleName,json.xAxisName3,json.series3,type);
				}
				$('#loadingDiv').hide();
			}else{
				alert("数据库异常！");
			}
		},
		error:function(XMLHttpRequest, textStatus, errorThrown){
	   		alert(XMLHttpRequest+"  "+errorThrown+"  "+textStatus);
	   }
	});
}

function sort(typeName,type){
	$('#secondTypeHis').val('');
	if(type==1){
		var firstType=getFirstTypeNames(typeName);
		$('#firstTypeHis').val(firstType);
		$('#midDiv').html(typeName+'工单统计');
		$('#loadingDiv').show();
		submitForm('callWorkFormHis');
	}else if(type==3){
		 var firstType=getFirstTypeNames(typeName);
		 if(firstType==0) return;
		 $('#firstTypeHis').val(firstType);
		 $('#loadingDiv').show();
		 submitForm('callWorkFormHis');
		 $('#midDiv1').show();
		 $('#midDiv2').show();
		 $('#midDiv3').show();
		 if(firstType==1){
		 	$('#midDiv1').html('售前咨询');
		 	$('#midDiv2').html('售后咨询');
		 	$('#midDiv3').hide();$('#storDivShow2').hide();
		 }else if(firstType==2){
		 	$('#midDiv1').html('订购查询');
		 	$('#midDiv2').html('售后查询');
		 	$('#midDiv3').hide();$('#storDivShow2').hide();
		 }else if(firstType==3){
		 	$('#midDiv1').html('<a href="javascript:secondSort(3,1,5);">商品投诉</a>');
		 	$('#midDiv2').html('<a href="javascript:secondSort(3,2,5);">服务投诉</a>');
		 	$('#midDiv3').html('泄密投诉');
		 	$('#storDivShow2').show();
		 }else if(firstType==4){
		 	$('#midDiv1').html('投诉回访');
		 	$('#midDiv2').html('服务回访');
		 	$('#midDiv3').html('调查回访');
		 	$('#storDivShow2').show();
		 }
	}else{
		//alert("提示并不比可悲!");
	}
}

//type 为了以后扩展使用  注 若以后三级分类新增了类别。可以根据firsttype 和seondType经行判断
function secondSort(firstType,secondType,type){
	$('#firstTypeHis').val(firstType);
	$('#secondTypeHis').val(secondType);
	$('#loadingDiv').show();
	submitForm('callWorkFormHis');
	if(type==1){
		if(secondType==1)
			$('#thridDivTextShow1').html('商品投诉');
		else
			$('#thridDivTextShow1').html('服务投诉');
			
	 	$('#thridDivShow').show();
	}else{
		if(secondType==1){
			$('#thridDivTextShow1').html('质量投诉');
			$('#thridDivTextShow2').html('外观投诉');
			$('#thridDivTextShow3').html('功能投诉');
			$('#thridDivTextShow4').html('客服原因');
		}else{
			$('#thridDivTextShow1').html('物流客服投诉');
			$('#thridDivTextShow2').html('售后服务投诉');
			$('#thridDivTextShow2').html('销售服务投诉');
			$('#thridDivTextShow4').html('前台服务投诉');
		}	
	 	$('#thridDivShow').show();
	}
	
}

function getFirstTypeNames(type){
	var firstType=0;
	if(!intTest(type)){
		if(type=="咨询类"){
			firstType=1;
		}else if(type=="查询类"){
			firstType=2;
		}else if(type=="投诉类"){
			firstType=3;
		}else if(type=="用户回访"){
			firstType=4;
		}else if(type=="销售中心"){
			firstType=5;
		}
	}else{
		if(type==1){
			firstType="咨询类";;
		}else if(type==2){
			firstType="查询类";
		}else if(type==3){
			firstType="投诉类";
		}else if(type==4){
			firstType="用户回访";
		}else if(type==5){
			firstType="销售中心";
		}
	}
	return firstType;
}
 
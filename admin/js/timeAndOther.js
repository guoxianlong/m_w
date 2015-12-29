  function getDateFromString(strDate){
       var arrYmd   =  strDate.split("-");
       var numYear  =  parseInt(arrYmd[0],10);
       var numMonth =  parseInt(arrYmd[1],10)-1;
       var numDay   =  parseInt(arrYmd[2],10);
       var leavetime=new Date(numYear,  numMonth,  numDay);
       return leavetime;
  }
  
  function getValidateSubDays(endTime,startTime){
  	 return (getDateFromString(endTime)-getDateFromString(startTime))/(1000*60*60*24);
  }
  
  function validateSubDate(endTime,startTime){
  	var day = getValidateSubDays(endTime,startTime);
	if(day<0){
		alert("结束时间必须大于开始时间");
		return false;
	}
	if(day>30){
		alert("日期时间段不得超过31天,请重新填写！");
		return false;
	} 
	return true;
  }
  
 function validateDate(endTime,startTime){
  	var day = getValidateSubDays(endTime,startTime);
	if(day<0){
		return false;
	}
	return true;
  }
  
  function CheckDate(strDate){  
	if(strDate.length!=10){
	   alert("日期格式错误，请重新填写！");  
       return false;
	}
	
 	var reg=/^([1-2]\d{3})[\/|\-](0?[1-9]|10|11|12)[\/|\-]([1-2]?[0-9]|0[1-9]|30|31)$/ig;
  	if(!reg.test(strDate)){  
        alert("日期格式错误，请重新填写！");  
        return false;  
  	}  
      return true;  
   }
   
   // least 为全选 more为其他 多选框
   function chageChecked(least,more){
		var deliverAmount = document.getElementsByName(least);
		var deliverValue =  document.getElementsByName(more); 
		for(var i=0;i<deliverValue.length;i++){
			if(deliverValue[i].disabled==false){
				deliverValue[i].checked=deliverAmount[0].checked;
			}
		}
	}
	
	function checkedAll(more,least){ //通过点击boutton 让more 变成全选状态 least  多选框
		var one = document.getElementsByName(least);
		var all =  document.getElementsByName(more); 
		for(var i=0;i<all.length;i++){
			all[i].checked=true;
		}
		try{
			one[0].checked=true;
		}catch(err){};
	}
	
	function firstChangeChecked(least,more){//least 为全选或反选  more没有全部选中least 则去掉选中状态 多选框
		var deliverAmount = document.getElementsByName(least);
		var deliverValue =  document.getElementsByName(more);
		var flag = true; 
		for(var i=0;i<deliverValue.length;i++){
			if(deliverValue[i].checked==false){
				flag=false;
			}
			if(!flag) break;
		}
		deliverAmount[0].checked=flag;
	}
	
	function checkValidate(id){ //验证是否选中 多选框
		var brandValues = document.getElementsByName(id);
		var flag=false;
		for(var i=0;i<brandValues.length;i++){
			if(brandValues[i].checked==true){
				flag=true;
			}
			if(flag) break;
		}
		return flag;
	}
	
	function checkValidateAndValus(id){//验证是否选中，并返回选中的值 多选框
		var brandValues = document.getElementsByName(id);
		var values="";
		for(var i=0;i<brandValues.length;i++){
			if(brandValues[i].checked==true && brandValues[i].value!=""){
				 values+=brandValues[i].value+","
			}
		}
		return values;
	}
	
	function selectedOptions(id,value){
		var select = document.getElementById(id);
		for(var i = 0; i < select.length; i++){
			if(select.options[i].value == value){
				select.options[i].selected = true;
				return i;
			}
		}
		return 0;
	}
		
	String.prototype.trim = function() {    //去掉左右空格
	    var r = this.replace(/^\s+|\s+$/g, "");    
	    r = Lremoveblank(r);    
	    r = Rremoveblank(r);    
	    return r;    
	}    
	   
	function Lremoveblank(s) {    
	    if (s.length == 1 && s.charCodeAt(0) == 160)    
	        return "";    
	    if (s.charCodeAt(0) == 160) {    
	        s = s.substr(1, s.length - 1);    
	        return Lremoveblank(s);    
	    }    
	    else {    
	        return s;    
	    }    
	}    
	   
	function Rremoveblank(s) {    
	    if (s.length == 1 && s.charCodeAt(0) == 160)    
	        return "";    
	    if (s.charCodeAt(s.length-1) == 160) {    
	        s = s.substr(0, s.length - 1);    
	        return Rremoveblank(s);    
	    }    
	    else {    
	        return s;    
	    }    
	} 	
	 
	 //判断两个日期是否在同 一年和一个月份
	function validateSameMonth(date1,date2){
		var firstYmd =  date1.split("-");
		var secondYmd =  date2.split("-");
		try{
			if(firstYmd[0]== secondYmd[0]){
				if(firstYmd[1]== secondYmd[1]){
					return true;
				}else{
					return false;
				}
			}else{
				return false;
			}
		}catch(err){
			return false;
		}
	}
	
	//选中 value中的直
	function checkboxChecked(checkbox,value){
		if(value==null||value.trim()==""||value=='undefined') return;
		var values = value.split(",");
		for(var j = 0; j < values.length; j++){
			for(var i = 0; i < checkbox.length; i++){
				if(checkbox[i].value == values[j]){
					checkbox[i].checked = true;
				}
			}
		}
	}
 	
	function dollarTest(num){//判断是否为浮点数（保留两位小数） 或为整数 
		if(num==null||num=="") return false;
	    var reFloat=/^\d+(|\.\d{1,2})+$/;
	    if(!reFloat.exec(num))return false;
		return true;
	}
	function dollarTest1(num){//判断是否为浮点数（保留两位小数） 或为整数 
		if(num==null||num=="") return false;
	    var reFloat=/^\d+(|\.\d{1,3})+$/;
	    if(!reFloat.exec(num))return false;
		return true;
	} 
	function floatTest(num){//判断是否为浮点数 或为整数 1.0 0.2
		if(num=="") return true;
	    var reFloat=/^\d+(|\.\d{1})+$/;
	    if(!reFloat.exec(num))return false;
		return true;
	 }
	
	function intTest(num){//只能输入大于0的整数 
		if(num=="") return false;
		if(num==0) return false;
	    var reFloat=/^\d+$/;
	    if(!reFloat.exec(num))return false;
		return true;
    }
    
	function pIntText(num){//正整数 
	    var reFloat=/^\d{1,9}$/;
	    if(!reFloat.exec(num))return false;
		return true;
    }
    
	 //去掉radios 中选中状态
	 function romveRadiosOption(radio){
	 	var radios = document.getElementsByName(radio);
		for(var i = 0; i < radios.length; i++){
			radios[i].checked = false;
		}
		return false;
	}
	
	//选中radios 
	 function selectRadiosOption(radio,value){
	 	var radios = document.getElementsByName(radio);
		for(var i = 0; i < radios.length; i++){
			if(radios[i].value==value)
				radios[i].checked = true;
		}
		return false;
	}
	
	function getRadiosValue(radio){
		var radios = document.getElementsByName(radio);
		if(radios==null || radios=="undefined")
			return "";
		for(var i = 0; i < radios.length; i++){
			if(radios[i].checked == true)
				return radios[i].value;
		}
		return "";
	}
	//验证电话号码 可以用逗号分割 或- 分割 
	function isTelephone(obj){ 
		obj=obj.replace(/\-/g,'');
		obj=obj.replace(/\,/g,'');
		var pattern=/^\d+$/; 
		if(pattern.test(obj)){ 
			return true; 
		}else{ 
			return false; 
		} 
		return false; 
	}
	
	//改变div背景颜色
	
	function changeBackColor(id){
		var div=document.getElementById(id);
		div.style.backgroundColor='#FF9900';
	}
	
	function displayBackColor(id){
		var div=document.getElementById(id);
		div.style.backgroundColor='';
	}
	
	function convertNull(str){
		if(str==null || str=='undefined'|| str=='null'){
			return '';
		}
		return str;
	}
	
	//---------------------------------js 浮点数精度-------------------------------------------
	function accuracySub(arg1,arg2){  
	     var r1,r2,m,n;  
	     try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}  
	     try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}  
	     m=Math.pow(10,Math.max(r1,r2));  
	     //last modify by deeka  
	     //动态控制精度长度  
	     n=(r1>=r2)?r1:r2;  
	     return ((arg2*m-arg1*m)/m).toFixed(n);  
	}  
	///给number类增加一个sub方法，调用起来更加方便  
	Number.prototype.sub = function (arg){  
	    return accuracySub(arg,this);  
	}
	function accuracyDiv(arg1,arg2){  
    var t1=0,t2=0,r1,r2;  
    try{t1=arg1.toString().split(".")[1].length}catch(e){}  
    try{t2=arg2.toString().split(".")[1].length}catch(e){}  
    with(Math){  
        r1=Number(arg1.toString().replace(".",""));  
        r2=Number(arg2.toString().replace(".",""));  
        return (r1/r2)*pow(10,t2-t1);  
    }  
	}  
	 
	Number.prototype.div = function (arg){  
	    return accuracyDiv(this, arg);  
	};  
	//乘法函数，用来得到精确的乘法结果  
	//说明：javascript的乘法结果会有误差，在两个浮点数相乘的时候会比较明显。这个函数返回较为精确的乘法结果。  
	//调用：accMul(arg1,arg2)  
	//返回值：arg1乘以arg2的精确结果  
	function accuracyMul(arg1,arg2)  
	{  
	    var m=0,s1=arg1.toString(),s2=arg2.toString();  
	    try{m+=s1.split(".")[1].length}catch(e){}  
	    try{m+=s2.split(".")[1].length}catch(e){}  
	    return Number(s1.replace(".",""))*Number(s2.replace(".",""))/Math.pow(10,m);  
	}  
	 
	Number.prototype.mul = function (arg){  
	    return accuracyMul(arg, this);  
	};  
	//加法函数，用来得到精确的加法结果  
	//说明：javascript的加法结果会有误差，在两个浮点数相加的时候会比较明显。这个函数返回较为精确的加法结果。  
	//调用：accAdd(arg1,arg2)  
	//返回值：arg1加上arg2的精确结果  
	function accuracyAdd(arg1,arg2){  
	    var r1,r2,m;  
	    try{r1=arg1.toString().split(".")[1].length}catch(e){r1=0}  
	    try{r2=arg2.toString().split(".")[1].length}catch(e){r2=0}  
	    m=Math.pow(10,Math.max(r1,r2));  
	    return (arg1*m+arg2*m)/m;  
	}  
	 
	Number.prototype.add = function (arg){  
	    return accuracyAdd(arg,this);  
	}  
	
	//显示不同颜色的div 需要有 css cools 样式
	function showDifferentColoresDiv(num){
		 for(var i=1;i<num;i++){
	    	$('#div'+i).mouseover(function(){
	    		$('#div'+s).removeClass('cools');
	    		$(this).addClass('cools');
	    	});
	    	$('#div'+i).mouseout(function(){
	    		$(this).removeClass('cools');
	    		$('#div'+s).addClass('cools');
	    	});
	    }
	}
	
	//比较两个日期 含分秒时 st1是否大于st2 2012-02-2
	function compareDate(st1,st2){
		var dt1=new Date(st1.replace(/-/g, "/"));  
        var dt2=new Date(st2.replace(/-/g, "/"));  
        return dt1>dt2;
	}
	
	//电话号码判断
	function isTelephone(Telephone) {
	    var regexpMobile = /(^\d{3,4}\-\d{3,8}$)|(^\d{3,8}$)|(^\(\d{3,4}\)\d{3,8}$)|(^\d{7,12}$)|((^\d{4}|\d{3})-(\d{7,8})-(\d{4}|\d{3}|\d{2}|\d{1})$)/
	    return regexpMobile.test(Telephone);
	}
	
	function forward(url){
		document.location.href=url;
	}
	
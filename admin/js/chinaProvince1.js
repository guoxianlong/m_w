//因为 方法路径写死了。 对于admin下子文件夹不能 进入jsp页面。

function Ajax(selectType,id){   
    $.get("../getCityAjax.jsp",{method:selectType,pranetId:id},function(str){
    	explainXML(str,selectType);
    });
    
}


function removeStreetOption(selectOptions){
    var streetSel = document.getElementById(selectOptions);;
    var streetLen = streetSel.options.length;
	streetLen-=1;
    while(streetLen>0){
	   	streetSel.remove(streetLen);
	   	streetLen-=1;
	}
}

var x ;
function explainXML(o,ids){
	var xmlDoc = o.documentElement; //xml文件
	var items = xmlDoc.getElementsByTagName("name");
	var selectOption = document.getElementById(ids);//名称 select
	for(var i =0;i<items.length;i++){  
		var y = items[i];
		var option = new Option(y.childNodes[0].nodeValue,y.getAttribute("value"));
		selectOption.options.add(option);
	}
}

function getCitys(){
	var value = document.getElementById('sheng').value;
	if(value==null|| value=="" || value=="undefined")
		return;
	Ajax('shi',value) 	
}

function getVillages(){
	var value = document.getElementById('shi').value;
	if(value==null|| value=="" || value=="undefined")
		return;
	Ajax('qu' ,value);
}

function getStreets(){
	var value = document.getElementById('qu').value;
	if(value==null|| value=="" || value=="undefined")
		return;
	 Ajax('streetId' ,value);				
}


function onLoadProvince(){
	Ajax('sheng',0);
}

/*
	自动匹配邮编
*/
function codeAuto(){
	//1.获取文本框中的值
	//var text = $("#_fmm.d._0.c").val();
	var text1=document.getElementById('sheng').options[document.getElementById('sheng').selectedIndex].text//省
	var text=document.getElementById('shi').options[document.getElementById('shi').selectedIndex].text//市
	text = text.trim(); //数据库中数据导入的时候 有些会有空格   去掉空格
             	 
             	
	//alert(text1+'--'+text);
	//2.将文本框中的值发给服务器
	/*
	$.post("postCodeAuto.jsp", {sheng:text1,word:text}, function(responsedata) {
		//当与服务器端的交互结束，数据成功返回之后，这个方法会被执行
		//3.接收服务器的响应结果，responsedata中可以获得服务器端返回的数据
		//4.将服务器的返回结果显示在页面上
		//alert(responsedata);
		document.all.postcode.value=responsedata;
	});
	*/
	var call =   
		{   
	        //正常返回处理函数   
			success: function(responsedata){
				document.all.postcode.value=responsedata.responseText;
			},   
	         //出错返回处理函数    
			failure: function(){
				
			},   
			argument: {} //可以在success函数和failure函数中访问的变量   
		};   
    //AJAX GET请求   
    //YAHOO.util.Connect.setForm("aorderForm");
    var transaction = YAHOO.util.Connect.asyncRequest('POST', "../postCodeAuto.jsp", call, "sheng=" + text1 + "&word=" + text);   
}


String.prototype.trim = function() {    
    var r = this.replace(/(^s*)|(s*$)/g, "");    
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
function Ajax(selectType,id){ 
	var pathName=window.document.location.pathname;
	var projectName=pathName.substring(1,pathName.substr(1).indexOf('/')+1);
    $.get("/"+projectName+"/admin/getCityAjax.jsp",{method:selectType,pranetId:id},function(str){
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
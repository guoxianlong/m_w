	var xmlhttp;
	function Ajax(id,names,status){
		if (window.XMLHttpRequest){// code for IE7+, Firefox, Chrome, Opera, Safari
		  xmlhttp=new XMLHttpRequest();
		}else{// code for IE6, IE5
		  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		}
		
		var url="" ;
		if(names!='sheng' &&names!='shi' && names!='provinces'&& names!='city'){
			if(status==null   || status=='undefined'){
				status=="";
			}
			url = 'productAjax.jsp?supplierId='+id+'&status='+status;
			xmlhttp.onreadystatechange=function(){
		        if (xmlhttp.readyState==4 && xmlhttp.status==200){
			  		var xmlDoc=xmlhttp.responseXML;
			   	    explainXML(xmlDoc,names)
		        }
		    }
		}else if(names=='provinces' ||  names=='city'){
			$.get("getCityAjax.jsp",{method:names,pranetId:id},function(str){
				explainCityXML(str,names);
		    });

		}else {
			url ='getCityAjax.jsp?method='+names+'&pranetId='+id;
			xmlhttp.onreadystatechange=function(){
			    if (xmlhttp.readyState==4 && xmlhttp.status==200){
			  		var xmlDoc=xmlhttp.responseXML;
			   	    explainCityXML(xmlDoc,names)
			    }
			}
		}
		xmlhttp.open("GET",url,false);
		xmlhttp.send();
	}
	
	function onloadSupplier(names,status){
		var value = document.getElementById("productLine").value;
		if(value!=null && value!="undefined"){
			if(value=="" ||value.length==0){
				value=0;
			}
			Ajax(value,names,status);
		}	
	}
	
	function onLoadProvince(){
		Ajax(0,'sheng','');
	}
	
	function onLoadProvinceSeconde(){
		Ajax(0,'provinces','');
	}
	
	function getCitySeconde(){
		var value = document.getElementById('provinces').value;
		if(value==null|| value=="" || value=="undefined")
			return;
		Ajax(value,'city',''); 	
	}
	
	function getCitys(){
		var value = document.getElementById('sheng').value;
		if(value==null|| value=="" || value=="undefined")
			return;
		Ajax(value,'shi'); 	
	}
	
	function explainXML(o,ids){
		var xmlDoc = o.documentElement; //xml文件
		var items = xmlDoc.getElementsByTagName("name");
		var selectOption = document.getElementById(ids);//名称 select
		for(var i =0;i<items.length;i++){  
			var y = items[i];
			var option = new Option(y.childNodes[0].nodeValue,y.childNodes[0].nodeValue);
		    selectOption.options.add(option);
		    
		}
	}

	function explainCityXML(o,ids){
		var xmlDoc = o.documentElement; //xml文件
		var items = xmlDoc.getElementsByTagName("name");
		var selectOption = document.getElementById(ids);//名称 select
		for(var i =0;i<items.length;i++){  
			var y = items[i];
			var option = new Option(y.childNodes[0].nodeValue,y.getAttribute("value"));
			selectOption.options.add(option);
		}
	}
	
	function removeSupplier(supplier){
	    var streetSel = document.getElementById(supplier);;
	    var streetLen = streetSel.options.length;
		streetLen-=1;
	    while(streetLen>0){
		   	streetSel.remove(streetLen);
		   	streetLen-=1;
		}
	}
	
	
	function getVillages(){
		var value = document.getElementById('shi').value;
		if(value==null|| value=="" || value=="undefined")
			return;
		Ajax(value,'qu');
	}

	function getStreets(){
		var value = document.getElementById('qu').value;
		if(value==null|| value=="" || value=="undefined")
			return;
		 Ajax(value ,'streetId');				
	}
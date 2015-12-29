
var XMLHttpReq, pid;                           //创建XMLHttpRequest对象
function createXMLHttpRequest() {
	if (window.XMLHttpRequest) {                               
  		//Mozilla浏览器
		XMLHttpReq = new XMLHttpRequest();
	} else {
		if (window.ActiveXObject) {                           
    	//IE浏览器
			try {
				XMLHttpReq = new ActiveXObject("Msxm12.XMLHTTP");
			}
			catch (e) {
				try {
					XMLHttpReq = new ActiveXObject("Microsoft.XMLHTTP");
				}
				catch (e) {
				}
			}
		}
	}
}  
  //发送Ajax请求
function sendAjaxRequest(url) {
	createXMLHttpRequest();                         //创建XMLHttpRequest对象
	XMLHttpReq.open("post", url, false);
	XMLHttpReq.onreadystatechange = processResponse;//指定响应函数
	XMLHttpReq.send(null);
}

  //回调函数processResponse
function processResponse() {
	if (XMLHttpReq.readyState == 4) {
		if (XMLHttpReq.status == 200) {
			var options = XMLHttpReq.responseXML.getElementsByTagName("option");
			var firstLevel = document.getElementById("firstLevel");
			var secondLevel = document.getElementById("secondLevel");
			if (pid == 0) {
				firstLevel.options.length = 0;
				secondLevel.options.length = 0;
				addOption(firstLevel, options);
				secondLevel.add(new Option("\u8bf7\u9009\u62e9", ""));
				return false;
			} else {
				if (pid == 1) {
					secondLevel.options.length = 0;
					addOption(secondLevel, options);
					return false;
				}
			}
		}
	}
}
function addOption(selectObject, options) {
	for (var i = 0; i < options.length; i++) {
		var option = options[i];
		var optionId = option.getElementsByTagName("optionId").item(0).firstChild.nodeValue;
		var optionName = option.getElementsByTagName("optionName").item(0).firstChild.nodeValue;
		optionName = decodeURIComponent(optionName);
		if (i == 0) {
			selectObject.add(new Option("\u8bf7\u9009\u62e9", 0));
			selectObject.add(new Option(optionName, optionId));
		} else {
			selectObject.add(new Option(optionName, optionId));
		}
	}
}
function getFirstLevel(father_id) {
	pid = 0;
	var url = "afterSales.do?method=getQT&level=0&parentId=" + father_id;
	sendAjaxRequest(url);
}
function getSecondLevel(father_id) {
	var secondLevel = document.getElementById("secondLevel");
	if (father_id == 0) {
		secondLevel.options.length = 0;
		secondLevel.add(new Option("\u8bf7\u9009\u62e9", 0));
	} else {
		pid = 1;
		var url = "afterSales.do?method=getQT&level=1&parentId=" + father_id;
		sendAjaxRequest(url);
	}
}


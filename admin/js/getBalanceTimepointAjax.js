
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
	var balanceDate = document.getElementById("balanceDate");
	balanceDate.options.length = 0;
	balanceDate.add(new Option("", ""));
	if (XMLHttpReq.readyState == 4) {
		if (XMLHttpReq.status == 200) {
			var options = XMLHttpReq.responseXML.getElementsByTagName("option");			
			addOption(balanceDate, options);
			return false;
		}
	}
}
function addOption(selectObject, options) {
	for (var i = 0; i < options.length; i++) {
		var option = options[i];
		var optionId = option.getElementsByTagName("optionId").item(0).firstChild.nodeValue;
		var optionName = option.getElementsByTagName("optionName").item(0).firstChild.nodeValue;
		optionName = decodeURIComponent(optionName);
		selectObject.add(new Option(optionName, optionId));
	}
}
function getBalanceTimepoint(balanceType) {
	var url = "../mailingBalance.do?method=getBalanceTimepoint&balanceType=" + balanceType;
	sendAjaxRequest(url);
}


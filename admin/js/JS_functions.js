var IE = (document.all) ? 1 : 0;
var NS = (document.layers) ? 1 : 0;

// WINDOW STATUS FUNCTIONS
window.defaultStatus = "";
function winStatus( msg )
{
	window.status = msg;
}

// ALERT FUNCTIONS
function adminAlert( msg ) {
	if( msg != null && trim(msg) != "" ) {
		alert( msg );
	}
}

function goThere( node,loc ) {
	//Only perform folder opening, don't close anything
	if( parent.nav.indexOfEntries[node].isOpen == false )
		parent.nav.clickOnNode(node);
	window.location = loc;
}

// NAV TREE FUNCTIONS
function navRefresh() {
	top.nav.location.reload();
}

// STRING FUNCTIONS
function trim( str ) {
	// Immediately return if no trimming is needed
	if( (str.charAt(0) != ' ') && (str.charAt(str.length-1) != ' ') ) { return str; }
	// Trim leading spaces
	while( str.charAt(0)  == ' ' ) {
		str = '' + str.substring(1,str.length);
	}
	// Trim trailing spaces
	while( str.charAt(str.length-1)  == ' ' ) {
		str = '' + str.substring(0,str.length-1);
	}

	return str;
}

function strReplace( entry, bad, good ) {
	temp = "" + entry; // temporary holder
	while( temp.indexOf(bad) > -1 ) {
		pos= temp.indexOf( bad );
		temp = "" + ( temp.substring(0, pos) + good + 
			temp.substring( (pos + bad.length), temp.length) );
	}
	return temp;
}

function OpenWindow(url,width,height,left,top,name){
    window.open(""+url+"",""+name+"","width="+width+",height="+height+",directories=no,toolbar=no,resizsable=no,menubar=no,scrollbars=yes,left="+left+",top="+top);
}

function deleteSelect(select){
	for(var i=0;i<select.options.length;){
		if(select.options[i].selected){
			select.options[i].value="";
			select.options[i].text="";
			select.options[i] = null;
			i = 0;
		}else{
			i++;
		}
	}
}

function selectOption(select,value){
	for(var i = 0; i < select.length; i++){
		if(select.options[i].value == value){
			select.options[i].selected = true;
			return i;
		}
	}
	return 0;
}

function setAllCheck(checkform,checkbox,checked)
{
		for(i=0;i<checkform.elements.length;i++)
		{
			if(checkform.elements[i].name==checkbox)
				checkform.elements[i].checked=checked;
		}
}

function getElementsByName_iefix(tag, name) {
	var elem = document.getElementsByTagName(tag);
	var arr = new Array();
 	for(i = 0,iarr = 0; i < elem.length; i++) {
 		att = elem[i].getAttribute("name");
		if(att == name) {
			arr[iarr] = elem[i];
			iarr++;
		}
	}
	return arr;
}

function checkboxChecked(checkbox,value){
	values = value.split(",");
	for(var j = 0; j < values.length; j++){
		for(var i = 0; i < checkbox.length; i++){
			if(checkbox[i].value == values[j]){
				checkbox[i].checked = true;
			}
		}
	}
}

function selectRadio(radio,value){
	for(var i = 0; i < radio.length; i++){
		if(radio[i].value == value){
			radio[i].checked = true;
		}
	}
}
function reserveCheck(name){
	  var names=document.getElementsByName(name);
	  var len=names.length;
	 if(len>0){
	 	var i=0;
	    for(i=0;i<len;i++){
	     if(names[i].checked)
	     names[i].checked=false;
	     else
	     names[i].checked=true;
	    }
	 } 
}
function colorChangeRadio(obj,color){
    obj.parentNode.now = obj;
    obj.parentNode.now.nextSibling.style.color = color;
    if(obj.parentNode.pre) obj.parentNode.pre.nextSibling.style.color = "black";
    obj.parentNode.pre = obj.parentNode.now;
}

function checkDate(str){
	var r = new RegExp("^[1-2]\\d{3}-(0?[1-9]||1[0-2])-(0?[1-9]||[1-2][0-9]||3[0-1])$"); 
	if((startTime.length!=0 && startTime.length!=10) || !r.test(startTime)){
		return false;
	}
	var d = new Date(str);
	var arr = str.split("-");
	if(!(parseInt(arr[0], 10) == d.getFullYear()) && (parseInt(arr[1], 10) == (d.getMonth() + 1)) && (parseInt(arr[2], 10) == d.getDate())){
		return false;
	}
	
	return true;
	
}
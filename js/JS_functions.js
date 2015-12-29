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
		}
	} 
}

function setAllCheck(checkform,checkbox,checked)
{
		for(i=0;i<checkform.elements.length;i++)
		{
			if(checkform.elements[i].name==checkbox)
				checkform.elements[i].checked=checked;
		}
}

function checkboxChecked(checkbox,value){
	var values = value.split(",");
	for(var j = 0; j < values.length; j++){
		for(var i = 0; i < checkbox.length; i++){
			if(checkbox[i].value == values[j]){
				checkbox[i].checked = true;
			}
		}
	}
}
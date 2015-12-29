var browserInfo = new Object();
var sAgent = navigator.userAgent.toLowerCase() ;

browserInfo.IsIE			= ( sAgent.indexOf("msie") != -1 ) ;
browserInfo.IsGecko		= !browserInfo.IsIE ;
browserInfo.IsSafari		= ( sAgent.indexOf("safari") != -1 ) ;
browserInfo.IsNetscape	= ( sAgent.indexOf("netscape") != -1 ) ;
		
		
function setDisplay(obj,bool){
	if(bool)
		obj.style.display="block";
	else
		obj.style.display="none";
}
var oncontextmenu = function(e)
{
	// Make sure popup only displays on menuTree
	if(checkEventIn(e, document.getElementById('menuTree')) == false) {
		return;
	}

    if(!e) {
        e = window.event;
    }

    if(browserInfo.IsGecko) {
        try {
            e.preventDefault();
        } catch(ex) {
        }
    }

    document.getElementById('popup').style.left = e.clientX + 'px';
    document.getElementById('popup').style.top = e.clientY  + 'px';
    document.getElementById('popup').style.display = "";
    fixPos(document.getElementById('popup'), e.clientX, e.clientY);

    return false;
};

OnDocumentClick = function( event )
{
	var ss = document.getElementById("popup");
	if(checkEventIn(event, document.getElementById('popup')) == true) {
		return;
	}


    document.getElementById('popup').style.display = "none";
}

function fixPos(menu, x, y)
{
	var docheight,docwidth,dh,dw;
	docheight = document.body.clientHeight;
	docwidth  = document.body.clientWidth;

	dh = y - docheight;
	dw = x - docwidth;
	//debug( menu.style.width );

	if(dw>0)
	{
		menu.style.left = (x - dw - menu.style.width) + document.body.scrollLeft + "px";
	}
	else
	{
		menu.style.left = x + document.body.scrollLeft;
	}
	if(dh>0)
	{
		menu.style.top = (y - dh) + document.body.scrollTop + "px"
	}
	else
	{
		menu.style.top  = y + document.body.scrollTop;
	}
}


if(browserInfo.IsGecko) {
    try {
       document.getElementById('menuTree').addEventListener(
	   	'contextmenu', oncontextmenu, true);
    } catch(ex) {
    }

    document.addEventListener('click',OnDocumentClick,true);
}

function hidePopup() {
	document.getElementById('popup').style.display = "none";
}

function checkEventIn(event, element)
{
    if(!event) {
        event = window.event;
    }

    var e;

    if(browserInfo.IsGecko) {
       e = event.target;
    } else {
       e = event.srcElement;
    }



    while ( e )
	{
		if ( e == element ) return true;
		e = e.parentNode ;
	}

    return false;
}

/**
 *  Refresh the selected node.(刷新)
 */
function refreshNode() {
	if(node_root.getSelected().src==null)
		return ;
	if (node_root.getSelected() && node_root.getSelected().reload) {
	    node_root.getSelected().reload();
	} else if(node_root.reload) {
		node_root.reaload();
	}
//	alert(node_root.getSelected().action);
}

function openNode() {
	if (node_root.getSelected()) {
	    window.open(node_root.getSelected().action, "_blank");
	}
}

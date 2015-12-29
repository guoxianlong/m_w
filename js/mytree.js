//**************************************************************** 
// You are free to copy the "Folder-Tree" script as long as you  
// keep this copyright notice: 
// Script found in: http://www.geocities.com/Paris/LeftBank/2178/ 
// Author: Marcelino Alves Martins (martins@hks.com) December '97. 
//**************************************************************** 
 
//Log of changes: 
//       17 Feb 98 - Fix initialization flashing problem with Netscape
//       
//       27 Jan 98 - Root folder starts open; support for USETEXTLINKS; 
//                   make the ftien4 a js file 
//       
// Definition of class Folder 
// ***************************************************************** 

//JRun change log:
//		03 Nov 99 - Added onMouseOver='winStatus(\"" + this.desc + "\"); return true;'
//					for folders and docs
//				  - Added window.defaultStatus = "";
//				  - Added function winStatus( msg ).
//				  - Added "onMouseOver='winStatus(\"\"); return true;'"
//					on <A HREF to hide ugly javascript window status.
//		10 Oct 99 - Appended "CLASS='contentList'" to appropriate areas.
//		01 Oct 99 - Turned on USETEXTLINKS.
//		15 Sep 99 - Appended TARGET=\"content\" so that all nav links
//					go to the content frame on the right
//		12 Jan 00 - Added logic to be able to change icons at the panel level
//					switch( this.desc ) { case "Change Password": (line circa 27)
//		12 Jan 00 - Added "&nbsp;" + this.desc for extra padding in front of the
//					description since we are now trimming the GIFs
//		08 Feb 00 - Moved default status and display function to /includes/JS_functions.js
//		28 Feb 00 - Added support for node level gifs in Item(desc,link), desc split on "^"
//					Folder(desc,href) splits the description on "|" and sets new attribute folderIconType and 
//					propagateChangesInState(folder) now points to jmc_icons and takes folderIconType
//onMouseOver='winStatus(\"a\");'
// ***************************************************************** 
//AFTECH chang log,by xxb:
//        
//******************************************************************
function Folder(folderDescription, hreference, value) //constructor 
{ 
  //constant data
  descTemp_arr = folderDescription .split("|");
  this.desc=descTemp_arr[0];
  // Assign the default icon if none specified
  if( descTemp_arr[1] == null ) {
	this.folderIconType = "iGeneric";
  } else {
	this.folderIconType = descTemp_arr[1];
  }
  if(descTemp_arr.length>=3){
      this.value=descTemp_arr[2];
  }else{
    this.value="";
  }
  this.contactId=value;
  //this.desc = folderDescription
  this.type=0 
  this.hreference = hreference 
  this.id = -1   
  this.navObj = 0  
  this.iconImg = 0  
  this.nodeImg = 0  
  this.isLastNode = 0 
 
  //dynamic data 
  this.isOpen = true 
  this.iconSrc = imagePath + this.folderIconType + ".gif"   
  this.children = new Array 
  this.nChildren = 0 
 
  //methods 
  this.initialize = initializeFolder 
  this.setState = setStateFolder 
  this.addChild = addChild 
  this.createIndex = createEntryIndex 
  this.hide = hideFolder 
  this.display = display 
  this.renderOb = drawFolder 
  this.totalHeight = totalHeight 
  this.subEntries = folderSubEntries 
  this.outputLink = outputFolderLink
} 

function setStateFolder(isOpen) 
{ 
  var subEntries 
  var totalHeight 
  var fIt = 0 
  var i=0 

  if (isOpen == this.isOpen) 
    return 
 
  if (browserVersion == 2)  
  { 
    totalHeight = 0 
    for (i=0; i < this.nChildren; i++) 
      totalHeight = totalHeight + this.children[i].navObj.clip.height 
      subEntries = this.subEntries() 
    if (this.isOpen) 
      totalHeight = 0 - totalHeight 
    for (fIt = this.id + subEntries + 1; fIt < nEntries; fIt++) 
      indexOfEntries[fIt].navObj.moveBy(0, totalHeight) 
  }  
  this.isOpen = isOpen 

  propagateChangesInState(this) 
} 
 
function propagateChangesInState(folder) 
{
  var i=0 
 
  if (folder.isOpen) 
  { 
    if (folder.nodeImg){ 
      if (folder.isLastNode){
        if(folder.nChildren > 0){
          folder.nodeImg.src =navPath+"ftv2mlastnode.gif"
        }else{
          folder.nodeImg.src =navPath+"ftv2lastnode.gif"
        }
      }else{
        if(folder.nChildren > 0){
	      folder.nodeImg.src = navPath+"ftv2mnode.gif"
	    }else{
	      folder.nodeImg.src = navPath+"ftv2node.gif"
	    }
	  }
	} 
    folder.iconImg.src = imagePath+"iOpenFolder.gif" 
    for (i=0; i<folder.nChildren; i++)
      folder.children[i].display()
  } 
  else 
  { 
    if (folder.nodeImg){ 
      if (folder.isLastNode){
        if(folder.nChildren > 0){
          folder.nodeImg.src = navPath+"ftv2plastnode.gif"
        }else{
          folder.nodeImg.src = navPath+"ftv2lastnode.gif"
        }
      }else{
        if(folder.nChildren > 0){ 
	      folder.nodeImg.src = navPath+"ftv2pnode.gif"
	    }else{
	      folder.nodeImg.src = navPath+"ftv2node.gif"
	    }
	  }
	} 
    folder.iconImg.src = imagePath+ folder.folderIconType + "Folder.gif"
    for (i=0; i<folder.nChildren; i++) 
      folder.children[i].hide() 
  }  
} 
 
function hideFolder() 
{ 
  if (browserVersion == 1) { 
    if (this.navObj.style.display == "none") 
      return 
    this.navObj.style.display = "none" 
  } else { 
    if (this.navObj.visibility == "hiden") 
      return 
    this.navObj.visibility = "hiden" 
  } 
   
  this.setState(0) 
} 
 
function initializeFolder(level, lastNode, leftSide) 
{ 
  var j=0 
  var i=0 
  var numberOfFolders 
  var numberOfDocs 
  var nc 
      
  nc = this.nChildren 
   
  this.createIndex() 
 
  var auxEv = "" 
 
  if (browserVersion > 0) 
    auxEv = "<a style='cursor:hand;' onClick='clickOnNode("+this.id+")' onMouseOver='winStatus(\"\"); return true;' 'CLASS='nav'>" 
  else 
    auxEv = "<a class='nav'>" 
 
  if (level>0) 
    if (lastNode) //the last 'brother' in the children array 
    { 
      this.renderOb(leftSide + auxEv + "<img name='nodeIcon" + this.id + "' src='"+navPath+"ftv2mlastnode.gif' width=16 height=22 border=0 align=absmiddle></a>") 
      leftSide = leftSide + "<img src='"+navPath+"ftv2blank.gif' width=16 height=22 align=absmiddle>"  
      this.isLastNode = 1 
    } 
    else 
    { 
      this.renderOb(leftSide + auxEv + "<img name='nodeIcon" + this.id + "' src='"+navPath+"ftv2mnode.gif' width=16 height=22 border=0 align=absmiddle></a>") 
      leftSide = leftSide + "<img src='"+navPath+"ftv2vertline.gif' width=16 height=22 align=absmiddle>" 
      this.isLastNode = 0 
    } 
  else 
    this.renderOb("") 
   
  if (nc > 0) 
  { 
    level = level + 1 
    for (i=0 ; i < this.nChildren; i++)  
    { 
      if (i == this.nChildren-1) 
        this.children[i].initialize(level, 1, leftSide) 
      else 
        this.children[i].initialize(level, 0, leftSide) 
      } 
  } 
} 
 
function drawFolder(leftSide) 
{ 
  if (browserVersion == 2) { 
    if (!doc.yPos) 
      doc.yPos=8 
    doc.write("<layer id='folder" + this.id + "' top=" + doc.yPos + " visibility=hiden>") 
  } 
   
  doc.write("<table  ") 
  if (browserVersion == 1) 
    doc.write(" id='folder" + this.id + "' style='position:block;' ") 
  doc.write(" border=0 cellspacing=0 cellpadding=0>") 
  doc.write("<tr><td>") 
  doc.write(leftSide) 
  this.outputLink() 
  doc.write("<img name='folderIcon" + this.id + "' ") 
  doc.write("src='" + this.iconSrc+"' height=16 width=16 border=0></a>") 
  doc.write("</td><td valign=middle nowrap CLASS='contentList'>") 
  if (USETEXTLINKS) 
  { 
    this.outputLink() 
    doc.write("&nbsp;" + this.desc + "</a>") 
  } 
  else 
    doc.write("&nbsp;" + this.desc) 
  doc.write("</td>")  
  doc.write("</table>") 
   
  if (browserVersion == 2) { 
    doc.write("</layer>") 
  } 
 
  if (browserVersion == 1) { 
    this.navObj = doc.all["folder"+this.id] 
    this.iconImg = doc.all["folderIcon"+this.id] 
    this.nodeImg = doc.all["nodeIcon"+this.id] 
  } else if (browserVersion == 2) { 
    this.navObj = doc.layers["folder"+this.id] 
    this.iconImg = this.navObj.document.images["folderIcon"+this.id] 
    this.nodeImg = this.navObj.document.images["nodeIcon"+this.id] 
    doc.yPos=doc.yPos+this.navObj.clip.height 
  } 
} 
 
function outputFolderLink() 
{ 
  if (this.hreference) 
  { 
    doc.write("<a class=nav href='" + this.hreference + "&jrun_fid=" + this.id + "&jrun_children=" + this.nChildren + "' TARGET=\""+strTarget+"\" onMouseOver='winStatus(\"" + this.desc + "\"); return true;' ")
    if (browserVersion > 0) 
      doc.write("onClick='javascript:clickOnFolder("+this.id+")'") 
    doc.write(">") 
  } 
  else 
    doc.write("<a class='nav'>") 
} 
 
function addChild(childNode) 
{ 
  this.children[this.nChildren] = childNode 
  this.nChildren++ 
  return childNode 
} 
 
function folderSubEntries() 
{ 
  var i = 0 
  var se = this.nChildren 
 
  for (i=0; i < this.nChildren; i++){ 
    if (this.children[i].children) //is a folder 
      se = se + this.children[i].subEntries() 
  } 
 
  return se 
} 
 
 
// Definition of class Item (a document or link inside a Folder) 
// ************************************************************* 
 
function Item(itemDescription, itemLink, value) // Constructor 
{ 
  // constant data
  descTemp_arr = itemDescription.split("^");
  this.desc=descTemp_arr[0];
  // Assign the default icon if none specified
  if( descTemp_arr[1] == null ) {
	this.iconSrc = imagePath+"iGenericAttribute.gif";
  } else {
    this.iconSrc = imagePath+ descTemp_arr[1];
  }  
  this.contactId=value;
  //this.desc = itemDescription
  this.type=1 
  this.link = itemLink 
  this.id = -1 //initialized in initalize() 
  this.navObj = 0 //initialized in render() 
  this.iconImg = 0 //initialized in render() 
  this.initialize = initializeItem 
  this.createIndex = createEntryIndex 
  this.hide = hideItem 
  this.display = display 
  this.renderOb = drawItem 
  this.totalHeight = totalHeight 
} 
 
function hideItem() 
{ 
  if (browserVersion == 1) { 
    if (this.navObj.style.display == "none") 
      return 
    this.navObj.style.display = "none" 
  } else { 
    if (this.navObj.visibility == "hiden") 
      return 
    this.navObj.visibility = "hiden" 
  }     
} 
 
function initializeItem(level, lastNode, leftSide) 
{  
  this.createIndex() 
 
  if (level>0) 
    if (lastNode) //the last 'brother' in the children array 
    { 
      this.renderOb(leftSide + "<img src='"+navPath+"ftv2lastnode.gif' width=16 height=22 align=absmiddle>") 
      leftSide = leftSide + "<img src='"+navPath+"ftv2blank.gif' width=16 height=22 align=absmiddle>"  
    } 
    else 
    { 
      this.renderOb(leftSide + "<img src='"+navPath+"ftv2node.gif' width=16 height=22 align=absmiddle>") 
      leftSide = leftSide + "<img src='"+navPath+"ftv2vertline.gif' width=16 height=22 align=absmiddle>" 
    } 
  else 
    this.renderOb("")   
} 
 
function drawItem(leftSide) 
{ 
  tempStr=this.desc;
  addStr="";
  if(tempStr.indexOf("|")>0){
  	descTemp_arr = tempStr.split("|");
        this.desc=descTemp_arr[1];
        addStr=descTemp_arr[0];
  }
  if (browserVersion == 2) 
    doc.write("<layer id='item" + this.id + "' top=" + doc.yPos + " visibility=hiden>") 
     
  doc.write("<table ") 
  if (browserVersion == 1) 
    doc.write(" id='item" + this.id + "' style='position:block;' ") 
  doc.write(" border=0 cellspacing=0 cellpadding=0>") 
  doc.write("<tr><td>") 
  doc.write(leftSide) 
  doc.write("<a class=nav href=" + this.link + " onMouseOver='winStatus(\"" + this.desc + "\"); return true;'>") 
  doc.write("<img id='itemIcon"+this.id+"' ") 
  doc.write("src='"+this.iconSrc+"' border=0>") 
  doc.write("</a>") 
  doc.write("</td><td valign=middle nowrap CLASS='contentList'>") 
  if (USETEXTLINKS) 
    doc.write(addStr+"<a class=nav href=" + this.link + " onMouseOver='winStatus(\"" + this.desc + "\"); return true;'>&nbsp;" + this.desc + "</a>") 
  else 
    doc.write("&nbsp;" + this.desc) 
  doc.write("</table>") 
   
  if (browserVersion == 2) 
    doc.write("</layer>") 
 
  if (browserVersion == 1) { 
    this.navObj = doc.all["item"+this.id] 
    this.iconImg = doc.all["itemIcon"+this.id] 
  } else if (browserVersion == 2) { 
    this.navObj = doc.layers["item"+this.id] 
    this.iconImg = this.navObj.document.images["itemIcon"+this.id] 
    doc.yPos=doc.yPos+this.navObj.clip.height 
  } 
} 

// Methods common to both objects (pseudo-inheritance) 
// ******************************************************** 

function display() 
{ 
  if (browserVersion == 1) 
    this.navObj.style.display = "block" 
  else 
    this.navObj.visibility = "show" 
} 
 
function createEntryIndex() 
{ 
  this.id = nEntries 
  indexOfEntries[nEntries] = this 
  nEntries++ 
} 
 
// total height of subEntries open 
function totalHeight() //used with browserVersion == 2 
{ 
  var h = this.navObj.clip.height 
  var i = 0 
   
  if (this.isOpen) //is a folder and _is_ open 
    for (i=0 ; i < this.nChildren; i++)  
      h = h + this.children[i].totalHeight() 
 
  return h 
} 
 
// Events 
// ********************************************************* 

function clickOnFolder(folderId) 
{ 
  alert(folderId);
  var clicked = indexOfEntries[folderId] 
 
  if (!clicked.isOpen) 
    clickOnNode(folderId) 
 
  return  
 
  if (clicked.isSelected) 
    return 
} 
 
function clickOnNode(folderId) 
{ 
  var clickedFolder = 0 
  var state = 0 
 
  clickedFolder = indexOfEntries[folderId] 
  state = clickedFolder.isOpen 
 
  clickedFolder.setState(!state) //open<->close 
   if(!state){
    if(clickedFolder.value!=""){
    	s=location.href+"";
    	pos2=s.lastIndexOf("\/");
    	if(pos2>1) s=s.substring(pos2+1);
    	pos=s.indexOf("&gid");
    	if(pos>1) s=s.substring(0,pos);
        location.replace(s+"&gid="+clickedFolder.value+"&folderId="+folderId);	 
    }
  }  
} 
function openOnNode(folderId){
  var clickedFolder = 0 
  var state = 0 
 
  clickedFolder = indexOfEntries[folderId]; 
  state = clickedFolder.isOpen; 
  for(i=1;i<=folderId;i++){
     tempFolder=indexOfEntries[i];
     if(tempFolder.type==0){
       for(j=0;j<tempFolder.nChildren;j++){
       	  if(tempFolder.children[j].type==0){
       	     if(tempFolder.children[j].id==folderId)
       	        openOnNode(i);	
       	  }
       	}
     }
  }
  clickedFolder.setState(true); //open<->close
}
function initializeDocument() 
{ 
  if (doc.all) 
    browserVersion = 1 //IE4   
  else 
    if (doc.layers) 
      browserVersion = 2 //NS4 
    else 
      browserVersion = 0 //other 
 
  foldersTree.initialize(0, 1, "") 
  foldersTree.display()
  
  if (browserVersion > 0) 
  { 
    doc.write("<layer top="+indexOfEntries[nEntries-1].navObj.top+">&nbsp;</layer>") 
 
    // close the whole tree 
    clickOnNode(0) 
    // open the root folder 
    clickOnNode(0) 
  } 
} 
 
// Auxiliary Functions for Folder-Treee backward compatibility 
// ********************************************************* 
 
function gFld(description, hreference, value) 
{ 
  folder = new Folder(description, hreference, value) 
  return folder 
} 
 
function gLnk(target, description, linkData, value) 
{ 
  fullLink = "" 
 
  if (target==0) 
  { 
    fullLink = "'"+linkData+"' target=\"_top\"" 
  } 
  else 
  { 
    if (target==1) 
       fullLink = "'"+linkData+"' target=_blank" 
       else if(target==3){
    	  fullLink="\""+linkData+"\"";
    	}
    else 
       fullLink = "'"+linkData+"' target=\""+strTarget+"\"" 
  } 
 
  linkItem = new Item(description, fullLink, value)   
  return linkItem 
} 
 
function insFld(parentFolder, childFolder) 
{ 
  return parentFolder.addChild(childFolder) 
} 
 
function insDoc(parentFolder, document) 
{ 
  parentFolder.addChild(document) 
} 
 
// Global variables 
// **************** 
 
USETEXTLINKS = 1
indexOfEntries = new Array 
nEntries = 0 
doc = document 
browserVersion = 0 
selectedFolder=0
strTarget="mainFrame"
imagePath="images/tree/";
navPath="images/tree/nav/";

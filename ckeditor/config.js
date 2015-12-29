/*
Copyright (c) 2003-2011, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

CKEDITOR.editorConfig = function( config ) { 
	config.toolbar = 'MyToolbar';  
	config.toolbar_MyToolbar = 
		[ { name: 'document', items : ['Source','NewPage','Preview' ] }, 
		  { name: 'basicstyles', items :['Bold','Italic','Strike','-','RemoveFormat' ] }, 
		  { name: 'clipboard', items : ['Cut','Copy','Paste','PasteText','PasteFromWord','-','Undo','Redo' ] }, 
		  { name:'styles', items : [ 'Styles','Format' ] }, 
		  { name: 'paragraph', items : ['NumberedList','BulletedList','-','Outdent','Indent','-','Blockquote' ] }, 
		  { name:'insert', items :['Image','Flash','Table','HorizontalRule','Smiley','SpecialChar','PageBreak' ,'Iframe'] }, 
		  { name: 'links', items : [ 'Link','Unlink','Anchor' ] } 
		]; };


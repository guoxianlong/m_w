<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" indent="no" omit-xml-declaration="yes"/>
<xsl:template match="wml/card">
<HTML>
<HEAD>
<meta id="viewport" name="viewport" content="width=200; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
<title>预览</title>
</HEAD>
<link href="proxy.css" rel="stylesheet" type="text/css"/>
<BODY>
<form action="" method="post" name="sform"></form>
<div align="center" style="width:200px;border:1 solid black;">
  <font color="orange"><b><xsl:value-of select="@title" /></b></font>
</div>
<script>
function go(url){document.sform.action=url;return document.sform.submit();}
function addfrom(name,from){add(name,document.getElementById(from).value);}
function add(name,value){
var oInput = document.createElement("input");
oInput.setAttribute("type","hidden");
oInput.setAttribute("name",name);
oInput.setAttribute("value",value);
document.sform.appendChild(oInput);
}
</script>
<div style="width:200px;border:1 solid black;padding:5px;">
<xsl:apply-templates />
</div>

</BODY>
</HTML>
</xsl:template>

<xsl:template match="a">
	<a>
	<xsl:copy-of select="@*" />
	<xsl:apply-templates />
	</a>
</xsl:template>

<xsl:template match="img">
	<img>
	<xsl:attribute name="src"><xsl:value-of select="@src"/></xsl:attribute>
	</img>
</xsl:template>

<xsl:template match="p">
	<p>
	<xsl:apply-templates/>
	</p>
</xsl:template>

<xsl:template match="br">
	<xsl:element name="br" />
</xsl:template>

<xsl:template match="input">
	<input type="text" id="{ @name }">
	<xsl:copy-of select="@name | @value" />
	</input>
</xsl:template>

<xsl:template match="anchor">
	<a href="void(0);">
	<xsl:apply-templates select="go|prev|refresh"/>
	<xsl:copy-of select=" text() " />
	</a>
</xsl:template>

<xsl:template match="go">
	<xsl:attribute name="onclick">
	<xsl:for-each select="postfield">
		<xsl:if test="starts-with(@value,'$')">
		addfrom('<xsl:value-of select="@name" />','<xsl:value-of select="substring(@value,2)" />');
		</xsl:if>
		<xsl:if test="not(starts-with(@value,'$'))">
		add('<xsl:value-of select="@name" />','<xsl:value-of select="@value" />');
		</xsl:if>
	</xsl:for-each>
	go('<xsl:value-of select="@href" />');return false;
	</xsl:attribute>
</xsl:template>    

<xsl:template match="prev">
	<xsl:attribute name="onclick">
	window.history.back();return false;
	</xsl:attribute>
</xsl:template>

<xsl:template match="refresh">
<!--	<xsl:attribute name="onclick">
	window.location.reload();return false;
	</xsl:attribute>-->
</xsl:template>

<xsl:template match="select">
	<select>
	<xsl:copy-of select="@id | @title | @name | @tabindex" /> 
	<xsl:apply-templates select="option" /> 
	</select>
</xsl:template>

<xsl:template match="option">
	<option>
	  <xsl:copy-of select="@id | @onpick | @title | @value | text()" /> 
	</option>
</xsl:template>

</xsl:stylesheet>
<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="html" encoding="UTF-8"/>

<!-- ======================================================================
                       osis.html.xsl

Modified from thml.html.xsl, original authors Jimmy Osborn and Wes Morgan 
Modified to format OSIS to html by Jon Van Hofwegen. 
Additional modification by Harry Plantinga. 2002-11-15
Modified 2003-01-29: give Hebrew text a type hebrewRev so we can prevent
  it from flipping around backwards.

Current state: it works reasonably, but it doesn't support all of OSIS. 
It assumes that the OSIS in use is the result of the CCEL's ThML->OSIS
conversion process, so there are OSIS peculiarities that must be met for
it to work. E.g. 

  - <div type="div1" divTitle="xxx"> for table of contents entries.
  - <milestone type="pb" n="xxiv" /> for page breaks
  - <creator role="aut" type="ccel"> for authorID
  - <p subType="x-myClassName"> for <p class="myClassName">
    where myClassName is defined in a separate css stylesheet.

Therefore, unless you are going to ferret out these requirements and 
duplicate them, you should probable consider this more of a proof of 
concept or a starting point for displaying non-CCEL OSIS docs.

========================================================================-->

<!--===========some variables================-->
<xsl:variable name="authorID" 
  select="/osis/osisText/header/work/creator[@role='aut' and @type='ccel']"/>
<xsl:variable name="bookID" 
  select="substring-before(/osis/osisText[@osisIDWork],'_')"/>
<xsl:variable name="osisIDWork" 
  select="/osis/osisText/@osisIDWork" />
<xsl:variable name="ccelURI">
  <xsl:value-of select="$osisIDWork" />.html</xsl:variable>
<xsl:variable name="Title">
  <xsl:value-of select="/osis/osisText/header/work/title" />
  by <xsl:value-of select="/osis/osisText/header/work/creator[@role='aut' and @subType='short-form']" />
</xsl:variable>


<!--======convert common attribute names=====-->
<xsl:template name="commonAtts">
  <xsl:if test="@subType != ''">
    <xsl:attribute name="class">
      <xsl:value-of select="substring-after(@subType, 'x-')" />
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="@osisID != ''">
    <xsl:attribute name="id">
      <xsl:value-of select="@osisID" />
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="@lang != ''">
    <xsl:attribute name="lang">
      <xsl:value-of select="@lang" />
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="@href != ''">
    <xsl:attribute name="href">
      <xsl:value-of select="@href" />
    </xsl:attribute>
  </xsl:if>
  <xsl:if test="@n != ''">
    <xsl:attribute name="n">
      <xsl:value-of select="@n" />
    </xsl:attribute>
  </xsl:if>
</xsl:template>

<!--===========main document structure===========-->
<xsl:template match="osis">
  <html>
    <head>
      <title><xsl:value-of select="$Title"/></title>
        <link rel="stylesheet" type="text/css" href="http://www.ccel.org/idx.css"/>
        <link rel="stylesheet" type="text/css" href="http://www.ccel.org/osis/OSIS11.css" />
        <link rel="stylesheet" type="text/css" href="{$osisIDWork}.css" />
    </head>
    <body>

<!--=============ABOUT========= -->
      <a name="about" />
      <table width="100%" bgcolor="#660000">
        <tr><td class="whitehead" style="text-align:left">
	    <xsl:value-of select="$Title"/></td>
	    <td class="whitehead" style="text-align:right">About</td>
	</tr>
      </table>
      <p>
        <table>
          <tr><td width="180" align="right" valign="top"><b>Title:</b></td><td width="12"></td><td width="400"><xsl:value-of select="/osis/osisText/header/work/title"/></td></tr>
          <tr><td align="right" valign="top"><b>Author(s):</b></td><td></td><td><xsl:value-of select="/osis/osisText/header/work/creator[@role = 'aut' and @subType = 'file-as']"/>
            <xsl:for-each select="/osis/osisText/header/work/creator[@role != 'aut' and @subType = 'file-as']">
            <br/><xsl:choose><xsl:when test="preceding-sibling::creator[@role != 'aut' and position() = 1]/@subType = 'ccel'"><a href="ccel/{preceding-sibling::creator[@role != 'aut' and @subType = 'ccel' and position() = 1]}/index.html"><xsl:value-of select="."/></a></xsl:when><xsl:otherwise><xsl:value-of select="/osis/osisText/header/work/creator[@role != 'aut' and @subType = 'short-form']"/></xsl:otherwise></xsl:choose>
        <xsl:choose>
  	  <xsl:when test="@role = 'edt'">(Editor)</xsl:when>
	  <xsl:when test="@role = 'trl'"> (Translator)</xsl:when>
	</xsl:choose>
    </xsl:for-each></td></tr>
    <xsl:if test="/osis/osisText/header/work/publisher != ''"><tr><td align="right" valign="top"><b>Publisher:</b></td><td></td><td><xsl:value-of select="/osis/osisText/header/work/publisher"/></td></tr></xsl:if>
    <xsl:if test="/osis/osisText/header/work/description != ''"><tr><td align="right" valign="top"><b>Description:</b></td><td></td><td><xsl:apply-templates select="/osis/osisText/header/work/description" mode="includeParsing"/></td></tr></xsl:if>
    <xsl:if test="/osis/osisText/header/work/source != ''"><tr><td align="right" valign="top"><b>Source:</b></td><td></td><td><xsl:choose><xsl:when test="/osis/osisText/header/work/source[@type = 'URL'] != ''"><a href="{/osis/osisText/header/work/source[@type = 'URL']}"><xsl:value-of select="/osis/osisText/header/work/source[not(@type)]"/></a></xsl:when><xsl:otherwise><xsl:value-of select="/osis/osisText/header/work/source[not(@type)]"/></xsl:otherwise></xsl:choose></td></tr></xsl:if>
    <xsl:if test="/osis/osisText/header/work/language != '' and not(starts-with(/osis/osisText/header/work/language, 'en'))"><tr><td align="right" valign="top"><b>Language:</b></td><td></td><td>
    <xsl:choose>
      <xsl:when test="/osis/osisText/header/work/language = 'cn'">Chinese</xsl:when>
      <xsl:when test="/osis/osisText/header/work/language = 'nl'">Dutch</xsl:when>
      <xsl:when test="/osis/osisText/header/work/language = 'fr'">French</xsl:when>
      <xsl:when test="/osis/osisText/header/work/language = 'de'">German</xsl:when>
      <xsl:when test="/osis/osisText/header/work/language = 'el'">Greek</xsl:when>
      <xsl:when test="/osis/osisText/header/work/language = 'he'">Hebrew</xsl:when>
      <xsl:when test="/osis/osisText/header/work/language = 'it'">Italian</xsl:when>
      <xsl:when test="/osis/osisText/header/work/language = 'la'">Latin</xsl:when>
      <xsl:when test="/osis/osisText/header/work/language = 'pt'">Portuguese</xsl:when>
      <xsl:when test="/osis/osisText/header/work/language = 'ru'">Russian</xsl:when>
      <xsl:when test="/osis/osisText/header/work/language = 'es'">Spanish</xsl:when>
     <xsl:otherwise><xsl:value-of select="/osis/osisText/header/work/language"/></xsl:otherwise>
    </xsl:choose></td></tr></xsl:if>
    <xsl:if test="/osis/osisText/header/work/rights != ''"><tr><td align="right" valign="top"><b>Rights:</b></td><td></td><td><xsl:value-of select="/osis/osisText/header/work/rights"/></td></tr></xsl:if>
    <xsl:if test="/osis/osisText/header/work/date != ''"><tr><td align="right"
    valign="top"><b>Date Created:</b></td><td></td><td><xsl:value-of
    select="/osis/osisText/header/work/date[@subType = 'Created']"/></td></tr></xsl:if>
    <xsl:if test="/osis/osisText/header/work/status != ''"><tr><td align="right" valign="top"><b>Status:</b></td><td></td><td><xsl:apply-templates select="/osis/osisText/header/work/status" mode="includeParsing"/></td></tr></xsl:if> 
    <xsl:if test="/osis/osisText/header/work/comments != ''"><tr><td align="right" valign="top"><b>General Comments:</b></td><td></td><td><xsl:apply-templates select="/osis/osisText/header/work/comments" mode="includeParsing"/></td></tr></xsl:if>
    <xsl:if test="/osis/osisText/header/work/contributor != ''"><tr><td align="right" valign="top"><b>Contributor(s):</b></td><td></td><td>
	<xsl:for-each select="/osis/osisText/header/work/contributor">
	  <xsl:if test=". != ''"><xsl:value-of select="."/> (<xsl:value-of select="@type"/>)<br/></xsl:if>
    </xsl:for-each></td></tr></xsl:if>
    <xsl:if test="/osis/osisText/header/work/subject[@type = 'ccel'] != ''"><tr><td align="right" valign="top"><b>CCEL Subjects:</b></td><td></td><td><xsl:value-of select="/osis/osisText/header/work/subject[@type = 'ccel']"/></td></tr></xsl:if>
    <xsl:if test="/osis/osisText/header/work/subject[@type = 'LCCN'] != ''"><tr><td align="right" valign="top"><b>LC Call no:</b></td><td></td><td><xsl:value-of select="/osis/osisText/header/work/subject[@type = 'LCCN']"/></td></tr></xsl:if>
    <xsl:if test="/osis/osisText/header/work/subject[starts-with(@type, 'lcsh')] != ''"><tr><td align="right" valign="top"><b>LC Subjects:</b></td><td></td><td>
	<xsl:for-each select="/osis/osisText/header/work/subject[starts-with(@type, 'lcsh')]">
	<xsl:variable name="class" select="concat('t', substring-after(@type, 'lcsh'))"/>
	<p class="{$class}"><xsl:value-of select="."/></p>
    </xsl:for-each></td></tr></xsl:if>
  </table>
</p>

<!-- ========TOC======== -->
      <a name="toc" />
      <table width="100%" bgcolor="#660000">
        <tr><td class="whitehead" style="text-align:left">
          <xsl:value-of select="$Title"/></td>
          <td class="whitehead" style="text-align:right">Table of Contents</td>
        </tr>
      </table>
      <div style="text-align:center">
      <table><tr><td>
<!--  
     <p class="TOC1"><a class="TOC" href="#about"><i>About This Book</i></a></p>
-->
	<xsl:apply-templates select="/osis/osisText/div[1]" mode="tocLevel1" />
      </td></tr></table></div>

<!-- ========PAGE======== -->
	<xsl:apply-templates select="/osis/osisText/div"/>
      </body>
    </html>
  </xsl:template>
<!-- ==========end of main document structure============ -->


<!--===================== handle divs =================== -->
<xsl:template match="div">
  <br/>
  <a name="{@osisID}"/>
    <table width="100%" bgcolor="#660000">
      <tr><td class="whitehead" style="text-align:left; width:42%">
          <xsl:value-of select="$Title"/></td>
          <td class="whitehead" style="text-align:center">
            <a class="whitehead">
              <xsl:attribute name="href">#<xsl:value-of select="preceding-sibling::div[1]/@osisID"/></xsl:attribute>&#8592;</a>
<!--        <a class="whitehead" href="#toc">&#9650;</a> -->
            <a class="whitehead"><xsl:attribute name="href">#<xsl:value-of select="../@osisID"/></xsl:attribute>&#8593;</a>
            <a class="whitehead">
              <xsl:attribute name="href">#<xsl:value-of select="following-sibling::div/@osisID"/></xsl:attribute>&#8594;</a>
          </td>
          <td class="whitehead" style="text-align:right; width:42%">
	    <xsl:value-of select="@divTitle"/>
	  </td>
      </tr>
    </table>
  <br/>
  <xsl:apply-templates/>
<!--  how to get a line only when there are notes? 
  <xsl:if test="">
    <hr align="left" width="30%"/>  
  </xsl:if>
-->
  <xsl:apply-templates mode="createNotes"/>
</xsl:template>

<xsl:template match="div" mode="tocLevel1">
  <div>
    <xsl:apply-templates select=".|following-sibling::div" mode="tocLevel2" />
  </div>
</xsl:template>

<xsl:template match="div" mode="tocLevel2">
  <xsl:variable name="level">
    <xsl:value-of select="substring-after(@type, 'x-div')"/>
  </xsl:variable>
<!--
  <p class="{concat('TOC', $level)}"><a class="TOC" href="#{@osisID}"><xsl:if test="boolean(@type)"><xsl:if test="boolean(@n)">&#160;<xsl:value-of select="@n" /></xsl:if>&#160;</xsl:if><xsl:value-of select="@divTitle" /></a></p>
-->
  <p class="{concat('TOC', $level)}"><a class="TOC" href="#{@osisID}"><xsl:value-of select="@divTitle" /></a></p>
  <xsl:apply-templates select="div[1]" mode="tocLevel1" />
</xsl:template>

<!--============name, date, foreign, seg ============-->
<xsl:template match="foreign[@lang='he']">
  <span class="hebrewRev">
    <xsl:for-each select="@*">
      <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
    </xsl:for-each>
    <xsl:apply-templates/>
  </span>
</xsl:template>

<xsl:template match="name|date|foreign|seg">
  <xsl:choose>
    <xsl:when test="@class != ''">
      <span class="{local-name()}">
        <xsl:for-each select="@*[name() != 'class']">
          <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
        </xsl:for-each>
        <span class="{@class}">
          <xsl:apply-templates/>
        </span>
      </span>
    </xsl:when>
    <xsl:otherwise>
      <span class="{local-name()}">
        <xsl:for-each select="@*">
          <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
        </xsl:for-each>
        <xsl:apply-templates/>
      </span>
    </xsl:otherwise>
  </xsl:choose>        
</xsl:template>

<!--==== elements that become <p>: signed,l ==============--> 
<xsl:template match="signed|l">
  <p type="{local-name()}">
    <xsl:call-template name="commonAtts" />
    <xsl:apply-templates/>
  </p>
</xsl:template>        
  
<!--====================== p =======================-->
<xsl:template match="title">
  <xsl:choose>
    <xsl:when test="@type='x-h1'">
      <h1>
        <xsl:call-template name="commonAtts" />
        <xsl:apply-templates/>
      </h1>
    </xsl:when>
    <xsl:when test="@type='x-h2'">
      <h2>
        <xsl:call-template name="commonAtts" />
        <xsl:apply-templates/>
      </h2>
    </xsl:when>
    <xsl:when test="@type='x-h3'">
      <h3>
        <xsl:call-template name="commonAtts" />
        <xsl:apply-templates/>
      </h3>
    </xsl:when>
    <xsl:when test="@type='x-h4'">
      <h4>
        <xsl:call-template name="commonAtts" />
        <xsl:apply-templates/>
      </h4>
    </xsl:when>
    <xsl:when test="@type='x-h5'">
      <h5>
        <xsl:call-template name="commonAtts" />
        <xsl:apply-templates/>
      </h5>
    </xsl:when>
    <xsl:when test="@type='x-h6'">
      <h6>
        <xsl:call-template name="commonAtts" />
        <xsl:apply-templates/>
      </h6>
    </xsl:when>
  </xsl:choose>
</xsl:template>

<!--====================== p =======================-->
<xsl:template match="p">
  <p>
    <xsl:call-template name="commonAtts" />
    <xsl:apply-templates/>
  </p>        
</xsl:template>

<!--================= lg ========================-->
<xsl:template match="lg">
  <table align="center"><tr><td>
  <div class="lg">
    <xsl:call-template name="commonAtts" />
    <xsl:apply-templates/>
  </div>        
  </td></tr></table>
</xsl:template>

<!--================= tables =======================-->
<xsl:template match="table">
  <table>
    <xsl:call-template name="commonAtts" />
    <xsl:apply-templates/>
  </table>        
</xsl:template>

<xsl:template match="row">
  <tr>
    <xsl:call-template name="commonAtts" />
    <xsl:apply-templates/>
  </tr>        
</xsl:template>

<xsl:template match="cell">
  <td>
    <xsl:call-template name="commonAtts" />
    <xsl:apply-templates/>
  </td>        
</xsl:template>

<!--============== anchors =======================-->
<xsl:template match="a">
  <a>
    <xsl:call-template name="commonAtts" />
    <xsl:apply-templates/>
  </a>        
</xsl:template>

<!--========= milestones x-br, x-hr, pb, or n= (sync) =========-->
<xsl:template match="milestone">
  <xsl:choose>
    <xsl:when test="@type = 'x-br'">
      <br/>
    </xsl:when>
    <xsl:when test="@type = 'x-hr'">
      <hr/>
    </xsl:when>
    <xsl:when test="@type = 'pb'">
      <table class="marg" align="left" bgcolor="#660000">
        <tr><td>
          <p class="page">
            <a>
              <xsl:attribute name="name">
                <xsl:value-of select="@osisID" />
              </xsl:attribute>
              <xsl:value-of select="@n"/>
            </a>
          </p>
        </td></tr>
      </table>
    </xsl:when>
    <xsl:when test="@n != '' ">
      <a name="{@n}"> </a>
    </xsl:when>
  </xsl:choose>
 </xsl:template>


<!--====== reference of type scripRef: link to bible gateway =====-->
<xsl:template match="reference">
  <xsl:choose>
    <xsl:when test="@type='scripRef'">
      <a class="scripRef" id="{@osisID}">
        <xsl:attribute name="href">http://www.gospelcom.net/bible?passage=<xsl:value-of select="."/></xsl:attribute>
        <xsl:value-of select="."/>
      </a>
    </xsl:when>
    <xsl:when test="@type='Commentary'">
      <a class="Commentary" name="{@osisID}">
        <xsl:value-of select="."/>
      </a>
    </xsl:when>
    <xsl:otherwise>
      <xsl:apply-templates/>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<!--=======note: store up footnotes, endnotes, etc============-->
<xsl:template match="note">
  <xsl:variable name="noteCount" select="count(preceding::note | ancestor::note | preceding-sibling::note) + 1"/>
  <a class="Note">
    <xsl:attribute name="name">_fnb<xsl:value-of select="$noteCount"/></xsl:attribute>
    <xsl:attribute name="href">#_fnf<xsl:value-of select="$noteCount"/></xsl:attribute>
    <sup class="Note"><xsl:value-of select="$noteCount"/></sup>
  </a>
</xsl:template>

<!--===============put the back footnotes into the doc============-->
<xsl:template match="note" mode="createNotes">
  <xsl:variable name="noteCount" select="count(preceding::note | ancestor::note | preceding-sibling::note) + 1"/>
  <div class="Note">
    <xsl:attribute name="id">_fnf<xsl:value-of select="$noteCount"/></xsl:attribute>
    <p class="footnote">
      <a class="Note">
        <xsl:attribute name="name">_fnf<xsl:value-of select="$noteCount"/></xsl:attribute>
        <xsl:attribute name="href">#_fnb<xsl:value-of select="$noteCount"/></xsl:attribute>
        <sup class="Note">
          <xsl:value-of select="$noteCount"/>
        </sup>
      </a>
      <font size="2">
        <xsl:apply-templates mode="createNotesContent"/>        
      </font>
    </p>
  </div>        
</xsl:template>

<!--===== templates for p, etc when showing footnotes =====-->
<xsl:template match="p" mode="createNotesContent">
  <xsl:choose>
    <xsl:when test="@class != ''">
      <span class="{local-name()}">
        <xsl:for-each select="@*[name() != 'class']">
          <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
        </xsl:for-each>
        <span class="{@class}">
          <xsl:apply-templates/>
        </span>
      </span>
    </xsl:when>
    <xsl:otherwise>
      <span class="{local-name()}">
        <xsl:for-each select="@*">
          <xsl:attribute name="{name()}"><xsl:value-of select="."/></xsl:attribute>
        </xsl:for-each>
        <xsl:apply-templates/>
      </span>
    </xsl:otherwise>
  </xsl:choose>
</xsl:template>

<xsl:template match="*[local-name() != 'p']" mode="createNotesContent">
  <xsl:apply-templates/>
</xsl:template> 

<xsl:template match="text()" mode="createNotes">
</xsl:template>

</xsl:stylesheet>

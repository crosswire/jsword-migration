<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:jsp="http://java.sun.com/JSP/Page"
    xmlns:html="http://www.w3.org/1999/xhtml"
    xmlns="http://www.w3.org/1999/xhtml"
    exclude-result-prefixes="html jsp"
    >

<xsl:output
    method="xml"
    doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
    indent="yes"
    encoding="windows-1252"
    />

<xsl:template match="/jsp:root">
  <jsp:root version="1.2">
    <xsl:apply-templates/>
  </jsp:root>
</xsl:template>

<xsl:template match="html:html">

<html>
  <head>
    <link rel="stylesheet" type="text/css" href="sword.css"/>
    <xsl:apply-templates select="html:head/*"/>
  </head>
  <body>
    <xsl:apply-templates select="html:body/@*"/>

<table width="100%">
  <tr align="center">
    <td class="navbutton" align="center"><a href="http://www.crosswire.org/">Crosswire</a></td>
    <td class="navbutton" align="center"><a href="http://www.crosswire.org/jsword/">JSword Home</a></td>
    <td class="navbutton" align="center"><a href="http://www.crosswire.org/sword/">Sword Home</a></td>
    <td class="navbutton" align="center"><a href="http://www.crosswire.org/sword/about/index.jsp">Purpose Statement</a></td>
  </tr>
</table>

<table width="100%" border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td class="logo" align="center" bgcolor="#cccc99">
      <a href="index.html"><img src="images/jsword.gif" width="512" height="75" border="0" alt="JSword Logo"/></a>
	</td>
  </tr>
</table>

<table width="100%">
  <tr align="center">
    <td class="navbutton" align="center"><a href="news.html">Latest News</a></td>
    <td class="navbutton" align="center"><a href="devt.html">Getting Involved</a></td>
    <td class="navbutton" align="center"><a href="screenshot.html">Screenshots</a></td>      
    <td class="navbutton" align="center"><a href="download.jsp">Download</a></td>
    <td class="navbutton" align="center"><a href="http://www.crosswire.org/sword/modules/index.jsp">Module Add-ins</a></td>
  </tr>
</table>

<table width="100%" border="0">
  
  <tr> 

    <td valign="top" class="maincell">
      <table cellpadding="5" border="0" width="100%">
        <tr>
          <td>
            <xsl:apply-templates select="html:body"/>
          </td>
        </tr>
      </table>
    </td>

    <td class="sidenav" valign="top"> 
      <p>About JSword</p>
      <ul>
        <li><a href="index.html">Home</a></li>
        <li><a href="news.html">News</a></li>
        <li><a href="change.html">Changes</a></li>
      </ul>
      <p>Getting-JSword</p>
      <ul>
        <li><a href="download.jsp">Download</a></li>
        <li><a href="http://www.crosswire.org/sword/modules/index.jsp">Modules</a></li>
        <li><a href="screenshot.html">Screenshots</a></li>
        <li><a href="demo.jsp">Web-Demo</a></li>
      </ul>
      <p>Getting-Involved</p>
      <ul>
        <li><a href="cvs.html">CVS</a></li>
        <li><a href="maillists.html">Mailing Lists</a></li>
      </ul>
      <p>Documentation</p>
      <ul>
        <li><a href="devt.html">Introduction</a></li>
        <li><a href="writingcode.html">Writing Code</a></li>
        <li><a href="java2html/org/crosswire/jsword/examples/APIExamples.java.html">API Primer</a></li>
        <li><a href="design.html">Design</a></li>
        <li><a href="osisCore.1.1.html">OSIS</a></li>
        <li><a href="api/org/crosswire/common/config/package-summary.html">Config API</a></li>
        <li><a href="api/index.html">JavaDoc</a></li>
        <li><a href="java2html/index.html">Java-Source</a></li>
        <li><a href="test/index.html">Test-Results</a></li>
        <li><a href="jcoverage/index.html">Test-Coverage</a></li>
        <li><a href="checkstyle/checkstyle_errors.html">CheckStyle</a></li>
        <li><a href="pmd/index.html">PMD Report</a></li>
        <li><a href="findbugs/report.txt">FindBugs Report</a></li>
        <li><a href="jdepend/jdepend-report.html">JDepend Report</a></li>
        <li><a href="javancss/index.html">NCSS Report</a></li>
        <li><a href="buildlog.txt">Build Log</a></li>
      </ul>
      <p>Other-Projects</p>
      <ul>
        <li><a href="http://www.crosswire.org/">Crosswire</a></li>
        <li><a href="http://www.crosswire.org/sword/index.jsp">Sword</a></li>
        <li><a href="http://www.sourceforge.net/projects/projectb/">Project-B</a></li>
      </ul>
    </td>

  </tr>
</table>

<table width="100%" cellpadding="3">
  <tr>
    <td class="navbutton" align="center">
      The SWORD Project; P. O. Box 2528; Tempe, AZ 85280-2528 USA
    </td>
  </tr>
</table>

</body>
</html>

</xsl:template>

<xsl:template match="html:head">
  <xsl:copy-of select="."/>
</xsl:template>

<xsl:template match="html:body">
  <xsl:copy-of select="node()"/>
</xsl:template>

<xsl:template match="html:body/@*">
  <xsl:copy/>
</xsl:template>

</xsl:stylesheet>
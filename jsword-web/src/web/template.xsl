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
    indent="yes"
    encoding="windows-1252"
    />
    <!-- Can't have a doctype in jsp!
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
    -->

<xsl:template match="/jsp:root">
  <jsp:root version="1.2">
    <xsl:apply-templates/>
  </jsp:root>
</xsl:template>

<xsl:template match="html:html">

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <link rel="stylesheet" type="text/css" href="sword.css"/>
    <xsl:copy-of select="html:head/*"/>
  </head>
  <body>
    <xsl:copy-of select="html:body/@*"/>

    <table width="100%">
      <tr align="center">
        <td class="navbutton" align="center"><a href="/">CrossWire</a></td>
        <td class="navbutton" align="center"><a href="index.html">JSword</a></td>
        <td class="navbutton" align="center"><a href="/bibledesktop">BibleDesktop</a></td>
        <td class="navbutton" align="center"><a href="/sword/">Sword</a></td>
        <td class="navbutton" align="center"><a href="/sword/about">Purpose Statement</a></td>
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
        <td class="navbutton" align="center"><a href="/bibledesktop/screenshot.html">Screenshots</a></td>      
        <td class="navbutton" align="center"><a href="download.jsp">Download</a></td>
        <td class="navbutton" align="center"><a href="/sword/modules">Module Add-ins</a></td>
      </tr>
    </table>

    <table width="100%" border="0">
  
      <tr> 

        <td valign="top" class="maincell">
          <table cellpadding="5" border="0" width="100%">
            <tr>
              <td>
                <xsl:copy-of select="html:body/*"/>
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
          <p>Getting JSword</p>
          <ul>
            <li><a href="download.jsp">Download</a></li>
            <li><a href="/sword/modules">Modules</a></li>
            <li><a href="/bibledesktop/screenshot.html">Screenshots</a></li>
            <!-- li><a href="demo.jsp">Web-Demo</a></li -->
          </ul>
          <p>Getting Involved</p>
          <ul>
            <li><a href="svn.html">SVN</a></li>
            <li><a href="maillists.html">Mailing Lists</a></li>
            <li><a href="/bugs">Issue Tracking</a></li>
            <li><a href="future.html">Roadmap</a></li>
          </ul>
          <p>Documentation</p>
          <ul>
            <li><a href="devt.html">Introduction</a></li>
            <li><a href="writingcode.html">Writing Code</a></li>
            <li><a href="design.html">Design</a></li>
            <li><a href="java2html/org/crosswire/jsword/examples/APIExamples.java.html">API Primer</a></li>
            <li><a href="api/org/crosswire/common/config/package-summary.html">Config API</a></li>
            <li><a href="api">JavaDoc</a></li>
            <li><a href="java2html">Java-Source</a></li>
          </ul>
          <p>Nightly Build</p>
          <ul>
            <li><a href="buildlog.txt">Build Log</a></li>
            <!-- li><a href="junit">Test-Results</a></li -->
            <!-- li><a href="jcoverage">Test-Coverage</a></li -->
            <li><a href="checkstyle">CheckStyle</a></li>
            <li><a href="pmd">PMD Report</a></li>
            <li><a href="cpd">CPD Report</a></li>
            <li><a href="findbugs">FindBugs Report</a></li>
            <li><a href="jdepend/jdepend-report.html">JDepend Report</a></li>
            <li><a href="javancss">NCSS Report</a></li>
          </ul>
          <p>Other Projects</p>
          <ul>
            <li><a href="/">CrossWire</a></li>
            <li><a href="/sword">Sword</a></li>
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

</xsl:stylesheet>

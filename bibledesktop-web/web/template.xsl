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
    <xsl:copy-of select="html:head/*"/>
  </head>
  <body>
    <xsl:copy-of select="html:body/@*"/>

    <xsl:copy-of select="html:body/*"/>

    <div id="side">
      <div class="lsidecell current"><a href="index.html">Home</a></div>
      <div class="lsidecell pagelink"><a href="download.html">Download</a></div>
      <div class="lsidecell pagelink"><a href="news.html">News</a></div>

      <p>
        Related Projects:
        <a href="/jsword">J-Sword</a><br/>
        <a href="/sword">Sword</a>
      </p>

      <div class="lsidecell subscribe">
        <form action="submit" method="post">
          Stay informed:<br/>
          <input type="text" size="10" value="your e-mail"/>
          <br/>
          <input type="submit" value="Subscribe"/>
        </form>
      </div>
    </div>

   </body>
</html>

</xsl:template>

</xsl:stylesheet>

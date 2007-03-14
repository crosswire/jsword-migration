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
    doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
    doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
    />

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
    <script type="text/javascript">
      <![CDATA[
      var thispage = location.href.substring(location.href.lastIndexOf("/")+1);
      function writeLink(parentName, dest, title)
      {
        var parent = document.getElementById(parentName);
        if (dest == thispage)
        {
          var div = document.createElement("div");
          parent.appendChild(div);
          div.setAttribute("class", "lsidecell current");
          div.appendChild(document.createTextNode(title));
        }
        else
        {
          var div = document.createElement("div");
          var a = document.createElement("a");
          parent.appendChild(div);
          div.setAttribute("class", "lsidecell pagelink");
          div.appendChild(a);
          a.setAttribute("href", dest);
          a.appendChild(document.createTextNode(title));
        }
      }

      function emptyIf(id, ifval)
      {
        var textele = document.getElementById(id);
        if (textele.value == ifval)
        {
          textele.value = "";
        }
      }
      ]]>
    </script>
  </head>
  <body>
    <xsl:copy-of select="html:body/@*"/>

    <xsl:copy-of select="html:body/*"/>

    <div id="side">
      <script type="text/javascript">
        <![CDATA[
        writeLink("side", "index.html", "Home");
        writeLink("side", "download.html", "Download");
        writeLink("side", "screenshot.html", "Screenshots");
        writeLink("side", "howto.html", "How-To");
        writeLink("side", "bibledesktop-manual.pdf", "Help");
        writeLink("side", "maillists.html", "Mailing-Lists");
        ]]>
      </script>

      <p>
        Other Bible Software on the Net:<br/>
        <a href="/sword/software/biblecs">The SWORD Project for Windows</a><br/>
        <a href="http://www.MacSword.com">MacSword</a><br/>
        <a href="http://www.BibleTime.info">BibleTime for Linux</a>
        <a href="http://gnomesword.sourceforge.net">GnomeSword for Linux</a>
      </p>

      <p>
        Related Projects:<br/>
        <a href="/jsword">JSword</a><br/>
        <a href="/sword">Sword</a>
      </p>

      <div class="lsidecell subscribe">
        <form method="post" action="/mailman/subscribe/bibledesktop-announce">
          <input type='hidden' name="digest" value="0"/>
          Stay informed:<br/>
          <input type="text" size="10" id="email" name="email" value="your e-mail" onfocus="emptyIf('email', 'your e-mail');"/>
          <br/>
          <input type="submit" name="email-button" value="Subscribe"/>
        </form>
      </div>
    </div>

   </body>
</html>

</xsl:template>

</xsl:stylesheet>

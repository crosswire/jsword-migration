<?xml version="1.0" encoding="iso-8859-1"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>JSword - Download</title>
</head>
<body>

<jsp:directive.page import="org.crosswire.jsword.view.web.DownloadSet" contentType="text/html"/>

<jsp:scriptlet><![CDATA[
  String webappJSword = application.getInitParameter("webapp.jsword");
  if (webappJSword == null)
  {
      throw new NullPointerException("webapp.jsword");
  }

  String ftpPrefix = application.getInitParameter("ftp.prefix");
  if (ftpPrefix == null)
  {
      throw new NullPointerException("ftp.prefix");
  }
]]></jsp:scriptlet>

<h1>Stable Release</h1>

<h2>Webstart</h2>
<p>
  The Desktop GUI of J-Sword is called Bible Desktop and it has it's own
  website along with a Webstart realease. The Webstart download page is
  <a href="/bibledesktop/download.html">here</a>.
</p>

<h2>Zip/Tar Based Downloads</h2>
<p>We keep official releases hanging around for a while.</p>
<table width="90%" align="center" border="1" bordercolor="#000000" cellspacing="0" cellpadding="2">
  <tr>
    <td>-</td>
    <td colspan="2" align="center">Binary</td>
    <td colspan="2" align="center">Source</td>
    <td colspan="2" align="center">Docs</td>
  </tr>
  <tr>
    <td>Compression</td>
    <td>.zip</td>
    <td>.tar.gz</td>
    <td>.zip</td>
    <td>.tar.gz</td>
    <td>.zip</td>
    <td>.tar.gz</td>
  </tr>
  <jsp:scriptlet><![CDATA[
  DownloadSet[] dls = DownloadSet.getDownloadSets(webappJSword + "/release", ftpPrefix + "/release", false);
  for (int i=0; i<dls.length; i++)
  {
  ]]></jsp:scriptlet>
  <tr>
    <td><jsp:expression>dls[i].getVersionString()</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.BIN_ZIP)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.BIN_TGZ)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.SRC_ZIP)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.SRC_TGZ)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.DOC_ZIP)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.DOC_TGZ)</jsp:expression></td>
  </tr>
  <jsp:scriptlet><![CDATA[
  }
  ]]></jsp:scriptlet>
</table>


<h2>Zip/Tar Based Downloads</h2>
<p>
  Regular releases are made and stored for a short time. You will need to use GNU
  tar to extract the doc.tar.gz files, although any tar should do for the others.
</p>
<table width="90%" align="center" border="1" bordercolor="#000000" cellspacing="0" cellpadding="2">
  <tr>
    <td>-</td>
    <td colspan="2" align="center">Binary</td>
    <td colspan="2" align="center">Source</td>
    <td colspan="2" align="center">Docs</td>
  </tr>
  <tr>
    <td>Compression</td>
    <td>.zip</td>
    <td>.tar.gz</td>
    <td>.zip</td>
    <td>.tar.gz</td>
    <td>.zip</td>
    <td>.tar.gz</td>
  </tr>
  <jsp:scriptlet><![CDATA[
  dls = DownloadSet.getDownloadSets(webappJSword + "/nightly", ftpPrefix + "/nightly", true);
  for (int i=0; i<dls.length; i++)
  {
  ]]></jsp:scriptlet>
  <tr>
    <td><jsp:expression>dls[i].getDateString()</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.BIN_ZIP)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.BIN_TGZ)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.SRC_ZIP)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.SRC_TGZ)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.DOC_ZIP)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.DOC_TGZ)</jsp:expression></td>
  </tr>
  <jsp:scriptlet><![CDATA[
  }
  ]]></jsp:scriptlet>
</table>

<h3>CVS Access</h3>
<p>
The most up to date access is via CVS. There are CVS access instruction 
on the <a href="devt.html">Getting Involved</a> page.
</p>

<h3>Modules</h3>
<p>
Sword modules are available <a href="http://www.crossire.org/sword/modules/index.jsp">here</a>. 
Most of these modules are working with JSword so please report any that fail.
</p>

</body>
</html>
</jsp:root>

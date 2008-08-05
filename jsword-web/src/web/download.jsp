<?xml version="1.0" encoding="iso-8859-1"?>
<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="2.0">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <title>JSword - Download</title>
</head>
<body>

<jsp:directive.page import="org.crosswire.jsword.view.web.DownloadSet" contentType="text/html"/>

<jsp:scriptlet><![CDATA[
  String scm = "SVN";

  String ftpBase = application.getInitParameter("ftp.base");
  if (ftpBase == null)
  {
      throw new NullPointerException("ftp.base");
  }

  String ftpPrefix = application.getInitParameter("ftp.prefix");
  if (ftpPrefix == null)
  {
      throw new NullPointerException("ftp.prefix");
  }
]]></jsp:scriptlet>

<h1>Bible Desktop</h1>
<p>
  The Desktop GUI of JSword is called Bible Desktop and it has it's own
  <a href="/bibledesktop/download.html">download page</a>. This provides a WebStart installer and
  installers for Windows, Linux and Apple.
</p>

<h1>JSword</h1>
<h2>Stable Release</h2>
<p>
  If the Bible Desktop installers do not work for you or you want an earlier release,
  you can obtain a binary install from here. These files are in tar.gz format. Some require GNU tar.
</p>
<p>
  We keep official releases hanging around for a long, long time.
</p>
<p>
  Source does not contain third party source. You can obtain these from <jsp:expression>scm</jsp:expression>.
</p>
<table width="50%" align="center" border="1" bordercolor="#000000" cellspacing="0" cellpadding="2">
  <tr>
    <td>-</td>
    <td align="center">Binary</td>
    <td align="center">Source</td>
    <td align="center">Docs</td>
  </tr>
  <jsp:scriptlet><![CDATA[
  DownloadSet[] dls = DownloadSet.getDownloadSets(ftpBase + "/release", ftpPrefix + "/release", false);
  for (int i=0; i<dls.length; i++)
  {
  ]]></jsp:scriptlet>
  <tr>
    <td><jsp:expression>dls[i].getVersionString()</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.BIN_TGZ)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.SRC_TGZ)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.DOC_TGZ)</jsp:expression></td>
  </tr>
  <jsp:scriptlet><![CDATA[
  }
  ]]></jsp:scriptlet>
</table>


<h2>Nightly Builds</h2>
<p>
  Nightly builds are made and stored for a short time. These files are in tar.gz format. Some require GNU tar.
</p>

<p>
  Source does not contain third party source. You can obtain these from <jsp:expression>scm</jsp:expression>.
</p>
<table width="50%" align="center" border="1" bordercolor="#000000" cellspacing="0" cellpadding="2">
  <tr>
    <td>-</td>
    <td align="center">Binary</td>
    <td align="center">Source</td>
    <td align="center">Docs</td>
  </tr>
  <jsp:scriptlet><![CDATA[
  dls = DownloadSet.getDownloadSets(ftpBase + "/nightly", ftpPrefix + "/nightly", true);
  for (int i=0; i<dls.length; i++)
  {
  ]]></jsp:scriptlet>
  <tr>
    <td><jsp:expression>dls[i].getDateString()</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.BIN_TGZ)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.SRC_TGZ)</jsp:expression></td>
    <td><jsp:expression>dls[i].getLinkString(DownloadSet.DOC_TGZ)</jsp:expression></td>
  </tr>
  <jsp:scriptlet><![CDATA[
  }
  ]]></jsp:scriptlet>
</table>

<h3><jsp:expression>scm</jsp:expression> Access</h3>
<p>
<jsp:expression>scm</jsp:expression> provides complete and up to data access. Also, all releases are tagged so you can get the full source of any prior stable release.
There are <jsp:expression>scm</jsp:expression> access instruction on the <a href="devt.html">Getting Involved</a> page.
</p>

</body>
</html>
</jsp:root>

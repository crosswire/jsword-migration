<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
	<!ENTITY nbsp "&#160;">
]>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:param name="messagefile" select="'file:///D:/Java/findbugs-0.6.5/etc/messages.xml'"/>
	<xsl:variable name="messagedoc" select="document($messagefile)"/>
	<xsl:template match="/">
		<html>
			<head>
				<title>Findbugs Report</title>
				<style type="text/css">
body {
	color: #000000;
	font: normal 68% verdana,arial,helvetica;
}

h1 {
	font: 165% verdana,arial,helvetica;
	margin: 0px 0px 5px;
}

h2 {
	font: bold 125% verdana,arial,helvetica;
	margin-bottom: 0.5em;
	margin-top: 1em;
}

h3 {
	font: bold 115% verdana,arial,helvetica;
	margin-bottom: 0.5em;
}

h6, h4, h5 {
	font: bold 100% verdana,arial,helvetica;
	margin-bottom: 0.5em;
}

p {
	line-height: 1.5em;
	margin: 0.5em 2em 1.0em;
}

table tr td, tr th {
	font-size: 68%;
}

table.details tr td{
	background: #eeeee0;
}

table.details tr th{
	background: #a6caf0;
	font-weight: bold;
	text-align: left;
}

.error {
	color: red;
	font-weight: bold;
}

.failure {
	color: purple;
	font-weight: bold;
}

.priority1{
	background: Red;
	height: 0.5em;
	margin-right: 0.5em;
	width: 1em;
}

.priority2{
	background: Orange;
	height: 0.5em;
	margin-right: 0.5em;
	width: 1em;
}

.priority3{
	background: Green;
	height: 0.5em;
	margin-right: 0.5em;
	width: 1em;
}

.bugClassname{
	background: #a7a6f0;
}

.properties {
	text-align: right;
}
        </style>
			</head>
			<body>
				<h1>
					<a name="top">Findbugs Audit</a>
				</h1>
				<p align="right">Designed for use with <a href="http://www.cs.umd.edu/~pugh/java/bugs/">Findbugs</a> and <a href="http://jakarta.apache.org">Ant</a>.</p>
				<hr size="2"/>
				<h2>Summary</h2>
				<table border="0" id="summary" class="details">
					<tbody>
						<tr>
							<th colspan="3">Summary</th>
						</tr>
						<tr>
							<th/>
							<th>Count</th>
							<th>Bugs</th>
						</tr>
						<tr>
							<th>Outer Classes :</th>
							<td>
								<xsl:value-of select="count(descendant::AppClass[ not(contains(text(),'$')) and not(@interface) ])"/>
							</td>
							<td>
								<xsl:value-of select="count(descendant::BugInstance/Class[ not(contains(@classname,'$')) and not(@interface) ])"/>
							</td>
						</tr>
						<tr>
							<th>Inner Classes :</th>
							<td>
								<xsl:value-of select="count(descendant::AppClass[contains(text(), '$') and not(@interface) ])"/>
							</td>
							<td>
								<xsl:value-of select="count(descendant::BugInstance/Class[contains(@classname,'$') and not(@interface)])"/>
							</td>
						</tr>
						<tr>
							<th>Interfaces :</th>
							<td>
								<xsl:value-of select="count(descendant::AppClass/@interface)"/>
							</td>
							<td>
								<!--xsl:value-of select="count(descendant::BugInstance/Class[contains(@classname,'$')])"/-->
							</td>
						</tr>
						<tr>
							<th>Total :</th>
							<td>
								<xsl:number level="any" value="count(descendant::AppClass)"/>
							</td>
							<td>
								<xsl:number level="any" value="count(descendant::BugInstance)"/>
							</td>
						</tr>
					</tbody>
				</table>
				<h2>Bug Details</h2>
				<table id="bugInstance" class="details" width="100%">
					<tbody>
						<xsl:for-each select="//BugInstance">
							<tr>
								<th colspan="2" class="bugClassname">
									<xsl:element name="span">
										<xsl:attribute name="class">priority<xsl:value-of select="@priority"/></xsl:attribute>
										<!--xsl:value-of select="@priority"/-->&nbsp;
									</xsl:element>&nbsp;<xsl:value-of select="Class/@classname"/>
								</th>
							</tr>
							<tr>
								<th>Type :</th>
								<td>
									<xsl:value-of select="substring-before(@type, '_')"/> - 
								<xsl:variable name="type" select="@type"/>
									<xsl:value-of select="$messagedoc//BugPattern[@type = $type]/ShortDescription"/>
								</td>
							</tr>
							<xsl:for-each select="Method">
								<tr>
									<th>Method :</th>
									<td>
										<xsl:value-of select="@name"/>
									</td>
								</tr>
							</xsl:for-each>
							<xsl:if test="Field">
								<tr>
									<th>Field :</th>
									<td>
										<xsl:value-of select="Field/@name"/>
									</td>
								</tr>
							</xsl:if>
							<xsl:if test="SourceLine">
								<tr>
									<th>Source :</th>
									<td>
										<xsl:value-of select="SourceLine/@sourcefile"/>
										<xsl:if test="SourceLine[not(contains(@start, '-1'))]">, 
 											line <xsl:value-of select="SourceLine/@start"/>
										</xsl:if>
									</td>
								</tr>
							</xsl:if>
							<tr>
								<td class="description" colspan="2">
									<xsl:variable name="type" select="@type"/>
									<xsl:value-of disable-output-escaping="yes" select="$messagedoc//BugPattern[@type = $type]/Details"/>
								</td>
							</tr>
							<tr>
								<td style="height:1em;background:White;"/>
							</tr>
						</xsl:for-each>
					</tbody>
				</table>
			</body>
		</html>
	</xsl:template>
</xsl:stylesheet>

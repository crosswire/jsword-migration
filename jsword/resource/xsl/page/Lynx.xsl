<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

<xsl:template match='bible'>
  <br/>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='title'>
  <h2 align='center'>
  <xsl:apply-templates/>
  </h2>
</xsl:template>

<xsl:template match='section'>
  <h4><xsl:value-of select='@title'/></h4>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='ref'>
  <xsl:text> </xsl:text>
  <xsl:if test="self::node()[@para='true']"><br/></xsl:if>
  <xsl:text> </xsl:text>
  <font size="-2" color='#666666'><xsl:value-of select='@v'/></font>
  <xsl:text> </xsl:text>
  <xsl:apply-templates select='*'/>
</xsl:template>

<xsl:template match='it'>
  <xsl:apply-templates/>
</xsl:template>

<xsl:template match='*|@*' priority='-1'>
  <xsl:copy>
    <xsl:apply-templates select='@*|*|text()'/>
  </xsl:copy>
</xsl:template>

</xsl:stylesheet>

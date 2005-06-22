<?xml version="1.0"?>
<xsl:stylesheet xmlns="http://www.w3.org/TR/REC-html40" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html" omit-xml-declaration="yes" indent="no"/>
  <!-- Be very careful about introducing whitespace into the document.
       strip-space merely remove space between one tag and another tag.
       This may cause significant whitespace to be removed.
       
       It is easy to have apply-templates on a line to itself which if
       it encounters text before anything else will introduce whitespace.
       With the browser we are using span will introduce whitespace,
       but font does not. Therefore we use font as a span.
    -->
  <!-- gdef and hdef refer to hebrew and greek definitions keyed by strongs -->
  <xsl:param name="greek.def.protocol" select="'gdef:'"/>
  <xsl:param name="hebrew.def.protocol" select="'hdef:'"/>
  <!-- currently these are not used, but they are for morphologic forms -->
  <xsl:param name="greek.morph.protocol" select="'gmorph:'"/>
  <xsl:param name="hebrew.morph.protocol" select="'hmorph:'"/>

  <!-- Whether to show Strongs or not -->
  <xsl:param name="Strongs" select="'false'"/>

  <!-- Whether to show morphologic forms or not -->
  <xsl:param name="Morph" select="'false'"/>

  <!-- Whether to start each verse on an new line or not -->
  <xsl:param name="VLine" select="'false'"/>

  <!-- Whether to show notes or not -->
  <xsl:param name="Notes" select="'true'"/>

  <!-- Whether to have linking cross references or not -->
  <xsl:param name="XRef" select="'true'"/>

  <!-- Whether to output no Verse numbers -->
  <xsl:param name="NoVNum" select="'false'"/>

  <!-- Whether to output Verse numbers or not -->
  <xsl:param name="VNum" select="'true'"/>

  <!-- Whether to output Chapter and Verse numbers or not -->
  <xsl:param name="CVNum" select="'false'"/>

  <!-- Whether to output Book, Chapter and Verse numbers or not -->
  <xsl:param name="BCVNum" select="'false'"/>

  <!-- Whether to output superscript verse numbers or normal size ones -->
  <xsl:param name="TinyVNum" select="'true'"/>

  <!-- The CSS stylesheet to use. The url must be absolute. -->
  <xsl:param name="css"/>
  
  <!-- The order of display. Hebrew is rtl (right to left) -->
  <xsl:param name="direction" select="'ltr'"/>

  <!--
  The font that is passed in is of the form: font or font,style,size 
  where style is a bit mask with 1 being bold and 2 being italic.
  This needs to be changed into a style="xxx" specification
  -->
  <xsl:param name="font" select="Serif"/>
  <xsl:variable name="aFont">
    <xsl:choose>
      <xsl:when test="substring-before($font, ',') = ''"><xsl:value-of select="$font"/>,0,16</xsl:when>
      <xsl:otherwise><xsl:value-of select="$font"/></xsl:otherwise>
    </xsl:choose>
  </xsl:variable>
  <xsl:variable name="fontfamily" select='concat("font-family: &apos;", substring-before($aFont, ","), "&apos;;")' />
  <xsl:variable name="fontsize" select="concat(' font-size: ', substring-after(substring-after($aFont, ','), ','), 'pt;')" />
  <xsl:variable name="styling" select="substring-before(substring-after($aFont, ','), ',')" />
  <xsl:variable name="fontweight">
    <xsl:if test="$styling = '1' or $styling = '3'"><xsl:text> font-weight: bold;</xsl:text></xsl:if>
  </xsl:variable>
  <xsl:variable name="fontstyle">
    <xsl:if test="$styling = '2' or $styling = '3'"> font-style: italic;</xsl:if>
  </xsl:variable>
  <xsl:variable name="fontspec" select="concat($fontfamily, $fontsize, $fontweight, $fontstyle)"/>

  <!--
  For now, we assume that all the works inside a corpus are of the
  same type.
  -->
  <xsl:variable name="osis-id-type" select="substring-before((//osisText)[1]/@osisIDWork, '.')"/>

  <xsl:variable name="page-div-type">
    <xsl:choose>
      <!--
      KJV is a special case. It should be Bible.KJV, but some OSIS
      transcriptions just use KJV instead.
      -->
      <xsl:when test="$osis-id-type = 'Bible' or $osis-id-type = 'KJV'">
        <xsl:text>chapter</xsl:text>
      </xsl:when>
      <xsl:when test="$osis-id-type = 'Dictionary'">
        <xsl:text>x-lexeme</xsl:text>
      </xsl:when>
      <xsl:when test="$osis-id-type = 'Lexicon'">
        <xsl:text>x-lemma</xsl:text>
      </xsl:when>
      <xsl:when test="$osis-id-type = 'Morph'">
        <xsl:text>x-tag</xsl:text>
      </xsl:when>
      <xsl:otherwise>
        <xsl:text>FIXME</xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>

  <!--=======================================================================-->
  <xsl:template match="/osis">
    <html dir="{$direction}">
      <head>
        <xsl:if test="$css != ''">
          <link rel="stylesheet" type="text/css" href="{$css}" title="styling" />
        </xsl:if>
        <style type="text/css">
          BODY { <xsl:value-of select="$fontspec" /> }
          A { text-decoration: none; }
          A.strongs { color: black; text-decoration: none; }
          SUB.strongs { font-size: 75%; color: red; }
          SUB.morph { font-size: 75%; color: blue; }
          SUB.lemma { font-size: 75%; color: red; }
          SUP.verse { font-size: 75%; color: gray; }
          SUP.note { font-size: 75%; color: green; }
          FONT.jesus { color: red; }
          FONT.speech { color: blue; }
          FONT.transChange { font-style: italic; }
          h3 { font-size: 110%; color: #666699; font-weight: bold; }
          h2 { font-size: 115%; color: #669966; font-weight: bold; }
          div.margin { font-size:90%; }
          TD.notes { width:100px; background:#f4f4e8; }
          TD.text { }
        </style>
      </head>
      <body>
        <!-- If there are notes, output a table with notes in the 2nd column. -->
        <xsl:choose>
          <xsl:when test="$Notes = 'true' and //note">
            <xsl:choose>
              <xsl:when test="$direction != 'rtl'">
	            <table cols="2" cellpadding="5" cellspacing="5">
	              <tr>
	                <td valign="top" class="text">
	                  <xsl:apply-templates/>
	                </td>
	                <td valign="top" class="notes">
	                  <p>&#160;</p>
	                  <xsl:apply-templates select="//verse" mode="print-notes"/>
	                </td>
	              </tr>
	            </table>
              </xsl:when>
              <xsl:otherwise>
                <!-- reverse the table for Right to Left languages -->
	            <table cols="2" cellpadding="5" cellspacing="5">
	              <!-- In a right to left, the alignment should be reversed too -->
	              <tr align="right">
	                <td valign="top" class="notes">
	                  <p>&#160;</p>
	                  <xsl:apply-templates select="//note" mode="print-notes"/>
	                </td>
	                <td valign="top" class="text">
	                  <xsl:apply-templates/>
	                </td>
	              </tr>
	            </table>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:when>
          <xsl:otherwise>
            <xsl:apply-templates/>
          </xsl:otherwise>
        </xsl:choose>
      </body>
    </html>
  </xsl:template>

  <!--=======================================================================-->
  <!-- Avoid adding whitespace -->
  <xsl:template match="osisCorpus">
    <xsl:for-each select="osisText">
      <!-- If this text has a header, apply templates to the header. -->
      <xsl:if test="preceding-sibling::*[1][self::header]">
        <div class="corpus-text-header"><xsl:apply-templates select="preceding-sibling::*[1][self::header]"/></div>
      </xsl:if>
      <xsl:apply-templates select="."/>
    </xsl:for-each>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="osisText">
    <xsl:apply-templates/>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="div">
    <xsl:if test="@divTitle">
      <h1><xsl:value-of select="@divTitle"/></h1>
    </xsl:if>
    <xsl:if test="@type = 'testament'">
      <h2>
        <xsl:choose>
          <xsl:when test="preceding::div[@type = 'testament']">
           <xsl:text>New Testament</xsl:text>
          </xsl:when>
          <xsl:otherwise>
            <xsl:text>Old Testament</xsl:text>
          </xsl:otherwise>
        </xsl:choose>
      </h2>
    </xsl:if>
    <xsl:apply-templates/>
    <xsl:if test="@divTitle">
      <p>&#0160;</p>
    </xsl:if>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="verse">
    <!-- If the verse don't start on their own line and -->
    <!-- the verse is not the first verse of a set of siblings, -->
    <!-- output an extra space. -->
    <xsl:if test="$VLine = 'false' and preceding-sibling::*[local-name() = 'verse']">
      <xsl:text>&#160;</xsl:text>
    </xsl:if>
    <xsl:variable name="title" select=".//title"/>
    <xsl:if test="string-length($title) > 0">
      <h3><xsl:value-of select="$title"/></h3>
    </xsl:if>
    <!-- Always output the verse -->
    <xsl:choose>
 	  <xsl:when test="$VLine = 'true'">
        <div class="l"><a name="{@osisID}"><xsl:call-template name="versenum"/></a><xsl:apply-templates/></div>
      </xsl:when>
      <xsl:otherwise>
        <xsl:call-template name="versenum"/><xsl:apply-templates/>
        <!-- Follow the verse with an extra space -->
        <!-- when they don't start on lines to themselves -->
        <xsl:text> </xsl:text>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="versenum">
    <!-- Are verse numbers wanted? -->
    <xsl:if test="$NoVNum = 'false'">
      <xsl:variable name="book" select="substring-before(@osisID, '.')"/>
      <xsl:variable name="chapter" select="substring-before(substring-after(@osisID, '.'), '.')"/>
      <xsl:variable name="verse" select="substring-after(substring-after(@osisID, '.'), '.')"/>
      <xsl:variable name="versenum">
        <xsl:choose>
          <xsl:when test="$BCVNum = 'true'">
          	<xsl:value-of select="concat($book, '&#160;', $chapter, ':', $verse)"/>
          </xsl:when>
          <xsl:when test="$CVNum = 'true'">
          	<xsl:value-of select="concat($chapter, ':', $verse)"/>
          </xsl:when>
          <xsl:otherwise>
          	<xsl:value-of select="$verse"/>
          </xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:choose>
        <xsl:when test="$TinyVNum = 'true' and $Notes = 'true'">
      	  <a name="{@osisID}"><sup class="verse"><xsl:value-of select="$versenum"/></sup></a>
      	</xsl:when>
        <xsl:when test="$TinyVNum = 'true' and $Notes = 'false'">
      	  <sup class="verse"><xsl:value-of select="$versenum"/></sup>
      	</xsl:when>
        <xsl:when test="$TinyVNum = 'false' and $Notes = 'true'">
      	  <a name="{@osisID}">(<xsl:value-of select="$versenum"/>)</a>
      	  <xsl:text> </xsl:text>
      	</xsl:when>
      	<xsl:otherwise>
      	  (<xsl:value-of select="$versenum"/>)
      	  <xsl:text> </xsl:text>
      	</xsl:otherwise>
      </xsl:choose>
    </xsl:if>
    <xsl:if test="$VNum = 'false' and $Notes = 'true'">
      <a name="{@osisID}"></a>
    </xsl:if>
  </xsl:template>

  <xsl:template match="verse" mode="print-notes">
    <xsl:if test="./note">
      <xsl:variable name="book" select="substring-before(@osisID, '.')"/>
      <xsl:variable name="chapter" select="substring-before(substring-after(@osisID, '.'), '.')"/>
      <xsl:variable name="versenum" select="substring-after(substring-after(@osisID, '.'), '.')"/>
      <a href="#{@osisID}">
        <xsl:value-of select="concat($book, '&#160;', $chapter, ':', $versenum)"/>
      </a>
      <xsl:apply-templates select="./note" mode="print-notes" />
      <div><xsl:text>&#160;</xsl:text></div>
    </xsl:if>
  </xsl:template>

  <!--=======================================================================-->
  <!-- Avoid adding whitespace -->
  <xsl:template match="a">
    <a href="{@href}"><xsl:apply-templates/></a>
  </xsl:template>

  <!--=======================================================================-->
  <!-- Avoid adding whitespace -->
  <!-- When we encounter a note, we merely output a link to the note. -->
  <xsl:template match="note">
    <xsl:if test="$Notes = 'true'">
      <!-- If the preceeding sibling was a note, emit a separator -->
      <xsl:choose>
        <xsl:when test="following-sibling::*[1][self::note]">
          <sup class="note"><a href="#note-{generate-id(.)}"><xsl:number level="any" from="/osis//verse" format="a"/></a>, </sup>
        </xsl:when>
        <xsl:otherwise>
          <sup class="note"><a href="#note-{generate-id(.)}"><xsl:number level="any" from="/osis//verse" format="a"/></a></sup>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:if>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="note" mode="print-notes">
    <div class="margin">
      <b><xsl:number level="any" from="/osis//verse" format="a"/></b>
      <a name="note-{generate-id(.)}">
        <xsl:text> </xsl:text>
      </a>
      <xsl:apply-templates/>
    </div>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="p">
    <p><xsl:apply-templates/></p>
  </xsl:template>
  
  <!--=======================================================================-->
  <xsl:template match="p" mode="print-notes">
    <!-- FIXME: This ignores text in the note. -->
    <!-- don't put para's in notes -->
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="w">
    <!-- Output the content followed by all the lemmas and then all the morphs. -->
    <xsl:apply-templates/>
    <xsl:if test="$Strongs = 'true' and (starts-with(@lemma, 'x-Strongs:') or starts-with(@lemma, 'strong:'))">
      <xsl:call-template name="lemma">
        <xsl:with-param name="lemma" select="@lemma"/>
      </xsl:call-template>
    </xsl:if>
    <xsl:if test="$Morph = 'true' and (starts-with(@morph, 'x-Robinson:') or starts-with(@morph, 'robinson:'))">
      <xsl:call-template name="morph">
        <xsl:with-param name="morph" select="@morph"/>
      </xsl:call-template>
    </xsl:if>
    <!--
        except when followed by a text node or non-printing node.
        This is true whether the href is output or not.
    -->
    <xsl:variable name="siblings" select="../child::node()"/>
    <xsl:variable name="next-position" select="position() + 1"/>
    <xsl:if test="$siblings[$next-position] and name($siblings[$next-position]) != ''">
      <xsl:text> </xsl:text>
    </xsl:if>
  </xsl:template>
  
  <xsl:template name="lemma">
    <xsl:param name="lemma"/>
    <xsl:param name="part" select="0"/>
    <xsl:variable name="orig-lemma" select="substring-after($lemma, ':')"/>
    <xsl:variable name="protocol">
      <xsl:choose>
        <xsl:when test="substring($orig-lemma, 1, 1) = 'H'">
          <xsl:value-of select="$hebrew.def.protocol"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$greek.def.protocol"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="separator">
      <xsl:choose>
        <xsl:when test="contains($orig-lemma, '|')">
          <xsl:value-of select="'|'"/>
        </xsl:when>
        <xsl:when test="contains($orig-lemma, ' ')">
          <xsl:value-of select="' '"/>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:variable name="sub">
      <xsl:choose>
        <xsl:when test="$separator != '' and $part = '0'">
          <xsl:value-of select="$part + 1"/>
        </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="$part"/>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$separator = ''">
        <sub class="strongs"><a href="{$protocol}{$orig-lemma}">S<xsl:number level="any" from="/osis//verse" format="1"/><xsl:number value="$sub" format="a"/></a></sub>
      </xsl:when>
      <xsl:otherwise>
        <sub class="strongs"><a href="{$protocol}{substring-before($orig-lemma, $separator)}">S<xsl:number level="single" from="/osis//verse" format="1"/><xsl:number value="$sub" format="a"/></a>, </sub>
        <xsl:call-template name="lemma">
          <xsl:with-param name="lemma" select="substring-after($lemma, $separator)"/>
          <xsl:with-param name="part">
            <xsl:choose>
              <xsl:when test="$sub">
                <xsl:value-of select="$sub + 1"/>
              </xsl:when>
              <xsl:otherwise>
                <xsl:value-of select="1"/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:with-param>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!-- FIXME: Only handles one Robinsons Greek morphology (which is the only morph module today) -->
  <xsl:template name="morph">
    <xsl:param name="morph"/>
    <xsl:variable name="orig-morph" select="substring-after($morph, ':')"/>
    <xsl:variable name="protocol" select="$greek.morph.protocol"/>
    <xsl:variable name="separator">
      <xsl:choose>
        <xsl:when test="contains($orig-morph, '|')">
          <xsl:value-of select="'|'"/>
        </xsl:when>
        <xsl:when test="contains($orig-morph, ' ')">
          <xsl:value-of select="' '"/>
        </xsl:when>
      </xsl:choose>
    </xsl:variable>
    <xsl:choose>
      <xsl:when test="$separator = ''">
        <sub class="morph"><a href="{$protocol}{$orig-morph}">M<xsl:number level="any" from="/osis//verse" format="1"/></a></sub>
      </xsl:when>
      <xsl:otherwise>
        <sub class="morph"><a href="{$protocol}{$orig-morph}">M<xsl:number level="any" from="/osis//verse" format="1"/></a></sub>
        <xsl:call-template name="morph">
          <xsl:with-param name="morph" select="substring-after($morph, $separator)"/>
        </xsl:call-template>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--=======================================================================-->
  <!-- Avoid adding whitespace -->
  <xsl:template match="seg">
    <xsl:choose>
      <xsl:when test="starts-with(@type, 'color:')">
        <font color="substring-before(substring-after(@type, 'color: '), ';')"><xsl:apply-templates/></font>
      </xsl:when>
      <xsl:when test="starts-with(@type, 'font-size:')">
        <font size="substring-before(substring-after(@type, 'font-size: '), ';')"><xsl:apply-templates/></font>
      </xsl:when>
      <xsl:when test="@type = 'x-variant'">
        <xsl:if test="@subType = 'x-class:1'">
          <xsl:apply-templates/>
        </xsl:if>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!--=======================================================================-->
  <!-- FIXME: Should we both expand and output?? -->
  <xsl:template match="abbr">
    <abbr class="abbr">
      <xsl:if test="@expansion">
        <xsl:attribute name="title">
          <xsl:value-of select="@expansion"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </abbr>
  </xsl:template>

  <!--=======================================================================-->
  <!-- Avoid adding whitespace -->
  <xsl:template match="speaker">
    <xsl:choose>
      <xsl:when test="@who='Jesus'">
        <font class="jesus"><xsl:apply-templates/></font>
      </xsl:when>
      <xsl:otherwise>
        <font class="speech"><xsl:apply-templates/></font>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <!--=======================================================================-->
  <!-- Avoid adding whitespace -->
  <xsl:template match="title">
    <h2><xsl:apply-templates/></h2>
  </xsl:template>

  <!--=======================================================================-->
  <xsl:template match="title[@type='section']">
  <!-- Done by a line in [verse]
    <h3>
      <xsl:apply-templates/>
    </h3>
  -->
  </xsl:template>

  <!--=======================================================================-->
  <!-- Avoid adding whitespace -->
  <xsl:template match="reference">
    <xsl:choose>
      <xsl:when test="$XRef = 'true'">
        <a href="bible://{@osisRef}"><xsl:apply-templates/></a>
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates/>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!--=======================================================================-->
  <!-- Avoid adding whitespace -->
  <xsl:template match="caption">
    <div class="caption"><xsl:apply-templates/></div>
  </xsl:template>
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="catchWord">
    <font class="catchWord"><xsl:apply-templates/></font>
  </xsl:template>
  
  <!--
      <cell> is handled shortly after <table> below and thus does not appear
      here.
  -->
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="closer">
    <div class="closer"><xsl:apply-templates/></div>
  </xsl:template>
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="date">
    <font class="date"><xsl:apply-templates/></font>
  </xsl:template>
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="divineName">
    <font class="divineName"><xsl:apply-templates/></font>
  </xsl:template>
  
  <xsl:template match="figure">
    <div class="figure">
      <img src="@src"/>  <!-- FIXME: Not necessarily an image... -->
      <xsl:apply-templates/>
    </div>
  </xsl:template>
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="foreign">
    <em class="foreign"><xsl:apply-templates/></em>
  </xsl:template>
  
  <!-- This is a subheading. -->
  <!-- Avoid adding whitespace -->
  <xsl:template match="head//head">
    <h5 class="head"><xsl:apply-templates/></h5>
  </xsl:template>
  
  <!-- This is a top-level heading. -->
  <!-- Avoid adding whitespace -->
  <xsl:template match="head">
    <h4 class="head"><xsl:apply-templates/></h4>
  </xsl:template>
  
  <xsl:template match="index">
    <a name="index{@id}" class="index"/>
  </xsl:template>
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="inscription">
    <font class="inscription"><xsl:apply-templates/></font>
  </xsl:template>
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="item">
    <li class="item"><xsl:apply-templates/></li>
  </xsl:template>
  
  <!--
      <item> and <label> are covered by <list> below and so do not appear here.
  -->
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="lg">
    <div class="lg"><xsl:apply-templates/></div>
  </xsl:template>
  
  <!-- Avoid adding whitespace -->
  <!-- We add a single space to the end of the line because of a bug in Sun's rendering. -->
  <xsl:template match="l">
    <div class="l"><xsl:apply-templates/></div>
  </xsl:template>
  
  <!-- Avoid adding whitespace -->
  <!-- While a BR is a break, if it is immediately followed by punctuation,
       indenting this rule can introduce whitespace.
       We use <div class="l"></div> here because <br/> does not work. Nor does <br>
    -->
  <xsl:template match="lb"><div class="l"></div></xsl:template>
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="list">
    <xsl:choose>
      <xsl:when test="label">
        <!-- If there are <label>s in the list, it's a <dl>. -->
        <dl class="list">
          <xsl:for-each select="node()">
            <xsl:choose>
              <xsl:when test="self::label">
                <dt class="label"><xsl:apply-templates/></dt>
              </xsl:when>
              <xsl:when test="self::item">
                <dd class="item"><xsl:apply-templates/></dd>
              </xsl:when>
              <xsl:when test="self::list">
                <dd class="list-wrapper"><xsl:apply-templates select="."/></dd>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </dl>
      </xsl:when>

      <xsl:otherwise>
        <!-- If there are no <label>s in the list, it's a plain old <ul>. -->
        <ul class="list">
          <xsl:for-each select="node()">
            <xsl:choose>
              <xsl:when test="self::item">
                <li class="item"><xsl:apply-templates/></li>
              </xsl:when>
              <xsl:when test="self::list">
                <li class="list-wrapper"><xsl:apply-templates select="."/></li>
              </xsl:when>
              <xsl:otherwise>
                <xsl:apply-templates/>
              </xsl:otherwise>
            </xsl:choose>
          </xsl:for-each>
        </ul>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="mentioned">
    <font class="mentioned"><xsl:apply-templates/></font>
  </xsl:template>
  
  <!--
      Note: I have not covered <milestone>, <milestoneStart>, or
            <milestoneEnd> here, since I have no idea what they are supposed
            to do, based on the spec.
  -->
  
  <xsl:template match="name">
    <font class="name"><xsl:apply-templates/></font>
  </xsl:template>
  
  <xsl:template match="q">
    <!--
        FIXME: Should I use <span> here?  The spec says that this can be used
               as an embedded quote or a block quote, but there seems to be no
               way to figure out which it is based on context.  Currently I've
               got it as a <blockquote> because it has block-level elements in
               it.
        
        FIXME: Should I include the speaker in the text, e.g.:
               
                   {@who}: {text()}
               
               ?  I'm not sure.  Currently I've just got it as a "title"
               attribute on the <span>.
    -->
    <blockquote class="q">
      <xsl:if test="@who">
        <xsl:attribute name="title"><xsl:value-of select="@who"/></xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </blockquote>
  </xsl:template>
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="rdg">
    <div class="rdg"><xsl:apply-templates/></div>
  </xsl:template>

  <!--
      <row> is handled near <table> below and so does not appear here.
  -->
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="salute">
    <div class="salute"><xsl:apply-templates/></div>
  </xsl:template>
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="signed">
    <font class="signed"><xsl:apply-templates/></font>
  </xsl:template>

  <!-- Avoid adding whitespace -->
  <xsl:template match="speech">
    <div class="speech"><xsl:apply-templates/></div>
  </xsl:template>
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="table">
    <table class="table">
      <xsl:copy-of select="@rows|@cols"/>
      <xsl:if test="head">
        <thead class="head"><xsl:apply-templates select="head"/></thead>
      </xsl:if>
      <tbody><xsl:apply-templates select="row"/></tbody>
    </table>
  </xsl:template>
  
  <!-- Avoid adding whitespace -->
  <xsl:template match="row">
    <tr class="row"><xsl:apply-templates/></tr>
  </xsl:template>
  
  <xsl:template match="cell">
    <xsl:variable name="element-name">
      <xsl:choose>
        <xsl:when test="@role = 'label'">
          <xsl:text>th</xsl:text>
        </xsl:when>
        <xsl:otherwise>
          <xsl:text>td</xsl:text>
        </xsl:otherwise>
      </xsl:choose>
    </xsl:variable>
    <xsl:element name="{$element-name}">
      <xsl:attribute name="class">cell</xsl:attribute>
      <xsl:attribute name="valign">top</xsl:attribute>
      <xsl:if test="@rows">
        <xsl:attribute name="rowspan">
          <xsl:value-of select="@rows"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:if test="@cols">
        <xsl:attribute name="colspan">
          <xsl:value-of select="@cols"/>
        </xsl:attribute>
      </xsl:if>
      <xsl:apply-templates/>
    </xsl:element>
  </xsl:template>

  <!-- Avoid adding whitespace -->
  <xsl:template match="transChange">
    <font class="transChange"><xsl:apply-templates/></font>
  </xsl:template>
  
  <xsl:template match="hi">
      <xsl:choose>
        <xsl:when test="@type = 'bold'">
          <strong><xsl:apply-templates/></strong>
        </xsl:when>
        <xsl:when test="@type = 'illuminated'">
          <strong><em><xsl:apply-templates/></em></strong>
        </xsl:when>
        <xsl:when test="@type = 'italic'">
          <em><xsl:apply-templates/></em>
        </xsl:when>
        <xsl:when test="@type = 'line-through'">
          <!-- later -->
          <xsl:apply-templates/>
        </xsl:when>
        <xsl:when test="@type = 'normal'">
           <!-- later -->
          <xsl:apply-templates/>
        </xsl:when>
        <xsl:when test="@type = 'small-caps'">
          <!-- later -->
          <xsl:apply-templates/>
        </xsl:when>
        <xsl:when test="@type = 'underline'">
          <u><xsl:apply-templates/></u>
        </xsl:when>
        <xsl:otherwise>
          <xsl:apply-templates/>
        </xsl:otherwise>
      </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>

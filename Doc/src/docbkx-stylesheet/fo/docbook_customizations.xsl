<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" version="1.0">

	<xsl:import href="urn:docbkx:stylesheet/docbook.xsl" />
	<xsl:import href="urn:docbkx:stylesheet/highlight.xsl" />

	<xsl:param name="use.extensions" select="1" />
	<xsl:param name="linenumbering.extension" select="1" />

	<xsl:param name="shade.verbatim" select="1" />
	<xsl:param name="highlight.source" select="1" />
	<xsl:param name="hyphenate.verbatim" select="0" />

	<xsl:attribute-set name="monospace.verbatim.properties"
		use-attribute-sets="verbatim.properties monospace.properties">
		<xsl:attribute name="wrap-option">wrap</xsl:attribute>
		<xsl:attribute name="hyphenation-character">&#x25BA;</xsl:attribute>
		<xsl:attribute name="font-size">
         <xsl:choose>
            <xsl:when test="processing-instruction('db-font-size')"><xsl:value-of
			select="processing-instruction('db-font-size')" /></xsl:when>
            <xsl:otherwise>inherit</xsl:otherwise>
         </xsl:choose>
      </xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="section.level1.properties">
		<xsl:attribute name="break-before">page</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="section.title.level1.properties">
		<xsl:attribute name="color">navy</xsl:attribute>
		<!-- <xsl:attribute name="font-size">16pt</xsl:attribute> -->
	</xsl:attribute-set>

	<xsl:attribute-set name="section.title.level2.properties">
		<xsl:attribute name="color">navy</xsl:attribute>
		<xsl:attribute name="border-bottom-style">solid</xsl:attribute>
		<xsl:attribute name="border-bottom-width">1pt</xsl:attribute>
		<!-- <xsl:attribute name="font-size">16pt</xsl:attribute> -->
	</xsl:attribute-set>

	<xsl:attribute-set name="shade.verbatim.properties">
		<xsl:attribute name="border-color">thin black ridge</xsl:attribute>
		<xsl:attribute name="background-color">silver</xsl:attribute>
	</xsl:attribute-set>

	<xsl:attribute-set name="olink.properties">
		<xsl:attribute name="show-destination">new</xsl:attribute>
	</xsl:attribute-set>

	<xsl:template match="processing-instruction('hard-pagebreak')">
		<fo:block break-after='page' />
	</xsl:template>

	<!-- omit second title page -->
	<xsl:template name="book.titlepage.verso" />

	<!-- include header with logo -->
	<xsl:template name="header.content">
		<xsl:param name="position" select="''" />
		<xsl:param name="sequence" select="''" />
		<xsl:choose>
			<xsl:when test="$position = 'right' ">
				<fo:external-graphic content-height="0.8cm"
					src="images/soda4LCA_logo.png" />
			</xsl:when>
			<xsl:when test="$position = 'left'">
				<xsl:apply-templates select="." mode="title.markup" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>

<?xml version="1.0" encoding="iso-8859-1" standalone="yes" ?>
<!-- $Id: normowl.xsl,v 1.1 2005/06/10 20:44:28 euzenat Exp $ -->

<!-- This stylesheet provides a rough view of a particular ontology -->

<xsl:stylesheet version="1.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:bib="http://www.inrialpes.fr/exmo/papers"
 xmlns:datext="http://www.jclark.com/xt/java/java.util.Date"
 xmlns:datexa="xalan://java.util.Date"
 xmlns:units="http://visus.mit.edu/fontomri/0.01/units.owl#"
 xmlns:foaf="http://xmlns.com/foaf/0.1/"
 xmlns:ical="http://www.w3.org/2002/12/cal/#"
 xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
 xmlns:xsd="http://www.w3.org/2001/XMLSchema#"
 xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
 xmlns:owl="http://www.w3.org/2002/07/owl#"
 xmlns:wot="http://xmlns.com/wot/0.1/"
 xmlns:dc="http://purl.org/dc/elements/1.1/"
 xmlns:dcterms="http://purl.org/dc/terms/"
 xmlns:dctype="http://purl.org/dc/dcmitype/"
 xmlns:bibtex="http://purl.org/net/nknouf/ns/bibtex#">

<xsl:template match="/">

<!-- select all the ordered templates in a variable
  select all the content with same name in a variable 
-->
  <xsl:variable name="compact">
    <xsl:for-each select="rdf:RDF/owl:Class">
      <xsl:copy>
	<xsl:apply-templates select="//rdf:RDF/owl:Class[@rdf:about='{@rdf:ID}]/*"/>
	<xsl:apply-templates/>
      </xsl:copy>
    </xsl:for-each>
  </xsl:variable>

  <xsl:for-each select="$compact">
    <xsl:if test="not(rdfs:subClassOf/@rdf:resource)">
      
    </xsl:if>
  </xsl:for-each>

</xsl:template>

<xsl:template match="owl:Class" mode="subs">
    <xsl:if test="rdfs:subClassOf/@rdf:resource">
      
    </xsl:if>
</xsl:template>

<xsl:template match="owl:Class" mode="compact">
  
  <!-- get all subs -->
  <xsl:variable name="subs">
    <xsl:apply-templates select="//rdf:RDF/*[@rdf:about='{@rdf:ID}"/>
  </xsl:variable>

  <!-- get all eqname -->
  <xsl:variable name="others">
    <xsl:apply-templates select="//rdf:RDF/*[@rdf:about='{@rdf:ID}"/>
  </xsl:variable>

  <!-- get all sameClassAs -->
</xsl:template>

<xsl:template match="owl:Class" mode="include">
  
  <!-- get all subs -->
  <xsl:variable name="subs">
    <xsl:apply-templates select="//rdf:RDF/*[@rdf:about='{@rdf:ID}"/>
  </xsl:variable>

  <!-- get all eqname -->
  <xsl:variable name="others">
    <xsl:apply-templates select="//rdf:RDF/*[@rdf:about='{@rdf:ID}"/>
  </xsl:variable>

  <!-- get all sameClassAs -->
</xsl:template>

<!--
<xsl:output method="xml" indent="yes"/>


<xsl:template match="add-exp[op-add]" mode="value">
<xsl:variable name="x">
<xsl:apply-templates select="*[1]" mode="value"/>
</xsl:variable>
<xsl:variable name="y">
<xsl:apply-templates select="*[3]" mode="value"/>
</xsl:variable>
<xsl:value-of select="$x + $y"/>
</xsl:template>

<xsl:template match="add-exp[op-sub]" mode="value">
<xsl:variable name="x">
<xsl:apply-templates select="*[1]" mode="value"/>
</xsl:variable>
<xsl:variable name="y">
<xsl:apply-templates select="*[3]" mode="value"/>
</xsl:variable>
<xsl:value-of select="$x - $y"/>
</xsl:template>


<xsl:template match="primary-exp[op-mult]" mode="value">
<xsl:variable name="x">
<xsl:apply-templates select="*[1]" mode="value"/>
</xsl:variable>
<xsl:variable name="y">
<xsl:apply-templates select="*[3]" mode="value"/>
</xsl:variable>
<xsl:value-of select="$x * $y"/>
</xsl:template>


<xsl:template match="literal" mode="value">
<xsl:value-of select="number(@value)"/>
</xsl:template>

<xsl:template match="*" mode="value">
<xsl:apply-templates select="*" mode="value"/>
</xsl:template>

<xsl:template match="*">
<xsl:copy>
<xsl:attribute name="value">
<xsl:apply-templates select="." mode="value"/>
</xsl:attribute>
<xsl:apply-templates/>
</xsl:copy>
</xsl:template>

<xsl:template match="op-add|op-sub|op-mult">
<xsl:copy>
<xsl:apply-templates/>
</xsl:copy>
</xsl:template>


</xsl:stylesheet>


-->
</xsl:stylesheet>

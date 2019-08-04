<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: generalization.xsl,v 1.6 2006/08/01 11:18:22 euzenat Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
		xmlns:my="http://oaei.ontologymatching.org/tests/101/onto.rdf#"
		xmlns:units="http://visus.mit.edu/fontomri/0.01/units.owl#" 
		xmlns:foaf="http://xmlns.com/foaf/0.1/#" 
		xmlns:ical="http://www.w3.org/2002/12/cal/#" 
		xmlns:xsd="http://www.w3.org/2001/XMLSchema#" 
		xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
		xmlns:owl="http://www.w3.org/2002/07/owl#" 
		xmlns:wot="http://xmlns.com/wot/0.1/" 
		xmlns:dc="http://purl.org/dc/elements/1.1/" 
		xmlns:dcterms="http://purl.org/dc/terms/" 
		xmlns:dctype="http://purl.org/dc/dcmitype/"
		version="1.0"
>
  <xsl:param name="param"></xsl:param>
  <xsl:param name="value"></xsl:param>
  <xsl:param name="ref"></xsl:param>

  <xsl:output method="xml" encoding="iso-8859-1"/>

  <!-- This is fully ad hoc -->
  <xsl:template match="rdf:type[@rdf:resource='http://www.w3.org/2002/07/owl#TransitiveProperty']"/>

  <xsl:template match="rdfs:domain[owl:Class/owl:unionOf]">
    <rdfs:domain rdf:resource="#Reference"/>
  </xsl:template>
  <xsl:template match="owl:allValuesFrom[owl:Class/owl:unionOf]">
    <owl:allValuesFrom rdf:resource="#PersonList"/>
  </xsl:template>

  <xsl:template match="@*|node()"> <!-- node() = text()|* ? -->
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
	
</xsl:stylesheet>

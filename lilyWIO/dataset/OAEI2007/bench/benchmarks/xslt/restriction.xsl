<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: restriction.xsl,v 1.7 2006/08/01 11:15:47 euzenat Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
		xmlns:my="http://oaei.ontologymatching.org/tests/101/onto.rdf#"
		xmlns:units="http://visus.mit.edu/fontomri/0.01/units.owl#" 
		xmlns:foaf="http://xmlns.com/foaf/0.1/" 
		xmlns:ical="http://www.w3.org/2002/12/cal/ical#" 
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

  <xsl:template match="owl:unionOf"/>
  <xsl:template match="owl:intersectionOf"/>
  <xsl:template match="owl:complementOf"/>
  <xsl:template match="owl:oneOf"/>
  <xsl:template match="owl:Class[owl:unionOf]"/>
  <xsl:template match="owl:Class[owl:intersectionOf]"/>
  <xsl:template match="owl:Class[owl:complementOf]"/>
  <xsl:template match="owl:Class[owl:oneOf]"/>
  <xsl:template match="rdf:type[@rdf:resource='http://www.w3.org/2002/07/owl#TransitiveProperty']"/>
  <xsl:template match="rdfs:domain[owl:Class/owl:unionOf]"/>
  <xsl:template match="rdfs:domain[owl:Class/owl:intersectionOf]"/>
  <xsl:template match="rdfs:domain[owl:Class/owl:complementOf]"/>
  <xsl:template match="rdfs:domain[owl:Class/owl:oneOf]"/>
  <xsl:template match="rdfs:range[owl:Class/owl:unionOf]"/>
  <xsl:template match="rdfs:range[owl:Class/owl:intersectionOf]"/>
  <xsl:template match="rdfs:range[owl:Class/owl:complementOf]"/>
  <xsl:template match="rdfs:range[owl:Class/owl:oneOf]"/>

  <!-- really ad hoc: this shows that a backward recursive scheme is needed -->
  <xsl:template
     match="rdfs:subClassOf[owl:Restriction/owl:allValuesFrom/owl:Class/owl:unionOf]"/>

  <xsl:template match="@*|node()"> <!-- node() = text()|* ? -->
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
	
</xsl:stylesheet>

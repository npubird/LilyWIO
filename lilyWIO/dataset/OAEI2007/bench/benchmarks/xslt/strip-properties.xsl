<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: strip-properties.xsl,v 1.8 2006/08/01 11:14:13 euzenat Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
		xmlns:my="http://oaei.ontologymatching.org/tests/101/onto.rdf#"
		xmlns:foaf="http://xmlns.com/foaf/0.1/" 
		xmlns:ical="http://www.w3.org/2002/12/cal/ical#" 
		xmlns:xsd="http://www.w3.org/2001/XMLSchema#" 
		xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#" 
		xmlns:owl="http://www.w3.org/2002/07/owl#" 
		xmlns:dc="http://purl.org/dc/elements/1.1/" 
		version="1.0"
>
  <xsl:param name="param"></xsl:param>
  <xsl:param name="value"></xsl:param>
  <xsl:param name="ref"></xsl:param>

  <xsl:output method="xml" encoding="iso-8859-1"/>

  <!-- quite ad hoc for avoiding to strip foaf and dc declarations -->
  <xsl:template match="owl:ObjectProperty[@rdf:ID]"/>
  <xsl:template match="owl:DatatypeProperty[@rdf:ID]"/>
  <xsl:template match="owl:TransitiveProperty"/>
  <xsl:template match="owl:SymmetricProperty"/>
  <xsl:template match="owl:FunctionalProperty"/>
  <xsl:template match="owl:InverseFunctionalProperty"/>
  <xsl:template match="rdfs:subClassOf[not(@rdf:resource)]"/>
  <xsl:template match="owl:Restriction"/>

  <!-- for suppressing properties in instances -->
  <xsl:template match="foaf:Person/my:*"/>
  <xsl:template match="foaf:Person/lastName"/> <!-- really ad hoc -->
  <xsl:template match="my:*/my:*"/>

  <!-- for suppressing properties in alignment -->

  <xsl:template match="@*|node()"> <!-- node() = text()|* ? -->
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
	
</xsl:stylesheet>

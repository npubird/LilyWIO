<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: strip-propalign.xsl,v 1.5 2005/07/07 14:47:57 euzenat Exp $ -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
		xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" 
		xmlns:align="http://knowledgeweb.semanticweb.org/heterogeneity/alignment"
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

  <xsl:output method="xml" encoding="iso-8859-1"/>

  <!-- JE: This is an anthological piece of XSLT! -->

  <xsl:variable name="uri"><xsl:value-of select="/rdf:RDF/align:Alignment/align:onto1/text()"/></xsl:variable>
  <xsl:variable name="file"><xsl:value-of select="document($uri)"/></xsl:variable>

  <xsl:key name="prop" match="owl:ObjectProperty|owl:TransitiveProperty|owl:SymmetricProperty|owl:InverseFunctionalProperty|owl:InverseFunctionalProperty|owl:DatatypeProperty" use="@rdf:ID"/>

  <!-- for suppressing properties in alignment -->
  <xsl:template match="align:Cell">
    <xsl:variable name="name" select="substring-after(align:entity1/@rdf:resource,'#')"/>
    <xsl:variable name="myself" select="."/>
    <xsl:for-each select="document($uri)">
      <xsl:if test="not(key('prop',$name))">
	<xsl:copy-of select="$myself"/>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
	
</xsl:stylesheet>

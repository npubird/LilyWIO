<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: flatten.xsl,v 1.3 2005/07/07 14:51:17 euzenat Exp $ -->

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

  <!-- for suppressing properties in alignment -->
  <xsl:template match="align:Cell[substring-after(align:entity2/@rdf:resource,'#')='Book']"/>
  <xsl:template match="align:Cell[substring-after(align:entity2/@rdf:resource,'#')='Part']"/>
  <xsl:template match="align:Cell[substring-after(align:entity2/@rdf:resource,'#')='Academic']"/>
  <xsl:template match="align:Cell[substring-after(align:entity2/@rdf:resource,'#')='Informal']"/>

  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
	
</xsl:stylesheet>

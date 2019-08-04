<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: trans-conv.xsl,v 1.9 2006/08/01 11:16:21 euzenat Exp $ -->

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
		xmlns:align="http://knowledgeweb.semanticweb.org/heterogeneity/alignment"
		xmlns:dctype="http://purl.org/dc/dcmitype/"
		version="1.0"
>
  <xsl:param name="param"></xsl:param>
  <xsl:param name="value"></xsl:param>
  <xsl:param name="ref"></xsl:param>

  <xsl:output method="xml" encoding="iso-8859-1"/>

  <xsl:template match="@*|node()"> 
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>
	
  <xsl:template match="@rdf:ID">
    <xsl:attribute name="rdf:ID">
      <xsl:call-template name="translate">
	<xsl:with-param name="string">
	  <xsl:value-of select="."/>
	</xsl:with-param>
      </xsl:call-template>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="@rdf:resource">
    <xsl:attribute name="rdf:resource">
      <xsl:call-template name="translate">
	<xsl:with-param name="string">
	  <xsl:value-of select="."/>
	</xsl:with-param>
      </xsl:call-template>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="@rdf:about">
    <xsl:attribute name="rdf:about">
      <xsl:call-template name="translate">
	<xsl:with-param name="string">
	  <xsl:value-of select="."/>
	</xsl:with-param>
      </xsl:call-template>
    </xsl:attribute>
  </xsl:template>

  <xsl:template match="rdfs:label">
    <rdfs:label>
      <xsl:if test="@xml:lang">
	<xsl:attribute name="xml:lang"><xsl:value-of select="@xml:lang"/></xsl:attribute>
      </xsl:if>
      <xsl:call-template name="translate">
	<xsl:with-param name="string">
	  <xsl:value-of select="text()"/>
	</xsl:with-param>
      </xsl:call-template>
    </rdfs:label>
  </xsl:template>

  <!-- do not change anything in this specific case -->
  <xsl:template match="align:entity1/@rdf:resource">
    <xsl:attribute name="rdf:resource"><xsl:value-of select="."/></xsl:attribute>
  </xsl:template>

  <!-- tag name -->
  <xsl:template match="*[namespace-uri(.)='http://oaei.ontologymatching.org/tests/101/onto.rdf#']">
    <xsl:variable name="new">
      <xsl:call-template name="translate">
	<xsl:with-param name="string">
	  <xsl:value-of select="name(.)"/>
	</xsl:with-param>
      </xsl:call-template>
      </xsl:variable>
    <xsl:element name="{$new}">
      <xsl:apply-templates select="@*|node()"/>
    </xsl:element>
  </xsl:template>

  <!-- uri in align: useless -->
  <xsl:template select="entity2">
    <xsl:element name="entity2">
      <xsl:attribute name="rdf:resource">
	<xsl:call-template name="translate">
	  <xsl:with-param name="string">
	    <xsl:value-of select="@rdf:resource"/>
	  </xsl:with-param>
	</xsl:call-template>
      </xsl:attribute>
    </xsl:element>
  </xsl:template>

  <xsl:template name="translate">
    <xsl:param name="string"/>
    <xsl:choose>
      <xsl:when test="not(contains($string,'#'))">
	<xsl:call-template name="replace">
	  <xsl:with-param name="suffix"><xsl:value-of select="$string"/></xsl:with-param>
	</xsl:call-template>
      </xsl:when>
      <xsl:when test="substring-before($string,'#')=''">
	<xsl:text>#</xsl:text>
	<xsl:call-template name="replace">
	  <xsl:with-param name="suffix"><xsl:value-of select="substring-after($string,'#')"/></xsl:with-param>
	</xsl:call-template>
      </xsl:when>
      <xsl:when test="substring-before($string,'#')='http://oaei.ontologymatching.org/tests/101/onto.rdf'">
	<xsl:text>http://oaei.ontologymatching.org/tests/101/onto.rdf#</xsl:text>
	<xsl:call-template name="replace">
	  <xsl:with-param name="suffix"><xsl:value-of select="substring-after($string,'#')"/></xsl:with-param>
	</xsl:call-template>
      </xsl:when>
      <xsl:otherwise><xsl:value-of select="$string"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

  <xsl:template name="replace">
    <xsl:param name="suffix"/>
    <xsl:choose>
      <xsl:when test="$suffix='isPartOf'">is_part_of</xsl:when>
      <xsl:when test="$suffix='UserGuide'">User_Guide</xsl:when>
      <xsl:when test="$suffix='ReferenceManual'">Reference_Manual</xsl:when>
      <xsl:when test="$suffix='Review'">Rev.</xsl:when>
      <xsl:when test="$suffix='InstitutionReport'">Institution_Report</xsl:when>
      <xsl:when test="$suffix='ProspectiveReport'">Prospective_Report</xsl:when>
      <xsl:when test="$suffix='EvaluationReport'">Evaluation_Report</xsl:when>
      <xsl:when test="$suffix='SerialReport'">Serial_Report</xsl:when>
      <xsl:when test="$suffix='NormalizationReport'">Normalization_Report</xsl:when>
      <xsl:when test="$suffix='Recommendation'">Rec.</xsl:when>
      <xsl:when test="$suffix='WorkReport'">Work_Report</xsl:when>
      <xsl:when test="$suffix='TechnicalMemo'">TM</xsl:when>
      <xsl:when test="$suffix='ResearchReport'">Technical_Report</xsl:when>
      <xsl:when test="$suffix='ResearchNote'">Research_Note</xsl:when>
      <xsl:when test="$suffix='PeriodicReport'">Periodic_Report</xsl:when>
      <xsl:when test="$suffix='YearlyReport'">Yearly_Report</xsl:when>
      <xsl:when test="$suffix='FinalReport'">Final_Report</xsl:when>
      <xsl:when test="$suffix='ScientificMeeting'">Scientific_Meeting</xsl:when>
      <xsl:when test="$suffix='Society'">Soc.</xsl:when>
      <xsl:when test="$suffix='HigherEducationInstitution'">Higher_Education_Institution</xsl:when>
      <xsl:when test="$suffix='University'">Univ.</xsl:when>
      <xsl:when test="$suffix='EngineerSchool'">Engineer_School</xsl:when>
      <xsl:when test="$suffix='InBook'">In_book</xsl:when>
      <xsl:when test="$suffix='InCollection'">In_collection</xsl:when>
      <xsl:when test="$suffix='InProceedings'">In_proceedings</xsl:when>
      <xsl:when test="$suffix='Reference'">REFERENCE</xsl:when>
      <xsl:when test="$suffix='LectureNotes'">Lecture_notes</xsl:when>
      <xsl:when test="$suffix='MastersThesis'">MSc_thesis</xsl:when>
      <xsl:when test="$suffix='PhdThesis'">PhD_thesis</xsl:when>
      <xsl:when test="$suffix='Misc'">Misc.</xsl:when>
      <xsl:when test="$suffix='Proceedings'">Proc.</xsl:when>
      <xsl:when test="$suffix='TechReport'">Technical_report</xsl:when>
      <xsl:when test="$suffix='Unpublished'">Un_published</xsl:when>
      <xsl:when test="$suffix='MotionPicture'">Motion_picture</xsl:when>
      <xsl:when test="$suffix='Journal'">JOURNAL</xsl:when>
      <xsl:when test="$suffix='Conference'">CONFERENCE</xsl:when>
      <xsl:when test="$suffix='Address'">ADDRESS</xsl:when>
      <xsl:when test="$suffix='PersonList'">PERSON_LIST</xsl:when>
      <xsl:when test="$suffix='PageRange'">PAGE_RANGE</xsl:when>
      <xsl:when test="$suffix='Date'">DATE</xsl:when>
      <xsl:when test="$suffix='organizer'">organiser</xsl:when>
      <xsl:when test="$suffix='firstPublished'">first_published</xsl:when>
      <xsl:when test="$suffix='humanCreator'">human_creator</xsl:when>
      <xsl:when test="$suffix='howPublished'">how_published</xsl:when>
      <xsl:when test="$suffix='isPartOf'">is_part_of</xsl:when>
      <xsl:when test="$suffix='organization'">organisation</xsl:when>
      <xsl:when test="$suffix='isbn'">I.S.B.N.</xsl:when>
      <xsl:when test="$suffix='issn'">I.S.S.N.</xsl:when>
      <xsl:when test="$suffix='lccn'">L.C.C.N.</xsl:when>
      <xsl:when test="$suffix='mrNumber'">M.R.Nb</xsl:when>
      <xsl:when test="$suffix='url'">U.R.L.</xsl:when>
      <xsl:when test="$suffix='shortName'">short_name</xsl:when>
      <xsl:when test="$suffix='numberOrVolume'">number_or_volume</xsl:when>
      <xsl:when test="$suffix='startPage'">start_page</xsl:when>
      <xsl:when test="$suffix='endPage'">end_page</xsl:when>
      <xsl:when test="$suffix='articles'">art</xsl:when>
      <xsl:when test="$suffix='communications'">com</xsl:when>
      <xsl:when test="$suffix='chapters'">chap</xsl:when>
      <xsl:otherwise><xsl:value-of select="$suffix"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>

 

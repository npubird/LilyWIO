<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: trans-synonyms.xsl,v 1.10 2006/08/01 11:17:17 euzenat Exp $ -->

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
      <xsl:when test="$suffix='Resource'">Source</xsl:when>
      <xsl:when test="$suffix='isPartOf'">componentOf</xsl:when>
      <xsl:when test="$suffix='Text'">Document</xsl:when>
      <xsl:when test="$suffix='UserGuide'">UserManual</xsl:when>
      <xsl:when test="$suffix='ReferenceManual'">ReferenceGuide</xsl:when>
      <xsl:when test="$suffix='JournalPart'">Contribution</xsl:when>
      <xsl:when test="$suffix='Review'">BookReview</xsl:when>
      <xsl:when test="$suffix='Editorial'">Forehead</xsl:when>
      <xsl:when test="$suffix='Letter'">Note</xsl:when>
      <xsl:when test="$suffix='ProspectiveReport'">ForecastReport</xsl:when>
      <xsl:when test="$suffix='Standard'">Norm</xsl:when>
      <xsl:when test="$suffix='WorkReport'">WhitePaper</xsl:when>
      <xsl:when test="$suffix='TechnicalMemo'">TechNote</xsl:when>
      <xsl:when test="$suffix='PeriodicReport'">RegularReport</xsl:when>
      <xsl:when test="$suffix='YearlyReport'">AnnualReport</xsl:when>
      <xsl:when test="$suffix='Periodical'"></xsl:when>
      <xsl:when test="$suffix='Society'">Association</xsl:when>
      <xsl:when test="$suffix='EngineerSchool'">TechnicalSchool</xsl:when>
      <xsl:when test="$suffix='Booklet'">Brochure</xsl:when>
      <xsl:when test="$suffix='InBook'">Extract</xsl:when>
      <xsl:when test="$suffix='InCollection'">InCompilation</xsl:when>
      <xsl:when test="$suffix='InProceedings'">Communication</xsl:when>
      <xsl:when test="$suffix='Reference'">Entry</xsl:when>
      <xsl:when test="$suffix='Journal'">Periodical</xsl:when>
      <xsl:when test="$suffix='Article'">JournalPaper</xsl:when>
      <xsl:when test="$suffix='Monograph'">Monography</xsl:when>
      <xsl:when test="$suffix='Collection'">Compilation</xsl:when>
      <xsl:when test="$suffix='Informal'">NonFormal</xsl:when>
      <xsl:when test="$suffix='Part'">Section</xsl:when>
      <xsl:when test="$suffix='Chapter'">Chapter</xsl:when>
      <xsl:when test="$suffix='LectureNotes'">CourseMaterial</xsl:when>
      <xsl:when test="$suffix='Manual'">ReferenceGuide</xsl:when>
      <xsl:when test="$suffix='Academic'">StudentReport</xsl:when>
      <xsl:when test="$suffix='MastersThesis'">MScThesis</xsl:when>
      <xsl:when test="$suffix='PhdThesis'">DoctoralThesis</xsl:when>
      <xsl:when test="$suffix='Misc'">Various</xsl:when>
      <xsl:when test="$suffix='Proceedings'">ConferenceMinutes</xsl:when>
      <xsl:when test="$suffix='TechReport'">TechnicalReport</xsl:when>
      <xsl:when test="$suffix='Unpublished'">Manuscript</xsl:when>
      <xsl:when test="$suffix='MotionPicture'">Movie</xsl:when>
      <xsl:when test="$suffix='Conference'">Congress</xsl:when>
      <xsl:when test="$suffix='Address'">Directions</xsl:when>
      <xsl:when test="$suffix='Institution'">Organization</xsl:when>
      <xsl:when test="$suffix='Publisher'">PublishingHouse</xsl:when>
      <xsl:when test="$suffix='School'">University</xsl:when>
      <xsl:when test="$suffix='PersonList'">People</xsl:when>
      <xsl:when test="$suffix='PageRange'">PageInterval</xsl:when>
      <xsl:when test="$suffix='reviewed'">selection</xsl:when>
      <xsl:when test="$suffix='address'">coordinates</xsl:when>
      <xsl:when test="$suffix='event'">meeting</xsl:when>
      <xsl:when test="$suffix='organizer'">organizedBy</xsl:when>
      <xsl:when test="$suffix='annote'">annotation</xsl:when>
      <xsl:when test="$suffix='periodicity'">frequency</xsl:when>
      <xsl:when test="$suffix='firstPublished'">startDate</xsl:when>
      <xsl:when test="$suffix='edition'">issue</xsl:when>
      <xsl:when test="$suffix='humanCreator'">createdBy</xsl:when>
      <xsl:when test="$suffix='author'">writtenBy</xsl:when>
      <xsl:when test="$suffix='editor'">coordinatedBy</xsl:when>
      <xsl:when test="$suffix='directors'">directedBy</xsl:when>
      <xsl:when test="$suffix='howPublished'">PublicationDetails</xsl:when>
      <xsl:when test="$suffix='institution'">issuedBy</xsl:when>
      <xsl:when test="$suffix='isPartOf'">partOf</xsl:when>
      <xsl:when test="$suffix='journal'">inPeriodical</xsl:when>
      <xsl:when test="$suffix='book'">inVolume</xsl:when>
      <xsl:when test="$suffix='collection'">inCompilation</xsl:when>
      <xsl:when test="$suffix='proceedings'">inMinutes</xsl:when>
      <xsl:when test="$suffix='date'">publishingDate</xsl:when>
      <xsl:when test="$suffix='note'">comment</xsl:when>
      <xsl:when test="$suffix='organization'">sponsoredBy</xsl:when>
      <xsl:when test="$suffix='publisher'">publishedBy</xsl:when>
      <xsl:when test="$suffix='school'">issuer</xsl:when>
      <xsl:when test="$suffix='series'">inSeries</xsl:when>
      <xsl:when test="$suffix='title'">heading</xsl:when>
      <xsl:when test="$suffix='affiliation'">company</xsl:when>
      <xsl:when test="$suffix='abstract'">summary</xsl:when>
      <xsl:when test="$suffix='contents'">table</xsl:when>
      <xsl:when test="$suffix='copyright'">rights</xsl:when>
      <xsl:when test="$suffix='isbn'">ISBN</xsl:when>
      <xsl:when test="$suffix='issn'">ISSN</xsl:when>
      <xsl:when test="$suffix='keywords'">KeyWordsAndPhrases</xsl:when>
      <xsl:when test="$suffix='key'">ref</xsl:when>
      <xsl:when test="$suffix='language'">idiom</xsl:when>
      <xsl:when test="$suffix='location'">place</xsl:when>
      <xsl:when test="$suffix='lccn'">LCCN</xsl:when>
      <xsl:when test="$suffix='mrNumber'">MRN</xsl:when>
      <xsl:when test="$suffix='price'">amount</xsl:when>
      <xsl:when test="$suffix='size'">dimensions</xsl:when>
      <xsl:when test="$suffix='url'">link</xsl:when>
      <xsl:when test="$suffix='name'">id</xsl:when>
      <xsl:when test="$suffix='shortName'">acronym</xsl:when>
      <xsl:when test="$suffix='chapter'">section</xsl:when>
      <xsl:when test="$suffix='pages'">pageRange</xsl:when>
      <xsl:when test="$suffix='numberOrVolume'">numbering</xsl:when>
      <xsl:when test="$suffix='city'">town</xsl:when>
      <xsl:when test="$suffix='startPage'">beginning</xsl:when>
      <xsl:when test="$suffix='endPage'">end</xsl:when>
      <xsl:when test="$suffix='articles'">papers</xsl:when>
      <xsl:when test="$suffix='communications'">talks</xsl:when>
      <xsl:when test="$suffix='parts'">tomes</xsl:when>
      <xsl:when test="$suffix='chapters'">sections</xsl:when>
      <xsl:otherwise><xsl:value-of select="$suffix"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>


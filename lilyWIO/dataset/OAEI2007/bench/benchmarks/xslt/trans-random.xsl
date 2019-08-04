<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: trans-random.xsl,v 1.9 2006/08/01 11:16:54 euzenat Exp $ -->

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
    <!-- keep the prefix if necessary -->
    <xsl:choose>
      <xsl:when test="$suffix='Resource'">uqwz</xsl:when>
      <xsl:when test="$suffix='isPartOf'">EzGNplyLBiS</xsl:when>
      <xsl:when test="$suffix='Text'">PBdgcs</xsl:when>
      <xsl:when test="$suffix='UserGuide'">ZikPzsz</xsl:when>
      <xsl:when test="$suffix='ReferenceManual'">ZGKW1E</xsl:when>
      <xsl:when test="$suffix='JournalPart'">RJbGVM</xsl:when>
      <xsl:when test="$suffix='Review'">Be</xsl:when>
      <xsl:when test="$suffix='Editorial'">fGfFm</xsl:when>
      <xsl:when test="$suffix='Letter'">wFuU</xsl:when>
      <xsl:when test="$suffix='InstitutionReport'">EwQu</xsl:when>
      <xsl:when test="$suffix='ProspectiveReport'">xhNJuGU</xsl:when>
      <xsl:when test="$suffix='EvaluationReport'">UWmlX</xsl:when>
      <xsl:when test="$suffix='SerialReport'">FPVijs</xsl:when>
      <xsl:when test="$suffix='NormalizationReport'">cnNCBO</xsl:when>
      <xsl:when test="$suffix='Standard'">ZVxsNo</xsl:when>
      <xsl:when test="$suffix='Recommendation'">jZVxsNo</xsl:when>
      <xsl:when test="$suffix='WorkReport'">Lluuk</xsl:when>
      <xsl:when test="$suffix='TechnicalMemo'">QMgwFB</xsl:when>
      <xsl:when test="$suffix='ResearchReport'">YeLgZJu</xsl:when>
      <xsl:when test="$suffix='ResearchNote'">LXtNU</xsl:when>
      <xsl:when test="$suffix='PeriodicReport'">XaWa</xsl:when>
      <xsl:when test="$suffix='YearlyReport'">FkmaDDV</xsl:when>
      <xsl:when test="$suffix='FinalReport'">pjDVyIr</xsl:when>
      <xsl:when test="$suffix='Periodical'">BeXCIG</xsl:when>
      <xsl:when test="$suffix='Magazine'">wxVRZcIXbbz</xsl:when>
      <xsl:when test="$suffix='ScientificMeeting'">cexQ</xsl:when>
      <xsl:when test="$suffix='Congress'">RcrfZK</xsl:when>
      <xsl:when test="$suffix='Symposium'">qHdl</xsl:when>
      <xsl:when test="$suffix='Workshop'">izVyrw</xsl:when>
      <xsl:when test="$suffix='Society'">YpNk</xsl:when>
      <xsl:when test="$suffix='HigherEducationInstitution'">BriFsw</xsl:when>
      <xsl:when test="$suffix='University'">MyRFOvC</xsl:when>
      <xsl:when test="$suffix='EngineerSchool'">SNNu</xsl:when>
      <xsl:when test="$suffix='Polytechnics'">SLoVq</xsl:when>
      <xsl:when test="$suffix='Reference'">sqdsq</xsl:when>
      <xsl:when test="$suffix='Article'">hazdn</xsl:when>
      <xsl:when test="$suffix='Book'">zajdhqs</xsl:when>
      <xsl:when test="$suffix='Monograph'">dzajndsq</xsl:when>
      <xsl:when test="$suffix='Collection'">sqxsqkd</xsl:when>
      <xsl:when test="$suffix='Informal'">qsdsnbsqd</xsl:when>
      <xsl:when test="$suffix='Booklet'">zdzaudbd</xsl:when>
      <xsl:when test="$suffix='Part'">sqdsopq</xsl:when>
      <xsl:when test="$suffix='Chapter'">dzqndbzq</xsl:when>
      <xsl:when test="$suffix='InBook'">vccfsq</xsl:when>
      <xsl:when test="$suffix='InCollection'">dcsqdcsqd</xsl:when>
      <xsl:when test="$suffix='InProceedings'">deqdxcsqcsq</xsl:when>
      <xsl:when test="$suffix='LectureNotes'">scds</xsl:when>
      <xsl:when test="$suffix='Manual'">dcsqdsq</xsl:when>
      <xsl:when test="$suffix='Academic'">zdazsx</xsl:when>
      <xsl:when test="$suffix='MastersThesis'">xsqlknk</xsl:when>
      <xsl:when test="$suffix='PhdThesis'">zqdzqzai</xsl:when>
      <xsl:when test="$suffix='Misc'">wxsdxqsdh</xsl:when>
      <xsl:when test="$suffix='Proceedings'">zdqssqdb</xsl:when>
      <xsl:when test="$suffix='Report'">dqzdxdcsqj</xsl:when>
      <xsl:when test="$suffix='TechReport'">sdcsqhyz</xsl:when>
      <xsl:when test="$suffix='Deliverable'">azzauhuie</xsl:when>
      <xsl:when test="$suffix='Unpublished'">zeadza</xsl:when>
      <xsl:when test="$suffix='MotionPicture'">dscdscg</xsl:when>
      <xsl:when test="$suffix='Journal'">qsdsquj</xsl:when>
      <xsl:when test="$suffix='Conference'">zqedzbx</xsl:when>
      <xsl:when test="$suffix='Address'">qzddj</xsl:when>
      <xsl:when test="$suffix='Institution'">izxnquo</xsl:when>
      <xsl:when test="$suffix='Publisher'">zauio</xsl:when>
      <xsl:when test="$suffix='School'">zadazxn</xsl:when>
      <xsl:when test="$suffix='PersonList'">dsqdbz</xsl:when>
      <xsl:when test="$suffix='PageRange'">dnqsb</xsl:when>
      <xsl:when test="$suffix='Date'">dzshyuua</xsl:when>
      <xsl:when test="$suffix='key'">zdzbh</xsl:when>
      <xsl:when test="$suffix='reviewed'">dsqndbsqx</xsl:when>
      <xsl:when test="$suffix='address'">qzd</xsl:when>
      <xsl:when test="$suffix='event'">zadzjadns</xsl:when>
      <xsl:when test="$suffix='organizer'">sdnsbh</xsl:when>
      <xsl:when test="$suffix='annote'">dsqnhza</xsl:when>
      <xsl:when test="$suffix='periodicity'">sdxsndxsqg</xsl:when>
      <xsl:when test="$suffix='contract'">zandsbh</xsl:when>
      <xsl:when test="$suffix='firstPublished'">sqdnbs</xsl:when>
      <xsl:when test="$suffix='edition'">sdsqddz</xsl:when>
      <xsl:when test="$suffix='humanCreator'">sbqgzga</xsl:when>
      <xsl:when test="$suffix='author'">zand</xsl:when>
      <xsl:when test="$suffix='editor'">dzajj</xsl:when>
      <xsl:when test="$suffix='directors'">zdnzqnd</xsl:when>
      <xsl:when test="$suffix='howPublished'">sdqz</xsl:when>
      <xsl:when test="$suffix='institution'">hsgiuyza</xsl:when>
      <xsl:when test="$suffix='isPartOf'">zansbzh</xsl:when>
      <xsl:when test="$suffix='journal'">sxqsnbvsq</xsl:when>
      <xsl:when test="$suffix='book'">sznsbq</xsl:when>
      <xsl:when test="$suffix='collection'">sqnbyz</xsl:when>
      <xsl:when test="$suffix='proceedings'">zassdzadb</xsl:when>
      <xsl:when test="$suffix='date'">sqndsqgy</xsl:when>
      <xsl:when test="$suffix='note'">zdsnsqdv</xsl:when>
      <xsl:when test="$suffix='organization'">sqdndvb</xsl:when>
      <xsl:when test="$suffix='publisher'">sddnsnqdb</xsl:when>
      <xsl:when test="$suffix='school'">dzbn</xsl:when>
      <xsl:when test="$suffix='series'">dszbnz</xsl:when>
      <xsl:when test="$suffix='title'">dznbaln</xsl:when>
      <xsl:when test="$suffix='type'">sqbdzg</xsl:when>
      <xsl:when test="$suffix='datasqbdzg'">datatype</xsl:when>
      <xsl:when test="$suffix='Datasqbdzg'">Datatype</xsl:when>
      <xsl:when test="$suffix='affiliation'">zadzqbsdg</xsl:when>
      <xsl:when test="$suffix='abstract'">dsqndsz</xsl:when>
      <xsl:when test="$suffix='contents'">zdnzdzh</xsl:when>
      <xsl:when test="$suffix='copyright'">zdzdzd</xsl:when>
      <xsl:when test="$suffix='isbn'">szdnzak</xsl:when>
      <xsl:when test="$suffix='issn'">zadsznad</xsl:when>
      <xsl:when test="$suffix='keywords'">zqdszh</xsl:when>
      <xsl:when test="$suffix='language'">sqkxndc</xsl:when>
      <xsl:when test="$suffix='location'">zdjznzn</xsl:when>
      <xsl:when test="$suffix='lccn'">dzandzah</xsl:when>
      <xsl:when test="$suffix='mrNumber'">dszbgz</xsl:when>
      <xsl:when test="$suffix='price'">dsq</xsl:when>
      <xsl:when test="$suffix='size'">dzsndzah</xsl:when>
      <xsl:when test="$suffix='url'">zdsznbadzagh</xsl:when>
      <xsl:when test="$suffix='name'">dszabdza</xsl:when>
      <xsl:when test="$suffix='shortName'">dsza</xsl:when>
      <xsl:when test="$suffix='chapter'">dsazdjz</xsl:when>
      <xsl:when test="$suffix='pages'">dzayue</xsl:when>
      <xsl:when test="$suffix='numberOrVolume'">dzezd</xsl:when>
      <xsl:when test="$suffix='number'">ndsbzh</xsl:when>
      <xsl:when test="$suffix='issue'">sqnzkzn</xsl:when>
      <xsl:when test="$suffix='volume'">zsbdgz</xsl:when>
      <xsl:when test="$suffix='year'">zdsnzsdh</xsl:when>
      <xsl:when test="$suffix='month'">asndzsd</xsl:when>
      <xsl:when test="$suffix='day'">dznadzh</xsl:when>
      <xsl:when test="$suffix='city'">zdzndh</xsl:when>
      <xsl:when test="$suffix='state'">zdnzadh</xsl:when>
      <xsl:when test="$suffix='country'">zadszabnds</xsl:when>
      <xsl:when test="$suffix='startPage'">sqnhdbdj</xsl:when>
      <xsl:when test="$suffix='endPage'">mkalnshsq</xsl:when>
      <xsl:when test="$suffix='articles'">YuEma</xsl:when>
      <xsl:when test="$suffix='communications'">PrSGUs</xsl:when>
      <xsl:when test="$suffix='parts'">TfniY</xsl:when>
      <xsl:when test="$suffix='chapters'">iNi4b8XV</xsl:when>
      <xsl:otherwise><xsl:value-of select="$suffix"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>


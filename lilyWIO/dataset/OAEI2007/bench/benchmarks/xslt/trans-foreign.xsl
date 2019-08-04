<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id: trans-foreign.xsl,v 1.9 2006/08/01 11:12:18 euzenat Exp $ -->

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
    <rdfs:label xml:lang="fr">
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
      <xsl:when test="$suffix='isPartOf'">partieDe</xsl:when>
      <xsl:when test="$suffix='Text'">Texte</xsl:when>
      <xsl:when test="$suffix='UserGuide'">ManuelUtilisateur</xsl:when>
      <xsl:when test="$suffix='ReferenceManual'">ManuelDeRéférence</xsl:when>
      <xsl:when test="$suffix='JournalPart'">Contribution</xsl:when>
      <xsl:when test="$suffix='Review'">Recension</xsl:when>
      <xsl:when test="$suffix='Editorial'">Éditorial</xsl:when>
      <xsl:when test="$suffix='Letter'">Lettre</xsl:when>
      <xsl:when test="$suffix='InstitutionReport'">RapportInstitutionnel</xsl:when>
      <xsl:when test="$suffix='ProspectiveReport'">RapportDeProspective</xsl:when>
      <xsl:when test="$suffix='EvaluationReport'">RapportDÉvaluation</xsl:when>
      <xsl:when test="$suffix='SerialReport'">RapportNuméroté</xsl:when>
      <xsl:when test="$suffix='NormalizationReport'">DocumentNormatif</xsl:when>
      <xsl:when test="$suffix='Standard'">Standard</xsl:when>
      <xsl:when test="$suffix='Recommendation'">Recommandation</xsl:when>
      <xsl:when test="$suffix='WorkReport'">RapportDeTravail</xsl:when>
      <xsl:when test="$suffix='TechnicalMemo'">Mémo</xsl:when>
      <xsl:when test="$suffix='ResearchReport'">RapportDeRecherche</xsl:when>
      <xsl:when test="$suffix='ResearchNote'">NoteDeRecherche</xsl:when>
      <xsl:when test="$suffix='PeriodicReport'">RapportPériodique</xsl:when>
      <xsl:when test="$suffix='YearlyReport'">RapportAnnuel</xsl:when>
      <xsl:when test="$suffix='FinalReport'">RapportFinal</xsl:when>
      <xsl:when test="$suffix='Periodical'">Périodique</xsl:when>
      <xsl:when test="$suffix='Magazine'">Magazine</xsl:when>
      <xsl:when test="$suffix='ScientificMeeting'">ManifestationScientifique</xsl:when>
      <xsl:when test="$suffix='Congress'">Congrès</xsl:when>
      <xsl:when test="$suffix='Symposium'">Symposium</xsl:when>
      <xsl:when test="$suffix='Workshop'">Atelier</xsl:when>
      <xsl:when test="$suffix='Society'">Société</xsl:when>
      <xsl:when test="$suffix='HigherEducationInstitution'">ÉtablissementDEnseignementSupérieur</xsl:when>
      <xsl:when test="$suffix='University'">Université</xsl:when>
      <xsl:when test="$suffix='EngineerSchool'">ÉcoleDIngénieur</xsl:when>
      <xsl:when test="$suffix='Polytechnics'">IUT</xsl:when>
      <xsl:when test="$suffix='Booklet'">Livret</xsl:when>
      <xsl:when test="$suffix='InBook'">ExtraitLivre</xsl:when>
      <xsl:when test="$suffix='InCollection'">ExtraitCompilation</xsl:when>
      <xsl:when test="$suffix='InProceedings'">ExtraitActes</xsl:when>
      <xsl:when test="$suffix='Reference'">Référence</xsl:when>
      <xsl:when test="$suffix='Article'">Article</xsl:when>
      <xsl:when test="$suffix='Book'">Livre</xsl:when>
      <xsl:when test="$suffix='Monograph'">Monographie</xsl:when>
      <xsl:when test="$suffix='Collection'">Compilation</xsl:when>
      <xsl:when test="$suffix='Informal'">Informel</xsl:when>
      <xsl:when test="$suffix='Part'">Partie</xsl:when>
      <xsl:when test="$suffix='Chapter'">Chapitre</xsl:when>
      <xsl:when test="$suffix='LectureNotes'">Polycopié</xsl:when>
      <xsl:when test="$suffix='Manual'">Manuel</xsl:when>
      <xsl:when test="$suffix='Academic'">Mémoire</xsl:when>
      <xsl:when test="$suffix='MastersThesis'">MémoireDeMastère</xsl:when>
      <xsl:when test="$suffix='PhdThesis'">MémoireDeDoctorat</xsl:when>
      <xsl:when test="$suffix='Misc'">Divers</xsl:when>
      <xsl:when test="$suffix='Proceedings'">Actes</xsl:when>
      <xsl:when test="$suffix='Report'">Raport</xsl:when>
      <xsl:when test="$suffix='TechReport'">RaportTechnique</xsl:when>
      <xsl:when test="$suffix='Deliverable'">Livrable</xsl:when>
      <xsl:when test="$suffix='Unpublished'">NonPublié</xsl:when>
      <xsl:when test="$suffix='MotionPicture'">Film</xsl:when>
      <xsl:when test="$suffix='Journal'">Revue</xsl:when>
      <xsl:when test="$suffix='Conference'">Conférence</xsl:when>
      <xsl:when test="$suffix='Address'">Adresse</xsl:when>
      <xsl:when test="$suffix='Institution'">Institution</xsl:when>
      <xsl:when test="$suffix='Publisher'">Éditeur</xsl:when>
      <xsl:when test="$suffix='School'">Université</xsl:when>
      <xsl:when test="$suffix='PersonList'">Personnes</xsl:when>
      <xsl:when test="$suffix='PageRange'">IntervalleDePages</xsl:when>
      <xsl:when test="$suffix='Date'">Date</xsl:when>
      <xsl:when test="$suffix='reviewed'">Évalué</xsl:when>
      <xsl:when test="$suffix='address'">adresse</xsl:when>
      <xsl:when test="$suffix='event'">évènement</xsl:when>
      <xsl:when test="$suffix='organizer'">organisateur</xsl:when>
      <xsl:when test="$suffix='annote'">annote</xsl:when>
      <xsl:when test="$suffix='periodicity'">périodicité</xsl:when>
      <xsl:when test="$suffix='contract'">contrat</xsl:when>
      <xsl:when test="$suffix='firstPublished'">dateDePublication</xsl:when>
      <xsl:when test="$suffix='edition'">édition</xsl:when>
      <xsl:when test="$suffix='humanCreator'">créateur</xsl:when>
      <xsl:when test="$suffix='author'">auteurs</xsl:when>
      <xsl:when test="$suffix='editor'">éditeurs</xsl:when>
      <xsl:when test="$suffix='directors'">réalisateurs</xsl:when>
      <xsl:when test="$suffix='howPublished'">modeDePublication</xsl:when>
      <xsl:when test="$suffix='institution'">institution</xsl:when>
      <xsl:when test="$suffix='isPartOf'">estPartieDe</xsl:when>
      <xsl:when test="$suffix='journal'">journal</xsl:when>
      <xsl:when test="$suffix='book'">livre</xsl:when>
      <xsl:when test="$suffix='collection'">collection</xsl:when>
      <xsl:when test="$suffix='proceedings'">actes</xsl:when>
      <xsl:when test="$suffix='date'">date</xsl:when>
      <xsl:when test="$suffix='note'">note</xsl:when>
      <xsl:when test="$suffix='organization'">organisation</xsl:when>
      <xsl:when test="$suffix='publisher'">éditeur</xsl:when>
      <xsl:when test="$suffix='school'">institution</xsl:when>
      <xsl:when test="$suffix='series'">série</xsl:when>
      <xsl:when test="$suffix='title'">titre</xsl:when>
      <xsl:when test="$suffix='type'">catégorie</xsl:when>
      <xsl:when test="$suffix='affiliation'">affiliation</xsl:when>
      <xsl:when test="$suffix='abstract'">résumé</xsl:when>
      <xsl:when test="$suffix='contents'">contenu</xsl:when>
      <xsl:when test="$suffix='copyright'">droits</xsl:when>
      <xsl:when test="$suffix='isbn'">isbn</xsl:when>
      <xsl:when test="$suffix='issn'">issn</xsl:when>
      <xsl:when test="$suffix='keywords'">mots-clé</xsl:when>
      <xsl:when test="$suffix='key'">Clé</xsl:when>
      <xsl:when test="$suffix='language'">langage</xsl:when>
      <xsl:when test="$suffix='location'">localisation</xsl:when>
      <xsl:when test="$suffix='lccn'">lccn</xsl:when>
      <xsl:when test="$suffix='mrNumber'">noMR</xsl:when>
      <xsl:when test="$suffix='price'">prix</xsl:when>
      <xsl:when test="$suffix='size'">taille</xsl:when>
      <xsl:when test="$suffix='url'">url</xsl:when>
      <xsl:when test="$suffix='name'">nom</xsl:when>
      <xsl:when test="$suffix='shortName'">nomCourt</xsl:when>
      <xsl:when test="$suffix='chapter'">chapitre</xsl:when>
      <xsl:when test="$suffix='pages'">pages</xsl:when>
      <xsl:when test="$suffix='numberOrVolume'">numéroOuVolume</xsl:when>
      <xsl:when test="$suffix='number'">numéro</xsl:when>
      <xsl:when test="$suffix='issue'">sortie</xsl:when>
      <xsl:when test="$suffix='volume'">volume</xsl:when>
      <xsl:when test="$suffix='year'">année</xsl:when>
      <xsl:when test="$suffix='month'">mois</xsl:when>
      <xsl:when test="$suffix='day'">jour</xsl:when>
      <xsl:when test="$suffix='city'">ville</xsl:when>
      <xsl:when test="$suffix='state'">état</xsl:when>
      <xsl:when test="$suffix='country'">pays</xsl:when>
      <xsl:when test="$suffix='startPage'">pageDébut</xsl:when>
      <xsl:when test="$suffix='endPage'">pageFin</xsl:when>
      <xsl:when test="$suffix='articles'">articles</xsl:when>
      <xsl:when test="$suffix='communications'">communications</xsl:when>
      <xsl:when test="$suffix='parts'">parties</xsl:when>
      <xsl:when test="$suffix='chapters'">chapitres</xsl:when>
      <xsl:otherwise><xsl:value-of select="$suffix"/></xsl:otherwise>
    </xsl:choose>
  </xsl:template>

</xsl:stylesheet>


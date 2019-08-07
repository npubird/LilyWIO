# LilyWIO
A method for matching weak informative ontologies

Brief introduction:

Most existing ontology matching methods utilize the literal information to discover alignments. However, some literal information in ontologies may be opaque and some ontologies may have not sufficient literal information.
This paper names these ontologies as weak informative ontologies.
String-based and linguistic-based matching methods cannot work well for weak informative ontologies. Some matching methods use external resources to improve their performance, but collecting and processing external resources is still time-consuming. 
In order to solve this problem, this work proposes a practical method for matching weak informative ontologies by employing ontology structure information to discover alignments. This method has several novel features: (1) It proposes semantic subgraphs to capture precise meanings of ontology elements. Semantic subgraphs can use limited information to understand ontology elements. (2) It designs a new similarity propagation model for matching ontologies. The similarity propagation is constrained by semantic subgraphs, that can avoid meaningless propagation. Meanwhile, this model ensures the balance between matching efficiency and quality. (3) This similarity propagation model can use few credible alignments as seeds to find more credible alignments, and it also adopts some useful strategies to improve the performance. This method has been implemented in the ontology matching system Lily. Experimental results demonstrate that the proposed method performs well on the OAEI benchmark dataset.

#Settings
using Config.ini to set parameters. An example is as follow: 

```
;Lily config file
[Matching_Ontologies]
SourceOnt = ./dataset/OAEI2007/bench/benchmarks/101/onto.rdf
TargetOnt = ./dataset/OAEI2007/bench/benchmarks/201/onto.rdf
RefAlignFile = ./dataset/OAEI2007/bench/benchmarks/201/refalign.rdf
[Public_Parameters]
Semantic_SubGraph_Size = 15
```
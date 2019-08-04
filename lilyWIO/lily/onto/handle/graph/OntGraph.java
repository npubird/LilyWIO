/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-4-26
 * Filename          OntGraph.java
 * Version           2.0
 * 
 * Last modified on  2007-4-26
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 * ��ͼ��ʽƥ�䱾�������
 ***********************************************/
package lily.onto.handle.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileNotFoundException; 

import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.ontology.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.alg.*;

import simpack.measure.graph.*;
import simpack.api.IGraphAccessor;
import simpack.accessor.graph.SimpleGraphAccessor;
import simpack.util.graph.Clique;
import simpack.util.graph.GraphNode;

import lily.tool.datastructure.*;
import lily.onto.parse.OWLOntParse;
import lily.tool.linearequation.*;
import lily.tool.graphsimilarity.*;
import lily.tool.filter.*;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-4-26
 * 
 * describe:
 * 
 ********************/
@SuppressWarnings("unchecked")
public class OntGraph {
	//��������ģ��
	public OntModel m;
	//������Ŀ
	public int conceptNum;
	//������Ŀ
	public int propNum;
	public int dataPropNum;
	public int objPropNum;
	//ʵ����Ŀ
	public int insNum;
	//������
	public String[] conceptName;
	//������
	public String[] propName;
	public String[] dataPropName;
	public String[] objPropName;
	//ʵ����
	public String[] insName;
	//base URI
	public String baseURI;
	//��������baseURI�µı���Ԫ��
	public int fullConceptNum;
	public int fullPropNum;
	public int fullDataPropNum;
	public int fullObjPropNum;
	public int fullInsNum;
	public OntClass[] fullConceptName;
	public OntProperty[] fullPropName;
	public DatatypeProperty[] fullDataPropName;
	public ObjectProperty[] fullObjPropName;
	public Individual[] fullInsName;
	//������Դ
	ArrayList anonCnpt;
	ArrayList anonProp;
	ArrayList anonIns;	
	
	//��Ԫ��
	public int trpNum;
	public Statement[] graphTrp;
	//��Ƕ��رߵ�List
	public ArrayList multiEdgeList;
	public ArrayList graphME;
	//���ģ�ͱ���ͼ
	public int graphVNum;
	public String[] graphVName;
	public String[] graphVFullName;
	public DirectedGraph orgnGraph;
	
	//Ȩ��
	public ConceptWt[] cWeight;
	public PropertyWt[] pWeight;
	public InstanceWt[] iWeight;
	public OntLngMetaWt[] metaWeight;
	//����Ԫ��
	public Set ontLngURI;
	public Set ontLngMetaSet;
	public int ontLngMetaNum;
	public String[] ontLngMetaName;
	//��·����
	public double[][] rawCurrent;
	public double[]   currentOut;
	public double[][] conductanceMatrix;
	public double[][] voltageMatrix;
	public DeliveryTable[][] Dvk;
	//��ͼ
	public ConceptSubGraph[] cnptSubG;
	public PropertySubGraph[] propSubG;
	
	//parse���
	public OWLOntParse ontParse;
	//����
	int LARGE_GRAPH;
	/*��ԴȨ�س���*/
	//�����ȨȨ��
	double cfw=0.3;
	double csw=0.5;
	double ciw=0.2;
	//���Լ�ȨȨ��
	double pfw=0.3;
	double psw=0.4;
	double piw=0.3;
	//ʵ����ȨȨ��
	double idpw=0.25;
	double iopw=0.25;
	double icw=0.5;
		
	public OntModel m_source;
	public OntModel m_target;
	public DirectedGraph source_Graph;
	public DirectedGraph target_Graph;


	public int source_ConceptNum;
	public int target_ConceptNum;
	public int sourceVertexNum;
	public int targetVertexNum;
	public String[] source_ConceptName;
	public String[] target_ConceptName;

	public int source_PropertyNum;
	public int target_PropertyNum;
	public String[] source_PropertyName;
	public String[] target_PropertyName;

	public int source_InstanceNum;
	public int target_InstanceNum;
	public String[] source_InstanceName;
	public String[] target_InstanceName;

	public String[] sourceVertexName = new String [5000];
	public String[] targetVertexName = new String [5000];
	public ConceptSubGraph[] targetSubGraph = new ConceptSubGraph[1000];
	public double[][] SimMatrixGraph;



	public Set sourceExset = new HashSet();
	public Set sourceOtset = new HashSet();
	public Set sourceStset = new HashSet();

	public Set targetExset = new HashSet();
	public Set targetOtset = new HashSet();
	public Set targetStset = new HashSet();
	
	public int MAX_DISPSUBGRAPH_SIZE;

//	constant varibles	
	public static final int MAX_DISPSUBGRAPH_ITER_TIMES = 6;


	public void SetConceptPara(int s_CNum, int t_CNum, String[] s_CName, String[] t_CName)
	{
		source_ConceptNum = s_CNum;
		target_ConceptNum = t_CNum;

		SimMatrixGraph = new double[source_ConceptNum][target_ConceptNum];
		source_ConceptName = new String [source_ConceptNum];
		target_ConceptName = new String [target_ConceptNum];
		
		source_ConceptName = s_CName;
		target_ConceptName = t_CName;
	}

	public void SetPropertyPara(int s_PNum, int t_PNum, String[] s_PName, String[] t_PName)
	{
		source_PropertyNum = s_PNum;
		target_PropertyNum = t_PNum;

		source_PropertyName = new String [source_PropertyNum];
		target_PropertyName = new String [target_PropertyNum];
		
		source_PropertyName = s_PName;
		target_PropertyName = t_PName;
	}

	public void SetInstancePara(int s_INum, int t_INum, String[] s_IName, String[] t_IName)
	{
		source_InstanceNum = s_INum;
		target_InstanceNum = t_INum;

		source_InstanceName = new String [source_InstanceNum];
		target_InstanceName = new String [target_InstanceNum];
		
		source_InstanceName = s_IName;
		target_InstanceName = t_IName;
	}

	/**********************
	 * ��������ı���ģ��ת��Ϊͼ
	 * Ϊ�˴���MultiGraph�ı���ͼ���������һ�ֻ��
	 * �ı�ʾģ��
	 ********************/
	@SuppressWarnings("unchecked")
	public DirectedGraph ont2Graph()
	{
	   	int mECount;
	   	ArrayList vList=new ArrayList();
	   	ArrayList vListWithURI=new ArrayList();
	    DirectedGraph g=new DefaultDirectedGraph(DefaultEdge.class);
	   	
//	    System.out.println("ԭʼ����Ĵ�С:" +m.getGraph().size());
	    mECount=0;
	   	//���챾��ͼ
	   	for (int i=0;i<trpNum;i++){
	   		Statement st=graphTrp[i];
	   		Resource r=st.getSubject();
	   		Property p=st.getPredicate();
	   		RDFNode o=st.getObject();
	   		
	   		//���S,P,O������
	   		ArrayList list=ontParse.getStLocalName(st);
	   		String sStr=(String)list.get(0);
	   		String pStr=(String)list.get(1);
	   		String oStr=(String)list.get(2);
	   		String stStr;
	   		
	   		//�������ͼ�ı�
	   		if (multiEdgeList.contains(st)){
	   			//����Statement�ڵ�
	   			stStr="Statement_"+mECount;
				g.addVertex(stStr);
				vList.add(stStr);
				vListWithURI.add(stStr);
				mECount++;
				
	   			//�����S
				if (!g.containsVertex(sStr)){
					  g.addVertex(sStr);
					  vList.add(sStr);
					  vListWithURI.add(r.toString());
				}
	   			//�����O
				if (!g.containsVertex(oStr)){
					  g.addVertex(oStr);
					  vList.add(oStr);
					  vListWithURI.add(o.toString());
				}
	   			//�����P
				if (!g.containsVertex(pStr)){
					  g.addVertex(pStr);
					  vList.add(pStr);
					  vListWithURI.add(p.toString());
				}
				
				//�����S-->St
				if (!g.containsEdge(sStr,stStr)){
					g.addEdge(sStr,stStr);
					graphME.add(g.getEdge(sStr,stStr));
				}
				//�����St-->P
				if (!g.containsEdge(stStr,pStr)){
					g.addEdge(stStr,pStr);
					graphME.add(g.getEdge(stStr,pStr));
				}
				//�����St-->O
				if (!g.containsEdge(stStr,oStr)){
					g.addEdge(stStr,oStr);
					graphME.add(g.getEdge(stStr,oStr));
				}
	   		}
	   		else//������ͨ��
	   		{
	   			//�����S
				if (!g.containsVertex(sStr)){
					  g.addVertex(sStr);
					  vList.add(sStr);
					  vListWithURI.add(r.toString());
				}
	   			//�����O
				if (!g.containsVertex(oStr)){
					  g.addVertex(oStr);
					  vList.add(oStr);
					  vListWithURI.add(o.toString());
				}
	   			//�����
				if (!g.containsEdge(sStr,oStr)){
					g.addEdge(sStr,oStr);
				}
	   		}
	   	}
	   	
	   	//�������һ��ȫ�ֽڵ�
	    //Add a global vertex, named "PengWang2007"
	    g.addVertex("pwang2007");
	    vList.add("pwang2007");
	    vListWithURI.add("pwang2007");
	   	
	   	//����ͼ�ڵ����Ϣ
	   	graphVNum=vList.size();
	   	graphVName=(String[])vList.toArray(new String[0]);
	   	graphVFullName=(String[])vListWithURI.toArray(new String[0]);
	   	
//	   	System.out.println("ת��Ϊ���ģ��ͼ��Ĵ�С:" +g.edgeSet().size());
	   	
	    //Output the graph for show
//	    WriteGraphDataForShow(g,VertexName);
		return g;
	}

	public DirectedGraph Onto2BiptGraph(OntModel m_Outer, boolean Flag_s_OR_t)
	{
		int i, count;
		OntModel m = m_Outer;
		String[] VertexName = new String [5000];
		int Vertex_Num;
		String baseURI,owlURI,rdfURI,rdfsURI;
		Set Exset = new HashSet();
		Set Otset = new HashSet();
		Set Stset = new HashSet();
		
	   	System.out.println("The size of Graph before Process: " +m.getGraph().size());
	    
	   	//��ʼ������URI
	   	baseURI = m.getNsPrefixURI("");
	   	owlURI = "http://www.w3.org/2002/07/owl#";
	   	rdfURI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	   	rdfsURI = "http://www.w3.org/2000/01/rdf-schema#";
	   	
	    if (Flag_s_OR_t)
	    {
	    	m_source =m;
	    }
	    else
	    {
	    	m_target =m;
	    }
	   	preProcessOnt();
	   	
	   	System.out.println("The size of Graph after Process: " +trpNum);
	    // create a graph based on URL objects
	   
	    DirectedGraph g =
	        new DefaultDirectedGraph(DefaultEdge.class);
	    
	    count = 0;

	    for (i=0;i<trpNum;i++)
		{
			String vs, vo, vp, stm;
			String stemp;
			boolean isURI;
			

			isURI = false;
			//ȡS
			if (graphTrp[i].getSubject().isURIResource())
			{
				vs=graphTrp[i].getSubject().getLocalName();
				isURI = true;
			}
			else
			{
				vs=graphTrp[i].getSubject().toString();
			}
			
			//��¼S��Set
			if (isURI)
			{
				stemp = getResourceBaseURI(graphTrp[i].getSubject().toString());
				
				if(stemp.equals(owlURI)
					|| stemp.equals(rdfURI)
					|| stemp.equals(rdfsURI))
					{
					//��Metadata Set
					Exset.add(vs);
					}
				else
				{
					//��Ontology Set
					Otset.add(vs);
				}
			}
			else
			{
				//��Ontology Set
				Otset.add(vs);
			}
			
			isURI = false;
			//ȡO
			if (graphTrp[i].getObject().isURIResource())
			{
				vo=graphTrp[i].getObject().asNode().getLocalName();
				isURI = true;
			}
			else
			{
				vo=graphTrp[i].getObject().toString();
			}
			
			//��¼O��Set
			if (isURI)
			{
				stemp = getResourceBaseURI(graphTrp[i].getObject().asNode().getURI());
				if(stemp.equals(owlURI)
					||stemp.equals(rdfURI)
					||stemp.equals(rdfsURI))
					{
					//��Metadata Set
					Exset.add(vo);
					}
				else
				{
					//��Ontology Set
					Otset.add(vo);
				}
			}
			else
			{
				//��Ontology Set
				Otset.add(vo);
			}
			
			isURI = false;
			//ȡP
			if (graphTrp[i].getPredicate().isURIResource())
			{
				vp=graphTrp[i].getPredicate().getLocalName();
				isURI = true;
			}
			else
			{
				vp=graphTrp[i].getPredicate().toString();
			}
			
			//��¼P��Set
			if (isURI)
			{
				stemp = getResourceBaseURI(graphTrp[i].getPredicate().getURI());
				if(stemp.equals(owlURI)
					||stemp.equals(rdfURI)
					||stemp.equals(rdfsURI))
					{
					//��Metadata Set
					Exset.add(vp);
					}
				else
				{
					//��Ontology Set
					Otset.add(vp);
				}
			}
			else
			{
				//��Ontology Set
				Otset.add(vp);
			}
			
			//����Statement
			stm = "Statement_"+i;
			
			//��¼Statement��Set
			Stset.add(stm);
			
			//����S
			if (g.containsVertex(vs) == false) 
			{
				  g.addVertex(vs);
				  VertexName[count]=vs;
				  count++;
			}
			
			//����O
			if (g.containsVertex(vo) == false) 
			{
				g.addVertex(vo);
			  	VertexName[count]=vo;
			  	count++;
			  	
			}
			
			//����P
			if (g.containsVertex(vp) == false) 
			{
				g.addVertex(vp);
			  	VertexName[count]=vp;
			  	count++;
			  	
			}
			
			//����Statement
			g.addVertex(stm);
			VertexName[count]=stm;
			count++;
			
			//�����S-->St
			if (g.containsEdge(vs, stm) == false) 
			{
				g.addEdge(vs,stm);
			}
			else
			{
				System.out.println(graphTrp[i].toString());
			}
			
			//�����St-->O
			if (g.containsEdge(stm,vo) == false) 
			{
				g.addEdge(stm,vo);
			}
			else
			{
				System.out.println(graphTrp[i].toString());
			}
			
			//�����St-->P
			if (g.containsEdge(stm,vp) == false) 
			{
				g.addEdge(stm,vp);
			}
			else
			{
				System.out.println(graphTrp[i].toString());
			}
		}
	    
	    // note directed edges are printed as: (<v1>,<v2>)
	    
	    //Record the numbers of vertex in the graph
	    Vertex_Num = count;
	    
	    //Record the numbers of edges in the graph
	    
	    trpNum = g.edgeSet().size();
	    
	    System.out.println("ת��ΪJGraphT��ʽ���ͼ��СΪ���㣺"+Vertex_Num+"\t�ߣ�"+trpNum);
	    
	    //Output the graph for show
	    writeGraphDataForShow(g,VertexName);
	  
	    if (Flag_s_OR_t)
	    {
	    	sourceVertexName = VertexName;
	    	sourceVertexNum = Vertex_Num;
	    	//����Set
	    	sourceExset = Exset;
	    	sourceOtset = Otset;
	    	sourceStset = Stset;
	    }
	    else
	    {
	    	targetVertexName = VertexName;
	    	targetVertexNum = Vertex_Num;
	    	//����Set
	    	targetExset = Exset;
	    	targetOtset = Otset;
	    	targetStset = Stset;
	    }
		return g;
	}

	public String getResourceBaseURI(String s)
	{
		String resStr=new String(s);
		int pos = 0;
		pos = resStr.indexOf((int)'#');
		if (pos==0) {return null;}
		resStr = resStr.substring(0,pos+1);
		return resStr;
	}
	
	public String getResourceLocalName(String s)
	{
		String resStr=new String(s);
		int pos=resStr.lastIndexOf('#');
		if (pos<0){return resStr;}
		resStr=resStr.substring(pos+1,resStr.length());
		return resStr;
	}
	
	public String getResourceLocalName(Resource r)
	{
		String resStr=new String();
		
		if (!ontParse.isBlankNode(r.toString()))
		{
			resStr=r.getLocalName();
		}
		else
		{
			resStr=r.toString();
		}
		return resStr;
	}


	public void writeGraphDataForShow(DirectedGraph g, String[] VertexName)
	{
		int i, count1;
		//  ------------------writting Graph File--------------------------
	   	PrintWriter outputStreamA = null;
	   	PrintWriter outputStreamB = null;
	   	PrintWriter outputStreamC = null;
	   	try
	   	{
	   		outputStreamA = new PrintWriter (new FileOutputStream ("E:/Temp/NetGraph.net"));
	   	}
	   	catch (FileNotFoundException e)
	   	{
	   		System.out.println("Can't open NetGraph.net");
	   		System.exit(0);
	   	}
	   	System.out.println("Writing to file...");
	   	
	   	try
	   	{
	   		outputStreamB = new PrintWriter (new FileOutputStream ("E:/Temp/VertexName.dat"));
	   	}
	   	catch (FileNotFoundException e)
	   	{
	   		System.out.println("Can't open VertexName.dat");
	   		System.exit(0);
	   	}
	   	
	   	try
	   	{
	   		outputStreamC = new PrintWriter (new FileOutputStream ("E:/Temp/EdgeName.dat"));
	   	}
	   	catch (FileNotFoundException e)
	   	{
	   		System.out.println("Can't open EdgeName.dat");
	   		System.exit(0);
	   	}
	   	
	    Iterator itr = g.vertexSet().iterator();
	    outputStreamA.println("*Vertices "+ g.vertexSet().size());
	    outputStreamB.println(g.vertexSet().size());
	    count1 = 0;
	    while (itr.hasNext())
	    {
	    	//vertex vt = (vertex) itr.next();
	    	VertexName[count1] = itr.next().toString();
	    	outputStreamA.println(count1+1 + " \"" +VertexName[count1]+"\"");
	    	outputStreamB.println(VertexName[count1]);
	    	count1++;
	    }
	    
	    outputStreamB.close();
	    
	    outputStreamA.println("*Edgeslist");
	    outputStreamC.println(g.edgeSet().size());
	    itr = g.edgeSet().iterator();
		String strRaw = new String();
		String strV1 = new String();
		String strV2 = new String();
		
	    while (itr.hasNext())
	    {
	    	strRaw = itr.next().toString();
	    	outputStreamC.println(strRaw);
	    	
	    	strV1 = strRaw.substring(1, strRaw.indexOf((int) ' '));
	    	for (i=0;i<count1;i++)
	    	{
	    		if (strV1.equals(VertexName[i]))
	    		{
	    			outputStreamA.print(i+1);
	    			break;
	    		}
	    	}
	    	strV2 = strRaw.substring(strRaw.lastIndexOf((int) ' ')+1, strRaw.length()-1);
	    	for (i=0;i<count1;i++)
	    	{
	    		if (strV2.equals(VertexName[i]))
	    		{
	    			outputStreamA.println(" " + (i+1));
	    			break;
	    		}
	    	}
	    	
	    	//outputStreamA.println("edge: { sourcename: \"" + strV1 + "\" targetname: \"" + strV2 + "\" }");
	    	
	    }
	    outputStreamC.close();
	   	outputStreamA.close();
	   	System.out.println("Writing complete!");

	}

	/*********************
	 * In the PreProcessGraph() Function, the ontology will be processed before matching.
	 * Using flag WITH_INSTANCE to control with instance or without instance
	 **********************/
	public void preProcessOnt()
	{
		OWLOntParse owlParse =ontParse;
		ArrayList trpList=new ArrayList();
		
		//Enrich the ontology
//		System.out.println("����ḻ");
		owlParse.enrichOnt(m,conceptNum,propNum,propName,baseURI);

	   	//List all the statements in Ontology
		StmtIterator iStm = m.listStatements();
   		
	   	//Traverse the statements to construct the triples needed in Graph
	   	while (iStm.hasNext()) 
	   	{
//			boolean WITH_INSTANCE=true;
			boolean WITH_LABEL_COMMENT=true;
			boolean CARDINALITY=true;
			boolean WITH_AXIOM=true;
			boolean WITH_ONTOLOGY_INFO=true;
			boolean WITH_NIL=true;
			boolean WITH_RDFTYPE=true;
			String str;
			
			Statement st = (Statement) iStm.nextStatement();
			
			//Remove the label and comment trip
			//����׼ȷ��˵���޸�Model��ֻ�ǿ��ƹ���ͼ����Ԫ��
			//ȥ������Ԫ��������֤ͼ�ĺ������ݴ���

			str = st.getPredicate().getLocalName();
			if (str.equals("comment") || str.equals("label")){//��Ҫע��
				WITH_LABEL_COMMENT = false;
			}
			
			if (str.equals("cardinality") || str.equals("maxCardinality")
				|| str.equals("minCardinality"))//Ҫά��
			{
				CARDINALITY = true;
			}
			
			if (   (str.equals("type") && st.getObject().asNode().getLocalName().equals("Ontology"))
				|| (str.equals("type") && st.getObject().asNode().getLocalName().equals("DeprecatedProperty"))
				|| (str.equals("type") && st.getObject().asNode().getLocalName().equals("DeprecatedClass"))
				|| (str.equals("type") && st.getObject().asNode().getLocalName().equals("DeprecatedProperty")))
				//��Ҫ�汾����
//				if (   (str.equals("type") && st.getObject().asNode().getLocalName().equals("Ontology"))
//						|| (str.equals("type") && st.getObject().asNode().getLocalName().equals("TransitiveProperty"))
//						|| (str.equals("type") && st.getObject().asNode().getLocalName().equals("SymmetricProperty"))
//						|| (str.equals("type") && st.getObject().asNode().getLocalName().equals("DeprecatedProperty"))
//						|| (str.equals("type") && st.getObject().asNode().getLocalName().equals("FunctionalProperty"))
//						|| (str.equals("type") && st.getObject().asNode().getLocalName().equals("DeprecatedClass"))
//						|| (str.equals("type") && st.getObject().asNode().getLocalName().equals("DeprecatedProperty")))
			{
				WITH_AXIOM = false;
			}
			
			if (str.equals("versionInfo")
				||str.equals("backwardCompatibleWith")
				||str.equals("imports")
				||str.equals("incompatibleWith")
				||str.equals("priorVersion"))//��Ҫ������Ϣ
			{
				WITH_ONTOLOGY_INFO = false;
			}
			
			if (str.equals("type")){
				WITH_RDFTYPE = true;
			}
			
			//����Ҫ������nil
			if ((st.getObject().toString()).equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil")){
				WITH_NIL=false;
			}
						
			if (WITH_LABEL_COMMENT && CARDINALITY && WITH_AXIOM
					&& WITH_ONTOLOGY_INFO && WITH_NIL && WITH_RDFTYPE) {
				// Keeping the triples with instances
				trpList.add(st);
			}
		}
// System.out.println("�����򵥹��˵ı�����Ԫ����Ŀ: "+trpList.size());
	   	//�ж���Ԫ����MutiGraph�ıߣ������д���Ϊ��ϵ�ͼģ�ͱ�ʾ��׼��
	   	trp4MultiGraph(trpList);
	   	//ת��Ϊ����
	   	trpNum=trpList.size();
	   	graphTrp=(Statement[])trpList.toArray(new Statement[0]);
	   	//��Ԫ������������Ҫ��������ܱ�֤������һ��
	   	sortGraphTriples(graphTrp);
	}

//	Construct the sub ontlogy graph by the neighbors method
	public void ConsSGNeighbor(Graph source, Graph target, int k_distance)
	{
		int i,j;
		Set curTotal_set = new HashSet();
		Set star_set = new HashSet();
		Set new_set = new HashSet();
		Set temp_set = new HashSet();
		
		//For each concept, decide its position in the graph,
		//then construct its subgraph using its neighbors.
		for (i=0;i<source_ConceptNum;i++)
		{
			System.out.println("Star Vertex:"+source_ConceptName[i]);
			//BreadthFirstIterator ir = new BreadthFirstIterator(source,source_ConceptName[i]);
			NeighborIndex ni = new NeighborIndex(source);
			if (!star_set.isEmpty()) star_set.clear();
			if (!curTotal_set.isEmpty()) curTotal_set.clear();
			if (source_ConceptName[i] == null) continue;
			
			//recorde the subgraph information
			cnptSubG[i] = new ConceptSubGraph();
			cnptSubG[i].conceptName = new String(source_ConceptName[i]);
			
			star_set.add((String)source_ConceptName[i]);
			curTotal_set.add((String)source_ConceptName[i]);
			
			cnptSubG[i].subGraph = new DefaultDirectedGraph(DefaultEdge.class);
			cnptSubG[i].subGraph.addVertex(source_ConceptName[i]);
			
			for (j=0;j<k_distance;j++)
			{
				if (!new_set.isEmpty()) new_set.clear();
				for (Iterator it=star_set.iterator(); it.hasNext();)
				{
					//Get a Vertex
					String cur_vertex = (String) it.next();
					//find the neighbors about the Vetex
					temp_set = ni.neighborsOf(cur_vertex);
					
					//decide the right edges to be added to subgraph
					for (Iterator itn=temp_set.iterator();itn.hasNext();)
					{
						String neighbor_vetex = (String) itn.next();
						
						//prepare for the new star_set
						if (!curTotal_set.contains(neighbor_vetex))
						{
							//add new vertex to the new subgraph
							cnptSubG[i].subGraph.addVertex(neighbor_vetex);
							
							//add the new edges to the new subgraph
							if (source_Graph.containsEdge(cur_vertex,neighbor_vetex))
							{
								cnptSubG[i].subGraph.addEdge(cur_vertex,neighbor_vetex);
							}
							else
							{
								cnptSubG[i].subGraph.addEdge(neighbor_vetex,cur_vertex);
							}
								
							new_set.add(neighbor_vetex);
							curTotal_set.add(neighbor_vetex);
						}
						
					}

				}
				
				//construct new star set
				System.out.println(new_set);
				//clear star_set
				star_set.clear();
				//add new neighbors
				star_set.addAll(new_set);
				
				if (new_set.isEmpty()) continue;
			}
			System.out.println("The SubGraph is:\n"+cnptSubG[i].subGraph.toString());
		}
		
		//clear all the sets
		if (!curTotal_set.isEmpty()) curTotal_set.clear();
		if (!star_set.isEmpty()) star_set.clear();
		if (!new_set.isEmpty()) new_set.clear();
		
		System.out.println("-----------------------------------------------");
		
		//For each concept, decide its position in the graph,
		//then construct its subgraph using its neighbors.
		for (i=0;i<target_ConceptNum;i++)
		{
			System.out.println("Star Vertex:"+target_ConceptName[i]);
			//BreadthFirstIterator ir = new BreadthFirstIterator(source,source_ConceptName[i]);
			NeighborIndex ni = new NeighborIndex(target);
			if (!star_set.isEmpty()) star_set.clear();
			if (!curTotal_set.isEmpty()) curTotal_set.clear();
			if (target_ConceptName[i] == null) continue;
			
			//recorde the subgraph information
			targetSubGraph[i] = new ConceptSubGraph();
			targetSubGraph[i].conceptName = new String(target_ConceptName[i]);
			
			star_set.add((String)target_ConceptName[i]);
			curTotal_set.add((String)target_ConceptName[i]);
			
			targetSubGraph[i].subGraph = new DefaultDirectedGraph(DefaultEdge.class);
			targetSubGraph[i].subGraph.addVertex(target_ConceptName[i]);
			
			for (j=0;j<k_distance;j++)
			{
				if (!new_set.isEmpty()) new_set.clear();
				for (Iterator it=star_set.iterator(); it.hasNext();)
				{
					//Get a Vertex
					String cur_vertex = (String) it.next();
					//find the neighbors about the Vetex
					temp_set = ni.neighborsOf(cur_vertex);
					
					//decide the right edges to be added to subgraph
					for (Iterator itn=temp_set.iterator();itn.hasNext();)
					{
						String neighbor_vetex = (String) itn.next();
						
						//prepare for the new star_set
						if (!curTotal_set.contains(neighbor_vetex))
						{
							//add new vertex to the new subgraph
							targetSubGraph[i].subGraph.addVertex(neighbor_vetex);
							
							//add the new edges to the new subgraph
							if (target_Graph.containsEdge(cur_vertex,neighbor_vetex))
							{
								targetSubGraph[i].subGraph.addEdge(cur_vertex,neighbor_vetex);
							}
							else
							{
								targetSubGraph[i].subGraph.addEdge(neighbor_vetex,cur_vertex);
							}
								
							new_set.add(neighbor_vetex);
							curTotal_set.add(neighbor_vetex);
						}
						
					}

				}
				
				//construct new star set
				System.out.println(new_set);
				//clear star_set
				star_set.clear();
				//add new neighbors
				star_set.addAll(new_set);
				
				if (new_set.isEmpty()) continue;
			}
			System.out.println("The SubGraph is:\n"+targetSubGraph[i].subGraph.toString());
		}
	 
	}

	public void ComputeSGMapping_Neighbor(ConceptSubGraph SG_source[], ConceptSubGraph SG_target[])
	{
		//MaxGraphIsoCoveringValiente match = new MaxGraphIsoCoveringValiente(null, null);
		int i,j;
		IGraphAccessor graph1 = new SimpleGraphAccessor();
		IGraphAccessor graph2 = new SimpleGraphAccessor();
		int sourceSize, targetSize, commonSize;
		
		SubgraphIsomorphism calc;
		
		for (i=0;i<source_ConceptNum;i++)
		{
				sourceSize = 0;
				targetSize = 0;
				if (cnptSubG[i] != null) {
					graph1 = JGraph2IGraph(cnptSubG[i].subGraph);
					sourceSize = cnptSubG[i].subGraph.vertexSet().size();
					for (j = 0; j < target_ConceptNum; j++) {
						if (targetSubGraph[j] != null) {
							graph2 = JGraph2IGraph(targetSubGraph[j].subGraph);
							targetSize = targetSubGraph[j].subGraph.vertexSet().size();
//							calc = new MaxCommonSubgraphIsoValiente(
//									graph1,
//									graph2,
//									MaxCommonSubgraphIsoValiente.DEFAULT_MIN_CLIQUE_SIZE,
//									MaxCommonSubgraphIsoValiente.DEFAULT_STRUCTURE_WEIGHT,
//									MaxCommonSubgraphIsoValiente.DEFAULT_LABEL_WEIGHT,
//									MaxCommonSubgraphIsoValiente.DEFAULT_DENOMINATOR);
							calc = new SubgraphIsomorphism(graph1,graph2);
							System.out.println("source:"+cnptSubG[i].subGraph.toString());
							System.out.println("target:"+targetSubGraph[j].subGraph.toString());
							System.out.println("sourceSubGraph[" + i + "]"
									+ "targetSubGraph[" + j + "]");
							
							long start = System.currentTimeMillis();//��ʼ��ʱ 
							calc.calculate();
							System.out.println("�˴νṹƥ���㷨ʱ�䣺"+(double)(System.currentTimeMillis()-start)/1000.+"��");
							TreeSet<String> covering = calc.getCliqueList();
							Iterator it = covering.iterator();
							commonSize = 0;
							if (it.hasNext()) 
							{
								String t1 = (String)it.next();
								System.out.println(t1);
								commonSize = GetCommonSubGraphSize(t1);
								System.out.println(commonSize);
							} 
							SimMatrixGraph[i][j] = (double)commonSize/(double)(sourceSize+targetSize-commonSize);
							System.out.println("SimMatrixGraph["+i+"]["+j+"]"+SimMatrixGraph[i][j]+"\n");
						}
					}
				}
			}


//		System.out.println("new MaxCommonSubgraphIsoValiente");
//		calc1.calculate();
//		System.out.println("calc1.calculate();");
//		calc1.isCalculated();
//		System.out.println("calc1.isCalculated();");

		
//		TreeSet<String> covering = calc1.getCliqueList();
////		covering.toArray()
//		for (TreeSet<String> c : covering) {
//			System.out.println("Covering " + c.getClique().toString() + " "
//					+ c.getSimilarity());
//		}

//			System.out.println(calc1.getSimilarity());
			
		System.out.println("done");
		
	}

	public IGraphAccessor JGraph2IGraph(DirectedGraph g)
	{
		IGraphAccessor igraph = new SimpleGraphAccessor();
		GraphNode[] node = new GraphNode[1500];
		int i, count, edgeStar, edgeEnd;
		int edgeCount;
		
	    Iterator itr = g.vertexSet().iterator();

	    count = 0;
	    while (itr.hasNext())
	    {
	    	node[count] = new GraphNode(itr.next().toString());
	    	count++;
	    }
	    
	    itr = g.edgeSet().iterator();
		String strRaw = new String();
		String strV1 = new String();
		String strV2 = new String();
		
		edgeCount=0;
	    while (itr.hasNext() && edgeCount<=7)
	    {
	    	strRaw = itr.next().toString();
	    	edgeStar = 0;
	    	edgeEnd = 0;
	    	strV1 = strRaw.substring(1, strRaw.indexOf((int) ' '));
	    	for (i=0;i<count;i++)
	    	{
	    		if (strV1.equals(node[i].toString()))
	    		{
	    			edgeStar = i;
	    			break;
	    		}
	    	}
	    	strV2 = strRaw.substring(strRaw.lastIndexOf((int) ' ')+1, strRaw.length()-1);
	    	for (i=0;i<count;i++)
	    	{
	    		if (strV2.equals(node[i].toString()))
	    		{
	    			edgeEnd = i;
	    			break;
	    		}
	    	}
	    	igraph.setEdge(node[edgeStar],node[edgeEnd]);
	    	edgeCount++;
	    	System.out.println(edgeCount);
	    }
		return igraph;
	}

	public int GetCommonSubGraphSize(String str)
	{
		int count = 1;
		int k = 0;
		if (str == null) return 0;
		while(k != -1)
		{
			k = str.indexOf((int)',',k+1);
			if (k != -1) count++;
		}
		return count;
	}

	public void ConsSGInformative(DirectedGraph g, boolean SOURCE_TARGET)
	{
		int i,j;
		int VertexNum,ConceptNum;
		int rn;
		int star;
		LinearEquation Equation = new LinearEquation();
		ConceptSubGraph[] SubGraph = new ConceptSubGraph[1000];
		String[] VertexName = new String [5000];
		String[] ConceptName = new String [1000];
		
		
		//set the vertex number in the graph
		if (SOURCE_TARGET)
		{
			VertexNum = sourceVertexNum;
			VertexName = sourceVertexName;
			ConceptName = source_ConceptName;
			ConceptNum = source_ConceptNum;
		}
		else
		{
			VertexNum = targetVertexNum;
			VertexName = targetVertexName;
			ConceptName = target_ConceptName;
			ConceptNum = target_ConceptNum;
		}
		
		//initinalize the weight of resources
		getResWeight();
		
		//Get the conduct matrix
		conductanceMatrix = getConductMatrix(VertexNum+1, g, VertexName);
		
		
		//for each vertex, compute its informative graph

		for (i=0;i<ConceptNum;i++)
		{
			if (ConceptName[i] == null) continue;
			
			System.out.println("����:"+i+"-->"+ConceptName[i]);
			
			//find the position of the concept
			star = -1;
			for (j=0;j<VertexNum;j++)
			{
				if (VertexName[j].equals(ConceptName[i]))
				{
					star = j+1;
					break;
				}
			}
			if (star == -1) continue;		
			
			//recorde the subgraph information
			SubGraph[i] = new ConceptSubGraph();
			SubGraph[i].conceptName = new String(ConceptName[i]);
			
			SubGraph[i].subGraph = new DefaultDirectedGraph(DefaultEdge.class);
			
			//------------------compute subgraph-------------------------
			
			
			//the last node is sink node, it is "sourceVertexNum+1"
			Equation.InitlizePara(VertexNum+1,star,VertexNum+1);
			
			//solve the linear system
			Equation.SetConductMatrix(conductanceMatrix);
			
			Equation.PrepareMatrixA_b(10.0, 0.0);
			
			Equation.Solve();
			
			//get row number of result
			rn=Equation.GetResultMaxtrixRowNum();
			
			//get result matrix
			voltageMatrix = Equation.GetResultMaxtrix();
			
			//raw current
			ComputeRawCurrent(VertexNum+1);
			//out current at each vertex
			ComputeCurrentOut(VertexNum+1);
			
			//run the displaygeneration algorithm
			//and saving the subgraph
			SubGraph[i].subGraph = displayGraphGeneration(VertexNum,VertexName,MAX_DISPSUBGRAPH_SIZE);
			
			System.out.println("Star Vertex:"+VertexName[star-1]+"\n");
			//-------------------compute subgraph done-----------------------
			
			//for show sub graph
			String[] tempName = new String[SubGraph[i].subGraph.vertexSet().size()];
			int ti = 0;
			int tcount = 0;
			
			Iterator itr = SubGraph[i].subGraph.vertexSet().iterator();
			tcount = 0;
		    while (itr.hasNext())
		    {
		    	tempName[tcount] = itr.next().toString();
		    	tcount++;
		    }
			
			this.writeGraphDataForShow(SubGraph[i].subGraph, tempName);
		}
		
		//return the subgraph
		if (SOURCE_TARGET)
		{
			cnptSubG = SubGraph;
		}
		else
		{
			targetSubGraph = SubGraph;
		}
		
	}

	public Resource GetResourceBySubject(OntModel m, Resource sub)
	{
		Resource r=null;
		Selector selector = new SimpleSelector(sub,null,(RDFNode)null);
		for (StmtIterator Iter = m.listStatements();Iter.hasNext();)
		{
			Statement ts = (Statement) Iter.next();
			r = ts.getSubject();
			if (r!=null && r.toString().equals(sub.toString())) break;
		}
		return r;
	}

	public RDFNode GetRDFNodeByObject(OntModel m, Resource obj)
	{
		RDFNode r=null;
		Selector selector = new SimpleSelector(null,null,(RDFNode)obj);
		for (StmtIterator Iter = m.listStatements();Iter.hasNext();)
		{
			Statement ts = (Statement) Iter.next();
			r = ts.getObject();
			if (r!=null && r.toString().equals(obj.toString())) break;
		}
		return r;
	}

	/*********************
	 * �������ͼ�еıߵĵ絼��
	 *********************/
	@SuppressWarnings("unchecked")
	public double[][] getConductMatrix(int Num, DirectedGraph g, String[] VName)
	{
		double[][] matrix = new double[Num][Num];
		double sum_Cuw;
		Resource r,rS,rO;
		OntResource ontr;
		Property rP;
		double wtS,wtP,wtO,wtPai;
		Statement stmt;
		int CWNum = fullConceptNum;
		int PWNum = fullPropNum;
		int IWNum = fullInsNum;
		int MWNum = ontLngMetaNum;
		int Sdegree;
		int Odegree;
		
		//����ͼ���ڽӾ��󣬼����Ӧ�ߵ�Ȩֵ
		for (int i=0;i<Num-1;i++)
		{
			sum_Cuw = 0;
			boolean sFlag=false;
			rS = null;
			wtS=0;
			for (int j=0;j<Num-1;j++)
			{
				//�ж�ͼ�е������Ƿ���ڱ�,Ҳ�����Ƿ���һ��triple
				if (g.containsEdge(VName[i],VName[j]))
				{
					//���S��O��Degree
					Sdegree = g.inDegreeOf(VName[i])+g.outDegreeOf(VName[i]);
								
					Odegree = g.inDegreeOf(VName[j])+g.outDegreeOf(VName[j]);
					
					//---------����������Ȩ��-----------
					
					//����Ҫ��ȡ��Triple��S,P,O,���ж����ǵľ���ɷ�
					
					if (!sFlag){//S��Ȩ�ػ�û�м����
						sFlag=true;
						/***********����S��Ȩ��***************/
						//��ȡS
						//�ж��ǲ���Statement���
						if (graphVName[i].length()>=9 && (graphVName[i].substring(0,9)).equals("Statement")){
							wtS=1.0;
						}
						else{
							//S�϶���Resource
							ontr = m.getOntResource(graphVFullName[i]);
							if (ontr==null){//��URL�ڵ�
								/*����Ԫ�����ҵ�S*/
								ontr=m.getOntResource(findSFromGraphTriple(graphVFullName[i]));
							}
							
							if (ontr!=null)//��OntResource
							{
								//��¼
								rS = ontr;
								
								//�ж�S�ĳɷ�
								boolean isMetaData=ontLngURI.contains(ontr.getNameSpace());
								if (ontr.isClass()&&!isMetaData)
								{
									//��class
									wtS = FindConceptWeight(graphVName[i], CWNum);

								}
								else if (ontr.isProperty()&&!isMetaData)
								{
									//��property
									wtS = this.FindPropertyWeight(graphVName[i],PWNum);
								}
								else if (ontr.isIndividual()&&!isMetaData)
								{
									//��Instance
									wtS = this.FindInstanceyWeight(graphVName[i],IWNum);
								}
								else if (isMetaData)
								{
									//��Ԫ��
									wtS = this.FindMetaWeight(graphVName[i],MWNum);
								}
								else
								{
									//����ʣ�µĲ���,����û�д���������ڵ�
									//����ȱʡֵ
									wtS = 0.5;
								}
							}
							else
							{
								//�����Resource
								r = m.getResource(graphVFullName[i]);
								r = GetResourceBySubject(m, r);
								
								//��¼
								rS = r;
								if (r != null) {
									if (r.isAnon()) {
										// �����������ڵ�
										// ����ȱʡֵ
										wtS = 0.5;
									} else if (r.isLiteral()) {
										// ��Literal��ֵ
										wtS = 0.3;
									} else {
										wtS = 0.2;
									}
								}
							}
						}

					}
					
					//**********����O��Ȩ��***************
					//��ȡO
					rO = null;
					wtO=0;
					//�ж��ǲ���Statement���
					if (graphVName[j].length()>=9 && (graphVName[j].substring(0,9)).equals("Statement")){
						wtO=1.0;
					}
					else{
						//O��RDFNode
						ontr = m.getOntResource(graphVFullName[j]);
						RDFNode node=null;
						if (ontr==null){//��URL�ڵ�
							/*����Ԫ�����ҵ�O*/
							node=findOFromGraphTriple(graphVFullName[j]);
							if (node.isAnon()){//�����ڵ�
								ArrayList lx = ontParse.getAnonResource(
										graphVFullName[j], m);
								if (((Integer) lx.get(1)).intValue() == 0) {
									ontr = m.getOntResource(m.getResource(node
											.toString()));
								} else {
									ontr=(OntResource)lx.get(0);
								}
							}
							else{
								ontr=m.getOntResource(m.getResource(node.toString()));
							}
						}
						//��¼
						rO=ontr;
						if (ontr!=null)//��OntResource
						{
							//�ж�O�ĳɷ�
							boolean isMetaData=ontLngURI.contains(ontr.getNameSpace());
							if (ontr.isClass()&&!isMetaData)
							{
								//��class
								wtO = FindConceptWeight(graphVName[j],CWNum);
							}
							else if (ontr.isProperty()&&!isMetaData)
							{
								//��property
								wtO = this.FindPropertyWeight(graphVName[j],PWNum);
							}
							else if (ontr.isIndividual()&&!isMetaData)
							{
								//��Instance
								wtO = this.FindInstanceyWeight(graphVName[j],IWNum);
							}
							else if (isMetaData)
							{
								//��Ԫ��
								wtO = this.FindMetaWeight(graphVName[j],MWNum);
							}
							else if (ontr.isAnon())
							{
								//�����������ڵ�
								//����ȱʡֵ
								wtO = 0.5;
							}
							else if (ontr.isLiteral())
							{
								//��Literal
								wtO = 0.3;
							}
							else
							{
								//�����������Щ,����Ƚϵ͵�Ȩ��
								wtO = 0.2;
							}
						}
						else
						{
							//��Resource,������OntResource
							RDFNode tr = node;
							//��¼
							rO =m.getResource(tr.toString());
							
							if (tr.isAnon())
							{
								//�����������ڵ�
								//����ȱʡֵ
								wtO = 0.5;
							}
							else if (tr.isLiteral())
							{
								//��Literal��ֵ
								wtO = 0.3;
							}
							else
							{
								wtO = 0.2;
							}
						}
					}

					//*********����P��Ȩ��*************		
					//��ȡP,��ȡ�Ĺ��̺�S,O��ͬ
					rP=null;
					wtP=0;
					stmt=null;
					String pStr=null;
					//����Ƕ���ͼ�ıߣ�����Ѱ��P
					if (rS!=null && rO!=null){
						ArrayList ls=new ArrayList();
						ls=findPFromGraphTriple(rS.toString(),rO.toString());
						rP=(Property)ls.get(0);
						stmt=(Statement)ls.get(1);
					}
					
					//���rPΪ��,��˵���Ƕ��ر�
					if (rP==null)
					{
						//��һ��ȱʡ��Ȩ��
						wtP=1.0;
					}
					else
					{
						//��OntProperty,Ԫ��������������������
						if ((m.getOntProperty(rP.toString())!=null) && !ontLngURI.contains(getResourceBaseURI(rP.toString())))
						{
							//��OntProperty
							wtP = this.FindPropertyWeight(this.getResourceLocalName(rP.toString()),PWNum);
						}
						else if(ontLngURI.contains(getResourceBaseURI(rP.toString()))){
							//��Ԫ��
							wtP = this.FindMetaWeight(this.getResourceLocalName(rP.toString()),MWNum);
						}
						else {
							// �����������
							wtP = 0.2;
						}
					}
					
					//**********����Statement��PaiȨ��***************
					if (stmt!=null)
					{
						wtPai = this.GetStatementPaiWeight( m,stmt,rP,rS);
					}
					else
					{
						wtPai = 0;
					}

					//--------------��������ߵ�Ȩ��----------------------
					if (wtPai>0)
					{
						matrix[i][j] = (wtP+(wtS/(double)Sdegree+wtO/(double)Odegree)/2.0+wtPai)/3.0;
					}
					else
					{
						matrix[i][j] = (wtP+(wtS/(double)Sdegree+wtO/(double)Odegree)/2.0)/2.0;
					}
					//Ҳ����������ߵ�Ȩ�أ����ܼ򵥵���������
//					if (wtP>0.7)
//					{
//						matrix[j][i] = 1.0*matrix[i][j];
//					}
//					else
//					{
//						matrix[j][i] = 0.2*matrix[i][j];
//					}
					//����ߵ�Ȩ��
					matrix[j][i] = 0.8*matrix[i][j];
	 			}
				sum_Cuw+=matrix[i][j];
			}

			//At the sink node Num, all nodes should link to it
			//The conductance of sink z is special, it is compted by follows:
			//     C(u,z)=a Sigma C(u,w), where w!=z
			// Here, we set a=1.0
			// I will impose the result graph in my test
			//�����Ȩ������Ҳ���ò�������������·��Ѹ���䵽sinknode
			//Ϊ�˱���ͷ�̫ǿ������ǰ���ϵ����
			matrix[i][Num-1] = 0.05*sum_Cuw;
			matrix[Num-1][i] = matrix[i][Num-1];
		}
		return matrix;
	}


//	compute the resource weight
	public void getResWeight()
	{
		//��������
		int CNum=fullConceptNum;;
		OntClass[] CName=fullConceptName;
		int PNum=fullPropNum;
		OntProperty[] PName=fullPropName;
		int INum=fullInsNum;
		Individual[] IName=fullInsName;
		int MNum=ontLngMetaNum;
		String[] MName=ontLngMetaName;
		
		ArrayList list=new ArrayList();
			
		// ��������Ȩ��
		double[] cnptFW=new double[CNum];
		double[] cnptSW=new double[CNum];
		double[] cnptIW=new double[CNum];
		//��1���õ�ʹ��Ƶ�ʶ�
		list=getCnptFrequentW();
		cnptFW=(double[])list.get(0);
		//(2)������ṹSpecificity��ʵ��ʹ��Ȩ��
		list=getCnptSpcfct();
		cnptSW=(double[])list.get(0);
		//(3)�����Ӧʵ�����ɵ�Ȩ��
		list=getCnptInsW();
		cnptIW=(double[])list.get(0);
	
		for (int i=0;i<CNum;i++)
		{
			String cStr=null;
			cStr=getResourceLocalName(CName[i]);
			cWeight[i] = new ConceptWt();
			cWeight[i].CName = new String(cStr);
			cWeight[i].weight = cfw*cnptFW[i]+csw*cnptSW[i]+ciw*cnptIW[i];
		}
		
		//�������Ե�Ȩ��
		double[] propFW=new double[PNum];
		double[] propSW=new double[PNum];
		double[] propIW=new double[PNum];

		//��1���õ�ʹ��Ƶ�ʶ�
		list=getPropFrequentW();
		propFW=(double[])list.get(0);
		//(2)������SpecificityȨ��
		list=getPropSpcfct();
		propSW=(double[])list.get(0);
		//(3)������ʵ���й��ɵ�Ȩ��
		list=getPropInsW();
		propIW=(double[])list.get(0);
		
		for (int i=0;i<PNum;i++)
		{
			OntProperty p = PName[i];
			String pStr=null;
			pStr=getResourceLocalName(p);
			pWeight[i] = new PropertyWt();
			pWeight[i].PName = new String(pStr);
			pWeight[i].weight = pfw*propFW[i]+psw*propSW[i]+piw*propIW[i];
		}	
		
		//����ʵ��Ȩ��
		double[] insDPW=new double[INum];
		double[] insOPW=new double[INum];
		double[] insCIW=new double[INum];

		//��1���õ�ʹ��datatypeproperty��Ƶ��Ȩ��
		list=getInsDPW();
		insDPW=(double[])list.get(0);
		//(2)����objectproperty��Ƶ��Ȩ��
		list=getInsOPW();
		insOPW=(double[])list.get(0);
		//(3)��Ӧ�����ʵ��������Ȩ��
		list=getInsCIW();
		insCIW=(double[])list.get(0);
		for (int i=0;i<INum;i++)
		{
			Individual idv = IName[i];
			String iStr=null;
			iStr=getResourceLocalName(idv);
			iWeight[i] = new InstanceWt();
			iWeight[i].IName = new String(iStr);
			iWeight[i].weight =idpw*insDPW[i]+iopw*insOPW[i]+icw*insCIW[i];
		}
		
		//����Ԫ���Ȩ��
		double[] metaFW=new double[MNum];
		//�õ�Ƶ��Ȩ��
		list=getMetaFW();
		metaFW=(double[])list.get(0);
		for (int i=0;i<MNum;i++)
		{
			metaWeight[i] = new OntLngMetaWt();
			metaWeight[i].MName = new String(MName[i]);
			metaWeight[i].weight =metaFW[i];
		}
	}
	
	/*********
	 * ����Ԫ���Ȩ��
	 * ����ķ����Ǳ�����Ԫ�飬��Ҳ������ͼ�Ķ�������
	 *********/
	public ArrayList getMetaFW()
	{
		int MNum=ontLngMetaNum;
		String[] MName=ontLngMetaName;;
		double[] mw=new double[MNum];
		int max=0;
		ArrayList result=new ArrayList();
		ArrayList mList=new ArrayList();
		//����Statements���жϳ��ֵ�Ԫ�����
		for(StmtIterator it=m.listStatements();it.hasNext();){
			Statement st=(Statement)it.next();
			Resource rs=st.getSubject();
			Property rp=st.getPredicate();
			RDFNode ro=st.getObject();
			//ͨ��URI�ж�Ԫ��
			String uriStr;
			if(rs.isURIResource()){
				uriStr=rs.getNameSpace();
				if (ontLngURI.contains(uriStr))
				{
					mList.add(rs.getLocalName());
				}
			}
			if(rp.isURIResource()){
				uriStr=rp.getNameSpace();
				if (ontLngURI.contains(uriStr))
				{
					mList.add(rp.getLocalName());
				}
			}
			if(ro.isURIResource()){
				uriStr=ro.asNode().getNameSpace();
				if (ontLngURI.contains(uriStr))
				{
					mList.add(ro.asNode().getLocalName());
				}
			}
		}
		for (int i=0;i<MNum;i++){
			for (Iterator j=mList.iterator();j.hasNext();){
				String str=(String)j.next();
				if(MName[i].equals(str)){
					mw[i]+=1.0;
				}
			}
			max=Math.max(max,(int)mw[i]);
		}
		//����Ȩ��
		for (int i=0;i<MNum;i++){
			if (max==0 || (int)mw[i]==0){
				mw[i]=0.0;
			}
			else {
				if (max==1){
					if ((int)mw[i]==1){
						mw[i]=1.0;
					}
				}
				else{
					mw[i]=0.5*(1.0/(double)mw[i])+0.5*(1.0-Math.log((double)mw[i])/Math.log((double)max));
				}
			}	
		}
		result.add(0,mw);
		result.add(1,max);
		return result;
	}
	/*********
	 * ����ʵ���漰��ͬ��ʵ����Ȩ��
	 *********/
	public ArrayList getInsCIW()
	{
		int INum=fullInsNum;
		Individual[] IName=fullInsName;;
		double[] icw=new double[INum];
		int max=0;
		ArrayList result=new ArrayList();
		for (int i=0;i<INum;i++){
			Individual idl =IName[i];
			ArrayList tl=new ArrayList();
			tl=ontParse.listSiblingOfInstance(m,idl);
			int n=((Integer)tl.get(0)).intValue();
			max=Math.max(max,n);
			icw[i]=(double)n;
		}
		//����Ȩ��
		for (int i=0;i<INum;i++){
			if (max==0 || icw[i]==0){
				icw[i]=0.0;
			}
			else{
				if (max==1 && icw[i]==1){
					icw[i]=1;
				}
				else{
					icw[i]=0.5*(1.0/icw[i])+0.5*(1-icw[i]/(double)max);
				}				
			}
		}
		result.add(0,icw);
		result.add(1,max);
		return result;
	}
	
	/*********
	 * ����ʵ���漰��DatatypeProperty��Ȩ��
	 *********/
	public ArrayList getInsDPW()
	{
		int INum=fullInsNum;
		Individual[] IName=fullInsName;;
		double[] dpw=new double[INum];
		int max=0;
		ArrayList result=new ArrayList();
		// ����ʵ����Ӧ��DatatypeProperty
		for (int i=0;i<INum;i++){
			Individual idl = IName[i];
			ArrayList tl=new ArrayList();
			tl=ontParse.listDatatypePropertiesOfInstance(m,idl);
			int n=((Integer)tl.get(0)).intValue();
			max=Math.max(max,n);
			dpw[i]=(double)n;
		}
		//����Ȩ��
		for (int i=0;i<INum;i++){
			if (max==0){
				dpw[i]=0.0;
			}
			else{
				dpw[i]=dpw[i]/(double)max;
			}
		}
		result.add(0,dpw);
		result.add(1,max);
		return result;
	}

	/****************
	 * ����ʵ���漰��ObjectProperty��Ȩ��
	 ****************/
	public ArrayList getInsOPW()
	{
		int INum=fullInsNum;
		Individual[] IName=fullInsName;;
		double[] opw=new double[INum];
		int max=0;
		ArrayList result=new ArrayList();
		// ����ʵ����Ӧ��ObjectProperty
		for (int i=0;i<INum;i++){
			Individual idl = IName[i];
			ArrayList tl=new ArrayList();
			tl=ontParse.listObjectPropertiesOfInstance(m,idl);
			int n=((Integer)tl.get(0)).intValue();
			max=Math.max(max,n);
			opw[i]=(double)n;
		}
		//����Ȩ��
		for (int i=0;i<INum;i++){
			if (max==0){
				opw[i]=0.0;
			}
			else{
				opw[i]=opw[i]/(double)max;
			}
		}
		result.add(0,opw);
		result.add(1,max);
		return result;
	}

	public ArrayList getCnptInsW()
	{
		int CNum=fullConceptNum;
		OntClass[] CName=fullConceptName;;
		double[] iw=new double[CNum];
		int max=0;
		ArrayList result=new ArrayList();
		
		// ��������Ӧ��ʵ��
		for (int i=0;i<CNum;i++){
			OntClass c=CName[i];
			ArrayList tl=new ArrayList();
			tl=ontParse.listInstanceOfConcept(c);
			int n=((Integer)tl.get(0)).intValue();
			max=Math.max(max,n);
			iw[i]=(double)n;
		}
		//����ʵ����ɵ�Ȩ��
		for (int i=0;i<CNum;i++){
			if (max==0 || (int)iw[i]==0){
				iw[i]=0.0;
			}
			else {
				if (max==1){
					if ((int)iw[i]==1){
						iw[i]=1.0;
					}
				}
				else{
					iw[i]=0.5*(1.0/(double)iw[i])+0.5*(1.0-Math.log((double)iw[i])/Math.log((double)max));
				}
			}	
		}
		result.add(0,iw);
		result.add(1,max);
		return result;
	}
	
	/***********************
	 *�������Բ��뵽instance����� 
	 **********************/
	public ArrayList getPropInsW()
	{
		int PNum=fullPropNum;
		OntProperty[] PName=fullPropName;;
		double[] iw=new double[PNum];
		int max=0;
		ArrayList result=new ArrayList();
		
		// �������Զ�Ӧ��ʵ��
		for (int i=0;i<PNum;i++){
			OntProperty p = PName[i];
			ArrayList tl=new ArrayList();
			tl=ontParse.listInstanceOfProperty(m,p);
			int n=((Integer)tl.get(0)).intValue();
			max=Math.max(max,n);
			iw[i]=(double)n;
		}
		//����ʵ����ɵ�Ȩ��
		for (int i=0;i<PNum;i++){
			if (max==0 || (int)iw[i]==0){
				iw[i]=0.0;
			}
			else {
				if (max==1){
					if ((int)iw[i]==1){
						iw[i]=1.0;
					}
				}
				else{
					iw[i]=0.5*(1/iw[i])+0.5*(1-Math.log((double)iw[i])/Math.log((double)max));
				}
			}	
		}
		result.add(0,iw);
		result.add(1,max);
		return result;
	}
	
	/***********************
	 *������������specificity 
	 **********************/
	public ArrayList getCnptSpcfct()
	{
		int CNum=fullConceptNum;
		OntClass[] CName=fullConceptName;;
		double[] sw=new double[CNum];
		ArrayList result=new ArrayList();
		int uplength,downlength;
		// ��������Specificity
		for (int i=0;i<CNum;i++){
			OntClass c=CName[i];
//			Node node=null;
//			node=m.getResource(CName[i]).asNode();
//			Resource ce=m.getResource(node.getURI());
//			OntClass c = m.getOntClass(node.getURI());
			
			uplength=0;
			downlength=0;
			Set cset=new HashSet();
			cset.add(c);
			uplength = ontParse.getMaxUpperClassPathCount(System.out,c,cset);
			cset.clear();
			cset.add(c);
			downlength = ontParse.getMaxLowerClassPathCount(System.out,c,cset);
			if (uplength==0) {uplength = 1;}
			if (downlength==0) {downlength = 1;}
			sw[i] = (double)uplength/(double)(uplength+downlength-1);			
		}
		result.add(0,sw);
		return result;
	}
	
	/***********************
	 *����������Ե�specificity 
	 **********************/
	public ArrayList getPropSpcfct()
	{
		int PNum=fullPropNum;
		OntProperty[] PName=fullPropName;;
		double[] sw=new double[PNum];
		ArrayList result=new ArrayList();
		int uplength,downlength;
		// ��������Specificity
		for (int i=0;i<PNum;i++){
			OntProperty p = PName[i];
			uplength=0;
			downlength=0;
			Set pset=new HashSet();
			pset.add(p);
			uplength = ontParse.getMaxUpperPropertyPathCount(System.out,p,pset);
			pset.clear();
			pset.add(p);
			downlength = ontParse.getMaxLowerPropertyPathCount(System.out,p,pset);
			if (uplength==0) uplength = 1;
			if (downlength==0) downlength = 1;
			sw[i] = (double)uplength/(double)(uplength+downlength-1);			
		}
		result.add(0,sw);
		return result;
	}
	
	/***********************
	 *�����������ı�ʹ�������Ƶ�ʶ�
	 *ͨ��ͼ�Ķ����� 
	 **********************/
	public ArrayList getCnptFrequentW()
	{
		//��������
		int CNum=fullConceptNum;
		OntClass[] CName=fullConceptName;;
		int maxDegree=0;
		double[] fw=new double[CNum];
		ArrayList result=new ArrayList();
			
		// ��������Ƶ��
		for (int i=0;i<CNum;i++){
			//�õ�LocalName
			String localName=ontParse.getResourceLocalName(CName[i]);
			//���
			fw[i]=orgnGraph.inDegreeOf(localName)+orgnGraph.outDegreeOf(localName);
			//�ж�����
			maxDegree=Math.max(maxDegree,(int)fw[i]);
		}
		//����Ȩ��
		for (int i=0;i<CNum;i++){//��������fw[i]<=maxDegree
			if (maxDegree==0 || (int)fw[i]==0){
				fw[i]=0.0;
			}
			else {
				if (maxDegree==1){
					if ((int)fw[i]==1){
						fw[i]=1.0;
					}
				}
				else{
					fw[i]=0.5*(1.0/(double)fw[i])+0.5*(1.0-Math.log((double)fw[i])/Math.log((double)maxDegree));
				}
			}			
		}
		//���ؽ��
		result.add(0,fw);
		result.add(1,maxDegree);
		return result;
	}

	/***********************
	 *����������Եı�ʹ�������Ƶ�ʶ�
	 *����ֻ����������Ϊ�ڵ������������Խڵ�Ķ���Ϊ������������������
	 *����Խ������ԣ���ϢԽ��
	 *ͨ��ͼ�Ķ����� 
	 **********************/
	public ArrayList getPropFrequentW()
	{
		//��������
		int PNum=fullPropNum;
		Property[] PName=fullPropName;;
		int maxDegree=0;
		double[] fw=new double[PNum];
		ArrayList result=new ArrayList();
			
		// �������Ե�Ƶ��
		for (int i=0;i<PNum;i++){
			//�õ�LocalName
			String localName=getResourceLocalName(PName[i]);
			//���
			fw[i]=orgnGraph.inDegreeOf(localName)+orgnGraph.outDegreeOf(localName);
			//�ж�����
			maxDegree=Math.max(maxDegree,(int)fw[i]);
		}
		//����Ȩ��
		for (int i=0;i<PNum;i++){
			if (maxDegree==0 || (int)fw[i]==0){
				fw[i]=0.0;
			}
			else {
				if (maxDegree==1){
					if ((int)fw[i]==1){
						fw[i]=1.0;
					}
				}
				else{
					fw[i]=0.5*(1.0/fw[i])+0.5*(1-Math.log((double)fw[i])/Math.log((double)maxDegree));
				}
			}			
		}
		//���ؽ��
		result.add(0,fw);
		result.add(1,maxDegree);
		return result;
	}
	

//	����Ҫ������Ӧ��class,������Ҫ�������Ǹ�����
	public double GetInstanceWeight(Individual idv, boolean Flag_s_OR_t, String baseURI)
	{
		int uplength=0,downlength=0;
		int i,j;
		double weight = 0;
		String[] CName = new String[100];
		int CNum = 0;
		int CWTNum = 0;
		double dtemp = 0;
		
		if (Flag_s_OR_t) 
		{
			CWTNum = source_ConceptNum;
		}
		else
		{
			CWTNum = target_ConceptNum;
		}
		CNum = ontParse.listConceptsOfInstance(CName,idv,true);
		
		//��instance��Ӧ������class,����,Ѱ������Ȩ��
		for (i=0;i<CNum;i++)
		{
			boolean HasClass = false;
			dtemp = 0;
			//��û�����class
			for (j=0;j<CWTNum;j++)
			{
				if ((baseURI+CName[i]).equals(cWeight[j].CName))
				{
					HasClass =true;
					dtemp = cWeight[j].weight;
					break;
				}
			}
			
			//��������class��Ȩ,������е�Ȩ��ȡ���ֵ
			if (HasClass) {weight = Math.max(weight,dtemp);}
			else
			//���û�����class,����ȱʡ��Ȩ��
			{weight = Math.max(weight,0.2);}
		}
		
		System.out.println(idv.toString()+"-->"+weight);
		
		return weight;
	}

	public double GetStatementPaiWeight(OntModel m,Statement stmt,Property rP, Resource rS)
	{
		double wtPai = 0;
		boolean flag;
		flag = false;
		int count = 0;
		String[] CName = new String[10];
		int CNum = 0;
		String CaName,CbName;
		boolean SameNameFlag;
		int i;
		Individual ix,iy;
		
		//�ж��Ƿ���Ҫ����Pai
		OntResource tempS = m.getOntResource(rS);
		RDFNode tempONode = stmt.getObject();
		OntResource temprO = m.getOntResource(m.getResource(tempONode.toString()));
		if (m.getOntProperty(rP.toString())!=null)
		{
			//��OntProperty����Ҫ����Pai
			//����Ҫ����(1):S��Instance,��(2)O��Instance��Literal

			if (tempS!=null && tempS.isIndividual())
			{
				//S��Instance
				//��һ���ж�O
	    		if (temprO!=null && temprO.isIndividual())
	    		{
//	    				System.out.println(tempS.toString());
	    				flag = true;
	    		}
	    		if (tempONode!=null && tempONode.isLiteral())
	    		{
//	    				System.out.println(tempS.toString());
	    				flag = true;
	    		}
	        	
			}
		}
		
		if (flag)
		{
			//��������,��Ҫ����Pai
			count = 0;
			ix=null;
			iy=null;
			if (tempS.isAnon()){
				ix=ontParse.getAnonIndividual(tempS.toString(),m);
			}
			else{
				ix=m.getIndividual(tempS.toString());
			}
			if (temprO!=null && temprO.isIndividual())
			{
				//����individual---individual���
				if (temprO.isAnon()){
					iy=ontParse.getAnonIndividual(temprO.toString(),m);
				}
				else{
					iy=m.getIndividual(temprO.toString());
				}
				
				
				//(0)Ԥ���ҵ�Ca��Cb
	    		ontParse.listConceptsOfInstance(CName, ix, true);
	    		CaName = CName[0];//ֻ���ǵ�һ����
	    		ontParse.listConceptsOfInstance(CName, iy, true);
	    		CbName = CName[0];//ֻ���ǵ�һ����
				
				//(1)�г�����<?,P,?>
				Selector selector = new SimpleSelector(null,rP,(RDFNode)null);
	        	for (StmtIterator Iter = m.listStatements( selector);Iter.hasNext();)
	        	{
	        		Statement temps = (Statement) Iter.next();
//	        		System.out.println(temps.toString());
	        		
	        		//(2)����,��֤?����individual
	            	Resource temprsub = temps.getSubject();
	            	OntResource tempOrsub = (OntResource)m.getOntResource(temprsub);
	            	
	            	RDFNode tempro = temps.getObject();
	            	OntResource tempOro = m.getOntResource(m.getResource(tempro.toString()));
	            	if (tempOrsub!=null && tempOrsub.isIndividual()
	            		&& tempOro!=null && tempOro.isIndividual())
	            	{
	            		//��֤��individual
	            		
	            		//(3)�ҵ�ix��iy��Ӧ��Cx��Cy
	            		if (tempOrsub.isAnon()){
	            			ix=this.obtainAnonIndividual(tempOrsub.toString());
	            		}
	            		else{
	            			ix = m.getIndividual(tempOrsub.toString());
	            		}
	            		if (tempOro.isAnon()){
	            			iy=ontParse.getAnonIndividual(tempOro.toString(),m);
	            		}
	            		else{
	            			iy = m.getIndividual(tempOro.toString());
	            		}
	            		
	            		// (4)�ȽϿ��Ƿ�͸�����ix��iy��Cx��Cy��ͬ
	            		
	            		SameNameFlag = false;
	            		
	            		//���ҵ�Cx
	            		CNum = ontParse.listConceptsOfInstance(CName, ix, true);
	            		
	            		if (CNum>0)//�Ƚ�Cx��Ca
	            		{
	            			//����������
	            			for (i=0;i<CNum;i++)
	            			{
	            				if (CName[i].equals(CaName))
	            				{
	            					SameNameFlag = true;
	            					break;
	            				}
	            			}
	            			
	            		}
	            		else
	            		{
	            			SameNameFlag = false;
	            		}
	            		
	            		if (SameNameFlag)
	            		{
	            			//Cx==Ca,������Ƚ�Cy��Cb
	                		SameNameFlag = false;
	                		
	                		//���ҵ�Cy
	                		CNum = ontParse.listConceptsOfInstance(CName, iy, true);
	                		
	                		if (CNum>0)//�Ƚ�Cy��Cb
	                		{
	                			//����������
	                			for (i=0;i<CNum;i++)
	                			{
	                				if (CName[i].equals(CbName))
	                				{
	                					SameNameFlag = true;
	                					break;
	                				}
	                			}
	                			
	                		}
	                		else
	                		{
	                			SameNameFlag = false;
	                		}
	                		
	                		if (SameNameFlag)
	                		{
	                			//���������Ӧ�ĸ�����
	                			count++;
	                		}
	            		}
	            	}
	        	}
			}
			else
			{
				//����individual---Literal���
				
				//(1)�ҵ�Ca
				if(tempS.isAnon()){
					ix=ontParse.getAnonIndividual(tempS.toString(),m);
				}
				else{
					ix=m.getIndividual(tempS.toString());
				}
				
	    		ontParse.listConceptsOfInstance(CName, ix, true);
	    		CaName = CName[0];
	    		
	    		//(2)�г�����<?,P,?>
				Selector selector = new SimpleSelector(null,rP,(RDFNode)null);
	        	for (StmtIterator Iter = m.listStatements( selector);Iter.hasNext();)
	        	{
	        		Statement temps = (Statement) Iter.next();
//	        		System.out.println(temps.toString());
	        		
	        		//(3)����,��֤?��individual��Literal
	            	Resource temprsub = temps.getSubject();
	            	OntResource tempOrsub = (OntResource)m.getOntResource(temprsub);
	            	
	            	RDFNode tempro = temps.getObject();
	            	OntResource tempOro = m.getOntResource(tempro.toString());
	            	//ע�������Literal�Ƿ�����ȷ�ж�
	            	if (tempOrsub!=null && tempOrsub.isIndividual()
	            		&& tempro!=null && tempro.isLiteral())
	            	{
	            		//��֤��individual--Literal
	            		
	            		//(4)�ҵ�ix��Ӧ��Cx,�ȽϿ��Ƿ�͸�����Ca��ͬ
	            		if (tempOrsub.isAnon()){
	            			ix=ontParse.getAnonIndividual(tempOrsub.toString(),m);
	            		}
	            		else{
	            			ix = m.getIndividual(tempOrsub.toString());
	            		}
	            		
	            		SameNameFlag = false;
	            		
	            		//�ҵ�Cx
	            		CNum = ontParse.listConceptsOfInstance(CName, ix, true);
	            		
	            		if (CNum>0)//�Ƚ�Cx��Ca
	            		{
	            			//����������
	            			for (i=0;i<CNum;i++)
	            			{
	            				if (CName[i].equals(CaName))
	            				{
	            					SameNameFlag = true;
	            					break;
	            				}
	            			}
	            			
	            		}
	            		else
	            		{
	            			SameNameFlag = false;
	            		}
	            		
	            		if (SameNameFlag)
	            		{
	            			//Cx==Ca,�����
	                   		count++;
	            		}
	            	}
	        	}
			}
			//����Pai
			if (count == 0)
			{
				wtPai = 0;
			}
			else
			{
				wtPai = 1.0/(double)count;
			}
		}
		else
		{
			//���ü���Pai
			wtPai = 0;
		}
		ix=null;
		iy=null;
		return wtPai;
	}

	public double FindConceptWeight(String Name, int Num)
	{
		int i;
		double weight = 0;
		for (i=0;i<Num;i++)
		{
			if (cWeight[i].CName.equals(Name))
			{
				weight = cWeight[i].weight;
				break;
			}
		}
		return weight;
	}

	public double FindPropertyWeight(String Name, int Num)
	{
		int i;
		double weight = 0;
		for (i=0;i<Num;i++)
		{
			if (pWeight[i].PName.equals(Name))
			{
				weight = pWeight[i].weight;
				break;
			}
		}
		return weight;
	}

	public double FindInstanceyWeight(String Name, int Num)
	{
		int i;
		double weight = 0;
		for (i=0;i<Num;i++)
		{
			if (iWeight[i].IName.equals(Name))
			{
				weight = iWeight[i].weight;
				break;
			}
		}
		return weight;
	}
	
	public double FindMetaWeight(String Name, int Num)
	{
		int i;
		double weight = 0;
		for (i=0;i<Num;i++)
		{
			if (metaWeight[i].MName.equals(Name))
			{
				weight = metaWeight[i].weight;
				break;
			}
		}
		return weight;
	}

	public void ComputeRawCurrent(int Num)
	{
		int i,j;
		
		for (i = 0; i < Num; i++) {
			for (j = 0; j < Num; j++) {
				rawCurrent[i][j] = conductanceMatrix[i][j]
						* (voltageMatrix[i][0] - voltageMatrix[j][0]);
			}
		}
	}

	public void ComputeCurrentOut(int Num)
	{
		int i,j;
		
		for (i=0;i<Num;i++)
		{
			currentOut[i] = 0;
			for (j=0;j<Num;j++)
			{
				if (rawCurrent[i][j]>0) currentOut[i] +=  rawCurrent[i][j];
			}
//			System.out.println("currentOut["+i+"]"+currentOut[i]);
		}
	}

	public DirectedGraph displayGraphGeneration(int VertexNum, String[] VertexName, int P)
	{
		int i,k;
		int k1;
		int v;
		int MAX_TABLE_ROW=VertexNum+10;
		int MAX_TABLE_COLADD=P;
		int[] order=new int[MAX_TABLE_ROW];
		DirectedGraph gDisp;
		double maxpath = 0;
		int max_v = 0,max_k = 0;
		int iter_v = 0, iter_k = 0;
		int[][] updateMatrix;
		int count = 0;
		int k_temp = 0;
		int temp_v = 0;
		String [] pathList;
		boolean AllVertex = false;
		int trueNode = 0;
		
//		P=P+1;//sink node���㣬��������Ҫ��1
		
		//sort the vertex
		order = SortVertexByVoltage(VertexNum);
		
		//Initialize output graph
		gDisp = new DefaultDirectedGraph(DefaultEdge.class);
		
		pathList = new String[P];
		
		//Initialize Dvk
		Dvk = new DeliveryTable[MAX_TABLE_ROW][P+MAX_TABLE_COLADD];
		
		updateMatrix = new int [MAX_TABLE_ROW][P+MAX_TABLE_COLADD];

		//While output graph is not big enough
		
		//Initinalize the table
		for (i=0;i<VertexNum;i++)
		{
			for (k=0;k<=P;k++)
			{
				Dvk[i][k] = new DeliveryTable();
			}
		}
			
			for (i=0;i<VertexNum;i++)
			{
				//Let v=ui
				v = i;
				for (k=1;k<=P;k++)
				{
					if (gDisp.containsVertex(VertexName[order[v]]))
					{
						k1 = k;
					}
					else
					{
						k1 = k-1;
					}
					
					if (v==0)
					{
						Dvk[v][k].deliveryCurrent = -1.0;
						continue;
					}

					//compute the Dvk
					//deal with v=s and dvk(s,1)
						
						int[] myint = new int[1];
						myint[0] = -1;
						Dvk[v][k].deliveryCurrent  = GetMaxDvk(v,k1,order,myint);
						Dvk[v][k].predV = myint[0];
						Dvk[v][k].predK = k1;
					
						//System.out.println("Dvk["+v+"]["+k+"]:"+Dvk[v][k].deliveryCurrent);
				}
				
			}
			
		while((trueNode<P) && (AllVertex==false))
		{

			AllVertex = true;
			
			//When a iterate is complete, update the D(v,k)
			//compute the inserted vertexs along the path
			for (i=0;i<VertexNum;i++)
				for (k=0;k<=P;k++)
				{
					if (Dvk[i][k].deliveryCurrent>0)
					{
						updateMatrix[i][k]=k;
					}
					else
					{
						updateMatrix[i][k]=0;
					}
				}
			
			
			//compute new k num for each Dvk
			for (i=0;i<VertexNum;i++)
			{
				
				for (k=0;k<=P;k++)
				{
					count = 0;
					k_temp = k;
					v = i;
					while (Dvk[v][k_temp].deliveryCurrent>0)
					{
						if (gDisp.containsVertex(VertexName[order[v]]))
						{
							count++;
						}
						temp_v = v;
						v = Dvk[v][k_temp].predV;
						k_temp = Dvk[temp_v][k_temp].predK;
						if (v == 0 && gDisp.containsVertex(VertexName[order[v]]))
						{
							count++;
							break;
						}
					}
					if (updateMatrix[i][k]>0)
					{
						
						updateMatrix[i][k]=k-count;
					}
				}
			}
				
			
			//find the max Dvk/k
			maxpath = 0;
			for (i=0;i<VertexNum;i++)
				for (k=0;k<=P;k++)
				{
					if (updateMatrix[i][k]>0)
					{
						AllVertex = false;
						if (Dvk[i][k].deliveryCurrent/(double)updateMatrix[i][k] > maxpath)
						{
							maxpath = Dvk[i][k].deliveryCurrent/(double)updateMatrix[i][k];
							max_v = i;
							max_k = k;
						}
					}
				}

					
			//Add the path maximizing D(t,k)/k
			iter_v = max_v;
			iter_k = max_k;
			count = 0;
//			System.out.println("maxpath delivery current:"+maxpath);
			
			while ((Dvk[iter_v][iter_k].deliveryCurrent>0.0))
			{
//				System.out.print(sourceVertexName[order[iter_v]]+"-->");
				if ((!ontParse.isBlankNode(VertexName[order[iter_v]]))
					 && (!gDisp.containsVertex(VertexName[order[iter_v]]))
					 && (VertexName[order[iter_v]].indexOf("Statement_")==-1))
					{
						trueNode++;
					}
				gDisp.addVertex(VertexName[order[iter_v]]);

				pathList[count] = VertexName[order[iter_v]];
				count++;
				iter_v = Dvk[iter_v][iter_k].predV;
				iter_k = Dvk[iter_v][iter_k].predK;
				if (iter_v == 0)
				{
//					System.out.println(sourceVertexName[order[iter_v]]);
					if (!ontParse.isBlankNode(VertexName[order[iter_v]])
							&& !gDisp.containsVertex(VertexName[order[iter_v]]))
						{
							trueNode++;
						}
					gDisp.addVertex(VertexName[order[iter_v]]);
		
					pathList[count] = VertexName[order[iter_v]];
					count++;
					break;
				}
			}
//			System.out.println();
			
			for (i=count-1;i>0;i--)
			{
				/*�����ǰҪ�жϱߵķ����Ƿ���ȷ*/
				/*�����븨����sink node*/
				if (!pathList[i].equals("pwang2007") && !pathList[i-1].equals("pwang2007")){
					if(orgnGraph.containsEdge(pathList[i], pathList[i-1])){
						gDisp.addEdge(pathList[i], pathList[i-1]);
					}
					else if(orgnGraph.containsEdge(pathList[i-1], pathList[i])){
						gDisp.addEdge(pathList[i-1], pathList[i]);
					}	
				}			
			}
		}
		/*ȥ��sink node*/
		if (gDisp.containsVertex("pwang2007")){
			gDisp.removeVertex("pwang2007");
		}
		return gDisp;
	}

	/*
	 * sort the vertex by voltage
	 * the order[] records the result
	 */
	public int[] SortVertexByVoltage(int Num)
	{
		int id =0;
		int[] order = new int[Num];
		double[] mirror=new double[Num];
		boolean[] flag = new boolean[Num];
		
		for (int i=0;i<Num;i++)
		{
			order[i] = i;
			mirror[i]=voltageMatrix[i][0];
			flag[i] = false;
		}
		
		Arrays.sort(mirror);
				
		for (int i = 0; i < Num; i++) 
		{
			id = 0;
			for (int j = 0; j < Num; j++) 
			{
				if (!flag[j]&&(voltageMatrix[j][0]==mirror[Num-i-1]))
				{
					id = j;
					break;
				}
			}
			order[i] = id;
			flag[id] = true;
		} 
		return order;
	}

	/**********************
	 * ������֤����ı���,ÿ�δ����triple����ͬ
	 ********************/
	public void sortGraphTriples(Statement[] trp)
	{
		StmComparator comp = new StmComparator();
		Arrays.sort(trp,comp);
	}

	public double GetMaxDvk(int v, int k1, int[] order, int[] get_u)
	{
		int i,j;
		int u = 0;
		double maxdc = -1.0;
		double dxv = 0;
		
		get_u[0] = -1;
		for (i=0;i<v;i++)
		{
			u = i;
			if (rawCurrent[order[u]][order[v]]>0)
			{
				if (u==0 && v>0 && k1==1) 
				{
					dxv = rawCurrent[order[u]][order[v]];
				}
				else
				{
					if (Dvk[u][k1].deliveryCurrent > 0.0)
					{
						dxv = Dvk[u][k1].deliveryCurrent 
					      * rawCurrent[order[u]][order[v]]/currentOut[order[u]];
					}
					else
						
					{
						dxv = -1.0;
					}
				}
			}
			if (dxv>maxdc)
			{
				maxdc = dxv;
				get_u[0] = u;
			}
		}
		return maxdc;
	}

	public void ComputeSGMapping_Informative(ConceptSubGraph SG_source[], ConceptSubGraph SG_target[])
	{
		//MaxGraphIsoCoveringValiente match = new MaxGraphIsoCoveringValiente(null, null);
		int i,j;


		int sourceSize, targetSize, commonSize;
		
		for (i=0;i<source_ConceptNum;i++)
		{
				sourceSize = 0;
				targetSize = 0;
				if (cnptSubG[i] != null) {

					sourceSize = cnptSubG[i].subGraph.vertexSet().size();
					for (j = 0; j < target_ConceptNum; j++) {
						SimMatrixGraph[i][j] = 0;
						if (targetSubGraph[j] != null) {

							targetSize = targetSubGraph[j].subGraph.vertexSet().size();

							System.out.println("source:"+source_ConceptName[i]+cnptSubG[i].subGraph.toString());
							System.out.println("target:"+target_ConceptName[j]+targetSubGraph[j].subGraph.toString());
							System.out.println("sourceSubGraph[" + i + "]"
									+ "targetSubGraph[" + j + "]");
							
							long start = System.currentTimeMillis();//��ʼ��ʱ 

							System.out.println("�˴νṹƥ���㷨ʱ�䣺"+(double)(System.currentTimeMillis()-start)/1000.+"��");

							SimMatrixGraph[i][j] = GetBlondeSimilarity(source_ConceptName[i], 
									                                   target_ConceptName[j], 
									                                   cnptSubG[i].subGraph, 
									                                   targetSubGraph[j].subGraph);
//							SimMatrixGraph[i][j] = (float)commonSize/(float)(sourceSize+targetSize-commonSize);
							System.out.println("SimMatrixGraph["+i+"]["+j+"]"+SimMatrixGraph[i][j]+"\n");
						}
					}
				}
			}
		

//		SimMatrixGraph = this.RemoveSmallSim( SimMatrixGraph,source_ConceptNum,target_ConceptNum);
		//try mutual information method
//		SimMatrixGraph = this.GetMutualInformation(SimMatrixGraph,source_ConceptNum,target_ConceptNum);
		
//		BlondelMatrixSimAlgorithm BMSim = new BlondelMatrixSimAlgorithm();
//		SimMatrixGraph = BMSim.GetSelectMatching(SimMatrixGraph,source_ConceptNum, target_ConceptNum);
		(new StableMarriageFilter()).run(SimMatrixGraph,source_ConceptNum,target_ConceptNum);
		
		
		System.out.println("done");
	}

	public double GetBlondeSimilarity(String iName, String jName, DirectedGraph soureG, DirectedGraph targetG)
	{
		int i,j,count;
		Iterator itr;
		int nA,nB;
		int sourcePos, targetPos;
		double sim;
		String[] SName = new String[MAX_DISPSUBGRAPH_SIZE+120];
		String[] TName = new String[MAX_DISPSUBGRAPH_SIZE+120];
		
		BlondelMatrixSimAlgorithm BMSim = new BlondelMatrixSimAlgorithm();
		
		//initinalize the basic params
		nA = soureG.vertexSet().size();
		nB = targetG.vertexSet().size();
		
		//get source subgraph vertex names
		itr = soureG.vertexSet().iterator();
	    count = 0;
	    sourcePos = 0;
	    while (itr.hasNext())
	    {
	    	SName[count] = itr.next().toString();
	    	//get iPos
	    	if (SName[count].equals(iName)) {sourcePos=count;}
	    	count++;
	    }
	    
		//get target subgraph vertex names
		itr = targetG.vertexSet().iterator();
	    count = 0;
	    targetPos = 0;
	    while (itr.hasNext())
	    {
	    	TName[count] = itr.next().toString();
	    	//get jPos
	    	if (TName[count].equals(jName)) {targetPos=count;}
	    	count++;
	    }
	    
		//initinalize arrayA
		double[][] arrayA = new double[nA][nA];
		for (i=0;i<nA;i++)
			for (j=0;j<nA;j++)
			{
				if (soureG.containsEdge(SName[i],SName[j]))
				{
					arrayA[i][j] = 1.0;
				}
				else
				{
					arrayA[i][j] = 0.0;
				}
			}
		
		//initinalize arrayB
		double[][] arrayB = new double[nB][nB];
		for (i=0;i<nB;i++)
			for (j=0;j<nB;j++)
			{
				if (targetG.containsEdge(TName[i],TName[j]))
				{
					arrayB[i][j] = 1.0;
				}
				else
				{
					arrayB[i][j] = 0.0;
				}
			}
		
		//initinalize similarity matrix
		double [][] SimMatrix = new double [nB][nA];
		
		SimMatrix = BMSim.solve(nA, nB, arrayA, arrayB, MAX_DISPSUBGRAPH_ITER_TIMES);
//		SimMatrix = TurnSim(SimMatrix,nB,nA);
		
//		SimMatrix = this.RemoveSmallSim( SimMatrix,nB,nA);
//		SimMatrix = this.RefineSim( SimMatrix,nB,nA);
		
		//try mutual information method
//		SimMatrix = this.GetMutualInformation(SimMatrix,nB,nA);
//		SimMatrix = BMSim.GetSelectMatchingRaw(SimMatrix,nB,nA);
//		SimMatrix = BMSim.GetSelectMatching(SimMatrix,nB,nA);
		
//		SimMatrix = this.RemoveSmallSim(SimMatrix,nB,nA);
//		SimMatrix = this.TurnSim(SimMatrix,nB,nA);
//		(new SMSelector()).run(SimMatrix,nB,nA);

		

		
		sim = SimMatrix[targetPos][sourcePos];
		
//		sim = 1.0/(1.0+Math.exp(-10.0*sim));
	                     
		return sim;
	}

	public void ComputeSGMapping_WholeGraph()
	{
		int i,j,k;
		double[][] sim = new double[1000][1000];
		int iPos=0, jPos=0;
		double max=0;
		
		sim = GetBlondeSimilarity_WholeGraph(source_Graph,target_Graph);
		
		for (i=0;i<source_ConceptNum;i++)
		{
			for(k=0;k<sourceVertexNum;k++)
			{
				if (sourceVertexName[k].equals(source_ConceptName[i]))
				{
					iPos = k;
					break;
				}
			}
			
			for (j = 0; j < target_ConceptNum; j++) {
				for(k=0;k<targetVertexNum;k++)
				{
					if (targetVertexName[k].equals(target_ConceptName[j]))
					{
						jPos = k;
						break;
					}
				}
				
				SimMatrixGraph[i][j]=sim[jPos][iPos];
//				SimMatrixGraph[i][j]=1.0/(1.0+Math.exp(-1.0*sim[jPos][iPos] ));
						}
					}
		
		//remove too small similarity values
//		SimMatrixGraph = this.RemoveSmallSim( SimMatrixGraph,source_ConceptNum,target_ConceptNum);
		//try mutual information method
//		SimMatrixGraph = this.GetMuturalInformation(SimMatrixGraph,source_ConceptNum,target_ConceptNum);
		
		System.out.println("Begin selecting");
		long start = System.currentTimeMillis();//��ʼ��ʱ 
		BlondelMatrixSimAlgorithm BMSim = new BlondelMatrixSimAlgorithm();
//		SimMatrixGraph = BMSim.GetSelectMatching(SimMatrixGraph,source_ConceptNum,target_ConceptNum);
//		BMSim.GetSelectMatching2(SimMatrixGraph,source_ConceptNum,target_ConceptNum);
		(new StableMarriageFilter()).run(SimMatrixGraph,source_ConceptNum,target_ConceptNum);
		long end = System.currentTimeMillis();//������ʱ
		long costtime = end - start;//ͳ���㷨ʱ��
		System.out.println("ѡ����ʱ�䣺"+(double)costtime/1000.+"��");
		System.out.println("End selecting"); 

	}

	public void ComputeSGMapping_BiptGraph()
	{
		int i,j,k;
		double[][] sim = new double[1000][1000];
		int iPos=0, jPos=0;
		double max=0;
		
//		sim = GetBlondeSimilarity_WholeGraph_Block(source_Graph,target_Graph);
//		sim = GetBlondeSimilarity_WholeGraph(source_Graph,target_Graph);
		SimMatrixGraph = this.GetBlondeSimilarity_WholeGraph_GMOBlock(source_Graph,target_Graph);

		System.out.println("Begin selecting");
		long start = System.currentTimeMillis();//��ʼ��ʱ 
		//�ϵ�ѡ�񷽷�
//		BlondelMatrixSimAlgorithm BMSim = new BlondelMatrixSimAlgorithm();
//		SimMatrixGraph = BMSim.GetSelectMatching(SimMatrixGraph,source_ConceptNum,target_ConceptNum);
		
		//�µ�ѡ�񷽷���Stable Marriage����
		(new StableMarriageFilter()).run(SimMatrixGraph,source_ConceptNum,target_ConceptNum);
		
		
		long end = System.currentTimeMillis();//������ʱ
		long costtime = end - start;//ͳ���㷨ʱ��
		System.out.println("ѡ����ʱ�䣺"+(double)costtime/1000.+"��");
		System.out.println("End selecting"); 
	}

	public void ComputeSGMapping_WholeGraph_Block()
	{
		int i,j,k;
		double[][] sim = new double[1000][1000];
		int iPos=0, jPos=0;
		
		sim = GetBlondeSimilarity_WholeGraph_Block(source_Graph,target_Graph);
		
		for (i=0;i<source_ConceptNum;i++)
		{
			for(k=0;k<sourceVertexNum;k++)
			{
				if (sourceVertexName[k].equals(source_ConceptName[i]))
				{
					iPos = k;
					break;
				}
			}
			
			for (j = 0; j < target_ConceptNum; j++) {
				for(k=0;k<targetVertexNum;k++)
				{
					if (targetVertexName[k].equals(target_ConceptName[j]))
					{
						jPos = k;
						break;
					}
				}
				
				SimMatrixGraph[i][j]=sim[jPos][iPos];
				
//				if (SimMatrixGraph[i][j]>max) {max=SimMatrixGraph[i][j];}
				
//				System.out.println("source:"+source_ConceptName[i]+"---"+sourceVertexName[iPos]);
//				System.out.println("target:"+target_ConceptName[j]+"---"+targetVertexName[jPos]);
										
//							long start = System.currentTimeMillis();//��ʼ��ʱ 
	//
//							System.out.println("�˴νṹƥ���㷨ʱ�䣺"+(double)(System.currentTimeMillis()-start)/1000.+"��");

							
//							SimMatrixGraph[i][j] = (float)commonSize/(float)(sourceSize+targetSize-commonSize);
//							System.out.println("SimMatrixGraph["+i+"]["+j+"]"+SimMatrixGraph[i][j]+"\n");
						}
					}
		
		//try mutual information method
//		SimMatrixGraph = this.GetMuturalInformation(SimMatrixGraph,source_ConceptNum,target_ConceptNum);
		
		System.out.println("Begin selecting");
		long start = System.currentTimeMillis();//��ʼ��ʱ 
		BlondelMatrixSimAlgorithm BMSim = new BlondelMatrixSimAlgorithm();
//		SimMatrixGraph = BMSim.GetSelectMatching(SimMatrixGraph,source_ConceptNum,target_ConceptNum);
//		BMSim.GetSelectMatching2(SimMatrixGraph,source_ConceptNum,target_ConceptNum);
		(new StableMarriageFilter()).run(SimMatrixGraph,source_ConceptNum,target_ConceptNum);
		long end = System.currentTimeMillis();//������ʱ
		long costtime = end - start;//ͳ���㷨ʱ��
		System.out.println("ѡ����ʱ�䣺"+(double)costtime/1000.+"��");
		System.out.println("End selecting"); 

	}


	public double[][] GetBlondeSimilarity_WholeGraph(DirectedGraph soureG, DirectedGraph targetG)
	{
		int i,j,count;
		Iterator itr;
		int nA,nB;
		String[] SName = new String[5000];
		String[] TName = new String[5000];
		
		BlondelMatrixSimAlgorithm BMSim = new BlondelMatrixSimAlgorithm();
		
		//initinalize the basic params
		nA = soureG.vertexSet().size();
		nB = targetG.vertexSet().size();
		
		//get source subgraph vertex names
		itr = soureG.vertexSet().iterator();
	    count = 0;
	    while (itr.hasNext())
	    {
	    	SName[count] = itr.next().toString();
	    	count++;
	    }
	    
		//get target subgraph vertex names
		itr = targetG.vertexSet().iterator();
	    count = 0;
	    while (itr.hasNext())
	    {
	    	TName[count] = itr.next().toString();
	    	count++;
	    }
	    
		//initinalize arrayA
		double[][] arrayA = new double[nA][nA];
		for (i=0;i<nA;i++)
			for (j=0;j<nA;j++)
			{
				if (soureG.containsEdge(SName[i],SName[j]))
				{
					arrayA[i][j] = 1.0;
				}
				else
				{
					arrayA[i][j] = 0.0;
				}
			}
		
		//initinalize arrayB
		double[][] arrayB = new double[nB][nB];
		for (i=0;i<nB;i++)
			for (j=0;j<nB;j++)
			{
				if (targetG.containsEdge(TName[i],TName[j]))
				{
					arrayB[i][j] = 1.0;
				}
				else
				{
					arrayB[i][j] = 0.0;
				}
			}
		
		//initinalize similarity matrix
		double [][] SimMatrix = new double [nB][nA];
		
		SimMatrix = BMSim.solve(nA, nB, arrayA, arrayB, MAX_DISPSUBGRAPH_ITER_TIMES);
		
		//mutural information
//		SimMatrix = this.GetMuturalInformation( SimMatrix,nB,nA);
		
//		SimMatrix = TurnSim(SimMatrix,nB,nA);
//		SimMatrix = BMSim.GetSelectMatching(SimMatrix,nB,nA);
	    
		return SimMatrix;
	}

	public double[][] GetBlondeSimilarity_WholeGraph_Block(DirectedGraph soureG, DirectedGraph targetG)
	{
		int i,j,k, count;
		Iterator itr;
		int nA,nB;
		String[] SName = new String[5000];
		String[] TName = new String[5000];
		Set sourceCPset = new HashSet();
		Set targetCPset = new HashSet();
		
		BlondelMatrixSimAlgorithm BMSim = new BlondelMatrixSimAlgorithm();
		
		//initinalize the basic params
		nA = soureG.vertexSet().size();
		nB = targetG.vertexSet().size();
		
		//get source subgraph vertex names
		itr = soureG.vertexSet().iterator();
	    count = 0;
	    while (itr.hasNext())
	    {
	    	SName[count] = itr.next().toString();
	    	count++;
	    }
	    
		//get target subgraph vertex names
		itr = targetG.vertexSet().iterator();
	    count = 0;
	    while (itr.hasNext())
	    {
	    	TName[count] = itr.next().toString();
	    	count++;
	    }
	    
		//construct the set position of concepts
		for (i=0;i<source_ConceptNum;i++)
		{
			for(k=0;k<nA;k++)
			{
				if (SName[k].equals(source_ConceptName[i]))
				{
					sourceCPset.add(k);
					break;
				}
			}
		}
		
		for (i=0;i<target_ConceptNum;i++)
		{
			for(k=0;k<nB;k++)
			{
				if (TName[k].equals(target_ConceptName[i]))
				{
					targetCPset.add(k);
					break;
				}
			}
		}
	    
		//initinalize arrayA
		double[][] arrayA = new double[nA][nA];
		for (i=0;i<nA;i++)
			for (j=0;j<nA;j++)
			{
				if (soureG.containsEdge(SName[i],SName[j]))
				{
					arrayA[i][j] = 1.0;
				}
				else
				{
					arrayA[i][j] = 0.0;
				}
			}
		
		//initinalize arrayB
		double[][] arrayB = new double[nB][nB];
		for (i=0;i<nB;i++)
			for (j=0;j<nB;j++)
			{
				if (targetG.containsEdge(TName[i],TName[j]))
				{
					arrayB[i][j] = 1.0;
				}
				else
				{
					arrayB[i][j] = 0.0;
				}
			}
		
		//initinalize similarity matrix
		double [][] SimMatrix = new double [nB][nA];
		
		SimMatrix = BMSim.solve_block(nA, nB, arrayA, arrayB, sourceCPset, targetCPset, MAX_DISPSUBGRAPH_ITER_TIMES);
//		SimMatrix = TurnSim(SimMatrix,nB,nA);
		
	    
		return SimMatrix;
	}

	public double[][] GetBlondeSimilarity_WholeGraph_GMOBlock(DirectedGraph sourceG, DirectedGraph targetG)
	{
		int i,j,k, count;
		String str1,str2;
		int nA,nB;//����ͼ�е�Vertex����
		int nExA,nOtA,nStA;//A������Vertex��Ŀ
		int	nExB,nOtB,nStB;//B������Vertex��Ŀ
		BlondelMatrixSimAlgorithm BMSim = new BlondelMatrixSimAlgorithm();
		
		//***************��Source Graph,��������ֿ����*********************

		//�������ĵ���
		nExA = sourceExset.size();
		nOtA = sourceOtset.size();
		nStA = sourceStset.size();
		
		//********1.����Ex--St-->AEs
		//��ʼ������AEs
		double[][] AEs = new double [nExA][nStA];
		i = 0;
		for (Iterator Iti = sourceExset.iterator();Iti.hasNext();)
		{
			str1 = (String) Iti.next();
			j = 0;
			for (Iterator Itj = sourceStset.iterator();Itj.hasNext();)
			{
				str2 = (String) Itj.next();
				if (sourceG.containsEdge(str1,str2))
				{
					AEs[i][j] = 1.0;
				}
				else
				{
					AEs[i][j] = 0.0;
				}
				j++;
			}
			i++;
		}
		
		//********2.����Ot--St-->As
		//��ʼ������As
		double[][] As = new double [nOtA][nStA];
		i = 0;
		for (Iterator Iti = sourceOtset.iterator();Iti.hasNext();)
		{
			str1 = (String) Iti.next();
			j = 0;
			for (Iterator Itj = sourceStset.iterator();Itj.hasNext();)
			{
				str2 = (String) Itj.next();
				if (sourceG.containsEdge(str1,str2))
				{
					As[i][j] = 1.0;
				}
				else
				{
					As[i][j] = 0.0;
				}
				j++;
			}
			i++;
		}
		
		//********3.����St--Ex-->AE
		//��ʼ������AE
		double[][] AE = new double [nStA][nExA];
		i = 0;
		for (Iterator Iti = sourceStset.iterator();Iti.hasNext();)
		{
			str1 = (String) Iti.next();
			j = 0;
			for (Iterator Itj = sourceExset.iterator();Itj.hasNext();)
			{
				str2 = (String) Itj.next();
				if (sourceG.containsEdge(str1,str2))
				{
					AE[i][j] = 1.0;
				}
				else
				{
					AE[i][j] = 0.0;
				}
				j++;
			}
			i++;
		}
		
		//********4.����St--Ot-->Aop
		//��ʼ������Aop
		double[][] Aop = new double [nStA][nOtA];
		i = 0;
		for (Iterator Iti = sourceStset.iterator();Iti.hasNext();)
		{
			str1 = (String) Iti.next();
			j = 0;
			for (Iterator Itj = sourceOtset.iterator();Itj.hasNext();)
			{
				str2 = (String) Itj.next();
				if (sourceG.containsEdge(str1,str2))
				{
					Aop[i][j] = 1.0;
				}
				else
				{
					Aop[i][j] = 0.0;
				}
				j++;
			}
			i++;
		}
		
		System.out.println("A�㼯�������");
		
		//***************��Target Graph,��������ֿ����*********************

		//�������ĵ���
		nExB = targetExset.size();
		nOtB = targetOtset.size();
		nStB = targetStset.size();
		
		//********1.����Ex--St-->BEs
		//��ʼ������BEs
		double[][] BEs = new double [nExB][nStB];
		i = 0;
		for (Iterator Iti = targetExset.iterator();Iti.hasNext();)
		{
			str1 = (String) Iti.next();
			j = 0;
			for (Iterator Itj = targetStset.iterator();Itj.hasNext();)
			{
				str2 = (String) Itj.next();
				if (targetG.containsEdge(str1,str2))
				{
					BEs[i][j] = 1.0;
				}
				else
				{
					BEs[i][j] = 0.0;
				}
				j++;
			}
			i++;
		}
		
		//********2.����Ot--St-->Bs
		//��ʼ������Bs
		double[][] Bs = new double [nOtB][nStB];
		i = 0;
		for (Iterator Iti = targetOtset.iterator();Iti.hasNext();)
		{
			str1 = (String) Iti.next();
			j = 0;
			for (Iterator Itj = targetStset.iterator();Itj.hasNext();)
			{
				str2 = (String) Itj.next();
				if (targetG.containsEdge(str1,str2))
				{
					Bs[i][j] = 1.0;
				}
				else
				{
					Bs[i][j] = 0.0;
				}
				j++;
			}
			i++;
		}
		
		//********3.����St--Ex-->BE
		//��ʼ������BE
		double[][] BE = new double [nStB][nExB];
		i = 0;
		for (Iterator Iti = targetStset.iterator();Iti.hasNext();)
		{
			str1 = (String) Iti.next();
			j = 0;
			for (Iterator Itj = targetExset.iterator();Itj.hasNext();)
			{
				str2 = (String) Itj.next();
				if (targetG.containsEdge(str1,str2))
				{
					BE[i][j] = 1.0;
				}
				else
				{
					BE[i][j] = 0.0;
				}
				j++;
			}
			i++;
		}
		
		//********4.����St--Ot-->Bop
		//��ʼ������Bop
		double[][] Bop = new double [nStB][nOtB];
		i = 0;
		for (Iterator Iti = targetStset.iterator();Iti.hasNext();)
		{
			str1 = (String) Iti.next();
			j = 0;
			for (Iterator Itj = targetOtset.iterator();Itj.hasNext();)
			{
				str2 = (String) Itj.next();
				if (targetG.containsEdge(str1,str2))
				{
					Bop[i][j] = 1.0;
				}
				else
				{
					Bop[i][j] = 0.0;
				}
				j++;
			}
			i++;
		}
		
		System.out.println("B�㼯�������");
		
		//*******�������EBA*************
		double[][] EBA = new double[nExB][nExA];
		i = 0;
		for (Iterator Iti = targetExset.iterator();Iti.hasNext();)
		{
			str1 = (String) Iti.next();
			j = 0;
			for (Iterator Itj = targetExset.iterator();Itj.hasNext();)
			{
				str2 = (String) Itj.next();
				if (str1.equals(str2))
				{
					EBA[i][j] = 1.0;
				}
				else
				{
					EBA[i][j] = 0.0;
				}
				j++;
			}
			i++;
		}
		
		
		//initinalize similarity matrix
		double[][] sim = new double [nStB][nStA];
		
		sim = BMSim.solve_GMO(nExA,nOtA,nStA,AEs,As,AE,Aop,nExB,nOtB,nStB,BEs,Bs,BE,Bop, EBA, MAX_DISPSUBGRAPH_ITER_TIMES);
		
		//ת��Ϊconcept--concept�����ƾ���
		double[][] simMatrix = new double[source_ConceptNum][target_ConceptNum];
		int iPos=0,jPos=0;
		for (i=0;i<source_ConceptNum;i++)
		{
			k=0;
			iPos = 0;
			for (Iterator Itj = sourceOtset.iterator();Itj.hasNext();)
			{
				str1 = (String) Itj.next();
				if (str1.equals(source_ConceptName[i]))
				{
					iPos = k;
					break;
				}

				k++;
			}
		
			
			for (j = 0; j < target_ConceptNum; j++) {
					
				k=0;
				jPos = 0;
				for (Iterator Itj = targetOtset.iterator();Itj.hasNext();)
				{
					str1 = (String) Itj.next();
					if (str1.equals(target_ConceptName[j]))
					{
						jPos = k;
						break;
					}

					k++;
				}
				simMatrix[i][j] = sim[jPos][iPos];
			}
		}
		return simMatrix;
	}

	public double[][] TurnSim(double[][] sim, int nA, int nB)
	{
		int i,j;
		int num = 0;
		double sum = 0;
		double avg = 0;
		double max = 0;
		
		for (i=0;i<nA;i++)
		{
			for (j=0;j<nB;j++)
			{
				max = Math.max(max,sim[i][j]);
				if (sim[i][j]>0) 
				{
					num++;
					sum += sim[i][j];
				}
			}
		}
		
		if (num>0)
		{
			avg = sum/num;
		}
		
		for (i=0;i<nA;i++)
		{
			for (j=0;j<nB;j++)
			{
				if (sim[i][j]>0)
				{
					sim[i][j]=sim[i][j]*(avg/max)*((double)num/(double)(nA+nB-num));
//					sim[i][j]=sim[i][j]*((double)num/(double)(nA+nB-num));
				}
			}
		}
		
		return sim;
	}

//	remove the too small value in the similarity matrix
	public double[][] RemoveSmallSim(double[][] sim, int nA, int nB)
	{
		int i,j;
		double max = 0;
		double pera = 1/(double)(Math.max(nA,nB)*Math.max(nA,nB));
		double threshold;
		
		for (i=0;i<nA;i++)
		{
			for (j=0;j<nB;j++)
			{
				if (sim[i][j]>max) {max = sim[i][j];}
			}
		}
		
		threshold = max * pera;
		
		for (i=0;i<nA;i++)
		{
			for (j=0;j<nB;j++)
			{
				if (sim[i][j]<threshold) 
				{
					sim[i][j] = 0;
				}
			}
		}
		return sim;
	}

	public double[][] GetMutualInformation(double[][] sim, int nA, int nB)
	{
		int i,j,k;
		double px,py;
		double[][] tempsim = new double[nA][nB];

		for (i=0;i<nA;i++)
		{
			px = 0;
			for (k=0;k<nB;k++) px+=sim[i][k];
			for (j = 0; j<nB; j++)
			{
				py = 0;
				for (k=0;k<nA;k++) py+=sim[k][j];
				if (px*py*sim[i][j] == 0)
				{
					tempsim[i][j] = 0;
				}
				else
				{
					tempsim[i][j] = sim[i][j]*Math.log((sim[i][j])/(px*py));

					tempsim[i][j] = 1/(1+Math.exp(-5.0*tempsim[i][j]));
				}
			}
		}
		return tempsim;
	}

	public double[][] RefineSim(double[][] sim, int nA, int nB)
	{
		int i,j,k;
		double[] px = new double[nA];
		double[] py = new double[nB];
		int[] nx = new int[nA];
		int[] ny = new int[nB];
		double[][] tempsim = new double[nA][nB];
		
		for (i=0;i<nA;i++)
		{
			for (j=0;j<nB;j++)
			{
				px[i] = px[i]+sim[i][j];
				if (sim[i][j]>0) {nx[i]=nx[i]+1;}
			}
		}
		
		for (j=0;j<nB;j++)
		{
			for (i=0;i<nA;i++)
			{
				py[j] = py[j]+sim[i][j];
				if (sim[i][j]>0) {ny[j]=ny[j]+1;}
			}
		}
		
		for (i=0;i<nA;i++)
		{
			for (j=0;j<nB;j++)
			{
				if (sim[i][j]>0)
				{
					tempsim[i][j] = sim[i][j]+(sim[i][j]-px[i]/nB)+(sim[i][j]-py[j]/nA);
					tempsim[i][j] = 0.5*(tempsim[i][j]*((double)(nB-nx[i]+1.0)/(double)nB)+tempsim[i][j]*((double)(nA-ny[j]+1)/(double)nA));
				}
				else
				{
					tempsim[i][j] = 0;
				}
			}
		}
		
		return tempsim;
	}
	
	/**********************
	 * �ع�����Informative graph�������
	 ********************/
	public ArrayList consInfSubOnt(ArrayList paraList)
	{
		ArrayList result=new ArrayList();
		
		//1.��������
		unPackOntPara(paraList);

		//2.����Ԥ����
//		System.out.println("����Ԥ����");
		preProcessOnt();
		//3.�ع�����ɷֵ�informativ graph
		//(1)ͼģ�͵Ľ���
//		System.out.println("ͼģ�͵Ľ���");
		orgnGraph=ont2Graph();
		//(2)����������ͼ�ĳ�ȡ
//		System.out.println("����������ͼ�ĳ�ȡ");
		reConsCnptSubOnt();
		//(3)����������ͼ�ĳ�ȡ
//		System.out.println("����������ͼ�ĳ�ȡ");
		reConsPropSubOnt();
		
		result.add(0,cnptSubG);
		result.add(1,propSubG);
		result.add(2,m);
		result.add(3,new ArrayList(Arrays.asList(graphTrp)));//����ͼ����Ԫ���ʾ
		
		return result;
	}
	
	/**********************
	 * ���ձ������
	 * ��ʼ��OntGraph
	 ********************/
	public void unPackOntPara(ArrayList paraList)
	{
		m=(OntModel)paraList.get(0);
		conceptNum=((Integer)paraList.get(1)).intValue();
		propNum=((Integer)paraList.get(2)).intValue();
		dataPropNum=((Integer)paraList.get(3)).intValue();
		objPropNum=((Integer)paraList.get(4)).intValue();
		insNum=((Integer)paraList.get(5)).intValue();
		
		fullConceptNum=((Integer)paraList.get(12)).intValue();
		fullPropNum=((Integer)paraList.get(13)).intValue();
		fullDataPropNum=((Integer)paraList.get(14)).intValue();
		fullObjPropNum=((Integer)paraList.get(15)).intValue();
		fullInsNum=((Integer)paraList.get(16)).intValue();
		
		//���ݵõ���number��ʼ����������
		initPara();
		conceptName=(String[])(paraList.get(6));
		propName=(String[])(paraList.get(7));
		dataPropName=(String[])(paraList.get(8));
		objPropName=(String[])(paraList.get(9));
		insName=(String[])(paraList.get(10));
		baseURI=(String)paraList.get(11);
		
		fullConceptName=(OntClass[])(paraList.get(17));
		fullPropName=(OntProperty[])(paraList.get(18));
		fullDataPropName=(DatatypeProperty[])(paraList.get(19));
		fullObjPropName=(ObjectProperty[])(paraList.get(20));
		fullInsName=(Individual[])(paraList.get(21));
		
		anonCnpt=(ArrayList)(paraList.get(22));
		anonProp=(ArrayList)(paraList.get(23));
		anonIns=(ArrayList)(paraList.get(24));
		
		MAX_DISPSUBGRAPH_SIZE=((Integer)(paraList.get(25))).intValue();
	}
	
	/**********************
	 * ��ʼ�������һЩ���ݽṹ
	 ********************/
	public void initPara()
	{
		conceptName=new String[conceptNum];
		propName=new String[propNum];
		dataPropName=new String[dataPropNum];
		objPropName=new String[objPropNum];
		insName=new String[insNum];
		fullConceptName=new OntClass[fullConceptNum];
		fullPropName=new OntProperty[fullPropNum];
		fullDataPropName=new DatatypeProperty[fullDataPropNum];
		fullObjPropName=new ObjectProperty[fullObjPropNum];
		fullInsName=new Individual[fullInsNum];
		
		trpNum=0;
		multiEdgeList=new ArrayList();
		graphME=new ArrayList();
		LARGE_GRAPH=4000;
		
		ontParse=new OWLOntParse();
		//��ñ���Ԫ����Ϣ
		getOntMetaInfo();
		cWeight = new ConceptWt[fullConceptNum];
		pWeight = new PropertyWt[fullPropNum];
		iWeight = new InstanceWt[fullInsNum];
		ontLngMetaNum=ontLngMetaSet.size();
		metaWeight=new OntLngMetaWt[ontLngMetaNum];
		ontLngMetaName=new String[ontLngMetaNum];
		ontLngMetaName=(String[])ontLngMetaSet.toArray(new String[0]);
		//������ͼ
		cnptSubG=new ConceptSubGraph[conceptNum];
		propSubG=new PropertySubGraph[propNum];
	}
	
	/**********************
	 * ����������˵���Ԫ�飬�õ��ܹ��ɻ��ģ�͵���Ԫ��
	 ********************/
	public void trp4MultiGraph(ArrayList oldList)
	{
		int stNum=0;
		
		//ԭListת��Ϊ���飬��������±���б����ͼ�¼
		int oldStNum=oldList.size();
		Statement[] oldSt=(Statement[])oldList.toArray(new Statement[0]);
		
		//��ȡ�������˵㣬�����ж�
		String[] vName=new String[oldStNum];
		for (int i=0;i<oldStNum;i++){
			Resource s=oldSt[i].getSubject();
			Property p=oldSt[i].getPredicate();
			RDFNode o=oldSt[i].getObject();
			//�Ѷ˵���Ϣ���ڸ���������
			vName[i]=s.toString()+o.toString();
		}
		
		//�����ж��Ѿ�������Ķ˵�ļ���
		Set typeSet=new HashSet();
		
		//�����������飬�ж϶��ر߳��ֵ�λ��
		for (int i = 0; i < oldStNum - 1; i++) {
			// ����������ͻ�û�б������
			if (!typeSet.contains(vName[i])) {
				// ��¼�������Ѿ�������
				typeSet.add(vName[i]);
				// �ҵ����е��������ͣ������б��
				boolean isM=false;
				for (int j = i+1; j < oldStNum; j++) {
					if (vName[i].equals(vName[j])) {
						// ��������رߣ����¼����
						// ����multiEdgeList
						multiEdgeList.add(oldSt[j]);
						isM=true;
					}
				}
				/*����Ƕ��رߣ���iҲ����*/
				if (isM){
					multiEdgeList.add(oldSt[i]);
				}
			}
		}
	}
	
	/**********************
	 * �ع�������ӱ���
	 ********************/
	public void reConsCnptSubOnt()
	{
		boolean largeOnt;
		//�ж��Ƿ����
		largeOnt=isLargeOnt();
		//������ԴȨ��
		getResWeight();
		//�������
		if(largeOnt){
			dealLargeOnt();
		}
		//����絼����
		double[][] cMatrix=new double[graphVNum][graphVNum];
		cMatrix=getConductMatrix(graphVNum, orgnGraph, graphVName);
		//�ⷽ�̵õ�������ͼ
		cnptSubG=gainCnptSubGraph(cMatrix);
	}
	
	/**********************
	 * �ع����Ե��ӱ���
	 ********************/
	public void reConsPropSubOnt()
	{
		boolean largeOnt;
		//�ж��Ƿ����
		largeOnt=isLargeOnt();
		//������ԴȨ��
		//������������ʱ����ԴȨ�صļ����ڸ����ӱ����ȡ���Ѿ�����
//		getResWeight();
		//�������
		if(largeOnt){
			dealLargeOnt();
		}
		//����絼����
		double[][] cMatrix=new double[graphVNum][graphVNum];
		cMatrix=getConductMatrix(graphVNum, orgnGraph, graphVName);
		//�ⷽ�̵õ�������ͼ
		propSubG=gainPropSubGraph(cMatrix);
	}
	
	/**********************
	 * �жϵ�ǰ��ͼ�Ƿ����
	 ********************/
	public boolean isLargeOnt()
	{
		//�ߺ͵��Ȩ��
		double a=0.8;
		double b=0.9;
		double scale=a*orgnGraph.edgeSet().size()+
		             b*orgnGraph.vertexSet().size();
		return (scale>LARGE_GRAPH);
	}
	
	/**********************
	 * ����ָ���ĸ��������壬��С�����ģ
	 ********************/
	public void dealLargeOnt()
	{
		
	}
	
	/**********************
	 * ��ñ����Ԫ����Ϣ
	 ********************/
	public void getOntMetaInfo()
	{
		ontLngURI=ontParse.getOntLngURI();
		ontLngMetaSet=ontParse.getOntLngMeta();
	}
	
	/**********************
	 * �ӹ���ͼ����Ԫ����ȷ��P
	 * ��������Ӱ��ϵͳ��Ч��
	 ********************/
	public ArrayList findPFromGraphTriple(String sStr,String oStr)
	{
		ArrayList result=new ArrayList();
		Property pr=null;
		Statement stm=null;
		for (int i=0;i<trpNum;i++){
	   		Statement st=graphTrp[i];
	   		Resource r=st.getSubject();
	   		Property p=st.getPredicate();
	   		RDFNode o=st.getObject();
	   		if(r.toString().equals(sStr) 
	   			&& o.toString().equals(oStr)){
	   			pr=p;
	   			stm=st;
	   			break;
	   		}
		}
		result.add(0,pr);
		result.add(1,stm);
		return result;
	}
	
	/**********************
	 * �ӹ���ͼ����Ԫ����ȷ��S
	 ********************/
	public Resource findSFromGraphTriple(String sStr)
	{
		Resource sub=null;
		for (int i=0;i<trpNum;i++){
	   		Statement st=graphTrp[i];
	   		Resource r=st.getSubject();
//	   		Property p=st.getPredicate();
//	   		RDFNode o=st.getObject();
	   		if(r.toString().equals(sStr)){
	   			sub=r;
	   			break;
	   		}
		}
		return sub;
	}
	
	public OntProperty findPropByFullName(String name)
	{
		OntProperty x=null;
		for(int i=0;i<fullPropNum;i++){
			String str=null;
			if (fullPropName[i].isAnon()){
				str=fullPropName[i].toString();
			}
			else{
				str=fullPropName[i].toString();
			}
			if (str.equals(name)){
				x=fullPropName[i];
				break;
			}
		}
		return x;
	}
	
	public OntProperty findPropByLocalName(String name)
	{
		OntProperty x=null;
		for(int i=0;i<fullPropNum;i++){
			String str=null;
			if (fullPropName[i].isAnon()){
				str=fullPropName[i].toString();
			}
			else{
				str=fullPropName[i].getLocalName();
			}
			if (str.equals(name)){
				x=fullPropName[i];
				break;
			}
		}
		return x;
	}
	
	public Individual findInsByLocalName(String name)
	{
		Individual x=null;
		for(int i=0;i<fullInsNum;i++){
			String str=null;
			if (fullInsName[i].isAnon()){
				str=fullInsName[i].toString();
			}
			else{
				str=fullInsName[i].getLocalName();
			}
			if (str.equals(name)){
				x=fullInsName[i];
				break;
			}
		}
		return x;
	}
	
	public OntClass findCnptByLocalName(String name)
	{
		OntClass x=null;
		for(int i=0;i<fullConceptNum;i++){
			String str=null;
			if (fullConceptName[i].isAnon()){
				str=fullConceptName[i].toString();
			}
			else{
				str=fullConceptName[i].getLocalName();
			}
			if (str.equals(name)){
				x=fullConceptName[i];
				break;
			}
		}
		return x;
	}
	
	/**********************
	 * �ӹ���ͼ����Ԫ����ȷ��O
	 ********************/
	public RDFNode findOFromGraphTriple(String oStr)
	{
		RDFNode obj=null;
		for (int i=0;i<trpNum;i++){
	   		Statement st=graphTrp[i];
	   		RDFNode o=st.getObject();
	   		if(o.toString().equals(oStr)){
	   			obj=o;
	   			break;
	   		}
		}
		return obj;
	}
	
	/**********************
	 * ͨ��������������ÿ���������ͼ
	 ********************/
	public ConceptSubGraph[] gainCnptSubGraph(double[][] cMatrix)
	{
		//��ʼ����·�еĲ���
		conductanceMatrix=new double[graphVNum][graphVNum];
		voltageMatrix=new double[graphVNum][1];
		rawCurrent=new double[graphVNum][graphVNum];
		currentOut=new double[graphVNum];
		
		ConceptSubGraph[] subG = new ConceptSubGraph[conceptNum];
		conductanceMatrix=cMatrix;
		
		for (int i=0;i<conceptNum;i++)
		{
			if (conceptName[i] == null) continue;
			
			System.out.println("����:"+i+"-->"+conceptName[i]);
			
			//find the position of the concept
			int star =this.findVetexPosInGraph(conceptName[i]);
			int end =graphVNum-1;
					
			//recorde the subgraph information
			subG[i] = new ConceptSubGraph();
			subG[i].conceptName = new String(conceptName[i]);
			subG[i].subGraph = new DefaultDirectedGraph(DefaultEdge.class);
			
			//����ָ������Ļ�����ͼ
			LinearEquation Equation = new LinearEquation();
			//the last node is sink node, it is "sourceVertexNum+1"
			Equation.InitlizePara(graphVNum,star,end);
			//�趨�絼����
			Equation.SetConductMatrix(conductanceMatrix);
			//ָ����ʼ��ѹ
			Equation.PrepareMatrixA_b(10.0, 0.0);
			//�����Է�����
			Equation.Solve();
			//�õ��������ϵ�ѹ
			voltageMatrix = Equation.GetResultMaxtrix();
			
			//ͼ�еĵ���
			ComputeRawCurrent(graphVNum);
			//ÿ�����out current
			ComputeCurrentOut(graphVNum);
			
			//Displaygeneration����ʽ�㷨
			subG[i].subGraph = displayGraphGeneration(graphVNum,graphVName,MAX_DISPSUBGRAPH_SIZE);
			
			//���ݵ�ǰ�ļ��㻷����������ͼ
			refineSubGraph(subG[i].subGraph);
			
			//�޲���ȱ��multiEdge
			mendSubGraphWithMultiEdge(subG[i].subGraph);
			
			//��ȡ��subGraph��Ӧ����Ԫ��List
			subG[i].stmList=extractSubGraphTriples(subG[i].subGraph);
			
			System.out.println("Display Graph: "+subG[i].subGraph.toString());
			System.out.println("Sub Graph Size:"+subG[i].subGraph.vertexSet().size());
			//��ʾ��ͼ��ʵ����Ԫ��
			for (Iterator itxx=subG[i].stmList.iterator();itxx.hasNext();){
				System.out.println((Statement)itxx.next());
			}
			
			//��ʾ�ʹ洢�õ�����ͼ
//			showAndSaveSubGraph(subG[i].subGraph,graphVName[star]);
		}
		return subG;
	}
	
	private void mendSubGraphWithMultiEdge(DirectedGraph g) {
		/*�ҵ����ڵ�Multi Edge*/
		ArrayList subgEdgeList=new ArrayList();
		subgEdgeList.addAll(g.edgeSet());
		Set multEdgeNode=new HashSet();
		for (Iterator it=subgEdgeList.iterator();it.hasNext();){
			DefaultEdge eg=(DefaultEdge)it.next();
			String sV=g.getEdgeSource(eg).toString();
			String tV=g.getEdgeTarget(eg).toString();
			/*sV,tV�Ƿ����Statement_x*/
			if (sV.indexOf("Statement_")!=-1 || tV.indexOf("Statement_")!=-1){//����
				String bgNode;
				if (sV.indexOf("Statement_")!=-1){
					bgNode=sV;
				}
				else{
					bgNode=tV;
				}
				/*��¼��ͼ�г��ֵĶ��ر߱��*/
				multEdgeNode.add(bgNode);
			}
		}
		
		/*�޲����ֵĶ��ر�*/
		for (Iterator it=multEdgeNode.iterator();it.hasNext();){
			String bgNode=(String)it.next();
			for (Iterator jt=graphME.iterator();jt.hasNext();){
				DefaultEdge eg=(DefaultEdge)jt.next();
				String sV=g.getEdgeSource(eg).toString();
				String tV=g.getEdgeTarget(eg).toString();
				if (sV.equals(bgNode) || tV.equals(bgNode)){//�ҵ����ر�
					g.addVertex(sV);
					g.addVertex(tV);
					g.addEdge(sV,tV);
				}
			}
		}
	}

	/**********************
	 * Ѱ�Ҹ�����ͼ�нڵ��λ��
	 ********************/
	public int findVetexPosInGraph(String name)
	{
		int pos = -1;
		for (int i=0;i<graphVNum;i++)
		{
			if (graphVName[i].equals(name))
			{
				pos = i;
				break;
			}
		}
		return pos;
	}
	
	/**********************
	 * ��ʾ�ʹ洢��ͼ
	 ********************/
	public void showAndSaveSubGraph(DirectedGraph g, String vName)
	{
//		System.out.println("Star Vertex:" + vName + "\n");
		String[] tempName = new String[g.vertexSet().size()];
		Iterator itr = g.vertexSet().iterator();
		int tcount = 0;
		while (itr.hasNext()) {
			tempName[tcount] = itr.next().toString();
			tcount++;
		}
		this.writeGraphDataForShow(g, tempName);
	}
	
	/**********************
	 * ��ͼ�ľ���
	 ********************/
	public void refineSubGraph(DirectedGraph g)
	{
		
	}
	
	/**********************
	 * ͨ��������������ÿ�����Ե���ͼ
	 ********************/
	public PropertySubGraph[] gainPropSubGraph(double[][] cMatrix)
	{
		//��ʼ����·�еĲ���
		conductanceMatrix=new double[graphVNum][graphVNum];
		voltageMatrix=new double[graphVNum][1];
		rawCurrent=new double[graphVNum][graphVNum];
		currentOut=new double[graphVNum];
		
		PropertySubGraph[] subG = new PropertySubGraph[propNum];
		conductanceMatrix=cMatrix;
		
		for (int i=0;i<propNum;i++)
		{
			if (propName[i] == null) continue;
			
//			System.out.println("����:"+i+"-->"+propName[i]);
			
			//find the position of the property
			int star =this.findVetexPosInGraph(propName[i]);
			int end =graphVNum-1;
			
			/*���ӱ�����������*/
			ArrayList newEdge=new ArrayList();
			double[][] newMatrix=new double[graphVNum][graphVNum];
			newMatrix=conductanceMatrix.clone();//����ԭʼȨ�ؾ���
			newEdge=addNewPropUseEdge(star,this.graphVFullName[star],newMatrix);
			
			//recorde the subgraph information
			subG[i] = new PropertySubGraph();
			subG[i].propName = new String(propName[i]);
			subG[i].subGraph = new DefaultDirectedGraph(DefaultEdge.class);
			
			//����ָ�����ԵĻ�����ͼ
			LinearEquation Equation = new LinearEquation();
			//the last node is sink node, it is "sourceVertexNum+1"
			Equation.InitlizePara(graphVNum,star,end);
			//�趨�絼����
			Equation.SetConductMatrix(newMatrix);
			//ָ����ʼ��ѹ
			Equation.PrepareMatrixA_b(10.0, 0.0);
			//�����Է�����
			Equation.Solve();
			//�õ��������ϵ�ѹ
			voltageMatrix = Equation.GetResultMaxtrix();
			
			//ͼ�еĵ���
			ComputeRawCurrent(graphVNum);
			//ÿ�����out current
			ComputeCurrentOut(graphVNum);
			
			//Displaygeneration����ʽ�㷨
			subG[i].subGraph = displayGraphGeneration(graphVNum,graphVName,MAX_DISPSUBGRAPH_SIZE);
			
			//������ͼ
			for (Iterator it=newEdge.iterator();it.hasNext();){
				DefaultEdge eg=(DefaultEdge)it.next();
				String sV=orgnGraph.getEdgeSource(eg).toString();
				String tV=orgnGraph.getEdgeTarget(eg).toString();
				if (subG[i].subGraph.containsVertex(sV) &&
						!subG[i].subGraph.containsEdge(sV,tV)){
					subG[i].subGraph.addVertex(tV);
					subG[i].subGraph.addEdge(sV,tV);
				}
			}
			
			//���ݵ�ǰ�ļ��㻷����������ͼ
			refineSubGraph(subG[i].subGraph);
			
			//�޲���ȱ��multiEdge
			mendSubGraphWithMultiEdge(subG[i].subGraph);
			
			System.out.println("Display Graph: "+subG[i].subGraph.toString());
			System.out.println("Sub Graph Size:"+subG[i].subGraph.vertexSet().size());
			
			//��ȡ��subGraph��Ӧ����Ԫ��List
			subG[i].stmList=extractSubGraphTriples(subG[i].subGraph);
			
			//��ʾ�ʹ洢�õ�����ͼ
//			showAndSaveSubGraph(subG[i].subGraph,graphVName[star]);
		}
		return subG;
	}
	
	/**********************
	 * �ӹ��ɴ�ͼ����Ԫ���г�ȡ���ɵ�ǰ��ͼ��triples
	 ********************/
	public ArrayList extractSubGraphTriples(DirectedGraph g)
	{
		ArrayList stmList=new ArrayList();
		
		/*ת����ͼ�ı�Ϊ<a,b>��<a,p,b>��������*/
		ArrayList gEdgeList=new ArrayList();
		gEdgeList.addAll(g.edgeSet());
		Set multEdge=new HashSet();
		for (Iterator it=g.edgeSet().iterator();it.hasNext();){
			DefaultEdge eg=(DefaultEdge)it.next();
			String sV=g.getEdgeSource(eg).toString();
			String tV=g.getEdgeTarget(eg).toString();
			/*sV,tV�Ƿ����Statement_x*/
			if (sV.indexOf("Statement_")!=-1 || tV.indexOf("Statement_")!=-1){//����
				String bgNode;
				if (sV.indexOf("Statement_")!=-1){
					bgNode=sV;
				}
				else{
					bgNode=tV;
				}
				/*��¼���ر߳��ֵ�λ��*/
				multEdge.add(bgNode);
			}
			else{//������
				/*�õ�Ψһȷ����Statement*/
				Statement st=getTripleFromGraph(sV,tV);
				if (!stmList.contains(st) && st!=null){
					stmList.add(st);
				}
				/*����Listɾ����ǰ�ı�*/
				gEdgeList.remove(eg);
			}
		}
		
		/*������ر�*/
		for (Iterator it=multEdge.iterator();it.hasNext();){
			String bgNode=(String)it.next();
			/*�ҵ�����Statement_x�ı�*/
			String mSub=null;
			String mPre=null;
			String mObj=null;
			for (Iterator jt=gEdgeList.iterator();jt.hasNext();){
				DefaultEdge eg=(DefaultEdge)jt.next();
				String sV=g.getEdgeSource(eg).toString();
				String tV=g.getEdgeTarget(eg).toString();
				if (!sV.equals(bgNode) && tV.equals(bgNode)){
					//�ҵ�S
					mSub=sV;
				}
				if (sV.equals(bgNode)){
					//�ҵ�P��O
					if(mPre==null){
						mPre=tV;
					}
					else{
						mObj=tV;
					}
				}
			}
			if(mSub!=null && mPre!=null && mObj!=null)
			{
				/*�õ�Ψһȷ����Statement*/
				Statement st=getTripleFromGraph(mSub,mPre,mObj);
				if (st==null ){
					st=getTripleFromGraph(mSub,mObj,mPre);
				}
				if (!stmList.contains(st) && st!=null){
					stmList.add(st);
				}
			}
			else{//��Ԫ����Ϣ�Ѿ�������
				/*��������Ĵ������жϣ����Ծ�ȷ����
				 * ������õĽ��Ʒ����Ǿ������ذ���<s,o>����Ԫ��*/
				if(mSub!=null && mPre!=null && mObj==null)
				{
					Statement st=getTripleFromGraph(mSub,mPre);
					if (!stmList.contains(st) && st!=null){
						stmList.add(st);
					}
				}
				if(mSub!=null && mPre==null && mObj!=null)
				{
					Statement st=getTripleFromGraph(mSub,mObj);
					if (!stmList.contains(st) && st!=null){
						stmList.add(st);
					}
				}
			}
		}
		return stmList;
	}
	
	/*****************
	 * �õ�Ψһ����s,o��������Ԫ�� 
	 *****************/
	public Statement getTripleFromGraph(String s, String o)
	{
		Statement result=null;
	   	for (int i=0;i<trpNum;i++){
	   		Statement st=graphTrp[i];
	   		Resource sub=st.getSubject();
	   		Property pre=st.getPredicate();
	   		RDFNode obj=st.getObject();
	   		
	   		//���S,P,O������
	   		ArrayList list=ontParse.getStLocalName(st);
	   		String sStr=(String)list.get(0);
	   		String pStr=(String)list.get(1);
	   		String oStr=(String)list.get(2);
	   		
	   		if (sStr.equals(s)&& oStr.equals(o)){
	   			result=st;
	   			break;
	   		}
	   	}
	   	return result;
	}
	
	/*****************
	 * �õ�Ψһ����s,p,o��������Ԫ�� 
	 *****************/
	public Statement getTripleFromGraph(String s, String p, String o)
	{
		Statement result=null;
	   	for (int i=0;i<trpNum;i++){
	   		Statement st=graphTrp[i];
	   		Resource sub=st.getSubject();
	   		Property pre=st.getPredicate();
	   		RDFNode obj=st.getObject();
	   		
	   		//���S,P,O������
	   		ArrayList list=ontParse.getStLocalName(st);
	   		String sStr=(String)list.get(0);
	   		String pStr=(String)list.get(1);
	   		String oStr=(String)list.get(2);
	   		
	   		if (sStr.equals(s)&& pStr.equals(p) && oStr.equals(o)){
	   			result=st;
	   			break;
	   		}
	   	}
	   	return result;
	}
	
	public OntClass obtainAnonOntClass(String name)
	{
		OntClass c=null;
		for (Iterator i=anonCnpt.iterator();i.hasNext();){
			OntClass c_temp = (OntClass) i.next();
			if (name.equals(c_temp.toString())){
				c=c_temp;
				break;
			}
		}
		return c;
	}
	
	public OntProperty obtainAnonProperty(String name)
	{
		OntProperty c=null;
		for (Iterator i=anonProp.iterator();i.hasNext();){
			OntProperty c_temp = (OntProperty) i.next();
			if (name.equals(c_temp.toString())){
				c=c_temp;
				break;
			}
		}
		return c;
	}
	
	public Individual obtainAnonIndividual(String name)
	{
		Individual c=null;
		for (Iterator i=anonIns.iterator();i.hasNext();){
			Individual c_temp = (Individual) i.next();
			if (name.equals(c_temp.toString())){
				c=c_temp;
				break;
			}
		}
		return c;
	}
	
	public ArrayList addNewPropUseEdge(int star,String pName,double[][] oldmatrix)
	{
		ArrayList newEdgeList=new ArrayList();
		/*(1)ȡ�����Ա�ʹ�õ���Ԫ��*/
		ArrayList stmList=new ArrayList();
		OntProperty p=findPropByFullName(pName);
		Selector sl=new SimpleSelector(null,p,(RDFNode)null);
		/*(2)������Ԫ�飬ȷ��DOMAIN��RANGE��ʵ���Ƿ���ͼ��*/
		for (StmtIterator it=m.listStatements(sl);it.hasNext();){
			Statement st=it.nextStatement();
			Resource sRes=st.getSubject();
			RDFNode oNode=st.getObject();
			ArrayList list=ontParse.getStLocalName(st);
	   		String sStr=(String)list.get(0);
	   		String oStr=(String)list.get(2);
	   		double wta=0;
	   		double wtb=0;
	   		double wtPai=0;
	   		double wtA=0;
	   		double wtB=0;
	   		double wnew=0;
	   		/*(3)��ӱ�*/
	   		if (orgnGraph.containsEdge(sStr,oStr)){//��ǰ��Ԫ�������ͼ��
	   			//��ӱ�P-->ai
	   			int aPos=-1;
	   			aPos=this.findVetexPosInGraph(sStr);
	   			DefaultEdge eg=(DefaultEdge)orgnGraph.getEdge(sStr,oStr);
	   			newEdgeList.add(eg);	   			
	   			/*(4)�����¼ӱߵ�Ȩ��*/
	   			//ai��Ȩ��
	   			//aiֻ����ʵ��
	   			Individual ai=findInsByLocalName(sStr);
	   			if (ai!=null){
	   				wta=FindInstanceyWeight(sStr,fullInsNum);
	   			}
	   			
	   			//bi��Ȩ��
	   			//bi������ʵ����ֵ
	   			int bPos=-1;
	   			bPos=this.findVetexPosInGraph(oStr);
	   			Individual bi=findInsByLocalName(oStr);
	   			if (bi==null){//����ʵ��
	   				if (oNode.isLiteral()){
	   					wtb=0.5;
	   				}
	   				else{
	   					wtb=0.2;
	   				}
	   			}
	   			else{//��ʵ��
	   				wtb=FindInstanceyWeight(oStr,fullInsNum);
	   			}
	   			//paiȨ��
	   			wtPai = this.GetStatementPaiWeight(m,st,p,sRes);
	   			//ai-->A��Ȩ��
	   			if (ai!=null){
		   			for (ExtendedIterator itx=ai.listRDFTypes(true);itx.hasNext();){
		   				Resource rx=(Resource)itx.next();
		   				int APos=-1;
		   				APos=this.findVetexPosInGraph(rx.getLocalName());
		   				wtA=Math.max(wtA,oldmatrix[aPos][APos]);
		   			}
	   			}

	   			//bi-->B��Ȩ��
	   			if (bi!=null){
		   			for (ExtendedIterator itx=bi.listRDFTypes(true);itx.hasNext();){
		   				Resource rx=(Resource)itx.next();
		   				int BPos=-1;
		   				BPos=this.findVetexPosInGraph(rx.getLocalName());
		   				wtB=Math.max(wtB,oldmatrix[aPos][BPos]);
		   			}
	   			}
	   			else{
	   				wtB=0.5;
	   			}
	   			//�ܵ�Ȩ��
	   			wnew=(wtA+wtB+(wtPai+wta+wtb)/3.0)/3.0;
	   			oldmatrix[star][aPos]=wnew;
	   			oldmatrix[aPos][star]=0.8*wnew;
	   		}
		}
		return newEdgeList;
	}
}
/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Filename          Rclm.java
 * Version           2.0
 * 
 * Last modified on  2018-9-26
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 * ������ͼ��ȡ���������ࡣ�����������������й���
 ***********************************************/
package lily.onto.mapping.method;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import org.dom4j.DocumentException;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import lily.onto.mapping.evaluation.EvaluateMapping;
import lily.onto.parse.OWLOntParse;
import lily.test.Test;
import lily.tool.datastructure.ConceptSubGraph;
import lily.tool.datastructure.MapRecord;
import lily.tool.datastructure.PropertySubGraph;
import lily.tool.datastructure.TextDes;
import lily.tool.filter.SimpleFilter;
import lily.tool.filter.StableMarriageFilter;
import lily.tool.inifile.INIFile;
import lily.tool.mappingfile.MappingFile;
import lily.tool.textsimilarity.TextDocSim;
import lily.tool.threshold.DynamicThreshold;
import lily.tool.threshold.SimDataVisual;
import lily.onto.handle.describe.OntDes;
import lily.onto.handle.graph.*;
import lily.onto.handle.propagation.CbSubSimPropagation;
import lily.onto.handle.propagation.FullSimPropagation;
import lily.onto.handle.propagation.SubSimPropagation;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * 
 * describe:
 * ��Ҫ�ࡣ��ͼ���������
 ********************/
public class Rclm {
	//Դ��Ŀ�걾����ļ�
	public String sourceOntFile;
	public String targetOntFile;
	//��׼ӳ���ļ�
	public String refalignFile;
	//Դ��Ŀ�걾���model
	public OntModel m_source;
	public OntModel m_target;
	//������Ŀ
	public int sourceConceptNum;
	public int targetConceptNum;
	//������Ŀ
	public int sourcePropNum;
	public int sourceDataPropNum;
	public int sourceObjPropNum;
	public int targetPropNum;
	public int targetDataPropNum;
	public int targetObjPropNum;
	//ʵ����Ŀ
	public int sourceInsNum;
	public int targetInsNum;
	//������
	public String[] sourceConceptName;
	public String[] targetConceptName;
	//������
	public String[] sourcePropName;
	public String[] sourceDataPropName;
	public String[] sourceObjPropName;
	public String[] targetPropName;
	public String[] targetDataPropName;
	public String[] targetObjPropName;
	//ʵ����
	public String[] sourceInsName;
	public String[] targetInsName;
	
	//������Դ
	ArrayList sourceAnonCnpt;
	ArrayList sourceAnonProp;
	ArrayList sourceAnonIns;	
	ArrayList targetAnonCnpt;
	ArrayList targetAnonProp;
	ArrayList targetAnonIns;	
	
	//��������baseURI�µı���Ԫ��
	public int sourceFullConceptNum;
	public int sourceFullPropNum;
	public int sourceFullDataPropNum;
	public int sourceFullObjPropNum;
	public int sourceFullInsNum;
	public OntClass[] sourceFullConceptName;
	public OntProperty[] sourceFullPropName;
	public DatatypeProperty[] sourceFullDataPropName;
	public ObjectProperty[] sourceFullObjPropName;
	public Individual[] sourceFullInsName;
	
	public int targetFullConceptNum;
	public int targetFullPropNum;
	public int targetFullDataPropNum;
	public int targetFullObjPropNum;
	public int targetFullInsNum;
	public OntClass[] targetFullConceptName;
	public OntProperty[] targetFullPropName;
	public DatatypeProperty[] targetFullDataPropName;
	public ObjectProperty[] targetFullObjPropName;
	public Individual[] targetFullInsName;
	
	//��ͼ��Ϣ
	public ConceptSubGraph[] sourceCnptSubG;
	public PropertySubGraph[] sourcePropSubG;
	public ConceptSubGraph[] targetCnptSubG;
	public PropertySubGraph[] targetPropSubG;
	
	//ȫͼ��Ϣ,��Ԫ����ʽ
	public ArrayList sourceStmList;
	public ArrayList targetStmList;
	
	//�ı�������Ϣ
	public TextDes[] sourceCnptTextDes;
	public TextDes[] sourcePropTextDes;
	public TextDes[] sourceInsTextDes;
	public ArrayList[] sourceCnptOtTextDes;
	public ArrayList[] sourcePropOtTextDes;
	public ArrayList sourceFullOtTextDes;
	public TextDes[] targetCnptTextDes;
	public TextDes[] targetPropTextDes;
	public TextDes[] targetInsTextDes;
	public ArrayList[] targetCnptOtTextDes;
	public ArrayList[] targetPropOtTextDes;
	public ArrayList targetFullOtTextDes;
	
	//���ƾ���
	public double[][] simMxConcept;
	public double[][] simMxProp;
	public double[][] simMxDataProp;
	public double[][] simMxObjProp;
	public double[][] simMxIns;
	
	//���ƶȴ���������ƾ���
	public double[][] pgSimMxConcept;
	public double[][] pgSimMxProp;
	
	//����ģʽ
	public boolean isSubProg;
	
	//ӳ������
	public int mappingNum;
	//ӳ����
	public MapRecord[] mappingResult;
	//�������
	public double precision;
	public double recall;
	public double f1Measure;
	//�ı����ƺ���ͼ���Ƶ�Ȩ��
	public double graphWeight;
	public double literalWeight;
	//base URI
	public String sourceBaseURI;
	public String targetBaseURI;
	//���ƶȷ�ֵ
	public double cnptSimThreshold;//�������Ʒ�ֵ
	public double dpSimThreshold;//Datatype Property���Ʒ�ֵ
	public double opSimThreshold;//Object Property���Ʒ�ֵ
	public double propSimThreshold;//Property���Ʒ�ֵ
	//TextMatch���Ž��λ��
	public Set sourceCnptOkSimPos;
	public Set sourcePropOkSimPos;
	public Set targetCnptOkSimPos;
	public Set targetPropOkSimPos;
	
	//�Ƿ���Ҫ���ƶȴ���
	public boolean isNeedSimProg;
	
	//������ļ���
	public String lilyFileName="";
	
	//constants
	public int EQUALITY = 0;
	public int GENERAL = 1;
	public int SPECIFIC = 2;
	public boolean DISTINCT_DP_OP=false;
	public double OKSIM = 0.5;
	public double BADSIM = 0.001;
	public int  Semantic_SubGraph_Size=1;
	
	
	/**
	 * Method information
	 * -------------------
	 * @param args
	 * return
	 * function:����ͼ�ķ��������б���ӳ��
	 */
	public static void main(String[] args) {
		new Rclm().runSample();
//		new Rclm().run2005Bench();
//		new Rclm().run2005Directory();
//		new Rclm().run2007DirectoryPtr();
//		new Rclm().run2007Conference();
//		new Rclm().runFood();
//		new Rclm().runLibrary();
//		new Rclm().evaluate("E:/OnGoingWork/ThingsToDo/OAEI2007Campaign/LilyResult/Premilary Result/" +
//				"/version2/benchmarks/303/lily.rdf",
//        "./dataset/OAEI2007/bench/benchmarks/303/refalign.rdf");
	}
	
	public void runSample(){
		Rclm ontM = new Rclm();
		ontM.readConfigFile();
		ontM.parseOnt();
		ontM.init();
		ontM.run();
		ontM.evaluate();
		ontM=null;
	}
	
	public void run2005Directory(){
		int fineNum=2265;
		double sum=0;
		for (int i=1300;i<1400;i++){
			Rclm ontM = new Rclm();
			ontM.sourceOntFile = "./dataset/OAEI2005/Directory/"+String.valueOf(i+1)+"/source.owl";
			ontM.targetOntFile = "./dataset/OAEI2005/Directory/"+String.valueOf(i+1)+"/target.owl";
			ontM.refalignFile = "./dataset/OAEI2005/Directory/mappings/"+String.valueOf(i+1)+".owl";
			ontM.parseOnt();
			ontM.init();
			System.out.println(ontM.targetOntFile);
			ontM.run();
			ontM.evaluate();
			sum+=ontM.recall;
			System.out.println("------------------");
			ontM=null;
			System.gc();
		}
		System.out.println("Directory test Recall:"+sum/fineNum);
	}
	
	public void run2007DirectoryPtr(){
		int fineNum=4640;
		double sum=0;
		for (int i=0;i<4640;i++){
			Rclm ontM = new Rclm();
			ontM.sourceOntFile = "./dataset/OAEI2007/Directory/ptr/"+String.valueOf(i+1)+"/source.owl";
			ontM.targetOntFile = "./dataset/OAEI2007/Directory/ptr/"+String.valueOf(i+1)+"/target.owl";
//			ontM.refalignFile = "./dataset/OAEI2007/Directory/ptr/ptrMappings/"+String.valueOf(i+1)+".rdf";
			ontM.refalignFile = "";
			ontM.parseOnt();
			ontM.init();
			System.out.println(ontM.targetOntFile);
			ontM.run();
			ontM.evaluate();
			sum+=ontM.recall;
			System.out.println("------------------");
			ontM=null;
			System.gc();
		}
		System.out.println("Directory test Recall:"+sum/fineNum);
	}
	
	public void run2007Conference(){
		String[] ontName={"Ekaw","Conference","Sigkdd","Iasted","Micro","Confious","Pcs",
				          "OpenConf","confOf","crs_dr","Cmt","Cocus","Paperdyne","Edas"};
		for (int i=0;i<14;i++){
			for (int j=1;j<14;j++){
				Rclm ontM = new Rclm();
				ontM.sourceOntFile = "./dataset/OAEI2007/conference/"+ontName[i]+".owl";
				ontM.targetOntFile = "./dataset/OAEI2007/conference/"+ontName[j]+".owl";
//				ontM.refalignFile = "./dataset/OAEI2007/Directory/ptr/ptrMappings/"+String.valueOf(i+1)+".rdf";
				ontM.refalignFile = "";
				ontM.lilyFileName = ontName[i]+"-"+ontName[j];
				ontM.parseOnt();
				ontM.init();
				System.out.println(ontM.sourceOntFile);
				System.out.println(ontM.targetOntFile);
				ontM.run();
				ontM.evaluate();
				System.out.println("------------------");
				ontM=null;
				System.gc();
			}
		}
	}
	
	public void runFood() {
		Rclm ontM = new Rclm();
		ontM.sourceOntFile = "E:/temp/agrovoc_oaei2007.owl";
		ontM.targetOntFile = "E:/temp/agrovoc_oaei2007.owl";
		ontM.refalignFile = "./dataset/OAEI2005/bench/benchmarks/101/refalign.rdf";
		ontM.parseOnt();
		ontM.init();
		System.out.println(ontM.targetOntFile);
		ontM.run();
		ontM.evaluate();
		System.out.println("------------------");
		ontM = null;
	}
	public void runLibrary() {
		Rclm ontM = new Rclm();
		ontM.sourceOntFile = "E:/temp/GTT_OAEI.owl";
		ontM.targetOntFile = "E:/temp/Brinkman_OAEI.owl";
		ontM.refalignFile = "./dataset/OAEI2005/bench/benchmarks/101/refalign.rdf";
		ontM.parseOnt();
		ontM.init();
		System.out.println(ontM.targetOntFile);
		ontM.run();
		ontM.evaluate();
		System.out.println("------------------");
		ontM = null;
	} 
	
	public void run2005Bench(){
		int fineNum=19;
//		int fileName[]={101,103,104,201,202,203,204,205,206,207,208,209,210,
//				           221,222,223,224,225,228,230,231,232,233,236,237,238,239,
//				           240,241,246,247,248,249,250,251,252,253,254,257,258,259,
//				           260,261,262,265,266,301,302,303,304};
		int fileName[]={248,249,250,251,252,253,254,257,258,259,
		                260,261,262,265,266,301,302,303,304};
		for (int i=0;i<fineNum;i++){
			System.gc();
			Rclm ontM = new Rclm();
			ontM.sourceOntFile = "./dataset/OAEI2005/bench/benchmarks/101/onto.rdf";
			ontM.targetOntFile = "./dataset/OAEI2005/bench/benchmarks/"+String.valueOf(fileName[i])+"/onto.rdf";
			ontM.refalignFile = "./dataset/OAEI2005/bench/benchmarks/"+String.valueOf(fileName[i])+"/refalign.rdf";
			ontM.parseOnt();
			ontM.init();
			System.out.println(ontM.targetOntFile);
			ontM.run();
			ontM.evaluate();
			System.out.println("------------------");
			ontM=null;
		}
	}
	
	
	public void runThreshold(){
		int fineNum=15;
		int fileName[]={101,103,201,202,208,209,221,232,241,248,
		                251,301,302,303,304};
		for (int i=0;i<fineNum;i++){
			System.gc();
			Rclm ontM = new Rclm();
			ontM.sourceOntFile = "./dataset/OAEI2005/bench/benchmarks/101/onto.rdf";
			ontM.targetOntFile = "./dataset/OAEI2005/bench/benchmarks/"+String.valueOf(fileName[i])+"/onto.rdf";
			ontM.refalignFile = "./dataset/OAEI2005/bench/benchmarks/"+String.valueOf(fileName[i])+"/refalign.rdf";
			ontM.parseOnt();
			ontM.init();
			System.out.println(ontM.targetOntFile);
			ontM.run();
			ontM.evaluate();
			System.out.println("------------------");
			ontM=null;
		}
	}
	
	//��ʼ������Ҫ�ǻ����Ĳ�������
	public void init()
	{
		graphWeight = 0.0;
		literalWeight = 1.0;
		isSubProg=true;//true:���ƶȰ�����ͼ����
		
		sourceCnptSubG=new ConceptSubGraph[sourceConceptNum];
		sourcePropSubG=new PropertySubGraph[sourcePropNum];
		targetCnptSubG=new ConceptSubGraph[targetConceptNum];
		targetPropSubG=new PropertySubGraph[targetPropNum];
	}
	
	//���ñ����ļ�
	public void setOntFile()
	{
		sourceOntFile = new String ("./dataset/OAEI2007/bench/benchmarks/101/onto.rdf");
//		sourceOntFile = "e:/temp/lilyOWLtest.owl";
//		sourceOntFile = "e:/temp/source.owl";
		targetOntFile = new String ("./dataset/OAEI2007/bench/benchmarks/101/onto.rdf");
//		targetOntFile = "e:/temp/lilyOWLtest.owl";
//		targetOntFile = "e:/temp/target.owl";
		refalignFile = new String ("./dataset/OAEI2007/bench/benchmarks/101/refalign.rdf");
//		targetOntFile = "e:/temp/lilyOWLtest.owl";
//		refalignFile = "e:/temp/mapping96.owl";
	}
	
	//���ⲿINI�ļ���ȡ��ʼ����
	@SuppressWarnings("unused")
	private void readConfigFile()
	{
		INIFile objINI = null;
		objINI = new INIFile("./config.ini");
		
		sourceOntFile=objINI.getStringProperty("Matching_Ontologies","SourceOnt");
		targetOntFile=objINI.getStringProperty("Matching_Ontologies","TargetOnt");
		refalignFile=objINI.getStringProperty("Matching_Ontologies","RefAlignFile");
		Semantic_SubGraph_Size=objINI.getIntegerProperty("Public_Parameters","Semantic_SubGraph_Size");
		
		System.out.println("Source Ontology:"+sourceOntFile);
		System.out.println("Target Ontology:"+targetOntFile);
	}
	
	//��������
	public void parseOnt()
	{
		System.out.println("Parsing ontologies...");
		OWLOntParse ontParse = new OWLOntParse();
		ArrayList list = new ArrayList();
    	
    	//Դ����----------------------------------------------
    	m_source = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    	//The ontology file information
        m_source.getDocumentManager().addAltEntry( "http://pengwang/", sourceOntFile);
        //Read the reference ontology file
        ontParse.readOntFile(m_source,sourceOntFile);
        
        //Դ�����base URI
        sourceBaseURI = ontParse.getOntBaseURI(m_source);
        
        //Get all Classes of Ontology
       	list = ontParse.listAllConceptsFilterBaseURI (m_source, sourceBaseURI);
       	sourceConceptNum = ((Integer)list.get(0)).intValue();
       	sourceConceptName = new String[sourceConceptNum];
       	sourceConceptName = (String[])((ArrayList)list.get(1)).toArray(new String[0]);
       	
       	//Get all datatype properties
       	list = ontParse.listAllDatatypeRelationsURI(m_source,sourceBaseURI);
       	sourceDataPropNum = ((Integer)list.get(0)).intValue();
       	sourceDataPropName = new String[sourceDataPropNum];
       	sourceDataPropName = (String[])((ArrayList)list.get(1)).toArray(new String[0]);
       	
       	//Get all object properties
       	list = ontParse.listAllObjectRelationsURI(m_source,sourceBaseURI);
       	sourceObjPropNum = ((Integer)list.get(0)).intValue();
       	sourceObjPropName = new String[sourceObjPropNum];
       	sourceObjPropName = (String[])((ArrayList)list.get(1)).toArray(new String[0]);
       	
       	//Get all properties
       	sourcePropNum = sourceDataPropNum+sourceObjPropNum;
       	sourcePropName = new String[sourcePropNum];
       	for(int i=0;i<sourceDataPropNum;i++) {sourcePropName[i]=sourceDataPropName[i];}
       	for(int i=0;i<sourceObjPropNum;i++) {sourcePropName[i+sourceDataPropNum]=sourceObjPropName[i];}
       	
       	//get all instances
       	list = ontParse.listAllInstances(m_source);
       	sourceInsNum = ((Integer)list.get(0)).intValue();
       	sourceInsName = new String[sourceInsNum];
       	sourceInsName = (String[])((ArrayList)list.get(1)).toArray(new String[0]);
       	
       	/*��������baseURI�ı�����Ϣ*/
       	ArrayList fullOntlist = ontParse.getFullOntInfo(m_source);
		//������Ϣ
		list = (ArrayList)fullOntlist.get(0);
       	sourceFullConceptNum = ((Integer)list.get(0)).intValue();
       	sourceFullConceptName = new OntClass[sourceFullConceptNum];
       	sourceFullConceptName = (OntClass[])((ArrayList)list.get(1)).toArray(new OntClass[0]);
       	//������Ϣ
       	list = (ArrayList)fullOntlist.get(1);
       	sourceFullPropNum = ((Integer)list.get(0)).intValue();
       	sourceFullPropName = new OntProperty[sourceFullPropNum];
       	sourceFullPropName = (OntProperty[])((ArrayList)list.get(1)).toArray(new OntProperty[0]);
       	//DatatypeProperty
       	list = (ArrayList)fullOntlist.get(2);
       	sourceFullDataPropNum = ((Integer)list.get(0)).intValue();
       	sourceFullDataPropName = new DatatypeProperty[sourceFullDataPropNum];
       	sourceFullDataPropName = (DatatypeProperty[])((ArrayList)list.get(1)).toArray(new DatatypeProperty[0]);
       	//ObjectProperty
       	//DatatypeProperty
       	list = (ArrayList)fullOntlist.get(3);
       	sourceFullObjPropNum = ((Integer)list.get(0)).intValue();
       	sourceFullObjPropName = new ObjectProperty[sourceFullObjPropNum];
       	sourceFullObjPropName = (ObjectProperty[])((ArrayList)list.get(1)).toArray(new ObjectProperty[0]);
       	//ʵ����Ϣ
       	list = (ArrayList)fullOntlist.get(4);
       	sourceFullInsNum = ((Integer)list.get(0)).intValue();
       	sourceFullInsName = new Individual[sourceFullInsNum];
       	sourceFullInsName = (Individual[])((ArrayList)list.get(1)).toArray(new Individual[0]);
    	
       	//������Դ
       	sourceAnonCnpt=new ArrayList();
    	sourceAnonProp=new ArrayList();
    	sourceAnonIns=new ArrayList();	
    	list = ontParse.getOntAnonInfo(m_source);
    	sourceAnonCnpt=(ArrayList)list.get(0);
    	sourceAnonProp=(ArrayList)list.get(1);
    	sourceAnonIns=(ArrayList)list.get(2);
       	
       	//Ŀ�걾��---------------------------------------------
    	m_target = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    	//The ontology file information
    	m_target.getDocumentManager().addAltEntry( "http://LiSun/", targetOntFile);

        //Read the target ontology file    	
        ontParse.readOntFile(m_target,targetOntFile);
        
        //Դ�����base URI
        targetBaseURI = ontParse.getOntBaseURI(m_target);
        
        //Get all Classes of Ontology
       	list = ontParse.listAllConceptsFilterBaseURI (m_target, targetBaseURI);
       	targetConceptNum = ((Integer)list.get(0)).intValue();
       	targetConceptName = new String[targetConceptNum];
       	targetConceptName = (String[])((ArrayList)list.get(1)).toArray(new String[0]);
       	
       	//Get all datatype properties
       	list = ontParse.listAllDatatypeRelationsURI(m_target,targetBaseURI);
       	targetDataPropNum = ((Integer)list.get(0)).intValue();
       	targetDataPropName = new String[targetDataPropNum];
       	targetDataPropName = (String[])((ArrayList)list.get(1)).toArray(new String[0]);
       	
       	//Get all object properties
       	list = ontParse.listAllObjectRelationsURI(m_target,targetBaseURI);
       	targetObjPropNum = ((Integer)list.get(0)).intValue();
       	targetObjPropName = new String[targetObjPropNum];
       	targetObjPropName = (String[])((ArrayList)list.get(1)).toArray(new String[0]);
       	
       	//Get all properties
       	targetPropNum = targetDataPropNum+targetObjPropNum;
       	targetPropName = new String[targetPropNum];
       	for(int i=0;i<targetDataPropNum;i++) {targetPropName[i]=targetDataPropName[i];}
       	for(int i=0;i<targetObjPropNum;i++) {targetPropName[i+targetDataPropNum]=targetObjPropName[i];}
       	
       	//get all instances
       	list = ontParse.listAllInstances(m_target);
       	targetInsNum = ((Integer)list.get(0)).intValue();
       	targetInsName = new String[targetInsNum];
       	targetInsName = (String[])((ArrayList)list.get(1)).toArray(new String[0]);
       	
    	/*��������baseURI�ı�����Ϣ*/
       	fullOntlist = ontParse.getFullOntInfo(m_target);
		//������Ϣ
		list = (ArrayList)fullOntlist.get(0);
		targetFullConceptNum = ((Integer)list.get(0)).intValue();
		targetFullConceptName = new OntClass[targetFullConceptNum];
		targetFullConceptName = (OntClass[])((ArrayList)list.get(1)).toArray(new OntClass[0]);
       	//������Ϣ
       	list = (ArrayList)fullOntlist.get(1);
       	targetFullPropNum = ((Integer)list.get(0)).intValue();
       	targetFullPropName = new OntProperty[targetFullPropNum];
       	targetFullPropName = (OntProperty[])((ArrayList)list.get(1)).toArray(new OntProperty[0]);
       	//DatatypeProperty
       	list = (ArrayList)fullOntlist.get(2);
       	targetFullDataPropNum = ((Integer)list.get(0)).intValue();
       	targetFullDataPropName = new DatatypeProperty[targetFullDataPropNum];
       	targetFullDataPropName = (DatatypeProperty[])((ArrayList)list.get(1)).toArray(new DatatypeProperty[0]);
       	//ObjectProperty
       	//DatatypeProperty
       	list = (ArrayList)fullOntlist.get(3);
       	targetFullObjPropNum = ((Integer)list.get(0)).intValue();
       	targetFullObjPropName = new ObjectProperty[targetFullObjPropNum];
       	targetFullObjPropName = (ObjectProperty[])((ArrayList)list.get(1)).toArray(new ObjectProperty[0]);
       	//ʵ����Ϣ
       	list = (ArrayList)fullOntlist.get(4);
       	targetFullInsNum = ((Integer)list.get(0)).intValue();
       	targetFullInsName = new Individual[targetFullInsNum];
       	targetFullInsName = (Individual[])((ArrayList)list.get(1)).toArray(new Individual[0]);
       	
       	//������Դ
       	targetAnonCnpt=new ArrayList();
    	targetAnonProp=new ArrayList();
    	targetAnonIns=new ArrayList();	
    	list = ontParse.getOntAnonInfo(m_target);
    	targetAnonCnpt=(ArrayList)list.get(0);
    	targetAnonProp=(ArrayList)list.get(1);
    	targetAnonIns=(ArrayList)list.get(2);
	}
	
	public void run()
	{
		//ƥ�����
        long start = System.currentTimeMillis();//��ʼ��ʱ
        //����ṹ��ȡ
        System.out.println("Constructing Semantic Subgraphs...");
        reConsSemInf();
        //�ı�ƥ��
        System.out.println("Matching...");
        ontMatchText();
        //�ṹƥ��
        ontMatchStru();
		long end = System.currentTimeMillis();//������ʱ
		long costtime = end - start;//ͳ���㷨ʱ��
//		System.out.println("���ı�ƥ���㷨ʱ�䣺"+(double)costtime/1000.+"��");
		
		if (isNeedSimProg){
	        /*���ƶȴ���*/
			System.out.println("Similarity Propagating...");
			simPropagation();
			//�ϲ����			
			combineResult();
		}

		//��ʾӳ����
		showResult(false);
		//����ӳ����
		saveResult();
    } 
	
	/**********************
	 * ����ͼΪ���������ƶȴ���
	 ********************/
	private void subSimPropagation() {
		pgSimMxConcept=new double[sourceConceptNum][targetConceptNum];
		pgSimMxProp=new double[sourcePropNum][targetPropNum];
		ArrayList paraList = new ArrayList();
		ArrayList lt=new ArrayList();
		
		/*������ͼ������Ԫ�ص�ȱʡ���ƶ�*/
//		System.out.println("������ͼ������Ԫ�ص�ȱʡ���ƶ�");
		ArrayList[][] cnptOtSim = new ArrayList[sourceConceptNum][targetConceptNum];
		for (int i = 0; i < sourceConceptNum; i++) {
			for (int j = 0; j < targetConceptNum; j++) {
				if (sourceCnptOtTextDes[i].isEmpty() || targetCnptOtTextDes[j].isEmpty()){
					cnptOtSim[i][j]=new ArrayList();
				}
				else{
					cnptOtSim[i][j] = new TextDocSim().getOtTextSim(
							sourceCnptOtTextDes[i], targetCnptOtTextDes[j]);
				}
//				System.out.println(sourceConceptName[i]+"--"+targetConceptName[j]);
			}
		}
//		System.out.println("������ͼ������Ԫ�ص�ȱʡ���ƶ�");
		ArrayList[][] propOtSim = new ArrayList[sourcePropNum][targetPropNum];
		for (int i=0;i<sourcePropNum;i++){
			for (int j=0;j<targetPropNum;j++){
				if (sourcePropOtTextDes[i].isEmpty() || targetPropOtTextDes[j].isEmpty()){
					propOtSim[i][j]=new ArrayList();
				}
				else{
					propOtSim[i][j] = new TextDocSim().getOtTextSim(
							sourcePropOtTextDes[i], targetPropOtTextDes[j]);
				}
//				System.out.println(sourcePropName[i]+"--"+targetPropName[j]);
			}
		}
		
		/*Դ����������ı�����*/
		//���������������
		packSubSimPgPara(paraList);
		paraList.add(29,cnptOtSim);
		paraList.add(30,propOtSim);
		
		System.out.println("���ƶȴ���");
		lt=new SubSimPropagation().ontSimPg(paraList);
		pgSimMxConcept=(double[][])lt.get(0);
		pgSimMxProp=(double[][])lt.get(1);
	}
	
	private void cbSubSimPropagation() {
		pgSimMxConcept=new double[sourceConceptNum][targetConceptNum];
		pgSimMxProp=new double[sourcePropNum][targetPropNum];
		ArrayList paraList = new ArrayList();
		ArrayList lt=new ArrayList();
		
		/*������ͼ������Ԫ�ص�ȱʡ���ƶ�*/
//		System.out.println("������ͼ������Ԫ�ص�ȱʡ���ƶ�");
		ArrayList[][] cnptOtSim = new ArrayList[sourceConceptNum][targetConceptNum];
		for (int i = 0; i < sourceConceptNum; i++) {
			for (int j = 0; j < targetConceptNum; j++) {
				if (sourceCnptOtTextDes[i].isEmpty() || targetCnptOtTextDes[j].isEmpty()){
					cnptOtSim[i][j]=new ArrayList();
				}
				else{
					cnptOtSim[i][j] = new TextDocSim().getOtTextSim(
							sourceCnptOtTextDes[i], targetCnptOtTextDes[j]);
				}
//				System.out.println(sourceConceptName[i]+"--"+targetConceptName[j]);
			}
		}
//		System.out.println("������ͼ������Ԫ�ص�ȱʡ���ƶ�");
		ArrayList[][] propOtSim = new ArrayList[sourcePropNum][targetPropNum];
		for (int i=0;i<sourcePropNum;i++){
			for (int j=0;j<targetPropNum;j++){
				if (sourcePropOtTextDes[i].isEmpty() || targetPropOtTextDes[j].isEmpty()){
					propOtSim[i][j]=new ArrayList();
				}
				else{
					propOtSim[i][j] = new TextDocSim().getOtTextSim(
							sourcePropOtTextDes[i], targetPropOtTextDes[j]);
				}
//				System.out.println(sourcePropName[i]+"--"+targetPropName[j]);
			}
		}
		
		/*Դ����������ı�����*/
		//���������������
		packSubSimPgPara(paraList);
		paraList.add(33,cnptOtSim);
		paraList.add(34,propOtSim);
		
//		System.out.println("���ƶȴ���");
		lt=new CbSubSimPropagation().ontSimPg(paraList);
		pgSimMxConcept=(double[][])lt.get(0);
		pgSimMxProp=(double[][])lt.get(1);
	}
	
	/**********************
	 * ��ȫ��ͼΪ���������ƶȴ���
	 ********************/
	private void fullSimPropagation() {
		pgSimMxConcept=new double[sourceConceptNum][targetConceptNum];
		pgSimMxProp=new double[sourcePropNum][targetPropNum];
		ArrayList paraList = new ArrayList();
		ArrayList lt=new ArrayList();
		
		/*������ͼ������Ԫ�ص�ȱʡ���ƶ�*/
		System.out.println("ͼ������Ԫ�ص�ȱʡ���ƶ�");
		ArrayList OtSimList;
		if (sourceStmList.isEmpty() || targetStmList.isEmpty()) {
			OtSimList = new ArrayList();
		} else {
			OtSimList = new TextDocSim().getOtTextSim(sourceFullOtTextDes,
					targetFullOtTextDes);
		}

		/*Դ����������ı�����*/
		//���������������
		packFullSimPgPara(paraList);
		paraList.add(27,OtSimList);
		
		System.out.println("���ƶȴ���");
		lt=new FullSimPropagation().ontSimPg(paraList);
		pgSimMxConcept=(double[][])lt.get(0);
		pgSimMxProp=(double[][])lt.get(1);
		
	}
	
	/**********************
	 * ����ͼΪ���������ƶȴ���
	 ********************/
	private void simPropagation() {
		if (isSubProg){
//			subSimPropagation();
			cbSubSimPropagation();
		}
		else{
			fullSimPropagation();
		}
	}

	/**********************
	 * �ع�Դ����Informative graph
	 * �ع�Ŀ�걾��Informative graph
	 ********************/
	public void reConsSemInf()
	{
		ArrayList paraList = new ArrayList();
		ArrayList subGList=new ArrayList();
		
		/*�����ع�Դ�����������ͼ*/
		//���������������
		packOntGraphPara(paraList,true);
		
		//��ȡԴ����������ͼ
//		System.out.println("��ȡԴ����������ͼ");
		subGList=new OntGraph().consInfSubOnt(paraList);
		sourceCnptSubG=(ConceptSubGraph[])subGList.get(0);
		sourcePropSubG=(PropertySubGraph[])subGList.get(1);
		m_source=(OntModel)subGList.get(2);
		sourceStmList=(ArrayList)subGList.get(3);		
		//���ع�Դ�����������ͼ
		
		/*�����ع�Ŀ�걾���������ͼ*/
		//���������������
		packOntGraphPara(paraList,false);
		//��ȡĿ�걾��������ͼ
		subGList=new OntGraph().consInfSubOnt(paraList);
		targetCnptSubG=(ConceptSubGraph[])subGList.get(0);
		targetPropSubG=(PropertySubGraph[])subGList.get(1);
		m_target=(OntModel)subGList.get(2);
		targetStmList=(ArrayList)subGList.get(3);
		//���ع�Ŀ�걾���������ͼ
	}
	
	/**********************
	 * �����ı���ƥ��
	 * 1.����Informative graph
	 * 2.��ȡ�ı�
	 * 3.�����ı����ƶ�
	 ********************/
	private void ontMatchText()
	{
		ArrayList paraList = new ArrayList();
		ArrayList lt=new ArrayList();
		
		/*Դ����������ı�����*/
		//���������������
		packOntDesPara(paraList,true);
//		System.out.println("Դ����������ı�����");
		lt=new OntDes().getOntDes(paraList);
		sourceCnptTextDes=(TextDes[])lt.get(0);
		sourcePropTextDes=(TextDes[])lt.get(1);
		sourceInsTextDes=(TextDes[])lt.get(2);
		if (isSubProg){
			sourceCnptOtTextDes=(ArrayList[])lt.get(3);
			sourcePropOtTextDes=(ArrayList[])lt.get(4);
		}
		else{
			sourceFullOtTextDes=(ArrayList)lt.get(3);
		}

		
		/*Ŀ�걾��������ı�����*/
		//���������������
		packOntDesPara(paraList,false);
		/*Դ����������ı�����*/
//		System.out.println("Ŀ�걾��������ı�����");
		lt=new OntDes().getOntDes(paraList);
		targetCnptTextDes=(TextDes[])lt.get(0);
		targetPropTextDes=(TextDes[])lt.get(1);
		targetInsTextDes=(TextDes[])lt.get(2);
		if (isSubProg){
			targetCnptOtTextDes=(ArrayList[])lt.get(3);
			targetPropOtTextDes=(ArrayList[])lt.get(4);
		}
		else{
			targetFullOtTextDes=(ArrayList)lt.get(3);
		}
		
		/*����ƥ��*/
		packTextDocSimPara(paraList);
		lt=new TextDocSim().getOntTextSim(paraList,DISTINCT_DP_OP);
		
		simMxConcept = new double[sourceConceptNum][targetConceptNum];
		simMxProp = new double[sourcePropNum][targetPropNum];
		simMxDataProp = new double[sourceDataPropNum][targetDataPropNum];
		simMxObjProp = new double[sourceObjPropNum][targetObjPropNum];
		simMxIns = new double[sourceInsNum][targetInsNum];
		
		simMxConcept=(double[][])lt.get(0);
		simMxIns=(double[][])lt.get(1);
		if (DISTINCT_DP_OP){
			simMxDataProp=(double[][])lt.get(2);
			simMxObjProp=(double[][])lt.get(3);
		}
		else{
			simMxProp=(double[][])lt.get(2);
		}
		
		/*���ƾ���Ŀ��ӻ�*/
//		new SimDataVisual().visualize(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			new SimDataVisual().visualize(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			new SimDataVisual().visualize(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			new SimDataVisual().visualize(simMxProp,sourcePropNum,targetPropNum);
//		}
		
//		/*ǿ�����ƾ���ʵ��*/
//		Test ts=new Test();
//		ts.enSimMatrix(simMxConcept,sourceConceptNum,targetConceptNum);
//		ts.enSimMatrix(simMxProp,sourcePropNum,targetPropNum);
//		
//		/*��ʾǿ����ľ���*/
//		new SimDataVisual().visualize(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			new SimDataVisual().visualize(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			new SimDataVisual().visualize(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			new SimDataVisual().visualize(simMxProp,sourcePropNum,targetPropNum);
//		}
		
		//���㶯̬��ֵ
		DynamicThreshold tdSelector=new DynamicThreshold();
		/*�򵥹��Ʒ���*/
//		cnptSimThreshold=tdSelector.naiveThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.naiveThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.naiveThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.naiveThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		/*����ط���*/
//		cnptSimThreshold=tdSelector.maxEntropyThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.maxEntropyThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.maxEntropyThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.maxEntropyThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		 /* ��������ط�����÷�ֵ*/
			cnptSimThreshold=tdSelector.maxEntropyThresholdA(simMxConcept,sourceConceptNum,targetConceptNum);
			if (DISTINCT_DP_OP){
				dpSimThreshold=tdSelector.maxEntropyThresholdA(simMxDataProp,sourceDataPropNum,targetDataPropNum);
				opSimThreshold=tdSelector.maxEntropyThresholdA(simMxObjProp,sourceObjPropNum,targetObjPropNum);
			}
			else{
				propSimThreshold=tdSelector.maxEntropyThresholdA(simMxProp,sourcePropNum,targetPropNum);
			}
		
		 /* ����ostu������÷�ֵ*/
//		cnptSimThreshold=tdSelector.ostuThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.ostuThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.ostuThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.ostuThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		 /* ����mini error������÷�ֵ*/
//		cnptSimThreshold=tdSelector.miniErrorThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.miniErrorThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.miniErrorThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.miniErrorThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		
		 /* ����max correlation������÷�ֵ*/
//		cnptSimThreshold=tdSelector.maxCorrelationThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.maxCorrelationThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.maxCorrelationThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.maxCorrelationThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		 /* ����WP������÷�ֵ*/
//		cnptSimThreshold=tdSelector.maxWPThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.maxWPThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.maxWPThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.maxWPThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		
		//������ˣ��ü򵥵ķ���
		simMxConcept = new SimpleFilter().maxValueFilter(sourceConceptNum,
				targetConceptNum, simMxConcept, cnptSimThreshold);
		if (DISTINCT_DP_OP){
			simMxDataProp = new SimpleFilter().maxValueFilter(sourceDataPropNum,
					targetDataPropNum, simMxDataProp, dpSimThreshold);
			simMxObjProp = new SimpleFilter().maxValueFilter(sourceObjPropNum,
					targetObjPropNum, simMxObjProp, opSimThreshold);
		}
		else{
			simMxProp = new SimpleFilter().maxValueFilter(sourcePropNum,
					targetPropNum, simMxProp, propSimThreshold);
		}
		
		/*����ʵ�����ƾ���*/
		simMxIns = new SimpleFilter().maxValueFilter(sourceInsNum,targetInsNum, simMxIns, 0.3);
		
//		//������ˣ����ȶ������ķ���
//		simMxConcept = new StableMarriageFilter().run(simMxConcept,sourceConceptNum,targetConceptNum);
//		simMxIns = new StableMarriageFilter().run(simMxIns,sourceInsNum,targetInsNum);
//		if (DISTINCT_DP_OP){
//			simMxDataProp = new StableMarriageFilter().run(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			simMxObjProp = new StableMarriageFilter().run(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			simMxProp = new StableMarriageFilter().run(simMxProp,sourcePropNum,targetPropNum);
//		}
//		simMxIns = new StableMarriageFilter().run(simMxIns,sourceInsNum,targetInsNum);
		
		
		/*��¼���Ž����λ��*/
		/*ͬʱ�ж��Ƿ���Ҫ���ƶȴ���*/
		int gotSim=0;//ʵ�ʵõ���ӳ����Ŀ
		int theorySim=Math.min(sourceConceptNum,targetConceptNum)+
		           Math.min(sourcePropNum,targetPropNum);//�����ϵ�ӳ����Ŀ
		sourceCnptOkSimPos=new HashSet();
		targetCnptOkSimPos=new HashSet();
		for (int i=0;i<sourceConceptNum;i++){
			for (int j=0;j<targetConceptNum;j++){
				if (simMxConcept[i][j]>cnptSimThreshold){
					gotSim++;
				}
				if (simMxConcept[i][j]>OKSIM){
					sourceCnptOkSimPos.add(i);
					targetCnptOkSimPos.add(j);
				}
			}
		}
		sourcePropOkSimPos=new HashSet();
		targetPropOkSimPos=new HashSet();
		for (int i=0;i<sourcePropNum;i++){
			for (int j=0;j<targetPropNum;j++){
				if (simMxProp[i][j]>propSimThreshold){
					gotSim++;
				}
				if (simMxProp[i][j]>OKSIM){
					sourcePropOkSimPos.add(i);
					targetPropOkSimPos.add(j);
				}
			}
		}
		
		isNeedSimProg=false;
		if ((double)gotSim/(double)theorySim<0.8 && (double)gotSim/(double)theorySim>0.2){
			//��Ҫ���ƶȴ���
			isNeedSimProg=true;
		}
		
//		/*���ƾ���Ŀ��ӻ�*/
//		new SimDataVisual().visualize(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			new SimDataVisual().visualize(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			new SimDataVisual().visualize(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			new SimDataVisual().visualize(simMxProp,sourcePropNum,targetPropNum);
//		}
		
		/*�Թ��Ǻ�����ƾ���Ϊ���룬
		 * ��������ط�����÷�ֵ*/
//		cnptSimThreshold=tdSelector.maxEntropyThresholdA(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.maxEntropyThresholdA(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.maxEntropyThresholdA(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.maxEntropyThresholdA(simMxProp,sourcePropNum,targetPropNum);
//		}
		 /* ����ostu������÷�ֵ*/
//			cnptSimThreshold=tdSelector.ostuThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//			if (DISTINCT_DP_OP){
//				dpSimThreshold=tdSelector.ostuThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//				opSimThreshold=tdSelector.ostuThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//			}
//			else{
//				propSimThreshold=tdSelector.ostuThreshold(simMxProp,sourcePropNum,targetPropNum);
//			}
		 /* ����mini error������÷�ֵ*/
//		cnptSimThreshold=tdSelector.miniErrorThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.miniErrorThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.miniErrorThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.miniErrorThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
//		 /* ����max correlation������÷�ֵ*/
//		cnptSimThreshold=tdSelector.maxCorrelationThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.maxCorrelationThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.maxCorrelationThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.maxCorrelationThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		
		/*�ۺϹ���ǰ��ķ�ֵ*/
//		cnptSimThreshold=(cnptSimThreshold+tdSelector.maxEntropyThresholdA(simMxConcept,sourceConceptNum,targetConceptNum))/2.0;
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=(dpSimThreshold+tdSelector.maxEntropyThresholdA(simMxDataProp,sourceDataPropNum,targetDataPropNum))/2.0;
//			opSimThreshold=(opSimThreshold+tdSelector.maxEntropyThresholdA(simMxObjProp,sourceObjPropNum,targetObjPropNum))/2.0;
//		}
//		else{
//			propSimThreshold=(propSimThreshold+tdSelector.maxEntropyThresholdA(simMxProp,sourcePropNum,targetPropNum))/2.0;
//		}
		 /* ����WP������÷�ֵ*/
//		cnptSimThreshold=tdSelector.maxWPThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.maxWPThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.maxWPThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.maxWPThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
//		cnptSimThreshold=0.32;
//		propSimThreshold=0.20;
				
}

	/**********************
	 * ���ڽṹ��ƥ��
	 * 1.����Informative graph
	 * 2.��ȡ�ṹ
	 * 3.����ṹ���ƶ�
	 ********************/
	private void ontMatchStru()
	{
		
		//�ı�ӳ�����
//		ComputeLiteralMapping();
       	
		//�ṹӳ�����
       	//----------Structure Methods Testing-------------------
//		OntGraph OntG = new OntGraph();
//        OntG.SetConceptPara(sourceConceptNum, targetConceptNum, sourceConceptName, targetConceptName);
//        OntG.SetPropertyPara(sourcePropNum, targetPropNum, sourcePropName, targetPropName);
//        OntG.SetInstancePara(sourceInsNum, targetInsNum, sourceInsName, targetInsName);
        //-------------------------------------------------------
        
        
        //ת��ΪBipartite Graph��ƥ���㷨
//        myOntoGraph.source_Graph = myOntoGraph.Onto2BiptGraph(m_source, true);
//        myOntoGraph.target_Graph = myOntoGraph.Onto2BiptGraph(m_target, false);
//        myOntoGraph.ComputeSGMapping_BiptGraph();
        
        //Informative Graph��ƥ���㷨
//        OntG.source_Graph = OntG.Onto2Graph(m_source, true);
//        OntG.target_Graph = OntG.Onto2Graph(m_target, false);
//        OntG.ConsSGInformative(OntG.source_Graph,true);
//        OntG.ConsSGInformative(OntG.target_Graph,false);
//        OntG.ComputeSGMapping_Informative(OntG.sourceSubGraph, OntG.targetSubGraph);
        
        //δ�Ż���ȫͼƥ���㷨
//        myOntoGraph.source_Graph = myOntoGraph.Onto2Graph(m_source, true);
//        myOntoGraph.target_Graph = myOntoGraph.Onto2Graph(m_target, false);
//        myOntoGraph.ComputeSGMapping_WholeGraph();
        
        //�򵥷ֿ��ȫͼƥ���㷨
//      myOntoGraph.source_Graph = myOntoGraph.Onto2Graph(m_source, true);
//      myOntoGraph.target_Graph = myOntoGraph.Onto2Graph(m_target, false);
//      myOntoGraph.ComputeSGMapping_WholeGraph_Block();
        

//        myOntoGraph.ConsSGNeighbor(myOntoGraph.source_Graph,myOntoGraph.target_Graph,2);
//		myOntoGraph.ComputeSGMapping_Neighbor(myOntoGraph.sourceSubGraph, myOntoGraph.targetSubGraph);
        
        
        //--------------------------------------------------------

//		CombineMultiMappingResults();
	}
	
	private void combineResult()
	{
		/*�ϲ���ͼ�������������ƶȺ����ƶȴ���������ƶȽ��*/
		/*����1.��򵥵ĺϲ�:ȡ��ֵ*/
//		for (int i=0;i<sourceConceptNum;i++){
//			for (int j=0;j<targetConceptNum;j++){
//				simMxConcept[i][j]=(simMxConcept[i][j]+pgSimMxConcept[i][j])/2.0;
//			}
//		}
//		for (int i=0;i<sourcePropNum;i++){
//			for (int j=0;j<targetPropNum;j++){
//				simMxProp[i][j]=(simMxProp[i][j]+pgSimMxProp[i][j])/2.0;
//			}
//		}
		
		/*����2.ֻ�������ӵ���ƥ��*/
		
		/*����3.�ۺϿ��ǵĺϲ�����*/
		/*�������*/
		//1.���Aij>=t��ȷ��Aij,ͬʱ�޸Ķ�Ӧ�Ĵ�������Bij
		for (int i=0;i<sourceConceptNum;i++){
			for (int j=0;j<targetConceptNum;j++){
				if (simMxConcept[i][j]>=OKSIM){
					//B��i��
					for (int k=0;k<targetConceptNum;k++){
						if (k!=j){
							pgSimMxConcept[i][k]=0;
						}
					}
					//B��j��
					for (int k=0;k<sourceConceptNum;k++){
						if (k!=i){
							pgSimMxConcept[k][j]=0;
						}
					}
				}
			}
		}
		//2.���Aij<t������ȷ��Aij,���촫������Bij
		//2.1Bij<t��Bij�϶�������
		for (int i=0;i<sourceConceptNum;i++){
			for (int j=0;j<targetConceptNum;j++){
				if (pgSimMxConcept[i][j]>0 && pgSimMxConcept[i][j]<BADSIM){
					pgSimMxConcept[i][j]=0;
				}
			}
		}
		
		//�ϲ�������
		for (int i = 0; i < sourceConceptNum; i++) {
			for (int j = 0; j < targetConceptNum; j++) {
				if (simMxConcept[i][j]<OKSIM){
					simMxConcept[i][j] = (simMxConcept[i][j] + pgSimMxConcept[i][j]) / 2.0;
				}								
			}
		}
		
		/*��������*/
		//1.���Aij>=t��ȷ��Aij,ͬʱ�޸Ķ�Ӧ�Ĵ�������Bij
		for (int i=0;i<sourcePropNum;i++){
			for (int j=0;j<targetPropNum;j++){
				if (simMxProp[i][j]>=OKSIM){
					//B��i��
					for (int k=0;k<targetPropNum;k++){
						if (k!=j){
							pgSimMxProp[i][k]=0;
						}
					}
					//B��j��
					for (int k=0;k<sourcePropNum;k++){
						if (k!=i){
							pgSimMxProp[k][j]=0;
						}
					}
				}
			}
		}
		//2.���Aij<t������ȷ��Aij,���촫������Bij
		//2.1Bij<t��Bij�϶�������
		for (int i=0;i<sourcePropNum;i++){
			for (int j=0;j<targetPropNum;j++){
				if (pgSimMxProp[i][j]>0 && pgSimMxProp[i][j]<BADSIM){
					pgSimMxProp[i][j]=0;
				}
			}
		}
		
		//�ϲ�������
		for (int i = 0; i < sourcePropNum; i++) {
			for (int j = 0; j < targetPropNum; j++) {
				if (simMxProp[i][j]<OKSIM){
					simMxProp[i][j] = (simMxProp[i][j] + pgSimMxProp[i][j]) / 2.0;
				}				
			}
		}
		
		//������ˣ����ȶ������ķ���
		simMxConcept = new StableMarriageFilter().run(simMxConcept,sourceConceptNum,targetConceptNum);
		if (DISTINCT_DP_OP){
			simMxDataProp = new StableMarriageFilter().run(simMxDataProp,sourceDataPropNum,targetDataPropNum);
			simMxObjProp = new StableMarriageFilter().run(simMxObjProp,sourceObjPropNum,targetObjPropNum);
		}
		else{
			simMxProp = new StableMarriageFilter().run(simMxProp,sourcePropNum,targetPropNum);
		}
		
		//���¼��㶯̬��ֵ
		DynamicThreshold tdSelector=new DynamicThreshold();
		 /* ��������ط�����÷�ֵ*/
		cnptSimThreshold=tdSelector.maxEntropyThresholdA(simMxConcept,sourceConceptNum,targetConceptNum);
		if (DISTINCT_DP_OP){
			dpSimThreshold=tdSelector.maxEntropyThresholdA(simMxDataProp,sourceDataPropNum,targetDataPropNum);
			opSimThreshold=tdSelector.maxEntropyThresholdA(simMxObjProp,sourceObjPropNum,targetObjPropNum);
		}
		else{
			propSimThreshold=tdSelector.maxEntropyThresholdA(simMxProp,sourcePropNum,targetPropNum);
		}
		

		cnptSimThreshold=0.05;
		propSimThreshold=0.05;
	}
	
	public void showResult(boolean flag)
	{
		//�������ƾ�������ӳ����
		mappingResult = new MapRecord[Math.max(sourceConceptNum,targetConceptNum)+
		                              Math.max(sourceDataPropNum,targetDataPropNum)+
		                              Math.max(sourceObjPropNum,targetObjPropNum)];
		if (DISTINCT_DP_OP){
			generateMapping(simMxConcept,simMxDataProp,simMxObjProp);
		}
		else{
			generateMapping(simMxConcept,simMxProp);
		}
		
		//��ʾ���
		if (!flag) return;
		for(int i=0;i<mappingNum;i++)
		{
			mappingResult[i].show();
		}
	}
	
	/**********************
	 * ����������ַ�ʽд���ļ���
	 * 1.��ͨ�ı��ļ�
	 * 2.xml�ļ�
	 * Ŀ���ļ�����target�����Ŀ¼
	 *********************/
	public void saveResult()
	{
		MappingFile mapFile=new MappingFile();
		if (lilyFileName.length()==0){
			lilyFileName="lily";
		}
		System.out.println("Matching Result File:");
		System.out.println(mapFile.getOntPath(targetOntFile)+lilyFileName+".rdf"+" and "+ lilyFileName+".txt");
		//��ͨ�ı��ļ��ķ�ʽ
		mapFile.save2txt(sourceOntFile,targetOntFile,mappingNum,mappingResult,lilyFileName+".txt");
		//XML�ļ��ķ�ʽ
		mapFile.setBaseURI(sourceBaseURI,targetBaseURI);
		mapFile.save2rdf(sourceOntFile,targetOntFile,mappingNum,mappingResult,lilyFileName+".rdf");		
	}
	
	/**********************
	 * ����ӳ������
	 * ����ӳ������������
	 * ͨ���ͻ�׼����Ƚϵõ����
	 *********************/
	private void evaluate()
	{
		ArrayList list = new ArrayList();
		MapRecord[] refMapResult = null;
		int refMapNum=0;
		if (refalignFile==null || refalignFile.length()==0){
			return;			
		}
		//������׼���
		try {
			list = new MappingFile().read4xml(refalignFile);
			refMapNum = ((Integer)list.get(0)).intValue();
			refMapResult = new MapRecord[refMapNum];
			refMapResult = (MapRecord[])((ArrayList)list.get(1)).toArray(new MapRecord[0]);
		} catch (MalformedURLException e) {
			System.out.println("Can't open refalign result file!"+e.toString());
		} catch (DocumentException e) {
			System.out.println("Can't open refalign result file!"+e.toString());
		}
		//�����������
		System.out.println("Evaluation:");
		list = new EvaluateMapping().getEvaluation(refMapNum,refMapResult,mappingNum,mappingResult);
		this.precision=((Double)list.get(0)).doubleValue();
		this.recall=((Double)list.get(1)).doubleValue();
		this.f1Measure=((Double)list.get(2)).doubleValue();
	}
	
	/***************
	 * �������ɵĽ���ļ�
	 ***************/
	public void evaluate(String lilyFile,String refFile)
	{
		ArrayList list = new ArrayList();
		MapRecord[] refMapResult = null;
		int refMapNum=0;
		MapRecord[] lilyMapResult = null;
		int lilyMapNum=0;
		if (refFile.length()==0){
			return;			
		}
		//������׼���
		try {
			list = new MappingFile().read4xml(refFile);
			refMapNum = ((Integer)list.get(0)).intValue();
			refMapResult = new MapRecord[refMapNum];
			refMapResult = (MapRecord[])((ArrayList)list.get(1)).toArray(new MapRecord[0]);
		} catch (MalformedURLException e) {
			System.out.println("Can't open refalign result file!"+e.toString());
		} catch (DocumentException e) {
			System.out.println("Can't open refalign result file!"+e.toString());
		}
		
		//��������õ����
		try {
			list = new MappingFile().read4xml(lilyFile);
			lilyMapNum = ((Integer)list.get(0)).intValue();
			lilyMapResult = new MapRecord[lilyMapNum];
			lilyMapResult = (MapRecord[])((ArrayList)list.get(1)).toArray(new MapRecord[0]);
		} catch (MalformedURLException e) {
			System.out.println("Can't open lily result file!"+e.toString());
		} catch (DocumentException e) {
			System.out.println("Can't open lily result file!"+e.toString());
		}
		
		//�����������
		list = new EvaluateMapping().getEvaluation(refMapNum,refMapResult,lilyMapNum,lilyMapResult);
		this.precision=((Double)list.get(0)).doubleValue();
		this.recall=((Double)list.get(1)).doubleValue();
		this.f1Measure=((Double)list.get(2)).doubleValue();
	}
	
	/**********************
	 * �������ﲢ��һ��ֻ����1-1��ӳ�䣬����ֻҪ���ƾ����е�Ԫ�ز�Ϊ0��
	 *����Ϊ�Ƿ���Ҫ���ӳ��
	 *********************/
	public void generateMapping(double[][] simMxC,double[][] simMxDP, double[][] simMxOp)
	{
		int cMappingNum,dpMappingNum,opMappingNum;//�ֱ��¼����ӳ�����Ŀ
		
		mappingNum = 0;
		//����ӳ����
		cMappingNum = 0;
		for(int i=0;i<sourceConceptNum;i++)
			for (int j=0;j<targetConceptNum;j++)
			{
				if (simMxC[i][j]>cnptSimThreshold )
				{
	   				mappingResult[mappingNum] = new MapRecord();
	   				mappingResult[mappingNum].sourceLabel = new String (sourceConceptName[i]);
	   				mappingResult[mappingNum].targetLabel = new String (targetConceptName[j]);
	   				mappingResult[mappingNum].similarity = simMxC[i][j];
	   				mappingResult[mappingNum].relationType = EQUALITY;
	   				mappingNum++;
	   				cMappingNum++;
				}
			}
//		System.out.println("cMappingNum:"+cMappingNum);
		//DatatypePropertyӳ����
		dpMappingNum = 0;
		for(int i=0;i<sourceDataPropNum;i++)
			for (int j=0;j<targetDataPropNum;j++)
			{
				if (simMxDP[i][j]>dpSimThreshold)
				{
	   				mappingResult[mappingNum] = new MapRecord();
	   				mappingResult[mappingNum].sourceLabel = new String (sourceDataPropName[i]);
	   				mappingResult[mappingNum].targetLabel = new String (targetDataPropName[j]);
	   				mappingResult[mappingNum].similarity = simMxDP[i][j];
	   				mappingResult[mappingNum].relationType = EQUALITY;
	   				mappingNum++;
	   				dpMappingNum++;
				}
			}
//		System.out.println("dpMappingNum:"+dpMappingNum);
		//ObjectPropertyӳ����
		opMappingNum = 0;
		for(int i=0;i<sourceObjPropNum;i++)
			for (int j=0;j<targetObjPropNum;j++)
			{
				if (simMxOp[i][j]>opSimThreshold)
				{
	   				mappingResult[mappingNum] = new MapRecord();
	   				mappingResult[mappingNum].sourceLabel = new String (sourceObjPropName[i]);
	   				mappingResult[mappingNum].targetLabel = new String (targetObjPropName[j]);
	   				mappingResult[mappingNum].similarity = simMxOp[i][j];
	   				mappingResult[mappingNum].relationType = EQUALITY;
	   				mappingNum++;
	   				opMappingNum++;
				}
			}
		System.out.println("opMappingNum:"+opMappingNum);
	}
	
	public void generateMapping(double[][] simMxC,double[][] simMxP)
	{
		int cMappingNum,pMappingNum;//�ֱ��¼����ӳ�����Ŀ
		
		mappingNum = 0;
		//����ӳ����
		cMappingNum = 0;
		for(int i=0;i<sourceConceptNum;i++)
			for (int j=0;j<targetConceptNum;j++)
			{
				if (simMxC[i][j]>cnptSimThreshold)
				{
	   				mappingResult[mappingNum] = new MapRecord();
	   				mappingResult[mappingNum].sourceLabel = new String (sourceConceptName[i]);
	   				mappingResult[mappingNum].targetLabel = new String (targetConceptName[j]);
	   				mappingResult[mappingNum].similarity = simMxC[i][j];
	   				mappingResult[mappingNum].relationType = EQUALITY;
	   				mappingNum++;
	   				cMappingNum++;
				}
			}
//		System.out.println("cMappingNum:"+cMappingNum);
		//Propertyӳ����
		pMappingNum = 0;
		for(int i=0;i<sourcePropNum;i++)
			for (int j=0;j<targetPropNum;j++)
			{
				if (simMxP[i][j]>propSimThreshold)
				{
	   				mappingResult[mappingNum] = new MapRecord();
	   				mappingResult[mappingNum].sourceLabel = new String (sourcePropName[i]);
	   				mappingResult[mappingNum].targetLabel = new String (targetPropName[j]);
	   				mappingResult[mappingNum].similarity = simMxP[i][j];
	   				mappingResult[mappingNum].relationType = EQUALITY;
	   				mappingNum++;
	   				pMappingNum++;
				}
			}
//		System.out.println("pMappingNum:"+pMappingNum);		
	}
	
	/**********************
	 * ������������
	 *********************/
	@SuppressWarnings("unchecked")
	private void packOntGraphPara(ArrayList list, boolean flag)
	{
		if (flag){
			//Դ����
			list.add(0,m_source);
			list.add(1,sourceConceptNum);
			list.add(2,sourcePropNum);
			list.add(3,sourceDataPropNum);
			list.add(4,sourceObjPropNum);
			list.add(5,sourceInsNum);
			list.add(6,sourceConceptName);
			list.add(7,sourcePropName);
			list.add(8,sourceDataPropName);
			list.add(9,sourceObjPropName);
			list.add(10,sourceInsName);
			list.add(11,sourceBaseURI);
			
			list.add(12,sourceFullConceptNum);
			list.add(13,sourceFullPropNum);
			list.add(14,sourceFullDataPropNum);
			list.add(15,sourceFullObjPropNum);
			list.add(16,sourceFullInsNum);
			list.add(17,sourceFullConceptName);
			list.add(18,sourceFullPropName);
			list.add(19,sourceFullDataPropName);
			list.add(20,sourceFullObjPropName);
			list.add(21,sourceFullInsName);
			
			list.add(22,sourceAnonCnpt);
			list.add(23,sourceAnonProp);
			list.add(24,sourceAnonIns);
		}
		else{
			//Ŀ�걾��
			list.add(0,m_target);
			list.add(1,targetConceptNum);
			list.add(2,targetPropNum);
			list.add(3,targetDataPropNum);
			list.add(4,targetObjPropNum);
			list.add(5,targetInsNum);
			list.add(6,targetConceptName);
			list.add(7,targetPropName);
			list.add(8,targetDataPropName);
			list.add(9,targetObjPropName);
			list.add(10,targetInsName);
			list.add(11,targetBaseURI);
			
			list.add(12,targetFullConceptNum);
			list.add(13,targetFullPropNum);
			list.add(14,targetFullDataPropNum);
			list.add(15,targetFullObjPropNum);
			list.add(16,targetFullInsNum);
			list.add(17,targetFullConceptName);
			list.add(18,targetFullPropName);
			list.add(19,targetFullDataPropName);
			list.add(20,targetFullObjPropName);
			list.add(21,targetFullInsName);
			
			list.add(22,targetAnonCnpt);
			list.add(23,targetAnonProp);
			list.add(24,targetAnonIns);
		}
		
		//��ͼ��С
		list.add(25,Semantic_SubGraph_Size);
	}
	/**********************
	 * ������������
	 *********************/
	@SuppressWarnings("unchecked")
	private void packOntDesPara(ArrayList list, boolean flag)
	{
		if (flag){
			//Դ����
			list.add(0,m_source);
			list.add(1,sourceConceptNum);
			list.add(2,sourcePropNum);
			list.add(3,sourceInsNum);
			
			list.add(4,sourceConceptName);
			list.add(5,sourcePropName);

			list.add(6,sourceBaseURI);
			
			list.add(7,sourceFullConceptNum);
			list.add(8,sourceFullPropNum);
			list.add(9,sourceFullInsNum);
			
			list.add(10,sourceFullConceptName);
			list.add(11,sourceFullPropName);
			list.add(12,sourceFullInsName);
			
			list.add(13,sourceCnptSubG);
			list.add(14,sourcePropSubG);
			
			list.add(15,sourceAnonCnpt);
			list.add(16,sourceAnonProp);
			list.add(17,sourceAnonIns);
			
			list.add(18,sourceInsName);
			
			list.add(19,sourceStmList);
			list.add(20,isSubProg);			
		}
		else{
			//Ŀ�걾��
			list.add(0,m_target);
			list.add(1,targetConceptNum);
			list.add(2,targetPropNum);
			list.add(3,targetInsNum);
			
			list.add(4,targetConceptName);
			list.add(5,targetPropName);

			list.add(6,targetBaseURI);
			
			list.add(7,targetFullConceptNum);
			list.add(8,targetFullPropNum);
			list.add(9,targetFullInsNum);
			
			list.add(10,targetFullConceptName);
			list.add(11,targetFullPropName);
			list.add(12,targetFullInsName);
			
			list.add(13,targetCnptSubG);
			list.add(14,targetPropSubG);
			
			list.add(15,targetAnonCnpt);
			list.add(16,targetAnonProp);
			list.add(17,targetAnonIns);
			
			list.add(18,targetInsName);
			
			list.add(19,targetStmList);
			list.add(20,isSubProg);			
		}			
	}
	/**********************
	 * ������������
	 *********************/
	@SuppressWarnings("unchecked")
	private void packTextDocSimPara(ArrayList list) 
	{
		//Դ����
		list.add(0, sourceConceptNum);
		list.add(1, sourcePropNum);
		list.add(2, sourceDataPropNum);
		list.add(3, sourceObjPropNum);

		list.add(4, sourceConceptName);
		list.add(5, sourcePropName);
		list.add(6, sourceDataPropName);
		list.add(7, sourceObjPropName);
		
		list.add(8, sourceCnptTextDes);
		list.add(9, sourcePropTextDes);
		
		//Ŀ�걾��
		list.add(10, targetConceptNum);
		list.add(11, targetPropNum);
		list.add(12, targetDataPropNum);
		list.add(13, targetObjPropNum);

		list.add(14, targetConceptName);
		list.add(15, targetPropName);
		list.add(16, targetDataPropName);
		list.add(17, targetObjPropName);
		
		list.add(18, targetCnptTextDes);
		list.add(19, targetPropTextDes);
		
		//ʵ����Ϣ
		list.add(20, sourceInsNum);
		list.add(21, sourceInsName);
		list.add(22, sourceInsTextDes);
		list.add(23, targetInsNum);
		list.add(24, targetInsName);
		list.add(25, targetInsTextDes);				
	}
	
	/**********************
	 * ������������
	 *********************/
	@SuppressWarnings("unchecked")
	private void packSubSimPgPara(ArrayList list) 
	{
		//ģ��
		list.add(0, m_source);
		list.add(1, m_target);
		
		//Դ����
		list.add(2, sourceConceptNum);
		list.add(3, sourcePropNum);
		list.add(4, sourceInsNum);
		list.add(5, sourceConceptName);
		list.add(6, sourcePropName);
		list.add(7, sourceInsName);		
		list.add(8, sourceCnptSubG);
		list.add(9, sourcePropSubG);		
		list.add(10, sourceBaseURI);
		
		//Ŀ�걾��
		list.add(11, targetConceptNum);
		list.add(12, targetPropNum);
		list.add(13, targetInsNum);
		list.add(14, targetConceptName);
		list.add(15, targetPropName);
		list.add(16, targetInsName);		
		list.add(17, targetCnptSubG);
		list.add(18, targetPropSubG);		
		list.add(19, targetBaseURI);
		
		//���ƶȾ���
		list.add(20, simMxConcept);
		list.add(21, simMxProp);
		list.add(22, simMxIns);
		
		//����full������Ϣ
		list.add(23,sourceAnonCnpt);
		list.add(24,sourceAnonProp);
		list.add(25,sourceAnonIns);
		list.add(26,targetAnonCnpt);
		list.add(27,targetAnonProp);
		list.add(28,targetAnonIns);

		//�Ѱ���ȷ�����ƶȵ�λ�ü���
		list.add(29,sourceCnptOkSimPos);
		list.add(30,targetCnptOkSimPos);
		list.add(31,sourcePropOkSimPos);
		list.add(32,targetPropOkSimPos);
	}
	
	@SuppressWarnings("unchecked")
	private void packFullSimPgPara(ArrayList list) 
	{
		//ģ��
		list.add(0, m_source);
		list.add(1, m_target);
		
		//Դ����
		list.add(2, sourceConceptNum);
		list.add(3, sourcePropNum);
		list.add(4, sourceInsNum);
		list.add(5, sourceConceptName);
		list.add(6, sourcePropName);
		list.add(7, sourceInsName);		
		list.add(8, sourceStmList);
		list.add(9, sourceBaseURI);
		
		//Ŀ�걾��
		list.add(10, targetConceptNum);
		list.add(11, targetPropNum);
		list.add(12, targetInsNum);
		list.add(13, targetConceptName);
		list.add(14, targetPropName);
		list.add(15, targetInsName);		
		list.add(16, targetStmList);
		
		list.add(17, targetBaseURI);
		
		//���ƶȾ���
		list.add(18, simMxConcept);
		list.add(19, simMxProp);
		list.add(20, simMxIns);
		
		//����full������Ϣ
		list.add(21,sourceAnonCnpt);
		list.add(22,sourceAnonProp);
		list.add(23,sourceAnonIns);
		list.add(24,targetAnonCnpt);
		list.add(25,targetAnonProp);
		list.add(26,targetAnonIns);
	}
}

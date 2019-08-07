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
 * 基于子图抽取方法的主类。控制整个方法的运行过程
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
 * 主要类。子图方法的起点
 ********************/
public class Rclm {
	//源和目标本体的文件
	public String sourceOntFile;
	public String targetOntFile;
	//基准映射文件
	public String refalignFile;
	//源和目标本体的model
	public OntModel m_source;
	public OntModel m_target;
	//概念数目
	public int sourceConceptNum;
	public int targetConceptNum;
	//属性数目
	public int sourcePropNum;
	public int sourceDataPropNum;
	public int sourceObjPropNum;
	public int targetPropNum;
	public int targetDataPropNum;
	public int targetObjPropNum;
	//实例数目
	public int sourceInsNum;
	public int targetInsNum;
	//概念名
	public String[] sourceConceptName;
	public String[] targetConceptName;
	//属性名
	public String[] sourcePropName;
	public String[] sourceDataPropName;
	public String[] sourceObjPropName;
	public String[] targetPropName;
	public String[] targetDataPropName;
	public String[] targetObjPropName;
	//实例名
	public String[] sourceInsName;
	public String[] targetInsName;
	
	//匿名资源
	ArrayList sourceAnonCnpt;
	ArrayList sourceAnonProp;
	ArrayList sourceAnonIns;	
	ArrayList targetAnonCnpt;
	ArrayList targetAnonProp;
	ArrayList targetAnonIns;	
	
	//不局限于baseURI下的本体元素
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
	
	//子图信息
	public ConceptSubGraph[] sourceCnptSubG;
	public PropertySubGraph[] sourcePropSubG;
	public ConceptSubGraph[] targetCnptSubG;
	public PropertySubGraph[] targetPropSubG;
	
	//全图信息,三元组形式
	public ArrayList sourceStmList;
	public ArrayList targetStmList;
	
	//文本描述信息
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
	
	//相似矩阵
	public double[][] simMxConcept;
	public double[][] simMxProp;
	public double[][] simMxDataProp;
	public double[][] simMxObjProp;
	public double[][] simMxIns;
	
	//相似度传播后的相似矩阵
	public double[][] pgSimMxConcept;
	public double[][] pgSimMxProp;
	
	//传播模式
	public boolean isSubProg;
	
	//映射结果数
	public int mappingNum;
	//映射结果
	public MapRecord[] mappingResult;
	//结果评价
	public double precision;
	public double recall;
	public double f1Measure;
	//文本相似和子图相似的权重
	public double graphWeight;
	public double literalWeight;
	//base URI
	public String sourceBaseURI;
	public String targetBaseURI;
	//相似度阀值
	public double cnptSimThreshold;//概念相似阀值
	public double dpSimThreshold;//Datatype Property相似阀值
	public double opSimThreshold;//Object Property相似阀值
	public double propSimThreshold;//Property相似阀值
	//TextMatch可信结果位置
	public Set sourceCnptOkSimPos;
	public Set sourcePropOkSimPos;
	public Set targetCnptOkSimPos;
	public Set targetPropOkSimPos;
	
	//是否需要相似度传播
	public boolean isNeedSimProg;
	
	//结果的文件名
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
	 * function:用子图的方法来进行本体映射
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
	
	//初始化，主要是基本的参数设置
	public void init()
	{
		graphWeight = 0.0;
		literalWeight = 1.0;
		isSubProg=true;//true:相似度按照子图传播
		
		sourceCnptSubG=new ConceptSubGraph[sourceConceptNum];
		sourcePropSubG=new PropertySubGraph[sourcePropNum];
		targetCnptSubG=new ConceptSubGraph[targetConceptNum];
		targetPropSubG=new PropertySubGraph[targetPropNum];
	}
	
	//设置本体文件
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
	
	//从外部INI文件读取初始参数
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
	
	//解析本体
	public void parseOnt()
	{
		System.out.println("Parsing ontologies...");
		OWLOntParse ontParse = new OWLOntParse();
		ArrayList list = new ArrayList();
    	
    	//源本体----------------------------------------------
    	m_source = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    	//The ontology file information
        m_source.getDocumentManager().addAltEntry( "http://pengwang/", sourceOntFile);
        //Read the reference ontology file
        ontParse.readOntFile(m_source,sourceOntFile);
        
        //源本体的base URI
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
       	
       	/*不局限于baseURI的本体信息*/
       	ArrayList fullOntlist = ontParse.getFullOntInfo(m_source);
		//概念信息
		list = (ArrayList)fullOntlist.get(0);
       	sourceFullConceptNum = ((Integer)list.get(0)).intValue();
       	sourceFullConceptName = new OntClass[sourceFullConceptNum];
       	sourceFullConceptName = (OntClass[])((ArrayList)list.get(1)).toArray(new OntClass[0]);
       	//属性信息
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
       	//实例信息
       	list = (ArrayList)fullOntlist.get(4);
       	sourceFullInsNum = ((Integer)list.get(0)).intValue();
       	sourceFullInsName = new Individual[sourceFullInsNum];
       	sourceFullInsName = (Individual[])((ArrayList)list.get(1)).toArray(new Individual[0]);
    	
       	//匿名资源
       	sourceAnonCnpt=new ArrayList();
    	sourceAnonProp=new ArrayList();
    	sourceAnonIns=new ArrayList();	
    	list = ontParse.getOntAnonInfo(m_source);
    	sourceAnonCnpt=(ArrayList)list.get(0);
    	sourceAnonProp=(ArrayList)list.get(1);
    	sourceAnonIns=(ArrayList)list.get(2);
       	
       	//目标本体---------------------------------------------
    	m_target = ModelFactory.createOntologyModel( OntModelSpec.OWL_MEM, null );
    	//The ontology file information
    	m_target.getDocumentManager().addAltEntry( "http://LiSun/", targetOntFile);

        //Read the target ontology file    	
        ontParse.readOntFile(m_target,targetOntFile);
        
        //源本体的base URI
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
       	
    	/*不局限于baseURI的本体信息*/
       	fullOntlist = ontParse.getFullOntInfo(m_target);
		//概念信息
		list = (ArrayList)fullOntlist.get(0);
		targetFullConceptNum = ((Integer)list.get(0)).intValue();
		targetFullConceptName = new OntClass[targetFullConceptNum];
		targetFullConceptName = (OntClass[])((ArrayList)list.get(1)).toArray(new OntClass[0]);
       	//属性信息
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
       	//实例信息
       	list = (ArrayList)fullOntlist.get(4);
       	targetFullInsNum = ((Integer)list.get(0)).intValue();
       	targetFullInsName = new Individual[targetFullInsNum];
       	targetFullInsName = (Individual[])((ArrayList)list.get(1)).toArray(new Individual[0]);
       	
       	//匿名资源
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
		//匹配计算
        long start = System.currentTimeMillis();//开始计时
        //语义结构抽取
        System.out.println("Constructing Semantic Subgraphs...");
        reConsSemInf();
        //文本匹配
        System.out.println("Matching...");
        ontMatchText();
        //结构匹配
        ontMatchStru();
		long end = System.currentTimeMillis();//结束计时
		long costtime = end - start;//统计算法时间
//		System.out.println("简单文本匹配算法时间："+(double)costtime/1000.+"秒");
		
		if (isNeedSimProg){
	        /*相似度传播*/
			System.out.println("Similarity Propagating...");
			simPropagation();
			//合并结果			
			combineResult();
		}

		//显示映射结果
		showResult(false);
		//保存映射结果
		saveResult();
    } 
	
	/**********************
	 * 以子图为基础的相似度传播
	 ********************/
	private void subSimPropagation() {
		pgSimMxConcept=new double[sourceConceptNum][targetConceptNum];
		pgSimMxProp=new double[sourcePropNum][targetPropNum];
		ArrayList paraList = new ArrayList();
		ArrayList lt=new ArrayList();
		
		/*计算子图中其它元素的缺省相似度*/
//		System.out.println("概念子图中其它元素的缺省相似度");
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
//		System.out.println("属性子图中其它元素的缺省相似度");
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
		
		/*源本体的语义文本描述*/
		//构造输入参数链表
		packSubSimPgPara(paraList);
		paraList.add(29,cnptOtSim);
		paraList.add(30,propOtSim);
		
		System.out.println("相似度传播");
		lt=new SubSimPropagation().ontSimPg(paraList);
		pgSimMxConcept=(double[][])lt.get(0);
		pgSimMxProp=(double[][])lt.get(1);
	}
	
	private void cbSubSimPropagation() {
		pgSimMxConcept=new double[sourceConceptNum][targetConceptNum];
		pgSimMxProp=new double[sourcePropNum][targetPropNum];
		ArrayList paraList = new ArrayList();
		ArrayList lt=new ArrayList();
		
		/*计算子图中其它元素的缺省相似度*/
//		System.out.println("概念子图中其它元素的缺省相似度");
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
//		System.out.println("属性子图中其它元素的缺省相似度");
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
		
		/*源本体的语义文本描述*/
		//构造输入参数链表
		packSubSimPgPara(paraList);
		paraList.add(33,cnptOtSim);
		paraList.add(34,propOtSim);
		
//		System.out.println("相似度传播");
		lt=new CbSubSimPropagation().ontSimPg(paraList);
		pgSimMxConcept=(double[][])lt.get(0);
		pgSimMxProp=(double[][])lt.get(1);
	}
	
	/**********************
	 * 以全局图为基础的相似度传播
	 ********************/
	private void fullSimPropagation() {
		pgSimMxConcept=new double[sourceConceptNum][targetConceptNum];
		pgSimMxProp=new double[sourcePropNum][targetPropNum];
		ArrayList paraList = new ArrayList();
		ArrayList lt=new ArrayList();
		
		/*计算子图中其它元素的缺省相似度*/
		System.out.println("图中其它元素的缺省相似度");
		ArrayList OtSimList;
		if (sourceStmList.isEmpty() || targetStmList.isEmpty()) {
			OtSimList = new ArrayList();
		} else {
			OtSimList = new TextDocSim().getOtTextSim(sourceFullOtTextDes,
					targetFullOtTextDes);
		}

		/*源本体的语义文本描述*/
		//构造输入参数链表
		packFullSimPgPara(paraList);
		paraList.add(27,OtSimList);
		
		System.out.println("相似度传播");
		lt=new FullSimPropagation().ontSimPg(paraList);
		pgSimMxConcept=(double[][])lt.get(0);
		pgSimMxProp=(double[][])lt.get(1);
		
	}
	
	/**********************
	 * 以子图为基础的相似度传播
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
	 * 重构源本体Informative graph
	 * 重构目标本体Informative graph
	 ********************/
	public void reConsSemInf()
	{
		ArrayList paraList = new ArrayList();
		ArrayList subGList=new ArrayList();
		
		/*首先重构源本体的语义子图*/
		//构造输入参数链表
		packOntGraphPara(paraList,true);
		
		//抽取源本体语义子图
//		System.out.println("抽取源本体语义子图");
		subGList=new OntGraph().consInfSubOnt(paraList);
		sourceCnptSubG=(ConceptSubGraph[])subGList.get(0);
		sourcePropSubG=(PropertySubGraph[])subGList.get(1);
		m_source=(OntModel)subGList.get(2);
		sourceStmList=(ArrayList)subGList.get(3);		
		//再重构源本体的语义子图
		
		/*首先重构目标本体的语义子图*/
		//构造输入参数链表
		packOntGraphPara(paraList,false);
		//抽取目标本体语义子图
		subGList=new OntGraph().consInfSubOnt(paraList);
		targetCnptSubG=(ConceptSubGraph[])subGList.get(0);
		targetPropSubG=(PropertySubGraph[])subGList.get(1);
		m_target=(OntModel)subGList.get(2);
		targetStmList=(ArrayList)subGList.get(3);
		//再重构目标本体的语义子图
	}
	
	/**********************
	 * 基于文本的匹配
	 * 1.构造Informative graph
	 * 2.抽取文本
	 * 3.计算文本相似度
	 ********************/
	private void ontMatchText()
	{
		ArrayList paraList = new ArrayList();
		ArrayList lt=new ArrayList();
		
		/*源本体的语义文本描述*/
		//构造输入参数链表
		packOntDesPara(paraList,true);
//		System.out.println("源本体的语义文本描述");
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

		
		/*目标本体的语义文本描述*/
		//构造输入参数链表
		packOntDesPara(paraList,false);
		/*源本体的语义文本描述*/
//		System.out.println("目标本体的语义文本描述");
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
		
		/*语义匹配*/
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
		
		/*相似矩阵的可视化*/
//		new SimDataVisual().visualize(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			new SimDataVisual().visualize(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			new SimDataVisual().visualize(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			new SimDataVisual().visualize(simMxProp,sourcePropNum,targetPropNum);
//		}
		
//		/*强化相似矩阵实验*/
//		Test ts=new Test();
//		ts.enSimMatrix(simMxConcept,sourceConceptNum,targetConceptNum);
//		ts.enSimMatrix(simMxProp,sourcePropNum,targetPropNum);
//		
//		/*显示强化后的矩阵*/
//		new SimDataVisual().visualize(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			new SimDataVisual().visualize(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			new SimDataVisual().visualize(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			new SimDataVisual().visualize(simMxProp,sourcePropNum,targetPropNum);
//		}
		
		//计算动态阀值
		DynamicThreshold tdSelector=new DynamicThreshold();
		/*简单估计方法*/
//		cnptSimThreshold=tdSelector.naiveThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.naiveThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.naiveThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.naiveThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		/*最大熵方法*/
//		cnptSimThreshold=tdSelector.maxEntropyThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.maxEntropyThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.maxEntropyThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.maxEntropyThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		 /* 利用最大熵方法获得阀值*/
			cnptSimThreshold=tdSelector.maxEntropyThresholdA(simMxConcept,sourceConceptNum,targetConceptNum);
			if (DISTINCT_DP_OP){
				dpSimThreshold=tdSelector.maxEntropyThresholdA(simMxDataProp,sourceDataPropNum,targetDataPropNum);
				opSimThreshold=tdSelector.maxEntropyThresholdA(simMxObjProp,sourceObjPropNum,targetObjPropNum);
			}
			else{
				propSimThreshold=tdSelector.maxEntropyThresholdA(simMxProp,sourcePropNum,targetPropNum);
			}
		
		 /* 利用ostu方法获得阀值*/
//		cnptSimThreshold=tdSelector.ostuThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.ostuThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.ostuThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.ostuThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		 /* 利用mini error方法获得阀值*/
//		cnptSimThreshold=tdSelector.miniErrorThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.miniErrorThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.miniErrorThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.miniErrorThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		
		 /* 利用max correlation方法获得阀值*/
//		cnptSimThreshold=tdSelector.maxCorrelationThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.maxCorrelationThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.maxCorrelationThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.maxCorrelationThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		 /* 利用WP方法获得阀值*/
//		cnptSimThreshold=tdSelector.maxWPThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.maxWPThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.maxWPThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.maxWPThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		
		//结果过滤，用简单的方法
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
		
		/*处理实例相似矩阵*/
		simMxIns = new SimpleFilter().maxValueFilter(sourceInsNum,targetInsNum, simMxIns, 0.3);
		
//		//结果过滤，用稳定婚姻的方法
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
		
		
		/*记录可信结果的位置*/
		/*同时判断是否组要相似度传播*/
		int gotSim=0;//实际得到的映射数目
		int theorySim=Math.min(sourceConceptNum,targetConceptNum)+
		           Math.min(sourcePropNum,targetPropNum);//理论上的映射数目
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
			//需要相似度传播
			isNeedSimProg=true;
		}
		
//		/*相似矩阵的可视化*/
//		new SimDataVisual().visualize(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			new SimDataVisual().visualize(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			new SimDataVisual().visualize(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			new SimDataVisual().visualize(simMxProp,sourcePropNum,targetPropNum);
//		}
		
		/*以过虑后的相似矩阵为输入，
		 * 利用最大熵方法获得阀值*/
//		cnptSimThreshold=tdSelector.maxEntropyThresholdA(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.maxEntropyThresholdA(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.maxEntropyThresholdA(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.maxEntropyThresholdA(simMxProp,sourcePropNum,targetPropNum);
//		}
		 /* 利用ostu方法获得阀值*/
//			cnptSimThreshold=tdSelector.ostuThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//			if (DISTINCT_DP_OP){
//				dpSimThreshold=tdSelector.ostuThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//				opSimThreshold=tdSelector.ostuThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//			}
//			else{
//				propSimThreshold=tdSelector.ostuThreshold(simMxProp,sourcePropNum,targetPropNum);
//			}
		 /* 利用mini error方法获得阀值*/
//		cnptSimThreshold=tdSelector.miniErrorThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.miniErrorThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.miniErrorThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.miniErrorThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
//		 /* 利用max correlation方法获得阀值*/
//		cnptSimThreshold=tdSelector.maxCorrelationThreshold(simMxConcept,sourceConceptNum,targetConceptNum);
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=tdSelector.maxCorrelationThreshold(simMxDataProp,sourceDataPropNum,targetDataPropNum);
//			opSimThreshold=tdSelector.maxCorrelationThreshold(simMxObjProp,sourceObjPropNum,targetObjPropNum);
//		}
//		else{
//			propSimThreshold=tdSelector.maxCorrelationThreshold(simMxProp,sourcePropNum,targetPropNum);
//		}
		
		/*综合过滤前后的阀值*/
//		cnptSimThreshold=(cnptSimThreshold+tdSelector.maxEntropyThresholdA(simMxConcept,sourceConceptNum,targetConceptNum))/2.0;
//		if (DISTINCT_DP_OP){
//			dpSimThreshold=(dpSimThreshold+tdSelector.maxEntropyThresholdA(simMxDataProp,sourceDataPropNum,targetDataPropNum))/2.0;
//			opSimThreshold=(opSimThreshold+tdSelector.maxEntropyThresholdA(simMxObjProp,sourceObjPropNum,targetObjPropNum))/2.0;
//		}
//		else{
//			propSimThreshold=(propSimThreshold+tdSelector.maxEntropyThresholdA(simMxProp,sourcePropNum,targetPropNum))/2.0;
//		}
		 /* 利用WP方法获得阀值*/
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
	 * 基于结构的匹配
	 * 1.构造Informative graph
	 * 2.抽取结构
	 * 3.计算结构相似度
	 ********************/
	private void ontMatchStru()
	{
		
		//文本映射计算
//		ComputeLiteralMapping();
       	
		//结构映射计算
       	//----------Structure Methods Testing-------------------
//		OntGraph OntG = new OntGraph();
//        OntG.SetConceptPara(sourceConceptNum, targetConceptNum, sourceConceptName, targetConceptName);
//        OntG.SetPropertyPara(sourcePropNum, targetPropNum, sourcePropName, targetPropName);
//        OntG.SetInstancePara(sourceInsNum, targetInsNum, sourceInsName, targetInsName);
        //-------------------------------------------------------
        
        
        //转换为Bipartite Graph的匹配算法
//        myOntoGraph.source_Graph = myOntoGraph.Onto2BiptGraph(m_source, true);
//        myOntoGraph.target_Graph = myOntoGraph.Onto2BiptGraph(m_target, false);
//        myOntoGraph.ComputeSGMapping_BiptGraph();
        
        //Informative Graph的匹配算法
//        OntG.source_Graph = OntG.Onto2Graph(m_source, true);
//        OntG.target_Graph = OntG.Onto2Graph(m_target, false);
//        OntG.ConsSGInformative(OntG.source_Graph,true);
//        OntG.ConsSGInformative(OntG.target_Graph,false);
//        OntG.ComputeSGMapping_Informative(OntG.sourceSubGraph, OntG.targetSubGraph);
        
        //未优化的全图匹配算法
//        myOntoGraph.source_Graph = myOntoGraph.Onto2Graph(m_source, true);
//        myOntoGraph.target_Graph = myOntoGraph.Onto2Graph(m_target, false);
//        myOntoGraph.ComputeSGMapping_WholeGraph();
        
        //简单分块的全图匹配算法
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
		/*合并子图描述方法的相似度和相似度传播后的相似度结果*/
		/*方法1.最简单的合并:取均值*/
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
		
		/*方法2.只考虑增加的新匹配*/
		
		/*方法3.综合考虑的合并方法*/
		/*处理概念*/
		//1.如果Aij>=t，确定Aij,同时修改对应的传播矩阵Bij
		for (int i=0;i<sourceConceptNum;i++){
			for (int j=0;j<targetConceptNum;j++){
				if (simMxConcept[i][j]>=OKSIM){
					//B的i行
					for (int k=0;k<targetConceptNum;k++){
						if (k!=j){
							pgSimMxConcept[i][k]=0;
						}
					}
					//B的j列
					for (int k=0;k<sourceConceptNum;k++){
						if (k!=i){
							pgSimMxConcept[k][j]=0;
						}
					}
				}
			}
		}
		//2.如果Aij<t，不能确定Aij,考察传播矩阵Bij
		//2.1Bij<t，Bij肯定是噪声
		for (int i=0;i<sourceConceptNum;i++){
			for (int j=0;j<targetConceptNum;j++){
				if (pgSimMxConcept[i][j]>0 && pgSimMxConcept[i][j]<BADSIM){
					pgSimMxConcept[i][j]=0;
				}
			}
		}
		
		//合并处理结果
		for (int i = 0; i < sourceConceptNum; i++) {
			for (int j = 0; j < targetConceptNum; j++) {
				if (simMxConcept[i][j]<OKSIM){
					simMxConcept[i][j] = (simMxConcept[i][j] + pgSimMxConcept[i][j]) / 2.0;
				}								
			}
		}
		
		/*处理属性*/
		//1.如果Aij>=t，确定Aij,同时修改对应的传播矩阵Bij
		for (int i=0;i<sourcePropNum;i++){
			for (int j=0;j<targetPropNum;j++){
				if (simMxProp[i][j]>=OKSIM){
					//B的i行
					for (int k=0;k<targetPropNum;k++){
						if (k!=j){
							pgSimMxProp[i][k]=0;
						}
					}
					//B的j列
					for (int k=0;k<sourcePropNum;k++){
						if (k!=i){
							pgSimMxProp[k][j]=0;
						}
					}
				}
			}
		}
		//2.如果Aij<t，不能确定Aij,考察传播矩阵Bij
		//2.1Bij<t，Bij肯定是噪声
		for (int i=0;i<sourcePropNum;i++){
			for (int j=0;j<targetPropNum;j++){
				if (pgSimMxProp[i][j]>0 && pgSimMxProp[i][j]<BADSIM){
					pgSimMxProp[i][j]=0;
				}
			}
		}
		
		//合并处理结果
		for (int i = 0; i < sourcePropNum; i++) {
			for (int j = 0; j < targetPropNum; j++) {
				if (simMxProp[i][j]<OKSIM){
					simMxProp[i][j] = (simMxProp[i][j] + pgSimMxProp[i][j]) / 2.0;
				}				
			}
		}
		
		//结果过滤，用稳定婚姻的方法
		simMxConcept = new StableMarriageFilter().run(simMxConcept,sourceConceptNum,targetConceptNum);
		if (DISTINCT_DP_OP){
			simMxDataProp = new StableMarriageFilter().run(simMxDataProp,sourceDataPropNum,targetDataPropNum);
			simMxObjProp = new StableMarriageFilter().run(simMxObjProp,sourceObjPropNum,targetObjPropNum);
		}
		else{
			simMxProp = new StableMarriageFilter().run(simMxProp,sourcePropNum,targetPropNum);
		}
		
		//重新计算动态阀值
		DynamicThreshold tdSelector=new DynamicThreshold();
		 /* 利用最大熵方法获得阀值*/
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
		//根据相似矩阵，生成映射结果
		mappingResult = new MapRecord[Math.max(sourceConceptNum,targetConceptNum)+
		                              Math.max(sourceDataPropNum,targetDataPropNum)+
		                              Math.max(sourceObjPropNum,targetObjPropNum)];
		if (DISTINCT_DP_OP){
			generateMapping(simMxConcept,simMxDataProp,simMxObjProp);
		}
		else{
			generateMapping(simMxConcept,simMxProp);
		}
		
		//显示结果
		if (!flag) return;
		for(int i=0;i<mappingNum;i++)
		{
			mappingResult[i].show();
		}
	}
	
	/**********************
	 * 将结果以两种方式写入文件：
	 * 1.普通文本文件
	 * 2.xml文件
	 * 目标文件夹是target本体的目录
	 *********************/
	public void saveResult()
	{
		MappingFile mapFile=new MappingFile();
		if (lilyFileName.length()==0){
			lilyFileName="lily";
		}
		System.out.println("Matching Result File:");
		System.out.println(mapFile.getOntPath(targetOntFile)+lilyFileName+".rdf"+" and "+ lilyFileName+".txt");
		//普通文本文件的方式
		mapFile.save2txt(sourceOntFile,targetOntFile,mappingNum,mappingResult,lilyFileName+".txt");
		//XML文件的方式
		mapFile.setBaseURI(sourceBaseURI,targetBaseURI);
		mapFile.save2rdf(sourceOntFile,targetOntFile,mappingNum,mappingResult,lilyFileName+".rdf");		
	}
	
	/**********************
	 * 评估映射结果：
	 * 输入映射结果给评估类
	 * 通过和基准结果比较得到结果
	 *********************/
	private void evaluate()
	{
		ArrayList list = new ArrayList();
		MapRecord[] refMapResult = null;
		int refMapNum=0;
		if (refalignFile==null || refalignFile.length()==0){
			return;			
		}
		//读出标准结果
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
		//输入给评估类
		System.out.println("Evaluation:");
		list = new EvaluateMapping().getEvaluation(refMapNum,refMapResult,mappingNum,mappingResult);
		this.precision=((Double)list.get(0)).doubleValue();
		this.recall=((Double)list.get(1)).doubleValue();
		this.f1Measure=((Double)list.get(2)).doubleValue();
	}
	
	/***************
	 * 测试生成的结果文件
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
		//读出标准结果
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
		
		//读出计算得到结果
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
		
		//输入给评估类
		list = new EvaluateMapping().getEvaluation(refMapNum,refMapResult,lilyMapNum,lilyMapResult);
		this.precision=((Double)list.get(0)).doubleValue();
		this.recall=((Double)list.get(1)).doubleValue();
		this.f1Measure=((Double)list.get(2)).doubleValue();
	}
	
	/**********************
	 * 由于这里并不一定只限制1-1的映射，所以只要相似矩阵中的元素不为0，
	 *就认为是符合要求的映射
	 *********************/
	public void generateMapping(double[][] simMxC,double[][] simMxDP, double[][] simMxOp)
	{
		int cMappingNum,dpMappingNum,opMappingNum;//分别记录几种映射的数目
		
		mappingNum = 0;
		//概念映射结果
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
		//DatatypeProperty映射结果
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
		//ObjectProperty映射结果
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
		int cMappingNum,pMappingNum;//分别记录几种映射的数目
		
		mappingNum = 0;
		//概念映射结果
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
		//Property映射结果
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
	 * 将本体参数打包
	 *********************/
	@SuppressWarnings("unchecked")
	private void packOntGraphPara(ArrayList list, boolean flag)
	{
		if (flag){
			//源本体
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
			//目标本体
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
		
		//子图大小
		list.add(25,Semantic_SubGraph_Size);
	}
	/**********************
	 * 将本体参数打包
	 *********************/
	@SuppressWarnings("unchecked")
	private void packOntDesPara(ArrayList list, boolean flag)
	{
		if (flag){
			//源本体
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
			//目标本体
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
	 * 将本体参数打包
	 *********************/
	@SuppressWarnings("unchecked")
	private void packTextDocSimPara(ArrayList list) 
	{
		//源本体
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
		
		//目标本体
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
		
		//实例信息
		list.add(20, sourceInsNum);
		list.add(21, sourceInsName);
		list.add(22, sourceInsTextDes);
		list.add(23, targetInsNum);
		list.add(24, targetInsName);
		list.add(25, targetInsTextDes);				
	}
	
	/**********************
	 * 将本体参数打包
	 *********************/
	@SuppressWarnings("unchecked")
	private void packSubSimPgPara(ArrayList list) 
	{
		//模型
		list.add(0, m_source);
		list.add(1, m_target);
		
		//源本体
		list.add(2, sourceConceptNum);
		list.add(3, sourcePropNum);
		list.add(4, sourceInsNum);
		list.add(5, sourceConceptName);
		list.add(6, sourcePropName);
		list.add(7, sourceInsName);		
		list.add(8, sourceCnptSubG);
		list.add(9, sourcePropSubG);		
		list.add(10, sourceBaseURI);
		
		//目标本体
		list.add(11, targetConceptNum);
		list.add(12, targetPropNum);
		list.add(13, targetInsNum);
		list.add(14, targetConceptName);
		list.add(15, targetPropName);
		list.add(16, targetInsName);		
		list.add(17, targetCnptSubG);
		list.add(18, targetPropSubG);		
		list.add(19, targetBaseURI);
		
		//相似度矩阵
		list.add(20, simMxConcept);
		list.add(21, simMxProp);
		list.add(22, simMxIns);
		
		//本体full基本信息
		list.add(23,sourceAnonCnpt);
		list.add(24,sourceAnonProp);
		list.add(25,sourceAnonIns);
		list.add(26,targetAnonCnpt);
		list.add(27,targetAnonProp);
		list.add(28,targetAnonIns);

		//已包含确信相似度的位置集合
		list.add(29,sourceCnptOkSimPos);
		list.add(30,targetCnptOkSimPos);
		list.add(31,sourcePropOkSimPos);
		list.add(32,targetPropOkSimPos);
	}
	
	@SuppressWarnings("unchecked")
	private void packFullSimPgPara(ArrayList list) 
	{
		//模型
		list.add(0, m_source);
		list.add(1, m_target);
		
		//源本体
		list.add(2, sourceConceptNum);
		list.add(3, sourcePropNum);
		list.add(4, sourceInsNum);
		list.add(5, sourceConceptName);
		list.add(6, sourcePropName);
		list.add(7, sourceInsName);		
		list.add(8, sourceStmList);
		list.add(9, sourceBaseURI);
		
		//目标本体
		list.add(10, targetConceptNum);
		list.add(11, targetPropNum);
		list.add(12, targetInsNum);
		list.add(13, targetConceptName);
		list.add(14, targetPropName);
		list.add(15, targetInsName);		
		list.add(16, targetStmList);
		
		list.add(17, targetBaseURI);
		
		//相似度矩阵
		list.add(18, simMxConcept);
		list.add(19, simMxProp);
		list.add(20, simMxIns);
		
		//本体full基本信息
		list.add(21,sourceAnonCnpt);
		list.add(22,sourceAnonProp);
		list.add(23,sourceAnonIns);
		list.add(24,targetAnonCnpt);
		list.add(25,targetAnonProp);
		list.add(26,targetAnonIns);
	}
}

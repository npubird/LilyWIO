/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-4-27
 * Filename          SimpleStrMatching.java
 * Version           2.0
 * 
 * Last modified on  2007-4-27
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 * �ַ����Ƶļ�ƥ���㷨
 ***********************************************/
package lily.onto.mapping.method;

import java.net.MalformedURLException;
import java.util.ArrayList;

import org.dom4j.DocumentException;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import lily.onto.mapping.evaluation.EvaluateMapping;
import lily.onto.parse.OWLOntParse;
import lily.tool.datastructure.MapRecord;
import lily.tool.strsimilarity.*;
import lily.tool.filter.*;
import lily.tool.mappingfile.*;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-4-27
 * 
 * describe:
 * 
 ********************/
public class SimpleStrMatching {
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
	//ӳ������
	public int mappingNum;
	//ӳ����
	public MapRecord[] mappingResult;
	//base URI
	public String sourceBaseURI;
	public String targetBaseURI;
	//���ƶȷ�ֵ
	public double simThreshold;
	//������ļ���
	public String lilyFileName="";
	
	//constants
	public int EQUALITY = 0;
	public int GENERAL = 1;
	public int SPECIFIC = 2;
	
	public static void main(String[] args) {
//		new SimpleStrMatching().runSample();
		new SimpleStrMatching().run2005Bench();
	}
	
	public void runSample(){
		SimpleStrMatching ontM = new SimpleStrMatching();
		ontM.setOntFile();
		ontM.parseOnt();
		ontM.init();
		ontM.run();
		ontM.evaluate();
	}
	
	public void run2005Bench(){
		int fineNum=51;
		int fileName[]={101,102,103,104,201,202,203,204,205,206,207,208,209,210,
				        221,222,223,224,225,228,230,231,232,233,236,237,238,239,
				        240,241,246,247,248,249,250,251,252,253,254,257,258,259,
				        260,261,262,265,266,301,302,303,304};
		for (int i=0;i<fineNum;i++){
			SimpleStrMatching ontM = new SimpleStrMatching();
			ontM.sourceOntFile = "./dataset/OAEI2005/bench/benchmarks/101/onto.rdf";
			ontM.targetOntFile = "./dataset/OAEI2005/bench/benchmarks/"+String.valueOf(fileName[i])+"/onto.rdf";
			ontM.refalignFile = "./dataset/OAEI2005/bench/benchmarks/"+String.valueOf(fileName[i])+"/refalign.rdf";
			ontM.parseOnt();
			ontM.init();
			System.out.println(ontM.targetOntFile);
			ontM.run();
			ontM.evaluate();
			System.out.println("------------------");
		}
	}
	
	//��ʼ������Ҫ�ǻ����Ĳ�������
	public void init()
	{
		simThreshold = 0.8;
	}
	
	//���ñ����ļ�
	public void setOntFile()
	{
		sourceOntFile = new String ("./dataset/OAEI2005/bench/benchmarks/101/onto.rdf");
		targetOntFile = new String ("./dataset/OAEI2005/bench/benchmarks/302/onto.rdf");
		refalignFile = new String ("./dataset/OAEI2005/bench/benchmarks/302/refalign.rdf");
	}

	//��������
	public void parseOnt()
	{
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
	}
	
	public void run()
	{
		//ƥ�����
        long start = System.currentTimeMillis();//��ʼ��ʱ 
        ontMatch();
		long end = System.currentTimeMillis();//������ʱ
		long costtime = end - start;//ͳ���㷨ʱ��
		System.out.println("���ı�ƥ���㷨ʱ�䣺"+(double)costtime/1000.+"��");
        //--------------------------------------------------------
		//��ʾӳ����
		showResult(true);
		//����ӳ����
		saveResult();
    }
	
	public double[][] getOntLiteralMatching(int sNum, String[] sName, int tNum, String[] tName)
	{
		int i,j;
		double[][] sim = new double [sNum][tNum];
      	
       	//Mapping concepts
       	for (i=0;i<sNum;i++)
       	{
       		for (j=0;j<tNum;j++)
       		{
       			sim[i][j] = new StrEDSim().getNormEDSim(sName[i],tName[j]);
       		}
       	}
       	return sim;
	}
	public void ontMatch()
	{
		//ƥ�����
		double[][] simMxConcept = new double[sourceConceptNum][targetConceptNum];
		simMxConcept = getOntLiteralMatching(sourceConceptNum,
				sourceConceptName, targetConceptNum, targetConceptName);
		// ƥ��DatatypeProperty
		double[][] simMxDataProp = new double[sourceDataPropNum][targetDataPropNum];
		simMxDataProp = getOntLiteralMatching(sourceDataPropNum,
				sourceDataPropName, targetDataPropNum, targetDataPropName);
		//ƥ��ObjectProperty
		double[][] simMxObjProp = new double[sourceObjPropNum][targetObjPropNum];
		simMxObjProp = getOntLiteralMatching(sourceObjPropNum,
				sourceObjPropName, targetObjPropNum, targetObjPropName);
		
		//������ˣ��ü򵥵ķ���
		simMxConcept = new SimpleFilter().maxValueFilter(sourceConceptNum,
				targetConceptNum, simMxConcept, simThreshold);
		simMxDataProp = new SimpleFilter().maxValueFilter(sourceDataPropNum,
				targetDataPropNum, simMxDataProp, simThreshold);
		simMxObjProp = new SimpleFilter().maxValueFilter(sourceObjPropNum,
				targetObjPropNum, simMxObjProp, simThreshold);
		
		//������Щ���ƾ�������ӳ����
		mappingResult = new MapRecord[Math.max(sourceConceptNum,targetConceptNum)+
		                              Math.max(sourceDataPropNum,targetDataPropNum)+
		                              Math.max(sourceObjPropNum,targetObjPropNum)];
		generateMapping(simMxConcept,simMxDataProp,simMxObjProp);
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
				if (simMxC[i][j]>=simThreshold)
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
		//DatatypePropertyӳ����
		opMappingNum = 0;
		for(int i=0;i<sourceDataPropNum;i++)
			for (int j=0;j<targetDataPropNum;j++)
			{
				if (simMxDP[i][j]>=simThreshold)
				{
	   				mappingResult[mappingNum] = new MapRecord();
	   				mappingResult[mappingNum].sourceLabel = new String (sourceDataPropName[i]);
	   				mappingResult[mappingNum].targetLabel = new String (targetDataPropName[j]);
	   				mappingResult[mappingNum].similarity = simMxDP[i][j];
	   				mappingResult[mappingNum].relationType = EQUALITY;
	   				mappingNum++;
	   				opMappingNum++;
				}
			}
		//ObjectPropertyӳ����
		dpMappingNum = 0;
		for(int i=0;i<sourceObjPropNum;i++)
			for (int j=0;j<targetObjPropNum;j++)
			{
				if (simMxOp[i][j]>=simThreshold)
				{
	   				mappingResult[mappingNum] = new MapRecord();
	   				mappingResult[mappingNum].sourceLabel = new String (sourceObjPropName[i]);
	   				mappingResult[mappingNum].targetLabel = new String (targetObjPropName[j]);
	   				mappingResult[mappingNum].similarity = simMxOp[i][j];
	   				mappingResult[mappingNum].relationType = EQUALITY;
	   				mappingNum++;
	   				dpMappingNum++;
				}
			}
	}
	
	public void showResult(boolean flag)
	{
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
		//��ͨ�ı��ļ��ķ�ʽ
		if (lilyFileName.length()==0){
			lilyFileName="lilyResult";
		}
		new MappingFile().save2txt(sourceOntFile,targetOntFile,mappingNum,mappingResult,lilyFileName+".txt");
		//XML�ļ��ķ�ʽ
		
	}
	
	/**********************
	 * ����ӳ������
	 * ����ӳ������������
	 * ͨ���ͻ�׼����Ƚϵõ����
	 *********************/
	public void evaluate()
	{
		ArrayList list = new ArrayList();
		MapRecord[] refMapResult = null;
		int refMapNum=0;
		
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
		list = new EvaluateMapping().getEvaluation(refMapNum,refMapResult,mappingNum,mappingResult);
	}
}

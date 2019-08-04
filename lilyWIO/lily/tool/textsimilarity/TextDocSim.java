/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-6-1
 * Filename          TextDocSim.java
 * Version           2.0
 * 
 * Last modified on  2007-6-1
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 * ���㱾���ı����������ƶȡ�
 * �����ר�ŵ����н��д�����Ϊ��������ܵ�������
 ***********************************************/
package lily.tool.textsimilarity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;

import lily.onto.parse.OWLOntParse;
import lily.tool.datastructure.ConceptSubGraph;
import lily.tool.datastructure.GraphElmSim;
import lily.tool.datastructure.PropertySubGraph;
import lily.tool.datastructure.TextDes;
import lily.tool.datastructure.Word;
import lily.tool.strsimilarity.StrEDSim;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-6-1
 * 
 * describe:
 * 
 ********************/
public class TextDocSim {
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
	
	//�ı�������Ϣ
	public TextDes[] sourceCnptTextDes;
	public TextDes[] sourcePropTextDes;
	public TextDes[] sourceInsTextDes;
	public TextDes[] targetCnptTextDes;
	public TextDes[] targetPropTextDes;
	public TextDes[] targetInsTextDes;
	
	public TextDes[] sCBasicDes;//�������
	public TextDes[] sCHSubDes;//���
	public TextDes[] sCHSupDes;
	public TextDes[] sCHSblDes;
	public TextDes[] sCHDsjDes;
	public TextDes[] sCHCmpDes;
	public TextDes[] sCPDmnDes;//����
	public TextDes[] sCPRngDes;
	public TextDes[] sCInsDes;//ʵ��
	
	public TextDes[] sPBasicDes;//���Ի���
	public TextDes[] sPHSubDes;//���
	public TextDes[] sPHSupDes;
	public TextDes[] sPHSblDes;
	public TextDes[] sPFDmnDes;//����
	public TextDes[] sPFRngDes;
	public TextDes[] sPFChrDes;
	public TextDes[] sPIDmnDes;//ʵ��
	public TextDes[] sPIRngDes;
	public TextDes[] sDPBasicDes;//Datatype���Ի���
	public TextDes[] sDPHSubDes;//���
	public TextDes[] sDPHSupDes;
	public TextDes[] sDPHSblDes;
	public TextDes[] sDPFDmnDes;//����
	public TextDes[] sDPFRngDes;
	public TextDes[] sDPFChrDes;
	public TextDes[] sDPIDmnDes;//ʵ��
	public TextDes[] sDPIRngDes;
	public TextDes[] sOPBasicDes;//Object���Ի���
	public TextDes[] sOPHSubDes;//���
	public TextDes[] sOPHSupDes;
	public TextDes[] sOPHSblDes;
	public TextDes[] sOPFDmnDes;//����
	public TextDes[] sOPFRngDes;
	public TextDes[] sOPFChrDes;
	public TextDes[] sOPIDmnDes;//ʵ��
	public TextDes[] sOPIRngDes;
	
	public TextDes[] tCBasicDes;
	public TextDes[] tCHSubDes;
	public TextDes[] tCHSupDes;
	public TextDes[] tCHSblDes;
	public TextDes[] tCHDsjDes;
	public TextDes[] tCHCmpDes;
	public TextDes[] tCPDmnDes;
	public TextDes[] tCPRngDes;
	public TextDes[] tCInsDes;
	public TextDes[] tDPBasicDes;
	public TextDes[] tPBasicDes;//���Ի���
	public TextDes[] tPHSubDes;
	public TextDes[] tPHSupDes;
	public TextDes[] tPHSblDes;
	public TextDes[] tPFDmnDes;
	public TextDes[] tPFRngDes;
	public TextDes[] tPFChrDes;
	public TextDes[] tPIDmnDes;
	public TextDes[] tPIRngDes;
	public TextDes[] tDPHSubDes;
	public TextDes[] tDPHSupDes;
	public TextDes[] tDPHSblDes;
	public TextDes[] tDPFDmnDes;
	public TextDes[] tDPFRngDes;
	public TextDes[] tDPFChrDes;
	public TextDes[] tDPIDmnDes;
	public TextDes[] tDPIRngDes;
	public TextDes[] tOPBasicDes;
	public TextDes[] tOPHSubDes;
	public TextDes[] tOPHSupDes;
	public TextDes[] tOPHSblDes;
	public TextDes[] tOPFDmnDes;
	public TextDes[] tOPFRngDes;
	public TextDes[] tOPFChrDes;
	public TextDes[] tOPIDmnDes;
	public TextDes[] tOPIRngDes;
	
	//�༭�������ƶȷ�ֵ
	private double edThreshold=0.85;
	
	//���ƶ�
	public double[][] cSimMatrix;
	public double[][] pSimMatrix;
	public double[][] dpSimMatrix;
	public double[][] opSimMatrix;	
	public double[][] iSimMatrix;
	
	/****************
	 * ��������
	 ****************/
	@SuppressWarnings("unchecked")
	public ArrayList getOntTextSim(ArrayList paraList,boolean disctinctOD)
	{
		ArrayList result=new ArrayList();
		
		/*��������*/
		unPackPara(paraList);
		/*�����ı�*/
		processDesText();
		/*��������*/
		getCnptTextSim();
		/*��������*/
		getPropTextSim(disctinctOD);
		/*ʵ������*/
		getInsTextSim();
		
		result.add(0,cSimMatrix);
		result.add(1,iSimMatrix);
		if (disctinctOD){
			result.add(2,dpSimMatrix);
			result.add(3,opSimMatrix);
		}
		else{
			result.add(2,pSimMatrix);
		}
		
		return result;
	}
	
	/****************
	 * �����ı�
	 ****************/
	public void processDesText()
	{
		/*����source�ı�*/
		for (int i=0;i<sourceConceptNum;i++){
			ArrayList desList=new ArrayList();
			
			/**************��������***************/
			/*��������*/
			sCBasicDes[i]=new TextDes();
			sCBasicDes[i].name=sourceConceptName[i];
			desList=(ArrayList)sourceCnptTextDes[i].text.get(0);
			sCBasicDes[i].text=desList;
			
			/*�������*/
			desList=(ArrayList)sourceCnptTextDes[i].text.get(1);
			/*subClass*/
			sCHSubDes[i]=new TextDes();
			sCHSubDes[i].name=sourceConceptName[i];
			sCHSubDes[i].text=((TextDes)desList.get(0)).text;
			
			/*superClass*/
			sCHSupDes[i]=new TextDes();
			sCHSupDes[i].name=sourceConceptName[i];
			sCHSupDes[i].text=((TextDes)desList.get(1)).text;
			/*sibling Class*/
			sCHSblDes[i]=new TextDes();
			sCHSblDes[i].name=sourceConceptName[i];
			sCHSblDes[i].text=((TextDes)desList.get(2)).text;
			/*disjoint Class*/
			sCHDsjDes[i]=new TextDes();
			sCHDsjDes[i].name=sourceConceptName[i];
			sCHDsjDes[i].text=((TextDes)desList.get(3)).text;
			/*complementOf Class*/
			sCHCmpDes[i]=new TextDes();
			sCHCmpDes[i].name=sourceConceptName[i];
			sCHCmpDes[i].text=((TextDes)desList.get(4)).text;
			
			/*������������*/
			desList=(ArrayList)sourceCnptTextDes[i].text.get(2);
			sCPDmnDes[i]=new TextDes();
			sCPDmnDes[i].name=sourceConceptName[i];
			sCPDmnDes[i].text=((TextDes)desList.get(0)).text;
			sCPRngDes[i]=new TextDes();
			sCPRngDes[i].name=sourceConceptName[i];
			sCPRngDes[i].text=((TextDes)desList.get(1)).text;
			
			/*ʵ������*/
			desList=(ArrayList)sourceCnptTextDes[i].text.get(3);
			sCInsDes[i]=new TextDes();
			sCInsDes[i].name=sourceConceptName[i];
			sCInsDes[i].text=((TextDes)desList.get(0)).text;
		}
		
		/*����target�ı�*/
		for (int i=0;i<targetConceptNum;i++){
			ArrayList desList=new ArrayList();
			
			/**************��������***************/
			/*��������*/
			tCBasicDes[i]=new TextDes();
			tCBasicDes[i].name=targetConceptName[i];
			desList=(ArrayList)targetCnptTextDes[i].text.get(0);
			tCBasicDes[i].text=desList;
			
			/*�������*/
			desList=(ArrayList)targetCnptTextDes[i].text.get(1);
			/*subClass*/
			tCHSubDes[i]=new TextDes();
			tCHSubDes[i].name=targetConceptName[i];
			tCHSubDes[i].text=((TextDes)desList.get(0)).text;
			
			/*superClass*/
			tCHSupDes[i]=new TextDes();
			tCHSupDes[i].name=targetConceptName[i];
			tCHSupDes[i].text=((TextDes)desList.get(1)).text;
			/*sibling Class*/
			tCHSblDes[i]=new TextDes();
			tCHSblDes[i].name=targetConceptName[i];
			tCHSblDes[i].text=((TextDes)desList.get(2)).text;
			/*disjoint Class*/
			tCHDsjDes[i]=new TextDes();
			tCHDsjDes[i].name=targetConceptName[i];
			tCHDsjDes[i].text=((TextDes)desList.get(3)).text;
			/*complementOf Class*/
			tCHCmpDes[i]=new TextDes();
			tCHCmpDes[i].name=targetConceptName[i];
			tCHCmpDes[i].text=((TextDes)desList.get(4)).text;
			
			/*������������*/
			desList=(ArrayList)targetCnptTextDes[i].text.get(2);
			tCPDmnDes[i]=new TextDes();
			tCPDmnDes[i].name=targetConceptName[i];
			tCPDmnDes[i].text=((TextDes)desList.get(0)).text;
			tCPRngDes[i]=new TextDes();
			tCPRngDes[i].name=targetConceptName[i];
			tCPRngDes[i].text=((TextDes)desList.get(1)).text;
			
			/*ʵ������*/
			desList=(ArrayList)targetCnptTextDes[i].text.get(3);
			tCInsDes[i]=new TextDes();
			tCInsDes[i].name=targetConceptName[i];
			tCInsDes[i].text=((TextDes)desList.get(0)).text;
		}
		
		/**************��������***************/
		/*����source�ı�*/
		for (int i=0;i<sourcePropNum;i++){
			ArrayList desList=new ArrayList();
		
			/*��������*/
			sPBasicDes[i]=new TextDes();
			sPBasicDes[i].name=sourcePropName[i];
			desList=(ArrayList)sourcePropTextDes[i].text.get(0);
			sPBasicDes[i].text=desList;
			
			/*�������*/
			desList=(ArrayList)sourcePropTextDes[i].text.get(1);
			/*subProperty*/
			sPHSubDes[i]=new TextDes();
			sPHSubDes[i].name=sourcePropName[i];
			sPHSubDes[i].text=((TextDes)desList.get(0)).text;
			/*superProperty*/
			sPHSupDes[i]=new TextDes();
			sPHSupDes[i].name=sourcePropName[i];
			sPHSupDes[i].text=((TextDes)desList.get(1)).text;
			/*sibling property*/
			sPHSblDes[i]=new TextDes();
			sPHSblDes[i].name=sourcePropName[i];
			sPHSblDes[i].text=((TextDes)desList.get(2)).text;
			
			/*������������*/
			desList=(ArrayList)sourcePropTextDes[i].text.get(2);
			/*Domain*/
			sPFDmnDes[i]=new TextDes();
			sPFDmnDes[i].name=sourcePropName[i];
			sPFDmnDes[i].text=((TextDes)desList.get(0)).text;
			/*Range*/
			sPFRngDes[i]=new TextDes();
			sPFRngDes[i].name=sourcePropName[i];
			sPFRngDes[i].text=((TextDes)desList.get(1)).text;
			/*����*/
			sPFChrDes[i]=new TextDes();
			sPFChrDes[i].name=sourcePropName[i];
			sPFChrDes[i].text=((TextDes)desList.get(2)).text;
			
			/*ʵ������*/
			desList=(ArrayList)sourcePropTextDes[i].text.get(3);
			/*Domain*/
			sPIDmnDes[i]=new TextDes();
			sPIDmnDes[i].name=sourcePropName[i];
			sPIDmnDes[i].text=((TextDes)desList.get(0)).text;
			/*Range*/
			sPIRngDes[i]=new TextDes();
			sPIRngDes[i].name=sourcePropName[i];
			sPIRngDes[i].text=((TextDes)desList.get(1)).text;
		}
		//�ֽ�Ϊ�������͵�����
		for (int i=0;i<sourceDataPropNum;i++){
			ArrayList desList=new ArrayList();
			int pos=-1;
			/*��ȷλ��*/
			for (int j=0;j<sourcePropNum;j++){
				if (sourcePropName[j].equals(sourceDataPropName[i])){
					pos=j;
				}
			}
			
			/*��������*/
			sDPBasicDes[i]=new TextDes();
			sDPBasicDes[i].name=sourceDataPropName[i];
			desList=(ArrayList)sourcePropTextDes[pos].text.get(0);
			sDPBasicDes[i].text=desList;
			
			/*�������*/
			desList=(ArrayList)sourcePropTextDes[pos].text.get(1);
			/*subProperty*/
			sDPHSubDes[i]=new TextDes();
			sDPHSubDes[i].name=sourceDataPropName[i];
			sDPHSubDes[i].text=((TextDes)desList.get(0)).text;
			/*superProperty*/
			sDPHSupDes[i]=new TextDes();
			sDPHSupDes[i].name=sourceDataPropName[i];
			sDPHSupDes[i].text=((TextDes)desList.get(1)).text;
			/*sibling property*/
			sDPHSblDes[i]=new TextDes();
			sDPHSblDes[i].name=sourceDataPropName[i];
			sDPHSblDes[i].text=((TextDes)desList.get(2)).text;
			
			/*������������*/
			desList=(ArrayList)sourcePropTextDes[pos].text.get(2);
			/*Domain*/
			sDPFDmnDes[i]=new TextDes();
			sDPFDmnDes[i].name=sourceDataPropName[i];
			sDPFDmnDes[i].text=((TextDes)desList.get(0)).text;
			/*Range*/
			sDPFRngDes[i]=new TextDes();
			sDPFRngDes[i].name=sourceDataPropName[i];
			sDPFRngDes[i].text=((TextDes)desList.get(1)).text;
			/*����*/
			sDPFChrDes[i]=new TextDes();
			sDPFChrDes[i].name=sourceDataPropName[i];
			sDPFChrDes[i].text=((TextDes)desList.get(2)).text;
			
			/*ʵ������*/
			desList=(ArrayList)sourcePropTextDes[pos].text.get(3);
			/*Domain*/
			sDPIDmnDes[i]=new TextDes();
			sDPIDmnDes[i].name=sourceDataPropName[i];
			sDPIDmnDes[i].text=((TextDes)desList.get(0)).text;
			/*Range*/
			sDPIRngDes[i]=new TextDes();
			sDPIRngDes[i].name=sourceDataPropName[i];
			sDPIRngDes[i].text=((TextDes)desList.get(1)).text;
		}
		for (int i=0;i<sourceObjPropNum;i++){
			ArrayList desList=new ArrayList();
			int pos=-1;
			/*��ȷλ��*/
			for (int j=0;j<sourcePropNum;j++){
				if (sourcePropName[j].equals(sourceObjPropName[i])){
					pos=j;
				}
			}
			
			/*��������*/
			sOPBasicDes[i]=new TextDes();
			sOPBasicDes[i].name=sourceObjPropName[i];
			desList=(ArrayList)sourcePropTextDes[pos].text.get(0);
			sOPBasicDes[i].text=desList;
			
			/*�������*/
			desList=(ArrayList)sourcePropTextDes[pos].text.get(1);
			/*subProperty*/
			sOPHSubDes[i]=new TextDes();
			sOPHSubDes[i].name=sourceObjPropName[i];
			sOPHSubDes[i].text=((TextDes)desList.get(0)).text;
			/*superProperty*/
			sOPHSupDes[i]=new TextDes();
			sOPHSupDes[i].name=sourceObjPropName[i];
			sOPHSupDes[i].text=((TextDes)desList.get(1)).text;
			/*sibling property*/
			sOPHSblDes[i]=new TextDes();
			sOPHSblDes[i].name=sourceObjPropName[i];
			sOPHSblDes[i].text=((TextDes)desList.get(2)).text;
			
			/*������������*/
			desList=(ArrayList)sourcePropTextDes[pos].text.get(2);
			/*Domain*/
			sOPFDmnDes[i]=new TextDes();
			sOPFDmnDes[i].name=sourceObjPropName[i];
			sOPFDmnDes[i].text=((TextDes)desList.get(0)).text;
			/*Range*/
			sOPFRngDes[i]=new TextDes();
			sOPFRngDes[i].name=sourceObjPropName[i];
			sOPFRngDes[i].text=((TextDes)desList.get(1)).text;
			/*����*/
			sOPFChrDes[i]=new TextDes();
			sOPFChrDes[i].name=sourceObjPropName[i];
			sOPFChrDes[i].text=((TextDes)desList.get(2)).text;
			
			/*ʵ������*/
			desList=(ArrayList)sourcePropTextDes[pos].text.get(3);
			/*Domain*/
			sOPIDmnDes[i]=new TextDes();
			sOPIDmnDes[i].name=sourceObjPropName[i];
			sOPIDmnDes[i].text=((TextDes)desList.get(0)).text;
			/*Range*/
			sOPIRngDes[i]=new TextDes();
			sOPIRngDes[i].name=sourceObjPropName[i];
			sOPIRngDes[i].text=((TextDes)desList.get(1)).text;
		}
		
		/*����target�ı�*/
		for (int i=0;i<targetPropNum;i++){
			ArrayList desList=new ArrayList();
		
			/*��������*/
			tPBasicDes[i]=new TextDes();
			tPBasicDes[i].name=targetPropName[i];
			desList=(ArrayList)targetPropTextDes[i].text.get(0);
			tPBasicDes[i].text=desList;
			
			/*�������*/
			desList=(ArrayList)targetPropTextDes[i].text.get(1);
			/*subProperty*/
			tPHSubDes[i]=new TextDes();
			tPHSubDes[i].name=targetPropName[i];
			tPHSubDes[i].text=((TextDes)desList.get(0)).text;
			/*superProperty*/
			tPHSupDes[i]=new TextDes();
			tPHSupDes[i].name=targetPropName[i];
			tPHSupDes[i].text=((TextDes)desList.get(1)).text;
			/*sibling property*/
			tPHSblDes[i]=new TextDes();
			tPHSblDes[i].name=targetPropName[i];
			tPHSblDes[i].text=((TextDes)desList.get(2)).text;
			
			/*������������*/
			desList=(ArrayList)targetPropTextDes[i].text.get(2);
			/*Domain*/
			tPFDmnDes[i]=new TextDes();
			tPFDmnDes[i].name=targetPropName[i];
			tPFDmnDes[i].text=((TextDes)desList.get(0)).text;
			/*Range*/
			tPFRngDes[i]=new TextDes();
			tPFRngDes[i].name=targetPropName[i];
			tPFRngDes[i].text=((TextDes)desList.get(1)).text;
			/*����*/
			tPFChrDes[i]=new TextDes();
			tPFChrDes[i].name=targetPropName[i];
			tPFChrDes[i].text=((TextDes)desList.get(2)).text;
			
			/*ʵ������*/
			desList=(ArrayList)targetPropTextDes[i].text.get(3);
			/*Domain*/
			tPIDmnDes[i]=new TextDes();
			tPIDmnDes[i].name=targetPropName[i];
			tPIDmnDes[i].text=((TextDes)desList.get(0)).text;
			/*Range*/
			tPIRngDes[i]=new TextDes();
			tPIRngDes[i].name=targetPropName[i];
			tPIRngDes[i].text=((TextDes)desList.get(1)).text;
		}
		
		//�ֽ�Ϊ�������͵�����
		for (int i=0;i<targetDataPropNum;i++){
			ArrayList desList=new ArrayList();
			int pos=-1;
			/*��ȷλ��*/
			for (int j=0;j<targetPropNum;j++){
				if (targetPropName[j].equals(targetDataPropName[i])){
					pos=j;
				}
			}
			
			/*��������*/
			tDPBasicDes[i]=new TextDes();
			tDPBasicDes[i].name=targetDataPropName[i];
			desList=(ArrayList)targetPropTextDes[pos].text.get(0);
			tDPBasicDes[i].text=desList;
			
			/*�������*/
			desList=(ArrayList)targetPropTextDes[pos].text.get(1);
			/*subProperty*/
			tDPHSubDes[i]=new TextDes();
			tDPHSubDes[i].name=targetDataPropName[i];
			tDPHSubDes[i].text=((TextDes)desList.get(0)).text;
			/*superProperty*/
			tDPHSupDes[i]=new TextDes();
			tDPHSupDes[i].name=targetDataPropName[i];
			tDPHSupDes[i].text=((TextDes)desList.get(1)).text;
			/*sibling property*/
			tDPHSblDes[i]=new TextDes();
			tDPHSblDes[i].name=targetDataPropName[i];
			tDPHSblDes[i].text=((TextDes)desList.get(2)).text;
			
			/*������������*/
			desList=(ArrayList)targetPropTextDes[pos].text.get(2);
			/*Domain*/
			tDPFDmnDes[i]=new TextDes();
			tDPFDmnDes[i].name=targetDataPropName[i];
			tDPFDmnDes[i].text=((TextDes)desList.get(0)).text;
			/*Range*/
			tDPFRngDes[i]=new TextDes();
			tDPFRngDes[i].name=targetDataPropName[i];
			tDPFRngDes[i].text=((TextDes)desList.get(1)).text;
			/*����*/
			tDPFChrDes[i]=new TextDes();
			tDPFChrDes[i].name=targetDataPropName[i];
			tDPFChrDes[i].text=((TextDes)desList.get(2)).text;
			
			/*ʵ������*/
			desList=(ArrayList)targetPropTextDes[pos].text.get(3);
			/*Domain*/
			tDPIDmnDes[i]=new TextDes();
			tDPIDmnDes[i].name=targetDataPropName[i];
			tDPIDmnDes[i].text=((TextDes)desList.get(0)).text;
			/*Range*/
			tDPIRngDes[i]=new TextDes();
			tDPIRngDes[i].name=targetDataPropName[i];
			tDPIRngDes[i].text=((TextDes)desList.get(1)).text;
		}
		for (int i=0;i<targetObjPropNum;i++){
			ArrayList desList=new ArrayList();
			int pos=-1;
			/*��ȷλ��*/
			for (int j=0;j<targetPropNum;j++){
				if (targetPropName[j].equals(targetObjPropName[i])){
					pos=j;
				}
			}
			
			/*��������*/
			tOPBasicDes[i]=new TextDes();
			tOPBasicDes[i].name=targetObjPropName[i];
			desList=(ArrayList)targetPropTextDes[pos].text.get(0);
			tOPBasicDes[i].text=desList;
			
			/*�������*/
			desList=(ArrayList)targetPropTextDes[pos].text.get(1);
			/*subProperty*/
			tOPHSubDes[i]=new TextDes();
			tOPHSubDes[i].name=targetObjPropName[i];
			tOPHSubDes[i].text=((TextDes)desList.get(0)).text;
			/*superProperty*/
			tOPHSupDes[i]=new TextDes();
			tOPHSupDes[i].name=targetObjPropName[i];
			tOPHSupDes[i].text=((TextDes)desList.get(1)).text;
			/*sibling property*/
			tOPHSblDes[i]=new TextDes();
			tOPHSblDes[i].name=targetObjPropName[i];
			tOPHSblDes[i].text=((TextDes)desList.get(2)).text;
			
			/*������������*/
			desList=(ArrayList)targetPropTextDes[pos].text.get(2);
			/*Domain*/
			tOPFDmnDes[i]=new TextDes();
			tOPFDmnDes[i].name=targetObjPropName[i];
			tOPFDmnDes[i].text=((TextDes)desList.get(0)).text;
			/*Range*/
			tOPFRngDes[i]=new TextDes();
			tOPFRngDes[i].name=targetObjPropName[i];
			tOPFRngDes[i].text=((TextDes)desList.get(1)).text;
			/*����*/
			tOPFChrDes[i]=new TextDes();
			tOPFChrDes[i].name=targetObjPropName[i];
			tOPFChrDes[i].text=((TextDes)desList.get(2)).text;
			
			/*ʵ������*/
			desList=(ArrayList)targetPropTextDes[pos].text.get(3);
			/*Domain*/
			tOPIDmnDes[i]=new TextDes();
			tOPIDmnDes[i].name=targetObjPropName[i];
			tOPIDmnDes[i].text=((TextDes)desList.get(0)).text;
			/*Range*/
			tOPIRngDes[i]=new TextDes();
			tOPIRngDes[i].name=targetObjPropName[i];
			tOPIRngDes[i].text=((TextDes)desList.get(1)).text;
		}	
	}	
	
	/****************
	 * ��������ı�����
	 ****************/
	public void getCnptTextSim()
	{
		double[][] mBasic=new double[sourceConceptNum][targetConceptNum];
		double[][] mSub=  new double[sourceConceptNum][targetConceptNum];
		double[][] mSup=  new double[sourceConceptNum][targetConceptNum];
		double[][] mSbl=  new double[sourceConceptNum][targetConceptNum];
		double[][] mDsj=  new double[sourceConceptNum][targetConceptNum];
		double[][] mCmp=  new double[sourceConceptNum][targetConceptNum];
		double[][] mDmn=  new double[sourceConceptNum][targetConceptNum];
		double[][] mRng=  new double[sourceConceptNum][targetConceptNum];
		double[][] mIns=  new double[sourceConceptNum][targetConceptNum];
		
		/*Ȩ��*/
		double wB=0.35;
		double wH=0.25;
		double wP=0.2;
		double wI=0.2;
		
		double wHsub=0.3;
		double wHsup=0.3;
		double wHsbl=0.2;
		double wHdsj=0.1;
		double wHcmp=0.1;
		
		double wPd=0.5;
		double wPr=0.5;
		
		/*����༭���봦��Vector�е������*/
		tuneDesDocbyED(sCBasicDes,sourceConceptNum,tCBasicDes,targetConceptNum);
		tuneDesDocbyED(sCHSubDes,sourceConceptNum,tCHSubDes,targetConceptNum);
		tuneDesDocbyED(sCHSupDes,sourceConceptNum,tCHSupDes,targetConceptNum);
		tuneDesDocbyED(sCHSblDes,sourceConceptNum,tCHSblDes,targetConceptNum);
		tuneDesDocbyED(sCHDsjDes,sourceConceptNum,tCHDsjDes,targetConceptNum);
		tuneDesDocbyED(sCHCmpDes,sourceConceptNum,tCHCmpDes,targetConceptNum);
		tuneDesDocbyED(sCPDmnDes,sourceConceptNum,tCPDmnDes,targetConceptNum);
		tuneDesDocbyED(sCPRngDes,sourceConceptNum,tCPRngDes,targetConceptNum);
		tuneDesDocbyED(sCInsDes, sourceConceptNum,tCInsDes, targetConceptNum);
		
		/*��������*/
		mBasic=computeTFIDFSim(sCBasicDes,sourceConceptNum,tCBasicDes,targetConceptNum);
		/*�������*/
		mSub=computeTFIDFSim(sCHSubDes,sourceConceptNum,tCHSubDes,targetConceptNum);
		mSup=computeTFIDFSim(sCHSupDes,sourceConceptNum,tCHSupDes,targetConceptNum);
		mSbl=computeTFIDFSim(sCHSblDes,sourceConceptNum,tCHSblDes,targetConceptNum);
		mDsj=computeTFIDFSim(sCHDsjDes,sourceConceptNum,tCHDsjDes,targetConceptNum);
		mCmp=computeTFIDFSim(sCHCmpDes,sourceConceptNum,tCHCmpDes,targetConceptNum);
		/*������������*/
		mDmn=computeTFIDFSim(sCPDmnDes,sourceConceptNum,tCPDmnDes,targetConceptNum);
		mRng=computeTFIDFSim(sCPRngDes,sourceConceptNum,tCPRngDes,targetConceptNum);
		/*ʵ������*/
		mIns=computeTFIDFSim(sCInsDes, sourceConceptNum,tCInsDes, targetConceptNum);
		
		/*�ϲ����*/
		double wB0,wH0,wP0,wI0,wHsub0,wHsup0,wHsbl0,wHdsj0,wHcmp0,wPd0,wPr0;
		for(int i=0;i<sourceConceptNum;i++){
			for (int j=0;j<targetConceptNum;j++){
				
				/*���¼���ʵ�ʵ�Ȩ��*/
				/*1.���Ȩ��*/
				if (Math.abs(mSub[i][j]-(-1.0))<0.00001){wHsub0=0.0;} else{wHsub0=wHsub;}
				if (Math.abs(mSup[i][j]-(-1.0))<0.00001){wHsup0=0.0;} else{wHsup0=wHsup;}
				if (Math.abs(mSbl[i][j]-(-1.0))<0.00001){wHsbl0=0.0;} else{wHsbl0=wHsbl;}
				if (Math.abs(mDsj[i][j]-(-1.0))<0.00001){wHdsj0=0.0;} else{wHdsj0=wHdsj;}
				if (Math.abs(mCmp[i][j]-(-1.0))<0.00001){wHcmp0=0.0;} else{wHcmp0=wHcmp;}
				double wHTotal=wHsub0+wHsup0+wHsbl0+wHdsj0+wHcmp0;
				if (wHTotal>0.0){
					wHsub0=wHsub0/wHTotal;
					wHsup0=wHsup0/wHTotal;
					wHsbl0=wHsbl0/wHTotal;
					wHdsj0=wHdsj0/wHTotal;
					wHcmp0=wHcmp0/wHTotal;
				}
				/*2.����Ȩ��*/
				if (Math.abs(mDmn[i][j]-(-1.0))<0.00001){wPd0=0.0;} else{wPd0=wPd;}
				if (Math.abs(mRng[i][j]-(-1.0))<0.00001){wPr0=0.0;} else{wPr0=wPr;}
				double wPTotal=wPd0+wPr0;
				if (wPTotal>0.0){
					wPd0=wPd0/wPTotal;
					wPr0=wPr0/wPTotal;
				}
				/*3.��Ȩ��*/
				if (Math.abs(mBasic[i][j]-(-1.0))<0.00001){wB0=0.0;} else{wB0=wB;}
				if (Math.abs(wHTotal)<0.00001){wH0=0.0;} else{wH0=wH;}
				if (Math.abs(wPTotal)<0.00001){wP0=0.0;} else{wP0=wP;}
				if (Math.abs(mIns[i][j]-(-1.0))<0.00001){wI0=0.0;} else{wI0=wI;}
				double wTotal=wB0+wH0+wP0+wI0;
				if (wTotal>0.0){
					wB0=wB0/wTotal;
					wH0=wH0/wTotal;
					wP0=wP0/wTotal;
					wI0=wI0/wTotal;
				}
				
				if (Math.abs(wTotal)<0.00001){
					cSimMatrix[i][j]=0.0;
				}
				else{
					cSimMatrix[i][j]=wB0*mBasic[i][j]
									 +wH0*(wHsub0*mSub[i][j]+wHsup0*mSup[i][j]+wHsbl0*mSbl[i][j]+wHdsj0*mDsj[i][j]+wHcmp0*mCmp[i][j])
									 +wP0*(wPd0*mDmn[i][j]+wPr0*mRng[i][j])
									 +wI0*mIns[i][j];
				}
			}
		}
	}
	
	/****************
	 * ���������ı�����
	 ****************/
	public void getPropTextSim(boolean flag)
	{
		/*Ȩ��*/
		double wB=0.3;
		double wH=0.1;
		double wF=0.3;
		double wI=0.3;
		
		double wHsub=0.3;
		double wHsup=0.3;
		double wHsbl=0.4;
		
		double wFd=0.45;
		double wFr=0.45;
		double wFc=0.1;
		
		double wId=0.5;
		double wIr=0.5;
		
		if (flag){
			double[][] mDBasic=new double[sourceDataPropNum][targetDataPropNum];
			double[][] mDSub = new double[sourceDataPropNum][targetDataPropNum];
			double[][] mDSup = new double[sourceDataPropNum][targetDataPropNum];
			double[][] mDSbl = new double[sourceDataPropNum][targetDataPropNum];
			double[][] mDDmn = new double[sourceDataPropNum][targetDataPropNum];
			double[][] mDRng = new double[sourceDataPropNum][targetDataPropNum];
			double[][] mDChr = new double[sourceDataPropNum][targetDataPropNum];
			double[][] mDIDmn= new double[sourceDataPropNum][targetDataPropNum];
			double[][] mDIRng= new double[sourceDataPropNum][targetDataPropNum];
			
			double[][] mOBasic=new double[sourceObjPropNum][targetObjPropNum];
			double[][] mOSub = new double[sourceObjPropNum][targetObjPropNum];
			double[][] mOSup = new double[sourceObjPropNum][targetObjPropNum];
			double[][] mOSbl = new double[sourceObjPropNum][targetObjPropNum];
			double[][] mODmn = new double[sourceObjPropNum][targetObjPropNum];
			double[][] mORng = new double[sourceObjPropNum][targetObjPropNum];
			double[][] mOChr = new double[sourceObjPropNum][targetObjPropNum];
			double[][] mOIDmn= new double[sourceObjPropNum][targetObjPropNum];
			double[][] mOIRng= new double[sourceObjPropNum][targetObjPropNum];
			
			/**********Datatype Property***********/
			/*����༭���봦��Vector�е������*/
			tuneDesDocbyED(sDPBasicDes,sourceDataPropNum,tDPBasicDes,targetDataPropNum);
			tuneDesDocbyED(sDPHSubDes,sourceDataPropNum,tDPHSubDes,targetDataPropNum);
			tuneDesDocbyED(sDPHSupDes,sourceDataPropNum,tDPHSupDes,targetDataPropNum);
			tuneDesDocbyED(sDPHSblDes,sourceDataPropNum,tDPHSblDes,targetDataPropNum);
			tuneDesDocbyED(sDPFDmnDes,sourceDataPropNum,tDPFDmnDes,targetDataPropNum);
			tuneDesDocbyED(sDPFRngDes,sourceDataPropNum,tDPFRngDes,targetDataPropNum);
			tuneDesDocbyED(sDPFChrDes,sourceDataPropNum,tDPFChrDes,targetDataPropNum);
			tuneDesDocbyED(sDPIDmnDes,sourceDataPropNum,tDPIDmnDes,targetDataPropNum);
			tuneDesDocbyED(sDPIRngDes,sourceDataPropNum,tDPIRngDes,targetDataPropNum);
			
			/*��������*/
			mDBasic=computeTFIDFSim(sDPBasicDes,sourceDataPropNum,tDPBasicDes,targetDataPropNum);
			/*�������*/
			mDSub=computeTFIDFSim(sDPHSubDes,sourceDataPropNum,tDPHSubDes,targetDataPropNum);
			mDSup=computeTFIDFSim(sDPHSupDes,sourceDataPropNum,tDPHSupDes,targetDataPropNum);
			mDSbl=computeTFIDFSim(sDPHSblDes,sourceDataPropNum,tDPHSblDes,targetDataPropNum);
			/*��������*/
			mDDmn=computeTFIDFSim(sDPFDmnDes,sourceDataPropNum,tDPFDmnDes,targetDataPropNum);
			mDRng=computeTFIDFSim(sDPFRngDes,sourceDataPropNum,tDPFRngDes,targetDataPropNum);
			mDChr=computeTFIDFSim(sDPFChrDes,sourceDataPropNum,tDPFChrDes,targetDataPropNum);
			/*ʵ������*/
			mDIDmn=computeTFIDFSim(sDPIDmnDes,sourceDataPropNum,tDPIDmnDes,targetDataPropNum);
			mDIRng=computeTFIDFSim(sDPIRngDes,sourceDataPropNum,tDPIRngDes,targetDataPropNum);
			
			/*�ϲ����*/
			double wB0,wH0,wF0,wI0,wHsub0,wHsup0,wHsbl0,wFd0,wFr0,wFc0,wId0,wIr0;
			for(int i=0;i<sourceDataPropNum;i++){
				for (int j=0;j<targetDataPropNum;j++){
					
					/*���¼���ʵ�ʵ�Ȩ��*/
					/*1.���Ȩ��*/
					if (Math.abs(mDSub[i][j]-(-1.0))<0.00001){wHsub0=0.0;} else{wHsub0=wHsub;}
					if (Math.abs(mDSup[i][j]-(-1.0))<0.00001){wHsup0=0.0;} else{wHsup0=wHsup;}
					if (Math.abs(mDSbl[i][j]-(-1.0))<0.00001){wHsbl0=0.0;} else{wHsbl0=wHsbl;}
					double wHTotal=wHsub0+wHsup0+wHsbl0;
					if (wHTotal>0.0){
						wHsub0=wHsub0/wHTotal;
						wHsup0=wHsup0/wHTotal;
						wHsbl0=wHsbl0/wHTotal;
					}
					/*2.����Ȩ��*/
					if (Math.abs(mDDmn[i][j]-(-1.0))<0.00001){wFd0=0.0;} else{wFd0=wFd;}
					if (Math.abs(mDRng[i][j]-(-1.0))<0.00001){wFr0=0.0;} else{wFr0=wFr;}
					if (Math.abs(mDChr[i][j]-(-1.0))<0.00001){wFc0=0.0;} else{wFc0=wFc;}
					double wFTotal=wFd0+wFr0+wFc0;
					if (wFTotal>0.0){
						wFd0=wFd0/wFTotal;
						wFr0=wFr0/wFTotal;
						wFc0=wFc0/wFTotal;
					}
					/*3.ʵ��Ȩ��*/
					if (Math.abs(mDIDmn[i][j]-(-1.0))<0.00001){wId0=0.0;} else{wId0=wId;}
					if (Math.abs(mDIRng[i][j]-(-1.0))<0.00001){wIr0=0.0;} else{wIr0=wIr;}
					double wITotal=wId0+wIr0;
					if (wITotal>0.0){
						wId0=wId0/wITotal;
						wIr0=wIr0/wITotal;
					}
					/*4.��Ȩ��*/
					if (Math.abs(mDBasic[i][j]-(-1.0))<0.00001){wB0=0.0;} else{wB0=wB;}
					if (Math.abs(wHTotal)<0.00001){wH0=0.0;} else{wH0=wH;}
					if (Math.abs(wFTotal)<0.00001){wF0=0.0;} else{wF0=wF;}
					if (Math.abs(wITotal)<0.00001){wI0=0.0;} else{wI0=wI;}
					double wTotal=wB0+wH0+wF0+wI0;
					if (wTotal>0.0){
						wB0=wB0/wTotal;
						wH0=wH0/wTotal;
						wF0=wF0/wTotal;
						wI0=wI0/wTotal;
					}
					
					if (Math.abs(wTotal)<0.00001){
						dpSimMatrix[i][j]=0.0;
					}
					else{
						dpSimMatrix[i][j]=wB0*mDBasic[i][j]
							              +wH0*(wHsub0*mDSub[i][j]+wHsup0*mDSup[i][j]+wHsbl0*mDSbl[i][j])
							              +wF0*(wFd0*mDDmn[i][j]+wFr0*mDRng[i][j]+wFc0*mDChr[i][j])
							              +wI0*(wId0*mDIDmn[i][j]+wIr0*mDIRng[i][j]);
					}
				}
			}
			
			/**********Object Property***********/
			/*����༭���봦��Vector�е������*/
			tuneDesDocbyED(sOPBasicDes,sourceObjPropNum,tOPBasicDes,targetObjPropNum);
			tuneDesDocbyED(sOPHSubDes,sourceObjPropNum,tOPHSubDes,targetObjPropNum);
			tuneDesDocbyED(sOPHSupDes,sourceObjPropNum,tOPHSupDes,targetObjPropNum);
			tuneDesDocbyED(sOPHSblDes,sourceObjPropNum,tOPHSblDes,targetObjPropNum);
			tuneDesDocbyED(sOPFDmnDes,sourceObjPropNum,tOPFDmnDes,targetObjPropNum);
			tuneDesDocbyED(sOPFRngDes,sourceObjPropNum,tOPFRngDes,targetObjPropNum);
			tuneDesDocbyED(sOPFChrDes,sourceObjPropNum,tOPFChrDes,targetObjPropNum);
			tuneDesDocbyED(sOPIDmnDes,sourceObjPropNum,tOPIDmnDes,targetObjPropNum);
			tuneDesDocbyED(sOPIRngDes,sourceObjPropNum,tOPIRngDes,targetObjPropNum);
			
			/*��������*/
			mOBasic=computeTFIDFSim(sOPBasicDes,sourceObjPropNum,tOPBasicDes,targetObjPropNum);
			/*�������*/
			mOSub=computeTFIDFSim(sOPHSubDes,sourceObjPropNum,tOPHSubDes,targetObjPropNum);
			mOSup=computeTFIDFSim(sOPHSupDes,sourceObjPropNum,tOPHSupDes,targetObjPropNum);
			mOSbl=computeTFIDFSim(sOPHSblDes,sourceObjPropNum,tOPHSblDes,targetObjPropNum);
			/*��������*/
			mODmn=computeTFIDFSim(sOPFDmnDes,sourceObjPropNum,tOPFDmnDes,targetObjPropNum);
			mORng=computeTFIDFSim(sOPFRngDes,sourceObjPropNum,tOPFRngDes,targetObjPropNum);
			mOChr=computeTFIDFSim(sOPFChrDes,sourceObjPropNum,tOPFChrDes,targetObjPropNum);
			/*ʵ������*/
			mOIDmn=computeTFIDFSim(sOPIDmnDes,sourceObjPropNum,tOPIDmnDes,targetObjPropNum);
			mOIRng=computeTFIDFSim(sOPIRngDes,sourceObjPropNum,tOPIRngDes,targetObjPropNum);
			
			/*�ϲ����*/
			double wB1,wH1,wF1,wI1,wHsub1,wHsup1,wHsbl1,wFd1,wFr1,wFc1,wId1,wIr1;
			for(int i=0;i<sourceObjPropNum;i++){
				for (int j=0;j<targetObjPropNum;j++){
					
					/*���¼���ʵ�ʵ�Ȩ��*/
					/*1.���Ȩ��*/
					if (Math.abs(mOSub[i][j]-(-1.0))<0.00001){wHsub1=0.0;} else{wHsub1=wHsub;}
					if (Math.abs(mOSup[i][j]-(-1.0))<0.00001){wHsup1=0.0;} else{wHsup1=wHsup;}
					if (Math.abs(mOSbl[i][j]-(-1.0))<0.00001){wHsbl1=0.0;} else{wHsbl1=wHsbl;}
					double wHTotal=wHsub1+wHsup1+wHsbl1;
					if (wHTotal>0.0){
						wHsub1=wHsub1/wHTotal;
						wHsup1=wHsup1/wHTotal;
						wHsbl1=wHsbl1/wHTotal;
					}
					/*2.����Ȩ��*/
					if (Math.abs(mODmn[i][j]-(-1.0))<0.00001){wFd1=0.0;} else{wFd1=wFd;}
					if (Math.abs(mORng[i][j]-(-1.0))<0.00001){wFr1=0.0;} else{wFr1=wFr;}
					if (Math.abs(mOChr[i][j]-(-1.0))<0.00001){wFc1=0.0;} else{wFc1=wFc;}
					double wFTotal=wFd1+wFr1+wFc1;
					if (wFTotal>0.0){
						wFd1=wFd1/wFTotal;
						wFr1=wFr1/wFTotal;
						wFc1=wFc1/wFTotal;
					}
					/*3.ʵ��Ȩ��*/
					if (Math.abs(mOIDmn[i][j]-(-1.0))<0.00001){wId1=0.0;} else{wId1=wId;}
					if (Math.abs(mOIRng[i][j]-(-1.0))<0.00001){wIr1=0.0;} else{wIr1=wIr;}
					double wITotal=wId1+wIr1;
					if (wITotal>0.0){
						wId1=wId1/wITotal;
						wIr1=wIr1/wITotal;
					}
					/*4.��Ȩ��*/
					if (Math.abs(mOBasic[i][j]-(-1.0))<0.00001){wB1=0.0;} else{wB1=wB;}
					if (Math.abs(wHTotal)<0.00001){wH1=0.0;} else{wH1=wH;}
					if (Math.abs(wFTotal)<0.00001){wF1=0.0;} else{wF1=wF;}
					if (Math.abs(wITotal)<0.00001){wI1=0.0;} else{wI1=wI;}
					double wTotal=wB1+wH1+wF1+wI1;
					if (wTotal>0.0){
						wB1=wB1/wTotal;
						wH1=wH1/wTotal;
						wF1=wF1/wTotal;
						wI1=wI1/wTotal;
					}
					
					if (Math.abs(wTotal)<0.00001){
						opSimMatrix[i][j]=0.0;
					}
					else{
						opSimMatrix[i][j]=wB1*mOBasic[i][j]
							              +wH1*(wHsub1*mOSub[i][j]+wHsup1*mOSup[i][j]+wHsbl1*mOSbl[i][j])
							              +wF1*(wFd1*mODmn[i][j]+wFr1*mORng[i][j]+wFc1*mOChr[i][j])
							              +wI1*(wId1*mOIDmn[i][j]+wIr1*mOIRng[i][j]);
					}
				}
			}
		}
		else{
			double[][] mDBasic=new double[sourcePropNum][targetPropNum];
			double[][] mDSub = new double[sourcePropNum][targetPropNum];
			double[][] mDSup = new double[sourcePropNum][targetPropNum];
			double[][] mDSbl = new double[sourcePropNum][targetPropNum];
			double[][] mDDmn = new double[sourcePropNum][targetPropNum];
			double[][] mDRng = new double[sourcePropNum][targetPropNum];
			double[][] mDChr = new double[sourcePropNum][targetPropNum];
			double[][] mDIDmn= new double[sourcePropNum][targetPropNum];
			double[][] mDIRng= new double[sourcePropNum][targetPropNum];
			
			/*����༭���봦��Vector�е������*/
			tuneDesDocbyED(sPBasicDes,sourcePropNum,tPBasicDes,targetPropNum);
			tuneDesDocbyED(sPHSubDes,sourcePropNum,tPHSubDes,targetPropNum);
			tuneDesDocbyED(sPHSupDes,sourcePropNum,tPHSupDes,targetPropNum);
			tuneDesDocbyED(sPHSblDes,sourcePropNum,tPHSblDes,targetPropNum);
			tuneDesDocbyED(sPFDmnDes,sourcePropNum,tPFDmnDes,targetPropNum);
			tuneDesDocbyED(sPFRngDes,sourcePropNum,tPFRngDes,targetPropNum);
			tuneDesDocbyED(sPFChrDes,sourcePropNum,tPFChrDes,targetPropNum);
			tuneDesDocbyED(sPIDmnDes,sourcePropNum,tPIDmnDes,targetPropNum);
			tuneDesDocbyED(sPIRngDes,sourcePropNum,tPIRngDes,targetPropNum);
			
			/*��������*/
			mDBasic=computeTFIDFSim(sPBasicDes,sourcePropNum,tPBasicDes,targetPropNum);
			/*�������*/
			mDSub=computeTFIDFSim(sPHSubDes,sourcePropNum,tPHSubDes,targetPropNum);
			mDSup=computeTFIDFSim(sPHSupDes,sourcePropNum,tPHSupDes,targetPropNum);
			mDSbl=computeTFIDFSim(sPHSblDes,sourcePropNum,tPHSblDes,targetPropNum);
			/*��������*/
			mDDmn=computeTFIDFSim(sPFDmnDes,sourcePropNum,tPFDmnDes,targetPropNum);
			mDRng=computeTFIDFSim(sPFRngDes,sourcePropNum,tPFRngDes,targetPropNum);
			mDChr=computeTFIDFSim(sPFChrDes,sourcePropNum,tPFChrDes,targetPropNum);
			/*ʵ������*/
			mDIDmn=computeTFIDFSim(sPIDmnDes,sourcePropNum,tPIDmnDes,targetPropNum);
			mDIRng=computeTFIDFSim(sPIRngDes,sourcePropNum,tPIRngDes,targetPropNum);
			
			/*�ϲ����*/
			double wB0,wH0,wF0,wI0,wHsub0,wHsup0,wHsbl0,wFd0,wFr0,wFc0,wId0,wIr0;
			for(int i=0;i<sourcePropNum;i++){
				for (int j=0;j<targetPropNum;j++){
					/*���¼���ʵ�ʵ�Ȩ��*/
					/*1.���Ȩ��*/
					if (Math.abs(mDSub[i][j]-(-1.0))<0.00001){wHsub0=0.0;} else{wHsub0=wHsub;}
					if (Math.abs(mDSup[i][j]-(-1.0))<0.00001){wHsup0=0.0;} else{wHsup0=wHsup;}
					if (Math.abs(mDSbl[i][j]-(-1.0))<0.00001){wHsbl0=0.0;} else{wHsbl0=wHsbl;}
					double wHTotal=wHsub0+wHsup0+wHsbl0;
					if (wHTotal>0.0){
						wHsub0=wHsub0/wHTotal;
						wHsup0=wHsup0/wHTotal;
						wHsbl0=wHsbl0/wHTotal;
					}
					/*2.����Ȩ��*/
					if (Math.abs(mDDmn[i][j]-(-1.0))<0.00001){wFd0=0.0;} else{wFd0=wFd;}
					if (Math.abs(mDRng[i][j]-(-1.0))<0.00001){wFr0=0.0;} else{wFr0=wFr;}
					if (Math.abs(mDChr[i][j]-(-1.0))<0.00001){wFc0=0.0;} else{wFc0=wFc;}
					double wFTotal=wFd0+wFr0+wFc0;
					if (wFTotal>0.0){
						wFd0=wFd0/wFTotal;
						wFr0=wFr0/wFTotal;
						wFc0=wFc0/wFTotal;
					}
					/*3.ʵ��Ȩ��*/
					if (Math.abs(mDIDmn[i][j]-(-1.0))<0.00001){wId0=0.0;} else{wId0=wId;}
					if (Math.abs(mDIRng[i][j]-(-1.0))<0.00001){wIr0=0.0;} else{wIr0=wIr;}
					double wITotal=wId0+wIr0;
					if (wITotal>0.0){
						wId0=wId0/wITotal;
						wIr0=wIr0/wITotal;
					}
					/*4.��Ȩ��*/
					if (Math.abs(mDBasic[i][j]-(-1.0))<0.00001){wB0=0.0;} else{wB0=wB;}
					if (Math.abs(wHTotal)<0.00001){wH0=0.0;} else{wH0=wH;}
					if (Math.abs(wFTotal)<0.00001){wF0=0.0;} else{wF0=wF;}
					if (Math.abs(wITotal)<0.00001){wI0=0.0;} else{wI0=wI;}
					double wTotal=wB0+wH0+wF0+wI0;
					if (wTotal>0.0){
						wB0=wB0/wTotal;
						wH0=wH0/wTotal;
						wF0=wF0/wTotal;
						wI0=wI0/wTotal;
					}
					
					if (Math.abs(wTotal)<0.00001){
						pSimMatrix[i][j]=0.0;
					}
					else{
						pSimMatrix[i][j]=wB0*mDBasic[i][j]
							              +wH0*(wHsub0*mDSub[i][j]+wHsup0*mDSup[i][j]+wHsbl0*mDSbl[i][j])
							              +wF0*(wFd0*mDDmn[i][j]+wFr0*mDRng[i][j]+wFc0*mDChr[i][j])
							              +wI0*(wId0*mDIDmn[i][j]+wIr0*mDIRng[i][j]);
					}
				}
			}
		}
	}
	
	/****************
	 * ����ʵ���ı�����
	 ****************/
	public void getInsTextSim()
	{
		/*����༭���봦��Vector�е������*/
		tuneDesDocbyED(sourceInsTextDes,sourceInsNum,targetInsTextDes,targetInsNum);
		
		/*��������*/
		iSimMatrix=computeTFIDFSim(sourceInsTextDes,sourceInsNum,targetInsTextDes,targetInsNum);
	}
	
	/****************
	 * ������ͼ������Ԫ���ı���������
	 ****************/
	public ArrayList getOtTextSim(ArrayList desListA, ArrayList desListB)
	{
		ArrayList simList=new ArrayList();
		
		/*����༭���봦��Vector�е������*/
		tuneDesDocbyED(desListA,desListB);
		
		/*�������������ƶ�*/
		simList=computeTFIDFSim(desListA,desListB);
		
		return simList;
	}
	
	/****************
	 * ͨ���ı����Ƽ��㺯��
	 * ���룬�����ı�
	 * ��������ƾ���
	 ****************/
	public double[][] computeTFIDFSim(TextDes[] docA,int nDocA,TextDes[] docB,int nDocB)
	{
		TfIdfSim tfidf=new TfIdfSim();
		ArrayList[] wATF=new ArrayList[nDocA];
		ArrayList[] wBTF=new ArrayList[nDocB];
		ArrayList[] wAIDF=new ArrayList[nDocA];
		ArrayList[] wBIDF=new ArrayList[nDocB];
		ArrayList[] wA=new ArrayList[nDocA];
		ArrayList[] wB=new ArrayList[nDocB];
		double[][] matrix=new double[nDocA][nDocB];
		/*����TF*/
		for(int i=0;i<nDocA;i++){
			wATF[i]=new ArrayList();
			wATF[i]=tfidf.getDocTF(docA[i].text);
		}
		for(int i=0;i<nDocB;i++){
			wBTF[i]=new ArrayList();
			wBTF[i]=tfidf.getDocTF(docB[i].text);
		}
		
		/*����IDF*/
		ArrayList lt=new ArrayList();
		lt=tfidf.getDocIDF(docA,nDocA,docB,nDocB);
		wAIDF=(ArrayList[])lt.get(0);
		wBIDF=(ArrayList[])lt.get(1);
		
		/*����TF*IDFȨ��*/
		wA=tfidf.getTFIDFWeight( docA,nDocA,wATF,wAIDF);
		wB=tfidf.getTFIDFWeight( docB,nDocB,wBTF,wBIDF);
		
		/*�����ǶԳƵģ���m,n��һ����ͬ*/
		for (int i=0;i<nDocA;i++){
			for (int j=0;j<nDocB;j++){
				ArrayList vA=new ArrayList();
				ArrayList vB=new ArrayList();
				/*��������*/
				lt=tfidf.consTextVector( docA[i].text,docB[j].text,wA[i],wB[j]);
				vA=(ArrayList)lt.get(0);
				vB=(ArrayList)lt.get(1);
				/*������������*/
				if (vA.isEmpty()&& vB.isEmpty()){
					/*vA vB��Ϊ�գ�˵�����߶�û���ⷽ������������ƶ�ֱ�Ӹ�ֵ-1.0*/
					matrix[i][j]=-1.0;
				}
				else{
					matrix[i][j]=tfidf.getTextVectorSim(vA,vB);
				}				
			}
		}
		return matrix;
	}
	
	public ArrayList computeTFIDFSim(ArrayList desListA, ArrayList desListB)
	{
		ArrayList result=new ArrayList();
		int nDocA=desListA.size();
		int nDocB=desListB.size();
		TfIdfSim tfidf=new TfIdfSim();
		ArrayList[] wATF=new ArrayList[nDocA];
		ArrayList[] wBTF=new ArrayList[nDocB];
		ArrayList[] wAIDF=new ArrayList[nDocA];
		ArrayList[] wBIDF=new ArrayList[nDocB];
		ArrayList[] wA=new ArrayList[nDocA];
		ArrayList[] wB=new ArrayList[nDocB];
				
		/*����TF*/
		int count=0;
		for(Iterator it=desListA.iterator();it.hasNext();){
			TextDes des=(TextDes)it.next();
			wATF[count]=new ArrayList();
			wATF[count]=tfidf.getDocTF(des.text);
			count++;
		}
		count=0;
		for(Iterator it=desListB.iterator();it.hasNext();){
			TextDes des=(TextDes)it.next();
			wBTF[count]=new ArrayList();
			wBTF[count]=tfidf.getDocTF(des.text);
			count++;
		}
		
		/*����IDF*/
		ArrayList lt=new ArrayList();
		lt=tfidf.getDocIDF((TextDes[])desListA.toArray(new TextDes[0]),nDocA,(TextDes[])desListB.toArray(new TextDes[0]),nDocB);
		wAIDF=(ArrayList[])lt.get(0);
		wBIDF=(ArrayList[])lt.get(1);
		
		/*����TF*IDFȨ��*/
		wA=tfidf.getTFIDFWeight((TextDes[])desListA.toArray(new TextDes[0]),nDocA,wATF,wAIDF);
		wB=tfidf.getTFIDFWeight((TextDes[])desListB.toArray(new TextDes[0]),nDocB,wBTF,wBIDF);
		
		/*�����ǶԳƵģ���m,n��һ����ͬ*/
		int counta=0,countb=0;
		for (Iterator itx=desListA.iterator();itx.hasNext();){
			TextDes desA=(TextDes)itx.next();
			countb=0;
			if (desA.text.isEmpty()){continue;}
			for (Iterator ity=desListB.iterator();ity.hasNext();){
				TextDes desB=(TextDes)ity.next();
				if (desB.text.isEmpty()){continue;}
				if (desA.type != desB.type) {
					countb++;
					continue;
				}//������ͬ���͵Ķ������
				ArrayList vA=new ArrayList();
				ArrayList vB=new ArrayList();
				/*��������*/
				lt=tfidf.consTextVector(desA.text,desB.text,wA[counta],wB[countb]);
				vA=(ArrayList)lt.get(0);
				vB=(ArrayList)lt.get(1);
				/*������������*/
				if (vA.isEmpty() || vB.isEmpty()){
					/*vA vB��Ϊ�գ�˵�����߶�û���ⷽ������������ƶ�ֱ�Ӹ�ֵ-1.0*/
					//��������²���¼
				}
				else{
					GraphElmSim simPair=new GraphElmSim();
					simPair.elmNameA=desA.name;
					simPair.elmNameB=desB.name;
					simPair.sim =tfidf.getTextVectorSim(vA,vB);
					/*���˹�С�����ƶ�*/
					if (simPair.sim>0.5){
						result.add(simPair);
					}					
				}
				countb++;
			}
			counta++;
		}
		return result;
	}
	
	/**********************
	 * ���ձ������
	 ********************/
	public void unPackPara(ArrayList paraList)
	{
		sourceConceptNum=((Integer)paraList.get(0)).intValue();
		sourcePropNum=((Integer)paraList.get(1)).intValue();
		sourceDataPropNum=((Integer)paraList.get(2)).intValue();
		sourceObjPropNum=((Integer)paraList.get(3)).intValue();
		sourceInsNum=((Integer)paraList.get(20)).intValue();
		
		targetConceptNum=((Integer)paraList.get(10)).intValue();
		targetPropNum=((Integer)paraList.get(11)).intValue();
		targetDataPropNum=((Integer)paraList.get(12)).intValue();
		targetObjPropNum=((Integer)paraList.get(13)).intValue();
		targetInsNum=((Integer)paraList.get(23)).intValue();
		
		//���ݵõ���number��ʼ����������
		initPara();

		sourceConceptName=(String[])(paraList.get(4));
		sourcePropName=(String[])(paraList.get(5));
		sourceDataPropName=(String[])(paraList.get(6));
		sourceObjPropName=(String[])(paraList.get(7));
		sourceInsName=(String[])(paraList.get(21));
		
		sourceCnptTextDes=(TextDes[])(paraList.get(8));
		sourcePropTextDes=(TextDes[])(paraList.get(9));
		sourceInsTextDes=(TextDes[])(paraList.get(22));

		targetConceptName=(String[])(paraList.get(14));
		targetPropName=(String[])(paraList.get(15));
		targetDataPropName=(String[])(paraList.get(16));
		targetObjPropName=(String[])(paraList.get(17));
		targetInsName=(String[])(paraList.get(24));

		targetCnptTextDes=(TextDes[])(paraList.get(18));
		targetPropTextDes=(TextDes[])(paraList.get(19));
		targetInsTextDes=(TextDes[])(paraList.get(25));
	}
	
	/**********************
	 * ��ʼ�������һЩ���ݽṹ
	 ********************/
	public void initPara()
	{
		/*������Ϣ*/
		sourceConceptName=new String[sourceConceptNum];
		sourcePropName=new String[sourcePropNum];
		sourceDataPropName=new String[sourceDataPropNum];
		sourceObjPropName=new String[sourceObjPropNum];
		sourceInsName=new String[sourceInsNum];
		
		targetConceptName=new String[targetConceptNum];
		targetPropName=new String[targetPropNum];
		targetDataPropName=new String[targetDataPropNum];
		targetObjPropName=new String[targetObjPropNum];
		targetInsName=new String[targetInsNum];
		
		/*������Ϣ*/
		sourceCnptTextDes=new TextDes[sourceConceptNum];
		sourcePropTextDes=new TextDes[sourcePropNum];
		sourceInsTextDes=new TextDes[sourceInsNum];
		
		targetCnptTextDes=new TextDes[targetConceptNum];
		targetPropTextDes=new TextDes[targetPropNum];
		targetInsTextDes=new TextDes[targetInsNum];
		
		sCBasicDes=new TextDes[sourceConceptNum];
		sCHSubDes=new TextDes[sourceConceptNum];
		sCHSupDes=new TextDes[sourceConceptNum];
		sCHSblDes=new TextDes[sourceConceptNum];
		sCHDsjDes=new TextDes[sourceConceptNum];
		sCHCmpDes=new TextDes[sourceConceptNum];
		sCPDmnDes=new TextDes[sourceConceptNum];
		sCPRngDes=new TextDes[sourceConceptNum];
		sCInsDes=new TextDes[sourceConceptNum];
		
		sPBasicDes=new TextDes[sourcePropNum];
		sPHSubDes=new TextDes[sourcePropNum];
		sPHSupDes=new TextDes[sourcePropNum];
		sPHSblDes=new TextDes[sourcePropNum];
		sPFDmnDes=new TextDes[sourcePropNum];
		sPFRngDes=new TextDes[sourcePropNum];
		sPFChrDes=new TextDes[sourcePropNum];
		sPIDmnDes=new TextDes[sourcePropNum];
		sPIRngDes=new TextDes[sourcePropNum];
		
		sDPBasicDes=new TextDes[sourceDataPropNum];
		sDPHSubDes=new TextDes[sourceDataPropNum];
		sDPHSupDes=new TextDes[sourceDataPropNum];
		sDPHSblDes=new TextDes[sourceDataPropNum];
		sDPFDmnDes=new TextDes[sourceDataPropNum];
		sDPFRngDes=new TextDes[sourceDataPropNum];
		sDPFChrDes=new TextDes[sourceDataPropNum];
		sDPIDmnDes=new TextDes[sourceDataPropNum];
		sDPIRngDes=new TextDes[sourceDataPropNum];
		
		sOPBasicDes=new TextDes[sourceObjPropNum];
		sOPHSubDes=new TextDes[sourceObjPropNum];
		sOPHSupDes=new TextDes[sourceObjPropNum];
		sOPHSblDes=new TextDes[sourceObjPropNum];
		sOPFDmnDes=new TextDes[sourceObjPropNum];
		sOPFRngDes=new TextDes[sourceObjPropNum];
		sOPFChrDes=new TextDes[sourceObjPropNum];
		sOPIDmnDes=new TextDes[sourceObjPropNum];
		sOPIRngDes=new TextDes[sourceObjPropNum];		
		
		tCBasicDes=new TextDes[targetConceptNum];
		tCHSubDes=new TextDes[targetConceptNum];
		tCHSupDes=new TextDes[targetConceptNum];
		tCHSblDes=new TextDes[targetConceptNum];
		tCHDsjDes=new TextDes[targetConceptNum];
		tCHCmpDes=new TextDes[targetConceptNum];
		tCPDmnDes=new TextDes[targetConceptNum];
		tCPRngDes=new TextDes[targetConceptNum];
		tCInsDes=new TextDes[targetConceptNum];
		
		tPBasicDes=new TextDes[targetPropNum];
		tPHSubDes=new TextDes[targetPropNum];
		tPHSupDes=new TextDes[targetPropNum];
		tPHSblDes=new TextDes[targetPropNum];
		tPFDmnDes=new TextDes[targetPropNum];
		tPFRngDes=new TextDes[targetPropNum];
		tPFChrDes=new TextDes[targetPropNum];
		tPIDmnDes=new TextDes[targetPropNum];
		tPIRngDes=new TextDes[targetPropNum];
		
		tDPBasicDes=new TextDes[targetDataPropNum];
		tDPHSubDes=new TextDes[targetDataPropNum];
		tDPHSupDes=new TextDes[targetDataPropNum];
		tDPHSblDes=new TextDes[targetDataPropNum];
		tDPFDmnDes=new TextDes[targetDataPropNum];
		tDPFRngDes=new TextDes[targetDataPropNum];
		tDPFChrDes=new TextDes[targetDataPropNum];
		tDPIDmnDes=new TextDes[targetDataPropNum];
		tDPIRngDes=new TextDes[targetDataPropNum];
		
		tOPBasicDes=new TextDes[targetObjPropNum];
		tOPHSubDes=new TextDes[targetObjPropNum];
		tOPHSupDes=new TextDes[targetObjPropNum];
		tOPHSblDes=new TextDes[targetObjPropNum];
		tOPFDmnDes=new TextDes[targetObjPropNum];
		tOPFRngDes=new TextDes[targetObjPropNum];
		tOPFChrDes=new TextDes[targetObjPropNum];
		tOPIDmnDes=new TextDes[targetObjPropNum];
		tOPIRngDes=new TextDes[targetObjPropNum];
		
		/*���ƶ�*/
		cSimMatrix=new double[sourceConceptNum][targetConceptNum];
		pSimMatrix=new double[sourcePropNum][targetPropNum];
		dpSimMatrix=new double[sourceDataPropNum][targetDataPropNum];
		opSimMatrix=new double[sourceObjPropNum][targetObjPropNum];
		iSimMatrix=new double[sourceInsNum][targetInsNum];
	}
	
	/**********************
	 * ����ַ������������������ĵ�
	 ********************/
	public void tuneDesDocbyED(TextDes[] docA,int nDocA,TextDes[] docB,int nDocB){
		
		StrEDSim edsim= new StrEDSim();
		
		/*1.�ϲ�ͬDoc�е����Ƶ�term*/
		for (int i=0;i<nDocA;i++){
			ArrayList termList=docA[i].text;
			combineTermIntraDoc(termList);
		}
		for (int i=0;i<nDocB;i++){
			ArrayList termList=docB[i].text;
			combineTermIntraDoc(termList);
		}
		
		/*2.�ϲ���ͬDoc�е����Ƶ�term*/
		for (int i=0;i<nDocA;i++){
			ArrayList termListA=docA[i].text;
			for (int j=0;j<nDocB;j++){
				ArrayList termlistB=docB[j].text;
				
				Word w1=new Word();
				Word w2=new Word();
				for (Iterator itx=termListA.iterator();itx.hasNext();){
					w1=(Word)itx.next();
					for (Iterator ity=termlistB.iterator();ity.hasNext();){
						w2=(Word)ity.next();
						if (!w1.content.equals(w2.content) 
								&& edsim.getNormEDSim(w1.content,w2.content)>edThreshold){
							/*ͳһterm,��AΪ׼*/
							w2.content=w1.content;
						}
					}
				}
			}
		}
	}
	
	public void tuneDesDocbyED(ArrayList desListA,ArrayList desListB){
		
		StrEDSim edsim= new StrEDSim();
		
		/*1.�ϲ�ͬDoc�е����Ƶ�term*/
		for (Iterator it=desListA.iterator();it.hasNext();){
			TextDes des=(TextDes)it.next();
			ArrayList termList=des.text;
			combineTermIntraDoc(termList);
		}
		for (Iterator it=desListB.iterator();it.hasNext();){
			TextDes des=(TextDes)it.next();
			ArrayList termList=des.text;
			combineTermIntraDoc(termList);
		}
		
		/*2.�ϲ���ͬDoc�е����Ƶ�term*/
		for (Iterator itx=desListA.iterator();itx.hasNext();){
			TextDes desA=(TextDes)itx.next();
			ArrayList termListA=desA.text;
			for (Iterator ity=desListB.iterator();ity.hasNext();){
				TextDes desB=(TextDes)ity.next();
				ArrayList termListB=desB.text;
								
				Word w1=new Word();
				Word w2=new Word();
				for (Iterator jtx=termListA.iterator();jtx.hasNext();){
					w1=(Word)jtx.next();
					for (Iterator jty=termListB.iterator();jty.hasNext();){
						w2=(Word)jty.next();
						if (!w1.content.equals(w2.content) 
								&& edsim.getNormEDSim(w1.content,w2.content)>edThreshold){
							/*ͳһterm,��AΪ׼*/
							w2.content=w1.content;
						}
					}
				}
			}
		}
	}
	
	/**********************
	 * �ϲ�ͬDoc�е����Ƶ�term
	 ********************/
	@SuppressWarnings("unchecked")
	public void combineTermIntraDoc(ArrayList termList){
		StrEDSim edsim= new StrEDSim();
		/*ȡ����ǰ�ĵ���ȫ��term*/
		Set termSet=new HashSet();
		for(Iterator it=termList.iterator();it.hasNext();){
			Word w=(Word)it.next();
			termSet.add(w);
		}
		/*�ϲ�term��ֱ��û�����Ƶ�termΪֹ*/
		boolean hasSimTerm=false;
		while(!hasSimTerm){
			hasSimTerm=true;
			Word w1=new Word();
			Word w2=new Word();
			for (Iterator itx=termSet.iterator();itx.hasNext();){
				w1=(Word)itx.next();
				for (Iterator ity=termSet.iterator();ity.hasNext();){
					w2=(Word)ity.next();
					if (!w1.content.equals(w2.content) 
							&& edsim.getNormEDSim(w1.content,w2.content)>edThreshold){
						hasSimTerm=false;
						break;
					}
				}
				if (!hasSimTerm){break;}
			}
			/*�������ƵĴʣ����кϲ���
			 * ��w2�ϲ���w1*/
			if (!hasSimTerm){
				w1.weight=w1.weight+w2.weight;
				termSet.remove(w2);
			}
		}
		/*�ϲ����������¹���doc[i]���ĵ�*/
		termList.clear();
		termList.addAll(termSet);
	}
}

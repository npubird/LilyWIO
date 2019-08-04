/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-8-20
 * Filename          FullSimPropagation.java
 * Version           2.0
 * 
 * Last modified on  2007-8-20
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 *
 ***********************************************/
package lily.onto.handle.propagation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lily.onto.parse.OWLOntParse;
import lily.tool.datastructure.ConceptSubGraph;
import lily.tool.datastructure.GraphElmSim;
import lily.tool.datastructure.PairGraphRes;
import lily.tool.datastructure.PropertySubGraph;
import lily.tool.datastructure.TriplePair;
import lily.tool.textsimilarity.TfIdfSim;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-8-20
 * 
 * describe:
 *  ȫ�ֱ���ͼ�����ƶȴ���
 ********************/
public class FullSimPropagation {
	/*���ݳ�Ա*/
	public OntModel m_source;
	public OntModel m_target;
	/*****Դ����******/
	public int s_cnptNum;//������Ŀ
	public int s_propNum;//������Ŀ
	public int s_insNum;//ʵ����Ŀ
	public String[] s_cnptName;//������
	public String[] s_propName;//������
	public String[] s_insName;//ʵ����
	public ArrayList s_stmList;//ͼ
	public String s_baseURI;
	
	/*****Ŀ�걾��******/
	public int t_cnptNum;//������Ŀ
	public int t_propNum;//������Ŀ
	public int t_insNum;//ʵ����Ŀ
	public String[] t_cnptName;//������
	public String[] t_propName;//������
	public String[] t_insName;//������
	public ArrayList t_stmList;//ͼ
	public String t_baseURI;
	
	/*****������Դ*****/
	ArrayList s_AnonCnpt;
	ArrayList s_AnonProp;
	ArrayList s_AnonIns;	
	ArrayList t_AnonCnpt;
	ArrayList t_AnonProp;
	ArrayList t_AnonIns;	
	
	/******����ͼ��Ϣ******/
	public Set[][] cnptTriplePair;//��ͼ������Ԫ��Լ���
	public Set[][] propTriplePair;//��ͼ������Ԫ��Լ���
	
	/******���ƶȾ���******/
	public double[][] cnptSimRaw;//ԭʼ�������ƶ�
	public double[][] cnptSimK0;//k�ε������ƶ�
	public double[][] cnptSimK1;//k+1�ε������ƶ�
	public double[][] propSimRaw;//ԭʼ�������ƶ�
	public double[][] propSimK0;//k�ε������ƶ�
	public double[][] propSimK1;//k+1�ε������ƶ�
	public double[][] insSimRaw;//ԭʼʵ�����ƶ�
	/******�������ƶȾ���****/
	//����Ԫ�ؼ�����ƶȾ���
	public ArrayList otElmSim;
	/*ԭʼ���ƶȵ�Hash*/
	HashMap cnptSimMap;
	HashMap propSimMap;
	HashMap insSimMap;
	
	/*****�����ѡ��Ԫ�鼯��*****/
	public Set[][] cnptCandiTPSet;
	/*****���Ժ�ѡ��Ԫ�鼯��*****/
	public Set[][] propCandiTPSet;
		
	//����Ԫ��Ϣ
	public Set ontLngURI;	
	public OWLOntParse ontParse;
	
	//��ͼ���±�־
	private boolean hasGUpdate;
	private boolean updateCnpt[][];
	private boolean updateProp[][];

	//��ǰ������ͼ���ͱ�־
	private boolean flagCnptSugG;
	private String curCnptA,curCnptB;
	private int curCnptIDA,curCnptIDB;
	private boolean flagPropSugG;
	private String curPropA,curPropB;	
	private int curPropIDA,curPropIDB;
	
	//��������������
	int maxProgTimes=8;
	//��ͼ���±�־
	boolean sGUpdate;
	
	/*******************
	 * ��������
	 *******************/
	public ArrayList ontSimPg(ArrayList paraList) {
		ArrayList result=new ArrayList();
		
		/*��������*/
		unPackPara(paraList);
		
		int times=0;
		
		long start = System.currentTimeMillis();// ��ʼ��ʱ

		// ������ͼ�����ƶȴ���
		simPropagation();
//		hasGUpdate = isGlobalConvergence();

		long end = System.currentTimeMillis();// ������ʱ
		long costtime = end - start;//ͳ���㷨ʱ��
		System.out.println("���ƶȴ����㷨ʱ�䣺"+(double)costtime/1000.+"��");
		
		cnptSimRaw=cnptSimK0.clone();
		for (int i=0;i<s_cnptNum;i++){
			if (cnptSimRaw[i]!=null){
				cnptSimRaw[i]=(double[])cnptSimK0[i].clone();
			}
		}
		propSimRaw=propSimK0.clone();
		for (int i=0;i<s_propNum;i++){
			if (propSimRaw[i]!=null){
				propSimRaw[i]=(double[])propSimK0[i].clone();
			}
		}
		result.add(0,cnptSimRaw);
		result.add(1,propSimRaw);
		return result;
	}
	
	/**********************
	 * ���ձ������
	 ********************/
	private void unPackPara(ArrayList paraList)
	{
		m_source=(OntModel)paraList.get(0);
		m_target=(OntModel)paraList.get(1); 
		
		s_cnptNum=((Integer)paraList.get(2)).intValue();
		s_propNum=((Integer)paraList.get(3)).intValue();
		s_insNum=((Integer)paraList.get(4)).intValue();
		t_cnptNum=((Integer)paraList.get(10)).intValue();
		t_propNum=((Integer)paraList.get(11)).intValue();
		t_insNum=((Integer)paraList.get(12)).intValue();
		
		//���ݵõ���number��ʼ����������
		initPara();
		
		s_cnptName=(String[])(paraList.get(5));
		s_propName=(String[])(paraList.get(6));
		s_insName=(String[])(paraList.get(7));
		
		s_stmList=(ArrayList)(paraList.get(8));
		
		s_baseURI=(String)(paraList.get(9));
		
		t_cnptName=(String[])(paraList.get(13));
		t_propName=(String[])(paraList.get(14));
		t_insName=(String[])(paraList.get(15));
		
		t_stmList=(ArrayList)(paraList.get(16));
		
		t_baseURI=(String)(paraList.get(17));
		
		cnptSimRaw=(double[][])(paraList.get(18));
		propSimRaw=(double[][])(paraList.get(19));
		insSimRaw=(double[][])(paraList.get(20));
		
		s_AnonCnpt=(ArrayList)(paraList.get(21));
		s_AnonProp=(ArrayList)(paraList.get(22));
		s_AnonIns=(ArrayList)(paraList.get(23));
		t_AnonCnpt=(ArrayList)(paraList.get(24));
		t_AnonProp=(ArrayList)(paraList.get(25));
		t_AnonIns=(ArrayList)(paraList.get(26));
		
		otElmSim=(ArrayList)(paraList.get(27));
		
		//��¡ԭʼ���ƶ�
		cnptSimK0=cnptSimRaw.clone();
		for (int i=0;i<s_cnptNum;i++){
			if (cnptSimK0[i]!=null){
				cnptSimK0[i]=(double[])cnptSimRaw[i].clone();
			}
		}
		propSimK0=propSimRaw.clone();
		for (int i=0;i<s_propNum;i++){
			if (propSimK0[i]!=null){
				propSimK0[i]=(double[])propSimRaw[i].clone();
			}
		}
		
		//Hash�������ƾ���
		cnptSimMap=new HashMap();
		for (int i=0;i<s_cnptNum;i++){
			for (int j=0;j<t_cnptNum;j++){
				cnptSimMap.put(s_cnptName[i]+t_cnptName[j],cnptSimRaw[i][j]);
			}
		}
		propSimMap=new HashMap();
		for (int i=0;i<s_propNum;i++){
			for (int j=0;j<t_propNum;j++){
				propSimMap.put(s_propName[i]+t_propName[j],propSimRaw[i][j]);
			}
		}
		insSimMap=new HashMap();
		for (int i=0;i<s_insNum;i++){
			for (int j=0;j<t_insNum;j++){
				insSimMap.put(s_insName[i]+t_insName[j],insSimRaw[i][j]);
			}
		}
	}
	
	/**********************
	 * ��ʼ��
	 ********************/
	private void initPara()
	{
		s_cnptName=new String[s_cnptNum];
		s_propName=new String[s_propNum];
		s_insName=new String[s_insNum];
		t_cnptName=new String[t_cnptNum];
		t_propName=new String[t_propNum];
		t_insName=new String[t_insNum];
		cnptSimRaw=new double[s_cnptNum][t_cnptNum];
		propSimRaw=new double[s_propNum][t_propNum];
		insSimRaw=new double[s_insNum][t_insNum];
		
		otElmSim=new ArrayList();
				
		cnptTriplePair=new Set[s_cnptNum][t_cnptNum];
		propTriplePair=new Set[s_propNum][t_propNum];
		
		cnptCandiTPSet=new Set[s_cnptNum][t_cnptNum];
		propCandiTPSet=new Set[s_propNum][t_propNum];
		
		ontParse=new OWLOntParse();
		ontLngURI=ontParse.metaURISet;
		
		cnptSimK0=new double[s_cnptNum][t_cnptNum];
		cnptSimK1=new double[s_cnptNum][t_cnptNum];
		propSimK0=new double[s_propNum][t_propNum];
		propSimK1=new double[s_propNum][t_propNum];
		
		updateCnpt=new boolean[s_cnptNum][t_cnptNum];
		updateProp=new boolean[s_propNum][t_propNum];
	}
	
	/******************
	 * ���ƶȵĴ��� 
	 *****************/
	private void simPropagation() {
		Set gNodes=new HashSet();
		Set gEdges=new HashSet();	
		HashMap edgeMap=new HashMap();
		HashMap progWeightMap=new HashMap();
		ArrayList lt=new ArrayList();
		
		//��ѡ������Ԫ���
		Set candiTPSet=new HashSet();
		candiTPSet=getSimTripleCandidate(s_stmList,t_stmList );
		
		lt=consTriplePairGraph(candiTPSet);
		gNodes=(HashSet)lt.get(0);
		gEdges=(HashSet)lt.get(1);
		edgeMap=(HashMap)lt.get(2);
		progWeightMap=(HashMap)lt.get(3);
		
		Set oldCTPSet=new HashSet();//�жϸ��µı�־����
		
		sGUpdate=true;
		int cn=0;
		while (sGUpdate && cn<6){
			//���ƶȴ�����ֱ����ͼ���ٸ���Ϊֹ
			propagation(edgeMap,progWeightMap);
			
			//���º�ѡ��Ԫ������ƶ�
			candiTPSet=updateTriplePairSim(candiTPSet,edgeMap);
			
			//���ϵ�ctp�Ƚ�
//			sGUpdate=isSameCTPSet(candiTPSet,oldCTPSet);
			
			//��¼��ǰ��ctp
//			oldCTPSet=((Set)((HashSet)candiTPSet).clone());
			
			cn++;
			//������ͼ
			if (cn<6){
				updateTripleCandidate(s_stmList,t_stmList,candiTPSet,edgeMap);
				lt=consTriplePairGraph(candiTPSet);
				gNodes=(HashSet)lt.get(0);
				gEdges=(HashSet)lt.get(1);
				edgeMap=(HashMap)lt.get(2);
				progWeightMap=(HashMap)lt.get(3);
			}
		}
		
		/*��¼���ƶȴ������*/
		for (int i = 0; i < s_cnptNum; i++) {
			for (int j = 0; j < t_cnptNum; j++) {
				// ͨ��ͼ�ĵ��ҵ����еĸ������ƶ�
				for (Iterator it = gNodes.iterator(); it.hasNext();) {
					PairGraphRes pair = (PairGraphRes) it.next();
					/* ����ֻ�ܳ����ڵ��ϣ���˲�ȡedgepair */

					// �ж����ƶ�
					if ((s_baseURI + s_cnptName[i]).equals(pair.resA.toString())
							&& (t_baseURI + t_cnptName[j]).equals(pair.resB.toString())) {
						cnptSimK0[i][j] = pair.sim0;
						break;
					}
				}
			}
		}
		
		for(int i=0;i<s_propNum;i++){
			for (int j=0;j<t_propNum;j++){
				//ͨ��ͼ�ĵ�ͱ��ҵ����е��������ƶ�
				double tempPSim=0;
				for (Iterator itx=edgeMap.entrySet().iterator();itx.hasNext();){
					java.util.Map.Entry entry=(java.util.Map.Entry)itx.next();
					ArrayList t2PairRes=(ArrayList)entry.getValue();
					PairGraphRes pairS=(PairGraphRes)t2PairRes.get(0);
					PairGraphRes pairO=(PairGraphRes)t2PairRes.get(1);
					PairGraphRes pairP=(PairGraphRes)t2PairRes.get(2);
					
					//�ж����ƶ�
					if ((s_baseURI+s_propName[i]).equals(pairS.resA.toString())
						&& (t_baseURI+t_propName[j]).equals(pairS.resB.toString())){
						tempPSim=Math.max(tempPSim,pairS.sim0);
						propSimK0[i][j]=tempPSim;
						break;
					}
					if ((s_baseURI+s_propName[i]).equals(pairP.resA.toString())
							&& (t_baseURI+t_propName[j]).equals(pairP.resB.toString())){
							tempPSim=Math.max(tempPSim,pairP.sim0);
							propSimK0[i][j]=tempPSim;
							break;
						}
					if ((s_baseURI+s_propName[i]).equals(pairO.resA.toString())
							&& (t_baseURI+t_propName[j]).equals(pairO.resB.toString())){
							tempPSim=Math.max(tempPSim,pairO.sim0);
							propSimK0[i][j]=tempPSim;
							break;
						}					
				}
			}
		}
	}
	
	/*******************
	 * ѡ���ѡ������Ԫ��
	 *******************/
	private Set getSimTripleCandidate(ArrayList s_Stm,ArrayList t_Stm) {
		Set tPairSet=new HashSet();
		
		/*����������Ԫ�鼯��*/
		double weight=0;//����ߵ�weight
		int ct=0;
		for (Iterator itx=s_Stm.iterator();itx.hasNext();){
			Statement stA=(Statement)itx.next();
			if (metaElmInTriple(stA)>=2){
				continue;
			}
			
			System.out.println("ѡ���ѡ������Ԫ��"+ct++);
			
			//������Ԫ��
			Resource subA=stA.getSubject();
			Property propA=stA.getPredicate();
			RDFNode objA=stA.getObject();
			String subAName=subA.toString();
			String propAName=propA.toString();
			String objAName=objA.toString();
			String urlSA=null,urlPA=null,urlOA=null;
			if (subA.isURIResource()){urlSA=subA.getNameSpace();}
			if (propA.isURIResource()){urlPA=propA.getNameSpace();}
			if (objA.isURIResource()){urlOA=objA.asNode().getNameSpace();}
			
			for (Iterator ity=t_Stm.iterator();ity.hasNext();){
				Statement stB=(Statement)ity.next();
				if (metaElmInTriple(stB)>=2){
					continue;
				}
				Resource subB=stB.getSubject();
				Property propB=stB.getPredicate();
				RDFNode objB=stB.getObject();
				String subBName=subB.toString();
				String propBName=propB.toString();
				String objBName=objB.toString();
				String urlSB=null,urlPB=null,urlOB=null;
				if (subB.isURIResource()){urlSB=subB.getNameSpace();}
				if (propB.isURIResource()){urlPB=propB.getNameSpace();}
				if (objB.isURIResource()){urlOB=objB.asNode().getNameSpace();}
				//�ж�������Ԫ���
				/*(1)����������Ԫ��,�Ѿ��ж�*/				
				/*(2)����һ�Է�Ԫ������*/
				//(a)��Ӧλ�óɷ�Ҫ��Ӧ
				//(b)��Ӧλ��Ҫ�������ƶ�
				int typeA, typeB;
				double simS,simP,simO;
				boolean metaS,metaP,metaO;
				int simElmNum=0;
				//�ж�s-s
				typeA=getResourceType(subAName,m_source);
				typeB=getResourceType(subBName,m_target);
				simS=-1.0;
				metaS=false;
				//�ж��ǲ���Ԫ��
				if (ontLngURI.contains(urlSA) && ontLngURI.contains(urlSB)){
					//�Ƿ�����ͬ��Ԫ��
					if (subAName.equals(subBName)){
						simS=1.0;
						metaS=true;
					}
					else{
						//Ԫ���������ͬ��ֱ��������ǰtriple-pair
						continue;
					}
				}
				//������Ԫ������
				else if (!ontLngURI.contains(urlSA) && !ontLngURI.contains(urlSB)){
					if (typeA==typeB){
						//��ͬ�����ͣ���Ҫȷ�����ƶ�
						simS=getElmSim(subA,subB,typeA);						
					}
					else{
						continue;
					}
				}
				if (simS < 0) {	simS = 0; }
				if (simS > 0) {	simElmNum++; }

				//�ж�p-p
				typeA=getResourceType(propAName,m_source);
				typeB=getResourceType(propBName,m_target);
				simP=-1.0;
				metaP=false;
				//�ж��ǲ���Ԫ��
				if (ontLngURI.contains(urlPA) && ontLngURI.contains(urlPB)){
					//�Ƿ�����ͬ��Ԫ��
					if (propAName.equals(propBName)){
						simP=1.0;
						metaP=true;
					}
					else{
						//Ԫ���������ͬ��ֱ��������ǰtriple-pair
						continue;
					}
				}
				//������Ԫ������
				else if (!ontLngURI.contains(urlPA) && !ontLngURI.contains(urlPB)){
					if (typeA==typeB){
						//��ͬ�����ͣ���Ҫȷ�����ƶ�
						simP=getElmSim(propA,propB,typeA);
					}
					else{
						continue;
					}
				}
				if (simP<0){
					simP=0;
				}
				if (simP>0){simElmNum++;}
				
				//�ж�o-o
				typeA=getResourceType(objAName,m_source);
				typeB=getResourceType(objBName,m_target);
				simO=-1.0;
				metaO=false;
				//�ж��ǲ���Ԫ��
				if (ontLngURI.contains(urlOA) && ontLngURI.contains(urlOB)){
					//�Ƿ�����ͬ��Ԫ��
					if (objAName.equals(objBName)){
						simO=1.0;
						metaO=true;
					}
					else{
						//Ԫ���������ͬ��ֱ��������ǰtriple-pair
						continue;
					}
				}
				//������Ԫ������
				else if (!ontLngURI.contains(urlOA) && !ontLngURI.contains(urlOB)){
					if (typeA==typeB){
						//��ͬ�����ͣ���Ҫȷ�����ƶ�
						simO=getElmSim(objA,objB,typeA);
					}
					else{
						continue;
					}
				}
				if (simO<0){
					simO=0;
				}
				if (simO>0){simElmNum++;}
				
				/*�����������triple��������������triple-pair����*/
				if(simElmNum>=2){
					weight+=5.0;
					TriplePair tpair=new TriplePair();
					tpair.tripleA = stA;
					tpair.tripleB = stB;
					tpair.simS = simS;
					tpair.simP = simP;
					tpair.simO = simO;
					tpair.sIsMeta=metaS;
					tpair.pIsMeta=metaP;
					tpair.oIsMeta=metaO;
					tpair.weight=weight;
					tPairSet.add(tpair);
				}
			}
		}
		return tPairSet;
	}
	
	/******************
	 * Statement�е�meta��Ŀ 
	 *****************/
	private int metaElmInTriple(Statement stm)
	{
		int metaNum = 0;
		Resource sub=stm.getSubject();
		Property prop=stm.getPredicate();
		RDFNode obj=stm.getObject();
		String suri=null,puri=null,ouri=null;
		
		if (sub.isURIResource()){
			suri=sub.getNameSpace();
		}
		if (prop.isURIResource()){
			puri=prop.getNameSpace();
		}
		if (obj.isURIResource()){
			ouri=obj.asNode().getNameSpace();
		}
		
		if (ontParse.metaURISet.contains(suri)){
			metaNum++;
		}
		if (ontParse.metaURISet.contains(puri)){
			metaNum++;
		}
		if (ontParse.metaURISet.contains(ouri)){
			metaNum++;
		}
		return metaNum;
	}
	
	private int getResourceType(String s, OntModel m)
	{
		Resource r=m.getResource(s);   
		if (r==null){
			return -1;//����resource
		}
		else if (r.isLiteral()){//����
			return 4;
		}
		else if (ontParse.metaURISet.contains(r.getNameSpace())){//Ԫ��
//			System.out.println("Ԫ�"+r.toString());
			return 5;
		}
		else{
			OntResource ontr=m.getOntResource(r);
			if (!ontParse.isBlankNode(s)){
				/*������*/
				if (ontr.isClass()){
					return 1;//Class
				}
				else if (ontr.isIndividual()){
					return 3;//Individual
				}
				else if (ontr.isProperty()){
					return 2;//Property
				}
				else{
					return 4;//��ͨ��resource
				}
			}
			else{
				/*����*/
				ArrayList lt=getAnonResourceWithType(s,m);
				int type=0;
				type=((Integer)lt.get(1)).intValue();
				if (type==0){
					return 4;
				}
				else{
					return type;
				}				
			}			
		}
	}
	
	private ArrayList getAnonResourceWithType(String name, OntModel m)
	{
		ArrayList anonCnpt;
		ArrayList anonIns;
		ArrayList anonProp;
		
		if (m == m_source) {
			anonCnpt = s_AnonCnpt;
			anonProp = s_AnonProp;
			anonIns = s_AnonIns;
		}
		else{
			anonCnpt = t_AnonCnpt;
			anonProp = t_AnonProp;
			anonIns = t_AnonIns;
		}
		
		ArrayList result=new ArrayList();
		int type=0;
		
		OntClass c=null;
		for(Iterator i=anonCnpt.iterator();i.hasNext();){
			OntClass cx=(OntClass)i.next();
			if (name.equals(cx.toString())){
				c=cx;
				break;
			}
		}
		if (c!=null){
			result.add(0,c);
			type=1;
		}
		else
		{
			Individual d=null;
			for(Iterator i=anonIns.iterator();i.hasNext();){
				Individual dx=(Individual)i.next();
				if (name.equals(dx.toString())){
					d=dx;
					break;
				}
			}
			if(d!=null){
				result.add(0,d);
				type=3;
			}
			else
			{
				Property p=null;
				for(Iterator i=anonProp.iterator();i.hasNext();){
					Property px=(Property)i.next();
					if (name.equals(px.toString())){
						p=px;
						break;
					}
				}
				if (p!=null){
					result.add(0,p);
					type=2;
				}
			}
		}
		
		/*�������C,I,P�������ڵ�*/
		
		if(type==0){
			Resource r=m.getResource(name);
			result.add(0,r);
		}
		result.add(1,type);
		return result;
	}
	
	/******************
	 * �ж���Ԫ�����Ԫ�ص����ƶ� 
	 *****************/
	private double getElmSim(Resource elmA, Resource elmB, int type)
	{
		double sim=-1.0;
		String elmNameA=elmA.getLocalName();
		String elmNameB=elmB.getLocalName();
		String fullNameA=elmA.toString();
		String fullNameB=elmB.toString();
		//������
		if (!elmA.isAnon() && !elmB.isAnon()){
			String uriA=elmA.getNameSpace();
			String uriB=elmB.getNameSpace();
			
			if (type==1){
				if (uriA.equals(s_baseURI) && uriB.equals(t_baseURI)){
					//��Classȫ�����ƶȱ�
					sim=queryCnptSimRaw(elmNameA,elmNameB);
				}
				else{
					//��ֲ����ƶȱ�
					sim=queryLocalSimRaw(fullNameA,fullNameB);
				}				
			}
			else if (type==2){
				if (uriA.equals(s_baseURI) && uriB.equals(t_baseURI)){
					//��Propertyȫ�����ƶȱ�
					sim=queryPropSimRaw(elmNameA,elmNameB);
				}
				else{
					//��ֲ����ƶȱ�
					sim=queryLocalSimRaw(fullNameA,fullNameB);
				}
			}
			else if (type==3){
				if (uriA.equals(s_baseURI) && uriB.equals(t_baseURI)){
					//��Instanceȫ�����ƶȱ�
					sim=queryInsSimRaw(elmNameA,elmNameB);
				}
				else{
					//��ֲ����ƶȱ�
					sim=queryLocalSimRaw(fullNameA,fullNameB);
				}
			}
			else{
				//��ֲ����ƶȱ�
				sim=queryLocalSimRaw(fullNameA,fullNameB);
			}
		}
		//����
		else if (elmA.isAnon() && elmB.isAnon()){
			//��Anon�������ƶȱ�	
			sim=queryLocalSimRaw(elmA.toString(),elmB.toString());
		}
		return sim;
	}
	private double getElmSim(RDFNode elmA, RDFNode elmB, int type)
	{
		double sim=-1.0;
		String elmNameA=null;
		String elmNameB=null;
		String uriA=null;
		String uriB=null;
		String fullNameA=elmA.toString();
		String fullNameB=elmB.toString();
		
		//������
		if (!elmA.isAnon() && !elmB.isAnon()){
			if (elmA.isURIResource()){
				elmNameA=elmA.asNode().getLocalName();
				uriA=elmA.asNode().getNameSpace();
			}
			else{
				elmNameA=elmA.toString();
			}
			if (elmB.isURIResource()){
				elmNameB=elmB.asNode().getLocalName();
				uriB=elmB.asNode().getNameSpace();
			}
			else{
				elmNameB=elmB.toString();
			}
				
			if (type==1){
				if (uriA.equals(s_baseURI) && uriB.equals(t_baseURI)){
					//��Classȫ�����ƶȱ�
					sim=queryCnptSimRaw(elmNameA,elmNameB);
				}
				else{
					//��ֲ����ƶȱ�
					sim=queryLocalSimRaw(fullNameA,fullNameB);
				}		
				
			}
			else if (type==2){
				if (uriA.equals(s_baseURI) && uriB.equals(t_baseURI)){
					//��Propertyȫ�����ƶȱ�
					sim=queryPropSimRaw(elmNameA,elmNameB);
				}
				else{
					//��ֲ����ƶȱ�
					sim=queryLocalSimRaw(fullNameA,fullNameB);
				}
			}
			else if (type==3){
				if (uriA.equals(s_baseURI) && uriB.equals(t_baseURI)){
					//��Instanceȫ�����ƶȱ�
					sim=queryInsSimRaw(elmNameA,elmNameB);
				}
				else{
					//��ֲ����ƶȱ�
					sim=queryLocalSimRaw(fullNameA,fullNameB);
				}	
			}
			else{
				//��ֲ����ƶȱ�
				sim=queryLocalSimRaw(fullNameA,fullNameB);
			}
		}
		//����
		else if (elmA.isAnon() && elmB.isAnon()){
			//��Anon�������ƶȱ�	
			sim=queryLocalSimRaw(fullNameA,fullNameB);
		}
		return sim;
	}
	
	/**********************
	 * ��ѯȫ�ָ������ƾ���
	 ********************/
	private double queryCnptSimRaw(String nameA, String nameB){
		double sim=0;
		sim=((Double)cnptSimMap.get(nameA+nameB)).doubleValue();
		return sim;
	}
	
	/**********************
	 * ��ѯȫ���������ƾ���
	 ********************/
	private double queryPropSimRaw(String nameA, String nameB){
		double sim=0;
		sim=((Double)propSimMap.get(nameA+nameB)).doubleValue();
		return sim;
	}
	
	/**********************
	 * ��ѯȫ��ʵ�����ƾ���
	 ********************/
	private double queryInsSimRaw(String nameA, String nameB){
		double sim=0;
		sim=((Double)insSimMap.get(nameA+nameB)).doubleValue();
		return sim;
	}
	
	/**********************
	 * ��ѯ�ֲ����ƾ���
	 ********************/
	private double queryLocalSimRaw(String nameA, String nameB){
		double sim=0;
		
		for (Iterator it=otElmSim.iterator();it.hasNext();){
			GraphElmSim pair=(GraphElmSim)it.next();
			if (pair.elmNameA.equals(nameA) && pair.elmNameB.equals(nameB)){
				sim=pair.sim;
				break;
			}
		}
		return sim;
	}

	/******************
	 * ����������Ԫ��Եĺϲ�ͼ 
	 *****************/
	@SuppressWarnings("unchecked")
	private ArrayList consTriplePairGraph(Set pairSet) {
		ArrayList result=new ArrayList();
		
		/*triple pair graph�ĵ㼯�Ϻͱ߼���*/
		Set tgNodes=new HashSet();
		Set tgEdges=new HashSet();		
		HashMap edgeMap=new HashMap();//��Ԫ�鵽�ߵ�Hash		
		HashMap progWeightMap=new HashMap();//ͼ��Ԫ�ش���ϵ��
		
		/*ֱ�ӱ�����Ԫ��ԣ�����ͼ*/
		int ct=0;
		for (Iterator it=pairSet.iterator();it.hasNext();){
			TriplePair pair=(TriplePair)it.next();
			PairGraphRes nodeStar=new PairGraphRes();
			PairGraphRes nodeEnd=new PairGraphRes();
			PairGraphRes gEdge=new PairGraphRes();
			nodeStar.resA =pair.tripleA.getSubject();
			nodeStar.resB =pair.tripleB.getSubject();
			nodeStar.sim0 = pair.simS;
			nodeStar.isMeta=pair.sIsMeta;
			nodeEnd.resA =pair.tripleA.getObject();
			nodeEnd.resB =pair.tripleB.getObject();
			nodeEnd.sim0 = pair.simO;
			nodeEnd.isMeta= pair.oIsMeta;
			gEdge.resA = pair.tripleA.getPredicate();
			gEdge.resB = pair.tripleB.getPredicate();
			gEdge.sim0 = pair.simP;
			gEdge.isMeta = pair.pIsMeta;
			
			String starName=nodeStar.getString();
			String endName=nodeEnd.getString();
			String edgeName=gEdge.getString();
			
			System.out.println("����������Ԫ��Եĺϲ�ͼ "+ct++);
			
			/*�����������㣬ֱ����ԭ���ĵ������*/
			boolean fStar=false;
			boolean fEnd=false;
			boolean fEdge=false;
			//�������е㼯
			for (Iterator itx=tgNodes.iterator();itx.hasNext();){
				if (fStar&&fEnd&&fEdge){break;}
				PairGraphRes tp=(PairGraphRes)itx.next();
				String tName=tp.getString();
				if (!fStar&&starName.equals(tName)){
					nodeStar=tp;
					fStar=true;
				}
				if (!fEnd&&endName.equals(tName)){
					nodeEnd=tp;
					fEnd=true;
				}
				
				if (!fEdge&&edgeName.equals(tName)){
					gEdge=tp;
					fEdge=true;
				}
			}
			//�������б߼�
			for (Iterator itx=tgEdges.iterator();itx.hasNext();){
				if (fStar && fEnd && fEdge) {break;}
				PairGraphRes tp=(PairGraphRes)itx.next();
				String tName=tp.getString();
				if (!fStar&&starName.equals(tName)){
					nodeStar=tp;
					fStar=true;
				}

				if (!fEnd&&endName.equals(tName)){
					nodeEnd=tp;
					fEnd=true;
				}
				if (!fEdge&&edgeName.equals(tName)){
					gEdge=tp;
					fEdge=true;
				}
			}
			
			//����Star Node
			if (!tgNodes.contains(nodeStar)){
				/*���ͼ�в����������*/
				tgNodes.add(nodeStar);
			}
			
			//����End Node
			if (!tgNodes.contains(nodeEnd)){
				/*���ͼ�в����������*/
				tgNodes.add(nodeEnd);
			}
			
			//����Edge Node
			if (!tgEdges.contains(gEdge)){
				/*���ͼ�в����������*/
				tgEdges.add(gEdge);
			}
			
			/*���ߺ���Ԫ��Ķ�Ӧ������һ�����У������ѯ*/
			ArrayList t2PairRes=new ArrayList();
			t2PairRes.add(0,nodeStar);
			t2PairRes.add(1,nodeEnd);
			t2PairRes.add(2,gEdge);
			edgeMap.put(pair,t2PairRes);
			
			/*���㴫��ϵ��*/
			String key;
			int value;
			//s--p��Ȩ��
			key=starName+edgeName;
			//o����Ԫ��
			if (!nodeEnd.isMeta){
				if (progWeightMap.containsKey(key)){
					value=((Integer)progWeightMap.get(key)).intValue();
				}
				else {
					value = 0;
				}
				progWeightMap.put(key,value+1);
			}
			//p--o��Ȩ��
			key=edgeName+endName;
			//s����Ԫ��
			if (!nodeStar.isMeta){
				if (progWeightMap.containsKey(key)){
					value=((Integer)progWeightMap.get(key)).intValue();
				}
				else {
					value = 0;
				}
				progWeightMap.put(key,value+1);
			}
			//s--o��Ȩ��
			key=starName+endName;
			//p����Ԫ��
			if (!gEdge.isMeta){
				if (progWeightMap.containsKey(key)){
					value=((Integer)progWeightMap.get(key)).intValue();
				}
				else {
					value = 0;
				}
				progWeightMap.put(key,value+1);
			}
		}
		
		result.add(0,tgNodes);
		result.add(1,tgEdges);
		result.add(2,edgeMap);
		result.add(3,progWeightMap);
		return result;
	}
	
	/******************
	 * ���ƶȵĴ���
	 * ���룺triple-pairͼ
	 * ��������ƶȴ���ͼ 
	 *****************/
	private void propagation(HashMap edgeMap,HashMap progWeightMap) {
		double delta=0.5;
		int k=0;
		
		/*��ͼԪ�ص�ǰ�����ƶ�
		 *��ǰ���ƶ���sim0��ʾ
		 *����������ƶ���simk��ʾ*/
		while (k < maxProgTimes && (delta>0.001 && delta < 0.995)) {

			double max = 0;//������ƶ�
			Set flagSet=new HashSet();//�����ι�һ�ı�Ǽ���
			
			/*1.����ͼ�ıߣ����㴫�������ƶ�*/
			flagSet.clear();
			edgeMap.keySet();
			
			for (Iterator itx=edgeMap.entrySet().iterator();itx.hasNext();){
				java.util.Map.Entry entry=(java.util.Map.Entry)itx.next();
				ArrayList t2PairRes=(ArrayList)entry.getValue();
				PairGraphRes pairS=(PairGraphRes)t2PairRes.get(0);
				PairGraphRes pairO=(PairGraphRes)t2PairRes.get(1);
				PairGraphRes pairP=(PairGraphRes)t2PairRes.get(2);
				
				String sName=pairS.getString();
				String oName=pairO.getString();
				String pName=pairP.getString();
				
				String key;
				double weight;
				
				//����s�ϵ����ƶȴ���,���ƶȴ�p-o����
				if (!pairS.isMeta){
					if (!flagSet.contains(pairS)){
						pairS.simk=pairS.sim0;
						flagSet.add(pairS);
					}
					key=pName+oName;
					weight=((Integer)progWeightMap.get(key)).doubleValue();
					pairS.simk+=pairP.sim0*pairO.sim0/weight;
					max=Math.max(max,pairS.simk);
					if (pairS.simk > 0 && pairS.sim0 == 0) {
						pairS.cFlag = true;
					}
				}
				
				//����p�ϵ����ƶȴ���,���ƶȴ�s-o����
				if (!pairP.isMeta){
					if (!flagSet.contains(pairP)){
						pairP.simk=pairP.sim0;
						flagSet.add(pairP);
					}
					key=sName+oName;
					weight=((Integer)progWeightMap.get(key)).doubleValue();
					pairP.simk+=pairS.sim0*pairO.sim0/weight;
					max=Math.max(max,pairP.simk);
					if (pairP.simk > 0 && pairP.sim0 == 0) {
						pairP.cFlag = true;
					}
				}
				
				//����o�ϵ����ƶȴ���,���ƶȴ�s-p����
				if (!pairO.isMeta){
					if (!flagSet.contains(pairO)){
						pairO.simk=pairO.sim0;
						flagSet.add(pairO);
					}
					key=sName+pName;
					weight=((Integer)progWeightMap.get(key)).doubleValue();
					pairO.simk+=pairS.sim0*pairP.sim0/weight;
					max=Math.max(max,pairO.simk);
					if (pairO.simk > 0 && pairO.sim0 == 0) {
						pairO.cFlag = true;
					}
				}				
			}
	
			/* ���ƶȹ�һ */
			flagSet.clear();
			for (Iterator itx=edgeMap.entrySet().iterator();itx.hasNext();){
				java.util.Map.Entry entry=(java.util.Map.Entry)itx.next();
				ArrayList t2PairRes=(ArrayList)entry.getValue();
				PairGraphRes pairS=(PairGraphRes)t2PairRes.get(0);
				PairGraphRes pairO=(PairGraphRes)t2PairRes.get(1);
				PairGraphRes pairP=(PairGraphRes)t2PairRes.get(2);
				
				if (!pairS.isMeta) {
					if (!flagSet.contains(pairS)){
						pairS.simk = pairS.simk / max;
						flagSet.add(pairS);
					}					
				}
				if (!pairP.isMeta) {
					if (!flagSet.contains(pairP)){
						pairP.simk = pairP.simk / max;
						flagSet.add(pairP);
					}					
				}
				if (!pairO.isMeta) {
					if (!flagSet.contains(pairO)){
						pairO.simk = pairO.simk / max;
						flagSet.add(pairO);
					}					
				}				
			}
			
			/*�ж����ƶ�����*/
			delta=0;
			ArrayList vA=new ArrayList();
			ArrayList vB=new ArrayList();
			flagSet.clear();
			for (Iterator itx=edgeMap.entrySet().iterator();itx.hasNext();){
				java.util.Map.Entry entry=(java.util.Map.Entry)itx.next();
				ArrayList t2PairRes=(ArrayList)entry.getValue();
				PairGraphRes pairS=(PairGraphRes)t2PairRes.get(0);
				PairGraphRes pairO=(PairGraphRes)t2PairRes.get(1);
				PairGraphRes pairP=(PairGraphRes)t2PairRes.get(2);
				
				if (!pairS.isMeta) {
					if (!flagSet.contains(pairS)){
						vA.add(pairS.sim0);
						vB.add(pairS.simk);
						flagSet.add(pairS);
					}					
				}
				if (!pairP.isMeta) {
					if (!flagSet.contains(pairP)){
						vA.add(pairP.sim0);
						vB.add(pairP.simk);
						flagSet.add(pairP);						
					}					
				}
				if (!pairO.isMeta) {
					if (!flagSet.contains(pairO)){
						vA.add(pairO.sim0);
						vB.add(pairO.simk);
						flagSet.add(pairO);						
					}					
				}
			}
			
			/*�����������жϵ����Ƿ����*/
			delta=new TfIdfSim().getTextVectorSim(vA,vB);
			
			/*����k�����ƶ�*/
			flagSet.clear();
			System.out.println("------"+k+"--------");
			for (Iterator itx=edgeMap.entrySet().iterator();itx.hasNext();){
				java.util.Map.Entry entry=(java.util.Map.Entry)itx.next();
				ArrayList t2PairRes=(ArrayList)entry.getValue();
				PairGraphRes pairS=(PairGraphRes)t2PairRes.get(0);
				PairGraphRes pairO=(PairGraphRes)t2PairRes.get(1);
				PairGraphRes pairP=(PairGraphRes)t2PairRes.get(2);
				
				if (!pairS.isMeta) {
					if (!flagSet.contains(pairS)){
						pairS.sim0=pairS.simk;
//						System.out.println("<"+pairS.resA.toString()+"--"+pairS.resB.toString()+">---"+pairS.sim0);
						flagSet.add(pairS);						
					}					
				}
				if (!pairP.isMeta) {
					if (!flagSet.contains(pairP)){
						pairP.sim0=pairP.simk;
//						System.out.println("<"+pairP.resA.toString()+"--"+pairP.resB.toString()+">---"+pairP.sim0);
						flagSet.add(pairP);						
					}					
				}
				if (!pairO.isMeta) {
					if (!flagSet.contains(pairO)){
						pairO.sim0=pairO.simk;
//						System.out.println("<"+pairO.resA.toString()+"--"+pairO.resB.toString()+">---"+pairO.sim0);
						flagSet.add(pairO);						
					}					
				}
			}
			k++;
			System.out.println("��ǰ����������������ƶȣ�"+delta);
		}
		
	}
	
	/******************
	 * ���º�ѡ��Ԫ�����
	 * �Ķ�Ӧ���ƶ� 
	 *****************/
	private Set updateTriplePairSim(Set tPairSet, HashMap edgeMap) {
		Set newtpSet=new HashSet();
		/*������ѡ��Ԫ���*/
		int ct=0;
		for (Iterator it=tPairSet.iterator();it.hasNext();){
			TriplePair tpair=(TriplePair)it.next();
			Statement stA=tpair.tripleA;
			Statement stB=tpair.tripleB;
			Resource subA=stA.getSubject();
			Resource subB=stB.getSubject();
			Property propA=stA.getPredicate();
			Property propB=stB.getPredicate();
			RDFNode objA=stA.getObject();
			RDFNode objB=stB.getObject();
			
			String subAName=subA.toString();
			String subBName=subB.toString();
			String propAName=propA.toString();
			String propBName=propB.toString();
			String objAName=objA.toString();
			String objBName=objB.toString();
			
			boolean fS=false;
			boolean fP=false;
			boolean fO=false;
			
			System.out.println("���º�ѡ��Ԫ������ƶ�"+ct++);
			/*�������������ƶȵĵ��*/
//			for (Iterator itx=nodesSet.iterator();itx.hasNext();){
//				PairGraphRes node=(PairGraphRes)itx.next();
//				if (node.resA.toString().equals(subA.toString()) 
//					&& node.resB.toString().equals(subB.toString())){
//					tpair.simS=node.sim0;
//				}
//				if (node.resA.equals(objA.toString()) 
//						&& node.resB.toString().equals(objB.toString())){
//						tpair.simO=node.sim0;
//					}
//				if (node.resA.equals(propA.toString()) 
//						&& node.resB.toString().equals(propB.toString())){
//						tpair.simP=node.sim0;
//					}
//			}
			
			for (Iterator itx=edgeMap.entrySet().iterator();itx.hasNext();){
				if (fS && fO && fP){break;}//�������ѭ��
				java.util.Map.Entry entry=(java.util.Map.Entry)itx.next();
				ArrayList t2PairRes=(ArrayList)entry.getValue();
				PairGraphRes pairS=(PairGraphRes)t2PairRes.get(0);
				PairGraphRes pairO=(PairGraphRes)t2PairRes.get(1);
				PairGraphRes pairP=(PairGraphRes)t2PairRes.get(2);
				
				String sAName=pairS.resA.toString();
				String sBName=pairS.resB.toString();
				String oAName=pairO.resA.toString();
				String oBName=pairO.resB.toString();
				String pAName=pairP.resA.toString();
				String pBName=pairP.resB.toString();
				
				if (!fS && sAName.equals(subAName)	&& sBName.equals(subBName)) {
					tpair.simS = pairS.sim0;
					fS=true;
				}
				if (!fO && oAName.equals(objAName)	&& oBName.equals(objBName)) {
					tpair.simO = pairO.sim0;
					fO=true;
				}
				if (!fP && pAName.equals(propAName) && pBName.equals(propBName)) {
					tpair.simP = pairP.sim0;
					fP=true;
				}
			}
			
			/*�����Դ����triple pair��ͼ������,ֻ�������ŵ�*/
			if (tpair.simS>0.001 && tpair.simP>0.001 && tpair.simO>0.001){
				newtpSet.add(tpair);
			}			
		}		
		return newtpSet;//���ع��˲����ŵĽ��
	}
	
	/*******************
	 * ���º�ѡ������Ԫ��
	 *******************/
	@SuppressWarnings("unused")
	private Set updateTripleCandidate(ArrayList s_Stm,ArrayList t_Stm,Set tPairSet,HashMap edgeMap) {
		Set upSet=new HashSet();
		double weight=0;
		
		//������ͼ������
		sGUpdate=false;
		
		/*ԭ������Ԫ�����tPairSet*/
		/*��ȡ���ƶ�,Ϊ�ж��µ����ƶ���׼��*/
		Set rawPairs=new HashSet();
		Set rawTriples=new HashSet();
		HashMap rawPairSim=new HashMap();
		
		for (Iterator it=tPairSet.iterator();it.hasNext();){
			TriplePair tpair=(TriplePair)it.next();
			
			rawTriples.add(tpair.tripleA.toString()+tpair.tripleB.toString());
			
			String nameA,nameB;
			double sim=0;
			nameA=tpair.tripleA.getSubject().toString();
			nameB=tpair.tripleB.getSubject().toString();
			sim=tpair.simS;
			if (!rawPairs.contains(nameA+nameB)){
				rawPairs.add(nameA+nameB);
				rawPairSim.put(nameA+nameB,sim);				
			}
			nameA=tpair.tripleA.getPredicate().toString();
			nameB=tpair.tripleB.getPredicate().toString();
			sim=tpair.simP;
			if (!rawPairs.contains(nameA+nameB)){
				rawPairs.add(nameA+nameB);
				rawPairSim.put(nameA+nameB,sim);				
			}
			nameA=tpair.tripleA.getObject().toString();
			nameB=tpair.tripleB.getObject().toString();
			sim=tpair.simO;
			if (!rawPairs.contains(nameA+nameB)){
				rawPairs.add(nameA+nameB);
				rawPairSim.put(nameA+nameB,sim);				
			}
		}
		
		/*����ͼ�ı�*/
		for (Iterator it=edgeMap.entrySet().iterator();it.hasNext();){
			java.util.Map.Entry entry=(java.util.Map.Entry)it.next();
			ArrayList t2PairRes=(ArrayList)entry.getValue();
			PairGraphRes pairS=(PairGraphRes)t2PairRes.get(0);
			PairGraphRes pairO=(PairGraphRes)t2PairRes.get(1);
			PairGraphRes pairP=(PairGraphRes)t2PairRes.get(2);
			
			/*�ж���û�������ƶȶ�,����¼*/
			if (pairS.cFlag){
				upSet.add(pairS);
				pairS.cFlag=false;
			}
			if (pairO.cFlag){
				upSet.add(pairO);
				pairO.cFlag=false;
			}
			if (pairP.cFlag){
				upSet.add(pairP);
				pairP.cFlag=false;
			}
		}
				
		/*���º�ѡ��Ԫ��*/
		int ct=0;
		for (Iterator it=upSet.iterator();it.hasNext();){
			PairGraphRes upair=(PairGraphRes)it.next();
			String upA=upair.resA.toString();
			String upB=upair.resB.toString();
			
			System.out.println("���º�ѡ��Ԫ��"+ct++);
			
			/*����ԭʼͼ,�ҵ���Ӧ������Ԫ��*/
			for (Iterator itx=s_Stm.iterator();itx.hasNext();){
				Statement stA=(Statement)itx.next();
				if (metaElmInTriple(stA)>=2){//����2��Ԫ�����Ԫ��
					continue;
				}
				//������Ԫ��
				Resource subA=stA.getSubject();
				Property propA=stA.getPredicate();
				RDFNode objA=stA.getObject();
				
				/*ȷ������������pair*/
				if (!upA.equals(subA.toString()) && !upA.equals(propA.toString())
					&& !upA.equals(objA.toString())){
					continue;
				}
				
				String stAName=stA.toString();
				
				String subAName=subA.toString();
				String propAName=propA.toString();
				String objAName=objA.toString();
				
				String urlSA=null,urlPA=null,urlOA=null;
				if (subA.isURIResource()){urlSA=subA.getNameSpace();}
				if (propA.isURIResource()){urlPA=propA.getNameSpace();}
				if (objA.isURIResource()){urlOA=objA.asNode().getNameSpace();}
				
				for (Iterator ity=t_Stm.iterator();ity.hasNext();){
					Statement stB=(Statement)ity.next();
					if (metaElmInTriple(stB)>=2){//����2��Ԫ�����Ԫ��
						continue;
					}
					Resource subB=stB.getSubject();
					Property propB=stB.getPredicate();
					RDFNode objB=stB.getObject();					
			
					/*ȷ������������pair*/
					if (!upB.equals(subB.toString()) && !upB.equals(propB.toString())
						&& !upB.equals(objB.toString())){
						continue;
					}
					
					String stBName=stB.toString();
					
					String subBName=subB.toString();
					String propBName=propB.toString();
					String objBName=objB.toString();
					
					String urlSB=null,urlPB=null,urlOB=null;
					if (subB.isURIResource()){urlSB=subB.getNameSpace();}
					if (propB.isURIResource()){urlPB=propB.getNameSpace();}
					if (objB.isURIResource()){urlOB=objB.asNode().getNameSpace();}
					
					
					
					//���ҵ��Ϸ�����Ԫ���
					int typeA, typeB;
					double simS,simP,simO;
					boolean metaS,metaP,metaO;
					int simElmNum=0;
					//�ж�s-s
					typeA=getResourceType(subAName,m_source);
					typeB=getResourceType(subBName,m_target);
					simS=-1.0;
					metaS=false;
					//�ж��ǲ���Ԫ��
					if (ontLngURI.contains(urlSA) && ontLngURI.contains(urlSB)){
						//�Ƿ�����ͬ��Ԫ��
						if (subAName.equals(subBName)){
							simS=1.0;
							metaS=true;
						}
						else{
							//Ԫ���������ͬ��ֱ��������ǰtriple-pair
							continue;
						}
					}
					//������Ԫ������
					else if (!ontLngURI.contains(urlSA) && !ontLngURI.contains(urlSB)){
						if (typeA==typeB){
							//��ͬ�����ͣ���Ҫȷ�����ƶ�
							/*�жϵ�ǰtpair�Ƿ�����ö�*/
							if (rawPairs.contains(subAName+subBName)){
								/*�Ѿ�����,���ü�������ƶ�*/
								simS=((Double)rawPairSim.get(subAName+subBName)).doubleValue();
							}
							else{
								/*û�а���,����*/
								simS=getElmSim(subA,subB,typeA);
							}
						}
						
					}
					if (simS<0){
						simS=0;
					}
					if (simS>0){simElmNum++;}
					
					//�ж�p-p
					typeA=getResourceType(propAName,m_source);
					typeB=getResourceType(propBName,m_target);
					simP=-1.0;
					metaP=false;
					//�ж��ǲ���Ԫ��
					if (ontLngURI.contains(urlPA) && ontLngURI.contains(urlPB)){
						//�Ƿ�����ͬ��Ԫ��
						if (propAName.equals(propBName)){
							simP=1.0;
							metaP=true;
						}
						else{
							//Ԫ���������ͬ��ֱ��������ǰtriple-pair
							continue;
						}
					}
					//������Ԫ������
					else if (!ontLngURI.contains(urlPA) && !ontLngURI.contains(urlPB)){
						if (typeA==typeB){
							//��ͬ�����ͣ���Ҫȷ�����ƶ�
							/*�жϵ�ǰtpair�Ƿ�����ö�*/
							if (rawPairs.contains(propAName+propBName)){
								/*�Ѿ�����,���ü�������ƶ�*/
								simP=((Double)rawPairSim.get(propAName+propBName)).doubleValue();
							}
							else{
								/*û�а���,����*/
								simP=getElmSim(propA,propB,typeA);
							}							
						}
					}
					if (simP<0){
						simP=0;
					}
					if (simP>0){simElmNum++;}
					
					//�ж�o-o
					typeA=getResourceType(objAName,m_source);
					typeB=getResourceType(objBName,m_target);
					simO=-1.0;
					metaO=false;
					//�ж��ǲ���Ԫ��
					if (ontLngURI.contains(urlOA) && ontLngURI.contains(urlOB)){
						//�Ƿ�����ͬ��Ԫ��
						if (objAName.equals(objBName)){
							simO=1.0;
							metaO=true;
						}
						else{
							//Ԫ���������ͬ��ֱ��������ǰtriple-pair
							continue;
						}
					}
					//������Ԫ������
					else if (!ontLngURI.contains(urlOA) && !ontLngURI.contains(urlOB)){
						if (typeA==typeB){
							//��ͬ�����ͣ���Ҫȷ�����ƶ�
							/*�жϵ�ǰtpair�Ƿ�����ö�*/
							if (rawPairs.contains(objAName+objBName)){
								/*�Ѿ�����,���ü�������ƶ�*/
								simO=((Double)rawPairSim.get(objAName+objBName)).doubleValue();
							}
							else{
								/*û�а���,����*/
								simO=getElmSim(objA,objB,typeA);
							}
						}
					}
					if (simO<0){
						simO=0;
					}	
					if (simO>0){simElmNum++;}
					
					/*������µ�����triple������triple-pair����*/
					if(simElmNum>=2 && !rawTriples.contains(stAName+stBName)){
						weight+=5.0;
						TriplePair tpair=new TriplePair();
						tpair.tripleA = stA;
						tpair.tripleB = stB;
						tpair.simS = simS;
						tpair.simP = simP;
						tpair.simO = simO;
						tpair.sIsMeta=metaS;
						tpair.pIsMeta=metaP;
						tpair.oIsMeta=metaO;
						tpair.weight=weight;
						tPairSet.add(tpair);
						
						rawTriples.add(stAName+stBName);
						
						//��ͼ��������
						sGUpdate=true;
					}
				}
			}
		}
		return tPairSet;
	}
	
	/*******************
	 * �ж�ȫ�ֵ����Ƿ����
	 * false:��ʾ��������
	 * true:��ʾ����Ҫ��������
	 *******************/
	private boolean isGlobalConvergence() {
		boolean flag=false;
		double delta=0;
		
		//��һ�����ȵĹ���
//		cnptSimK1 = new StableMarriageFilter().run(cnptSimK1,s_cnptNum,t_cnptNum);
//		propSimK1 = new StableMarriageFilter().run(propSimK1,s_propNum,t_propNum);
		
		ArrayList vA=new ArrayList();
		ArrayList vB=new ArrayList();
		
		for (int i=0;i<s_cnptNum;i++){
			for (int j=0;j<t_cnptNum;j++){
//				delta+=Math.abs(cnptSimK0[i][j]-cnptSimK1[i][j]);
				vA.add(cnptSimK0[i][j]);
				vB.add(cnptSimK1[i][j]);
				cnptSimK0[i][j]=cnptSimK1[i][j];
			}
		}
		
		for (int i=0;i<s_propNum;i++){
			for (int j=0;j<t_propNum;j++){
//				delta+=Math.abs(propSimK0[i][j]-propSimK1[i][j]);
				vA.add(propSimK0[i][j]);
				vB.add(propSimK1[i][j]);
				propSimK0[i][j]=propSimK1[i][j];
			}
		}
		
		/*�����������жϵ����Ƿ����*/
		delta=new TfIdfSim().getTextVectorSim(vA,vB);
		
		System.out.println("ȫ�����ƾ���ĵ����������ƶ�:"+delta);
		if (delta<0.999){
			flag=true;
		}
		
		if (flag){
			/*�����Ҫ����ȫ�ּ��㣬���²��û������ƶ�HashMap*/
			for (int i=0;i<s_cnptNum;i++){
				for (int j=0;j<t_cnptNum;j++){
					cnptSimMap.put(s_cnptName[i]+t_cnptName[j],cnptSimK0[i][j]);
				}
			}
			propSimMap=new HashMap();
			for (int i=0;i<s_propNum;i++){
				for (int j=0;j<t_propNum;j++){
					propSimMap.put(s_propName[i]+t_propName[j],propSimK0[i][j]);
				}
			}
		}
		return flag;
	}
	
	/******************
	 * ͨ���ж��Ƿ�õ��µ����ƶԣ�������
	 * ���������Ƿ��ܽ���
	 *****************/
	private boolean isSameCTPSet(Set newCTPSet, Set oldCTPSet) {
		if (newCTPSet.size()!=oldCTPSet.size()){//��������
			return true;
		}
		
		boolean result=false;//����û��������
		for (Iterator itx=newCTPSet.iterator();itx.hasNext();){
			TriplePair newtpair=(TriplePair)itx.next();
			Statement newstA=newtpair.tripleA;
			Statement newstB=newtpair.tripleB;
			
			String newName=newstA.toString()+newstB.toString();
			
			boolean cFlag=false;
			
			for (Iterator ity=oldCTPSet.iterator();ity.hasNext();){
				TriplePair oldtpair=(TriplePair)ity.next();
				Statement oldstA=oldtpair.tripleA;
				Statement oldstB=oldtpair.tripleB;
				
				String oldName=oldstA.toString()+oldstB.toString();
				
				if (newName.equals(oldName)){
					cFlag=true;
					break;
				}
			}
			if (!cFlag){
				result=true;
				break;
			}
		}
		
		
		return result;
	}	
}

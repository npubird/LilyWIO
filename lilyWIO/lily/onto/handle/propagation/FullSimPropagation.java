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
 *  全局本体图的相似度传播
 ********************/
public class FullSimPropagation {
	/*数据成员*/
	public OntModel m_source;
	public OntModel m_target;
	/*****源本体******/
	public int s_cnptNum;//概念数目
	public int s_propNum;//属性数目
	public int s_insNum;//实例数目
	public String[] s_cnptName;//概念名
	public String[] s_propName;//属性名
	public String[] s_insName;//实例名
	public ArrayList s_stmList;//图
	public String s_baseURI;
	
	/*****目标本体******/
	public int t_cnptNum;//概念数目
	public int t_propNum;//属性数目
	public int t_insNum;//实例数目
	public String[] t_cnptName;//概念名
	public String[] t_propName;//属性名
	public String[] t_insName;//属性名
	public ArrayList t_stmList;//图
	public String t_baseURI;
	
	/*****匿名资源*****/
	ArrayList s_AnonCnpt;
	ArrayList s_AnonProp;
	ArrayList s_AnonIns;	
	ArrayList t_AnonCnpt;
	ArrayList t_AnonProp;
	ArrayList t_AnonIns;	
	
	/******相似图信息******/
	public Set[][] cnptTriplePair;//子图相似三元组对集合
	public Set[][] propTriplePair;//子图相似三元组对集合
	
	/******相似度矩阵******/
	public double[][] cnptSimRaw;//原始概念相似度
	public double[][] cnptSimK0;//k次迭代相似度
	public double[][] cnptSimK1;//k+1次迭代相似度
	public double[][] propSimRaw;//原始属性相似度
	public double[][] propSimK0;//k次迭代相似度
	public double[][] propSimK1;//k+1次迭代相似度
	public double[][] insSimRaw;//原始实例相似度
	/******其它相似度矩阵****/
	//其它元素间的相似度矩阵
	public ArrayList otElmSim;
	/*原始相似度的Hash*/
	HashMap cnptSimMap;
	HashMap propSimMap;
	HashMap insSimMap;
	
	/*****概念候选三元组集合*****/
	public Set[][] cnptCandiTPSet;
	/*****属性候选三元组集合*****/
	public Set[][] propCandiTPSet;
		
	//本体元信息
	public Set ontLngURI;	
	public OWLOntParse ontParse;
	
	//子图更新标志
	private boolean hasGUpdate;
	private boolean updateCnpt[][];
	private boolean updateProp[][];

	//当前处理子图类型标志
	private boolean flagCnptSugG;
	private String curCnptA,curCnptB;
	private int curCnptIDA,curCnptIDB;
	private boolean flagPropSugG;
	private String curPropA,curPropB;	
	private int curPropIDA,curPropIDB;
	
	//传播最大迭代次数
	int maxProgTimes=8;
	//子图更新标志
	boolean sGUpdate;
	
	/*******************
	 * 类的主入口
	 *******************/
	public ArrayList ontSimPg(ArrayList paraList) {
		ArrayList result=new ArrayList();
		
		/*解析参数*/
		unPackPara(paraList);
		
		int times=0;
		
		long start = System.currentTimeMillis();// 开始计时

		// 概念子图间相似度传播
		simPropagation();
//		hasGUpdate = isGlobalConvergence();

		long end = System.currentTimeMillis();// 结束计时
		long costtime = end - start;//统计算法时间
		System.out.println("相似度传播算法时间："+(double)costtime/1000.+"秒");
		
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
	 * 接收本体参数
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
		
		//根据得到的number初始化各种数组
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
		
		//克隆原始相似度
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
		
		//Hash概念相似矩阵
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
	 * 初始化
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
	 * 相似度的传播 
	 *****************/
	private void simPropagation() {
		Set gNodes=new HashSet();
		Set gEdges=new HashSet();	
		HashMap edgeMap=new HashMap();
		HashMap progWeightMap=new HashMap();
		ArrayList lt=new ArrayList();
		
		//候选相似三元组对
		Set candiTPSet=new HashSet();
		candiTPSet=getSimTripleCandidate(s_stmList,t_stmList );
		
		lt=consTriplePairGraph(candiTPSet);
		gNodes=(HashSet)lt.get(0);
		gEdges=(HashSet)lt.get(1);
		edgeMap=(HashMap)lt.get(2);
		progWeightMap=(HashMap)lt.get(3);
		
		Set oldCTPSet=new HashSet();//判断更新的标志集合
		
		sGUpdate=true;
		int cn=0;
		while (sGUpdate && cn<6){
			//相似度传播，直到子图不再更新为止
			propagation(edgeMap,progWeightMap);
			
			//更新候选三元组对相似度
			candiTPSet=updateTriplePairSim(candiTPSet,edgeMap);
			
			//和老的ctp比较
//			sGUpdate=isSameCTPSet(candiTPSet,oldCTPSet);
			
			//记录当前的ctp
//			oldCTPSet=((Set)((HashSet)candiTPSet).clone());
			
			cn++;
			//更新子图
			if (cn<6){
				updateTripleCandidate(s_stmList,t_stmList,candiTPSet,edgeMap);
				lt=consTriplePairGraph(candiTPSet);
				gNodes=(HashSet)lt.get(0);
				gEdges=(HashSet)lt.get(1);
				edgeMap=(HashMap)lt.get(2);
				progWeightMap=(HashMap)lt.get(3);
			}
		}
		
		/*记录相似度传播结果*/
		for (int i = 0; i < s_cnptNum; i++) {
			for (int j = 0; j < t_cnptNum; j++) {
				// 通过图的点找到所有的概念相似对
				for (Iterator it = gNodes.iterator(); it.hasNext();) {
					PairGraphRes pair = (PairGraphRes) it.next();
					/* 由于只能出现在点上，因此不取edgepair */

					// 判断相似对
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
				//通过图的点和边找到所有的属性相似对
				double tempPSim=0;
				for (Iterator itx=edgeMap.entrySet().iterator();itx.hasNext();){
					java.util.Map.Entry entry=(java.util.Map.Entry)itx.next();
					ArrayList t2PairRes=(ArrayList)entry.getValue();
					PairGraphRes pairS=(PairGraphRes)t2PairRes.get(0);
					PairGraphRes pairO=(PairGraphRes)t2PairRes.get(1);
					PairGraphRes pairP=(PairGraphRes)t2PairRes.get(2);
					
					//判断相似对
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
	 * 选择候选相似三元组
	 *******************/
	private Set getSimTripleCandidate(ArrayList s_Stm,ArrayList t_Stm) {
		Set tPairSet=new HashSet();
		
		/*遍历两个三元组集合*/
		double weight=0;//区别边的weight
		int ct=0;
		for (Iterator itx=s_Stm.iterator();itx.hasNext();){
			Statement stA=(Statement)itx.next();
			if (metaElmInTriple(stA)>=2){
				continue;
			}
			
			System.out.println("选择候选相似三元组"+ct++);
			
			//分离三元组
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
				//判断相似三元组对
				/*(1)不出现两个元语,已经判断*/				
				/*(2)至少一对非元语相似*/
				//(a)对应位置成分要对应
				//(b)对应位置要存在相似度
				int typeA, typeB;
				double simS,simP,simO;
				boolean metaS,metaP,metaO;
				int simElmNum=0;
				//判断s-s
				typeA=getResourceType(subAName,m_source);
				typeB=getResourceType(subBName,m_target);
				simS=-1.0;
				metaS=false;
				//判断是不是元语
				if (ontLngURI.contains(urlSA) && ontLngURI.contains(urlSB)){
					//是否是相同的元语
					if (subAName.equals(subBName)){
						simS=1.0;
						metaS=true;
					}
					else{
						//元语如果不相同，直接跳过当前triple-pair
						continue;
					}
				}
				//都不是元语的情况
				else if (!ontLngURI.contains(urlSA) && !ontLngURI.contains(urlSB)){
					if (typeA==typeB){
						//相同的类型，需要确定相似度
						simS=getElmSim(subA,subB,typeA);						
					}
					else{
						continue;
					}
				}
				if (simS < 0) {	simS = 0; }
				if (simS > 0) {	simElmNum++; }

				//判断p-p
				typeA=getResourceType(propAName,m_source);
				typeB=getResourceType(propBName,m_target);
				simP=-1.0;
				metaP=false;
				//判断是不是元语
				if (ontLngURI.contains(urlPA) && ontLngURI.contains(urlPB)){
					//是否是相同的元语
					if (propAName.equals(propBName)){
						simP=1.0;
						metaP=true;
					}
					else{
						//元语如果不相同，直接跳过当前triple-pair
						continue;
					}
				}
				//都不是元语的情况
				else if (!ontLngURI.contains(urlPA) && !ontLngURI.contains(urlPB)){
					if (typeA==typeB){
						//相同的类型，需要确定相似度
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
				
				//判断o-o
				typeA=getResourceType(objAName,m_source);
				typeB=getResourceType(objBName,m_target);
				simO=-1.0;
				metaO=false;
				//判断是不是元语
				if (ontLngURI.contains(urlOA) && ontLngURI.contains(urlOB)){
					//是否是相同的元语
					if (objAName.equals(objBName)){
						simO=1.0;
						metaO=true;
					}
					else{
						//元语如果不相同，直接跳过当前triple-pair
						continue;
					}
				}
				//都不是元语的情况
				else if (!ontLngURI.contains(urlOA) && !ontLngURI.contains(urlOB)){
					if (typeA==typeB){
						//相同的类型，需要确定相似度
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
				
				/*如果满足相似triple条件，加入相似triple-pair集合*/
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
	 * Statement中的meta数目 
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
			return -1;//不是resource
		}
		else if (r.isLiteral()){//文字
			return 4;
		}
		else if (ontParse.metaURISet.contains(r.getNameSpace())){//元语
//			System.out.println("元语："+r.toString());
			return 5;
		}
		else{
			OntResource ontr=m.getOntResource(r);
			if (!ontParse.isBlankNode(s)){
				/*非匿名*/
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
					return 4;//普通的resource
				}
			}
			else{
				/*匿名*/
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
		
		/*如果不是C,I,P的匿名节点*/
		
		if(type==0){
			Resource r=m.getResource(name);
			result.add(0,r);
		}
		result.add(1,type);
		return result;
	}
	
	/******************
	 * 判断三元组的两元素的相似度 
	 *****************/
	private double getElmSim(Resource elmA, Resource elmB, int type)
	{
		double sim=-1.0;
		String elmNameA=elmA.getLocalName();
		String elmNameB=elmB.getLocalName();
		String fullNameA=elmA.toString();
		String fullNameB=elmB.toString();
		//非匿名
		if (!elmA.isAnon() && !elmB.isAnon()){
			String uriA=elmA.getNameSpace();
			String uriB=elmB.getNameSpace();
			
			if (type==1){
				if (uriA.equals(s_baseURI) && uriB.equals(t_baseURI)){
					//查Class全局相似度表
					sim=queryCnptSimRaw(elmNameA,elmNameB);
				}
				else{
					//查局部相似度表
					sim=queryLocalSimRaw(fullNameA,fullNameB);
				}				
			}
			else if (type==2){
				if (uriA.equals(s_baseURI) && uriB.equals(t_baseURI)){
					//查Property全局相似度表
					sim=queryPropSimRaw(elmNameA,elmNameB);
				}
				else{
					//查局部相似度表
					sim=queryLocalSimRaw(fullNameA,fullNameB);
				}
			}
			else if (type==3){
				if (uriA.equals(s_baseURI) && uriB.equals(t_baseURI)){
					//查Instance全局相似度表
					sim=queryInsSimRaw(elmNameA,elmNameB);
				}
				else{
					//查局部相似度表
					sim=queryLocalSimRaw(fullNameA,fullNameB);
				}
			}
			else{
				//查局部相似度表
				sim=queryLocalSimRaw(fullNameA,fullNameB);
			}
		}
		//匿名
		else if (elmA.isAnon() && elmB.isAnon()){
			//查Anon本地相似度表	
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
		
		//非匿名
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
					//查Class全局相似度表
					sim=queryCnptSimRaw(elmNameA,elmNameB);
				}
				else{
					//查局部相似度表
					sim=queryLocalSimRaw(fullNameA,fullNameB);
				}		
				
			}
			else if (type==2){
				if (uriA.equals(s_baseURI) && uriB.equals(t_baseURI)){
					//查Property全局相似度表
					sim=queryPropSimRaw(elmNameA,elmNameB);
				}
				else{
					//查局部相似度表
					sim=queryLocalSimRaw(fullNameA,fullNameB);
				}
			}
			else if (type==3){
				if (uriA.equals(s_baseURI) && uriB.equals(t_baseURI)){
					//查Instance全局相似度表
					sim=queryInsSimRaw(elmNameA,elmNameB);
				}
				else{
					//查局部相似度表
					sim=queryLocalSimRaw(fullNameA,fullNameB);
				}	
			}
			else{
				//查局部相似度表
				sim=queryLocalSimRaw(fullNameA,fullNameB);
			}
		}
		//匿名
		else if (elmA.isAnon() && elmB.isAnon()){
			//查Anon本地相似度表	
			sim=queryLocalSimRaw(fullNameA,fullNameB);
		}
		return sim;
	}
	
	/**********************
	 * 查询全局概念相似矩阵
	 ********************/
	private double queryCnptSimRaw(String nameA, String nameB){
		double sim=0;
		sim=((Double)cnptSimMap.get(nameA+nameB)).doubleValue();
		return sim;
	}
	
	/**********************
	 * 查询全局属性相似矩阵
	 ********************/
	private double queryPropSimRaw(String nameA, String nameB){
		double sim=0;
		sim=((Double)propSimMap.get(nameA+nameB)).doubleValue();
		return sim;
	}
	
	/**********************
	 * 查询全局实例相似矩阵
	 ********************/
	private double queryInsSimRaw(String nameA, String nameB){
		double sim=0;
		sim=((Double)insSimMap.get(nameA+nameB)).doubleValue();
		return sim;
	}
	
	/**********************
	 * 查询局部相似矩阵
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
	 * 构造相似三元组对的合并图 
	 *****************/
	@SuppressWarnings("unchecked")
	private ArrayList consTriplePairGraph(Set pairSet) {
		ArrayList result=new ArrayList();
		
		/*triple pair graph的点集合和边集合*/
		Set tgNodes=new HashSet();
		Set tgEdges=new HashSet();		
		HashMap edgeMap=new HashMap();//三元组到边的Hash		
		HashMap progWeightMap=new HashMap();//图中元素传播系数
		
		/*直接遍历三元组对，构造图*/
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
			
			System.out.println("构造相似三元组对的合并图 "+ct++);
			
			/*如果包含这个点，直接用原来的点来替代*/
			boolean fStar=false;
			boolean fEnd=false;
			boolean fEdge=false;
			//遍历已有点集
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
			//遍历已有边集
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
			
			//加入Star Node
			if (!tgNodes.contains(nodeStar)){
				/*如果图中不包含这个点*/
				tgNodes.add(nodeStar);
			}
			
			//加入End Node
			if (!tgNodes.contains(nodeEnd)){
				/*如果图中不包含这个点*/
				tgNodes.add(nodeEnd);
			}
			
			//加入Edge Node
			if (!tgEdges.contains(gEdge)){
				/*如果图中不包含这个点*/
				tgEdges.add(gEdge);
			}
			
			/*将边和三元组的对应保存在一个表中，方便查询*/
			ArrayList t2PairRes=new ArrayList();
			t2PairRes.add(0,nodeStar);
			t2PairRes.add(1,nodeEnd);
			t2PairRes.add(2,gEdge);
			edgeMap.put(pair,t2PairRes);
			
			/*计算传播系数*/
			String key;
			int value;
			//s--p的权重
			key=starName+edgeName;
			//o不是元语
			if (!nodeEnd.isMeta){
				if (progWeightMap.containsKey(key)){
					value=((Integer)progWeightMap.get(key)).intValue();
				}
				else {
					value = 0;
				}
				progWeightMap.put(key,value+1);
			}
			//p--o的权重
			key=edgeName+endName;
			//s不是元语
			if (!nodeStar.isMeta){
				if (progWeightMap.containsKey(key)){
					value=((Integer)progWeightMap.get(key)).intValue();
				}
				else {
					value = 0;
				}
				progWeightMap.put(key,value+1);
			}
			//s--o的权重
			key=starName+endName;
			//p不是元语
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
	 * 相似度的传播
	 * 输入：triple-pair图
	 * 输出：相似度传播图 
	 *****************/
	private void propagation(HashMap edgeMap,HashMap progWeightMap) {
		double delta=0.5;
		int k=0;
		
		/*子图元素当前的相似度
		 *当前相似度用sim0表示
		 *传播后的相似度用simk表示*/
		while (k < maxProgTimes && (delta>0.001 && delta < 0.995)) {

			double max = 0;//最大相似度
			Set flagSet=new HashSet();//避免多次归一的标记集合
			
			/*1.遍历图的边，计算传播的相似度*/
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
				
				//计算s上的相似度传播,相似度从p-o传入
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
				
				//计算p上的相似度传播,相似度从s-o传入
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
				
				//计算o上的相似度传播,相似度从s-p传入
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
	
			/* 相似度归一 */
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
			
			/*判断相似度收敛*/
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
			
			/*用向量方法判断迭代是否结束*/
			delta=new TfIdfSim().getTextVectorSim(vA,vB);
			
			/*更新k次相似度*/
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
			System.out.println("当前迭代结果的向量相似度："+delta);
		}
		
	}
	
	/******************
	 * 更新候选三元组对中
	 * 的对应相似度 
	 *****************/
	private Set updateTriplePairSim(Set tPairSet, HashMap edgeMap) {
		Set newtpSet=new HashSet();
		/*遍历候选三元组对*/
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
			
			System.out.println("更新候选三元组对相似度"+ct++);
			/*遍历保存新相似度的点对*/
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
				if (fS && fO && fP){break;}//避免多余循环
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
			
			/*将明显错误的triple pair从图中消除,只保留可信的*/
			if (tpair.simS>0.001 && tpair.simP>0.001 && tpair.simO>0.001){
				newtpSet.add(tpair);
			}			
		}		
		return newtpSet;//返回过滤不可信的结果
	}
	
	/*******************
	 * 更新候选相似三元组
	 *******************/
	@SuppressWarnings("unused")
	private Set updateTripleCandidate(ArrayList s_Stm,ArrayList t_Stm,Set tPairSet,HashMap edgeMap) {
		Set upSet=new HashSet();
		double weight=0;
		
		//假设子图不更新
		sGUpdate=false;
		
		/*原来的三元组放在tPairSet*/
		/*提取相似对,为判断新的相似度做准备*/
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
		
		/*遍历图的边*/
		for (Iterator it=edgeMap.entrySet().iterator();it.hasNext();){
			java.util.Map.Entry entry=(java.util.Map.Entry)it.next();
			ArrayList t2PairRes=(ArrayList)entry.getValue();
			PairGraphRes pairS=(PairGraphRes)t2PairRes.get(0);
			PairGraphRes pairO=(PairGraphRes)t2PairRes.get(1);
			PairGraphRes pairP=(PairGraphRes)t2PairRes.get(2);
			
			/*判断有没有新相似度对,并记录*/
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
				
		/*更新候选三元组*/
		int ct=0;
		for (Iterator it=upSet.iterator();it.hasNext();){
			PairGraphRes upair=(PairGraphRes)it.next();
			String upA=upair.resA.toString();
			String upB=upair.resB.toString();
			
			System.out.println("更新候选三元组"+ct++);
			
			/*遍历原始图,找到对应的新三元组*/
			for (Iterator itx=s_Stm.iterator();itx.hasNext();){
				Statement stA=(Statement)itx.next();
				if (metaElmInTriple(stA)>=2){//跳过2个元语的三元组
					continue;
				}
				//分离三元组
				Resource subA=stA.getSubject();
				Property propA=stA.getPredicate();
				RDFNode objA=stA.getObject();
				
				/*确保包含待更新pair*/
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
					if (metaElmInTriple(stB)>=2){//跳过2个元语的三元组
						continue;
					}
					Resource subB=stB.getSubject();
					Property propB=stB.getPredicate();
					RDFNode objB=stB.getObject();					
			
					/*确保包含待更新pair*/
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
					
					
					
					//先找到合法的三元组对
					int typeA, typeB;
					double simS,simP,simO;
					boolean metaS,metaP,metaO;
					int simElmNum=0;
					//判断s-s
					typeA=getResourceType(subAName,m_source);
					typeB=getResourceType(subBName,m_target);
					simS=-1.0;
					metaS=false;
					//判断是不是元语
					if (ontLngURI.contains(urlSA) && ontLngURI.contains(urlSB)){
						//是否是相同的元语
						if (subAName.equals(subBName)){
							simS=1.0;
							metaS=true;
						}
						else{
							//元语如果不相同，直接跳过当前triple-pair
							continue;
						}
					}
					//都不是元语的情况
					else if (!ontLngURI.contains(urlSA) && !ontLngURI.contains(urlSB)){
						if (typeA==typeB){
							//相同的类型，需要确定相似度
							/*判断当前tpair是否包含该对*/
							if (rawPairs.contains(subAName+subBName)){
								/*已经包含,采用计算的相似度*/
								simS=((Double)rawPairSim.get(subAName+subBName)).doubleValue();
							}
							else{
								/*没有包含,采用*/
								simS=getElmSim(subA,subB,typeA);
							}
						}
						
					}
					if (simS<0){
						simS=0;
					}
					if (simS>0){simElmNum++;}
					
					//判断p-p
					typeA=getResourceType(propAName,m_source);
					typeB=getResourceType(propBName,m_target);
					simP=-1.0;
					metaP=false;
					//判断是不是元语
					if (ontLngURI.contains(urlPA) && ontLngURI.contains(urlPB)){
						//是否是相同的元语
						if (propAName.equals(propBName)){
							simP=1.0;
							metaP=true;
						}
						else{
							//元语如果不相同，直接跳过当前triple-pair
							continue;
						}
					}
					//都不是元语的情况
					else if (!ontLngURI.contains(urlPA) && !ontLngURI.contains(urlPB)){
						if (typeA==typeB){
							//相同的类型，需要确定相似度
							/*判断当前tpair是否包含该对*/
							if (rawPairs.contains(propAName+propBName)){
								/*已经包含,采用计算的相似度*/
								simP=((Double)rawPairSim.get(propAName+propBName)).doubleValue();
							}
							else{
								/*没有包含,采用*/
								simP=getElmSim(propA,propB,typeA);
							}							
						}
					}
					if (simP<0){
						simP=0;
					}
					if (simP>0){simElmNum++;}
					
					//判断o-o
					typeA=getResourceType(objAName,m_source);
					typeB=getResourceType(objBName,m_target);
					simO=-1.0;
					metaO=false;
					//判断是不是元语
					if (ontLngURI.contains(urlOA) && ontLngURI.contains(urlOB)){
						//是否是相同的元语
						if (objAName.equals(objBName)){
							simO=1.0;
							metaO=true;
						}
						else{
							//元语如果不相同，直接跳过当前triple-pair
							continue;
						}
					}
					//都不是元语的情况
					else if (!ontLngURI.contains(urlOA) && !ontLngURI.contains(urlOB)){
						if (typeA==typeB){
							//相同的类型，需要确定相似度
							/*判断当前tpair是否包含该对*/
							if (rawPairs.contains(objAName+objBName)){
								/*已经包含,采用计算的相似度*/
								simO=((Double)rawPairSim.get(objAName+objBName)).doubleValue();
							}
							else{
								/*没有包含,采用*/
								simO=getElmSim(objA,objB,typeA);
							}
						}
					}
					if (simO<0){
						simO=0;
					}	
					if (simO>0){simElmNum++;}
					
					/*如果是新的相似triple，加入triple-pair集合*/
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
						
						//子图发生更新
						sGUpdate=true;
					}
				}
			}
		}
		return tPairSet;
	}
	
	/*******************
	 * 判断全局迭代是否结束
	 * false:表示迭代结束
	 * true:表示还需要继续迭代
	 *******************/
	private boolean isGlobalConvergence() {
		boolean flag=false;
		double delta=0;
		
		//做一个事先的过滤
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
		
		/*用向量方法判断迭代是否结束*/
		delta=new TfIdfSim().getTextVectorSim(vA,vB);
		
		System.out.println("全局相似矩阵的迭代收敛相似度:"+delta);
		if (delta<0.999){
			flag=true;
		}
		
		if (flag){
			/*如果还要继续全局计算，重新布置基本相似度HashMap*/
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
	 * 通过判断是否得到新的相似对，来估计
	 * 传播过程是否能结束
	 *****************/
	private boolean isSameCTPSet(Set newCTPSet, Set oldCTPSet) {
		if (newCTPSet.size()!=oldCTPSet.size()){//发生更新
			return true;
		}
		
		boolean result=false;//假设没发生更新
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

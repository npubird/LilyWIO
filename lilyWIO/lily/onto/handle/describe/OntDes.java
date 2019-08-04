/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-5-14
 * Filename          OntDes.java
 * Version           2.0
 * 
 * Last modified on  2007-5-14
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 * ����Ԫ�ص��ı������ͽṹ����
 ***********************************************/
package lily.onto.handle.describe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import lily.onto.parse.OWLOntParse;
import lily.tool.datastructure.ConceptSubGraph;
import lily.tool.datastructure.ConceptWt;
import lily.tool.datastructure.GraphTriple;
import lily.tool.datastructure.InstanceWt;
import lily.tool.datastructure.OntLngMetaWt;
import lily.tool.datastructure.PropertySubGraph;
import lily.tool.datastructure.PropertyWt;
import lily.tool.datastructure.TextDes;
import lily.tool.datastructure.Word;
import lily.tool.textprocess.DelStopWords;
import lily.tool.textprocess.SplitWords;

import com.hp.hpl.jena.ontology.ConversionException;
import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.rdf.model.*;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-5-14
 * 
 * describe:
 * ����Ԫ�ص��ı������ͽṹ����
 ********************/
@SuppressWarnings("unchecked")
public class OntDes {
	//��������ģ��
	public OntModel m;
	//������Ŀ
	public int conceptNum;
	//������Ŀ
	public int propNum;
	//ʵ����Ŀ
	public int insNum;
	
	//������
	public String[] conceptName;
	//������
	public String[] propName;
	//ʵ����
	public String[] insName;
	//base URI
	public String baseURI;
	
	//��������baseURI�µı���Ԫ��
	public int fullConceptNum;
	public int fullPropNum;
	public int fullInsNum;
	public OntClass[] fullConceptName;
	public OntProperty[] fullPropName;
	public Individual[] fullInsName;
	
	//������Դ
	ArrayList anonCnpt;
	ArrayList anonProp;
	ArrayList anonIns;	
	
	//�����ı�����
	public TextDes[] cnptBasicTextDes;
	public TextDes[] propBasicTextDes;
	public TextDes[] insBasicTextDes;
		
	//������ı�����
	public TextDes[] cnptTextDes;
	//���Ե��ı�����
	public TextDes[] propTextDes;
	//ʵ�����ı�����
	public TextDes[] insTextDes; 
	
	//��ͼ�зǻ�����Ϣ���ı�����
	public ArrayList[] cnptOtTextDes;
	public ArrayList[] propOtTextDes;
	public ArrayList fullOtTextDes;
	
	//��ͼ
	public ConceptSubGraph[] cnptSubG;
	public PropertySubGraph[] propSubG;
	
	public ArrayList curStmList;
	
	public ArrayList fullGraphStms;
	
	public boolean isSubProg;
	
	//����Ԫ��Ϣ
	Set ontLngURI;
	
	OWLOntParse ontParse;
	
	//����
	public double wLocalName=1.0;
	public double wLabel=1.0;
	public double wComment=0.8;
	public double wSeeAlso=0.6;
	public double wIsDefineBy=0.6;
	public double wEQClass=1.0;
	public double wAnonNode=0.8;
	public double subClassDec=0.08;//˥��ϵ��
	public double superClassDec=0.1;//˥��ϵ��
	public double subPropertyDec=0.1;//˥��ϵ��
	public double superPropertyDec=0.15;//˥��ϵ��
	public double wsiblingClassDec=0.8;
	public double wsiblingPropertyDec=0.8;
	public double wDisjClassDec=0.6;
	public double wCmplClassDec=0.9;
	
	
	/************
	 * ���������������
	 ***********/
	public ArrayList getOntDes(ArrayList list)
	{
		ArrayList desResult=new ArrayList();
		
		//��������
		unPackPara(list);
		//�ı�����
		getOntTextDes();
		//�ṹ����
		getOntStructDes();
		
		desResult.add(0,cnptTextDes);
		desResult.add(1,propTextDes);
		desResult.add(2,insTextDes);
		if (isSubProg){
			desResult.add(3,cnptOtTextDes);
			desResult.add(4,propOtTextDes);
		}
		else{
			desResult.add(3,fullOtTextDes);
		}
		
		return desResult;
	}
	
	/************
	 * ���㱾����ı�����
	 ***********/
	public void getOntTextDes()
	{
		/*�����������*/
		basicTextDes();
		/*���������ı�����*/
		cnptTextDes();
		/*�������Ե��ı�����*/
		propTextDes();
		/*����ʵ�����ı�����*/
		insTextDes();
		/*ȫͼ����Ԫ������*/
		if (!isSubProg){
			fullOtTextDes=fullOtherTexDes();
		}
	}
	
	/************
	 * ����������ı�����
	 ***********/
	public void basicTextDes()
	{
		/*�����������*/
		cnptBasicTextDes();
		/*���Ի�������*/
		propBasicTextDes();
		/*ʵ����������*/
		insBasicTextDes();
		/*���Ƹ����������*/
		perfectCnptBTDes();
		/*�������Ի�������*/
		perfectPropBTDes();
		/*����ʵ����������*/
		perfectInsBTDes();
		
	}
	
	/************
	 *���Ƹ���Ļ����ı�����
	 *����isDefinedby��seeAlso��equivalentClass,sameAs
	 ***********/
	public void perfectCnptBTDes()
	{
		/*��������������Ļ�������*/
		for(int i=0;i<fullConceptNum;i++){
			OntClass c=fullConceptName[i];
			if (c.isAnon()){
 				ArrayList anonList=anonBasicDesInModel(c,0);
				for (Iterator itx=anonList.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					cnptBasicTextDes[i].text.add(w);
				}
			}
		}
		
		TextDes[] richCnptBasicTextDes=new TextDes[fullConceptNum];
		/*��ʼ������*/
		for(int i=0;i<fullConceptNum;i++){
			richCnptBasicTextDes[i]=new TextDes();
			richCnptBasicTextDes[i].name =cnptBasicTextDes[i].name;
			richCnptBasicTextDes[i].text = new ArrayList();
			if (cnptBasicTextDes[i].text!=null){
				richCnptBasicTextDes[i].text=(ArrayList)(cnptBasicTextDes[i].text).clone();
			}
		}
		
		for(int i=0;i<fullConceptNum;i++){
			OntClass c=fullConceptName[i];
			
//			if (c.isAnon()){//��������������
//				continue;
//			}
			
			/*����seeAlso*/
			Property p=m.getProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");
			Selector sl = new SimpleSelector(c,p,(RDFNode)null);
	    	for(StmtIterator it=m.listStatements(sl);it.hasNext();)
	    	{
	    		Statement st=(Statement)it.next();
	    		RDFNode o=st.getObject();
	    		if(!o.isAnon()){//������
	    			OntClass cx=m.getOntClass(o.toString());
	    			if(cx!=null){//�ǿո���
	    				/*����ø����Des*/
	    				int pos=findCnptPosInFullName(cx.toString());
	    				for (Iterator it1=cnptBasicTextDes[pos].text.iterator();it1.hasNext();){
	    					Word w=(Word)it1.next();
	    					w.weight*=wSeeAlso;//ע��������ܸı�ԭ����word
	    					richCnptBasicTextDes[i].text.add(w);
	    				}
	    			}
	    		}
	    	}
	    	
	    	/*����isDefinedBy*/
			p=m.getProperty("http://www.w3.org/2000/01/rdf-schema#isDefinedBy");
			sl = new SimpleSelector(c,p,(RDFNode)null);
	    	for(StmtIterator it=m.listStatements(sl);it.hasNext();)
	    	{
	    		Statement st=(Statement)it.next();
	    		RDFNode o=st.getObject();
	    		if(!o.isAnon()){//������
	    			OntClass cx=m.getOntClass(o.toString());
	    			if(cx!=null){//�ǿո���
	    				/*����ø����Des*/
	    				int pos=findCnptPosInFullName(cx.toString());
	    				for (Iterator it1=cnptBasicTextDes[pos].text.iterator();it1.hasNext();){
	    					Word w=(Word)it1.next();
	    					w.weight*=wSeeAlso;//ע��������ܸı�ԭ����word
	    					richCnptBasicTextDes[i].text.add(w);
	    				}
	    			}
	    		}
	    	}
		}
		
		for(int i=0;i<fullConceptNum;i++){
			OntClass c=fullConceptName[i];
			
//			if (c.isAnon()){//��������������
//				continue;
//			}
			
			/*����equivalentClass*/
			Property p=m.getProperty("http://www.w3.org/2002/07/owl#equivalentClass");
			Selector sl = new SimpleSelector(c,p,(RDFNode)null);
	    	for(StmtIterator it=m.listStatements(sl);it.hasNext();)
	    	{
	    		Statement st=(Statement)it.next();
	    		RDFNode o=st.getObject();
	    		OntClass cx=null;
	    		if (!o.isAnon()){
	    			cx=m.getOntClass(o.toString());
	    		}
	    		else{
	    			cx=this.findCnptInFullName(o.toString());
	    		}

	    		if(!cx.isAnon()){//������
	    			/*����ø����Des*/
    				int pos=findCnptPosInFullName(cx.toString());
    				if (pos>=0){
        				for (Iterator it1=cnptBasicTextDes[pos].text.iterator();it1.hasNext();){
        					Word w=(Word)it1.next();
        					richCnptBasicTextDes[i].text.add(w);
        				}
    				}
	    		}
	    		else{//��������
	    			/*�õ������ڵ��Des*/
	    			ArrayList anonList=anonBasicDesInModel(cx,0);
    				for (Iterator it1=anonList.iterator();it1.hasNext();){
    					Word w=(Word)it1.next();
    					richCnptBasicTextDes[i].text.add(w);
    				}
	    		}
	    	}
		}
		
		for(int i=0;i<fullConceptNum;i++){
			OntClass c=fullConceptName[i];
			
//			if (c.isAnon()){//��������������
//				continue;
//			}
			
			/*����sameAs*/
			Property p=m.getProperty("http://www.w3.org/2002/07/owl#sameAs");
			Selector sl = new SimpleSelector(c,p,(RDFNode)null);
	    	for(StmtIterator it=m.listStatements(sl);it.hasNext();)
	    	{
	    		Statement st=(Statement)it.next();
	    		RDFNode o=st.getObject();
	    		OntClass cx=null;
	    		if (!o.isAnon()){
	    			cx=m.getOntClass(o.toString());
	    		}
	    		else{
	    			cx=this.findCnptInFullName(o.toString());
	    		}
	    		
	    		if(!cx.isAnon()){//������
	    			/*����ø����Des*/
    				int pos=findCnptPosInFullName(cx.toString());
    				if (pos>=0){
        				for (Iterator it1=cnptBasicTextDes[pos].text.iterator();it1.hasNext();){
        					Word w=(Word)it1.next();
        					richCnptBasicTextDes[i].text.add(w);
        				}
    				}
	    		}
	    		else{//��������
	    			/*�õ������ڵ��Des*/
	    			ArrayList anonList=anonBasicDesInModel(cx,0);
    				for (Iterator it1=anonList.iterator();it1.hasNext();){
    					Word w=(Word)it1.next();
    					richCnptBasicTextDes[i].text.add(w);
    				}
	    		}
	    	}
		}
		
		/*���ô���*/
		for(int i=0;i<fullConceptNum;i++){
			richCnptBasicTextDes[i].text=desPostProcess(richCnptBasicTextDes[i].text);
		}
		/*��ԭ��������*/
		cnptBasicTextDes=richCnptBasicTextDes;
	}
	
	/************
	 *�������ԵĻ����ı�����
	 *����isDefinedby��seeAlso��equivalentProperty,sameAs
	 ***********/
	public void perfectPropBTDes()
	{
		TextDes[] richPropBasicTextDes=new TextDes[fullPropNum];
		/*��ʼ������*/
		for(int i=0;i<fullPropNum;i++){
			richPropBasicTextDes[i]=new TextDes();
			richPropBasicTextDes[i].name =propBasicTextDes[i].name;
			richPropBasicTextDes[i].text = new ArrayList();
			if (propBasicTextDes[i].text!=null){
				richPropBasicTextDes[i].text=(ArrayList)(propBasicTextDes[i].text).clone();
			}
		}
		
		for(int i=0;i<fullPropNum;i++){
			OntProperty p=fullPropName[i];
			
			if (p.isAnon()){//��������������
				continue;
			}
			
			/*����seeAlso*/
			Property pm=m.getProperty("http://www.w3.org/2000/01/rdf-schema#seeAlso");
			Selector sl = new SimpleSelector(p,pm,(RDFNode)null);
	    	for(StmtIterator it=m.listStatements(sl);it.hasNext();)
	    	{
	    		Statement st=(Statement)it.next();
	    		RDFNode o=st.getObject();
	    		if(!o.isAnon()){//������
	    			OntProperty px=m.getOntProperty(o.toString());
	    			if(px!=null){//�ǿ�����
	    				/*��������Ե�Des*/
	    				int pos=findPropPosInFullName(px.toString());
	    				for (Iterator it1=propBasicTextDes[pos].text.iterator();it1.hasNext();){
	    					Word w=(Word)it1.next();
	    					w.weight*=wSeeAlso;//ע��������ܸı�ԭ����word
	    					richPropBasicTextDes[i].text.add(w);
	    				}
	    			}
	    		}
	    	}
	    	
	    	/*����isDefinedBy*/
			pm=m.getProperty("http://www.w3.org/2000/01/rdf-schema#isDefinedBy");
			sl = new SimpleSelector(p,pm,(RDFNode)null);
	    	for(StmtIterator it=m.listStatements(sl);it.hasNext();)
	    	{
	    		Statement st=(Statement)it.next();
	    		RDFNode o=st.getObject();
	    		if(!o.isAnon()){//������
	    			OntProperty px=m.getOntProperty(o.toString());
	    			if(px!=null){//�ǿ�����
	    				/*��������Ե�Des*/
	    				int pos=findPropPosInFullName(px.toString());
	    				for (Iterator it1=propBasicTextDes[pos].text.iterator();it1.hasNext();){
	    					Word w=(Word)it1.next();
	    					w.weight*=wSeeAlso;//ע��������ܸı�ԭ����word
	    					richPropBasicTextDes[i].text.add(w);
	    				}
	    			}
	    		}
	    	}
		}
		
		for(int i=0;i<fullPropNum;i++){
			OntProperty p=fullPropName[i];
			
			if (p.isAnon()){//��������������
				continue;
			}
			
			/*����equivalentProperty*/
			Property pm=m.getProperty("http://www.w3.org/2002/07/owl#equivalentProperty");
			Selector sl = new SimpleSelector(p,pm,(RDFNode)null);
	    	for(StmtIterator it=m.listStatements(sl);it.hasNext();)
	    	{
	    		Statement st=(Statement)it.next();
	    		RDFNode o=st.getObject();
	    		OntProperty px=null;
	    		if (!o.isAnon()){
	    			px=m.getOntProperty(o.toString());
	    		}
	    		else{
	    			px=this.findPropInFullName(o.toString());
	    		}
	    		
	    		if(!px.isAnon()){//������
	    			/*��������Ե�Des*/
    				int pos=findPropPosInFullName(px.toString());
    				if (pos>=0){
        				for (Iterator it1=propBasicTextDes[pos].text.iterator();it1.hasNext();){
        					Word w=(Word)it1.next();
        					richPropBasicTextDes[i].text.add(w);
        				}
    				}
	    		}
	    		else{//��������
	    			/*�õ������ڵ��Des*/
	    			ArrayList anonList=anonBasicDesInModel(px,0);
    				for (Iterator it1=anonList.iterator();it1.hasNext();){
    					Word w=(Word)it1.next();
    					richPropBasicTextDes[i].text.add(w);
    				}
	    		}
	    	}
		}
		
		for(int i=0;i<fullPropNum;i++){
			OntProperty p=fullPropName[i];
			
			if (p.isAnon()){//��������������
				continue;
			}
			
			/*����sameAs*/
			Property pm=m.getProperty("http://www.w3.org/2002/07/owl#sameAs");
			Selector sl = new SimpleSelector(p,pm,(RDFNode)null);
	    	for(StmtIterator it=m.listStatements(sl);it.hasNext();)
	    	{
	    		Statement st=(Statement)it.next();
	    		RDFNode o=st.getObject();
	    		OntProperty px=null;
	    		if (!o.isAnon()){
	    			px=m.getOntProperty(o.toString());
	    		}
	    		else{
	    			px=this.findPropInFullName(o.toString());
	    		}
	    		
	    		if(!px.isAnon()){//������
	    			/*��������Ե�Des*/
    				int pos=findPropPosInFullName(px.toString());
    				if (pos>=0){
        				for (Iterator it1=propBasicTextDes[pos].text.iterator();it1.hasNext();){
        					Word w=(Word)it1.next();
        					richPropBasicTextDes[i].text.add(w);
        				}
    				}
	    		}
	    		else{//��������
	    			/*�õ������ڵ��Des*/
	    			ArrayList anonList=anonBasicDesInModel(px,0);
    				for (Iterator it1=anonList.iterator();it1.hasNext();){
    					Word w=(Word)it1.next();
    					richPropBasicTextDes[i].text.add(w);
    				}
	    		}
	    	}
		}
		
		/*���ô���*/
		for(int i=0;i<fullPropNum;i++){
			richPropBasicTextDes[i].text=desPostProcess(richPropBasicTextDes[i].text);
		}
		/*��ԭ��������*/
		propBasicTextDes=richPropBasicTextDes;
	}
	
	/************
	 *����ʵ���Ļ����ı�����
	 *����sameAs
	 ***********/
	public void perfectInsBTDes()
	{
		TextDes[] richInsBasicTextDes=new TextDes[fullInsNum];
		/*��ʼ������*/
		for(int i=0;i<fullInsNum;i++){
			richInsBasicTextDes[i]=new TextDes();
			richInsBasicTextDes[i].name =insBasicTextDes[i].name;
			richInsBasicTextDes[i].text = new ArrayList();
			if (insBasicTextDes[i].text!=null){
				richInsBasicTextDes[i].text=(ArrayList)(insBasicTextDes[i].text).clone();
			}
		}
		
		for(int i=0;i<fullInsNum;i++){
			Individual id=fullInsName[i];
			
			if (id.isAnon()){//����������ʵ��
				continue;
			}
			
			/*����sameAs*/
			Property pm=m.getProperty("http://www.w3.org/2002/07/owl#sameAs");
			Selector sl = new SimpleSelector(id,pm,(RDFNode)null);
	    	for(StmtIterator it=m.listStatements(sl);it.hasNext();)
	    	{
	    		Statement st=(Statement)it.next();
	    		RDFNode o=st.getObject();
	    		Individual idx=null;
	    		if (!o.isAnon()){
	    			idx=m.getIndividual(o.toString());
	    		}
	    		else{
	    			idx=this.findInsInFullName(o.toString());
	    		}
	    		if(!idx.isAnon()){//������
	    			/*�����ʵ����Des*/
    				int pos=findInsPosInFullName(idx.toString());
    				if (pos>=0){
        				for (Iterator it1=insBasicTextDes[pos].text.iterator();it1.hasNext();){
        					Word w=(Word)it1.next();
        					richInsBasicTextDes[i].text.add(w);
        				}
    				}
	    		}
	    		else{//����ʵ��
	    			/*�õ������ڵ��Des*/
	    			ArrayList anonList=anonBasicDesInModel(idx,0);
    				for (Iterator it1=anonList.iterator();it1.hasNext();){
    					Word w=(Word)it1.next();
    					richInsBasicTextDes[i].text.add(w);
    				}
	    		}
	    	}
		}
		
		/*���ô���*/
		for(int i=0;i<fullInsNum;i++){
			richInsBasicTextDes[i].text=desPostProcess(richInsBasicTextDes[i].text);
		}
		/*��ԭ��������*/
		insBasicTextDes=richInsBasicTextDes;
	}
	
	/************
	 *����Ļ����ı�����
	 ***********/
	public void cnptBasicTextDes()
	{
        SplitWords spWord = new SplitWords();
        DelStopWords delSWrod= new DelStopWords();
        delSWrod.loadStopWords();
        
		/*�����������*/
		for(int i=0;i<fullConceptNum;i++){
			cnptBasicTextDes[i]=new TextDes();
			OntClass c=fullConceptName[i];
			cnptBasicTextDes[i].text=new ArrayList();
			if (!c.isAnon()){//������������
				cnptBasicTextDes[i].name=fullConceptName[i].getLocalName();
				String s=null;
				ArrayList list=new ArrayList();
				/*local name*/
				s=c.getLocalName();
				/*�ı�Ԥ����*/
		        s=delSWrod.removeStopWords(s);
		        list=spWord.split(s);
		        list=delSWrod.removeStopWords(list);
		        /*���Ȩ��*/
		        for (Iterator it=list.iterator();it.hasNext();){
		        	String stemp=(String)it.next();
		        	Word w=new Word();
		        	w.content=stemp;
		        	w.weight =wLocalName;
		        	cnptBasicTextDes[i].text.add(w);
		        }
		        
				/*label*/
		        s=null;
		        list=null;
				s=c.getLabel(null);
				if (s!=null){
					/*�ı�Ԥ����*/
			        s=delSWrod.removeStopWords(s);
			        list=spWord.split(s);
			        list=delSWrod.removeStopWords(list);
			        /*���Ȩ��*/
			        for (Iterator it=list.iterator();it.hasNext();){
			        	String stemp=(String)it.next();
			        	Word w=new Word();
			        	w.content=stemp;
			        	w.weight =wLabel;
			        	cnptBasicTextDes[i].text.add(w);
			        }
				}
		        
				/*comment*/
		        s=null;
		        list=null;
				s=c.getComment(null);
				if (s!=null){
					/*�ı�Ԥ����*/
			        s=delSWrod.removeStopWords(s);
			        list=spWord.split(s);
			        list=delSWrod.removeStopWords(list);
			        /*���Ȩ��*/
			        for (Iterator it=list.iterator();it.hasNext();){
			        	String stemp=(String)it.next();
			        	Word w=new Word();
			        	w.content=stemp;
			        	w.weight =wComment;
			        	cnptBasicTextDes[i].text.add(w);
			        }
				}
			}
			else{
				/*������������*/
				cnptBasicTextDes[i].name=fullConceptName[i].toString();
				//��ȫ������������ɺ����
			}
		}
	}
	
	/************
	 *���ԵĻ����ı�����
	 ***********/
	public void propBasicTextDes()
	{
        SplitWords spWord = new SplitWords();
        DelStopWords delSWrod= new DelStopWords();
        delSWrod.loadStopWords();
	
		/*���Ի�������*/
		for(int i=0;i<fullPropNum;i++){
			propBasicTextDes[i]=new TextDes();
			OntProperty p=fullPropName[i];
			if (!p.isAnon()){//������������
				propBasicTextDes[i].name=fullPropName[i].getLocalName();
				propBasicTextDes[i].text=new ArrayList();
				String s=null;
				ArrayList list=new ArrayList();
				/*local name*/
				s=p.getLocalName();
				/*�ı�Ԥ����*/
		        s=delSWrod.removeStopWords(s);
		        list=spWord.split(s);
		        list=delSWrod.removeStopWords(list);
		        /*���Ȩ��*/
		        for (Iterator it=list.iterator();it.hasNext();){
		        	String stemp=(String)it.next();
		        	Word w=new Word();
		        	w.content=stemp;
		        	w.weight =wLocalName;
		        	propBasicTextDes[i].text.add(w);
		        }
				
				/*label*/
				s=null;
				list=null;
				s=p.getLabel(null);
				if (s!=null){
					/*�ı�Ԥ����*/
			        s=delSWrod.removeStopWords(s);
			        list=spWord.split(s);
			        list=delSWrod.removeStopWords(list);
			        /*���Ȩ��*/
			        for (Iterator it=list.iterator();it.hasNext();){
			        	String stemp=(String)it.next();
			        	Word w=new Word();
			        	w.content=stemp;
			        	w.weight =wLabel;
			        	propBasicTextDes[i].text.add(w);
			        }
				}
				
				/*comment*/
				s=null;
				list=null;
				s=p.getComment(null);
				if (s!=null){
					/*�ı�Ԥ����*/
			        s=delSWrod.removeStopWords(s);
			        list=spWord.split(s);
			        list=delSWrod.removeStopWords(list);
			        /*���Ȩ��*/
			        for (Iterator it=list.iterator();it.hasNext();){
			        	String stemp=(String)it.next();
			        	Word w=new Word();
			        	w.content=stemp;
			        	w.weight =wComment;
			        	propBasicTextDes[i].text.add(w);
			        }
				}
			}
		}
	}
	
	/************
	 *ʵ���Ļ����ı�����
	 ***********/
	public void insBasicTextDes()
	{
        SplitWords spWord = new SplitWords();
        DelStopWords delSWrod= new DelStopWords();
        delSWrod.loadStopWords();
        
		/*ʵ����������*/
		for(int i=0;i<fullInsNum;i++){
			insBasicTextDes[i]=new TextDes();
			Individual d=fullInsName[i];
			if (!d.isAnon()){//��������ʵ��
				insBasicTextDes[i].name=fullInsName[i].getLocalName();
				insBasicTextDes[i].text=new ArrayList();
				String s=null;
				ArrayList list=new ArrayList();
				/*local name*/
				s=d.getLocalName();
				/*�ı�Ԥ����*/
		        s=delSWrod.removeStopWords(s);
		        list=spWord.split(s);
		        list=delSWrod.removeStopWords(list);
		        /*���Ȩ��*/
		        for (Iterator it=list.iterator();it.hasNext();){
		        	String stemp=(String)it.next();
		        	Word w=new Word();
		        	w.content=stemp;
		        	w.weight =wLocalName;
		        	insBasicTextDes[i].text.add(w);
		        }
				
				/*label*/
				s=null;
				list=null;
				/*local name*/
				s=d.getLabel(null);
				if (s!=null){
					/*�ı�Ԥ����*/
			        s=delSWrod.removeStopWords(s);
			        list=spWord.split(s);
			        list=delSWrod.removeStopWords(list);
			        /*���Ȩ��*/
			        for (Iterator it=list.iterator();it.hasNext();){
			        	String stemp=(String)it.next();
			        	Word w=new Word();
			        	w.content=stemp;
			        	w.weight =wLabel;
			        	insBasicTextDes[i].text.add(w);
			        }
				}
				
				/*comment*/
				s=null;
				list=null;
				/*local name*/
				s=d.getComment(null);
				if (s!=null){
					/*�ı�Ԥ����*/
			        s=delSWrod.removeStopWords(s);
			        list=spWord.split(s);
			        list=delSWrod.removeStopWords(list);
			        /*���Ȩ��*/
			        for (Iterator it=list.iterator();it.hasNext();){
			        	String stemp=(String)it.next();
			        	Word w=new Word();
			        	w.content=stemp;
			        	w.weight =wComment;
			        	insBasicTextDes[i].text.add(w);
			        }
				}
			}
		}
	}
	
	/************
	 * ���������ı�����
	 ***********/
	public void cnptTextDes()
	{
		for(int i=0;i<conceptNum;i++){
			OntClass c=m.getOntClass(baseURI+conceptName[i]);
			
			/*c��Ӧ����ͼ����Ԫ��*/
			int pos=getSubGraph(c);
			DirectedGraph gc=cnptSubG[pos].subGraph;
//			curStmList.clear();
			curStmList=cnptSubG[pos].stmList;
			
			cnptTextDes[i]=new TextDes();
			cnptTextDes[i].name=conceptName[i];
			cnptTextDes[i].text=new ArrayList();
			
			/*����self����,��basic�������Ѿ����*/
			int basicDespos=findCnptPosInFullName(c.toString());
			ArrayList lt=new ArrayList();
			lt=cnptBasicTextDes[basicDespos].text;
			cnptTextDes[i].text.add(0,lt);
			
			/*�����ı�����*/
			lt=cnptHrcTextDes(c,gc);
			cnptTextDes[i].text.add(1,lt);
			
			/*������������*/
			lt=cnptPropTextDes(c,gc);
			cnptTextDes[i].text.add(2,lt);
			
			/*ʵ����������*/
			lt=cnptInsTexDes(c,gc);
			cnptTextDes[i].text.add(3,lt);
			
			/*������ͼ������Ԫ�ص�����*/
			if (isSubProg){
				cnptOtTextDes[i]=cnptOtherTexDes(c,gc);
			}
			else{
				cnptOtTextDes[i]=null;
			}
		}
	}
	
	/************
	 * �������Ե��ı�����
	 ***********/
	public void propTextDes()
	{
		for(int i=0;i<propNum;i++){
			OntProperty p=m.getOntProperty(baseURI+propName[i]);
			
			/*p��Ӧ����ͼ����Ԫ��*/
			int pos=getSubGraph(p);
			DirectedGraph gp=propSubG[pos].subGraph;
//			curStmList.clear();
			curStmList=propSubG[pos].stmList;
			
			propTextDes[i]=new TextDes();
			propTextDes[i].name=propName[i];
			propTextDes[i].text=new ArrayList();
			
			/*����self����,��basic�������Ѿ����*/
			int basicDespos=findPropPosInFullName(p.toString());
			ArrayList lt=new ArrayList();
			lt=propBasicTextDes[basicDespos].text;
			propTextDes[i].text.add(0,lt);
			
			/*����ı�����*/
			lt=propHrcTextDes(p,gp);
			propTextDes[i].text.add(1,lt);
			
			/*������������*/
			lt=propFunctionTextDes(p,gp);
			propTextDes[i].text.add(2,lt);
			
			/*ʵ����������*/
			lt=propInsTexDes(p,gp);
			propTextDes[i].text.add(3,lt);
			
			/*������ͼ������Ԫ�ص�����*/
			if (isSubProg){
				propOtTextDes[i]=propOtherTexDes(p,gp);
			}
			else{
				propOtTextDes[i]=null;
			}				
		}
	}	
	
	/************
	 * ����ʵ�����ı�����
	 * ֱ�Ӵӻ���������ȡ��
	 ***********/
	public void insTextDes()
	{
		for(int i=0;i<insNum;i++){
			Individual idv=m.getIndividual(baseURI+insName[i]);
			
			insTextDes[i]=new TextDes();
			insTextDes[i].name=insName[i];
			insTextDes[i].text=new ArrayList();
			
			if (idv==null){
				continue;
			}
			
			/*����self����,��basic�������Ѿ����*/
			int basicDespos=findInsPosInFullName(idv.toString());
			insTextDes[i].text=insBasicTextDes[basicDespos].text;
		}
	}	
	
	/************
	 * ���㱾��Ľṹ����
	 ***********/
	public void getOntStructDes()
	{
		/*�������Ľṹ����*/
		
		/*�������ԵĽṹ����*/
	}
	
	/**********************
	 * ���ձ������
	 ********************/
	public void unPackPara(ArrayList paraList)
	{
		m=(OntModel)paraList.get(0);
		conceptNum=((Integer)paraList.get(1)).intValue();
		propNum=((Integer)paraList.get(2)).intValue();
		insNum=((Integer)paraList.get(3)).intValue();
		
		fullConceptNum=((Integer)paraList.get(7)).intValue();
		fullPropNum=((Integer)paraList.get(8)).intValue();
		fullInsNum=((Integer)paraList.get(9)).intValue();
		
		//���ݵõ���number��ʼ����������
		initPara();
		conceptName=(String[])(paraList.get(4));
		propName=(String[])(paraList.get(5));
		insName=(String[])(paraList.get(18));
		fullConceptName=(OntClass[])(paraList.get(10));
		fullPropName=(OntProperty[])(paraList.get(11));
		fullInsName=(Individual[])(paraList.get(12));
		cnptSubG=(ConceptSubGraph[])(paraList.get(13));
		propSubG=(PropertySubGraph[])(paraList.get(14));
		baseURI=(String)paraList.get(6);
		
		//������Դ
		anonCnpt=(ArrayList)paraList.get(15);
		anonProp=(ArrayList)paraList.get(16);
		anonIns=(ArrayList)paraList.get(17);
		
		//ȫͼ��Ԫ��
		fullGraphStms=(ArrayList)paraList.get(19);
		
		//����ģʽ
		isSubProg=(Boolean)paraList.get(20);
	}
	
	/**********************
	 * ��ʼ�������һЩ���ݽṹ
	 ********************/
	public void initPara()
	{
		/*������Ϣ*/
		conceptName=new String[conceptNum];
		propName=new String[propNum];
		insName=new String[insNum];
		fullConceptName=new OntClass[fullConceptNum];
		fullPropName=new OntProperty[fullPropNum];
		fullInsName=new Individual[fullInsNum];
		
		/*������Ϣ*/
		cnptBasicTextDes=new TextDes[fullConceptNum];
		propBasicTextDes=new TextDes[fullPropNum];
		insBasicTextDes=new TextDes[fullInsNum];
		cnptTextDes=new TextDes[conceptNum];
		propTextDes=new TextDes[propNum];
		insTextDes=new TextDes[insNum];
		
		cnptOtTextDes=new ArrayList[conceptNum];
		propOtTextDes=new ArrayList[propNum];
		fullOtTextDes=new ArrayList();
		
		
		//������ͼ
		cnptSubG=new ConceptSubGraph[conceptNum];
		propSubG=new PropertySubGraph[propNum];
		curStmList=new ArrayList();
		
		ontLngURI=new OWLOntParse().getOntLngURI();
		ontParse=new OWLOntParse();
	}
	
	public int findCnptPosInFullName(String s)
	{
		int pos=-1;
		for (int i=0;i<fullConceptNum;i++){
			if (fullConceptName[i].toString().equals(s)){
				pos=i;
				break;
			}
		}
		return pos;
	}
	
	public OntClass findCnptInFullName(String s)
	{
		OntClass c=null;
		for (int i=0;i<fullConceptNum;i++){
			if (fullConceptName[i].toString().equals(s)){
				c=fullConceptName[i];
				break;
			}
		}
		return c;
	}
	
	public int findPropPosInFullName(String s)
	{
		int pos=-1;
		for (int i=0;i<fullPropNum;i++){
			if (fullPropName[i].toString().equals(s)){
				pos=i;
				break;
			}
		}
		return pos;
	}
	
	public OntProperty findPropInFullName(String s)
	{
		OntProperty c=null;
		for (int i=0;i<fullPropNum;i++){
			if (fullPropName[i].toString().equals(s)){
				c=fullPropName[i];
				break;
			}
		}
		return c;
	}
	
	public int findInsPosInFullName(String s)
	{
		int pos=-1;
		for (int i=0;i<fullInsNum;i++){
			if (fullInsName[i].toString().equals(s)){
				pos=i;
				break;
			}
		}
		return pos;
	}
	
	public Individual findInsInFullName(String s)
	{
		Individual c=null;
		for (int i=0;i<fullInsNum;i++){
			if (fullInsName[i].toString().equals(s)){
				c=fullInsName[i];
				break;
			}
		}
		return c;
	}
	
	/**********************
	 * �ݹ��������ڵ������������е�����
	 ********************/
	public ArrayList anonDesInModel(Resource b, int level)
	{
		ArrayList result=new ArrayList();
		
		/*�ж����Ƿ��еȼ۶���*/
		boolean hasEqu=false;
		ArrayList eqList=anonEquInModel(b);
		int subType=((Integer)eqList.get(0)).intValue();
		if (subType>=1 && subType<=3){
			ArrayList member=(ArrayList)eqList.get(1);
			if (subType==1){//��
				for (Iterator jt=member.iterator();jt.hasNext();){
					OntClass ctemp=(OntClass)jt.next();
					int tempPos=findCnptPosInFullName(ctemp.toString());
					result.addAll(cnptBasicTextDes[tempPos].text);
					hasEqu=true;
				}
			}
			else if (subType==2){//����
				for (Iterator jt=member.iterator();jt.hasNext();){
					OntProperty ptemp=(OntProperty)jt.next();
					int tempPos=findPropPosInFullName(ptemp.toString());
					result.addAll(propBasicTextDes[tempPos].text);
					hasEqu=true;
				}
			}
			else if (subType==3){//ʵ��
				for (Iterator jt=member.iterator();jt.hasNext();){
					Individual itemp=(Individual)jt.next();
					int tempPos=findInsPosInFullName(itemp.toString());
					result.addAll(insBasicTextDes[tempPos].text);
					hasEqu=true;
				}
			}
		}
		
		/*����еȼ۶���ֱ�ӷ���*/
		if(hasEqu){
			return result;
		}
		
		/*����b��ͷ����Ԫ��*/
		Selector s = new SimpleSelector(b,null,(RDFNode)null);
		for(StmtIterator it=m.listStatements(s);it.hasNext();){
			Statement st=it.nextStatement();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			
			if (p.isAnon()){//pΪ���������������
				result=null;
			}
			
			/*p��Description*/
			ArrayList lp=new ArrayList();
			/*�ж�p�ǲ���Ԫ����*/
			if (!isMetaOntData(p)){
				int pPos=findPropPosInFullName(p.toString());
				lp=propBasicTextDes[pPos].text;
			}
			
			if (lp!=null){
				result.addAll(lp);
			}
			
			ArrayList lo=new ArrayList();
			/*�ݹ���ֹ����*/
			if (!o.isAnon()){
				/*����Ԫ��*/
				if (!isMetaOntData(m.getResource(o.toString()))){
					/*�ж�o�ĳɷ�*/
					int t=getResourceType(o.toString());
					if (t==1){//Class
						int tempPos=findCnptPosInFullName(o.toString());
						/*�õ�Description*/
						lo=cnptBasicTextDes[tempPos].text;
					}
					else if (t==2){//Property
						int tempPos=findPropPosInFullName(o.toString());
						/*�õ�Description*/
						lo=propBasicTextDes[tempPos].text;
					}
					else if (t==3){//Instance
						int tempPos=findInsPosInFullName(o.toString());
						/*�õ�Description*/
						lo=insBasicTextDes[tempPos].text;
					}
					else if (t==4){//Other resource
						/*ֱ�Ӵ���local name*/
						String localname=null;
						if (o.isLiteral()){
							Literal l=(Literal)o;
							localname=l.getValue().toString(); 
						}
						else{
							localname=o.asNode().getLocalName(); 
						}
						ArrayList otherList=simpleStringPreProcess(localname);
				        for (Iterator xt=otherList.iterator();xt.hasNext();){
				        	String stemp=(String)xt.next();
				        	Word w=new Word();
				        	w.content=stemp;
				        	w.weight =1.0;
				        	lo.add(w);
				        }
					}
					/*return��ֹ����*/
					result.addAll(lo);
				}
			}
			else{
				/*�ݹ�*/
				lo=anonDesInModel(m.getResource(o.toString()),level+1);
				
				/*��˥��ϵ��*/
				double downPare=Math.pow(wAnonNode,((double)level+1.0));
				for (Iterator itx=lo.iterator();itx.hasNext();){
					Word w=(Word)itx.next();
					w.weight*=downPare;
				}
				result.addAll(lo);
			}
		}
		return result;
	}
	
	/**********************
	 * �ݹ��������ڵ������������еĻ�������
	 * ��ֻ������ͨ������
	 ********************/
	public ArrayList anonBasicDesInModel(Resource b,int level)
	{
		ArrayList result=new ArrayList();
		/*����b��ͷ����Ԫ��*/
		Selector s = new SimpleSelector(b,null,(RDFNode)null);
		for(StmtIterator it=m.listStatements(s);it.hasNext();){
			Statement st=it.nextStatement();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			
			if (p.isAnon()){//pΪ���������������
				result=null;
			}
			
			/*p��Description*/
			ArrayList lp=new ArrayList();
			boolean hasEqu=false;
			/*�ж�p�ǲ���Ԫ����*/
			if (!isMetaOntData(p)){
				/*ֱ�Ӵ���local name*/
				ArrayList otherList=simpleStringPreProcess(p.getLocalName());
		        for (Iterator xt=otherList.iterator();xt.hasNext();){
		        	String stemp=(String)xt.next();
		        	Word w=new Word();
		        	w.content=stemp;
		        	w.weight =1.0;
		        	lp.add(w);
		        }
			}
			else{
				String strp=p.getLocalName().toString();
				if (strp.equals("equivalentClass")
					||strp.equals("equivalentProperty")
					||strp.equals("sameAs")){
					hasEqu=true;
				}
			}
			
			if (lp!=null){
				result.addAll(lp);
			}
			
			/*�ݹ���ֹ����*/
			ArrayList lo=new ArrayList();
			if (!hasEqu){//û�еȼ۵Ĺ�ϵ
				if (!o.isAnon()) {
					/*����Ԫ��*/
					if (!isMetaOntData(m.getResource(o.toString()))){
						/* ֱ�Ӵ���local name */
						String ostr=null;
						if (o.isLiteral()){
							ostr=o.asNode().getLiteralValue().toString();
						}
						else if(o.isURIResource()){
							ostr=o.asNode().getLocalName();
						}
						ArrayList otherList = simpleStringPreProcess(ostr);
						for (Iterator xt = otherList.iterator(); xt.hasNext();) {
							String stemp = (String) xt.next();
							Word w = new Word();
							w.content = stemp;
							w.weight = 1.0;
							lo.add(w);
						}
						/* return��ֹ���� */
						result.addAll(lo);
					}					
				}
				else{
					/*�ݹ�*/
					lo=anonBasicDesInModel(m.getResource(o.toString()),level+1);
					/*��˥��ϵ��*/
					double downPare=Math.pow(wAnonNode,((double)level+1.0));
					for (Iterator itx=lo.iterator();itx.hasNext();){
						Word w=(Word)itx.next();
						w.weight*=downPare;
					}
					result.addAll(lo);
				}
			}
		}
		return result;
	}
	
	public ArrayList anonEquInModel(Resource r)
	{
		ArrayList result=new ArrayList();
		ArrayList member=new ArrayList();
		//�õ�sub������
		int subType=getResourceType(r.toString());
		result.add(0,subType);
		if (subType==1){//Class
			OntClass cx=null;
			cx=this.findCnptInFullName(r.toString());
			
			/*�ж���û�еȼ���*/
			for (ExtendedIterator it=cx.listEquivalentClasses();it.hasNext();){
				OntClass cy=(OntClass)it.next();
				if (!cy.isAnon()){
					member.add(cy);
				}
			}
			/*�ж���û��sameAs*/
			for (ExtendedIterator it=cx.listSameAs();it.hasNext();){
				OntClass cy=(OntClass)it.next();
				if (!cy.isAnon()){
					member.add(cy);
				}
			}
		}
		else if (subType==2){//Property
			OntProperty px=null;
			px=this.findPropInFullName(r.toString());
			/*�ж���û�еȼ�����*/
			for (ExtendedIterator it=px.listEquivalentProperties();it.hasNext();){
				OntProperty cy=(OntProperty)it.next();
				if (!cy.isAnon()){
					member.add(cy);
				}
			}
			/*�ж���û��sameAs*/
			for (ExtendedIterator it=px.listSameAs();it.hasNext();){
				OntProperty cy=(OntProperty)it.next();
				if (!cy.isAnon()){
					member.add(cy);
				}
			}
			
		}else if (subType==3){//Instance
			Individual idx=null;
			idx=this.findInsInFullName(r.toString());
			/*�ж���û��sameAs*/
			for (ExtendedIterator it=idx.listSameAs();it.hasNext();){
				Individual cy=(Individual)it.next();
				if (!cy.isAnon()){
					member.add(cy);
				}
			}
		}
		result.add(1,member);
		return result;
	}
	
	/**********************
	 * �ݹ��������ڵ�����ͼ�е�����
	 * ֻ����blank node��ͷ����Ԫ��
	 ********************/
	public ArrayList anonDesInSubGraphSingleDirect(Resource b,int level,DirectedGraph g)
	{
		ArrayList result=new ArrayList();
		/*����b��ͷ����Ԫ��*/
		Selector s = new SimpleSelector(b,null,(RDFNode)null);
		for(StmtIterator it=m.listStatements(s);it.hasNext();){
			Statement st=it.nextStatement();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			
	   		ArrayList localNamelist=ontParse.getStLocalName(st);
//	   		String pStr=(String)localNamelist.get(1);
	   		String oStr=(String)localNamelist.get(2);
			
	   		/*��ǰ��Ԫ����ͼ�е�����������o��ͼ��,���Ⲣ���ǳ������*/
	   		if (!g.containsVertex(oStr)){
	   			continue;
	   		}
	   		
			if (p.isAnon()){//pΪ���������������
				result=null;
			}
			
			/*p��Description*/
			ArrayList lp=new ArrayList();
			/*�ж�p�ǲ���Ԫ����*/
			if (!isMetaOntData(p)){
				int pPos=findPropPosInFullName(p.toString());
				if (pPos!=-1){
					lp=propBasicTextDes[pPos].text;
				}
				else{
					/*ֱ����ȡlocal name*/
					String localname=null;
					localname=p.getLocalName(); 

					ArrayList otherList=simpleStringPreProcess(localname);
			        for (Iterator xt=otherList.iterator();xt.hasNext();){
			        	String stemp=(String)xt.next();
			        	Word w=new Word();
			        	w.content=stemp;
			        	w.weight =1.0;
			        	lp.add(w);
			        }
				}
			}
			
			if (lp!=null){
				result.addAll(lp);
			}
			
			/*o��Description*/
			ArrayList lo=new ArrayList();
			/*�ݹ���ֹ����*/
			if (!ontParse.isBlankNode(o.toString())){
				/*����Ԫ��*/
				if (!isMetaOntData(m.getResource(o.toString()))){
					/*�ж�o�ĳɷ�*/
					int t=getResourceType(o.toString());
					if (t==1){//Class
						int tempPos=findCnptPosInFullName(o.toString());
						lo=cnptBasicTextDes[tempPos].text;
					}
					else if (t==2){//Property
						int tempPos=findPropPosInFullName(o.toString());
						/*�õ�Description*/
						lo=propBasicTextDes[tempPos].text;
					}
					else if (t==3){//Instance
						int tempPos=findInsPosInFullName(o.toString());
						/*�õ�Description*/
//						if (tempPos==-1){
//							System.out.println("stop");
//						}
						lo=insBasicTextDes[tempPos].text;
					}
					else if (t==4){//Other resource
						/*ֱ����ȡlocal name*/
						String localname=null;
						if (o.isLiteral()){
							Literal l=(Literal)o;
							localname=l.getValue().toString(); 
						}
						else{
							localname=o.asNode().getLocalName(); 
						}
						if (localname.equals("")){
							localname=o.toString();
						}
						ArrayList otherList=simpleStringPreProcess(localname);
				        for (Iterator xt=otherList.iterator();xt.hasNext();){
				        	String stemp=(String)xt.next();
				        	Word w=new Word();
				        	w.content=stemp;
				        	w.weight =1.0;
				        	lo.add(w);
				        }
					}
					/*return��ֹ����*/
					result.addAll(lo);
				}				
			}
			else{
				/*�ݹ�*/
				lo=anonDesInSubGraphSingleDirect(m.getResource(o.toString()),level+1,g);
				
				/*��˥��ϵ��*/
				double downPare=Math.pow(wAnonNode,((double)level+1.0));
				for (Iterator itx=lo.iterator();itx.hasNext();){
					Word w=(Word)itx.next();
					w.weight*=downPare;
				}
				result.addAll(lo);
			}
		}
		return result;
	}
	
	/**********************
	 * �ݹ��������ڵ�����ͼ�е�����
	 * ͬʱ����blank node��ͷ�ͽ�β����Ԫ��
	 ********************/
	public ArrayList anonDesInSubGraphBiDirect(Resource b,int level,DirectedGraph g, Set visitedSet)
	{
		ArrayList result=new ArrayList();
		
		/*����b��ͷ����Ԫ��*/
		for(Iterator it=curStmList.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			Resource s=st.getSubject();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			
			//�Ƿ��Ѿ����ʹ�
			if (visitedSet.contains(st)){
				continue;//��������Ԫ��
			}
			
			//�ж���b��ͷ
			if (s.toString().equals(b.toString())){
		   		
		   		//������ʹ��б�
		   		visitedSet.add(st);
		   		
		   		if (p.isAnon()){//pΪ���������������
					result=null;
				}
				
				/*p��Description*/
				ArrayList lp=new ArrayList();
				/*�ж�p�ǲ���Ԫ����*/
				if (!isMetaOntData(p)){
					int pPos=findPropPosInFullName(p.toString());
					if (pPos!=-1){
						lp=propBasicTextDes[pPos].text;
					}
					else{
						/*ֱ����ȡlocal name*/
						String localname=null;
						localname=p.getLocalName(); 

						ArrayList otherList=simpleStringPreProcess(localname);
				        for (Iterator xt=otherList.iterator();xt.hasNext();){
				        	String stemp=(String)xt.next();
				        	Word w=new Word();
				        	w.content=stemp;
				        	w.weight =1.0;
				        	lp.add(w);
				        }
					}
				}
				
				if (lp!=null){
					result.addAll(lp);
				}
				
				/*o��Description*/
				ArrayList lo=new ArrayList();
				/*�ݹ���ֹ����*/
				if (!ontParse.isBlankNode(o.toString())){
					/*����Ԫ��*/
					if (!isMetaOntData(m.getResource(o.toString()))){
						/*�ж�o�ĳɷ�*/
						int t=getResourceType(o.toString());
						if (t==1){//Class
							int tempPos=findCnptPosInFullName(o.toString());
							lo=cnptBasicTextDes[tempPos].text;
						}
						else if (t==2){//Property
							int tempPos=findPropPosInFullName(o.toString());
							/*�õ�Description*/
							lo=propBasicTextDes[tempPos].text;
						}
						else if (t==3){//Instance
							int tempPos=findInsPosInFullName(o.toString());
							/*�õ�Description*/
//							if (tempPos==-1){
//								System.out.println("stop");
//							}
							lo=insBasicTextDes[tempPos].text;
						}
						else if (t==4){//Other resource
							/*ֱ����ȡlocal name*/
							String localname=null;
							if (o.isLiteral()){
								Literal l=(Literal)o;
								localname=l.getLexicalForm().toString(); 
							}
							else{
								localname=o.asNode().getLocalName(); 
							}
							if (localname.equals("")){
								localname=o.toString();
							}
							ArrayList otherList=simpleStringPreProcess(localname);
					        for (Iterator xt=otherList.iterator();xt.hasNext();){
					        	String stemp=(String)xt.next();
					        	Word w=new Word();
					        	w.content=stemp;
					        	w.weight =1.0;
					        	lo.add(w);
					        }
						}
						/*return��ֹ����*/
						result.addAll(lo);
					}				
				}
				else{
					/*�ݹ�*/
					lo=anonDesInSubGraphBiDirect(m.getResource(o.toString()),level+1,g,visitedSet);
					
					/*��˥��ϵ��*/
					double downPare=Math.pow(wAnonNode,((double)level+1.0));
					for (Iterator itx=lo.iterator();itx.hasNext();){
						Word w=(Word)itx.next();
						w.weight*=downPare;
					}
					result.addAll(lo);
				}
			}
		}
		
		/*����b��β����Ԫ��*/
		for(Iterator it=curStmList.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			Resource s=st.getSubject();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			
			//�Ƿ��Ѿ����ʹ�
			if (visitedSet.contains(st)){
				continue;//��������Ԫ��
			}
			
			//�ж���b��β
			if (o.asNode().toString().equals(b.toString())){
		   		
		   		//������ʹ��б�
		   		visitedSet.add(st);
		   		
		   		if (p.isAnon()){//pΪ���������������
					result=null;
				}
				
				/*p��Description*/
				ArrayList lp=new ArrayList();
				/*�ж�p�ǲ���Ԫ����*/
				if (!isMetaOntData(p)){
					int pPos=findPropPosInFullName(p.toString());
					if (pPos!=-1){
						lp=propBasicTextDes[pPos].text;
					}
					else{
						/*ֱ����ȡlocal name*/
						String localname=null;
						localname=p.getLocalName(); 

						ArrayList otherList=simpleStringPreProcess(localname);
				        for (Iterator xt=otherList.iterator();xt.hasNext();){
				        	String stemp=(String)xt.next();
				        	Word w=new Word();
				        	w.content=stemp;
				        	w.weight =1.0;
				        	lp.add(w);
				        }
					}
				}
				
				if (lp!=null){
					result.addAll(lp);
				}
				
				/*s��Description*/
				ArrayList lo=new ArrayList();
				/*�ݹ���ֹ����*/
				if (!ontParse.isBlankNode(s.toString())){
					/*����Ԫ��*/
					if (!isMetaOntData(m.getResource(s.toString()))){
						/*�ж�s�ĳɷ�*/
						int t=getResourceType(s.toString());
						if (t==1){//Class
							int tempPos=findCnptPosInFullName(s.toString());
							lo=cnptBasicTextDes[tempPos].text;
						}
						else if (t==2){//Property
							int tempPos=findPropPosInFullName(s.toString());
							/*�õ�Description*/
							lo=propBasicTextDes[tempPos].text;
						}
						else if (t==3){//Instance
							int tempPos=findInsPosInFullName(s.toString());
							/*�õ�Description*/
//							if (tempPos==-1){
//								System.out.println("stop");
//							}
							lo=insBasicTextDes[tempPos].text;
						}
						else if (t==4){//Other resource
							/*ֱ����ȡlocal name*/
							String localname=null;
							if (s.isLiteral()){
								Literal l=(Literal)s;
								localname=l.getValue().toString(); 
							}
							else{
								localname=s.asNode().getLocalName(); 
							}
							if (localname.equals("")){
								localname=s.toString();
							}
							ArrayList otherList=simpleStringPreProcess(localname);
					        for (Iterator xt=otherList.iterator();xt.hasNext();){
					        	String stemp=(String)xt.next();
					        	Word w=new Word();
					        	w.content=stemp;
					        	w.weight =1.0;
					        	lo.add(w);
					        }
						}
						/*return��ֹ����*/
						result.addAll(lo);
					}				
				}
				else{
					/*�ݹ�*/
					lo=anonDesInSubGraphBiDirect(m.getResource(s.toString()),level+1,g,visitedSet);
					
					/*��˥��ϵ��*/
					double downPare=Math.pow(wAnonNode,((double)level+1.0));
					for (Iterator itx=lo.iterator();itx.hasNext();){
						Word w=(Word)itx.next();
						w.weight*=downPare;
					}
					result.addAll(lo);
				}
			}
		}

		return result;
	}
	
	/**********************
	 * �ݹ��������ڵ�����ͼ�е�����
	 * ͬʱ����blank node��ͷ�ͽ�β����Ԫ��
	 ********************/
	public ArrayList anonDesInFullGraphBiDirect(Resource b,int level, Set visitedSet)
	{
		ArrayList result=new ArrayList();
		
		/*����b��ͷ����Ԫ��*/
		for(Iterator it=fullGraphStms.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			Resource s=st.getSubject();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			
			//�Ƿ��Ѿ����ʹ�
			if (visitedSet.contains(st)){
				continue;//��������Ԫ��
			}
			
			//�ж���b��ͷ
			if (s.toString().equals(b.toString())){
		   		
		   		//������ʹ��б�
		   		visitedSet.add(st);
		   		
		   		if (p.isAnon()){//pΪ���������������
					result=null;
				}
				
				/*p��Description*/
				ArrayList lp=new ArrayList();
				/*�ж�p�ǲ���Ԫ����*/
				if (!isMetaOntData(p)){
					int pPos=findPropPosInFullName(p.toString());
					if (pPos!=-1){
						lp=propBasicTextDes[pPos].text;
					}
					else{
						/*ֱ����ȡlocal name*/
						String localname=null;
						localname=p.getLocalName(); 

						ArrayList otherList=simpleStringPreProcess(localname);
				        for (Iterator xt=otherList.iterator();xt.hasNext();){
				        	String stemp=(String)xt.next();
				        	Word w=new Word();
				        	w.content=stemp;
				        	w.weight =1.0;
				        	lp.add(w);
				        }
					}
				}
				
				if (lp!=null){
					result.addAll(lp);
				}
				
				/*o��Description*/
				ArrayList lo=new ArrayList();
				/*�ݹ���ֹ����*/
				if (!ontParse.isBlankNode(o.toString())){
					/*����Ԫ��*/
					if (!isMetaOntData(m.getResource(o.toString()))){
						/*�ж�o�ĳɷ�*/
						int t=getResourceType(o.toString());
						if (t==1){//Class
							int tempPos=findCnptPosInFullName(o.toString());
							lo=cnptBasicTextDes[tempPos].text;
						}
						else if (t==2){//Property
							int tempPos=findPropPosInFullName(o.toString());
							/*�õ�Description*/
							lo=propBasicTextDes[tempPos].text;
						}
						else if (t==3){//Instance
							int tempPos=findInsPosInFullName(o.toString());
							/*�õ�Description*/
//							if (tempPos==-1){
//								System.out.println("stop");
//							}
							lo=insBasicTextDes[tempPos].text;
						}
						else if (t==4){//Other resource
							/*ֱ����ȡlocal name*/
							String localname=null;
							if (o.isLiteral()){
								Literal l=(Literal)o;
								localname=l.getLexicalForm().toString(); 
							}
							else{
								localname=o.asNode().getLocalName(); 
							}
							if (localname.equals("")){
								localname=o.toString();
							}
							ArrayList otherList=simpleStringPreProcess(localname);
					        for (Iterator xt=otherList.iterator();xt.hasNext();){
					        	String stemp=(String)xt.next();
					        	Word w=new Word();
					        	w.content=stemp;
					        	w.weight =1.0;
					        	lo.add(w);
					        }
						}
						/*return��ֹ����*/
						result.addAll(lo);
					}				
				}
				else{
					/*�ݹ�*/
					lo=anonDesInFullGraphBiDirect(m.getResource(o.toString()),level+1,visitedSet);
					
					/*��˥��ϵ��*/
					double downPare=Math.pow(wAnonNode,((double)level+1.0));
					for (Iterator itx=lo.iterator();itx.hasNext();){
						Word w=(Word)itx.next();
						w.weight*=downPare;
					}
					result.addAll(lo);
				}
			}
		}
		
		/*����b��β����Ԫ��*/
		for(Iterator it=fullGraphStms.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			Resource s=st.getSubject();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			
			//�Ƿ��Ѿ����ʹ�
			if (visitedSet.contains(st)){
				continue;//��������Ԫ��
			}
			
			//�ж���b��β
			if (o.asNode().toString().equals(b.toString())){
		   		
		   		//������ʹ��б�
		   		visitedSet.add(st);
		   		
		   		if (p.isAnon()){//pΪ���������������
					result=null;
				}
				
				/*p��Description*/
				ArrayList lp=new ArrayList();
				/*�ж�p�ǲ���Ԫ����*/
				if (!isMetaOntData(p)){
					int pPos=findPropPosInFullName(p.toString());
					if (pPos!=-1){
						lp=propBasicTextDes[pPos].text;
					}
					else{
						/*ֱ����ȡlocal name*/
						String localname=null;
						localname=p.getLocalName(); 

						ArrayList otherList=simpleStringPreProcess(localname);
				        for (Iterator xt=otherList.iterator();xt.hasNext();){
				        	String stemp=(String)xt.next();
				        	Word w=new Word();
				        	w.content=stemp;
				        	w.weight =1.0;
				        	lp.add(w);
				        }
					}
				}
				
				if (lp!=null){
					result.addAll(lp);
				}
				
				/*s��Description*/
				ArrayList lo=new ArrayList();
				/*�ݹ���ֹ����*/
				if (!ontParse.isBlankNode(s.toString())){
					/*����Ԫ��*/
					if (!isMetaOntData(m.getResource(s.toString()))){
						/*�ж�s�ĳɷ�*/
						int t=getResourceType(s.toString());
						if (t==1){//Class
							int tempPos=findCnptPosInFullName(s.toString());
							lo=cnptBasicTextDes[tempPos].text;
						}
						else if (t==2){//Property
							int tempPos=findPropPosInFullName(s.toString());
							/*�õ�Description*/
							lo=propBasicTextDes[tempPos].text;
						}
						else if (t==3){//Instance
							int tempPos=findInsPosInFullName(s.toString());
							/*�õ�Description*/
//							if (tempPos==-1){
//								System.out.println("stop");
//							}
							lo=insBasicTextDes[tempPos].text;
						}
						else if (t==4){//Other resource
							/*ֱ����ȡlocal name*/
							String localname=null;
							if (s.isLiteral()){
								Literal l=(Literal)s;
								localname=l.getValue().toString(); 
							}
							else{
								localname=s.asNode().getLocalName(); 
							}
							if (localname.equals("")){
								localname=s.toString();
							}
							ArrayList otherList=simpleStringPreProcess(localname);
					        for (Iterator xt=otherList.iterator();xt.hasNext();){
					        	String stemp=(String)xt.next();
					        	Word w=new Word();
					        	w.content=stemp;
					        	w.weight =1.0;
					        	lo.add(w);
					        }
						}
						/*return��ֹ����*/
						result.addAll(lo);
					}				
				}
				else{
					/*�ݹ�*/
					lo=anonDesInFullGraphBiDirect(m.getResource(s.toString()),level+1,visitedSet);
					
					/*��˥��ϵ��*/
					double downPare=Math.pow(wAnonNode,((double)level+1.0));
					for (Iterator itx=lo.iterator();itx.hasNext();){
						Word w=(Word)itx.next();
						w.weight*=downPare;
					}
					result.addAll(lo);
				}
			}
		}

		return result;
	}
	
	public int getResourceType(String s)
	{
		Resource r=m.getResource(s);   
		if (r==null){
			return -1;//����resource
		}
		else if (r.isLiteral()){//����
			return 4;
		}
		else if (this.ontLngURI.contains(r.getNameSpace())){//Ԫ��
//			System.out.println("Ԫ�"+r.toString());
			return 4;
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
				ArrayList lt=getAnonResourceWithType(s);
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
	
	public boolean isMetaOntData(Resource r)
	{
		String s=new OWLOntParse().getResourceBaseURI(r.toString());
		return (ontLngURI.contains(s));
	}
	
	/**********************
	 * ������ͼ�е������ı�����
	 ********************/
	public ArrayList cnptHrcTextDes(OntClass c,DirectedGraph g)
	{
		ArrayList result=new ArrayList();
		
		/*subClass�ı�����*/
		TextDes subDes=new TextDes();
		subDes=cnptSubClassDes(c,g);
		result.add(0,subDes);
				
		/*superClass�ı�����*/
		TextDes superDes=new TextDes();
		superDes=cnptSuperClassDes(c,g);
		result.add(1,superDes);
		
		/*sibling Class�ı�����*/
		TextDes siblingDes=new TextDes();
		siblingDes=cnptSiblingClassDes(c,g);
		result.add(2,siblingDes);
		
		/*disjoint Class�ı�����*/
		TextDes disjointDes=new TextDes();
		disjointDes=cnptDisjointClassDes(c,g);
		result.add(3,disjointDes);
		
		/*complementOf Class�ı�����*/
		TextDes compDes=new TextDes();
		compDes=cnptComplementClassDes(c,g);
		result.add(4,compDes);
		
		return result;
	}
	
	public int getSubGraph(OntClass c)
	{
		int pos=0;
		for (int i=0;i<conceptNum;i++){
			if (cnptSubG[i].conceptName.equals(c.getLocalName())){
				pos=i;
				break;
			}
		}
		return pos;
	}
	
	public int getSubGraph(OntProperty p)
	{
		int pos=0;
		for (int i=0;i<propNum;i++){
			if (propSubG[i].propName.equals(p.getLocalName())){
				pos=i;
				break;
			}
		}
		return pos;
	}
	
	/**********************
	 * subClass�ı�����
	 ********************/
	public TextDes cnptSubClassDes(OntClass c,DirectedGraph g)
	{
		OWLOntParse parse=new OWLOntParse();
		TextDes subDes=new TextDes();
		subDes.name=c.toString();
		subDes.text=new ArrayList();
		/*�õ�ȫ��subClass*/
		Set set=new HashSet();
		set.add(c);
		ArrayList subCList=parse.listSubClassOfConceptWithDistance(c,0,set);
		for(Iterator it=subCList.iterator();it.hasNext();){
			ArrayList lt=(ArrayList)it.next();
			OntClass subC=(OntClass)lt.get(0);
			int level=((Integer)lt.get(1)).intValue();
			
			/*�жϵ�ǰ��subClass�Ƿ��������ͼ��
			 * �ϸ���ж�Ӧ�ÿ�����������Ԫ�飬����ֻ�򵥿��ǽڵ��Ƿ񱻰���
			 */
			if (!g.containsVertex(parse.getResourceLocalName(subC))){
				continue;
			}
			
			/*��Ӹ�subClass��Description*/
			/*���ж��Ƿ�����������*/
			if (!subC.isAnon()){//������
				int pos=findCnptPosInFullName(subC.toString());//λ��
				ArrayList lb=cnptBasicTextDes[pos].text;
				for (Iterator itx=lb.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=(1-subClassDec*level);
					subDes.text.add(w);
				}
			}
			else{//����
				/*�����ͼ�жԸ������ڵ������*/
				Statement sx=ontParse.getAStatement(m,subC,
						m.getProperty("http://www.w3.org/2000/01/rdf-schema#subClassOf"),c);
				Set setx=new HashSet();
				setx.add(sx);
				ArrayList anonList=anonDesInSubGraphBiDirect(subC,0,g,setx);
//				ArrayList anonList=anonDesInSubGraphSingleDirect(subC,0,g);
//				ArrayList anonList=this.anonDesInModel(c,0);
				for (Iterator itx=anonList.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=(1-subClassDec*level);
					subDes.text.add(w);
				}
			}
		}
		
		/*���ô���*/
		subDes.text=desPostProcess(subDes.text);
		return subDes;
	}
	
	/**********************
	 * superClass�ı�����
	 ********************/
	public TextDes cnptSuperClassDes(OntClass c,DirectedGraph g)
	{
		OWLOntParse parse=new OWLOntParse();
		TextDes des=new TextDes();
		des.name=c.toString();
		des.text=new ArrayList();
		/*�õ�ȫ��superClass*/
		Set set=new HashSet();
		set.add(c);
		ArrayList subCList=parse.listSuperClassOfConceptWithDistance(c,0,set);
		for(Iterator it=subCList.iterator();it.hasNext();){
			ArrayList lt=(ArrayList)it.next();
			OntClass superC=(OntClass)lt.get(0);
			int level=((Integer)lt.get(1)).intValue();
			
			/*�жϵ�ǰ��superClass�Ƿ��������ͼ��
			 * �ϸ���ж�Ӧ�ÿ�����������Ԫ�飬����ֻ�򵥿��ǽڵ��Ƿ񱻰���
			 */
			if (!g.containsVertex(parse.getResourceLocalName(superC))){
				continue;
			}
			
			/*��Ӹ�superClass��Description*/
			/*���ж��Ƿ�����������*/
			if (!superC.isAnon()){//������
				int pos=findCnptPosInFullName(superC.toString());//λ��
				if (pos>=0){
					ArrayList lb=cnptBasicTextDes[pos].text;
					for (Iterator itx=lb.iterator();itx.hasNext();){
						Word w=(Word)((Word)itx.next()).clone();
						w.weight*=(1-superClassDec*level);
						des.text.add(w);
					}
				}				
			}
			else{//����
				/*�����ͼ�жԸ������ڵ������*/
				Statement sx=ontParse.getAStatement(m,(Resource)c,
						m.getProperty("http://www.w3.org/2000/01/rdf-schema#subClassOf"),superC);
				Set setx=new HashSet();
				setx.add(sx);
				ArrayList anonList=anonDesInSubGraphBiDirect(superC,0,g,setx);
//				ArrayList anonList=anonDesInSubGraphSingleDirect(superC,0,g);
//				ArrayList anonList=anonDesInModel(c,0);
				for (Iterator itx=anonList.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=(1-superClassDec*level);
					des.text.add(w);
				}
			}
		}
		/*���ô���*/
		des.text=desPostProcess(des.text);
		return des;
	}
	
	/**********************
	 * sibling Class�ı�����
	 ********************/
	public TextDes cnptSiblingClassDes(OntClass c,DirectedGraph g)
	{
		OWLOntParse parse=new OWLOntParse();
		TextDes des=new TextDes();
		des.name=c.toString();
		des.text=new ArrayList();
		/*�õ�ȫ��sibling Class*/
		ArrayList subCList=parse.listSiblingsOfConcept(c);
		for(Iterator it=subCList.iterator();it.hasNext();){
			OntClass sibC=(OntClass)it.next();
			
			/*�жϵ�ǰ��siblingClass�Ƿ��������ͼ��
			 * �ϸ���ж�Ӧ�ÿ�����������Ԫ�飬����ֻ�򵥿��ǽڵ��Ƿ񱻰���
			 */
			if (!g.containsVertex(parse.getResourceLocalName(sibC))){
				continue;
			}
			
			/*��Ӹ�siblingClass��Description*/
			/*���ж��Ƿ�����������*/
			if (!sibC.isAnon()){//������
				int pos=findCnptPosInFullName(sibC.toString());//λ��
				ArrayList lb=cnptBasicTextDes[pos].text;
				for (Iterator itx=lb.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=wsiblingClassDec;
					des.text.add(w);
				}
			}
			else{//����
				/*�����ͼ�жԸ������ڵ������*/
				Set setx=new HashSet();
				ArrayList anonList=anonDesInSubGraphBiDirect(sibC,0,g,setx);
//				ArrayList anonList=anonDesInSubGraphSingleDirect(sibC,0,g);
//				ArrayList anonList=anonDesInModel(c,0);
				for (Iterator itx=anonList.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=wsiblingClassDec;
					des.text.add(w);
				}
			}
		}
		
		/*���ô���*/
		des.text=desPostProcess(des.text);
		
		return des;
	}
	
	/**********************
	 * disjoint Class�ı�����
	 ********************/
	public TextDes cnptDisjointClassDes(OntClass c,DirectedGraph g)
	{
		OWLOntParse parse=new OWLOntParse();
		TextDes des=new TextDes();
		des.name=c.toString();
		des.text=new ArrayList();
		/*�õ�ȫ��disjoint Class*/
		ArrayList disCList=parse.listDisjointClassOfConcept(c);
		for(Iterator it=disCList.iterator();it.hasNext();){
			OntClass disjC=(OntClass)it.next();
			
			/*�жϵ�ǰ��disjointClass�Ƿ��������ͼ��
			 * �ϸ���ж�Ӧ�ÿ�����������Ԫ�飬����ֻ�򵥿��ǽڵ��Ƿ񱻰���
			 */
			if (!g.containsVertex(parse.getResourceLocalName(disjC))){
				continue;
			}
			
			/*��Ӹ�disjointClass��Description*/
			/*���ж��Ƿ�����������*/
			if (!disjC.isAnon()){//������
				int pos=findCnptPosInFullName(disjC.toString());//λ��
				ArrayList lb=cnptBasicTextDes[pos].text;
				for (Iterator itx=lb.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=wDisjClassDec;
					des.text.add(w);
				}
			}
			else{//����
				/*�����ͼ�жԸ������ڵ������*/
				Set setx=new HashSet();
				ArrayList anonList=anonDesInSubGraphBiDirect(disjC,0,g,setx);
//				ArrayList anonList=anonDesInSubGraphSingleDirect(disjC,0,g);
//				ArrayList anonList=anonDesInModel(c,0);
				for (Iterator itx=anonList.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=wDisjClassDec;
					des.text.add(w);
				}
			}
		}
		
		/*���ô���*/
		des.text=desPostProcess(des.text);
		return des;
	}
	
	/**********************
	 * complementOf Class�ı�����
	 ********************/
	public TextDes cnptComplementClassDes(OntClass c,DirectedGraph g)
	{
		OWLOntParse parse=new OWLOntParse();
		TextDes des=new TextDes();
		des.name=c.toString();
		des.text=new ArrayList();
		/*�õ�ȫ��complementOf Class*/
		ArrayList cmpCList=parse.listComplementClassOfConcept(c);
		for(Iterator it=cmpCList.iterator();it.hasNext();){
			OntClass cmplC=(OntClass)it.next();
			
			/*�жϵ�ǰ��complementOf Class�Ƿ��������ͼ��
			 * �ϸ���ж�Ӧ�ÿ�����������Ԫ�飬����ֻ�򵥿��ǽڵ��Ƿ񱻰���
			 */
			if (!g.containsVertex(parse.getResourceLocalName(cmplC))){
				continue;
			}
			
			/*��Ӹ�complementOf Class��Description*/
			/*���ж��Ƿ�����������*/
			if (!cmplC.isAnon()){//������
				int pos=findCnptPosInFullName(cmplC.toString());//λ��
				ArrayList lb=cnptBasicTextDes[pos].text;
				for (Iterator itx=lb.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=wCmplClassDec;
					des.text.add(w);
				}
			}
			else{//����
				/*�����ͼ�жԸ������ڵ������*/
				Set setx=new HashSet();
				ArrayList anonList=anonDesInSubGraphBiDirect(cmplC,0,g,setx);
//				ArrayList anonList=anonDesInSubGraphSingleDirect(cmplC,0,g);
//				ArrayList anonList=anonDesInModel(c,0);
				for (Iterator itx=anonList.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=wCmplClassDec;
					des.text.add(w);
				}
			}
		}
		
		/*���ô���*/
		des.text=desPostProcess(des.text);
		return des;
	}
	
	/**********************
	 * ������ͼ�е����������Ե��ı�����
	 ********************/
	public ArrayList cnptPropTextDes(OntClass c,DirectedGraph g)
	{
		ArrayList result=new ArrayList();
		
		/*Class��Domain�ı�����*/
		TextDes domainDes=new TextDes();
		domainDes=cnptDomainDes(c,g);
		result.add(0,domainDes);
		
		/*Class��Range�ı�����*/
		TextDes RangeDes=new TextDes();
		RangeDes=cnptRangeDes(c,g);
		result.add(1,RangeDes);
		
		return result;
	}
	
	/**********************
	 * Class��Domain�ı�����
	 ********************/
	public TextDes cnptDomainDes(OntClass c,DirectedGraph g)
	{
		OWLOntParse parse=new OWLOntParse();
		TextDes des=new TextDes();
		des.name=c.toString();
		des.text=new ArrayList();
		
		/*�õ���c��ΪDomain��Property*/
		ArrayList pList=new ArrayList();
		for(Iterator it=curStmList.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			Resource r=st.getSubject();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			if (p.toString().equals("http://www.w3.org/2000/01/rdf-schema#domain")
				&& o.toString().equals(c.toString())){
				OntProperty pt=m.getOntProperty(r.toString());
				if (pt!=null){
					pList.add(pt);
				}
			}
		}
		
		/*ȷ��ÿ��property��c��Զ���̶�*/
		/*�������ҵ�c�����и��࣬�����к������Domain����
		 * ������Զ�ľ��Ǹ�Domain��ԭʼ������*/
		/*c�ĸ���*/
		Set set=new HashSet();
		set.add(c);
		ArrayList fatherList=parse.listSuperClassOfConceptWithDistance(c,0,set);
		for (Iterator jt=pList.iterator();jt.hasNext();){
			/*����p--Domain--c��ȷ��ԭʼ���������c�ľ���*/
			OntProperty pt=(OntProperty)jt.next();
			OntClass maxC=null;
			int maxLevel=0;
			for(Iterator kt=fatherList.iterator();kt.hasNext();){
				ArrayList lt=(ArrayList)kt.next();
				OntClass superC=(OntClass)lt.get(0);
				int level=((Integer)lt.get(1)).intValue();
				if (parse.listDomainPropertyOfConcept(superC,true).contains(pt)){
					if (level>=maxLevel){
						maxLevel=level;
						maxC=superC;
					}
				}
			}
			
			/*����p��Description*/
			if (!pt.isAnon()){//������
				int pos=findPropPosInFullName(pt.toString());//λ��
				ArrayList lb=propBasicTextDes[pos].text;
				for (Iterator itx=lb.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=(1.0/(1.0+maxLevel));
					des.text.add(w);
				}
			}
			else{//����
				/*�����ͼ�жԸ������ڵ������*/
				Statement sx=ontParse.getAStatement(m,(Resource)pt,
						m.getProperty("http://www.w3.org/2000/01/rdf-schema#domain"),c);
				Set setx=new HashSet();
				setx.add(sx);
				ArrayList anonList=anonDesInSubGraphBiDirect(pt,0,g,setx);
//				ArrayList anonList=anonDesInSubGraphSingleDirect(pt,0,g);
//				ArrayList anonList=anonDesInModel(pt,0);
				for (Iterator itx=anonList.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=(1.0/(1.0+maxLevel));
					des.text.add(w);
				}
			}
		}
		
		/*���ô���*/
		des.text=desPostProcess(des.text);
		return des;
	}
	
	/**********************
	 * Class��Range�ı�����
	 ********************/
	public TextDes cnptRangeDes(OntClass c,DirectedGraph g)
	{
		OWLOntParse parse=new OWLOntParse();
		TextDes des=new TextDes();
		des.name=c.toString();
		des.text=new ArrayList();
		
		/*�õ���c��ΪRange��Property*/
		ArrayList pList=new ArrayList();
		for(Iterator it=curStmList.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			Resource r=st.getSubject();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			if (p.toString().equals("http://www.w3.org/2000/01/rdf-schema#range")
				&& o.toString().equals(c.toString())){
				OntProperty pt=m.getOntProperty(r.toString());
				if (pt!=null){
					pList.add(pt);
				}
			}
		}
		
		/*ȷ��ÿ��property��c��Զ���̶�*/
		/*�������ҵ�c�����и��࣬�����к������Range����
		 * ������Զ�ľ��Ǹ�Range��ԭʼ������*/
		/*c�ĸ���*/
		Set set=new HashSet();
		set.add(c);
		ArrayList fatherList=parse.listSuperClassOfConceptWithDistance(c,0,set);
		for (Iterator jt=pList.iterator();jt.hasNext();){
			/*����p--Range--c��ȷ��ԭʼ���������c�ľ���*/
			OntProperty pt=(OntProperty)jt.next();
			OntClass maxC=null;
			int maxLevel=0;
			for(Iterator kt=fatherList.iterator();kt.hasNext();){
				ArrayList lt=(ArrayList)kt.next();
				OntClass superC=(OntClass)lt.get(0);
				int level=((Integer)lt.get(1)).intValue();
				if (parse.listRangePropertyOfConcept(superC).contains(pt)){
					if (level>=maxLevel){
						maxLevel=level;
						maxC=superC;
					}
				}
			}
			
			/*����p��Description*/
			if (!pt.isAnon()){//������
				int pos=findPropPosInFullName(pt.toString());//λ��
				ArrayList lb=propBasicTextDes[pos].text;
				for (Iterator itx=lb.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=(1.0/(1.0+maxLevel));
					des.text.add(w);
				}
			}
			else{//����
				/*�����ͼ�жԸ������ڵ������*/
				Statement sx=ontParse.getAStatement(m,(Resource)pt,
						m.getProperty("http://www.w3.org/2000/01/rdf-schema#range"),c);
				Set setx=new HashSet();
				setx.add(sx);
				ArrayList anonList=anonDesInSubGraphBiDirect(pt,0,g,setx);
//				ArrayList anonList=anonDesInSubGraphSingleDirect(pt,0,g);
//				ArrayList anonList=anonDesInModel(pt,0);
				for (Iterator itx=anonList.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=(1.0/(1.0+maxLevel));
					des.text.add(w);
				}
			}
		}
		
		/*���ô���*/
		des.text=desPostProcess(des.text);
		return des;
	}
	
	/**********************
	 * ������ͼ�е�������ʵ�����ı�����
	 ********************/
	public ArrayList cnptInsTexDes(OntClass c,DirectedGraph g)
	{
		ArrayList result=new ArrayList();
		
		TextDes des=new TextDes();
		des.name=c.toString();
		des.text=new ArrayList();
		
		/*�ҵ���ͼ�е�ʵ��*/
		for(Iterator it=curStmList.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			Resource r=st.getSubject();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			if (p.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
				&& o.toString().equals(c.toString())){
				Individual nt=null;
				nt=m.getIndividual(r.toString());
				if (nt==null){
					nt=this.findInsInFullName(r.toString());
				}
				
				if (nt!=null){
					/*����i��Description*/
					if (!nt.isAnon()){//������
						int pos=findInsPosInFullName(nt.toString());//λ��
						/*ʵ���Ļ�������*/
						ArrayList lb=insBasicTextDes[pos].text;
						for (Iterator itx=lb.iterator();itx.hasNext();){
							Word w=(Word)((Word)itx.next()).clone();
							des.text.add(w);
						}
						/*ʵ������ͼ�е���������*/
						Set setx=new HashSet();
						setx.add(st);
						ArrayList insList=insDesInSubGraphBiDirect(nt,0,g,setx);
						for (Iterator itx=insList.iterator();itx.hasNext();){
							Word w=(Word)((Word)itx.next()).clone();
							des.text.add(w);
						}
					}
					else{//����
						/*�����ͼ�жԸ������ڵ������*/
						Set setx=new HashSet();
						setx.add(st);
						ArrayList anonList=anonDesInSubGraphBiDirect(nt,0,g,setx);
//						ArrayList anonList=anonDesInSubGraphSingleDirect(nt,0,g);
//						ArrayList anonList=anonDesInModel(nt,0);
						for (Iterator itx=anonList.iterator();itx.hasNext();){
							Word w=(Word)((Word)itx.next()).clone();
							des.text.add(w);
						}
					}
				}
			}
		}
		
		/*���ô���*/
		des.text=desPostProcess(des.text);
		
		result.add(des);
		return result;
	}
	
	/**********************
	 * ������ͼ�е��������Ԫ�ص��ı�����
	 ********************/
	public ArrayList cnptOtherTexDes(OntClass c,DirectedGraph g)
	{
		ArrayList result=new ArrayList();
		ArrayList checkedSet=new ArrayList();//Ԫ���Ƿ񱻴������־
        SplitWords spWord = new SplitWords();
        DelStopWords delSWrod= new DelStopWords();
        delSWrod.loadStopWords();
		
		/*������ͼ�е���Ԫ��*/
		for(Iterator it=curStmList.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			String localURI=null;
			int elmType=-1;
			Resource r=st.getSubject();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			
			/*����s*/
			if (r.isURIResource()){	localURI=r.getNameSpace();}
			if (!ontLngURI.contains(localURI) && !baseURI.equals(localURI)
					&& !checkedSet.contains(r.toString())) {// ����Ԫ�����Ԫ�أ�û�����
				checkedSet.add(r.toString());
				TextDes des=new TextDes();
				des.name=r.toString();
				des.text=new ArrayList();
				if (!r.isAnon()){//����������Դ
					//�ж�����
					elmType=getResourceType(r.toString());
					des.type=elmType;
					if (elmType==1){
						//��Class��
						int basicDespos=findCnptPosInFullName(r.toString());
						des.text=cnptBasicTextDes[basicDespos].text;
					}
					else if (elmType==2){
						//��Property��
						int basicDespos=findPropPosInFullName(r.toString());
						des.text=propBasicTextDes[basicDespos].text;
					}
					else if (elmType==3){
						//��Individual��
						int basicDespos=findInsPosInFullName(r.toString());
						des.text=insBasicTextDes[basicDespos].text;
					}
					else if (elmType==4){
						//����������value��Դ
						//��ʱ��������������Դ�������Ҫ����������Ļ�������
					}
				}
				else{
					//����������Դ
					Set setx=new HashSet();
//					setx.add(st);
//					ArrayList anonList=anonDesInSubGraphBiDirect(r,0,g,setx);
					ArrayList anonList=anonDesInSubGraphSingleDirect(r,0,g);
					for (Iterator itx=anonList.iterator();itx.hasNext();){
						Word w=(Word)((Word)itx.next()).clone();
						des.text.add(w);
					}
				}
				/*���ô���*/
				des.text=desPostProcess(des.text);
				if (!des.text.isEmpty()){
					result.add(des);
				}				
			}
			
			/*����p*/
			localURI=null;
			if (p.isURIResource()){localURI=p.getNameSpace();}			
			if (!ontLngURI.contains(localURI) && !baseURI.equals(localURI)
					&& !checkedSet.contains(p.toString())) {// ����Ԫ�����Ԫ�أ�û�����
				checkedSet.add(p.toString());
				TextDes des=new TextDes();
				des.name=p.toString();
				des.text=new ArrayList();
				if (!p.isAnon()){//����������Դ
					//�ж�����
					elmType=getResourceType(p.toString());
					des.type=elmType;
					if (elmType==1){
						//��Class��
						int basicDespos=findCnptPosInFullName(p.toString());
						if (basicDespos>=0){
							des.text=cnptBasicTextDes[basicDespos].text;
						}						
					}
					else if (elmType==2){
						//��Property��
						int basicDespos=findPropPosInFullName(p.toString());
						if (basicDespos>=0){
							des.text=propBasicTextDes[basicDespos].text;
						}						
					}
					else if (elmType==3){
						//��Individual��
						int basicDespos=findInsPosInFullName(p.toString());
						if (basicDespos>=0){
							des.text=insBasicTextDes[basicDespos].text;
						}						
					}
					else if (elmType==4){
						//����������value��Դ
						//��ʱ��������������Դ�������Ҫ����������Ļ�������
					}
				}
				else{
					//����������Դ
					Set setx=new HashSet();
//					setx.add(st);
//					ArrayList anonList=anonDesInSubGraphBiDirect(p,0,g,setx);
					ArrayList anonList=anonDesInSubGraphSingleDirect(p,0,g);
					for (Iterator itx=anonList.iterator();itx.hasNext();){
						Word w=(Word)((Word)itx.next()).clone();
						des.text.add(w);
					}
				}
				/*���ô���*/
				des.text=desPostProcess(des.text);	
				if (!des.text.isEmpty()){
					result.add(des);
				}	
			}
			
			/*����o*/
			localURI=null;
			if (o.isURIResource()){	localURI=o.asNode().getNameSpace(); }
			if (!ontLngURI.contains(localURI) && !baseURI.equals(localURI)
					&& !checkedSet.contains(o.toString())) {// ����Ԫ�����Ԫ�أ�û�����
				checkedSet.add(o.toString());
				TextDes des=new TextDes();
				des.name=o.toString();
				des.text=new ArrayList();
				if (!o.isAnon()){//����������Դ
					//�ж�����
					elmType=getResourceType(o.toString());
					des.type=elmType;
					if (elmType==1){
						//��Class��
						int basicDespos=findCnptPosInFullName(o.toString());
						if (basicDespos>=0){
							des.text=cnptBasicTextDes[basicDespos].text;
						}
						
					}
					else if (elmType==2){
						//��Property��
						int basicDespos=findPropPosInFullName(o.toString());
						if (basicDespos>=0){
							des.text=propBasicTextDes[basicDespos].text;
						}						
					}
					else if (elmType==3){
						//��Individual��
						int basicDespos=findInsPosInFullName(o.toString());
						if (basicDespos>=0){
							des.text=insBasicTextDes[basicDespos].text;
						}						
					}
					else if (elmType==4){
						String str=null;
						if (o.isLiteral()){
							str=o.asNode().getLiteralLexicalForm();
						}
						else {
							str=o.toString();
						}
						str=delSWrod.removeStopWords(str);
				        ArrayList list=spWord.split(str);
				        list=delSWrod.removeStopWords(list);
				        /*���Ȩ��*/
				        for (Iterator itx=list.iterator();itx.hasNext();){
				        	String stemp=(String)itx.next();
				        	Word w=new Word();
				        	w.content=stemp;
				        	w.weight =1.0;
				        	des.text.add(w);
				        }
					}
				}
				else{
					//����������Դ
					Set setx=new HashSet();
//					setx.add(st);
//					ArrayList anonList=anonDesInSubGraphBiDirect(m.getResource(o.toString()),0,g,setx);
					ArrayList anonList=anonDesInSubGraphSingleDirect(m.getResource(o.toString()),0,g);
					for (Iterator itx=anonList.iterator();itx.hasNext();){
						Word w=(Word)((Word)itx.next()).clone();
						des.text.add(w);
					}
				}
				/*���ô���*/
				des.text=desPostProcess(des.text);	
				if (!des.text.isEmpty()){
					result.add(des);
				}	
			}
		}		
		return result;
	}
	
	/**********************
	 * ������ͼ�е��������Ԫ�ص��ı�����
	 ********************/
	public ArrayList propOtherTexDes(OntProperty pr,DirectedGraph g)
	{
		ArrayList result=new ArrayList();
		ArrayList checkedSet=new ArrayList();//Ԫ���Ƿ񱻴������־
        SplitWords spWord = new SplitWords();
        DelStopWords delSWrod= new DelStopWords();
        delSWrod.loadStopWords();
		
		/*������ͼ�е���Ԫ��*/
		for(Iterator it=curStmList.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			String localURI=null;
			int elmType=-1;
			Resource r=st.getSubject();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			
			/*����s*/
			if (r.isURIResource()){localURI=r.getNameSpace();}
			if (!ontLngURI.contains(localURI) && !baseURI.equals(localURI)
					&& !checkedSet.contains(r.toString())) {// ����Ԫ�����Ԫ�أ�û�����
				checkedSet.add(r.toString());
				TextDes des=new TextDes();
				des.name=r.toString();
				des.text=new ArrayList();
				if (!r.isAnon()){//����������Դ
					//�ж�����
					elmType=getResourceType(r.toString());
					des.type=elmType;
					if (elmType==1){
						//��Class��
						int basicDespos=findCnptPosInFullName(r.toString());
						des.text=cnptBasicTextDes[basicDespos].text;
					}
					else if (elmType==2){
						//��Property��
						int basicDespos=findPropPosInFullName(r.toString());
						des.text=propBasicTextDes[basicDespos].text;
					}
					else if (elmType==3){
						//��Individual��
						int basicDespos=findInsPosInFullName(r.toString());
						des.text=insBasicTextDes[basicDespos].text;
					}
					else if (elmType==4){
						//����������value��Դ
						//��ʱ��������������Դ�������Ҫ����������Ļ�������
					}
				}
				else{
					//����������Դ
					Set setx=new HashSet();
//					setx.add(st);
//					ArrayList anonList=anonDesInSubGraphBiDirect(r,0,g,setx);
					ArrayList anonList=anonDesInSubGraphSingleDirect(r,0,g);
					for (Iterator itx=anonList.iterator();itx.hasNext();){
						Word w=(Word)((Word)itx.next()).clone();
						des.text.add(w);
					}
				}
				/*���ô���*/
				des.text=desPostProcess(des.text);		
				if (!des.text.isEmpty()){
					result.add(des);
				}	
			}
			
			/*����p*/
			localURI=null;
			if (p.isURIResource()){localURI=p.getNameSpace();}
			if (!ontLngURI.contains(localURI) && !baseURI.equals(localURI)
					&& !checkedSet.contains(p.toString())) {// ����Ԫ�����Ԫ�أ�û�����
				checkedSet.add(p.toString());
				TextDes des=new TextDes();
				des.name=p.toString();
				des.text=new ArrayList();
				if (!p.isAnon()){//����������Դ
					//�ж�����
					elmType=getResourceType(p.toString());
					des.type=elmType;
					if (elmType==1){
						//��Class��
						int basicDespos=findCnptPosInFullName(p.toString());
						if (basicDespos>=0){
							des.text=cnptBasicTextDes[basicDespos].text;							
						}						
					}
					else if (elmType==2){
						//��Property��
						int basicDespos=findPropPosInFullName(p.toString());
						if (basicDespos>=0){
							des.text=propBasicTextDes[basicDespos].text;
						}						
					}
					else if (elmType==3){
						//��Individual��
						int basicDespos=findInsPosInFullName(p.toString());
						if (basicDespos>=0){
							des.text=insBasicTextDes[basicDespos].text;
						}						
					}
					else if (elmType==4){
						//����������value��Դ
						//��ʱ��������������Դ�������Ҫ����������Ļ�������
					}
				}
				else{
					//����������Դ
					Set setx=new HashSet();
//					setx.add(st);
//					ArrayList anonList=anonDesInSubGraphBiDirect(p,0,g,setx);
					ArrayList anonList=anonDesInSubGraphSingleDirect(p,0,g);
					for (Iterator itx=anonList.iterator();itx.hasNext();){
						Word w=(Word)((Word)itx.next()).clone();
						des.text.add(w);
					}
				}
				/*���ô���*/
				des.text=desPostProcess(des.text);	
				if (!des.text.isEmpty()){
					result.add(des);
				}	
			}
			
			/*����o*/
			localURI=null;
			if (o.isURIResource()){localURI=o.asNode().getNameSpace();}			
			if (!ontLngURI.contains(localURI) && !baseURI.equals(localURI)
					&& !checkedSet.contains(o.toString())) {// ����Ԫ�����Ԫ�أ�û�����
				checkedSet.add(o.toString());
				TextDes des=new TextDes();
				des.name=o.toString();
				des.text=new ArrayList();
				if (!o.isAnon()){//����������Դ
					//�ж�����
					elmType=getResourceType(o.toString());
					des.type=elmType;
					if (elmType==1){
						//��Class��
						int basicDespos=findCnptPosInFullName(o.toString());
						if (basicDespos>=0){
							des.text=cnptBasicTextDes[basicDespos].text;
						}						
					}
					else if (elmType==2){
						//��Property��
						int basicDespos=findPropPosInFullName(o.toString());
						if (basicDespos>=0){
							des.text=propBasicTextDes[basicDespos].text;
						}						
					}
					else if (elmType==3){
						//��Individual��
						int basicDespos=findInsPosInFullName(o.toString());
						if (basicDespos>=0){
							des.text=insBasicTextDes[basicDespos].text;
						}						
					}
					else if (elmType==4){
						String str=null;
						if (o.isLiteral()){
							str=o.asNode().getLiteralLexicalForm();
						}
						else {
							str=o.toString();
						}
						str=delSWrod.removeStopWords(str);
				        ArrayList list=spWord.split(str);
				        list=delSWrod.removeStopWords(list);
				        /*���Ȩ��*/
				        for (Iterator itx=list.iterator();itx.hasNext();){
				        	String stemp=(String)itx.next();
				        	Word w=new Word();
				        	w.content=stemp;
				        	w.weight =1.0;
				        	des.text.add(w);
				        }
					}
				}
				else{
					//����������Դ
					Set setx=new HashSet();
//					setx.add(st);
//					ArrayList anonList=anonDesInSubGraphBiDirect(m.getResource(o.toString()),0,g,setx);
					ArrayList anonList=anonDesInSubGraphSingleDirect(m.getResource(o.toString()),0,g);
					for (Iterator itx=anonList.iterator();itx.hasNext();){
						Word w=(Word)((Word)itx.next()).clone();
						des.text.add(w);
					}
				}
				/*���ô���*/
				des.text=desPostProcess(des.text);	
				if (!des.text.isEmpty()){
					result.add(des);
				}	
			}
		}		
		return result;
	}
	
	/**********************
	 * ������ͼ�е����Բ���ı�����
	 ********************/
	public ArrayList propHrcTextDes(OntProperty p,DirectedGraph g)
	{
		ArrayList result=new ArrayList();
		
		/*subProperty�ı�����*/
		TextDes subDes=new TextDes();
		subDes=propSubPropertyDes(p,g);
		result.add(0,subDes);
				
		/*superProperty�ı�����*/
		TextDes superDes=new TextDes();
		superDes=propSuperPropertyDes(p,g);
		result.add(1,superDes);
		
		/*sibling property�ı�����*/
		TextDes siblingDes=new TextDes();
		siblingDes=propSiblingPropertyDes(p,g);
		result.add(2,siblingDes);
		
		return result;
	}
	
	/**********************
	 * subProperty�ı�����
	 ********************/
	public TextDes propSubPropertyDes(OntProperty p,DirectedGraph g)
	{
		OWLOntParse parse=new OWLOntParse();
		TextDes subDes=new TextDes();
		subDes.name=p.toString();
		subDes.text=new ArrayList();
		
		/*�õ�ȫ��subProperty*/
		ArrayList subPList=parse.listSubPropertyOfPropertyWithDistance(p,0);
		for(Iterator it=subPList.iterator();it.hasNext();){
			ArrayList lt=(ArrayList)it.next();
			OntProperty subP=(OntProperty)lt.get(0);
			int level=((Integer)lt.get(1)).intValue();
			
			/*�жϵ�ǰ��subProperty�Ƿ��������ͼ��
			 * �ϸ���ж�Ӧ�ÿ�����������Ԫ�飬����ֻ�򵥿��ǽڵ��Ƿ񱻰���
			 */
			if (!g.containsVertex(parse.getResourceLocalName(subP))){
				continue;
			}
			
			/*��Ӹ�subProperty��Description*/
			/*���ж��Ƿ�����������*/
			if (!subP.isAnon()){//������
				int pos=findPropPosInFullName(subP.toString());//λ��
				ArrayList lb=propBasicTextDes[pos].text;
				for (Iterator itx=lb.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=(1-subPropertyDec*level);
					subDes.text.add(w);
				}
			}
			else{//����
				/*�����ͼ�жԸ������ڵ������*/
				Statement sx=ontParse.getAStatement(m,(Resource)subP,
						m.getProperty("http://www.w3.org/2000/01/rdf-schema#subPropertyOf"),p);
				Set setx=new HashSet();
				setx.add(sx);
				ArrayList anonList=anonDesInSubGraphBiDirect(subP,0,g,setx);
//				ArrayList anonList=anonDesInSubGraphSingleDirect(subP,0,g);
//				ArrayList anonList=anonDesInModel(p,0);
				for (Iterator itx=anonList.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=(1-subPropertyDec*level);
					subDes.text.add(w);
				}
			}
		}
		
		/*���ô���*/
		subDes.text=desPostProcess(subDes.text);
		
		return subDes;
	}
	
	/**********************
	 * superProperty�ı�����
	 ********************/
	public TextDes propSuperPropertyDes(OntProperty p,DirectedGraph g)
	{
		OWLOntParse parse=new OWLOntParse();
		TextDes superDes=new TextDes();
		superDes.name=p.toString();
		superDes.text=new ArrayList();
		
		/*�õ�ȫ��superProperty*/
		ArrayList superPList=parse.listSuperPropertyOfPropertyWithDistance(p,0);
		for(Iterator it=superPList.iterator();it.hasNext();){
			ArrayList lt=(ArrayList)it.next();
			OntProperty superP=(OntProperty)lt.get(0);
			int level=((Integer)lt.get(1)).intValue();
			
			/*�жϵ�ǰ��superProperty�Ƿ��������ͼ��
			 * �ϸ���ж�Ӧ�ÿ�����������Ԫ�飬����ֻ�򵥿��ǽڵ��Ƿ񱻰���
			 */
			if (!g.containsVertex(parse.getResourceLocalName(superP))){
				continue;
			}
			
			/*��Ӹ�superProperty��Description*/
			/*���ж��Ƿ�����������*/
			if (!superP.isAnon()){//������
				int pos=findPropPosInFullName(superP.toString());//λ��
				ArrayList lb=propBasicTextDes[pos].text;
				for (Iterator itx=lb.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=(1-superPropertyDec*level);
					superDes.text.add(w);
				}
			}
			else{//����
				/*�����ͼ�жԸ������ڵ������*/
				Statement sx=ontParse.getAStatement(m,(Resource)p,
						m.getProperty("http://www.w3.org/2000/01/rdf-schema#subPropertyOf"),superP);
				Set setx=new HashSet();
				setx.add(sx);
				ArrayList anonList=anonDesInSubGraphBiDirect(superP,0,g,setx);
//				ArrayList anonList=anonDesInSubGraphSingleDirect(superP,0,g);
//				ArrayList anonList=anonDesInModel(p,0);
				for (Iterator itx=anonList.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=(1-superPropertyDec*level);
					superDes.text.add(w);
				}
			}
		}
		
		/*���ô���*/
		superDes.text=desPostProcess(superDes.text);
		
		return superDes;
	}
	
	/**********************
	 * sibling Property�ı�����
	 ********************/
	public TextDes propSiblingPropertyDes(OntProperty p,DirectedGraph g)
	{
		OWLOntParse parse=new OWLOntParse();
		TextDes des=new TextDes();
		des.name=p.toString();
		des.text=new ArrayList();
		/*�õ�ȫ��sibling Property*/
		ArrayList subCList=parse.listSiblingsOfProperty(p);
		for(Iterator it=subCList.iterator();it.hasNext();){
			OntProperty sibP=(OntProperty)it.next();
			
			/*�жϵ�ǰ��siblingProperty�Ƿ��������ͼ��
			 * �ϸ���ж�Ӧ�ÿ�����������Ԫ�飬����ֻ�򵥿��ǽڵ��Ƿ񱻰���
			 */
			if (!g.containsVertex(parse.getResourceLocalName(sibP))){
				continue;
			}
			
			/*��Ӹ�siblingProperty��Description*/
			/*���ж��Ƿ�����������*/
			if (!sibP.isAnon()){//������
				int pos=findPropPosInFullName(sibP.toString());//λ��
				ArrayList lb=propBasicTextDes[pos].text;
				for (Iterator itx=lb.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=wsiblingPropertyDec;
					des.text.add(w);
				}
			}
			else{//����
				/*�����ͼ�жԸ������ڵ������*/
//				ArrayList anonList=anonDesInSubGraph(p,0);
				ArrayList anonList=anonDesInModel(p,0);
				for (Iterator itx=anonList.iterator();itx.hasNext();){
					Word w=(Word)((Word)itx.next()).clone();
					w.weight*=wsiblingPropertyDec;
					des.text.add(w);
				}
			}
		}
		
		/*���ô���*/
		des.text=desPostProcess(des.text);
		
		return des;
	}
	
	/**********************
	 * ������ͼ�е����������Ե��ı�����
	 ********************/
	public ArrayList propFunctionTextDes(OntProperty p,DirectedGraph g)
	{
		ArrayList result=new ArrayList();
		
		/*Property��Domain�ı�����*/
		TextDes domainDes=new TextDes();
		domainDes=propDomainDes(p,g);
		result.add(domainDes);
		
		/*Property��Range�ı�����*/
		TextDes rangeDes=new TextDes();
		rangeDes=propRangeDes(p,g);
		result.add(rangeDes);
		
		/*Property�������ı�����*/
		TextDes CharDes=new TextDes();
		CharDes=propCharDes(p);
		result.add(CharDes);
		
		return result;
	}
	
	/**********************
	 * Property��Domain�ı�����
	 ********************/
	public TextDes propDomainDes(OntProperty p,DirectedGraph g)
	{
		OWLOntParse parse=new OWLOntParse();
		TextDes des=new TextDes();
		des.name=p.toString();
		des.text=new ArrayList();
		
		/*�õ�p��Domain*/
		ArrayList dList=new ArrayList();
		for(Iterator it=curStmList.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
//			System.out.println(st.toString());
			Resource s=st.getSubject();
			Property pr=st.getPredicate();
			RDFNode o=st.getObject();
			if (pr.toString().equals("http://www.w3.org/2000/01/rdf-schema#domain")
				&& s.toString().equals(p.toString())){
				Resource ot=m.getOntResource(m.getResource(o.toString()));
				String oBaseURI=ontParse.getResourceBaseURI(o.toString());
				if (ot!=null && !dList.contains(ot) && !ontParse.metaURISet.contains(oBaseURI)){
					dList.add(ot);
				}
			}
		}
		
		for (Iterator jt=dList.iterator();jt.hasNext();){
			RDFNode obj=(RDFNode)jt.next();
			
			/*�ж�o�ĳɷ�*/
			int t=getResourceType(obj.toString());
			if (t==1){//Class
				OntClass ctemp=null;
				if(!ontParse.isBlankNode(obj.toString())){
					ctemp=m.getOntClass(obj.toString());
				}
				else{
					ctemp=(OntClass)(getAnonResourceWithType(obj.toString()).get(0));
				}
				
				/*c�ĸ���*/
				Set set=new HashSet();
				set.add(ctemp);
				ArrayList fatherList=parse.listSuperClassOfConceptWithDistance(ctemp,0,set);
				int maxLevel=0;
				for(Iterator kt=fatherList.iterator();kt.hasNext();){
					ArrayList lt=(ArrayList)kt.next();
					OntClass superC=(OntClass)lt.get(0);
					int level=((Integer)lt.get(1)).intValue();
					if (parse.listDomainPropertyOfConcept(superC,true).contains(p)){
						if (level>=maxLevel){
							maxLevel=level;
						}
					}
				}
				
				/*����c��Description*/
				if (!ctemp.isAnon()){//������
					int pos=findCnptPosInFullName(ctemp.toString());//λ��
					if(pos==-1){
						System.out.println(ctemp.toString());
					}
					ArrayList lb=cnptBasicTextDes[pos].text;
					for (Iterator itx=lb.iterator();itx.hasNext();){
						Word w=(Word)((Word)itx.next()).clone();
						w.weight*=(1.0/(1.0+maxLevel));
						des.text.add(w);
					}
				}
				else{//����
					/*�����ͼ�жԸ������ڵ������*/
					Statement sx=ontParse.getAStatement(m,(Resource)p,
							m.getProperty("http://www.w3.org/2000/01/rdf-schema#domain"),ctemp);
					Set setx=new HashSet();
					setx.add(sx);
					ArrayList anonList=anonDesInSubGraphBiDirect(ctemp,0,g,setx);
//					ArrayList anonList=anonDesInSubGraphSingleDirect(ctemp,0,g);
//					ArrayList anonList=anonDesInModel(ctemp,0);
					for (Iterator itx=anonList.iterator();itx.hasNext();){
						Word w=(Word)((Word)itx.next()).clone();
						w.weight*=(1.0/(1.0+maxLevel));
						des.text.add(w);
					}
				}
			}
			else if (t==4){//Other resource
				/*ֱ����ȡlocal name*/
				Word w=new Word();
				w.content=obj.asNode().getLocalName();
				w.weight=1.0;
				des.text.add(w);
			}
		}
		
		/*���ô���*/
		des.text=desPostProcess(des.text);
		
		return des;
	}
	
	/**********************
	 * Property��Range�ı�����
	 ********************/
	public TextDes propRangeDes(OntProperty p,DirectedGraph g)
	{
		OWLOntParse parse=new OWLOntParse();
		TextDes des=new TextDes();
		des.name=p.toString();
		des.text=new ArrayList();
		
		/*�õ�p��Range*/
		ArrayList dList=new ArrayList();
		for(Iterator it=curStmList.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			Resource s=st.getSubject();
			Property pr=st.getPredicate();
			RDFNode o=st.getObject();
			if (pr.toString().equals("http://www.w3.org/2000/01/rdf-schema#range")
				&& s.toString().equals(p.toString())){
				Resource ot=m.getOntResource(m.getResource(o.toString()));
				String oBaseURI=ontParse.getResourceBaseURI(o.toString());
				if (ot!=null && !dList.contains(ot)&& !ontParse.metaURISet.contains(oBaseURI)){
					dList.add(ot);
				}
			}
		}
		
		for (Iterator jt=dList.iterator();jt.hasNext();){
			RDFNode obj=(RDFNode)jt.next();
			
			/*�ж�o�ĳɷ�*/
			int t=getResourceType(obj.toString());
			if (t==1){//Class
				OntClass ctemp=null;
				ctemp=m.getOntClass(obj.toString());
				if (ctemp==null){
					try{
						ctemp=m.getOntResource(m.getResource(obj.asNode().toString())).asClass();
					}
					catch(ConversionException e){
						continue;
					}
					
				}
				/*c�ĸ���*/
				Set set=new HashSet();
				set.add(ctemp);
				ArrayList fatherList=parse.listSuperClassOfConceptWithDistance(ctemp,0,set);
				int maxLevel=0;
				for(Iterator kt=fatherList.iterator();kt.hasNext();){
					ArrayList lt=(ArrayList)kt.next();
					OntClass superC=(OntClass)lt.get(0);
					int level=((Integer)lt.get(1)).intValue();
					if (parse.listDomainPropertyOfConcept(superC,true).contains(p)){
						if (level>=maxLevel){
							maxLevel=level;
						}
					}
				}
				
				/*����c��Description*/
				if (!ctemp.isAnon()){//������
					int pos=findCnptPosInFullName(ctemp.toString());//λ��					
					if (pos!=-1){
						ArrayList lb=cnptBasicTextDes[pos].text;
						for (Iterator itx=lb.iterator();itx.hasNext();){
							Word w=(Word)((Word)itx.next()).clone();
							w.weight*=(1.0/(1.0+maxLevel));
							des.text.add(w);
						}
					}
					else{//�������оٳ�������
						/*ֱ����ȡlocal name*/
						Word w=new Word();
						w.content=ctemp.getLocalName();
						w.weight=1.0;
						des.text.add(w);
					}

				}
				else{//����
					/*�����ͼ�жԸ������ڵ������*/
					Statement sx=ontParse.getAStatement(m,(Resource)p,
							m.getProperty("http://www.w3.org/2000/01/rdf-schema#range"),ctemp);
					Set setx=new HashSet();
					setx.add(sx);
					ArrayList anonList=anonDesInSubGraphBiDirect(ctemp,0,g,setx);
//					ArrayList anonList=anonDesInSubGraphSingleDirect(ctemp,0,g);
//					ArrayList anonList=anonDesInModel(ctemp,0);
					for (Iterator itx=anonList.iterator();itx.hasNext();){
						Word w=(Word)((Word)itx.next()).clone();
						w.weight*=(1.0/(1.0+maxLevel));
						des.text.add(w);
					}
				}
			}
			else if (t==4){//Other resource
				/*ֱ����ȡlocal name*/
				Word w=new Word();
				w.content=obj.asNode().getLocalName();
				w.weight=1.0;
				des.text.add(w);
			}
		}
		
		/*���ô���*/
		des.text=desPostProcess(des.text);
		
		return des;
	}
	
	/**********************
	 * Property��Restriction�ı�����
	 * û��ʹ��
	 ********************/
	public TextDes propRestrDes(OntProperty p,DirectedGraph g)
	{
		TextDes des=new TextDes();
		des.name=p.toString();
		des.text=new ArrayList();

		return des;
	}
	
	
	/**********************
	 * Property�������ı�����
	 * ����һ�������ռ����ʽ
	 ********************/
	public TextDes propCharDes(OntProperty p)
	{
		TextDes des=new TextDes();
		des.name=p.toString();
		des.text=new ArrayList();
		
		/*DatatypeProperty*/
		Word w1=new Word();
		w1.content="DatatypeProperty";
		if (p.isDatatypeProperty()){
			w1.weight=1.0;
		}
		des.text.add(0,w1);
		/*ObjectProperty*/
		Word w2=new Word();
		w2.content="ObjectProperty";
		if (p.isObjectProperty()){
			w2.weight=1.0;
		}
		des.text.add(1,w2);
		/*AnnotationProperty*/
		Word w3=new Word();
		w3.content="AnnotationProperty";
		if (p.isAnnotationProperty()){
			w3.weight=1.0;
		}
		des.text.add(2,w3);
		/*FunctionalProperty*/
		Word w4=new Word();
		w4.content="FunctionalProperty";
		if (p.isFunctionalProperty()){
			w4.weight=1.0;
		}
		des.text.add(3,w4);
		/*InverseFunctionalProperty*/
		Word w5=new Word();
		w5.content="InverseFunctionalProperty";
		if (p.isInverseFunctionalProperty()){
			w5.weight=1.0;
		}
		des.text.add(4,w5);
		/*SymmetricProperty*/
		Word w6=new Word();
		w6.content="SymmetricProperty";
		if (p.isSymmetricProperty()){
			w6.weight=1.0;
		}
		des.text.add(5,w6);
		/*TransitiveProperty*/
		Word w7=new Word();
		w7.content="TransitiveProperty";
		if (p.isTransitiveProperty()){
			w7.weight=1.0;
		}
		des.text.add(6,w7);
		
		/*�����ǹ����ı�����*/
		des.text.clear();
		
		return des;
	}
	
	/**********************
	 * ������ͼ�е����Ե����ʵ�����ı�����
	 ********************/
	public ArrayList propInsTexDes(OntProperty p,DirectedGraph g)
	{
		ArrayList result=new ArrayList();
		
		TextDes domainDes=new TextDes();
		domainDes.name=p.toString();
		domainDes.text=new ArrayList();
		
		TextDes rangeDes=new TextDes();
		rangeDes.name=p.toString();
		rangeDes.text=new ArrayList();
		
		/*�ҵ���ͼ��p��Ӧ��ʵ��*/
		for(Iterator it=curStmList.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			Resource rSub=st.getSubject();
			Property rPre=st.getPredicate();
			RDFNode rObj=st.getObject();
			int subType=getResourceType(rSub.toString());
			int objType=getResourceType(rObj.toString());
			if (rPre.toString().equals(p.toString())
				&& (subType==3)
				&& (objType==3 || objType==4)){
				
				/*Domainʵ����Description*/
				Individual idomain=null;
				idomain=m.getIndividual(rSub.toString());
				if (idomain==null){
					idomain=this.findInsInFullName(rSub.toString());
				}
				if (idomain!=null){
					/*����i��Description*/
					if (!idomain.isAnon()){//������
						int pos=findInsPosInFullName(idomain.toString());//λ��
						ArrayList lb=insBasicTextDes[pos].text;
						for (Iterator itx=lb.iterator();itx.hasNext();){
							Word w=(Word)((Word)itx.next()).clone();
							domainDes.text.add(w);
						}
						/*ʵ������ͼ�е���������*/
						Set setx=new HashSet();
						setx.add(st);
						ArrayList insList=insDesInSubGraphBiDirect(idomain,0,g,setx);
						for (Iterator itx=insList.iterator();itx.hasNext();){
							Word w=(Word)((Word)itx.next()).clone();
							domainDes.text.add(w);
						}
					}
					else{//����
						/*�����ͼ�жԸ������ڵ������*/
						Set setx=new HashSet();
						setx.add(st);
						ArrayList anonList=anonDesInSubGraphBiDirect(idomain,0,g,setx);
//						ArrayList anonList=anonDesInSubGraphSingleDirect(idomain,0,g);
//						ArrayList anonList=anonDesInModel(idomain,0);
						for (Iterator itx=anonList.iterator();itx.hasNext();){
							Word w=(Word)((Word)itx.next()).clone();
							domainDes.text.add(w);
						}
					}
				}
				
				/*Rangeʵ����Description*/
				if (objType==3){
					Individual iRange=null;
					iRange=m.getIndividual(rObj.toString());
					if (iRange==null){
						iRange=this.findInsInFullName(rObj.toString());
					}
					
					if (iRange!=null){
						/*����i��Description*/
						if (!iRange.isAnon()){//������
							int pos=findInsPosInFullName(iRange.toString());//λ��
							ArrayList lb=insBasicTextDes[pos].text;
							for (Iterator itx=lb.iterator();itx.hasNext();){
								Word w=(Word)((Word)itx.next()).clone();
								rangeDes.text.add(w);
							}
							/*ʵ������ͼ�е���������*/
							Set setx=new HashSet();
							setx.add(st);
							ArrayList insList=insDesInSubGraphBiDirect(iRange,0,g,setx);
							for (Iterator itx=insList.iterator();itx.hasNext();){
								Word w=(Word)((Word)itx.next()).clone();
								rangeDes.text.add(w);
							}
						}
						else{//����
							/*�����ͼ�жԸ������ڵ������*/
							Set setx=new HashSet();
							setx.add(st);
							ArrayList anonList=anonDesInSubGraphBiDirect(iRange,0,g,setx);
//							ArrayList anonList=anonDesInSubGraph(iRange,0);
//							ArrayList anonList=anonDesInModel(iRange,0);
							for (Iterator itx=anonList.iterator();itx.hasNext();){
								Word w=(Word)((Word)itx.next()).clone();
								rangeDes.text.add(w);
							}
						}
					}
				}
				else{
					Resource rt=m.getResource(rObj.toString());
					if (rt!=null){
						if (rt.isAnon()){//����
							Set setx=new HashSet();
							setx.add(st);
							ArrayList anonList=anonDesInSubGraphBiDirect(rt,0,g,setx);
//							ArrayList anonList=anonDesInSubGraph(rt,0);
//							ArrayList anonList=anonDesInModel(rt,0);
							for (Iterator itx=anonList.iterator();itx.hasNext();){
								Word w=(Word)((Word)itx.next()).clone();
								rangeDes.text.add(w);
							}
						}
						else{//������
							String localname=null;
							//ͨ��getPropertyValue()���������ֵ
//							if (rt.toString().contains("gMonth")){
//								System.out.println("stop");
//							}
							localname=idomain.getPropertyValue(p).asNode().getLiteralLexicalForm().toString();
							if (localname.equals("")){
								localname=rt.toString();
							}
							//����ֵ��һ������
							localname=processPVaule(localname);
//							if (rt.asNode().isLiteral()){
//								Literal l=(Literal)rt;
//								localname=l.getValue().toString(); 
//							}
//							else if (rt.asNode().isURI()){
//								localname=rt.asNode().getLocalName(); 
//							}
//							else {
//								localname=rt.toString();
//							}
//							if (localname.equals("")){
//								localname=rt.toString();
//							}

							Word w=new Word();
							w.content=localname;
							w.weight=1.0;
							rangeDes.text.add(w);
						}
					}
				}
			}
		}
		
		/*���ô���*/
		domainDes.text=desPostProcess(domainDes.text);
		rangeDes.text=desPostProcess(rangeDes.text);
		
		result.add(domainDes);
		result.add(rangeDes);
		return result;
	}
	
	private String processPVaule(String name) {
		//�ж��Ƿ����^^
		String newName=null;
		newName=name;
		int pos=-1;
		pos=name.indexOf("^^");
		if (pos!=-1){
			newName=name.substring(0,pos);
		}
		return newName;
	}

	/*************
	 * �������ʵ�Ԥ����
	 * ���ܻᱻ���Ϊ����ʣ�������List��¼���
	 *************/
    public ArrayList simpleStringPreProcess(String s)
	{
    	ArrayList list = new ArrayList();
    	
    	SplitWords spWord = new SplitWords();
		DelStopWords delSWrod = new DelStopWords();
		delSWrod.loadStopWords();
		
		/* �ı�Ԥ���� */
		s = delSWrod.removeStopWords(s);
		list = spWord.split(s);
		list=delSWrod.removeStopWords(list);
		
		return list;
	}
	
	/**********************
	 * �����ĺ�����
	 * ��һ���ǽ�������������ͬ���ı����кϲ�
	 ********************/
    public ArrayList desPostProcess(ArrayList rawList)
	{
    	if (rawList==null){//����Ϊ�յ����
    		return null;
    	}
    	
    	ArrayList newList = new ArrayList();
    	Set termSet=new HashSet();
    	for(Iterator it=rawList.iterator();it.hasNext();){
    		Word w=new Word();
    		w=(Word)it.next();
    		String content=w.content;
    		double weight=w.weight;
    		/*��ǰ���Ƿ��Ѿ�����*/
    		if (termSet.contains(content)){//����
    			/*�ҵ���ǰ�ʵ�λ��*/
    			int pos=0;
    			Word wt=new Word();
    			for(Iterator jt=newList.iterator();jt.hasNext();){
    				wt=(Word)jt.next();
    	    		String ct=wt.content;
    	    		if (ct.equals(content)){
    	    			pos=newList.indexOf(wt);
    	    			wt.weight+=weight;
    	    			break;
    	    		}
    			}
//    			wt.weight+=weight;
    			
//    			newList.set(pos,wt);
    		}
    		else{//������
    			termSet.add(content);
    			newList.add(w.clone());
    		}
    	}
    	return newList;
	}
    
	public ArrayList getAnonResourceWithType(String name)
	{
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
	
	/**********************
	 * �ݹ���ʵ���ڵ�ǰ��ͼ�е�����
	 * ͬʱ����blank node��ͷ�ͽ�β����Ԫ��
	 ********************/
	public ArrayList insDesInSubGraphBiDirect(Resource b,int level,DirectedGraph g, Set visitedSet)
	{
		ArrayList result=new ArrayList();
		
		/*����b��ͷ����Ԫ��*/
		for(Iterator it=curStmList.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			Resource s=st.getSubject();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			
			//�Ƿ��Ѿ����ʹ�
			if (visitedSet.contains(st)){
				continue;//��������Ԫ��
			}
			
			//�ж���b��ͷ
			if (s.toString().equals(b.toString())){
		   		
		   		//������ʹ��б�
		   		visitedSet.add(st);
		   		
		   		if (p.isAnon()){//pΪ���������������
					result=null;
				}
				
				/*p��Description*/
				ArrayList lp=new ArrayList();
				/*�ж�p�ǲ���Ԫ����*/
				if (!isMetaOntData(p)){
					int pPos=findPropPosInFullName(p.toString());
					if (pPos!=-1){
						lp=propBasicTextDes[pPos].text;
					}
					else{
						/*ֱ����ȡlocal name*/
						String localname=null;
						localname=p.getLocalName(); 

						ArrayList otherList=simpleStringPreProcess(localname);
				        for (Iterator xt=otherList.iterator();xt.hasNext();){
				        	String stemp=(String)xt.next();
				        	Word w=new Word();
				        	w.content=stemp;
				        	w.weight =1.0;
				        	lp.add(w);
				        }
					}
				}
				
				if (lp!=null){
					result.addAll(lp);
				}
				
				/*o��Description*/
				ArrayList lo=new ArrayList();
				/*�ݹ���ֹ����*/
				if (!ontParse.isBlankNode(o.toString())){
					/*����Ԫ��*/
					if (!isMetaOntData(m.getResource(o.toString()))){
						/*�ж�o�ĳɷ�*/
						int t=getResourceType(o.toString());
						if (t==1){//Class
							//�������������
							int tempPos=findCnptPosInFullName(o.toString());
							lo=cnptBasicTextDes[tempPos].text;
						}
						else if (t==2){//Property
							//�������Ҳ������
							int tempPos=findPropPosInFullName(o.toString());
							/*�õ�Description*/
							lo=propBasicTextDes[tempPos].text;
						}
						else if (t==3){//Instance
							//oҲ��ʵ�������
							int tempPos=findInsPosInFullName(o.toString());
							/*�õ�Description*/
//							if (tempPos==-1){
//								System.out.println("stop");
//							}
							lo=insBasicTextDes[tempPos].text;
						}
						else if (t==4){//Other resource
							/*ֱ����ȡlocal name*/
							String localname=null;
							if (o.isLiteral()){
								Literal l=(Literal)o;
								localname=l.getLexicalForm().toString(); 
							}
							else{
								localname=o.asNode().getLocalName(); 
							}
							if (localname.equals("")){
								localname=o.toString();
							}
							ArrayList otherList=simpleStringPreProcess(localname);
					        for (Iterator xt=otherList.iterator();xt.hasNext();){
					        	String stemp=(String)xt.next();
					        	Word w=new Word();
					        	w.content=stemp;
					        	w.weight =1.0;
					        	lo.add(w);
					        }
						}
						/*return��ֹ����*/
						result.addAll(lo);
					}				
				}
				else{
					/*�ݹ�*/
					lo=insDesInSubGraphBiDirect(m.getResource(o.toString()),level+1,g,visitedSet);
					
					/*��˥��ϵ��*/
					double downPare=Math.pow(wAnonNode,((double)level+1.0));
					for (Iterator itx=lo.iterator();itx.hasNext();){
						Word w=(Word)itx.next();
						w.weight*=downPare;
					}
					result.addAll(lo);
				}
			}
		}
		
		/*����b��β����Ԫ��*/
		for(Iterator it=curStmList.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			Resource s=st.getSubject();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			
			//�Ƿ��Ѿ����ʹ�
			if (visitedSet.contains(st)){
				continue;//��������Ԫ��
			}
			
			//�ж���b��β
			if (o.asNode().toString().equals(b.toString())){
		   		
		   		//������ʹ��б�
		   		visitedSet.add(st);
		   		
		   		if (p.isAnon()){//pΪ���������������
					result=null;
				}
				
				/*p��Description*/
				ArrayList lp=new ArrayList();
				/*�ж�p�ǲ���Ԫ����*/
				if (!isMetaOntData(p)){
					int pPos=findPropPosInFullName(p.toString());
					if (pPos!=-1){
						lp=propBasicTextDes[pPos].text;
					}
					else{
						/*ֱ����ȡlocal name*/
						String localname=null;
						localname=p.getLocalName(); 

						ArrayList otherList=simpleStringPreProcess(localname);
				        for (Iterator xt=otherList.iterator();xt.hasNext();){
				        	String stemp=(String)xt.next();
				        	Word w=new Word();
				        	w.content=stemp;
				        	w.weight =1.0;
				        	lp.add(w);
				        }
					}
				}
				
				if (lp!=null){
					result.addAll(lp);
				}
				
				/*s��Description*/
				ArrayList lo=new ArrayList();
				/*�ݹ���ֹ����*/
				if (!ontParse.isBlankNode(s.toString())){
					/*����Ԫ��*/
					if (!isMetaOntData(m.getResource(s.toString()))){
						/*�ж�s�ĳɷ�*/
						int t=getResourceType(s.toString());
						if (t==1){//Class
							int tempPos=findCnptPosInFullName(s.toString());
							lo=cnptBasicTextDes[tempPos].text;
						}
						else if (t==2){//Property
							int tempPos=findPropPosInFullName(s.toString());
							/*�õ�Description*/
							lo=propBasicTextDes[tempPos].text;
						}
						else if (t==3){//Instance
							int tempPos=findInsPosInFullName(s.toString());
							/*�õ�Description*/
//							if (tempPos==-1){
//								System.out.println("stop");
//							}
							lo=insBasicTextDes[tempPos].text;
						}
						else if (t==4){//Other resource
							/*ֱ����ȡlocal name*/
							String localname=null;
							if (s.isLiteral()){
								Literal l=(Literal)s;
								localname=l.getValue().toString(); 
							}
							else{
								localname=s.asNode().getLocalName(); 
							}
							if (localname.equals("")){
								localname=s.toString();
							}
							ArrayList otherList=simpleStringPreProcess(localname);
					        for (Iterator xt=otherList.iterator();xt.hasNext();){
					        	String stemp=(String)xt.next();
					        	Word w=new Word();
					        	w.content=stemp;
					        	w.weight =1.0;
					        	lo.add(w);
					        }
						}
						/*return��ֹ����*/
						result.addAll(lo);
					}				
				}
				else{
					/*�ݹ�*/
					lo=insDesInSubGraphBiDirect(m.getResource(s.toString()),level+1,g,visitedSet);
					
					/*��˥��ϵ��*/
					double downPare=Math.pow(wAnonNode,((double)level+1.0));
					for (Iterator itx=lo.iterator();itx.hasNext();){
						Word w=(Word)itx.next();
						w.weight*=downPare;
					}
					result.addAll(lo);
				}
			}
		}

		return result;
	}
	
	/**********************
	 * ����ͼ�е�����Ԫ�ص��ı�����
	 ********************/
	public ArrayList fullOtherTexDes()
	{
		ArrayList result=new ArrayList();
		ArrayList checkedSet=new ArrayList();//Ԫ���Ƿ񱻴������־
        SplitWords spWord = new SplitWords();
        DelStopWords delSWrod= new DelStopWords();
        delSWrod.loadStopWords();
		
		/*������ͼ�е���Ԫ��*/
		for(Iterator it=fullGraphStms.iterator();it.hasNext();){
			Statement st=(Statement)it.next();
			String localURI=null;
			int elmType=-1;
			Resource r=st.getSubject();
			Property p=st.getPredicate();
			RDFNode o=st.getObject();
			
			/*����s*/
			if (r.isURIResource()){	localURI=r.getNameSpace();}
			if (!ontLngURI.contains(localURI) && !baseURI.equals(localURI)
					&& !checkedSet.contains(r.toString())) {// ����Ԫ�����Ԫ�أ�û�����
				checkedSet.add(r.toString());
				TextDes des=new TextDes();
				des.name=r.toString();
				des.text=new ArrayList();
				if (!r.isAnon()){//����������Դ
					//�ж�����
					elmType=getResourceType(r.toString());
					des.type=elmType;
					if (elmType==1){
						//��Class��
						int basicDespos=findCnptPosInFullName(r.toString());
						des.text=cnptBasicTextDes[basicDespos].text;
					}
					else if (elmType==2){
						//��Property��
						int basicDespos=findPropPosInFullName(r.toString());
						des.text=propBasicTextDes[basicDespos].text;
					}
					else if (elmType==3){
						//��Individual��
						int basicDespos=findInsPosInFullName(r.toString());
						des.text=insBasicTextDes[basicDespos].text;
					}
					else if (elmType==4){
						//����������value��Դ
						//��ʱ��������������Դ�������Ҫ����������Ļ�������
					}
				}
				else{
					//����������Դ
					Set setx=new HashSet();
					setx.add(st);
					ArrayList anonList=anonDesInFullGraphBiDirect(r,0,setx);
//					ArrayList anonList=anonDesInSubGraphSingleDirect(r,0,g);
					for (Iterator itx=anonList.iterator();itx.hasNext();){
						Word w=(Word)((Word)itx.next()).clone();
						des.text.add(w);
					}
				}
				/*���ô���*/
				des.text=desPostProcess(des.text);
				if (!des.text.isEmpty()){
					result.add(des);
				}				
			}
			
			/*����p*/
			localURI=null;
			if (p.isURIResource()){localURI=p.getNameSpace();}			
			if (!ontLngURI.contains(localURI) && !baseURI.equals(localURI)
					&& !checkedSet.contains(p.toString())) {// ����Ԫ�����Ԫ�أ�û�����
				checkedSet.add(p.toString());
				TextDes des=new TextDes();
				des.name=p.toString();
				des.text=new ArrayList();
				if (!p.isAnon()){//����������Դ
					//�ж�����
					elmType=getResourceType(p.toString());
					des.type=elmType;
					if (elmType==1){
						//��Class��
						int basicDespos=findCnptPosInFullName(p.toString());
						des.text=cnptBasicTextDes[basicDespos].text;
					}
					else if (elmType==2){
						//��Property��
						int basicDespos=findPropPosInFullName(p.toString());
						des.text=propBasicTextDes[basicDespos].text;
					}
					else if (elmType==3){
						//��Individual��
						int basicDespos=findInsPosInFullName(p.toString());
						des.text=insBasicTextDes[basicDespos].text;
					}
					else if (elmType==4){
						//����������value��Դ
						//��ʱ��������������Դ�������Ҫ����������Ļ�������
					}
				}
				else{
					//����������Դ
					Set setx=new HashSet();
					setx.add(st);
					ArrayList anonList=anonDesInFullGraphBiDirect(r,0,setx);
//					ArrayList anonList=anonDesInSubGraphSingleDirect(p,0,g);
					for (Iterator itx=anonList.iterator();itx.hasNext();){
						Word w=(Word)((Word)itx.next()).clone();
						des.text.add(w);
					}
				}
				/*���ô���*/
				des.text=desPostProcess(des.text);	
				if (!des.text.isEmpty()){
					result.add(des);
				}	
			}
			
			/*����o*/
			localURI=null;
			if (o.isURIResource()){	localURI=o.asNode().getNameSpace(); }
			if (!ontLngURI.contains(localURI) && !baseURI.equals(localURI)
					&& !checkedSet.contains(o.toString())) {// ����Ԫ�����Ԫ�أ�û�����
				checkedSet.add(o.toString());
				TextDes des=new TextDes();
				des.name=o.toString();
				des.text=new ArrayList();
				if (!o.isAnon()){//����������Դ
					//�ж�����
					elmType=getResourceType(o.toString());
					des.type=elmType;
					if (elmType==1){
						//��Class��
						int basicDespos=findCnptPosInFullName(o.toString());
						des.text=cnptBasicTextDes[basicDespos].text;
					}
					else if (elmType==2){
						//��Property��
						int basicDespos=findPropPosInFullName(o.toString());
						des.text=propBasicTextDes[basicDespos].text;
					}
					else if (elmType==3){
						//��Individual��
						int basicDespos=findInsPosInFullName(o.toString());
						des.text=insBasicTextDes[basicDespos].text;
					}
					else if (elmType==4){
						String str=null;
						if (o.isLiteral()){
							str=o.asNode().getLiteralLexicalForm();
						}
						else {
							str=o.toString();
						}
						str=delSWrod.removeStopWords(str);
				        ArrayList list=spWord.split(str);
				        list=delSWrod.removeStopWords(list);
				        /*���Ȩ��*/
				        for (Iterator itx=list.iterator();itx.hasNext();){
				        	String stemp=(String)itx.next();
				        	Word w=new Word();
				        	w.content=stemp;
				        	w.weight =1.0;
				        	des.text.add(w);
				        }
					}
				}
				else{
					//����������Դ
					Set setx=new HashSet();
					setx.add(st);
					ArrayList anonList=anonDesInFullGraphBiDirect(m.getResource(o.toString()),0,setx);
//					ArrayList anonList=anonDesInSubGraphSingleDirect(m.getResource(o.toString()),0,g);
					for (Iterator itx=anonList.iterator();itx.hasNext();){
						Word w=(Word)((Word)itx.next()).clone();
						des.text.add(w);
					}
				}
				/*���ô���*/
				des.text=desPostProcess(des.text);	
				if (!des.text.isEmpty()){
					result.add(des);
				}	
			}
		}		
		return result;
	}
}

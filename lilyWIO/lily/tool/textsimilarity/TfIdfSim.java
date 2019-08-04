/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-5-22
 * Filename          TfIdfSim.java
 * Version           2.0
 * 
 * Last modified on  2007-5-22
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 *
 ***********************************************/
package lily.tool.textsimilarity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lily.tool.datastructure.TextDes;
import lily.tool.datastructure.Word;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-5-22
 * 
 * describe:
 * ����TF/IDF�����ı������е����ƶ�
 * ���룺����������ı�
 * ��������ƶ�
 ********************/
public class TfIdfSim {
	
	
	
	/*************
	 * ����TF
	 * ���룺List��ʽ��ʾ��Doc
	 * �����List��ʽ��ʾ��TF
	 *************/
	public ArrayList getDocTF(ArrayList doc){
		ArrayList tfList=new ArrayList();
		
		/*����frequency*/
		double maxFre=0;
		for(Iterator it=doc.iterator();it.hasNext();){
			Word w=(Word)it.next();
			maxFre=Math.max(maxFre,w.weight);
		}
		
		/*�õ�TF*/
		for(Iterator it=doc.iterator();it.hasNext();){
			Word w=(Word)it.next();
			tfList.add(w.weight/maxFre);
		}
		
		return tfList;
	}
	
	/*************
	 * ����IDF
	 * ���룺TextDes[]��ʽ��ʾ��DocA��DocB
	 * �����DocA��DocB��List��ʽ��ʾ��IDF
	 *************/
	public ArrayList getDocIDF(TextDes[] docA,int nDocA,TextDes[] docB,int nDocB){
		ArrayList result=new ArrayList();
		ArrayList[] idfDocA=new ArrayList[nDocA];
		ArrayList[] idfDocB=new ArrayList[nDocB];
		
		/*���ĵ���*/
		int nDoc=nDocA+nDocB;
		
		/*�ȹ���term�ļ���*/
		ArrayList docTerm=new ArrayList();
		for (int i=0;i<nDocA;i++){
			ArrayList termList=docA[i].text;
			Set termSet=new HashSet();
			for(Iterator it=termList.iterator();it.hasNext();){
				Word w=(Word)it.next();
				termSet.add(w.content);
			}
			docTerm.add(i,termSet);
		}
		for (int i=0;i<nDocB;i++){
			ArrayList termList=docB[i].text;
			Set termSet=new HashSet();
			for(Iterator it=termList.iterator();it.hasNext();){
				Word w=(Word)it.next();
				termSet.add(w.content);
			}
			docTerm.add(nDocA+i,termSet);
		}
		
		/*docA��IDF*/
		double idf=0.0;
		for (int i=0;i<nDocA;i++){
			ArrayList termList=docA[i].text;//ȡterm��List
			idfDocA[i]=new ArrayList();
			for(Iterator it=termList.iterator();it.hasNext();){
				Word w=(Word)it.next();
				String term=w.content;//ȡterm
				/*ͳ��term���ĵ�Ƶ��*/
				int df=0;
				for(Iterator jt=docTerm.iterator();jt.hasNext();){
					Set st=(Set)jt.next();
					if (st.contains(term)){
						df++;
					}
				}
				/*log(N/n)��ĸ��0.01��Ϊ����������0�Ľ����*/
				idf=Math.log(((double)nDoc+0.01)/(double)df);
				idfDocA[i].add(idf);
			}
		}
		
		/*docB��IDF*/
		for (int i=0;i<nDocB;i++){
			ArrayList termList=docB[i].text;//ȡterm��List
			idfDocB[i]=new ArrayList();
			for(Iterator it=termList.iterator();it.hasNext();){
				Word w=(Word)it.next();
				String term=w.content;//ȡterm
				/*ͳ��term���ĵ�Ƶ��*/
				int df=0;
				for(Iterator jt=docTerm.iterator();jt.hasNext();){
					Set st=(Set)jt.next();
					if (st.contains(term)){
						df++;
					}
				}
				/*log(N/n)��ĸ��0.01��Ϊ����������0�Ľ����*/
				idf=Math.log(((double)nDoc+0.01)/(double)df);
				idfDocB[i].add(idf);
			}
		}
		result.add(0,idfDocA);
		result.add(1,idfDocB);
		return result;
	}
	
	/*************
	 * ����doc��if/idfȨ��
	 * ���룺TextDes[]��ʽ��ʾ��Doc
	 * �����List��ʽ��ʾ��Ȩ��
	 *************/
	public ArrayList[] getTFIDFWeight(TextDes[] doc,int nDoc,
			                   ArrayList[] tfList,ArrayList[] idfList)
	{
		ArrayList[] weightList=new ArrayList[nDoc];
		
		for (int i=0;i<nDoc;i++){
			weightList[i]=new ArrayList();
			if (tfList[i]==null){continue;}
			for (int j=0;j<tfList[i].size();j++){
				/*�õ�TF*/
				double tf=((Double)tfList[i].get(j)).doubleValue();
				/*�õ�IDF*/
				double idf=((Double)idfList[i].get(j)).doubleValue();
				/*�õ�weight*/
				weightList[i].add(tf*idf);
			}
		}
		return weightList;
	}
	
	
	/*************
	 * ���������ı�������
	 * ���룺List��ʽ��ʾ��dA��dB,�Լ���Ӧ��Ȩ��List
	 * �����dA��dB������
	 *************/
	public ArrayList consTextVector(ArrayList dA,ArrayList dB,ArrayList wdA,ArrayList wdB)
	{
		ArrayList result=new ArrayList();
		ArrayList vA=new ArrayList();
		ArrayList vB=new ArrayList();
		
		/*�ȹ��������*/
		ArrayList baseV=new ArrayList();
		
		ArrayList termA=new ArrayList();
		for(Iterator it=dA.iterator();it.hasNext();){
			Word w=(Word)it.next();
			termA.add(w.content);
			if (!baseV.contains(w.content)){
				baseV.add(w.content);
			}
		}
		ArrayList termB=new ArrayList();
		for(Iterator it=dB.iterator();it.hasNext();){
			Word w=(Word)it.next();
			termB.add(w.content);
			if (!baseV.contains(w.content)){
				baseV.add(w.content);
			}
		}
		
		/*��������A*/
		int pos=0;
		double weight=0.0;
		for(int i=0;i<baseV.size();i++){
			String term=(String)baseV.get(i);
			if (termA.contains(term)){//��ǰά����
				pos=termA.indexOf(term);
				weight=((Double)wdA.get(pos)).doubleValue();
				vA.add(i,weight);
			}
			else{//��ǰά������
				vA.add(0.0);
			}
		}
		
		/*��������B*/
		for(int i=0;i<baseV.size();i++){
			String term=(String)baseV.get(i);
			if (termB.contains(term)){//��ǰά����
				pos=termB.indexOf(term);
				weight=((Double)wdB.get(pos)).doubleValue();
				vB.add(i,weight);
			}
			else{//��ǰά������
				vB.add(0.0);
			}
		}
		
		result.add(0,vA);
		result.add(1,vB);
		return result;
	}
	
	/*************
	 * �����������ƶ�
	 * ���룺TextDes[]��ʽ��ʾ��DocA��DocB
	 * �����DocA��DocB��List��ʽ��ʾ��IDF
	 *************/
	public double getTextVectorSim(ArrayList vA,ArrayList vB)
	{
		double sim=0.0;
		
		/*�ж���������ά���Ƿ����*/
		if (vA.size()!=vB.size()){
			System.out.println("����ά������");
			return -1;
		}
		
		/*���ӵ��*/
		double aDotb=0;
		for (int i=0;i<vA.size();i++){
			double x=((Double)vA.get(i)).doubleValue();
			double y=((Double)vB.get(i)).doubleValue();
			aDotb=aDotb+x*y;
		}
		
		/*��ĸ��������*/
		double aDisb=0;
		double x2=0;
		double y2=0;
		for (int i=0;i<vB.size();i++){
			double x=((Double)vA.get(i)).doubleValue();
			double y=((Double)vB.get(i)).doubleValue();
			x2=x2+x*x;
			y2=y2+y*y;
		}
		
		aDisb=(Math.sqrt(x2))*(Math.sqrt(y2));
		
		/*���ƶ�*/
		if (Math.abs(aDisb)<0.00001){
			sim=0.0;
		}
		else{
			sim=aDotb/aDisb;
		}
		
		return sim;
	}
}

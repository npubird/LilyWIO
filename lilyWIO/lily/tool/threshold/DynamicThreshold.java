/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-6-22
 * Filename          DynamicThreshold.java
 * Version           2.0
 * 
 * Last modified on  2007-6-22
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 * ��̬��ֵ��ѡ��
 ***********************************************/
package lily.tool.threshold;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-6-22
 * 
 * describe:
 * 
 ********************/
public class DynamicThreshold {
	private double lowSimThreshold = 0.001;//������ֵ
	private double highThreshold = 0.65;//�������ƶȺܸߵķ�ֵ
	private double minInterval=0.001;//��С��� 
	
	/***************
	 * �򵥷�����
	 * ��������Ĺ�ģ���򵥹��Ʒ�ֵ
	 ***************/
	public double naiveThreshold(double[][] simMx, int m,int n) {
		double td=0;
		double maxSim=0.0;
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				maxSim=Math.max(maxSim,simMx[i][j]);
			}
		}
		
		if (maxSim<lowSimThreshold){
			/*���������ƶȴﲻ��������ֵ*/
			td=lowSimThreshold;
		}
		else{
			/*���㵱ǰ���Ƶķ�ֵ*/
			td=maxSim/Math.max((double)m,(double)n);
		}
		System.out.println("��ֵ��"+td);
		return td;
	}
	
	/*******************
	 * ����ָ�����
	 *******************/
	public double getInterval(double[] simMx, int m)
	{
		double interval=1.0;
    	double[] mx=new double[m];
    	mx=simMx.clone();
    	Arrays.sort(mx);
    	/*�������*/
    	for (int i=1;i<m;i++){
    		if (mx[i]>0 && mx[i-1]>0){
    			if (Math.abs(mx[i]-mx[i-1])>minInterval){
    				interval=Math.min(Math.abs(mx[i]-mx[i-1]),interval);   
    			}    			 			
    		}    		
    	}
//    	System.out.println("iterval:"+interval);
    	return interval;
	}
	
	/*******************
	 * ����ط���
	 * ��������ѡ����
	 *******************/
    public double maxEntropyThreshold(double[] simMx, int m)
    {
    	double interval=0;
    	double max=0;
    	double threshold=0;
    	int sNum=0;//������Ŀ
		int[] freqSum=new int[sNum];//ֱ��i����Ƶ��
		double[] freqSim=new double[sNum];//iλ�õ�Ƶ�ʶ�Ӧ�����ƶ�
		int[] freqCur=new int[sNum];//iλ�ô���Ƶ��
		int count=0;
		double ps=0;
		double pi=0;
		double Hs=0;
		double Hn=0;
		double Kapur=0;//Kapur�ָ��ж�����
    	
    	/*��ü��*/
    	interval=getInterval(simMx,m);
    	
		/*������������*/
		sNum=(int)(1.0/interval);
		if (1.0/interval-sNum>0){
			sNum++;
		}
		/*ͳ��ÿ����������ƶȷֲ�Ƶ��*/
		for (int i=0;i<sNum;i++){
			int countCur=0;
			for (int j=0;j<m;j++){
				if (simMx[j]>=i*interval && simMx[j]<(i+1)*interval){
					count++;
					freqSim[i]=simMx[j];
					countCur++;
				}
			}
			freqCur[i]=countCur;
			freqSum[i]=count;
			System.out.println(freqSum[i]);
		}		

		int split=0;//�ָ�λ��
		for (int i=0;i<sNum;i++){
			/*s=i*/
			ps=(double)freqSum[i]/(double)m;
			if (ps>0){
				/*Hs*/
				Hs=0;
				for (int j=0;j<=i;j++){
					pi=(double)freqCur[j]/(double)m;
					if (pi>0){
						Hs+=(-1.0)*pi*Math.log(pi);
					}
				}
				/*Hn*/
				Hn=0;
				for (int j=i+1;j<sNum;j++){
					pi=(double)freqCur[j]/(double)m;
					if (pi>0){
						Hn+=(-1.0)*pi*Math.log(pi);
					}
				}
				Kapur=Math.log(ps*(1.0-ps))+Hs/ps+(Hn-Hs)/(1-ps);
				if (Kapur>max){
					max=Kapur;
					split=i;
					System.out.println("max="+Kapur+"s="+i);
				}
			}
		}
		
		threshold=(double)split*interval*0.95;
		if (threshold<lowSimThreshold){
			threshold=lowSimThreshold;
		}
		if (threshold>highThreshold){
			threshold=highThreshold;
		}
		
		System.out.println("�ָ�㣺"+split+"  �ָ�λ�ã�"+threshold );
		
		return threshold;
    }
    
    /*******************
	 * ����ط���
	 * ������������ƶȾ���
	 *******************/
    public double maxEntropyThreshold(double[][] simMx, int m, int n)
    {
    	double[] sim;
    	int dataNum=0;
    	double interval=0;
    	double max=0;
    	double threshold=0;
    	int sNum=0;//������Ŀ
		int[] freqSum;//ֱ��i����Ƶ��
		double[] freqSim;//iλ�õ�Ƶ�ʶ�Ӧ�����ƶ�
		int[] freqCur;//iλ�ô���Ƶ��
		int count=0;
		double ps=0;
		double pi=0;
		double Hs=0;
		double Hn=0;
		double Kapur=0;//Kapur�ָ��ж�����
    	
		/*��ʼ��*/
		
		/*���ά����Ϊһά,������Ϊ0��λ��*/
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				if (simMx[i][j]>0){
					count++;
				}
			}
		}
		dataNum=count;
		sim=new double[dataNum];
		count=0;
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				if (simMx[i][j]>0){
					sim[count]=simMx[i][j];
					count++;
				}
			}
		}
		count=0;
		
    	/*��ü��*/
    	interval=getInterval(sim,dataNum);
    	
		/*������������*/
		sNum=(int)(1.0/interval);
		if (1.0/interval-sNum>0){
			sNum++;
		}
		freqSum=new int[sNum];//ֱ��i����Ƶ��
		freqSim=new double[sNum];//iλ�õ�Ƶ�ʶ�Ӧ�����ƶ�
		freqCur=new int[sNum];//iλ�ô���Ƶ��
		/*ͳ��ÿ����������ƶȷֲ�Ƶ��*/
		for (int i=0;i<sNum;i++){
			int countCur=0;
			for (int j=0;j<dataNum;j++){
				if (sim[j]>=i*interval && sim[j]<(i+1)*interval){
					count++;
					freqSim[i]=sim[j];
					countCur++;
				}
			}
			freqCur[i]=countCur;
			freqSum[i]=count;
//			System.out.println(freqSum[i]);
		}		

		int split=0;//�ָ�λ��
		for (int i=0;i<sNum;i++){
			/*s=i*/
			ps=(double)freqSum[i]/(double)dataNum;
			if (ps>0){
				/*Hs*/
				Hs=0;
				for (int j=0;j<=i;j++){
					pi=(double)freqCur[j]/(double)dataNum;
					if (pi>0){
						Hs+=(-1.0)*pi*Math.log(pi);
					}
				}
				/*Hn*/
				Hn=0;
				for (int j=i+1;j<sNum;j++){
					pi=(double)freqCur[j]/(double)dataNum;
					if (pi>0){
						Hn+=(-1.0)*pi*Math.log(pi);
					}
				}
				Kapur=Math.log(ps*(1.0-ps))+Hs/ps+(Hn-Hs)/(1-ps);
				if (Kapur>max){
					max=Kapur;
					split=i;
					System.out.println("max="+Kapur+"s="+i);
				}
			}
		}
		
		threshold=(double)split*interval*0.95;
		if (threshold<lowSimThreshold){
			threshold=lowSimThreshold;
		}
		if (threshold>highThreshold){
			threshold=highThreshold;
		}
		
		System.out.println("�ָ�㣺"+split+"  �ָ�λ�ã�"+threshold );
		
		return threshold;
    }
    
    /*******************
	 * ����ط���������A
	 * ���ڹ��Ǻ�����ƶȾ���
	 *******************/
    public double maxEntropyThresholdA(double[][] simMx, int m, int n)
    {
    	double[] sim;
    	int dataNum=0;
    	double interval=0;
    	double max=0;
    	double min=1.1;
    	double threshold=0;
    	int sNum=0;//������Ŀ
		int[] freqSum;//ֱ��i����Ƶ��
		double[] freqSim;//iλ�õ�Ƶ�ʶ�Ӧ�����ƶ�
		int[] freqCur;//iλ�ô���Ƶ��
		int count=0;
		double ps=0;
		double pi=0;
		double Hs=0;
		double Hn=0;
		double Kapur=0;//Kapur�ָ��ж�����
    	
		/*��ʼ��*/

		
		/*���ά����Ϊһά*/
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				if (simMx[i][j]>1.0){
					simMx[i][j]=1.0;
				}
				if (simMx[i][j]>0){
					dataNum++;
					min=Math.min(min,simMx[i][j]);
					break;
				}
			}
		}
		
		/*�������ƶ��Ƿ�ܸ�*/
		if (min>highThreshold){
			threshold=highThreshold;
			return threshold;
		}
		
		sim=new double[dataNum];
		count=0;
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				if (simMx[i][j]>0){
					sim[count]=simMx[i][j];
					count++;
					break;
				}
			}
		}
		count=0;
		
    	/*��ü��*/
    	interval=getInterval(sim,dataNum);
    	
		/*������������*/
		sNum=(int)(1.0/interval);
		if (1.0/interval-sNum>0){
			sNum++;
		}
		freqSum=new int[sNum];//ֱ��i����Ƶ��
		freqSim=new double[sNum];//iλ�õ�Ƶ�ʶ�Ӧ�����ƶ�
		freqCur=new int[sNum];//iλ�ô���Ƶ��
		/*ͳ��ÿ����������ƶȷֲ�Ƶ��*/
		for (int i=0;i<sNum;i++){
			int countCur=0;
			for (int j=0;j<dataNum;j++){
				if (sim[j]>=i*interval && sim[j]<(i+1)*interval){
					count++;
					freqSim[i]=sim[j];
					countCur++;
				}
			}
			freqCur[i]=countCur;
			freqSum[i]=count;
//			System.out.println(freqSum[i]);
		}		

		int split=0;//�ָ�λ��
		for (int i=0;i<sNum;i++){
			/*s=i*/
			ps=(double)freqSum[i]/(double)dataNum;
			if (ps>0){
				/*Hs*/
				Hs=0;
				for (int j=0;j<=i;j++){
					pi=(double)freqCur[j]/(double)dataNum;
					if (pi>0){
						Hs+=(-1.0)*pi*Math.log(pi);
					}
				}
				/*Hn*/
				Hn=0;
				for (int j=i+1;j<sNum;j++){
					pi=(double)freqCur[j]/(double)dataNum;
					if (pi>0){
						Hn+=(-1.0)*pi*Math.log(pi);
					}
				}
				Kapur=Math.log(ps*(1.0-ps))+Hs/ps+(Hn-Hs)/(1-ps);
				if (Kapur>max){
					max=Kapur;
					split=i;
//					System.out.println("max="+Kapur+"s="+i);
				}
			}
		}
		
		threshold=(double)split*interval*0.95;
		if (threshold<lowSimThreshold){
			threshold=lowSimThreshold;
		}
		if (threshold>highThreshold){
			threshold=highThreshold;
		}
		
//		System.out.println("�ָ�㣺"+split+"  �ָ�λ�ã�"+threshold );
		
		return threshold;
    }
    
    /*******************
	 * Ostu����
	 * ���ڹ��Ǻ�����ƶȾ���
	 *******************/
    public double ostuThreshold(double[][] simMx, int m, int n)
    {
    	double[] sim;
    	int dataNum=0;
    	double interval=0;
    	double max=0;
    	double min=1.1;
    	double threshold=0;
    	int sNum=0;//������Ŀ
		int[] freqSum;//ֱ��i����Ƶ��
		double[] freqSim;//iλ�õ�Ƶ�ʶ�Ӧ�����ƶ�
		int[] freqCur;//iλ�ô���Ƶ��
		int count=0;
		double pi=0;
		double omega0,omega1;
		double sigmaB;
		double mju0,mju1,mjut,mjuT;
    	
		/*��ʼ��*/

		
		/*���ά����Ϊһά*/
		/*��ͳ�ƴ���0�����ƶ���Ŀ*/
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				if (simMx[i][j]>1.0){
					simMx[i][j]=1.0;
				}
				if (simMx[i][j]>0){
					dataNum++;
					min=Math.min(min,simMx[i][j]);
					break;
				}
			}
		}
		
		/*�������ƶ��Ƿ�ܸ�*/
		if (min>highThreshold){
			threshold=highThreshold;
			return threshold;
		}
		/*��Ϊһά*/
		sim=new double[dataNum];
		count=0;
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				if (simMx[i][j]>0){
					sim[count]=simMx[i][j];
					count++;
					break;
				}
			}
		}
		count=0;
		
    	/*��ü��*/
    	interval=getInterval(sim,dataNum);
    	
		/*������������*/
		sNum=(int)(1.0/interval);
		if (1.0/interval-sNum>0){
			sNum++;
		}
		freqSum=new int[sNum];//ֱ��i����Ƶ��
		freqSim=new double[sNum];//iλ�õ�Ƶ�ʶ�Ӧ�����ƶ�
		freqCur=new int[sNum];//iλ�ô���Ƶ��
		/*ͳ��ÿ����������ƶȷֲ�Ƶ��*/
		for (int i=0;i<sNum;i++){
			int countCur=0;
			for (int j=0;j<dataNum;j++){
				if (sim[j]>=i*interval && sim[j]<(i+1)*interval){
					count++;
					freqSim[i]=sim[j];
					countCur++;
				}
			}
			freqCur[i]=countCur;
			freqSum[i]=count;
		}
		
		/*Ostu����*/
		/*����mjuT*/
		mjuT=0;
		for (int j=0;j<sNum;j++){
			pi=(double)freqCur[j]/(double)dataNum;
			mjuT+=(double)j*pi;
		}
		int split=0;//�ָ�λ��
		for (int i=0;i<sNum;i++){
			/*split=i*/
			
			/*����omega0*/
			omega0=0;
			for(int j=0;j<=i;j++){
				pi=(double)freqCur[j]/(double)dataNum;
				omega0+=pi;
			}
			/*����omega1*/
			omega1=0;
			omega1=1-omega0;
			/*����mju0*/
			mju0=0;
			mjut=0;
			for (int j=0;j<=i;j++){
				pi=(double)freqCur[j]/(double)dataNum;
				mjut+=(double)j*pi;
			}
			mju0=mjut/omega0;
			/*����mju1*/
			mju1=0;
			mju1=(mjuT-mjut)/omega1;
			/*�ж���׼SigmaB*/
			sigmaB=omega0*omega1*(mju0-mju1)*(mju0-mju1);
			
			if (sigmaB>max){
				max=sigmaB;
				split=i;
				System.out.println("max="+sigmaB+"s="+i);
			}			
		}
		
		threshold=(double)split*interval*0.95;
		if (threshold<lowSimThreshold){
			threshold=lowSimThreshold;
		}
		if (threshold>highThreshold){
			threshold=highThreshold;
		}
		
		System.out.println("�ָ�㣺"+split+"  �ָ�λ�ã�"+threshold );
		
		return threshold;
    }
    
    /*******************
	 * Minimum error����
	 * ���ڹ��Ǻ�����ƶȾ���
	 *******************/
    public double miniErrorThreshold(double[][] simMx, int m, int n)
    {
    	double[] sim;
    	int dataNum=0;
    	double interval=0;
    	double min=99999999;
    	double threshold=0;
    	int sNum=0;//������Ŀ
		int[] freqSum;//ֱ��i����Ƶ��
		double[] freqSim;//iλ�õ�Ƶ�ʶ�Ӧ�����ƶ�
		int[] freqCur;//iλ�ô���Ƶ��
		int count=0;
		double p0,p1;
		double sigma0,sigma1;
		double mju0,mju1;
		double Kittler=0;
    	
		/*���ά����Ϊһά*/
		/*��ͳ�ƴ���0�����ƶ���Ŀ*/
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				if (simMx[i][j]>1.0){
					simMx[i][j]=1.0;
				}
				if (simMx[i][j]>0){
					dataNum++;
					min=Math.min(min,simMx[i][j]);
					break;
				}
			}
		}
		
		/*�������ƶ��Ƿ�ܸ�*/
		if (min>highThreshold){
			threshold=highThreshold;
			return threshold;
		}
		/*��Ϊһά*/
		sim=new double[dataNum];
		count=0;
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				if (simMx[i][j]>0){
					sim[count]=simMx[i][j];
					count++;
					break;
				}
			}
		}
		count=0;
		
    	/*��ü��*/
    	interval=getInterval(sim,dataNum);
    	
		/*������������*/
		sNum=(int)(1.0/interval);
		if (1.0/interval-sNum>0){
			sNum++;
		}
		freqSum=new int[sNum];//ֱ��i����Ƶ��
		freqSim=new double[sNum];//iλ�õ�Ƶ�ʶ�Ӧ�����ƶ�
		freqCur=new int[sNum];//iλ�ô���Ƶ��
		/*ͳ��ÿ����������ƶȷֲ�Ƶ��*/
		for (int i=0;i<sNum;i++){
			int countCur=0;
			for (int j=0;j<dataNum;j++){
				if (sim[j]>=i*interval && sim[j]<(i+1)*interval){
					count++;
					freqSim[i]=sim[j];
					countCur++;
				}
			}
			freqCur[i]=countCur;
			freqSum[i]=count;
		}
		
		/*Minimum error����*/
		min=99999999;
		int split=0;//�ָ�λ��
		for (int i=0;i<sNum;i++){
			/*split=i*/
			/*����P0*/
			p0=0;
			for(int j=0;j<=i;j++){
				p0=p0+(double)freqCur[j];
			}
			/*����P1*/
			p1=0;
			for(int j=i+1;j<sNum;j++){
				p1=p1+(double)freqCur[j];
			}
			if (Math.abs(p0*p1-0.0)<0.0001){//�������Ϊ0
				continue;
			}
			/*����mju0*/
			mju0=0;
			for(int j=0;j<=i;j++){
				mju0=mju0+(double)freqCur[j]*(double)j;
			}
			mju0=mju0/p0;
			/*����mju1*/
			mju1=0;
			for(int j=i+1;j<sNum;j++){
				mju1=mju1+(double)freqCur[j]*(double)j;
			}
			mju1=mju1/p1;
			/*����sigma0*/
			sigma0=0;
			for(int j=0;j<=i;j++){
				sigma0=sigma0+(double)freqCur[j]*((double)j-mju0)*((double)j-mju0);
			}
			sigma0=sigma0/p0;
			/*����sigma1*/
			sigma1=0;
			for(int j=i+1;j<sNum;j++){
				sigma1=sigma1+(double)freqCur[j]*((double)j-mju1)*((double)j-mju1);
			}
			sigma1=sigma1/p1;
			
			if (Math.abs(sigma0*sigma1-0.0)<0.0001){//�������Ϊ0
				continue;
			}
			
			/*�ж���׼Kittler*/
			Kittler=1.0+2.0*(p0*Math.log(sigma0)+p1*Math.log(sigma1))
			           -2.0*(p0*Math.log(p0)+p1*Math.log(p1));
			if (Kittler<min){
				min=Kittler;
				split=i;
				System.out.println("min="+Kittler+"s="+i);
			}	
		}

		threshold=(double)split*interval*0.95;
		if (threshold<lowSimThreshold){
			threshold=lowSimThreshold;
		}
		if (threshold>highThreshold){
			threshold=highThreshold;
		}
		
		System.out.println("�ָ�㣺"+split+"  �ָ�λ�ã�"+threshold );
		
		return threshold;
    }
    
    /*******************
	 * max correlation����
	 * ���ڹ��Ǻ�����ƶȾ���
	 *******************/
    public double maxCorrelationThreshold(double[][] simMx, int m, int n)
    {
    	double[] sim;
    	int dataNum=0;
    	double interval=0;
    	double max=0;
    	double min=99999999;
    	double threshold=0;
    	int sNum=0;//������Ŀ
		int[] freqSum;//ֱ��i����Ƶ��
		double[] freqSim;//iλ�õ�Ƶ�ʶ�Ӧ�����ƶ�
		int[] freqCur;//iλ�ô���Ƶ��
		int count=0;
		double gO,gB;
		double pi,pt;
		double tc=0;
    	
		/*���ά����Ϊһά*/
		/*��ͳ�ƴ���0�����ƶ���Ŀ*/
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				if (simMx[i][j]>1.0){
					simMx[i][j]=1.0;
				}
				if (simMx[i][j]>0){
					dataNum++;
					min=Math.min(min,simMx[i][j]);
					break;
				}
			}
		}
		
		/*�������ƶ��Ƿ�ܸ�*/
		if (min>highThreshold){
			threshold=highThreshold;
			return threshold;
		}
		/*��Ϊһά*/
		sim=new double[dataNum];
		count=0;
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				if (simMx[i][j]>0){
					sim[count]=simMx[i][j];
					count++;
					break;
				}
			}
		}
		count=0;
		
    	/*��ü��*/
    	interval=getInterval(sim,dataNum);
    	
		/*������������*/
		sNum=(int)(1.0/interval);
		if (1.0/interval-sNum>0){
			sNum++;
		}
		freqSum=new int[sNum];//ֱ��i����Ƶ��
		freqSim=new double[sNum];//iλ�õ�Ƶ�ʶ�Ӧ�����ƶ�
		freqCur=new int[sNum];//iλ�ô���Ƶ��
		/*ͳ��ÿ����������ƶȷֲ�Ƶ��*/
		for (int i=0;i<sNum;i++){
			int countCur=0;
			for (int j=0;j<dataNum;j++){
				if (sim[j]>=i*interval && sim[j]<(i+1)*interval){
					count++;
					freqSim[i]=sim[j];
					countCur++;
				}
			}
			freqCur[i]=countCur;
			freqSum[i]=count;
		}
		
		/*Max correlation����*/
		int split=0;//�ָ�λ��
		for (int i=0;i<sNum;i++){
			/*split=i*/
			/*����go*/
			gO=0;
			for(int j=0;j<=i;j++){
				pi=(double)freqCur[j]/(double)dataNum;
				gO=gO+pi*pi;
			}
			/*����gB*/
			gB=0;
			for(int j=i+1;j<sNum;j++){
				pi=(double)freqCur[j]/(double)dataNum;
				gB=gB+pi*pi;
			}			
			/*����pt*/
			pt=(double)freqSum[i]/(double)dataNum;
			if ((Math.abs(gO*gB)<0.0001)||(Math.abs(pt*(1.0-pt))<0.0001)){
				continue;
			}
			/*�ж���׼TC*/
			tc=-Math.log(gO*gB)+2.0*Math.log(pt*(1.0-pt));
			
			if (tc>max){
				max=tc;
				split=i;
				System.out.println("max="+tc+"s="+i);
			}	
		}

		threshold=(double)split*interval*0.95;
		if (threshold<lowSimThreshold){
			threshold=lowSimThreshold;
		}
		if (threshold>highThreshold){
			threshold=highThreshold;
		}
		
		System.out.println("�ָ�㣺"+split+"  �ָ�λ�ã�"+threshold );
		
		return threshold;
    }
    
    /*******************
	 * �Լ��ķ���
	 *******************/
    public double maxWPThreshold(double[][] simMx, int m, int n)
    {
    	double[] sim;
    	int dataNum=0;
    	double interval=0;
    	double max=0;
    	double min=99999999;
    	double threshold=0;
    	int sNum=0;//������Ŀ
		int[] freqSum;//ֱ��i����Ƶ��
		double[] freqSim;//iλ�õ�Ƶ�ʶ�Ӧ�����ƶ�
		int[] freqCur;//iλ�ô���Ƶ��
		int count=0;
		double pi,pt;
		double wp=0;
    	
		/*���ά����Ϊһά*/
		/*��ͳ�ƴ���0�����ƶ���Ŀ*/
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				if (simMx[i][j]>1.0){
					simMx[i][j]=1.0;
				}
				if (simMx[i][j]>0){
					dataNum++;
					min=Math.min(min,simMx[i][j]);
					break;
				}
			}
		}
		
		/*�������ƶ��Ƿ�ܸ�*/
		if (min>highThreshold){
			threshold=highThreshold;
			return threshold;
		}
		/*��Ϊһά*/
		sim=new double[dataNum];
		count=0;
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				if (simMx[i][j]>0){
					sim[count]=simMx[i][j];
					count++;
					break;
				}
			}
		}
		count=0;
		
    	/*��ü��*/
    	interval=getInterval(sim,dataNum);
    	
		/*������������*/
		sNum=(int)(1.0/interval);
		if (1.0/interval-sNum>0){
			sNum++;
		}
		freqSum=new int[sNum];//ֱ��i����Ƶ��
		freqSim=new double[sNum];//iλ�õ�Ƶ�ʶ�Ӧ�����ƶ�
		freqCur=new int[sNum];//iλ�ô���Ƶ��
		/*ͳ��ÿ����������ƶȷֲ�Ƶ��*/
		for (int i=0;i<sNum;i++){
			int countCur=0;
			for (int j=0;j<dataNum;j++){
				if (sim[j]>=i*interval && sim[j]<(i+1)*interval){
					count++;
					freqSim[i]=sim[j];
					countCur++;
				}
			}
			freqCur[i]=countCur;
			freqSum[i]=count;
		}
		
		/*���Եķ���*/
		int split=0;//�ָ�λ��
		for (int i=0;i<sNum;i++){
			/*��ǰ�ָ�ֵ*/
			double t=(double)i*interval;
			/*����ԭʼ����*/
			double[][] matrix=new double[m][n];
			matrix=(double[][])simMx.clone();
			for (int j=0;j<m;j++){
				if (matrix[j]!=null){
					matrix[j]=(double[])simMx[j].clone();
				}
			}
			/*t�Ѿ����г�һ����*/
//			for (int k=0;k<m;k++){
//				for (int l=0;l<n;l++){
//					if (matrix[k][l]<=t){
//						matrix[k][l]=0;
//					}
//				}
//			}
			/*���У��еľ�ֵ*/
			double[] avgrow=new double[m];
			double[] avgcol=new double[n];
			for (int k=0;k<m;k++){
				avgrow[k]=0;
				for (int j=0;j<n;j++){
					avgrow[k]=avgrow[k]+matrix[k][j];
				}
//				avgrow[i]=avgrow[i]/(double)n;
			}
			for (int k=0;k<n;k++){
				avgcol[k]=0;
				for (int j=0;j<m;j++){
					avgcol[k]=avgcol[k]+matrix[j][k];
				}
//				avgcol[i]=avgcol[i]/(double)m;
			}
			
			/*���㵱ǰ����������ѡ�������positiveλ��*/
			ArrayList psList=new ArrayList();
			psList=this.positiveFinder(matrix,m,n);
			
			/*�������0��λ�õ����Ƴ̶�*/
			double[][] predomain=new double[m][n];
			for (int k = 0; k < m; k++) {
				for (int j = 0; j < n; j++) {
					if (matrix[k][j] > 0) {
						double havg = (avgrow[k] + avgcol[j] - matrix[k][j])
								/ (double) (m + n - 1);
						predomain[k][j] = havg* (matrix[k][j] - havg);
						predomain[k][j] = matrix[k][j]* predomain[k][j];
					}
				}
			}			
			
			/*�ж���׼WP*/
			double positive1=0;
			double negative1=0;
			double positive2=0;
			double negative2=0;
			for (int k = 0; k < m; k++) {
				for (int j = 0; j < n; j++) {
					if (matrix[k][j]>t){
						/* ��positive */
						boolean hasPosi = false;
						for (Iterator ix = psList.iterator(); ix.hasNext();) {
							int rx = ((Integer) ix.next()).intValue();
							int cx = ((Integer) ix.next()).intValue();
							if (rx == k && cx == j) {
								positive1 += predomain[k][j];
								hasPosi = true;
								break;
							}
						}
						/* ��negtive */
						if (!hasPosi) {
							negative1 += predomain[k][j];
						}
					}
					else{
						/* ��positive */
						boolean hasPosi = false;
						for (Iterator ix = psList.iterator(); ix.hasNext();) {
							int rx = ((Integer) ix.next()).intValue();
							int cx = ((Integer) ix.next()).intValue();
							if (rx == k && cx == j) {
								positive2 += predomain[k][j];
								hasPosi = true;
								break;
							}
						}
						/* ��negtive */
						if (!hasPosi) {
							negative2 += predomain[k][j];
						}
					}
				}
			}
			wp = positive1-positive2-negative1;
			if (wp > max) {
				max = wp;
				split = i;
				System.out.println("max=" + wp + "s=" + i);
			}
		}

		threshold=(double)split*interval*0.95;
		if (threshold<lowSimThreshold){
			threshold=lowSimThreshold;
		}
		if (threshold>highThreshold){
			threshold=highThreshold;
		}
		
		System.out.println("�ָ�㣺"+split+"  �ָ�λ�ã�"+threshold );
		
		return threshold;
	}
    
	private ArrayList positiveFinder(double[][] sim,int m,int n)
	{
		int i,j,k;
		int maxNum = 0;
		int count = 0;
		double[][] simFilter= new double[m][n];
		double[] row = new double[m*n];
		int[] matchA,matchB;
		boolean BREAK;
		
		ArrayList result=new ArrayList();
		
		//�������һ��������
		for (i=0;i<m;i++)
			for (j=0;j<n;j++)
			{
				row[count] = sim[i][j];
				count++;
			}
		
		Arrays.sort(row);//����
		maxNum = Math.max(m,n);
		matchA= new int[maxNum];
		matchB= new int[maxNum];
		for (i=0;i<maxNum;i++){matchA[i]=-1;matchB[i]=-1;}
				
		k = m*n-1;
		while(k>=0 && row[k]>0 && row[k]>0.0)
		{
			BREAK = false;
			for (i=0;i<m;i++)
			{
				if (BREAK) break;
				for (j=0;j<n;j++)
				{
					if ((sim[i][j]==row[k]) && (matchA[i]==-1) && (matchB[j]==-1))
					{
						matchA[i]=j;
						matchB[j]=i;
						simFilter[i][j]=row[k];
						
						result.add(i);
						result.add(j);
											
						BREAK = true;
						break;
					}
				}
			}
			k--;
		}
		return result; 
	}
}

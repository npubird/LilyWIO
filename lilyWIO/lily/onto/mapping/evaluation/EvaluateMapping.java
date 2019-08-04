/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-4-26
 * Filename          EvaluateMapping.java
 * Version           2.0
 * 
 * Last modified on  2007-4-26
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 *
 ***********************************************/
package lily.onto.mapping.evaluation;

import java.util.ArrayList;

import lily.tool.datastructure.*;
/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-4-26
 * 
 * describe:
 * 
 ********************/
public class EvaluateMapping {

	private MapRecord refResult[];
	private MapRecord lilyResult[];
	private int conjunNum, refNum, lilyNum;
	private double p,r,f1;
	
	public void init()
	{
		refResult=new MapRecord[refNum];
		lilyResult=new MapRecord[lilyNum];
		p = 0;
		r = 0;
		f1 = 0;
	}
	public ArrayList getEvaluation(int m, MapRecord ref[], int n, MapRecord lily[])
	{
		//初始化
		refNum=m;
		lilyNum=n;
		init();
		refResult=ref;
		lilyResult=lily;
		//计算
		computeConjunNum();
		this.getPrecision();
		this.getRecall();
		this.getF1Measure();
		//显示
		this.show();
		//返回结果
		ArrayList list = new ArrayList();
		list.add(0,p);
		list.add(1,r);
		list.add(2,f1);
		return list;
	}
	private void computeConjunNum()
	{
		//compute the conjunction of the two sets
		conjunNum = 0;
		for (int i=0;i<lilyNum;i++)
		{
			boolean flag=false;
			for (int j=0;j<refNum;j++)
			{
				if (refResult[j].sourceLabel.equals(lilyResult[i].sourceLabel) && 
					refResult[j].targetLabel.equals(lilyResult[i].targetLabel) &&
					(refResult[j].relationType == lilyResult[i].relationType))					
				{
					conjunNum++;
					flag=true;
					break;
				}
			}
			if (!flag){
				System.out.println("错误:"+lilyResult[i].sourceLabel+int2type(lilyResult[i].relationType)+lilyResult[i].targetLabel);
				}
		}
		
		conjunNum = 0;
		for (int i=0;i<refNum;i++)
		{
			boolean flag=false;
			for (int j=0;j<lilyNum;j++)
			{
				if (refResult[i].sourceLabel.equals(lilyResult[j].sourceLabel) && 
					refResult[i].targetLabel.equals(lilyResult[j].targetLabel) &&
					(refResult[i].relationType == lilyResult[j].relationType))					
				{
					conjunNum++;
					flag=true;
					break;
				}
			}
			if (!flag){
				System.out.println("遗漏:"+refResult[i].sourceLabel+int2type(refResult[i].relationType)+refResult[i].targetLabel);
				}
		}
	}
	
	public void getPrecision()
	{	
		if (conjunNum==0 && lilyNum==0){
			if (refNum==0){
				p=1.0;
			}
			else{
				p=0.0;
			}
			
		}
		else{
			p = (double)conjunNum/(double)lilyNum;
		}		
	}
	
	public void getRecall()
	{
		if (refNum==0 && conjunNum==0){
			r=1.0;
		}
		else if (refNum==0 && conjunNum!=0)
		{
			r = (double)conjunNum/(double)refNum;
		}
		else{
			r = (double)conjunNum/(double)refNum;
		}		
	}
	
	public void getF1Measure()
	{
		f1 = (2.0*p*r)/(p+r);
	}
	
	public void show()
	{
		System.out.println("Precision:"+p);
		System.out.println("Recall:"+r);
		System.out.println("F1Measure:"+f1);
	}
	public String int2type(int t)
	{
		if (t==0) {return "=";}
		else if (t==1) {return ">";}
		else if (t==2) {return "<";}
		else return "";
	}
	

}

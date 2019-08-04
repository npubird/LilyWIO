/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-4-26
 * Filename          StableMarriageFilter.java
 * Version           2.0
 * 
 * Last modified on  2007-4-26
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 *
 ***********************************************/
package lily.tool.filter;


import java.util.*;
import lily.tool.stablemarriage.*;
/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-4-26
 * 
 * describe:
 * ��stable marriage����������ͼƥ��Ľ��
 ********************/
public class StableMarriageFilter {
	public double[][] run(double[][] sim, int Mnum, int Wnum) 
	{
		boolean change=false;
		StableMarriage s = new StableMarriage();
		
		//ÿ�дӴ�С����,�������һ��LinkedList
		LinkedList[] menPre = new LinkedList[Mnum];
		for (int i = 0;i<Mnum;i++)
		{
			double[] array = new double[Wnum];
			System.arraycopy( sim[i],0,array,0,sim[i].length);//����һ��
			this.Sort(array,Wnum);//����
			//����LinkedList
			menPre[i] = new LinkedList();
			for(int k=0;k<Wnum;k++)
			{
				for (int j=0;j<Wnum;j++)
				{
					if (array[k]==sim[i][j] && !menPre[i].contains(j))
					{
						//��¼LinkedList
						menPre[i].add(j);
						break;
					}
				}
			}
		}
		
		//ÿ�дӴ�С����,�������һ��LinkedList
		LinkedList[] womenPre = new LinkedList[Wnum];
		for (int i = 0;i<Wnum;i++)
		{
			double[] array = new double[Mnum];
			for (int j=0;j<Mnum;j++) {array[j] = sim[j][i];}//����һ��
			
			this.Sort(array,Mnum);//����
			
			//����LinkedList
			womenPre[i] = new LinkedList();
			for(int k=0;k<Mnum;k++)
			{
				for (int j=0;j<Mnum;j++)
				{
					if (array[k]==sim[j][i] && !womenPre[i].contains(j))
					{
						//��¼LinkedList
						womenPre[i].add(j);
						break;
					}
				}
			}
		}
		
		//��С����ΪMen,������ΪWomen.ȱʡ��MenΪ��,WomenΪ��
		
		//���Men>Women,�ı�����
		change = (Mnum>Wnum);
			
		if (!change)
		{//û�иı�����
			//��ʼ��Men��Women������
			s.setnumber(Mnum,Wnum);
			//��Prefer����
			s.setpreferences(menPre,womenPre);
		}
		else
		{//�ı�������
			//����Men��Women
			//��ʼ��Men��Women������
			s.setnumber(Wnum,Mnum);
			
			//��Prefer����
			s.setpreferences(womenPre,menPre);
		}
		
		//��ʼѡ��
		s.run();
		
		int[] map = new int[Math.min(Wnum,Mnum)];
		
		//��ý��
		map = s.getresult();
		
		//������
		if(!change)
		{
			//��û�л����д���
			for (int i = 0;i<Mnum;i++)
			{
				for (int j=0;j<Wnum;j++)
				{
					if (j!=map[i])
					{
						sim[i][j] = 0;
					}
				}
			}
		}
		else
		{
			//�������к���
			for (int col = 0;col<Wnum;col++)
			{
				for (int row=0;row<Mnum;row++)
				{
					if (row!=map[col])
					{
						sim[row][col] = 0;
					}
				}
			}
		}
		
		return sim;
	}
	
	public  void Sort(double[] pData, int Num) {
		int i, j;
		int itemp;
		double max;
		
		for (i=0;i<Num-1;i++)
		{
			max = pData[i];
			itemp = i;
			for (j=i+1;j<Num;j++)
			{
				if (pData[j]>max)
				{
					itemp=j;
					max = pData[j];
				}
			}
			if (itemp!=i)
			{
				pData[itemp]=pData[i];
				pData[i]=max;
			}
		}
	}
}

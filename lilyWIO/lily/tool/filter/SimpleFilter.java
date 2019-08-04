/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-4-27
 * Filename          SimpleFilter.java
 * Version           2.0
 * 
 * Last modified on  2007-4-27
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 * 一般的相似矩阵结果过滤方法
 ***********************************************/
package lily.tool.filter;

import java.util.*;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-4-27
 * 
 * describe:
 * 结果过滤
 ********************/
public class SimpleFilter {
	//普通的只取最大值的结果过滤
	//只取大于阀值的行列上的最大值
	//需要排序来做辅助
	public double[][] maxValueFilter(int n,int m,double[][] sim,double threshold)
	{
		int i,j,k;
		int maxNum = 0;
		int count = 0;
		double[][] simFilter= new double[n][m];
		double[] row = new double[n*m];
		int[] matchA,matchB;
		boolean BREAK;
		
		//结果放入一个数组中
		for (i=0;i<n;i++)
			for (j=0;j<m;j++)
			{
				row[count] = sim[i][j];
				count++;
			}
		
		Arrays.sort(row);//排序
		maxNum = Math.max(n,m);
		matchA= new int[maxNum];
		matchB= new int[maxNum];
		for (i=0;i<maxNum;i++){matchA[i]=-1;matchB[i]=-1;}
				
		k = n*m-1;
		while(k>=0 && row[k]>0 && row[k]>threshold)
		{
			BREAK = false;
			for (i=0;i<n;i++)
			{
				if (BREAK) break;
				for (j=0;j<m;j++)
				{
					if ((sim[i][j]==row[k]) && (matchA[i]==-1) && (matchB[j]==-1))
					{
						matchA[i]=j;
						matchB[j]=i;
						simFilter[i][j]=row[k];
						
						BREAK = true;
						break;
					}
				}
			}
			k--;
		}
		return simFilter; 
	}
}

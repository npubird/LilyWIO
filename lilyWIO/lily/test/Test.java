/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-4-27
 * Filename          Test.java
 * Version           2.0
 * 
 * Last modified on  2007-4-27
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 * 内部的测试用
 ***********************************************/
package lily.test;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-4-27
 * 
 * describe:
 * 
 ********************/
public class Test {

	/****************
	 * 强化相似矩阵
	 ****************/
	public void enSimMatrix(double[][] sim, int m, int n){
		double[] avgrow=new double[m];
		double[] avgcol=new double[n];
		double[][] matrix=new double[m][n];
		
		matrix=(double[][])sim.clone();
		for (int i=0;i<m;i++){
			if (matrix[i]!=null){
				matrix[i]=(double[])sim[i].clone();
			}
		}
		
		/*求行，列的均值*/
		for (int i=0;i<m;i++){
			avgrow[i]=0;
			for (int j=0;j<n;j++){
				avgrow[i]=avgrow[i]+sim[i][j];
			}
//			avgrow[i]=avgrow[i]/(double)n;
		}
		for (int i=0;i<n;i++){
			avgcol[i]=0;
			for (int j=0;j<m;j++){
				avgcol[i]=avgcol[i]+sim[j][i];
			}
//			avgcol[i]=avgcol[i]/(double)m;
		}
		
		double delta=0;
		double h=0;
		for (int i=0;i<m;i++){
			for (int j=0;j<n;j++){
				/*计算delta*/
				h=(avgrow[i]+avgcol[j]-sim[i][j])/(double)(m+n-1);
				delta=sim[i][j]-h;
				sim[i][j]=sim[i][j]+delta*0.5;
				if (sim[i][j]>1.0){sim[i][j]=1.0;}
				if (sim[i][j]<0.0){sim[i][j]=0.0;}
			}
		}		
	}
}

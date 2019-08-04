/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-4-26
 * Filename          MapResult.java
 * Version           2.0
 * 
 * Last modified on  2007-4-26
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 * ӳ���������ݽṹ
 ***********************************************/
package lily.tool.datastructure;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-4-26
 * 
 * describe:
 * ӳ�������ݽṹ
 ********************/
public class MapRecord {

	public String sourceLabel;
	public double similarity;
	public String targetLabel;
	public int relationType;
	
	public MapRecord()
	{
		//��ʼ��
		similarity = -1;
		relationType = -1;
	}
	
	public void show()
	{
		char mptype[]={'=','>','<'};
		System.out.println(sourceLabel+mptype[relationType]+targetLabel);
		System.out.println("\t"+similarity);
	}

}

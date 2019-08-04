/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-7-21
 * Filename          PairGraphNode.java
 * Version           2.0
 * 
 * Last modified on  2007-7-21
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 *
 ***********************************************/
package lily.tool.datastructure;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-7-21
 * 
 * describe:
 * triple-pair graph������ݽṹ
 ********************/
public class PairGraphRes {
	public Object resA;
	public Object resB;
	public double sim0;
	public double simk;
	public double simr;//δ��һ������ʵ���ƶ�
	public boolean isMeta;
	public boolean cFlag=false;
	public String getString(){
		return (resA.toString()+resB.toString());
	}
}

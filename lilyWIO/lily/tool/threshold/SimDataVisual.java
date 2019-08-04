/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-6-23
 * Filename          SimDataVisual.java
 * Version           2.0
 * 
 * Last modified on  2007-6-23
 *               by  Peng Wang, Keyue Li
 * -----------------------
 * Functions describe:
 * ��ʾ���ƶ����ݵ�ͼ������
 ***********************************************/
   
package lily.tool.threshold;

import javax.swing.*;
import java.awt.*;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-6-23
 * 
 * describe:
 * ��ʾ���ƶ����ݵ�ͼ������
 * 
 ********************/
public class SimDataVisual extends JFrame{
	private static final long serialVersionUID = 1L;
	public double[][] matrix;
	private int mNum;
	private int nNum;
	public SimDataVisual() {
		this.setSize(650, 700); // ����Ĵ�С
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);// ���岻�ܸı��С
		this.setTitle("similarity matrix"); // ���ñ���
		JPanel contentPanel = new JPanel();
		contentPanel.setBackground(Color.white);
		contentPanel.setPreferredSize(new Dimension(650, 700));
		this.getContentPane().add(contentPanel, BorderLayout.CENTER);
	}
	
	public void paint(Graphics g) {
		int[] hg=new int[256];
		/*�������ƾ���*/
		super.paint(g);
		for (int i=0;i<mNum;i++){
			for (int j=0;j<nNum;j++){
				/*ӳ�����ƶ�[0-1]���Ҷ�[0-255]*/
				double gray=matrix[i][j]*255.0;
				if (gray>255){gray=255;}
				hg[(int)gray]++;
				/*��Ӧ����ɫ*/
				Color c=new Color((int)gray,(int)gray,(int)gray);
//				g.setColor(Color.getHSBColor(0, (float) 0.7, (float)(1.0-matrix[i][j])));
				g.setColor(c);
				g.fillRect(20+j*6, 40+i*6, 6, 6);
			}
		}
		/*����ֱ��ͼ*/
		/*����*/
		g.setColor(Color.BLACK);
		g.drawLine(20,640,20+280,640);
		g.drawLine(20,640,20,640-150);
		for (int i=1;i<256;i++){
			/*ֱ��ͼ*/
			if (hg[i]>50){hg[i]=50;}
			g.drawLine(20+i,640,20+i,640-hg[i]*3);
		}
	}
	
	public void visualize(double[][] sim, int m, int n) {
		mNum=m;
		nNum=n;
		matrix=new double[m][n];
		matrix=(double[][])sim.clone();
		for (int i=0;i<m;i++){
			if (matrix[i]!=null){
				matrix[i]=(double[])sim[i].clone();
			}
		}
//		SimDataVisual kyodaiUI = new SimDataVisual();
//		kyodaiUI.show();
		this.show();
	}
}

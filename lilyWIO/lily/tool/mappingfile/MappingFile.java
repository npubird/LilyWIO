/************************************************
 * Source code information
 * -----------------------
 * Original author	 Peng Wang, School of Computer Science & Eng., Southeast University
 * Author email      pwangseu@gmail.com
 * Web               http://ontomapping.googlepages.com
 * Created			 2007-4-27
 * Filename          MappingFile.java
 * Version           2.0
 * 
 * Last modified on  2007-4-27
 *               by  Peng Wang
 * -----------------------
 * Functions describe:
 * ����ӳ���ļ���صĲ���
 ***********************************************/
package lily.tool.mappingfile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.BufferedWriter; 
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import org.dom4j.*;
import org.dom4j.io.*;
import org.dom4j.tree.DefaultNamespace;


import lily.tool.datastructure.*;

/*********************
 * Class information
 * -------------------
 * @author Peng Wang
 * @date   2007-4-27
 * 
 * describe:
 * ӳ�����ļ�����
 ********************/
public class MappingFile {
	public String s_baseURI;
	public String t_baseURI;
	public String s_ontName;
	public String t_ontName;
	public String sType[]={"=",">","<"};
	
	public void setBaseURI(String urlA,String urlB){
		this.s_baseURI=urlA;
		this.t_baseURI=urlB;
		if (s_baseURI.length()>0){
			s_ontName=s_baseURI.substring(0,s_baseURI.length()-1);
		}
		else{
			s_ontName="";
		}
		if (t_baseURI.length()>0){
			t_ontName=t_baseURI.substring(0,t_baseURI.length()-1);
		}
		else{
			t_ontName="";
		}
	}
	
	public void save2txt(String sourceOnt, String targetOnt, int mapNum, MapRecord[] map,String lilyFileName)
	{
		//���Ŀ���ļ��е�·��
		String filePath=getOntPath(targetOnt);
		//�����ļ�
		try {
			FileWriter out = new FileWriter(filePath+lilyFileName);
			BufferedWriter bw = new BufferedWriter(out);
		//д���ļ�
			bw.write("Source:");
			bw.newLine();
			bw.write(sourceOnt);
			bw.newLine();
			bw.write("Target:");
			bw.newLine();
			bw.write(targetOnt);
			bw.newLine();
			bw.write("Correspondences");
			bw.newLine();
			for(int i=0;i<mapNum;i++)
			{
				bw.write(map[i].sourceLabel+sType[map[i].relationType]
						 +map[i].targetLabel);
				bw.newLine();
				bw.write(String.valueOf(map[i].similarity));
				bw.newLine();
			}
			
		//�ر��ļ�
			bw.close();
			out.close();
		} catch (IOException e) {
   		System.err.println("Can't open result file:\n" + e.toString());
   		System.exit(1);
		}
	}
	
	/**********************
	 * ��*.rdf��ʽд���׼ӳ����
	 *********************/
	public void save2rdf(String sourceOnt, String targetOnt, int mapNum, MapRecord[] map, String lilyFileName)
	{
		//���Ŀ���ļ��е�·��
		String filePath=getOntPath(targetOnt);
		
		Document document = DocumentHelper.createDocument();
				
		/*���ڵ�*/
		Element rootElement = document.addElement("rdf:RDF");
        Namespace spaceDefault = new DefaultNamespace("", "http://knowledgeweb.semanticweb.org/heterogeneity/alignment");
        Namespace spaceRdf = new DefaultNamespace("rdf","http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        Namespace spaceXsd = new DefaultNamespace("xsd","http://www.w3.org/2001/XMLSchema#");
        rootElement.add(spaceDefault);
        rootElement.add(spaceRdf);
        rootElement.add(spaceXsd);
		
		Element alignmentElement = rootElement.addElement("Alignment","http://knowledgeweb.semanticweb.org/heterogeneity/alignment");
		Element xmlElement = alignmentElement.addElement("xml");
		xmlElement.setText("yes");
		Element levelElement = alignmentElement.addElement("level");
		levelElement.setText("0");
		Element typeElement = alignmentElement.addElement("type");
		typeElement.setText("11");
		Element onto1Element = alignmentElement.addElement("onto1");
		onto1Element.setText(s_ontName);
		Element onto2Element = alignmentElement.addElement("onto2");
		onto2Element.setText(t_ontName);
		Element uri1Element = alignmentElement.addElement("uri1");
		uri1Element.setText(s_ontName);
		Element uri2Element = alignmentElement.addElement("uri2");
		uri2Element.setText(t_ontName);
		
		for(int i=0;i<mapNum;i++)
		{
			Element mapElement = alignmentElement.addElement("map");
			Element cellElement = mapElement.addElement("Cell");
			Element entity1Element = cellElement.addElement("entity1");
			entity1Element.addAttribute("rdf:resource",s_baseURI+map[i].sourceLabel);
			Element entity2Element = cellElement.addElement("entity2");
			entity2Element.addAttribute("rdf:resource",t_baseURI+map[i].targetLabel);
			Element measureElement = cellElement.addElement("measure");
			measureElement.addAttribute("rdf:datatype","http://www.w3.org/2001/XMLSchema#float");
			measureElement.setText(String.valueOf(map[i].similarity));//��ʵ�����ƶ�
			measureElement.setText(String.valueOf(1.0));//ǿ��Ϊ���ƶ�1.0
			Element relationElement = cellElement.addElement("relation");
			relationElement.setText(sType[map[i].relationType]);
		}
		try {
			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			XMLWriter output = new XMLWriter(new FileOutputStream(new File(filePath+lilyFileName)),format);
			output.write(document);
			output.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	
	/**********************
	 * ��*.rdf�����׼ӳ����
	 * ����Dom4j����xml�ĵ�
	 *********************/
	@SuppressWarnings("unchecked")
	public ArrayList read4xml(String filePath)throws MalformedURLException, DocumentException 
	{
		ArrayList recordList = new ArrayList();
		int recordNum = 0;
		
		//��ȡXML�ĵ�
		SAXReader reader = new SAXReader();
		Document doc = reader.read(new File(filePath));
		//ȡ��root�ڵ�
		Element root = doc.getRootElement();
		//����XML��
		//ö�������ӽڵ㣬ֱ���ҵ�map�ڵ�
		for(Iterator i1=root.elementIterator("Alignment");i1.hasNext(); )
		{
			Element alignment = (Element)i1.next();
			//�ٴ�Alignment����ȥ��map
			for(Iterator i2=alignment.elementIterator("map");i2.hasNext();)
			{
				Element map = (Element)i2.next();
				//�ٴ�map����ȥ��Cell
				for(Iterator i3=map.elementIterator("Cell");i3.hasNext();)
				{
					Element cell = (Element)i3.next();
					MapRecord mapRecord = new MapRecord();
					//�õ�ÿ��Cell�µľ�������
					for(Iterator i4=cell.elementIterator();i4.hasNext();)
					{
						Element record = (Element)i4.next();
						String s;
						//��ȡsourceLabel
						if(record.getName().equals("entity1"))
						{
							s = record.attributeValue("resource");
							s = this.getLocalName(s);
							mapRecord.sourceLabel=new String(s);
						}
						//��ȡtargetLabel
						if(record.getName().equals("entity2"))
						{
							s = record.attributeValue("resource");
							s = this.getLocalName(s);
							mapRecord.targetLabel=new String(s);
						}
						//��ȡmeasure
						if(record.getName().equals("measure"))
						{
							s = record.getStringValue();
							mapRecord.similarity=Double.parseDouble(s);
						}
						//��ȡrelation
						if(record.getName().equals("relation"))
						{
							s = record.getStringValue();
							mapRecord.relationType=this.type2int(s);
						}
					}
					recordList.add(recordNum,mapRecord);
					recordNum++;
				}
			}
		}
		//���沢���صõ��Ľ��
		ArrayList result = new ArrayList();
		result.add(0,recordNum);
		result.add(1,recordList);
		
		return result;
	}
	
	public String getOntPath(String ontFile)
	{
		String path="";
		int pos = ontFile.lastIndexOf('/');
		if (pos>0)
		{
			path=ontFile.substring(0,pos+1);
		}
		return path;
	}
	
	public String getLocalName(String s)
	{
		s=s.substring(s.lastIndexOf('#')+1,s.length());
		return s;
	}
	
	public int type2int(String s)
	{
		if (s.equals("=")) {return 0;}
		else if (s.equals(">")) {return 1;}
		else if (s.equals("<")) {return 2;}
		else return -1;
	}
}

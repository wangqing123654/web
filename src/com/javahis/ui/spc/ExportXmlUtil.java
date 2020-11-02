package com.javahis.ui.spc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import com.dongyang.control.TControl;

public class ExportXmlUtil extends TControl {

	private static final ExportXmlUtil INSTANCE =new ExportXmlUtil();
	
	public ExportXmlUtil(){	
	}
	
	public static ExportXmlUtil getInstance(){
	     return INSTANCE;
	}
	
	/**
     * ����Xml�ļ�
     * @param headerDate String
     * @param mainDate TParm
     * @param fileDefName Ĭ���ļ�����
     */

    public static void exeSaveXml(Document doc,String fileDefName) {
        
        //��׼SWING
        JFileChooser chooser = new JFileChooser();
        //���õ�ǰ��Ŀ¼��Ŀǰд����
        File dir= new File("C:\\JavaHis\\XML");
        if(!dir.exists())
            dir.mkdirs();
        chooser.setCurrentDirectory(dir);

        //Ĭ�ϵ��ļ���������
        chooser.setSelectedFile(new File(fileDefName));//�ṩĬ����
        //���öԻ���ı���
        chooser.setDialogTitle("���ɶ�������");
        //���ù��ˣ���չ����
        chooser.setFileFilter(new FileFilter() {
            public boolean accept(File f) {
                return f.getName().toUpperCase().endsWith(".xml");
            }

            public String getDescription() {
                return "XML (*.xml)";
            }
        });

        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            writeFile(doc, path);
        }
    }
    
    /**
	 * дXML�ļ���ָ����·����
	 * @param document    
	 * @param path        ����·��
	 */
	public static  void writeFile(Document document,String fileName){
		XMLWriter output = null ;
		//����һ���ļ�����
        File file;
		try{
			//��·���а���.xls�Ͳ��Ӻ�׺
            if (!fileName.contains(".xml")) {
                file = new File(fileName + ".xml");
            } else {
                file = new File(fileName);
            }
			
			//�жϸ��ļ��Ƿ���ڣ�����Ѿ�������ʾ�Ƿ񸲸ǣ������Ǿ��˳�������
            if (file.exists() &&
                JOptionPane.showConfirmDialog(null, "����", "���ļ��Ѿ�����,�Ƿ񸲸ǣ�",
                                              JOptionPane.YES_NO_OPTION) == 1)
                return;
			//output = new XMLWriter(new FileWriter( file)); //�����ĵ�
            OutputFormat formet =OutputFormat.createPrettyPrint();//���ø�ʽ�������  
            formet.setEncoding("UTF-8");  
			//���ֽ����ķ�ʽ������ļ�
			output = new XMLWriter(new FileOutputStream(fileName),formet);
			output.write(document);
            //output.write( document );
            JOptionPane.showMessageDialog(null, "���ɶ����ɹ�");
        }catch(IOException e){
        	System.out.println(e.getMessage());
        	JOptionPane.showMessageDialog(null, "���ɶ���ʧ��");
        }finally{
        	if(output != null ){
	        	try {
	        		
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        	}
        }
    }
	
	/**
	 * ����xml�ַ����õ�Document����
	 * 
	 * @param srcXml
	 * @return
	 */
	public static Document getDocument(String srcXml) {
		Document document = null;
		try {
			document = DocumentHelper.parseText(srcXml);
		} catch (DocumentException e) {
			 
			e.printStackTrace();
		}
		return document;
	}

	/**
	 * ��ȡ���ڵ�
	 * 
	 * @param document
	 * @return
	 */
	public static Element getRootElement(Document document) {
		Element element = document.getRootElement();
		return element;
	}

	/**
	 * ��ȡ���ڵ����ӽڵ��б�
	 * 
	 * @param rootElement
	 *            ���ڵ�
	 * @param nodeName
	 *            �ڵ�����
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Iterator<Element> getSubRootElement(Element rootElement,
			String nodeName) {
		Iterator<Element> iterator = rootElement.elementIterator(nodeName);
		return iterator;
	}

	/**
	 * ����dom����
	 * 
	 * @return
	 */
	public static Document createDocument() {
		return DocumentHelper.createDocument();
	}

	/**
	 * �������ڵ�
	 * 
	 * @param document
	 * @param rootName
	 * @return
	 */
	public static Element createElementRoot(Document document, String rootName) {
		Element rootElement = document.addElement(rootName);
		return rootElement;
	}

	/**
	 * �������ڵ��µ��ӽڵ�
	 * 
	 * @param element
	 * @param nodeName
	 * @return
	 */
	public static Element createElementSub(Element element, String nodeName) {
		if (element != null) {
			return element.addElement(nodeName);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getNodeAttributeMap(String xpath,
			Document document) {
		Map<String, Object> map = new HashMap<String, Object>();
		List list = document.selectNodes(xpath);
		for (int i = 0; i < list.size(); i++) {
			Element element = (Element) list.get(i);
			Iterator it = element.elementIterator();
			while (it.hasNext()) {
				Element elt = (Element) it.next();
				List<Attribute> attrs = elt.attributes();
				for (Attribute attr : attrs) {
					map.put(attr.getName(), attr.getValue());
				}
			}
		}
		return map;
	}

	/**
	 * �����ڵ�����
	 * 
	 * @param element
	 * @param attributeName
	 * @param attributeValue
	 */
	public static void createElementAttribute(Element element,
			String attributeName, String attributeValue) {
		element.addAttribute(attributeName, attributeValue);
	}

	public static Document createXml(List list){
	 	Document doc = DocumentHelper.createDocument() ;
        Element root = doc.addElement("dw_gyyhd_detail");
        
        for(int i = 0 ; i < list.size() ; i++){
        	Map<String, Object> map = (Map<String, Object>)list.get(i);
	        Element detailRow = root.addElement("dw_gyyhd_detail_row");
	        for (Map.Entry<String, Object> entry : map.entrySet()) {
				Object value =  (Object) entry.getValue();
				Element d = detailRow.addElement(entry.getKey());
				d.setText(value+"");
			}
        }
        return doc;
	}
	
	public static void main(String[] args) {

	}
}

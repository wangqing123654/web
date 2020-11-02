package jdo.ind.inf;

import java.util.Iterator;

import jdo.ind.IndSysParmTool;

import org.dom4j.Document;
import org.dom4j.Element;

import com.dongyang.Service.Server;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

public class IndDispensDaoImpl extends TJDOTool {
	
	/**
	 * ʵ��
	 */
	public static IndDispensDaoImpl instanceObject;
	
	/**
	 * �õ�ʵ��
	 * 
	 * @return INDTool
	 */
	public static IndDispensDaoImpl getInstance() {
		if (instanceObject == null)
			instanceObject = new IndDispensDaoImpl();
		return instanceObject;
	}

	/**
	 * ������
	 */
	public IndDispensDaoImpl() {
		onInit();
	}
	
	
	public  String onSave(String xml){
		if(xml == null  || xml.equals("") || xml.equals("null")){
			return  "fail";
		}
		try {
			Document doc = XmlUtil.getDocument(xml);
			Element rootElement = XmlUtil.getRootElement(doc);

			Element indmElement = rootElement.element("INDM");
			TParm parm = new TParm();
			// ����
			TParm parmM = new TParm();
			for (Iterator it = indmElement.elementIterator(); it.hasNext();) {
				Element child = (Element) it.next();
				String name = child.getName(); // ��ȡ�ڵ������
				String text = child.getText(); // ��ȡ�ڵ������
				//System.out.println("�ڵ�����" + name + "     ���ݣ�" + text);
				if(name != null && name.endsWith("DATE")){
					if(text != null && !text.equals("")){
						text = text.substring(0,text.length()-2);
						parmM.setData(name, StringTool.getTimestamp(text, "yyyy-MM-dd HH:mm:ss"));
					}else{
						parmM.setData(name, text);
					}
				}else{
					parmM.setData(name, text);
				}
			}
	        // ��λ���� // ���뵥����
			parm.setData("REQTYPE_CODE",parmM.getValue("REQTYPE_CODE"));
			parm.setData("ORG_CODE",parmM.getValue("TO_ORG_CODE"));
			parm.setData("UNIT_TYPE",parmM.getValue("UNIT_TYPE"));
			parm.setData("OUT_M", parmM.getData());
						 
			// �ӱ�
			TParm parmD = new TParm();
			
			Element inddElement = rootElement.element("INDDS");
			Iterator iters  = inddElement.elementIterator("INDD");
			
			while(iters.hasNext()){
				Element child = (Element) iters.next();
				
				//�õ��ӽڵ���������� 
				Iterator childIterator = child.elementIterator() ;
				int count = 0;
				while(childIterator.hasNext()){
				
					Element subChild = (Element)childIterator.next() ;
					
					String name = subChild.getName(); // ��ȡ�ڵ������
					String text = subChild.getText(); // ��ȡ�ڵ������
					//System.out.println("�ڵ�����" + name + "     ���ݣ�" + text);
					
					if(name != null && name.endsWith("DATE")){
						if(text != null && !text.equals("")){
							text = text.substring(0,text.length()-2);
							parmD.setData(name, count, StringTool.getTimestamp(text, "yyyy-MM-dd HH:mm:ss"));
						}else{
							parmD.setData(name, count,text);
						}
					}else{
						parmD.setData(name, count,text);
					}
					count++;
				}
			}
			if (parmD != null) {
				parm.setData("OUT_D", parmD.getData());
			}
			
			System.out.println("parm---:" + parm);
			Server.autoInit(this);
			
	        TParm sysParm = getSysParm() ;
	     // �������ҵ״̬�ж�(1-���ȷ�ϣ�2-���⼴���)
	        String dis_check = getDisCheckFlg(sysParm,parmM.getValue("TO_ORG_CODE"),parmM.getValue("APP_ORG_CODE"));
	        
	        if ("1".equals(dis_check)) {
		        parm = Server.executeAction("action.ind.INDDispenseAction",
		                "onInsertOutOn", parm);
	        }else{
	        	// ִ����������
	            parm = Server.executeAction("action.ind.INDDispenseAction",
	                                                "onInsertOutIn", parm);
	        }

			// �ж��Ƿ�ɹ�
			if (parm == null || parm.getErrCode() < 0) {
				return "fail";
			}

		} catch (Exception e) {
			// TODO: handle exceptions
			return "fail";
		}

		return "success";
	}
	
	/**
     * ҩ�������Ϣ
     * @return TParm
     */
    private TParm getSysParm() {
        return IndSysParmTool.getInstance().onQuery();
    }
    
    /**
     * �������ҵ״̬�ж�
     *
     * @return
     */
    private String getDisCheckFlg(TParm parm,String out_org_code,String in_org_code) {
        // �������ҵ״̬�ж�
        if ("Y".equals(parm.getValue("DISCHECK_FLG", 0))
            && !"".equals(out_org_code) && !"".equals(in_org_code)) {
            // ��������ȷ���������뵥״̬����ⲿ�ŽԲ�Ϊ��-->��;״̬
            return "1";
        }
        else if ("N".equals(parm.getValue("DISCHECK_FLG", 0))
                 && !"".equals(out_org_code) && !"".equals(in_org_code)) {
            // ����������ȷ���������뵥״̬����ⲿ�ŽԲ�Ϊ��-->���⼴���
            return "2";
        }
        return "1";
    }
    

}

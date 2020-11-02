package com.javahis.bsm;
/**
*
* <p>Title: xml������</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.05.14 
* @version 4.0
*/
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXB;
import javax.xml.bind.Marshaller;

import jdo.sys.Pat;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
public class XmlUtils {
	/**
	 * ������ʵ�֣�����������ת��Ϊxml������201����ʱʹ�ã���ҩ��
	 * @return 
	 */

	public static  StringWriter  onCreateXmlDispense(TParm parm){
		
		Pat pat = Pat.onQueryByMrNo(((TParm)parm.getData("Data", "MAIN")).getValue("MR_NO"));
		Prescription prescription = new Prescription() ; //��������
		//���ñ�ͷ
		 prescription.setOPIP(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_TERM")) ;         //����ip
		 prescription.setOPMANNAME(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_USER")) ;   //����name
		 prescription.setOPMANNO(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_CODE")) ;     //������Ա����
		 prescription.setOPTYPE(((TParm)parm.getData("Data", "TITLE")).getValue("WAY")) ;           //��������
		 prescription.setOPWINID(((TParm)parm.getData("Data", "TITLE")).getValue("OPWINID")) ;     //�����ն�,���ں�
		 //���ô�������
		 List<PrescriptionTableMain> listMain  = prescription.getMain() ;
		 PrescriptionTableMain main = new PrescriptionTableMain() ;
		 main.setPRESC_DATE(((TParm)parm.getData("Data", "DETAIL")).getValue("ORDER_DATE",0).substring(0, 19)) ;//����ʱ��
		 main.setPRESC_NO(((TParm)parm.getData("Data", "MAIN")).getValue("RX_NO")) ;   //�������
		 main.setRCPT_INFO("") ;//�����Ϣ???????????????????????????
		 main.setPRESCRIBED_BY(((TParm)parm.getData("Data", "MAIN")).getValue("DR_NAME")) ;//����ҽ��
		 main.setPRESCRIBED_ID(((TParm)parm.getData("Data", "MAIN")).getValue("DR_CODE")) ;//����ҽ������
		 main.setCHARGE_TYPE("") ;//  ҽ������====?????????????????????????
		 main.setCOSTS(((TParm)parm.getData("Data", "MAIN")).getDouble("SUM_FEE")+"") ;       //����
		 main.setDATE_OF_BIRTH(pat.getBirthday().toString()) ;//���߳�������
		 main.setDISPENSARY("�ż���ҩ��") ;//��ҩҩ��
		 main.setDISPENSE_PRI("") ;//��ҩ���ȼ�?????????????????????/////
		 main.setENTERED_BY(((TParm)parm.getData("Data", "MAIN")).getValue("DR_NAME")) ;//¼����
		 main.setORDERED_BY(((TParm)parm.getData("Data", "MAIN")).getValue("DEPT_DESC")) ;//��������
		 main.setORDERED_ID(((TParm)parm.getData("Data", "MAIN")).getValue("DEPT_CODE")) ;//�������Ҵ���
		 main.setPATIENT_ID(((TParm)parm.getData("Data", "MAIN")).getValue("MR_NO")) ;//���￨��
		 main.setPATIENT_NAME(((TParm)parm.getData("Data", "MAIN")).getValue("PAT_NAME")) ;//��������
		 main.setPATIENT_TYPE("00") ;//��������
		 main.setPAYMENTS(((TParm)parm.getData("Data", "MAIN")).getDouble("SUM_FEE")+"");//ʵ������
		 main.setPRESC_ATTR("") ;//��������?????????????????????????????????????????
		 main.setPRESC_IDENTITY(((TParm)parm.getData("Data", "DETAIL")).getValue("CTZ1_CODE",0)) ;//�������
		 main.setPRESC_INFO(onRxtype(((TParm)parm.getData("Data", "MAIN")).getValue("RX_TYPE"))) ;//��������(�Ʒѷ�ʽ)
		 main.setRCPT_REMARK("") ;//������ע???????????????????????????????
		 main.setREPETITION("") ;//����      ?????????????????????????????????
		 main.setSEX(pat.getSexString()) ;//�����Ա�
		 listMain.add(main) ;
		 //���ô���ϸ��
		 List<PrescriptionTableDetail> detailList = main.getDetail() ;
		 PrescriptionTableDetail detail = null  ;
		 for(int i=0;i<((TParm)parm.getData("Data", "DETAIL")).getCount();i++){
			 detail = new PrescriptionTableDetail() ;
			 detail.setADMINISTRATION(((TParm)parm.getData("Data", "DETAIL")).getValue("ROUTE_CODE", i)) ;//ҩƷ�÷�
			 detail.setADVICE_CODE(((TParm)parm.getData("Data", "DETAIL")).getValue("SEQ_NO", i)) ;//ҽ�����
			 detail.setCOSTS(((TParm)parm.getData("Data", "DETAIL")).getDouble("OWN_AMT", i)+"") ;//����
			 detail.setDOSAGE(((TParm)parm.getData("Data", "DETAIL")).getValue("MEDI_QTY", i)) ;//ҩƷ����
			 detail.setDOSAGE_UNITS(((TParm)parm.getData("Data", "DETAIL")).getValue("MEDI_UNIT", i)) ;//������λ
			 detail.setDRUG_CODE(((TParm)parm.getData("Data", "DETAIL")).getValue("BAR_CODE", i)) ;//ҩƷ���HIS
			 detail.setBAT_CODE(((TParm)parm.getData("Data", "DETAIL")).getValue("ORDER_CODE", i)) ;//ҩƷ���
			 detail.setDRUG_NAME(((TParm)parm.getData("Data", "DETAIL")).getValue("ORDER_DESC", i)) ;//ҩƷ����
			 detail.setFLG(((TParm)parm.getData("Data", "DETAIL")).getValue("FLG", i)) ;//��ҩ���Ƿ��ҩ���
			 detail.setDRUG_PACKAGE(((TParm)parm.getData("Data", "DETAIL")).getValue("SPECIFICATION", i)) ;//ҩƷ��װ���
			 detail.setDRUG_PRICE(((TParm)parm.getData("Data", "DETAIL")).getDouble("OWN_PRICE", i)+"") ;//ҩƷ�۸�
			 detail.setDRUG_SPEC(((TParm)parm.getData("Data", "DETAIL")).getValue("SPECIFICATION", i)) ;//ҩƷ���
			 detail.setDRUG_UNIT(((TParm)parm.getData("Data", "DETAIL")).getValue("DISPENSE_UNIT", i)) ;//ҩƷ��λ
			 detail.setQUANTITY(((TParm)parm.getData("Data", "DETAIL")).getValue("DISPENSE_QTY", i)) ;//����
			 detail.setPRESC_NO(((TParm)parm.getData("Data", "DETAIL")).getValue("RX_NO", i)) ;///�������
			 detail.setPRESC_DATE(((TParm)parm.getData("Data", "DETAIL")).getValue("ORDER_DATE", i).substring(0, 19)) ;//����ʱ��
			 detail.setPAYMENTS(((TParm)parm.getData("Data", "DETAIL")).getDouble("OWN_AMT", i)+"");//	ʵ������
			 detail.setITEM_NO(((TParm)parm.getData("Data", "DETAIL")).getValue("SEQ_NO", i)) ;//ҩƷ���
			 detail.setFREQUENCY(((TParm)parm.getData("Data", "DETAIL")).getValue("FREQ_DESC", i)) ;//	ҩƷ����
			 detail.setTRADE_NAME(((TParm)parm.getData("Data", "DETAIL")).getValue("GOODS_DESC", i)) ;//ҩƷ��Ʒ��
			 detail.setFIRM_ID(((TParm)parm.getData("Data", "DETAIL")).getValue("FIRM_ID", i)) ;//ҩƷ����
			 detail.setDISPENSE_DAYS(((TParm)parm.getData("Data", "DETAIL")).getValue("TAKE_DAYS", i)) ;   //��ҩ����
			 detail.setDISPENSEDTOTALDOSE(((TParm)parm.getData("Data", "DETAIL")).getValue("DISPENSE_QTY", i)) ;//��ҩ������
			 detail.setDISPENSEDUNIT(((TParm)parm.getData("Data", "DETAIL")).getValue("DISPENSE_UNIT", i)) ;//��ҩ��λ
			 detail.setAMOUNT_PER_PACKAGE(((TParm)parm.getData("Data", "DETAIL")).getValue("QTY_BUONE", i));//һƬ�ļ��� (��ֵ)
			double qty = Double.parseDouble(((TParm)parm.getData("Data", "DETAIL")).getValue("MEDI_QTY", i));
			double qtybyone = Double.parseDouble(((TParm)parm.getData("Data", "DETAIL")).getValue("QTY_BUONE", i));
			 detail.setDISPESEDDOSE(qty/qtybyone+"");//һ����İ�ҩ��
			 detail.setFREQ_DESC_DETAIL(((TParm)parm.getData("Data", "DETAIL")).getValue("FREQ_DESC_DETAIL", i)) ;//����ʱ��
			 detail.setFREQ_DESC_DETAIL_CODE(((TParm)parm.getData("Data", "DETAIL")).getValue("FREQ_DESC_DETAIL_CODE", i)) ;//����ʱ�����
			 detail.setATC_FLG(((TParm)parm.getData("Data", "DETAIL")).getValue("ATC_FLG", i)) ;//�����Ƿ��Ϳڷ���ҩ���ı�־
			 detailList.add(detail) ;
		 } 		
		       StringWriter xml = new StringWriter() ;
		       try {
		    	   JAXB.marshal(prescription, xml); 
		    	   return xml ;
			} catch (Exception e) {
				System.out.println("err===="+e.toString());
			}	            
	            return xml ;	     
	}
	/**
	 * ������ʵ�֣�����������ת��Ϊxml������202��203����ʱʹ��(��ҩ)
	 * @return 
	 */
	public static  StringWriter  onCreateXmlSend(TParm parm){
		Prescription prescription = new Prescription() ; //��������
		//���ñ�ͷ
		 prescription.setOPIP(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_TERM")) ;      //����ip
		 prescription.setOPMANNAME(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_USER")) ; //����name
		 prescription.setOPMANNO(((TParm)parm.getData("Data", "TITLE")).getValue("OPT_CODE")) ;  //������Ա����
		 prescription.setOPTYPE(((TParm)parm.getData("Data", "TITLE")).getValue("WAY")) ;        //��������
		 prescription.setOPWINID(((TParm)parm.getData("Data", "TITLE")).getValue("OPWINID")) ;   //�����ն˴��ں�
		 //���ô�������
		 List<PrescriptionTableMain> listMain  = prescription.getMain() ;
		 PrescriptionTableMain main = null ;
			 main = new PrescriptionTableMain() ;
			 String date = ((TParm)parm.getData("Data", "MAIN")).getValue("PHA_DOSAGE_DATE") ;
			 if(date.length()>0)
			 main.setPRESC_DATE(date.substring(0, 19)) ;//������ҩ���ʱ��
			 else  main.setPRESC_DATE("") ;//������ҩ���ʱ��
			 main.setPRESC_NO(((TParm)parm.getData("Data", "MAIN")).getValue("RX_NO")) ;//�������
			 listMain.add(main) ;
			   StringWriter xml = new StringWriter() ;
			   try {
				   JAXB.marshal(prescription, xml); 
				   return xml ;
			} catch (Exception e) {
				System.out.println("err======"+e.toString());
			}
	            
	            return xml ;	        
	}
	/**
	 * ����xml����xmlת��Ϊ����
	 * @return
	 */
	public static TParm createXmltoParm(String returnString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(returnString) ;
			ReturnBean returnValue = JAXB.unmarshal(read, ReturnBean.class) ;
			result.setErrCode(returnValue.getRETCODE()) ;
			result.setData("RETVAL", returnValue.getRETVAL()) ;
			result.setData("MESSAGE", returnValue.getRETMSG()) ;
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
		}	
		
		return  result ;
	}
	/**
	 * ����xml����xmlת��Ϊ����301������������������xml
	 * @return
	 */
	public static TParm createSendxmltoParm(String sendString){
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			Prescription sendValue = JAXB.unmarshal(read, Prescription.class) ;
			result.setData("PRESC_NO", sendValue.getReturn().get(0).getPRESC_NO()) ;
			result.setData("LOCATION", sendValue.getReturn().get(0).getLOCATION()) ;
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
		}	
		
		return  result ;
	}
	/**
	 * ����xml����xmlת��Ϊ����305������������������xml,���쵥
	 * @return
	 */
	public static TParm createSendxml305toParm(String sendString){
		System.out.println("send=============="+sendString);
		TParm  result = new  TParm() ;
		try {
			StringReader read = new StringReader(sendString) ;
			Prescription sendValue = JAXB.unmarshal(read, Prescription.class) ;
			for(int i=0;i<sendValue.getRequest().size();i++){
				result.addData("ORDER_CODE", sendValue.getRequest().get(i).getBARCODE()) ;
				result.addData("APPQUANTITY", sendValue.getRequest().get(i).getAPPQUANTITY()) ;
				result.addData("CURQUANTITY", sendValue.getRequest().get(i).getCURQUANTITY()) ;
			}
		
		} catch (Exception e) {
			System.out.println("err======"+e.toString());
		}	
		
		return  result ;
	}
	public static void main(String[] args){
		String sendString = "<ROOT>" +
		        "<CONSIS_STORAGE_APPVW> " +
				"<BARCODE>12345</BARCODE>" +
				"<APPQUANTITY>10</APPQUANTITY>" +
				"<CURQUANTITY>10</CURQUANTITY>" +
				 "</CONSIS_STORAGE_APPVW> " +
				 "<CONSIS_STORAGE_APPVW> " +
					"<BARCODE>12345</BARCODE>" +
					"<APPQUANTITY>10</APPQUANTITY>" +
					"<CURQUANTITY>10</CURQUANTITY>" +
					 "</CONSIS_STORAGE_APPVW> " +
				"</ROOT>" ;
		
	TParm parm = 	createSendxml305toParm(sendString) ;
	System.out.println("parm======1======"+parm);
	}
	/**
	 * �������󣬽�����ת��Ϊxml,����ר��
	 * @return
	 */
	public static StringWriter createSendParmtoXml(){
		TParm  result = new  TParm() ;
			SendBean bean = new SendBean() ;
			bean.setPRESC_NO("1305290002") ;
			bean.setLOCATION("9") ;
			 StringWriter xml = new StringWriter() ;
			 JAXB.marshal(bean, xml); 
			   return xml ;
	}
	/**
	 * �������󣬽�����ת��Ϊxml,301�������������ص�xml
	 * @return
	 */
	public static StringWriter createParmtoXml(String message,int status){
			ReturnBean bean = new ReturnBean() ;
			bean.setRETCODE(status);
			bean.setRETMSG(message);
			bean.setRETVAL(0+"") ;
			 StringWriter xml = new StringWriter() ;
			 JAXB.marshal(bean, xml); 
			   return xml ;
	}
	/**
	 * �������͵�ת�� 0��7������Ƽ�
          1������ҩ
          2������ҩƷ
          3����ҩ��Ƭ
          4��������Ŀ
          5����������Ŀ
	 */

	public static  String onRxtype(String code){
		String typeDesc = "" ;
		Map<String, String>  map = new HashMap<String, String>();
		map.put("0", "����Ƽ�") ;
		map.put("7", "����Ƽ�") ;
		map.put("1", "����ҩ") ;
		map.put("2", "����ҩƷ") ;
		map.put("3", "��ҩ��Ƭ") ;
		map.put("4", "������Ŀ") ;
		map.put("5", "��������Ŀ") ;
		Iterator  ite = map.entrySet().iterator();
		  while(ite.hasNext()){
			  Map.Entry entry = (Entry) ite.next() ;
			  if(entry.getKey().equals(code)){
				  typeDesc =entry.getValue().toString() ;
			  }
		  }
		return typeDesc ;
	}
	/**
	 * ͬ���ֵ�()ҩƷ��sys_fee ����101����
	 * @param parm
	 * @return
	 */
	public static StringWriter onSysFeeDictionary(TParm parm){
		StringWriter xml = new StringWriter() ;
		Prescription prescription = new Prescription() ; //��������
		//���ñ�ͷ
		 prescription.setOPIP(parm.getValue("OPT_TERM")) ;      //����ip
		 prescription.setOPMANNAME(parm.getValue("OPT_USER")) ; //����name
		 prescription.setOPMANNO(parm.getValue("OPT_CODE")) ;  //������Ա����
		 prescription.setOPTYPE(parm.getValue("WAY")) ;        //��������
		 prescription.setOPWINID(parm.getValue("OPWINID")) ;   //�����ն˴��ں�
		 //�����ֵ���ϸ
		 List<DictionarySysFee> listSysFee = prescription.getSysFee() ;
		 DictionarySysFee sysFee = null ;
		 for(int i=0;i<parm.getCount();i++){
			 sysFee = new DictionarySysFee() ;
			 sysFee.setDRUG_CODE(parm.getValue("ORDER_CODE", i)); //HISҩƷ���
			 sysFee.setDRUG_NAME(parm.getValue("ORDER_DESC", i)) ;//ҩƷ����
			 sysFee.setTRADE_NAME(parm.getValue("GOODS_DESC", i));//ҩƷ��Ʒ��
			 sysFee.setDRUG_SPEC(parm.getValue("SPECIFICATION", i));//ҩƷ���
			 sysFee.setDRUG_PACKAGE(parm.getValue("SPECIFICATION", i));//ҩƷ��װ���
			 sysFee.setDRUG_UNIT(parm.getValue("UNIT_CHN_DESC", i)) ;//ҩƷ��λ
			 sysFee.setFIRM_ID(parm.getValue("MAN_CHN_DESC", i)) ;//ҩƷ����
			 sysFee.setDRUG_PRICE(parm.getDouble("OWN_PRICE", i)+"");//ҩƷ�۸�
			 sysFee.setDRUG_FORM(parm.getValue("CHN_DESC", i)) ;//ҩƷ����
			 sysFee.setDRUG_SORT(parm.getValue("CTRLDRUGCLASS_CHN_DESC", i)) ;//ҩƷ����
			 sysFee.setBARCODE(parm.getValue("BAR_CODE", i));//ҩƷ������????????????????????????
			 sysFee.setLAST_DATE(parm.getValue("OPT_DATE", i)) ;//������ʱ��
			 sysFee.setPINYIN(parm.getValue("PY1", i)) ;//ҩƷƴ��
			 sysFee.setDRUG_CONVERTATION(parm.getValue("DOSAGE_QTY",i));//������?????????????????????????????????
			 sysFee.setALLOWIND(parm.getValue("ACTIVE_FLG", i)) ;//�Ƿ����� 
			 listSysFee.add(sysFee) ;
		 }
		 try {
			JAXB.marshal(prescription, xml) ;
			
//			
			return xml ;
		} catch (Exception e) {
		    System.out.println("������Ϣ=========="+e.toString());
		}
		
		return xml ;
	}
	
	/**
	 * ͬ���ֵ�()ҩƷ��� ����102����
	 * @param parm
	 * @return
	 */
	public static StringWriter onStockDictionary(TParm parm){
		StringWriter xml = new StringWriter() ;
		Prescription prescription = new Prescription() ; //��������
		//���ñ�ͷ
		 prescription.setOPIP(parm.getValue("OPT_TERM")) ;      //����ip
		 prescription.setOPMANNAME(parm.getValue("OPT_USER")) ; //����name
		 prescription.setOPMANNO(parm.getValue("OPT_CODE")) ;  //������Ա����
		 prescription.setOPTYPE(parm.getValue("WAY")) ;        //��������
		 prescription.setOPWINID(parm.getValue("OPWINID")) ;   //�����ն˴��ں�
		 //�����ֵ���ϸ
		 List<DictionaryStock> listStock = prescription.getStock();
		 DictionaryStock stock = null ;
		 for(int i=0;i<parm.getCount();i++){
			 stock = new DictionaryStock() ;
			 stock.setDISPENSARY("�ż���ҩ��") ;//��ҩҩ�ִ���
			 stock.setDRUG_CODE(parm.getValue("ORDER_CODE", i)) ;//ҩƷ���
			 stock.setDRUG_QUANTITY("0") ;//ҩƷ����
			 stock.setLOCATIONINFO(parm.getValue("MATERIAL_LOC_CODE", i)) ;//ҩƷ��λ
			 listStock.add(stock) ;
		
		 }
		 try {
			JAXB.marshal(prescription, xml) ;
			return xml ;
		} catch (Exception e) {
		    System.out.println("������Ϣ=========="+e.toString());
		}
		
		return xml ;
	}
	
	
	
}

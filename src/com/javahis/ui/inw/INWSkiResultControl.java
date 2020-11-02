package com.javahis.ui.inw;

import java.sql.Timestamp;

import jdo.inw.InwForOdiTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>
 * Title: Ƥ�Խ��
 * </p>
 * 
 * <p>
 * Description: Ƥ�Խ��
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author YanJ 2013-11-07
 * @version 1.0
 */
public class INWSkiResultControl extends TControl{
	private TParm parmmeter;//���ܻ�ʿִ�н���Ĳ���
	private String caseNo = "";//�����
	private String orderCode = "";//ҩ������
	private String phl="";//add caoyong 2014/03/27����ִ��
	private String batchNo;
	private String skinflg;
	private String orderNo;
	private String orderSeq;
	private String rxNo;
	/**
	 * ��ʼ������
	 */
	public void onInit() {
		this.parmmeter = new TParm();
		Object obj = this.getParameter();
		if (obj.toString().length() != 0 || obj != null) {
			this.parmmeter = (TParm) obj;
			this.setValue("BATCH_NO", parmmeter.getValue("BATCH_NO"));//��ʼ������
			this.setValue("SKINTEST_NOTE", parmmeter.getValue("SKINTEST_FLG"));//��ʼ��Ƥ�Խ��
		}
		phl=parmmeter.getValue("PHL");//add caoyong 2014/03/27����ִ��
		caseNo = parmmeter.getValue("CASE_NO");
		orderCode = parmmeter.getValue("ORDER_CODE");
		orderNo = parmmeter.getValue("ORDER_NO");
		orderSeq = parmmeter.getValue("SEQ_NO");
		rxNo = parmmeter.getValue("RX_NO");
		//Ƥ������ add caoyong 2014/4/3 start
		batchNo=parmmeter.getValue("BATCH_NO");
		skinflg=parmmeter.getValue("SKINTEST_FLG");
		 if(!"".equals(skinflg)||skinflg!=null){
			   if("(-)����".equals(skinflg.trim())){
				   skinflg="0";
			   }
			   if("(+)����".equals(skinflg.trim())){
				   skinflg="1";
			   }
			   this.setValue("SKINTEST_NOTE", skinflg);
		   }
		//Ƥ�Խ�� add caoyong 2014/4/3 end 
	}
	/**
	 * Ƥ�Իش�
	 */
   public void onFecth(){
	   TParm result = new TParm();
	   String skiNo = this.getValueString("BATCH_NO");//��ȡ�����Ƥ������
	   String skiResult = this.getValueString("SKINTEST_NOTE");//��ȡ�����Ƥ�Խ��
	  
	   if(skiNo.equals(null)||"".equals(skiNo)){
		   this.messageBox("Ƥ�����Ų���Ϊ�ա�");
		   return;
	   }
	   if(skiResult.equals(null)||"".equals(skiResult)){
		   this.messageBox("Ƥ�Խ������Ϊ�ա�");
		   return;
	   }
	   result.addData("BATCH_NO", skiNo);
	   result.addData("SKINTEST_NOTE", skiResult);
	   this.setReturnValue(result);
	   //ͬ���޸�pha_anti���е�Ƥ�����ź�Ƥ�Խ��
	   //��ѯ�����Ƥ�Խ��
	   if("".equals(phl)||phl==null){//������Ǿ���ִ�н������ add caoyong 20140327
		   TParm inParm = new TParm();
		   inParm.setData("ORDER_CODE", orderCode);
		   inParm.setData("SKINTEST_NOTE", skiResult);
		   inParm.setData("BATCH_NO", skiNo);
		   inParm.setData("CASE_NO", caseNo);
		   inParm.setData("ORDER_NO", orderNo);//
		   inParm.setData("ORDER_SEQ", orderSeq);//
		   inParm.setData("OPT_USER", Operator.getID());//
		   inParm.setData("OPT_TERM", Operator.getIP());//
	// ����actionִ������
       TParm result1 = TIOM_AppServer.executeAction(
               "action.inw.InwOrderExecAction", "insertSkinNote", inParm);
       if(result1.getErrCode()<0){
    	   this.messageBox(result1.getErrText());
    	   return;
       }
	   }else{//�������
		   TParm inParm = new TParm();
		   inParm.setData("RX_NO", rxNo);
	       inParm.setData("ORDER_CODE", orderCode);
	       inParm.setData("SKINTEST_FLG", skiResult);
	       inParm.setData("BATCH_NO", skiNo);
	       inParm.setData("CASE_NO", caseNo);
	       inParm.setData("ORDER_NO", orderNo);//
	       inParm.setData("SEQ_NO", orderSeq);//
	       inParm.setData("OPT_USER", Operator.getID());//
	       inParm.setData("OPT_TERM", Operator.getIP());//
         // ����actionִ������
           TParm result1 = TIOM_AppServer.executeAction(
           "action.inw.InwOrderExecAction", "insertSkinNotePHL", inParm);
           if(result1.getErrCode()<0){
        	   this.messageBox(result1.getErrText());
        	   return;
           }
           }
		   this.closeWindow();
   }

}

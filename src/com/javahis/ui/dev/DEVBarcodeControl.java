package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;

/**
 * <p>Title: �豸��������ӡ���������</p>
 *
 * <p>Description: �豸��������ӡ���������</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>
 *
 * @author sundx
 * @version 1.0
 */
public class DEVBarcodeControl extends TControl {

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //�豸��ϸ���༭�¼�
        addEventListener("DEV_INWAREHOUSED->" + TTableEvent.CHANGE_VALUE,"onTableValueChange");
        onInitParm((TParm)getParameter());
    }

    /**
     * ͨ���������ݳ�ʼ������
     * @param parm TParm
     */
    private void onInitParm(TParm parm){
        //�õ���ⵥ��ϸ��Ϣ
        TParm inParmD = parm.getParm("DEV_INWAREHOUSED");
        //�õ���ⵥ��ϸ��Ź�����Ϣ
        TParm inParmDD = parm.getParm("DEV_INWAREHOUSEDD");
        //������ⵥ��ϸ����
        TParm parmD = new TParm();
        //ȡ����ⵥ��ϸ��Ź������
        TParm parmDD = new TParm();
        //�����豸�����ձ�
        Map mapSpe = new HashMap();
        //�����豸������ձ�
        Map mapKind = new HashMap();
        for(int i = 0;i<inParmD.getCount("DEV_CODE");i++){
            //���������Ź������¼�򲻷�����ϸ����ֻ��¼��������
            if(inParmD.getData("SEQMAN_FLG",i).equals("Y")){
                mapSpe.put(inParmD.getData("SEQ_NO",i),inParmD.getData("SPECIFICATION",i));
                mapKind.put(inParmD.getData("SEQ_NO",i),inParmD.getData("DEVKIND_CODE",i));
                continue;
            }
            //û����Ź������������ϸ����
            cloneTParm(inParmD, parmD, i);
            parmD.addData("SEL_FLG","N");
            parmD.addData("PRINT_NUM","1");
        }
        //���豸��Ź�����Ϣ������Ź������ 
        for(int i = 0;i<inParmDD.getCount("DEV_CODE");i++){
            cloneTParm(inParmDD, parmDD, i);
            parmDD.addData("SEL_FLG","N");
            //ͨ���豸�����ϸ�������������Ź�����Ϣ�ĶԸ�����
            parmDD.addData("SPECIFICATION",mapSpe.get(inParmDD.getData("SEQ_NO",i)));
            parmDD.addData("DEVKIND_CODE",mapKind.get(inParmDD.getData("SEQ_NO",i)));
        }
        //���û�����=�Ϸ��ռ���Ϣ  
        setValue("INWAREHOUSE_NO",parm.getData("INWAREHOUSE_NO"));
        setValue("INWAREHOUSE_DATE",parm.getData("INWAREHOUSE_DATE"));
        setValue("INWAREHOUSE_DEPT",parm.getData("INWAREHOUSE_DEPT"));
        setValue("INWAREHOUSE_USER",parm.getData("INWAREHOUSE_USER"));
        ((TTable)getComponent("DEV_INWAREHOUSED")).setParmValue(parmD);
        ((TTable)getComponent("DEV_INWAREHOUSEDD")).setParmValue(parmDD);
    }


    /**
     * ������ı��¼�
     * @param obj Object
     * @return boolean 
     */
    public boolean onTableValueChange(Object obj) {
        TTableNode node = (TTableNode) obj;
        if(node.getColumn() != 7)
            return false;
        int qty = Integer.parseInt("" + ((TTable)getComponent("DEV_INWAREHOUSED")).getValueAt(node.getRow(),5));
        if(Integer.parseInt("" + node.getValue()) > qty){
            messageBox("��ӡ�������ɴ����������");
            return true;
        }
        return false;
    }       
    /**
     * �豸�����ϸTableȫѡ�¼�
     */
    public void onSelecctAllD(){ 
        TTable table = ((TTable)getComponent("DEV_INWAREHOUSED"));
        for(int i = 0;i < table.getRowCount();i++){
            table.getParmValue().setData("SEL_FLG",i,getValue("selectAllD"));
            table.setValueAt(getValue("selectAllD"), i, 0);
        }

    }

    /**
     * ����豸�����ϸ��Ź���Tableȫѡ�¼�
     */
    public void onSelecctAllDD(){
        TTable table = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
        for(int i = 0;i < table.getRowCount();i++){
            table.getParmValue().setData("SEL_FLG",i,getValue("selectAllDD"));
            table.setValueAt(getValue("selectAllDD"), i, 0);
        }
    }
    /**
     * ����TParm 
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from,TParm to,int row){
    	//�õ�����
        String names[] = from.getNames();
        for(int i = 0;i < names.length;i++){
        	//parm���������
            Object obj = from.getData(names[i],row);
            if(obj == null)
                obj = "";
            to.addData(names[i],obj);
        }
     }

     /**
      * �����ӡ�¼�
      */
     public void onPrint(){
         getTTable("DEV_INWAREHOUSED").acceptText();
         getTTable("DEV_INWAREHOUSEDD").acceptText();
         //ȡ�õ�ǰѡ��ҳǩ
         int index = ((TTabbedPane)getComponent("TAB_PANEL")).getSelectedIndex();
         //����������ϸҳǩ����������ӡ������ӡ����
         if(index == 0){             
            TTable table = ((TTable)getComponent("DEV_INWAREHOUSED"));
            TParm parm = table.getParmValue();
            for(int i = 0;i<table.getRowCount();i++){
                if("N".equals(parm.getValue("SEL_FLG", i)))
                    continue;
                for(int j = 0; j < parm.getInt("PRINT_NUM",i);j++){
                    String desc = parm.getValue("DEV_CHN_DESC", i);
                    String specification = parm.getValue("SPECIFICATION", i);
                    String kind = parm.getValue("DEVKIND_CODE", i);
                    //�����Ϊ�豸���
                    String code = parm.getValue("DEV_CODE", i);
                    TParm parmI = new TParm();
                    parmI.setData("DESC", "TEXT", desc);
                    parmI.setData("SPECIFICATION", "TEXT", specification);
                    parmI.setData("KIND", "TEXT", getDevKindDesc(kind));
                    parmI.setData("CODE", "TEXT", code);
                    openPrintDialog("%ROOT%\\config\\prt\\dev\\DevBarcode.jhw",
                                    parmI);
                }
            }
         }
         //����������ϸ��Ź�����Ϣ
         else{ 
             TTable table = ((TTable)getComponent("DEV_INWAREHOUSEDD"));
             TParm parm = table.getParmValue();
             for(int i = 0;i<table.getRowCount();i++){
                 if("N".equals(parm.getValue("SEL_FLG", i)))
                    continue;
                 String desc = parm.getValue("DEV_CHN_DESC",i);
                 String specification = parm.getValue("SPECIFICATION",i);
                 String kind = parm.getValue("DEVKIND_CODE",i);
                 String code = parm.getValue("DEV_CODE",i);
                 String seq = parm.getValue("DEVSEQ_NO",i);
                 seq = "0000".substring(0,4 - seq.length()) + seq;
                 TParm parmI = new TParm();
                 parmI.setData("DESC","TEXT",desc);
                 parmI.setData("SPECIFICATION","TEXT",specification);
                 parmI.setData("KIND","TEXT",getDevKindDesc(kind));
                 //�����Ϊ�豸��ż��豸��ˮ��
                 parmI.setData("CODE","TEXT",code + seq);
                 openPrintDialog("%ROOT%\\config\\prt\\dev\\DevBarcode.jhw",parmI);
            }
         }
     }

     /**
      * �õ����ݿ���ʹ���
      * @return TJDODBTool
      */
     public TJDODBTool getDBTool() {
         return TJDODBTool.getInstance();
     }

     /**
      * ȡ���豸������������
      * @param devKindCode String
      * @return String
      */
     public String getDevKindDesc(String devKindCode){
         TParm parm = new TParm(getDBTool().select(" SELECT CHN_DESC FROM SYS_DICTIONARY "+
                                                   " WHERE GROUP_ID = 'DEVKIND_CODE'"+
                                                   " AND   ID = '"+devKindCode+"'"));
         return parm.getValue("CHN_DESC",0);
    }
     /**
      * �õ�TTable
      * @param tag String
      * @return TTable
      */
     public TTable getTTable(String tag){
         return (TTable)getComponent(tag);
    }
}

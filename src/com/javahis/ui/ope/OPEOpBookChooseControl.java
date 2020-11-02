package com.javahis.ui.ope;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.ope.OPEOpBookTool;
import com.dongyang.ui.TTable;
import jdo.ope.OPEOpDetailTool;

/**
 * <p>Title: ����ԤԼ�����������¼�б�</p>
 *
 * <p>Description: ����ԤԼ�����������¼�б�</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-28
 * @version 4.0
 */
public class OPEOpBookChooseControl
    extends TControl {
    private String MR_NO = "";
    private String OPBOOK_SEQ = "";
    private String CASE_NO = "";
    private String FLG = "close";//����״̬ Ĭ��Ϊ�ر�
    public void onInit(){
        super.onInit();
        PageParmInit();
        DataInit();
    }
    /**
     * ҳ�������ʼ��
     */
    private void PageParmInit(){
        //�Ӳ�  ����ԤԼ����
        Object obj = this.getParameter();
        TParm parm = new TParm();
        if(obj instanceof TParm){
            parm = (TParm)obj;
        }else{
            return;
        }
        MR_NO = parm.getValue("MR_NO");
        OPBOOK_SEQ = parm.getValue("OPBOOK_SEQ");
        CASE_NO = parm.getValue("CASE_NO");
    }
    /**
     * ���ݳ�ʼ��
     */
    private void DataInit(){
        TParm opBook = new TParm();
        if(MR_NO.length()>0){
            opBook.setData("MR_NO",MR_NO);
        }else if(OPBOOK_SEQ.length()>0){
            opBook.setData("OPBOOK_SEQ",OPBOOK_SEQ);
        }
        if(CASE_NO.length()>0){
            opBook.setData("CASE_NO",CASE_NO);
        }
        TParm result = OPEOpBookTool.getInstance().selectOpBook(opBook);
        TTable table1 = (TTable)this.getComponent("Table1");
        table1.setParmValue(result);
    }
    /**
     * �ش��¼�
     */
    public void onBack(){
        TTable table1= (TTable)this.getComponent("Table1");
        TTable table2= (TTable)this.getComponent("Table2");
        
        int row1 = table1.getSelectedRow();
        int row2 = table2.getSelectedRow();
        TParm reParm = new TParm();
        //���ѡ������ԤԼ���� û��ѡ��������¼���� ��ô��ʾ �½�������¼
        if(row1>-1&&row2<0){
        	TParm tableParm2=table2.getParmValue();
        	if(tableParm2.getCount()<=0){
	            int re = this.messageBox("��ʾ","E0098",0);
	            //�½�
	            if(re==0){
	                FLG = "new";
	                reParm.setData("FLG","new");//����״̬
	                reParm.setData("OPBOOK_SEQ",table1.getValueAt(row1,0));//�ش�����ԤԼ����
	                this.setReturnValue(reParm);
	                this.closeWindow();
	            }else if(re==1){//���½�
	
	            }
        	}else{
            	this.messageBox("��ѡ��!");
            }
        }
        if(row2>=0){
            FLG = "update";
            reParm.setData("FLG","update");//����״̬
            reParm.setData("OPBOOK_SEQ",table1.getValueAt(row1,0));//�ش�����ԤԼ����
            reParm.setData("OP_RECORD_NO",table2.getValueAt(row2,0));//�ش�������¼����
            this.setReturnValue(reParm);
            this.closeWindow();
        }
    }
    /**
     * ����ԤԼ��¼�����¼�
     */
    public void onTable1Selected(){
        TTable table= (TTable)this.getComponent("Table1");
        int row = table.getSelectedRow();
        String opBookNo = table.getValueAt(row,0).toString();
        TParm parm = new TParm();
        parm.setData("OPBOOK_NO",opBookNo);
        TParm result = OPEOpDetailTool.getInstance().selectData(parm);
        TTable table2 = (TTable)this.getComponent("Table2");
        table2.setParmValue(result);
    }
    /**
     * �ر��¼�
     * @return boolean
     */
    public boolean onClosing(){
        //���û��ѡ���½������޸���Ϣ ��ô���ء��رա�״̬ ֱ�ӹرո�����
        if(FLG.equals("close")){
            TParm parm = new TParm();
            parm.setData("FLG",FLG);
            this.setReturnValue(parm);
        }
        return true;
    }
}

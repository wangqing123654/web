package com.javahis.ui.ins;

import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.ins.INSSingleTransferTool;

/**
 * <p>Title: ������ת�������</p>
 *
 * <p>Description: ������ת�������</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2012.02.13
 * @version 1.0
 */
public class INSSingleTransferControl
    extends TControl {
   

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * ��ʼ������
     */
    public void initPage() {
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        //��Ժ��ʼʱ��
        setValue("S_DATE", yesterday);
        //��Ժ����ʱ��
        setValue("E_DATE", SystemTool.getInstance().getDate());
        //Ĭ��ֵ ��ְ
        setValue("CARD_TYPE", "1");
        //Ĭ��ֵ δת��
        setValue("TRANS_TYPE", "2");
        //tableר�õļ���
        getTTable("Table").addEventListener(TTableEvent.CLICKED, this,
                                                "onTable1Clicked");
    }

    /**
     * ����
     */
    public void onSave() {
        int row = getTTable("Table").getSelectedRow();
        if (row < 0) {
            messageBox("���ѡһ������");
            return;
        }
        TParm parm = new TParm();
        parm = getTTable("Table").getParmValue().getRow(row);
//        parm.setData("TRANS_TYPE",getValueString("TRANS_TYPE"));
        //System.out.println("table"+parm);
        TParm result = new TParm();
        //�����ʸ�ȷ����״̬
        result = INSSingleTransferTool.getInstance().onUpINSIbsUpload(parm);
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;

        }else{
            messageBox("P0005");
            onQuery();
        }
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm result = new TParm();
        TParm parm = new TParm();
        parm.setData("CARD_TYPE", this.getValueString("CARD_TYPE"));
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("S_DATE",StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyyMMdd"));
        parm.setData("E_DATE",
                     StringTool.getString(TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd"));
        
        String mrNo = getValueString("MR_NO").length()==0?"":PatTool.getInstance().checkMrno(getValueString("MR_NO"));
        parm.setData("MR_NO", mrNo);
        if (this.getValueString("TRANS_TYPE").equals("1"))
            result = INSSingleTransferTool.getInstance().getSingleTransData(
                parm);
        else
            result = INSSingleTransferTool.getInstance().getSingleNoTransData(
                parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount("CONFIRM_NO") < 1) {
            //��������
            this.messageBox("E0008");
            this.onClear();
            return;
        }
        this.callFunction("UI|Table|setParmValue", result);
        return;
    }
    /**
     * ���
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        this.clearValue("MR_NO");
    }
    /**
     * table�����¼�
     */
    public void onTable1Clicked() {
        callFunction("UI|Table1|getClickedRow");
    }
    /**
     * �õ�TTable
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }
    /**
     * �����������ݸ�ֵ
     */
    public void setDesc(){
      TTextFormat singleDisease = (TTextFormat) this.getComponent("SYS_SINGLEDISEASE");
      String text=singleDisease.getText();
      getTTable("Table").setValueAt(text,getTTable("Table").getSelectedRow(),10);
    }


}


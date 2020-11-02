package com.javahis.ui.sys;

import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.SYSNewRegionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.TMessage;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;

/**
 * <p>
 * Title:����
 * </p>
 *
 * <p>
 * Description: ����
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company:Javahis
 * </p>
 *
 * @author zhangkun 20090226
 * @version 1.0
 */
public class SYSRegionControl
    extends TControl {

    // ��ѯ�ֵ���еĹ��� statelist �е�����
    TParm stateList = SYSNewRegionTool.getInstance().getStateList(
        "SYS_REGION_FLG");

    // ��¼���ѡ������
    int selectedRowIndex = -1;

    public SYSRegionControl() {
    }

    public void onInit() {
        super.onInit();
        //yanjing20130420��Ӳ�ѯע�ͱ�ˢ��
        this.onQuery();
//        this.getTable("TABLE").onQuery();
        // ɾ��Ȩ��
        if (!this.getPopedem("delPopedem")) {
            ( (TMenuItem)this.getComponent("delete")).setVisible(false);
        }
        this.setValue("DETECTPWDTIME",90);//����У��
    }

    /**
     * ��ȡָ�����Ƶ�Table
     *
     * @param tableName
     *            String Table�ؼ���
     * @return TTable
     */
    private TTable getTable(String tableName) {
        TTable table = (TTable)this.getComponent(tableName);
        return table;
    }

    /**
     * �е���¼�
     */
    public void RowClicked() {
        // this.messageBox("�е���¼�");
        TTextField txt = (TTextField)this.getComponent("REGION_CODE");
        txt.setEditable(false); // ���á�������롱���ɱ༭
        TTable table = this.getTable("TABLE");
        TParm data = table.getParmValue(); // ��ȡ����
        selectedRowIndex = table.getSelectedRow(); // ��ȡѡ���к�
        String STATE_LIST = data.getValue("STATE_LIST", selectedRowIndex); // ��ȡ�����λ�б�����
        this.setValue("PWD_STRENGTH",data.getValue("PWD_STRENGTH",selectedRowIndex));
        //=======pangben modify 20110601 ����У������
        this.setValue("DETECTPWDTIME",data.getValue("DETECTPWDTIME",selectedRowIndex));
        //=======yanjing modify 20130420 ���������
        this.setValue("SPC_FLG", data.getValue("SPC_FLG",selectedRowIndex));
        //=======shendr modify 20131014 ����ҩ����������ϵͳ����ע��
        this.setValue("PIVAS_FLG", data.getValue("PIVAS_FLG",selectedRowIndex));
        //=======pangben modify 20131108  �����ע��
        this.setValue("LOCK_FLG", data.getValue("LOCK_FLG",selectedRowIndex));
        int count = stateList.getCount("ID"); // Ҫ��ʾ���ֶθ���
        char[] s_list = STATE_LIST.toCharArray();
        data.setDataN("ss", getValue("sss"));
        if (count > 0 && count == s_list.length) {
            for (int i = 0; i < count; i++) {
                this.setValue(stateList.getValue("ID", i), String
                              .valueOf(s_list[i]));
            }
        }
    }
    /**
     * �����¼�
     */
    public void onSave() {
        // �жϱ����ֶ�
        if (this.getValue("REGION_CODE").equals("")
            || this.getValue("REGION_CHN_DESC").equals("")
            || this.getValue("REGION_CHN_ABN").equals("")) {
            this.messageBox("����д�������ƺ����ļ�ƣ�");
            return;
        }

        // -----------��������ֵ-----------------
        TParm parm = new TParm();
        parm.setData("REGION_CODE", this.getValue("REGION_CODE"));
        parm.setData("NHI_NO", this.getValue("NHI_NO"));
        parm.setData("SPC_FLG",this.getValueString("SPC_FLG"));//yanjing20130419���������
        parm.setData("LOCK_FLG",this.getValueString("LOCK_FLG"));//pangben 20131108 �������
        parm.setData("MAIN_FLG", this.getValue("MAIN_FLG"));
        parm.setData("HOSP_CLASS", this.getValue("HOSP_CLASS")); // �б��
        parm.setData("REGION_CHN_DESC", this.getValue("REGION_CHN_DESC"));
        parm.setData("REGION_CHN_ABN", this.getValue("REGION_CHN_ABN"));
        parm.setData("PY1", this.getValue("PY1"));
        parm.setData("REGION_ENG_DESC", this.getValue("REGION_ENG_DESC"));
        parm.setData("REGION_ENG_ABN", this.getValue("REGION_ENG_ABN"));
        parm.setData("DESCRIPTION", this.getValue("DESCRIPTION"));
        parm.setData("PY2", this.getValue("PY2"));
        parm.setData("SEQ", this.getValue("SEQ"));
        parm.setData("SUPERINTENDENT", this.getValue("SUPERINTENDENT"));
        parm.setData("NHIMAIN_NAME", this.getValue("NHIMAIN_NAME"));
        parm.setData("REGION_TEL", this.getValue("REGION_TEL"));
        parm.setData("REGION_FAX", this.getValue("REGION_FAX"));
        parm.setData("REGION_ADDR", this.getValue("REGION_ADDR"));
        parm.setData("E_MAIL", this.getValue("E_MAIL"));
        parm.setData("ACTIVE_FLG", this.getValue("ACTIVE_FLG"));
        parm.setData("IP_RANGE_START", this.getValue("IP_RANGE_START"));
        parm.setData("IP_RANGE_END", this.getValue("IP_RANGE_END"));
        parm.setData("AP_IP_ADDR", this.getValue("AP_IP_ADDR"));
        parm.setData("PWD_STRENGTH", this.getValue("PWD_STRENGTH"));
        //===========pangben modify 20110601 start  ����У������
        parm.setData("DETECTPWDTIME", this.getValue("DETECTPWDTIME"));
        //===========pangben modify 20110601 stop
        // �ж����ֿ��Ƿ�Ϊnull
        if (this.getValue("TOP_BEDFEE") == null) {
            parm.setData("TOP_BEDFEE", "");
        }
        else {
            parm.setData("TOP_BEDFEE", this.getValue("TOP_BEDFEE"));
        }
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        // ��ȡ��ʶ�б����
        int list_num = stateList.getCount("ID");
        String state = "";
        if (list_num > 0) {
            for (int i = 0; i < list_num; i++) {
                state += this.getValue(stateList.getValue("ID", i));
            }
        }
        // ����ַ����������ֶ��б���ͬĬ������Ϊ��ȫ��
        if (state.length() != list_num) {
            state = "NNNNNNNNNN";
        }
        parm.setData("STATE_LIST", state);
        
        //======== shendr modify 20131014 ����ҩ����������ϵͳ����ע��
        parm.setData("PIVAS_FLG", this.getValueString("PIVAS_FLG"));
        // -----------��������ֵ����-----------------
        // �жϡ�REGION_CODE���ؼ��Ƿ���ֻ�����ԣ���������޸ġ��������½���
        TParm result = new TParm();
        if ( ( (TTextField)this.getComponent("REGION_CODE")).isEditable()) {
        	 //=====yanjing20140422�������У��
            TTable table = getTable("TABLE");
        	TParm tableParm=table.getParmValue();
        	for (int i = 0; i < tableParm.getCount(); i++) {
    			if (tableParm.getValue("REGION_CODE",i).equals(this.getValue("REGION_CODE"))){
    				this.messageBox("������벻���ظ���");
    				return;
    			}			
    		}
            // �ж���Ժ��
            if ("Y".equals(getValueString("MAIN_FLG"))) {
                TParm region = SYSNewRegionTool.getInstance().getAllRegion();
                if (region.getErrCode() < 0) {
                    this.messageBox("����", region.getErrText(), -1);
                    return;
                }
                for (int i = 0; i < region.getVector("MAIN_FLG").size(); i++) {
                    String flg = ( (Vector) region.getVector("MAIN_FLG").get(i))
                        .get(0).toString();
                    if (!region.getValue("REGION_CODE",
                        i).equals(this.getValue("REGION_CODE")) && "Y".equals(flg)) {
                        this.messageBox("�Ѿ�������Ժ��");
                        return;
                    }
                }
            }
            result = SYSNewRegionTool.getInstance().onInsert(parm);
            this.messageBox("�½��ɹ���");
            this.onClear();
            //yanjing20130420 �޸�
            this.onQuery();
//            this.getTable("TABLE").onQuery(); // ��ˢ��
        }
        else {
            result = SYSNewRegionTool.getInstance().onUpdate(parm);
            this.messageBox("�޸ĳɹ���");
            //20130419yanjing �޸�
            this.onClear();
            this.onQuery();
//            this.getTable("TABLE").onQuery(); // ��ˢ��
         //   this.getTable("TABLE").setSelectedRow(selectedRowIndex);
        }
        if (result.getErrCode() < 0) {
            this.messageBox("����", result.getErrText(), -1);
            return;
        }

    }

    /**
     * ��ѯ�¼�
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("REGION_CODE", this.getValue("REGION_CODE"));
        parm.setDataN("NHI_NO", this.getValue("NHI_NO"));
        parm.setDataN("HOSP_CLASS", this.getValue("HOSP_CLASS")); // �б��
        parm.setDataN("REGION_CHN_DESC", this.getValue("REGION_CHN_DESC"));
        parm.setDataN("REGION_CHN_ABN", this.getValue("REGION_CHN_ABN"));
        parm.setDataN("PY1", this.getValue("PY1"));
        parm.setDataN("REGION_ENG_DESC", this.getValue("REGION_ENG_DESC"));
        parm.setDataN("REGION_ENG_ABN", this.getValue("REGION_ENG_ABN"));
        parm.setDataN("DESCRIPTION", this.getValue("DESCRIPTION"));
        parm.setDataN("PY2", this.getValue("PY2"));
        parm.setDataN("SUPERINTENDENT", this.getValue("SUPERINTENDENT"));
        parm.setDataN("NHIMAIN_NAME", this.getValue("NHIMAIN_NAME"));
        parm.setDataN("REGION_TEL", this.getValue("REGION_TEL"));
        parm.setDataN("REGION_FAX", this.getValue("REGION_FAX"));
        parm.setDataN("REGION_ADDR", this.getValue("REGION_ADDR"));
        parm.setDataN("E_MAIL", this.getValue("E_MAIL"));
        parm.setDataN("IP_RANGE_START", this.getValue("IP_RANGE_START"));
        parm.setDataN("IP_RANGE_END", this.getValue("IP_RANGE_END"));
        parm.setDataN("AP_IP_ADDR", this.getValue("AP_IP_ADDR"));
        TParm result = SYSNewRegionTool.getInstance().onQuery(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        this.getTable("TABLE").setParmValue(result);
        selectedRowIndex = -1;
    }

    /**
     * ɾ���¼�
     */
    public void onDelete() {
        // ȷ��ɾ��
        if (this.messageBox("ѯ��", "ȷ��ɾ��?", 0) == 1) {
            return;
        }
        // ���ɾ����Ϣ����Ϊ��
        if (this.getValue("REGION_CODE").equals("")
            || this.getValue("REGION_CODE").equals(null)) {
            this.messageBox("��ѡ��Ҫɾ�����");
            return;
        }
        TParm parm = new TParm();
        parm.setData("REGION_CODE", this.getValue("REGION_CODE"));
        // ���ϵͳ�Ƿ�����
        TParm re = SYSNewRegionTool.getInstance().onQuery(parm);
        if (re.getValue("ACTIVE_FLG", 0).equals("Y")) {
            this.messageBox("ϵͳ�����ò���ɾ����");
            return;
        }
        TParm result = SYSNewRegionTool.getInstance().onDelete(parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }
        this.messageBox("ɾ���ɹ���");
        this.onClear();
        this.getTable("TABLE").onQuery();
    }

    /**
     * ����¼�
     */
    public void onClear() {
        this
            .clearValue("REGION_CODE;NHI_NO;MAIN_FLG;HOSP_CLASS;REGION_CHN_DESC;REGION_CHN_ABN;SEQ;PY1;PY2;REGION_ENG_DESC;REGION_ENG_ABN;DESCRIPTION");
        this
            .clearValue("SUPERINTENDENT;NHIMAIN_NAME;REGION_TEL;REGION_FAX;REGION_ADDR;E_MAIL;ACTIVE_DATE;ACTIVE_FLG;IP_RANGE_START;IP_RANGE_END;AP_IP_ADDR;EMR;EKT;LDAP;INS;ONLINE;ODO;REASONABLEMED;REG;HANDINMED;CHARGE;TOP_BEDFEE;PWD_STRENGTH;PIVAS_FLG;SPC_FLG;LOCK_FLG");
        ( (TTextField)this.getComponent("REGION_CODE")).setEditable(true); // ���á�������롱Ϊ�ɱ༭״̬
        selectedRowIndex = -1;
        this.getTable("TABLE").clearSelection(); // ���TABLEѡ��״̬
        this.setValue("DETECTPWDTIME",90);//����У��
    }

    /**
     * �������ƻس��¼�
     */
    public void onRegionChnDescAction() {
        String desc = this.getValueString("REGION_CHN_DESC");
        String py = TMessage.getPy(desc);
        this.setValue("PY1", py);
        ( (TTextField) getComponent("REGION_CHN_ABN")).grabFocus();
    }

}

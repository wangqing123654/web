package com.javahis.ui.clp;


import jdo.clp.CLPAssessStandardTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

/**
 * <p>Title: �ٴ�·��������׼</p>
 *
 * <p>Description: �ٴ�·��������׼ </p>
 *
 * <p>Copyright: Copyright (c) �����к� 2011</p>
 *
 * <p>Company: javahis </p>
 * com.javahis.ui.clp.CLPAssessStandardControl
 * 
 * @author ZhenQin
 * @version 4.0
 */
public class CLPAssessStandardControl
    extends TControl {

    /**
     * ��ʾ��TABle-CLP_EVL_CAT1
     */
    private TTable CLP_EVL_CAT1 = null;

    /**
     * ��ʾ��TABle-CLP_EVL_CAT3
     */
    private TTable CLP_EVL_CAT2 = null;

    /**
     * ��ʾ��TABle-CLP_EVL_CAT3
     */
    private TTable CLP_EVL_CAT3 = null;

    private String action = "update";
    
    private int index = 1;

    /**
     * ���췽��
     */
    public CLPAssessStandardControl() {
        super();
    }

    /**
     * ��ʼ��
     */
    public void onInit() {
        CLP_EVL_CAT1 = (TTable)this.getComponent("CLP_EVL_CAT1");
        CLP_EVL_CAT2 = (TTable)this.getComponent("CLP_EVL_CAT2");
        CLP_EVL_CAT3 = (TTable)this.getComponent("CLP_EVL_CAT3");
        ((TMenuItem)this.getComponent("delete")).setEnabled(false);
        ((TMenuItem)this.getComponent("save")).setEnabled(false);
    }

    /**
     * ��ѯ,����ѯʱ,��������code,��code���в�ѯ<br>
     * ��������desc(��,Ӣ),�������ģ����ѯ<br>
     * �������ȫ����ѯ
     */
    public void onQuery() {
        TParm param = new TParm();
        //ȡ��code��desc��ֵ
        TParm progress = this.getParmForTag("CAT1_CODE;CAT1_CHN_DESC;CAT1_ENG_DESC");
        //���������code,��code��ѯ
        if (!progress.getValue("CAT1_CODE").equals("")) {
            param.setData("CAT1_CODE", progress.getValue("CAT1_CODE"));
        }
        //�������������desc,������descģ����ѯ
        if (!progress.getValue("CAT1_CHN_DESC").equals("")) {
            param.setData("CAT1_CHN_DESC", progress.getValue("CAT1_CHN_DESC") + "%");
        }
        //���������Ӣ��code,��Ӣ��codeģ����ѯ
        if (!progress.getValue("CAT1_ENG_DESC").equals("")) {
            param.setData("CAT1_ENG_DESC", progress.getValue("CAT1_ENG_DESC") + "%");
        }
        param.setData("REGION_CODE", Operator.getRegion());
        //��ѯ����
        param = CLPAssessStandardTool.getInstance().queryCLP_EVL_CAT(
            "queryCLP_EVL_CAT1", param);
        //��ѯ�쳣��û�в�ѯ�����
        if (param.getErrCode() < 0 || param.getCount("CAT1_CODE") <= 0) {
            this.messageBox("û�в�ѯ������!");
            return;
        }
        //��ѯ����������ʾ��table��
        CLP_EVL_CAT1.setParmValue(param);
        ((TMenuItem)this.getComponent("delete")).setEnabled(true);
        //��ѯ��������������CLP_EVL_CAT2,CLP_EVL_CAT3
        CLP_EVL_CAT2.removeRowAll();
        CLP_EVL_CAT3.removeRowAll();

    }

    /**
     * ��������
     */
    public void onSave() {
    	
        //ȡ��TAg��ֵ
        TParm param = this.getParmForTag("CAT1_CODE;CAT1_CHN_DESC;"
        		+ "CAT1_ENG_DESC;CAT2_CODE;CAT2_CHN_DESC;CAT2_ENG_DESC;"
        		+ "CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;"
        		+ "PY1;PY2;PY3;DESCRIPTION1;DESCRIPTION2;DESCRIPTION3;"
        		+ "SEQ1;SEQ2;SEQ3");
        //����ZJֵ,����PY2
        param.setData("ZJ1", "");
        param.setData("ZJ2", "");
        param.setData("ZJ3", "");
        //ȡ��Ժ��
        param.setData("REGION_CODE", Operator.getRegion());
        //������ID
        param.setData("OPT_USER", Operator.getID());
        //ȡ�õ�ǰʱ��,�������������Ǹ���,ʱ�䶼�ǵ�ǰʱ��
        param.setData("OPT_DATE", SystemTool.getInstance().getDate());
        //������IP
        param.setData("OPT_TERM", Operator.getIP());
        //�ж��Ǹ��¶�����������
        if(action.equals("update")){
        	update(param);
        }else{
        	//���������ɹ����,����action�������ó�Ĭ�ϵĸ���
        	action = "update";
        	insert(param);
        }
        ((TMenuItem)this.getComponent("add")).setEnabled(true);
    }

    /**
     * �������
     */
    public void onClear() {
    	index = 1;
    	action = "update";
        this.clearValue("CAT1_CODE;CAT1_CHN_DESC;"
        		+ "CAT1_ENG_DESC;CAT2_CODE;CAT2_CHN_DESC;CAT2_ENG_DESC;"
        		+ "CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;"
        		+ "PY1;PY2;PY3;DESCRIPTION1;DESCRIPTION2;DESCRIPTION3;"
        		+ "SEQ1;SEQ2;SEQ3");
        CLP_EVL_CAT1.removeRowAll();
        CLP_EVL_CAT2.removeRowAll();
        CLP_EVL_CAT3.removeRowAll();
        TTextField CAT1_CODE = (TTextField)this.getComponent("CAT1_CODE");
        CAT1_CODE.setEnabled(true);
        ((TMenuItem)this.getComponent("delete")).setEnabled(false);
        ((TMenuItem)this.getComponent("save")).setEnabled(false);
        ((TMenuItem)this.getComponent("add")).setEnabled(true);
    }

    /**
     * ɾ��
     */
    public void onDelete() {
        //ȡ��TAg��ֵ
        TParm param = this.getParmForTag("CAT1_CODE;CAT2_CODE;CAT3_CODE");
        param.setData("REGION_CODE", Operator.getRegion());
        param.setData("INDEX", index);
        //������˵�һ���б�,action=2,��ʱɾ�����ǵ�һ����CLP_EVL_CAT1������
        //��ɾ����һ�����е�����,���CLP_EVL_CAT2��CLP_EVL_CAT3����CAT1_CODEΪ��������ݶ�ɾ��
        if (index == 2) {
            TParm parm = CLP_EVL_CAT1.getParmValue();
            //û������,����i
            if (parm == null || parm.getCount("CAT1_CODE") == 0) {
                this.messageBox("û������!");
                return;
            }
            int select = CLP_EVL_CAT1.getSelectedRow();
            //û��ѡ��,����
            if (select < 0) {
                this.messageBox("��ѡ����!");
                return;
            }
            //�ٴ�ѯ���Ƿ�ɾ��
            if(this.messageBox("����", "ɾ��������ɾ���˷����µ������ӷ���,��ȷ��Ҫɾ����?",
                               TControl.OK_CANCEL_OPTION) ==
               TControl.CANCEL_OPTION){
                return ;
            }
            //��������ɾ��,����ȫɾ��commit
            TParm result = TIOM_AppServer.executeAction("action.clp.CLPAssessStandardAction",
                                              "deleteCLP_EVL_CAT", param);
            if(result.getErrCode() < 0){
                this.messageBox("ɾ��ʧ��!");
                return;
            }else{
                this.messageBox("ɾ���ɹ�!");
                CLP_EVL_CAT1.removeRow(select);
                CLP_EVL_CAT2.removeRowAll();
                CLP_EVL_CAT3.removeRowAll();
                //ɾ�����ݳɹ�,������б��϶Զ��������
                this.clearValue("CAT1_CODE;CAT1_CHN_DESC;CAT1_ENG_DESC;"
                		+ "CAT2_CODE;CAT2_CHN_DESC;CAT2_ENG_DESC;"
                		+ "CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;PY1;PY2;PY3");
                return;
            }
            //������˵ڶ����б�,action=3,��ʱɾ�����ǵڶ�����CLP_EVL_CAT2������
        } else if(index == 3){
            TParm parm = CLP_EVL_CAT2.getParmValue();
            //û������,����i
            if (parm == null || parm.getCount("CAT2_CODE") == 0) {
                this.messageBox("û������!");
                return;
            }
            int select = CLP_EVL_CAT2.getSelectedRow();
            //û��ѡ��,����
            if (select < 0) {
                this.messageBox("��ѡ����!");
                return;
            }
            if(this.messageBox("����", "ɾ���з����ɾ���˷����µ�����С����,��ȷ��Ҫɾ����?",
                               TControl.OK_CANCEL_OPTION) ==
               TControl.CANCEL_OPTION){
                return ;
            }
            //��ɾ���ڶ��ű�ĵ�������ʱ,Ҫͬʱɾ�������ű��е��Եڶ��ű�CAT2_CODEΪ�������������
            TParm result = TIOM_AppServer.executeAction("action.clp.CLPAssessStandardAction",
                                              "deleteCLP_EVL_CAT", param);
            if(result.getErrCode() < 0){
                this.messageBox("ɾ��ʧ��!");
                return;
            }else{
                this.messageBox("ɾ���ɹ�!");
                CLP_EVL_CAT2.removeRow(select);
                CLP_EVL_CAT3.removeRowAll();
                this.clearValue("CAT2_CODE;CAT2_CHN_DESC;CAT2_ENG_DESC;"
                		+ "CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;PY2;PY3");
                return;
            }
            //������˵������б�,action=-1,��ʱɾ�����ǵ�������CLP_EVL_CAT3������
        }else if(index == -1){
            TParm parm = CLP_EVL_CAT3.getParmValue();
            //û������,����i
            if (parm == null || parm.getCount("CAT3_CODE") == 0) {
                this.messageBox("û������!");
                return;
            }
            int select = CLP_EVL_CAT3.getSelectedRow();
            //û��ѡ��,����
            if (select < 0) {
                this.messageBox("��ѡ����!");
                return;
            }
            if(this.messageBox("����", "��ȷ��Ҫɾ��ѡ���е�������?",
                               TControl.OK_CANCEL_OPTION) ==
               TControl.CANCEL_OPTION){
                return ;
            }
            //ֱ��ɾ��,��Ϊ�������,��������
            TParm result = CLPAssessStandardTool.getInstance().deleteCLP_EVL_CAT(
            "deleteCLP_EVL_CAT3", param);
            if(result.getErrCode() < 0){
                this.messageBox("ɾ��ʧ��!");
                return;
            }else{
                this.messageBox("ɾ���ɹ�!");
                CLP_EVL_CAT3.removeRow(select);
                this.clearValue("CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;PY3");
                return;
            }
            //����ֵ���ǳ��������ڴ���
        } else {
            this.messageBox("��������!");
            return ;
        }
    }

    /**
     * ���ڵ�һ��Table�Ϸ�������¼�
     */
    public void onSelect_1() {
        TParm parm = CLP_EVL_CAT1.getParmValue();
        //û������,����i
        if (parm == null || parm.getCount("CAT1_CODE") == 0) {
            return;
        }
        int select = CLP_EVL_CAT1.getSelectedRow();
        //û��ѡ��,����
        if (select < 0) {
            return;
        }
//        �������ó�2,��ʾҪ��������CLP_EVL_CAT1
        //��Ϊ����ԭ��,�����������λ��ʹ��Ƶ��,������㸳ֵ
        index = 2;
        //���¶���
        action = "update";
        //ȡ�ò�����ѡ���е�TParm
        TParm selectParm = parm.getRow(select);
        TTextField CAT1_CODE = (TTextField)this.getComponent("CAT1_CODE");
        this.setValueForParm("CAT1_CODE;CAT1_CHN_DESC;CAT1_ENG_DESC;PY1;DESCRIPTION1;SEQ1", selectParm);
        //���û�ѡ��ʱ,����CAT1_CODE,��ʱ,�û�����ʱ�Ǹ��¶���
        //��������Ϊϵͳԭ��,SEQ����ռ��޷���ֵ,�����ȡǿ�Ƹ�ֵ�ķ�ʽ
        String seq = selectParm.getValue("SEQ1");
        this.setValue("SEQ1",seq);
        //ѡ����һ������,��ȡ��������,����������ʾ�ڿؼ���,����code��ס
        CAT1_CODE.setEnabled(false);
        TParm param = new TParm();
        param.setData("CAT1_CODE", selectParm.getValue("CAT1_CODE"));
        ((TMenuItem)this.getComponent("add")).setEnabled(true);
        ((TMenuItem)this.getComponent("save")).setEnabled(true);
        this.clearValue("CAT2_CODE;CAT2_CHN_DESC;CAT2_ENG_DESC;"
        		+ "CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;PY2;PY3;"
        		+ "PY3;DESCRIPTION2;DESCRIPTION3;"
        		+ "SEQ2;SEQ3");
        //��������������
        CLP_EVL_CAT3.removeRowAll();
        //��ѯѡ��������ѡ����codeΪ���������,����ʾ��CLP_EVL_CAT2
        TParm result = CLPAssessStandardTool.getInstance().queryCLP_EVL_CAT(
        		"queryCLP_EVL_CAT2", param);
        if (result.getErrCode() >= 0) {
            CLP_EVL_CAT2.setParmValue(result);
        }
        
    }

    /**
     * ͬ��,һ���Ľṹ
     */
    public void onSelect_2() {
        TParm parm = CLP_EVL_CAT2.getParmValue();
        //û������,����i
        if (parm == null || parm.getCount("CAT2_CODE") == 0) {
            return;
        }
        int select = CLP_EVL_CAT2.getSelectedRow();
        //û��ѡ��,����
        if (select < 0) {
            return;
        }
        index = 3;
        action = "update";
        TParm selectParm = parm.getRow(select);
        selectParm.setData("PY2", selectParm.getValue("PY1"));
        TTextField CAT2_CODE = (TTextField)this.getComponent("CAT2_CODE");
        this.setValueForParm("CAT2_CODE;CAT2_CHN_DESC;CAT2_ENG_DESC;PY2;DESCRIPTION2;SEQ2", selectParm);
        //���û�ѡ��ʱ,����CHKUSER_CODE,��ʱ,�û�����ʱ�Ǹ��¶���
        CAT2_CODE.setEnabled(false);
        TParm param = this.getParmForTag("CAT1_CODE;CAT2_CODE");
        ((TMenuItem)this.getComponent("add")).setEnabled(true);
        ((TMenuItem)this.getComponent("save")).setEnabled(true);
        this.clearValue("CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;"
        		+ "SCORE;PY3;DESCRIPTION3;SEQ3");
        this.setValue("SEQ2",selectParm.getValue("SEQ2"));
        TParm result = CLPAssessStandardTool.getInstance().queryCLP_EVL_CAT(
        		"queryCLP_EVL_CAT3", param);
        if (result.getErrCode() >= 0) {
            CLP_EVL_CAT3.setParmValue(result);
        }

    }

    /**
     * ͬ��
     */
    public void onSelect_3() {
        TParm parm = CLP_EVL_CAT3.getParmValue();
        //û������,����i
        if (parm == null || parm.getCount("CAT3_CODE") == 0) {
            return;
        }
        int select = CLP_EVL_CAT3.getSelectedRow();
        //û��ѡ��,����
        if (select < 0) {
            return;
        }
        index = -1;
        action = "update";
        TParm selectParm = parm.getRow(select);
        selectParm.setData("PY3", selectParm.getValue("PY1"));
        TTextField CAT3_CODE = (TTextField)this.getComponent("CAT3_CODE");
        CAT3_CODE.setText(selectParm.getValue("CAT3_CODE"));
        this.setValueForParm("CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;SCORE;PY3;DESCRIPTION3;SEQ3", selectParm);
        //���û�ѡ��ʱ,����CHKUSER_CODE,��ʱ,�û�����ʱ�Ǹ��¶���
        CAT3_CODE.setEnabled(false);
        this.setValue("SEQ3",selectParm.getValue("SEQ3"));
        ((TMenuItem)this.getComponent("add")).setEnabled(false);
        ((TMenuItem)this.getComponent("save")).setEnabled(true);
    }

    /**
     * �������
     */
    public void onAdd() {
        //ȡ��TAg��ֵ
        TParm param = this.getParmForTag("CAT1_CODE;CAT2_CODE;CAT3_CODE");
        //ȡ�ò����ߵĻ��
        if (index == 1) {
        	//ȡ�õ�һ����������,���������������+1
        	TParm result = CLPAssessStandardTool.getInstance().queryMaxCode(
                "queryMaxCode1", param);
            if (result.getErrCode() >= 0) {
                String maxcode = "";
                //���û��ֵ����00��ʾ
                if (result.getCount("MAXCODE") == 0) {
                    maxcode = "00";
                }
                else {
                	//��ֵ��ȡ�ø�ֵ
                    maxcode = result.getValue("MAXCODE", 0);
                }
                //ͨ���������,����ȡ��һ��Ψһ��������
                maxcode = getNextCode("", maxcode);
                if (maxcode != null) {
                	//��������
                    TParm temp = new TParm();
                    temp.setData("CAT1_CODE", maxcode);
                    temp.setData("SEQ1", getSEQ(maxcode));
                    temp.setData("DESCRIPTION1", "");
                    temp.setData("CAT1_CHN_DESC", "");
                    temp.setData("CAT1_ENG_DESC", "");
                    temp.setData("OPT_USER", Operator.getID());
                    temp.setData("OPT_DATE", SystemTool.getInstance().getDate());
                    temp.setData("OPT_TERM", Operator.getIP());
                    temp.setData("PY1", "");
                    //������º�
                    CLP_EVL_CAT1.addRow(temp);
                    ((TTextField)this.getComponent("CAT1_CODE")).setEnabled(false);
                    this.setValueForParm("CAT1_CODE;CAT1_CHN_DESC;PY1;SEQ1;DESCRIPTION1", temp);
                }
            }
        }
        else if (index == 2) {
        	//��֤��Ҫ��������code�Ƿ��Ѿ��ڸ����д���,���������
        	//�ӵڶ�����ʼ,��ͬ�ڵ�һ�ű�,�е�ʱ��������һ�ű�����������,
        	//û�б�������ӵڶ����е�����,����ǲ������
        	TParm result = CLPAssessStandardTool.getInstance().queryMaxCode(
                    "isExists1", param);
        	//û�б��游������
        	if(result.getErrCode() < 0 || result.getCount("MAXCODE") == 0 
        			|| result.getInt("MAXCODE", 0) != 1){
        		this.messageBox("��Ŀǰ���ܶԸ÷��������ӷ���!");
        		return;
        	}
        	//ִ�е�����,�����Ѿ����˸���
            result = CLPAssessStandardTool.getInstance().queryMaxCode(
                "queryMaxCode2", param);
            //һ�´���ͬ��
            if (result.getErrCode() >= 0) {
                String maxcode = "";
                if (result.getCount("MAXCODE") == 0) {

                    maxcode = "00";
                }
                else {
                    maxcode = result.getValue("MAXCODE", 0);
                }
                maxcode = getNextCode(param.getValue("CAT1_CODE"), maxcode);
                if (maxcode != null) {
                    TParm temp = new TParm();
                    temp.setData("CAT2_CODE", maxcode);
                    temp.setData("SEQ2", getSEQ(maxcode));
                    temp.setData("CAT2_CHN_DESC", "");
                    temp.setData("CAT2_ENG_DESC", "");
                    temp.setData("DESCRIPTION2", "");
                    temp.setData("OPT_USER", Operator.getID());
                    temp.setData("OPT_DATE", SystemTool.getInstance().getDate());
                    temp.setData("OPT_TERM", Operator.getIP());
                    temp.setData("PY2", "");
                    CLP_EVL_CAT2.addRow(temp);
                    this.setValueForParm("CAT2_CODE;CAT2_CHN_DESC;PY2;SEQ2;DESCRIPTION2", temp);
                }
            }
        }
        else if (index == 3) {
        	//ͬ��
        	TParm result = CLPAssessStandardTool.getInstance().queryMaxCode(
                    "isExists2", param);
        	if(result.getErrCode() < 0 || result.getCount("MAXCODE") == 0 
        			|| result.getInt("MAXCODE", 0) != 1){
        		this.messageBox("��Ŀǰ���ܶԸ÷��������ӷ���!");
        		return;
        	}
            result = CLPAssessStandardTool.getInstance().queryMaxCode(
                "queryMaxCode3", param);
            if (result.getErrCode() >= 0) {
            	String maxcode = "";
                if (result.getCount("MAXCODE") == 0) {
                    maxcode = "00";
                }
                else {
                    maxcode = result.getValue("MAXCODE", 0);
                }
                maxcode = getNextCode(param.getValue("CAT2_CODE"), maxcode);
                if (maxcode != null) {
                    TParm temp = new TParm();
                    temp.setData("CAT3_CODE", maxcode);
                    temp.setData("SEQ3", getSEQ(maxcode));
                    temp.setData("CAT3_CHN_DESC", "");
                    temp.setData("CAT3_ENG_DESC", "");
                    temp.setData("DESCRIPTION3", "");
                    temp.setData("SCORE", 1);
                    temp.setData("OPT_USER", Operator.getID());
                    temp.setData("OPT_DATE", SystemTool.getInstance().getDate());
                    temp.setData("OPT_TERM", Operator.getIP());
                    temp.setData("PY3", "");
                    CLP_EVL_CAT3.addRow(temp);
                    this.setValueForParm("CAT3_CODE;CAT3_CHN_DESC;CAT3_ENG_DESC;"
                    		+ "SCORE;PY3;SEQ3;DESCRIPTION3", temp);
                }
            }
            //������������С�ڵ�,���������
        } else {
            this.messageBox("�Ѿ�����С�ڵ�,���Ѿ����������!");
            return;
        }
        action = "insert";
        ((TMenuItem)this.getComponent("add")).setEnabled(false);
        ((TMenuItem)this.getComponent("save")).setEnabled(true);
    }

    /**
     * ������һ������,ע��,�����õ��˵ݹ�,֪��Ѱ�ҵ�һ�����ʵĺ���<br>
     * �������Ĭ�ϵ������,ϵͳ������ˮ����,�������.���ǵ��û�ǿ��д���ݿ�,�����ִ���<br>
     * ������������ĳ���
     * 
     * @param maxCode String
     * @return String
     */
    private String getNextCode(String oldValue, String maxCode) {
        String code = "";
        if (maxCode == null || maxCode.equals("")) {
            maxCode = "00";
        }
        try {
            int num = Integer.parseInt(maxCode);
            if(num < 9){
                maxCode =  "0" + (num + 1);
            }else{
                maxCode = "" + (num + 1);
            }

        }catch (Exception e) {
            maxCode += "1";
        }
        code = oldValue + maxCode;
        TParm data = new TParm();
        data.setData("CAT3_CODE", code);
        data.setData("REGION_CODE", Operator.getRegion());
        TParm result = CLPAssessStandardTool.getInstance().queryMaxCode(
                "verify", data);
        if(result.getValue("COUNTS", 0).equals("0")){
            return code;
        }else{
            return getNextCode(oldValue, maxCode);
        }
    }
    
    
    /**
     * �������
     * @param param
     */
    private void insert(TParm param){
    	if (index == 1) {
        	if(param.getValue("CAT1_CHN_DESC").equals("")){
        		this.messageBox("���������˵������Ϊ��!");
        		return;
        	}
            TParm result = CLPAssessStandardTool.getInstance().
                insertCLP_EVL_CAT("insertCLP_EVL_CAT1", param);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + result.getErrText());
                this.messageBox("����ʧ��!" + result.getErrName() +
                                result.getErrText());
                return;
            }
            else {
                this.messageBox("����ɹ�!");
                onQuery();
                return;
            }
        }
        else if (index == 2) {
        	if(param.getValue("CAT2_CHN_DESC").equals("")){
        		this.messageBox("���������˵������Ϊ��!");
        		return;
        	}
            TParm result = CLPAssessStandardTool.getInstance().
                insertCLP_EVL_CAT("insertCLP_EVL_CAT2", param);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + result.getErrText());
                this.messageBox("����ʧ��!" + result.getErrName() +
                                result.getErrText());
                return;
            }
            else {
                this.messageBox("����ɹ�!");
                onSelect_1();
                return;
            }

        }
        else if (index == 3) {
        	
        	if(param.getValue("CAT3_CHN_DESC").equals("")){
        		this.messageBox("���������˵������Ϊ��!");
        		return;
        	}
        	try{
            	Double.parseDouble(param.getValue("SCORE"));
            } catch(NumberFormatException e) {
            	this.messageBox("���ķ�ֵ����Ĳ���ȷ!");
            	return;
            }
            TParm result = CLPAssessStandardTool.getInstance().
                insertCLP_EVL_CAT("insertCLP_EVL_CAT3", param);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + result.getErrText());
                this.messageBox("����ʧ��!" + result.getErrName() +
                                result.getErrText());
                return;
            }
            else {
                this.messageBox("����ɹ�!");
                onSelect_2();
                return;
            }
        }
        else {
            this.messageBox("����ʧ��,��������������ɵ���!");
            return;
        }
    }
    
    
    /**
     * ��������
     * @param param
     */
    private void update(TParm param){
    	//ȡ��TAg��ֵ
        param.setData("INDEX", index);
        //������˵�һ���б�,action=2,��ʱɾ�����ǵ�һ���б������
        if (index == 2) {
        	if(param.getValue("CAT1_CHN_DESC").equals("")){
        		this.messageBox("���������˵������Ϊ��!");
        		return;
        	}
        	try{
            	int seq = Integer.parseInt(param.getValue("SEQ1"));
            	if(seq <= 0){
            		throw new RuntimeException("˳��ű�����һ������0������!");
            	}
            } catch(Exception e) {
            	if(e instanceof RuntimeException){
            		this.messageBox("˳��ű�����һ������0������!");
            		this.setValue("SEQ1", 1);
            	} else {
            		this.messageBox("����ʧ��,λ���쳣!");
            	}
            	return;
            }
            TParm result = CLPAssessStandardTool.getInstance().updateCLP_EVL_CAT(
                    "updateCLP_EVL_CAT1", param);
            if(result.getErrCode() < 0){
                this.messageBox("����ʧ��!");
                return;
            }else{
                this.messageBox("���³ɹ�!");
                onQuery();
                
                return;
            }
            //������˵ڶ����б�,action=3,��ʱɾ�����ǵڶ����б������
        } else if(index == 3){
        	if(param.getValue("CAT2_CHN_DESC").equals("")){
        		this.messageBox("�����з���˵������Ϊ��!");
        		return;
        	}
        	try{
            	int seq = Integer.parseInt(param.getValue("SEQ2"));
            	if(seq <= 0){
            		throw new RuntimeException("˳��ű�����һ������0������!");
            	}
            } catch(Exception e) {
            	if(e instanceof RuntimeException){
            		this.messageBox("˳��ű�����һ������0������!");
            		this.setValue("SEQ2", 1);
            	} else {
            		this.messageBox("����ʧ��,λ���쳣!");
            	}
            	return;
            }
            //��ɾ���ڶ��ű�ĵ�������ʱ,Ҫͬʱɾ�������ű��е��Եڶ��ű�CAT2_CODEΪ�������������
            TParm result = CLPAssessStandardTool.getInstance().updateCLP_EVL_CAT(
                    "updateCLP_EVL_CAT2", param);
            if(result.getErrCode() < 0){
                this.messageBox("����ʧ��!");
                return;
            }else{
                this.messageBox("���³ɹ�!");
                onSelect_1();
                
                return;
            }
            //������˵������б�,action=-1,��ʱɾ�����ǵ������б������
        }else if(index == -1){
        	if(param.getValue("CAT3_CHN_DESC").equals("")){
        		this.messageBox("����С�ַ���˵������Ϊ��!");
        		return;
        	}
        	try{
            	Double.parseDouble(param.getValue("SCORE"));
            	int seq = Integer.parseInt(param.getValue("SEQ3"));
            	if(seq <= 0){
            		throw new RuntimeException("˳��ű�����һ������0������!");
            	}
            } catch(Exception e) {
            	if(e instanceof NumberFormatException){
            		this.messageBox("���ķ�ֵ����Ĳ���ȷ!");
            		this.setValue("SCORE", 1);
            	} else if(e instanceof RuntimeException){
            		this.messageBox("˳��ű�����һ������0������!");
            		this.setValue("SEQ3", 1);
            	} else {
            		this.messageBox("����ʧ��,λ���쳣!");
            	}
            	return;
            }
            
            TParm result = CLPAssessStandardTool.getInstance().updateCLP_EVL_CAT(
            "updateCLP_EVL_CAT3", param);
            if(result.getErrCode() < 0){
                this.messageBox("����ʧ��!");
                return;
            }else{
                this.messageBox("���³ɹ�!");
                onSelect_2();
                
                return;
            }
        }else{
            this.messageBox("��������!");
            return ;
        }
        
        
    }
    
    
    /**
     * ����Ĭ�ϵ�˳���
     * @param code
     * @return
     */
    private String getSEQ(String code){
    	if(code == null || code.equals("")){
    		return "0";
    	}
    	String seq = code.substring(code.length() - 2);
    	return Integer.parseInt(seq) + "";
    }
}

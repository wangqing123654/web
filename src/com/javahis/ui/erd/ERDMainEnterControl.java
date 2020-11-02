package com.javahis.ui.erd;

import com.dongyang.control.TControl;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import com.dongyang.ui.TPanel;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.erd.ErdForBedAndRecordTool;
import jdo.reg.PatAdmTool;
import jdo.sys.PatTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.ekt.EKTIO;
import jdo.sys.Operator;

/**
 * <p>Title: </p>
 *
 * <p>Description: סԺ�������������</p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH 2009-9-10
 *
 * @version 1.0
 */

public class ERDMainEnterControl
    extends TControl {

    //�����������
    private String runFlg = "RECORD";

    //ҽ�����
    private TRadioButton Radio0;
    private TRadioButton Radio1;
    private TRadioButton Radio2;

    private TTextFormat from_Date;
    private TTextFormat to_Date;

    private TTextField MR_NO;
    private TTextField NAME;

    private TPanel panel;
    private TTable table;

    private String workPanelTag;

    public ERDMainEnterControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        initParmFromOutside();
        //������ĳ�ʼ��
        myInitControler();
        onQuery();
    }

    /**
     * ��ʼ���������caseNo/stationCode�����ⲿ����������洫���Ĳ�����
     */
    public void initParmFromOutside() {
        //�ӿ���õ�
        Object obj = this.getParameter();
//        this.messageBox_(outsideParm);
        if (obj != null) {
            this.setRunFlg(this.getParameter().toString());
        }
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        table.setParmValue(new TParm());
        //��ʼ����ǰtable
        initTable();
    }

    /**
     * ��ʼ��table
     * ��ѯ�����ǣ� caseNo/����
     */
    public void initTable() {

        TParm selParm = new TParm();
        selParm = getQueryParm();
        if(selParm == null)
            return;
        TParm query = ErdForBedAndRecordTool.getInstance().selPat(selParm);
        filterQuery(query);
        if (query.getCount() <= 0) {
            table.setParmValue(query);
            this.messageBox("û��������ݣ�");
            return;
        }
        //ѭ���޸�ʱ���ʽ
        for (int i = 0; i < query.getCount(); i++) {
            String regDate = StringTool.getString( (Timestamp) query.getData(
                "REG_DATE", i), "yyyy/MM/dd");
            query.setData("REG_DATE", i, regDate);
        }
        table.setParmValue(query);
        return;
    }

    private void filterQuery(TParm query){
        int row = query.getCount();
        int effDays = getRegParm().getInt("EFFECT_DAYS",0);
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        for(int i = row-1;i >= 0;i--){
            if(effDays < StringTool.getDateDiffer(now,query.getTimestamp("ADM_DATE",i))){
                query.removeRow(i);
                continue;
            }
            return;
        }
    }


    private TParm getRegParm(){
        String SQL="SELECT EFFECT_DAYS FROM REG_SYSPARM";
        return new TParm(TJDODBTool.getInstance().select(SQL));
    }


    /**
     * ��ý����ϵ����в�ѯ����
     * @return TParm
     */
    public TParm getQueryParm() {

        TParm parm = new TParm();
        if (Radio0.isSelected())
            parm.setData("ADM_STATUS", 2); //����
        else if (Radio1.isSelected())
            parm.setData("ADM_STATUS", 6); //����
        else if (Radio2.isSelected()){
            if(getValueString("from_Date").length() != 0 &&
               getValueString("to_Date").length() == 0){
                messageBox("ʱ�����ڲ��Ϸ�");
                return null;
            }
            if(getValueString("from_Date").length() == 0 &&
               getValueString("to_Date").length() != 0){
                messageBox("ʱ�����ڲ��Ϸ�");
                return null;
            }
            if(getValueString("from_Date").length() != 0 &&
               getValueString("to_Date").length() != 0){
                if(getValueString("from_Date").compareTo(getValueString("to_Date"))>0){
                    messageBox("ʱ�����ڲ��Ϸ�");
                    return null;
                }
                parm.setData("START_DATE", this.getValue("from_Date"));
                parm.setData("END_DATE", this.getValue("to_Date"));
            }
            parm.setData("ADM_STATUS", 9); //��ת��

        }
        //ͨ��MR_NO�õ�CASE_NO
        String mrNo = MR_NO.getValue();
        if (!"".equals(mrNo)) {
            String case_no = ( (TParm) PatAdmTool.getInstance().
                              selMaxCaseNoByMrNo(PatTool.getInstance().checkMrno(getValueString("MR_NO")),Operator.getRegion())).getValue("CASE_NO", 0);
            parm.setData("CASE_NO", case_no);
        }
        if(getValueString("ERD_REGION").length() != 0)
            parm.setData("ERD_REGION",getValue("ERD_REGION"));
        if(getValueString("MR_NO").length() != 0)
            parm.setData("MR_NO",PatTool.getInstance().checkMrno(getValueString("MR_NO")));
        return parm;
    }

    /**
     * ���ȵõ�����UI�Ŀؼ�����/ע����Ӧ���¼�
     */

    public void myInitControler() {

        //�õ�ʱ��ؼ�
        from_Date = (TTextFormat)this.getComponent("from_Date");
        to_Date = (TTextFormat)this.getComponent("to_Date");

        //�õ�table�ؼ�
        table = (TTable)this.getComponent("TABLE");

        //�õ���ѯ����UI�Ķ���
        Radio0 = (TRadioButton)this.getComponent("Radio0");
        Radio1 = (TRadioButton)this.getComponent("Radio1");
        Radio2 = (TRadioButton)this.getComponent("Radio2");

        MR_NO = (TTextField)this.getComponent("MR_NO");
        NAME = (TTextField)this.getComponent("NAME");

        panel = (TPanel)this.getComponent("PANEL");

        //TABLE˫���¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE" + "->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubled");
    }

    /**
     * ˫���¼�
     * @param row int
     */
    public void onTableDoubled(int row) {
        if (row < 0)
            return;
//        lockUpContorl(false);
        TParm tableValue = table.getParmValue();
        String mrNo = (String) tableValue.getData("MR_NO", row);
        String name = (String) tableValue.getData("PAT_NAME", row);
        String caseNo = (String) tableValue.getData("CASE_NO", row);
        if("RECORD".equals(getRunFlg())){
        	 lockUpContorl(false);
            //���ý��洫��
            TParm parmToErd = new TParm();
            parmToErd.setData("MR_NO", mrNo);
            parmToErd.setData("PAT_NAME", name);
            parmToErd.setData("CASE_NO", caseNo);
            //��ʿ���ñ�ǣ��б���ҽ��
            parmToErd.setData("FLG", "NURSE");
            //����ERD��¼������
            table.setVisible(false);
            panel.addItem("ERDDynamicRcd", "%ROOT%\\config\\erd\\ERDDynamicRcd.x",
                          parmToErd, false);
            workPanelTag="ERDDynamicRcd";

        }else if("CHECK".equals(getRunFlg())){
        	
            //===ֻ�����ۿ��Խ���������
            if(this.getValueBoolean("Radio1")){//������
            	 lockUpContorl(false);
            	 //���ý��洫��
                TParm parmToExec = new TParm();
                parmToExec.setData("MR_NO", mrNo);
                parmToExec.setData("PAT_NAME", name);
                parmToExec.setData("CASE_NO", caseNo);
                //����ERD��¼������
                table.setVisible(false);
            	panel.addItem("ERDDynamicRcd", "%ROOT%\\config\\erd\\ERDOrderExecMain.x",
                        parmToExec, false);
          workPanelTag="ERDDynamicRcd";
            }else{
            	this.messageBox("�������в��ɽ��롣");
            	return;
            }
            
        }
    }

    /**
     * ����
     */
    public void onClick() {
        String mrNo = (String) table.getValueAt(table.getSelectedRow(), 1);
        String name = (String) table.getValueAt(table.getSelectedRow(), 2);
        //��ʼ���ؼ�
        MR_NO.setValue(mrNo);
        NAME.setValue(name);
        TParm tableParm = table.getParmValue();
        setValue("ERD_REGION",tableParm.getValue("ERD_REGION_CODE",table.getSelectedRow()));
    }

    /**
     * ����MR_NO
     */
    public void onMrNo() {
        String mrNo = MR_NO.getValue();
        MR_NO.setValue(PatTool.getInstance().checkMrno(mrNo));
        //�õ���������
        getPatName(mrNo);
    }

    /**
     * ��øò��˵�����
     * @param mrNo String
     */
    private void getPatName(String mrNo) {
        NAME.setValue(PatTool.getInstance().getNameForMrno(mrNo));
    }

    /**
     * ����ʱ��ؼ�
     */
    public void onGetDate(Object flg) {

        if ("2".equals(flg + "")) {
            Timestamp now = TJDODBTool.getInstance().getDBTime();
            String dateTime = (now + "").replace("-","");
            from_Date.setValue(StringTool.getDate(dateTime.substring(0,8)+"000000","yyyyMMddHHmmss"));
            to_Date.setValue(now);
            from_Date.setEnabled(true);
            to_Date.setEnabled(true);
        }
        else {
            from_Date.setValue(null);
            to_Date.setValue(null);
            from_Date.setEnabled(false);
            to_Date.setEnabled(false);
        }
        //���
        table.setParmValue(new TParm());
        onQuery();
    }


    /**
     * �رչ���ҳ��
     * @return boolean
     */
    public Object onClose() {
        if (workPanelTag == null || workPanelTag.length() == 0)
            return null;
        TPanel p = (TPanel) getComponent(workPanelTag);
        if(!p.getControl().onClosing()){
            return "OK";
        }
        panel.remove(p);
        workPanelTag=null;
        table.setVisible(true);
        //�Ƴ���UIMenuBar
        callFunction("UI|removeChildMenuBar");
        //�Ƴ���UIToolBar
        callFunction("UI|removeChildToolBar");
        //��ʾUIshowTopMenu
        callFunction("UI|showTopMenu");
        lockUpContorl(true);
        onQuery();
        return "OK";
    }

    /**
     * �������ϱߵĿؼ�
     * @param flg boolean
     */
    private void lockUpContorl(boolean flg){
        Radio0.setEnabled(flg);
        Radio1.setEnabled(flg);
        Radio2.setEnabled(flg);
        MR_NO.setEnabled(flg);
        NAME.setEnabled(flg);
    }



    public String getRunFlg() {
        return runFlg;
    }

    public void setRunFlg(String runFlg) {
        this.runFlg = runFlg;
    }

    /**
     * ��շ���
     */
    public void onClear(){
        setValue("MR_NO","");
        setValue("NAME","");
        setValue("ERD_REGION","");
        ((TTable)getComponent("TABLE")).removeRowAll();
    }

    /**
     * ��ʾ�����˵�
     */
    public void showMwnu(){
        if (workPanelTag == null || workPanelTag.length() == 0){
            //��ʾUIshowTopMenu
            callFunction("UI|showTopMenu");
            return;
        }
        TPanel p = (TPanel) getComponent(workPanelTag);
        p.getControl().callFunction("onShowWindowsFunction");
    }

    /**
     * ҽ�ƿ���������
     */
    public void onEKT(){
    	TParm patParm = EKTIO.getInstance().TXreadEKT();
//        TParm patParm = EKTIO.getInstance().getPat();
        if (patParm.getErrCode() < 0) {
            this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
            return;
        }
        setValue("MR_NO",patParm.getValue("MR_NO"));
       onMrNo();
    }

    //��������
    public static void main(String[] args) {
        JavaHisDebug.initClient();
        //JavaHisDebug.TBuilder();

//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("erd\\ERDMainEnter.x");
    }


}

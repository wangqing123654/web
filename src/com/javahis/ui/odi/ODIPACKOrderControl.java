package com.javahis.ui.odi;

import com.dongyang.control.*;
import com.dongyang.data.*;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import java.util.List;
import java.util.ArrayList;

/**
 * <p>Title: HISҽ��ϵͳ</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author WangM
 * @version 1.0
 */
public class ODIPACKOrderControl extends TControl {
    /**
     * �������
     */
    private TParm parmmeter;
    /**
     * ����ϵͳ ODI ҽ��վסԺ IBS סԺ�Ƽ�
     */
    private String system;
    /**
     * ���ÿ���
     */
    private String deptCode;
    /**
     * ������Ա
     */
    private String userId;
    /**
     * Ȩ�� 0,��ʱ 1������ 2,��Ժ��ҩ 3,��ҩ ,4ȫ��
     */
    private int ruleType;
    /**
     * �ײ����
     */
    private String deptOrdr;

    public void onInitParameter() {
        //��������
    }

    /**
     * ��ʼ������
     */
    public void onInit(){
//        super.onInit();
        this.parmmeter = new TParm();
        Object obj = this.getParameter();
//        if(obj==null||obj.toString().length()==0)
//            onInitPage();
        if(obj.toString().length()!=0||obj!=null){
            this.parmmeter = (TParm)obj;
        }
        onInitPage();
    }
    /**
     * ��ʼ��ҳ��
     */
    public void onInitPage(){
        //TABLE1�����¼�
       callFunction("UI|" + "TABLE1" + "|addEventListener",
                    "TABLE1" + "->" + TTableEvent.CLICKED, this,
                    "onTableClicked");
       //TABLE2�����¼�
       callFunction("UI|" + "TABLE2" + "|addEventListener",
                    "TABLE2" + "->" + TTableEvent.CLICKED, this,
                    "onTableClicked");
       //TABLE3�����¼�
       callFunction("UI|" + "TABLE3" + "|addEventListener",
                    "TABLE3" + "->" + TTableEvent.CLICKED, this,
                    "onTableClicked");
       //TABLE4�����¼�
       callFunction("UI|" + "TABLE4" + "|addEventListener",
                    "TABLE4" + "->" + TTableEvent.CLICKED, this,
                    "onTableClicked");
       //����¼�
        callFunction("UI|" + "ITEMTABLE" + "|addEventListener",
                    "ITEMTABLE" + "->" + TTableEvent.CLICKED, this,
                    "onTableClickedItem");

        this.setSystem(this.parmmeter.getValue("SYSTEM_TYPE"));
        this.setDeptCode(this.parmmeter.getValue("DEPT_CODE"));
        this.setUserId(this.parmmeter.getValue("USER_ID"));
        this.setRuleType(this.parmmeter.getInt("RULE_TYPE"));
        this.setDeptOrdr(this.parmmeter.getValue("DEPT_OR_DR"));
        if("ODI".equals(this.getSystem())){
            if(getRuleType()==4){
                dbaPage();
            }else{
                normalPage();
            }
        }
        if("IBS".equals(this.getSystem())){
            if(getRuleType()==4){
                dbaPage();
            }else{
                normalPage();
            }
        }
    }

    /**
     * ����¼�
     * @param row int
     */
    public void onTableClicked(int row) {
        this.getTTable("ITEMTABLE").acceptText();
        this.getTTable("ITEMTABLE").clearSelection();
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        String packCode = "";
        switch (tab.getSelectedIndex()) {
            case 0:
                TParm tempST = this.getTTable("TABLE1").getParmValue().getRow(row);
                packCode = tempST.getValue("PACK_CODE");
                break;
            case 1:
                TParm tempUD = this.getTTable("TABLE2").getParmValue().getRow(row);
                packCode = tempUD.getValue("PACK_CODE");
                break;
            case 2:
                TParm tempDS = this.getTTable("TABLE3").getParmValue().getRow(row);
                packCode = tempDS.getValue("PACK_CODE");
                break;
            case 3:
                TParm tempIG = this.getTTable("TABLE4").getParmValue().getRow(row);
                packCode = tempIG.getValue("PACK_CODE");
                break;
        }
        this.onQueryItem(packCode);
    }
    /**
     * ��ѯ�ײ�ϸ��
     * @param packCode String
     */
    public void onQueryItem(String packCode){
    	//$$================ del by lx 2012/04/14    ȥ�� �ظ��Ĺ��  start=====================================$$//
    	//B.SPECIFICATION,
        String sql = "SELECT 'Y' AS FLG,A.PACK_CODE,A.SEQ_NO,A.DESCRIPTION,A.SPECIFICATION,A.ORDER_DESC,A.TRADE_ENG_DESC, "+
            " A.LINKMAIN_FLG,A.LINK_NO,A.ORDER_CODE,A.MEDI_QTY,A.MEDI_UNIT,A.DEPT_OR_DR,A.DEPTORDR_CODE, "+
            " A.ROUTE_CODE,A.FREQ_CODE,A.DCTEXCEP_CODE,A.TAKE_DAYS,B.PY1,B.PY2, "+
            " B.GOODS_DESC,B.GOODS_PYCODE,B.ALIAS_DESC,B.ALIAS_PYCODE,B.NHI_FEE_DESC,B.HABITAT_TYPE, "+
            " B.MAN_CODE,B.HYGIENE_TRADE_CODE,B.CHARGE_HOSP_CODE,B.OWN_PRICE,B.NHI_PRICE,B.GOV_PRICE,B.UNIT_CODE,B.LET_KEYIN_FLG, "+
            " B.DISCOUNT_FLG,B.EXPENSIVE_FLG,B.OPD_FIT_FLG,B.EMG_FIT_FLG,B.IPD_FIT_FLG,B.HRM_FIT_FLG,B.DR_ORDER_FLG,B.INTV_ORDER_FLG, "+
            " B.LCS_CLASS_CODE,B.TRANS_OUT_FLG,B.TRANS_HOSP_CODE,B.USEDEPT_CODE,B.EXEC_ORDER_FLG,B.EXEC_DEPT_CODE,B.INSPAY_TYPE, "+
            " B.ADDPAY_RATE,B.ADDPAY_AMT,B.NHI_CODE_O,B.NHI_CODE_E,B.NHI_CODE_I,B.CTRL_FLG,B.CLPGROUP_CODE,B.ORDERSET_FLG,B.INDV_FLG, "+
            " B.SUB_SYSTEM_CODE,B.RPTTYPE_CODE,B.DEV_CODE,B.OPTITEM_CODE,B.MR_CODE,B.DEGREE_CODE,B.CIS_FLG,B.ACTION_CODE,B.ATC_FLG,B.OWN_PRICE2, "+
            " B.OWN_PRICE3,B.TUBE_TYPE,B.ACTIVE_FLG,B.IS_REMARK,B.ATC_FLG_I,B.CAT1_TYPE,B.ORDER_CAT1_CODE "+
            " FROM ODI_PACK_ORDER A,SYS_FEE B WHERE PACK_CODE='"+packCode+"' AND A.ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y' "+
            " ORDER BY A.PACK_CODE,A.SEQ_NO ";
      //$$================ del by lx 2012/04/14    ȥ�� �ظ��Ĺ��  end=====================================$$//
        //System.out.println(sql);

        TParm parm = new TParm(this.getDBTool().select(sql));
        //System.out.println("======parm====="+parm);
        this.getTTable("ITEMTABLE").setParmValue(parm);
    }
    /**
     * ����
     */
    public void onSend(){
        this.getTTable("ITEMTABLE").acceptText();
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        String rxKind = getRxKindString(tab.getSelectedIndex());
        TParm parm = this.getTTable("ITEMTABLE").getParmValue();
        int rowCount = parm.getCount();
        List orderList = new ArrayList();
        for(int i=0;i<rowCount;i++){
            TParm temp = parm.getRow(i);
            if("N".equals(temp.getValue("FLG")))
                continue;
            temp.setData("RX_KIND",rxKind);
            orderList.add(temp);
        }
        this.parmmeter.runListener("INSERT_TABLE",orderList);
    }
    /**
     * �ر��¼�
     * @return boolean
     */
    public boolean onClosing(){
        TParm parm = new TParm();
        parm.setData("YYLIST","Y");
        this.parmmeter.runListener("INSERT_TABLE_FLG",parm);
        return true;
    }

    /**
     * TABLE����¼�
     * @param row int
     */
    public void onTableClickedItem(int row){
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        switch (tab.getSelectedIndex()) {
            case 0:
                this.getTTable("TABLE1").acceptText();
                this.getTTable("TABLE1").clearSelection();
                break;
            case 1:
                this.getTTable("TABLE2").acceptText();
                this.getTTable("TABLE2").clearSelection();
                break;
            case 2:
                this.getTTable("TABLE3").acceptText();
                this.getTTable("TABLE3").clearSelection();
                break;
            case 3:
                this.getTTable("TABLE4").acceptText();
                this.getTTable("TABLE4").clearSelection();
                break;
        }
    }

    public void onChange(){
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        switch (tab.getSelectedIndex()) {
            case 0:
                //��������TABLE
                this.getTTable("ITEMTABLE").setHeader("��,30,boolean;��,30,boolean;��,50,int;ҽ������,260;����,60,double,#########0.000;��λ,60,UNIT_COMBO;�÷�,60,ROUTE_CODE;Ƶ��,90,FREQ_CODE;���,180;��ע,200");
                this.getTTable("ITEMTABLE").setLockColumns("1,2,3,4,5,6,7,8,9");
                this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("1,rigth;2,left;3,right;5,left;6,left");
                this.getTTable("ITEMTABLE").setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;FREQ_CODE;SPECIFICATION;DESCRIPTION");
                this.onQuery(0);
                break;
            case 1:
                this.getTTable("ITEMTABLE").setHeader("��,30,boolean;��,30,boolean;��,50,int;ҽ������,260;����,60,double,#########0.000;��λ,60,UNIT_COMBO;�÷�,60,ROUTE_CODE;Ƶ��,90,FREQ_CODE;���,180;��ע,200");
                this.getTTable("ITEMTABLE").setLockColumns("1,2,3,4,5,6,7,8,9");
                this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("1,rigth;2,left;3,right;5,left;6,left");
                this.getTTable("ITEMTABLE").setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;FREQ_CODE;SPECIFICATION;DESCRIPTION");
                this.onQuery(1);
                break;
            case 2:
                this.getTTable("ITEMTABLE").setHeader("��,30,boolean;��,30,boolean;��,50,int;ҽ������,260;����,60,double,#########0.000;��λ,60,UNIT_COMBO;�÷�,60,ROUTE_CODE;����,40,int;Ƶ��,90,FREQ_CODE;���,180;��ע,200");
                this.getTTable("ITEMTABLE").setLockColumns("1,2,3,4,5,6,7,8,9,10");
                this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("1,rigth;2,left;3,right;5,left;6,left");
                this.getTTable("ITEMTABLE").setParmMap("FLG;LINKMAIN_FLG;LINK_NO;ORDER_DESC;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;TAKE_DAYS;FREQ_CODE;SPECIFICATION;DESCRIPTION");
                this.onQuery(2);
                break;
            case 3:
                this.getTTable("ITEMTABLE").setHeader("��,30,boolean;ҽ������,120;����,100,double,#########0.000;����巨,100,DCTAGENT_COMBO");
                this.getTTable("ITEMTABLE").setLockColumns("1,2,3");
                this.getTTable("ITEMTABLE").setColumnHorizontalAlignmentData("0,left;1,right;2,left;3,left;4,right;5,left;6,left;7,right;8,left;9,left;10,right;11,left;");
                this.getTTable("ITEMTABLE").setParmMap("FLG;ORDER_DESC;MEDI_QTY;DCTEXCEP_CODE");
                this.onQuery(3);
                break;
        }

    }
    /**
     * ��ͨ��ѯ
     */
    public void normalPage(){
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        switch (this.getRuleType()) {
            case 0:
                tab.setEnabledAt(1,false);
                tab.setEnabledAt(2,false);
                tab.setEnabledAt(3,false);
                tab.setSelectedIndex(0);
                this.onQuery(0);
                return;
            case 1:
                tab.setEnabledAt(0,false);
                tab.setEnabledAt(2,false);
                tab.setEnabledAt(3,false);
                tab.setSelectedIndex(1);
                break;
            case 2:
                tab.setEnabledAt(0,false);
                tab.setEnabledAt(1,false);
                tab.setEnabledAt(3,false);
                tab.setSelectedIndex(2);
                break;
            case 3:
                tab.setEnabledAt(0,false);
                tab.setEnabledAt(1,false);
                tab.setEnabledAt(2,false);
                tab.setSelectedIndex(3);
                break;
        }
        //��ѯ
//        this.onQuery(tab.getSelectedIndex());
    }
    /**
     * �õ�TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    /**
     * �õ�����|��Ա
     * @param deptOrDr String
     * @return String
     */
    public String getDeptOrDrCodeString(String deptOrDr){
        String deptOrDrCode = "";
        if("1".equals(deptOrDr)||"3".equals(deptOrDr)){
            deptOrDrCode =this.getDeptCode();
        }
        if("2".equals(deptOrDr)||"4".equals(deptOrDr)){
            deptOrDrCode = this.getUserId();
        }
        return deptOrDrCode;
    }
    /**
     * ȫѡ
     */
    public void onSelAll(){
        this.getTTable("ITEMTABLE").acceptText();
        TParm parm = this.getTTable("ITEMTABLE").getParmValue();
        int rowCount = parm.getCount();
        for(int i=0;i<rowCount;i++){
            if(parm.getBoolean("FLG",i)){
                parm.setData("FLG",i,false);
            }else{
                parm.setData("FLG",i,true);
            }
        }
        this.getTTable("ITEMTABLE").setParmValue(parm);
    }
    /**
     * ��ѯ
     */
    public void onQuery(int type){
        String sql = "";
        if("1".equals(this.getDeptOrdr())||"3".equals(this.getDeptOrdr())){
            sql = "SELECT A.PACK_CODE,A.DEPT_OR_DR,B.DEPT_CHN_DESC AS DEPTUSER,A.RX_KIND,A.PACK_DESC,A.GFREQ_CODE,A.GROUTE_CODE,A.GDCTAGENT_CODE,A.OPT_USER,A.OPT_TERM,A.OPT_DATE FROM ODI_PACK_MAIN A,SYS_DEPT B  WHERE A.DEPT_OR_DR='"+this.getDeptOrdr()+"' AND A.DEPTORDR_CODE='"+getDeptOrDrCodeString(this.getDeptOrdr())+"' AND A.RX_KIND='"+getRxKindString(type)+"' AND A.DEPTORDR_CODE=B.DEPT_CODE";
        }
        if("2".equals(this.getDeptOrdr())||"4".equals(this.getDeptOrdr())){
            sql = "SELECT A.PACK_CODE,A.DEPT_OR_DR,B.USER_NAME AS DEPTUSER,A.RX_KIND,A.PACK_DESC,A.GFREQ_CODE,A.GROUTE_CODE,A.GDCTAGENT_CODE,A.OPT_USER,A.OPT_TERM,A.OPT_DATE FROM ODI_PACK_MAIN A,SYS_OPERATOR B  WHERE A.DEPT_OR_DR='"+this.getDeptOrdr()+"' AND A.DEPTORDR_CODE='"+getDeptOrDrCodeString(this.getDeptOrdr())+"' AND A.RX_KIND='"+getRxKindString(type)+"' AND A.DEPTORDR_CODE=B.USER_ID";
        }
        TParm parm = new TParm(this.getDBTool().select(sql));
        this.getTTable("TABLE"+(type+1)).setParmValue(parm);
    }
    public String getRxKindString(int type){
        String rxKind = "ST";
        switch (type) {
            case 0:
                rxKind = "ST";
                break;
            case 1:
                rxKind = "UD";
                break;
            case 2:
                rxKind = "DS";
                break;
            case 3:
                rxKind = "IG";
                break;
        }
        return rxKind;

    }

    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * ��ͨ��ѯ
     */
    public void dbaPage(){
        TTabbedPane tab = (TTabbedPane)this.getComponent("TTABBEDPANE");
        onQuery(tab.getSelectedIndex());
    }
    public String getSystem() {
        return system;
    }

    public String getUserId() {
        return userId;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public int getRuleType() {
        return ruleType;
    }

    public String getDeptOrdr() {
        return deptOrdr;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRuleType(int ruleType) {
        this.ruleType = ruleType;
    }

    public void setDeptOrdr(String deptOrdr) {
        this.deptOrdr = deptOrdr;
    }
}

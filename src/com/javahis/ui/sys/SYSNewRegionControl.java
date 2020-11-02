package com.javahis.ui.sys;

import com.dongyang.control.*;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import jdo.sys.SYSNewRegionTool;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TMenuItem;
import com.javahis.util.JavaHisDebug;

/**
 * <p>Title:���� </p>
 *
 * <p>Description: ����</p>
 *
 * <p>Copyright: Copyright (c)  Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author zhangkun 20090226
 * @version 1.0
 */
public class SYSNewRegionControl extends TControl {

    //��ѯ�ֵ���еĹ��� statelist �е�����
    TParm stateList = SYSNewRegionTool.getInstance().getStateList("SYS_REGION_FLG");
    //��¼���ѡ������
    int selectedRowIndex = -1;
    public SYSNewRegionControl() {
    }

    public void onInit(){
        super.onInit();
        this.getTable("TABLE").onQuery();
        //ɾ��Ȩ��
        if(!this.getPopedem("delPopedem")){
            ((TMenuItem)this.getComponent("delete")).setVisible(false);
        }
    }
    /**
     * ��ȡָ�����Ƶ�Table
     * @param tableName String Table�ؼ���
     * @return TTable
     */
    private TTable getTable(String tableName){
        TTable table = (TTable)this.getComponent(tableName);
        return table;
    }

    /**
     * �е���¼�
     */
    public void RowClicked(){
//        this.messageBox("�е���¼�");
        TTextField txt = (TTextField)this.getComponent("REGION_CODE");
        txt.setEditable(false);//���á�������롱���ɱ༭
        TTable table = this.getTable("TABLE");
        TParm data = table.getParmValue();//��ȡ����
        selectedRowIndex = table.getSelectedRow();//��ȡѡ���к�
        String STATE_LIST = data.getValue("STATE_LIST",selectedRowIndex);//��ȡ�����λ�б�����
        int count = stateList.getCount("ID");//Ҫ��ʾ���ֶθ���
        char[] s_list = STATE_LIST.toCharArray();
        data.setDataN("ss",getValue("sss"));
        if(count>0&&count==s_list.length){
            for(int i=0;i<count;i++){
                this.setValue(stateList.getValue("ID",i),String.valueOf(s_list[i]));
            }
        }
    }
    /**
     * �����¼�
     */
    public void onSave(){
        //�жϱ����ֶ�
        if(this.getValue("REGION_CODE").equals("")|| this.getValue("REGION_CHN_DESC").equals("")||this.getValue("REGION_CHN_ABN").equals("")){
            this.messageBox("����д�������ƺ����ļ�ƣ�");
            return;
        }
        //-----------��������ֵ-----------------
        TParm parm = new TParm();
        parm.setData("REGION_CODE",this.getValue("REGION_CODE"));
        parm.setData("NHI_NO",this.getValue("NHI_NO"));
        parm.setData("MAIN_FLG",this.getValue("MAIN_FLG"));
        parm.setData("HOSP_CLASS",this.getValue("HOSP_CLASS"));//�б��
        parm.setData("REGION_CHN_DESC",this.getValue("REGION_CHN_DESC"));
        parm.setData("REGION_CHN_ABN",this.getValue("REGION_CHN_ABN"));
        parm.setData("PY1",this.getValue("PY1"));
        parm.setData("REGION_ENG_DESC",this.getValue("REGION_ENG_DESC"));
        parm.setData("REGION_ENG_ABN",this.getValue("REGION_ENG_ABN"));
        parm.setData("DESCRIPTION",this.getValue("DESCRIPTION"));
        parm.setData("PY2",this.getValue("PY2"));
        parm.setData("SEQ",this.getValue("SEQ"));
        parm.setData("SUPERINTENDENT",this.getValue("SUPERINTENDENT"));
        parm.setData("NHIMAIN_NAME",this.getValue("NHIMAIN_NAME"));
        parm.setData("REGION_TEL",this.getValue("REGION_TEL"));
        parm.setData("REGION_FAX",this.getValue("REGION_FAX"));
        parm.setData("REGION_ADDR",this.getValue("REGION_ADDR"));
        parm.setData("E_MAIL",this.getValue("E_MAIL"));
        parm.setData("ACTIVE_FLG",this.getValue("ACTIVE_FLG"));
        parm.setData("IP_RANGE_START",this.getValue("IP_RANGE_START"));
        parm.setData("IP_RANGE_END",this.getValue("IP_RANGE_END"));
        parm.setData("AP_IP_ADDR",this.getValue("AP_IP_ADDR"));
        //�ж����ֿ��Ƿ�Ϊnull
        if(this.getValue("TOP_BEDFEE")==null){
            parm.setData("TOP_BEDFEE", "");
        }
        else{
            parm.setData("TOP_BEDFEE", this.getValue("TOP_BEDFEE"));
        }
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        //��ȡ��ʶ�б����
        int list_num = stateList.getCount("ID");
        String state = "";
        if(list_num>0){
            for(int i=0;i<list_num;i++){
                state += this.getValue(stateList.getValue("ID",i));
            }
        }
        //����ַ����������ֶ��б���ͬĬ������Ϊ��ȫ��
        if(state.length()!=list_num){
            state = "NNNNNNNNNN";
        }
        parm.setData("STATE_LIST",state);
        //-----------��������ֵ����-----------------
        //�жϡ�REGION_CODE���ؼ��Ƿ���ֻ�����ԣ���������޸ġ��������½���
        TParm result = new TParm();
        if(((TTextField)this.getComponent("REGION_CODE")).isEditable()){
            result = SYSNewRegionTool.getInstance().onInsert(parm);
            this.messageBox("�½��ɹ���");
            this.onClear();
            this.getTable("TABLE").onQuery(); //��ˢ��
        }
        else{
            result = SYSNewRegionTool.getInstance().onUpdate(parm);
            this.messageBox("�޸ĳɹ���");
            this.getTable("TABLE").onQuery(); //��ˢ��
            this.getTable("TABLE").setSelectedRow(selectedRowIndex);
        }
        if(result.getErrCode()<0){
            //this.messageBox("����",result.getErrText(),-1);
            return;
        }

    }
    /**
     * ��ѯ�¼�
     */
    public void onQuery(){
        TParm parm  = new TParm();
        parm.setDataN("REGION_CODE",this.getValue("REGION_CODE"));
        parm.setDataN("NHI_NO",this.getValue("NHI_NO"));
        parm.setDataN("HOSP_CLASS",this.getValue("HOSP_CLASS"));//�б��
        parm.setDataN("REGION_CHN_DESC",this.getValue("REGION_CHN_DESC"));
        parm.setDataN("REGION_CHN_ABN",this.getValue("REGION_CHN_ABN"));
        parm.setDataN("PY1",this.getValue("PY1"));
        parm.setDataN("REGION_ENG_DESC",this.getValue("REGION_ENG_DESC"));
        parm.setDataN("REGION_ENG_ABN",this.getValue("REGION_ENG_ABN"));
        parm.setDataN("DESCRIPTION",this.getValue("DESCRIPTION"));
        parm.setDataN("PY2",this.getValue("PY2"));
        parm.setDataN("SUPERINTENDENT",this.getValue("SUPERINTENDENT"));
        parm.setDataN("NHIMAIN_NAME",this.getValue("NHIMAIN_NAME"));
        parm.setDataN("REGION_TEL",this.getValue("REGION_TEL"));
        parm.setDataN("REGION_FAX",this.getValue("REGION_FAX"));
        parm.setDataN("REGION_ADDR",this.getValue("REGION_ADDR"));
        parm.setDataN("E_MAIL",this.getValue("E_MAIL"));
        parm.setDataN("IP_RANGE_START",this.getValue("IP_RANGE_START"));
        parm.setDataN("IP_RANGE_END",this.getValue("IP_RANGE_END"));
        parm.setDataN("AP_IP_ADDR",this.getValue("AP_IP_ADDR"));
        TParm result = SYSNewRegionTool.getInstance().onQuery(parm);
        if(result.getErrCode()<0){
            //this.messageBox(result.getErrText());
            return;
        }
        this.getTable("TABLE").setParmValue(result);
        selectedRowIndex = -1;
    }

    /**
     * ɾ���¼�
     */
    public void onDelete(){
        //ȷ��ɾ��
        if (this.messageBox("ѯ��", "ȷ��ɾ��?", 0) == 1) {
            return;
        }
        //���ɾ����Ϣ����Ϊ��
        if(this.getValue("REGION_CODE").equals("")||this.getValue("REGION_CODE").equals(null)){
            this.messageBox("��ѡ��Ҫɾ�����");
            return;
        }
        TParm parm = new TParm();
        parm.setData("REGION_CODE",this.getValue("REGION_CODE"));
        //���ϵͳ�Ƿ�����
        TParm re = SYSNewRegionTool.getInstance().onQuery(parm);
        if(re.getValue("ACTIVE_FLG",0).equals("Y")){
            this.messageBox("ϵͳ�����ò���ɾ����");
            return;
        }
        TParm result = SYSNewRegionTool.getInstance().onDelete(parm);
        if(result.getErrCode()<0){
            //this.messageBox(result.getErrText());
            return;
        }
        this.messageBox("ɾ���ɹ���");
        this.onClear();
        this.getTable("TABLE").onQuery();
    }
    /**
     * ����¼�
     */
    public void onClear(){
        this.clearValue("REGION_CODE;NHI_NO;MAIN_FLG;HOSP_CLASS;REGION_CHN_DESC;REGION_CHN_ABN;SEQ;PY1;PY2;REGION_ENG_DESC;REGION_ENG_ABN;DESCRIPTION");
        this.clearValue("SUPERINTENDENT;NHIMAIN_NAME;REGION_TEL;REGION_FAX;REGION_ADDR;E_MAIL;ACTIVE_DATE;ACTIVE_FLG;IP_RANGE_START;IP_RANGE_END;AP_IP_ADDR;EMR;EKT;LDAP;INS;ONLINE;ODO;REASONABLEMED;REG;HANDINMED;CHARGE;TOP_BEDFEE");
        ((TTextField)this.getComponent("REGION_CODE")).setEditable(true);//���á�������롱Ϊ�ɱ༭״̬
        selectedRowIndex = -1;
        this.getTable("TABLE").clearSelection();//���TABLEѡ��״̬
    }
//    public static void main(String[] args) {
//        JavaHisDebug.runFrame("sys\\SYSNewRegion.x");
//        System.out.println(SYSNewRegionTool.getInstance().isEKT("2"));
//        System.out.println(SYSNewRegionTool.getInstance().isINS("2"));
//        System.out.println(SYSNewRegionTool.getInstance().isODO("2"));
//        System.out.println(SYSNewRegionTool.getInstance().isREG("2"));
//        System.out.println(SYSNewRegionTool.getInstance().isCHARGE("2"));
//    }
}

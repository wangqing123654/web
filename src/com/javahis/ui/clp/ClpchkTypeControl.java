package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextFormat;
import jdo.clp.ClpchkTypeTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TComboBox;
import jdo.sys.Operator;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title:�������ֵ� </p>
 *
 * <p>Description:�������ֵ䵥�� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110429
 * @version 1.0
 */
public class ClpchkTypeControl extends TControl{
    public ClpchkTypeControl() {
    }
    //��������룬���ƴ��������˵����Ӣ��˵�������,ע��,��ע
    TTextField CHKTYPE_CODE,PY1,CHKTYPE_CHN_DESC,CHKTYPE_ENG_DESC,SEQ,PY2,DESCRIPTION;
    //ϸ����
    TTextFormat ORDER_CAT1;
    //���
    TTable TABLE;
    //����
    TComboBox REGION_CODE;
    /**
     * ��ʼ��
     */
    public void onInit(){
       super.onInit();
       onPage();
       //======== Ȩ�����
       REGION_CODE.setValue(Operator.getRegion());
       //֮��Ҫʹ������.8��û�к�̨�����ִ���
//       TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
//       cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
//               getValueString("REGION_CODE")));
       onQuery();
    }
    /**
     * ��ʼ��ҳ��
     */
    public void onPage(){
        ((TMenuItem)this.getComponent("delete")).setEnabled(false);
        //���������
        CHKTYPE_CODE = (TTextField)this.getComponent("CHKTYPE_CODE");
        //���ƴ��
        PY1 = (TTextField) getComponent("PY1");
        //����˵��
        CHKTYPE_CHN_DESC = (TTextField) getComponent("CHKTYPE_CHN_DESC");
        //Ӣ��˵��
        CHKTYPE_ENG_DESC= (TTextField) getComponent("CHKTYPE_ENG_DESC");
        //���
        SEQ = (TTextField) getComponent("SEQ");
        //ע��
        PY2 = (TTextField) getComponent("PY2");
        //��ע
        DESCRIPTION = (TTextField) getComponent("DESCRIPTION");
        //ϸ����
        ORDER_CAT1 = (TTextFormat) getComponent("ORDER_CAT1");
        //����
        REGION_CODE = (TComboBox) getComponent("REGION_CODE");
        //���
        TABLE = (TTable) getComponent("TABLE");

    }
    /**
     * ��ѯ����
     */
    public void onQuery() {
        // ��ѯ����
        TParm result = new TParm(TJDODBTool.getInstance().select(getSQL()));
        callFunction("UI|TABLE|setParmValue",result);
    }
    /**
     * ���淽��
     */
    public void onSave(){
        //�����������벻����Ϊ��ֵ
        if (null == this.getValue("REGION_CODE") || this.getValueString("REGION_CODE").equals("")) {
            this.messageBox("����������");
            return;
        }
        if (null ==this.getValue("CHKTYPE_CODE")  || this.getValueString("CHKTYPE_CODE").equals("")) {
            this.messageBox("��������������");
            return;
        }
        if (null == this.getValue("ORDER_CAT1") || this.getValueString("ORDER_CAT1").equals("")) {
            this.messageBox("������ϸ����");
            return;
        }

        TParm parm = new TParm();
        //���������
        parm.setData("CHKTYPE_CODE", CHKTYPE_CODE.getValue());
        //ϸ����
        parm.setData("ORDER_CAT1", ORDER_CAT1.getValue());
        //����
        parm.setData("REGION_CODE", REGION_CODE.getValue());
        //���
        parm.setData("SEQ", SEQ.getValue());
        //ƴ��
        parm.setData("PY1", PY1.getValue());
        //����˵��
        parm.setData("CHKTYPE_CHN_DESC", CHKTYPE_CHN_DESC.getValue());
        //Ӣ��˵��
        parm.setData("CHKTYPE_ENG_DESC", CHKTYPE_ENG_DESC.getValue());
        //ע��
        parm.setData("PY2", PY2.getValue());
        //��ע
        parm.setData("DESCRIPTION", DESCRIPTION.getValue());
        //�û��޸�
        parm.setData("OPT_USER", Operator.getRegion());
        //��ǰʱ��
        parm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
        //IP
        parm.setData("OPT_TERM", Operator.getIP());

       TParm result=ClpchkTypeTool.getInstance().selectIsExist(parm);
       if (null != result&&result.getCount("REGION_CODE")>0)
           updateRow(parm);
       else
           InsertRow(parm);
       onClear();
       onQuery();
    }
    /**
     *  ��ӷ���
     */
    public void InsertRow(TParm parm) {
        TParm result = ClpchkTypeTool.getInstance().saveClpchkType(parm);
        if (null != result) {
            this.messageBox("��ӳɹ�");
        } else
            this.messageBox("���ʧ��");
    }
    /**
     * �޸ķ���
     * @param parm TParm
     */
    public void updateRow(TParm parm) {

        TParm result = ClpchkTypeTool.getInstance().updateClpchkType(parm);
        if (null != result) {
            this.messageBox("�޸ĳɹ�");
        } else
            this.messageBox("�޸�ʧ��");

    }
    /**
     * ��ѯsql���
     * @return String
     */
    public String getSQL() {
        String sql = "SELECT B.REGION_CHN_DESC,A.REGION_CODE,A.CHKTYPE_CODE,A.ORDER_CAT1,A.CHKTYPE_CHN_DESC, " +
                     "A.PY1,A.SEQ,A.CHKTYPE_ENG_DESC,A.DESCRIPTION,A.PY2 FROM CLP_CHKTYPE A,SYS_REGION B WHERE A.REGION_CODE=B.REGION_CODE(+) ";
        //���������
        String chkTypeCode = "";
        //����
        String regionCode = "";
        //ϸ����
        String orderCat = "";
        //���������
        if (null != CHKTYPE_CODE.getValue() && !"".equals(CHKTYPE_CODE.getValue()))
            chkTypeCode = " AND A.CHKTYPE_CODE LIKE '%" +
                          CHKTYPE_CODE.getValue().trim() + "%'";
        //ϸ����
        if (null != ORDER_CAT1.getValue() &&
            !"".equals(ORDER_CAT1.getValue()))
            orderCat = " AND A.ORDER_CAT1= '" + ORDER_CAT1.getValue() + "'";
        //����
        if (null != REGION_CODE.getValue() &&
            !"".equals(REGION_CODE.getValue()))
            regionCode = " AND A.REGION_CODE ='" + REGION_CODE.getValue() + "'";
        sql += chkTypeCode + orderCat + regionCode;
        return sql;
    }

    /**
     * ��շ���
     */
    public void onClear(){

       // String clearObject="CHKTYPE_CODE,CHKTYPE_PYCODE,CHKTYPE_DESC,SEQ";
        this.clearValue("SEQ;PY1;CHKTYPE_CODE;ORDER_CAT1;CHKTYPE_CHN_DESC;PY2;CHKTYPE_ENG_DESC;DESCRIPTION;REGION_CODE");
        // clearValue(clearObject);
        //�������
        //���������
        CHKTYPE_CODE.setEnabled(true);
        //ϸ����
        ORDER_CAT1.setEnabled(true);
        //����
        REGION_CODE.setValue(Operator.getRegion());
        //ɾ����ť
        ((TMenuItem)this.getComponent("delete")).setEnabled(false);
        //��ձ��
        TABLE.setParmValue(new TParm());
    }
    /**
     * �������һ������
     */
    public void onTableClicked() {
        //����޸ķ���������������
        //���������
        CHKTYPE_CODE.setEnabled(false);
        //ϸ����
        ORDER_CAT1.setEnabled(false);
        //ɾ����ť����ѡ��
        ((TMenuItem)this.getComponent("delete")).setEnabled(true);
        //��ǰ��
        int row = TABLE.getSelectedRow();
        //��ñ����������ֵ
        TParm parm = TABLE.getParmValue();
        setValueForParm(
           "PY1;CHKTYPE_CODE;ORDER_CAT1;CHKTYPE_CHN_DESC;PY2;CHKTYPE_ENG_DESC;DESCRIPTION;REGION_CODE",
           parm, row);
        this.setValue("SEQ",parm.getValue("SEQ",row));
    }
    /**
     * ɾ������
     */
    public void onDelete() {
        TParm parm=new TParm();
        //���������
        if (null != CHKTYPE_CODE.getValue() && !"".equals(CHKTYPE_CODE.getValue()))
           parm.setData("CHKTYPE_CODE",CHKTYPE_CODE.getValue());
        //ϸ����
        if (null != ORDER_CAT1.getValue() &&
            !"".equals(ORDER_CAT1.getValue()))
          parm.setData("ORDER_CAT1",ORDER_CAT1.getValue());
        //����
        if (null != REGION_CODE.getValue() &&
            !"".equals(REGION_CODE.getValue()))
             parm.setData("REGION_CODE",REGION_CODE.getValue());

        TParm result = ClpchkTypeTool.getInstance().deleteClpchkType(parm);
        if (null != result) {
            this.messageBox("ɾ���ɹ�");
        } else
            this.messageBox("ɾ��ʧ��");
        onClear();
        onQuery();
    }
}

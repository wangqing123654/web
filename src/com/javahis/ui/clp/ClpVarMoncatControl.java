package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import com.dongyang.ui.TMenuItem;
import com.dongyang.data.TParm;
import jdo.clp.ClpVarMoncatTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;
import jdo.sys.SYSHzpyTool;
import java.util.Date;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDataStore;
import java.sql.Timestamp;
import jdo.clp.ClpchkTypeTool;
import java.util.ArrayList;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.event.TKeyListener;
import java.awt.Component;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.datawindow.DataStore;
import com.dongyang.ui.TTextFormat;
import com.dongyang.manager.TIOM_AppServer;

/**
 * <p>Title: �����ֵ�</p>
 *
 * <p>Description: �����ֵ�</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 2011-05-02
 * @version 1.0
 */
public class ClpVarMoncatControl extends TControl {
    public ClpVarMoncatControl() {
    }
    //����
    TComboBox REGION_CODE;
    //��һ���е��ı��ؼ�
    //���������롢��š������������˵�����������Ӣ��˵��,�������ƴ��,ע��,��ע
    TTextField MONCAT_CODE, SEQ, MONCAT_CHN_DESC,MONCAT_ENG_DESC, PY1,PY2,DESCRIPTION;
    //���
    TTable TABLE_VARMONCAT,TABLE_VARIANCE;
    //�ڶ�������е�����
    TParm varianceParm;
    //�ж��Ƿ�ɾ���������� true:ɾ����false:��ɾ��
    boolean flg =true;
    //��õ�һ������е���ĵ�Ԫ����
    int selectRow=-1;
     //��õڶ�������е���ĵ�Ԫ����
    int selectRowTwo=-1;
    //�ڶ������е���
    String variance="MONCAT_CODE,VARIANCE_CODE,SEQ,PY2,VARIANCE_CHN_DESC,PY1,VARIANCE_ENG_DESC,CLNCPATH_CHN_DESC,DESCRIPTION,CLNCPATH_CODE,REGION_CODE,OPT_USER,OPT_TERM,OPT_DATE";
    String variances[]=null;
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        onPage();
        variances=variance.split(",");
        //��ӵڶ������tableֵ�ı��¼�
        this.addEventListener("TABLE_VARIANCE->" + TTableEvent.CHANGE_VALUE,
                              "onTABLEVARIANCEChargeValue");
        //�����ٴ�·����Ŀ����Ӽ����¼�
        callFunction("UI|TABLE_VARIANCE|addEventListener",
                     TTableEvent.CREATE_EDIT_COMPONENT, this,
                     "onCreateVARIANCE");
        // ����ӱ�table�����¼�
       callFunction("UI|TABLE_VARIANCE|addEventListener",
                    "TABLE_VARIANCE->" + TTableEvent.CLICKED, this,
                    "onVarianceClicked");
        getRegionShow();
        onQuery();
    }
    public void getRegionShow(){
        //�����ʼֵ
      REGION_CODE.setValue(Operator.getRegion());
      //֮��Ҫʹ������.8��û�к�̨�����ִ���
      //Ȩ�����
//       TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
//       cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
//               getValueString("REGION_CODE")));


    }
    /**
     * �ӱ����¼�
     * �ж��Ƿ�ɾ���ӱ���Ϣ
     * @param row int
     */
    public void onVarianceClicked(int row){
        flg=false;// ֻɾ���ӱ���Ϣ
        ((TMenuItem)this.getComponent("delete")).setEnabled(true);
        selectRowTwo = row;
        //�Ƴ����������ѡ����
        TABLE_VARMONCAT.clearSelection();
        if (row < 0)
            return;
    }
    /**
     * TABLE_VARIANCE�������¼���ֵ�ı��¼�
     * @param obj Object
     * @return boolean
     */
    public boolean onTABLEVARIANCEChargeValue(Object obj){
        //�õ��ڵ�����,�洢��ǰ�ı���к�,�к�,����,��������Ϣ
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        //����ı�Ľڵ����ݺ�ԭ����������ͬ�Ͳ����κ�����
        if (node.getValue().equals(node.getOldValue()))
            return false;
        //�õ�table�ϵ�parmmap������
        String columnName = node.getTable().getParmMap(node.
            getColumn());
        //�õ��ı����
        int row = node.getRow();
        //�õ���ǰ�ı�������
        String value = "" + node.getValue();
        //��������Ƹı���ƴ��1�Զ�����,����ԭ��˵������Ϊ��
        if ("VARIANCE_CHN_DESC".equals(columnName)) {
            if (value.equals("") || value == null) {
                messageBox("ԭ��˵������Ϊ��!");
                return true;
            }
//            //Ĭ�ϱ�������
//            table.setItem(row, "VARIANCE_CHN_DESC", value);
            //������������и�ֵƴ�����
            String py = SYSHzpyTool.getInstance().charToCode(value);
            varianceParm.setData("PY1", row, py);
            varianceParm.setData("VARIANCE_CHN_DESC", row, value);
        }
        //�ӱ�����
        if("VARIANCE_CODE".equals(columnName)){
            if (value.equals("") || value == null) {
                messageBox("ԭ����벻��Ϊ��!");
                return true;
            }
            varianceParm.setData("VARIANCE_CODE", row, value);
        }
        //ע��
        if ("PY2".equals(columnName)) {
            varianceParm.setData("PY2", row, value);
        }
        //ԭ��Ӣ��˵��
        if ("VARIANCE_ENG_DESC".equals(columnName)) {
            varianceParm.setData("VARIANCE_ENG_DESC", row, value);
        }
        //��ע
        if ("DESCRIPTION".equals(columnName)) {
            varianceParm.setData("DESCRIPTION", row, value);
        }
        TABLE_VARIANCE.setParmValue(varianceParm);
        return false;

    }
    /**
     * ��ʼ��ҳ��
     */
    public void onPage() {
        //ɾ����ť��ʼֵ�����Ե��
        ((TMenuItem)this.getComponent("delete")).setEnabled(false);
        //��һ���е�����
        //���
        TABLE_VARMONCAT=(TTable)getComponent("TABLE_VARMONCAT");
        TABLE_VARIANCE=(TTable)getComponent("TABLE_VARIANCE");
        //�ı���
        //����������
        MONCAT_CODE=(TTextField)getComponent("MONCAT_CODE");
        //���
        SEQ=(TTextField)getComponent("SEQ");
        //�����������˵��
        MONCAT_CHN_DESC=(TTextField)getComponent("MONCAT_CHN_DESC");
        //�������ƴ��
        PY1=(TTextField)getComponent("PY1");
        //�������Ӣ��˵��
        MONCAT_ENG_DESC=(TTextField)getComponent("MONCAT_ENG_DESC");
        //ע��
        PY2=(TTextField)getComponent("PY2");
        //��ע
        DESCRIPTION=(TTextField)getComponent("DESCRIPTION");
        //����
        REGION_CODE=(TComboBox)getComponent("REGION_CODE");
        //�ڶ�������е�����
        varianceParm=new TParm();
    }
    /**
     * �ڶ��������Ӽ�����ʾ�����ٴ�·����Ŀ�е�����
     * @param com Component
     * @param row int
     * @param column int
     */
    public void onCreateVARIANCE(Component com, int row, int column) {

        if (row < 0) {
            return;
        }
        if (column != 7) {
            return;
        }
        selectRow=row;//��һ�����ǰ��
        TTextField textFilter = (TTextField) com;
        //��ʼ��
        textFilter.onInit();
        //�򿪽���
        textFilter.setPopupMenuParameter("BSCINFO",getConfigParm().newConfig("%ROOT%\\config\\sys\\ClpBscInfoPopupUI.x"));
        //������ܷ���ֵ����
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");

    }
    /**
     * ���ܷ���ֵ����
     * @param tag String
     * @param obj Object
     */
    public void popReturn(String tag, Object obj) {
        TABLE_VARIANCE.acceptText();
        // �ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
        if (obj == null && ! (obj instanceof TParm)) {
            return;
        }
        // ����ת����TParm
        TParm result = (TParm) obj;

        //����ǰ�и�ֵCLNCPATH_CHN_DESC
        TABLE_VARIANCE.setValueAt(result.getValue(
                "CLNCPATH_CHN_DESC"), selectRow, 7);
        varianceParm.setData("CLNCPATH_CODE", selectRow,
                             result.getValue("CLNCPATH_CODE"));
        varianceParm.setData("CLNCPATH_CHN_DESC", selectRow,
                             result.getValue(
                                     "CLNCPATH_CHN_DESC"));
        //��ʾ����
     //  TABLE_VARIANCE.setParmValue(varianceParm);
      }

    /**
     * ��ѯ����
     */
    public void onQuery(){
        String regionCode = "";
        String moncatCode = "";
        //��������
        if (null != REGION_CODE.getValue() && !REGION_CODE.getValue().equals("")){
            regionCode = " AND A.REGION_CODE='"+REGION_CODE.getValue()+"'";
        }
        //����������
        if (null != MONCAT_CODE.getValue() && !MONCAT_CODE.getValue().equals(""))
            moncatCode = " AND A.MONCAT_CODE LIKE '%" + MONCAT_CODE.getValue().trim() +
                         "%'";
        //������sql���
        String sql = "SELECT A.REGION_CODE ,A.MONCAT_CODE,A.PY1 " +
                     ",A.MONCAT_CHN_DESC, A.SEQ ,A.PY2,A.DESCRIPTION,A.MONCAT_ENG_DESC,B.REGION_CHN_DESC FROM  CLP_VARMONCAT A ,SYS_REGION B WHERE "
                     + "A.REGION_CODE=B.REGION_CODE(+)" + regionCode +
                     moncatCode;
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        //����һ�������ֵ
        TABLE_VARMONCAT.setParmValue(result);
    }
    /**
     * ��շ���
     */
    public void onClear(){
        //ɾ����ť
        ((TMenuItem)this.getComponent("delete")).setEnabled(false);
        //��һ����������������������
        MONCAT_CODE.setEnabled(true);
        this.clearValue("MONCAT_CODE;MONCAT_CHN_DESC;PY1;REGION_CODE;SEQ;PY2;MONCAT_ENG_DESC;DESCRIPTION");
        getRegionShow();
        selectRow = -1;
        selectRowTwo = -1;

        //���
        TABLE_VARMONCAT.setParmValue(new TParm());
        TABLE_VARIANCE.setParmValue(new TParm());
    }
    /**
     * ��Ӻ��޸ķ���
     */
    public boolean onSave(){
        TTable tablevarmoncat=(TTable)this.getComponent("TABLE_VARMONCAT");
        tablevarmoncat.acceptText();
        TTable tablevariance=(TTable)this.getComponent("TABLE_VARIANCE");
        tablevariance.acceptText();
        //�ж��Ƿ������������
        if(MONCAT_CODE.isEnabled()){
            if (null == REGION_CODE.getValue() ||
                "".equals(REGION_CODE.getValue())) {
                this.messageBox("����������");
                return false;
            }
            if (null == MONCAT_CODE.getValue() ||
                "".equals(MONCAT_CODE.getValue())) {
                this.messageBox("���������������");
                return false;
            }
        }
        TParm parm=new TParm();
        //����������
        parm.setData("MONCAT_CODE", MONCAT_CODE.getValue());
        //����
        parm.setData("REGION_CODE", REGION_CODE.getValue());
        //���
        parm.setData("SEQ", SEQ.getValue());
        //�����������˵��
        parm.setData("MONCAT_CHN_DESC", MONCAT_CHN_DESC.getValue());
        //��ע
        parm.setData("DESCRIPTION", DESCRIPTION.getValue());
        //�������ƴ��
        parm.setData("PY1", PY1.getValue());
        //ע��
        parm.setData("PY2", PY2.getValue());
        //�������Ӣ��˵��
        parm.setData("MONCAT_ENG_DESC", MONCAT_ENG_DESC.getValue());
        //����ʱ��
        Timestamp date = StringTool.getTimestamp(new Date());
        parm.setData("OPT_USER", Operator.getID()); //������Ա
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP()); //�����ն�
        //��ѯ�������Ƿ���ڴ�������
        TParm result =ClpVarMoncatTool.getInstance().selectIsExist(parm);
        //������ִ����ӷ���
        if(null==result||result.getCount("REGION_CODE")<=0){
            InsertRow(parm);
        }else
            updateRow(parm);
        //�ڶ���������
        for (int i = 0; i < varianceParm.getCount("MONCAT_CODE"); i++) {
            varianceParm.setData("OPT_USER", i, Operator.getID());
            varianceParm.setData("OPT_TERM", i, Operator.getIP());
            varianceParm.setData("OPT_DATE", i, date);
        }
        //ʵ�ֶ���������޸�
        TParm resultVariance = TIOM_AppServer.executeAction("action.clp.ClpVarMoncatAction",
             "saveVariance", varianceParm);
         if (resultVariance.getErrCode() < 0) {
             err(resultVariance.getErrName() + " " + resultVariance.getErrText());
             this.messageBox("E0001");
             return false;
         }
         else
             this.messageBox("P0001");
       this.onClear();
       this.onQuery();
       return true;
    }
    /**
    *  ��ӷ����������
    */
   public void InsertRow(TParm parm) {
       TParm result =ClpVarMoncatTool.getInstance().saveClpVarMoncat(parm);
       if (null != result) {
           this.messageBox("�����ֵ���������ӳɹ�");
           //table�ϼ���������������ʾ
           callFunction("UI|TABLE_VARMONCAT|addRow", parm,
                        "MONCAT_CODE;SEQ;PY2;MONCAT_CHN_DESC;PY1;MONCAT_ENG_DESC;DESCRIPTION;REGION_CODE");

       } else
           this.messageBox("�����ֵ����������ʧ��");

   }
   /**
    * �޸ķ����������
    * @param parm TParm
    */
   public void updateRow(TParm parm) {
       //��ý���
      // int row = (Integer) callFunction("UI|TABLE_VARIANCE|getSelectedRow");
       if (!flg)
           this.messageBox("�����ֵ��������޸���");
       else {
           TParm result = ClpVarMoncatTool.getInstance().updateClpVarMoncat(
                   parm);
           if (null != result) {
               this.messageBox("�����ֵ��������޸ĳɹ�");
           } else {
               this.messageBox("�����ֵ��������޸�ʧ��");
           }
       }
   }
    /**
     * ɾ������
     */
    public void onDelete() {
        TParm parm = new TParm();
        //�������
        String regionCode = this.getValueString("REGION_CODE");
        //��ñ���������
        String moncatCode = this.getValueString("MONCAT_CODE");
        if (null != regionCode && !"".equals(regionCode))
            parm.setData("REGION_CODE", regionCode);
        if (null != moncatCode && !"".equals(moncatCode))
            parm.setData("MONCAT_CODE", moncatCode);
        //�ӱ�����ԭ�����
        String varianceCode = varianceParm.getValue("VARIANCE_CODE",selectRowTwo);
        String delMoncatSql = ""; //����
        String delVatianceSql = ""; //�ӱ�
        ArrayList list = new ArrayList();
        //ɾ������
        //flg=true ����ɾ��������Ϣ
        if (flg) {
            if (this.messageBox("ѯ��", "�Ƿ�ɾ�������ֵ��������е�����", 2) == 0) {
                //ɾ�������ֵ�
                delMoncatSql = "DELETE FROM CLP_VARMONCAT WHERE MONCAT_CODE='" +
                               moncatCode + "' AND REGION_CODE='" + regionCode +
                               "'";
                //ɾ���ӱ��ֵ�
                delVatianceSql = "DELETE FROM CLP_VARIANCE WHERE MONCAT_CODE='" +
                                 moncatCode + "'";
                list.add(delMoncatSql);
                list.add(delVatianceSql);
                String[] allSql = (String[]) list.toArray(new String[] {});
                TParm delMoncatParm = new TParm(TJDODBTool.getInstance().update(
                        allSql));
                if (delMoncatParm.getErrCode() < 0) {
                    err(delMoncatParm.getErrName() + " " +
                        delMoncatParm.getErrText());
                    return;
                }
            }
        } else {
             moncatCode = varianceParm.getValue( "MONCAT_CODE",selectRowTwo);
            //ɾ���ӱ��ֵ�
            delVatianceSql =
                    "DELETE FROM CLP_VARIANCE WHERE MONCAT_CODE='" +
                    moncatCode + "' AND VARIANCE_CODE='" + varianceCode +
                    "'";
            TParm delVarianceParm = new TParm(TJDODBTool.getInstance().
                                              update(
                    delVatianceSql));
            if (delVarianceParm.getErrCode() < 0) {
                err(delVarianceParm.getErrName() + " " +
                    delVarianceParm.getErrText());
                return;
            }
        }
        this.messageBox("ɾ���ɹ�");
        this.onClear();
        this.onQuery();
    }
    /**
     * ����table�����¼�
     * @param row int
     */
    public void onTableVarMoncatClicked() {
        //ɾ����ť��ʼֵ�����Ե��
        ((TMenuItem)this.getComponent("delete")).setEnabled(true);
        //��õ�ǰ��
        int row = TABLE_VARMONCAT.getSelectedRow();
        flg = true;//������������ɾ������
        if (row < 0)
            return;
        //�޸Ĳ���
        MONCAT_CODE.setEnabled(false);
        REGION_CODE.setEnabled(false);
        TParm data = (TParm) callFunction("UI|TABLE_VARMONCAT|getParmValue");
        setValueForParm(
                "MONCAT_CODE;MONCAT_CHN_DESC;PY1;REGION_CODE;PY2;MONCAT_ENG_DESC;DESCRIPTION",
                data, row);
        this.setValue("SEQ", data.getValue("SEQ", row));
        //�ڶ��������ʾ����
        String sql = "SELECT 'N' AS FLG, A.MONCAT_CODE,A.VARIANCE_CODE,A.SEQ,A.PY2,A.VARIANCE_CHN_DESC,A.PY1,A.VARIANCE_ENG_DESC,B.CLNCPATH_CHN_DESC,"+
                     "A.DESCRIPTION,A.CLNCPATH_CODE,A.REGION_CODE,A.OPT_USER,A.OPT_TERM,A.OPT_DATE FROM CLP_VARIANCE A,CLP_BSCINFO B WHERE A.CLNCPATH_CODE =B.CLNCPATH_CODE(+)";
        if (!getValue("MONCAT_CODE").toString().equals(""))
            sql += " AND A.MONCAT_CODE='" + getValue("MONCAT_CODE").toString() +
                    "'";
        if (!getValueString("REGION_CODE").equals(""))
            sql += " AND A.REGION_CODE='" + getValue("REGION_CODE").toString() +
                    "'";
        //�ڶ�������в�ѯֵ
        varianceParm=new TParm(TJDODBTool.getInstance().select(sql));
        TABLE_VARIANCE.setParmValue(varianceParm);
        //��ñ���е�������
        int roomAllRow = TABLE_VARIANCE.getParmValue().getCount("MONCAT_CODE");
        //�ж��Ƿ���Ҫ������� varianceeCode��ֵ���һ��
        String varianceeCode = TABLE_VARIANCE.getParmValue().getValue("VARIANCE_CODE",
            roomAllRow - 1);
        //�������ֵ����û�����ݶ������һ������
        if ( (varianceeCode != null && varianceeCode.length() != 0) ||
            roomAllRow == -1) {
            this.onNew();
        }
    }

    /**
     * ���������ڶ�����������һ��������
     */
    public void onNew() {
        //���������
        int seq=getMaxSeq(TABLE_VARIANCE.getParmValue(),"SEQ");
        // ��������ݵ�˳����
        if (getValue("MONCAT_CODE").toString().length() > 0) {
            // ��õ�ǰ�ı������
          // TParm oldParm= TABLE_VARIANCE.getParmValue();
            // ��ǰѡ�е���
            varianceParm.addData("FLG", "Y");
            for (int i = 0; i < variances.length; i++) {
                // ����������
                if (variances[i].equals("MONCAT_CODE"))
                    varianceParm.addData(variances[i], getValueString("MONCAT_CODE"));
                //���
                else if(variances[i].equals("SEQ"))
                    varianceParm.addData(variances[i], seq);
                //����
                else if(variances[i].equals("REGION_CODE"))
                    varianceParm.addData(variances[i], REGION_CODE.getValue());
                else
                     varianceParm.addData(variances[i], "");
            }
           // varianceParm = oldParm;//�ڶ�����������е�����
            //��ʾ����
            TABLE_VARIANCE.setParmValue(varianceParm);
        } else {
            this.messageBox("�޹���");
            return;
        }
    }
    /**
     * �õ����ı��
     *
     * @param dataStore
     *            TDataStore
    * @param columnName
    *            String
    * @return String
    */
   public int getMaxSeq(TParm parm, String columnName) {
       if (parm == null)
           return 0;
       // ����������
       int count = parm.getCount();
       // ��������
       int s = 0;
       for (int i = 0; i < count; i++) {
           int value = parm.getInt(columnName,i);
           // �������ֵ
           if (s < value) {
               s = value;
               continue;
           }
       }
       // ���ż�1
       s++;
       return s;
   }

}

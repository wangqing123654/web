package com.javahis.ui.med;

import com.dongyang.control.*;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import jdo.opb.OPB;
import java.sql.Timestamp;
import jdo.reg.PatAdmTool;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MedChooseVisitAdmControl
    extends TControl {
    int selectrow = -1;
    OPB opb=new OPB();
    private String admType="O";
    String regionCode="";
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //�õ�ǰ̨���������ݲ���ʾ�ڽ�����
        TParm recptype = (TParm) getParameter();
        if (!recptype.getData("count").equals("0")) {
            setValueForParm("MR_NO;PAT_NAME;SEX_CODE;AGE", recptype.getParm("PARM"),
                            -1);
            callFunction("UI|TABLE|setParmValue", recptype.getParm("RESULT"));
        }
        if (recptype.getData("count").equals("0")) {
            setValueForParm("MR_NO;PAT_NAME;SEX_CODE;AGE", recptype, -1);
        }
        //Ԥ�����ʱ���

        this.callFunction("UI|STARTTIME|setValue",recptype.getTimestamp("START_DATE"));
        this.callFunction("UI|ENDTIME|setValue",recptype.getTimestamp("END_DATE"));
        //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
        this.admType = recptype.getValue("ADM_TYPE");
        regionCode = recptype.getValue("REGION_CODE");
        //Ĭ��Table����ʾ����Һż�¼
        onQuery();
    }

    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        //���������¼�
        this.callFunction("UI|TABLE|acceptText");
//   TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        selectrow = row;
    }

    public void onTableDoubleClicked(int row) {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue(data.getRow(selectrow));
        this.callFunction("UI|onClose");
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        if("O".equals(this.admType)||"E".equals(this.admType)){
            parm = PatAdmTool.getInstance().selDateByMrNoAdm(getValueString("MR_NO"),
            (Timestamp) getValue("STARTTIME"), (Timestamp) getValue("ENDTIME"),this.admType,regionCode);
        }
        if("I".equals(this.admType)){
            String sql = "SELECT CASE_NO,IN_DATE AS ADM_DATE,'' AS SESSION_CODE,DEPT_CODE,VS_DR_CODE AS DR_CODE "+
                "FROM ADM_INP "+
                "  WHERE MR_NO='"+this.getValueString("MR_NO")+"'"+
                " AND IN_DATE BETWEEN TO_DATE('"+StringTool.getString((Timestamp) getValue("STARTTIME"),"yyyyMMdd")+"','YYYYMMDD') AND TO_DATE('"+StringTool.getString((Timestamp) getValue("ENDTIME"),"yyyyMMdd")+"','YYYYMMDD')"+
                " ORDER BY CASE_NO";
            // System.out.println("SQL==="+sql);
            parm = new TParm(getDBTool().select(sql));
        }
        if (parm.getCount() < 0)
            return;
        if (parm.getCount() == 0)
            this.messageBox("�޾�����Ϣ!");
        this.callFunction("UI|TABLE|setParmValue", parm);
    }
    /**
     * �������ݿ��������
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     *
     */
    public void onOK() {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue(data.getRow(selectrow));
        this.callFunction("UI|onClose");
    }

}

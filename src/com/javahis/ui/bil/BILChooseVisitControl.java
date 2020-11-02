package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: �����Ų�ѯ����</p>
 *
 * <p>Description: �����Ų�ѯ����</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.12.24
 * @version 1.0
 */
public class BILChooseVisitControl
    extends TControl {
    int selectrow = -1;
    String mrNo;
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        //�õ�ǰ̨���������ݲ���ʾ�ڽ�����
        TParm patInfo = (TParm) getParameter();
        if (!patInfo.getData("count").equals("0")) {
            setValueForParm("MR_NO;PAT_NAME;SEX_CODE;AGE", patInfo.getParm("PARM"),
                            -1);
            callFunction("UI|TABLE|setParmValue", patInfo.getParm("RESULT"));
        }
        if (patInfo.getData("count").equals("0")) {
            setValueForParm("MR_NO;PAT_NAME;SEX_CODE;AGE", patInfo, -1);
        }
        //Ԥ����Ժʱ���
        this.callFunction("UI|STARTTIME|setValue",
                          SystemTool.getInstance().getDate());
        this.callFunction("UI|ENDTIME|setValue",
                          SystemTool.getInstance().getDate());
        //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
        onQuery();
    }

    /**
     * ���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        //���������¼�
        this.callFunction("UI|TABLE|acceptText");
        selectrow = row;
    }

    public void onTableDoubleClicked(int row) {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
//        this.setReturnValue( (String) data.getData("IPD_NO", row));
        this.setReturnValue(data.getRow(selectrow));
        this.callFunction("UI|onClose");
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
    	//===ZHANGP 20120815 START
//        TParm inParm = new TParm();
//        inParm.setData("MR_NO", mrNo);
//        TParm parm = ADMInpTool.getInstance().queryCaseNo(inParm);
		String date_s = getValueString("STARTTIME");
		String date_e = getValueString("ENDTIME");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("��������Ҫ��ѯ��ʱ�䷶Χ");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		
//		System.out.println("----date_s11-----"+date_s);
		date_e=date_e.substring(0, 8)+"235959";		
		//System.out.println("----date_e22-----"+date_e);
		
    	String sql = 
    		" SELECT CASE_NO,IPD_NO,MR_NO,DEPT_CODE,STATION_CODE," +
    		" BED_NO,CTZ1_CODE,CTZ2_CODE,CTZ3_CODE,ADM_SOURCE,IN_DATE,OPD_DR_CODE," +
    		" VS_DR_CODE,VS_DR_CODE AS ATTEND_DR_CODE,DIRECTOR_DR_CODE,ADM_DAYS,RED_SIGN,YELLOW_SIGN,TOTAL_BILPAY ," +
    		" CUR_AMT,NEW_BORN_FLG,IN_COUNT,M_CASE_NO,SERVICE_LEVEL,AGN_CODE,AGN_INTENTION  " +
    		" FROM ADM_INP " +
    		" WHERE CANCEL_FLG <> 'Y' " +
    		" AND MR_NO = '" + getValue("MR_NO") + "'" +
    		" AND IN_DATE BETWEEN TO_DATE('" + date_s + "','YYYYMMDDHH24MISS') AND " +
    		" TO_DATE('" + date_e + "','YYYYMMDDHH24MISS')" +
    		" ORDER BY IN_DATE";
//    	System.out.println("�������ҽ��sql is����"+sql);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm.getCount() < 0)
            return;
        if (parm.getCount() == 0)
            this.messageBox("����Ժ��Ϣ!");
        this.callFunction("UI|TABLE|setParmValue", parm);
    }

    /**
     * ȷ�ϴ����¼�
     */
    public void onOK() {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
//        this.setReturnValue( (String) data.getData("IPD_NO", selectrow));
        this.setReturnValue(data.getRow(selectrow));

        this.callFunction("UI|onClose");
    }
}

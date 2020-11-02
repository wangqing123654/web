package com.javahis.ui.opb;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

import jdo.sys.SystemTool;
import jdo.reg.PatAdmTool;
import jdo.opb.OPB;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.sys.Operator;
/**
 * <p>Title:�����ѡ�� </p>
 *
 * <p>Description:�����ѡ�� </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author fudw
 * @version 1.0
 */
public class OPBChooseVisitControl
    extends TControl {
    int selectrow = -1;
    OPB opb=new OPB();
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
        //yuml   20141022   strat
		this.setValue("STARTTIME",StringTool.getString(queryFirstDayOfLastMonth(StringTool.getString(
				SystemTool.getInstance().getDate(), "yyyyMMdd")), "yyyy/MM/dd")+" 00:00:00");
//        this.setValue("STARTTIME", SystemTool.getInstance().getDate().toString().substring(0,10).replace('-', '/')+" 00:00:00");
        //yuml   20141022   end
        this.setValue("ENDTIME", SystemTool.getInstance().getDate().toString().substring(0,10).replace('-', '/')+" 23:59:59");
        //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1�ĵ��������¼�
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
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
        this.setReturnValue( (String) data.getData("CASE_NO", row));
        this.callFunction("UI|onClose");
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        //==========pangben modify 20110421 start ��Ӳ�����ΪselDateByMrNo��������������������
        String regionCode = Operator.getRegion();
        TParm parm = PatAdmTool.getInstance().selDateByMrNo(getValueString("MR_NO"),
            (Timestamp) getValue("STARTTIME"), (Timestamp) getValue("ENDTIME"),regionCode);
        //==========pangben modify 20110421 start
        if (parm.getCount() < 0)
            return;
//        if (parm.getCount() == 0)
//            this.messageBox("�޹Һ���Ϣ!");
        opbArrearage(parm);
        this.callFunction("UI|TABLE|setParmValue", parm);
    }
    
    private void opbArrearage(TParm parm){
    	String startDate = getValueString("STARTTIME").substring(0, 19).replace("-", "").replace(":", "");
    	String endDate = getValueString("ENDTIME").substring(0, 19).replace("-", "").replace(":", "");
    	String mrNo = getValueString("MR_NO");
    	
    	String sql =
    		" SELECT DISTINCT A.CASE_NO" +
    		" FROM OPD_ORDER A, REG_PATADM B" +
    		" WHERE     A.MR_NO = '" + mrNo + "'" +
    		" AND A.BILL_FLG = 'N'" +
    		" AND A.EXEC_FLG = 'Y'" +
    		" AND A.MEM_PACKAGE_ID IS NULL" +
    		" AND A.CASE_NO = B.CASE_NO" +
    		" AND B.ADM_DATE BETWEEN TO_DATE ('" + startDate + "', 'YYYYMMDDHH24MISS') " +
    		" AND TO_DATE ('" + endDate + "', 'YYYYMMDDHH24MISS')";
    	TParm p = new TParm(TJDODBTool.getInstance().select(sql));
    	parm.addData("SYSTEM", "COLUMNS", "OPB_ARREAGRAGE");
    	for (int i = 0; i < parm.getCount(); i++) {
    		parm.addData("OPB_ARREAGRAGE", "N");
			for (int j = 0; j < p.getCount(); j++) {
				if(parm.getValue("CASE_NO", i).equals(p.getValue("CASE_NO", j))){
					parm.setData("OPB_ARREAGRAGE", i, "Y");
				}
			}
		}
    }

    /**
     *
     */
    public void onOK() {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        if(selectrow>-1){
        	this.setReturnValue( (String) data.getData("CASE_NO", selectrow));
        }else{
        	this.messageBox("��ѡ��һ������");
        	return;
        }
        this.callFunction("UI|onClose");
    }
    //$-----------add caoyong 2013013
    public void onCan(){
    	this.callFunction("UI|onClose");
    	return ;
    }
    //$-----------end ----- add caoyong 2013013
    
    
	/**
	 * �õ���3����
	 * 
	 * @param dateStr
	 *            String
	 * @return Timestamp
	 */
	 //yuml   20141022
	public Timestamp queryFirstDayOfLastMonth(String dateStr) {
		DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		try {
			d = defaultFormatter.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -3);
//		cal.set(Calendar.DAY_OF_MONTH, 1);
		return StringTool.getTimestamp(cal.getTime());
	}
}

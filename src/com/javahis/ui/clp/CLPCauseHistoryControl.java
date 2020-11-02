package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: �ٴ�·��������ԭ��ͳ��</p>
 *
 * <p>Description: �ٴ�·��������ԭ��ͳ��</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author pangben 2015-8-15
 * @version 1.0
 */
public class CLPCauseHistoryControl extends TControl{
	private TTable table;
	private SimpleDateFormat formateDate=new SimpleDateFormat("yyyy/MM/dd");
	/**
     * ҳ���ʼ������
     */
    public void onInit() {
        super.onInit();
        initPage();
        onClear();
    }
    public void initPage(){
    	table=(TTable)this.getComponent("TABLE");
    }
    /**
     * 
    * @Title: onClear
    * @Description: TODO(���)
    * @author pangben
    * @throws
     */
    public void onClear(){
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                getDate(), -7);
        setValue("START_DATE", formateDate.format(yesterday)+" 00:00:00");
        setValue("END_DATE", formateDate.format(SystemTool.getInstance().getDate())+" 23:59:59");
    	table.setParmValue(new TParm());
    }
    public void onQuery(){
    	String startDate=SystemTool.getInstance().getDateReplace(this.getValueString("START_DATE"), true).toString();
    	String endDate=SystemTool.getInstance().getDateReplace(this.getValueString("END_DATE"), true).toString();
    	String sql="SELECT A.MR_NO,B.PAT_NAME,TO_CHAR(C.IN_DATE,'yyyy/MM/dd') IN_DATE,D.USER_NAME AS CAUSE_USER," +
    			"TO_CHAR(A.CAUSE_DATE,'yyyy/MM/dd') CAUSE_DATE,CASE WHEN  B.SEX_CODE ='1' THEN '��' ELSE 'Ů' END SEX_CODE,E.CHN_DESC AS CAUSE_NAME " +
    			"FROM CLP_CAUSE_HISTORY A,SYS_PATINFO B,ADM_INP C,SYS_OPERATOR D,SYS_DICTIONARY E WHERE A.CASE_NO=C.CASE_NO AND A.MR_NO=B.MR_NO" +
    			" AND A.CAUSE_USER=D.USER_ID AND A.CAUSE_CODE=E.ID AND E.GROUP_ID='CLP_UNENTER_CAUSE' " +
    			" AND A.CAUSE_DATE BETWEEN TO_DATE("+startDate+",'YYYYMMDDHH24MISS') AND TO_DATE("+endDate+",'YYYYMMDDHH24MISS')";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if (result.getErrCode()<0) {
			this.messageBox("��ѯ���ִ���");
    		return;
		}
    	if (result.getCount()<=0) {
    		this.messageBox("û����Ҫ��ѯ������");
    		table.setParmValue(new TParm());
    		return;
		}
    	table.setParmValue(result);
    }
    /**
     * ���Excel
     */
    public void onExport() {

        //�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "·��������ԭ�򱨱�");
    }
}

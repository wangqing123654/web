package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
/**
 * <p>Title: סԺ����������ϸ��</p>
 *
 * <p>Description: סԺ����������ϸ��</p>
 *
 * <p>Copyright: Copyright (c)bluecore 2016</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author pangben 2016/5/10
 * @version 1.0
 */
public class BILOdiOpeFeeDetailControl  extends TControl {

	private TTable table;
    public void onInit() {
        super.onInit();
        table=(TTable)this.getComponent("TABLE");
           
        String head="������,80;����,60;�Ա�,40;��������,80;��Ժ����,100;��Ժ����,100;��Ժʱ��,80;��Ժʱ��,80;" +
		"����ʱ��,160;�ս�ʱ��,160;�����ܽ��,100;" +
		"ʵ���ܽ��,90;������,80;������,80;������,80;��Ѫ��,80;�����,80;����ʱ��,80;�ײ�����,150;�վݺ���,100;�ս����,100";
        String mapParm="MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;DEPT_CHN_DESC;OUT_DEPT_DESC;" +
		"IN_DATE;DS_DATE;CHARGE_DATE;ACCOUNT_DATE;TOT_AMT;" +
		"AR_AMT;REDUCE_AMT;AMT;212;211;HZAMT;OPT_CHN_DESC;PACKAGE_DESC;RECEIPT_NO;ACCOUNT_SEQ";
        String send="0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,left;8,left;9,left;10,right;11,right;12,right;13,right;" +
		"14,right;15,right;16,right;17,left;18,left;19,left;20,left";
    	table.setHeader(head);
    	table.setParmMap(mapParm);
    	table.setColumnHorizontalAlignmentData(send);
        initPage();
    }
    public void onMrno(){
    	Pat pat = Pat.onQueryByMrNo(PatTool.getInstance().checkMrno(getValueString("MR_NO")));
		if (pat == null) {
			messageBox_("���޴˲�����");
			this.setValue("MR_NO", "");
		}
		this.setValue("MR_NO", pat.getMrNo());
    }
    /**
     * ��ʼ������
     */
    public void initPage() {
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(),-30);
        String startTime = StringTool.getString(yesterday, "yyyy/MM/dd");
        setValue("START_DATE", startTime+" 00:00:00");
        setValue("END_DATE",  StringTool.getString(SystemTool.getInstance().
                getDate(), "yyyy/MM/dd")+" 23:59:59");
//        this.callFunction("UI|TABLE|removeRowAll");
        table.setParmValue(new TParm());
        this.clearValue("MR_NO;RECEIPT_NO;ACCOUNT_SEQ;DEPT_CODE;IN_DEPT_CODE");
    }
    public void onQuery(){
    	String where="";
    	String [] array={"MR_NO","RECEIPT_NO","ACCOUNT_SEQ"};
    	for (int i = 0; i < array.length; i++) {
    		if (this.getValue(array[i]).toString().length()>0) {
        		where+=" AND G."+array[i]+"='"+this.getValue(array[i]).toString()+"'";
    		}
		}
    	array=new String[]{"DEPT_CODE","IN_DEPT_CODE"};
    	for (int i = 0; i < array.length; i++) {
    		if (this.getValue(array[i]).toString().length()>0) {
        		where+=" AND A."+array[i]+"='"+this.getValue(array[i]).toString()+"'";
    		}
		}
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyyMMddHHmmss");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyyMMddHHmmss");
        String sql="SELECT K.MR_NO, K.PAT_NAME, CASE WHEN K.SEX_CODE = '1' THEN '��' WHEN K.SEX_CODE = '2' THEN 'Ů' ELSE 'δ֪' "+
		      " END SEX_CODE, K.BIRTH_DATE,C.DEPT_CHN_DESC,B.DEPT_CHN_DESC AS OUT_DEPT_DESC,  A.IN_DATE, A.DS_DATE,"+
		      " TO_CHAR(G.CHARGE_DATE, 'YYYY/MM/DD HH:MM:SS') CHARGE_DATE,"+
		      " TO_CHAR(G.ACCOUNT_DATE, 'YYYY/MM/DD HH:MM:SS') ACCOUNT_DATE, G.AR_AMT TOT_AMT,"+
		      " G.AR_AMT - CASE WHEN G.REDUCE_AMT IS NULL THEN 0 ELSE G.REDUCE_AMT END AR_AMT,"+
		      " G.REDUCE_AMT, DM.AMT AMT, H.*, HZ.HZAMT, P.OPT_CHN_DESC, L.PACKAGE_DESC,G.ACCOUNT_SEQ "+
		 " FROM ADM_INP A, SYS_DEPT B,SYS_DEPT C,BIL_IBS_RECPM G,(SELECT PACKAGE_CODE, PACKAGE_DESC "+
		         " FROM MEM_PACKAGE WHERE ADM_TYPE = 'I') L, SYS_PATINFO K, (SELECT D.CASE_NO, SUM(D.TOT_AMT) AMT "+
		         " FROM IBS_ORDD D WHERE D.ORDER_CHN_DESC LIKE '%��Ƹ%' GROUP BY D.CASE_NO) DM, "+//�ֳ�����   ��������Ϊ��Ƹ  machao
		      " (SELECT *  FROM (SELECT REXP_CODE, WRT_OFF_AMT, RECEIPT_NO  FROM BIL_IBS_RECPD "+
		          " WHERE RECEIPT_NO IN (SELECT RECEIPT_NO FROM BIL_IBS_RECPD)) PIVOT(SUM(WRT_OFF_AMT) FOR REXP_CODE IN('212','211'))) H,"+
		      " (SELECT D.CASE_NO, SUM(D.TOT_AMT) HZAMT FROM IBS_ORDD D, SYS_FEE S WHERE D.ORDER_CODE = S.ORDER_CODE "+
		          " AND S.CHARGE_HOSP_CODE = 'AF'  GROUP BY D.CASE_NO) HZ, (SELECT A.CASE_NO,"+
		              " LISTAGG(TO_CHAR(OP.OP_DATE,'YYYY/MM/DD'), ',') WITHIN GROUP(ORDER BY A.CASE_NO) OPT_CHN_DESC "+
		         " FROM ADM_INP  A, OPE_OPBOOK OP WHERE A.CASE_NO = OP.CASE_NO AND OP.STATE > 0 GROUP BY A.CASE_NO) P "+
		 " WHERE A.CASE_NO = G.CASE_NO "+
		  " AND A.CASE_NO = DM.CASE_NO(+) "+
		  " AND A.CASE_NO = HZ.CASE_NO(+) "+
		  " AND A.DEPT_CODE = B.DEPT_CODE "+
		  " AND A.LUMPWORK_CODE = L.PACKAGE_CODE(+) "+
		  " AND A.IN_DEPT_CODE = C.DEPT_CODE "+
		  " AND A.CASE_NO = P.CASE_NO(+) "+
		  " AND A.MR_NO = K.MR_NO "+
		  " AND G.RECEIPT_NO = H.RECEIPT_NO "+
		  " AND G.CHARGE_DATE BETWEEN TO_DATE('"+startTime+"', 'YYYYMMDDHH24MISS') AND "+
		      " TO_DATE('"+endTime+"', 'YYYYMMDDHH24MISS') "+where+
		 " ORDER BY B.DEPT_CHN_DESC, G.CHARGE_DATE, PAT_NAME";
        System.out.println("sql:sss:::"+sql);
    	TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
    	if (parm.getErrCode()<0) {
			this.messageBox("��ѯ��������");
			return;
		}
    	String sumValue="TOT_AMT;AR_AMT;REDUCE_AMT;AMT;HZAMT;212;211";
    	String [] arrayValue=sumValue.split(";");
    	
    	if (parm.getCount()<=0) {
			this.messageBox("û����Ҫ��ѯ������");
			table.setParmValue(new TParm());
			return;
		}
    	DecimalFormat df = new DecimalFormat("##########0.00");
    	double[]sumDouble=new double[arrayValue.length];
    	for (int i = 0; i < parm.getCount(); i++) {
			for (int j = 0; j < arrayValue.length; j++) {
				parm.setData(arrayValue[j], i, df.format(parm.getDouble(arrayValue[j],i)));
				sumDouble[j]+=StringTool.round(parm.getDouble(arrayValue[j],i),2);
			}
		}
    	String stringValue="MR_NO;PAT_NAME;SEX_CODE;DEPT_CHN_DESC;OUT_DEPT_DESC;BIRTH_DATE;IN_DATE;DS_DATE;" +
		"CHARGE_DATE;ACCOUNT_DATE;OPT_CHN_DESC;PACKAGE_DESC;RECEIPT_NO;ACCOUNT_SEQ";
    	String [] stringArray=stringValue.split(";");
    	for (int i = 0; i < stringArray.length; i++) {
    		if (stringArray[i].equals("MR_NO")) {
    			parm.addData(stringArray[i], "�ϼƣ�");
			}else{
				parm.addData(stringArray[i], "");
			}
		}
    	for (int i = 0; i < sumDouble.length; i++) {
    		parm.addData(arrayValue[i], df.format(StringTool.round(sumDouble[i],2)) );
		}
    	parm.setCount(parm.getCount("MR_NO"));
    	table.setParmValue(parm);
    }
    public void onClear(){
    	initPage();
    }
    public void onPrint(){
    	 int row = table.getRowCount();
         if (row < 1) {
             this.messageBox_("�Ȳ�ѯ����!");
             return;
         }
         String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                 "START_DATE")), "yyyy/MM/dd HH:mm:ss");
         String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
                 "END_DATE")), "yyyy/MM/dd HH:mm:ss");
         String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                               "yyyy/MM/dd HH:mm:ss");
         TParm parm = new TParm();
       //����
         parm.setData("TITLE", "TEXT","�ż���ʵ������ϸ����");
         parm.setData("PRINT_DATE","TEXT",sysDate);
         parm.setData("DATE", "TEXT", startTime + " �� " + endTime);
 		//parm.setData("E_DATE", eDate);
 		parm.setData("OPT_USER", "TEXT", Operator.getName());
 		parm.setData("printDate", "TEXT", sysDate);
 		parm.setData("T1", table.getParmValue().getData());
 		parm.setData("TOT_AMT", "TEXT", "�ϼ�:  "
 				+ table.getShowParmValue().getValue("TOT_AMT",
 						table.getRowCount() - 1));// ===zhangp 20120824
 		// System.out.println("��Ժ����ҽ�Ʒ�����ϸ��" + printData.getData());
 		this.openPrintWindow(
 				"%ROOT%\\config\\prt\\BIL\\BILOpdPaidDetail.jhw", parm);
    }
    /**
     * ���Excel
     */
    public void onExcel() {

        //�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
       // TTable table = (TTable) callFunction("UI|TABLE|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "סԺ�������ñ���");
    }

}

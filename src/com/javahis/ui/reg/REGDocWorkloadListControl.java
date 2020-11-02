package com.javahis.ui.reg;

import com.dongyang.ui.TTable;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.dongyang.control.TControl;
import java.text.DecimalFormat;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.javahis.util.ExportExcelUtil;
import com.sun.xml.bind.v2.schemagen.xmlschema.List;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import com.dongyang.util.StringTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: ҽ��������ͳ�Ʊ���</p>
 *
 * <p>Description: ҽ��������ͳ�Ʊ���</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.08.28
 * @version 1.0
 */
public class REGDocWorkloadListControl
    extends TControl {
    TParm printData;
    public void onInit() {
        super.onInit();
        callFunction("UI|Table|addEventListener",
                     "Table->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
        initPage();
        //========pangben modify 20110421 start Ȩ�����
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop

    }

    /**
     * ��˫���¼�
     * @param row int
     */
    public void onTableDoubleClicked(int row) {
        if (row < 0)
            return;
        onDetial();
    }

    /**
     * ��ʼ������
     */
    public void initPage() {

        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().
                                                  getDate(), -1);
        setValue("S_DATE", yesterday);
        setValue("E_DATE", SystemTool.getInstance().getDate());
        setValue("REGION_CODE", Operator.getRegion());
        setValue("DEPT_CODE", Operator.getDept());
        setValue("DR_CODE", Operator.getID());
        //�鳤Ȩ��
        if (this.getPopedem("LEADER")) {
            callFunction("UI|DR_CODE|setEnabled", true);
        }
        //ȫԺ
        if (this.getPopedem("ALL")) {
            callFunction("UI|DEPT_CODE|setEnabled", true);
            callFunction("UI|DR_CODE|setEnabled", true);
        }
        this.callFunction("UI|Table|removeRowAll");
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        print();
    }

    /**
     * ���ñ����ӡԤ������
     */
    private void print() {
       // TTable table = (TTable)this.getComponent("Table");
        //int row = table.getRowCount();
        int rowCount = printData.getCount("REGION_CHN_ABN");
        if (rowCount < 1) {
            this.messageBox("�Ȳ�ѯ����!");
            return;
        }
        TParm T1 = new TParm(); //�������
        TTable table = ((TTable)this.getComponent("Table"));
        for (int i = 0; i < rowCount; i++) {
            T1.addRowData(printData, i);
        }
        T1.setCount(rowCount);
        String[] chage = table.getParmMap().split(";");
        for (int i = 0; i < chage.length; i++) {
            T1.addData("SYSTEM", "COLUMNS", chage[i]);
        }
        //===========pangben modify 20110425 start ������е����ݴ�ӡ��������Ҫ�ٴβ�ѯһ��
//        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
//            "S_DATE")), "yyyyMMdd");
//        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
//            "E_DATE")), "yyyyMMdd");
        String sysDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                              "yyyy/MM/dd HH:mm:ss");
   //     TParm printData = this.getPrintDate(startTime, endTime);
    //===========pangben modify 20110425 stop
        String sDate = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyy/MM/dd")+" "+this.getValue("S_TIME");
        String eDate = StringTool.getString(TypeTool.getTimestamp(getValue(
                "E_DATE")), "yyyy/MM/dd")+" "+this.getValue("E_TIME");
        TParm parm = new TParm();
        //========pangben modify 20110413 start
        String region= ((TTable)this.getComponent("Table")).getParmValue().getRow(0).getValue("REGION_CHN_ABN");
        parm.setData("TITLE", "TEXT",
                     ( this.getValue("REGION_CODE")!=null&&!this.getValue("REGION_CODE").equals("")?region:"����ҽԺ") + "ҽ��������ͳ�Ʊ���");
        //========pangben modify 20110328 stop
        parm.setData("S_DATE","TEXT", sDate);
        parm.setData("E_DATE","TEXT", eDate);
        parm.setData("OPT_USER","TEXT", Operator.getName());
        parm.setData("OPT_DATE","TEXT", sysDate);
        parm.setData("docWorkloadListTable", T1.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGDocWorkloadList.jhw",
                             parm);

    }

    /**
     * �����ӡ����
     * @param startTime String
     * @param endTime String
     * @return TParm
     */
    private TParm getPrintDate(String startTime, String endTime) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm selParm = new TParm();
        String admTypeWhere = "";
        if (getValue("ADM_TYPE") != null) {
            if (getValue("ADM_TYPE").toString().length() != 0)
                admTypeWhere = " AND A.ADM_TYPE = '" + getValue("ADM_TYPE") +
                    "'  ";
        }
        String deptCodeWhere = "";
        if (getValue("DEPT_CODE") != null) {
            if (getValue("DEPT_CODE").toString().length() != 0)
                deptCodeWhere = " AND A.REALDEPT_CODE = '" +
                    getValue("DEPT_CODE") +
                    "'  ";
        }

        String drCodeWhere = "";
        if (getValue("DR_CODE") != null) {
            if (getValue("DR_CODE").toString().length() != 0)

                drCodeWhere = " AND A.REALDR_CODE = '" + getValue("DR_CODE") +
                    "'  ";
        }
        //================pangben modify 20110408 start
        String reqion = "";
        if (this.getValueString("REGION_CODE").length() != 0)
            reqion = " AND A.REGION_CODE= '" + this.getValue("REGION_CODE") + "' ";
        
        //================pangben modify 20110408 stop
        
       
        //========add by huangtt 20141128 end

        String sql =
            " SELECT G.REGION_CHN_ABN,A.ADM_DATE, A.SESSION_CODE, A.CLINICTYPE_CODE, A.REALDEPT_CODE," +
            "        A.REALDR_CODE, C.SESSION_DESC, D.DEPT_ABS_DESC, E.USER_NAME," +
            "        F.CLINICTYPE_DESC, SUM (B.REG_FEE_REAL) REG_FEE," +
            "        SUM (B.CLINIC_FEE_REAL) CLINIC_FEE, SUM (B.AR_AMT) AR_AMT," +
            "        (COUNT (B.CASE_NO) - COUNT (B.RESET_RECEIPT_NO)) REG_COUNT," +
            "        COUNT (B.RESET_RECEIPT_NO) UN_REG_COUNT " +
            "   FROM REG_PATADM A, BIL_REG_RECP B,REG_SESSION C,SYS_DEPT D,SYS_OPERATOR E,REG_CLINICTYPE F,SYS_REGION G " +
            "  WHERE A.CASE_NO = B.CASE_NO(+) "+reqion +//======pangben modify 20110408
            "    AND A.SESSION_CODE = C.SESSION_CODE " +
            "    AND A.REALDEPT_CODE = D.DEPT_CODE " +
            "    AND A.REALDR_CODE = E.USER_ID " +
            "    AND A.REGION_CODE = G.REGION_CODE " +//=========pangben modify 20110408
            "    AND A.CLINICTYPE_CODE = F.CLINICTYPE_CODE " +
            admTypeWhere +
            drCodeWhere +
            deptCodeWhere +
            "    AND A.ADM_DATE BETWEEN TO_DATE('" + startTime +
            "000000" + "','yyyyMMddHH24miss') " +
            "            AND TO_DATE('" + endTime + "235959" +
            "','yyyyMMddHH24miss') " +
            "  GROUP BY A.ADM_DATE, A.SESSION_CODE,A.CLINICTYPE_CODE, A.REALDEPT_CODE, A.REALDR_CODE,C.SESSION_DESC," +
            "           D.DEPT_ABS_DESC,E.USER_NAME,F.CLINICTYPE_DESC,G.REGION_CHN_ABN ORDER BY G.REGION_CHN_ABN ";
        
        selParm = new TParm(TJDODBTool.getInstance().select(sql));
        
        if (selParm.getCount("REALDR_CODE") < 1) {
            this.messageBox("��������");
            this.initPage();
            return selParm;
        }
        
        //==========================lim modify 20120320 start
        //modify by huangtt 20150819 start
//        String sql1 = "SELECT A.CASE_NO,A.VISIT_CODE FROM REG_PATADM A WHERE 1=1 "+admTypeWhere + drCodeWhere +deptCodeWhere + "    AND A.ADM_DATE BETWEEN TO_DATE('" + startTime +
//        "000000" + "','yyyyMMddHH24miss') " +
//        "            AND TO_DATE('" + endTime + "235959" +
//        "','yyyyMMddHH24miss') "+
//        " ORDER BY A.REALDR_CODE ";
        String sql1 = "SELECT A.CASE_NO,A.VISIT_CODE FROM REG_PATADM A, BIL_REG_RECP B WHERE 1=1 AND  A.CASE_NO(+) = B.CASE_NO "+admTypeWhere 
        + drCodeWhere +deptCodeWhere + 
        "    AND A.ADM_DATE BETWEEN TO_DATE('" + startTime +
        "000000" + "','yyyyMMddHH24miss') " +
        "            AND TO_DATE('" + endTime + "235959" +
        "','yyyyMMddHH24miss') "+
        " ORDER BY A.REALDR_CODE ";
        //modify by huangtt 20150819 end
        
        String sql2 =
            " SELECT G.REGION_CHN_ABN,TO_CHAR(A.ADM_DATE,'YYYY/MM/DD') AS ADM_DATE, A.SESSION_CODE, A.CLINICTYPE_CODE, A.REALDEPT_CODE," +
            "        A.REALDR_CODE ,A.CASE_NO "+
            "   FROM REG_PATADM A, BIL_REG_RECP B,REG_SESSION C,SYS_DEPT D,SYS_OPERATOR E,REG_CLINICTYPE F,SYS_REGION G " +
            "  WHERE A.CASE_NO(+) = B.CASE_NO "+reqion +//======pangben modify 20110408
            "  AND A.REGCAN_USER IS NULL AND A.REGCAN_DATE IS NULL" +  //add by huangtt 20150819
            "    AND A.SESSION_CODE = C.SESSION_CODE " +
            "    AND A.REALDEPT_CODE = D.DEPT_CODE " +
            "    AND A.REALDR_CODE = E.USER_ID " +
            "    AND A.REGION_CODE = G.REGION_CODE " +//=========pangben modify 20110408
            "    AND A.CLINICTYPE_CODE = F.CLINICTYPE_CODE " +
            admTypeWhere +
            drCodeWhere +
            deptCodeWhere +
            "    AND A.ADM_DATE BETWEEN TO_DATE('" + startTime +
            "000000" + "','yyyyMMddHH24miss') " +
            "            AND TO_DATE('" + endTime + "235959" +
            "','yyyyMMddHH24miss') " +
            " ORDER BY A.REALDR_CODE ";
        

        TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
        TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
        //==========================lim modify 20120320 end
        
        //======pangben modify 20110425 start �ۼ���ʾ
        //�Һŷ�
        double sumRegFee = 0.00;
        //����
        double sumClinicFee = 0.00;
        //�ܽ��
        double sumArAmt = 0.00;
        //�Һ��˴�
        int sumRegCount = 0;
        //�˹��˴�
        int sumUnRegCount = 0;
        //��������                                 =================modify lim 20120319
        int sumInitClinic = 0 ;
        //��������
        int sumTwiceClinic = 0 ;//================modify lim 20120319
        
        //======pangben modify 20110425 stop
      //  TParm endDate = new TParm();
        int count = selParm.getCount("REALDR_CODE");
       // System.out.println("SSSSSSSSSSSSSSS:::::::::"+count);
        //ADM_DATE;SESSION_DESC;CLINICTYPE_DESC;DEPT_ABS_DESC;USER_NAME;REG_FEE;CLINIC_FEE;AR_AMT;REG_COUNT;UN_REG_COUNT
        for (int i = 0; i < count; i++) {
            //=============pangben modify 20110408 start �ڴ�ӡ�ı������������
            String reqionTemp=selParm.getValue("REGION_CHN_ABN",i);
             //=============pangben modify 20110408 stop
            Timestamp admDate = selParm.getTimestamp("ADM_DATE", i);
            String admDateStr = StringTool.getString(admDate,"yyyy/MM/dd");
//            String admDateStr = StringTool.getString(admDate, "yyyy/MM/dd");
//            String sessionDesc = selParm.getValue("SESSION_DESC", i);
//            String clinicTypeDesc = selParm.getValue("CLINICTYPE_DESC", i);
//            String deptDesc = selParm.getValue("DEPT_ABS_DESC", i);
//            String userName = selParm.getValue("USER_NAME", i);
            double regFee = selParm.getDouble("REG_FEE", i);
            double clinicFee = selParm.getDouble("CLINIC_FEE", i);
            double arAmt = selParm.getDouble("AR_AMT", i);
            int regCount = selParm.getInt("REG_COUNT", i);
            int unRegCount = selParm.getInt("UN_REG_COUNT", i);

            //=======pangben modify 20110425 start
            sumRegFee += StringTool.round(regFee,2);
            sumClinicFee += StringTool.round(clinicFee,2);
            sumArAmt +=StringTool.round(arAmt,2);
            sumRegCount += regCount;
            sumUnRegCount += unRegCount;
            selParm.setData("REG_FEE", i, df.format(StringTool.round(regFee, 2)));
            selParm.setData("CLINIC_FEE", i,
                            df.format(StringTool.round(clinicFee, 2)));
            selParm.setData("AR_AMT", i, df.format(StringTool.round(arAmt, 2)));
            selParm.setData("ADM_DATE", i, admDateStr);
            //=======pangben modify 20110425 stop
            
            //===============================lim modify 20120320 start
            String realDrCode1 = selParm.getValue("REALDR_CODE", i);
            String admDate1 = selParm.getValue("ADM_DATE", i);
            String sessionCode1 = selParm.getValue("SESSION_CODE", i);
            String clinicTypeCode1 = selParm.getValue("CLINICTYPE_CODE", i);
            String realDeptCode1 = selParm.getValue("REALDEPT_CODE", i);
            
            java.util.List<String> caseList = new ArrayList<String>() ;
            //�õ���DR�µ����е�CASE_NO.
            for (int j = 0; j < parm2.getCount("CASE_NO"); j++) {
				if((realDrCode1!=null && realDrCode1.equals(parm2.getValue("REALDR_CODE", j)) 
	    				&& (admDate1!=null && admDate1.equals(parm2.getValue("ADM_DATE", j))))
	    				&& (sessionCode1!=null && sessionCode1.equals(parm2.getValue("SESSION_CODE", j)))
	    				&& (clinicTypeCode1!=null && clinicTypeCode1.equals(parm2.getValue("CLINICTYPE_CODE", j)))
	    				&&  (realDeptCode1!=null && realDeptCode1.equals(parm2.getValue("REALDEPT_CODE", j))) ){
					caseList.add(parm2.getValue("CASE_NO", j)) ;
				}				
			}

            int initClinic = 0 ;
            int twiceClinic = 0 ;
            if(parm1.getCount()>0){
                for (int j = 0; j < parm1.getCount(); j++) {
                	String visitCode = parm1.getValue("VISIT_CODE", j) ;
                	for (String caseNo : caseList) {
						if(caseNo!=null && caseNo.equals(parm1.getValue("CASE_NO", j))){
	    					if("1".equals(visitCode)){
	    						twiceClinic++ ;
	    					}else{
	    						initClinic++ ;
	    					}							
						}
					}
    			}           	
            }
            selParm.setData("INIT_CLINIC", i, initClinic);//����
            selParm.setData("TWICE_CLINIC", i, twiceClinic);//����
            sumInitClinic += initClinic ;
            sumTwiceClinic +=twiceClinic ;     
            //==================================lim modify 20120320 end
            
            //=======================pangben modify 20110425 start ע�͵�����Ҫ
           // System.out.println("unRegCount"+i+":"+unRegCount);
//            endDate.addData("REGION_CHN_ABN", reqionTemp);
//            endDate.addData("ADM_DATE", admDateStr);
//            endDate.addData("SESSION_DESC", sessionDesc);
//            endDate.addData("CLINICTYPE_DESC", clinicTypeDesc);
//            endDate.addData("DEPT_ABS_DESC", deptDesc);
//            endDate.addData("USER_NAME", userName);
//            endDate.addData("REG_FEE", df.format(regFee));
//            endDate.addData("CLINIC_FEE", df.format(clinicFee));
//            endDate.addData("AR_AMT", df.format(arAmt));
//            endDate.addData("REG_COUNT", regCount);
//            endDate.addData("UN_REG_COUNT", unRegCount);
            //=======================pangben modify 20110425 stop
        }
//        endDate.setCount(count);
//         //=============pangben modify 20110408 start
//        endDate.addData("SYSTEM", "COLUMNS","REGION_CHN_ABN");
//         //=============pangben modify 20110408 stop
//        endDate.addData("SYSTEM", "COLUMNS", "ADM_DATE");
//        endDate.addData("SYSTEM", "COLUMNS", "SESSION_DESC");
//        endDate.addData("SYSTEM", "COLUMNS", "CLINICTYPE_DESC");
//        endDate.addData("SYSTEM", "COLUMNS", "DEPT_ABS_DESC");
//        endDate.addData("SYSTEM", "COLUMNS", "USER_NAME");
//        endDate.addData("SYSTEM", "COLUMNS", "REG_FEE");
//        endDate.addData("SYSTEM", "COLUMNS", "CLINIC_FEE");
//        endDate.addData("SYSTEM", "COLUMNS", "AR_AMT");
//        endDate.addData("SYSTEM", "COLUMNS", "REG_COUNT");
//        endDate.addData("SYSTEM", "COLUMNS", "UN_REG_COUNT");

        //=======================pangben modify 20110425 stop
        //===========pangben modify 20110425 start
        selParm.setData("REGION_CHN_ABN", count,
                        "�ܼƣ�");
        selParm.setData("ADM_DATE", count, "");
        selParm.setData("SESSION_DESC", count,"");
        selParm.setData("CLINICTYPE_DESC", count,"");
        selParm.setData("DEPT_ABS_DESC",count, "");
        selParm.setData("USER_NAME", count,"");
        selParm.setData("REG_FEE",count, df.format(sumRegFee));
        selParm.setData("CLINIC_FEE", count, df.format(sumClinicFee));
        selParm.setData("AR_AMT", count, df.format(sumArAmt));
        selParm.setData("REG_COUNT", count, sumRegCount);
        selParm.setData("UN_REG_COUNT", count, sumUnRegCount);
        
        //=====================lim modify 20120319 begin
        selParm.setData("INIT_CLINIC", count, sumInitClinic);
        selParm.setData("TWICE_CLINIC", count, sumTwiceClinic);
        //=====================lim modify 20120319 end

        //===========pangben modify 20110425 stop

        this.callFunction("UI|Table|setParmValue", selParm);
        return selParm;
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "S_DATE")), "yyyyMMdd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
            "E_DATE")), "yyyyMMdd");
        printData = this.getPrintDate(startTime, endTime);
    }

    /**
     * ���Excel
     */
    public void onExport() {

        //�õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
        TTable table = (TTable) callFunction("UI|Table|getThis");
        ExportExcelUtil.getInstance().exportExcel(table, "ҽ��������ͳ�Ʊ���");
    }

    /**
     * ���
     */
    public void onClear() {
        initPage();
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        this.setValue("ADM_TYPE", "");


    }

    /**
     * ��ϸ
     */
    public void onDetial() {
        TTable table = (TTable)this.getComponent("Table");
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            this.messageBox("���ѡ������!");
        }
        TParm tableParm = table.getParmValue();
        String drCode = tableParm.getValue("REALDR_CODE", selRow);
        String admDate =tableParm.getValue("ADM_DATE", selRow);
        //add by huangtt 20141128 start
        String clinictype = "";
        if(this.getValueBoolean("Exclude")){
        	if(this.getValueString("CLINICTYPE").length() != 0)
        		clinictype = this.getValueString("CLINICTYPE");
        }
        //add by huangtt 20141128 end
        Vector vct = new Vector();
        vct.add(0, getValue("S_DATE"));
        vct.add(1, getValue("E_DATE"));
        vct.add(2, drCode);
        vct.add(3, admDate);
        
        this.openDialog("%ROOT%\\config\\reg\\REGDocWorkloadDetial.x", vct);
    }
    



}

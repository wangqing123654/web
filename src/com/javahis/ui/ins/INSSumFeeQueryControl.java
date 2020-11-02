package com.javahis.ui.ins;

import com.dongyang.control.*;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.bil.BILDailyFeeQueryTool;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import java.text.DecimalFormat;
import jdo.bil.BILPayTool;
import jdo.sys.Pat;
import com.javahis.util.StringUtil;
import jdo.sys.SYSChargeHospCodeTool;
import jdo.sys.DictionaryTool;
import jdo.adm.ADMInpTool;
//import com.javahis.device.NJCityInwDriver;
import jdo.sys.PatTool;

/**
 * <p>Title: ÿ�շ����嵥</p>
 *
 * <p>Description: ÿ�շ����嵥</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-
 * @version 1.0
 */
public class INSSumFeeQueryControl extends TControl {
    private String inHosp = ""; //��¼��ѯ������Ժ���˻��ǳ�Ժ����
    private boolean isPush = false; //��¼��ǰ�Ƿ��ǲ�ѯǷ�Ѳ��˵�״̬
    public void onInit() {
        onClear();
    }

    /**
     * ��ʼ������
     */
    public void dateInit() {
        inHosp = "";
        isPush = false;
        callFunction("UI|push|setEnabled", isPush);
        String now = StringTool.getString(SystemTool.getInstance().getDate(),
                                          "yyyyMMdd");
        this.setValue("START_DATE",
                      StringTool.getTimestamp(now + "000000", "yyyyMMddHHmmss"));
        this.setValue("END_DATE",
                      StringTool.getTimestamp(now + "235959", "yyyyMMddHHmmss"));
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        TParm parm = new TParm();
        //��Ժ
        if (this.getValueBoolean("IN")) {
            parm.setData("IN", "Y");
            inHosp = "IN";
        } else if (this.getValueBoolean("OUT")) {
            parm.setData("DS_DATE_S", this.getValue("START_DATE"));
            parm.setData("DS_DATE_E", this.getValue("END_DATE"));
            parm.setData("OUT", "Y");
            inHosp = "OUT";
        }
        if (this.getValueString("DEPT_CODE").length() > 0) {
            parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
        }
        if (this.getValueString("STATION_CODE").length() > 0) {
            parm.setData("STATION_CODE", this.getValue("STATION_CODE"));
        }
        if (this.getValueString("BED_NO").length() > 0) {
            parm.setData("BED_NO", this.getValue("BED_NO"));
        }
        if (this.getValueString("MR_NO").length() > 0) {
            String MR_NO = PatTool.getInstance().checkIpdno(this.getValueString("MR_NO"));
            this.setValue("MR_NO", MR_NO);
            parm.setData("MR_NO", MR_NO);
        }
        if (this.getValueString("IPD_NO").length() > 0) {
            String IPD_NO = PatTool.getInstance().checkIpdno(this.getValueString("IPD_NO"));
            this.setValue("IPD_NO", IPD_NO);
            parm.setData("IPD_NO", this.getValue("IPD_NO"));
        }
        if ("Y".equalsIgnoreCase(this.getValueString("YELLOW_SIGN"))) {
            parm.setData("YELLOW_SIGN", this.getValue("YELLOW_SIGN"));
            isPush = true;
        } else {
            if ("Y".equalsIgnoreCase(this.getValueString("RED_SIGN"))) {
                parm.setData("RED_SIGN", this.getValue("RED_SIGN"));
                isPush = true;
            } else {
                isPush = false;
            }
        }
        if (!"".equals(Operator.getRegion()))
            parm.setData("REGION_CODE", Operator.getRegion());
        TParm result = BILDailyFeeQueryTool.getInstance().selectdata(parm);
        if (result.getErrCode() != 0) {
            this.messageBox("E0005");
            isPush = false;
            return;
        }
        if (result.getCount() == 0) {
            this.messageBox("E0008");
        }
        ((TTable)this.getComponent("TABLE")).setParmValue(result);
        callFunction("UI|push|setEnabled", isPush);
    }

    /**
     * ��ӡԤ��
     */
    public void onPrintView() {
        print(false);
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        print(true);
    }

    /**
     * ��ӡ
     * @param isView boolean �Ƿ���Ԥ��
     */
    public void print(boolean isView) {
        TTable table = (TTable)this.getComponent("TABLE");
        table.acceptText();
        int count = table.getRowCount();
        if (count <= 0) {
            return;
        }
        String startDate = "";
        String endDate = "";
        //�жϲ�ѯ��ʱ���
        if (this.getValueBoolean("PRINT_DATE")) { //ѡ���ӡʱ��
            if (this.getValueString("START_PD").length() <= 0) {
                this.messageBox_("��ѡ���ӡʱ�ε���ʼ����");
                return;
            }
            if (this.getValueString("END_PD").length() <= 0) {
                this.messageBox_("��ѡ���ӡʱ�εĽ�������");
                return;
            }
            startDate = StringTool.getString((Timestamp)this.getValue(
                    "START_PD"), "yyyy/MM/dd");
            endDate = StringTool.getString((Timestamp)this.getValue("END_PD"),
                                           "yyyy/MM/dd");
        }
        for (int i = 0; i < count; i++) {
            if ("N".equals(table.getItemString(i, "FLG"))) {
                continue;
            }
            TParm parm = new TParm();
            if (this.getValueBoolean("FEE_TYPE1")) { //Ժ�ڷ���
                   //������ԡ������嵥���ĸ�ʽ��ӡ ��ôֻ�����վݷ��ô�������Ϳ����� ����ɸѡÿһ���
                    parm.setData("ORDER_BY1", "Y");
            } else if (this.getValueBoolean("FEE_TYPE2")) { //�վݷ���
                    parm.setData("ORDER_BY2", "Y");
            }
            String CASE_NO = table.getItemString(i, "CASE_NO");
//            System.out.println("hhhhhh" + CASE_NO);
            parm.setData("CASE_NO", CASE_NO);
            //�жϲ�ѯ��ʱ���
            if (!this.getValueBoolean("PRINT_DATE")) {
                if ("IN".equals(inHosp)) { //��Ժ���˲�ѯ��ӡ����
                    //��ӡ���ݵ���������ʼ����Ϊ׼
                    startDate = StringTool.getString((Timestamp)this.getValue(
                            "START_DATE"), "yyyy/MM/dd");
                    //��������ȡ��ǰʱ��
                    endDate = StringTool.getString(SystemTool.getInstance().
                            getDate(), "yyyy/MM/dd");
                } else if ("OUT".equals(inHosp)) {
                    startDate = StringTool.getString(table.getItemTimestamp(i,
                            "IN_DATE"), "yyyy/MM/dd");
                    endDate = StringTool.getString(table.getItemTimestamp(i,
                            "DS_DATE"), "yyyy/MM/dd");
                }
            }
            parm.setData("DATE_S", startDate);
            parm.setData("DATE_E", endDate);
            parm.setData("REGION_CODE", Operator.getRegion());
            TParm result = new TParm();
            		String where = "";
                    String insWhere = "";
                    if(parm.getValue("DATE_S").length()>0&&parm.getValue("DATE_E").length()>0){
                        where = " AND A.BILL_DATE BETWEEN TO_DATE('"+parm.getValue("DATE_S")+"'||'000000','YYYY/MM/DDHH24MISS') AND TO_DATE('"+parm.getValue("DATE_E")+"'||'235959','YYYY/MM/DDHH24MISS') ";
                        insWhere = 
                            	" AND (H.KSSJ < TO_DATE ('"+parm.getValue("DATE_S")+"'||'000000', 'YYYY/MM/DDHH24MISS') OR H.KSSJ IS NULL)" +
                            	" AND (H.JSSJ > TO_DATE ('"+parm.getValue("DATE_E")+"'||'235959', 'YYYY/MM/DDHH24MISS') OR H.JSSJ IS NULL)";
                    }
                    String regionWhere  = "";
                    if(!"".equals(parm.getValue("REGION_CODE")))
                        regionWhere = " AND F.REGION_CODE = '"+parm.getValue("REGION_CODE")+"' ";
                    regionWhere = " ";
                    String sql = "SELECT A.REXP_CODE,A.HEXP_CODE,A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION, "+
                   " A.DOSAGE_UNIT,A.OWN_PRICE,SUM(A.DOSAGE_QTY) AS DOSAGE_QTY,SUM(A.TOT_AMT) AS TOT_AMT,C.UNIT_CHN_DESC,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD') AS BILL_DATE, "+
                   " D.CHARGE_HOSP_DESC,E.CHN_DESC,A.EXE_DEPT_CODE,F.COST_CENTER_CHN_DESC AS DEPT_CHN_DESC,B.ORDER_CAT1_CODE, "+
                   " B.NHI_CODE_I,H.PZWH,H.ZFBL1" + //ÿ���嵥����ҽ���롢��ҩ׼�ֺš���������
                   " FROM IBS_ORDD A,SYS_FEE B,SYS_UNIT C,SYS_CHARGE_HOSP D,SYS_DICTIONARY E,SYS_COST_CENTER F,INS_RULE H "+
                   " WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"'" +
                   where +
                   " AND A.OWN_PRICE<>0 "+
                   regionWhere+
                   " AND A.ORDER_CODE=B.ORDER_CODE "+
                   " AND A.DOSAGE_UNIT = C.UNIT_CODE(+) "+
                   " AND B.NHI_CODE_I = H.SFXMBM(+) " +
                   insWhere+
                   " AND A.HEXP_CODE=D.CHARGE_HOSP_CODE "+
                   " AND E.GROUP_ID='SYS_CHARGE' "+
                   " AND A.REXP_CODE=E.ID "+
                   " AND A.EXE_DEPT_CODE=F.COST_CENTER_CODE(+) "+
                   " GROUP BY A.REXP_CODE,A.HEXP_CODE," +
                   "A.ORDER_CODE,B.ORDER_DESC,A.DOSAGE_UNIT,A.OWN_PRICE,A.EXE_DEPT_CODE,TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),C.UNIT_CHN_DESC, "+
                   " D.CHARGE_HOSP_DESC,E.CHN_DESC,B.SPECIFICATION, "+                 
    			   " A.DOSAGE_UNIT,F.COST_CENTER_CHN_DESC,B.ORDER_CAT1_CODE " +
			       ",B.NHI_CODE_I,H.PZWH,H.ZFBL1";
                    String orderBy = "";
                    if(parm.getData("ORDER_BY1")!=null){
                        orderBy = " ORDER BY A.HEXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY2")!=null){
                        orderBy = " ORDER BY A.REXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY3")!=null){
                        orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.HEXP_CODE,A.ORDER_CODE ";
                    }else if(parm.getData("ORDER_BY4")!=null){
                        orderBy = " ORDER BY TO_CHAR(A.BILL_DATE,'YYYY/MM/DD'),A.REXP_CODE,A.ORDER_CODE ";
                    }
//                    System.out.println("ssssssssssssssssss" + sql+orderBy);
                  result = new TParm(TJDODBTool.getInstance().select(sql+orderBy));           	
            if (result.getErrCode() < 0) {
                this.messageBox("E0005");
                return;
            }
            TParm print = new TParm();
            //��ӡ �����嵥
            print = getSumPrintData(result);
            double yjj = getYJJ(CASE_NO); //Ԥ����
            TParm inpData = BILDailyFeeQueryTool.getInstance().selectInpInfo(
                    CASE_NO);
            Pat pat = Pat.onQueryByMrNo(table.getItemString(i, "MR_NO"));
            TParm printData = new TParm();
            printData.setData("TABLE", print.getData());
            printData.setData("TITLE",
                              Operator.getHospitalCHNShortName() + "סԺ������ϸ�嵥");
            printData.setData("MR_NO", table.getItemString(i, "MR_NO"));
            printData.setData("IPD_NO", table.getItemString(i, "IPD_NO"));
            printData.setData("PAT_NAME", table.getItemString(i, "PAT_NAME"));
            printData.setData("PRICE", yjj);
            printData.setData("SEX", pat.getSexString());
            printData.setData("AGE",
                              StringUtil.showAge(pat.getBirthday(),
                                                 inpData.
                                                 getTimestamp("IN_DATE", 0)));
            printData.setData("DEPT", inpData.getValue("DEPT_CHN_DESC", 0));
            printData.setData("STATION", inpData.getValue("STATION_DESC", 0));
            printData.setData("BED", inpData.getValue("BED_NO_DESC", 0));
            printData.setData("CTZ", inpData.getValue("CTZ_DESC", 0));
            printData.setData("DATE", startDate + "��" + endDate);
            printData.setData("NOW",
                              StringTool.getString(SystemTool.getInstance().
                    getDate(), "yyyy/MM/dd HH:mm:ss"));
            if (this.getValueBoolean("PRINT_SUM")) {
                if (this.getValueBoolean("FEE_TYPE1")) {
                    printData.setData("SUM", getFeeType1Sum(result).getData());
                } else if (this.getValueBoolean("FEE_TYPE2")) {
                    printData.setData("SUM", getFeeType2Sum(result).getData());
                }
                printData.setData("TITLE2",
                                  Operator.getHospitalCHNShortName() + "סԺ������ϸ�嵥����");
                printData.setData("DATE2", "���ڣ�" + startDate + "��" + endDate);
            } else {
                TParm s = new TParm();
                s.setData("Visible", false);
                printData.setData("SUM", s.getData());
            }
            this.openPrintDialog(
                    "%ROOT%\\config\\prt\\INS\\INSSumFeeQuery.jhw", printData,
                    isView);
        }
    }

    /**
     * ���ػ����嵥����
     * @param parm TParm
     * @return TParm
     */
    private TParm getSumPrintData(TParm parm) {

        TParm result = new TParm();
        String colunmName = ""; //��¼ָ��������
        String descName = ""; //��¼ָ��������
        //���ݡ�Ժ�ڷ��á���ӡ
        if (this.getValueBoolean("FEE_TYPE1")) {
            colunmName = "HEXP_CODE";
            descName = "CHARGE_HOSP_DESC"; //Ժ�ڷ��ô������ĵ�����
        }
        //���ݡ��վݷ��á���ӡ
        else if (this.getValueBoolean("FEE_TYPE2")) {
            colunmName = "REXP_CODE";
            descName = "CHN_DESC"; //�վݷ������ĵ�����
        }
        DecimalFormat df = new DecimalFormat("0.00");
        DecimalFormat df1 = new DecimalFormat("0");
        String code = ""; //��¼ÿ�����ݵ� �վ����ʹ��� ���� Ժ�ڷ��ô���
        double tot = 0; //��¼�ܼ۸�
        int count = parm.getCount();
        int printCount = 0;
        String orderCode = "" ;
        double ownPrice = 0 ;//�洢��ͬ����ҩƷ�ļ۸�ϼ�
        double dosageQty = 0 ;//�洢����
        double totAmt = 0 ;
        double everyOwnPrice = 0 ;//�洢ÿһ��ҩƷ�ĵ���
        String feeTypeDesc = "" ;
        String cName ="" ;
        String specification = "" ;
        String orderDesc = "" ;
        String unitChnDesc = "" ;
        String execDept = "" ;
        String dosageUnit = "" ;
        String execDeptCode = "" ;
        String billDate = "" ;
        //סԺҽ��ҽ������
        String nhi_code_i = "";
        //��ҩ׼�ֺ�
        String pzwh = "";
        //�Ը�����
        double zfbl1 = 0;
        
        double totDay =0;

        if(parm.getCount()>0){
        	//��ʼ����һ��
        	orderCode = parm.getValue("ORDER_CODE",0) ;
			feeTypeDesc = parm.getValue(descName, 0) ;
			cName = parm.getValue(colunmName, 0) ;
			specification = parm.getValue("SPECIFICATION", 0) ;
			nhi_code_i = parm.getValue("NHI_CODE_I", 0) ;
			pzwh = parm.getValue("PZWH", 0) ;
			zfbl1 = parm.getDouble("ZFBL1", 0) ;
			orderDesc = parm.getValue("ORDER_DESC", 0) ;
			unitChnDesc = parm.getValue("UNIT_CHN_DESC", 0) ;
			execDept = parm.getValue("DEPT_CHN_DESC", 0) ;
			ownPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", 0))) ;
			dosageQty = Double.valueOf(df.format(parm.getDouble("DOSAGE_QTY", 0))) ;
			totAmt = Double.valueOf(df.format(parm.getDouble("TOT_AMT", 0))) ;
			dosageUnit = parm.getValue("DOSAGE_UNIT", 0) ;
			everyOwnPrice = ownPrice ;
			execDeptCode = parm.getValue("EXE_DEPT_CODE", 0) ;
			billDate = parm.getValue("BILL_DATE",0) ;

			//�������һ��
			parm.addData(descName, "") ;
			parm.addData("SPECIFICATION", "") ;
			parm.addData("ORDER_DESC", "") ;
			parm.addData("UNIT_CHN_DESC", "") ;
			parm.addData("DEPT_CHN_DESC", "") ;
			parm.addData("OWN_PRICE", 0) ;
			parm.addData("DOSAGE_QTY", 0) ;
			parm.addData("TOT_AMT", 0) ;
        }

        TParm tempParm = new TParm() ;

        for (int i = 1; i < parm.getCount("ORDER_DESC"); i++) {
        		if(orderCode.equals(parm.getValue("ORDER_CODE",i)) 
        				&& dosageUnit.equals(parm.getValue("DOSAGE_UNIT", i)) 
        				&& everyOwnPrice == Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) 
        				&& execDeptCode.equals(parm.getValue("EXE_DEPT_CODE", i))){
        			ownPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) ;
    				dosageQty += Double.valueOf(df.format(parm.getDouble("DOSAGE_QTY", i))) ;
    				totAmt += Double.valueOf(df.format(parm.getDouble("TOT_AMT", i))) ;
        		}else
        		{
    				tempParm.addData(descName, feeTypeDesc) ;
    				tempParm.addData(colunmName, cName) ;
    				tempParm.addData("SPECIFICATION", specification) ;
    				tempParm.addData("NHI_CODE_I", nhi_code_i);
    				tempParm.addData("PZWH", pzwh);
    				tempParm.addData("ZFBL1", zfbl1);
    				tempParm.addData("ORDER_DESC", orderDesc) ;
    				tempParm.addData("UNIT_CHN_DESC", unitChnDesc) ;
    				tempParm.addData("DEPT_CHN_DESC", execDept) ;

    				tempParm.addData("OWN_PRICE", ownPrice) ;
    				tempParm.addData("DOSAGE_QTY", dosageQty) ;
    				tempParm.addData("TOT_AMT", totAmt) ;

    				orderCode = parm.getValue("ORDER_CODE",i) ;
    				ownPrice = 0 ;
    				dosageQty = 0 ;
    				totAmt = 0 ;

    				feeTypeDesc = parm.getValue(descName, i) ;
    				cName = parm.getValue(colunmName, i) ;
    				specification = parm.getValue("SPECIFICATION", i) ;
    				nhi_code_i = parm.getValue("NHI_CODE_I", i);
    				pzwh = parm.getValue("PZWH", i);
    				zfbl1 = parm.getDouble("ZFBL1", i);
    				orderDesc = parm.getValue("ORDER_DESC", i) ;
    				unitChnDesc = parm.getValue("UNIT_CHN_DESC", i) ;
    				execDept = parm.getValue("DEPT_CHN_DESC", i) ;
    				dosageUnit = parm.getValue("DOSAGE_UNIT", i) ;
    				everyOwnPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) ;
    				execDeptCode = parm.getValue("EXE_DEPT_CODE", i) ;
    				billDate = parm.getValue("BILL_DATE",i) ;

    				ownPrice = Double.valueOf(df.format(parm.getDouble("OWN_PRICE", i))) ;
    				dosageQty += Double.valueOf(df.format(parm.getDouble("DOSAGE_QTY", i))) ;
    				totAmt += Double.valueOf(df.format(parm.getDouble("TOT_AMT", i))) ;
        		}

		}

        parm = tempParm ;
        count = parm.getCount("ORDER_DESC") ;
        for (int i = 0; i < count; i++) {
            if (parm.getDouble("TOT_AMT", i) == 0)
                continue;
            //��������������еĴ��벻ͬ ��ô��¼���µĴ��룬�����ڱ����д�ӡ����Ӧ������ ��Ϊһ������Ŀ�ʼ��
            if (!code.equals(parm.getValue(colunmName, i))) {
            	if(i!=0){
            		result.addData("FEE_TYPE_DESC", "");
            		result.addData("ORDER_DESC", "");
            		result.addData("UNIT_CHN_DESC", "");
            		result.addData("OWN_PRICE", "");
            		result.addData("DOSAGE_QTY", "С��:");
            		result.addData("TOT_AMT", df.format(totDay));
            		result.addData("EXE_DEPT", "");
            		result.addData(".TableRowLineShow", false);
                    totDay = 0;
                    printCount++;
            	}
                code = parm.getValue(colunmName, i);
                result.addData("FEE_TYPE_DESC", parm.getValue(descName, i));
                if (printCount > 0){
                	result.setData(".TableRowLineShow", printCount - 1, true); //����һ�е��߸�Ϊ��ʾ
                }
            } else {
                result.addData("FEE_TYPE_DESC", ""); //ֻ��һ�����ݵ�������ʾ�������� �����в���ʾ
            }
            String SPECIFICATION = parm.getValue("SPECIFICATION", i).length() <=
                                   0 ? "" :
                                   "(" + parm.getValue("SPECIFICATION", i) +
                                   ")";
            String mark = "";
            Double addRate = Double.parseDouble(parm.getValue("ZFBL1", i).equals("")?"0":parm.getValue("ZFBL1", i));
            if(addRate == 1)
            	mark = "��";
            else if (addRate > 0.00&&addRate!=1)
            	mark = "#";
			result.addData("ORDER_DESC", mark + parm.getValue("NHI_CODE_I", i) + " "
					+ parm.getValue("ORDER_DESC", i) + SPECIFICATION + " "
					+ parm.getValue("PZWH", i) + " "
					+ df1.format(parm.getDouble("ZFBL1", i) * 100) + "%");
            result.addData("UNIT_CHN_DESC", parm.getValue("UNIT_CHN_DESC", i));
            result.addData("OWN_PRICE", df.format(parm.getDouble("OWN_PRICE", i)));
            result.addData("DOSAGE_QTY",
                           df.format(parm.getDouble("DOSAGE_QTY", i)));
            result.addData("TOT_AMT", df.format(parm.getDouble("TOT_AMT", i)));
            result.addData("EXE_DEPT", parm.getValue("DEPT_CHN_DESC", i));
            if (i + 1 == count)
                result.addData(".TableRowLineShow", false); //��������ݵ����һ�� ��ô�Ӻ���
            else
                result.addData(".TableRowLineShow", false);
            tot += StringTool.round(parm.getDouble("TOT_AMT", i),2);
            printCount++;
            totDay += parm.getDouble("TOT_AMT", i);
        }
        result.addData("FEE_TYPE_DESC", "");
		result.addData("ORDER_DESC", "");
		result.addData("UNIT_CHN_DESC", "");
		result.addData("OWN_PRICE", "");
		result.addData("DOSAGE_QTY", "С��:");
		result.addData("TOT_AMT", df.format(totDay));
		result.addData("EXE_DEPT", "");
		result.addData(".TableRowLineShow", true);
		printCount++;
        //�ܼ���
        result.addData("FEE_TYPE_DESC", "");
        result.addData("ORDER_DESC", "");
        result.addData("UNIT_CHN_DESC", "");
        result.addData("OWN_PRICE", "");
        result.addData("DOSAGE_QTY", "�ϼƣ�");
        result.addData("TOT_AMT", df.format(tot));
        result.addData("EXE_DEPT", "");
        result.addData(".TableRowLineShow", false);
        result.setCount(printCount + 1);
        result.addData("SYSTEM", "COLUMNS", "FEE_TYPE_DESC");
        result.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        result.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        result.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        result.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
        result.addData("SYSTEM", "COLUMNS", "TOT_AMT");
        result.addData("SYSTEM", "COLUMNS", "EXE_DEPT");
        return result;
    }
    /**
     * Ժ�ڷ����嵥���ܱ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm getFeeType1Sum(TParm parm) {
        TParm result = new TParm();
        //��ѯԺ�ڷ��ô���
        TParm hospCode = SYSChargeHospCodeTool.getInstance().selectalldata();
        int count = hospCode.getCount();
        int rowCount = 0;
        for (int i = 0; i < count; ) {
            //��һ��
            result.addData("DESC_1", hospCode.getValue("CHARGE_HOSP_DESC", i));
            result.addData("AMT_1",
                           getAmt(parm, hospCode.getValue("CHARGE_HOSP_CODE", i),
                                  "HEXP_CODE"));
            i++;
            //�ڶ���
            result.addData("DESC_2", hospCode.getValue("CHARGE_HOSP_DESC", i));
            result.addData("AMT_2",
                           getAmt(parm, hospCode.getValue("CHARGE_HOSP_CODE", i),
                                  "HEXP_CODE"));
            i++;
            //������
            result.addData("DESC_3", hospCode.getValue("CHARGE_HOSP_DESC", i));
            result.addData("AMT_3",
                           getAmt(parm, hospCode.getValue("CHARGE_HOSP_CODE", i),
                                  "HEXP_CODE"));
            i++;
            //������
            result.addData("DESC_4", hospCode.getValue("CHARGE_HOSP_DESC", i));
            result.addData("AMT_4",
                           getAmt(parm, hospCode.getValue("CHARGE_HOSP_CODE", i),
                                  "HEXP_CODE"));
            i++;
            //������
            result.addData("DESC_5", hospCode.getValue("CHARGE_HOSP_DESC", i));
            result.addData("AMT_5",
                           getAmt(parm, hospCode.getValue("CHARGE_HOSP_CODE", i),
                                  "HEXP_CODE"));
            i++;
            rowCount++;
        }
        result.setCount(rowCount);
        result.addData("SYSTEM", "COLUMNS", "DESC_1");
        result.addData("SYSTEM", "COLUMNS", "AMT_1");
        result.addData("SYSTEM", "COLUMNS", "DESC_2");
        result.addData("SYSTEM", "COLUMNS", "AMT_2");
        result.addData("SYSTEM", "COLUMNS", "DESC_3");
        result.addData("SYSTEM", "COLUMNS", "AMT_3");
        result.addData("SYSTEM", "COLUMNS", "DESC_4");
        result.addData("SYSTEM", "COLUMNS", "AMT_4");
        result.addData("SYSTEM", "COLUMNS", "DESC_5");
        result.addData("SYSTEM", "COLUMNS", "AMT_5");
        return result;
    }

    /**
     * �վݷ��û��ܱ�����
     * @param parm TParm
     * @return TParm
     */
    public TParm getFeeType2Sum(TParm parm) {
        TParm result = new TParm();
        //��ѯԺ�ڷ��ô���
        TParm hospCode = DictionaryTool.getInstance().getListAll("SYS_CHARGE");
        //===zhangp 20120306 modify start
        String sql =
			"SELECT CHARGE01,CHARGE02,CHARGE03,CHARGE04,CHARGE05,CHARGE06," +
			"CHARGE07,CHARGE08,CHARGE09,CHARGE10,CHARGE11,CHARGE12,CHARGE13," +
			"CHARGE14,CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19,CHARGE20," +
			"CHARGE21,CHARGE22,CHARGE23,CHARGE24,CHARGE25,CHARGE26," +
			"CHARGE27,CHARGE28,CHARGE29,CHARGE30 " +
			"FROM BIL_RECPPARM WHERE ADM_TYPE = 'I'";
		TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
		TParm hospCode2 = new TParm();
		for (int i = 1; i < 31; i++) {
			if(i==5){
				hospCode2.addData("NAME", "��ҩ��");
				hospCode2.addData("ID", "");
			}
			for (int j = 0; j < hospCode.getCount(); j++) {
				if(i<10){
					if(hospCode.getData("ID", j).equals(temp.getData("CHARGE0"+i, 0))){
						hospCode2.addData("NAME", hospCode.getData("NAME", j));
						hospCode2.addData("ID", hospCode.getData("ID", j));
					}
				}else{
					if(hospCode.getData("ID", j).equals(temp.getData("CHARGE"+i, 0))){
						hospCode2.addData("NAME", hospCode.getData("NAME", j));
						hospCode2.addData("ID", hospCode.getData("ID", j));
					}
				}
			}
		}
//		System.out.println("hospCode2==="+hospCode2);
        int count = hospCode2.getCount("ID");
        int rowCount = 0;
        for (int i = 0; i < count; ) {
            //��һ��
            result.addData("DESC_1", hospCode2.getValue("NAME", i));
            result.addData("AMT_1",
                           getAmt(parm, hospCode2.getValue("ID", i), "REXP_CODE"));
            i++;
            //�ڶ���
            result.addData("DESC_2", hospCode2.getValue("NAME", i));
            result.addData("AMT_2",
                           getAmt(parm, hospCode2.getValue("ID", i), "REXP_CODE"));
            i++;
            //������
            result.addData("DESC_3", hospCode2.getValue("NAME", i));
            result.addData("AMT_3",
                           getAmt(parm, hospCode2.getValue("ID", i), "REXP_CODE"));
            i++;
            //������
            result.addData("DESC_4", hospCode2.getValue("NAME", i));
            result.addData("AMT_4",
                           getAmt(parm, hospCode2.getValue("ID", i), "REXP_CODE"));
            i++;
            //������
            if(i==4){
            	result.addData("DESC_5", hospCode2.getValue("NAME", i));
            	result.addData("AMT_5",result.getDouble("AMT_4", 0)+result.getDouble("AMT_3", 0));
            }else{
            	result.addData("DESC_5", hospCode2.getValue("NAME", i));
                result.addData("AMT_5",
                               getAmt(parm, hospCode2.getValue("ID", i), "REXP_CODE"));
            }
            i++;
            rowCount++;
        }
//        System.out.println("result===="+result);
      //===zhangp 20120306 modify end
        result.setCount(rowCount);
        result.addData("SYSTEM", "COLUMNS", "DESC_1");
        result.addData("SYSTEM", "COLUMNS", "AMT_1");
        result.addData("SYSTEM", "COLUMNS", "DESC_2");
        result.addData("SYSTEM", "COLUMNS", "AMT_2");
        result.addData("SYSTEM", "COLUMNS", "DESC_3");
        result.addData("SYSTEM", "COLUMNS", "AMT_3");
        result.addData("SYSTEM", "COLUMNS", "DESC_4");
        result.addData("SYSTEM", "COLUMNS", "AMT_4");
        result.addData("SYSTEM", "COLUMNS", "DESC_5");
        result.addData("SYSTEM", "COLUMNS", "AMT_5");
        result.setData("Visible", true);
        return result;
    }

    /**
     * ����CODE����ÿ�ַ��õ��ܼ�
     * @param parm TParm
     * @param code String
     * @param colunm String
     * @return double
     */
    private String getAmt(TParm parm, String code, String colunm) {
        double amt = 0;
        DecimalFormat df = new DecimalFormat("0.00");
        int count = parm.getCount();
        for (int j = count - 1; j >= 0; j--) {
            if (code.equals(parm.getValue(colunm, j))) {
                amt += parm.getDouble("TOT_AMT", j);
                parm.removeRow(j);
            }
        }
        return df.format(amt);
    }

    /**
     * ȡ�ò��˵�Ԥ�����ܶ�
     * @param CASE_NO String
     * @return double
     */
    private double getYJJ(String CASE_NO) {
        TParm yjParm = new TParm();
        yjParm.setData("CASE_NO", CASE_NO);
        TParm yjj = BILPayTool.getInstance().selAllDataByRecpNo(yjParm);
        if (yjj.getErrCode() < 0) {
            return 0.0;
        }
        double atm = 0;
        for (int i = 0; i < yjj.getCount(); i++) {
            atm += yjj.getDouble("PRE_AMT", i);
        }
        return atm;
    }

    /**
     * ���
     */
    public void onClear() {
        this.clearValue("START_DATE;END_DATE;DEPT_CODE;STATION_CODE;BED_NO;MR_NO;IPD_NO;START_PD;END_PD;PRINT_SUM;PRINT_DATE");
        this.callFunction("UI|START_PD|setEnabled", false);
        this.callFunction("UI|END_PD|setEnabled", false);
        this.callFunction("UI|STATION_CODE|onQuery");
        this.callFunction("UI|BED_NO|onQuery");
        this.clearValue("YELLOW_SIGN;RED_SIGN");
        dateInit();
        ((TTable)this.getComponent("TABLE")).removeRowAll();
    }

    /**
     * ����ѡ���¼�
     */
    public void onDEPT_CODE() {
        this.clearValue("STATION_CODE;BED_NO");
        this.callFunction("UI|STATION_CODE|onQuery");
        this.callFunction("UI|BED_NO|onQuery");
    }

    /**
     * ����ѡ���¼�
     */
    public void onSTATION_CODE() {
        this.clearValue("BED_NO");
        this.callFunction("UI|BED_NO|onQuery");
    }

    /**
     * ѡ���ӡʱ��
     */
    public void onPRINT_DATE() {
        if (this.getValueBoolean("PRINT_DATE")) {
            this.callFunction("UI|START_PD|setEnabled", true);
            this.callFunction("UI|END_PD|setEnabled", true);
        } else {
            this.callFunction("UI|START_PD|setEnabled", false);
            this.callFunction("UI|END_PD|setEnabled", false);
        }
    }

    /**
     * ȫѡ�¼�
     */
    public void onSEECLTALL() {
        TTable table = (TTable)this.getComponent("TABLE");
        int count = table.getRowCount();
        if (this.getValueBoolean("SELECTALL")) {
            for (int i = 0; i < count; i++) {
                table.setItem(i, "FLG", "Y");
            }
        } else if (!this.getValueBoolean("SELECTALL")) {
            for (int i = 0; i < count; i++) {
                table.setItem(i, "FLG", "N");
            }
        }
    }   
}




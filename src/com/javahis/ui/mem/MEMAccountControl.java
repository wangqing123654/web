package com.javahis.ui.mem;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jdo.bil.BIL;
import jdo.bil.BILSQL;
import jdo.mem.MEMReceiptTool;
import jdo.opb.OPBReceiptTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class MEMAccountControl extends TControl{
	
	TTable table;
    String accountType = "MEM";
    String admType = "M";
    String recpType = "MEM";
    String accountTime;
    
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        //================pangben modify 20110405 start ��������
        setValue("REGION_CODE", Operator.getRegion());
        //================pangben modify 20110405 stop
        //========pangben modify 20110421 start Ȩ�����
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop
        String parmAdmType = (String)this.getParameter();
        if (parmAdmType != null && parmAdmType.length() > 0)
            admType = parmAdmType;
        setValue("ADM_TYPE", "M");
        table = (TTable)this.getComponent("TABLE");
        //�õ��ս�ʱ���
        getAccountTime();
        //��ʼ��ҳ����Ϣ
        initPage();
    }
    
    /**
     * �õ��ս�ʱ���
     * @return String
     */
    public String getAccountTime() {
        String data = StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyyMMddHHmmSS").substring(0, 8);
        accountTime = StringTool.getString(SystemTool.getInstance().getDate(),
        "yyyyMMddHHmmss");

        return accountTime;
    }
    
    /**
     * ��ʼ��ҳ����Ϣ
     */
    public void initPage() {
        setValue("REGION_CODE", Operator.getRegion());
        setValue("ACCOUNT_USER", Operator.getID() == null ? "" : Operator.getID());

        String accountDate =
            getAccountTime().substring(0, 4) + "/" +
            getAccountTime().substring(4, 6) + "/" +
            getAccountTime().substring(6, 8) ;
        String accountDateS =
            getAccountTime().substring(0, 4) + "/" +
            getAccountTime().substring(4, 6) + "/" +
            getAccountTime().substring(6, 8) ;
        setValue("ACCOUNT_DATE", accountDateS);
        setValue("ACCOUNT_DATEE", accountDate);
//        setValue("DEPT", Operator.getDept());

    }
    
    /**
     * ��ѯ
     */
    public void onQuery() {

        TParm parm = new TParm();
        parm.setData("ACCOUNT_USER", getValue("ACCOUNT_USER"));
        if (getValue("ACCOUNT_DATE") == null ||
            getValueString("ACCOUNT_DATE").equals("")) {
            messageBox("��ѡ��ʼʱ��!");
            return;
        }
        parm.setData("ACCOUNT_DATE", StringTool.getString((Timestamp) getValue("ACCOUNT_DATE"),"yyyyMMdd")+this.getValueString("S_TIME").replace(":",""));
        if (getValue("ACCOUNT_DATEE") == null ||
            getValueString("ACCOUNT_DATEE").equals("")) {
            messageBox("��ѡ�����ʱ��!");
            return;
        }
        parm.setData("ACCOUNT_DATEE",StringTool.getString((Timestamp) getValue("ACCOUNT_DATEE"),"yyyyMMdd")+this.getValueString("E_TIME").replace(":",""));
		parm.setData("ADM_TYPE", getValue("ADM_TYPE"));

		if (getValueString("REGION_CODE").length() > 0)
			parm.setData("REGION_CODE", getValueString("REGION_CODE"));

		String sql = "SELECT 'N' S,B.REGION_CHN_ABN AS REGION_CHN_DESC,A.ACCOUNT_SEQ,A.ACCOUNT_DATE,A.ACCOUNT_USER,A.AR_AMT,"
				+ "       A.STATUS,A.INVALID_COUNT "
				+ "  FROM BIL_ACCOUNT A, SYS_REGION B ,SYS_OPERATOR_DEPT C  "
				+ "WHERE A.REGION_CODE=B.REGION_CODE AND A.ACCOUNT_TYPE = 'MEM' AND " +
				"A.ACCOUNT_USER = C.USER_ID(+) AND C.MAIN_FLG = 'Y' AND";
		String region = parm.getValue("REGION_CODE");
		String dept = getValueString("DEPT");
		if (region != null && !region.trim().equals(""))
			sql += " A.REGION_CODE='" + region + "' AND ";
		String value = parm.getValue("ACCOUNT_USER");
		if (value != null && !value.equals(""))
			sql += " A.ACCOUNT_USER='" + value + "' AND ";
		String admType = parm.getValue("ADM_TYPE");
		if (admType != null && !admType.equals("")){
			sql += " A.ADM_TYPE='" + admType + "' AND ";
		}else{
			sql += " A.ADM_TYPE IN ('M') AND ";
		}
		if (dept != null && !dept.equals(""))
			sql += " C.DEPT_CODE='" + dept + "' AND ";
		sql += " A.ACCOUNT_DATE BETWEEN TO_DATE('"
				+ parm.getValue("ACCOUNT_DATE") + "','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE('" + parm.getValue("ACCOUNT_DATEE")
				+ "','YYYYMMDDHH24MISS')";
		sql += " ORDER BY B.REGION_CHN_DESC, A.ACCOUNT_SEQ";

		 System.out.println("�ս��ѯ����������������������������"+sql);
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return;
		}
		table.setParmValue(result);
	}
    
    /**
     * ȫѡ�¼�
     */
    public void onSelectAll() {
        String select = getValueString("SELECT");
        TParm parm = table.getParmValue();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            parm.setData("S", i, select);
        }
        table.setParmValue(parm);
    }
    
    /**
     * ����Excel���
     */
    public void onExport() {
        TTable table = (TTable) callFunction("UI|TABLE|getThis");
        if (table.getRowCount() <= 0) {
            messageBox("�޵�������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "�ײ�����ת�սᱨ��");
    }
    
    /**
     * ��˴�ӡ����
     * @param tableParm TParm
     * @return boolean
     */
    public boolean checkPrintData(TParm tableParm) {
        //�ս�������
        int count = table.getRowCount();
        if (count <= 0) {
            messageBox("�޴�ӡ����!");
            return false;
        }
        for (int i = 0; i < count; i++) {
            if (tableParm.getValue("S", i).equals("Y")) {
                return true;
            }
        }
        messageBox("�޴�ӡ����!");
        return false;
    }
    
    /**
     *�õ��ս�����
     * @param tableParm TParm
     * @return TParm
     */
    public TParm getAccountData(TParm tableParm) {
        TParm parm = new TParm();
        String accountSeq = getAccountSeq(tableParm);
//        toBalacnce(accountSeq);
//        System.out.println("===accountSeq accountSeq is ::"+accountSeq);
        parm = getPrintNo(accountSeq);
        if (parm == null)
            return null;
        return parm;
    }

    /**
     * �õ��ս��
     * @param tableParm TParm
     * @return String
     */
    public String getAccountSeq(TParm tableParm) {
      //  System.out.println("�õ��ս�����"+tableParm);
        int count = tableParm.getCount();
        String accountSeq = "";
        String seq = "";
        for (int i = 0; i < count; i++) {
            seq = tableParm.getValue("S", i);
            if (seq.equals("Y")) {
                if (accountSeq.length() > 0)
                    accountSeq += ",";
                accountSeq += "'" + tableParm.getValue("ACCOUNT_SEQ", i) + "'";
            }
        }
        return accountSeq;
    }
    
    /**
     * Ʊ��
     * @param parm TParm
     * @return TParm
     */
    public String dealTearPrint(TParm parm) {
        int count = parm.getCount();
        String printNo = "";
        if (count > 0)
            for (int i = 0; i < count; i++) {
                if (printNo.length() > 0)
                    printNo += "'";
                printNo += parm.getValue("PRINT_NO", i);
            }
        return printNo;
    }
    
	/**
	 * �Ƚ�Ʊ��
	 * ===========zhangp 20120918
	 * @param inv_no
	 * @param latestInv_no
	 * @return
	 */
	private boolean compareInvno(String inv_no, String latestInv_no) {
		String inv_no_num = inv_no.replaceAll("[^0-9]", "");// ȥ������
		String inv_no_word = inv_no.replaceAll("[0-9]", "");// ȥ����
		String latestInv_no_num = latestInv_no.replaceAll("[^0-9]", "");
		String latestInv_no_word = latestInv_no.replaceAll("[0-9]", "");
		if (inv_no_word.equals(latestInv_no_word)
				&& Long.valueOf(inv_no_num)
						- Long.valueOf(latestInv_no_num) == 1) {
			return true;
		} else {
			return false;
		}
	}
    
    /**
	 * ȡ���շ�Ʊ��
	 * =======zhangp 20120918
	 * @param start_date
	 * @param end_date
	 * @return
	 */
	private TParm getOPBparm(String account_seq) {
		String sql = " SELECT   '�շ�' RECP_TYPE, A.INV_NO, B.USER_NAME PRINT_USER " +
						" FROM BIL_INVRCP A , SYS_OPERATOR B " +
						" WHERE A.ACCOUNT_SEQ IN (" + account_seq + ")" +
						" AND A.PRINT_USER = B.USER_ID " +
						" AND A.RECP_TYPE = 'MEM'";
		sql += " ORDER BY A.RECP_TYPE, A.PRINT_USER, A.INV_NO";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() < 0) {
			return result;
		}
		String print_user = result.getValue("PRINT_USER", 0);
		String inv_no = result.getValue("INV_NO", 0);
		TParm opbParm = new TParm();
		List<String> opblist = new ArrayList<String>();
		opblist.add(result.getValue("INV_NO", 0));
		int opbcount = 0;
		String inv_nos = "";
		for (int i = 1; i < result.getCount(); i++) {
			if (result.getValue("PRINT_USER", i).equals(print_user)) {
				if (!compareInvno(result.getValue("INV_NO", i), inv_no)) {
					inv_nos += opblist.get(0) + "~"
							+ opblist.get(opblist.size() - 1) + ",";
					opbcount += opblist.size();
					opblist = new ArrayList<String>();
				}
			} else {
				inv_nos += opblist.get(0) + "~"
						+ opblist.get(opblist.size() - 1) + ",";
				opbcount += opblist.size();
				opblist = new ArrayList<String>();
				inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
				opbParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", i));
				opbParm.addData("PRINT_USER", print_user);
				opbParm.addData("INV_NOS", inv_nos);
				opbParm.addData("INV_COUNT", opbcount);
				inv_nos = "";
				opbcount = 0;
			}
			inv_no = result.getValue("INV_NO", i);
			print_user = result.getValue("PRINT_USER", i);
			opblist.add(result.getValue("INV_NO", i));
		}
		if (opblist.size() > 0) {
			inv_nos += opblist.get(0) + "~" + opblist.get(opblist.size() - 1)
					+ ",";
		}
		opbcount += opblist.size();
		inv_nos = inv_nos.substring(0, inv_nos.length() - 1);
		opbParm.addData("RECP_TYPE", result.getValue("RECP_TYPE", 0));
		opbParm.addData("PRINT_USER", print_user);
		opbParm.addData("INV_NOS", inv_nos);
		opbParm.addData("INV_COUNT", opbcount);
		return opbParm;
	}
	
	 /**
     * �õ�����Ʊ��
     * @param accountSeq String
     * @param admType String
     * @return String
     */
    public  String getTearPringNo(String accountSeq, String admType) {
        if (accountSeq == null || accountSeq.length() <= 0)
            return "";
        String sql =
                " SELECT INV_NO,AR_AMT FROM BIL_INVRCP " +
                "  WHERE ACCOUNT_SEQ IN(" + accountSeq + ")" +
                "    AND RECP_TYPE = 'MEM' " +
                "    AND ADM_TYPE LIKE '%" + admType + "%' " +
                "    AND CANCEL_FLG IN('2','3')";
        return sql;
    }
    
    /**
     * Ʊ������
     * @param printNo Vector
     * @return String
     */
    public String getPrintNOs(Vector printNo) {
        if (printNo == null)
            return "";
        String s1 = "";
        String s2 = "";
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < printNo.size(); i++) {
            String t = (String) printNo.get(i);
            if (s2.length() == 0) {
                s1 = t;
                s2 = s1;
                continue;
            }
            if (StringTool.addString(s2).equals(t))
                s2 = t;
            else {
                if (sb.length() > 0)
                    sb.append(",");
                if (s1.equals(s2)) {
                    sb.append(s1);
                }
                else
                    sb.append(s1 + "-" + s2);
                s1 = t;
                s2 = s1;
            }
        }
        if (sb.length() > 0)
            sb.append(",");
        if (s1.equals(s2)) {
            sb.append(s1);
        }
        else
            sb.append(s1 + "-" + s2);
        return sb.toString();
    }
    
    /**
     * �����ӡparm
     * @param parm TParm
     * @return TParm
     */
    public TParm dealPrintParm(TParm parm) {
        TParm printParm = new TParm();
        BIL bil = new BIL();
        //֧������parm
        TParm payParm = bil.getPayParm();
//        System.out.println("֧������PARM=="+payParm);
        //֧�������ܼ�
        TParm payTot = bil.getPayParm();
        //�շ���ĿParma
        TParm chargeParm = bil.getChargeParm();
        //�˷�
        TParm payParmBack = bil.getPayParm();
//        TParm ssssparm = sssss();
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            //�շ���Ŀ
            chargeParm.setData("CHARGE01",
                               chargeParm.getDouble("CHARGE01") +
                               parm.getDouble("CHARGE01", i));
            chargeParm.setData("CHARGE02",
                               chargeParm.getDouble("CHARGE02") +
                               parm.getDouble("CHARGE02", i));
            chargeParm.setData("CHARGE1AND2",
            		chargeParm.getDouble("CHARGE02") +
                    chargeParm.getDouble("CHARGE01") );
            chargeParm.setData("CHARGE03",
                               chargeParm.getDouble("CHARGE03") +
                               parm.getDouble("CHARGE03", i));
            chargeParm.setData("CHARGE04",
                               chargeParm.getDouble("CHARGE04") +
                               parm.getDouble("CHARGE04", i));
            chargeParm.setData("CHARGE05",
                               chargeParm.getDouble("CHARGE05") +
                               parm.getDouble("CHARGE05", i));
            chargeParm.setData("CHARGE06",
                               chargeParm.getDouble("CHARGE06") +
                               parm.getDouble("CHARGE06", i));
            chargeParm.setData("CHARGE07",
                               chargeParm.getDouble("CHARGE07") +
                               parm.getDouble("CHARGE07", i));
            chargeParm.setData("CHARGE08",
                               chargeParm.getDouble("CHARGE08") +
                               parm.getDouble("CHARGE08", i));
            chargeParm.setData("CHARGE09",
                               chargeParm.getDouble("CHARGE09") +
                               parm.getDouble("CHARGE09", i));
            chargeParm.setData("CHARGE10",
                               chargeParm.getDouble("CHARGE10") +
                               parm.getDouble("CHARGE10", i));
            chargeParm.setData("CHARGE11",
                               chargeParm.getDouble("CHARGE11") +
                               parm.getDouble("CHARGE11", i));
            chargeParm.setData("CHARGE12",
                               chargeParm.getDouble("CHARGE12") +
                               parm.getDouble("CHARGE12", i));
            chargeParm.setData("CHARGE13",
                               chargeParm.getDouble("CHARGE13") +
                               parm.getDouble("CHARGE13", i));
            chargeParm.setData("CHARGE14",
                               chargeParm.getDouble("CHARGE14") +
                               parm.getDouble("CHARGE14", i));
            chargeParm.setData("CHARGE15",
                               chargeParm.getDouble("CHARGE15") +
                               parm.getDouble("CHARGE15", i));
            chargeParm.setData("CHARGE16",
                               chargeParm.getDouble("CHARGE16") +
                               parm.getDouble("CHARGE16", i));
            chargeParm.setData("CHARGE17",
                               chargeParm.getDouble("CHARGE17") +
                               parm.getDouble("CHARGE17", i));
            chargeParm.setData("CHARGE18",
                               chargeParm.getDouble("CHARGE18") +
                               parm.getDouble("CHARGE18", i));
            chargeParm.setData("CHARGE19",
                               chargeParm.getDouble("CHARGE19") +
                               parm.getDouble("CHARGE19", i));
            chargeParm.setData("CHARGE20",
                               chargeParm.getDouble("CHARGE20") +
                               parm.getDouble("CHARGE20", i));
            chargeParm.setData("CHARGE21",
                               chargeParm.getDouble("CHARGE21") +
                               parm.getDouble("CHARGE21", i));
            chargeParm.setData("CHARGE22",
                               chargeParm.getDouble("CHARGE22") +
                               parm.getDouble("CHARGE22", i));
            chargeParm.setData("CHARGE23",
                               chargeParm.getDouble("CHARGE23") +
                               parm.getDouble("CHARGE23", i));
            chargeParm.setData("CHARGE24",
                               chargeParm.getDouble("CHARGE24") +
                               parm.getDouble("CHARGE24", i));
            chargeParm.setData("CHARGE25",
                               chargeParm.getDouble("CHARGE25") +
                               parm.getDouble("CHARGE25", i));
            chargeParm.setData("CHARGE26",
                               chargeParm.getDouble("CHARGE26") +
                               parm.getDouble("CHARGE26", i));
            chargeParm.setData("CHARGE27",
                               chargeParm.getDouble("CHARGE27") +
                               parm.getDouble("CHARGE27", i));
            chargeParm.setData("CHARGE28",
                               chargeParm.getDouble("CHARGE28") +
                               parm.getDouble("CHARGE28", i));
            chargeParm.setData("CHARGE29",
                               chargeParm.getDouble("CHARGE29") +
                               parm.getDouble("CHARGE29", i));
            chargeParm.setData("CHARGE30",
                               chargeParm.getDouble("CHARGE30") +
                               parm.getDouble("CHARGE30", i));
            chargeParm.setData("REDUCE_AMT",//������
                    chargeParm.getDouble("REDUCE_AMT")+
                     parm.getDouble("REDUCE_AMT", i));
           
            if (parm.getDouble("AR_AMT", i) >= 0) {
//                System.out.println("payaramt========"+payParm.getDouble("AR_AMT"));
                //֧����ʽ
                payParm.setData("TOT_AMT",
                                payParm.getDouble("TOT_AMT") +
                                parm.getDouble("TOT_AMT", i));
                payParm.setData("AR_AMT",
                                payParm.getDouble("AR_AMT") +
                                parm.getDouble("AR_AMT", i));
                payParm.setData("PAY_CASH",
                		 payParm.getDouble("PAY_CASH") +
                         parm.getDouble("PAY_TYPE01", i));//�ֽ�
                payParm.setData("PAY_MEDICAL_CARD",
                                payParm.getDouble("PAY_MEDICAL_CARD") +
                                parm.getDouble("PAY_MEDICAL_CARD", i));//ҽ�ƿ�
                payParm.setData("PAY_BANK_CARD",
                                payParm.getDouble("PAY_BANK_CARD") +
                                parm.getDouble("PAY_BANK_CARD", i)+
                                parm.getDouble("PAY_TYPE02", i)//ˢ��
                                );
                payParm.setData("PAY_INS_CARD",
                                payParm.getDouble("PAY_INS_CARD") +
                                parm.getDouble("PAY_INS_CARD", i));
                payParm.setData("PAY_CHECK",
                                payParm.getDouble("PAY_CHECK") +
                                parm.getDouble("PAY_CHECK", i)+
                                parm.getDouble("PAY_TYPE06", i));//֧Ʊ
                
                payParm.setData("PAY_OTHER3",
                        payParm.getDouble("PAY_OTHER3") +
                        parm.getDouble("PAY_OTHER3", i)+
                        parm.getDouble("PAY_TYPE05", i));//��Ʒ��  yanjing 20141020 
                payParm.setData("PAY_OTHER4",
                        payParm.getDouble("PAY_OTHER4") +
                        parm.getDouble("PAY_OTHER4", i) +
                        parm.getDouble("PAY_TYPE07", i));//�ֽ��ۿ�ȯ  yanjing 20141020 
                payParm.setData("PAY_TYPE04",
                        payParm.getDouble("PAY_TYPE04") +
                        parm.getDouble("PAY_TYPE04", i));//ҽԺ�渶   yanjing 20141020 
                payParm.setData("PAY_TYPE08",
                        payParm.getDouble("PAY_TYPE08") +
                        parm.getDouble("PAY_TYPE08", i));//����ֱ��  huangtt 20150519
                payParm.setData("PAY_TYPE09",
                		payParm.getDouble("PAY_TYPE09") +
                		parm.getDouble("PAY_TYPE09", i));//����ֱ��  huangtt 20150519
                payParm.setData("PAY_TYPE10",
                		payParm.getDouble("PAY_TYPE10") +
                		parm.getDouble("PAY_TYPE10", i));//����ֱ��  huangtt 20150519
                
                payParm.setData("PAY_DEBIT",
                                payParm.getDouble("PAY_DEBIT") +
                                parm.getDouble("PAY_DEBIT", i));
                payParm.setData("PAY_BILPAY",
                                payParm.getDouble("PAY_BILPAY") +
                                parm.getDouble("PAY_BILPAY", i));
                payParm.setData("PAY_OTHER1",
                                payParm.getDouble("PAY_OTHER1") +
                                parm.getDouble("PAY_OTHER1", i));
                payParm.setData("PAY_OTHER2",
                                payParm.getDouble("PAY_OTHER2") +
                                parm.getDouble("PAY_OTHER2", i));
                payParm.setData("PAY_INS",
                                payParm.getDouble("PAY_INS") +
                                parm.getDouble("PAY_INS", i));
                //֧����ʽ�ܼ�
                payTot.setData("TOT_AMT",
                               payTot.getDouble("TOT_AMT") +
                               parm.getDouble("TOT_AMT", i));
                payTot.setData("AR_AMT",
                               payTot.getDouble("AR_AMT") +
                               parm.getDouble("AR_AMT", i));
                payTot.setData("DIS_AMT",//�Żݽ��
                        payTot.getDouble("TOT_AMT") -
                        payTot.getDouble("AR_AMT"));
                payTot.setData("PAY_CASH",
                               payTot.getDouble("PAY_CASH") +
                               parm.getDouble("PAY_TYPE01", i));
                payTot.setData("PAY_MEDICAL_CARD",
                               payTot.getDouble("PAY_MEDICAL_CARD") +
                               parm.getDouble("PAY_MEDICAL_CARD", i));
                payTot.setData("PAY_OTHER3",
                		payTot.getDouble("PAY_OTHER3") +
                        parm.getDouble("PAY_OTHER3", i)+
                        parm.getDouble("PAY_TYPE05", i));//��Ʒ��  yanjing 20141020 
                payTot.setData("PAY_OTHER4",
                		payTot.getDouble("PAY_OTHER4") +
                        parm.getDouble("PAY_OTHER4", i)+
                        parm.getDouble("PAY_TYPE07", i));//�ֽ��ۿ�ȯ  yanjing 20141020 
                payTot.setData("PAY_TYPE04",
                		payTot.getDouble("PAY_TYPE04") +
                        parm.getDouble("PAY_TYPE04", i));//ҽԺ�渶   yanjing 20141020 
                payTot.setData("PAY_TYPE08",
                		payTot.getDouble("PAY_TYPE08") +
                        parm.getDouble("PAY_TYPE08", i));//����ֱ�� huangtt 20150519
                payTot.setData("PAY_TYPE09",
                		payTot.getDouble("PAY_TYPE09") +
                		parm.getDouble("PAY_TYPE09", i));//����ֱ�� huangtt 20150519
                payTot.setData("PAY_TYPE10",
                		payTot.getDouble("PAY_TYPE10") +
                		parm.getDouble("PAY_TYPE10", i));//����ֱ�� huangtt 20150519
                
                payTot.setData("PAY_BANK_CARD",
                               payTot.getDouble("PAY_BANK_CARD") +
                               parm.getDouble("PAY_BANK_CARD", i)+
                               parm.getDouble("PAY_TYPE02", i));
                payTot.setData("PAY_INS_CARD",
                               payTot.getDouble("PAY_INS_CARD") +
                               parm.getDouble("PAY_INS_CARD", i));
                payTot.setData("PAY_CHECK",
                               payTot.getDouble("PAY_CHECK") +
                               parm.getDouble("PAY_CHECK", i)+
                               parm.getDouble("PAY_TYPE06", i));
                payTot.setData("PAY_DEBIT",
                               payTot.getDouble("PAY_DEBIT") +
                               parm.getDouble("PAY_DEBIT", i));
                payTot.setData("PAY_BILPAY",
                               payTot.getDouble("PAY_BILPAY") +
                               parm.getDouble("PAY_BILPAY", i));
                payTot.setData("PAY_OTHER1",
                               payTot.getDouble("PAY_OTHER1") +
                               parm.getDouble("PAY_OTHER1", i));
                payTot.setData("PAY_OTHER2",
                               payTot.getDouble("PAY_OTHER2") +
                               parm.getDouble("PAY_OTHER2", i));
                payTot.setData("PAY_INS",
                               payTot.getDouble("PAY_INS") +
                               parm.getDouble("PAY_INS", i));
            }
            else { //�˷��ܼ�
                //֧����ʽ
                payParmBack.setData("TOT_AMT",
                		(payParmBack.getDouble("TOT_AMT") +
                              parm.getDouble("TOT_AMT", i)));
                payParmBack.setData("AR_AMT",
                		(payParmBack.getDouble("AR_AMT") +
                                parm.getDouble("AR_AMT", i)));
                payParmBack.setData("PAY_CASH",
                		(payParmBack.getDouble("PAY_CASH") +
                                parm.getDouble("PAY_TYPE01", i)));
                payParmBack.setData("PAY_MEDICAL_CARD",
                		(payParmBack.getDouble("PAY_MEDICAL_CARD") +
                                parm.getDouble("PAY_MEDICAL_CARD", i)));
                payParmBack.setData("PAY_OTHER3",
                		(payParmBack.getDouble("PAY_OTHER3") +
                                parm.getDouble("PAY_OTHER3", i)+
                                parm.getDouble("PAY_TYPE05", i)));
                payParmBack.setData("PAY_OTHER4",
                		(payParmBack.getDouble("PAY_OTHER4") +
                                parm.getDouble("PAY_OTHER4", i)+
                                parm.getDouble("PAY_TYPE07", i)));
                payParmBack.setData("PAY_TYPE04",
                		payParmBack.getDouble("PAY_TYPE04") +
                        parm.getDouble("PAY_TYPE04", i));//ҽԺ�渶   yanjing 20141020
                
                payParmBack.setData("PAY_TYPE08",
                		payParmBack.getDouble("PAY_TYPE08") +
                        parm.getDouble("PAY_TYPE08", i));//����ֱ�� huangtt 20150519
                payParmBack.setData("PAY_TYPE09",
                		payParmBack.getDouble("PAY_TYPE09") +
                		parm.getDouble("PAY_TYPE09", i));//����ֱ�� huangtt 20150519
                payParmBack.setData("PAY_TYPE10",
                		payParmBack.getDouble("PAY_TYPE10") +
                		parm.getDouble("PAY_TYPE10", i));//����ֱ�� huangtt 20150519
                
                payParmBack.setData("PAY_BANK_CARD",
                		(payParmBack.getDouble("PAY_BANK_CARD") +
                                parm.getDouble("PAY_BANK_CARD", i)+
                                parm.getDouble("PAY_TYPE02", i)));
                payParmBack.setData("PAY_INS_CARD",
                		Math.abs((payParmBack.getDouble("PAY_INS_CARD") +
                                parm.getDouble("PAY_INS_CARD", i))));
                payParmBack.setData("PAY_CHECK",
                		(payParmBack.getDouble("PAY_CHECK") +
                                parm.getDouble("PAY_CHECK", i)+
                                parm.getDouble("PAY_TYPE06", i)));
                payParmBack.setData("PAY_DEBIT",
                		(payParmBack.getDouble("PAY_DEBIT") +
                                parm.getDouble("PAY_DEBIT", i)));
                payParmBack.setData("PAY_BILPAY",
                		(payParmBack.getDouble("PAY_BILPAY") +
                                parm.getDouble("PAY_BILPAY", i)));
                payParmBack.setData("PAY_OTHER1",
                		(payParmBack.getDouble("PAY_OTHER1") +
                                parm.getDouble("PAY_OTHER1", i)));
                payParmBack.setData("PAY_OTHER2",
                		(payParmBack.getDouble("PAY_OTHER2") +
                                parm.getDouble("PAY_OTHER2", i)));
                payParmBack.setData("PAY_INS",
                		(payParmBack.getDouble("PAY_INS") +
                                parm.getDouble("PAY_INS", i)));
                //===zhangp 20120418 end
                //֧����ʽ�ܼ�
                payTot.setData("TOT_AMT",
                               payTot.getDouble("TOT_AMT") +
                               parm.getDouble("TOT_AMT", i));
                payTot.setData("AR_AMT",
                               payTot.getDouble("AR_AMT") +
                               parm.getDouble("AR_AMT", i));
                payTot.setData("DIS_AMT",//�Żݽ��
                        payTot.getDouble("TOT_AMT") -
                        payTot.getDouble("AR_AMT"));
                payTot.setData("PAY_CASH",
                               payTot.getDouble("PAY_CASH") +
                               parm.getDouble("PAY_TYPE01", i));
                payTot.setData("PAY_MEDICAL_CARD",
                               payTot.getDouble("PAY_MEDICAL_CARD") +
                               parm.getDouble("PAY_MEDICAL_CARD", i));
                payTot.setData("PAY_OTHER3",
                        payTot.getDouble("PAY_OTHER3") +
                        parm.getDouble("PAY_OTHER3", i)+
                        parm.getDouble("PAY_TYPE05", i));
                payTot.setData("PAY_OTHER4",
                        payTot.getDouble("PAY_OTHER4") +
                        parm.getDouble("PAY_OTHER4", i)+
                        parm.getDouble("PAY_TYPE07", i));
                payTot.setData("PAY_TYPE04",
                		payTot.getDouble("PAY_TYPE04") +
                        parm.getDouble("PAY_TYPE04", i));//ҽԺ�渶   yanjing 20141020 
                payTot.setData("PAY_TYPE08",
                		payTot.getDouble("PAY_TYPE08") +
                        parm.getDouble("PAY_TYPE08", i));//����ֱ�� huangtt 20150519
                payTot.setData("PAY_TYPE09",
                		payTot.getDouble("PAY_TYPE09") +
                		parm.getDouble("PAY_TYPE09", i));//����ֱ�� huangtt 20150519
                payTot.setData("PAY_TYPE10",
                		payTot.getDouble("PAY_TYPE10") +
                		parm.getDouble("PAY_TYPE10", i));//����ֱ�� huangtt 20150519
                payTot.setData("PAY_BANK_CARD",
                               payTot.getDouble("PAY_BANK_CARD") +
                               parm.getDouble("PAY_BANK_CARD", i)+
                               parm.getDouble("PAY_TYPE02", i));
                payTot.setData("PAY_INS_CARD",
                               payTot.getDouble("PAY_INS_CARD") +
                               parm.getDouble("PAY_INS_CARD", i));
                payTot.setData("PAY_CHECK",
                               payTot.getDouble("PAY_CHECK") +
                               parm.getDouble("PAY_CHECK", i)+
                               parm.getDouble("PAY_TYPE06", i));
                payTot.setData("PAY_DEBIT",
                               payTot.getDouble("PAY_DEBIT") +
                               parm.getDouble("PAY_DEBIT", i));
                payTot.setData("PAY_BILPAY",
                               payTot.getDouble("PAY_BILPAY") +
                               parm.getDouble("PAY_BILPAY", i));
                payTot.setData("PAY_OTHER1",
                               payTot.getDouble("PAY_OTHER1") +
                               parm.getDouble("PAY_OTHER1", i));
                payTot.setData("PAY_OTHER2",
                               payTot.getDouble("PAY_OTHER2") +
                               parm.getDouble("PAY_OTHER2", i));
            }
        }
        if(chargeParm.getDouble("REDUCE_AMT")>0){//����Ľ����ʾΪ��ֵ
        	chargeParm.setData("REDUCE_AMT",//������
                    -chargeParm.getDouble("REDUCE_AMT"));
        }

//        charge3�г�ҩ��
//        charge4�в�ҩ��
//        charge5����
//        charge6���Ʒ�
//        charge7�����
//        charge8������
//        charge10�����
//        charge9��Ѫ��
//        charge18������
//        charge11����
//        charge12����ҽ��
//        charge13�۲촲��
//        charge16�ԷѲ���
//        charge14CT
//        charge15MR
//        charge17���Ϸ�
//        charge1������
//        charge2�ǿ�����
        //===zhangp 2012419 start
        payParmBack.setData("TOT_AMT",
        		Math.abs(payParmBack.getDouble("TOT_AMT")));
        payParmBack.setData("AR_AMT",
        		Math.abs(payParmBack.getDouble("AR_AMT")));
        payParmBack.setData("PAY_CASH",
        		Math.abs(payParmBack.getDouble("PAY_CASH")));
        payParmBack.setData("PAY_MEDICAL_CARD",
        		Math.abs(payParmBack.getDouble("PAY_MEDICAL_CARD")));
        payParmBack.setData("PAY_OTHER3",
        		Math.abs(payParmBack.getDouble("PAY_OTHER3")));
        payParmBack.setData("PAY_OTHER4",
        		Math.abs(payParmBack.getDouble("PAY_OTHER4")));
        payParmBack.setData("PAY_TYPE04",
        		Math.abs(payParmBack.getDouble("PAY_TYPE04")));
        payParmBack.setData("PAY_TYPE08",
        		Math.abs(payParmBack.getDouble("PAY_TYPE08")));  //add by huangtt 20150519  ����ֱ��
        payParmBack.setData("PAY_TYPE09",
        		Math.abs(payParmBack.getDouble("PAY_TYPE09")));  //add by huangtt 20150519  ����ֱ��
        payParmBack.setData("PAY_TYPE10",
        		Math.abs(payParmBack.getDouble("PAY_TYPE10")));  //add by huangtt 20150519  ����ֱ��
        payParmBack.setData("PAY_BANK_CARD",
        		Math.abs(payParmBack.getDouble("PAY_BANK_CARD")));
        payParmBack.setData("PAY_INS_CARD",
        		Math.abs(payParmBack.getDouble("PAY_INS_CARD")));
        payParmBack.setData("PAY_CHECK",
        		Math.abs(payParmBack.getDouble("PAY_CHECK")));
        payParmBack.setData("PAY_DEBIT",
        		Math.abs(payParmBack.getDouble("PAY_DEBIT")));
        payParmBack.setData("PAY_BILPAY",
        		Math.abs(payParmBack.getDouble("PAY_BILPAY")));
        payParmBack.setData("PAY_OTHER1",
        		Math.abs(payParmBack.getDouble("PAY_OTHER1")));
        payParmBack.setData("PAY_OTHER2",
        		Math.abs(payParmBack.getDouble("PAY_OTHER2")));
        payParmBack.setData("PAY_INS",
        		Math.abs(payParmBack.getDouble("PAY_INS")));
        printParm.setData("PAY", payParm.getData());
        printParm.setData("PAYBACK", payParmBack.getData());
        printParm.setData("PAYTOT", payTot.getData());
        printParm.setData("CHARGE", chargeParm.getData());
//        System.out.println("�����Ʒ������printParm ���� "+printParm);
        return printParm;
    }
    
    /**
     * �������ϱ��ӡ����
     * ===zhangp 20120328
     * @param accountSeq String
     * @return TParm
     */
    private TParm getPrintCancelTableDate(String accountSeq) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm parmData = new TParm();
        String selMrNo =
            " SELECT INV_NO,AR_AMT FROM BIL_INVRCP " +
            "  WHERE RECP_TYPE = 'MEM' "+
            "    AND ACCOUNT_SEQ IN (" +accountSeq + ") "+
            "    AND CANCEL_FLG = '3' " +
            "    AND CANCEL_DATE < ACCOUNT_DATE";
        selMrNo += " AND LENGTH (INV_NO) < 12";//add by wanglong 20121112 ���˵�12λ�Ľ��л�����Ʊ�ݺ�
        parmData = new TParm(TJDODBTool.getInstance().select(
            selMrNo));
        int count = parmData.getCount("INV_NO");
        TParm aparm = new TParm();
        // ��������ʾ�㷨
        int row = 0;
        int column = 0;
        for (int i = 0; i < count; i++) {

            aparm.addData("INV_NO_" + column,
                          parmData.getData("INV_NO", i));
            aparm.addData("AR_AMT_" + column,
                          df.format(parmData.getDouble("AR_AMT", i)));
            column++;
            if (column == 2) {
                column = 0;
            }
        }
        if(count % 2 == 1){
            row = count / 2 + 1;
            aparm.addData("INV_NO_1", "");
            aparm.addData("AR_AMT_1", "");
        }else
            row = count/2;
        aparm.setCount(row);
        TParm printData = new TParm(); //��ӡ����
        printData.setCount(row);
        printData = aparm;
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_0");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_1");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
        return printData;
    }
    
    /**
     * �����˷Ѵ�ӡ����
     * ===zhangp 20120328
     * @param accountSeq String
     * @return TParm
     */
    private TParm getPrintReturnTableDate(String accountSeq) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm parmData = new TParm();
        String selMrNo =
            " SELECT B.INV_NO, B.AR_AMT" +
            " FROM BIL_MEM_RECP A, BIL_INVRCP B" +
            " WHERE A.PRINT_NO = B.INV_NO" +
            " AND B.RECP_TYPE = 'MEM'" +
            " AND A.ACCOUNT_SEQ IN (" +accountSeq + ")" +
            " AND B.CANCEL_FLG = '1'" +
            " AND RESET_RECEIPT_NO IS NULL" ;
        selMrNo += " AND LENGTH (INV_NO) < 12";//add by wanglong 20121112 ���˵�12λ�Ľ��л�����Ʊ�ݺ�
        parmData = new TParm(TJDODBTool.getInstance().select(
            selMrNo));
        int count = parmData.getCount("INV_NO");
        TParm aparm = new TParm();
        // ��������ʾ�㷨
        int row = 0;
        int column = 0;
        for (int i = 0; i < count; i++) {

            aparm.addData("INV_NO_" + column,
                          parmData.getData("INV_NO", i));
            aparm.addData("AR_AMT_" + column,
                          df.format(parmData.getDouble("AR_AMT", i)));
            column++;
            if (column == 2) {
                column = 0;
            }
        }
        if(count % 2 == 1){
            row = count / 2 + 1;
            aparm.addData("INV_NO_1", "");
            aparm.addData("AR_AMT_1", "");
        }else
            row = count/2;
        aparm.setCount(row);
        TParm printData = new TParm(); //��ӡ����
        printData.setCount(row);
        printData = aparm;
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_0");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_1");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
        return printData;
    }
    /**
     * �������Ʊ�Ŵ�ӡ����
     * ===zhangp 20120328
     * @param accountSeq String
     * @return TParm
     */
    private TParm getChangeTableDate(String accountSeq) {
        DecimalFormat df = new DecimalFormat("##########0.00");
        TParm parmData = new TParm();
        String selMrNo =
            " SELECT INV_NO,AR_AMT FROM BIL_INVRCP " +
            "  WHERE RECP_TYPE = 'MEM' "+
            "    AND ACCOUNT_SEQ IN (" +accountSeq + ") "+
            "    AND STATUS = '2' ";
        selMrNo += " AND LENGTH (INV_NO) < 12";//add by wanglong 20121112 ���˵�12λ�Ľ��л�����Ʊ�ݺ�
        parmData = new TParm(TJDODBTool.getInstance().select(
            selMrNo));
        int count = parmData.getCount("INV_NO");
        TParm aparm = new TParm();
        // ��������ʾ�㷨
        int row = 0;
        int column = 0;
        for (int i = 0; i < count; i++) {

            aparm.addData("INV_NO_" + column,
                          parmData.getData("INV_NO", i));
            aparm.addData("AR_AMT_" + column,
                          df.format(parmData.getDouble("AR_AMT", i)));
            column++;
            if (column == 2) {
                column = 0;
            }
        }
        if(count % 2 == 1){
            row = count / 2 + 1;
            aparm.addData("INV_NO_1", "");
            aparm.addData("AR_AMT_1", "");
        }else
            row = count/2;
        aparm.setCount(row);
        TParm printData = new TParm(); //��ӡ����
        printData.setCount(row);
        printData = aparm;
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_0");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_0");
        printData.addData("SYSTEM", "COLUMNS", "INV_NO_1");
        printData.addData("SYSTEM", "COLUMNS", "AR_AMT_1");
        return printData;
    }
    
    /**
     * �õ��˷�Ʊ��Ʊ��
     * @param accountSeq String
     * @return String
     */
    public  String getBackPrintNo(String accountSeq) {
        if (accountSeq == null || accountSeq.length() <= 0)
            return "";
        String sql =
                "SELECT PRINT_NO,AR_AMT FROM BIL_MEM_RECP " +
                " WHERE ACCOUNT_SEQ IN(" + accountSeq + ") " +
                "   AND AR_AMT < 0 " +
                //====zhangp 20120306 modify start
                " AND (PRINT_NO IS NOT NULL OR PRINT_NO <> '')";
        //====zhangp 20120306 modify end
        return sql;
    }
    
    /**
     * �õ��ս�Ʊ��
     * @param accountSeq String
     * @return String
     */
    public static String getReceiptParm(String accountSeq) {
        if (accountSeq == null || accountSeq.equals(""))
            return "";
        String sql =
                "SELECT TRADE_NO AS CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO," +
                "       RESET_RECEIPT_NO,PRINT_NO,BILL_DATE,CHARGE_DATE," +
                "       PRINT_DATE,CHARGE01,CHARGE02,CHARGE03,CHARGE04," +
                "       CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09," +
                "       CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14," +
                "       CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19," +
                "       CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24," +
                "       CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29," +
                "       CHARGE30,TOT_AMT,REDUCE_REASON,REDUCE_AMT," +
                "       REDUCE_DATE,REDUCE_DEPT_CODE,REDUCE_RESPOND," +
                "       AR_AMT,PAY_CASH,PAY_MEDICAL_CARD,PAY_BANK_CARD," +
                "       PAY_INS_CARD,PAY_CHECK,PAY_DEBIT,PAY_BILPAY,PAY_DRAFT, " +
                "       PAY_INS,PAY_OTHER1,PAY_OTHER2,PAY_OTHER3,PAY_OTHER4,PAY_REMARK," +
                "       CASHIER_CODE,OPT_USER,OPT_DATE,OPT_TERM ,PAY_TYPE01,PAY_TYPE02," +
                "       PAY_TYPE03,PAY_TYPE04,PAY_TYPE05,PAY_TYPE06,PAY_TYPE07,PAY_TYPE08," +
                "       PAY_TYPE09,PAY_TYPE10 " +
                "  FROM BIL_MEM_RECP " +
                " WHERE ACCOUNT_SEQ IN (" + accountSeq + ")";
        return sql;
    }

    
    /**
     * �õ��ս�Ʊ��
     * @param accountSeq String
     * @return TParm
     */
    public TParm getPrintNo(String accountSeq) {

    	String admType = getValueString("ADM_TYPE");
        TParm printParm = new TParm();
        TParm returnParm = new TParm();
        printParm.setData("ACCOUNT_SEQ", accountSeq);
        String[] accseqs = accountSeq.split("'");
        String accseq = "";
        for (int i = 0; i < accseqs.length; i++) {
        	accseq = accseq + accseqs[i];
		}
        returnParm.setData("ACCOUNT_SEQ", accseq);
        String printNosql = BILSQL.getPrintNo(accountSeq, "MEM",admType);
        TParm printNo = new TParm(TJDODBTool.getInstance().select(printNosql));
        if (printNo.getErrCode() < 0) {
           // System.out.println("��ӡƱ�Ų��Ҵ��� " + printNo.getErrText());
            return null;
        }

        String printUsersql = 
        	"SELECT DISTINCT B.USER_NAME" +
        	" FROM BIL_MEM_RECP A, SYS_OPERATOR B" +
        	" WHERE A.CASHIER_CODE = B.USER_ID AND A.ACCOUNT_SEQ IN (" + accountSeq + ")";
        if(!admType.equals("")&&admType!=null){
        	printUsersql += " AND A.ADM_TYPE = '" + admType + "'";
        }
        TParm printUser = new TParm(TJDODBTool.getInstance().select(
            printUsersql));
        if (printUser.getErrCode() < 0) {
           // System.out.println("�շ���Ա���Ҵ��� " + printUser.getErrText());
            return null;
        }
        String printNoS = dealTearPrint(printNo);

        String printUserS = "";
        for (int i = 0; i < printUser.getCount(); i++) {
        	printUserS += ","+printUser.getValue("USER_NAME", i);
		}
        printUserS = printUserS.substring(1, printUserS.length());
        printNo.setCount(0);
        returnParm.setData("PRINTNO", printNo.getData());

        String sqlBil =  "SELECT PRINT_NO,BILL_DATE,ACCOUNT_SEQ FROM BIL_MEM_RECP " +
        		" WHERE ADM_TYPE LIKE '%"+admType+"%' AND ACCOUNT_SEQ in ("+
        		accountSeq+") ORDER BY BILL_DATE ";

        TParm bilParm = new TParm(TJDODBTool.getInstance().select(sqlBil));
//        System.out.println("bilParm========"+bilParm);
        String recp_no = "";
        TParm recParm = getOPBparm(accountSeq);
        for (int i = 0; i < recParm.getCount("PRINT_USER"); i++) {
        	recp_no += recParm.getValue("INV_NOS",i) + ";";
		}
        returnParm.setData("PRINTNOS", recp_no);
        returnParm.setData("PRINTUSERS", printUserS);
        //�˷�Ʊ��

        String backPringNoSql = getBackPrintNo(accountSeq);
        TParm backPrintNo = new TParm(TJDODBTool.getInstance().select(
            backPringNoSql));
        if (backPrintNo.getErrCode() < 0) {
          //  System.out.println("�˷�Ʊ�Ų��Ҵ��� " + backPrintNo.getErrText());
            return null;
        }
        returnParm.setData("BACKPRINT", backPrintNo.getData());
        //����Ʊ��
//        TParm tearPrintNo = BILInvrcptTool.getInstance().getTearPrintNo(
//            printParm);
        String tearPrintNoSql = getTearPringNo(accountSeq,admType);
        TParm tearPrintNo = new TParm(TJDODBTool.getInstance().select(
            tearPrintNoSql));
        if (tearPrintNo.getErrCode() < 0) {
           // System.out.println("����Ʊ�Ų��Ҵ��� " + tearPrintNo.getErrText());
            return null;
        }
        String tearNo = dealTearPrint(tearPrintNo);
        returnParm.setData("TEARPRINTNO", getPrintNOs((Vector)tearPrintNo.getData("INV_NO")));
        //�õ��վ�parm
        String sql = getReceiptParm(accountSeq);
        if (sql == "" || sql.equals(""))
            return null;

        TParm receiptParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (receiptParm.getErrCode() < 0) {
            //System.out.println("�վݲ��Ҵ��� " + receiptParm.getErrText());
            return null;
        }
        //����Ʊ��parm
        TParm endReceiptParm = dealPrintParm(receiptParm);
        String arAmtWord = com.javahis.util.StringUtil.getInstance().numberToWord(endReceiptParm.getParm("PAYTOT").getDouble("AR_AMT"));
        returnParm.setData("AR_AMT_W", arAmtWord);
        returnParm.setData("RECEIPT", endReceiptParm.getData());
        TParm chargeparm = endReceiptParm.getParm("CHARGE");
        double charge1 = chargeparm.getDouble("CHARGE01");//������
        double charge2 = chargeparm.getDouble("CHARGE02");//�ǿ�����
        double charge3 = chargeparm.getDouble("CHARGE03");//�г�ҩ��
        double charge4 = chargeparm.getDouble("CHARGE04");//�в�ҩ��
        double charge5 = chargeparm.getDouble("CHARGE05");//����       
        double charge6 = chargeparm.getDouble("CHARGE06");//���Ʒ�        
        double charge7 = chargeparm.getDouble("CHARGE07");//�����
        double charge8 = chargeparm.getDouble("CHARGE08");//������
        double charge9 = chargeparm.getDouble("CHARGE09");//��Ѫ��
        double charge10 = chargeparm.getDouble("CHARGE10");//�����       
        double charge11 = chargeparm.getDouble("CHARGE11");//����
        double charge12 = chargeparm.getDouble("CHARGE12");//����ҽ��
        double charge13 = chargeparm.getDouble("CHARGE13");//�۲촲��
        double charge14 = chargeparm.getDouble("CHARGE14");//CT
        double charge15 = chargeparm.getDouble("CHARGE15");//MR
        double charge16 = chargeparm.getDouble("CHARGE16");//�ԷѲ���
        double charge17 = chargeparm.getDouble("CHARGE17");//���Ϸ�
        double charge18 = chargeparm.getDouble("CHARGE18");//������
        double charge19 = chargeparm.getDouble("CHARGE19");
        double charge1and2 = charge1+charge2;//��ҩ��
        double chargeAmt = 0.00;//�ܼ�
        for (int i = 1; i < 31; i++) {
        	if(i<10){
        		chargeAmt = chargeAmt +chargeparm.getDouble("CHARGE0"+i);
        		returnParm.setData("CHARGE0"+i, "TEXT", StringTool.round(chargeparm.getDouble("CHARGE0"+i), 2));
        	}else{
        		chargeAmt = chargeAmt +chargeparm.getDouble("CHARGE"+i);
        		returnParm.setData("CHARGE"+i, "TEXT", StringTool.round(chargeparm.getDouble("CHARGE"+i), 2));
        	}
		}
        returnParm.setData("CHARGE1AND2", "TEXT", StringTool.round(charge1and2,2));
        returnParm.setData("CHARGEAMT", "TEXT", StringTool.round(chargeAmt,2));
        TParm totParm = endReceiptParm.getParm("PAYTOT");
        TParm payParm = endReceiptParm.getParm("PAY");
        TParm backParm = endReceiptParm.getParm("PAYBACK");
        //��ʼʱ��
        String dispenseDate = getValueString("ACCOUNT_DATE").replace("-", "").
            replace(" ", "").replace(":", "");
        dispenseDate = dealDate(dispenseDate);
        String datesql = 
        	"SELECT ACCOUNT_DATE FROM BIL_INVRCP WHERE ACCOUNT_SEQ in ("+accountSeq+") ORDER BY ACCOUNT_DATE";
        TParm dateparm= new TParm(TJDODBTool.getInstance().select(datesql));
        String stardate = bilParm.getData("BILL_DATE", 0).toString();
        String enddate = bilParm.getData("BILL_DATE", bilParm.getCount()-1).toString();
        stardate = stardate.substring(0, 19);
        enddate = enddate.substring(0, 19);
        returnParm.setData("ACCOUNT_DATE", stardate+" �� "+enddate);
        String dispenseDateE = StringTool.getString((Timestamp)this.getValue("ACCOUNT_DATEE"),"yyyyMMdd")+this.getValueString("E_TIME").replace(":", "");
        dispenseDateE = dealDate(dispenseDateE);
        returnParm.setData("ACCOUNT_DATEE", dispenseDateE);
        //��ӡʱ��
        String apDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                             "yyyyMMddHHmmss");
        apDate = dealDate(apDate);

        returnParm.setData("PRINT_TIME", apDate);
        
        
        double payInsNhiS = 0;
        double payInsHelpS = 0;
        double unreimAmtY = 0;
        double unreimAmtT = 0;
        double unreimAmtS = 0;
        double payInsCardS = 0;
        double payInsS = 0;
        double payInsCardT = 0;
        double payInsT = 0;
        double payInsCardY = 0;
        double payInsY = 0;
        double payInsNhiT = 0;
        double payInsHelpT = 0;
		double payInsNhiY = 0;
		double payInsHelpY = 0;
		double payInsTotY = 0;
		double payInsTotT = 0;
		double payInsTotS = 0;

        unreimAmtS = unreimAmtY + unreimAmtT;
        payInsCardS = payInsCardY + payInsCardT ;
        payInsS = payInsY + payInsT;
        payInsHelpS = payInsHelpY + payInsHelpT;
        payInsNhiS = payInsNhiY + payInsNhiT;
//		ҽ�����С��= �����˻�+�籣����֧��+�������+ͳ��-����δ�������
        payInsTotY = payInsCardY + payInsY + payInsHelpY + payInsNhiY;
        payInsTotT = payInsCardT + payInsT + payInsHelpT + payInsNhiT;
        payInsTotS = payInsTotY + payInsTotT;
		returnParm.setData("PAY_INS_NHI_S", "TEXT", StringTool.round(payInsNhiS,2));
		returnParm.setData("PAY_INS_CARD_S", "TEXT", StringTool.round(payInsCardS,2));
        returnParm.setData("PAY_INS_HELP_S", "TEXT", StringTool.round(payInsHelpS,2));
        returnParm.setData("PAY_INS_S", "TEXT", StringTool.round(payInsS,2));
		returnParm.setData("PAY_UNREIM_AMT_Y", "TEXT", StringTool.round(unreimAmtY,2));
		returnParm.setData("PAY_UNREIM_AMT_T", "TEXT", Math.abs(StringTool.round(unreimAmtT,2)));
		returnParm.setData("PAY_UNREIM_AMT_S", "TEXT", StringTool.round(unreimAmtS,2));
		returnParm.setData("PAY_INS_TOT_Y", "TEXT", StringTool.round(payInsTotY,2));
		returnParm.setData("PAY_INS_TOT_T", "TEXT", Math.abs(StringTool.round(payInsTotT,2)));
		returnParm.setData("PAY_INS_TOT_S", "TEXT", StringTool.round(payInsTotS,2));
        TParm printCancleDate = this.getPrintCancelTableDate(accountSeq);
        TParm printReturnDate = this.getPrintReturnTableDate(accountSeq);
        TParm printChangeDate = this.getChangeTableDate(accountSeq);
		returnParm.setData("cancelTable", printCancleDate.getData());
		returnParm.setData("returnFeeTable", printReturnDate.getData());
		returnParm.setData("changeTable", printChangeDate.getData());
        String region = table.getParmValue().getRow(
                0).getValue("REGION_CHN_DESC");
        returnParm.setData("TITLE", "TEXT",
                           (this.getValue("REGION_CODE") != null &&
                            !this.getValue("REGION_CODE").equals("") ? region :
                            "����ҽԺ") + "�����ײ�����սᱨ��");
//        System.out.println("��ӡ111222returnParm returnParm is :"+returnParm);
        return returnParm;
    }
    
    /**
	 * �ײʹ�ӡ���ݲ�ѯ
	 * yanjing 20140430
	 */
	public TParm onPrintPackData(TParm inParm){
		TParm result = new TParm ();
		TParm receipteResult = new TParm ();
		String accountSeq = "'";		
		for(int i = 0;i<inParm.getCount();i++){
			if(i!= inParm.getCount() -1){
			accountSeq +=  inParm.getValue("ACCOUNT_SEQ",i)+"','";
			}else if (i == inParm.getCount() -1){
			accountSeq +=  inParm.getValue("ACCOUNT_SEQ",i)+"'";
			}
		}

		String orderSql = "SELECT SUM(B.TOT_AMT) AS AR_AMT,SUM(B.CHARGE01) AS CHARGE01,SUM(B.CHARGE02) AS CHARGE02,SUM(B.CHARGE03) AS CHARGE03," +
		"SUM(B.CHARGE04) AS CHARGE04,SUM(B.CHARGE05) AS CHARGE05,SUM(B.CHARGE06) AS CHARGE06,SUM(B.CHARGE07) AS CHARGE07,SUM(B.CHARGE08) AS CHARGE08," +
		"SUM(B.CHARGE09) AS CHARGE09,SUM(B.CHARGE10) AS CHARGE10,SUM(B.CHARGE11) AS CHARGE11,SUM(B.CHARGE12) AS CHARGE12,SUM(B.CHARGE13) AS CHARGE13," +
		"SUM(B.CHARGE14) AS CHARGE14,SUM(B.CHARGE15) AS CHARGE15,SUM(B.CHARGE16) AS CHARGE16,SUM(B.CHARGE17) AS CHARGE17,SUM(B.CHARGE18) AS CHARGE18," +
		"SUM(B.CHARGE19) AS CHARGE19,SUM(B.CHARGE20) AS CHARGE20,SUM(B.CHARGE21) AS CHARGE21,SUM(B.CHARGE22) AS CHARGE22,SUM(B.CHARGE23) AS CHARGE23," +
		"SUM(B.CHARGE24) AS CHARGE24,SUM(B.CHARGE25) AS CHARGE25,SUM(B.CHARGE26) AS CHARGE26,SUM(B.CHARGE27) AS CHARGE27,SUM(B.CHARGE28) AS CHARGE28," +
		"SUM(B.CHARGE29) AS CHARGE29,SUM(B.CHARGE30) AS CHARGE30, " +
		"SUM(B.PAY_TYPE09) AS PAY_TYPE09,SUM(B.PAY_TYPE09) AS PAY_TYPE10 " +
		" FROM BIL_MEM_RECP B " +
		" WHERE B.TOT_AMT >=0 AND B.MEM_PACK_FLG ='Y' " +
		" AND B.ACCOUNT_SEQ IN ("+accountSeq+")";
		
        result = new TParm(TJDODBTool.getInstance().select(orderSql));
		return result;
	}
	
	/**
	 * �ײʹ�ӡ���ݲ�ѯ�˿�
	 * yanjing 20140430
	 */
	public TParm onTrPrintPackData(TParm inParm){
		TParm result = new TParm ();
		TParm receipteResult = new TParm ();
		String accountSeq = "'";
		for(int i = 0;i<inParm.getCount();i++){
			if(i!= inParm.getCount() -1){
			accountSeq +=  inParm.getValue("ACCOUNT_SEQ",i)+"','";
			}else if (i == inParm.getCount() -1){
			accountSeq +=  inParm.getValue("ACCOUNT_SEQ",i)+"'";
			}
		}
		
		String orderSql = "SELECT ABS(SUM(B.TOT_AMT)) AS AR_AMT,SUM(B.CHARGE01) AS CHARGE01,SUM(B.CHARGE02) AS CHARGE02,SUM(B.CHARGE03) AS CHARGE03," +
		"SUM(B.CHARGE04) AS CHARGE04,SUM(B.CHARGE05) AS CHARGE05,SUM(B.CHARGE06) AS CHARGE06,SUM(B.CHARGE07) AS CHARGE07,SUM(B.CHARGE08) AS CHARGE08," +
		"SUM(B.CHARGE09) AS CHARGE09,SUM(B.CHARGE10) AS CHARGE10,SUM(B.CHARGE11) AS CHARGE11,SUM(B.CHARGE12) AS CHARGE12,SUM(B.CHARGE13) AS CHARGE13," +
		"SUM(B.CHARGE14) AS CHARGE14,SUM(B.CHARGE15) AS CHARGE15,SUM(B.CHARGE16) AS CHARGE16,SUM(B.CHARGE17) AS CHARGE17,SUM(B.CHARGE18) AS CHARGE18," +
		"SUM(B.CHARGE19) AS CHARGE19,SUM(B.CHARGE20) AS CHARGE20,SUM(B.CHARGE21) AS CHARGE21,SUM(B.CHARGE22) AS CHARGE22,SUM(B.CHARGE23) AS CHARGE23," +
		"SUM(B.CHARGE24) AS CHARGE24,SUM(B.CHARGE25) AS CHARGE25,SUM(B.CHARGE26) AS CHARGE26,SUM(B.CHARGE27) AS CHARGE27,SUM(B.CHARGE28) AS CHARGE28," +
		"SUM(B.CHARGE29) AS CHARGE29,SUM(B.CHARGE30) AS CHARGE30," +
		"SUM(B.PAY_TYPE09) AS PAY_TYPE09,SUM(B.PAY_TYPE10) AS PAY_TYPE10 " +// add by kangy-20160628------���΢��pay_type09��֧��������pay_type10--- " +
		" FROM BIL_MEM_RECP B " +
		" WHERE B.TOT_AMT < 0 AND B.MEM_PACK_FLG ='Y' " +
		" AND B.ACCOUNT_SEQ IN ("+accountSeq+")";

        result = new TParm(TJDODBTool.getInstance().select(orderSql));
		return result;
	}
    
	/**
	 * �ײʹ�ӡ���ݲ�ѯ��
	 * yanjing 20140430
	 */
	public TParm onAllPrintPackData(TParm inParm){
		TParm result = new TParm ();
		TParm receipteResult = new TParm ();
		String accountSeq = "'";
		for(int i = 0;i<inParm.getCount();i++){
			if(i!= inParm.getCount() -1){
			accountSeq +=  inParm.getValue("ACCOUNT_SEQ",i)+"','";
			}else if (i == inParm.getCount() -1){
			accountSeq +=  inParm.getValue("ACCOUNT_SEQ",i)+"'";
			}
		}
		
		String orderSql = "SELECT SUM(B.TOT_AMT) AS AR_AMT,SUM(B.CHARGE01) AS CHARGE01,SUM(B.CHARGE02) AS CHARGE02,SUM(B.CHARGE03) AS CHARGE03," +
		"SUM(B.CHARGE04) AS CHARGE04,SUM(B.CHARGE05) AS CHARGE05,SUM(B.CHARGE06) AS CHARGE06,SUM(B.CHARGE07) AS CHARGE07,SUM(B.CHARGE08) AS CHARGE08," +
		"SUM(B.CHARGE09) AS CHARGE09,SUM(B.CHARGE10) AS CHARGE10,SUM(B.CHARGE11) AS CHARGE11,SUM(B.CHARGE12) AS CHARGE12,SUM(B.CHARGE13) AS CHARGE13," +
		"SUM(B.CHARGE14) AS CHARGE14,SUM(B.CHARGE15) AS CHARGE15,SUM(B.CHARGE16) AS CHARGE16,SUM(B.CHARGE17) AS CHARGE17,SUM(B.CHARGE18) AS CHARGE18," +
		"SUM(B.CHARGE19) AS CHARGE19,SUM(B.CHARGE20) AS CHARGE20,SUM(B.CHARGE21) AS CHARGE21,SUM(B.CHARGE22) AS CHARGE22,SUM(B.CHARGE23) AS CHARGE23," +
		"SUM(B.CHARGE24) AS CHARGE24,SUM(B.CHARGE25) AS CHARGE25,SUM(B.CHARGE26) AS CHARGE26,SUM(B.CHARGE27) AS CHARGE27,SUM(B.CHARGE28) AS CHARGE28," +
		"SUM(B.CHARGE29) AS CHARGE29,SUM(B.CHARGE30) AS CHARGE30," +
		"SUM(B.PAY_TYPE09) AS PAY_TYPE09,SUM(B.PAY_TYPE10) AS PAY_TYPE10 " +// add by kangy-20160628------���΢��pay_type09��֧��������pay_type10---
		" FROM BIL_MEM_RECP B" +
		" WHERE B.MEM_PACK_FLG ='Y' " +
		" AND B.ACCOUNT_SEQ IN ("+accountSeq+")";
		
//		System.out.println("ȫ��==============orderSql orderSql is ::"+orderSql);
       result = new TParm(TJDODBTool.getInstance().select(orderSql));
		return result;
	}
	
	/**
//	 * �ײʹ�ӡ�����˷�����
//	 * yanjing 20140430
//	 */
	public TParm onTrPrintPackQty(TParm inParm){
		TParm result = new TParm ();
		TParm receipteResult = new TParm ();
		String accountSeq = "'";
		for(int i = 0;i<inParm.getCount();i++){
			if(i!= inParm.getCount() -1){
			accountSeq +=  inParm.getValue("ACCOUNT_SEQ",i)+"','";
			}else if (i == inParm.getCount() -1){
			accountSeq +=  inParm.getValue("ACCOUNT_SEQ",i)+"'";
			}
		}
		
		String orderSql = "SELECT * FROM BIL_MEM_RECP B  " +
		"WHERE B.MEM_PACK_FLG ='Y'  AND B.TOT_AMT < 0  " +
		"AND B.ACCOUNT_SEQ IN ("+accountSeq+")";
		
//		System.out.println("��Ʊ==============orderSql orderSql is ::"+orderSql);
       result = new TParm(TJDODBTool.getInstance().select(orderSql));
		return result;
	}
    
    /**
     * ��ӡ�սᱨ��
     */
    public void onPrint() {
        //�õ�table�ϵ�����
        table.acceptText();
        TParm tableParm = table.getParmValue();
        DecimalFormat df = new DecimalFormat("0.00");
       // System.out.println("tableParm"+tableParm);
        //��˴�ӡ����
        if (!checkPrintData(tableParm))
            return;
        int count = tableParm.getCount("ACCOUNT_SEQ");
        TParm endParm  = new TParm();
        String seq = "";
        int rowCount = 0;
        for (int i = 0; i < count; i++) {
            seq = tableParm.getValue("S", i);
            if (seq.equals("Y")) {
                endParm.addData("S",tableParm.getData("S",i));
                endParm.addData("INVALID_COUNT",tableParm.getData("INVALID_COUNT",i));
                endParm.addData("ACCOUNT_DATE",tableParm.getData("ACCOUNT_DATE",i));
                endParm.addData("ACCOUNT_SEQ",tableParm.getData("ACCOUNT_SEQ",i));
                endParm.addData("STATUS",tableParm.getData("STATUS",i));
                endParm.addData("ACCOUNT_USER",tableParm.getData("ACCOUNT_USER",i));
                endParm.addData("AR_AMT",tableParm.getData("AR_AMT",i));
                rowCount = rowCount+1;
            }
        }
        endParm.setCount(rowCount);
        TParm printData = new TParm();
        TParm singleParm = new TParm();
        if ("N".equals(getValue("TOGEDER_FLG"))) {
            for (int i = 0; i < rowCount; i++) {
                singleParm = new TParm();
                singleParm.addData("S",endParm.getData("S",i));
                singleParm.addData("INVALID_COUNT",endParm.getData("INVALID_COUNT",i));
                singleParm.addData("ACCOUNT_DATE",endParm.getData("ACCOUNT_DATE",i));
                singleParm.addData("ACCOUNT_SEQ",endParm.getData("ACCOUNT_SEQ",i));
                singleParm.addData("STATUS",endParm.getData("STATUS",i));
                singleParm.addData("ACCOUNT_USER",endParm.getData("ACCOUNT_USER",i));
                singleParm.addData("AR_AMT",endParm.getData("AR_AMT",i));
                singleParm.setCount(1);
                printData = getAccountData(singleParm);
                printData.setData("accountDate1","�ս�����:"+endParm.getData("ACCOUNT_DATE",i).toString().substring(0, 19));//�ս����� 
//                System.out.println("ppppppooooooprintData printData is ::"+printData);
                //======��ӻ�Ա���ײ��ս����� start
               TParm packParm = this.onPrintPackData(singleParm);//����
               TParm packTrParm = this.onTrPrintPackData(singleParm);//�˿�
               TParm packAllParm = this.onAllPrintPackData(singleParm);//�ܼ�
               TParm packQty = this.onTrPrintPackQty(singleParm);//�˷�Ʊ������
//               System.out.println("+++++packAllParm packAllParm is ::"+packAllParm);
//               System.out.println("+++++packAllParm.getCount() packAllParm.getCount() is ::"+packAllParm.getCount());
               if(packAllParm.getCount()>0){//�ܼƴ���ֵ�������ӡ����
            	   if(packQty.getCount()>0){//���˷�
            		   printData.setData("TR_QTY",packQty.getCount());//�˷�����
            	   }else{
            		   printData.setData("TR_QTY",0);//�˷�����
            	   }
            	   printData.setData("AR_AMT1", df.format(packParm.getDouble("AR_AMT",0)));//�ײ�������
            	   printData.setData("TAR_AMT1", df.format(packTrParm.getDouble("AR_AMT",0)));//�ײ����˷�
            	   printData.setData("AR_AMT_TOT1", df.format(packAllParm.getDouble("AR_AMT",0)));//�������˷�֮��
            	   printData.setData("PCHARGE01", df.format(packAllParm.getDouble("CHARGE01",0)));
            	   printData.setData("PCHARGE02", df.format(packAllParm.getDouble("CHARGE02",0)));
            	   printData.setData("PCHARGE1AND2", df.format(packAllParm.getDouble("CHARGE02",0)+packAllParm.getDouble("CHARGE01",0)));
            	   printData.setData("PCHARGE03", df.format(packAllParm.getDouble("CHARGE03",0)));
            	   printData.setData("PCHARGE04", df.format(packAllParm.getDouble("CHARGE04",0)));
            	   printData.setData("PCHARGE05", df.format(packAllParm.getDouble("CHARGE05",0)));
            	   printData.setData("PCHARGE06", df.format(packAllParm.getDouble("CHARGE06",0)));
            	   printData.setData("PCHARGE07", df.format(packAllParm.getDouble("CHARGE07",0)));
            	   printData.setData("PCHARGE08", df.format(packAllParm.getDouble("CHARGE08",0)));
            	   printData.setData("PCHARGE09", df.format(packAllParm.getDouble("CHARGE09",0)));
            	   printData.setData("PCHARGE10", df.format(packAllParm.getDouble("CHARGE10",0)));
            	   printData.setData("PCHARGE11", df.format(packAllParm.getDouble("CHARGE11",0)));
            	   printData.setData("PCHARGE12", df.format(packAllParm.getDouble("CHARGE12",0)));
            	   printData.setData("PCHARGE13", df.format(packAllParm.getDouble("CHARGE13",0)));
            	   printData.setData("PCHARGE14", df.format(packAllParm.getDouble("CHARGE14",0)));
            	   printData.setData("PCHARGE15", df.format(packAllParm.getDouble("CHARGE15",0)));
            	   printData.setData("PCHARGE16", df.format(packAllParm.getDouble("CHARGE16",0)));
            	   printData.setData("PCHARGE17", df.format(packAllParm.getDouble("CHARGE17",0)));
            	   printData.setData("PCHARGE18", df.format(packAllParm.getDouble("CHARGE18",0)));
            	   printData.setData("PCHARGE19", df.format(packAllParm.getDouble("CHARGE19",0)));
            	   printData.setData("PCHARGE20", df.format(packAllParm.getDouble("CHARGE20",0)));
            	   printData.setData("PCHARGE21", df.format(packAllParm.getDouble("CHARGE21",0)));
            	   printData.setData("PCHARGE22", df.format(packAllParm.getDouble("CHARGE22",0)));
            	   printData.setData("PCHARGE23", df.format(packAllParm.getDouble("CHARGE23",0)));
            	   printData.setData("PPAY_TYPE09", df.format(packAllParm.getDouble("PAY_TYPE09",0)));
            	   printData.setData("PPAY_TYPE10", df.format(packAllParm.getDouble("PAY_TYPE10",0)));
 
               }
                printData.setData("USER","TEXT",Operator.getName());//
 
                //add by huangtt 20141223  start----------�շ���Ŀ��ȥ�ײ��ѽ�ת�շ���Ŀ��Ǯ��������������������
                TParm receiptParm = printData.getParm("RECEIPT");
                TParm chargeParm = receiptParm.getParm("CHARGE");
                //�շ���Ŀ
                chargeParm.setData("CHARGE01", chargeParm.getDouble("CHARGE01") - printData.getDouble("PCHARGE01"));
                chargeParm.setData("CHARGE02", chargeParm.getDouble("CHARGE02") - printData.getDouble("PCHARGE02"));
                chargeParm.setData("CHARGE1AND2", chargeParm.getDouble("CHARGE1AND2") - printData.getDouble("PCHARGE1AND2") );
                chargeParm.setData("CHARGE03", chargeParm.getDouble("CHARGE03") - printData.getDouble("PCHARGE03"));
                chargeParm.setData("CHARGE04", chargeParm.getDouble("CHARGE04") - printData.getDouble("PCHARGE04"));
                chargeParm.setData("CHARGE05", chargeParm.getDouble("CHARGE05") - printData.getDouble("PCHARGE05"));
                chargeParm.setData("CHARGE06", chargeParm.getDouble("CHARGE06") - printData.getDouble("PCHARGE06"));
                chargeParm.setData("CHARGE07", chargeParm.getDouble("CHARGE07") - printData.getDouble("PCHARGE07"));
                chargeParm.setData("CHARGE08", chargeParm.getDouble("CHARGE08") - printData.getDouble("PCHARGE08"));
                chargeParm.setData("CHARGE09", chargeParm.getDouble("CHARGE09") - printData.getDouble("PCHARGE09"));
                chargeParm.setData("CHARGE10", chargeParm.getDouble("CHARGE10") - printData.getDouble("PCHARGE10"));
                chargeParm.setData("CHARGE11", chargeParm.getDouble("CHARGE11") - printData.getDouble("PCHARGE11"));
                chargeParm.setData("CHARGE12", chargeParm.getDouble("CHARGE12") - printData.getDouble("PCHARGE12"));
                chargeParm.setData("CHARGE13", chargeParm.getDouble("CHARGE13") - printData.getDouble("PCHARGE13"));
                chargeParm.setData("CHARGE14", chargeParm.getDouble("CHARGE14") - printData.getDouble("PCHARGE14"));
                chargeParm.setData("CHARGE15", chargeParm.getDouble("CHARGE15") - printData.getDouble("PCHARGE15"));
                chargeParm.setData("CHARGE16", chargeParm.getDouble("CHARGE16") - printData.getDouble("PCHARGE16"));
                chargeParm.setData("CHARGE17", chargeParm.getDouble("CHARGE17") - printData.getDouble("PCHARGE17"));
                chargeParm.setData("CHARGE18", chargeParm.getDouble("CHARGE18") - printData.getDouble("PCHARGE18"));
                chargeParm.setData("CHARGE19", chargeParm.getDouble("CHARGE19") - printData.getDouble("PCHARGE19"));
                chargeParm.setData("CHARGE20", chargeParm.getDouble("CHARGE20") - printData.getDouble("PCHARGE20"));
                chargeParm.setData("CHARGE21", chargeParm.getDouble("CHARGE21") - printData.getDouble("PCHARGE21"));
                chargeParm.setData("CHARGE22", chargeParm.getDouble("CHARGE22") - printData.getDouble("PCHARGE22"));
                chargeParm.setData("CHARGE23", chargeParm.getDouble("CHARGE23") - printData.getDouble("PCHARGE23"));
                //add by kangy 20160627  start----------���΢�š�֧�������룭������������������ 
                chargeParm.setData("PAY_TYPE09", chargeParm.getDouble("PAY_TYPE09") - printData.getDouble("PPAY_TYPE09"));
                chargeParm.setData("PAY_TYPE10", chargeParm.getDouble("PAY_TYPE10") - printData.getDouble("PPAY_TYPE10"));
                //add by kangy 20160627  end----------���΢�š�֧�������룭������������������ 
                receiptParm.setData("CHARGE", chargeParm.getData());
                printData.setData("RECEIPT", receiptParm.getData());
              //add by huangtt 20141223  end----------�շ���Ŀ��ȥ�ײ��ѽ�ת�շ���Ŀ��Ǯ�������������������� 
                this.openPrintWindow(
                    "%ROOT%\\config\\prt\\opb\\OPBReceiptPrint.jhw", printData);
              

            }

        }
        else {
            printData = getAccountData(endParm);
            TParm packParm = this.onPrintPackData(endParm);//yanjing 20140505����
            TParm packTrParm = this.onTrPrintPackData(endParm);//yanjing 20140505�˿�
            TParm packAllParm = this.onAllPrintPackData(endParm);//yanjing 20140505�ܼ�
            TParm packQty = this.onTrPrintPackQty(endParm);//�˷�Ʊ������
//            System.out.println("222+++++packAllParm packAllParm is ::"+packAllParm);
//            System.out.println("222+++++packAllParm.getCount() packAllParm.getCount() is ::"+packAllParm.getCount());
            if(packAllParm.getCount()>0){//�ܼƴ���ֵ�������ӡ����
         	   if(packQty.getCount()>0){//���˷�
         		   printData.setData("TR_QTY",packQty.getCount());//�˷�����
         	   }else{
         		   printData.setData("TR_QTY",0);//�˷�����
         	   }
         	   printData.setData("AR_AMT1", df.format(packParm.getDouble("AR_AMT",0)));//�ײ�������
         	   printData.setData("TAR_AMT1", df.format(packTrParm.getDouble("AR_AMT",0)));//�ײ����˷�
         	   printData.setData("AR_AMT_TOT1", df.format(packAllParm.getDouble("AR_AMT",0)));//�������˷�֮��
         	   printData.setData("PCHARGE01", df.format(packAllParm.getDouble("CHARGE01",0)));
         	   printData.setData("PCHARGE02", df.format(packAllParm.getDouble("CHARGE02",0)));
         	   printData.setData("PCHARGE1AND2", df.format(packAllParm.getDouble("CHARGE02",0)+packAllParm.getDouble("CHARGE01",0)));
         	   printData.setData("PCHARGE03", df.format(packAllParm.getDouble("CHARGE03",0)));
         	   printData.setData("PCHARGE04", df.format(packAllParm.getDouble("CHARGE04",0)));
         	   printData.setData("PCHARGE05", df.format(packAllParm.getDouble("CHARGE05",0)));
         	   printData.setData("PCHARGE06", df.format(packAllParm.getDouble("CHARGE06",0)));
         	   printData.setData("PCHARGE07", df.format(packAllParm.getDouble("CHARGE07",0)));
         	   printData.setData("PCHARGE08", df.format(packAllParm.getDouble("CHARGE08",0)));
         	   printData.setData("PCHARGE09", df.format(packAllParm.getDouble("CHARGE09",0)));
         	   printData.setData("PCHARGE10", df.format(packAllParm.getDouble("CHARGE10",0)));
         	   printData.setData("PCHARGE11", df.format(packAllParm.getDouble("CHARGE11",0)));
         	   printData.setData("PCHARGE12", df.format(packAllParm.getDouble("CHARGE12",0)));
         	   printData.setData("PCHARGE13", df.format(packAllParm.getDouble("CHARGE13",0)));
         	   printData.setData("PCHARGE14", df.format(packAllParm.getDouble("CHARGE14",0)));
         	   printData.setData("PCHARGE15", df.format(packAllParm.getDouble("CHARGE15",0)));
         	   printData.setData("PCHARGE16", df.format(packAllParm.getDouble("CHARGE16",0)));
         	   printData.setData("PCHARGE17", df.format(packAllParm.getDouble("CHARGE17",0)));
         	   printData.setData("PCHARGE18", df.format(packAllParm.getDouble("CHARGE18",0)));
         	   printData.setData("PCHARGE19", df.format(packAllParm.getDouble("CHARGE19",0)));
         	   printData.setData("PCHARGE20", df.format(packAllParm.getDouble("CHARGE20",0)));
         	   printData.setData("PCHARGE21", df.format(packAllParm.getDouble("CHARGE21",0)));
         	   printData.setData("PCHARGE22", df.format(packAllParm.getDouble("CHARGE22",0)));
         	   printData.setData("PCHARGE23", df.format(packAllParm.getDouble("CHARGE23",0)));
         	//add by kangy 20160627  start----------���΢�š�֧�������룭������������������ 
         	  printData.setData("PPAY_TYPE09", df.format(packAllParm.getDouble("PAY_TYPE09",0)));
         	 printData.setData("PPAY_TYPE10", df.format(packAllParm.getDouble("PAY_TYPE10",0)));
               //add by kangy 20160627  end----------���΢�š�֧�������룭������������������         	   
            }
            printData.setData("USER","TEXT",Operator.getName());//�޸� ����շ�Աǩ�� 20130722 caoyong
            
          //add by huangtt 20141223  start----------�շ���Ŀ��ȥ�ײ��ѽ�ת�շ���Ŀ��Ǯ��������������������
            TParm receiptParm = printData.getParm("RECEIPT");
            TParm chargeParm = receiptParm.getParm("CHARGE");
            //�շ���Ŀ
            chargeParm.setData("CHARGE01", chargeParm.getDouble("CHARGE01") - printData.getDouble("PCHARGE01"));
            chargeParm.setData("CHARGE02", chargeParm.getDouble("CHARGE02") - printData.getDouble("PCHARGE02"));
            chargeParm.setData("CHARGE1AND2", chargeParm.getDouble("CHARGE1AND2") - printData.getDouble("PCHARGE1AND2") );
            chargeParm.setData("CHARGE03", chargeParm.getDouble("CHARGE03") - printData.getDouble("PCHARGE03"));
            chargeParm.setData("CHARGE04", chargeParm.getDouble("CHARGE04") - printData.getDouble("PCHARGE04"));
            chargeParm.setData("CHARGE05", chargeParm.getDouble("CHARGE05") - printData.getDouble("PCHARGE05"));
            chargeParm.setData("CHARGE06", chargeParm.getDouble("CHARGE06") - printData.getDouble("PCHARGE06"));
            chargeParm.setData("CHARGE07", chargeParm.getDouble("CHARGE07") - printData.getDouble("PCHARGE07"));
            chargeParm.setData("CHARGE08", chargeParm.getDouble("CHARGE08") - printData.getDouble("PCHARGE08"));
            chargeParm.setData("CHARGE09", chargeParm.getDouble("CHARGE09") - printData.getDouble("PCHARGE09"));
            chargeParm.setData("CHARGE10", chargeParm.getDouble("CHARGE10") - printData.getDouble("PCHARGE10"));
            chargeParm.setData("CHARGE11", chargeParm.getDouble("CHARGE11") - printData.getDouble("PCHARGE11"));
            chargeParm.setData("CHARGE12", chargeParm.getDouble("CHARGE12") - printData.getDouble("PCHARGE12"));
            chargeParm.setData("CHARGE13", chargeParm.getDouble("CHARGE13") - printData.getDouble("PCHARGE13"));
            chargeParm.setData("CHARGE14", chargeParm.getDouble("CHARGE14") - printData.getDouble("PCHARGE14"));
            chargeParm.setData("CHARGE15", chargeParm.getDouble("CHARGE15") - printData.getDouble("PCHARGE15"));
            chargeParm.setData("CHARGE16", chargeParm.getDouble("CHARGE16") - printData.getDouble("PCHARGE16"));
            chargeParm.setData("CHARGE17", chargeParm.getDouble("CHARGE17") - printData.getDouble("PCHARGE17"));
            chargeParm.setData("CHARGE18", chargeParm.getDouble("CHARGE18") - printData.getDouble("PCHARGE18"));
            chargeParm.setData("CHARGE19", chargeParm.getDouble("CHARGE19") - printData.getDouble("PCHARGE19"));
            chargeParm.setData("CHARGE20", chargeParm.getDouble("CHARGE20") - printData.getDouble("PCHARGE20"));
            chargeParm.setData("CHARGE21", chargeParm.getDouble("CHARGE21") - printData.getDouble("PCHARGE21"));
            chargeParm.setData("CHARGE22", chargeParm.getDouble("CHARGE22") - printData.getDouble("PCHARGE22"));
            chargeParm.setData("CHARGE23", chargeParm.getDouble("CHARGE23") - printData.getDouble("PCHARGE23"));
          //add by kangy 20160627  start----------���΢�š�֧�������룭������������������ 
            chargeParm.setData("PAY_TYPE09", chargeParm.getDouble("PAY_TYPE09") - printData.getDouble("PPAY_TYPE09"));
            chargeParm.setData("PAY_TYPE10", chargeParm.getDouble("PAY_TYPE10") - printData.getDouble("PPAY_TYPE10"));
            //add by kangy 20160627  end----------���΢�š�֧�������룭������������������ 
            receiptParm.setData("CHARGE", chargeParm.getData());
            printData.setData("RECEIPT", receiptParm.getData());
          //add by huangtt 20141223  end----------�շ���Ŀ��ȥ�ײ��ѽ�ת�շ���Ŀ��Ǯ�������������������� 
            
            
            
            if (printData == null)
                return;
            this.openPrintWindow(
                "%ROOT%\\config\\prt\\opb\\OPBReceiptPrint.jhw", printData);
//            this.openPrintWindow(
//                    "%ROOT%\\config\\prt\\opb\\aaa.jhw", printData);

        }
    
    }
    
    /**
     * ���
     */
    public void onClear() {
        onInit();
        this.callFunction("UI|TABLE|removeRowAll");

    }
    /**
     * ADM_TYPE������
     * =====zhangp 20120306
     */
    public void onAdmTypeClick(){
    	admType = getValueString("ADM_TYPE");
    }
    
    
    /**
     * �ս�
     * ===zhangp 20120315
     */
    public void onUpdate(){
    	
    		update("M");
    		
    }
    
    /**
     * �ս᷽��
     * @return boolean
     */
    public boolean update(String admType) {
    	getAccountTime();//====================�õ��ս�ʱ���  yanjing 20140618
        //ȡ���ս���Ա
        String casherUser = getValueString("ACCOUNT_USER");
        String regionCode=Operator.getRegion();
        //ȫ�������ս���Ա
        TParm casherParm;
        //���ûѡ�ս���Ա��ʾȫ��һ���ս�
        if (casherUser == null || casherUser.length() == 0) {
            //�õ�ȫ�������ս���Ա
            casherParm = getAccountUser(regionCode);
            //ȡ����Ա�ĸ���
            int row = casherParm.getCount();
            //ѭ�����е��շ�Ա
            //======zhangp 20120302 modify start
            List<String> successCashier = new ArrayList<String>();
            List<String>  faileCashier = new ArrayList<String>();
            for (int i = 0; i < row; i++) {
                //ȡ��һ���շ���Ա
                casherUser = casherParm.getValue("USER_ID", i);
                //����һ���˵��ս����
                if (!accountOneCasher(casherUser,admType)) {
                	faileCashier.add(casherParm.getValue("USER_NAME", i));
                    continue;
                }
                successCashier.add(casherParm.getValue("USER_NAME", i));
            }
            String successCashiers = "";
            String faileCashiers = "";
            if(successCashier.size()>0){
            	for (int i = 0; i < successCashier.size(); i++) {
            		if(i%10==0){
            			successCashiers = successCashiers+"," + successCashier.get(i) + "\n";
            		}else{
            			successCashiers = successCashiers+"," + successCashier.get(i);
            		}
				}
            }
            if(faileCashier.size()>0){
            	for (int i = 0; i < faileCashier.size(); i++) {
            		if(i%10==0){
            			faileCashiers = faileCashiers+"," + faileCashier.get(i) + "\n";
            		}else{
            			faileCashiers = faileCashiers+"," + faileCashier.get(i);
            		}
				}
            }
            if(!faileCashiers.equals("")){
            	messageBox(faileCashiers+"\n���ս�����!");
            }
            if(!successCashiers.equals("")){
            	messageBox(successCashiers+"\n�ս�ɹ�!");
            }
            //====zhangp 20120302 modify end
            return true;
        }
        //�����ѡ���ս���Ա,,���õ����ս����
        if (!accountOneCasher(casherUser,admType)) {
            messageBox("���ս�����!");
            return false;
        }
       else
          messageBox("�ս�ɹ�!");
        return true;
    }
    
    /**
     * �õ��ս���Ա��
     * @return String[]
     * 
     */
    public TParm getAccountUser(String regionCode) {
         String region = "";
         if (!"".equals(regionCode) && null != regionCode)
             region = " AND region_code = '" + regionCode + "' ";
         String sql =
                 "SELECT user_id AS USER_ID, user_name AS USER_NAME, user_eng_name AS enname, py1, py2" +
                 "  FROM sys_operator "
                 + " WHERE pos_code IN (SELECT pos_code"
                 + " FROM sys_position"
                 + " WHERE pos_type = '5') " + region +
                 "ORDER BY user_id";
         TParm accountUser = new TParm(TJDODBTool.getInstance().select(sql));
        if (accountUser.getErrCode() < 0)
            System.out.println(" ȡ���շ�Ա " + accountUser.getErrText());
        return accountUser;
    }
    
    /**
     * �ս�һ���շ�Ա������
     * @param casherUser String
     * @return boolean
     */
    public boolean accountOneCasher(String casherUser,String admType) {
        if (!checkCasherAccountData(casherUser,admType)) {
            return false;
        }
        TParm accountParm = new TParm();
        accountParm.setData("ACCOUNT", getAccountParm(casherUser,admType).getData());
        //���ñ�������
        TParm result = TIOM_AppServer.executeAction("action.mem.MEMPackageSectionAction",
            "onSaveAcctionMEM", accountParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }
    
    /**
     * ����շ�Ա�ս�����
     * @param casherUser String
     * @return boolean
     */
    public boolean checkCasherAccountData(String casherUser,String admType) {
        //����ս�Ʊ����
        TParm parm = new TParm();
        parm.setData("CASHIER_CODE", casherUser);
        parm.setData("RECP_TYPE", recpType);
        parm.setData("PRINT_DATE", accountTime);
        parm.setData("ADM_TYPE", admType);
        parm = MEMReceiptTool.getInstance().getMemRecpCount(parm);

        if (parm.getErrCode() < 0) {
            return false;
        }

        if (parm.getInt("COUNT", 0) <= 0)
            return false;
         this.messageBox("�ս�Ʊ��"+parm.getInt("COUNT",0));

        return true;
    }
    
    /**
     * �õ��ս�����
     * @param casherUser String
     * @return TParm
     */
    public TParm getAccountParm(String casherUser,String admType) {
        TParm accountParm = new TParm();
        accountParm.setData("ADM_TYPE", admType);
        accountParm.setData("RECP_TYPE", recpType);
        accountParm.setData("ACCOUNT_USER", casherUser);
        accountParm.setData("OPT_USER", Operator.getID());
        accountParm.setData("OPT_TERM", Operator.getIP());
        accountParm.setData("ACCOUNT_TYPE", accountType);
        accountParm.setData("AR_AMT", getArAmt(casherUser,admType));
        accountParm.setData("STATUS", "0");
        accountParm.setData("INVALID_COUNT", getInvalidCount(casherUser,admType));
        accountParm.setData("ACCOUNT_DATA", accountTime);
        accountParm.setData("REGION_CODE", Operator.getRegion());
        return accountParm;
    }
    
    /**
     * �õ��ս���
     * @param casherUser String
     * @return double
     */
    public double getArAmt(String casherUser,String admType) {
        TParm parm = new TParm();
        parm.setData("CASHIER_CODE", casherUser);
        parm.setData("ADM_TYPE", admType);
        parm.setData("BILL_DATE", accountTime);
        parm = MEMReceiptTool.getInstance().getSumAramt(parm);

        if (parm.getErrCode() < 0) {
            System.out.println("" + parm.getErrText());
            return 0.00;
        }
        return parm.getDouble("AR_AMT", 0);
    }
    
    /**
     * �õ���������(�˷�+����Ʊ��+��ӡ)
     * @param casherUser String
     * @return int
     */
    public int getInvalidCount(String casherUser,String admType) {
        TParm parm = new TParm();
        parm.setData("CASHIER_CODE", casherUser);
        parm.setData("RECP_TYPE", recpType);
        parm.setData("PRINT_DATE", accountTime);
        parm.setData("ADM_TYPE", admType);
        //===zhangp 20120312 modify start
        String backPringNoSql = "";
        backPringNoSql = MEMReceiptTool.getInstance().getBackPrintNo(parm);
        parm = OPBReceiptTool.getInstance().getOpbResetCount(parm);
        if (parm.getErrCode() < 0) {
            return 0;
        }
        int printNo = 0;
        if(parm !=null&&parm.getInt("COUNT", 0)>=0)
            printNo += parm.getInt("COUNT", 0);
       // System.out.println("�õ��˷�����parm"+parm);
        return printNo;
    }

    /**
     * ����ʱ��
     * @param date String
     * @return String
     */
    public String dealDate(String date) {
        String outDate = "";
        if (date == null || date.length() == 0)
            return "";
        if (date.length() >= 8)
            outDate += date.substring(0, 4) + " �� " + date.substring(4, 6) +
                " �� " +
                date.substring(6, 8) + " �� ";
        if (date.length() >= 14)
            outDate += " " + date.substring(8, 10) + " ʱ " +
                date.substring(10, 12) + " �� " + date.substring(12, 14) + " ��";
        return outDate;
    }

}

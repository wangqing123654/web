package com.javahis.ui.hrm;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jdo.bil.BIL;
import jdo.bil.BILSQL;
import jdo.bil.BILSysParmTool;
import jdo.opb.OPBReceiptTool;
import jdo.sys.Operator;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

public class HRMAccountDailyControl  extends TControl {
    TTable table;
    String accountType = "OPB";
    String admType = "H";
    String recpType = "OPB";
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
        setValue("ADM_TYPE", "H");
        ((TComboBox)getComponent("ADM_TYPE")).setEnabled(false);
        table = (TTable)this.getComponent("TABLE");
        //�õ��ս�ʱ���
        getAccountTime();
        //��ʼ��ҳ����Ϣ
        initPage();
    }

    /**
     * ��ʼ��ҳ����Ϣ
     */
    public void initPage() {
        setValue("REGION_CODE", Operator.getRegion());
        setValue("ACCOUNT_USER", Operator.getID() == null ? "" : Operator.getID());
        //=========pangben modify 20110414 start
        String accountDate =
            getAccountTime().substring(0, 4) + "/" +
            getAccountTime().substring(4, 6) + "/" +
            getAccountTime().substring(6, 8) ;
        String accountDateS =
            getAccountTimeS().substring(0, 4) + "/" +
            getAccountTimeS().substring(4, 6) + "/" +
            getAccountTimeS().substring(6, 8) ;
        setValue("ACCOUNT_DATE", accountDateS);
        setValue("ACCOUNT_DATEE", accountDate);
        //=========pangben modify 20110414 start
        //===zhangp 20120514 start
        setValue("DEPT", Operator.getDept());
        //===zhangp 20120514 end
    }

    public String getAccountTimeS() {
        String accTime = "";
        String data = StringTool.getString(StringTool.rollDate(SystemTool.
            getInstance().getDate(), -1),
                                           "yyyyMMddHHmmSS").substring(0, 8);
        //��������ʱ��
        TParm parm = BILSysParmTool.getInstance().getDayCycle("O");
        if (parm.getErrCode() < 0) {
            out(parm.getErrText());
        }
        if (parm.getValue("DAY_CYCLE") == null ||
            parm.getValue("DAY_CYCLE").length() == 0)
            accTime = StringTool.getString(SystemTool.getInstance().getDate(),
                                           "yyyyMMddHHmmSS");
        else
            accTime = data + parm.getValue("DAY_CYCLE", 0);
        return accTime;
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
       // System.out.println("        "+accountTime);
        //��������ʱ��
        //===zhangp 20120320 start
//        TParm parm = BILSysParmTool.getInstance().getDayCycle("O");
//        if (parm.getErrCode() < 0) {
//            out(parm.getErrText());
//        }
//        if (parm.getValue("DAY_CYCLE") == null ||
//            parm.getValue("DAY_CYCLE").length() == 0)
//            accountTime = StringTool.getString(SystemTool.getInstance().getDate(),
//                                               "yyyyMMddHHmmSS");
//        else
//            accountTime = data + parm.getValue("DAY_CYCLE", 0);
        //===zhangp 20120320 end
        return accountTime;
    }
    /**
     * �ս�
     * ===zhangp 20120315
     */
    public void onUpdate(){
    	if(admType == null||admType.equals("")){
    		update("O");
    		update("E");
    		update("H");
    	}else{
    		update(admType);
    	}
    }
    /**
     * �ս᷽��
     * @return boolean
     */
    public boolean update(String admType) {
        //ȡ���ս���Ա
        String casherUser = getValueString("ACCOUNT_USER");
        String regionCode=Operator.getRegion();
        //ȫ�������ս���Ա
        TParm casherParm;
        //���ûѡ�ս���Ա��ʾȫ��һ���ս�
        if (casherUser == null || casherUser.length() == 0) {
            //�õ�ȫ�������ս���Ա
          //===========pangben modify 20110421 start
            casherParm = getAccountUser(regionCode);
            //===========pangben modify 20110421 stop
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
//                    messageBox("" + casherParm.getValue("USER_NAME", i) +
//                               "�ս�ʧ��!");
//                    messageBox(casherParm.getValue("USER_NAME", i) + "���ս�����");
                	faileCashier.add(casherParm.getValue("USER_NAME", i));
                    continue;
                }
//                messageBox("" + casherParm.getValue("USER_NAME", i) + "�ս�ɹ�!");
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
        TParm result = TIOM_AppServer.executeAction("action.bil.BILAction",
            "onSaveAcctionOpb", accountParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * �õ��ս�����
     * @param casherUser String
     * @return TParm
     */
    public TParm getAccountParm(String casherUser,String admType) {
        TParm accountParm = new TParm();
       // System.out.println("�ż���"+admType);
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
        //System.out.println(accountTime);
        //=============pangben moidfy 20110620 start
        accountParm.setData("REGION_CODE", Operator.getRegion());
        return accountParm;
    }

    /**
     * �õ��ս���Ա��
     * @return String[]
     * ======pangben modify 20110421 ����������
     */
    public TParm getAccountUser(String regionCode) {
         //=============pangben modify 20110620 start
//         if(!"".equals(regionCode)&&null!=regionCode)
//             parm.setData("REGION_CODE",regionCode);
//        TParm accountUser = SYSOperatorTool.getInstance().getCasherCode(parm);
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
         //=============pangben modify 20110620 stop
        if (accountUser.getErrCode() < 0)
            this.messageBox(" ȡ���շ�Ա��Ϣʧ�� " + accountUser.getErrText());
        return accountUser;
    }

    /**
     * �õ��ս���
     * @param casherUser String
     * @return double
     */
    public double getArAmt(String casherUser,String admType) {
        TParm parm = new TParm();
        //====zhangp 20120305 modify start
//        parm.setData("PRINT_USER", casherUser);
        parm.setData("CASHIER_CODE", casherUser);
        //===zhangp 20120305 modify end
        parm.setData("ADM_TYPE", admType);
        parm.setData("BILL_DATE", accountTime);
        //=== zhangp 20120312 start
//        if(admType.equals("")||admType==null){
//        	parm = OPBReceiptTool.getInstance().getSumAramtAll(parm);
//        }else{
        	parm = OPBReceiptTool.getInstance().getSumAramt(parm);
//        }
        //===zhangp 20120312 end
        if (parm.getErrCode() < 0) {
            this.messageBox("��ȡ�ϼƽ��ʧ�� " + parm.getErrText());
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
//        if(admType.equals("")||admType==null){
//        	backPringNoSql = BILSQL.getBackPrintNoAll(parm);
//        }else{
        	backPringNoSql = BILSQL.getBackPrintNo(parm);
//        }
        //===zhangp 20120312 modify end
//        TParm backPrintNo = new TParm(TJDODBTool.getInstance().select(
//            backPringNoSql));
//        if (backPrintNo.getErrCode() < 0) {
//            System.out.println("�˷�Ʊ�Ų��Ҵ��� " + backPrintNo.getErrText());
//            return 0;
//        }
        //===zhangp 20120310 modify start
//        if(admType.equals("")||admType==null){
//        	parm = BILInvrcptTool.getInstance().getInvalidCountAll(parm);
//        }else{
        //===zhangp 20120319 start ����Ʊ�ݸ�������ȡBIL_INVRCP �����ս�Ʊ�ݱ����Ϻ� �ڴ˱����޷����
//        	parm = BILInvrcptTool.getInstance().getInvalidCount(parm);
        	parm = OPBReceiptTool.getInstance().getOpbResetCount(parm);
        	//==zhangp 20120319 end
//        }
        //===zhangp 20120310 modify end
        if (parm.getErrCode() < 0) {
           // System.out.println("" + parm.getErrText());
            return 0;
        }
        int printNo = 0;
//        if(backPrintNo!=null&&backPrintNo.getCount("PRINT_NO")>=0)
//            printNo = backPrintNo.getCount("PRINT_NO");
        if(parm !=null&&parm.getInt("COUNT", 0)>=0)
            printNo += parm.getInt("COUNT", 0);
       // System.out.println("�õ��˷�����parm"+parm);
        return printNo;
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
        //===zhangp 20120310 modify start
        parm.setData("ADM_TYPE", admType);
//        if(admType.equals("")||admType == null){
//        	parm = BILInvrcptTool.getInstance().getInvrcpCountAll(parm);
//        }else{
        //===ZHANGP 20120319 start ��ӦȡBIL_INVRCP ���ս��Ʊ�� �����Ϻ���BIL_INVRCP�޷�ȡ��
//        parm = BILInvrcptTool.getInstance().getInvrcpCount(parm);
        parm = OPBReceiptTool.getInstance().getOpbRecpCount(parm);
        //===ZHANGP 20120319 end
        	
//        }
        //===zhangp 20120310 modify end
        if (parm.getErrCode() < 0) {
            //System.out.println("" + parm.getErrText());
            return false;
        }
        //============pangben modify 20110405 start û��Ҫ��ѯ�����ݲ���ʾ��Ϣ
        if (parm.getInt("COUNT", 0) <= 0)
            return false;
         this.messageBox("�ս�Ʊ��"+parm.getInt("COUNT",0));
        //============pangben modify 20110405 stop
//        //��˽��
//        TParm checkReceipt = new TParm();
//        checkReceipt.setData("CASHIER_CODE", casherUser);
//        checkReceipt.setData("ADM_TYPE", admType);
//        checkReceipt.setData("BILL_DATE", accountTime);
//        checkReceipt = OPBReceiptTool.getInstance().getSumAramt(checkReceipt);
//        if (checkReceipt.getErrCode() < 0) {
//            System.out.println("" + parm.getErrText());
//            return false;
//        }
//        this.messageBox("�ս���"+checkReceipt.getDouble("AR_AMT",0));
//        if (checkReceipt.getDouble("AR_AMT", 0) <= 0)
//            return false;
        return true;
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
//    	String admType = getValueString("ADM_TYPE");
//    	if(admType.equals("")){
//    		messageBox("��ѡ���ż���");
//    		return;
//    	}
        TParm parm = new TParm();
        parm.setData("ACCOUNT_USER", getValue("ACCOUNT_USER"));
        if (getValue("ACCOUNT_DATE") == null ||
            getValueString("ACCOUNT_DATE").equals("")) {
            messageBox("��ѡ��ʼʱ��!");
            return;
        }
        parm.setData("ACCOUNT_DATE", StringTool.getString((Timestamp) getValue("ACCOUNT_DATE"),"yyyyMMdd")+this.getValueString("S_TIME").replace(":",""));//=======pangben modify 20110414
        if (getValue("ACCOUNT_DATEE") == null ||
            getValueString("ACCOUNT_DATEE").equals("")) {
            messageBox("��ѡ�����ʱ��!");
            return;
        }
        parm.setData("ACCOUNT_DATEE",StringTool.getString((Timestamp) getValue("ACCOUNT_DATEE"),"yyyyMMdd")+this.getValueString("E_TIME").replace(":",""));//=======pangben modify 20110414
        //===zhangp 20120511 start
//		if (getValue("ADM_TYPE") == null
//				|| getValueString("ADM_TYPE").equals("")) {
//		} else {
		parm.setData("ADM_TYPE", getValue("ADM_TYPE"));
//		}
		//===zhangp 20120511 end
        //=======================pangben modify 20110405 start  ��Ӳ�ѯ����
          //����
		if (getValueString("REGION_CODE").length() > 0)
			parm.setData("REGION_CODE", getValueString("REGION_CODE"));
		// =======================pangben modify 20110405 stop
		// ===zhangp 20120511 start
		// String sql = BILSQL.getAccountSql(parm);
		String sql = "SELECT 'N' S,B.REGION_CHN_ABN AS REGION_CHN_DESC,A.ACCOUNT_SEQ,A.ACCOUNT_DATE,A.ACCOUNT_USER,A.AR_AMT,"
				+ "       A.STATUS,A.INVALID_COUNT "
				+ "  FROM BIL_ACCOUNT A, SYS_REGION B ,SYS_OPERATOR_DEPT C  "
				+ "WHERE A.REGION_CODE=B.REGION_CODE AND A.ACCOUNT_TYPE = 'OPB' AND C.MAIN_FLG = 'Y' AND " +
				"   A.ACCOUNT_USER = C.USER_ID(+) AND  ";
		String region = parm.getValue("REGION_CODE");
		String dept = getValueString("DEPT");
		if (region != null && !region.trim().equals(""))
			sql += " A.REGION_CODE='" + region + "' AND ";
		String value = parm.getValue("ACCOUNT_USER");
		if (value != null && !value.equals(""))
			sql += " A.ACCOUNT_USER='" + value + "' AND ";
		String admType = parm.getValue("ADM_TYPE");
		if (admType != null && !admType.equals(""))
			sql += " A.ADM_TYPE='" + admType + "' AND ";
		if (dept != null && !dept.equals(""))
			sql += " C.DEPT_CODE='" + dept + "' AND ";
		sql += " A.ACCOUNT_DATE BETWEEN TO_DATE('"
				+ parm.getValue("ACCOUNT_DATE") + "','YYYYMMDDHH24MISS')"
				+ " AND TO_DATE('" + parm.getValue("ACCOUNT_DATEE")
				+ "','YYYYMMDDHH24MISS')";
		sql += " ORDER BY B.REGION_CHN_DESC, A.ACCOUNT_SEQ";
		// ===zhangp 20120511 end
		//System.out.println("�ս��ѯ����������������������������"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			// System.out.println("�ս����ݲ�ѯ����  " + result.getErrText());
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
     * ��ӡ�սᱨ��
     */
    public void onPrint() {
        //�õ�table�ϵ�����
        table.acceptText();
        TParm tableParm = table.getParmValue();
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
                this.openPrintWindow(
                    "%ROOT%\\config\\prt\\hrm\\HRMReceiptPrint.jhw", printData);

            }

        }
        else {
            printData = getAccountData(endParm);
            if (printData == null)
                return;
            this.openPrintWindow(
                "%ROOT%\\config\\prt\\hrm\\HRMReceiptPrint.jhw", printData);

        }
//       System.out.println("�շ��ս�����"+printData);
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
       // System.out.println("222222222222222222>>>>>>"+count);
        for (int i = 0; i < count; i++) {
            if (tableParm.getValue("S", i).equals("Y")) {
                return true;
            }
        }
        //System.out.println("3333333333333333������������"+tableParm);
        messageBox("�޴�ӡ����!");
        return false;
    }

    /**
     * �õ���ǰʱ��
     * @return String
     */
    public String getApTime() {
        return StringTool.getString(SystemTool.getInstance().getDate(),
                                    "yyyy/MM/dd HH:mm:ss");
    }

    /**
     *�õ��ս�����
     * @param tableParm TParm
     * @return TParm
     */
    public TParm getAccountData(TParm tableParm) {
        TParm parm = new TParm();
        String accountSeq = getAccountSeq(tableParm);
        parm = getPrintNo(accountSeq,tableParm);
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
      //  System.out.println("�õ��ս�ų���"+accountSeq);
        return accountSeq;
    }
    
    /**
     * �����ս�����
     * @param tableParm
     * @return
     */
    public String getAccountDate(TParm tableParm){
    	 int count = tableParm.getCount();
         String accountSeq = "";
         String seq = "";
         for (int i = 0; i < count; i++) {
             seq = tableParm.getValue("S", i);
             if (seq.equals("Y")) {
                 if (accountSeq.length() > 0)
                     accountSeq += ",";
                 accountSeq += "'" + tableParm.getValue("ACCOUNT_DATE", i) + "'";
             }
         }
       //  System.out.println("�õ��ս�ų���"+accountSeq);
         return accountSeq;
    }

    /**
     * �õ��ս�Ʊ��
     * @param accountSeq String
     * @param tableParm ҳ��ȡ������Ҫ��ӡ�ս��Ʊ���б�����
     * @return TParm
     */
    public TParm getPrintNo(String accountSeq,TParm tableParm) {
    	//===zhangp 20120511 start
    	String admType = getValueString("ADM_TYPE");
    	//===zhangp 20120511 end
        TParm printParm = new TParm();
        TParm returnParm = new TParm();
        printParm.setData("ACCOUNT_SEQ", accountSeq);
        //=====20120218 zhangp modify start
        String[] accseqs = accountSeq.split("'");
        String accseq = "";
        for (int i = 0; i < accseqs.length; i++) {
        	accseq = accseq + accseqs[i];
		}
        returnParm.setData("ACCOUNT_SEQ", accseq);
      //=====20120218 zhangp modify end
//        ExportExcelUtil.getInstance().exeSaveExcel(new TParm[]{new TParm(),new TParm()},"������������Ա���");
        //�õ���ӡƱ��
//        TParm pringNo = BILInvrcptTool.getInstance().getPrintNo(printParm);
        String printNosql = BILSQL.getPrintNo(accountSeq, "OPB",admType);
        TParm printNo = new TParm(TJDODBTool.getInstance().select(printNosql));
        if (printNo.getErrCode() < 0) {
           // System.out.println("��ӡƱ�Ų��Ҵ��� " + printNo.getErrText());
            return null;
        }
        //===zhangp 20120511 start
//        String printUsersql = BILSQL.getPrintUser(accountSeq,"OPB",admType);
        String printUsersql = 
//        	"SELECT CASHIER_CODE FROM BIL_OPB_RECP " +
//            " WHERE ACCOUNT_SEQ IN(" + accountSeq + ")" +
//            //===ZHANGP 20120312 START
//            "   AND ADM_TYPE = '" + admType + "' ";
        	"SELECT DISTINCT B.USER_NAME" +
        	" FROM BIL_OPB_RECP A, SYS_OPERATOR B" +
        	" WHERE A.CASHIER_CODE = B.USER_ID AND A.ACCOUNT_SEQ IN (" + accountSeq + ")";
        if(!admType.equals("")&&admType!=null){
        	printUsersql += " AND A.ADM_TYPE = '" + admType + "'";
        }
        //===zhangp 20120511 end
        TParm printUser = new TParm(TJDODBTool.getInstance().select(
            printUsersql));
        if (printUser.getErrCode() < 0) {
           // System.out.println("�շ���Ա���Ҵ��� " + printUser.getErrText());
            return null;
        }
//        System.out.println("printNo========="+printNo);
        String printNoS = dealTearPrint(printNo);
        //===zhangp 20120511 start
//        String printUserS = dealPrintUser(printUser);
        String printUserS = "";
        for (int i = 0; i < printUser.getCount(); i++) {
        	printUserS += ","+printUser.getValue("USER_NAME", i);
		}
        printUserS = printUserS.substring(1, printUserS.length());
        //===zhangp 20120511 end
        printNo.setCount(0);
        returnParm.setData("PRINTNO", printNo.getData());
        //=======zhangp 20120218 modify start
        //=====zhangp 20120229 modify start
//        String admType = getValueString("ADM_TYPE");
        String sqlBil = 
        	//===zhangp 20120327 start
//        	"SELECT INV_NO AS PRINT_NO,PRINT_DATE AS BILL_DATE,ACCOUNT_SEQ FROM BIL_INVRCP WHERE ADM_TYPE = '"+admType+"' AND ACCOUNT_SEQ IN ("+
//        	accountSeq+") AND RECP_TYPE = 'OPB' ORDER BY PRINT_DATE ";
        	//===zhangp 20120511 start
//        "SELECT PRINT_NO,BILL_DATE,ACCOUNT_SEQ FROM BIL_OPB_RECP WHERE ADM_TYPE = '"+admType+"' AND ACCOUNT_SEQ in ("+
        "SELECT PRINT_NO,BILL_DATE,ACCOUNT_SEQ FROM BIL_OPB_RECP WHERE ADM_TYPE LIKE '%"+admType+"%' AND ACCOUNT_SEQ in ("+
        //===zhangp 20120511 end
        accountSeq+") ORDER BY BILL_DATE ";
        //======zhangp 20120229 modify end
        TParm bilParm = new TParm(TJDODBTool.getInstance().select(sqlBil));
//        System.out.println("bilParm========"+bilParm);
        String recp_no = "";
        TParm recParm = getOPBparm(accountSeq);
        for (int i = 0; i < recParm.getCount("PRINT_USER"); i++) {
        	recp_no += recParm.getValue("INV_NOS",i) + ";";
		}
        returnParm.setData("PRINTNOS", recp_no);
        //============zhangp 20120218 modify end
        returnParm.setData("PRINTUSERS", printUserS);
        //�˷�Ʊ��
//        TParm backPrintNo = BILInvrcptTool.getInstance().getBackPrintNo(
//            printParm);
        String backPringNoSql = BILSQL.getBackPrintNo(accountSeq);
        TParm backPrintNo = new TParm(TJDODBTool.getInstance().select(
            backPringNoSql));
        if (backPrintNo.getErrCode() < 0) {
          //  System.out.println("�˷�Ʊ�Ų��Ҵ��� " + backPrintNo.getErrText());
            return null;
        }
        returnParm.setData("BACKPRINT", backPrintNo.getData());
        
         /**
          * ����Ʊ��
          */
//        TParm tearPrintNo = BILInvrcptTool.getInstance().getTearPrintNo(
//            printParm);
        String tearPrintNoSql = BILSQL.getTearPringNo(accountSeq,admType);
        TParm tearPrintNo = new TParm(TJDODBTool.getInstance().select(
            tearPrintNoSql));
        if (tearPrintNo.getErrCode() < 0) {
           // System.out.println("����Ʊ�Ų��Ҵ��� " + tearPrintNo.getErrText());
            return null;
        }
        String tearNo = dealTearPrint(tearPrintNo);
        returnParm.setData("TEARPRINTNO", getPrintNO((Vector)tearPrintNo.getData("INV_NO")));
        //�õ��վ�parm
        String sql = BILSQL.getReceiptParm(accountSeq);
//        System.out.println("Ǯ��sql====="+sql);
        if (sql == "" || sql.equals(""))
            return null;
        TParm receiptParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (receiptParm.getErrCode() < 0) {
            //System.out.println("�վݲ��Ҵ��� " + receiptParm.getErrText());
            return null;
        }
        /**
         * ����Ʊ��parm(BIL_OPB_RECP)
         */
        TParm endReceiptParm = dealPrintParm(receiptParm);
        //===zhangp 20120308 modify start
        String arAmtWord = com.javahis.util.StringUtil.getInstance().numberToWord(endReceiptParm.getParm("PAYTOT").getDouble("AR_AMT"));
        returnParm.setData("AR_AMT_W", "TEXT", arAmtWord);
        //===zhangp 20120308 modify end
        //==========================================================  chenxi modify 20130327
        double allMoney = 0.00 ;
        TParm remarkTable =  new TParm() ;
        for(int i=0;i<endReceiptParm.getParm("REMARK").getCount("REMARK");i++){
        	remarkTable.addData("NAME", endReceiptParm.getParm("REMARK").getValue("REMARK", i)+"   "+
        			 "��"+endReceiptParm.getParm("REMARK").getValue("MONEY", i));  
        	allMoney +=endReceiptParm.getParm("REMARK").getDouble("MONEY", i);
        }
        remarkTable.setCount(endReceiptParm.getParm("REMARK").getCount("REMARK")) ;
        remarkTable.addData("SYSTEM", "COLUMNS", "NAME");
        //==========================================================   chenxi modify 20130327
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
        double charge19 = chargeparm.getDouble("CHARGE19");//����  //modify by wanglong 20130124
//        double charge20 = chargeparm.getDouble("CHARGE20");
//        double charge21 = chargeparm.getDouble("CHARGE21");
//        double charge22 = chargeparm.getDouble("CHARGE22");
//        double charge23 = chargeparm.getDouble("CHARGE23");
//        double charge24 = chargeparm.getDouble("CHARGE24");
//        double charge25 = chargeparm.getDouble("CHARGE25");
//        double charge26 = chargeparm.getDouble("CHARGE26");
//        double charge27 = chargeparm.getDouble("CHARGE27");
//        double charge28 = chargeparm.getDouble("CHARGE28");
//        double charge29 = chargeparm.getDouble("CHARGE29");
//        double charge30 = chargeparm.getDouble("CHARGE30");
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
        //===ZHANGP 20120313 START
        TParm totParm = endReceiptParm.getParm("PAYTOT");
        TParm payParm = endReceiptParm.getParm("PAY");
        TParm backParm = endReceiptParm.getParm("PAYBACK");
//        returnParm.setData("PAY_INS_CARD_Y", "TEXT", StringTool.round(payParm.getDouble("PAY_INS_CARD"),2));
//        returnParm.setData("PAY_INS_CARD_T", "TEXT", StringTool.round(backParm.getDouble("PAY_INS_CARD"),2));
//        returnParm.setData("PAY_INS_CARD_S", "TEXT", StringTool.round(totParm.getDouble("PAY_INS_CARD"),2));
//        returnParm.setData("PAY_INS_PERSON_Y", "TEXT", StringTool.round(endReceiptParm.getParm("PAYTOT").getDouble("AR_AMT")-payParm.getDouble("PAY_INS"),2));
//        returnParm.setData("PAY_INS_PERSON_T", "TEXT", StringTool.round(endReceiptParm.getParm("PAYTOT").getDouble("AR_AMT")-backParm.getDouble("PAY_INS"),2));
//        returnParm.setData("PAY_INS_PERSON_S", "TEXT", StringTool.round(endReceiptParm.getParm("PAYTOT").getDouble("AR_AMT")-totParm.getDouble("PAY_INS"),2));
//        returnParm.setData("PAY_INS_TOT_Y", "TEXT", StringTool.round(payParm.getDouble("PAY_INS_CARD"),2));
//        returnParm.setData("PAY_INS_TOT_T", "TEXT", StringTool.round(backParm.getDouble("PAY_INS_CARD"),2));
//        returnParm.setData("PAY_INS_TOT_S", "TEXT", StringTool.round(totParm.getDouble("PAY_INS_CARD"),2));
        //===ZHANGP 20120313 END
        //��ʼʱ��
        String dispenseDate = getValueString("ACCOUNT_DATE").replace("-", "").
            replace(" ", "").replace(":", "");
        dispenseDate = dealDate(dispenseDate);
        //==================zhangp 20120217 modify start
        String datesql = 
        	"SELECT ACCOUNT_DATE FROM BIL_INVRCP WHERE ACCOUNT_SEQ in ("+accountSeq+") ORDER BY ACCOUNT_DATE";
        TParm dateparm= new TParm(TJDODBTool.getInstance().select(datesql));
        String stardate = bilParm.getData("BILL_DATE", 0).toString();
        String enddate = bilParm.getData("BILL_DATE", bilParm.getCount()-1).toString();
        stardate = stardate.substring(0, 19);
        enddate = enddate.substring(0, 19);
        returnParm.setData("ACCOUNT_DATE", stardate+" �� "+enddate);
      //==================zhangp 20120217 modify end
        //����ʱ��
        //=================pangben modify 20110414 start
        String dispenseDateE = StringTool.getString((Timestamp)this.getValue("ACCOUNT_DATEE"),"yyyyMMdd")+this.getValueString("E_TIME").replace(":", "");
      //=================pangben modify 20110414 stop
     //  System.out.println("dispenseDateE"+dispenseDateE);
        dispenseDateE = dealDate(dispenseDateE);

        returnParm.setData("ACCOUNT_DATEE", dispenseDateE);
        //��ӡʱ��
        String apDate = StringTool.getString(SystemTool.getInstance().getDate(),
                                             "yyyyMMddHHmmss");
        apDate = dealDate(apDate);

        returnParm.setData("PRINT_TIME", apDate);
        //===zhangp 20120320 start
        //====zhangp 20120324 start
        sql = 
        	"SELECT " +
        	" A.INS_CROWD_TYPE," +
        	" SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT," +
        	" SUM(A.OTOT_AMT)        AS OTOT_AMT," +
        	" SUM(A.ARMY_AI_AMT)     AS ARMY_AI_AMT," +
        	" SUM(A.TOTAL_AGENT_AMT) AS TOTAL_AGENT_AMT," +
        	" SUM(A.FLG_AGENT_AMT)   AS FLG_AGENT_AMT," +
        	" SUM(A.SERVANT_AMT)     AS SERVANT_AMT," +
        	" SUM(A.UNREIM_AMT)      AS UNREIM_AMT " +
        	"FROM " +
        	" INS_OPD A,BIL_OPB_RECP B " +
        	"WHERE " +
        	" A.REGION_CODE = '"+Operator.getRegion()+"' " +
        	" AND A.REGION_CODE  = B.REGION_CODE " +
        	" AND A.CASE_NO = B.CASE_NO " +
        	" AND A.CONFIRM_NO NOT LIKE '*%' " +
        	" AND A.INV_NO = B.PRINT_NO " +
        	" AND B.ACCOUNT_SEQ IN ("+accountSeq+") " +
        	" AND B.AR_AMT>0 " +
        	"GROUP BY  " +
        	" INS_CROWD_TYPE " +
        	"ORDER BY " +
        	" INS_CROWD_TYPE";
        TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
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
		if(insParm.getCount()>0){
			for (int i = 0; i < insParm.getCount(); i++) {
				if(insParm.getData("INS_CROWD_TYPE", i).equals("1")){
					//��ְINS_CROWD_TYPE = ��1��
					//�����˻�=ACCOUNT_PAY_AMT
					//�籣����֧��=OTOT_AMT+ ARMY_AI_AMT+TOTAL_AGENT_AMT+FLG_AGENT_AMT+SERVANT_AMT
					payInsCardY = insParm.getDouble("ACCOUNT_PAY_AMT", i);
					payInsNhiY = insParm.getDouble("OTOT_AMT", i) + insParm.getDouble("ARMY_AI_AMT", i) + 
										insParm.getDouble("TOTAL_AGENT_AMT", i) + insParm.getDouble("FLG_AGENT_AMT", i) + 
										insParm.getDouble("SERVANT_AMT", i);
					returnParm.setData("PAY_INS_CARD_Y", "TEXT", StringTool.round(payInsCardY,2));
					returnParm.setData("PAY_INS_NHI_Y", "TEXT", StringTool.round(payInsNhiY,2));
				}
				if(insParm.getData("INS_CROWD_TYPE", i).equals("2")){
//					�Ǿ�INS_CROWD_TYPE = ��2��
//					�������=FLG_AGENT_AMT+ ARMY_AI_AMT+ SERVANT_AMT
//					ͳ��=TOTAL_AGENT_AMT
					payInsHelpY = insParm.getDouble("FLG_AGENT_AMT", i) + insParm.getDouble("ARMY_AI_AMT", i) + 
										insParm.getDouble("SERVANT_AMT", i);
					payInsY = insParm.getDouble("TOTAL_AGENT_AMT", i);
					returnParm.setData("PAY_INS_HELP_Y", "TEXT", StringTool.round(payInsHelpY,2));
					returnParm.setData("PAY_INS_Y", "TEXT", StringTool.round(payInsY,2));
				}
				unreimAmtY += insParm.getDouble("UNREIM_AMT", i);
			}
		}
        sql = 
        	"SELECT " +
        	" A.INS_CROWD_TYPE," +
        	" SUM(A.ACCOUNT_PAY_AMT) AS ACCOUNT_PAY_AMT," +
        	" SUM(A.OTOT_AMT)        AS OTOT_AMT," +
        	" SUM(A.ARMY_AI_AMT) ARMY_AI_AMT," +
        	" SUM(A.TOTAL_AGENT_AMT) AS TOTAL_AGENT_AMT," +
        	" SUM(A.FLG_AGENT_AMT)   AS FLG_AGENT_AMT," +
        	" SUM(A.SERVANT_AMT)     AS SERVANT_AMT," +
        	" SUM(A.UNREIM_AMT)      AS UNREIM_AMT " +
        	"FROM " +
        	" INS_OPD A,BIL_OPB_RECP B " +
        	"WHERE A.REGION_CODE = '"+Operator.getRegion()+"' " +
        	" AND A.REGION_CODE  = B.REGION_CODE " +
        	" AND A.CASE_NO = B.CASE_NO " +
        	" AND A.INV_NO = B.PRINT_NO " +
        	" AND A.CONFIRM_NO  LIKE '*%' " +
        	" AND B.ACCOUNT_SEQ IN ("+accountSeq+") " +
        	" AND B.AR_AMT<0 " +
        	"GROUP BY " +
        	" INS_CROWD_TYPE " +
        	"ORDER BY " +
        	" INS_CROWD_TYPE";
//        System.out.println("��==="+sql);
        TParm insParmT = new TParm(TJDODBTool.getInstance().select(sql));
        if(insParmT.getCount()>0){
        	for (int i = 0; i < insParmT.getCount(); i++) {
            	if(insParmT.getData("INS_CROWD_TYPE", i).equals("1")){
            		//��ְINS_CROWD_TYPE = ��1��
            		//�����˻�=ACCOUNT_PAY_AMT
            		//�籣����֧��=OTOT_AMT+ ARMY_AI_AMT+TOTAL_AGENT_AMT+FLG_AGENT_AMT+SERVANT_AMT
            		payInsCardT = insParmT.getDouble("ACCOUNT_PAY_AMT", i);
            		payInsNhiT = insParmT.getDouble("OTOT_AMT", i) + insParmT.getDouble("ARMY_AI_AMT", i) + 
            		insParmT.getDouble("TOTAL_AGENT_AMT", i) + insParmT.getDouble("FLG_AGENT_AMT", i) + 
            		insParmT.getDouble("SERVANT_AMT", i);
            		returnParm.setData("PAY_INS_CARD_T", "TEXT", Math.abs(StringTool.round(payInsCardT,2)));
            		returnParm.setData("PAY_INS_NHI_T", "TEXT", Math.abs(StringTool.round(payInsNhiT,2)));
            	}
            	if(insParmT.getData("INS_CROWD_TYPE", i).equals("2")){
//    				�Ǿ�INS_CROWD_TYPE = ��2��
//    				�������=FLG_AGENT_AMT+ ARMY_AI_AMT+ SERVANT_AMT
//    				ͳ��=TOTAL_AGENT_AMT
            		payInsHelpT = insParmT.getDouble("FLG_AGENT_AMT", i) + insParmT.getDouble("ARMY_AI_AMT", i) + 
            		insParmT.getDouble("SERVANT_AMT", i);
            		payInsT = insParmT.getDouble("TOTAL_AGENT_AMT", i);
            		returnParm.setData("PAY_INS_HELP_T", "TEXT", Math.abs(StringTool.round(payInsHelpT,2)));
            		returnParm.setData("PAY_INS_T", "TEXT", Math.abs(StringTool.round(payInsT,2)));
            	}
            	unreimAmtT += insParmT.getDouble("UNREIM_AMT", i);
            }
        }
        unreimAmtS = unreimAmtY - unreimAmtT;
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
        //===zhangp 20120320 end
		//==ZHANGP 20120328 start
        TParm printCancleDate = this.getPrintCancelTableDate(accountSeq);
        TParm printReturnDate = this.getPrintReturnTableDate(accountSeq);
        TParm printChangeDate = this.getChangeTableDate(accountSeq);
		returnParm.setData("cancelTable", printCancleDate.getData());
		returnParm.setData("returnFeeTable", printReturnDate.getData());
		returnParm.setData("changeTable", printChangeDate.getData());
		returnParm.setData("REMARKTABLE", remarkTable.getData()); //=====modify  chenxi 20130714
		//==ZHANGP 20120328 end
        //========pangben modify 20110419 start
        String region = table.getParmValue().getRow(
                0).getValue("REGION_CHN_DESC");
        returnParm.setData("TITLE", "TEXT",
                           (this.getValue("REGION_CODE") != null &&
                            !this.getValue("REGION_CODE").equals("") ? region :
                            "����ҽԺ") + "����շ��սᱨ��");
        returnParm.setData("NAME", "TEXT", printUserS);
        returnParm.setData("ALLMONEY", "TEXT", "�ܼ�:��"+StringTool.round(allMoney, 2));
        //========pangben modify 20110419 stop
//        System.out.println(returnParm);
        return returnParm;
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
     * Ʊ������
     * @param printNo Vector
     * @return String
     */
    public String getPrintNO(Vector printNo) {
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
     * �շ���Ա
     * @param parm TParm
     * @return String
     */
    public String dealPrintUser(TParm parm) {
        int count = parm.getCount();
        String printUserAll = "";
        if (count > 0)
            for (int i = 0; i < count; i++) {
                String name = parm.getValue("CASHIER_CODE", i);
                TParm operator = SYSOperatorTool.getInstance().selectdata(name);
                name = operator.getValue("USER_NAME", 0);
                if (printUserAll.indexOf(name) != -1)
                    continue;
                if (printUserAll.length() > 0)
                    printUserAll += "'";
                printUserAll += name;
            }
        return printUserAll;
    }

    /**
     * �˷�Ʊ��
     * @param parm TParm
     * @return TParm
     */
    public TParm dealBackPrint(TParm parm) {
        TParm tearPrint = new TParm();
        int count = parm.getCount();
        if (count > 0)
            for (int i = 0; i < count; i++) {
                tearPrint.addData("INV_NO", parm.getData("INV_NO", i));
                tearPrint.addData("AR_AMT", parm.getData("AR_AMT", i));
            }
        tearPrint.addData("SYSTEM", "COLUMNS", "INV_NO");
        tearPrint.addData("SYSTEM", "COLUMNS", "AR_AMT");
        tearPrint.setCount(count);
        return tearPrint;
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

    /**
     * ����Ʊ�ŵ�������
     * @param printNoParm TParm
     * @return TParm
     */
    public TParm dealPrintNo(TParm printNoParm) {
        TParm parm = new TParm();
        return parm;
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
        //֧Ʊ��ע
        TParm parmRemark = new TParm() ;    //   chenxi  add 20130327
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
                                parm.getDouble("PAY_CASH", i));
                payParm.setData("PAY_MEDICAL_CARD",
                                payParm.getDouble("PAY_MEDICAL_CARD") +
                                parm.getDouble("PAY_MEDICAL_CARD", i));
                payParm.setData("PAY_BANK_CARD",
                                payParm.getDouble("PAY_BANK_CARD") +
                                parm.getDouble("PAY_BANK_CARD", i));
                payParm.setData("PAY_INS_CARD",
                                payParm.getDouble("PAY_INS_CARD") +
                                parm.getDouble("PAY_INS_CARD", i));
                payParm.setData("PAY_CHECK",
                                payParm.getDouble("PAY_CHECK") +
                                parm.getDouble("PAY_CHECK", i));
//              if(parm.getDouble("PAY_CHECK", i)>0){
                if(parm.getInt("CANCEL_FLG", i)==0){//modify by wanglong 20130927
                	parmRemark.addData("REMARK", parm.getValue("PAY_REMARK", i)) ; //  chenxi add 20130327
                	parmRemark.addData("MONEY",  parm.getDouble("PAY_CHECK", i)) ;
                }
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
                //��Ʊ
                payParm.setData("PAY_DRAFT",
                				payParm.getDouble("PAY_DRAFT")+
                				parm.getDouble("PAY_DRAFT",i));
                
                //֧����ʽ�ܼ�
                payTot.setData("TOT_AMT",
                               payTot.getDouble("TOT_AMT") +
                               parm.getDouble("TOT_AMT", i));
                payTot.setData("AR_AMT",
                               payTot.getDouble("AR_AMT") +
                               parm.getDouble("AR_AMT", i));
                payTot.setData("PAY_CASH",
                               payTot.getDouble("PAY_CASH") +
                               parm.getDouble("PAY_CASH", i));
                payTot.setData("PAY_MEDICAL_CARD",
                               payTot.getDouble("PAY_MEDICAL_CARD") +
                               parm.getDouble("PAY_MEDICAL_CARD", i));
                payTot.setData("PAY_BANK_CARD",
                               payTot.getDouble("PAY_BANK_CARD") +
                               parm.getDouble("PAY_BANK_CARD", i));
                payTot.setData("PAY_INS_CARD",
                               payTot.getDouble("PAY_INS_CARD") +
                               parm.getDouble("PAY_INS_CARD", i));
                payTot.setData("PAY_CHECK",
                               payTot.getDouble("PAY_CHECK") +
                               parm.getDouble("PAY_CHECK", i));
                payTot.setData("PAY_DEBIT",
                               payTot.getDouble("PAY_DEBIT") +
                               parm.getDouble("PAY_DEBIT", i));
                payTot.setData("PAY_DRAFT",
                        payTot.getDouble("PAY_DRAFT") +
                        parm.getDouble("PAY_DRAFT", i));
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
            	//===zhangp 20120315 start
            	//===zhangp 20120418 start
                payParmBack.setData("TOT_AMT",
//                		(payParmBack.getDouble("TOT_AMT") +
//                                parm.getDouble("TOT_AMT", i))<0?-(payParmBack.getDouble("TOT_AMT") +
//                                    parm.getDouble("TOT_AMT", i)):(payParmBack.getDouble("TOT_AMT") +
//                                            parm.getDouble("TOT_AMT", i)));
                		(payParmBack.getDouble("TOT_AMT") +
                              parm.getDouble("TOT_AMT", i)));
                payParmBack.setData("AR_AMT",
//                		(payParmBack.getDouble("AR_AMT") +
//                                parm.getDouble("AR_AMT", i))<0?-(payParmBack.getDouble("AR_AMT") +
//                                    parm.getDouble("AR_AMT", i)):(payParmBack.getDouble("AR_AMT") +
//                                            parm.getDouble("AR_AMT", i)));
                		(payParmBack.getDouble("AR_AMT") +
                                parm.getDouble("AR_AMT", i)));
                payParmBack.setData("REDUCE_AMT", (payParmBack.getDouble("REDUCE_AMT") + parm
                        .getDouble("REDUCE_AMT", i)));// add by wanglong 20130826
                payParmBack.setData("PAY_CASH",
//                		(payParmBack.getDouble("PAY_CASH") +
//                                parm.getDouble("PAY_CASH", i))<0?-(payParmBack.getDouble("PAY_CASH") +
//                                    parm.getDouble("PAY_CASH", i)):(payParmBack.getDouble("PAY_CASH") +
//                                            parm.getDouble("PAY_CASH", i)));
                		(payParmBack.getDouble("PAY_CASH") +
                                parm.getDouble("PAY_CASH", i)));
                payParmBack.setData("PAY_MEDICAL_CARD",
//                		(payParmBack.getDouble("PAY_MEDICAL_CARD") +
//                                parm.getDouble("PAY_MEDICAL_CARD", i))<0?-(payParmBack.getDouble("PAY_MEDICAL_CARD") +
//                                    parm.getDouble("PAY_MEDICAL_CARD", i)):(payParmBack.getDouble("PAY_MEDICAL_CARD") +
//                                            parm.getDouble("PAY_MEDICAL_CARD", i)));
                		(payParmBack.getDouble("PAY_MEDICAL_CARD") +
                                parm.getDouble("PAY_MEDICAL_CARD", i)));
                payParmBack.setData("PAY_BANK_CARD",
//                		(payParmBack.getDouble("PAY_BANK_CARD") +
//                                parm.getDouble("PAY_BANK_CARD", i))<0?-(payParmBack.getDouble("PAY_BANK_CARD") +
//                                    parm.getDouble("PAY_BANK_CARD", i)):(payParmBack.getDouble("PAY_BANK_CARD") +
//                                            parm.getDouble("PAY_BANK_CARD", i)));
                		(payParmBack.getDouble("PAY_BANK_CARD") +
                                parm.getDouble("PAY_BANK_CARD", i)));
                payParmBack.setData("PAY_INS_CARD",
//                		(payParmBack.getDouble("PAY_INS_CARD") +
//                                parm.getDouble("PAY_INS_CARD", i))<0?-(payParmBack.getDouble("PAY_INS_CARD") +
//                                    parm.getDouble("PAY_INS_CARD", i)):(payParmBack.getDouble("PAY_INS_CARD") +
//                                            parm.getDouble("PAY_INS_CARD", i)));
                		Math.abs((payParmBack.getDouble("PAY_INS_CARD") +
                                parm.getDouble("PAY_INS_CARD", i))));
                payParmBack.setData("PAY_CHECK",
//                		(payParmBack.getDouble("PAY_CHECK") +
//                                parm.getDouble("PAY_CHECK", i))<0?-(payParmBack.getDouble("PAY_CHECK") +
//                                    parm.getDouble("PAY_CHECK", i)):(payParmBack.getDouble("PAY_CHECK") +
//                                            parm.getDouble("PAY_CHECK", i)));
                		(payParmBack.getDouble("PAY_CHECK") +
                                parm.getDouble("PAY_CHECK", i)));
                payParmBack.setData("PAY_DEBIT",
//                		(payParmBack.getDouble("PAY_DEBIT") +
//                                parm.getDouble("PAY_DEBIT", i))<0?-(payParmBack.getDouble("PAY_DEBIT") +
//                                    parm.getDouble("PAY_DEBIT", i)):(payParmBack.getDouble("PAY_DEBIT") +
//                                            parm.getDouble("PAY_DEBIT", i)));
                		(payParmBack.getDouble("PAY_DEBIT") +
                                parm.getDouble("PAY_DEBIT", i)));
                payParmBack.setData("PAY_BILPAY",
//                		(payParmBack.getDouble("PAY_BILPAY") +
//                                parm.getDouble("PAY_BILPAY", i))<0?-(payParmBack.getDouble("PAY_BILPAY") +
//                                    parm.getDouble("PAY_BILPAY", i)):(payParmBack.getDouble("PAY_BILPAY") +
//                                            parm.getDouble("PAY_BILPAY", i)));
                		(payParmBack.getDouble("PAY_BILPAY") +
                                parm.getDouble("PAY_BILPAY", i)));
                payParmBack.setData("PAY_OTHER1",
//                		(payParmBack.getDouble("PAY_OTHER1") +
//                                parm.getDouble("PAY_OTHER1", i))<0?-(payParmBack.getDouble("PAY_OTHER1") +
//                                    parm.getDouble("PAY_OTHER1", i)):(payParmBack.getDouble("PAY_OTHER1") +
//                                            parm.getDouble("PAY_OTHER1", i)));
                		(payParmBack.getDouble("PAY_OTHER1") +
                                parm.getDouble("PAY_OTHER1", i)));
                payParmBack.setData("PAY_OTHER2",
//                		(payParmBack.getDouble("PAY_OTHER2") +
//                                parm.getDouble("PAY_OTHER2", i))<0?-(payParmBack.getDouble("PAY_OTHER2") +
//                                    parm.getDouble("PAY_OTHER2", i)):(payParmBack.getDouble("PAY_OTHER2") +
//                                            parm.getDouble("PAY_OTHER2", i)));
                		(payParmBack.getDouble("PAY_OTHER2") +
                                parm.getDouble("PAY_OTHER2", i)));
                payParmBack.setData("PAY_INS",
//                		(payParmBack.getDouble("PAY_INS") +
//                                parm.getDouble("PAY_INS", i))<0?-(payParmBack.getDouble("PAY_INS") +
//                                    parm.getDouble("PAY_INS", i)):(payParmBack.getDouble("PAY_INS") +
//                                            parm.getDouble("PAY_INS", i)));
                		(payParmBack.getDouble("PAY_INS") +
                                parm.getDouble("PAY_INS", i)));
                payParmBack.setData("PAY_DRAFT",
//                		(payParmBack.getDouble("PAY_INS") +
//                                parm.getDouble("PAY_INS", i))<0?-(payParmBack.getDouble("PAY_INS") +
//                                    parm.getDouble("PAY_INS", i)):(payParmBack.getDouble("PAY_INS") +
//                                            parm.getDouble("PAY_INS", i)));
                		(payParmBack.getDouble("PAY_DRAFT") +
                                parm.getDouble("PAY_DRAFT", i)));
//                payParmBack.setData("PAY_DEBIT",
////                		(payParmBack.getDouble("PAY_INS") +
////                                parm.getDouble("PAY_INS", i))<0?-(payParmBack.getDouble("PAY_INS") +
////                                    parm.getDouble("PAY_INS", i)):(payParmBack.getDouble("PAY_INS") +
////                                            parm.getDouble("PAY_INS", i)));
//                		(payParmBack.getDouble("PAY_DEBIT") +
//                                parm.getDouble("PAY_DEBIT", i)));//delete by wanglong 20130327 ǰ���Ѿ����ˣ��ظ���
                //===zhangp 20120418 end
                //֧����ʽ�ܼ�
                payTot.setData("TOT_AMT",
                               payTot.getDouble("TOT_AMT") +
                               parm.getDouble("TOT_AMT", i));
                payTot.setData("AR_AMT",
                               payTot.getDouble("AR_AMT") +
                               parm.getDouble("AR_AMT", i));

                payTot.setData("PAY_CASH",
                               payTot.getDouble("PAY_CASH") +
                               parm.getDouble("PAY_CASH", i));
                payTot.setData("PAY_MEDICAL_CARD",
                               payTot.getDouble("PAY_MEDICAL_CARD") +
                               parm.getDouble("PAY_MEDICAL_CARD", i));
                payTot.setData("PAY_BANK_CARD",
                               payTot.getDouble("PAY_BANK_CARD") +
                               parm.getDouble("PAY_BANK_CARD", i));
                payTot.setData("PAY_INS_CARD",
                               payTot.getDouble("PAY_INS_CARD") +
                               parm.getDouble("PAY_INS_CARD", i));
                payTot.setData("PAY_CHECK",
                               payTot.getDouble("PAY_CHECK") +
                               parm.getDouble("PAY_CHECK", i));
                payTot.setData("PAY_DEBIT",
                               payTot.getDouble("PAY_DEBIT") +
                               parm.getDouble("PAY_DEBIT", i));
                payTot.setData("PAY_DRAFT",
                        payTot.getDouble("PAY_DRAFT") +
                        parm.getDouble("PAY_DRAFT", i));
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
//        System.out.println("�������"+payParm.getData());
        //����parm
//        for (int j = 0; j < ssssparm.getCount("CHARGE"); j++) {
//			int charge = ssssparm.getInt("CHARGE", j);
//			for (int k = 0; k < 30; k++) {
//				if(charge==k){
//					System.out.println("charge"+(k+1)+ssssparm.getData("CHN_DESC", j));
//				}
//			}
//			
//		}
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
        payParmBack.setData("REDUCE_AMT", Math.abs(payParmBack.getDouble("REDUCE_AMT")));//add by wanglong 20130826
        payParmBack.setData("PAY_CASH",
        		Math.abs(payParmBack.getDouble("PAY_CASH")));
        payParmBack.setData("PAY_MEDICAL_CARD",
        		Math.abs(payParmBack.getDouble("PAY_MEDICAL_CARD")));
        payParmBack.setData("PAY_BANK_CARD",
        		Math.abs(payParmBack.getDouble("PAY_BANK_CARD")));
        payParmBack.setData("PAY_INS_CARD",
        		Math.abs(payParmBack.getDouble("PAY_INS_CARD")));
        payParmBack.setData("PAY_CHECK",
        		Math.abs(payParmBack.getDouble("PAY_CHECK")));
        payParmBack.setData("PAY_DEBIT",
        		Math.abs(payParmBack.getDouble("PAY_DEBIT")));
        payParmBack.setData("PAY_DRAFT",
        		Math.abs(payParmBack.getDouble("PAY_DRAFT")));
        payParmBack.setData("PAY_BILPAY",
        		Math.abs(payParmBack.getDouble("PAY_BILPAY")));
        payParmBack.setData("PAY_OTHER1",
        		Math.abs(payParmBack.getDouble("PAY_OTHER1")));
        payParmBack.setData("PAY_OTHER2",
        		Math.abs(payParmBack.getDouble("PAY_OTHER2")));
        payParmBack.setData("PAY_INS",
        		Math.abs(payParmBack.getDouble("PAY_INS")));
        //===zhangp 2012419 end
        printParm.setData("PAY", payParm.getData());
        printParm.setData("PAYBACK", payParmBack.getData());
        printParm.setData("PAYTOT", payTot.getData());
        printParm.setData("CHARGE", chargeParm.getData());
        printParm.setData("REMARK",parmRemark.getData()) ;  //chenxi add 20130327
//        System.out.println("printparm======"+printParm);
        return printParm;
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
            "  WHERE RECP_TYPE = 'OPB' "+
            "    AND ACCOUNT_SEQ IN (" +accountSeq + ") "+
            "    AND CANCEL_FLG = '3' " +
            "    AND CANCEL_DATE < ACCOUNT_DATE";
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
            " SELECT INV_NO,AR_AMT FROM BIL_INVRCP " +
            "  WHERE RECP_TYPE = 'OPB' "+
            "    AND ACCOUNT_SEQ IN (" +accountSeq + ") "+
            "    AND CANCEL_FLG = '1' " ;
//            "  AND CANCEL_DATE < ACCOUNT_DATE";
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
            "  WHERE RECP_TYPE = 'OPB' "+
            "    AND ACCOUNT_SEQ IN (" +accountSeq + ") "+
            "    AND STATUS = '2' ";
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
						" AND A.RECP_TYPE = 'OPB'";
		sql += " AND LENGTH (A.INV_NO) < 12";//add by wanglong 20121112 ���˵�12λ�Ľ��л�����Ʊ�ݺ�
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

    
}

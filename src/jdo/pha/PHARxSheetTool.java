package jdo.pha;

import com.dongyang.jdo.TJDOTool;
import jdo.sys.Operator;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;
import jdo.odo.ODO;
import jdo.odo.OpdRxSheetTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import jdo.opd.OPDSysParmTool;
import jdo.ekt.EKTIO;
import jdo.odo.OpdOrder;
import jdo.sys.DictionaryTool;
import jdo.odo.RegPatAdm;
import jdo.odo.PatInfo;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class PHARxSheetTool extends TJDOTool {
    /**
     * ʵ��
     */
    public static PHARxSheetTool instanceObject;

    /**
     * ���췽��
     */
    public PHARxSheetTool() {
        onInit();
    }

    /**
     * �õ�ʵ��
     * @return OpdRxSheetTool
     */
    public static PHARxSheetTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PHARxSheetTool();
        return instanceObject;
    }

    /**
     * ��ӡ����
     * @param realDeptCode String
     * @param rxType String
     * @param opdOrder OpdOrder
     * @param rxNo String
     * @param psyFlg String
     * @param regPatAdm RegPatAdm
     * @param patInfo PatInfo
     * @return TParm
     */
    /*
    public TParm getOrderPrintParm(String realDeptCode, String rxType, OpdOrder opdOrder,
                                   String rxNo, String psyFlg,RegPatAdm regPatAdm,PatInfo patInfo) {
        TParm inParam = new TParm();
        String text = "TEXT";
        inParam.setData("ADDRESS", text,
                        "OpdExaSheet From " + Operator.getID() + " To LPT1");
        inParam.setData("PRINT_TIME", text,
                        StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                             "yyyy/MM/dd HH:mm:ss"));
        inParam.setData("HOSP_NAME", text,
                        OpdRxSheetTool.getInstance().getHospFullName());
        inParam.setData("HOSP_NAME_ENG", text, Operator.getHospitalENGShortName());
        inParam.setData("SHEET_NAME", text,
                        OpdRxSheetTool.getInstance().getExaOrOpName(rxType));
        inParam.setData("ORG_CODE", text,
                        OpdRxSheetTool.getInstance().
                        getOrgName(opdOrder.getCaseNo(), rxNo));
        inParam.setData("PAY_TYPE", text,
                        "�ѱ�:" +
                        OpdRxSheetTool.getInstance().
                        getPayTypeName(regPatAdm.getItemString(0, "CTZ1_CODE")));
        inParam.setData("PAY_TYPE_ENG", text,
                        "Cate:" +
                        OpdRxSheetTool.getInstance().
                        getPayTypeEngName(regPatAdm.getItemString(0, "CTZ1_CODE")));
        inParam.setData("MR_NO", text, "ID��:" + opdOrder.getMrNo());
        inParam.setData("MR_NO_ENG", text, "Pat ID:" + opdOrder.getMrNo());
        inParam.setData("PAT_NAME", text,
                        "����:" +
                        OpdRxSheetTool.getInstance().getPatName(opdOrder.getMrNo()));
        inParam.setData("PAT_NAME_ENG", text,
                        "Name:" +
                        OpdRxSheetTool.getInstance().getPatEngName(opdOrder.getMrNo()));
        inParam.setData("PAT_ID", text,
                        "�������֤�ţ�" +
                        OpdRxSheetTool.getInstance().getId(opdOrder.getMrNo()));
        inParam.setData("SEX_CODE", text,
                        "�Ա�:" +
                        OpdRxSheetTool.getInstance().getSexName(opdOrder.getMrNo()));
        inParam.setData("SEX_CODE_ENG", text,
                       "Gender:" + OpdRxSheetTool.getInstance().getSexEngName(opdOrder.getMrNo()));
        inParam.setData("BIRTHDAY", text,
                        "��������:" +
                        OpdRxSheetTool.getInstance().getBirthDays(opdOrder.getMrNo()));
        inParam.setData("BBB", text,
                        "Birthday:" +
                        OpdRxSheetTool.getInstance().getBirthDays(opdOrder.getMrNo()));
        inParam.setData("AGE", text,
                        "����:" +
                        OpdRxSheetTool.getInstance().
                        getAgeName(opdOrder.getCaseNo(), opdOrder.getMrNo()));
        inParam.setData("AGE_ENG", text,
                        "Age:" +
                        OpdRxSheetTool.getInstance().
                        getAgeEngName(opdOrder.getCaseNo(), opdOrder.getMrNo()));
        inParam.setData("DEPT_CODE", text,
                        "�Ʊ�:" +
                        OpdRxSheetTool.getInstance().getDeptName(realDeptCode));
        inParam.setData("DEPT_CODE_ENG", text,
                        "Dept:" +
                        OpdRxSheetTool.getInstance().getDeptEngName(realDeptCode));
        inParam.setData("CLINIC_ROOM", text,
                        "���:" +
                        OpdRxSheetTool.getInstance().getClinicName(opdOrder.getCaseNo()));
        inParam.setData("CLINIC_ROOM_ENG", text,
                        "Room:" +
                        OpdRxSheetTool.
                        getInstance().getClinicEngName(opdOrder.getCaseNo()));
        inParam.setData("DR_CODE", text, "ҽ��:" + Operator.getName());
        inParam.setData("DR_CODE_ENG", text, "M.D.:" + Operator.getName());
        inParam.setData("ADM_DATE", text,
                        "����ʱ��:" +
                        OpdRxSheetTool.getInstance().getOrderDate(opdOrder.getCaseNo()));
        inParam.setData("ADM_DATE_ENG", text,
                        "Date:" +
                        OpdRxSheetTool.getInstance().getOrderDate(opdOrder.getCaseNo()));
        inParam.setData("SESSION_CODE", text,
                        "ʱ��:" +
                        OpdRxSheetTool.getInstance().getSessionName(opdOrder.getCaseNo()));
        inParam.setData("SESSION_CODE_ENG", text,
                        "Time:" +
                        OpdRxSheetTool.getInstance().
                        getSessionEngName(opdOrder.getCaseNo()));
        inParam.setData("FOOT_DR", text, "ҽʦ/M.D.:" + Operator.getName());
        inParam.setData("FOOT_DR_CODE", text,
                        "ҽʦ����/Phys code:" + Operator.getID());
        inParam.setData("BAR_CODE", text, rxNo);
        inParam.setData("BALANCE", text,
                        "���/Deduction(��):" +  OpdRxSheetTool.getInstance().getArAmt(rxNo, opdOrder));

        double amt = EKTIO.getInstance().getRxBalance(opdOrder.getCaseNo(), rxNo);
        inParam.setData("CURRENT_AMT", text, "���/Balance(��):" + amt);
//		System.out.println("my arAmt="+getArAmt(rxNo,odo.getOpdOrder()));
//		System.out.println("amt="+amt);
        double orginalAmt = StringTool.round( OpdRxSheetTool.getInstance().getArAmt(rxNo, opdOrder) +
                                             amt, 2);
        inParam.setData("ORINGINAL_AMT", text, "ԭ��/Amount(��):" + orginalAmt);
        inParam.setData("DIAG", text, "�ٴ���ϣ�" + OpdRxSheetTool.getInstance().getIcdName(opdOrder.getCaseNo()));
        inParam.setData("DIAG_ENG", text,
                        "Diagnosis:" + OpdRxSheetTool.getInstance().getIcdEngName(opdOrder.getCaseNo()));
        inParam.setData("PRINT_NO_NO", text,opdOrder.getItemString(0, "PRINT_NO"));
//		System.out.println("printNoNo===="+odo.getOpdOrder().getItemString(0, "PRINT_NO"));
        inParam.setData("PRINT_NO", text, "��ҩ�ţ�");
        inParam.setData("PRINT_NO_ENG", text, "PrscrptNo:");
        String orgCode = opdOrder.getItemString(0, "EXEC_DEPT_CODE");
        String counterDate = StringTool.getString(opdOrder.getDBTime(),
                                                  "yyyyMMdd");
        String counterNo = opdOrder.getItemInt(0, "COUNTER_NO") + "";
        TParm counterParm = OpdRxSheetTool.getInstance().getCounterNames(orgCode, counterDate, counterNo);
        String counterDesc = "";
        String counterEngDesc = "";
        if (counterParm != null) {
            counterDesc = counterParm.getValue("COUNTER_DESC", 0);
            counterEngDesc = counterParm.getValue("COUNTER_ENG_DESC", 0);
        }
        inParam.setData("COUNTER_NO", text, "��̨��");
        inParam.setData("COUNTER_NO_ENG", text, "Counter:" + counterEngDesc);
        inParam.setData("COUNTER_NO_NO", text, counterNo);
//		System.out.println("counterNoNo==="+counterDesc);
        String orderType = "",
            orderTypeCode = regPatAdm.getItemString(0, "ADM_TYPE");

        //System.out.println("orderTypeCode=="+orderTypeCode);

        if ("O".equalsIgnoreCase(orderTypeCode)) {
            orderType = "�� Outpatient";
        }
        else if ("E".equalsIgnoreCase(orderTypeCode)) {
            orderType = "�� Emergency";
        }
//		System.out.println("birthday="+odo.getPatInfo().getItemTimestamp(0, "BIRTH_DATE"));
        String birthDate = patInfo.getItemTimestamp(0, "BIRTH_DATE") +
            "";
        if ("2".equalsIgnoreCase(rxType)) {
            if ("1".equalsIgnoreCase(psyFlg)) {
                orderType = "������һ Poison��MHD A";
            }
            else if ("2".equalsIgnoreCase(psyFlg)) {
                orderType = "���� MHD B";
            }
        }
        else if (!StringUtil.isNullString(birthDate) &&
                 OPDSysParmTool.
                 getInstance().isChild(patInfo.getItemTimestamp(0, "BIRTH_DATE"))) {
            orderType = "�� Pediatrics";
        }
        else if ("3".equalsIgnoreCase(rxType)) {
            orderType = "��ҽ Chn Med";
        }

        inParam.setData("ORDER_TYPE", text, orderType);
        inParam.setData("RX_NO", rxNo);
        inParam.setData("CASE_NO", opdOrder.getCaseNo());
        inParam.setData("RX_TYPE", rxType);
        if (!"3".equalsIgnoreCase(rxType)) {
            return inParam;
        }

        String sql = "SELECT   ORDER_DESC,MEDI_QTY,AR_AMT,D.FREQ_TIMES||'��/'||D.CYCLE||'��' FREQ_CODE,A. TAKE_DAYS,"
            +
            "         B.CHN_DESC DCTAGENT_CODE,'' DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT"
            +
            "  FROM   OPD_ORDER A,SYS_DICTIONARY B, SYS_DICTIONARY C,SYS_PHAFREQ D"
            + "  WHERE   RX_NO = '"
            + rxNo
            + "'"
            + "         AND B.GROUP_ID='PHA_DCTAGENT'"
            + "         AND A.DCTAGENT_CODE=B.ID"
            + "         AND C.GROUP_ID='PHA_DCTEXCEP'"
            + "         AND A.DCTEXCEP_CODE IS NULL"
            + "         AND A.FREQ_CODE=D.FREQ_CODE" +
            " 	UNION" +
            "	SELECT   DISTINCT ORDER_DESC,MEDI_QTY,AR_AMT,D.FREQ_TIMES || '��/' || D.CYCLE || '��' FREQ_CODE,A.TAKE_DAYS," +
            "                  B.CHN_DESC DCTAGENT_CODE,C.CHN_DESC DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT" +
            "   FROM   OPD_ORDER A,SYS_DICTIONARY B,SYS_DICTIONARY C,SYS_PHAFREQ D" +
            "   WHERE       RX_NO = '" + rxNo + "'" +
            "         AND B.GROUP_ID = 'PHA_DCTAGENT'" +
            "         AND A.DCTAGENT_CODE = B.ID" +
            "         AND C.GROUP_ID = 'PHA_DCTEXCEP'" +
            "         AND A.DCTEXCEP_CODE = C.ID" +
            "         AND A.FREQ_CODE = D.FREQ_CODE";
        //System.out.println("sql==============" + sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getErrCode() != 0) {
            //System.out.println("û������");
            return parm;
        }

        //System.out.println("tableparm=========="+parm);
        int count = parm.getCount();
        text = "TEXT";
        double totAmtDouble = 0.0;
//		Systme.out.printl
        for (int i = 0; i < count; i++) {
            String row = (i / 4 + 1) + "";
            //System.out.println("row==========="+row);
            String column = (i % 4 + 1) + "";
            //System.out.println("olumn========"+column);
            totAmtDouble += parm.getDouble("TOT_AMT", i);
            inParam.setData("ORDER_DESC" + row + column, text, parm.getValue(
                "ORDER_DESC", i));
            inParam.setData("MEDI_QTY" + row + column, text, parm.getValue(
                "MEDI_QTY", i)
                            + "��");
            inParam.setData("DCTAGENT" + row + column, text, parm.getValue(
                "DCTEXCEP_CODE", i));
        }
        inParam.setData("TAKE_DAYS", text, "������"
                        + parm.getValue("TAKE_DAYS", 0) + "��");
        inParam.setData("FREQ_CODE", text, "��Σ�"
                        + parm.getValue("FREQ_CODE", 0));
        inParam.setData("ROUTE_CODE", text, "������"
                        + parm.getValue("DCTAGENT_CODE", 0));
        inParam.setData("DCT_TAKE_QTY", text, "ÿ�η�������"
                        + parm.getValue("DCT_TAKE_QTY", 0) + "ml");
        inParam.setData("PACKAGE_TOT", text, "ÿ���ܿ�����"
                        + parm.getValue("PACKAGE_TOT", 0) + "��");
        return inParam;

    }*/
    public TParm getOrderPrintParm(String realDeptCode, String rxType, ODO odo,
                                   String rxNo, String psyFlg) {
        TParm inParam = new TParm();
        String text = "TEXT";
        int printNum = OpdRxSheetTool.getInstance().getPrintNum(odo.getCaseNo(),rxNo);//��ӡ����
        inParam.setData("Print.fs",printNum>0?printNum:1);//Ĭ�ϴ�ӡ����
        inParam.setData("ADDRESS", text,
                        "OpdExaSheet From " + Operator.getID() + " To LPT1");
        inParam.setData("PRINT_TIME", text,
                        StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                             "yyyy/MM/dd HH:mm:ss"));
        inParam.setData("HOSP_NAME", text,
                        OpdRxSheetTool.getInstance().getHospFullName());
        inParam.setData("HOSP_NAME_ENG", text, Operator.getHospitalENGShortName());
        inParam.setData("SHEET_NAME", text,
                        OpdRxSheetTool.getInstance().getExaOrOpName(rxType));
        inParam.setData("ORG_CODE", text,
                        OpdRxSheetTool.getInstance().
                        getOrgName(odo.getCaseNo(), rxNo));
        inParam.setData("PAY_TYPE", text,
                        "�ѱ�:" +
                        OpdRxSheetTool.getInstance().
                        getPayTypeName(odo.getRegPatAdm().
                                       getItemString(0, "CTZ1_CODE")));
        inParam.setData("PAY_TYPE_ENG", text,
                        "Cate:" +
                        OpdRxSheetTool.getInstance().
                        getPayTypeEngName(odo.
                                          getRegPatAdm().getItemString(0, "CTZ1_CODE")));
        inParam.setData("MR_NO", text, "������:" + odo.getMrNo());
        inParam.setData("MR_NO_ENG", text, "Pat ID:" + odo.getMrNo());
        inParam.setData("PAT_NAME", text,
                        "����:" +
                        OpdRxSheetTool.getInstance().getPatName(odo.getMrNo()));
        inParam.setData("PAT_NAME_ENG", text,
                        "Name:" +
                        OpdRxSheetTool.getInstance().getPatEngName(odo.getMrNo()));
        inParam.setData("PAT_ID", text,
                        "�������֤�ţ�" +
                        OpdRxSheetTool.getInstance().getId(odo.getMrNo()));
        inParam.setData("SEX_CODE", text,
                        "�Ա�:" +
                        OpdRxSheetTool.getInstance().getSexName(odo.getMrNo()));
        inParam.setData("SEX_CODE_ENG", text,
                       "Gender:" + OpdRxSheetTool.getInstance().getSexEngName(odo.getMrNo()));
        inParam.setData("BIRTHDAY", text,
                        "��������:" +
                        OpdRxSheetTool.getInstance().getBirthDays(odo.getMrNo()));
        inParam.setData("BBB", text,
                        "Birthday:" +
                        OpdRxSheetTool.getInstance().getBirthDays(odo.getMrNo()));
        inParam.setData("AGE", text,
                        "����:" +
                        OpdRxSheetTool.getInstance().
                        getAgeName(odo.getCaseNo(), odo.getMrNo()));
        inParam.setData("AGE_ENG", text,
                        "Age:" +
                        OpdRxSheetTool.getInstance().
                        getAgeEngName(odo.getCaseNo(), odo.getMrNo()));
        inParam.setData("DEPT_CODE", text,
                        "�Ʊ�:" +
                        OpdRxSheetTool.getInstance().getDeptName(realDeptCode));
        inParam.setData("DEPT_CODE_ENG", text,
                        "Dept:" +
                        OpdRxSheetTool.getInstance().getDeptEngName(realDeptCode));
        inParam.setData("CLINIC_ROOM", text,
                        "���:" +
                        OpdRxSheetTool.getInstance().getClinicName(odo.getCaseNo()));
        inParam.setData("CLINIC_ROOM_ENG", text,
                        "Room:" +
                        OpdRxSheetTool.
                        getInstance().getClinicEngName(odo.getCaseNo()));
        inParam.setData("DR_CODE", text, "ҽ��:" + OpdRxSheetTool.getInstance().GetRegDr(odo.getCaseNo()));
        inParam.setData("DR_CODE_ENG", text, "M.D.:" + OpdRxSheetTool.getInstance().GetRegDrEng(odo.getCaseNo()));
        inParam.setData("ADM_DATE", text,
                        "����ʱ��:" +
                        OpdRxSheetTool.getInstance().getOrderDate(odo.getCaseNo()));
        inParam.setData("ADM_DATE_ENG", text,
                        "Date:" +
                        OpdRxSheetTool.getInstance().getOrderDate(odo.getCaseNo()));
        inParam.setData("SESSION_CODE", text,
                        "ʱ��:" +
                        OpdRxSheetTool.getInstance().getSessionName(odo.getCaseNo()));
        inParam.setData("SESSION_CODE_ENG", text,
                        "Time:" +
                        OpdRxSheetTool.getInstance().
                        getSessionEngName(odo.getCaseNo()));
        inParam.setData("FOOT_DR", text, "ҽʦ/M.D.:" + OpdRxSheetTool.getInstance().getDrName(odo.getCaseNo(),rxNo));
        inParam.setData("FOOT_DR_CODE", text,
                        "ҽʦ����/Phys code:" + OpdRxSheetTool.getInstance().getOpdOrder(odo.getCaseNo(),rxNo).
                                  getItemString(0, "DR_CODE"));
        inParam.setData("BAR_CODE", text, rxNo);
        inParam.setData("BALANCE", text,
                        "�������/Deduction(��):" + OpdRxSheetTool.getInstance().getArAmt(rxNo, odo.getCaseNo()));

        double amt = EKTIO.getInstance().getRxBalance(odo.getCaseNo(), rxNo);
        inParam.setData("CURRENT_AMT", text, "�����/Balance(��):" + amt);
        double orginalAmt = EKTIO.getInstance().getRxOriginal(odo.getCaseNo(), rxNo);
        inParam.setData("ORINGINAL_AMT", text, "��ԭ��/Amount(��):" + orginalAmt);
        inParam.setData("CHARGE", text, "���οۿ�/Charge(��):" + StringTool.round(orginalAmt-amt, 2));
        inParam.setData("DIAG", text, "�ٴ���ϣ�" + OpdRxSheetTool.getInstance().getIcdName(odo.getCaseNo()));
        inParam.setData("DIAG_ENG", text,
                        "Diagnosis:" + OpdRxSheetTool.getInstance().getIcdEngName(odo.getCaseNo()));
        inParam.setData("PRINT_NO_NO", text,
                        odo.getOpdOrder().getItemString(0, "PRINT_NO"));
//		System.out.println("printNoNo===="+odo.getOpdOrder().getItemString(0, "PRINT_NO"));
        inParam.setData("PRINT_NO", text, "��ҩ�ţ�");
        inParam.setData("PRINT_NO_ENG", text, "PrscrptNo:");
        String orgCode = odo.getOpdOrder().getItemString(0, "EXEC_DEPT_CODE");
        String counterDate = StringTool.getString(odo.getOpdOrder().getDBTime(),
                                                  "yyyyMMdd");
        String counterNo = odo.getOpdOrder().getItemInt(0, "COUNTER_NO") + "";
        TParm counterParm = OpdRxSheetTool.getInstance().getCounterNames(orgCode, counterDate, counterNo);
        String counterDesc = "";
        String counterEngDesc = "";
        if (counterParm != null) {
            counterDesc = counterParm.getValue("COUNTER_DESC", 0);
            counterEngDesc = counterParm.getValue("COUNTER_ENG_DESC", 0);
        }
        inParam.setData("COUNTER_NO", text, "��̨��");
        inParam.setData("COUNTER_NO_ENG", text, "Counter:" + counterEngDesc);
        inParam.setData("COUNTER_NO_NO", text, counterNo);
//		System.out.println("counterNoNo==="+counterDesc);
        String orderType = "",
            orderTypeCode = odo.getRegPatAdm().getItemString(0, "ADM_TYPE");

        //System.out.println("orderTypeCode=="+orderTypeCode);

        if ("O".equalsIgnoreCase(orderTypeCode)) {
            orderType = "�� Outpatient";
        }
        else if ("E".equalsIgnoreCase(orderTypeCode)) {
            orderType = "�� Emergency";
        }
//		System.out.println("birthday="+odo.getPatInfo().getItemTimestamp(0, "BIRTH_DATE"));
        String birthDate = odo.getPatInfo().getItemTimestamp(0, "BIRTH_DATE") +
            "";
        if ("2".equalsIgnoreCase(rxType)) {
            if ("1".equalsIgnoreCase(psyFlg)) {
                orderType = "������һ Poison��MHD A";
            }
            else if ("2".equalsIgnoreCase(psyFlg)) {
                orderType = "���� MHD B";
            }
        }
        else if (!StringUtil.isNullString(birthDate) &&
                 OPDSysParmTool.
                 getInstance().isChild(odo.getPatInfo().
                                       getItemTimestamp(0, "BIRTH_DATE"))) {
            orderType = "�� Pediatrics";
        }
        else if ("3".equalsIgnoreCase(rxType)) {
            orderType = "��ҽ Chn Med";
        }

        inParam.setData("ORDER_TYPE", text, orderType);
        inParam.setData("RX_NO", rxNo);
        inParam.setData("CASE_NO", odo.getCaseNo());
        inParam.setData("RX_TYPE", rxType);
        if (!"3".equalsIgnoreCase(rxType)) {
            return inParam;
        }

        String sql = "SELECT   ORDER_DESC,MEDI_QTY,AR_AMT,D.FREQ_TIMES||'��/'||D.CYCLE||'��' FREQ_CODE,A. TAKE_DAYS,"
            +
            "         B.CHN_DESC DCTAGENT_CODE,'' DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,E.ROUTE_CHN_DESC AS ROUTE_CODE,A.DCTAGENT_FLG,A.SEQ_NO"
            +
            "  FROM   OPD_ORDER A,SYS_DICTIONARY B, SYS_DICTIONARY C,SYS_PHAFREQ D,SYS_PHAROUTE E"
            + "  WHERE   RX_NO = '"
            + rxNo
            + "'"
            + "         AND B.GROUP_ID='PHA_DCTAGENT'"
            + "         AND A.DCTAGENT_CODE=B.ID"
            + "         AND C.GROUP_ID='PHA_DCTEXCEP'"
            + "         AND A.DCTEXCEP_CODE IS NULL"
            + "         AND A.FREQ_CODE=D.FREQ_CODE" +
            "         AND A.ROUTE_CODE=E.ROUTE_CODE" +
            " 	UNION" +
            "	SELECT   DISTINCT ORDER_DESC,MEDI_QTY,AR_AMT,D.FREQ_TIMES || '��/' || D.CYCLE || '��' FREQ_CODE,A.TAKE_DAYS," +
            "                  B.CHN_DESC DCTAGENT_CODE,C.CHN_DESC DCTEXCEP_CODE,DCT_TAKE_QTY,PACKAGE_TOT,E.ROUTE_CHN_DESC AS ROUTE_CODE,A.DCTAGENT_FLG,A.SEQ_NO" +
            "   FROM   OPD_ORDER A,SYS_DICTIONARY B,SYS_DICTIONARY C,SYS_PHAFREQ D,SYS_PHAROUTE E" +
            "   WHERE       RX_NO = '" + rxNo + "'" +
            "         AND B.GROUP_ID = 'PHA_DCTAGENT'" +
            "         AND A.DCTAGENT_CODE = B.ID" +
            "         AND C.GROUP_ID = 'PHA_DCTEXCEP'" +
            "         AND A.DCTEXCEP_CODE = C.ID" +
            "         AND A.FREQ_CODE = D.FREQ_CODE"+
            "         AND A.ROUTE_CODE=E.ROUTE_CODE"+
            "  ORDER BY SEQ_NO";
        //System.out.println("sql==============" + sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getErrCode() != 0) {
            //System.out.println("û������");
            return parm;
        }
        inParam.setData("FOOT_DR", text, "ҽʦ:" + OpdRxSheetTool.getInstance().GetRealRegDr(odo.getCaseNo()));
        //System.out.println("tableparm=========="+parm);
        int count = parm.getCount();
        text = "TEXT";
        double totAmtDouble = 0.0;
//		Systme.out.printl
        for (int i = 0; i < count; i++) {
            String row = (i / 4 + 1) + "";
            //System.out.println("row==========="+row);
            String column = (i % 4 + 1) + "";
            //System.out.println("olumn========"+column);
            totAmtDouble += parm.getDouble("TOT_AMT", i);
            inParam.setData("ORDER_DESC" + row + column, text, parm.getValue(
                "ORDER_DESC", i));
            inParam.setData("MEDI_QTY" + row + column, text, parm.getValue(
                "MEDI_QTY", i)
                            + "��");
            inParam.setData("DCTAGENT" + row + column, text, parm.getValue(
                "DCTEXCEP_CODE", i));
        }
        inParam.setData("TAKE_DAYS", text, "������"
                        + parm.getValue("TAKE_DAYS", 0) + "��");
        inParam.setData("FREQ_CODE", text, "��Σ�"
                        + parm.getValue("FREQ_CODE", 0));
        inParam.setData("ROUTE_CODE", text, "�÷���"
                        + parm.getValue("ROUTE_CODE", 0));
        inParam.setData("DCTAGENT_CODE", text, "������"
                        + parm.getValue("DCTAGENT_CODE", 0));
        inParam.setData("DCT_TAKE_QTY", text, "ÿ�η�������"
                        + parm.getValue("DCT_TAKE_QTY", 0) + "ml");
        inParam.setData("PACKAGE_TOT", text, "ÿ���ܿ�����"
                        + parm.getValue("PACKAGE_TOT", 0) + "��");
        if("Y".equals(parm.getValue("DCTAGENT_FLG",0)))
            inParam.setData("DCTAGENT_FLG", text, "��");
        return inParam;

    }
}

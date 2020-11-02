package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;

import jdo.adm.ADMInpTool;
import jdo.bil.BILIBSRecpmTool;
import jdo.bil.BILTool;
import jdo.ekt.EKTIO;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.text.DecimalFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: �վ��ٻ��˷Ѵ���</p>
 *
 * <p>Description: �վ��ٻ��˷Ѵ���</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILIBSRecpReturnDetailControl extends TControl {
    String caseNo = "";
    String sexCode = "";
    private TParm parmEKT;//ҽ�ƿ�����
    public void onInit() {
        super.onInit();
        TParm parm = (TParm)this.getParameter();
        setValue("RECEIPT_NO", parm.getValue("RECEIPT_NO", 0));
        setValue("MR_NO", parm.getValue("MR_NO", 0));
        setValue("IPD_NO", parm.getValue("IPD_NO", 0));
        setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));
        setValue("AR_AMT", parm.getValue("AR_AMT", 0));
        caseNo = parm.getValue("CASE_NO", 0);
        sexCode = parm.getValue("SEX_CODE", 0);
        callFunction("UI|EKT_SHOW|setVisible", false);

    }

    /**
     * �˷�
     */
    public void onSave() {
        TParm parm = new TParm();
        parm.setData("RECEIPT_NO", this.getValueString("RECEIPT_NO"));
        parm.setData("MR_NO", this.getValueString("MR_NO"));
        parm.setData("IPD_NO", this.getValueString("IPD_NO"));
        parm.setData("CASE_NO", caseNo);
        parm.setData("AR_AMT", this.getValueString("AR_AMT"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        if (this.getValueBoolean("FLG")){
            parm.setData("FLG", "Y");
            //====pangben 2016-1-21 �ײͲ���У��
            if(!ADMInpTool.getInstance().onCheckLumWorkReturn(caseNo)){
            	this.messageBox("�ײͻ��������˵�,�Ƚ�ĸ���ٻ�");
            	return;
            }
        }else
            parm.setData("FLG", "N");
        TParm actionParm = new TParm();
        actionParm.setData("DATA", parm.getData());
        TParm payTypeParm=BILIBSRecpmTool.getInstance().selectAllData(parm);
        if(payTypeParm.getCount()<=0){
        	this.messageBox("û�в�ѯ��Ʊ����Ϣ");
        	return;
        }
        //======pangben 2016-6-27 ���΢�š�֧������Ʊ���׺�¼��
        if(payTypeParm.getDouble("PAY_TYPE09",0)>0||payTypeParm.getDouble("PAY_TYPE10",0)>0){
        	TParm typeParm=new TParm();
        	if(payTypeParm.getDouble("PAY_TYPE09",0)>0){
        		typeParm.setData("WX_FLG","Y");
        		typeParm.setData("WX_AMT",payTypeParm.getDouble("PAY_TYPE09",0));
        		
        	}
        	if(payTypeParm.getDouble("PAY_TYPE10",0)>0){
        		typeParm.setData("ZFB_FLG","Y");
        		typeParm.setData("ZFB_AMT",payTypeParm.getDouble("PAY_TYPE10",0));
        	}
        	Object result = this.openDialog(
        	            "%ROOT%\\config\\bil\\BILPayTypeTransactionNo.x", typeParm, false);
        	if(null==result){
        		return;
        	}
        	actionParm.setData("PAY_TYPE_FLG","Y");//֧��������Ʊ��ӽ��׺�
        	actionParm.setData("PAY_TYPE_PARM",((TParm)result).getData());
        }
        boolean ektFlg = false;
        if(payTypeParm.getDouble("PAY_MEDICAL_CARD", 0) != 0){
        	if(parmEKT == null){
    			this.messageBox("���ȡҽ�ƿ���Ϣ");
    			return ;
    		}
    		if (!EKTIO.getInstance().ektSwitch()) {
    			messageBox_("ҽ�ƿ�����û������!");
    			return ;
    		}
    		
    		//�ɷ���ҵʱ�ۿ��¼
    		String ibsSql = "SELECT PAY_OTHER3,PAY_OTHER4 FROM EKT_TRADE WHERE TRADE_NO='"+payTypeParm.getValue("EKT_BUSINESS_NO", 0)+"'";
    		System.out.println("ibsSql----"+ibsSql);
    		TParm ibsParm = new TParm(TJDODBTool.getInstance().select(ibsSql));
    		System.out.println("ibsParm---"+ibsParm);
    		//��ѯ�ɷ���ҵʱ��Ԥ�������������Ԥ��������ҽ�ƿ�֧��������
    		String bilSql = "SELECT PAY_OTHER3,PAY_OTHER4 FROM EKT_TRADE" +
    				" WHERE TRADE_NO IN (SELECT BUSINESS_NO FROM BIL_PAY" +
    				" WHERE RESET_RECP_NO = '"+this.getValueString("RECEIPT_NO")+"'" +
    				" AND CASE_NO = '"+caseNo+"'" +
    				" AND BUSINESS_NO IS NOT NULL" +
    				" AND PAY_TYPE = 'EKT')";
    		TParm bilParm = new TParm(TJDODBTool.getInstance().select(bilSql));
    		double payOther3 = 0;
    		double payOther4 = 0;
    		if(ibsParm.getCount() > 0){
    			payOther3 += ibsParm.getDouble("PAY_OTHER3", 0);
    			payOther4 += ibsParm.getDouble("PAY_OTHER4", 0);
    		}
    		if(bilParm.getCount() > 0){
    			payOther3 += bilParm.getDouble("PAY_OTHER3", 0);
    			payOther4 += bilParm.getDouble("PAY_OTHER4", 0);
    		}
    		
    		TParm parmE = new TParm();
    		parmE.setData("READ_CARD", parmEKT.getData());
    		parmE.setData("PAY_OTHER3",payOther3);
    		parmE.setData("PAY_OTHER4",payOther4);
    		parmE.setData("EXE_AMT",-this.getValueDouble("AR_AMT"));
    		parmE.setData("MR_NO",this.getValueString("MR_NO"));
    		parmE.setData("BUSINESS_TYPE","IBS");
    		parmE.setData("CASE_NO",caseNo);//���׺�
    		Object r =  this.openDialog("%ROOT%\\config\\ekt\\EKTChageOtherUI.x",parmE);
    		if(r == null){
    			this.messageBox("ҽ�ƿ��ۿ�ȡ������ִ�б���");
    			return ;
    		}
    		TParm rParm = (TParm) r;
    		if(rParm.getErrCode() < 0){
    			this.messageBox(rParm.getErrText());
    			return ;
    		}else if(rParm.getValue("OP_TYPE").equals("2")){
    			return ;
    		}else{
    			actionParm.setData("ektSql", rParm.getData("ektSql"));
    			ektFlg = true;
    		}
        }
        
        TParm result = TIOM_AppServer.executeAction("action.bil.BILAction",
                "onSaveReceiptReturn", actionParm);
        
        if(ektFlg){
        	 this.setReturnValue("true");
        	 this.closeWindow();
        }
        
        String preAmt = result.getValue("PRE_AMT");
        String payType = result.getValue("PAY_TYPE");
        String payTypeName= result.getValue("PAY_TYPE_NAME");//====pangben 2014-6-24 ��ʾ����
        String invNo = result.getValue("INV_NO");
        String stationCode = result.getValue("STATION_CODE");
        String deptCode = result.getValue("DEPT_CODE");
        String recpNo = result.getValue("RECEIPT_NO");
//        System.out.println("�س�Ԥ�������" + preAmt + "|" + invNo + "|" + recpNo +
//                           "|" + stationCode + "|" + deptCode);
        if (recpNo.length() > 0)
            printBillPay(preAmt, payTypeName, recpNo, stationCode, deptCode);
        this.messageBox("Ʊ��" + result.getValue("INV_NO") + "���ջ�");
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return;
        } else
            this.setReturnValue("true");
        this.closeWindow();
    }

    /**
     * �س��Ʊ
     * @param preAmt String
     * @param payType String
     * @param recpNo String
     * @param stationCode String
     * @param deptCode String
     */
    public void printBillPay(String preAmt, String payType, String recpNo,
                             String stationCode, String deptCode) {
        //��ӡԤ�����վ�
        TParm forPrtParm = new TParm();
        String bilPayC = StringUtil.getInstance().numberToWord(TypeTool.
                getDouble(preAmt));
        forPrtParm.setData("TITLE", "TEXT", "סԺԤ�����վ�");
        Timestamp printDate = (SystemTool.getInstance().getDate());
        String pDate = StringTool.getString(printDate, "yyyy/MM/dd HH:mm:ss");
        forPrtParm.setData("COPY", "TEXT", "");
        forPrtParm.setData("REGION_CHN_DESC", "TEXT",
                           Operator.getHospitalCHNFullName());
        forPrtParm.setData("REGION_ENG_DESC", "TEXT",
                           Operator.getHospitalENGFullName());
        forPrtParm.setData("Data", "TEXT",pDate);
        forPrtParm.setData("Name", "TEXT",
                           this.getValueString("PAT_NAME"));
        forPrtParm.setData("TOLL_COLLECTOR", "TEXT", Operator.getName());
        forPrtParm.setData("MR_N0", "TEXT",
                           this.getValueString("MR_NO"));
        forPrtParm.setData("IPD_NO", "TEXT",
                           "סԺ��:" + this.getValueString("IPD_NO"));
        if (sexCode.equals("1"))
            forPrtParm.setData("SEX", "TEXT", "�Ա�:" + "��");
        else if (sexCode.equals("2"))
            forPrtParm.setData("SEX", "TEXT", "�Ա�:" + "Ů");
        else
            forPrtParm.setData("SEX", "TEXT", "�Ա�:" + "����");
        forPrtParm.setData("DEPT", "TEXT",
                           getDeptDesc(deptCode));
        forPrtParm.setData("STATION", "TEXT",
                           getStationDesc(stationCode));
        forPrtParm.setData("Capital", "TEXT", bilPayC);
        DecimalFormat formatObject = new DecimalFormat("###########0.00");

        forPrtParm.setData("SmallCaps", "TEXT",
                           formatObject.format(
                                   TypeTool.getDouble(preAmt))+"Ԫ");
//        forPrtParm.setData("SEQ_NO", "TEXT",
//               "��ˮ��:"+ recpNo);
        forPrtParm.setData("RECEIPT_NO", "TEXT",
                            recpNo);
        forPrtParm.setData("PRINT_DATE", "TEXT", "��ӡ����:" + pDate);
//        if (payType.equals("C0")) {
//            forPrtParm.setData("CASH", "TEXT", "��");
//            forPrtParm.setData("BANK", "TEXT", "");
//            forPrtParm.setData("OTHERS", "TEXT", "");
//        } else if (payType.equals("C1")) {
//            forPrtParm.setData("CASH", "TEXT", "");
//            forPrtParm.setData("BANK", "TEXT", "��");
//            forPrtParm.setData("OTHERS", "TEXT", "");
//        } else {
//            forPrtParm.setData("CASH", "TEXT", "");
//            forPrtParm.setData("BANK", "TEXT", "");
//            forPrtParm.setData("OTHERS", "TEXT", "��");
//        }
        payType +="��" + formatObject.format(TypeTool.getDouble(preAmt))+"Ԫ";
        forPrtParm.setData("WAY","TEXT",payType) ;//====pangben 2014-4-15 ֧����ʽ��ʾ��ȷ

//        System.out.println("�س�Ԥ����" + forPrtParm);
//        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPrepayment.jhw",
//                             forPrtParm);
        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPrepayment_V45.jhw",
                forPrtParm);
    }

    /**
     * �õ���������
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode) {
        String deptDesc = "";
        String selDept =
                " SELECT DEPT_CHN_DESC " +
                "   FROM SYS_DEPT " +
                "  WHERE DEPT_CODE = '" + deptCode + "' ";
        //��ѯ��������
        TParm selDeptParm = new TParm(TJDODBTool.getInstance().select(
                selDept));
        deptDesc = selDeptParm.getValue("DEPT_CHN_DESC", 0);
        return deptDesc;
    }

    /**
     * �õ���������
     * @param stationCode String
     * @return String
     */
    public String getStationDesc(String stationCode) {
        String stationDesc = "";
        String selStation =
                " SELECT STATION_DESC " +
                "   FROM SYS_STATION " +
                "  WHERE STATION_CODE = '" + stationCode + "' ";
        //��ѯ��������
        TParm selStationParm = new TParm(TJDODBTool.getInstance().select(
                selStation));
        stationDesc = selStationParm.getValue("STATION_DESC", 0);
        return stationDesc;
    }
    /**
     * ҽ�ƿ�����
     * huangtt 20140318
     */
    public void onEKTcard(){
        //��ȡҽ�ƿ�
        parmEKT = EKTIO.getInstance().TXreadEKT();
        if (null == parmEKT || parmEKT.getErrCode() < 0 ||
            parmEKT.getValue("MR_NO").length() <= 0) {
            this.messageBox(parmEKT.getErrText());
            parmEKT = null;
            return;
        }
        if(!parmEKT.getValue("MR_NO").equals(this.getValueString("MR_NO"))){
        	this.messageBox("ҽ�ƿ���Ϣ�벡����Ϣ����");
            return; 
        }
        callFunction("UI|EKT_SHOW|setVisible", true);  
        
        
    }

}


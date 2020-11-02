package jdo.opb;

import com.dongyang.data.TModifiedList;
import jdo.sys.Pat;
import jdo.reg.Reg;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

import jdo.opd.PrescriptionList;
import jdo.opd.OrderList;
import java.util.Map;
import java.util.HashMap;
import jdo.bil.BIL;
import jdo.sys.SYSPublic;
import com.dongyang.util.StringTool;
import java.text.DecimalFormat;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: Ʊ��list</p> 
 *
 * <p>Description: </p>
 *
 * Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author fudw
 * @version 1.0
 */

public class OPBReceiptList
    extends TModifiedList {
    //ȡ��ǰʱ��
    Timestamp date = SystemTool.getInstance().getDate();
    /**
     * Ʊ�ݵ�
     */
    private OPBReceipt receiptOne;
    private Pat pat;
    private Reg reg;
    public void setPat(Pat pat){
    this.pat=pat;
    }
    public Pat getPat(){
    return this.pat;
    }
    public void setReg(Reg reg){
    this.reg = reg;
    }
    public Reg getReg(){
    return this.reg;
    }

    /**
     * ����һ��Ʊ��
     * @param OpbReceipt OPBReceipt
     */
    public void setOPBReceipt(OPBReceipt OpbReceipt) {
        this.receiptOne = OpbReceipt;
    }

    /**
     * �õ�һ��Ʊ��
     * @return OPBReceipt
     */
    public OPBReceipt getOPBReceipt() {
        return this.receiptOne;
    }


    /**
     * ������
     */
    public OPBReceiptList() {
        setMapString("caseNo:CASE_NO;receiptNo:RECEIPT_NO;admType:ADM_TYPE;region:REGION_CODE;mrNo:MR_NO;resetReceiptNo:RESET_RECEIPT_NO;"
                     + "printNo:PRINT_NO;billDate:BILL_DATE;chargeDate:CHARGE_DATE;printDate:PRINT_DATE;charge01:CHARGE01;charge02:CHARGE02;"
                     + "charge03:CHARGE03;charge04:CHARGE04;charge05:CHARGE05;charge06:CHARGE06;charge07:CHARGE07;charge08:CHARGE08;charge09:CHARGE09;"
                     + "charge10:CHARGE10;charge11:CHARGE11;charge12:CHARGE12;charge13:CHARGE13;charge14:CHARGE14;charge15:CHARGE15;"
                     + "charge16:CHARGE16;charge17:CHARGE17;charge18:CHARGE18;charge19:CHARGE19;charge20:CHARGE20;charge21:CHARGE21;"
                     + "charge22:CHARGE22;charge23:CHARGE23;charge24:CHARGE24;charge25:CHARGE25;charge26:CHARGE26;charge27:CHARGE27;"
                     + "charge28:CHARGE28;charge29:CHARGE29;charge30:CHARGE30;totAmt:TOT_AMT;reduceReason:REDUCE_REASON;reduceAmt:REDUCE_AMT;"
                     + "reduceTime:REDUCE_DATE;reduceDeptCode:REDUCE_DEPT_CODE;reduceRespond:REDUCE_RESPOND;arAmt:AR_AMT;payCash:PAY_CASH;"
                     + "payMedicalCard:PAY_MEDICAL_CARD;payBankCard:PAY_BANK_CARD;payInsCard:PAY_INS_CARD;payCheck:PAY_CHECK;"
                     + "payDepit:PAY_DEBIT;payBilPay:PAY_BILPAY;payIns:PAY_INS;payOther1:PAY_OTHER1;payOther2:PAY_OTHER2;payRemark:PAY_REMARK;"
                     + "cashierCode:CASHIER_CODE;accountFlg:ACCOUNT_FLG;accountDate:ACCOUNT_DATE;accountUser:ACCOUNT_USER;"
                     + "contractCode:ACCOUNT_CODE;bankSeq:BANK_SEQ;bankDate:BANK_DATE;bankUser:BANK_USER;optUser:OPT_USER;"
                     + "optDate:OPT_DATE;optTerm:OPT_TERM;");
    }

    /**
     * ����Ʊ��
     * @return OPBReceipt
     */
    public OPBReceipt newReceipt() {
        OPBReceipt newReceipt = new OPBReceipt();
        //�������
        newReceipt.setCaseNo(reg.caseNo());
        //Ʊ�ݺ�
//                newReceipt.setReceiptNo();
        //�ż�ס��
        newReceipt.setAdmType(reg.getAdmType());
        //����
        newReceipt.setRegion(Operator.getRegion());

        //������
        newReceipt.setMrNo(pat.getMrNo());
        //����վݺ�
//                newReceipt.setResetReceiptNo();
        //��ӡ��Ʊ�ݺ�
//                newReceipt.setPrintNo();
        //�շ�ʱ��
        newReceipt.setBilDate(date);
        //�Ʒ�ʱ��
        newReceipt.setChargeDate(date);
        //��Ʊʱ��
        newReceipt.setPrintDate(date);
        //����1~~30Ĭ��ȫ0
        newReceipt.setCharge01(0);
        newReceipt.setCharge02(0);
        newReceipt.setCharge03(0);
        newReceipt.setCharge04(0);
        newReceipt.setCharge05(0);
        newReceipt.setCharge06(0);
        newReceipt.setCharge08(0);
        newReceipt.setCharge09(0);
        newReceipt.setCharge10(0);
        newReceipt.setCharge11(0);
        newReceipt.setCharge12(0);
        newReceipt.setCharge13(0);
        newReceipt.setCharge14(0);
        newReceipt.setCharge15(0);
        newReceipt.setCharge16(0);
        newReceipt.setCharge17(0);
        newReceipt.setCharge18(0);
        newReceipt.setCharge19(0);
        newReceipt.setCharge20(0);
        newReceipt.setCharge21(0);
        newReceipt.setCharge22(0);
        newReceipt.setCharge23(0);
        newReceipt.setCharge24(0);
        newReceipt.setCharge25(0);
        newReceipt.setCharge26(0);
        newReceipt.setCharge27(0);
        newReceipt.setCharge28(0);
        newReceipt.setCharge29(0);
        newReceipt.setCharge30(0);
        //�ܽ��
        newReceipt.setTotAmt(0);
//                newReceipt.setReduceDeptCode();
//                newReceipt.setReduceReason();
        //�ۿ۽��
        newReceipt.setReduceAmt(0);
        newReceipt.setReduceDate(date);
//                newReceipt.setReduceRespond();
        //�Էѽ��
        newReceipt.setArAmt(0);
        //�ֽ�
        newReceipt.setPayCash(0);
        //ҽ�ƿ�
        newReceipt.setPayMedicalCard(0);
        //ˢ��
        newReceipt.setPayBankCard(0);
        //ҽ����
        newReceipt.setPayInsCard(0);
        //֧Ʊ
        newReceipt.setPayCheck(0);
        //����
        newReceipt.setPayDepit(0);
        //Ѻ��
        newReceipt.setPayBilPay(0);
        //ҽ��
        newReceipt.setPayIns(0);
        //����֧��1
        newReceipt.setPayOther1(0);
        //����֧��2
        newReceipt.setPayOther2(0);
//                newReceipt.setPayRemark();
        newReceipt.setCashierCode(Operator.getID());
        newReceipt.setOptUser(Operator.getID());
        newReceipt.setOptDate(date);
        newReceipt.setOptTerm(Operator.getIP());
        //���������һ���µ�Ʊ��
        add(newReceipt);
        return newReceipt;
    }

    /**
     * ���ݴ����tparm�齨Ʊ��list
     * @param parm TParm
     * @return OPBReceipt
     */
    public OPBReceiptList InitOpbReceiptList(TParm parm) {
        int count = parm.getCount();
        //���������
        if (parm.getCount() <= 0)
            return null;
        OPBReceiptList opbReceiptList=new OPBReceiptList();
        //ѭ����������
        for (int i = 0; i < count; i++) {
            //�½�Ʊ�ݶ���
            OPBReceipt opbReceiptOne =getReceiptByTParm(parm, i);
            //��list���һ������
            opbReceiptList.add(i,opbReceiptOne);
        }

    return opbReceiptList;
    }
    /**
     * ����һ��TParm����һ��Ʊ�ݶ���
     * @param parm TParm
     * @param row int
     * @return OPBReceipt
     */
    public OPBReceipt getReceiptByTParm(TParm parm, int row) {
        OPBReceipt opbReceiptOne = new OPBReceipt();
        //�������
        opbReceiptOne.setCaseNo(parm.getValue("CASE_NO", row));
        //Ʊ�ݺ�
        opbReceiptOne.setReceiptNo(parm.getValue("RECEIPT_NO", row));
        //�ż�ס��
        opbReceiptOne.setAdmType(parm.getValue("ADM_TYPE", row));
        //����
        opbReceiptOne.setRegion(parm.getValue("REGION", row));
        //������
        opbReceiptOne.setMrNo(parm.getValue("MR_NO", row));
        //����վݺ�
        opbReceiptOne.setResetReceiptNo(parm.getValue("RESET_RECEIPT_NO", row));
        //��ӡ��Ʊ�ݺ�
        opbReceiptOne.setPrintNo(parm.getValue("PRINT_NO", row));
        //�շ�ʱ��
        opbReceiptOne.setBilDate(parm.getTimestamp("BILL_DATE", row));
        //�Ʒ�ʱ��
        opbReceiptOne.setChargeDate(parm.getTimestamp("CHARGE_DATE", row));
        //��Ʊʱ��
        opbReceiptOne.setPrintDate(parm.getTimestamp("PRINT_DATE", row));
        //�շ���Ŀ��ϸ1~30
        opbReceiptOne.setCharge01(parm.getDouble("CHARGE01", row));
        opbReceiptOne.setCharge02(parm.getDouble("CHARGE02", row));
        opbReceiptOne.setCharge03(parm.getDouble("CHARGE03", row));
        opbReceiptOne.setCharge04(parm.getDouble("CHARGE04", row));
        opbReceiptOne.setCharge05(parm.getDouble("CHARGE05", row));
        opbReceiptOne.setCharge06(parm.getDouble("CHARGE06", row));
        opbReceiptOne.setCharge07(parm.getDouble("CHARGE07", row));
        opbReceiptOne.setCharge08(parm.getDouble("CHARGE08", row));
        opbReceiptOne.setCharge09(parm.getDouble("CHARGE09", row));
        opbReceiptOne.setCharge10(parm.getDouble("CHARGE10", row));
        opbReceiptOne.setCharge11(parm.getDouble("CHARGE11", row));
        opbReceiptOne.setCharge12(parm.getDouble("CHARGE12", row));
        opbReceiptOne.setCharge13(parm.getDouble("CHARGE13", row));
        opbReceiptOne.setCharge14(parm.getDouble("CHARGE14", row));
        opbReceiptOne.setCharge15(parm.getDouble("CHARGE15", row));
        opbReceiptOne.setCharge16(parm.getDouble("CHARGE16", row));
        opbReceiptOne.setCharge17(parm.getDouble("CHARGE17", row));
        opbReceiptOne.setCharge18(parm.getDouble("CHARGE18", row));
        opbReceiptOne.setCharge19(parm.getDouble("CHARGE19", row));
        opbReceiptOne.setCharge20(parm.getDouble("CHARGE20", row));
        opbReceiptOne.setCharge21(parm.getDouble("CHARGE21", row));
        opbReceiptOne.setCharge22(parm.getDouble("CHARGE22", row));
        opbReceiptOne.setCharge23(parm.getDouble("CHARGE23", row));
        opbReceiptOne.setCharge24(parm.getDouble("CHARGE24", row));
        opbReceiptOne.setCharge25(parm.getDouble("CHARGE25", row));
        opbReceiptOne.setCharge26(parm.getDouble("CHARGE26", row));
        opbReceiptOne.setCharge27(parm.getDouble("CHARGE27", row));
        opbReceiptOne.setCharge28(parm.getDouble("CHARGE28", row));
        opbReceiptOne.setCharge29(parm.getDouble("CHARGE29", row));
        opbReceiptOne.setCharge30(parm.getDouble("CHARGE30", row));
        //TOT_AMT;REDUCE_AMT;AR_AMT;PAY_CASH;PAY_MEDICAL_CARD;
        //PAY_BANK_CARD;PAY_INS_CARD;PAY_CHECK;PAY_DEBIT;PAY_BILPAY;PAY_INS;PAY_OTHER1;PAY_OTHER2;CASHIER_CODE
        //����֧����ʽ
        opbReceiptOne.setTotAmt(parm.getDouble("TOT_AMT",row));
        opbReceiptOne.setReduceAmt(parm.getDouble("REDUCE_AMT",row));
        opbReceiptOne.setArAmt(parm.getDouble("AR_AMT",row));
        opbReceiptOne.setPayCash(parm.getDouble("PAY_CASH",row));
        opbReceiptOne.setPayMedicalCard(parm.getDouble("PAY_MEDICAL_CARD",row));
        opbReceiptOne.setPayBankCard(parm.getDouble("PAY_BANK_CARD",row));
        opbReceiptOne.setPayInsCard(parm.getDouble("PAY_INS_CARD",row));
        opbReceiptOne.setPayCheck(parm.getDouble("PAY_CHECK",row));
        opbReceiptOne.setPayDepit(parm.getDouble("PAY_DEBIT",row));
        opbReceiptOne.setPayBilPay(parm.getDouble("PAY_BILPAY",row));
        opbReceiptOne.setPayIns(parm.getDouble("PAY_INS",row));
        opbReceiptOne.setPayOther1(parm.getDouble("PAY_OTHER1",row));
        opbReceiptOne.setPayOther2(parm.getDouble("PAY_OTHER2",row));
        //֧Ʊ��
        opbReceiptOne.setPayRemark(parm.getValue("PAY_REMARK", row));
        //�շ�Ա
        opbReceiptOne.setCashierCode(parm.getValue("CASHIER_CODE", row));
        //�ս���
        opbReceiptOne.setAccountFlg(parm.getValue("ACCOUNT_FLG", row));
        //�ս�����
        opbReceiptOne.setAccountDate(parm.getTimestamp("ACCOUNT_DATE", row));
        //�ս���Ա
        opbReceiptOne.setAccountUser(parm.getValue("ACCOUNT_USER", row));
        //
        opbReceiptOne.setContractCode(parm.getValue("CONTRACT_CODE", row));
        //���н��˺�
        opbReceiptOne.setBankSeq(parm.getValue("BANK_SEQ", row));
        //����ʱ��
        opbReceiptOne.setBankDate(parm.getTimestamp("BANK_DATE", row));
        //������Ա
        opbReceiptOne.setBankUser(parm.getValue("BANK_USER", row));
        return opbReceiptOne;
    }

    /**
     *  ɾ��һ��Ʊ��
     * @param row int
     */
    public void deleteReceipt(int row) {
        this.removeData(row);

    }

    /**
     * ��ʼ�������б�
     * @param caseNo String
     * @return OPBReceiptList
     */
    public OPBReceiptList initReceiptList(String caseNo){
        //��ȡ�շ�����
        TParm result=OPBReceiptTool.getInstance().getReceipt(caseNo);
        if(result.getErrCode()<0)
            return null;
        int row=result.getCount();
        if(row<=0)
            return null;
        return InitOpbReceiptList(result);
    }
    /**
     * ��ʼ�������б�:��ѯ������Ʊ���б����˲��ң�û��ִ�н�������������˷�
     * @param caseNo String
     * @return OPBReceiptList
     */
    public OPBReceiptList initContractReceiptList(String caseNo){
        //��ȡ�շ�����
        TParm result=OPBReceiptTool.getInstance().getContractReceipt(caseNo);
        if(result.getErrCode()<0)
            return null;
        int row=result.getCount();
        if(row<=0)
            return null;
        return InitOpbReceiptList(result);
    }

    /**
     * Ʊ�ݷ�Ʊ
     * @param prescriptionList PrescriptionList
     */
    public void initOrder(PrescriptionList prescriptionList) {
        int count=this.size();
        for(int i=0;i<count;i++){
            OPBReceipt opbReceipt=(OPBReceipt)this.get(i);
            opbReceipt.initOrderList(prescriptionList);
        }
    }
    /**
     * �õ������嵥
     * @return TParm
     */
    public TParm getOrderParm(){
        //��¼�ܵ�parm
        TParm parm=new TParm();
        parm.setCount(0);
        //ÿ��Ʊ���ϵ�orderparm
        TParm parmOrder;
        //Ʊ������
        int count=this.size();
        for(int i=0;i<count;i++){
            //˳��ȡ�����е�Ʊ��
            OPBReceipt opbReceipt=(OPBReceipt)this.get(i);
            //�õ����е�parm
            parmOrder = opbReceipt.getOrderList().getParm(OrderList.PRIMARY);
            parm=addNewRow(parm,parmOrder);
        }
        return parm;
    }
    /**
     * ��һ��parm�е��������ݷ�����һ��parm
     * @param parmMain TParm
     * @param parmDetial TParm
     * @return TParm
     */
    public TParm addNewRow(TParm parmMain,TParm parmDetial){
        //��Parma������
        int rowMain=parmMain.getCount();
        //��ϸparm������
        int rowDetial=parmDetial.getCount();
        for(int i=0;i<rowDetial;i++){
            //���е�������һ�����һ����ϸparm�ĵ�i��
            parmMain.addRowData(parmDetial,i);
        }
        parmMain.setCount(rowMain+rowDetial);
        return parmMain;
    }
    /**
     * ����ҽ���б�..��Ϊ�˷����嵥ʹ��
     *   ��Ҫ�ǰ�����������
     * @param parmIn TParm
     * @return TParm
     */
    public TParm dealTParm(TParm parmIn,TParm batchParm){
        BIL bil=new BIL();
        //��������������ƵĶ���
        Map chargeToRexpMap=bil.getChargeToRexpdDescMap();
        if(chargeToRexpMap==null)
            return null;
        //�õ���λ����map
        Map unitMap=SYSPublic.getUnitMap();
        //�洢������ɺ������
        TParm parmOut=new TParm();
        //�洢�շ����
        Map rexpMap=new HashMap();
        //������������
        int rowCount=parmIn.getCount();
        int count = rowCount;//���ô�ӡ���� add by sunqy 20140625
        DecimalFormat df = new DecimalFormat("0.00");
        double sumSubS = 0.00;//С��Ӧ��
        double sumSubR = 0.00;//С��ʵ��
        double sumTotS = 0.00;//�ܼ�Ӧ��
        double sumTotR = 0.00;//�ܼ�ʵ��
        //modify by sunqy 20140625 ----start----
        String code = "";//��¼ÿ���շ����
        String rexpCode = "";//yanjing 20141225 modify
        double reduceAmt = 0.00;//yanjing 20141225 modify
       for(int j=0;j<rowCount;j++){
//           int row=parmOut.insertRow();
//           System.out.println("rowcount======"+rowCount);
//           System.out.println("�շ����"+chargeToRexpMap.get(parmIn.getData("REXP_CODE",j)));
    	   if (parmIn.getDouble("AR_AMT", j) == 0)
               continue;
           if (!code.equals(chargeToRexpMap.get(parmIn.getData("REXP_CODE",j)))){
        	   if(j!=0){//�ж���������շ������ͬһ�־����������һ��С�ƣ����Ӧ��ʵ�ս��
        		   TParm reduceParm = new TParm();//====yanjing 20141224 modify
//					parmOut.addData("REXP_CODE", "");
//					parmOut.addData("SPECIFICATION", "");
//					parmOut.addData("UNIT_CHN_DESC", "");
//					parmOut.addData("OWN_PRICE", "");
//					parmOut.addData("DOSAGE_QTY", "С��:");
//					parmOut.addData("TOT_AMT", df.format(sumSubS));
//					parmOut.addData("AR_AMT", df.format(sumSubR));
//					sumTotS += sumSubS;
//					sumTotR += sumSubR;
//					sumSubS = 0;
//					sumSubR = 0;
//					count++;
					reduceParm = selectReduceAmt(parmIn.getValue("CASE_NO",j),parmIn.getValue("RECEIPT_NO",j),rexpCode);
					if(reduceParm.getDouble("REDUCE_AMT", 0)!=0){//===�м����ʱ����Ӽ���Ľ��
						parmOut.addData("REXP_CODE", "");
						parmOut.addData("SPECIFICATION", "");
						parmOut.addData("UNIT_CHN_DESC", "");
						parmOut.addData("OWN_PRICE", "");
						parmOut.addData("DOSAGE_QTY", "����:");
						parmOut.addData("TOT_AMT", "");
						parmOut.addData("AR_AMT",-reduceParm.getDouble("REDUCE_AMT", 0) );
						reduceAmt+=StringTool.round(reduceParm.getDouble("REDUCE_AMT", 0), 2);
						count++;
					}
					parmOut.addData("REXP_CODE", "");
					parmOut.addData("SPECIFICATION", "");
					parmOut.addData("UNIT_CHN_DESC", "");
					parmOut.addData("OWN_PRICE", "");
					parmOut.addData("DOSAGE_QTY", "С��:");
					parmOut.addData("TOT_AMT", df.format(sumSubS));
					if(reduceParm.getDouble("REDUCE_AMT", 0)!=0){
						parmOut.addData("AR_AMT", df.format(sumSubR-reduceParm.getDouble("REDUCE_AMT", 0)));
					}else{
						parmOut.addData("AR_AMT", df.format(sumSubR));
					}
					sumTotS += sumSubS;
					sumTotR += sumSubR;
					sumSubS = 0;
					sumSubR = 0;
					count++;
        	   }
        	   //�շ����
        	   rexpCode = parmIn.getValue("REXP_CODE",j);
        	   code = (String) chargeToRexpMap.get(parmIn.getData("REXP_CODE",j));
               parmOut.addData("REXP_CODE",chargeToRexpMap.get(parmIn.getData("REXP_CODE",j)));
           }else{
        	   parmOut.addData("REXP_CODE",""); 
           }
           //��Ŀ����/���
           if(parmIn.getValue("SPECIFICATION",j).length()==0){
        	   parmOut.addData("SPECIFICATION",parmIn.getValue("ORDER_DESC",j));
           }else{
        	   parmOut.addData("SPECIFICATION",(OPB.getPrescriptionFlg(parmIn.getValue("ORDER_CODE",j))? "��":"") +   //add by huangtt 20150520
        			   parmIn.getValue("ORDER_DESC",j)+"/"+parmIn.getValue("SPECIFICATION",j) ); 
           }
           //��λ
           parmOut.addData("UNIT_CHN_DESC",TypeTool.getString(unitMap.get(parmIn.getValue("DISPENSE_UNIT",j)))); //DOSAGE_UNIT modify by DISPENSE_UNIT  huangtt 20141118
           //����
           parmOut.addData("OWN_PRICE",df.format(StringTool.round(parmIn.getDouble("OWN_PRICE",j),2)));
           //����
           parmOut.addData("DOSAGE_QTY",parmIn.getData("DISPENSE_QTY",j));
           //Ӧ�ս��
           parmOut.addData("TOT_AMT",df.format(StringTool.round(parmIn.getDouble("OWN_AMT",j),2)));
           //ʵ�ս��
           parmOut.addData("AR_AMT",df.format(StringTool.round(parmIn.getDouble("AR_AMT",j),2)));
           sumSubS += StringTool.round(parmIn.getDouble("OWN_AMT",j),2);
           sumSubR += StringTool.round(parmIn.getDouble("AR_AMT",j),2);
       }
       sumTotS += sumSubS;
       sumTotR += sumSubR;
       TParm reduceParm1 = new TParm();
       reduceParm1 = selectReduceAmt(parmIn.getValue("CASE_NO",rowCount-1),parmIn.getValue("RECEIPT_NO",rowCount-1),rexpCode);
       if(reduceParm1.getDouble("REDUCE_AMT", 0)!=0){//===�м����ʱ����Ӽ���Ľ��
			parmOut.addData("REXP_CODE", "");
			parmOut.addData("SPECIFICATION", "");
			parmOut.addData("UNIT_CHN_DESC", "");
			parmOut.addData("OWN_PRICE", "");
			parmOut.addData("DOSAGE_QTY", "����:");
			parmOut.addData("TOT_AMT", "");
			parmOut.addData("AR_AMT",-reduceParm1.getDouble("REDUCE_AMT", 0) );
			reduceAmt+=StringTool.round(reduceParm1.getDouble("REDUCE_AMT", 0),2);
			count++;
		}
       	parmOut.addData("REXP_CODE", "");
		parmOut.addData("SPECIFICATION", "");
		parmOut.addData("UNIT_CHN_DESC", "");
		parmOut.addData("OWN_PRICE", "");
		parmOut.addData("DOSAGE_QTY", "С��:");
		parmOut.addData("TOT_AMT", df.format(sumSubS));
		if(reduceParm1.getDouble("REDUCE_AMT", 0)!=0){//===�м����ʱ����Ӽ���Ľ��
			parmOut.addData("AR_AMT", df.format(sumSubR-reduceParm1.getDouble("REDUCE_AMT", 0)));
		}else{
			parmOut.addData("AR_AMT", df.format(sumSubR));
		}
		count++;
        //�ж��Ƿ��м��������д�ӡ����
        if(reduceAmt != 0 ){
        	parmOut.addData("REXP_CODE", "");
       		parmOut.addData("SPECIFICATION", "");
       		parmOut.addData("UNIT_CHN_DESC", "");
       		parmOut.addData("OWN_PRICE", "");
       		parmOut.addData("DOSAGE_QTY", "�ܼ���:");
       		parmOut.addData("TOT_AMT", "");
       		parmOut.addData("AR_AMT", df.format(-reduceAmt));
       		sumSubR += StringTool.round(-reduceAmt,3);
       		sumTotR += StringTool.round(-reduceAmt, 3);
       		count++;
        }
        
      //modify by sunqy 20140625 ----end----
//        for(int i=0;i<rowCount;i++){
//        	//ȡ���շ����
//        	String rexpCode=parmIn.getValue("REXP_CODE",i);
//        	if(rexpMap.get(rexpCode)==null){
//        		rexpMap.put(rexpCode,"Y");
//        		for(int j=i;j<rowCount;j++){
//        			String rexpCodeNew=parmIn.getValue("REXP_CODE",j);
//        			if(rexpCodeNew.equals(rexpCode)){
//        				int row=parmOut.insertRow();
//        				//===============pangben 20111014 start
//        				//�շ���Ŀ
////                       parmOut.setData("REXP_CODE",row,chargeToRexpMap.get(parmIn.getData("REXP_CODE",j)));
//        				//���� ���
//        				boolean istrue=false;
//        				//���ż���
//        				for(int index=0;index<batchParm.getCount();index++){
//        					if(batchParm.getValue("ORDER_CODE",index).equals(parmIn.getValue("ORDER_CODE",j))){
//        						parmOut.setData("BATCH_NO",row,batchParm.getValue("BATCH_NO",index));
//        						istrue=true;
//        						break;
//        					}
//        				}
//        				parmOut.setData("ORDER_DESC",row,parmIn.getValue("ORDER_DESC",j));
//        				//����
//        				if(!istrue){
//        					parmOut.setData("BATCH_NO", row,
//        					"");
//        				}
//        				//����
//        				parmOut.setData("DOSE_CHN_DESC",row,parmIn.getValue("DOSE_CHN_DESC",j));
//        				//���
//        				parmOut.setData("SPECIFICATION",row,parmIn.getValue("SPECIFICATION",j));
//        				//����
//        				parmOut.setData("AL",row,parmIn.getData("AL",j));
//        				//����
//        				parmOut.setData("DOSAGE_QTY",row,parmIn.getData("DOSAGE_QTY",j));
//        				//��λ
//        				//parmOut.setData("UNIT_CHN_DESC",row,TypeTool.getString(unitMap.get(parmIn.getValue("DOSAGE_UNIT",j))));
//        				//����
//        				parmOut.setData("OWN_PRICE",row,df.format(StringTool.round(parmIn.getDouble("OWN_PRICE",j),2)));
//        				//�۸�
//        				parmOut.setData("AR_AMT",row,df.format(StringTool.round(parmIn.getDouble("AR_AMT",j),2)));
//        				sum += StringTool.round(parmIn.getDouble("AR_AMT",j),3);
//        				//===============pangben 20111014 stop
//        			}
//        		}
//        	}
//        }
//        //===============pangben 20111014 start
//        parmOut.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//        parmOut.addData("SYSTEM", "COLUMNS", "BATCH_NO");
//        parmOut.addData("SYSTEM", "COLUMNS", "DOSE_CHN_DESC");
//        parmOut.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//        parmOut.addData("SYSTEM", "COLUMNS", "AL");
//        parmOut.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
//        parmOut.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//        parmOut.addData("SYSTEM", "COLUMNS", "AR_AMT");
//        //===============pangben 20111014 stop
//        parmOut.setCount(rowCount);
        parmOut.setCount(count);
        parmOut.addData("SYSTEM", "COLUMNS", "REXP_CODE");
        parmOut.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parmOut.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        parmOut.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        parmOut.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
        parmOut.addData("SYSTEM", "COLUMNS", "TOT_AMT");
        parmOut.addData("SYSTEM", "COLUMNS", "AR_AMT");
        parmOut.setData("SUMTOTS",sumTotS);//�ܼ�Ӧ��
        parmOut.setData("SUMTOTR",sumTotR);//�ܼ�ʵ��
        return parmOut;
    }
    /**
     * ����reduceNO��rexpCode�������Ľ��
     * yanjing 20141224
     * @param parmIn
     * @param batchParm
     * @return
     */
    private TParm selectReduceAmt(String caseNo,String receiptNo,String rexpCode){
    	TParm result = new TParm();
    	//===ȡ��reduceNo
    	String sql = "SELECT REDUCE_NO FROM BIL_OPB_RECP WHERE CASE_NO = '"+caseNo+"' AND RECEIPT_NO = '"+receiptNo+"' ";
    	result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount()>0){
    		String reduceSql = "SELECT SUM(REDUCE_AMT) AS REDUCE_AMT FROM BIL_REDUCED  WHERE REDUCE_NO = '"+result.getValue("REDUCE_NO",0)+"' " +
    				"AND REXP_CODE = '"+rexpCode+"'";
        	result = new TParm(TJDODBTool.getInstance().select(reduceSql));
    	}
    	return result;
    	
    }
    public TParm dealTParmEng(TParm parmIn,TParm batchParm){
        BIL bil=new BIL();
        //��������������ƵĶ���
        Map chargeToRexpMap=bil.getChargeToRexpdDescMap();
        if(chargeToRexpMap==null)
            return null;
        //�õ���λ����map
        Map unitMap=SYSPublic.getUnitMap();
        //�洢������ɺ������
        TParm parmOut=new TParm();
        //�洢�շ����
        Map rexpMap=new HashMap();
        //������������
        int rowCount=parmIn.getCount();
        int count = rowCount;//���ô�ӡ���� add by sunqy 20140625
        DecimalFormat df = new DecimalFormat("0.00");
        double sumSubS = 0.00;//С��Ӧ��
        double sumSubR = 0.00;//С��ʵ��
        double sumTotS = 0.00;//�ܼ�Ӧ��
        double sumTotR = 0.00;//�ܼ�ʵ��
        //modify by sunqy 20140625 ----start----
        String code = "";//��¼ÿ���շ����
        String rexpCode = "";//yanjing 20141225 modify
        double reduceAmt = 0.00;//yanjing 20141225 modify
       for(int j=0;j<rowCount;j++){

    	   if (parmIn.getDouble("AR_AMT", j) == 0)
               continue;
           if (!code.equals(parmIn.getData("ENG_DESC",j))){
        	   if(j!=0){//�ж���������շ������ͬһ�־����������һ��С�ƣ����Ӧ��ʵ�ս��
        		   TParm reduceParm = new TParm();//====yanjing 20141224 modify
        		   reduceParm = selectReduceAmt(parmIn.getValue("CASE_NO",j),parmIn.getValue("RECEIPT_NO",j),rexpCode);
					if(reduceParm.getDouble("REDUCE_AMT", 0)!=0){//===�м����ʱ����Ӽ���Ľ��
						parmOut.addData("REXP_CODE", "");
						parmOut.addData("SPECIFICATION", "");
						parmOut.addData("UNIT_CHN_DESC", "");
						parmOut.addData("OWN_PRICE", "");
						parmOut.addData("DOSAGE_QTY", "REDUCED:");
						parmOut.addData("TOT_AMT", "");
						parmOut.addData("AR_AMT",-reduceParm.getDouble("REDUCE_AMT", 0) );
						reduceAmt+=StringTool.round(reduceParm.getDouble("REDUCE_AMT", 0), 2);
						count++;
					}
					parmOut.addData("REXP_CODE", "");
					parmOut.addData("SPECIFICATION", "");
					parmOut.addData("UNIT_ENG_DESC", "");
					parmOut.addData("OWN_PRICE", "");
					parmOut.addData("DOSAGE_QTY", "TOTAL:");
					parmOut.addData("TOT_AMT", df.format(sumSubS));
					if(reduceParm.getDouble("REDUCE_AMT", 0)!=0){
						parmOut.addData("AR_AMT", df.format(sumSubR-reduceParm.getDouble("REDUCE_AMT", 0)));
					}else{
						parmOut.addData("AR_AMT", df.format(sumSubR));
					}
					sumTotS += sumSubS;
					sumTotR += sumSubR;
					sumSubS = 0;
					sumSubR = 0;
					count++;
        	   }
        	   //�շ����
        	   rexpCode = parmIn.getValue("REXP_CODE",j);//YANJING 20141225
        	   code = (String) parmIn.getData("ENG_DESC",j);
               parmOut.addData("REXP_CODE",parmIn.getData("ENG_DESC",j));
           }else{
        	   parmOut.addData("REXP_CODE",""); 
           }
           //��Ŀ����/���
           if(parmIn.getValue("SPECIFICATION",j).length()==0){
        	   parmOut.addData("SPECIFICATION",parmIn.getValue("TRADE_ENG_DESC",j));
           }else{
        	   parmOut.addData("SPECIFICATION",(OPB.getPrescriptionFlg(parmIn.getValue("ORDER_CODE",j))? "*":"") +  //add by huangtt 20150520
        			   parmIn.getValue("TRADE_ENG_DESC",j)+"/"+parmIn.getValue("SPECIFICATION",j));  
           }
           //��λ 
           parmOut.addData("UNIT_CHN_DESC",TypeTool.getString(unitMap.get(parmIn.getValue("DISPENSE_UNIT",j))));
           //����
           parmOut.addData("OWN_PRICE",df.format(StringTool.round(parmIn.getDouble("OWN_PRICE",j),2)));
           //����
          
           parmOut.addData("DOSAGE_QTY",parmIn.getData("DISPENSE_QTY",j));
           //Ӧ�ս��
           parmOut.addData("TOT_AMT",df.format(StringTool.round(parmIn.getDouble("OWN_AMT",j),2)));
           //ʵ�ս��
           parmOut.addData("AR_AMT",df.format(StringTool.round(parmIn.getDouble("AR_AMT",j),2)));
           sumSubS += StringTool.round(parmIn.getDouble("OWN_AMT",j),2);
           sumSubR += StringTool.round(parmIn.getDouble("AR_AMT",j),2);
       }
       sumTotS += sumSubS;
       sumTotR += sumSubR;
       TParm reduceParm1 = new TParm();
       reduceParm1 = selectReduceAmt(parmIn.getValue("CASE_NO",rowCount-1),parmIn.getValue("RECEIPT_NO",rowCount-1),rexpCode);
       if(reduceParm1.getDouble("REDUCE_AMT", 0)!=0){//===�м����ʱ����Ӽ���Ľ��
			parmOut.addData("REXP_CODE", "");
			parmOut.addData("SPECIFICATION", "");
			parmOut.addData("UNIT_CHN_DESC", "");
			parmOut.addData("OWN_PRICE", "");
			parmOut.addData("DOSAGE_QTY", "REDUCED:");
			parmOut.addData("TOT_AMT", "");
			parmOut.addData("AR_AMT",-reduceParm1.getDouble("REDUCE_AMT", 0) );
			reduceAmt+=StringTool.round(reduceParm1.getDouble("REDUCE_AMT", 0),2);
			count++;
		}
       	parmOut.addData("REXP_CODE", "");
		parmOut.addData("SPECIFICATION", "");
		parmOut.addData("UNIT_ENG_DESC", "");
		parmOut.addData("OWN_PRICE", "");
		/* modify by lich �������滻��Ӣ�� 20141010  start*/ 
//		parmOut.addData("DOSAGE_QTY", "С��:");
		parmOut.addData("DOSAGE_QTY", "TOTAL:");
		/* modify by lich �������滻��Ӣ�� 20141010 end*/
		parmOut.addData("TOT_AMT", df.format(sumSubS));
		if(reduceParm1.getDouble("REDUCE_AMT", 0)!=0){//===�м����ʱ����Ӽ���Ľ��
			parmOut.addData("AR_AMT", df.format(sumSubR-reduceParm1.getDouble("REDUCE_AMT", 0)));
		}else{
			parmOut.addData("AR_AMT", df.format(sumSubR));
		}
		count++;
        //�ж��Ƿ��м��������д�ӡ����
        if(reduceAmt != 0 ){
        	parmOut.addData("REXP_CODE", "");
       		parmOut.addData("SPECIFICATION", "");
       		parmOut.addData("UNIT_ENG_DESC", "");
       		parmOut.addData("OWN_PRICE", "");
       		// modify by lich �������滻��Ӣ��
       		/* modify by lich �������滻��Ӣ�� 20141010  start*/ 
//       		parmOut.addData("DOSAGE_QTY", "����:");
       		parmOut.addData("DOSAGE_QTY", "REDUCED:");
       		/* modify by lich �������滻��Ӣ�� 20141010 end*/
       		parmOut.addData("TOT_AMT", "");
       		parmOut.addData("AR_AMT", df.format(-reduceAmt));
       		sumSubR += StringTool.round(-reduceAmt,3);
       		sumTotR += StringTool.round(-reduceAmt, 3);
       		count++;
        }
        
        parmOut.setCount(count);
        parmOut.addData("SYSTEM", "COLUMNS", "REXP_CODE");
        parmOut.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
//        parmOut.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        parmOut.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        parmOut.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
        parmOut.addData("SYSTEM", "COLUMNS", "TOT_AMT");
        parmOut.addData("SYSTEM", "COLUMNS", "AR_AMT");
        parmOut.setData("SUMTOTS",sumTotS);//�ܼ�Ӧ��
        parmOut.setData("SUMTOTR",sumTotR);//�ܼ�ʵ��
        return parmOut;
    }
}

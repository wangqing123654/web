package com.javahis.ui.ekt;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import jdo.ekt.EKTIO;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: ҽ�ƿ����׼�¼</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.9.16
 * @version 1.0
 */
public class EKTTredeControl
    extends TControl {

    private TTable table;
    
    //zhangp 20120130 �Ƿ����
    private boolean readCardFlg = false;

    public EKTTredeControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
        table = getTable("TABLE");
//        setValue("USER_ID", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        String sql = getSQL();
//        System.out.println("sql---" + sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount("BUSINESS_NO") <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        table.setParmValue(parm);
    }

    /**
     * ȡ�ò�ѯ��SQL���
     * @return String
     */
    private String getSQL() {
        String where1 = "";
        String where2 = "";
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
            String start_date = this.getValueString("START_DATE").substring(0, 19);
            start_date = start_date.substring(0, 4) + start_date.substring(5, 7) +
                start_date.substring(8, 10) + start_date.substring(11, 13) +
                start_date.substring(14, 16) + start_date.substring(17, 19);
            String end_date = this.getValueString("END_DATE").substring(0, 19);
            end_date = end_date.substring(0, 4) + end_date.substring(5, 7) +
                end_date.substring(8, 10) + end_date.substring(11, 13) +
                end_date.substring(14, 16) + end_date.substring(17, 19);
            where1 += " AND A.OPT_DATE BETWEEN TO_DATE('" + start_date +
                "','YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
                "','YYYYMMDDHH24MISS')";
            where2 += " AND A.OPT_DATE BETWEEN TO_DATE('" + start_date +
            "','YYYYMMDDHH24MISS') AND TO_DATE('" + end_date +
            "','YYYYMMDDHH24MISS')";
        }
        if (!"".equals(this.getValueString("USER_ID"))) {
            where1 += " AND A.OPT_USER = '" + getValueString("USER_ID") + "'";
            where2 += " AND A.OPT_USER = '" + getValueString("USER_ID") + "'";
        }
//        if (!"".equals(this.getValueString("TREDE_NO"))) {     
//            where += " AND A.TREDE_NO = '" + getValueString("TREDE_NO") + "'";
//        }
        if (!"".equals(this.getValueString("CARD_NO"))) {
            where1 += " AND A.CARD_NO = '" + getValueString("CARD_NO") + "'";
            where2 += " AND A.CARD_NO = '" + getValueString("CARD_NO") + "'";
        }
        if (!"".equals(this.getValueString("MR_NO"))) {
            where1 += " AND A.MR_NO = '" + getValueString("MR_NO") + "'";
            where2 += " AND A.MR_NO = '" + getValueString("MR_NO") + "'";
        }
        if (!"".equals(this.getValueString("CASE_NO"))) {
            where1 += " AND A.CASE_NO = '" + getValueString("CASE_NO") + "'";
            where2 += " AND A.CASE_NO = '" + getValueString("CASE_NO") + "'";
        }
        if (!"".equals(this.getValueString("BUSINESS_NO"))) {
//            where1 += " AND A.BUSINESS_NO = '" + getValueString("BUSINESS_NO") +
//                "'";
            where2 += " AND A.BUSINESS_NO = '" + getValueString("BUSINESS_NO") +
            "'";
        }
        if (!"".equals(this.getValueString("STATE"))) {
            where1 += " AND A.STATE = '" + getValueString("STATE") + "'";
            where2 += " AND A.CHARGE_FLG = '" + getValueString("STATE") + "'";
        }
        if (!"".equals(this.getValueString("BUSINESS_TYPE"))) {
            where1 += " AND A.BUSINESS_TYPE = '" +
                getValueString("BUSINESS_TYPE") + "'";
            where2 += " AND A.BUSINESS_TYPE = '" +
            	getValueString("BUSINESS_TYPE") + "'";
        }
        if (!"".equals(this.getValueString("PAT_NAME"))) {
        	where1 += " AND B.PAT_NAME = '" +
        	getValueString("PAT_NAME") + "'";
        	where2 += " AND B.PAT_NAME = '" +
        	getValueString("PAT_NAME") + "'";
        }
        return 
        " SELECT   A.MR_NO, A.CARD_NO, A.CASE_NO, A.TRADE_NO BUSINESS_NO, B.PAT_NAME," +
        " A.OLD_AMT, A.AMT, " +
        " A.STATE, A.BUSINESS_TYPE, A.OPT_USER, A.OPT_DATE," +
        " A.OPT_TERM" +
        " FROM EKT_TRADE A, SYS_PATINFO B" +
        " WHERE A.MR_NO = B.MR_NO" +
        where1 +
        " UNION ALL" +
        " SELECT   A.MR_NO, A.CARD_NO, CASE WHEN A.CASE_NO = 'none' THEN '' ELSE A.CASE_NO END AS CASE_NO, A.BUSINESS_NO, B.PAT_NAME," +
        " A.ORIGINAL_BALANCE, A.BUSINESS_AMT, " +
        " CASE" +
        " WHEN A.CHARGE_FLG = '3'" +
        " THEN '0'" +
        " ELSE A.CHARGE_FLG" +
        " END STATE, '', A.OPT_USER," +
        " A.OPT_DATE, A.OPT_TERM" +
        " FROM EKT_ACCNTDETAIL A, SYS_PATINFO B" +
        " WHERE A.MR_NO = B.MR_NO AND A.CHARGE_FLG IN ('3', '4', '5', '7', '8')" +
        where2 +
        " ORDER BY OPT_DATE";
    }
    
    /**
     * ��շ���
     */
    public void onClear() {
        String clear =
            "START_DATE;END_DATE;USER_ID;TREDE_NO;CARD_NO;MR_NO;CASE_NO;BUSINESS_NO;STATE;BUSINESS_TYPE;PAT_NAME";
        this.clearValue(clear);
        table.removeRowAll();
        //zhangp 20120130
        readCardFlg = false;
        TTextField mrNoTextField = (TTextField) getComponent("MR_NO");
        mrNoTextField.setEnabled(true);
        TTextField cardNoTextField = (TTextField) getComponent("CARD_NO");
        cardNoTextField.setEnabled(true);
        Timestamp today = SystemTool.getInstance().getDate();
        String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    }

    /**
     * ���Excel
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("û�л������");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "ҽ�ƿ����׼�¼��");
    }

    /**
     * �����Żس��¼�
     */
    public void onMrNoAction() {
		String mrNo = ""+getValue("MR_NO");
		Pat pat = Pat.onQueryByMrNo(mrNo);
		mrNo = pat.getMrNo();
        this.setValue("MR_NO", mrNo);
        this.setValue("PAT_NAME", pat.getName());
    }

    /**
     * ��ҽ�ƿ�
     */
    public void onCardNoAction() {
    	//zhangp 20111230
//        TParm parm = EKTIO.getInstance().getPat();
    	TParm parm = EKTIO.getInstance().TXreadEKT();
        //System.out.println("parm==="+parm);
    	if (null == parm || parm.getValue("MR_NO").length() <= 0) {
            this.messageBox("�˿���Ч");
            return;
        }
    	//zhangp 20120130
    	if(parm.getErrCode()<0){
    		messageBox(parm.getErrText());
    	}
    	String cardNo = parm.getValue("MR_NO")+parm.getValue("SEQ");
        this.setValue("CARD_NO", cardNo);
        //zhangp 20120130 �ӹܿأ����������ܴ�ӡ
        readCardFlg = true;
        //zhangp 20120130 �Ӳ�����
        this.setValue("MR_NO", parm.getValue("MR_NO"));
        //zhangp 20120130 
        TTextField mrNoTextField = (TTextField) getComponent("MR_NO");
        mrNoTextField.setEnabled(false);
        TTextField cardNoTextField = (TTextField) getComponent("CARD_NO");
        cardNoTextField.setEnabled(false);
        //===zhangp 20120319 start
        Pat pat = Pat.onQueryByMrNo(parm.getValue("MR_NO"));
        setValue("PAT_NAME", pat.getName());
        //===zhangp 20120319 end
        //zhangp 20120131
        onQuery();
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    
    /**
     * ��ӡ
     * zhangp 20120129
     */
    public void onPrint(){
    	//zhangp 20120130 �Ӷ�����֤
    	if(!readCardFlg){
    		messageBox("���ȡҽ�ƿ�");
    		return;
    	}
    	String cardno = getValueString("CARD_NO");
    	String mrno = getValueString("MR_NO");
    	TParm result = getParm(mrno,cardno);
//    	TParm result = getParm("000000400598","000000400598001");
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return;
    	}
    	//=============modify by lim 2012/02/24 begin
    	this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTTrede.jhw",result);
    	//=============modify by lim 2012/02/24 end
    }
    /**
     * ��ȡ��ӡ����
     * zhangp 20120129
     * @param mrno
     * @param cardno
     * @return
     */
    public TParm getParm(String mrno,String cardno){
    	Pat pat = Pat.onQueryByMrNo(mrno);
    	//�õ���������
        String[] AGE =  StringTool.CountAgeByTimestamp(pat.getBirthday(),SystemTool.getInstance().getDate());
    	String sql =
    		"SELECT A.ISSUE_DATE,A.CARD_NO,B.USER_NAME" +
    		" FROM EKT_ISSUELOG A,SYS_OPERATOR B " +
    		" WHERE A.CARD_NO = '"+cardno+"' AND A.OPT_USER = B.USER_ID ";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return result;
    	}
		TParm data = new TParm();// ��ӡ������
//		TParm parm = new TParm();// �������
		data.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName());
		data.setData("MR_NO", "TEXT", "������: "+mrno);
		data.setData("SEX", "TEXT", "�Ա�: "+pat.getSexString());
		data.setData("NAME", "TEXT", "����: "+pat.getName());
//		data.setData("AGE", "TEXT", "����: "+AGE[0]+"��"+AGE[1]+"����"+AGE[2]+"��");
		data.setData("AGE", "TEXT", "����: "+AGE[0]+"��");
		data.setData("IDNO", "TEXT", "���֤��: "+pat.getIdNo());
		data.setData("COMPANY", "TEXT", "��λ����: "+pat.getCompanyDesc());
		data.setData("COMPANYCALL", "TEXT", "��λ�绰: "+pat.getTelCompany());
		data.setData("CELLPHONE", "TEXT", "�绰: "+pat.getCellPhone());
		data.setData("ISSUE_DATE", "TEXT", "�ۿ�����: "+result.getValue("ISSUE_DATE", 0).replace("-", "/").substring(0, result.getValue("ISSUE_DATE", 0).length()-2));
		data.setData("USER_NAME", "TEXT", "�ۿ���Ա: "+result.getData("USER_NAME", 0));
		String date = SystemTool.getInstance().getDate().toString();
		data.setData("PRINT_DATE", "TEXT", "��ӡ����: "+date.substring(0, 4)+
    			"/"+date.substring(5, 7)+"/"+date.substring(8, 10));
		sql = 
			"SELECT A.BUSINESS_DATE,A.CHARGE_FLG,B.GATHER_TYPE,A.ACCNT_STATUS,A.ORIGINAL_BALANCE,A.BUSINESS_AMT,A.CURRENT_BALANCE "+
            " FROM EKT_ACCNTDETAIL A,EKT_BIL_PAY B WHERE A.MR_NO = '"+
            mrno+"' AND A.CHARGE_FLG IN (3,4,5,7) AND A.BUSINESS_NO = B.BIL_BUSINESS_NO "+
            " UNION "+
            " SELECT A.BUSINESS_DATE,A.CHARGE_FLG,'' AS GATHER_TYPE,A.ACCNT_STATUS,A.ORIGINAL_BALANCE,A.BUSINESS_AMT,A.CURRENT_BALANCE "+
            " FROM EKT_ACCNTDETAIL A WHERE A.MR_NO = '"+
            mrno+"' AND A.CHARGE_FLG IN (1,2)";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return result;
    	}
		double businessAmt = 0.0;
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//�������ڸ�ʽ
		DecimalFormat   df1 = new DecimalFormat("#0.00"); //�������ݸ�ʽ
		for (int i = 0; i < result.getCount(); i++) {
			if(result.getInt("CHARGE_FLG", i)==1){
				result.setData("CHARGE_FLG", i, "�ۿ�");
			}
			if(result.getInt("CHARGE_FLG", i)==2){
				result.setData("CHARGE_FLG", i, "�˿�");
				businessAmt = -result.getDouble("BUSINESS_AMT", i);
				result.setData("BUSINESS_AMT", i, businessAmt);
			}
			if(result.getInt("CHARGE_FLG", i)==3){
				result.setData("CHARGE_FLG", i, "ҽ�ƿ���ֵ");
			}
			if(result.getInt("CHARGE_FLG", i)==4){
				result.setData("CHARGE_FLG", i, "�ƿ�");
			}
			if(result.getInt("CHARGE_FLG", i)==5){
				result.setData("CHARGE_FLG", i, "����");
			}
			if(result.getInt("CHARGE_FLG", i)==7){
				result.setData("CHARGE_FLG", i, "�˷�");
				businessAmt = -result.getDouble("BUSINESS_AMT", i);
				result.setData("BUSINESS_AMT", i, businessAmt);
			}
			if(result.getInt("ACCNT_STATUS",i)==1){
				result.setData("ACCNT_STATUS", i, "δ����");
			}
			if(result.getInt("ACCNT_STATUS",i)==2){
				result.setData("ACCNT_STATUS", i, "�Ѷ���");
			}
			if(result.getData("GATHER_TYPE", i).equals("C0")){
				result.setData("GATHER_TYPE", i, "�ֽ�");
			}
			if(result.getData("GATHER_TYPE", i).equals("C1")){
				result.setData("GATHER_TYPE", i, "ˢ��");
			}
			if(result.getData("GATHER_TYPE", i).equals("C2")){
				result.setData("GATHER_TYPE", i, "��Ʊ");
			}
			if(result.getData("GATHER_TYPE", i).equals("C4")){
				result.setData("GATHER_TYPE", i, "Ӧ�տ�");
			}
			if(result.getData("GATHER_TYPE", i).equals("T0")){
				result.setData("GATHER_TYPE", i, "֧Ʊ");
			}
			if(result.getData("GATHER_TYPE", i).equals("Z")){
				result.setData("GATHER_TYPE", i, "����Ȧ���");
			}
			
			result.setData("BUSINESS_DATE", i, df.format(result.getTimestamp("BUSINESS_DATE", i)));			
			result.setData("ORIGINAL_BALANCE",i, df1.format(result.getData("ORIGINAL_BALANCE", i)));
			result.setData("BUSINESS_AMT",i, df1.format(result.getData("BUSINESS_AMT", i)));
			result.setData("CURRENT_BALANCE",i, df1.format(result.getData("CURRENT_BALANCE", i)));
		}
		data.setData("TABLE", result.getData());
    	return data;
    }


}

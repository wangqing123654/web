package com.javahis.ui.bil;

import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.ekt.EKTIO;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: ���˿��ѯ</p>
 *
 * <p>Description: ���˿��ѯ</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author luhai 20120320
 * @version 1.0
 */
public class BILPayTypeDetailControl extends TControl{
    /**
      * ��ʼ������
      */
     public void onInit() {
         initPage();
         //Ȩ�����
         TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
         cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                 getValueString("REGION_CODE")));
     }

     private TTable table;
     DecimalFormat df1 = new DecimalFormat("########0.00");
     public BILPayTypeDetailControl() {
     }
     public static final String cash="�ֽ�";
     public static final String bank="���п�";
     public static final String check="֧Ʊ";
     public static final String debit="����";
     
     /**
      * ��ѯ����
      */
     public void onQuery() {
         TParm parm = new TParm();
         String date_s = getValueString("DATE_S");
         String date_e = getValueString("DATE_E");
         if (null == date_s || date_s.length() <= 0 || null == date_e ||
             date_e.length() <= 0) {
             this.messageBox("��������Ҫ��ѯ��ʱ�䷶Χ");
             return;
         }
         date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "").
                  replace("-", "").replace(" ", "");
         date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "").
                  replace("-", "").replace(" ", "");
         if (null != this.getValueString("REGION_CODE") && this.getValueString("REGION_CODE").length() > 0)
             parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
         parm.setData("DATE_S", date_s);
         parm.setData("DATE_E", date_e);
//         TParm result = CLPOverPersonManagerTool.getInstance().selectData("selectOverflowCase",parm);
         StringBuffer wherebf =new StringBuffer();
         if(!"".equals(date_s)){
        	 wherebf.append(" AND A.CHARGE_DATE>=TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') ");
         }
         if(!"".equals(date_e)){
        	 wherebf.append(" AND A.CHARGE_DATE<=TO_DATE('"+date_e+"','YYYYMMDDHH24MISS') ");
         }
         String mrNo=this.getValueString("MR_NO");
         if(!"".equals(mrNo)){
        	 wherebf.append(" AND A.MR_NO ='"+mrNo+"'");
         }
         String optUser=this.getValueString("OPT_USER");
         if(!"".equals(optUser)){
        	 wherebf.append(" AND A.OPT_USER ='"+optUser+"'");
         }
         StringBuffer sqlbf = new StringBuffer();
         //===zhangp 20120412 start
         sqlbf.append("(SELECT A.RECEIPT_NO," +
         		" CASE" +
         		" WHEN PAY_CASH< 0" +
         		" THEN '�����˿�'" +
         		" ELSE '���㲹��'" +
         		" END AS TYPE," +
         		" A.MR_NO, D.PAT_NAME," +
         		" A.PAY_CASH AS AMT, C.STATION_DESC AS SATION_DESC, '�ֽ�' AS PAY_TYPE," +
         		" E.USER_NAME AS OPT_NAME" +
         		" FROM BIL_IBS_RECPM A," +
         		" ADM_INP B," +
         		" SYS_STATION C," +
         		" SYS_PATINFO D," +
         		" SYS_OPERATOR E" +
         		" WHERE A.CASE_NO = B.CASE_NO(+)" +
         		" AND B.STATION_CODE = C.STATION_CODE(+)" +
         		" AND A.MR_NO = D.MR_NO(+)" +
         		" AND A.OPT_USER = E.USER_ID(+)" +
         		" AND (CASE" +
         		" WHEN A.PAY_BILPAY IS NULL" +
         		" THEN 0" +
         		" ELSE A.PAY_BILPAY" +
         		" END - A.OWN_AMT) <> 0" +
         		" AND PAY_CASH <> 0" +
         		//===zhangp 20120507 start
         		//===zhangp 20120614 start
//         		" AND A.AR_AMT>=0" +
//         		" AND REFUND_DATE IS NULL");
         		" AND A.AR_AMT>=0");
         //===zhangp 20120614 end
         		//===zhangp 20120507 end
         sqlbf.append(wherebf.toString());
         sqlbf.append(")");
         sqlbf.append(" UNION ALL ");
         sqlbf.append("(SELECT A.RECEIPT_NO," +
         		" CASE" +
         		" WHEN PAY_BANK_CARD< 0" +
         		" THEN '�����˿�'" +
         		" ELSE '���㲹��'" +
         		" END AS TYPE," +
         		" A.MR_NO, D.PAT_NAME," +
         		" A.PAY_BANK_CARD AS AMT, C.STATION_DESC AS SATION_DESC, '���п�' AS PAY_TYPE," +
         		" E.USER_NAME AS OPT_NAME" +
         		" FROM BIL_IBS_RECPM A," +
         		" ADM_INP B," +
         		" SYS_STATION C," +
         		" SYS_PATINFO D," +
         		" SYS_OPERATOR E" +
         		" WHERE A.CASE_NO = B.CASE_NO(+)" +
         		" AND B.STATION_CODE = C.STATION_CODE(+)" +
         		" AND A.MR_NO = D.MR_NO(+)" +
         		" AND A.OPT_USER = E.USER_ID(+)" +
         		" AND (CASE" +
         		" WHEN A.PAY_BILPAY IS NULL" +
         		" THEN 0" +
         		" ELSE A.PAY_BILPAY" +
         		" END - A.OWN_AMT) <> 0" +
         		" AND PAY_BANK_CARD <> 0" +
         		//===zhangp 20120507 start
         		//===zhangp 20120614 start
//         		" AND A.AR_AMT>=0" +
//         		" AND REFUND_DATE IS NULL");
         		" AND A.AR_AMT>=0");
         //===zhangp 20120614 end
         		//===zhangp 20120507 end
         sqlbf.append(wherebf.toString());
         sqlbf.append(")");
         sqlbf.append(" UNION ALL ");
         sqlbf.append("(SELECT A.RECEIPT_NO," +
         		" CASE" +
         		" WHEN PAY_CHECK< 0" +
         		" THEN '�����˿�'" +
         		" ELSE '���㲹��'" +
         		" END AS TYPE," +
         		" A.MR_NO, D.PAT_NAME, PAY_CHECK AS AMT, C.STATION_DESC AS SATION_DESC," +
         		" '֧Ʊ' AS PAY_TYPE, E.USER_NAME AS OPT_NAME" +
         		" FROM BIL_IBS_RECPM A," +
         		" ADM_INP B," +
         		" SYS_STATION C," +
         		" SYS_PATINFO D," +
         		" SYS_OPERATOR E" +
         		" WHERE A.CASE_NO = B.CASE_NO(+)" +
         		" AND B.STATION_CODE = C.STATION_CODE(+)" +
         		" AND A.MR_NO = D.MR_NO(+)" +
         		" AND A.OPT_USER = E.USER_ID(+)" +
         		" AND (CASE" +
         		" WHEN A.PAY_BILPAY IS NULL" +
         		" THEN 0" +
         		" ELSE A.PAY_BILPAY" +
         		" END - A.OWN_AMT) <> 0" +
         		" AND PAY_CHECK <> 0" +
         		//===zhangp 20120507 start
         		//===zhangp 20120614 start
//         		" AND A.AR_AMT>=0" +
//         		" AND REFUND_DATE IS NULL");
         		" AND A.AR_AMT>=0");
         //===zhangp 20120614 end
         		//===zhangp 20120507 end
         sqlbf.append(wherebf.toString());
         sqlbf.append(")");
         sqlbf.append(" UNION ALL ");
         sqlbf.append("(SELECT A.RECEIPT_NO," +
         		" CASE" +
         		" WHEN PAY_DEBIT< 0" +
         		" THEN '�����˿�'" +
         		" ELSE '���㲹��'" +
         		" END AS TYPE," +
         		" A.MR_NO, D.PAT_NAME, PAY_DEBIT AS AMT, C.STATION_DESC AS SATION_DESC," +
         		" '����' AS PAY_TYPE, E.USER_NAME AS OPT_NAME" +
         		" FROM BIL_IBS_RECPM A," +
         		" ADM_INP B," +
         		" SYS_STATION C," +
         		" SYS_PATINFO D," +
         		" SYS_OPERATOR E" +
         		" WHERE A.CASE_NO = B.CASE_NO(+)" +
         		" AND B.STATION_CODE = C.STATION_CODE(+)" +
         		" AND A.MR_NO = D.MR_NO(+)" +
         		" AND A.OPT_USER = E.USER_ID(+)" +
         		" AND (CASE" +
         		" WHEN A.PAY_BILPAY IS NULL" +
         		" THEN 0" +
         		" ELSE A.PAY_BILPAY" +
         		" END - A.OWN_AMT) <> 0" +
         		" AND PAY_DEBIT <> 0" +
         		//===zhangp 20120507 start
         		//===zhangp 20120614 start
//         		" AND A.AR_AMT>=0" +
//         		" AND REFUND_DATE IS NULL");
         		" AND A.AR_AMT>=0");
         //===zhangp 20120614 end
         		//===zhangp 20120507 end
         sqlbf.append(wherebf.toString());
         sqlbf.append(")");
       //===zhangp 20120412 end
//         sqlbf.append("SELECT A.CASE_NO,CASE WHEN A.ADM_TYPE='E' THEN '����' WHEN A.ADM_TYPE='O' THEN '����' WHEN A.ADM_TYPE='I' THEN 'סԺ' WHEN A.ADM_TYPE='H' THEN '����'  end AS ADM_TYPE_DESC ,");
//         sqlbf.append(" B.PAT_NAME ,A.ADM_DATE,A.OPT_USER,C.USER_NAME AS OPT_USER_DESC,A.GREEN_BALANCE,A.GREEN_PATH_TOTAL,A.MR_NO ");
//         sqlbf.append("  FROM REG_PATADM A,SYS_PATINFO B,SYS_OPERATOR C  WHERE A.MR_NO=B.MR_NO(+) AND A.OPT_USER=C.USER_ID(+)  ");
//         sqlbf.append(" AND (A.GREEN_BALANCE>0  or GREEN_PATH_TOTAL>0) ");

//         System.out.println("��ѯsql��"+sqlbf.toString());
//         System.out.println("======sqlbf===asdasd=="+sqlbf.toString()); 
         TParm result = new TParm(TJDODBTool.getInstance().select(sqlbf.toString()));
         if(result.getErrCode()<0){
        	 this.messageBox("��ѯʧ��");
        	 return;
         }
         if(result.getCount()<=0){
             this.messageBox("��������");
         }
         table.setParmValue(result);
     }

     /**
      * ��ʼ��������
      */
     private void initPage() {
         Timestamp date = StringTool.getTimestamp(new Date());
         table = (TTable) getComponent("TABLE");
         this.setValue("REGION_CODE", Operator.getRegion());
         // ��ʼ����ѯ����
         this.setValue("DATE_E",
                       date.toString().substring(0, 10).replace('-', '/') +
                       " 23:59:59");
         this.setValue("DATE_S",
                      date.toString().substring(0, 10).
                       replace('-', '/') + " 00:00:00");
         //�󶨿ؼ��¼�
         callFunction("UI|MR_NO|addEventListener",
                      "MR_NO->" + TKeyListener.KEY_RELEASED, this,
                      "onKeyReleased");
         this.setValue("OPT_USER", Operator.getID());
         
     }
     public void onKeyReleased(KeyEvent e) {
         if (e.getKeyCode() != 10) {
             return;
         }
         TTextField mrNO=(TTextField)this.getComponent("MR_NO");
         mrNO.setValue( PatTool.getInstance().checkMrno(mrNO.getValue()));
         mrNO.setFocusable(true);
         this.onQuery();
         mrNO.setFocusable(false);//====yznjing 20140710 
     }
 	/**
 	 * ҽ�ƿ�����
 	 */
 	public void onEKT() {
 		TParm parm = EKTIO.getInstance().TXreadEKT();
         //System.out.println("parm==="+parm);
     	if (null == parm || parm.getValue("MR_NO").length() <= 0) {
             this.messageBox("��ҽ�ƿ���Ч");
             return;
         } 
     	//zhangp 20120130
     	if(parm.getErrCode()<0){
     		messageBox(parm.getErrText());
     	}
 		setValue("MR_NO", parm.getValue("MR_NO"));
 		this.onQuery();
 		//�޸Ķ�ҽ�ƿ�����  end luhai 
 	}
     /**
      * ��ӡ����
      */
     public void onPrint() {
         //�������
         int fillCnt=0;
         //������
         double fillAmt=0;
         //�����ֽ�
         double fillCash=0;
         //����֧Ʊ
         double fillCheck=0;
         //�˿����
         int returnCnt=0;
         //�˿���
         double returnAmt=0;
         //�˿��ֽ�
         double returnCash=0;
         //�˿�֧Ʊ
         double returnCheck=0;
         //�ܼƱ���
         int totCnt=0;
         //�ܼƽ��
         double totAmt=0;
         //�ܼ��ֽ�
         double totCash=0;
         //�ܼ�֧Ʊ
         double totCheck=0;
         //��������
         TParm result = new TParm();
         TParm parm = table.getParmValue();
         if (null == parm || parm.getCount() <= 0) {
             this.messageBox("û����Ҫ��ӡ������");
             return;
         }
         TParm parmValue=new TParm();
         for(int i=0;i<parm.getCount();i++){
             parmValue.addData("RECEIPT_NO",parm.getValue("RECEIPT_NO",i));
             parmValue.addData("TYPE",parm.getValue("TYPE",i));
             parmValue.addData("MR_NO",parm.getValue("MR_NO",i));
             parmValue.addData("PAT_NAME",parm.getValue("PAT_NAME",i));
             parmValue.addData("AMT",parm.getValue("AMT",i));
             parmValue.addData("SATION_DESC",parm.getValue("SATION_DESC",i));
             parmValue.addData("PAY_TYPE",parm.getValue("PAY_TYPE",i));
             parmValue.addData("OPT_NAME",parm.getValue("OPT_NAME",i));
//             parmValue.addData("STATION_DESC",parm.getValue("STATION_DESC",i));
             if("�����˿�".equals(parm.getValue("TYPE",i))){
            	 returnCnt++;
            	 returnAmt+=parm.getDouble("AMT",i);
            	 if(cash.equals(parm.getValue("PAY_TYPE",i))){
            		 returnCash+=parm.getDouble("AMT",i);            		 
            	 }
            	 //===zhangp 20120601 start
//            	 if(check.equals(parm.getValue("PAY_TYPE",i))){
            	 if(check.equals(parm.getValue("PAY_TYPE",i)) || bank.equals(parm.getValue("PAY_TYPE",i))){
            		 //===zhangp 20120601 end
            		 returnCheck+=parm.getDouble("AMT",i);            		 
            	 }
             }
             if("���㲹��".equals(parm.getValue("TYPE",i))){
            	 fillCnt++;
            	 fillAmt+=parm.getDouble("AMT",i);
            	 if(cash.equals(parm.getValue("PAY_TYPE",i))){
            		 fillCash+=parm.getDouble("AMT",i);            		 
            	 }
            	 //===zhangp 20120601 start
//            	 if(check.equals(parm.getValue("PAY_TYPE",i))){
            	 if(check.equals(parm.getValue("PAY_TYPE",i)) || bank.equals(parm.getValue("PAY_TYPE",i))){
            		 //===zhangp 20120601 end
            		 fillCheck+=parm.getDouble("AMT",i);            		 
            	 }
             }
             totCnt++;
             totAmt+=parm.getDouble("AMT",i);
        	 if(cash.equals(parm.getValue("PAY_TYPE",i))){
        		 totCash+=parm.getDouble("AMT",i);            		 
        	 }
        	 //===zhangp 20120601 start
//        	 if(check.equals(parm.getValue("PAY_TYPE",i))){
        	 if(check.equals(parm.getValue("PAY_TYPE",i)) || bank.equals(parm.getValue("PAY_TYPE",i))){
        		 //===zhangp 20120601 end
        		 totCheck+=parm.getDouble("AMT",i);            		 
        	 }
         }
         parmValue.setCount(parm.getCount());
         parmValue.addData("SYSTEM", "COLUMNS", "RECEIPT_NO");
         parmValue.addData("SYSTEM", "COLUMNS", "TYPE");
         parmValue.addData("SYSTEM", "COLUMNS", "MR_NO");
         parmValue.addData("SYSTEM", "COLUMNS", "PAT_NAME");
         parmValue.addData("SYSTEM", "COLUMNS", "SATION_DESC");
         parmValue.addData("SYSTEM", "COLUMNS", "AMT");
         parmValue.addData("SYSTEM", "COLUMNS", "PAY_TYPE");
         parmValue.addData("SYSTEM", "COLUMNS", "OPT_NAME");
//         parmValue.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
         result.setData("S_DATE", "TEXT",
                        getValueString("DATE_S").substring(0,
                 getValueString("DATE_S").lastIndexOf(".")));
         result.setData("E_DATE", "TEXT",
                        getValueString("DATE_E").substring(0,
                 getValueString("DATE_S").lastIndexOf(".")));
         result.setData("OPT_USER", Operator.getName());
         result.setData("T1", parmValue.getData());
         result.setData("TITLE", "TEXT",
                        (null != Operator.getHospitalCHNFullName() ?
                         Operator.getHospitalCHNFullName() : "����Ժ��") +
                        "���㲹���˿���ϸ�����ܣ�");
         //��ӡʱ��
         result.setData("PRINT_DATE","TEXT",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy��MM��dd��"));
         //����ϼ������Ϣ
         result.setData("fillCnt","TEXT", fillCnt);
         result.setData("fillAmt","TEXT", df1.format(fillAmt));
         result.setData("fillCash","TEXT", df1.format(fillCash));
         result.setData("fillCheck","TEXT", df1.format(fillCheck));
         result.setData("returnCnt","TEXT", returnCnt);
         result.setData("returnAmt","TEXT", df1.format(returnAmt));
         result.setData("returnCash","TEXT", df1.format(returnCash));
         result.setData("returnCheck","TEXT", df1.format(returnCheck));
         result.setData("totCnt","TEXT", totCnt);
         result.setData("totAmt","TEXT", df1.format(totAmt));
         result.setData("totCash","TEXT",df1.format( totCash));
         result.setData("totCheck","TEXT", df1.format(totCheck));
         //¬�������Ʊ���
         //��β
         result.setData("CREATEUSER", "TEXT", Operator.getName());
//         this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPayTypeDetail.jhw.jhw",
//                              result);
         this.openPrintWindow(IReportTool.getInstance().getReportPath("BILPayTypeDetail.jhw"),
                              IReportTool.getInstance().getReportParm("BILPayTypeDetail.class", result));//����ϲ�modify by wanglong 20130730

     }

     /**
      * ���
      */
     public void onClear() {
    	 this.setValue("MR_NO", "");
    	 this.setValue("OPT_USER", "");
         initPage();
         table.removeRowAll();
     }

     /**
      * ���Excel
      */
     public void onExport() {
         //�õ�UI��Ӧ�ؼ�����ķ���
         TParm parm = table.getParmValue();
         if (null == parm || parm.getCount() <= 0) {
             this.messageBox("û����Ҫ����������");
             return;
         }
         ExportExcelUtil.getInstance().exportExcel(table, "���˿��ѯ��");
     }

}

package com.javahis.ui.ekt;

import java.text.DecimalFormat;
import java.util.Date;

import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: �°�ҽ�ƿ���ֵ��ӡ�汾
 * </p>
 * 
 * <p>
 * Description: �°�ҽ�ƿ���ֵ��ӡ�汾
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author sunqy 20140611
 * @version 1.0
 */

public class EKTReceiptPrintControl{
	
	private static EKTReceiptPrintControl print;
	private String returnFee;//�˷ѵ�"��"
	private String returnO;//�˷ѵ�"o"
	public EKTReceiptPrintControl() {
	}
	
	public static EKTReceiptPrintControl getInstance(){
		if(print == null){
			print = new EKTReceiptPrintControl();
		}
		return print;
	}
	public void onPrint(TTable table,TParm parmSum,String copy,int row,Pat pat,boolean flg, TControl tc){
//		System.out.println(SystemTool.getInstance().getDate());
		DecimalFormat df=new DecimalFormat("#0.00");
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT", parmSum.getData("TITLE"));
//		if (null == bil_business_no)
//			bil_business_no = EKTTool.getInstance().getBillBusinessNo();// ��ӡ����
//		parm.setData("RecNO", "TEXT", bil_business_no);// �վݺ�
		parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); // ����
		parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); // ������
		String sql = "SELECT B.CHN_DESC GATHER_TYPE,A.BIL_BUSINESS_NO RECNO,A.DESCRIPTION,A.CARD_TYPE,A.GATHER_TYPE TYPE,A.PRINT_NO " +
				"FROM SYS_DICTIONARY B,EKT_BIL_PAY A,EKT_ISSUELOG C " + 
				"WHERE A.GATHER_TYPE = B.ID " +
				"AND B.GROUP_ID = 'GATHER_TYPE' " +
				"AND A.CARD_NO = C.CARD_NO " +
				" AND A.CARD_NO='"+parmSum.getValue("CARD_NO")+"' AND A.BIL_BUSINESS_NO='"+parmSum.getValue("BIL_BUSINESS_NO")+"'"+
				"ORDER BY A.BIL_BUSINESS_NO DESC";
		//System.out.println("sql:::"+sql);
//		if(pat.getRcntIpdDept().length()>0){
//			sql += " AND A.DEPT_CODE = '"+pat.getRcntIpdDept()+"'";
//		}
//		if(parmSum.getValue("GATHER_TYPE").length()>0){
//			sql += " AND B.ID = '"+parmSum.getValue("GATHER_TYPE")+"'";
//		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		result = result.getRow(0);
//		tc.messageBox("result = "+result);
		parm.setData("RecNO", "TEXT", SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO", "MEM_NO")); //Ʊ�ݺ�
		parm.setData("DEPT_CODE", "TEXT", result.getValue("DEPT_CODE"));// �Ʊ�
		double money = parmSum.getDouble("BUSINESS_AMT")==0.0 ? parmSum.getDouble("AMT") : Double.parseDouble(parmSum.getValue("BUSINESS_AMT"));
//		tc.messageBox("money0==="+parmSum.getDouble("BUSINESS_AMT"));
//		tc.messageBox("money1==="+Double.parseDouble(parmSum.getValue("AMT")));
//		tc.messageBox("money2==="+money);
		parm.setData("MONEY", "TEXT", df.format(StringTool.round(money, 2))+"Ԫ"); // ���
		parm.setData("CAPITAL", "TEXT", StringUtil.getInstance().numberToWord(money)); // ��д���
//		if(!result.getValue("CARD_TYPE").equals("")){
//			System.out.println("select chn_desc from sys_dictionary where GROUP_ID='SYS_CARDTYPE' and id='"+result.getValue("CARD_TYPE")+"'");
//			TParm data=new TParm(TJDODBTool.getInstance().select("select chn_desc from sys_dictionary where GROUP_ID='SYS_CARDTYPE' and id='"+result.getValue("CARD_TYPE")+"'"));
//			data.getRow(0);
//			System.out.println(":::"+data.getValue("chn_desc"));
//			parm.setData("CARD_TYPE","TEXT",data.getValue("CHN_DESC"));
//		}
		//System.out.println(":::"+result.getValue("TYPE"));
		if("C1".equals(result.getValue("TYPE"))){//�����˷�û�п����ͣ�����Ҫ�ͳ�ֵ���ֿ���add by huangjw 20150115
			/*if(parmSum.getData("UnFeeFLG")!=null && parmSum.getData("UnFeeFLG").equals("Y")){
				parm.setData("ACOUNT_NO", "TEXT", result.getValue("DESCRIPTION"));// �˺�
			}else{*/
				String cardSql="select chn_desc from sys_dictionary where group_id='SYS_CARDTYPE' and id='"+result.getValue("CARD_TYPE")+"'";
				TParm param=new TParm(TJDODBTool.getInstance().select(cardSql));
				parm.setData("CARD_TYPE","TEXT",param.getValue("CHN_DESC",0));//��ӿ����� add by huangjw 20141231
				parm.setData("ACOUNT_NO", "TEXT", result.getValue("DESCRIPTION"));// �˺�
			//}
			
		}
		//==start=== add by kangy 20171024 ΢��֧����������ӿ����ͱ�ע���׺�
		if("WX".equals(result.getValue("TYPE"))||"ZFB".equals(result.getValue("TYPE"))){
			String cardSql="select chn_desc from sys_dictionary where group_id='SYS_CARDTYPE' and id='"+result.getValue("CARD_TYPE")+"'";
			TParm param=new TParm(TJDODBTool.getInstance().select(cardSql));
			if(result.getValue("DESCRIPTION").length()>0){
				parm.setData("CARD_TYPE","TEXT",param.getValue("CHN_DESC",0)+" ��ע:"+result.getValue("DESCRIPTION"));
			}else
			parm.setData("CARD_TYPE","TEXT",param.getValue("CHN_DESC",0));
			parm.setData("ACOUNT_NO", "TEXT", "���׺�:"+result.getValue("PRINT_NO"));
		}
		//==end=== add by kangy 20171024 ΢��֧����������ӿ����ͱ�ע���׺�
		
		if (table!=null && table.getRowCount() > 0) {
//			tc.messageBox("�б������");
			if(parmSum.getData("UnFeeFLG")!=null && parmSum.getData("UnFeeFLG").equals("Y")){
				String payType = result.getValue("GATHER_TYPE") + "��" + df.format(table.getValueAt(row, 3))+"Ԫ";// ���õ���֧����ʽ��֧�����ϲ�
				parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
			}else{
				String payType = result.getValue("GATHER_TYPE") + "��" + df.format(table.getValueAt(row, 3))+"Ԫ";// ���õ���֧����ʽ��֧�����ϲ�
				parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
			}
		}else{
//			tc.messageBox("�б������");
			if(parmSum.getValue("UnFeeFLG").equals("Y")){
				String payType = result.getValue("GATHER_TYPE") + "��" + df.format(tc.getValueDouble("UN_FEE"))+"Ԫ";
				parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
			}else{
				if(row!=-1){
					String payType = result.getValue("GATHER_TYPE") + "��" + df.format(tc.getValueDouble("TOP_UPFEE"))+"Ԫ";// ���õ���֧����ʽ��֧�����ϲ�
					parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
				}else{//����ҺŴ��������������趨row==-1
					if(tc.getValue("PROCEDURE_PRICE1")!=null){//ҽ�ƿ�����/��ʧ���շ�
						String payType = result.getValue("GATHER_TYPE") + "��" + df.format(tc.getValueDouble("TOP_UP_PRICE"))+"Ԫ";// ���õ���֧����ʽ��֧�����ϲ�
						parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
					}else{
						String payType = result.getValue("GATHER_TYPE") + "��" + df.format(tc.getValueDouble("GATHER_PRICE"))+"Ԫ";// ���õ���֧����ʽ��֧�����ϲ�
						parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
					}
				}
			}
		}
		String date = StringTool.getTimestamp(new Date()).toString().substring(
				0, 19).replace('-', '/');
		parm.setData("DATE", "TEXT", date);// ����
		parm.setData("OP_NAME", "TEXT", Operator.getID()); // �տ���
		if(flg == false){//�Ƿ�Ϊ�˷Ѳ����жϱ�ʶ
			returnFee = "";
			returnO = "";
		}else{
			returnFee = "��";
			returnO = "o";
		}
		parm.setData("RETURN", "TEXT", returnFee); // ��
		parm.setData("o", "TEXT", returnO);// ��
		parm.setData("COPY", "TEXT", copy); // ��ӡע��
//		tc.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTReceiptV45.jhw",
//				parm, true);
		parm.setData("HOSP_NAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalCHNFullName() : "����ҽԺ");
        parm.setData("HOSP_ENAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalENGFullName() : "ALL HOSPITALS");
		String prtSwitch=IReportTool.getInstance().getPrintSwitch("EKTReceiptV45.prtSwitch");
//		System.out.println(SystemTool.getInstance().getDate());
		if(IReportTool.ON.equals(prtSwitch)){			
			tc.openPrintWindow(IReportTool.getInstance().getReportPath(
			"EKTReceiptV45.jhw"), IReportTool.getInstance().getReportParm("EKTReceiptV45.class",parm));
//			System.out.println(SystemTool.getInstance().getDate());
		}
		
		// add by sunqy 201406010 ----end----
	}
}

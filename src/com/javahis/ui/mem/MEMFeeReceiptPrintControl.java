package com.javahis.ui.mem;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jdo.bil.PaymentTool;
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
 * Title: �°��Ա����ֵ��ӡ�汾
 * </p>
 * 
 * <p>
 * Description: �°��Ա����ֵ��ӡ�汾
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
 * @author sunqy 20140612
 * @version 1.0
 */

public class MEMFeeReceiptPrintControl {
	
	private static MEMFeeReceiptPrintControl print;
	private String returnFee;//�˷ѵ�"��"
	private String returnO;//�˷ѵ�"o"
	
	public MEMFeeReceiptPrintControl(){
	}
	
	public static MEMFeeReceiptPrintControl getInstance(){
		if(print == null){
			print = new MEMFeeReceiptPrintControl();
		}
		return print;
	}
	
	public void onPrint(TTable table,TParm parmSum,String copy,int row,Pat pat,boolean flg, TControl tc, PaymentTool paymentTool,String status){
		DecimalFormat df=new DecimalFormat("#0.00");
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("C0", "�ֽ�");
		map.put("C1", "ˢ��");
		map.put("T0", "֧Ʊ");
		map.put("C4", "Ӧ�տ�");
		map.put("LPK", "��Ʒ��");
		map.put("XJZKQ", "�ֽ��ۿ�ȯ");
		map.put("INS", "ҽ����");
		map.put("WX", "΢��");
		map.put("ZFB", "֧����");
		map.put("EKT", "ҽ�ƿ�");
		
		String gatherTypeSql = 
			" SELECT GATHER_TYPE, PAYTYPE FROM BIL_GATHERTYPE_PAYTYPE";
		TParm gatherParm = new TParm(TJDODBTool.getInstance().select(gatherTypeSql));
		Map<String, String> gatherMap = new HashMap<String, String>();
		for (int i = 0; i < gatherParm.getCount(); i++) {
			gatherMap.put(gatherParm.getValue("PAYTYPE", i), map.get(gatherParm.getValue("GATHER_TYPE", i)));
		}
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT", parmSum.getData("TITLE"));
		parm.setData("TYPE", "TEXT", parmSum.getValue("TYPE")); //���
		parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); // ������
		String sql = "";
		if("".equals(copy) || copy==null){
			sql += "SELECT MEM_DESC CTZ_CODE,MEMO1,MEMO2,MEMO3,MEMO4,MEMO5,MEMO6,MEMO7,MEMO8,MEMO9,MEMO10,CARD_TYPE " +
					"FROM MEM_TRADE A WHERE MR_NO = '"+ parmSum.getValue("MR_NO") + "' AND STATUS = '"+status+"' ORDER BY OPT_DATE DESC";
		}else{
			sql += "SELECT A.MEM_DESC CTZ_CODE,A.PAY_TYPE01,A.PAY_TYPE02,A.PAY_TYPE03,A.PAY_TYPE04,A.PAY_TYPE05,A.PAY_TYPE06,A.PAY_TYPE07," +
					"A.PAY_TYPE08,A.PAY_TYPE09,A.PAY_TYPE10," +
					"A.MEMO1,A.MEMO2,A.MEMO3,A.MEMO4,A.MEMO5,A.MEMO6,A.MEMO7,A.MEMO8,A.MEMO9,A.MEMO10,CARD_TYPE " +
					"FROM MEM_TRADE A " +
					"WHERE MR_NO = '"+ parmSum.getValue("MR_NO") + 
					"' AND TRADE_NO = '" + parmSum.getValue("TRADE_NO") +
					"' AND STATUS = '"+status+"'";//�������˿��ǵ�״̬�ǲ�ͬ��
		}
		System.out.println("sq;:::"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		result = result.getRow(0);
		parm.setData("RecNO", "TEXT", SystemTool.getInstance().getNo("ALL", "EKT", "MEM_NO", "MEM_NO")); //Ʊ�ݺ�
		parm.setData("DEPT_CODE", "TEXT", "");// �Ʊ�
		parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); // ����
//		double money = parmSum.getDouble("BUSINESS_AMT")==0.00 ? parmSum.getDouble("AMT") : Double.parseDouble(parmSum.getValue("BUSINESS_AMT"));
		parm.setData("MONEY", "TEXT", (df.format(StringTool.round(parmSum.getDouble("BUSINESS_AMT"), 2))+"Ԫ")); // ���
		parm.setData("CAPITAL", "TEXT", StringUtil.getInstance().numberToWord(parmSum.getDouble("BUSINESS_AMT"))); // ��д���
		
		String cardtype="";
		//��¼�����ͺͿ��� add by huangjw 20141230
		cardtype=result.getValue("MEMO2");
		//System.out.println("sq;:::"+cardtype);
//		String[] str;
//		String[] str1;
		//String str1="";
		int count=0;
		
		if(!"".equals(cardtype)&&!"#".equals(cardtype)){
			String[] str=cardtype.split("#");
			String[] str1=str[0].split(";");
//			String[] str2=str[1].split(";");
			String [] str2=null;
			if(str.length == 2){
				str2=str[1].split(";");
			}
			String bankNo = "";
			for(int m=0;m<str1.length;m++){
				String cardsql= "select CHN_DESC from sys_dictionary where id='"+str1[m]+"' and group_id='SYS_CARDTYPE'";
				TParm cardParm=new TParm(TJDODBTool.getInstance().select(cardsql));
//				bankNo=bankNo+cardParm.getValue("CHN_DESC",0)+" "+str2[m]+" "+";";
				
				bankNo=bankNo+cardParm.getValue("CHN_DESC",0)+" ";				
				if(str2 != null){
					if(m < str2.length ){
						bankNo=bankNo+str2[m]+" ";
					}
				}
			}
			parm.setData("ACOUNT_NO", "TEXT",bankNo.substring(0,bankNo.length()-1));
		}
//		for (int i = 1; i < 11; i++) {
//			if(!"".equals(result.getValue("MEMO"+i))){
//				if(!"".equals(cardtype)||cardtype!=null){
//					count++;
//					str=cardtype.split(",");
//					if(count<=str.length){
//						String cardsql="select CHN_DESC from sys_dictionary where id='"+str[count-1]+"' and group_id='SYS_CARDTYPE' ";
//						TParm Cardparm=new TParm(TJDODBTool.getInstance().select(cardsql));
//						str1=str1+Cardparm.getValue("CHN_DESC",0);
//					}
//				}
//				bankNo += str1+" "+result.getValue("MEMO"+i)+";";
//				str1="";
//			}
//		}
//		if(bankNo.length()>0){
//			bankNo = bankNo.substring(0, bankNo.length()-1);
//		}
//		parm.setData("ACOUNT_NO", "TEXT", bankNo);// �˺�
		TTable table1 = paymentTool.table;//֧����ʽ���
		int tableRow = table1.getRowCount();
		int tableColumn0= table1.getColumnIndex("PAY_TYPE");//֧����ʽ��
		int tableColumn1 = table1.getColumnIndex("AMT");//֧�������
		String payType = "";
		for(int i=0;i<tableRow;i++){
			if(table1.getValueAt(i, tableColumn0)==null||table1.getValueAt(i, tableColumn0)==""){
				break;
			}
			if((table1.getValueAt(i, tableColumn0)!=null||table1.getValueAt(i, tableColumn0)!="") 
					&& (table1.getValueAt(i, tableColumn1)==null || table1.getValueAt(i, tableColumn1)=="")){
				break;
			}
			if(copy.length()>0 && "(copy)".equals(copy)){
			}else{
				payType += ";"+ map.get(table1.getValueAt(i, tableColumn0)) + ":" 
								+ table1.getValueAt(i, tableColumn1) +"Ԫ";// ���õ���֧����ʽ��֧�����ϲ�
			}
		}
		if(!"".equals(payType)){
			payType = payType.substring(1, payType.length());
		}
		if(copy.length()>0 && "(copy)".equals(copy)){
			String key, value, keyStr;
			for (int j = 0; j < gatherParm.getCount(); j++) {
				key = gatherParm.getValue("PAYTYPE", j);
				value = result.getValue(key);
				keyStr = gatherMap.get(key);
				payType += this.onReturnPayType(keyStr, value) ;
			}
			payType = payType.substring(1, payType.length());
		}
		String arr[] = payType.split(";");//���֧����ʽ��3�����������ʾ
		if(arr.length > 2){
			parm.setData("PAY_TYPE2", "TEXT", arr[0]);
			parm.setData("PAY_TYPE3", "TEXT", arr[1]+";"+arr[2]);
		}else{
			parm.setData("PAY_TYPE", "TEXT", payType);// ֧����ʽ
		}
		parm.setData("CTZ_CODE", "TEXT", result.getValue("CTZ_CODE"));// ��Ʒ
		parm.setData("REASON", "TEXT", "");// �ۿ�ԭ��
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
		parm.setData("HOSP_NAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalCHNFullName() : "����ҽԺ");
        parm.setData("HOSP_ENAME", "TEXT", Operator.getRegion() != null && Operator.getRegion().length() > 0 ? 
        		Operator.getHospitalENGFullName() : "ALL HOSPITALS");
		tc.openPrintDialog(IReportTool.getInstance().getReportPath("MEMFeeReceiptV45.jhw"),
				IReportTool.getInstance().getReportParm("MEMFeeReceiptV45.class", parm));//�ϲ�����
	}
	private String onReturnPayType(String key, String value){
		DecimalFormat format = new DecimalFormat("########0.00");
		String str = "";
		if(value.length() > 0 && Double.valueOf(value) > 0){
			str = ";" + key + ":" + format.format(Double.valueOf(value)) + "Ԫ";
		}
		return str;
	}
}

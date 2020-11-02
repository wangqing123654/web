package com.javahis.ui.ekt;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.DocFlavor.STRING;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p>Title: ҽ�ƿ��ۿ���ϸ��</p>
 *
 * <p>Description: ҽ�ƿ��ۿ���ϸ�� </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author zhangp 20111226 
 * @version 1.0
 */
public class EKTSellCardDetailControl extends TControl {
	
	private TParm result;//��ѯ���
	TTable table;
	
	public EKTSellCardDetailControl(){
		
	}
	
	/**
     * ��ʼ������
     */
    public void onInit() {
    	setValue("REGION_CODE", Operator.getRegion());
    	setValue("OPT_USER", Operator.getID());
    	Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    	setValue("DEPT", Operator.getDept());
    	table = (TTable)this.getComponent("TABLE");
    }
    
    /**
     * ��ѯ����
     */
	public void onQuery(){
		String regionCode = getValue("REGION_CODE").toString();
		String accntType = getValue("ACCNT_TYPE").toString(); 
		String optUser = getValueString("OPT_USER");
		String dept = getValueString("DEPT");
		String startDate = "";
		String endDate = "";
		if (!"".equals(this.getValueString("START_DATE")) &&
	            !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10) + startDate.substring(11, 13) +
			startDate.substring(14, 16) + startDate.substring(17, 19);
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10) + endDate.substring(11, 13) +
			endDate.substring(14, 16) + endDate.substring(17, 19);
		}
		StringBuilder sql = new StringBuilder();
		String sql1 = 
			" SELECT   D.EKT_CARD_NO AS CARD_NO, A.STORE_DATE, B.USER_NAME," +
			" A.ACCNT_TYPE REAL_ACCNT_TYPE, A.GATHER_TYPE REAL_GATHER_TYPE," +
			" A.MR_NO, C.PAT_NAME, A.AMT, A.PROCEDURE_AMT," +
			" CASE" +
			" WHEN A.ACCNT_TYPE = 1" +
			" THEN '����'" +
			" WHEN A.ACCNT_TYPE = 2" +
			" THEN '����'" +
			" WHEN A.ACCNT_TYPE = 3" +
			" THEN '����'" +
			" WHEN A.ACCNT_TYPE = 4" +
			" THEN '��ֵ'" +
			" WHEN A.ACCNT_TYPE = 5" +
			" THEN '�ۿ�'" +
			" WHEN A.ACCNT_TYPE = 6" +
			" THEN '�˷�'" +
			" END ACCNT_TYPE," +
			" F.CHN_DESC GATHER_TYPE, A.CREAT_USER" +
			" FROM EKT_BIL_PAY A," +
			" SYS_OPERATOR B," +
			" SYS_PATINFO C," +
			" EKT_ISSUELOG D," +
			" SYS_OPERATOR_DEPT E," +
			" SYS_DICTIONARY F" +
			" WHERE A.CREAT_USER = B.USER_ID(+)" +
			" AND A.MR_NO = C.MR_NO(+)" +
			" AND A.CARD_NO = D.CARD_NO(+)" +
			" AND A.ACCNT_TYPE <> '5'" +
			" AND B.USER_ID = E.USER_ID(+)" +
			" AND E.MAIN_FLG = 'Y'" +
			" AND F.GROUP_ID = 'GATHER_TYPE'" +
			" AND A.GATHER_TYPE = F.ID" ;
		sql.append(sql1);
		if(!regionCode.equals("")&&regionCode!=null){
			sql.append(" AND B.REGION_CODE ='"+regionCode+"' ");
		}
		if(!startDate.equals("")&&startDate!=null&&!endDate.equals("")&&endDate!=null){
			sql.append(" AND A.STORE_DATE BETWEEN TO_DATE('" + startDate +
					"','YYYYMMDDHH24MISS') AND TO_DATE('" + endDate +
                "','YYYYMMDDHH24MISS') ");
		}
		if(!accntType.equals("")&&accntType!=null){
			sql.append(" AND A.ACCNT_TYPE = '"+accntType+"' ");
		}
		if(!optUser.equals("")&&optUser!=null){
			sql.append(" AND A.OPT_USER = '"+optUser+"' ");
		}
		if(!dept.equals("")&&dept!=null){
			sql.append(" AND E.DEPT_CODE = '"+dept+"' ");
		}
		sql.append(" ORDER BY A.CREAT_USER , A.STORE_DATE");
		result =new TParm(TJDODBTool.getInstance().select(sql.toString()));
		if(result.getCount()<=0)
            this.messageBox("û��Ҫ��ѯ������");
        this.callFunction("UI|TABLE|setParmValue", result);
	}
	
//	EKTSellCardDetail.jhw
	/**
	 * ��ӡ
	 */
	public void onPrint(){
		onQuery();
		TParm data = new TParm();
		TParm tableParm = print(result);
//		System.out.println(tableParm);
		tableParm.setCount(tableParm.getCount("USER_NAME"));
		tableParm.addData("SYSTEM", "COLUMNS", "STORE_DATE");
		tableParm.addData("SYSTEM", "COLUMNS", "USER_NAME");
		tableParm.addData("SYSTEM", "COLUMNS", "ACCNT_TYPE");
		tableParm.addData("SYSTEM", "COLUMNS", "GATHER_TYPE");
		tableParm.addData("SYSTEM", "COLUMNS", "MR_NO");
		tableParm.addData("SYSTEM", "COLUMNS", "CARD_NO");
		tableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		tableParm.addData("SYSTEM", "COLUMNS", "AMT");
		tableParm.addData("SYSTEM", "COLUMNS", "PROCEDURE_AMT");
		String date = SystemTool.getInstance().getDate().toString();
//		System.out.println(date);
		data.setData("TITLE1", "TEXT", Operator.getHospitalCHNFullName());
		data.setData("PRINTDATE", "TEXT", "��ӡ����: "+date.substring(0, 4)+
    			"/"+date.substring(5, 7)+"/"+date.substring(8, 10));
		
		//�ѱ��������ӽ�Ҫ��ӡ��parm
		data.setData("TABLE", tableParm.getData());
		//========modify by lim 2012/02/24 begin
		this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKTSellCardDetail.jhw",data);
		//========modify by lim 2012/02/24 end
	}
	/**
	 * ��ȡ��ӡ����
	 * @param parm
	 * @return
	 */
	public TParm print(TParm parm){
		String creatUser = parm.getValue("CREAT_USER", 0);
		int buyCardCount = 0;//��������
		int totbuyCardCount = 0;//��������
		double sumAmt = 0;//������Ǯ��
		double sumProcedureAmt = 0;//������������
		double totAmt = 0;//��Ǯ��
		double totProcedureAmt = 0;//��������
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//�������ڸ�ʽ
		DecimalFormat   df1 = new DecimalFormat("#0.00"); //�������ݸ�ʽ
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			if(parm.getValue("CREAT_USER", i).equals(creatUser)){
				if(parm.getValue("REAL_ACCNT_TYPE", i).equals("1")){
					buyCardCount++;
					totbuyCardCount++;
				}
				if(parm.getValue("REAL_ACCNT_TYPE", i).equals("6")){
					sumAmt -= parm.getDouble("AMT", i);
					sumProcedureAmt -= parm.getDouble("PROCEDURE_AMT", i);
					totAmt -= parm.getDouble("AMT", i);
					totProcedureAmt -= parm.getDouble("PROCEDURE_AMT", i);
				}else{
					sumAmt += parm.getDouble("AMT", i);
					sumProcedureAmt += parm.getDouble("PROCEDURE_AMT", i);
					totAmt += parm.getDouble("AMT", i);
					totProcedureAmt += parm.getDouble("PROCEDURE_AMT", i);
				}
				
				result.addData("STORE_DATE", df.format(parm.getData("STORE_DATE", i)));
				result.addData("USER_NAME", parm.getData("USER_NAME", i));
				result.addData("ACCNT_TYPE", parm.getData("ACCNT_TYPE", i));
				result.addData("GATHER_TYPE", parm.getData("GATHER_TYPE", i));
				result.addData("MR_NO", parm.getData("MR_NO", i));
				result.addData("CARD_NO", parm.getData("CARD_NO", i));
				result.addData("PAT_NAME", parm.getData("PAT_NAME", i));
				result.addData("AMT", df1.format(parm.getData("AMT", i)));
				result.addData("PROCEDURE_AMT", df1.format(parm.getData("PROCEDURE_AMT", i)));
			}else{
				result.addData("STORE_DATE", "");
				result.addData("USER_NAME", "<��Ա����");
				result.addData("ACCNT_TYPE", "�ϼ�>");
				result.addData("GATHER_TYPE", buyCardCount+" �ſ�");
				result.addData("MR_NO", "");
				result.addData("CARD_NO", "");
				result.addData("PAT_NAME", "");
				result.addData("AMT", df1.format(StringTool.round(sumAmt,2)));
				result.addData("PROCEDURE_AMT", df1.format(StringTool.round(sumProcedureAmt,2)));
				
				result.addData("STORE_DATE", "");
				result.addData("USER_NAME", "");
				result.addData("ACCNT_TYPE", "");
				result.addData("GATHER_TYPE", "");
				result.addData("MR_NO", "");
				result.addData("CARD_NO", "");
				result.addData("PAT_NAME", "");
				result.addData("AMT", "");
				result.addData("PROCEDURE_AMT", "");
				
				sumAmt = 0;
				sumProcedureAmt = 0;
				buyCardCount = 0;
				
				sumAmt += parm.getDouble("AMT", i);
				sumProcedureAmt += parm.getDouble("PROCEDURE_AMT", i);
				totAmt += parm.getDouble("AMT", i);
				totProcedureAmt += parm.getDouble("PROCEDURE_AMT", i);
				
				result.addData("STORE_DATE", df.format(parm.getData("STORE_DATE", i)));
				result.addData("USER_NAME", parm.getData("USER_NAME", i));
				result.addData("ACCNT_TYPE", parm.getData("ACCNT_TYPE", i));
				result.addData("GATHER_TYPE", parm.getData("GATHER_TYPE", i));
				result.addData("MR_NO", parm.getData("MR_NO", i));
				result.addData("CARD_NO", parm.getData("CARD_NO", i));
				result.addData("PAT_NAME", parm.getData("PAT_NAME", i));
				result.addData("AMT", df1.format(parm.getData("AMT", i)));
				result.addData("PROCEDURE_AMT", df1.format(parm.getData("PROCEDURE_AMT", i)));
			}
			creatUser = parm.getValue("CREAT_USER", i);
		}
		result.addData("STORE_DATE", "");
		result.addData("USER_NAME", "<��Ա����");
		result.addData("ACCNT_TYPE", "�ϼ�>");
		result.addData("GATHER_TYPE", buyCardCount+" �ſ�");
		result.addData("MR_NO", "");
		result.addData("CARD_NO", "");
		result.addData("PAT_NAME", "");
		result.addData("AMT", df1.format(StringTool.round(sumAmt,2)));
		result.addData("PROCEDURE_AMT", df1.format(StringTool.round(sumProcedureAmt,2)));
		
		result.addData("STORE_DATE", "");
		result.addData("USER_NAME", "<�����ܼ�");
		result.addData("ACCNT_TYPE", ">");
		result.addData("GATHER_TYPE", totbuyCardCount+" �ſ�");
		result.addData("MR_NO", "");
		result.addData("CARD_NO", "");
		result.addData("PAT_NAME", "");
		result.addData("AMT", df1.format(StringTool.round(totAmt,2)));
		result.addData("PROCEDURE_AMT", df1.format(StringTool.round(totProcedureAmt,2)));
//		DecimalFormat df = new DecimalFormat("########0.00");
//		String sql =
//			"SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'GATHER_TYPE'";
//		TParm gather = new TParm(TJDODBTool.getInstance().select(sql));
//		Map<Object, Object> map = parm.getData();
//		map = (Map<Object, Object>) map.get("Data");
//		
//		List user_names = (List) map.get("USER_NAME");
//		List amts = (List) map.get("AMT");
//		List procedure_amts = (List) map.get("PROCEDURE_AMT");
//		List mr_nos = (List) map.get("MR_NO");
//		List store_dates = (List) map.get("STORE_DATE");
//		List gather_types = (List) map.get("GATHER_TYPE");
//		List pay_names = (List) map.get("PAT_NAME");
//		List accnt_types = (List) map.get("ACCNT_TYPE");
//		List cardNos = (List) map.get("CARD_NO");
//		String gather_type_str = "";
//		String store_date_str = "";
//		String accnt_type_str = "";
//		int buyCardCount = 0;//��������
//		String user_name = "";
//		double amtFee = 0.00;//����
//		double prAmtFee = 0.00;//������
//		double fee = 0.00;//���
//		int buyCardCountTotal = 0;//�����ܴ���
//		double feeTotal = 0.00;//�ܽ��
//		double amtFeeTotal = 0.00;//������
//		double prAmtFeeTotal = 0.00;//��������
//		int k = 0;
//		for (int i = 0; i < user_names.size(); i++) {
//			for (int j = 0; j < gather.getCount(); j++) {
//				if(gather_types.get(i).equals(gather.getData("ID", j))){
//					gather_type_str= gather.getData("CHN_DESC", j).toString();
//					gather_types.set(i, gather_type_str);
//				}
//			}
//			if(store_dates.get(i)!=null&&!store_dates.get(i).equals("")){
//				store_date_str = store_dates.get(i).toString().substring(0, 4)+
//				"/"+store_dates.get(i).toString().substring(5, 7)+"/"+store_dates.get(i).toString().substring(8, 10);
//				store_dates.set(i, store_date_str);
//			}
////			��ϸ�ʱ�(1:����,2:����,3:����,4:��ֵ,5:�ۿ�,6:�˷�)
//			if(accnt_types.get(i).equals("1")){
//				accnt_type_str = "����";
//				accnt_types.set(i, accnt_type_str);
//			}
//			if(accnt_types.get(i).equals("2")){
//				accnt_type_str = "����";
//				accnt_types.set(i, accnt_type_str);
//			}
//			if(accnt_types.get(i).equals("3")){
//				accnt_type_str = "����";
//				accnt_types.set(i, accnt_type_str);
//			}
//			if(accnt_types.get(i).equals("4")){
//				accnt_type_str = "��ֵ";
//				accnt_types.set(i, accnt_type_str);
//			}
////			if(accnt_types.get(i).equals("5")){
////				accnt_type_str = "�ۿ�";
////				accnt_types.set(i, accnt_type_str);
////			}
//			if(accnt_types.get(i).equals("6")){
//				accnt_type_str = "�˷�";
//				accnt_types.set(i, accnt_type_str);
//			}
//			if(!user_names.get(i).toString().equals(user_name)&&i!=0){
//				for (int j = k; j < i; j++) {
//					//����
//					if(accnt_types.get(j).equals("����")){
////						System.out.println(i);
//						buyCardCount++;//��������
//						amtFee = amtFee + Double.parseDouble(amts.get(j).toString());//����
//						prAmtFee = prAmtFee + Double.parseDouble(procedure_amts.get(j).toString());//������
//					}
//					//����������ֵ�ۿ�
//					if(accnt_types.get(j).equals("����")||accnt_types.get(j).equals("����")
//							||accnt_types.get(j).equals("��ֵ")){
//						amtFee = amtFee + Double.parseDouble(amts.get(j).toString());//����
//						prAmtFee = prAmtFee + Double.parseDouble(procedure_amts.get(j).toString());//������
//					}
//					//�˷�
//					if(accnt_types.get(j).equals("�˷�")){
//						amtFee = amtFee - Double.parseDouble(amts.get(j).toString());//����
//						prAmtFee = prAmtFee + Double.parseDouble(procedure_amts.get(j).toString());//������
//					}
//					k=i;
//				}
//				fee = amtFee;
//				feeTotal = feeTotal+fee;
//				amtFeeTotal = amtFeeTotal + amtFee;
//				prAmtFeeTotal = prAmtFeeTotal + prAmtFee;
//				buyCardCountTotal = buyCardCountTotal +buyCardCount;
//				user_names.add(i, "");
//				user_names.add(i, "<��Ա����");
//				amts.add(i, "");
//				amts.add(i, StringTool.round(fee,2));
//				procedure_amts.add(i, "");
//				procedure_amts.add(i, StringTool.round(prAmtFee,2));
//				mr_nos.add(i, "");
//				mr_nos.add(i, "");
//				store_dates.add(i, "");
//				store_dates.add(i, "");
//				gather_types.add(i, "");
//				gather_types.add(i, buyCardCount+" �ſ�");
//				pay_names.add(i, "");
//				pay_names.add(i, "");
//				accnt_types.add(i, "");
//				accnt_types.add(i, "�ϼ�>");
//				cardNos.add(i, "");
//				cardNos.add(i, "");
//				i=i+2;
//				amtFee=0;
//				prAmtFee=0;
//				feeTotal=0;
//				amtFeeTotal=0;
//				prAmtFeeTotal=0;
//				fee=0;
//				prAmtFee=0;
//				buyCardCount=0;
//			}
//			user_name = user_names.get(i).toString();
//		}
//		for (int j = k; j < user_names.size(); j++) {
//			//����
//			if(accnt_types.get(j).equals("����")){
//				buyCardCount++;//��������
//				amtFee = amtFee + Double.parseDouble(amts.get(j).toString());//����
//				prAmtFee = prAmtFee + Double.parseDouble(procedure_amts.get(j).toString());//������
//			}
//			//����������ֵ�ۿ�
//			if(accnt_types.get(j).equals("����")||accnt_types.get(j).equals("����")
//					||accnt_types.get(j).equals("��ֵ")){
//				amtFee = amtFee + Double.parseDouble(amts.get(j).toString());//����
//				prAmtFee = prAmtFee + Double.parseDouble(procedure_amts.get(j).toString());//������
//			}
//			//�˷�
//			if(accnt_types.get(j).equals("�˷�")){
//				amtFee = amtFee - Double.parseDouble(amts.get(j).toString());//����
//				prAmtFee = prAmtFee + Double.parseDouble(procedure_amts.get(j).toString());//������
//			}
//		}
//		fee = amtFee;
//		feeTotal = feeTotal+fee;
//		amtFeeTotal = amtFeeTotal + amtFee;
//		prAmtFeeTotal = prAmtFeeTotal + prAmtFee;
//		buyCardCountTotal = buyCardCountTotal +buyCardCount;
//		user_names.add("<��Ա����");
//		user_names.add("<�����ܼ�");
//		amts.add(StringTool.round(fee,2));
//		amts.add(StringTool.round(feeTotal,2));
//		procedure_amts.add(StringTool.round(prAmtFee,2));
//		procedure_amts.add(StringTool.round(prAmtFeeTotal,2));
//		mr_nos.add("");
//		mr_nos.add("");
//		store_dates.add("");
//		store_dates.add("");
//		gather_types.add(buyCardCount+" �ſ�");
//		gather_types.add(buyCardCountTotal+" �ſ�");
//		pay_names.add("");
//		pay_names.add("");
//		accnt_types.add("�ϼ�>");
//		accnt_types.add(">");
//		cardNos.add("");
//		cardNos.add("");
//		
//		map.put("AMT", amts);
//		map.put("PROCEDURE_AMT", procedure_amts);
//		map.put("MR_NO", mr_nos);
//		map.put("STORE_DATE", store_dates);
//		map.put("GATHER_TYPE", gather_types);
//		map.put("PAT_NAME", pay_names);
//		map.put("USER_NAME", user_names);
//		map.put("ACCNT_TYPE", accnt_types);
//		Map<Object, Object> resultMap = new HashMap<Object, Object>();;
//		resultMap.put("Data", map);
//		TParm result = new TParm(resultMap);
		return result;
	}
	/**
	 * ���
	 */
	public void onClear(){
		clearValue("ACCNT_TYPE;OPT_USER;START_DATE;END_DATE");
		Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", today);
    	table.removeRowAll();
    
	}
}
